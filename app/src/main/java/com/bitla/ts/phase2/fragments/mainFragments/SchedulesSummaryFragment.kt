package com.bitla.ts.phase2.fragments.mainFragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.format_type
import com.bitla.ts.data.schedule_summary_details
import com.bitla.ts.databinding.FragmentSchedulesSummaryBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.phase2.adapter.child.SchedulesSummaryActiveAdapter
import com.bitla.ts.phase2.adapter.child.SchedulesSummaryCancelledAdapter
import com.bitla.ts.phase2.dashboardContainer.activity.DashboardDetailsActivity
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.request.ReqBody
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.request.SchedulesSummaryRequest
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.response.ActiveService
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.response.CancelledService
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.common.getDateYMD
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D
import com.bitla.ts.utils.constants.SCHEDULES_SUMMARY
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.constants.SchedulesSummary
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
import toast
import visible

class SchedulesSummaryFragment : BaseFragment() {

    private lateinit var binding: FragmentSchedulesSummaryBinding

    //private lateinit var serviceWiseBookingAdapter: SchedulesSummaryActiveAdapter
    //private lateinit var schedulesSummaryCancelledAdapter: SchedulesSummaryCancelledAdapter
    //private lateinit var layoutManager: RecyclerView.LayoutManager
    private var selectServiceType = ""
    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var fromDate = ""
    private var toDate = ""
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
    private var allottedServicesResponseModel: AllotedServicesResponseModel? = null
    private var serviceId = "-1"
    private var totalOverallServices = 0
    private var selectedServiceName = "All Services"
    private var dashboardServiceFilterConf = DashboardServiceFilterConf()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var serviceSize: Int? = 0
    private var locale: String? = ""
    private var currentTabPosition: Int? = 0

    override fun onResume() {
        super.onResume()
        currentTabPosition = (activity as? DashboardDetailsActivity)?.findViewById<ViewPager2>(R.id.viewPagerDashboard)?.currentItem
        if (currentTabPosition == 4) {
            if (isAttachedToActivity()) {
                callScheduleSummaryApi()
            }
            setUpObserver()

            binding.selectSeatType.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    this.resources.getStringArray(R.array.activeCancelledServicesArray)
                )
            )
            serviceFilter()
        }
    }
    //    private var allotedServicesResponseModel: AllotedServicesResponseModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSchedulesSummaryBinding.inflate(inflater, container, false)
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

