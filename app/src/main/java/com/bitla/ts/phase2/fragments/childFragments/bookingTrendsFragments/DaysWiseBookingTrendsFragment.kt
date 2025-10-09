package com.bitla.ts.phase2.fragments.childFragments.bookingTrendsFragments

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.data.listener.OnclickitemMultiView
import com.bitla.ts.databinding.FragmentETicketsBinding
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.alloted_services.request.AllotedServiceRequest
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.city_details.request.CityDetailRequest
import com.bitla.ts.domain.pojo.city_details.request.ReqBody
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.adapter.child.OccupancyAllTabsDetailsAdapter
import com.bitla.ts.phase2.chartUtils.ReportValue
import com.bitla.ts.phase2.dashboard_pojo.OccupancyAllTabsDetailsModel
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.request.BookingTrendsRequest
import com.bitla.ts.presentation.view.activity.DashboardOccupancyFilterActivity
import com.bitla.ts.presentation.view.activity.SearchServiceActivity
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
import com.google.gson.Gson
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

class DaysWiseBookingTrendsFragment : BaseFragment(), OnItemClickListener, DialogSingleButtonListener,
    OnItemPassData, SlyCalendarDialog.Callback {

    companion object {
        val TAG = DaysWiseBookingTrendsFragment::class.java.simpleName
    }

    private var privileges: PrivilegeResponseModel? = null
    private lateinit var binding: FragmentETicketsBinding

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
    private var date = ""
    private var serviceId = "-1"
    private var selectedServiceName = "All Services"
    private var isSelectDate: Boolean = true
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentETicketsBinding.inflate(inflater, container, false)
        val view: View = binding.root
        setDateLocale(PreferenceUtils.getlang(),requireContext())

        PreferenceUtils.removeKey(DAY_WISE_BOOKING_TRENDS_TO_DATE)
        PreferenceUtils.removeKey(DAY_WISE_BOOKING_TRENDS_FROM_DATE)
        //PreferenceUtils.removeKey(DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_NAME)
        PreferenceUtils.removeKey(DAY_WISE_BOOKING_TRENDS_DESTINATION_CITY_NAME)

        getPref()

        PreferenceUtils.putString(DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_NAME, selectedServiceName)
        //PreferenceUtils.putString(DAY_WISE_BOOKING_TRENDS_FROM_DATE, fromDate)

        //callCityDetailsApi()
        //callRevenueDetailsApi()
        callAllottedServiceApi()
        setAllottedDetailObserver()
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
            callBookingTrendsApi()
        }

        binding.apply {

            btnShare.setOnClickListener {
                binding.btnChangeChart.gone()
                binding.btnShare.gone()
                shareView(requireActivity(), getDefaultChart())
                binding.btnChangeChart.visible()
                binding.btnShare.visible()
            }

            btnChangeChart.setOnClickListener {
                DialogUtils.dialogChartPopup(
                    requireContext(),
                    this@DaysWiseBookingTrendsFragment
                )
                legendValues.clear()
                entries.clear()
                colors.clear()
                legendEntry.clear()
            }

            /*btnSort.setOnClickListener {
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
            }*/
        }

        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
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

        return view
    }

    override fun isInternetOnCallApisAndInitUI() {
        if (isAttachedToActivity()) {
            callBookingTrendsApi()
            callCityDetailsApi()
        }
    }

    override fun isNetworkOff() {
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    private fun setOccupancyDayWiseListAdapter() {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvOccupancyDayWise.layoutManager = layoutManager
        occupancyAllTabsDetailsAdapter =
            OccupancyAllTabsDetailsAdapter(requireActivity(), privileges,occupancyDayWiseList)
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

            //occupancyReportList = getDayWiseOccupancyBarChartReportList()
            //setDecimalFormatBarChart(binding.barChartCommon.barChart, occupancyReportList)
            setDecimalFormatBarChart(
                requireContext(),
                binding.barChartCommon.barChart,
                occupancyReportList
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
            occupancyReportList
        )
    }

    //    initAndSetupLineChart
    private fun initAndSetupLineChart() {

        //lineChartTitleList = getDayWiseOccupancyBarChartReportList()
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
                false
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
                false
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
        privileges = (activity as BaseActivity).getPrivilegeBase()

        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = PreferenceUtils.getDashboardCurrentDate()
        from = fromDate
        to = toDate
    }

    private fun callBookingTrendsApi() {
        startShimmerEffect()
        if (requireActivity().isNetworkAvailable()) {
            val reqBody =
                com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.request.ReqBody(
                    apiKey = loginModelPref.api_key,
                    originId = originIdFinal,
                    destinationId = destinationIdFinal,
                    serviceId = serviceId,
                    branchId = "-1",
                    from = fromDate,
                    to = fromDate,
                    sortBy = sortBy,
                    apiType = 7
                )

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
                dashboardDetailsDayWiseTrendsLayout.gone()
                noData.root.visible()
                noData.tvNoData.text = requireActivity().getString(R.string.no_network_msg)
            }
        }
    }

    private fun setUpObserver() {
        dashboardViewModel.bookingTrendsResponseViewModel.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayoutDashboard.isRefreshing = false
            stopShimmerEffect()

            if (it.code == 200) {
                try {
                    Timber.d(Gson().toJson(it))
                    occupancyReportList.clear()
                    occupancyDayWiseList.clear()
                    val bestPerformanceDay = it
                    if (bestPerformanceDay.result.bestPerformanceDays?.isNotEmpty() == true) {
                        bestPerformanceDay.result.bestPerformanceDays.forEachIndexed { i, element ->
//                        if (DASHBOARD_CHART_LIMIT > occupancyDayWiseList.size) { }
                            occupancyReportList.add(
                                ReportValue(
                                    element.service.substring(
                                        SUBSTRING_START, min(SUBSTRING_END, element.service.length)
                                    ).plus(CUSTOM_ELLIPSIS),
                                    element.performance.toDouble().roundToInt().toString(),
                                    getDashboardChartColor(
                                        requireContext(),
                                        element.performance.toFloat().roundToInt()
                                    ),
                                    element.service,
                                    "${element.performance.toFloat()}%"
                                )
                            )
                            occupancyDayWiseList.add(
                                OccupancyAllTabsDetailsModel(
                                    element.service,
                                    element.performance.plus("%")
                                )
                            )
                        }

                        binding.dashboardDetailsDayWiseTrendsLayout.visible()
                        binding.dashboardDetailsDayWiseTrendsCard.visible()
                        binding.noData.root.gone()

                        setDefaultChart()
                        setOccupancyDayWiseListAdapter()
                    } else {
                        binding.dashboardDetailsDayWiseTrendsLayout.visible()
                        binding.dashboardDetailsDayWiseTrendsCard.gone()
                        binding.noData.root.visible()
                    }

                    setDefaultChart()
                    setOccupancyDayWiseListAdapter()

                } catch (e: Exception) {
                    if (it?.message != null) {
                        it.message.let { it1 -> requireContext().toast(it1) }
                    }
                    Timber.d("Error in DayWiseBookingTrendsFragment OccupancyDetails Observer ${e.message}")
                }
            } else {
                if (it?.message != null) {
                    stopShimmerEffect()
                    binding.noData.root.visible()
                    binding.dashboardDetailsDayWiseTrendsLayout.visible()
                    binding.dashboardDetailsDayWiseTrendsCard.gone()
                    binding.noData.tvNoData.text = "${it.message}}"
                } else {
                    stopShimmerEffect()
                    binding.noData.root.visible()
                    binding.dashboardDetailsDayWiseTrendsLayout.visible()
                    binding.dashboardDetailsDayWiseTrendsCard.gone()
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
/*
                binding.btnFilter.setOnClickListener {
                    if (PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_ID) == "") {
                        cityIdOrigin = -1
                    } else {
                        cityIdOrigin =
                            PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_ID)
                                ?.toInt() ?: -1
                    }
                    if (PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_DESTINATION_CITY_ID) == "") {
                        cityIdDestination = -1
                    } else {
                        cityIdDestination =
                            PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_DESTINATION_CITY_ID)
                                ?.toInt() ?: -1
                    }
                    DialogUtils.dialogFilterByNew(
                        requireContext(),
                        originCityList,
                        PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_NAME) ?: "",
                        PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_DESTINATION_CITY_NAME)
                            ?: "",
                        PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_FROM_DATE) ?: "",
                        PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_TO_DATE) ?: "",
                        isSelectDate,
                        isBranchFilter = false,
                        object : OnclickitemMultiView {
                            override fun onClickMuliView(
                                view: View,
                                view2: View,
                                view3: View,
                                view4: View,
                                resID: String,
                                remarks: String
                            ) {
                                autoCompleteOrigin = view as AutoCompleteTextView
                                autoCompleteDestination = view2 as AutoCompleteTextView

                                if (remarks == "origin") {
                                    currentSelection = remarks
                                    val intent: Intent = Intent(
                                        requireContext(),
                                        DashboardOccupancyFilterActivity::class.java
                                    )
                                    intent.putExtra("currentSelection", currentSelection)
                                    startActivityForResult(intent, RESULT_CODE_SOURCE)
                                } else if (remarks == "destination") {
                                    if (cityIdOrigin != -1) {
                                        currentSelection = remarks
                                        val intent: Intent = Intent(
                                            requireContext(),
                                            DashboardOccupancyFilterActivity::class.java
                                        )
                                        intent.putExtra("currentSelection", currentSelection)
                                        intent.putExtra("currentSelectedId", cityIdOrigin)
                                        startActivityForResult(intent, RESULT_CODE_SOURCE)
                                    } else {
                                        requireContext().toast("Please Select Origin First")
                                    }
                                } else if (remarks == "selectServices") {
                                    currentSelection = remarks
                                    val intent: Intent = Intent(
                                        requireContext(),
                                        SearchServiceActivity::class.java
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


                            }



                }, this, this
                )
            }
*/
            } catch (e: Exception) {
                requireActivity().toast(getString(R.string.opps))
                Timber.d("Error in DayWiseBookingTrendsFragment CityDetails Observer ${e.message}")
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if (data != null) {
        if (requestCode == RESULT_CODE_SOURCE) {
            if (currentSelection == "origin") {
                if (data?.getStringExtra(getString(R.string.SELECTED_CITY_NAME)) != null) {
                    originCityName =
                        data.getStringExtra(getString(R.string.SELECTED_CITY_NAME)) ?: ""
                }
                if (data?.getIntExtra(
                        getString(R.string.SELECTED_CITY_ID),
                        -1
                    ) != null && data.getIntExtra(getString(R.string.SELECTED_CITY_ID), -1) != -1
                ) {
                    cityIdOrigin =
                        data.getIntExtra(getString(R.string.SELECTED_CITY_ID), -1) ?: -1
                }
                autoCompleteOrigin.setText(originCityName)
            } else if (currentSelection == "destination") {
                if (data?.getStringExtra(getString(R.string.SELECTED_CITY_NAME)) != null) {
                    destinationCityName =
                        data.getStringExtra(getString(R.string.SELECTED_CITY_NAME)) ?: ""
                }
                if (data?.getIntExtra(
                        getString(R.string.SELECTED_CITY_ID),
                        -1
                    ) != null && data.getIntExtra(getString(R.string.SELECTED_CITY_ID), -1) != -1
                ) {
                    cityIdDestination =
                        data.getIntExtra(getString(R.string.SELECTED_CITY_ID), -1) ?: -1
                }
                autoCompleteDestination.setText(destinationCityName)
            }
        } else if (resultCode == SELECT_SERVICE_INTENT_REQUEST_CODE) {

            val allotedServicesResponseModel =
                PreferenceUtils.getObject<AllotedServicesResponseModel>(
                    ALLOTTED_SERVICES_MODEL_DAY_WISE_BOOKING_TRENDS
                )
            var totalSelectedServices = 0
            allotedServicesResponseModel?.services?.forEach {
                if (it.isChecked)
                    totalSelectedServices++
            }

            if (totalSelectedServices > 0 && totalSelectedServices < (allotedServicesResponseModel?.services?.size
                    ?: 0)
            ) {

                serviceId = ""
                selectedServiceName = ""

                allotedServicesResponseModel?.services?.forEach {
                    if (it.isChecked) {
                        serviceId += it.routeId.toString().replace(".0", "") + ","
                        selectedServiceName += it.number.toString() + ","
                    }
                }

/*                PreferenceUtils.setPreference(
                    PREF_SELECTED_SERVICE_ID_FILTER_PENDING_QUOTA,
                    serviceId.substring(0, serviceId.lastIndexOf(","))
                )*/

            } else {

                serviceId = "-1"
                selectedServiceName = "All Services"

                /*PreferenceUtils.setPreference(
                    PREF_SELECTED_SERVICE_ID_FILTER_PENDING_QUOTA, "-1"
                )*/
            }

            if (serviceId == "-1") {

                selectedServiceName = "All Services"
            } else {

                selectedServiceName =
                    selectedServiceName.substring(0, selectedServiceName.lastIndexOf(","))

                serviceId =
                    serviceId.substring(0, serviceId.lastIndexOf(","))


            }
            autoCompleteOrigin.setText(selectedServiceName)
            originCityName = selectedServiceName

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

    private fun callAllottedServiceApi() {
        if (requireActivity().isNetworkAvailable()) {
            val allotedServiceRequest = AllotedServiceRequest(
                bccId.toString(),
                alloted_Service_method_name,
                format_type,
                com.bitla.ts.domain.pojo.alloted_services.request.ReqBody(
                    loginModelPref.api_key,
                    getDateYMD(getTodayDate()), "", "",
                    is_group_by_hubs = false,
                    is_from_middle_tier = true,
                    locale = locale,
                    view_mode = "report"
                )
            )

            pickUpChartViewModel.allotedServiceAPI(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                allotedServiceRequest,
                alloted_Service_method_name
            )

        }
    }

    private fun setAllottedDetailObserver() {
        pickUpChartViewModel.allotedDetailResponse.observe(viewLifecycleOwner) { it ->
            if (it?.services != null) {

                serviceId = "-1"
                selectedServiceName = "All Services"

                PreferenceUtils.putString(
                    DAY_WISE_BOOKING_TRENDS_SERVICE_ID,
                    serviceId
                )
                PreferenceUtils.putString(
                    DAY_WISE_BOOKING_TRENDS_SERVICE_NAME,
                    selectedServiceName
                )
                PreferenceUtils.putObject(it, ALLOTTED_SERVICES_MODEL_DAY_WISE_BOOKING_TRENDS)
                callBookingTrendsApi()
                binding.btnFilter.setOnClickListener {
                    if (PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_ID) == "") {
                        cityIdOrigin = -1
                    } else {
                        cityIdOrigin =
                            PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_ID)
                                ?.toInt() ?: -1
                    }
                    if (PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_DESTINATION_CITY_ID) == "") {
                        cityIdDestination = -1
                    } else {
                        cityIdDestination =
                            PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_DESTINATION_CITY_ID)
                                ?.toInt() ?: -1
                    }
                    DialogUtils.dialogFilterByNew(
                        requireContext(),
                        originCityList,
                        PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_NAME) ?: "",
                        PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_DESTINATION_CITY_NAME)
                            ?: "",
                        PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_FROM_DATE) ?: "",
                        PreferenceUtils.getString(DAY_WISE_BOOKING_TRENDS_TO_DATE) ?: "",
                        isSelectDate,
                        isBranchFilter = false,
                        object : OnclickitemMultiView {
                            override fun onClickMuliView(
                                view: View,
                                view2: View,
                                view3: View,
                                view4: View,
                                resID: String,
                                remarks: String
                            ) {
                                autoCompleteOrigin = view as AutoCompleteTextView
                                autoCompleteDestination = view2 as AutoCompleteTextView

                                if (remarks == "origin") {
                                    currentSelection = remarks
                                    val intent: Intent = Intent(
                                        requireContext(),
                                        DashboardOccupancyFilterActivity::class.java
                                    )
                                    intent.putExtra("currentSelection", currentSelection)
                                    startActivityForResult(intent, RESULT_CODE_SOURCE)
                                } else if (remarks == "destination") {
                                    if (cityIdOrigin != -1) {
                                        currentSelection = remarks
                                        val intent: Intent = Intent(
                                            requireContext(),
                                            DashboardOccupancyFilterActivity::class.java
                                        )
                                        intent.putExtra("currentSelection", currentSelection)
                                        intent.putExtra("currentSelectedId", cityIdOrigin)
                                        startActivityForResult(intent, RESULT_CODE_SOURCE)
                                    } else {
                                        requireContext().toast("Please Select Origin First")
                                    }
                                } else if (remarks == "selectServices") {
                                    currentSelection = remarks
                                    val intent: Intent = Intent(
                                        requireContext(),
                                        SearchServiceActivity::class.java
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


                            }

                            override fun onClickAdditionalData(view0: View, view1: View) {

                            }


                        }, this, this
                    )
                }

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
//        else requireContext().noNetworkToast()


    override fun onSingleButtonClick(str: String) {
        if (str == "cancel") {
            //cityIdOrigin = -1
        } else if (str == "filter") {
            originIdFinal = cityIdOrigin
            destinationIdFinal = cityIdDestination
            from = fromDate
            to = toDate
            PreferenceUtils.putString(DAY_WISE_BOOKING_TRENDS_FROM_DATE, from)
            PreferenceUtils.putString(DAY_WISE_BOOKING_TRENDS_TO_DATE, to)
            //PreferenceUtils.putString(DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_NAME, originCityName)
            PreferenceUtils.putString(DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_NAME, selectedServiceName)
            PreferenceUtils.putString(
                DAY_WISE_BOOKING_TRENDS_DESTINATION_CITY_NAME,
                destinationCityName
            )
            PreferenceUtils.putString(
                DAY_WISE_BOOKING_TRENDS_ORIGIN_CITY_ID,
                originIdFinal.toString()
            )
            PreferenceUtils.putString(
                DAY_WISE_BOOKING_TRENDS_DESTINATION_CITY_ID,
                destinationIdFinal.toString()
            )
            PreferenceUtils.putString(
                E_BOOKING_TRENDS_SERVICE_ID,
                serviceId
            )
            PreferenceUtils.putString(
                E_BOOKING_TRENDS_SERVICE_NAME,
                selectedServiceName
            )
            occupancyReportList.clear()
            occupancyDayWiseList.clear()
            legendValues.clear()
            entries.clear()
            colors.clear()
            legendEntry.clear()
            callBookingTrendsApi()
        }
    }


    override fun onItemData(view: View, str1: String, str2: String) {
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

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
        etToDate = view as TextInputEditText
        fromDate = str1
        if (fromDate != "") {
            isFromDate = false
            fromDate = convertDateYYYYMMDDtoDDMMYY(etFromDate.text.toString())

            SlyCalendarDialog()
                .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                .setEndDate(stringToDate(toDate, DATE_FORMAT_Y_M_D))
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
            shimmerDashboardDetails.visible()
            dashboardDetailsDayWiseTrendsLayout.gone()
            shimmerDashboardDetails.startShimmer()
            noData.root.gone()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            shimmerDashboardDetails.gone()
            dashboardDetailsDayWiseTrendsLayout.visible()
            if (shimmerDashboardDetails.isShimmerStarted) {
                shimmerDashboardDetails.stopShimmer()
            }
        }
    }
}