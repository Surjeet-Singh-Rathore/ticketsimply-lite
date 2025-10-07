package com.bitla.ts.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildBusDelayBinding
import com.bitla.ts.domain.pojo.Weekdays


class BusDelayAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var weekdays: List<Weekdays>
) :
    RecyclerView.Adapter<BusDelayAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildBusDelayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return weekdays.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weekdays: Weekdays = weekdays[position]
        holder.tvBusDelayTime.text = weekdays.day

        setSelectedBackground(weekdays, holder)

        holder.tvBusDelayTime.setOnClickListener {
            weekdays.isSelected = !(weekdays.isSelected)
            setSelectedBackground(weekdays, holder)
            onItemClickListener.onClick(holder.tvBusDelayTime, position)
        }


        if (weekdays.isSelected) {
            holder.tvBusDelayTime.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00adb5"))
            holder.tvBusDelayTime.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.tvBusDelayTime.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#cfcfcf"))
            holder.tvBusDelayTime.setTextColor(Color.parseColor("#4a4a4a"))
        }
    }

    private fun setSelectedBackground(
        weekdays: Weekdays,
        holder: ViewHolder
    ) {
        if (weekdays.isSelected) {
            // Change this color for selection
            holder.tvBusDelayTime.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00adb5"))
            holder.tvBusDelayTime.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.tvBusDelayTime.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#cfcfcf"))
            holder.tvBusDelayTime.setTextColor(Color.parseColor("#4a4a4a"))
        }
    }


    class ViewHolder(binding: ChildBusDelayBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvBusDelayTime: TextView = binding.tvBusDelayTime
    }
}