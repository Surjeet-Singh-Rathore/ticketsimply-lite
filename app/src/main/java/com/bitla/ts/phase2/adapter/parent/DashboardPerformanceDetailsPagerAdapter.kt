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
import com.bitla.ts.phase2.fragments.childFragments.bookingTrendsFragments.BranchContributionFragment
import com.bitla.ts.phase2.fragments.childFragments.bookingTrendsFragments.EBookingTrendsFragment
import com.bitla.ts.presentation.adapter.BusPagerAdapter

class DashboardPerformanceDetailsPagerAdapter(
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
                EBookingTrendsFragment()
            }
            1 -> {
                BranchContributionFragment()
            }
            /*2 -> {
                DaysWiseBookingTrendsFragment()
            }*/

            else -> {
                return EBookingTrendsFragment()
            }
        }
    }

    override fun getCount(): Int {
        return tabList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.e_tickets)
            1 -> context.getString(R.string.branch)
            //2 -> context.getString(R.string.best_days)
            else -> {
                return context.getString(R.string.e_tickets)
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