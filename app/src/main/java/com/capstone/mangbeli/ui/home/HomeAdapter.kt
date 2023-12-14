package com.capstone.mangbeli.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.mangbeli.R
import com.capstone.mangbeli.data.local.entity.VendorEntity
import com.capstone.mangbeli.databinding.ItemListHomeBinding
import com.capstone.mangbeli.ui.detail.DetailActivity
import com.capstone.mangbeli.utils.setVisibility

class HomeAdapter : PagingDataAdapter<VendorEntity, HomeAdapter.VendorViewHolder>(DIFF_CALLBACK) {
    class VendorViewHolder(var binding: ItemListHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val vendorName = binding.tvItemNameVendor
        private val name = binding.tvItemName
        private val products = binding.tvListProduct
        @SuppressLint("SetTextI18n")
        fun bind(vendor: VendorEntity) {
            val minPrice = vendor.minPrice.toString()
            val maxPrice = vendor.maxPrice.toString()
            binding.tvMinPrice.text = "Rp. $minPrice"
            binding.tvMaxPrice.text = "Rp. $maxPrice"
            vendorName.text = vendor.nameVendor ?: "Pedagang"
            name.text = vendor.name
            if (vendor.products != null) {
                setVisibility(products, true)
                products.text = vendor.products
            }
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
        val vendor = getItem(position)
        val image = vendor?.imageUrl
        if (image != null) {
            holder.binding.imgVendor.loadImage(image)
        } else {
            holder.binding.imgVendor.setImageResource(R.drawable.logo_mangbeli)
        }
        if (vendor != null) {
            holder.bind(vendor)
        }

        holder.itemView.setOnClickListener {
            val contextIntent = holder.itemView.context
            val intent = Intent(contextIntent, DetailActivity::class.java)
            intent.apply {
                putExtra("id", vendor?.vendorId)
                putExtra("photoUrl", vendor?.imageUrl)
                putExtra("name", vendor?.name)
                putExtra("latitude", vendor?.latitude)
                putExtra("longitude", vendor?.longitude)
                putExtra("noHp", vendor?.noHp)
            }
            contextIntent.startActivity(intent)
        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VendorEntity>() {
            override fun areItemsTheSame(oldItem: VendorEntity, newItem: VendorEntity): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: VendorEntity, newItem: VendorEntity): Boolean {
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