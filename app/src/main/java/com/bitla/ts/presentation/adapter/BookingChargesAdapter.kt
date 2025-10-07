package com.bitla.ts.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildBookingAmountBinding
import com.bitla.ts.domain.pojo.fare_breakup.response.FareBreakUpHash
import com.bitla.ts.utils.common.convert

class BookingChargesAdapter(
    private val context: Context,
    private var fareBreakUpHashList: MutableList<FareBreakUpHash>,
    private val currency: String,
    private val currencyFormat: String
) :
    RecyclerView.Adapter<BookingChargesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildBookingAmountBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fareBreakUpHashList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fareBreakUpHash: FareBreakUpHash = fareBreakUpHashList[position]
        holder.tvLabel.text = fareBreakUpHash.label
        val totalFare =
            "$currency ${(fareBreakUpHash.value.toString().toDouble())?.convert(currencyFormat)}"
        holder.tvValue.text = totalFare
    }

    class ViewHolder(binding: ChildBookingAmountBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvLabel = binding.tvLabel
        val tvValue = binding.tvValue
    }
}