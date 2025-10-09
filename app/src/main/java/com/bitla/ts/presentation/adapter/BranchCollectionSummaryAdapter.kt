package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.BuildConfig
import com.bitla.ts.databinding.ChildBranchCollectionSummaryReportBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.branch_collection_summary_report_data.TicketData
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class BranchCollectionSummaryAdapter(
    private val context: Context,
    private var dataListList: ArrayList<TicketData>,
    val privileges: PrivilegeResponseModel?

) :
    RecyclerView.Adapter<BranchCollectionSummaryAdapter.ViewHolder>() {
    private var TAG: String = BranchCollectionSummaryAdapter::class.java.simpleName
    private var currency: String = ""
    private var currencyFormat: String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ChildBranchCollectionSummaryReportBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataListList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (privileges != null) {
            currency = privileges.currency
            currencyFormat = getCurrencyFormat(context, privileges.currencyFormat)
        }

        val data = dataListList[position]

        try {
            holder.seatCountValueTV.text = data.bookingCount.toString()
            holder.bookingAmountValueTV.text = currency + data.bookingAmount?.toDouble()?.convert(currencyFormat)
            holder.cancellationAmountValueTV.text = currency + data.cancelAmount?.toDouble()?.convert(currencyFormat)
            holder.totalAmountValueTV.text = currency + data.totalAmount?.toDouble()?.convert(currencyFormat)
            holder.serviceNumberTV.text = data.branchName

           // holder.totalFareTV.text = data.totalFare

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }


    class ViewHolder(binding: ChildBranchCollectionSummaryReportBinding) : RecyclerView.ViewHolder(binding.root) {
        val seatCountValueTV = binding.seatCountValueTV
        val bookingAmountValueTV = binding.bookingAmountValueTV
        val cancellationAmountValueTV = binding.cancellationAmountValueTV
        val totalAmountValueTV = binding.totalAmountValueTV
        val serviceNumberTV = binding.serviceNumberTV
    }
}