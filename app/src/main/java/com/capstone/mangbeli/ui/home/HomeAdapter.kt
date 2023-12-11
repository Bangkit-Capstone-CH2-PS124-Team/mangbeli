package com.capstone.mangbeli.ui.home

import android.content.Intent
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
        private val distance = binding.tvItemDistance
        fun bind(vendor: ListVendorsItem) {
            val location = binding.root.context.getString(
                R.string.vendor_location_format,
                vendor.latitude.toString(),
                vendor.longitude.toString()
            )

            vendorName.text = vendor.nameVendor
            name.text = vendor.name
            distance.text = location

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
        if (image != null) {
            holder.binding.imgVendor.loadImage(image)
        }
        holder.bind(vendor)

        holder.itemView.setOnClickListener {
            val contextIntent = holder.itemView.context
            val intent = Intent(contextIntent, DetailActivity::class.java)
            intent.putExtra("id", vendor.userId)
            // delete the intent below when data is already apply
            intent.putExtra("photoUrl", vendor.imageUrl)
            intent.putExtra("vendorName", vendor.nameVendor)
            intent.putExtra("name", vendor.name)
            intent.putExtra("latitude", vendor.latitude)
            intent.putExtra("longitude", vendor.longitude)
            intent.putStringArrayListExtra("products",
                vendor.products?.let { it1 -> ArrayList(it1) })
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