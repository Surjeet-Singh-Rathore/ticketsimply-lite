package com.bitla.ts.phase2.fragments.mainFragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.format_type
import com.bitla.ts.data.occupancy_details
import com.bitla.ts.databinding.FragmentOccupancyBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.phase2.chartUtils.ReportValue
import com.bitla.ts.phase2.dashboardContainer.activity.DashboardDetailsActivity
import com.bitla.ts.phase2.dashboardContainer.activity.DashboardOccupancyDetailsActivity
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.request.OccupancyDetailsRequest
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.request.ReqBody
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.response.*
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.constants.SortBtn.SERVICE_WISE_COLLECTION_REVENUE_SORT
import com.bitla.ts.utils.constants.ViewDetails.DAY_WISE_OCCUPANCY
import com.bitla.ts.utils.constants.ViewDetails.OCCUPANCY_BY_SOURCE
import com.bitla.ts.utils.constants.ViewDetails.SERVICE_WISE_OCCUPANCY
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION
import com.bitla.ts.utils.sharedPref.PREF_LOCALE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.math.roundToInt

class OccupancyFragment : BaseFragment() {

    private lateinit var binding: FragmentOccupancyBinding
    private var daysWiseOccupancyList = ArrayList<ReportValue>()
    private var serviceOccupancyList = ArrayList<ReportValue>()
    private var occupancyByBookingSourceList = ArrayList<ReportValue>()
    private var occupancyBySeatStatusList = ArrayList<ReportValue>()
    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resId: String = ""
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private val dashboardViewModelDayWise by viewModel<DashboardViewModel<Any?>>()
    private var fromDate = ""
    private var toDate = ""
    private var currentDate = ""
    private var defaultSelection = 1
    private var isBeforeFromDateSelection: Boolean = true
    private var isAfterToDateSelection: Boolean = true
    private var isAfterFromDateSelection: Boolean = true
    private var hideYesterdayDateFilter: Boolean = false
    private var hideTodayDateFilter: Boolean = false
    private var hideTomorrowDateFilter: Boolean = false
    private var hideLast7DaysDateFilter: Boolean = true
    private var hideLast30DaysDateFilter: Boolean = true
    private var hideCustomDateFilter: Boolean = false
    private var hideCustomDateRangeFilter: Boolean = true
    private var isCustomDateFilterSelected: Boolean = false
    private var isCustomDateRangeFilterSelected: Boolean = false
    private var isLocalFilter = false
    private var allottedServicesResponseModel: AllotedServicesResponseModel? = null
    private var serviceId = "-1"
    private var totalOverallServices = 0
    private var selectedServiceName = "All Services"
    private var dashboardServiceFilterConf = DashboardServiceFilterConf()
    private var locale: String? = ""
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var allotedServicesResponseModel: AllotedServicesResponseModel? = null
    private var serviceSize: Int? = 0
    private var currentTabPosition: Int? = 0

