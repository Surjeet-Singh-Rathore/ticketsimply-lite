package com.bitla.ts.phase2.adapter.child

import android.content.*
import android.os.*
import android.view.*
import androidx.annotation.*
import androidx.recyclerview.widget.*
import com.bitla.ts.databinding.*
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.response.*

class SchedulesSummaryActiveAdapter(
    private val context: Context,
    private var activeServicesList: List<ActiveService>,
) :
    RecyclerView.Adapter<SchedulesSummaryActiveAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildActiveSchedulesSummaryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return activeServicesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = activeServicesList[position]
        
        holder.cardViewContainer.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        holder.tvServiceName.text = item.service
    }

    class ViewHolder(binding: ChildActiveSchedulesSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvServiceName = binding.tvServiceName
        val cardViewContainer = binding.cardView
    }
}