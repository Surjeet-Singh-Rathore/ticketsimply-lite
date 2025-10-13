package com.bitla.ts.phase2.dashboardContainer.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPinnedListener
import com.bitla.ts.databinding.FragmentDashboardChartBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.city_details.request.CityDetailRequest
import com.bitla.ts.domain.pojo.dashboard_fetch.request.DashboardFetchRequest
import com.bitla.ts.domain.pojo.dashboard_fetch.request.OrderBy
import com.bitla.ts.domain.pojo.dashboard_fetch.request.ReqBody
import com.bitla.ts.domain.pojo.dashboard_fetch.response.DashboardFetchResponse
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.DashboardFetchAdapter
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.viewModel.CityDetailViewModel
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DashboardFragmentChart : BaseFragment(), OnItemClickListener,
    OnItemPinnedListener {

    companion object {
        val TAG = DashboardFragmentChart::class.java.simpleName
    }

//    private var privileges: PrivilegeResponseModel? = null
    private var enableNewOwnerDashboardWithBusinessMetrics: Boolean? = false
    private var showLoader: Boolean = true
    private lateinit var binding: FragmentDashboardChartBinding
    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private lateinit var dashboardFetchAdapter: DashboardFetchAdapter
    private var newDataList = ArrayList<String>()
    var list = mutableListOf<com.bitla.ts.domain.pojo.dashboard_fetch.response.Result>()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var locale: String? = ""
    private var serviceSize: Int? = 0
    private var date = ""
    private var isAllService = true
    private var serviceId = ""
    private lateinit var orderBy: OrderBy
    private var currentDate = ""
    private var pastDateFrom = ""
    private var futureDateTo = ""
    private var defaultSelection = 1
    private var toDate: String = ""
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
    private var dashboardServiceFilterConf = DashboardServiceFilterConf()
    private val cityDetailViewModel by viewModel<CityDetailViewModel<Any?>>()


    private var dashboardNavigationScreen = ""
    private var dashboardFetchResponseModel: DashboardFetchResponse? = null

    private var handler: Handler? = null
    private var primaryProgressStatus = 0
    private var millisecondsApiCall = ""
    private val time = 0
    private var lable : ArrayList<String> = arrayListOf()

    private val itemTouchHelper by lazy {
        val itemTouchCallback =
            object : SimpleCallback(UP or DOWN,0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val pinnedIndex = list.indexOfLast { it.isPinned == 1 }.plus(1)
                    val recyclerviewAdapter = recyclerView.adapter as DashboardFetchAdapter
                    val fromPosition = viewHolder.absoluteAdapterPosition//2
                    var toPosition = target.absoluteAdapterPosition//0

                    val list = recyclerviewAdapter.differ.currentList.toMutableList()
                    /*if (pinnedIndex != -1 && toPosition <= pinnedIndex)
                        toPosition = pinnedIndex.plus(1)*/
                    //Timber.d("toPosition $toPosition pinnedIndex $pinnedIndex")
                    recyclerviewAdapter.moveItem(fromPosition, toPosition)
                    recyclerviewAdapter.notifyItemMoved(fromPosition, toPosition)

                    Collections.swap(lable, fromPosition, toPosition)
                    for (i in 0..lable.size.minus(1)){
                        if (lable[i].equals("occupancy", true)) {
                            orderBy.occupancy = "${i + 1}:${list[fromPosition].isPinned}"
                        }
                        else if (lable[i].equals("performance", true)) {
                            orderBy.performance = "${i + 1}:${list[fromPosition].isPinned}"
                        }
                        else if (lable[i].equals("revenue", true)) {
                            orderBy.revenue = "${i + 1}:${list[fromPosition].isPinned}"
                        }
                        else if (lable[i].equals("total_pending_quota_seats", true)) {
                            orderBy.totalPendingQuotaSeats = "${i + 1}:${list[fromPosition].isPinned}"
                        }
                        else if (lable[i].equals("time_blocked_seats_booked_released", true)) {
                            orderBy.timeBlockedSeatsBookedReleased = "${i + 1}:${list[fromPosition].isPinned}"
                        }
                        else if (lable[i].equals("schedules_summary_active_cancelled", true)) {
                            orderBy.schedulesSummaryActiveCancelled = "${i + 1}:${list[fromPosition].isPinned}"
                        }
                    }
                    Timber.d("orderByList: = ${orderBy}")
                    setOrderByPref()

                    showLoader = false
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.scaleY = 1.3f
                        viewHolder?.itemView?.alpha = 0.7f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    binding.swipeRefreshLayoutDashboard.isEnabled= true
                    viewHolder.itemView.scaleY = 1.0f
                    viewHolder.itemView?.alpha = 1.0f
                }

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val pinnedIndex = list.indexOfLast { it.isPinned == 1 }
                    //if (pinnedIndex == -1)

                    //Timber.d("pinnedIndex $pinnedIndex")

                    val isDraggable = viewHolder.absoluteAdapterPosition > pinnedIndex
                    val dragFlags = if (isDraggable)
                        UP or DOWN or START or END
                    else
                        0
                    return makeMovementFlags(dragFlags, 0)
                }

                override fun isLongPressDragEnabled(): Boolean {
                    binding.swipeRefreshLayoutDashboard.isEnabled= false
                    return true
                }


            }
        ItemTouchHelper(itemTouchCallback)
    }

    override fun onPause() {
        super.onPause()
        setOrderByPref()
//        callDashboardApi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {

        binding = FragmentDashboardChartBinding.inflate(inflater, container, false)
        val view: View = binding.root
        setDateLocale(PreferenceUtils.getlang(),requireContext())

        PreferenceUtils.removeKey(PREF_SELECTED_SERVICE_ID_FILTER)
        PreferenceUtils.removeKey(PREF_SELECTED_SERVICE_FILTER)
        PreferenceUtils.removeKey(getString(R.string.selectedChartId))
        PreferenceUtils.setPreference(getString(R.string.selectedChartId), 0)

        date = getDateYMD(getTodayDate())
        toDate = date
        setDashboardDatePref()
        getPref()

        dashboardDateSetText(
            textView = binding.tvDate,
            fromDate = getDateYMD(getTodayDate()),
            toDate = null,
            inputDateFormat = DATE_FORMAT_Y_M_D
        )


        setUpObserver()
        callAllottedServiceApi(date, date)
        callCityDetailsApi()
        setCityDetailsObserver()
        setAllottedDetailObserver()
//        setNetworkConnectionObserver()

        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            binding.swipeRefreshLayoutDashboard.isRefreshing = true
            callAllottedServiceApi(date, date)
            callCityDetailsApi()
            callDashboardApi()
        }
        binding.tvDate.setOnClickListener {

            DialogUtils.dialogDateFilter(
                context = requireContext(),
                defaultSelection = defaultSelection,
                todayDate = getDateYMD(getTodayDate()),
                fromDate = date,
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
                        date = finalFromDate
                        toDate = finalToDate ?: date
                        defaultSelection = lastSelectedItem

                        dashboardDateSetText(
                            textView = binding.tvDate,
                            fromDate = date,
                            toDate = null,
                            inputDateFormat = DATE_FORMAT_Y_M_D
                        )

                        isCustomDateFilterSelected = isCustomDateFilter
                        isCustomDateRangeFilterSelected = isCustomDateRangeFilter

                        callDashboardApi()

                        dashboardServiceFilterConf.fromId = ""
                        dashboardServiceFilterConf.toId = ""
                        dashboardServiceFilterConf.fromTitle = "All Cities"
                        dashboardServiceFilterConf.toTitle = "All Cities"
                        dashboardServiceFilterConf.hubTitle = ""
                        dashboardServiceFilterConf.hubId = ""
                        dashboardServiceFilterConf.isHub = false


                        callAllottedServiceApi(date, toDate)
                    }
                }
            )
        }

        firebaseLogEvent(
            requireContext(),
            DASHBOARD_ICON,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            DASHBOARD_ICON,
            DashboardIcon.DASHBOARD_ICON
        )
        //setUpAdapter()
        return view
    }

    override fun isInternetOnCallApisAndInitUI() {
        if (isAttachedToActivity()) {
            callAllottedServiceApi(date, date)
            callCityDetailsApi()
            callDashboardApi()
        }
    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    private fun getPref() {
//        privileges = (activity as BaseActivity).getPrivilegeBase()

        lifecycleScope.launch {
            val privilege = (activity as BaseActivity).getPrivilegeBaseSafely()
            dashboardViewModel.updatePrivileges(privilege)
        }

        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        val loginkey = loginModelPref.api_key
        locale = PreferenceUtils.getlang()



        if (PreferenceUtils.getObject<OrderBy>("orderBy") != null) {
            orderBy = PreferenceUtils.getObject<OrderBy>("orderBy") as OrderBy
        }
        else {
            orderBy = OrderBy(
                occupancy = "1:0",
                performance = "2:0",
                revenue = "3:0",
                schedulesSummaryActiveCancelled = "4:0",
                timeBlockedSeatsBookedReleased = "5:0",
                totalPendingQuotaSeats = "6:0"
            )
        }

        getDashboardCacheData()
        val privilegeResponse = (activity as BaseActivity).getPrivilegeBase()
        enableNewOwnerDashboardWithBusinessMetrics = privilegeResponse?.enableNewOwnerDashboardWithBusinessMetrics ?: false
    }

    private fun getDashboardCacheData() {
        dashboardNavigationScreen =
            PreferenceUtils.getString(PREF_DASHBOARD_NAVIGATE_SCREEN).toString()

        if (dashboardNavigationScreen == getString(R.string.bookings)
            || dashboardNavigationScreen == getString(R.string.pick_up_chart)
            || dashboardNavigationScreen == getString(R.string.reports)
        ) {

            if (PreferenceUtils.getObject<DashboardFetchResponse>(PREF_DASHBOARD_MODEL_DATA) != null) {
                dashboardFetchResponseModel =
                    PreferenceUtils.getObject<DashboardFetchResponse>(PREF_DASHBOARD_MODEL_DATA)

                dashboardFetchResponseModel?.let {
                    if (dashboardFetchResponseModel?.lastUpdated != null) {
                        (activity as DashboardNavigateActivity).lastUpdatedOn(
                            dashboardFetchResponseModel!!.lastUpdated.toString()
                        )
                    }

                    list = dashboardFetchResponseModel?.result?.toMutableList()!!
                    list.sortBy {
                        it.order
                    }
                    setUpAdapter()
                    callAllApis()
                }
            } else {
                callAllApis()
                startShimmerEffect()
            }
        } else {
            callAllApis()
            startShimmerEffect()
        }
    }

    private fun callAllApis() {
        callDashboardApi()
        callAllottedServiceApi(date, date)
        callCityDetailsApi()
    }

    private fun setOrderByPref() {
        PreferenceUtils.putObject(orderBy, "orderBy")
    }

    private fun callDashboardApi() {

        startDashboardProgress()
        setDashboardDatePref()

        if (requireActivity().isNetworkAvailable()) {
            val reqBody = ReqBody(
                apiKey = loginModelPref.api_key,
                date = date,
                isAllService = isAllService,
                orderBy = orderBy,
                serviceId = serviceId,
                isFromMiddleTier = true,
                locale = locale ?: "en"
            )

            val dashboardFetchRequest = DashboardFetchRequest(
                bccId = bccId.toString(),
                format_type,
                dashboard_fetch,
                reqBody
            )

            /*  dashboardViewModel.dashBoardFetch(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            dashboardFetchRequest,
            dashboard_fetch
        )*/

            dashboardViewModel.dashBoardFetch(
                reqBody,
                dashboard_fetch
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
//                stopShimmerEffect()
                dashboardChartContainer.gone()
                rvDraggable.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun setUpObserver() {
        dashboardViewModel.dashboardFetchResponseViewModel.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayoutDashboard.isRefreshing = false
            stopShimmerEffect()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (!it.lastUpdated.isNullOrEmpty()) {
                            (activity as DashboardNavigateActivity).lastUpdatedOn(it.lastUpdated)
                        } else if (!it.result[0].lastUpdated.isNullOrEmpty()) {
                            (activity as DashboardNavigateActivity).lastUpdatedOn(it.result[0].lastUpdated.toString())
                        } else {
                            (activity as DashboardNavigateActivity).binding.appBar.lastUpdateTV.gone()
                        }
                        list = it.result.toMutableList()
                        list.sortBy {
                            it.order
                        }

                        lable.clear()
                        list.forEach {
                            lable.add(it.label)
                        }

                        if (showLoader) {
                            setUpAdapter()
                        }
                        showLoader = true

                        PreferenceUtils.putObject(it, PREF_DASHBOARD_MODEL_DATA)
                        binding.dashboardAPIProcessing.progress = 100
                        binding.dashboardAPIProcessing.gone()
                        requireActivity().toast(getString(R.string.dashboard_updated_successfully))
                    }
                    399 -> {
                        /*DialogUtils.twoButtonDialog(
                                this,
                                getString(R.string.use_here),
                                getString(R.string.already_logged_in),
                                getString(R.string.cancel),
                                getString(R.string.use_here2),
                                this
                            )*/

                    }
                    else -> {
                        binding.dashboardChartContainer.visible()
                        binding.noData.root.visible()
                        binding.rvDraggable.gone()
                        binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
                    }
                }
            } else {
                binding.dashboardChartContainer.visible()
                binding.noData.root.visible()
                binding.rvDraggable.gone()
                binding.noData.tvNoData.text =
                    requireActivity().getString(R.string.no_data_available)
            }
        }

    }

    private fun setUpAdapter() {
        dashboardViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
            itemTouchHelper.attachToRecyclerView(binding.rvDraggable)
            val dateInDMY = getCurrentFormattedDate(date, DATE_FORMAT_Y_M_D, DATE_FORMAT_D_M_Y)
            dashboardFetchAdapter = DashboardFetchAdapter(
                onItemClickListener = this,
                onItemPinnedListener = this,
                parentDate = dateInDMY,
                enableNewOwnerDashboardWithBusinessMetrics = enableNewOwnerDashboardWithBusinessMetrics,
                privileges = privilegeResponse
            )
            dashboardFetchAdapter.differ.submitList(list)
            binding.rvDraggable.adapter = dashboardFetchAdapter
        }

    }

    private fun callAllottedServiceApi(from: String, toDate: String) {
        try {
            if (isAttachedToActivity() && requireActivity().isNetworkAvailable()) {
                pickUpChartViewModel.getAllottedServicesWithDateChange(
                    apiKey = loginModelPref.api_key,
                    origin = "",
                    destination = "",
                    from = from,
                    to = toDate,
                    hubId = "",
                    isGroupByHubs = false,
                    viewMode = "report",
                    locale = "en",
                    isFromMiddleTier = true,
                    methodName = alloted_service_Dashboard_method
                )

            } else{
                requireActivity().noNetworkToast()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun setAllottedDetailObserver() {
        pickUpChartViewModel.allotedDetailResponse.observe(viewLifecycleOwner) { it ->
            if (it?.services != null) {
                val services = it.services

                allottedServicesResponseModel = it
                PreferenceUtils.putObject(it, "allotted_services_model_dashboard")

                serviceSize = it.services.size
                PreferenceUtils.setPreference("total_overall_service_size", serviceSize)

                binding.tvAllService.text = "${getString(R.string.all_services_title_case)} (${it.services.size})"
                binding.tvAllService.setOnClickListener {
                    PreferenceUtils.putString(getString(R.string.tag), TAG)
                    val intent = Intent(requireActivity(), SearchServiceActivity::class.java)

                    intent.putExtra(
                        getString(R.string.dashboardGraphServiceFiter), jsonToString(
                            allottedServicesResponseModel!!
                        )
                    )

                    intent.putExtra(
                        getString(R.string.dashboard_service_filter_conf),
                        jsonToString(dashboardServiceFilterConf)
                    )
                    startActivityForResult(intent, 2)
                }

            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == SELECT_SERVICE_INTENT_REQUEST_CODE) {

            val allottedServiceString = data?.getStringExtra("allotted_service_response")
            if (data?.hasExtra(getString(R.string.dashboard_service_filter_conf)) == true) {
                val dashboardServiceFilterConfString =
                    data.getStringExtra(getString(R.string.dashboard_service_filter_conf)) ?: ""
                dashboardServiceFilterConf = stringToJson(dashboardServiceFilterConfString)
            }

            if (allottedServiceString != null) {

                val aa = stringToJson<AllotedServicesResponseModel>(allottedServiceString)

                allottedServicesResponseModel = aa

                var totalSelectedServices = 0
                aa.services?.forEach {
                    if (it.isChecked)
                        totalSelectedServices++
                }

                if (totalSelectedServices > 0 && totalSelectedServices < (serviceSize ?: 0)
                ) {
                    binding.tvAllService.text = "Services (${totalSelectedServices})"
                    isAllService = false
                    serviceId = ""
                    aa.services?.forEach {
                        if (it.isChecked) {
                            serviceId += it.routeId.toString().replace(".0", "") + ","
                        }
                    }

                    serviceId = serviceId.substring(0, serviceId.lastIndexOf(","))
                    callDashboardApi()
                } else {
                    binding.tvAllService.text = "All Services (${serviceSize})"
                    isAllService = true
                    serviceId = ""
                    callDashboardApi()
                }
            }
        }
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {


        when (data) {
            "occupancy" -> {
                val intent = Intent(requireContext(), DashboardDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 0)
                startActivity(intent)

            }
            "revenue" -> {
                val intent = Intent(requireContext(), DashboardDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 1)
                startActivity(intent)
            }
            "performance" -> {
                val intent = Intent(requireContext(), DashboardDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 2)
                startActivity(intent)
            }
            "service_wise_booking" -> {
                val intent = Intent(requireContext(), DashboardDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 3)
                startActivity(intent)
            }
            "schedules_summary_active_cancelled" -> {
                val intent = Intent(requireContext(), DashboardDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 4)
                startActivity(intent)
            }
            "time_blocked_seats_booked_released" -> {
                val intent = Intent(requireContext(), DashboardDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 5)
                startActivity(intent)
            }
            "total_pending_quota_seats" -> {
                val intent = Intent(requireContext(), DashboardDetailsActivity::class.java)
                intent.putExtra(getString(R.string.fragmentTabPosition), 6)
                startActivity(intent)
            }
        }
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onItemClick(isPinned: String, position: Int, label: String) {

        when (label) {
            "occupancy" -> {
                orderBy.occupancy = "${position + 1}:${isPinned}"
            }
            "revenue" -> {
                orderBy.revenue = "${position + 1}:${isPinned}"
            }
            "performance" -> {
                orderBy.performance = "${position + 1}:${isPinned}"
            }
            "total_pending_quota_seats" -> {
                orderBy.totalPendingQuotaSeats =
                    "${position + 1}:${isPinned}"
            }
            "time_blocked_seats_booked_released" -> {
                orderBy.timeBlockedSeatsBookedReleased =
                    "${position + 1}:${isPinned}"
            }
            "schedules_summary_active_cancelled" -> {
                orderBy.schedulesSummaryActiveCancelled =
                    "${position + 1}:${isPinned}"
            }
//            "service_wise_booking" -> {
//                orderBy.serviceWiseBooking =
//                    "${position + 1}:${isPinned}"
//            }
        }
        setOrderByPref()
        callDashboardApi()
    }

    private fun setDashboardDatePref() {
        if (getDaysDifference(date, toDate, DATE_FORMAT_Y_M_D) >= 2) {
            currentDate = date
            pastDateFrom = date
            futureDateTo = toDate

        } else {
            currentDate = date
            pastDateFrom = LocalDate.parse(date, DateTimeFormatter.ISO_DATE).minusDays(1).toString()
            futureDateTo = LocalDate.parse(date, DateTimeFormatter.ISO_DATE).plusDays(1).toString()
        }
        PreferenceUtils.apply {
            setPreference("dashboard_resId", serviceId)
            setPreference(DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION, defaultSelection)
            setPreference(PREF_DASHBOARD_CURRENT_DATE, currentDate)
            setPreference(PREF_DASHBOARD_PAST_DATE, pastDateFrom)
            setPreference(PREF_DASHBOARD_FUTURE_DATE, futureDateTo)
            putObject(allottedServicesResponseModel, "allotted_services_model_dashboard")
            putObject(dashboardServiceFilterConf, getString(R.string.dashboard_service_filter_conf))
            //setPreference("total_overall_service_size", serviceSize)

        }
    }

    private fun startShimmerEffect() {
        binding.apply {
            shimmerNewDashboard.visible()
            dashboardChartContainer.gone()
            rvDraggable.gone()
            noData.root.gone()
            shimmerNewDashboard.startShimmer()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            shimmerNewDashboard.gone()
            dashboardChartContainer.visible()
            rvDraggable.visible()
            if (shimmerNewDashboard.isShimmerStarted) {
                shimmerNewDashboard.stopShimmer()
            }
        }
    }

    private fun callCityDetailsApi() {
        if (requireContext().isNetworkAvailable()) {
            val cityDetailRequest = CityDetailRequest(
                bccId.toString(),
                city_Details_method_name,
                format_type,
                com.bitla.ts.domain.pojo.city_details.request.ReqBody(
                    loginModelPref.api_key,
                    response_format,
                    locale = locale
                )
            )
            cityDetailViewModel.cityDetailAPI(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                locale!!,
                city_Details_method_name
            )
        }
//        else requireContext().noNetworkToast()
    }

    private fun setCityDetailsObserver() {
        cityDetailViewModel.cityDetailResponse.observe(requireActivity()) {
            try {

                if (it != null) {
                    if (it.code == 200) {
                        if (it.result != null && it.result.isNotEmpty()) {
                            PreferenceUtils.putObject(it, "cityListModel")
                        }
                    }
                } else {
                    requireActivity().toast(getString(R.string.server_error))
                }
            } catch (e: Exception) {
                requireActivity().toast(getString(R.string.opps))
                Timber.d("Error in DashboardFragmentChart CityDetails Observer ${e.message}")
            }
        }

    }

    private fun startDashboardProgress() {
        millisecondsApiCall =
            (PreferenceUtils.getString(PREF_DASHBOARD_API_MEASURE_TIME).toString())
        binding.dashboardAPIProcessing.visible()
        binding.dashboardAPIProcessing.progress = 0
        primaryProgressStatus = 0

//        Timber.d("dashboardAPIMeasureTime-dashboard $millisecondsApiCall")
        if (millisecondsApiCall.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {

                    while (primaryProgressStatus <= 100) {
                        primaryProgressStatus += 1

                        delay((millisecondsApiCall.toFloat().toInt().toLong() / 50))

                        binding.dashboardAPIProcessing.progress = primaryProgressStatus
//                        Timber.d("primaryProgressStatus - $primaryProgressStatus")
//                        if (primaryProgressStatus == 101) {
//                        }
                    }
                }
            }
        }
    }
}