package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildAmenitiesAdapterBinding
import com.bitla.ts.domain.pojo.available_routes.BusAmenity
import com.bumptech.glide.Glide

class AmenitiesAdapter(
    private val context: Context,
    private var busAmenities: List<BusAmenity>
) :
    RecyclerView.Adapter<AmenitiesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildAmenitiesAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return busAmenities.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val busAmenity: BusAmenity = busAmenities[position]
        holder.tvAmenities.text = busAmenity.amenities_name
        if (busAmenity.image_url.isNotEmpty()) {
            Glide.with(context)
                .load(busAmenity.image_url)
                .fitCenter()
                .into(holder.imgAmenities)
        } else {
            holder.imgAmenities.setImageDrawable(context.resources.getDrawable(R.drawable.ic_block))
        }
    }

    class ViewHolder(binding: ChildAmenitiesAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgAmenities = binding.imgAmenities
        val tvAmenities = binding.tvAmenities
    }
}