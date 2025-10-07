package com.bitla.ts.presentation.view.activity

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctRequest.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.adapter.SortByAdaper.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible
import java.util.*


class SelectAllotedServiceActivity : BaseActivity(), OnItemClickListener,
    DialogSingleButtonListener, OnItemPassData {

    private var isGroupByHub: Boolean = false
    private var selectedHubId: Int ?= null
    private var destinationName: String? = ""
    private var destinationID: String? = ""
    private var originID: String? = ""
    private var originName: String? = ""
    private var travleDate: String? = ""
    private var convertedDate: String? = ""
    private var isFromReservationChart: Boolean = false
    private lateinit var binding: ActivitySelectServiceBinding

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val availableRoutesViewModel by viewModel<AvailableRoutesViewModel<Any?>>()

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var oldreservationId: Long? = 0L
    private var ymdDate: String = ""
    private var availableRoutesList =
        mutableListOf<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>()
    private var travelDate: String = ""
    private var newTravelDate: String = ""
    private var selectedApi: String = ""
    private var apiNumber: String = ""
    private var selectedRouteId: String = ""
    private var selectedReportType: String? = ""
    private var fromAtivity = PreferenceUtils.getPreference("fromTicketDetail", false)

    private lateinit var serviceAdapter: SelectAllotServiceAdapter
    private var tempList =
        arrayListOf<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>()
    private var locale: String? = ""
    lateinit var domain: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra(getString(R.string.report)))
            selectedReportType = intent.getStringExtra(getString(R.string.report))

//        setallotedDetailObserver()

        binding.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }


    override fun initUI() {
        binding = ActivitySelectServiceBinding.inflate(layoutInflater)

        isFromReservationChart = intent.getBooleanExtra("is_from_reservation_chart", false)

        destinationName = intent.getStringExtra("selected_destination_name")
        destinationID = intent.getStringExtra("selected_dest_id")
        originName = intent.getStringExtra("selected_source_name")
        originID = intent.getStringExtra("selected_origin_id")
        selectedHubId = intent.getIntExtra("selected_hub_id",0)
        isGroupByHub = intent.getBooleanExtra("is_group_by_hub",false)
        travleDate = PreferenceUtils.getString(PREF_TRAVEL_DATE)

        getPrefs()

        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        if (this.isNetworkAvailable()) {
            allotedObserver()
            allotedDirectService()
        } else
            this.noNetworkToast()

        binding.etSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempList.clear()
                val searchText = newText?.lowercase(Locale.getDefault())
                if (searchText?.isNotEmpty() == true && availableRoutesList.isNotEmpty()) {
                    for (i in 0..availableRoutesList.size.minus(1)) {
                        if (availableRoutesList[i].number.lowercase(Locale.getDefault())
                                .contains(searchText)
                        ) {
                            tempList.add(availableRoutesList[i])
                        }
                    }
                    if (binding.rvApi.adapter != null)
                        binding.rvApi.adapter?.notifyDataSetChanged()

                } else {
                    tempList.addAll(availableRoutesList)
                    binding.rvApi.adapter?.notifyDataSetChanged()
                }
                return false
            }
        })

        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
    }

    private fun adapter(availableRoutes: List<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>) {

        layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rvApi.layoutManager = layoutManager
        serviceAdapter =
            SelectAllotServiceAdapter(
                context = this,
                searchList = availableRoutes,
                onItemClickListener = this,
                onItemPassData = this,
                isFromReservation = isFromReservationChart
            )
        binding.rvApi.adapter = serviceAdapter

    }


    private fun getPrefs() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        domain = PreferenceUtils.getPreference(
            PREF_DOMAIN,
            getString(R.string.empty)
        )!!
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
//        PreferenceUtils.putString("TicketDetail_Traveldate", travelDate)
//        PreferenceUtils.putString("TicketDetail_originId", sourceId)
//        PreferenceUtils.putString("TicketDetail_destinationId", destinationId)

//        serviceSource = PreferenceUtils.getString("ViewReservation_OriginId")!!
//        serviceDestination = PreferenceUtils.getString("ViewReservation_DestinationId")!!
//        oldreservationId = PreferenceUtils.getString("reservationid")!!
        oldreservationId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)

        newTravelDate = PreferenceUtils.getString("shiftPassenger_selectedDate")!!

