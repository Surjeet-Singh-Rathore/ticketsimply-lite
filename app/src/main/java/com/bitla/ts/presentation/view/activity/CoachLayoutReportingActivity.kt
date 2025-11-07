package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.util.*
import android.view.*
import android.view.View.*
import android.view.animation.*
import android.widget.*
import androidx.appcompat.widget.*
import androidx.constraintlayout.widget.*
import androidx.core.view.*
import com.bitla.ts.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.boarding_stage_seats.request.*
import com.bitla.ts.domain.pojo.drag_drop_remarks_update.request.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.pickup_chart_crew_details.response.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.reservation_stages.request.*
import com.bitla.ts.domain.pojo.send_sms_email.request.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.view.activity.reservationOption.extendedFare.*
import com.bitla.ts.presentation.view.fragments.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getBccId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLogin
import com.google.android.material.bottomsheet.*
import com.google.zxing.integration.android.*
import gone
import isNetworkAvailable
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible

class CoachLayoutReportingActivity : BaseActivity(), OnSeatSelectionListener, OnItemClickListener, VarArgListener,
    DialogButtonAnyDataListener{
    private lateinit var binding: ActivityCoachLayoutReportingBinding
    private var loginModelPref = LoginModel()
    private var locale = ""
    private val coachLayoutReportingViewModel by viewModel<CoachLayoutReportingViewModel>()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private val cityDetailViewModel by viewModel<CityDetailViewModel<Any?>>()
    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private lateinit var allCoachFragment: AllCoachFragment
    private var sourcePopupWindow: PopupWindow? = null
    private var searchBpCoachLayoutReportingAdapter: SearchBpCoachLayoutReportingAdapter? = null
    private var isPermanentPhoneBooking: Boolean = false
    private var reservationId: String? = null
    private var originId: String? = null
    private var destinationId: String? = null
    private var origin: String? = null
    private var destination: String? = null
    private var travelDate: String = ""
    private var busType: String? = null
    private var callNumber: String = ""
    private var crewDetailsData: PickupChartCrewDetailsResponse? = null
    private var crewDetailBottomSheetBinding: BottomsheetCrewDetailsCoachBinding? = null
    private lateinit var baseUpdateCancelTicket: BaseUpdateCancelTicket
    private lateinit var cancelTicketSheet: CancelTicketSheet
    private var lastSelectedSeatPosition: Int = 0
    private var pnrAdapter: SeatPassengerAdapter? = null
    private var boardingPointList: MutableList<String> = mutableListOf()
    private var ticketNumber = ""
    private var isBimaServiceDetails: Boolean? = null
    private var isEditChartClicked: Boolean = false
    private var updatePassengerTravelStatus = false

    private var role: String = ""
    private var isAllowOnlyOnce = false
    private var isExtraSeat = false
    private var lastSelectedSeatNumber: String = ""
    private var currentSelectedBoardingStageId = ""
    private var serviceDetails: ServiceDetailsModel? = null
    private lateinit var rapidBookingDialog: AlertDialog
    private var bpDpBoarding: MutableList<BoardingPointDetail> = mutableListOf()
    private var bpDpDropping: MutableList<DropOffDetail> = mutableListOf()

    var emptyBoarding: BoardingPointDetail = BoardingPointDetail("", "", "", "", "", "")
    var emptyDropping: DropOffDetail = DropOffDetail("", "", "", "", "", "")

    private var boardingPoint: String = ""
    private var droppingPoint: String = ""

    private var ymdDate: String = ""
    private var stageDetails = mutableListOf<StageDetail>()
    private var isSwitchClicked = false
    private lateinit var onSeatSelectionListener: OnSeatSelectionListener

    private var scanTag = ""
    private var seatNum = ""
    private var pnrNum = ""

    private lateinit var bindingStatus: SheetBoardedCheckBinding
    private lateinit var bottomSheetDialogStatus: BottomSheetDialog

    private lateinit var bindingSheet: SheetReleaseTicketsBinding
    private lateinit var bottomSheetDialoge: BottomSheetDialog
    private var qrresponse = ""
    private lateinit var switch: SwitchCompat

    private lateinit var convertToPermanentPhoneBlockDialogBinding: DialogConvertToPermanentPhoneBlockBinding
    private var convertToPermanentPhoneBlockDialog: AlertDialog? = null

    private var passengerName: String = ""
    private var newOtp: String = ""
    private var skipQrCcode: Boolean = false
    private var templist = listOf<String>()
    private var restrictSkipVerification = false
    private var boardedSms = false
    private var previousScreen: String? = null
    private var currencyFormatt: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setMultistationSeatData()
    }

    override fun initUI() {
        binding = ActivityCoachLayoutReportingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        previousScreen =
            if (intent.hasExtra(REDIRECT_FROM)) intent.getStringExtra(REDIRECT_FROM) else null

        if (this.isNetworkAvailable()) {
            getPref()
            getDataFromIntent()
            setToolbarTitle()
            onClickListener()
            onSeatSelectionListener = this

            bindingStatus = SheetBoardedCheckBinding.inflate(LayoutInflater.from(this))
            bottomSheetDialogStatus = BottomSheetDialog(this, R.style.BottomSheetDialog)
            bottomSheetDialogStatus.setContentView(bindingStatus.root)


            bindingSheet = SheetReleaseTicketsBinding.inflate(LayoutInflater.from(this))
            bottomSheetDialoge = BottomSheetDialog(this, R.style.BottomSheetDialog)
            bottomSheetDialoge.setContentView(bindingSheet.root)

            binding.includeHeader.moreIV.gone()
            binding.includeHeader.headerLL.gone()
            binding.includeHeader.imgBack.setOnClickListener{
                onBackPressed()
            }
            binding.coachProgressBar.visible()

            baseUpdateCancelTicket =
                supportFragmentManager.findFragmentById(R.id.layoutUpdateTicketContainer) as BaseUpdateCancelTicket

            cancelTicketSheet =
                supportFragmentManager.findFragmentById(R.id.layoutCancelTicketSheet) as CancelTicketSheet

            callGetReservationStagesApi(reservationId ?: "")

            setObserver()
        } else {
            this.noNetworkToast()
        }
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    private fun setMultistationSeatData () {
        cityDetailViewModel.multistationSeatData.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (isSwitchClicked && !it.passenger_details.isNullOrEmpty()) {
                            getBookedSeatOptions(it.passenger_details, false)
                            isSwitchClicked = false
                        } else {
                            if (!it.passenger_details.isNullOrEmpty()) getBookedSeatOptions(it.passenger_details)
                        }

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
                        showUnauthorisedDialog()

                    }

                    else -> {
                        binding.apply {

                            noData.root.visible()
                            noData.tvNoData.text = it.message ?: it.result.message
                                    ?: getString(R.string.no_data_available)
                            toast(noData.tvNoData.text.toString())
                        }
                    }
                }
            }
        }
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        privilegeResponseModel = getPrivilegeBase()
        currencyFormatt = getCurrencyFormat(this, privilegeResponseModel?.currencyFormat)

        if (privilegeResponseModel?.isPermanentPhoneBooking != null) {
            isPermanentPhoneBooking = privilegeResponseModel?.isPermanentPhoneBooking ?: false
        }

        role = getUserRole(loginModelPref, isAgentLogin = privilegeResponseModel?.isAgentLogin ?: false, this)

        if (role == getString(R.string.role_field_officer)) {
            updatePassengerTravelStatus =
                privilegeResponseModel?.boLicenses?.updatePassengerTravelStatus == true
            isAllowOnlyOnce =
                privilegeResponseModel?.boLicenses?.allowUserToBoardingStatusOnlyOnce ?: false
        } else {
            updatePassengerTravelStatus =
                privilegeResponseModel?.updatePassengerTravelStatus == true
            isAllowOnlyOnce =
                privilegeResponseModel?.availableAppModes?.allow_user_to_change_the_the_boarding_status_only_once
                    ?: false
        }
        travelDate = PreferenceUtils.getTravelDate()
        ymdDate = getDateYMD(travelDate.replace("/", "-"))
    }

    private fun onClickListener() {
        binding.apply {
            layoutBookedSeatDetails.menuCancelTicket.setOnClickListener(this@CoachLayoutReportingActivity)
            layoutBookedSeatDetails.menuUpdateTicket.setOnClickListener(this@CoachLayoutReportingActivity)
            layoutBookedSeatDetails.menuUpdateRemark.setOnClickListener(this@CoachLayoutReportingActivity)
            layoutBookedSeatDetails.menuViewticket.setOnClickListener(this@CoachLayoutReportingActivity)
            layoutBookedSeatDetails.callPassenger.setOnClickListener(this@CoachLayoutReportingActivity)
            layoutBookedSeatDetails.resendSms.setOnClickListener(this@CoachLayoutReportingActivity)
            layoutBookedSeatDetails.menuShift.setOnClickListener(this@CoachLayoutReportingActivity)
            layoutBookedSeatDetails.menuShiftSameService.setOnClickListener(this@CoachLayoutReportingActivity)
            layoutBookedSeatDetails.menuMoveExtra.setOnClickListener(this@CoachLayoutReportingActivity)
            layoutBookedSeatDetails.boardedSwitchBox.setOnClickListener(this@CoachLayoutReportingActivity)
            layoutBookedSeatDetails.menuConvertPermanentPhoneBlock.setOnClickListener(this@CoachLayoutReportingActivity)
            transparentOptionV.setOnClickListener(this@CoachLayoutReportingActivity)
            transparentBookedSeatsOptionsV.setOnClickListener(this@CoachLayoutReportingActivity)
        }
    }


    private fun sourcePopupDialog() {
            var popupBinding = SearchBpCoachLayoutReportingBinding.inflate(LayoutInflater.from(this))

            popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

            searchBpCoachLayoutReportingAdapter =
                SearchBpCoachLayoutReportingAdapter(
                    this,
                    coachLayoutReportingViewModel.reservationStagesResponse.value?.result?.stageDetails
                        ?: arrayListOf(),
                ) { item ->
                    sourcePopupWindow?.dismiss()
                    boardingPointList.add(
                        this.getString(
                            R.string.stage_details_text, item.stageTime, item.cityName, item.stageName
                        )
                    )
                    binding.boardingPointDropdownET.setText(
                        this.getString(
                            R.string.stage_details_text, item.stageTime, item.cityName, item.stageName
                        )
                    )
                    currentSelectedBoardingStageId = item.stageId.toString()
                    binding.coachProgressBar.visible()


                    callGetBoardingStageSeatsApi(
                        reservationId ?: "",
                        originId ?: "",
                        destinationId ?: "",
                        currentSelectedBoardingStageId
                    )
                }
            popupBinding.searchRV.adapter = searchBpCoachLayoutReportingAdapter

            popupBinding.searchET.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    searchBpCoachLayoutReportingAdapter?.filter?.filter(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
                ) {
                }
            })

            val displayMetrics = DisplayMetrics()
            val windowManager =
                getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenHeight = displayMetrics.heightPixels

            var popupHeight = (screenHeight * 0.4).toInt()

            if(boardingPointList.size <= 5) {
                popupHeight = FrameLayout.LayoutParams.WRAP_CONTENT
            }

            sourcePopupWindow = PopupWindow(
                popupBinding.root, binding.boardingPointDropdownET.width, popupHeight,
                true
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sourcePopupWindow?.elevation = 12.0f;
            }

            sourcePopupWindow?.showAsDropDown(binding.boardingPointDropdownET, 0, 0, Gravity.END)
            sourcePopupWindow?.elevation = 25f

            popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
                sourcePopupWindow?.dismiss()
                true
            }
    }

    private fun setToolbarTitle() {
        val srcDest = "$origin-$destination"
        val subtitle = "${getDateMMM(travelDate)} | $busType"
        binding.includeHeader.toolbarHeaderText.text = srcDest
        binding.includeHeader.tvCurrentHeader.text = getString(R.string.branch_booking)
        binding.includeHeader.toolbarSubtitle.text = subtitle

    }

    private fun callGetReservationStagesApi(reservationId: String) {
        val reservationStagesRequest = ReservationStagesRequest(
            reservationId,
            loginModelPref.api_key,
            operator_api_key,
            locale
        )

        coachLayoutReportingViewModel.getReservationStagesApi(reservationStagesRequest)

    }

    private fun callGetBoardingStageSeatsApi(
        reservationId: String,
        originId: String,
        destinationId: String,
        boardingId: String
    ) {
        val boardingStageSeatsRequest = BoardingStageSeatsRequest(
            reservationId,
            originId,
            destinationId,
            loginModelPref.api_key,
            operator_api_key,
            locale,
            false,
            boardingId
        )

        coachLayoutReportingViewModel.getBoardingStageSeatsApi(boardingStageSeatsRequest)
    }

    private fun setObserver() {
        coachLayoutReportingViewModel.reservationStagesResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        //For start for the activity
                        val stageList = it.result?.stageDetails
                        if(stageList?.isNotEmpty() == true) {
                            binding.boardingPointDropdownET.setText(
                                this.getString(
                                    R.string.stage_details_text, stageList[0].stageTime, stageList[0].cityName, stageList[0].stageName
                                )
                            )
                            currentSelectedBoardingStageId =
                                stageList[0].stageId.toString()

                            callGetBoardingStageSeatsApi(
                                reservationId!!, originId!!, destinationId!!,
                                currentSelectedBoardingStageId
                            )

                            binding.boardingPointDropdownET.setOnClickListener {
                                    sourcePopupDialog()
                            }
                        }
                    }

                    401 -> showUnauthorisedDialog()
                    else -> toast(it.message)
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        coachLayoutReportingViewModel.boardingStagesSeatsResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        serviceDetails = it

                        allCoachFragment =
                            supportFragmentManager.findFragmentById(R.id.layoutCoachView) as AllCoachFragment
                        allCoachFragment.setCoachData(serviceDetails?.body!!)
                        showLayoutCrewDetails(serviceDetails?.body!!)
                        binding.coachProgressBar.gone()
                        binding.layoutCoachView.visible()

                        if (::allCoachFragment.isInitialized) {
                            allCoachFragment.setCoachData(serviceDetails?.body!!)
                        }

                        PreferenceUtils.setPreference(
                            PREF_RESERVATION_ID,
                            it.body?.reservationId?.toLong()
                        )
                        allCoachFragment.toggleExtraSeatsButtonVisibility(false)
                    }

                    401 -> showUnauthorisedDialog()
                    else -> toast(it.message)
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }


        ticketDetailsViewModel.dataSMSEmailResponse.observe(this) {

            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            val message = it.message
                            message?.let { it1 -> toast(it1) }
                        }

                        401 -> {
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

        pickUpChartViewModel.updateBoardedStatusResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        PreferenceUtils.setPreference(
                            "pickUpChartStatus", "${"2"}"
                        )

                        bottomSheetDialogStatus.dismiss()

                        callGetBoardingStageSeatsApi(
                            reservationId ?: "",
                            originId ?: "",
                            destinationId ?: "",
                            currentSelectedBoardingStageId
                        )

                        if (::switch.isInitialized) {
                            switch.isChecked = it.status == "2"
                        }
                        toast(getString(R.string.update_passenger_status))

                        isSwitchClicked = true
                        hitMultistationSeatDetailApi(reservationId ?: "", lastSelectedSeatNumber)


                    }

                    401 -> {
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



        sharedViewModel.dragDropRemarks.observe(this) { it ->

            if (it != null) {
                toast(it.message)
                hideSeatBookingDetailsLayout()
            } else {
                toast(getString(R.string.server_error))
            }

        }

        cityDetailViewModel.phoneBlockTempToPermanent.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {

                        it.message.let { it1 -> toast(it1) }
                        binding.coachProgressBar.visible()
                        callGetBoardingStageSeatsApi(
                            reservationId ?: "",
                            originId ?: "",
                            destinationId ?: "",
                            currentSelectedBoardingStageId
                        )

                        val view: View =
                            findViewById<View>(R.id.layout_coach_options) as ConstraintLayout

                        if (this::allCoachFragment.isInitialized) {
                            if (view.visibility == VISIBLE) {
                                closeToggle()
                            }
                            if (PreferenceUtils.getPreference(PREF_UPDATE_COACH, false) == true) {
                                callGetBoardingStageSeatsApi(
                                    reservationId ?: "",
                                    originId ?: "",
                                    destinationId ?: "",
                                    currentSelectedBoardingStageId
                                )
                            }
                        }
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        it.message.let { it1 -> toast(it1) }
                    }
                }
            }
        }


        pickUpChartViewModel.pickupChartCrewDetailsResponse.observe(this) { it ->
            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            crewDetailsData = it
                            showCrewDetailsBottomSheet(it)
                        }

                        401 -> {
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

    private fun hideSeatBookingDetailsLayout() {
        val view: View =
            findViewById<View>(R.id.layout_booked_seat_details) as ConstraintLayout
        val slideLeft: Animation =
            AnimationUtils.loadAnimation(this, R.anim.exit_to_right)
        view.visibility = GONE
        view.startAnimation(slideLeft)
        binding.transparentBookedSeatsOptionsV.gone()

    }

    private var coachNumber: Any? = null
    private fun showLayoutCrewDetails(response: Body) {
        coachNumber = response.coachDetails?.coachNumber ?: ""

        try {
            if (response.coachDetails?.coachNumber != null && response.coachDetails?.coachNumber.toString()
                    .isNotEmpty()
            ) {
                binding.layoutCrewI.busNumberTV.text =
                    response.coachDetails?.coachNumber.toString()
            } else {
                binding.layoutCrewI.busNumberTV.text =
                    getString(R.string.not_assigned)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        binding.layoutCrewI.crewTV.setOnClickListener {

            firebaseLogEvent(
                this,
                CREW_SEAT_LAYOUT,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                CREW_SEAT_LAYOUT,
                "Crew_SeatLayout"
            )

            if (isNetworkAvailable())
                callPickupChartCrewDetailsApi()
            else
                noNetworkToast()
        }
        if (response.coachDetails?.coachNumber != null) {
            coachNumber = response.coachDetails?.coachNumber
        }

        if (response.coachDetails?.coachNumber != null && response.coachDetails?.coachNumber != "") {
            binding.layoutCrewI.busNumberTV.text = response.coachDetails?.coachNumber.toString()
        } else {
            binding.layoutCrewI.busNumberTV.text = getString(R.string.not_assigned)
        }
    }

    private fun getBookedSeatOptions(
        seatDetail: ArrayList<com.bitla.ts.domain.pojo.multistation_data.PassengerDetail>,
        isShowHide: Boolean = true
    ) {
        getSeatData(seatDetail)
        showSeatDetailToggle()
    }

    private var seatPassengersList: ArrayList<com.bitla.ts.domain.pojo.multistation_data.PassengerDetail> = arrayListOf()
    private var passangerPos = -1

    private fun getSeatData(seatDetail: ArrayList<com.bitla.ts.domain.pojo.multistation_data.PassengerDetail>) {
        seatPassengersList.clear()
        lastSelectedSeatPosition = 0
        seatPassengersList = seatDetail

        if (seatPassengersList.isNotEmpty())
            setPassengersAdapter()

    }

    private fun setPassengersAdapter() {
        seatPassengersList[0].is_selected = true
        pnrAdapter =
            SeatPassengerAdapter(
                this,
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
            view.agentNameLL.gone()
            if (data.no_of_seats > 1) {
                view.otherSeatLL.visible()
                val filteredSeatNumbers = data.seat_numbers.split(",").toMutableList()
                filteredSeatNumbers.remove(data.seat_no)
                val resultString = filteredSeatNumbers.joinToString(",")
                view.otherSeatValueTV.text = resultString
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

            ticketNumber = data.ticket_no.substringBefore(" ")

            if (!data.release_datetime.isNullOrEmpty()) {
                view.tillLL.visible()
                view.tillValue.text = data.release_datetime

            } else {
                view.tillLL.gone()

            }

            try {
                view.fareValueTV.text = "${privilegeResponseModel?.currency} ${data.seat_fare.toDouble().convert(currencyFormatt)}"
            } catch (e: Exception) {
                view.fareValueTV.text = data.seat_fare
            }

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


            if (data.can_confirm_phone_block || (privilegeResponseModel?.isAgentLogin ?: false) || role.contains(
                    getString(
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
                    if (!privilegeResponseModel?.country.equals("Indonesia", true) && !isExtraSeat) {
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


            if (data.can_cancel && !data.can_release_phone_block) {
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

//            view.menuMoveExtra.gone()
//            view.viewMoveExtraSeat.gone()
        }
    }

    private fun showSeatDetailToggle() {

        val view: View = findViewById<View>(R.id.layout_booked_seat_details) as ConstraintLayout

        val slideRight: Animation = AnimationUtils.loadAnimation(this, R.anim.enter_from_right)
        val slideLeft: Animation = AnimationUtils.loadAnimation(this, R.anim.exit_to_right)

        if (view.visibility == GONE) {
            view.visibility = VISIBLE
            view.startAnimation(slideRight)
            binding.transparentBookedSeatsOptionsV.visible()
        } else {
            view.visibility = GONE
            view.startAnimation(slideLeft)
            binding.transparentBookedSeatsOptionsV.gone()
        }
    }

    fun closeSeatDetailToggle() {
        val view: View = findViewById<View>(R.id.layout_booked_seat_details) as ConstraintLayout
        val slideRight: Animation = AnimationUtils.loadAnimation(this, R.anim.enter_from_right)
        val slideLeft: Animation = AnimationUtils.loadAnimation(this, R.anim.exit_to_right)
        view.visibility = GONE
        view.startAnimation(slideLeft)
        binding.transparentBookedSeatsOptionsV.gone()
    }

    fun hitMultistationSeatDetailApi(reservationId: String, seatNumber: String) {
        lastSelectedSeatNumber = seatNumber
        cityDetailViewModel.multistationPassengerDataApi(
            apiKey = loginModelPref.api_key,
            reservationId = reservationId,
            seatNumber = seatNumber,
            isBima = isBimaServiceDetails ?: false,
            apiType = multistation_seat_details_api,
            locale = locale
        )
    }

    fun callPickupChartCrewDetailsApi() {
        pickUpChartViewModel.pickupChartCrewDetailsApi(
            loginModelPref.api_key,
            reservationId.toString(),
            pickup_chart_crew_details,
            locale = locale
        )
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable()) {
            getPref()
        } else
            noNetworkToast()
        if (boardingPoint.isNotEmpty() && droppingPoint.isNotEmpty() && ::rapidBookingDialog.isInitialized && rapidBookingDialog.isShowing) {
            rapidBookingDialog.cancel()

            rapidBookingDialog = DialogUtils.rapidBookingDialog(
                boardingPoint = boardingPoint,
                droppingPoint = droppingPoint,
                context = this,
                varArgListener = this
            )!!
        }

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

    private fun getDataFromIntent() {
        if (intent.hasExtra("reservationId")) {
            reservationId = intent.getStringExtra("reservationId")
        }

        if (intent.hasExtra("originId")) {
            originId = intent.getStringExtra("originId")
        }

        if (intent.hasExtra("destinationId")) {
            destinationId = intent.getStringExtra("destinationId")
        }

        if (intent.hasExtra("busType")) {
            busType = intent.getStringExtra("busType")
        }

        if (intent.hasExtra("origin")) {
            origin = intent.getStringExtra("origin")
        }

        if (intent.hasExtra("travelDate")) {
            travelDate = intent.getStringExtra("travelDate") ?: ""
        }

        if (intent.hasExtra("destination")) {
            destination = intent.getStringExtra("destination")
        }

        if (intent.hasExtra("is_bima")) {
            isBimaServiceDetails = intent.getBooleanExtra("is_bima", false)
        }

    }

    override fun onSeatSelection(
        selectedSeatDetails: ArrayList<SeatDetail>,
        finalSeatNumber: ArrayList<String?>,
        totalSum: Double,
        isAllSeatSelected: Boolean,
        isSeatLongPress: Boolean?
    ) {

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

    }

    override fun editSeatFare(seatNumber: String, newFare: String) {

    }

    override fun bookExtraSeats(isChecked: Boolean?, isSeatSelected: Boolean?) {

    }

    override fun moveExtraSeat(isChecked: Boolean) {

    }

    override fun releaseTicket(ticketNumber: String, releaseTicket: String) {

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

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {

            when (view.id) {

                R.id.optionRootCL -> {
                    when (view.tag) {

                        getString(R.string.edit_chart_option) -> {
                            isEditChartClicked = true
                            //  excludePassengerDetails = false
                            callGetBoardingStageSeatsApi(
                                reservationId ?: "",
                                originId ?: "",
                                destinationId ?: "",
                                currentSelectedBoardingStageId
                            )
                            binding.coachProgressBar.visible()
                            closeToggle()
                        }

                        getString(R.string.update_rate_card_options) -> {
                            val busDetails =
                                "${serviceDetails?.body?.number} | ${serviceDetails?.body?.travelDate} ${serviceDetails?.body?.origin?.name} - ${serviceDetails?.body?.destination?.name} ${serviceDetails?.body?.busType} "
                            val intent = Intent(this, UpdateRateCardActivity::class.java)
                            intent.putExtra(
                                getString(R.string.origin),
                                serviceDetails?.body?.origin?.name
                            )
                            intent.putExtra(
                                getString(R.string.destination),
                                serviceDetails?.body?.destination?.name
                            )
                            intent.putExtra(getString(R.string.bus_type), busDetails)

                            PreferenceUtils.putString(
                                getString(R.string.updateRateCard_resId),
                                reservationId.toString()
                            )
                            PreferenceUtils.putString(
                                getString(R.string.updateRateCard_origin),
                                serviceDetails?.body?.origin?.name
                            )
                            PreferenceUtils.putString(
                                getString(R.string.updateRateCard_destination),
                                serviceDetails?.body?.destination?.name
                            )
                            PreferenceUtils.putString(
                                getString(R.string.updateRateCard_originId),
                                serviceDetails?.body?.origin?.id
                            )
                            PreferenceUtils.putString(
                                getString(R.string.updateRateCard_destinationId),
                                serviceDetails?.body?.destination?.id
                            )
                            PreferenceUtils.putString(
                                getString(R.string.updateRateCard_busType),
                                busDetails
                            )

                            firebaseLogEvent(
                                this,
                                UPDATE_RATE_CARD,
                                loginModelPref.userName,
                                loginModelPref.travels_name,
                                loginModelPref.role,
                                UPDATE_RATE_CARD,
                                "Update Rate Card - SRP"
                            )

                            startActivity(intent)
                            try {
                                if (serviceDetails?.body?.travelDate!!.contains("-")) {
                                    val date = serviceDetails?.body?.travelDate!!.split("-")
                                    val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                    PreferenceUtils.putString(
                                        getString(R.string.updateRateCard_travelDate),
                                        finalDate
                                    )
                                } else {
                                    val date = serviceDetails?.body?.travelDate!!.split("/")
                                    val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                    PreferenceUtils.putString(
                                        getString(R.string.updateRateCard_travelDate),
                                        finalDate
                                    )
                                }
                            } catch (e: Exception) {
                                toast(e.message.toString())
                            }


                        }


                        getString(R.string.quick_book_option) -> {
                            val intent = Intent(this, QuickBookChileActivity::class.java)

                            intent.putExtra("originID", serviceDetails?.body?.origin?.id?.toInt())
                            intent.putExtra(
                                "destinationID",
                                serviceDetails?.body?.destination?.id!!.toInt()
                            )
                            intent.putExtra("reservationID", reservationId)
                            intent.putExtra("serviceNumber", serviceDetails?.body?.number)
                            startActivity(intent)
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

                                val intent = Intent(this, ViewReservationActivity::class.java)
//                                Timber.d("orifinidqw", "${dateList}")
                                intent.putExtra("pickUpResid", resID)

                                startActivity(intent)

                                firebaseLogEvent(
                                    this,
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
                                "${serviceDetails?.body?.number} | ${getDateDMYY(travelDate)} ${serviceDetails?.body?.depTime} | $busType"
                            val intent = Intent(this, ServiceDetailsActivity::class.java)
                            intent.putExtra(getString(R.string.origin), serviceDetails?.body?.origin?.name ?: "")
                            intent.putExtra(getString(R.string.destination), destination)
                            intent.putExtra(getString(R.string.bus_type), busDetails)

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
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    fun setIsExtraSeat(flag: Boolean) {
        isExtraSeat = flag
    }

    private fun closeToggle() {
        val slideLeft: Animation = AnimationUtils.loadAnimation(this, R.anim.exit_to_right)
        val view: View = findViewById<View>(R.id.layout_coach_options) as ConstraintLayout
        view.visibility = GONE
        view.startAnimation(slideLeft)
        binding.transparentOptionV.gone()
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
                    startActivity(intent)
                }

                getString(R.string.confirm) -> {

                    var noOfTickets = ""
                    noOfTickets = args[1] as String
                    try {
                        if (noOfTickets.toInt() > (serviceDetails?.body?.availableSeats ?: 0)) {
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


    private fun gotoBusTrackingPage() {
        if (privilegeResponseModel?.country.equals("India", true)) {
            val intent = Intent(this, CurrentLocationActivity::class.java)
            PreferenceUtils.putString(
                "toolbarheader",
                binding.includeHeader.toolbarHeaderText.text.toString()
            )
            PreferenceUtils.putString(
                "toolbarsubheader",
                "${serviceDetails?.body?.number} | ${getDateDMYY(travelDate)} | ${serviceDetails?.body?.origin?.name}-${serviceDetails?.body?.destination?.name} | ${serviceDetails?.body?.busType} ${serviceDetails?.body?.coachDetails?.totalSeats?.toString()}"
            )

            startActivity(intent)
        } else {
            val intent = Intent(this, BusTrackingActivity::class.java)
            intent.putExtra(
                "toolbarSubHeader",
                "${serviceDetails?.body?.origin?.name}-${serviceDetails?.body?.destination?.name}"
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

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.resend_otp -> {
                if (this.isNetworkAvailable()) {
                    resendOtpAndQrCodeAPI(pnrNum, seatNum)

                } else this.noNetworkToast()
            }

            R.id.scan_qr_code -> {
                scanTag = "verificationScan"
                qrresponse = ""
                scanScaeen()
            }


            R.id.toolbar_image_left -> {
                onBackPressed()
            }

            R.id.transparentOptionV -> {
                Timber.d("transparentOptionV clicked")
                toggle()
            }


            R.id.transparentBookedSeatsOptionsV -> {
                val view: View =
                    findViewById<View>(R.id.layout_booked_seat_details) as ConstraintLayout
                val slideLeft: Animation =
                    AnimationUtils.loadAnimation(this, R.anim.exit_to_right)
                view.visibility = GONE
                view.startAnimation(slideLeft)
                binding.transparentBookedSeatsOptionsV.gone()

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
                val intent = Intent(this, TicketDetailsActivityCompose::class.java)
                intent.putExtra(
                    getString(R.string.TICKET_NUMBER),
                    seatPassengersList[lastSelectedSeatPosition].ticket_no
                )
                intent.putExtra("returnToDashboard", false)
                startActivity(intent)
            }

            R.id.menu_update_ticket -> {
                val num =
                    seatPassengersList[lastSelectedSeatPosition].ticket_no.substringBefore(" ")
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
                callSendSMSEmailApi("sms")
            }

            R.id.menu_shift -> {
                val intent = Intent(this, ShiftPassengerActivity::class.java)
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


                PreferenceUtils.putString("SHIFT_servicename", serviceDetails?.body?.number ?: "")
                PreferenceUtils.putString("SHIFT_originId", originId)
                PreferenceUtils.putString("SHIFT_destinationId", destinationId)
                PreferenceUtils.putString(
                    "oldServiceNumberShiftACTIVITY",
                    "${serviceDetails?.body?.number ?: ""}?${serviceDetails?.body?.travelDate ?: ""}"
                )
                PreferenceUtils.putString(
                    "TicketDetail_noOfSeats",
                    (seatPassengersList[lastSelectedSeatPosition].no_of_seats ?: 1).toString()
                )

                startActivity(intent)
            }

            R.id.menu_shift_same_service_ -> {
                val intent = Intent(this, ShiftPassengerActivity::class.java)
                intent.putExtra(
                    "service_ticketno",
                    seatPassengersList[lastSelectedSeatPosition].ticket_no
                )
                intent.putExtra("partial_shift", true)
                PreferenceUtils.putString("SHIFT_servicename", serviceDetails?.body?.number ?: "")
                PreferenceUtils.putString(
                    "SHIFT_SeatPnrNumber",
                    seatPassengersList[lastSelectedSeatPosition].ticket_no
                )
                PreferenceUtils.putString(
                    "TicketDetail_SeatNumbes",
                    seatPassengersList[lastSelectedSeatPosition].seat_no
                )

                PreferenceUtils.putString("SHIFT_originId", originId)
                PreferenceUtils.putString("SHIFT_destinationId", destinationId)

                PreferenceUtils.putString(
                    "oldServiceNumberShiftACTIVITY",
                    "${serviceDetails?.body?.number ?: ""}?${serviceDetails?.body?.travelDate ?: ""}"
                )
                startActivity(intent)
            }


            R.id.menuConvertPermanentPhoneBlock -> {

                showConvertToPermanentPhoneBlockDialog()
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

    private fun callSendSMSEmailApi(type: String) {
        val bccId = getBccId()
        loginModelPref = getLogin()

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
            ticketDetailsViewModel.sendSMSEmailApi(
                reqBody,
                send_sms_email_method_name
            )
        }
    }

    private fun resendOtpAndQrCodeAPI(pnrNum: String, seatNum: String) {

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

    private fun toggle() {
        val view = findViewById<View>(R.id.layout_coach_options) as ConstraintLayout

        // slide-up animation
        val slideRight: Animation = AnimationUtils.loadAnimation(this, R.anim.enter_from_right)
        val slideLeft: Animation = AnimationUtils.loadAnimation(this, R.anim.exit_to_right)

        if (view.visibility == GONE) {
            view.visibility = VISIBLE
            view.startAnimation(slideRight)
            binding.transparentOptionV.visible()
        } else {
            view.visibility = GONE
            view.startAnimation(slideLeft)
            binding.transparentOptionV.gone()
        }
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

        if (getPrivilegeBase() != null) {
            val privilegeResponse = getPrivilegeBase() as PrivilegeResponseModel
            privilegeResponse.let {

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

        if (privilegeResponseModel != null) {
            val privilegeResponse = privilegeResponseModel
            privilegeResponseModel?.let {
                isBoardedSms = privilegeResponseModel?.sendOtpToCustomersToAuthenticateBoardingStatus?:false
                isBoardedQr =
                    privilegeResponseModel?.sendQrCodeToCustomersToAuthenticateBoardingStatus?:false
                restrictSkipVerification =
                    privilegeResponseModel?.restrictOrHideSkipVerificationOptionInTsApp?:false


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

                if (privilegeResponseModel?.allowToCapturePassAndCrewTemp == true) {
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

                privilegeResponseModel?.let {
                    if (privilegeResponseModel?.allowToCapturePassAndCrewTemp == true) {
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
                        getString(R.string.resend_sms_qr_code)
                    resendQrImg.gone()
                    resendSmsImg.visible()
                    lenterotp.visible()
                    etenterOtp.clearFocus()
                    scanQrCode.setOnClickListener(this@CoachLayoutReportingActivity)
                }

                if (isBoardedQr && !isBoardedSms) {
                    scanqrText.text = getString(R.string.scan_qr)
                    lenterotp.gone()
                    otpText.gone()
                    resendText.text = getString(R.string.resend_qr)
                    scanQrCode.setOnClickListener(this@CoachLayoutReportingActivity)
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
                    resendText.text = getString(R.string.resend_sms)
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

                    privilegeResponseModel?.let {

                        if (privilegeResponseModel?.allowToCapturePassAndCrewTemp == true) {

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
                    reservation_id = reservationId ?: "",
                    temp = templist,
                    remarks = bindingStatus.etRemarksText.text.toString().trim(),
                    locale = locale
                ),
                update_boarded_status_method_name
            )

        } else this.noNetworkToast()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
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

    override fun onDataSend(type: Int, file: Any) {
        when (type) {
            1 -> {
                if ((file as String) == "success") {
                    isSwitchClicked = true
                    hitMultistationSeatDetailApi(reservationId ?: "", lastSelectedSeatNumber)
                }
            }
        }
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {

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

}