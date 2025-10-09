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
import com.bitla.ts.phase2.fragments.childFragments.occupanyFragments.BookingSourceFragment
import com.bitla.ts.phase2.fragments.childFragments.occupanyFragments.DayWiseOccFragment
import com.bitla.ts.phase2.fragments.childFragments.occupanyFragments.SeatStatusFragment
import com.bitla.ts.phase2.fragments.childFragments.occupanyFragments.ServiceOccupancyFragment
import com.bitla.ts.presentation.adapter.BusPagerAdapter

class DashboardOccupancyDetailsPagerAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {
    companion object {
        val tag: String = BusPagerAdapter::class.java.simpleName
    }

    @SuppressLint("LogNotTimber")
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                DayWiseOccFragment()
            }
            1 -> {
                ServiceOccupancyFragment()
            }
            2 -> {
                BookingSourceFragment()
            }
            3 -> {
                SeatStatusFragment()
            }

            else -> {
                return DayWiseOccFragment()
            }
        }
    }

    override fun getCount(): Int {
        return tabList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.day_hyphen_wise)
            1 -> context.getString(R.string.service_hyphen_wise)
            2 -> context.getString(R.string.booking_source_hyphen_wise)
            3 -> context.getString(R.string.seat_status_hyphen_wise)
            else -> {
                return context.getString(R.string.day_hyphen_wise)
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