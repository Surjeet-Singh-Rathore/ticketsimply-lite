package com.bitla.ts.phase2.fragments.childFragments.revenueFragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
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
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.revenue_details
import com.bitla.ts.databinding.FragmentUnsoldSeatsBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.adapter.child.EmptySeatTabsDetailsAdapter
import com.bitla.ts.phase2.chartUtils.ReportValue
import com.bitla.ts.phase2.dashboard_pojo.OccupancyAllTabsDetailsModel
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
import com.bitla.ts.presentation.view.fragments.EmptySeatsFragment
import com.bitla.ts.presentation.viewModel.CityDetailViewModel
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
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
import kotlin.math.min

class EmptySeatsFragment : BaseFragment(), OnItemClickListener {

    companion object {
        val TAG = EmptySeatsFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentUnsoldSeatsBinding

    private lateinit var occupancyAllTabsDetailsAdapter: EmptySeatTabsDetailsAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var occupancyDayWiseList = arrayListOf<OccupancyAllTabsDetailsModel>()
    private var occupancyReportList = ArrayList<ReportValue>()
    private var occupancyReportFullList = ArrayList<ReportValue>()

    private var barChartCount = 0
    private var pieChartCount = 0
    private var lineChartCount = 0
    private val entries: ArrayList<PieEntry> = ArrayList()
    private val legendValues: ArrayList<PieEntry> = ArrayList()
    private val colors: ArrayList<Int> = ArrayList()
    private val legendEntry: MutableList<LegendEntry> = ArrayList()
    private var lineChartTitleList: ArrayList<ReportValue> = ArrayList()
    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var sortBy = "htol"
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
    private var hideCustomDateRangeFilter: Boolean = false
    private var isCustomDateFilterSelected: Boolean = false
    private var isCustomDateRangeFilterSelected: Boolean = false
    private var isLocalFilter = false
    private var allottedServicesResponseModel: AllotedServicesResponseModel? = null
    private var serviceId = "-1"
    private var totalOverallServices = 0
    private var selectedServiceName = "All Services"
    private var dashboardServiceFilterConf = DashboardServiceFilterConf()
    private var selectedChartPosition = 0
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var serviceSize: Int? = 0
    private var privilegeResponse: PrivilegeResponseModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUnsoldSeatsBinding.inflate(inflater, container, false)
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

        getParentIntent()

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

            tvChartStatusTitle.text = "(${privilegeResponse?.currency}) ${getString(R.string.total_revenue)}"

            sortLayout.btnSort.setOnClickListener {
                DialogUtils.dialogSortBy(
                    context = requireContext(),
                    sortBy = sortBy,
                    newLowToHighLabel = requireActivity().getString(R.string.empty_seats_revenue_sort_ltoh),
                    newHighToLowLabel = requireActivity().getString(R.string.empty_seats_revenue_sort_htol),
                    showOccupancyFilter = true,
                    showDateFilter = false,
                    showNetAmountFilter = false,

                    object : DialogSingleButtonListener {
                        override fun onSingleButtonClick(str: String) {
                            sortBy = str
                            callRevenueDetailsApi()
                            binding.sortLayout.filterMarker.visible()

                            firebaseLogEvent(
                                requireContext(),
                                SORT_BTN,
                                loginModelPref.userName,
                                loginModelPref.travels_name,
                                loginModelPref.role,
                                SORT_BTN,
                                SortBtn.EMPTY_SEATS_WISE_REVENUE_SORT
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
                    this@EmptySeatsFragment
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
//                            callRevenueDetailsApi()

                            dashboardServiceFilterConf.fromId = ""
                            dashboardServiceFilterConf.toId = ""
                            dashboardServiceFilterConf.fromTitle = getString(R.string.all_cities)
                            dashboardServiceFilterConf.toTitle = getString(R.string.all_cities)
                            dashboardServiceFilterConf.hubTitle = ""
                            dashboardServiceFilterConf.hubId = ""
                            dashboardServiceFilterConf.isHub = false

                            serviceId = "-1"

                            callAllottedServiceApi(fromDate, toDate)
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

        return view
    }

    override fun isInternetOnCallApisAndInitUI() {
        if (isAttachedToActivity()) {
            callRevenueDetailsApi()
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

//        if(requireActivity().intent.hasExtra(getString(R.string.dashboardGraphToDate))){
//            toDate = requireActivity().intent.getStringExtra(getString(R.string.dashboardGraphToDate)) ?: toDate
//        }

        if (requireActivity().intent.hasExtra(getString(R.string.dashboardGraphServiceFiter))) {
            val temp =
                requireActivity().intent.getStringExtra(getString(R.string.dashboardGraphServiceFiter))
                    ?: ""
            allottedServicesResponseModel =
                stringToJson(temp)
        }

        if (requireActivity().intent.hasExtra(getString(R.string.dashboard_service_filter_conf))) {
            val temp =
                requireActivity().intent.getStringExtra(getString(R.string.dashboard_service_filter_conf))
                    ?: ""
            dashboardServiceFilterConf = stringToJson(temp)
        }
        if (requireActivity().intent.hasExtra(getString(R.string.dashboard_total_overall_services))) {
            totalOverallServices = requireActivity().intent.getIntExtra(
                getString(R.string.dashboard_total_overall_services),
                0
            )
        }

        /*if(requireActivity().intent.hasExtra(getString(R.string.dashboardGraphResId))){
            resId = requireActivity().intent.getStringExtra(getString(R.string.dashboardGraphResId))
        }*/
    }

    private fun setOccupancyDayWiseListAdapter() {

        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvOccupancyDayWise.layoutManager = layoutManager
        occupancyAllTabsDetailsAdapter =
            EmptySeatTabsDetailsAdapter(requireActivity(), occupancyDayWiseList, true,privilegeResponse)
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

            setNormalBarChart(
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
        setNormalBarChart(
            requireContext(),
            binding.barChartCommon.barChart,
            occupancyReportList,
            true,
            isNegativeValue
        )
    }

    //    initAndSetupLineChart
    private fun initAndSetupLineChart() {

//        binding.lineChartCommon.lineChart.setVisibleXRange(1f,7f)

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
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {
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
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        allottedServicesResponseModel =
            PreferenceUtils.getObject<AllotedServicesResponseModel>("allotted_services_model_dashboard")

        totalOverallServices = PreferenceUtils.getPreference("total_overall_service_size", 0) ?: 0
        dashboardServiceFilterConf =
            PreferenceUtils.getObject<DashboardServiceFilterConf>(getString(R.string.dashboard_service_filter_conf))
                ?: DashboardServiceFilterConf()

        defaultSelection = PreferenceUtils.getPreference(
            DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION, 1
        )?.toInt() ?: 1
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = PreferenceUtils.getDashboardCurrentDate()
        from = fromDate
        to = toDate
        privilegeResponse = (activity as BaseActivity).getPrivilegeBase()

    }

/*
    private fun callRevenueDetailsApi() {

        if (requireActivity().isNetworkAvailable()) {

            startShimmerEffect()

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
                    serviceId = serviceId,
                    branchId = "-1",
                    from = from,
                    to = from,
                    sortBy = sortBy,
                    apiType = 2
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
                dashboardDetailsEmptySeatsLayout.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }
*/

    private fun callRevenueDetailsApi() {

        if (requireActivity().isNetworkAvailable()) {

            startShimmerEffect()

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
                from = from,
                to = to,
                sortBy = sortBy,
                serviceId = serviceId,
                branchId =  "-1",
                apiType = 2,
                is3DaysData = isLocalFilter.not(),
                locale = locale ?: "en",
                is_from_middle_tier = true,
                methodName = revenue_details
            )
        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                dashboardDetailsEmptySeatsLayout.gone()
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

                    if (occupancyDetailsResponse.result.unsoldSeatsLoss?.isEmpty()?.not() == true) {
                        occupancyDetailsResponse.result.unsoldSeatsLoss.forEachIndexed { i, element ->
//                        if (DASHBOARD_CHART_LIMIT > occupancyDayWiseList.size) { }
                            if (element.emptySeatsCount!! < 0) {
                                isNegativeValue = true
                            }
                            occupancyReportList.add(
                                ReportValue(
                                    element.unsoldSeats?.substring(
                                        SUBSTRING_START,
                                        min(SUBSTRING_END, element.unsoldSeats.length)
                                    ).plus(CUSTOM_ELLIPSIS),
                                    element.emptySeatsCount.toString(),
                                    getDashboardChartColor(
                                        requireContext(),
                                        element.emptySeatsCount.toDouble()?.toInt() ?: 0
                                    ),
                                    element.unsoldSeats,
                                    "${getString(R.string.empty_seats)} ${element.emptySeatsCount}"
                                )
                            )

                            occupancyDayWiseList.add(
                                OccupancyAllTabsDetailsModel(
                                    element.unsoldSeats,
                                    (privilegeResponse?.currency ?: "") + element.revenue, element.emptySeatsCount
                                )
                            )
                        }
                        stopShimmerEffect()
                        binding.unSoldSeatCardContainer.visible()
                        binding.noData.root.gone()
                    } else {
                        stopShimmerEffect()
                        binding.unSoldSeatCardContainer.gone()
                        binding.noData.root.visible()
                    }

                    setOccupancyDayWiseListAdapter()
                    setDefaultChart()

                } catch (e: Exception) {
                    if (it?.message != null) {
                        it.message.let { it1 -> requireContext().toast(it1) }
                    }
                    //Timber.d("Error in EmptySeatsFragment OccupancyDetails Observer ${e.message}")
                }
            } else {
                if (it?.message != null) {
                    stopShimmerEffect()
                    binding.unSoldSeatCardContainer.gone()
                    binding.noData.root.visible()
                    binding.noData.tvNoData.text = "${it.message}}"
                } else {
                    stopShimmerEffect()
                    binding.unSoldSeatCardContainer.gone()
                    binding.noData.root.visible()
                    binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
                }
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
                Timber.d("Error in EmptySeatsFragment CityDetails Observer ${e.message}")
            }
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

    private fun startShimmerEffect() {
        binding.apply {
            sortLayout.root.gone()
            shimmerDashboardDetails.visible()
            dashboardDetailsEmptySeatsLayout.gone()
            shimmerDashboardDetails.startShimmer()
            noData.root.gone()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            sortLayout.root.visible()
            shimmerDashboardDetails.gone()
            dashboardDetailsEmptySeatsLayout.visible()
            if (shimmerDashboardDetails.isShimmerStarted) {
                shimmerDashboardDetails.stopShimmer()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if (data != null) {
        if (resultCode == SELECT_SERVICE_INTENT_REQUEST_CODE) {
            val allottedServiceString = data?.getStringExtra("allotted_service_response")
            if (allottedServiceString != null) {

                val aa = stringToJson<AllotedServicesResponseModel>(allottedServiceString)

                allottedServicesResponseModel = aa
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
        callRevenueDetailsApi()

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

                callRevenueDetailsApi()
            }
        }
    }

}