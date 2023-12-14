package com.capstone.mangbeli.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.FragmentHomeBinding
import com.capstone.mangbeli.ui.MenuActivity
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.utils.LoadingStateAdapter
import com.capstone.mangbeli.utils.setVisibility
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var homeAdapter: HomeAdapter

    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        homeAdapter = HomeAdapter()

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.token.isEmpty()) {
                startActivity(Intent(requireContext(), MenuActivity::class.java))
                requireActivity().finish()
            }
        }

        binding?.rvHomeUser?.apply {
            layoutManager = LinearLayoutManager(this.context)
        }

        viewModel.searchquery.observe(viewLifecycleOwner) {
            initAllVendors()
        }

        viewModel.filterBy.observe(viewLifecycleOwner) {
            initAllVendors()
        }

        setUpSearchBar()
        initAllVendors()
        showRecyclerView()


        return binding?.root
    }

    private fun showRecyclerView() {
        binding?.rvHomeUser?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    homeAdapter.retry()
                }
            )
        }
    }

    private fun initAllVendors() {
        lifecycleScope.launch {

            viewModel.getAllVendors(
                search = viewModel.searchquery.value,
                filter = viewModel.filterBy.value
            ).collectLatest {
                binding?.homeProgressBar?.let { it1 -> setVisibility(it1, false) }
                homeAdapter.submitData(lifecycle, it)
            }
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnDismissListener {
        }
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.option_name -> {
                    viewModel.setFilterBy("name")
                }

                R.id.option_min_price -> {
                    viewModel.setFilterBy("minPrice")
                }

                R.id.option_max_price -> {
                    viewModel.setFilterBy("maxPrice")
                }

                else -> Log.d(TAG, "showMenu: ")
            }
            true
        }
        // Show the popup menu.
        popup.show()
    }

    private fun setUpSearchBar() {
        with(binding) {
            this?.searchBar?.inflateMenu(R.menu.popup_menu)
            this?.searchBar?.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.option_name -> {
                        viewModel.setFilterBy("name")
                    }

                    R.id.option_min_price -> {
                        viewModel.setFilterBy("minPrice")
                    }

                    R.id.option_max_price -> {
                        viewModel.setFilterBy("maxPrice")
                    }

                    else -> Log.d(TAG, "showMenu: ")
                }
                true
            }
            this?.searchView?.setupWithSearchBar(searchBar)
            this?.searchView
                ?.editText
                ?.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchBar.setText(searchView.text)
                        viewModel.setSearchQuery(searchView.text.toString())
                        searchView.hide()
                        searchView.clearFocus()
                        initAllVendors()
                        true
                    } else {
                        false
                    }
                }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}