package com.capstone.mangbeli.ui.home

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.ItemListHomeBinding
import com.capstone.mangbeli.ui.detail.DetailActivity
import com.bumptech.glide.Glide
import com.capstone.mangbeli.data.remote.response.ListVendorsItem

class HomeAdapter : ListAdapter<ListVendorsItem, HomeAdapter.VendorViewHolder>(DIFF_CALLBACK) {
    class VendorViewHolder(var binding: ItemListHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val vendorName = binding.tvItemNameVendor
        private val name = binding.tvItemName
        private val products = binding.tvItemDistance
        fun bind(vendor: ListVendorsItem) {

            vendorName.text = vendor.nameVendor ?: "Pedagang"
            name.text = vendor.name
            products.text = vendor.products?.joinToString(", ")
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
        val vendor = getItem(position) as ListVendorsItem
        val image = vendor.imageUrl
        Log.d("TestImage", "onBindViewHolder: $image")
        if (image != null) {
            holder.binding.imgVendor.loadImage(image)
        } else {
            holder.binding.imgVendor.setImageResource(R.drawable.gobaklogo)
        }
        holder.bind(vendor)

        holder.itemView.setOnClickListener {
            val contextIntent = holder.itemView.context
            val intent = Intent(contextIntent, DetailActivity::class.java)
            intent.apply {
                putExtra("id", vendor.vendorId)
                putExtra("photoUrl", vendor.imageUrl)
                putExtra("name", vendor.name)
                putExtra("latitude", vendor.latitude)
                putExtra("longitude", vendor.longitude)
                putExtra("noHp", vendor.noHp)
            }
            contextIntent.startActivity(intent)
        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListVendorsItem>() {
            override fun areItemsTheSame(oldItem: ListVendorsItem, newItem: ListVendorsItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: ListVendorsItem, newItem: ListVendorsItem): Boolean {
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