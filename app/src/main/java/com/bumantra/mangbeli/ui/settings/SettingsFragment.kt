package com.bumantra.mangbeli.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumantra.mangbeli.databinding.FragmentSettingsBinding
import com.bumantra.mangbeli.ui.MenuActivity
import com.bumantra.mangbeli.ui.ViewModelFactory
import com.bumantra.mangbeli.ui.home.HomeViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), MenuActivity::class.java))
            requireActivity().finish()
        }

        return root
    }

}