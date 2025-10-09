package com.bitla.ts.presentation.view.activity.ticketDetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.util.Log
import android.view.*
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseUpdateCancelTicket
import com.bitla.ts.app.base.EditPassengerSheet
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.ActivityTicketDetailsBinding
import com.bitla.ts.databinding.DialogCancelTicketBinding
import com.bitla.ts.databinding.SheetReleaseTicketsBinding
import com.bitla.ts.databinding.WhatsappBottomSheetBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.cancel_partial_ticket_model.request.CancelPartialTicketRequest
import com.bitla.ts.domain.pojo.cancellation_details_model.request.ZeroCancellationDetailsRequest
import com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.ConfirmOtpReleasePhoneBlockTicketRequest
import com.bitla.ts.domain.pojo.confirm_pay_at_bus.PayAtBusResponse
import com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReleaseTicketRequest
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.passenger_details_result.ContactDetail
import com.bitla.ts.domain.pojo.passenger_details_result.PassengerDetailsResult
import com.bitla.ts.domain.pojo.pay_pending_amount.PayPendingAmount
import com.bitla.ts.domain.pojo.pay_pending_amount.request.PayPendingAmountRequest
import com.bitla.ts.domain.pojo.pay_pending_amount.request.Ticket
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.redelcom.RedelcomPreferenceData
import com.bitla.ts.domain.pojo.redelcom.ReqBodyPrint
import com.bitla.ts.domain.pojo.release_partial_booked.ReleasePartialBookedTicket
import com.bitla.ts.domain.pojo.send_sms_email.request.ReqBody
import com.bitla.ts.domain.pojo.send_sms_email.request.SendSMSEmailRequest
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail
import com.bitla.ts.domain.pojo.ticket_details.request.TicketDetailsRequest
import com.bitla.ts.domain.pojo.ticket_details.response.Body
import com.bitla.ts.domain.pojo.ticket_details.response.Insurance
import com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail
import com.bitla.ts.domain.pojo.ticket_details.response.TicketDetailsModel
import com.bitla.ts.presentation.adapter.MealCouponsAdapter
import com.bitla.ts.presentation.adapter.ReleaseTicketPassengersListAdapter
import com.bitla.ts.presentation.view.activity.ConfirmPhoneBookingActivity
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.NewCoachActivity
import com.bitla.ts.presentation.view.activity.NewConfirmPhoneBookingActivity
import com.bitla.ts.presentation.view.activity.ShiftPassengerActivity
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.viewModel.CancelTicketViewModel
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.TicketDetailsViewModel
import com.bitla.ts.utils.PosPrintUtils.SunmiPrintHelper
import com.bitla.ts.utils.bluetooth_print.AsyncBluetoothEscPosPrint
import com.bitla.ts.utils.bluetooth_print.AsyncEscPosPrint
import com.bitla.ts.utils.bluetooth_print.AsyncEscPosPrinter
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.constants.BookingHistory.CLOCK_ICON
import com.bitla.ts.utils.constants.CallOptionsClick.CALL_ICON_TICKET_DETAILS
import com.bitla.ts.utils.constants.EmailShare.EMAIL_SHARE_TICKET_DETAILS
import com.bitla.ts.utils.constants.RelaseTicket.RELEASE_TICKET_DETAILS
import com.bitla.ts.utils.constants.ShareIcon.SHARE_ICON_TICKET_DETAILS
import com.bitla.ts.utils.constants.ShareViaSms.WHATSAPP_SHARE_TICKET_DETAILS
import com.bitla.ts.utils.constants.ShiftPax.SHIFT_PASSENGER_TICKET_DETAILS
import com.bitla.ts.utils.constants.SmsShare.SMS_SHARE_TICKET_DETAILS
import com.bitla.ts.utils.constants.TicketDetails.NEW_BOOKING_TICKET_DETAILS
import com.bitla.ts.utils.constants.TicketDetails.UPDATE_TICKET_TICKET_DETAILS
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64
import com.google.gson.GsonBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import toBitmap
import toast
import visible
import java.io.ByteArrayOutputStream
import java.lang.Double.parseDouble
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class TicketDetailsActivity : BaseActivity(), View.OnClickListener, OnItemClickListener,
    OnItemCheckListener, DialogSingleButtonListener, DialogButtonListener, VarArgListener,
    DialogButtonTagListener, DialogReturnDialogInstanceListener {

    companion object{
        val tag = TicketDetailsActivity::class.java.simpleName
    }

    private var hexvalue: String? = ""
    private var allowQoalaInsurance: Boolean = false
    private var otpDialog: AlertDialog? = null
    private var bottomSheet: BottomSheetDialog? = null
    private var withoutSpacePrint = false
    private var busLogo: Bitmap? = null
    private var TicketQRCode: Bitmap? = null
    private var barcodeValue: String? = null
    private var isAllowToPrintBarcode: Boolean = false
    private var creditDebitCardNo: String? = null
    private var selectedPartialPaymentOption: String = "1"
    private var isFirstPrint: Boolean = true
    private var qrCodeInput: String = ""
    private var hexaDecimalString: String? = null
    private var myWebView: WebView? = null
    private var operatorLogo: String? = ""
    private lateinit var binding: ActivityTicketDetailsBinding
    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()
    private lateinit var loginModelPref: LoginModel
    private var ticketNumber: String? = ""
    private var pnr: String? = ""
    private var travelDate: String? = ""
    private var bookedBy: String? = ""
    private var bookedAt: String? = ""
    private var boarding: String? = ""
    private var bAddress: String? = ""
    private var dropping: String? = ""
    private var dAddress: String? = ""
    private var passName: String? = ""
    private var age: String? = ""
    private var fare: String? = ""
    private var coach: String? = ""
    private var busType: String? = ""
    private var gender = " "
    private var reservationId: Long = 0L
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var serviceName: String = ""
    private var tempTicket: String = ""
    private var shareTextWhatsapp: String = ""


    private var boardingTravelDate: String? = ""
    private var boardingDepTime: String? = ""
    private var dropOffTravelDate: String? = ""
    private var dropOffDepTime: String? = ""
    private var boardingContactNumbers: String? = ""
    private var seatNumbers: String? = ""
    private var serviceNumber: String? = ""
    private var serviceTextAmount: Double = 0.0
    private var passengerMobile: String? = ""

    private lateinit var baseUpdateCancelTicket: BaseUpdateCancelTicket
    private lateinit var editPassengerSheet: EditPassengerSheet

    private lateinit var bccId: String
    private lateinit var apiKey: String
    private var isShiftPassenger: Boolean = false
    private var isInsurance: Boolean = false
    private var isCanCancelTicket: Boolean = false
    private var redirectToDashBoardActivity: Boolean = true

    private lateinit var _sheetReleaseTicketsBinding: SheetReleaseTicketsBinding
    private lateinit var _sheetWhatsapp: WhatsappBottomSheetBinding
    private var passengerDetailList: MutableList<PassengerDetail?>? = null
    private var toolbarTitle: String = ""
    private var isFromPnrActivity: Boolean = false
    private var isFromBooking: Boolean = false

    private var isAllowToSendSmsInPnrSearchPage: Boolean? = false
    private var isAllowToSendWhatsappMessages: Boolean? = false
    private var isShowPassengerSearchInHomePageForUsers: Boolean? = false
    private var isMasking: Boolean? = false
    private var role: String? = ""
    private var isAgentLogin: Boolean = false
    private var boardingStageID: String? = ""
    private var droppingStageID: String? = ""

    private val seatList = ArrayList<SeatDetail>()
    private var passengerList: ArrayList<PassengerDetailsResult> = ArrayList()
    private var passengerContactDetailList: ArrayList<ContactDetail> =
        ArrayList()
    var selectedSeatDetails = ArrayList<SeatDetail>()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private val releaseTicketRemarks: String = "release ticket" //fixed
    private val isFromDashboard: Boolean = false //fixed
    private var amountCurrency: String = ""
    private var currencyFormatt: String = ""
    private var countryName: String = ""
    private val selectedSeatNumber = StringBuilder()
    private val currentCheckedItem: MutableList<PassengerDetail?> = ArrayList()
    private var totalNoOfSeats = 0
    private var bookingSource = ""
    private var originalTemplate: String? = null
    private var bluetoothPrintTemplate: String? = null
    private lateinit var ticketData: Body
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var selectedDevice: BluetoothConnection? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var bmpLogo: Bitmap? = null
    private var bmpQrCode: Bitmap? = null
    private var insuranceBitmap: Bitmap? = null
    private var locale: String? = ""
    private var countryList = ArrayList<Int>()
    private var finalMobileNumber = ""
    private var finalFareAmount = ""
    private var refundAmountFare = ""
    private var cancellationCharges = 0.0
    private var cancelOptkey = ""
    private var cancelOtp = ""
    private val cancelTicketViewModel by viewModel<CancelTicketViewModel<Any?>>()
    private var cancelOtpLayoutDialogOpenCount = 0
    private lateinit var progressDialog: ProgressDialog
    private var isPayAtBus = false
    lateinit var domain: String


    private val paymentOptionsList = mutableListOf<SearchModel>()

    private var selectedCancellationType: String = ""
    private var isZeroPercentCancellationCheck = false

    private var cancellationAmount: String = ""
    private var refundAmount: String = ""
    private var cancelPercent: String = ""

    private var cancelOnBehalOf: Int? = null
    private var isCanCancelTicketForUser: Boolean = false
    private var isOnbehalfOnlineAgentFlag: Boolean = false
    private var sendSms = false


    private var ticketCancellationPercentage: String = ""

    private var isBound: Boolean? = false
    private var mServerMessenger: Messenger? = null


    private val PLUTUS_SMART_PACKAGE = "com.pinelabs.masterapp"
    private val PLUTUS_SMART_ACTION = "com.pinelabs.masterapp.SERVER"
    private val MESSAGE_CODE = 1001
    private val BILLING_REQUEST_TAG = "MASTERAPPREQUEST"
    private val BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE"
    private var bottomSheetDialoge: BottomSheetDialog? = null

    var message: Message = Message.obtain(null, MESSAGE_CODE)
    var printArray = JSONArray()
    private var pinSize = 0
    private var shouldPhoneBlockingRelease = false
    private var shouldTicketCancellation = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SunmiPrintHelper.getInstance().initSunmiPrinterService(this)
        onClickListeners()
        startShimmerEffect()
        initializePinelab()
        if(IS_PINELAB_DEVICE){
            binding.shareBtn.gone()
            binding.callPhoneNumber.gone()
        }
    }

    private fun initializePinelab() {
        val intent = Intent()
        intent.setAction(PLUTUS_SMART_ACTION)
        intent.setPackage(PLUTUS_SMART_PACKAGE)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun initUI() {
        binding = ActivityTicketDetailsBinding.inflate(layoutInflater)



        getPref()


        lifecycleScope.launch(Dispatchers.Main) {
            busLogo = async(Dispatchers.IO) {
                getBitmapDirectFromUrl(operatorLogo!!)
            }.await()
        }
        swipeRefreshLayout()
        swipeRefreshLayout()
        if (intent.hasExtra(getString(R.string.TICKET_NUMBER))) {
            val getTicketNumber = intent.getStringExtra(getString(R.string.TICKET_NUMBER))
                ?: getString(R.string.empty)
            ticketNumber = getTicketNumber.substringBefore(" ")
        }

        if (intent.hasExtra("returnToDashboard")) {
            redirectToDashBoardActivity = intent.getBooleanExtra("returnToDashboard", false)
        }

        if (intent.hasExtra("fromPnrActivity")) {
            isFromPnrActivity = intent.getBooleanExtra("fromPnrActivity", false)
        }
        if (intent.hasExtra("activityName2")) {
            Timber.d("intentOutcome: ${intent.getStringExtra("activityName2")}")
            isFromBooking = true
        }

//        if (isFromPnrActivity) {
//            binding.imgTitle.gone()
//        }
        if (isFromBooking) {
            binding.reBookTktBtn.visible()
        }

        baseUpdateCancelTicket =
            supportFragmentManager.findFragmentById(R.id.layoutUpdateTicketContainer) as BaseUpdateCancelTicket
        editPassengerSheet =
            supportFragmentManager.findFragmentById(R.id.layoutEditPassengerSheet) as EditPassengerSheet



        setSendSMSEmailObserver()
        setReleaseTicketObserver(bottomSheetDialoge!!)

        binding.confirmBookingBtn.setOnClickListener {
            if (isPayAtBus) {
                DialogUtils.twoButtonDialog(
                    this,
                    "${getString(R.string.confirmBooking)}?",
                    getString(R.string.confirm_this_booking),
                    getString(R.string.no_dont_confirm),
                    getString(R.string.confirm),
                    this
                )

            } else {
                val intent = if(countryName.equals("india",true) && !privilegeResponseModel.isAgentLogin) {
                    Intent(this, NewConfirmPhoneBookingActivity::class.java)
                }else{
                    Intent(this, ConfirmPhoneBookingActivity::class.java)
                }
                intent.putExtra("fromTicketDetailsActivity", true)
                intent.putExtra(getString(R.string.pnr_number), pnr)
                intent.putExtra(getString(R.string.select_boarding_stage), boardingStageID)
                intent.putExtra(getString(R.string.select_dropping_stage), droppingStageID)
                intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                // intent.putExtra(getString(R.string.travel_date), getDateDMY(travelDate.toString()))
                intent.putExtra(getString(R.string.travel_date), travelDate.toString())
                intent.putExtra(getString(R.string.bus_type), busType)
                intent.putExtra(getString(R.string.source_id), sourceId)
                intent.putExtra(getString(R.string.destination_id), destinationId)
                intent.putExtra(getString(R.string.origin), boarding)
                intent.putExtra(getString(R.string.destination), dropping)
                Timber.d("confirmBook: $finalFareAmount")

                intent.putExtra(getString(R.string.totalAmount), finalFareAmount)
                intent.putExtra("seatNumbers", seatNumbers)
                if(ticketDetailsViewModel.dataTicketDetails.value?.body?.booking_source=="Online Agent Booking" || ticketDetailsViewModel.dataTicketDetails.value?.body?.booking_source=="Offline Agent Booking" ){
                    intent.putExtra("isOnBehalgOfAgent",true)
                }
                startActivity(intent)

            }

        }
        setTicketDetailsObserver()


        callTicketDetailsApi()

        setCancelTicketObserve()

        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        lifecycleScope.launch {
            ticketDetailsViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            dashboardViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            cancelTicketViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
    }


    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        locale = PreferenceUtils.getlang()
//        toolbarTitle = PreferenceUtils.getString(getString(R.string.toolbar_title)).toString()

        loginModelPref = PreferenceUtils.getLogin()
        role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, this)
        domain = PreferenceUtils.getPreference(
            PREF_DOMAIN,
            getString(R.string.empty)
        )!!
        if (getPrivilegeBase()!= null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel

            privilegeResponseModel.apply {
                if (allowToSendSmsInPnrSearchPage != null) {
                    isAllowToSendSmsInPnrSearchPage = allowToSendSmsInPnrSearchPage
                }

                if (allowToSendWhatsappMessages != null) {
                    isAllowToSendWhatsappMessages = allowToSendWhatsappMessages
                }


                if (showPassengerSearchInHomePageForUsers != null) {
                    isShowPassengerSearchInHomePageForUsers = showPassengerSearchInHomePageForUsers
                }

                amountCurrency = currency.ifEmpty {
                    currency
                }
                currencyFormatt = getCurrencyFormat(
                    this@TicketDetailsActivity,
                    privilegeResponseModel.currencyFormat
                )

                pinSize = pinCount ?: 6
                shouldTicketCancellation = pinBasedActionPrivileges?.ticketCancellation ?: false
                shouldPhoneBlockingRelease = pinBasedActionPrivileges?.phoneBlockingRelease ?: false

//                currencyFormatt =
//                    privilegeResponseModel.currencyFormat.ifEmpty { getString(R.string.indian_currency_format) }

                countryName = privilegeResponseModel.country

            }
        }

        try {
            if (getCountryCodes().isNotEmpty())
                countryList = getCountryCodes()
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
        /*else {
            val prefCountryCode = PreferenceUtils.getObject<DomainModel?>(PREF_COUNTRY_CODE)
            Timber.d("+91Check:${prefCountryCode?.dailing_code} ")

            if (prefCountryCode?.dailing_code != null && prefCountryCode?.dailing_code?.isNotEmpty()) {
                countryList = prefCountryCode.dailing_code
            } else {
                countryList.add(91)
            }

        }*/

        if (isAllowToSendSmsInPnrSearchPage == true) {
            binding.sendSms.visible()
        } else {
            binding.sendSms.gone()
        }

        if (isAllowToSendWhatsappMessages == true) {
            if(!IS_PINELAB_DEVICE){
                binding.shareToWhatsapp.visible()
            }else{
                binding.shareToWhatsapp.gone()
            }
        } else {
            binding.shareToWhatsapp.gone()
        }

        /*if (PreferenceUtils.getPreference(PREF_LOGO, getString(R.string.empty)) != null) {
            operatorLogo = PreferenceUtils.getPreference(PREF_LOGO, getString(R.string.empty))
            Timber.d("operatorLogo $operatorLogo")
        }*/



        operatorLogo = PreferenceUtils.getPreference(PREF_LOGO, "")

        if(PreferenceUtils.getPrintingType() == PRINT_TYPE_PINELAB) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val imagePath =
                        getBitmapDirectFromUrl(operatorLogo!!)
                    val output = ByteArrayOutputStream(imagePath!!.getByteCount())
                    imagePath.compress(Bitmap.CompressFormat.JPEG, 100, output)
                    val imageBytes: ByteArray = output.toByteArray()

                    hexvalue = bytesToHex(imageBytes)
                }
            } catch (e: java.lang.Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        }
        allowQoalaInsurance = privilegeResponseModel.allowQoalaInsurance
    }

    fun bytesToHex(bytes: ByteArray): String {
        val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = HEX_ARRAY[v ushr 4]
            hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun loadTicketDetails() {
        binding.apply {
            imgTitle.setBackgroundResource(R.drawable.thumbs_up)
            notePhoneBlocked.gone()
            confirmBookingBtn.gone()
            releaseTicketBtn.gone()
//            btnNewBooking.gone()
            binding.includeProgress.progressBar.visibility = View.GONE

        }

        if (allowQoalaInsurance && ::ticketData.isInitialized && ticketData.passengerDetails != null && ticketData.passengerDetails?.any { it?.insuranceAmount != null }!!) {
            binding.tvInsuranceTitle.visible()
            binding.tvInsuranceAmt.visible()
            if (ticketData.insuranceTransDetails?.details?.isNotEmpty() == true)
                binding.imgInsuranceInfo.visible()
            else
                binding.imgInsuranceInfo.gone()

            var totalInsuranceAmt = 0.0
            ticketData.passengerDetails!!.forEach {
                totalInsuranceAmt += it?.insuranceAmount?.replace("Rp", "")?.toDouble() ?: 0.0
            }
            binding.tvInsuranceAmt.text = "${privilegeResponseModel.currency} $totalInsuranceAmt"

            binding.imgInsuranceInfo.setOnClickListener {
                DialogUtils.dialogInsurance(this, this, ticketData.insuranceTransDetails)
            }
        } else {
            binding.tvInsuranceTitle.gone()
            binding.tvInsuranceAmt.gone()
        }

        if (::ticketData.isInitialized && ticketData.partialPaymentDetails != null) {
            binding.cancelTktBtn.gone()
            binding.imgTitle.visible()
            binding.imgTitle.setBackgroundResource(R.drawable.ic_partial_ticket)
            binding.titleMain.setText(R.string.partially_paid_ticket)
            binding.imgPartialPaymentInfo.visible()
            binding.btnPayPendingAmt.visible()
        } else {
            binding.cancelTktBtn.visible()
            binding.imgTitle.setBackgroundResource(R.drawable.thumbs_up)
            binding.titleMain.setText(R.string.ticket_booked_successfully)
            binding.imgPartialPaymentInfo.gone()
            binding.btnPayPendingAmt.gone()
        }
    }

    private fun setMealsAdapter(mealCouponList: MutableList<String>) {
        binding.rvMealCoupons.layoutManager = GridLayoutManager(this, 3)
        val mealCouponsAdapter = MealCouponsAdapter(this, mealCouponList)
        binding.rvMealCoupons.adapter = mealCouponsAdapter
    }

    private fun setMealTypeAdapter(mealTypeList: MutableList<String>) {
        binding.rvMealType.layoutManager = GridLayoutManager(this, 3)
        val mealTypeAdapter = MealCouponsAdapter(this, mealTypeList)
        binding.rvMealType.adapter = mealTypeAdapter
    }

    private fun loadCancelledTicket() {
        binding.apply {
            shareBtn.gone()
            travelDate.text = ""
            toolbar.setBackgroundColor(
                ContextCompat.getColor(
                    this@TicketDetailsActivity,
                    R.color.colorRed2
                )
            )
            constraintLayoutTop2.setBackgroundColor(
                ContextCompat.getColor(
                    this@TicketDetailsActivity,
                    R.color.colorRed2
                )
            )

            titleMain.setText(R.string.cancelled_tickets)
            imgTitle.setBackgroundResource(R.drawable.cancelled_img)
            cardView3.gone()
            cardView4.gone()
            viewPassengersBtn.gone()
            cardView2.alpha = 0.5f
            constraintLayout6.alpha = 0.5f
            callPhoneNumber.isEnabled = false
            cardView5.gone()
            notePhoneBlocked.gone()
//            noteAgent.gone()
        }



        binding.btnNewBooking.setOnClickListener {
            PreferenceUtils.putString(
                getString(R.string.BACK_PRESS),
                getString(R.string.new_booking)
            )
            intent = Intent(this, DashboardNavigateActivity::class.java)
            intent.putExtra("newBooking", true)
            startActivity(intent)
            finish()
        }
    }

    private fun loadPartiallyCancelled() {

        binding.apply {
            imgTitle.setBackgroundResource(R.drawable.partially_cancelled_img)
            notePhoneBlocked.gone()
            titleMain.setText(R.string.partially_cancelled_ticket)
            confirmBookingBtn.gone()
            releaseTicketBtn.gone()
            btnNewBooking.gone()
        }
    }

    private fun loadUnconfirmedTicket() {
        binding.apply {
            toolbar.setBackgroundColor(
                ContextCompat.getColor(
                    this@TicketDetailsActivity,
                    R.color.blue_dark
                )
            )
            constraintLayoutTop2.setBackgroundColor(
                ContextCompat.getColor(
                    this@TicketDetailsActivity,
                    R.color.blue_dark
                )
            )
            imgTitle.setBackgroundResource(R.drawable.unconfirmed_ticket)

            constraintLayout6.setBackgroundResource(R.drawable.phone_block_ticket_repeat)
            cardView2.setBackgroundResource(R.drawable.phone_block_ticket_repeat)
            constraintLayout6.setBackgroundResource(R.color.white)
            cardView2.setBackgroundResource(R.color.white)

            titleMain.setText(R.string.phone_booking)
            btnNewBooking.gone()
            cancelTktBtn.gone()


            if ((privilegeResponseModel.country.equals(
                    "indonesia",
                    true
                ) && isPayAtBus && ::ticketData.isInitialized
                        && ticketData.isConfirmOtaBooking != null
                        && ticketData.isConfirmOtaBooking) || (ticketData.ticketStatus.equals(
                    getString(R.string.pending)
                ) && ticketData.passengerDetails != null && ticketData.passengerDetails!![0]?.canConfirmPhoneBlock!!)
            ) {
                confirmBookingBtn.visible()
                binding.confirmBookingBtn.text = getString(R.string.confirm)
            } else {
                confirmBookingBtn.gone()
            }

            if ((privilegeResponseModel.country.equals(
                    "indonesia",
                    true
                ) && isPayAtBus && ::privilegeResponseModel.isInitialized
                        && privilegeResponseModel.allowToReleaseApiTentativeBlockedTickets != null
                        && privilegeResponseModel.allowToReleaseApiTentativeBlockedTickets!!) || (ticketData.ticketStatus.equals(
                    getString(R.string.pending)
                ) && ticketData.passengerDetails != null && ticketData.passengerDetails!![0]?.canReleasePhoneBlock!!)
            ) {
                releaseTicketBtn.visible()
            } else {
                releaseTicketBtn.gone()
            }
        }

//        loadBookingFailed()
    }

    @SuppressLint("SetTextI18n")
    private fun loadBookingFailed() {
        binding.apply {
            toolbar.setBackgroundColor(
                ContextCompat.getColor(
                    this@TicketDetailsActivity,
                    R.color.color_03_review_02_moderate
                )
            )
            constraintLayoutTop2.setBackgroundColor(
                ContextCompat.getColor(
                    this@TicketDetailsActivity,
                    R.color.color_03_review_02_moderate
                )
            )
            imgTitle.setBackgroundResource(R.drawable.booking_failed_img)
            titleMain.setText(R.string.booking_failed)
            travelDate.text = "Travel Date N/A"
            pnrText.text = "PNR / Invoice Number - N/A"
            pnrCard.alpha = 0.5f
            cardView2.gone()
            constraintLayout6.gone()
            shareBtn.gone()
            historyBtn.gone()
            cardView5.gone()
            cardView3.gone()
            cardView4.gone()
            notePhoneBlocked.gone()
            confirmBookingBtn.gone()
            releaseTicketBtn.gone()
            btnNewBooking.gone()
//            btnNewBookingBlu.gone()
//            retryBtn.visible()
//            goHomeBtn.visible()
            noteAgent.gone()
        }

    }

//    fun shareFunction() {
//
//        val sendIntent: Intent = Intent().apply {
//            action = Intent.ACTION_SEND
//            putExtra(Intent.EXTRA_TEXT, "Share Ticket details text here")
//            type = "text/plain"
//        }
//
//        val shareIntent = Intent.createChooser(sendIntent, null)
//        startActivity(shareIntent)
//    }

    private fun callFunction(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(
                this@TicketDetailsActivity,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this@TicketDetailsActivity,
                arrayOf(Manifest.permission.CALL_PHONE),
                200
            )
        } else {
            val telNo = getPhoneNumber(passPhone = phoneNumber, countryName)
            if (countryList.isNotEmpty()) {
                val finalTelNo = "+${countryList[0]}$telNo"
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$finalTelNo"))
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.d("")
            } else {
                toast("Call Permission Denied")
            }
        }
    }

    private fun shareToWhatsapp() {
        /*val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareTextWhatsapp)
        try {
            startActivity(whatsappIntent)
        } catch (ex: ActivityNotFoundException) {
            toast("Whatsapp is not Installed.")
        }*/

        val packageManager: PackageManager = packageManager

        val whatsappFound: Boolean = available("com.whatsapp")
        val whatsappFoundBusiness: Boolean = available("com.whatsapp.w4b")
        Timber.d("packageCheck::: $whatsappFound, $whatsappFoundBusiness")
        if (whatsappFound && whatsappFoundBusiness) {
            val bottomSheetDialoge = BottomSheetDialog(this, R.style.BottomSheetDialog)
            _sheetWhatsapp = WhatsappBottomSheetBinding.inflate(LayoutInflater.from(this))
            bottomSheetDialoge.setContentView(_sheetWhatsapp.root)
            _sheetWhatsapp.whatsappNormal.setOnClickListener {
                whatsappIntent("com.whatsapp")
                bottomSheetDialoge.dismiss()
            }
            _sheetWhatsapp.whatsappBusiness.setOnClickListener {
                whatsappIntent("com.whatsapp.w4b")
                bottomSheetDialoge.dismiss()
            }
            bottomSheetDialoge.show()
        } else
            if (whatsappFoundBusiness) {
                whatsappIntent("com.whatsapp.w4b")
            } else if (whatsappFound) {
                whatsappIntent("com.whatsapp")
            } else {
                toast("Whatsapp is not Installed.")
            }

    }

    private fun whatsappIntent(packageName: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)


            finalMobileNumber = getPhoneNumber(passPhone = passengerMobile, countryName)

            if (finalMobileNumber.isNotEmpty()) {

                val url =
                    "https://api.whatsapp.com/send?phone=" + "+${countryList[0]}" + finalMobileNumber + "&text=" + URLEncoder.encode(
                        shareTextWhatsapp,
                        "UTF-8"
                    )
                i.setPackage(packageName)
                i.data = Uri.parse(url)
                //if (i.resolveActivity(packageManager) != null) {
                startActivity(i)
                //}
            } else
                toast(getString(R.string.number_not_registered))
        } catch (ex: ActivityNotFoundException) {
            toast("Whatsapp is not Installed.")
        } catch (e: java.lang.Exception) {
            Timber.d("ExceptionMsg ${e.printStackTrace()}")
        }
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        return try {
            val list = pm.getInstalledPackages(0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }

    private fun available(name: String): Boolean {
        var available = true
        try {
            this.packageManager.getPackageInfo(name, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            available = false
        }
        // check if available

        return available
    }

    fun isAppInstalled(context: Context, packageName: String?): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName!!, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


    private fun callSendSMSEmailApi(type: String) {
        val bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
//        binding.includeProgress.progressBar.visible()

        val reqBody = ReqBody(
            ticketNumber ?: "", type, locale = locale,  api_key = if (getPrivilegeBase()?.country.equals("india", true)) {
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

    private fun viewPassengers() {
        tempTicket.trim().let { baseUpdateCancelTicket.showViewPassengersSheet(it) }
    }

    private fun releaseTicket() {
        bottomSheetDialoge = BottomSheetDialog(this, R.style.BottomSheetDialog)
        _sheetReleaseTicketsBinding = SheetReleaseTicketsBinding.inflate(LayoutInflater.from(this))
        bottomSheetDialoge!!.setContentView(_sheetReleaseTicketsBinding.root)

        bottomSheet = bottomSheetDialoge

        progressDialog = ProgressDialog(this)

        ticketDetailsViewModel.dataTicketDetails.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        ticketData = it.body
                        passengerDetailList = it.body.passengerDetails
                        originalTemplate = it.body.bluetoothPrintTemplate
                        bluetoothPrintTemplate = it.body.bluetoothPrintTemplate




                        finalFareAmount =
                            if (it.body.totalFare != null) it.body.totalFare.toString() else ""

                        selectedSeatNumber.clear()

                        //bluetoothPrint()

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
                            it.result.message.let { it1 -> toast(it1) }
                        }
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> toast(it1) }
                        }
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> toast(it1) }
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        var releaseTicketBtnClickCount = 0
        _sheetReleaseTicketsBinding.releaseTicketBtn.setOnClickListener {

            Timber.d("seat2:added-$currentCheckedItem")
            if (releaseTicketBtnClickCount == 0) {
                for (i in 0 until currentCheckedItem.size) {
                    selectedSeatNumber.append(currentCheckedItem[i]?.seatNumber)
                    if (i < currentCheckedItem.size - 1) {
                        selectedSeatNumber.append(",")
                    }
                }
            }

            if (selectedSeatNumber.isEmpty()) {
                toast(getString(R.string.selectSeat))
            } else if (releaseTicketBtnClickCount == 0) {
                _sheetReleaseTicketsBinding.progressBarRelease.visible()
                authPinPhoneReleaseDialog()
                releaseTicketBtnClickCount++
            }
        }

        setConfirmOtpReleaseObserver()
        setReleaseTicketPassengerAdapter()
        bottomSheetDialoge!!.show()
    }

    private fun bluetoothPrint() {

        Timber.d("bluetoothPrintTemplate before $bluetoothPrintTemplate ")

        if (bluetoothPrintTemplate?.contains("</img>")!!) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.substringAfter("</img>")
            bluetoothPrintTemplate = "\nIMAGE$bluetoothPrintTemplate"
        }
        if (bluetoothPrintTemplate?.contains("<qrcode") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "[C]<qrcode size='25'>$qrCodeInput</qrcode>\n[C]=======cut here=======",
                "BOARDING_QR\n[C]=======cut here======="
            )
            // bluetoothPrintTemplate = "${bluetoothPrintTemplate?.replace("[C]=======cut here=======","BOARDING_QR\n[C]=======cut here=======")}"
        }
        Timber.d("bluetoothPrintTemplate image edit $bluetoothPrintTemplate ")

        if (bluetoothPrintTemplate != null && ::ticketData.isInitialized && ticketData != null && ::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            commonReplacementPrint()
            if (!bluetoothPrintTemplate?.contains("FOR_EACH_SEAT")!!) {
                singleSeatBluetoothPrint()
            } else {
                multiSeatBluetoothPrint()
            }
        }

        Timber.d("bluetoothPrintTemplate after $bluetoothPrintTemplate ")
    }

    private fun singleSeatPOSPrint() {
        generateQrcode()
        var isBold = false
        var template = ""

        template = bluetoothPrintTemplate!!


        if (template.contains("PNR_NUMBER")) {
            template = template.replace("PNR_NUMBER", ticketData.ticketNumber!!)
        }
        if (template.contains("TRAVEL_DATE")) {
            template = template.replace("TRAVEL_DATE", ticketData.travelDate!!)
        }
        if (template.contains("SERVICE_NUMBER")) {
            template = template.replace("SERVICE_NUMBER", ticketData.serviceNumber!!)
        }
        if (template.contains("TAB_SPACE")) {
            template = template.replace("TAB_SPACE", " ")
        }
        if (template.contains("NEW_LINE")) {
            template = template.replace("NEW_LINE", " ")
        }
        if (template.contains("ONE_SPACE")) {
            template = template.replace("ONE_SPACE", " ")
        }
        if (template.contains("SEAT_EACH_NUMBERS")) {
            template = template.replace("SEAT_EACH_NUMBERS", ticketData.seatNumbers!!)
        }
        if (template.contains("BOARDING_POINT")) {
            template = template.replace("BOARDING_POINT", ticketData.boardingDetails?.address!!)
        }
        if (template.contains("DROPPING_POINT")) {
            template = template.replace("DROPPING_POINT", ticketData.dropOffDetails?.address!!)
        }
        if (template.contains("DEPARTURE_TIME")) {
            template = template.replace(
                "DEPARTURE_TIME", ticketData.boardingDetails?.depTime!!
            )
        }
        if(template.contains("BOLD_ON")){
            template = template.replace("BOLD_ON"," ")
        }
        if(template.contains("BOLD_OFF")){
            template = template.replace("BOLD_OFF"," ")
        }

        if(template.contains("ORIGIN")){
            template = template.replace("ORIGIN",ticketData.origin)
        }
        if(template.contains("DESTINATION")){
            template = template.replace("DESTINATION",ticketData.destination?:"")
        }
        if (template.contains("PASSENGER_NAME")) {
            template =
                template.replace("PASSENGER_NAME", ticketData.passengerDetails?.get(0)?.name!!)
        }
        if (template.contains("MOBILE_NUMBER")) {
            template =
                template.replace("MOBILE_NUMBER", ticketData.passengerDetails?.get(0)?.mobile!!)
        }
        if (template.contains("TICKET_EACH_FARE")) {
            template = template.replace(
                "TICKET_EACH_FARE", "${privilegeResponseModel.currency} ${
                    ticketData.totalFare?.toString()?.toDouble()?.convert(currencyFormatt) ?: ""
                }"
            )
        }
        if (template.contains("ACCOUNT_HOLDER_NAME")) {
            template = template.replace("ACCOUNT_HOLDER_NAME", loginModelPref.userName)
        }
        if (template.contains("TICKET_BOOKED_BY")) {
            template = template.replace(
                "TICKET_BOOKED_BY",
                ticketData.ticketLeadDetail?.ticketBookedBy!!
            )
        }
        if (ticketData.partialPaymentDetails?.paidAmount != null) {
            template = template.replace(
                "PAID_AMOUNT",
                ticketData.partialPaymentDetails?.paidAmount.toString().toDouble()
                    .convert(currencyFormat = currencyFormatt)
            )
        } else {
            template = template.replace("PAID_AMOUNT", "-")
        }

        if (ticketData.partialPaymentDetails?.remainingAmount != null) {
            template = template.replace(
                "REMAINING_AMOUNT",
                ticketData.partialPaymentDetails?.remainingAmount.toString().toDouble()
                    .convert(currencyFormat = currencyFormatt)
            )
        } else {
            template = template.replace(
                "REMAINING_AMOUNT", "-"
            )
        }

        template = ticketData.ticketStatus?.let { template.replace("TICKET_STATUS", it) } ?: "-"

        template = template.replace(
            "TICKET_FARE",
            "${privilegeResponseModel.currency} ${
                ticketData.totalFare?.toString()?.toDouble()?.convert(currencyFormatt) ?: ""
            }"
        )

        template =
            ticketData.seatNumbers?.let {
                template.replace(
                    "SEAT_NUMBERS",
                    it
                )
            }!!

        template = ticketData.boardingDetails?.contactPersons?.let {
            template.replace(
                "CONTACT_PERSON",
                it
            )
        }!!

        template =
            ticketData.busType?.let { template.replace("COACH_TYPE", it) }!!
        template =
            template.replace("REMARKS", ticketData.remarks ?: "-")
        template =
            template.replace("TERMINAL_ID", ticketData.terminalRefNo ?: "")
        if (ticketData.terminalRefNo.isNullOrEmpty()) {
            template = template.replace("TERMINAL_PULOGABANG", "")
        }
        template =
            template.replace("CURRENT_DATE", getTodayDate())

        template =
            template.replace("CURRENT_TIME", getTodayDateWithTime())



        if (template.contains("MEAL_COUPON_NUMBER")) {
            template =
                if (ticketData.passengerDetails != null && ticketData.passengerDetails!!.isNotEmpty()) {
                    var mealCoupons = ""
                    ticketData.passengerDetails?.forEach {
                        mealCoupons += it?.meal_coupons.toString()
                            .replace("[", "")
                            .replace("]", "").replace(",", "\n").replace(" ", "")
                        mealCoupons += "\n"
                    }
                    template.replace(
                        "MEAL_COUPON_NUMBER",
                        mealCoupons
                    )
                } else {
                    template.replace(
                        "MEAL_COUPON_NUMBER",
                        "-"
                    )
                }!!

        }

        if (template.contains("MEAL_COUNT") && !ticketData.passengerDetails.isNullOrEmpty()) {
            var mealCouponCount = 0
            ticketData.passengerDetails?.forEach {
                mealCouponCount += it?.meal_coupons?.size!!
            }
            template = template.replace(
                "MEAL_COUNT",
                mealCouponCount.toString()
            )
        }

        if (template.contains("MEAL_COUPON_LOOP")) {
            template = template.replace(
                "MEAL_COUPON_LOOP",
                ""
            )
        }

        template = ticketData.boardingDetails?.contactNumbers?.let {
            template.replace(
                "CONTACT_NUMBER_PERSON",
                it
            )
        }!!

        template = ticketData.boardingDetails?.landmark?.let {
            template.replace(
                "LANDMARK",
                it
            )
        } ?: "-"

        template = template.replace(
            "WEB_ADDRESS",
            privilegeResponseModel.webAddressUrl
        )


        val temp = template.split("\n")
        if (withoutSpacePrint) {
            for (i in temp) {
                when {
                    i.contains("FOR_EACH_SEAT") -> {
                        SunmiPrintHelper.getInstance()
                            .printText(i.replace("FOR_EACH_SEAT", ""), 24f, false, false, "")
                    }

                    i.contains("ALIGN_CENTER|") -> {
                        SunmiPrintHelper.getInstance().setAlign(1)
                        SunmiPrintHelper.getInstance()
                            .printText(i.substringAfter("|"), 24f, false, false, "")

                    }
                    i.contains("ALIGN_CENTER") -> {
                        SunmiPrintHelper.getInstance().setAlign(1)
                        SunmiPrintHelper.getInstance()
                            .printText(i.substringAfter("R"), 24f, false, false, "")
                    }
                    i.contains("BOLD_ON") -> {
                        isBold = true
                    }
                    i.contains("BOLD_OFF") -> {
                        isBold = false
                    }
                    i.contains("IMAGE") -> {
                        if (busLogo != null) {
//                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                            SunmiPrintHelper.getInstance().printBitmap(busLogo, 1)
                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                        }
                    }
                    i.contains("ALIGN_LEFT|") -> {
                        if (isBold) {
                            SunmiPrintHelper.getInstance().setAlign(0)
                            SunmiPrintHelper.getInstance()
                                .printText(i.substringAfter("|"), 24f, true, false, "")
                        } else {
                            SunmiPrintHelper.getInstance().setAlign(0)
                            SunmiPrintHelper.getInstance()
                                .printText(i.substringAfter("|"), 24f, false, false, "")
                        }

                    }
                    i.contains("BOARDING_QR") -> {
                        SunmiPrintHelper.getInstance().printBitmap(TicketQRCode, 1)
                        SunmiPrintHelper.getInstance().feedPaper()


                    }
                    else -> {
                        if (isBold) {
                            SunmiPrintHelper.getInstance().setAlign(0)
                            SunmiPrintHelper.getInstance()
                                .printText(i, 24f, true, false, "")
                        } else {
                            SunmiPrintHelper.getInstance().setAlign(0)
                            SunmiPrintHelper.getInstance()
                                .printText(i, 24f, false, false, "")
                        }

                    }
                }


            }

        } else {
            for (i in temp) {
                when {
                    i.contains("FOR_EACH_SEAT") -> {
                        SunmiPrintHelper.getInstance()
                            .printText(i.replace("FOR_EACH_SEAT", ""), 24f, false, false, "")
                    }

                    i.contains("ALIGN_CENTER|") -> {
                        SunmiPrintHelper.getInstance().setAlign(1)
                        SunmiPrintHelper.getInstance()
                            .printText(i.substringAfter("|") + "\n", 24f, false, false, "")

                    }
                    i.contains("ALIGN_CENTER") -> {
                        SunmiPrintHelper.getInstance().setAlign(1)
                        SunmiPrintHelper.getInstance()
                            .printText(i.substringAfter("R") + "\n", 24f, false, false, "")
                    }
                    i.contains("BOLD_ON") -> {
                        isBold = true
                    }
                    i.contains("BOLD_OFF") -> {
                        isBold = false
                    }
                    i.contains("IMAGE") -> {
                        if (busLogo != null) {
//                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                            SunmiPrintHelper.getInstance().printBitmap(busLogo, 1)
                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                        }
                    }
                    i.contains("ALIGN_LEFT|") -> {
                        if (isBold) {
                            SunmiPrintHelper.getInstance().setAlign(0)
                            SunmiPrintHelper.getInstance()
                                .printText(i.substringAfter("|") + "\n", 24f, true, false, "")
                        } else {
                            SunmiPrintHelper.getInstance().setAlign(0)
                            SunmiPrintHelper.getInstance()
                                .printText(i.substringAfter("|") + "\n", 24f, false, false, "")
                        }

                    }
                    i.contains("BOARDING_QR") -> {
                        SunmiPrintHelper.getInstance().printBitmap(TicketQRCode, 1)
                        SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                        SunmiPrintHelper.getInstance().feedPaper()


                    }
                    else -> {
                        if (isBold) {
                            SunmiPrintHelper.getInstance().setAlign(0)
                            SunmiPrintHelper.getInstance()
                                .printText(i + "\n", 24f, true, false, "")
                        } else {
                            SunmiPrintHelper.getInstance().setAlign(0)
                            SunmiPrintHelper.getInstance()
                                .printText(i + "\n", 24f, false, false, "")
                        }

                    }
                }


            }

        }
        SunmiPrintHelper.getInstance().feedPaper()


    }

    private fun multipleSeatPostPrint() {
        var template = bluetoothPrintTemplate!!

        try {
            if (ticketData.passengerDetails != null && ticketData.passengerDetails?.isNotEmpty()!!) {

                val multiSeats = mutableListOf<String>()
                for (i in 0..ticketData.passengerDetails?.size?.minus(1)!!) {
                    if (ticketData.passengerDetails?.size!! != 1) {
                        if (i < ticketData.passengerDetails?.size?.minus(1)!!) {
                            template = template.replace("[C]=", "")
                            template =
                                template.replace("cut here", "")?.trimEnd()!!
                            template =
                                template.replace("BOARDING_QR", "")?.trim()!!
                            template =
                                template.replace("BAR_CODE", "")?.trim()!!

                        } else {
                            template = template.replace("[C]=", "")
                            template =
                                template.replace("=", "")?.trimEnd()!!

                            if (originalTemplate?.contains("BOARDING_QR")!!)
                                template = "${template}\nBOARDING_QR"
                            if (originalTemplate?.contains("BAR_CODE")!!)
                                template = "${template}\n\nBAR_CODE"
                            template =
                                "${template}\nALIGN_CENTER|=======cut here======="
                        }
                    }

                    template =
                        template.replace("FOR_EACH_SEAT", "")
                    if (template.contains("SEAT_EACH_NUMBERS")) {
                        template = template.replace(
                            "SEAT_EACH_NUMBERS",
                            ticketData.passengerDetails!![i]?.seatNumber ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketData.passengerDetails!![i.minus(1)]?.seatNumber?.let {
                                    template.replace(
                                        it,
                                        ticketData.passengerDetails!![i]?.seatNumber ?: ""
                                    )
                                }!!
                        }
                    }
                    if (template.contains("PASSENGER_EACH_NAME")) {
                        template = template.replace(
                            "PASSENGER_EACH_NAME",
                            ticketData.passengerDetails!![i]?.name ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketData.passengerDetails!![i.minus(1)]?.name?.let {
                                    template.replace(
                                        it,
                                        ticketData.passengerDetails!![i]?.name ?: ""
                                    )
                                }!!
                        }
                    }


                    if (template.contains("TICKET_EACH_FARE")) {
                        template = template.replace(
                            "TICKET_EACH_FARE",
                            ticketData.passengerDetails!![i]?.netFare?.toDouble()
                                ?.convert(currencyFormatt) ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketData.passengerDetails!![i.minus(1)]?.netFare?.let {
                                    template.replace(
                                        it,
                                        ticketData.passengerDetails!![i]?.netFare?.toDouble()
                                            ?.convert(currencyFormatt) ?: ""
                                    )
                                }!!
                        }
                    }


                    if (!ticketData.passengerDetails!![i]?.meal_coupons.isNullOrEmpty()) {
                        if (template.contains("MEAL_COUPON_LOOP")) {

                            template = template.replace(
                                "MEAL_COUPON_LOOP",
                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                    .replace("[", "")
                                    .replace("]", "").replace(",", "\n").replace(" ", "")
                            )
                        } else {
                            if (i > 0) {
                                template =
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons?.toString()
                                        ?.let {
                                            template.replace(
                                                it,
                                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                            )
                                        }!!
                            }
                        }

                        if (template.contains("MEAL_COUPON_NUMBER")) {
                            template = template.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                    .replace("[", "").replace("]", "")
                            )
                        } else {
                            if (i > 0) {
                                template = template.replace(
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons.toString()
                                        .replace("[", "").replace("]", ""),
                                    ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                        .replace("[", "").replace("]", "")
                                )
                            }
                        }

                        if (template.contains("MEAL_COUNT")) {
                            template = template.replace(
                                "MEAL_COUNT",
                                ticketData.passengerDetails!![i]?.meal_coupons?.size.toString()
                            )
                        } else {
                            if (i > 0) {
                                template = template.replace(
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons?.size.toString(),
                                    ticketData.passengerDetails!![i]?.meal_coupons?.size.toString()
                                )
                            }
                        }
                    } else {
                        template = template.replace(
                            "MEAL_COUPON_LOOP", ""
                        )
                    }


                    if (template.contains("NEW_LINE")) {
                        template = template.replace("NEW_LINE", " ")
                    }
                    if (template.contains("ONE_SPACE")) {
                        template = template.replace("ONE_SPACE", " ")
                    }

                    if(template.contains("ORIGIN")){
                        template = template.replace("ORIGIN",ticketData.origin)
                    }
                    if(template.contains("DESTINATION")){
                        template = template.replace("DESTINATION",ticketData.destination?:"")
                    }
                    if(template.contains("BOLD_ON")){
                        template = template.replace("BOLD_ON"," ")
                    }
                    if(template.contains("BOLD_OFF")){
                        template = template.replace("BOLD_OFF"," ")
                    }

                    template =
                        ticketData.serviceNumber?.let {
                            template.replace(
                                "SERVICE_NUMBER",
                                it
                            )
                        }!!

                    template = template.replace(
                        "PAID_AMOUNT",
                        ticketData.partialPaymentDetails?.paidAmount.toString()
                    )
                    template = template.replace(
                        "REMAINING_AMOUNT",
                        ticketData.partialPaymentDetails?.remainingAmount.toString()
                    )


                    template = ticketData.boardingDetails?.landmark?.let {
                        template.replace(
                            "LANDMARK",
                            it
                        )
                    } ?: "-"

                    template = template.replace(
                        "OPERATOR_NAME",
                        privilegeResponseModel.operatorName
                    )

                    template = template.replace(
                        "WEB_ADDRESS",
                        privilegeResponseModel.webAddressUrl
                    )

                    template = template.replace(
                        "ACCOUNT_HOLDER_NAME",
                        loginModelPref.userName
                    )
                    template =
                        ticketData.ticketNumber?.let {
                            template.replace(
                                "PNR_NUMBER",
                                it
                            )
                        }!!
                    template =
                        ticketData.ticketStatus?.let {
                            template.replace(
                                "TICKET_STATUS",
                                it
                            )
                        } ?: "-"
                    template =
                        template.replace("ORIGIN", ticketData.origin)
                    template =
                        ticketData.destination?.let {
                            template.replace(
                                "DESTINATION",
                                it
                            )
                        }!!
                    template = ticketData.boardingDetails?.depTime?.let {
                        template.replace(
                            "DEPARTURE_TIME",
                            it
                        )
                    }!!
                    template =
                        ticketData.travelDate?.let {
                            template.replace(
                                "TRAVEL_DATE",
                                "$it"
                            )
                        }!!
                    template = template.replace(
                        "TICKET_FARE",
                        "${privilegeResponseModel.currency} ${
                            ticketData.totalFare?.toString()?.toDouble()
                                ?.convert(currencyFormatt) ?: ""
                        }"
                    )
                    template = ticketData.passengerDetails?.get(i)?.name?.let {
                        template.replace(
                            "PASSENGER_NAME",
                            it
                        )
                    } ?: ""
                    template = ticketData.passengerDetails?.get(i)
                        ?.let {
                            it.mobile?.let { it1 ->
                                template.replace(
                                    "MOBILE_NUMBER",
                                    it1
                                )
                            }
                        } ?: ""
                    template =
                        ticketData.seatNumbers?.let {
                            template.replace(
                                "SEAT_NUMBERS",
                                it
                            )
                        }!!


                    template = ticketData.boardingDetails?.address?.let {
                        template.replace(
                            "BOARDING_POINT",
                            it
                        )
                    }!!
                    template = ticketData.dropOffDetails?.address?.let {
                        template.replace(
                            "DROPPING_POINT",
                            it
                        )
                    }!!
                    template = ticketData.boardingDetails?.contactPersons?.let {
                        template.replace(
                            "CONTACT_PERSON",
                            it
                        )
                    }!!

                    template = ticketData.boardingDetails?.contactNumbers?.let {
                        template.replace(
                            "CONTACT_NUMBER_PERSON",
                            it
                        )
                    }!!

                    template = ticketData.ticketLeadDetail?.ticketBookedBy?.let {
                        template.replace(
                            "TICKET_BOOKED_BY",
                            it.substringBefore(",")
                        )
                    }!!

                    template =
                        ticketData.busType?.let {
                            template.replace(
                                "COACH_TYPE",
                                it
                            )
                        }!!
                    template =
                        template.replace("REMARKS", ticketData.remarks ?: "-")
                    template = template.replace(
                        "TERMINAL_ID",
                        ticketData.terminalRefNo ?: ""
                    )
                    if (ticketData.terminalRefNo.isNullOrEmpty()) {
                        template =
                            template.replace("TERMINAL_PULOGABANG", "")
                    }
                    template =
                        template.replace("CURRENT_DATE", getTodayDate())
                    template =
                        template.replace("CURRENT_TIME", getTodayDateWithTime())

                    template = template.replace("TAB_SPACE", " ")

                    template = template.replace("BAR_CODE", " ")

                    template = "\n$template\n"
                    template.let { multiSeats.add(it) }


                    val temp = template.split("\n")
                    var isBold = false
                    if (withoutSpacePrint) {
                        for (i in temp) {
                            when {
                                i.contains("FOR_EACH_SEAT") -> {
                                    SunmiPrintHelper.getInstance()
                                        .printText(
                                            i.replace("FOR_EACH_SEAT", ""),
                                            24f,
                                            false,
                                            false,
                                            ""
                                        )
                                }

                                i.contains("ALIGN_CENTER|") -> {
                                    SunmiPrintHelper.getInstance().setAlign(1)
                                    SunmiPrintHelper.getInstance()
                                        .printText(i.substringAfter("|"), 24f, false, false, "")

                                }
                                i.contains("ALIGN_CENTER") -> {
                                    SunmiPrintHelper.getInstance().setAlign(1)
                                    SunmiPrintHelper.getInstance()
                                        .printText(i.substringAfter("R"), 24f, false, false, "")
                                }
                                i.contains("BOLD_ON") -> {
                                    isBold = true
                                }
                                i.contains("BOLD_OFF") -> {
                                    isBold = false

                                }
                                i.contains("IMAGE") -> {
                                    if (busLogo != null) {
//                                        SunmiPrintHelper.getInstance()
//                                            .printText("\n", 24f, true, false, "")
                                        SunmiPrintHelper.getInstance().printBitmap(busLogo, 1)
                                        SunmiPrintHelper.getInstance()
                                            .printText("\n", 24f, true, false, "")
                                    }
                                }
                                i.contains("ALIGN_LEFT|") -> {
                                    if (isBold) {
                                        SunmiPrintHelper.getInstance().setAlign(0)
                                        SunmiPrintHelper.getInstance()
                                            .printText(i.substringAfter("|"), 24f, true, false, "")
                                    } else {
                                        SunmiPrintHelper.getInstance().setAlign(0)
                                        SunmiPrintHelper.getInstance()
                                            .printText(i.substringAfter("|"), 24f, false, false, "")
                                    }

                                }
                                i.contains("BOARDING_QR") -> {
                                    SunmiPrintHelper.getInstance().printBitmap(TicketQRCode, 1)
                                    SunmiPrintHelper.getInstance().feedPaper()


                                }
                                else -> {
                                    if (isBold) {
                                        SunmiPrintHelper.getInstance().setAlign(0)
//                                        SunmiPrintHelper.getInstance()
//                                            .printText(i, 24f, true, false, "")
                                    } else {
                                        SunmiPrintHelper.getInstance().setAlign(0)
//                                        SunmiPrintHelper.getInstance()
//                                            .printText(i, 24f, false, false, "")
                                    }

                                }
                            }


                        }

                    } else {
                        for (i in temp) {
                            when {
                                i.contains("FOR_EACH_SEAT") -> {
                                    SunmiPrintHelper.getInstance()
                                        .printText(
                                            i.replace("FOR_EACH_SEAT", ""),
                                            24f,
                                            false,
                                            false,
                                            ""
                                        )
                                }

                                i.contains("ALIGN_CENTER|") -> {
                                    SunmiPrintHelper.getInstance().setAlign(1)
                                    SunmiPrintHelper.getInstance()
                                        .printText(
                                            i.substringAfter("|") + "\n",
                                            24f,
                                            false,
                                            false,
                                            ""
                                        )

                                }
                                i.contains("ALIGN_CENTER") -> {
                                    SunmiPrintHelper.getInstance().setAlign(1)
                                    SunmiPrintHelper.getInstance()
                                        .printText(
                                            i.substringAfter("R") + "\n",
                                            24f,
                                            false,
                                            false,
                                            ""
                                        )
                                }
                                i.contains("BOLD_ON") -> {
                                    isBold = true
                                }
                                i.contains("BOLD_OFF") -> {
                                    isBold = false
                                }
                                i.contains("IMAGE") -> {
                                    if (busLogo != null) {
//                                        SunmiPrintHelper.getInstance()
//                                            .printText("\n", 24f, true, false, "")
                                        SunmiPrintHelper.getInstance().printBitmap(busLogo, 1)
                                        SunmiPrintHelper.getInstance()
                                            .printText("\n", 24f, true, false, "")
                                    }
                                }
                                i.contains("ALIGN_LEFT|") -> {
                                    if (isBold) {
                                        SunmiPrintHelper.getInstance().setAlign(0)
                                        SunmiPrintHelper.getInstance()
                                            .printText(
                                                i.substringAfter("|") + "\n",
                                                24f,
                                                true,
                                                false,
                                                ""
                                            )
                                    } else {
                                        SunmiPrintHelper.getInstance().setAlign(0)
                                        SunmiPrintHelper.getInstance()
                                            .printText(
                                                i.substringAfter("|") + "\n",
                                                24f,
                                                false,
                                                false,
                                                ""
                                            )
                                    }

                                }
                                i.contains("BOARDING_QR") -> {
                                    SunmiPrintHelper.getInstance().printBitmap(TicketQRCode, 1)
                                    SunmiPrintHelper.getInstance()
                                        .printText("\n", 24f, true, false, "")


                                }
                                else -> {
                                    if (isBold) {
                                        SunmiPrintHelper.getInstance().setAlign(0)
//                                        SunmiPrintHelper.getInstance()
//                                            .printText(i + "\n", 24f, true, false, "")
                                    } else {
                                        SunmiPrintHelper.getInstance().setAlign(0)
//                                        SunmiPrintHelper.getInstance()
//                                            .printText(i + "\n", 24f, false, false, "")
                                    }

                                }
                            }


                        }

                    }
//                    if (i == ticketData.passengerDetails?.size?.minus(1)) {
                        SunmiPrintHelper.getInstance().feedPaper()
//                    }


                }


            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }


    }

    private fun multiplePrintPinelab() {
        var template = bluetoothPrintTemplate!!

        try {
            if (ticketData.passengerDetails != null && ticketData.passengerDetails?.isNotEmpty()!!) {
                printArray = JSONArray()

                val multiSeats = mutableListOf<String>()
                Timber.e("Passenger size : ${ticketData.passengerDetails!!.size}")
                for (i in 0..ticketData.passengerDetails?.size?.minus(1)!!) {
                    if (ticketData.passengerDetails?.size!! != 1) {
                        if (i < ticketData.passengerDetails?.size?.minus(1)!!) {
                            template = template.replace("[C]=", "")
                            template =
                                template.replace("cut here", "")?.trimEnd()!!
                            template =
                                template.replace("BOARDING_QR", "")?.trim()!!
                            template =
                                template.replace("BAR_CODE", "")?.trim()!!

                        } else {
                            template = template.replace("[C]=", "")
                            template =
                                template.replace("=", "")?.trimEnd()!!

                            if (originalTemplate?.contains("BOARDING_QR")!!)
                                template = "${template}\nBOARDING_QR"
                            if (originalTemplate?.contains("BAR_CODE")!!)
                                template = "${template}\n\nBAR_CODE"
                            template =
                                "${template}\nALIGN_CENTER|=======cut here======="
                        }
                    }

                    template =
                        template.replace("FOR_EACH_SEAT", "")
                    if (template.contains("SEAT_EACH_NUMBERS")) {
                        template = template.replace(
                            "SEAT_EACH_NUMBERS",
                            ticketData.passengerDetails!![i]?.seatNumber ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketData.passengerDetails!![i.minus(1)]?.seatNumber?.let {
                                    template.replace(
                                        it,
                                        ticketData.passengerDetails!![i]?.seatNumber ?: ""
                                    )
                                }!!
                        }
                    }
                    if (template.contains("PASSENGER_EACH_NAME")) {
                        template = template.replace(
                            "PASSENGER_EACH_NAME",
                            ticketData.passengerDetails!![i]?.name ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketData.passengerDetails!![i.minus(1)]?.name?.let {
                                    template.replace(
                                        it,
                                        ticketData.passengerDetails!![i]?.name ?: ""
                                    )
                                }!!
                        }
                    }


                    if (template.contains("TICKET_EACH_FARE")) {
                        template = template.replace(
                            "TICKET_EACH_FARE",
                            ticketData.passengerDetails!![i]?.netFare?.toDouble()
                                ?.convert(currencyFormatt) ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketData.passengerDetails!![i.minus(1)]?.netFare?.let {
                                    template.replace(
                                        it,
                                        ticketData.passengerDetails!![i]?.netFare?.toDouble()
                                            ?.convert(currencyFormatt) ?: ""
                                    )
                                }!!
                        }
                    }


                    if (!ticketData.passengerDetails!![i]?.meal_coupons.isNullOrEmpty()) {
                        if (template.contains("MEAL_COUPON_LOOP")) {

                            template = template.replace(
                                "MEAL_COUPON_LOOP",
                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                    .replace("[", "")
                                    .replace("]", "").replace(",", "\n").replace(" ", "")
                            )
                        } else {
                            if (i > 0) {
                                template =
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons?.toString()
                                        ?.let {
                                            template.replace(
                                                it,
                                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                            )
                                        }!!
                            }
                        }

                        if (template.contains("MEAL_COUPON_NUMBER")) {
                            template = template.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                    .replace("[", "").replace("]", "")
                            )
                        } else {
                            if (i > 0) {
                                template = template.replace(
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons.toString()
                                        .replace("[", "").replace("]", ""),
                                    ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                        .replace("[", "").replace("]", "")
                                )
                            }
                        }

                        if (template.contains("MEAL_COUNT")) {
                            template = template.replace(
                                "MEAL_COUNT",
                                ticketData.passengerDetails!![i]?.meal_coupons?.size.toString()
                            )
                        } else {
                            if (i > 0) {
                                template = template.replace(
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons?.size.toString(),
                                    ticketData.passengerDetails!![i]?.meal_coupons?.size.toString()
                                )
                            }
                        }
                    } else {
                        template = template.replace(
                            "MEAL_COUPON_LOOP", ""
                        )
                    }




                    template =
                        ticketData.serviceNumber?.let {
                            template.replace(
                                "SERVICE_NUMBER",
                                it
                            )
                        }!!

                    template = template.replace(
                        "PAID_AMOUNT",
                        ticketData.partialPaymentDetails?.paidAmount.toString()
                    )
                    template = template.replace(
                        "REMAINING_AMOUNT",
                        ticketData.partialPaymentDetails?.remainingAmount.toString()
                    )

                    if (template.contains("ORIGIN")) {
                        template = template.replace("ORIGIN", ticketData.origin)
                    }
                    if (template.contains("DESTINATION")) {
                        template = template.replace("DESTINATION", ticketData.destination!!)
                    }
                    if (template.contains("WEB_ADDRESS")) {
                        template = template.replace(
                            "WEB_ADDRESS",
                            privilegeResponseModel.webAddressUrl
                        )
                    }


                    if (template.contains("TICKET_FARE")) {
                        template = template.replace(
                            "TICKET_FARE",
                            "${privilegeResponseModel.currency} ${
                                ticketData.totalFare?.toString()?.toDouble()?.convert(currencyFormatt) ?: ""
                            }"
                        )
                    }



                    template = ticketData.boardingDetails?.landmark?.let {
                        template.replace(
                            "LANDMARK",
                            it
                        )
                    } ?: "-"

                    template = template.replace(
                        "OPERATOR_NAME",
                        privilegeResponseModel.operatorName
                    )

                    template = template.replace(
                        "WEB_ADDRESS",
                        privilegeResponseModel.webAddressUrl
                    )

                    template = template.replace(
                        "ACCOUNT_HOLDER_NAME",
                        loginModelPref.userName
                    )
                    template =
                        ticketData.ticketNumber?.let {
                            template.replace(
                                "PNR_NUMBER",
                                it
                            )
                        }!!
                    template =
                        ticketData.ticketStatus?.let {
                            template.replace(
                                "TICKET_STATUS",
                                it
                            )
                        } ?: "-"
                    template =
                        template.replace("ORIGIN", ticketData.origin)
                    template =
                        ticketData.destination?.let {
                            template.replace(
                                "DESTINATION",
                                it
                            )
                        }!!
                    template = ticketData.boardingDetails?.depTime?.let {
                        template.replace(
                            "DEPARTURE_TIME",
                            it
                        )
                    }!!
                    template =
                        ticketData.travelDate?.let {
                            template.replace(
                                "TRAVEL_DATE",
                                "$it"
                            )
                        }!!
                    template = template.replace(
                        "TICKET_FARE",
                        "${privilegeResponseModel.currency} ${
                            ticketData.totalFare?.toString()?.toDouble()
                                ?.convert(currencyFormatt) ?: ""
                        }"
                    )
                    template = ticketData.passengerDetails?.get(i)?.name?.let {
                        template.replace(
                            "PASSENGER_NAME",
                            it
                        )
                    } ?: ""
                    template = ticketData.passengerDetails?.get(i)
                        ?.let {
                            it.mobile?.let { it1 ->
                                template.replace(
                                    "MOBILE_NUMBER",
                                    it1
                                )
                            }
                        } ?: ""
                    template =
                        ticketData.seatNumbers?.let {
                            template.replace(
                                "SEAT_NUMBERS",
                                it
                            )
                        }!!


                    template = ticketData.boardingDetails?.address?.let {
                        template.replace(
                            "BOARDING_POINT",
                            it
                        )
                    }!!
                    template = ticketData.dropOffDetails?.address?.let {
                        template.replace(
                            "DROPPING_POINT",
                            it
                        )
                    }!!
                    template = ticketData.boardingDetails?.contactPersons?.let {
                        template.replace(
                            "CONTACT_PERSON",
                            it
                        )
                    }!!

                    template = ticketData.boardingDetails?.contactNumbers?.let {
                        template.replace(
                            "CONTACT_NUMBER_PERSON",
                            it
                        )
                    }!!

                    template = ticketData.ticketLeadDetail?.ticketBookedBy?.let {
                        template.replace(
                            "TICKET_BOOKED_BY",
                            it.substringBefore(",")
                        )
                    }!!

                    template =
                        ticketData.busType?.let {
                            template.replace(
                                "COACH_TYPE",
                                it
                            )
                        }!!
                    template =
                        template.replace("REMARKS", ticketData.remarks ?: "-")
                    template = template.replace(
                        "TERMINAL_ID",
                        ticketData.terminalRefNo ?: ""
                    )
                    if (ticketData.terminalRefNo.isNullOrEmpty()) {
                        template =
                            template.replace("TERMINAL_PULOGABANG", "")
                    }
                    template =
                        template.replace("CURRENT_DATE", getTodayDate())
                    template =
                        template.replace("CURRENT_TIME", getTodayDateWithTime())

                    template = template.replace("TAB_SPACE", " ")

                    template = template.replace("BAR_CODE", " ")

                    template = template.replace("BOLD_ON", " ")
                    template = template.replace("BOLD_OFF", " ")

                    template = "\n$template\n"
                    template.let { multiSeats.add(it) }

                    Timber.e("Template : $template")


                    val temp = template.split("\n")

                    for (i in temp) {
                        if (i.contains("ALIGN_LEFT")) {
                            val print = i.substringAfter("|")
                            createJson("0", false, print, "0", "0")
                        } else if (i.contains("ALIGN_CENTER") && i.contains("|")) {
                            val print = i.substringAfter("|")
                            createJson("0", true, print, "0", "0")
                        } else if (i.contains("ALIGN_CENTER")) {
                            val print = i.substringAfter("ALIGN_CENTER")
                            createJson("0", true, print, "0", "0")
                        } else if (i.contains("ALIGN_CENTER") && i.contains("|")) {
                            val print = i.substringAfter("|")
                            createJson("0", true, print, "0", "0")
                        } else if (i.contains("IMAGE")) {
                            if(hexvalue!!.isNotEmpty()){
                                createJson("2", true, "", "0", hexvalue!!)
                            }
                        } else {
                            val print = i
                            createJson("0", true, print, "0", "0")
                        }
                    }

                    // createJson("0",true,"Hello","0","0")

                }

                val headerObj = JSONObject()
                headerObj.put("ApplicationId", "be1dc81f1cd941f39afd7ccbb7d7f023")
                headerObj.put("UserId", "user1234")
                headerObj.put("MethodId", "1002")
                headerObj.put("VersionNo", "1.0")

                val detailObj = JSONObject()
                detailObj.put("PrintRefNo", ticketData.ticketNumber!!)
                detailObj.put("SavePrintData", true)
                detailObj.put("Data", printArray)

                val json = JSONObject()
                json.put("Header", headerObj)
                json.put("Detail", detailObj)

                val data = Bundle()
                data.putString(BILLING_REQUEST_TAG, json.toString())
                message.data = data
                try {
                    message.replyTo = Messenger(IncomingHandler(this))
                    mServerMessenger!!.send(message)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }


    }

    private fun generateQrcode() {
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // initializing a variable for default display.

        // initializing a variable for default display.
        val display = manager.defaultDisplay

        // creating a variable for point which
        // is to be displayed in QR Code.

        // creating a variable for point which
        // is to be displayed in QR Code.
        val point = Point()
        display.getSize(point)

        // getting width and
        // height of a point

        // getting width and
        // height of a point
        val width: Int = 200
        val height: Int = 250

        // generating dimension from width and height.

        // generating dimension from width and height.
        var dimen = if (width < height) width else height
        dimen = dimen * 2 / 2

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        val qrgEncoder = QRGEncoder(qrCodeInput, null, QRGContents.Type.TEXT, dimen)
        try {
            // getting our qrcode in the form of bitmap.
            var bitmap = qrgEncoder.getBitmap(0)
            TicketQRCode = bitmap
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
        } catch (e: WriterException) {
            // this method is called for
            // exception handling.
        }
    }


    private fun commonReplacementPrint() {
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("\\r\\n|\\r|\\n", "")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("/", "")
        // bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("-", "")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("|", "")
        //bluetoothPrintTemplate = bluetoothPrintTemplate.replace("INITIALIZE", commands.HARDWARE.HW_INIT + commands.TEXT_FORMAT.TXT_NORMAL + commands.TEXT_FORMAT.TXT_ALIGN_CT)
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("NEW_LINE", "\n")
        // bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("NEW_LINE", " ")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("TAB_SPACE", " ")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("ONE_SPACE", " ")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("BOLD_ON", "<b>")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("BOLD_OFF", "</b>")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("ALIGN_CENTER", "[C]")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("ALIGN_LEFT", "[L]")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("ALIGN_RIGHT", "[R]")
    }

    private fun multiSeatBluetoothPrint() {
        try {
            if (ticketData.passengerDetails != null && ticketData.passengerDetails?.isNotEmpty()!!) {
                val multiSeats = mutableListOf<String>()
                for (i in 0..ticketData.passengerDetails?.size?.minus(1)!!) {
                    /*if (i > 0) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("IMAGE", "\nIMAGE")
                    }*/

                    if (ticketData.passengerDetails?.size!! != 1) {
                        if (i < ticketData.passengerDetails?.size?.minus(1)!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("[C]=", "")
                            bluetoothPrintTemplate =
                                bluetoothPrintTemplate?.replace("cut here", "")?.trimEnd()
                        /*    bluetoothPrintTemplate =
                                bluetoothPrintTemplate?.replace("BOARDING_QR", "")?.trim()*/
                            bluetoothPrintTemplate =
                                bluetoothPrintTemplate?.replace("BAR_CODE", "")?.trim()

                        } else {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("[C]=", "")
                            bluetoothPrintTemplate =
                                bluetoothPrintTemplate?.replace("=", "")?.trimEnd()

                     /*       if (originalTemplate?.contains("BOARDING_QR")!!)
                                bluetoothPrintTemplate = "${bluetoothPrintTemplate}\nBOARDING_QR"*/
                            if (originalTemplate?.contains("BAR_CODE")!!)
                                bluetoothPrintTemplate = "${bluetoothPrintTemplate}\n\nBAR_CODE"
                            bluetoothPrintTemplate =
                                "${bluetoothPrintTemplate}\n[C]=======cut here======="
                        }
                    }

                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace("FOR_EACH_SEAT", "")
                    if (bluetoothPrintTemplate?.contains("SEAT_EACH_NUMBERS")!!) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "SEAT_EACH_NUMBERS",
                            " ${ticketData.passengerDetails!![i]?.seatNumber} "
                        )
                    } else {
                        if (i > 0) {
                            bluetoothPrintTemplate =
                                ticketData.passengerDetails!![i.minus(1)]?.seatNumber?.let {
                                    bluetoothPrintTemplate?.replace(
                                        " $it ",
                                        " ${ticketData.passengerDetails!![i]?.seatNumber} "
                                    )
                                }
                        }
                    }
                    if (bluetoothPrintTemplate?.contains("PASSENGER_EACH_NAME")!!) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "PASSENGER_EACH_NAME",
                            ticketData.passengerDetails!![i]?.name ?: ""
                        )
                    } else {
                        if (i > 0) {
                            bluetoothPrintTemplate =
                                ticketData.passengerDetails!![i.minus(1)]?.name?.let {
                                    bluetoothPrintTemplate?.replace(
                                        it,
                                        ticketData.passengerDetails!![i]?.name ?: ""
                                    )
                                }
                        }
                    }


                    if (bluetoothPrintTemplate?.contains("TICKET_EACH_FARE")!!) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "TICKET_EACH_FARE",
                            ticketData.passengerDetails!![i]?.netFare?.toDouble()?.convert(currencyFormatt)
                                ?: ""
                        )
                    } else {
                        if (i > 0) {
                            bluetoothPrintTemplate =
                                bluetoothPrintTemplate?.replace(
                                    "${
                                        ticketData.passengerDetails!![i]?.netFare?.toDouble()
                                            ?.convert(currencyFormatt)
                                    }",
                                    ticketData.passengerDetails!![i]?.netFare?.toDouble()
                                        ?.convert(currencyFormatt) ?: ""
                                )
                        }
                    }


                    if (!qrCodeInput.isNullOrEmpty()) {
                        if (!bluetoothPrintTemplate.isNullOrEmpty() && bluetoothPrintTemplate?.contains(
                                "BOARDING_QR"
                            )!!
                        ) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "BOARDING_QR", "[C]<qrcode size='25'>$qrCodeInput</qrcode>"
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate =
                                    bluetoothPrintTemplate?.replace(
                                        "$qrCodeInput\n[C]=======cut here=======",
                                        "[C]<qrcode size='25'>$qrCodeInput</qrcode>"
                                    )
                            }
                        }
                    }

                    if (!ticketData.passengerDetails!![i]?.meal_coupons.isNullOrEmpty()) {
                        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_LOOP")!!) {

                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_COUPON_LOOP",
                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                    .replace("[", "")
                                    .replace("]", "").replace(",", "\n").replace(" ", "")
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate =
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons?.toString()
                                        ?.let {
                                            bluetoothPrintTemplate?.replace(
                                                it,
                                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                            )
                                        }
                            }
                        }

                        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_NUMBER")!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                    .replace("[", "").replace("]", "")
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons.toString()
                                        .replace("[", "").replace("]", ""),
                                    ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                        .replace("[", "").replace("]", "")
                                )
                            }
                        }

                        if (bluetoothPrintTemplate?.contains("MEAL_COUNT")!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_COUNT",
                                ticketData.passengerDetails!![i]?.meal_coupons?.size.toString()
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons?.size.toString(),
                                    ticketData.passengerDetails!![i]?.meal_coupons?.size.toString()
                                )
                            }
                        }
                    } else {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "MEAL_COUPON_LOOP", ""
                        )
                    }

                    if (ticketData.passengerDetails!![i]?.selected_meal_type.toString()
                            .isNotEmpty() && ticketData.passengerDetails!![i]?.selected_meal_type.toString() != "-"
                    ) {
                        if (bluetoothPrintTemplate?.contains("MEAL_TYPE")!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_TYPE",
                                ticketData.passengerDetails!![i]?.selected_meal_type.toString()
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    ticketData.passengerDetails!![i.minus(1)]?.selected_meal_type.toString(),
                                    ticketData.passengerDetails!![i]?.selected_meal_type.toString()
                                )
                            }
                        }
                    } else {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "MEAL_TYPE",
                            "-"
                        )
                    }


                    if (!ticketData.insuranceTransDetails?.details.isNullOrEmpty()) {
                        if (bluetoothPrintTemplate?.contains("ALL_INSURANCE_NUMBERS")!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "ALL_INSURANCE_NUMBERS",
                                "${getString(R.string.seat)} (${getString(R.string.booking_code)}): ${ticketData.insuranceTransDetails?.details?.get(i)?.seat_no} (${ticketData.insuranceTransDetails?.details?.get(i)?.info?.booking_code}) ${
                                    getString(
                                        R.string.policy
                                    )
                                }: ${ticketData.insuranceTransDetails?.details?.get(i)?.info?.policy_number}"
                            )
                        } else {
                            val newPolicyNumber =
                                ticketData.insuranceTransDetails?.details?.get(i)?.info?.policy_number?.trim()
                            val newSeatNo = "${ticketData.insuranceTransDetails?.details?.get(i)?.seat_no}"
                            if (i > 0) {
                                val oldPolicyNumber =
                                    ticketData.insuranceTransDetails?.details?.get(i.minus(1))?.info?.policy_number?.trim()
                                val oldSeatNo =
                                    "${ticketData.insuranceTransDetails?.details?.get(i.minus(1))?.seat_no}"
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    oldValue = "(${ticketData.insuranceTransDetails?.details?.get(i.minus(1))?.info?.booking_code})",
                                    newValue = "(${ticketData.insuranceTransDetails?.details?.get(i)?.info?.booking_code})"
                                )

                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    oldPolicyNumber ?: "",
                                    newPolicyNumber ?: ""
                                )

                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replaceFirst(
                                    oldSeatNo,
                                    newSeatNo
                                )
                            }
                        }
                    }

                    bluetoothPrintTemplate =
                        ticketData.serviceNumber?.let {
                            bluetoothPrintTemplate?.replace(
                                "SERVICE_NUMBER",
                                it
                            )
                        }

                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "PAID_AMOUNT",
                        ticketData.partialPaymentDetails?.paidAmount.toString()
                    ) ?: "-"
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "REMAINING_AMOUNT",
                        ticketData.partialPaymentDetails?.remainingAmount.toString()
                    ) ?: "-"

                    getLandmarkPrint()
                    getOperatorNamePrint()
                    getWebAddressPrint()

                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "ACCOUNT_HOLDER_NAME",
                        loginModelPref.userName
                    )

                    bluetoothPrintTemplate =
                        ticketData.ticketStatus?.let {
                            bluetoothPrintTemplate?.replace(
                                "TICKET_STATUS",
                                it
                            )
                        } ?: "-"
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace("ORIGIN", ticketData.origin)
                    bluetoothPrintTemplate =
                        ticketData.destination?.let {
                            bluetoothPrintTemplate?.replace(
                                "DESTINATION",
                                it
                            )
                        }
                    bluetoothPrintTemplate = ticketData.boardingDetails?.depTime?.let {
                        bluetoothPrintTemplate?.replace(
                            "DEPARTURE_TIME",
                            it
                        )
                    }
                    bluetoothPrintTemplate =
                        ticketData.travelDate?.let {
                            bluetoothPrintTemplate?.replace(
                                "TRAVEL_DATE",
                                "$it"
                            )
                        }
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "TICKET_FARE",
                        "${privilegeResponseModel.currency} ${
                            ticketData.totalFare?.toString()?.toDouble()
                                ?.convert(currencyFormatt) ?: ""
                        }"
                    )
                    bluetoothPrintTemplate = ticketData.passengerDetails?.get(i)?.name?.let {
                        bluetoothPrintTemplate?.replace(
                            "PASSENGER_NAME",
                            it
                        )
                    } ?: ""
                    bluetoothPrintTemplate = ticketData.passengerDetails?.get(i)
                        ?.let {
                            it.mobile?.let { it1 ->
                                bluetoothPrintTemplate?.replace(
                                    "MOBILE_NUMBER",
                                    it1
                                )
                            }
                        } ?: ""
                    bluetoothPrintTemplate =
                        ticketData.seatNumbers?.let {
                            bluetoothPrintTemplate?.replace(
                                "SEAT_NUMBERS",
                                it
                            )
                        }


                    bluetoothPrintTemplate = ticketData.boardingDetails?.address?.let {
                        bluetoothPrintTemplate?.replace(
                            "BOARDING_POINT",
                            it
                        )
                    }
                    if (ticketData.dropOffDetails != null) {
                        bluetoothPrintTemplate = ticketData.dropOffDetails?.address?.let {
                            bluetoothPrintTemplate?.replace(
                                "DROPPING_POINT",
                                it
                            )
                        }
                    }
                    bluetoothPrintTemplate = ticketData.boardingDetails?.contactPersons?.let {
                        bluetoothPrintTemplate?.replace(
                            "CONTACT_PERSON",
                            it
                        )
                    }
                    getContactNumberPrint()
                    getTicketBookedByPrint()

                    bluetoothPrintTemplate =
                        ticketData.busType?.let {
                            bluetoothPrintTemplate?.replace(
                                "COACH_TYPE",
                                it
                            )
                        }
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace("REMARKS", ticketData.remarks ?: "-")
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "TERMINAL_ID",
                        ticketData.terminalRefNo ?: ""
                    )
                    if (ticketData.terminalRefNo.isNullOrEmpty()) {
                        bluetoothPrintTemplate =
                            bluetoothPrintTemplate?.replace("TERMINAL_PULOGABANG", "")
                    }
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace("CURRENT_DATE", getTodayDate())
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace("CURRENT_TIME", getTodayDateWithTime())

                    bluetoothPrintTemplate = "$bluetoothPrintTemplate"
                    bluetoothPrintTemplate?.let { multiSeats.add(it) }

                }
                Timber.d("multiSeats $multiSeats")
                bluetoothPrintTemplate =
                    multiSeats.toString().removePrefix("[").removeSuffix("]").replace(",", "")

                bluetoothPrintTemplate =
                    ticketData.ticketNumber?.let {
                        bluetoothPrintTemplate?.replace(
                            "PNR_NUMBER",
                            it
                        )
                    }

            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    private fun getOperatorNamePrint() {
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "OPERATOR_NAME",
            privilegeResponseModel.operatorName
        )
    }

    private fun getTicketBookedByPrint() {
        bluetoothPrintTemplate = ticketData.ticketLeadDetail?.ticketBookedBy?.let {
            bluetoothPrintTemplate?.replace(
                "TICKET_BOOKED_BY",
                it.substringBefore(",")
            )
        }
    }

    private fun getLandmarkPrint() {
        bluetoothPrintTemplate = ticketData.boardingDetails?.landmark?.let {
            bluetoothPrintTemplate?.replace(
                "LANDMARK",
                it
            )
        } ?: "-"
    }

    private fun singleSeatBluetoothPrint() {

        bluetoothPrintTemplate =
            ticketData.serviceNumber?.let {
                bluetoothPrintTemplate?.replace(
                    "SERVICE_NUMBER",
                    it
                )
            }

        if (ticketData.partialPaymentDetails?.paidAmount != null) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PAID_AMOUNT",
                ticketData.partialPaymentDetails?.paidAmount.toString().toDouble()
                    .convert(currencyFormat = currencyFormatt)
            ) ?: "-"
        } else {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PAID_AMOUNT", "-"
            )
        }

        if (ticketData.partialPaymentDetails?.remainingAmount != null) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "REMAINING_AMOUNT",
                ticketData.partialPaymentDetails?.remainingAmount.toString().toDouble()
                    .convert(currencyFormat = currencyFormatt)
            ) ?: "-"
        } else {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "REMAINING_AMOUNT", "-"
            )
        }

        getLandmarkPrint()
        getOperatorNamePrint()
        getWebAddressPrint()

        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("ACCOUNT_HOLDER_NAME", loginModelPref.userName)
        bluetoothPrintTemplate =
            ticketData.ticketNumber?.let {
                bluetoothPrintTemplate?.replace(
                    "PNR_NUMBER",
                    it
                )
            }
        bluetoothPrintTemplate =
            ticketData.ticketStatus?.let {
                bluetoothPrintTemplate?.replace(
                    "TICKET_STATUS",
                    it
                )
            } ?: "-"
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("ORIGIN", ticketData.origin)
        bluetoothPrintTemplate =
            ticketData.destination?.let {
                bluetoothPrintTemplate?.replace(
                    "DESTINATION",
                    it
                )
            }
        bluetoothPrintTemplate = ticketData.boardingDetails?.depTime?.let {
            bluetoothPrintTemplate?.replace(
                "DEPARTURE_TIME",
                it
            )
        }
        bluetoothPrintTemplate =
            ticketData.travelDate?.let {
                bluetoothPrintTemplate?.replace(
                    "TRAVEL_DATE",
                    it
                )
            }

        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "TICKET_EACH_FARE",
            "${privilegeResponseModel.currency} ${
                ticketData.totalFare?.toString()?.toDouble()?.convert(currencyFormatt) ?: ""
            }"
        )

        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "TICKET_FARE",
            "${privilegeResponseModel.currency} ${
                ticketData.totalFare?.toString()?.toDouble()?.convert(currencyFormatt) ?: ""
            }"
        )

        bluetoothPrintTemplate = ticketData.passengerDetails?.get(0)?.name?.let {
            bluetoothPrintTemplate?.replace(
                "PASSENGER_NAME",
                it
            )
        } ?: ""
        bluetoothPrintTemplate = ticketData.passengerDetails?.get(0)
            ?.let {
                it.mobile?.let { it1 ->
                    bluetoothPrintTemplate?.replace(
                        "MOBILE_NUMBER",
                        it1
                    )
                }
            } ?: ""

        if (bluetoothPrintTemplate?.contains("SEAT_EACH_NUMBERS")== true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "SEAT_EACH_NUMBERS",
                ticketData.seatNumbers ?: ""
            )
        }

        bluetoothPrintTemplate =
            ticketData.seatNumbers?.let {
                bluetoothPrintTemplate?.replace(
                    "SEAT_NUMBERS",
                    it
                )
            }

        bluetoothPrintTemplate = ticketData.boardingDetails?.address?.let {
            bluetoothPrintTemplate?.replace(
                "BOARDING_POINT",
                it
            )
        }

        if (ticketData.dropOffDetails != null) {
            bluetoothPrintTemplate = ticketData.dropOffDetails?.address?.let {
                bluetoothPrintTemplate?.replace(
                    "DROPPING_POINT",
                    it
                )
            }
        }
        bluetoothPrintTemplate = ticketData.boardingDetails?.contactPersons?.let {
            bluetoothPrintTemplate?.replace(
                "CONTACT_PERSON",
                it
            )
        }
        getContactNumberPrint()
        getTicketBookedByPrint()
        bluetoothPrintTemplate =
            ticketData.busType?.let { bluetoothPrintTemplate?.replace("COACH_TYPE", it) }
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("REMARKS", ticketData.remarks ?: "-")
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("TERMINAL_ID", ticketData.terminalRefNo ?: "")
        if (ticketData.terminalRefNo.isNullOrEmpty()) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("TERMINAL_PULOGABANG", "")
        }
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("CURRENT_DATE", getTodayDate())

        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("CURRENT_TIME", getTodayDateWithTime())


        if (bluetoothPrintTemplate?.contains("PASSENGER_EACH_NAME") == true ) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PASSENGER_EACH_NAME",
                ticketData.passengerDetails!![0]?.name ?: ""
            )
        }

        if (bluetoothPrintTemplate?.contains("SEAT_EACH_NUMBER") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "SEAT_EACH_NUMBER",
                ticketData.passengerDetails!![0]?.seatNumber ?: ""
            )
        }

        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_NUMBER") == true) {
            bluetoothPrintTemplate =
                if (ticketData.passengerDetails != null && ticketData.passengerDetails!!.isNotEmpty()) {
                    var mealCoupons = ""
                    ticketData.passengerDetails?.forEach {
                        mealCoupons += it?.meal_coupons.toString()
                            .replace("[", "")
                            .replace("]", "").replace(",", "\n").replace(" ", "")
                        mealCoupons += "\n"
                    }
                    bluetoothPrintTemplate?.replace(
                        "MEAL_COUPON_NUMBER",
                        mealCoupons
                    )
                } else {
                    bluetoothPrintTemplate?.replace(
                        "MEAL_COUPON_NUMBER",
                        "-"
                    )
                }

        }

        if (bluetoothPrintTemplate?.contains("MEAL_COUNT") == true && !ticketData.passengerDetails.isNullOrEmpty()) {
            var mealCouponCount = 0
            ticketData.passengerDetails?.forEach {
                mealCouponCount += it?.meal_coupons?.size!!
            }
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "MEAL_COUNT",
                mealCouponCount.toString()
            )
        }

        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_LOOP") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "MEAL_COUPON_LOOP",
                ""
            )
        }

        if (bluetoothPrintTemplate?.contains("MEAL_TYPE") == true) {
            bluetoothPrintTemplate =
                if (ticketData.passengerDetails != null && ticketData.passengerDetails!!.isNotEmpty() && ticketData.passengerDetails!!.any { it?.selected_meal_type != "-" }) {
                    var mealTypes = ""
                    ticketData.passengerDetails?.forEach {
                        mealTypes += it?.selected_meal_type.toString()
                        mealTypes += ","
                    }
                    bluetoothPrintTemplate?.replace(
                        "MEAL_TYPE",
                        mealTypes.removeSuffix(",")
                    )
                } else {
                    bluetoothPrintTemplate?.replace(
                        "MEAL_TYPE",
                        "-"
                    )
                }

        }
        if (bluetoothPrintTemplate?.contains("ALL_INSURANCE_NUMBERS") == true && ticketData.insuranceTransDetails?.details != null) {
            var allInsuranceNumbers = ""
            ticketData.insuranceTransDetails?.details?.forEach {
                allInsuranceNumbers += "${getString(R.string.seat)} (${getString(R.string.booking_code)}): ${it.seat_no} (${it.info.booking_code}) ${
                    getString(
                        R.string.policy
                    )
                }: ${it.info.policy_number}\n"
            }
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "ALL_INSURANCE_NUMBERS",
                allInsuranceNumbers
            )
        }

        if (!bluetoothPrintTemplate.isNullOrEmpty() && bluetoothPrintTemplate?.contains("BOARDING_QR")!! && !qrCodeInput.isNullOrEmpty()) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "BOARDING_QR", "[C]<qrcode size='25'>$qrCodeInput</qrcode>"
            )
        } else {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "BOARDING_QR", ""
            )
        }
    }

    private fun getWebAddressPrint() {
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "WEB_ADDRESS",
            privilegeResponseModel.webAddressUrl
        )
    }

    private fun getContactNumberPrint() {
        bluetoothPrintTemplate = ticketData.boardingDetails?.contactNumbers?.let {
            bluetoothPrintTemplate?.replace(
                "CONTACT_NUMBER_PERSON",
                it
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun browseBluetoothDevice() {
        try {
            val bluetoothDevicesList = BluetoothPrintersConnections().list
            if (bluetoothDevicesList != null) {
                val items = arrayOfNulls<String>(bluetoothDevicesList.size)
                var i = 0
                for (device in bluetoothDevicesList) {
                    items[i++] = device.device.name
                }

                val savedBluetoothPrinter = PreferenceUtils.getPreference(PREF_BLUETOOTH_DEVICE,"")
                if (items.contains(savedBluetoothPrinter)){
                   Handler().postDelayed(Runnable {
                       val savedPrinterIndex = bluetoothDevicesList.indexOfFirst { it.device.name == savedBluetoothPrinter }
                       if (savedPrinterIndex != -1) {
                           selectedDevice = bluetoothDevicesList[savedPrinterIndex]
                           //privilegeResponseModel?.availableAppModes?.allowReprint = true
                           if (isFirstPrint)
                               printBluetooth()
                           else {
                               if (::privilegeResponseModel.isInitialized && privilegeResponseModel.availableAppModes?.allowReprint == true) {
                                   printBluetooth()
                               } else
                                   toast(getString(R.string.not_allowed_to_reprint))
                           }
                       }
                   },1000)
                }else {
                    if (items.isNotEmpty()) {
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.setTitle(getString(R.string.bluetooth_printer_selection))
                        alertDialog.setItems(
                            items
                        ) { dialogInterface, i ->
                            val index = i
                            if (index == -1) {
                                selectedDevice = null
                            } else {
                                selectedDevice = bluetoothDevicesList[index]
                                //privilegeResponseModel?.availableAppModes?.allowReprint = true
                                if (isFirstPrint)
                                    printBluetooth()
                                else {
                                    if (::privilegeResponseModel.isInitialized && privilegeResponseModel.availableAppModes?.allowReprint == true) {
                                        printBluetooth()
                                    } else
                                        toast(getString(R.string.not_allowed_to_reprint))
                                }
                            }
                        }

                        val alert = alertDialog.create()
                        alert.setCanceledOnTouchOutside(true)
                        alert.show()
                    } else
                        toast(getString(R.string.no_paired_devices))
                }
            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }


    @SuppressLint("MissingPermission")
    private fun enableDeviceBluetooth() {
        try {
            val bluetoothManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.getSystemService(BluetoothManager::class.java)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getSystemService(BluetoothManager::class.java)
                } else {
                    ContextCompat.getSystemService(this, BluetoothManager::class.java)
                }
            }
            mBluetoothAdapter = bluetoothManager?.adapter
            if (mBluetoothAdapter != null) {
                if (!mBluetoothAdapter?.isEnabled!!) {
                    val enableBtIntent = Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE
                    )
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                } else {
                    browseBluetoothDevice()
                }
            }
        } catch (e: Exception) {
//            handle
        }
    }


    @Deprecated("Deprecated in Java")
    @SuppressLint("LongLogTag", "MissingPermission")
    override fun onActivityResult(
        mRequestCode: Int, mResultCode: Int,
        mDataIntent: Intent?
    ) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent)
        when (mRequestCode) {
            REQUEST_ENABLE_BT -> if (mResultCode == Activity.RESULT_OK) {
                browseBluetoothDevice()
            } else {
                toast(getString(R.string.device_not_connected))
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PERMISSION_BLUETOOTH_ADMIN
            )
        } else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    PERMISSION_BLUETOOTH_CONNECT
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                    PERMISSION_BLUETOOTH_SCAN
                )
            } else
                enableDeviceBluetooth()
    }

    private fun printBluetooth() {
        isFirstPrint = false
        AsyncBluetoothEscPosPrint(
            this,
            object : AsyncEscPosPrint.OnPrintFinished() {
                override fun onError(
                    asyncEscPosPrinter: AsyncEscPosPrinter?,
                    codeException: Int
                ) {
                    Timber.d(
                        "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                    )
                }

                override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                    if (ActivityCompat.checkSelfPermission(
                            this@TicketDetailsActivity,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    PreferenceUtils.setPreference(PREF_BLUETOOTH_DEVICE,selectedDevice?.device?.name)
                    Timber.d(
                        "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                    )
                }
            }
        )
            .execute(this.getAsyncEscPosPrinter(selectedDevice))
    }

    private fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        if (bmpLogo != null) {
            hexaDecimalString = PrinterTextParserImg.bitmapToHexadecimalString(
                printer,
                bmpLogo
            )

            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "IMAGE", "[C]<img>${
                    hexaDecimalString
                }</img>"
            )
        }







        if (bmpQrCode != null) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "BOARDING_QR", "[C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        bmpQrCode
                    )
                }</img>"
            )
        }

        if (insuranceBitmap != null && ::ticketData.isInitialized && ticketData.insuranceTransDetails?.details?.isNotEmpty() == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "INSURANCE_QRCODE", "[C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        insuranceBitmap
                    )
                }</img>"
            )
        } else {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "INSURANCE_QRCODE", ""
            )
        }

        getBarCodePrint(printer)
        return printer.addTextToPrint(
            bluetoothPrintTemplate?.trim()
        )
    }

    private fun getBarCodePrint(printer: AsyncEscPosPrinter) {
        if (generateBarCode() != null && bluetoothPrintTemplate?.contains("BAR_CODE") == true && isAllowToPrintBarcode && barcodeValue != null) {
            hexaDecimalString = PrinterTextParserImg.bitmapToHexadecimalString(
                printer,
                generateBarCode()
            )

            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "BAR_CODE", "[C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        generateBarCode()
                    )
                }</img>"
            )
        } else {
            bluetoothPrintTemplate?.replace(
                "BAR_CODE", ""
            )
        }
    }

    private fun generateBarCode(): Bitmap? {
        try {
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.encodeBitmap("$barcodeValue", BarcodeFormat.CODE_128, 40, 80)
        } catch (e: java.lang.Exception) {
            Timber.d("exceptionMsg ${e.message} ")
        }
        return null
    }


    private fun setReleaseTicketPassengerAdapter() {
        _sheetReleaseTicketsBinding.rvPassengers.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        _sheetReleaseTicketsBinding.rvPassengers.adapter = ReleaseTicketPassengersListAdapter(
            this,
            passengerDetailList,
            this
        )
    }

    @SuppressLint("SetTextI18n")
    private fun callTicketDetailsApi() {
        val bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
//        binding.includeProgress.progressBar.visible()
        var numeric = false
        if (intent.hasExtra("qrscan")) {
            numeric = intent.getBooleanExtra("qrscan", false)
        } else {
            numeric = false
        }

        try {
            parseDouble(ticketNumber.toString())
        } catch (e: NumberFormatException) {
            Timber.d(" numeric: $numeric")

//            numeric = true
        }

        Timber.d("ticketNumber : ${ticketNumber}, numeric: $numeric")

        val reqBody = ticketNumber?.trim()?.let {
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

        /*    if (ticketDetailsRequest != null) {
                ticketDetailsViewModel.ticketDetailsApi(
                    loginModelPref.auth_token,
                    loginModelPref.api_key,
                    ticketDetailsRequest,
                    ticket_details_method_name
                )
            }*/

        ticketDetailsViewModel.ticketDetailsApi(
            loginModelPref.api_key,
            ticketNumber.toString(),
            true,
            false, locale!!,
            ticket_details_method_name
        )
    }


    private fun getBitmapFromURL(image: String, imageType: String) {
        val urlImage = URL(image)
        val result: Deferred<Bitmap?> = GlobalScope.async {
            urlImage.toBitmap()
        }

        GlobalScope.launch(Dispatchers.Main) {
            if (imageType == getString(R.string.logo))
                bmpLogo = result.await()
            if (imageType == getString(R.string.qr_code))
                bmpQrCode = result.await()
            if (imageType == getString(R.string.insurance_bitmap))
                insuranceBitmap = result.await()
        }


    }

    @SuppressLint("SetTextI18n")
    private fun setTicketDetailsObserver() {
        ticketDetailsViewModel.dataTicketDetails.observe(this) {
            if (it != null) {
                binding.swipeRefreshLayout.isRefreshing = false

                when {
                    it.code == 200 && it.success -> {
                        stopShimmerEffect()

                        ticketData = it.body

                        if(!ticketData.releaseDatetime.isNullOrEmpty()){
                            binding.notePhoneBlocked.text =
                                getString(R.string.note_this_is_not_a_confirmed_ticket_this_ticket_is_valid_till_manual_release_or_autorelease_on_at) + ticketData.releaseDatetime + getString(
                                    R.string.collect_full_payment_and_confirm_the_ticket
                                )
                        }
                        if (ticketData.isPayAtBusTicket != null && ticketData.isPayAtBusTicket) {
                            isPayAtBus = true

                            binding.tvCurrentHeader.visible()
                            binding.tvCurrentHeader.text = getString(R.string.pay_bus)
                            if (ticketData.ticketStatus.equals(getString(R.string.pending))) {
                                // binding.confirmBookingBtn.visible()
                                binding.confirmBookingBtn.text = getString(R.string.confirm)
                                // binding.releaseTicketBtn.visible()
                            }
                        } else {
                            isPayAtBus = false
                        }
                        ticketDetails(it)
                        finalFareAmount =
                            if (it.body.totalFare != null) it.body.totalFare.toString() else ""
                        if (::privilegeResponseModel.isInitialized && privilegeResponseModel.allowBluetoothPrint == true && !ticketData.ticketStatus.equals(
                                "PENDING",
                                true
                            )
                        ) {

                            binding.btnPrint.visible()
                            /*if(privilegeResponseModel?.country.equals("India", true)) {
                                binding.btnPrint.visible()
                            }else{
                                if(!checkInsurance(ticketData.insuranceTransDetails) && privilegeResponseModel?.country.equals("Indonesia", true)) {
                                    binding.btnPrint.visible()
                                }
                            }*/
                        } else {
                            binding.btnPrint.gone()
                        }
                        passengerDetailList = it.body.passengerDetails
                        bluetoothPrintTemplate = it.body.bluetoothPrintTemplate
                        originalTemplate = it.body.bluetoothPrintTemplate

                        firebaseLogEvent(
                            this,
                            TICKET_BOOKED_SUCCESSFULLY,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            TICKET_BOOKED_SUCCESSFULLY,
                            TicketBookedSuccessfully.TICKET_BOOKED_SUCCESSFULLY
                        )
                    }
                    it.code == 401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
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
                    }
                    else -> {
                        if (it.message?.isNotEmpty() == true) {
                            it.message.let { it1 -> toast(it1) }
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
                            TicketBookedFailed.TICKET_BOOKED_FAILED
                        )
                    }
                }
            } else {
                toast(getString(R.string.server_error))
                onBackPressed()
            }
        }
    }

    private fun checkInsurance(insuranceTransDetails: Insurance): Boolean {
        return insuranceTransDetails.partnerTransId
    }

    @SuppressLint("SetTextI18n")
    private fun ticketDetails(ticketDetailsModel: TicketDetailsModel) {
        val res = ticketDetailsModel.body

        passengerList.clear()
        passengerDetailList?.clear()
        seatList.clear()
        selectedSeatDetails.clear()
        tempTicket = res.ticketNumber ?: ""
        qrCodeInput = res.qrCodeData
        isAllowToPrintBarcode = res.isAllowToPrintBarcode
        barcodeValue = res.barcodeValue

        Timber.d("status ticket${res.ticketStatus}")

        if (res.code == 400) {
            /*DialogUtils.unAuthorizedDialog(
                this,
                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                this
            )*/
            showUnauthorisedDialog()

        } else {
            Timber.d("Language value check : ${res.ticketStatus}")
            when (res.ticketStatus) {
                getString(R.string.booked) -> {
                    loadTicketDetails()
                }
                getString(R.string.cancelled) -> {
                    loadCancelledTicket()
                }
                getString(R.string.phone_blocked) -> {
                    loadPartiallyCancelled()
                }
                getString(R.string.pending) -> {
                    loadUnconfirmedTicket()
                }
                getString(R.string.seat_booked) -> {
                    loadTicketDetails()
                }
                else -> {
                    loadTicketDetails()
                }
            }

            if (::ticketData.isInitialized && ticketData.passengerDetails != null && ticketData.passengerDetails?.isNotEmpty()!!
            ) {
                val mealCouponList = mutableListOf<String>()
                var mealCoupons = ""

                val mealTypeList = mutableListOf<String>()
                var mealTypes = ""
                ticketData.passengerDetails?.forEach {
                    if (it?.meal_coupons != null && it.meal_coupons.isNotEmpty()) {
                        mealCoupons += it.meal_coupons.toString().replace("[", "").replace("]", "")
                            .replace(",", "\n").replace(" ", "")
                        mealCouponList.add(mealCoupons)
                        mealCoupons = ""
                    }

                    if (!it?.selected_meal_type.isNullOrEmpty() && it?.selected_meal_type != "-") {
                        mealTypes += it?.selected_meal_type
                        mealTypeList.add(mealTypes)
                        mealTypes = ""
                    }
                }
                if (mealCouponList.isNotEmpty()) {
                    binding.cardMealCoupons.visible()
                    setMealsAdapter(mealCouponList)
                }
                if (mealTypeList.isNotEmpty()) {
                    binding.cardMealTypes.visible()
                    setMealTypeAdapter(mealTypeList)
                }
            } else {
                binding.cardMealCoupons.gone()
                binding.cardMealTypes.gone()
            }

            if (res.passengerDetails != null) {
                for (i in 0..res.passengerDetails.size.minus(1)) {
                    isShiftPassenger = res.passengerDetails[i]!!.canShiftTicket
                    if (res.insuranceTransDetails?.details != null) {
                        val insuranceList = res.insuranceTransDetails.details
                        if (insuranceList.isEmpty()) {
                            isInsurance = true
                        }
                    }
                    isCanCancelTicket = res.passengerDetails[i]!!.canCancel
                    seatList.clear()
                    selectedSeatDetails.clear()

                    passengerContactDetailList.add(
                        ContactDetail(
                            "${res.passengerDetails[i]?.mobile}",
                            "${res.passengerDetails[i]?.mobile}",
                            "${res.passengerDetails[i]?.email}"
                        )
                    )
                    passengerList.add(
                        PassengerDetailsResult(
                            expand = true,
                            isPrimary = true,
                            seatNumber = res.passengerDetails[i]!!.seatNumber,
                            name = res.passengerDetails[i]!!.name,
                            age = res.passengerDetails[i]?.age.toString(),
                            sex = res.passengerDetails[i]!!.gender,
                            contactDetail = passengerContactDetailList,
                            fare = res.passengerDetails[i]?.netFare,
                            meal_coupons = res.passengerDetails[i]?.meal_coupons ?: mutableListOf(),
                            mealRequired = res.passengerDetails[i]?.meal_required!!,
                            selectedMealType = res.passengerDetails[i]?.selected_meal_type
                        )
                    )

                    for (j in 0 until passengerList.size) {
                        val seatDetail = SeatDetail()
                        seatDetail.isPrimary = true
                        seatDetail.number = res.passengerDetails[j]?.seatNumber ?: ""
                        seatDetail.sex = res.passengerDetails[j]?.gender
                        seatDetail.name = res.passengerDetails[j]?.name
                        seatDetail.age = res.passengerDetails[j]?.age?.toString()
                        seatDetail.fare = res.passengerDetails[i]?.netFare
                        selectedSeatDetails.add(seatDetail)
                    }

                    setSelectedPassengers(passengers = passengerList)

                    val seatDetailNew = SeatDetail()
                    seatDetailNew.number = res.seatNumbers ?: ""
                    seatList.add(seatDetailNew)
                    setSelectSeats(seatList)
                }
                setSelectSeats(selectedSeatDetail = selectedSeatDetails)
            }

            try {
                /* val travelYear = res.travelDate!!.split("-")[2]
                 val traveldate = res.travelDate.split("-")[0]
                 val travelmonth = res.travelDate.split("-")[1]*/
                totalNoOfSeats = res.noOfSeats
                travelDate = "${res.travelDate}"

                reservationId = res.reservationId
                sourceId = res.originId.toString()
                destinationId = res.destinationId.toString()
                pnr = res.ticketNumber
                bookedBy = res.ticketLeadDetail?.ticketBookedBy?.split(",")?.get(0)
                    ?: getString(R.string.notAvailable)
                bookedAt = res.bookedAt ?: getString(R.string.notAvailable)
//            bookedAt = res.ticketLeadDetail?.ticketBookedBy?.split(",")?.get(1) ?: getString(R.string.notAvailable)
                boarding = res.origin
                bAddress = res.boardingDetails?.stageName ?: res.boardingDetails?.address
                        ?: getString(R.string.notAvailable)
                dropping = res.destination ?: getString(R.string.notAvailable)
                dAddress = res.dropOffDetails?.stageName ?: getString(R.string.notAvailable)
                passName =
                    res.passengerDetails?.get(0)?.name ?: getString(R.string.notAvailable)
                age = (res.passengerDetails?.get(0)?.age
                    ?: getString(R.string.notAvailable)).toString()
                gender =
                    res.passengerDetails?.get(0)?.gender ?: getString(R.string.notAvailable)
                if (gender.equals("M", true)) {
                    gender = getString(R.string.genderM)
                } else if (gender.equals("F", true)) {
                    gender = getString(R.string.genderF)
                }
                fare = (res.totalFare ?: getString(R.string.notAvailable)).toString()
                refundAmountFare =
                    (res.refundAmount ?: getString(R.string.notAvailable)).toString()
                cancellationCharges = res.cancellationCharges ?: 0.0
                if (res.ticketStatus?.equals(getString(R.string.status_cancelled), true) == true) {
                    showRefundAmount()
                }

                coach = res.coachNumber ?: getString(R.string.notAvailable)
                busType = res.busType ?: getString(R.string.notAvailable)

                boardingTravelDate =
                    res.boardingDetails?.travelDate ?: getString(R.string.notAvailable)
                boardingDepTime =
                    res.boardingDetails?.depTime ?: getString(R.string.notAvailable)
                dropOffTravelDate =
                    res.dropOffDetails?.travelDate ?: getString(R.string.notAvailable)
                dropOffDepTime = res.dropOffDetails?.arrTime ?: getString(R.string.notAvailable)
                boardingContactNumbers =
                    res.boardingDetails?.contactNumbers ?: getString(R.string.notAvailable)
                seatNumbers = res.seatNumbers ?: getString(R.string.notAvailable)
                serviceNumber = res.serviceNumber ?: getString(R.string.notAvailable)
                passengerMobile =
                    res.passengerDetails?.get(0)?.mobile ?: getString(R.string.notAvailable)
                shareTextWhatsapp = "${res.smsTicketHash} \n \n${res.sharingPdfLink}"
                boardingStageID = res.boardingDetails?.stageId.toString()
                boardingStageID = res.dropOffDetails?.stageId.toString()
                serviceTextAmount = res.serviceTaxAmount!!
                bookingSource = res.booking_source
                if (bookingSource.isNotEmpty()) {
                    if (isPayAtBus) {
                        binding.tvCurrentHeader.visible()
                        binding.tvCurrentHeader.text = getString(R.string.pay_bus)
                    } else {
                        binding.tvCurrentHeader.visible()
                        binding.tvCurrentHeader.text = bookingSource
                    }
                } else {
                    binding.tvCurrentHeader.gone()
                }
            } catch (e: Exception) {
                Timber.d("$e")
            }

            PreferenceUtils.apply {
                setPreference(PREF_BOARDING_TIME, boardingDepTime)
                setPreference(PREF_BOARDING_TIME, boardingDepTime)
                setPreference(PREF_BOARDING_AT, bAddress)
                setPreference(PREF_BOARDING_DATE, boardingTravelDate)
                setPreference(PREF_DROP_OFF_TIME, dropOffDepTime)
                setPreference(PREF_DROP_OFF, dAddress)
                setPreference(PREF_DROP_OFF_DATE, dropOffTravelDate)
            }

            if (passengerMobile?.contains("*") == true) {
                isMasking = true
            }

            if (isMasking == true) {
                binding.callPhoneNumber.gone()
            } else {
                if(!IS_PINELAB_DEVICE){
                    binding.callPhoneNumber.visible()
                }
            }

            PreferenceUtils.apply {
                putString("SHIFT_servicename", serviceNumber)
                putString("oldServiceNumberShiftACTIVITY", "${serviceNumber}?${res.travelDate}")
                putString("SHIFT_Traveldate", travelDate)
                putString("SHIFT_originId", sourceId)
                putString("SHIFT_destinationId", destinationId)
                putString("TicketDetail_noOfSeats", totalNoOfSeats.toString())
                putString("SHIFT_noOfSeats", totalNoOfSeats.toString())
                putString("TicketDetail_SeatNumbes", seatNumbers)
                putString("SHIFT_SeatNumbes", seatNumbers)
                putString("SHIFT_SeatPnrNumber", pnr)
                putString("SHIFT_origin_destination", "$boarding - $dropping")
            }

            if (pnr == "N/A") {
                binding.apply {
                    pnrText.text = getString(R.string.invalid_pnr_number)
                    pnrText.setTextColor(
                        ContextCompat.getColor(
                            this@TicketDetailsActivity,
                            R.color.colorRed2
                        )
                    )
                    imgTitle.gone()
                    titleMain.gone()
                    cardView2.gone()
                    constraintLayout6.gone()
                    cardView3.gone()
                    cardView4.gone()
                    cardView5.gone()
                }
                toast(getString(R.string.invalid_pnr_number))
            } else {
                binding.travelDate.text = "${getString(R.string.travel_date_colon)} $travelDate"

                if (serviceTextAmount > 0.0) {
                    if (fare?.contains(getString(R.string.rupess_symble)) == true)
                        binding.tvFare.text = "$fare ${getString(R.string.inc_gst)}"
                    else
                        binding.tvFare.text =
                            "$amountCurrency ${(fare?.toDouble())?.convert(currencyFormatt)} ${
                                getString(R.string.inc_gst)
                            }"
                } else {
                    if (fare?.contains(getString(R.string.rupess_symble)) == true)
                        binding.tvFare.text = "$fare"
                    else
                        binding.tvFare.text =
                            "$amountCurrency ${(fare?.toDouble())?.convert(currencyFormatt)}"
                }

                binding.apply {
                    pnrText.text = getString(R.string.pnr) + " - $pnr"
                    seatNumbers = res.seatNumbers ?: getString(R.string.notAvailable)

                    when (res.ticketStatus) {
                        getString(R.string.status_cancelled) -> {
                            noteAgent.text = ""
                            tvSeats.setPadding(0, 100, 0, 0)
                        }
                        else -> {
//                        if (bookedAt.isNullOrEmpty()) {
//                            noteAgent.text = "Issued by $bookedBy"
//                        } else {
//                            noteAgent.text = "Issued on $bookedAt by $bookedBy"
//                        }
                            noteAgent.text =
                                "${getString(R.string.issuedBy)} ${res.ticketLeadDetail?.ticketBookedBy}"
                            if (res.isEticket) {
                                if (isPayAtBus) {
                                    binding.tvCurrentHeader.text = getString(R.string.pay_bus)
                                } else {
                                    noteAgent.text = ""
                                    binding.tvCurrentHeader.text = getString(R.string.e_booking)
                                }

                            }
                        }
                    }
                    tvBoardingDate.text = boardingTravelDate
                    tvDroppingDate.text = dropOffTravelDate
                    tvBoardingTime.text = boardingDepTime
                    tvDroppingTime.text = dropOffDepTime
                    tvBoadringPoint.text = "$bAddress,\n$boarding"
                    tvDropOffPoint.text = "$dAddress,\n$dropping"
                    tvPassName.text = "$passName ($age)"
                    tvGender.text = gender
                    tvPhoneNum.text = passengerMobile
                    tvBpContact.text = boardingContactNumbers
                    tvSeats.text = seatNumbers
                    tvVehicleDetails.text = "$coach | $busType"
                    tvServiceNo.text = "$serviceNumber"

                    if (coach.isNullOrEmpty() && busType.isNullOrEmpty()) {
                        tvVehicleDetailsLabel.gone()
                        tvVehicleDetails.gone()
                    } else {
                        tvVehicleDetailsLabel.visible()
                        tvVehicleDetails.visible()
                    }

                    if (serviceNumber.isNullOrEmpty()) {
                        serviceNoLabel.gone()
                        tvServiceNo.gone()
                    } else {
                        serviceNoLabel.visible()
                        tvServiceNo.visible()
                    }

                    if (coach.isNullOrEmpty()
                        && busType.isNullOrEmpty()
                        && serviceNumber.isNullOrEmpty()
                    ) {
                        cardView4.gone()
                    } else {
                        cardView4.visible()
                    }

                    if (gender == getString(R.string.notAvailable)) {
                        genderHeading.gone()
                        tvGender.gone()
                    } else {
                        genderHeading.visible()
                        tvGender.visible()
                    }

                    if (isPassengerMobileEmpty(passengerMobile.toString())) {
                        tvPhoneNumLabel.gone()
                        tvPhoneNum.gone()
                        callPhoneNumber.gone()
                    } else {
                        tvPhoneNumLabel.visible()
                        tvPhoneNum.visible()
                        if(!IS_PINELAB_DEVICE){
                            binding.callPhoneNumber.visible()
                        }
                    }

                    if (!res.isUpdateTicket) {
                        binding.updateTktBtn.gone()
                    } else {
                        binding.updateTktBtn.visible()
                    }

                    if (!isShiftPassenger) {
                        binding.ticketDetailsBtnShiftPassenger.gone()
                    } else {
                        binding.ticketDetailsBtnShiftPassenger.visible()
                    }

                    if (!isCanCancelTicket || res.partialPaymentDetails != null) {
                        binding.cancelTktBtn.gone()
                    } else {
                        binding.cancelTktBtn.visible()
                    }
                          //  && !isShiftPassenger
                    /*if (!res.isUpdateTicket) {
                        binding.apply {
                            updateTktBtn.gone()
                            *//*ticketDetailsBtnShiftPassenger.gone()
                            layoutBtnShiftUpdate.gone()*//*
                        }
                    } else {
                        binding.apply {
                            updateTktBtn.visible()
                            *//*ticketDetailsBtnShiftPassenger.visible()
                            layoutBtnShiftUpdate.visible()*//*
                        }
                    }*/

                    if (!res.isUpdateTicket && !isShiftPassenger && !isCanCancelTicket) {
                        binding.apply {
                            updateTktBtn.gone()
                            ticketDetailsBtnShiftPassenger.gone()
                            layoutBtnShiftUpdate.gone()
                            cancelTktBtn.gone()
                            tvMoreOptions.gone()
                        }
                    }

                    if(getPrivilegeBase()?.country.equals("indonesia",true)){
                        if (isShiftPassenger && isInsurance) {
                            binding.ticketDetailsBtnShiftPassenger.visible()
                        } else {
                            binding.ticketDetailsBtnShiftPassenger.gone()
                        }
                    }



                    PreferenceUtils.setPreference(
                        getString(R.string.viewPassengerEditVisibility),
                        res.isUpdateTicket
                    )
                }
            }
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

    private fun isPassengerMobileEmpty(mobileNumber: String): Boolean {
        return mobileNumber.length <= 4
    }

    private fun dismissProgressBar() {
        _sheetReleaseTicketsBinding.progressBarRelease.gone()
    }

    private fun authPinPhoneReleaseDialog() {
        if (shouldPhoneBlockingRelease && countryName.equals("india",true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = this@TicketDetailsActivity,
                fragmentManager = supportFragmentManager,
                pinSize = pinSize,
                getString(R.string.phone_block_release),
                onPinSubmitted = { pin: String ->
                    callReleaseTicketApi(pin)
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
            callReleaseTicketApi("")
            dismissProgressBar()
            selectedSeatNumber.clear()
            currentCheckedItem.clear()
        }
    }

    private fun callReleaseTicketApi(authPin: String) {
        val releaseTicketRequest = ReleaseTicketRequest(
            bccId,
            format_type,
            release_phone_block_ticket_method_name,
            com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBody(
                loginModelPref.api_key,
                ticketNumber.toString(),
                releaseTicketRemarks,
                isFromDashboard,
                com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.Ticket(
                    selectedSeatNumber.toString()
                ),
                json_format,
                locale = locale,
                authPin = authPin
            )
        )
        /*dashboardViewModel.releaseTicketAPI(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            releaseTicketRequest,
            release_phone_block_ticket_method_name
        ) */

        dashboardViewModel.releaseTicketAPI(
            com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBody(
                loginModelPref.api_key,
                ticketNumber.toString(),
                releaseTicketRemarks,
                isFromDashboard,
                com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.Ticket(
                    selectedSeatNumber.toString()
                ),
                json_format,
                locale = locale,
                authPin = authPin
            ),
            release_phone_block_ticket_method_name
        )
    }

    private fun setReleaseTicketObserver(bottomSheetDialoge: BottomSheetDialog) {

        cancelOtpLayoutDialogOpenCount = 0

        dashboardViewModel.releaseTicketResponseViewModel.observe(this) { it ->
            if (it != null && it.code == 200) {
                _sheetReleaseTicketsBinding.progressBarRelease.gone()

                if (it.key.isNotEmpty() == true) {
                    if (cancelOtpLayoutDialogOpenCount == 0) {
                        DialogUtils.cancelOtpLayoutDialog(this, this, this, dimissAction = {})
                        cancelOptkey = it.key
                        if (it.result != null && !it.result.message.isNullOrEmpty()) {
                            toast(it.result.message)
                        } else {
                            toast(it.message)
                        }
                        cancelOtpLayoutDialogOpenCount++
                    }
                    cancelOptkey = it.key

                } else {
                    if (it.result != null && !it.result.message.isNullOrEmpty()) {
                        toast(it.result.message)
                    } else {
                        toast(it.message)
                    }
                    bottomSheetDialoge.dismiss()
                    intent = Intent(this, DashboardNavigateActivity::class.java)
                    intent.putExtra("newBooking", true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            } else {
                toast(getString(R.string.opps))
//                    _sheetReleaseTicketsBinding.progressBarRelease.gone()
//                    currentCheckedItem.clear()
//                    bottomSheetDialoge.dismiss()
            }
        }
    }

    private fun callConfirmOtpReleasePhoneBlockTicketApi() {
        val reqBody =
            com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.ReqBody(
                apiKey,
                true,
                cancelOptkey,
                cancelOtp,
                ticketNumber.toString(),
                "",
                com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.Ticket(
                    selectedSeatNumber.toString()
                )
            )
        val confirmOtpReleasePhoneBlockTicketRequest = ConfirmOtpReleasePhoneBlockTicketRequest(
            bccId,
            format_type,
            confirm_otp_release_phoneblock_ticket,
            reqBody
        )

        /*cancelTicketViewModel.getConfirmOtpReleasePhoneBlockTicketApi(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            confirmOtpReleasePhoneBlockTicketRequest,
            cancellation_details_ticket_method_name
        ) */

        cancelTicketViewModel.getConfirmOtpReleasePhoneBlockTicketApi(
            reqBody,
            cancellation_details_ticket_method_name
        )
    }

    private fun setConfirmOtpReleaseObserver() {

        cancelTicketViewModel.confirmOtpReleasePhoneBlockTicketResponse.observe(this) {
            progressDialog.dismiss()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        otpDialog!!.dismiss()
                        DialogUtils.successfulMsgDialog(this, it.message)
                        bottomSheet!!.dismiss()
                        progressDialog.dismiss()
                        Handler(Looper.getMainLooper()).postDelayed({
                            intent = Intent(this, DashboardNavigateActivity::class.java)
                            intent.putExtra("newBooking", true)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }, 2100)


                    }
                    422 -> {
                        if (it.message != null) {
                            toast("${it.message}")
                        }
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        progressDialog.dismiss()

                    }
                    401 -> {
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        progressDialog.dismiss()
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }
                    else -> {
                        toast(it.message)
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        progressDialog.dismiss()
                    }
                }

            } else {
                toast(getString(R.string.server_error))
                cancelOtp = ""
            }
        }
    }

    private fun onClickListeners() {
        binding.apply {
            viewPassengersBtn.setOnClickListener(this@TicketDetailsActivity)
            btnNewBooking.setOnClickListener(this@TicketDetailsActivity)
            updateTktBtn.setOnClickListener(this@TicketDetailsActivity)
            historyBtn.setOnClickListener(this@TicketDetailsActivity)
            ticketDetailsBtnShiftPassenger.setOnClickListener(this@TicketDetailsActivity)
            cancelTktBtn.setOnClickListener(this@TicketDetailsActivity)
            callPhoneNumber.setOnClickListener(this@TicketDetailsActivity)
            shareToWhatsapp.setOnClickListener(this@TicketDetailsActivity)
            sendSms.setOnClickListener(this@TicketDetailsActivity)
            sendEmail.setOnClickListener(this@TicketDetailsActivity)
            shareBtn.setOnClickListener(this@TicketDetailsActivity)
            releaseTicketBtn.setOnClickListener(this@TicketDetailsActivity)
            reBookTktBtn.setOnClickListener(this@TicketDetailsActivity)
            btnPrint.setOnClickListener(this@TicketDetailsActivity)
            btnPayPendingAmt.setOnClickListener(this@TicketDetailsActivity)
            imgPartialPaymentInfo.setOnClickListener(this@TicketDetailsActivity)
        }
        binding.btnBack.setOnClickListener {
            if (redirectToDashBoardActivity) {
                intent = Intent(this, DashboardNavigateActivity::class.java)
                intent.putExtra("newBooking", true)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                super.onBackPressed()
                selectedSeatNumber.clear()
                currentCheckedItem.clear()
            }
            if (intent.getBooleanExtra(getString(R.string.pnr_search), false)) {
                intent = Intent(this, DashboardNavigateActivity::class.java)
                intent.putExtra("newBooking", true)
                startActivity(intent)
            }
            super.onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (redirectToDashBoardActivity) {
            intent = Intent(this, DashboardNavigateActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

            intent.putExtra("newBooking", true)
            startActivity(intent)
            selectedSeatNumber.clear()
            currentCheckedItem.clear()
            finish()
        }
        if (intent.getBooleanExtra(getString(R.string.pnr_search), false)) {
            intent = Intent(this, DashboardNavigateActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("newBooking", true)
            startActivity(intent)
        }
    }

    @SuppressLint("LogNotTimber")
    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.historyBtn -> {
                firebaseLogEvent(
                    this,
                    BOOKING_HISTORY,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    BOOKING_HISTORY,
                    CLOCK_ICON
                )
                intent = Intent(this, BookingHistoryActivity::class.java)
                intent.putExtra(getString(R.string.pnr_number), ticketNumber)
                startActivity(intent)
            }
            R.id.ticketDetails_btnShiftPassenger -> {
                if(getPrivilegeBase()?.country.equals("indonesia",true)){
                    if (isShiftPassenger && isInsurance) {
                        goToShiftPassengerActivity()

                    } else {
                        toast(getString(R.string.passenger_cannot_be_shifted))
                    }
                }else{
                    goToShiftPassengerActivity()
                }


            }
            R.id.viewPassengersBtn -> {
                firebaseLogEvent(
                    this,
                    VIEW_PASSENGER,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    VIEW_PASSENGER,
                    ViewPassanger.VIEW_PASSANGER
                )
                viewPassengers()
            }
            R.id.btnNewBooking -> {
                PreferenceUtils.putString(
                    getString(R.string.BACK_PRESS),
                    getString(R.string.new_booking)
                )
                intent = Intent(this, DashboardNavigateActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("newBooking", true)
                startActivity(intent)
                finish()

                firebaseLogEvent(
                    this,
                    NEW_BOOKING,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    NEW_BOOKING,
                    NEW_BOOKING_TICKET_DETAILS
                )
            }
            R.id.update_tkt_btn -> {
                tempTicket.trim().let {
                    editPassengerSheet.showEditPassengersSheet(it)


                }

                firebaseLogEvent(
                    this,
                    UPDATE_TICKET,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    UPDATE_TICKET,
                    UPDATE_TICKET_TICKET_DETAILS
                )
            }

            R.id.cancel_tkt_btn -> {
                tempTicket.trim().let { baseUpdateCancelTicket.showTicketCancellationSheet(it) }

                firebaseLogEvent(
                    this,
                    CANCEL_TICKET,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    CANCEL_TICKET,
                  TicketDetails.CANCEL_TICKET_TICKET_DETAILS
                )
            }
            R.id.callPhoneNumber -> {
                callFunction(passengerMobile ?: "")

                firebaseLogEvent(
                    this,
                    CALL_OPTION_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    CALL_OPTION_CLICKS,
                    CALL_ICON_TICKET_DETAILS
                )
            }

            R.id.shareToWhatsapp -> {
                shareToWhatsapp()

                firebaseLogEvent(
                    this,
                    SHARE_VIA_WA,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    SHARE_VIA_WA,
                    WHATSAPP_SHARE_TICKET_DETAILS
                )
            }
            R.id.sendSms -> {
                callSendSMSEmailApi("sms")

                firebaseLogEvent(
                    this,
                    SHARE_VIA_SMS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    SHARE_VIA_SMS,
                    SMS_SHARE_TICKET_DETAILS
                )
            }
            R.id.sendEmail -> {
                callSendSMSEmailApi("email")

                firebaseLogEvent(
                    this,
                    SHARE_VIA_EMAIL,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    SHARE_VIA_EMAIL,
                    EMAIL_SHARE_TICKET_DETAILS
                )
            }
            R.id.shareBtn -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareTextWhatsapp)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)

                firebaseLogEvent(
                    this,
                    SHARE_ICON,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    SHARE_ICON,
                    SHARE_ICON_TICKET_DETAILS
                )
            }
            R.id.release_ticket_btn -> {
                releaseTicket()

                firebaseLogEvent(
                    this,
                    RELEASE_TICKET,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    RELEASE_TICKET,
                    RELEASE_TICKET_DETAILS
                )

            }
            R.id.btnPrint -> {
                // busLogo = getBitmapDirectFromUrl(operatorLogo!!)
                /*lifecycleScope.launch(Dispatchers.Main) {
                    busLogo = async(Dispatchers.IO) {
                        getBitmapDirectFromUrl(operatorLogo!!)
                    }.await()
                }*/


                generateQrcode()

                    if (!originalTemplate.isNullOrEmpty())
                        bluetoothPrintTemplate = originalTemplate

                    Timber.d("originalTemplate $originalTemplate")

                    if (bluetoothPrintTemplate != null && bluetoothPrintTemplate!!.isNotEmpty()) {
                        if (bluetoothPrintTemplate?.contains("00000")!! && hexaDecimalString != null) {
                            return
                            //bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("<img>$hexaDecimalString<img>","IMAGE")
                        }

                        if (!operatorLogo.isNullOrEmpty()) {
                            getBitmapFromURL(operatorLogo!!, getString(R.string.logo))
                        }

                        getBitmapFromURL(
                            "http://cdn-assets1-cf-r5in.ticketsimply.net/images/qoala-qrcode.jpeg",
                            getString(R.string.insurance_bitmap)
                        )

                        if(IS_PINELAB_DEVICE){
                            if (bluetoothPrintTemplate!!.contains("FOR_EACH_SEAT")) {
                                multiplePrintPinelab()
                            } else {
                                pineLabPrint()
                            }
                        }else if(privilegeResponseModel.isEzetapEnabledInTsApp){
//                            printEzetapTicket()
                        }else{
                            if (PreferenceUtils.getPrintingType() == PRINT_TYPE_BLUETOOTH) {
                                checkPermissions()
                                bluetoothPrint()
                            } else if (PreferenceUtils.getPrintingType() == PRINT_TYPE_HARVARD) {
                                withoutSpacePrint = true
                                if (bluetoothPrintTemplate!!.contains("FOR_EACH_SEAT")) {
                                    multipleSeatPostPrint()
                                } else {
                                    singleSeatPOSPrint()
                                }

                            } else {
                                withoutSpacePrint = false
                                if (bluetoothPrintTemplate!!.contains("FOR_EACH_SEAT")) {
                                    multipleSeatPostPrint()
                                } else {
                                    singleSeatPOSPrint()
                                }
                            }
                        }




                    } else
                        toast(getString(R.string.template_not_configured))

            }


            R.id.re_book_tkt_btn -> {
                PreferenceUtils.setPreference(PREF_UPDATE_COACH, true)

                val intent = Intent(this, NewCoachActivity::class.java)
                intent.putExtra(REDIRECT_FROM, tag)
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

            R.id.btnPayPendingAmt -> {
                if (::ticketData.isInitialized && ticketData != null)
                    DialogUtils.dialogPartialPaid(this, ticketData, setPaymentOptions(), this)
            }
            R.id.imgPartialPaymentInfo -> {
                if (::ticketData.isInitialized && ticketData != null)
//                   setNetworkConnectionObserver()
                    DialogUtils.dialogPartialPaid(this, ticketData, setPaymentOptions(), this)
            }
        }
    }

    private fun goToShiftPassengerActivity() {
        PreferenceUtils.setPreference("BUlK_shifting", false)
        intent = Intent(this, ShiftPassengerActivity::class.java)
        intent.putExtra("1234", seatNumbers)

        intent.putExtra("service_name", serviceName)
        //                    intent.putExtra("service_date", travelDate )
        //                    PreferenceUtils.putString("reservationid",reservationId.toString())!!
        PreferenceUtils.setPreference(
            PREF_RESERVATION_ID, reservationId
        )
        intent.putExtra("service_ticketno", ticketNumber)
        startActivity(intent)

        firebaseLogEvent(
            this,
            SHIFT_PAX,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            SHIFT_PAX,
            SHIFT_PASSENGER_TICKET_DETAILS
        )
    }

    /*var printerInterface: EPrinterInterface? = null
    private fun printEzetapTicket() {

        var template = bluetoothPrintTemplate!!

        try {
            if (ticketData.passengerDetails != null && ticketData.passengerDetails?.isNotEmpty()!!) {
                printArray = JSONArray()

                val multiSeats = mutableListOf<String>()
                Timber.e("Passenger size : ${ticketData.passengerDetails!!.size}")
                for (i in 0..ticketData.passengerDetails?.size?.minus(1)!!) {
                    if (ticketData.passengerDetails?.size!! != 1) {
                        if (i < ticketData.passengerDetails?.size?.minus(1)!!) {
                            template = template.replace("[C]=", "")
                            template =
                                template.replace("cut here", "")?.trimEnd()!!
                            template =
                                template.replace("BOARDING_QR", "")?.trim()!!
                            template =
                                template.replace("BAR_CODE", "")?.trim()!!

                        } else {
                            template = template.replace("[C]=", "")
                            template =
                                template.replace("=", "")?.trimEnd()!!

                            if (originalTemplate?.contains("BOARDING_QR")!!)
                                template = "${template}\nBOARDING_QR"
                            if (originalTemplate?.contains("BAR_CODE")!!)
                                template = "${template}\n\nBAR_CODE"
                            template =
                                "${template}"
                        }
                    }

                    template =
                        template.replace("FOR_EACH_SEAT", "")
                    if (template.contains("SEAT_EACH_NUMBERS")) {
                        template = template.replace(
                            "SEAT_EACH_NUMBERS",
                            ticketData.passengerDetails!![i]?.seatNumber ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketData.passengerDetails!![i.minus(1)]?.seatNumber?.let {
                                    template.replace(
                                        it,
                                        ticketData.passengerDetails!![i]?.seatNumber ?: ""
                                    )
                                }!!
                        }
                    }
                    if (template.contains("PASSENGER_EACH_NAME")) {
                        template = template.replace(
                            "PASSENGER_EACH_NAME",
                            ticketData.passengerDetails!![i]?.name ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketData.passengerDetails!![i.minus(1)]?.name?.let {
                                    template.replace(
                                        it,
                                        ticketData.passengerDetails!![i]?.name ?: ""
                                    )
                                }!!
                        }
                    }


                    if (template.contains("TICKET_EACH_FARE")) {
                        template = template.replace(
                            "TICKET_EACH_FARE",
                            ticketData.passengerDetails!![i]?.netFare?.toDouble()
                                ?.convert(currencyFormatt) ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketData.passengerDetails!![i.minus(1)]?.netFare?.let {
                                    template.replace(
                                        it,
                                        ticketData.passengerDetails!![i]?.netFare?.toDouble()
                                            ?.convert(currencyFormatt) ?: ""
                                    )
                                }!!
                        }
                    }


                    if (!ticketData.passengerDetails!![i]?.meal_coupons.isNullOrEmpty()) {
                        if (template.contains("MEAL_COUPON_LOOP")) {

                            template = template.replace(
                                "MEAL_COUPON_LOOP",
                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                    .replace("[", "")
                                    .replace("]", "").replace(",", "\n").replace(" ", "")
                            )
                        } else {
                            if (i > 0) {
                                template =
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons?.toString()
                                        ?.let {
                                            template.replace(
                                                it,
                                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                            )
                                        }!!
                            }
                        }

                        if (template.contains("MEAL_COUPON_NUMBER")) {
                            template = template.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                    .replace("[", "").replace("]", "")
                            )
                        } else {
                            if (i > 0) {
                                template = template.replace(
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons.toString()
                                        .replace("[", "").replace("]", ""),
                                    ticketData.passengerDetails!![i]?.meal_coupons.toString()
                                        .replace("[", "").replace("]", "")
                                )
                            }
                        }

                        if (template.contains("MEAL_COUNT")) {
                            template = template.replace(
                                "MEAL_COUNT",
                                ticketData.passengerDetails!![i]?.meal_coupons?.size.toString()
                            )
                        } else {
                            if (i > 0) {
                                template = template.replace(
                                    ticketData.passengerDetails!![i.minus(1)]?.meal_coupons?.size.toString(),
                                    ticketData.passengerDetails!![i]?.meal_coupons?.size.toString()
                                )
                            }
                        }
                    } else {
                        template = template.replace(
                            "MEAL_COUPON_LOOP", ""
                        )
                    }




                    template =
                        ticketData.serviceNumber?.let {
                            template.replace(
                                "SERVICE_NUMBER",
                                it
                            )
                        }!!

                    template = template.replace(
                        "PAID_AMOUNT",
                        ticketData.partialPaymentDetails?.paidAmount.toString()
                    )
                    template = template.replace(
                        "REMAINING_AMOUNT",
                        ticketData.partialPaymentDetails?.remainingAmount.toString()
                    )

                    if (template.contains("ORIGIN")) {
                        template = template.replace("ORIGIN", ticketData.origin)
                    }
                    if (template.contains("DESTINATION")) {
                        template = template.replace("DESTINATION", ticketData.destination!!)
                    }
                    if (template.contains("WEB_ADDRESS")) {
                        template = template.replace(
                            "WEB_ADDRESS",
                            privilegeResponseModel.webAddressUrl
                        )
                    }


                    if (template.contains("TICKET_FARE")) {
                        template = template.replace(
                            "TICKET_FARE",
                            "${privilegeResponseModel.currency} ${
                                ticketData.totalFare?.toString()?.toDouble()
                                    ?.convert(currencyFormatt) ?: ""
                            }"
                        )
                    }



                    template = ticketData.boardingDetails?.landmark?.let {
                        template.replace(
                            "LANDMARK",
                            it
                        )
                    } ?: "-"

                    template = template.replace(
                        "OPERATOR_NAME",
                        privilegeResponseModel.operatorName
                    )

                    template = template.replace(
                        "WEB_ADDRESS",
                        privilegeResponseModel.webAddressUrl
                    )

                    template = template.replace(
                        "ACCOUNT_HOLDER_NAME",
                        loginModelPref.userName
                    )
                    template =
                        ticketData.ticketNumber?.let {
                            template.replace(
                                "PNR_NUMBER",
                                it
                            )
                        }!!
                    template =
                        ticketData.ticketStatus?.let {
                            template.replace(
                                "TICKET_STATUS",
                                it
                            )
                        } ?: "-"
                    template =
                        template.replace("ORIGIN", ticketData.origin)
                    template =
                        ticketData.destination?.let {
                            template.replace(
                                "DESTINATION",
                                it
                            )
                        }!!
                    template = ticketData.boardingDetails?.depTime?.let {
                        template.replace(
                            "DEPARTURE_TIME",
                            it
                        )
                    }!!
                    template =
                        ticketData.travelDate?.let {
                            template.replace(
                                "TRAVEL_DATE",
                                "$it"
                            )
                        }!!
                    template = template.replace(
                        "TICKET_FARE",
                        "${privilegeResponseModel.currency} ${
                            ticketData.totalFare?.toString()?.toDouble()
                                ?.convert(currencyFormatt) ?: ""
                        }"
                    )
                    template = ticketData.passengerDetails?.get(i)?.name?.let {
                        template.replace(
                            "PASSENGER_NAME",
                            it
                        )
                    } ?: ""
                    template = ticketData.passengerDetails?.get(i)
                        ?.let {
                            it.mobile?.let { it1 ->
                                template.replace(
                                    "MOBILE_NUMBER",
                                    it1
                                )
                            }
                        } ?: ""
                    template =
                        ticketData.seatNumbers?.let {
                            template.replace(
                                "SEAT_NUMBERS",
                                it
                            )
                        }!!


                    template = ticketData.boardingDetails?.address?.let {
                        template.replace(
                            "BOARDING_POINT",
                            it
                        )
                    }!!
                    template = ticketData.dropOffDetails?.address?.let {
                        template.replace(
                            "DROPPING_POINT",
                            it
                        )
                    }!!
                    template = ticketData.boardingDetails?.contactPersons?.let {
                        template.replace(
                            "CONTACT_PERSON",
                            it
                        )
                    }!!

                    template = ticketData.boardingDetails?.contactNumbers?.let {
                        template.replace(
                            "CONTACT_NUMBER_PERSON",
                            it
                        )
                    }!!

                    template = ticketData.ticketLeadDetail?.ticketBookedBy?.let {
                        template.replace(
                            "TICKET_BOOKED_BY",
                            it.substringBefore(",")
                        )
                    }!!

                    template =
                        ticketData.busType?.let {
                            template.replace(
                                "COACH_TYPE",
                                it
                            )
                        }!!
                    template =
                        template.replace("REMARKS", ticketData.remarks ?: "-")
                    template = template.replace(
                        "TERMINAL_ID",
                        ticketData.terminalRefNo ?: ""
                    )
                    if (ticketData.terminalRefNo.isNullOrEmpty()) {
                        template =
                            template.replace("TERMINAL_PULOGABANG", "")
                    }
                    template =
                        template.replace("CURRENT_DATE", getTodayDate())
                    template =
                        template.replace("CURRENT_TIME", getTodayDateWithTime())

                    template = template.replace("TAB_SPACE", " ")

                    template = template.replace("BAR_CODE", " ")

                    template = template.replace("BOLD_ON", "")
                    template = template.replace("BOLD_OFF", "")
                    template = template.replace("ALIGN_LEFT|", "")
                    template = template.replace("ALIGN_CENTER|", "")
                    template = template.replace("NEW_LINE", "\n")
                    template = template.replace("ONE_SPACE", "")


                    template = "\n$template\n"
                    template.let { multiSeats.add(it) }

                    Timber.e("Template : $template")




                    printerInterface = EPrinterImplementation.getInstance()
                    printerInterface!!.init(this)
                    if (printerInterface!!.isPrinterSupported) {
                        Log.v("tag", "printer is supported")
                    } else {
                        Log.v("tag", "printer NOT supported")
                    }

                    if (printerInterface != null) {
                        Thread {
                            printerInterface!!.printText(
                                template
                            ) { event, data ->
                                runOnUiThread {
                                    Toast.makeText(
                                        this,
                                        data.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }.start()
                    }

                }
            }}
        catch (e:Exception){

        }
    }*/



    private fun pineLabPrint() {

        if (isBound!!) {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y_H_M)
            val currentDate = dateFormat.format(calendar.time)
            
            
            printArray = JSONArray()
            var template = bluetoothPrintTemplate

            if (template!!.contains("PNR_NUMBER")) {
                template = template.replace("PNR_NUMBER", ticketData.ticketNumber!!)
            }
            if (template.contains("TRAVEL_DATE")) {
                template = template.replace("TRAVEL_DATE", ticketData.travelDate!!)

            }
            if (template.contains("TAB_SPACE")) {
                template = template.replace("TAB_SPACE", "")

            }
            if (template.contains("NEW_LINE")) {
                template = template.replace("NEW_LINE", "\n")

            }
            if (template.contains("ONE_SPACE")) {
                template = template.replace("ONE_SPACE", " ")
            }

            if (template.contains("SEAT_EACH_NUMBERS")) {
                template = template.replace("SEAT_EACH_NUMBERS", ticketData.seatNumbers!!)

            }
            if (template.contains("SEAT_NUMBERS")) {
                template = template.replace("SEAT_NUMBERS", ticketData.seatNumbers!!)

            }
            if (template.contains("BOARDING_POINT")) {
                template = template.replace("BOARDING_POINT", ticketData.boardingDetails?.address!!)

            }
            if (template.contains("DROPPING_POINT")) {
                template = template.replace("DROPPING_POINT", ticketData.dropOffDetails?.address!!)

            }
            if (template.contains("PASSENGER_NAME")) {
                template =
                    template.replace("PASSENGER_NAME", ticketData.passengerDetails?.get(0)?.name!!)
            }
            if (template.contains("DEPARTURE_TIME")) {
                template = template.replace(
                    "DEPARTURE_TIME", ticketData.boardingDetails?.depTime!!
                )
            }
            if (template.contains("MOBILE_NUMBER")) {
                template =
                    template.replace("MOBILE_NUMBER", ticketData.passengerDetails?.get(0)?.mobile!!)
            }
            if (template.contains("TICKET_EACH_FARE")) {
                template = template.replace(
                    "TICKET_EACH_FARE", "${privilegeResponseModel.currency} ${
                        ticketData.totalFare?.toString()?.toDouble()?.convert(currencyFormatt) ?: ""
                    }"
                )
            }
            if (template.contains("ACCOUNT_HOLDER_NAME")) {
                template = template.replace("ACCOUNT_HOLDER_NAME", loginModelPref.userName)

            }
            if (template.contains("TICKET_BOOKED_BY")) {
                template = template.replace(
                    "TICKET_BOOKED_BY",
                    ticketData.ticketLeadDetail?.ticketBookedBy!!
                )
            }
            if (template.contains("SERVICE_NUMBER")) {
                template = template.replace("SERVICE_NUMBER", ticketData.serviceNumber!!)

            }
            if (template.contains("CONTACT_NUMBER_PERSON")) {
                template = ticketData.boardingDetails?.contactPersons?.let {
                    template?.replace(
                        "CONTACT_NUMBER_PERSON",
                        it
                    )
                }!!
            }
            if (template.contains("LANDMARK")) {
                template = ticketData.boardingDetails?.landmark?.let {
                    template?.replace(
                        "LANDMARK",
                        it
                    )
                } ?: "-"
            }
            if (template.contains("CURRENT_TIME")) {
                template = template.replace("CURRENT_TIME", currentDate)!!
            }
            if (template.contains("BOLD_ON")) {
                template = template.replace("BOLD_ON", "")
            }
            if (template.contains("BOLD_OFF")) {
                template = template.replace("BOLD_OFF", "")
            }
            if (template.contains("INSURANCE_QRCODE")) {
                template = template.replace("INSURANCE_QRCODE", "")
            }
            if (template.contains("MEAL_TYPE")) {
                template = template.replace("MEAL_TYPE", "")
            }
            if (template.contains("ALL_INSURANCE_NUMBERS")) {
                template = template.replace("ALL_INSURANCE_NUMBERS", "")
            }
            if (template.contains("ORIGIN")) {
                template = template.replace("ORIGIN", ticketData.origin)
            }
            if (template.contains("DESTINATION")) {
                template = template.replace("DESTINATION", ticketData.destination!!)
            }
            if (template.contains("WEB_ADDRESS")) {
                template = template.replace(
                    "WEB_ADDRESS",
                    privilegeResponseModel.webAddressUrl
                )!!
            }


            if (template.contains("TICKET_FARE")) {
                template = template.replace(
                    "TICKET_FARE",
                    "${privilegeResponseModel.currency} ${
                        ticketData.totalFare?.toString()?.toDouble()?.convert(currencyFormatt) ?: ""
                    }"
                )!!
            }


            val arr = template.split("\n")

            for (i in arr) {
                if (i.contains("ALIGN_LEFT")) {
                    val print = i.substringAfter("|")
                    createJson("0", false, print, "0", "0")
                } else if (i.contains("ALIGN_CENTER") && i.contains("|")) {
                    val print = i.substringAfter("|")
                    createJson("0", true, print, "0", "0")
                } else if (i.contains("ALIGN_CENTER")) {
                    val print = i.substringAfter("ALIGN_CENTER")
                    createJson("0", true, print, "0", "0")
                } else if (i.contains("IMAGE")) {
                    if (hexvalue!!.isNotEmpty()) {
                        createJson("2", true, "", "0", hexvalue!!)
                    }

                } else if (i.contains("BOARDING_QR")) {
                    if (qrCodeInput != null) {
                        createJson("4", true, qrCodeInput, "0", "0")
                    }
                } else {
                    val print = i
                    createJson("0", true, print, "0", "0")
                }
            }


            val headerObj = JSONObject()
           // headerObj.put("ApplicationId", "be1dc81f1cd941f39afd7ccbb7d7f023")
            headerObj.put("ApplicationId", "202819bb9da84d89a356bdefd8585212")
            headerObj.put("UserId", "user1234")
            headerObj.put("MethodId", "1002")
            headerObj.put("VersionNo", "1.0")

            val detailObj = JSONObject()
            detailObj.put("PrintRefNo", ticketData.ticketNumber!!)
            detailObj.put("SavePrintData", true)
            detailObj.put("Data", printArray)


            val json = JSONObject()
            json.put("Header", headerObj)
            json.put("Detail", detailObj)


            val data = Bundle()
            data.putString(BILLING_REQUEST_TAG, json.toString())
            message.data = data
            try {
                message.replyTo = Messenger(IncomingHandler(this))
                mServerMessenger!!.send(message)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        } else {
            toast("Pinelab device not found")
        }


    }


    private fun getBitmapDirectFromUrl(image: String): Bitmap? {
        var image1: Bitmap? = null
        try {
            CoroutineScope(Dispatchers.IO).run {
            val url = URL(image)
            image1 = BitmapFactory.decodeStream(url.openConnection().getInputStream())
             }
        }catch (e : Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }
        return image1
    }

    fun createJson(
        printType: String,
        isCenter: Boolean,
        dataToPrint: String,
        imagePath: String,
        imageData: String
    ) {
        val json = JSONObject()
        json.put("PrintDataType", printType)
        json.put("PrinterWidth", 24)
        json.put("IsCenterAligned", isCenter)
        json.put("DataToPrint", dataToPrint)
        json.put("ImagePath", imagePath)
        json.put("ImageData", imageData)
        printArray.put(json)

    }

    private fun redelcomPrintdataSet(ticketData: Body) {
        /*val cDate = Date()
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy").format(cDate)
        val currentTime: String = SimpleDateFormat("HH:mm").format(cDate)
        var ticketPrint = " ";

        val bAddress = ticketData.boardingDetails?.stageName ?: ticketData.boardingDetails?.address
        ?: getString(R.string.notAvailable)
        val dAddress = ticketData.dropOffDetails?.stageName ?: getString(R.string.notAvailable)
        var usersList: MutableList<LoginModel>? = arrayListOf()
        if (PreferenceUtils.getObject<Users>(PREF_USER_LIST_STRING) != null) {
            val users = PreferenceUtils.getObject<Users>(PREF_USER_LIST_STRING)
            usersList = users!!.users
        }

        val passengerNameList: ArrayList<String> = arrayListOf()
        val seatNoList: ArrayList<String> = arrayListOf()
        for (i in 0 until ticketData.passengerDetails!!.size) {
            passengerNameList.add(ticketData.passengerDetails[i]!!.name!!)
            seatNoList.add(ticketData.passengerDetails[i]!!.seatNumber)
        }

        val pName = java.lang.StringBuilder("")
        val sNumber = java.lang.StringBuilder("")

        for (eachstring in passengerNameList) {
            pName.append(eachstring).append(",")
        }
        for (eachstring in seatNoList) {
            sNumber.append(eachstring).append(",")
        }
        var passengerNames: String = ""
        if (pName.toString().length > 1) {
            passengerNames = pName.toString().substring(0, pName.toString().length - 1)
        } else {
            passengerNames = pName.toString()
        }
        var seatsNumbers: String = ""
        if (sNumber.toString().length > 1) {
            seatsNumbers = sNumber.toString().substring(0, sNumber.toString().length - 1)
        } else {
            seatsNumbers = sNumber.toString()
        }
        var ownerContact = ""
        var ownerName = usersList!![0].name
        if (usersList!![0].phone_number == "") {
            ownerContact = "-"
        } else {
            ownerContact = usersList[0].phone_number
        }
        val operatorName = PreferenceUtils.getLogin().travels_name
        val domainName = PreferenceUtils.getLogin().domainName
        ticketPrint += "{reset}";
        ticketPrint += "{center}";
        ticketPrint += "{b}" + operatorName + "{br}";
        ticketPrint += domainName + "{br}";
        ticketPrint += "BUS_TICKET" + "{br}{br}";


        ticketPrint += "TRAVEL_DETAILS" + "{br}" +
                "{left}" + getString(R.string.TICKET_NUMBER_PRINT) + ": " + "{right}" + ticketData.ticketNumber + "{br}" +
                "{left}" + getString(R.string.BOARDING_STAGE) + ": " + "{right}" + bAddress + "{br}" +
                "{left}" + getString(R.string.ORIGIN_LABEL) + ": " + "{right}" + ticketData.origin + "{br}" +
                "{left}" + getString(R.string.DESTINATION) + ": " + "{right}" + dAddress + "{br}" +
                "{left}" + getString(R.string.DEP_TIME) + ": " + "{right}" + ticketData.depTime + "{br}" +


                "{left}" + getString(R.string.TRAVEL_DATE) + ": " + "{right}" + ticketData.travelDate + "{br}" +
                "{left}" + getString(R.string.BASE_FARE) + ": " + "{right}" + ticketData.totalFare + "{br}" +
                "{left}" + getString(R.string.BOOKED_BY) + ": " + "{right}" + ticketData.booking_source + "{br}{br}" +

                "{left}" + getString(R.string.PASSENGER_DETAILS) + "{br}" +
                "{left}" + getString(R.string.NAME) + ": " + "{right}" + passengerNames + "{br}" +
                "{left}" + getString(R.string.CONTACT_NUMBER) + ": " + "{right}" + ticketData.passengerDetails[0]!!.mobile + "{br}" +
                "{left}" + getString(R.string.SEATS) + ": " + "{right}" + seatsNumbers + "{br}{br}" +

                "{left}" + getString(R.string.PRINT_BY) + ": " + "{right}" + ownerName + "{br}" +
                "{left}" + getString(R.string.CONTACT_NUMBER) + ": " + "{right}" + ownerContact + "{br}" +
                "{left}" + getString(R.string.DATE_AND_TIME) + ": " + "{right}" + currentDate + "," + "{right}" + currentTime +

                getString(R.string.NOTE_PRINT) + "{br}{br}{br}";


        val json = JSONObject()
        json.put("printText", ticketPrint)
        json.put("terminalId", usersList[0].redelcomData!!.terminalId)
        val body = ReqBodyPrint(ticketPrint, usersList[0].redelcomData!!.terminalId)
        val authCode =
            generateAuthCode(usersList[0].redelcomData!!.api_key, API_PRINT, json.toString())
        hitRedelcomPrintApi(authCode!!, body)

         */
    }


    private fun hitRedelcomPrintApi(authCode: String, body: ReqBodyPrint) {

        binding.includeProgress.progressBar.visibility = View.VISIBLE
        val retrofit = initRetrofit()
        val api = retrofit!!.create(ApiInterface::class.java)

        val call = api.apiRedelcomPrint(authCode, body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == 200) {
                    binding.includeProgress.progressBar.visibility = View.GONE
                    toast(getString(R.string.printing))

                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }


        })
    }

    private fun initRetrofit(): Retrofit? {
        val retrofit = Retrofit.Builder()
            .baseUrl(PreferenceUtils.getObject<RedelcomPreferenceData>(PREF_REDELCOM_DETAILS)!!.redelcom_uri)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    @Throws(java.lang.Exception::class)
    fun generateAuthCode(key: String, path: String, body: String): String {
        val msg = "/" + path + "," + body
        val sha256_HMAC = Mac.getInstance("HmacSHA256")
        val secret_key = SecretKeySpec(key.toByteArray(charset("UTF-8")), "HmacSHA256")
        sha256_HMAC.init(secret_key)
        val base64 =
            Base64.encodeBase64String(sha256_HMAC.doFinal(msg.toByteArray(charset("UTF-8"))))
        val authKey =
            PreferenceUtils.getObject<RedelcomPreferenceData>(PREF_REDELCOM_DETAILS)!!.client_id + ";" + base64
        return authKey
    }

    private fun printWebView() {
        val webView = WebView(this)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                createWebPrintJob(view)
                myWebView = null
            }

        }

        val htmlDocument = ("<html><body><h1>Android Print Test</h1><p>"
                + "This is some sample content.</p></body></html>")

        webView.loadDataWithBaseURL(
            null, htmlDocument,
            "text/HTML", "UTF-8", null
        )

        myWebView = webView
    }

    private fun createWebPrintJob(webView: WebView?) {

        val printManager: PrintManager = this.getSystemService(PRINT_SERVICE) as PrintManager

        val printAdapter: PrintDocumentAdapter =
            webView!!.createPrintDocumentAdapter("MyDocument")

        val jobName = getString(R.string.app_name) + " Print Test"

        printManager.print(
            jobName, printAdapter,
            PrintAttributes.Builder().build()
        )
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

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    /*
   * this method to used for start Shimmer Effect
   * */
    private fun startShimmerEffect() {
        binding.shimmerTicketDetails.visible()
        binding.mainLayoutTicketDetails.gone()
        binding.toolbar.gone()
        binding.shimmerTicketDetails.startShimmer()
    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.shimmerTicketDetails.gone()
        binding.mainLayoutTicketDetails.visible()
        binding.toolbar.visible()
        if (binding.shimmerTicketDetails.isShimmerStarted) {
            binding.shimmerTicketDetails.stopShimmer()
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
        }
        if (str == "resend") {
            authPinPhoneReleaseDialog()
        } else {
            cancelOtp = str
            if (::progressDialog.isInitialized)
                progressDialog.show()
            callConfirmOtpReleasePhoneBlockTicketApi()
        }
    }

    private fun showRefundAmount() {
        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel

            privilegeResponseModel.apply {
                if (privilegeResponseModel.country.equals("Indonesia", true)) {
                    binding.tvRefundAmount.gone()
                    binding.tvRefundAmountFare.gone()
                    return
                }
            }
        }
        Timber.d("ticketdetailsRefundAnount:${refundAmountFare} ")

        if (refundAmountFare == getString(R.string.notAvailable)) {
            binding.tvRefundAmountFare.gone()
            binding.tvRefundAmount.gone()
        } else {
            binding.tvRefundAmountFare.visible()
            binding.tvRefundAmount.visible()
        }
//        binding.tvRefundAmount.visible()
//        binding.tvRefundAmountFare.visible()
        if (cancellationCharges > 0.0) {
            binding.tvRefundAmountFare.text =
                "$amountCurrency ${(refundAmountFare.toDouble())?.convert(currencyFormatt)} (${
                    getString(
                        R.string.cancellation_charges
                    )
                }-$cancellationCharges)"
        } else {
            binding.tvRefundAmountFare.text =
                "$amountCurrency ${(refundAmountFare.toDouble())?.convert(currencyFormatt)}"
        }
    }

    private fun swipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            startShimmerEffect()
            callTicketDetailsApi()
        }
    }

    override fun onLeftButtonClick() {
        Timber.d("cancel confirm")
    }

    override fun onRightButtonClick() {
        startShimmerEffect()

        payAtBus()
    }

    private fun payAtBus() {

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://$domain/")
            //.addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


        val apiInterface: ApiInterface = retrofit.create(ApiInterface::class.java)


        val payAtBusCall = apiInterface.confirmPayAtBUs(
            pnr.toString(), loginModelPref.api_key, locale
        )
        val call: Call<PayAtBusResponse> = payAtBusCall
        call.enqueue(object : Callback<PayAtBusResponse> {
            override fun onResponse(
                call: Call<PayAtBusResponse>,
                response: Response<PayAtBusResponse>
            ) {
                if (response != null) {
                    if (response.body() != null) {
                        Timber.d("responsePayAtBus: ${call.request()}")
                        if (response.body()?.ticket_status.isNullOrEmpty()) {
                            if (!response.body()?.response?.message.isNullOrEmpty())
                                callTicketDetailsApi()
                            toast(response.body()?.response?.message)

                        } else {
                            if (response.body()?.ticket_status.equals(getString(R.string.seat_booked))) {
                                callTicketDetailsApi()

                                binding.toolbar.setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@TicketDetailsActivity,
                                        R.color.colorPrimary
                                    )
                                )
                                binding.constraintLayoutTop2.setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@TicketDetailsActivity,
                                        R.color.colorPrimary
                                    )
                                )

                                binding.titleMain.setText(R.string.ticket_booked_successfully)
                            }
                        }

                    } else {
                        toast(getString(R.string.something_went_wrong))
                    }


                }


            }

            override fun onFailure(call: Call<PayAtBusResponse>, t: Throwable) {
                Timber.d("directApiCall:1: ${t.message}")
            }

        })

