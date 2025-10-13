package com.bitla.ts.phase2.adapter

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
import com.bitla.ts.phase2.dashboardContainer.DashboardFragmentChart
import com.bitla.ts.phase2.fragments.mainFragments.*
import com.bitla.ts.presentation.adapter.BusPagerAdapter
import com.bitla.ts.presentation.view.dashboard.DashboardFragment

class DashboardTabsPagerAdapter(
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
                DashboardFragmentChart()
            }
            1 -> {
                DashboardFragment()
            }
            else -> {
                return DashboardFragmentChart()
            }
        }
    }

    override fun getCount(): Int {
        return tabList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.business)
            1 -> context.getString(R.string.bookings)

            else -> {
                return context.getString(R.string.bookings)
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