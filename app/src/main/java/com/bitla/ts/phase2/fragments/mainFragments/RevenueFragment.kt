package com.bitla.ts.phase2.fragments.mainFragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.format_type
import com.bitla.ts.data.revenue_details
import com.bitla.ts.databinding.FragmentRevenueBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.chartUtils.ReportValue
import com.bitla.ts.phase2.dashboardContainer.activity.DashboardDetailsActivity
import com.bitla.ts.phase2.dashboardContainer.activity.DashboardRevenueDetailsActivity
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.response.*
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION
import com.bitla.ts.utils.sharedPref.PREF_LOCALE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
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

class RevenueFragment : BaseFragment() {

    private lateinit var binding: FragmentRevenueBinding
    private var branchListSummaryList = ArrayList<ReportValue>()
    private var emptySeatsList = ArrayList<ReportValue>()
    private var serviceWiseCollectionList = ArrayList<ReportValue>()
    private var dayWiseColletionBarChartReportList = ArrayList<ReportValue>()
    private var gstCollectionReportList = ArrayList<ReportValue>()
    private var agentWiseNetRevenueList = ArrayList<ReportValue>()
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


    private var isNegativeValueDayWiseCollection = false
    private var isNegativeValueUnsoldSeats = false
    private var isNegativeValueBranchWiseCollection = false
    private var isNegativeValueServiceWise = false
    private var isNegativeValueServiceTaxCollection = false
    private var privilegeResponse: PrivilegeResponseModel? = null
    private var currentTabPosition: Int? = 0

    override fun onResume() {
        super.onResume()
        currentTabPosition = (activity as? DashboardDetailsActivity)?.findViewById<ViewPager2>(R.id.viewPagerDashboard)?.currentItem
        if (currentTabPosition == 1) {
            if (isAttachedToActivity()) {
                callRevenueDayWiseDetailsApi()
                callRevenueDetailsApi()
                setUpObserver()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentRevenueBinding.inflate(inflater, container, false)
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
//        val fragment = DashboardRevenueDetailsFragment()
        binding.apply {
            tvViewDetailsServiceOccupancy.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent = Intent(requireContext(), DashboardRevenueDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 0)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
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
                    ViewDetails.BRANCH_WISE_REVENUE
                )
            }

            tvViewDetailsOccupancy.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent = Intent(requireContext(), DashboardRevenueDetailsActivity::class.java)
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
                    ViewDetails.EMPTY_SEAT_WISE_REVENUE
                )
            }