//        ApiRepo.callRetrofit(directionCall, this, directionUrl, this, progress_bar, this)
    }

    override fun onButtonClick(vararg args: Any) {
        if (args.isNotEmpty()) {
            progressDialog = ProgressDialog(this)
            when (args[0]) {
                PARTIAL_RELEASE_BTN -> {
                    selectedSeatNumber.clear()
                    progressDialog.show()
                    if (isNetworkAvailable()) {
                        // releasePartialBookedDirectApi()
                        callCancellationDetailsApi()
                    } else
                        noNetworkToast()
                }
                PARTIAL_CONFIRM_BTN -> {
                    progressDialog.show()
                    if (isNetworkAvailable())
                        payPendingAmountDirectApi()
                    else
                        noNetworkToast()
                }
                PARTIAL_PAYMENT_OPTION -> {
                    val paymentPosition: Int = args[1].toString().toDouble().toInt()
                    selectedPartialPaymentOption = paymentOptionsList[paymentPosition].id.toString()
                    if (selectedPartialPaymentOption == "2")
                        DialogUtils.creditDebitDialog(this, this)
                }
                getString(R.string.credit_debit) -> {
                    creditDebitCardNo = args[1].toString()
                }
            }

        }

    }

    private fun releasePartialBookedDirectApi(
    ) {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://$domain/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val apiInterface: ApiInterface = retrofit.create(ApiInterface::class.java)

        val releasePartialBookedCall = locale?.let {
            ticketNumber?.let { it1 ->
                apiInterface.releasePartialBookedTicket(
                    it1,
                    loginModelPref.api_key, operator_api_key, it
                )
            }
        }
        val call: Call<ReleasePartialBookedTicket> = releasePartialBookedCall!!
        call.enqueue(object : Callback<ReleasePartialBookedTicket> {
            override fun onResponse(
                call: Call<ReleasePartialBookedTicket>,
                response: Response<ReleasePartialBookedTicket>
            ) {
                progressDialog.dismiss()
                if (response.body() != null) {
                    val releasePartialBookedTicket: ReleasePartialBookedTicket = response.body()!!
                    if (releasePartialBookedTicket.code == 200) {
                        toast(releasePartialBookedTicket.message)

                        PreferenceUtils.putString(
                            getString(R.string.BACK_PRESS),
                            getString(R.string.new_booking)
                        )
                        val intent = Intent(
                            this@TicketDetailsActivity,
                            DashboardNavigateActivity::class.java
                        )
                        intent.putExtra("newBooking", true)
                        startActivity(intent)
                        finish()
                    } else
                        toast(releasePartialBookedTicket.message)
                } else {
                    toast(getString(R.string.server_error))
                }

            }

            override fun onFailure(call: Call<ReleasePartialBookedTicket>, t: Throwable) {
                toast(t.message)
                progressDialog.dismiss()
            }

        })

    }

    private fun payPendingAmountDirectApi(
    ) {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://$domain/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val apiInterface: ApiInterface = retrofit.create(ApiInterface::class.java)

        val payPendingAmountRequest =
            ticketNumber?.let {
                PayPendingAmountRequest(
                    loginModelPref.user_id ?: -1,
                    1,
                    selectedPartialPaymentOption,
                    it
                )
            }

        if (selectedPartialPaymentOption == "2") {
            payPendingAmountRequest?.ticket = Ticket(creditDebitCardNo)
        }

        val payPendingAmountCall = locale?.let {
            payPendingAmountRequest?.let { it1 ->
                apiInterface.payPendingAmount(
                    loginModelPref.api_key, operator_api_key, it,
                    it1
                )
            }
        }
        val call: Call<PayPendingAmount> = payPendingAmountCall!!
        call.enqueue(object : Callback<PayPendingAmount> {
            override fun onResponse(
                call: Call<PayPendingAmount>,
                response: Response<PayPendingAmount>
            ) {

                progressDialog.dismiss()
                if (response.body() != null) {
                    val payPendingAmount: PayPendingAmount = response.body()!!
                    if (payPendingAmount.code == 200) {
                        toast(payPendingAmount.message)

                        if (isNetworkAvailable())
                            callTicketDetailsApi()
                        else
                            noNetworkToast()

                    } else {
                        if (payPendingAmount.message != null)
                            toast(payPendingAmount.message)
                        else {
                            if (payPendingAmount.result?.message != null)
                                toast(payPendingAmount.result?.message)
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }

            }

            override fun onFailure(call: Call<PayPendingAmount>, t: Throwable) {
                toast(t.message)
                progressDialog.dismiss()
            }

        })

    }

    private fun setPaymentOptions(): MutableList<SearchModel> {
        paymentOptionsList.clear()
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel.allowCashCreditOptionsInBooking != null && privilegeResponseModel.allowCashCreditOptionsInBooking) {
                val cash = SearchModel()
                cash.id = "1"
                cash.name = getString(R.string.cash)

                val creditDebitCard = SearchModel()
                creditDebitCard.id = "2"
                creditDebitCard.name = getString(R.string.credit_debit)

                paymentOptionsList.add(cash)
                paymentOptionsList.add(creditDebitCard)

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
                paymentOptionsList.add(walletUpi)

            }
            if (privilegeResponseModel.allowToConfigurePaymentOptionsInBookingPage) {
                val others = SearchModel()
                others.id = "3"
                others.name = getString(R.string.others)
                paymentOptionsList.add(others)

            }
            if (privilegeResponseModel.allowDepositOptionsInBooking) {
                val deposit = SearchModel()
                deposit.id = "158"
                deposit.name = getString(R.string.deposit)
                paymentOptionsList.add(deposit)

            }
        }
        return paymentOptionsList
    }


    private fun callCancellationDetailsApi() {

        if (::ticketData.isInitialized && ticketData.passengerDetails != null) {
            val passList = ticketData.passengerDetails
            for (i in 0 until passList?.size!!) {
                selectedSeatNumber.append(passList[i]?.seatNumber)
                if (i < passList.size - 1) {
                    selectedSeatNumber.append(",")
                }
            }

            if (isNetworkAvailable()) {
                val reqBody = com.bitla.ts.domain.pojo.cancellation_details_model.request.ReqBody2(
                    apiKey,
                    selectedCancellationType,
                    true,
                    locale,
                    operator_api_key,
                    "",
                    ticketNumber ?: "",
                    json_format,
                    selectedSeatNumber.toString(),
                    isZeroPercentCancellationCheck,
                    json_format
                )


                val cancellationDetailsRequest = ZeroCancellationDetailsRequest(
                    bccId,
                    format_type,
                    cancellation_details_ticket_method_name,
                    reqBody
                )

                cancelTicketViewModel.getZeroCancellationDetailsApi(
                    reqBody,
                    loginModelPref.api_key
                )
            }
        } else
            noNetworkToast()
    }

    private fun setCancelTicketObserve() {
        cancelTicketViewModel.cancellationDetailsResponse.observe(this) {
            Timber.d("messageResult-${it}")
            progressDialog.dismiss()
            if (it != null) {
                if (it.code == 200) {
                    cancellationAmount = it.result.cancelledFare.toString()
                    refundAmount = it.result.refundAmount.toString()
                    cancelPercent = it.result.cancelPercent.toString()
                    openCancelConfirmDialog(
                        cancellationAmount,
                        refundAmount,
                        cancelPercent
                    )
                    Timber.d("messageResult-${it.result}")
                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

                } else {
                    if (it.message != null) {
                        it.message.let { it -> toast(it) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

    }

    private fun openCancelConfirmDialog(
        cancellationAmountX: String,
        refundAmountX: String,
        cancelPercent: String
    ) {

        var cancellationChargesPercentage = ""
        var cancellationChargesRupee = ""

        when (selectedCancellationType) {
            "1" -> {
                cancellationChargesPercentage = "%"
                cancellationChargesRupee = ""
            }
            "2" -> {
                cancellationChargesRupee = amountCurrency
                cancellationChargesPercentage = ""

            }
            "" -> {
                cancellationChargesPercentage = "%"
                cancellationChargesRupee = ""
            }
        }
        val informPassengersAboutCancellation =
            getString(R.string.selected_seat_s_will_be_cancelled)
        if (::ticketData.isInitialized) {
            val source = ticketData.origin
            val destination = ticketData.destination
            ticketCancelDialog(
                context = this,
                title = getString(R.string.cancel_tickets),
                message = informPassengersAboutCancellation,
                srcDest = "$source - $destination",
                journeyDate = busType ?: "",
                ticketCancellationPercentage = "$cancellationChargesRupee$cancelPercent$cancellationChargesPercentage",
                seatNo = selectedSeatNumber.toString(),
                cancellationAmount = "$amountCurrency${cancellationAmountX.toDouble().convert(currencyFormatt)}",
                refundAmount = "$amountCurrency${refundAmountX.toDouble().convert(currencyFormatt)}",
                buttonLeftText = getString(R.string.goBack),
                buttonRightText = getString(R.string.confirm_cancellation),
                dialogButtonTagListener = this
            )
        }

    }

    private fun ticketCancelDialog(
        context: Context,
        title: String,
        message: String,
        srcDest: String,
        journeyDate: String,
        ticketCancellationPercentage: String,
        seatNo: String,
        cancellationAmount: String,
        refundAmount: String,
        buttonLeftText: String,
        buttonRightText: String,
        dialogButtonTagListener: DialogButtonTagListener,
    ) {
        val builder = AlertDialog.Builder(context).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
        val binding: DialogCancelTicketBinding =
            DialogCancelTicketBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)

        binding.apply {
            tvRefundAmount.visible()
            tvCancellationAmount.visible()
            viewBottom2.visible()
            tvCanellationAmmountText.visible()
            tvRefundText.visible()
            tvHeader.text = title
            tvMessage.text = message
            tvHeaderText.text = srcDest
            tvSubtitle.text = journeyDate
            tvTicketCancellationPercentage.text = ticketCancellationPercentage
            tvSelectedSeatNo.text = seatNo
            tvCancellationAmount.text = cancellationAmount
            tvRefundAmount.text = refundAmount
            btnDark.text = buttonLeftText
            btnLight.text = buttonRightText
        }

        if (message.isEmpty()) {
            binding.tvMessage.gone()
            binding.viewBottom2.gone()
        } else {
            binding.tvMessage.visible()
            binding.viewBottom2.visible()
        }

        binding.btnDark.setOnClickListener {
            builder.cancel()
            dialogButtonTagListener.onLeftButtonClick(binding.btnDark)
        }

        binding.btnLight.setOnClickListener {

            cancelOtpLayoutDialogOpenCount = 0
            DialogPinAuth()
        }

        // setConfirmOtpCancelPartialTicketObserver()
        setCancelPartialOtpTicketObserver()
        builder.setView(binding.root)
        builder.show()
    }

    private fun DialogPinAuth() {
        if (shouldTicketCancellation && countryName.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = this@TicketDetailsActivity,
                fragmentManager = supportFragmentManager,
                pinSize = pinSize,
                getString(R.string.ticket_cancellation),
                onPinSubmitted = { pin: String ->
                    callCancelPartialTicketApi(pin)
                },
                onDismiss = null
            )
        } else {
            callCancelPartialTicketApi("")
        }
    }

    private fun callCancelPartialTicketApi(authPin: String) {
        if (isNetworkAvailable()) {
            val reqBody = com.bitla.ts.domain.pojo.cancel_partial_ticket_model.request.ReqBody(
                apiKey = apiKey,
                cancelType = selectedCancellationType,
                isFromBusOptApp = true,
                locale = locale,
                operatorApiKey = operator_api_key,
                passengerDetails = "",
                responseFormat = json_format,
                seatNumbers = selectedSeatNumber.toString(),
                ticketCancellationPercentageP = ticketCancellationPercentage,
                ticketNumber = ticketNumber ?: "",
                travelDate = travelDate ?: "",
                zeroPercent = isZeroPercentCancellationCheck,
                isOnbehalfBookedUser = isCanCancelTicketForUser,
                onbehalf_online_agent_flag = isOnbehalfOnlineAgentFlag,
                onBehalfUserId = cancelOnBehalOf,
                json_format = json_format,
                is_sms_send = sendSms,
                authPin = authPin,
                remarkCancelTicket = ""
            )
            val cancelPartialTicketRequest = CancelPartialTicketRequest(
                bccId,
                format_type,
                cancel_partial_ticket_method_name,
                reqBody
            )

            cancelTicketViewModel.getCancelPartialTicketApi(
                reqBody,
                loginModelPref.api_key
            )

        } else
            noNetworkToast()
    }

    private fun setCancelPartialOtpTicketObserver() {
        cancelTicketViewModel.cancelPartialTicketViewModel.observe(this) {
            // sheetTicketCancellationBinding.includeProgress.progressBar.gone()

            if (it != null) {
                if (it.code == 200) {
                    DialogUtils.successfulMsgDialog(
                        this, getString(R.string.successfully_cancelled_ticket)
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this, TicketDetailsActivity::class.java)
                        intent.putExtra(
                            getString(R.string.put_extra_cancelTicket),
                            getString(R.string.put_extra_cancelTicket)
                        )
                        intent.putExtra(getString(R.string.TICKET_NUMBER), ticketNumber)
                        this.startActivity(intent)
                    }, 2500)

                    //bottomSheetDialog.dismiss()
                } else if (it.result?.key?.isNotEmpty() == true) {
                    if (cancelOtpLayoutDialogOpenCount == 0) {
                        DialogUtils.cancelOtpLayoutDialog(this, this, this, dimissAction = {})
                        cancelOptkey = it.result.key.toString()
                        toast(it.result.message.toString())
                    }
                    cancelOptkey = it.result.key.toString()

                } else {
                    if (it.message != null)
                        toast(it.message)
                    it.result?.message?.let { it1 -> toast(it1) }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    override fun onLeftButtonClick(tag: View?) {
    }

    override fun onRightButtonClick(tag: View?) {
    }

    override fun onReturnInstance(dialog: Any) {
        otpDialog = dialog as AlertDialog

    }


}

private class IncomingHandler(ticketDetailsActivity: Context) : Handler() {
    private val BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE"
    val context = ticketDetailsActivity
    override fun handleMessage(msg: Message) {
        val bundle = msg.data
        val value = bundle.getString(BILLING_RESPONSE_TAG)
        val json = JSONObject(value!!)
        val text = json.getJSONObject("Response").getString("ResponseMsg")
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        Timber.e("Value : $value")
        // process the response Json as required.
    }
}

