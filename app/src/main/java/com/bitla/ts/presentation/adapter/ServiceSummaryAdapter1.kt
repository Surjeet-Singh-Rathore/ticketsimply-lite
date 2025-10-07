package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.databinding.ItemListBookingServiceSummary1Binding
import com.bitla.ts.domain.pojo.booking.SummaryInfoData
import com.bitla.ts.utils.common.convert
import visible

class ServiceSummaryAdapter1(
    private val context: Context,
    private val seatOnClick: (heading: String, position: Int)-> Unit,
    private var cityList: List<SummaryInfoData>,
    private var isAmount: Boolean,
    private var currency: String,
    private var currencyFormat: String
) :
    RecyclerView.Adapter<ServiceSummaryAdapter1.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListBookingServiceSummary1Binding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cityList.size
    }


    class ViewHolder(binding: ItemListBookingServiceSummary1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.textName
        val moreSeats = binding.textNumberOfSeats
        val amount = binding.textAmount
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val summaryInfoData: SummaryInfoData = cityList.get(position)
        holder.name.text = summaryInfoData.name
        if (summaryInfoData.seatNumber.isNotEmpty()) {
            val seatNo = summaryInfoData.seatNumber.split(",")
            val totalSeats = seatNo.size
            if (isAmount) {
                holder.amount.visible()
                try {
                    holder.amount.text =
                        "$currency${
                            if (summaryInfoData.amount.isNotEmpty()) (summaryInfoData.amount.toDouble()).convert(
                                currencyFormat
                            ) else summaryInfoData.amount
                        }"
                }catch (e: Exception){
                    if(BuildConfig.DEBUG){
                        e.printStackTrace()
                    }
                }

            }

            if (seatNo.size > 0) {
                holder.moreSeats.text = totalSeats.toString()
                holder.moreSeats.setOnClickListener {
                    seatOnClick.invoke(holder.name.text.toString(), position)
                }
            } else
                holder.moreSeats.text = context.getString(R.string.empty)
        }
    }
}