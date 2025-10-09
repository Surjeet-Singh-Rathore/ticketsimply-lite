package com.bitla.ts.phase2.fragments.childFragments.revenueFragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.branch_list_method_name
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.revenue_details
import com.bitla.ts.databinding.FragmentAgentWiseNetRevenueBinding
import com.bitla.ts.domain.pojo.BranchModel.Branch
import com.bitla.ts.domain.pojo.BranchModel.BranchList
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.adapter.child.OccupancyAllTabsDetailsAdapter
import com.bitla.ts.phase2.chartUtils.ReportValue
import com.bitla.ts.phase2.dashboard_pojo.OccupancyAllTabsDetailsModel
import com.bitla.ts.presentation.view.activity.SearchBranchActivity
import com.bitla.ts.presentation.viewModel.BlockViewModel
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import kotlin.math.min

class AgentWiseNetRevenueFragment : BaseFragment() {

    private lateinit var binding: FragmentAgentWiseNetRevenueBinding

    private lateinit var occupancyAllTabsDetailsAdapter: OccupancyAllTabsDetailsAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var occupancyDayWiseList = arrayListOf<OccupancyAllTabsDetailsModel>()
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
    private var currentSelection = ""
    private var fromDate = ""
    private var toDate = ""
    private var from = getDateYMD(getTodayDate())
    private var to = getDateYMD(getTodayDate())
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private var branchList: MutableList<Branch> = mutableListOf()
    private var branchId = "-1"
    private var selectedServiceName = "All Branches"
    private var defaultSelection = -1
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
    private var selectedChartPosition = 0
    private var privilegeResponse: PrivilegeResponseModel? = null

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgentWiseNetRevenueBinding.inflate(inflater, container, false)
        val view = binding.root
        PreferenceUtils.removeKey(BRANCH_WISE_REVENUE_TO_DATE)
        PreferenceUtils.removeKey(BRANCH_WISE_REVENUE_FROM_DATE)
        //PreferenceUtils.removeKey(BRANCH_WISE_REVENUE_ORIGIN_CITY_NAME)
        PreferenceUtils.removeKey(BRANCH_WISE_REVENUE_DESTINATION_CITY_NAME)
        PreferenceUtils.removeKey(BRANCH_WISE_MODEL_BRANCH_WISE_REVENUE)

        getPref()

        getParentIntent()

        if (defaultSelection == 5) {
            isCustomDateFilterSelected = true
        }

        if (defaultSelection == 6) {
            isCustomDateRangeFilterSelected = true
        }

        PreferenceUtils.putString(BRANCH_WISE_REVENUE_ORIGIN_CITY_NAME, selectedServiceName)
        //PreferenceUtils.putString(BRANCH_WISE_REVENUE_FROM_DATE, fromDate)

