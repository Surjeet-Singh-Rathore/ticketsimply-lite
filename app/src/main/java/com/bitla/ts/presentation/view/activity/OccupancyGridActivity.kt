package com.bitla.ts.presentation.view.activity

import android.content.*
import android.os.Build
import androidx.activity.result.contract.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.activate_deactivate_service.request.*
import com.bitla.ts.domain.pojo.active_inactive_services.request.*
import com.bitla.ts.domain.pojo.active_inactive_services.response.*
import com.bitla.ts.domain.pojo.alloted_services.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.occupancy_datewise.request.*
import com.bitla.ts.domain.pojo.occupancy_datewise.response.*
import com.bitla.ts.domain.pojo.occupancy_datewise.response.Occupancy
import com.bitla.ts.domain.pojo.occupancy_datewise.response.Service
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.view.activity.reservationOption.extendedFare.*
import com.bitla.ts.presentation.view.merge_bus.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.scrollable_grid_with_fixed_headers.*
import com.bitla.ts.utils.scrollable_grid_with_fixed_headers.syncedRecycler.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.bottomsheet.*
import gone
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible

class OccupancyGridActivity : BaseActivity() {

    private lateinit var binding: ActivityOccupancyGridBinding

    private val occupancyGridViewModel by viewModel<OccupancyGridViewModel<Any?>>()
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var loginModelPref: LoginModel = LoginModel()

    private lateinit var occupancyGridDateAdapter: OccupancyGridDateAdapter
    private lateinit var occupancyGridServiceAdapter: OccupancyGridServiceAdapter
    private lateinit var occupancyGridCountAdapter: OccupancyGridCountAdapter

    private var defaultSelection = 1
    private var fromDate = ""
    private var isCustomDateFilterSelected = false
    private var routeId: String = ""
    private var activeInactiveServicesResponse: ActiveInactiveServicesResponse? = null
    private var allottedServicesResponseModel: AllotedServicesResponseModel? = null
    private var totalOverallServices = 0
    private var isFilterActiveInactiveServicesFromAllottedServiceList = true
    private var resetFilter = false

