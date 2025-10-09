package com.bitla.ts.phase2.adapter.parent

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bitla.ts.R
import com.bitla.ts.databinding.SlideTabBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.phase2.fragments.mainFragments.*
import com.bitla.ts.presentation.adapter.BusPagerAdapter

class DashboardPagerAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    fm: FragmentActivity
) : FragmentStateAdapter(fm,) {
    companion object {
        val tag: String = BusPagerAdapter::class.java.simpleName
    }

//    @SuppressLint("LogNotTimber")
//    override fun getItem(position: Int): Fragment {
//
//
//        return when (position) {
//            0 -> {
//                OccupancyFragment()
//            }
//            1 -> {
//                RevenueFragment()
//            }
//            2 -> {
//                BookingTrendsFragment()
//            }
//            3 -> {
//                ServiceWiseBookingFragment()
//            }
//            4 -> {
//                SchedulesSummaryFragment()
//            }
//            5 -> {
//                PhoneBlockedFragment()
//            }
//            6 -> {
//                PendingQuotaFragment()
//            }
//            else -> {
//                return OccupancyFragment()
//            }
//        }
//    }

//    override fun getCount(): Int {
//        return tabList.size
//    }

//    override fun getPageTitle(position: Int): CharSequence {
//        return when (position) {
//            0 -> context.getString(R.string.occupancy)
//            1 -> context.getString(R.string.revenue)
//            2 -> context.getString(R.string.booking_trends)
//            3 -> context.getString(R.string.service_wise_booking)
//            4 -> context.getString(R.string.schedules_summary)
//            5 -> context.getString(R.string.phone_bookings)
//            6 -> context.getString(R.string.pending_quota)
//            else -> {
//                return context.getString(R.string.occupancy)
//            }
//        }
//    }

    // custom tabs
    fun getTabView(position: Int): View {
        val binding = SlideTabBinding.inflate(LayoutInflater.from(context), null, false)

        //val view : View = LayoutInflater.from(context).inflate(R.layout.slide_tab,null)
        val tvTab: TextView = binding.tvTab
        tvTab.text = tabList[position].title

        return binding.root
    }

    override fun getItemCount(): Int {
        return tabList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                OccupancyFragment()
            }
            1 -> {
                RevenueFragment()
            }
            2 -> {
                BookingTrendsFragment()
            }
            3 -> {
                ServiceWiseBookingFragment()
            }
            4 -> {
                SchedulesSummaryFragment()
            }
            5 -> {
                PhoneBlockedFragment()
            }
            6 -> {
                PendingQuotaFragment()
            }
            else -> {
                return OccupancyFragment()
            }
        }
    }
}