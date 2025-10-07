package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.content.*
import android.content.res.*
import android.graphics.*
import android.os.*
import android.text.*
import android.util.Base64
import android.view.*
import android.widget.*
import androidx.core.text.*
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import androidx.transition.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.book_extra_seat.*
import com.bitla.ts.domain.pojo.book_extra_seat.request.*
import com.bitla.ts.domain.pojo.book_ticket_full.*
import com.bitla.ts.domain.pojo.book_ticket_full.request.*
import com.bitla.ts.domain.pojo.book_ticket_full.request.BookingDetails
import com.bitla.ts.domain.pojo.book_ticket_full.request.ContactDetail
import com.bitla.ts.domain.pojo.book_ticket_full.request.PackageDetailsId
import com.bitla.ts.domain.pojo.book_ticket_full.request.ReqBody
import com.bitla.ts.domain.pojo.book_ticket_full.request.SeatDetail
import com.bitla.ts.domain.pojo.book_with_extra_seat.request.*
import com.bitla.ts.domain.pojo.booking.PayGayType
import com.bitla.ts.domain.pojo.booking_custom_request.*
import com.bitla.ts.domain.pojo.custom_applied_coupons.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.fare_breakup.request.*
import com.bitla.ts.domain.pojo.fare_breakup.response.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.passenger_details_result.*
import com.bitla.ts.domain.pojo.phonepe_direct_upi_for_app.request.*
import com.bitla.ts.domain.pojo.phonepe_direct_upi_transaction_status.request.*
import com.bitla.ts.domain.pojo.photo_block_tickets.request.*
import com.bitla.ts.domain.pojo.pinelabs.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.child_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.upi_check_status.request.*
import com.bitla.ts.domain.pojo.upi_create_qr.request.*
import com.bitla.ts.domain.pojo.validate_otp_wallets.request.*
import com.bitla.ts.domain.pojo.wallet_otp_generation.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.ticketDetails.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.dialog.DialogUtils.Companion.progressDialog
import com.bitla.ts.utils.dialog.DialogUtils.Companion.transactionFailedInterfaceDialog
import com.bitla.ts.utils.sharedPref.*
import com.bitla.tscalender.*
import com.google.android.material.bottomsheet.*
import com.google.gson.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import onChange
import org.json.*
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*

