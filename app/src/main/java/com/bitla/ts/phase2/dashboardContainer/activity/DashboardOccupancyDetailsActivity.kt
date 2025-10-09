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
import com.bitla.ts.databinding.ActivityDashboardOccupancyDetailsBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.phase2.adapter.parent.DashboardOccupancyDetailsPagerAdapter
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

class DashboardOccupancyDetailsActivity : BaseActivity(), DialogSingleButtonListener {

    private lateinit var binding: ActivityDashboardOccupancyDetailsBinding
    private var tabsList: MutableList<Tabs> = mutableListOf()
    private var occupancyCalendarList = arrayListOf<Result>()
    var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var date = ""

    override fun initUI() {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardOccupancyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPref()
        getParentIntent()

        val fragmentId = intent.getIntExtra(getString(R.string.fragmentTabPosition), 0)
        binding.layoutToolbar.toolbarHeaderText.text = getString(R.string.occupancy_)
        binding.layoutToolbar.imgToolbarSearch.gone()
        binding.layoutToolbar.tvCurrentHeader.text = inputFormatToOutput(
            date,
            DATE_FORMAT_Y_M_D,
            DATE_FORMAT_D_M_YY
        )

        initTab()
        binding.viewPagerOccupancyDetails.currentItem = fragmentId

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
        tabDayWise.title = "Day-Wise Occupancy"
        tabsList.add(tabDayWise)

        val tabService = Tabs()
        tabService.title = getString(R.string.service)
        tabsList.add(tabService)

        val tabBookingSource = Tabs()
        tabBookingSource.title = getString(R.string.booking_source_chart)
        tabsList.add(tabBookingSource)

        val tabSeatStatus = Tabs()
        tabSeatStatus.title = getString(R.string.seat_status)
        tabsList.add(tabSeatStatus)


        val fragmentAdapter =
            DashboardOccupancyDetailsPagerAdapter(this, tabsList, supportFragmentManager)
        binding.viewPagerOccupancyDetails.adapter = fragmentAdapter
        binding.tabsOccupancyDetails.setupWithViewPager(binding.viewPagerOccupancyDetails)
        // custom tabs
        for (i in 0..binding.tabsOccupancyDetails.tabCount.minus(1)) {
            val tab = binding.tabsOccupancyDetails.getTabAt(i)!!
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

        binding.tabsOccupancyDetails.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPagerOccupancyDetails.currentItem = tab!!.position

                val text: TextView = tab.customView as TextView
                text.setTypeface(null, Typeface.BOLD)
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
        date = PreferenceUtils.getDashboardCurrentDate()
    }

    private fun getParentIntent() {

        if (intent.hasExtra(getString(R.string.dashboardGraphFromDate))) {
            date = intent.getStringExtra(getString(R.string.dashboardGraphFromDate)) ?: date
        }
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
       /* dashboardViewModel.occupancyCalendarApi(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            occupancyCalendarRequest,
            occupancy_calendar_method_name
        )*/

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
                            this@DashboardOccupancyDetailsActivity,
                            occupancyCalendarList, this@DashboardOccupancyDetailsActivity
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