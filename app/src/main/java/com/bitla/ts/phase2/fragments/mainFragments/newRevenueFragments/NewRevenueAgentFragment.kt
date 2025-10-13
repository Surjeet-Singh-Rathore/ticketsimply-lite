package com.bitla.ts.phase2.fragments.mainFragments.newRevenueFragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.get_revenue_data_api
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.FragmentRevenueServiceBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.revenue_data.RevenueData
import com.bitla.ts.domain.pojo.revenue_data.RevenueRouteDetails
import com.bitla.ts.phase2.adapter.parent.RevenueServicesAdapter
import com.bitla.ts.phase2.dashboardContainer.activity.RevenueServiceDetailsActivity
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.response.*
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.SearchAgentActivity
import com.bitla.ts.presentation.viewModel.BlockViewModel
import com.bitla.ts.presentation.viewModel.DashboardRevenueViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible

class NewRevenueAgentFragment : BaseFragment(), com.bitla.ts.data.listener.OnItemClickListener {

    private lateinit var binding: FragmentRevenueServiceBinding

    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = ""
    private var adapter: RevenueServicesAdapter? = null
    private val dashboardRevenueViewModel by viewModel<DashboardRevenueViewModel<Any?>>()
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
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
    private var hideLast7DaysDateFilter: Boolean = false
    private var hideLast30DaysDateFilter: Boolean = true
    private var hideCustomDateFilter: Boolean = true
    private var hideCustomDateRangeFilter: Boolean = false
    private var isCustomDateFilterSelected: Boolean = true
    private var isCustomDateRangeFilterSelected: Boolean = false
    private var isLocalFilter = false
    private var allottedServicesResponseModel: AllotedServicesResponseModel? = null

    private var privilegeResponse: PrivilegeResponseModel? = null
    private var dashboardServiceFilterConf = DashboardServiceFilterConf()

    private var serviceId = "-1"
    private var totalOverallServices = 0
    private var selectedServiceName = "All Services"
    var routeList = arrayListOf<RevenueRouteDetails>()
    var journeyBy = "doj"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentRevenueServiceBinding.inflate(inflater, container, false)
        getPref()

        getRevenueDataObserver()
        setAllServices()











        binding.allServicesAndDateFilterContainer.tvDate.setOnClickListener {

            getDateFilter()
        }





