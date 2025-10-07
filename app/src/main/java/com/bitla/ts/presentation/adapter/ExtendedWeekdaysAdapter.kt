package com.bitla.ts.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.LayoutExtendedWeekdaysBinding
import com.bitla.ts.domain.pojo.Weekdays
import gone

class ExtendedWeekdaysAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var weekdays: List<Weekdays>
) :
    RecyclerView.Adapter<ExtendedWeekdaysAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutExtendedWeekdaysBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return weekdays.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == 0)
            holder.view.gone()

        val weekdays: Weekdays = weekdays[position]
        holder.tvDay.text = weekdays.day

        setDaysBackground(weekdays, holder)

        holder.layoutWeekdays.setOnClickListener {
            holder.tvDay.tag = weekdays
            weekdays.isSelected = !(weekdays.isSelected)
            setDaysBackground(weekdays, holder)
            onItemClickListener.onClick(holder.tvDay, position)
        }


        if (weekdays.isSelected) {
            holder.layoutWeekdays.setBackgroundColor(Color.parseColor("#00adb5"))
            holder.tvDay.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.layoutWeekdays.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.tvDay.setTextColor(Color.parseColor("#9b9b9b"))
        }
    }

    private fun setDaysBackground(
        weekdays: Weekdays,
        holder: ViewHolder
    ) {
        if (weekdays.isSelected) {
            // Change this color for selection
            holder.layoutWeekdays.setBackgroundColor(Color.parseColor("#00adb5"))
            holder.tvDay.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.layoutWeekdays.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.tvDay.setTextColor(Color.parseColor("#9b9b9b"))
        }
    }


    class ViewHolder(binding: LayoutExtendedWeekdaysBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvDay: TextView = binding.tvDay
        val layoutWeekdays: LinearLayout = binding.layoutReccuringDays
        val view: View = binding.view
    }
}