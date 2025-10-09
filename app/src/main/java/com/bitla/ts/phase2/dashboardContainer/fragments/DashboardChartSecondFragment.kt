package com.bitla.ts.phase2.dashboardContainer.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.databinding.FragmentDashboardChartSecondBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.phase2.adapter.parent.DashboardPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DashboardChartSecondFragment : BaseFragment() {

    private lateinit var binding: FragmentDashboardChartSecondBinding
    private var tabsList: MutableList<Tabs> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
    }

    override fun isInternetOnCallApisAndInitUI() {
        initTab()
    }

    override fun isNetworkOff() {
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {

        binding = FragmentDashboardChartSecondBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTab()
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    private fun initTab() {
        val tabOccupancy = Tabs()
        tabOccupancy.title = getString(R.string.occupancy)
        tabsList.add(tabOccupancy)

        val tabRevenue = Tabs()
        tabRevenue.title = getString(R.string.revenue)
        tabsList.add(tabRevenue)

        val tabPerformance = Tabs()
        tabPerformance.title = getString(R.string.performance)
        tabsList.add(tabPerformance)

        val tabServiceWiseBooking = Tabs()
        tabServiceWiseBooking.title = getString(R.string.service_wise_booking)
        tabsList.add(tabServiceWiseBooking)

        val tabSchedulesSummary = Tabs()
        tabSchedulesSummary.title = getString(R.string.schedules_summary)
        tabsList.add(tabSchedulesSummary)

        val tabPhoneBlocked = Tabs()
        tabPhoneBlocked.title = getString(R.string.phone_bookings)
        tabsList.add(tabPhoneBlocked)

        val tabPendingQuota = Tabs()
        tabPendingQuota.title = getString(R.string.pending_quota)
        tabsList.add(tabPendingQuota)


        val fragmentAdapter = DashboardPagerAdapter(
            requireContext(),
            tabsList,
            requireActivity()
        )
        binding.viewPagerDashboard.adapter = fragmentAdapter

        TabLayoutMediator(binding.tabsDashboard, binding.viewPagerDashboard) { tab, position ->
            // Customize tab labels if needed
            tab.text = when (position) {
                0 -> getString(R.string.occupancy)
                1 -> getString(R.string.revenue)
                2 -> getString(R.string.booking_trends)
                3 -> getString(R.string.service_wise_booking)
                4 -> getString(R.string.schedules_summary)
                5 -> getString(R.string.phone_bookings)
                6 -> getString(R.string.pending_quota)
                else -> {
                    getString(R.string.occupancy)
                }
            }
        }.attach()
//        binding.tabsDashboard.setupWithViewPager(binding.viewPagerDashboard)
        // custom tabs
        for (i in 0..binding.tabsDashboard.tabCount.minus(1)) {
            val tab = binding.tabsDashboard.getTabAt(i)!!
            tab.customView = null
            //tab!!.customView = fragmentAdapter.getTabView(i)

            val tabTextView: TextView = TextView(requireContext())
            tab.customView = tabTextView

            tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            tabTextView.text = tab.text

            if (i == 0) {
                // This set the font style of the first tab
                tabTextView.setTypeface(null, Typeface.BOLD)

            }
            if (i == 1) {
                // This set the font style of the first tab

                tabTextView.setTypeface(null, Typeface.NORMAL)

            }
        }

        binding.tabsDashboard.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPagerDashboard.currentItem = tab!!.position
                val text: TextView = tab.customView as TextView
                text.setTypeface(null, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text: TextView = tab?.customView as TextView
                text.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    }
}