package com.bitla.ts.phase2.dashboardContainer.activity

import android.app.*
import android.graphics.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.phase2.adapter.parent.*
import com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.request.*
import com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response.Result
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.tabs.*
import gone
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.*
import toast
import java.time.*
import java.util.*

class DashboardDetailsActivity : BaseActivity(), DialogSingleButtonListener,
    OnItemClickListener {

    private lateinit var binding: ActivityDashboardDetailsBinding
    private var tabsList: MutableList<Tabs> = mutableListOf()
    private var occupancyCalendarList = arrayListOf<Result>()
    var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var date: String = ""


    override fun initUI() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            WindowCompat.setDecorFitsSystemWindows(window, false) // Enables edge-to-edge
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val ime = insets.getInsets(WindowInsetsCompat.Type.ime()) // 👈 keyboard

                val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

                view.setPadding(
                    systemBars.left,
                    systemBars.top, // status bar handled visually
                    systemBars.right,
                    if (isKeyboardVisible) ime.bottom else systemBars.bottom
                )
                insets
            }

        }

        getPref()


//        binding.layoutToolbar.toolbarHeaderText.text = getString(R.string.occupancy)
        binding.layoutToolbar.toolbarHeaderText.text = getString(R.string.dashboard)
        binding.layoutToolbar.imgToolbarSearch.gone()

        val fragmentId = intent.getIntExtra(getString(R.string.fragmentTabPosition), 0)

        binding.layoutToolbar.tvCurrentHeader.text = inputFormatToOutput(
            date,
            DATE_FORMAT_D_M_Y,
            DATE_FORMAT_D_M_YY
        )

        initTab()

        binding.viewPagerDashboard.currentItem = fragmentId

        setOccupancyCalendarList()

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


    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        date = getDateDMY(PreferenceUtils.getDashboardCurrentDate()).toString()


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

//        val fragmentAdapter = DashboardPagerAdapter(this, tabsList, this.supportFragmentManager)

        val fragmentAdapter = DashboardPagerAdapter(this, tabsList, this)
        binding.viewPagerDashboard.adapter = fragmentAdapter
        binding.viewPagerDashboard.setOffscreenPageLimit(1)
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

        binding.tabsDashboard.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPagerDashboard.currentItem = tab!!.position

                val text: TextView = tab.customView as TextView
                text.setTypeface(null, Typeface.BOLD)
//                binding.layoutToolbar.toolbarHeaderText.text = tab.text
                binding.layoutToolbar.toolbarHeaderText.text = getString(R.string.dashboard)

//                removeDashboardFilterPref()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text: TextView = tab?.customView as TextView
                text.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


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
                            this@DashboardDetailsActivity,
                            occupancyCalendarList, this@DashboardDetailsActivity,
                            this@DashboardDetailsActivity
                        )
                    }

                    else -> {
                        toast("$it")
                    }
                }
            }
        }

    }

    private fun setOccupancyCalendarList() {

    }

    override fun onSingleButtonClick(str: String) {
//        toast("$str")
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {}

    override fun onClickOfItem(data: String, position: Int) {

        val currentMonth = YearMonth.now()
        val startYYMMDD = "${data}-01"
        val endYYMMDD = "${currentMonth.atEndOfMonth()}"
        toast("$position")
//      callOccupancyCalendarApi(startYYMMDD, endYYMMDD)
    }

    override fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        busData: com.bitla.ts.domain.pojo.available_routes.Result
    ) {
    }

    private fun removeDashboardFilterPref() {
        PreferenceUtils.apply {

            removeKey(PREF_SELECTED_SERVICE_ID_FILTER)
            removeKey(PREF_SELECTED_SERVICE_FILTER)
            removeKey(PREF_SLIDER_FROM_VALUE)
            removeKey(PREF_SLIDER_TO_VALUE)


//            removeKey( "allotted_services_model_dashboard")
//            removeKey( "allotted_services_model_serviceWise")
//            removeKey( "allotted_services_model_pendingQuota")
//            removeKey( "allotted_services_model_phoneBlocked")
        }
    }

}