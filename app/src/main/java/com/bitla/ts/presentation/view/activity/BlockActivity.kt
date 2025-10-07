package com.bitla.ts.presentation.view.activity

import android.widget.RadioButton as RadioButton1
import android.annotation.*
import android.app.*
import android.content.*
import android.content.res.*
import android.graphics.*
import android.os.*
import android.text.method.*
import android.view.*
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.block_configuration_model.request.*
import com.bitla.ts.domain.pojo.block_configuration_model.request.ReqBody
import com.bitla.ts.domain.pojo.block_seats.request.*
import com.bitla.ts.domain.pojo.block_seats.request.Record
import com.bitla.ts.domain.pojo.block_seats.request.SearchbusParams
import com.bitla.ts.domain.pojo.block_seats.request.Ticket
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.branch_list_model.request.*
import com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.passenger_details_result.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.released_summary.ReleaseTicket
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.service_summary.*
import com.bitla.ts.domain.pojo.ticket_details.request.*
import com.bitla.ts.domain.pojo.ticket_details.response.*
import com.bitla.ts.domain.pojo.unblock_seat.request.*
import com.bitla.ts.domain.pojo.user_list.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.fragments.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getBccId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLogin
import com.bitla.tscalender.*
import com.google.android.material.bottomsheet.*
import com.google.android.material.tabs.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*


