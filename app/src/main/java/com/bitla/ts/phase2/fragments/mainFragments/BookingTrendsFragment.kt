package com.bitla.ts.phase2.fragments.mainFragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.booking_trends
import com.bitla.ts.data.format_type
import com.bitla.ts.data.revenue_details
import com.bitla.ts.databinding.FragmentPerformanceBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.adapter.child.PerformanceSummaryAdapter
import com.bitla.ts.phase2.chartUtils.ReportValue
import com.bitla.ts.phase2.dashboardContainer.activity.DashboardBookingTrendsDetailsActivity
import com.bitla.ts.phase2.dashboardContainer.activity.DashboardDetailsActivity
import com.bitla.ts.phase2.dashboard_pojo.PerformanceSummaryModel
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.request.BookingTrendsRequest
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.response.BestPerformanceDay
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.response.BranchProfitPerformance
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.response.ETicketsPerformance
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.response.PerformanceSource
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.constants.ViewDetails.BRANCH_WISE_BOOKING_TRENDS
import com.bitla.ts.utils.constants.ViewDetails.DAY_WISE_BOOKING_TRENDS
import com.bitla.ts.utils.constants.ViewDetails.E_BOOKING_TRENDS
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import visible
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.math.roundToInt

class BookingTrendsFragment : BaseFragment() {

    private lateinit var binding: FragmentPerformanceBinding
    private var eTicketPerformanceList = ArrayList<ReportValue>()
    private var branchProfitPerformanceDaysList = ArrayList<ReportValue>()
    private var bestPerformanceDaysList = ArrayList<ReportValue>()

    private lateinit var performanceSummaryAdapter: PerformanceSummaryAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var performanceSummaryList = arrayListOf<PerformanceSummaryModel>()
    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
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
    private var serviceSize: Int? = 0
    private var allotedServicesResponseModel: AllotedServicesResponseModel? = null
    private var journeyBy: String? = null
    private var privilegeResponse: PrivilegeResponseModel? = null
    private var currentTabPosition: Int? = 0

    override fun onResume() {
        super.onResume()
        currentTabPosition = (activity as? DashboardDetailsActivity)?.findViewById<ViewPager2>(R.id.viewPagerDashboard)?.currentItem
        if (currentTabPosition == 2) {
            if (isAttachedToActivity()) {
                callBookingTrendsDayWiseApi()
                callBookingTrendsApi()
            }
            setUpObserver()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPerformanceBinding.inflate(inflater, container, false)
        val view: View = binding.root

        getPref()

        lifecycleScope.launch {
            dashboardViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
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

        //callBookingTrendsApi()
        //callBookingTrendsDayWiseApi()
//        setUpObserver()
        //setUpDayWiseObserver()

        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            binding.swipeRefreshLayoutDashboard.isRefreshing = true
            performanceSummaryList.clear()
            eTicketPerformanceList.clear()
            branchProfitPerformanceDaysList.clear()
            bestPerformanceDaysList.clear()
            callBookingTrendsApi()
            //callBookingTrendsDayWiseApi()
        }

        binding.apply {

            tvViewDetailsServiceOccupancy.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent =
                    Intent(requireContext(), DashboardBookingTrendsDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 0)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
                intent.putExtra(getString(R.string.dashboardGraphCurrentDate), currentDate)
                intent.putExtra(getString(R.string.dashboardGraphFromDate), fromDate)
                intent.putExtra(getString(R.string.dashboardGraphToDate), toDate)
                intent.putExtra(
                    getString(R.string.dashboardGraphServiceFiter),
                    jsonToString(allottedServicesResponseModel!!)
                )
                intent.putExtra(
                    getString(R.string.dashboard_service_filter_conf),
                    jsonToString(dashboardServiceFilterConf)
                )
                //intent.putExtra(getString(R.string.dashboardGraphResId), resId)
                intent.putExtra(
                    getString(R.string.dashboard_total_overall_services),
                    totalOverallServices
                )

                intent.putExtra(
                    getString(R.string.booking_trends_journey_by_filter),
                    journeyBy
                )

                startActivity(intent)

                firebaseLogEvent(
                    requireContext(),
                    VIEW_DETAILS,
                    PreferenceUtils.getLogin().userName,
                    PreferenceUtils.getLogin().travels_name,
                    PreferenceUtils.getLogin().role,
                    VIEW_DETAILS,
                    E_BOOKING_TRENDS
                )
            }


            tvViewDetailsBookingService.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent =
                    Intent(requireContext(), DashboardBookingTrendsDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 1)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
                intent.putExtra(getString(R.string.dashboardGraphCurrentDate), currentDate)
                intent.putExtra(getString(R.string.dashboardGraphFromDate), fromDate)
                intent.putExtra(getString(R.string.dashboardGraphToDate), toDate)
                intent.putExtra(
                    getString(R.string.dashboardGraphServiceFiter),
                    jsonToString(allottedServicesResponseModel!!)
                )
                intent.putExtra(
                    getString(R.string.dashboard_service_filter_conf),
                    jsonToString(dashboardServiceFilterConf)
                )
                //intent.putExtra(getString(R.string.dashboardGraphResId), resId)
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
                    BRANCH_WISE_BOOKING_TRENDS
                )
            }

