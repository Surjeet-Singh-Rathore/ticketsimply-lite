package com.bitla.ts.phase2.dashboardContainer.activity

import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ActivityRevenueDetailsNewBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.phase2.adapter.parent.DashboardRevenuePagerAdapter
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.utils.common.getDateDMY
import com.bitla.ts.utils.common.inputFormatToOutput
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_YY
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.material.tabs.TabLayout
import gone
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardRevenueNewActivity : BaseActivity(), DialogSingleButtonListener,
    OnItemClickListener, OnPageChangeListener {

    private lateinit var binding: ActivityRevenueDetailsNewBinding
    private var tabsList: MutableList<Tabs> = mutableListOf()
    var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var date: String = ""


    override fun initUI() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRevenueDetailsNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPref()

        binding.layoutToolbar.toolbarHeaderText.text = getString(R.string.revenue)
        binding.layoutToolbar.imgToolbarSearch.gone()

        val fragmentId = intent.getIntExtra(getString(R.string.fragmentTabPosition), 0)

        binding.layoutToolbar.tvCurrentHeader.text = inputFormatToOutput(
            date,
            DATE_FORMAT_D_M_Y,
            DATE_FORMAT_D_M_YY
        )

        initTab()

        binding.viewPagerDashboard.currentItem = fragmentId



        binding.layoutToolbar.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }


    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        date = getDateDMY(PreferenceUtils.getDashboardCurrentDate()).toString()

    }

    private fun initTab() {

        val tabOccupancy = Tabs()
        tabOccupancy.title = getString(R.string.service)
        tabsList.add(tabOccupancy)

        val tabRevenue = Tabs()
        tabRevenue.title = getString(R.string.channel)
        tabsList.add(tabRevenue)

        val tabPerformance = Tabs()
        tabPerformance.title = getString(R.string.agent)
        tabsList.add(tabPerformance)

        val tabServiceWiseBooking = Tabs()
        tabServiceWiseBooking.title = getString(R.string.hub)
        tabsList.add(tabServiceWiseBooking)



        val fragmentAdapter = DashboardRevenuePagerAdapter(this, tabsList, this.supportFragmentManager)
        binding.viewPagerDashboard.adapter = fragmentAdapter
        binding.tabsDashboard.setupWithViewPager(binding.viewPagerDashboard)
        // custom tabs
        for (i in 0..binding.tabsDashboard.tabCount.minus(1)) {
            val tab = binding.tabsDashboard.getTabAt(i)!!
            tab.customView = null
            //tab!!.customView = fragmentAdapter.getTabView(i)

            val tabTextView: TextView = TextView(this)
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


        binding.viewPagerDashboard.addOnPageChangeListener(this)



        binding.tabsDashboard.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPagerDashboard.currentItem = tab!!.position

                val text: TextView = tab.customView as TextView
                text.setTypeface(null, Typeface.BOLD)
                binding.layoutToolbar.toolbarHeaderText.text = tab.text

//                removeDashboardFilterPref()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text: TextView = tab?.customView as TextView
                text.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.viewPagerDashboard.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
//                when (i) {
//                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
    }


    override fun onSingleButtonClick(str: String) {
//        toast("$str")
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {


    }

    override fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        busData: com.bitla.ts.domain.pojo.available_routes.Result
    ) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        PreferenceUtils.putRevenueFilterList(mutableListOf())
    }

    override fun onPageSelected(position: Int) {
        PreferenceUtils.putRevenueFilterList(mutableListOf())
    }

    override fun onPageScrollStateChanged(state: Int) {

    }


}