//        travelDate = PreferenceUtils.getString("TicketDetail_Traveldate")!!

        sourceId = PreferenceUtils.getString("SHIFT_originId")!!
        destinationId = PreferenceUtils.getString("SHIFT_destinationId")!!
    }

    private fun allotedDirectService() {
        if (isFromReservationChart) {
            var hub: Int ?= null
            if(selectedHubId != 0){
                hub = selectedHubId
            }else{
                hub = null
            }

            pickUpChartViewModel.allotedServiceApiDirect(

                AllotedDirectRequest(
                    is_group_by_hubs = isGroupByHub,
                    hub_id = hub,
                    api_key = loginModelPref.api_key,
                    travel_date = getDateYMD(getTodayDate()),
                    page = null,
                    per_page = null,
                    view_mode = "report",
                    pagination = false,
                    origin = originID,
                    destination = destinationID,
                    locale = locale,
                    isCheckingInspector = null,
                    serviceFilter = null
                ),
                lock_chart_method_name
            )
        } else {
            pickUpChartViewModel.allotedServiceApiDirect(

                AllotedDirectRequest(
                    is_group_by_hubs = false,
                    hub_id = null,
                    api_key = loginModelPref.api_key,
                    travel_date = getDateYMD(getTodayDate()),
                    page = null,
                    per_page = null,
                    view_mode = "report",
                    pagination = false,
                    origin = null,
                    destination = null,
                    locale = locale,
                    isCheckingInspector = null,
                    serviceFilter = null
                ),
                lock_chart_method_name
            )
        }
    }

    private fun allotedObserver() {
        pickUpChartViewModel.dataAllotedServiceDirect.observe(this) { it ->
            if (it != null) {
                when (it.code) {
                    200 -> {
                        availableRoutesList = it.services?.toMutableList() ?: mutableListOf()
                        if (availableRoutesList.isEmpty()) {
                            binding.progressBarList.gone()
                            binding.NoResult.visible()
                            binding.noResultText.text = getString(R.string.no_data_available)
                        } else {
                            PreferenceUtils.putObject(it, PREF_AVAILABLE_ROUTES_RESPONSE)
                            tempList.addAll(availableRoutesList)
                            if (selectedReportType == getString(R.string.tickets_booked_by_you) || selectedReportType == getString(
                                    R.string.branch_collection_report
                                ) || selectedReportType == getString(R.string.occupancy_report) || selectedReportType == getString(
                                    R.string.bus_service_collection
                                ) || selectedReportType == getString(R.string.cargo_booking_report)
                                || isFromReservationChart
                            ) {
                                val service =
                                    com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service()

                                if (isFromReservationChart) {
                                    service.allService = getString(R.string.all)
                                    service.number = getString(R.string.all)
                                } else {
                                    service.allService = getString(R.string.all_service)
                                    service.number = getString(R.string.all_service)
                                }
                                service.routeId = ""
                                tempList.add(0, service)

                            }
                            binding.progressBarList.gone()
                            adapter(tempList)
                        }
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this@SelectAllotedServiceActivity,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this@SelectAllotedServiceActivity
                        )*/
                        showUnauthorisedDialog()

                    }

                    else -> {
                        if (it.result?.message != null) {
                            binding.progressBarList.gone()
                            binding.NoResult.visible()
                            binding.noResultText.text = it.result.message.let { it ->
                                getString(R.string.no_data_available)
                            }
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }

        }
    }


    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (isFromReservationChart) {
            intent.putExtra("selected_service_name", tempList[position].number.toString())
            intent.putExtra("selected_service_id", tempList[position].routeId.toString())
            if (tempList[position].number.equals("all", true)) {
                intent.putExtra(
                    "selected_reservation_id", ""
                )
            } else {
                intent.putExtra(
                    "selected_reservation_id",
                    tempList[position].reservationId.toString()
                )
            }

            intent.putExtra("TravelSelection", "ServiceId")
            intent.putExtra("ApiNameSelected", selectedApi)
            intent.putExtra("selectedRouteId", selectedRouteId)

            setResult(RESULT_OK, intent)
            finish()
        } else {
            if (tempList.isEmpty()) {
                selectedApi = availableRoutesList[position].number.toString()
                selectedRouteId =
                    if (selectedReportType == getString(R.string.cargo_booking_report) && position != 0)
                        availableRoutesList[position].number.toString()
                    else
                        availableRoutesList[position].routeId.toString()
            } else {
                selectedApi = tempList[position].number.toString()
                selectedRouteId =
                    if (selectedReportType == getString(R.string.cargo_booking_report) && position != 0)
                        tempList[position].number.toString()
                    else
                        tempList[position].routeId.toString()
            }

            intent.putExtra("ApiNameSelected", selectedApi)
            intent.putExtra("selectedRouteId", selectedRouteId)

            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
        selectedApi = data
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }


    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onItemData(view: View, str1: String, str2: String) {
        apiNumber = str2
        selectedApi = str1
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
    }
}
