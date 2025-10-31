package com.bitla.ts.presentation.view.activity


/*Modified By "GAurav Saini" on 6th august*/


import SingleViewModel
import android.Manifest
import android.annotation.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.content.res.*
import android.graphics.*
import android.net.*
import android.os.*
import android.text.*
import android.util.Log
import android.view.*
import android.view.View.*
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.*
import androidx.cardview.widget.*
import androidx.constraintlayout.widget.*
import androidx.core.content.*
import androidx.core.text.*
import androidx.core.view.*
import androidx.fragment.app.Fragment
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
import com.bitla.ts.domain.pojo.block_unblock_reservation.ReasonList
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.drag_drop_remarks_update.request.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.move_to_extra_seat.request.*
import com.bitla.ts.domain.pojo.move_to_normal_seats.*
import com.bitla.ts.domain.pojo.passenger_details_result.*
import com.bitla.ts.domain.pojo.pickup_chart_crew_details.response.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.sendOtpAndQrCode.request.*
import com.bitla.ts.domain.pojo.send_sms_email.request.*
import com.bitla.ts.domain.pojo.service_details.request.*
import com.bitla.ts.domain.pojo.service_details.request.ReqBody
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.service_details_response.Body
import com.bitla.ts.domain.pojo.shortRouteCityPair.*
import com.bitla.ts.domain.pojo.single_block_unblock.single_block_unblock_request.*
import com.bitla.ts.domain.pojo.ticket_details.request.*
import com.bitla.ts.domain.pojo.ticket_details.response.*
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.view.activity.reservationOption.extendedFare.*
import com.bitla.ts.presentation.view.activity.ticketDetails.*
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.view.fragments.*
import com.bitla.ts.presentation.view.passenger_payment.*
import com.bitla.ts.presentation.view.passenger_payment_show_new_flow.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getBccId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getDestinationId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLogin
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getSourceId
import com.bitla.tscalender.*
import com.google.android.material.bottomsheet.*
import com.google.android.material.tabs.*
import com.google.zxing.integration.android.*
import com.skydoves.balloon.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.io.*
import java.text.*
import java.util.*
import kotlin.math.*
import com.bitla.ts.domain.pojo.fare_breakup.request.ChargeDetails
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.PassengerDetail
import com.bitla.ts.domain.pojo.trackingo_response.TrackingoResponse
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Double.parseDouble
import kotlin.collections.ArrayList