    override fun onResume() {
        super.onResume()
        currentTabPosition =
            (activity as? DashboardDetailsActivity)?.findViewById<ViewPager2>(R.id.viewPagerDashboard)?.currentItem
        if (currentTabPosition == 0) {
            if (isAttachedToActivity()) {
                callOccupancyDetailsApi()
                setUpObserver()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentOccupancyBinding.inflate(inflater, container, false)
        val view: View = binding.root
        binding.apply {

            tvViewDetailsOccupancy.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent = Intent(requireContext(), DashboardOccupancyDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 0)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
                intent.putExtra(getString(R.string.dashboardGraphCurrentDate), currentDate)
                intent.putExtra(getString(R.string.dashboardGraphFromDate), fromDate)
                intent.putExtra(getString(R.string.dashboardGraphToDate), toDate)
                intent.putExtra(getString(R.string.dashboardGraphResId), resId)
                intent.putExtra(
                    getString(R.string.dashboardGraphServiceFiter),
                    jsonToString(allottedServicesResponseModel!!)
                )
                intent.putExtra(
                    getString(R.string.dashboard_service_filter_conf),
                    jsonToString(dashboardServiceFilterConf)
                )
                intent.putExtra(
                    getString(R.string.dashboard_total_overall_services),
                    totalOverallServices
                )

                startActivity(intent)

                firebaseLogEvent(
                    requireContext(),
                    VIEW_DETAILS,
                    PreferenceUtils.getLogin().userName,
                    PreferenceUtils.getLogin().travels_name,
                    PreferenceUtils.getLogin().role,
                    VIEW_DETAILS,
                    DAY_WISE_OCCUPANCY
                )

            }

            tvViewDetailsServiceOccupancy.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent = Intent(requireContext(), DashboardOccupancyDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 1)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
                intent.putExtra(getString(R.string.dashboardGraphCurrentDate), currentDate)
                intent.putExtra(getString(R.string.dashboardGraphFromDate), fromDate)
                intent.putExtra(getString(R.string.dashboardGraphToDate), toDate)
                intent.putExtra(getString(R.string.dashboardGraphResId), resId)
                intent.putExtra(
                    getString(R.string.dashboardGraphServiceFiter),
                    jsonToString(allottedServicesResponseModel!!)
                )
                intent.putExtra(
                    getString(R.string.dashboard_service_filter_conf),
                    jsonToString(dashboardServiceFilterConf)
                )
                intent.putExtra(
                    getString(R.string.dashboard_total_overall_services),
                    totalOverallServices
                )
                startActivity(intent)

                firebaseLogEvent(
                    requireContext(),
                    VIEW_DETAILS,
                    PreferenceUtils.getLogin().userName,
                    PreferenceUtils.getLogin().travels_name,
                    PreferenceUtils.getLogin().role,
                    VIEW_DETAILS,
                    SERVICE_WISE_OCCUPANCY
                )
            }

            tvViewDetailsBookingService.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent = Intent(requireContext(), DashboardOccupancyDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 2)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
                intent.putExtra(getString(R.string.dashboardGraphCurrentDate), currentDate)
                intent.putExtra(getString(R.string.dashboardGraphFromDate), fromDate)
                intent.putExtra(getString(R.string.dashboardGraphToDate), toDate)
                intent.putExtra(getString(R.string.dashboardGraphResId), resId)
                intent.putExtra(
                    getString(R.string.dashboardGraphServiceFiter),
                    jsonToString(allottedServicesResponseModel!!)
                )
                intent.putExtra(
                    getString(R.string.dashboard_service_filter_conf),
                    jsonToString(dashboardServiceFilterConf)
                )
                intent.putExtra(
                    getString(R.string.dashboard_total_overall_services),
                    totalOverallServices
                )

                startActivity(intent)

                firebaseLogEvent(
                    requireContext(),
                    VIEW_DETAILS,
                    PreferenceUtils.getLogin().userName,
                    PreferenceUtils.getLogin().travels_name,
                    PreferenceUtils.getLogin().role,
                    VIEW_DETAILS,
                    OCCUPANCY_BY_SOURCE
                )
            }
            tvViewDetailsSeatStatus.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent = Intent(requireContext(), DashboardOccupancyDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 3)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
                intent.putExtra(getString(R.string.dashboardGraphCurrentDate), currentDate)
                intent.putExtra(getString(R.string.dashboardGraphFromDate), fromDate)
                intent.putExtra(getString(R.string.dashboardGraphToDate), toDate)
                intent.putExtra(getString(R.string.dashboardGraphResId), resId)
                intent.putExtra(
                    getString(R.string.dashboardGraphServiceFiter),
                    jsonToString(allottedServicesResponseModel!!)
                )
                intent.putExtra(
                    getString(R.string.dashboard_service_filter_conf),
                    jsonToString(dashboardServiceFilterConf)
                )
                intent.putExtra(
                    getString(R.string.dashboard_total_overall_services),
                    totalOverallServices
                )

                startActivity(intent)

                firebaseLogEvent(
                    requireContext(),
                    VIEW_DETAILS,
                    PreferenceUtils.getLogin().userName,
                    PreferenceUtils.getLogin().travels_name,
                    PreferenceUtils.getLogin().role,
                    VIEW_DETAILS,
                    SERVICE_WISE_COLLECTION_REVENUE_SORT
                )
            }

