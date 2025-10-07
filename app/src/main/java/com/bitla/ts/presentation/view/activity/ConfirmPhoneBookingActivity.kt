package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.content.*
import android.content.res.*
import android.graphics.*
import android.net.Uri
import android.os.*
import android.util.*
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.*
import androidx.core.os.postDelayed
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import androidx.transition.*
import com.bitla.ts.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.account_info.request.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.booking_custom_request.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.ezetap.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.passenger_details_result.*
import com.bitla.ts.domain.pojo.paytm_pos_integration.paytm_pos_txn_status_api.request.PaytmPosTxnStatusRequest
import com.bitla.ts.domain.pojo.photo_block_tickets.request.*
import com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody
import com.bitla.ts.domain.pojo.pinelabs.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.child_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.validate_otp_wallets.request.*
import com.bitla.ts.domain.pojo.wallet_otp_generation.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.constants.AgentPaymentOptions
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.textfield.*
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

class ConfirmPhoneBookingActivity : BaseActivity(), OnItemClickListener, DialogButtonListener,
    VarArgListener, DialogSingleButtonListener {

    companion object {
        val tag: String = BookingPaymentOptionsActivity::class.java.simpleName
    }

    private var privileges: PrivilegeResponseModel? = null
    private var pinelabResponseString: String? = ""
    private var pinelabResponseData: CardSaleResponse? = null
    private var seatNumbers: String? = null
    private var lastBookedTicketNumber: String? = ""
    private var lastBookedSeatsNumber: String? = ""
    private var currency: String = ""
    private var isAgentLogin: Boolean = false
    private val isFromDashboard: Boolean = false //fixed
    private val releaseTicketRemarks: String = "release ticket" //fixed
    private var bookTicketTotalFare: String = "0.0"
    private var bookTicketPnr: String = ""
    private val isPhoneBlockedWallet: String = "true" // fixed
    private lateinit var walletUpiAlertDialog: AlertDialog
    private lateinit var upiCreateQRAlertDialog: AlertDialog
    private lateinit var upiAuthSmsAndVPADialog: AlertDialog
    private var walletMobileNo: String = ""
    private var selectedWalletUpiOptionName: String? = null
    private var selectedWalletUpiOptionId: Int? = null
    private var selectedEasebuzzOptionName: String? = null
    private var selectedEasebuzzOptionId: Int? = null
    // agent sub payment
    private var selectedSubPaymentOptionName: String? = null
//    private var selectedSubPaymentOptionId: Int? = null
    private var agentPayViaVPA: String = ""
    private var agentPayViaPhoneNumberSMS: String = ""
    private var branchUserPayViaVPA: String = ""
    private var branchUserPayViaPhoneNumberSMS: String = ""
    
    private var selectedWalletOrUpi: String? = null
    private var selectedEasebuzz: String? = null
    private var walletPaymentOptions = mutableListOf<WalletPaymentOption>()
    private var easebuzzPaymentOptions = mutableListOf<WalletPaymentOption>()
    private var easebuzzPayViaQr: Int? = null
    private var easebuzzPayViaSms: Int? = null
    private var easebuzzPayViaUpi: Int? = null
    private var isPermanentPhoneBooking: Boolean = false
    private var selectedOtherPaymentOption: String? = null
    private var creditDebitCardNo: String? = null
    private var blockingDate: String = ""
    private var blockingTimeHours: String = ""
    private var blockingTimeMins: String = ""
    private var blockingAmPm: String = ""
    private var isPhoneBlocking: Boolean = false
    private var isBima: Boolean? = null
    private var mobileNumber: String? = null
    private var emergencyName: String? = null
    private var email: String? = null
    private var paymentType: Int = PAYMENT_TYPE_CASH//(by default for cash)
    private var lastSelectedPaymentPosition: Int = 0
    private var noOfSeats: String? = "0"
    private var selectedSeatNo: String? = null
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
    private var busType: String? = null
    private var deptTime: String? = null
    private var arrTime: String? = null
    private var deptDate: String? = null
    private var arrDate: String? = null
    private var boardingPoint: String? = null
    private var droppingPoint: String? = null
    private var droppingId: String? = ""
    private var boardingId: String? = ""
    private var srcDest: String? = null
    private var toolbarSubTitleInfo: String? = null
    private lateinit var privilegeResponseModel: PrivilegeResponseModel

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val paymentOptionsList = mutableListOf<SearchModel>()
    private val otherPaymentOptions = mutableListOf<PayGayType>()

    private var bookingCustomRequest = BookingCustomRequest()
    private var selectedSeatDetails =
        ArrayList<com.bitla.ts.domain.pojo.service_details_response.SeatDetail>()
    private var passengerList: ArrayList<PassengerDetailsResult> = ArrayList()
    private var ticket = Ticket("")

    private lateinit var binding: ActivityConfirmPhoneBookingBinding
    private val bookingOptionViewModel by viewModel<BookingOptionViewModel<Any?>>()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private val agentRechargeViewModel by viewModel<AgentRechargeViewModel<Any>>()


    //    private var isOwnRoute = false
    private var toolbarTitle: String = ""
    private var isPhoneBlockTicket = false

    //    private var confirmByType: String = ""
    private var boardingStageTime = ""
    private var droppingStageTime = ""
    private var userId = ""
    private var pnrNumber = ""
    private var totalFare = 0.0
    private var totalFareString = ""
    private var isOwnRoute = false
    private var locale: String? = ""
    private var currencyFormatt: String = ""

    private var pinelabBillingRefNo: String = ""
    private var mServerMessenger: Messenger? = null

    private var isBound: Boolean? = false

    private val PLUTUS_SMART_PACKAGE = "com.pinelabs.masterapp"
    private val PLUTUS_SMART_ACTION = "com.pinelabs.masterapp.SERVER"
    private val MESSAGE_CODE = 1001
    private val BILLING_REQUEST_TAG = "MASTERAPPREQUEST"
    private val BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE"

    var message: Message = Message.obtain(null, MESSAGE_CODE)
    private var isOnBehalfOfAgent = false
    val REQUEST_CODE_INITIALIZE_EZETAP = 10001
    val REQUEST_EZETAP_DEVICE_INFO = 10003
    private var ezetapDeviceId: String = ""
    val REQUEST_CODE_PAY_EZETAP = 10002
    private val agentAccountInfoViewModel by viewModel<AgentAccountInfoViewModel<Any>>()
    private var getAvailableBalance = ""
    private var isCancelledClicked: Boolean = false

    private var availableAgentSubPaymentOptions: List<String> = listOf()
    private var isPhonePeV2Selected: Boolean = false
    private var isEasebuzzSelected: Boolean = false
    private var showPhonePeV2PendingDialog: Boolean = false
    private lateinit var phonePeV2PendingDialog: AlertDialog

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


    override fun initUI() {
        binding = ActivityConfirmPhoneBookingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        privileges = getPrivilegeBase()
        if (IS_PINELAB_DEVICE) {
            if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                paymentType = PAYMENT_TYPE_PINELAB_QR// QR code by default
            }
        }

        if (privileges != null) {
            privilegeResponseModel = privileges as PrivilegeResponseModel

            privilegeResponseModel.apply {
                currencyFormatt = getCurrencyFormat(
                    this@ConfirmPhoneBookingActivity,
                    privilegeResponseModel.currencyFormat
                )
            }
        }


        if (intent.hasExtra(getString(R.string.pnr_number))) {
            pnrNumber = intent.getStringExtra(getString(R.string.pnr_number)).toString()
        }
        if (intent.hasExtra(getString(R.string.select_boarding_stage))) {
            boardingId = intent.getStringExtra(getString(R.string.select_boarding_stage)).toString()
        }
        if (intent.hasExtra(getString(R.string.select_dropping_stage))) {
            droppingId = intent.getStringExtra(getString(R.string.select_dropping_stage)).toString()
        }
        if (intent.hasExtra(getString(R.string.travel_date))) {
            travelDate = intent.getStringExtra(getString(R.string.travel_date)).toString()
        }
        if (intent.hasExtra(getString(R.string.source_id))) {
            sourceId = intent.getStringExtra(getString(R.string.source_id)).toString()
        }
        if (intent.hasExtra(getString(R.string.destination_id))) {
            destinationId = intent.getStringExtra(getString(R.string.destination_id)).toString()
        }
        if (intent.hasExtra(getString(R.string.origin))) {
            source = intent.getStringExtra(getString(R.string.origin)).toString()
        }
        if (intent.hasExtra(getString(R.string.destination))) {
            destination = intent.getStringExtra(getString(R.string.destination)).toString()
        }
        if (intent.hasExtra(getString(R.string.bus_type))) {
            busType = intent.getStringExtra(getString(R.string.bus_type)).toString()
        }
        if (intent.hasExtra("seatNumbers")) {
            seatNumbers = intent.getStringExtra("seatNumbers").toString()
        }
        if (intent.hasExtra(getString(R.string.totalAmount))) {
            val fare = intent.getStringExtra(getString(R.string.totalAmount)).toString()
            totalFareString = fare.toDouble()?.convert(currencyFormatt) ?: ""
            totalFare = fare.toDouble() ?: 0.0
        }
        if (intent.hasExtra("isOnBehalgOfAgent")) {
            isOnBehalfOfAgent = intent.getBooleanExtra("isOnBehalgOfAgent", false)
        }
        Timber.d("confirmBook: $totalFareString")
        getPref()
        agentAccountInfo()
        accountObserver()
        getBookingRequest()
        setPaymentOptionsAdapter()
        handlePrivilegesNew()
        setBoardingDroppingDetails()
        clickListener()
        setObserver()
        getSeatDetails()
        getPassengersList()
        setPassengersAdapter()
        setPassengerInfo()
        setConfirmPhoneBlockTicketObserver()
        phonePeV2StatusObserver()
        confirmPhonePeV2PendingSeatObserver()

        setToolbar()


        if (isBima == true) {
            binding.cardPhoneBooking.gone()
        }

        if (Build.MANUFACTURER.contains("PAX",true)&&privileges?.isEzetapEnabledInTsApp == true && !privilegeResponseModel.isAgentLogin && !isOnBehalfOfAgent) {
            paymentType = PAYMENT_TYPE_EZETAP
            initEzetap()
        }
        lifecycleScope.launch {
            bookingOptionViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
//                   showToast(it)
                }
            }
        }
    }


    private fun initEzetap() {
        val jsonRequest = JSONObject()
        try {
            jsonRequest.put("prodAppKey", privilegeResponseModel.ezetapApiKey ?: "")
            jsonRequest.put("demoAppKey", privilegeResponseModel.ezetapApiKey ?: "")
            jsonRequest.put("merchantName", "Bitlasoft")
            jsonRequest.put("userName", privilegeResponseModel.ezetapUserName ?: "")
            jsonRequest.put("currencyCode", "INR")
            jsonRequest.put("appMode", "DEMO")
            jsonRequest.put("captureSignature", "false")
            jsonRequest.put("prepareDevice", "false")
//            EzeAPI.initialize(this, REQUEST_CODE_INITIALIZE_EZETAP, jsonRequest)
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun handlePrivilegesNew() {
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel.currency != null)
                currency = privilegeResponseModel.currency
            if (privilegeResponseModel.isAgentLogin != null)
                isAgentLogin = privilegeResponseModel.isAgentLogin
            if (privilegeResponseModel.isPermanentPhoneBooking != null)
                isPermanentPhoneBooking = privilegeResponseModel.isPermanentPhoneBooking
            if (privilegeResponseModel.walletPaymentOptions != null) {
                walletPaymentOptions = privilegeResponseModel.walletPaymentOptions

                val upiIndex = walletPaymentOptions.indexOfFirst {
                    it.type.equals(getString(R.string.upi), true)
                }
                // removing UPI options
                if (upiIndex != -1)
                    walletPaymentOptions.removeAt(upiIndex)
            }
            val upiIndex = walletPaymentOptions.indexOfFirst {
                it.type.equals(getString(R.string.upi), true)
            }
            // removing UPI options
            if (upiIndex != -1)
                walletPaymentOptions.removeAt(upiIndex)
            if (privilegeResponseModel.allowToShowWhatsappCheckboxInBookingPage)
                binding.cardWhatsapp.visible() else
                binding.cardWhatsapp.gone()

            val role = getUserRole(loginModelPref, isAgentLogin, this)
            if (privilegeResponseModel.isPhoneBooking && !role.contains(
                    getString(R.string.role_agent),
                    true
                ) && !role.contains(getString(R.string.role_field_officer), true)
            ) {
//                privilegesPhoneBookingForOnlineAgent()
//                privilegesPhoneBookingForOfflineAgent()
//                privilegesPhoneBookingForBranchAndWalkin()
            } else
                binding.cardPhoneBooking.gone()
        }
        
        if (privilegeResponseModel.allowUpiForDirectPgBookingForAgents && paymentOptionsList.isNotEmpty()) {
            binding.layoutPaymentOptions.visible()
            
        } else {
            if (getUserRole(loginModelPref, isAgentLogin, this).contains(getString(R.string.role_agent), true)
                || paymentOptionsList.isEmpty()
            ) {
                paymentType = 1
                binding.layoutPaymentOptions.gone()
            } else {
                paymentType = 1
                binding.layoutPaymentOptions.visible()
            }
        }

        if(!privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser){
            if(privilegeResponseModel.easebuzzSubPaymentOptions?.payViaQr != null)
                easebuzzPayViaQr = privilegeResponseModel.easebuzzSubPaymentOptions?.payViaQr!!
            if(privilegeResponseModel.easebuzzSubPaymentOptions?.payViaSms != null)
                easebuzzPayViaSms = privilegeResponseModel.easebuzzSubPaymentOptions?.payViaSms!!
            if(privilegeResponseModel.easebuzzSubPaymentOptions?.payViaUpi != null)
                easebuzzPayViaUpi = privilegeResponseModel.easebuzzSubPaymentOptions?.payViaUpi!!
        }

        if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.agentSubPaymentOptions != null) {
            availableAgentSubPaymentOptions = privilegeResponseModel.agentSubPaymentOptions ?: emptyList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPinelabs()


        if (selectedSeatDetails.isNotEmpty()) {
            for (i in 0..selectedSeatDetails.size.minus(1)) {
                val seatDetails = selectedSeatDetails[i]

                if (passengerList.isNotEmpty() && passengerList.size == selectedSeatDetails.size) {
                    seatDetails.additionalFare = passengerList[i].additionalFare?.toDouble()
                    if (!passengerList[i].discountAmount.isNullOrEmpty())
                        seatDetails.discountAmount = passengerList[i].discountAmount?.toDouble()
                    seatDetails.passportIssuedDate = passengerList[i].passportIssuedDate
                    seatDetails.passportExpiryDate = passengerList[i].passportExpiryDate
                    seatDetails.placeOfIssue = passengerList[i].placeOfIssue
                    seatDetails.nationality = passengerList[i].nationality
                    seatDetails.idCardNumber = passengerList[i].idCardNumber
                    if(!passengerList[i].idCardType.isNullOrEmpty()){
                        seatDetails.idCardType = passengerList[i].idCardType?.toInt()?:0
                    }
                    seatDetails.isPrimary = passengerList[i].isPrimary
                    seatDetails.age = passengerList[i].age
                    seatDetails.name = passengerList[i].name
                    seatDetails.sex = passengerList[i].sex
                }
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun setPassengerInfo() {
        val totalSeatDetails =
            "${passengerList.size} ${getString(R.string.seats)} ${getString(R.string.details)}"
        binding.tvTotalSeats.text = totalSeatDetails

        // val netAmount = "${getString(R.string.netAmount)} : $currency$totalFare"
        // binding.tvNetAmt.text = netAmount

        val bookingAmount =
            "${getString(R.string.collet_cash)} $currency $totalFareString ${getString(R.string.and)} ${
                getString(R.string.book)
            }"
        Timber.d("confirmBook:0- $bookingAmount")

        binding.btnBook.text = bookingAmount
        binding.btnBook.setBackgroundColor(resources.getColor(R.color.colorPrimary))


        if (passengerList.isNotEmpty() && passengerList[0].contactDetail.isNotEmpty()) {
            mobileNumber = passengerList[0].contactDetail[0].mobileNumber!!
            emergencyName = passengerList[0].contactDetail[0].alternateMobileNumber!!
            email = passengerList[0].contactDetail[0].email!!
            if (mobileNumber != null && mobileNumber?.isNotEmpty()!!) {
                binding.layoutContactDetails.visible()
                binding.tvContactNo.text = mobileNumber
            } else
                binding.layoutContactDetails.gone()
        }
    }

    private fun getPassengersList() {
        passengerList = retrieveSelectedPassengers()
        val bookExtraSeatNoList = mutableListOf<String>()
        val seatNoUpdatedList = mutableListOf<String>()

        passengerList.forEach {
//            Timber.d("selectedSeatNoPassengerList111 - ${it.fare}")
//            totalFare += it.fare.toString()
            Timber.d("selectedSeatNoPassengerList ${it.seatNumber}")

            selectedSeatNo = it.seatNumber
            seatNoUpdatedList.add(selectedSeatNo.toString())
            val commaSeparatedSeatNoUpdated = android.text.TextUtils.join(",", seatNoUpdatedList)
            selectedSeatNo = commaSeparatedSeatNoUpdated
        }

        if (passengerList.any { it.isExtraSeat }) {
            val commaSeparatedExtraSeats = android.text.TextUtils.join(",", bookExtraSeatNoList)
            selectedSeatNo = commaSeparatedExtraSeats
            for (i in 0..passengerList.size.minus(1)) {
                selectedSeatDetails[i].fare = passengerList[i].fare
                selectedSeatDetails[i].number = passengerList[i].seatNumber ?: ""
            }
        }

        binding.cardMealCoupons.gone()
        binding.cardMealTypes.gone()
    }

    private fun getSeatDetails() {
        selectedSeatDetails.clear()
        selectedSeatDetails = retrieveSelectedSeats()
        noOfSeats = selectedSeatDetails.size.toString()

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
        onBeHalfUser = bookingCustomRequest.branch_user
        onBehalfBranch = bookingCustomRequest.branch_id.toString()
        refBookingNo = bookingCustomRequest.reference_no
        onBehalf = bookingCustomRequest.offline_agent_on_behalf.toString()
        onBehalfOnlineAgent = bookingCustomRequest.online_agent_on_behalf.toString()
    }

    private fun setObserver() {

        bookingOptionViewModel.loadingState.observe(this) {
            Timber.d("LoadingState ${it.status}")
            when (it) {
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }
        }


        bookingOptionViewModel.pinelabTransactionData.observe(this) {
            binding.includeProgress.progressBar.gone()


            if (it != null && it.code != null) {
                when (it.code) {
                    200 -> {
                        if (DialogUtils.progressDialog!!.isShowing) {
                            DialogUtils.progressDialog?.dismiss()
                        }
                        if (it.responseCode == 0 && it.data!!.code == null) {
//                            val intent = if(privilegeResponseModel.country.equals("India", true) || privilegeResponseModel.country.equals("Indonesia", true)) {
//                                Intent(this, TicketDetailsActivityCompose::class.java)
//                            } else {
//                                Intent(this, TicketDetailsActivity::class.java)
//                            }
                            val intent=Intent(this, TicketDetailsActivityCompose::class.java)

                            intent.putExtra(
                                "activityName",
                                BookingPaymentOptionsActivity::class.java
                            )
                            intent.putExtra("activityName2", "booking")

                            intent.putExtra(getString(R.string.TICKET_NUMBER), pinelabBillingRefNo)
                            intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                            startActivity(intent)
                            finish()
                        } else if (it.message == "This ticket is released and now its ready for re-booking.") {
                            // do nothing
                            if (DialogUtils.progressDialog!!.isShowing) {
                                DialogUtils.progressDialog?.dismiss()
                            }
                            toast("Payment failed! Please try again")
                        } else {
                            toast(it.message)
                            val jsonObj = JSONObject(pinelabResponseString!!)
                            val reqBody =
                                ReqBodyPinelab(
                                    loginModelPref.api_key,
                                    pnrNumber,
                                    true,
                                    pinelab_response = jsonObj
                                )
                            DialogUtils.showProgressDialog(this)
                            bookingOptionViewModel.pinelabStatusApi(
                                reqBody,
                                pinelab_transaction_status_api
                            )
                            pinelabBillingRefNo = pnrNumber


                        }
                    }

                    400 -> {
                        if (DialogUtils.progressDialog!!.isShowing) {
                            DialogUtils.progressDialog?.dismiss()
                        }
                    }

                    211 -> {
                        if (DialogUtils.progressDialog!!.isShowing) {
                            DialogUtils.progressDialog?.dismiss()
                        }
                        toast("Payment failed! Please try again")
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

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
            } else {
                if (DialogUtils.progressDialog!!.isShowing) {
                    DialogUtils.progressDialog?.dismiss()
                }
                toast("Error occured! Please try again")
            }
        }


        bookingOptionViewModel.walletOtpGeneration.observe(this) {

            binding.includeProgress.progressBar.gone()
            Timber.d("walletOtpGenerationResponse $it")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (::walletUpiAlertDialog.isInitialized) {
                            walletUpiAlertDialog.findViewById<TextInputLayout>(
                                R.id.layout_otp
                            ).visible()
                            
                            walletUpiAlertDialog.findViewById<Button>(
                                R.id.btnConfirm
                            ).text = getString(R.string.confirm_validate)
                            
                            walletUpiAlertDialog.findViewById<TextView>(
                                R.id.tvSubTitle
                            ).gone()
                            
                            walletUpiAlertDialog.findViewById<RecyclerView>(
                                R.id.rvWalletUpi
                            ).gone()
                        }
                        toast(it.message)
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
                        toast(it.message)
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
                    if (::walletUpiAlertDialog.isInitialized)
                        walletUpiAlertDialog.cancel()

//                    val intent = if(privilegeResponseModel.country.equals("India", true) || privilegeResponseModel.country.equals("Indonesia", true)) {
//                        Intent(this, TicketDetailsActivityCompose::class.java)
//                    } else {
//                        Intent(this, TicketDetailsActivity::class.java)
//                    }

                    val intent=Intent(this, TicketDetailsActivityCompose::class.java)

                    intent.putExtra(getString(R.string.TICKET_NUMBER), bookTicketPnr)
                    intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                    intent.putExtra("activityName2", "booking")
                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                    finish()
                } else {
                    if (it.message != null) {
                        it.message.let { it1 ->
                            toast(it1)
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        dashboardViewModel.releaseTicketResponseViewModel.observe(this) {

            if (it != null) {
                if (it.code == 200) {
                    if(!it.result.message.isNullOrEmpty()){
                        toast(it.result.message)
                    } else if (!it.message.isNullOrEmpty()) {
                        it.message.let { it1 -> toast(it1) }
                    }
                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

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
            Timber.d("upCreateQRCodeResponse-X $it")

            if (it != null) {
                when (it.code) {
                    200 -> {
                        toast(it.message)
                        
                        val intent=Intent(this, TicketDetailsActivityCompose::class.java)
                        
                        intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                        intent.putExtra("activityName2", "booking")

                        if ((privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) ||
                            (!privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser)) {
                            toast(it.status)
                            intent.putExtra(getString(R.string.TICKET_NUMBER), it.pnrNumber)
                        } else {
                            toast(it.message)
                            intent.putExtra(getString(R.string.TICKET_NUMBER), it.data?.ticketNumber)
                        }
                        intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                        startActivity(intent)
                        finish()
                    }
                    400 -> {
                        if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                            if (!isCancelledClicked) {
                                callPayStatOfAgentInsRechargStatusApi()
                            }
                        } else if (!privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser) {
                            if (!isCancelledClicked) {
                                callBranchUpiTranxStatusApi()
                            }
                        }
                        else {
                            callUPICheckStatusApi()
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        
        bookingOptionViewModel.ezetapTransactionData.observe(this) {

            binding.includeProgress.progressBar.gone()
            Timber.d("upCreateQRCodeResponse $it")

            if (it != null) {
                if (it.code == 200) {
                    toast(it.message)

//                    val intent = if(privilegeResponseModel.country.equals("India", true) || privilegeResponseModel.country.equals("Indonesia", true)) {
//                        Intent(this, TicketDetailsActivityCompose::class.java)
//                    } else {
//                        Intent(this, TicketDetailsActivity::class.java)
//                    }


                    val intent=Intent(this, TicketDetailsActivityCompose::class.java)
                    intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                    intent.putExtra("activityName2", "booking")

                    intent.putExtra(getString(R.string.TICKET_NUMBER), pnrNumber)
                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                    finish()
                } else if (it.code == 211) {
                    toast(getString(R.string.payment_error_please_try_again_later))
                } else if (it.code == 400) {
                    toast(getString(R.string.error_occured_please_try_again_later))
                }

            } else {
                toast(getString(R.string.server_error))
            }
        }



        bookingOptionViewModel.paytmPosTxnStatusResponse.observe(this) {

            binding.includeProgress.progressBar.gone()
            Timber.d("upCreateQRCodeResponse $it")

            if (it != null) {
                if (it.code == 200) {
                    toast(it.message.toString())

//                    val intent = if(privilegeResponseModel.country.equals("India", true) || privilegeResponseModel.country.equals("Indonesia", true)) {
//                        Intent(this, TicketDetailsActivityCompose::class.java)
//                    } else {
//                        Intent(this, TicketDetailsActivity::class.java)
//                    }


                    val intent=Intent(this, TicketDetailsActivityCompose::class.java)
                    intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                    intent.putExtra("activityName2", "booking")

                    intent.putExtra(getString(R.string.TICKET_NUMBER), pnrNumber)
                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                    finish()
                } else if (it.code == 211) {
                    toast(getString(R.string.payment_error_please_try_again_later))
                } else if (it.code == 400) {
                    toast(getString(R.string.error_occured_please_try_again_later))
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
        loginModelPref = PreferenceUtils.getLogin()

        if (PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false) != null)
            isOwnRoute = PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false)!!


        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }
        if (PreferenceUtils.getPreference(PREF_DROP_OFF_TIME, "") != null) {
            droppingStageTime = PreferenceUtils.getPreference(PREF_DROP_OFF_TIME, "")!!
        }
        if (PreferenceUtils.getPreference(PREF_DROP_OFF, "") != null) {
            droppingPoint = PreferenceUtils.getPreference(PREF_DROP_OFF, "")!!
        }
        if (PreferenceUtils.getPreference(PREF_BOARDING_TIME, "") != null) {
            boardingStageTime = PreferenceUtils.getPreference(PREF_BOARDING_TIME, "")!!
        }
        if (PreferenceUtils.getPreference(PREF_BOARDING_AT, "") != null) {
            boardingPoint = PreferenceUtils.getPreference(PREF_BOARDING_AT, "")!!
        }
        if (PreferenceUtils.getPreference(PREF_BOARDING_TIME, "") != null) {
            arrTime = PreferenceUtils.getPreference(PREF_BOARDING_TIME, "")!!
        }
        if (PreferenceUtils.getPreference(PREF_BOARDING_DATE, "") != null) {
            arrDate = PreferenceUtils.getPreference(PREF_BOARDING_DATE, "")!!
        }
        if (PreferenceUtils.getPreference(PREF_DROP_OFF_DATE, "") != null) {
            deptDate = PreferenceUtils.getPreference(PREF_DROP_OFF_DATE, "")!!
        }
        if (PreferenceUtils.getPreference(PREF_BOARDING_TIME, "") != null) {
            deptTime = PreferenceUtils.getPreference(PREF_BOARDING_TIME, "")!!
        }
    }

    private fun setToolbar() {
        srcDest = "$source-$destination"
        binding.toolbar.toolbarHeaderText.text = srcDest

        if (travelDate.isNotEmpty()) {
            toolbarSubTitleInfo = "$travelDate | $deptTime | $busType"
            binding.toolbar.toolbarSubtitle.text = toolbarSubTitleInfo
        }
        binding.toolbar.tvCurrentHeader.text = getString(R.string.phone_booking_title)
    }

    private fun clickListener() {
        binding.layoutBookingDetailsFixed.setOnClickListener(this)
        binding.cardPhoneBooking.setOnClickListener(this)
        binding.btnBook.setOnClickListener(this)
        binding.toolbar.imgBack.setOnClickListener(this)
    }
    
    private fun agentAccountInfo() {
        val agentRequest = AgentAccountInfoRequest(
            bccId.toString(),
            format_type,
            agent_account_info,
            com.bitla.ts.domain.pojo.account_info.request.ReqBody(
                loginModelPref.api_key,
                locale = locale
            )
        )
        
        agentAccountInfoViewModel.agentAccountInfoAPI(
            agentAccountInfoRequest = agentRequest,
            agentId = "",
            branchId = "",
            apiType = agent_account_info
        )
    }
    
    private fun accountObserver() {
        agentAccountInfoViewModel.agentInfo.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        getAvailableBalance = it.available_balance
                        setPaymentOptionsAdapter()
                    }
                    401 -> {
                        showUnauthorisedDialog()
                    }
                    else -> {
                        it.result.message?.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }
    
    
    private fun setUpiForDirectPgBookingForAgents() {
        /*if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
            val cash = SearchModel()
            cash.id = "1"
            cash.name = getString(R.string.cash)
            paymentOptionsList.add(cash)
            if(PreferenceUtils.getSubAgentRole()!="true"){
            
            val walletAgent = SearchModel()
            walletAgent.id = "15"
            walletAgent.name = "${getString(R.string.wallet)} (Bal: $ $getAvailableBalance)"
            paymentOptionsList.add(walletAgent)
            
            val netAmtAgent = SearchModel()
            netAmtAgent.id = "16"
            netAmtAgent.name = getString(R.string.net_amt_less_off_comm)
            paymentOptionsList.add(netAmtAgent)
            
            val fullAmountAgent = SearchModel()
            fullAmountAgent.id = "17"
            fullAmountAgent.name = getString(R.string.full_amount)
            paymentOptionsList.add(fullAmountAgent)
        }}
        else if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser) {
            val cash = SearchModel()
            cash.id = "1"
            cash.name = getString(R.string.cash)
            paymentOptionsList.add(cash)
        }*/

        if (privileges != null) {
            val availablePaymentOptionsListForAgent=privileges?.agentPaymentOptions

            if(availablePaymentOptionsListForAgent != null){
                paymentOptionsList.clear()

                if ("CASH" in availablePaymentOptionsListForAgent) {
                    val cash = SearchModel().apply {
                        id = "1"
                        name = getString(R.string.cash)
                    }
                    paymentOptionsList.add(cash)
                } else {
                    lastSelectedPaymentPosition = -1
                }

                if (PreferenceUtils.getSubAgentRole() != "true") {
                    if ("PAY_FROM_WALLET" in availablePaymentOptionsListForAgent) {
                        val walletAgent = SearchModel().apply {
                            id = "15"
                            name = "${getString(R.string.wallet)} (Bal: $ $getAvailableBalance)"
                        }
                        paymentOptionsList.add(walletAgent)
                    }

                    if ("PAY_NET_AMOUNT" in availablePaymentOptionsListForAgent) {
                        val netAmtAgent = SearchModel().apply {
                            id = "16"
                            name = getString(R.string.net_amt_less_off_comm)
                        }
                        paymentOptionsList.add(netAmtAgent)
                    }

                    if ("PAY_FULL_AMOUNT" in availablePaymentOptionsListForAgent) {
                        val fullAmountAgent = SearchModel().apply {
                            id = "17"
                            name = getString(R.string.full_amount)
                        }
                        paymentOptionsList.add(fullAmountAgent)
                    }
                }
            }
        }
    }

    private fun setSubPaymentOptionsAgents() {
        walletPaymentOptions.apply {
            clear()
            if (availableAgentSubPaymentOptions.isNotEmpty()) {
                if (PaymentTypes.QR in availableAgentSubPaymentOptions) {
                    add(WalletPaymentOption(getString(R.string.pay_via_qr), AgentPaymentOptions.PAY_VIA_QR, ""))
                }
                if (PaymentTypes.SMS in availableAgentSubPaymentOptions) {
                    add(WalletPaymentOption(getString(R.string.pay_via_sms), AgentPaymentOptions.PAY_VIA_SMS, ""))
                }
                if (PaymentTypes.VPA in availableAgentSubPaymentOptions) {
                    add(WalletPaymentOption(getString(R.string.pay_via_upi), AgentPaymentOptions.PAY_VIA_UPI, ""))
                }
                if (PaymentTypes.PHONEPE_V2 in availableAgentSubPaymentOptions) {
                    add(WalletPaymentOption(getString(R.string.phonepe_v2), AgentPaymentOptions.PHONEPE_V2, ""))
                }
            }
        }
    }

    private fun setPaymentOptionsAdapter() {
        paymentOptionsList.clear()
        
        if (::privilegeResponseModel.isInitialized) {

            if (IS_PINELAB_DEVICE) {
                if (privilegeResponseModel.allowUserToUsePinelabDevicesForUpiPayment) {
                    val pinelabQR = SearchModel()
                    pinelabQR.id = PAYMENT_TYPE_PINELAB_QR.toString()
                    pinelabQR.name = getString(R.string.pinelab_QR)

                    paymentOptionsList.add(pinelabQR)


                    val pinelabCreditDebit = SearchModel()
                    pinelabCreditDebit.id = PAYMENT_TYPE_PINELAB_CREDIT_DEBIT.toString()
                    pinelabCreditDebit.name = getString(R.string.pinelab_debitcard)


                    paymentOptionsList.add(pinelabCreditDebit)
                }
            }
            if (privilegeResponseModel.allowCashCreditOptionsInBooking) {
                val cash = SearchModel()
                cash.id = "1"
                cash.name = getString(R.string.cash)

                val creditDebitCard = SearchModel()
                creditDebitCard.id = "2"
                creditDebitCard.name = getString(R.string.credit_debit)

                paymentOptionsList.add(cash)
                paymentOptionsList.add(creditDebitCard)
            }

            if (!privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowWalletAndUpiOptionsInBookingPage != null
                && privilegeResponseModel.allowWalletAndUpiOptionsInBookingPage
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

            if (privilegeResponseModel.isEzetapEnabledInTsApp && !privilegeResponseModel.isAgentLogin && !isOnBehalfOfAgent) {
                paymentOptionsList.clear()

                //Ezetap Radio Option
                val ezetap = SearchModel()
                ezetap.id = PAYMENT_TYPE_EZETAP
                ezetap.name = getString(R.string.ezetap)
                paymentOptionsList.add(ezetap)


                val cash = SearchModel()
                cash.id = "1"
                cash.name = getString(R.string.cash)
                paymentOptionsList.add(cash)


            }
            
            setUpiForDirectPgBookingForAgents()

            // As discussed with Naresh & Faraz, easebuzz pg is renamed as UPI
            if (!privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser &&
                privilegeResponseModel.tsPrivileges?.allowEasebuzzInTs == true
                ){
                val easebuzz = SearchModel()
                easebuzz.id = "11"
                easebuzz.name = getString(R.string.upi_caps)
                paymentOptionsList.add(easebuzz)
            }

            if (privilegeResponseModel.isPaytmPosEnabled && !privilegeResponseModel.isAgentLogin && !isOnBehalfOfAgent) {
                paymentOptionsList.clear()

                //Paytm Radio Option
                val paytm = SearchModel()
                paytm.id = PAYMENT_TYPE_PAYTM
                paytm.name = getString(R.string.paytm)
                paymentOptionsList.add(paytm)


                val cash = SearchModel()
                cash.id = "1"
                cash.name = getString(R.string.cash)
                paymentOptionsList.add(cash)

            }
        }

        if (!privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
            if(privilegeResponseModel.isAgentLogin || isOnBehalfOfAgent){
                paymentOptionsList.clear()
                paymentType = 1
            }
        }

        if (totalFareString.toDoubleOrNull() == 0.0) {
            paymentOptionsList.clear()
            lastSelectedPaymentPosition = 0

            val cash = SearchModel()
            cash.id = "1"
            cash.name = getString(R.string.cash)
            paymentOptionsList.add(cash)
        }
        
        Timber.d("paymentOptionsList = ${paymentOptionsList.size}")
        
        if (paymentOptionsList.size > 0) {
            layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvPaymentOptions.layoutManager = layoutManager
            val filterAdapter = FilterAdapter(
                this,
                this,
                paymentOptionsList,
                lastSelectedPaymentPosition,
                false
            )
            binding.rvPaymentOptions.adapter = filterAdapter
        }
        
    }

    private fun setPassengersAdapter() {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPassengers.layoutManager = layoutManager
        val bookPassengersAdapter =
            BookPassengersAdapter(this, this, passengerList)
        binding.rvPassengers.adapter = bookPassengersAdapter
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

            R.id.cardPhoneBooking -> {

                DialogUtils.phoneBlockingDialog(this, this, isPermanentPhoneBooking)
            }

            R.id.btnBook -> {
                if (lastSelectedPaymentPosition == -1 && paymentType == 1) {
                    toast(getString(R.string.please_select_the_payment_method))
                    return
                }

                if (isPhoneBlockTicket) {
                    if (isNetworkAvailable()) {
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
                    } else {
                        noNetworkToast()
                    }
                } else {
                    if (binding.btnBook.text.toString() != getString(R.string.do_phone_booking)) {
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
                    } else {
                        isPhoneBlocking = true
                        if (isNetworkAvailable()) {
                            if (paymentType == PAYMENT_TYPE_PINELAB_QR) {
                                pinelabPaymentType(PAYMENT_QR)

                            } else if (paymentType == PAYMENT_TYPE_PINELAB_CREDIT_DEBIT) {
                                pinelabPaymentType(PAYMENT_DEBIT_CREDIT)

                            }else if(paymentType == PAYMENT_TYPE_PAYTM && privilegeResponseModel.isPaytmPosEnabled){
                                generatePaytmPayment()
                            }else if (paymentType == PAYMENT_TYPE_EZETAP) {
                                EzePayAPI()
                            }  else {
                                confirmPhoneBlockTicketApi()

                            }
                        } else
                            noNetworkToast()
                    }
                }
            }
        }
    }




    private fun generatePaytmPayment() {

        var ticketNumber: String? = ""
        var finalfare = intent.getStringExtra(getString(R.string.totalAmount)).toString()
        ticketNumber = pnrNumber

        val edcPackage="com.paytm.pos.debug"
//       val edcPackage="com.paytm.pos"

        val packageName=packageName
        val payDeepLink="paytmedc://paymentV2"
        val callBackAction="com.paytm.pos.payment.CALL_BACK_RESULT_PHONE_BLOCK"
        val orderId= ticketNumber
        val payMode="all"
        val amount=finalfare.toDouble().toInt()*100
        val deepLink= "paytmedc://paymentV2?" + "callbackAction=" + callBackAction + "&stackClear=true" +
                "&callbackPkg=" + packageName + "&callbackDl=" +  payDeepLink + "&requestPayMode=" + payMode +
                "&orderId=" + orderId + "&amount=" + amount


        val launchIntent=packageManager.getLaunchIntentForPackage(edcPackage)
        if (launchIntent != null) {
            launchIntent.putExtra("deeplink", deepLink)
            startActivity(launchIntent)
        }

    }


    private fun EzePayAPI() {


        var ticketNumber: String? = ""
        var finalfare = intent.getStringExtra(getString(R.string.totalAmount)).toString()
        ticketNumber = pnrNumber


        var obj = JSONObject()
        obj.put("appKey", privilegeResponseModel.ezetapApiKey ?: "")
        obj.put("externalRefNumber", ticketNumber)
        obj.put("username", privilegeResponseModel.ezetapUserName ?: "")
        if (finalfare.contains("₹")) {
            finalfare = finalfare.replace("₹", "")
        }
        obj.put("amount", finalfare)
        obj.put("mode", "ALL")
        val pushObj = JsonObject()
        pushObj.addProperty("deviceId", "$ezetapDeviceId|ezetap_android")
        obj.put("pushTo", pushObj)




//        EzeAPI.pay(this, REQUEST_CODE_PAY_EZETAP, obj)
    }

    private fun pinelabPaymentType(type: Int) {
        var totalFaree: String = ""
        var passengerPhoneNo: String? = ""
        var ticketNumber: String? = ""

        val finalfare = intent.getStringExtra(getString(R.string.totalAmount)).toString()
        // val ticketData = (bookTicketFullResponse as BookTicketFullResponse)
        totalFaree = (finalfare.toDouble() * 100).toInt().toString()
        passengerPhoneNo = mobileNumber
        ticketNumber = pnrNumber
        /*  } else {
              val ticketData = (bookTicketFullResponse as BookExtraSeatResponse)
              if(ticketData.total_fare.contains("₹")){
                  totalFaree = (ticketData.total_fare.substringAfter("₹").toDouble() * 100).toInt().toString()
              }else{
                  totalFaree = (ticketData.total_fare.toDouble() * 100).toInt().toString()
              }
              passengerPhoneNo = ticketData.passenger_details[0].mobile
              ticketNumber = ticketData.ticketNumber
          }*/

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

        pinelabPayment(json)
    }

    private fun initPinelabs() {
        val intent = Intent()
        intent.setAction(PLUTUS_SMART_ACTION)
        intent.setPackage(PLUTUS_SMART_PACKAGE)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }


    private fun pinelabPayment(json: JSONObject) {
        if (isBound!!) {
            val data = Bundle()
            val value = json.toString()
            data.putString(BILLING_REQUEST_TAG, value)
            message.data = data
            try {
                message.replyTo = Messenger(IncomingHandler(this))
                mServerMessenger!!.send(message)
            }
            catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        } else {
            toast("Pinelab device connection error!")
        }

    }

    override fun onClickOfNavMenu(position: Int) {

    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        intent?.let {
            if (intent.extras != null) {
                val keys= intent.extras!!.keySet()
                for(key in keys){
                    val value = intent.extras!!.get(key)
                }

                if (it.hasExtra("deeplink")) {
                    val dl = it.getStringExtra("deeplink")
                    val uri = Uri.parse(dl)
                    when (uri.host) {
                        "paymentV2" -> {
                            paytmPosTxtStatusApi(uri)
                        }
                    }
                }
            }


        }
    }



    private fun paytmPosTxtStatusApi(uri: Uri) {

        val paytmObj = createJsonFromUri(uri.toString())


        val reqBody = PaytmPosTxnStatusRequest(
            apiKey = loginModelPref.api_key?:"",
            connectingPnrNumber = "",
            destination = destinationId,
            isExtraSeat = false,
            isRoundTrip = false,
            isSendSms = false,
            origin = sourceId,
            paytmPosResponse = paytmObj,
            paytmPosPaymentType = 59,
            pnrNumber = pnrNumber,
            reservationId = ""
        )


        bookingOptionViewModel.paytmPosTxnStatusApi(
            paytmPosTxnStatusRequest = reqBody,
            apiType = ""
        )
    }


    fun createJsonFromUri(uriString: String): JsonObject {
        // Parse the URI
        val uri = Uri.parse(uriString)

        // Create a JsonObject
        val jsonObject = JsonObject()

        // Extract query parameters dynamically and put them in the JsonObject
        val queryParameterNames = uri.queryParameterNames
        for (key in queryParameterNames) {
            jsonObject.addProperty(key, uri.getQueryParameter(key))
        }

        return jsonObject
    }








    override fun onClick(view: View, position: Int) {
        Timber.d("$tag radioTag ${view.tag}")

        isPhonePeV2Selected = false
        isEasebuzzSelected = false
        if (paymentOptionsList.isNotEmpty()) {
            paymentType = paymentOptionsList[position].id.toString().toInt()

            if (paymentOptionsList[position].name == getString(R.string.credit_debit)) {
                DialogUtils.creditDebitDialog(this, this)
            } else if (paymentOptionsList[position].name == getString(R.string.others)) {
                otherPaymentOptions.clear()
                /*val payGayType = PayGayType()
                payGayType.payGayTypeName = getString(R.string.notAvailable)
                otherPaymentOptions.add(payGayType)*/

                if (::privilegeResponseModel.isInitialized
                    && !privilegeResponseModel.othersPaymentOption.isNullOrEmpty()
                ) {
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
//                if (!walletPaymentOptions.contains(WalletPaymentOption("UPI", 2, ""))) {
//                    walletPaymentOptions.add(0, WalletPaymentOption("UPI", 2, ""))
//                }
                walletUpiAlertDialog = DialogUtils.walletUpiDialog(
                    this,
                    walletPaymentOptions,
                    this,
                    mobile = passengerMobile
                )
            } else if (paymentOptionsList[position].name == this.getString(R.string.pinelab_QR)) {
                paymentType = paymentOptionsList[position].id.toString().toInt()

            } else if (paymentOptionsList[position].name == this.getString(R.string.pinelab_debitcard)) {
                paymentType = paymentOptionsList[position].id.toString().toInt()

            } else if (paymentOptionsList[position].name == this.getString(R.string.paytm)) {
                paymentType = paymentOptionsList[position].id.toString().toInt()
            }else if (paymentOptionsList[position].name == this.getString(R.string.ezetap)) {
                paymentType = paymentOptionsList[position].id.toString().toInt()

            }
            
            else if (paymentOptionsList[position].name?.split(" ")?.get(0) == getString(R.string.wallet)) {
               
                if (totalFare > (getAvailableBalance.toDoubleOrNull() ?: 0.0)) {

                    setSubPaymentOptionsAgents()
                    
                    walletUpiAlertDialog = DialogUtils.walletUpiDialog(
                        context = this,
                        walletPaymentOption = walletPaymentOptions,
                        dialogSingleButtonListener = this,
                        mobile = ""
                    )
                    
                    walletUpiAlertDialog.apply {
                        findViewById<TextView>(R.id.tvTitle).text = getString(R.string.wallet)
                    }
                }else{
                    toast(getString(R.string.insufficient_wallet_balance))
                }
            }

            else if (paymentOptionsList[position].name == getString(R.string.net_amt_less_off_comm)) {

                setSubPaymentOptionsAgents()
                
                walletUpiAlertDialog = DialogUtils.walletUpiDialog(
                    context = this,
                    walletPaymentOption = walletPaymentOptions,
                    dialogSingleButtonListener = this,
                    mobile = ""
                )
                
                walletUpiAlertDialog.apply {
                    findViewById<TextView>(R.id.tvTitle).text = getString(R.string.net_amt_less_off_comm)
                }
            }

            else if (paymentOptionsList[position].name == getString(R.string.full_amount)) {

                setSubPaymentOptionsAgents()
                
                walletUpiAlertDialog = DialogUtils.walletUpiDialog(
                    context = this,
                    walletPaymentOption = walletPaymentOptions,
                    dialogSingleButtonListener = this,
                    mobile = ""
                )
                
                walletUpiAlertDialog.apply {
                    findViewById<TextView>(R.id.tvTitle).text = getString(R.string.full_amount)
                }
            }

            else if (paymentOptionsList[position].name == getString(R.string.upi_caps)) {

                easebuzzPaymentOptions.apply {
                    clear()
                    add(0, WalletPaymentOption(getString(R.string.pay_via_qr_user), EasebuzzPaymentOptions.PAY_VIA_QR, ""))
                    add(1, WalletPaymentOption(getString(R.string.pay_via_sms_user), EasebuzzPaymentOptions.PAY_VIA_ONLINE_LINK, ""))
                    add(2, WalletPaymentOption(getString(R.string.pay_via_upi_user), EasebuzzPaymentOptions.PAY_VIA_UPI, ""))
                }

                walletUpiAlertDialog = DialogUtils.easebuzzDialog(
                    context = this,
                    easebuzzPaymentOption = easebuzzPaymentOptions,
                    dialogSingleButtonListener = this,
                    mobile = ""
                )

                walletUpiAlertDialog.apply {
                    findViewById<TextView>(R.id.tvTitle).text = getString(R.string.upi_caps)
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

    override fun onLeftButtonClick() {
    }

    override fun onRightButtonClick() {

        if (paymentType == PAYMENT_TYPE_PINELAB_QR) {
            DialogUtils.showProgressDialog(this)
            pinelabPaymentType(PAYMENT_QR)
        } else if (paymentType == PAYMENT_TYPE_PINELAB_CREDIT_DEBIT) {
            DialogUtils.showProgressDialog(this)
            pinelabPaymentType(PAYMENT_DEBIT_CREDIT)
        }else if(paymentType == PAYMENT_TYPE_PAYTM && privilegeResponseModel.isPaytmPosEnabled){
            generatePaytmPayment()
        }
        else if (paymentType == PAYMENT_TYPE_EZETAP) {
            EzePayAPI()
        } else {
            
            if (privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                confirmPhoneBlockTicketApi()
                
                Timber.d("selectedWalletUpiOptionId $selectedWalletUpiOptionId")
                Timber.d("selectedWalletUpiOptionId-Sub $selectedSubPaymentOptionName")
                Timber.d("selectedWalletUpiOptionId-Sub $agentPayViaVPA")
                Timber.d("selectedWalletUpiOptionId-Sub $agentPayViaPhoneNumberSMS")
            } else {
                if (isBima != null && isBima == true) {
                    confirmBimaPhoneBlockTicketApi()
                } else {
                    confirmPhoneBlockTicketApi()
                }
            }
        }
    }

    private fun walletOtpGenerationApi() {
        val reqBody = com.bitla.ts.domain.pojo.wallet_otp_generation.request.ReqBody(
            amount = totalFareString,
            api_key = loginModelPref.api_key,
            is_from_middle_tier = is_from_middle_tier.toBoolean(),
            pnr_number = pnrNumber,
            wallet_mobile = walletMobileNo,
            wallet_type = selectedWalletUpiOptionId.toString(),
            locale = locale,
            is_resend_otp = true
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
            amount = totalFareString,
            api_key = loginModelPref.api_key,
            is_from_middle_tier = is_from_middle_tier.toBoolean(),
            otp_number = otp,
            phone_blocked = isPhoneBlockedWallet,
            pnr_number = pnrNumber,
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


    private fun confirmPhoneBlockTicketApi() {
        binding.includeProgress.progressBar.visible()

        if (isNetworkAvailable()) {
            var payGayType: Int? = null
            if (isAgentLogin) {
                payGayType = if (isPhonePeV2Selected) PayGayTypes.PHONEPE_V2 else if (isEasebuzzSelected) PayGayTypes.EASEBUZZ else null
            }
            val deviceInfo = getDeviceInfo(loginModelPref)

            var reqBody: ReqBody? = null
            reqBody = ReqBody(
                apiKey = loginModelPref.api_key,
                paymentType = if (privilegeResponseModel.allowUpiForDirectPgBookingForAgents && paymentType == 1) 1
                else if (!privilegeResponseModel.allowUpiForDirectPgBookingForAgents) paymentType
                else 0,
                pnrNumber = pnrNumber,
                ticket = Ticket(creditDebitCardNo.toString()),
                travelBranch = "",
                userId = "",
                locale = locale,
                agentPaymentType = if (paymentType == 1) "" else if (isAgentLogin) "$paymentType" else "",
                agentSubPaymentType = if (paymentType == 1) "" else if(isAgentLogin) "$selectedSubPaymentOptionName" else "",
                agentPhone = agentPayViaPhoneNumberSMS,
                agentVpa = agentPayViaVPA,
                subPaymentType = if(!isAgentLogin) selectedSubPaymentOptionName else "",
                branchVpa = branchUserPayViaVPA,
                branchPhone = branchUserPayViaPhoneNumberSMS,
                payGayType = payGayType,
                deviceInfo = deviceInfo
            )
            
            bookingOptionViewModel.confirmPhoneBlockTicketApi(
                confirmPhoneBlockTicketReq = reqBody,
                apiType = confirm_phone_block_ticket_method_name
            )
        } else
            noNetworkToast()
    }
    
    private fun confirmBimaPhoneBlockTicketApi() {
        
        if (isNetworkAvailable()) {
            var reqBody: ReqBody? = null
            reqBody = ReqBody(
                apiKey = loginModelPref.api_key,
                paymentType = if (!privilegeResponseModel.allowUpiForDirectPgBookingForAgents) paymentType else 0,
                pnrNumber = pnrNumber,
                ticket = Ticket(creditDebitCardNo.toString()),
                travelBranch = "",
                userId = "",
                locale = locale,
                agentPaymentType = "$selectedWalletUpiOptionId",
                agentSubPaymentType = selectedSubPaymentOptionName,
                subPaymentType = "",
                branchVpa = "",
                branchPhone = ""
            )
            bookingOptionViewModel.confirmBimaPhoneBlockTicketApi(
                reqBody
            )
        } else
            noNetworkToast()
    }
    
    private fun setConfirmPhoneBlockTicketObserver() {
        
        bookingOptionViewModel.confirmPhoneBlockTicket.observe(this) {
            if (it != null) {
                binding.includeProgress.progressBar.gone()
                Timber.d("bookTicketFullResponse $it")
                
                if (it.code == 200) {
                    
                    if (selectedSubPaymentOptionName == "QR"
                        && it.result?.status.equals(getString(R.string.pending), ignoreCase = true)
                    ) {
                        
                        if (it.result?.agentRechargeQrResp?.isNotEmpty() == true) {
                            try {
                                upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                                    context = this,
                                    isFromAgentRechargePG = true,
                                    dialogSingleButtonListener = this
                                )
                                
                                val base64String = it.result.agentRechargeQrResp.substring(22)
                                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                                val decodedImage =
                                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                
                                if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {
                                    
                                    upiCreateQRAlertDialog.findViewById<ImageView>(
                                        R.id.qr_code_image
                                    ).setImageBitmap(decodedImage)
                                }
                                
                                callPayStatOfAgentInsRechargStatusApi()
                            } catch (e: Exception) {
                                toast(getString(R.string.something_went_wrong))
                            }
                        } else if (it.result?.branchUpiQrResp?.isNotEmpty() == true) {
                            try {
                                upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                                    context = this,
                                    isFromAgentRechargePG = false,
                                    dialogSingleButtonListener = this,
                                    isFromBranchUser = true
                                )

                                val base64String = it.result.branchUpiQrResp?.substring(22)
                                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                                val decodedImage =
                                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                                if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {

                                    upiCreateQRAlertDialog.findViewById<ImageView>(
                                        R.id.qr_code_image
                                    ).setImageBitmap(decodedImage)
                                }

                                callBranchUpiTranxStatusApi()
                            } catch (e: Exception) {
                                toast(getString(R.string.something_went_wrong))
                            }
                        } else {
                                toast(getString(R.string.something_went_wrong))
                        }
                        
                    }
                    else if (selectedSubPaymentOptionName.toString() == "SMS"
                        && it.result?.status.equals(getString(R.string.pending), ignoreCase = true)
                    ) {
                        
                        upiAuthSmsAndVPADialog =  DialogUtils.upiAuthSmsAndVPADialog(
                            context = this,
                            isSmsAuth = true,
                            dialogSingleButtonListener = this,
                            isFromBranchUser = privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser && !privilegeResponseModel.isAgentLogin
                        )
                        if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                            callPayStatOfAgentInsRechargStatusApi()
                        } else if (privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser) {
                            callBranchUpiTranxStatusApi()
                        }
                        
                    }
                    else if (selectedSubPaymentOptionName == "VPA"
                        && it.result?.status.equals(getString(R.string.pending), ignoreCase = true)
                    ) {
                        
                        upiAuthSmsAndVPADialog = DialogUtils.upiAuthSmsAndVPADialog(
                            context = this,
                            isSmsAuth = false,
                            dialogSingleButtonListener = this,
                            isFromBranchUser = privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser && !privilegeResponseModel.isAgentLogin
                        )

                        if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                            callPayStatOfAgentInsRechargStatusApi()
                        } else if (privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser) {
                            callBranchUpiTranxStatusApi()
                        }
                    }
                    else if (selectedSubPaymentOptionName == PaymentTypes.PHONEPE_V2 && it.result?.isPhonePeV2Payment == true
                        && it.result?.status.equals(getString(R.string.pending), ignoreCase = true)
                    ) {
                        openPhonePeV2(
                            context = this,
                            activityResultLauncher = activityResultLauncher,
                            isLiveEnvironment = it.result.isLiveEnvironment ?: false,
                            merchantId = it.result.merchantId,
                            flowId = pnrNumber,
                            token = it.result.token,
                            orderId = it.result.orderId
                        )
                    }
                    else {
                        val intent= Intent(this, TicketDetailsActivityCompose::class.java)
                        intent.apply {
                            putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                            putExtra("activityName2", "booking")
                            putExtra(getString(R.string.TICKET_NUMBER), it.result?.ticketNumber)
                            putExtra(getString(R.string.toolbar_title), toolbarTitle)
                        }
                        startActivity(intent)
                        finish()
                    }
                } else {
                    it.message?.let { it1 -> toast(it1) }
                }
            } else {
//                toast(getString(R.string.server_error))
            }
        }
    }

    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        isCancelledClicked = false
        showPhonePeV2PendingDialog = true
        callPhonePeV2StatusApi()
    }

    private fun callPhonePeV2StatusApi() {
        if (isNetworkAvailable())
            agentRechargeViewModel.getPhonePeV2Status(
                apiKey = loginModelPref.api_key,
                orderId = pnrNumber
            )
        else
            noNetworkToast()
    }

    private fun phonePeV2StatusObserver() {
        agentRechargeViewModel.phonePeV2StatusResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        when (it.status) {
                            "COMPLETED" -> {
                                closePhonePeV2PendingDialog()
                                hitPhonePeV2SuccessConPay()
                            }
                            "PENDING" -> {
                                if (!isCancelledClicked) {
                                    Handler(Looper.getMainLooper()).postDelayed(1000) {
                                        callPhonePeV2StatusApi()
                                    }
                                }
                                if (showPhonePeV2PendingDialog) {
                                    showPhonePeV2PendingDialog = false
                                    phonePeV2PendingDialog = DialogUtils.phonePeV2PendingDialog(
                                        this,
                                        this,
                                        getString(R.string.payment_pending),
                                        getString(R.string.payment_pending_msg),
                                        getString(R.string.payment_pending_desc),
                                        getString(R.string.cancel_payment),
                                    )
                                }
                            }
                            "FAILED" -> {
                                closePhonePeV2PendingDialog()
                                toast(getString(R.string.payment_failed))
                            }
                            else -> {
                                closePhonePeV2PendingDialog()
                                toast(getString(R.string.server_error))
                            }
                        }
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        toast(getString(R.string.something_went_wrong))
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun closePhonePeV2PendingDialog() {
        if (::phonePeV2PendingDialog.isInitialized) {
            phonePeV2PendingDialog.dismiss()
        }
    }

    private fun hitPhonePeV2SuccessConPay() {
        if (isNetworkAvailable()) {
            bookingOptionViewModel.confirmPhonePeV2PendingSeat(
                pnrNumber = pnrNumber
            )
        } else {
            noNetworkToast()
        }
    }

    private fun confirmPhonePeV2PendingSeatObserver() {
        bookingOptionViewModel.confirmPhonePeV2PendingSeatResponse.observe(this) {
            if (it != null) {
                val intent = Intent(this, TicketDetailsActivityCompose::class.java)
                intent.putExtra("activityName2", "booking")
                intent.putExtra(getString(R.string.TICKET_NUMBER), pnrNumber)

                startActivity(intent)
                finish()
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun callUPICreateQrCodeApi() {
        val reqBody = com.bitla.ts.domain.pojo.upi_create_qr.request.ReqBody(
            amount = totalFareString,
            apiKey = loginModelPref.api_key,
            isFromMiddleTier = is_from_middle_tier.toBoolean(),
            pnrNumber = pnrNumber,
            userNumber = walletMobileNo,
            upiType = 2

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
            pnrNumber = pnrNumber,
            isSendSms = true
        )
        
        bookingOptionViewModel.upiTranxStatusApi(
            reqBody = reqBody,
            apiType = upi_tranx_status
        )
    }


    private fun callPayStatOfAgentInsRechargStatusApi() {
        if (isNetworkAvailable()) {
            bookingOptionViewModel.getPayStatOfAgentInsRechargStatusApi(
                apiKey = loginModelPref.api_key,
                pnrNumber = pnrNumber,
                phone = agentPayViaPhoneNumberSMS,
//                amount = totalFareString,
                isFromAgentRecharge = "${privilegeResponseModel.allowUpiForDirectPgBookingForAgents}"
            )
        } else {
            noNetworkToast()
        }
    }

    private fun callBranchUpiTranxStatusApi() {
        if (isNetworkAvailable()) {
            bookingOptionViewModel.getBranchUpiTranxStatusApi(
                apiKey = loginModelPref?.api_key ?: "",
                pnrNumber = pnrNumber,
                branchPhone = branchUserPayViaPhoneNumberSMS
            )
        } else {
            noNetworkToast()
        }
    }
    
    
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

                getString(R.string.phone_blocking_use_btn) -> {
                    binding.layoutPaymentOptions.gone()
                    binding.cardPhoneBooking.setCardBackgroundColor(resources.getColor(R.color.colorBlue1))
                    binding.btnBook.text = getString(R.string.do_phone_booking)
                    blockingDate = args[1].toString()
                    blockingTimeHours = args[2].toString()
                    blockingTimeMins = args[3].toString()

                    blockingAmPm =
                        if (blockingTimeHours.isNotEmpty() && blockingTimeHours.toDouble()
                                .toInt() >= 12
                        )
                            getString(R.string.pm)
                        else
                            getString(R.string.am)

                }

                getString(R.string.phone_blocking_cancel_btn) -> {
                    binding.layoutPaymentOptions.visible()

                    val bookingAmount =
                        "${getString(R.string.collet_cash)} $currency $totalFareString ${getString(R.string.and)} ${
                            getString(
                                R.string.book
                            )
                        }"
                    binding.btnBook.text = bookingAmount

                    binding.cardPhoneBooking.setCardBackgroundColor(resources.getColor(R.color.button_secondary_bg))
                }
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        isPhonePeV2Selected = false
        isEasebuzzSelected = false
        if (str.isNotEmpty()) {
            if (str == getString(R.string.goBack)) {
                lastSelectedPaymentPosition = 0
                paymentType = 1
                if (IS_PINELAB_DEVICE) {
                    if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                        paymentType = PAYMENT_TYPE_PINELAB_QR
                    }
                }
                setPaymentOptionsAdapter()
            }
            
            else if (str == getString(R.string.wallet_go_back)) {
                lastSelectedPaymentPosition = 0
                paymentType = 1
                setPaymentOptionsAdapter()

                if (IS_PINELAB_DEVICE) {
                    if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                        paymentType = PAYMENT_TYPE_PINELAB_QR
                    }
                }

                selectedWalletUpiOptionId = null
                selectedWalletOrUpi = null
                selectedWalletUpiOptionName = null

                if (bookTicketPnr.isNotEmpty()) {
                    if (isNetworkAvailable())
//                        callReleaseTicketApi()
                    else
                        noNetworkToast()
                }

            }

            else if (str == getString(R.string.easebuzz_go_back)) {
                lastSelectedPaymentPosition = 0
                paymentType = 1
                setPaymentOptionsAdapter()

                if (IS_PINELAB_DEVICE) {
                    if (PreferenceUtils.getPrivilege()!!.allowUserToUsePinelabDevicesForUpiPayment) {
                        paymentType = PAYMENT_TYPE_PINELAB_QR
                    }
                }

                selectedEasebuzzOptionId = null
                selectedEasebuzz = null
                selectedEasebuzzOptionName = null

                if (bookTicketPnr.isNotEmpty()) {
                    if (isNetworkAvailable())
//                        callReleaseTicketApi()
                    else
                        noNetworkToast()
                }

            }
            
            else if (str.contains(getString(R.string.other_payments_confirm))) {
                val otherPaymentOptionPosition = str.substringAfter("-")

                selectedOtherPaymentOption =
                    otherPaymentOptions[otherPaymentOptionPosition.toDouble()
                        .toInt()].payGayTypeName

                Timber.d("selectedOtherPaymentOption $selectedOtherPaymentOption")
            }
            
            else if (str.contains(WalletOptionAdapter.TAG) || str.contains(WalletOptionAgentRechargeAdapter.TAG)) {

                val walletUpiPosition = str.substringAfter("-")

                selectedWalletUpiOptionName = walletPaymentOptions[walletUpiPosition.toDouble().toInt()].name
                selectedWalletUpiOptionId = walletPaymentOptions[walletUpiPosition.toDouble().toInt()].paygayType
                selectedWalletOrUpi = walletPaymentOptions[walletUpiPosition.toDouble().toInt()].type
                
                
                when (selectedWalletUpiOptionId) {
                    
                    15-> {
                        PreferenceUtils.putString("upiSelected", "QR")
                        
                        walletUpiAlertDialog.apply {
                            findViewById<TextInputLayout>(R.id.layout_mobile_number).gone()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).gone()
                            findViewById<RadioGroup>(R.id.layout_upi_id).gone()
                            findViewById<RadioGroup>(R.id.et_upi_id).gone()
                        }
                    }
                    
                    16-> {
                        PreferenceUtils.putString("upiSelected", "SMS")
                        
                        walletUpiAlertDialog.apply {
                            findViewById<TextInputLayout>(R.id.layout_mobile_number).visible()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).visible()
                            
                            findViewById<RadioGroup>(R.id.layout_upi_id).gone()
                            findViewById<RadioGroup>(R.id.et_upi_id).gone()
                        }
                        
                    }
                    17-> {
                        PreferenceUtils.putString("upiSelected", "VPA")
                        
                        walletUpiAlertDialog.apply {
                            findViewById<RadioGroup>(R.id.layout_upi_id).visible()
                            findViewById<RadioGroup>(R.id.et_upi_id).visible()
                            
                            findViewById<TextInputLayout>(R.id.layout_mobile_number).gone()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).gone()
                        }
                    }
                    18 -> {
                        PreferenceUtils.putString("upiSelected", PaymentTypes.PHONEPE_V2)

                        walletUpiAlertDialog.apply {
                            findViewById<RadioGroup>(R.id.layout_upi_id).gone()
                            findViewById<RadioGroup>(R.id.et_upi_id).gone()

                            findViewById<TextInputLayout>(R.id.layout_mobile_number).gone()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).gone()
                        }
                    }
                    5 -> {
                        PreferenceUtils.putString("upiSelected", "wallet")
                        val groupRadio = walletUpiAlertDialog.findViewById<RadioGroup>(
                            R.id.upi_radio_group
                        )
                        groupRadio.gone()
                        walletUpiAlertDialog.findViewById<TextInputLayout>(
                            R.id.layout_mobile_number
                        ).visible()

                        walletUpiAlertDialog.findViewById<TextInputEditText>(
                            R.id.et_mobile_number
                        ).visible()
                    }

                    PAYMENT_TYPE_PINELAB_QR -> {
                        PreferenceUtils.putString("upiSelected", "wallet")
                        val groupRadio = walletUpiAlertDialog.findViewById<RadioGroup>(
                            R.id.upi_radio_group
                        )
                        groupRadio.gone()
                        walletUpiAlertDialog.findViewById<TextInputLayout>(
                            R.id.layout_mobile_number
                        ).visible()

                        walletUpiAlertDialog.findViewById<TextInputEditText>(
                            R.id.et_mobile_number
                        ).visible()
                    }

                    else -> {
                        PreferenceUtils.putString("upiSelected", "wallet")
                    }
                }

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val walletConfirmButton =
                        walletUpiAlertDialog.findViewById<Button>(
                            R.id.btnConfirm
                        )
                    walletConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))


                    walletUpiAlertDialog.findViewById<TextInputEditText>(
                        R.id.et_mobile_number
                    ).onChange {
                        if (it.isNotEmpty())
                            walletConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                        else
                            walletConfirmButton.setBackgroundColor(resources.getColor(R.color.button_default_color))

                    }
                }
                
                Timber.d("selectedWalletUpiOptionId $selectedWalletUpiOptionId")
                Timber.d("selectedWalletUpiOptionId-Name $selectedWalletUpiOptionName")
                Timber.d("selectedWalletUpiOptionId $selectedWalletOrUpi")
            }

            else if (str.contains(EasebuzzOptionAdapter.TAG)) {
                val easebuzzPosition = str.substringAfter("-")

                selectedEasebuzzOptionName = easebuzzPaymentOptions[easebuzzPosition.toDouble().toInt()].name
                selectedEasebuzzOptionId = easebuzzPaymentOptions[easebuzzPosition.toDouble().toInt()].paygayType
                selectedEasebuzz = easebuzzPaymentOptions[easebuzzPosition.toDouble().toInt()].type

                val btnConfirm =  walletUpiAlertDialog.findViewById<Button>(R.id.btnConfirm)

                when (selectedEasebuzzOptionId) {

                    20-> {
                        PreferenceUtils.putString("easebuzzSelected", "QR")

                        walletUpiAlertDialog.apply {
                            findViewById<TextInputLayout>(R.id.layout_mobile_number).gone()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).gone()
                            findViewById<RadioGroup>(R.id.layout_upi_id).gone()
                            findViewById<RadioGroup>(R.id.et_upi_id).gone()
                        }

                        enableDisableConfirmBtn(btnConfirm, true)
                    }

                    21-> {
                        PreferenceUtils.putString("easebuzzSelected", "SMS")

                        walletUpiAlertDialog.apply {
                            findViewById<TextInputLayout>(R.id.layout_mobile_number).visible()
                            val etMobileNumber = findViewById<TextInputEditText>(R.id.et_mobile_number)
                            etMobileNumber.visible()

                            findViewById<RadioGroup>(R.id.layout_upi_id).gone()
                            findViewById<RadioGroup>(R.id.et_upi_id).gone()

                            enableDisableConfirmBtn(btnConfirm, etMobileNumber.text.toString().isNotEmpty() && etMobileNumber.text.toString().length == 10)
                        }

                    }
                    22-> {
                        PreferenceUtils.putString("easebuzzSelected", "VPA")

                        walletUpiAlertDialog.apply {
                            findViewById<RadioGroup>(R.id.layout_upi_id).visible()
                            val etUpiId = findViewById<TextInputEditText>(R.id.et_upi_id)
                            etUpiId.visible()

                            findViewById<TextInputLayout>(R.id.layout_mobile_number).gone()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).gone()

                            enableDisableConfirmBtn(btnConfirm, etUpiId.text.toString().isNotEmpty())
                        }
                    }
                    else -> {
                        PreferenceUtils.putString("easebuzzSelected", "UPI_Selected")
                    }
                }

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    walletUpiAlertDialog.findViewById<TextInputEditText>(R.id.et_mobile_number).onChange {
                        if (it.length == 10) {
                            enableDisableConfirmBtn(btnConfirm, true)
                        } else {
                            enableDisableConfirmBtn(btnConfirm, false)
                        }
                    }

                    walletUpiAlertDialog.findViewById<TextInputEditText>(R.id.et_upi_id).onChange {
                        if (it.isNotEmpty()) {
                            enableDisableConfirmBtn(btnConfirm, true)
                        } else {
                            enableDisableConfirmBtn(btnConfirm, false)
                        }
                    }
                }
            }
            
            else if (str.contains("UPI_Selected")) {

                val groupRadio = walletUpiAlertDialog.findViewById<RadioGroup>(
                    R.id.upi_radio_group
                )

//                groupRadio.visible()

                walletUpiAlertDialog.findViewById<RadioButton>(
                    R.id.upi_create_qr
                ).setOnClickListener {
                    walletUpiAlertDialog.findViewById<TextInputLayout>(
                        R.id.layout_mobile_number
                    ).gone()

                    walletUpiAlertDialog.findViewById<TextInputEditText>(
                        R.id.et_mobile_number
                    ).gone()

                }

                when (groupRadio.checkedRadioButtonId) {
//                    R.id.upi_send_sms -> {
//
//                    }

//                    R.id.upi_create_qr -> {
//                        walletUpiAlertDialog.findViewById<ProgressBar>(
//                            R.id.progress_bar
//                        ).visible()
//
//                        callUPICreateQrCodeApi()
//                    }
                }
            }
            
            else if (str.contains(getString(R.string.wallet_upi_confirm))) {

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
                                        walletOtpGenerationApi()
                                    } else
                                        noNetworkToast()
                                } else {
                                    toast(getString(R.string.invalid_mobile_number))
                                }

                            }
                        }
                    } else {
                        val walletOtp =
                            walletUpiAlertDialog.findViewById<TextInputEditText>(
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
            }
            
            else if (str.contains("QR")) {
//                toast("testing QR $str")
                selectedSubPaymentOptionName = str
                
                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val btnText = walletUpiAlertDialog.findViewById<Button>(R.id.btnConfirm
                    ).text.toString()
                    if (btnText == getString(R.string.select)) {
                        
                        when {
                            selectedSubPaymentOptionName?.isEmpty() == true -> toast(getString(R.string.please_selecte_an_option))
                            else -> {
                                isEasebuzzSelected = true
                                walletUpiAlertDialog.dismiss()
                            }
                        }
                    }
                }
            }
            
            else if (str.contains("SMS")) {
                val strList = str.split("-")
                if (strList.isNotEmpty()) {
                    if(isAgentLogin)
                        agentPayViaPhoneNumberSMS = strList[1]
                    else
                        branchUserPayViaPhoneNumberSMS = strList[1]
                }
                
                selectedSubPaymentOptionName = strList[0]
                
//                toast("testing SMS - $agentPayViaPhoneNumberSMS")
                
                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val btnText = walletUpiAlertDialog.findViewById<Button>(
                        R.id.btnConfirm
                    ).text.toString()
                    if (btnText == getString(R.string.select)) {
                        val walletConfirmButton =
                            walletUpiAlertDialog.findViewById<Button>(
                                R.id.btnConfirm
                            )
                        walletConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))

                        if (isAgentLogin) {
                            when {
                                agentPayViaPhoneNumberSMS.isEmpty() -> toast(getString(R.string.validate_mobile_number))
                                selectedSubPaymentOptionName?.isEmpty() == true -> toast(getString(R.string.please_selecte_an_option))
                                else -> if (privilegeResponseModel.phoneNumValidationCount!! <= agentPayViaPhoneNumberSMS.toCharArray().size) {
                                    if (isNetworkAvailable()) {
                                        isEasebuzzSelected = true
                                        walletUpiAlertDialog.dismiss()
                                    } else
                                        noNetworkToast()
                                } else {
                                    toast(getString(R.string.invalid_mobile_number))
                                }
                            }
                        } else {
                            when {
                                branchUserPayViaPhoneNumberSMS.isEmpty() -> toast(getString(R.string.validate_mobile_number))
                                selectedSubPaymentOptionName?.isEmpty() == true -> toast(getString(R.string.please_selecte_an_option))
                                else -> if (privilegeResponseModel.phoneNumValidationCount!! <= branchUserPayViaPhoneNumberSMS.toCharArray().size) {
                                    if (isNetworkAvailable()) {
                                        walletUpiAlertDialog.dismiss()
                                    } else
                                        noNetworkToast()
                                } else {
                                    toast(getString(R.string.invalid_mobile_number))
                                }
                            }
                        }
                    }
                }
            }
            
            else if (str.contains("VPA")) {
                val strList = str.split("-")
                if (strList.isNotEmpty()) {
                    if(isAgentLogin)
                        agentPayViaVPA = strList[1]
                    else
                        branchUserPayViaVPA = strList[1]
                }
                
//                toast("testing VPA - $agentPayViaVPA")
                
                selectedSubPaymentOptionName = strList[0]
                
                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val btnText = walletUpiAlertDialog.findViewById<Button>(
                        R.id.btnConfirm
                    ).text.toString()
                    if (btnText == getString(R.string.select)) {
                        val walletConfirmButton =
                            walletUpiAlertDialog.findViewById<Button>(
                                R.id.btnConfirm
                            )
                        walletConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))

                        if (isAgentLogin) {
                            when {
                                selectedSubPaymentOptionName?.isEmpty() == true -> toast(getString(R.string.please_selecte_an_option))
                                agentPayViaVPA.isEmpty() -> toast(getString(R.string.enter_upi_id))
                                else -> {
                                    isEasebuzzSelected = true
                                    walletUpiAlertDialog.dismiss()
                                }
                            }
                        } else {
                            when {
                                branchUserPayViaVPA.isEmpty() -> toast(getString(R.string.enter_upi_id))
                                selectedSubPaymentOptionName?.isEmpty() == true -> toast(getString(R.string.please_selecte_an_option))
                                else -> {
                                    walletUpiAlertDialog.dismiss()
                                }
                            }
                        }
                    }
                }
            }

            else if (str == PaymentTypes.PHONEPE_V2) {
                selectedSubPaymentOptionName = str

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val btnText = walletUpiAlertDialog.findViewById<Button>(R.id.btnConfirm).text.toString()

                    if (btnText == getString(R.string.select)) {
                        when {
                            selectedSubPaymentOptionName?.isEmpty() == true -> toast(getString(R.string.please_selecte_an_option))
                            else -> {
                                isPhonePeV2Selected = true
                                walletUpiAlertDialog.dismiss()
                            }
                        }
                    }
                }
            }

            else if (str == "qr_confirm") {
                if (::walletUpiAlertDialog.isInitialized) {
                    walletUpiAlertDialog.dismiss()
                }
                if (::upiCreateQRAlertDialog.isInitialized) {
                    upiCreateQRAlertDialog.dismiss()
                }
                
                if (::upiAuthSmsAndVPADialog.isInitialized) {
                    upiAuthSmsAndVPADialog.dismiss()
                }
            }
            else if (str == getString(R.string.cancel) || str == "Confirm Release") {
                
                lastSelectedPaymentPosition = 0
                paymentType = 1
                
                if (::walletUpiAlertDialog.isInitialized) {
                    walletUpiAlertDialog.dismiss()
                }
                if (::upiCreateQRAlertDialog.isInitialized) {
                    upiCreateQRAlertDialog.dismiss()
                }
                
                if (::upiAuthSmsAndVPADialog.isInitialized) {
                    upiAuthSmsAndVPADialog.dismiss()
                }

                closePhonePeV2PendingDialog()
                showPhonePeV2PendingDialog = false

                setPaymentOptionsAdapter()
                isCancelledClicked = true
            }
            else if (str == getString(R.string.unauthorized)) {
                //clearAndSave(requireContext())
                PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun enableDisableConfirmBtn(button: Button, isEnable: Boolean) {
        if (isEnable) {
            button.backgroundTintList = AppCompatResources.getColorStateList(this, R.color.colorPrimary)
            button.isEnabled = true
        } else {
            button.backgroundTintList = AppCompatResources.getColorStateList(this, R.color.colorShadow)
            button.isEnabled = false
        }
    }

    private class IncomingHandler(bookingPaymentOptionsActivity: ConfirmPhoneBookingActivity) :
        Handler() {
        private val BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE"
        var activity = bookingPaymentOptionsActivity

        override fun handleMessage(msg: Message) {
            val bundle = msg.data
            val value = bundle.getString(BILLING_RESPONSE_TAG)
            Timber.d("Value :", value.toString())

            val data =
                Gson().fromJson<CardSaleResponse>(value.toString(), CardSaleResponse::class.java)

            var respData: CardSaleResponse? = null
            respData = data
            activity.handlePinelabSuccessResp(respData, value.toString())
        }
    }


    fun handlePinelabSuccessResp(respData: CardSaleResponse, pinelabResponse: String) {
        pinelabResponseData = respData
        pinelabResponseString = pinelabResponse
        if (DialogUtils.progressDialog != null && DialogUtils.progressDialog!!.isShowing) {
            DialogUtils.progressDialog?.dismiss()
        }
        // if (respData.detail ) {
        val jsonObj = JSONObject(pinelabResponseString!!)
        val reqBody =
            ReqBodyPinelab(
                loginModelPref.api_key,
                pnrNumber,
                true,
                pinelab_response = jsonObj,
                pinelab_payment_type = paymentType

            )
        DialogUtils.showProgressDialog(this)
        bookingOptionViewModel.pinelabStatusApi(reqBody, pinelab_transaction_status_api)
        pinelabBillingRefNo = pnrNumber
        /* } else {
             toast("Booking failed, Please try again")
             *//*  dashboardViewModel.releaseTicketAPI(
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_INITIALIZE_EZETAP -> {
                if(resultCode== RESULT_OK){
//                    EzeAPI.getDeviceInfo(this, REQUEST_EZETAP_DEVICE_INFO, JSONObject())
                }}

            REQUEST_EZETAP_DEVICE_INFO -> {
                if(resultCode== RESULT_OK){
                    val deviceData = JSONObject(data?.extras!!["response"].toString())
                    if (deviceData.has("result")) {
                        ezetapDeviceId =
                            deviceData.getJSONObject("result").getString("deviceSerialNo")
                    }
                }}

            REQUEST_CODE_PAY_EZETAP -> {
                ezetapPaySuccessHandling(data)
            }
        }
    }

    private fun ezetapPaySuccessHandling(data: Intent?) {
        val data = JSONObject(data?.extras!!["response"].toString())
        if (data.has("result")) {
            val obj = data.getJSONObject("result").getJSONObject("txn")
            obj.put("success", true)
            obj.put("messageCode", "P2P_DEVICE_TXN_DONE")
            val jsonParser = JsonParser()
            val statusData = jsonParser.parse(obj.toString()) as JsonObject
            handleEzetapResponse(statusData)
        }
    }

    private fun handleEzetapResponse(
        ezetapResponse: JsonObject
    ) {
        var reqBody: ReqBodyEzetapStatus? = null
        var payType = 0
        reqBody =
            ReqBodyEzetapStatus(
                api_key = loginModelPref.api_key,
                pnr_number = pnrNumber,
                is_send_sms = true,
                ezetap_response = ezetapResponse,
                ezetap_payment_type = PAYMENT_TYPE_EZETAP.toInt(),
                reservation_id = ""

            )
        ezetapStatusApi(reqBody)
    }

    private fun ezetapStatusApi(reqBody: ReqBodyEzetapStatus) {
        bookingOptionViewModel.ezetapStatusApi(reqBody, ezetap_status_api)
    }
}