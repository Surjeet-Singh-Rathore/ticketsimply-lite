package com.bitla.ts.phase2.fragments.mainFragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.data.phone_blocked_seats
import com.bitla.ts.databinding.FragmentPhoneBlockedBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.adapter.child.PhoneBlockedSectionAdapter
import com.bitla.ts.phase2.dashboardContainer.activity.*
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.phone_blocked_model.response.Detail
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D
import com.bitla.ts.utils.constants.PHONE_BLOCKED
import com.bitla.ts.utils.constants.PhoneBlocked
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_SERVICE_ID_FILTER
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible


class PhoneBlockedFragment : BaseFragment(), DialogSingleButtonListener, OnItemPassData {

    companion object {
        val TAG = PhoneBlockedFragment::class.java.simpleName
    }

    private var privilegeResponse: PrivilegeResponseModel? = null
    private lateinit var binding: FragmentPhoneBlockedBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var phoneBlockedAdapter: PhoneBlockedSectionAdapter
    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var sortBy = "htol"
    private var items: MutableList<com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.phone_blocked_model.response.Result> =
        mutableListOf()
    private var isAllService = true
    private var serviceIdPref: String = ""
    private var fromDate = ""
    private var toDate = ""
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var locale: String? = ""
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
    private var serviceSize: Int? = 0
    private var allotedServicesResponseModel: AllotedServicesResponseModel? = null
    private var country: String? = null
    private var pendingTicketsDetailList: MutableList<Detail> = mutableListOf()
    private var releasedTicketsDetailList: MutableList<Detail> = mutableListOf()
    private var currentTabPosition: Int? = 0
    