        binding.allServicesAndDateFilterContainer.tvAllService.setOnClickListener {

            val intent: Intent = Intent(
                requireContext(),
                SearchAgentActivity::class.java
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
            intent.putExtra("isFromAgentRevenue", true)
            startActivityForResult(intent, SELECT_SERVICE_INTENT_REQUEST_CODE)

        }


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

    private fun getAgentCount() {
        blockViewModel.userListApi(loginModelPref.api_key, "", "1", "", locale!!, "user_list")
    }


    @SuppressLint("SetTextI18n")
    private fun agentListObserver() {
        blockViewModel.userList.observe(this) {
            try {
                if (it.code == 200) {
                    if (it.active_users.isNotEmpty()) {
                        binding.allServicesAndDateFilterContainer.tvAllService.text =
                            "${getString(R.string.agent)} (${it.active_users.size})"

                        totalOverallServices = it.active_users.size
                    }
                } else if(it.code == 401) {
                    //openUnauthorisedDialog()
                    (activity as BaseActivity).showUnauthorisedDialog()
                }

            } catch (t: Throwable) {
                Timber.d("exceptionMsgUser ${t.message}")
                requireContext().toast("An error occurred while fetching Agent List")
            }
        }
    }


    private fun setAdapter(revenueRouteList: ArrayList<RevenueRouteDetails>?) {
        if (revenueRouteList?.size!! > 0) {
            adapter = RevenueServicesAdapter(
                requireContext(),
                revenueRouteList,
                privilegeResponse,
                this
            )
            binding.mainRV.adapter = adapter
        }

    }


    private fun callGetRevenueDataApi() {
        binding.progressBar.visible()
        binding.mainRV.gone()
        binding.totalRevenueValueTV.text = privilegeResponse?.currency + " 0"
        dashboardRevenueViewModel.getRevenueData(
            loginModelPref.api_key,
            fromDate,
            toDate,
            "-1",
            journeyBy,
            "true",
            "20",
            "1",
            "3",
            serviceId,
            "",
            get_revenue_data_api
        )

    }

    override fun isInternetOnCallApisAndInitUI() {
        if (isAttachedToActivity()) {
        }
    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardDateSetText(
            textView = binding.allServicesAndDateFilterContainer.tvDate,
            fromDate = fromDate,
            toDate = null,
            inputDateFormat = DATE_FORMAT_Y_M_D
        )


        lifecycleScope.launch {
            blockViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            dashboardRevenueViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    @SuppressLint("SetTextI18n")


    // simulate api call


    private fun getPref() {
        startShimmerEffect()
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()

        locale = PreferenceUtils.getlang()


        privilegeResponse = (activity as BaseActivity).getPrivilegeBase()

        allottedServicesResponseModel =
            PreferenceUtils.getObject<AllotedServicesResponseModel>("allotted_services_model_dashboard")
        totalOverallServices = PreferenceUtils.getPreference("total_overall_service_size", 0) ?: 0
        dashboardServiceFilterConf =
            PreferenceUtils.getObject<DashboardServiceFilterConf>(getString(R.string.dashboard_service_filter_conf))
                ?: DashboardServiceFilterConf()
        currentDate = PreferenceUtils.getDashboardCurrentDate()
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = fromDate
    }


    private fun getRevenueDataObserver() {
        dashboardRevenueViewModel.revenueData.observe(requireActivity(), Observer {

            if (it != null) {
                binding.progressBar.gone()
                when (it.code) {
                    200 -> {
                        setData(it)
                        binding.noData.root.gone()
                        binding.mainRV.visible()
                        stopShimmerEffect()

                    }

                    401 -> {
                       // openUnauthorisedDialog()

                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        binding.noData.root.visible()
                        binding.mainRV.gone()
                        requireActivity().toast(it.message)

                    }
                }
            } else
                requireActivity().toast(getString(R.string.server_error))

        })
    }

    @SuppressLint("SetTextI18n")
    private fun setData(revenueData: RevenueData) {
        routeList.clear()
        routeList = revenueData.revenueRouteDetails ?: arrayListOf()
        binding.totalRevenueValueTV.text =
            privilegeResponse?.currency + revenueData.totalRevenue?.toDouble()
                ?.convert(
                    privilegeResponse?.currencyFormat
                        ?: requireActivity().getString(R.string.indian_currency_format)
                )
        setAdapter(routeList)

//        binding.allServicesAndDateFilterContainer.tvAllService.text=
//            "${requireContext().getString(R.string.all_services)}(${revenueData.totalService.toString()})"

    }


    private fun startShimmerEffect() {
//        shimmerLayout.visible()
//        shimmerLayout.startShimmer()

    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
//        shimmerLayout.gone()
//
//        if (shimmerLayout.isShimmerStarted) {
//           shimmerLayout.stopShimmer()
//
//        }
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
                "${getString(R.string.all_agent)} ($totalOverallServices)"

        }

        if (serviceId == "-1" && totalSelectedServices == totalOverallServices) {

            selectedServiceName =
                "${getString(R.string.all_agent)} (${totalOverallServices})"

        } else {

            selectedServiceName = "${getString(R.string.agent)} ($totalSelectedServices)"

            serviceId =
                serviceId.substring(0, serviceId.lastIndexOf(","))

        }
        binding.allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
        callGetRevenueDataApi()

    }


    private fun getDateFilter() {
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


                    if (fromDate.contains("/")) {
                        val checkedId = fromDate.substringAfter("/")
                        fromDate = fromDate.substringBefore("/")
                        when (checkedId.toInt()) {
                            R.id.dojRB -> {
                                journeyBy = "doj"
                                binding.forDateJourneyTV.text =
                                    requireContext().getString(R.string.for_date_of_journey)
                            }

                            R.id.doiRB -> {
                                journeyBy = "doi"
                                binding.forDateJourneyTV.text =
                                    requireContext().getString(R.string.for_date_of_issue)
                            }
                        }
                    }

                    isLocalFilter = true
                    revenueDateSetText(fromDate, toDate, DATE_FORMAT_Y_M_D)
                    callGetRevenueDataApi()
                }
            },
            true,
            journeyBy

        )
    }


    private fun revenueDateSetText(
        fromDate: String,
        toDate: String?,
        inputDateFormat: String
    ) {
        var text = ""
        if (toDate != null) {
            if (toDate != "") {
                text = "${
                    getCurrentFormattedDate(
                        fromDate,
                        inputDateFormat,
                        DATE_FORMAT_MMM_DD
                    )
                } - ${getCurrentFormattedDate(toDate, inputDateFormat, DATE_FORMAT_MMM_DD)}"
            } else {
                text = getCurrentFormattedDate(fromDate, inputDateFormat, DATE_FORMAT_D_MMM_Y)
            }
        } else {
            text = getCurrentFormattedDate(fromDate, inputDateFormat, DATE_FORMAT_D_MMM_Y)
        }

        binding.allServicesAndDateFilterContainer.tvDate.text = text
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == SELECT_SERVICE_INTENT_REQUEST_CODE) {
            data?.let { it ->
                val allottedServiceString = it.getStringExtra("allotted_service_response")
                val allottedResponseModelString =
                    allottedServiceString?.let { stringToJson<AllotedServicesResponseModel>(it) }

                allottedServicesResponseModel = allottedResponseModelString

                if (it.hasExtra(getString(R.string.dashboard_service_filter_conf))) {
                    val dashboardServiceFilterConfString: String =
                        it.getStringExtra(getString(R.string.dashboard_service_filter_conf)) ?: ""
                    dashboardServiceFilterConf = stringToJson(dashboardServiceFilterConfString)
                }

                setAllServices()
            }
        }
    }


    override fun onClick(view: View, position: Int) {
        super.onClick(view, position)
        when (view.id) {
            R.id.rightArrowIV, R.id.cardCL -> {

                val intent = Intent(context, RevenueServiceDetailsActivity::class.java)
                intent.putExtra("fromDate", fromDate)
                intent.putExtra("toDate", toDate)
                intent.putExtra("routeId", routeList[position].routeId)
                intent.putExtra("agentId", routeList[position].routeId)
                intent.putExtra("title", routeList[position].name)
                intent.putExtra("date", binding.allServicesAndDateFilterContainer.tvDate.text)
                intent.putExtra("journeyBy", journeyBy)
                context?.startActivity(intent)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (PreferenceUtils.getRevenueFilterList().size == 0) {
            getAgentCount()
            agentListObserver()
        }
    }



}