            tvViewDetailsSeatStatus.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent =
                    Intent(requireContext(), DashboardBookingTrendsDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 2)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
                intent.putExtra(getString(R.string.dashboardGraphCurrentDate), currentDate)
                intent.putExtra(getString(R.string.dashboardGraphFromDate), fromDate)
                intent.putExtra(getString(R.string.dashboardGraphToDate), toDate)
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
                //intent.putExtra(getString(R.string.dashboardGraphResId), resId)

                startActivity(intent)

                firebaseLogEvent(
                    requireContext(),
                    VIEW_DETAILS,
                    PreferenceUtils.getLogin().userName,
                    PreferenceUtils.getLogin().travels_name,
                    PreferenceUtils.getLogin().role,
                    VIEW_DETAILS,
                    DAY_WISE_BOOKING_TRENDS
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
                            callBookingTrendsApi()
                            //callBookingTrendsDayWiseApi()

                            dashboardServiceFilterConf.fromId = ""
                            dashboardServiceFilterConf.toId = ""
                            dashboardServiceFilterConf.fromTitle =
                                "${getString(R.string.all_cities)}"
                            dashboardServiceFilterConf.toTitle = "${getString(R.string.all_cities)}"
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

                intent.putExtra(getString(R.string.from_date), fromDate)
                intent.putExtra(getString(R.string.to_date), toDate)
                startActivityForResult(intent, SELECT_SERVICE_INTENT_REQUEST_CODE)

            }

