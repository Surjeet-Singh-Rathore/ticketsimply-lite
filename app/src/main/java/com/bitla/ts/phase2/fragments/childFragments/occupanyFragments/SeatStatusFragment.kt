package com.bitla.ts.phase2.fragments.childFragments.occupanyFragments

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.occupancy_details
import com.bitla.ts.databinding.FragmentSeatStatusBinding
import com.bitla.ts.domain.pojo.DashboardServiceFilterConf
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.adapter.child.OccupancyAllTabsDetailsAdapter
import com.bitla.ts.phase2.chartUtils.ReportValue
import com.bitla.ts.phase2.dashboard_pojo.OccupancyAllTabsDetailsModel
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.request.OccupancyDetailsRequest
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.request.ReqBody
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.response.OccupancyBySeatStatus
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.response.OccupancyDetailsResponse
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
import com.bitla.ts.presentation.viewModel.CityDetailViewModel
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
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
import kotlin.math.roundToInt

class SeatStatusFragment : BaseFragment(), OnItemClickListener {

    companion object {
        val TAG = SeatStatusFragment::class.java.simpleName
    }

    private var privileges: PrivilegeResponseModel? = null
    private lateinit var binding: FragmentSeatStatusBinding

    private lateinit var occupancyAllTabsDetailsAdapter: OccupancyAllTabsDetailsAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var occupancyDayWiseList = arrayListOf<OccupancyAllTabsDetailsModel>()
    private var barChartSeatStatusList = ArrayList<ReportValue>()
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
    private lateinit var occupancyDetailsResponse: OccupancyDetailsResponse
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
    private var isSelectDate: Boolean = false
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var defaultSelection = 1
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
    private var allottedServicesResponseModel: AllotedServicesResponseModel? = null
    private var serviceId = "-1"
    private var totalOverallServices = 0
    private var selectedServiceName = "All Services"
    private var dashboardServiceFilterConf = DashboardServiceFilterConf()
    private var selectedChartPosition = 0
    private var serviceSize: Int? = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSeatStatusBinding.inflate(inflater, container, false)
        val view: View = binding.root

        PreferenceUtils.removeKey(SEAT_STATUS_OCCUPANCY_TO_DATE)
        PreferenceUtils.removeKey(SEAT_STATUS_OCCUPANCY_FROM_DATE)
        //PreferenceUtils.removeKey(SEAT_STATUS_OCCUPANCY_ORIGIN_CITY_NAME)
        PreferenceUtils.removeKey(SEAT_STATUS_OCCUPANCY_DESTINATION_CITY_NAME)
        setDateLocale(PreferenceUtils.getlang(),requireContext())

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
            barChartSeatStatusList.clear()
            occupancyDayWiseList.clear()
            legendValues.clear()
            entries.clear()
            colors.clear()
            legendEntry.clear()
            //callCityDetailsApi()
            callOccupancyDetailsApi()
        }

        binding.apply {

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

            sortLayout.btnSort.setOnClickListener {
                DialogUtils.dialogSortBy(
                    context = requireContext(),
                    sortBy = sortBy,
                    newLowToHighLabel = requireActivity().getString(R.string.occupancy_sort_ltoh),
                    newHighToLowLabel = requireActivity().getString(R.string.occupancy_sort_htol),
                    showOccupancyFilter = true,
                    showDateFilter = false,
                    showNetAmountFilter = false,

                    object : DialogSingleButtonListener {
                        override fun onSingleButtonClick(str: String) {
                            sortBy = str
                            callOccupancyDetailsApi()
                            binding.sortLayout.filterMarker.visible()

                            firebaseLogEvent(
                                requireContext(),
                                SORT_BTN,
                                loginModelPref.userName,
                                loginModelPref.travels_name,
                                loginModelPref.role,
                                SORT_BTN,
                                SortBtn.SEAT_STATUS_WISE_SORT
                            )

                        }
                    }
                )
            }

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
                            callOccupancyDetailsApi()

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
            callOccupancyDetailsApi()
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
                initAndSetupPieChart(
                    occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)
                        ?: OccupancyBySeatStatus("0", "0", "0", "0", "0")
                )
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

