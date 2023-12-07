package com.capstone.mangbeli.ui.pedagang.homepedagang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.mangbeli.databinding.FragmentHomePedagangBinding


class HomePedagangFragment : Fragment() {

    private var _binding: FragmentHomePedagangBinding? = null


    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomePedagangBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }



}