package com.bitla.ts.presentation.view.passenger_payment_show_new_flow

import SingleViewModel
import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.*
import android.net.Uri
import android.os.*
import android.text.*
import android.util.*
import android.util.Base64
import android.view.*
import android.view.WindowInsets
import android.widget.*
import androidx.activity.compose.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.*
import androidx.compose.ui.window.*
import androidx.core.os.postDelayed
import androidx.core.text.*
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import androidx.transition.*
import asString
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.account_info.request.*
import com.bitla.ts.domain.pojo.book_extra_seat.request.*
import com.bitla.ts.domain.pojo.book_ticket_full.Result
import com.bitla.ts.domain.pojo.book_ticket_full.request.*
import com.bitla.ts.domain.pojo.book_ticket_full.request.BookingDetails
import com.bitla.ts.domain.pojo.book_ticket_full.request.ContactDetail
import com.bitla.ts.domain.pojo.book_ticket_full.request.PackageDetailsId
import com.bitla.ts.domain.pojo.book_ticket_full.request.PrivilegeCardHash
import com.bitla.ts.domain.pojo.book_ticket_full.request.ReqBody
import com.bitla.ts.domain.pojo.book_ticket_full.request.SeatDetail
import com.bitla.ts.domain.pojo.book_ticket_full.request.SmartMilesHash
import com.bitla.ts.domain.pojo.book_with_extra_seat.request.*
import com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.request.*
import com.bitla.ts.domain.pojo.coupon.request.*
import com.bitla.ts.domain.pojo.ezetap.*
import com.bitla.ts.domain.pojo.fare_breakup.request.*
import com.bitla.ts.domain.pojo.fare_breakup.response.*
import com.bitla.ts.domain.pojo.paytm_pos_integration.paytm_pos_txn_status_api.request.PaytmPosTxnStatusRequest
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.rapid_booking.request.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.smart_miles_otp.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.ticketDetails.*
import com.bitla.ts.presentation.view.passenger_payment.ui.*
import com.bitla.ts.presentation.view.passenger_payment_show_new_flow.ui.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getDateDMMM
import com.bitla.ts.utils.common.getDateDMY
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.common.getUserRole
import com.bitla.ts.utils.common.inputFormatToOutput
import com.bitla.ts.utils.common.saveAgentList
import com.bitla.ts.utils.common.saveBranchList
import com.bitla.ts.utils.common.saveCityList
import com.bitla.ts.utils.common.saveUserList
import com.bitla.ts.utils.common.stringToDate
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.tscalender.*
import com.google.gson.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import noNetworkToast
import onChange
import org.json.*
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible
import java.lang.Runnable
import java.text.*
import java.util.*

