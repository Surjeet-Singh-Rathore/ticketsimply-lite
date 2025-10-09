package com.bitla.ts.phase2.fragments.mainFragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.service_wise_booking_details
import com.bitla.ts.databinding.FragmentServiceWiseBookingBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.adapter.child.ServiceWiseBookingAdapter
import com.bitla.ts.phase2.dashboardContainer.activity.DashboardDetailsActivity
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.service_wise_booking_model.request.ServiceWiseBookingRequest
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.service_wise_booking_model.response.ServiceWiseBooking
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.dashboardDateSetText
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getDateYMD
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.common.jsonToString
import com.bitla.ts.utils.common.stringToJson
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.constants.SERVICE_WISE_BOOKING
import com.bitla.ts.utils.constants.SORT_BTN
import com.bitla.ts.utils.constants.SchedulesSummary
import com.bitla.ts.utils.constants.SortBtn
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION
import com.bitla.ts.utils.sharedPref.PREF_SLIDER_FROM_VALUE
import com.bitla.ts.utils.sharedPref.PREF_SLIDER_TO_VALUE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.bitla.ts.utils.toArrayList
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible


class ServiceWiseBookingFragment : BaseFragment() {

    companion object {
        val TAG = ServiceWiseBookingFragment::class.java.simpleName
    }

    private val binding by lazy { FragmentServiceWiseBookingBinding.inflate(layoutInflater) }
    private lateinit var serviceWiseBookingAdapter: ServiceWiseBookingAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var serviceWiseBookingList = arrayListOf<ServiceWiseBooking>()

    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var sortBy = "htol"
    private var isAllService = true

    //private var date:String=""
    private var serviceIdPref: String = ""
    private var locale: String? = ""
    private var occupancyStart: String? = "0"
    private var occupancyEnd: String? = "200"
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
    private var allotedServicesResponseModel: AllotedServicesResponseModel? = null
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var currentTabPosition: Int? = 0