            allServicesAndDateFilterContainer.tvDate.setOnClickListener {

                DialogUtils.dialogDateFilter(
                    context = requireContext(),
                    defaultSelection = defaultSelection,
                    todayDate = getDateYMD(getTodayDate()),
                    fromDate = fromDate,
                    toDate = toDate,
                    isBeforeFromDateSelection = isBeforeFromDateSelection,
                    isAfterToDateSelection = isAfterToDateSelection,
                    isAfterFromDateSelection = isAfterFromDateSelection,
                    hideYesterdayDateFilter = hideYesterdayDateFilter,
                    hideTodayDateFilter = hideTodayDateFilter,
                    hideTomorrowDateFilter = hideTomorrowDateFilter,
                    hideLast7DaysDateFilter = hideLast7DaysDateFilter,
                    hideLast30DaysDateFilter = hideLast30DaysDateFilter,
                    hideCustomDateFilter = hideCustomDateFilter,
                    hideCustomDateRangeFilter = hideCustomDateRangeFilter,
                    isCustomDateFilterSelected = isCustomDateFilterSelected,
                    isCustomDateRangeFilterSelected = isCustomDateRangeFilterSelected,
                    fragmentManager = (context as AppCompatActivity).supportFragmentManager,
                    tag = "",
                    onApply = { finalFromDate, finalToDate, lastSelectedItem, isCustomDateFilter, isCustomDateRangeFilter ->
                        if (finalFromDate != null) {
                            fromDate = finalFromDate
                            toDate = finalToDate ?: fromDate
                            defaultSelection = lastSelectedItem

                            isCustomDateFilterSelected = isCustomDateFilter
                            isCustomDateRangeFilterSelected = isCustomDateRangeFilter

                            isLocalFilter = true
                            callOccupancyDetailsApi()
                            //callOccupancyDaysWiseDetailsApi()

                            dashboardServiceFilterConf.fromId = ""
                            dashboardServiceFilterConf.toId = ""
                            dashboardServiceFilterConf.fromTitle = getString(R.string.all_cities)
                            dashboardServiceFilterConf.toTitle = getString(R.string.all_cities)
                            dashboardServiceFilterConf.hubTitle = ""
                            dashboardServiceFilterConf.hubId = ""
                            dashboardServiceFilterConf.isHub = false

                            callAllottedServiceApi(fromDate, toDate)

                        }
                    }
                )
            }

