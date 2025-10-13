package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildBusServiceCollectionReportBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.bus_service_collection_summary_report_data.BusServiceCollectionData
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.common.formatCurrencyWithSymbol

class BusServiceCollectionDetailedReportAdapter (
    private val context: Context,
    private var dataList: ArrayList<BusServiceCollectionData>,
    private var privilegeResponseModel: PrivilegeResponseModel?
): RecyclerView.Adapter<BusServiceCollectionDetailedReportAdapter.ViewHolder>() {

    private var currency: String? = null
    private var currencyFormat: String = ""
    private var fareValueTv: String = ""

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildBusServiceCollectionReportBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        getPref()
        return ViewHolder(binding)
    }

    private fun getPref() {
        if (privilegeResponseModel != null) {
            currency = privilegeResponseModel?.currency
                ?: context.getString(R.string.rupess_symble)
            currencyFormat =
                getCurrencyFormat(context, privilegeResponseModel?.currencyFormat)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        val notAvailable = holder.itemView.context.getString(R.string.notAvailable)
        holder.apply {
            pnrTV.text = data.pnrNumber?.takeIf { it.isNotEmpty() } ?: notAvailable
            originDestinationIdTV.text = if (!data.origin.isNullOrEmpty() && !data.destination.isNullOrEmpty()) {
                "${data.origin} - ${data.destination}"
            } else {
                notAvailable
            }
            seatNumValueTV.text = data.seatNumbers?.takeIf { it.isNotEmpty() } ?: notAvailable
            branchNameValueTV.text = data.issuedBy?.takeIf { it.isNotEmpty() } ?: notAvailable
            fareValueTV.text = if (!data.fare.isNullOrEmpty()) {
                fareValueTv = data.fare.toDouble().convert(currencyFormat)
                formatCurrencyWithSymbol(fareValueTv, 14, currency?: context.getString(R.string.rupess_symble))
            } else {
                notAvailable
            }
            netAmountValueTV.text = if (!data.netAmount.isNullOrEmpty()) {
                fareValueTv = data.netAmount.toDouble().convert(currencyFormat)
                formatCurrencyWithSymbol(fareValueTv, 14, currency?: context.getString(R.string.rupess_symble))
            } else {
                notAvailable
            }
            tvCoachNumber.text = if(!data.coachNumber.isNullOrEmpty()) {
                "(${data.coachNumber})"
            } else {
                "($notAvailable)"
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(binding: ChildBusServiceCollectionReportBinding) : RecyclerView.ViewHolder(binding.root) {
        val pnrTV = binding.pnrTV
        val originDestinationIdTV = binding.originDestinationIdTV
        val seatNumValueTV = binding.seatNumValueTV
        val branchNameValueTV = binding.branchNameValueTV
        val fareValueTV = binding.fareValueTV
        val netAmountValueTV = binding.netAmountValueTV
        val tvCoachNumber = binding.tvCoachNumber
    }


}