package com.bitla.ts.phase2.adapter.child

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildCancelledSchedulesSummaryBinding
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.response.CancelledService

class SchedulesSummaryCancelledAdapter(
    private val context: Context,
    private var cancelledServicesList: List<CancelledService>
) :
    RecyclerView.Adapter<SchedulesSummaryCancelledAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildCancelledSchedulesSummaryBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cancelledServicesList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: CancelledService = cancelledServicesList[position]
        holder.serviceName.text = item.service
        holder.tvCancelledBy.text = item.cancelledBy
        holder.tvTime.text = item.time
    }

    class ViewHolder(binding: ChildCancelledSchedulesSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val serviceName = binding.serviceName
        val tvCancelledBy = binding.tvCancelledBy
        val tvTime = binding.tvTime
    }
}