            allServicesAndDateFilterContainer.tvAllService.setOnClickListener {

                val intent: Intent = Intent(
                    requireContext(),
                    SearchServiceActivity::class.java
                )
                intent.putExtra(
                    getString(R.string.dashboardGraphServiceFiter),
                    jsonToString(allottedServicesResponseModel!!)
                )
                intent.putExtra(
                    getString(R.string.dashboard_service_filter_conf),
                    jsonToString(dashboardServiceFilterConf)
                )
                intent.putExtra(getString(R.string.from_date),fromDate)
                intent.putExtra(getString(R.string.to_date),toDate)
                startActivityForResult(intent, SELECT_SERVICE_INTENT_REQUEST_CODE)

            }

        }

        setAllottedDetailObserver()

        firebaseLogEvent(
        requireContext(),
        OCCUPANCY ,
        loginModelPref.userName,
        loginModelPref.travels_name,
        loginModelPref.role,
        OCCUPANCY ,
            Occupancy.OCCUPANCY
        )

        return view
    }

    override fun isInternetOnCallApisAndInitUI() {
       //callOccupancyDaysWiseDetailsApi()
        if(isAttachedToActivity()){
//            callOccupancyDetailsApi()
        }
    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPref()
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            dashboardViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        setAllServices()

        if (defaultSelection == 5) {
            isCustomDateFilterSelected = true
        }

        if (defaultSelection == 6) {
            isCustomDateRangeFilterSelected = true
        }

        //callOccupancyDetailsApi()
        //callOccupancyDaysWiseDetailsApi()
        setUpObserver()
        //setUpDayWiseObserver()
        // setNetworkConnectionObserver(view)

        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            binding.swipeRefreshLayoutDashboard.isRefreshing = true
            daysWiseOccupancyList.clear()
            serviceOccupancyList.clear()
            occupancyByBookingSourceList.clear()
            occupancyBySeatStatusList.clear()
            callOccupancyDetailsApi()
            //callOccupancyDaysWiseDetailsApi()

        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    private fun setOccupancyBySeatStatusGraphData(occupancyBySeatStatus: OccupancyBySeatStatus) {

        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            dragDecelerationFrictionCoef = 0.0f
            isDrawHoleEnabled = true
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(10)
            isRotationEnabled = true
            rotationAngle = 0f
            isHighlightPerTapEnabled = true
            animateY(1000, Easing.EaseInOutQuad)
            legend.isEnabled = false
            holeRadius = 0f
        }

        //binding.pieChart.getLegend().setWordWrapEnabled(true)
//        binding.pieChart.isDrawHoleEnabled = true
//        chart.setHoleColorTransparent(false)
//        binding.pieChart.transparentCircleRadius = 50f
//        binding.pieChart.setDrawCenterText(true)
//        mChart.setUnit(" €")
//        binding.pieChart.setDrawEntryLabels(true)
//        binding.pieChart.setUsePercentValues(true)

        val entries: ArrayList<PieEntry> = ArrayList()
        entries.apply {
            add(PieEntry(occupancyBySeatStatus.bookedSeats.replace(",", "").toFloat()))
            add(PieEntry(occupancyBySeatStatus.availableSeats.replace(",", "").toFloat()))
            add(PieEntry(occupancyBySeatStatus.cancelledSeats.replace(",", "").toFloat()))
            add(PieEntry(occupancyBySeatStatus.pendingSeats.replace(",", "").toFloat()))
//            add(PieEntry(occupancyBySeatStatus.totalSeats.replace(",","").toFloat()))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.sliceSpace = 5f
        dataSet.selectionShift = 4f
//        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
//        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val colors: ArrayList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(requireContext(), R.color.booked_tickets))
        colors.add(ContextCompat.getColor(requireContext(), R.color.pale_primary))
        colors.add(ContextCompat.getColor(requireContext(), R.color.blocked_tickets))
        colors.add(ContextCompat.getColor(requireContext(), R.color.orange))
        colors.add(ContextCompat.getColor(requireContext(), R.color.blue))
        dataSet.colors = colors

        dataSet.valueLinePart1OffsetPercentage = 100f
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pieChart))
        data.setValueTextSize(10f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.BLACK)
        binding.pieChart.data = data
        binding.pieChart.highlightValues(null)
        binding.pieChart.invalidate()
    }

    //    setChart
    private fun setOccupancyBarChart(dayWiseServiceOccupancy: List<DayWiseServiceOccupancy>) {
        daysWiseOccupancyList = getDayWiseOccupancyBarChartReportList(dayWiseServiceOccupancy)
        setDecimalFormatBarChart(requireContext(), binding.barChartOccupancy, daysWiseOccupancyList)
    }

    private fun setSeatStatusOccupancyBarChart(occupancyBySeatStatus: OccupancyDetailsResponse) {
        occupancyBySeatStatusList = getSeatStatusOccupancyBarChartReportList(occupancyBySeatStatus)
        setDecimalFormatBarChart(
            requireContext(),
            binding.seatStatusBarChart,
            occupancyBySeatStatusList,
            true
        )
    }

    private fun setServiceOccupancyBarChart(serviceOccupancy: List<ServiceOccupancy>) {
        serviceOccupancyList = getServiceOccupancyBarChartReportList(serviceOccupancy)
        setDecimalFormatBarChart(
            requireContext(),
            binding.barChartServiceOccupancy,
            serviceOccupancyList,
            true
        )
    }

    private fun setBookingSourceBarChart(occupancyByBookingSource: List<OccupancyByBookingSource>) {
        occupancyByBookingSourceList.clear()
        occupancyByBookingSourceList = getBookingSourceBarChartReportList(occupancyByBookingSource)
        setDecimalFormatBarChart(
            requireContext(),
            binding.barChartBookingSource,
            occupancyByBookingSourceList
        )
    }

    // simulate api call
    private fun getDayWiseOccupancyBarChartReportList(dayWiseServiceOccupancy: List<DayWiseServiceOccupancy>): ArrayList<ReportValue> {
        daysWiseOccupancyList.clear()

        dayWiseServiceOccupancy.forEachIndexed { i, element ->
            daysWiseOccupancyList.add(
                ReportValue(
                    getDateMMMDD(dayWiseServiceOccupancy[i].day).toString(),
                    dayWiseServiceOccupancy[i].occupancy.toDouble().toString(),
                    getDashboardChartColor(
                        requireContext(),
                        dayWiseServiceOccupancy[i].occupancy.toDouble().toInt()
                    ),
                    getDateMMMDD(dayWiseServiceOccupancy[i].day).toString(),
                    "${dayWiseServiceOccupancy[i].occupancy.toFloat()}%"
                )
            )

//            daysWiseOccupancyList.sortByDescending { it.name }
        }

        return daysWiseOccupancyList
    }

    private fun getServiceOccupancyBarChartReportList(serviceOccupancy: List<ServiceOccupancy>): ArrayList<ReportValue> {
        serviceOccupancyList.clear()

        serviceOccupancy.forEachIndexed { i, _ ->
            serviceOccupancyList.add(
                ReportValue(
                    serviceOccupancy[i].service.substring(
                        SUBSTRING_START, min(SUBSTRING_END, serviceOccupancy[i].service.length)
                    )
                        .plus(CUSTOM_ELLIPSIS),
                    serviceOccupancy[i].occupancy.toDouble().toString(),
                    getDashboardChartColor(
                        requireContext(),
                        serviceOccupancy[i].occupancy.toDouble().toInt()
                    ),
                    serviceOccupancy[i].service,
                    "${serviceOccupancy[i].occupancy.toFloat()}%"
                )
            )
        }
        return serviceOccupancyList
    }

    private fun getBookingSourceBarChartReportList(occupancyByBookingSource: List<OccupancyByBookingSource>): ArrayList<ReportValue> {
        occupancyByBookingSourceList.clear()

        occupancyByBookingSource.forEachIndexed { i, _ ->
//            if (DASHBOARD_CHART_LIMIT > occupancyByBookingSourceList.size) { }
            occupancyByBookingSourceList.add(
                ReportValue(
                    occupancyByBookingSource[i].bookingSource,
                    occupancyByBookingSource[i].occupancy.toDouble().toString(),
                    getDashboardChartColor(
                        requireContext(),
                        occupancyByBookingSource[i].occupancy.toFloat().roundToInt()
                    ),
                    occupancyByBookingSource[i].bookingSource,
                    "${occupancyByBookingSource[i].occupancy.toFloat()}%"
                )
            )
        }
        return occupancyByBookingSourceList
    }

    private fun getSeatStatusOccupancyBarChartReportList(occupancyBySeatStatus: OccupancyDetailsResponse): ArrayList<ReportValue> {
        val list = ArrayList<ReportValue>()
        occupancyBySeatStatus.result.occupancyBySeatStatus?.forEach {
            list.add(
                ReportValue(
                    getString(R.string.booked_seats),
                    it.bookedSeats,
                    requireContext().getColor(R.color.booked_tickets),
                    getString(R.string.booked_seats),
                    it.bookedSeats.toFloat().toString()
                )
            )
            list.add(
                ReportValue(
                    getString(R.string.available_seats),
                    it.availableSeats,
                    requireContext().getColor(R.color.pale_primary),
                    getString(R.string.available_seats),
                    it.availableSeats.toFloat().toString()
                )
            )
            list.add(
                ReportValue(
                    getString(R.string.cancel_seats),
                    it.cancelledSeats,
                    requireContext().getColor(R.color.blocked_tickets),
                    getString(R.string.cancel_seats),
                    it.cancelledSeats.toFloat().toString()
                )
            )
            list.add(
                ReportValue(
                    getString(R.string.pending_seats),
                    it.pendingSeats,
                    requireContext().getColor(R.color.orange),
                    getString(R.string.pending_seats),
                    "${it.pendingSeats.toFloat()}",
                )
            )
        }

        return list
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        allottedServicesResponseModel =
            PreferenceUtils.getObject<AllotedServicesResponseModel>("allotted_services_model_dashboard")

        totalOverallServices = PreferenceUtils.getPreference("total_overall_service_size", 0) ?: 0
        dashboardServiceFilterConf =
            PreferenceUtils.getObject<DashboardServiceFilterConf>(getString(R.string.dashboard_service_filter_conf))
                ?: DashboardServiceFilterConf()
        locale = PreferenceUtils.getString(PREF_LOCALE)

        defaultSelection = PreferenceUtils.getPreference(
            DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION, 1
        )?.toInt() ?: 1
        currentDate = PreferenceUtils.getDashboardCurrentDate()
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = PreferenceUtils.getDashboardToFutureDate()
        resId = PreferenceUtils.getPreference("dashboard_resId", "").toString()
    }


    private fun callOccupancyDetailsApi() {
        startShimmerEffect()
        if (requireActivity().isNetworkAvailable()) {

            if (defaultSelection == 3 || defaultSelection == 4 || defaultSelection == 6) {
                dashboardDateSetText(
                    textView = binding.allServicesAndDateFilterContainer.tvDate,
                    fromDate = fromDate,
                    toDate = toDate,
                    inputDateFormat = DATE_FORMAT_Y_M_D
                )
            } else {
                dashboardDateSetText(
                    textView = binding.allServicesAndDateFilterContainer.tvDate,
                    fromDate = fromDate,
                    toDate = null,
                    inputDateFormat = DATE_FORMAT_Y_M_D
                )
            }

            dashboardViewModel.getOccupancyDetails(
                apiKey = loginModelPref.api_key,
                originId = -1,
                destinationId = -1,
                from = fromDate,
                to = fromDate,
                sortBy = "htol",
                serviceId = serviceId,
                apiType = 0,
                is3DaysData = isLocalFilter.not(),
                locale = locale ?: "en",
                is_from_middle_tier = true,
                methodName = occupancy_details
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                occupancyContainer.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun callOccupancyDaysWiseDetailsApi() {
        startShimmerEffect()
        if (requireActivity().isNetworkAvailable()) {
            val reqBody =
                ReqBody(
                    apiKey = loginModelPref.api_key,
                    originId = -1,
                    destination = -1,
                    serviceId = serviceId,
                    from = fromDate,
                    to = toDate,
                    sortBy = "ltoh",
                    apiType = 1
                )

            if (getDaysDifference(fromDate, toDate, DATE_FORMAT_Y_M_D) < 2 && !isLocalFilter) {

                reqBody.from =
                    LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).minusDays(1).toString()
                reqBody.to =
                    LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).plusDays(1).toString()

            }


            val occupancyDetailsRequest =
                OccupancyDetailsRequest(
                    bccId = bccId.toString(),
                    methodName = occupancy_details,
                    format = format_type,
                    reqBody = reqBody
                )

            /* dashboardViewModelDayWise.occupancyDetailsDayWise(
                 authorization = loginModelPref.auth_token,
                 apiKey = loginModelPref.api_key,
                 occupancyDetailsRequest = occupancyDetailsRequest,
                 apiType = occupancy_details
             )*/
            dashboardViewModelDayWise.occupancyDetailsDayWise(
                occupancyDetailsRequest = reqBody,
                apiType = occupancy_details
            )
        }
    }


        private fun setUpObserver() {
            dashboardViewModel.occupancyDetailsResponseViewModel.observe(viewLifecycleOwner) {

                try {
                    binding.swipeRefreshLayoutDashboard.isRefreshing = false
                    stopShimmerEffect()

                if (it != null && it.code == 200) {
                    try {

                        if (it.result.dayWiseServiceOccupancy.isNullOrEmpty().not()) {
                            val newList: MutableList<DayWiseServiceOccupancy> = it.result.dayWiseServiceOccupancy?.toMutableList() ?: mutableListOf()
                            newList.sortBy { it.day }
                            setOccupancyBarChart(newList)
                            
                            binding.tvViewDetailsOccupancy.visible()
                            binding.barChartOccupancy.visible()
                            binding.view1.visible()
                        } else {
                            binding.view1.gone()
                            binding.tvViewDetailsOccupancy.gone()
                        }

                        if (it.result.serviceOccupancy?.isEmpty()?.not() == true) {
                            binding.tvViewDetailsServiceOccupancy.visible()
                            binding.view2.visible()
                            setServiceOccupancyBarChart(it.result.serviceOccupancy)
                        } else {
                            binding.barChartServiceOccupancy.clear()
                            binding.view2.gone()
                            binding.tvViewDetailsServiceOccupancy.gone()
                        }
                        
                        if (it.result.occupancyByBookingSource?.isEmpty()?.not() == true) {
                            binding.tvViewDetailsBookingService.visible()
                            binding.view3.visible()
                            setBookingSourceBarChart(it.result.occupancyByBookingSource)
                        } else {
                            binding.barChartBookingSource.clear()
                            binding.tvViewDetailsBookingService.gone()
                            binding.view3.gone()
                        }
                        if (it.result.occupancyBySeatStatus?.isEmpty()?.not() == true) {
                            if (it.result.occupancyBySeatStatus[0].bookedSeats != "0"
                                && it.result.occupancyBySeatStatus[0].availableSeats != "0"
                                && it.result.occupancyBySeatStatus[0].pendingSeats != "0"
                                && it.result.occupancyBySeatStatus[0].cancelledSeats != "0"
                            ) {
                                binding.tvViewDetailsSeatStatus.visible()
                                binding.viewSeatStatus.visible()
                                binding.tvTotalSeatOccupancySeatStatusCount.text =
                                    it.result.occupancyBySeatStatus[0].totalSeats
                                setOccupancyBySeatStatusGraphData(it.result.occupancyBySeatStatus[0])
                                setSeatStatusOccupancyBarChart(it)
                            } else {
                                binding.tvViewDetailsSeatStatus.gone()
                                binding.viewSeatStatus.gone()
                            }

                        } else {
                            binding.pieChart.clear()
                            binding.tvViewDetailsSeatStatus.gone()
                            binding.viewSeatStatus.gone()
                        }
                    } catch (e: Exception) {
                        requireActivity().toast(getString(R.string.opps))
                        Timber.d("Error in OccupancyDetailsObserver ${e.message}")
                    }
                } else {
                    if (it?.message != null) {
                        stopShimmerEffect()
                        binding.occupancyContainer.gone()
                        binding.noData.root.visible()
                        binding.noData.tvNoData.text = "${it.message}}"
                    } else {
                        stopShimmerEffect()
                        binding.occupancyContainer.gone()
                        binding.noData.root.visible()
                        binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
                    }
                }
            } catch (e: Exception) {
                Timber.d("Error in OccupancyDetailsObserver ${e.message}")
            }
        }
    }

    private fun setUpDayWiseObserver() {
        dashboardViewModelDayWise.occupancyDetailsResponseViewModelDayWise.observe(
            viewLifecycleOwner
        ) {
            try {
                binding.swipeRefreshLayoutDashboard.isRefreshing = false
                if (binding.shimmerOccupancy.isShimmerStarted) {
                    binding.shimmerOccupancy.stopShimmer()
                }

                if (it != null) {
                    if (it.code == 200) {
                        try {
                            if (it.result.dayWiseServiceOccupancy.isNullOrEmpty().not()) {
                                binding.tvViewDetailsOccupancy.visible()

                                val newList: MutableList<DayWiseServiceOccupancy> =
                                    it.result.dayWiseServiceOccupancy?.toMutableList()
                                        ?: mutableListOf()
                                newList.sortBy { it.day }
                                setOccupancyBarChart(newList)
                                binding.barChartOccupancy.visible()
                            } else {
                                binding.barChartOccupancy.clear()
                                binding.tvViewDetailsOccupancy.gone()
                                binding.view1.gone()
                            }
                        } catch (e: Exception) {
                            requireActivity().toast(getString(R.string.opps))
                            Timber.d("Error in OccupancyDetailsObserver ${e.message}")
                        }
                    } else {
                        if (it.message != null) {
                            stopShimmerEffect()
                            binding.occupancyContainer.gone()
                            binding.noData.root.visible()
                            binding.noData.tvNoData.text = "${it.message}}"
                        } else {
                            stopShimmerEffect()
                            binding.occupancyContainer.gone()
                            binding.noData.root.visible()
                            binding.noData.tvNoData.text =
                                requireActivity().getString(R.string.opps)
                        }
                    }
                } else {
                    binding.tvViewDetailsOccupancy.gone()
                }
            } catch (e: Exception) {
                Timber.d("Error in OccupancyDetailsObserver ${e.message}")
            }
        }
    }

    private fun startShimmerEffect() {
        binding.apply {
            shimmerOccupancy.visible()
            occupancyContainer.gone()
            noData.root.gone()
            shimmerOccupancy.startShimmer()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            shimmerOccupancy.gone()
            occupancyContainer.visible()
            if (shimmerOccupancy.isShimmerStarted) {
                shimmerOccupancy.stopShimmer()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if (data != null) {
        if (resultCode == SELECT_SERVICE_INTENT_REQUEST_CODE) {
            val allottedServiceString = data?.getStringExtra("allotted_service_response")
            if (allottedServiceString != null) {

                val allottedResponseModelString =
                    stringToJson<AllotedServicesResponseModel>(allottedServiceString)

                allottedServicesResponseModel = allottedResponseModelString

                if (data.hasExtra(getString(R.string.dashboard_service_filter_conf))) {
                    val dashboardServiceFilterConfString: String =
                        data.getStringExtra(getString(R.string.dashboard_service_filter_conf))
                            ?: ""
                    dashboardServiceFilterConf = stringToJson(dashboardServiceFilterConfString)
                }

                setAllServices()
            }
        }
        //}
    }

    private fun setAllServices() {

        var totalSelectedServices = 0

        allottedServicesResponseModel?.services?.forEach {
            if (it.isChecked)
                totalSelectedServices++
        }

        if (totalSelectedServices > 0 && totalSelectedServices < totalOverallServices) {
            serviceId = ""
            selectedServiceName = ""

            allottedServicesResponseModel?.services?.forEach {
                if (it.isChecked) {
                    serviceId += it.routeId.toString().replace(".0", "") + ","
                    selectedServiceName += it.number.toString() + ","
                }
            }

        } else {
            serviceId = "-1"
            selectedServiceName = "${getString(R.string.all_services_title_case)} ($totalOverallServices)"

        }

        if (serviceId == "-1" && totalSelectedServices == totalOverallServices) {

            selectedServiceName = "${getString(R.string.all_services_title_case)} (${totalOverallServices})"

        } else {
            selectedServiceName = "${getString(R.string.service)} ($totalSelectedServices)"
            try {
                if (serviceId.isNotEmpty()) {
                    serviceId = serviceId.substring(0, serviceId.lastIndexOf(","))
                }
            } catch (e: Exception) {
                requireActivity().toast(getString(R.string.something_went_wrong))
            }
        }
        binding.allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
//        callOccupancyDetailsApi()
        //callOccupancyDaysWiseDetailsApi()

    }


    private fun callAllottedServiceApi(from: String, toDate: String) {
        if (requireActivity().isNetworkAvailable()) {
            pickUpChartViewModel.getAllottedServicesWithDateChange(
                apiKey = loginModelPref.api_key,
                origin = "",
                destination = "",
                from = from,
                to = toDate,
                hubId = null,
                isGroupByHubs = false,
                viewMode = "report",
                locale = locale ?: "en",
                isFromMiddleTier = true,
                methodName = alloted_service_Dashboard_method
            )

        } else
            requireActivity().noNetworkToast()
    }

    private fun setAllottedDetailObserver() {
        pickUpChartViewModel.allotedDetailResponse.observe(viewLifecycleOwner) { it ->
            if (it?.services != null) {
                val services = it.services

                allottedServicesResponseModel = it

                serviceSize = it.services.size
                totalOverallServices = it.services.size
                binding.allServicesAndDateFilterContainer.tvAllService.text =
                    "${getString(R.string.all_services_title_case)} (${it.services.size})"

            }
        }
    }


}