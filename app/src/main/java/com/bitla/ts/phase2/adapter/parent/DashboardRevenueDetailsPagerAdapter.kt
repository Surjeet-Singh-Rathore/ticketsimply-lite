package com.bitla.ts.phase2.adapter.parent

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bitla.ts.R
import com.bitla.ts.databinding.SlideTabBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.fragments.childFragments.revenueFragments.*
import com.bitla.ts.presentation.adapter.BusPagerAdapter

class DashboardRevenueDetailsPagerAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    fm: FragmentManager,
    var privilegeResponse: PrivilegeResponseModel? = null
) : FragmentStatePagerAdapter(fm) {
    companion object {
        val tag: String = BusPagerAdapter::class.java.simpleName
    }

    @SuppressLint("LogNotTimber")
    override fun getItem(position: Int): Fragment {


        return when (position) {
            0 -> {
                BranchWiseRevenueFragment()
            }
            1 -> {
                EmptySeatsFragment()
            }
            2 -> {
                ServiceWiseCollectionFragment()
            }
            3 -> {
                DayWiseCollectionFragment()
            }
            4 -> {
                if (privilegeResponse?.country == "India") {
                    GSTCollectionFragment()
                } else {
                    AgentWiseNetRevenueFragment()
                }
            }

            else -> {
                return BranchWiseRevenueFragment()
            }
        }
    }

    override fun getCount(): Int {
        return tabList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.branch_hyphen_wise)
            1 -> context.getString(R.string.empty_seats_hyphen_wise)
            2 -> context.getString(R.string.service_hyphen_wise)
            3 -> context.getString(R.string.day_hyphen_wise)
            4 -> {
                if(privilegeResponse?.country == "India") {
                    context.getString(R.string.gst_collection)
                } else {
                    context.getString(R.string.agent_wise_net_revenue)
                }
            }
            else -> {
                return context.getString(R.string.branch_hyphen_wise)
            }
        }
    }

    // custom tabs
    fun getTabView(position: Int): View {
        val binding = SlideTabBinding.inflate(LayoutInflater.from(context), null, false)

        //val view : View = LayoutInflater.from(context).inflate(R.layout.slide_tab,null)
        val tvTab: TextView = binding.tvTab
        tvTab.text = tabList[position].title

        return binding.root
    }
}