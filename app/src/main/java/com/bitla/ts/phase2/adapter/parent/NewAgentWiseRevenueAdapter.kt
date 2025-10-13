package com.bitla.ts.phase2.adapter.parent

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.AdapterAgentWiseRevenueBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.revenue_data.AgentWiseRevenue
import com.bitla.ts.utils.common.convert
import gone
import visible

class NewAgentWiseRevenueAdapter(
    private val context: Context,
    private val agentSummary: ArrayList<AgentWiseRevenue>?,
    private val privilegeResponse: PrivilegeResponseModel?,
) :
    RecyclerView.Adapter<NewAgentWiseRevenueAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterAgentWiseRevenueBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return agentSummary?.size!!
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = agentSummary?.get(position)
        holder.apply {
            titleTV.text = data?.agentName
            fareTV.text = privilegeResponse?.currency + data?.fare?.toDouble()?.convert(
                privilegeResponse?.currencyFormat
                    ?: context.getString(R.string.indian_currency_format)
            )
            cancellationTV.text =
                privilegeResponse?.currency + data?.cancellation?.toDouble()?.convert(
                    privilegeResponse?.currencyFormat
                        ?: context.getString(R.string.indian_currency_format)
                )
            seatCountTV.text = data?.bookedSeats.toString()
            if (data?.commision != null) {
                commissionLL.visible()
                commissionTV.text =
                    privilegeResponse?.currency + data.commision?.toDouble()?.convert(
                        privilegeResponse?.currencyFormat
                            ?: context.getString(R.string.indian_currency_format)
                    )
            } else {
                commissionLL.gone()
            }
        }

    }

    class ViewHolder(binding: AdapterAgentWiseRevenueBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val titleTV = binding.titleTV
        val fareTV = binding.fareValueTV
        val commissionTV = binding.commissionValueTV
        val cancellationTV = binding.cancellationValueTV
        val seatCountTV = binding.seatCountTV
        val commissionLL = binding.commissionLL
    }
}