            filterLayout.btnFilter.setOnClickListener {
                DialogUtils.dialogJourneyByDashboard(
                    context = requireContext(),
                    journeyBy = journeyBy,
                    dialogFilterByListener = {
                        journeyBy = it
                        callBookingTrendsApi()
                        callBookingTrendsDayWiseApi()
                        binding.filterLayout.filterMarker.visible()
                    }
                )
            }
        }
        setAllottedDetailObserver()

        firebaseLogEvent(
            requireContext(),
            BOOKING_TRENDS,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            BOOKING_TRENDS,
            BookingTrends.BOOKING_TRENDS
        )

        return view
    }

    override fun isInternetOnCallApisAndInitUI() {

        if (isAttachedToActivity()) {
//            callBookingTrendsDayWiseApi()
//            callBookingTrendsApi()
        }
    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    private fun setPerformanceSummaryAdapter(performanceSource: List<PerformanceSource>) {
        performanceSummaryList = getPerformanceSummaryList(performanceSource)

        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvPerformanceSummary.layoutManager = layoutManager
        performanceSummaryAdapter =
            PerformanceSummaryAdapter(requireActivity(), performanceSummaryList,privilegeResponse)
        binding.rvPerformanceSummary.adapter = performanceSummaryAdapter
    }

    //    setChart
    private fun setETicketPerformanceBarChart(eTicketsPerformance: List<ETicketsPerformance>) {
        eTicketPerformanceList = getETicketPerformanceList(eTicketsPerformance)
//        Timber.d("daywiseObserver::${eTicketPerformanceList[2]} ")

        /*
                if (eTicketPerformanceList.size==2){
                    Timber.d("daywiseObserver::${eTicketPerformanceList} ")

                    if (getDateMMMDD(toDate) != eTicketPerformanceList[1].name
                        || getDateMMMDD(toDate) != eTicketPerformanceList[2].name){

                        eTicketPerformanceList.add(
                            ReportValue(
                                getDateMMMDD(toDate).toString(),
                                "${0}",
                                getDashboardChartColor(
                                    requireContext(),
                                    0.toFloat().roundToInt()
                                ),
                                getDateMMMDD(toDate).toString(),
                                "${0}%"
                            )
                        )
                        eTicketPerformanceList.sortByDescending { it.name }

                    }
                    else if (getDateMMMDD(fromDate) != eTicketPerformanceList[1].name
                        || getDateMMMDD(fromDate) != eTicketPerformanceList[2].name){

                        eTicketPerformanceList.add(
                            ReportValue(
                                getDateMMMDD(fromDate).toString(),
                                "${0}",
                                getDashboardChartColor(
                                    requireContext(),
                                    0.toFloat().roundToInt()
                                ),
                                getDateMMMDD(fromDate).toString(),
                                "${0}%"
                            )
                        )
                        eTicketPerformanceList.sortByDescending { it.name }

                    }
                    else if (getDateMMMDD(currentDate) != eTicketPerformanceList[1].name
                        || getDateMMMDD(currentDate) != eTicketPerformanceList[2].name){

                        eTicketPerformanceList.add(
                            ReportValue(
                                getDateMMMDD(currentDate).toString(),
                                "${0}",
                                getDashboardChartColor(
                                    requireContext(),
                                    0.toFloat().roundToInt()
                                ),
                                getDateMMMDD(currentDate).toString(),
                                "${0}%"
                            )
                        )
                        eTicketPerformanceList.sortByDescending { it.name }

                    }
                }
        */

        setDecimalFormatBarChart(
            requireContext(),
            binding.barChartETicketPerformance,
            eTicketPerformanceList
        )
        binding.barChartETicketPerformance.visible()
    }

    private fun setBookingSourceBarChart(branchProfitPerformance: List<BranchProfitPerformance>) {
        branchProfitPerformanceDaysList =
            getBookingSourceBarChartReportList(branchProfitPerformance)
        setDecimalFormatBarChart(
            requireContext(),
            binding.barChartBookingSource,
            branchProfitPerformanceDaysList,
            true
        )
    }

    private fun bestPerformanceDayChart(bestPerformanceDay: List<BestPerformanceDay>) {
        bestPerformanceDaysList = getBestPerformanceDayChartReportList(bestPerformanceDay)
        setDecimalFormatBarChart(
            requireContext(),
            binding.barChartSeatStatus,
            bestPerformanceDaysList
        )

    }

    // simulate api call
    private fun getETicketPerformanceList(eTicketsPerformance: List<ETicketsPerformance>): ArrayList<ReportValue> {
        eTicketPerformanceList.clear()
        Timber.d("daywiseObserver::1${eTicketsPerformance} ")

        eTicketsPerformance.forEachIndexed { i, _ ->
//            if (DASHBOARD_CHART_LIMIT > eTicketPerformanceList.size) { }
            eTicketPerformanceList.add(
                ReportValue(
                    getDateMMMDD(eTicketsPerformance[i].eTicket).toString(),
                    eTicketsPerformance[i].performance.toString(),
                    getDashboardChartColor(
                        requireContext(),
                        eTicketsPerformance[i].performance.toFloat().roundToInt()
                    ),
                    getDateMMMDD(eTicketsPerformance[i].eTicket).toString(),
                    "${eTicketsPerformance[i].performance.toFloat()}%"
                )
            )
        }
        return eTicketPerformanceList
    }

    private fun getPerformanceSummaryList(performanceSource: List<PerformanceSource>): ArrayList<PerformanceSummaryModel> {

        performanceSummaryList.clear()
        performanceSource.forEach {
            performanceSummaryList.add(
                PerformanceSummaryModel(
                    it.source,
                    it.seatsSold.toString(),
                    it.netRevenue.toString(),
                    it.grossRevenue.toString()
                )
            )
        }
        return performanceSummaryList
    }


    private fun getBookingSourceBarChartReportList(branchProfitPerformance: List<BranchProfitPerformance>): ArrayList<ReportValue> {
        branchProfitPerformanceDaysList.clear()

        branchProfitPerformance.forEachIndexed { i, _ ->
//            if (DASHBOARD_CHART_LIMIT > branchProfitPerformanceDaysList.size) { }
            branchProfitPerformanceDaysList.add(
                ReportValue(
                    branchProfitPerformance[i].branch.substring(
                        SUBSTRING_START,
                        min(SUBSTRING_END, branchProfitPerformance[i].branch.length)
                    )
                        .plus(CUSTOM_ELLIPSIS),
                    branchProfitPerformance[i].performance.toDouble().toString(),
                    getDashboardChartColor(
                        requireContext(),
                        branchProfitPerformance[i].performance.toFloat().roundToInt()
                    ),
                    branchProfitPerformance[i].branch,
                    "${branchProfitPerformance[i].performance.toFloat()}%"
                )
            )
        }
        return branchProfitPerformanceDaysList
    }

    private fun getBestPerformanceDayChartReportList(bestPerformanceDay: List<BestPerformanceDay>): ArrayList<ReportValue> {

        bestPerformanceDaysList.clear()

        bestPerformanceDay.forEachIndexed { i, _ ->
//            if (DASHBOARD_CHART_LIMIT > bestPerformanceDaysList.size) { }
            bestPerformanceDaysList.add(
                ReportValue(
                    bestPerformanceDay[i].service.substring(
                        SUBSTRING_START, min(SUBSTRING_END, bestPerformanceDay[i].service.length)
                    )
                        .plus(CUSTOM_ELLIPSIS),
                    bestPerformanceDay[i].performance.toDouble().roundToInt().toString(),
                    getDashboardChartColor(
                        requireContext(),
                        bestPerformanceDay[i].performance.toFloat().roundToInt()
                    ),
                    bestPerformanceDay[i].service,
                    "${bestPerformanceDay[i].performance.toFloat()}%"
                )
            )
        }
        return bestPerformanceDaysList
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        allottedServicesResponseModel =
            PreferenceUtils.getObject<AllotedServicesResponseModel>("allotted_services_model_dashboard")
        locale = PreferenceUtils.getlang()

        totalOverallServices = PreferenceUtils.getPreference("total_overall_service_size", 0) ?: 0
        dashboardServiceFilterConf =
            PreferenceUtils.getObject<DashboardServiceFilterConf>(getString(R.string.dashboard_service_filter_conf))
                ?: DashboardServiceFilterConf()

        defaultSelection = PreferenceUtils.getPreference(
            DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION, 1
        )?.toInt() ?: 1
        currentDate = PreferenceUtils.getDashboardCurrentDate()
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = fromDate
        privilegeResponse = (activity as BaseActivity).getPrivilegeBase()


    }

    private fun callBookingTrendsDayWiseApi() {
        startShimmerEffect()
        if (requireActivity().isNetworkAvailable()) {
            val reqBody =
                com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.request.ReqBody(
                    apiKey = loginModelPref.api_key,
                    originId = -1,
                    destinationId = -1,
                    serviceId = serviceId,
                    branchId = "-1",
                    from = fromDate,
                    to = toDate,
                    sortBy = "htol",
                    apiType = 0
                )

            if (getDaysDifference(fromDate, toDate, DATE_FORMAT_Y_M_D) < 2 && !isLocalFilter) {

                reqBody.from =
                    LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).minusDays(1).toString()
                reqBody.to =
                    LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).plusDays(1).toString()
                toDate = reqBody.to
            }

            val bookingTrendsRequest =
                BookingTrendsRequest(
                    bccId = bccId.toString(),
                    methodName = booking_trends,
                    format = format_type,
                    reqBody = reqBody
                )

            dashboardViewModelDayWise.bookingTrendsDetailsDayWiseApi(
                bookingTrendsRequest = reqBody,
                apiType = revenue_details
            )
        }
    }

    /*private fun callBookingTrendsApi() {
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

            val reqBody =
                com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.request.ReqBody(
                    apiKey = loginModelPref.api_key,
                    originId = -1,
                    destinationId = -1,
                    serviceId = serviceId,
                    branchId = "-1",
                    from = fromDate,
                    to = toDate,
                    sortBy = "htol",
                    apiType = 0,
                    is3DaysData = true
                )

*//*            if(getDaysDifference(fromDate, toDate, DATE_FORMAT_Y_M_D) < 2 && !isLocalFilter) {

                reqBody.from = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).minusDays(1).toString()
                reqBody.to = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).plusDays(1).toString()

            }*//*

            if (isLocalFilter) {
                reqBody.is3DaysData = false
            }

            val bookingTrendsRequest =
                BookingTrendsRequest(
                    bccId = bccId.toString(),
                    methodName = booking_trends,
                    format = format_type,
                    reqBody = reqBody
                )

            dashboardViewModel.bookingTrendsDetailsApi(
                bookingTrendsRequest = reqBody,
                apiType = revenue_details
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                container1Performance.gone()
                dashboard2Container.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }
*/

    private fun callBookingTrendsApi() {
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
            dashboardViewModel.performanceDetailsApi(
                apiKey = loginModelPref.api_key,
                originId = -1,
                destinationId = -1,
                from = fromDate,
                to = toDate,
                sortBy = "htol",
                serviceId = serviceId,
                branchId = "-1",
                apiType = 0,
                is3DaysData = isLocalFilter.not(),
                locale = locale ?: "en",
                is_from_middle_tier = true,
                journeyBy = journeyBy,
                methodName = booking_trends
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                container1Performance.gone()
                dashboard2Container.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun setUpObserver() {
        dashboardViewModel.bookingTrendsResponseViewModel.observe(viewLifecycleOwner) {
            stopShimmerEffect()
            try {
                binding.swipeRefreshLayoutDashboard.isRefreshing = false

                if (it != null && it.code == 200) {

                    if (it.result.eTicketsPerformance.isNullOrEmpty().not()) {
                        binding.tvViewDetailsServiceOccupancy.visible()
                        binding.view1.visible()

                        val newList: MutableList<ETicketsPerformance> =
                            it.result.eTicketsPerformance?.toMutableList() ?: mutableListOf()
                        newList.sortBy { it.eTicket }

                        setETicketPerformanceBarChart(newList)
                    } else {
                        binding.tvViewDetailsServiceOccupancy.gone()
                        binding.view1.gone()
                    }

                    setPerformanceSummaryAdapter(it.result.performanceSummary?.get(0)?.performanceSource!!)

                    if (it.result.branchProfitPerformance?.isEmpty() == true) {
                        binding.tvViewDetailsBookingService.gone()
                        binding.view3.gone()
                    } else {
                        binding.tvViewDetailsBookingService.visible()
                        binding.view3.visible()
                        setBookingSourceBarChart(it.result.branchProfitPerformance!!)
                    }

                    /*if (it.result.bestPerformanceDays?.isNotEmpty() == true) {
                        binding.tvViewDetailsSeatStatus.visible()
                        binding.view4.visible()
                        bestPerformanceDayChart(it.result.bestPerformanceDays!!)
                    } else {
                        binding.tvViewDetailsSeatStatus.gone()
                        binding.view4.gone()
                    }*/

                    var newGrossRev = it.result.performanceSummary[0].totalGrossRevenue.toString()
                        .replace(privilegeResponse?.currency ?: "", "")
                    var newNetRev =
                        it.result.performanceSummary[0].totalNetRevenue.toString()
                            .replace(privilegeResponse?.currency ?: "", "")

                    newGrossRev = privilegeResponse?.currency + newGrossRev.toDouble()
                        .convert(
                            privilegeResponse?.currencyFormat
                                ?: requireActivity().getString(R.string.indian_currency_format)
                        )
                    newNetRev = privilegeResponse?.currency + newNetRev.toDouble()
                        .convert(
                            privilegeResponse?.currencyFormat
                                ?: requireActivity().getString(R.string.indian_currency_format)
                        )

                    binding.apply {
                        tvTotalSeats.text =
                            "${getString(R.string.total_seats)} : ${it.result.performanceSummary[0].totalSeats}"
                        tvGross.text =
                            "${getString(R.string.gross_amt_with_dot)} : ${newGrossRev}"
                        tvSeatSold.text =
                            "${getString(R.string.sold_seats)} : ${it.result.performanceSummary[0].totalSoldSeats}"
                        tvNetAmount.text =
                            "${getString(R.string.net_amt_with_dot)} : ${newNetRev}"
                    }

                    binding.apply {
                        container1Performance.visible()
                        dashboard2Container.visible()
                        noData.root.gone()
                    }

                } else {
                    if (it?.message != null) {
                        stopShimmerEffect()
                        binding.apply {
                            container1Performance.gone()
                            dashboard2Container.gone()
                            noData.root.visible()
                            binding.noData.tvNoData.text = "${it.message}}"
                        }

                    } else {
                        stopShimmerEffect()
                        binding.apply {
                            container1Performance.gone()
                            dashboard2Container.gone()
                            noData.root.visible()
                            noData.tvNoData.text = requireActivity().getString(R.string.opps)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.d("Error in BookingTrendsDetailsObserver ${e.message}")
            }
        }
    }

    private fun setUpDayWiseObserver() {
        dashboardViewModelDayWise.bookingTrendsResponseViewModelDayWise.observe(viewLifecycleOwner) {
            stopShimmerEffect()
            try {
                binding.swipeRefreshLayoutDashboard.isRefreshing = false
                if (it != null) {
                    if (it.code == 200) {

                        if (it.result.eTicketsPerformance.isNullOrEmpty().not()) {
                            binding.tvViewDetailsServiceOccupancy.visible()
                            binding.view1.visible()

                            val newList: MutableList<ETicketsPerformance> =
                                it.result.eTicketsPerformance?.toMutableList() ?: mutableListOf()
                            newList.sortBy { it.eTicket }

                            setETicketPerformanceBarChart(newList)
                        } else {
                            binding.tvViewDetailsServiceOccupancy.gone()
                            binding.view1.gone()
                        }
                    } else {
                        if (it.message != null) {
                            if (binding.shimmerDashboardPerformance.isShimmerStarted) {
                                binding.shimmerDashboardPerformance.stopShimmer()
                            }
                            binding.apply {
                                container1Performance.gone()
                                dashboard2Container.gone()
                                noData.root.visible()
                                binding.noData.tvNoData.text = "${it.message}}"
                            }
                        }
                    }
                } else {
                    binding.tvViewDetailsServiceOccupancy.gone()
                }
            } catch (e: Exception) {
                Timber.d("Error in BookingTrendsDetailsObserver ${e.message}")
            }

        }
    }

    private fun startShimmerEffect() {
        binding.apply {
            filterLayout.root.gone()
            noData.root.gone()
            shimmerDashboardPerformance.visible()
            container1Performance.gone()
            dashboard2Container.gone()
            shimmerDashboardPerformanceInclude.cardViewShimmerSourceTrend.visible()
            shimmerDashboardPerformanceInclude.cardViewShimmerOPR4.gone()
            shimmerDashboardPerformance.startShimmer()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            if (privilegeResponse?.country == "India") {
                filterLayout.root.gone()
            } else {
                filterLayout.root.visible()
            }
            shimmerDashboardPerformance.gone()
            container1Performance.visible()
            dashboard2Container.visible()
            shimmerDashboardPerformanceInclude.cardViewShimmerSourceTrend.gone()
            if (shimmerDashboardPerformance.isShimmerStarted) {
                shimmerDashboardPerformance.stopShimmer()
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

    override fun onButtonClick(view: Any, dialog: Dialog) {
//        TODO("Not yet implemented")
    }

    private fun setAllServices() {

        var totalSelectedServices = 0

        allottedServicesResponseModel?.services?.forEach {
            if (it.isChecked)
                totalSelectedServices++
        }

        if (totalSelectedServices > 0 && totalSelectedServices < totalOverallServices
        ) {

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
            selectedServiceName =
                "${getString(R.string.all_services_title_case)} ($totalOverallServices)"

        }

        if (serviceId == "-1" && totalSelectedServices == totalOverallServices) {

            selectedServiceName =
                "${getString(R.string.all_services_title_case)} (${totalOverallServices})"

        } else {

            selectedServiceName = "${getString(R.string.service)} ($totalSelectedServices)"

            if (serviceId.lastIndexOf(",") != -1)
                serviceId = serviceId.substring(0, serviceId.lastIndexOf(","))

        }
        binding.allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
//        callBookingTrendsApi()
        //callBookingTrendsDayWiseApi()

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
                totalOverallServices = it.services.size
                serviceSize = it.services.size
                binding.allServicesAndDateFilterContainer.tvAllService.text =
                    "${getString(R.string.all_services_title_case)} (${it.services.size})"

            }
        }
    }
}