class BlockActivity : BaseActivity(), AdapterView.OnItemSelectedListener, OnItemClickListener,
    DialogSingleButtonListener, OnItemCheckListener,
    SlyCalendarDialog.Callback, OnSeatSelectionListener, DialogButtonListener {

    companion object {
        val TAG = BlockActivity::class.java.simpleName
    }

//    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var isProceeded: Boolean = false
    private var timeHH: String = ""
    private var timeMM: String = ""
    private var branchId: String? = null
    private var userId: String? = null
    private var agentId: String? = null
    var mainOpId: String? = null //hard coded
    private var userTypeId: Int = 0
    private var userTypeValue: String = ""
    private var blockTypeId: Int? = null
    private var blockTypeValue: String? = null
    private var fromDate: String? = null
    private var toDate: String? = null

    private var userTypeString: String = ""
    private var userTypeList: MutableList<SpinnerItems> = mutableListOf()
    private var blockTypeList: MutableList<SpinnerItems> = mutableListOf()
    private var userList: MutableList<SpinnerItems> = mutableListOf()
    private var branchList: MutableList<SpinnerItems> = mutableListOf()
    private var selectedUserTypeList: MutableList<SpinnerItems> = mutableListOf()

    // private lateinit var weekdaysAdapter: WeekdaysAdapter
    private var weekdays: ArrayList<Weekdays> = arrayListOf()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var apiKey: String = ""
    private var bccId: String = ""
    private var resId: Long? = 0L
    private var loginModelPref = LoginModel()

    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var dateType: String? = null
    private var selectedSeats: String? = null
    private var travelDate: String = ""
    private var serviceNumber: String = ""
    private var busType: String? = null
    private var deptTime: String? = null
    private val coachfunction = PreferenceUtils.getString("SelectionCoach")
    val coachChoice = PreferenceUtils.getPreference("COACH_VIEW_SELECTION", "SingleViewSelected")

    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private var legendDetails = arrayListOf<LegendDetail>()


    private lateinit var commonCoachsingle: AllCoachFragment

    private var selectedSeatDetails = ArrayList<SeatDetail>()
    private var isAllSeatSelected: Boolean = false
    private var finalSeatNumbers = arrayListOf<String?>()
    private var totalSum: Double = 0.0
    private var constresId: String = "80487"
    private var constOriginId: String = "18"
    private var constDestinationId: String = "19"
    private var constFormat = "true"
    private var isClick: Int = 0
    private var tvMultipleUserType: Int = 0
    private var isAllowMultipleQuota: Boolean = true
    private lateinit var seatLegendsAdapter: SeatLegendsAdapter
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()

    private lateinit var binding: ActivityBlockBinding

    private var multiStationList = mutableListOf<Multistation>()
    private var boardingList = mutableListOf<BoardingFrom>()
    private var dropOffList = mutableListOf<DropOff>()
    private var collectionBookingList =
        mutableListOf<com.bitla.ts.domain.pojo.collection_summary.Booking>()
    private var releasedTicketsList = mutableListOf<ReleaseTicket>()
    private var agentList = mutableListOf<Agent>()


    private var isblocked = 0
    private var serviceDateTimeBusType = ""
    private var proceedClicked = false
    private var isCanBlockSeats: Boolean = false
    private var titleBlocked = ""


    private var isReleaseTicket: String = ""

    private var boardingTravelDate: String? = ""
    private var boardingDepTime: String? = ""
    private var dropOffTravelDate: String? = ""
    private var dropOffDepTime: String? = ""
    private var bAddress: String? = ""
    private var dAddress: String? = ""

    private var pnr: String? = ""
    private var boardingStageID: String? = ""
    private var droppingStageID: String? = ""
    private var boarding: String? = ""
    private var dropping: String? = ""
    private var toolbarTitle: String = ""
    private var isShiftPassenger: Boolean = false
    private var isCanCancelTicket: Boolean = false
    private var passengerContactDetailList: ArrayList<ContactDetail> =
        ArrayList()
    private val seatList = ArrayList<SeatDetail>()
    private lateinit var _sheetReleaseTicketsBinding: SheetReleaseTicketsBinding
    private val currentCheckedItem: MutableList<PassengerDetail?> = ArrayList()
    private val selectedSeatNumber = StringBuilder()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()

    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()
    private var passengerDetailList: MutableList<PassengerDetail?>? = null
    private var releaseTicketNumber: String = ""

    lateinit var bindingSheet: SheetReleaseTicketsBinding
    lateinit var bottomSheetDialoge: BottomSheetDialog
    private var locale: String? = ""

    private var selectedBoarding: BoardingPointDetail? = null
    private var selectedDropping: DropOffDetail? = null
    private var isApplyBPDPFare: String? = "false"
    private var isFromChile: Boolean = false
    private var shouldPhoneBlockingRelease = false
    private var pinSize = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDateLocale(PreferenceUtils.getlang(), this@BlockActivity)

        getPref()

        callBlockConfigApi()

        titleBlocked = intent.getStringExtra(getString(R.string.blocked)).toString()
        if (!titleBlocked.isNullOrEmpty()) {
            binding.tvBlocked.gone()
            binding.includeToolbar.tvCurrentHeader.visible()
            binding.includeToolbar.tvCurrentHeader.setTextColor(resources.getColor(R.color.color_user_red))
            binding.includeToolbar.tvCurrentHeader.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.color_user_red))
            binding.includeToolbar.tvCurrentHeader.text = getString(R.string.service_blocked)
            binding.includeToolbar.toolbarHeaderText.setTextColor(resources.getColor(R.color.colorDimShadow6))
            binding.includeToolbar.toolbarSubtitle.setTextColor(resources.getColor(R.color.colorShadow))
        }
        binding.includeToolbar.toolbarHeaderText.text = "$source-$destination"
        val subtitle = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${getDateDMYY(travelDate)} $deptTime | $busType"
        else
            "${getDateDMYY(travelDate)} $deptTime | $busType"
        binding.includeToolbar.toolbarSubtitle.text = subtitle
        serviceDateTimeBusType = subtitle
        binding.coachProgressBar.visible()
        if (isFromChile) {
            binding.layoutRemarks.visible()
        } else {
            binding.layoutRemarks.gone()
        }

        if (isApplyBPDPFare == "true") {
            callBpDpServiceApi(selectedBoarding?.id.toString(), selectedDropping?.id.toString())
        } else {
            callServiceApi()
        }

        setUpObserver()

        blockRadioGroup()

        setWeekdays()
        releaseTicketFun()
    }

    override fun isInternetOnCallApisAndInitUI() {
        if (!isProceeded) {
            userList.clear()
            userTypeList.clear()
            blockTypeList.clear()
            branchList.clear()
            selectedUserTypeList.clear()
            selectedSeatDetails.clear()

            if (isApplyBPDPFare == "true") {
                callBpDpServiceApi(selectedBoarding?.id.toString(), selectedDropping?.id.toString())
            } else {
                callServiceApi()
            }
            callBlockConfigApi()
        }

    }

    private fun getPref() {
//        privilegeResponseModel = getPrivilegeBase()

        lifecycleScope.launch {
            val privilege = getPrivilegeBaseSafely()
            blockViewModel.updatePrivileges(privilege)
        }

        loginModelPref = getLogin()
        bccId = getBccId().toString()
        apiKey = loginModelPref.api_key
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()
        locale = PreferenceUtils.getlang()
        isApplyBPDPFare = PreferenceUtils.getObject<String>(IS_APPLY_BP_DP_FARE).toString()

//        Timber.d("selectedBPDP: isApplyBPDPFare $isApplyBPDPFare")
//        Timber.d("selectedBPDP: isBPDP ${PreferenceUtils.getObject<String>(IS_APPLY_BP_DP_FARE)}")
//        Timber.d("selectedBPDP: boarding${PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL)}")
//        Timber.d("selectedBPDP: dropping${PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL)}")

        if (isApplyBPDPFare == "true") {
            selectedBoarding = PreferenceUtils.getObject(SELECTED_BOARDING_DETAIL)
            selectedDropping = PreferenceUtils.getObject(SELECTED_DROPPING_DETAIL)
        }

        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null) {
            resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!
        }

        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            deptTime = result?.dep_time ?: getString(R.string.dash)
            busType = result?.bus_type ?: getString(R.string.empty)
            serviceNumber = result?.number ?: getString(R.string.empty)
        }

        blockViewModel.privilegesLiveData.observe(this) { privilegeResponseModel ->

            if (privilegeResponseModel != null) {
                if (privilegeResponseModel?.isChileApp != null) {
                    isFromChile = privilegeResponseModel?.isChileApp == true
                }

                isCanBlockSeats = if (privilegeResponseModel?.isCanBlockSeats == null) {
                    false
                } else {
                    privilegeResponseModel?.isCanBlockSeats == true
                }

                isAllowMultipleQuota = if (privilegeResponseModel?.allowMultipleQuota == null) {
                    false
                } else {
                    privilegeResponseModel?.allowMultipleQuota == true
                }
                shouldPhoneBlockingRelease = privilegeResponseModel?.pinBasedActionPrivileges?.phoneBlockingRelease ?: false
                pinSize = privilegeResponseModel?.pinCount ?: 6
            }
            else {
                toast(getString(R.string.server_error))
            }
        }

    }

    private fun setUpObserver() {

        blockViewModel.loadingState.observe(this, Observer {
            when (it) {
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> {
                    it.msg?.let { it1 -> toast(it1) }
                    binding.includeProgress.progressBar.gone()
                }
            }

        })

        sharedViewModel.serviceDetails.observe(this) {
            binding.includeProgress.progressBar.gone()

            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {

                            binding.coachProgressBar.gone()
                            mainOpId = it.body.routeId.toString()
                            legendDetails = it.body.legendDetails!!

                            initVariables()
                            hiddenPropertiesAll()
                            onNoSeatSelection()
                            setSeatLegendsAdapter()
                                binding.coachProgressBar.gone()
                                binding.layoutCoachSingle.visible()
                                commonCoachsingle =
                                    supportFragmentManager.findFragmentById(R.id.layout_coach_single) as AllCoachFragment
                                commonCoachsingle.setCoachData(it.body)
                                //commonCoachsingle.binding.selectallseats.gone()
                            onExpand(false)

                        }

                        401 -> {
                           /* DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            showUnauthorisedDialog()
                        }

                        else -> it.message?.let { it1 -> toast(it1) }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                toast("An error occurred")
            }
        }

        blockViewModel.blockDetails.observe(this, Observer { it ->
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                if (!it.user_types.isNullOrEmpty()) {
                    userTypeList.clear()
                    it.user_types.forEach {

                        if (it.label == "USER") {
                            userTypeId = it.id
                        }
                        val spinnerItems = SpinnerItems(it.id, it.label)
                        userTypeList.add(spinnerItems)
                        userTypeSpinner()
                    }
                }

                if (it.blocking_types.isNotEmpty()) {
                    blockTypeList.clear()
                    it.blocking_types.forEach {
                        val spinnerBlockItems = SpinnerItems(0, it)
                        blockTypeList.add(spinnerBlockItems)
                    }
                    blockTypeSpinner()
                }

            } else {
                toast(getString(R.string.server_error))
            }
        })

        blockViewModel.userList.observe(this) { it ->
            binding.includeProgress.progressBar.gone()
            try {
                userList.clear()
                if (it != null) {
                    if (it.active_users != null && it.active_users.isNotEmpty()) {
                        it.active_users.forEach {
                            val spinnerItems = SpinnerItems(it.id, it.label)
                            userList.add(spinnerItems)
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }

                if (userTypeId == 1 || userTypeValue.equals("Onl-Agt", true)) {
                    stageUserSpinner()
                    if (!isAllowMultipleQuota && userList.isNotEmpty()) {
                        userList.sortBy { it.value.lowercase() }
                        agentId = userList[0].id.toString()
                        binding.tvAgentTypeSpinner.setText(userList[0].value)
                        setButtonObservable()
                    }
                } else {
                    userListSpinner()
                    if (!isAllowMultipleQuota && userList.isNotEmpty()) {
                        userList.sortBy { it.value.lowercase() }
                        userId = userList[0].id.toString()
                        binding.tvUserSpinner.setText(userList[0].value)
                        setButtonObservable()
                    }
                }
            } catch (t: Throwable) {

            }
        }

        blockViewModel.branchList.observe(this) { it ->
            binding.includeProgress.progressBar.gone()
            branchList.clear()
            try {
                if (it != null) {
                    if (it.branchlists.isNotEmpty()) {
                        it.branchlists.forEach {
                            val spinnerItems = SpinnerItems(it.id, it.label)
                            branchList.add(spinnerItems)
                        }
                    }
                    branchListSpinner()
                    if (!isAllowMultipleQuota && branchList.isNotEmpty()) {
                        branchList.sortBy { it.value.lowercase() }
                        branchId = branchList[0].id.toString()
                        binding.tvBranchTypeSpinner.setText(branchList[0].value)
                        if (userTypeValue.equals("USER", true) || userTypeId == 12)
                            callUserListApi()
                        setButtonObservable()
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                toast("An error occurred while fetching Branch List")
            }
        }

        blockViewModel.validationData.observe(this) {
            if (it == getString(R.string.empty)) {
                binding.btnBlockSeats.setBackgroundResource(R.drawable.button_selected_bg)
                DialogUtils.blockSeatsDialog(
                    showMsg = true,
                    context = this,
                    title = getString(R.string.confirmSeats),
                    message = updateBlockContentMessage(),
                    srcDest = "$source-$destination",
                    journeyDate = serviceDateTimeBusType,
                    noOfSeats = selectedSeatDetails.size.toString(),
                    seatNo = selectedSeats ?: getString(R.string.notAvailable),
                    buttonLeftText = getString(R.string.goBack),
                    buttonRightText = getString(R.string.block),
                    dialogButtonListener = this
                )
            } else {
                toast(it)
            }
        }

        blockViewModel.changeButtonBackground.observe(this) {
            if (it) {
                binding.btnBlockSeats.apply {
                    setBackgroundResource(R.drawable.button_selected_bg)
                    isEnabled = true
                }
            } else {
                if (!isAllowMultipleQuota) {
                    binding.btnBlockSeats.setBackgroundResource(R.drawable.button_selected_bg)
                    binding.btnBlockSeats.isEnabled = true
                } else {
                    binding.btnBlockSeats.apply {
                        setBackgroundResource(R.drawable.button_default_bg)
                        isEnabled = false
                    }
                }
            }
        }

        blockViewModel.blockSeats.observe(this) {
            binding.includeProgress.progressBar.gone()
            try {
                if (it != null) {
                    if (it.code == 200) {
                        DialogUtils.successfulBlockSeatDialog(
                            this,
                            getString(R.string.seat_block_successful)
                        )

                        callBlockConfigApi()

                            commonCoachsingle.binding.selectallseats.isChecked = false

                        if (isApplyBPDPFare == "true") {
                            callBpDpServiceApi(
                                selectedBoarding?.id.toString(),
                                selectedDropping?.id.toString()
                            )
                        } else {
                            callServiceApi()
                        }
                    } else {
                        toast(it.message)
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                toast("An error occurred while Blocking Seat(s)")
            }
        }

        blockViewModel.unblockSeats.observe(this) {
            binding.includeProgress.progressBar.gone()
            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            DialogUtils.successfulBlockSeatDialog(
                                this,
                                getString(R.string.seat_unblock_unsuccessful)
                            )
                            if (isApplyBPDPFare == "true") {
                                callBpDpServiceApi(
                                    selectedBoarding?.id.toString(),
                                    selectedDropping?.id.toString()
                                )
                            } else {
                                callServiceApi()
                            }
                        }

                        401 -> {
                            /*DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            showUnauthorisedDialog()

                        }

                        else -> {
                            //toast("Error in unblocking Seats")
                            toast(it.message.toString())
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (e: Exception) {
                toast(e.message.toString())
            }
        }

    }


    private fun setSeatLegendsAdapter() {
        val colorLegendName = arrayListOf<String>()
        val color1 = arrayListOf<String>()
        val color2 = arrayListOf<String>()

        for (i in 0 until legendDetails.size) {
            if (legendDetails[i].color!!.contains(",")) {
                val color = legendDetails[i].color!!.split(",")
                colorLegendName.add(legendDetails[i].colorLegend.toString())
                color1.add(color[0])
                color2.add(color[1])

            } else {
                colorLegendName.add(legendDetails[i].colorLegend.toString())
                color1.add(legendDetails[i].color!!)
                color2.add(legendDetails[i].color!!)
            }
        }
        Timber.d("legendPart: $colorLegendName, \n $color1 \n , $color2")
        seatLegendsAdapter.setList(colorLegendName, color1, color2)


    }

    private fun initRecyclerView() {
        binding.layoutPassengerDetailsSeatLegends.rvPassengersDetailsSeats.layoutManager =
            GridLayoutManager(this, 5)
        binding.layoutPassengerDetailsSeatLegends.rvPassengersDetailsSeats.adapter =
            SeatLegendsAdapter()
        seatLegendsAdapter = SeatLegendsAdapter()
        binding.layoutPassengerDetailsSeatLegends.rvPassengersDetailsSeats.adapter =
            seatLegendsAdapter
    }


    private fun initVariables() {

        // userTypeId = 0
        selectedUserTypeList.clear()

        branchId = null
        userId = null
        agentId = null
        blockTypeValue = "none"
        fromDate = null
        toDate = null

        selectedSeatDetails.clear()
        binding.sUserType.setSelection(0)
        try {
            binding.sBlockType.setText(binding.sBlockType.adapter.getItem(0).toString(), false)
        } catch (t: Throwable) {

        }
        //As block type none is selected by default therefore following views are no long required to be displayed
        binding.apply {
            blockDurationTextView.visibility = View.GONE
            blockDateDurationLinearLayout.visibility = View.GONE
            tvFromDate.visibility = View.GONE
            tvToDate.visibility = View.GONE
            releaseTimeTextView.visibility = View.GONE
            layoutHour.visibility = View.GONE
            editTextHour.visibility = View.GONE
            layoutMinute.visibility = View.GONE
            editTextMinute.visibility = View.GONE
            tvFromDate.text = getString(R.string.fromDate)
            tvToDate.text = getString(R.string.toDate)   
        }

        invalidateCount()
        if (isAllowMultipleQuota)
            binding.sUserType.setText("")
    }


    private fun callBlockConfigApi() {
        if (isNetworkAvailable()) {
            val reqBody = ReqBody(apiKey, locale = locale)
            val blockConfigRequest =
                BlockConfigRequest(bccId, format_type, block_config_method_name, reqBody)

            blockViewModel.blockConfigurationApi(
                loginModelPref.api_key,
                locale!!,
                block_config_method_name
            )


        } else
            noNetworkToast()
    }

    private fun callUserListApi() {
        if (isNetworkAvailable()) {
            val reqBody = com.bitla.ts.domain.pojo.user_list.request.ReqBody(
                apiKey,
                userTypeId,
                locale = locale
            )
            val userListRequest =
                UserListRequest(bccId, format_type, user_list_method_name, reqBody)


            blockViewModel.userListApi(
                apiKey = loginModelPref.api_key,
                cityId = "",
                userType = userTypeId.toString(),
                branchId = branchId.toString(),
                locale = locale!!,
                apiType = user_list_method_name
            )
            Timber.d("userListRequest $userListRequest")

        } else
            noNetworkToast()
    }

    private fun callBranchListApi() {
        if (isNetworkAvailable()) {
            val reqBody = com.bitla.ts.domain.pojo.branch_list_model.request.ReqBody(apiKey, locale = locale)
            val branchListRequest = BranchListRequest(bccId, format_type, branch_list_method_name, reqBody)

            blockViewModel.branchListApi(
                loginModelPref.api_key,
                locale ?: "en",
                branch_list_method_name
            )

        } else
            noNetworkToast()
    }

    private fun callBpDpServiceApi(boarding: String, dropping: String) {

        if (isNetworkAvailable()) {
            sharedViewModel.getBpDpServiceDetails(
                reservationId = resId.toString(),
                apiKey = loginModelPref.api_key,
                origin = sourceId,
                destinationId = destinationId,
                operator_api_key = operator_api_key,
                locale = "$locale",
                apiType = service_details_method,
                boardingAt = boarding,
                dropOff = dropping
            )

        } else {
            noNetworkToast()
        }
    }

    private fun callServiceApi() {

        if (isNetworkAvailable()) {
            sharedViewModel.getServiceDetails(
                reservationId = resId.toString(),
                apiKey = loginModelPref.api_key,
                originId = sourceId,
                destinationId = destinationId,
                operatorApiKey = operator_api_key,
                locale = locale!!,
                apiType = service_details_method,
                excludePassengerDetails = false
            )
        } else {
            noNetworkToast()
        }
    }

    private fun blockRadioGroup() {
        val radioId = binding.rgBlockAll.checkedRadioButtonId
        val selectedBlockRadio = findViewById<RadioButton1>(radioId)
    }

    private fun userTypeSpinner() {
        binding.sUserType.onItemSelectedListener = this

        if (isAllowMultipleQuota) {

            binding.sUserType.setAdapter(
                SelectUserTypeArrayAdapter(this,
                    R.layout.spinner_dropdown_item_witch_checkbox,
                    R.id.tvItem,
                    userTypeList,
                    selectedUserTypeList,
                    isAllowMultipleQuota,
                    object : SelectUserTypeArrayAdapter.ItemClickListener {
                        override fun onSelected(position: Int, item: SpinnerItems) {
                            if (selectedUserTypeList.contains(item).not())
                                selectedUserTypeList.add(item)
                            binding.sUserType.setText(item.value)

                            userTypeTag(position)
                            invalidateCount()
                            setButtonObservable()
                        }

                        override fun onDeselect(position: Int, item: SpinnerItems) {
                            if (selectedUserTypeList.contains(item))
                                selectedUserTypeList.remove(item)
                            binding.sUserType.setText(
                                selectedUserTypeList.firstOrNull().toString().replace("null", "")
                            )
                            if (selectedUserTypeList.size == 0) {
                                binding.sUserType.isFocusable = false
                                userTypeTag(position)
                            }
                            if (selectedUserTypeList.size == 1) {
                                val selectedPosition = userTypeList.indexOfFirst {
                                    it.value == selectedUserTypeList[0].value
                                }
                                binding.sUserType.isFocusable = true
                                userTypeTag(selectedPosition)

                            }
                            invalidateCount()
                            setButtonObservable()
                        }
                    })
            )
        } else {
            if (userTypeList.isNotEmpty()) {
                binding.sUserType.setText(userTypeList[0].value)
                selectedUserTypeList.clear()
                selectedUserTypeList.add(userTypeList[0])
                userTypeTag(0)
                setButtonObservable()
            }
            binding.sUserType.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    userTypeList
                )
            )


            binding.sUserType.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    selectedUserTypeList.clear()
                    selectedUserTypeList.add(userTypeList[position])
                    userTypeTag(position)
                    setButtonObservable()
                }
        }


//        binding.sUserType.onItemSelectedListener = this

        /*binding.sUserType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                userTypeTag(position)

                *//*if (view?.tag == getString(R.string.userType)) {

                    userTypeTag(position)

                } else if (view?.tag == getString(R.string.blockType)) {
                    blockTypeTag(position)
                }*//*

                setButtonObservable()
            }*/

        binding.sBlockType.setOnItemClickListener { adapterView, view, position, l ->
            blockTypeTag(position)
            setButtonObservable()
        }

    }

    private fun invalidateCount() {
        if (selectedUserTypeList.size > 1) {
            binding.tvMoreUserType.apply {
                visibility = View.VISIBLE
                text = "+ ${selectedUserTypeList.size - 1} more"
            }
        } else {
            binding.tvMoreUserType.visibility = View.GONE
        }
    }

    private fun updateBlockContentMessage(): String {
        val blockContent = getString(R.string.blockContentUserTypes)
        if (!isAllowMultipleQuota) {
            return "$blockContent ${binding.sUserType.text}"
        } else {
            var temp = "and "
            var prefix = ""
            if (selectedUserTypeList.size > 1) {
                temp += selectedUserTypeList[selectedUserTypeList.size - 1].value
                for (i in 0..selectedUserTypeList.size - 2) {
                    prefix += selectedUserTypeList[i].value + ", "
                }
                prefix = prefix.substring(0, prefix.length - 2)
                temp = prefix + " " + temp
            } else if (selectedUserTypeList.size == 1) {
                temp = selectedUserTypeList[selectedUserTypeList.size - 1].value
            } else {
                temp = ""
            }
            return blockContent + " " + temp
        }
    }

    private fun blockTypeSpinner() {
        /*    binding.sBlockType.onItemSelectedListener = this
            blockSpinnerAdapter = SpinnersAdapter(
                applicationContext,
                blockTypeList,
                getString(R.string.blockType)
            )
            binding.sBlockType.adapter = blockSpinnerAdapter*/
        blockTypeList = blockTypeList.asReversed()
        blockTypeValue = blockTypeList.get(0).value
        binding.sBlockType.setText(blockTypeList.get(0).value)
        binding.sBlockType.setAdapter(
            ArrayAdapter<SpinnerItems>(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                blockTypeList
            )
        )
    }

    private fun userListSpinner() {
        saveUserList(userList)
    }

    private fun stageUserSpinner() {
        saveAgentList(userList)
    }

    private fun branchListSpinner() {
        saveBranchList(branchList)
    }

    private fun saveblockTypeSpinner() {
        saveBlockType(blockTypeList)
    }

    /* override fun getLayout(): Int {
         return R.layout.activity_block
     }*/

    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityBlockBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        initRecyclerView()

        bindingSheet = SheetReleaseTicketsBinding.inflate(LayoutInflater.from(this))
        bottomSheetDialoge = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheetDialoge.setContentView(bindingSheet.root)

        binding.includeToolbar.tvScan.gone()
        binding.includeToolbar.layoutLegendLocation.visible()
        binding.includeToolbar.currentLocation.gone()

        binding.includeToolbar.imgLegend.setOnClickListener {
            binding.layoutPassengerDetailsSeatLegends.root.visible()
            binding.tansparentbackbround.visible()
        }
        binding.layoutPassengerDetailsSeatLegends.passengerDetailsTvOkay.setOnClickListener {
            binding.layoutPassengerDetailsSeatLegends.root.gone()
            binding.tansparentbackbround.gone()
        }

        clickListener()

        lifecycleScope.launch {
            supervisorScope {
                launch {
                    ticketDetailsViewModel.messageSharedFlow.collect{
                        if (it.isNotEmpty()){
                            showToast(it)
                        }
                    }
                }
                launch {
                    blockViewModel.messageSharedFlow.collect{
                        if (it.isNotEmpty()){
                            showToast(it)
                        }
                    }
                }
                launch {
                    sharedViewModel.messageSharedFlow.collect{
                        if (it.isNotEmpty()){
                            showToast(it)
                        }
                    }
                }
                launch {
                    dashboardViewModel.messageSharedFlow.collect{
                        if (it.isNotEmpty()){
                            showToast(it)
                        }
                    }
                }
            }

        }
    }

    private fun clickListener() {
        binding.btnServiceSummary.setOnClickListener(this)
        binding.includeToolbar.imgBack.setOnClickListener(this)
        binding.tvFromDate.setOnClickListener(this)
        binding.tvToDate.setOnClickListener(this)
        binding.editTextHour.setOnClickListener(this)
        binding.editTextMinute.setOnClickListener(this)
        binding.tvAgentTypeSpinner.setOnClickListener(this)
        binding.tvBranchTypeSpinner.setOnClickListener(this)
        binding.tvUserSpinner.setOnClickListener(this)
        binding.imgCollapse.setOnClickListener(this)
        binding.imgExpand.setOnClickListener(this)
        binding.editPriceLayout.tvProceed.setOnClickListener(this)
        binding.tvEditSeats.setOnClickListener(this)
        binding.btnBlockSeats.setOnClickListener(this)
        binding.layoutWeekdaysLinear.layoutSunday.setOnClickListener(this)
        binding.layoutWeekdaysLinear.layoutMonday.setOnClickListener(this)
        binding.layoutWeekdaysLinear.layoutTuesday.setOnClickListener(this)
        binding.layoutWeekdaysLinear.layoutWednesday.setOnClickListener(this)
        binding.layoutWeekdaysLinear.layoutThursday.setOnClickListener(this)
        binding.layoutWeekdaysLinear.layoutFriday.setOnClickListener(this)
        binding.layoutWeekdaysLinear.layoutSaturday.setOnClickListener(this)

//        binding.sUserType.setOnClickListener(this)

        binding.layoutMultistation.setOnClickListener(this)
        binding.layoutBoardinPoint.setOnClickListener(this)
        binding.layoutDroppingPoint.setOnClickListener(this)
        binding.layoutBookedBy.setOnClickListener(this)
        binding.layoutServiceDetails.setOnClickListener(this)
        binding.layoutBookings.setOnClickListener(this)
        binding.layoutCollections.setOnClickListener(this)
        binding.layoutReleasedTickets.setOnClickListener(this)

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    @SuppressLint("SetTextI18n")
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    private fun setButtonObservable() {
        blockViewModel.changeButtonBackground1(
            selectedSeats = selectedSeats,
            selectedUserTypeList = selectedUserTypeList,
            userTypeId = userTypeId,
            branchId = branchId,
            userId = userId,
            agentId = agentId,
            blockTypeValue = blockTypeValue,
            fromDate = fromDate,
            toDate = toDate,
            timeHH = timeHH,
            timeMM = timeMM,
            isAllowMultipleQuota = isAllowMultipleQuota
        )
    }

    private fun blockTypeTag(position: Int) {
        blockTypeId = blockTypeList[position].id
        blockTypeValue = blockTypeList[position].value
        binding.includeToolbar.tvCurrentHeader.text = getString(R.string.blocking)
        if (blockTypeValue.equals("permanent", true)) {

            binding.blockDurationTextView.gone()
            //binding.tvFromDate.text=null
            binding.tvFromDate.visibility = View.GONE
            //binding.tvToDate.text=null
            binding.tvToDate.visibility = View.GONE
            binding.blockDateDurationLinearLayout.gone()
            binding.btnBlockSeats.setBackgroundResource(R.drawable.button_selected_bg)
            binding.btnBlockSeats.isEnabled = true


            blockViewModel.changeButtonBackground1(
                selectedSeats = selectedSeats,
                selectedUserTypeList = selectedUserTypeList,
                userTypeId = userTypeId,
                branchId = branchId,
                userId = userId,
                agentId = agentId,
                blockTypeValue = blockTypeValue,
                fromDate = fromDate,
                toDate = toDate,
                timeHH = timeHH,
                timeMM = timeMM,
                isAllowMultipleQuota = isAllowMultipleQuota
            )
            
            binding.apply {
                releaseTimeTextView.gone()
                layoutHour.gone()
                editTextHour.gone()
                layoutMinute.gone()
                editTextMinute.gone()
                selectRecurringDays.gone()
                mainWeekendLayout.gone()    
            }
           
        } else if (blockTypeValue.equals("none", true)) {
            binding.apply {
                mainWeekendLayout.visible()
                selectRecurringDays.visible()
                blockDurationTextView.visibility = View.GONE
                blockDateDurationLinearLayout.visibility = View.GONE
                tvFromDate.visibility = View.GONE
                tvToDate.visibility = View.GONE
                releaseTimeTextView.visibility = View.GONE
                layoutHour.visibility = View.GONE
                editTextHour.visibility = View.GONE
                layoutMinute.visibility = View.GONE
                editTextMinute.visibility = View.GONE
            }
          
        } else if (blockTypeValue.equals("custom", true) || blockTypeValue.equals(
                "temporary",
                true
            )
        ) {
            binding.apply {
                selectRecurringDays.visible()
                mainWeekendLayout.visible()
                releaseTimeTextView.visibility = View.GONE
                layoutHour.visibility = View.GONE
                editTextHour.visibility = View.GONE
                layoutMinute.visibility = View.GONE
                editTextMinute.visibility = View.GONE
                editTextHour.text = null
                editTextMinute.text = null
                blockDurationTextView.visibility = View.VISIBLE
                blockDateDurationLinearLayout.visibility = View.VISIBLE
                tvFromDate.visibility = View.VISIBLE
                tvToDate.visibility = View.VISIBLE
                toDate = binding.tvToDate.text.toString()
                fromDate = binding.tvFromDate.text.toString()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun userTypeTag(
        position: Int
    ) {
        userTypeId = this.userTypeList[position].id
        userTypeValue = this.userTypeList[position].value

        // set toolbar header
        binding.includeToolbar.tvCurrentHeader.text = "$userTypeValue - ${getString(R.string.blocking)}"
        // when user type "onl-Agt"
        userTypeOnlineAgent()
        //when user type "USER"
        userTypeUSER()
    }

    private fun userTypeUSER() {
        if (selectedUserTypeList.any {
                it.value.equals(
                    "USER",
                    true
                )
            } && selectedUserTypeList.size == 1) {
            binding.tvBranchType.visible()
            binding.layoutBranch.visible()
            binding.tvSelectUserType.visible()
            binding.layoutUser.visible()
            // call "user_list" api on selection of "USER" from "user type" spinner
            callBranchListApi()
        } else {
            hiddenPropertiesForUser()
        }
    }

    private fun hiddenPropertiesForUser() {
        binding.tvBranchType.gone()
        binding.layoutBranch.gone()
        binding.tvSelectUserType.gone()
        binding.layoutUser.gone()
        //binding.tvBranchHint.gone()
        //binding.tvUserHint.gone()
        // binding.tvBranchTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
        //binding.tvUserSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
    }

    private fun hiddenPropertiesAll() {
        if (isAllowMultipleQuota && !userTypeValue.equals("USER", true))
            hiddenPropertiesForUser()
        // binding.tvFromDate.gone()
        binding.tvFromDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)

        binding.tvToHint.gone()
        binding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)

        /*binding.tvBlockTypeHint.gone()
        if (::blockSpinnerAdapter.isInitialized)
            blockSpinnerAdapter.unSelectViewColor()*/

        // binding.tvUserTypeHint.gone()
        /*if (::spinnerAdapter.isInitialized)
            spinnerAdapter.unSelectViewColor()*/

        //binding.tvAgentHint.gone()
        //binding.tvAgentTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
    }

    private fun userTypeOnlineAgent() {
        if (selectedUserTypeList.any {
                it.value.equals(
                    "Onl-Agt",
                    true
                )
            } && selectedUserTypeList.size == 1) {
            binding.tvAgentType.visible()
            binding.layoutAgent.visible()

            /*if (binding.tvAgentTypeSpinner.text.toString() != getString(R.string.selectAgent)) {
                binding.tvAgentHint.visible()
                binding.tvAgentTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
            } else {
                binding.tvAgentHint.gone()
                binding.tvAgentTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
            }*/

            callUserListApi()
        } else {
            // binding.tvAgentHint.gone()
            binding.tvAgentType.gone()
            binding.layoutAgent.gone()
            //binding.tvAgentTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
        }
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag == SelectedSeatsAdapter.TAG) {
            val v: View = selectedSeatDetails[position].seatView!!
            v.tag = selectedSeatDetails[position].seatStatusData
            v.id = selectedSeatDetails[position].seatCount!!

//            commonCoach.seatSelection(v)
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.btn_service_summary -> {
                tabs("btnServiceSummary")
            }

            R.id.fabsummary -> {
                binding.editPriceLayout.fabsummary.gone()
                tabs("fabSummary")
            }

            R.id.img_back -> {
                onBackPressed()
            }

            R.id.tvFromDate -> {
                dateType = getString(R.string.fromDate)
                if (binding.tvToDate.text != getString(R.string.toDate)) {
                    binding.tvToHint.gone()
                    binding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)
                    binding.tvToDate.text = getString(R.string.toDate)
                    val scale = resources.displayMetrics.density
                    val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                    binding.tvToDate.setPadding(paddingtLeftRightinDp, 0, paddingtLeftRightinDp, 0)
                }

                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show(supportFragmentManager, TAG)
                fromDate = binding.tvFromDate.text.toString()
                setButtonObservable()


            }

            R.id.tvToDate -> {
                dateType = getString(R.string.toDate)
                val fromDate: String = binding.tvFromDate.text.toString()
                val toDate1: String = binding.tvToDate.text.toString()

                if (fromDate == getString(R.string.fromDate)) {
                    toast("Please select from date")
                } else {

                    if (fromDate != getString(R.string.fromDate) && toDate1 != getString(R.string.toDate)) // both the dates already selected
                    {
                        SlyCalendarDialog()
                            .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setEndDate(stringToDate(toDate1, DATE_FORMAT_D_M_Y))
                            .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(supportFragmentManager, TAG)
                    } else if (fromDate != getString(R.string.fromDate)) // only from date selected
                    {
                        SlyCalendarDialog()
                            .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(supportFragmentManager, TAG)
                    } else {
                        SlyCalendarDialog()
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(supportFragmentManager, TAG)
                    }
                }
                toDate = binding.tvToDate.text.toString()

            }

            R.id.editText_hour -> {
                timeHH = binding.editTextHour.text.toString()
            }

            R.id.editText_minute -> {
                timeMM = binding.editTextMinute.text.toString()
            }

            R.id.tvAgentTypeSpinner -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(
                    getString(R.string.CITY_SELECTION_TYPE),
                    getString(R.string.selectAgent)
                )
                startActivityForResult(intent, RESULT_CODE_SEARCH_AGENT)
            }

            R.id.tvBranchTypeSpinner -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(
                    getString(R.string.CITY_SELECTION_TYPE),
                    getString(R.string.selectBranch)
                )
                startActivityForResult(intent, RESULT_CODE_SEARCH_BRANCH)
            }

            R.id.tvUserSpinner -> {
                if (branchId == null) {
                    toast("Please select branch")
                } else {
                    val intent = Intent(this, SearchActivity::class.java)
                    intent.putExtra(
                        getString(R.string.CITY_SELECTION_TYPE),
                        getString(R.string.selectUser)
                    )
                    startActivityForResult(intent, RESULT_CODE_SEARCH_USER)
                }
            }

            R.id.img_collapse -> {
                onExpand(false)
            }

            R.id.img_expand -> {
                onCollapse(false)
            }

            R.id.tv_proceed -> {
                isProceeded = true
//                isblocked = 1
                if (isCanBlockSeats) {
                    if (isAllSeatSelected)
                        binding.tvSeatSelected.text = getString(R.string.selectedAvailableSeats)
                    else
                        binding.tvSeatSelected.text =
                            selectedSeats?.let { replaceBracketsString(it) }
                    binding.coachUnderView.visible()
                    onCollapse(true)
                } else {
                    toast(getString(R.string.blockSeatFunctionalityNotApplicable))
                }
            }

            R.id.tvEditSeats -> {
                onExpand(true)
            }

            R.id.layout_sunday -> {
                val txtview = findViewById<TextView>(R.id.tvSunday)
                setDaysBackground(0, v, txtview)
            }

            R.id.layout_monday -> {
                val txtview = findViewById<TextView>(R.id.tvMonday)
                setDaysBackground(1, v, txtview)
            }

            R.id.layout_tuesday -> {
                val txtview = findViewById<TextView>(R.id.tvTuesday)
                setDaysBackground(2, v, txtview)
            }

            R.id.layout_wednesday -> {
                val txtview = findViewById<TextView>(R.id.tvWednesday)
                setDaysBackground(3, v, txtview)
            }

            R.id.layout_thursday -> {
                val txtview = findViewById<TextView>(R.id.tvThursday)
                setDaysBackground(4, v, txtview)
            }

            R.id.layout_friday -> {
                val txtview = findViewById<TextView>(R.id.tvFriday)
                setDaysBackground(5, v, txtview)
            }

            R.id.layout_saturday -> {
                val txtview = findViewById<TextView>(R.id.tvSaturday)
                setDaysBackground(6, v, txtview)
            }

            R.id.btn_block_seats -> {
                /*
                                var editTextHour = findViewById(R.id.editText_hour) as EditText
                                timeHH = editTextHour.text.toString()

                                var editTextMinutes = findViewById(R.id.editText_minute) as EditText
                                timeMM = editTextMinutes.text.toString()

                                Timber.d(TAG, "onClick: " + timeHH)
                                if (blockTypeValue.equals("none")) {
                                    fromDate = ""
                                    toDate = ""
                                    timeHH = ""
                                    timeMM = ""
                                } else if (blockTypeValue.equals("permanent")) {
                                    fromDate = ""
                                    toDate = ""
                                } else if (blockTypeValue.equals("custom") || blockTypeValue.equals("temporary")) {
                                    if (fromDate.equals(""))
                                        fromDate = null
                                    if (toDate.equals(""))
                                        toDate = null
                                }
                */
                timeHH = binding.editTextHour.text.toString()
                timeMM = binding.editTextMinute.text.toString()

                blockViewModel.validation(
                    selectedSeats = selectedSeats,
                    selectedUserTypeList = selectedUserTypeList,
                    userTypeId = userTypeId,
                    branchId = branchId,
                    userId = userId,
                    agentId = agentId,
                    blockTypeValue = blockTypeValue,
                    fromDate = fromDate,
                    toDate = toDate,
                    timeHH = timeHH,
                    timeMM = timeMM,
                    isAllowMultipleQuota = isAllowMultipleQuota
                )
            }

            R.id.sUserType -> {
                if (isClick == 0) {
                    binding.rvSelectMultipleUserType.visible()
                    isClick++
                } else {
                    binding.rvSelectMultipleUserType.gone()
                    isClick = 0
                }
            }

            R.id.layout_multistation -> {
                val intent = Intent(this, ServiceSummaryActivity::class.java)
                intent.putExtra("TYPE", "Multistation Booking")
                startActivity(intent)
            }

            R.id.layout_boardin_point -> {
                val intent = Intent(this, ServiceSummaryActivity::class.java)
                intent.putExtra("TYPE", "Boarding Points")
                startActivity(intent)
            }

            R.id.layout_dropping_point -> {
                val intent = Intent(this, ServiceSummaryActivity::class.java)
                intent.putExtra("TYPE", "Dropping Points")
                startActivity(intent)
            }

            R.id.layout_booked_by -> {
                val intent = Intent(this, ServiceSummaryActivity::class.java)
                intent.putExtra("TYPE", "Booked By")
                startActivity(intent)
            }

            R.id.layout_bookings -> {
                val intent = Intent(this, ServiceSummaryActivity::class.java)
                intent.putExtra("TYPE", "Bookings")
                startActivity(intent)
            }

            R.id.layout_collections -> {
                val intent = Intent(this, ServiceSummaryActivity::class.java)
                intent.putExtra("TYPE", "Collection")
                startActivity(intent)
            }

            R.id.layout_released_tickets -> {
                val intent = Intent(this, ServiceSummaryActivity::class.java)
                intent.putExtra("TYPE", "Released Tickets")
                startActivity(intent)
            }

            R.id.layout_service_details -> {
                //val busDetails = "$travelDate $source - $destination $busType "

                val busDetails = "$serviceNumber $travelDate $source - $destination $busType "
                val intent = Intent(this, ServiceDetailsActivity::class.java)
                intent.putExtra(getString(R.string.origin), source)
                intent.putExtra(getString(R.string.destination), destination)
                intent.putExtra(getString(R.string.bus_type), busDetails)

                PreferenceUtils.apply {
                    removeKey(getString(R.string.scannedUserName))
                    removeKey(getString(R.string.scannedUserId))
                    removeKey("selectedScanType")
                    removeKey(getString(R.string.scan_coach))
                    removeKey(getString(R.string.scan_driver_1))
                    removeKey(getString(R.string.scan_driver_2))
                    removeKey(getString(R.string.scan_cleaner))
                    removeKey(getString(R.string.scan_contractor))
                }
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setDateLocale(PreferenceUtils.getlang(), this@BlockActivity)

    }

    private fun onExpand(editbtn: Boolean) {
        binding.layoutCoach.visible()
        binding.tvSeatSelected.gone()

        binding.tvEditSeats.gone()

        if (editbtn) {
            binding.imgCollapse.gone()
            binding.imgExpand.gone()
            binding.editPriceLayout.root.visible()
            binding.tvSeatSelected.gone()
            binding.multioptionView.gone()
            binding.coachUnderView.gone()
        } else {
            binding.imgCollapse.gone()
//            binding.imgExpand.visible()
            binding.multioptionView.visible()
        }


        if (finalSeatNumbers.isEmpty()) {
            binding.tvSeatSelected.text != getString(R.string.noSeatSelected)
            binding.tvSeatSelected.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.white)))
            binding.tvSeatSelected.backgroundTintList =
                resources.getColorStateList(R.color.colorDimShadow6)
        } else {
            binding.tvSeatSelected.text = selectedSeats?.let { replaceBracketsString(it) }
            binding.tvSeatSelected.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
            binding.tvSeatSelected.backgroundTintList =
                resources.getColorStateList(R.color.colorPrimaryTransparent)
        }
    }

    private fun onCollapse(proceed: Boolean) {
        binding.tvSeatSelected.visible()
        binding.layoutCoach.gone()


        if (proceed) {
            binding.imgExpand.gone()
            binding.imgCollapse.gone()
            binding.tvEditSeats.visible()
            binding.multioptionView.gone()
            binding.coachUnderView.visible()
            binding.editPriceLayout.root.gone()
        } else {
            binding.imgExpand.gone()
//            binding.imgCollapse.visible()
            binding.tvEditSeats.gone()
            binding.multioptionView.visible()
            binding.coachUnderView.gone()
        }


        if (finalSeatNumbers.isEmpty()) {
            binding.tvSeatSelected.text != getString(R.string.noSeatSelected)
            binding.tvSeatSelected.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.white)))
            binding.tvSeatSelected.backgroundTintList =
                resources.getColorStateList(R.color.colorDimShadow6)
        } else {
            binding.tvSeatSelected.text = selectedSeats?.let { replaceBracketsString(it) }
            binding.tvSeatSelected.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
            binding.tvSeatSelected.backgroundTintList =
                resources.getColorStateList(R.color.colorPrimaryTransparent)
        }
    }

    override fun onCancelled() {

    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?,
        hours: Int,
        minutes: Int
    ) {
        val scale = resources.displayMetrics.density
        if (firstDate != null) {
            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours)
                firstDate.set(Calendar.MINUTE, minutes)
                binding.tvFromDate.visible()
                binding.tvFromHint.visible()
                binding.tvFromDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)


                if (dateType != null && dateType == getString(R.string.fromDate)) {
                    fromDate = SimpleDateFormat(
                        DATE_FORMAT_D_M_Y,
                        Locale.getDefault()
                    ).format(firstDate.time)

                    binding.tvFromDate.text = fromDate
                    val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                    val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                    binding.tvFromDate.setPadding(
                        paddingtLeftRightinDp,
                        paddingtTopinDp,
                        paddingtLeftRightinDp,
                        0
                    )
                } else {
                    toDate = SimpleDateFormat(
                        DATE_FORMAT_D_M_Y,
                        Locale.getDefault()
                    ).format(firstDate.time)

                    binding.tvToDate.text = toDate
                    binding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                    binding.tvToHint.visible()
                    val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                    val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                    binding.tvToDate.setPadding(
                        paddingtLeftRightinDp,
                        paddingtTopinDp,
                        paddingtLeftRightinDp,
                        0
                    )
                }

            } else {
                binding.tvToHint.visible()
                binding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                fromDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)
                binding.tvFromDate.text = fromDate
                val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                binding.tvFromDate.setPadding(
                    paddingtLeftRightinDp,
                    paddingtTopinDp,
                    paddingtLeftRightinDp,
                    0
                )


                toDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(secondDate.time)
                binding.tvToDate.text = toDate
                binding.tvToDate.setPadding(
                    paddingtLeftRightinDp,
                    paddingtTopinDp,
                    paddingtLeftRightinDp,
                    0
                )

                setButtonObservable()
            }
        } else {
            binding.tvFromDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)
            binding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
            binding.tvFromDate.gone()
            binding.tvToHint.gone()
        }
        setButtonObservable()

    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (data?.getStringExtra(getString(R.string.SELECTED_SEARCHED_TYPE)) != null) {
                val selectedType: String =
                    data.getStringExtra(getString(R.string.SELECTED_SEARCHED_TYPE)).toString()
                val selectedName: String =
                    data.getStringExtra(getString(R.string.SELECTED_SEARCHED_NAME)).toString()
                val selectedId: String =
                    data.getStringExtra(getString(R.string.SELECTED_SEARCHED_ID)).toString()


                if (selectedType == getString(R.string.selectAgent)) {
                    agentId = selectedId
                    // binding.tvAgentHint.visible()
                    binding.tvAgentTypeSpinner.setText(selectedName)
                    //binding.tvAgentTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                    setButtonObservable()
                } else if (selectedType == getString(R.string.selectBranch)) {
                    // binding.tvBranchHint.visible()
                    branchId = selectedId
                    binding.tvBranchTypeSpinner.setText(selectedName)
                    //binding.tvUserSpinner.setText(getString(R.string.selectUser))
                    // binding.tvBranchTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                    if (userTypeValue.equals("USER", true) || userTypeId == 12)
                        callUserListApi()
                    else
                        callUserListApi()
                    setButtonObservable()
                } else if (selectedType == getString(R.string.selectUser)) {
                    userId = selectedId
                    //binding.tvUserHint.visible()
                    binding.tvUserSpinner.setText(selectedName)
                    //binding.tvUserSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                    setButtonObservable()
                }
            }
        }
    }

    override fun onSeatSelection(
        selectedSeatDetails: ArrayList<SeatDetail>,
        finalSeatNumber: ArrayList<String?>,
        totalSum: Double,
        isAllSeatSelected: Boolean,
        isSeatLongPress: Boolean?
    ) {
        this.selectedSeatDetails = selectedSeatDetails
        this.isAllSeatSelected = isAllSeatSelected
        this.totalSum = totalSum
        this.finalSeatNumbers = finalSeatNumber

        var selectedSeatsNumber: String? = ""
        if (finalSeatNumbers.isNotEmpty()) {
            binding.multioptionView.gone()
            binding.btnServiceSummary.gone()
            binding.coachUnderView.gone()
            binding.imgCollapse.gone()
            binding.imgExpand.gone()
            binding.editPriceLayout.editpriceLayout.visible()
            binding.editPriceLayout.editprice.gone()
            binding.editPriceLayout.layoutExtraSeatProceed.gone()
            binding.editPriceLayout.tvSelectedSeats.text =
                replaceBracketsString(finalSeatNumbers.toString())
            binding.editPriceLayout.tvSelectedSeats.movementMethod = ScrollingMovementMethod()
//            if (coachChoice == "SingleViewSelected") {
//
//            }
            val proceedLayout = findViewById<View>(R.id.proceed_layout) as LinearLayout
            val proceedLayoutObserver = proceedLayout.viewTreeObserver
            var proceedLayoutHeight: Int = 0
            binding.editPriceLayout.proceedLayout.measure(0, 0)
            val height = binding.editPriceLayout.proceedLayout.measuredHeight
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 0, 0, height)
            binding.layoutCoach.layoutParams = layoutParams
//            if(commonCoach.binding.emptyLinearLayout.visibility==View.GONE)
//                commonCoach.binding.emptyLinearLayout.visibility=View.VISIBLE
//            proceedLayoutObserver.addOnGlobalLayoutListener {
//                Timber.d(
//                    "Log",
//                    "Height: " + proceedLayout.height
//                )
//                proceedLayoutHeight = proceedLayout.height
//                var commonCoachEmptyLayout=commonCoach.binding.emptyLinearLayout
//                var params: ViewGroup.LayoutParams = commonCoachEmptyLayout.layoutParams
//                params.height = proceedLayoutHeight
//                commonCoachEmptyLayout.layoutParams = params
//
//
//            }
//            if (isAllSeatSelected) {
//                toast("allselected")
//            } else {
//
//                setSelectedSeatAdapter(selectedSeatDetails)
//            }

            selectedSeatDetails.forEach {
                selectedSeatsNumber = "$selectedSeatsNumber ${it.number},"
            }

            selectedSeats = selectedSeatsNumber?.removeSuffix(",")
            setButtonObservable()


        } else {
//            commonCoach.binding.emptyLinearLayout.visibility=View.GONE
            onNoSeatSelection()


        }
    }

    private fun onNoSeatSelection() {
        selectedSeats = null

        binding.editPriceLayout.editpriceLayout.gone()
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 0)
        binding.layoutCoach.layoutParams = layoutParams
