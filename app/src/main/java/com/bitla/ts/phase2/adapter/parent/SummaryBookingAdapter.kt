package com.bitla.ts.phase2.adapter.parent


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterSummaryBookingBinding
import com.bitla.ts.domain.pojo.booking_summary_details.Detail
import com.bitla.ts.presentation.view.activity.ticketDetails.BookingSummaryActivity
import com.bitla.ts.utils.common.convert

class SummaryBookingAdapter(
    private val context: Context?,
    private val OTADetailsList: List<Detail>,
    private val onItemClickListener: BookingSummaryActivity,
    private val isCardClickable: Boolean = true,
    private val currency: String,
    private val  currencyFormat: String

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
//        holder.tvRevenue.text = bookingSummaryDetailsData.revenue

        val revenue = "$currency${bookingSummaryDetailsData.revenue.toString().toDouble().convert(currencyFormat)}"
        holder.tvRevenue.text = revenue
    }

    class ViewHolder(binding: AdapterSummaryBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val OtaName = binding.titleTV
        val seatsCount = binding.title1TV
        val tvRevenue = binding.title2TV

    }
}