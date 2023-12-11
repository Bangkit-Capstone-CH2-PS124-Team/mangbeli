package com.capstone.mangbeli.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.mangbeli.data.remote.response.ListVendorsItem
import com.capstone.mangbeli.databinding.FragmentHomeBinding
import com.capstone.mangbeli.ui.MenuActivity
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.utils.Result.*
import com.capstone.mangbeli.utils.setVisibility

@Suppress("UNCHECKED_CAST")
class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
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


        setUpSearchBar()
        viewModel.getAllVendor(10,1).observe(viewLifecycleOwner) {result ->
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

        return root
    }

    private fun setUpSearchBar() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    searchBar.setText(searchView.text)
                    searchView.hide()
                    searchView.clearFocus()
                    false
                }
        }
    }

    private fun showRecyclerView(vendors: List<ListVendorsItem>) {
        val adapter = HomeAdapter()
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