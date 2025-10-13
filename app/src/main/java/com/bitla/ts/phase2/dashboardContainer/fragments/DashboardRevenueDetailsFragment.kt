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
import com.bitla.ts.databinding.FragmentDashboardRevenueDetailsBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.phase2.adapter.parent.DashboardRevenueDetailsPagerAdapter
import com.google.android.material.tabs.TabLayout

class DashboardRevenueDetailsFragment : BaseFragment() {

    private lateinit var binding: FragmentDashboardRevenueDetailsBinding
    private var tabsList: MutableList<Tabs> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        binding = FragmentDashboardRevenueDetailsBinding.inflate(inflater, container, false)
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
        val tabDayWise = Tabs()
        tabDayWise.title = getString(R.string.summary)
        tabsList.add(tabDayWise)

        val tabService = Tabs()
        tabService.title = getString(R.string.service)
        tabsList.add(tabService)

        val tabBookingSource = Tabs()
        tabBookingSource.title = getString(R.string.gst_collection)
        tabsList.add(tabBookingSource)

        val tabSeatStatus = Tabs()
        tabSeatStatus.title = getString(R.string.day_wise)
        tabsList.add(tabSeatStatus)

        val tabBranchWiseCollection = Tabs()
        tabBranchWiseCollection.title = getString(R.string.service_wise_collection)
        tabsList.add(tabBranchWiseCollection)


        val fragmentAdapter = DashboardRevenueDetailsPagerAdapter(
            requireContext(),
            tabsList,
            requireActivity().supportFragmentManager
        )
        binding.viewPagerDashboard.adapter = fragmentAdapter
        binding.tabsDashboard.setupWithViewPager(binding.viewPagerDashboard)
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