class PassengerPaymentNewFlowActivity : BaseActivity(), VarArgListener, DialogSingleButtonListener,
    DialogButtonListener, DialogButtonAnyDataListener, SlyCalendarDialog.Callback {
    private var isRapidBooking: Boolean = false
    private var toolbarTitle: String = ""
    private var role: String? = null
    private var blockingDate: String = ""
    private var blockingTimeHours: String = ""
    private var blockingTimeMins: String = ""
    private var blockingAmPm: String = ""
    private var isPermanentPhoneBookingChecked: Boolean = true

    private val passengerDetailsViewModel by viewModel<PassengerDetailsViewModel<Any?>>()
    private val cityDetailViewModel by viewModel<CityDetailViewModel<Any?>>()
    private val privilegeDetailsViewModel by viewModel<PrivilegeDetailsViewModel>()
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private val singleViewModel by viewModel<SingleViewModel<Any?>>()
    private val passengerHistoryViewModel by viewModel<PassengerHistoryViewModel<Any?>>()
    private val validateCouponViewModel by viewModel<ValidateCouponViewModel<Any?>>()
    private val bookingOptionViewModel by viewModel<BookingOptionViewModel<Any?>>()
    private val redelcomViewModel by viewModel<RedelcomViewModel<Any?>>()
    private val agentAccountInfoViewModel by viewModel<AgentAccountInfoViewModel<Any>>()
    private val agentRechargeViewModel by viewModel<AgentRechargeViewModel<Any>>()
    private var srcDest: String = ""

    private lateinit var walletUpiAlertDialog: AlertDialog
    private lateinit var upiCreateQRAlertDialog: AlertDialog
    private lateinit var upiAuthSmsAndVPADialog: AlertDialog
    private lateinit var upiAppsPaymentDialog: AlertDialog
    private lateinit var phonePeV2PendingDialog: AlertDialog

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    val REQUEST_CODE_INITIALIZE_EZETAP = 10001
    val REQUEST_CODE_PAY_EZETAP = 10002
    val REQUEST_EZETAP_DEVICE_INFO = 10003
    val REQUEST_PAYMENT_INTENT_FLOW = 10005
    private var ezetapDeviceId: String = ""
    var ezetapData = EzetapTransactionRequest()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var lastCheckedSeat: String? = null
    private var lastCheckedFare: String? = null
    private var modifiedFareDone: MutableList<String> = mutableListOf()
    private var originalFaresString: String = ""
    private var modifiedFaresString: String = ""
    private var originalSeatListString: String = ""
    private var originalFares: MutableList<String> = mutableListOf()
    private var modifiedFares: MutableList<String> = mutableListOf()
    private var originalSeatList: MutableList<String> = mutableListOf()
    private val ticketDetailsViewModel by viewModel<TicketDetailsComposeViewModel<Any?>>()

    override fun initUI() {

//        if(getPrivilegeBase() != null){
//            passengerDetailsViewModel.privileges = getPrivilegeBase()
//        }

        lifecycleScope.launch {
            val privilege = getPrivilegeBaseSafely()
            passengerDetailsViewModel.updatePrivileges(privilege)
        }

        if (intent.getStringExtra(getString(R.string.toolbar_title)) != null) {
            toolbarTitle = intent.getStringExtra(getString(R.string.toolbar_title))!!
        }
        if (intent.getSerializableExtra(getString(R.string.boarding)) != null) {
            val boardingList =
                intent.getSerializableExtra(getString(R.string.boarding)) as MutableList<StageDetail>
            boardingList.forEach {
                passengerDetailsViewModel.boardingList.add(it)
                passengerDetailsViewModel.boardingSpinnerList.add(
                    SpinnerItems(
                        it.id ?: 0,
                        it.name ?: ""
                    )
                )
            }

        }
        if (intent.getSerializableExtra(getString(R.string.dropping)) != null) {
            val droppingList =
                intent.getSerializableExtra(getString(R.string.dropping)) as MutableList<StageDetail>
            droppingList.forEach {
                passengerDetailsViewModel.droppingList.add(it)
                passengerDetailsViewModel.droppingSpinnerList.add(
                    SpinnerItems(
                        it.id ?: 0,
                        it.name ?: ""
                    )
                )
            }
        }
        passengerDetailsViewModel.setPhoneBlockTime(ResourceProvider.TextResource.fromStringId(R.string.not_specified))
        passengerDetailsViewModel.setBoardingPointText()
        passengerDetailsViewModel.setDroppingPointText()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setPrivilegesObserver()
        setCityObserver()
        setOnBehalfAgentObserver()
        setBranchObserver()
        setUserObserver()
        setMealInfoObserver()
        setPaxHistoryObserver()
        setCouponResponseObserver()
        setFareBreakupObserver()
        setBookTicketObserver()
        setBookExtraTicketObserver()
        setUpiQrObserver()
        setUpiTranxStatusObserver()
        setWalletOtpObserver()
        validateWalletOtpObserver()
        bookSeatWithExtraSeatObserver()
        setConfirmPhoneBlockTicketObserver()
        accountObserver()
        setCampaignsAndPromotionsDiscountObserver()
        //fareBreakupApi()
        setEzetapStatusObserver()
        setPaytmTxnStatusObserver()
        setReleaseTicketObserver()
        setReleaseBranchUpiBlockedSeatsObserver()
        editFareValues()
        setTicketDetailsObserver()
        phonePeV2StatusObserver()
        phonePeV2TicketSuccessConPayObserver()

        setContent {
            if (passengerDetailsViewModel.passengerDataList.any { it.isExtraSeat }
                && passengerDetailsViewModel.isAllMandatoryFieldsFilled.size == passengerDetailsViewModel.passengerDataList.size
            ) {
                if (passengerDetailsViewModel.isExtraSeatChanged) {
                    LaunchedEffect(passengerDetailsViewModel.isExtraSeatChanged) {
                        delay(2000)
                        fareBreakupApi()
                        passengerDetailsViewModel.isExtraSeatChanged = false
                    }
                }

            }


            if (passengerDetailsViewModel.isEnableCampaignPromotions && passengerDetailsViewModel.isEnableCampaignPromotionsChecked && passengerDetailsViewModel.isPassengerAgeChanged) {
                LaunchedEffect(passengerDetailsViewModel.isPassengerAgeChanged) {
                    delay(2000)
                    callCampaignsAndPromotionsDiscountApi()
                    passengerDetailsViewModel.isPassengerAgeChanged = false
                }
            }

            if (passengerDetailsViewModel.selectedRadioOnlineAgent || passengerDetailsViewModel.selectedRadioOfflineAgent) {
                if (!passengerDetailsViewModel?.loginModelPref?.city_id.isNullOrEmpty() && passengerDetailsViewModel?.selectedCityId == 0) {
                    LaunchedEffect(passengerDetailsViewModel.selectedCityId) {
                        passengerDetailsViewModel.selectedCityId =
                            passengerDetailsViewModel.loginModelPref?.city_id?.toInt() ?: 0
                        callAgentListApi()
                    }
                }
            }


            if (passengerDetailsViewModel.isEnableCampaignPromotions && passengerDetailsViewModel.isEnableCampaignPromotionsChecked && passengerDetailsViewModel.isPerBookingDiscountAmountChanged) {
                LaunchedEffect(passengerDetailsViewModel.isPerBookingDiscountAmountChanged) {
                    fareBreakupApi()
                    passengerDetailsViewModel.isPerBookingDiscountAmountChanged = false
                }
            }
            if (isNetworkAvailable()) {
                if (passengerDetailsViewModel.isFareBreakupApiCalled) {
                    fareBreakupApi()
                }
//                LaunchedEffect(passengerDetailsViewModel.isFareBreakupApiCalled) {
//                    if (passengerDetailsViewModel.isFareBreakupApiCalled) {
//                        fareBreakupApi()
//                        passengerDetailsViewModel.isFareBreakupApiCalled = false
//                    }
//                }
            } else
                noNetworkToast()

            if (isNetworkAvailable()) {
                if (passengerDetailsViewModel.isSmartMilesOtpApi.value)
                    getSmartMilesOtpApi()

                LaunchedEffect(passengerDetailsViewModel.isAppliedCoupon.value) {
                    if (passengerDetailsViewModel.isAppliedCoupon.value) {
                        passengerDetailsViewModel.discountParams?.let {
                            validateCouponCode(it)
                        }
                    }
                }

                if (passengerDetailsViewModel.isRetrieveClicked.value
                    && !passengerDetailsViewModel.showDialog.value
                )
                    callPassengerHistoryApi()

            } else
                noNetworkToast()

            if (passengerDetailsViewModel.isRetrieveClicked.value
                && passengerDetailsViewModel.showDialog.value
                && passengerDetailsViewModel.passengerHistoryList.isNotEmpty()
            ) {
                BoxWithConstraints {
                    Box(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .background(colorResource(id = R.color.button_color))
                            .height(this@BoxWithConstraints.maxHeight)
                            .padding(16.dp)
                    )
                    {
                        ItemRetrievePax(
                            this@PassengerPaymentNewFlowActivity,
                            passengerDetailsViewModel, onCheckedChange = { index, isChecked ->

                                passengerDetailsViewModel.passengerHistoryList[index].isChecked =
                                    isChecked
                                val checkedPassenger =
                                    passengerDetailsViewModel.passengerHistoryList[index]
                                if (passengerDetailsViewModel.passengerHistoryList[index].isChecked) {
                                    passengerDetailsViewModel.checkedPassengerList.add(
                                        checkedPassenger
                                    )
                                } else {
                                    passengerDetailsViewModel.checkedPassengerList.remove(
                                        checkedPassenger
                                    )
                                }

                                if (passengerDetailsViewModel.checkedPassengerList.size > passengerDetailsViewModel.passengerDataList.size) {
                                    passengerDetailsViewModel.passengerHistoryList[index].isChecked =
                                        false
                                    passengerDetailsViewModel.checkedPassengerList.remove(
                                        checkedPassenger
                                    )
                                    toast(
                                        "${getString(R.string.validate_max_pax)} ${passengerDetailsViewModel.passengerDataList.size} ${
                                            getString(R.string.max_passenger)
                                        }"
                                    )
                                }
                            }
                        )
                    }
                }
            }

            passengerDetailsViewModel.apply {
                val passengerStateList =
                    passengerDetailsViewModel.listPassengerFlow.collectAsState()
                if (passengerStateList.value.isNotEmpty()) {
                    for (i in 0..passengerStateList.value.size.minus(1)) {
                        passengerDataList[i].firstName = passengerStateList.value[i].firstName
                        passengerDataList[i].lastName = passengerStateList.value[i].lastName
                        passengerDataList[i].name = passengerStateList.value[i].name
                        passengerDataList[i].age = passengerStateList.value[i].age
                        passengerDataList[i].sex = passengerStateList.value[i].sex
                        passengerDataList[i].isPrimary = passengerStateList.value[i].isPrimary
                    }

                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                PassengerDetailsScreen()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun getTicketDetailsApi() {
        if (this.isNetworkAvailable()) {
            ticketDetailsViewModel.ticketDetailsApi(
                apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                ticketNumber =  passengerDetailsViewModel.bookTicketPnr,
                jsonFormat = true,
                loadPrivs = false,
                menuPrivilege = false,
                isQrScan = false,
                locale = passengerDetailsViewModel.locale,
                apiType = ticket_details_method_name
            )
        } else this.noNetworkToast()
    }


    private fun editFareValues() {
        originalFaresString = PreferenceUtils.getPreference(PREF_ORIGINAL_FARE_LIST, "") ?: ""
        modifiedFaresString = PreferenceUtils.getPreference(PREF_EDITED_FARE_LIST, "") ?: ""
        originalSeatListString = PreferenceUtils.getPreference(PREF_ORIGINAL_SEAT_LIST, "") ?: ""

        originalFares = if (originalFaresString.isNotBlank())
            originalFaresString.split(",").toMutableList() else mutableListOf()

        modifiedFares = if (modifiedFaresString.isNotBlank())
            modifiedFaresString.split(",").toMutableList() else mutableListOf()

        originalSeatList = if (originalSeatListString.isNotBlank())
            originalSeatListString.split(",").toMutableList() else mutableListOf()

        modifiedFareDone = modifiedFares
    }

    private fun setConfirmPhoneBlockTicketObserver() {
        passengerDetailsViewModel.showShimmer = false
        bookingOptionViewModel.confirmPhoneBlockTicket.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        val intent = if (passengerDetailsViewModel.country.equals(
                                "India",
                                true
                            ) || passengerDetailsViewModel.country.equals("Indonesia", true)
                        ) {
                            Intent(this, TicketDetailsActivityCompose::class.java)
                        } else {
                            Intent(this, TicketDetailsActivity::class.java)
                        }
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

    private fun bookSeatWithExtraSeatObserver() {
        passengerDetailsViewModel.showShimmer = false
        bookingOptionViewModel.bookSeatWithExtraSeat.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (it.result.payment_initiatives == "RedelcomPay") {
                            DialogUtils.createRedelcomPaymentDialog(this, this, this)
                            if (passengerDetailsViewModel.terminalId != "") {
                                handler.postDelayed(Runnable {
                                    passengerDetailsViewModel.isHandlerRunning = true
                                    handler.postDelayed(
                                        runnable!!,
                                        passengerDetailsViewModel.delay.toLong()
                                    )
                                    passengerDetailsViewModel.pnrNumber = it.result.ticket_number
                                    redelcomViewModel.redelcomPgStatusApi(
                                        passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                                        passengerDetailsViewModel.locale,
                                        passengerDetailsViewModel.pnrNumber,
                                        passengerDetailsViewModel.terminalId
                                    )
                                }.also { runnable = it }, passengerDetailsViewModel.delay.toLong())

                            }
                        }
                        else if (passengerDetailsViewModel.selectedBookingTypeId == 4) {
                            if (passengerDetailsViewModel.isUpiPayment) {
                                passengerDetailsViewModel.bookTicketTotalFare =
                                    it.result.total_fare.toString()
                                passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number
                                callUPICreateQrCodeApi()

                            } else {
                                if (it.result != null) {
                                    passengerDetailsViewModel.bookTicketTotalFare =
                                        it.result.total_fare.toString()
                                    passengerDetailsViewModel.bookTicketPnr =
                                        it.result.ticket_number
                                }

                                if (isNetworkAvailable())
                                    walletOtpGenerationApi()
                                else
                                    noNetworkToast()
                            }

                        }
                        // agent recharge
                        else if (passengerDetailsViewModel.selectedSubPaymentOptionId == "QR"
                            && it.result.ticket_status.equals(getString(R.string.pending), ignoreCase = true)
                        ) {
//                              toast("Pay via QR")
                            if (it.result.agentRechargeQrResp?.isNotEmpty()==true) {
                                try {
                                    passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number
                                    passengerDetailsViewModel.bookTicketTotalFare = it.result.total_fare.toString()
                                    
                                    upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                                        context = this,
                                        isFromAgentRechargePG = true,
                                        dialogSingleButtonListener = this
                                    )
                                    
                                    val base64String = it.result.agentRechargeQrResp?.substring(22)
                                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                    
                                    if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {
                                        upiCreateQRAlertDialog.findViewById<ImageView>(
                                            R.id.qr_code_image
                                        ).setImageBitmap(decodedImage)
                                    }
                                    
                                    callPayStatOfAgentInsRechargStatusApi()
                                } catch (e:Exception) {
                                    toast(getString(R.string.something_went_wrong))
                                }
                            } else {
                                toast(getString(R.string.something_went_wrong))
                            }
                        }
                        else if (passengerDetailsViewModel.selectedSubPaymentOptionId.toString() == "SMS"
                            && it.result.ticket_status.equals(getString(R.string.pending), ignoreCase = true)
                        ) {
//                            toast("Pay via SMS")
                            passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number
                            passengerDetailsViewModel.bookTicketTotalFare = it.result.total_fare.toString()
                            
                            upiAuthSmsAndVPADialog =  DialogUtils.upiAuthSmsAndVPADialog(
                                context = this,
                                isSmsAuth = true,
                                dialogSingleButtonListener = this
                            )
                            callPayStatOfAgentInsRechargStatusApi()
                            
                        }
                        else if (passengerDetailsViewModel.selectedSubPaymentOptionId.toString() == "VPA"
                            && it.result.ticket_status.equals(getString(R.string.pending), ignoreCase = true)
                        ) {
//                            toast("Pay via UPI")
                            passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number
                            passengerDetailsViewModel.bookTicketTotalFare = it.result.total_fare.toString()
                            
                            upiAuthSmsAndVPADialog =  DialogUtils.upiAuthSmsAndVPADialog(
                                context = this,
                                isSmsAuth = false,
                                dialogSingleButtonListener = this
                            )
                            
                            callPayStatOfAgentInsRechargStatusApi()
                        }
                        else {
                            val intent = if (passengerDetailsViewModel.country.equals(
                                    "India",
                                    true
                                ) || passengerDetailsViewModel.country.equals("Indonesia", true)
                            ) {
                                Intent(this, TicketDetailsActivityCompose::class.java)
                            } else {
                                Intent(this, TicketDetailsActivity::class.java)
                            }

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
                        passengerDetailsViewModel.selectedPaymentOptionId = 1
                        toast(it.message)
                    }

                    else -> {
                        if (it?.message != null) {
                            it?.message?.let { it1 -> toast(it1) }
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
    }

    private fun validateWalletOtpObserver() {
        passengerDetailsViewModel.showShimmer = false

        bookingOptionViewModel.validateWalletOtp.observe(this) {
            if (it != null) {
                if (it.code == 200) {
                    if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null)
                        walletUpiAlertDialog.cancel()

                    val intent = if (passengerDetailsViewModel.country.equals(
                            "India",
                            true
                        ) || passengerDetailsViewModel.country.equals("Indonesia", true)
                    ) {
                        Intent(this, TicketDetailsActivityCompose::class.java)
                    } else {
                        Intent(this, TicketDetailsActivity::class.java)
                    }

                    intent.putExtra(
                        getString(R.string.TICKET_NUMBER),
                        passengerDetailsViewModel.bookTicketPnr
                    )
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
                    if (it?.message != null) {
                        it?.message?.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setWalletOtpObserver() {
        bookingOptionViewModel.walletOtpGeneration.observe(this) {
            if (it != null) {
                if (it.code == 200) {
                    if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                        walletUpiAlertDialog.findViewById<com.google.android.material.textfield.TextInputLayout>(
                            R.id.layout_otp
                        ).visible()

                        walletUpiAlertDialog.findViewById<android.widget.Button>(
                            R.id.btnConfirm
                        ).text = getString(R.string.confirm_validate)
                        walletUpiAlertDialog.findViewById<android.widget.Button>(
                            R.id.btnConfirm
                        ).isClickable = true

                        walletUpiAlertDialog.findViewById<android.widget.Button>(
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
                            it.message?.let { it1 -> toast(it1) }
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
                        it.message?.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setUpiTranxStatusObserver() {
        bookingOptionViewModel.upiTranxStatusObserver.observe(this) {
            if (it != null) {
                if (it.code == 200) {
                    toast(it.message)

                    val intent = if (passengerDetailsViewModel.country.equals("India",
                            true) || passengerDetailsViewModel.country.equals("Indonesia", true)
                    ) {
                        Intent(this, TicketDetailsActivityCompose::class.java)
                    } else {
                        Intent(this, TicketDetailsActivity::class.java)
                    }

                    intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                    intent.putExtra("activityName2", "booking")

                    if (it.pnrNumber?.isNotEmpty() == true) {
                        toast(it.status)
                        intent.putExtra(getString(R.string.TICKET_NUMBER), it.pnrNumber)
                    } else if (it.data?.ticketNumber?.isNotEmpty() == true) {
                        toast(it.message)
                        intent.putExtra(getString(R.string.TICKET_NUMBER), it.data.ticketNumber)
                    } else {
                        toast(getString(R.string.invalid_pnr_number))
                    }
                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                } else if (it.code == 400) {
                    if(it.error?.isNotEmpty()==true && it.error?.contains("Ticket Already Confirmed",true)==true){

                        val intent = if (passengerDetailsViewModel.country.equals("India",
                                true) || passengerDetailsViewModel.country.equals("Indonesia", true)
                        ) {
                            Intent(this, TicketDetailsActivityCompose::class.java)
                        } else {
                            Intent(this, TicketDetailsActivity::class.java)
                        }

                        intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                        intent.putExtra("activityName2", "booking")

                        if (it.pnrNumber?.isNotEmpty() == true) {
                            toast(it.status)
                            intent.putExtra(getString(R.string.TICKET_NUMBER), it.pnrNumber)
                        } else if (it.data?.ticketNumber?.isNotEmpty() == true) {
                            toast(it.message)
                            intent.putExtra(getString(R.string.TICKET_NUMBER), it.data.ticketNumber)
                        } else {
                            toast(getString(R.string.invalid_pnr_number))
                        }
                        intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                        startActivity(intent)
                    }else{
                    if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
                        if (!passengerDetailsViewModel.isCancelledClicked) {
                            callPayStatOfAgentInsRechargStatusApi()
                        }
                    } else if (passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers) {
                        if (!passengerDetailsViewModel.isCancelledClicked) {
                            callBranchUpiTranxStatusApi()
                        }
                    } else {
                        if (!passengerDetailsViewModel.isCancelledClicked) {
                            callUPICheckStatusApi()
                        }
                    }
                }}
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }
    
    private fun callPayStatOfAgentInsRechargStatusApi() {
        if (isNetworkAvailable()) {
            bookingOptionViewModel.getPayStatOfAgentInsRechargStatusApi(
                apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                pnrNumber = passengerDetailsViewModel.bookTicketPnr,
                phone = passengerDetailsViewModel.agentPayViaPhoneNumberSMS,
//                amount = passengerDetailsViewModel.bookTicketTotalFare.toString(),
                isFromAgentRecharge = "${passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents}"
            )
        } else {
            noNetworkToast()
        }
    }

    private fun callBranchUpiTranxStatusApi() {
        if (isNetworkAvailable()) {
            bookingOptionViewModel.getBranchUpiTranxStatusApi(
                apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                pnrNumber = passengerDetailsViewModel.bookTicketPnr,
                branchPhone = passengerDetailsViewModel.userPayViaPhoneNumberSMS
            )
        } else {
            noNetworkToast()
        }
    }
    
    private fun setUpiQrObserver() {
        bookingOptionViewModel.upiCreateQRCodeObserver.observe(this) {

            walletUpiAlertDialog.findViewById<ProgressBar>(
                R.id.progress_bar
            ).gone()


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
                            upiCreateQRAlertDialog.findViewById<android.widget.ImageView>(
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
    }

    private fun callUPICheckStatusApi() {
        val reqBody = com.bitla.ts.domain.pojo.upi_check_status.request.ReqBody(
            apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
            isFromMiddleTier = is_from_middle_tier.toBoolean(),
            pnrNumber = passengerDetailsViewModel.bookTicketPnr,
            isSendSms = true
        )

        bookingOptionViewModel.upiTranxStatusApi(
            reqBody,
            apiType = upi_tranx_status
        )
    }

    private fun setBookExtraTicketObserver() {
        bookingOptionViewModel.bookExtraSeat.observe(this) { it
            passengerDetailsViewModel.showShimmer = false
            if (it != null) {
                if (it.paymentInitiatives == "RedelcomPay") {
                    DialogUtils.createRedelcomPaymentDialog(this, this, this)
                    if (passengerDetailsViewModel.terminalId != "") {
                        handler.postDelayed(Runnable {
                            passengerDetailsViewModel.isHandlerRunning = true
                            handler.postDelayed(
                                runnable!!,
                                passengerDetailsViewModel.delay.toLong()
                            )
                            passengerDetailsViewModel.pnrNumber = it.ticketNumber
                            redelcomViewModel.redelcomPgStatusApi(
                                passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                                passengerDetailsViewModel.locale,
                                passengerDetailsViewModel.pnrNumber,
                                passengerDetailsViewModel.terminalId
                            )
                        }.also { runnable = it }, passengerDetailsViewModel.delay.toLong())

                    }
                }
                else if (passengerDetailsViewModel.selectedPaymentOptionId.toString() == "14" && !passengerDetailsViewModel.phoneBlock) {
                }else if(passengerDetailsViewModel.selectedPaymentOption.asString(this.resources)?.contains("paytm",true)==true && !passengerDetailsViewModel.phoneBlock){
                    passengerDetailsViewModel.pnrNumber =
                        it.ticketNumber
                    generatePaytmPayment(it.paytmPosDetails?.amount.toString())
                } else if (passengerDetailsViewModel.selectedPaymentOptionId.toString() == "14" && !passengerDetailsViewModel.phoneBlock) {
                    passengerDetailsViewModel.isExtraSeatBooking = false
                    ezetapData.apiKey = it.ezetapApiKey ?: ""
                    ezetapData.userName = it.ezetapUserName ?: ""
                    ezetapData.ticketNumber = it.ticketNumber ?: ""
                    ezetapData.amount = it.total_fare ?: ""
                    EzePayAPI()

                }
                // agent recharge
                else if (passengerDetailsViewModel.selectedSubPaymentOptionId == "QR"
                    && it.ticket_status.equals(getString(R.string.pending), ignoreCase = true)
                ) {
//                            toast("Pay via QR")
                    if (it.agentRechargeQrResp?.isNotEmpty() == true) {
                        try {
                            passengerDetailsViewModel.bookTicketPnr = it.ticketNumber
                            
                            upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                                context = this,
                                isFromAgentRechargePG = true,
                                dialogSingleButtonListener = this
                            )
                            
                            val base64String = it.agentRechargeQrResp?.substring(22)
                            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            
                            if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {
                                
                                upiCreateQRAlertDialog.findViewById<ImageView>(
                                    R.id.qr_code_image
                                ).setImageBitmap(decodedImage)
                            }
                            
                            callPayStatOfAgentInsRechargStatusApi()
                        } catch (e:Exception) {
                            toast(getString(R.string.something_went_wrong))
                        }
                    } else if (it.branchUpiQrResp?.isNotEmpty() == true) {
                        try {
                            passengerDetailsViewModel.bookTicketPnr = it.ticketNumber
                            passengerDetailsViewModel.bookTicketTotalFare = it.total_fare

                            upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                                context = this,
                                isFromAgentRechargePG = false,
                                dialogSingleButtonListener = this,
                                isFromBranchUser = true
                            )

                            val base64String = it.branchUpiQrResp?.substring(22)
                            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {

                                upiCreateQRAlertDialog.findViewById<ImageView>(
                                    R.id.qr_code_image
                                ).setImageBitmap(decodedImage)
                            }

                            callBranchUpiTranxStatusApi()
                        } catch (e:Exception) {
                            toast(getString(R.string.something_went_wrong))
                        }
                    } else {
                        toast(getString(R.string.something_went_wrong))
                    }
                }
                else if (passengerDetailsViewModel.selectedSubPaymentOptionId.toString() == "SMS"
                    && it.ticket_status.equals(getString(R.string.pending), ignoreCase = true)
                ) {
//                            toast("Pay via SMS")
                    passengerDetailsViewModel.bookTicketPnr = it.ticketNumber
                    
                    upiAuthSmsAndVPADialog =  DialogUtils.upiAuthSmsAndVPADialog(
                        context = this,
                        isSmsAuth = true,
                        dialogSingleButtonListener = this,
                        isFromBranchUser = passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers && !passengerDetailsViewModel.isAgentLogin
                    )

                    if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
                        callPayStatOfAgentInsRechargStatusApi()
                    } else if (passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers) {
                        callBranchUpiTranxStatusApi()
                    }
                }
                else if (passengerDetailsViewModel.selectedSubPaymentOptionId.toString() == "VPA"
                    && it.ticket_status.equals(getString(R.string.pending), ignoreCase = true)
                ) {
//                            toast("Pay via UPI")
                    passengerDetailsViewModel.bookTicketPnr = it.ticketNumber
                    
                    upiAuthSmsAndVPADialog =  DialogUtils.upiAuthSmsAndVPADialog(
                        context = this,
                        isSmsAuth = false,
                        dialogSingleButtonListener = this,
                        isFromBranchUser = passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers && !passengerDetailsViewModel.isAgentLogin
                    )

                    if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
                        callPayStatOfAgentInsRechargStatusApi()
                    } else if (passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers) {
                        callBranchUpiTranxStatusApi()
                    }
                }
                else if (passengerDetailsViewModel.selectedSubPaymentOptionId == PaymentTypes.PHONEPE_V2 && it.isPhonePeV2Payment
                    && it.ticket_status.equals(getString(R.string.pending), ignoreCase = true)
                ) {
                    passengerDetailsViewModel.bookTicketPnr = it.ticketNumber
                    passengerDetailsViewModel.bookTicketTotalFare = it.total_fare
                    openPhonePeV2(
                        context = this,
                        activityResultLauncher = activityResultLauncher,
                        isLiveEnvironment = it.isLiveEnvironment ?: false,
                        merchantId = it.merchantId,
                        flowId = it.ticketNumber,
                        token = it.token,
                        orderId = it.orderId
                    )
                }
                else if (it.passenger_details != null && it.passenger_details.isNotEmpty()) {
                    val intent = if (passengerDetailsViewModel.country.equals(
                            "India",
                            true
                        ) || passengerDetailsViewModel.country.equals("Indonesia", true)
                    ) {
                        Intent(this, TicketDetailsActivityCompose::class.java)
                    } else {
                        Intent(this, TicketDetailsActivity::class.java)
                    }

                    intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                    intent.putExtra("activityName2", "booking")

                    intent.putExtra(
                        getString(R.string.TICKET_NUMBER),
                        it.passenger_details[0].pnr_number
                    )
                    startActivity(intent)
                    finish()
                }
                else {
                    if (it?.message != null) {
                        toast(it.message.toString())
                    } else {
                        toast(getString(R.string.opps))
                    }
                    if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
                        callReleaseTicketApi()
                    } else if (passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers) {
                        callReleaseBranchUpiBlockedSeatsApi()
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setBookTicketObserver() {
        bookingOptionViewModel.bookTicketFull.observe(this) {
            passengerDetailsViewModel.showShimmer = false
            if (it != null) {
                when (it.code) {
                    200 -> {

                        if (it.result.payment_initiatives == "RedelcomPay") {
                            DialogUtils.createRedelcomPaymentDialog(this, this, this)
                            if (passengerDetailsViewModel.terminalId != "") {
                                handler.postDelayed(
                                    Runnable {
                                        passengerDetailsViewModel.isHandlerRunning = true
                                        handler.postDelayed(
                                            runnable!!,
                                            passengerDetailsViewModel.delay.toLong()
                                        )
                                        passengerDetailsViewModel.pnrNumber =
                                            it.result.ticket_number
                                        redelcomViewModel.redelcomPgStatusApi(
                                            passengerDetailsViewModel.loginModelPref?.api_key
                                                ?: "",
                                            passengerDetailsViewModel.locale,
                                            passengerDetailsViewModel.pnrNumber,
                                            passengerDetailsViewModel.terminalId
                                        )
                                    }.also { runnable = it },
                                    passengerDetailsViewModel.delay.toLong()
                                )

                            }
                        } else if (passengerDetailsViewModel.selectedPaymentOptionId.toString() == "4") {
                            if (passengerDetailsViewModel.isUpiPayment) {
                                passengerDetailsViewModel.bookTicketTotalFare =
                                    it.result.total_fare.toString()
                                passengerDetailsViewModel.bookTicketPnr =
                                    it.result.ticket_number

                                callUPICreateQrCodeApi()

                            } else {
                                if (it?.result != null) {
                                    passengerDetailsViewModel.bookTicketTotalFare = it.result.total_fare.toString()
                                    passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number
                                }

                                if (isNetworkAvailable())
                                    walletOtpGenerationApi()
                                else
                                    noNetworkToast()
                            }

                        }else if(passengerDetailsViewModel.selectedPaymentOption.asString(this.resources)?.contains("paytm",true)==true && !passengerDetailsViewModel.phoneBlock){

                            passengerDetailsViewModel.pnrNumber =
                                it.result.ticket_number
                            generatePaytmPayment(it.result.paytmPosDetails?.amount.toString())
                        } else if (passengerDetailsViewModel.selectedPaymentOptionId.toString() == "14" && !passengerDetailsViewModel.phoneBlock) {

                            passengerDetailsViewModel.isExtraSeatBooking = false
                            ezetapData.apiKey = it.result.ezetapApiKey ?: ""
                            ezetapData.userName = it.result.ezetapUserName ?: ""
                            ezetapData.ticketNumber = it.result.ticket_number ?: ""
                            ezetapData.amount = it.result.total_fare.toString() ?: ""
                            EzePayAPI()

                        }
                        else if (passengerDetailsViewModel.selectedSubPaymentOptionId == "QR"
                            && it.result.ticket_status == getString(R.string.pending)
                        ) {
//                            toast("Pay via QR")
                            
                            if (it.result.agentRechargeQrResp?.isNotEmpty()==true) {
                                try {
                                    passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number
                                    passengerDetailsViewModel.bookTicketTotalFare = it.result.total_fare.toString()
                                    
                                    upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                                        context = this,
                                        isFromAgentRechargePG = true,
                                        dialogSingleButtonListener = this
                                    )
                                    
                                    val base64String = it.result.agentRechargeQrResp?.substring(22)
                                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                    
                                    if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {
                                        
                                        upiCreateQRAlertDialog.findViewById<ImageView>(
                                            R.id.qr_code_image
                                        ).setImageBitmap(decodedImage)
                                    }
                                    
                                    callPayStatOfAgentInsRechargStatusApi()
                                } catch (e:Exception) {
                                    toast(getString(R.string.something_went_wrong))
                                }
                            } else if (it.result.branchUpiQrResp?.isNotEmpty() == true) {
                                try {
                                    passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number

                                    upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                                        context = this,
                                        isFromAgentRechargePG = false,
                                        dialogSingleButtonListener = this,
                                        isFromBranchUser = true
                                    )

                                    val base64String = it.result.branchUpiQrResp?.substring(22)
                                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                                    if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {

                                        upiCreateQRAlertDialog.findViewById<ImageView>(
                                            R.id.qr_code_image
                                        ).setImageBitmap(decodedImage)
                                    }

                                    callBranchUpiTranxStatusApi()
                                } catch (e:Exception) {
                                    toast(getString(R.string.something_went_wrong))
                                }
                            } else {
                                toast(getString(R.string.something_went_wrong))
                            }
                            
                        }
                        else if (passengerDetailsViewModel.selectedSubPaymentOptionId.toString() == "SMS"
                            && it.result.ticket_status == getString(R.string.pending)
                        ) {
//                            toast("Pay via SMS")
                            passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number
                            passengerDetailsViewModel.bookTicketTotalFare = it.result.total_fare.toString()
                            
                            
                            upiAuthSmsAndVPADialog = DialogUtils.upiAuthSmsAndVPADialog(
                                context = this,
                                isSmsAuth = true,
                                dialogSingleButtonListener = this,
                                isFromBranchUser = passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers && !passengerDetailsViewModel.isAgentLogin
                            )

                            if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
                                callPayStatOfAgentInsRechargStatusApi()
                            } else if (passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers) {
                                callBranchUpiTranxStatusApi()
                            }
                        }
                        else if (passengerDetailsViewModel.selectedSubPaymentOptionId.toString() == "VPA"
                            && it.result.ticket_status == getString(
                                R.string.pending
                            )
                        ) {
//                            toast("Pay via UPI")
                            passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number
                            passengerDetailsViewModel.bookTicketTotalFare = it.result.total_fare.toString()
                            
                            upiAuthSmsAndVPADialog = DialogUtils.upiAuthSmsAndVPADialog(
                                context = this,
                                isSmsAuth = false,
                                dialogSingleButtonListener = this,
                                isFromBranchUser = passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers && !passengerDetailsViewModel.isAgentLogin
                            )

                            if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
                                callPayStatOfAgentInsRechargStatusApi()
                            } else if (passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers) {
                                callBranchUpiTranxStatusApi()
                            }
                        }
                        else if (passengerDetailsViewModel.selectedSubPaymentOptionId.toString() == "UPI INTENT"
                            && it.result.ticket_status.equals(getString(R.string.pending), ignoreCase = true) && !passengerDetailsViewModel.phoneBlock
                        ) {
                            passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number
                            passengerDetailsViewModel.bookTicketTotalFare = it.result.total_fare.toString()
                            payViaApps(it.result)
                        }
                        else if (passengerDetailsViewModel.selectedSubPaymentOptionId == PaymentTypes.PHONEPE_V2 && it.result.isPhonePeV2Payment
                            && it.result.ticket_status.equals(getString(R.string.pending), ignoreCase = true)
                        ) {
                            passengerDetailsViewModel.bookTicketPnr = it.result.ticket_number
                            passengerDetailsViewModel.bookTicketTotalFare = it.result.total_fare.toString()
                            openPhonePeV2(
                                context = this,
                                activityResultLauncher = activityResultLauncher,
                                isLiveEnvironment = it.result.isLiveEnvironment ?: false,
                                merchantId = it.result.merchantId,
                                flowId = it.result.ticket_number,
                                token = it.result.token,
                                orderId = it.result.orderId
                            )
                        }
                        else {

                            val intent = if (passengerDetailsViewModel.country.equals(
                                    "India",
                                    true
                                ) || passengerDetailsViewModel.country.equals("Indonesia", true)
                            ) {
                                Intent(this, TicketDetailsActivityCompose::class.java)
                            } else {
                                Intent(this, TicketDetailsActivity::class.java)
                            }

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
                        if (it.message == "Transaction number already used" && passengerDetailsViewModel.selectedPaymentOptionId == 2) {
                            DialogUtils.creditDebitDialog(this, this)
                        }
                        passengerDetailsViewModel.selectedPaymentOptionId = 1
                        toast(it.message.toString())
                        /* DialogUtils.createRedelcomPaymentDialog(this, this, this)
                         if (passengerDetailsViewModel.terminalId != "") {
                             passengerDetailsViewModel.pnrNumber = it.result.ticket_number
                             redelcomViewModel.redelcomPgStatusApi(
                                 passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                                 passengerDetailsViewModel.locale,
                                 passengerDetailsViewModel.pnrNumber,
                                 passengerDetailsViewModel.terminalId
                             )
                         }*/

                    }

                    else -> {
                        if (it.message != null) {
                            toast(it.message.toString())
                        }
                        if (::walletUpiAlertDialog.isInitialized) {
                            walletUpiAlertDialog.findViewById<ProgressBar>(
                                R.id.progress_bar
                            ).gone()
                        }
                        passengerDetailsViewModel.showShimmer = false
                        if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
                            callReleaseTicketApi()
                        } else if (passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers) {
                            callReleaseBranchUpiBlockedSeatsApi()
                        }
                    }
                }


            } else {
                toast(getString(R.string.server_error))
            }
        }
    }



    private fun payViaApps(result: Result) {
        val paymentMode=result.paymentMode
        if(!paymentMode?.accessKey.isNullOrEmpty()) {
            if(!paymentMode?.upiLink.isNullOrEmpty()) {
                val upiLink = paymentMode?.upiLink
                if (upiLink?.isNotEmpty()==true && isUpiAppPresent(this)) {
                    val intent = Intent()
                    intent.setAction(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(upiLink))
                    val chooser = Intent.createChooser(intent, "Pay with")
                    startActivityForResult(chooser, REQUEST_PAYMENT_INTENT_FLOW, null)
                }
            }
        } else {
            toast("Access Key is null")
            return
        }
    }

    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        passengerDetailsViewModel.isCancelledClicked = false
        passengerDetailsViewModel.showPhonePeV2PendingDialog = true
        callPhonePeV2StatusApi()
    }

    private fun callPhonePeV2StatusApi() {
        if (isNetworkAvailable())
            agentRechargeViewModel.getPhonePeV2Status(
                apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                orderId = passengerDetailsViewModel.bookTicketPnr
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
                                if (!passengerDetailsViewModel.isCancelledClicked) {
                                    Handler(Looper.getMainLooper()).postDelayed(1000) {
                                        callPhonePeV2StatusApi()
                                    }
                                }
                                if (passengerDetailsViewModel.showPhonePeV2PendingDialog) {
                                    passengerDetailsViewModel.showPhonePeV2PendingDialog = false
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
                                if (passengerDetailsViewModel.isAgentLogin) {
                                    callReleaseTicketApi()
                                } else {
                                    callReleaseBranchUpiBlockedSeatsApi()
                                }
                            }
                            else -> {
                                closePhonePeV2PendingDialog()
                                toast(getString(R.string.server_error))
                                if (passengerDetailsViewModel.isAgentLogin) {
                                    callReleaseTicketApi()
                                } else {
                                    callReleaseBranchUpiBlockedSeatsApi()
                                }
                            }
                        }
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        toast(getString(R.string.something_went_wrong))
                        if (passengerDetailsViewModel.isAgentLogin) {
                            callReleaseTicketApi()
                        } else {
                            callReleaseBranchUpiBlockedSeatsApi()
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
                if (passengerDetailsViewModel.isAgentLogin) {
                    callReleaseTicketApi()
                } else {
                    callReleaseBranchUpiBlockedSeatsApi()
                }
            }
        }
    }

    private fun closePhonePeV2PendingDialog() {
        if (::phonePeV2PendingDialog.isInitialized) {
            phonePeV2PendingDialog.dismiss()
        }
    }

    private fun hitPhonePeV2SuccessConPay() {
        passengerDetailsViewModel.showShimmer = true
        if (isNetworkAvailable()) {
            bookingOptionViewModel.confirmPhonePeV2PendingSeat(
                pnrNumber = passengerDetailsViewModel.bookTicketPnr
            )
        } else {
            noNetworkToast()
        }
    }

    private fun phonePeV2TicketSuccessConPayObserver() {
        bookingOptionViewModel.confirmPhonePeV2PendingSeatResponse.observe(this) {
            if (it != null) {
                passengerDetailsViewModel.showShimmer = false
                val intent = Intent(this, TicketDetailsActivityCompose::class.java)
                intent.putExtra("activityName2", "booking")
                intent.putExtra(getString(R.string.TICKET_NUMBER), passengerDetailsViewModel.bookTicketPnr)

                startActivity(intent)
                finish()
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun generatePaytmPayment(totalFare: String) {
        val edcPackage="com.paytm.pos.debug"
//       val edcPackage="com.paytm.pos"

        val packageName=packageName
        val payDeepLink="paytmedc://paymentV2"
        val callBackAction="com.paytm.pos.payment.CALL_BACK_RESULT_NEW_FLOW"
        val orderId= passengerDetailsViewModel.pnrNumber
        val payMode="all"
        val amount=totalFare
        val deepLink= "paytmedc://paymentV2?" + "callbackAction=" + callBackAction + "&stackClear=true" +
                "&callbackPkg=" + packageName + "&callbackDl=" +  payDeepLink + "&requestPayMode=" + payMode +
                "&orderId=" + orderId + "&amount=" + amount



        val launchIntent=packageManager.getLaunchIntentForPackage(edcPackage)
        if (launchIntent != null) {
            launchIntent.putExtra("deeplink", deepLink)
            launchIntent.flags=Intent.FLAG_ACTIVITY_NO_HISTORY
            Log.d("Payment Link Paytm",deepLink.toString())
            startActivity(launchIntent)
        }

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

        passengerDetailsViewModel.showShimmer = true

        val reqBody = PaytmPosTxnStatusRequest(
            apiKey = passengerDetailsViewModel.loginModelPref?.api_key?:"",
            connectingPnrNumber = "",
            destination = passengerDetailsViewModel.destinationId,
            isExtraSeat = passengerDetailsViewModel.isExtraSeat,
            isRoundTrip = false,
            isSendSms = false,
            origin = passengerDetailsViewModel.sourceId,
            paytmPosResponse = paytmObj,
            paytmPosPaymentType = 59,
            pnrNumber = passengerDetailsViewModel.pnrNumber?:"",
            reservationId = passengerDetailsViewModel.resId.toString()?:""
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






    private fun setPaytmTxnStatusObserver() {
        bookingOptionViewModel.paytmPosTxnStatusResponse.observe(this) {

            if (it?.code != null)
                when (it.code) {
                    200 -> {
                        if (passengerDetailsViewModel.showShimmer) {
                            passengerDetailsViewModel.showShimmer = false
                        }

//                    val intent = if(passengerDetailsViewModel.country.equals("India", true) || passengerDetailsViewModel.country.equals("Indonesia", true)) {
//                        Intent(this, TicketDetailsActivityCompose::class.java)
//                    } else {
//                        Intent(this, TicketDetailsActivity::class.java)
//                    }

                        val intent = Intent(this, TicketDetailsActivityCompose::class.java)


//                    if (it.responseCode == 0 && it.data!!.code == null) {
//                    val intent = Intent(this, TicketDetailsActivity::class.java)
                        intent.putExtra(
                            "activityName",
                            BookingPaymentOptionsActivity::class.java
                        )
                        intent.putExtra("activityName2", "booking")

                        intent.putExtra(
                            getString(R.string.TICKET_NUMBER),
                            passengerDetailsViewModel.pnrNumber
                        )
                        intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                        startActivity(intent)
                        finish()
//                    } else if (it.message == "This ticket is released and now its ready for re-booking.") {
//                        if (passengerDetailsViewModel.showShimmer) {
//                            passengerDetailsViewModel.showShimmer = false
//                        }
//                        toast(getString(R.string.payment_failed_please_try_again))
//                    } else {
//                        toast(it.data!!.message)
//                    }
                    }

                    211 -> {
                        if (passengerDetailsViewModel.showShimmer) {
                            passengerDetailsViewModel.showShimmer = false
                        }
                        toast(getString(R.string.payment_failed_please_try_again))
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
                        toast(it.message.toString())
                    }

                    else -> {
                        toast(it.message.toString())
                    }

                }
        }
    }




    private fun EzePayAPI() {

        val jsonRequest = JSONObject()
        val jsonOptionalParams = JSONObject()
        val jsonReferences = JSONObject()
        val jsonCustomer = JSONObject()
        val jsonUpi = JSONObject()

        //Building Customer Object

        //Building Customer Object
        jsonCustomer.put("name", "")
        jsonCustomer.put("mobileNo", "")
        jsonCustomer.put("email", "")


        jsonReferences.put("reference1", ezetapData.ticketNumber)

        jsonOptionalParams.put("references", jsonReferences)
        jsonOptionalParams.put("customer", jsonCustomer)


        //Building final request object


        //Building final request object
        jsonRequest.put("amount", ezetapData.amount)
        jsonUpi.put("payerVPA", "xyz@upi");


        jsonRequest.put("options", jsonOptionalParams)
        jsonRequest.put("upi", jsonUpi)
//        EzeAPI.pay(this, REQUEST_CODE_PAY_EZETAP, jsonRequest)


    }

    private fun walletOtpGenerationApi() {
        val reqBody = com.bitla.ts.domain.pojo.wallet_otp_generation.request.ReqBody(
            amount = passengerDetailsViewModel.bookTicketTotalFare,
            api_key = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
            is_from_middle_tier = is_from_middle_tier.toBoolean(),
            pnr_number = passengerDetailsViewModel.bookTicketPnr,
            wallet_mobile = passengerDetailsViewModel.walletMobileNo,
            wallet_type = passengerDetailsViewModel.selectedWalletUpiOptionId.toString(),
            locale = passengerDetailsViewModel.locale,
            is_resend_otp = false
        )

        bookingOptionViewModel.walletOtpGenerationApi(
            reqBody,
            apiType = wallet_otp_generation_method_name
        )
    }

    private fun validateWalletOtpApi(otp: String) {
        val reqBody = com.bitla.ts.domain.pojo.validate_otp_wallets.request.ReqBody(
            amount = passengerDetailsViewModel.bookTicketTotalFare,
            api_key = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
            is_from_middle_tier = is_from_middle_tier.toBoolean(),
            otp_number = otp,
            phone_blocked = passengerDetailsViewModel.isPhoneBlockedWallet,
            pnr_number = passengerDetailsViewModel.bookTicketPnr,
            wallet_mobile = passengerDetailsViewModel.walletMobileNo,
            wallet_type = passengerDetailsViewModel.selectedWalletUpiOptionId.toString(),
            locale = passengerDetailsViewModel.locale
        )

        bookingOptionViewModel.validateWalletOtpApi(
            reqBody,
            apiType = validate_otp_wallets_method_name
        )
    }

    private fun callUPICreateQrCodeApi() {
        val reqBody = com.bitla.ts.domain.pojo.upi_create_qr.request.ReqBody(
            amount = passengerDetailsViewModel.bookTicketTotalFare,
            apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
            isFromMiddleTier = is_from_middle_tier.toBoolean(),
            pnrNumber = passengerDetailsViewModel.bookTicketPnr,
            userNumber = passengerDetailsViewModel.walletMobileNo,
            upiType = 2

        )

        bookingOptionViewModel.upiCreateQrCodeApi(
            reqBody,
            apiType = upi_create_qr_code
        )
    }

    private fun setFareBreakupObserver() {
        bookingOptionViewModel.fareBreakup.observe(this) {
            passengerDetailsViewModel.isFareBreakupApiCalled = false
            if (it != null) {
                passengerDetailsViewModel.showShimmer = false
                when (it.code) {
                    200 -> {
                        if (it.fare_break_up_hash != null) {
                            val fareBreakup = mutableListOf<FareBreakUpHash>()
                            for (i in 0..it.fare_break_up_hash.size.minus(1)) {
                                if (it.fare_break_up_hash[i].value.toString().toDouble() > 0.0)
                                    fareBreakup.add(it.fare_break_up_hash[i])
                            }
                            passengerDetailsViewModel.setFareBreakupDetails(fareBreakup)
                            // checking for if total fare comes as negative/minus
                            passengerDetailsViewModel.totalFareString =
                                if (it.total_fare.toString().contains("-")) {
                                    "0.0"
                                } else {
                                    it.total_fare.toString()
                                }
                            
                            passengerDetailsViewModel.payableAmount = it.payble_amount.toString().toDouble()
                            
                            // hide for now
                            //setPassengerInfo()
                            handlePartialPaymentPrivilege(
                                this,
                                passengerDetailsViewModel,
                                onPartialPaymentChange = {
                                    paymentRadioListener(
                                        passengerDetailsViewModel
                                    )
                                })

                            val boldPartialAmt =
                                SpannableStringBuilder().append("${getString(R.string.total_amount)}:")
                                    .bold {
                                        append(
                                            " ${passengerDetailsViewModel.privilegeResponseModel?.currency ?: ""} ${
                                                passengerDetailsViewModel.totalFareString.toDouble()
                                                    .convert(passengerDetailsViewModel.currencyFormat)
                                            }"
                                        )
                                    }
                            passengerDetailsViewModel.fullPartialTotalAmount =
                                boldPartialAmt.toString()
                        }
                        passengerDetailsViewModel.isFareBreakupApiCalled = false
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
    }


    private fun setUserObserver() {
        blockViewModel.userList.observe(this) { it ->

            if (it != null) {
                if (it.active_users != null && it.active_users.isNotEmpty()) {
                    if (passengerDetailsViewModel.selectedBranchId == 0) {
                        passengerDetailsViewModel.bookingAgentList.clear()
                        it.active_users.forEach {
                            val spinnerItems = SpinnerItems(it.id, it.label)
                            passengerDetailsViewModel.bookingAgentList.add(spinnerItems)
                        }
                        saveAgentList(passengerDetailsViewModel.bookingAgentList)
                    } else {
                        passengerDetailsViewModel.userList.clear()
                        it.active_users.forEach {
                            val spinnerItems = SpinnerItems(
                                id = it.id,
                                value = it.label,
                                role_discount = it.role_discount.toString().toDoubleOrNull()
                            )
                            passengerDetailsViewModel.userList.add(spinnerItems)
                        }
                        saveUserList(passengerDetailsViewModel.userList)
                        val intent = Intent(this, SearchActivity::class.java)
                        intent.putExtra(
                            getString(R.string.CITY_SELECTION_TYPE),
                            getString(R.string.selectUser)
                        )
                        startActivityForResult(intent, RESULT_CODE_SEARCH_USER)
                    }
                } else {
                    passengerDetailsViewModel.bookingAgentList.clear()
                    saveAgentList(passengerDetailsViewModel.bookingAgentList)
                    if (it.message != null) {
                        toast(it.message)
                        passengerDetailsViewModel.onBehalfOfAgentName = ""
                        passengerDetailsViewModel.onBehalfOfAgentId = 0
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setBranchObserver() {
        blockViewModel.branchList.observe(this) { it ->
            if (it != null) {
                if (it.branchlists.isNotEmpty()) {
                    passengerDetailsViewModel.branchList.clear()
                    it.branchlists.forEach {
                        val spinnerItems = SpinnerItems(
                            id = it.id,
                            value = it.label,
                            branch_discount = it.branch_discount.toString().toDoubleOrNull()
                        )
                        passengerDetailsViewModel.branchList.add(spinnerItems)
                    }
                }
                saveBranchList(passengerDetailsViewModel.branchList)

                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(
                    getString(R.string.CITY_SELECTION_TYPE),
                    getString(R.string.selectBranch)
                )
                startActivityForResult(intent, RESULT_CODE_SEARCH_BRANCH)
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setOnBehalfAgentObserver() {
        blockViewModel.userList.observe(this) { it ->
            if (it != null) {
                if (it.active_users != null && it.active_users.isNotEmpty()) {
                    if (passengerDetailsViewModel.selectedBranchId == 0) {
                        passengerDetailsViewModel.bookingAgentList.clear()
                        it.active_users.forEach {
                            val spinnerItems = SpinnerItems(it.id, it.label)
                            passengerDetailsViewModel.bookingAgentList.add(spinnerItems)
                        }
                        saveAgentList(passengerDetailsViewModel.bookingAgentList)

                        if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.selectedBookingTypeId == 33) {
                            val intent = Intent(
                                this@PassengerPaymentNewFlowActivity,
                                SearchActivity::class.java
                            )
                            intent.putExtra(
                                getString(R.string.CITY_SELECTION_TYPE),
                                getString(R.string.selectAgent)
                            )
                            startActivityForResult(
                                intent,
                                RESULT_CODE_SEARCH_AGENT
                            )
                        }
                    } else {
                        passengerDetailsViewModel.userList.clear()
                        it.active_users.forEach {
                            val spinnerItems = SpinnerItems(it.id, it.label)
                            passengerDetailsViewModel.userList.add(spinnerItems)
                        }
                        saveUserList(passengerDetailsViewModel.userList)
                        /* val intent = Intent(this, SearchActivity::class.java)
                         intent.putExtra(
                             getString(R.string.CITY_SELECTION_TYPE),
                             getString(R.string.selectUser)
                         )
                         startActivityForResult(intent, RESULT_CODE_SEARCH_USER)*/
                    }
                } else {
                    passengerDetailsViewModel.bookingAgentList.clear()
                    saveAgentList(passengerDetailsViewModel.bookingAgentList)
                    if (it.message != null)
                        toast(it.message)
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setCityObserver() {
        cityDetailViewModel.cityDetailResponse.observe(this) {
            if (it != null) {
                if (it.code == 200) {
                    val cityList = it.result
                    saveCityList(cityList)

                    val intent = Intent(this, SearchActivity::class.java)
                    intent.putExtra(
                        getString(R.string.CITY_SELECTION_TYPE),
                        getString(R.string.CITY_SELECTION)
                    )
                    startActivityForResult(intent, RESULT_CODE_SOURCE)
                } else {
                    if (it.message != null)
                        toast(it.message)
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun checkBookingTypeCardVisibility() {
        if (passengerDetailsViewModel.roleType == getString(R.string.role_agent) || passengerDetailsViewModel.roleType == getString(
                R.string.role_field_officer
            )
        ) {
            if (passengerDetailsViewModel.selectedBookingType?.asString(
                    resources
                ) == getString(R.string.walkin)
            )
                passengerDetailsViewModel.setBookingCardVisibility(false)

            if (passengerDetailsViewModel.roleType == getString(R.string.role_agent)) {
                passengerDetailsViewModel.setBookingCardVisibility(true)
            }
        } else
            passengerDetailsViewModel.setBookingCardVisibility(true)
    }


    private fun getRole() {
        if (passengerDetailsViewModel.loginModelPref != null)
            role = getUserRole(
                passengerDetailsViewModel.loginModelPref!!,
                isAgentLogin = passengerDetailsViewModel.isAgentLogin,
                this
            )
        role?.let { passengerDetailsViewModel.setRoleType(it) }
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun PassengerDetailsScreen() {

        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = 0,
            initialFirstVisibleItemScrollOffset = 0
        )
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                Toolbar()
            }
        ) { innerPadding ->
            BoxWithConstraints {
                Box(
                    modifier = Modifier
                        .semantics {
                            testTagsAsResourceId = true
                        }
                        .padding(
                            androidx.compose.foundation.layout.WindowInsets
                                .systemBars  // includes status + nav bar
                                .only(WindowInsetsSides.Bottom)
                                .asPaddingValues()
                        )
                        .background(colorResource(id = R.color.button_color))
                        .height(this@BoxWithConstraints.maxHeight)
                        .width(this@BoxWithConstraints.maxWidth)
                        .padding(innerPadding)
                ) {

                    if (passengerDetailsViewModel.showShimmer) {

                        Dialog(
                            onDismissRequest = { !passengerDetailsViewModel.showShimmer },
                            DialogProperties(
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false
                            )
                        ) {
                            Box(
                                contentAlignment = Center,
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        color = (White.copy(alpha = 0.0f)),
//                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colors.secondary)
                            }
                        }
                    }

                    Column {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.weight(.92f)
                        ) {
                            
                            item {
                                BpDpCard(passengerDetailsViewModel,
                                    onBoardingPointSelection = {
                                    
                                    },
                                    onDroppingPointSelection = {
                                    
                                    }
                                )
                            }

                            passengerDetailsViewModel.apply {

                                if (!isSeatWiseDiscountEdit && isDiscountAmountChanged) {
                                    passengerDetailsViewModel.showShimmer = true
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index = 2)
                                    }
                                }

                                if (!isExtraSeat) {
                                    item {
                                        CardComponent(
                                            shape = RoundedCornerShape(8.dp),
                                            bgColor = colorResource(id = R.color.white), modifier = Modifier
                                                .padding(top = 4.dp, start = 8.dp, end = 8.dp)
                                                .fillMaxWidth()
                                                .wrapContentHeight(),
                                            onClick = {}
                                        )
                                        {
                                            Column {
                                                if (rapidBookingType != RAPID_TYPE_HIDE) {
                                                    RapidBookingBookingTypeCard(passengerDetailsViewModel = passengerDetailsViewModel,
                                                        onRapidBookingCheck = {
                                                            rapidBookingSkip = it
                                                            if (it) {
                                                                rapidBookingType = 0
                                                                isFareBreakupApiCalled = true
                                                            } else {
                                                                rapidBookingType = 1
                                                                isRapidBooking = "false"
                                                                isAllMandatoryFieldsFilled.clear()
                                                                passengerDetailsViewModel.showShimmer = true
                                                                isBookingTypeValueChanged = true
                                                                coroutineScope.launch {
                                                                    listState.animateScrollToItem(index = 1)
                                                                }
                                                            }
                                                            setPaymentOptionsVisibility(this@PassengerPaymentNewFlowActivity,passengerDetailsViewModel)
                                                        }
                                                    )
                                                }


                                                if (rapidBookingType != 0) {
                                                    if (isBookingTypeCardVisible || isPhoneBookingVisible) {
                                                        BookingTypeCardNewFlow(role,
                                                            passengerDetailsViewModel,
                                                            this@PassengerPaymentNewFlowActivity,
                                                            onClick = {
                                                                onBookingTypeSelection(it)
                                                            },
                                                            onPhoneBookingClick = {
                                                                DialogUtils.phoneBlockingDialog(
                                                                    this@PassengerPaymentNewFlowActivity,
                                                                    this@PassengerPaymentNewFlowActivity,
                                                                    passengerDetailsViewModel.privilegeResponseModel?.isPermanentPhoneBooking
                                                                        ?: false,
                                                                    removePreSelectionOptionInTheBooking = passengerDetailsViewModel.removePreSelectionOptionInTheBooking,
                                                                    hours = passengerDetailsViewModel.calculatedHours.toString(),
                                                                    minutes = passengerDetailsViewModel.calculatedMinutes.toString(),
                                                                    amOrpm = passengerDetailsViewModel.checkAMOrPM,
                                                                    selectedDate = passengerDetailsViewModel.selectedDate,
                                                                    isPhoneBlockedDateChanged = passengerDetailsViewModel.isPhoneBlockedDateChanged
                                                                )
                                                            },
                                                            onBookingTypeClick = {
//                                                      isBookingTypeValueChanged = true
//                                                      coroutineScope.launch {
//                                                      listState.animateScrollToItem(index = 1)
//                                                      }
                                                                passengerDetailsViewModel.isFareBreakupApiCalled = true
                                                                coroutineScope.launch {
                                                                    listState.animateScrollToItem(index = 1)
                                                                }
                                                            }
                                                        )
                                                    }

                                                    if (passengerDetailsViewModel.country.equals("india", true)) {
                                                        if (passengerDetailsViewModel.rapidBookingType != 0) {
                                                            if (!passengerDetailsViewModel.isAdditionalOfferCardVisible) {
                                                                SpaceComponent(modifier = Modifier.height(4.dp))

                                                                SpecialBookingCard(
                                                                    passengerDetailsViewModel
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (isPhoneDialogVisible && (isAgentLogin ||
                                            (privilegeResponseModel?.allowToSwitchSinglePageBooking != null && privilegeResponseModel?.allowToSwitchSinglePageBooking!!)))
                                {
                                    DialogUtils.phoneBlockingDialog(
                                        this@PassengerPaymentNewFlowActivity,
                                        this@PassengerPaymentNewFlowActivity,
                                        passengerDetailsViewModel.privilegeResponseModel?.isPermanentPhoneBooking
                                            ?: false,
                                        removePreSelectionOptionInTheBooking = passengerDetailsViewModel.removePreSelectionOptionInTheBooking,
                                        hours = passengerDetailsViewModel.calculatedHours.toString(),
                                        minutes = passengerDetailsViewModel.calculatedMinutes.toString(),
                                        amOrpm = passengerDetailsViewModel.checkAMOrPM,
                                        selectedDate = passengerDetailsViewModel.selectedDate,
                                        isPhoneBlockedDateChanged = passengerDetailsViewModel.isPhoneBlockedDateChanged
                                    )
                                    setPhoneDialogViewVisible(false)
                                }
                                
                                if (rapidBookingType != 0) {
                                    item { ContactDetailsCardNewFlow(passengerDetailsViewModel) }
                                    
                                    if (isInsuranceCardVisible) item {
                                        InsuranceCard(
                                            passengerDetailsViewModel,
                                            onInsuranceChecked = {
                                                passengerDetailsViewModel.isInsuranceChecked.value = it
                                                handlePartialPaymentPrivilege(this@PassengerPaymentNewFlowActivity,
                                                    passengerDetailsViewModel,
                                                    onPartialPaymentChange = {
                                                        paymentRadioListener(
                                                            passengerDetailsViewModel
                                                        )
                                                    })
                                                passengerDetailsViewModel.isFareBreakupApiCalled =
                                                    true
                                            })
                                    }
                                    
                                    if (isEnableCampaignPromotions && (isExtraSeat == false)) {
                                        item {
                                            AgentDiscountCard(
                                                passengerDetailsViewModel = passengerDetailsViewModel
                                            ) {
                                                if (it) {
                                                    callCampaignsAndPromotionsDiscountApi()
                                                } else {
                                                    fareBreakupApi()
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                itemsIndexed (passengerDataList) { index, item ->
                                    val safeOriginalSeatList: MutableList<String> = originalSeatList ?: mutableListOf()
                                    ItemPassengerDetailsNewFlow(this@PassengerPaymentNewFlowActivity,
                                        passengerDetailsViewModel = passengerDetailsViewModel,
                                        item = item,
                                        paxIndex = index,
                                        onMealChecked = {
                                            passengerDetailsViewModel.passengerDataList[index].mealRequired =
                                                it
                                        },
                                        originalFares = originalFares ?: mutableListOf(),
                                        originalSeatList = safeOriginalSeatList,
                                        onFareChange = { seatNumber, newFare, _ ->
                                            if(newFare.isNotBlank() && newFare != null) {
                                                modifiedFareDone[index] = (newFare.toDoubleOrNull() ?: 0.0).toString()
                                                if (passengerDetailsViewModel.country.equals("india", true) &&
                                                    passengerDetailsViewModel.editFareMandatoryForAgentUser &&
                                                    passengerDetailsViewModel.isAllowedEditFare
                                                ) {
                                                    val allSeatsValid = hasFareChanges(originalFares, modifiedFareDone)

                                                    if (allSeatsValid) {
                                                        if (index != -1) {
                                                            modifiedFares?.set(index, newFare)
                                                            lastCheckedSeat = seatNumber
                                                            lastCheckedFare = newFare
                                                        }

                                                        lifecycleScope.launch {
                                                            if (isNetworkAvailable()) {
                                                                fareBreakupApi()
                                                            }
                                                        }
                                                    } else {
                                                        showCustomFareUpdateToast(
                                                            this@PassengerPaymentNewFlowActivity,
                                                            getString(R.string.update_fare_validation)
                                                        )
                                                    }
                                                }else{
                                                    if(newFare.isNotBlank() && newFare != null){
                                                        if (index != -1) {
                                                            modifiedFares?.set(index, newFare)
                                                            lastCheckedSeat = seatNumber
                                                            lastCheckedFare = newFare
                                                        }

                                                        lifecycleScope.launch {
                                                            if (isNetworkAvailable()) {
                                                                fareBreakupApi()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                                
                                if (rapidBookingType != 0) {
                                    
                                    if (!isAdditionalOfferCardVisible) item {
                                        AdditionalOfferTypesCard(
                                            passengerDetailsViewModel
                                        )
                                    }
                                    
//                                    if (privilegeResponseModel?.allowToSendSmsOnBooking == true) item {
//                                        SendSmsOnBookingCard(
//                                            passengerDetailsViewModel
//                                        )
//                                    }
//
//                                    if (privilegeResponseModel?.allowToShowWhatsappCheckboxInBookingPage == true) item {
//                                        SendSmsOnWhatsAppBookingCard(
//                                            passengerDetailsViewModel
//                                        )
//                                    }
                                    
                                    if (isRemarksCardVisible) item {
                                        RamarksCardNewFlow(
                                            passengerDetailsViewModel
                                        )
                                    }
                                    
                                    if (isFullPartialCardVisible) item {
                                        FullPartialPaymentCardNewFlow(this@PassengerPaymentNewFlowActivity,
                                            passengerDetailsViewModel,
                                            onPaymentTypeSelection = {
                                                when (it) {
                                                    getString(R.string.full_payment) -> {
                                                        passengerDetailsViewModel.apply {
                                                            isPartialPaymentInfoVisible = false
                                                            isShowReleaseDate = false
                                                            isShowReleaseTime = false
                                                        }
                                                    }
                                                    
                                                    getString(R.string.partial_payment) -> {
                                                        passengerDetailsViewModel.isPartialPaymentInfoVisible =
                                                            true
                                                        releaseRadioListener(
                                                            this@PassengerPaymentNewFlowActivity,
                                                            passengerDetailsViewModel
                                                        )
                                                    }
                                                    
                                                    getString(R.string.do_not_release) -> {
                                                        passengerDetailsViewModel.isShowReleaseDate =
                                                            false
                                                        passengerDetailsViewModel.isShowReleaseTime =
                                                            false
                                                    }
                                                    
                                                    else -> {
                                                        passengerDetailsViewModel.isShowReleaseDate =
                                                            true
                                                        passengerDetailsViewModel.isShowReleaseTime =
                                                            true
                                                    }
                                                }
                                                paymentRadioListener(
                                                    passengerDetailsViewModel
                                                )
                                            },
                                            onReleaseDateClick = {
                                                openCalender()
                                            })
                                    }
                                    
                                    if (passengerDetailsViewModel.isAgentLogin && isAllowUpiForDirectPgBookingForAgents) {
                                        item { SpaceComponent(modifier = Modifier.padding(top = 8.dp)) }


                                        item {
                                            PaymentOptions(
                                                this@PassengerPaymentNewFlowActivity,
                                                passengerDetailsViewModel,
                                                onPaymentOptionSelection = {
                                                    onPaymentOptionSelection(it)
                                                }
                                            )
                                        }
                                    } else if (!passengerDetailsViewModel.isAgentLogin && isAllowUpiForDirectPgBookingForUsers){
                                        if (!isPaymentOptionCardVisible) {
                                            item { SpaceComponent(modifier = Modifier.padding(top = 8.dp)) }
                                        }

                                        if (isPaymentOptionCardVisible) {
                                            item {
                                                PaymentOptions(
                                                    this@PassengerPaymentNewFlowActivity,
                                                    passengerDetailsViewModel,
                                                    onPaymentOptionSelection = {
                                                        onPaymentOptionSelection(it)
                                                    }
                                                )
                                            }
                                        }
                                    } else {
                                        if (!isPaymentOptionCardVisible) {
                                            item { SpaceComponent(modifier = Modifier.padding(top = 8.dp)) }
                                        }
                                        
                                        if (isPaymentOptionCardVisible) {
                                            item {
                                                PaymentOptionsNewFlow(
                                                    this@PassengerPaymentNewFlowActivity,
                                                    passengerDetailsViewModel,
                                                    onPaymentOptionSelection = {
                                                        onPaymentOptionSelection(it)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    
                                }
                                
                                if (isEnableCampaignPromotions && isEnableCampaignPromotionsChecked && showAgentDiscountPerBookingCard) {
                                    item {
                                        AgentDiscountPerBookingCard(passengerDetailsViewModel)
                                    }
                                }
                            }

                        }

                        Column(
                            modifier = Modifier.wrapContentHeight(),
//                            modifier = if (passengerDetailsViewModel.isFareBreakupBottomSheetVisible) Modifier.height(
//                                250.dp
//                            ) else if (passengerDetailsViewModel.getAvailableBalance.isNotEmpty()) Modifier.height(
//                                92.dp
//                            ) else Modifier.height(46.dp)
                        ) {

                            if (passengerDetailsViewModel.getAvailableBalance.isNotEmpty())
                                AccountBalanceCard(passengerDetailsViewModel = passengerDetailsViewModel)

                            FareBreakupBottomSheet(passengerDetailsViewModel)

                            BookTicket(onClick = {
                                if (it == BOOK_TICKET) {
//                                    passengerDetailsViewModel.getPassengersList()
                                    if (!passengerDetailsViewModel.rapidBookingSkip && passengerDetailsViewModel.isRemarkMandatory && passengerDetailsViewModel.remarks.isEmpty()) {
                                        toast(getString(R.string.validate_remarks))
                                    } else if (passengerDetailsViewModel.selectedBookingTypeId == 1 && passengerDetailsViewModel.onBehalfOfAgentName.isEmpty()) {
                                        toast(getString(R.string.error_select_online_agent))
                                    } else if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.selectedBookingTypeId == 33 && passengerDetailsViewModel.onBehalfOfAgentName.isEmpty()) {
                                        toast(getString(R.string.please_select_sub_agent))
                                    } else {
                                        if (passengerDetailsViewModel.isPhoneBlockTicket) {
                                            if (isNetworkAvailable()) {
//                                                DialogUtils.blockSeatsDialog(
//                                                    showMsg = false,
//                                                    this@PassengerPaymentActivity,
//                                                    getString(R.string.confirmBooking),
//                                                    getString(R.string.selected_seat_s_will_be_assigned),
//                                                    srcDest = srcDest
//                                                        ?: getString(R.string.dash),
//                                                    journeyDate = passengerDetailsViewModel.toolbarSubTitleInfo
//                                                        ?: getString(R.string.dash),
//                                                    noOfSeats = "${passengerDetailsViewModel.noOfSeats}",
//                                                    seatNo = passengerDetailsViewModel.selectedSeatNo,
//                                                    getString(R.string.goBack),
//                                                    getString(R.string.confirmBooking),
//                                                    this@PassengerPaymentActivity
//                                                )
                                                bookTicketWithoutConfirm()
                                            } else {
                                                noNetworkToast()
                                            }
                                        } else {
                                            if (!passengerDetailsViewModel.phoneBlock) {
                                                if (passengerDetailsViewModel.selectedPaymentType.asString(
                                                        resources = resources
                                                    ) == getString(R.string.partial_payment)
                                                ) {
                                                    if (passengerDetailsViewModel.partialAmount > passengerDetailsViewModel.totalFare)
                                                        toast(getString(R.string.partial_amount_validation))
                                                    else if (passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitType != null && passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitType == "2" && passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitValue != null && passengerDetailsViewModel.partialAmount < passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitValue?.toDouble()!!)
                                                        toast("${getString(R.string.less_partial_amount_validation)} ${passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitValue}")
                                                    else if (passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitType != null && passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitType == "1" && passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitValue != null && passengerDetailsViewModel.partialAmount < passengerDetailsViewModel.getPartialPercent())
                                                        toast("${getString(R.string.less_partial_amount_validation)} ${passengerDetailsViewModel.getPartialPercent()}")
                                                    else if (passengerDetailsViewModel.partialAmount == 0.0)
                                                        toast(getString(R.string.enter_partial_amount))
                                                    else if (passengerDetailsViewModel.selectedPartialPayment.asString(
                                                            resources = resources
                                                        ) == getString(R.string.release) && passengerDetailsViewModel.partialBlockingTimeHours.isEmpty()
                                                    )
                                                        toast(getString(R.string.select_hour))
                                                    else if (passengerDetailsViewModel.selectedPartialPayment.asString(
                                                            resources = resources
                                                        ) == getString(R.string.release) && passengerDetailsViewModel.partialBlockingTimeMins.isEmpty()
                                                    )
                                                        toast(getString(R.string.select_minute))
                                                    else {
                                                        passengerDetailsViewModel.isPhoneBlocking =
                                                            false
                                                        if (passengerDetailsViewModel.isPhoneBlockTicket) {
                                                            if (isNetworkAvailable()) {
                                                                confirmPhoneBlockTicketApi()
                                                            } else {
                                                                noNetworkToast()
                                                            }
                                                        } else {
                                                            bookTicketWithoutConfirm()

//                                                            DialogUtils.blockSeatsDialog(
//                                                                showMsg = false,
//                                                                this@PassengerPaymentActivity,
//                                                                getString(R.string.confirmBooking),
//                                                                getString(R.string.selected_seat_s_will_be_assigned),
//                                                                srcDest = srcDest,
//                                                                journeyDate = passengerDetailsViewModel.toolbarSubTitleInfo,
//                                                                noOfSeats = passengerDetailsViewModel.noOfSeats!!,
//                                                                seatNo = passengerDetailsViewModel.selectedSeatNo,
//                                                                getString(R.string.goBack),
//                                                                getString(R.string.confirmBooking),
//                                                                this@PassengerPaymentActivity
//                                                            )
                                                        }
                                                    }

                                                } else {
                                                    bookTicketWithoutConfirm()

//                                                    DialogUtils.blockSeatsDialog(
//                                                        showMsg = false,
//                                                        this@PassengerPaymentActivity,
//                                                        getString(R.string.confirmBooking),
//                                                        getString(R.string.selected_seat_s_will_be_assigned),
//                                                        srcDest = srcDest,
//                                                        journeyDate = passengerDetailsViewModel.toolbarSubTitleInfo,
//                                                        noOfSeats = "${passengerDetailsViewModel.noOfSeats}",
//                                                        seatNo = passengerDetailsViewModel.selectedSeatNo,
//                                                        getString(R.string.goBack),
//                                                        getString(R.string.confirmBooking),
//                                                        this@PassengerPaymentActivity
//                                                    )
                                                }
                                            } else {
                                                passengerDetailsViewModel.isPhoneBlocking = true
                                                if (isNetworkAvailable()) {
                                                    pinAuthDialog(false)
                                                } else {
                                                    noNetworkToast()
                                                }
                                            }

                                        }
                                    }
                                } else {
                                    passengerDetailsViewModel.isFareBreakupBottomSheetVisible =
                                        !passengerDetailsViewModel.isFareBreakupBottomSheetVisible
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    private fun hasFareChanges(originalFares: MutableList<String>, modifiedFares: MutableList<String>): Boolean {
        if (originalFares.isEmpty() && modifiedFares.isEmpty()) return false
        if (originalFares.size != modifiedFares.size) return false

        return originalFares.indices.all { index ->
            val original = originalFares[index].toDoubleOrNull() ?: 0.0
            val modified = modifiedFares[index].toDoubleOrNull() ?: 0.0

            original != modified
        }
    }

    private fun paymentRadioListener(
        passengerDetailsViewModel: PassengerDetailsViewModel<Any?>
    ) {
        when (passengerDetailsViewModel.selectedPaymentType.asString(resources)) {
            getString(R.string.full_payment) -> {
                passengerDetailsViewModel.isPartialPayment = false
                passengerDetailsViewModel.partialPaymentOption = "1"
                hidePartialPaymentInfo(passengerDetailsViewModel)
            }

            getString(R.string.partial_payment) -> {
                passengerDetailsViewModel.isPartialPayment = true
                passengerDetailsViewModel.partialPaymentOption = "2"
                showPartialPaymentInfo(passengerDetailsViewModel)
            }
        }
    }

    private fun openCalender() {
        val minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
        SlyCalendarDialog()
            .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
            .setMinDate(minDate)
            .setMaxDate(getDateDMY(passengerDetailsViewModel.travelDate)?.let {
                stringToDate(
                    it,
                    DATE_FORMAT_D_M_Y
                )
            })
            .setSingle(true)
            .setFirstMonday(false)
            .setCallback(this@PassengerPaymentNewFlowActivity)
            .show(supportFragmentManager, tag)
    }

    private fun onPaymentOptionSelection(paymentOption: String) {
        if (passengerDetailsViewModel.paymentOptionsList.isNotEmpty()) {

            if (paymentOption == getString(R.string.credit_debit)) {
                DialogUtils.creditDebitDialog(this, this)
            } else if (paymentOption == getString(R.string.others)) {
                if (passengerDetailsViewModel.otherPaymentOptions.isNotEmpty())
                    DialogUtils.otherPaymentsDialog(
                        this,
                        passengerDetailsViewModel.otherPaymentOptions,
                        this
                    )
            } else if (paymentOption == getString(R.string.wallet_upi)) {
                var passengerMobile = getString(R.string.empty)
                if (passengerDetailsViewModel.primaryMobileNo.isNotEmpty())
                    passengerMobile =
                        passengerDetailsViewModel.primaryMobileNo.substringAfter("-")

                passengerDetailsViewModel.setWalletOptions()

                walletUpiAlertDialog = DialogUtils.walletUpiDialog(
                    this,
                    passengerDetailsViewModel.walletPaymentOptions,
                    this@PassengerPaymentNewFlowActivity,
                    mobile = passengerMobile
                )
            }
        }
    }

    private fun onBookingTypeSelection(it: String) {
        when (it) {
            getString(R.string.city) -> {
                if (isNetworkAvailable())
                    callCityDetailsApi()
                else
                    noNetworkToast()
            }

            getString(R.string.on_behalf_of) -> {
                if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.selectedBookingTypeId == 33) {
                    callAgentListApi()
                } else {
                    if (passengerDetailsViewModel.selectedCityId != 0) {
                        val intent = Intent(
                            this@PassengerPaymentNewFlowActivity,
                            SearchActivity::class.java
                        )
                        intent.putExtra(
                            getString(R.string.CITY_SELECTION_TYPE),
                            getString(R.string.selectAgent)
                        )
                        startActivityForResult(
                            intent,
                            RESULT_CODE_SEARCH_AGENT
                        )
                    } else
                        toast(getString(R.string.validate_city))
                }
            }

            getString(R.string.selectBranch) -> {
                passengerDetailsViewModel.selectedUserId = 0
                passengerDetailsViewModel.selectedUserName = ""
                callBranchListApi()
            }

            getString(R.string.selectUser) -> {
                if (isNetworkAvailable()) {
                    if (passengerDetailsViewModel.selectedBranchId != 0) {
                        callUserListApi()
                    } else
                        toast(getString(R.string.validate_branch))
                } else
                    noNetworkToast()
            }
        }
    }

    private fun callUserListApi() {
        var userId = passengerDetailsViewModel.selectedBookingTypeId
        val selectedBranchId = passengerDetailsViewModel.selectedBranchId

        if (userId == 3)
            userId = 12

        blockViewModel.userListApi(
            apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
            cityId = "",
            userType = userId.toString(),
            branchId = selectedBranchId.toString(),
            locale = passengerDetailsViewModel.locale,
            apiType = user_list_method_name
        )
    }

    private fun callBranchListApi() {
        blockViewModel.branchListApi(
            passengerDetailsViewModel.loginModelPref?.api_key ?: "",
            passengerDetailsViewModel.locale,
            branch_list_method_name
        )
    }

    private fun callCityDetailsApi() {
        cityDetailViewModel.cityDetailAPI(
            passengerDetailsViewModel.loginModelPref?.api_key ?: "",
            response_format,
            passengerDetailsViewModel.locale,
            city_Details_method_name
        )
    }

    @Composable
    private fun ExistingCouponFragment(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {
        AndroidView(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxHeight()
                .fillMaxWidth(),
            factory = { context ->
                FrameLayout(context).apply {
                    val binding =
                        FragmentCouponBinding.inflate(
                            LayoutInflater.from(context),
                            this,
                            true
                        ).root
                    val layoutCoupons = binding.findViewById<RelativeLayout>(R.id.layoutCoupons)
                    val layoutCouponsHidden =
                        binding.findViewById<LinearLayout>(R.id.layoutCouponsHidden)
                    val imgArrowUpCoupons =
                        binding.findViewById<ImageView>(R.id.imgArrowUpCoupons)
                    val cardCoupons = binding.findViewById<CardView>(R.id.cardCoupons)

                    if (passengerDetailsViewModel.isCouponExpand)
                        layoutCouponsHidden.gone()
                    else
                        layoutCouponsHidden.visible()

                    layoutCoupons.setOnClickListener {
                        if (layoutCouponsHidden.isVisible) {
                            TransitionManager.beginDelayedTransition(
                                cardCoupons,
                                AutoTransition()
                            )
                            layoutCouponsHidden.gone()
                            imgArrowUpCoupons.setImageResource(R.drawable.ic_arrow_down)
                            passengerDetailsViewModel.isCouponExpand = false
                        } else {
                            TransitionManager.beginDelayedTransition(
                                cardCoupons,
                                AutoTransition()
                            )
                            layoutCouponsHidden.visible()
                            imgArrowUpCoupons.setImageResource(R.drawable.ic_arrow_up)
                            passengerDetailsViewModel.isCouponExpand = true
                        }
                    }
                }
            },
            update = {

            }
        )
    }

    @Composable
    fun ContactDetails(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {
        ContactDetailsCard(passengerDetailsViewModel)
    }

    @Composable
    fun BookTicket(onClick: (String) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            CardComponent(
                shape = RoundedCornerShape(2.dp),
                bgColor = colorResource(id = R.color.white),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, colorResource(id = R.color.colorPrimary)))
                    .wrapContentHeight(), onClick = { onClick(FARE_DETAILS) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(8.dp)
                ) {
                    // Fare display with arrow
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .align(CenterVertically)
                            .semantics { role = Role.Button }
                            .testTag(FARE_DETAILS)
                            .semantics { this.contentDescription = "btn $FARE_DETAILS" },
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextBoldLarge(
                            text = if (passengerDetailsViewModel.partialAmount == 0.0 ||
                                !passengerDetailsViewModel.isPartialPayment ||
                                passengerDetailsViewModel.isInsuranceChecked.value
                            ) {
                                "${passengerDetailsViewModel.amountCurrency} ${
                                    passengerDetailsViewModel.totalFareString.toDouble()
                                        .convert(passengerDetailsViewModel.currencyFormat)
                                }"
                            } else {
                                "${passengerDetailsViewModel.amountCurrency} ${
                                    passengerDetailsViewModel.partialAmount.convert(
                                        passengerDetailsViewModel.currencyFormat
                                    )
                                }"
                            },
                            modifier = Modifier.align(CenterVertically),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )

                        IconButton(
                            onClick = { onClick(FARE_DETAILS) },
                            modifier = Modifier.semantics { role = Role.Button }
                        ) {
                            Icon(
                                imageVector = if (passengerDetailsViewModel.isFareBreakupBottomSheetVisible)
                                    ImageVector.vectorResource(id = R.drawable.ic_arrow_up)
                                else
                                    ImageVector.vectorResource(id = R.drawable.ic_arrow_down),
                                contentDescription = stringResource(id = R.string.fare_details),
                                tint = colorResource(id = R.color.colorPrimary)
                            )
                        }
                    }

                    // Book/Phone Button
                    val isButtonEnabled = if (passengerDetailsViewModel.rapidBookingSkip) {
                        true
                    } else if (passengerDetailsViewModel.phoneBlock || passengerDetailsViewModel.isCashEnabled) {
                        passengerDetailsViewModel.isAllMandatoryFieldsFilled.size == passengerDetailsViewModel.passengerDataList.size
                    } else {
                        passengerDetailsViewModel.isAllMandatoryFieldsFilled.size == passengerDetailsViewModel.passengerDataList.size
                                && passengerDetailsViewModel.selectedPaymentOptionId != 1
                                && passengerDetailsViewModel.selectedPaymentOption != ResourceProvider.TextResource.fromStringId(R.string.cash)
                    }

                    val buttonText = if (passengerDetailsViewModel.phoneBlock)
                        stringResource(id = R.string.do_phone_booking)
                    else
                        stringResource(id = R.string.confirm_and_book)

                    val buttonColor = if (isButtonEnabled)
                        colorResource(id = R.color.colorAccent)
                    else
                        colorResource(id = R.color.grey_300)

                    CreateButton(
                        enabled = isButtonEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .padding(start = 8.dp)
                            .align(CenterVertically)
                            .background(buttonColor)
                            .semantics { role = Role.Button },
                        text = buttonText,
                        onClick = {
                            if (passengerDetailsViewModel.country.equals("india", ignoreCase = true) &&
                                passengerDetailsViewModel.editFareMandatoryForAgentUser &&
                                passengerDetailsViewModel.isAllowedEditFare
                            ) {
                                if (hasFareChanges(originalFares, modifiedFareDone)) {
                                    passengerDetailsViewModel.isCancelledClicked = false
                                    onClick(BOOK_TICKET)
                                } else {
                                    showCustomFareUpdateToast(
                                        this@PassengerPaymentNewFlowActivity,
                                        getString(R.string.update_fare_validation)
                                    )
                                }
                            } else {
                                passengerDetailsViewModel.isCancelledClicked = false
                                onClick(BOOK_TICKET)
                            }
                        },
                        style = TextStyle(
                            color = colorResource(id = R.color.colorWhite),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }


    @Composable
    private fun Toolbar() {
        TopAppBar(
            modifier = Modifier
                .statusBarsPadding()
                .height(80.dp),
            title = {
                val text = "${passengerDetailsViewModel.serviceNumber} | ${
                    getDateDMMM(passengerDetailsViewModel.travelDate)
                } ${passengerDetailsViewModel.deptTime} | ${passengerDetailsViewModel.busType} "
                srcDest =
                    "${passengerDetailsViewModel.source}-${passengerDetailsViewModel.destination}"

                Column {
                    TextBoldLarge(
                        text = srcDest,
                        Modifier.offset(x = (-16).dp)
                    )
                    TextNormalSmall(

                        text = text ?: "",
                        Modifier.offset(x = (-16).dp)
                    )
                }

            },
            navigationIcon = {
                IconButton(onClick = {
                    onBackPressed()

                }) { Icon(Icons.Filled.ArrowBack, "") }
            },
            backgroundColor = Color.White
        )
    }


    private fun setPrivilegesObserver() {
//            val privilegeResponse = getPrivilegeBase()

        passengerDetailsViewModel.privilegesLiveData.observe(this) { privilegeResponse ->
            if (privilegeResponse != null) {
                passengerDetailsViewModel.apply {
                    setPrivilegeData(privilegeResponse)
                    setAgentLogin(privilegeResponse.isAgentLogin ?: false)
                    getRole()
                    checkBookingTypeCardVisibility()
                    customBookingTypes(
                        phoneBooking = getString(R.string.phone),
                        walkin = getString(R.string.walkin),
                        confirmBooking = getString(R.string.confirmBooking),
                        onlineAgent = getString(R.string.online_agent),
                        offlineAgent = getString(R.string.offline_agent),
                        branch = getString(R.string.branch),
                        subAgent = getString(R.string.sub_agent_title)
                    )
                    bookingStatus(
                        confirmBooking = getString(R.string.confirm),
                        phoneBooking = getString(R.string.phone)
                    )

                    handlePrivileges(
                        passengerDetailsViewModel = passengerDetailsViewModel,
                        role = passengerDetailsViewModel.roleType,
                        context = this@PassengerPaymentNewFlowActivity
                    )

                    setBoardingDroppingData(
                        role = passengerDetailsViewModel.roleType,
                        agentRoleName = getString(R.string.agent)
                    )

                    if (isAllowUpiForDirectPgBookingForAgents
                        && roleType == getString(R.string.agent)
                    ) {
                        setPaymentOptionsAgents()
                        setSubPaymentOptionsAgents()
                    } else {
                        setPaymentOptions()
                        setOtherPaymentOptionList()
                        setWalletAndUpiOptions()

                        if (isAllowUpiForDirectPgBookingForUsers) {
                            setSubPaymentOptionsUsers()
                        }
                    }

                    setRemarksCardVisibility(
                        hide = getString(R.string.hide),
                        mandatory = getString(R.string.mandatory)
                    )

                    setAdditionalOfferCardVisibility()
                    setInsuranceInfo()

                    setPaymentOptionsVisibility(
                        context = this@PassengerPaymentNewFlowActivity,
                        passengerDetailsViewModel = passengerDetailsViewModel
                    )

                    if (passengerDetailsViewModel.isAgentLogin) {
                        agentAccountInfo()
                    }
                    if (Build.MANUFACTURER.contains("PAX", true)
                        && privilegeResponse?.isEzetapEnabledInTsApp == true
                        && privilegeResponse.ezetapUserName != ""
                        && privilegeResponse.ezetapApiKey != ""
                    ) {
                        initEzetap(privilegeResponse)
                    }
                    if (isNetworkAvailable()) {
                        fareBreakupApi()
                    }
                }
            }
        }
    }

    private fun initEzetap(privilegeResponse: PrivilegeResponseModel) {

        val jsonRequest = JSONObject()
        try {

            jsonRequest.put(
                "prodAppKey",
                privilegeResponse.ezetapApiKey
            )
            jsonRequest.put(
                "demoAppKey",
                privilegeResponse.ezetapApiKey
            )
            jsonRequest.put("merchantName", "Bitlasoft")

            jsonRequest.put(
                "userName",
                privilegeResponse.ezetapUserName
            )
            jsonRequest.put("currencyCode", "INR")
            jsonRequest.put("appMode", "DEMO")
            jsonRequest.put("captureSignature", "false");
            jsonRequest.put("prepareDevice", "false");


//            EzeAPI.initialize(this, REQUEST_CODE_INITIALIZE_EZETAP, jsonRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }



    private fun setMealInfoObserver() {
        singleViewModel.mealInfoLiveData.observe(this) {
            passengerDetailsViewModel.setMealInfoData(it)
        }
    }

    private fun callAgentListApi() {
        if (isNetworkAvailable()) {
            passengerDetailsViewModel.apply {
                val id = if (isAgentLogin && selectedBookingTypeId == 33) 3 else selectedBookingTypeId

                blockViewModel.userListApi(
                    apiKey = loginModelPref?.api_key ?: "",
                    cityId = selectedCityId.toString(),
                    userType = id.toString(),
                    branchId = selectedBranchId.toString(),
                    locale = locale,
                    apiType = user_list_method_name
                )
            }

        } else
            noNetworkToast()
    }

    private fun callPassengerHistoryApi() {
        if (isNetworkAvailable()) {
            passengerHistoryViewModel.passengerHistoryApi(
                apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                response_format = json_format,
                passenger_details = passengerDetailsViewModel.primaryMobileNo,
                operator_api_key = operator_api_key,
                locale = passengerDetailsViewModel.locale,
                apiType = ticket_details_method_name
            )
        } else {
            noNetworkToast()
        }
    }

    private fun setPaxHistoryObserver() {
        passengerHistoryViewModel.dataPassengersHistory.observe(this) {

            if (it?.code != null && it.code == 200) {
                passengerDetailsViewModel.setPaxHistory(it)

            } else if (it.code != null && it.code == 401) {

            } else {
                it?.message?.let { it1 -> toast(it1) }
            }
        }
    }

    private fun getSmartMilesOtpApi() {
        val mobileNumber = passengerDetailsViewModel.applySmartMilesMobileNo
        val reqBody = com.bitla.ts.domain.pojo.smart_miles_otp.request.ReqBody(
            passengerDetailsViewModel.loginModelPref?.api_key ?: "",
            mobileNumber,
            locale = passengerDetailsViewModel.locale
        )

        SmartMilesOtpRequest(
            passengerDetailsViewModel.bccId.toString(),
            format_type,
            smart_miles_otp_method_name, reqBody
        )

        validateCouponViewModel.smartMilesOtpApi(
            smartMilesOtpRequest = reqBody,
            apiType = smart_miles_otp_method_name
        )
    }

    private fun validateCouponCode(discountParams: DiscountParams) {
        val reqBody = com.bitla.ts.domain.pojo.coupon.request.ReqBody()
        reqBody.apiKey = passengerDetailsViewModel.loginModelPref?.api_key
        reqBody.discountParams = discountParams
        reqBody.locale = passengerDetailsViewModel.locale

        val couponRequest = CouponRequest()
        couponRequest.bccId = passengerDetailsViewModel.bccId.toString()
        couponRequest.format = format_type
        couponRequest.methodName = validate_coupon_method_name
        couponRequest.reqBody = reqBody

        validateCouponViewModel.validateCouponApi(
            couponRequest = reqBody,
            apiType = validate_coupon_method_name
        )
    }

    private fun setCouponResponseObserver() {
        validateCouponViewModel.loadingState.observe(this) {
            when (it) {
                LoadingState.LOADING -> {}
                LoadingState.LOADED -> {}
                else -> {}
            }
        }

        validateCouponViewModel.couponDetails.observe(this) {
            if (it != null) {
                passengerDetailsViewModel.checkUncheckCoupons(code = it.code)
                when (it.code) {
                    "200" -> {
                        passengerDetailsViewModel.setCouponDetails(it)
                        toast(it.message)

                    }

                    "401" -> {
                    }

                    else -> {
                        toast(it.message)
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        validateCouponViewModel.smartMilesOtp.observe(this) {
            it
            if (it != null) {
                if (it.code == 200) {
                    passengerDetailsViewModel.setSmartMilesOtp(it.otp, it.otp_key)
                } else {
                    if (it?.message != null) {
                        it?.message?.let { it1 ->
                            toast(it1)
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_INITIALIZE_EZETAP -> {
//                    EzeAPI.getDeviceInfo(this, REQUEST_EZETAP_DEVICE_INFO, JSONObject())
                }

                REQUEST_EZETAP_DEVICE_INFO -> {
                    val deviceData = JSONObject(data?.extras!!["response"].toString())
                    if (deviceData.has("result")) {
                        if (deviceData.getJSONObject("result").has("deviceSerialNo")) {
                            ezetapDeviceId =
                                deviceData.getJSONObject("result").getString("deviceSerialNo")
                        } else {
                            toast(getString(R.string.couldn_t_get_device_information))
                        }
                    }
                }

                REQUEST_CODE_PAY_EZETAP -> {
                    ezetapPaySuccessHandling(data)
                }
                REQUEST_PAYMENT_INTENT_FLOW->{
                    if( data?.extras!!["response"]==null){
                        callReleaseTicketApi()
                    }else {
                        val params = data?.extras!!["response"].toString().split("&").associate {
                            val (key, value) = it.split("=")
                            key to value
                        }
                        val status = params["Status"]
                        if(status=="SUCCESS" || status=="Success" || status!!.contains("Success",true)){
                            upiAppsPaymentDialog = DialogUtils.upiAppsPaymentConfirmationDialog(
                                context = this,
                                dialogSingleButtonListener = this,
                            )
                            if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
                                callPayStatOfAgentInsRechargStatusApi()
                            } else if (passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers) {
                                callBranchUpiTranxStatusApi()
                            }
                        }else{
                            callReleaseTicketApi()
                        }
                    }

                }

                else -> {
                    if (data?.getStringExtra(getString(R.string.SELECTED_CITY_TYPE)) != null) {
                        data.getStringExtra(getString(R.string.SELECTED_CITY_TYPE))!!

                        val selectedCityType: String =
                            data.getStringExtra(getString(R.string.SELECTED_CITY_TYPE)).toString()
                        val selectedCityName: String =
                            data.getStringExtra(getString(R.string.SELECTED_CITY_NAME)).toString()
                        val selectedCityId: String =
                            data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                        val selectedDiscountValue: String =
                            data.getStringExtra(getString(R.string.SELECTED_DISCOUNT_VALUE))
                                .toString()

                        when (selectedCityType) {
                            getString(R.string.select_city) -> {
                                passengerDetailsViewModel.selectedCityName = selectedCityName
                                passengerDetailsViewModel.selectedCityId =
                                    selectedCityId.toDouble().toInt()

                                passengerDetailsViewModel.onBehalfOfAgentName = ""

                                if (passengerDetailsViewModel.selectedCityId != 0)
                                    callAgentListApi()
                                else
                                    toast(getString(R.string.validate_city))
                            }

                            getString(R.string.selectAgent) -> {
                                if (passengerDetailsViewModel.selectedBookingTypeId == 1
                                    || passengerDetailsViewModel.selectedBookingTypeId == 2
                                    || passengerDetailsViewModel.selectedBookingTypeId == 33)
                                {
                                    passengerDetailsViewModel.onBehalfOfAgentId =
                                        selectedCityId.toDouble().toInt()
                                    passengerDetailsViewModel.onBehalfOfAgentName = selectedCityName
                                }
                            }

                            getString(R.string.selectBranch) -> {
                                val selectedBranchId = selectedCityId.toDouble().toInt()

                                passengerDetailsViewModel.selectedBranchId = selectedBranchId
                                passengerDetailsViewModel.selectedBranchName = selectedCityName

                                if (passengerDetailsViewModel.branchRoleDiscountType != getString(R.string.none)) {
                                    PreferenceUtils.setPreference(
                                        PREF_BRANCH_DISCOUNT_VALUE,
                                        selectedDiscountValue
                                    )
                                }
                                if (passengerDetailsViewModel.privilegeResponseModel?.allowToApplyCurrentUserRoleBranchDiscount==true
                                    && passengerDetailsViewModel.privilegeResponseModel?.applyRoleOrBranchDiscountAtTimeOfBooking==false)
                                {
                                    passengerDetailsViewModel.branchDiscountValue = passengerDetailsViewModel.discountValue
                                }
                                else {
                                    passengerDetailsViewModel.branchDiscountValue = selectedDiscountValue
                                }

                                if (!passengerDetailsViewModel.isSeatWiseDiscountEdit) {
                                    passengerDetailsViewModel.isDiscountAmountChanged = true
                                }

//                                toast("branch- ${passengerDetailsViewModel.branchDiscountValue}")
                            }

                            getString(R.string.selectUser) -> {
                                val selectedUserId = selectedCityId.toDouble().toInt()
                                passengerDetailsViewModel.selectedUserId = selectedUserId
                                passengerDetailsViewModel.selectedUserName = selectedCityName

                                if (passengerDetailsViewModel.branchRoleDiscountType != getString(R.string.none)) {
                                    PreferenceUtils.setPreference(
                                        PREF_ROLE_DISCOUNT_VALUE,
                                        selectedDiscountValue
                                    )
                                }
//                                passengerDetailsViewModel.roleDiscountValue = selectedDiscountValue

                                if (passengerDetailsViewModel.privilegeResponseModel?.allowToApplyCurrentUserRoleBranchDiscount==true
                                    && passengerDetailsViewModel.privilegeResponseModel?.applyRoleOrBranchDiscountAtTimeOfBooking==false)
                                {
                                    passengerDetailsViewModel.roleDiscountValue = passengerDetailsViewModel.discountValue
                                }
                                else {
                                    passengerDetailsViewModel.roleDiscountValue = selectedDiscountValue
                                }
                                if (!passengerDetailsViewModel.isSeatWiseDiscountEdit) {
                                    passengerDetailsViewModel.isDiscountAmountChanged = true
                                }
                            }
                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }

        } else {

            when (requestCode) {
                REQUEST_CODE_PAY_EZETAP -> {
                    ezetapPayFailureHandling()
                }
                REQUEST_PAYMENT_INTENT_FLOW->{
                    toast(getString(R.string.payment_failed_please_try_again))
                    callReleaseTicketApi()
                }
            }
        }

    }


    private fun ezetapPayFailureHandling() {
        val obj = JSONObject()
        obj.put("success", true)
        obj.put("messageCode", null)
        obj.put("message", null)
        obj.put("errorCode", null)
        obj.put("errorMessage", null)
        obj.put("realCode", null)
        obj.put("apiMessageTitle", null)
        obj.put("apiMessage", null)
        obj.put("apiMessageText", null)
        obj.put("apiWarning", null)
        obj.put("p2pRequestId", "")

        val jsonParser = JsonParser()
        val statusData = jsonParser.parse(obj.toString()) as JsonObject

        handleEzetapResponse(statusData)
    }


    private fun ezetapPaySuccessHandling(data: Intent?) {
        val data = JSONObject(data?.extras!!["response"].toString())
        Log.d("EzetapRespose", data.toString())
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
                passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                ezetapData.ticketNumber,
                true,
                ezetap_response = ezetapResponse,
                ezetap_payment_type = 14,
                reservation_id = ""

            )

        ezetapStatusApi(reqBody)

    }

    private fun ezetapStatusApi(reqBody: ReqBodyEzetapStatus) {
        passengerDetailsViewModel.showShimmer = true
        bookingOptionViewModel.ezetapStatusApi(reqBody, ezetap_status_api)
    }


    private fun setEzetapStatusObserver() {
        bookingOptionViewModel.ezetapTransactionData.observe(this) {

            when (it.code) {
                200 -> {
                    if (passengerDetailsViewModel.showShimmer) {
                        passengerDetailsViewModel.showShimmer = false
                    }

                    val intent = if (passengerDetailsViewModel.country.equals(
                            "India",
                            true
                        ) || passengerDetailsViewModel.country.equals("Indonesia", true)) {
                        Intent(this, TicketDetailsActivityCompose::class.java)
                    } else {
                        Intent(this, TicketDetailsActivity::class.java)
                    }

                    intent.putExtra(
                        "activityName",
                        BookingPaymentOptionsActivity::class.java
                    )
                    intent.putExtra("activityName2", "booking")

                    intent.putExtra(
                        getString(R.string.TICKET_NUMBER),
                        ezetapData.ticketNumber
                    )
                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                    finish()
//                    } else if (it.message == "This ticket is released and now its ready for re-booking.") {
//                        if (passengerDetailsViewModel.showShimmer) {
//                            passengerDetailsViewModel.showShimmer = false
//                        }
//                        toast(getString(R.string.payment_failed_please_try_again))
//                    } else {
//                        toast(it.data!!.message)
//                    }
                }

                211 -> {
                    if (passengerDetailsViewModel.showShimmer) {
                        passengerDetailsViewModel.showShimmer = false
                    }
                    toast(getString(R.string.payment_failed_please_try_again))
                }

                401 -> {
                    DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )
                }

                402 -> {
                    toast(it.message)
                }

                else -> {
                    toast(it.message)
                }

            }
        }
    }





    @SuppressLint("SetTextI18n")
    private fun setTicketDetailsObserver() {
        ticketDetailsViewModel.dataTicketDetails.observe(this) {

            if (it != null) {
                if (it.code == 200) {
                    if(it.body?.ticketStatus=="Booked"){
                        val intent = if (passengerDetailsViewModel.country.equals(
                                "India",
                                true
                            ) || passengerDetailsViewModel.country.equals("Indonesia", true)
                        ) {
                            Intent(this, TicketDetailsActivityCompose::class.java)
                        } else {
                            Intent(this, TicketDetailsActivity::class.java)
                        }

                        intent.putExtra(
                            getString(R.string.TICKET_NUMBER),
                            passengerDetailsViewModel.bookTicketPnr
                        )
                        intent.putExtra(
                            "activityName",
                            BookingPaymentOptionsActivity::class.java
                        )
                        intent.putExtra("activityName2", "booking")
                        intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                        startActivity(intent)
                        finish()

                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

                } else {
                    if (it.message != null) {
                        it.message.let { it1 -> toast(it1) }
                    } else if (it.message != null) {
                        it.message.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.opps))
            }
        }}}


    override fun onButtonClick(vararg args: Any) {
        if (args.isNotEmpty()) {
            when (args[0]) {
                getString(R.string.goBack) -> {
                    passengerDetailsViewModel.selectedPaymentOption =
                        ResourceProvider.TextResource.fromStringId(R.string.cash)
                    passengerDetailsViewModel.selectedPaymentOptionId = 1
                }

                getString(R.string.credit_debit) -> {
                    passengerDetailsViewModel.creditDebitCardNo = args[1].toString()
                    DialogUtils.blockSeatsDialog(
                        showMsg = false,
                        this,
                        getString(R.string.confirmBooking),
                        getString(R.string.selected_seat_s_will_be_assigned),
                        srcDest = srcDest ?: getString(R.string.dash),
                        journeyDate = passengerDetailsViewModel.toolbarSubTitleInfo,
                        noOfSeats = passengerDetailsViewModel.noOfSeats ?: "0",
                        seatNo = passengerDetailsViewModel.selectedSeatNo,
                        getString(R.string.goBack),
                        getString(R.string.confirmBooking),
                        this@PassengerPaymentNewFlowActivity
                    )
                }

                getString(R.string.phone_blocking_use_btn) -> {
                    passengerDetailsViewModel.phoneBookingCardColor = PHONE_BOOKING_SELECTED
                    passengerDetailsViewModel.selectedDate = args[1].toString()
                    blockingDate = args[1].toString()
                    blockingTimeHours = args[2].toString()
                    blockingTimeMins = args[3].toString()
                    blockingAmPm = args[4].toString()
                    isPermanentPhoneBookingChecked = args[5].toString().toBoolean()

                    if (isPermanentPhoneBookingChecked) {
                        blockingTimeHours = ""
                        blockingTimeMins = ""
                        blockingAmPm = ""
                    }

                    if (blockingTimeHours.isEmpty()) {
                        passengerDetailsViewModel.setPhoneBlockTime(
                            ResourceProvider.TextResource.fromText(
                                blockingDate
                            )
                        )
                    } else {
                        passengerDetailsViewModel.setPhoneBlockTime(
                            ResourceProvider.TextResource.fromText(
                                "$blockingTimeHours:$blockingTimeMins $blockingAmPm | $blockingDate "
                            )
                        )
                    }

//                    blockingAmPm =
//                        if (blockingTimeHours.isNotEmpty() && blockingTimeHours.toDouble()
//                                .toInt() >= 12
//                        )
//                            getString(R.string.pm)
//                        else
//                            getString(R.string.am)

                    PreferenceUtils.putString(
                        "phoneBlock",
                        "${blockingDate}#${blockingTimeHours}#${blockingTimeMins}#${blockingAmPm}#${isPermanentPhoneBookingChecked}"
                    )

                    passengerDetailsViewModel.phoneBlock = true
                    setPaymentOptionsVisibility(
                        this@PassengerPaymentNewFlowActivity,
                        passengerDetailsViewModel
                    )
                    passengerDetailsViewModel.setPhoneDialogViewVisible(false)
                    if (passengerDetailsViewModel.privilegeResponseModel?.allowToSwitchSinglePageBooking != null
                        && passengerDetailsViewModel.privilegeResponseModel?.allowToSwitchSinglePageBooking!!
                    ) {
                        passengerDetailsViewModel.setPhoneBlockDateTimeVisible(true)
                    }
                }

                getString(R.string.phone_blocking_cancel_btn) -> {
                    passengerDetailsViewModel.setPhoneBlockTime(
                        ResourceProvider.TextResource.fromStringId(
                            R.string.not_specified
                        )
                    )
                    passengerDetailsViewModel.phoneBlock = false
                    passengerDetailsViewModel.phoneBookingCardColor = PHONE_BOOKING_NOT_SELECTED
                    setPaymentOptionsVisibility(
                        this@PassengerPaymentNewFlowActivity,
                        passengerDetailsViewModel
                    )
                    passengerDetailsViewModel.setPhoneDialogViewVisible(false)
                    if (passengerDetailsViewModel.privilegeResponseModel?.allowToSwitchSinglePageBooking != null
                        && passengerDetailsViewModel.privilegeResponseModel?.allowToSwitchSinglePageBooking!!
                    ) {
                        passengerDetailsViewModel.setPhoneBlockDateTimeVisible(true)
                    }
                }
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str.isNotEmpty()) {
            if (str == getString(R.string.goBack)) {

                passengerDetailsViewModel.selectedPaymentOption =
                    ResourceProvider.TextResource.fromStringId(R.string.cash)
                passengerDetailsViewModel.selectedPaymentOptionId = 1
                passengerDetailsViewModel.isCancelledClicked = true
            } else if (str == getString(R.string.timeout) || str == "go_back") {
                handler.removeCallbacks(runnable!!)
                passengerDetailsViewModel.isHandlerRunning = false

            } else if (str == getString(R.string.wallet_go_back)) {
                passengerDetailsViewModel.isCancelledClicked = true
                passengerDetailsViewModel.selectedPaymentOption =
                    ResourceProvider.TextResource.fromStringId(R.string.cash)
                passengerDetailsViewModel.selectedPaymentOptionId = 1

                passengerDetailsViewModel.selectedWalletUpiOptionId = null
                passengerDetailsViewModel.selectedWalletOrUpi = null
                passengerDetailsViewModel.selectedWalletUpiOptionName = null


            } else if (str.contains(getString(R.string.other_payments_confirm))) {
                val otherPaymentOptionPosition = str.substringAfter("-")
                passengerDetailsViewModel.selectedOtherPaymentOption =
                    passengerDetailsViewModel.otherPaymentOptions[otherPaymentOptionPosition.toDouble()
                        .toInt()].payGayTypeName
            }
            else if (str.contains("Confirm Release")) {
                getTicketDetailsApi()
                DialogUtils.confirmationDialog(
                    context = this,
                    dialogSingleButtonListener = this
                )


            }
            else if (str.contains("Check Status")) {
                  getTicketDetailsApi()

            } else if (str.contains(WalletOptionAdapter.TAG)) {
                val walletUpiPosition = str.substringAfter("-")

                passengerDetailsViewModel.selectedWalletUpiOptionName =
                    passengerDetailsViewModel.walletPaymentOptions[walletUpiPosition.toDouble()
                        .toInt()].name

                passengerDetailsViewModel.selectedWalletUpiOptionId =
                    passengerDetailsViewModel.walletPaymentOptions[walletUpiPosition.toDouble()
                        .toInt()].paygayType

                passengerDetailsViewModel.selectedWalletOrUpi =
                    passengerDetailsViewModel.walletPaymentOptions[walletUpiPosition.toDouble()
                        .toInt()].type

                when (passengerDetailsViewModel.selectedWalletUpiOptionId) {
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

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val walletConfirmButton =
                        walletUpiAlertDialog.findViewById<android.widget.Button>(
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
                    passengerDetailsViewModel.walletMobileNo = strList[1]
                }

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val btnText = walletUpiAlertDialog.findViewById<android.widget.Button>(
                        R.id.btnConfirm
                    ).text.toString()
                    if (btnText == getString(R.string.confirm)) {
                        when {
                            passengerDetailsViewModel.selectedWalletUpiOptionId == null -> toast(
                                getString(R.string.validate_wallet_upi)
                            )

                            passengerDetailsViewModel.walletMobileNo.isEmpty() -> toast(getString(R.string.validate_mobile_number))
                            else -> {
                                if (passengerDetailsViewModel.privilegeResponseModel?.phoneNumValidationCount != null && passengerDetailsViewModel.privilegeResponseModel?.phoneNumValidationCount!! <= passengerDetailsViewModel.walletMobileNo.toCharArray().size) {

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
                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog.isShowing && !isFinishing) {
                    walletUpiAlertDialog.dismiss()
                }
                if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog.isShowing && !isFinishing) {
                    upiCreateQRAlertDialog.dismiss()
                }
                
                if (::upiAuthSmsAndVPADialog.isInitialized && upiAuthSmsAndVPADialog.isShowing && !isFinishing) {
                    upiAuthSmsAndVPADialog.dismiss()
                }
            } else if (str == getString(R.string.cancel)) {
                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog.isShowing && !isFinishing) {
                    walletUpiAlertDialog.dismiss()
                }
                if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog.isShowing && !isFinishing) {
                    upiCreateQRAlertDialog.dismiss()
                }
                
                if (::upiAuthSmsAndVPADialog.isInitialized && upiAuthSmsAndVPADialog.isShowing && !isFinishing) {
                    upiAuthSmsAndVPADialog.dismiss()
                }

                if (::upiAppsPaymentDialog.isInitialized && upiAppsPaymentDialog.isShowing && !isFinishing) {
                    upiAppsPaymentDialog.dismiss()
                }
                passengerDetailsViewModel.isCancelledClicked = true
                passengerDetailsViewModel.showPhonePeV2PendingDialog = false
                closePhonePeV2PendingDialog()

                if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
                    callReleaseTicketApi()
                } else if (passengerDetailsViewModel.isAllowUpiForDirectPgBookingForUsers) {
                    callReleaseBranchUpiBlockedSeatsApi()
                }
                
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

    override fun onLeftButtonClick() {

    }

    override fun onRightButtonClick() {
        passengerDetailsViewModel.isPhoneBlocking = false
        if (passengerDetailsViewModel.isPhoneBlockTicket) {
            if (isNetworkAvailable()) {
                confirmPhoneBlockTicketApi()
            } else {
                noNetworkToast()
            }
        } else {
            if (passengerDetailsViewModel.selectedSeatDetails.isNotEmpty() && passengerDetailsViewModel.selectedExtraSeatDetails.isNotEmpty()) {
                if (isNetworkAvailable()) {
                    bookSeatsWithExtraSeatApi()
                } else
                    noNetworkToast()
            } else if (passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }) {
                passengerDetailsViewModel.isExtraSeat = true

                if (isNetworkAvailable()) {
                    if (passengerDetailsViewModel.shouldExtraSeatBooking && passengerDetailsViewModel.country.equals("india", true)) {
                        DialogUtils.showFullHeightPinInputBottomSheet(
                            activity = this@PassengerPaymentNewFlowActivity,
                            fragmentManager = supportFragmentManager,
                            pinSize = passengerDetailsViewModel.pinSize,
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
                if (isNetworkAvailable()) {
                    pinAuthDialog(false)
                } else
                    noNetworkToast()
            }
        }
    }

    private fun bookTicketWithoutConfirm() {
        if (passengerDetailsViewModel.selectedSeatDetails.isNotEmpty()
            && passengerDetailsViewModel.selectedExtraSeatDetails.isNotEmpty()
        ) {
            if (isNetworkAvailable()) {
                bookSeatsWithExtraSeatApi()
            } else
                noNetworkToast()
        } else if (passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }) {
            passengerDetailsViewModel.isExtraSeat =
                true

            if (isNetworkAvailable()) {
                if (passengerDetailsViewModel.shouldExtraSeatBooking &&
                    passengerDetailsViewModel.country.equals("india", true))
                {
                    DialogUtils.showFullHeightPinInputBottomSheet(
                        activity = this@PassengerPaymentNewFlowActivity,
                        fragmentManager = supportFragmentManager,
                        pinSize = passengerDetailsViewModel.pinSize,
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
            if (isNetworkAvailable()) {
                pinAuthDialog(false)
            } else
                noNetworkToast()
        }
    }

    private fun confirmPhoneBlockTicketApi() {
        if (isNetworkAvailable()) {
            val reqBody = com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody(
                passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                passengerDetailsViewModel.selectedPaymentOptionId,
                passengerDetailsViewModel.pnrNumber,
                passengerDetailsViewModel.ticket.value,
                "",
                passengerDetailsViewModel.userId,
                locale = passengerDetailsViewModel.locale
            )

            bookingOptionViewModel.confirmPhoneBlockTicketApi(
                reqBody,
                confirm_phone_block_ticket_method_name
            )

        } else
            noNetworkToast()
    }

    private fun bookSeatsWithExtraSeatApi() {
        val seatList =
            mutableListOf<com.bitla.ts.domain.pojo.book_with_extra_seat.request.SeatDetail>()
        val extraSeatList =
            mutableListOf<com.bitla.ts.domain.pojo.book_with_extra_seat.request.SeatDetailExtra>()

        passengerDetailsViewModel.passengerDataList.forEach {
            if (!it.isExtraSeat) {
                val seatDetail = com.bitla.ts.domain.pojo.book_with_extra_seat.request.SeatDetail(
                    isPrimary = true,
                    seatNumber = it.seatNumber ?: getString(R.string.empty),
                    sex = it.sex ?: getString(R.string.empty),
                    name = it.name ?: getString(R.string.empty),
                    age = it.age ?: getString(R.string.empty),
                    firstName = it.name ?: getString(R.string.empty),
                    lastName = it.name ?: getString(R.string.empty),
                    idCardType = it.idCardTypeId?.toInt()?:0,
                    idCardNumber = it.idCardNumber.toString(),
                    nationality = it.nationality.toString(),
                    additionalFare = "",
                    discountAmount = "",
                    isRoundTripSeat = false,
                    passengerCategory = ""

                )

                seatDetail.fare = it.fare.toString()

                seatList.add(seatDetail)
            }

            if (it.isExtraSeat) {
                val seatDetailExtra =
                    com.bitla.ts.domain.pojo.book_with_extra_seat.request.SeatDetailExtra(
                        isPrimary = "false",
                        seatNumber = it.seatNumber ?: getString(R.string.empty),
                        sex = it.sex ?: getString(R.string.empty),
                        name = it.name ?: getString(R.string.empty),
                        age = it.age ?: getString(R.string.empty),
                        mobileNumber = passengerDetailsViewModel.primaryMobileNo,
                        alternateNumber = passengerDetailsViewModel.alternateMobileNo,
                        idCardType = it.idCardTypeId?.toInt()?:0,
                        idCardNumber = it.idCardNumber.toString(),
                        nationality = it.nationality.toString(),
                    )


                seatDetailExtra.fare = it.fare.toString()
                extraSeatList.add(seatDetailExtra)
            }
        }

        val bookingDetails = com.bitla.ts.domain.pojo.book_with_extra_seat.request.BookingDetails(
            agentType = "${passengerDetailsViewModel.selectedBookingTypeId}",
            remarks = "${passengerDetailsViewModel.remarks}"
        )

        bookingDetails.agentType = "${passengerDetailsViewModel.selectedBookingTypeId}"
        bookingDetails.agentPaymentType = if (passengerDetailsViewModel.selectedPaymentOptionId==1) {
            ""
        } else {
            "${passengerDetailsViewModel.selectedPaymentOptionId}"
        }
        
        if (passengerDetailsViewModel.isAgentSubPaymentSelected) {
            bookingDetails.agentSubPaymentType = passengerDetailsViewModel.selectedSubPaymentOptionId
            bookingDetails.agentPhone = passengerDetailsViewModel.agentPayViaPhoneNumberSMS
            bookingDetails.agentVpa = passengerDetailsViewModel.agentPayViaVPA
        }
        


        val contactDetail = com.bitla.ts.domain.pojo.book_with_extra_seat.request.ContactDetail(
            email = passengerDetailsViewModel.emailId,
            emergencyName = passengerDetailsViewModel.emergencyName,
            alternateNumber = passengerDetailsViewModel.alternateMobileNo,
            mobileNumber = passengerDetailsViewModel.primaryMobileNo,
            sendSmsOnBooking = true
        )

        val extraSeatDetail = ExtraSeats(
            passengerDetailsViewModel.exNoOfSeats.toInt(),
            extraSeatList
        )

        val bookExtraSeatRequest = BookTicketWithExtraSeatRequest(
            apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
            reservationId = passengerDetailsViewModel.resId.toString(),
            boardingAt = passengerDetailsViewModel.boardingId.toString(),
            destinationId = passengerDetailsViewModel.destinationId,
            dropOff = passengerDetailsViewModel.droppingId.toString(),
            noOfSeats = passengerDetailsViewModel.noOfSeats.toString(),
            originId = passengerDetailsViewModel.sourceId,
            locale = passengerDetailsViewModel.locale,
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


        bookingOptionViewModel.bookSeatWithExtraSeatApi(
            bookExtraSeatRequest = bookExtraSeatRequest,
            apiType = book_ticket_method_name
        )
    }

    private fun fareBreakupApi() {
        val seatNumberArray = mutableListOf<String>()
        val reqBody = com.bitla.ts.domain.pojo.fare_breakup.request.ReqBody()
        val returnSeatNumberArray = mutableListOf<String>()
        val couponDetails = mutableListOf<String>()

        passengerDetailsViewModel.apply {

            if (!country.equals("india", true)) {
                showShimmer = true
            } else {
                if (rapidBookingSkip) {
                    showShimmer = true
                }
            }

            passengerDataList.forEach {
                seatNumberArray.add(it.seatNumber.toString())
            }

            // GST
            /*val gstIndex = appliedCouponList.indexOfFirst {
                    it.couponTypeResource?.asString(resources = resources) == getString(R.string.gst_details)
                }

            if (gstIndex != -1) {
                val gstNumber = appliedCouponList[gstIndex].coupon_code.substringBefore("-")
                val gstCompanyName = appliedCouponList[gstIndex].coupon_code.substringAfter("-")

                val passengerGstDetails = PassengerGstDetails()
                passengerGstDetails.gstId = gstNumber
                passengerGstDetails.registrationName = gstCompanyName
                reqBody.passengerGstDetails = passengerGstDetails
            }*/

            val couponCodeIndex = appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(resources = resources) == getString(R.string.coupon_code)
            }

            if (appliedCouponList.size > 0) {
                if (couponCodeIndex != -1) {
                    val couponCode = appliedCouponList[couponCodeIndex].coupon_code
                    reqBody.couponCode = couponCode
                }
            }

            //promotion coupon
            val promotionCouponCodeIndex = appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(resources = resources) == getString(R.string.promotion_coupon)
            }

            if (appliedCouponList.size > 0) {
                if (promotionCouponCodeIndex != -1) {
                    val couponCode = appliedCouponList[promotionCouponCodeIndex].coupon_code
                    reqBody.promotionCoupon = couponCode
                }
            }

            if (!PreferenceUtils.getString("AutoDiscountCouponCode").isNullOrEmpty()) {
                reqBody.auto_discount_coupon = PreferenceUtils.getString("AutoDiscountCouponCode")
            }

            // Free Ticket
            val isFreeTicket = appliedCouponList.any {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.free_ticket)
            }
            if (isFreeTicket)
                reqBody.isFreeBookingAllowed = passengerDetailsViewModel.isFreeBookingAllowed

            // Vip Booking
            val isVipTicket = appliedCouponList.any {
                it.couponTypeResource?.asString(resources = resources) == getString(R.string.vip_ticket)
            }
            if (isVipTicket) {
                reqBody.isVipTicket = passengerDetailsViewModel.vipTicket
            }

            // Smart miles
            val smartMilesIndex =
                appliedCouponList.indexOfFirst {
                    it.couponTypeResource?.asString(
                        resources = resources
                    ) == getString(R.string.apply_smart_miles)
                }
            if (smartMilesIndex != -1) {
                useSmartMiles = "true"
                val smartMilesNumber = appliedCouponList[smartMilesIndex].coupon_code
                val smartMilesHash = SmartMilesHash()
                smartMilesHash.phoneNumber = smartMilesNumber
                reqBody.smartMilesHash = smartMilesHash
            }

            // Privilege Card
            val privilegeCardIndex = appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.privilege_card)
            }

            if (privilegeCardIndex != -1) {
                val cardOrMobileNumber =
                    appliedCouponList[privilegeCardIndex].coupon_code

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
                privilegeCardHash.reservationId = passengerDetailsViewModel.resId.toString()
                privilegeCardHash.selectedSeats = passengerDetailsViewModel.noOfSeats

                reqBody.privilegeCardHash = privilegeCardHash

            }

            // Pre-postpone
            val prePostponeIndex = appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.pre_postpone_ticket)
            }
            if (prePostponeIndex != -1) {
                val prePostponeNumber = appliedCouponList[prePostponeIndex].coupon_code
                reqBody.prePostPonePnr = prePostponeNumber
                reqBody.isMatchPrepostponeAmount =
                    passengerDetailsViewModel.isMatchPrepostponeAmount
                reqBody.allowPrePostPoneOtherBranch =
                    passengerDetailsViewModel.allowPrePostPoneOtherBranch
                reqBody.corpCompanyId = passengerDetailsViewModel.corpCompanyId
            }

            // Previous Pnr
            val previousPnrIndex = appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(resources = resources) == getString(R.string.quote_previous_pnr)
            }

            if (previousPnrIndex != -1) {
                val previousPnrNumber =
                    appliedCouponList[previousPnrIndex].coupon_code.substringBefore("-")
                val previousPnrDetails = PreviousPnrDetails()
                previousPnrDetails.previous_pnr = previousPnrNumber
                previousPnrDetails.phone_number =
                    appliedCouponList[previousPnrIndex].coupon_code.substringAfter("-")
                reqBody.previousPnrDetails = previousPnrDetails
            }

            // Discount On Total Amount
            val discountOnTotalIndex = appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(resources = resources) == getString(R.string.discount_amount)
            }

            if (discountOnTotalIndex != -1) {
                val amount = appliedCouponList[discountOnTotalIndex].coupon_code
                reqBody.totalDiscountAmount = amount
            }

            // Discount On individual seat
            if (individualDiscountAmount > 0) {
                reqBody.discountAmount =
                    passengerDetailsViewModel.individualDiscountAmount.toString()
            }

//        ----------individual discount-------------
            if (isDiscountPrivilege.value == false && !isAgentLogin) {
                if (!PreferenceUtils.getSelectedCoupon().isNullOrEmpty()) {
                    reqBody.seatWiseFare = PreferenceUtils.getSelectedCoupon()
                } else {

                    val seatWiseFares = mutableListOf<SeatWiseFare>()
                    var totalDiscountAmountSeatWise = 0.0


                    if (isAllowToApplyDiscountOnBookingPageWithPercentage) {

                        passengerDataList.forEach {
                            val seatWiseFare = SeatWiseFare()

                            if (it.discountAmount.toString().isNotEmpty()) {
                                val calculateDiscountValue = it.fare.toString()
                                    .toDouble() / 100.0f * it.discountAmount.toString().toDouble()
                                seatWiseFare.seatNo = it.seatNumber ?: getString(R.string.empty)
                                seatWiseFare.discount = "$calculateDiscountValue"
                                seatWiseFares.add(seatWiseFare)
                            } else {
                                if (!rapidBookingSkip) {
                                    val calculateDiscountValue = it.fare.toString()
                                        .toDouble() / 100.0f * discountValue.toString().toDouble()
                                    seatWiseFare.seatNo = it.seatNumber ?: getString(R.string.empty)
                                    seatWiseFare.discount = "$calculateDiscountValue"
                                    seatWiseFares.add(seatWiseFare)
                                }
                            }
                        }
                    } else {
                        passengerDataList.forEach {
                            val seatWiseFare = SeatWiseFare()
                            if (it.discountAmount.toString().isNotEmpty()) {
                                seatWiseFare.seatNo = it.seatNumber ?: getString(R.string.empty)
                                seatWiseFare.discount = it.discountAmount
                                seatWiseFares.add(seatWiseFare)
                            } else {
                                if (!rapidBookingSkip) {
                                    seatWiseFare.seatNo = it.seatNumber ?: getString(R.string.empty)
                                    seatWiseFare.discount = passengerDetailsViewModel.discountAmount
                                    seatWiseFares.add(seatWiseFare)
                                }
                            }
                        }
                    }

                    if (!rapidBookingSkip) {
                        reqBody.seatWiseFare = seatWiseFares
                    }

                    if (!country.equals("india", true)) {
                        seatWiseFares.forEach {
                            if (it.discount?.isNotEmpty() == true) {
                                totalDiscountAmountSeatWise += it.discount.toString().toDouble()
                            }
                        }
                        reqBody.totalDiscountAmount = totalDiscountAmountSeatWise.toString()
                    }
                }
            }

            if (passengerDetailsViewModel.isEnableCampaignPromotions && passengerDetailsViewModel.isEnableCampaignPromotionsChecked && passengerDetailsViewModel.perSeatDiscountList.isNotEmpty()) {
                val seatWiseFares = mutableListOf<SeatWiseFare>()
                var totalDiscountAmountSeatWise = 0.0
                passengerDataList.forEach {
                    val seatWiseFare = SeatWiseFare()
                    if (it.discountAmount.toString().isNotEmpty()) {
                        seatWiseFare.seatNo = it.seatNumber ?: getString(R.string.empty)
                        seatWiseFare.discount = it.discountAmount
                        seatWiseFares.add(seatWiseFare)
                    }
                }

                reqBody.seatWiseFare = seatWiseFares

                seatWiseFares.forEach {
                    totalDiscountAmountSeatWise += it.discount.toString().toDouble()
                }
                reqBody.totalDiscountAmount = totalDiscountAmountSeatWise.toString()

            }


            if (passengerDetailsViewModel.isEnableCampaignPromotions && passengerDetailsViewModel.isEnableCampaignPromotionsChecked && passengerDetailsViewModel.perSeatDiscountList.isEmpty()) {
                reqBody.totalDiscountAmount =
                    "${passengerDetailsViewModel.perBookingEditedDiscountValue}"
            }

            val additionalFares = mutableListOf<AdditionalFare>()

//        ----------additional Fare-------------
            passengerDataList.forEach {
                val additionalFare = AdditionalFare()
                additionalFare.seatNo = it.seatNumber ?: getString(R.string.empty)
                additionalFare.fare = it.additionalFare.toString()
                additionalFare.age = it.age ?: ""
                additionalFares.add(additionalFare)
            }

            reqBody.additionalFare = additionalFares.distinct()

            if (selectedSeatDetails.any {
                    !it.isExtraSeat
                            && it.fare != null
                            && it.fare.toString().toDouble() > 0.0
                }) {
                val editFares = mutableListOf<EditFare>()

                passengerDataList.forEach {
                    val editFare = EditFare()
                    editFare.seatNo = it.seatNumber ?: getString(R.string.empty)
                    editFare.age = it.age ?: getString(R.string.empty)
                    editFare.passengerCategory = getString(R.string.empty)

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
                passengerDetailsViewModel.isExtraSeat = true
                reqBody.isExtraSeat = true

                val extraSeatFares = mutableListOf<ExtraSeatFare>()

                passengerDataList.forEach {
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
        }

        reqBody.apiKey = passengerDetailsViewModel.loginModelPref?.api_key
        reqBody.resId = passengerDetailsViewModel.resId.toString()
        reqBody.origin = passengerDetailsViewModel.sourceId
        reqBody.destination = passengerDetailsViewModel.destinationId
        reqBody.boardingAt = passengerDetailsViewModel.boardingId.toString()
        reqBody.dropOff = passengerDetailsViewModel.droppingId.toString()
        reqBody.noOfSeats = passengerDetailsViewModel.noOfSeats?.toInt()
        reqBody.isMiddleTier = is_middle_tier
        reqBody.isRoundTrip = passengerDetailsViewModel.isRoundTrip
        reqBody.isBima = "${passengerDetailsViewModel.isBima}"
        reqBody.seatNumbers = seatNumberArray
        reqBody.returnSeatNumbers = returnSeatNumberArray
        reqBody.passengerTitles = PassengerTitles()
        reqBody.returnBoardingAt = passengerDetailsViewModel.returnBoardingPoint
        reqBody.returnDropoff = passengerDetailsViewModel.returnDroppingPoint
        reqBody.offerCoupon = passengerDetailsViewModel.offerCoupon
        reqBody.promoCoupon = passengerDetailsViewModel.promoCoupon
        reqBody.useSmartMiles = passengerDetailsViewModel.useSmartMiles
        reqBody.privCardNumber = passengerDetailsViewModel.privilegeCardNo
        reqBody.couponDetails = couponDetails
        reqBody.paymentType = passengerDetailsViewModel.selectedPaymentOptionId.toString()
        
        reqBody.agentPaymentType = if (passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
            if (passengerDetailsViewModel.selectedPaymentOptionId==1) {
                ""
            } else {
                "${passengerDetailsViewModel.selectedPaymentOptionId}"
            }
        } else {
            ""
        }
        
        reqBody.agentType = passengerDetailsViewModel.selectedBookingTypeId.toString()
        reqBody.locale = passengerDetailsViewModel.locale

        if (passengerDetailsViewModel.isExtraSeat)
            reqBody.isInsuranceEnabled = false
        else {
            if (passengerDetailsViewModel.isInsuranceChecked.value)
                reqBody.isInsuranceEnabled = passengerDetailsViewModel.isInsuranceChecked.value
        }


        bookingOptionViewModel.fareBreakupApi(
            reqBody,
            apiType = fare_breakup_method_name
        )
    }

    private fun bookTicketApi(isUpi: Boolean, authPin: String?) {
        passengerDetailsViewModel.showShimmer = true
        passengerDetailsViewModel.isUpiPayment = isUpi
        val seatList = mutableListOf<SeatDetail>()
        val packageDetailsId = PackageDetailsId()
        val couponDetails = mutableListOf<Any>()

        passengerDetailsViewModel.passengerDataList.forEach {
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
                if (it.seatNumber == seatNoList[i]) {
                    seatDetail.couponCode = couponList[i]
                }
            }

            seatDetail.isPrimary = it.isPrimary
            seatDetail.seatNumber = it.seatNumber ?: getString(R.string.empty)
            seatDetail.sex = it.sex ?: getString(R.string.empty)
            if (passengerDetailsViewModel.isInsuranceChecked.value)
                seatDetail.name = "${it.firstName} ${it.lastName}"
            else
                seatDetail.name = it.name ?: getString(R.string.empty)
            seatDetail.age = it.age ?: getString(R.string.empty)
            seatDetail.additionalFare = it.additionalFare

            if (!passengerDetailsViewModel.isAgentLogin) {
                if (passengerDetailsViewModel.isAllowToApplyDiscountOnBookingPageWithPercentage) {
                    if (it.discountAmount?.isNotEmpty() == true) {
                        val calculateDiscountValue =
                            it.fare.toString().toDouble() / 100.0f * it.discountAmount.toString()
                                .toDoubleOrNull()!!
                        seatDetail.discountAmount = calculateDiscountValue
                    } else {
                        seatDetail.discountAmount = it.discountAmount.toString().toDoubleOrNull()
                    }

                } else {
                    seatDetail.discountAmount = it.discountAmount.toString().toDoubleOrNull()
                }
            }
            seatDetail.isRoundTripSeat = passengerDetailsViewModel.isRoundTripSeat
            seatDetail.passengerCategory = getString(R.string.empty)
            seatDetail.firstName = it.firstName ?: getString(R.string.empty)
            seatDetail.lastName = it.lastName ?: getString(R.string.empty)
            seatDetail.idCardType = it.idCardTypeId?.toInt()
            seatDetail.idCardNumber = it.idCardNumber
            seatDetail.passportIssuedDate = it.passportIssuedDate
            seatDetail.passportExpiryDate = it.passportExpiryDate
            seatDetail.placeOfIssue = it.placeOfIssue
            seatDetail.nationality = it.nationality

            if (it.mealRequired) {
                seatDetail.mealRequired = it.mealRequired
                seatDetail.selectedMealType = it.selectedMealTypeId.toString()
            }
            //  seatDetail.fare = "0" // fare will be zero as per discussed Anand
            /*if (it.editFare != null && it.editFare.toString().isNotEmpty())
                seatDetail.fare = it.editFare
            else
                seatDetail.fare = it.baseFareFilter*/

            seatDetail.fare = it.fare


            seatList.add(seatDetail)
        }

        val bookTicketFullRequest = BookTicketFullRequest()
        bookTicketFullRequest.bccId = passengerDetailsViewModel.bccId.toString()
        bookTicketFullRequest.format = format_type
        bookTicketFullRequest.methodName = book_ticket_method_name

        val bookingDetails = BookingDetails()
        bookingDetails.isBimaTicket = passengerDetailsViewModel.isBima
        bookingDetails.agentType =
            if (passengerDetailsViewModel.selectedBookingTypeId.toString() == "4"
            ) {
                "0"
            } else if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.selectedBookingTypeId == 33) {
                "1"
            } else {
                passengerDetailsViewModel.selectedBookingTypeId.toString()
            }
        bookingDetails.remarks = passengerDetailsViewModel.remarks

        if(passengerDetailsViewModel.isAgentLogin) {
            bookingDetails.agentPaymentType =
                if (passengerDetailsViewModel.selectedPaymentOptionId == 1) {
                    ""
                } else {
                    "${passengerDetailsViewModel.selectedPaymentOptionId}"
                }

            if (passengerDetailsViewModel.isAgentSubPaymentSelected) {
                bookingDetails.agentSubPaymentType =
                    passengerDetailsViewModel.selectedSubPaymentOptionId
                bookingDetails.agentPhone = passengerDetailsViewModel.agentPayViaPhoneNumberSMS
                bookingDetails.agentVpa = passengerDetailsViewModel.agentPayViaVPA
            }

            if (passengerDetailsViewModel.payableAmount > 0.0 && passengerDetailsViewModel.selectedPaymentOptionId != 1) {
                if (passengerDetailsViewModel.selectedSubPaymentOptionId == PaymentTypes.PHONEPE_V2) {
                    bookingDetails.payGayType = PayGayTypes.PHONEPE_V2
                } else {
                    bookingDetails.payGayType = PayGayTypes.EASEBUZZ
                }
            }
        } else {
//            bookingDetails.agentPaymentType =
//                if (passengerDetailsViewModel.selectedPaymentOptionId == 1) {
//                    ""
//                } else {
//                    "${passengerDetailsViewModel.selectedPaymentOptionId}"
//                }

            if (passengerDetailsViewModel.isUserSubPaymentSelected) {
                bookingDetails.subPaymentType = passengerDetailsViewModel.selectedSubPaymentOptionId
                bookingDetails.branchPhone = passengerDetailsViewModel.userPayViaPhoneNumberSMS
                bookingDetails.branchVpa = passengerDetailsViewModel.userPayViaVPA

                bookingDetails.payGayType = if (passengerDetailsViewModel.selectedSubPaymentOptionId == PaymentTypes.PHONEPE_V2) {
                    PayGayTypesUser.PHONEPE_V2
                } else {
                    PayGayTypesUser.EASEBUZZ
                }
            }
        }
        
        if (passengerDetailsViewModel.isEnableCampaignPromotions && passengerDetailsViewModel.isEnableCampaignPromotionsChecked && passengerDetailsViewModel.perSeatDiscountList.isNotEmpty()) {
            val seatWiseFares = mutableListOf<SeatWiseFare>()
            var totalDiscountAmountSeatWise = 0.0
            passengerDetailsViewModel.passengerDataList.forEach {
                val seatWiseFare = SeatWiseFare()
                if (it.discountAmount.toString().isNotEmpty()) {
                    seatWiseFare.seatNo = it.seatNumber ?: getString(R.string.empty)
                    seatWiseFare.discount = it.discountAmount
                    seatWiseFares.add(seatWiseFare)
                }
            }
            seatWiseFares.forEach {
                totalDiscountAmountSeatWise += it.discount.toString().toDouble()

                bookingDetails.discountOnTotalAmount = totalDiscountAmountSeatWise.toString()
            }
        }

        if (passengerDetailsViewModel.isEnableCampaignPromotions && passengerDetailsViewModel.isEnableCampaignPromotionsChecked && passengerDetailsViewModel.perSeatDiscountList.isEmpty()) {
            bookingDetails.discountOnTotalAmount =
                "${passengerDetailsViewModel.perBookingEditedDiscountValue}"
        }


        // online
        if (passengerDetailsViewModel.selectedBookingTypeId.toString() == "1") {
            bookingDetails.onBehalfOnlineAgentValue =
                passengerDetailsViewModel.onBehalfOfAgentId.toString()
        }
        // offline
        if (passengerDetailsViewModel.selectedBookingTypeId.toString() == "2") {
            bookingDetails.onBehalf = passengerDetailsViewModel.onBehalfOfAgentId.toString()
            if (!passengerDetailsViewModel.amountPaidOffline)
                bookingDetails.onBehalfPaid = "0"
            else
                bookingDetails.onBehalfPaid = "1"
        }
        // branch
        if (passengerDetailsViewModel.selectedBookingTypeId.toString() == "3") {
            bookingDetails.onBehalfBranch = passengerDetailsViewModel.selectedBranchId.toString()
            bookingDetails.onBehalfUser = passengerDetailsViewModel.selectedUserId.toString()
        }


        if (passengerDetailsViewModel.privilegeResponseModel!!.isEzetapEnabledInTsApp && !passengerDetailsViewModel.phoneBlock) {
            if (passengerDetailsViewModel.selectedPaymentOptionId.toString() == "14") {
                bookingDetails.ezetapDeviceId = ezetapDeviceId ?: ""
                bookingDetails.isEzetapPayment = true
                bookingDetails.paymentType =
                    passengerDetailsViewModel.selectedPaymentOptionId.toString()
            }

        }


        if(passengerDetailsViewModel.selectedPaymentOption.asString(this.resources)?.contains("paytm",true)==true &&  !passengerDetailsViewModel.phoneBlock){
            bookingDetails.isPaytmPayment=true
            bookingDetails.paymentType="14"
        }else{
            bookingDetails.isPaytmPayment=false
        }


        val phoneBlockConstraints = PreferenceUtils.getString("phoneBlock")
        val singleDemo = phoneBlockConstraints!!.split("#")

        if (passengerDetailsViewModel.phoneBlock) {
            blockingDate = singleDemo[0]
            blockingTimeHours = singleDemo[1]
            blockingTimeMins = singleDemo[2]
            isPermanentPhoneBookingChecked = singleDemo[4].toBoolean()
            blockingAmPm = singleDemo[3]


            if (passengerDetailsViewModel.isPermanentPhoneBooking && isPermanentPhoneBookingChecked)
                bookingDetails.permanentBlockedFlag = "1"
            bookingDetails.blockedFlag = "1"
            bookingDetails.blockingDate = singleDemo[0]
            bookingDetails.blockingTimeHours = singleDemo[1]
            bookingDetails.blockingTimeMins = singleDemo[2]
            bookingDetails.blockingAmPm = singleDemo[3]

// Discount On individual seat
            if (passengerDetailsViewModel.individualDiscountAmount > 0) {
                bookingDetails.discountAmount =
                    passengerDetailsViewModel.individualDiscountAmount.toString()
            } else {
                val discountOnTotalIndex =
                    passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                        it.couponTypeResource?.asString(resources = resources) == getString(
                            R.string.discount_amount
                        )
                    }
                if (discountOnTotalIndex != -1) {
                    bookingDetails.discountOnTotalAmount =
                        passengerDetailsViewModel.discountOnTotalAmount
                }
            }
        }

        if (passengerDetailsViewModel.phoneBlock) {
            val blockedDateFormatted = inputFormatToOutput(
                blockingDate,
                DATE_FORMAT_D_M_Y, DATE_FORMAT_D_M_Y_SLASH
            )
//            bookingDetails.discountOnTotalAmount = discountOnTotalAmount.toString()
            if (passengerDetailsViewModel.isPermanentPhoneBooking && isPermanentPhoneBookingChecked)
                bookingDetails.permanentBlockedFlag =
                    passengerDetailsViewModel.blockedFlag
            bookingDetails.blockedFlag = passengerDetailsViewModel.blockedFlag
            bookingDetails.blockingDate = blockedDateFormatted
            bookingDetails.blockingTimeHours = blockingTimeHours
            bookingDetails.blockingTimeMins = blockingTimeMins
            bookingDetails.blockingAmPm = blockingAmPm

            // Discount On individual seat
            if (passengerDetailsViewModel.individualDiscountAmount > 0) {
                bookingDetails.discountAmount =
                    passengerDetailsViewModel.individualDiscountAmount.toString()
            } else {
                // Discount On Total Amount
                val discountOnTotalIndex =
                    passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                        it.couponTypeResource?.asString(resources = resources) == getString(
                            R.string.discount_amount
                        )
                    }
                if (discountOnTotalIndex != -1) {
                    bookingDetails.discountOnTotalAmount =
                        passengerDetailsViewModel.discountOnTotalAmount
                }
            }
        }
        if (passengerDetailsViewModel.selectedPaymentOptionId.toString() != "4")
            bookingDetails.paymentType =
                passengerDetailsViewModel.selectedPaymentOptionId.toString()

        // if payment option is "credit/debit card"
        if (passengerDetailsViewModel.creditDebitCardNo != null) {
            bookingDetails.creditTransactionNumber = passengerDetailsViewModel.creditDebitCardNo
        }

        // if payment option is "Others"
        if (passengerDetailsViewModel.selectedOtherPaymentOption != null) {
            bookingDetails.paymentTypeConfig = passengerDetailsViewModel.selectedOtherPaymentOption
        }

        // if payment option is "Wallet/UPI"
        if (passengerDetailsViewModel.selectedPaymentOptionId.toString() == "4") {
            if (isUpi) {
                val upiPaymentHash = UpiPaymentHash(
                    true
                )
                bookingDetails.upiPaymentHash = upiPaymentHash

            } else {
                val selectedWallet =
                    passengerDetailsViewModel.selectedWalletUpiOptionId?.toString() ?: "0"
                val walletPaymentHash = WalletPaymentHash(
                    allow_wallet_booking = passengerDetailsViewModel.allowWalletBooking,
                    selected_wallet = selectedWallet,
                    wallet_mobile_number = passengerDetailsViewModel.walletMobileNo
                )
                bookingDetails.walletPaymentHash = walletPaymentHash
            }
        }


        // Coupon Code
        val couponCodeIndex =
            passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.coupon_code)
            }
        if (couponCodeIndex != -1) {
            val couponCode =
                passengerDetailsViewModel.appliedCouponList[couponCodeIndex].coupon_code
            bookingDetails.couponCode = couponCode
        }

        // Promotion Coupon Code
        val promotionCouponCodeIndex =
            passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.promotion_coupon)
            }
        if (promotionCouponCodeIndex != -1) {
            val couponCode =
                passengerDetailsViewModel.appliedCouponList[promotionCouponCodeIndex].coupon_code
            bookingDetails.promotionCoupon = couponCode
        }

        // Free Ticket
        val isFreeTicket =
            passengerDetailsViewModel.appliedCouponList.any {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.free_ticket)
            }
        if (isFreeTicket)
            bookingDetails.isFreeBookingAllowed = passengerDetailsViewModel.isFreeBookingAllowed

        // Vip Booking
        val isVipTicket =
            passengerDetailsViewModel.appliedCouponList.any {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.vip_ticket)
            }
        if (isVipTicket) {
            bookingDetails.isVipTicket = passengerDetailsViewModel.vipTicket

            val vipTicketIndex =
                passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                    it.couponTypeResource?.asString(resources = resources) == getString(
                        R.string.vip_ticket
                    )
                }
            if (vipTicketIndex != -1) {
                val vipCategory =
                    passengerDetailsViewModel.appliedCouponList[vipTicketIndex].coupon_code.substringBefore(
                        ":"
                    )
                bookingDetails.vipBookingCategory = vipCategory
            }
        }

        // Discount On individual seat
        if (passengerDetailsViewModel.individualDiscountAmount > 0) {
            bookingDetails.discountAmount =
                passengerDetailsViewModel.individualDiscountAmount.toString()
        } else {
            // Discount On Total Amount
            val discountOnTotalIndex =
                passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                    it.couponTypeResource?.asString(resources = resources) == getString(
                        R.string.discount_amount
                    )
                }
            if (discountOnTotalIndex != -1) {
                val amount =
                    passengerDetailsViewModel.appliedCouponList[discountOnTotalIndex].coupon_code
                bookingDetails.discountOnTotalAmount = amount
            }
        }

        // Smart miles
        val smartMilesIndex =
            passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.apply_smart_miles)
            }
        if (smartMilesIndex != -1) {
            val smartMilesNumber =
                passengerDetailsViewModel.appliedCouponList[smartMilesIndex].coupon_code
            val smartMilesHash = SmartMilesHash()
            smartMilesHash.phoneNumber = smartMilesNumber
            bookingDetails.smartMilesHash = smartMilesHash
        }

        // Privilege Card
        val privilegeCardIndex =
            passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.privilege_card)
            }
        if (privilegeCardIndex != -1) {
            val cardOrMobileNumber =
                passengerDetailsViewModel.appliedCouponList[privilegeCardIndex].coupon_code
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
            privilegeCardHash.reservationId = passengerDetailsViewModel.resId.toString()
            privilegeCardHash.selectedSeats = passengerDetailsViewModel.noOfSeats
            bookingDetails.privilegeCardHash = privilegeCardHash
        }

        // Pre-postpone
        val prePostponeIndex =
            passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.pre_postpone_ticket)
            }
        if (prePostponeIndex != -1) {
            val prePostponeNumber =
                passengerDetailsViewModel.appliedCouponList[prePostponeIndex].coupon_code
            bookingDetails.prePostPonePnr = prePostponeNumber
            bookingDetails.isMatchPrepostponeAmount =
                passengerDetailsViewModel.isMatchPrepostponeAmount
        }


        // Previous Pnr
        val previousPnrIndex =
            passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.quote_previous_pnr)
            }
        if (previousPnrIndex != -1) {
            val previousPnrNumber =
                passengerDetailsViewModel.appliedCouponList[previousPnrIndex].coupon_code.substringBefore(
                    "-"
                )
            bookingDetails.previousPnrNumber = previousPnrNumber
            bookingDetails.phoneNumber =
                passengerDetailsViewModel.appliedCouponList[previousPnrIndex].coupon_code.substringAfter(
                    "-"
                )
        }


        val contactDetail = ContactDetail()
        contactDetail.email = passengerDetailsViewModel.emailId
        contactDetail.emergencyName = passengerDetailsViewModel.emergencyName
        contactDetail.mobileNumber = passengerDetailsViewModel.primaryMobileNo
        contactDetail.alternateNumber = passengerDetailsViewModel.alternateMobileNo
        contactDetail.sendSmsOnBooking = passengerDetailsViewModel.isSendSmsOnBooking.value

        val reqBody = ReqBody()
        reqBody.isWhatsappUpdate = passengerDetailsViewModel.sendWhatsAppOnBooking.value
        reqBody.apiKey = passengerDetailsViewModel.loginModelPref?.api_key
        reqBody.boardingAt = passengerDetailsViewModel.boardingId.toString()
        reqBody.bookingDetails = bookingDetails
        reqBody.contactDetail = contactDetail
        reqBody.couponDetails = couponDetails
        reqBody.destinationId = passengerDetailsViewModel.destinationId
        reqBody.dropOff = passengerDetailsViewModel.droppingId.toString()
        reqBody.locale = passengerDetailsViewModel.locale
        reqBody.noOfSeats = passengerDetailsViewModel.noOfSeats.toString()
        reqBody.operatorApiKey = operator_api_key
        reqBody.originId = passengerDetailsViewModel.sourceId
        reqBody.reservationId = passengerDetailsViewModel.resId.toString()
        reqBody.seatDetails = seatList
        reqBody.isFromBusOptApp = passengerDetailsViewModel.isFromBusOptApp
        reqBody.isFromMiddleTier = is_from_middle_tier
        reqBody.isRapidBooking = passengerDetailsViewModel.isRapidBooking
        reqBody.packageDetailsId = packageDetailsId
        reqBody.authPin = authPin
        if (!PreferenceUtils.getString("AutoDiscountCouponCode").isNullOrEmpty()) {
            reqBody.couponCode = PreferenceUtils.getString("AutoDiscountCouponCode")
        }

        // On behalf sub agent
        if (passengerDetailsViewModel.isAgentLogin && passengerDetailsViewModel.selectedBookingTypeId == 33) {
            reqBody.onBehalfOnlineAgent = passengerDetailsViewModel.onBehalfOfAgentId.toString()
        }

        // GST
        val gstIndex =
            passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.gst_details)
            }
        if (gstIndex != -1) {
            val gstNumber =
                passengerDetailsViewModel.appliedCouponList[gstIndex].coupon_code.substringBefore(
                    "-"
                )
            val gstCompanyName =
                passengerDetailsViewModel.appliedCouponList[gstIndex].coupon_code.substringAfter(
                    "-"
                )

            val passengerGstDetails = PassengerGstDetails()
            passengerGstDetails.gstId = gstNumber
            passengerGstDetails.registrationName = gstCompanyName
            reqBody.passengerGstDetails = passengerGstDetails
        }

        val reqBodyWithRapidBooking = RapidBookingRequest()
        if (passengerDetailsViewModel.isRapidBooking == "true") {
            passengerDetailsViewModel.apply {
                reqBodyWithRapidBooking.reservationId = resId.toString()
                reqBodyWithRapidBooking.apiKey = loginModelPref?.api_key
                reqBodyWithRapidBooking.originId = sourceId
                reqBodyWithRapidBooking.destinationId = destinationId
                reqBodyWithRapidBooking.noOfSeats = noOfSeats.toString()
                reqBodyWithRapidBooking.operatorApiKey = operator_api_key
                reqBodyWithRapidBooking.isFromBusOptApp = isFromBusOptApp.toBoolean()
                reqBodyWithRapidBooking.isRapidBooking = isRapidBooking.toBoolean()
                reqBodyWithRapidBooking.responseFormat = true
                reqBodyWithRapidBooking.seatNumbers =
                    selectedSeatNo.trim().filterNot { it.isWhitespace() }
                reqBodyWithRapidBooking.locale = locale
                reqBodyWithRapidBooking.editedSeatFare = finalEditedFare
                reqBodyWithRapidBooking.dropOff = passengerDetailsViewModel.droppingId.toString()
                reqBodyWithRapidBooking.boardingAt = passengerDetailsViewModel.boardingId.toString()
            }
        }

        val reqBodyWithInsurance = ReqBodyWithInsurance()
        if (passengerDetailsViewModel.isInsuranceChecked.value) {
            reqBodyWithInsurance.apiKey = passengerDetailsViewModel.loginModelPref?.api_key
            reqBodyWithInsurance.boardingAt = passengerDetailsViewModel.boardingId.toString()
            reqBodyWithInsurance.bookingDetails = bookingDetails
            reqBodyWithInsurance.contactDetail = contactDetail
            reqBodyWithInsurance.couponDetails = couponDetails
            reqBodyWithInsurance.destinationId = passengerDetailsViewModel.destinationId
            reqBodyWithInsurance.dropOff = passengerDetailsViewModel.droppingId.toString()
            reqBodyWithInsurance.locale = passengerDetailsViewModel.locale
            reqBodyWithInsurance.noOfSeats = passengerDetailsViewModel.noOfSeats.toString()
            reqBodyWithInsurance.operatorApiKey = operator_api_key
            reqBodyWithInsurance.originId = passengerDetailsViewModel.sourceId
            reqBodyWithInsurance.reservationId = passengerDetailsViewModel.resId.toString()
            reqBodyWithInsurance.seatDetails = seatList
            reqBodyWithInsurance.isFromBusOptApp = passengerDetailsViewModel.isFromBusOptApp
            reqBodyWithInsurance.isFromMiddleTier = is_from_middle_tier
            reqBodyWithInsurance.isRapidBooking = passengerDetailsViewModel.isRapidBooking
            reqBodyWithInsurance.packageDetailsId = packageDetailsId
            if (!PreferenceUtils.getString("AutoDiscountCouponCode").isNullOrEmpty()) {
                reqBodyWithInsurance.couponCode =
                    PreferenceUtils.getString("AutoDiscountCouponCode")
            }

        }


        if (passengerDetailsViewModel.isPartialPayment) {
            val enteredPartialAmt = passengerDetailsViewModel.partialAmount
            if (enteredPartialAmt != 0.0) {
                passengerDetailsViewModel.partialAmount = enteredPartialAmt
                passengerDetailsViewModel.pendingAmount =
                    passengerDetailsViewModel.totalFareString.toDouble()
                        .minus(passengerDetailsViewModel.partialAmount)
            }

            val partialPaymentDetails = PartialPaymentDetails()
            partialPaymentDetails.partialAmount = passengerDetailsViewModel.partialAmount
            partialPaymentDetails.pendingAmount = passengerDetailsViewModel.pendingAmount
            partialPaymentDetails.option = passengerDetailsViewModel.partialPaymentOption
            partialPaymentDetails.type = passengerDetailsViewModel.partialType
            partialPaymentDetails.blockingDate = passengerDetailsViewModel.partialBlockingDate
            partialPaymentDetails.timeHours = passengerDetailsViewModel.partialBlockingTimeHours
            partialPaymentDetails.timeMins = passengerDetailsViewModel.partialBlockingTimeMins
            reqBody.partialPaymentDetails = partialPaymentDetails

            if (passengerDetailsViewModel.isInsuranceChecked.value) {
                reqBodyWithInsurance.partialPaymentDetails = partialPaymentDetails
            }


        }

        reqBody.deviceInfo =
            passengerDetailsViewModel.loginModelPref?.let { getDeviceInfo(loginModel = it) }
        reqBodyWithInsurance.deviceInfo =
            passengerDetailsViewModel.loginModelPref?.let { getDeviceInfo(loginModel = it) }
        reqBodyWithRapidBooking.deviceInfo =
            passengerDetailsViewModel.loginModelPref?.let { getDeviceInfo(loginModel = it) }


        if (passengerDetailsViewModel.isInsuranceChecked.value)
            reqBodyWithInsurance.isInsuranceEnabled =
                passengerDetailsViewModel.isInsuranceChecked.value


        if (passengerDetailsViewModel.isInsuranceChecked.value) {
            bookTicketFullRequest.reqBody = reqBodyWithInsurance
            bookingOptionViewModel.bookTicketWithInsurance(
                reqBodyWithInsurance,
                apiType = book_ticket_method_name
            )
        } else if (passengerDetailsViewModel.isRapidBooking == "true") {
            bookTicketFullRequest.reqBody = reqBodyWithRapidBooking
            bookingOptionViewModel.bookTicketWithRapidBooking(
                reqBodyWithRapidBooking,
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
        val isPaymentTypeExcluded = passengerDetailsViewModel.excludeTicketConfirmation.any {
            it.id.toString() == passengerDetailsViewModel.selectedPaymentOptionId.toString()
        }
        if(passengerDetailsViewModel.isRapidBooking == "true") {
            bookTicketApi(isUpi, "")
            return
        } else if(passengerDetailsViewModel.phoneBlock) {
            if(passengerDetailsViewModel.country.equals("india",true)){
                if(passengerDetailsViewModel.shouldPhoneBlock) {
                    DialogUtils.showFullHeightPinInputBottomSheet(
                        activity = this,
                        fragmentManager = supportFragmentManager,
                        passengerDetailsViewModel.pinSize,
                        getString(R.string.phone_block_book_ticket),
                        onPinSubmitted = { pin: String ->
                            bookTicketApi(isUpi, pin)
                        },
                        onDismiss = null
                    )
                    return
                }
                bookTicketApi(isUpi, "")
                return
            }
            bookTicketApi(isUpi, "")
            return
        } else if(passengerDetailsViewModel.country.equals("india",true)) {

            if(passengerDetailsViewModel.shouldTicketConfirm){
                if(!isPaymentTypeExcluded){
                    DialogUtils.showFullHeightPinInputBottomSheet(
                        activity = this,
                        fragmentManager = supportFragmentManager,
                        passengerDetailsViewModel.pinSize,
                        getString(R.string.book_ticket),
                        onPinSubmitted = { pin: String ->
                            bookTicketApi(isUpi, pin)
                        },
                        onDismiss = null
                    )
                    return
                }
                else {
                    bookTicketApi(isUpi, "")
                    return
                }
            } else {
                bookTicketApi(isUpi, "")
                return
            }

        } else {
            bookTicketApi(isUpi, "")
            return
        }
    }

    private fun bookExtraSeatApi(authPin: String) {
        passengerDetailsViewModel.showShimmer = true

        val seatList = mutableListOf<com.bitla.ts.domain.pojo.book_extra_seat.request.SeatDetail>()

        passengerDetailsViewModel.passengerDataList.forEach {
            val seatDetail = com.bitla.ts.domain.pojo.book_extra_seat.request.SeatDetail()
            seatDetail.isPrimary = it.isPrimary.toString()
            seatDetail.seatNumber = it.seatNumber ?: getString(R.string.empty)
            seatDetail.sex = it.sex ?: getString(R.string.empty)
            seatDetail.name = it.name ?: getString(R.string.empty)
            seatDetail.age = it.age ?: getString(R.string.empty)
            seatDetail.firstName = it.name ?: getString(R.string.empty)
            seatDetail.lastName = it.name ?: getString(R.string.empty)
            seatDetail.idCardType = it.idCardTypeId.toString()
            seatDetail.idCardNumber = it.idCardNumber

            seatDetail.nationality = it.nationality
            seatDetail.fare = it.fare.toString()
            seatList.add(seatDetail)
        }


        val bookingDetails = com.bitla.ts.domain.pojo.book_extra_seat.request.BookingDetails()
        if (!passengerDetailsViewModel.isAgentLogin && !passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
            bookingDetails.paymentType = passengerDetailsViewModel.selectedPaymentOptionId.toString()
        }
        else if(passengerDetailsViewModel.isAgentLogin && !passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents){
            bookingDetails.paymentType = "1"
        }
        else {
            if (passengerDetailsViewModel.selectedPaymentOptionId == 1) {
                bookingDetails.paymentType = "1"
            } else {
                bookingDetails.paymentType = ""
            }
        }
        
        bookingDetails.agentType = passengerDetailsViewModel.selectedBookingTypeId.toString()
        bookingDetails.agentPaymentType = if (passengerDetailsViewModel.selectedPaymentOptionId==1 || !passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
            ""
        } else {
            "${passengerDetailsViewModel.selectedPaymentOptionId}"
        }
        
        if (passengerDetailsViewModel.isAgentSubPaymentSelected) {
            bookingDetails.agentSubPaymentType = passengerDetailsViewModel.selectedSubPaymentOptionId
            bookingDetails.agentPhone = passengerDetailsViewModel.agentPayViaPhoneNumberSMS
            bookingDetails.agentVpa = passengerDetailsViewModel.agentPayViaVPA

            if (passengerDetailsViewModel.payableAmount > 0.0 && passengerDetailsViewModel.selectedPaymentOptionId != 1) {
                if (passengerDetailsViewModel.selectedSubPaymentOptionId == PaymentTypes.PHONEPE_V2) {
                    bookingDetails.payGayType = PayGayTypes.PHONEPE_V2
                } else {
                    bookingDetails.payGayType = PayGayTypes.EASEBUZZ
                }
            }
        } else if (passengerDetailsViewModel.isUserSubPaymentSelected) {
            bookingDetails.subPaymentType = passengerDetailsViewModel.selectedSubPaymentOptionId
            bookingDetails.branchPhone = passengerDetailsViewModel.userPayViaPhoneNumberSMS
            bookingDetails.branchVpa = passengerDetailsViewModel.userPayViaVPA

            bookingDetails.payGayType = if (passengerDetailsViewModel.selectedSubPaymentOptionId == PaymentTypes.PHONEPE_V2) {
                PayGayTypesUser.PHONEPE_V2
            } else {
                PayGayTypesUser.EASEBUZZ
            }
        } else {
            bookingDetails.agentSubPaymentType = ""
            bookingDetails.agentPhone = ""
            bookingDetails.agentVpa = ""
        }


        if (passengerDetailsViewModel.selectedBookingTypeId.toString() == "1") {
            // bookingDetails.discountOnTotalAmount = discountOnTotalAmount
            bookingDetails.onBehalfOnlineAgentValue =
                passengerDetailsViewModel.bookingCustomRequest?.online_agent_on_behalf.toString()
        }
        // offline
        if (passengerDetailsViewModel.selectedBookingTypeId.toString() == "2") {
            //bookingDetails.discountOnTotalAmount = discountOnTotalAmount
            bookingDetails.onBehalf = passengerDetailsViewModel.onBehalfOfAgentName
            bookingDetails.onBehalfPaid = passengerDetailsViewModel.amountPaidOffline.toString()
        }
        // branch
        if (passengerDetailsViewModel.selectedBookingTypeId.toString() == "3") {
            // bookingDetails.discountOnTotalAmount = discountOnTotalAmount
            bookingDetails.onBehalfBranch =
                passengerDetailsViewModel.bookingCustomRequest?.branch_id.toString()
            bookingDetails.onBehalfUser =
                passengerDetailsViewModel.bookingCustomRequest?.branch_user.toString()
        }


        if (passengerDetailsViewModel.privilegeResponseModel!!.isEzetapEnabledInTsApp && !passengerDetailsViewModel.phoneBlock) {
            if (passengerDetailsViewModel.selectedPaymentOptionId.toString() == "14") {
                bookingDetails.ezetapDeviceId = ezetapDeviceId
                bookingDetails.isEzetapPayment = true
            }

        }


        if(passengerDetailsViewModel.selectedPaymentOption.asString(this.resources)?.contains("paytm",true)==true && !passengerDetailsViewModel.phoneBlock){
            bookingDetails.isPaytmPayment=true
            bookingDetails.paymentType="14"
        }else{
            bookingDetails.isPaytmPayment=false
        }


        // if payment option is "Others"
        if (passengerDetailsViewModel.selectedOtherPaymentOption != null) {
            bookingDetails.paymentTypeConfig = passengerDetailsViewModel.selectedOtherPaymentOption
        }


        // if payment option is "credit/debit card"
        if (passengerDetailsViewModel.creditDebitCardNo != null) {
            bookingDetails.creditTransactionNumber = passengerDetailsViewModel.creditDebitCardNo
        }

        val contactDetail = com.bitla.ts.domain.pojo.book_extra_seat.request.ContactDetail()
        contactDetail.email = passengerDetailsViewModel.emailId
        contactDetail.emergencyName = passengerDetailsViewModel.emergencyName
        contactDetail.alternateNumber = passengerDetailsViewModel.alternateMobileNo
        contactDetail.mobileNumber = passengerDetailsViewModel.primaryMobileNo

        val reqBody = com.bitla.ts.domain.pojo.book_extra_seat.request.ReqBody()
        reqBody.apiKey = passengerDetailsViewModel.loginModelPref?.api_key
        reqBody.boardingAt = passengerDetailsViewModel.boardingId.toString()
        reqBody.bookingDetails = bookingDetails
        reqBody.contactDetail = contactDetail
        reqBody.destinationId = passengerDetailsViewModel.destinationId
        reqBody.dropOff = passengerDetailsViewModel.droppingId.toString()
        reqBody.noOfSeats = passengerDetailsViewModel.noOfSeats!!
        reqBody.originId = passengerDetailsViewModel.sourceId
        reqBody.reservationId = passengerDetailsViewModel.resId.toString()
        reqBody.seatDetails = seatList
        reqBody.locale = passengerDetailsViewModel.locale
//        reqBody.isRapidBooking = passengerDetailsViewModel.isRapidBooking
        reqBody.authPin = authPin

        // GST
        val gstIndex =
            passengerDetailsViewModel.appliedCouponList.indexOfFirst {
                it.couponTypeResource?.asString(
                    resources = resources
                ) == getString(R.string.gst_details)
            }
        if (gstIndex != -1) {
            val gstNumber =
                passengerDetailsViewModel.appliedCouponList[gstIndex].coupon_code.substringBefore(
                    "-"
                )
            val gstCompanyName =
                passengerDetailsViewModel.appliedCouponList[gstIndex].coupon_code.substringAfter(
                    "-"
                )

            val passengerGstDetails = PassengerGstDetails()
            passengerGstDetails.gstId = gstNumber
            passengerGstDetails.registrationName = gstCompanyName
            reqBody.passengerGstDetails = passengerGstDetails


        }
        reqBody.deviceInfo =
            passengerDetailsViewModel.loginModelPref?.let { getDeviceInfo(loginModel = it) }
        val bookExtraSeatRequest = BookExtraSeatRequest()
        bookExtraSeatRequest.bccId = passengerDetailsViewModel.bccId.toString()
        bookExtraSeatRequest.format = format_type
        bookExtraSeatRequest.jsonFormat = json_format
        bookExtraSeatRequest.methodName = book_extra_seat_method_name
        bookExtraSeatRequest.reqBody = reqBody


        bookingOptionViewModel.bookExtraSeatApi(
            bookExtraSeatRequest = reqBody,
            apiType = book_extra_seat_method_name
        )

    }

    private fun callReleaseBranchUpiBlockedSeatsApi() {
        passengerDetailsViewModel.showShimmer = true
        val reqBody = com.bitla.ts.domain.pojo.book_ticket.release_ticket.request.ReleaseAgentRechargBlockedSeatsRequest(
            apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
            pnrNumber = passengerDetailsViewModel.bookTicketPnr,
            json_format = json_format,
            locale = passengerDetailsViewModel.locale
        )

        dashboardViewModel.releaseBranchUpiBlockedSeatsApi(
            reqBody
        )
    }

    private fun setReleaseBranchUpiBlockedSeatsObserver() {

        dashboardViewModel.releaseBranchUserBlockedSeatsResponseViewModel.observe(this) { it ->
            passengerDetailsViewModel.showShimmer = false
            if (it != null && it.code == 200) {
                toast(it.message)

                passengerDetailsViewModel.apply {
                    isShowUserSubPaymentDialog = false
                    selectedPaymentOptionId = 1
                    selectedPaymentOption = ResourceProvider.TextResource.fromStringId(R.string.cash)
                    isFareBreakupApiCalled = true
                    passengerDetailsViewModel.isUserSubPaymentSelected = false
                }

            } else {
                toast(getString(R.string.opps))
            }
        }
    }
    
    private fun callReleaseTicketApi() {
        passengerDetailsViewModel.showShimmer = true
        dashboardViewModel.releaseAgentRechargBlockedSeatsTicket(
            com.bitla.ts.domain.pojo.book_ticket.release_ticket.request.ReleaseAgentRechargBlockedSeatsRequest(
                apiKey = passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                pnrNumber = passengerDetailsViewModel.bookTicketPnr,
                json_format = json_format,
                locale = passengerDetailsViewModel.locale
            ),
        )
    }
    
    private fun setReleaseTicketObserver() {
        
        dashboardViewModel.releaseAgentRechargBlockedSeatsResponseViewModel.observe(this) { it ->
            passengerDetailsViewModel.showShimmer = false
            if (it != null && it.code == 200) {
                toast(it.message)
                
                passengerDetailsViewModel.apply {
                    isShowAgentSubPaymentDialog = false
                    selectedPaymentOptionId = 1
                    selectedPaymentOption = ResourceProvider.TextResource.fromStringId(R.string.cash)
                    isFareBreakupApiCalled = true
                    passengerDetailsViewModel.isAgentSubPaymentSelected = false
                }
                
            } else {
                toast(getString(R.string.opps))
            }
        }
    }
    
    private fun agentAccountInfo() {
        val agentRequest = AgentAccountInfoRequest(
            passengerDetailsViewModel.bccId.toString(),
            format_type,
            agent_account_info,
            com.bitla.ts.domain.pojo.account_info.request.ReqBody(
                passengerDetailsViewModel.loginModelPref?.api_key ?: "",
                locale = passengerDetailsViewModel.locale
            )
        )
        agentAccountInfoViewModel.agentAccountInfoAPI(
            agentRequest, "", "",
            agent_account_info
        )
    }

    private fun accountObserver() {
        agentAccountInfoViewModel.agentInfo.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        passengerDetailsViewModel.setAvailableBalance(it)
                    }

                    401 -> {
                        DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    }

                    else -> {
                        it.result.message?.let { it1 -> toast(it1) }
                    }
                }
            } else {
                //PassengerDetailsActivity.binding.passengerDetailsPaymentProceed.balanceAvailableHeader.gone()
                toast(getString(R.string.server_error))
            }
        }
    }

    override fun onDataSend(type: Int, file: Any) {
        //..
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

                passengerDetailsViewModel.partialBlockingDate = releaseDate

            }
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

    companion object {
        val tag: String = PassengerPaymentNewFlowActivity::class.java.simpleName
    }

    private fun callCampaignsAndPromotionsDiscountApi() {

        val selectedSeatNoList = mutableListOf<SelectedSeatNo>()

        passengerDetailsViewModel.passengerDataList.forEach {
            val selectedSeatNo = SelectedSeatNo(
                age = it.age ?: "",
                passengerCategory = "",
                seatNo = it.seatNumber ?: ""
            )
            selectedSeatNoList.add(selectedSeatNo)
        }

        val reqBody = com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.request.ReqBody(
            agentType = passengerDetailsViewModel.selectedBookingTypeId,
            selectedSeatNo = selectedSeatNoList
        )

        val campaignsAndPromotionsDiscountResponse = CampaignsAndPromotionsDiscountRequest(
            reservationId = passengerDetailsViewModel.resId.toString(),
            api_key = passengerDetailsViewModel.loginModelPref?.api_key,
            operator_api_key = operator_api_key,
            locale = passengerDetailsViewModel.locale,
            origin_id = passengerDetailsViewModel.sourceId,
            destination_id = passengerDetailsViewModel.destinationId,
            boardingAt = passengerDetailsViewModel.boardingId.toString(),
            dropOff = passengerDetailsViewModel.droppingId.toString(),
            reqBody = reqBody
        )

        bookingOptionViewModel.campaignsAndPromotionsDiscount(
            campaignsAndPromotionsDiscountRequest = campaignsAndPromotionsDiscountResponse
        )
    }

    private fun setCampaignsAndPromotionsDiscountObserver() {
        bookingOptionViewModel.campaignsAndPromotionsDiscount.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        passengerDetailsViewModel.setCampaignsAndPromotionsDiscountData(it)

                        fareBreakupApi()
                    }

                    401 -> {
                        DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    }

                    else -> {
                        passengerDetailsViewModel.setCampaignsAndPromotionsDiscountData(null)
                        toast(it.message ?: getString(R.string.opps))
                        if (passengerDetailsViewModel.isDeletePassengerClicked) {
                            fareBreakupApi()
                            passengerDetailsViewModel.isDeletePassengerClicked = false
                        }
                    }
                }
            } else {
                passengerDetailsViewModel.setCampaignsAndPromotionsDiscountData(null)
                toast(getString(R.string.server_error))
            }

            /*
                        if (passengerDetailsViewModel.isEnableCampaignPromotions && passengerDetailsViewModel.isEnableCampaignPromotionsChecked) {

                            Handler(Looper.getMainLooper()).postDelayed({
                                callCampaignsAndPromotionsDiscountApi()
                            }, 10000)

                        }*/
        }
    }
}