//        binding.imgExpand.visible()
        binding.btnServiceSummary.visible()
        binding.multioptionView.visible()
        binding.coachUnderView.gone()
        binding.editPriceLayout.fabsummary.setOnClickListener(this)
        binding.tvSeatSelected.text = getString(R.string.noSeatSelected)
        binding.tvSeatSelected.backgroundTintList =
            resources.getColorStateList(R.color.colorDimShadow6)
        binding.tvSeatSelected.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.white)))

        setButtonObservable()
    }

//    private fun setSelectedSeatAdapter(selectedSeatDetails: ArrayList<SeatDetail>) {
//        binding.rvSeats.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        binding.rvSeats.adapter =
//            SelectedSeatsAdapter(this, this, selectedSeatDetails, false)
//    }

    override fun unSelectAllSeats() {
        onNoSeatSelection()
    }

    override fun onLeftButtonClick() {

    }

    override fun onRightButtonClick() {
        isblocked = 0
        callBlockSeatApi()
    }

    private fun callBlockSeatApi() {

        if (binding.sBlockType.text.toString().equals("none")) {

            timeMM = ""
            timeHH = ""
            toDate = ""
            fromDate = ""
        } else if (binding.sBlockType.text.equals("permanent")) {
            toDate = ""
            fromDate = ""
        } else if (binding.sBlockType.text.equals("custom") || binding.sBlockType.text.equals("temporary")) {
            timeHH = ""
            timeHH = ""

        }
        var isBima: Boolean? = null
        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }
        val blockSeatRequest = BlockSeatRequest()
        blockSeatRequest.bccId = bccId
        blockSeatRequest.methodName = block_seat_method_name
        blockSeatRequest.format = format_type

        val reqBody1 = ReqBody__1()
        reqBody1.agentType = userTypeId.toString()
        reqBody1.apiKey = loginModelPref.api_key
        reqBody1.isFromMiddleTier = true
        reqBody1.locale = locale
        reqBody1.mainOpId = mainOpId
        reqBody1.operatorApiKey = operator_api_key
        reqBody1.reservedSeatCount = selectedSeatDetails.size
        reqBody1.is_bima = isBima
        reqBody1.remarks = binding.remrksField.text.toString()
        if (binding.sBlockType.text.toString().equals("none", true))
            reqBody1.selectionType = ""
        else if (binding.sBlockType.text.toString().equals("custom", true))
            reqBody1.selectionType = "custom"
        else if (binding.sBlockType.text.toString().equals("permanent", true))
            reqBody1.selectionType = "apply_all"
        else if (binding.sBlockType.text.toString().equals("temporary", true))
            reqBody1.selectionType = "temporary"

        reqBody1.resId = resId.toString()
        reqBody1.agent_types = null
        val ticket = Ticket()
        ticket.selectedSeats = selectedSeats?.trim()
        ticket.selectedSeats = ticket.selectedSeats?.replace(" ", "")
        reqBody1.ticket = ticket
        //toast(ticket.selectedSeats.toString())


        val searchBusParams = SearchbusParams()

        if (isApplyBPDPFare == "true") {
            searchBusParams.from = sourceId
            searchBusParams.to = destinationId
//            if (sourceId.contains(":")) {
//                val src = sourceId.split(":")
//                searchBusParams.from = sourceId
//            }
//            if (destinationId.contains(":")) {
//                val dest = destinationId.split(":")
//                searchBusParams.to = dest[1]
//            }
        } else {
            searchBusParams.from = sourceId
            searchBusParams.to = destinationId
        }

        reqBody1.searchbusParams = searchBusParams

        val recordModel = Record()
        recordModel.weekly_schedule = getSelectedWeekdays()
        recordModel.from_date = fromDate
        recordModel.to_date = toDate
        recordModel.quota_release_hours = timeHH
        recordModel.quota_release_mins = timeMM

        if (binding.sBlockType.text.toString().equals("none")) {
            recordModel.from_date = ""
            recordModel.to_date = ""
            recordModel.quota_release_hours = ""
            recordModel.quota_release_mins = ""
        } else if (binding.sBlockType.text.toString().equals("custom")) {
            recordModel.quota_release_hours = ""
            recordModel.quota_release_mins = ""
        } else if (binding.sBlockType.text.toString().equals("permanent")) {
            recordModel.from_date = ""
            recordModel.to_date = ""
        } else if (binding.sBlockType.text.toString().equals("temporary")) {
            recordModel.quota_release_hours = ""
            recordModel.quota_release_mins = ""
        }



        reqBody1.record = recordModel
        // based on user type
        if (userTypeId == 12) // USER
        {
            reqBody1.searchBusOnBehalfBranch = branchId ?: getString(R.string.empty)
            reqBody1.searchBusOnBehalfUser = userId ?: getString(R.string.empty)
        } else if (userTypeId == 1) // Onl-Agt
        {
            reqBody1.searchBusOnBehalfOnlineAgent = agentId ?: getString(R.string.empty)
        }

        if (isAllowMultipleQuota && selectedUserTypeList.size >= 2) {
            var a: String = ""
            reqBody1.agentType = null
            selectedUserTypeList.forEach {
                if (it.value.equals("Api-Agt", ignoreCase = true)) {
                    a += "9" + ","
                    return@forEach
                }
                if (it.value.equals("Default HO", ignoreCase = true)) {
                    a += "2" + ","
                    return@forEach
                }
                a += it.id.toString() + ","

            }
            reqBody1.agent_types = a.substring(0, a.length - 1)
            //Timber.d(TAG, "callBlockSeatApi: User type is 12, User List is: " + reqBody1.agent_types)
        } else if (isAllowMultipleQuota && selectedUserTypeList.size == 1) {
            if (userTypeId == 1) {
                if (reqBody1.searchBusOnBehalfOnlineAgent?.isEmpty() == true) {
                    reqBody1.agent_types = reqBody1.agentType
                    reqBody1.agentType = null
                } else {
                    reqBody1.agentType = selectedUserTypeList[0].id.toString()
                }
            } else if (userTypeId == 12) {
                if (reqBody1.searchBusOnBehalfBranch?.isEmpty() == true) {
                    reqBody1.agent_types = selectedUserTypeList[0].id.toString()
                    reqBody1.agentType = null
                    reqBody1.searchBusOnBehalfBranch = null
                    if (reqBody1.searchBusOnBehalfUser?.isEmpty() == true) {
                        reqBody1.searchBusOnBehalfUser = null
                    } else {
                        reqBody1.searchBusOnBehalfBranch = branchId
                        reqBody1.searchBusOnBehalfUser = userId
                    }
                } else {
                    reqBody1.agentType = selectedUserTypeList[0].id.toString()
                }
            } else {
                if (selectedUserTypeList[0].value.equals("Default HO", ignoreCase = true)) {
                    reqBody1.agent_types = null
                    reqBody1.agentType = "2"
                } else {
                    reqBody1.agent_types = null
                    reqBody1.agentType = selectedUserTypeList[0].id.toString()
                }
            }
        } else {

            Timber.d("selectedUserTypeList=> ${binding.sUserType.text}")

            if (binding.sUserType.text.toString() == "Default HO") {
                reqBody1.agent_types = null
                reqBody1.agentType = "2"
            }
        }

        blockSeatRequest.reqBody = reqBody1
        blockViewModel.blockSeatsApi(
            reqBody1,
            block_seat_method_name
        )

    }

    private fun tabs(btn: String) {
        binding.summaryLayout.root.visible()
        binding.btnServiceSummary.gone()
        binding.tansparentbackbround.visible()

        val tabsList: MutableList<Tabs> = mutableListOf()

        val tabsummary = Tabs()
        tabsummary.title = getString(R.string.summary)
        tabsList.add(tabsummary)

        blockViewModel.privilegesLiveData.observe(this) { privilegeResponseModel ->

            if (privilegeResponseModel?.isChileApp == false) {
                val tabAmenities = Tabs()
                tabAmenities.title = getString(R.string.amenities)
                tabsList.add(tabAmenities)
            }
//        val tabAmenities = Tabs()
//        tabAmenities.title = getString(R.string.amenities)
//        tabsList.add(tabAmenities)

            val tabCancellation = Tabs()
            tabCancellation.title = getString(R.string.cancellation)
            tabsList.add(tabCancellation)


            val fragmentAdapter = ServiceSummaryTabsAdapter(
                context = this,
                tabList = tabsList,
                fm = this.supportFragmentManager,
                privilegeResponseModel = privilegeResponseModel
            )

            binding.summaryLayout.viewpagerPickup.adapter = fragmentAdapter
            binding.summaryLayout.tabsPickup.setupWithViewPager(binding.summaryLayout.viewpagerPickup)
            // custom tabs
            for (i in 0..binding.summaryLayout.tabsPickup.tabCount.minus(1)) {
                val tab = binding.summaryLayout.tabsPickup.getTabAt(i)!!
                tab.customView = null
                //tab!!.customView = fragmentAdapter.getTabView(i)

                val tabTextView: TextView = TextView(this)
                tab.customView = tabTextView

                tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

                tabTextView.text = tab.text

                if (i == 0) {
                    // This set the font style of the first tab
                    tabTextView.setTypeface(null, Typeface.BOLD)

                }
                if (i == 1) {
                    // This set the font style of the second tab
                    tabTextView.setTypeface(null, Typeface.NORMAL)
                }

            }
            binding.summaryLayout.tabsPickup.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    binding.summaryLayout.viewpagerPickup.currentItem = tab!!.position
                    val text: TextView? = tab.customView as TextView?
                    text?.setTypeface(null, Typeface.BOLD)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val text: TextView = tab?.customView as TextView
                    text.setTypeface(null, Typeface.NORMAL)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
        }


        binding.summaryLayout.headText.setOnClickListener {
            if (btn == "btnServiceSummary") {
                binding.summaryLayout.root.gone()
                binding.btnServiceSummary.visible()

                binding.tansparentbackbround.gone()
            } else {
                binding.summaryLayout.root.gone()
                binding.editPriceLayout.fabsummary.visible()

                binding.tansparentbackbround.gone()
            }
        }
    }

    private fun getSelectedWeekdays(): String {
        var days: String = ""
        weekdays.forEach {
            if (it.isSelected == true) {
                days = days + "1"
            } else
                days = days + "0"
        }
        return days
    }

    private fun userTypeRecyclerView() {
        binding.rvSelectMultipleUserType.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectMultipleUserType.adapter =
            SelectUserTypeAdapter(this, userTypeList, this)
    }

    private fun setDaysBackground(
        index: Int,
        view: View,
        textView: TextView
    ) {
        if (!weekdays[index].isSelected) {
            // Change this color for selection
            view.setBackgroundColor(Color.parseColor("#00adb5"))
            textView.setTextColor(Color.parseColor("#ffffff"))
            weekdays[index].isSelected = true
        } else {
            view.setBackgroundColor(Color.parseColor("#ffffff"))
            textView.setTextColor(Color.parseColor("#9b9b9b"))
            weekdays[index].isSelected = false

        }
    }

    fun setWeekdays() {
        weekdays.add(Weekdays("Sun", true))
        weekdays.add(Weekdays("Mon", true))
        weekdays.add(Weekdays("Tue", true))
        weekdays.add(Weekdays("Wed", true))
        weekdays.add(Weekdays("Thu", true))
        weekdays.add(Weekdays("Fri", true))
        weekdays.add(Weekdays("Sat", true))
        findViewById<TextView>(R.id.tvSunday).text = weekdays[0].day
        findViewById<TextView>(R.id.tvMonday).text = weekdays[1].day
        findViewById<TextView>(R.id.tvTuesday).text = weekdays[2].day
        findViewById<TextView>(R.id.tvWednesday).text = weekdays[3].day
        findViewById<TextView>(R.id.tvThursday).text = weekdays[4].day
        findViewById<TextView>(R.id.tvFriday).text = weekdays[5].day
        findViewById<TextView>(R.id.tvSaturday).text = weekdays[6].day
    }

    override fun unblockSeat(
        seatNumber: String,
        selectionType: String,
        fromDate: String?,
        toDate: String?,
        remarks: String?
    ) {
        if (this.isNetworkAvailable()) {

            var isBima: Boolean? = null
            if (PreferenceUtils.getPreference("is_bima", false) == true) {
                isBima = true
            }

            val ticket = com.bitla.ts.domain.pojo.unblock_seat.request.Ticket(seatNumber)
            val searchBusParams = com.bitla.ts.domain.pojo.unblock_seat.request.SearchbusParams(
                sourceId,
                destinationId
            )

            blockViewModel.privilegesLiveData.observe(this) { privilegeResponseModel ->

                val record = com.bitla.ts.domain.pojo.unblock_seat.request.Record(
                    fromDate = fromDate,
                    quotaReleaseHours = "",
                    quotaReleaseMins = "",
                    releaseHoursBeforeDeparture = "",
                    toDate = toDate,
                    weeklySchedule = "1111111",
                    remarks = ""
                )

                //val searchBusParams = SearchbusParams()
                if (isApplyBPDPFare == "true") {
                    if (sourceId.contains(":")) {
                        val src = sourceId.split(":")
                        searchBusParams.from = src[1]
                    }
                    if (destinationId.contains(":")) {
                        val dest = destinationId.split(":")
                        searchBusParams.to = dest[1]
                    }
                }
                else {
                    searchBusParams.from = sourceId
                    searchBusParams.to = destinationId
                }


                val reqBody = com.bitla.ts.domain.pojo.unblock_seat.request.ReqBody(
                    apiKey = loginModelPref.api_key,
                    isFromMiddleTier = true,
                    locale = locale,
                    mainOpId = mainOpId,
                    operatorApiKey = operator_api_key,
                    record = record,
                    resId = resId.toString(),
                    rowCountDates = "0",
                    searchbusParams = searchBusParams,
                    selectionType = if (privilegeResponseModel?.country.equals("Indonesia", true)) {
                        ""
                    } else selectionType,
                    ticket = ticket
                )

                UnblockSeatRequest(
                    bccId = bccId,
                    format = format_type,
                    methodName = unblock_seat_method_name,
                    reqBody = reqBody
                )

                blockViewModel.unblockSeatsApi(
                    request = reqBody,
                    apiType = unblock_seat_method_name
                )
            }

        } else this.noNetworkToast()
    }

    override fun bookExtraSeats(isChecked: Boolean?, isSeatSelected: Boolean?) {
        Timber.d("checkPointer: $isChecked")

    }

    override fun moveExtraSeat(isChecked: Boolean) {
        Timber.d("checkPointer: $isChecked")

    }

    override fun releaseTicket(ticket: String, releaseTicket: String) {
        releaseTicketNumber = ticket
        isReleaseTicket = releaseTicket
        if (this.isNetworkAvailable())
            callTicketDetailsApi()
        else this.noNetworkToast()

    }

    override fun callPassenger(ticketNumber: String, contactNumber: String) {
    }

    override fun checkBoardedStatus(
        status: Boolean,
        passengerName: String,
        pnrNum: String,
        seatNumber: String,
        view: View
    ) {

    }

    override fun selectedSeatCount(selectedSeats: ArrayList<SeatDetail>) {

    }


    @SuppressLint("SetTextI18n")
    private fun callTicketDetailsApi() {
        val bccId = getBccId()
        loginModelPref = getLogin()
//        binding.includeProgress.progressBar.visible()
        var numeric = false
        if (intent.hasExtra("qrscan")) {
            numeric = intent.getBooleanExtra("qrscan", false)
        } else {
            numeric = false
        }

        try {
            java.lang.Double.parseDouble(releaseTicketNumber.toString())
        } catch (e: NumberFormatException) {
            Timber.d(" numeric: $numeric")

//            numeric = true
        }

        Timber.d("ticketNumber : ${releaseTicketNumber}, numeric: $numeric")

        val reqBody = releaseTicketNumber.trim()?.let {
            com.bitla.ts.domain.pojo.ticket_details.request.ReqBody(
                it, loginModelPref.api_key,
                jsonFormat = true,
                isFromMiddleTier = true,
                isFromQrScan = numeric,
                locale = locale
            )
        }

        val ticketDetailsRequest = reqBody?.let {
            TicketDetailsRequest(
                bccId = bccId.toString(),
                format = format_type,
                methodName = ticket_details_method_name,
                reqBody = it
            )
        }

        if (ticketDetailsRequest != null) {
            /*   ticketDetailsViewModel.ticketDetailsApi(
                   loginModelPref.auth_token,
                   loginModelPref.api_key,
                   ticketDetailsRequest,
                   ticket_details_method_name
               )
   */
            ticketDetailsViewModel.ticketDetailsApi(
                apiKey = loginModelPref.api_key,
                ticketNumber = releaseTicketNumber,
                jsonFormat = true,
                isQrScan = numeric, locale = locale!!,
                apiType = ticket_details_method_name
            )
        }

    }


    private fun releaseTicketFun() {
        _sheetReleaseTicketsBinding = SheetReleaseTicketsBinding.inflate(LayoutInflater.from(this))


        ticketDetailsViewModel.dataTicketDetails.observe(this) {
            if (!passengerDetailList.isNullOrEmpty()) {
                passengerDetailList?.clear()

            }
            if (it != null) {
                when (it.code) {
                    200 -> {
                        passengerDetailList = it.body.passengerDetails
                        if (isReleaseTicket == "true") {
//                            val bottomSheetDialoge = BottomSheetDialog(this, R.style.BottomSheetDialog)
//                            bottomSheetDialoge.setContentView(_sheetReleaseTicketsBinding.root)

                            bindingSheet.releaseTicketBtn.setOnClickListener {

                                Timber.d("seat2:added-$currentCheckedItem")

                                for (i in 0 until currentCheckedItem.size) {
                                    selectedSeatNumber.append(currentCheckedItem[i]?.seatNumber)
                                    if (i < currentCheckedItem.size - 1) {
                                        selectedSeatNumber.append(",")
                                    }
                                }
                                if (selectedSeatNumber.isEmpty()) {
                                    toast(getString(R.string.selectSeat))
                                } else {
                                    bindingSheet.progressBarRelease.visible()
                                    if (this.isNetworkAvailable()) {
                                        authPinPhoneReleaseDialog(releaseTicketNumber)
                                        setReleaseTicketObserver(bottomSheetDialoge)
                                    } else this.noNetworkToast()
                                }
                            }
                            setReleaseTicketPassengerAdapter()

                            bottomSheetDialoge.show()
                        } else {
                            passengerList.clear()
                            seatList.clear()
                            selectedSeatDetails.clear()
                            Timber.d("resultselected-3$isReleaseTicket")

                            pnr = it.body.ticketNumber
                            boardingStageID = it.body.dropOffDetails?.stageId.toString()
                            boarding = it.body.origin
                            dropping = it.body.destination ?: getString(R.string.notAvailable)
                            bAddress = it.body.boardingDetails?.stageName
                                ?: getString(R.string.notAvailable)
                            dAddress = it.body.dropOffDetails?.stageName
                                ?: getString(R.string.notAvailable)
                            boardingTravelDate = it.body.boardingDetails?.travelDate
                                ?: getString(R.string.notAvailable)
                            boardingDepTime =
                                it.body.boardingDetails?.depTime ?: getString(R.string.notAvailable)
                            dropOffTravelDate = it.body.dropOffDetails?.travelDate
                                ?: getString(R.string.notAvailable)
                            dropOffDepTime =
                                it.body.dropOffDetails?.arrTime ?: getString(R.string.notAvailable)


                            if (it.body.passengerDetails != null) {

                                for (i in 0..it.body.passengerDetails.size.minus(1)) {
                                    isShiftPassenger = it.body.passengerDetails[i]!!.canShiftTicket
                                    isCanCancelTicket = it.body.passengerDetails[i]!!.canCancel
//                                    seatList.clear()
                                    selectedSeatDetails.clear()

                                    passengerContactDetailList.add(
                                        ContactDetail(
                                            "${it.body.passengerDetails[i]?.mobile}",
                                            "${it.body.passengerDetails[i]?.mobile}",
                                            "${it.body.passengerDetails[i]?.email}"
                                        )
                                    )
                                    passengerList.add(
                                        PassengerDetailsResult(
                                            expand = true,
                                            isPrimary = true,
                                            seatNumber = it.body.passengerDetails[i]!!.seatNumber,
                                            name = it.body.passengerDetails[i]!!.name,
                                            age = it.body.passengerDetails[i]?.age.toString(),
                                            sex = it.body.passengerDetails[i]!!.gender,
                                            contactDetail = passengerContactDetailList,
                                            fare = it.body.passengerDetails[i]?.netFare
                                        )
                                    )

                                    for (j in 0 until passengerList.size) {
                                        val seatDetail = SeatDetail()
                                        seatDetail.isPrimary = true
                                        seatDetail.number = it.body.passengerDetails[j]?.seatNumber ?: ""
                                        seatDetail.sex = it.body.passengerDetails[j]?.gender
                                        seatDetail.name = it.body.passengerDetails[j]?.name
                                        seatDetail.age =
                                            it.body.passengerDetails[j]?.age?.toString()
                                        seatDetail.fare = it.body.passengerDetails[i]?.netFare
                                        selectedSeatDetails.add(seatDetail)
                                    }

                                    setSelectedPassengers(passengers = passengerList)

                                    val seatDetailNew = SeatDetail()
                                    seatDetailNew.number = it.body.seatNumbers ?: ""
                                    seatList.add(seatDetailNew)
                                    setSelectSeats(seatList)
                                }
                                setSelectSeats(selectedSeatDetail = selectedSeatDetails)
                            }

                            PreferenceUtils.apply {
                                setPreference(PREF_BOARDING_TIME, boardingDepTime)
                                setPreference(PREF_BOARDING_AT, bAddress)
                                setPreference(PREF_BOARDING_DATE, boardingTravelDate)
                                setPreference(PREF_DROP_OFF_TIME, dropOffDepTime)
                                setPreference(PREF_DROP_OFF, dAddress)
                                setPreference(PREF_DROP_OFF_DATE, dropOffTravelDate)
                            }
                            val intent = Intent(this, ConfirmPhoneBookingActivity::class.java)
                            intent.putExtra("fromTicketDetailsActivity", true)
                            intent.putExtra(getString(R.string.pnr_number), pnr)
                            intent.putExtra(
                                getString(R.string.select_boarding_stage),
                                boardingStageID
                            )
                            intent.putExtra(
                                getString(R.string.select_dropping_stage),
                                droppingStageID
                            )
                            intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                            intent.putExtra(
                                getString(R.string.travel_date),
                                getDateDMY(travelDate.toString())
                            )
                            intent.putExtra(getString(R.string.bus_type), busType)
                            intent.putExtra(getString(R.string.source_id), sourceId)
                            intent.putExtra(getString(R.string.destination_id), destinationId)
                            intent.putExtra(getString(R.string.origin), boarding)
                            intent.putExtra(getString(R.string.destination), dropping)
                            intent.putExtra(
                                getString(R.string.totalAmount),
                                it.body.totalFare.toString()
                            )
                            startActivity(intent)
                        }

                    }

                    401 -> {
                       /* DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    else -> {
                        if (it.result?.message != null) {
                            it.result.message.let { it1 ->
                                toast(it1)
                            }
                        }
                        if (it.result?.message != null) {
                            it.result.message.let { it1 ->
                                toast(it1)
                            }
                        }
                        if (it.result?.message != null) {
                            it.result.message.let { it1 ->
                                toast(it1)
                            }
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun dismissProgressBar() {
        _sheetReleaseTicketsBinding.progressBarRelease.gone()
    }

    private fun authPinPhoneReleaseDialog(releaseTicketNumber: String) {

        blockViewModel.privilegesLiveData.observe(this) { privilegeResponseModel ->

            if (shouldPhoneBlockingRelease && privilegeResponseModel?.country.equals("India", true)) {
                DialogUtils.showFullHeightPinInputBottomSheet(
                    activity = this@BlockActivity,
                    fragmentManager = supportFragmentManager,
                    pinSize = pinSize,
                    getString(R.string.phone_block_release),
                    onPinSubmitted = { pin: String ->
                        callReleaseTicketApi(releaseTicketNumber, pin)
                        dismissProgressBar()
                        selectedSeatNumber.clear()
                        currentCheckedItem.clear()
                    },
                    onDismiss = {
                        dismissProgressBar()
                        selectedSeatNumber.clear()
                    }
                )
            }
            else {
                callReleaseTicketApi(releaseTicketNumber, "")
                dismissProgressBar()
                selectedSeatNumber.clear()
                currentCheckedItem.clear()
            }
        }
    }

    private fun setReleaseTicketPassengerAdapter() {
        bindingSheet.rvPassengers.layoutManager = LinearLayoutManager(
            /* context = */ this,
            /* orientation = */ LinearLayoutManager.VERTICAL,
            /* reverseLayout = */ false
        )

        bindingSheet.rvPassengers.adapter = ReleaseTicketPassengersListAdapter(
            context = this,
            passengerDetailSeatList = passengerDetailList,
            onItemCheckListener = this
        )
    }

    private fun callReleaseTicketApi(ticketNumber: String, authPin: String) {
        val releaseTicketRequest = ReleaseTicketRequest(
            bccId.toString(),
            format_type,
            release_phone_block_ticket_method_name,
            com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBody(
                apiKey = loginModelPref.api_key,
                pnrNumber = ticketNumber.toString(),
                remarks = "release ticket",
                isFromDashboard = false,
                ticket = com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.Ticket(
                    selectedSeatNumber.toString()
                ),
                json_format = json_format,
                locale = locale,
                authPin = authPin
            )
        )
        /*dashboardViewModel.releaseTicketAPI(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            releaseTicketRequest,
            release_phone_block_ticket_method_name
        )*/

        dashboardViewModel.releaseTicketAPI(
            com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBody(
                apiKey = loginModelPref.api_key,
                pnrNumber = ticketNumber.toString(),
                remarks = "release ticket",
                isFromDashboard = false,
                ticket = com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.Ticket(
                    selectedSeatNumber.toString()
                ),
                json_format = json_format,
                locale = locale,
                authPin = authPin
            ),
            release_phone_block_ticket_method_name
        )
    }

    private fun setReleaseTicketObserver(bottomSheetDialoge: BottomSheetDialog) {

        dashboardViewModel.releaseTicketResponseViewModel.observe(this) { releaseApi ->

            try {
                if (releaseApi != null) {
                    when (releaseApi.code) {
                        200 -> {
                            //DialogUtils.successfulMsgDialog(this, getString(R.string.successfully_released_ticket))
                            _sheetReleaseTicketsBinding.progressBarRelease.gone()
                            toast(releaseApi.message)
                            bottomSheetDialoge.dismiss()
                            binding.coachProgressBar.visible()
                            if (isApplyBPDPFare == "true") {
                                callBpDpServiceApi(
                                    selectedBoarding?.id.toString(),
                                    selectedDropping?.id.toString()
                                )
                            } else {
                                callServiceApi()
                            }
                            currentCheckedItem.clear()
                        }

                        401 -> {
                            /*DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            showUnauthorisedDialog()

                        }

                        else -> {
                            if(!releaseApi.result.message.isNullOrEmpty()){
                                toast(releaseApi.result.message)
                            } else if (!releaseApi.message.isNullOrEmpty()) {
                                toast(releaseApi.message)
                            } else {
                                toast(releaseApi.message)
                            }
                            _sheetReleaseTicketsBinding.progressBarRelease.gone()
                            currentCheckedItem.clear()
                            bottomSheetDialoge.dismiss()

                        }
                    }
                } else {

                    toast(getString(R.string.server_error))
                    _sheetReleaseTicketsBinding.progressBarRelease.gone()
                    currentCheckedItem.clear()
                    bottomSheetDialoge.dismiss()
                }
            } catch (e: Exception) {
                toast(e.message.toString())
                _sheetReleaseTicketsBinding.progressBarRelease.gone()
                currentCheckedItem.clear()
                bottomSheetDialoge.dismiss()
            }
        }
    }

    override fun editSeatFare(seatNumber: String, newFare: String) {
        Timber.d("checkPointer: $seatNumber , $newFare")

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

    override fun onItemCheck(item: PassengerDetail?) {
        currentCheckedItem.add(item)
        Timber.d("seat:added-$currentCheckedItem")
    }

    override fun onItemUncheck(item: PassengerDetail?) {
        currentCheckedItem.remove(item)
        if (currentCheckedItem.size == 0) {
            currentCheckedItem.clear()
            selectedSeatNumber.clear()

        }
        selectedSeatNumber.clear()
        Timber.d("seat:removed-$currentCheckedItem")
    }
}