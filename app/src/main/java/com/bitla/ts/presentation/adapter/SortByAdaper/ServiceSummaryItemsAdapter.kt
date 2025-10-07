package com.bitla.ts.presentation.adapter.SortByAdaper

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.AdapterServiceSummaryBinding
import com.bitla.ts.databinding.AdapterServiceSummaryItemsBinding
import com.bitla.ts.databinding.ChildAmenitiesAdapterBinding
import com.bitla.ts.domain.pojo.available_routes.BusAmenity
import com.bitla.ts.domain.pojo.service_summary.SummaryData
import com.bitla.ts.utils.common.convert
import com.bumptech.glide.Glide

class ServiceSummaryItemsAdapter(
    private val context: Context,
    private val detailsList: ArrayList<SummaryData?>?,
    private val currency: String?,
    private val currencyFormat: String

) :
    RecyclerView.Adapter<ServiceSummaryItemsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterServiceSummaryItemsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return detailsList?.size ?: 0
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = detailsList?.get(position)
        holder.tvLabel.text = item?.label ?: ""
        holder.tvCount.text = "${item?.count ?: ""}"
        holder.tvAmount.text = "$currency ${item?.amount?.convert(currencyFormat) ?: ""}"

    }

    class ViewHolder(binding: AdapterServiceSummaryItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvLabel = binding.tvLabel
        val tvCount = binding.tvCount
        val tvAmount = binding.tvAmount
    }
}