package com.bitla.ts.phase2.fragments.mainFragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.data.pending_quota_seats
import com.bitla.ts.databinding.FragmentPendingQuotaBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.phase2.adapter.child.PendingQuotaSectionAdapter
import com.bitla.ts.phase2.dashboardContainer.activity.*
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.request.PendingQuotaRequest
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.request.ReqBody
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response.PassengerDetail
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response.PendingQuotaResponse
import com.bitla.ts.presentation.view.activity.PendingQuotaDetailsActivity
import com.bitla.ts.presentation.view.activity.SearchActivity
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D
import com.bitla.ts.utils.constants.PENDING_QUOTA
import com.bitla.ts.utils.constants.PendingQuote
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION
import com.bitla.ts.utils.sharedPref.PREF_LOCALE
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_SERVICE_ID_FILTER_PENDING_QUOTA
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.gson.Gson
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible

class PendingQuotaFragment : BaseFragment(), DialogSingleButtonListener, OnItemPassData,
    OnItemClickListener {

    companion object {
        val TAG = PendingQuotaFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentPendingQuotaBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var pendingQuotaAdapter: PendingQuotaSectionAdapter
    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var sortBy = "htol"
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var isAllService = true
    private var serviceIdPref: String = ""
    private var fromDate = ""
    private var toDate = ""
    private var locale: String? = ""
    private var items: MutableList<com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response.Service> =
        mutableListOf()
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
    private var currentTabPosition: Int? = 0
    private var list = mutableListOf<PassengerDetail>()
    private lateinit var  pendingQuotaResponseData: PendingQuotaResponse

    override fun onResume() {
        super.onResume()
        currentTabPosition = (activity as? DashboardDetailsActivity)?.findViewById<ViewPager2>(R.id.viewPagerDashboard)?.currentItem
//        if (currentTabPosition == 6) {
//            if (isAttachedToActivity()) {
//                callPendingQuotaApi()
//            }
//        }
    }
    
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPendingQuotaBinding.inflate(inflater, container, false)
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

        if (defaultSelection == 5) {
            isCustomDateFilterSelected = true
        }

        if (defaultSelection == 6) {
            isCustomDateRangeFilterSelected = true
        }
        
        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            binding.swipeRefreshLayoutDashboard.isRefreshing = true
            callPendingQuotaApi()
        }

        binding.apply {

/*
            btnSort.setOnClickListener {
                DialogUtils.dialogSortBy(
                    requireContext(),
                    sortBy,
                    object : DialogSingleButtonListener {
                        override fun onSingleButtonClick(str: String) {
                            sortBy = str
                            callPendingQuotaApi()
                        }
                    }
                )
            }
*/

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

                            callPendingQuotaApi()


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
            PENDING_QUOTA,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            PENDING_QUOTA,
            PendingQuote.PENDING_QUOTA
        )

        callPendingQuotaApi()
        setUpObserver()
        setAllServices()

        return view
    }

    override fun isInternetOnCallApisAndInitUI() {
//        if (isAttachedToActivity()) {
//            callPendingQuotaApi()
//        }
    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    private fun setPendingQuotaAdapter() {

        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvPendingQuota.layoutManager = layoutManager
        pendingQuotaAdapter = PendingQuotaSectionAdapter(requireActivity(), items,this)
        binding.rvPendingQuota.adapter = pendingQuotaAdapter

//        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
//        binding.rvPhoneBlocked.layoutManager = layoutManager
//        phoneBlockedAdapter = PhoneBlockedSectionAdapter(requireActivity(), items)
//        binding.rvPhoneBlocked.adapter = phoneBlockedAdapter
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
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = PreferenceUtils.getDashboardCurrentDate()

    }

    private fun callPendingQuotaApi() {
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
                    sortBy = sortBy,
                    serviceId = serviceId,
                    startDate = fromDate,
                    endDate = toDate,
                )

            val pendingQuotaRequest =
                PendingQuotaRequest(
                    bccId = bccId.toString(),
                    format = format_type,
                    pending_quota_seats,
                    reqBody = reqBody
                )

            dashboardViewModel.pendingQuota(
                pendingQuotaRequest = reqBody,
                apiType = pending_quota_seats
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                containerPendingQuotaDashboard.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun setUpObserver() {
        dashboardViewModel.pendingQuotaResponseViewModel.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayoutDashboard.isRefreshing = false
            stopShimmerEffect()

            if (it != null && it.code == 200) {
                pendingQuotaResponseData = it

                try {
                    if (!it.result.isNullOrEmpty() && it.result[0].services.isEmpty()) {
                        binding.apply {
                            containerPendingQuotaDashboard.visible()
                            containerPendingQuotaDashboardCard.gone()
                            noData.root.visible()
                            noData.tvNoData.text = requireContext().getString(R.string.no_data_available)
                        }

                    } else {
                        binding.apply {
                            noData.root.gone()
                            containerPendingQuotaDashboard.visible()
                            containerPendingQuotaDashboardCard.visible()
                        }
                        items.clear()
                        items = it.result[0].services.toMutableList()
                        setPendingQuotaAdapter()
                    }
                }catch (e: Exception){
                    requireActivity().toast(e.message)
                }

            } else {
                stopShimmerEffect()
                binding.noData.root.visible()
                binding.containerPendingQuotaDashboard.visible()
                binding.containerPendingQuotaDashboardCard.gone()
                binding.containerPendingQuotaDashboard2.gone()
                binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
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
            PreferenceUtils.getPreference(PREF_SELECTED_SERVICE_ID_FILTER_PENDING_QUOTA, serviceId)
                .toString()
        callPendingQuotaApi()
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
    }


    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }


    override fun onClickOfItem(data: String, position: Int) {

        val pendingQuotaResponseModel: PendingQuotaResponse = pendingQuotaResponseData
        val jsonString = jsonToString(pendingQuotaResponseModel)
        PreferenceUtils.putString(getString(R.string.pending_quota_model_dashboard), jsonString)

        val intent = Intent(requireContext(), PendingQuotaDetailsActivity::class.java)
        intent.putExtra(getString(R.string.pending_quota), position)
        startActivity(intent)
    }

    private fun startShimmerEffect() {
        binding.apply {
            shimmerPendingQuotaDashboard.visible()
            containerPendingQuotaDashboard.gone()
            shimmerPendingQuotaDashboard.startShimmer()
            noData.root.gone()
        }
    }

    private fun stopShimmerEffect() {

        binding.apply {
            shimmerPendingQuotaDashboard.gone()
            containerPendingQuotaDashboard.visible()
            if (shimmerPendingQuotaDashboard.isShimmerStarted) {
                shimmerPendingQuotaDashboard.stopShimmer()
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
//        callPendingQuotaApi()
        
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