class BookingPaymentOptionsActivity : BaseActivity(), OnItemClickListener, DialogButtonListener,
    VarArgListener, DialogSingleButtonListener, DialogButtonAnyDataListener,
    SlyCalendarDialog.Callback {

    companion object {
        val tag: String = BookingPaymentOptionsActivity::class.java.simpleName
    }

    private var privileges: PrivilegeResponseModel? = null
    private var pinelabResponseString: String? = ""
    private var pinelabResponseData: CardSaleResponse? = null
    private var isExtraSeatBooking: Boolean = false
    private var bookExtraSeatData: BookExtraSeatResponse? = null
    private var lastBookedTicketNumber: String? = ""
    private var lastBookingData: BookTicketFullResponse? = null
    private var lastBookedSeatsNumber: String? = ""
    private var pinelabBillingRefNo: String = ""
    private var mServerMessenger: Messenger? = null

    private var isBound: Boolean? = false

    private val PLUTUS_SMART_PACKAGE = "com.pinelabs.masterapp"
    private val PLUTUS_SMART_ACTION = "com.pinelabs.masterapp.SERVER"
    private val MESSAGE_CODE = 1001
    private val BILLING_REQUEST_TAG = "MASTERAPPREQUEST"
    private val BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE"
    private var pinSize = 0
    private var shouldPhoneBlocking = false
    private var shouldExtraSeatBooking = false
    private var shouldTicketConfirmation = false
    private var excludeTicketConfirmation = mutableListOf<ExcludeTicketConfirmation>()
    private var currentCountry: String = ""

    var message: Message = Message.obtain(null, MESSAGE_CODE)


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mServerMessenger = Messenger(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mServerMessenger = null
            isBound = false

        }
    }
    private var isInsuranceChecked: Boolean? = false
    private var redelcomPaymentDialog: AlertDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private var isHandlerRunning = false
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 1500

    private var partialPaymentOption: String = "1"
    private var isPartialPayment: Boolean = false
    private var currency: String = ""
    private var terminalId: String = ""
    private var currencyFormat: String = ""
    private var isAgentLogin: Boolean = false
    private val isFromDashboard: Boolean = false //fixed
    private val releaseTicketRemarks: String = "release ticket" //fixed
    private var bookTicketTotalFare: String = "0.0"
    private var bookTicketPnr: String = ""
    private val isPhoneBlockedWallet: String = "true" // fixed
    private lateinit var walletUpiAlertDialog: AlertDialog
    private lateinit var upiCreateQRAlertDialog: AlertDialog
    private var walletMobileNo: String = ""
    private var selectedWalletUpiOptionName: String? = null
    private var selectedWalletUpiOptionId: Int? = null
    private var selectedWalletOrUpi: String? = null
    private val allowWalletBooking: Boolean = true // fixed
    private var walletPaymentOptions = mutableListOf<WalletPaymentOption>()
    private var isPermanentPhoneBooking: Boolean = false
    private var isRemarkMandatory: Boolean = false
    private val corpCompanyId: String = "" // fixed
    private var selectedOtherPaymentOption: String? = null
    private var creditDebitCardNo: String? = null
    private var discountOnTotalAmount: String = "0"
    private val blockedFlag: Any = 1 //fixed
    private var blockingDate: String = ""
    private var blockingTimeHours: String = ""
    private var blockingTimeMins: String = ""

    private var partialBlockingDate: String? = null
    private var partialBlockingTimeHours: String? = null
    private var partialBlockingTimeMins: String? = null

    private var blockingAmPm: String = ""
    private var isPermanentPhoneBookingChecked: Boolean = false
    private var isUpiPayment: Boolean = false

    private var partialAmount: Double = 0.0
    private var pendingAmount: Double = 0.0
    private var partialType: String = "1" // 1 for "Do not release"

    private var isPhoneBlocking: Boolean = false
    private val isRoundTripSeat: Boolean = false //fixed
    private val isMatchPrepostponeAmount: String = "false"
    private val allowPrePostPoneOtherBranch: String = "false" // fixed
    private val isRapidBooking: String = "false" //fixed
    private val isFromBusOptApp: String = "true" //fixed
    private val privilegeCardNo: String = "" //fixed
    private var useSmartMiles: String = "false"
    private val offerCoupon: String = "" //fixed
    private val promoCoupon: String = "" //fixed
    private var isBima: Boolean? = null
    private val isRoundTrip: Boolean = false //fixed
    private val onBehalfPaid: String = "0" //fixed
    private val vipTicket: String = "1" //fixed
    private val isFreeBookingAllowed: String = "1" //fixed

    private var sendSmsOnBooking: Boolean = false
    private var sendWhatsAppOnBooking: Boolean = true
    private var mobileNumber: String? = null
    private var alternateMobileNumber: String? = null
    private var emergencyName: String? = null
    private var email: String? = null
    private var paymentType: String = "1" //(by default for cash)
    private var lastSelectedPaymentPosition: Int = 0
    private var noOfSeatsTotal: String? = "0"
    private var noOfSeats: String? = "0"
    private var exNoOfSeats: String? = "0"
    private var selectedSeatNo: String? = ""
    private var onBeHalfUser: Int = 0
    private var onBehalfBranch: String? = null
    private var onBehalf: String? = null
    private var onBehalfOnlineAgent: String? = null
    private var refBookingNo: String? = null
    private var agentType: String? = null
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var serviceNumber: String = ""
    private var busType: String? = null
    private var deptTime: String? = null
    private var arrTime: String? = null
    private var deptDate: String? = null
    private var arrDate: String? = null
    private var boardingPoint: String? = null
    private var droppingPoint: String? = null
    private var returnBoardingPoint: String = ""
    private var returnDroppingPoint: String = ""
    private var droppingId: Int? = null
    private var boardingId: Int? = null
    private var srcDest: String? = null
    private var toolbarSubTitleInfo: String? = null
    private var resId: Long? = null
    private var totalFare: Double = 0.0
    private var individualDiscountAmount: Int = 0
    private lateinit var privilegeResponseModel: PrivilegeResponseModel

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val paymentOptionsList = mutableListOf<SearchModel>()
    private val otherPaymentOptions = mutableListOf<PayGayType>()

    private var bookingCustomRequest = BookingCustomRequest()
    private var passengerList: ArrayList<PassengerDetailsResult> = ArrayList()
    private var ticket = Ticket("")


    private lateinit var binding: ActivityBookingPaymentOptionsBinding
    private val bookingOptionViewModel by viewModel<BookingOptionViewModel<Any?>>()
    private val redelcomViewModel by viewModel<RedelcomViewModel<Any?>>()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var appliedCouponList = mutableListOf<AppliedCoupon>()

    private var isOwnRoute = false
    private var toolbarTitle: String = ""
    private var isPhoneBlockTicket = false
    private var confirmByType: String = ""
    private var boardingStageTime = ""
    private var droppingStageTime = ""
    private var userId = ""
    private var pnrNumber = ""
    private var totalFareString = "0.0"
    private var phoneBlock = ""
    private var locale: String? = ""
    private var isCreditDebitPaymentSelected = false
    private var bookingType = ""
    private var isExtraSeat = false
    private var selectedExtraSeatDetails =
        ArrayList<com.bitla.ts.domain.pojo.service_details_response.SeatDetail>()
    private var selectedSeatDetails =
        ArrayList<com.bitla.ts.domain.pojo.service_details_response.SeatDetail>()

    private var isCancelledClicked = false

    private val phonePeViewModel by viewModel<PhonePeUpiDirectViewModel<Any?>>()
    private var linearLayoutProgressBar: LinearLayout? = null
    private var buttonConfirm: Button? = null
    private var buttonVerify: Button? = null
    private var isPhonePePayment = false
    private var phonePeUpiType: String? = null
    private var phonePeUserNumber: String? = null
    private var phonePeVpa: String? = null
    private var phonePeUPITicketPNR: String? = null
    private var stopPhonePeTransactionStatusApiCall: Boolean = false
    override fun initUI() {
        binding = ActivityBookingPaymentOptionsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        privileges = getPrivilegeBase()

        if (IS_PINELAB_DEVICE) {
            if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                paymentType = "6" // QR code by default
            }
        }
        bookingType = getBookingType1()
        getPref()
        lifecycleScope.launch {
            bookingOptionViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            redelcomViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            phonePeViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        getBookingRequest()
        setPaymentOptionsAdapter()
        handlePrivileges()
        setToolbar()
        setBoardingDroppingDetails()
        getPassengersList()
        getSeatDetails()
        setPassengersAdapter()
        clickListener()
        setPassengerInfo()
        setConfirmPhoneBlockTicketObserver()
        setObserver()
        setPhonePeObserver()

    }

    private fun handlePartialPaymentPrivilege() {
        try {
            if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
                if (privilegeResponseModel.availableAppModes?.allowToDoPartialPayment == true && !isExtraSeat && !(phoneBlock.lowercase()
                        .contains("true")) && isInsuranceChecked != null && !isInsuranceChecked!!
                ) {
                    hidePartialPaymentInfo()
                    binding.layoutPartialAmt.tvPartialPaymentAmt.text =
                        "${getString(R.string.partial_payment_amount)} (${privilegeResponseModel.currency})"
                    binding.layoutPartialAmt.root.visible()
                    if (privilegeResponseModel.partialPaymentLimitType != null) {
                        if (privilegeResponseModel.partialPaymentLimitType == "1") {
                            val partialPercentValue = getPartialPercent()
                            binding.layoutPartialAmt.edtPartialAmt.setText(partialPercentValue.toString())
                        } else {
                            binding.layoutPartialAmt.edtPartialAmt.setText(privilegeResponseModel.partialPaymentLimitValue)
                        }
                    }


                    val boldPartialAmt =
                        SpannableStringBuilder().append("${getString(R.string.total_amount)}:")
                            .bold { append(" ${privilegeResponseModel.currency} $totalFareString") }
                    binding.layoutPartialAmt.tvTotalAmt.text = boldPartialAmt
                    partialAmount = if (privilegeResponseModel.partialPaymentLimitType == "1")
                        getPartialPercent()
                    else
                        privilegeResponseModel.partialPaymentLimitValue?.toDouble() ?: 0.0
                    pendingAmount = totalFareString.toDouble().minus(partialAmount)
                    val boldPartialAmtAmt =
                        SpannableStringBuilder().append("${getString(R.string.remaining_amount)}:")
                            .bold { append(" ${privilegeResponseModel.currency} $pendingAmount") }
                    binding.layoutPartialAmt.tvRemainingAmt.text = boldPartialAmtAmt

                    hourMinuteAdapter()
                    releaseRadioListener()
                    paymentRadioListener()
                    binding.layoutPartialAmt.edtPartialAmt.onChange {
                        if (binding.layoutPartialAmt.edtPartialAmt.text?.isNotEmpty()!!) {
                            partialAmount =
                                binding.layoutPartialAmt.edtPartialAmt.text.toString().toDouble()
                            pendingAmount = totalFareString.toDouble().minus(partialAmount)
                            val boldPartialAmtAmt =
                                SpannableStringBuilder().append("${getString(R.string.remaining_amount)}:")
                                    .bold { append(" ${privilegeResponseModel.currency} $pendingAmount") }
                            binding.layoutPartialAmt.tvRemainingAmt.text = boldPartialAmtAmt
                        } else {
                            partialAmount = 0.0
                            pendingAmount = totalFareString.toDouble().minus(partialAmount)
                            val boldPartialAmtAmt =
                                SpannableStringBuilder().append("${getString(R.string.remaining_amount)}:")
                                    .bold { append(" ${privilegeResponseModel.currency} $pendingAmount") }
                            binding.layoutPartialAmt.tvRemainingAmt.text = boldPartialAmtAmt
                        }

                        collectAmountBtnText(partialAmount.toString())
                    }
                } else
                    binding.layoutPartialAmt.root.gone()
            }
        } catch (e: java.lang.Exception) {
            toast(getString(R.string.partial_payment_is_not_confi))
        }
    }

    private fun getPartialPercent(): Double {
        var percent = 0.0
        if (privilegeResponseModel.partialPaymentLimitValue != null) {
            percent =
                (totalFare / 100.0f) * privilegeResponseModel.partialPaymentLimitValue?.toDouble()!!
        }
        return percent
    }

    private fun paymentRadioListener() {
        binding.layoutPartialAmt.rgPaymentType.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                R.id.rbFullPayment -> {
                    isPartialPayment = false
                    partialPaymentOption = "1"
                    hidePartialPaymentInfo()
                    collectAmountBtnText(totalFareString)
                }

                R.id.rbPartialPayment -> {
                    isPartialPayment = true
                    partialPaymentOption = "2"
                    showPartialPaymentInfo()
                    collectAmountBtnText(partialAmount.toString())
                }
            }
        }
    }

    private fun showPartialPaymentInfo() {
        binding.layoutPartialAmt.rbDoNotRelease.isChecked = true
        binding.layoutPartialAmt.rgReleaseOption.visible()
        binding.layoutPartialAmt.tvPartialPaymentAmt.visible()
        binding.layoutPartialAmt.inputLayoutPartialAmt.visible()
        binding.layoutPartialAmt.tvRemainingAmt.visible()
        binding.layoutPartialAmt.tvTotalAmt.visible()
    }

    private fun hidePartialPaymentInfo() {
        binding.layoutPartialAmt.rgReleaseOption.gone()
        binding.layoutPartialAmt.tvPartialPaymentAmt.gone()
        binding.layoutPartialAmt.inputLayoutPartialAmt.gone()
        binding.layoutPartialAmt.tvRemainingAmt.gone()
        binding.layoutPartialAmt.tvTotalAmt.gone()
        binding.layoutPartialAmt.tvReleaseDate.gone()
        binding.layoutPartialAmt.layoutHhMm.gone()
    }

    private fun releaseRadioListener() {
        binding.layoutPartialAmt.rgReleaseOption.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                R.id.rbDoNotRelease -> {
                    binding.layoutPartialAmt.tvReleaseDate.gone()
                    binding.layoutPartialAmt.layoutHhMm.gone()
                    partialType = "1"
                }

                R.id.rbRelease -> {
                    binding.layoutPartialAmt.tvReleaseDate.visible()
                    binding.layoutPartialAmt.layoutHhMm.visible()
                    partialType = "2"
                    binding.layoutPartialAmt.tvReleaseDate.text = getTodayDate()
                    partialBlockingDate = getTodayDate()
                }
            }
        }
    }

    private fun hourMinuteAdapter() {
        binding.layoutPartialAmt.acHH.setAdapter(
            ArrayAdapter<String>(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                resources.getStringArray(R.array.hourArray)
            )
        )

        binding.layoutPartialAmt.acMM.setAdapter(
            ArrayAdapter<String>(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                resources.getStringArray(R.array.minuteArray)
            )
        )



        binding.layoutPartialAmt.acHH.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                partialBlockingTimeHours = s.toString()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.layoutPartialAmt.acMM.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                partialBlockingTimeMins = s.toString()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun handlePrivileges() {
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel.currency != null)
                currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(this, privilegeResponseModel.currencyFormat)
            if (privilegeResponseModel.isAgentLogin != null)
                isAgentLogin = privilegeResponseModel.isAgentLogin
            if (privilegeResponseModel.isPermanentPhoneBooking != null)
                isPermanentPhoneBooking = true
            if (privilegeResponseModel.walletPaymentOptions != null) {
                walletPaymentOptions = privilegeResponseModel.walletPaymentOptions

                val upiIndex = walletPaymentOptions.indexOfFirst {
                    it.type.equals(getString(R.string.upi), true)
                }
                // removing UPI options
                if (upiIndex != -1)
                    walletPaymentOptions.removeAt(upiIndex)
            }
            if (privilegeResponseModel.allowToShowWhatsappCheckboxInBookingPage == true){
                binding.cardWhatsapp.visible()
                binding.switchWhatsApp.isChecked= true
            }
               else{
                binding.cardWhatsapp.gone()
                binding.switchWhatsApp.isChecked= false

            }
            if (privilegeResponseModel.allowToSendSmsOnBooking == true){
                binding.cardsendSms.visible()
                binding.switchSms.isChecked= false
            }
            else {
                binding.cardsendSms.gone()
                binding.switchSms.isChecked= false

            }
            val role = getUserRole(loginModelPref, isAgentLogin, this)

            if (privilegeResponseModel.appPassengerDetailConfig?.remarks != null) {
                val remarkOption = privilegeResponseModel.appPassengerDetailConfig?.remarks?.option
                if (remarkOption.equals(
                        getString(R.string.hide),
                        true
                    )
                ) {
                    binding.cardRemarks.gone()
                    binding.btnBook.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                } else {
                    binding.cardRemarks.visible()
                    isRemarkMandatory = remarkOption.equals(
                        getString(R.string.mandatory),
                        true
                    )

                    if (isRemarkMandatory) {
                        binding.etRemarks.onChange {
                            if (it.isNotEmpty()) {
                                binding.btnBook.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                                isRemarkMandatory = false
                            } else
                                binding.btnBook.setBackgroundColor(resources.getColor(R.color.button_default_color))
                        }
                    } else {
                        binding.btnBook.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    }
                }
            }
        }
        if (getUserRole(loginModelPref, isAgentLogin, this).contains(
                getString(R.string.role_agent),
                true
            ) || (agentType == "1") || paymentOptionsList.isEmpty()
        ) {
            paymentType = getString(R.string.empty)

            binding.layoutPaymentOptions.gone()
        } else {

            paymentType = "1"

            if (phoneBlock.lowercase().contains("true")) {
                binding.layoutPaymentOptions.gone()
                binding.btnBook.text = getString(R.string.do_phone_booking)

            }
            if (IS_PINELAB_DEVICE) {
                if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                    paymentType = "6"
                }
            }


            if (phoneBlock.contains("true")) {
                paymentType = "1"
                binding.layoutPaymentOptions.gone()
            } else if (selectedExtraSeatDetails.size > 0) {
                binding.layoutPaymentOptions.visible()
            } else if (isExtraSeat) {
                binding.layoutPaymentOptions.gone()
            } else {
                binding.layoutPaymentOptions.visible()
            }
        }
    }


    override fun onPause() {
        if (isHandlerRunning) {
            handler.removeCallbacks(runnable!!)
            isHandlerRunning = false
        }
        super.onPause()
    }

    private fun setPassengerInfo() {
        val totalSeatDetails =
            "${passengerList.size} ${getString(R.string.seats)} ${getString(R.string.details)}"
        binding.tvTotalSeats.text = totalSeatDetails

        val netAmount = "${getString(R.string.netAmount)} : $currency$totalFare"
        binding.tvNetAmt.text = netAmount

        val bookingAmount =
            "${getString(R.string.collet_cash)} $currency ${
                (totalFareString.toDouble()).convert(
                    currencyFormat
                )
            } ${getString(R.string.and)} ${
                getString(
                    R.string.book
                )
            }"
        Timber.d("$bookingAmount = totalFareString - $totalFareString")
        binding.btnBook.text = bookingAmount
        collectAmountBtnText(totalFareString)

        if (passengerList.isNotEmpty() && passengerList[0].contactDetail.isNotEmpty()) {
            emergencyName = passengerList[0].name
            mobileNumber = passengerList[0].contactDetail[0].mobileNumber!!
            alternateMobileNumber = passengerList[0].contactDetail[0].alternateMobileNumber!!
            email = passengerList[0].contactDetail[0].email!!
            binding.tvContactNo.text = mobileNumber
            if (mobileNumber.isNullOrEmpty()) {
                binding.tvContactDetails.gone()
                binding.tvMobileNumber.gone()
                binding.rlContactNumber.gone()
            } else {
                binding.tvContactDetails.visible()
                binding.tvMobileNumber.visible()
                binding.rlContactNumber.visible()
            }
        }
    }

    private fun collectAmountBtnText(totalFareString: String) {
        val bookingAmount =
            "${getString(R.string.collet_cash)} $currency ${
                (totalFareString.toDouble()).convert(
                    currencyFormat
                )
            } ${getString(R.string.and)} ${
                getString(
                    R.string.book
                )
            }"
        binding.btnBook.text = bookingAmount
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initPinelabs()


        if (selectedSeatDetails.isNotEmpty()) {
            for (i in 0..selectedSeatDetails.size.minus(1)) {
                val seatDetails = selectedSeatDetails[i]
                if (passengerList.isNotEmpty() && passengerList.size == selectedSeatDetails.size) {
                    seatDetails.additionalFare = passengerList[i].additionalFare?.toDouble()
                    seatDetails.discountAmount = passengerList[i].discountAmount?.toDouble()
                    seatDetails.passportIssuedDate = passengerList[i].passportIssuedDate
                    seatDetails.passportExpiryDate = passengerList[i].passportExpiryDate
                    seatDetails.placeOfIssue = passengerList[i].placeOfIssue
                    seatDetails.nationality = passengerList[i].nationality
                    seatDetails.idCardNumber = passengerList[i].idCardNumber
                    seatDetails.idCardType = passengerList[i].idCardType?.toInt()
                    seatDetails.isPrimary = passengerList[i].isPrimary
                    seatDetails.age = passengerList[i].age
                    seatDetails.name = passengerList[i].name
                    seatDetails.firstName = passengerList[i].firstName
                    seatDetails.lastName = passengerList[i].lastName

                    seatDetails.sex = passengerList[i].sex

                    seatDetails.mealRequired = passengerList[i].mealRequired
                    seatDetails.selectedMealType = passengerList[i].selectedMealType
                }
            }
        }

        if (isNetworkAvailable()) {
            binding.includeProgress.progressBar
            fareBreakupApi()
        } else
            noNetworkToast()

    }

    private fun initPinelabs() {
        val intent = Intent()
        intent.setAction(PLUTUS_SMART_ACTION)
        intent.setPackage(PLUTUS_SMART_PACKAGE)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun isInternetOnCallApisAndInitUI() {
        if (selectedSeatDetails.isNotEmpty()) {
            for (i in 0..selectedSeatDetails.size.minus(1)) {
                val seatDetails = selectedSeatDetails[i]
                if (passengerList.isNotEmpty() && passengerList.size == selectedSeatDetails.size) {
                    seatDetails.apply {
                        additionalFare = passengerList[i].additionalFare?.toDouble()
                        discountAmount = passengerList[i].discountAmount?.toDouble()
                        passportIssuedDate = passengerList[i].passportIssuedDate
                        passportExpiryDate = passengerList[i].passportExpiryDate
                        placeOfIssue = passengerList[i].placeOfIssue
                        nationality = passengerList[i].nationality
                        idCardNumber = passengerList[i].idCardNumber
                        idCardType = passengerList[i].idCardType?.toInt()
                        isPrimary = passengerList[i].isPrimary
                        age = passengerList[i].age
                        name = passengerList[i].name
                        sex = passengerList[i].sex
                    }
                }
            }
        }

        if (isNetworkAvailable()) {
            binding.includeProgress.progressBar
            fareBreakupApi()
        } else
            noNetworkToast()

        binding.layoutPaymentOptions.gone()


    }

    private fun getPassengersList() {
        passengerList = retrieveSelectedPassengers()

        if (isInsuranceChecked != null && isInsuranceChecked!! && !passengerList.any { it.isExtraSeat }) {
            passengerList.forEach {
                it.name = "${it.firstName} ${it.lastName}"
            }
        }

        val bookExtraSeatNoList = mutableListOf<String>()

        Timber.d("selectedSeatNoPassengerList -Size ${passengerList.size}")

        if (passengerList.isNotEmpty()) {
            totalFare = 0.0
            passengerList.forEach {
                totalFare += it.fare?.toDouble() ?: 0.0
                individualDiscountAmount += it.discountAmount?.toInt() ?: 0
                bookExtraSeatNoList.add(it.seatNumber.toString())
                Timber.d("selectedSeatNoPassengerList ${it.seatNumber}")
            }
        }

        if (passengerList.any { it.isExtraSeat }) {
            isExtraSeat = true
            val commaSeparatedExtraSeats = TextUtils.join(",", bookExtraSeatNoList)
            selectedSeatNo = commaSeparatedExtraSeats

//            for (i in 0..passengerList.size.minus(1)) {
//                selectedSeatDetails[i].fare = passengerList[i].fare
//                selectedSeatDetails[i].number = passengerList[i].seatNumber
//            }
        }
        selectedSeatDetails.forEach {
            it.additionalFare = passengerList[0].additionalFare?.toDouble()
        }

        passengerList.forEach {
            it.additionalFare = passengerList[0].additionalFare
        }
    }

    private fun getSeatDetails() {
        selectedSeatDetails = retrieveSelectedSeats()
        selectedExtraSeatDetails = retrieveSelectedExtraSeats()

        noOfSeatsTotal = selectedSeatDetails.size.toString()
        noOfSeats = selectedSeatDetails.size.minus(selectedExtraSeatDetails.size).toString()
        exNoOfSeats = selectedExtraSeatDetails.size.toString()

        if (!passengerList.any { it.isExtraSeat }) {
            selectedSeatNo = retrieveSelectedSeatNumber()
        }
    }

    private fun getBookingRequest() {
        bookingCustomRequest = retrieveBookingCustomRequest()
        agentType = if (bookingCustomRequest.selected_booking_id.toString() == "12")
            "3"
        else
            bookingCustomRequest.selected_booking_id.toString()

        Timber.d("agentType $agentType")

        onBeHalfUser = bookingCustomRequest.branch_user
        onBehalfBranch = bookingCustomRequest.branch_id.toString()
        refBookingNo = bookingCustomRequest.reference_no
        onBehalf = bookingCustomRequest.offline_agent_on_behalf.toString()
        onBehalfOnlineAgent = bookingCustomRequest.online_agent_on_behalf.toString()
    }
    val fareBreakup = mutableListOf<FareBreakUpHash>()
    private fun setObserver() {

        redelcomViewModel.redelComData.observe(this) {
            if (it.code == 200) {
                handler.removeCallbacks(runnable!!)
                isHandlerRunning = false
                val intent = Intent(this, TicketDetailsActivity::class.java)
                intent.putExtra(
                    getString(R.string.TICKET_NUMBER),
                    pnrNumber
                )
                intent.putExtra(
                    "activityName",
                    BookingPaymentOptionsActivity::class.java
                )
                intent.putExtra("activityName2", "booking")
                intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                startActivity(intent)
                finish()
            } else if (it.code == 402) {
                handler.removeCallbacks(runnable!!)
                isHandlerRunning = false
                if (countDownTimer != null) {
                    countDownTimer!!.cancel()
                }
                redelcomPaymentDialog!!.dismiss()
                transactionFailedInterfaceDialog(this, it.message, this)


            }


        }

        bookingOptionViewModel.pinelabTransactionData.observe(this) {
            binding.includeProgress.progressBar.gone()

            if (it != null && it.code != null) {
                when (it.code) {
                    200 ->  {
                        if (progressDialog?.isShowing == true) {
                            progressDialog?.dismiss()
                        }
                        if (it.responseCode == 0 && it.data!!.code == null ) {
                            val intent = Intent(this, TicketDetailsActivity::class.java)
                            intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                            intent.putExtra("activityName2", "booking")

                            intent.putExtra(getString(R.string.TICKET_NUMBER), pinelabBillingRefNo)
                            intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                            startActivity(intent)
                            finish()
                        }
                        else if(it.message == "This ticket is released and now its ready for re-booking."){
                            // do nothing
                            if (progressDialog?.isShowing == true) {
                                progressDialog?.dismiss()
                            }
                            toast("Payment failed! Please try again")
                        }
                        else {
                            toast(it.message)
                            var reqBody : ReqBodyPinelab?= null
                            if(isExtraSeatBooking){
                                val jsonObj = JSONObject(pinelabResponseString!!)
                                reqBody =
                                    ReqBodyPinelab(
                                        loginModelPref.api_key,
                                        pnrNumber,
                                        true,
                                        true,
                                        resId.toString(),
                                        sourceId,
                                        destinationId,
                                        pinelab_response = jsonObj
                                    )
                            }else{
                                val jsonObj = JSONObject(pinelabResponseString!!)
                                reqBody =
                                    ReqBodyPinelab(
                                        loginModelPref.api_key,
                                        pnrNumber,
                                        true,
                                        pinelab_response = jsonObj
                                    )
                            }
                            bookingOptionViewModel.pinelabStatusApi(reqBody, pinelab_transaction_status_api)
                            pinelabBillingRefNo = pinelabResponseData!!.detail!!.billingRefNo!!

                        }
                    }
                    211 -> {
                        if (progressDialog?.isShowing == true) {
                            progressDialog?.dismiss()
                        }
                        toast("Payment failed! Please try again")
                    }

                    401 -> {
                        DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    }

                    402 -> {
                        it.message.let { it1 -> toast(it1) }
                    }

                    else -> {
                        if (it.message != null) {
                            toast(it.message)
                        }
                    }
                }
            }else{
                if (progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
                toast("Error occured! Please try again")
            }


        }

        bookingOptionViewModel.loadingState.observe(this) { it ->
            Timber.d("LoadingState ${it.status}")
            when (it) {
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }
        }

        dashboardViewModel.releaseTicketResponseViewModel.observe(this) {
            if (progressDialog?.isShowing == true) {
                progressDialog?.dismiss()
            }
            if (it != null) {
                if (it.code == 200) {
                    if (!it.message.isNullOrEmpty()) {
                        it.message.let { it1 -> toast(it1) }
                    }
                } else if (it.code == 401) {
                    DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }




        bookingOptionViewModel.fareBreakup.observe(this) {
            binding.includeProgress.progressBar.gone()
            Timber.d("fareBreakupResponse $it")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (it.fare_break_up_hash != null) {

                            for (i in 0..it.fare_break_up_hash.size.minus(1)) {
                                if (it.fare_break_up_hash[i].value.toString().toDouble() > 0.0)
                                    fareBreakup.add(it.fare_break_up_hash[i])
                            }
                            setChargesAdapter(fareBreakup)
                            // checking for if total fare comes as negative/minus
                            totalFareString =
                                if (it.total_fare.toString().contains("-")) {
                                    "0.0"
                                } else {
                                    it.total_fare.toString()
                                }

                            if (totalFareString == "0.0") {
                                binding.layoutPartialAmt.rbFullPayment.isEnabled = false
                                binding.layoutPartialAmt.rbFullPayment.isEnabled = false
                                binding.layoutPartialAmt.rbFullPayment.isClickable = false
                                binding.layoutPartialAmt.rbPartialPayment.isClickable = false
                            }
                            setPassengerInfo()
                            setPaymentOptionsAdapter()
                            handlePartialPaymentPrivilege()
                        }
                    }

                    401 -> {
                        DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    }

                    402 -> {
                        it.message?.let { it1 -> toast(it1) }
                    }

                    else -> {
                        if (it.message != null) {
                            toast(it.message)
                        } else {
                            toast(message = it.status.message)
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        bookingOptionViewModel.bookSeatWithExtraSeat.observe(this) {

            binding.includeProgress.progressBar.gone()
            Timber.d("bookTicketWithExtraSeatFResponse $it")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (it.result.payment_initiatives == "RedelcomPay") {
                            DialogUtils.createRedelcomPaymentDialog(this, this, this)
                            if (terminalId != "") {
                                handler.postDelayed(Runnable {
                                    isHandlerRunning = true
                                    handler.postDelayed(runnable!!, delay.toLong())
                                    pnrNumber = it.result.ticket_number
                                    redelcomViewModel.redelcomPgStatusApi(
                                        loginModelPref.api_key,
                                        locale!!,
                                        pnrNumber,
                                        terminalId
                                    )
                                }.also { runnable = it }, delay.toLong())

                            }
                        } else if (paymentType == "4") {
                            if (isUpiPayment) {
//                                val qrCode = walletUpiAlertDialog.findViewById<android.widget.Button>(R.id.qr_code_image).visible()

                                bookTicketTotalFare = it.result.total_fare.toString()
                                bookTicketPnr = it.result.ticket_number
                                callUPICreateQrCodeApi()

                            } else {
                                if (it.result != null) {
                                    bookTicketTotalFare = it.result.total_fare.toString()
                                    bookTicketPnr = it.result.ticket_number
                                }

                                if (isNetworkAvailable())
                                    walletOtpGenerationApi()
                                else
                                    noNetworkToast()
                            }

                        } else {
                            val intent = Intent(this, TicketDetailsActivity::class.java)
                            intent.putExtra(
                                getString(R.string.TICKET_NUMBER),
                                it.result.ticket_number
                            )
                            intent.putExtra(
                                "activityName",
                                BookingPaymentOptionsActivity::class.java
                            )
                            intent.putExtra("activityName2", "booking")
                            intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                            startActivity(intent)
                            finish()
                        }
                        PreferenceUtils.removeKey("AutoDiscountCouponCode")
                        val emptyArray = arrayListOf<SeatWiseFare>()
                        PreferenceUtils.putSelectedCoupon(emptyArray)
                    }

                    401 -> {
                        DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    }

                    400 -> {
                        /*if(it.message == "Transaction number already used" && isCreditDebitPaymentSelected) {
                            DialogUtils.creditDebitDialog(this, this)
                        }*/
                        lastSelectedPaymentPosition = 0
                        setPaymentOptionsAdapter()
                        toast(it.message)
                        /*  DialogUtils.createRedelcomPaymentDialog(this,this)
                          if(terminalId != ""){
                              pnrNumber = it.result.ticket_number
                              redelcomViewModel.redelcomPgStatusApi(loginModelPref.api_key,locale!!,pnrNumber,terminalId)
                          }*/

                    }

                    else -> {
                        if (it.message != null) {
                            it.message.let { it1 -> toast(it1) }
                        }
                        if (::walletUpiAlertDialog.isInitialized) {
                            walletUpiAlertDialog.findViewById<ProgressBar>(
                                R.id.progress_bar
                            ).gone()
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        bookingOptionViewModel.bookTicketFull.observe(this) {
            binding.includeProgress.progressBar.gone()
            Timber.d("bookTicketWithExtraSeatFResponse $it")
            if (it != null) {
                if (progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
                when (it.code) {
                    200 -> {
                        lastBookedSeatsNumber = it.result.seat_numbers
                        lastBookedTicketNumber = it.result.ticket_number
                        if (it.result.payment_initiatives == "RedelcomPay") {
                            DialogUtils.createRedelcomPaymentDialog(this, this, this)
                            if (terminalId != "") {
                                handler.postDelayed(Runnable {
                                    isHandlerRunning = true
                                    handler.postDelayed(runnable!!, delay.toLong())
                                    pnrNumber = it.result.ticket_number
                                    redelcomViewModel.redelcomPgStatusApi(
                                        loginModelPref.api_key,
                                        locale!!,
                                        pnrNumber,
                                        terminalId
                                    )
                                }.also { runnable = it }, delay.toLong())

                            }
                        } else if (paymentType == "4") {
                            if (isUpiPayment) {
//                                val qrCode = walletUpiAlertDialog.findViewById<android.widget.Button>(R.id.qr_code_image).visible()

                                bookTicketTotalFare = it.result.total_fare.toString()
                                bookTicketPnr = it.result.ticket_number
                                callUPICreateQrCodeApi()

                            } else {
                                if (it.result != null) {
                                    bookTicketTotalFare = it.result.total_fare.toString()
                                    bookTicketPnr = it.result.ticket_number
                                }

                                if (isNetworkAvailable())
                                    walletOtpGenerationApi()
                                else
                                    noNetworkToast()
                            }
                        } else if (paymentType == "5") { //PhonePeUPI Payment
                            phonePeUPITicketPNR = it.result.ticket_number
                            callPhonePeDirectUPIApp(phonePeUPITicketPNR, it.result.total_fare)
                        } else if (paymentType == "6") {
                            isExtraSeatBooking = false
                            pinelabPaymentType(PAYMENT_QR, it, false)

                        } else if (paymentType == "7") {
                            isExtraSeatBooking = false
                            pinelabPaymentType(PAYMENT_DEBIT_CREDIT, it, false)
                        } else {
                            val intent = Intent(this, TicketDetailsActivity::class.java)
                            intent.putExtra(
                                getString(R.string.TICKET_NUMBER),
                                it.result.ticket_number
                            )
                            intent.putExtra(
                                "activityName",
                                BookingPaymentOptionsActivity::class.java
                            )
                            intent.putExtra("activityName2", "booking")
                            intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                            startActivity(intent)
                            finish()
                        }
                        PreferenceUtils.removeKey("AutoDiscountCouponCode")
                        val emptyArray = arrayListOf<SeatWiseFare>()
                        PreferenceUtils.putSelectedCoupon(emptyArray)
                    }

                    401 -> {
                        DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    }

                    400 -> {
                        /*if(it.message == "Transaction number already used" && isCreditDebitPaymentSelected) {
                            DialogUtils.creditDebitDialog(this, this)
                        }*/
                        lastSelectedPaymentPosition = 0
                        setPaymentOptionsAdapter()
                        toast(it.message.toString())
                        /*  DialogUtils.createRedelcomPaymentDialog(this,this)
                          if(terminalId != ""){
                              pnrNumber = it.result.ticket_number
                              redelcomViewModel.redelcomPgStatusApi(loginModelPref.api_key,locale!!,pnrNumber,terminalId)
                          }*/

                    }

                    else -> {
                        if (it.message != null) {
                            it.message.let { it1 -> toast(it1.toString()) }
                        }
                        if (::walletUpiAlertDialog.isInitialized) {
                            walletUpiAlertDialog.findViewById<ProgressBar>(
                                R.id.progress_bar
                            ).gone()
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        bookingOptionViewModel.bookExtraSeat.observe(this) {
            binding.includeProgress.progressBar.gone()
            Timber.d("bookExtraSeatResponse $it")
            if (it != null) {
                if (progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
                if(it.ticketNumber != null){
                    lastBookedSeatsNumber = it.seat_numbers
                    lastBookedTicketNumber = it.ticketNumber
                    bookExtraSeatData = it
                    if (it.paymentInitiatives == "RedelcomPay") {
                        DialogUtils.createRedelcomPaymentDialog(this, this, this)
                        if (terminalId != "") {
                            handler.postDelayed(Runnable {
                                isHandlerRunning = true
                                handler.postDelayed(runnable!!, delay.toLong())
                                pnrNumber = it.ticketNumber
                                redelcomViewModel.redelcomPgStatusApi(
                                    loginModelPref.api_key,
                                    locale!!,
                                    pnrNumber,
                                    terminalId
                                )
                            }.also { runnable = it }, delay.toLong())

                        }
                    } else if (paymentType == "6") {
                        isExtraSeatBooking = true
                        pinelabPaymentType(PAYMENT_QR, it, true)
                    } else if (paymentType == "7") {
                        isExtraSeatBooking = true
                        pinelabPaymentType(PAYMENT_DEBIT_CREDIT, it, true)
                    }
                    else if (it.passenger_details != null && it.passenger_details.isNotEmpty()) {

                        val intent = Intent(this, TicketDetailsActivity::class.java)
                        intent.putExtra(
                            "activityName",
                            BookingPaymentOptionsActivity::class.java
                        )
                        intent.putExtra("activityName2", "booking")

                        intent.putExtra(
                            getString(R.string.TICKET_NUMBER),
                            it.passenger_details[0].pnr_number
                        )
                        startActivity(intent)
                        finish()
                    }
                }else{
                    when (it.code) {
                        200 -> {

                        }

                        401 -> {
                            DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )
                        }

                        else -> {
                            if (it.message != null) {
                                toast(it.message.toString())
                            } else {
                                toast(getString(R.string.opps))
                            }
                        }
                    }
                }

            } else {
                toast(getString(R.string.server_error))
            }
        }

        bookingOptionViewModel.walletOtpGeneration.observe(this) {

            binding.includeProgress.progressBar.gone()
            Timber.d("walletOtpGenerationResponse $it")

            if (it != null) {
                if (it.code == 200) {
                    if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                        walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputLayout>(
                            R.id.layout_otp
                        ).visible()

                        walletUpiAlertDialog.findViewById<Button>(
                            R.id.btnConfirm
                        ).text = getString(R.string.confirm_validate)
                        walletUpiAlertDialog.findViewById<Button>(
                            R.id.btnConfirm
                        ).isClickable = true

                        walletUpiAlertDialog.findViewById<Button>(
                            R.id.btnConfirm
                        ).setBackgroundResource(R.drawable.button_selected_bg)


                        walletUpiAlertDialog.findViewById<TextView>(
                            R.id.tvSubTitle
                        ).gone()

                        walletUpiAlertDialog.findViewById<RecyclerView>(
                            R.id.rvWalletUpi
                        ).gone()
                    } else {
                        if (!it.message.isNullOrEmpty()) {
                            it.message.let { it1 -> toast(it1) }
                        }
                    }

                } else if (it.code == 401) {
                    DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )
                } else {
                    if (!it.message.isNullOrEmpty()) {
                        it.message.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        bookingOptionViewModel.validateWalletOtp.observe(this) {
            binding.includeProgress.progressBar.gone()
            Timber.d("validateWalletOtpResponse $it")
            if (it != null) {
                if (it.code == 200) {
                    if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null)
                        walletUpiAlertDialog.cancel()

                    val intent = Intent(this, TicketDetailsActivity::class.java)
                    intent.putExtra(getString(R.string.TICKET_NUMBER), bookTicketPnr)
                    intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                    intent.putExtra("activityName2", "booking")

                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                    finish()
                } else if (it.code == 401) {
                    DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )
                } else {
                    if (it.message != null) {
                        it.message.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        dashboardViewModel.releaseTicketResponseViewModel.observe(this) {

            if (it != null) {
                if (it.code == 200) {
                    if (!it.message.isNullOrEmpty()) {
                        it.message.let { it1 -> toast(it1) }
                    }
                } else if (it.code == 401) {
                    DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        bookingOptionViewModel.upiCreateQRCodeObserver.observe(this) {

            walletUpiAlertDialog.findViewById<ProgressBar>(
                R.id.progress_bar
            ).gone()

            Timber.d("upCreateQRCodeResponse $it")

            if (it != null) {
                if (it.code == 200) {

                    if (it.data.body.resultInfo.resultStatus == "FAILURE") {
                        toast(it.data.body.resultInfo.resultMsg)

                    } else {
                        val base64String = it.data.body.image
                        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                        val decodedImage =
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        
                        upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                            context = this,
                            isFromAgentRechargePG = false,
                            dialogSingleButtonListener = this
                        )
                        if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {

                            upiCreateQRAlertDialog.findViewById<ImageView>(
                                R.id.qr_code_image
                            ).setImageBitmap(decodedImage)
                        }

                        callUPICheckStatusApi()
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        bookingOptionViewModel.upiTranxStatusObserver.observe(this) {

            binding.includeProgress.progressBar.gone()
            Timber.d("upCreateQRCodeResponse $it")

            if (it != null) {
                if (it.code == 200) {
                    toast(it.message)

                    val intent = Intent(this, TicketDetailsActivity::class.java)
                    intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                    intent.putExtra("activityName2", "booking")

                    intent.putExtra(getString(R.string.TICKET_NUMBER), it.data?.ticketNumber)
                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                    finish()
                } else if (it.code == 400) {
                    if (!isCancelledClicked) {
                        callUPICheckStatusApi()
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setBoardingDroppingDetails() {
        binding.apply {
            layoutBoardingDropping.tvBoardingDate.text = deptDate?.let {
                inputFormatToOutput(
                    it,
                    DATE_FORMAT_D_M_Y_SLASH,
                    DATE_FORMAT_D_M_YY
                )
            }
            layoutBoardingDropping.tvBoardingTime.text = boardingStageTime
            layoutBoardingDropping.tvDroppingDate.text = arrDate?.let {
                inputFormatToOutput(
                    it,
                    DATE_FORMAT_D_M_Y_SLASH,
                    DATE_FORMAT_D_M_YY
                )
            }
            layoutBoardingDropping.tvDroppingTime.text = droppingStageTime
            layoutBoardingDropping.tvBoarding.text = "$boardingPoint, $source"
            layoutBoardingDropping.tvDropping.text = "$droppingPoint, $destination"
        }
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()
        loginModelPref = PreferenceUtils.getLogin()

        if (intent.hasExtra(getString(R.string.applied_coupons)))
            appliedCouponList =
                intent.getSerializableExtra(getString(R.string.applied_coupons)) as MutableList<AppliedCoupon>

        if (appliedCouponList.isNotEmpty()) {
            binding.viewCoupons.visible()
            setCouponsAdapter()
        } else
            binding.viewCoupons.gone()

        binding.switchSms.setOnCheckedChangeListener { buttonView, isChecked ->
            sendSmsOnBooking = isChecked
            if (isChecked) {
                binding.smsText.visible()
            }else {
                binding.smsText.gone()
            }
        }
        binding.switchWhatsApp.setOnCheckedChangeListener { buttonView, isChecked ->
            sendWhatsAppOnBooking = isChecked
        }

        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }
        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!
        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            busType = result?.bus_type ?: getString(R.string.empty)
            deptTime = result?.dep_time ?: getString(R.string.empty)
            arrTime = result?.arr_time ?: getString(R.string.empty)
            deptDate = result?.dep_date ?: getString(R.string.empty)
            arrDate = result?.arr_date ?: getString(R.string.empty)
            serviceNumber = result?.number ?: getString(R.string.empty)
        }
        if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
            val droppingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)!!
            droppingPoint = droppingStageDetail.name!!
            droppingId = droppingStageDetail.id

        }

        if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
            val boardingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)!!

            boardingPoint = boardingStageDetail.name!!
            boardingId = boardingStageDetail.id


        }

        if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
            droppingStageTime =
                PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)?.time.toString()
        }
        if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
            boardingStageTime =
                PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)?.time.toString()
        }
        if (privileges != null)

        privileges?.let { privilegeResponseModel ->
            pinSize = privilegeResponseModel.pinCount ?: 6
            shouldExtraSeatBooking = privilegeResponseModel.pinBasedActionPrivileges?.extraSeatBook ?: false
            shouldPhoneBlocking = privilegeResponseModel.pinBasedActionPrivileges?.phoneBlocking ?: false
            shouldTicketConfirmation = privilegeResponseModel.pinBasedActionPrivileges?.ticketConfirmation ?: false
            excludeTicketConfirmation =
                privilegeResponseModel.pinBasedActionPrivileges?.excludeTicketConfirmation ?: mutableListOf<ExcludeTicketConfirmation>()
            currentCountry = privilegeResponseModel.country ?: ""
        }


        if (PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false) != null)
            isOwnRoute = PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false)!!

        phoneBlock = PreferenceUtils.getString("ISphoneBlock")!!
        val phoneBlockConstraints = PreferenceUtils.getString("phoneBlock")
        if (!phoneBlock.isNullOrEmpty()) {
            if (phoneBlock.lowercase().contains("true")) {
                binding.layoutPaymentOptions.gone()
                binding.btnBook.text = getString(R.string.do_phone_booking)

            }
        }

        isInsuranceChecked = PreferenceUtils.getPreference(PREF_IS_INSURANCE_CHECKED, false)

        Timber.d("isInsuranceChecked $isInsuranceChecked")

    }

    private fun setToolbar() {
        srcDest = "$source-$destination"
        binding.toolbar.toolbarHeaderText.text = srcDest
        if (travelDate.isNotEmpty()) {
            //toolbarSubTitleInfo = "${getDateDMYY(travelDate)} | $deptTime | $busType"
            toolbarSubTitleInfo = if (serviceNumber.isNotEmpty())
                "$serviceNumber | ${travelDate.let { getDateDMYY(it) }} $deptTime | $busType"
            else
                "${travelDate.let { getDateDMYY(it) }} $$deptTime | $busType"
            binding.toolbar.toolbarSubtitle.text = toolbarSubTitleInfo
        }
        if (intent.getStringExtra(getString(R.string.toolbar_title)) != null) {
            toolbarTitle = intent.getStringExtra(getString(R.string.toolbar_title))!!
            binding.toolbar.tvCurrentHeader.text = toolbarTitle
        }

        PreferenceUtils.putString(getString(R.string.toolbar_title), toolbarTitle)
    }

    private fun clickListener() {
        binding.layoutBookingDetailsFixed.setOnClickListener(this)
        binding.btnBook.setOnClickListener(this)
        binding.toolbar.imgBack.setOnClickListener(this)
        binding.layoutPartialAmt.tvReleaseDate.setOnClickListener(this)
    }

    private fun setPaymentOptionsAdapter() {
        paymentOptionsList.clear()

//        if (selectedExtraSeatDetails.size>0) {
//            binding.layoutPaymentOptions.visible()
//        }
//        else if (isExtraSeat){
//            binding.layoutPaymentOptions.gone()
//        }
//        else{
//            binding.layoutPaymentOptions.visible()
//        }

        val isFreeTicket =
            appliedCouponList.any { it.coupon_type == getString(R.string.free_ticket) }

        // Vip Booking
        val isVipTicket = appliedCouponList.any { it.coupon_type == getString(R.string.vip_ticket) }

        if (isExtraSeat) {
            binding.layoutPaymentOptions.gone()
        }
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {


            if (IS_PINELAB_DEVICE) {
                if (privilegeResponseModel.allowUserToUsePinelabDevicesForUpiPayment) {
                    val pinelabQR = SearchModel()
                    pinelabQR.id = "6"
                    pinelabQR.name = getString(R.string.pinelab_QR)

                    if (selectedExtraSeatDetails.size > 0) {
                        paymentOptionsList.add(pinelabQR)
                    } else if (!isExtraSeat) {
                        paymentOptionsList.add(pinelabQR)
                    }

                    val pinelabCreditDebit = SearchModel()
                    pinelabCreditDebit.id = "7"
                    pinelabCreditDebit.name = getString(R.string.pinelab_debitcard)

                    if (selectedExtraSeatDetails.size > 0) {
                        paymentOptionsList.add(pinelabCreditDebit)
                    } else if (!isExtraSeat) {
                        paymentOptionsList.add(pinelabCreditDebit)
                    }
                }
            }
            if (privilegeResponseModel.allowCashCreditOptionsInBooking != null && privilegeResponseModel.allowCashCreditOptionsInBooking) {
                val cash = SearchModel()
                cash.id = "1"
                cash.name = getString(R.string.cash)

                val creditDebitCard = SearchModel()
                creditDebitCard.id = "2"
                creditDebitCard.name = getString(R.string.credit_debit)

                paymentOptionsList.add(cash)
                if (selectedExtraSeatDetails.size > 0) {
                    paymentOptionsList.add(creditDebitCard)
                } else if (!isExtraSeat) {
                    paymentOptionsList.add(creditDebitCard)
                }
            }
            if (privilegeResponseModel.allowWalletAndUpiOptionsInBookingPage != null
                && privilegeResponseModel.allowWalletAndUpiOptionsInBookingPage
                && bookingType != getString(R.string.online_agent)
                && bookingType != getString(R.string.offline_agent)
                && bookingType != getString(R.string.branch)
            ) {
                val walletUpi = SearchModel()
                walletUpi.id = "4"
                walletUpi.name = getString(R.string.wallet_upi)

                if (selectedExtraSeatDetails.size > 0) {
                    paymentOptionsList.add(walletUpi)
                } else if (!isExtraSeat) {
                    paymentOptionsList.add(walletUpi)
                }
            }
            if (privilegeResponseModel.allowToConfigurePaymentOptionsInBookingPage) {
                val others = SearchModel()
                others.id = "3"
                others.name = getString(R.string.others)

                if (selectedExtraSeatDetails.size > 0) {
                    paymentOptionsList.add(others)
                } else if (!isExtraSeat) {
                    paymentOptionsList.add(others)
                }
            }
            if (privilegeResponseModel.allowDepositOptionsInBooking) {
                val deposit = SearchModel()
                deposit.id = "158"
                deposit.name = getString(R.string.deposit)

                if (selectedExtraSeatDetails.size > 0) {
                    paymentOptionsList.add(deposit)
                } else if (!isExtraSeat) {
                    paymentOptionsList.add(deposit)
                }
            }

//            toast("isFreeTicket $isFreeTicket, isVipTicket $isVipTicket, totalFareString $totalFareString\"")
//            Timber.d("setPaymentOptionsAdapter() isFreeTicket $isFreeTicket, isVipTicket $isVipTicket, totalFareString $totalFareString")

            if(privilegeResponseModel.allowToShowPhonePeDirectUPIPaymentOptionInBookingPage == true && totalFareString != "0.0") {
                val phonePeUPI = SearchModel()
                phonePeUPI.id = "5"
                phonePeUPI.name = "Phonepe UPI"
                paymentOptionsList.add(phonePeUPI)

            }


        }
        layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPaymentOptions.layoutManager = layoutManager
        val filterAdapter =
            FilterAdapter(this, this, paymentOptionsList, lastSelectedPaymentPosition, true)
        binding.rvPaymentOptions.adapter = filterAdapter


    }

    private fun setPassengersAdapter() {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPassengers.layoutManager = layoutManager
        val bookPassengersAdapter =
            BookPassengersAdapter(this, this, passengerList)
        binding.rvPassengers.adapter = bookPassengersAdapter
    }

    private fun setChargesAdapter(fareBreakUpHashList: List<FareBreakUpHash>) {
        val serviceTaxIndex = fareBreakUpHashList.indexOfFirst {
            (it.label.contains(getString(R.string.service_tax_amount))) && (it.value.toString()
                .toDouble() > 0.0)
        }
        if (serviceTaxIndex != -1) {
            val totalFareIndex = fareBreakUpHashList.indexOfFirst {
                (it.label == getString(R.string.total_fare))
            }
            if (totalFareIndex != -1) {
                fareBreakUpHashList[totalFareIndex].value =
                    "${fareBreakUpHashList[totalFareIndex].value}"
            }
        }

        layoutManager =
            GridLayoutManager(this, 2)
        binding.rvCharges.layoutManager = layoutManager
        val bookingChargesAdapter =
            BookingChargesAdapter(
                this,
                fareBreakUpHashList as ArrayList<FareBreakUpHash>,
                currency,
                currencyFormat
            )
        binding.rvCharges.adapter = bookingChargesAdapter
    }

    private fun setCouponsAdapter() {
        layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCoupons.layoutManager = layoutManager
        val couponsAdapter =
            CouponsAdapter(this, appliedCouponList, currencyFormat)
        binding.rvCoupons.adapter = couponsAdapter
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.img_back -> onBackPressed()
            R.id.layoutBookingDetailsFixed -> {
                if (binding.layoutBookingDetailsHidden.isVisible) {
                    TransitionManager.beginDelayedTransition(
                        binding.cardBookingDetails,
                        AutoTransition()
                    )
                    binding.layoutBookingDetailsHidden.gone()
                    binding.imgBookingDetails.setImageResource(R.drawable.ic_arrow_down)
                    binding.imgBookingDetails.imageTintList = ColorStateList.valueOf(
                        (resources.getColor(
                            R.color.white
                        ))
                    )
                } else {
                    TransitionManager.beginDelayedTransition(
                        binding.cardBookingDetails,
                        AutoTransition()
                    )
                    binding.layoutBookingDetailsHidden.visible()
                    binding.imgBookingDetails.setImageResource(R.drawable.ic_arrow_up)
                }
            }

            R.id.btnBook -> {
                getPassengersList()
                Timber.d("btnBook ${binding.btnBook.text}")

                if (isRemarkMandatory) {
                    toast(getString(R.string.validate_remarks))
                } else {
                    if (isPhoneBlockTicket) {
                        if (isNetworkAvailable()) {
                            DialogUtils.blockSeatsDialog(
                                showMsg = false,
                                this,
                                getString(R.string.confirmBooking),
                                getString(R.string.selected_seat_s_will_be_assigned),
                                srcDest = srcDest ?: getString(R.string.dash),
                                journeyDate = toolbarSubTitleInfo ?: getString(R.string.dash),
                                noOfSeats = "$noOfSeatsTotal",
                                seatNo = "$selectedSeatNo",
                                getString(R.string.goBack),
                                getString(R.string.confirmBooking),
                                this
                            )
                        } else {
                            noNetworkToast()
                        }
                    } else {

                        if (binding.btnBook.text.toString() != getString(R.string.do_phone_booking)) {

                            if (binding.layoutPartialAmt.rbPartialPayment.isChecked) {
                                if (partialAmount > totalFare)
                                    toast(getString(R.string.partial_amount_validation))
                                else if (privilegeResponseModel.partialPaymentLimitType != null && privilegeResponseModel.partialPaymentLimitType == "2" && privilegeResponseModel.partialPaymentLimitValue != null && partialAmount < privilegeResponseModel.partialPaymentLimitValue?.toDouble()!!)
                                    toast("${getString(R.string.less_partial_amount_validation)} ${privilegeResponseModel.partialPaymentLimitValue}")
                                else if (privilegeResponseModel.partialPaymentLimitType != null && privilegeResponseModel.partialPaymentLimitType == "1" && privilegeResponseModel.partialPaymentLimitValue != null && partialAmount < getPartialPercent())
                                    toast("${getString(R.string.less_partial_amount_validation)} ${getPartialPercent()}")
                                else if (partialAmount == 0.0)
                                    toast(getString(R.string.enter_partial_amount))
                                else if (binding.layoutPartialAmt.rbRelease.isChecked && partialBlockingTimeHours == null)
                                    toast(getString(R.string.select_hour))
                                else if (binding.layoutPartialAmt.rbRelease.isChecked && partialBlockingTimeMins == null)
                                    toast(getString(R.string.select_minute))
                                else
                                    DialogUtils.blockSeatsDialog(
                                        showMsg = false,
                                        this,
                                        getString(R.string.confirmBooking),
                                        getString(R.string.selected_seat_s_will_be_assigned),
                                        srcDest = srcDest ?: getString(R.string.dash),
                                        journeyDate = toolbarSubTitleInfo
                                            ?: getString(R.string.dash),
                                        noOfSeats = noOfSeats!!,
                                        seatNo = selectedSeatNo.toString(),
                                        getString(R.string.goBack),
                                        getString(R.string.confirmBooking),
                                        this
                                    )
                            } else {

                                DialogUtils.blockSeatsDialog(
                                    showMsg = false,
                                    this,
                                    getString(R.string.confirmBooking),
                                    getString(R.string.selected_seat_s_will_be_assigned),
                                    srcDest = srcDest ?: getString(R.string.dash),
                                    journeyDate = toolbarSubTitleInfo ?: getString(R.string.dash),
                                    noOfSeats = "$noOfSeatsTotal",
                                    seatNo = "$selectedSeatNo",
                                    getString(R.string.goBack),
                                    getString(R.string.confirmBooking),
                                    this
                                )
                            }
                        } else {

                            /* DialogUtils.twoButtonDialogUpdate(
                                 this,
                                 "${getString(R.string.confirmPhoneBooking)}?",
                                 getString(R.string.confirm_this_phone_booking),
                                 resources.getColor(R.color.colorBlackShadow),
                                 getString(R.string.no_dont_confirm),
                                 getString(R.string.yes_continue),
                                 this
                             )*/

                            isPhoneBlocking = true
                            if (isNetworkAvailable()) {
                                pinAuthDialog(false)
                            } else {
                                noNetworkToast()
                            }
                        }

                    }
                }
            }

            R.id.tvReleaseDate -> {
                val minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(minDate)
                    .setMaxDate(getDateDMY(travelDate)?.let { stringToDate(it, DATE_FORMAT_D_M_Y) })
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show(supportFragmentManager, tag)
            }
        }
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        Timber.d("$tag radioTag ${view.tag}")
        if (paymentOptionsList.isNotEmpty()) {
            paymentType = paymentOptionsList[position].id.toString()
            if (paymentOptionsList[position].name == getString(R.string.credit_debit)) {
                DialogUtils.creditDebitDialog(this, this)
            } else if (paymentOptionsList[position].name == getString(R.string.others)) {
                otherPaymentOptions.clear()
                /*val payGayType = PayGayType()
                payGayType.payGayTypeName = getString(R.string.notAvailable)
                otherPaymentOptions.add(payGayType)*/

                if (::privilegeResponseModel.isInitialized && !privilegeResponseModel.othersPaymentOption.isNullOrEmpty()) {
                    privilegeResponseModel.othersPaymentOption.forEach {
                        val payGayType = PayGayType()
                        payGayType.payGayTypeName = it.label
                        payGayType.payGayTypeId = it.id.toString()
                        otherPaymentOptions.add(payGayType)
                    }
                }
                if (otherPaymentOptions.isNotEmpty())
                    DialogUtils.otherPaymentsDialog(this, otherPaymentOptions, this)
            } else if (paymentOptionsList[position].name == getString(R.string.wallet_upi)) {
                var passengerMobile = getString(R.string.empty)
                if (mobileNumber != null)
                    passengerMobile = mobileNumber?.substringAfter("-").toString()

                if (privilegeResponseModel.allowToShowUpiPaymentOptionInBookingPage != null
                    && privilegeResponseModel.allowToShowUpiPaymentOptionInBookingPage
                ) {
                    if (!walletPaymentOptions.contains(WalletPaymentOption("UPI", 2, ""))) {
                        walletPaymentOptions.add(0, WalletPaymentOption("UPI", 2, ""))
                    }
                }
                /* //Pinelab integration
                 if (!walletPaymentOptions.contains(WalletPaymentOption("PineLab", 3, ""))) {
                     walletPaymentOptions.add(0,WalletPaymentOption("PineLab",3,""))
                 }*/

                walletUpiAlertDialog = DialogUtils.walletUpiDialog(
                    this,
                    walletPaymentOptions,
                    this,
                    mobile = passengerMobile
                )
            } else if(paymentOptionsList[position].name == "Phonepe UPI") {

                DialogUtils.dialogPhonePePaymentOptions(
                    this,
                    privileges?.phonePeDirectUPIOptions,
                    onVerifyButtonClick = { vpa, llProgressBar, btnConfirm, btnVerifyUPI ->
                        linearLayoutProgressBar = llProgressBar
                        buttonConfirm = btnConfirm
                        buttonVerify = btnVerifyUPI

                        phonePeViewModel.callPhonePeDirectValidateUpiId(
                            loginModelPref.api_key,
                            vpa,
                            validate_ts_app_vpa_phonepe
                        )

                    },
                    onConfirmButtonClick = { phonePeDialogUpiType, phonePeDialogUserNumber, phonePeDialogVpa ->
                        isPhonePePayment = true
                        phonePeUpiType = phonePeDialogUpiType
                        phonePeUserNumber = phonePeDialogUserNumber
                        phonePeVpa = phonePeDialogVpa
                        pinAuthDialog(true)
                    },
                    onCancelButtonClick = {
                        isPhonePePayment = false
                        lastSelectedPaymentPosition = 0
                        setPaymentOptionsAdapter()
                    }
                )
            } else if (paymentOptionsList[position].name == this.getString(R.string.pinelab_QR)) {
                // pinelabPaymentType(PAYMENT_QR)
                paymentType = paymentOptionsList[position].id.toString()

            } else if (paymentOptionsList[position].name == this.getString(R.string.pinelab_debitcard)) {
                paymentType = paymentOptionsList[position].id.toString()
                //pinelabPaymentType(PAYMENT_DEBIT_CREDIT)
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    override fun onLeftButtonClick() {
    }

    override fun onRightButtonClick() {
        //isCreditDebitPaymentSelected = false
        isPhoneBlocking = false
        DialogUtils.showProgressDialog(this)
        if (isPhoneBlockTicket) {
            if (isNetworkAvailable()) {
                confirmPhoneBlockTicketApi()
            } else {
                noNetworkToast()
            }
        } else {
            if (selectedSeatDetails.isNotEmpty() && selectedExtraSeatDetails.isNotEmpty()) {
                Timber.d("checkApiCall- bookSeatsWithExtraSeatApi")
                if (isNetworkAvailable()) {
                    bookSeatsWithExtraSeatApi()
                } else
                    noNetworkToast()
            } else if (selectedSeatDetails.any { it.isExtraSeat }) {
                Timber.d("checkApiCall- bookExtraSeatApi")
                isExtraSeat = true
                if (isNetworkAvailable()) {
                    if (shouldExtraSeatBooking && currentCountry.equals("india", true)) {
                        DialogUtils.showFullHeightPinInputBottomSheet(
                            activity = this@BookingPaymentOptionsActivity,
                            fragmentManager = supportFragmentManager,
                            pinSize = pinSize,
                            getString(R.string.extra_seat_booking),
                            onPinSubmitted = { pin: String ->
                                bookExtraSeatApi(pin)
                            },
                            onDismiss = null
                        )
                    } else {
                        bookExtraSeatApi("")
                    }
                } else
                    noNetworkToast()
            } else {
                Timber.d("checkApiCall- bookTicketApi")
                if (isNetworkAvailable()) {
                    pinAuthDialog(false)
                } else
                    noNetworkToast()
            }
        }

    }

    private fun bookExtraSeatApi(authPin: String) {
        val seatList =
            mutableListOf<com.bitla.ts.domain.pojo.book_extra_seat.request.SeatDetail>()

        passengerList.forEach {
            val seatDetail = com.bitla.ts.domain.pojo.book_extra_seat.request.SeatDetail()
            seatDetail.isPrimary = it.isPrimary.toString()
            seatDetail.seatNumber = it.seatNumber ?: getString(R.string.empty)
            seatDetail.sex = it.sex ?: getString(R.string.empty)
            seatDetail.name = it.name ?: getString(R.string.empty)
            seatDetail.age = it.age ?: getString(R.string.empty)
            seatDetail.firstName = it.name ?: getString(R.string.empty)
            seatDetail.lastName = it.name ?: getString(R.string.empty)
            seatDetail.idCardType = it.idCardType.toString()
            seatDetail.idCardNumber = it.idCardNumber
            /*seatDetail.passportIssuedDate = it.passportIssuedDate
            seatDetail.passportExpiryDate = it.passportExpiryDate
            seatDetail.placeOfIssue = it.placeOfIssue*/
            seatDetail.nationality = it.nationality
//            if (it.editFare != null && it.editFare.toString().isNotEmpty())
//                seatDetail.fare = it.editFare.toString()
//            else
//                seatDetail.fare = it.fare.toString()
            seatDetail.fare = it.fare.toString()
            seatList.add(seatDetail)
        }


        val bookingDetails = com.bitla.ts.domain.pojo.book_extra_seat.request.BookingDetails()
        bookingDetails.agentType = agentType!!


        if (agentType == "1") {
            // bookingDetails.discountOnTotalAmount = discountOnTotalAmount
            bookingDetails.onBehalfOnlineAgentValue =
                bookingCustomRequest.online_agent_on_behalf.toString()
        }
        // offline
        if (agentType == "2") {
            //bookingDetails.discountOnTotalAmount = discountOnTotalAmount
            bookingDetails.onBehalf = bookingCustomRequest.offline_agent_on_behalf.toString()
            bookingDetails.onBehalfPaid = bookingCustomRequest.amt_paid_offline.toString()
        }
        // branch
        if (agentType == "3") {
            // bookingDetails.discountOnTotalAmount = discountOnTotalAmount
            bookingDetails.onBehalfBranch = bookingCustomRequest.branch_id.toString()
            bookingDetails.onBehalfUser = bookingCustomRequest.branch_user.toString()
        }

        if (IS_PINELAB_DEVICE) {
            if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                if(paymentType == "6" || paymentType == "7"){
                    bookingDetails.isPinelabPayment = true
                    bookingDetails.pinelabPaymentType = paymentType.toInt()
                }

            }

        }

        val contactDetail = com.bitla.ts.domain.pojo.book_extra_seat.request.ContactDetail()
        contactDetail.email = email
        contactDetail.emergencyName = emergencyName
        contactDetail.alternateNumber = alternateMobileNumber
        contactDetail.mobileNumber = mobileNumber

        val reqBody = com.bitla.ts.domain.pojo.book_extra_seat.request.ReqBody()
        reqBody.apiKey = loginModelPref.api_key
        reqBody.boardingAt = boardingId.toString()
        reqBody.bookingDetails = bookingDetails
        reqBody.contactDetail = contactDetail
        reqBody.destinationId = destinationId
        reqBody.dropOff = droppingId.toString()
        reqBody.noOfSeats = noOfSeats!!
        reqBody.originId = sourceId
        reqBody.reservationId = resId.toString()
        reqBody.seatDetails = seatList
        reqBody.locale = locale
        reqBody.authPin = authPin

        val bookExtraSeatRequest = BookExtraSeatRequest()
        bookExtraSeatRequest.bccId = bccId.toString()
        bookExtraSeatRequest.format = format_type
        bookExtraSeatRequest.jsonFormat = json_format
        bookExtraSeatRequest.methodName = book_extra_seat_method_name
        bookExtraSeatRequest.reqBody = reqBody

        Timber.d("bookExtraSeatRequest ${Gson().toJson(bookExtraSeatRequest)}")

        /* bookingOptionViewModel.bookExtraSeatApi(
             authorization = loginModelPref.auth_token,
             apiKey = loginModelPref.api_key,
             bookExtraSeatRequest = bookExtraSeatRequest,
             apiType = book_extra_seat_method_name
         )*/

        bookingOptionViewModel.bookExtraSeatApi(
            bookExtraSeatRequest = reqBody,
            apiType = book_extra_seat_method_name
        )

    }

    private fun bookSeatsWithExtraSeatApi() {
        val seatList =
            mutableListOf<com.bitla.ts.domain.pojo.book_with_extra_seat.request.SeatDetail>()
        val extraSeatList =
            mutableListOf<SeatDetailExtra>()

        passengerList.forEach {
            if (!it.isExtraSeat) {
                val seatDetail = com.bitla.ts.domain.pojo.book_with_extra_seat.request.SeatDetail(
                    isPrimary = true,
                    seatNumber = it.seatNumber ?: getString(R.string.empty),
                    sex = it.sex ?: getString(R.string.empty),
                    name = it.name ?: getString(R.string.empty),
                    age = it.age ?: getString(R.string.empty),
                    firstName = it.name ?: getString(R.string.empty),
                    lastName = it.name ?: getString(R.string.empty),
                    idCardType = it.idCardType?.toInt()?:0,
                    idCardNumber = it.idCardNumber.toString(),
                    nationality = it.nationality.toString(),
                    additionalFare = "",
                    discountAmount = "",
                    isRoundTripSeat = false,
                    passengerCategory = ""

                )

//                if (it.editFare != null && it.editFare.toString().isNotEmpty()) {
//                    seatDetail.fare = it.editFare.toString()
//                } else {
//                    seatDetail.fare = it.fare.toString()
//                }
                seatDetail.fare = it.fare.toString()

                seatList.add(seatDetail)
            }

            if (it.isExtraSeat) {
//                selectedExtraSeatDetails.forEach { }

                val seatDetailExtra =
                    SeatDetailExtra(
                        isPrimary = "false",
                        seatNumber = it.seatNumber ?: getString(R.string.empty),
                        sex = it.sex ?: getString(R.string.empty),
                        name = it.name ?: getString(R.string.empty),
                        age = it.age ?: getString(R.string.empty),
                        mobileNumber = mobileNumber ?: getString(R.string.empty),
                        alternateNumber = alternateMobileNumber ?: getString(R.string.empty),
                        idCardType = it.idCardType?.toInt()?:0,
                        idCardNumber = it.idCardNumber.toString(),
                        nationality = it.nationality.toString(),
                    )

//                    if (it.editFare != null && it.editFare.toString().isNotEmpty()) {
//                        seatDetailExtra.fare = it.editFare.toString()
//                    } else {
//                        seatDetailExtra.fare = it.fare.toString()
//                    }

                seatDetailExtra.fare = it.fare.toString()
                extraSeatList.add(seatDetailExtra)
            }
        }

        val bookingDetails = com.bitla.ts.domain.pojo.book_with_extra_seat.request.BookingDetails(
            agentType = "$agentType",
            remarks = binding.etRemarks.text.toString()
        )

        bookingDetails.agentType = agentType.toString()


        val contactDetail = com.bitla.ts.domain.pojo.book_with_extra_seat.request.ContactDetail(
            email = email,
            emergencyName = emergencyName,
            alternateNumber = alternateMobileNumber,
            mobileNumber = mobileNumber,
            sendSmsOnBooking = true
        )

        val extraSeatDetail = ExtraSeats(
            exNoOfSeats?.toInt(),
            extraSeatList
        )

        val bookExtraSeatRequest = BookTicketWithExtraSeatRequest(
            apiKey = loginModelPref.api_key,
            reservationId = resId.toString(),
            boardingAt = boardingId.toString(),
            destinationId = destinationId,
            dropOff = droppingId.toString(),
            noOfSeats = noOfSeats.toString(),
            originId = sourceId,
            locale = locale,
            jsonFormat = json_format,
            format = format_type,

            operator_api_key = operator_api_key,
            is_from_bus_opt_app = "true",

            bookingDetails = bookingDetails,
            contactDetail = contactDetail,
            seatDetails = seatList,
            extraSeats = extraSeatDetail,
            couponDetails = null,
            packageDetailsId = null
        )
//        bookExtraSeatRequest.bccId = bccId.toString()
//        bookExtraSeatRequest.format = format_type
//        bookExtraSeatRequest.jsonFormat = json_format
//        bookExtraSeatRequest.methodName = book_ticket_method_name

        Timber.d("bookSeatWithExtraSeatRequest ${Gson().toJson(bookExtraSeatRequest)}")

        bookingOptionViewModel.bookSeatWithExtraSeatApi(
            bookExtraSeatRequest = bookExtraSeatRequest,
            apiType = book_ticket_method_name
        )
    }

    private fun fareBreakupApi() {
        val seatNumberArray = mutableListOf<String>()
        passengerList.forEach {
            seatNumberArray.add(it.seatNumber.toString())
        }
//        seatNumberArray.add("L11")
//        seatNumberArray.add("L10")


//        Timber.d("SeatArry $seatNumberArray")
//        Timber.d("SeatArry--- ${seatNumberArray[0]}")
//        Timber.d("SeatArry---size ${seatNumberArray.size}")

        val returnSeatNumberArray = mutableListOf<String>()
        val couponDetails = mutableListOf<String>()

        val reqBody = com.bitla.ts.domain.pojo.fare_breakup.request.ReqBody()


        //val bookingDetails = BookingDetails()

        // Coupon Code

        val couponCodeIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.coupon_code) }
        if (appliedCouponList.size > 0) {
            if (couponCodeIndex != -1) {
                val couponCode = appliedCouponList[couponCodeIndex].coupon_code
//            Timber.d("couponCode $couponCode")
                reqBody.couponCode = couponCode
            }
        }
        if (!PreferenceUtils.getString("AutoDiscountCouponCode").isNullOrEmpty()) {
            reqBody.auto_discount_coupon = PreferenceUtils.getString("AutoDiscountCouponCode")
        }
        // Free Ticket
        val isFreeTicket =
            appliedCouponList.any { it.coupon_type == getString(R.string.free_ticket) }
        if (isFreeTicket){
            reqBody.isFreeBookingAllowed = isFreeBookingAllowed
            binding.layoutPaymentOptions.gone()
            paymentType = "1"
        }



        // Vip Booking
        val isVipTicket = appliedCouponList.any { it.coupon_type == getString(R.string.vip_ticket) }
        if (isVipTicket) {
            reqBody.isVipTicket = vipTicket

            if(privileges?.isVipAFreeBooking == true){
                binding.layoutPaymentOptions.gone()
                paymentType = "1"
            }


            /*val vipTicketIndex =
                appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.vip_ticket) }
            if (vipTicketIndex != -1) {
                val vipCategory = appliedCouponList[vipTicketIndex].coupon_code
                Timber.d("vipCategory $vipCategory")
                reqBody.vipBookingCategory = vipCategory
            }*/
        }

        /* val discountOnTotalIndex =
             appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.discount_amount) }
         if (discountOnTotalIndex != -1) {
             val amount = appliedCouponList[discountOnTotalIndex].coupon_code
             discountOnTotalAmount = amount
             Timber.d("amount $amount")
             reqBody.discountOnTotalAmount = amount
         }*/

        /*
         // Discount On Total Amount
         val discountOnTotalIndex =
             appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.discount_amount) }
         if (discountOnTotalIndex != -1) {
             val amount = appliedCouponList[discountOnTotalIndex].coupon_code
             discountOnTotalAmount = amount
             Timber.d("amount $amount")
             bookingDetails.discountOnTotalAmount = amount
         }*/

        // Discount On Total Amount
        val discountOnTotalIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.discount_amount) }
        if (discountOnTotalIndex != -1) {
            val amount = appliedCouponList[discountOnTotalIndex].coupon_code
            Timber.d("amount $amount")
            reqBody.totalDiscountAmount = amount
        }

        // Discount On individual seat
        if (individualDiscountAmount > 0) {
            reqBody.discountAmount = individualDiscountAmount.toString()
        }

        // Smart miles
        val smartMilesIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.apply_smart_miles) }
        if (smartMilesIndex != -1) {
            useSmartMiles = "true"
            val smartMilesNumber = appliedCouponList[smartMilesIndex].coupon_code
            Timber.d("smartMilesNumber $smartMilesNumber")
            val smartMilesHash = SmartMilesHash()
            smartMilesHash.phoneNumber = smartMilesNumber
            reqBody.smartMilesHash = smartMilesHash
        }

        // Privilege Card
        val privilegeCardIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.privilege_card) }

        if (privilegeCardIndex != -1) {
            val cardOrMobileNumber = appliedCouponList[privilegeCardIndex].coupon_code
            Timber.d("cardOrMobileNumber $cardOrMobileNumber")

            var privilegeMobileNumber = ""
            var cardNumber = ""

            if (isNumeric(cardOrMobileNumber)) {
                privilegeMobileNumber = cardOrMobileNumber.trim()
                cardNumber = getString(R.string.empty)
            } else {
                cardNumber = cardOrMobileNumber.trim()
                privilegeMobileNumber = getString(R.string.empty)
            }

            val privilegeCardHash = PrivilegeCardHash()
            privilegeCardHash.cardNumber = cardNumber
            privilegeCardHash.mobileNumber = privilegeMobileNumber
            privilegeCardHash.reservationId = resId.toString()
            privilegeCardHash.selectedSeats = noOfSeatsTotal

            reqBody.privilegeCardHash = privilegeCardHash

//            req.privilegeCardHash = privilegeCardHash
//            reqBody.privCardNumber = cardOrMobileNumber
        }

        // Pre-postpone
        val prePostponeIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.pre_postpone_ticket) }
        if (prePostponeIndex != -1) {
            val prePostponeNumber = appliedCouponList[prePostponeIndex].coupon_code
            Timber.d("prePostponeNumber $prePostponeNumber")
            reqBody.prePostPonePnr = prePostponeNumber
            reqBody.isMatchPrepostponeAmount = isMatchPrepostponeAmount
            reqBody.allowPrePostPoneOtherBranch = allowPrePostPoneOtherBranch
            reqBody.corpCompanyId = corpCompanyId
        }

        // Previous Pnr
        val previousPnrIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.quote_previous_pnr) }
        if (previousPnrIndex != -1) {
            val previousPnrNumber =
                appliedCouponList[previousPnrIndex].coupon_code.substringBefore("-")
            Timber.d("previousPnrNumber $previousPnrNumber")
            val previousPnrDetails = PreviousPnrDetails()
            previousPnrDetails.previous_pnr = previousPnrNumber
            previousPnrDetails.phone_number =
                appliedCouponList[previousPnrIndex].coupon_code.substringAfter("-")
            reqBody.previousPnrDetails = previousPnrDetails
        }

        //  reqBody.bookingDetails = bookingDetails

        if (selectedSeatDetails.any { it.additionalFare != null && it.additionalFare!! > 0.0 }) {
            val additionalFares = mutableListOf<AdditionalFare>()
            selectedSeatDetails.forEach {
                val additionalFare = AdditionalFare()
                additionalFare.seatNo = it.number
                additionalFare.fare = it.additionalFare.toString()
                additionalFares.add(additionalFare)
            }
            reqBody.additionalFare = additionalFares
        }

        if (selectedSeatDetails.any {
                !it.isExtraSeat
                        && it.fare != null
                        && it.fare.toString().toDouble() > 0.0
            }) {
            val editFares = mutableListOf<EditFare>()

            passengerList.forEach {
                val editFare = EditFare()
                editFare.seatNo = it.seatNumber ?: getString(R.string.empty)

                if (!it.isExtraSeat) {
                    if (it.fare != null) {
                        editFare.fare = it.fare.toString()
                    } else {
                        editFare.fare = ""
                    }
                    editFares.add(editFare)
                }

            }
            reqBody.editFare = editFares
        }

        // extra seat fare
        if (selectedSeatDetails.any { it.isExtraSeat }) {
            isExtraSeat = true
            reqBody.isExtraSeat = true

            val extraSeatFares = mutableListOf<ExtraSeatFare>()

            passengerList.forEach {
                val extraSeatFare = ExtraSeatFare()
                extraSeatFare.seatNo = it.seatNumber ?: getString(R.string.empty)

                if (it.isExtraSeat) {
                    if (it.fare != null && it.fare.toString().isNotEmpty())
                        extraSeatFare.fare = it.fare.toString()
                    else
                        extraSeatFare.fare = it.fare.toString()
                    extraSeatFares.add(extraSeatFare)
                }
            }
            reqBody.extraSeatFare = extraSeatFares
        }

        // individual discount
        if (!PreferenceUtils.getSelectedCoupon().isNullOrEmpty()) {
            reqBody.seatWiseFare = PreferenceUtils.getSelectedCoupon()
        } else {
            if (selectedSeatDetails.any { it.discountAmount != null && it.discountAmount!! > 0.0 }) {
                val seatWiseFares = mutableListOf<SeatWiseFare>()
                selectedSeatDetails.forEach {
                    val seatWiseFare = SeatWiseFare()
                    seatWiseFare.seatNo = it.number
                    seatWiseFare.discount = it.discountAmount.toString()
                    seatWiseFares.add(seatWiseFare)
                }
                reqBody.seatWiseFare = seatWiseFares
            }
        }

        reqBody.apiKey = loginModelPref.api_key
        reqBody.resId = resId.toString()
        reqBody.origin = sourceId
        reqBody.destination = destinationId
        reqBody.boardingAt = boardingId.toString()
        reqBody.dropOff = droppingId.toString()
        reqBody.noOfSeats = noOfSeatsTotal?.toInt()
        reqBody.isMiddleTier = is_middle_tier
        reqBody.isRoundTrip = isRoundTrip
        reqBody.isBima = "$isBima"
        reqBody.seatNumbers = seatNumberArray
        reqBody.returnSeatNumbers = returnSeatNumberArray
        reqBody.passengerTitles = PassengerTitles()
        reqBody.returnBoardingAt = returnBoardingPoint
        reqBody.returnDropoff = returnDroppingPoint
        reqBody.offerCoupon = offerCoupon
        reqBody.promoCoupon = promoCoupon
        reqBody.useSmartMiles = useSmartMiles
        reqBody.privCardNumber = privilegeCardNo
        reqBody.couponDetails = couponDetails
        reqBody.paymentType = paymentType
        reqBody.agentType = agentType
        reqBody.locale = locale


        if (isExtraSeat)
            reqBody.isInsuranceEnabled = false
        else {
            if (isInsuranceChecked != null && isInsuranceChecked!!)
                reqBody.isInsuranceEnabled = isInsuranceChecked!!
        }

        val fareBreakupRequest = FareBreakupRequest()
        fareBreakupRequest.bccId = bccId.toString()
        fareBreakupRequest.format = format_type
        fareBreakupRequest.methodName = fare_breakup_method_name
        fareBreakupRequest.reqBody = reqBody

        /*bookingOptionViewModel.fareBreakupApi(
            authorization = loginModelPref.auth_token,
            apiKey = loginModelPref.api_key,
            fareBreakupRequest = fareBreakupRequest,
            apiType = fare_breakup_method_name
        )*/

        bookingOptionViewModel.fareBreakupApi(
            reqBody,
            apiType = fare_breakup_method_name
        )

        Timber.d("fareBreakupRequest ${Gson().toJson(fareBreakupRequest)}")
    }

    private fun walletOtpGenerationApi() {
        val reqBody = com.bitla.ts.domain.pojo.wallet_otp_generation.request.ReqBody(
            amount = bookTicketTotalFare,
            api_key = loginModelPref.api_key,
            is_from_middle_tier = is_from_middle_tier.toBoolean(),
            pnr_number = bookTicketPnr,
            wallet_mobile = walletMobileNo,
            wallet_type = selectedWalletUpiOptionId.toString(),
            locale = locale,
            is_resend_otp = false
        )

        val walletOtpGenerationRequest = WalletOtpGenerationRequest(
            bcc_id = bccId.toString(), format_type, wallet_otp_generation_method_name, reqBody
        )
        bookingOptionViewModel.walletOtpGenerationApi(
            reqBody,
            apiType = wallet_otp_generation_method_name
        )
    }

    private fun validateWalletOtpApi(otp: String) {
        val reqBody = com.bitla.ts.domain.pojo.validate_otp_wallets.request.ReqBody(
            amount = bookTicketTotalFare,
            api_key = loginModelPref.api_key,
            is_from_middle_tier = is_from_middle_tier.toBoolean(),
            otp_number = otp,
            phone_blocked = isPhoneBlockedWallet,
            pnr_number = bookTicketPnr,
            wallet_mobile = walletMobileNo,
            wallet_type = selectedWalletUpiOptionId.toString(),
            locale = locale
        )

        val validateOtpWalletsRequest = ValidateOtpWalletsRequest(
            bcc_id = bccId.toString(), format_type, validate_otp_wallets_method_name, reqBody
        )
        bookingOptionViewModel.validateWalletOtpApi(
            reqBody,
            apiType = validate_otp_wallets_method_name
        )
    }

    private fun callUPICreateQrCodeApi() {
        val reqBody = com.bitla.ts.domain.pojo.upi_create_qr.request.ReqBody(
            amount = bookTicketTotalFare,
            apiKey = loginModelPref.api_key,
            isFromMiddleTier = is_from_middle_tier.toBoolean(),
            pnrNumber = bookTicketPnr,
            userNumber = walletMobileNo,
            upiType = 2

        )

        val upiCreateQRCodeRequest = UPICreateQRCodeRequest(
            bccId = bccId.toString(),
            format_type,
            upi_create_qr_code,
            reqBody
        )
        bookingOptionViewModel.upiCreateQrCodeApi(
            reqBody,
            apiType = upi_create_qr_code
        )
    }

    private fun callUPICheckStatusApi() {
        val reqBody = com.bitla.ts.domain.pojo.upi_check_status.request.ReqBody(
            apiKey = loginModelPref.api_key,
            isFromMiddleTier = is_from_middle_tier.toBoolean(),
            pnrNumber = bookTicketPnr,
            isSendSms = true
        )

        val upiTranxStatusRequest = UpiTranxStatusRequest(
            bccId = bccId.toString(),
            format_type,
            upi_tranx_status,
            reqBody
        )
        bookingOptionViewModel.upiTranxStatusApi(
            reqBody,
            apiType = upi_tranx_status
        )
    }

    private fun bookTicketApi(isUpi: Boolean, authPin: String?) {
        isUpiPayment = isUpi
        val seatList = mutableListOf<SeatDetail>()
        val packageDetailsId = PackageDetailsId()
        val couponDetails = mutableListOf<Any>()

        Timber.d("selectedSeatDetails ${Gson().toJson(selectedSeatDetails)}")

        selectedSeatDetails.forEach {
            val seatDetail = SeatDetail()
            val couponList = arrayListOf<String?>()
            val seatNoList = arrayListOf<String>()

            if (!PreferenceUtils.getSelectedCoupon().isNullOrEmpty()) {
                PreferenceUtils.getSelectedCoupon()!!.forEach {
                    couponList.add(it.auto_discount_coupon)
                    seatNoList.add(it.seatNo!!)
                }
            }
            for (i in 0..seatNoList.size.minus(1)) {
                if (it.number == seatNoList[i]) {
                    seatDetail.couponCode = couponList[i]
                }
            }

            seatDetail.isPrimary = it.isPrimary
            seatDetail.seatNumber = it.number
            seatDetail.sex = it.sex ?: getString(R.string.empty)
            if (isInsuranceChecked != null && isInsuranceChecked!!)
                seatDetail.name = "${it.firstName} ${it.lastName}"
            else
                seatDetail.name = it.name ?: getString(R.string.empty)
            seatDetail.age = it.age ?: getString(R.string.empty)
            seatDetail.additionalFare = it.additionalFare
            seatDetail.discountAmount = it.discountAmount ?: 0.0
            seatDetail.isRoundTripSeat = isRoundTripSeat
            seatDetail.passengerCategory = getString(R.string.empty)
            seatDetail.firstName = it.firstName ?: getString(R.string.empty)
            seatDetail.lastName = it.lastName ?: getString(R.string.empty)
            seatDetail.idCardType = it.idCardType
            seatDetail.idCardNumber = it.idCardNumber
            seatDetail.passportIssuedDate = it.passportIssuedDate
            seatDetail.passportExpiryDate = it.passportExpiryDate
            seatDetail.placeOfIssue = it.placeOfIssue
            seatDetail.nationality = it.nationality

            if (it.mealRequired) {
                seatDetail.mealRequired = it.mealRequired
                seatDetail.selectedMealType = it.selectedMealType
            }
            //  seatDetail.fare = "0" // fare will be zero as per discussed Anand
            if (it.editFare != null && it.editFare.toString().isNotEmpty())
                seatDetail.fare = it.editFare
            else
                seatDetail.fare = it.baseFareFilter


            seatList.add(seatDetail)
        }

        val bookTicketFullRequest = BookTicketFullRequest()
        bookTicketFullRequest.bccId = bccId.toString()
        bookTicketFullRequest.format = format_type
        bookTicketFullRequest.methodName = book_ticket_method_name

        val bookingDetails = BookingDetails()
        bookingDetails.isBimaTicket = isBima
        bookingDetails.agentType = agentType
        bookingDetails.remarks = binding.etRemarks.text.toString()

        if (IS_PINELAB_DEVICE) {
            if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                if(paymentType == "6" || paymentType == "7"){
                    bookingDetails.isPinelabPayment = true
                    bookingDetails.pinelabPaymentType = paymentType.toInt()
                }
            }

        }

        /*bookingDetails.discountAmount = discountAmount
        bookingDetails.discountOnTotalAmount = discountOnTotalAmount
        bookingDetails.isFreeBookingAllowed = isFreeBookingAllowed
        bookingDetails.isVipTicket = isVipTicket*/
        //bookingDetails.ref_booking_number = refBookingNo!!,
        //bookingDetails.on_behalf_online_agent_value = onBehalfOnlineAgent!!,
        //bookingDetails.on_behalf = onBehalf!!,
        //onbehalf_paid = onBehalfPaid,
        //on_behalf_branch = onBehalfBranch!!,
        //on_behalf_user = onBeHalfUser.toString()

        // online
        if (agentType == "1") {
            // bookingDetails.discountOnTotalAmount = discountOnTotalAmount
            bookingDetails.onBehalfOnlineAgentValue = onBehalfOnlineAgent
            bookingDetails.refBookingNumber = refBookingNo

        }
        // offline
        if (agentType == "2") {
            //bookingDetails.discountOnTotalAmount = discountOnTotalAmount
            bookingDetails.onBehalf = onBehalf
            bookingDetails.onBehalfPaid = onBehalfPaid
            bookingDetails.refBookingNumber = refBookingNo
        }
        // branch
        if (agentType == "3") {
            // bookingDetails.discountOnTotalAmount = discountOnTotalAmount
            bookingDetails.onBehalfBranch = onBehalfBranch
            bookingDetails.onBehalfUser = onBeHalfUser.toString()
        }
        val phoneBlockConstraints = PreferenceUtils.getString("phoneBlock")
        val singleDemo = phoneBlockConstraints!!.split("#")

        if (!phoneBlock.isNullOrEmpty()) {
            if (phoneBlock.lowercase().contains("true")) {
                blockingDate = singleDemo[0]
                blockingTimeHours = singleDemo[1]
                blockingTimeMins = singleDemo[2]
                isPermanentPhoneBookingChecked = singleDemo[4].toBoolean()
                blockingAmPm = singleDemo[3]


                if (isPermanentPhoneBooking && isPermanentPhoneBookingChecked)
                    bookingDetails.permanentBlockedFlag = "1"
                bookingDetails.blockedFlag = "1"
                bookingDetails.blockingDate = singleDemo[0]
                bookingDetails.blockingTimeHours = singleDemo[1]
                bookingDetails.blockingTimeMins = singleDemo[2]
                bookingDetails.blockingAmPm = singleDemo[3]

// Discount On individual seat
                if (individualDiscountAmount > 0) {
                    bookingDetails.discountAmount = individualDiscountAmount.toString()
                } else {
                    val discountOnTotalIndex =
                        appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.discount_amount) }
                    if (discountOnTotalIndex != -1) {
                        bookingDetails.discountOnTotalAmount = discountOnTotalAmount
                    }
                }
            }
        }

//        isPhoneBlocking= true
        if (phoneBlock.lowercase().contains("true")) {
            val blockedDateFormatted = inputFormatToOutput(
                blockingDate,
                DATE_FORMAT_D_M_Y, DATE_FORMAT_D_M_Y_SLASH
            )
//            bookingDetails.discountOnTotalAmount = discountOnTotalAmount.toString()
            if (isPermanentPhoneBooking && isPermanentPhoneBookingChecked)
                bookingDetails.permanentBlockedFlag = blockedFlag.toString()
            bookingDetails.blockedFlag = blockedFlag.toString()
            bookingDetails.blockingDate = blockedDateFormatted
            bookingDetails.blockingTimeHours = blockingTimeHours
            bookingDetails.blockingTimeMins = blockingTimeMins
            bookingDetails.blockingAmPm = blockingAmPm

            // Discount On individual seat
            if (individualDiscountAmount > 0) {
                bookingDetails.discountAmount = individualDiscountAmount.toString()
            } else {
                // Discount On Total Amount
                val discountOnTotalIndex =
                    appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.discount_amount) }
                if (discountOnTotalIndex != -1) {
                    bookingDetails.discountOnTotalAmount = discountOnTotalAmount
                }
            }
        }
        if (paymentType != "4") {
            bookingDetails.paymentType = paymentType

        }

        // if payment option is "credit/debit card"
        if (creditDebitCardNo != null) {
            bookingDetails.creditTransactionNumber = creditDebitCardNo
        }

        // if payment option is "Others"
        if (selectedOtherPaymentOption != null) {
            bookingDetails.paymentTypeConfig = selectedOtherPaymentOption
        }

        // if payment option is "Wallet/UPI"
        if (paymentType == "4") {
            if (isUpi) {
                val upiPaymentHash = UpiPaymentHash(
                    true
                )
                bookingDetails.upiPaymentHash = upiPaymentHash

            } else {
                val selectedWallet = selectedWalletUpiOptionId?.toString() ?: "0"
                val walletPaymentHash = WalletPaymentHash(
                    allow_wallet_booking = allowWalletBooking,
                    selected_wallet = selectedWallet,
                    wallet_mobile_number = walletMobileNo
                )
                bookingDetails.walletPaymentHash = walletPaymentHash
            }
        }


        if(paymentType == "5") { // If phonepe upi is selected
            val upiDirectPaymentHash = UpiDirectPaymentHash(
                allow_upi_booking = true,
                upi_payment_type = phonePeUpiType
            )
            bookingDetails.paymentType = null
            bookingDetails.upiDirectPaymentHash = upiDirectPaymentHash
        }
        // Coupon Code
        val couponCodeIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.coupon_code) }
        if (couponCodeIndex != -1) {
            val couponCode = appliedCouponList[couponCodeIndex].coupon_code
            Timber.d("couponCode $couponCode")
            bookingDetails.couponCode = couponCode
        }

        // Free Ticket
        val isFreeTicket =
            appliedCouponList.any { it.coupon_type == getString(R.string.free_ticket) }
        if (isFreeTicket){
            bookingDetails.isFreeBookingAllowed = isFreeBookingAllowed
            binding.layoutPaymentOptions.gone()
            paymentType = "1"
        }


        // Vip Booking
        val isVipTicket = appliedCouponList.any { it.coupon_type == getString(R.string.vip_ticket) }
        if (isVipTicket) {
            bookingDetails.isVipTicket = vipTicket

            if(privileges?.isVipAFreeBooking == true){
                binding.layoutPaymentOptions.gone()
                paymentType = "1"
            }

            val vipTicketIndex =
                appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.vip_ticket) }
            if (vipTicketIndex != -1) {
                val vipCategory = appliedCouponList[vipTicketIndex].coupon_code.substringBefore(":")
                Timber.d("vipCategory $vipCategory")
                bookingDetails.vipBookingCategory = vipCategory
            }
        }

        // Discount On individual seat
        if (individualDiscountAmount > 0) {
            bookingDetails.discountAmount = individualDiscountAmount.toString()
        } else {
            // Discount On Total Amount
            val discountOnTotalIndex =
                appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.discount_amount) }
            if (discountOnTotalIndex != -1) {
                val amount = appliedCouponList[discountOnTotalIndex].coupon_code
                Timber.d("amount $amount")
                bookingDetails.discountOnTotalAmount = amount
            }
        }

        // Smart miles
        val smartMilesIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.apply_smart_miles) }
        if (smartMilesIndex != -1) {
            val smartMilesNumber = appliedCouponList[smartMilesIndex].coupon_code
            Timber.d("smartMilesNumber $smartMilesNumber")
            val smartMilesHash = SmartMilesHash()
            smartMilesHash.phoneNumber = smartMilesNumber
            bookingDetails.smartMilesHash = smartMilesHash
        }

        // Privilege Card
        val privilegeCardIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.privilege_card) }
        if (privilegeCardIndex != -1) {
            val cardOrMobileNumber = appliedCouponList[privilegeCardIndex].coupon_code
            Timber.d("cardOrMobileNumber $cardOrMobileNumber")
            var privilegeMobileNumber = ""
            var cardNumber = ""
            if (isNumeric(cardOrMobileNumber)) {
                privilegeMobileNumber = cardOrMobileNumber.trim()
                cardNumber = getString(R.string.empty)
            } else {
                cardNumber = cardOrMobileNumber.trim()
                privilegeMobileNumber = getString(R.string.empty)
            }

            val privilegeCardHash = PrivilegeCardHash()
            privilegeCardHash.cardNumber = cardNumber
            privilegeCardHash.mobileNumber = privilegeMobileNumber
            privilegeCardHash.reservationId = resId.toString()
            privilegeCardHash.selectedSeats = noOfSeatsTotal
            bookingDetails.privilegeCardHash = privilegeCardHash
        }

        // Pre-postpone
        val prePostponeIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.pre_postpone_ticket) }
        if (prePostponeIndex != -1) {
            val prePostponeNumber = appliedCouponList[prePostponeIndex].coupon_code
            Timber.d("prePostponeNumber $prePostponeNumber")
            bookingDetails.prePostPonePnr = prePostponeNumber
            bookingDetails.isMatchPrepostponeAmount = isMatchPrepostponeAmount
        }


        // Previous Pnr
        val previousPnrIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.quote_previous_pnr) }
        if (previousPnrIndex != -1) {
            val previousPnrNumber =
                appliedCouponList[previousPnrIndex].coupon_code.substringBefore("-")
            Timber.d("previousPnrNumber $previousPnrNumber")
            bookingDetails.previousPnrNumber = previousPnrNumber
            bookingDetails.phoneNumber =
                appliedCouponList[previousPnrIndex].coupon_code.substringAfter("-")
        }

        val contactDetail = ContactDetail()
        contactDetail.email = email
        contactDetail.emergencyName = emergencyName
        contactDetail.mobileNumber = mobileNumber
        contactDetail.alternateNumber = alternateMobileNumber
        contactDetail.sendSmsOnBooking = sendSmsOnBooking

        val reqBody = ReqBody()
        reqBody.isWhatsappUpdate= sendWhatsAppOnBooking
        reqBody.apiKey = loginModelPref.api_key
        reqBody.boardingAt = boardingId.toString()
        reqBody.bookingDetails = bookingDetails
        reqBody.contactDetail = contactDetail
        reqBody.couponDetails = couponDetails
        reqBody.destinationId = destinationId
        reqBody.dropOff = droppingId.toString()
        reqBody.locale = locale
        reqBody.noOfSeats = noOfSeatsTotal.toString()
        reqBody.operatorApiKey = operator_api_key
        reqBody.originId = sourceId
        reqBody.reservationId = resId.toString()
        reqBody.seatDetails = seatList
        reqBody.isFromBusOptApp = isFromBusOptApp
        reqBody.isFromMiddleTier = is_from_middle_tier
        reqBody.isRapidBooking = isRapidBooking
        reqBody.packageDetailsId = packageDetailsId
        reqBody.authPin = authPin
        if (!PreferenceUtils.getString("AutoDiscountCouponCode").isNullOrEmpty()) {
            reqBody.couponCode = PreferenceUtils.getString("AutoDiscountCouponCode")
        }

        val reqBodyWithInsurance = ReqBodyWithInsurance()
        if (isInsuranceChecked != null && isInsuranceChecked!!) {
            reqBodyWithInsurance.apiKey = loginModelPref.api_key
            reqBodyWithInsurance.boardingAt = boardingId.toString()
            reqBodyWithInsurance.bookingDetails = bookingDetails
            reqBodyWithInsurance.contactDetail = contactDetail
            reqBodyWithInsurance.couponDetails = couponDetails
            reqBodyWithInsurance.destinationId = destinationId
            reqBodyWithInsurance.dropOff = droppingId.toString()
            reqBodyWithInsurance.locale = locale
            reqBodyWithInsurance.noOfSeats = noOfSeatsTotal.toString()
            reqBodyWithInsurance.operatorApiKey = operator_api_key
            reqBodyWithInsurance.originId = sourceId
            reqBodyWithInsurance.reservationId = resId.toString()
            reqBodyWithInsurance.seatDetails = seatList
            reqBodyWithInsurance.isFromBusOptApp = isFromBusOptApp
            reqBodyWithInsurance.isFromMiddleTier = is_from_middle_tier
            reqBodyWithInsurance.isRapidBooking = isRapidBooking
            reqBodyWithInsurance.packageDetailsId = packageDetailsId
            if (!PreferenceUtils.getString("AutoDiscountCouponCode").isNullOrEmpty()) {
                reqBodyWithInsurance.couponCode =
                    PreferenceUtils.getString("AutoDiscountCouponCode")
            }

        }


        if (isPartialPayment) {
            val enteredPartialAmt = binding.layoutPartialAmt.edtPartialAmt.text.toString()
            if (enteredPartialAmt.isNotEmpty()) {
                partialAmount = enteredPartialAmt.toDouble()
                pendingAmount = totalFareString.toDouble().minus(partialAmount)
            }

            val partialPaymentDetails = PartialPaymentDetails()
            partialPaymentDetails.partialAmount = partialAmount
            partialPaymentDetails.pendingAmount = pendingAmount
            partialPaymentDetails.option = partialPaymentOption
            partialPaymentDetails.type = partialType
            partialPaymentDetails.blockingDate = partialBlockingDate
            partialPaymentDetails.timeHours = partialBlockingTimeHours
            partialPaymentDetails.timeMins = partialBlockingTimeMins
            reqBody.partialPaymentDetails = partialPaymentDetails

            if (isInsuranceChecked != null && isInsuranceChecked!!) {
                reqBodyWithInsurance.partialPaymentDetails = partialPaymentDetails
            }
        }

        if (isInsuranceChecked != null && isInsuranceChecked!!)
            reqBodyWithInsurance.isInsuranceEnabled = isInsuranceChecked!!


        //Timber.d("bookTicketFullRequest ${Gson().toJson(bookTicketFullRequest)}")

        if (isInsuranceChecked != null && isInsuranceChecked!!) {
            bookTicketFullRequest.reqBody = reqBodyWithInsurance
            bookingOptionViewModel.bookTicketWithInsurance(
                reqBodyWithInsurance,
                apiType = book_ticket_method_name
            )
        } else {
            bookTicketFullRequest.reqBody = reqBody
            bookingOptionViewModel.bookTicketFullApi(
                reqBody,
                apiType = book_ticket_method_name
            )
        }
    }

    private fun pinAuthDialog(isUpi: Boolean) {
        val isPaymentTypeExcluded = excludeTicketConfirmation.any { it.id.toString() == paymentType }
        if(phoneBlock.lowercase().contains("true")) {
            if(currentCountry.equals("india",true)) {
                if(shouldPhoneBlocking) {
                    DialogUtils.showFullHeightPinInputBottomSheet(
                        activity = this,
                        fragmentManager = supportFragmentManager,
                        pinSize,
                        getString(R.string.phone_block_book_ticket),
                        onPinSubmitted = { pin: String ->
                            bookTicketApi(isUpi, pin)
                        },
                        onDismiss = null
                    )
                } else {
                    bookTicketApi(isUpi, "")
                }
            } else {
                bookTicketApi(isUpi, null)
            }
        } else if(currentCountry.equals("india",true)) {
            if(shouldTicketConfirmation) {
                if(!isPaymentTypeExcluded) {
                    DialogUtils.showFullHeightPinInputBottomSheet(
                        activity = this,
                        fragmentManager = supportFragmentManager,
                        pinSize,
                        getString(R.string.book_ticket),
                        onPinSubmitted = { pin: String ->
                            bookTicketApi(isUpi, pin)
                        },
                        onDismiss = null
                    )
                }
                else {
                    bookTicketApi(isUpi, "")
                }
            } else {
                bookTicketApi(isUpi, "")
            }

        } else {
            bookTicketApi(isUpi, null)
        }
    }

    private fun confirmPhoneBlockTicketApi() {

        if (isNetworkAvailable()) {
            val reqBody = com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody(
                loginModelPref.api_key,
                paymentType.toInt(),
                pnrNumber,
                ticket,
                "",
                userId,
                locale = locale
            )
            val confirmPhoneBlockTicketReq = ConfirmPhoneBlockTicketReq(
                bccId.toString(),
                format_type,
                confirm_phone_block_ticket_method_name,
                reqBody
            )

            /* bookingOptionViewModel.confirmPhoneBlockTicketApi(
                 loginModelPref.auth_token,
                 loginModelPref.api_key,
                 confirmPhoneBlockTicketReq,
                 confirm_phone_block_ticket_method_name
             ) */

            bookingOptionViewModel.confirmPhoneBlockTicketApi(
                reqBody,
                confirm_phone_block_ticket_method_name
            )

        } else
            noNetworkToast()
    }