/*        if(requireActivity().intent.hasExtra(getString(R.string.dashboardGraphToDate))){
            toDate = requireActivity().intent.getStringExtra(getString(R.string.dashboardGraphToDate)) ?: toDate
        }*/

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

    private fun setOccupancyDayWiseListAdapter(sortBy: String) {

        occupancyDayWiseList.clear()

        occupancyDayWiseList.apply {
            add(
                OccupancyAllTabsDetailsModel(
                    getString(R.string.booked_seats),
                    "${occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)?.bookedSeats}%"
                )
            )
            add(
                OccupancyAllTabsDetailsModel(
                    getString(R.string.available_seats),
                    "${occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)?.availableSeats}%"
                )
            )
            add(
                OccupancyAllTabsDetailsModel(
                    getString(R.string.cancel_seats),
                    "${occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)?.cancelledSeats}%"
                )
            )
            add(
                OccupancyAllTabsDetailsModel(
                    getString(R.string.pending_seats),
                    "${occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)?.pendingSeats}%"
                )
            )
        }

//        occupancyDayWiseList.forEach {
//            occupancyDayWiseList.add(OccupancyAllTabsDetailsModel(it.title,"${it.value}%")
//        }

        if (sortBy == "htol") {
            occupancyDayWiseList.sortByDescending { it.value }
        } else if (sortBy == "ltoh") {
            occupancyDayWiseList.sortBy { it.value }
        }

        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvOccupancyDayWise.layoutManager = layoutManager
        occupancyAllTabsDetailsAdapter =
            OccupancyAllTabsDetailsAdapter(requireActivity(), privileges,occupancyDayWiseList)
        binding.rvOccupancyDayWise.adapter = occupancyAllTabsDetailsAdapter
    }

    //    initAndSetupPieChart
    private fun initAndSetupPieChart(occupancyBySeatStatus: OccupancyBySeatStatus) {

        binding.apply {
            barChartCommon.root.gone()
            pieChartCommon.root.visible()
            lineChartCommon.root.gone()
        }

        setPieInitAttributes(occupancyBySeatStatus)
    }

    private fun setPieInitAttributes(occupancyBySeatStatus: OccupancyBySeatStatus) {
        entries.apply {
            add(PieEntry(occupancyBySeatStatus.bookedSeats.replace(",", "").toFloat()))
            add(PieEntry(occupancyBySeatStatus.availableSeats.replace(",", "").toFloat()))
            add(PieEntry(occupancyBySeatStatus.cancelledSeats.replace(",", "").toFloat()))
            add(PieEntry(occupancyBySeatStatus.pendingSeats.replace(",", "").toFloat()))
        }

        legendValues.apply {
            add(
                PieEntry(
                    occupancyBySeatStatus.bookedSeats.replace(",", "").toFloat(),
                    requireContext().getString(R.string.booked_seats)
                )
            )
            add(
                PieEntry(
                    occupancyBySeatStatus.availableSeats.replace(",", "").toFloat(),
                    requireContext().getString(R.string.available_seats)
                )
            )
            add(
                PieEntry(
                    occupancyBySeatStatus.cancelledSeats.replace(",", "").toFloat(),
                    requireContext().getString(R.string.cancelled_tickets)
                )
            )
            add(
                PieEntry(
                    occupancyBySeatStatus.pendingSeats.replace(",", "").toFloat(),
                    requireContext().getString(R.string.pending_seats)
                )
            )
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.apply {
            sliceSpace = 1f
            selectionShift = 3f
            setAutomaticallyDisableSliceSpacing(true)
        }

        colors.apply {
            add(ContextCompat.getColor(requireContext(), R.color.booked_tickets))
            add(ContextCompat.getColor(requireContext(), R.color.pale_primary))
            add(ContextCompat.getColor(requireContext(), R.color.blocked_tickets))
            add(ContextCompat.getColor(requireContext(), R.color.orange))
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

            //barChartSeatStatusList=getDayWiseOccupancyBarChartReportList()
            setDecimalFormatBarChart(
                requireContext(),
                binding.barChartCommon.barChart,
                barChartSeatStatusList,
                false, true
            )

        } else {
            binding.apply {
                barChartCommon.root.visible()
                pieChartCommon.root.gone()
                lineChartCommon.root.gone()
                barChartCommon.barChart.animateY(1000)
            }
            setDecimalFormatBarChart(
                requireContext(),
                binding.barChartCommon.barChart,
                barChartSeatStatusList,
                false, true
            )

        }

        barChartCount++
        /*setDecimalFormatBarChart(
            requireContext(),
            binding.barChartCommon.barChart,
            barChartSeatStatusList,
            false,
            true
        )*/
    }

    //    initAndSetupLineChart
    private fun initAndSetupLineChart() {

        lineChartTitleList = barChartSeatStatusList

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
                false,
                4f,
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
                false,
                4f,
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
//        when (position) {
//            0 -> {
//                PreferenceUtils.setPreference(
//                    requireContext().getString(R.string.selectedChartId),
//                    position
//                )
//                initAndSetupBarChart()
//            }
//            1 -> {
//                PreferenceUtils.setPreference(
//                    requireContext().getString(R.string.selectedChartId),
//                    position
//                )
//                initAndSetupPieChart(
//                    occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)
//                        ?: OccupancyBySeatStatus("0", "0", "0", "0", "0")
//                )
//            }
//            2 -> {
//                PreferenceUtils.setPreference(
//                    requireContext().getString(R.string.selectedChartId),
//                    position
//                )
//                initAndSetupLineChart()
//            }
//        }
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        privileges = (activity as BaseActivity).getPrivilegeBase()


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
        toDate = fromDate
        from = fromDate
        to = fromDate

    }


    private fun callOccupancyDetailsApi() {
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

            dashboardViewModel.getOccupancyDetails(
                apiKey = loginModelPref.api_key,
                originId = -1,
                destinationId = -1,
                from = from,
                to = to,
                sortBy = sortBy,
                serviceId = serviceId,
                apiType = 4,
                is3DaysData = isLocalFilter.not(),
                locale = locale ?: "en",
                is_from_middle_tier = true,
                methodName = occupancy_details
            )

        } else {
            binding.apply {
                swipeRefreshLayoutDashboard.isRefreshing = false
                stopShimmerEffect()
                dashboardDetailsSeatStatusLayout.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun setUpObserver() {
        dashboardViewModel.occupancyDetailsResponseViewModel.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayoutDashboard.isRefreshing = false
            stopShimmerEffect()

            if (it.code == 200) {
                try {
                    barChartSeatStatusList.clear()
                    occupancyDayWiseList.clear()
                    occupancyDetailsResponse = it

                    binding.pieChartCommon.tvTotalSeatOccupancySeatStatusCount.text =
                        it.result.occupancyBySeatStatus?.get(0)?.totalSeats

                    if (occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)?.bookedSeats != "0"
                        && occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)?.availableSeats != "0"
                        && occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)?.pendingSeats != "0"
                        && occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)?.cancelledSeats != "0"
                    ) {

                        occupancyDetailsResponse.result.occupancyBySeatStatus?.forEach {
                            barChartSeatStatusList.add(
                                ReportValue(
                                    getString(R.string.booked_seats),
                                    it.bookedSeats,
                                    getDashboardChartColor(
                                        requireContext(),
                                        it.bookedSeats.toFloat().roundToInt()
                                    ),
                                    getString(R.string.booked_seats),
                                    it.bookedSeats.toFloat().toString()
                                )
                            )
                            barChartSeatStatusList.add(
                                ReportValue(
                                    getString(R.string.available_seats),
                                    it.availableSeats,
                                    getDashboardChartColor(
                                        requireContext(),
                                        it.availableSeats.toFloat().roundToInt()
                                    ),
                                    getString(R.string.available_seats),
                                    it.availableSeats.toFloat().toString()
                                )
                            )
                            barChartSeatStatusList.add(
                                ReportValue(
                                    getString(R.string.cancel_seats),
                                    it.cancelledSeats,
                                    getDashboardChartColor(
                                        requireContext(),
                                        it.cancelledSeats.toFloat().roundToInt()
                                    ),
                                    getString(R.string.cancel_seats),
                                    it.cancelledSeats.toFloat().toString()
                                )
                            )
                            barChartSeatStatusList.add(
                                ReportValue(
                                    getString(R.string.pending_seats),
                                    it.pendingSeats,
                                    getDashboardChartColor(
                                        requireContext(),
                                        it.pendingSeats.toFloat().roundToInt()
                                    ),
                                    getString(R.string.pending_seats),
                                    "${it.pendingSeats.toFloat()}",
                                )
                            )
                        }

                        if (sortBy == "htol") {
                            barChartSeatStatusList.sortByDescending { it.value }
                        } else if (sortBy == "ltoh") {
                            barChartSeatStatusList.sortBy { it.value }
                        }
                        setDefaultChart()
                        setOccupancyDayWiseListAdapter(sortBy)

                        binding.dashboardDetailsSeatStatusCard.visible()
                        binding.noData.root.gone()

                    } else {
                        binding.dashboardDetailsSeatStatusCard.gone()
                        binding.noData.root.visible()
                    }

                } catch (e: Exception) {
                    if (it?.message != null) {
                        it.message.let { it1 -> requireContext().toast(it1) }
                    }
                    Timber.d("Error in SeatStatusFragment OccupancyDetails Observer ${e.message}")
                }
            } else {
                if (it?.message != null) {
                    stopShimmerEffect()
                    binding.dashboardDetailsSeatStatusCard.gone()
                    binding.noData.root.visible()
                    binding.noData.tvNoData.text = "${it.message}}"
                } else {
                    stopShimmerEffect()
                    binding.dashboardDetailsSeatStatusCard.gone()
                    binding.noData.root.visible()
                    binding.noData.tvNoData.text = requireActivity().getString(R.string.opps)
                }
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
                initAndSetupPieChart(
                    occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)
                        ?: OccupancyBySeatStatus("0", "0", "0", "0", "0")
                )
            }
            2 -> {
                initAndSetupLineChart()
            }
            else -> {
                initAndSetupPieChart(
                    occupancyDetailsResponse.result.occupancyBySeatStatus?.get(0)
                        ?: OccupancyBySeatStatus("0", "0", "0", "0", "0")
                )
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
            dashboardDetailsSeatStatusLayout.gone()
            shimmerDashboardDetails.startShimmer()
            noData.root.gone()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            sortLayout.root.visible()
            shimmerDashboardDetails.gone()
            dashboardDetailsSeatStatusLayout.visible()
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
        callOccupancyDetailsApi()

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