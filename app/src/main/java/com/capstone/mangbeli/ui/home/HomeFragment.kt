package com.capstone.mangbeli.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.mangbeli.R
import com.capstone.mangbeli.data.remote.response.ListVendorsItem
import com.capstone.mangbeli.databinding.FragmentHomeBinding
import com.capstone.mangbeli.ui.MenuActivity
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.utils.Result.Error
import com.capstone.mangbeli.utils.Result.Loading
import com.capstone.mangbeli.utils.Result.Success
import com.capstone.mangbeli.utils.setVisibility

@Suppress("UNCHECKED_CAST")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var searchQuery: String = ""
    private var filterBy: String = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.token.isEmpty()) {
                startActivity(Intent(requireContext(), MenuActivity::class.java))
                requireActivity().finish()
            }
        }

        binding.rvHomeUser.apply {
            layoutManager = LinearLayoutManager(this.context)
        }

        binding.sortBy.setOnClickListener {
            showMenu(it, R.menu.popup_menu)
        }

        setUpSearchBar()
        initListVendor()

        return root
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnDismissListener {
        }
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.option_name -> {
                    filterBy = "name"
                    initListVendor()
                }
                R.id.option_min_price -> {
                    filterBy = "minPrice"
                    initListVendor()
                }
                R.id.option_max_price -> {
                    filterBy = "maxPrice"
                    initListVendor()
                }
            }
            return@setOnMenuItemClickListener true
        }
        // Show the popup menu.
        popup.show()
    }

    private fun initListVendor() {
        viewModel.getAllVendor(10, 1, search = searchQuery, filter = filterBy).observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "searchQuery: $searchQuery")
            when (result) {
                is Loading -> {
                    setVisibility(binding.homeProgressBar, true)
                    Log.d(TAG, "onCreateView: Loading..")
                }

                is Success -> {
                    setVisibility(binding.homeProgressBar, false)
                    ViewModelFactory.refreshInstance()
                    Log.d(TAG, "onCreateView: Sukses ${result.data.message}")
                    val vendors = result.data.listVendors
                    showRecyclerView(vendors as List<ListVendorsItem>)
                    Log.d(TAG, "Cek Ombak: $vendors")
                }

                is Error -> {
                    setVisibility(binding.homeProgressBar, false)
                    Log.d(TAG, "onCreateView: ${result.error}")
                }
            }
        }

    }

    private fun setUpSearchBar() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchBar.setText(searchView.text)
                        searchQuery = searchView.text.toString()
                        Log.d(TAG, "setUpSearchBar: $searchQuery")
                        searchView.hide()
                        searchView.clearFocus()
                        initListVendor()
                        true
                    } else {
                        false
                    }
                }
        }
    }

    private fun showRecyclerView(vendors: List<ListVendorsItem>) {
        val adapter = HomeAdapter()
        adapter.submitList(emptyList())
        adapter.submitList(vendors)
        binding.rvHomeUser.adapter = adapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}