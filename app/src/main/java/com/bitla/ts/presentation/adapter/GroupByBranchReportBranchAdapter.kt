package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildGroupByBranchTabBranchBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.group_by_branch_report_data.group_by_branch_report_response.GroupByBranchReportBranchData
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.common.formatCurrencyWithSymbol
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class GroupByBranchReportBranchAdapter(
    private val context: Context,
    private val branchList: ArrayList<GroupByBranchReportBranchData>,
    private var currency: String? = null,
    private var currencyFormat: String = ""
) : RecyclerView.Adapter<GroupByBranchReportBranchAdapter.ViewHolder>() {
    private var fareValueTv: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildGroupByBranchTabBranchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val branch = branchList[position]
        val notAvailable = holder.itemView.context.getString(R.string.notAvailable)

        holder.apply {
            serviceNameTV.text = branch.branchName?.takeIf { it.isNotEmpty() } ?: notAvailable
            serviceFareTVValue.text = if(branch.fare != null) {
                fareValueTv = branch.fare.toDouble().convert(currencyFormat)
                formatCurrencyWithSymbol(fareValueTv, 16, currency ?: context.getString(R.string.rupess_symble))
            } else {
                notAvailable
            }

            val childAdapter = GroupByBranchDetailsAdapter(context, branch.detail)
            listRV.layoutManager = LinearLayoutManager(context)
            listRV.adapter = childAdapter
        }
    }

    override fun getItemCount(): Int {
        return branchList.size
    }

    class ViewHolder(binding: ChildGroupByBranchTabBranchBinding) : RecyclerView.ViewHolder(binding.root) {
        val serviceNameTV = binding.tvServiceName
        val serviceFareTVValue = binding.tvServiceFareValue
        val listRV = binding.listRV
    }

}