package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bitla.ts.R
import com.bitla.ts.databinding.SlideTabBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.presentation.view.dashboard.ViewReservationFragments.ReservationChartFragment
import com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments.CommissionFragment
import com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments.FareFragment
import com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments.NewFareFragment
import com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments.TimeFragment

class UpdateRateCardAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    fm: FragmentManager,
    var currentCountry: String?,
    var isHideCommissionTab: Boolean?,
    var isMultiHopService: Boolean
) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> {
                if (currentCountry.equals("india", true)) {
                    NewFareFragment()
                } else {
                    FareFragment()
                }
            }
            1 -> {
                TimeFragment(isMultiHopService)
            }
            2 -> {
                CommissionFragment()
            }
           /* 3 -> {
                MultistationFragment()
            }
            4 -> {
                SeatWiseFragment()
            }*/
            else -> {
                return ReservationChartFragment()
            }
        }
    }

    override fun getCount(): Int {
        return tabList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.fare)
            1 -> context.getString(R.string.time)
            2 -> context.getString(R.string.commission)
        /*    3 -> context.getString(R.string.multi_station)
            4 -> context.getString(R.string.seat_wise)*/
            else -> {
                return context.getString(R.string.fare)
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