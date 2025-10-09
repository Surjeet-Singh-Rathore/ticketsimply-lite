package com.bitla.ts.presentation.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateListOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.alloted_Service_method_name
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.branch_list_method_name
import com.bitla.ts.data.bus_service_collection_name
import com.bitla.ts.data.cargo_booking_report_method_name
import com.bitla.ts.data.checking_inspector_report_name
import com.bitla.ts.data.format_type
import com.bitla.ts.data.fuel_transaction_detail_name
import com.bitla.ts.data.group_by_branch_report_method_name
import com.bitla.ts.data.is_middle_tier
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.occupancy_report_name
import com.bitla.ts.data.payemnt_status_report_method_name
import com.bitla.ts.data.restaurant_meal_report_method_name
import com.bitla.ts.data.route_wise_booking_memo_name
import com.bitla.ts.data.service_wise_city_pickup_name
import com.bitla.ts.data.starred_reports_name
import com.bitla.ts.data.tickets_booked_by_you_method_name
import com.bitla.ts.data.user_collection_details_name
import com.bitla.ts.data.user_list_method_name
import com.bitla.ts.databinding.DialogProgressBarBinding
import com.bitla.ts.databinding.LayoutReportsFragmentBinding
import com.bitla.ts.domain.pojo.BranchModel.Branch
import com.bitla.ts.domain.pojo.BranchModel.BranchList
import com.bitla.ts.domain.pojo.SpinnerItems
import com.bitla.ts.domain.pojo.all_reports.AllReports
import com.bitla.ts.domain.pojo.all_reports.all_report_request.AllReportRequest
import com.bitla.ts.domain.pojo.all_reports.all_report_request.Report
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.alloted_services.request.AllotedServiceRequest
import com.bitla.ts.domain.pojo.alloted_services.request.ReqBody
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.coach_list.Coach
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.starred_reports.StarredReport
import com.bitla.ts.domain.pojo.starred_reports.request.StarredReportsRequest
import com.bitla.ts.presentation.adapter.ReportsDateAdapter
import com.bitla.ts.presentation.view.activity.BookedByYouReportActivity
import com.bitla.ts.presentation.view.activity.BranchCollectionDetailsReportActivity
import com.bitla.ts.presentation.view.activity.BranchCollectionSummaryReportActivity
import com.bitla.ts.presentation.view.activity.BusServiceCollectionDetailedReportActivity
import com.bitla.ts.presentation.view.activity.BusServiceCollectionSummaryReportActivity
import com.bitla.ts.presentation.view.activity.CheckingInspectorReportActivity
import com.bitla.ts.presentation.view.activity.GroupByBranchReportActivity
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.MealsReportActivity
import com.bitla.ts.presentation.view.activity.OccupancyReportActivity
import com.bitla.ts.presentation.view.activity.RoutewiseBookingMemoActivity
import com.bitla.ts.presentation.view.activity.SearchActivity
import com.bitla.ts.presentation.view.activity.SearchBranchActivity
import com.bitla.ts.presentation.view.activity.SearchMultiSelectServiceActivity
import com.bitla.ts.presentation.view.activity.SelectAllotedServiceActivity
import com.bitla.ts.presentation.view.activity.ServiceWiseCityPickupClosureReportActivity
import com.bitla.ts.presentation.view.activity.ticketDetails.SelectRestaurantActivity
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.viewModel.AllReportsViewModel
import com.bitla.ts.presentation.viewModel.BlockViewModel
import com.bitla.ts.presentation.viewModel.MyBookingsViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.presentation.viewModel.RestaurantViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.presentation.viewModel.StarredReportsViewModel
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getDateDMYY
import com.bitla.ts.utils.common.getDateYMD
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.common.getUserRole
import com.bitla.ts.utils.common.jsonToString
import com.bitla.ts.utils.common.saveAgentList
import com.bitla.ts.utils.common.saveUserList
import com.bitla.ts.utils.common.setDateLocale
import com.bitla.ts.utils.common.stringToJson
import com.bitla.ts.utils.common.toDp
import com.bitla.ts.utils.constants.ADD_FAVORITES_REPORT_CHECK
import com.bitla.ts.utils.constants.ADD_REPORT_STARRED_TAB
import com.bitla.ts.utils.constants.ALL_REPORTS
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.constants.DOWNLOAD_REPORT
import com.bitla.ts.utils.constants.FROM_DATE
import com.bitla.ts.utils.constants.GENERATE_DOWNLOAD_REPORT_CLICKS
import com.bitla.ts.utils.constants.Pagination
import com.bitla.ts.utils.constants.REPORT_SERVICE_SELECTION
import com.bitla.ts.utils.constants.REPORT_TYPE_SELECTION
import com.bitla.ts.utils.constants.RESULT_CODE_SEARCH_AGENT
import com.bitla.ts.utils.constants.RESULT_CODE_SEARCH_BRANCH
import com.bitla.ts.utils.constants.RESULT_CODE_SEARCH_USER
import com.bitla.ts.utils.constants.ReportDateType
import com.bitla.ts.utils.constants.SELECT_BRANCH_INTENT_REQUEST_CODE
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.constants.STORAGE_PERMISSION_CODE
import com.bitla.ts.utils.constants.TO_DATE
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.downloadPdf.DownloadPdf
import com.bitla.ts.utils.sharedPref.PREF_BRANCH_LIST_REPORT_MODEL
import com.bitla.ts.utils.sharedPref.PREF_BRANCH_REPORT_DATA
import com.bitla.ts.utils.sharedPref.PREF_DATE_TYPE_ISSUE_TRAVEL_DATE
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FragmentReports : BaseFragment(), View.OnClickListener, OnItemClickListener,
    DialogSingleButtonListener {

    companion object {
        val TAG = FragmentReports::class.java.simpleName
    }

    private var ticketBookByYouReqBody: String = ""
    private var checkingInspectorReqBody: String = ""
    private var serviceWiseCityPickupClosureReqBody: String = ""
    private var busServiceCollectionReqBody: String = ""
    private var groupByBranchReqBody: String = ""
    private var occupancyReqBody: String = ""
    private var reqBodyToSend: String = ""
    private var currentCountry: String? = ""
//    private var privileges: PrivilegeResponseModel? = null
    private val isPdfDownload: Boolean = true //fixed
    private val responseFormat: String = "hash" // fixed
    private val isExportPdf: String = "true" // fixed
    private val reportId: String = "" // fixed
    private val busGroups: String = "" // fixed
    private val hubOptions: String = "-1" // fixed
    private var isStarredCheck: Boolean = false
    private var isViewReport: Boolean = false
    private val dateRange: String =
        "4" // fixed (date range 4 means , the API request has both from and to date parameters)
    private var dateWise: String = "2" // 1 -> travel_date , 2 -> issue_date
    private var occupancyType: String = "1"
    private var allReportSuccessResponse: AllReports? = null
    private var resID: String? = null
    private lateinit var binding: LayoutReportsFragmentBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var favouriteReportsAdapter: ReportsDateAdapter
    private var loginModelPref: LoginModel = LoginModel()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private val allReportsViewModel by viewModel<AllReportsViewModel<Any?>>()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private val restaurantViewModel by viewModel<RestaurantViewModel>()
    private var apiKey: String? = null
    private var bccId: String? = null
    private var returnedRouteId: String = ""
    private var returnApiSelected: String = ""

    private var restaurantId="-1"
    private var restaurantName=""
    private var day = 0
    private var month: Int = 0
    private var ReportApi: String = ""
    private var servicesList: MutableList<SpinnerItems> = mutableListOf()
    private var year: Int = 0
    private var selectedReportTypeValue = ""
    private var locale: String? = ""
    private val myBookingsViewModel by viewModel<MyBookingsViewModel<Any>>()
    private var roleNames: String? = null
    private var isAgentLogin: Boolean = false
//    private var selectedRouteId: String = ""

    private lateinit var mcalendar: Calendar
    private val starredReportsViewModel by viewModel<StarredReportsViewModel<Any?>>()

    private var reportTypeList = mutableListOf<String>()
    private var paymentTypeList = arrayOf<String>()
    private var isBranchCollectionReport = false
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()

    //    private var branchList: MutableList<SpinnerItems> = mutableStateListOf()
    private var branchList: MutableList<Branch> = mutableListOf()
    private var userList: MutableList<SpinnerItems> = mutableStateListOf()
    private var selectedBranchId: String = ""
    private var selectedBranchName: String = ""
    private var selectedUserName: String = ""
    private var selectedUserId: Int = 0
    private var selectedBookingTypeId: Int? = null
    private var selectedBookingType: String = ""
    private var isDetailsReport: Boolean = true
    private var allottedServicesResponseModel: AllotedServicesResponseModel? = null
    private var serviceSize: Int? = 0
    private var branchSize: Int? = 0
    private var fromDate = ""
    private var toDate = ""
    private var selectedDate = ""
    private var dateOptions: List<String> = listOf()
    private var endDate = ""
    private var startDate = ""
    private var date = ""
    private var coachList: MutableList<String> = mutableStateListOf()
    private var coachListData: MutableList<Coach> = mutableListOf()
    private var selectedCoachId: String = ""
    private var selectedCoachName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutReportsFragmentBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun isInternetOnCallApisAndInitUI() {
        callStarredReportsApi()
    }

    override fun isNetworkOff() {
    }

    private fun init() {

        (activity as? DashboardNavigateActivity)?.reduceMarginTop()

        getPref()
        lifecycleScope.launch {
            blockViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            allReportsViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                    dismissProgressDialog()
                }
            }
        }
        lifecycleScope.launch {
            starredReportsViewModel.messageSharedFlow.collect {
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
        onEditFields()
        resID = binding.acSelectService.text.toString()
        setAllottedDetailObserver()
        setAllReportsObserver()
        setTicketBookedByYouNewObserver()
        setPaymentStatusReportObserver()
        setRouteWiseBookingMemoObserver()
        setUpOccupancyReportViewObserver()
        setCheckingInspectorReportObserver()
        setServiceWisePickupReportObserver()
        setGroupByBranchReportObserver()
        setBusServiceCollectionReportObserver()
        setRestaurantMealReportObserver()
//        setBranchCollectionSummaryReportObserver()
//        setOccupancyReportObserver()
        setBranchObserver()
        setUserObserver()
        setupCoachListObserver()
        PreferenceUtils.setPreference(
            PREF_RESERVATION_ID, ""
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val ime = insets.getInsets(WindowInsetsCompat.Type.ime()) // 👈 keyboard
                val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

                val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

                view.setPadding(
                    systemBars.left,
                    0, // status bar handled visually
                    systemBars.right,
                    if (isKeyboardVisible) ime.bottom else systemBars.bottom
                )


                insets
            }


        }

        allReportsViewModel.privilegesLiveData.observe(requireActivity()) { privileges ->

            if (privileges != null && privileges?.userWiseCollectionReport != null && privileges?.userWiseCollectionReport==true)
                reportTypeList.add(getString(R.string.branch_collection_report))
            if (privileges != null && privileges?.isServiceWiseCityPickupClosureReport != null && privileges?.isServiceWiseCityPickupClosureReport==true)
                reportTypeList.add(getString(R.string.service_wise_city_pickup_report))
            if (privileges != null && privileges?.showCheckingInspectorReport != null && privileges?.showCheckingInspectorReport==true)
                reportTypeList.add(getString(R.string.checking_inspector_report))
            if (privileges != null && privileges?.fuelUtilityReport != null && privileges?.fuelUtilityReport==true)
                reportTypeList.add(getString(R.string.fuel_utility_report))
            if (privileges != null && privileges?.occupancyReport != null && privileges?.occupancyReport==true)
                reportTypeList.add(getString(R.string.occupancy_report))
            if (privileges != null && privileges?.ticketBookedByYou != null && privileges?.ticketBookedByYou==true)
                reportTypeList.add(getString(R.string.tickets_booked_by_you))
            if (privileges != null && privileges?.busServiceReport != null && privileges?.busServiceReport==true)
                reportTypeList.add(getString(R.string.bus_service_collection))
            if (privileges != null && privileges?.isAllowGroupByBranch != null && privileges?.isAllowGroupByBranch==true)
                reportTypeList.add(getString(R.string.group_by_branch_report))
            if (privileges != null && privileges?.cargoBookingReport != null && privileges?.cargoBookingReport==true)
                reportTypeList.add(getString(R.string.cargo_booking_report))
            if (privileges != null && privileges?.routeWiseBookingMemoForTsApp != null && privileges?.routeWiseBookingMemoForTsApp==true)
                reportTypeList.add(getString(R.string.route_wise_booking_memo))
            if (privileges != null && privileges?.paymentStatusReport != null && privileges?.paymentStatusReport==true)
                reportTypeList.add(getString(R.string.payment_status_report))

//        if (privileges != null && privileges?.paymentStatusReport != null && privileges?.paymentStatusReport==true)

            if(privileges != null && privileges?.mealReport != null && privileges?.mealReport==true){
                reportTypeList.add(getString(R.string.restaurant_report))}


            showProgressDialog(requireContext())
            if(privileges?.country.equals("India",true)) {
                callAllottedServiceApi(
                    originId = "",
                    destinationId = "",
                    hubId = "",
                    isGroupByHubs = false
                )
            }
            callBranchListApi()
        }


        binding.chkStarred.setOnCheckedChangeListener { buttonView, isChecked ->
            isStarredCheck = isChecked

            firebaseLogEvent(
                requireContext(),
                ADD_REPORT_STARRED_TAB,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                ADD_REPORT_STARRED_TAB,
                "Add this report to starred tab click"
            )

        }

        dateOptions = listOf(
            getString(R.string.today),
            getString(R.string.yesterday),
            getString(R.string.last_seven_days),
            getString(R.string.custom_date)
        )

        binding.acSelectCustomDate.setOnItemClickListener { _, _, position, _ ->
            val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y, Locale.getDefault())
            when (dateOptions[position]) {
                getString(R.string.today) -> {
                    binding.layoutFromToDate.gone()
                    val today = Calendar.getInstance()
                    selectedDate = dateFormat.format(today.time)
                    binding.acSelectCustomDate.setText(dateOptions[0])
                }
                getString(R.string.yesterday) -> {
                    binding.layoutFromToDate.gone()
                    val yesterday = Calendar.getInstance()
                    yesterday.add(Calendar.DAY_OF_YEAR, -1)
                    selectedDate = dateFormat.format(yesterday.time)
                    binding.acSelectCustomDate.setText(dateOptions[1])
                }
                getString(R.string.last_seven_days) -> {
                    binding.layoutFromToDate.gone()
                    val calendar = Calendar.getInstance()
                    endDate = dateFormat.format(calendar.time)
                    calendar.add(Calendar.DAY_OF_YEAR, -6)
                    startDate = dateFormat.format(calendar.time)
                    binding.acSelectCustomDate.setText(dateOptions[2])
                }
                getString(R.string.custom_date) -> {
                    binding.acSelectCustomDate.setText(dateOptions[3])
                    binding.layoutFromToDate.visible()
                }
            }
            setDateChoiceSelectionAdapter()
        }

        binding.detailSummaryRG.setOnCheckedChangeListener { radioGroup, i ->
            isDetailsReport = when (i) {
                R.id.detailedRB -> {
                    true
                }

                R.id.summaryRB -> {
                    false
                }

                else -> {
                    true
                }
            }
        }

        if (roleNames.equals(getString(R.string.role_agent)))
        {
            binding.acSelectReportType.setText(getString(R.string.tickets_booked_by_you))
            selectedReportTypeValue = binding.acSelectReportType.text.toString()
            binding.acSelectService.setText(getString(R.string.all_service))
            returnedRouteId = ""
            returnApiSelected = getString(R.string.all_service)

            if (currentCountry != null && currentCountry.equals("indonesia", true)) {

                binding.btnViewReport.visible()
                binding.rgDate.visible()
            }

        }

        binding.acSelectReportType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                selectedReportTypeValue = binding.acSelectReportType.text.toString()

                if (reportTypeList[position] == getString(R.string.branch_collection_report)
                    || reportTypeList[position] == getString(
                        R.string.occupancy_report
                    )
                    || reportTypeList[position] == getString(R.string.tickets_booked_by_you)
                    || reportTypeList[position] == getString(
                        R.string.bus_service_collection
                    )
                    || reportTypeList[position] == getString(
                        R.string.cargo_booking_report
                    )
                ) {
//                    binding.acSelectService.setText(getString(R.string.all_service))
                    binding.acSelectService.visible()
                    binding.acSelectService.setText("All Services (${serviceSize})")
                    binding.spinnerSelectPaymentType.gone()
                    binding.spinnerSelectRestaurantTIL.gone()

                    if(reportTypeList[position] == getString(R.string.bus_service_collection)) {
                        binding.apply {
                            if (currentCountry != null && currentCountry.equals("india", true)) {
                                spinnerSelectCoach.visible()
                                btnDownload.text = getString(R.string.download_report)
                                btnViewReport.visible()
                                detailSummaryRG.visible()
                                spinnerSelectCustomDate.visible()
                            } else {
                                spinnerSelectCoach.gone()
                                btnViewReport.gone()
                                detailSummaryRG.gone()
                                spinnerSelectCustomDate.gone()
                            }
                            acSelectCoach.setText(R.string.all_coach)
                            binding.acSelectCustomDate.setAdapter(null)
                            setDateChoiceSelectionAdapter()
                        }
                    } else {
                        binding.acSelectCustomDate.setAdapter(null)
                        binding.spinnerSelectCustomDate.gone()
                    }

                    returnedRouteId = ""
                    returnApiSelected = getString(R.string.all_service)

                }else if(reportTypeList[position] == getString(
                        R.string.payment_status_report
                    )){
                    binding.spinnerSelectService.gone()
                    binding.spinnerSelectPaymentType.visible()
                }
                else {
                    binding.acSelectService.setText("")
                }

                if (reportTypeList[position].equals(
                        getString(R.string.route_wise_booking_memo),
                        true
                    )
                ) {
                    binding.btnDownload.text = getString(R.string.generate_report)
                    binding.btnViewReport.gone()
                } else if (reportTypeList[position] == getString(R.string.branch_collection_report)
                    || reportTypeList[position] == getString(R.string.occupancy_report)
                ) {
                    binding.btnDownload.text = getString(R.string.download_report)
                    if (currentCountry != null && currentCountry.equals("india", true)) {
                        binding.btnViewReport.visible()
                    } else {
                        binding.btnViewReport.gone()

                    }
                } else if (reportTypeList[position] == getString(R.string.tickets_booked_by_you)) {
                    binding.btnDownload.text = getString(R.string.download_report)
                    if (currentCountry != null && currentCountry.equals("india", true)) {
                        binding.btnViewReport.visible()
                    } else {
                        if (currentCountry != null && currentCountry.equals("Malaysia", true)){
                            binding.btnViewReport.gone()
                        }else{
                            binding.btnViewReport.visible()
                        }
                    }
                }
                else if (reportTypeList[position] == getString(R.string.payment_status_report)) {

                    binding.apply {
                        btnViewReport.visible()
                        btnDownload.isEnabled = true
                        btnViewReport.isEnabled = true
                        btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                        btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                        btnDownload.setTextColor(resources.getColor(R.color.white))
                        btnViewReport.setTextColor(resources.getColor(R.color.white))
                        btnDownload.text = getString(R.string.download_report)
                    }
                }

                else if (reportTypeList[position] == getString(R.string.restaurant_report)) {

                    binding.apply {
                        btnViewReport.visible()
                        btnDownload.isEnabled = true
                        btnViewReport.isEnabled = true
                        btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                        btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                        btnDownload.setTextColor(resources.getColor(R.color.white))
                        btnViewReport.setTextColor(resources.getColor(R.color.white))
                        btnDownload.text = getString(R.string.download_report)

                        binding.acSelectService.visible()
                        binding.acSelectService.setText("All Services (${serviceSize})")
                        binding.spinnerSelectPaymentType.gone()
                        returnedRouteId = ""
                        returnApiSelected = getString(R.string.all_service)
                        binding.spinnerSelectRestaurantTIL.visible()
                        binding.acSelectRestaurant.setText("All Restaurants")
                        binding.rgDate.gone()
                    }
                }

                else if (reportTypeList[position] == getString(R.string.checking_inspector_report) ||
                    reportTypeList[position] == getString(R.string.service_wise_city_pickup_report) ||
                    reportTypeList[position] == getString(R.string.bus_service_collection) ||
                    reportTypeList[position] == getString(R.string.group_by_branch_report)
                    )
                {
                    if (currentCountry != null && currentCountry.equals("india", true)) {
                        binding.apply{
                            btnViewReport.visible()
                            btnDownload.text = getString(R.string.download_report)
                        }
                    }
                }

                else {
                    binding.btnDownload.text = getString(R.string.generate_report_download)
                    binding.btnViewReport.gone()
                }

                if (reportTypeList[position] == getString(R.string.tickets_booked_by_you) || reportTypeList[position] == getString(R.string.payment_status_report)) {
                    binding.rgDate.visible()
                } else {
                    binding.rgDate.gone()

                }

                if (reportTypeList[position] == getString(R.string.branch_collection_report)) {
                    if (currentCountry != null && currentCountry.equals("india", true)) {
                        binding.branchListTIL.visible()
                        binding.userListTIL.visible()
                        binding.detailSummaryRG.visible()
                    } else {
                        binding.branchListTIL.gone()
                        binding.userListTIL.gone()
                        binding.detailSummaryRG.gone()
                    }
                }
            }

        startShimmerEffect()
        swipeRefreshLayout()
       // binding.rbIssueDate.isChecked = true
        binding.rbTravelDate.isChecked = true
        binding.rgDate.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rbIssueDate -> {
                    dateWise = "2"
                }

                R.id.rbTravelDate -> {
                    dateWise = "1"
                }
            }
        }

        binding.acSelectService.setOnClickListener {

            when (binding.acSelectReportType.text.toString()) {
                getString(R.string.branch_collection_report) -> ReportApi =
                    user_collection_details_name

                getString(R.string.tickets_booked_by_you) -> ReportApi =
                    tickets_booked_by_you_method_name

                getString(R.string.fuel_utility_report) -> {
                    ReportApi = fuel_transaction_detail_name
                }

                getString(R.string.occupancy_report) -> ReportApi = occupancy_report_name
                getString(R.string.service_wise_city_pickup_report) -> ReportApi =
                    service_wise_city_pickup_name

                getString(R.string.checking_inspector_report) -> ReportApi =
                    checking_inspector_report_name

                getString(R.string.bus_service_collection) -> ReportApi =
                    bus_service_collection_name

                getString(R.string.group_by_branch_report) -> ReportApi =
                    group_by_branch_report_method_name

                getString(R.string.cargo_booking_report) -> ReportApi =
                    cargo_booking_report_method_name

                getString(R.string.route_wise_booking_memo) -> ReportApi =
                    route_wise_booking_memo_name
            }
            if (currentCountry != null && currentCountry.equals("india", true)){
                if (ReportApi == user_collection_details_name
                    || ReportApi == tickets_booked_by_you_method_name || ReportApi == occupancy_report_name
                ) {
                    val intent = Intent(requireActivity(), SearchMultiSelectServiceActivity::class.java)
                    startActivityForResult(intent, SELECT_SERVICE_INTENT_REQUEST_CODE)
                } else {
                    navigateToAllotmentService()
                }
            }else{
                navigateToAllotmentService()
            }
        }

        binding.acSelectRestaurant.setOnClickListener {
            navigateToSelectRestaurant()
        }

        binding.spinnerSelectService.setEndIconOnClickListener {
            when (binding.acSelectReportType.text.toString()) {
                getString(R.string.branch_collection_report) -> ReportApi =
                    user_collection_details_name

                getString(R.string.tickets_booked_by_you) -> ReportApi =
                    tickets_booked_by_you_method_name

                getString(R.string.fuel_utility_report) -> {
                    ReportApi = fuel_transaction_detail_name
                }

                getString(R.string.occupancy_report) -> ReportApi = occupancy_report_name
                getString(R.string.service_wise_city_pickup_report) -> ReportApi =
                    service_wise_city_pickup_name

                getString(R.string.checking_inspector_report) -> ReportApi =
                    checking_inspector_report_name

                getString(R.string.bus_service_collection) -> ReportApi =
                    bus_service_collection_name

                getString(R.string.group_by_branch_report) -> ReportApi =
                    group_by_branch_report_method_name

                getString(R.string.cargo_booking_report) -> ReportApi =
                    cargo_booking_report_method_name

                getString(R.string.route_wise_booking_memo) -> ReportApi =
                    route_wise_booking_memo_name
            }

            if (currentCountry != null && currentCountry.equals("india", true)){
                if (ReportApi == user_collection_details_name
                    || ReportApi == tickets_booked_by_you_method_name || ReportApi == occupancy_report_name
                ) {
                    val intent = Intent(requireActivity(), SearchMultiSelectServiceActivity::class.java)
                    startActivityForResult(intent, SELECT_SERVICE_INTENT_REQUEST_CODE)
                } else {
                    navigateToAllotmentService()
                }
            }else{
                navigateToAllotmentService()
            }

        }


        binding.branchListTV.setOnClickListener {
            val intent = Intent(requireContext(), SearchBranchActivity::class.java)
            startActivityForResult(intent, RESULT_CODE_SEARCH_BRANCH)
        }
        binding.branchListTIL.setEndIconOnClickListener {
            val intent = Intent(requireContext(), SearchBranchActivity::class.java)
            startActivityForResult(intent, RESULT_CODE_SEARCH_BRANCH)
        }

        binding.userListTV.setOnClickListener {
            if (requireContext().isNetworkAvailable()) {
//                if (selectedBranchId != "") {
//                    callUserListApi()
//                    showProgressDialog(requireContext())
//                } else {
//                    dismissProgressDialog()
//                    requireContext().toast(getString(R.string.validate_branch))
//                }
                callUserListApi()
                showProgressDialog(requireContext())
            } else {
                dismissProgressDialog()
                requireContext().noNetworkToast()
            }
        }
        binding.userListTIL.setEndIconOnClickListener {

            if (requireContext().isNetworkAvailable()) {
                if (selectedBranchId.isNotEmpty()) {
                    callUserListApi()
                    showProgressDialog(requireContext())

                } else {
                    dismissProgressDialog()
                    requireContext().toast(getString(R.string.validate_branch))
                }
            } else {
                dismissProgressDialog()
                requireContext().noNetworkToast()
            }
        }

        binding.acSelectCoach.setOnClickListener {

            if (requireContext().isNetworkAvailable()) {
                binding.apply {
                    if (acSelectReportType.text.toString() == getString(R.string.bus_service_collection)) {
                        if (currentCountry != null && currentCountry.equals("india", true)) {

                            if (returnedRouteId.isNotEmpty()) {
                                apiKey?.let { apiKey ->
                                    callCoachListApi(apiKey, returnedRouteId)
                                }
                            } else {
                                apiKey?.let { apiKey ->
                                    callCoachListApi(apiKey, "")
                                }
                            }

                        }
                    }
                }
            } else {
                dismissProgressDialog()
                requireContext().noNetworkToast()
            }
        }


        firebaseLogEvent(
            requireContext(),
            ALL_REPORTS,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            ALL_REPORTS,
            "All reports click"
        )

    }

    private fun setCheckingInspectorReportObserver() {
        allReportsViewModel.checkingInspectorReport.observe(viewLifecycleOwner) {
            Timber.d("allReports - CheckingInspectorReport ->> $it")
            binding.progressPB.root.gone()
            dismissProgressDialog()

            if(it != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }
                when(it.code) {
                    200 -> {
                        val intent = Intent (requireContext(), CheckingInspectorReportActivity::class.java)
                        intent.putExtra("data", jsonToString(it))
                        intent.putExtra(
                            "travel_date",
                            "${binding.tvTravelDate.text}  | ${binding.acSelectService.text}"
                        )
                        intent.putExtra("req_data", checkingInspectorReqBody)
                        startActivity(intent)
                        stopShimmerEffect()
                        dismissProgressDialog()
                    }
                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }
                    else -> {
                        if (it.message != null) {
                            it.message.let { it1 -> requireContext().toast(it1) }
                        }
                        stopShimmerEffect()
                        dismissProgressDialog()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                stopShimmerEffect()
                checkingInspectorReqBody = ""
            }
        }
    }

    private fun setDateChoiceSelectionAdapter() {
        binding.acSelectCustomDate.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                dateOptions
            )
        )
    }

    private fun setCoachSelectionAdapter() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            coachList
        )
        binding.acSelectCoach.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        binding.acSelectCoach.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String

            if (selectedItem == getString(R.string.all_coach)) {
                selectedCoachId = ""
                selectedCoachName = getString(R.string.all_coach)
            } else {
                val selectedCoach = coachListData.find { it.coachName == selectedItem }
                selectedCoach?.let {
                    selectedCoachId = it.coachId.toString()
                    selectedCoachName = it.coachName
                }
            }

        }
    }

    private fun setServiceWisePickupReportObserver() {
        allReportsViewModel.serviceWiseCityPickupReport.observe(viewLifecycleOwner) {
            Timber.d("allReports - ServiceWisePickupReport ->> $it")
            binding.progressPB.root.gone()
            dismissProgressDialog()

            if(it != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }
                when(it.code) {
                    200 -> {
                        val intent = Intent (requireContext(), ServiceWiseCityPickupClosureReportActivity::class.java)
                        intent.putExtra("data", jsonToString(it))
                        intent.putExtra(
                            "travel_date",
                            "${binding.tvTravelDate.text}  | ${binding.acSelectService.text}"
                        )
                        intent.putExtra("req_data", serviceWiseCityPickupClosureReqBody)
                        startActivity(intent)
                        stopShimmerEffect()
                        dismissProgressDialog()
                    }
                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }
                    412 -> {
                        if (it.message != null) {
                            requireContext().toast(it.message)
                        } else {
                            dismissProgressDialog()
                        }
                    }
                    else -> {
                        if (it.message != null) {
                            it.message?.let { it1 -> requireContext().toast(it1) }
                        } else {
                            dismissProgressDialog()
                        }
                        stopShimmerEffect()
                        dismissProgressDialog()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                stopShimmerEffect()
                dismissProgressDialog()
                serviceWiseCityPickupClosureReqBody = ""
            }
        }
    }

    private fun setBusServiceCollectionReportObserver() {
        allReportsViewModel.busServiceCollectionReport.observe(viewLifecycleOwner) { it ->
            Timber.d("allReports - BusServiceCollection ->> $it")
            binding.progressPB.root.gone()
            dismissProgressDialog()

            if(it != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }

                when (it.code) {
                    200 -> {
                        if (binding.detailSummaryRG.isVisible && binding.summaryRB.isChecked) {
                            val intent = Intent(
                                requireContext(),
                                BusServiceCollectionSummaryReportActivity::class.java
                            )
                            val data = jsonToString(it)
                            intent.putExtra("data", data)

                            binding.apply {
                                if(acSelectCustomDate.text.toString() == getString(R.string.custom_date)){
                                    intent.putExtra(
                                        "travel_date",
                                        "${tvFromDate.text} to ${tvToDate.text} | ${acSelectService.text}   "
                                    )
                                } else if(acSelectCustomDate.text.toString() == getString(R.string.today)) {
                                    intent.putExtra(
                                        "travel_date",
                                        "${getDateYMD(selectedDate)} | ${acSelectService.text}   "
                                    )
                                } else if(acSelectCustomDate.text.toString() == getString(R.string.yesterday)) {
                                    intent.putExtra(
                                        "travel_date",
                                        "${getDateYMD(selectedDate)} | ${acSelectService.text}   "
                                    )
                                } else if(acSelectCustomDate.text.toString() == getString(R.string.last_seven_days)) {
                                    intent.putExtra(
                                        "travel_date",
                                        "${getDateYMD(startDate)} to ${getDateYMD(endDate)} | ${acSelectService.text}   "
                                    )
                                } else {
                                    intent.putExtra(
                                        "travel_date",
                                        "${acSelectService.text}   "
                                    )
                                }
                            }

                            intent.putExtra("req_data", busServiceCollectionReqBody)
                            startActivity(intent)

                        } else if (binding.detailedRB.isVisible && binding.detailedRB.isChecked) {
                            val intent = Intent(
                                requireContext(),
                                BusServiceCollectionDetailedReportActivity::class.java
                            )
                            val data = jsonToString(it)
                            intent.putExtra("data", data)

                            binding.apply {
                                if(acSelectCustomDate.text.toString() == getString(R.string.custom_date)){
                                    intent.putExtra(
                                        "travel_date",
                                        "${tvFromDate.text} to ${tvToDate.text} | ${acSelectService.text}   "
                                    )
                                } else if(acSelectCustomDate.text.toString() == getString(R.string.today)) {
                                    intent.putExtra(
                                        "travel_date",
                                        "${getDateYMD(selectedDate)} | ${acSelectService.text}   "
                                    )
                                } else if(acSelectCustomDate.text.toString() == getString(R.string.yesterday)) {
                                    intent.putExtra(
                                        "travel_date",
                                        "${getDateYMD(selectedDate)} | ${acSelectService.text}   "
                                    )
                                } else if(acSelectCustomDate.text.toString() == getString(R.string.last_seven_days)) {
                                    intent.putExtra(
                                        "travel_date",
                                        "${getDateYMD(startDate)} to ${getDateYMD(endDate)} | ${acSelectService.text}   "
                                    )
                                } else {
                                    intent.putExtra(
                                        "travel_date",
                                        "${acSelectService.text}   "
                                    )
                                }
                            }

                            intent.putExtra("req_data", busServiceCollectionReqBody)
                            intent.putExtra("coach_id", "$selectedCoachId")
                            PreferenceUtils.apply {
                                setPreference(PREF_BRANCH_REPORT_DATA, data)
                            }
                            startActivity(intent)
                        } else {
                            requireContext().toast(getString(R.string.no_data_available))
                        }
                    }
                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }
                    412 -> {
                        if (it.message != null) {
                            requireContext().toast(it.message)
                        }
                        stopShimmerEffect()
                        dismissProgressDialog()
                    }
                    else -> {
                        if (it.message != null) {
                            it.message?.let { it1 -> requireContext().toast(it1) }
                        }
                        stopShimmerEffect()
                        dismissProgressDialog()
                    }
                }
            }
        }
    }

    private fun setGroupByBranchReportObserver() {
        allReportsViewModel.groupByBranchReport.observe(viewLifecycleOwner) { it ->
            Timber.d("allReports_GroupByBranch$it")
            binding.progressPB.root.gone()
            dismissProgressDialog()

            if (it != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }

                when(it.code) {
                    200 -> {
                        val intent = Intent (requireContext(), GroupByBranchReportActivity::class.java)
                        intent.putExtra("data", jsonToString(it))
                        intent.putExtra(
                            "travel_date",
                            "${binding.tvTravelDate.text}  | ${binding.acSelectService.text}"
                        )
                        intent.putExtra("req_data", groupByBranchReqBody)
                        startActivity(intent)
                        stopShimmerEffect()
                        dismissProgressDialog()
                    }
                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }
                    412 -> {
                        if (it.message != null) {
                            requireContext().toast(it.message)
                        }
                        stopShimmerEffect()
                        dismissProgressDialog()
                    }
                    else -> {

                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> requireContext().toast(it1) }
                        } else if (it.message != null) {
                            it.message?.let { it1 -> requireContext().toast(it1) }
                        }
                        stopShimmerEffect()
                        dismissProgressDialog()
                    }
                }
            }
        }
    }

    private fun setUpOccupancyReportViewObserver() {
        allReportsViewModel.newOccupancyReportDetail.observe(viewLifecycleOwner) { it ->
            Timber.d("allReports_TicketBookedByYou$it")
            binding.progressPB.root.gone()
            dismissProgressDialog()

            if (it != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }

                when (it.code) {
                    200 -> {
                        val intent = Intent(requireContext(), OccupancyReportActivity::class.java)
                        intent.putExtra("data", Gson().toJson(it))
                        intent.putExtra(
                            "travel_date",
                            "${binding.tvFromDate.text} - ${binding.tvToDate.text}  | ${binding.acSelectService.text}"
                        )
                        intent.putExtra("req_data", occupancyReqBody)
                        startActivity(intent)
                        stopShimmerEffect()
                        dismissProgressDialog()


                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        if (it.message != null) {
                            it.message?.let { it1 -> requireContext().toast(it1) }
                        }
                        stopShimmerEffect()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                stopShimmerEffect()
                occupancyReqBody = ""
            }
        }
    }


    private fun getNumberOfDaysBetweenDates(): Int {
        val dateStr1 = formatDate(binding.tvFromDate.text.toString())
        val dateStr2 = formatDate(binding.tvToDate.text.toString())

// Create a SimpleDateFormat object to parse the date strings
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")

        try {
            // Parse the date strings into Date objects
            val date1 = dateFormat.parse(dateStr1)
            val date2 = dateFormat.parse(dateStr2)

            // Calculate the number of milliseconds between the two dates
            val millisecondsBetween = date2.time - date1.time

            // Calculate the number of days by dividing milliseconds by the number of milliseconds in a day
            val daysBetween = millisecondsBetween / (24 * 60 * 60 * 1000)
            return daysBetween.toInt()

            // Now, 'daysBetween' contains the number of days between the two dates
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return -1

    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd-M-yyyy")
        val outputFormat = SimpleDateFormat("dd-MM-yyyy")
        try {
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return inputDate // Return the original date if parsing fails
    }

    private fun callUserListApi() {

        blockViewModel.userListApi(
            apiKey = loginModelPref.api_key,
            cityId = "",
            userType = "12",
            branchId = selectedBranchId,
            locale = locale.toString(),
            apiType = user_list_method_name
        )
    }

    private fun callCoachListApi (apiKey: String, routeId: String) {
        pickUpChartViewModel.getCoachList(
            apiKey,
            routeId
        )
    }

    private fun setUserObserver() {

        blockViewModel.userList.observe(requireActivity()) { it ->
            dismissProgressDialog()

            if (it != null) {
                if (it.active_users != null && it.active_users.isNotEmpty()) {
                    userList.clear()
                    it.active_users.forEach {
                        val spinnerItems = SpinnerItems(
                            id = it.id,
                            value = it.label,
                            role_discount = it.role_discount.toString().toDoubleOrNull()
                        )
                        userList.add(spinnerItems)
                    }
                    saveUserList(userList)

                    val intent = Intent(requireContext(), SearchActivity::class.java)
                    intent.putExtra(
                        getString(R.string.CITY_SELECTION_TYPE),
                        getString(R.string.selectUser)

                    )
                    intent.putExtra("all_user", getString(R.string.all_users))

                    startActivityForResult(intent, RESULT_CODE_SEARCH_USER)
                } else {
                    if (it.message != null) {
                        requireContext().toast(it.message)
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun callBranchListApi() {
        blockViewModel.branchListApi(
            apiKey = loginModelPref.api_key.toString(),
            locale = locale.toString(),
            apiType = branch_list_method_name
        )
    }

    private fun setBranchObserver() {
        blockViewModel.branchList.observe(requireActivity()) { it ->
            dismissProgressDialog()

            if (it != null) {
//                if (it.branchlists.isNotEmpty()) {
//                    branchList.clear()
//
//                    var spinnerItems: SpinnerItems
//
//                    it.branchlists.forEach {
//                        spinnerItems = SpinnerItems(
//                            id = it.id,
//                            value = it.label,
//                            branch_discount = it.branch_discount.toString().toDoubleOrNull()
//                        )
//                        branchList.add(spinnerItems)
//                    }
//                }
//                saveBranchList(branchList)
//
//                val intent = Intent(requireContext(), SearchActivity::class.java)
//                intent.putExtra(
//                    getString(R.string.CITY_SELECTION_TYPE),
//                    getString(R.string.selectBranch)
//                )
//                intent.putExtra("all_branch", getString(R.string.all_branches))
//
//                startActivityForResult(intent, RESULT_CODE_SEARCH_BRANCH)
//            }

                if (!it.branchlists.isNullOrEmpty()) {
                    it.branchlists.forEach {
                        val branchItem = Branch(it.id, it.label, true)
                        branchList.add(branchItem)
                    }

                    branchSize = branchList.size
                }

                PreferenceUtils.putObject(
                    BranchList(branchList),
                    PREF_BRANCH_LIST_REPORT_MODEL
                )
                PreferenceUtils.putString(getString(R.string.tag), TAG)

            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun setDefault() {
        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)
        calendar()
    }

    private fun setFavouriteReportsAdapter(list: List<StarredReport>) {
        layoutManager =
            LinearLayoutManager(context?.applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rvFavoutriteReports.layoutManager = layoutManager
        favouriteReportsAdapter =
            ReportsDateAdapter(
                requireActivity(),
                list,
                false,
                this
            )
        binding.rvFavoutriteReports.adapter = favouriteReportsAdapter
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key

//        privileges = (activity as BaseActivity).getPrivilegeBase()
        lifecycleScope.launch {
            val privilege = (activity as BaseActivity).getPrivilegeBaseSafely()
            allReportsViewModel.updatePrivileges(privilege)
        }

        locale = PreferenceUtils.getlang()
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = PreferenceUtils.getDashboardCurrentDate()

        allReportsViewModel.privilegesLiveData.observe(requireActivity()) { privileges ->
            if (!privileges?.country.isNullOrEmpty()) {
                currentCountry = privileges?.country
            }

            if (privileges?.isAgentLogin == true) {
                isAgentLogin = privileges?.isAgentLogin?:false
            }
            roleNames = getUserRole(loginModelPref, isAgentLogin, requireContext())
            Timber.d("Rolenames prog ${roleNames}")
            callStarredReportsApi()
            setStarredReportObserver()
            setReportTypeObserver()
            if (privileges?.paymentStatusReport!=null && privileges?.paymentStatusReport==true){
                setPaymentType()
            }

            setDefault()

        }

    }

    private fun setPaymentType() {
         paymentTypeList= context?.resources?.getStringArray(R.array.report_payment_type)?: arrayOf()
        binding.acSelectSelectPaymentType.setAdapter(
            context?.let {
                ArrayAdapter(
                    it,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    paymentTypeList
                )
            }
        )
        binding.acSelectSelectPaymentType.setText(paymentTypeList[0], false)



    }

    private fun callStarredReportsApi() {
//        if (requireActivity().isNetworkAvailable()) {
        val reqBody =
            com.bitla.ts.domain.pojo.starred_reports.request.ReqBody(
                apiKey?:"",
                true,
                locale = locale
            )
        val starredReportsRequest =
            StarredReportsRequest(bccId?:"", format_type, starred_reports_name, reqBody)

        /*starredReportsViewModel.starredReportsApi(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            starredReportsRequest,
            starred_reports_name
        )*/

        starredReportsViewModel.starredReportsApi(
            loginModelPref.api_key,
            true,
            locale?:"",
            starred_reports_name
        )

//        } else
//            requireActivity().noNetworkToast()
    }

    private fun callAllottedServiceApi(
        originId: String?,
        destinationId: String?,
        hubId: String?,
        isGroupByHubs: Boolean
    ) {
        if (requireActivity().isNetworkAvailable()) {
            AllotedServiceRequest(
                bccId.toString(), alloted_Service_method_name, format_type, ReqBody(
                    loginModelPref.api_key,
                    PreferenceUtils.getDashboardCurrentDate(),
                    origin = originId,
                    destination = destinationId,
                    is_group_by_hubs = isGroupByHubs,
                    is_from_middle_tier = true,
                    locale = locale,
                    hub_id = hubId,
                    view_mode = "report"
                )
            )

            pickUpChartViewModel.getAllottedServicesWithDateChange(
                apiKey = loginModelPref.api_key,
                origin = originId.toString(),
                destination = destinationId.toString(),
                from = fromDate,
                to = toDate,
                hubId = hubId,
                isGroupByHubs = isGroupByHubs,
                viewMode = "report",
                locale = locale ?: "en",
                isFromMiddleTier = true,
                methodName = alloted_service_Dashboard_method
            )

        } else requireActivity().noNetworkToast()
    }

    private fun setAllottedDetailObserver() {
        pickUpChartViewModel.allotedDetailResponse.observe(requireActivity()) { it ->
            if(it.code!=null){
            when (it.code) {
                200 -> {
                    if (it?.services?.isNotEmpty() == true) {
                        serviceSize = it.services.size
//                        allottedServicesResponseModel?.services = it.services
                        PreferenceUtils.putObject(it, "allotted_services_model_report")
                        dismissProgressDialog()
                    }
                }

                401 -> {
                    /*DialogUtils.unAuthorizedDialog(
                        requireContext(),
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    (activity as BaseActivity).showUnauthorisedDialog()

                }

                else -> {
                    //it.result?.message?.let { it1 -> requireActivity().toast(it1) }
                    dismissProgressDialog()
                }
            }
        } else{
                dismissProgressDialog()
                requireContext().toast(requireContext().getString(R.string.server_error))
            }
        }
    }

    private fun setServices(list: List<Service>) {

        list.forEach {
            val spinnerItems = SpinnerItems(it.reservationId as Int, it.number as String)
            servicesList.add(spinnerItems)
        }

        saveAgentList(servicesList)

        val intent = Intent(requireContext(), SearchActivity::class.java)
        intent.putExtra(
            getString(R.string.CITY_SELECTION_TYPE),
            getString(R.string.selectAgent)
        )
        startActivityForResult(intent, RESULT_CODE_SEARCH_AGENT)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            if (data?.getStringExtra(getString(R.string.SELECTED_CITY_TYPE)) != null) {

                data.getStringExtra(getString(R.string.SELECTED_CITY_TYPE))

                val selectedCityType: String =
                    data.getStringExtra(getString(R.string.SELECTED_CITY_TYPE)).toString()
                val selectedCityName: String =
                    data.getStringExtra(getString(R.string.SELECTED_CITY_NAME)).toString()
                val selectedCityId: String =
                    data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()

                when (selectedCityType) {
                    getString(R.string.selectBranch) -> {
                        selectedBranchId = selectedCityId
                        selectedBranchName = selectedCityName
                        binding.branchListTV.setText(selectedBranchName)

                        selectedUserId = 0
                        selectedUserName = ""
                        binding.userListTV.setText("")
//                        requireContext().toast("branch- $selectedBranchName , $selectedBranchId$")
                    }

                    getString(R.string.selectUser) -> {
                        selectedUserId = selectedCityId.toDouble().toInt()
                        selectedUserName = selectedCityName
                        binding.userListTV.setText(selectedUserName)
//                        requireContext().toast("user- $selectedUserName , $selectedUserId$")
                    }

                    else -> {
                        val value = data.getStringExtra(getString(R.string.SELECTED_SEARCHED_NAME))
                        Timber.d("Result prog ${value}")
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        } else if (resultCode == SELECT_SERVICE_INTENT_REQUEST_CODE) {

            val allottedServiceString = data?.getStringExtra("allotted_service_response")
            serviceSize = data?.getIntExtra("allotted_service_size", 0)

            if (allottedServiceString != null) {

                val aa = stringToJson<AllotedServicesResponseModel>(allottedServiceString)

                allottedServicesResponseModel = aa

                var totalSelectedServices = 0

                var previousRouteId = returnedRouteId
                var wasAllServices = binding.acSelectService.text.toString().startsWith("All Services")

                aa.services.forEach {
                    if (it.isChecked)
                        totalSelectedServices++
                }

                if (totalSelectedServices > 0 && totalSelectedServices < (serviceSize ?: 0)
                ) {
                    binding.acSelectService.setText("Services (${totalSelectedServices})")
                    returnedRouteId = ""
                    aa.services.forEach {
                        if (it.isChecked) {
                            returnedRouteId += it.routeId.toString().replace(".0", "") + ","
                        }
                    }
                    returnedRouteId = returnedRouteId.substring(0, returnedRouteId.lastIndexOf(","))
                } else {
                    binding.acSelectService.setText("All Services (${serviceSize})")
                    returnedRouteId = ""
                }
            }
        } else if (resultCode == SELECT_BRANCH_INTENT_REQUEST_CODE) {

            val branchListModel =
                PreferenceUtils.getObject<BranchList>(PREF_BRANCH_LIST_REPORT_MODEL)
            var totalSelectedServices = 0

            branchListModel?.branchList?.forEach {
                if (it.isChecked)
                    totalSelectedServices++
            }

            if (totalSelectedServices > 0 && totalSelectedServices < (branchListModel?.branchList?.size
                    ?: 0)
            ) {
                selectedBranchId = ""
                selectedBranchName = ""

                branchListModel?.branchList?.forEach {
                    if (it.isChecked) {
                        selectedBranchId += it.id.toString() + ","
                        selectedBranchName += it.value + ","
                    }
                }
            } else {
                selectedBranchId = ""
                selectedBranchName = "All Branches (${branchListModel?.branchList?.size ?: ""})"
            }

            if (selectedBranchId == "" && totalSelectedServices == (branchListModel?.branchList?.size
                    ?: 0)
            ) {
                selectedBranchName = "All Branches (${totalSelectedServices})"
            } else {
                selectedBranchName = "Branch ($totalSelectedServices)"
                selectedBranchId = selectedBranchId.substring(0, selectedBranchId.lastIndexOf(","))
            }
            binding.branchListTV.setText(selectedBranchName)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setStarredReportObserver() {
        starredReportsViewModel.starredReport.observe(viewLifecycleOwner) {
//            Timber.d("Test Run Starred Report Details ${it.toString()}")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        stopShimmerEffect()
                        binding.swipeRefreshLayout.isRefreshing = false

                        if (it.data?.recently_generated_reports.isNullOrEmpty()) {
                            binding.recentReportLayout.gone()
                            stopShimmerEffect()
                            binding.swipeRefreshLayout.isRefreshing = false
                        } else {
                            binding.recentReportLayout.visible()
                            val recentReports = it.data.recently_generated_reports
                            recentReports?.sortByDescending { it.date }
                            recentReports?.let { it1 -> setFavouriteReportsAdapter(it1) }
                            stopShimmerEffect()
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        it.message?.let { it1 -> requireActivity().toast(it1) }
                        binding.recentReportLayout.gone()
                        stopShimmerEffect()
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                binding.recentReportLayout.gone()
                stopShimmerEffect()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode === Activity.RESULT_OK) {
                if(result?.data?.hasExtra("selectedRestaurant")==true){
                  result?.data?.getStringExtra("selectedRestaurant")?.let {
                      if (it.isNotEmpty() && it.contains("@")) {
                              restaurantId = it.substringBefore("@")
                              restaurantName = it.substringAfter("@")
                              binding.acSelectRestaurant.setText(restaurantName)
                      }
                  }
                }else{
                returnApiSelected =
                    result.data?.getStringExtra("ApiNameSelected").toString()
                returnedRouteId =
                    result.data?.getStringExtra("selectedRouteId").toString()
                binding.acSelectService.setText(returnApiSelected)
            }}
        }

    @SuppressLint("SetTextI18n")
    private fun setallotedDetailObserver() {
        pickUpChartViewModel.allotedDetailResponse.observe(viewLifecycleOwner) { it ->
            if (it?.services != null) {
                val services = it.services

                binding.acSelectService.setOnClickListener {
                    navigateToAllotmentService()
                }

                binding.spinnerSelectService.setEndIconOnClickListener {
                    navigateToAllotmentService()
                }
            }
        }
    }

    private fun navigateToAllotmentService() {
        PreferenceUtils.setPreference("fromTicketDetail", false)
        Timber.d("acSelectReportType ${binding.acSelectReportType.text}")

        val selectedReport = binding.acSelectReportType.text.toString()
        val intent =
            Intent(requireContext(), SelectAllotedServiceActivity::class.java)
        intent.putExtra(getString(R.string.report), selectedReport)
        resultLauncher.launch(intent)
    }



    private fun navigateToSelectRestaurant() {
        val intent =
            Intent(requireContext(), SelectRestaurantActivity::class.java)
        intent.putExtra("apiKey",apiKey)
        resultLauncher.launch(intent)
    }



    fun calendar() {
        binding.tvFromDate.setOnClickListener {
            try {
                val listener =
                    DatePickerDialog.OnDateSetListener {
                            _, year, monthOfYear, dayOfMonth,
                        ->
                        binding.tvFromHint.visible()
                        val selectedFromDate = "$dayOfMonth-${monthOfYear.plus(1)}-$year"
                        binding.tvFromDate.text = selectedFromDate
                        binding.tvToDate.text = getString(R.string.toDate)
                        validation()
                    }

                val getFromDate = binding.tvFromDate.text.toString()
                if (getFromDate.contains("-")) {
                    val splitFromDate = getFromDate.split("-")
                    day = splitFromDate[0].toInt()
                    month = splitFromDate[1].toInt().minus(1)
                    year = splitFromDate[2].toInt()
                }

                setDateLocale(locale?:"", requireContext())
                val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
                dpDialog.show()


            } catch (e: Exception) {
                Timber.d("exceptionMsg ${e.message}")
            }


            firebaseLogEvent(
                requireContext(),
                FROM_DATE,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                FROM_DATE,
                "From date click"
            )


        }

        binding.tvToDate.setOnClickListener {
            val selectedFromDate = binding.tvFromDate.text.toString()
            if (selectedFromDate.contains("-")) {
                val listener =
                    DatePickerDialog.OnDateSetListener {
                            _, year, monthOfYear, dayOfMonth,
                        ->
                        binding.tvToHint.visible()
                        val selectedToDate = "$dayOfMonth-${monthOfYear.plus(1)}-$year"

                        val sdf = SimpleDateFormat("dd-MM-yyyy")
                        if (selectedFromDate.contains("-") && selectedToDate.contains("-")) {
                            val fromDate: Date = sdf.parse(selectedFromDate)
                            val toDate: Date = sdf.parse(selectedToDate)

                            val dateResult = fromDate.compareTo(toDate)
                            if (dateResult > 0) {
                                context?.toast(getString(R.string.date_compare))
                                binding.tvToDate.text = getString(R.string.toDate)
                            } else {
                                binding.tvToDate.text = selectedToDate
                            }
                        } else
                            binding.tvToDate.text = selectedToDate
                        validation()
                    }
                val getToDate = binding.tvToDate.text.toString()
                if (getToDate.contains("-")) {
                    val splitToDate = getToDate.split("-")
                    if (splitToDate.size > 2) {
                        day = splitToDate[0].toInt()
                        month = splitToDate[1].toInt().minus(1)
                        year = splitToDate[2].toInt()
                    }
                }

                val maxDateCalendar = Calendar.getInstance()
                val minDateCalendar = Calendar.getInstance()
                val splitFromDate = selectedFromDate.split("-")
                if (splitFromDate.size > 2) {
                    val day = splitFromDate[0].toInt()
                    val month = splitFromDate[1].toInt().minus(1)
                    val year = splitFromDate[2].toInt()

                    maxDateCalendar.set(Calendar.DAY_OF_MONTH, day.plus(30))
                    maxDateCalendar.set(Calendar.MONTH, month)
                    maxDateCalendar.set(Calendar.YEAR, year)

                    minDateCalendar.set(Calendar.DAY_OF_MONTH, day)
                    minDateCalendar.set(Calendar.MONTH, month)
                    minDateCalendar.set(Calendar.YEAR, year)
                }

                setDateLocale(locale?:"", requireContext())
                val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
                dpDialog.datePicker.minDate = minDateCalendar.timeInMillis
                dpDialog.datePicker.maxDate = maxDateCalendar.timeInMillis
                dpDialog.show()
            } else
                requireContext().toast(getString(R.string.selectFromDateFirst))

            firebaseLogEvent(
                requireContext(),
                TO_DATE,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                TO_DATE,
                "To date click"
            )
        }

        binding.tvTravelDate.setOnClickListener {
            val listener =
                DatePickerDialog.OnDateSetListener {
                        _, year, monthOfYear, dayOfMonth,
                    ->
                    binding.tvTravelHint.visible()
                    val selectedFromDate = "$dayOfMonth-${monthOfYear.plus(1)}-$year"
                    binding.tvTravelDate.text = selectedFromDate
                    validation()
                }

            val getTravelDate = binding.tvTravelDate.text.toString()
            if (getTravelDate.contains("-")) {
                val splitTravelDate = getTravelDate.split("-")
                day = splitTravelDate[0].toInt()
                month = splitTravelDate[1].toInt().minus(1)
                year = splitTravelDate[2].toInt()
            } else {
                setDefault()
            }

            setDateLocale(locale?:"", requireContext())
            val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
            dpDialog.show()
        }
    }

    private fun setReportTypeObserver() {
        binding.acSelectReportType.setAdapter(
            context?.let {
                ArrayAdapter(
                    it,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    reportTypeList
                )
            }
        )
    }

    private fun callAllReportsApi(isViewReport: Boolean = false) {
        when (binding.acSelectReportType.text.toString()) {
            getString(R.string.branch_collection_report) -> ReportApi =
                user_collection_details_name

            getString(R.string.tickets_booked_by_you) -> ReportApi =
                tickets_booked_by_you_method_name

            getString(R.string.fuel_utility_report) -> {
                ReportApi = fuel_transaction_detail_name
            }

            getString(R.string.occupancy_report) -> ReportApi = occupancy_report_name
            getString(R.string.service_wise_city_pickup_report) -> ReportApi =
                service_wise_city_pickup_name

            getString(R.string.checking_inspector_report) -> ReportApi =
                checking_inspector_report_name

            getString(R.string.bus_service_collection) -> ReportApi =
                bus_service_collection_name

            getString(R.string.group_by_branch_report) -> ReportApi =
                group_by_branch_report_method_name

            getString(R.string.cargo_booking_report) -> ReportApi =
                cargo_booking_report_method_name

            getString(R.string.route_wise_booking_memo) -> ReportApi = route_wise_booking_memo_name

            getString(R.string.payment_status_report) -> ReportApi = payemnt_status_report_method_name
            getString(R.string.restaurant_report) -> ReportApi = restaurant_meal_report_method_name

        }


        val allReportRequest = AllReportRequest()
        allReportRequest.bccId = bccId
        allReportRequest.format = format_type
        allReportRequest.methodName = ReportApi
        val reqBody = com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody()
        reqBody.apiKey = apiKey
        reqBody.isPdfDownload = isPdfDownload
        reqBody.isFromMiddleTier = is_middle_tier
        reqBody.locale = locale
        reqBody.isStarredReport = isStarredCheck
        reqBody.isDetailed = isDetailsReport


//        if (ReportApi != cargo_booking_report_method_name)

        if (ReportApi != service_wise_city_pickup_name) {
            if (ReportApi != fuel_transaction_detail_name) {
                if (ReportApi != checking_inspector_report_name && ReportApi != group_by_branch_report_method_name) {
                    reqBody.dateRange = dateRange
                    if (ReportApi != cargo_booking_report_method_name) {
                        reqBody.dateWise = dateWise
                        reqBody.locale = locale
                    }
                    if (ReportApi != occupancy_report_name && ReportApi != cargo_booking_report_method_name)
                        reqBody.responseFormat = responseFormat
                }

                if (ReportApi == occupancy_report_name) {
                    allReportsViewModel.privilegesLiveData.observe(requireActivity()) { privileges ->
                        if (returnApiSelected == getString(R.string.all_service)
                            && (privileges?.country.equals("Malaysia", true) || privileges?.country.equals("Vietnam", true))
                        ) {
                            reqBody.routeId = getString(R.string.empty)

                        } else {
                            reqBody.routeId = returnApiSelected.substringBefore(".")
                        }
                    }

                } else {
                    reqBody.routeId = returnedRouteId.substringBefore(".")
                }
            }

            if (ReportApi != checking_inspector_report_name && ReportApi != group_by_branch_report_method_name) {
                if(ReportApi == bus_service_collection_name && currentCountry != null && currentCountry.equals("India",true)){
                    when (binding.acSelectCustomDate.text.toString()) {
                        getString(R.string.today) -> {
                            reqBody.fromDate = getDateYMD(selectedDate)
                            reqBody.toDate = getDateYMD(selectedDate)
                        }
                        getString(R.string.yesterday) -> {
                            reqBody.fromDate = getDateYMD(selectedDate)
                            reqBody.toDate = getDateYMD(selectedDate)
                        }
                        getString(R.string.last_seven_days) -> {
                            reqBody.fromDate = getDateYMD(startDate)
                            reqBody.toDate = getDateYMD(endDate)

                        }
                        getString(R.string.custom_date) -> {
                            reqBody.fromDate = getDateYMD(binding.tvFromDate.text.toString())
                            reqBody.toDate = getDateYMD(binding.tvToDate.text.toString())
                        }
                    }
                } else {
                    reqBody.fromDate = getDateYMD(binding.tvFromDate.text.toString())
                    reqBody.toDate = getDateYMD(binding.tvToDate.text.toString())
                }
            }
        }

        if (ReportApi == fuel_transaction_detail_name) {
            reqBody.isExportPdf = isExportPdf
        }

        if (ReportApi == user_collection_details_name) {
            reqBody.isPdfDownload = !isViewReport
            reqBody.branchId = selectedBranchId
            if (selectedUserId == 0) {
                reqBody.userId = ""
            } else {
                reqBody.userId = selectedUserId.toString()
            }
        }


        if (ReportApi == tickets_booked_by_you_method_name) {
            // reqBody.isPdfDownload = true
            if (isViewReport) {
                reqBody.isPdfDownload = false
                reqBody.isReport = true
            } else {
                reqBody.isReport = false
                reqBody.isPdfDownload = true
            }
            reqBody.routeId = returnedRouteId.substringBefore(".")
            reqBody.page = Pagination.INITIAL_PAGE
            reqBody.perPage = Pagination.PER_PAGE
            reqBody.pagination = true
            reqBody.routeId = returnedRouteId


        }

        if (ReportApi == payemnt_status_report_method_name) {
            // reqBody.isPdfDownload = true
            if (isViewReport) {
                reqBody.isPdfDownload = false
                reqBody.isReport = true
            } else {
                reqBody.isReport = false
                reqBody.isPdfDownload = true
            }
            reqBody.routeId = returnedRouteId.substringBefore(".")
            reqBody.page = Pagination.INITIAL_PAGE
            reqBody.perPage = Pagination.PER_PAGE
            reqBody.pagination = true
            reqBody.routeId = returnedRouteId

            when(binding.acSelectSelectPaymentType.text.toString()){
                getString(R.string.all) ->{
                 reqBody.paymentType= getString(R.string.all_)
             }
                getString(R.string.confirm) -> {
                 reqBody.paymentType = getString(R.string.confirm_)

             }

                getString(R.string.pending)->{
                    reqBody.paymentType= getString(R.string.pending_)
                }
                getString(R.string.partial_ticket)->{
                    reqBody.paymentType= getString(R.string.partially_paid_)
                }
            }


        }

        if (ReportApi == group_by_branch_report_method_name) {
            if (isViewReport) {
                reqBody.isPdfDownload = false
            } else {
                reqBody.isPdfDownload = true
            }
        }

        if (ReportApi == occupancy_report_name) {
            reqBody.occupancyType = occupancyType
            if (isViewReport) {
                reqBody.isPdfDownload = false

            }
        }

        if (ReportApi == checking_inspector_report_name || ReportApi == group_by_branch_report_method_name) {
            reqBody.travelDate = getDateYMD(binding.tvTravelDate.text.toString())
        }

        if (ReportApi == service_wise_city_pickup_name) {
            val report = Report()
            report.date = getDateYMD(binding.tvTravelDate.text.toString())
            report.id = reportId
            val serviceActive = mutableListOf<Int>()
            serviceActive.add(returnedRouteId.toDouble().toInt())
            report.serviceActive = serviceActive
            reqBody.report = report
            //reqBody.isStarredReport = isStarredCheck

            if(isViewReport) {
                reqBody.isPdfDownload = false
                reqBody.isReport = true
            } else {
                reqBody.isPdfDownload = true
                reqBody.isReport = false
            }

        }

        if (ReportApi == checking_inspector_report_name && currentCountry != null && currentCountry.equals("India",true)) {
            if (isViewReport) {
                reqBody.isPdfDownload = false
                reqBody.isReport = true

            } else {
                reqBody.isPdfDownload = true
                reqBody.isReport = false
            }
        }

        if (ReportApi == route_wise_booking_memo_name) {
            reqBody.routeId = reportId
            reqBody.travelDate = getDateYMD(binding.tvTravelDate.text.toString())
        }

        if (ReportApi == bus_service_collection_name) {
            reqBody.hubOptions = hubOptions
            //reqBody.isStarredReport = isStarredCheck
            reqBody.busGroups = busGroups
            if (currentCountry != null && currentCountry.equals("india", true)){
                if (isViewReport) {
                    reqBody.isPdfDownload = false
                    reqBody.pagination = true
                    reqBody.page = Pagination.INITIAL_PAGE
                    reqBody.perPage = Pagination.PER_PAGE
                    reqBody.routeId = returnedRouteId
                } else {
                    reqBody.isPdfDownload = true
                }
            } else {
                reqBody.isPdfDownload = true
            }
        }
        reqBody.locale = locale
        allReportRequest.reqBody = reqBody

        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(reqBody)
        Timber.d("Req_body", json.toString())

        when (ReportApi) {
            user_collection_details_name -> {
                if (currentCountry != null && currentCountry.equals("india", true)){
                    reqBody.pagination = true
                    reqBody.page = Pagination.INITIAL_PAGE
                    reqBody.perPage = Pagination.PER_PAGE
                    reqBody.routeId = returnedRouteId
                }
                reqBodyToSend = Gson().toJson(reqBody)
                allReportsViewModel.userCollectionDetailApi(
                    apiKey?:"",
                    locale?:"",
                    reqBody,
                    alloted_Service_method_name
                )
            }

            checking_inspector_report_name -> {
                 if(isViewReport) {
                     reqBody.pagination = true
                     reqBody.page = Pagination.INITIAL_PAGE
                     reqBody.perPage = Pagination.PER_PAGE
                     reqBody.responseFormat = ""
                     checkingInspectorReqBody = Gson().toJson(reqBody)
                     allReportsViewModel.checkingInspectorReportNewApi(
                         apiKey = apiKey?:"",
                         locale = locale?:"",
                         allReportsRequest = reqBody,
                         apiType = alloted_Service_method_name
                     )
                 } else {
                     allReportsViewModel.checkingInspectorReportApi(
                         reqBody,
                         alloted_Service_method_name
                     )
                }
            }

            route_wise_booking_memo_name -> {
                allReportsViewModel.routeWiseBookingMemoApi(reqBody, route_wise_booking_memo_name)
            }

            fuel_transaction_detail_name -> {
                allReportsViewModel.fuelTransactionDetailApi(
                    apiKey?:"",
                    locale?:"",
                    reqBody,
                    alloted_Service_method_name
                )

            }

            occupancy_report_name -> {
                if (!isViewReport) {
                    allReportsViewModel.occupancyReportApi(reqBody, alloted_Service_method_name)

                } else {
                    if (currentCountry != null && currentCountry.equals("india", true)){
                        reqBody.pagination = true
                        reqBody.page = Pagination.INITIAL_PAGE
                        reqBody.perPage = Pagination.PER_PAGE
                        reqBody.routeId = returnedRouteId
                    }
                    occupancyReqBody = Gson().toJson(reqBody)
                    allReportsViewModel.occupancyReportApiViewOnly(
                        reqBody,
                        alloted_Service_method_name
                    )
                }
            }

            bus_service_collection_name -> {
                if(isViewReport) {
                    busServiceCollectionReqBody = Gson().toJson(reqBody)
                    allReportsViewModel.busServiceCollectionNewApi(
                        apiKey?:"",
                        locale?:"",
                        selectedCoachId,
                        reqBody,
                        alloted_Service_method_name
                    )
                } else {
                    allReportsViewModel.busServiceCollectionApi(
                        apiKey?:"",
                        locale?:"",
                        reqBody,
                        alloted_Service_method_name
                    )
                }
            }

            group_by_branch_report_method_name -> {
                if(isViewReport) {
                    allReportsViewModel.groupByBranchNewReportApi(
                        apiKey ?: "",
                        locale ?: "",
                        reqBody,
                        alloted_Service_method_name
                    )
                } else {
                    allReportsViewModel.groupByBranchReportApi(
                        apiKey ?: "",
                        locale ?: "",
                        reqBody,
                        alloted_Service_method_name
                    )
                }
            }

            tickets_booked_by_you_method_name -> {
                allReportsViewModel.privilegesLiveData.observe(requireActivity()) { privileges ->
                    Timber.d("country - main->>> ${privileges?.country}")
                    if(binding.rbTravelDate.isChecked){  //binding.rbIssueDate.isChecked
                        reqBody.dateType = ReportDateType.TRAVEL_DATE
                        var dateTypeTravel: String ="2"
                        PreferenceUtils.putString(PREF_DATE_TYPE_ISSUE_TRAVEL_DATE,dateTypeTravel)
                    }else{
                        reqBody.dateType = ReportDateType.ISSUE_DATE
                        var dateTypeIssued: String ="1"
                        PreferenceUtils.putString(PREF_DATE_TYPE_ISSUE_TRAVEL_DATE,dateTypeIssued)
                    }
                    if (isViewReport) {
                        ticketBookByYouReqBody = Gson().toJson(reqBody)
                        allReportsViewModel.ticketBookedByYouNewApi(
                            apiKey = apiKey?:"",
                            locale = locale?:"",
                            allReportsRequest = reqBody,
                            apiType = alloted_Service_method_name
                        )
                    }

                    /* else if (isViewReport == false && currentCountry != null && currentCountry.equals("Indonesia",true)){
                     ticketBookByYouReqBody = Gson().toJson(reqBody)
                     allReportsViewModel.ticketBookedByYouNewApi(
                     apiKey!!,
                     locale!!,
                     reqBody,
                     alloted_Service_method_name
                 )}*/
                    else {
                        ticketBookByYouReqBody = Gson().toJson(reqBody)
                        allReportsViewModel.ticketsBookedByYouApi(
                            apiKey?:"",
                            locale?:"",
                            reqBody,
                            alloted_Service_method_name
                        )
                    }
                }
            }


            payemnt_status_report_method_name -> {
                allReportsViewModel.privilegesLiveData.observe(requireActivity()) { privileges ->

                    Timber.d("country - main->>> ${privileges?.country}")
                    if(binding.rbTravelDate.isChecked){  //binding.rbIssueDate.isChecked
                        reqBody.dateType = ReportDateType.TRAVEL_DATE
                        var dateTypeTravel: String ="2"
                        PreferenceUtils.putString(PREF_DATE_TYPE_ISSUE_TRAVEL_DATE,dateTypeTravel)
                    }else{
                        reqBody.dateType = ReportDateType.ISSUE_DATE
                        var dateTypeIssued: String ="1"
                        PreferenceUtils.putString(PREF_DATE_TYPE_ISSUE_TRAVEL_DATE,dateTypeIssued)
                    }


                    if (isViewReport) {
                        ticketBookByYouReqBody = Gson().toJson(reqBody)
                        allReportsViewModel.paymentStatusReportApi(
                            apiKey = apiKey?:"",
                            locale = locale?:"",
                            allReportsRequest = reqBody,
                            apiType = alloted_Service_method_name
                        )
                    }
                    /* else if (isViewReport == false && currentCountry != null && currentCountry.equals("Indonesia",true)){
                         ticketBookByYouReqBody = Gson().toJson(reqBody)
                         allReportsViewModel.ticketBookedByYouNewApi(
                         apiKey!!,
                         locale!!,
                         reqBody,
                         alloted_Service_method_name
                     )}*/
                    else {
                        ticketBookByYouReqBody = Gson().toJson(reqBody)
                        allReportsViewModel.paymentStatusReportDownloadApi(
                            apiKey?:"",
                            locale?:"",
                            reqBody,
                            alloted_Service_method_name
                        )
                    }
                }



            }


            restaurant_meal_report_method_name -> {
//                Timber.d("country - main->>> ${privileges?.country}")


                if(returnedRouteId.isEmpty()){
                    reqBody.routeId="-1"
                }else{
                reqBody.routeId=returnedRouteId}

                if (isViewReport) {
                    restaurantViewModel.getReportsApi(apiKey?:"",responseFormat?:"",false,reqBody.fromDate?:"",reqBody.toDate?:"",locale?:"",Pagination.INITIAL_PAGE,Pagination.PER_PAGE,true,restaurantId,reqBody.routeId?:"-1")
                }
                else {
                    restaurantViewModel.getReportsApi(apiKey?:"",responseFormat?:"",true,reqBody.fromDate?:"",reqBody.toDate?:"",locale?:"",Pagination.INITIAL_PAGE,Pagination.PER_PAGE,false,restaurantId,reqBody.routeId?:"-1")
                }
            }


            service_wise_city_pickup_name -> {
                if(isViewReport) {
                    reqBody.pagination = true
                    reqBody.page = Pagination.INITIAL_PAGE
                    reqBody.perPage = Pagination.PER_PAGE
                    serviceWiseCityPickupClosureReqBody = Gson().toJson(reqBody)
                    allReportsViewModel.serviceWisePickupClosureReportNewApi(
                        allReportsRequest = reqBody,
                        apiType = alloted_Service_method_name
                    )
                } else {
                    allReportsViewModel.serviceWiseCityPickup(
                        reqBody,
                        service_wise_city_pickup_name
                    )
                }
            }

            cargo_booking_report_method_name -> {
                allReportsViewModel.cargoReportApi(
                    apiKey?:"",
                    locale?:"",
                    reqBody,
                    alloted_Service_method_name
                )

            }

            else -> {
                allReportsViewModel.allReportsApi(
                    loginModelPref.auth_token,
                    loginModelPref.api_key,
                    allReportsRequest = allReportRequest,
                    alloted_Service_method_name
                )
            }
        }

        firebaseLogEvent(
            requireContext(),
            DOWNLOAD_REPORT,
            PreferenceUtils.getLogin().userName,
            PreferenceUtils.getLogin().travels_name,
            PreferenceUtils.getLogin().role,
            DOWNLOAD_REPORT,
            binding.acSelectReportType.text.toString()
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setAllReportsObserver() {
        allReportsViewModel.allReports.observe(viewLifecycleOwner) {
            Timber.d("allReports - main->>> $it")
            binding.progressPB.root.gone()
            dismissProgressDialog()

            if (it != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }

                when (it.code) {
                    200 -> {
                        if (!isViewReport) {
                            allReportSuccessResponse = it
                            DownloadPdf.downloadReportPdf(requireContext(), it.pdf_url)
                         /*   binding.acSelectReportType.setText("")
                            binding.acSelectService.setText("")
                            binding.tvFromDate.text = ""
                            binding.tvToDate.text = ""
                            binding.chkStarred.isChecked = false
                            binding.rgDate.gone()
                            binding.btnViewReport.gone()*/

                            stopShimmerEffect()
                            dismissProgressDialog()
                        } else {
                            dismissProgressDialog()
                            stopShimmerEffect()

                            if (isBranchCollectionReport) {

                                if (it.tickets != null && it.tickets.size > 0) {
                                    if (binding.detailSummaryRG.isVisible && binding.summaryRB.isChecked) {
                                        val intent = Intent(
                                            requireContext(),
                                            BranchCollectionSummaryReportActivity::class.java
                                        )
                                        val data = Gson().toJson(it)
                                        intent.putExtra("data", data)
                                        startActivity(intent)
                                    }
                                } else if (it.result != null && (it.result.totalBookingAmount != 0.0 || it.result.totalCancelAmount != 0.0)) {
                                    if (binding.detailedRB.isVisible && binding.detailedRB.isChecked) {
                                        val intent = Intent(
                                            requireContext(),
                                            BranchCollectionDetailsReportActivity::class.java
                                        )
                                        val data = Gson().toJson(it)
                                        intent.putExtra("data", data)
                                        intent.putExtra(
                                            "travel_date",
                                            "${binding.tvFromDate.text} to ${binding.tvToDate.text} | ${binding.acSelectService.text}   "
                                        )
                                        intent.putExtra("req_data", reqBodyToSend)
                                        PreferenceUtils.apply {
                                            setPreference(PREF_BRANCH_REPORT_DATA, data)
                                        }
                                        startActivity(intent)
                                    }
                                } else {
                                    requireContext().toast(getString(R.string.no_data_available))
                                }

                            } else {
                                if (it.result != null) {
                                    val data = Gson().toJson(it)
                                    val intent = Intent(
                                        requireActivity(),
                                        OccupancyReportActivity::class.java
                                    )
                                    intent.putExtra(
                                        "travel_date",
                                        binding.tvFromDate.text.toString()
                                    )
                                    intent.putExtra("data", data)
                                    startActivity(intent)
                                } else {
                                    requireContext().toast(getString(R.string.no_data_available))
                                }
                            }
                        }
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> requireContext().toast(it1) }
                        } else if (it.message != null) {
                            it.message.let { it1 -> requireContext().toast(it1) }
                        }
                        stopShimmerEffect()
                        dismissProgressDialog()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                stopShimmerEffect()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTicketBookedByYouNewObserver() {
        allReportsViewModel.ticketBookedByYouNewResp.observe(viewLifecycleOwner) { it ->
            Timber.d("allReports - TicketBookedByYou ->> $it")
            binding.progressPB.root.gone()
            dismissProgressDialog()

            if (it != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }

                when (it.code) {
                    200 -> {
                        if (currentCountry != null && currentCountry.equals("India", true)) {
                            val intent =
                                Intent(requireContext(), BookedByYouReportActivity::class.java)
                            intent.putExtra(
                                R.string.data_ticket_boked_by_you.toString(),
                                Gson().toJson(it)
                            )
                            intent.putExtra(
                                R.string.travel_date.toString(),
                                "${binding.tvFromDate.text} to ${binding.tvToDate.text} | ${binding.acSelectService.text}   "
                            )
                            intent.putExtra(
                                R.string.request_data_ticket_booked_by_you.toString(),
                                ticketBookByYouReqBody
                            )

                            startActivity(intent)
                        } else {
                            var strDate = binding.tvFromDate.text
                            var dmyDateFrom = getDateDMYY(strDate.toString())

                            var strDate1 = binding.tvToDate.text
                            var dmyDateTo = getDateDMYY(strDate1.toString())
                            var travelDateStr =
                                "${dmyDateFrom} to ${dmyDateTo} | ${binding.acSelectService.text}   "
                            val intent =
                                Intent(requireContext(), BookedByYouReportActivity::class.java)
                            intent.putExtra(
                                R.string.data_ticket_boked_by_you.toString(),
                                Gson().toJson(it)
                            )
                            intent.putExtra(
                                R.string.request_data_ticket_booked_by_you.toString(),
                                ticketBookByYouReqBody
                            )
                            intent.putExtra(
                                R.string.travel_date.toString(),
                                travelDateStr
                            )
                            startActivity(intent)
                        }
                    }

                    401 -> {
                       /* DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> requireContext().toast(it1) }
                        } else if (it.message != null) {
                            it.message.let { it1 -> requireContext().toast(it1) }
                        }
                        stopShimmerEffect()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                stopShimmerEffect()
            }
        }
    }
    private fun setPaymentStatusReportObserver() {
        allReportsViewModel.paymentStatusReportResp.observe(viewLifecycleOwner) { it ->
            Timber.d("allReports - TicketBookedByYou ->> $it")
            binding.progressPB.root.gone()
            dismissProgressDialog()

            if (it != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }

                when (it.code) {
                    200 -> {
                        if (currentCountry != null && currentCountry.equals("India", true)) {
                            val intent =
                                Intent(requireContext(), BookedByYouReportActivity::class.java)
                            intent.putExtra(
                                R.string.data_ticket_boked_by_you.toString(),
                                Gson().toJson(it)
                            )
                            intent.putExtra(
                                R.string.travel_date.toString(),
                                "${binding.tvFromDate.text} to ${binding.tvToDate.text} | ${binding.acSelectService.text}   "
                            )
                            intent.putExtra(
                                R.string.request_data_ticket_booked_by_you.toString(),
                                ticketBookByYouReqBody
                            )
                            startActivity(intent)
                        } else {
                            var strDate = binding.tvFromDate.text
                            var dmyDateFrom = getDateDMYY(strDate.toString())

                            var strDate1 = binding.tvToDate.text
                            var dmyDateTo = getDateDMYY(strDate1.toString())
                            var travelDateStr =
                                "${dmyDateFrom} to ${dmyDateTo} | ${binding.acSelectService.text}   "
                            val intent =
                                Intent(requireContext(), BookedByYouReportActivity::class.java)
                            intent.putExtra(
                                R.string.data_ticket_boked_by_you.toString(),
                                Gson().toJson(it)
                            )
                            intent.putExtra(
                                R.string.request_data_ticket_booked_by_you.toString(),
                                ticketBookByYouReqBody
                            )
                            intent.putExtra(
                                R.string.travel_date.toString(),
                                travelDateStr
                            )

                            intent.putExtra(
                              "isPaymentStatusReport",
                               true
                            )
                            intent.putExtra(
                                "payment_type",
                                binding.acSelectSelectPaymentType.text.toString()
                            )


                            startActivity(intent)
                        }
                    }

                    401 -> {
                        DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    }

                    else -> {
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> requireContext().toast(it1) }
                        } else if (it.message != null) {
                            it.message.let { it1 -> requireContext().toast(it1) }
                        }
                        stopShimmerEffect()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                stopShimmerEffect()
            }
        }
    }




    private fun setRestaurantMealReportObserver() {
        restaurantViewModel.reportsResponse.observe(viewLifecycleOwner) { it ->
            binding.progressPB.root.gone()
            dismissProgressDialog()

            if (it != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }

                when (it.code) {
                    200 -> {

                        if (isViewReport == false) {
                            DownloadPdf.downloadReportPdf(requireContext(), it.pdfUrl)
                            stopShimmerEffect()
                            dismissProgressDialog()
                        } else {
                            stopShimmerEffect()
                            dismissProgressDialog()
                            val intent = Intent(requireContext(), MealsReportActivity::class.java)
                            intent.putExtra("reportsData", Gson().toJson(it))
                            intent.putExtra(
                                "fromDate",
                                getDateYMD(binding.tvFromDate.text.toString())
                            )
                            intent.putExtra(
                                "toDate",
                                getDateYMD(binding.tvToDate.text.toString())
                            )
                            intent.putExtra("serviceId", returnedRouteId)
                            intent.putExtra("restaurantId", restaurantId)
                            startActivity(intent)
                        }
                    }

                    401 -> {
                        DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    }

                    else -> {
                        if (it?.message != null) {
                            it.message.let { it1 -> requireContext().toast(it1) }
                        }
                        stopShimmerEffect()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                stopShimmerEffect()
            }
        }
    }



    private fun setRouteWiseBookingMemoObserver() {
        allReportsViewModel.routeWiseMemo.observe(viewLifecycleOwner) { it ->
            Timber.d("allReports $it")
            if (it != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }

                when (it.code) {
                    200 -> {
                        dismissProgressDialog()
                        stopShimmerEffect()
                        val data = Gson().toJson(it)
                        val intent =
                            Intent(requireActivity(), RoutewiseBookingMemoActivity::class.java)
                        intent.putExtra("travel_date", binding.tvTravelDate.text.toString())
                        intent.putExtra("data", data)
                        startActivity(intent)

                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> requireContext().toast(it1) }
                        } else if (it.message != null) {
                            it.message.let { it1 -> requireContext().toast(it1) }
                        }
                        stopShimmerEffect()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                stopShimmerEffect()
            }
        }
    }

    fun showProgressDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.Style_Dialog_Rounded_littl_Corner)
        val dialogBinding = DialogProgressBarBinding.inflate(LayoutInflater.from(context))
        builder.setView(dialogBinding.root)
        DialogUtils.progressDialog = builder.create()
        DialogUtils.progressDialog?.setCancelable(false)
        DialogUtils.progressDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        DialogUtils.progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        if (DialogUtils.progressDialog != null && DialogUtils.progressDialog?.isShowing==true) {
            DialogUtils.progressDialog?.dismiss()
        }
    }
    
    private fun checkPermission(permission: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, requireActivity())
        if (Build.VERSION.SDK_INT >= 33) {
            callAllReportsApi()
            startShimmerEffect()
        } else {
            if (permissionResult) {
                callAllReportsApi()
                startShimmerEffect()
            } else {
                DownloadPdf.onRequestPermissionsResult(
                    STORAGE_PERMISSION_CODE,
                    permission,
                    requireActivity()
                )
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }
            }
        }
    }

    private fun setupCoachListObserver() {
        pickUpChartViewModel.coachList.observe(viewLifecycleOwner) { response ->
            Timber.d("coachList $response")
            if (response != null) {
                binding.apply {
                    btnDownload.isEnabled = true
                    btnViewReport.isEnabled = true
                    btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                    btnDownload.setTextColor(resources.getColor(R.color.white))
                    btnViewReport.setTextColor(resources.getColor(R.color.white))
                }

                when (response.code) {
                    200 -> {
                        dismissProgressDialog()
                        stopShimmerEffect()

                        if (response.coaches != null && response.coaches.isNotEmpty()) {

                            coachListData.clear()
                            coachListData.addAll(response.coaches)

                            coachList = mutableListOf(getString(R.string.all_coach))
                            coachList.addAll(response.coaches.map { coach -> coach.coachName })

                            setCoachSelectionAdapter()
                        }
                    }

                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }

                    else -> {
                        response.message.let { it -> requireContext().toast(it) }
                        stopShimmerEffect()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                stopShimmerEffect()
            }
        }
    }

    private fun onEditFields() {

        binding.apply {

            acSelectReportType.onChange {
                setDefault()
                PreferenceUtils.setPreference(
                    PREF_RESERVATION_ID, ""
                )
                acSelectService.text.clear()


                if(currentCountry != null && currentCountry.equals("India",true))
                {
                    acSelectCoach.text?.clear()

                    coachListData.clear()
                    coachList.clear()
                    coachList = mutableListOf(getString(R.string.all_coach))
                    setCoachSelectionAdapter()
                    binding.acSelectCoach.setText(getString(R.string.all_coach), false)
                    selectedCoachId = ""
                    selectedCoachName = getString(R.string.all_coach)
                    tvFromDate.text = getString(R.string.fromDate)
                    tvToDate.text = getString(R.string.toDate)
                }
                else
                {
                    tvFromDate.text = getTodayDate()
                    tvToDate.text = getTodayDate()
                }

                tvTravelDate.text = getString(R.string.travel_date)
                spinnerSelectCustomDate.hint = getString(R.string.select_date)
                acSelectCustomDate.setText("")

                val selectedReport = binding.acSelectReportType.text.toString()

                if(currentCountry != null && currentCountry.equals("India",true) && selectedReport.isNotEmpty() && selectedReport == getString(R.string.bus_service_collection)) {
                    spinnerSelectCoach.visible()
                    acSelectCoach.setText(R.string.all_coach)
                    detailSummaryRG.visible()
                    spinnerSelectCustomDate.visible()
                } else {
                    spinnerSelectCoach.gone()
                    detailSummaryRG.gone()
                    spinnerSelectCustomDate.gone()
                }


                if (selectedReport.isNotEmpty() && selectedReport == getString(R.string.fuel_utility_report))
                    spinnerSelectService.gone()
                else
                    spinnerSelectService.visible()

                if (selectedReport == getString(R.string.service_wise_city_pickup_report)
                    || selectedReport == getString(
                        R.string.group_by_branch_report
                    )
                    || selectedReport == getString(R.string.checking_inspector_report)
                    || selectedReport == getString(
                        R.string.route_wise_booking_memo
                    )
                ) {
                    layoutTravelDate.visible()
                    layoutFromToDate.gone()
                    binding.spinnerSelectPaymentType.gone()
                } else {
                    layoutTravelDate.gone()
                    if(currentCountry != null && currentCountry.equals("India",true)) {
                        if(selectedReport == getString(R.string.bus_service_collection)){
                            date = binding.acSelectCustomDate.text.toString()
                            if(date.equals(R.string.custom_date)) {
                                layoutFromToDate.visible()
                            }
                            else {
                                layoutFromToDate.gone()
                            }
                        } else {
                            layoutFromToDate.visible()
                        }
                    } else {
                        layoutFromToDate.visible()
                    }
                }

                if (selectedReport == getString(R.string.branch_collection_report)) {
                    detailSummaryRG.visible()
                    branchListTIL.visible()
                    userListTIL.visible()

                    isBranchCollectionReport = true
                    binding.branchListTV.setText("${getString(R.string.all_branches)} ($branchSize)")
                    binding.userListTV.setText(getString(R.string.all_users))

                } else {
                    detailSummaryRG.gone()

                    branchListTIL.gone()
                    userListTIL.gone()
                    binding.branchListTV.setText("")
                    binding.userListTV.setText("")
                    selectedBranchId = ""
                    selectedBranchName = ""
                    selectedUserName = ""
                    selectedUserId = 0
                }
                validation()
            }
            acSelectService.onChange {
                if(currentCountry != null && currentCountry.equals("India",true)) {
                    coachListData.clear()
                    coachList.clear()
                    coachList = mutableListOf(getString(R.string.all_coach))
                    setCoachSelectionAdapter()
                    binding.acSelectCoach.setText(getString(R.string.all_coach), false)
                    selectedCoachId = ""
                    selectedCoachName = getString(R.string.all_coach)
                }
                validation()
            }
            acSelectCoach.onChange {
                validation()
            }
            acSelectCustomDate.onChange {
                validation()
            }
        }
    }

    fun validation() {
        if (binding.acSelectReportType.text.toString().isNotEmpty()
        ) {
            if ((binding.acSelectService.isVisible && binding.acSelectService.text.toString()
                    .isNotEmpty()) || (binding.acSelectReportType.text.toString() == getString(R.string.fuel_utility_report))
            ) {
                if ((binding.acSelectReportType.text.toString() == getString(R.string.bus_service_collection)) &&
                    currentCountry != null && currentCountry.equals("india", true)) {
                    if(binding.acSelectCoach.text.toString().isNotEmpty()){
                        when (binding.acSelectCustomDate.text.toString()) {
                            getString(R.string.today),
                            getString(R.string.yesterday),
                            getString(R.string.last_seven_days) -> {
                                downloadBtnObserver(true)
                            }
                            getString(R.string.custom_date) -> {
                                if (binding.layoutFromToDate.isVisible &&
                                    binding.tvFromDate.text.toString() != getString(R.string.fromDate) &&
                                    binding.tvToDate.text.toString() != getString(R.string.toDate)) {
                                    downloadBtnObserver(true)
                                } else {
                                    downloadBtnObserver(false)
                                }
                            }
                            else -> downloadBtnObserver(false)
                        }
                    }
                } else if (binding.layoutFromToDate.isVisible && binding.tvFromDate.text.toString() != getString(
                        R.string.fromDate
                    ) && binding.tvToDate.text.toString() != getString(
                        R.string.toDate
                    )
                ) {
                    downloadBtnObserver(true)
                } else if (binding.layoutTravelDate.isVisible && binding.tvTravelDate.text.toString() != getString(
                        R.string.travel_date
                    )
                ) {
                    downloadBtnObserver(true)
                } else {
                    downloadBtnObserver(false)
                }
            } else {
                if((binding.acSelectReportType.text.toString() == getString(R.string.payment_status_report))){
                    downloadBtnObserver(true)
                }else{
                downloadBtnObserver(false)
            }}
        } else {
            downloadBtnObserver(false)
        }
    }

    private fun downloadBtnObserver(clickable: Boolean) {

        if (clickable) {

            binding.apply {
                btnDownload.isEnabled = true
                btnViewReport.isEnabled = true

                btnDownload.setBackgroundResource(R.drawable.bg_little_round_blue)
                btnViewReport.setBackgroundResource(R.drawable.bg_little_round_blue)
                btnDownload.setTextColor(resources.getColor(R.color.white))
                btnViewReport.setTextColor(resources.getColor(R.color.white))
            }

            if (binding.btnDownload.isEnabled) {

                binding.btnDownload.setOnClickListener {
                    isViewReport = false

                    callFirebaseReportEvents()

//                    binding.btnDownload.isEnabled = false
//                    binding.btnDownload.setBackgroundColor(resources.getColor(R.color.colorDimShadow6))

                    binding.apply {
                        btnDownload.isEnabled = false
                        btnViewReport.isEnabled = false
                        btnDownload.setBackgroundResource(R.drawable.rounded_border_primary_second)
                        btnViewReport.setBackgroundResource(R.drawable.rounded_border_primary_second)
                        btnDownload.setTextColor(resources.getColor(R.color.colorAccent))
                        btnViewReport.setTextColor(resources.getColor(R.color.colorAccent))
                    }

                    val reportType = binding.acSelectReportType.text.toString()

                    if (reportType == getString(R.string.route_wise_booking_memo)) {
                        showProgressDialog(requireContext())
                        val reqBody =
                            com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody()
                        reqBody.apiKey = apiKey
                        reqBody.isPdfDownload = isPdfDownload
                        reqBody.isFromMiddleTier = is_middle_tier
                        reqBody.locale = locale
                        reqBody.isStarredReport = isStarredCheck
                        reqBody.routeId = returnedRouteId.substringBefore(".")
                        reqBody.travelDate = getDateYMD(binding.tvTravelDate.text.toString())

                        allReportsViewModel.routeWiseBookingMemoApi(
                            reqBody,
                            route_wise_booking_memo_name
                        )


                    } else {
                        if (reportType == getString(R.string.service_wise_city_pickup_report) || reportType == getString(
                                R.string.group_by_branch_report
                            ) || reportType == getString(R.string.checking_inspector_report)
                        ) {

                            if (binding.tvTravelDate.text == getString(R.string.travel_date)) {
                                context?.toast(
                                    getString(R.string.validate_travel_date)
                                )
                            } else {
                                checkPermission(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            }
                        } else {
                            if(reportType == getString(R.string.bus_service_collection) && currentCountry != null && currentCountry.equals("india", true)) {
                                busServiceButtonHandling()

                            } else {
                                when {
                                    binding.tvFromDate.text == getString(R.string.fromDate) -> context?.toast(
                                        getString(R.string.validate_from_date)
                                    )

                                    binding.tvToDate.text == getString(R.string.toDate) -> context?.toast(
                                        getString(
                                            R.string.validate_to_date
                                        )
                                    )

                                    else -> {
                                        checkPermission(
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
                binding.btnViewReport.setOnClickListener {
                    isViewReport = true
                    // binding.progressPB.root.visible()v
                    val reportType = binding.acSelectReportType.text.toString()
                    if (reportType == getString(R.string.occupancy_report)) {
                        // val dateDiff = getNumberOfDaysBetweenDates()
                        // if(dateDiff != -1){
                        // if(getNumberOfDaysBetweenDates() <= 5){
                        showProgressDialog(requireContext())
                        callAllReportsApi(true)
                        /* }else{
                             context?.toast(getString(R.string.max_5_days_allowed))
                         }*/
                        /*}else{
                            context?.toast(getString(R.string.error_occured_please_re_select_the_date))

                        }*/

                    } else {
                        showProgressDialog(requireContext())
                        callAllReportsApi(true)
                    }


                }
            }
        } else {
            if (!binding.acSelectService.text.toString().isNotEmpty()) {
                binding.btnDownload.setOnClickListener {
                    requireActivity().toast("Select service")

                }
            }
//            binding.btnDownload.setBackgroundColor(resources.getColor(R.color.colorDimShadow6))
            binding.apply {
                btnDownload.isEnabled = false
                btnViewReport.isEnabled = false
                btnDownload.setBackgroundResource(R.drawable.rounded_border_primary_second)
                btnViewReport.setBackgroundResource(R.drawable.rounded_border_primary_second)
                btnDownload.setTextColor(resources.getColor(R.color.colorAccent))
                btnViewReport.setTextColor(resources.getColor(R.color.colorAccent))
            }
        }
    }

    private fun busServiceButtonHandling() {
        date = binding.acSelectCustomDate.text.toString()
        when (date) {
            getString(R.string.today),
            getString(R.string.yesterday),
            getString(R.string.last_seven_days) -> {
                isViewReport = false
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                val reqBody = com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody()
                reqBody.apiKey = apiKey
                reqBody.isPdfDownload = isPdfDownload
                reqBody.isFromMiddleTier = is_middle_tier
                reqBody.locale = locale
                reqBody.isStarredReport = isStarredCheck

                when (date) {
                    getString(R.string.today) -> {
                        reqBody.fromDate = getDateYMD(selectedDate)
                        reqBody.toDate = getDateYMD(selectedDate)
                    }
                    getString(R.string.yesterday) -> {
                        reqBody.fromDate = getDateYMD(selectedDate)
                        reqBody.toDate = getDateYMD(selectedDate)
                    }
                    getString(R.string.last_seven_days) -> {
                        reqBody.fromDate = getDateYMD(startDate)
                        reqBody.toDate = getDateYMD(endDate)
                    }
                }
            }
            getString(R.string.custom_date) -> {
                when {
                    binding.tvFromDate.text == getString(R.string.fromDate) ->
                        context?.toast(getString(R.string.validate_from_date))
                    binding.tvToDate.text == getString(R.string.toDate) ->
                        context?.toast(getString(R.string.validate_to_date))
                    else -> {
                        isViewReport = false
                        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            }
            else -> {
                context?.toast(getString(R.string.validate_travel_date))
            }
        }
    }

    private fun callFirebaseReportEvents() {

        firebaseLogEvent(
            requireContext(),
            REPORT_TYPE_SELECTION,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            REPORT_TYPE_SELECTION,
            selectedReportTypeValue
        )

        firebaseLogEvent(
            requireContext(),
            REPORT_SERVICE_SELECTION,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            REPORT_SERVICE_SELECTION,
            returnApiSelected
        )

        firebaseLogEvent(
            requireContext(),
            ADD_FAVORITES_REPORT_CHECK,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            ADD_FAVORITES_REPORT_CHECK,
            "$isStarredCheck"

        )

        firebaseLogEvent(
            requireContext(),
            GENERATE_DOWNLOAD_REPORT_CLICKS,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            GENERATE_DOWNLOAD_REPORT_CLICKS,
            "Generate Download Report Clicks"
        )

    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
        val permission = DownloadPdf.checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            requireActivity()
        )
        if(Build.VERSION.SDK_INT > 32){
            DownloadPdf.downloadReportPdf(requireContext(), data)

        }else{
            if (permission) {
                DownloadPdf.downloadReportPdf(requireContext(), data)
            } else {
                DownloadPdf.onRequestPermissionsResult(
                    STORAGE_PERMISSION_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    requireActivity()
                )
            }
        }


    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onClick(v: View?) {
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun swipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            startShimmerEffect()
            Handler(Looper.getMainLooper()).postDelayed({
                callStarredReportsApi()
                setStarredReportObserver()
            }, 500)

        }
    }

    /*
  * this method to used for start Shimmer Effect
  * */
    private fun startShimmerEffect() {
        binding.shimmerReport.visible()
        binding.reportMainContainer.gone()
        binding.shimmerReport.startShimmer()
    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.shimmerReport.gone()
        binding.reportMainContainer.visible()
        if (binding.shimmerReport.isShimmerStarted) {
            binding.shimmerReport.stopShimmer()
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

}