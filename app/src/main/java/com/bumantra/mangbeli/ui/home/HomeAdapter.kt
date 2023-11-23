package com.bumantra.mangbeli.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumantra.mangbeli.databinding.ItemListHomeBinding
import com.bumantra.mangbeli.model.Vendor
import com.bumptech.glide.Glide

class HomeAdapter : ListAdapter<Vendor, HomeAdapter.VendorViewHolder>(DIFF_CALLBACK) {
    class VendorViewHolder(var binding: ItemListHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val vendorName = binding.tvItemNameVendor
        private val name = binding.tvItemName
        private val distance = binding.tvItemDistance
        fun bind(vendor: Vendor) {
            vendorName.text = vendor.vendorName
            name.text = vendor.name
            distance.text = vendor.distance

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VendorViewHolder {
        val inflater =
            ItemListHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VendorViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: VendorViewHolder, position: Int) {
        val vendor = getItem(position) as Vendor
        holder.binding.imgVendor.loadImage(vendor.photoUrl)
        holder.bind(vendor)
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Vendor>() {
            override fun areItemsTheSame(oldItem: Vendor, newItem: Vendor): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Vendor, newItem: Vendor): Boolean {
                return oldItem == newItem
            }
        }

    }

    private fun ImageView.loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .into(this)
    }
}