    override fun onResume() {
        super.onResume()
        currentTabPosition = (activity as? DashboardDetailsActivity)?.findViewById<ViewPager2>(R.id.viewPagerDashboard)?.currentItem
        if (currentTabPosition == 3) {
            if (isAttachedToActivity()) {
                callServiceWiseBookingApi()
            }
            setUpObserver()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        PreferenceUtils.putString(PREF_SLIDER_FROM_VALUE, "0")
        PreferenceUtils.putString(PREF_SLIDER_TO_VALUE, "100")

        binding.apply {

            sortFilterLayout.btnSort.setOnClickListener {
                DialogUtils.dialogSortBy(
                    context = requireContext(),
                    sortBy = sortBy,
                    newLowToHighLabel = requireActivity().getString(R.string.occupancy_sort_ltoh),
                    newHighToLowLabel = requireActivity().getString(R.string.occupancy_sort_htol),
                    showOccupancyFilter = true,
                    showDateFilter = false,
                    showNetAmountFilter = true,

                    object : DialogSingleButtonListener {
                        override fun onSingleButtonClick(str: String) {
                            sortBy = str
                            callServiceWiseBookingApi()
                            binding.sortFilterLayout.sortMarker.visible()
                            firebaseLogEvent(
                                requireContext(),
                                SORT_BTN,
                                loginModelPref.userName,
                                loginModelPref.travels_name,
                                loginModelPref.role,
                                SORT_BTN,
                                SortBtn.SERVICE_WISE_BOOKING_SORT
                            )
                        }
                    }
                )
            }


            sortFilterLayout.btnFilter.setOnClickListener {
                DialogUtils.dialogSericeWiseSlider(
                    requireContext(),
                    rangeStart = occupancyStart ?: "",
                    rangeEnd = occupancyEnd ?: "",
                    onApplyFilter = { newStartRange: String?, newEndRange: String? ->
                        occupancyStart = newStartRange
                        occupancyEnd = newEndRange
                        binding.sortFilterLayout.filterMarker.visible()
                        callServiceWiseBookingApi()
                    }
                )
            }

            allServicesAndDateFilterContainer.tvDate.setOnClickListener {

                try {
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

                                callServiceWiseBookingApi()

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
                }catch (e: Exception){
                    if(BuildConfig.DEBUG){
                        e.printStackTrace()
                    }
                }

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
        setAllottedDetailObserver()
        return  binding.root
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPref()
        setAllServices()

        if (defaultSelection == 5) {
            isCustomDateFilterSelected = true
        }

        if (defaultSelection == 6) {
            isCustomDateRangeFilterSelected = true
        }

        setUpObserver()

        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            binding.swipeRefreshLayoutDashboard.isRefreshing = true
            occupancyStart = PreferenceUtils.getString(PREF_SLIDER_FROM_VALUE)
            occupancyEnd = PreferenceUtils.getString(PREF_SLIDER_TO_VALUE)
            callServiceWiseBookingApi()

        }

        firebaseLogEvent(
        requireContext(),
        SERVICE_WISE_BOOKING,
        loginModelPref.userName,
        loginModelPref.travels_name,
        loginModelPref.role,
        SERVICE_WISE_BOOKING,
            SchedulesSummary.SCHEDULES_SUMMARY
        )
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }


    private fun setServiceWiseBookingAdapter(serviceWiseBookingList: ArrayList<ServiceWiseBooking>) {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvSeatWiseBooking.layoutManager = layoutManager
        serviceWiseBookingAdapter = ServiceWiseBookingAdapter(requireActivity(), serviceWiseBookingList,privilegeResponseModel!!)
        binding.rvSeatWiseBooking.adapter = serviceWiseBookingAdapter
    }



    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        //date = PreferenceUtils.getDashboardCurrentDate()
        allottedServicesResponseModel = PreferenceUtils.getObject<AllotedServicesResponseModel>("allotted_services_model_dashboard")

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
        privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase()

    }


    private fun callServiceWiseBookingApi() {
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
                com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.service_wise_booking_model.request.ReqBody(
                    apiKey = loginModelPref.api_key,
                    routeId = serviceId,
                    sortBy = sortBy,
                    occupancyStart = occupancyStart.toString(),
                    occupancyEnd = occupancyEnd.toString(),
                    date = null,
                    from = fromDate,
                    to = toDate,
                    serviceId = serviceId
                )

            val serviceWiseBookingRequest =
                ServiceWiseBookingRequest(
                    bccId = bccId,
                    methodName = service_wise_booking_details,
                    format = format_type,
                    reqBody = reqBody
                )

            dashboardViewModel.serviceWiseBookingApi(
                serviceWiseBookingRequest = reqBody,
                apiType = service_wise_booking_details
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                serviceWiseContainer.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun setUpObserver() {
        dashboardViewModel.serviceWiseBookingResponseViewModel.observe(viewLifecycleOwner) { it ->
            try {
                binding.swipeRefreshLayoutDashboard.isRefreshing = false

                stopShimmerEffect()
                serviceWiseBookingList.clear()

                if (it != null && it.code == 200) {
                    if (it.result.serviceWiseBooking != null && it.result.serviceWiseBooking.isNotEmpty()) {

                        it.result.serviceWiseBooking.forEach {
                            val occ = it.occupancy?.toDouble()?.toInt() ?: 0

                            serviceWiseBookingList.add(
                                ServiceWiseBooking(
                                    occ.toString(),
                                    it.service,
                                    it.revenue,
                                    it.seatsSold ?: "",
                                    it.grossRevenue
                                )
                            )
                        }
                        setServiceWiseBookingAdapter(serviceWiseBookingList)

                        var newRev = it.result.totalRevenue.toString().replace(privilegeResponseModel?.currency ?: "", "")
                        newRev = newRev.toDouble()
                            .convert(requireContext().getString(R.string.indian_currency_format))

                        binding.apply {
                            tvSeatWiseTotalSeats.text = it.result.totalSeats
                            tvSeatWiseTotalServices.text = it.result.totalServices
                            tvSeatWiseRevenue.text = "${privilegeResponseModel?.currency ?: ""}${newRev}"
                        }

                        binding.serviceWiseContainer.visible()
                        binding.serviceWiseContainerCard.visible()
                        binding.noData.root.gone()
                    } else {
                        binding.serviceWiseContainer.visible()
                        binding.serviceWiseContainerCard.gone()
                        binding.noData.root.visible()
                        binding.noData.tvNoData.text =
                            requireActivity().getString(R.string.no_data_available)
                    }
                } else {
                    binding.serviceWiseContainer.visible()
                    binding.serviceWiseContainerCard.gone()
                    binding.noData.root.visible()
                    binding.noData.tvNoData.text = it.message ?: ""
                }
            } catch (e: Exception) {
                Timber.d("Error in ServiceWiseBokingFragment ${e.message}")
                requireActivity().toast(getString(R.string.opps))
            }
        }
    }


    private fun openActivityForResult() {
        val intent = Intent(requireContext(), SearchServiceActivity::class.java)
        startActivityForResult(intent, 2)
        PreferenceUtils.putString(getString(R.string.tag), TAG)

    }

    private fun startShimmerEffect() {
        binding.apply {
            sortFilterLayout.root.gone()
            clToolBar.gone()
            shimmerDashboardServiceWise.visible()
            serviceWiseContainer.gone()
            noData.root.gone()
            shimmerDashboardServiceWise.startShimmer()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            sortFilterLayout.root.visible()
            clToolBar.visible()
            shimmerDashboardServiceWise.gone()
            serviceWiseContainer.visible()
            if (shimmerDashboardServiceWise.isShimmerStarted) {
                shimmerDashboardServiceWise.stopShimmer()
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

        try {
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

                serviceId = serviceId.substringBeforeLast(",", serviceId)

            }
            binding.allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
//        callServiceWiseBookingApi()
        }
        catch (e: IndexOutOfBoundsException) {
            Timber.e(getString(R.string.index_is_out_of_bounds, e.message))

        } catch (e: NumberFormatException) {
            Timber.e(getString(R.string.invalid_number_format, e.message))

        } catch (e: Exception) {
            // Generic fallback
            Timber.e(getString(R.string.some_other_error, e.message))
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