            tvViewDetailsBookingService.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent = Intent(requireContext(), DashboardRevenueDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 4)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
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
                    ViewDetails.GST_COLLECTION_REVENUE
                )
            }

            tvViewDetailsSeatStatus.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent = Intent(requireContext(), DashboardRevenueDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 3)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
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
                    ViewDetails.DAY_WISE_REVENUE
                )
            }

            tvViewDetailsServiceTaxCollection.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent = Intent(requireContext(), DashboardRevenueDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 2)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
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
                    ViewDetails.SERVICE_WISE_REVENUE
                )
            }

            tvViewDetailsAgentWiseRevenue.setOnClickListener {
                PreferenceUtils.removeKey(getString(R.string.selectedChartId))
                PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)
                val intent = Intent(requireContext(), DashboardRevenueDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 4)
                intent.putExtra(
                    getString(R.string.dashboardGraphDefaultDateFilterSelection),
                    defaultSelection
                )
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
                    "View Details - Service Wise Revenue"
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
                            callRevenueDetailsApi()
                            //callRevenueDayWiseDetailsApi()


                            dashboardServiceFilterConf.fromId = ""
                            dashboardServiceFilterConf.toId = ""
                            dashboardServiceFilterConf.fromTitle = "${getString(R.string.all_cities)}"
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
                intent.putExtra(getString(R.string.from_date),fromDate)
                intent.putExtra(getString(R.string.to_date),toDate)
                startActivityForResult(intent, SELECT_SERVICE_INTENT_REQUEST_CODE)

            }

        }

        setAllottedDetailObserver()

        firebaseLogEvent(
            requireContext(),
            REVENUE,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            REVENUE,
            Revenue.REVENUE
        )


        return binding.root
    }

    override fun isInternetOnCallApisAndInitUI() {
//        if (isAttachedToActivity()) {
//            callRevenueDayWiseDetailsApi()
//            callRevenueDetailsApi()
//        }
    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        setAllServices()

        if (defaultSelection == 5) {
            isCustomDateFilterSelected = true
        }

        if (defaultSelection == 6) {
            isCustomDateRangeFilterSelected = true
        }


        setUpObserver()
        //setUpDayWiseObserver()
        // setNetworkConnectionObserver(view)
        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            binding.swipeRefreshLayoutDashboard.isRefreshing = true
            branchListSummaryList.clear()
            emptySeatsList.clear()
            dayWiseColletionBarChartReportList.clear()
            gstCollectionReportList.clear()
            agentWiseNetRevenueList.clear()
            callRevenueDetailsApi()
            //callRevenueDayWiseDetailsApi()

        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    @SuppressLint("SetTextI18n")
//    private fun setNetworkConnectionObserver(view: View) {
//
//        networkConnection.observe(requireActivity()) { isConnected ->
//
//            if (isConnected != null) {
//                if (isConnected) {
//
////                    if (isOnlineInit) {
//                    DialogUtils.showNetworkBackOnline(
//                        networkErrorWithDisableAllViews,
//                        networkBackOnline
//                    )
//                    DialogUtils.enableDisableView(view, true)
//
//                    if (isAdded && context != null) {
//
//                        if (checkIfFragmentAttached(this, requireActivity())) {
////                            viewLifecycleOwner.lifecycleScope.launch {
//                            if (branchListSummaryList.isNotEmpty() && emptySeatsList.isNotEmpty()
//                                && dayWiseColletionBarChartReportList.isNotEmpty() && gstCollectionReportList.isNotEmpty()
//                            ) {
//                                branchListSummaryList.clear()
//                                emptySeatsList.clear()
//                                dayWiseColletionBarChartReportList.clear()
//                                gstCollectionReportList.clear()
//                            }
//
//                            callRevenueDetailsApi()
//                            callRevenueDayWiseDetailsApi()
////                            }
//                        }
//                    }
////                    }
//
//                } else {
//                    DialogUtils.showNetworkError(
//                        networkErrorWithDisableAllViews,
//                        networkBackOnline
//                    )
//                    DialogUtils.enableDisableView(view, false)
//                    isOnlineInit = true
//                }
//            }
//        }
//
//    }
    //    setChart
    private fun setBranchSummaryBarChart(branchAccountsSummary: List<BranchAccountsSummary>) {
        branchListSummaryList = getBranchAccountSummaryList(branchAccountsSummary)
        setCurrencyBranchFormatBarChart(
            requireContext(),
            binding.barChartOccupancy,
            branchListSummaryList,
            true,
            isNegativeValueBranchWiseCollection
        )
    }

    private fun setUnsoldSeatBarChart(unsoldSeatsLos: List<UnsoldSeatsLos>) {
        emptySeatsList = getUnsoldSeatsList(unsoldSeatsLos)
        setNormalBarChart(
            requireContext(),
            binding.barChartUnsold,
            emptySeatsList,
            true,
            isNegativeValueUnsoldSeats
        )
    }


    private fun setDaysWiseCollectionBarChart(dayWiseCollection: List<DayWiseCollection>) {
        dayWiseColletionBarChartReportList =
            getDayWiseColletionBarChartReportList(dayWiseCollection)
        setCurrencyFormatBarChart(
            requireContext(),
            binding.barChartDayWiseCollection,
            dayWiseColletionBarChartReportList,
            false,
            isNegativeValueDayWiseCollection
        )
    }

    private fun setServiceTaxCollection(serviceTaxCollection: List<ServiceTaxCollection>) {
        gstCollectionReportList = getServiceTaxCollectionBarChartReportList(serviceTaxCollection)
        setCurrencyFormatBarChart(
            requireContext(),
            binding.barChartServiceTaxCollection,
            gstCollectionReportList,
            true,
            isNegativeValueServiceTaxCollection
        )
    }

    private fun setAgentWiseNetRevenue(agentWiseNetRevenue: List<AgentWiseNetRevenue?>?) {
        agentWiseNetRevenueList = getAgentWiseNetRevenueBarChartReportList(agentWiseNetRevenue)
        setCurrencyFormatBarChart(
            requireContext(),
            binding.barChartAgentWiseRevenue,
            agentWiseNetRevenueList,
            true,
            isNegativeValueServiceTaxCollection
        )
    }

    private fun setServiceWiseCollectionBarChart(serviceWiseCollection: List<ServiceWiseCollection>) {
        serviceWiseCollectionList = getServiceWiseCollectionList(serviceWiseCollection)
        setCurrencyFormatBarChart(
            requireContext(),
            binding.barChartServiceWiseCollection,
            serviceWiseCollectionList,
            true,
            isNegativeValueServiceWise

        )
    }

    // simulate api call
    private fun getBranchAccountSummaryList(branchAccountsSummary: List<BranchAccountsSummary>): ArrayList<ReportValue> {

        branchListSummaryList.clear()

        branchAccountsSummary.forEachIndexed { i, _ ->
//            if (DASHBOARD_CHART_LIMIT > branchListSummaryList.size) { }
            var branchRevenue =
                branchAccountsSummary[i].revenue.toDouble().toString().replace(privilegeResponse?.currency ?: "", "").toDouble()
                    .convert(privilegeResponse?.currencyFormat ?: requireActivity().getString(R.string.indian_currency_format))
            if (branchAccountsSummary[i].revenue.toDouble() < 0) {
                isNegativeValueBranchWiseCollection = true
            }
            branchListSummaryList.add(
                ReportValue(
                    branchAccountsSummary[i].branch.substring(
                        SUBSTRING_START, min(SUBSTRING_END, branchAccountsSummary[i].branch.length)
                    )
                        .plus(CUSTOM_ELLIPSIS),
                    branchAccountsSummary[i].revenue.toDouble().toString(),
                    getDashboardChartColor(
                        requireContext(),
                        branchAccountsSummary[i].revenue.toDouble().toInt()
                    ),
                    branchAccountsSummary[i].branch,
                    "₹${branchRevenue}",
                )
            )
        }

        return branchListSummaryList
    }

    private fun getUnsoldSeatsList(unsoldSeatsLos: List<UnsoldSeatsLos>): ArrayList<ReportValue> {
        emptySeatsList.clear()

        unsoldSeatsLos.forEachIndexed { i, _ ->
//            if (DASHBOARD_CHART_LIMIT > emptySeatsList.size) { }
            if (unsoldSeatsLos[i].emptySeatsCount!! < 0) {
                isNegativeValueUnsoldSeats = true
            }
            emptySeatsList.add(
                ReportValue(
                    unsoldSeatsLos[i].unsoldSeats?.substring(
                        SUBSTRING_START, min(SUBSTRING_END, unsoldSeatsLos[i].unsoldSeats!!.length)
                    )
                        .plus(CUSTOM_ELLIPSIS),
                    (unsoldSeatsLos[i].emptySeatsCount ?: 0).toString(),
                    getDashboardChartColor(
                        requireContext(),
                        unsoldSeatsLos[i].emptySeatsCount?.toDouble()?.toInt() ?: 0
                    ),
                    unsoldSeatsLos[i].unsoldSeats,
                    "Empty Seats: ${unsoldSeatsLos[i].emptySeatsCount}"

                )
            )
        }

        return emptySeatsList
    }

    private fun getServiceWiseCollectionList(serviceWiseCollection: List<ServiceWiseCollection>): ArrayList<ReportValue> {
        serviceWiseCollectionList.clear()

        serviceWiseCollection.forEachIndexed { i, _ ->
//            if (DASHBOARD_CHART_LIMIT > serviceWiseCollectionList.size) { }
            var serviceRevenue =
                serviceWiseCollection[i].revenue.toDouble().toString().replace(privilegeResponse?.currency ?: "", "").toDouble()
                    .convert(privilegeResponse?.currencyFormat ?: requireActivity().getString(R.string.indian_currency_format))
            if (serviceWiseCollection[i].revenue.toDouble() < 0) {
                isNegativeValueServiceWise = true
            }
            serviceWiseCollectionList.add(
                ReportValue(
                    serviceWiseCollection[i].service.substring(
                        SUBSTRING_START, min(SUBSTRING_END, serviceWiseCollection[i].service.length)
                    )
                        .plus(CUSTOM_ELLIPSIS),
                    serviceWiseCollection[i].revenue.toDouble().toString(),
                    getDashboardChartColor(
                        requireContext(),
                        serviceWiseCollection[i].revenue.toDouble().toInt()
                    ),
                    serviceWiseCollection[i].service,
                    "₹${serviceRevenue}",
                )
            )
        }

        return serviceWiseCollectionList
    }

    private fun getDayWiseColletionBarChartReportList(dayWiseCollection: List<DayWiseCollection>): ArrayList<ReportValue> {

        dayWiseColletionBarChartReportList.clear()

        dayWiseCollection.forEachIndexed { i, _ ->
//            if (DASHBOARD_CHART_LIMIT > dayWiseColletionBarChartReportList.size) { }
            var daywiseRevenue =
                dayWiseCollection[i].revenue.toDouble().toString().replace(privilegeResponse?.currency ?: "", "").toDouble()
                    .convert(privilegeResponse?.currencyFormat ?: requireActivity().getString(R.string.indian_currency_format))
            if (dayWiseCollection[i].revenue.toDouble() < 0) {
                isNegativeValueDayWiseCollection = true
            }
            dayWiseColletionBarChartReportList.add(
                ReportValue(
                    getDateMMMDD(dayWiseCollection[i].date).toString(),
                    dayWiseCollection[i].revenue.toDouble().toString(),
                    getDashboardChartColor(
                        requireContext(),
                        dayWiseCollection[i].revenue.toDouble().toInt()
                    ),
                    getDateMMMDD(dayWiseCollection[i].date).toString(),
                    "₹${daywiseRevenue}",
                )
            )
        }

        return dayWiseColletionBarChartReportList
    }

    private fun getAgentWiseNetRevenueBarChartReportList(agentWiseNetRevenue: List<AgentWiseNetRevenue?>?): ArrayList<ReportValue> {
        agentWiseNetRevenueList.clear()
        agentWiseNetRevenue?.forEachIndexed { i, _ ->
//            if (DASHBOARD_CHART_LIMIT > gstCollectionReportList.size) { }
            var serviceTaxRevenue =
                agentWiseNetRevenue[i]?.revenue?.toDouble().toString().replace(privilegeResponse?.currency ?: "", "").toDouble()
                    .convert(privilegeResponse?.currencyFormat ?: requireActivity().getString(R.string.indian_currency_format))
            if ((agentWiseNetRevenue[i]?.revenue?.toDouble() ?: 0.0) < 0) {
                isNegativeValueServiceTaxCollection = true
            }
            agentWiseNetRevenueList.add(
                ReportValue(
                    agentWiseNetRevenue[i]?.branch?.substring(
                        SUBSTRING_START, min(SUBSTRING_END, agentWiseNetRevenue[i]?.branch?.length ?: 0)
                    )
                        .plus(CUSTOM_ELLIPSIS),
                    agentWiseNetRevenue[i]?.revenue?.toDouble().toString(),
                    getDashboardChartColor(
                        requireContext(),
                        agentWiseNetRevenue[i]?.revenue?.toDouble()?.toFloat()?.roundToInt() ?: 0
                    ),
                    agentWiseNetRevenue[i]?.branch,
                    "${privilegeResponse?.currency}${serviceTaxRevenue}",
                )
            )
        }
        return agentWiseNetRevenueList
    }

    private fun getServiceTaxCollectionBarChartReportList(serviceTaxCollection: List<ServiceTaxCollection>): ArrayList<ReportValue> {
        gstCollectionReportList.clear()
        serviceTaxCollection.forEachIndexed { i, _ ->
//            if (DASHBOARD_CHART_LIMIT > gstCollectionReportList.size) { }
            var serviceTaxRevenue =
                serviceTaxCollection[i].revenue.toDouble().toString().replace(privilegeResponse?.currency ?: "", "").toDouble()
                    .convert(privilegeResponse?.currencyFormat ?: requireActivity().getString(R.string.indian_currency_format))
            if (serviceTaxCollection[i].revenue.toDouble() < 0) {
                isNegativeValueServiceTaxCollection = true
            }
            gstCollectionReportList.add(
                ReportValue(
                    serviceTaxCollection[i].service.substring(
                        SUBSTRING_START, min(SUBSTRING_END, serviceTaxCollection[i].service.length)
                    )
                        .plus(CUSTOM_ELLIPSIS),
                    serviceTaxCollection[i].revenue.toDouble().toString(),
                    getDashboardChartColor(
                        requireContext(),
                        serviceTaxCollection[i].revenue.toDouble().toFloat().roundToInt()
                    ),
                    serviceTaxCollection[i].service,
                    "₹${serviceTaxRevenue}",
                )
            )
        }
        return gstCollectionReportList
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

        locale = PreferenceUtils.getlang()

        defaultSelection = PreferenceUtils.getPreference(
            DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION, 1
        )?.toInt() ?: 1
        currentDate = PreferenceUtils.getDashboardCurrentDate()
        fromDate = PreferenceUtils.getDashboardCurrentDate()

        privilegeResponse = (activity as BaseActivity).getPrivilegeBase()
        //toDate = PreferenceUtils.getDashboardToFutureDate()
        toDate = fromDate
    }

/*
    private fun callRevenueDetailsApi() {
        startShimmerEffect()
        if (requireActivity().isNetworkAvailable()) {

            val reqBody =
                com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.request.ReqBody(
                    apiKey = loginModelPref.api_key,
                    originId = -1,
                    destination = -1,
                    serviceId = serviceId,
                    branchId = "-1",
                    from = fromDate,
                    to = toDate,
                    sortBy = "htol",
                    apiType = 0,
                    is3DaysData = true
                )

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

                reqBody.from = fromDate
                reqBody.to = fromDate
            }


*/
/*            if(getDaysDifference(fromDate, toDate, DATE_FORMAT_Y_M_D) < 2 && !isLocalFilter) {

                reqBody.from = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).minusDays(1).toString()
                reqBody.to = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).plusDays(1).toString()

            }*//*


            if (isLocalFilter) {
                reqBody.is3DaysData = false
            }

            val revenueDetailsRequest =
                com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.request.RevenueDetailsRequest(
                    bccId = bccId.toString(),
                    methodName = revenue_details,
                    format = format_type,
                    reqBody = reqBody
                )

            dashboardViewModel.revenueDetails(
                revenueDetailsRequest = reqBody,
                apiType = revenue_details
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                revenueContainer.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }
*/

    private fun callRevenueDetailsApi() {
        startShimmerEffect()
        if (requireActivity().isNetworkAvailable()) {

            if(defaultSelection == 3 || defaultSelection == 4 || defaultSelection == 6) {
                dashboardDateSetText(
                    textView = binding.allServicesAndDateFilterContainer.tvDate,
                    fromDate = fromDate,
                    toDate = toDate,
                    inputDateFormat = DATE_FORMAT_Y_M_D
                )
            } else  {
                dashboardDateSetText(
                    textView = binding.allServicesAndDateFilterContainer.tvDate,
                    fromDate = fromDate,
                    toDate = null,
                    inputDateFormat = DATE_FORMAT_Y_M_D
                )

            }

            dashboardViewModel.getRevenueDetails(
                apiKey = loginModelPref.api_key,
                originId = -1,
                destinationId = -1,
                from = fromDate,
                to = fromDate,
                sortBy = "htol",
                serviceId = serviceId,
                branchId =  "-1",
                apiType = 0,
                is3DaysData = isLocalFilter.not(),
                locale = locale ?: "en",
                is_from_middle_tier = true,
                methodName = revenue_details
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                revenueContainer.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun callRevenueDayWiseDetailsApi() {

        if (requireActivity().isNetworkAvailable()) {
            val reqBody =
                com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.request.ReqBody(
                    apiKey = loginModelPref.api_key,
                    originId = -1,
                    destination = -1,
                    serviceId = serviceId,
                    branchId = "-1",
                    from = fromDate,
                    to = toDate,
                    sortBy = "htol",
                    apiType = 0
                )

            if (getDaysDifference(fromDate, toDate, DATE_FORMAT_Y_M_D) < 2 && !isLocalFilter && fromDate.isNotBlank()) {

                reqBody.from =
                    LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).minusDays(1).toString()
                reqBody.to =
                    LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).plusDays(1).toString()

            }

            val revenueDetailsRequest =
                com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.request.RevenueDetailsRequest(
                    bccId = bccId.toString(),
                    methodName = revenue_details,
                    format = format_type,
                    reqBody = reqBody
                )

        dashboardViewModelDayWise.revenueDetailsDayWise(
            revenueDetailsRequest = reqBody,
            apiType = revenue_details
        )
    }}

    private fun setUpObserver() {
        dashboardViewModel.revenueDetailsResponseViewModel.observe(viewLifecycleOwner) {

            try {
                binding.swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()

                if (it != null && it.code == 200) {
                    try {

                        if (it.result.dayWiseCollection?.isEmpty()?.not() == true) {
                            binding.view4.visible()
                            binding.tvViewDetailsSeatStatus.visible()
                            val newList: MutableList<DayWiseCollection> =
                                it.result.dayWiseCollection.toMutableList()
                                    ?: mutableListOf()
                            newList.sortBy { it.date }
                            setDaysWiseCollectionBarChart(newList)
                        }

                        if (it.result.branchAccountsSummary?.isEmpty()?.not() == true) {
                            binding.tvViewDetailsServiceOccupancy.visible()
                            binding.view1.visible()
                            setBranchSummaryBarChart(it.result.branchAccountsSummary)
                        } else {
                            binding.tvViewDetailsServiceOccupancy.gone()
                            binding.view1.gone()
                        }
                        if (it.result.unsoldSeatsLoss?.isEmpty()?.not() == true) {
                            setUnsoldSeatBarChart(it.result.unsoldSeatsLoss)
                            binding.view2.visible()
                            binding.tvViewDetailsOccupancy.visible()
                        } else {
                            binding.tvViewDetailsOccupancy.gone()
                            binding.view2.gone()
                        }
                        if (it.result.serviceWiseCollection?.isEmpty()?.not() == true) {
                            binding.viewServiceTaxCollection.visible()
                            binding.tvViewDetailsServiceTaxCollection.visible()
                            setServiceWiseCollectionBarChart(it.result.serviceWiseCollection)
                        } else {
                            binding.viewServiceTaxCollection.gone()
                            binding.tvViewDetailsServiceTaxCollection.gone()
                        }
                        if (it.result.serviceTaxCollection?.isEmpty()?.not() == true) {
                            binding.view3.visible()
                            binding.tvViewDetailsBookingService.visible()
                            setServiceTaxCollection(it.result.serviceTaxCollection)
                        } else {
                            binding.view3.gone()
                            binding.tvViewDetailsBookingService.gone()
                        }

                        if(it.result.agentWiseNetRevenue?.isEmpty()?.not() == true) {
                            binding.cardViewBookingService.gone()
                            binding.cardViewAgentWiseRevenue.visible()
                            binding.view4.visible()
                            binding.tvViewDetailsAgentWiseRevenue.visible()
                            setAgentWiseNetRevenue(it.result.agentWiseNetRevenue)

                        }

                        if(privilegeResponse?.country == "India") {
                            binding.view3.visible()
                            binding.tvViewDetailsBookingService.visible()
                            binding.cardViewBookingService.visible()
                            binding.cardViewAgentWiseRevenue.gone()
                        } else {
                            binding.cardViewBookingService.gone()
                            binding.cardViewAgentWiseRevenue.visible()
                            binding.view4.visible()
                            binding.tvViewDetailsAgentWiseRevenue.visible()
                        }
                        binding.revenueContainer.visible()
                        binding.noData.root.gone()
                    } catch (e: Exception) {
                        requireActivity().toast(getString(R.string.opps))
                        Timber.d("Error in RevenueDetailsObserver ${e.message}")
                    }
                } else {
                    if (it?.message != null) {
                        binding.revenueContainer.gone()
                        binding.noData.root.visible()
                        binding.noData.tvNoData.text = "${it.message}}"
                    } else {
                        binding.revenueContainer.gone()
                        binding.noData.root.visible()
                        binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
                    }
                }
            } catch (e: Exception) {
                Timber.d("Error in RevenueDetailsObserver ${e.message}")
            }
        }
    }

    private fun setUpDayWiseObserver() {
        dashboardViewModelDayWise.revenueDetailsResponseViewModelDayWise.observe(viewLifecycleOwner) {
            try {
                binding.swipeRefreshLayoutDashboard.isRefreshing = false

                if (it != null && it.code == 200) {
                    stopShimmerEffect()

                    if (it.result.dayWiseCollection?.isEmpty()?.not() == true) {
                        binding.view4.visible()
                        binding.tvViewDetailsSeatStatus.visible()
                        val newList: MutableList<DayWiseCollection> =
                            it.result.dayWiseCollection.toMutableList()
                                ?: mutableListOf()
                        newList.sortBy { it.date }
                        setDaysWiseCollectionBarChart(newList)

                    } else {
                        binding.view4.gone()
                        binding.tvViewDetailsSeatStatus.gone()
                    }

                    binding.revenueContainer.visible()
                    binding.noData.root.gone()
                } else {
                    if (it?.message != null) {
                        binding.revenueContainer.gone()
                        binding.noData.root.visible()
                        binding.noData.tvNoData.text = "${it.message}}"
                    } else {
                        binding.revenueContainer.gone()
                        binding.noData.root.visible()
                        binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
                    }
                }
            } catch (e: Exception) {
                Timber.d("Error in RevenueDetailsObserver ${e.message}")
            }
        }
    }

    private fun startShimmerEffect() {
        binding.apply {
            shimmerRevenue.visible()
            revenueContainer.gone()
            noData.root.gone()
            shimmerRevenue.startShimmer()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            shimmerRevenue.gone()
            revenueContainer.visible()
            if (shimmerRevenue.isShimmerStarted) {
                shimmerRevenue.stopShimmer()
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
            selectedServiceName = "${getString(R.string.all_services_title_case)} ($totalOverallServices)"

        }

        if (serviceId == "-1" && totalSelectedServices == totalOverallServices) {

            selectedServiceName = "${getString(R.string.all_services_title_case)} (${totalOverallServices})"

        } else {

            selectedServiceName = "${getString(R.string.service)} ($totalSelectedServices)"

            if (serviceId.lastIndexOf(",")!=-1){
                serviceId = serviceId.substring(0, serviceId.lastIndexOf(","))
            }

        }
        binding.allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
//        callRevenueDetailsApi()
        //callRevenueDayWiseDetailsApi()

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