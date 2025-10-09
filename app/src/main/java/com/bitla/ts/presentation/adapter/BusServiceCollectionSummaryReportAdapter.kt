package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.AdapterOriginDestinationBusServiceCollectionSummaryLayoutBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.bus_service_collection_summary_report_data.BusServiceCollectionData

class BusServiceCollectionSummaryReportAdapter(
    private val context: Context,
    private var dataList: ArrayList<BusServiceCollectionData>
): RecyclerView.Adapter<BusServiceCollectionSummaryReportAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterOriginDestinationBusServiceCollectionSummaryLayoutBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        val notAvailable = holder.itemView.context.getString(R.string.notAvailable)
        holder.originDestinationNameTV.text = data.fromToTrip?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.originDestinationSeatValueTV.text = data.fromToSeats?.takeIf { it.isNotEmpty() } ?: notAvailable
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(binding: AdapterOriginDestinationBusServiceCollectionSummaryLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val originDestinationNameTV = binding.originDestinationNameTV
        val originDestinationSeatValueTV = binding.originDestinationSeatValueTV
    }
}