    override fun initUI() {
        binding = ActivityOccupancyGridBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()
        initAdapters()
        lifecycleScope.launch {
            occupancyGridViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        //callOccupancyDateWiseApi()
        callActiveInactiveServiceApi()

        setObserver()

        binding.layoutToolbar.titleTV.text = getString(R.string.occupancy)

        binding.layoutToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        /*binding.allServicesAndDateFilterContainer.tvAllService.text =
            getString(R.string.active_services)*/
        binding.allServicesAndDateFilterContainer.tvAllService.setOnClickListener {
            if (activeInactiveServicesResponse != null) {

                val intent = Intent(this, SearchActiveInactiveServiceActivity::class.java)

                intent.putExtra(getString(R.string.from_date), fromDate)

                intent.putExtra(
                    "active_inactive_services_response",
                    jsonToString(activeInactiveServicesResponse!!)
                )

                intent.putExtra(
                    "filter_active_inactive_services_from_allotted_service_list",
                    isFilterActiveInactiveServicesFromAllottedServiceList
                )

                intent.putExtra(
                    getString(R.string.dashboardGraphServiceFiter),
                    jsonToString(allottedServicesResponseModel!!)
                )
                intent.putExtra(
                    getString(R.string.dashboard_total_overall_services),
                    totalOverallServices
                )

                resultLauncher.launch(intent)
            }
        }

        binding.allServicesAndDateFilterContainer.tvDate.setOnClickListener {
            DialogUtils.dialogDateFilter(
                context = this,
                defaultSelection = defaultSelection,
                todayDate = getDateYMD(getTodayDate()),
                fromDate = fromDate,
                toDate = getDateYMD(getTodayDate()),
                isBeforeFromDateSelection = true,
                isAfterToDateSelection = true,
                isAfterFromDateSelection = true,
                hideYesterdayDateFilter = false,
                hideTodayDateFilter = false,
                hideTomorrowDateFilter = false,
                hideLast7DaysDateFilter = true,
                hideLast30DaysDateFilter = true,
                hideCustomDateFilter = false,
                hideCustomDateRangeFilter = true,
                isCustomDateFilterSelected = isCustomDateFilterSelected,
                isCustomDateRangeFilterSelected = false,
                fragmentManager = supportFragmentManager,
                tag = "",
                onApply = { finalFromDate, finalToDate, lastSelectedItem, isCustomDateFilter, isCustomDateRangeFilter ->

                    if (finalFromDate != null) {
                        fromDate = finalFromDate
                        defaultSelection = lastSelectedItem

                        dashboardDateSetText(
                            textView = binding.allServicesAndDateFilterContainer.tvDate,
                            fromDate = fromDate,
                            toDate = null,
                            inputDateFormat = DATE_FORMAT_Y_M_D
                        )

                        isCustomDateFilterSelected = isCustomDateFilter

                        routeId = ""
                        callActiveInactiveServiceApi()
                        resetFilter = true

                    }
                }
            )
        }
    }

    private fun initAdapters() {
        binding.rvDate.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        occupancyGridDateAdapter = OccupancyGridDateAdapter {
            showServiceSummaryBottomSheet(it)

        }
        binding.rvDate.adapter = occupancyGridDateAdapter

        occupancyGridServiceAdapter = OccupancyGridServiceAdapter {
            showServiceDetailBottomSheet(it)
        }
        binding.rvService.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvService.adapter = occupancyGridServiceAdapter



        binding.rvServiceCount.layoutManager = FixedGridLayoutManager().apply {
            totalColumnCount = 8
        }
        occupancyGridCountAdapter = OccupancyGridCountAdapter(this, privilegeResponseModel) {
            if(privilegeResponseModel?.occupancyForecastReport != true) {
                showServiceOptionsBottomSheet(it)
            }
        }
        binding.rvServiceCount.adapter = occupancyGridCountAdapter


        binding.rvServiceCount.bindTo(
            binding.rvDate, SyncedRecyclerView.ALIGN_ORIENTATION_HORIZONTAL
        )
        binding.rvDate.bindTo(
            binding.rvServiceCount, SyncedRecyclerView.ALIGN_ORIENTATION_HORIZONTAL
        )
        binding.rvServiceCount.bindTo(
            binding.rvService, SyncedRecyclerView.ALIGN_ORIENTATION_VERTICAL
        )
        binding.rvService.bindTo(
            binding.rvServiceCount, SyncedRecyclerView.ALIGN_ORIENTATION_VERTICAL
        )

        binding.rvService.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //Toast.makeText(this@OccupancyGridActivity, "test", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPref() {

        privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel

        loginModelPref = PreferenceUtils.getLogin()

        allottedServicesResponseModel =
            PreferenceUtils.getObject<AllotedServicesResponseModel>("allotted_services_model_dashboard")

        totalOverallServices = PreferenceUtils.getPreference("total_overall_service_size", 0) ?: 0

        defaultSelection = PreferenceUtils.getPreference(
            DASHBOARD_GRAPH_DATE_FILTER_DEFAULT_SELECTION, 1
        )?.toInt() ?: 1

        if (defaultSelection == 5) {
            isCustomDateFilterSelected = true
        }

        fromDate = PreferenceUtils.getDashboardCurrentDate()


    }

    /*override fun onResume() {
        super.onResume()
        callOccupancyDateWiseApi()
    }*/

    override fun isInternetOnCallApisAndInitUI() {

    }

    private fun callOccupancyDateWiseApi() {

        dashboardDateSetText(
            textView = binding.allServicesAndDateFilterContainer.tvDate,
            fromDate = fromDate,
            toDate = null,
            inputDateFormat = DATE_FORMAT_Y_M_D
        )

        val occupancyDateWiseRequest = OccupancyDateWiseRequest(
            loginModelPref.api_key, fromDate, routeId
        )

        occupancyGridViewModel.occupancyDateWiseApi(
            occupancyDateWiseRequest,
            occupancy_datewise
        )

        showLoader()
    }

    private fun callActiveInactiveServiceApi() {

        val activeInactiveServicesRequest = ActiveInactiveServicesRequest(
            apiKey = loginModelPref.api_key,
            from = fromDate,
            to = fromDate
        )

        occupancyGridViewModel.activeInactiveServicesApi(
            activeInactiveServicesRequest,
            active_inactive_services
        )

        showLoader()
    }

    private fun setObserver() {
        occupancyGridViewModel.occupancyDateWiseResponse.observe(this) {

            hideLoader()
            if (it != null) {

                when (it.code) {
                    200 -> {
                        val occupancyCountList = mutableListOf<Occupancy>()
                        occupancyGridDateAdapter.updateList(
                            it.dateWiseSummary?.toMutableList() ?: mutableListOf()
                        )



                        it.serviceList?.forEach { serviceItem ->

                            serviceItem?.occupancy?.forEach { occupancyItem ->
                                if (occupancyItem != null) {
                                    occupancyItem.serviceId = serviceItem.id
                                    occupancyItem.serviceName = serviceItem.name
                                    occupancyItem.coachType = serviceItem.coachType
                                    occupancyItem.busType = serviceItem.busType

                                    occupancyItem.originId = serviceItem.originId
                                    occupancyItem.origin = serviceItem.origin

                                    occupancyItem.destinationId = serviceItem.destinationId
                                    occupancyItem.destination = serviceItem.destination
                                    occupancyItem.deptTime = serviceItem.deptTime

                                    occupancyCountList.add(occupancyItem)
                                }
                            }
                        }

                        occupancyGridCountAdapter.updateList(occupancyCountList)

                        occupancyGridServiceAdapter.updateList(
                            it.serviceList?.toMutableList() ?: mutableListOf()
                        )
                    }

                    401 -> {
                       // openUnauthorisedDialog()
                        showUnauthorisedDialog()

                    }
                }

            } else {
                toast(getString(R.string.server_error))
            }

        }

        occupancyGridViewModel.activeInactiveServices.observe(this) {
            hideLoader()
            if (it != null) {
                when (it.code) {
                    200 -> {

                        activeInactiveServicesResponse = it

                        val tempActiveServiceList =
                            mutableListOf<com.bitla.ts.domain.pojo.active_inactive_services.response.Service?>()
                        val tempInactiveServiceList =
                            mutableListOf<com.bitla.ts.domain.pojo.active_inactive_services.response.Service?>()

                        if (isFilterActiveInactiveServicesFromAllottedServiceList) {


                            isFilterActiveInactiveServicesFromAllottedServiceList = false

                            allottedServicesResponseModel?.services?.forEach {
                                activeInactiveServicesResponse?.activeService?.forEach { item ->
                                    if (it.routeId.toString().equals(item?.routeId, false)) {
                                        item?.isChecked = it.isChecked
                                        tempActiveServiceList.add(item)
                                    }
                                }

                                activeInactiveServicesResponse?.inactiveService?.forEach { item ->
                                    if (it.routeId.toString().equals(item?.routeId, false)) {
                                        item?.isChecked = it.isChecked
                                        tempInactiveServiceList.add(item)
                                    }
                                }
                            }

                            activeInactiveServicesResponse?.activeService = tempActiveServiceList
                            activeInactiveServicesResponse?.inactiveService =
                                tempInactiveServiceList

                            setAllServices()

                        } else {
                            /*allottedServicesResponseModel?.services?.forEach {
                                activeInactiveServicesResponse?.activeService?.forEach { item ->
                                    if (it.routeId.toString().equals(item?.routeId, false)) {
                                        item?.isChecked = true
                                        tempActiveServiceList.add(item)
                                    }
                                }

                                activeInactiveServicesResponse?.inactiveService?.forEach { item ->
                                    if (it.routeId.toString().equals(item?.routeId, false)) {
                                        item?.isChecked = true
                                        tempInactiveServiceList.add(item)
                                    }
                                }
                            }*/

                            activeInactiveServicesResponse?.activeService?.forEach { item ->
                                item?.isChecked = true
                                tempActiveServiceList.add(item)
                            }

                            activeInactiveServicesResponse?.inactiveService?.forEach { item ->
                                item?.isChecked = true
                                tempInactiveServiceList.add(item)
                            }

                            activeInactiveServicesResponse?.activeService = tempActiveServiceList
                            activeInactiveServicesResponse?.inactiveService =
                                tempInactiveServiceList

                            binding.allServicesAndDateFilterContainer.tvAllService.text =
                                "${getString(R.string.all_services_title_case)} (${(it.activeService?.size ?: 0) + (it.inactiveService?.size ?: 0)})"

                            callOccupancyDateWiseApi()
                        }

                    }

                    401 -> {
                       // openUnauthorisedDialog()
                        showUnauthorisedDialog()

                    }

                    else -> {
                        toast(it.message)
                        activeInactiveServicesResponse = null
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        occupancyGridViewModel.activateDeactivateService.observe(this) {
            hideLoader()
            if (it != null) {

                when (it.code) {
                    200 -> {

                        toast(it.message)
                        callActiveInactiveServiceApi()
                    }

                    401 -> {
                        //openUnauthorisedDialog()
                        showUnauthorisedDialog()

                    }

                    else -> {
                        toast(it.message)
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }



    private fun showServiceSummaryBottomSheet(item: DateWiseSummary?) {

        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val bottomSheetOccupancyServiceSummaryBinding =
            BottomSheetOccupancyServiceSummaryBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetOccupancyServiceSummaryBinding.root)

        if (item?.date?.isNotEmpty() == true) {
            val date = getDateMMMDD(item.date)
            bottomSheetOccupancyServiceSummaryBinding.tvDate.text = date
        } else {
            bottomSheetOccupancyServiceSummaryBinding.tvDate.text = ""
        }

        bottomSheetOccupancyServiceSummaryBinding.tvTotalSeats.text = "${item?.totalSeats ?: 0}"
        bottomSheetOccupancyServiceSummaryBinding.tvActiveServices.text =
            "${item?.activeServices ?: 0}"
        bottomSheetOccupancyServiceSummaryBinding.tvTotalSeatsAvailableSold.text =
            "${item?.bookedSeats ?: 0}/${item?.availableSeats ?: 0}"
        bottomSheetOccupancyServiceSummaryBinding.tvTotalFare.text =
            "${privilegeResponseModel?.currency ?: ""}${
                item?.totalFare?.convert(privilegeResponseModel?.currencyFormat) ?: 0
            }"
        bottomSheetOccupancyServiceSummaryBinding.tvInactiveServices.text =
            "${item?.inactiveServices ?: 0}"

        bottomSheetDialog.show()

        bottomSheetOccupancyServiceSummaryBinding.btnOk.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    private fun showServiceOptionsBottomSheet(item: Occupancy?) {

        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val bottomSheetOccupancyServiceOptionsBinding =
            BottomSheetOccupancyServiceOptionsBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetOccupancyServiceOptionsBinding.root)

        val date = if (item?.date?.isNotEmpty() == true) {
            getDateMMMDD(item.date)
        } else {
            ""
        }

        val title = "${item?.serviceName} - $date"
        bottomSheetOccupancyServiceOptionsBinding.tvTitle.text = title
        val subtitle = "${item?.coachType ?: ""} | ${item?.busType ?: ""}"
        bottomSheetOccupancyServiceOptionsBinding.tvSubtitle.text = subtitle

        bottomSheetOccupancyServiceOptionsBinding.serviceSwitch.setOnClickListener {

            callActivateDeactivateServicesApi(
                item?.reservationId,
                bottomSheetOccupancyServiceOptionsBinding.serviceSwitch.isChecked
            )

            bottomSheetDialog.dismiss()

        }

        bottomSheetOccupancyServiceOptionsBinding.serviceSwitch.isChecked =
            item?.isInactiveService?.not() ?: true


        bottomSheetOccupancyServiceOptionsBinding.layoutChangeFare.setOnClickListener {
            try {
                val busDetails =
                    "${item?.serviceName ?: ""} | ${
                        convertDateFormat(
                            item?.date ?: "",
                            DATE_FORMAT_Y_M_D,
                            DATE_FORMAT_D_M_YY
                        )
                    } ${item?.origin ?: ""} - ${item?.destination ?: ""} ${item?.busType ?: ""} "
                val intent = Intent(this, UpdateRateCardActivity::class.java)
                intent.putExtra(
                    getString(R.string.origin),
                    item?.origin
                )
                intent.putExtra(
                    getString(R.string.destination),
                    item?.destination
                )
                intent.putExtra(getString(R.string.bus_type), busDetails)

                PreferenceUtils.putString(
                    getString(R.string.updateRateCard_resId),
                    "${item?.reservationId}"
                )
                PreferenceUtils.putString(
                    getString(R.string.updateRateCard_origin),
                    item?.origin
                )
                PreferenceUtils.putString(
                    getString(R.string.updateRateCard_destination),
                    item?.destination
                )
                PreferenceUtils.putString(
                    getString(R.string.updateRateCard_originId),
                    item?.originId ?: ""
                )
                PreferenceUtils.putString(
                    getString(R.string.updateRateCard_destinationId),
                    item?.destinationId.toString()
                )
                PreferenceUtils.putString(
                    getString(R.string.updateRateCard_busType),
                    busDetails
                )

                startActivity(intent)

                if (item?.date?.contains("-") == true) {
                    val date = item.date.split("-")
                    val finalDate = "${date?.get(2)}-${date?.get(1)}-${date?.get(0)}"
                    PreferenceUtils.putString(
                        getString(R.string.updateRateCard_travelDate),
                        finalDate
                    )
                } else {
                    val date = item?.date?.split("/")
                    val finalDate = "${date?.get(2)}-${date?.get(1)}-${date?.get(0)}"
                    PreferenceUtils.putString(
                        getString(R.string.updateRateCard_travelDate),
                        finalDate
                    )
                }

            } catch (e: Exception) {
                toast(e.message.toString())
            }
        }

        bottomSheetOccupancyServiceOptionsBinding.layoutAssignCrewToService.setOnClickListener {
            val busDetails =
                "${item?.serviceId} | ${getDateDMYY(item?.date ?: "")} ${item?.deptTime ?: ""} | ${item?.busType ?: ""}"
            val intent = Intent(this, ServiceDetailsActivity::class.java)
            intent.putExtra(getString(R.string.origin), item?.origin)
            intent.putExtra(getString(R.string.destination), item?.destination)
            intent.putExtra(getString(R.string.bus_type), busDetails)
            PreferenceUtils.setPreference(
                PREF_RESERVATION_ID, item?.reservationId
            )
            PreferenceUtils.removeKey(getString(R.string.scannedUserName))
            PreferenceUtils.removeKey(getString(R.string.scannedUserId))
            PreferenceUtils.removeKey("selectedScanType")
            PreferenceUtils.removeKey(getString(R.string.scan_coach))
            PreferenceUtils.removeKey(getString(R.string.scan_driver_1))
            PreferenceUtils.removeKey(getString(R.string.scan_driver_2))
            PreferenceUtils.removeKey(getString(R.string.scan_cleaner))
            PreferenceUtils.removeKey(getString(R.string.scan_contractor))

            startActivity(intent)
        }


        bottomSheetOccupancyServiceOptionsBinding.layoutManifest.setOnClickListener {
            openViewReservationActivity(item, 0)
        }

        bottomSheetOccupancyServiceOptionsBinding.layoutShiftPassenger.setOnClickListener {

            var intent=Intent(this, MergeBusShiftActivity::class.java)
            val busDetails =
                "${item?.serviceName}\n${item?.deptTime ?: ""} | ${item?.origin ?: ""} - ${item?.destination ?: ""}"
            intent.putExtra(getString(R.string.origin), item?.origin)
            intent.putExtra(getString(R.string.destination), item?.destination)
            intent.putExtra(getString(R.string.res_id),item?.reservationId.toString())
            intent.putExtra(getString(R.string.toolbarheader),busDetails)
            intent.putExtra(getString(R.string.source_id),item?.originId)
            intent.putExtra(getString(R.string.destination_id),item?.destinationId)
            intent.putExtra(getString(R.string.date),item?.date)
            PreferenceUtils.setPreference(PREF_SOURCE_ID,item?.originId)
            //startActivity(intent)
            resultLauncherMergeBusDetailsActivity.launch(intent)


//            openViewReservationActivity(item, 2)
        }

        if (item?.isInactiveService == true) {

            bottomSheetOccupancyServiceOptionsBinding.viewManifest.visible()
            bottomSheetOccupancyServiceOptionsBinding.layoutManifest.visible()

            bottomSheetOccupancyServiceOptionsBinding.viewChangeFare.gone()
            bottomSheetOccupancyServiceOptionsBinding.layoutChangeFare.gone()

            bottomSheetOccupancyServiceOptionsBinding.viewAssignCrewToService.gone()
            bottomSheetOccupancyServiceOptionsBinding.layoutAssignCrewToService.gone()

            bottomSheetOccupancyServiceOptionsBinding.viewShiftPassenger.gone()
            bottomSheetOccupancyServiceOptionsBinding.layoutShiftPassenger.gone()
        } else {
            if (privilegeResponseModel?.mergeBusOnTsApp == true) {
                bottomSheetOccupancyServiceOptionsBinding.viewShiftPassenger.visible()
                bottomSheetOccupancyServiceOptionsBinding.layoutShiftPassenger.visible()
            } else {
                bottomSheetOccupancyServiceOptionsBinding.viewShiftPassenger.gone()
                bottomSheetOccupancyServiceOptionsBinding.layoutShiftPassenger.gone()
            }

            if (privilegeResponseModel?.country.equals("Indonesia", true)) {
                bottomSheetOccupancyServiceOptionsBinding.viewManifest.visible()
                bottomSheetOccupancyServiceOptionsBinding.layoutManifest.visible()
            } else {
                bottomSheetOccupancyServiceOptionsBinding.viewManifest.gone()
                bottomSheetOccupancyServiceOptionsBinding.layoutManifest.gone()
            }

            if (privilegeResponseModel?.allowUpdateDetailsOptionInReservationChart == true) {
                bottomSheetOccupancyServiceOptionsBinding.viewAssignCrewToService.visible()
                bottomSheetOccupancyServiceOptionsBinding.layoutAssignCrewToService.visible()
            } else {
                bottomSheetOccupancyServiceOptionsBinding.viewAssignCrewToService.gone()
                bottomSheetOccupancyServiceOptionsBinding.layoutAssignCrewToService.gone()
            }

            if (privilegeResponseModel?.isEditReservation == true) {
                bottomSheetOccupancyServiceOptionsBinding.viewChangeFare.visible()
                bottomSheetOccupancyServiceOptionsBinding.layoutChangeFare.visible()
            } else {
                bottomSheetOccupancyServiceOptionsBinding.viewChangeFare.gone()
                bottomSheetOccupancyServiceOptionsBinding.layoutChangeFare.gone()
            }
        }

        bottomSheetDialog.show()

        bottomSheetOccupancyServiceOptionsBinding.btnOk.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    private fun showServiceDetailBottomSheet(item: Service?) {

        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val bottomSheetOccupancyServiceDetailsBinding =
            BottomSheetOccupancyServiceDetailsBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetOccupancyServiceDetailsBinding.root)

        bottomSheetOccupancyServiceDetailsBinding.tvName.text = item?.name ?: ""
        bottomSheetOccupancyServiceDetailsBinding.tvCoachType.text = item?.coachType ?: ""
        bottomSheetOccupancyServiceDetailsBinding.tvTotalSeats.text = "${item?.totalSeats ?: 0}"
        bottomSheetOccupancyServiceDetailsBinding.tvDepTime.text = item?.deptTime ?: ""

        bottomSheetDialog.show()

        bottomSheetOccupancyServiceDetailsBinding.btnOk.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    private fun callActivateDeactivateServicesApi(serviceId: Long?, activate: Boolean?) {
        val serviceItem: com.bitla.ts.domain.pojo.activate_deactivate_service.request.Service =
            com.bitla.ts.domain.pojo.activate_deactivate_service.request.Service(
                serviceId = serviceId ?: 0L,
                activate = activate ?: false
            )
        val serviceList =
            mutableListOf<com.bitla.ts.domain.pojo.activate_deactivate_service.request.Service?>()
        serviceList.add(serviceItem)
        val activateDeactivateServiceRequest = ActivateDeactivateServiceRequest(
            serviceList = serviceList
        )
        occupancyGridViewModel.activateDeactivateServiceApi(
            apikey = loginModelPref.api_key,
            activateDeactivateServiceRequest = activateDeactivateServiceRequest,
            apiType = activate_deactivate_service
        )

        showLoader()
    }

    private fun showLoader() {
        binding.progressBar.visible()
    }

    private fun hideLoader() {
        binding.progressBar.gone()
    }

    private fun openViewReservationActivity(item: Occupancy?, tabPosition: Int) {
        try {

            PreferenceUtils.putString(
                "reservationid",
                "${item?.reservationId}"
            )
            PreferenceUtils.putString(
                "ViewReservation_OriginId",
                item?.originId ?: ""
            )
            PreferenceUtils.putString(
                "ViewReservation_DestinationId",
                item?.destinationId ?: ""
            )
            PreferenceUtils.putString(
                "ViewReservation_data",
                "${item?.serviceName ?: ""} | ${getDateDMY(item?.date ?: "")} | ${item?.origin} - ${item?.destination} | ${item?.busType}"
            )
            PreferenceUtils.putString(
                "ViewReservation_date",
                "${item?.date ?: ""} "
            )
            PreferenceUtils.setPreference(
                PREF_RESERVATION_ID, item?.reservationId
            )
            PreferenceUtils.putString(
                "ViewReservation_name",
                "${item?.origin ?: ""} - ${item?.destination}"
            )
            PreferenceUtils.putString(
                "ViewReservation_number",
                item?.serviceName ?: ""
            )
            PreferenceUtils.putString(
                "ViewReservation_seats",
                item?.serviceName ?: ""
            )


            val ymdDate = item?.date ?: ""

            PreferenceUtils.putString("ViewReservation_date", ymdDate)


            PreferenceUtils.putString(
                "ViewReservation_data",
                "${item?.serviceName} | ${getDateDMY(ymdDate)} | ${item?.origin} - ${item?.destination} | ${item?.busType ?: ""}"
            )

            PreferenceUtils.setPreference("BUlK_shifting", false)
            PreferenceUtils.putString("BulkShiftBack", "")
            //PreferenceUtils.setPreference("shiftPassenger_tab", 0)
            PreferenceUtils.setPreference(
                "seatwiseFare",
                "fromBulkShiftPassenger"
            )
            PreferenceUtils.setPreference("shiftPassenger_tab", tabPosition)
            if (item?.occupiedSeats != null && item.occupiedSeats > 0)
                PreferenceUtils.setPreference("dataAvailable", true)
            else
                PreferenceUtils.setPreference("dataAvailable", false)
            val intent = Intent(this, ViewReservationActivity::class.java)
            intent.putExtra("pickUpResid", item?.reservationId)

            startActivity(intent)

        } catch (e: Exception) {

        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == SELECT_SERVICE_INTENT_REQUEST_CODE) {

                val data: String? = result.data?.getStringExtra("active_inactive_services_response")
                val serviceFilterType = result.data?.getStringExtra("service_filter_type")

                if (data?.isNotEmpty() == true) {
                    val arrayList: ArrayList<String?> = arrayListOf()
                    activeInactiveServicesResponse = stringToJson(data)
                    activeInactiveServicesResponse?.activeService?.forEach {
                        if (it?.isChecked == true) {
                            arrayList.add(it.routeId)
                        }
                    }

                    activeInactiveServicesResponse?.inactiveService?.forEach {
                        if (it?.isChecked == true) {
                            arrayList.add(it.routeId)
                        }
                    }

                    if (arrayList.isNotEmpty() == true) {
                        routeId = arrayList.joinToString {
                            it ?: ""
                        }
                    } else {
                        routeId = ""
                    }

                    var totalSelectedServices = 0
                    var selectedServiceName = ""
                    var totalActiveInactiveServices =
                        activeInactiveServicesResponse?.activeService?.size?.plus(
                            activeInactiveServicesResponse?.inactiveService?.size ?: 0
                        )

                    activeInactiveServicesResponse?.activeService?.forEach {
                        if (it?.isChecked == true)
                            totalSelectedServices++
                    }

                    activeInactiveServicesResponse?.inactiveService?.forEach {
                        if (it?.isChecked == true)
                            totalSelectedServices++
                    }

                    /*if (totalSelectedServices == totalActiveInactiveServices) {
                        routeId = ""
                        selectedServiceName = "${getString(R.string.all_services_title_case)} (${totalActiveInactiveServices})"
                    } else {
                        selectedServiceName = "${getString(R.string.service)} ($totalSelectedServices)"
                    }*/
                    if (serviceFilterType == ACTIVE_SERVICE) {
                        selectedServiceName =
                            "${getString(R.string.active_services)} ($totalSelectedServices)"
                    } else if (serviceFilterType == INACTIVE_SERVICE) {
                        selectedServiceName =
                            "${getString(R.string.inactive_services)} ($totalSelectedServices)"
                    }
                    binding.allServicesAndDateFilterContainer.tvAllService.text =
                        selectedServiceName
                    callOccupancyDateWiseApi()
                }
            }
        }

    private var resultLauncherMergeBusDetailsActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESTART_OCCUPANCY_GRID_ACTIVITY_REQUEST_CODE) {
                finish()
                startActivity(intent)
            }
        }

    private fun setAllServices() {

        var totalSelectedServices = 0
        var selectedServiceName = ""

        allottedServicesResponseModel?.services?.forEach {
            if (it.isChecked)
                totalSelectedServices++
        }

        if (totalSelectedServices > 0 && totalSelectedServices < totalOverallServices) {
            routeId = ""
            selectedServiceName = ""
            allottedServicesResponseModel?.services?.forEach {
                if (it.isChecked) {
                    this@OccupancyGridActivity.routeId += it.routeId.toString()
                        .replace(".0", "") + ","
                    selectedServiceName += it.number.toString() + ","
                }
            }
        } else {
            this@OccupancyGridActivity.routeId = ""
            selectedServiceName =
                "${getString(R.string.all_services_title_case)} ($totalOverallServices)"
        }
        if (this@OccupancyGridActivity.routeId == "" && totalSelectedServices == totalOverallServices) {
            selectedServiceName =
                "${getString(R.string.all_services_title_case)} (${totalOverallServices})"
        } else {
            selectedServiceName = "${getString(R.string.service)} ($totalSelectedServices)"
            if (this@OccupancyGridActivity.routeId.lastIndexOf(",")!=-1){
                this@OccupancyGridActivity.routeId = this@OccupancyGridActivity.routeId.substring(
                    0,
                    this@OccupancyGridActivity.routeId.lastIndexOf(",")
                )
            }
        }
        binding.allServicesAndDateFilterContainer.tvAllService.text = selectedServiceName
        callOccupancyDateWiseApi()
    }



}