        //callCityDetailsApi()
        //callRevenueDetailsApi()
        callBranchListApi()
        setBranchListObserver()
        setUpObserver()

        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            binding.swipeRefreshLayoutDashboard.isRefreshing = true
            occupancyReportList.clear()
            occupancyDayWiseList.clear()
            legendValues.clear()
            entries.clear()
            colors.clear()
            legendEntry.clear()
            //callCityDetailsApi()
            callRevenueDetailsApi()
        }

        binding.apply {


            tvChartStatusTitle.text = "(${privilegeResponse?.currency}) ${requireActivity().getString(R.string.total_revenue)}"
            sortLayout.btnSort.setOnClickListener {
                DialogUtils.dialogSortBy(
                    context = requireContext(),
                    sortBy = sortBy,
                    newLowToHighLabel = requireActivity().getString(R.string.branch_wise_revenue_sort_ltoh),
                    newHighToLowLabel = requireActivity().getString(R.string.branch_wise_revenue_sort_htol),
                    showOccupancyFilter = true,
                    showDateFilter = false,
                    showNetAmountFilter = false,

                    object : DialogSingleButtonListener {
                        override fun onSingleButtonClick(str: String) {
                            sortBy = str
                            callRevenueDetailsApi()
                            binding.sortLayout.filterMarker.visible()
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
                    this@BranchWiseRevenueFragment
                )

                legendValues.clear()
                entries.clear()
                colors.clear()
                legendEntry.clear()
            }*/


            /*btnSort.setOnClickListener {
                DialogUtils.dialogSortBy(
                    requireContext(),
                    sortBy,
                    object : DialogSingleButtonListener {
                        override fun onSingleButtonClick(str: String) {
                            sortBy = str
                            callRevenueDetailsApi()
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

                            isLocalFilter = true
                            callRevenueDetailsApi()
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

                val intent = Intent(
                    requireContext(),
                    SearchBranchActivity::class.java
                )
                PreferenceUtils.putString(
                    requireActivity().getString(R.string.tag),
                    BranchWiseRevenueFragment.TAG
                )
                startActivityForResult(
                    intent,
                    SELECT_SERVICE_INTENT_REQUEST_CODE
                )
            }
        }

        firebaseLogEvent(
            requireContext(),
            SORT_BTN,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            SORT_BTN,
            "Agent-wise Revenue sort"
        )

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            blockViewModel.messageSharedFlow.collect {
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
            "Change Graph"
        )
    }

    private fun getParentIntent() {

        if (requireActivity().intent.hasExtra(requireActivity().getString(R.string.dashboardGraphDefaultDateFilterSelection))) {
            defaultSelection = requireActivity().intent.getIntExtra(
                requireActivity().getString(R.string.dashboardGraphDefaultDateFilterSelection),
                defaultSelection
            )
        }

        if (requireActivity().intent.hasExtra(requireActivity().getString(R.string.dashboardGraphCurrentDate))) {
            currentSelection =
                requireActivity().intent.getStringExtra(requireActivity().getString(R.string.dashboardGraphCurrentDate))
                    ?: currentSelection
        }

        if (requireActivity().intent.hasExtra(requireActivity().getString(R.string.dashboardGraphFromDate))) {
            fromDate =
                requireActivity().intent.getStringExtra(requireActivity().getString(R.string.dashboardGraphFromDate))
                    ?: fromDate
            toDate = fromDate
            from = fromDate
            to = fromDate
        }

//        if(requireActivity().intent.hasExtra(getString(R.string.dashboardGraphToDate))){
//            toDate = requireActivity().intent.getStringExtra(getString(R.string.dashboardGraphToDate)) ?: toDate
//        }

        /*if(requireActivity().intent.hasExtra(getString(R.string.dashboardGraphResId))){
            resId = requireActivity().intent.getStringExtra(getString(R.string.dashboardGraphResId))
        }*/
    }

    private fun setOccupancyDayWiseListAdapter() {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvOccupancyDayWise.layoutManager = layoutManager
        occupancyAllTabsDetailsAdapter =
            OccupancyAllTabsDetailsAdapter(
                requireActivity(),
                privilegeResponse,
                occupancyDayWiseList,
                showGrossNetAmount = false,
                true
            )
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

            setCurrencyBranchFormatBarChart(
                requireContext(),
                binding.barChartCommon.barChart,
                occupancyReportList,
                true,
                isNegativeValue
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
        setCurrencyBranchFormatBarChart(
            requireContext(),
            binding.barChartCommon.barChart,
            occupancyReportList,
            true,
            isNegativeValue
        )
    }

    //    initAndSetupLineChart
    private fun initAndSetupLineChart() {

        lineChartTitleList = occupancyReportList
        if (lineChartCount == 0) {
            binding.apply {
                barChartCommon.root.gone()
                pieChartCommon.root.gone()
                lineChartCommon.root.visible()
            }
            setCurrencyLineChart(
                requireContext(),
                binding.lineChartCommon.lineChart,
                lineChartTitleList,
                lineChartValueList(lineChartTitleList),
                true,
                true
            )

        } else {
            binding.apply {
                barChartCommon.root.gone()
                pieChartCommon.root.gone()
                lineChartCommon.root.visible()
                lineChartCommon.lineChart.animateY(1000)
            }

            setCurrencyLineChart(
                requireContext(),
                binding.lineChartCommon.lineChart,
                lineChartTitleList,
                lineChartValueList(lineChartTitleList),
                true,
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
        privilegeResponse = (activity as BaseActivity).getPrivilegeBase()
        locale = PreferenceUtils.getlang()
        defaultSelection = PreferenceUtils.getPreference(
            DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION, 1
        )?.toInt() ?: 1
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = PreferenceUtils.getDashboardToFutureDate()
        from = fromDate
        to = toDate

    }

/*
    private fun callRevenueDetailsApi() {
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
                com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.request.ReqBody(
                    apiKey = loginModelPref.api_key,
                    originId = -1,
                    destination = -1,
                    serviceId = "-1",
                    branchId = branchId,
                    from = from,
                    to = from,
                    sortBy = sortBy,
                    apiType = 1
                )

*/
/*            if(getDaysDifference(fromDate, toDate, DATE_FORMAT_Y_M_D) < 2 && !isLocalFilter) {

                reqBody.from = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).minusDays(1).toString()
                reqBody.to = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE).plusDays(1).toString()

            }*//*


            val revenueDetailsRequest =
                RevenueDetailsRequest(
                    bccId = bccId.toString(),
                    methodName = revenue_details,
                    format = format_type,
                    reqBody = reqBody
                )

            dashboardViewModel.revenueDetails(
                reqBody,
                loginModelPref.api_key,
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                dashboardDetailsDayWiseLayout.gone()
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
            to = toDate

            dashboardViewModel.getRevenueDetails(
                apiKey = loginModelPref.api_key,
                originId = -1,
                destinationId = -1,
                from = fromDate,
                to = toDate,
                sortBy = sortBy,
                serviceId = "-1",
                branchId =  branchId,
                apiType = 6,
                is3DaysData = isLocalFilter.not(),
                locale = locale ?: "en",
                is_from_middle_tier = true,
                methodName = revenue_details
            )

        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                dashboardDetailsDayWiseLayout.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private var isNegativeValue = false
    private fun setUpObserver() {
        dashboardViewModel.revenueDetailsResponseViewModel.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayoutDashboard.isRefreshing = false
            stopShimmerEffect()

            if (it.code == 200) {
                try {
                    occupancyReportList.clear()
                    occupancyDayWiseList.clear()
                    val occupancyDetailsResponse = it

                    if (occupancyDetailsResponse.result.agentWiseNetRevenue?.isNotEmpty() == true) {
                        occupancyDetailsResponse.result.agentWiseNetRevenue.forEachIndexed { i, _ ->
//                        if (DASHBOARD_CHART_LIMIT > occupancyReportList.size) { }

                            val newRevenue =
                                occupancyDetailsResponse.result.agentWiseNetRevenue[i]?.revenue.toString()
                                    .replace(privilegeResponse?.currency ?: "", "").toDouble()
                                    .convert(privilegeResponse?.currencyFormat ?: requireActivity().getString(R.string.indian_currency_format))
                            if (occupancyDetailsResponse.result.agentWiseNetRevenue[i]?.revenue?.toDouble()!! < 0) {
                                isNegativeValue = true
                            }
                            occupancyReportList.add(
                                ReportValue(
                                    occupancyDetailsResponse.result.agentWiseNetRevenue[i]?.branch?.substring(
                                        SUBSTRING_START, min(
                                            SUBSTRING_END,
                                            occupancyDetailsResponse.result.agentWiseNetRevenue[i]?.branch?.length ?: 0
                                        )
                                    ).plus(CUSTOM_ELLIPSIS),
                                    occupancyDetailsResponse.result.agentWiseNetRevenue[i]?.revenue?.toDouble()
                                        .toString(),
                                    getDashboardChartColor(
                                        requireContext(),
                                        occupancyDetailsResponse.result.agentWiseNetRevenue[i]?.revenue?.toDouble()
                                            ?.toInt() ?: 0
                                    ),
                                    occupancyDetailsResponse.result.agentWiseNetRevenue[i]?.branch,
                                    "${privilegeResponse?.currency}${newRevenue}"
                                )
                            )

                            occupancyDayWiseList.add(
                                OccupancyAllTabsDetailsModel(
                                    occupancyDetailsResponse.result.agentWiseNetRevenue[i]?.branch,
                                    "${privilegeResponse?.currency}${occupancyDetailsResponse.result.agentWiseNetRevenue[i]?.revenue}",
                                    grossRevenue = "${privilegeResponse?.currency} 0.0",
                                    netRevenue = "${privilegeResponse?.currency} 0.0"
                                )
                            )
                        }

                        setOccupancyDayWiseListAdapter()
                        setDefaultChart()
                        binding.dashboardDetailsDayWiseCard.visible()
                        binding.noData.root.gone()
                    } else {
                        binding.dashboardDetailsDayWiseCard.gone()
                        binding.noData.root.visible()
                    }


                } catch (e: Exception) {
                    if (it?.message != null) {
                        it.message.let { it1 -> requireContext().toast(it1) }
                    }
                    Timber.d("Error in AgentWiseRevenueFragment OccupancyDetails Observer ${e.message}")
                }
            } else {
                if (it?.message != null) {
                    stopShimmerEffect()
                    binding.dashboardDetailsDayWiseCard.gone()
                    binding.noData.root.visible()
                    binding.noData.tvNoData.text = "${it.message}}"
                } else {
                    stopShimmerEffect()
                    binding.dashboardDetailsDayWiseCard.gone()
                    binding.noData.root.visible()
                    binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
                }
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
                    BRANCH_WISE_MODEL_BRANCH_WISE_REVENUE
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
                        selectedServiceName += it.value + ","
                    }
                }


            } else {

                branchId = "-1"
                selectedServiceName = "${requireActivity().getString(R.string.all_branches)} (${branchListModel?.branchList?.size ?: ""})"
            }

            if (branchId == "-1" && totalSelectedServices == (branchListModel?.branchList?.size
                    ?: 0)
            ) {

                selectedServiceName = "${requireActivity().getString(R.string.all_branches)} (${totalSelectedServices})"
            } else {

                selectedServiceName =
                    "${requireActivity().getString(R.string.branch)} ($totalSelectedServices)"

                branchId =
                    branchId.substring(0, branchId.lastIndexOf(","))


            }
            binding.allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
            callRevenueDetailsApi()

        }
        //}
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
                        it.branchlists.forEach {
                            val branchItem = Branch(it.id, it.label, true)
                            branchList.add(branchItem)
                        }
                    }

                    branchId = "-1"
                    selectedServiceName = "${requireActivity().getString(R.string.all_branches)} (${it.branchlists.size})"
                    binding.allServicesAndDateFilterContainer.tvAllService.text =
                        selectedServiceName

                    PreferenceUtils.putString(
                        BRANCH_WISE_REVENUE_SERVICE_ID,
                        branchId
                    )
                    PreferenceUtils.putString(
                        BRANCH_WISE_REVENUE_SERVICE_NAME,
                        selectedServiceName
                    )
                    PreferenceUtils.putObject(
                        BranchList(branchList),
                        BRANCH_WISE_MODEL_BRANCH_WISE_REVENUE
                    )


                    callRevenueDetailsApi()

                } else {
                    requireActivity().toast(requireActivity().getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                requireActivity().toast(requireActivity().getString(R.string.opps))
                Timber.d("An error occurred while fetching Branch List")
            }
        }

    }

    private fun startShimmerEffect() {
        binding.apply {
            sortLayout.root.gone()
            shimmerDashboardDetails.visible()
            dashboardDetailsDayWiseLayout.gone()
            shimmerDashboardDetails.startShimmer()
            noData.root.gone()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            sortLayout.root.visible()
            shimmerDashboardDetails.gone()
            dashboardDetailsDayWiseLayout.visible()
            if (shimmerDashboardDetails.isShimmerStarted) {
                shimmerDashboardDetails.stopShimmer()
            }
        }
    }
}