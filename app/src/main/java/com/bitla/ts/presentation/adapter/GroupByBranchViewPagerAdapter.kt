package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bitla.ts.BuildConfig
import com.bitla.ts.domain.pojo.all_reports.new_response.group_by_branch_report_data.group_by_branch_report_response.GroupByBranchReportResponse
import com.bitla.ts.presentation.view.fragments.GroupByBranchReportApiBookingFragment
import com.bitla.ts.presentation.view.fragments.GroupByBranchReportBranchFragment
import com.bitla.ts.presentation.view.fragments.GroupByBranchReportETicketFragment

class GroupByBranchViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val groupByBranchData: GroupByBranchReportResponse
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    @SuppressLint("LogNotTimber")
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val branchData = if (groupByBranchData.branch.isNullOrEmpty()) {
                    null
                } else {
                    groupByBranchData.branch
                }
                GroupByBranchReportBranchFragment.newInstance(branchData ?: arrayListOf())
            }
            1 -> {
                val eTicketData = if (groupByBranchData.eTicket.isNullOrEmpty()) {
                    null
                } else {
                    groupByBranchData.eTicket
                }
                GroupByBranchReportETicketFragment.newInstance(eTicketData ?: arrayListOf())
            }
            2 -> {
                val apiBookingData = if (groupByBranchData.apiBooking.isNullOrEmpty()) {
                    null
                } else {
                    groupByBranchData.apiBooking
                }
                GroupByBranchReportApiBookingFragment.newInstance(apiBookingData ?: arrayListOf())
            }
            else -> {
                GroupByBranchReportBranchFragment.newInstance(groupByBranchData.branch)
            }
        }
    }
}