//    private fun callReleaseTicketApi() {
//        val releaseTicketRequest = ReleaseTicketRequest(
//            bccId.toString(),
//            format_type,
//            release_phone_block_ticket_method_name,
//            com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBody(
//                loginModelPref.api_key,
//                bookTicketPnr,
//                releaseTicketRemarks,
//                isFromDashboard,
//                json_format
//            )
//        )
//        dashboardViewModel.releaseTicketAPI(
//            loginModelPref.auth_token,
//            loginModelPref.api_key,
//            releaseTicketRequest,
//            release_phone_block_ticket_method_name
//        )
//    }

    private fun setConfirmPhoneBlockTicketObserver() {

        bookingOptionViewModel.confirmPhoneBlockTicket.observe(this) {
            if (it != null) {
                binding.includeProgress.progressBar.gone()
                Timber.d("bookTicketFullResponse $it")
                when (it.code) {
                    200 -> {
                        val intent = Intent(this, TicketDetailsActivity::class.java)
                        intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                        intent.putExtra("activityName2", "booking")

                        intent.putExtra(getString(R.string.TICKET_NUMBER), it.result?.ticketNumber)
                        intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                        startActivity(intent)
                        finish()
                    }

                    401 -> {
                        DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    }

                    else -> {
                        if (!it.message.isNullOrEmpty()) {
                            it.message.let { it1 -> toast(it1) }
                        }
                    }
                }
            } else {
                toast(getString(R.string.opps))
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onButtonClick(vararg args: Any) {
        if (args.isNotEmpty()) {
            val tag = args[0]
            when (tag) {
                getString(R.string.goBack) -> {
                    lastSelectedPaymentPosition = 0
                    setPaymentOptionsAdapter()
                }

                getString(R.string.credit_debit) -> {
                    creditDebitCardNo = args[1].toString()
                    DialogUtils.blockSeatsDialog(
                        showMsg = false,
                        this,
                        getString(R.string.confirmBooking),
                        getString(R.string.selected_seat_s_will_be_assigned),
                        srcDest = srcDest ?: getString(R.string.dash),
                        journeyDate = toolbarSubTitleInfo ?: getString(R.string.dash),
                        noOfSeats = noOfSeats!!,
                        seatNo = selectedSeatNo.toString(),
                        getString(R.string.goBack),
                        getString(R.string.confirmBooking),
                        this
                    )
                }
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str.isNotEmpty()) {
            if (str == getString(R.string.goBack)) {
                lastSelectedPaymentPosition = 0
                paymentType = "1"
                if (IS_PINELAB_DEVICE) {
                    if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                        paymentType = "6"
                    }
                }
                setPaymentOptionsAdapter()
                isCancelledClicked = true
            } else if (str == getString(R.string.timeout) || str == "go_back") {
                handler.removeCallbacks(runnable!!)
                isHandlerRunning = false

            }
            /*else if(str == "pinelab_selected"){
                if(isBound!!){
                    PreferenceUtils.putString("upiSelected", "PineLab")
                    pinelabPaymentType()
                }else{
                    toast(getString(R.string.pinelab_device_not_connected_please_try_again))
                }

            }*/
            else if (str == getString(R.string.wallet_go_back)) {
                isCancelledClicked = true
                lastSelectedPaymentPosition = 0
                paymentType = "1"
                if (IS_PINELAB_DEVICE) {
                    if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                        paymentType = "6"
                    }
                }
                setPaymentOptionsAdapter()

                selectedWalletUpiOptionId = null
                selectedWalletOrUpi = null
                selectedWalletUpiOptionName = null

                if (bookTicketPnr.isNotEmpty()) {
                    if (isNetworkAvailable())
//                        callReleaseTicketApi()
                    else
                        noNetworkToast()
                }

            } else if (str.contains(getString(R.string.other_payments_confirm))) {
                val otherPaymentOptionPosition = str.substringAfter("-")

                selectedOtherPaymentOption =
                    otherPaymentOptions[otherPaymentOptionPosition.toDouble()
                        .toInt()].payGayTypeName

                Timber.d("selectedOtherPaymentOption $selectedOtherPaymentOption")
            } else if (str.contains(WalletOptionAdapter.TAG)) {
                val walletUpiPosition = str.substringAfter("-")

                selectedWalletUpiOptionName =
                    walletPaymentOptions[walletUpiPosition.toDouble()
                        .toInt()].name

                selectedWalletUpiOptionId =
                    walletPaymentOptions[walletUpiPosition.toDouble()
                        .toInt()].paygayType

                selectedWalletOrUpi =
                    walletPaymentOptions[walletUpiPosition.toDouble()
                        .toInt()].type

                when (selectedWalletUpiOptionId) {
                    2 -> {
                        PreferenceUtils.putString("upiSelected", "UPI")
                        val groupRadio = walletUpiAlertDialog.findViewById<RadioGroup>(
                            R.id.upi_radio_group
                        )
//                        groupRadio.visible()
                        groupRadio.gone()
                        walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputLayout>(
                            R.id.layout_mobile_number
                        ).gone()

                        walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputEditText>(
                            R.id.et_mobile_number
                        ).gone()
                    }

                    3 -> {
                        PreferenceUtils.putString("upiSelected", "PineLab")
                    }

                    5 -> {
                        PreferenceUtils.putString("upiSelected", "wallet")
                        val groupRadio = walletUpiAlertDialog.findViewById<RadioGroup>(
                            R.id.upi_radio_group
                        )
                        groupRadio.gone()
                        walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputLayout>(
                            R.id.layout_mobile_number
                        ).visible()

                        walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputEditText>(
                            R.id.et_mobile_number
                        ).visible()
                    }

                    6 -> {
                        PreferenceUtils.putString("upiSelected", "wallet")
                        val groupRadio = walletUpiAlertDialog.findViewById<RadioGroup>(
                            R.id.upi_radio_group
                        )
                        groupRadio.gone()
                        walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputLayout>(
                            R.id.layout_mobile_number
                        ).visible()

                        walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputEditText>(
                            R.id.et_mobile_number
                        ).visible()
                    }

                    else -> {
                        PreferenceUtils.putString("upiSelected", "UPI")
                    }
                }

                Timber.d("selectedWalletUpiOptionId $selectedWalletUpiOptionId")
                Timber.d("selectedWalletUpiOptionId $selectedWalletUpiOptionName")
                Timber.d("selectedWalletUpiOptionId $selectedWalletOrUpi")

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val walletConfirmButton =
                        walletUpiAlertDialog.findViewById<Button>(
                            R.id.btnConfirm
                        )
                    walletConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))


                    walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputEditText>(
                        R.id.et_mobile_number
                    ).onChange {
                        if (it.isNotEmpty())
                            walletConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                        else
                            walletConfirmButton.setBackgroundColor(resources.getColor(R.color.button_default_color))

                    }
                }
            } else if (str.contains("UPI_Selected")) {

                val groupRadio = walletUpiAlertDialog.findViewById<RadioGroup>(
                    R.id.upi_radio_group
                )
//                groupRadio.visible()

                walletUpiAlertDialog.findViewById<RadioButton>(
                    R.id.upi_create_qr
                ).setOnClickListener {
                    walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputLayout>(
                        R.id.layout_mobile_number
                    ).gone()

                    walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputEditText>(
                        R.id.et_mobile_number
                    ).gone()
                }

                when (groupRadio.checkedRadioButtonId) {
//                    R.id.upi_send_sms -> {
//
//                    }

                    R.id.upi_create_qr -> {
                        walletUpiAlertDialog.findViewById<ProgressBar>(
                            R.id.progress_bar
                        ).visible()
                        pinAuthDialog(true)
                    }
                }
            } else if (str.contains(getString(R.string.wallet_upi_confirm))) {

                val strList = str.split("-")
                if (strList.isNotEmpty()) {
                    walletMobileNo = strList[1]
                }

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val btnText = walletUpiAlertDialog.findViewById<Button>(
                        R.id.btnConfirm
                    ).text.toString()
                    if (btnText == getString(R.string.confirm)) {
                        when {
                            selectedWalletUpiOptionId == null -> toast(getString(R.string.validate_wallet_upi))
                            walletMobileNo.isEmpty() -> toast(getString(R.string.validate_mobile_number))
                            else -> {
                                if (privilegeResponseModel.phoneNumValidationCount!! <= walletMobileNo.toCharArray().size) {
                                    if (isNetworkAvailable()) {
                                        pinAuthDialog(false)
                                    } else
                                        noNetworkToast()
                                } else {
                                    toast(getString(R.string.invalid_mobile_number))
                                }

                            }
                        }
                    } else {
                        val walletOtp =
                            walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputEditText>(
                                R.id.et_otp
                            ).text.toString()

                        if (walletOtp.isEmpty())
                            toast(getString(R.string.validate_otp))
                        else {
                            if (isNetworkAvailable()) {
                                validateWalletOtpApi(otp = walletOtp)
                            } else
                                noNetworkToast()
                        }
                    }
                }
            } else if (str == "qr_confirm") {
                walletUpiAlertDialog.dismiss()
                upiCreateQRAlertDialog.dismiss()
            } else if (str == getString(R.string.cancel)) {
                walletUpiAlertDialog.dismiss()
                upiCreateQRAlertDialog.dismiss()
                isCancelledClicked = true
            } else if (str == getString(R.string.unauthorized)) {
                //clearAndSave(requireContext())
                PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun pinelabPaymentType(type: Int, bookTicketFullResponse: Any, isExtraSeat: Boolean) {
        var totalFaree: String = ""
        var passengerPhoneNo: String? = ""
        var ticketNumber: String? = ""

        if (!isExtraSeat) {
            val ticketData = (bookTicketFullResponse as BookTicketFullResponse)
            totalFaree = (ticketData.result.total_fare * 100).toInt().toString()
            passengerPhoneNo = ticketData.result.passenger_details[0].mobile
            ticketNumber = ticketData.result.ticket_number
            pnrNumber = ticketNumber

        } else {
            val ticketData = (bookTicketFullResponse as BookExtraSeatResponse)
            if(ticketData.total_fare.contains("₹")){
                totalFaree = (ticketData.total_fare.substringAfter("₹").toDouble() * 100).toInt().toString()
            }else{
                totalFaree = (ticketData.total_fare.toDouble() * 100).toInt().toString()
            }
            passengerPhoneNo = ticketData.passenger_details[0].mobile
            ticketNumber = ticketData.ticketNumber
            pnrNumber = ticketNumber
        }

        var paymentType = ""
        if (type == PAYMENT_QR) {
            paymentType = "5120"
        } else {
            paymentType = "4001"

        }

        val headerObj = JSONObject()
        headerObj.put("ApplicationId", "be1dc81f1cd941f39afd7ccbb7d7f023")
        headerObj.put("UserId", "user1234")
        headerObj.put("MethodId", "1001")
        headerObj.put("VersionNo", "1.0")

        val detailObj = JSONObject()
        detailObj.put("TransactionType", paymentType)
        detailObj.put("BillingRefNo", ticketNumber)
        detailObj.put("PaymentAmount", totalFaree)
        detailObj.put("MobileNumberForEChargeSlip", passengerPhoneNo)


        Timber.e("TransactionType : $paymentType")
        Timber.e("BillingRefNo : $ticketNumber")
        Timber.e("PaymentAmount : $totalFaree")
        Timber.e("MobileNumberForEChargeSlip : $passengerPhoneNo")


        val json = JSONObject()
        json.put("Header", headerObj)
        json.put("Detail", detailObj)

        pinelabPayment(json,isExtraSeat)
    }

    private fun pinelabPayment(json: JSONObject, isExtraSeat: Boolean) {
        val data = Bundle()
        val value = json.toString()
        val isExtra = isExtraSeat
        data.putString(BILLING_REQUEST_TAG, value)
        message.data = data
        try {
            message.replyTo = Messenger(IncomingHandler(this,isExtraSeat))
            mServerMessenger!!.send(message)
        } catch (e: Exception) {

        }
    }

    override fun onDataSend(type: Int, file: Any) {
        when (type) {
            1 -> {
                countDownTimer = file as CountDownTimer
            }

            2 -> {
                redelcomPaymentDialog = file as AlertDialog
            }

            3 -> {
                val intent = Intent(this, NewCoachActivity::class.java)
                intent.putExtra("fromTicketDetails", "rebooking")
                PreferenceUtils.putString("SelectionCoach", "BOOK")
                PreferenceUtils.putString("fromBusDetails", "bookBlock")
                PreferenceUtils.removeKey("seatwiseFare")
                PreferenceUtils.removeKey("isEditSeatWise")
                PreferenceUtils.removeKey("PERSEAT")

                retrieveSelectedSeats().clear()
                seatDetailList.clear()
                getPassengerDetails().clear()
                com.bitla.ts.utils.common.passengerList.clear()
                retrieveSelectedPassengers().clear()
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {


    }


    override fun onCancelled() {

    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?,
        hours: Int,
        minutes: Int
    ) {
        if (firstDate != null) {
            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours)
                firstDate.set(Calendar.MINUTE, minutes)

                val releaseDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)

                binding.layoutPartialAmt.tvReleaseDate.text = releaseDate
                partialBlockingDate = releaseDate

            }
        }
    }
    fun fareBreakupDialog(context: Context, totalFare:String, tax:String, discount:String, netFare:String){
        val bindingSheet = DialogFareBreakupBinding.inflate(LayoutInflater.from(context))
        val  bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialog)
        bottomSheetDialog.setContentView(bindingSheet.root)



        val serviceTaxIndex = fareBreakup.indexOfFirst {
            (it.label.contains(getString(R.string.service_tax_amount))) && (it.value.toString()
                .toDouble() > 0.0)
        }
        if (serviceTaxIndex != -1) {
            val totalFareIndex = fareBreakup.indexOfFirst {
                (it.label == getString(R.string.total_fare))
            }
            if (totalFareIndex != -1) {
                fareBreakup[totalFareIndex].value =
                    "${fareBreakup[totalFareIndex].value}"
            }
        }


        val bookingChargesAdapter =
            FareBreakupAdapter(
                this,
                fareBreakup as ArrayList<FareBreakUpHash>,
                currency,
                currencyFormat
            )
        bindingSheet.fareBreakupRV.adapter = bookingChargesAdapter


        bottomSheetDialog.show()

        bindingSheet.crossIV.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bindingSheet.netValueTV.text=netFare

    }
    fun handlePinelabSuccessResp(respData: CardSaleResponse, isExtra: Boolean, pinelabResponse: String) {
        pinelabResponseData = respData
        pinelabResponseString = pinelabResponse
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog?.dismiss()
        }
       // if (respData.detail != null && respData.response!!.responseCode == 0) {
            var reqBody : ReqBodyPinelab?= null
            if(isExtra){
                val jsonObj = JSONObject(pinelabResponseString!!)

                reqBody =
                    ReqBodyPinelab(
                        loginModelPref.api_key,
                        pnrNumber,
                        true,
                        true,
                        resId.toString(),
                        sourceId,
                        destinationId,
                        pinelab_response = jsonObj
                    )
            }else{
                val jsonObj = JSONObject(pinelabResponseString!!)

                reqBody =
                    ReqBodyPinelab(
                        loginModelPref.api_key,
                        pnrNumber,
                        true,
                        pinelab_response = jsonObj,
                        pinelab_payment_type = 0
                    )
            }
            DialogUtils.showProgressDialog(this)
            bookingOptionViewModel.pinelabStatusApi(reqBody, pinelab_transaction_status_api)
            pinelabBillingRefNo = pnrNumber
        /*} else {
           *//* toast(respData.response!!.responseMsg)
            DialogUtils.showProgressDialog(this)
            dashboardViewModel.releaseTicketAPI(
                com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBody(
                    loginModelPref.api_key,
                    lastBookedTicketNumber!!,
                    releaseTicketRemarks,
                    false,
                    com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.Ticket(
                        lastBookedSeatsNumber.toString()
                    ),
                    json_format,
                    locale = locale
                ),
                release_phone_block_ticket_method_name
            )*//*


        }*/


    }

    private fun callPhonePeDirectUPIApp(pnr: String?, amount: Double) {
        if(pnr == null) {
            toast("PNR Cannot be null")
            return
        }
        val phonePeDirectUPIForAppRequest = PhonePeDirectUPIForAppRequest(
            amount = amount,
            pnrNumber = pnr,
            upiType = phonePeUpiType ,
            userNumber = phonePeUserNumber ,
            vpa = phonePeVpa,
        )

        phonePeViewModel.callPhonePeDirectUPIForApp(
            loginModelPref.api_key,
            phonePeDirectUPIForAppRequest,
            phonepe_direct_upi_for_app
        )

        binding.includeProgress.progressBar.visible()
    }

    private fun callPhonePeUpiTransactionStatusApi(pnr: String?) {

        if(pnr == null) {
            toast("PNR Cannot be null")
            return
        }
        val phonePeUpiTransactionStatus = PhonePeDirectUPITransactionStatusRequest(
            apiKey = loginModelPref.api_key,
            isFromMiddleTier = true,
            isSendSms = true,
            pnrNumber = pnr
        )

        phonePeViewModel.callPhonePeDirectUPITransactionStatus(phonePeUpiTransactionStatus,upi_phonepe_direct_txn_status)
    }

    private fun setPhonePeObserver() {
        phonePeViewModel.phonePeDirectValidateUpiId.observe(this) {
            try {
                linearLayoutProgressBar?.gone()

                if (it.code == "SUCCESS") {
                    buttonVerify?.gone()
                    buttonConfirm?.isClickable = true
                    buttonConfirm?.setBackgroundResource(R.drawable.button_selected_bg)
                    toast(it.data?.name)
                } else {
                    toast(it.message)
                }
            } catch(e: Exception) {
                Timber.d("Error in PhonePeObserver phonePeDirectValidateUpiId ${e.message}")
                toast(getString(R.string.server_error))
            }

        }

        phonePeViewModel.phonePeDirectUPIForApp.observe(this) {
            binding.includeProgress.progressBar.gone()

            try {
                if (it.code == 200) {
                    if (it.data?.pngUrl.isNullOrEmpty().not()) {
                        val header = "data:image/png;base64,"
                        val imageStringBase64 = it.data?.pngUrl?.replace(header, "")
                        val imageBytes = Base64.decode(imageStringBase64, Base64.DEFAULT)
                        val decodedImage =
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)


                        DialogUtils.phonePeUPICreateQrCodeDialog(this,
                            decodedImage,
                            transactionStatusApiCallListener = {
                                stopPhonePeTransactionStatusApiCall = it

                                lastSelectedPaymentPosition = 0
                                setPaymentOptionsAdapter()
                            }
                        )

                        callPhonePeUpiTransactionStatusApi(phonePeUPITicketPNR)
                    } else {

                        DialogUtils.phonePeUPICreateQrCodeDialog(this,
                            null,
                            transactionStatusApiCallListener = {
                                stopPhonePeTransactionStatusApiCall = it

                                lastSelectedPaymentPosition = 0
                                setPaymentOptionsAdapter()
                            }
                        )
                        callPhonePeUpiTransactionStatusApi(phonePeUPITicketPNR)
                    }

                } else {
                    toast(it.message)
                }
                callPhonePeUpiTransactionStatusApi(phonePeUPITicketPNR)
            } catch(e: Exception) {
                Timber.d("Error in PhonePeObserver phonePeDirectUPIForApp ${e.message}")
                toast(getString(R.string.server_error))
            }
        }

        phonePeViewModel.phonePeDirectUPITransactionStatus.observe(this) {
            try {
                if (it.code == 200) {
                    val intent = Intent(this, TicketDetailsActivity::class.java)
                    intent.putExtra(
                        getString(R.string.TICKET_NUMBER),
                        phonePeUPITicketPNR
                    )
                    intent.putExtra(
                        "activityName",
                        BookingPaymentOptionsActivity::class.java
                    )
                    intent.putExtra("activityName2", "booking")
                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                    finish()
                } else if (it.code == 411) {
                    if (!stopPhonePeTransactionStatusApiCall) {
                        callPhonePeUpiTransactionStatusApi(phonePeUPITicketPNR)
                    }
                } else {
                    toast(it.message)
                }
            } catch( e: Exception) {
                Timber.d("Error in PhonePeObserver phonePeDirectUPITransactionStatus ${e.message}")
                toast(getString(R.string.server_error))
            }
        }
    }
}

private class IncomingHandler(
    bookingPaymentOptionsActivity: BookingPaymentOptionsActivity,
    isExtraSeat: Boolean
) :
    Handler() {
    private val BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE"
    var activity = bookingPaymentOptionsActivity
    var isExtra = isExtraSeat

    override fun handleMessage(msg: Message) {
        val bundle = msg.data
        val value = bundle.getString(BILLING_RESPONSE_TAG)
        Timber.d("Value :", value.toString())
        val data = Gson().fromJson<CardSaleResponse>(value.toString(), CardSaleResponse::class.java)
        var respData: CardSaleResponse? = null
        respData = data
        activity.handlePinelabSuccessResp(respData,isExtra,value.toString())
    }
}