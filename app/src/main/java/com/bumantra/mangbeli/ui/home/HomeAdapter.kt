package com.bumantra.mangbeli.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumantra.mangbeli.R
import com.bumantra.mangbeli.databinding.ItemListHomeBinding
import com.bumantra.mangbeli.model.Vendor
import com.bumantra.mangbeli.ui.detail.DetailActivity
import com.bumptech.glide.Glide

class HomeAdapter : ListAdapter<Vendor, HomeAdapter.VendorViewHolder>(DIFF_CALLBACK) {
    class VendorViewHolder(var binding: ItemListHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val vendorName = binding.tvItemNameVendor
        private val name = binding.tvItemName
        private val distance = binding.tvItemDistance
        fun bind(vendor: Vendor) {
            val location = binding.root.context.getString(
                R.string.vendor_location_format,
                vendor.latitude.toString(),
                vendor.longitude.toString()
            )

            vendorName.text = vendor.vendorName
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
        val vendor = getItem(position) as Vendor
        holder.binding.imgVendor.loadImage(vendor.photoUrl)
        holder.bind(vendor)

        holder.itemView.setOnClickListener {
            val contextIntent = holder.itemView.context
            val intent = Intent(contextIntent, DetailActivity::class.java)
            intent.putExtra("id", vendor.id)
            // delete the intent below when data is already apply
            intent.putExtra("photoUrl", vendor.photoUrl)
            intent.putExtra("vendorName", vendor.vendorName)
            intent.putExtra("name", vendor.name)
            intent.putStringArrayListExtra("products", ArrayList(vendor.products))
            contextIntent.startActivity(intent)
        }
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