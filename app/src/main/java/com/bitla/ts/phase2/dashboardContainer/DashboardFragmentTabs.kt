package com.bitla.ts.phase2.dashboardContainer

import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.databinding.FragmentDashboardChartBinding
import com.bitla.ts.databinding.FragmentDashboardTabsBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.phase2.adapter.DashboardTabsPagerAdapter
import com.bitla.ts.phase2.adapter.parent.DashboardPagerAdapter
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.google.android.material.tabs.TabLayout

class DashboardFragmentTabs : BaseFragment() {

    companion object {
        val TAG = DashboardFragmentTabs::class.java.simpleName
    }

    private lateinit var binding : FragmentDashboardTabsBinding
    private var tabsList: MutableList<Tabs> = mutableListOf()
    
    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardTabsBinding.inflate(inflater, container, false)
        val view: View = binding.root
        initTab()
        return view
    }

    private fun initTab() {

        (activity as? DashboardNavigateActivity)?.increaseMarginTop()

        val tabOccupancy = Tabs()
        tabOccupancy.title = getString(R.string.business)
        tabsList.add(tabOccupancy)

        val tabRevenue = Tabs()
        tabRevenue.title = getString(R.string.bookings)
        tabsList.add(tabRevenue)

        val fragmentAdapter = DashboardTabsPagerAdapter(requireActivity(), tabsList, childFragmentManager)
        binding.viewPagerDashboard.adapter = fragmentAdapter
        binding.tabsDashboard.setupWithViewPager(binding.viewPagerDashboard)
        // custom tabs
        for (i in 0..binding.tabsDashboard.tabCount.minus(1)) {
            val tab = binding.tabsDashboard.getTabAt(i)!!
            tab.customView = null
            //tab!!.customView = fragmentAdapter.getTabView(i)

            val tabTextView: TextView = TextView(requireActivity())
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

                /*val text: TextView = tab.customView as TextView
                text.setTypeface(null, Typeface.BOLD)
                binding.layoutToolbar.toolbarHeaderText.text = tab.text*/

//                removeDashboardFilterPref()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text: TextView = tab?.customView as TextView
                text.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.viewPagerDashboard.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
//                when (i) {
//                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
    }

}