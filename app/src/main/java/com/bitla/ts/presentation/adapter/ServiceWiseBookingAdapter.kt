package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildServicewiseBookingDashboardBinding
import com.bitla.ts.domain.pojo.dashboard_fetch.response.BookingDetail

class ServiceWiseBookingAdapter(
    private val context: Context,
    private val serviceWiseList: List<BookingDetail>?
) : RecyclerView.Adapter<ServiceWiseBookingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildServicewiseBookingDashboardBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = serviceWiseList?.get(position)
        holder.serviceName.text = item?.service
        holder.tvOccupancyValue.text = item?.occupancy
        holder.tvSeatsSoldValue.text = item?.seatsSold
        holder.tvRevenueValue.text = item?.revenue

        if (true) {
            holder.rectangleImage.setBackgroundResource(R.drawable.ic_rectangle_active)
        } else {
            holder.rectangleImage.setBackgroundResource(R.drawable.ic_rectangle_cancelled)
            holder.tvOccupancyValue.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
        }

    }

    override fun getItemCount(): Int {
        return serviceWiseList?.size ?: 0
    }

    class ViewHolder(binding: ChildServicewiseBookingDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var serviceName = binding.serviceName
        var tvOccupancyValue = binding.tvOccupancyValue
        var tvSeatsSoldValue = binding.tvSeatsSoldValue
        var tvRevenueValue = binding.tvRevenueValue
        var rectangleImage = binding.rectangleImage

    }

}
