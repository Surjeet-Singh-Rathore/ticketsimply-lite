package com.bitla.ts.phase2.dashboardContainer.activity

import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.occupancy_calendar_method_name
import com.bitla.ts.databinding.ActivityDashboardRevenueDetailsBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.adapter.parent.DashboardRevenueDetailsPagerAdapter
import com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.request.OccupancyCalendarRequest
import com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.request.ReqBody
import com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response.Result
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_YY
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.android.material.tabs.TabLayout
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import java.time.YearMonth
import java.util.*

class DashboardRevenueDetailsActivity : BaseActivity(), DialogSingleButtonListener {

    private lateinit var binding: ActivityDashboardRevenueDetailsBinding
    private var tabsList: MutableList<Tabs> = mutableListOf()
    private var occupancyCalendarList = arrayListOf<Result>()
    var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var date: String = ""
    private var privilegeResponse: PrivilegeResponseModel? = null


    override fun initUI() {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardRevenueDetailsBinding.inflate(layoutInflater)
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
        binding.viewPagerRevenueDetails.currentItem = fragmentId


        binding.layoutToolbar.imgBack.setOnClickListener {
            onBackPressed()
        }


        val currentMonth = YearMonth.now()
        val startYYMMDD = "${currentMonth}-01"
        val next31Days = get31stDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y) ?: Date())
        val endYYMMDD = dateToString(next31Days, DATE_FORMAT_Y_M_D)
        //val endYYMMDD="${getNext31DayDate(stringToDate(date, DATE_FORMAT_D_M_Y) ?: Date())}"

        binding.layoutToolbar.imgToolbarCalendar.setOnClickListener {
            callOccupancyCalendarApi(getDateYMD(getTodayDate()), endYYMMDD)
        }

        setOccupancyCalendarObserver()
//        setNetworkConnectionObserver()
        lifecycleScope.launch {
            dashboardViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }


    private fun initTab() {
        val tabDayWise = Tabs()
        tabDayWise.title = getString(R.string.summary)
        tabsList.add(tabDayWise)

        val tabService = Tabs()
        tabService.title = getString(R.string.service)
        tabsList.add(tabService)

        val tabSeatStatus = Tabs()
        tabSeatStatus.title = getString(R.string.day_wise)
        tabsList.add(tabSeatStatus)

        val tabServiceWiseCollection = Tabs()
        tabServiceWiseCollection.title = getString(R.string.service_wise_collection)
        tabsList.add(tabServiceWiseCollection)

        val tabBookingSource = Tabs()
        tabsList.add(tabBookingSource)
        if(privilegeResponse?.country == "India") {
            tabBookingSource.title = getString(R.string.gst_collection)
        } else {
            tabBookingSource.title = getString(R.string.agent_wise_net_revenue)
        }


        val fragmentAdapter =
            DashboardRevenueDetailsPagerAdapter(this, tabsList, supportFragmentManager, privilegeResponse)
        binding.viewPagerRevenueDetails.adapter = fragmentAdapter
        binding.tabsRevenueDetails.setupWithViewPager(binding.viewPagerRevenueDetails)
        // custom tabs
        for (i in 0..binding.tabsRevenueDetails.tabCount.minus(1)) {
            val tab = binding.tabsRevenueDetails.getTabAt(i)!!
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
        binding.tabsRevenueDetails.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPagerRevenueDetails.currentItem = tab!!.position

                val text: TextView = tab.customView as TextView
                text.setTypeface(null, Typeface.BOLD)
//                binding.layoutToolbar.toolbarHeaderText.text = tab.text
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text: TextView = tab?.customView as TextView
                text.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        date = getDateDMY(PreferenceUtils.getDashboardCurrentDate()).toString()
        privilegeResponse = getPrivilegeBase()
    }

    private fun callOccupancyCalendarApi(startDate: String, endDate: String) {
        val occupancyCalendarRequest = OccupancyCalendarRequest(
            bccId.toString(),
            format_type,
            occupancy_calendar_method_name,

            ReqBody(
                apiKey = loginModelPref.api_key,
                reservationId = -1,
                startDate = startDate,
                endDate = endDate
            )
        )
        /*dashboardViewModel.occupancyCalendarApi(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            occupancyCalendarRequest,
            occupancy_calendar_method_name
        ) */

        dashboardViewModel.occupancyCalendarApi(
            ReqBody(
                apiKey = loginModelPref.api_key,
                reservationId = -1,
                startDate = startDate,
                endDate = endDate
            ),
            occupancy_calendar_method_name
        )
    }

    private fun setOccupancyCalendarObserver() {
        dashboardViewModel.occupancyCalendarViewModel.observe((this)) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        for (i in 0 until it.result.size) {
                            occupancyCalendarList.add(
                                Result(
                                    it.result[i].day,
                                    it.result[i].occupancy.toDouble().toString(),
                                )
                            )
                        }
                        DialogUtils.dialogOccupancyCalendar(
                            this,
                            occupancyCalendarList, this
                        )
                    }
                    else -> {
                        toast("$it")
                    }
                }
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
    }

}