//        setUpObserver()


        binding.allServicesAndDateFilterContainer.tvDate.setOnClickListener {

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


                        dashboardServiceFilterConf.fromId = ""
                        dashboardServiceFilterConf.toId = ""
                        dashboardServiceFilterConf.fromTitle = getString(R.string.all_cities)
                        dashboardServiceFilterConf.toTitle = getString(R.string.all_cities)
                        dashboardServiceFilterConf.hubTitle = ""
                        dashboardServiceFilterConf.hubId = ""
                        dashboardServiceFilterConf.isHub = false
                        serviceId = "-1"

                        callAllottedServiceApi(fromDate, toDate)
                        callScheduleSummaryApi()

                    }
                }

            )
        }

        binding.allServicesAndDateFilterContainer.tvAllService.setOnClickListener {

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

        binding.selectSeatType.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                this.resources.getStringArray(R.array.activeCancelledServicesArray)
            )
        )

        binding.selectSeatType.setOnItemClickListener { parent, view, position, id ->
            selectServiceType = parent.getItemAtPosition(position).toString()

            serviceFilter()
        }

        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            binding.swipeRefreshLayoutDashboard.isRefreshing = true
            callScheduleSummaryApi()

        }

        binding.tvActiveServices.setOnClickListener {
            hideShowActiveServices()
        }

        binding.tvCancelledServies.setOnClickListener {
            hideShowCancelledServices()
        }

        setAllottedDetailObserver()

        firebaseLogEvent(
            requireContext(),
        SCHEDULES_SUMMARY,
        loginModelPref.userName,
        loginModelPref.travels_name,
        loginModelPref.role,
        SCHEDULES_SUMMARY,
            SchedulesSummary.SCHEDULES_SUMMARY
        )




        return view
    }

    override fun isInternetOnCallApisAndInitUI() {
//        if (isAttachedToActivity()) {
//            callScheduleSummaryApi()
//        }
    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }



    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectSeatType.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                this.resources.getStringArray(R.array.activeCancelledServicesArray)
            )
        )
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
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = PreferenceUtils.getDashboardCurrentDate()

    }

    private fun callScheduleSummaryApi() {
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
                ReqBody(
                    apiKey = loginModelPref.api_key,
                    originId = -1,
                    destination = -1,
                    serviceId = serviceId,
                    startDate = fromDate,
                    endDate = toDate,
                    sortBy = "htol"
                )

            val scheduleSummaryDetailsRequest =
                SchedulesSummaryRequest(
                    bccId = bccId.toString(),
                    methodName = schedule_summary_details,
                    format = format_type,
                    reqBody = reqBody
                )

            dashboardViewModel.schedulesSummaryApi(
                schedulesSummaryRequest = reqBody,
                locale = locale ?: "en",
                apiType = schedule_summary_details
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                schedulesSummaryContainer.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun setUpObserver() {
        dashboardViewModel.scheduleSummaryDetailsResponseViewModel.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayoutDashboard.isRefreshing = false
            stopShimmerEffect()


            if (it != null && it.code == 200) {
                try {
                    binding.tvActiveServices.text =
                        "${getString(R.string.active_trips)} (${it.result.schedulesSummary[0].activeServices.size})"
                    binding.tvCancelledServies.text =
                        "${getString(R.string.cancelled_trips)} (${it.result.schedulesSummary[0].cancelledServices.size})"

                    if (it.result.schedulesSummary[0].activeServices.isNotEmpty() || it.result.schedulesSummary[0].cancelledServices.isNotEmpty()) {
                        binding.schedulesSummaryContainer.visible()
                        binding.cardViewSchedulesSummary.visible()
                        setMyBookingsAdapter(it.result.schedulesSummary[0].activeServices)
                        setMyBookingsAdapterCancelled(it.result.schedulesSummary[0].cancelledServices)
                    } else {

                        stopShimmerEffect()

                        binding.schedulesSummaryContainer.visible()
                        binding.cardViewSchedulesSummary.gone()
                        binding.noData.root.visible()
                        binding.noData.tvNoData.text =
                            requireActivity().getString(R.string.no_data_available)
                    }
                } catch (e: Exception) {
                    requireActivity().toast(getString(R.string.opps))
                    Timber.d("Error in ScheduleSummaryFragment ScheduleSummaryDetails Observer ${e.message}")
                }
            } else {
                stopShimmerEffect()
                binding.cardViewSchedulesSummary.gone()
                binding.noData.root.visible()
                binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
            }

        }
    }

    private fun setMyBookingsAdapter(activeServicesList: List<ActiveService>) {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvActiveServices.layoutManager = layoutManager
        val serviceWiseBookingAdapter =
            SchedulesSummaryActiveAdapter(requireActivity(), activeServicesList)
        binding.rvActiveServices.adapter = serviceWiseBookingAdapter
    }

    private fun setMyBookingsAdapterCancelled(cancelledServicesList: List<CancelledService>) {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvCancelledServices.layoutManager = layoutManager
        val schedulesSummaryCancelledAdapter =
            SchedulesSummaryCancelledAdapter(requireActivity(), cancelledServicesList)
        binding.rvCancelledServices.adapter = schedulesSummaryCancelledAdapter
    }

    private fun serviceFilter() {
        selectServiceType = binding.selectSeatType.text.toString()
        when (selectServiceType) {
            getString(R.string.all_active_cancel) -> {
                binding.apply {
                    tvActiveServices.visible()
                    binding.rvActiveServices.gone()
                    binding.tvActiveServices.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_down,
                        0
                    )
                    //hideShowActiveServices()
                    //rvActiveServices.visible()

                    tvCancelledServies.visible()
                    binding.rvCancelledServices.gone()
                    binding.tvCancelledServies.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_down,
                        0
                    )
                    
                    //hideShowCancelledServices()
                    //rvCancelledServices.visible()
                }
            }

            getString(R.string.only_active_trips) -> {
                binding.apply {
                    tvActiveServices.visible()
                    binding.rvActiveServices.gone()
                    binding.tvActiveServices.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_down,
                        0
                    )
                    //rvActiveServices.visible()

                    tvCancelledServies.gone()
                    rvCancelledServices.gone()
                }
            }

            getString(R.string.only_cancelled_trips) -> {
                binding.apply {
                    tvActiveServices.gone()
                    rvActiveServices.gone()

                    tvCancelledServies.visible()
                    binding.rvCancelledServices.gone()
                    binding.tvCancelledServies.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_down,
                        0
                    )
                }
            }
        }
    }

    private fun startShimmerEffect() {
        binding.apply {
            shimmerSchedulesSummary.visible()
            schedulesSummaryContainer.gone()
            noData.root.gone()
            shimmerSchedulesSummary.startShimmer()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            shimmerSchedulesSummary.gone()
            schedulesSummaryContainer.visible()
            if (shimmerSchedulesSummary.isShimmerStarted) {
                shimmerSchedulesSummary.stopShimmer()
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

            serviceId =
                serviceId.substring(0, serviceId.lastIndexOf(","))

        }
        binding.allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
//        callScheduleSummaryApi()

    }

    private fun hideShowActiveServices() {

        if (binding.rvActiveServices.isVisible) {
            binding.rvActiveServices.gone()
            binding.tvActiveServices.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_down,
                0
            )
            
        } else {
            binding.rvActiveServices.visible()
            binding.tvActiveServices.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_up_light,
                0
            )
            
        }
    }

    private fun hideShowCancelledServices() {
        if (binding.rvCancelledServices.isVisible) {
            binding.rvCancelledServices.gone()
            binding.tvCancelledServies.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_down,
                0
            )
            
        } else {
            binding.rvCancelledServices.visible()
            binding.tvCancelledServies.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_up_light,
                0
            )
            
        }
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