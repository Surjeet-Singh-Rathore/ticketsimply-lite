package com.bitla.ts.phase2.fragments.childFragments.bookingTrendsFragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.FragmentBranchRevenueBinding
import com.bitla.ts.domain.pojo.BranchModel.Branch
import com.bitla.ts.domain.pojo.BranchModel.BranchList
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.branch_list_model.request.BranchListRequest
import com.bitla.ts.domain.pojo.city_details.request.CityDetailRequest
import com.bitla.ts.domain.pojo.city_details.request.ReqBody
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.adapter.child.OccupancyAllTabsDetailsAdapter
import com.bitla.ts.phase2.chartUtils.ReportValue
import com.bitla.ts.phase2.dashboard_pojo.OccupancyAllTabsDetailsModel
import com.bitla.ts.presentation.view.activity.SearchBranchActivity
import com.bitla.ts.presentation.viewModel.BlockViewModel
import com.bitla.ts.presentation.viewModel.CityDetailViewModel
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.bitla.tscalender.SlyCalendarDialog
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.textfield.TextInputEditText
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt

class BranchContributionFragment : BaseFragment(), OnItemClickListener, SlyCalendarDialog.Callback {

    companion object {
        val TAG = BranchContributionFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentBranchRevenueBinding

    private lateinit var occupancyAllTabsDetailsAdapter: OccupancyAllTabsDetailsAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var occupancyBranchList = arrayListOf<OccupancyAllTabsDetailsModel>()
    private var occupancyReportList = ArrayList<ReportValue>()

    private var barChartCount = 0
    private var pieChartCount = 0
    private var lineChartCount = 0
    private val entries: ArrayList<PieEntry> = ArrayList()
    private val legendValues: ArrayList<PieEntry> = ArrayList()
    private val colors: ArrayList<Int> = ArrayList()
    private val legendEntry: MutableList<LegendEntry> = ArrayList()
    private var lineChartTitleList: ArrayList<ReportValue> = ArrayList()
    private var sortBy = "htol"
    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var locale: String? = ""
    private val cityDetailViewModel by viewModel<CityDetailViewModel<Any?>>()
    private var originCityList =
        mutableListOf<com.bitla.ts.domain.pojo.city_details.response.Result>()
    private lateinit var autoCompleteOrigin: AutoCompleteTextView
    private lateinit var autoCompleteDestination: AutoCompleteTextView
    private var currentSelection = ""
    private var cityIdOrigin = -1
    private var cityIdDestination = -1
    private var fromDate = ""
    private var toDate = ""
    private lateinit var etFromDate: TextInputEditText
    private lateinit var etToDate: TextInputEditText

    private var from = getDateYMD(getTodayDate())
    private var to = getDateYMD(getTodayDate())
    private var originCityName = ""
    private var destinationCityName = ""
    private var originIdFinal = -1
    private var destinationIdFinal = -1
    private var isFromDate = false
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private var branchList: MutableList<Branch> = mutableListOf()
    private var branchId = "-1"
    private var selectedServiceName = "All Branches"
    private var isSelectDate: Boolean = true
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var defaultSelection = -1
    private var isBeforeFromDateSelection: Boolean = true
    private var isAfterToDateSelection: Boolean = true
    private var isAfterFromDateSelection: Boolean = true
    private var hideYesterdayDateFilter: Boolean = false
    private var hideTodayDateFilter: Boolean = false
    private var hideTomorrowDateFilter: Boolean = false
    private var hideLast7DaysDateFilter: Boolean = false
    private var hideLast30DaysDateFilter: Boolean = false
    private var hideCustomDateFilter: Boolean = false
    private var hideCustomDateRangeFilter: Boolean = false
    private var isCustomDateFilterSelected: Boolean = false
    private var isCustomDateRangeFilterSelected: Boolean = false
    private var isLocalFilter = false
    private var selectedChartPosition = 0
    private var journeyBy: String? = null
    private var privilegeResponse: PrivilegeResponseModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentBranchRevenueBinding.inflate(inflater, container, false)
        val view: View = binding.root

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdgeFromOnlyBottom(binding.root)
        }

        PreferenceUtils.removeKey(BRANCH_CONTRIBUTION_TO_DATE)
        PreferenceUtils.removeKey(BRANCH_CONTRIBUTION_FROM_DATE)
        //PreferenceUtils.removeKey(BRANCH_CONTRIBUTION_ORIGIN_CITY_NAME)
        PreferenceUtils.removeKey(BRANCH_CONTRIBUTION_DESTINATION_CITY_NAME)
        PreferenceUtils.removeKey(BRANCH_WISE_MODEL_BRANCH_CONTRIBUTION)
        setDateLocale(PreferenceUtils.getlang(),requireContext())

