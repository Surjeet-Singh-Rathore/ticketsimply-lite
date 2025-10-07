//package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

/*class BoardingPointListAdapter (
    private val context: Context?,
    private val boardingPointDetailsList: List<Detail>,
    private val onItemClickListener: BookingSummaryActivity,
    private val isCardClickable: Boolean = true
) :
    RecyclerView.Adapter<SummaryBookingAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterSummaryBookingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return OTADetailsList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingSummaryDetailsData = OTADetailsList[position]

        holder.OtaName.text = bookingSummaryDetailsData.name
        holder.seatsCount.text = bookingSummaryDetailsData.seatCount
        holder.tvRevenue.text = bookingSummaryDetailsData.revenue
    }

    class ViewHolder(binding: AdapterSummaryBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val OtaName = binding.titleTV
        val seatsCount = binding.title1TV
        val tvRevenue = binding.title2TV


    }
}*/