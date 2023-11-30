package com.bumantra.mangbeli.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumantra.mangbeli.databinding.FragmentHomeBinding
import com.bumantra.mangbeli.model.VendorsData.vendors

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvHomeUser.apply {
            layoutManager = LinearLayoutManager(this.context)
        }

        showRecyclerView()
        setUpSearchBar()


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

    private fun showRecyclerView() {
        val adapter = HomeAdapter()
        adapter.submitList(vendors)
        binding.rvHomeUser.adapter = adapter


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}