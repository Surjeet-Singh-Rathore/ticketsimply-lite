package com.bitla.ts.phase2.adapter.child

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildOccupancyCalendarBinding

class OccupancyCalendarAdapter(
    private val context: Context,
    private var occupancyCalendarResponse: MutableList<com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response.Result>
) :
    RecyclerView.Adapter<OccupancyCalendarAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildOccupancyCalendarBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return occupancyCalendarResponse.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val occupancyCalendarData: com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response.Result =
            occupancyCalendarResponse[position]

        holder.tvDay.text = occupancyCalendarData.day
//        holder.tvOccupancy.text = "${occupancyCalendarData.occupancy}%"


        if (occupancyCalendarData.occupancy.toInt() >= 50) {
            holder.tvOccupancy.setBackgroundResource(R.drawable.layout_rounded_shape_occupancy_green)
            holder.tvOccupancy.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        if (occupancyCalendarData.occupancy.toInt() <= 10) {
            holder.tvOccupancy.setBackgroundResource(R.drawable.layout_rounded_shape_occupancy_red)
            holder.tvOccupancy.setTextColor(ContextCompat.getColor(context, R.color.white))
        }

    }

    class ViewHolder(binding: ChildOccupancyCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvDay = binding.tvDay
        val tvOccupancy = binding.tvOccupancy
    }
}