package com.bitla.ts.phase2.adapter.parent

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterSummaryRevenueBinding
import com.bitla.ts.domain.pojo.revenue_data.ServiceSummary

class SummaryRevenueAdapter(
    private val context: Context,
    private val serviceSummary: ServiceSummary?,
) :
    RecyclerView.Adapter<SummaryRevenueAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterSummaryRevenueBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 5
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val busAmenity: BusAmenity = busAmenities[position]

    }

    class ViewHolder(binding: AdapterSummaryRevenueBinding) :
        RecyclerView.ViewHolder(binding.root)
}