    override fun onResume() {
        super.onResume()
        currentTabPosition = (activity as? DashboardDetailsActivity)?.findViewById<ViewPager2>(R.id.viewPagerDashboard)?.currentItem
        if (currentTabPosition == 5) {
            callPhoneBlockApi()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhoneBlockedBinding.inflate(inflater, container, false)
        val view: View = binding.root
        currentTabPosition = (activity as? DashboardDetailsActivity)?.findViewById<ViewPager2>(R.id.viewPagerDashboard)?.currentItem

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

//        callPhoneBlockApi()

        if (country != null && country.equals("India", true)) {
            binding.containerPhoneBlockedDashboardCardIndia.visible()
            binding.containerPhoneBlockedDashboardCard.gone()
            setUpPhoneBlockedIndiaObserver()


        } else {
            binding.containerPhoneBlockedDashboardCard.visible()
            binding.containerPhoneBlockedDashboardCardIndia.gone()
            setUpObserver()
            setPhoneBlockedAdapter()
        }

        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            binding.swipeRefreshLayoutDashboard.isRefreshing = true
            callPhoneBlockApi()

        }

        binding.apply {


            /*btnSort.setOnClickListener {
                DialogUtils.dialogSortBy(
                    requireContext(),
                    sortBy,
                    object : DialogSingleButtonListener {
                        override fun onSingleButtonClick(str: String) {
                            sortBy = str
                            callPhoneBlockApi()
                        }
                    }
                )
            }*/

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

                            dashboardServiceFilterConf.fromId = ""
                            dashboardServiceFilterConf.toId = ""
                            dashboardServiceFilterConf.fromTitle = getString(R.string.all_cities)
                            dashboardServiceFilterConf.toTitle = getString(R.string.all_cities)
                            dashboardServiceFilterConf.hubTitle = ""
                            dashboardServiceFilterConf.hubId = ""
                            dashboardServiceFilterConf.isHub = false

                            callAllottedServiceApi(fromDate, toDate)
                            callPhoneBlockApi()
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

        currentTabPosition = (activity as? DashboardDetailsActivity)?.findViewById<ViewPager2>(R.id.viewPagerDashboard)?.currentItem
        if (currentTabPosition == 5) {
            setUpObserver()
            setAllottedDetailObserver()
        }

        setTextViewDrawableColor(requireContext(),binding.tvPendingTickets,R.color.white)
        setTextViewDrawableColor(requireContext(),binding.tvReleasedTickets,R.color.white)

        firebaseLogEvent(
            requireContext(),
            PHONE_BLOCKED,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            PHONE_BLOCKED,
            PhoneBlocked.PHONE_BLOCKED)

        return view
    }

    private fun hideShowPendingTickets() {
        setTextViewDrawableColor(requireContext(),binding.tvPendingTickets,R.color.white)
        setTextViewDrawableColor(requireContext(),binding.tvReleasedTickets,R.color.white)

        if (binding.rvPendingTickets.isVisible) {
            binding.rvPendingTickets.gone()
            binding.tvPendingTickets.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_down,
                0
            )
            
        } else {
            binding.rvPendingTickets.visible()
            binding.tvPendingTickets.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_up_light,
                0
            )

        }
    }

    private fun hideShowReleasedTickets() {
        setTextViewDrawableColor(requireContext(),binding.tvPendingTickets,R.color.white)
        setTextViewDrawableColor(requireContext(),binding.tvReleasedTickets,R.color.white)

        if (binding.rvReleasedTickets.isVisible) {
            binding.rvReleasedTickets.gone()
            binding.tvReleasedTickets.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_down,
                0
            )
            
        } else {
            binding.rvReleasedTickets.visible()
            binding.tvReleasedTickets.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_up_light,
                0
            )
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
        if (isAttachedToActivity() && currentTabPosition == 5) {
//            callPhoneBlockApi()
        }
    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    private fun setPhoneBlockedAdapter() {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvPhoneBlocked.layoutManager = layoutManager
        phoneBlockedAdapter = PhoneBlockedSectionAdapter(
            requireActivity(),
            items,
            null,
            privilegeResponse
        )
        binding.rvPhoneBlocked.adapter = phoneBlockedAdapter
    }

    private fun setPendingTicketsAdapter(pendingTicketsDetail: MutableList<Detail>) {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvPendingTickets.layoutManager = layoutManager
        phoneBlockedAdapter = PhoneBlockedSectionAdapter(
            requireActivity(),
            items,
            pendingTicketsDetail,
            privilegeResponse
        )
        binding.rvPendingTickets.adapter = phoneBlockedAdapter

        if (pendingTicketsDetailList.size != 0) {
            binding.tvPendingTickets.setOnClickListener {
                hideShowPendingTickets()
                if (releasedTicketsDetailList.size != 0) {
                    if (binding.rvPendingTickets.isVisible) {
                        binding.rvReleasedTickets.gone()
                        binding.tvReleasedTickets.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_down,
                            0
                        )
                    }
                    setTextViewDrawableColor(requireContext(),binding.tvPendingTickets,R.color.white)
                    setTextViewDrawableColor(requireContext(),binding.tvReleasedTickets,R.color.white)
                }
            }
        }
    }

    private fun setReleasedTicketsAdapter(releasedTicketsDetail: MutableList<Detail>) {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvReleasedTickets.layoutManager = layoutManager
        phoneBlockedAdapter = PhoneBlockedSectionAdapter(requireActivity(), items, releasedTicketsDetail,privilegeResponse)
        binding.rvReleasedTickets.adapter = phoneBlockedAdapter

        if (releasedTicketsDetailList.size != 0) {
            binding.tvReleasedTickets.setOnClickListener {
                hideShowReleasedTickets()

                if (pendingTicketsDetailList.size != 0) {
                    if (binding.rvReleasedTickets.isVisible) {
                        binding.rvPendingTickets.gone()
                        binding.tvPendingTickets.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_down,
                            0
                        )
                    }
                    setTextViewDrawableColor(requireContext(),binding.tvPendingTickets,R.color.white)
                    setTextViewDrawableColor(requireContext(),binding.tvReleasedTickets,R.color.white)
                }
            }
        }
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        allottedServicesResponseModel = PreferenceUtils.getObject<AllotedServicesResponseModel>("allotted_services_model_dashboard")
        locale = PreferenceUtils.getlang()

        totalOverallServices = PreferenceUtils.getPreference("total_overall_service_size", 0) ?: 0
        dashboardServiceFilterConf =
            PreferenceUtils.getObject<DashboardServiceFilterConf>(getString(R.string.dashboard_service_filter_conf))
                ?: DashboardServiceFilterConf()

        defaultSelection = PreferenceUtils.getPreference(DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION, 1)?.toInt() ?: 1
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = PreferenceUtils.getDashboardCurrentDate()

        if ((activity as BaseActivity).getPrivilegeBase() != null) {
            privilegeResponse = (activity as BaseActivity).getPrivilegeBase()

            if (!privilegeResponse?.country.isNullOrEmpty()) {
                country = privilegeResponse?.country
            }
        }
    }

    private fun callPhoneBlockApi() {
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

            dashboardViewModel.getPhoneBlocked(
                apiKey = loginModelPref.api_key,
                sortBy = sortBy,
                serviceId = serviceId,
                startDate = fromDate,
                endDate = toDate,
                reservationId = "",
                ticketStatusFliter = country != null && country.equals("India", true),
                locale = "en",
                is_from_middle_tier = true,
                methodName = phone_blocked_seats
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                containerPhoneBlockedDashboard.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun setUpObserver() {
        dashboardViewModel.phoneBlockedResponseViewModel.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayoutDashboard.isRefreshing = false
            stopShimmerEffect()

            if (it != null && it.code == 200) {
                if (it.result.isEmpty()) {
                    binding.containerPhoneBlockedDashboard.visible()
                    binding.containerPhoneBlockedDashboardCard.gone()
                    binding.noData.root.visible()
                    binding.noData.tvNoData.text = requireActivity().getString(R.string.no_data_available)
                } else {
                    binding.containerPhoneBlockedDashboard.visible()
                    binding.containerPhoneBlockedDashboardCard.visible()
                    items.clear()
                    items = it.result.toMutableList()
                    setPhoneBlockedAdapter()
                    binding.noData.root.gone()
                }
            } else {
                stopShimmerEffect()
                binding.containerPhoneBlockedDashboard.visible()
                binding.containerPhoneBlockedDashboardCard.gone()
                binding.noData.root.visible()
                binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
            }
        }
    }

    private fun setUpPhoneBlockedIndiaObserver() {
        dashboardViewModel.phoneBlockedResponseViewModel.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayoutDashboard.isRefreshing = false
            stopShimmerEffect()

            if (it != null && it.code == 200) {
                if (it.result.isNotEmpty()) {
                    try {
                        binding.apply {
                            tvPendingTickets.text =
                                "${getString(R.string.pending_seats)} (${it.result[0].totalSeatCount})"
                            tvReleasedTickets.text =
                                "${getString(R.string.release_seats)} (${it.result[1].totalSeatCount})"
                            containerPhoneBlockedDashboard.visible()
                            containerPhoneBlockedDashboardCardIndia.visible()
                            noData.root.gone()

                            if (it.result[0].totalSeatCount != 0) {
                                binding.rvPendingTickets.visible()
                                binding.tvPendingTickets.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.ic_arrow_up_light,
                                    0
                                )
                                setTextViewDrawableColor(requireContext(), binding.tvPendingTickets, R.color.white)
                                setTextViewDrawableColor(requireContext(), binding.tvReleasedTickets, R.color.white)
                            }

                            if (it.result[0].totalSeatCount == 0
                                && it.result[1].totalSeatCount != 0
                            ) {
                                binding.rvReleasedTickets.visible()
                                binding.tvReleasedTickets.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.ic_arrow_up_light,
                                    0
                                )

                                setTextViewDrawableColor(requireContext(), binding.tvPendingTickets, R.color.white)
                                setTextViewDrawableColor(requireContext(), binding.tvReleasedTickets, R.color.white)
                            }
                        }

                        items.clear()
                        pendingTicketsDetailList.clear()
                        releasedTicketsDetailList.clear()

                        items = it.result.toMutableList()
                        pendingTicketsDetailList = it.result[0].details.toMutableList()
                        releasedTicketsDetailList = it.result[1].details.toMutableList()

                        setPendingTicketsAdapter(pendingTicketsDetailList)
                        setReleasedTicketsAdapter(releasedTicketsDetailList)

                    } catch (e: Exception) {
                        requireActivity().toast(getString(R.string.opps))
                    }
                } else {
                    binding.apply {
                        containerPhoneBlockedDashboard.visible()
                        containerPhoneBlockedDashboardCardIndia.gone()
                        noData.root.visible()
                        noData.tvNoData.text = requireActivity().getString(R.string.no_data_available)
                    }
                }
            } else {
                binding.apply {
                    containerPhoneBlockedDashboard.visible()
                    containerPhoneBlockedDashboardCardIndia.gone()
                    noData.root.visible()
                    noData.tvNoData.text = requireActivity().getString(R.string.opps)
                }
            }
        }
    }

    private fun openActivityForResult() {
        val intent = Intent(requireContext(), SearchServiceActivity::class.java)
        startActivityForResult(intent, 2)
        PreferenceUtils.putString(getString(R.string.tag), TAG)
    }

    override fun onSingleButtonClick(str: String) {
        openActivityForResult()
    }

    override fun onItemData(view: View, str1: String, str2: String) {
        serviceIdPref =
            PreferenceUtils.getPreference(PREF_SELECTED_SERVICE_ID_FILTER, serviceId).toString()
        callPhoneBlockApi()
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
    }

    private fun startShimmerEffect() {
        binding.apply {
            shimmerPhoneBlockedDashboard.visible()
            containerPhoneBlockedDashboard.gone()
            shimmerPhoneBlockedDashboard.startShimmer()
            noData.root.gone()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            shimmerPhoneBlockedDashboard.gone()
            if (shimmerPhoneBlockedDashboard.isShimmerStarted) {
                shimmerPhoneBlockedDashboard.stopShimmer()
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
        TODO("Not yet implemented")
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

            serviceId = serviceId.substring(0, serviceId.lastIndexOf(","))

        }
        binding.allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
//        callPhoneBlockApi()

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