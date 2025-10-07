package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.AdapterServiceSummaryBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_summary.SummaryData
import com.bitla.ts.presentation.adapter.SortByAdaper.ServiceSummaryItemsAdapter
import com.bitla.ts.utils.common.convert
import gone
import toast

class ServiceSummaryAdapter(
    private val context: Context,
    private val bookingDetails: ArrayList<SummaryData>?=null,
    private val currency: String,
    private val currencyFormat: String
    ) :
    RecyclerView.Adapter<ServiceSummaryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterServiceSummaryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookingDetails?.size?:0
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var data = SummaryData()
        if (!bookingDetails.isNullOrEmpty()){
            data = bookingDetails[position]
        }
        holder.labelTV.text = data.label
        holder.countTV.text = data.count.toString()
        try {
            if(currency.isNullOrEmpty()){
                holder.amountTV.text = "$currency ${data.amount?.convert(currencyFormat)}"
            }else{
                holder.amountTV.text = "${data.amount?.convert(currencyFormat)}"
            }
        }catch (e: Exception){
            context.toast(e.message)
        }

        if (data.details?.isNotEmpty() == true) {
            val adapter = ServiceSummaryItemsAdapter(context,data.details,currency,currencyFormat)
            holder.itemsRV.adapter = adapter
        } else {
            holder.itemsRV.gone()
        }




    }

    class ViewHolder(binding: AdapterServiceSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val itemsRV = binding.serviceRV
        val labelTV = binding.labelTV
        val countTV = binding.countTV
        val amountTV = binding.amountTV

    }
}