        getPref()

        getParentIntent()

        if (defaultSelection == 5) {
            isCustomDateFilterSelected = true
        }

        if (defaultSelection == 6) {
            isCustomDateRangeFilterSelected = true
        }

        PreferenceUtils.putString(BRANCH_CONTRIBUTION_ORIGIN_CITY_NAME, selectedServiceName)
        //PreferenceUtils.putString(BRANCH_CONTRIBUTION_FROM_DATE, fromDate)

        //callCityDetailsApi()
        //callBookingTrendsApi()
        callBranchListApi()
        setBranchListObserver()
        setUpObserver()

        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            binding.swipeRefreshLayoutDashboard.isRefreshing = true
            occupancyReportList.clear()
            occupancyBranchList.clear()
            legendValues.clear()
            entries.clear()
            colors.clear()
            legendEntry.clear()
            //callCityDetailsApi()
            callBookingTrendsApi()
        }

        binding.apply {

            sortLayout.btnSort.setOnClickListener {
                DialogUtils.dialogSortBy(
                    context = requireContext(),
                    sortBy = sortBy,
                    newLowToHighLabel = requireActivity().getString(R.string.branch_contribution_booking_trends_sort_ltoh),
                    newHighToLowLabel = requireActivity().getString(R.string.branch_contribution_booking_trends_sort_htol),
                    showOccupancyFilter = true,
                    showDateFilter = false,
                    showNetAmountFilter = false,

                    object : DialogSingleButtonListener {
                        override fun onSingleButtonClick(str: String) {
                            sortBy = str
                            callBookingTrendsApi()
                            binding.sortLayout.filterMarker.visible()

                            firebaseLogEvent(
                                requireContext(),
                                SORT_BTN,
                                loginModelPref.userName,
                                loginModelPref.travels_name,
                                loginModelPref.role,
                                SORT_BTN,
                                SortBtn.BRANCH_CONTRIBUTION_BOOKING_TRENDS_SORT
                            )
                        }
                    }
                )
            }

            sortAndFilterLayout.btnSort.setOnClickListener {
                DialogUtils.dialogSortBy(
                    context = requireContext(),
                    sortBy = sortBy,
                    newLowToHighLabel = requireActivity().getString(R.string.branch_contribution_booking_trends_sort_ltoh),
                    newHighToLowLabel = requireActivity().getString(R.string.branch_contribution_booking_trends_sort_htol),
                    showOccupancyFilter = true,
                    showDateFilter = false,
                    showNetAmountFilter = false,

                    object : DialogSingleButtonListener {
                        override fun onSingleButtonClick(str: String) {
                            sortBy = str
                            callBookingTrendsApi()
                            binding.sortAndFilterLayout.sortMarker.visible()

                            firebaseLogEvent(
                                requireContext(),
                                SORT_BTN,
                                loginModelPref.userName,
                                loginModelPref.travels_name,
                                loginModelPref.role,
                                SORT_BTN,
                                "Branch-Contribution Booking Trends sort"
                            )
                        }
                    }
                )
            }

            btnShare.setOnClickListener {
                binding.chartChangeLayout.gone()
                binding.btnShare.gone()
                binding.tvShareDate.visible()
                shareView(requireActivity(), getDefaultChart())
                binding.chartChangeLayout.visible()
                binding.btnShare.visible()
                binding.tvShareDate.gone()
            }
            barChangeChart.setOnClickListener {
                barChangeChart.apply {
                    setBackgroundResource(R.drawable.chart_filled_left)
                    setIconTintResource(R.color.white)
                }
                pieChartChartButton.apply {
                    setBackgroundResource(R.drawable.border_primary_second)
                    setIconTintResource(R.color.colorPrimary)
                }
                lineChartButton.apply {
                    setBackgroundResource(R.drawable.right_border_primary_second)
                    setIconTintResource(R.color.colorPrimary)
                }
                selectedChartPosition = 0
                changeGraph(selectedChartPosition)
            }
            pieChartChartButton.setOnClickListener {

                barChangeChart.apply {
                    setBackgroundResource(R.drawable.left_border_primary_second)
                    setIconTintResource(R.color.colorPrimary)
                }
                pieChartChartButton.apply {
                    setBackgroundResource(R.drawable.chart_filled_center)
                    setIconTintResource(R.color.white)
                }
                lineChartButton.apply {
                    setBackgroundResource(R.drawable.right_border_primary_second)
                    setIconTintResource(R.color.colorPrimary)
                }

                selectedChartPosition = 1
                changeGraph(selectedChartPosition)

            }
            lineChartButton.setOnClickListener {
                barChangeChart.apply {
                    setBackgroundResource(R.drawable.left_border_primary_second)
                    setIconTintResource(R.color.colorPrimary)
                }
                pieChartChartButton.apply {
                    setBackgroundResource(R.drawable.border_primary_second)
                    setIconTintResource(R.color.colorPrimary)
                }
                lineChartButton.apply {
                    setBackgroundResource(R.drawable.chart_filled_right)
                    setIconTintResource(R.color.white)
                }
                selectedChartPosition = 2
                changeGraph(selectedChartPosition)
            }

            /*btnChangeChart.setOnClickListener {
                DialogUtils.dialogChartPopup(
                    requireContext(),
                    this@BranchContributionFragment
                )
                legendValues.clear()
                entries.clear()
                colors.clear()
                legendEntry.clear()
            }*/

/*
            btnSort.setOnClickListener {
                DialogUtils.dialogSortBy(
                    requireContext(),
                    sortBy,
                    object : DialogSingleButtonListener {
                        override fun onSingleButtonClick(str: String) {
                            sortBy = str
                            callBookingTrendsApi()
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

                            isLocalFilter = true
                            callBookingTrendsApi()
                        }
                    }
                    /*onDatesSelected = { fromDate ->
                        requireActivity().toast(fromDate)
                    },
                    onCancel = {
                        requireActivity().toast("Cancelled")
                    }*/
                )
            }
            
            allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
            allServicesAndDateFilterContainer.tvAllService.setOnClickListener {

                val intent: Intent = Intent(
                    requireContext(),
                    SearchBranchActivity::class.java
                )
                PreferenceUtils.putString(
                    getString(R.string.tag),
                    TAG
                )
                startActivityForResult(
                    intent,
                    SELECT_SERVICE_INTENT_REQUEST_CODE
                )
            }

            sortAndFilterLayout.btnFilter.setOnClickListener {
                DialogUtils.dialogJourneyByDashboard(
                    context = requireContext(),
                    journeyBy = journeyBy,
                    dialogFilterByListener = {
                        journeyBy = it
                        callBookingTrendsApi()
                        binding.sortAndFilterLayout.filterMarker.visible()
                    }
                )
            }

        }

        return view
    }

    override fun isInternetOnCallApisAndInitUI() {

        if (isAttachedToActivity()) {
            callBookingTrendsApi()
            callBranchListApi()
        }
        lifecycleScope.launch {
            blockViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            cityDetailViewModel.messageSharedFlow.collect {
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
    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    fun changeGraph(position: Int) {
        legendValues.clear()
        entries.clear()
        colors.clear()
        legendEntry.clear()
        when (position) {
            0 -> {
                PreferenceUtils.setPreference(
                    requireContext().getString(R.string.selectedChartId),
                    position
                )
                initAndSetupBarChart()
            }
            1 -> {
                PreferenceUtils.setPreference(
                    requireContext().getString(R.string.selectedChartId),
                    position
                )
                initAndSetupPieChart()
            }
            2 -> {
                PreferenceUtils.setPreference(
                    requireContext().getString(R.string.selectedChartId),
                    position
                )
                initAndSetupLineChart()
            }
        }

        firebaseLogEvent(
            requireContext(),
            CHANGE_GRAPH,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            CHANGE_GRAPH,
            ChangeGraph.CHANGE_GRAPH
        )
    }

    private fun getParentIntent() {

        if (requireActivity().intent.hasExtra(getString(R.string.dashboardGraphDefaultDateFilterSelection))) {
            defaultSelection = requireActivity().intent.getIntExtra(
                getString(R.string.dashboardGraphDefaultDateFilterSelection),
                defaultSelection
            )
        }

        if (requireActivity().intent.hasExtra(getString(R.string.dashboardGraphCurrentDate))) {
            currentSelection =
                requireActivity().intent.getStringExtra(getString(R.string.dashboardGraphCurrentDate))
                    ?: currentSelection
        }

        if (requireActivity().intent.hasExtra(getString(R.string.dashboardGraphFromDate))) {
            fromDate =
                requireActivity().intent.getStringExtra(getString(R.string.dashboardGraphFromDate))
                    ?: fromDate
            toDate = fromDate
            from = fromDate
            to = fromDate
        }

        if (requireActivity().intent.hasExtra(getString(R.string.booking_trends_journey_by_filter))) {
            journeyBy =
                requireActivity().intent.getStringExtra(getString(R.string.booking_trends_journey_by_filter))
        }

/*        if(requireActivity().intent.hasExtra(getString(R.string.dashboardGraphToDate))){
            toDate = requireActivity().intent.getStringExtra(getString(R.string.dashboardGraphToDate)) ?: toDate
        }*/

        /*if(requireActivity().intent.hasExtra(getString(R.string.dashboardGraphResId))){
            resId = requireActivity().intent.getStringExtra(getString(R.string.dashboardGraphResId))
        }*/
    }

    private fun setBookingTrendsBranchListAdapter() {

        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvOccupancyDayWise.layoutManager = layoutManager
        occupancyAllTabsDetailsAdapter =
            OccupancyAllTabsDetailsAdapter(requireActivity(),privilegeResponse, occupancyBranchList)
        binding.rvOccupancyDayWise.adapter = occupancyAllTabsDetailsAdapter
    }


    //    initAndSetupPieChart
    private fun initAndSetupPieChart() {

        if (pieChartCount == 0) {

            binding.apply {
                barChartCommon.root.gone()
                pieChartCommon.root.visible()
                lineChartCommon.root.gone()

                pieChartCommon.tvTotalSeatOccupancySeatStatus.gone()
                pieChartCommon.tvTotalSeatOccupancySeatStatusCount.gone()
            }

            setPieInitData()

        } else {
            binding.apply {
                barChartCommon.root.gone()
                pieChartCommon.root.visible()
                lineChartCommon.root.gone()
                pieChartCommon.pieChart.animateY(1000, Easing.EaseInOutQuad)
            }
            setPieInitData()
        }
        pieChartCount++
    }

    private fun setPieInitData() {
        entries.apply {
            for (i in 0 until occupancyReportList.size) {
                add(PieEntry(occupancyReportList[i].value.toFloat()))
            }
        }

        legendValues.apply {
            for (i in 0 until occupancyReportList.size) {
                add(
                    PieEntry(
                        occupancyReportList[i].value.toFloat(),
                        occupancyReportList[i].name
                    )
                )
            }
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.apply {
            sliceSpace = 1f
            selectionShift = 3f
            setAutomaticallyDisableSliceSpacing(true)
        }

        colors.apply {
            for (i in 0 until occupancyReportList.size) {
                add(
                    getDashboardChartColor(
                        requireContext(),
                        occupancyReportList[i].value.toDouble().toInt()
                    )
                )

            }
        }

        dataSet.colors = colors
        dataSet.valueLinePart1OffsetPercentage = 100f

        val data = PieData(dataSet)
        data.apply {
            setValueFormatter(PercentFormatter(binding.pieChartCommon.pieChart))
            setValueTextSize(10f)
            setValueTypeface(Typeface.DEFAULT_BOLD)
            setValueTextColor(Color.BLACK)
        }

        binding.pieChartCommon.apply {
            pieChart.data = data
            pieChart.highlightValues(null)
            pieChart.setExtraOffsets(0f, 0f, 10f, 0f)
            pieChart.invalidate()
        }

        for (i in 0 until legendValues.size) {
            val entry = LegendEntry()
            entry.formColor = colors[i]
            entry.label = legendValues[i].label
            legendEntry.add(entry)
        }
        setPieChart(binding.pieChartCommon.pieChart, legendEntry)

    }

    //    initAndSetupBarChart
    private fun initAndSetupBarChart() {
        if (barChartCount == 0) {
            binding.apply {
                barChartCommon.root.visible()
                pieChartCommon.root.gone()
                lineChartCommon.root.gone()
            }

            //occupancyReportList=getDayWiseOccupancyBarChartReportList()

            setDecimalFormatBarChart(
                requireContext(),
                binding.barChartCommon.barChart,
                occupancyReportList,
                true
            )

        } else {
            binding.apply {
                barChartCommon.root.visible()
                pieChartCommon.root.gone()
                lineChartCommon.root.gone()
                barChartCommon.barChart.animateY(1000)
            }
        }
        barChartCount++
        setDecimalFormatBarChart(
            requireContext(),
            binding.barChartCommon.barChart,
            occupancyReportList,
            true
        )
    }

    //    initAndSetupLineChart
    private fun initAndSetupLineChart() {

        //lineChartTitleList=getDayWiseOccupancyBarChartReportList()
        lineChartTitleList = occupancyReportList
        if (lineChartCount == 0) {
            binding.apply {
                barChartCommon.root.gone()
                pieChartCommon.root.gone()
                lineChartCommon.root.visible()
            }
            setLineChart(
                requireContext(),
                binding.lineChartCommon.lineChart,
                lineChartTitleList,
                lineChartValueList(lineChartTitleList),
                false,
                true
            )

        } else {
            binding.apply {
                barChartCommon.root.gone()
                pieChartCommon.root.gone()
                lineChartCommon.root.visible()
                lineChartCommon.lineChart.animateY(1000)
            }

            setLineChart(
                requireContext(),
                binding.lineChartCommon.lineChart,
                lineChartTitleList,
                lineChartValueList(lineChartTitleList),
                false,
                true
            )
        }
        lineChartCount++
    }

    private fun lineChartValueList(occupancyReportList: ArrayList<ReportValue>): ArrayList<Entry> {
        val values = ArrayList<Entry>()
        for (i in 0 until occupancyReportList.size) {
            values.add(Entry(i.toFloat(), occupancyReportList[i].value.toFloat()))
        }
        return values
    }


    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {
        /*when (position) {
            0 -> {
                PreferenceUtils.setPreference(
                    requireContext().getString(R.string.selectedChartId),
                    position
                )
                initAndSetupBarChart()
            }
            1 -> {
                PreferenceUtils.setPreference(
                    requireContext().getString(R.string.selectedChartId),
                    position
                )
                initAndSetupPieChart()
            }
            2 -> {
                PreferenceUtils.setPreference(
                    requireContext().getString(R.string.selectedChartId),
                    position
                )
                initAndSetupLineChart()
            }
        }*/
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        defaultSelection = PreferenceUtils.getPreference(
            DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION, 1
        )?.toInt() ?: 1
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = fromDate
        from = fromDate
        to = fromDate
        privilegeResponse = (activity as BaseActivity).getPrivilegeBase()
    }

/*
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
                dashboardDateSetText(
                    textView = binding.tvShareDate,
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
                dashboardDateSetText(
                    textView = binding.tvShareDate,
                    fromDate = fromDate,
                    toDate = null,
                    inputDateFormat = DATE_FORMAT_Y_M_D
                )
            }

            from = fromDate
            to = toDate
            val reqBody =
                com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.request.ReqBody(
                    apiKey = loginModelPref.api_key,
                    originId = -1,
                    destinationId = -1,
                    serviceId = "-1",
                    branchId = branchId,
                    from = from,
                    to = to,
                    sortBy = sortBy,
                    apiType = 2
                )

*/
/*            if(getDaysDifference(fromDate, toDate, DATE_FORMAT_Y_M_D) < 2 && !isLocalFilter) {

                reqBody.from = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).minusDays(1).toString()
                reqBody.to = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).plusDays(1).toString()

            }*//*


            val bookingTrendsRequest =
                BookingTrendsRequest(
                    bccId = bccId.toString(),
                    methodName = booking_trends,
                    format = format_type,
                    reqBody = reqBody
                )

            /*dashboardViewModel.bookingTrendsDetailsApi(
            authorization = loginModelPref.auth_token,
            apiKey = loginModelPref.api_key,
            bookingTrendsRequest = bookingTrendsRequest,
            apiType = revenue_details
        )*/

            dashboardViewModel.bookingTrendsDetailsApi(
                bookingTrendsRequest = reqBody,
                apiType = revenue_details
            )

        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                dashboardDetailsBranchLayout.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }
*/

    private fun callBookingTrendsApi() {
        startShimmerEffect()
        if (requireActivity().isNetworkAvailable()) {


            if(defaultSelection == 3 || defaultSelection == 4 || defaultSelection == 6) {
                dashboardDateSetText(
                    textView = binding.allServicesAndDateFilterContainer.tvDate,
                    fromDate = fromDate,
                    toDate = toDate,
                    inputDateFormat = DATE_FORMAT_Y_M_D
                )
                dashboardDateSetText(
                    textView = binding.tvShareDate,
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
                dashboardDateSetText(
                    textView = binding.tvShareDate,
                    fromDate = fromDate,
                    toDate = null,
                    inputDateFormat = DATE_FORMAT_Y_M_D
                )
            }

            from = fromDate

            dashboardViewModel.performanceDetailsApi(
                apiKey = loginModelPref.api_key,
                originId = -1,
                destinationId = -1,
                from = from,
                to = to,
                sortBy = sortBy,
                serviceId = "-1",
                branchId = branchId,
                apiType = 2,
                is3DaysData = false,
                locale = locale ?: "en",
                is_from_middle_tier = true,
                journeyBy = journeyBy,
                methodName = booking_trends
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                dashboardDetailsBranchLayout.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun setUpObserver() {
        dashboardViewModel.bookingTrendsResponseViewModel.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayoutDashboard.isRefreshing = false
            stopShimmerEffect()
            try {

                if (it.code == 200) {
                    try {
                        occupancyReportList.clear()
                        occupancyBranchList.clear()

                        if (it.result.branchProfitPerformance?.isNotEmpty() == true) {
                            it.result.branchProfitPerformance.forEachIndexed { i, element ->
//                        if (DASHBOARD_CHART_LIMIT > occupancyBranchList.size) { }
                                occupancyReportList.add(
                                    ReportValue(
                                        it.result.branchProfitPerformance[i].branch.substring(
                                            SUBSTRING_START,
                                            min(
                                                SUBSTRING_END,
                                                it.result.branchProfitPerformance[i].branch.length
                                            )
                                        ).plus(CUSTOM_ELLIPSIS),
                                        it.result.branchProfitPerformance[i].performance.toDouble()
                                            .toString(),
                                        getDashboardChartColor(
                                            requireContext(),
                                            it.result.branchProfitPerformance[i].performance.toFloat()
                                                .roundToInt()
                                        ),
                                        it.result.branchProfitPerformance[i].branch,
                                        "${it.result.branchProfitPerformance[i].performance.toFloat()}%"
                                    )
                                )
                                occupancyBranchList.add(
                                    OccupancyAllTabsDetailsModel(
                                        element.branch,
                                        "${element.performance}%"
                                    )
                                )
                            }

                            binding.dashboardDetailsBranchLayout.visible()
                            binding.dashboardDetailsBranchCard.visible()
                            binding.noData.root.gone()

                            setDefaultChart()
                            setBookingTrendsBranchListAdapter()
                        } else {
                            binding.dashboardDetailsBranchLayout.visible()
                            binding.dashboardDetailsBranchCard.gone()
                            binding.noData.root.visible()
                        }

                    } catch (e: Exception) {
                        if (it?.message != null) {
                            it.message.let { it1 -> requireContext().toast(it1) }
                        }
                        Timber.d("Error in BranchContributionFrargment Occupancy DetailsObserver ${e.message}")
                    }
                } else {
                    if (it?.message != null) {
                        stopShimmerEffect()
                        binding.noData.root.visible()
                        binding.dashboardDetailsBranchLayout.visible()
                        binding.dashboardDetailsBranchCard.gone()
                        binding.noData.tvNoData.text = "${it.message}}"
                    } else {
                        stopShimmerEffect()
                        binding.noData.root.visible()
                        binding.dashboardDetailsBranchLayout.visible()
                        binding.dashboardDetailsBranchCard.gone()
                        binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
                    }
                }
            } catch (e: Exception) {
                if (it?.message != null) {
                    it.message.let { it1 -> requireContext().toast(it1) }
                }
                Timber.d("Error in BranchContributionFragment BookingTrends Observer ${e.message}")
            }

        }

        cityDetailViewModel.cityDetailResponse.observe(requireActivity()) {
            try {

                if (it != null) {
                    if (it.code == 200) {
                        if (it.result != null && it.result.isNotEmpty()) {
                            originCityList = it.result
                            PreferenceUtils.putObject(it, "cityListModel")
                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }

            } catch (e: Exception) {
                requireActivity().toast(getString(R.string.opps))
                Timber.d("Error in BranchContributionFragment CityDetails Observer ${e.message}")
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if (data != null) {
        if (resultCode == SELECT_BRANCH_INTENT_REQUEST_CODE) {

            val branchListModel =
                PreferenceUtils.getObject<BranchList>(
                    BRANCH_WISE_MODEL_BRANCH_CONTRIBUTION
                )
            var totalSelectedServices = 0
            branchListModel?.branchList?.forEach {
                if (it.isChecked)
                    totalSelectedServices++
            }

            if (totalSelectedServices > 0 && totalSelectedServices < (branchListModel?.branchList?.size
                    ?: 0)
            ) {

                branchId = ""
                selectedServiceName = ""

                branchListModel?.branchList?.forEach {
                    if (it.isChecked) {
                        branchId += it.id.toString() + ","
                        selectedServiceName += it.value.toString() + ","
                    }
                }


            } else {

                branchId = "-1"
                selectedServiceName = "${getString(R.string.all_branches)} (${branchListModel?.branchList?.size ?: ""})"
            }

            if (branchId == "-1" && totalSelectedServices == (branchListModel?.branchList?.size
                    ?: 0)
            ) {

                selectedServiceName = "${getString(R.string.all_branches)} (${totalSelectedServices})"
            } else {

                selectedServiceName =
                    "${getString(R.string.branch)} ($totalSelectedServices)"

                branchId =
                    branchId.substring(0, branchId.lastIndexOf(","))


            }
            binding.allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
            callBookingTrendsApi()

        }
    }

    private fun setDefaultChart() {
        when (PreferenceUtils.getPreference(
            requireContext().getString(R.string.selectedChartId),
            0
        )) {
            0 -> {
                initAndSetupBarChart()
            }
            1 -> {
//                initAndSetupPieChart()
                initAndSetupBarChart()
            }
            2 -> {
                initAndSetupLineChart()
            }
            else -> {
                initAndSetupPieChart()
            }
        }

    }

    private fun getDefaultChart(): View {

        when (PreferenceUtils.getPreference(
            requireContext().getString(R.string.selectedChartId),
            0
        )) {
            0 -> {
                return binding.chartViewContainer
            }
            1 -> {
                return binding.chartViewContainer
            }
            2 -> {
                return binding.chartViewContainer
            }
            else -> {
                return binding.chartViewContainer
            }
        }
    }

    private fun callBranchListApi() {
        if (requireActivity().isNetworkAvailable()) {
            val reqBody =
                com.bitla.ts.domain.pojo.branch_list_model.request.ReqBody(
                    loginModelPref.api_key,
                    locale = locale
                )
            val branchListRequest =
                BranchListRequest(bccId.toString(), format_type, branch_list_method_name, reqBody)

            blockViewModel.branchListApi(
                loginModelPref.api_key,
                locale!!,
                branch_list_method_name
            )

        } else
            requireActivity().noNetworkToast()
    }

    private fun setBranchListObserver() {
        blockViewModel.branchList.observe(requireActivity()) {

            branchList.clear()
            try {
                if (it != null) {
                    if (it.branchlists.isNotEmpty()) {
                        it.branchlists.forEach { it ->
                            val branchItem = Branch(it.id, it.label, true)
                            branchList.add(branchItem)
                        }
                    }

                    branchId = "-1"
                    selectedServiceName = "${getString(R.string.all_branches)} (${it.branchlists.size})"
                    binding.allServicesAndDateFilterContainer.tvAllService.text =
                        selectedServiceName

                    PreferenceUtils.putString(
                        BRANCH_CONTRIBUTION_SERVICE_ID,
                        branchId
                    )
                    PreferenceUtils.putString(
                        BRANCH_CONTRIBUTION_SERVICE_NAME,
                        selectedServiceName
                    )
                    PreferenceUtils.putObject(
                        BranchList(branchList),
                        BRANCH_WISE_MODEL_BRANCH_CONTRIBUTION
                    )


                    callBookingTrendsApi()

                } else {
                    requireActivity().toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                requireActivity().toast(getString(R.string.opps))
                Timber.d("An error occurred while fetching Branch List")
            }
        }

    }

    private fun callCityDetailsApi() {
        if (requireContext().isNetworkAvailable()) {
            val cityDetailRequest = CityDetailRequest(
                bccId.toString(),
                city_Details_method_name,
                format_type,
                ReqBody(
                    loginModelPref.api_key,
                    response_format,
                    locale = locale
                )
            )
            /*cityDetailViewModel.cityDetailAPI(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                cityDetailRequest,
                city_Details_method_name
            )*/

            cityDetailViewModel.cityDetailAPI(
                loginModelPref.api_key,
                response_format,
                locale!!,
                city_Details_method_name
            )
        } else requireContext().noNetworkToast()
    }

    override fun onSingleButtonClick(str: String) {
        if (str == "cancel") {
            //cityIdOrigin = -1
        } else if (str == "filter") {
            originIdFinal = cityIdOrigin
            destinationIdFinal = cityIdDestination
            from = fromDate
            to = toDate
            PreferenceUtils.putString(BRANCH_CONTRIBUTION_FROM_DATE, from)
            PreferenceUtils.putString(BRANCH_CONTRIBUTION_TO_DATE, to)
            //PreferenceUtils.putString(BRANCH_CONTRIBUTION_ORIGIN_CITY_NAME, originCityName)
            PreferenceUtils.putString(BRANCH_CONTRIBUTION_ORIGIN_CITY_NAME, selectedServiceName)
            PreferenceUtils.putString(
                BRANCH_CONTRIBUTION_DESTINATION_CITY_NAME,
                destinationCityName
            )
            PreferenceUtils.putString(BRANCH_CONTRIBUTION_ORIGIN_CITY_ID, originIdFinal.toString())
            PreferenceUtils.putString(
                BRANCH_CONTRIBUTION_DESTINATION_CITY_ID,
                destinationIdFinal.toString()
            )
            PreferenceUtils.putString(
                BRANCH_CONTRIBUTION_SERVICE_ID,
                branchId
            )
            PreferenceUtils.putString(
                BRANCH_CONTRIBUTION_SERVICE_NAME,
                selectedServiceName
            )
            occupancyReportList.clear()
            occupancyBranchList.clear()
            legendValues.clear()
            entries.clear()
            colors.clear()
            legendEntry.clear()
            callBookingTrendsApi()
        }
    }


    fun onItemData(view: View, str1: String, str2: String) {
        etFromDate = view as TextInputEditText
        fromDate = str1
        isFromDate = true
        /*var minDate = convertDateYYYYMMDDtoDDMMYY(PreferenceUtils.getDashboardCurrentDate())
        if(str1.isEmpty().not()) {
            minDate = convertDateYYYYMMDDtoDDMMYY(str1)
        } else {
            fromDate = PreferenceUtils.getDashboardCurrentDate()
        }*/
        if (str1.isEmpty()) {
            fromDate = PreferenceUtils.getDashboardCurrentDate()
        }
        SlyCalendarDialog()
            .setMinDate(stringToDate("1970-01-01", DATE_FORMAT_D_M_Y))
            //.setMinDate(getLast31DayDate(stringToDate(minDate, DATE_FORMAT_D_M_Y) ?: Date()))
            //.setMaxDate(getNext31DayDate(stringToDate(minDate, DATE_FORMAT_D_M_Y) ?: Date()))
            .setStartDate(stringToDate(fromDate, DATE_FORMAT_Y_M_D))
            .setSingle(true)
            .setFirstMonday(false)
            .setCallback(this)
            .show((context as AppCompatActivity).supportFragmentManager, TAG)
    }

    fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
        etToDate = view as TextInputEditText
        fromDate = str1
        if (fromDate != "") {
            isFromDate = false
            fromDate = convertDateYYYYMMDDtoDDMMYY(etFromDate.text.toString())

            SlyCalendarDialog()
                .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                .setEndDate(stringToDate(toDate, DATE_FORMAT_Y_M_D))
                .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                .setMaxDate(getNext31DayDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y) ?: Date()))
                .setSingle(false)
                .setFirstMonday(false)
                .setCallback(this)
                .show((context as AppCompatActivity).supportFragmentManager, TAG)
            fromDate = etFromDate.text.toString()

        } else {
            requireContext().toast("Enter From date")
        }
    }

    override fun onCancelled() {
        if (etFromDate.text?.isNotEmpty() == true) {
            fromDate = convertDateYYYYMMDDtoDDMMYY(etFromDate.text.toString())
        }
        isFromDate = !isFromDate
    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?,
        hours: Int,
        minutes: Int
    ) {
        try {
            if (isSelectDate == false) {

                if (secondDate == null) {
                    if (isFromDate) {
                        fromDate = SimpleDateFormat(DATE_FORMAT_Y_M_D).format(firstDate?.time)
                        etFromDate.setText(SimpleDateFormat(DATE_FORMAT_Y_M_D).format(firstDate?.time))
                    } else {
                        toDate = SimpleDateFormat(DATE_FORMAT_Y_M_D).format(firstDate?.time)
                        etToDate.setText(toDate)
                    }
                } else {
                    if (!isFromDate) {
                        fromDate = SimpleDateFormat(DATE_FORMAT_Y_M_D).format(firstDate?.time)
                        etFromDate.setText(SimpleDateFormat(DATE_FORMAT_Y_M_D).format(firstDate?.time))

                        toDate = SimpleDateFormat(DATE_FORMAT_Y_M_D).format(secondDate.time)
                        etToDate.setText(toDate)

                    }
                }
            } else {
                fromDate = SimpleDateFormat(DATE_FORMAT_Y_M_D).format(firstDate?.time)
                etFromDate.setText(SimpleDateFormat(DATE_FORMAT_Y_M_D).format(firstDate?.time))
            }
        } catch (e: Exception) {
            Timber.d("Error in SlyCalendar ${e.message}")
            if (etFromDate.text?.isNotEmpty() == true) {
                fromDate = convertDateYYYYMMDDtoDDMMYY(etFromDate.text.toString())
            }
            isFromDate = !isFromDate
        }
    }

    private fun startShimmerEffect() {
        binding.apply {
            sortLayout.root.gone()
            sortAndFilterLayout.root.gone()
            shimmerDashboardDetails.visible()
            dashboardDetailsBranchLayout.gone()
            shimmerDashboardDetails.startShimmer()
            noData.root.gone()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            if(privilegeResponse?.country.equals("India", true)) {
                sortLayout.root.visible()
                sortAndFilterLayout.root.gone()
            } else {
                sortLayout.root.gone()
                sortAndFilterLayout.root.visible()
            }
            shimmerDashboardDetails.gone()
            dashboardDetailsBranchLayout.visible()
            if (shimmerDashboardDetails.isShimmerStarted) {
                shimmerDashboardDetails.stopShimmer()
            }
        }
    }
}