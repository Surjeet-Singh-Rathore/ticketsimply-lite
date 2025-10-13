package com.bitla.ts.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.AdapterChildGroupTabReportBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.group_by_branch_report_data.group_by_branch_report_response.GroupByBranchReportDetailData
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.common.formatCurrencyWithSymbol
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class GroupByBranchDetailsAdapter(
    private val context: Context,
    private val dataList: ArrayList<GroupByBranchReportDetailData>
): RecyclerView.Adapter<GroupByBranchDetailsAdapter.ViewHolder>() {
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var currency: String? = null
    private var currencyFormat: String = ""
    private var fareValueTv: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterChildGroupTabReportBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        getPref()
        return ViewHolder(binding)
    }

    private fun getPref() {
        if (PreferenceUtils.getPrivilege() != null) {
            privilegeResponseModel = PreferenceUtils.getPrivilege()!!
            if (privilegeResponseModel != null) {
                currency = privilegeResponseModel?.currency
                    ?: context.getString(R.string.rupess_symble)
                currencyFormat =
                    getCurrencyFormat(context, privilegeResponseModel?.currencyFormat)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        val notAvailable = holder.itemView.context.getString(R.string.notAvailable)

        holder.apply {
            confirmTV.text = data.bookingStatus?.takeIf { it.isNotEmpty() } ?: notAvailable
            pnrNumberTV.text = data.pnrNumber?.takeIf { it.isNotEmpty() } ?: notAvailable
            fareValueTv = data.fare.convert(currencyFormat)
            val fareValueDouble = fareValueTv?.replace(",", "")?.toDoubleOrNull() ?: 0.0
            fareTV.text = if (fareValueDouble > 0) {
                "(${formatCurrencyWithSymbol(fareValueTv, 16, currency ?: context.getString(R.string.rupess_symble))})"
            } else {
                "($notAvailable)"
            }
            mobileNumberTV.text = data.customerPhoneNumber?.takeIf { it.isNotEmpty() } ?: notAvailable
            serviceNameTVValueTV.text = data.boardingStage?.takeIf { it.isNotEmpty() } ?: notAvailable
            customerNameTV.text = data.customerName?.takeIf { it.isNotEmpty() } ?: notAvailable
            seatNumberValueTV.text = data.seatNumbers?.takeIf { it.isNotEmpty() } ?: notAvailable
            bookedByValueTV.text = data.userBooked?.takeIf { it.isNotEmpty() } ?: notAvailable
        }

        holder.confirmTV.apply {
            background = ContextCompat.getDrawable(context, R.drawable.less_rounded_green_background)
            backgroundTintList = ColorStateList.valueOf(
                when (data.bookingStatus.lowercase()) {
                    "confirm" -> ContextCompat.getColor(context, R.color.colorGreen)
                    "pending" -> ContextCompat.getColor(context, R.color.lightest_yellow)
                    "release" -> ContextCompat.getColor(context, R.color.stages_red)
                    "cancel" -> ContextCompat.getColor(context, R.color.stages_red)
                    else -> ContextCompat.getColor(context, R.color.colorGreen)
                }
            )
        }

        holder.apply {
            pnrNumberIV.setOnClickListener {
                if (constraintData.visibility == View.GONE) {
                    constraintData.visibility = View.VISIBLE
                    pnrNumberIV.setImageResource(R.drawable.ic_arrow_up_group_by_branch_report)
                } else {
                    constraintData.visibility = View.GONE
                    pnrNumberIV.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(binding: AdapterChildGroupTabReportBinding): RecyclerView.ViewHolder(binding.root) {

        val confirmTV = binding.confirmTV
        val pnrNumberTV = binding.pnrNumberTV
        val fareTV = binding.fareTV
        val mobileNumberTV = binding.mobileNumberTV
        val serviceNameTVValueTV = binding.serviceNameTVValueTV
        val customerNameTV = binding.customerNameTV
        val seatNumberValueTV = binding.seatNumberValueTV
        val bookedByValueTV = binding.bookedByValueTV
        val constraintData = binding.constraintData
        val pnrNumberIV = binding.pnrNumberIV
    }

}