class NewCoachActivity : BaseActivity(), OnSeatSelectionListener, OnItemClickListener,
    DialogSingleButtonListener, VarArgListener, OnItemCheckListener,
    BulkUpdateInterface,
    SingleMenuClickListener, DialogButtonAnyDataListener {

    companion object {
        val TAG = NewCoachActivity::class.java.simpleName
    }
    private val ticketDetailsComposeViewModel by viewModel<TicketDetailsComposeViewModel<Any?>>()
private var totalNetAmount: String = ""
private var transactionFare: String = ""
    private var isServiceLoading = false
    private var progressBar: androidx.appcompat.app.AlertDialog? = null
    private var isMultiHopService: Boolean? = false
    private var isDateSelected: Boolean = false
    private var isEditChartClicked: Boolean = false
    private var excludePassengerDetails: Boolean = false
    private var isFirstTime: Boolean = true
    private var newUpdatedprice: String? = null
    private var editFareBinding: DialogEditFareBinding? = null
    private var editFareAdapter: EditFareAdapter? = null
    private var restrictSkipVerification = false
    private var cancelOtp = ""
    private var otpDialog: AlertDialog? = null
    private val cancelTicketViewModel by viewModel<CancelTicketViewModel<Any?>>()


    private var isSwitchClicked = false
    private var lastSelectedSeatNumber: String = ""
    private var pnrAdapter: SeatPassengerAdapter? = null
    private var lastSelectedSeatPosition: Int = 0
    private var callNumber: String = ""
    private var crewDetailsData: PickupChartCrewDetailsResponse? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var cancelOptkey = ""


    private var crewDetailBottomSheetBinding: BottomsheetCrewDetailsCoachBinding? = null
    private var lastSelectedBookingTypePosition: Int = 0
    private lateinit var binding: LayoutBookingNewCoachActivityBinding

    private lateinit var editFareDialogBuilder: AlertDialog
    private var selectedCityIdNo: Int = 0
    private var isAgentLogin: Boolean = false
    private lateinit var tvEditTotalFare: TextView
    private var blockedSeatsList = arrayListOf<SeatDetail>()

    //private var newEditTotalFare: Double = 0.0
    private var minExtraSeatFare: Int = 0
    private var extraSeats: Int = 1
    private var isClickBoardingPoint: Boolean = false
    private lateinit var commonCoach: AllCoachFragment

    private var finalSeatNumbers = arrayListOf<String?>()
    private var totalSum: Double = 0.0

    private var cancelOtpLayoutDialogOpenCount = 0



    var reservationId: Long = 0L
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var tempSourceId: String = ""
    private var tempDestinationId: String = ""
    private var tempSourceName: String = ""
    private var tempDestinationIdName: String = ""


    lateinit var context: Context
    private var loginModelPref: LoginModel = LoginModel()
    private var bccId: Int? = 0
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var travelDateModifyLayout: String = ""
    private var busType: String? = ""
    private var deptTime: String? = ""
    var selectedSeatDetails = ArrayList<SeatDetail>()
    var selectedExtraSeatsDetails = ArrayList<SeatDetail>()
    var editFareSeatDetails = ArrayList<SeatDetail>()
    private var isAllSeatSelected: Boolean = false

    private val singleViewModel by viewModel<SingleViewModel<Any?>>()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private val cityDetailViewModel by viewModel<CityDetailViewModel<Any?>>()
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private val bookingViewModel by viewModel<BookingOptionViewModel<Any?>>()
    private lateinit var seatLegendsAdapter: SeatLegendsAdapter
    private lateinit var branchLegendsAdapter: SeatLegendsAdapter
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    var backgroundColor: String? = ""
    private var boardingPointList = mutableListOf<StageDetail>()
    private var droppingPointList = mutableListOf<StageDetail>()
    private var stageDetails = mutableListOf<StageDetail>()
    private var legendDetails = arrayListOf<LegendDetail>()
    private var branchLegendDetails = arrayListOf<LegendDetail>()
    private var boardingPoint: String = ""
    private var droppingPoint: String = ""
    private lateinit var droppingStageDetail: StageDetail
    private lateinit var boardingStageDetail: StageDetail
    private var mainOpId: String? = null
    private var isOwnRoute = false
    private var serviceNumber = ""
    private var serviceTravelDate = ""
    private var serviceBusType = ""
    private var totalSeats = ""
    private var coachDetails: CoachDetails? = null
    private var legendDetailList = ""
    var myHashMap = HashMap<Int, FareDetailPerSeat>()
    private var isBima: Boolean = false
    private var phoneBlockingRelease = false


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
    private var passengerContactDetailList: java.util.ArrayList<ContactDetail> =
        java.util.ArrayList()
    private val seatList = ArrayList<SeatDetail>()
    private lateinit var _sheetReleaseTicketsBinding: SheetReleaseTicketsBinding
    private lateinit var switch: SwitchCompat
    private val currentCheckedItem: MutableList<com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail?> = ArrayList()
    private val selectedSeatNumber = StringBuilder()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()

    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()
    private var passengerDetailList: MutableList<com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail?>? = null
    private var releaseTicketNumber: String = ""
    private var isReleaseTicket: String = ""
    lateinit var bindingSheet: SheetReleaseTicketsBinding
    lateinit var bottomSheetDialoge: BottomSheetDialog
    private var currency: String = ""
    private var currencyFormat: String = ""
    lateinit var bindingStatus: SheetBoardedCheckBinding
    lateinit var bottomSheetDialogStatus: BottomSheetDialog
    private var skipQrCcode: Boolean = false
    private var passengerName: String = ""
    private var newOtp: String = ""
    private var templist = listOf<String>()
    private var qrresponse = ""
    private var resId: Long? = null
    private var scanTag = ""
    private var seatNum = ""
    private var pnrNum = ""
    private lateinit var bindingdialoge: DialogBookMenuBinding
    private var currencySymbol = ""
    private var selectedBoarding: BoardingPointDetail? = null
    private var selectedDropping: DropOffDetail? = null
    private var isApplyBPDPFare: String? = "false"
    private var locale: String? = ""
    private var countryList = java.util.ArrayList<Int>()
    private var countryName: String = ""
    private var isUpdateTicketCalled = false
    private var boardedSms = false
    private var isExtraSeatChecked = false
    private var isSelectedSeat = false
    private var navigateTag: String? = null
    private val bookExtraSeatNoList = mutableListOf<String>()
    private var serviceDetails: ServiceDetailsModel? = null

    private var bpDpBoarding: MutableList<BoardingPointDetail> = mutableListOf()
    private var bpDpDropping: MutableList<DropOffDetail> = mutableListOf()
    private var isPickupDropoffChargesEnabled: Boolean = false
    private var pickupChargeDetails = ChargeDetails()
    private var dropoffChargeDetails = ChargeDetails()
    private var isEnableCopyPassengerCheckbox: Boolean = true


    var emptyBoarding: BoardingPointDetail = BoardingPointDetail("", "", "", "", "")
    var emptyDropping: DropOffDetail = DropOffDetail("", "", "", "", "")

    private lateinit var rapidBookingDialog: AlertDialog

    private var ymdDate: String = ""
    private lateinit var baseUpdateCancelTicket: BaseUpdateCancelTicket
    private lateinit var cancelTicketSheet: CancelTicketSheet

    private lateinit var onSeatSelectionListener: OnSeatSelectionListener
    private val moveToExtraSeatViewModel by viewModel<MoveToExtraSeatViewModel<Any?>>()

    private var isQuickBookingsForTSApp: Boolean? = false
    private var coachOptionsArray: ArrayList<CoachOptionsModel> = arrayListOf()
    private var previousScreen: String? = null
    private lateinit var originSearchList: MutableList<SearchModel>
    private lateinit var shortRouteList: MutableList<City>
    private lateinit var destinationSearchList: MutableList<SearchModel>

    val originList = arrayListOf<CityPairItem>()
    private var isServiceBlocked = false
    private var bulkUpdationOfTickets = false
    private var isAllowBpDpFare: Boolean = false
    private var isEditReservation: Boolean? = false
    private var allowToExtendFareForServices = false
    private var isAllowRapidBookingFlow: Boolean = false
    private var isFromChile: Boolean = false
    private var showViewChartLinkInTheSearchResults = false
    private var isIndonesiaLogin: Boolean = false
    private var branchRoleDiscountType = ""
    private var discountType: String = ""
    private var discountValue = "0.0"
    private var role: String = ""
    private var updatePassengerTravelStatus = false
    private var isAllowOnlyOnce = false
    private var isExtraSeat = false
    private var ticketNumber = ""
    private var isPermanentPhoneBooking: Boolean = false
    private var removePreSelectionOptionInTheBooking: Boolean = false
    private var phoneBlockReleaseTime: String = ""
    private var calculatedHours: Long = 0L
    private var calculatedMinutes: Long = 0L
    private var releaseTimePoliciesOptions = ""
    private var checkAMOrPM = ""
    private var allowBookingForAllotedServices: Boolean = false
    private var allowBookingForAllServices: Boolean = false
    private lateinit var convertToPermanentPhoneBlockDialogBinding: DialogConvertToPermanentPhoneBlockBinding
    private lateinit var layoutNotifyPassengerOptionsBinding: LayoutNotifyPassengerOptionBinding
    private var convertToPermanentPhoneBlockDialog: AlertDialog? = null
    private var canBlockSeat = false
    private var canUnblockSeat = false

    private val reservationIdList = ArrayList<String>()
    private lateinit var iterator: MutableListIterator<String>
    private var swipePosition = 0
    private var isNextSwipeClick = false
    private var isPreviousSwipeClick = false
    private var reservationIdSwipe: String = ""
    private var isCityPairOrDateModified = false
    private var dateList = mutableListOf<StageData>()
    private var sevenDaysDate: String = getTodayDate()
    private var oldPosition: Int = 0
    private val availableRoutesViewModel by viewModel<AvailableRoutesViewModel<Any?>>()
    private val showOnlyAvailableServices: String = "false" //fixed
    private val showInJourneyServices: String = "true" // fixed
    private var isAllowToDoNextAndPreviousDatesServices = false
    private var availableRoutesList =
        mutableListOf<com.bitla.ts.domain.pojo.service_routes_list.response.Result>()
    private var srpServiceSelectionPos = 1
    private var availableSeatsCount = 0
    private var isBimaServiceDetails: Boolean? = null
    private var isAllowPhoneBlockingInBima: Boolean? = null
    private val multiSelectMoveToExtra = BaseMultiSelectMoveToExtraTicket()

    private var needed_seat_numbers: MutableList<String> = mutableListOf()
    private lateinit var notifyPassengerOptionsAdapter: NotifyPassengerOptionsAdapter
    private var notifyOptionsList: List<String>? = null
    private var shouldTicketMoveToSeatExtraSeat: Boolean = false
    private var pinSize = 0
    private var shouldPhoneBlockingRelease = false
     var callCoach = true
    private var editFareMandatoryForAgentUser = false
    private val originalFares = mutableListOf<String>()
    private val modifiedFares = mutableListOf<String>()
    private val originalSeatList = mutableListOf<String>()
    private var currentUnmodifiedSeat: String = ""

    private var blockReasonsList = mutableListOf<ReasonList>()
    private var shouldSingleBlockUnblock : Boolean = false
    private var trackingoData: TrackingoResponse? = null

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        callCoach = true
        getPref()
        setToolbarTitle()
    }


    override fun onResume() {
        super.onResume()


        progressBar = showAlertProgressDialog(this)

        originalFares.clear()
        currentUnmodifiedSeat = ""
        originalSeatList.clear()
        modifiedFares.clear()

        if (::originSearchList.isInitialized) {
            originSearchList.clear()
        }
//        isSelectedSeat = false
        if (isClickBoardingPoint) {
            if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
                droppingStageDetail =
                    PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)!!
                droppingPoint = droppingStageDetail.name!!
            }
            if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
                boardingStageDetail =
                    PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)!!
                boardingPoint = boardingStageDetail.name!!
            }
            if (boardingPoint.isNotEmpty()) {
                binding.tvSelectBoardingPoint.setText(boardingPoint)
            }
            if (droppingPoint.isNotEmpty()) {
                binding.tvSelectDropingPoint.setText(droppingPoint)
            }
        }


        val bookTicketInSameService =
            PreferenceUtils.getString(getString(R.string.rebooking_same_service))

        if (bookTicketInSameService == getString(R.string.book_ticket_in_same_service)) {
            if (isExtraSeatChecked) {
                binding.apply {
                    editPriceLayout.root.visible()
                    editPriceLayout.proceedLayout.gone()
                    editPriceLayout.editprice.gone()
                    editPriceLayout.layoutExtraSeatProceed.visible()
                    editPriceLayout.btnExtraBookingProceed.visible()
                    editPriceLayout.nextBackLayout.gone()

                    btnServiceSummary.gone()
                    layoutSummary.root.gone()
                }
            } else {
                binding.apply {
                    btnServiceSummary.visible()
                    binding.layoutSummary.root.gone()

                    coachSwipeButtonsVisibility()
                    editPriceLayout.proceedLayout.gone()
                    editPriceLayout.editprice.gone()
                    editPriceLayout.fabsummary.gone()
                    editPriceLayout.layoutExtraSeatProceed.gone()
                }
            }

            if (isBimaServiceDetails != null && isBimaServiceDetails!!) {
                binding.layoutSummary.root.gone()
            } else {
                binding.layoutSummary.root.visible()
            }

            binding.editPriceLayout.fabsummary.setOnClickListener(this)
            this.editFareSeatDetails.clear()
            this.selectedSeatDetails.clear()
            PreferenceUtils.removeKey(getString(R.string.rebooking_same_service))
        }

        if (isUpdateTicketCalled) {
            isUpdateTicketCalled = false
        }


        isClickBoardingPoint = false

        if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
            droppingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)!!
            droppingPoint = droppingStageDetail.name!!
        }
        if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
            boardingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)!!
            boardingPoint = boardingStageDetail.name!!
        }
        if (boardingPoint.isNotEmpty() && droppingPoint.isNotEmpty() && ::rapidBookingDialog.isInitialized && rapidBookingDialog.isShowing) {
            rapidBookingDialog.cancel()

            rapidBookingDialog = DialogUtils.rapidBookingDialog(
                boardingPoint = boardingPoint,
                droppingPoint = droppingPoint,
                context = this,
                varArgListener = this
            )!!

            return
        } else {
            val view: View = findViewById<View>(R.id.layout_coach_options) as ConstraintLayout

            if (this::commonCoach.isInitialized) {
                if (view.visibility == VISIBLE) {
                    closeToggle()
                }
                if (PreferenceUtils.getPreference(PREF_UPDATE_COACH, false) == true) {

                    if (isApplyBPDPFare == "true") {
                        callBpDpServiceApi(
                            selectedBoarding?.id.toString(),
                            selectedDropping?.id.toString()
                        )
                    } else {
                        callServiceApi()
                    }
                }
            }
        }

        // binding.editPriceLayout.root.gone()
        if (finalSeatNumbers.isNotEmpty()) {
            binding.btnServiceSummary.gone()
        }

        if (isExtraSeatChecked) {
            selectedSeatDetails.clear()
        }

        callServiceApi()
        binding.coachProgressBar.visible()

        if (privilegeResponseModel?.tsPrivileges?.allowServiceBlockingReasonsList == true) {
            if (isNetworkAvailable())
                pickUpChartViewModel.getServiceBlockReasonsListApi(loginModelPref.api_key)
            else
                noNetworkToast()

            serviceBlockReasonsListObserver()
        }
    }

    fun setBlockedList(list: ArrayList<SeatDetail>) {
        blockedSeatsList = list
    }

    fun hitMultistationSeatDetailApi(reservationId: String, seatNumber: String) {
        lastSelectedSeatNumber = seatNumber
        cityDetailViewModel.multistationPassengerDataApi(
            apiKey = loginModelPref.api_key,
            reservationId = reservationId,
            seatNumber = seatNumber,
            isBima = isBimaServiceDetails ?: false,
            apiType = multistation_seat_details_api,
            locale = locale ?: "en"
        )

    }

    private fun callPhoneBlockTempToPermanentApi(
        pnrNumber: String,
        apiKey: String,
    ) {
        val phoneBlockTempToPermanentReq =
            com.bitla.ts.domain.pojo.phone_block_temp_to_permanent_data.request.PhoneBlockTempToPermanentReq(
                apiKey = apiKey,
                pnrNumber = pnrNumber
            )

        cityDetailViewModel.phoneBlockTempToPermanent(
            phoneBlockTempToPermanent = phoneBlockTempToPermanentReq,
            apiType = phone_block_temp_to_permanent
        )
    }

    fun setIsExtraSeat(flag: Boolean) {
        isExtraSeat = flag
    }


    fun callPickupChartCrewDetailsApi() {
        pickUpChartViewModel.pickupChartCrewDetailsApi(
            loginModelPref.api_key,
            reservationId.toString(),
            pickup_chart_crew_details,
            locale = locale.toString()
        )
    }

    private fun setPickupChartCrewDetailsObserver() {
        pickUpChartViewModel.pickupChartCrewDetailsResponse.observe(this) { it ->
            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            crewDetailsData = it
                            showCrewDetailsBottomSheet(it)

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
                            it.result?.message?.let { it1 -> toast(it1) }

                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                toast(this.getString(R.string.opps))
                Timber.d("An error occurred at setPickupChartCrewDetailsObserver(): ${t.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@NewCoachActivity

        if (intent.hasExtra(getString(R.string.navigate_tag))) {
            navigateTag = intent.getStringExtra(getString(R.string.navigate_tag))
        }

        binding.layoutPassengerDetailsSeatLegends.passengerDetailsTvOkay.setOnClickListener {
            seatLegendAnimation()

        }
        getPref()
        init()
        PreferenceUtils.setPreference(PREF_UPDATE_COACH, false)
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun getDates(travelDateX: String) {

        privilegeResponseModel?.let {
            if (privilegeResponseModel?.isAllowBookingAfterTravelDate == true) {
                val date = travelDate
                if (date.isNotEmpty()) sevenDaysDate = getTwoDaysBack(date)
                sharedViewModel.getNextFiveCalenderDates(sevenDaysDate, travelDateX)
            } else {
                // get next five days date with current date
                if (travelDateX != getTodayDate()) {
                    val date = travelDateX
                    if (date.isNotEmpty()) {
                        val dateDifferenceInDays = getDateDifference(getTodayDate(), travelDateX)
                        sevenDaysDate = if (dateDifferenceInDays.toInt() == 1) getTwoDaysBack(
                            date, dateDifferenceInDays.toInt()
                        )
                        else getTwoDaysBack(date)
                    }
                    sharedViewModel.getNextFiveCalenderDates(sevenDaysDate, travelDateX)
                } else {
                    sevenDaysDate = travelDateX.ifEmpty { getTodayDate() }
                    sharedViewModel.getNextFiveCalenderDates(sevenDaysDate, travelDateX)
                }
            }
        }

        sharedViewModel.listOfDatesFiveDays.observe(this) {
            dateList = it

            Timber.d("NewCoachActivityDateList - $it")
            setDatesAdapterMethod()
        }
    }

    private fun setDatesAdapterMethod() {
        if (dateList.isNotEmpty()) {
            dateList.forEach { it.isSelected = false }
            dateList[oldPosition].isSelected = true
        }
        binding.rvDateDetails.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvDateDetails.adapter = MyBookingsDatesAdapter(
            context = this,
            onItemClickListener = this,
            menuList = dateList,
            isShowCalendar = false,
            travelDate
        )
    }

    private fun init() {

        binding.apply {
            coachProgressBar.visible()
            btnServiceSummary.setOnClickListener(this@NewCoachActivity)
            editPriceLayout.proceedLayout.setOnClickListener(this@NewCoachActivity)
            imgExpand.setOnClickListener(this@NewCoachActivity)
            imgCollapse.setOnClickListener(this@NewCoachActivity)
            tvEditSeats.setOnClickListener(this@NewCoachActivity)
            editPriceLayout.btnExtraBookingProceed.setOnClickListener(this@NewCoachActivity)
            editPriceLayout.imgRemoveSeat.setOnClickListener(this@NewCoachActivity)
            editPriceLayout.imgAddSeat.setOnClickListener(this@NewCoachActivity)
            editPriceLayout.tvEditPrice.setOnClickListener(this@NewCoachActivity)
            editPriceLayout.editFareIV.setOnClickListener(this@NewCoachActivity)
            changeBpDp.setOnClickListener(this@NewCoachActivity)
            includeHeader.moreIV.setOnClickListener(this@NewCoachActivity)
        }
        //New Booking Flow
        onSeatSelectionListener = this

        excludePassengerDetails =
            role == getString(R.string.role_agent) && countryName.equals("Indonesia", true)

        PreferenceUtils.removeKey(PREF_ENABLE_CAMPAIGN_PROMOTIONS)

        if (isApplyBPDPFare == "true") {
            callBpDpServiceApi(selectedBoarding?.id.toString(), selectedDropping?.id.toString())
        } else {
            when (role) {
                getString(R.string.role_field_officer) -> {
                    if (allowBookingForAllServices && isAllowToDoNextAndPreviousDatesServices) {
                        callServiceApi()
                        callServiceRoutesListApi(getDateYMD(travelDate))
                        getDates(travelDate)                             // Added calendar because of regression issue App-I55 in IOS it is showing for field officer
                    } else {
                        if (isApplyBPDPFare == "true") {
                            callBpDpServiceApi(
                                selectedBoarding?.id.toString(), selectedDropping?.id.toString()
                            )
                        } else {
                            callServiceApi()
                        }
                    }
                }

                else -> {
                    if (isAllowToDoNextAndPreviousDatesServices) {
                        callServiceApi()
                        callServiceRoutesListApi(getDateYMD(travelDate))
                        getDates(travelDate)
                    } else {
                        if (isApplyBPDPFare == "true") {
                            callBpDpServiceApi(
                                selectedBoarding?.id.toString(), selectedDropping?.id.toString()
                            )
                        } else {
                            callServiceApi()
                        }
                    }
                }
            }
        }

        setObserver()
        moveToNormalSeatObserver()
        unblockObserver()
        setTicketDetailsV1Observer()
        setUpSeatWisePerSeatObserver()
        releaseTicketFun()
        updateBoardedStatusObserver()
        setSendSMSEmailObserver()
        setQuotaBlockingTooltipInfoObserver()
        setServiceRoutesListObserver()
        setConfirmOtpReleaseObserver()
        moveToExtraSeatObserver()
        singleBlockUnblockObserver()
        setReleaseTicketObserver(bottomSheetDialoge)

        setAdapter()



        baseUpdateCancelTicket =
            supportFragmentManager.findFragmentById(R.id.layoutUpdateTicketContainer) as BaseUpdateCancelTicket

        cancelTicketSheet = supportFragmentManager.findFragmentById(R.id.layoutCancelTicketSheet) as CancelTicketSheet

        if (privilegeResponseModel?.country.equals("India", true)) {
            binding.modifySearchLayout.dateLayout.visible()
        } else {
            binding.modifySearchLayout.dateLayout.gone()
        }
    }

    private fun callQuotaBlockingTooltipInfoApi(seatNo: String) {

        if (this.isNetworkAvailable()) {
            blockViewModel.getQuotaBlockingTooltipInfo(
                apiKey = loginModelPref.api_key,
                reservationId = reservationId.toString(),
                seatNumber = seatNo,
                locale = locale ?: "en",
                apiType = quota_blocking_information
            )
        } else this.noNetworkToast()

    }

    private fun setQuotaBlockingTooltipInfoObserver() {

        blockViewModel.quotaBlockingTooltipInfoResponse.observe(this) {
            //Timber.d("quotaBlockingTooltipInfoResponse${it}")
            //   binding.includeProgressCoach.gone()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        callCoach = true
                        if (it.result != null) {
                            DialogUtils.dialogQuotaBlockTooltipInfo(
                                context = this,
//                                seatNumber = "${it.result.blockedSeats.substringBefore(",")} is blocked",
                                quotaTypeValue = it.result.quotaType,
                                quotaForValue = it.result.quotaFor,
                                blockingNoValue = it.result.blockingNo,
                                seatNosValue = it.result.blockedSeats,
                                remarksValue = it.result.remarks,
                                blockedByValue = it.result.blockedBy,
                                blockedOnValue = it.result.blockedOn,
                                genderOnValue = it.result.gender,
                                privilegeResponseModel
                            )
                        } else {
                            toast(getString(R.string.something_went_wrong))
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
                        it.message?.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun moveToExtraSeatObserver() {
        moveToExtraSeatViewModel.moveToExtraSeat.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        it.message?.let { it1 ->
                            DialogUtils.successfulMsgDialog(
                                this, it1
                            )
                        }
                        callCoach = true
                        callServiceApi()
                        closeSeatDetailToggle()
                        //onSeatSelectionListener.moveExtraSeat(true)
                    }

                    413 -> {

                        if (it.message != null) {
                            it.message.let { it1 -> this.toast(it1) }
                        }

                        if (privilegeResponseModel?.allowToMoveSpecificSeatsRelatedToAPnr == true) {
                            multiSelectMoveToExtra.showMultiExtraSeatSelectionDialog(
                                context = this,
                                title = context.resources.getString(R.string.move_to_extra_seat),
                                message = "${context.resources.getString(R.string.selectedSeatNo)} : " + if (seatPassengersList.isNotEmpty()) seatPassengersList[lastSelectedSeatPosition].seat_no else "",
                                buttonLeftText = context.resources.getString(R.string.cancel),
                                buttonRightText = context.resources.getString(R.string.move),
                                seatNumber = if (seatPassengersList.isNotEmpty()) seatPassengersList[lastSelectedSeatPosition].seat_no else "",
                                extraSeatNumber = "",
                                isEditable = false,
                                neededSeatNumbers = needed_seat_numbers,
                                dialogButtonMoveSeatExtraListener = object : DialogButtonMoveSeatExtraListener {
                                    override fun onLeftButtonClick(string: String?) {
                                    }

                                    override fun onRightButtonClick(
                                        remarks: String,
                                        seatNo: String,
                                        extraSeatNo: String,
                                        sms: Boolean
                                    ) {
                                        var ticketNumber =
                                            if (seatPassengersList.isNotEmpty()) seatPassengersList[lastSelectedSeatPosition].ticket_no else ""
                                        if (ticketNumber.contains("(")) ticketNumber =
                                            ticketNumber.substringBefore("(").trim()

                                        if(shouldTicketMoveToSeatExtraSeat && countryName.equals("india", true)) {
                                            DialogUtils.showFullHeightPinInputBottomSheet(
                                                activity = this@NewCoachActivity,
                                                fragmentManager = supportFragmentManager,
                                                pinSize,
                                                getString(R.string.move_to_extra_seat),
                                                onPinSubmitted = { pin: String ->
                                                    callMoveToExtraSeatApi(
                                                        sms = sms,
                                                        remarks = remarks,
                                                        resID = PreferenceUtils.getPreference(
                                                            PREF_RESERVATION_ID,
                                                            0L
                                                        )!!.toString(),
                                                        seatNo = seatNo,
                                                        extraSeatNo = extraSeatNo,
                                                        ticketNo = ticketNumber,
                                                        authPin = pin
                                                    )
                                                },
                                                onDismiss = null
                                            )
                                        }
                                        else {
                                            callMoveToExtraSeatApi(
                                                sms = sms,
                                                remarks = remarks,
                                                resID = PreferenceUtils.getPreference(
                                                    PREF_RESERVATION_ID,
                                                    0L
                                                )!!.toString(),
                                                seatNo = seatNo,
                                                extraSeatNo = extraSeatNo,
                                                ticketNo = ticketNumber,
                                                authPin = ""
                                            )
                                        }
                                    }
                                }
                            )
                        } else {

                        DialogUtils.moveToExtraSeatDialog(
                            context,
                            context.resources.getString(R.string.move_to_extra_seat),
                            "${context.resources.getString(R.string.selectedSeatNo)} : " + if (seatPassengersList.isNotEmpty()) seatPassengersList[lastSelectedSeatPosition].seat_no else "",
                            context.resources.getString(R.string.cancel),
                            context.resources.getString(R.string.move),
                            if (seatPassengersList.isNotEmpty()) seatPassengersList[lastSelectedSeatPosition].seat_no else "",
                            "",
                            object : DialogButtonMoveSeatExtraListener {
                                override fun onLeftButtonClick(string: String?) {

                                }

                                override fun onRightButtonClick(
                                    remarks: String,
                                    seatNo: String,
                                    extraSeatNo: String,
                                    sms: Boolean
                                ) {
                                    var ticketNumber =
                                        if (seatPassengersList.isNotEmpty()) seatPassengersList[lastSelectedSeatPosition].ticket_no else ""
                                    if (ticketNumber.contains("(")) ticketNumber =
                                        ticketNumber.substringBefore("(").trim()

                                    if(shouldTicketMoveToSeatExtraSeat && countryName.equals("india", true)) {
                                        DialogUtils.showFullHeightPinInputBottomSheet(
                                            activity = this@NewCoachActivity,
                                            fragmentManager = supportFragmentManager,
                                            pinSize,
                                            getString(R.string.move_to_extra_seat),
                                            onPinSubmitted = { pin: String ->
                                                callMoveToExtraSeatApi(
                                                    sms = true,
                                                    remarks = remarks,
                                                    resID = PreferenceUtils.getPreference(
                                                        PREF_RESERVATION_ID,
                                                        0L
                                                    )!!.toString(),
                                                    seatNo = seatNo,
                                                    extraSeatNo = extraSeatNo,
                                                    ticketNo = ticketNumber,
                                                    authPin = pin
                                                )
                                            },
                                            onDismiss = null
                                        )
                                    }
                                    else {
                                        callMoveToExtraSeatApi(
                                            sms = true,
                                            remarks = remarks,
                                            resID = PreferenceUtils.getPreference(
                                                PREF_RESERVATION_ID,
                                                0L
                                            )!!.toString(),
                                            seatNo = seatNo,
                                            extraSeatNo = extraSeatNo,
                                            ticketNo = ticketNumber,
                                            authPin = ""
                                        )
                                    }
                                }

                            },
                        true
                        )
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
                        it.message?.let { it1 -> this.toast(it1) }
                    }
                }
            } else {
                this.toast(getString(R.string.server_error))
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun moveToNormalSeatObserver() {
        moveToExtraSeatViewModel.moveToNormalSeat.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        it.message?.let { it1 ->
                            DialogUtils.successfulMsgDialog(
                                this, it1
                            )
                        }
                        callCoach = true
                        callServiceApi()
                        //onSeatSelectionListener.moveExtraSeat(true)
                    }

                    413 -> {

                        if (it.message != null) {
                            it.message.let { it1 -> this.toast(it1) }
                        }
                        DialogUtils.moveToExtraSeatDialog(
                            context,
                            context.resources.getString(R.string.move_to_extra_seat),
                            "${context.resources.getString(R.string.selectedSeatNo)} : " + if (seatPassengersList.isNotEmpty()) seatPassengersList[lastSelectedSeatPosition].seat_no else "",
                            context.resources.getString(R.string.cancel),
                            context.resources.getString(R.string.move),
                            if (seatPassengersList.isNotEmpty()) seatPassengersList[lastSelectedSeatPosition].seat_no else "",
                            "",
                            object : DialogButtonMoveSeatExtraListener {
                                override fun onLeftButtonClick(string: String?) {

                                }

                                override fun onRightButtonClick(
                                    remarks: String,
                                    seatNo: String,
                                    extraSeatNo: String,
                                    sms: Boolean
                                ) {
                                    var ticketNumber =
                                        if (seatPassengersList.isNotEmpty()) seatPassengersList[lastSelectedSeatPosition].ticket_no else ""
                                    if (ticketNumber.contains("(")) ticketNumber =
                                        ticketNumber.substringBefore("(").trim()

                                    if(shouldTicketMoveToSeatExtraSeat && countryName.equals("india", true)) {
                                        DialogUtils.showFullHeightPinInputBottomSheet(
                                            activity = this@NewCoachActivity,
                                            fragmentManager = supportFragmentManager,
                                            pinSize = pinSize,
                                            getString(R.string.move_to_normal_seat),
                                            onPinSubmitted = { pin: String ->
                                                callMoveToExtraSeatApi(
                                                    sms = true,
                                                    remarks = remarks,
                                                    resID = PreferenceUtils.getPreference(
                                                        PREF_RESERVATION_ID,
                                                        0L
                                                    )!!.toString(),
                                                    seatNo = seatNo,
                                                    extraSeatNo = extraSeatNo,
                                                    ticketNo = ticketNumber,
                                                    authPin = pin
                                                )
                                            },
                                            onDismiss = null
                                        )
                                    }
                                    else {
                                        callMoveToExtraSeatApi(
                                            sms = true,
                                            remarks = remarks,
                                            resID = PreferenceUtils.getPreference(
                                                PREF_RESERVATION_ID,
                                                0L
                                            )!!.toString(),
                                            seatNo = seatNo,
                                            extraSeatNo = extraSeatNo,
                                            ticketNo = ticketNumber,
                                            authPin = ""
                                        )
                                    }
                                }

                            },
                            true
                        )
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
                        if (it.message.equals("Selected seat is successfully shifted!")) {

                            callCoach = true
                            callServiceApi()
                            closeSeatDetailToggle()
                            DialogUtils.successfulMsgDialog(
                                this, it.message ?: ""
                            )
                        } else {
                            it.message?.let { it1 -> this.toast(it1) }
                        }
                    }
                }
            } else {
                this.toast(getString(R.string.server_error))
            }
        }

    }

    private fun singleBlockUnblockObserver() {
        availableRoutesViewModel.dataSingleBLockUnblock.observe(this) {
            it
            //  stopShimmerEffect()
            if (it != null) {
                if (it.code == 200) {
                    if (it?.message != null)
                        toast(it?.message)

                    callServiceApi()
                } else if (it.code == 401) {
                    DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )
                    if (it?.message != null)
                        toast(it?.message)
                }

            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun getPref() {
        loginModelPref = getLogin()
        // lifecycleScope.launch {
        privilegeResponseModel = getPrivilegeBase()

        privilegeResponseModel?.let {
            if (privilegeResponseModel?.allowUpdateDetailsOptionInReservationChart == true) {
//                    binding.layoutSummary.layoutServiceDetails.visible()
            } else {
                binding.layoutSummary.layoutServiceDetails.gone()
            }

            phoneBlockingRelease =
                privilegeResponseModel?.pinBasedActionPrivileges?.phoneBlockingRelease ?: false


            if (privilegeResponseModel?.currency?.isNotEmpty() == true) {
                currencySymbol = privilegeResponseModel?.currency ?: ""
            }

            isIndonesiaLogin = privilegeResponseModel?.country.equals("Indonesia", true)

            isAgentLogin = privilegeResponseModel?.isAgentLogin ?: false

        }


        if (::privilegeResponseModel != null) {
            if (privilegeResponseModel?.isPermanentPhoneBooking != null) {
                isPermanentPhoneBooking = privilegeResponseModel?.isPermanentPhoneBooking ?: false
            }
            if (privilegeResponseModel?.removePreSelectionOptionInTheBooking != null) {
                removePreSelectionOptionInTheBooking =
                    privilegeResponseModel?.removePreSelectionOptionInTheBooking ?: false
            }
            if (privilegeResponseModel?.phoneBlockReleaseTime != null) {
                phoneBlockReleaseTime = privilegeResponseModel?.phoneBlockReleaseTime!!
            }
            if (privilegeResponseModel?.releaseTimePoliciesOptions != null) {
                releaseTimePoliciesOptions = privilegeResponseModel?.releaseTimePoliciesOptions!!
            }
        }

        if (privilegeResponseModel != null) {
            currency = privilegeResponseModel?.currency ?: ""
            currencyFormat =
                getCurrencyFormat(this@NewCoachActivity, privilegeResponseModel?.currencyFormat)

            role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, this@NewCoachActivity)
            allowBookingForAllotedServices =
                privilegeResponseModel?.allowBookingForAllotedServices ?: false

            if (role == getString(R.string.role_field_officer)) {
                updatePassengerTravelStatus =
                    privilegeResponseModel?.boLicenses?.updatePassengerTravelStatus == true
                isAllowOnlyOnce =
                    privilegeResponseModel?.boLicenses?.allowUserToBoardingStatusOnlyOnce ?: false
                allowBookingForAllServices =
                    privilegeResponseModel?.boLicenses?.allowBookingForAllServices ?: false

            } else {
                updatePassengerTravelStatus =
                    privilegeResponseModel?.updatePassengerTravelStatus == true
                isAllowOnlyOnce =
                    privilegeResponseModel?.availableAppModes?.allow_user_to_change_the_the_boarding_status_only_once
                        ?: false
            }
        }

        if (privilegeResponseModel?.country != null) countryName =
            privilegeResponseModel?.country ?: ""

        if (privilegeResponseModel?.isAllowQuickBookingsForTSMobileApp != null) {
            isQuickBookingsForTSApp = privilegeResponseModel?.isAllowQuickBookingsForTSMobileApp
        }


        if (privilegeResponseModel?.bulkUpdationOfTickets != null) {
            bulkUpdationOfTickets = privilegeResponseModel?.bulkUpdationOfTickets!!
        }
        if (privilegeResponseModel?.availableAppModes?.allowBpDpFare != null) {
            isAllowBpDpFare = privilegeResponseModel?.availableAppModes?.allowBpDpFare!!
        }
        if (privilegeResponseModel?.isEditReservation != null) {
            isEditReservation = privilegeResponseModel?.isEditReservation
        }

        if (privilegeResponseModel?.allowToExtendFareForServices != null) {
            allowToExtendFareForServices =
                privilegeResponseModel?.allowToExtendFareForServices ?: false
        }
        if (privilegeResponseModel?.allowRapidBookingFlow != null) {
            isAllowRapidBookingFlow = privilegeResponseModel?.allowRapidBookingFlow!!
        }

        isFromChile = privilegeResponseModel?.isChileApp == true


        if (privilegeResponseModel?.showViewChartLinkInTheSearchResults != null) {
            showViewChartLinkInTheSearchResults =
                privilegeResponseModel?.showViewChartLinkInTheSearchResults ?: false
        }

        if (privilegeResponseModel?.allowToDoNextAndPreviousDatesServices != null) {
            isAllowToDoNextAndPreviousDatesServices =
                privilegeResponseModel?.allowToDoNextAndPreviousDatesServices ?: false
        }

        if (privilegeResponseModel?.pinCount != null) {
            pinSize =
                privilegeResponseModel?.pinCount ?: 6
        }

        if(privilegeResponseModel?.tsPrivileges?.editFareMandatoryForAgentUser != null) {
            editFareMandatoryForAgentUser = privilegeResponseModel?.tsPrivileges?.editFareMandatoryForAgentUser ?: false
        }

        if (privilegeResponseModel?.pinBasedActionPrivileges?.ticketMoveToSeatExtraSeat != null) {
            shouldTicketMoveToSeatExtraSeat =
                privilegeResponseModel?.pinBasedActionPrivileges?.ticketMoveToSeatExtraSeat ?: false
        }

        if (privilegeResponseModel?.pinBasedActionPrivileges?.phoneBlockingRelease != null) {
            shouldPhoneBlockingRelease =
                privilegeResponseModel?.pinBasedActionPrivileges?.phoneBlockingRelease ?: false
        }

        if (privilegeResponseModel?.pinBasedActionPrivileges?.singlePageBlockUnblock != null) {
            shouldSingleBlockUnblock = privilegeResponseModel?.pinBasedActionPrivileges?.singlePageBlockUnblock ?: false
        }

        addCoachOptions()

        isApplyBPDPFare = PreferenceUtils.getObject<String>(IS_APPLY_BP_DP_FARE).toString()

        if (isApplyBPDPFare == "true" && PreferenceUtils.getObject<BoardingPointDetail>(
                SELECTED_BOARDING_DETAIL
            ) != null && PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL) != null
        ) {
            val originDestinationCity = PreferenceUtils.getString("OriginDestinationCity") ?: ""
            selectedBoarding = PreferenceUtils.getObject(SELECTED_BOARDING_DETAIL)!!
            selectedDropping = PreferenceUtils.getObject(SELECTED_DROPPING_DETAIL)!!
            val isFromAgent = intent.getBooleanExtra(IS_FROM_AGENT, false)
            if (isFromAgent) {
                tempSourceName = source as String
                tempDestinationIdName = destination as String
            } else {
                tempSourceName = "${selectedBoarding?.name ?: ""}, $source"
                tempDestinationIdName = "${selectedDropping?.name ?: ""}, $destination"
            }
        }

        if (isApplyBPDPFare == "true" || (isAgentLogin && allowBookingForAllotedServices)) {
            binding.includeHeader.headerLL.gone()
        }
        //}

        bccId = getBccId()

        source = PreferenceUtils.getSource()
        sourceId = getSourceId()
        destinationId = getDestinationId()
        destination = PreferenceUtils.getDestination()
        tempSourceName = source ?: ""
        tempSourceId = sourceId
        tempDestinationIdName = destination ?: ""
        tempDestinationId = destinationId
        travelDate = PreferenceUtils.getTravelDate()
        ymdDate = getDateYMD(travelDate.replace("/", "-"))

        locale = PreferenceUtils.getlang()
        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }

        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null) {
            reservationId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!
            resId = reservationId
        }

        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            deptTime = result?.dep_time ?: getString(R.string.dash)
            busType = result?.bus_type ?: getString(R.string.empty)
            serviceNumber = result?.number ?: getString(R.string.empty)
        }
        try {
            if (getCountryCodes().isNotEmpty()) countryList = getCountryCodes()
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }

        isServiceBlocked =
            PreferenceUtils.getPreference(PREF_AVAILABLE_ROUTES_ITEM_IS_SERVICE_BLOCKED, false)
                ?: false
        isPickupDropoffChargesEnabled = PreferenceUtils.getPreference(PREF_PICKUP_DROPOFF_CHARGES_ENABLED, false) ?: false
    }

    private fun initNextPreviousDate() {

        binding.editPriceLayout.arrowCoachBackImg.setTextColor(
            ContextCompat.getColor(
                this@NewCoachActivity, R.color.colorDimShadow6
            )
        )
        swipePosition = 0
        isNextSwipeClick = false
        isPreviousSwipeClick = false
        reservationIdSwipe = ""

        binding.apply {
            if (!availableRoutesList.isNullOrEmpty()) {
                for (i in 0 until availableRoutesList.size) {
                    reservationIdList.add(i, availableRoutesList[i].reservation_id.toString())
                }
            }
            if (reservationIdList.isNotEmpty()) {
                if (srpServiceSelectionPos > 0 && srpServiceSelectionPos < reservationIdList.size) {
                    reservationId = reservationIdList[srpServiceSelectionPos].toLong()
                    swipePosition = srpServiceSelectionPos
                } else {
                    reservationId = reservationIdList[0].toLong()
                }
            }

            if (!reservationIdList.isNullOrEmpty()) {
                val lastIndex = reservationIdList.size - 1
                val enabledColor = ContextCompat.getColor(this@NewCoachActivity, R.color.colorPrimary)
                val disabledColor = ContextCompat.getColor(this@NewCoachActivity, R.color.colorDimShadow6)

                val pos = if (swipePosition < 0) 0 else swipePosition

                val isAtStart = pos <= 0
                val isAtEnd = pos >= lastIndex

                editPriceLayout.arrowCoachBackImg.apply {
                    isEnabled = !isAtStart
                    isClickable = !isAtStart
                    setTextColor(if (!isAtStart) enabledColor else disabledColor)
                }

                editPriceLayout.arrowCoachNextImg.apply {
                    isEnabled = !isAtEnd
                    isClickable = !isAtEnd
                    setTextColor(if (!isAtEnd) enabledColor else disabledColor)
                }
            }

            if (!availableRoutesList.isNullOrEmpty() && swipePosition == availableRoutesList.size - 1) {
                editPriceLayout.arrowCoachNextImg.setTextColor(
                    ContextCompat.getColor(
                        this@NewCoachActivity, R.color.colorDimShadow6
                    )
                )
            }

            iterator = reservationIdList.listIterator()

            if (!availableRoutesList.isNullOrEmpty()) {
                for (i in 0 until availableRoutesList.size) {
                        if (reservationId == availableRoutesList[i].reservation_id) {
                            swipePosition = i
                            isNextSwipeClick = true
                            for (x in 0 until swipePosition + 1) {
                                if (iterator.hasNext())
                                    iterator.next()
                            }
                        }

                }
            }

            coachSwipeButtonsVisibility()
            editPriceLayout.proceedLayout.gone()
            editPriceLayout.editprice.gone()
            editPriceLayout.fabsummary.gone()
            editPriceLayout.layoutExtraSeatProceed.gone()
            srpServiceSelectionPos = 0
        }
    }


    private fun addCoachOptions() {
        coachOptionsArray.clear()


//        if (bulkUpdationOfTickets && !serviceDetails?.body.is_service_blocked && !isAllowBpDpFare) {
        if (privilegeResponseModel?.bulkUpdationOfTickets == true && !isServiceBlocked) {
            if (privilegeResponseModel?.availableAppModes?.allowBpDpFare == false) {
                coachOptionsArray.add(
                    CoachOptionsModel(
                        coachOption = getString(R.string.edit_chart_option),
                        coachOptionIcon = ContextCompat.getDrawable(
                            this, R.drawable.edit_chart_button_new_booking_flow
                        )
                    )
                )
            }
        }


        Timber.d("showViewChartLinkInTheSearchResults $showViewChartLinkInTheSearchResults  isAgentLogin $isAgentLogin")
        if (showViewChartLinkInTheSearchResults && !isAgentLogin) {
            coachOptionsArray.add(
                CoachOptionsModel(
                    coachOption = getString(R.string.view_reservation_chart),
                    coachOptionIcon = ContextCompat.getDrawable(
                        this, R.drawable.view_reservation_chart_icon
                    )
                )
            )
        } else {
            // Pickup Chart GONE
        }

        coachOptionsArray.add(
            CoachOptionsModel(
                coachOption = getString(R.string.bus_location_option),
                coachOptionIcon = ContextCompat.getDrawable(this, R.drawable.current_location)
            )
        )

        if (privilegeResponseModel?.notifyOption == true) {
            coachOptionsArray.add(
                CoachOptionsModel(
                    coachOption = getString(R.string.sms_notification_option),
                    coachOptionIcon = ContextCompat.getDrawable(
                        this, R.drawable.sms_notification_button_new_booking_flow
                    )
                )
            )
        }

        if (privilegeResponseModel?.allowToShowFrequentTravellerTag == true) {
            coachOptionsArray.add(
                CoachOptionsModel(
                    coachOption = getString(R.string.frequent_traveller),
                    coachOptionIcon = ContextCompat.getDrawable(
                        this, R.drawable.ic_frequent_traveller
                    )
                )
            )
        }

        if ((loginModelPref.role.equals(context.getString(R.string.role_field_officer), true)
            && privilegeResponseModel?.boLicenses?.allowToUpdateVehicleExpenses == true)
            || (privilegeResponseModel?.allowUpdateDetailsOptionInReservationChart == true))
        {
            coachOptionsArray.add(
                CoachOptionsModel(
                    coachOption = getString(R.string.update_details_option),
                    coachOptionIcon = ContextCompat.getDrawable(
                        this, R.drawable.update_service_details
                    )
                )
            )
        }


        if (loginModelPref.role == getString(R.string.role_field_officer)) {
            if (privilegeResponseModel?.country.equals(
                    "India", true
                ) && privilegeResponseModel?.boLicenses?.showBookingAndCollectionTabInTsApp == true && (serviceDetails?.body?.isBima == null || serviceDetails?.body?.isBima == false)) {

//                Timber.d("check_BimaService = $isBimaServiceDetails")

                if (isBimaServiceDetails == null || isBimaServiceDetails == false) {
                    coachOptionsArray.add(
                        CoachOptionsModel(
                            coachOption = getString(R.string.booking_option),
                            coachOptionIcon = ContextCompat.getDrawable(
                                this,
                                R.drawable.booked_by_button_new_booking_flow
                            )
                        )
                    )

                }

            }

        }
    }




    private fun setObserver() {
        cityDetailViewModel.multistationSeatData.observe(this) {
            it?.let { response ->
                when (response.code) {
                    200 -> {
                        needed_seat_numbers.clear()

                        response.passenger_details?.forEach { passenger ->
                            passenger.seat_numbers?.let { seatNumbers ->
                                needed_seat_numbers.addAll(seatNumbers.split(",").map { it.trim() }.filter { !it.startsWith("Ex-") }.filter { it.isNotEmpty() })
                            }
                        }

                        // Ensure passenger_details is not null and not empty before checking
                        if (isSwitchClicked && !response.passenger_details.isNullOrEmpty()) {
                            getBookedSeatOptions(response.passenger_details, false)
                            isSwitchClicked = false
                        } else {
                            if (!response.passenger_details.isNullOrEmpty()) getBookedSeatOptions(response.passenger_details)
                        }

                        // Check if passenger_details is not null and contains data before accessing its first element
                        if (isPermanentPhoneBooking
                            && it.passenger_details.isNotEmpty() && it.passenger_details[0].is_temporary_phone_block == true
                            && it.passenger_details[0].is_phone_block
                            && isBimaServiceDetails == false
                        ) {
                            binding.layoutBookedSeatDetails.menuConvertPermanentPhoneBlock.visible()
                        } else {
                            binding.layoutBookedSeatDetails.menuConvertPermanentPhoneBlock.gone()
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
                        binding.apply {
                            btnServiceSummary.gone()
                            layoutPassengerDetailsSeatLegends.root.gone()
                            nestedScrollView.gone()
                            noData.root.visible()
                            noData.tvNoData.text = response.message ?: response.result.message
                                    ?: getString(R.string.no_data_available)
                            toast(noData.tvNoData.text.toString())
                        }
                    }
                }
            }
        }

        cityDetailViewModel.phoneBlockTempToPermanent.observe(this) {
            it?.let { response ->
                when (response.code) {
                    200 -> {
                        response.message?.let { toast(it) }
                        binding.coachProgressBar.visible()
                        callServiceApi()

                        val view: View = findViewById<View>(R.id.layout_coach_options) as ConstraintLayout

                        if (this::commonCoach.isInitialized) {
                            if (view.visibility == VISIBLE) {
                                closeToggle()
                            }
                            if (PreferenceUtils.getPreference(PREF_UPDATE_COACH, false) == true) {
//                                binding.editPriceLayout.root.gone()
                                binding.apply {
                                    if (availableRoutesList.size == 1) {
                                        binding.editPriceLayout.root.gone()
                                    } else {
                                        coachSwipeButtonsVisibility()
                                        binding.apply {
                                            editPriceLayout.proceedLayout.gone()
                                            editPriceLayout.editprice.gone()
                                            editPriceLayout.fabsummary.gone()
                                            editPriceLayout.layoutExtraSeatProceed.gone()
                                        }
                                    }
                                }

                                if (isApplyBPDPFare == "true") {
                                    callBpDpServiceApi(
                                        selectedBoarding?.id.toString(),
                                        selectedDropping?.id.toString()
                                    )
                                } else {
                                    callServiceApi()
                                }
                            }
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
                        response.message?.let { toast(it) }
                    }
                }
            }
        }

        sharedViewModel.serviceDetails.observe(this) { response ->
            handleServiceDetailsResponse(response)
        }

        sharedViewModel.serviceDetailsByRouteId.observe(this) { response ->
            binding.editPriceLayout.arrowCoachBackImg.gone()
            binding.editPriceLayout.arrowCoachNextImg.gone()
            binding.editPriceLayout.nextBackLayout.gone()
            handleServiceDetailsResponse(response)
        }

        sharedViewModel.dragDropRemarks.observe(this) { response ->
            if (response != null) {
                toast(response.message)
                hideSeatBookingDetailsLayout()
            } else {
                toast(getString(R.string.server_error))
            }

        }

        cityDetailViewModel.cityDetailResponse.observe(this) {
            binding.coachProgressBar.gone()
            it?.let { response ->
                if (response.code == 200) {
                    val cityList = response.result
                    saveCityList(cityList)

                    val intent = Intent(this, SearchActivity::class.java)
                    intent.putExtra(
                        getString(R.string.CITY_SELECTION_TYPE), getString(R.string.CITY_SELECTION)
                    )
                    startActivityForResult(intent, RESULT_CODE_SOURCE)
                } else {
                    response.message?.let { toast(it) }
                }
            } ?: run {
                toast(getString(R.string.server_error))
            }
        }
        setPickupChartCrewDetailsObserver()
        shortRouteCityPairObsever()
    }

    private fun getBpDpPointsLists(stageDetails: MutableList<StageDetail>?) {
        bpDpBoarding.clear()
        bpDpDropping.clear()

        if (!stageDetails.isNullOrEmpty()) {
            for (i in 0 until stageDetails.size) {
                when (stageDetails[i].type) {
                    0 -> {
                        bpDpBoarding.add(
                            BoardingPointDetail(
                                address = "",
                                id = stageDetails[i].id!!.toString(),
                                landmark = stageDetails[i].landmark ?: "",
                                name = stageDetails[i].name!!.toString(),
                                time = stageDetails[i].time!!.toString(),
                                distance = stageDetails[i].distance ?: ""
                            )
                        )
                    }

                    1 -> {
                        bpDpDropping.add(
                            DropOffDetail(
                                address = "",
                                id = stageDetails[i].id!!.toString(),
                                landmark = stageDetails[i].landmark ?: "",
                                name = stageDetails[i].name!!.toString(),
                                time = stageDetails[i].time!!.toString(),
                                distance =stageDetails[i].distance ?: ""
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setSeatLegendsAdapter(isBranchLegend: Boolean, legendsList: ArrayList<LegendDetail>) {
        val colorLegendName = arrayListOf<String>()
        val color1 = arrayListOf<String>()
        val color2 = arrayListOf<String>()

        for (i in 0 until legendsList.size) {
            if (legendsList[i].color?.contains(",") == true) {
                val color = legendsList[i].color?.split(",")
                if (isBranchLegend) {
                    colorLegendName.add(legendsList[i].branchColorLegend.toString())
                } else {
                    colorLegendName.add(legendsList[i].colorLegend.toString())
                }
                color1.add(color?.get(0) ?: "")
                color2.add(color?.get(1) ?: "")

            } else {
                if (isBranchLegend) {
                    colorLegendName.add(legendsList[i].branchColorLegend.toString())
                } else {
                    colorLegendName.add(legendsList[i].colorLegend.toString())
                }
                color1.add(legendsList[i].color ?: "")
                color2.add(legendsList[i].color ?: "")
            }
        }
        if (isBranchLegend) {
            branchLegendsAdapter.setList(colorLegendName, color1, color2)
        } else {
            seatLegendsAdapter.setList(colorLegendName, color1, color2)
        }
    }

    private fun updateBoardedStatusObserver() {
        pickUpChartViewModel.updateBoardedStatusResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        callCoach = true
                        PreferenceUtils.setPreference(
                            "pickUpChartStatus", "${"2"}"
                        )

                        bottomSheetDialogStatus.dismiss()
                        if (isApplyBPDPFare == "true") {
                            callBpDpServiceApi(
                                selectedBoarding?.id.toString(),
                                selectedDropping?.id.toString()
                            )
                        } else {
                            callServiceApi()
                        }

                        if (::switch.isInitialized) {
                            switch.isChecked = it.status == "2"
                        }
                        toast(getString(R.string.update_passenger_status))

                        isSwitchClicked = true
                        hitMultistationSeatDetailApi(resId.toString(), lastSelectedSeatNumber)


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
                        toast(it.result.message)
                    }
                }
            } else {
                this.toast(getString(R.string.server_error))
            }
        }
    }

    private fun initRecyclerView() {
        binding.layoutPassengerDetailsSeatLegends.rvPassengersDetailsSeats.layoutManager =
            GridLayoutManager(this, 5)
        binding.layoutPassengerDetailsSeatLegends.rvPassengersDetailsSeats.adapter =
            SeatLegendsAdapter()
        seatLegendsAdapter = SeatLegendsAdapter()
        binding.layoutPassengerDetailsSeatLegends.rvPassengersDetailsSeats.adapter =
            seatLegendsAdapter

        binding.layoutPassengerDetailsSeatLegends.rvBranchLegendColors.layoutManager = GridLayoutManager(this, 5)
        branchLegendsAdapter = SeatLegendsAdapter()
        binding.layoutPassengerDetailsSeatLegends.rvBranchLegendColors.adapter = branchLegendsAdapter
    }

    @SuppressLint("ResourceType")
    private fun setAdapter() {
        val convertedList = coachOptionsArray.toSet().toList()
        val uniqueList = ArrayList(convertedList)

        binding.layoutCoachOptions.coachOptionsRv.adapter =
            CoachOptionsAdapter(context, this, uniqueList,privilegeResponseModel)
    }


    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_coach_options, fragment)
            .commit()
    }

    private fun addFragmentNew(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_coach_options, fragment) // Replace with your container ID
            .commit()
    }

    override fun initUI() {
        binding = LayoutBookingNewCoachActivityBinding.inflate(layoutInflater)
        val view = binding.root

        previousScreen =
            if (intent.hasExtra(REDIRECT_FROM)) intent.getStringExtra(REDIRECT_FROM) else null

        srpServiceSelectionPos =
            if (intent.hasExtra(getString(R.string.srp_service_selection_pos))) intent.getIntExtra(
                getString(R.string.srp_service_selection_pos),
                0
            ) else 0

        context = this@NewCoachActivity
//        bindingStatus = SheetBoardedCheckBinding.inflate(LayoutInflater.from(context))
        bindingStatus = SheetBoardedCheckBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialogStatus = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheetDialogStatus.setContentView(bindingStatus.root)


        bindingSheet = SheetReleaseTicketsBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialoge = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheetDialoge.setContentView(bindingSheet.root)

        bindingdialoge =
            DialogBookMenuBinding.inflate(LayoutInflater.from(context))


        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()
        setToolbarTitle()

        binding.includeHeader.etaTime.gone()
        binding.includeHeader.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.modifySearchLayout.busServiceRoot.gone()
        initRecyclerView()
        clickListener()

        binding.autoBoardingPoint.setEndIconOnClickListener {
//            selectBoardingPoint()
            selected()
        }

        binding.autoDroppingPoint.setEndIconOnClickListener {
//            selectDroppingPoint()
            selected()
        }

        notifyOptionsList = listOf(getString(R.string.ticket_details), getString(R.string.bus_info_sms),
            getString(R.string.crew_details), getString(R.string.boarding_details))

        if(countryName.equals("India", true) && privilegeResponseModel?.notifyOption == true) {
            binding.layoutBookedSeatDetails.notifyPassengerBtn.visible()
            binding.layoutBookedSeatDetails.notifyPassengerBtn.setOnClickListener {
                layoutNotifyPassengerOptionsBinding =
                    LayoutNotifyPassengerOptionBinding.inflate(LayoutInflater.from(this))

                val balloon = Balloon.Builder(this)
                    .setLayout(layoutNotifyPassengerOptionsBinding.root)
                    .setHeight(BalloonSizeSpec.WRAP)
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                    .setArrowSize(6)
                    .setArrowPosition(0.18f)
                    .setElevation(8)
                    .setCornerRadius(8f)
                    .setMarginRight(50)
                    .setBalloonAnimation(BalloonAnimation.NONE)
                    .setBackgroundColorResource(android.R.color.white)
                    .setLifecycleOwner(this)
                    .build()

                // Setting the recycler view for the notification passenger layout
                layoutNotifyPassengerOptionsBinding.notifyPassengerOptionRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                // Setting up the adapter
                notifyPassengerOptionsAdapter =
                    NotifyPassengerOptionsAdapter(this, notifyOptionsList ?: emptyList())
                    { selectedOption -> callSendSMSEmailApi(selectedOption) }
                layoutNotifyPassengerOptionsBinding.notifyPassengerOptionRV.adapter = notifyPassengerOptionsAdapter

                balloon.showAlignBottom(it)
            }
        } else {
            binding.layoutBookedSeatDetails.notifyPassengerBtn.gone()
        }

        lifecycleScope.launch {
            availableRoutesViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            ticketDetailsViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            blockViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            dashboardViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            moveToExtraSeatViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            bookingViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            cityDetailViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }


    private fun selected() {

        if (boardingPointList.size == 1 && droppingPointList.size == 1 && !isPickupDropoffChargesEnabled) {
            binding.tvSelectBoardingPoint.setText(boardingPointList[0].name.toString())
            binding.tvSelectDropingPoint.setText(droppingPointList[0].name.toString())
            navigateToPassengerPaymentActivity()
        } else if (boardingPointList.size > 1 && droppingPointList.size == 1) {
            binding.tvSelectDropingPoint.setText(droppingPointList[0].name.toString())
            selectBoardingPoint()
        } else if (droppingPointList.size > 1 && boardingPointList.size == 1) {
            binding.tvSelectBoardingPoint.setText(boardingPointList[0].name.toString())
            selectBoardingPoint()
        } else {
            selectBoardingPoint()
        }
    }


    private fun setToolbarTitle() {

        val srcDest = "$tempSourceName-$tempDestinationIdName"

        if (deptTime.isNullOrEmpty()) {
            deptTime = PreferenceUtils.getPreference(PREF_DEPARTURE_TIME, "")
        }

        if (busType.isNullOrEmpty()) {
            busType = PreferenceUtils.getPreference(PREF_BUS_TYPE, "")
        }
        val subtitle = "$serviceNumber |${getDateDMMM(travelDate)} $deptTime | $busType"

        binding.includeHeader.toolbarHeaderText.text = srcDest
        binding.includeHeader.tvCurrentHeader.text = getString(R.string.branch_booking)
        binding.includeHeader.toolbarSubtitle.text = subtitle

        if (privilegeResponseModel?.allowBimaInTs == true) {
            binding.includeHeader.headerLL.gone()
        }
    }


    private fun callBpDpServiceApi(boarding: String, dropping: String) {
        binding.boardingDroppingLayout.gone()

        BpDpServiceDetailsRequest(
            bccId.toString(),
            service_details_method,
            format_type,
            BPDPReqBody(
                reservationId.toString(),
                loginModelPref.api_key,
                operator_api_key,
                locale,
                sourceId,
                destinationId,
                "true",
                boarding,
                dropping
            )
        )

        if (isNetworkAvailable()) {

            sharedViewModel.getBpDpServiceDetails(
                reservationId = reservationId.toString(),
                apiKey = loginModelPref.api_key,
                origin = sourceId, destinationId = destinationId,
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


     fun callServiceApi() {
         isServiceLoading = true
        if (intent.hasExtra("is_bima")) {
            isBimaServiceDetails = intent.getBooleanExtra("is_bima", false)
            Timber.d("checkBima - $isBimaServiceDetails")
        }

        if (isBimaServiceDetails == true) {
            binding.includeHeader.moreIV.gone()
        }

        ServiceDetailsRequest(
            bccId.toString(), service_details_method, format_type,
            ReqBody(
                id = reservationId.toString(),
                api_key = loginModelPref.api_key,
                operator_api_key = operator_api_key,
                locale = locale,
                origin_id = sourceId,
                destination_id = destinationId,
                app_bima_enabled = isBimaServiceDetails,
                json_format = "true"
            )
        )

        if (isNetworkAvailable()) {
            sharedViewModel.getServiceDetails(
                reservationId = reservationId.toString(),
                apiKey = loginModelPref.api_key,
                originId = sourceId,
                destinationId = destinationId,
                operatorApiKey = operator_api_key,
                locale = locale ?: "",
                apiType = service_details_method,
                appBimaEnabled = isBimaServiceDetails,
                excludePassengerDetails = excludePassengerDetails
            )
        } else {
            noNetworkToast()
        }
    }

    private fun callServiceRoutesListApi(travelDateX: String) {
        if (intent.hasExtra("is_bima")) {
            isBimaServiceDetails = intent.getBooleanExtra("is_bima", false)
            Timber.d("checkBima - $isBimaServiceDetails")
        }
        binding.coachProgressBar.visible()

        var isCsShared: Boolean? = null
        if (isBimaServiceDetails == true) {
            isCsShared = true
        }
        var serviceRouteSourceId="-1"
        var serviceRouteDestinationId="-1"
        if(PreferenceUtils.getPreference("isAllToAllFlow",false)==true){
            serviceRouteSourceId=PreferenceUtils.getPreference("serviceRouteOriginId","-1").toString()
            serviceRouteDestinationId=PreferenceUtils.getPreference("serviceRouteDestinationId","-1").toString()

            if(serviceRouteSourceId=="0"){
                serviceRouteSourceId="-1"
            }
            if(serviceRouteDestinationId=="0"){
                serviceRouteDestinationId="-1"
            }

        }else{
            serviceRouteSourceId=sourceId
            serviceRouteDestinationId=destinationId
        }

        if (isBimaServiceDetails == false) {
            if (isNetworkAvailable()) {
                availableRoutesViewModel.serviceRoutesListApi(
                    apiKey = loginModelPref.api_key,
                    originId = serviceRouteSourceId,
                    destinationId = serviceRouteDestinationId,
                    showInJourneyServices = showInJourneyServices,
                    isCsShared = isCsShared ?: false,
                    operatorkey = operator_api_key,
                    responseFormat = format_type,
                    travelDate = travelDateX,
                    showOnlyAvalServices = showOnlyAvailableServices,
                    locale = locale ?: "",
                    apiType = service_routes_list
                )
            } else
                noNetworkToast()
        }
    }


    private fun setServiceRoutesListObserver() {

        availableRoutesViewModel.serviceRoutesList.observe(this) {
            PreferenceUtils.removeKey(PREF_AVAILABLE_ROUTES_ITEM_IS_SERVICE_BLOCKED)
            if (reservationIdList!=null){
                reservationIdList.clear()
            }
            if (availableRoutesList != null) {
                availableRoutesList.clear()
            }

            if (it != null) {
                if (it.code == 200) {
                    availableRoutesList = it.result

                    initNextPreviousDate()

                    if (it.result.isNullOrEmpty()) {
                        binding.noData.root.visible()
                        binding.noData.tvNoData.visible()
                        binding.noData.tvNoData.text = "${it.message}"
                    } else {
                        binding.noData.root.gone()
                        binding.noData.tvNoData.gone()
                    }

                    if (isDateSelected) {
                        if (isApplyBPDPFare == "true") {
                            callBpDpServiceApi(
                                selectedBoarding?.id.toString(),
                                selectedDropping?.id.toString()
                            )
                        } else {
                            callServiceApi()
                        }
                    }

                } else if (it.code == 401) {
                    /* DialogUtils.unAuthorizedDialog(
                         this,
                         "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                         this
                     )*/
                    showUnauthorisedDialog()

                } else if (it.code == 404) {
                    binding.noData.root.visible()
                    binding.noData.tvNoData.visible()
                    binding.noData.tvNoData.text = "${it.error}"
                } else {
                    binding.noData.root.visible()
                    binding.noData.tvNoData.visible()
                    binding.noData.tvNoData.text = "${it.message}"
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun callServiceDetailsByRouteIdApi() {

        if (isNetworkAvailable()) {

            if (travelDateModifyLayout.isNotEmpty()) {
                travelDate = getDateDMY(travelDateModifyLayout).toString()
            } else {
                travelDateModifyLayout = getDateYMD(travelDate)
                travelDate = getDateDMY(travelDateModifyLayout).toString()
            }

            sharedViewModel.getServiceDetailsByRouteId(
                routeId = mainOpId ?: "",
                apiKey = loginModelPref.api_key,
                originId = sourceId,
                destinationId = destinationId,
                travelDate = travelDateModifyLayout,
                operatorApiKey = operator_api_key,
                locale = locale ?: "en",
                apiType = service_details_method,
                excludePassengerDetails = excludePassengerDetails
            )
        } else {
            noNetworkToast()
        }

    }

    interface CoachDataInterface {
        fun setCoachData(serviceDetails: Body)
    }

    interface coachDialogData {
        fun dialogSwitch(isChecked: Boolean)
    }

    override fun onSeatSelection(
        selectedSeatDetails: ArrayList<SeatDetail>,
        finalSeatnumber: ArrayList<String?>,
        totalSum: Double,
        isAllSeatSelected: Boolean,
        isSeatLongPress: Boolean?
    ) {
//        Timber.d("selectedSeatDetails ${selectedSeatDetails.size}")

        if (isSeatLongPress == true) {
            // binding.includeProgressCoach.visible()
            callQuotaBlockingTooltipInfoApi(replaceBracketsString(finalSeatnumber.toString()))
        } else {
            setSelectSeats(selectedSeatDetails)
            setSelectSeatNumber(replaceBracketsString(finalSeatnumber.toString()))
            this.selectedSeatDetails = selectedSeatDetails
            this.editFareSeatDetails = selectedSeatDetails
            this.isAllSeatSelected = isAllSeatSelected
            this.totalSum = roundOffDecimal(totalSum) ?: 0.0
            finalSeatNumbers = finalSeatnumber

            if (finalSeatNumbers.isNotEmpty()) {
                if (isExtraSeatChecked) {
                    binding.apply {
                        editPriceLayout.root.visible()
                        editPriceLayout.proceedLayout.visible()
                        editPriceLayout.editprice.visible()
                        editPriceLayout.layoutExtraSeatProceed.visible()
                        editPriceLayout.btnExtraBookingProceed.gone()
                        editPriceLayout.nextBackLayout.gone()
                        btnServiceSummary.gone()
                        layoutSummary.root.gone()
                    }

                } else {
                    multiSeatBtnView(selectedSeatDetails)
                }

                isSelectedSeat = true
                binding.imgExpand.gone()
                binding.imgCollapse.gone()
                binding.editPriceLayout.tvSelectedSeats.text =
                    replaceBracketsString(finalSeatNumbers.toString())
                binding.editPriceLayout.tvUnblockSelectedSeats.text =
                    replaceBracketsString(finalSeatNumbers.toString())


                binding.editPriceLayout.tvProceed.text =
                    replaceBracketsString(finalSeatNumbers.toString())
                val totalFare = "${getString(R.string.netAmount)}: $currency ${
                    (roundOffDecimal(totalSum))?.convert(currencyFormat)
                }"
                val editFareIndex = selectedSeatDetails.indexOfFirst { it.editFare != null }
                if (editFareIndex != -1)
                    onEditFareChange()
                else
                    binding.editPriceLayout.ticketPrice.text = totalFare
                binding.editPriceLayout.fabsummary.setOnClickListener(this)
            } else {
                binding.imgCollapse.visible()
                binding.editPriceLayout.tvSelectedSeats.text = getString(R.string.empty)
                binding.editPriceLayout.tvUnblockSelectedSeats.text = getString(R.string.empty)
                binding.editPriceLayout.tvProceed.text = ""
//            binding.editPriceLayout.editpriceLayout.gone()
//            binding.layoutSummary.mainLayout.visible()
//            binding.btnServiceSummary.visible()
//            binding.layoutviews.visible()
                binding.editPriceLayout.fabsummary.setOnClickListener(this)
                onNoSeatSelection()
            }

        }
    }

    private fun multiSeatBtnView(selectedSeatDetails: ArrayList<SeatDetail>) {
        val role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, this)

        var btnBlock = true
        var btnUnblock = false
        var btnBook = privilegeResponseModel?.allowBooking == true
        if (role == getString(R.string.role_agent)) {
            btnBlock = canBlockSeat
            btnUnblock = canUnblockSeat

            binding.editPriceLayout.blockLL.gone()
            binding.editPriceLayout.unblockLL.gone()

            for (i in 0..selectedSeatDetails.size.minus(1)) {
                if (selectedSeatDetails[i].available == true) {
                    if (selectedSeatDetails[i].isBlocked == true) {
                        btnUnblock = true
                        btnBlock = false
                    } else {
                        btnBlock = true
                        btnUnblock = false
                        break
                    }
                } else {
                    if (selectedSeatDetails[i].isBlocked == true) {
                        btnUnblock = true
                        btnBlock = true
                    } else {
                        btnUnblock = false
                        btnBlock = true
                        break
                    }
                    btnBook = false
                }
            }
            bookUnblockBtnViewAgentLogin(btnBook, btnBlock, btnUnblock)

        } else {
            for (i in 0..selectedSeatDetails.size.minus(1)) {
                if (selectedSeatDetails[i].available == true) {



                                        if (selectedSeatDetails[i].isBlocked == true) {
                        btnUnblock = true
                        btnBlock = false
                        btnBook = true
                    } else {
                        btnBlock = true
                        btnBook = true
                        btnUnblock = false
                        break
                    }
                } else {
                    if (selectedSeatDetails[i].isBlocked == true) {
                        btnUnblock = true
                        btnBlock = true
                    } else {
                        btnUnblock = false
                        btnBlock = true
                        break
                    }
                    btnBook = false
                }
            }

            bookUnblockBtnView(btnBook, btnBlock, btnUnblock)
        }
    }

    private fun bookUnblockBtnView(book: Boolean, block: Boolean, unblock: Boolean) {
        binding.apply {
            editPriceLayout.root.visible()
            editPriceLayout.proceedLayout.visible()
            editPriceLayout.editprice.visible()
            btnServiceSummary.gone()
            layoutSummary.root.gone()
            editPriceLayout.layoutExtraSeatProceed.gone()
            editPriceLayout.btnExtraBookingProceed.visible()
            editPriceLayout.nextBackLayout.gone()
        }

        if (book)
            binding.editPriceLayout.bookLL.visible()
        else
            binding.editPriceLayout.bookLL.gone()

        if (block && privilegeResponseModel != null && privilegeResponseModel?.isCanBlockSeats == true && role != getString(
                R.string.role_field_officer
            )
        ) {
            if (isBimaServiceDetails != null && isBimaServiceDetails == true) {
                binding.editPriceLayout.blockLL.gone()
            } else {
                binding.editPriceLayout.blockLL.visible()
            }
        } else {
            binding.editPriceLayout.blockLL.gone()
        }


        if (unblock && privilegeResponseModel != null && privilegeResponseModel?.isCanUnblockSeats == true && role != getString(
                R.string.role_field_officer
            )
        ) {
            if (isBimaServiceDetails != null && isBimaServiceDetails == true) {
                binding.editPriceLayout.unblockLL.gone()
            } else {
                binding.editPriceLayout.unblockLL.visible()
            }
        } else
            binding.editPriceLayout.unblockLL.gone()
    }

    private fun bookUnblockBtnViewAgentLogin(book: Boolean, block: Boolean, unblock: Boolean) {

        binding.apply {
            editPriceLayout.root.visible()
            editPriceLayout.proceedLayout.visible()
            editPriceLayout.editprice.visible()
            btnServiceSummary.gone()
            layoutSummary.root.gone()
            editPriceLayout.layoutExtraSeatProceed.gone()
            editPriceLayout.btnExtraBookingProceed.visible()
            editPriceLayout.nextBackLayout.gone()

            if (book)
                editPriceLayout.bookLL.visible()
            else
                editPriceLayout.bookLL.gone()

            if (isBimaServiceDetails != null && isBimaServiceDetails == true) {
                editPriceLayout.blockLL.gone()
                editPriceLayout.unblockLL.gone()
            } else {
                if (block && canBlockSeat)
                    editPriceLayout.blockLL.visible()
                else
                    editPriceLayout.blockLL.gone()

                if (unblock && canUnblockSeat)
                    editPriceLayout.unblockLL.visible()
                else
                    editPriceLayout.unblockLL.gone()
            }


            if (isBimaServiceDetails != null && isBimaServiceDetails == true) {
                if (privilegeResponseModel?.chartSharedPrivilege?.get(0)?.privileges?.allow_booking == true) {
                    editPriceLayout.bookLL.visible()
                } else {
                    editPriceLayout.bookLL.gone()
                }
            }
        }
    }


    private fun unBlockFunction(
        seatList: ArrayList<SeatDetail>,
        seatNumber: String,
        currentSeatNo: String
    ) {
        val serviceDateTimeBusType =
            "${getDateDMYY(travelDate)} | $deptTime  | $busType"
        DialogUtils.unblockSeatsDialog(
            this,
            getString(R.string.unblock_seat),
            getString(R.string.unBlockContent),
            "$source-$destination",
            serviceDateTimeBusType,
            seatList.size.toString(),
            seatNumber,
            getString(R.string.goBack),
            getString(R.string.unblock_seat),
            false,
            currentSeatNo,
            object : DialogButtonUnblockSeatListener {
                override fun onLeftButtonClick() {

                }

                override fun onRightButtonClick(
                    seats: String,
                    selectionType: String,
                    fromDate: String?,
                    toDate: String?,
                    remarks: String?
                ) {
                    onSeatSelectionListener.unblockSeat(
                        seats,
                        selectionType,
                        fromDate,
                        toDate,
                        remarks
                    )
                }
            }
        )
    }


    private fun onNoSeatSelection() {
        isSelectedSeat = false
        if (isExtraSeatChecked) {
            binding.apply {
                editPriceLayout.root.visible()
                editPriceLayout.proceedLayout.gone()
                editPriceLayout.editprice.gone()
                editPriceLayout.layoutExtraSeatProceed.visible()
                editPriceLayout.btnExtraBookingProceed.visible()
                editPriceLayout.nextBackLayout.gone()

                btnServiceSummary.gone()
                layoutSummary.root.gone()
            }
        } else {
            binding.apply {
                if (isBimaServiceDetails != null && isBimaServiceDetails == true) {
                    btnServiceSummary.gone()
                } else {
                    btnServiceSummary.visible()
                }
                binding.layoutSummary.root.gone()

                coachSwipeButtonsVisibility()
                editPriceLayout.proceedLayout.gone()
                editPriceLayout.editprice.gone()
                editPriceLayout.fabsummary.gone()
                editPriceLayout.layoutExtraSeatProceed.gone()
            }
        }

        binding.editPriceLayout.fabsummary.setOnClickListener(this)
        this.editFareSeatDetails.clear()
        this.selectedSeatDetails.clear()
    }

    private fun unselectSeatData() {

    }

    override fun unSelectAllSeats() {
    }



    override fun unblockSeat(
        seatNumber: String,
        selectionType: String,
        fromDate: String?,
        toDate: String?,
        remarks: String?
    ) {

        if (this.isNetworkAvailable()) {


            val ticket =
                com.bitla.ts.domain.pojo.unblock_seat.request.Ticket(
                    seatNumber.replace(
                        " ",
                        ""
                    )
                )
            val searchBusParams = com.bitla.ts.domain.pojo.unblock_seat.request.SearchbusParams(
                sourceId,
                destinationId
            )

            val record = com.bitla.ts.domain.pojo.unblock_seat.request.Record(
                fromDate,
                "",
                "",
                "",
                toDate,
                "1111111",
                remarks
            )

            searchBusParams.from = sourceId.substringAfter(":")
            searchBusParams.to = destinationId.substringAfter(":")

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
                selectionType = if (privilegeResponseModel?.country.equals(
                        "Indonesia",
                        true
                    )
                ) {
                    ""
                } else selectionType,
                ticket = ticket
            )

            blockViewModel.unblockSeatsApi(
                reqBody,
                unblock_seat_method_name
            )
        } else this.noNetworkToast()

    }

    override fun editSeatFare(seatNumber: String, newFare: String) {

    }

    private fun unblockObserver() {
        blockViewModel.unblockSeats.observe(this) {
            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            callCoach = true
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
//                            binding.editPriceLayout.root.gone()
                            binding.apply {
                                if (availableRoutesList.size == 1) {
                                    editPriceLayout.arrowCoachNextImg.gone()
                                    editPriceLayout.arrowCoachBackImg.gone()
                                    editPriceLayout.nextBackLayout.gone()
                                } else {
                                    coachSwipeButtonsVisibility()
                                    binding.apply {
                                        editPriceLayout.proceedLayout.gone()
                                        editPriceLayout.editprice.gone()
                                        editPriceLayout.fabsummary.gone()
                                        editPriceLayout.layoutExtraSeatProceed.gone()
                                    }
                                }
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

    private fun tabs(btn: String) {
        binding.summaryLayout.root.visible()
        binding.btnServiceSummary.gone()
        binding.tansparentbackbroundServiceSummary.visible()

        val tabsList: MutableList<Tabs> = mutableListOf()

        val tabsummary = Tabs()
        tabsummary.title = getString(R.string.summary)
        tabsList.add(tabsummary)

        if (privilegeResponseModel?.isChileApp == false) {
            val tabAmenities = Tabs()
            tabAmenities.title = getString(R.string.amenities)
            tabsList.add(tabAmenities)
        }


        val tabCancellation = Tabs()
        tabCancellation.title = getString(R.string.cancellation)
        tabsList.add(tabCancellation)
//            binding.summaryLayout.tabsPickup.tabMode = TabLayout.MODE_SCROLLABLE


        val fragmentAdapter = ServiceSummaryTabsAdapter(
            this,
            tabsList,
            this.supportFragmentManager,
            privilegeResponseModel
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
            tabTextView.textSize = 13f

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


        binding.summaryLayout.headText.setOnClickListener {
            if (btn == "btnServiceSummary") {
                binding.summaryLayout.root.gone()
                binding.btnServiceSummary.visible()

                binding.tansparentbackbround.gone()
                binding.tansparentbackbroundServiceSummary.gone()
            } else {
                binding.summaryLayout.root.gone()
                if (isBimaServiceDetails != null && isBimaServiceDetails == true) {
                    binding.editPriceLayout.fabsummary.gone()
                } else {
                    binding.editPriceLayout.fabsummary.visible()
                }
                binding.tansparentbackbround.gone()
                binding.tansparentbackbroundServiceSummary.gone()
            }
        }
    }


    private fun clickListener() {
//        binding.includeHeader.currentLocation.setOnClickListener(this)

        binding.tvSelectBoardingPoint.setOnClickListener(this)
        binding.tvSelectDropingPoint.setOnClickListener(this)
        binding.layoutSummary.layoutMultistation.setOnClickListener(this)
        binding.layoutSummary.layoutBoardinPoint.setOnClickListener(this)
        binding.layoutSummary.layoutDroppingPoint.setOnClickListener(this)
        binding.layoutSummary.layoutBookedBy.setOnClickListener(this)
        binding.layoutSummary.layoutServiceDetails.setOnClickListener(this)
        binding.layoutSummary.layoutBookings.setOnClickListener(this)
        binding.layoutSummary.layoutCollections.setOnClickListener(this)
        binding.layoutSummary.layoutReleasedTickets.setOnClickListener(this)
        binding.seatLegendsIV.setOnClickListener(this)
        binding.transparentOptionV.setOnClickListener(this)
        binding.includeHeader.headerLL.setOnClickListener(this)
        binding.editPriceLayout.arrowCoachBackImg.setOnClickListener(this)
        binding.editPriceLayout.arrowCoachNextImg.setOnClickListener(this)

        binding.modifySearchLayout.tvSource.setOnClickListener(this)
        binding.modifySearchLayout.btnSearch.setOnClickListener(this)
        binding.modifySearchLayout.tvDestination.setOnClickListener(this)
        binding.modifySearchLayout.tvTodayDate.setOnClickListener(this)
        binding.modifySearchLayout.tvTomorrowDate.setOnClickListener(this)
        binding.modifySearchLayout.tvSelectDate.setOnClickListener(this)
        binding.modifySearchLayout.tvSelectReturnDate.setOnClickListener(this)
        binding.modifySearchLayout.btnRotate.setOnClickListener(this)
        binding.modifySearchLayout.btnCancel.setOnClickListener(this)
        binding.modifySearchLayout.busServices.setOnClickListener(this)
        binding.editPriceLayout.blockLL.setOnClickListener(this)
        binding.editPriceLayout.unblockLL.setOnClickListener(this)
        binding.editPriceLayout.bookLL.setOnClickListener(this)
        binding.editPriceLayout.ticketPrice.setOnClickListener(this)
        binding.transparentBookedSeatsOptionsV.setOnClickListener(this)
        binding.layoutBookedSeatDetails.menuCancelTicket.setOnClickListener(this)
        binding.layoutBookedSeatDetails.menuUpdateTicket.setOnClickListener(this)
        binding.layoutBookedSeatDetails.menuUpdateRemark.setOnClickListener(this)
        binding.layoutBookedSeatDetails.menuViewticket.setOnClickListener(this)
        binding.layoutBookedSeatDetails.callPassenger.setOnClickListener(this)
        binding.layoutBookedSeatDetails.resendSms.setOnClickListener(this)
        binding.layoutBookedSeatDetails.menuShift.setOnClickListener(this)
        binding.layoutBookedSeatDetails.menuShiftSameService.setOnClickListener(this)
        binding.layoutBookedSeatDetails.menuMoveExtra.setOnClickListener(this)
        binding.layoutBookedSeatDetails.boardedSwitchBox.setOnClickListener(this)
        binding.layoutBookedSeatDetails.menuConvertPermanentPhoneBlock.setOnClickListener(this)
        binding.editPriceLayout.nextBackLayout.setOnClickListener {
        }

    }


    private fun navigateToPassengerPaymentActivity() {

        if (privilegeResponseModel?.allowToShowNewFlowInTsApp == true
        ) {
            //humsafar
            val intent = Intent(this, PassengerPaymentNewFlowActivity::class.java)
            intent.putExtra("is_extra_seat", isExtraSeatChecked)

            if (isApplyBPDPFare == "true") {
                stageDetails.forEach {
                    if (it.id.toString() == selectedBoarding?.id) {
                        PreferenceUtils.putObject<StageDetail>(
                            it,
                            PREF_BOARDING_STAGE_DETAILS
                        )
                    }
                    if (it.id.toString() == selectedDropping?.id) {
                        PreferenceUtils.putObject<StageDetail>(
                            it,
                            PREF_DROPPING_STAGE_DETAILS
                        )
                    }
                }
                startActivity(intent)

            } else {
                startActivity(intent)
            }
        } else {
            val intent = Intent(this, PassengerPaymentActivity::class.java)
            intent.putExtra("is_extra_seat", isExtraSeatChecked)

            if (isApplyBPDPFare == "true") {
                stageDetails.forEach {
                    if (it.id.toString() == selectedBoarding?.id) {
                        PreferenceUtils.putObject<StageDetail>(
                            it,
                            PREF_BOARDING_STAGE_DETAILS
                        )
                    }
                    if (it.id.toString() == selectedDropping?.id) {
                        PreferenceUtils.putObject<StageDetail>(
                            it,
                            PREF_DROPPING_STAGE_DETAILS
                        )
                    }
                }
                startActivity(intent)

            } else {
                startActivity(intent)
            }
        }

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.resend_otp -> {
                if (this.isNetworkAvailable()) {
                    resendOtpAndQrCodeAPI(pnrNum, seatNum)
                    resendOtpAndQrCodeObserver()
                } else this.noNetworkToast()
            }

            R.id.scan_qr_code -> {
                scanTag = "verificationScan"
                qrresponse = ""
                scanScaeen()
            }

            R.id.btn_service_summary -> {
                tabs("btnServiceSummary")
                firebaseLogEvent(
                    this,
                    SERVICE_SUMMARY,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    SERVICE_SUMMARY,
                    "service summary"
                )
            }

            R.id.fabsummary -> {
                binding.editPriceLayout.fabsummary.gone()
                tabs("fabSummary")
            }

            R.id.toolbar_image_left -> {
                onBackPressed()
            }

            R.id.cardPhoneBooking -> {
                DialogUtils.phoneBlockingDialog(
                    context = this,
                    varArgListener = this,
                    isPermanentPhoneBooking = isPermanentPhoneBooking,
                    removePreSelectionOptionInTheBooking = removePreSelectionOptionInTheBooking,
                    hours = calculatedHours.toString(),
                    minutes = calculatedMinutes.toString(),
                    amOrpm = checkAMOrPM
                )
            }

            R.id.current_location -> {
                val intent = Intent(this, CurrentLocationActivity::class.java)
                PreferenceUtils.putString(
                    "toolbarheader",
                    binding.includeHeader.toolbarHeaderText.text.toString()
                )
                PreferenceUtils.putString(
                    "toolbarsubheader",
                    "${serviceNumber} | ${getDateDMYY(travelDate)} | $source-$destination | ${serviceBusType} ${totalSeats}"
                )

                startActivity(intent)
            }


            R.id.layout_service_details -> {
                // val busDetails = "${getDateDMYY(travelDate)} $source - $destination $busType "
                val busDetails =
                    "$serviceNumber | ${getDateDMYY(travelDate)} $source - $destination | $busType"
                val intent = Intent(context, ServiceDetailsActivity::class.java)
                intent.putExtra(context.getString(R.string.origin), source)
                intent.putExtra(context.getString(R.string.destination), destination)
                intent.putExtra(context.getString(R.string.bus_type), busDetails)

                PreferenceUtils.removeKey(getString(R.string.scannedUserName))
                PreferenceUtils.removeKey(getString(R.string.scannedUserId))
                PreferenceUtils.removeKey("selectedScanType")
                PreferenceUtils.removeKey(getString(R.string.scan_coach))
                PreferenceUtils.removeKey(getString(R.string.scan_driver_1))
                PreferenceUtils.removeKey(getString(R.string.scan_driver_2))
                PreferenceUtils.removeKey(getString(R.string.scan_cleaner))
                PreferenceUtils.removeKey(getString(R.string.scan_contractor))

                context.startActivity(intent)
                firebaseLogEvent(
                    this,
                    UPDATE_DETAILS_BOOKCLICK,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    UPDATE_DETAILS_BOOKCLICK,
                    "Update details"
                )
            }

            R.id.tv_select_boarding_point -> {
                selectBoardingPoint()
            }

            R.id.tv_select_droping_point -> {
                selectDroppingPoint()
            }

            R.id.bookLL -> {
                trackFares()
                if (countryName.equals("india", true) &&
                    editFareMandatoryForAgentUser &&
                    !checkFares() &&
                    binding.editPriceLayout.editFareIV.visibility == View.VISIBLE) {
                    showCustomFareUpdateToast(this, getString(R.string.update_fare_validation))
                }
                else {
                    if (isApplyBPDPFare == "true") {

                        if (privilegeResponseModel?.allowToShowNewFlowInTsApp == true
                        ) {
                            //humsafar
                            val intent = Intent(this, PassengerPaymentNewFlowActivity::class.java)
                            if (isApplyBPDPFare == "true") {
                                stageDetails.forEach {
                                    if (it.id.toString() == selectedBoarding?.id) {
                                        PreferenceUtils.putObject<StageDetail>(
                                            it,
                                            PREF_BOARDING_STAGE_DETAILS
                                        )
                                    }
                                    if (it.id.toString() == destinationId.split(":")
                                            .get(0)
                                    ) {// The splitting is done because the ID we are getting in BPDP case is  in the form cityname:stationName
                                        PreferenceUtils.putObject<StageDetail>(
                                            it,
                                            PREF_DROPPING_STAGE_DETAILS
                                        )
                                    }
                                }
                            } else {

                                stageDetails.forEach {
                                    if (it.id.toString() == selectedBoarding?.id) {
                                        PreferenceUtils.putObject<StageDetail>(
                                            it,
                                            PREF_BOARDING_STAGE_DETAILS
                                        )
                                    }
                                    if (it.id.toString() == selectedDropping?.id) {
                                        PreferenceUtils.putObject<StageDetail>(
                                            it,
                                            PREF_DROPPING_STAGE_DETAILS
                                        )
                                    }
                                }
                            }
                            startActivity(intent)
                        }
                        else {
                            val intent = Intent(this, PassengerPaymentActivity::class.java)
                            if (isApplyBPDPFare == "true") {
                                stageDetails.forEach {
                                    if (it.id.toString() == selectedBoarding?.id) {
                                        PreferenceUtils.putObject<StageDetail>(
                                            it,
                                            PREF_BOARDING_STAGE_DETAILS
                                        )
                                    }
                                    if (it.id.toString() == destinationId.split(":")
                                            .get(0)
                                    ) {// The splitting is done because the ID we are getting in BPDP case is  in the form cityname:stationName
                                        PreferenceUtils.putObject<StageDetail>(
                                            it,
                                            PREF_DROPPING_STAGE_DETAILS
                                        )
                                    }
                                }
                                intent.putExtra("pickupAddressCharge", pickupChargeDetails)
                                intent.putExtra("dropoffAddressCharge", dropoffChargeDetails)
                            } else {

                                stageDetails.forEach {
                                    if (it.id.toString() == selectedBoarding?.id) {
                                        PreferenceUtils.putObject<StageDetail>(
                                            it,
                                            PREF_BOARDING_STAGE_DETAILS
                                        )
                                    }
                                    if (it.id.toString() == selectedDropping?.id) {
                                        PreferenceUtils.putObject<StageDetail>(
                                            it,
                                            PREF_DROPPING_STAGE_DETAILS
                                        )
                                    }
                                }
                            }
                            startActivity(intent)
                        }


                    } else if (isSelectedSeat && isExtraSeatChecked) {

                        bookExtraSeatNoList.clear()
                        selectedExtraSeatsDetails.clear()
                        for (i in 0..extraSeats.minus(1)) {
                            val dummySeatNo = i + 1
                            val seatDetail = SeatDetail()
                            seatDetail.fare = minExtraSeatFare
                            seatDetail.baseFareFilter = minExtraSeatFare
                            seatDetail.number = ""
                            seatDetail.additionalFare = 0.0
                            seatDetail.discountAmount = 0.0
                            seatDetail.isExtraSeat = true
                            selectedExtraSeatsDetails.add(seatDetail)
                            bookExtraSeatNoList.add(dummySeatNo.toString())
                        }

                        val clubbedExtraSeat = selectedSeatDetails + selectedExtraSeatsDetails
                        setSelectSeats(clubbedExtraSeat as java.util.ArrayList<SeatDetail>)
                        setSelectExtraSeats(selectedExtraSeatsDetails)

                        val commaSeparatedExtraSeats =
                            android.text.TextUtils.join(",", bookExtraSeatNoList)
                        setSelectSeatNumber(commaSeparatedExtraSeats)
                        val extraSeats =
                            "${getString(R.string.extra_seats)} (${bookExtraSeatNoList.size})"

                        binding.imgCollapse.gone()
                        binding.imgExpand.gone()

                        if (isAllSeatSelected)
                            binding.tvSeatSelected.text = getString(R.string.selectedAvailableSeats)
                        else {
                            binding.tvSeatSelected.text =
                                "${replaceBracketsString(finalSeatNumbers.toString())}, $extraSeats"
                        }

                        binding.tvSeatSelected.setTextColor(
                            ColorStateList.valueOf(
                                resources.getColor(
                                    R.color.colorPrimary
                                )
                            )
                        )
                        binding.tvSeatSelected.backgroundTintList =
                            resources.getColorStateList(R.color.colorPrimaryTransparent)

                        selected()
                    } else {
                        retrieveSelectedExtraSeats().clear()
                        bookExtraSeatNoList.clear()
                        selectedExtraSeatsDetails.clear()

                        selectedSeatDetails.forEach {
                            it.isExtraSeat = false
                        }

                        binding.imgCollapse.gone()
                        binding.imgExpand.gone()

                        if (isAllSeatSelected)
                            binding.tvSeatSelected.text = getString(R.string.selectedAvailableSeats)
                        else
                            binding.tvSeatSelected.text =
                                replaceBracketsString(finalSeatNumbers.toString())

                        binding.tvSeatSelected.setTextColor(
                            ColorStateList.valueOf(
                                resources.getColor(
                                    R.color.colorPrimary
                                )
                            )
                        )
                        binding.tvSeatSelected.backgroundTintList =
                            resources.getColorStateList(R.color.colorPrimaryTransparent)
                        selected()
                        setSelectSeats(selectedSeatDetails)
                    }
                    PreferenceUtils.setPreference(PREF_UPDATE_COACH, false)
                    navigateToPassengerPayment()
                }
            }

            R.id.blockLL -> {
                val intent = Intent(this, NewPassengerDetailsActivity::class.java)
                intent.putExtra(
                    getString(R.string.boarding_point_key),
                    selectedBoarding?.name
                )
                intent.putExtra(
                    getString(R.string.dropping_point_key),
                    selectedDropping?.name
                )
                intent.putExtra(
                    getString(R.string.boarding_point_id_key),
                    selectedBoarding?.id
                )
                intent.putExtra(
                    getString(R.string.dropping_point_id_key),
                    selectedDropping?.id
                )
                intent.putExtra(
                    ROUTE_ID,
                    mainOpId
                )
                intent.putExtra(
                    NEW_BOOK_BLOCK_CHECK,
                    false
                )

                stageDetails.forEach {
                    if (it.id.toString() == selectedBoarding?.id) {
                        PreferenceUtils.putObject<StageDetail>(
                            it,
                            PREF_BOARDING_STAGE_DETAILS
                        )
                    }
                    if (it.id.toString() == selectedDropping?.id) {
                        PreferenceUtils.putObject<StageDetail>(
                            it,
                            PREF_DROPPING_STAGE_DETAILS
                        )
                    }
                }
                setSelectSeats(selectedSeatDetails)
                PreferenceUtils.setPreference(PREF_UPDATE_COACH, false)
                startActivity(intent)
            }

            R.id.unblockLL -> {
                var seatNumbers = ""

                if (blockedSeatsList.isNotEmpty()) {
                    blockedSeatsList.forEach {
                        seatNumbers += "${it.number},"
                    }
                    seatNumbers = seatNumbers.substring(0, seatNumbers.length - 1)

                }
                unBlockFunction(
                    blockedSeatsList,
                    seatNumbers,
                    retrieveSelectedSeatNumber() ?: ""
                )
            }

            R.id.btnExtraBookingProceed -> {
                bookExtraSeatNoList.clear()
                selectedExtraSeatsDetails.clear()
                selectedSeatDetails.clear()

                val bookExtraSeatNoList = mutableListOf<String>()
                for (i in 0..extraSeats.minus(1)) {
                    val dummySeatNo = i + 1
                    val seatDetail = SeatDetail()
                    seatDetail.fare = minExtraSeatFare
                    seatDetail.baseFareFilter = minExtraSeatFare
                    seatDetail.number = ""
                    seatDetail.additionalFare = 0.0
                    seatDetail.discountAmount = 0.0
                    seatDetail.isExtraSeat = true
                    selectedSeatDetails.add(seatDetail)
                    bookExtraSeatNoList.add(dummySeatNo.toString())
                }
                setSelectSeats(selectedSeatDetails)
                val commaSeparatedExtraSeats =
                    android.text.TextUtils.join(",", bookExtraSeatNoList)
                setSelectSeatNumber(commaSeparatedExtraSeats)
                val extraSeats =
                    "${getString(R.string.extra_seats)} (${bookExtraSeatNoList.size})"
                binding.tvSeatSelected.text = extraSeats


                if (isApplyBPDPFare == "true") {
                    val intent = Intent(this, PassengerPaymentActivity::class.java)

                    stageDetails.forEach {
                        if (it.id.toString() == selectedBoarding?.id) {
                            PreferenceUtils.putObject<StageDetail>(
                                it,
                                PREF_BOARDING_STAGE_DETAILS
                            )
                        }
                        if (it.id.toString() == selectedDropping?.id) {
                            PreferenceUtils.putObject<StageDetail>(
                                it,
                                PREF_DROPPING_STAGE_DETAILS
                            )
                        }
                    }
                    startActivity(intent)
                } else {
                    // selectBoardingPoint()
                    selected()
                }
            }

            R.id.img_collapse -> {

                collapseCoach(false)
            }

            R.id.img_expand -> {
                expandCoach(false)
            }

            R.id.tvEditSeats -> {
                expandCoach(true)
            }

            R.id.imgRemoveSeat -> {
                if (extraSeats > 1) {
                    extraSeats--
                    binding.editPriceLayout.tvExtraSeats.text = extraSeats.toString()
                }
            }

            R.id.imgAddSeat -> {
                extraSeats++
                binding.editPriceLayout.tvExtraSeats.text = extraSeats.toString()
            }

            R.id.tv_edit_price -> {
                editFareDialogBuilder = editFareDialog()
            }

            R.id.editFareIV -> {
                editFareDialogBuilder = editFareDialog()
            }

            R.id.change_bp_dp -> {
                DialogUtils.bpDpDialog(
                    this,
                    boardingPointList,
                    droppingPointList,
                    this,
                    selectedBoarding!!,
                    selectedDropping!!
                )
            }

            R.id.moreIV -> {
                if (serviceDetails != null) {    // added this condition because isBima depends on service details api and few options depends on isBima key
                    toggle()
                }

            }

            R.id.seatLegendsIV -> {
                seatLegendAnimation()
            }

            R.id.transparentOptionV -> {
                Timber.d("transparentOptionV clicked")
                toggle()
            }

            R.id.headerLL -> {
                originSearchList = mutableListOf()
                destinationSearchList = mutableListOf()
                shortRouteList = mutableListOf()
                binding.modifySearchLayout.tvSource.text = tempSourceName
                binding.modifySearchLayout.tvDestination.text = tempDestinationIdName
                binding.modifySearchLayout.tvSelectDate.text =
                    thFormatDateMMMOutput(getDateYMD(travelDate))

                callShoutRouteCityPair()
                //modifySearchHandling()
            }

            R.id.btnCancel -> {
                isCityPairOrDateModified = false
                closeChageStartionLayout()
            }

            R.id.tvSource -> {
                selectService(getString(R.string.origin))
            }

            R.id.tvDestination -> {

                if (binding.modifySearchLayout.tvSource.text.isNotEmpty()) {
                    selectService(getString(R.string.destination))
                } else {
                    toast(getString(R.string.selectSource))
                }
            }

            R.id.tvSelectDate -> {
                var minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)

                /*if (isAgentLogin) {
                    if (privilegeResponseModel.allowToViewDepartedServices == true) {
                        minDate = stringToDate("01-01-1900", DATE_FORMAT_D_M_Y)
                    } else {
                        minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                    }
                } else {
                    if (privilegeResponseModel.allowToViewPastDateServices == true) {
                        minDate = stringToDate("01-01-1900", DATE_FORMAT_D_M_Y)
                    } else {
                        minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                    }
                }*/

                if (privilegeResponseModel?.isAllowBookingAfterTravelDate == true) {
                    minDate = stringToDate("01-01-1900", DATE_FORMAT_D_M_Y)
                }

                if (isAgentLogin) {
                    minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                }

                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(minDate)
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(object : SlyCalendarDialog.Callback {
                        override fun onCancelled() {

                        }

                        override fun onDataSelected(
                            firstDate: Calendar?,
                            secondDate: Calendar?,
                            hours: Int,
                            minutes: Int
                        ) {
                            firstDate!!.set(Calendar.HOUR_OF_DAY, hours)
                            firstDate.set(Calendar.MINUTE, minutes)
                            val date = SimpleDateFormat(
                                DATE_FORMAT_D_M_Y,
                                Locale.getDefault()
                            ).format(firstDate.time)

                            travelDateModifyLayout = getDateYMD(date)
                            binding.modifySearchLayout.tvSelectDate.text =
                                thFormatDateMMMOutput(travelDateModifyLayout)

                            isCityPairOrDateModified = true
                        }

                    }).show(supportFragmentManager, TAG)
            }


            R.id.tvTodayDate -> {
                setTodayDate()
            }

            R.id.tvTomorrowDate -> {
                setTomorrowDate()
            }

            R.id.btnSearch -> {
                modifySearchButtonClick()
            }

            R.id.btnRotate -> {
                var temp = ""
                temp = tempSourceName
                tempSourceName = tempDestinationIdName
                tempDestinationIdName = temp
                temp = tempSourceId
                tempSourceId = tempDestinationId
                tempDestinationId = temp
                binding.modifySearchLayout.tvSource.text = tempSourceName
                binding.modifySearchLayout.tvDestination.text = tempDestinationIdName


            }

            R.id.transparentBookedSeatsOptionsV -> {

                val view: View =
                    findViewById<View>(R.id.layout_booked_seat_details) as ConstraintLayout
                val slideLeft: Animation =
                    AnimationUtils.loadAnimation(this, R.anim.exit_to_right)
                view.visibility = GONE
                view.startAnimation(slideLeft)
                binding.transparentBookedSeatsOptionsV.gone()
                binding.seatLegendsIV.visible()
                coachSwipeButtonsVisibility()

                if (isBimaServiceDetails == true) {
                    binding.btnServiceSummary.gone()
                } else {
                    if (finalSeatNumbers.isEmpty()) {
                        binding.btnServiceSummary.visible()
                    }
                }

            }

            R.id.boarded_switch_box -> {
                if (lastSelectedSeatPosition < seatPassengersList.size) {
                    if (binding.layoutBookedSeatDetails.boardedSwitchBox.isChecked) {
                        binding.layoutBookedSeatDetails.boardedSwitchBox.isChecked = false
                        onSeatSelectionListener.checkBoardedStatus(
                            true,
                            seatPassengersList[lastSelectedSeatPosition].name,
                            seatPassengersList[lastSelectedSeatPosition].ticket_no,
                            seatPassengersList[lastSelectedSeatPosition].seat_no,
                            binding.layoutBookedSeatDetails.boardedSwitchBox
                        )
                    } else {
                        binding.layoutBookedSeatDetails.boardedSwitchBox.isChecked = true
                        onSeatSelectionListener.checkBoardedStatus(
                            false,
                            seatPassengersList[lastSelectedSeatPosition].name,
                            seatPassengersList[lastSelectedSeatPosition].ticket_no,
                            seatPassengersList[lastSelectedSeatPosition].seat_no,
                            binding.layoutBookedSeatDetails.boardedSwitchBox
                        )
                    }
                }

            }

            R.id.menu_cancel_ticket -> {
               // baseUpdateCancelTicket.showTicketCancellationSheet(seatPassengersList[lastSelectedSeatPosition].ticket_no)
                cancelTicketSheet.showTicketCancellationSheet(seatPassengersList[lastSelectedSeatPosition].ticket_no)

            }

            R.id.menu_viewticket -> {
//                    val intent = if (privilegeResponseModel?.country.equals(
//                            "India",
//                            true
//                        ) || privilegeResponseModel?.country.equals("Indonesia", true)
//                    ) {
//                        Intent(this, TicketDetailsActivityCompose::class.java)
//                    } else {
//                        Intent(this, TicketDetailsActivity::class.java)
//                    }
                val intent = Intent(this, TicketDetailsActivityCompose::class.java)
                intent.putExtra(
                    context.getString(R.string.TICKET_NUMBER),
                    seatPassengersList[lastSelectedSeatPosition].ticket_no
                )
                intent.putExtra("returnToDashboard", false)
                context.startActivity(intent)
            }

            R.id.menu_update_ticket -> {
                val num =
                    seatPassengersList[lastSelectedSeatPosition].ticket_no.substringBefore(" ")
                /* baseUpdateCancelTicket.showSingleTicketUpdateSheet(
                     num,
                     seatPassengersList[lastSelectedSeatPosition].seat_no!!
                 )*/
                baseUpdateCancelTicket.updateBulkTicketData(
                    num,
                    seatPassengersList[lastSelectedSeatPosition].seat_no, this
                )
            }

            R.id.menu_update_remark -> {
                openUpdateRemarksDialog()
            }

            R.id.call_passenger -> {

                onSeatSelectionListener.callPassenger(
                    seatPassengersList[lastSelectedSeatPosition].ticket_no,
                    seatPassengersList[lastSelectedSeatPosition].phone_num
                )
            }

            R.id.resend_sms -> {
                /*val intent = Intent(context, SmsNotificationActivity::class.java)
                startActivity(intent)*/

                callSendSMSEmailApi("sms")
            }

            R.id.menu_shift -> {
                val intent = Intent(context, ShiftPassengerActivity::class.java)
                intent.putExtra(
                    "service_ticketno",
                    seatPassengersList[lastSelectedSeatPosition].ticket_no
                )

                intent.putExtra(
                    "travel_date",
                    seatPassengersList[lastSelectedSeatPosition].travel_date
                )

                PreferenceUtils.putString(
                    "SHIFT_SeatPnrNumber",
                    seatPassengersList[lastSelectedSeatPosition].ticket_no
                )
                PreferenceUtils.putString(
                    "TicketDetail_SeatNumbes",
                    seatPassengersList[lastSelectedSeatPosition].seat_numbers
                )

                Timber.d("sourceIdsourceIdTest - $sourceId & $destinationId")

                PreferenceUtils.putString("SHIFT_servicename", serviceNumber)
                PreferenceUtils.putString("SHIFT_originId", sourceId)
                PreferenceUtils.putString("SHIFT_destinationId", destinationId)
                PreferenceUtils.putString(
                    "oldServiceNumberShiftACTIVITY",
                    "${serviceNumber}?${serviceTravelDate}"
                )
                PreferenceUtils.putString(
                    "TicketDetail_noOfSeats",
                    (seatPassengersList[lastSelectedSeatPosition].no_of_seats ?: 1).toString()
                )

                startActivity(intent)
            }

            R.id.menu_shift_same_service_ -> {
                val intent = Intent(context, ShiftPassengerActivity::class.java)
                intent.putExtra(
                    "service_ticketno",
                    seatPassengersList[lastSelectedSeatPosition].ticket_no
                )
                intent.putExtra("partial_shift", true)
                PreferenceUtils.putString("SHIFT_servicename", serviceNumber)
                PreferenceUtils.putString(
                    "SHIFT_SeatPnrNumber",
                    seatPassengersList[lastSelectedSeatPosition].ticket_no
                )
                PreferenceUtils.putString(
                    "TicketDetail_SeatNumber_sameService",
                    seatPassengersList[lastSelectedSeatPosition].seat_no
                )
                PreferenceUtils.putString(
                    "TicketDetail_SeatNumbes",
                    seatPassengersList[lastSelectedSeatPosition].seat_no
                )

                PreferenceUtils.putString("SHIFT_originId", sourceId)
                PreferenceUtils.putString("SHIFT_destinationId", destinationId)

                PreferenceUtils.putString(
                    "oldServiceNumberShiftACTIVITY",
                    "${serviceNumber}?${serviceTravelDate}"
                )
                startActivity(intent)
            }

            R.id.menu_move_extra -> {

                if (isExtraSeat) {
                    DialogUtils.moveToNormalSeatDialog(
                        this,
                        "Move To Book Seats",
                        "${context.resources.getString(R.string.selectedSeatNo)} : ${seatPassengersList[lastSelectedSeatPosition].seat_no}",
                        context.resources.getString(R.string.cancel),
                        context.resources.getString(R.string.move),
                        seatPassengersList[lastSelectedSeatPosition].seat_no,
                        seatPassengersList[lastSelectedSeatPosition].seat_no,
                        object : DialogButtonMoveSeatExtraListener {
                            override fun onLeftButtonClick(string: String?) {

                            }

                            override fun onRightButtonClick(
                                remarks: String,
                                seatNo: String,
                                extraSeatNo: String,
                                sms: Boolean
                            ) {
                                if (seatPassengersList != null && seatPassengersList?.isNotEmpty() == true) {
                                    var ticketNumber =
                                        seatPassengersList[lastSelectedSeatPosition].ticket_no
                                    if (ticketNumber.contains("("))
                                        ticketNumber = ticketNumber.substringBefore("(").trim()
                                }

                                if (shouldTicketMoveToSeatExtraSeat && countryName.equals(
                                        "india",
                                        true
                                    )
                                ) {
                                    DialogUtils.showFullHeightPinInputBottomSheet(
                                        activity = this@NewCoachActivity,
                                        fragmentManager = supportFragmentManager,
                                        pinSize,
                                        getString(R.string.move_to_normal_seat),
                                        onPinSubmitted = { pin: String ->
                                            callMoveToNormalSeatApi(
                                                true,
                                                remarks,
                                                resId?.toLong() ?: 0L,
                                                seatNo,
                                                extraSeatNo,
                                                ticketNumber.trim(),
                                                authPin = pin
                                            )
                                        },
                                        onDismiss = null
                                    )
                                } else {
                                    callMoveToNormalSeatApi(
                                        true,
                                        remarks,
                                        resId?.toLong() ?: 0L,
                                        seatNo,
                                        extraSeatNo,
                                        ticketNumber.trim(),
                                        authPin = ""
                                    )
                                }

                            }
                        },
                        true
                    )
                } else {
                    if (privilegeResponseModel?.allowToMoveSpecificSeatsRelatedToAPnr == true) {
                        multiSelectMoveToExtra.showMultiExtraSeatSelectionDialog(
                            context = this,
                            title = resources.getString(R.string.move_to_extra_seat),
                            message = "${resources.getString(R.string.selectedSeatNo)} : " + seatPassengersList[lastSelectedSeatPosition].seat_no,
                            buttonLeftText = resources.getString(R.string.cancel),
                            buttonRightText = resources.getString(R.string.move),
                            seatNumber = seatPassengersList[lastSelectedSeatPosition].seat_no,
                            extraSeatNumber = "",
                            isEditable = false,
                            neededSeatNumbers = needed_seat_numbers,
                            dialogButtonMoveSeatExtraListener = object :
                                DialogButtonMoveSeatExtraListener {
                                override fun onLeftButtonClick(string: String?) {

                                }

                                override fun onRightButtonClick(
                                    remarks: String,
                                    seatNo: String,
                                    extraSeatNo: String,
                                    sms: Boolean
                                ) {
                                    var ticketNumber =
                                        seatPassengersList[lastSelectedSeatPosition].ticket_no
                                    if (ticketNumber.contains("("))
                                        ticketNumber = ticketNumber.substringBefore("(").trim()

                                    if (shouldTicketMoveToSeatExtraSeat && countryName.equals(
                                            "india",
                                            true
                                        )
                                    ) {
                                        DialogUtils.showFullHeightPinInputBottomSheet(
                                            activity = this@NewCoachActivity,
                                            fragmentManager = supportFragmentManager,
                                            pinSize,
                                            getString(R.string.move_to_extra_seat),
                                            onPinSubmitted = { pin: String ->
                                                callMoveToExtraSeatApi(
                                                    sms = sms,
                                                    remarks = remarks,
                                                    resID = (PreferenceUtils.getPreference(
                                                        PREF_RESERVATION_ID,
                                                        0L
                                                    ) ?: 0L).toString(),
                                                    seatNo = seatNo,
                                                    extraSeatNo = extraSeatNo,
                                                    ticketNo = ticketNumber,
                                                    authPin = pin
                                                )
                                            },
                                            onDismiss = null
                                        )
                                    } else {
                                        callMoveToExtraSeatApi(
                                            sms = sms,
                                            remarks = remarks,
                                            resID = (PreferenceUtils.getPreference(
                                                PREF_RESERVATION_ID,
                                                0L
                                            ) ?: 0L).toString(),
                                            seatNo = seatNo,
                                            extraSeatNo = extraSeatNo,
                                            ticketNo = ticketNumber,
                                            authPin = ""
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        DialogUtils.moveToExtraSeatDialog(
                            context,
                            context.resources.getString(R.string.move_to_extra_seat),
                            "${context.resources.getString(R.string.selectedSeatNo)} : " + seatPassengersList[lastSelectedSeatPosition].seat_no,
                            context.resources.getString(R.string.cancel),
                            context.resources.getString(R.string.move),
                            seatPassengersList[lastSelectedSeatPosition].seat_no,
                            "",
                            object : DialogButtonMoveSeatExtraListener {
                                override fun onLeftButtonClick(string: String?) {

                                }

                                override fun onRightButtonClick(
                                    remarks: String,
                                    seatNo: String,
                                    extraSeatNo: String,
                                    sms: Boolean
                                ) {
                                    if (seatPassengersList != null && seatPassengersList?.isNotEmpty() == true) {
                                        var ticketNumber =
                                            seatPassengersList[lastSelectedSeatPosition].ticket_no
                                        if (ticketNumber.contains("("))
                                            ticketNumber = ticketNumber.substringBefore("(").trim()
                                    }

                                    if (shouldTicketMoveToSeatExtraSeat && countryName.equals(
                                            "india",
                                            true
                                        )
                                    ) {
                                        DialogUtils.showFullHeightPinInputBottomSheet(
                                            activity = this@NewCoachActivity,
                                            fragmentManager = supportFragmentManager,
                                            pinSize,
                                            getString(R.string.move_to_extra_seat),
                                            onPinSubmitted = { pin: String ->
                                                callMoveToExtraSeatApi(
                                                    sms = true,
                                                    remarks = remarks,
                                                    resID = (PreferenceUtils.getPreference(
                                                        PREF_RESERVATION_ID,
                                                        0L
                                                    )
                                                        ?: 0L).toString(),
                                                    seatNo = seatNo,
                                                    extraSeatNo = extraSeatNo,
                                                    ticketNo = ticketNumber,
                                                    authPin = pin
                                                )
                                            },
                                            onDismiss = null
                                        )
                                    } else {
                                        callMoveToExtraSeatApi(
                                            sms = true,
                                            remarks = remarks,
                                            resID = (PreferenceUtils.getPreference(
                                                PREF_RESERVATION_ID,
                                                0L
                                            )
                                                ?: 0L).toString(),
                                            seatNo = seatNo,
                                            extraSeatNo = extraSeatNo,
                                            ticketNo = ticketNumber,
                                            authPin = ""
                                        )
                                    }
                                }

                            },
                            true
                        )
                    }
                }
            }

            R.id.menuConvertPermanentPhoneBlock -> {

                showConvertToPermanentPhoneBlockDialog()
            }

            R.id.arrowCoachBackImg -> {

                if (!isServiceLoading) {
                    if (swipePosition > 0) {
                        swipePosition--
                        reservationId = availableRoutesList[swipePosition].reservation_id
                        binding.coachProgressBar.visible()
                        callCoach = true
                        setSourceAndDestination()
                        callServiceApi()
                    }

                    updateArrowUI()
                }

//                /*if(swipePosition != 0){
//                    progressBar?.show()
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        progressBar?.dismiss()
//                    }, 1000)
//                }*/
//                if(!isServiceLoading){
//
////                    if(isNextSwipeClick){
////                        isPreviousSwipeClick=true
////                        isNextSwipeClick=false
////                    }
//                    binding.apply {
//                        if (swipePosition == -1 || swipePosition == 0) {
//                            editPriceLayout.arrowCoachBackImg.isClickable = false
//                            editPriceLayout.arrowCoachBackImg.isEnabled = false
//                        } else {
//                            if (swipePosition == 1) {
////                            arrowCoachBackImg.setBackgroundResource(R.drawable.ic_arrow_coach_back_disable)
//                                editPriceLayout.arrowCoachBackImg.setTextColor(
//                                    ContextCompat.getColor(
//                                        this@NewCoachActivity,
//                                        R.color.colorDimShadow6
//                                    )
//                                )
//                            } else {
//                                editPriceLayout.arrowCoachBackImg.isClickable = true
//                                editPriceLayout.arrowCoachBackImg.isEnabled = true
////                            arrowCoachBackImg.setBackgroundResource(R.drawable.ic_arrow_coach_back_active)
////                            arrowCoachNextImg.setBackgroundResource(R.drawable.ic_arrow_coach_next_active)
//
//                                editPriceLayout.arrowCoachBackImg.setTextColor(
//                                    ContextCompat.getColor(
//                                        this@NewCoachActivity,
//                                        R.color.colorPrimary
//                                    )
//                                )
//                                editPriceLayout.arrowCoachNextImg.setTextColor(
//                                    ContextCompat.getColor(
//                                        this@NewCoachActivity,
//                                        R.color.colorPrimary
//                                    )
//                                )
//                            }
//
//                            if (swipePosition == 1 && availableRoutesList.size - 1 == 1) {
//                                editPriceLayout.arrowCoachNextImg.setTextColor(
//                                    ContextCompat.getColor(
//                                        this@NewCoachActivity,
//                                        R.color.colorPrimary
//                                    )
//                                )
//                            }
//
//                            if (swipePosition == -1) {
//                                iterator.previous()
//                                swipePosition--
//                            } else if (swipePosition == 0 && !isNextSwipeClick) {
//                                iterator.previous()
//                            }
//
//                            if (::iterator.isInitialized && isNextSwipeClick) {
//                                iterator.previous()
//                                isNextSwipeClick = false
//                            }
////                        if (availableRoutesList[swipePosition - 1].is_service_blocked) {
////                            iterator.previous()
////                            swipePosition--
////                        }
//
//                            try {
//                                if (::iterator.isInitialized) {
//                                    reservationIdSwipe = iterator.previous()
//                                    reservationId = reservationIdSwipe.toLong()
//
//                                    binding.coachProgressBar.visible()
//                                    callCoach= true
//                                    callServiceApi()
//                                    swipePosition--
//                                }
//                            } catch (e: Exception) {
//                                Timber.d("ExceptionMsg ${e.message}")
//                            }
//                        }
//
//
//                    }
//                }
//
//
////                Timber.d("reservationId => Previous  $swipePosition , ${availableRoutesList.size-1}")
            }

            R.id.arrowCoachNextImg -> {
                if (!isServiceLoading && !availableRoutesList.isNullOrEmpty() && swipePosition < availableRoutesList.size - 1) {
                    swipePosition++
                    try {
                        reservationId = availableRoutesList[swipePosition].reservation_id
                        binding.coachProgressBar.visible()
                        callCoach = true
                        setSourceAndDestination()
                        callServiceApi()
                    } catch (e: Exception) {
                        Timber.d("ExceptionMsg ${e.message}")
                    }

                    updateArrowUI()
                }


//               /* if (swipePosition != availableRoutesList.size - 1){
//                    progressBar?.show()
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        progressBar?.dismiss()
//                    }, 1000)
//                }*/
//                    if(!isServiceLoading){
//                        binding.apply {
//                            editPriceLayout.arrowCoachBackImg.isClickable = true
//                            editPriceLayout.arrowCoachBackImg.isEnabled = true
////                    arrowCoachBackImg.setBackgroundResource(R.drawable.ic_arrow_coach_back_active)
//                            editPriceLayout.arrowCoachBackImg.setTextColor(
//                                ContextCompat.getColor(
//                                    this@NewCoachActivity,
//                                    R.color.colorPrimary
//                                )
//                            )
//                            if(availableRoutesList != null && availableRoutesList.size > 0){
//                                if (::iterator.isInitialized && swipePosition < availableRoutesList.size - 1) {
//                                    if (swipePosition == -1) {
//                                        iterator.next()
//                                        swipePosition++
//                                    } else if (swipePosition == 0 && !isNextSwipeClick) {
//                                        iterator.next()
//                                    }
//
//                                    if (availableRoutesList[swipePosition + 1].is_service_blocked) {
//                                        iterator.next()
//                                        swipePosition++
//                                    }
//
//                                    if(::iterator.isInitialized && isPreviousSwipeClick) {
//                                        iterator.next()
//                                    }
//
//                                    try {
//                                        reservationId = availableRoutesList[swipePosition+1].reservation_id
//                                        binding.coachProgressBar.visible()
//                                        callCoach= true
//                                        callServiceApi()
//                                        swipePosition++
//                                        isNextSwipeClick = true
//                                        isPreviousSwipeClick = false
//                                    } catch (e: Exception) {
//                                        Timber.d("ExceptionMsg ${e.message}")
//                                    }
//                                }
//
//                                if (swipePosition == availableRoutesList.size - 1) {
////                        arrowCoachNextImg.setBackgroundResource(R.drawable.ic_arrow_coach_next_disable)
//                                    editPriceLayout.arrowCoachNextImg.setTextColor(
//                                        ContextCompat.getColor(
//                                            this@NewCoachActivity,
//                                            R.color.colorDimShadow6
//                                        )
//                                    )
//                                }
//                            }
//                        }
//
//                    }
//
////                Timber.d("reservationId => Next $swipePosition , ${availableRoutesList.size-1}")
            }
        }
    }

    private fun setSourceAndDestination() {
        if (!availableRoutesList.isNullOrEmpty()) {
            for (i in 0 until availableRoutesList.size) {
                    if (reservationId == availableRoutesList[i].reservation_id) {
                        sourceId=availableRoutesList[i].originId.toString()
                        destinationId=availableRoutesList[i].destinationId.toString()
                        }
                    }
            }
        }


    private fun showConvertToPermanentPhoneBlockDialog() {
        convertToPermanentPhoneBlockDialog = AlertDialog.Builder(this).create()
        convertToPermanentPhoneBlockDialogBinding =
            DialogConvertToPermanentPhoneBlockBinding.inflate(LayoutInflater.from(this))
        convertToPermanentPhoneBlockDialog?.setView(convertToPermanentPhoneBlockDialogBinding.root)

        convertToPermanentPhoneBlockDialogBinding.btnProcceed.setOnClickListener {
            var ticketNumber =
                if (seatPassengersList.isNotEmpty()) seatPassengersList[lastSelectedSeatPosition].ticket_no else ""
            if (ticketNumber.contains("("))
                ticketNumber = ticketNumber.substringBefore("(").trim()

            Timber.d("ticketNumberX- $ticketNumber")

            callPhoneBlockTempToPermanentApi(
                apiKey = loginModelPref.api_key,
                pnrNumber = ticketNumber
            )
            convertToPermanentPhoneBlockDialog?.dismiss()
            closeToggle()
        }

        convertToPermanentPhoneBlockDialogBinding.closeTV.setOnClickListener {
            convertToPermanentPhoneBlockDialog?.dismiss()
        }

        convertToPermanentPhoneBlockDialogBinding.tvCancel.setOnClickListener {
            convertToPermanentPhoneBlockDialog?.dismiss()
        }

        convertToPermanentPhoneBlockDialog?.show()
    }

    private fun closeChageStartionLayout() {
        binding.modifySearchLayout.root.gone()
        binding.tansparentbackbroundServiceSummary.gone()

        if (isBimaServiceDetails == true) {
            binding.btnServiceSummary.gone()
        } else {
            if (finalSeatNumbers.isEmpty()) {
                binding.btnServiceSummary.visible()
            }
        }

        binding.seatLegendsIV.visible()
    }



    private fun updateArrowUI() {
        binding.apply {
            if(availableRoutesList != null && availableRoutesList.isNotEmpty()) {
                val isAtStart = swipePosition <= 0
                val isAtEnd = swipePosition >= (availableRoutesList?.size?.minus(1) ?: 0)

                editPriceLayout.arrowCoachBackImg.isEnabled = !isAtStart
                editPriceLayout.arrowCoachBackImg.isClickable = !isAtStart
                editPriceLayout.arrowCoachBackImg.setTextColor(
                    ContextCompat.getColor(
                        this@NewCoachActivity,
                        if (isAtStart) R.color.colorDimShadow6 else R.color.colorPrimary
                    )
                )

                editPriceLayout.arrowCoachNextImg.isEnabled = !isAtEnd
                editPriceLayout.arrowCoachNextImg.isClickable = !isAtEnd
                editPriceLayout.arrowCoachNextImg.setTextColor(
                    ContextCompat.getColor(
                        this@NewCoachActivity,
                        if (isAtEnd) R.color.colorDimShadow6 else R.color.colorPrimary
                    )
                )
            }

        }
    }


    private fun callMoveToExtraSeatApi(
        sms: Boolean,
        remarks: String,
        resID: String,
        seatNo: String,
        extraSeatNo: String,
        ticketNo: String,
        authPin: String
    ) {


        if (context.isNetworkAvailable()) {
            val reqBody =
                com.bitla.ts.domain.pojo.move_to_extra_seat.request.ReqBody(
                    loginModelPref.api_key,
                    sms,
                    remarks,
                    resID,
                    seatNo,
                    extraSeatNo,
                    ticketNo,
                    locale = locale,
                    auth_pin = authPin
                )
            val moveToExtraSeatRequest =
                MoveToExtraSeatRequest(
                    bccId.toString(),
                    format_type,
                    move_to_extra_seat,
                    reqBody
                )

            /* moveToExtraSeatViewModel.moveToExtraSeatApi(
                 loginModelPref.auth_token,
                 loginModelPref.api_key,
                 moveToExtraSeatRequest,
                 move_to_extra_seat
             ) */

            moveToExtraSeatViewModel.moveToExtraSeatApi(
                reqBody,
                move_to_extra_seat
            )
        } else
            context.noNetworkToast()


    }


    private fun seatLegendAnimation() {
        val view: View =
            findViewById<View>(R.id.layout_passenger_details_seat_legends) as CardView

// slide-up animation

// slide-up animation
        val slideUP: Animation =
            AnimationUtils.loadAnimation(this, R.anim.dialog_slide_up_400_delay)
        val slideDown: Animation = AnimationUtils.loadAnimation(this, R.anim.dialog_slide_down)

        if (view.visibility == GONE) {
            view.visibility = VISIBLE
            view.startAnimation(slideUP)
            binding.tansparentbackbround.visible()
            binding.btnServiceSummary.gone()
        } else {
            view.visibility = GONE
            view.startAnimation(slideDown)
            binding.tansparentbackbround.gone()

            if (isBimaServiceDetails == true) {
                binding.btnServiceSummary.gone()
            } else {
                binding.btnServiceSummary.visible()
            }
        }
    }


    private fun toggle() {
        val view = findViewById<View>(R.id.layout_coach_options) as ConstraintLayout
// slide-up animation
// slide-up animation
        val slideRight: Animation = AnimationUtils.loadAnimation(this, R.anim.enter_from_right)
        val slideLeft: Animation = AnimationUtils.loadAnimation(this, R.anim.exit_to_right)

        if (view.visibility == GONE) {
            view.visibility = VISIBLE
            view.startAnimation(slideRight)
            binding.transparentOptionV.visible()
            binding.btnServiceSummary.gone()
        } else {
            view.visibility = GONE
            view.startAnimation(slideLeft)
            binding.transparentOptionV.gone()

            if (isBimaServiceDetails == true) {
                binding.btnServiceSummary.gone()
            } else {
                if (finalSeatNumbers.isEmpty()) {
                    binding.btnServiceSummary.visible()
                }
            }

        }
    }


    private fun modifySearchHandling() {
        // slide-up animation

        // slide-up animation
        val slideRight: Animation = AnimationUtils.loadAnimation(this, R.anim.dialog_slide_down)
        val slideLeft: Animation = AnimationUtils.loadAnimation(this, R.anim.dialog_slide_up)

//        if (binding.modifySearchLayout.root.visibility== GONE) {
        binding.modifySearchLayout.root.visible()

        if (privilegeResponseModel?.country.equals("india", true)) {
            binding.modifySearchLayout.dateLayout.visible()
        } else {
            binding.modifySearchLayout.dateLayout.gone()
        }

        binding.tansparentbackbroundServiceSummary.visible()
        binding.btnServiceSummary.gone()
        binding.seatLegendsIV.gone()

//        } else {
//
//        }


//        binding.modifySearchLayout.TvsourceText.setOnClickListener {
//            selecteServiceOnClick(binding.modifySearchLayout.TvsourceText, originList)
//
//        }

//        selectecServiceOnClick(binding.modifySearchLayout.TvsourceText)

    }

    private fun selectService(selectionType: String) {
        val intent = Intent(
            this,
            ShortRoutCitySelectionActivity::class.java
        )

        if (selectionType.equals(getString(R.string.origin))) {
            shortRouteList.forEach {
                val cityModel = SearchModel()
                cityModel.id = it.origin.id.toString()
                cityModel.name = it.origin.name

                originSearchList.add(cityModel)
            }

        } else {
            shortRouteList.forEach {

                if (tempSourceId.substringAfter(":") == it.origin.id.substringAfter(":")) {
                    val cityModel = SearchModel()
                    cityModel.id = it.destination.id.toString()
                    cityModel.name = it.destination.name
                    originSearchList.add(cityModel)
                }
            }

        }
        val bpdpList = originSearchList.distinctBy { it.id }.toMutableList()
        PreferenceUtils.putBpDpList(bpdpList)

        intent.putExtra(
            getString(R.string.CITY_SELECTION_TYPE),
            selectionType
        )

        startActivityForResult(
            intent,
            RESULT_CODE_SOURCE
        )
    }

    private fun selectDroppingPoint() {
        val refNo = binding.etRefNo.text.toString()
        val boardingPoint = binding.tvSelectBoardingPoint.text.toString()
    }

    private fun resendOtpAndQrCodeAPI(pnrNum: String, seatNum: String) {
        val sendOtpAndQrCodeRequest = SendOtpAndQrCodeRequest(
            bccId.toString(),
            format_type,
            resend_otp_and_qr_code_method_name,
            com.bitla.ts.domain.pojo.sendOtpAndQrCode.request.ReqBody(
                loginModelPref.api_key,
                pnrNum,
                seatNum,
                locale = locale
            )
        )
        pickUpChartViewModel.resendOtpAndQrCodeAPI(
            com.bitla.ts.domain.pojo.sendOtpAndQrCode.request.ReqBody(
                loginModelPref.api_key,
                pnrNum,
                seatNum,
                locale = locale
            ),
            resend_otp_and_qr_code_method_name
        )
    }

    private fun scanScaeen() {
        val scanner = IntentIntegrator(this)
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        scanner.setBeepEnabled(true)
        scanner.setBarcodeImageEnabled(true)
        scanner.initiateScan()

    }

    private fun resendOtpAndQrCodeObserver() {

        pickUpChartViewModel.sendOtpAndQrCodeResponse.observe(this) {
            if (it != null) {
                if (it.code == 200) {
                    this.toast(it.message)
                } else {

                    if (it.result?.message != null) {
                        it.result.message.let { it1 ->
                            this.toast(it1)
                        }
                    }
                }


            } else {
                this.toast(getString(R.string.server_error))
            }
        }
    }


    private fun selectBoardingPoint() {
        if (privilegeResponseModel?.allowBookingForAllotedServices != null && privilegeResponseModel?.allowBookingForAllotedServices!! && !isPickupDropoffChargesEnabled) {
            navigateToPassengerPaymentActivity()
        } else {
            if (binding.tvSelectBoardingPoint.text.isEmpty()) {
                PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
            }
            if (binding.tvSelectDropingPoint.text.isEmpty()) {
                PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
            }
            saveBookingCustomRequest(bookingRequest = bookingCustomRequest)
            isClickBoardingPoint = true
            val intent = Intent(this, SelectBoardingDroppingPointActivity::class.java)
            intent.putExtra(getString(R.string.tag), getString(R.string.boarding))
            intent.putExtra(getString(R.string.boarding), boardingPointList as Serializable)
            intent.putExtra(getString(R.string.dropping), droppingPointList as Serializable)
            intent.putExtra(getString(R.string.bus_type), busType)
            intent.putExtra(getString(R.string.dep_time), deptTime)
            intent.putExtra(getString(R.string.service_number), serviceNumber)
            intent.putExtra(
                getString(R.string.service_type),
                getString(R.string.proceed)
            )
            startActivity(intent)
        }
    }

    private fun editFareDialog(): AlertDialog {
        val builder = AlertDialog.Builder(context).create()
        val binding: DialogEditFareBinding =
            DialogEditFareBinding.inflate(LayoutInflater.from(context))
        editFareBinding = binding

        builder.setCancelable(false)
        tvEditTotalFare = binding.tvContent
        setNetAmount()
        setEditFareAdapter(binding)

        binding.btnGoBack.setOnClickListener {
            builder.cancel()
        }

        binding.btnConfirm.setOnClickListener {
            trackFares()

            if (countryName.equals("india", true) &&
                editFareMandatoryForAgentUser &&
                !hasFareChanges()) {
                showCustomFareUpdateToast(this, getString(R.string.update_fare_validation_dialog, currentUnmodifiedSeat))
                return@setOnClickListener
            }

            builder.cancel()

            if (editFareSeatDetails.isNotEmpty())
                editFareSeatDetails[0].isEditFareApply = true

            for (i in 0 until editFareSeatDetails.size) {
                editFareSeatDetails[0].editFareMap += Pair(
                    editFareSeatDetails[i].number.toString(),
                    editFareSeatDetails[i].editFare.toString()
                )
            }
        }
        if (editFareSeatDetails.size > 1) {
            binding.applyToAllFareCB.visible()
        } else {
            binding.applyToAllFareCB.gone()
        }

        if (editFareSeatDetails.isNotEmpty())
            binding.applyToAllFareCB.isChecked = editFareSeatDetails[0].isApplyToAll ?: false

        binding.applyToAllFareCB.setOnClickListener {
            if (editFareSeatDetails.isNotEmpty()) {
                isFirstTime = false
                editFareSeatDetails[0].isApplyToAll = binding.applyToAllFareCB.isChecked
                editFareSeatDetails[0].isEditFareApply = false
                editFareAdapter?.notifyDataSetChanged()
            }
        }

        builder.setView(binding.root)
        builder.show()
        builder.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        builder.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return builder
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (intent.hasExtra("fromTicketDetails")) {
            val intent = Intent(this, BusDetailsActivity::class.java)
            intent.putExtra("fromTicketDetails", "rebooking")
            intent.putExtra(
                getString(R.string.JOURNEY_DATE),
                PreferenceUtils.getPreference("convertedDate", "")
            )
            startActivity(intent)
        }
        if (navigateTag != null && navigateTag == ViewReservationActivity.tag) {
            val intent = Intent(this, ViewReservationActivity::class.java)
            intent.putExtra("pickUpResid", resId)
            startActivity(intent)
        } else {
            if (privilegeResponseModel?.allowToShowNewFlowInTsApp == true) {
                finish()
            }
        }


    }

    private fun setNetAmount() {
        val label = getColoredSpanned(getString(R.string.current_total_fare_is), "#4a4a4a")
        val totalFare =
            getColoredSpanned("${currencySymbol}${totalSum.convert(currencyFormat)}", "#00ADB5")
        getColoredSpanned("$currency ${(totalSum).convert(currencyFormat)}", "#00ADB5")
        val finalFareLabel = "$label $totalFare"
        if (::tvEditTotalFare.isInitialized && tvEditTotalFare != null) {
            tvEditTotalFare.text =
                HtmlCompat.fromHtml(finalFareLabel, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun getColoredSpanned(text: String, color: String): String {
        return "<font color=$color>$text</font>"
    }

    private fun setEditFareAdapter(binding: DialogEditFareBinding) {
        val layoutManager = LinearLayoutManager(
            /* context = */ this,
            /* orientation = */ LinearLayoutManager.VERTICAL,
            /* reverseLayout = */ false
        )

        binding.rvNewFare.layoutManager = layoutManager

        if (editFareSeatDetails.isNotEmpty()) {
            editFareSeatDetails[0].updatedFare = null
            editFareSeatDetails[0].isApplyToAll = false

            editFareAdapter =
                EditFareAdapter(this,
                    editFareSeatDetails = editFareSeatDetails,
                    privilegeResponseModel,
                    onItemClickListener = this,
                    object : EditFareAdapter.EditTextListener {
                        override fun isEmpty(isEmpty: Boolean) {

                        }

                        override fun getRvData(
                            position: Int,
                            fareDetail: FareDetailPerSeat,
                            isCorrectFare: Boolean,
                            minMaxFareError: String
                        ) {
                            if (fareDetail.fare != null && fareDetail.fare.isNotEmpty()) {
                                myHashMap.put(position, fareDetail)
                                onEditFareChange()
                                confirmBtnColor(isCorrectFare, minMaxFareError)
                            }
                        }
                    },
                    currencyFormat,
                    object : DialogButtonAnyDataListener {
                        override fun onDataSend(type: Int, file: Any) {
                            when (type) {
                                APPLIED_VALUE -> {
                                    try {
                                        newUpdatedprice = if (isFirstTime) {
                                            editFareSeatDetails[0].baseFareFilter.toString()
                                        } else {
                                            file as String
                                        }
                                        editFareSeatDetails[0].updatedFare = newUpdatedprice
                                    }catch (e: Exception){
                                        toast(e.message)
                                    }

                                }
                            }
                        }

                        override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {

                        }
                    }
                )
            binding.rvNewFare.adapter = editFareAdapter
        }
    }

    private fun onEditFareChange() {
        var newEditTotalFare = 0.0

        editFareSeatDetails.forEach {

            if (it.editFare != null && it.editFare.toString().isNotEmpty()) {
                try {
                    newEditTotalFare += it.editFare.toString().toDouble()
                } catch (e: Exception) {
                }
            } else {
                try {
                    newEditTotalFare += it.baseFareFilter.toString().toDouble()

                } catch (e: Exception) {
                }
            }
        }

        totalSum = roundOffDecimal(newEditTotalFare)!!
        setNetAmount()

        val totalFare =
            "${getString(R.string.netAmount)}: ${currencySymbol}${
                totalSum
            }"
        "${getString(R.string.netAmount)}: $currency ${(totalSum).convert(currencyFormat)}"
        binding.editPriceLayout.ticketPrice.text = totalFare
    }

    private fun trackFares() {
        originalFares.clear()
        originalSeatList.clear()
        modifiedFares.clear()

        editFareSeatDetails.forEach { seatDetail ->
            originalFares.add(seatDetail.baseFareFilter?.toString() ?: "0")
        }

        editFareSeatDetails.forEach { seatDetail ->
            originalSeatList.add(seatDetail.number ?: "")
        }

        editFareSeatDetails.forEach { seatDetail ->
            modifiedFares.add(seatDetail.editFare?.toString() ?: seatDetail.baseFareFilter?.toString() ?: "0")
        }

    }

    private fun navigateToPassengerPayment() {
        val originalFaresString = originalFares.joinToString(separator = ",")
        val originalSeatsString = originalSeatList.joinToString(separator = ",")
        val modifiedFaresString = modifiedFares.joinToString(separator = ",")

        PreferenceUtils.setPreference(PREF_ORIGINAL_FARE_LIST, originalFaresString)
        PreferenceUtils.setPreference(PREF_EDITED_FARE_LIST, modifiedFaresString)
        PreferenceUtils.setPreference(PREF_ORIGINAL_SEAT_LIST, originalSeatsString)
    }

    private fun hasFareChanges(): Boolean {
        if (originalFares.isEmpty() && modifiedFares.isEmpty()) return false
        if (originalFares.size != modifiedFares.size) return false
        currentUnmodifiedSeat = ""

        return originalFares.indices.all { index ->
            val original = originalFares[index].toDoubleOrNull() ?: 0.0
            val modified = modifiedFares[index].toDoubleOrNull() ?: 0.0

            val fareChanged = original != modified

            if (!fareChanged && currentUnmodifiedSeat.isEmpty()) {
                currentUnmodifiedSeat = originalSeatList[index]
            }
            fareChanged
        }
    }

    private fun checkFares(): Boolean {
        trackFares()
        return hasFareChanges()
    }

    private fun confirmBtnColor(isCorrectFare: Boolean, minMaxFareError: String) {
        if (::editFareDialogBuilder.isInitialized && editFareDialogBuilder != null) {
            val editFareConfirmButton =
                editFareDialogBuilder.findViewById<android.widget.Button>(
                    R.id.btnConfirm
                )

            val tvMinMaxError =
                editFareDialogBuilder.findViewById<TextView>(
                    R.id.tvMinMaxError
                )

            if (isCorrectFare) {
                tvMinMaxError.gone()
                tvMinMaxError.text = minMaxFareError

                editFareConfirmButton.isClickable = true
                editFareConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } else {
                if (minMaxFareError.isNotEmpty()) {
                    tvMinMaxError.visible()
                    tvMinMaxError.text = minMaxFareError
                }
                editFareConfirmButton.isClickable = false
                editFareConfirmButton.setBackgroundColor(resources.getColor(R.color.button_default_color))
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data?.getStringExtra(getString(R.string.SELECTED_CITY_TYPE)) != null) {
                data.getStringExtra(getString(R.string.SELECTED_CITY_TYPE))!!

                val selectedCityType: String =
                    data.getStringExtra(getString(R.string.SELECTED_CITY_TYPE)).toString()
                val selectedCityName: String =
                    data.getStringExtra(getString(R.string.SELECTED_CITY_NAME)).toString()
                val selectedCityId: String =
                    data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                originSearchList.clear()
                when (selectedCityType) {
                    getString(R.string.origin) -> {
                        binding.modifySearchLayout.tvSource.text = selectedCityName
                        tempSourceId = selectedCityId
                        tempSourceName = selectedCityName
                        binding.modifySearchLayout.tvDestination.text = ""
                        isCityPairOrDateModified = true
//                        source= selectedCityName
                    }

                    getString(R.string.destination) -> {
                        binding.modifySearchLayout.tvDestination.text = selectedCityName
                        tempDestinationId = selectedCityId
                        tempDestinationIdName = selectedCityName
                        isCityPairOrDateModified = true
//                        destination= selectedCityName
                    }
                }
            } else {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

                if (result != null) {
                    if (result.contents == null) {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                    } else {
                        qrresponse = result.contents

                        if (!bindingStatus.remarksLayout.isVisible || !bindingStatus.lpassengerTemp.isVisible) {
                            verifybtnobserver(
                                true,
                                "",
                                qrresponse,
                                pnrNum,
                                seatNum,
                                "",
                                "2"
                            )
                        } else {
                            temp(pnrNum, "2")
                        }
                        bindingStatus.scanqrText.text = "QR Scanned"

                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    override fun bookExtraSeats(isChecked: Boolean?, isSeatSelected: Boolean?) {
        if (isChecked != null) {
            isExtraSeatChecked = isChecked
        }

        if (isChecked == true) {
            if (isSelectedSeat) {

                binding.apply {
                    editPriceLayout.root.visible()
                    editPriceLayout.layoutExtraSeatProceed.visible()
                    editPriceLayout.btnExtraBookingProceed.visible()
                    if (isBimaServiceDetails == true) {
                        binding.editPriceLayout.fabsummary.gone()
                    } else {
                        binding.editPriceLayout.fabsummary.visible()
                    }

                    btnServiceSummary.gone()
                    layoutSummary.root.gone()
                    editPriceLayout.proceedLayout.gone()
                    editPriceLayout.editprice.gone()

                    editPriceLayout.nextBackLayout.gone()
                }

                selectedSeatDetails.forEach {
                    it.isExtraSeat = true
                }
            } else {
                binding.apply {
                    editPriceLayout.root.visible()
                    editPriceLayout.layoutExtraSeatProceed.visible()
                    btnServiceSummary.gone()
                    if (isBimaServiceDetails == true) {
                        binding.editPriceLayout.fabsummary.gone()
                    } else {
                        binding.editPriceLayout.fabsummary.visible()
                    }
                    layoutSummary.root.gone()

                    editPriceLayout.proceedLayout.gone()
                    editPriceLayout.editprice.gone()
                    editPriceLayout.nextBackLayout.gone()
                }
            }
            selectedSeatDetails.forEach {
                it.isExtraSeat = false
            }

        } else {
            if (isSelectedSeat) {
                binding.apply {
                    editPriceLayout.root.visible()
                    if (isBimaServiceDetails == true) {
                        binding.btnServiceSummary.gone()
                    } else {
                        btnServiceSummary.visible()
                    }
                    editPriceLayout.fabsummary.gone()
                    editPriceLayout.layoutExtraSeatProceed.gone()
//                    layoutSummary.root.visible()
                    layoutSummary.root.gone()
                    editPriceLayout.proceedLayout.visible()
                    editPriceLayout.editprice.visible()

                    binding.apply {
                        editPriceLayout.arrowCoachNextImg.gone()
                        editPriceLayout.arrowCoachBackImg.gone()
                        editPriceLayout.nextBackLayout.gone()
                    }
                }

            } else {
                binding.apply {
                    if (availableRoutesList != null && availableRoutesList.size == 1) {
                        binding.editPriceLayout.root.gone()
                    } else {
                        coachSwipeButtonsVisibility()
                        editPriceLayout.proceedLayout.gone()
                        editPriceLayout.editprice.gone()
                        editPriceLayout.fabsummary.gone()
                        editPriceLayout.layoutExtraSeatProceed.gone()
                    }

                    if (isBimaServiceDetails == true) {
                        binding.btnServiceSummary.gone()
                    } else {
                        btnServiceSummary.visible()
                    }
                    layoutSummary.root.gone()
                }
            }
            selectedSeatDetails.forEach {
                it.isExtraSeat = false
            }

            retrieveSelectedExtraSeats().clear()
        }
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun moveExtraSeat(isChecked: Boolean) {
        if (isApplyBPDPFare == "true") {
            callBpDpServiceApi(selectedBoarding?.id.toString(), selectedDropping?.id.toString())
        } else {
//            callServiceApi()
            isSwitchClicked = true
            hitMultistationSeatDetailApi(resId.toString(), lastSelectedSeatNumber)
        }
    }

    override fun releaseTicket(ticket: String, releaseTicket: String) {
        releaseTicketNumber = ticket.trim().substringBefore(" ")
        isReleaseTicket = releaseTicket
      //  callTicketDetailsApi()
        callTicketDetailsV1Api()

    }

    private fun callTicketDetailsV1Api() {

        val locale = PreferenceUtils.getlang()
        val numeric = if (intent.hasExtra("qrscan")) {
            intent.getBooleanExtra("qrscan", false)
        } else {
            false
        }

        try {
            parseDouble(ticketNumber.toString())
        } catch (e: NumberFormatException) {
            Timber.d(" numeric: $numeric")

//            numeric = true
        }

        Timber.d("ticketNumber : ${ticketNumber}, numeric: $numeric")

        ticketDetailsComposeViewModel.showRootProgressBar = true
        ticketDetailsComposeViewModel.ticketDetailsApi(
            apiKey = loginModelPref.api_key,
            ticketNumber = ticketNumber.toString(),
            jsonFormat = true,
            isQrScan = false,
            locale = locale,
            apiType = ticket_details_method_name,
            loadPrivs = true,
            menuPrivilege = false
        )
    }

    private fun setTicketDetailsV1Observer() {
        ticketDetailsComposeViewModel.dataTicketDetails.observe(this) {
            ticketDetailsComposeViewModel.showRootProgressBar = false

            if (it != null) {
                when (it.code) {
                    200 -> {

                        if (it.body != null && it.body?.code == 419) {
                            ticketDetailsComposeViewModel.setIsTicketDetailsApiSuccess(false)
                            // failed case


                        } else {
                            transactionFare =it.body?.transactionFare?:""
                            totalNetAmount = it.body?.totalNetAmount?:""
                            callTicketDetailsApi()

                        }
                    }

                    401 -> {
                        // openUnauthorisedDialog()
                        showUnauthorisedDialog()


                        firebaseLogEvent(
                            this,
                            TICKET_BOOKED_FAILED,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            TICKET_BOOKED_FAILED,
                            TicketBookedFailed.TICKET_BOOKED_FAILED
                        )
                        ticketDetailsComposeViewModel.setIsTicketDetailsApiSuccess(false)
                    }

                    else -> {
                        if (it.message?.isNotEmpty() == true) {
                            toast(it.message)
                            onBackPressed()
                        } else {
                            toast(getString(R.string.server_error))
                            onBackPressed()
                        }
                        firebaseLogEvent(
                            this,
                            TICKET_BOOKED_FAILED,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            TICKET_BOOKED_FAILED,
                            TICKET_BOOKED_FAILED
                        )
                        ticketDetailsComposeViewModel.setIsTicketDetailsApiSuccess(false)
                    }
                }
            } else {
                toast(getString(R.string.server_error))
                onBackPressed()
            }
        }
    }

    override fun callPassenger(ticketNumber: String, contactNumber: String) {
        val telNo = getPhoneNumber(passPhone = contactNumber, countryName)

        if (countryList.size > 0) {
            callNumber = if (contactNumber.contains("+")) {
                "+$telNo"
            } else {
                "+${countryList[0]}$telNo"
            }
            callUser()
        } else {
            toast(getString(R.string.not_valid_phone_number))
        }

        firebaseLogEvent(
            context,
            CALL_OPTION_CLICKS,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            CALL_OPTION_CLICKS,
            "Call Option Clicks - Book ticket Coach Fragment"
        )
    }

    override fun checkBoardedStatus(
        status: Boolean,
        passengerName: String,
        pnrNum: String,
        seatNumber: String,
        view: View
    ) {
        switch = view as SwitchCompat
        bindingStatus.etPassengerTemp.clearFocus()
        bindingStatus.etPassengerTemp.setText("")
        bindingStatus.etRemarksText.clearFocus()
        bindingStatus.etRemarksText.setText("")
        if (status) {
            qrresponse = ""
            boarded(passengerName, seatNumber, pnrNum)
        } else {
            unBoarded(passengerName, seatNumber, pnrNum)
        }
    }

    override fun selectedSeatCount(selectedSeats: ArrayList<SeatDetail>) {
    }

    fun unBoarded(passengerNam: String, seatNumber: String, pnrNumber: String) {
        seatNum = seatNumber
        pnrNum = pnrNumber.substringBefore("(").trim()
        passengerName = passengerNam
        bindingStatus.scanLayout.gone()
        bindingStatus.otpText.gone()
        bindingStatus.resendOtp.gone()
        bindingStatus.lenterotp.gone()
        bindingStatus.btnVerifyBoarding.text = getString(R.string.update)
        bindingStatus.bottomSheetHeader.text = getString(R.string.update)

        if (privilegeResponseModel != null) {
            val privilegeResponse = privilegeResponseModel
            privilegeResponse?.let {

                if (privilegeResponse.allowToCapturePassAndCrewTemp) {
                    bindingStatus.lpassengerTemp.visible()
                } else {
                    bindingStatus.lpassengerTemp.gone()
                }
                if (privilegeResponse.validateRemarksForBoardingStageInMobilityApp) {
                    bindingStatus.remarksLayout.visible()
                } else {
                    bindingStatus.remarksLayout.gone()
                }

            }
        } else {
            this.toast(this.getString(R.string.server_error))
        }
        temp(pnrNum, "0")
    }

    fun temp(pnrNumber: String, status: String) {
        if (bindingStatus.lenterotp.isVisible && bindingStatus.remarksLayout.isVisible && bindingStatus.lpassengerTemp.isVisible) {
            bottomSheetDialogStatus.show()

            bindingStatus.etenterOtp.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        if (qrresponse != "") {
                            verifybtnobserver(
                                true,
                                "",
                                qrresponse,
                                pnrNumber,
                                seatNum,
                                "",
                                status
                            )
                        } else {
                            verifybtnobserver(false, "", "", "", "", "", "")

                        }
                    } else {
                        if (bindingStatus.etRemarksText.text.isNullOrEmpty() || bindingStatus.etPassengerTemp.text.isNullOrEmpty()) {
                            verifybtnobserver(
                                false,
                                s.toString(),
                                "",
                                pnrNumber,
                                seatNum,
                                "",
                                status
                            )
                        } else {
                            newOtp = s.toString()
                            verifybtnobserver(
                                true,
                                s.toString(),
                                "",
                                pnrNumber,
                                seatNum,
                                "",
                                status
                            )
                        }
                    }

                }
            })
            bindingStatus.etRemarksText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            newOtp,
                            qrresponse,
                            "",//pnrNumber
                            "",
                            "",
                            ""
                        )
                    } else {
                        if (qrresponse != "") {

                            if (bindingStatus.etPassengerTemp.text.isNullOrEmpty()) {
                                verifybtnobserver(
                                    false,
                                    s.toString(),
                                    "",
                                    pnrNumber,
                                    seatNum,
                                    "",
                                    status
                                )
                            } else {
                                verifybtnobserver(
                                    true,
                                    "",
                                    qrresponse,
                                    pnrNumber,
                                    seatNum,
                                    "",
                                    status
                                )
                            }

                        } else {

                            if (bindingStatus.etenterOtp.text.isNullOrEmpty() || bindingStatus.etPassengerTemp.text.isNullOrEmpty()) {
                                verifybtnobserver(
                                    false,
                                    s.toString(),
                                    "",
                                    pnrNumber,
                                    seatNum,
                                    "",
                                    status
                                )
                            } else {
                                newOtp = bindingStatus.etenterOtp.text.toString()
                                verifybtnobserver(
                                    true,
                                    newOtp,
                                    qrresponse,
                                    pnrNumber,
                                    seatNum,
                                    "",
                                    status
                                )
                            }
                        }

                    }
                }
            })
            bindingStatus.etPassengerTemp.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            "",
                            qrresponse,
                            "",//pnrNumber
                            "",
                            "",
                            ""
                        )
                    } else {
                        if (qrresponse != "") {
                            if (bindingStatus.etRemarksText.text.isNullOrEmpty()) {
                                verifybtnobserver(
                                    false,
                                    "",
                                    "",
                                    pnrNumber,
                                    seatNum,
                                    "",
                                    status
                                )
                            } else {
                                verifybtnobserver(
                                    true,
                                    "",
                                    qrresponse,
                                    pnrNumber,
                                    seatNum,
                                    "",
                                    status
                                )
                            }

                        } else {
                            if (bindingStatus.etenterOtp.text.isNullOrEmpty() || bindingStatus.etRemarksText.text.isNullOrEmpty()) {
                                verifybtnobserver(
                                    false,
                                    "",
                                    "",
                                    pnrNumber,
                                    seatNum,
                                    "",
                                    status
                                )
                            } else {
                                verifybtnobserver(
                                    true,
                                    "",
                                    "",
                                    pnrNumber,
                                    seatNum,
                                    "",
                                    status
                                )
                            }
                        }

                    }
                }
            })
        } else if (!bindingStatus.lenterotp.isVisible && bindingStatus.remarksLayout.isVisible && bindingStatus.lpassengerTemp.isVisible) {
            bottomSheetDialogStatus.show()

            bindingStatus.etRemarksText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            "",
                            qrresponse,
                            "",//pnrNumber
                            "",
                            "",
                            ""
                        )
                    } else {
                        if (bindingStatus.etPassengerTemp.text.isNullOrEmpty()) {
                            verifybtnobserver(
                                false,
                                newOtp,
                                qrresponse,
                                "",//pnrNumber
                                "",
                                "",
                                ""
                            )
                        } else {

                            verifybtnobserver(
                                true,
                                "",
                                qrresponse,
                                pnrNumber,//pnrNumber
                                seatNum,
                                s.toString(),
                                status
                            )
                        }

                    }
                }
            })
            bindingStatus.etPassengerTemp.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            "",
                            qrresponse,
                            "",//pnrNumber
                            "",
                            "",
                            ""
                        )
                    } else {
                        if (bindingStatus.etRemarksText.text.isNullOrEmpty()) {
                            verifybtnobserver(
                                false,
                                newOtp,
                                qrresponse,
                                "",//pnrNumber
                                "",
                                "",
                                ""
                            )
                        } else {
                            verifybtnobserver(
                                true,
                                "",
                                qrresponse,
                                pnrNumber,//pnrNumber
                                seatNum,
                                s.toString(),
                                status
                            )
                        }

                    }
                }
            })
        } else if (bindingStatus.lenterotp.isVisible && !bindingStatus.remarksLayout.isVisible && bindingStatus.lpassengerTemp.isVisible) {
            bottomSheetDialogStatus.show()


            bindingStatus.etenterOtp.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            newOtp,
                            qrresponse,
                            "",//pnrNumber
                            "",
                            "",
                            ""
                        )
                    } else {
                        if (bindingStatus.etPassengerTemp.text.isNullOrEmpty()) {
                            verifybtnobserver(
                                false,
                                newOtp,
                                qrresponse,
                                "",//pnrNumber
                                "",
                                "",
                                ""
                            )
                        } else {
                            newOtp = s.toString()
                            verifybtnobserver(
                                true,
                                newOtp,
                                qrresponse,
                                pnrNumber,//pnrNumber
                                seatNum,
                                "",
                                status
                            )
                        }

                    }
                }
            })
            bindingStatus.etPassengerTemp.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            newOtp,
                            qrresponse,
                            "",//pnrNumber
                            "",
                            "",
                            ""
                        )
                    } else {

                        if (bindingStatus.etenterOtp.text.isNullOrEmpty()) {
                            verifybtnobserver(
                                false,
                                newOtp,
                                qrresponse,
                                "",//pnrNumber
                                "",
                                "",
                                ""
                            )
                        } else {
                            verifybtnobserver(
                                true,
                                "",
                                qrresponse,
                                pnrNumber,//pnrNumber
                                seatNum,
                                "",
                                status
                            )
                        }

                    }
                }
            })

        } else if (bindingStatus.lenterotp.isVisible && bindingStatus.remarksLayout.isVisible && !bindingStatus.lpassengerTemp.isVisible) {
            bottomSheetDialogStatus.show()

            bindingStatus.etenterOtp.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            newOtp,
                            qrresponse,
                            "",//pnrNumber
                            "",
                            "",
                            ""
                        )
                    } else {
                        if (bindingStatus.etRemarksText.text.isNullOrEmpty()) {
                            verifybtnobserver(
                                false,
                                newOtp,
                                qrresponse,
                                "",//pnrNumber
                                "",
                                "",
                                ""
                            )
                        } else {
                            newOtp = s.toString()
                            verifybtnobserver(
                                true,
                                newOtp,
                                qrresponse,
                                pnrNumber,//pnrNumber
                                seatNum,
                                "",
                                status
                            )
                        }

                    }
                }
            })
            bindingStatus.etRemarksText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            newOtp,
                            qrresponse,
                            "",//pnrNumber
                            "",
                            "",
                            ""
                        )
                    } else {
                        if (bindingStatus.etenterOtp.text.isNullOrEmpty()) {
                            verifybtnobserver(
                                false,
                                newOtp,
                                qrresponse,
                                "",//pnrNumber
                                "",
                                "",
                                ""
                            )
                        } else {
                            verifybtnobserver(
                                true,
                                "",
                                qrresponse,
                                pnrNumber,//pnrNumber
                                seatNum,
                                s.toString(),
                                status
                            )
                        }

                    }
                }
            })
        } else if (!bindingStatus.lenterotp.isVisible && !bindingStatus.remarksLayout.isVisible && bindingStatus.lpassengerTemp.isVisible) {
            bottomSheetDialogStatus.show()

            bindingStatus.etPassengerTemp.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            newOtp,
                            qrresponse,
                            "",//pnrNumber
                            "",
                            "",
                            ""
                        )
                    } else {
                        verifybtnobserver(
                            true,
                            "",
                            qrresponse,
                            pnrNumber,//pnrNumber
                            seatNum,
                            "",
                            status
                        )
                    }
                }
            })
        } else if (bindingStatus.lenterotp.isVisible && !bindingStatus.remarksLayout.isVisible && !bindingStatus.lpassengerTemp.isVisible) {
            bottomSheetDialogStatus.show()
            bindingStatus.etenterOtp.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    /*if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            newOtp,
                            qrresponse,
                            "",//pnrNumber
                            ""!!,
                            "",
                            ""
                        )
                    } else {
                        newOtp = s.toString()
                        verifybtnobserver(
                            true,
                            newOtp,
                            qrresponse,
                            pnrNumber,//pnrNumber
                            seatNum!!,
                            "",
                            status
                        )
                    }*/

                    newOtp = s.toString()
                    verifybtnobserver(
                        true,
                        newOtp,
                        qrresponse,
                        pnrNumber,//pnrNumber
                        seatNum,
                        "",
                        status
                    )
                }
            })

        } else if (!bindingStatus.lenterotp.isVisible && bindingStatus.remarksLayout.isVisible && !bindingStatus.lpassengerTemp.isVisible) {
            bottomSheetDialogStatus.show()
            bindingStatus.etRemarksText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        verifybtnobserver(
                            false,
                            newOtp,
                            qrresponse,
                            "",//pnrNumber
                            "",
                            "",
                            ""
                        )
                    } else {
                        verifybtnobserver(
                            true,
                            "",
                            qrresponse,
                            pnrNumber,//pnrNumber
                            seatNum,
                            "",
                            status
                        )
                    }
                }
            })
        } else if (!bindingStatus.lenterotp.isVisible && !bindingStatus.remarksLayout.isVisible && !bindingStatus.lpassengerTemp.isVisible) {
            skipQrCcode = true
            updateBoardedStatusApi("", "", pnrNum, seatNum, status, templist, "")


        }


    }


    fun boarded(passengerNam: String, seatNumber: String, pnrNumber: String) {
        seatNum = seatNumber
        pnrNum = pnrNumber.substringBefore("(").trim()
        passengerName = passengerNam
        var isBoardedSms = false
        var isBoardedQr = false
        bindingStatus.btnVerifyBoarding.text = getString(R.string.verify_boarding)
        bindingStatus.remarksLayout.gone()

        if (privilegeResponseModel!= null) {
            val privilegeResponse = privilegeResponseModel
            privilegeResponse?.let {
                isBoardedSms = privilegeResponse.sendOtpToCustomersToAuthenticateBoardingStatus
                isBoardedQr =
                    privilegeResponse.sendQrCodeToCustomersToAuthenticateBoardingStatus
                restrictSkipVerification =
                    privilegeResponse.restrictOrHideSkipVerificationOptionInTsApp


                if (isBoardedSms || isBoardedQr) {
                    bindingStatus.skipVerification.visible()
                    if (!restrictSkipVerification) {
                        bindingStatus.skipVerification.visible()
                    } else {
                        bindingStatus.skipVerification.gone()
                    }
                } else {
                    bindingStatus.skipVerification.gone()
                }

                if (privilegeResponse.allowToCapturePassAndCrewTemp) {
                    bindingStatus.lpassengerTemp.visible()
                } else {
                    bindingStatus.lpassengerTemp.gone()
                }
//                if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {
//                    bindingStatus.remarksLayout.visible()
//                } else {
//                    bindingStatus.remarksLayout.gone()
//                }
            }

        } else {
            this.toast(getString(R.string.server_error))
        }
        boardedSms = isBoardedSms

        if (!isBoardedQr && !isBoardedSms) {
            skipQrCcode = true
            bindingStatus.scanLayout.gone()
            bindingStatus.otpText.gone()
            bindingStatus.resendOtp.gone()
            bindingStatus.lenterotp.gone()

            temp(pnrNum, "2")
        } else {
            bindingStatus.etenterOtp.clearFocus()
            bindingStatus.etenterOtp.text?.clear()
            bindingStatus.etPassengerTemp.clearFocus()
            bindingStatus.etPassengerTemp.text?.clear()
            qrresponse = ""

            if (privilegeResponseModel != null) {
                val privilegeResponse = privilegeResponseModel
                privilegeResponse?.let {
                    if (privilegeResponse.allowToCapturePassAndCrewTemp) {
                        bindingStatus.lpassengerTemp.visible()
                    } else {
                        bindingStatus.lpassengerTemp.gone()
                    }
//                    if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {
//                        bindingStatus.remarksLayout.visible()
//                    } else {
//                        bindingStatus.remarksLayout.gone()
//                    }

                }
            } else {
                this.toast(this.getString(R.string.server_error))
            }

            bindingStatus.apply {
                if (isBoardedQr && isBoardedSms) {

                    resendQrImg.gone()
                    resendSmsImg.visible()
                    scanLayout.visible()
                    otpText.visible()
                    resendOtp.visible()
                    resendText.text =
                        this@NewCoachActivity.getString(R.string.resend_sms_qr_code)
                    resendQrImg.gone()
                    resendSmsImg.visible()
                    lenterotp.visible()
                    etenterOtp.clearFocus()
                    scanQrCode.setOnClickListener(this@NewCoachActivity)
                }

                if (isBoardedQr && !isBoardedSms) {
                    scanqrText.text = getString(R.string.scan_qr)
                    lenterotp.gone()
                    otpText.gone()
                    resendText.text = this@NewCoachActivity.getString(R.string.resend_qr)
                    scanQrCode.setOnClickListener(this@NewCoachActivity)
                    scanLayout.visible()
                    resendOtp.visible()
                    resendQrImg.visible()
                    resendSmsImg.gone()
                    resendQrImg.visible()
                    resendSmsImg.gone()
                }

                if (!isBoardedQr && isBoardedSms) {
                    scanLayout.gone()
                    otpText.visible()
                    resendOtp.visible()
                    resendText.text = this@NewCoachActivity.getString(R.string.resend_sms)
                    resendQrImg.gone()
                    resendSmsImg.visible()
                    lenterotp.visible()
                    etenterOtp.clearFocus()
                    scanLayout.gone()
                    resendQrImg.gone()
                    resendSmsImg.visible()
                }
            }

            temp(pnrNum, "2")

            bindingStatus.resendOtp.setOnClickListener(this)

            bindingStatus.skipVerification.setOnClickListener {

                skipQrCcode = true
                var remarks = ""
                var temp = arrayListOf<String>()



                if (privilegeResponseModel != null) {
                    val privilegeResponse = privilegeResponseModel
                    privilegeResponse?.let {

                        if (privilegeResponse.allowToCapturePassAndCrewTemp) {

                            val temp2 = bindingStatus.etPassengerTemp.text.toString()
                            if (temp2.isNullOrEmpty()) {

                                toast(getString(R.string.please_enter_temperature))
                            } else {
                                val floatTemp = temp2.toFloat()
                                if (floatTemp in 89.00..108.00) {
                                    temp =
                                        arrayListOf("${seatNum}:$floatTemp")
                                }
                            }
//
//                            if (privilegeResponse.validateRemarksForBoardingStageInMobilityApp) {
//                                if (bindingStatus.etRemarksText.text.toString().isEmpty()) {
//                                    toast(getString(R.string.enter_remarks))
//                                } else {
//                                    remarks = bindingStatus.etRemarksText.text.toString()
//                                }
//                            }

                            updateBoardedStatusApi(
                                newOtp,
                                "",
                                pnrNum,
                                seatNum,
                                "2",
                                temp,
                                remarks
                            )
                        } else {
//                            if (privilegeResponse.validateRemarksForBoardingStageInMobilityApp) {
//                                if (bindingStatus.etRemarksText.text.toString().isEmpty()) {
//                                    toast(getString(R.string.enter_remarks))
//                                } else {
//                                    remarks = bindingStatus.etRemarksText.text.toString()
//                                }
//                            }

                            updateBoardedStatusApi(
                                newOtp,
                                "",
                                pnrNum,
                                seatNum,
                                "2",
                                temp,
                                remarks
                            )

                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            }

        }
    }

    fun verifybtnobserver(
        verifybutton: Boolean,
        otp: String,
        qr: String,
        pnrNumber: String,
        seatNumber: String,
        remarks: String,
        status: String
    ) {
        skipQrCcode = false

        if (verifybutton) {

            bindingStatus.btnVerifyBoarding.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            bindingStatus.btnVerifyBoarding.setOnClickListener {
                val temp2 = bindingStatus.etPassengerTemp.text.toString()
                val remaks = bindingStatus.etRemarksText.text
                var otpEt = bindingStatus.etenterOtp.text.toString()


                val templistSingle = arrayListOf<String>()
                if (temp2.isNotEmpty()) {
                    val floatTemp = temp2.toFloat()
                    if (floatTemp in 89.00..108.00) {
                        templistSingle.add("$seatNumber:$temp2")
                        updateBoardedStatusApi(
                            otpEt,
                            qr,
                            pnrNumber,
                            seatNumber,
                            status,
                            templistSingle,
                            remaks.toString()
                        )
                    } else {
                        this.toast(this.getString(R.string.temp_range_validation))
                    }
                } else {
                    updateBoardedStatusApi(
                        otpEt,
                        qr,
                        pnrNumber,
                        seatNumber,
                        status,
                        templistSingle,
                        remaks.toString()
                    )
                }
            }
        } else {
            bindingStatus.btnVerifyBoarding.setBackgroundColor(resources.getColor(R.color.button_default_color))
            bindingStatus.btnVerifyBoarding.setOnClickListener {
                this.toast(getString(R.string.please_fill_all_the_required_details))
            }
        }
    }

    private fun updateBoardedStatusApi(
        otp: String,
        qrCode: String,
        pnrNumber: String,
        seatNumber: String,
        status: String,
        templist: List<String>,
        remarks: String
    ) {
        if (this.isNetworkAvailable()) {

//            if (status == "2") {
//                val updateBoardedStatusRequest = UpdateBoardedStatusRequest(
//                    bccId.toString(),
//                    format_type,
//                    update_boarded_status_method_name,
//                    com.bitla.ts.domain.pojo.update_boarded_status.ReqBody(
//                        api_key = loginModelPref.api_key,
//                        pnr_number = pnrNumber,
//                        seat_number = seatNumber,
//                        status = status,
//                        new_qr_code = qrCode,//Qr Code
//                        skip_qr_code = skipQrCcode,
//                        new_otp = otp,//New OTP
//                        passenger_name = passengerName,
//                        reservation_id = resId!!,
//                        temp = templist,
//                        remarks = getString(R.string.passenger_boarded_status),
//                        locale = locale
//                    )
//                )
//                pickUpChartViewModel.updateBoardedStatusAPI(
//                    loginModelPref.auth_token,
//                    loginModelPref.api_key,
//                    updateBoardedStatusRequest,
//                    update_boarded_status_method_name
//                )
//            }
//            else{
//                val updateBoardedStatusRequest = UpdateBoardedStatusRequest(
//                    bccId.toString(),
//                    format_type,
//                    update_boarded_status_method_name,
//                    com.bitla.ts.domain.pojo.update_boarded_status.ReqBody(
//                        api_key = loginModelPref.api_key,
//                        pnr_number = pnrNumber,
//                        seat_number = seatNumber,
//                        status = status,
//                        new_qr_code = qrCode,//Qr Code
//                        skip_qr_code = skipQrCcode,
//                        new_otp = otp,//New OTP
//                        passenger_name = passengerName,
//                        reservation_id = resId!!,
//                        temp = templist,
//                        remarks= remarks,
//                        locale = locale
//                    )
//                )
//                pickUpChartViewModel.updateBoardedStatusAPI(
//                    loginModelPref.auth_token,
//                    loginModelPref.api_key,
//                    updateBoardedStatusRequest,
//                    update_boarded_status_method_name
//                )
//            }

//            val updateBoardedStatusRequest = UpdateBoardedStatusRequest(
//                bccId.toString(),
//                format_type,
//                update_boarded_status_method_name,
//                com.bitla.ts.domain.pojo.update_boarded_status.ReqBody(
//                    loginModelPref.api_key,
//                    pnrNumber.substringBefore("(").trim(),
//                    seatNumber,
//                    status,
//                    qrCode,//Qr Code
//                    skipQrCcode,
//                    otp,//New OTP
//                    passengerName,
//                    resId!!,
//                    templist,
//                    remarks,
//                    locale = locale
//                )
//            )

            pickUpChartViewModel.updateBoardedStatusAPI(
                com.bitla.ts.domain.pojo.update_boarded_status.ReqBody(
                    api_key = loginModelPref.api_key,
                    pnr_number = pnrNumber,
                    seat_number = seatNumber,
                    status = status,
                    new_qr_code = qrCode,//Qr Code
                    skip_qr_code = skipQrCcode,
                    new_otp = otp,//New OTP
                    passenger_name = passengerName,
                    reservation_id = resId.toString(),
                    temp = templist,
                    remarks = bindingStatus.etRemarksText.text.toString().trim(),
                    locale = locale
                ),
                update_boarded_status_method_name
            )

        } else this.noNetworkToast()
    }


    @SuppressLint("SetTextI18n")
    private fun callTicketDetailsApi() {
        if (this.isNetworkAvailable()) {
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
            }

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
                    bccId.toString(),
                    format_type,
                    ticket_details_method_name,
                    it
                )
            }

            if (ticketDetailsRequest != null) {
                /*  ticketDetailsViewModel.ticketDetailsApi(
                      loginModelPref.auth_token,
                      loginModelPref.api_key,
                      ticketDetailsRequest,
                      ticket_details_method_name
                  )
  */
                ticketDetailsViewModel.ticketDetailsApi(
                    loginModelPref.api_key,
                    releaseTicketNumber,
                    true,
                    false, locale!!,
                    ticket_details_method_name
                )
            }
        } else this.noNetworkToast()

    }


    private fun releaseTicketFun() {
        _sheetReleaseTicketsBinding =
            SheetReleaseTicketsBinding.inflate(LayoutInflater.from(this))


        ticketDetailsViewModel.dataTicketDetails.observe(this) {
            if (!passengerDetailList.isNullOrEmpty()) {
                passengerDetailList?.clear()

            }
            if (it != null) {
                when (it.code) {
                    200 -> {

                        passengerDetailList = it.body.passengerDetails
                        if (isReleaseTicket == "true") {
                            bindingSheet.releaseTicketBtn.setOnClickListener {
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
                                    authPinDialog(releaseTicketNumber)
                                }
                            }

                            setReleaseTicketPassengerAdapter()
                            bottomSheetDialoge.show()
                        } else {
                            passengerList.clear()
                            seatList.clear()
                            selectedSeatDetails.clear()
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
                                it.body.boardingDetails?.depTime
                                    ?: getString(R.string.notAvailable)
                            dropOffTravelDate = it.body.dropOffDetails?.travelDate
                                ?: getString(R.string.notAvailable)
                            dropOffDepTime =
                                it.body.dropOffDetails?.arrTime
                                    ?: getString(R.string.notAvailable)


                            if (it.body.passengerDetails != null) {

                                for (i in 0..it.body.passengerDetails.size.minus(1)) {
                                    isShiftPassenger =
                                        it.body.passengerDetails[i]!!.canShiftTicket
                                    isCanCancelTicket = it.body.passengerDetails[i]!!.canCancel
                                    seatList.clear()
                                    selectedSeatDetails.clear()

                                    passengerContactDetailList.add(
                                        ContactDetail(
                                            "${it.body.passengerDetails[i]?.mobile}",
                                            "${it.body.passengerDetails[i]?.mobile}",
                                            "${it.body.passengerDetails[i]?.email}",
                                            "${it.body.passengerDetails[i]?.cusMobile}",
                                        )
                                    )
                                    passengerList.add(
                                        PassengerDetailsResult(
                                            true,
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
                                        seatDetail.number =
                                            it.body.passengerDetails[i]?.seatNumber ?: ""
                                        seatDetail.sex = it.body.passengerDetails[i]?.gender
                                        seatDetail.name = it.body.passengerDetails[i]?.name
                                        seatDetail.age =
                                            it.body.passengerDetails[i]?.age?.toString()
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

                            val intent = if(countryName.equals("india", true) && privilegeResponseModel?.isAgentLogin == false && it.body.booking_source.equals(
                                    getString(
                                        R.string.branch_booking_
                                    ),true)) {
                                Intent(this, NewConfirmPhoneBookingActivity::class.java)
                            }else{
                                Intent(this, ConfirmPhoneBookingActivity::class.java)

                            }
                            if (it.body?.booking_source == "Online Agent Booking" ||it.body.booking_source == "Offline Agent Booking") {
                                intent.putExtra("isOnBehalgOfAgent", true)
                            }
                            intent.putExtra("fromTicketDetailsActivity", true)
                            intent.putExtra(getString(R.string.pnr_number), pnr)
                            intent.putExtra(
                                getString(R.string.select_boarding_stage),
                                boardingStageID
                            )
                            intent.putExtra(
                                getString(R.string.total_net_amount),
                                totalNetAmount
                            )
                            intent.putExtra(
                                getString(R.string.transaction_fare),
                               transactionFare
                            )
                            intent.putExtra("reservationId",reservationId)

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
                        /*DialogUtils.unAuthorizedDialog(
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



    private fun setReleaseTicketPassengerAdapter() {

        bindingSheet.rvPassengers.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        bindingSheet.rvPassengers.adapter = ReleaseTicketPassengersListAdapter(
            this,
            passengerDetailList,
            this
        )
    }

    private fun dismissProgressBar() {
        bindingSheet.progressBarRelease.gone()
    }

    private fun authPinDialog(releaseTicketNumber: String) {
        if (shouldPhoneBlockingRelease && countryName.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = this@NewCoachActivity,
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
        } else {
            callReleaseTicketApi(releaseTicketNumber, "")
            dismissProgressBar()
        }
    }

    private fun callReleaseTicketApi(ticketNumber: String, authPin: String) {
        if (this.isNetworkAvailable()) {

            dashboardViewModel.releaseTicketAPI(
                com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBody(
                    loginModelPref.api_key,
                    ticketNumber.toString(),
                    "release ticket",
                    false,
                    Ticket(
                        selectedSeatNumber.toString()
                    ),
                    json_format,
                    locale = locale,
                    authPin = authPin
                ),
                release_phone_block_ticket_method_name
            )
        } else this.noNetworkToast()
    }

    private fun setReleaseTicketObserver(bottomSheetDialoge: BottomSheetDialog) {
        dashboardViewModel.releaseTicketResponseViewModel.observe(this) { releaseApi ->

            try {
                if (releaseApi != null) {
                    when (releaseApi.code) {
                        200 -> {
                            if (!releaseApi.key.isNullOrEmpty()) {
                                if (cancelOtpLayoutDialogOpenCount == 0) {
                                    DialogUtils.cancelOtpLayoutDialog(
                                        this,
                                        object : DialogSingleButtonListener {
                                            override fun onSingleButtonClick(str: String) {
                                                if (str == "resend") {
                                                    if (isBima) {
                                                        callReleaseBimaTicketApi()
                                                    } else {
                                                        authPinPhoneReleaseDialog()
                                                    }
                                                } else {
                                                    cancelOtp = str
                                                    binding.coachProgressBar.visible()
                                                    callConfirmOtpReleasePhoneBlockTicketApi()
                                                }
                                            }
                                        },
                                        object : DialogReturnDialogInstanceListener {
                                            override fun onReturnInstance(dialog: Any) {
                                                otpDialog = dialog as AlertDialog
                                            }
                                        },
                                        dimissAction = {
                                            cancelOtpLayoutDialogOpenCount = 0

                                        })
                                    cancelOptkey = releaseApi.key
                                    if (releaseApi.result != null && !releaseApi.result.message.isNullOrEmpty()) {
                                        toast(releaseApi.result.message)
                                    } else {
                                        toast(releaseApi.message)
                                    }
                                    cancelOtpLayoutDialogOpenCount++
                                }
                                cancelOptkey = releaseApi.key

                            }else{
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

                        }

                        401 -> {
                            showUnauthorisedDialog()

                        }

                        else -> {
                            if(releaseApi.result != null && !releaseApi.result.message.isNullOrEmpty()){
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

    private fun callConfirmOtpReleasePhoneBlockTicketApi() {
        val reqBody =
            com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.ReqBody(
                apiKey = loginModelPref.api_key,
                isFromMiddleTier = true,
                key = cancelOptkey,
                otp = cancelOtp,
                pnrNumber = ticketNumber.toString(),
                remarks = "test",
                ticket = com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.Ticket(
                    selectedSeatNumber.toString()
                )
            )

        cancelTicketViewModel.getConfirmOtpReleasePhoneBlockTicketApi(
            reqBody,
            cancellation_details_ticket_method_name
        )

        _sheetReleaseTicketsBinding.progressBarRelease.visible()
    }

    private fun setConfirmOtpReleaseObserver() {

        cancelTicketViewModel.confirmOtpReleasePhoneBlockTicketResponse.observe(this) {
            binding.coachProgressBar.gone()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        otpDialog?.dismiss()
                        DialogUtils.successfulMsgDialog(this, it.message)
                        bottomSheetDialoge?.dismiss()
                        binding.coachProgressBar.gone()
                        cancelOtpLayoutDialogOpenCount = 0
                        callServiceApi()
                       /* Handler(Looper.getMainLooper()).postDelayed({
                            intent = Intent(this, DashboardNavigateActivity::class.java)
                            intent.putExtra("newBooking", true)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }, 2100)*/


                    }

                    422 -> {
                        if (it?.message != null) {
                            toast("${it.message}")
                        }
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        binding.coachProgressBar.gone()

                    }

                    401 -> {
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        binding.coachProgressBar.gone()
                        // openUnauthorisedDialog()

                        showUnauthorisedDialog()

                    }

                    else -> {
                        toast(it.message)
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        binding.coachProgressBar.gone()
                    }
                }

            } else {
                toast(getString(R.string.server_error))
                cancelOtp = ""
            }
        }
    }

    private fun callReleaseBimaTicketApi() {

        dashboardViewModel.releaseBimaTicketAPI(
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
                authPin = ""
            )
        )
    }
    private fun authPinPhoneReleaseDialog() {
        if (phoneBlockingRelease && privilegeResponseModel?.country.equals("india",true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = this,
                fragmentManager = supportFragmentManager,
                pinSize = pinSize,
                getString(R.string.phone_block_release),
                onPinSubmitted = { pin: String ->
                    callReleaseTicketApi(releaseTicketNumber,pin)
                    dismissProgressBar()
                    selectedSeatNumber.clear()
                    currentCheckedItem.clear()
                },
                onDismiss = {
                    dismissProgressBar()
                    selectedSeatNumber.clear()

                }
            )
        } else {
            callReleaseTicketApi(releaseTicketNumber,"")
            dismissProgressBar()
            selectedSeatNumber.clear()

            currentCheckedItem.clear()
        }
    }

    override fun onClick(view: View, position: Int) {

        if (view.tag != null) {

            if (view.tag == "DATES") {
                callCoach= true
                isDateSelected = true
                oldPosition = position

                ymdDate = inputFormatToOutput(
                    dateList[position].title,
                    DATE_FORMAT_MMM_DD_EEE_YYYY,
                    DATE_FORMAT_Y_M_D
                ).replace("1970", getCurrentYear())
                travelDate = getDateDMY(ymdDate)!!

                callServiceRoutesListApi(getDateYMD(travelDate))
//                Timber.d("hhh travelDate ${travelDate} == ${ymdDate} == ${dateList[position].title}")

                if (position == 0 || position == 4) {
                    getDates(travelDate)
                } else {
                    getDates("")
                }
            }
        }

        when (view.id) {

            R.id.optionRootCL -> {
                when (view.tag) {

                    getString(R.string.edit_chart_option) -> {
                        isEditChartClicked = true
                        //  excludePassengerDetails = false
                        callServiceApi()
                        binding.coachProgressBar.visible()
                        closeToggle()
                    }


                    getString(R.string.quick_book_option) -> {
                        val intent = Intent(context, QuickBookChileActivity::class.java)

                        intent.putExtra("originID", serviceDetails?.body?.origin?.id!!.toInt())
                        intent.putExtra(
                            "destinationID",
                            serviceDetails?.body?.destination?.id!!.toInt()
                        )
                        intent.putExtra("reservationID", reservationId)
                        intent.putExtra("serviceNumber", serviceDetails?.body?.number)
                        context.startActivity(intent)
                    }


                    getString(R.string.rapid_booking_option) -> {
                        firebaseLogEvent(
                            this,
                            RAPID_BOOK,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            RAPID_BOOK,
                            "Rapid Book"
                        )

                        saveBpDpList()

                        rapidBookingDialog = DialogUtils.rapidBookingDialog(
                            boardingPoint = boardingPoint,
                            droppingPoint = droppingPoint,
                            context = this,
                            varArgListener = this
                        )!!
                    }


                    getString(R.string.view_reservation_chart) -> {
                        try {

                            PreferenceUtils.putString(
                                "reservationid",
                                "${reservationId}"
                            )
                            PreferenceUtils.putString(
                                "ViewReservation_OriginId",
                                "${serviceDetails?.body?.origin?.id}"
                            )
                            PreferenceUtils.putString(
                                "ViewReservation_DestinationId",
                                "${serviceDetails?.body?.destination?.id}"
                            )
                            PreferenceUtils.putString(
                                "ViewReservation_data",
                                "${serviceDetails?.body?.number} | ${getDateDMY(serviceDetails?.body?.travelDate!!)} | ${serviceDetails?.body?.origin?.name} - ${serviceDetails?.body?.destination?.name} | ${serviceDetails?.body?.busType}"
                            )
                            PreferenceUtils.putString(
                                "ViewReservation_date",
                                "${serviceDetails?.body?.travelDate} "
                            )
                            PreferenceUtils.setPreference(
                                PREF_RESERVATION_ID, reservationId
                            )
                            PreferenceUtils.putString(
                                "ViewReservation_name",
                                "${serviceDetails?.body?.origin?.name} - ${serviceDetails?.body?.destination?.name}"
                            )
                            PreferenceUtils.putString(
                                "ViewReservation_number",
                                "${serviceDetails?.body?.number} "
                            )
                            PreferenceUtils.putString(
                                "ViewReservation_seats",
                                "${serviceDetails?.body?.number} "
                            )


                            ymdDate =
                                getDateYMD(serviceDetails?.body?.travelDate!!.replace("/", "-"))

                            val resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
                            PreferenceUtils.putString("ViewReservation_date", ymdDate)

                            PreferenceUtils.putString(
                                "ViewReservation_data",
                                "${serviceDetails?.body?.number} | ${getDateDMY(ymdDate)} | ${serviceDetails?.body?.origin?.name} - ${serviceDetails?.body?.destination?.name} | ${serviceDetails?.body?.busType}"
                            )

                            PreferenceUtils.setPreference("BUlK_shifting", false)
                            PreferenceUtils.putString("BulkShiftBack", "")
                            PreferenceUtils.setPreference("shiftPassenger_tab", 0)
                            PreferenceUtils.setPreference(
                                "seatwiseFare",
                                "fromBulkShiftPassenger"
                            )

                            val intent = Intent(context, ViewReservationActivity::class.java)
//                                Timber.d("orifinidqw", "${dateList}")
                            intent.putExtra("pickUpResid", resID)

                            startActivity(intent)

                            firebaseLogEvent(
                                context,
                                RESERVATION_CHART,
                                loginModelPref.userName,
                                loginModelPref.travels_name,
                                loginModelPref.role,
                                RESERVATION_CHART,
                                "Reservation Chart - SRP"
                            )
                        } catch (e: Exception) {
                            //

                        }
                    }

                    getString(R.string.sms_notification_option) -> {
                        firebaseLogEvent(
                            this,
                            BOOKINGPG_SEND_SMS,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            BOOKINGPG_SEND_SMS,
                            "Send SMS"
                        )
                        PreferenceUtils.removeKey(PREF_EMPLOYEE_TYPE_OPTIONS)
                        PreferenceUtils.removeKey(PREF_SMS_TEMPLATE)
                        PreferenceUtils.removeKey(PREF_CHECKED_PNR)
                        PreferenceUtils.removeKey(PREF_SMS_PASSENGER_TYPE)
                        val intent = Intent(this, SmsNotificationActivity::class.java)
                        startActivity(intent)
                    }


                    getString(R.string.update_details_option) -> {
                        // val busDetails = "$travelDate $source - $destination $busType"
                        firebaseLogEvent(
                            this,
                            BOOKINGPG_UPDATE_DETAILS,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            BOOKINGPG_UPDATE_DETAILS,
                            "Update Details"
                        )
                        val busDetails =
                            "$serviceNumber | ${getDateDMYY(travelDate)} $deptTime | $busType"
                        val intent = Intent(context, ServiceDetailsActivity::class.java)
                        intent.putExtra(context.getString(R.string.origin), source)
                        intent.putExtra(context.getString(R.string.destination), destination)
                        intent.putExtra(context.getString(R.string.bus_type), busDetails)

                        PreferenceUtils.removeKey(context.getString(R.string.scannedUserName))
                        PreferenceUtils.removeKey(context.getString(R.string.scannedUserId))
                        PreferenceUtils.removeKey("selectedScanType")
                        PreferenceUtils.removeKey(context.getString(R.string.scan_coach))
                        PreferenceUtils.removeKey(context.getString(R.string.scan_driver_1))
                        PreferenceUtils.removeKey(context.getString(R.string.scan_driver_2))
                        PreferenceUtils.removeKey(context.getString(R.string.scan_cleaner))
                        PreferenceUtils.removeKey(context.getString(R.string.scan_contractor))

                        context.startActivity(intent)
                    }


                    getString(R.string.bus_location_option) -> {
                        gotoBusTrackingPage()
                    }

                    getString(R.string.frequent_traveller) -> {
                        val intent = Intent(this, FrequentTravellerDataActivity::class.java)
                        startActivity(intent)

                        firebaseLogEvent(
                            this,
                            FREQUENT_TRAVELLER,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            FREQUENT_TRAVELLER,
                            "Frequent Traveller"
                        )
                    }

                }
            }

            R.id.passengerNameRT -> {
                lastSelectedSeatPosition = position
                for (i in 0 until seatPassengersList.size) {
                    seatPassengersList[i].is_selected = false
                }
                seatPassengersList[position].is_selected = true

                if (pnrAdapter != null) {
                    pnrAdapter!!.updateList(seatPassengersList, lastSelectedSeatPosition)
                }

                setSeatBookingDetails(position)
            }

            else -> {
                var newEditTotalFare = 0.0
                editFareSeatDetails.forEach {

                    if (it.editFare != null && it.editFare.toString().isNotEmpty()) {
                        try {
                            newEditTotalFare += it.editFare.toString().toDouble()
                        } catch (e: NumberFormatException) {
                        }
                    } else {
                        try {
                            newEditTotalFare += it.baseFareFilter.toString().toDouble()
                        } catch (e: NumberFormatException) {
                        }
                    }
                }

                totalSum = roundOffDecimal(newEditTotalFare)!!
                setNetAmount()

                val totalFare =
                    "${getString(R.string.netAmount)}: ${currencySymbol}${
                        totalSum.convert(currencyFormat)
                    }"
                binding.editPriceLayout.ticketPrice.text = totalFare
            }
        }

    }

    private fun gotoBusTrackingPage() {
        if (privilegeResponseModel?.country.equals("India", true)) {
            val intent = Intent(this, CurrentLocationActivity::class.java)
            PreferenceUtils.putString(
                "toolbarheader",
                binding.includeHeader.toolbarHeaderText.text.toString()
            )
            PreferenceUtils.putString(
                "toolbarsubheader",
                "${serviceNumber} | ${getDateDMYY(travelDate)} | $source-$destination | ${serviceBusType} ${totalSeats}"
            )

            startActivity(intent)
        } else {
            val intent = Intent(this, BusTrackingActivity::class.java)
            intent.putExtra(
                "toolbarSubHeader",
                "$source-$destination"
            )
            intent.putExtra(
                "coachNumber",
                "${serviceDetails?.body?.coachDetails?.coachNumber}"
            )
            intent.putExtra(
                "serviceNumber",
                "${serviceDetails?.body?.number}"
            )
            intent.putExtra(
                "routeId",
                serviceDetails?.body?.routeId
            )

            intent.putExtra(
                "deptDateTime",
                serviceDetails?.body?.depTime.toString() + "T" + travelDate
            )

            startActivity(intent)
        }
    }

    private fun saveBpDpList() {
        if (sourceId.contains(":")) {
            if (sourceId.contains("-1")) {
                if (bpDpBoarding.size == 1) {
                    boardingPoint = bpDpBoarding[0].name
                    PreferenceUtils.putObject(
                        bpDpBoarding[0],
                        PREF_BOARDING_STAGE_DETAILS
                    )
                } else {
                    PreferenceUtils.putObject(
                        emptyBoarding,
                        SELECTED_BOARDING_DETAIL
                    )
                    boardingPoint = ""
                }
            } else {
                var tempSource = ""
                bpDpBoarding.forEach {
                    if (source?.contains(",") == true) {
                        val temp = source?.split(",")
                        tempSource = temp?.get(0)!!
                    }

                    if (it.name == tempSource) {
                        boardingPoint = it.name
                        val boardingPointIndex = bpDpBoarding.indexOfFirst { it.name == tempSource }
                        if (boardingPointIndex != -1) {
                            PreferenceUtils.putObject(
                                bpDpBoarding[boardingPointIndex],
                                PREF_BOARDING_STAGE_DETAILS
                            )
                            PreferenceUtils.putObject(
                                bpDpBoarding[boardingPointIndex],
                                SELECTED_BOARDING_DETAIL
                            )
                        }
                    }
                }
            }
        } else {
            if (bpDpBoarding.size == 1) {
                boardingPoint = bpDpBoarding[0].name
                PreferenceUtils.putObject(
                    bpDpBoarding[0],
                    PREF_BOARDING_STAGE_DETAILS
                )
                PreferenceUtils.putObject(
                    bpDpBoarding[0],
                    SELECTED_BOARDING_DETAIL
                )
            } else {
                PreferenceUtils.putObject(
                    emptyBoarding,
                    SELECTED_BOARDING_DETAIL
                )
                boardingPoint = ""
            }
        }


        if (destinationId.contains(":")) {
            if (destinationId.contains("-1")) {
                if (bpDpDropping.size == 1) {
                    droppingPoint = bpDpDropping[0].name
                    PreferenceUtils.putObject(
                        bpDpDropping[0],
                        PREF_DROPPING_STAGE_DETAILS
                    )
                } else {
                    PreferenceUtils.putObject(
                        emptyDropping,
                        SELECTED_DROPPING_DETAIL
                    )
                    droppingPoint = ""
                }
            } else {
                var tempDestination = ""
                bpDpDropping.forEach {
                    if (destination?.contains(",") == true) {
                        val temp = destination?.split(",")
                        tempDestination = temp?.get(0)!!
                    }

                    if (it.name == tempDestination) {
                        droppingPoint = it.name
                        PreferenceUtils.putObject(
                            bpDpDropping[0],
                            PREF_DROPPING_STAGE_DETAILS
                        )
                        PreferenceUtils.putObject(
                            bpDpDropping[0],
                            SELECTED_DROPPING_DETAIL
                        )
                    }
                }
            }
        } else {
            if (bpDpDropping.size == 1) {
                droppingPoint = bpDpDropping[0].name
                PreferenceUtils.putObject(
                    bpDpDropping[0],
                    PREF_DROPPING_STAGE_DETAILS
                )
                PreferenceUtils.putObject(
                    bpDpDropping[0],
                    SELECTED_DROPPING_DETAIL
                )
            } else {
                PreferenceUtils.putObject(
                    emptyDropping,
                    SELECTED_DROPPING_DETAIL
                )
                droppingPoint = ""
            }

        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }


    private fun callSeatWisePerSeatFareApi() {
        val fareDetailsArrayList = java.util.ArrayList<FareDetailPerSeat>()
        for ((key, value) in myHashMap) {
            fareDetailsArrayList.add(value)
        }

        val updateRateCardPerSeatRequest = UpdateRateCardPerSeatRequest(
            bccId.toString(),
            format_type,
            manage_fare_method_name,

            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request.ReqBody(
                loginModelPref.api_key,
                reservationId.toString(),
                routeId.toString(),
                "perseat",
                sourceId.toInt(),
                destinationId.toInt(),
                getDateYMD(travelDate).toString(),
                getDateYMD(travelDate).toString(),
                fareDetailsArrayList,
                locale = locale
            )
        )
        /*  pickUpChartViewModel.updateRateCardSeatWisePerSeatApi(
              loginModelPref.auth_token,
              loginModelPref.api_key,
              updateRateCardPerSeatRequest,
              manage_fare_method_name
          )*/

        pickUpChartViewModel.updateRateCardSeatWisePerSeatApi(
            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request.ReqBody(
                loginModelPref.api_key,
                reservationId.toString(),
                routeId.toString(),
                "perseat",
                sourceId.toInt(),
                destinationId.toInt(),
                getDateYMD(travelDate).toString(),
                getDateYMD(travelDate).toString(),
                fareDetailsArrayList,
                locale = locale
            ),
            manage_fare_method_name
        )
    }

    private fun setUpSeatWisePerSeatObserver() {

        pickUpChartViewModel.updateRateCardPerSeatResponse.observe(this) {
            if (it != null) {
                if (it.code == 200) {


                    DialogUtils.successfulMsgDialog(
                        this,
                        getString(R.string.fare_updated_successfully)
                    )
                    if (isApplyBPDPFare == "true") {
                        callBpDpServiceApi(
                            selectedBoarding?.id.toString(),
                            selectedDropping?.id.toString()
                        )
                    } else {
                        callServiceApi()
                    }
                    binding.editPriceLayout.tvSelectedSeats.text =
                        getString(R.string.empty)
                    binding.editPriceLayout.tvUnblockSelectedSeats.text =
                        getString(R.string.empty)
                    binding.editPriceLayout.tvProceed.text = ""
                    binding.editPriceLayout.editpriceLayout.gone()
//                    binding.layoutSummary.mainLayout.visible()
                    binding.layoutSummary.root.gone()

                    if (isBimaServiceDetails == true) {
                        binding.btnServiceSummary.gone()
                    } else {
                        binding.btnServiceSummary.visible()
                    }
                    //binding.layoutviews.visible()
                    binding.editPriceLayout.fabsummary.setOnClickListener(this)
                    onNoSeatSelection()

                } else {
                    if (it.result?.message != null) {
                        it.result.message.let { it1 ->
                            toast(it1)
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun collapseCoach(proceedClick: Boolean) {
        binding.imgCollapse.gone()
        binding.tvSeatSelected.visible()

        binding.coachProgressBar.gone()
        binding.layoutCoachSingle.gone()
        binding.editPriceLayout.root.gone()

        if (proceedClick) {
            binding.imgExpand.gone()

            binding.tvEditSeats.visible()
            binding.layoutSummary.root.gone()
            binding.layoutviews.visible()
        } else {
            binding.imgExpand.visible()

            binding.tvEditSeats.gone()
//            binding.layoutSummary.root.visible()
            binding.layoutSummary.root.gone()
            binding.layoutviews.gone()
            if (finalSeatNumbers.isNotEmpty()) {

                binding.tvSeatSelected.text = replaceBracketsString(finalSeatNumbers.toString())
                binding.tvSeatSelected.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
                binding.tvSeatSelected.backgroundTintList =
                    resources.getColorStateList(R.color.colorPrimaryTransparent)
            } else {
                binding.tvSeatSelected.text = getString(R.string.noSeatSelected)
                binding.tvSeatSelected.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.white)))
                binding.tvSeatSelected.backgroundTintList =
                    resources.getColorStateList(R.color.colorDimShadow6)
            }
        }
    }

    private fun expandCoach(editClicked: Boolean) {
        binding.imgExpand.gone()

        binding.tvSeatSelected.gone()
        binding.tvEditSeats.gone()
        binding.coachProgressBar.gone()
        binding.editPriceLayout.root.gone()
        if (editClicked) {
            binding.imgCollapse.gone()

            binding.layoutSummary.root.gone()
            binding.layoutviews.gone()
            binding.editPriceLayout.root.visible()

        } else {
            binding.imgCollapse.visible()

//            binding.layoutSummary.root.visible()
            binding.layoutSummary.root.gone()
            binding.layoutviews.gone()
            binding.editPriceLayout.root.gone()
        }


//        if (binding.tvSeatSelected.text != getString(R.string.noSeatSelected)) {
//            binding.editPriceLayout.editpriceLayout.visible()
//
//        } else {
//            binding.editPriceLayout.editpriceLayout.gone()
//        }
    }


    //collapsing the coach
    private fun onExpandClick() {
        binding.tvSeatSelected.visible()
        binding.tvEditSeats.visible()

        binding.layoutviews.gone()
        binding.coachProgressBar.gone()
        binding.layoutCoachSingle.gone()
        binding.layoutSummary.root.gone()
        binding.editPriceLayout.root.gone()

        if (binding.tvSeatSelected.text != getString(R.string.noSeatSelected)) {
            binding.tvSeatSelected.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
            binding.tvSeatSelected.backgroundTintList =
                resources.getColorStateList(R.color.colorPrimaryTransparent)
        } else {
            binding.tvSeatSelected.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.white)))
            binding.tvSeatSelected.backgroundTintList =
                resources.getColorStateList(R.color.colorDimShadow6)
        }
    }

    //expanding the coach
    private fun onCollapseClicked() {

//        binding.imgCollapse.gone()
//        binding.imgExpand.visible()

        binding.tvSeatSelected.gone()
        binding.tvEditSeats.gone()
        binding.layoutviews.gone()
        binding.coachProgressBar.gone()
//        binding.layoutSummary.root.visible()
        binding.layoutSummary.root.gone()
        binding.editPriceLayout.root.gone()

        //binding.editPriceLayout.layoutExtraSeatProceed.rootView.gone()


        if (binding.tvSeatSelected.text != getString(R.string.noSeatSelected)) {
            binding.editPriceLayout.editpriceLayout.visible()

        } else {
            binding.editPriceLayout.editpriceLayout.gone()
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } else if (str.contains(getString(R.string.block))) {

            val remarks = str.substringBefore("|")

            if (isNetworkAvailable()) {
                if (shouldSingleBlockUnblock && privilegeResponseModel?.country.equals("india", true)) {
                    DialogUtils.showFullHeightPinInputBottomSheet(
                        activity = this@NewCoachActivity,
                        fragmentManager = supportFragmentManager,
                        pinSize = pinSize,
                        getString(R.string.single_block_unblock),
                        onPinSubmitted = { pin: String ->
                            callSingleBlockUnblock(
                                resId = resId.toString(),
                                isBlock = true,
                                remarks = remarks,
                                authPin = pin
                            )
                            binding.coachProgressBar.gone()
                        },
                        onDismiss = {
                            binding.coachProgressBar.gone()
                        }
                    )
                } else {
                    callSingleBlockUnblock(
                        resId = resId.toString(),
                        isBlock = true,
                        remarks = remarks,
                        authPin = ""
                    )
                    binding.coachProgressBar.gone()
                }
            } else
                noNetworkToast()

        } else {
            try {
                selectedBoarding = PreferenceUtils.getObject(SELECTED_BOARDING_DETAIL)!!
                selectedDropping = PreferenceUtils.getObject(SELECTED_DROPPING_DETAIL)!!
                selectedSeatNumber.clear()
                binding.editPriceLayout.root.gone()
                binding.coachProgressBar.visible()
//              binding.layoutSummary.root.visible()
                binding.layoutSummary.root.gone()

                binding.boardingDroppingLayout.gone()
                binding.layoutviews.gone()
                callBpDpServiceApi(selectedBoarding?.id.toString(), selectedDropping?.id.toString())

            }catch (e: Exception){
                toast(e.message)
            }
            }
    }

    override fun onItemCheck(item: com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail?) {
        currentCheckedItem.add(item)
    }

    override fun onItemUncheck(item: com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail?) {
        currentCheckedItem.remove(item)
        if (currentCheckedItem.size == 0) {
            currentCheckedItem.clear()
            selectedSeatNumber.clear()

        }
        selectedSeatNumber.clear()

    }

    override fun onButtonClick(vararg args: Any) {
        if (args.isNotEmpty()) {
            val tag = args[0]
            when (tag) {
                getString(R.string.boarding_at) -> {
                    val intent = Intent(this, InterBDActivity::class.java)
                    intent.putExtra(getString(R.string.tag), getString(R.string.boarding))
                    PreferenceUtils.putBoarding(bpDpBoarding)
                    PreferenceUtils.putDropping(bpDpDropping)
                    intent.putExtra(getString(R.string.bus_type), serviceDetails?.body?.busType)
                    intent.putExtra(getString(R.string.dep_time), serviceDetails?.body?.depTime)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        getString(R.string.rapid_booking)
                    )
                    intent.putExtra("sourceKey", "fromNewCoachActivity")
                    startActivity(intent)
                }

                getString(R.string.drop_off_at) -> {
                    val intent = Intent(this, InterBDActivity::class.java)
                    intent.putExtra(getString(R.string.tag), getString(R.string.dropping))
                    PreferenceUtils.putBoarding(bpDpBoarding)
                    PreferenceUtils.putDropping(bpDpDropping)
                    intent.putExtra(getString(R.string.bus_type), serviceDetails?.body?.busType)
                    intent.putExtra(getString(R.string.dep_time), serviceDetails?.body?.depTime)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        getString(R.string.rapid_booking)
                    )
                    intent.putExtra("sourceKey", "fromNewCoachActivity")
                    startActivity(intent)
                }

                getString(R.string.confirm) -> {

//                      var availableSeatsCount = 0
                    var noOfTickets = ""
                    noOfTickets = args[1] as String

//                        for (i in 0 until availableRoutesList.size) {
//                            availableSeatsCount = serviceDetails?.body?.availableSeats!!
//                        }

//                      serviceApiType = getString(R.string.rapid_booking)
//                      val availableSeatLength = availableSeatsCount.toString().length

                    try {
                        if (noOfTickets.toInt() > availableSeatsCount) {
                            toast(getString(R.string.rapid_booking_seat_count))
                        } else if (noOfTickets.toInt() == 0) {
                            toast("Please enter valid number of tickets")
                        } else {
                            val dropOff =
                                PreferenceUtils.getObject<DropOffDetail>(
                                    PREF_DROPPING_STAGE_DETAILS
                                )
                            val boardingAt = PreferenceUtils.getObject<BoardingPointDetail>(
                                PREF_BOARDING_STAGE_DETAILS
                            )
                            stageDetails.forEach {
                                if (it.id.toString() == dropOff?.id) {
                                    PreferenceUtils.putObject(it, PREF_DROPPING_STAGE_DETAILS)
                                } else if (it.id.toString() == boardingAt?.id) {
                                    PreferenceUtils.putObject(it, PREF_BOARDING_STAGE_DETAILS)
                                }
                            }

                            val intent = Intent(this, QuickBookingActivity::class.java)
                            intent.putExtra("SEATS", noOfTickets)
                            Timber.d("confertmbpdp:001 $noOfTickets , $boardingPoint, $droppingPoint")
                            intent.putExtra(getString(R.string.boarding_point), boardingPoint)
                            intent.putExtra(getString(R.string.dropping_point), droppingPoint)
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        toast(getString(R.string.rapid_booking_seat_count))
                    }
                }
            }
        }
    }

    fun getBookedSeatOptions(
        seatDetail: ArrayList<com.bitla.ts.domain.pojo.multistation_data.PassengerDetail>,
        isShowHide: Boolean = true
    ) {
        getSeatData(seatDetail)
//        binding.layoutBookedSeatDetails.root.visible()
        showSeatDetailToggle()
    }


    private fun showSeatDetailToggle() {


        val view: View = findViewById<View>(R.id.layout_booked_seat_details) as ConstraintLayout


        val slideRight: Animation = AnimationUtils.loadAnimation(this, R.anim.enter_from_right)
        val slideLeft: Animation = AnimationUtils.loadAnimation(this, R.anim.exit_to_right)

        if (view.visibility == GONE) {
            view.visibility = VISIBLE
            view.startAnimation(slideRight)
            binding.seatLegendsIV.gone()
            binding.editPriceLayout.arrowCoachNextImg.gone()
            binding.editPriceLayout.arrowCoachBackImg.gone()
            binding.editPriceLayout.nextBackLayout.gone()
            binding.transparentBookedSeatsOptionsV.visible()
            binding.btnServiceSummary.gone()
        } else {
            view.visibility = GONE
            view.startAnimation(slideLeft)
            binding.transparentBookedSeatsOptionsV.gone()
            binding.btnServiceSummary.visible()
            binding.seatLegendsIV.visible()
            coachSwipeButtonsVisibility()
        }
    }

    fun closeSeatDetailToggle() {
        val view: View = findViewById<View>(R.id.layout_booked_seat_details) as ConstraintLayout
        val slideRight: Animation = AnimationUtils.loadAnimation(this, R.anim.enter_from_right)
        val slideLeft: Animation = AnimationUtils.loadAnimation(this, R.anim.exit_to_right)
        view.visibility = GONE
        view.startAnimation(slideLeft)
        binding.transparentBookedSeatsOptionsV.gone()
        binding.btnServiceSummary.visible()
        binding.seatLegendsIV.visible()
        coachSwipeButtonsVisibility()
    }


    private var seatPassengersList: ArrayList<com.bitla.ts.domain.pojo.multistation_data.PassengerDetail> =
        arrayListOf()
    private var passangerPos = -1
    private fun getSeatData(seatDetail: ArrayList<com.bitla.ts.domain.pojo.multistation_data.PassengerDetail>) {
        seatPassengersList.clear()
        lastSelectedSeatPosition = 0
        seatPassengersList = seatDetail

        /* var seatData = seatDetail.passengerDetails
         seatPassengersList?.add(seatData!!)*/
        if (seatPassengersList.isNotEmpty())
            setPassengersAdapter()

    }

    private fun setPassengersAdapter() {
        seatPassengersList[0].is_selected = true
        pnrAdapter =
            SeatPassengerAdapter(
                context,
                this,
                seatPassengersList,
                lastSelectedSeatPosition,
                this
            )
        binding.layoutBookedSeatDetails.passengerRV.adapter = pnrAdapter
        setSeatBookingDetails(lastSelectedSeatPosition)
        Timber.d("lastSelectedSeatPosition $lastSelectedSeatPosition")
    }

    fun setSeatBookingDetails(position: Int) {
        passangerPos = position
        if (position < seatPassengersList.size) {
            val data = seatPassengersList[position]
            val view = binding.layoutBookedSeatDetails
            view.pnrValueTV.text = data.ticket_no.substringBefore("(")
            view.seatValueTV.text = data.seat_no
            view.otherSeatLL.gone()
            view.shiftedSeatLL.gone()
            view.agentNameLL.gone()
            if (data.no_of_seats > 1){
                view.otherSeatLL.visible()
                val filteredSeatNumbers = data.seat_numbers.split(",").toMutableList()
                filteredSeatNumbers.remove(data.seat_no)
                val resultString = filteredSeatNumbers.joinToString(",")
                view.otherSeatValueTV.text = resultString
            }

            if(!data.shifted_by.isNullOrEmpty()){
                view.shiftedLL.visible()
                view.shiftedByValue.text = data.shifted_by
            }

            if(!data.shifted_on.isNullOrEmpty()){
                view.shiftedOnLL.visible()
                view.shiftedOnValue.text = data.shifted_on
            }

            if(!data.previous_shift_numbers.isNullOrEmpty() && (privilegeResponseModel?.country != null
                        && privilegeResponseModel?.country.equals(INDIA, true)
                        )){
                view.shiftedSeatLL.visible()
                val sharedSeatNumbers = data.previous_shift_numbers?.split(",")?.toMutableList() ?: mutableListOf()
                val resultString = sharedSeatNumbers.joinToString(",")
                view.shiftedSeatValueTV.text = resultString
            }

            view.passengerValueTV.text = data.name
            view.mobileValueTV.text = data.phone_num
            view.originValueTV.text = data.origin_name

            view.boardingValueTV.text = data.boarding_stage.substringAfter("Boarding stage:")
            view.destinationValueTV.text = data.destination_name
            if (data.drop_off_stage.isNullOrEmpty()) {
                view.droppingValueTV.text = "-"
            } else {
                view.droppingValueTV.text = data.drop_off_stage

            }
            view.bookedByValueTV.text = data.booked_by
            view.bookingDateValueTV.text = data.booked_date
            view.menuSendSms.gone()

            if(!data.travel_date.isNullOrEmpty()) {
                view.dateOfJourneyLL.visible()
                view.dateOfJourneyValueTV.text = data.travel_date
            } else {
                view.dateOfJourneyLL.gone()
            }

            if(!data.pay_mode.isNullOrEmpty()) {
                view.paymentViaLL.visible()
                view.paymentViaValueTV.text = data.pay_mode
            } else {
                view.paymentViaLL.gone()
            }

            ticketNumber = data.ticket_no.substringBefore(" ")

            if (!data.release_datetime.isNullOrEmpty()) {
                view.tillLL.visible()
                view.tillValue.text = data.release_datetime

            } else {
                view.tillLL.gone()

            }
            try {
                view.fareValueTV.text =
                    "$currency ${data.seat_fare.toDouble().convert(currencyFormat)}"
            } catch (e: Exception) {
                view.fareValueTV.text = data.seat_fare
            }

            if (privilegeResponseModel?.country != null
                && privilegeResponseModel?.country.equals(INDIA, true)
            ) {
                if (isBimaServiceDetails == true) {
                    view.boardingStatus.gone()
                } else {
                    view.boardedSwitchBox.isChecked = data.status == 2

                    if (isAllowOnlyOnce) {
                        view.boardedSwitchBox.isEnabled = (data.status == 0)
                    } else {
                        view.boardedSwitchBox.isEnabled = true
                    }
                }
            } else {
                view.boardedSwitchBox.isChecked = data.status == 2
                view.boardedSwitchBox.isEnabled = (data.status == 0)

                view.boardedSwitchBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        view.boardedSwitchBox.isEnabled = false
                    }
                }
            }

            if (data.can_confirm_phone_block || isAgentLogin || role.contains(
                    context.getString(
                        R.string.role_agent
                    )
                ) || !updatePassengerTravelStatus
            ) {
                view.boardingStatus.gone()
                view.boardingStatusView.gone()
            } else {
                if (isBimaServiceDetails == true) {
                    view.boardingStatus.gone()
                } else {
                    view.boardingStatus.visible()
                    view.boardingStatusView.visible()
                }

            }

//            if (isBimaServiceDetails == true) {
//                view.menuUpdateTicket.gone()
//            } else {
//                if (data.is_update_ticket) {
//                    view.menuUpdateTicket.visible()
//                } else {
//                    view.menuUpdateTicket.gone()
//                }
//            }

            if (data.is_update_ticket) {
                view.menuUpdateTicket.visible()
            } else {
                view.menuUpdateTicket.gone()
            }

            if (data.can_release_phone_block) {
                view.cancelPhoneBookingView.visible()
                view.cancelPhoneBooking.visible()
                view.cancelPhoneBooking.setOnClickListener {
                    releaseTicket(data.ticket_no, "true")
                }
            } else {
                view.cancelPhoneBookingView.gone()
                view.cancelPhoneBooking.gone()
            }

            if (data.can_confirm_phone_block) {
                view.confirmPhoneBookingView.visible()
                view.confirmPhoneBooking.visible()
                view.confirmPhoneBooking.setOnClickListener {
                    closeSeatDetailToggle()
                    releaseTicket(data.ticket_no, "false")
                }
            } else {
                view.confirmPhoneBookingView.gone()
                view.confirmPhoneBooking.gone()
            }

            if (isBimaServiceDetails == true) {
                view.menuMoveExtra.gone()
                view.viewMoveExtraSeat.gone()
            } else {
                if (privilegeResponseModel?.allowToMoveBookedSeatToExtraSeat == true) {
                    view.menuMoveExtra.visible()
                    view.viewMoveExtraSeat.visible()
                } else {
                    view.menuMoveExtra.gone()
                    view.viewMoveExtraSeat.gone()
                }
            }

            if (isExtraSeat) {
                if (getAvailableSeats().size > 0) {
                    view.menuMoveExtra.visible()
                    view.moveTV.text = "Move To Book Seats"
                } else {
                    view.menuMoveExtra.gone()
                }

            } else {
                view.moveTV.text = "Move To Extra Seats"
            }

            if (data.phone_num.isNullOrEmpty()) {
                view.callPassenger.gone()
            } else {
                view.callPassenger.visible()
            }
            if (data.policy_number.isNullOrEmpty()) {
                if (data.can_shift_ticket) {
                    view.menuShift.visible()
                } else {
                    view.menuShift.gone()
                }
            } else {
                view.menuShift.gone()
            }

            if (data.policy_number.isNullOrEmpty()) {
                if (data.can_shift_ticket) {
                    if (!isIndonesiaLogin && !isExtraSeat) {
                        view.menuShiftSameService.visible()
                    } else {
                        view.menuShiftSameService.gone()
                    }

                } else {
                    view.menuShiftSameService.gone()
                }
            } else {
                view.menuShiftSameService.gone()
            }


            if (data.can_cancel && !data.can_release_phone_block && PreferenceUtils.getSubAgentRole() != "true") {
                view.menuCancelTicketView.visible()
                view.menuCancelTicket.visible()
            } else {
                view.menuCancelTicket.gone()
            }


            if (privilegeResponseModel?.showUpdateRemarksLinkInTheTicketSearch == true) {
                view.menuUpdateRemark.visible()
            } else {
                view.menuUpdateRemark.gone()
            }

            if (privilegeResponseModel?.allowToSendSmsInPnrSearchPage == true) {
                view.resendSms.visible()
                view.resendSmsView.visible()
            } else {
                view.resendSms.gone()
                view.resendSmsView.gone()
            }


            if (!data.remarks.isNullOrEmpty()) {
                view.remarkLL.visible()
                view.remarkValueTV.text = data.remarks
            } else {
                view.remarkLL.gone()
            }

            if(!data.onbehalf.isNullOrEmpty() || !data.online_agent.isNullOrEmpty()) {
                if(!data.onbehalf.isNullOrEmpty()) {
                    view.agentNameLL.visible()
                    view.agentNameValueTV.text = data.onbehalf
                } else if (!data.online_agent.isNullOrEmpty()) {
                    view.agentNameLL.visible()
                    view.agentNameValueTV.text = data.online_agent
                } else {
                    view.agentNameLL.gone()
                }
            }

        }
    }

    fun showCrewDetailsBottomSheet() {
        crewDetailBottomSheetBinding =
            BottomsheetCrewDetailsCoachBinding.inflate(LayoutInflater.from(this))
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheetDialog!!.setContentView(crewDetailBottomSheetBinding!!.root)
        bottomSheetDialog!!.setCancelable(false)
        crewDetailBottomSheetBinding!!.cancelIV.setOnClickListener {
            bottomSheetDialog!!.dismiss()
        }
        setCrewData(crewDetailBottomSheetBinding!!)

        crewDetailBottomSheetBinding!!.driverOneNumberTV.setOnClickListener {
            callNumber = crewDetailsData!!.driver1Contact
            callUser()
        }
        crewDetailBottomSheetBinding!!.driverTwoNumberTV.setOnClickListener {
            callNumber = crewDetailsData!!.driver2Contact
            callUser()
        }
        crewDetailBottomSheetBinding!!.driverThreeNumberTV.setOnClickListener {
            callNumber = crewDetailsData!!.driver3contact
            callUser()
        }
        crewDetailBottomSheetBinding!!.cleanerNumberTV.setOnClickListener {
            callNumber = crewDetailsData!!.cleanerContact
            callUser()
        }
        bottomSheetDialog!!.show()
    }

    private fun showCrewDetailsBottomSheet(crewDetailsResponse: PickupChartCrewDetailsResponse) {
        val crewDetailBottomSheetBinding =
            BottomsheetCrewDetailsCoachBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheetDialog.setContentView(crewDetailBottomSheetBinding.root)
        bottomSheetDialog.setCancelable(false)

        crewDetailBottomSheetBinding.apply {

            if (privilegeResponseModel?.country != null
                && privilegeResponseModel?.country.equals(INDIA, true)
            ) {
                conductorTV.text = getString(R.string.conductor)
            } else {
                conductorTV.text = getString(R.string.contractor)
            }

            driverOneNameTV.text = crewDetailsResponse.driver1
            driverTwoNameTV.text = crewDetailsResponse.driver2
            driverThreeNameTV.text = crewDetailsResponse.driver3
            cleanerNameTV.text = crewDetailsResponse.cleaner
            conductorNameTV.text = crewDetailsResponse.attendent
            chartOperatedByNameTV.text = crewDetailsResponse.chartOperatedBy
        }


        if (!crewDetailsResponse.driver1Contact.isNullOrEmpty()) {
            crewDetailBottomSheetBinding.driverOneNumberTV.visible()
            crewDetailBottomSheetBinding.driverOneNumberTV.text = crewDetailsResponse.driver1Contact
            crewDetailBottomSheetBinding.driverOneNumberTV.setOnClickListener {
                callNumber = crewDetailsResponse.driver1Contact
                callUser()
            }

        } else {
            crewDetailBottomSheetBinding.driverOneNumberTV.gone()
        }

        if (!crewDetailsResponse.driver2Contact.isNullOrEmpty()) {
            crewDetailBottomSheetBinding.driverTwoNumberTV.visible()
            crewDetailBottomSheetBinding.driverTwoNumberTV.text = crewDetailsResponse.driver2Contact

            crewDetailBottomSheetBinding.driverTwoNumberTV.setOnClickListener {
                callNumber = crewDetailsResponse.driver2Contact
                callUser()
            }
        } else {
            crewDetailBottomSheetBinding.driverTwoNumberTV.gone()
        }

        if (!crewDetailsResponse.driver3contact.isNullOrEmpty()) {
            crewDetailBottomSheetBinding.driverThreeNumberTV.visible()
            crewDetailBottomSheetBinding.driverThreeNumberTV.text =
                crewDetailsResponse.driver3contact

            crewDetailBottomSheetBinding.driverThreeNumberTV.setOnClickListener {
                callNumber = crewDetailsResponse.driver3contact
                callUser()
            }
        } else
            crewDetailBottomSheetBinding.driverThreeNumberTV.gone()

        if (crewDetailsResponse.cleanerContact.isNotEmpty()) {
            crewDetailBottomSheetBinding.cleanerNumberTV.visible()
            crewDetailBottomSheetBinding.cleanerNumberTV.text = crewDetailsResponse.cleanerContact

            crewDetailBottomSheetBinding.cleanerNumberTV.setOnClickListener {
                callNumber = crewDetailsResponse.cleanerContact
                callUser()
            }

        } else
            crewDetailBottomSheetBinding.cleanerNumberTV.gone()


        if (crewDetailsResponse.attendentContact.isNotEmpty()) {
            crewDetailBottomSheetBinding.conductorNumberTV.visible()
            crewDetailBottomSheetBinding.conductorNumberTV.text =
                crewDetailsResponse.attendentContact

            crewDetailBottomSheetBinding.conductorNumberTV.setOnClickListener {
                callNumber = crewDetailsResponse.attendentContact
                callUser()
            }

        } else
            crewDetailBottomSheetBinding.conductorNumberTV.gone()

        crewDetailBottomSheetBinding.cancelIV.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }


    private fun callUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_PHONE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (callNumber != "") {
                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$callNumber"))
                        startActivity(intent)
                    } else {
                        toast(this.getString(R.string.error_occured))
                    }
                } else
                    toast(getString(R.string.call_permission_denied))
            }
        }
    }

    private fun setCrewData(binding: BottomsheetCrewDetailsCoachBinding) {
        if (crewDetailsData != null) {
            binding.driverOneNameTV.text =
                validateData(crewDetailsData!!.driver1, binding.driverOneNameTV)
            binding.driverTwoNameTV.text = validateData(
                crewDetailsData!!.driver2,
                binding.driverTwoNameTV
            )
            binding.driverThreeNameTV.text = validateData(
                crewDetailsData!!.driver3,
                binding.driverThreeNameTV
            )
            binding.cleanerNameTV.text = validateData(
                crewDetailsData!!.cleaner,
                binding.cleanerNameTV
            )
            binding.chartOperatedByNameTV.text = validateData(
                crewDetailsData!!.chartOperatedBy,
                binding.chartOperatedByNameTV
            )
            validatePhoneNumber(crewDetailsData!!.driver1Contact, binding.driverOneNumberTV)
            validatePhoneNumber(crewDetailsData!!.driver2Contact, binding.driverTwoNumberTV)
            validatePhoneNumber(crewDetailsData!!.driver3contact, binding.driverThreeNumberTV)
            validatePhoneNumber(crewDetailsData!!.cleanerContact, binding.cleanerNumberTV)
        } else {
            binding.driverOneNameTV.text = this.getString(R.string.not_assigned)
            binding.driverTwoNameTV.text = this.getString(R.string.not_assigned)
            binding.driverThreeNameTV.text = this.getString(R.string.not_assigned)
            binding.cleanerNameTV.text = this.getString(R.string.not_assigned)
            binding.chartOperatedByNameTV.text = this.getString(R.string.not_assigned)
            binding.driverOneNumberTV.gone()
            binding.driverTwoNumberTV.gone()
            binding.driverThreeNumberTV.gone()
            binding.cleanerNumberTV.gone()

        }


    }

    private fun validateData(data: String, view: AppCompatTextView): String {
        var msg = ""
        if (!data.isNullOrEmpty()) {
            msg = data
        } else {
            msg = this.getString(R.string.not_assigned)
            view.setTextColor(ContextCompat.getColor(this, R.color.color_user_red))
        }
        return msg
    }

    private fun validatePhoneNumber(data: String, view: AppCompatTextView) {
        if (!data.isNullOrEmpty()) {
            view.text = data
            view.visible()
        } else {
            view.gone()
        }
    }

    private fun closeToggle() {
        val slideLeft: Animation = AnimationUtils.loadAnimation(this, R.anim.exit_to_right)
        val view: View = findViewById<View>(R.id.layout_coach_options) as ConstraintLayout
        view.visibility = GONE
        view.startAnimation(slideLeft)
        binding.transparentOptionV.gone()
        if (isBimaServiceDetails == true) {
            binding.btnServiceSummary.gone()
        } else {
            binding.btnServiceSummary.visible()
        }
    }

    override fun returnData(value: Any) {
        hitMultistationSeatDetailApi(resId.toString(), lastSelectedSeatNumber)
    }

    override fun onDataSend(type: Int, file: Any) {
        when (type) {
            1 -> {
                if ((file as String) == "success") {
                    isSwitchClicked = true
                    hitMultistationSeatDetailApi(resId.toString(), lastSelectedSeatNumber)
                }
            }
        }
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {

    }

    private fun callSendSMSEmailApi(type: String) {
        val bccId = getBccId()
        loginModelPref = getLogin()
//        binding.includeProgress.progressBar.visible()

        val reqBody = com.bitla.ts.domain.pojo.send_sms_email.request.ReqBody(
            ticketNumber,
            type,
            locale = locale,
            api_key = if (privilegeResponseModel?.country.equals("india", true)) {
                loginModelPref.api_key
            } else {
                null
            } ?: "",
        )
        val sendSMSEmailRequest = SendSMSEmailRequest(
            bccId.toString(),
            format_type,
            send_sms_email_method_name,
            reqBody,
        )
        if (isNetworkAvailable()) {
            /*ticketDetailsViewModel.sendSMSEmailApi(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                sendSMSEmailRequest,
                send_sms_email_method_name
            )*/

            ticketDetailsViewModel.sendSMSEmailApi(
                reqBody,
                send_sms_email_method_name
            )
        }
    }

    private fun setSendSMSEmailObserver() {
        ticketDetailsViewModel.dataSMSEmailResponse.observe(this) {

            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            val message = it.message
                            message?.let { it1 -> toast(it1) }
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
                            if (it.message != "null")
                                it.message?.let { it1 -> toast(it1) }
                            else {
                                toast(getString(R.string.opps))

                            }
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                toast(t.message.toString())
            }
        }
    }

    private fun callMoveToNormalSeatApi(
        sms: Boolean,
        remarks: String,
        resID: Long,
        seatNo: String,
        extraSeatNo: String,
        ticketNo: String,
        authPin: String
    ) {


        if (isNetworkAvailable()) {
            val reqBody =
                MoveToNormalSeatRequest(
                    loginModelPref.api_key,
                    sms,
                    remarks,
                    resID,
                    extraSeatNo,
                    seatNo,
                    ticketNo,
                    "1",
                    authPin
                )

            moveToExtraSeatViewModel.moveToNormalSeatApi(
                reqBody,
                move_to_extra_seat
            )
        } else
            noNetworkToast()


    }

    private fun callShoutRouteCityPair() {
        bookingViewModel.shortRouteCityPairAPI(
            loginModelPref.api_key,
            resId.toString()
        )
    }

    fun shortRouteCityPairObsever() {
        bookingViewModel.shortRouteCityPair.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {

                        if (it.cities.isNotEmpty()) {
                            if (::shortRouteList.isInitialized)
                                shortRouteList.addAll(it.cities)

                            modifySearchHandling()


//                            if (!source.isNullOrEmpty()){
//                                it.cities.forEach {
//
//                                    var cityModel = SearchModel()
//                                    cityModel.id = it.origin.id.toString()
//                                    cityModel.name = it.origin.name
//
//                                    searchList.add(cityModel)
//                                    Timber.d("shotRouteApiCheck:: ${searchList}")
//
//                                }
//                            }else{
//                                it.cities.forEach {
//                                    if (it.origin.name== source){
//                                        tempSourceId= it.origin.id
//                                        var cityModel = SearchModel()
//                                        cityModel.id = it.destination.id.toString()
//                                        cityModel.name = it.destination.name
//                                        searchList.add(cityModel)
//                                    }
//
//                                }
//                            }

//                            val finalList= searchList.distinctBy { it.id }
//                            setCityAdapter(finalList.toMutableList())
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
                        it.message?.let { it1 -> toast(it1) }
                    }
                }
            }
        }
    }

    private fun openUpdateRemarksDialog() {
        DialogUtils.updateRemarkDialog(
            context = this,
            onUpdateButtonClick = { remark ->
                callDragDropRemarksUpdateApi(
                    binding.layoutBookedSeatDetails.pnrValueTV.text.toString(),
                    remark
                )
            },
            onCancelButtonClick = {
//                    toast("onCancelCalled")
            }
        )
    }

    private fun callDragDropRemarksUpdateApi(pnr: String, remark: String) {
        val reqBody = DragDropRemarksUpdateRequest(
            apiKey = loginModelPref.api_key,
            pnrNumber = pnr,
            remarks = remark
        )

        sharedViewModel.dragDropRemarksUpdate(
            reqBody,
            drag_drop_remarks_update
        )
    }

    private fun hideSeatBookingDetailsLayout() {
        val view: View =
            findViewById<View>(R.id.layout_booked_seat_details) as ConstraintLayout
        val slideLeft: Animation =
            AnimationUtils.loadAnimation(this, R.anim.exit_to_right)
        view.visibility = GONE
        view.startAnimation(slideLeft)
        binding.transparentBookedSeatsOptionsV.gone()
        binding.seatLegendsIV.visible()
        coachSwipeButtonsVisibility()

        if (isBimaServiceDetails == true) {
            binding.btnServiceSummary.gone()
        } else {
            if (finalSeatNumbers.isEmpty()) {
                binding.btnServiceSummary.visible()
            }
        }

    }

    private fun handleServiceDetailsResponse(it: ServiceDetailsModel?) {
        isServiceLoading = false
        isCityPairOrDateModified = false
        if (it != null) {
            when (it.code) {
                200 -> {
                    PreferenceUtils.removeKey("is_bima")
                    if (it.body.isBima != null) {
                        PreferenceUtils.setPreference("is_bima", it.body.isBima)
                        isBimaServiceDetails = it.body.isBima
                    } else {
                        PreferenceUtils.setPreference("is_bima", false)
                        isBimaServiceDetails = false
                    }

                    isMultiHopService = it.body.multihopService



                    PreferenceUtils.putString("parent_travel_id", it.body.parentTravelId.toString())
                    Timber.d("parentTravelId_isBima-Coach- ${it.body.parentTravelId} and ${it.body.isBima}")

                    Timber.d("isBimaServiceDetails-test = ${it.body.isBima} == $isBimaServiceDetails")

                    availableSeatsCount = it.body?.availableSeats ?: 0

                    if(PreferenceUtils.getPreference("isAllToAllFlow",false)==true){
                        binding.includeHeader.toolbarHeaderText.text="${it.body.origin?.name}-${it.body.destination?.name}"
                    }

                    val subtitle =
                        "${it.body.number} |${getDateDMMM(travelDate)} ${it.body.depTime} | ${it.body.busType}"

                    binding.includeHeader.toolbarSubtitle.text = subtitle

                    canBlockSeat = it.body.canBlockSeat ?: false
                    canUnblockSeat = it.body.canUnblockSeat ?: false

                    if(privilegeResponseModel?.country.equals("India", true)) {
                        isEnableCopyPassengerCheckbox =
                            it.body?.isEnableCopyPassengerCheckbox ?: true
                    } else {
                        isEnableCopyPassengerCheckbox = true
                    }

                    PreferenceUtils.setPreference(
                        PREF_EXTRA_ENABLE_COPY_PASSENGER,
                        isEnableCopyPassengerCheckbox
                    )


                    branchRoleDiscountType =
                        it.body.discountConfiguration?.branchRoleDiscountType ?: ""

                    PreferenceUtils.setPreference(
                        PREF_BRANCH_ROLE_DISCOUNT_TYPE,
                        branchRoleDiscountType
                    )

                    PreferenceUtils.setPreference(
                        PREF_ENABLE_CAMPAIGN_PROMOTIONS,
                        it.body.enableCampaignPromotions
                    )

                    if (privilegeResponseModel?.country.equals("India", true)) {
                        PreferenceUtils.setPreference(
                            PREF_RESERVATION_ID,
                            it.body.reservationId.toLong()
                        )
                        reservationId = it.body.reservationId.toLong()
                        resId = it.body.reservationId.toLong()
                    }

                    if (it.body.travelDate.isNullOrEmpty().not()) {
                        val temp = it.body.travelDate?.replace("/", "-")
                        PreferenceUtils.setPreference(PREF_TRAVEL_DATE, temp)
                    }
                    if (!branchRoleDiscountType.isNullOrEmpty()) {
                        if (branchRoleDiscountType != getString(R.string.none)) {
                            discountType = it.body.discountConfiguration?.discountType ?: ""
                            discountValue = it.body.discountConfiguration?.discountValue.toString()
                            if (discountValue == "null") discountValue = "0.0"

                            PreferenceUtils.setPreference(PREF_DISCOUNT_TYPE, discountType)
                        }
                    }
                    PreferenceUtils.setPreference(PREF_DISCOUNT_VALUE, discountValue)

                    //Timber.d("discountConfiguration - $branchRoleDiscountType , $discountType , $discountValue")

                    serviceDetails = it
                    if (privilegeResponseModel?.allowToShowNewFlowInTsApp == true) {
                        val fragment = CoachOptionsFragment.newInstance(isServiceBlocked)
                        addFragmentNew(fragment)
                    }
                    boardingPointList.clear()
                    droppingPointList.clear()
                    mainOpId = it.body.routeId.toString()

                    //added because these coach options depends on isBima key which comes under service details api

                    if (loginModelPref.role == getString(R.string.role_field_officer)) {
                        if (privilegeResponseModel?.country.equals(
                                "India", true
                            ) && privilegeResponseModel?.boLicenses?.showBookingAndCollectionTabInTsApp == true && (serviceDetails?.body?.isBima == null || serviceDetails?.body?.isBima == false)) {

//                Timber.d("check_BimaService = $isBimaServiceDetails")

                            if (isBimaServiceDetails == null || isBimaServiceDetails == false) {
                                coachOptionsArray.add(
                                    CoachOptionsModel(
                                        coachOption = getString(R.string.booking_option),
                                        coachOptionIcon = ContextCompat.getDrawable(
                                            this,
                                            R.drawable.booked_by_button_new_booking_flow
                                        )
                                    )
                                )

                                coachOptionsArray.add(
                                    CoachOptionsModel(
                                        coachOption = getString(R.string.collection_option),
                                        coachOptionIcon = ContextCompat.getDrawable(
                                            this,
                                            R.drawable.collection_icon_new_booking_flow
                                        )
                                    )
                                )

                                coachOptionsArray.add(
                                    CoachOptionsModel(
                                        coachOption = getString(R.string.view_multistation_bookings_option),
                                        coachOptionIcon = ContextCompat.getDrawable(
                                            this,
                                            R.drawable.view_multistation_bookings_new_booking_flow
                                        )
                                    )
                                )

                                coachOptionsArray.add(
                                    CoachOptionsModel(
                                        coachOption = getString(R.string.boarding_point_option),
                                        coachOptionIcon = ContextCompat.getDrawable(
                                            this,
                                            R.drawable.boarding_points_button_new_booking_flow
                                        )
                                    )
                                )

                                coachOptionsArray.add(
                                    CoachOptionsModel(
                                        coachOption = getString(R.string.dropping_point_option),
                                        coachOptionIcon = ContextCompat.getDrawable(
                                            this,
                                            R.drawable.dropping_points_button_new_booking_flow
                                        )
                                    )
                                )

                                coachOptionsArray.add(
                                    CoachOptionsModel(
                                        coachOption = getString(R.string.booked_by_option),
                                        coachOptionIcon = ContextCompat.getDrawable(
                                            this,
                                            R.drawable.booked_by_button_new_booking_flow
                                        )
                                    )
                                )
                            }

                        }

                    } else {

                        if (serviceDetails?.body?.isBima == null || serviceDetails?.body?.isBima == false) {
                            coachOptionsArray.add(
                                CoachOptionsModel(
                                    coachOption = getString(R.string.booking_option),
                                    coachOptionIcon = ContextCompat.getDrawable(
                                        this,
                                        R.drawable.booked_by_button_new_booking_flow
                                    )
                                )
                            )

                            coachOptionsArray.add(
                                CoachOptionsModel(
                                    coachOption = getString(R.string.collection_option),
                                    coachOptionIcon = ContextCompat.getDrawable(
                                        this,
                                        R.drawable.collection_icon_new_booking_flow
                                    )
                                )
                            )

                            coachOptionsArray.add(
                                CoachOptionsModel(
                                    coachOption = getString(R.string.view_multistation_bookings_option),
                                    coachOptionIcon = ContextCompat.getDrawable(
                                        this,
                                        R.drawable.view_multistation_bookings_new_booking_flow
                                    )
                                )
                            )

                            coachOptionsArray.add(
                                CoachOptionsModel(
                                    coachOption = getString(R.string.boarding_point_option),
                                    coachOptionIcon = ContextCompat.getDrawable(
                                        this,
                                        R.drawable.boarding_points_button_new_booking_flow
                                    )
                                )
                            )

                            coachOptionsArray.add(
                                CoachOptionsModel(
                                    coachOption = getString(R.string.dropping_point_option),
                                    coachOptionIcon = ContextCompat.getDrawable(
                                        this,
                                        R.drawable.dropping_points_button_new_booking_flow
                                    )
                                )
                            )

                            coachOptionsArray.add(
                                CoachOptionsModel(
                                    coachOption = getString(R.string.booked_by_option),
                                    coachOptionIcon = ContextCompat.getDrawable(
                                        this,
                                        R.drawable.booked_by_button_new_booking_flow
                                    )
                                )
                            )
                        }

                    }

                    singleViewModel.setMealInfo(it.body)
                    getBpDpPointsLists(serviceDetails?.body?.stageDetails)

                    binding.coachProgressBar.gone()
                    binding.layoutCoachSingle.visible()
                    commonCoach =
                        supportFragmentManager.findFragmentById(R.id.layout_coach_single) as AllCoachFragment

                    if (callCoach){
                        callCoach = false
                        if (::commonCoach.isInitialized) {
                            commonCoach.updateFromCoach()
                            commonCoach.setCoachData(it.body)
                            if (previousScreen != null && previousScreen == BusDetailsActivity.TAG || previousScreen == TicketDetailsActivityCompose.tag || previousScreen == TicketDetailsActivity.tag) {
                                binding.editPriceLayout.proceedLayout.gone()
                                binding.editPriceLayout.layoutExtraSeatProceed.gone()
                                binding.editPriceLayout.editprice.gone()
                                binding.editPriceLayout.fabsummary.gone()
                                if (isBimaServiceDetails == true) {
                                    binding.btnServiceSummary.gone()
                                } else {
                                    binding.btnServiceSummary.visible()
                                }
                                coachSwipeButtonsVisibility()
                                commonCoach.binding.layoutCrewI.root.visible()
                            } else {
                                commonCoach.binding.layoutCrewI.root.gone()
                            }
                        }
                    }


                    if (it.body.isBima == true && privilegeResponseModel?.chartSharedPrivilege?.isNotEmpty() == true) {
                        if (privilegeResponseModel?.chartSharedPrivilege?.get(0)?.privileges?.allow_to_book_extra_seat == true) {
                            commonCoach.binding.layoutBlockAllSeats.visible()
                        } else {
                            commonCoach.binding.layoutBlockAllSeats.gone()
                        }
                    }


//                    if (isBima) {
//                        commonCoach.binding.layoutBlockAllSeats.gone()
//                    }

                    if (it.body.isBima == true) {
                        binding.editPriceLayout.tvEditPrice.gone()
                        binding.editPriceLayout.editFareIV.gone()
                    } else {
                        if (it.body?.isOwnRoute != null) {
                            isOwnRoute = it.body.isOwnRoute!!
                            if (privilegeResponseModel != null) {
                                if ((isOwnRoute && privilegeResponseModel?.isAllowedToEditFare == true || privilegeResponseModel?.isAllowedEditFare == true) || (!isOwnRoute && privilegeResponseModel?.isAllowedToEditFareForOtherRoute == true)) {
//                                        binding.editPriceLayout.tvEditPrice.visible()
                                    binding.editPriceLayout.editFareIV.visible()
                                } else {
//                                        binding.editPriceLayout.tvEditPrice.gone()
                                    binding.editPriceLayout.editFareIV.gone()
                                }
                            }
                            PreferenceUtils.setPreference(PREF_IS_OWN_ROUTE, isOwnRoute)
                        }
                    }

                    serviceNumber = it.body.number ?: getString(R.string.empty)
                    if (serviceNumber.isNotEmpty()) {
                        binding.layoutCoachOptions.serviceTV.text =
                            "${getString(R.string.service_options)} ${getString(R.string.for_)} $serviceNumber"
                    } else {
                        binding.layoutCoachOptions.serviceTV.text =
                            getString(R.string.service_options)

                    }
                    serviceTravelDate = it.body.travelDate ?: ""
                    serviceBusType = it.body.busType ?: ""
                    totalSeats = it.body.coachDetails?.totalSeats.toString()
                    legendDetails = it.body.legendDetails ?: arrayListOf<LegendDetail>()
                    coachDetails = it.body.coachDetails ?: CoachDetails()
                    branchLegendDetails = it.body.branchLegendDetails ?: arrayListOf()
                    if (branchLegendDetails.isNullOrEmpty()) {
                        binding.layoutPassengerDetailsSeatLegends.branchLegendsLayout.gone()
                    } else {
                        binding.layoutPassengerDetailsSeatLegends.branchLegendsLayout.visible()
                        setSeatLegendsAdapter(true, branchLegendDetails)
                    }

                    it.body.routeId?.let { it1 -> setRouteId(it1, it.body.isGstApplicable) }

                    if (it.body.allFareDetails != null && it.body.allFareDetails?.isNotEmpty() == true) {
                        minExtraSeatFare =
                            it.body.allFareDetails?.get(0).toString().toDouble().roundToInt()
                    }

                    binding.coachProgressBar.gone()
                    boardingPointList = mutableListOf()
                    droppingPointList = mutableListOf()
                    stageDetails = it.body.stageDetails ?: arrayListOf()
                    val legends = it.body.legendDetails

                    if (isApplyBPDPFare == "true" && isPickupDropoffChargesEnabled && privilegeResponseModel?.country.equals("Vietnam")) {
                        if (intent.hasExtra("pickupAddressCharge") && intent?.extras?.get("pickupAddressCharge") != null) {
                            pickupChargeDetails =
                                intent?.extras?.get("pickupAddressCharge") as ChargeDetails
                        }
                        if (intent.hasExtra("dropoffAddressCharge") && intent?.extras?.get("dropoffAddressCharge") != null) {
                            dropoffChargeDetails =
                                intent?.extras?.get("dropoffAddressCharge") as ChargeDetails
                        }
                        val selectedBp =
                            stageDetails.filter { it.id.toString() == selectedBoarding?.id }
                        val selectedDp =
                            stageDetails.filter { it.id.toString() == selectedDropping?.id }

                        if (!pickupChargeDetails.address.isNullOrEmpty() && pickupChargeDetails?.address?.matches(ADDRESS_PATTERN_CHECK.toRegex()) == true)
                            pickupChargeDetails.charge = selectedBp[0].pickupCharge
                        if (!dropoffChargeDetails.address.isNullOrEmpty() && dropoffChargeDetails?.address?.matches(ADDRESS_PATTERN_CHECK.toRegex()) == true)
                            dropoffChargeDetails.charge = selectedDp[0].dropoffCharge

                    }

                    setSeatLegendsAdapter(false, legendDetails)

                    var temp = ""

                    if (isBimaServiceDetails != null && isBimaServiceDetails == true) {
                        temp = if (sourceId.contains("@")) {
                            val tempSource = sourceId.split("@")
                            tempSource[1]
                        } else {
                            sourceId
                        }
                    } else {
                        temp = if (sourceId.contains("@")) {
                            if (sourceId.contains("@")) {
                                val tempSource = sourceId.split("@")
                                tempSource[0]
                            } else {
                                sourceId
                            }
                        } else {
                            if (sourceId.contains(":")) {
                                val tempSource = sourceId.split(":")
                                tempSource[1]
                            } else {
                                sourceId
                            }
                        }
                    }

                    Timber.d("sourceIdNewTest - $sourceId == $destinationId == temp = $temp")

                    if (!allowBookingForAllotedServices) {
                        stageDetails.forEach {
                            if (it.cityId.toString() == temp) {
                                boardingPointList.add(it)
                                setBoardingList(boardings = boardingPointList)
                                PreferenceUtils.putObject(it, PREF_BOARDING_STAGE_DETAILS)
                            } else {
                                droppingPointList.add(it)
                                setDroppingList(droppings = droppingPointList)
                                PreferenceUtils.putObject(
                                    droppingPointList[0],
                                    PREF_DROPPING_STAGE_DETAILS
                                )
                            }
                        }
                    }

                    if (it.body.idTypesArr != null && it.body.idTypesArr!!.isNotEmpty()) {
                        val idTypesItemList = mutableListOf<SpinnerItems>()
                        for (i in 0..it.body.idTypesArr?.size?.minus(1)!!) {
                            if (it.body.idTypesArr!![i].isNotEmpty()) {
                                idTypesItemList.add(
                                    SpinnerItems(
                                        it.body.idTypesArr!![i][1].toInt(),
                                        it.body.idTypesArr!![i][0]
                                    )
                                )
                                setIdTypesList(idTypesItemList)
                            }
                        }
                    }

                    binding.apply {
//                            passengerPageProceedBtn.visible()
                        nestedScrollView.visible()
                        noData.root.gone()
                    }

                    if (isBimaServiceDetails != null && isBimaServiceDetails!!) {
                        binding.layoutSummary.root.gone()
                        binding.btnServiceSummary.gone()
                    } else {
                        binding.btnServiceSummary.visible()
                        binding.layoutSummary.root.visible()
                    }

                    if(binding.editPriceLayout.fabsummary.isVisible) {
                        binding.btnServiceSummary.gone()
                    }

                    if (isEditChartClicked) {
                        isEditChartClicked = false
                        binding.coachProgressBar.gone()
                        callCoach = true
                        saveBpDpList()
                        val availableSeatList = mutableListOf<String>()

                        val passengerList = mutableListOf<PassengerDetails>()
                        val seatDetails: List<SeatDetail>? =
                            serviceDetails?.body?.coachDetails?.seatDetails
                        seatDetails?.forEach { it ->
                            if (serviceDetails != null) {
                                if (it.available!! && it.isBlocked == false)
                                    availableSeatList.add(
                                        "${it.number} ($currency${
                                            it.fare.toString().toDouble()
                                                .convert(currencyFormat)
                                        })"
                                    )
                                if (it.passengerDetails != null) {
                                    passengerList.add(it.passengerDetails!!)
                                }
                            }
                        }
                        availableSeats(availableSeats = availableSeatList)
                        setPassengerDetails(passengerDetails = passengerList)
                        val intent = Intent(this, EditChartActivity::class.java)
                        startActivity(intent)
                    }
                    val assetId = it?.body?.rAssetId ?: ""
                    if(assetId != "") {
                        if (isNetworkAvailable()) {
                            trackingoApiCall(assetId)
                        } else {
                            noNetworkToast()
                        }
                    } else {
                        binding.includeHeader.currentLocationLL.gone()
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
                    binding.apply {
                        btnServiceSummary.gone()
                        layoutPassengerDetailsSeatLegends.root.gone()
                        nestedScrollView.gone()

                        noData.root.visible()
                        noData.tvNoData.text = "${it.message}"
                    }

                    it.message?.let { it1 -> Timber.d(it1) }
                }
            }

        } else {
            binding.apply {
                btnServiceSummary.gone()
                layoutPassengerDetailsSeatLegends.root.gone()
                nestedScrollView.gone()

                noData.root.visible()
                noData.tvNoData.text = getString(R.string.something_went_wrong)
            }
        }
    }

    private fun trackingoApiCall(assetId: String){
        if (!assetId.isNullOrEmpty()) {
            val loginModel = PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER)
            loginModel?.let {
                val trackingoApiKey = it.trackingo_api_key
                val trackingoUrl = it.trackingo_url
                if (!trackingoApiKey.isNullOrEmpty() && !trackingoUrl.isNullOrEmpty()) {
                    callTrackingoApi(
                        baseUrl = trackingoUrl,
                        apiKey = trackingoApiKey,
                        assetId = assetId
                    )

                }
            }
        }
    }

    private fun setTodayDate() {
        when {
            binding.modifySearchLayout.tvSource.text.isEmpty() -> context.toast("Please select source city")
            binding.modifySearchLayout.tvDestination.text.isEmpty() -> context.toast("Please select destination city")
            else -> {
                binding.modifySearchLayout.tvSelectDate.text =
                    thFormatDateMMMOutput(getDateYMD(getTodayDate()))
                todayDateColor()
                travelDateModifyLayout = getDateYMD(getTodayDate())

                isCityPairOrDateModified = true
            }
        }
    }

    private fun setTomorrowDate() {
        if (binding.modifySearchLayout.tvSource.text.isEmpty()) context.toast(
            context.getString(
                R.string.validate_source
            )
        )
        else if (binding.modifySearchLayout.tvDestination.text.isEmpty()) context.toast(
            context.getString(
                R.string.validate_destination
            )
        )
        else {
            tomorrowDateColor()
            binding.modifySearchLayout.tvSelectDate.text =
                thFormatDateMMMOutput(getDateYMD(getTomorrowDate()))
            binding.modifySearchLayout.tvSelectReturnDate.text = getString(R.string.empty)
            travelDateModifyLayout = getDateYMD(getTomorrowDate())

            isCityPairOrDateModified = true
            modifySearchButtonClick()
        }
    }

    private fun tomorrowDateColor() {
        binding.modifySearchLayout.tvTodayDate.setTextColor(context.resources.getColor(R.color.button_default_color))
        binding.modifySearchLayout.tvTomorrowDate.setTextColor(context.resources.getColor(R.color.colorPrimary))
    }

    private fun todayDateColor() {
        binding.modifySearchLayout.tvTodayDate.setTextColor(context.resources.getColor(R.color.colorPrimary))
        binding.modifySearchLayout.tvTomorrowDate.setTextColor(context.resources.getColor(R.color.button_default_color))
    }

    private fun modifySearchButtonClick() {

        if (!binding.modifySearchLayout.tvSource.text.isNullOrEmpty() && !binding.modifySearchLayout.tvDestination.text.isNullOrEmpty()) {
            sourceId = tempSourceId
            source = tempSourceName
            destinationId = tempDestinationId
            destination = tempDestinationIdName

            PreferenceUtils.putString(PREF_SOURCE_ID, sourceId)
            PreferenceUtils.putString(PREF_DESTINATION_ID, destinationId)
            PreferenceUtils.putString(PREF_SOURCE, source)
            PreferenceUtils.putString(PREF_DESTINATION, destination)

            closeChageStartionLayout()

            callCoach = true
            binding.coachProgressBar.visible()
            if (isApplyBPDPFare == "true") {
                callBpDpServiceApi(
                    sourceId.split(":").get(0),
                    destinationId.split(":").get(0)
                )
            } else {
                if (privilegeResponseModel?.country.equals("india", true)) {
                    if (isCityPairOrDateModified) {
                        callServiceDetailsByRouteIdApi()
                    } else {
                        callServiceApi()
                    }
                } else {
                    callServiceApi()
                }
            }
            //callServiceApi()

            setToolbarTitle()

            binding.editPriceLayout.arrowCoachNextImg.gone()
            binding.editPriceLayout.arrowCoachBackImg.gone()
            binding.editPriceLayout.nextBackLayout.gone()

        } else {
            toast(getString(R.string.please_fill_all_the_required_details))
        }
    }

    private fun coachSwipeButtonsVisibility() {

//        Timber.d("availableRoutesList_size-- ${availableRoutesList.size}")

        binding.apply {
            if (privilegeResponseModel?.country.equals("India", true)) {

                if (role == getString(R.string.role_field_officer)
                ) {
                    if (allowBookingForAllServices && isAllowToDoNextAndPreviousDatesServices) {

                        binding.includeHeader.headerLL.gone()

                        if (availableRoutesList != null && availableRoutesList.size == 1) {
                            editPriceLayout.root.gone()
                            editPriceLayout.arrowCoachNextImg.gone()
                            editPriceLayout.arrowCoachBackImg.gone()
                            editPriceLayout.nextBackLayout.gone()
                            binding.rvDateDetails.gone()
                        } else {
                            editPriceLayout.arrowCoachNextImg.visible()
                            editPriceLayout.arrowCoachBackImg.visible()
                            editPriceLayout.nextBackLayout.visible()
                            editPriceLayout.root.visible()
                            binding.rvDateDetails.visible()
                        }
                    } else {
                        editPriceLayout.root.gone()
                        binding.rvDateDetails.gone()
                    }
                } else {
                    if (isAllowToDoNextAndPreviousDatesServices) {

                        binding.includeHeader.headerLL.gone()
                        if (!availableRoutesList.isNullOrEmpty() && availableRoutesList.size == 1) {
                            editPriceLayout.root.gone()
                            editPriceLayout.arrowCoachNextImg.gone()
                            editPriceLayout.arrowCoachBackImg.gone()
                            editPriceLayout.nextBackLayout.gone()
                            binding.rvDateDetails.gone()
                        } else {
                            editPriceLayout.arrowCoachNextImg.visible()
                            editPriceLayout.arrowCoachBackImg.visible()
                            editPriceLayout.nextBackLayout.visible()
                            editPriceLayout.root.visible()
                            binding.rvDateDetails.visible()
                        }
                    } else {
                        editPriceLayout.root.gone()
                        binding.rvDateDetails.gone()
                    }
                }

            } else {
                editPriceLayout.root.gone()
                editPriceLayout.arrowCoachNextImg.gone()
                editPriceLayout.arrowCoachBackImg.gone()
                editPriceLayout.nextBackLayout.gone()
                binding.rvDateDetails.gone()
            }
        }
    }

    override fun onOptionMenuClick(option: String) {
        when (option) {
            getString(R.string.seat_wise_fare) -> {
                navigateToSeatWiseFare()
            }

            getString(R.string.block) -> {
                DialogUtils.releaseTicketDialog(
                    this,
                    "",
                    "",
                    this,
                    isSingleBlockUnblock = true,
                    blockReasonsList
                )
                firebaseLogEvent(
                    context,
                    BLOCK_UNBLOCK,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    BLOCK_UNBLOCK,
                    BlockUnblock.BLOCK
                )
            }

            getString(R.string.unblock) -> {
                if (isNetworkAvailable()) {
                    if (shouldTicketMoveToSeatExtraSeat && countryName.equals(
                            "india", true
                        )
                    ) {
                        DialogUtils.showFullHeightPinInputBottomSheet(
                            activity = this@NewCoachActivity,
                            fragmentManager = supportFragmentManager,
                            pinSize,
                            getString(R.string.single_block_unblock),
                            onPinSubmitted = { pin: String ->
                                callSingleBlockUnblock(
                                    resId = resId.toString(),
                                    isBlock = false,
                                    authPin = pin
                                )
                                binding.coachProgressBar.gone()
                            },
                            onDismiss = {
                                binding.coachProgressBar.gone()
                            }
                        )
                    } else {
                        callSingleBlockUnblock(
                            resId = resId.toString(),
                            isBlock = false,
                            authPin = ""
                        )
                        binding.coachProgressBar.gone()
                    }
                }
                else
                    noNetworkToast()
            }

            getString(R.string.update_details_option) -> {
                navigateToUpdateDetails()
            }

            getString(R.string.send_sms) -> {
                navigateToSendSms()
            }

            getString(R.string.pick_up_chart) -> {
                navigateToViewReservationChart()
            }
        }
    }

    private fun navigateToSeatWiseFare() {
        try {
            val busDetails =
                "${serviceDetails?.body?.number} | ${serviceDetails?.body?.travelDate} ${serviceDetails?.body?.origin?.name} - ${serviceDetails?.body?.destination?.name} ${serviceDetails?.body?.busType} "
            PreferenceUtils.putString(
                context.getString(R.string.updateRateCard_resId),
                "$reservationId"
            )

            PreferenceUtils.putString(
                context.getString(R.string.updateRateCard_origin),
                serviceDetails?.body?.origin?.name
            )
            PreferenceUtils.putString(
                context.getString(R.string.updateRateCard_destination),
                serviceDetails?.body?.destination?.name
            )
            PreferenceUtils.putString(
                context.getString(R.string.updateRateCard_originId),
                serviceDetails?.body?.origin?.id
            )
            PreferenceUtils.putString(
                context.getString(R.string.updateRateCard_destinationId),
                serviceDetails?.body?.destination?.id
            )
            PreferenceUtils.putString(
                context.getString(R.string.updateRateCard_busType),
                busDetails
            )


            if (serviceDetails?.body?.travelDate?.contains("-") == true) {
                val date = serviceDetails?.body?.travelDate!!.split("-")
                val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                PreferenceUtils.putString(
                    context.getString(R.string.updateRateCard_travelDate),
                    finalDate
                )
            } else {
                val date = serviceDetails?.body?.travelDate!!.split("/")
                val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                PreferenceUtils.putString(
                    context.getString(R.string.updateRateCard_travelDate),
                    finalDate
                )
            }

            val intent = Intent(context, SeatWiseFareActivity::class.java)
            intent.putExtra(context.getString(R.string.bus_type), busDetails)

            seatWiseResultLauncher.launch(intent)

        } catch (e: Exception) {
            context.toast(e.message.toString())
        }
    }

    private val seatWiseResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val callCoach = result.data?.getBooleanExtra("call_coach", false) ?: false
            if (callCoach) {
                this.callCoach = true
            }
        }
    }

    private fun navigateToViewReservationChart() {
        try {

            PreferenceUtils.putString(
                "reservationid", "${reservationId}"
            )
            PreferenceUtils.putString(
                "ViewReservation_OriginId", "${serviceDetails?.body?.origin?.id}"
            )
            PreferenceUtils.putString(
                "ViewReservation_DestinationId", "${serviceDetails?.body?.destination?.id}"
            )
            PreferenceUtils.putString(
                "ViewReservation_data",
                "${serviceDetails?.body?.number} | ${getDateDMY(serviceDetails?.body?.travelDate!!)} | ${serviceDetails?.body?.origin?.name} - ${serviceDetails?.body?.destination?.name} | ${serviceDetails?.body?.busType}"
            )
            PreferenceUtils.putString(
                "ViewReservation_date", "${serviceDetails?.body?.travelDate} "
            )
            PreferenceUtils.setPreference(
                PREF_RESERVATION_ID, reservationId
            )
            PreferenceUtils.putString(
                "ViewReservation_name",
                "${serviceDetails?.body?.origin?.name} - ${serviceDetails?.body?.destination?.name}"
            )
            PreferenceUtils.putString(
                "ViewReservation_number", "${serviceDetails?.body?.number} "
            )
            PreferenceUtils.putString(
                "ViewReservation_seats", "${serviceDetails?.body?.number} "
            )


            ymdDate = getDateYMD(serviceDetails?.body?.travelDate!!.replace("/", "-"))

            val resID = PreferenceUtils.getPreference(
                PREF_RESERVATION_ID, 0
            ).toString()
            PreferenceUtils.putString("ViewReservation_date", ymdDate)


            PreferenceUtils.putString(
                "ViewReservation_data",
                "${serviceDetails?.body?.number} | ${getDateDMY(ymdDate)} | ${serviceDetails?.body?.origin?.name} - ${serviceDetails?.body?.destination?.name} | ${serviceDetails?.body?.busType}"
            )

            PreferenceUtils.setPreference("BUlK_shifting", false)
            PreferenceUtils.putString("BulkShiftBack", "")
            PreferenceUtils.setPreference("shiftPassenger_tab", 0)
            PreferenceUtils.setPreference(
                "seatwiseFare", "fromBulkShiftPassenger"
            )

            val intent = Intent(context, ViewReservationActivity::class.java)
            intent.putExtra("pickUpResid", resID)

            startActivity(intent)

            firebaseLogEvent(
                context,
                RESERVATION_CHART,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                RESERVATION_CHART,
                "Reservation Chart - SRP"
            )
        } catch (e: Exception) {
        }
    }

    private fun navigateToSendSms() {
        firebaseLogEvent(
            this,
            BOOKINGPG_SEND_SMS,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            BOOKINGPG_SEND_SMS,
            "Send SMS"
        )
        PreferenceUtils.removeKey(PREF_EMPLOYEE_TYPE_OPTIONS)
        PreferenceUtils.removeKey(PREF_SMS_TEMPLATE)
        PreferenceUtils.removeKey(PREF_CHECKED_PNR)
        PreferenceUtils.removeKey(PREF_SMS_PASSENGER_TYPE)
        val intent = Intent(this, SmsNotificationActivity::class.java)
        startActivity(intent)

    }

    private fun navigateToUpdateDetails() {
        firebaseLogEvent(
            this,
            BOOKINGPG_UPDATE_DETAILS,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            BOOKINGPG_UPDATE_DETAILS,
            "Update Details"
        )
        val busDetails = "$serviceNumber | ${getDateDMYY(travelDate)} $deptTime | $busType"
        val intent = Intent(context, ServiceDetailsActivity::class.java)
        intent.putExtra(context.getString(R.string.origin), source)
        intent.putExtra(context.getString(R.string.destination), destination)
        intent.putExtra(context.getString(R.string.bus_type), busDetails)

        PreferenceUtils.removeKey(context.getString(R.string.scannedUserName))
        PreferenceUtils.removeKey(context.getString(R.string.scannedUserId))
        PreferenceUtils.removeKey("selectedScanType")
        PreferenceUtils.removeKey(context.getString(R.string.scan_coach))
        PreferenceUtils.removeKey(context.getString(R.string.scan_driver_1))
        PreferenceUtils.removeKey(context.getString(R.string.scan_driver_2))
        PreferenceUtils.removeKey(context.getString(R.string.scan_cleaner))
        PreferenceUtils.removeKey(context.getString(R.string.scan_contractor))

        context.startActivity(intent)
    }

    private fun navigateToModifyFare() {
        val busDetails =
            "${serviceDetails?.body?.number} | ${serviceDetails?.body?.travelDate} ${serviceDetails?.body?.origin?.name} - ${serviceDetails?.body?.destination?.name} ${serviceDetails?.body?.busType} "
        val intent = Intent(context, UpdateRateCardActivity::class.java)
        intent.putExtra(
            context.getString(R.string.origin), serviceDetails?.body?.origin?.name
        )


        intent.putExtra(
            context.getString(R.string.destination), serviceDetails?.body?.destination?.name
        )
        intent.putExtra(context.getString(R.string.bus_type), busDetails)

        PreferenceUtils.putString(
            context.getString(R.string.updateRateCard_resId), reservationId.toString()
        )
        PreferenceUtils.putString(
            context.getString(R.string.updateRateCard_origin), serviceDetails?.body?.origin?.name
        )
        PreferenceUtils.putString(
            context.getString(R.string.updateRateCard_destination),
            serviceDetails?.body?.destination?.name
        )
        PreferenceUtils.putString(
            context.getString(R.string.updateRateCard_originId), serviceDetails?.body?.origin?.id
        )
        PreferenceUtils.putString(
            context.getString(R.string.updateRateCard_destinationId),
            serviceDetails?.body?.destination?.id
        )
        PreferenceUtils.putString(
            context.getString(R.string.updateRateCard_busType), busDetails
        )

        firebaseLogEvent(
            context,
            UPDATE_RATE_CARD,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            UPDATE_RATE_CARD,
            "Update Rate Card - SRP"
        )

        context.startActivity(intent)
        try {
            if (serviceDetails?.body?.travelDate!!.contains("-")) {
                val date = serviceDetails?.body?.travelDate!!.split("-")
                val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                PreferenceUtils.putString(
                    context.getString(R.string.updateRateCard_travelDate), finalDate
                )
            } else {
                val date = serviceDetails?.body?.travelDate!!.split("/")
                val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                PreferenceUtils.putString(
                    context.getString(R.string.updateRateCard_travelDate), finalDate
                )
            }
        } catch (e: Exception) {
            context.toast(e.message.toString())
        }

    }

    private fun showCustomFareUpdateToast(context: Context, message: String) {
        val binding = CustomToastFareUpdateBinding.inflate(LayoutInflater.from(context))
        binding.toastText.text = message
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        toast.view = binding.root
        toast.show()
    }


    private fun callSingleBlockUnblock(resId: String, isBlock: Boolean, remarks: String = "", authPin: String) {
        val reqBody =
            com.bitla.ts.domain.pojo.single_block_unblock.single_block_unblock_request.ReqBody(
                api_key = loginModelPref.api_key,
                res_id = resId,
                response_format = response_format.toBoolean(),
                locale = locale
            )

        if (isBlock) {
            val remarksInList = remarks.split(",")
            val blockingReasonId = remarksInList[0]
            val remark = remarksInList.drop(1).joinToString(",")

            reqBody.remarks = remark
            reqBody.blockingReason = blockingReasonId
        }

        val singleBlockUnblockRequest = SingleBlockUnblockRequest(
            bccId.toString(),
            format_type,
            single_block_unblock_method_name,
            req_body = reqBody
        )
        availableRoutesViewModel.singleBlockUnblockApi(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            singleBlockUnblockRequest,
            single_block_unblock_method_name,
            authPin = authPin
        )
    }

    private fun serviceBlockReasonsListObserver() {
        pickUpChartViewModel.serviceBlockReasonsListResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        blockReasonsList = it.reasons
                    }

                    401 -> {
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

    private fun callTrackingoApi(baseUrl: String, apiKey: String, assetId: String) {
        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiService = retrofit.create(ApiInterface::class.java)

        apiService.getAssetWiseServiceDetails(assetId, apiKey)
            .enqueue(object : Callback<TrackingoResponse> {
                override fun onResponse(call: Call<TrackingoResponse>, response: Response<TrackingoResponse>) {
                    if (response.isSuccessful) {
                        trackingoData = response.body()
                        if(trackingoData != null) {
                            if (trackingoData?.code == 200) {
                                val currentAddress = trackingoData?.data?.currentAddress ?: ""
                                val lastLocatedAt = trackingoData?.data?.lastLocatedAt ?: ""
                                if (currentAddress.isNotEmpty()) {
                                    binding.includeHeader.apply {
                                        currentLocationLL.visible()
                                        currentLocationSubtitle.isSelected = true
                                        currentLocationSubtitle.text = currentAddress
                                    }
                                } else if (!lastLocatedAt.isNotEmpty()) {
                                    binding.includeHeader.apply {
                                        currentLocationLL.visible()
                                        currentLocationSubtitle.isSelected = true
                                        currentLocationSubtitle.text = lastLocatedAt
                                    }
                                } else {
                                    binding.includeHeader.apply {
                                        currentLocationLL.gone()
                                        currentLocationSubtitle.isSelected = false
                                    }
                                }
                            }
                        }

                    } else {
                        Timber.d("Trackingo API error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<TrackingoResponse>, t: Throwable) {
                    Timber.e("API Failure: ${t.message}")
                }
            })
    }



}