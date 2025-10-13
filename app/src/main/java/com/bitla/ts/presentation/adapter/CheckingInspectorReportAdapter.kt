package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildCheckingInspectorReportBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.checking_inspector_report_data.InspectionResult

class CheckingInspectorReportAdapter(
    private val context: Context,
    private var dataList: List<InspectionResult>
): RecyclerView.Adapter<CheckingInspectorReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): CheckingInspectorReportAdapter.ViewHolder {
        val binding = ChildCheckingInspectorReportBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        val notAvailable = holder.itemView.context.getString(R.string.notAvailable)
        holder.totalSeatsValueTV.text = data.totalSeats.toString()?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.reservedSeatsValueTV.text = data.reservedSeats.toString()?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.maleSeatsValueTV.text = data.maleSeats.toString()?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.femaleSeatsValueTV.text = data.femaleSeats.toString()?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.extraCabinSeatsValueTV.text = data.extraCabinSeats.toString()?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.serviceNameTV.text = "${data.origin ?: ""} to ${data.destination ?: ""}".trim()?.takeIf { it.isNotEmpty() } ?: notAvailable
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(binding: ChildCheckingInspectorReportBinding) : RecyclerView.ViewHolder(binding.root) {
        val totalSeatsValueTV = binding.totalSeatsValueTV
        val reservedSeatsValueTV = binding.reservedSeatsValueTV
        val maleSeatsValueTV = binding.maleSeatsValueTV
        val femaleSeatsValueTV = binding.femaleSeatsValueTV
        val extraCabinSeatsValueTV = binding.extraCabinSeatsValueTV
        val serviceNameTV = binding.serviceNameTV
    }
}