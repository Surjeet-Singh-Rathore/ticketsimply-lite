package com.bitla.ts.phase2.adapter.parent

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.AdapterRevenueServicesBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.revenue_data.RevenueRouteDetails
import com.bitla.ts.utils.common.convert
import gone
import visible

class RevenueServicesAdapter(
    private val context: Context,
    private val revenueRouteList: ArrayList<RevenueRouteDetails>,
    private val privilegeResponseModel: PrivilegeResponseModel?,
    private val onItemClickListener: OnItemClickListener,
    private val isCardClickable: Boolean = true
) :
    RecyclerView.Adapter<RevenueServicesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterRevenueServicesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return revenueRouteList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val serviceRevenueData = revenueRouteList[position]

        holder.serviceName.text = serviceRevenueData.name
        holder.seatCountTV.text = serviceRevenueData.totalBookedSeats.toString()
        holder.totalRevenueTV.text = privilegeResponseModel?.currency + serviceRevenueData.revenue?.toDouble()?.convert(
                privilegeResponseModel?.currencyFormat
                    ?: context.getString(R.string.indian_currency_format))
        holder.grossTV.text = privilegeResponseModel?.currency + serviceRevenueData.gross?.toDouble()?.convert(
            privilegeResponseModel?.currencyFormat
                ?: context.getString(R.string.indian_currency_format))
        holder.deductionTV.text = privilegeResponseModel?.currency + serviceRevenueData.deduction?.toDouble()?.convert(
            privilegeResponseModel?.currencyFormat
                ?: context.getString(R.string.indian_currency_format))
        holder.cardL.setOnClickListener {
            if (isCardClickable)
                onItemClickListener.onClick(holder.cardL, position)
        }
        if (isCardClickable) {
            holder.rightArrowIV.visible()
        } else {
            holder.rightArrowIV.gone()

        }

        holder.rightArrowIV.setOnClickListener {
            if (isCardClickable)
                onItemClickListener.onClick(holder.rightArrowIV, position)

        }

    }

    class ViewHolder(binding: AdapterRevenueServicesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cardL = binding.cardCL
        val rightArrowIV = binding.rightArrowIV
        val serviceName = binding.titleTV
        val seatCountTV = binding.seatCountTV
        val grossTV = binding.grossValueTV
        val deductionTV = binding.deductionValueTV
        val totalRevenueTV = binding.priceTV

    }
}