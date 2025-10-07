package com.bitla.ts.presentation.adapter.tripCollection

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildTripCollectionSublistBinding
import com.bitla.ts.databinding.ChildTripCollectionSublistBinding.inflate
import com.bitla.ts.domain.pojo.collection_details.trip_collection.PassengerDetailData
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y_H_M_AP
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y_NEW_LINE_H_M_AP
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChildTripCollectionDetailsAdapter(
    private val context: Context,
    private var passengerDetail: ArrayList<PassengerDetailData>,
   private val privileges: PrivilegeResponseModel?, ) : RecyclerView.Adapter<ChildTripCollectionDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val passengerDetail=passengerDetail[position]


        holder.tvname.text=passengerDetail.pnrNumber+"\n"+ passengerDetail.bookedDateTime?.substringBefore(" ")+ "\n"+passengerDetail.bookedDateTime?.substringAfter(" ")
        holder.fromTo.text=passengerDetail.fromTo


        val  currencyFormat = getCurrencyFormat(context, privileges?.currencyFormat)
        if (privileges?.currency?.isNotEmpty()==true) {
            holder.tvfare.text = "${privileges?.currency} ${
                (passengerDetail.amount)?.toDouble()?.convert(currencyFormat)}"
        } else {
            holder.tvfare.text = (passengerDetail.amount)?.toDouble()?.convert(currencyFormat)
        }

        holder.tvseatNumber.text=passengerDetail.seatNumbers.toString()
    }

    override fun getItemCount(): Int {
        return passengerDetail.size
    }


    class ViewHolder(binding: ChildTripCollectionSublistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvname = binding.passengerName
        val tvfare = binding.seatFare
        val tvseatNumber = binding.seatNumber
        val fromTo = binding.fromTo
    }
}