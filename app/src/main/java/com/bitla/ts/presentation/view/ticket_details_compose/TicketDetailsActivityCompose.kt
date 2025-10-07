package com.bitla.ts.presentation.view.ticket_details_compose

import ClickHandler
import com.bitla.ts.BuildConfig
import android.Manifest
import android.annotation.*
import android.app.*
import android.bluetooth.*
import android.content.*
import android.content.pm.*
import android.graphics.*
import android.net.*
import android.os.*
import android.util.*
import android.view.*
import android.widget.*
import androidmads.library.qrgenearator.*
import androidx.activity.compose.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.*
import androidx.core.app.*
import androidx.core.content.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import coil.compose.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.confirm_pay_at_bus.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.passenger_details_result.*
import com.bitla.ts.domain.pojo.pay_pending_amount.*
import com.bitla.ts.domain.pojo.pay_pending_amount.request.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.send_sms_email.request.ReqBody
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.ticket_details_phase_3.*
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.*
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.PassengerDetail
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.ticketDetails.*
import com.bitla.ts.presentation.view.dashboard.*
import com.bitla.ts.presentation.view.ticket_details_compose.ui.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.PosPrintUtils.*
import com.bitla.ts.utils.bluetooth_print.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.dantsu.escposprinter.connection.*
import com.dantsu.escposprinter.connection.bluetooth.*
import com.dantsu.escposprinter.textparser.*
import com.google.android.material.bottomsheet.*
import com.google.gson.*
import com.google.zxing.*
import com.journeyapps.barcodescanner.*
import com.paytm.printgenerator.*
import com.paytm.printgenerator.page.*
import com.paytm.printgenerator.printer.*
import com.paytm.printgenerator.printer.Printer
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import org.json.*
import org.koin.androidx.viewmodel.ext.android.*
import retrofit2.*
import retrofit2.Response
import retrofit2.converter.gson.*
import timber.log.*
import toast
import visible
import java.io.*
import java.lang.Double.*
import java.net.*
import java.text.*
import java.util.*

class TicketDetailsActivityCompose : BaseActivity() {
    private var allowToSendSmsInPnrSearchPage: Boolean? = false
    private val ticketDetailsComposeViewModel by viewModel<TicketDetailsComposeViewModel<Any?>>()
    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()
    private var ticketNumber: String? = ""
    private lateinit var loginModelPref: LoginModel
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var originalTemplate: String? = null
    private var bluetoothPrintTemplate: String? = null
    private var busLogo: Bitmap? = null
    private var operatorLogo: String? = ""
    private var operatorName: String? = ""
    private var hexaDecimalString: String? = null
    private var qrCodeInput: String = ""
    private var withoutSpacePrint = false
    private var bmpLogo: Bitmap? = null
    private var bmpQrCode: Bitmap? = null
    private var insuranceBitmap: Bitmap? = null
    private var terminalQrBitmap: Bitmap? = null
    private var hexvalue: String? = ""
    private var printArray = JSONArray()
    private var TicketQRCode: Bitmap? = null
    private val BILLING_REQUEST_TAG = "MASTERAPPREQUEST"
    private val MESSAGE_CODE = 1001
    private val PLUTUS_SMART_PACKAGE = "com.pinelabs.masterapp"
    private val PLUTUS_SMART_ACTION = "com.pinelabs.masterapp.SERVER"
    private var message: Message = Message.obtain(null, MESSAGE_CODE)
    private var mServerMessenger: Messenger? = null
    private var isBound: Boolean? = false
    private var isFirstPrint: Boolean = true
    private var selectedDevice: BluetoothConnection? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null

    private lateinit var baseUpdateCancelTicket: BaseUpdateCancelTicket
    private lateinit var editPassengerSheet: EditPassengerSheet
    private lateinit var viewPassengerSheet: ViewPassengerSheet
    private lateinit var cancelTicketSheet: CancelTicketSheet
    private var locale = "en"
    private var redirectToDashBoardActivity: Boolean = true

    private val seatList = java.util.ArrayList<SeatDetail>()
    private var passengerList: java.util.ArrayList<PassengerDetailsResult> = java.util.ArrayList()
    private var passengerContactDetailList: java.util.ArrayList<ContactDetail> =
        java.util.ArrayList()
    var selectedSeatDetails = java.util.ArrayList<SeatDetail>()
    private lateinit var progressDialog: ProgressDialog
    private var domain: String = ""
    private var selectedPartialPaymentOption: String = "1"
    private var creditDebitCardNo: String? = null
    private val cancelTicketViewModel by viewModel<CancelTicketViewModel<Any?>>()
    private val selectedSeatNumber = StringBuilder()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private lateinit var _sheetReleaseTicketsBinding: SheetReleaseTicketsBinding
    private var cancelOtpLayoutDialogOpenCount = 0
    private var cancelOtp = ""
    private var otpDialog: AlertDialog? = null
    private var cancelOptkey = ""
    private var selectedCancellationType: String = ""
    private val currentCheckedItem: MutableList<PassengerDetail?> = java.util.ArrayList()
    private var isPickupDropoffChargesEnabled: Boolean = false
    private var bottomSheet: BottomSheetDialog? = null
    private var pinSize = 0
    private var phoneBlockingRelease = false
    private var shouldTicketCancellation = false
    private lateinit var bottomSheetDialoge: BottomSheetDialog
    private var reprintChargesAmount: Double? = null
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()

    companion object {
        val tag = TicketDetailsActivityCompose::class.java.simpleName
    }

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

        SunmiPrintHelper.getInstance().initSunmiPrinterService(this)
        getPref()
        initializePinelab()
        ticketDetailsComposeViewModel.setTicketStatus(
            cancelled = getString(R.string.cancelled),
            pending = getString(R.string.pending),
            phoneBlocked = getString(R.string.phone_blocked),
            booked = getString(R.string.booked),
            seatBooked = getString(R.string.seat_booked)
        )

        if (intent.hasExtra("returnToDashboard")) {
            redirectToDashBoardActivity = intent.getBooleanExtra("returnToDashboard", false)
        }

        if (intent.hasExtra(getString(R.string.TICKET_NUMBER))) {
            val getTicketNumber = intent.getStringExtra(getString(R.string.TICKET_NUMBER))
                ?: getString(R.string.empty)
            ticketNumber = getTicketNumber.substringBefore(" ")
        }

        if (intent.hasExtra("activityName2")) {
            ticketDetailsComposeViewModel.showRebookButton = true
        }

        setContent {
            Surface(
                modifier = Modifier
                    .background(
                        color = colorResource(R.color.flash_white_bg)
                    )
                    .fillMaxSize()
            ) {

                when (ticketDetailsComposeViewModel.isTicketDetailsApiSuccess) {
                    null -> {
                        RootProgressBar()
                    }

                    true -> RootLayout()
                    else -> {
                        InvalidPNRNumberLayout(
                            ticketNumber = ticketNumber ?: "",
                            onBackButtonClick = {
                                onBackPressed()
                            },
                        )
                    }
                }
            }
        }

        callTicketDetailsApi(false)
        setTicketDetailsObserver()
        setSendSMSEmailObserver()
        setUpdatePrintCountObserver()
        setMenuObserver()
        setCancelTicketObserver()
        setCancelPartialOtpTicketObserver()
        setConfirmOtpReleaseObserver()
        fetchLuggageDetailsObserver()
        updateLuggageDetailsObserver()
        showHideMenuFromOtherFragmentObserver()

        lifecycleScope.launch {
            ticketDetailsComposeViewModel.messageSharedFlow.collect {
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
            cancelTicketViewModel.messageSharedFlow.collect {
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
    }

    private fun setUpdatePrintCountObserver() {
        ticketDetailsComposeViewModel.updatePrintCountData.observe(this) {
            if (it != null && it.code == 200) {
                if (isNetworkAvailable()) {
                    callTicketDetailsApi(false)
                    ticketDetailsComposeViewModel.resetMenuAction()
                } else {
                    noNetworkToast()

                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
            allowToSendSmsInPnrSearchPage =
                privilegeResponseModel.allowToSendSmsInPnrSearchPage ?: false
            pinSize = privilegeResponseModel?.pinCount ?: 6
            phoneBlockingRelease =
                privilegeResponseModel?.pinBasedActionPrivileges?.phoneBlockingRelease ?: false
            shouldTicketCancellation =
                privilegeResponseModel?.pinBasedActionPrivileges?.ticketCancellation ?: false
            ticketDetailsComposeViewModel.currency =
                privilegeResponseModel?.currency ?: getString(R.string.rupess_symble)
            ticketDetailsComposeViewModel.country = privilegeResponseModel?.country ?: "India"
            ticketDetailsComposeViewModel.currencyFormat =
                if (!privilegeResponseModel.currencyFormat.isNullOrEmpty()) privilegeResponseModel.currencyFormat!! else "#,##,###.00"
            try {

                if (getCountryCodes().isNotEmpty()) {
                    ticketDetailsComposeViewModel.countryCode = "+${getCountryCodes()[0]}"
                } else {
                    ticketDetailsComposeViewModel.countryCode = "+91"
                }
            } catch (e: Exception) {
            }
            //}
            ticketDetailsComposeViewModel.allowBluetoothPrint =
                privilegeResponseModel.allowBluetoothPrint

            if (privilegeResponseModel.currencyFormat == null || privilegeResponseModel.currencyFormat!!.isEmpty()) {
                privilegeResponseModel.currencyFormat = getString(R.string.indian_currency_format)
            }
        }
        operatorLogo = PreferenceUtils.getString(PREF_LOGO)
        operatorName = loginModelPref.travels_name
        if (PreferenceUtils.getPrintingType() == PRINT_TYPE_PINELAB) {
            try {
                lifecycleScope.launch {
                    val imagePath = getBitmap(operatorLogo!!)
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
        if (PreferenceUtils.getPrintingType() == PRINT_TYPE_SUNMI) {
            lifecycleScope.launch { busLogo = getBitmap(operatorLogo!!) }
        }

        domain = PreferenceUtils.getPreference(
            PREF_DOMAIN,
            getString(R.string.empty)
        ) ?: ""
        locale = PreferenceUtils.getlang()


        isPickupDropoffChargesEnabled =
            PreferenceUtils.getPreference(PREF_PICKUP_DROPOFF_CHARGES_ENABLED, false) ?: false
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun RootLayout() {
        val isRefreshing = ticketDetailsComposeViewModel.swipeToRefresh

        val pullRefreshState = rememberPullRefreshState(isRefreshing, {
            ticketDetailsComposeViewModel.swipeToRefresh = true
            callTicketDetailsApi(false)
            ticketDetailsComposeViewModel.resetMenuAction()
        })
        Box(
            Modifier
                .fillMaxSize()
                .padding(
                    WindowInsets
                        .systemBars  // includes status + nav bar
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Bottom)
                        .asPaddingValues()
                )
                .pullRefresh(pullRefreshState)
                .background(
                    color = colorResource(R.color.flash_white_bg)
                )
        ) {


            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .background(
                        color = colorResource(R.color.flash_white_bg)
                    )
                    .fillMaxHeight()
            ) {
                Toolbar(ticketDetailsComposeViewModel = ticketDetailsComposeViewModel,
                    onBackButtonClick = {
                        onBackPressed()
                    },
                    onPrintButtonClick = {
                        if (privilegeResponseModel.country.equals("Malaysia", true)) {
                            if (privilegeResponseModel?.availableAppModes?.allowReprint == true || ticketDetailsComposeViewModel.printCount == 0) {
                                if(reprintChargesAmount == null || reprintChargesAmount == 0.0) {
                                    printTicket()
                                } else {
                                    DialogUtils.twoButtonDialog(
                                        this@TicketDetailsActivityCompose,
                                        "${getString(R.string.reprint_charges)}",
                                        getString(R.string.reprint_charges_confirmation, String.format("${privilegeResponseModel.currency} %.2f", reprintChargesAmount)),
                                        getString(R.string.cancel),
                                        getString(R.string.confirm),
                                        object : DialogButtonListener {
                                            override fun onLeftButtonClick() {

                                            }
                                            override fun onRightButtonClick() {
                                                printTicket()
                                            }

                                        }
                                    )
                                }
                            } else {
                                toast(getString(R.string.you_are_not_allowed_to_reprint_this_ticket))
                            }
                        } else {
                            if (privilegeResponseModel?.availableAppModes?.allowReprint == true || ticketDetailsComposeViewModel.printCount == 0) {
                                printTicket()
                            } else {
                                toast(getString(R.string.you_are_not_allowed_to_reprint_this_ticket))
                            }
                        }
                    },
                    onSideBarMenuClick = {
                        ticketDetailsComposeViewModel.showTicketDetailsSideBarMenu = true
                        if (ticketDetailsComposeViewModel.sideBarOptionsList.isEmpty()) {
                            callTicketDetailsMenusApi()
                        }
                        //ticketDetailsComposeViewModel.sideBarOptionsList.clear()
                    }
                )
                HeaderLayout(ticketDetailsComposeViewModel)
                TicketDetailsLayout()
            }
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            if (ticketDetailsComposeViewModel.ticketDetailsMenuOptions != null) {
                TicketDetailsSideBar()
            }

            BaseUpdateCancelTicketComposable()
            editPassengerSheetComposable()
            CancelTicketComposable()
            ViewPassengerSheetComposable()
            RootProgressBar()

        }
    }

    private fun callTicketDetailsMenusApi() {
        ticketDetailsComposeViewModel.showRootProgressBar = true

        ticketDetailsComposeViewModel.ticketDetailsMenus(
            loginModelPref.api_key,
            ticketNumber.toString(),
            jsonFormat = true,
            locale = locale
        )
    }

    @Composable
    private fun RootProgressBar() {
        val interactionSource = remember { MutableInteractionSource() }
        if (ticketDetailsComposeViewModel.showRootProgressBar) {
            val whiteBackgroundColor = colorResource(id = R.color.white)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .conditional(ticketDetailsComposeViewModel.showWhiteBackgroundInProgressBar) {
                        background(whiteBackgroundColor)
                    }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.colorPrimary)
                )
            }
        }
    }

    @Composable
    private fun BaseUpdateCancelTicketComposable() {
        AndroidViewBinding(
            TicketDetailsPhase3BaseUpdateCancelTicketContainerBinding::inflate,
            modifier = Modifier.fillMaxSize()
        ) {
            baseUpdateCancelTicket = layoutUpdateTicketContainer.getFragment()
        }
    }

    @Composable
    private fun editPassengerSheetComposable() {
        AndroidViewBinding(
            LayoutEditPassengerFragmentContainerBinding::inflate,
            modifier = Modifier.fillMaxSize()
        ) {
            editPassengerSheet = layoutEditPassengerSheet.getFragment()
        }
    }

    @Composable
    private fun CancelTicketComposable() {
        AndroidViewBinding(
            CancelTicketDialogContainerComposeBinding::inflate,
            modifier = Modifier.fillMaxSize()
        ) {
            cancelTicketSheet = layoutCancelTicketSheet.getFragment()
        }
    }

    @Composable
    private fun ViewPassengerSheetComposable() {
        AndroidViewBinding(
            ViewPassengerSheetBinding::inflate,
            modifier = Modifier.fillMaxSize()
        ) {
            viewPassengerSheet = layoutViewPassengerSheet.getFragment()
        }
    }


    @Composable
    private fun TicketDetailsLayout() {
        PNRLayout(ticketDetailsComposeViewModel)
        if (ticketDetailsComposeViewModel.ticketStatus.equals(
                getString(R.string.phone_blocked),
                true
            ) ||
            ticketDetailsComposeViewModel.ticketStatus.equals(getString(R.string.pending), true)
        ) {
            NotePhoneBooking(ticketDetailsComposeViewModel)
        }
        CardTicketDetails()
        if (ticketDetailsComposeViewModel.showTicketDetails) {
            if (!ticketDetailsComposeViewModel.ticketStatus.equals(
                    getString(R.string.cancelled),
                    true
                ) && ticketDetailsComposeViewModel.boardingPointContactNumber.isNotEmpty()
            ) {
                CardBoardingPointContact()
            }
            CardVehicleDetails(ticketDetailsComposeViewModel)
        }

        if (ticketDetailsComposeViewModel.whatsappShareTicketBtnVisibility() && privilegeResponseModel.allowToSendWhatsappMessages == true && !IS_PINELAB_DEVICE) {
            ShareTicketOnWhatsappLayout(onClick = {
                ticketDetailsComposeViewModel.shareTicketOnWhatsapp = true
                if (ticketDetailsComposeViewModel.smsTicketHash.isEmpty() || ticketDetailsComposeViewModel.sharingPDFLink.isEmpty() ||
                    ticketDetailsComposeViewModel.smsTicketHash.equals(
                        getString(
                            R.string.notAvailable
                        )
                    ) ||
                    ticketDetailsComposeViewModel.sharingPDFLink.equals(
                        getString(
                            R.string.notAvailable
                        )
                    )
                ) {
                    ticketDetailsComposeViewModel.shareTicketOnWhatsapp = true
                    callTicketDetailsMenusApi()
                } else {
                    shareToWhatsapp()
                }
            })
        }

        if (!ticketDetailsComposeViewModel.ticketStatus.equals(
                getString(R.string.cancelled),
                true
            )
        ) {
            DottedLine()
            if (ticketDetailsComposeViewModel.showRebookButton) {
                BookAnotherTicketLayout(onClick = {
                    bookTicketInSameService()
                })
            }
            if (ticketDetailsComposeViewModel.showRebookButton && ticketDetailsComposeViewModel.showNewBookingButton) {
                OrTextLayout()
            }
            if (ticketDetailsComposeViewModel.showNewBookingButton) {
                GoToBookingPageLayout(onClick = {
                    PreferenceUtils.putString(
                        getString(R.string.BACK_PRESS), getString(R.string.new_booking)
                    )
                    PreferenceUtils.removeKey(PREF_PICKUP_DROPOFF_CHARGES_ENABLED)
                    val intent = Intent(
                        this@TicketDetailsActivityCompose, DashboardNavigateActivity::class.java
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("newBooking", true)
                    startActivity(intent)
                    finish()
                })
            }
        }
    }

    @Composable
    private fun CardTicketDetails() {
        val cardAlpha = if (ticketDetailsComposeViewModel.isCancelledTicket) {
            0.5f
        } else {
            1f
        }
        Card(
            modifier = Modifier
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                .alpha(cardAlpha),

            shape = RoundedCornerShape(
                topStartPercent = 4, topEndPercent = 4, bottomEndPercent = 4, bottomStartPercent = 4
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
            ) {

                if (!ticketDetailsComposeViewModel.country.equals("india", true)) {

                    Row {
                        Column(Modifier.weight(1f)) {
                            Spacer(
                                modifier = Modifier
                                    .padding(4.dp)
                            )
                            TextBoldRegular(
                                text = operatorName.toString(), modifier = Modifier,
                                textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow))
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End,
                        ) {
                            AsyncImage(
                                model = operatorLogo,
                                contentDescription = "Operator Logo",
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(102.dp)
                            )
                        }


                    }

                    Row {
                        Column(Modifier.weight(1f)) {
                            Spacer(
                                modifier = Modifier
                                    .padding(10.dp)
                            )
                        }
                    }


                }




                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        TimeDateAndLocationLayout(
                            time = ticketDetailsComposeViewModel.boardingTime,
                            date = ticketDetailsComposeViewModel.boardingDate,
                            location = ticketDetailsComposeViewModel.boardingLocation,
                            locationTextAlignment = TextAlign.Start
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        JourneyDurationLayout()
                    }

                    Column(
                        modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End
                    ) {
                        TimeDateAndLocationLayout(
                            time = ticketDetailsComposeViewModel.droppingTime,
                            date = ticketDetailsComposeViewModel.droppingDate,
                            location = ticketDetailsComposeViewModel.droppingLocation,
                            locationTextAlignment = TextAlign.End
                        )
                    }
                }

                ViewHideLayout()
                AnimatedVisibility(visible = ticketDetailsComposeViewModel.showTicketDetails) {
                    Column {
                        if (ticketDetailsComposeViewModel.country.equals(
                                "indonesia",
                                true
                            ) && ticketDetailsComposeViewModel.qrCode.isNotEmpty()
                        ) {
                            qrCodeLayout()
                        }
                        PassengerDetailsLayout()
                        MobileNumberLayout()
                        if (isPickupDropoffChargesEnabled) {
                            val hasPickupAddress =
                                !ticketDetailsComposeViewModel.pickupAddress.equals("")
                            val hasDropoffAddress =
                                !ticketDetailsComposeViewModel.dropoffAddress.equals("")

                            if (hasPickupAddress) {
                                PickupAddressLayout()
                            }

                            if (hasDropoffAddress) {
                                if (hasPickupAddress) {
                                    AddressDivideLayout()
                                }
                                DropoffAddressLayout()
                            }
                        }
                        if (ticketDetailsComposeViewModel.isPartialTicket) {
                            PartialAmountLayout()
                        }
                        BookingAmountLayout()

                        if (privilegeResponseModel.allowQoalaInsurance && ticketDetailsComposeViewModel.passengerDetails.isNotEmpty() && ticketDetailsComposeViewModel.passengerDetails.any { it?.insuranceAmount != null }) {

                            InsuranceDetailsLayout(ticketDetailsComposeViewModel,
                                onInfoButtonClick = {
                                    if (!ticketDetailsComposeViewModel.isCancelledTicket) {

                                        DialogUtils.dialogInsurance(
                                            this@TicketDetailsActivityCompose,
                                            object : DialogSingleButtonListener {
                                                override fun onSingleButtonClick(str: String) {

                                                }

                                            },
                                            ticketDetailsComposeViewModel.insuranceTransDetails.value
                                        )
                                    }
                                }
                            )
                        }
                        SeatsLayout(ticketDetailsComposeViewModel,
                            onClick = {
                                if (!ticketDetailsComposeViewModel.isCancelledTicket) {

                                    viewPassengers()

                                }
                            }
                        )

                        MealsLayout(ticketDetailsComposeViewModel = ticketDetailsComposeViewModel)

                    }
                }
            }


        }

    }

    @Composable
    private fun qrCodeLayout() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = ticketDetailsComposeViewModel.qrCode,
                contentDescription = "Terminal Barcode",
                modifier = Modifier
                    .height(102.dp)
                    .width(102.dp)
            )
        }
    }

    @Composable
    private fun JourneyDurationLayout() {
        TextNormalRegular(
            text = ticketDetailsComposeViewModel.duration,
            modifier = Modifier,
            textStyle = TextStyle(
                color = colorResource(R.color.very_light_greyyy)
            )
        )

        Image(
            painter = painterResource(id = R.drawable.ticket_details_arrow),
            contentDescription = "Arrow",
            contentScale = ContentScale.FillWidth
        )
    }

    @Composable
    private fun TimeDateAndLocationLayout(
        time: String, date: String, location: String, locationTextAlignment: TextAlign
    ) {

        TextBoldRegular(
            text = "$time, ",
            modifier = Modifier,
            textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow))
        )

        TextNormalRegular(
            text = date,
            modifier = Modifier
        )

        Text(
            text = location, textAlign = locationTextAlignment
        )
    }

    @Composable
    private fun ViewHideLayout() {

        val drawableIconId = if (ticketDetailsComposeViewModel.showTicketDetails) {
            R.drawable.double_arrow_up
        } else {
            R.drawable.double_arrow_down
        }

        if (ticketDetailsComposeViewModel.showTicketDetails) {
            ticketDetailsComposeViewModel.showTicketDetailsButtonText =
                getString(R.string.Hide_details)
        } else {
            ticketDetailsComposeViewModel.showTicketDetailsButtonText =
                getString(R.string.View_details)
        }

        Row(
            modifier = Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            val dottedLineColor = colorResource(id = R.color.view_color)
            Canvas(
                Modifier
                    .height(1.dp)
                    .weight(1f)
            ) {

                drawLine(
                    color = dottedLineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = pathEffect
                )
            }
            Row(
                modifier = Modifier
                    .border(
                        BorderStroke(1.dp, colorResource(id = R.color.colorPrimary)),
                        RoundedCornerShape(
                            topStartPercent = 80,
                            topEndPercent = 80,
                            bottomEndPercent = 80,
                            bottomStartPercent = 80
                        )
                    )
                    .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
                    .clickable {
                        ticketDetailsComposeViewModel.showTicketDetails =
                            !ticketDetailsComposeViewModel.showTicketDetails

                        if (ticketDetailsComposeViewModel.showTicketDetails) {
                            ticketDetailsComposeViewModel.showTicketDetailsButtonText =
                                getString(R.string.Hide_details)
                        } else {
                            ticketDetailsComposeViewModel.showTicketDetailsButtonText =
                                getString(R.string.View_details)
                        }
                    }, verticalAlignment = Alignment.CenterVertically

            ) {
                TextNormalRegular(
                    text = ticketDetailsComposeViewModel.showTicketDetailsButtonText,
                    modifier = Modifier,
                    textStyle = TextStyle(
                        fontSize = 10.sp,
                        color = colorResource(id = R.color.colorPrimary),
                    ),
                )

                Image(
                    painter = painterResource(id = drawableIconId),
                    contentDescription = "Double Arrow",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .height(16.dp)
                        .width(16.dp)

                )
            }


            Canvas(
                Modifier
                    .height(1.dp)
                    .weight(1f)
            ) {

                drawLine(
                    color = dottedLineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = pathEffect
                )

            }
        }
    }

    @Composable
    private fun PassengerDetailsLayout() {
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            ticketDetailsComposeViewModel.passengerDetails.forEach {
                if (it != null) {
                    PassengerDetailsItem(it)
                }
            }
        }
    }


    @Composable
    private fun PassengerDetailsItem(item: com.bitla.ts.domain.pojo.ticket_details_phase_3.response.PassengerDetail) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Column {
                TextNormalRegular(text = stringResource(id = R.string.name), modifier = Modifier)
                TextBoldRegular(
                    text = "${item.name} (${item.age})",
                    modifier = Modifier,
                    textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow))
                )
            }

            if (item.gender?.isNotEmpty() == true) {
                Column(
                    modifier = Modifier.weight(0.2f), horizontalAlignment = Alignment.End
                ) {
                    TextNormalRegular(
                        text = stringResource(id = R.string.gender), modifier = Modifier
                    )
                    TextBoldRegular(
                        text = if (item.gender.equals("M", true)) {
                            getString(R.string.genderM)
                        } else if (item.gender.equals(
                                "F", true
                            )
                        ) {
                            getString(R.string.genderF)
                        } else {
                            item.gender
                        },
                        modifier = Modifier,
                        textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow)),
                    )
                }
            }
        }
    }

    @Composable
    private fun MobileNumberLayout() {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            if (!isPassengerMobileEmpty(ticketDetailsComposeViewModel.passengerMobileNumber)) {
                TextNormalRegular(
                    text = stringResource(id = R.string.mobile_number),
                    modifier = Modifier
                )

                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .border(
                            BorderStroke(1.dp, colorResource(id = R.color.colorPrimary)),
                            RoundedCornerShape(
                                topStartPercent = 10,
                                topEndPercent = 10,
                                bottomEndPercent = 10,
                                bottomStartPercent = 10
                            )
                        )
                        .padding(8.dp)
                        .clickable {
                            if (!ticketDetailsComposeViewModel.isCancelledTicket && ticketDetailsComposeViewModel.passengerMobileNumber.contains(
                                    "*"
                                ) == false && !IS_PINELAB_DEVICE
                            ) {

                                callFunction(ticketDetailsComposeViewModel.ticketDetailsPassMobNumber)
                            }
                        }, verticalAlignment = Alignment.CenterVertically

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.call),
                        contentDescription = "Call Button",
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp)
                            .height(16.dp)
                            .width(16.dp)
                    )

                    TextBoldRegular(
                        text = ticketDetailsComposeViewModel.ticketDetailsPassMobNumber,
                        modifier = Modifier,
                        textStyle = TextStyle(color = colorResource(id = R.color.colorPrimary)),
                    )
                }
            }
        }
    }

    @Composable
    private fun PickupAddressLayout() {
        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                TextBoldRegular(
                    text = "${stringResource(id = R.string.pickup_address)}",
                    modifier = Modifier,
                    textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow))
                )
                TextNormalRegular(
                    text = "${ticketDetailsComposeViewModel.pickupAddress}",
                    modifier = Modifier,
                    textStyle = TextStyle(fontStyle = FontStyle.Italic)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_address_location),
                contentDescription = "Location Icon",
                modifier = Modifier
                    .height(36.dp)
                    .width(36.dp)
                    .padding(start = 8.dp)
            )
        }
    }

    @Composable
    private fun AddressDivideLayout() {
        Row(
            modifier = Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            val dottedLineColor = colorResource(id = R.color.view_color)
            Canvas(
                Modifier
                    .height(1.dp)
                    .weight(1f)
            ) {
                drawLine(
                    color = dottedLineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = pathEffect
                )
            }
        }
    }

    @Composable
    private fun DropoffAddressLayout() {
        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                TextBoldRegular(
                    text = "${stringResource(id = R.string.dropoff_address)}",
                    modifier = Modifier,
                    textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow))
                )
                TextNormalRegular(
                    text = "${ticketDetailsComposeViewModel.dropoffAddress}",
                    modifier = Modifier,
                    textStyle = TextStyle(fontStyle = FontStyle.Italic)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_address_location),
                contentDescription = "Location Icon",
                modifier = Modifier
                    .height(36.dp)
                    .width(36.dp)
                    .padding(start = 8.dp)
            )
        }
    }

    @Composable
    private fun PartialAmountLayout() {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                TextNormalRegular(text = stringResource(id = R.string.paid), modifier = Modifier)
                TextBoldRegular(
                    text = "${ticketDetailsComposeViewModel.currency} ${
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount?.toDouble()
                            ?.convert(ticketDetailsComposeViewModel.currencyFormat) ?: ""
                    }",
                    modifier = Modifier,
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorBlackShadow
                        )
                    )
                )
            }
            Column(modifier = Modifier.padding(top = 12.dp)) {
                TextNormalRegular(
                    text = stringResource(id = R.string.remaining),
                    modifier = Modifier
                )
                TextBoldRegular(
                    text = "${ticketDetailsComposeViewModel.currency} ${
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount?.toDouble()
                            ?.convert(ticketDetailsComposeViewModel.currencyFormat) ?: ""
                    }",
                    modifier = Modifier,
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorBlackShadow
                        )
                    )
                )
            }
            Column(modifier = Modifier.padding(top = 12.dp)) {
                TextNormalRegular(text = stringResource(id = R.string.total), modifier = Modifier)

                TextBoldRegular(
                    text = "${ticketDetailsComposeViewModel.currency} ${
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.totalAmount?.toDouble()
                            ?.convert(ticketDetailsComposeViewModel.currencyFormat) ?: ""
                    }",
                    modifier = Modifier,
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorBlackShadow
                        )
                    )
                )
            }
        }
    }

    @Composable
    private fun BookingAmountLayout() {
        var bookingAmountText = ""
        if (ticketDetailsComposeViewModel.country.equals("india", true)) {
            bookingAmountText =
                "${ticketDetailsComposeViewModel.currency} ${ticketDetailsComposeViewModel.bookingAmount} (${
                    stringResource(
                        id = R.string.Inclusive_of_GST
                    )
                })"
        } else {
            if (!ticketDetailsComposeViewModel.bookingAmount.isNullOrEmpty()) {
                if (isPickupDropoffChargesEnabled) {
                    val bookingAmount = "${ticketDetailsComposeViewModel.currency} ${
                        ticketDetailsComposeViewModel.bookingAmount.toDouble()
                            .convert(ticketDetailsComposeViewModel.currencyFormat)
                    }"

                    bookingAmountText = when {
                        ticketDetailsComposeViewModel.pickupCharge != 0.0 && ticketDetailsComposeViewModel.dropoffCharge != 0.0 ->
                            "$bookingAmount (${stringResource(id = R.string.inclusive_of_pickup_dropoff_charges)}: ${ticketDetailsComposeViewModel.pickupCharge + ticketDetailsComposeViewModel.dropoffCharge})"

                        ticketDetailsComposeViewModel.pickupCharge != 0.0 ->
                            "$bookingAmount (${stringResource(id = R.string.inclusive_of_pickup_charge)}: ${ticketDetailsComposeViewModel.pickupCharge})"

                        ticketDetailsComposeViewModel.dropoffCharge != 0.0 ->
                            "$bookingAmount (${stringResource(id = R.string.inclusive_of_dropoff_charge)}: ${ticketDetailsComposeViewModel.dropoffCharge})"

                        else -> bookingAmount
                    }
                } else {
                    bookingAmountText = "${ticketDetailsComposeViewModel.currency} ${
                        ticketDetailsComposeViewModel.bookingAmount.toDouble()
                            .convert(ticketDetailsComposeViewModel.currencyFormat)
                    }"
                }
            }
        }
        Column(modifier = Modifier.padding(top = 12.dp)) {
            TextNormalRegular(
                text = stringResource(id = R.string.booking_amount),
                modifier = Modifier
            )
            TextBoldRegular(
                text = bookingAmountText,
                modifier = Modifier,
                textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow))
            )
        }
    }

    @Composable
    private fun CardBoardingPointContact() {
        Card(
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(
                topStartPercent = 10,
                topEndPercent = 10,
                bottomEndPercent = 10,
                bottomStartPercent = 10
            )
        ) {
            BoardingPointContactLayout()
        }
    }

    @Composable
    private fun BoardingPointContactLayout() {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            TextNormalRegular(
                text = stringResource(id = R.string.boarding_point_contact),
                modifier = Modifier
            )

            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .border(
                        BorderStroke(1.dp, colorResource(id = R.color.colorPrimary)),
                        RoundedCornerShape(
                            topStartPercent = 10,
                            topEndPercent = 10,
                            bottomEndPercent = 10,
                            bottomStartPercent = 10
                        )
                    )
                    .padding(8.dp)
                    .clickable {
                        callBoardingDetailFunction(ticketDetailsComposeViewModel.boardingPointContactNumber)
                    }, verticalAlignment = Alignment.CenterVertically

            ) {
                Image(
                    painter = painterResource(id = R.drawable.call),
                    contentDescription = "Call Button",
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .height(16.dp)
                        .width(16.dp)
                )

                TextBoldRegular(
                    text = "${ticketDetailsComposeViewModel.countryCode} ${ticketDetailsComposeViewModel.boardingPointContactNumber}",
                    modifier = Modifier,
                    textStyle = TextStyle(color = colorResource(id = R.color.colorPrimary)),
                )
            }
        }
    }

    private fun callTicketDetailsApi(menuPrivilege: Boolean) {

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
            menuPrivilege = menuPrivilege
        )
    }

    private fun setTicketDetailsObserver() {
        ticketDetailsComposeViewModel.dataTicketDetails.observe(this) {
            ticketDetailsComposeViewModel.showRootProgressBar = false

            if (it != null) {
                when (it.code) {
                    200 -> {

                        if (it.body != null && it.body?.code == 419) {
                            ticketDetailsComposeViewModel.setIsTicketDetailsApiSuccess(false)
                            // failed case

                        } else {
                            ticketDetailsComposeViewModel.showWhiteBackgroundInProgressBar = false
                            ticketDetailsComposeViewModel.reservationId =
                                it.body?.reservationId ?: 0L
                            ticketDetailsComposeViewModel.setIsTicketDetailsApiSuccess(true)

                            ticketDetailsComposeViewModel.apply {
                                if ((it.body?.ticketStatus.equals(
                                        getString(R.string.booked),
                                        true
                                    ) && it.body?.partialPaymentDetails != null) || (it.body?.ticketStatus.equals(
                                        getString(R.string.seat_booked),
                                        true
                                    ) && it.body?.partialPaymentDetails != null)
                                ) {
                                    isPartialTicket = true
                                } else {
                                    isPartialTicket = false
                                }
                                lifecycleScope.launch {
                                    if (!it.body?.terminalRefQrCode.isNullOrEmpty()) {
                                        terminalQrBitmap =
                                            getBitmap(it.body?.terminalRefQrCode ?: "")
                                    }
                                }
                                headerBgColor = getHeaderColor(it.body?.ticketStatus)
                                ticketStatusIcon = getTicketStatusIcon(
                                    it.body?.ticketStatus,
                                    it.body?.partialPaymentDetails
                                )

                                qrCodeInput = it.body?.qrCodeData ?: ""

                                ticketStatusTitle = getTicketStatusTitle(
                                    it.body?.ticketStatus,
                                    it.body?.partialPaymentDetails
                                )
                                setTicketDetailsData(
                                    ticketDetailsResponse = it, notAvailableString = getString(
                                        R.string.notAvailable
                                    )
                                )

                                passengerGender = it.body?.passengerDetails?.get(0)?.gender ?: ""
                                if (passengerGender.equals("M", true)) {
                                    passengerGender = getString(R.string.genderM)
                                } else if (passengerGender.equals("F", true)
                                ) {
                                    passengerGender = getString(R.string.genderF)
                                }

                                pickupAddress = it.body?.pickupAddress ?: ""
                                dropoffAddress = it.body?.dropoffAddress ?: ""
                                pickupCharge = it.body?.pickupCharge ?: 0.0
                                dropoffCharge = it.body?.dropoffCharge ?: 0.0
                                totalInsuranceAmt = 0.0

                                if (ticketStatus.equals(getString(R.string.cancelled), true)) {
                                    isCancelledTicket = true
                                }

                                if (ticketStatus.equals(getString(R.string.phone_blocked), true) ||
                                    ticketStatus.equals(getString(R.string.pending), true)
                                ) {
                                    showNewBookingButton = false
                                } else {
                                    showNewBookingButton = true
                                }

                                passengerDetails.forEach {
                                    totalInsuranceAmt += it?.insuranceAmount?.replace("Rp", "")
                                        ?.replace("RM", "")
                                        ?.replace("$", "")
                                        ?.toDouble() ?: 0.0
                                }

                                if (privilegeResponseModel.allowQoalaInsurance && it.body?.passengerDetails != null && it.body?.passengerDetails?.any { it?.insuranceAmount != null } == true) {
                                    showInsuranceDetails = true
                                } else {
                                    showInsuranceDetails = false
                                }
                            }

                            originalTemplate = it.body?.tsAppPrintTemplate
                            reprintChargesAmount = it.body?.reprintCharges

                            PreferenceUtils.apply {
                                setPreference(
                                    PREF_BOARDING_TIME,
                                    ticketDetailsComposeViewModel.boardingTime
                                )
                                setPreference(
                                    PREF_BOARDING_AT,
                                    ticketDetailsComposeViewModel.bAddress
                                )
                                setPreference(
                                    PREF_BOARDING_DATE,
                                    ticketDetailsComposeViewModel.boardingDate
                                )
                                setPreference(
                                    PREF_DROP_OFF_TIME,
                                    ticketDetailsComposeViewModel.droppingTime
                                )
                                setPreference(PREF_DROP_OFF, ticketDetailsComposeViewModel.dAddress)
                                setPreference(
                                    PREF_DROP_OFF_DATE,
                                    ticketDetailsComposeViewModel.boardingDate
                                )
                            }

                            passengerList.clear()
                            if (it.body?.passengerDetails != null) {
                                for (i in 0..it.body.passengerDetails.size.minus(1)) {


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
                                            expand = true,
                                            isPrimary = true,
                                            seatNumber = it.body.passengerDetails[i]!!.seatNumber,
                                            name = it.body.passengerDetails[i]!!.name,
                                            age = it.body.passengerDetails[i]?.age.toString(),
                                            sex = it.body.passengerDetails[i]!!.gender,
                                            contactDetail = passengerContactDetailList,
                                            fare = (it.body.passengerDetails[i]?.netFare
                                                ?: 0.0).toString(),
                                            meal_coupons = it.body.passengerDetails[i]?.mealCoupons
                                                ?: mutableListOf(),
                                            mealRequired = it.body.passengerDetails[i]?.mealRequired
                                                ?: false,
                                            selectedMealType = it.body.passengerDetails[i]?.selectedMealType
                                        )
                                    )

                                    for (j in 0 until it.body.passengerDetails.size) {
                                        val seatDetail = SeatDetail()
                                        seatDetail.isPrimary = true
                                        seatDetail.number =
                                            it.body.passengerDetails[j]?.seatNumber ?: ""
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

    private fun getTicketStatusIcon(
        ticketStatus: String?,
        partialPaymentDetails: PartialPaymentDetails?
    ): Int {
        return when {
            (ticketStatus.equals(
                getString(R.string.booked),
                true
            ) && partialPaymentDetails != null) || (ticketStatus.equals(
                getString(R.string.seat_booked),
                true
            ) && partialPaymentDetails != null) -> R.drawable.ic_partial_ticket

            ticketStatus.equals(
                getString(R.string.booked),
                true
            ) || ticketStatus.equals(getString(R.string.seat_booked), true) -> R.drawable.thumbs_up

            ticketStatus.equals(
                getString(R.string.phone_blocked),
                true
            ) -> R.drawable.partially_cancelled_img

            ticketStatus.equals(getString(R.string.cancelled), true) -> R.drawable.cancelled_img
            ticketStatus.equals(getString(R.string.pending), true) -> R.drawable.unconfirmed_ticket
            else -> R.drawable.thumbs_up
        }
    }

    private fun getTicketStatusTitle(
        ticketStatus: String?,
        partialPaymentDetails: PartialPaymentDetails?
    ): String? {
        return when {
            (ticketStatus.equals(
                getString(R.string.booked),
                true
            ) && partialPaymentDetails != null) || (ticketStatus.equals(
                getString(R.string.seat_booked),
                true
            ) && partialPaymentDetails != null) -> getString(R.string.partially_paid_ticket)

            ticketStatus.equals(
                getString(R.string.seat_booked),
                true
            ) -> getString(R.string.ticket_booked_successfully)

            ticketStatus.equals(
                getString(R.string.booked),
                true
            ) -> getString(R.string.ticket_booked_successfully)

            ticketStatus.equals(
                getString(R.string.phone_blocked),
                true
            ) -> getString(R.string.partially_cancelled_ticket)

            ticketStatus.equals(
                getString(R.string.cancelled),
                true
            ) -> getString(R.string.cancelled)

            ticketStatus.equals(
                getString(R.string.pending),
                true
            ) -> getString(R.string.phone_booking)

            else -> getString(R.string.ticket_booked_successfully)
        }
    }

    private fun getHeaderColor(ticketStatus: String?): Color {
        return when {
            ticketDetailsComposeViewModel.isPartialTicket -> Color(resources.getColor(R.color.partialTicket))

            ticketStatus.equals(
                getString(R.string.booked),
                true
            ) || ticketStatus.equals(
                getString(R.string.phone_blocked),
                true
            ) || ticketStatus.equals(
                getString(R.string.seat_booked),
                true
            ) -> Color(resources.getColor(R.color.colorPrimary))

            ticketStatus.equals(
                getString(R.string.cancelled),
                true
            ) -> Color(resources.getColor(R.color.colorRed2))

            ticketStatus.equals(
                getString(R.string.pending),
                true
            ) -> Color(resources.getColor(R.color.blue_dark))

            else -> Color(resources.getColor(R.color.colorPrimary))
        }
    }


    private fun setMenuObserver() {
        var isClicked = false

        ticketDetailsComposeViewModel.dataTicketDetailsMenus.observe(this) {
            ticketDetailsComposeViewModel.showRootProgressBar = false


            if (it != null) {
                when (it.code) {
                    200 -> {
                        it.body?.let { it1 -> ticketDetailsComposeViewModel.setMenusAction(it1) }

                        if (ticketDetailsComposeViewModel.shareTicketOnWhatsapp) {
                            shareToWhatsapp()
                        }
                        ticketDetailsComposeViewModel.shareTicketOnWhatsapp = false

                        ticketDetailsComposeViewModel.sideBarOptionsList.clear()

                        ticketDetailsComposeViewModel.sideBarOptionsList.add(
                            TicketDetailsSideBarOptionsModel(
                                R.drawable.ticket_history, getString(R.string.ticket_history)
                            ) {
                                 lifecycleScope.launch {
                                     ClickHandler.runWithDelay {
                                         val intent = Intent(
                                             this@TicketDetailsActivityCompose,
                                             BookingHistoryActivity::class.java
                                         )
                                         intent.putExtra(getString(R.string.pnr_number), ticketNumber)
                                         startActivity(intent)
                                     }
                                 }





                            })

                        if (ticketDetailsComposeViewModel.smsBtnVisibility() && allowToSendSmsInPnrSearchPage == true) {
                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.message_bubble, getString(R.string.sms)
                                ) {

                                    lifecycleScope.launch {
                                        ClickHandler.runWithDelay {
                                            callSendSMSEmailApi("sms")

                                            firebaseLogEvent(
                                                this@TicketDetailsActivityCompose,
                                                SHARE_VIA_SMS,
                                                loginModelPref.userName,
                                                loginModelPref.travels_name,
                                                loginModelPref.role,
                                                SHARE_VIA_SMS,
                                                SmsShare.SMS_SHARE_TICKET_DETAILS
                                            )

                                            lifecycleScope.launch {
                                                delay(1000)
                                                isClicked = false
                                            }
                                        }
                                    }



                                })
                        }

                        if (ticketDetailsComposeViewModel.emailBtnVisibility()) {
                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.envelope, getString(R.string.email)
                                ) {

                                    lifecycleScope.launch {
                                        ClickHandler.runWithDelay {

                                            callSendSMSEmailApi("email")

                                            firebaseLogEvent(
                                                this@TicketDetailsActivityCompose,
                                                SHARE_VIA_EMAIL,
                                                loginModelPref.userName,
                                                loginModelPref.travels_name,
                                                loginModelPref.role,
                                                SHARE_VIA_EMAIL,
                                                EmailShare.EMAIL_SHARE_TICKET_DETAILS
                                            )
                                        }
                                    }

                                })
                        }

                        if (ticketDetailsComposeViewModel.shareTicketVisibility()) {
                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.ic_share,
                                    getString(R.string.share_ticket)
                                ) {
                                    lifecycleScope.launch {
                                        ClickHandler.runWithDelay {
                                            val sendIntent: Intent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "${ticketDetailsComposeViewModel.smsTicketHash} \n \n${ticketDetailsComposeViewModel.sharingPDFLink}",
                                                )
                                                type = "text/plain"
                                            }

                                            val shareIntent = Intent.createChooser(sendIntent, null)
                                            startActivity(shareIntent)

                                            firebaseLogEvent(
                                                this@TicketDetailsActivityCompose,
                                                SHARE_ICON,
                                                loginModelPref.userName,
                                                loginModelPref.travels_name,
                                                loginModelPref.role,
                                                SHARE_ICON,
                                                ShareIcon.SHARE_ICON_TICKET_DETAILS
                                            )

                                        }
                                    }

                                })
                        }

                        if (ticketDetailsComposeViewModel.updateTicketBtnVisibility()) {
                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.ic_book_icon, getString(R.string.update_ticket)
                                ) {

                                    lifecycleScope.launch {
                                        ClickHandler.runWithDelay {
                                            ticketNumber?.trim().let { it1 ->
                                                if (it1 != null) {
                                                    editPassengerSheet.showEditPassengersSheet(it1)
                                                    editPassengerSheet.getTicketDetailsComposeViewModel(ticketDetailsComposeViewModel)
                                                    // baseUpdateCancelTicket.showEditPassengersSheet(it1)

                                                }

                                            }

                                            firebaseLogEvent(
                                                this@TicketDetailsActivityCompose,
                                                UPDATE_TICKET,
                                                loginModelPref.userName,
                                                loginModelPref.travels_name,
                                                loginModelPref.role,
                                                UPDATE_TICKET,
                                                TicketDetails.UPDATE_TICKET_TICKET_DETAILS
                                            )
                                        }
                                    }




                                })
                        }

                        if (ticketDetailsComposeViewModel.shiftTicketBtnVisibility(
                                privilegeResponseModel
                            )
                        ) {

                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.ic_shift_arrow, getString(R.string.shift_passengers)
                                ) {

                                    lifecycleScope.launch {
                                        ClickHandler.runWithDelay {
                                            val intent = Intent(
                                                this@TicketDetailsActivityCompose,
                                                ShiftPassengerActivity::class.java
                                            )
                                            intent.putExtra(
                                                "service_ticketno", ticketNumber ?: ""
                                            )
                                            PreferenceUtils.putString(
                                                "SHIFT_SeatPnrNumber", ticketNumber
                                            )
                                            PreferenceUtils.putString(
                                                "TicketDetail_SeatNumbes",
                                                ticketDetailsComposeViewModel.seatNumbers
                                            )
                                            PreferenceUtils.putString(
                                                "SHIFT_servicename",
                                                ticketDetailsComposeViewModel.serviceNumber
                                            )
                                            PreferenceUtils.putString(
                                                "SHIFT_originId", ticketDetailsComposeViewModel.originId
                                            )
                                            PreferenceUtils.putString(
                                                "SHIFT_destinationId",
                                                ticketDetailsComposeViewModel.destinationId
                                            )
                                            PreferenceUtils.putString(
                                                "oldServiceNumberShiftACTIVITY",
                                                "${ticketDetailsComposeViewModel.serviceNumber}?${ticketDetailsComposeViewModel.travelDate}"
                                            )

                                            PreferenceUtils.putString(
                                                "TicketDetail_noOfSeats",
                                                ticketDetailsComposeViewModel.noOfSeats.toString()
                                            )

                                            PreferenceUtils.setPreference(
                                                PREF_RESERVATION_ID,
                                                ticketDetailsComposeViewModel.reservationId
                                            )

                                            startActivity(intent)

                                        }
                                    }

                                })
                        }

                        if (ticketDetailsComposeViewModel.cancelTicketBtnVisibility()) {
                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.ic_cancel_shape, getString(R.string.cancel_ticket)
                                ) {
                                    lifecycleScope.launch {
                                        ClickHandler.runWithDelay {
                                            ticketNumber?.let {
                                                cancelTicketSheet.showTicketCancellationSheet(
                                                    it.trim()
                                                )
                                            }

                                        }
                                    }

                                })
                        }

                        /*if (::privilegeResponseModel.isInitialized && ticketDetailsComposeViewModel.releaseTicketBtnVisibility(privilegeResponseModel)) {
                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.released_tickets_button_new_booking_flow,
                                    getString(R.string.release_ticket)
                                ) {
                                    releaseTicket()
                                }
                            )
                        }*/

                        if (::privilegeResponseModel.isInitialized && ticketDetailsComposeViewModel.confirmTicketBtnVisibility(
                                privilegeResponseModel
                            )
                        ) {
                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.confirm_phone_booking_ticket_details_sidebar,
                                    getString(R.string.confirmPhoneBooking)
                                ) {

                                    lifecycleScope.launch {
                                        ClickHandler.runWithDelay {
                                            if (ticketDetailsComposeViewModel.isPayAtBusTicket) {
                                                DialogUtils.twoButtonDialog(
                                                    this@TicketDetailsActivityCompose,
                                                    "${getString(R.string.confirmBooking)}?",
                                                    getString(R.string.confirm_this_booking),
                                                    getString(R.string.no_dont_confirm),
                                                    getString(R.string.confirm),
                                                    object : DialogButtonListener {
                                                        override fun onLeftButtonClick() {

                                                        }

                                                        override fun onRightButtonClick() {
                                                            payAtBus()
                                                        }

                                                    }
                                                )

                                            }
                                            else {
//                                                val intent =
//                                                    Intent(this@TicketDetailsActivityCompose, ConfirmPhoneBookingActivity::class.java)
                                                val intent = if(privilegeResponseModel.country.equals("india",true) == true && !privilegeResponseModel.isAgentLogin && ticketDetailsComposeViewModel.bookingSource.equals(
                                                        getString(
                                                            R.string.branch_booking_
                                                        ),true)) {
                                                    Intent(this@TicketDetailsActivityCompose, NewConfirmPhoneBookingActivity::class.java)
                                                }else{
                                                    Intent(this@TicketDetailsActivityCompose, ConfirmPhoneBookingActivity::class.java)
                                                }
                                                intent.putExtra("fromTicketDetailsActivity", true)
                                                intent.putExtra(
                                                    getString(R.string.pnr_number),
                                                    ticketDetailsComposeViewModel.pnrNumber
                                                )
                                                intent.putExtra(
                                                    getString(R.string.select_boarding_stage),
                                                    ticketDetailsComposeViewModel.boardingStageID
                                                )
                                                intent.putExtra("reservationId",ticketDetailsComposeViewModel.reservationId)
                                                intent.putExtra(
                                                    getString(R.string.select_dropping_stage),
                                                    ticketDetailsComposeViewModel.droppingStageID
                                                )
                                                //intent.putExtra(getString(R.string.toolbar_title), ticketDetailsComposeViewModel.toolbarTitle)
                                                // intent.putExtra(getString(R.string.travel_date), getDateDMY(travelDate.toString()))
                                                intent.putExtra(
                                                    getString(R.string.travel_date),
                                                    ticketDetailsComposeViewModel.travelDate
                                                )
                                                intent.putExtra(
                                                    getString(R.string.total_net_amount),
                                                    ticketDetailsComposeViewModel.totalNetAmount
                                                )
                                                intent.putExtra(
                                                    getString(R.string.transaction_fare),
                                                    ticketDetailsComposeViewModel.transactionFare
                                                )
                                                intent.putExtra(
                                                    getString(R.string.bus_type),
                                                    ticketDetailsComposeViewModel.busType
                                                )
                                                intent.putExtra(
                                                    getString(R.string.source_id),
                                                    ticketDetailsComposeViewModel.originId
                                                )
                                                intent.putExtra(
                                                    getString(R.string.destination_id),
                                                    ticketDetailsComposeViewModel.destinationId
                                                )
                                                intent.putExtra(
                                                    getString(R.string.origin),
                                                    ticketDetailsComposeViewModel.origin
                                                )
                                                intent.putExtra(
                                                    getString(R.string.destination),
                                                    ticketDetailsComposeViewModel.destination
                                                )
                                                intent.putExtra(
                                                    getString(R.string.totalAmount),
                                                    ticketDetailsComposeViewModel.totalFare
                                                )
                                                intent.putExtra(
                                                    "seatNumbers",
                                                    ticketDetailsComposeViewModel.seatNumbers
                                                )
                                                if (ticketDetailsComposeViewModel.dataTicketDetails.value?.body?.bookingSource == "Online Agent Booking" || ticketDetailsComposeViewModel.dataTicketDetails.value?.body?.bookingSource == "Offline Agent Booking") {
                                                    intent.putExtra("isOnBehalgOfAgent", true)
                                                }
                                                startActivity(intent)

                                            }
                                        }
                                    }



                                })
                        }

                        if (it.body?.can_release_phone_block == true) {
                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.release_ticket_ticket_details_sidebar,
                                    getString(R.string.release_ticket)
                                ) {
                                    lifecycleScope.launch {
                                        ClickHandler.runWithDelay {
                                            releaseTicket()

                                        }
                                    }
                                }
                            )
                        }

                        if (it.body?.partial_payment_details != null) {
                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.pay_pending_amount_ticket_details_sidebar,
                                    getString(R.string.pay_pending_amount)
                                ) {

                                    lifecycleScope.launch {
                                        ClickHandler.runWithDelay {
                                            val paymentOptionsList = setPaymentOptions()

                                            DialogUtils.dialogPartialPaidNew(
                                                context = this@TicketDetailsActivityCompose,
                                                passengerName = ticketDetailsComposeViewModel.passengerName,
                                                passengerAge = ticketDetailsComposeViewModel.passengerAge,
                                                passengerGender = ticketDetailsComposeViewModel.passengerGender,
                                                passengerMobile = ticketDetailsComposeViewModel.passengerMobileNumber,
                                                ticketNumber = ticketNumber,
                                                seatNumbers = ticketDetailsComposeViewModel.seatNumbers,
                                                boardingStage = ticketDetailsComposeViewModel.boardingDetails.value.stageName,
                                                dropOffStage = ticketDetailsComposeViewModel.dropOffDetails.value.stageName,
                                                origin = ticketDetailsComposeViewModel.origin,
                                                destination = ticketDetailsComposeViewModel.destination,
                                                partialPaymentDetails = it.body.partial_payment_details,
                                                paymentOptionsList = paymentOptionsList,
                                                currencySymbol = ticketDetailsComposeViewModel.currency,
                                                currencyFormat = ticketDetailsComposeViewModel.currencyFormat,
                                                varArgListener = object : VarArgListener {
                                                    override fun onButtonClick(vararg args: Any) {
                                                        if (args.isNotEmpty()) {
                                                            progressDialog =
                                                                ProgressDialog(this@TicketDetailsActivityCompose)
                                                            when (args[0]) {
                                                                PARTIAL_RELEASE_BTN -> {
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
                                                                    val paymentPosition: Int =
                                                                        args[1].toString().toDouble()
                                                                            .toInt()
                                                                    selectedPartialPaymentOption =
                                                                        paymentOptionsList[paymentPosition].id.toString()
                                                                    if (selectedPartialPaymentOption == "2")
                                                                        DialogUtils.creditDebitDialog(
                                                                            this@TicketDetailsActivityCompose,
                                                                            this
                                                                        )
                                                                }

                                                                getString(R.string.credit_debit) -> {
                                                                    creditDebitCardNo = args[1].toString()
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }


                                }
                            )
                        }

                        if (it.body?.is_update_luggage_post_confirmation == true && privilegeResponseModel?.country?.equals("Indonesia", true) == true) {
                            ticketDetailsComposeViewModel.sideBarOptionsList.add(
                                TicketDetailsSideBarOptionsModel(
                                    R.drawable.ic_luggage,
                                    getString(R.string.update_luggage)
                                ) {
                                    lifecycleScope.launch {
                                        ClickHandler.runWithDelay {
                                            if (isNetworkAvailable()) {
                                                pickUpChartViewModel.fetchLuggageDetailsIntlApi(
                                                    apiKey = loginModelPref?.api_key ?: "",
                                                    pnrNumber = ticketNumber.toString()
                                                )
                                            } else {
                                                noNetworkToast()
                                            }
                                        }
                                    }

                                }
                            )
                        }

                    }

                    401 -> {
                        //openUnauthorisedDialog()
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
                        if (it.message.isNotEmpty() == true) {
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
                    }
                }
            } else {
                toast(getString(R.string.server_error))
                onBackPressed()
            }
        }
    }

    @Composable
    private fun TicketDetailsSideBar() {

        if (ticketDetailsComposeViewModel.showTicketDetailsSideBarMenu && ticketDetailsComposeViewModel.sideBarOptionsList.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(colorResource(id = R.color.transparent_tint_color)),
            ) {
                Spacer(modifier = Modifier
                    .weight(0.25f)
                    .fillMaxHeight()
                    .clickable {
                        ticketDetailsComposeViewModel.showTicketDetailsSideBarMenu = false
                    })
                LazyColumn(
                    modifier = Modifier
                        .weight(0.75f)
                        .fillMaxHeight()
                        .background(Color.White)
                ) {
                    items(items = ticketDetailsComposeViewModel.sideBarOptionsList) {
                        TicketDetailsSideBarItem(it)
                    }
                }
            }
        }
    }

    @Composable
    private fun TicketDetailsSideBarItem(ticketDetailsSideBarOptionsModel: TicketDetailsSideBarOptionsModel) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    .clickable {
                        ticketDetailsSideBarOptionsModel.onClick.invoke()


                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(18.dp)
                        .width(18.dp),
                    painter = painterResource(id = ticketDetailsSideBarOptionsModel.iconId),
                    contentDescription = ticketDetailsSideBarOptionsModel.name,
                    colorFilter = ColorFilter.tint(
                        color = if (ticketDetailsSideBarOptionsModel.name.equals(
                                getString(R.string.cancel_ticket),
                                true
                            )
                        ) {
                            colorResource(id = R.color.colorRed2)
                        } else colorResource(id = R.color.colorBlackShadow)
                    )
                )

                TextNormalRegular(
                    text = ticketDetailsSideBarOptionsModel.name, modifier = Modifier
                )

            }

            Divider(color = colorResource(id = R.color.view_color), thickness = 1.dp)

        }
    }

    private fun updateLuggageDetailsApi(pnrNumber: String?, luggageOptionDetail: String?) {
        pickUpChartViewModel.updateLuggageOptionIntlApi(
            reqBody = com.bitla.ts.domain.pojo.luggage_details.request.ReqBody(
                apiKey = loginModelPref.api_key,
                locale = locale,
                pnrNumber = pnrNumber,
                luggageDetail = luggageOptionDetail,
            )
        )
    }

    private fun fetchLuggageDetailsObserver() {
        pickUpChartViewModel.fetchLuggageDetailsResponse.observe(
            this@TicketDetailsActivityCompose
        ) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        dialogUpdateLuggageDetailsIntl(it.luggageDesc)
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        toast(getString(R.string.luggageNotFound, ticketNumber))
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun dialogUpdateLuggageDetailsIntl(luggageDesc: String) {
        DialogUtils.dialogUpdateLuggageIntl(
            context = this,
            pnrNumber = ticketNumber.toString(),
            luggageDesc = luggageDesc,
            singleButtonListener = object : DialogSingleButtonListener {
                override fun onSingleButtonClick(str: String) {
                    if (str == getString(R.string.luggage_option)) {
                        val original = PreferenceUtils.getString("luggageOptionData")?.trim().orEmpty()
                        val luggageOptionDetail = original.substringBeforeLast(" ")
                        val pnrNumber = PreferenceUtils.getString("luggageOptionData")
                            ?.split(" ")?.lastOrNull()?.trim()
                        if (!pnrNumber.isNullOrEmpty()) {
                            if (isNetworkAvailable()) {
                                updateLuggageDetailsApi(
                                    pnrNumber,
                                    luggageOptionDetail
                                )
                            } else {
                                noNetworkToast()
                            }
                        } else {
                            toast(getString(R.string.pnr_number_not_found))
                        }
                    }
                }
            }
        )
    }

    private fun updateLuggageDetailsObserver() {
        pickUpChartViewModel.updateLuggageOption.observe(this) {
            if (it != null) {
                if (it.code == 200) {
                    toast(it.message)
                } else {
                    toast(it.message)
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun viewPassengers() {
        firebaseLogEvent(
            this,
            VIEW_PASSENGER,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            VIEW_PASSENGER,
            ViewPassanger.VIEW_PASSANGER
        )
        ticketNumber?.let {

           // baseUpdateCancelTicket.showViewPassengersSheet(it.trim())
            viewPassengerSheet.showViewPassengersSheet(it.trim())

        }
    }

    private fun callFunction(phoneNumber: String) {
        var countryList = ArrayList<Int>()

        try {
            if (getCountryCodes().isNotEmpty()) countryList = getCountryCodes()
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
        if (ContextCompat.checkSelfPermission(
                this@TicketDetailsActivityCompose, Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this@TicketDetailsActivityCompose, arrayOf(Manifest.permission.CALL_PHONE), 200
            )
        } else {
            val telNo =
                getPhoneNumber(passPhone = phoneNumber, ticketDetailsComposeViewModel.country)
            if (countryList.isNotEmpty()) {
                val finalTelNo = if (phoneNumber.contains("+")) {
                    "+$telNo"
                } else {
                    "+${countryList[0]}$telNo"
                }
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$finalTelNo"))
                startActivity(intent)
            }
        }
    }

    private fun callBoardingDetailFunction(phoneNumber: String) {
        var countryList = ArrayList<Int>()

        try {
            if (getCountryCodes().isNotEmpty()) countryList = getCountryCodes()
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
        if (ContextCompat.checkSelfPermission(
                this@TicketDetailsActivityCompose, Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this@TicketDetailsActivityCompose, arrayOf(Manifest.permission.CALL_PHONE), 200
            )
        } else {
            val telNo =
                getPhoneNumber(passPhone = phoneNumber, ticketDetailsComposeViewModel.country)
            if (countryList.isNotEmpty()) {
                val finalTelNo = "+${countryList[0]}$telNo"
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$finalTelNo"))
                startActivity(intent)
            }
        }
    }

    private fun shareToWhatsapp() {
        lateinit var _sheetWhatsapp: WhatsappBottomSheetBinding
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
        } else if (whatsappFoundBusiness) {
            whatsappIntent("com.whatsapp.w4b")
        } else if (whatsappFound) {
            whatsappIntent("com.whatsapp")
        } else {
            toast("Whatsapp is not Installed.")
        }

    }

    private fun whatsappIntent(packageName: String) {
        try {

            var countryList = ArrayList<Int>()
            if (getCountryCodes().isNotEmpty()) countryList = getCountryCodes()

            val i = Intent(Intent.ACTION_VIEW)

            val finalMobileNumber = getPhoneNumber(
                passPhone = ticketDetailsComposeViewModel.passengerMobileNumber,
                ticketDetailsComposeViewModel.country
            )

            if (finalMobileNumber.isNotEmpty()) {
                val url = if (ticketDetailsComposeViewModel.passengerMobileNumber.contains("+")) {
                    "https://api.whatsapp.com/send?phone=" + "+${ticketDetailsComposeViewModel.ticketDetailsPassMobNumber}" + "&text=" + URLEncoder.encode(
                        "${ticketDetailsComposeViewModel.smsTicketHash} \n \n${ticketDetailsComposeViewModel.sharingPDFLink}",
                        "UTF-8"
                    )
                } else {
                    "https://api.whatsapp.com/send?phone=" + "+${countryList[0]}" + finalMobileNumber + "&text=" + URLEncoder.encode(
                        "${ticketDetailsComposeViewModel.smsTicketHash} \n \n${ticketDetailsComposeViewModel.sharingPDFLink}",
                        "UTF-8"
                    )
                }
                i.setPackage(packageName)
                i.data = Uri.parse(url)
                //if (i.resolveActivity(packageManager) != null) {
                startActivity(i)
                //}
            } else toast(getString(R.string.number_not_registered))
        } catch (ex: ActivityNotFoundException) {
            toast("Whatsapp is not Installed.")
        } catch (e: java.lang.Exception) {
            Timber.d("ExceptionMsg ${e.printStackTrace()}")
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

    private fun bookTicketInSameService() {
        PreferenceUtils.setPreference(PREF_UPDATE_COACH, true)

        val intent = Intent(this, NewCoachActivity::class.java)
        intent.putExtra(REDIRECT_FROM, TicketDetailsActivity.tag)
        intent.putExtra("fromTicketDetails", "rebooking")
        PreferenceUtils.putString("SelectionCoach", "BOOK")
        PreferenceUtils.putString(
            getString(R.string.rebooking_same_service),
            getString(R.string.book_ticket_in_same_service)
        )
        PreferenceUtils.putString("fromBusDetails", "bookBlock")
        PreferenceUtils.removeKey("seatwiseFare")
        PreferenceUtils.removeKey("isEditSeatWise")
        PreferenceUtils.removeKey("PERSEAT")

        retrieveSelectedSeats().clear()
        com.bitla.ts.utils.common.seatDetailList.clear()
        setSelectSeats(arrayListOf())
        setPassengerDetails(mutableListOf())
        com.bitla.ts.utils.common.passengerList.clear()
        setSelectedPassengers(arrayListOf())
        retrieveSelectedPassengers().clear()
        startActivity(intent)
        finish()

    }

    private fun printTicket() {
        generateQrcode()

        if (!originalTemplate.isNullOrEmpty()) bluetoothPrintTemplate = originalTemplate

        Timber.d("originalTemplate $originalTemplate")

        if (bluetoothPrintTemplate != null && bluetoothPrintTemplate!!.isNotEmpty()) {
            if (bluetoothPrintTemplate?.contains("00000")!! && hexaDecimalString != null) {
                return
                //bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("<img>$hexaDecimalString<img>","IMAGE")
            }

            if (!operatorLogo.isNullOrEmpty()) {
                getBitmapFromURL(operatorLogo!!, getString(R.string.logo))
            }

            if (!privilegeResponseModel.tsPrivileges?.qoalaImageV1.isNullOrEmpty()) {
                getBitmapFromURL(
                    privilegeResponseModel.tsPrivileges?.qoalaImageV1 ?: "",
                    getString(R.string.insurance_bitmap)
                )
            }

            if (IS_PINELAB_DEVICE) {
                if (bluetoothPrintTemplate!!.contains("FOR_EACH_SEAT")) {
                    multiplePrintPinelab()
                } else {
                    pineLabPrint()
                }
            } else if (privilegeResponseModel?.isEzetapEnabledInTsApp!!) {
//                commonReplacementPrint()
               /* if (bluetoothPrintTemplate!!.contains("FOR_EACH_SEAT")) {
                    printEzetapTicket()
                } else {
                    singleSeatEzetapPrint()
                }*/

            } else if (privilegeResponseModel.isPaytmPosEnabled == true) {
                if (originalTemplate!!.contains("FOR_EACH_SEAT")) {
                    paytmPrint(false)
                } else {
                    paytmPrint(true)
                }
            } else {
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
                    callUpdatePrintCountApi()
                }
            }


        } else toast(getString(R.string.template_not_configured))
    }

    private fun getBitmapFromURL(image: String, imageType: String) {
        if (!isValidUrl(image)) return

        lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = getBitmapDirectFromUrl(image)

            withContext(Dispatchers.Main) {
                when (imageType) {
                    getString(R.string.logo) -> bmpLogo = bitmap
                    getString(R.string.qr_code) -> bmpQrCode = bitmap
                    getString(R.string.insurance_bitmap) -> insuranceBitmap = bitmap
                }
            }
        }
    }

    private suspend fun getBitmap(image: String): Bitmap? = withContext(Dispatchers.IO) {
        getBitmapDirectFromUrl(image)
    }

    private fun getBitmapDirectFromUrl(image: String): Bitmap? {
        var image1: Bitmap? = null
        try {
            CoroutineScope(Dispatchers.IO).run {
                val url = URL(image)
                image1 = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
        return image1
    }

    //    URL is valid and reachable to avoid unnecessary errors and exceptions.
    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: MalformedURLException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            false
        }
    }

    private fun multiplePrintPinelab() {
        var template = bluetoothPrintTemplate!!

        try {
            if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel?.passengerDetails?.isNotEmpty()!!) {
                printArray = JSONArray()

                val multiSeats = mutableListOf<String>()
                Timber.e("Passenger size : ${ticketDetailsComposeViewModel.passengerDetails!!.size}")
                for (i in 0..ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)!!) {
                    if (ticketDetailsComposeViewModel.passengerDetails?.size!! != 1) {
                        if (i < ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)!!) {
                            template = template?.replace("[C]=", "")!!
                            template = template?.replace("cut here", "")?.trimEnd()!!
                            template = template?.replace("BOARDING_QR", "")?.trim()!!
                            template = template?.replace("BAR_CODE", "")?.trim()!!

                        } else {
                            template = template?.replace("[C]=", "")!!
                            template = template?.replace("=", "")?.trimEnd()!!

                            if (originalTemplate?.contains("BOARDING_QR")!!) template =
                                "${template}\nBOARDING_QR"
                            if (originalTemplate?.contains("BAR_CODE")!!) template =
                                "${template}\n\nBAR_CODE"
                            template = "${template}\nALIGN_CENTER|=======cut here======="
                        }
                    }

                    template = template?.replace("FOR_EACH_SEAT", "")!!
                    if (template?.contains("SEAT_EACH_NUMBERS")!!) {
                        template = template?.replace(
                            "SEAT_EACH_NUMBERS",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.seatNumber ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.seatNumber?.let {
                                    template?.replace(
                                        it,
                                        ticketDetailsComposeViewModel?.passengerDetails!![i]?.seatNumber
                                            ?: ""
                                    )
                                }!!
                        }
                    }
                    if (template?.contains("PASSENGER_EACH_NAME") == true) {
                        template = template?.replace(
                            "PASSENGER_EACH_NAME",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.name ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.name?.let {
                                    template?.replace(
                                        it,
                                        ticketDetailsComposeViewModel?.passengerDetails!![i]?.name
                                            ?: ""
                                    )
                                }!!
                        }
                    }


                    if (template?.contains("TICKET_EACH_FARE")!!) {
                        template = template?.replace(
                            "TICKET_EACH_FARE",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.netFare?.toDouble()
                                ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.netFare.let {
                                    template.replace(
                                        it.toString(),
                                        ticketDetailsComposeViewModel.passengerDetails[i]?.netFare?.convert(
                                            privilegeResponseModel.currencyFormat
                                        ) ?: ""
                                    )
                                }
                        }
                    }


                    if (!ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.isNullOrEmpty()) {
                        if (template?.contains("MEAL_COUPON_LOOP")!!) {

                            template = template?.replace(
                                "MEAL_COUPON_LOOP",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "").replace(",", "\n")
                                    .replace(" ", "")
                            )!!
                        } else {
                            if (i > 0) {
                                template =
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons?.toString()
                                        ?.let {
                                            template?.replace(
                                                it,
                                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                            )
                                        }!!
                            }
                        }

                        if (template?.contains("MEAL_COUPON_NUMBER")!!) {
                            template = template?.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "")
                            )!!
                        } else {
                            if (i > 0) {
                                template = template?.replace(
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons.toString()
                                        .replace("[", "").replace("]", ""),
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                        .replace("[", "").replace("]", "")
                                )!!
                            }
                        }

                        if (template?.contains("MEAL_COUNT")!!) {
                            template = template?.replace(
                                "MEAL_COUNT",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.size.toString()
                            )!!
                        } else {
                            if (i > 0) {
                                template = template?.replace(
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons?.size.toString(),
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.size.toString()
                                )!!
                            }
                        }
                    } else {
                        template = template?.replace(
                            "MEAL_COUPON_LOOP", ""
                        )!!
                    }




                    template = ticketDetailsComposeViewModel.serviceNumber?.let {
                        template?.replace(
                            "SERVICE_NUMBER", it
                        )
                    }!!

                    template = template?.replace(
                        "PAID_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    ) ?: "-"
                    template = template.replace(
                        "REMAINING_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    ) ?: "-"

                    if (template.contains("ORIGIN")) {
                        template = template.replace("ORIGIN", ticketDetailsComposeViewModel.origin)
                    }
                    if (template.contains("DESTINATION")) {
                        template = template.replace(
                            "DESTINATION", ticketDetailsComposeViewModel.destination
                        )
                    }
                    if (template.contains("WEB_ADDRESS")) {
                        template = template.replace(
                            "WEB_ADDRESS", privilegeResponseModel.webAddressUrl
                        )
                    }


                    if (template.contains("TICKET_FARE")) {
                        template = template.replace(
                            "TICKET_FARE", "${privilegeResponseModel.currency} ${
                                ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                                    ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                            }"
                        )
                    }



                    template = ticketDetailsComposeViewModel.boardingDetails.value.landmark?.let {
                        template?.replace(
                            "LANDMARK", it
                        )
                    } ?: "-"

                    template = template?.replace(
                        "OPERATOR_NAME", privilegeResponseModel.operatorName
                    )!!

                    template = template?.replace(
                        "WEB_ADDRESS", privilegeResponseModel.webAddressUrl
                    )!!

                    template = template?.replace(
                        "ACCOUNT_HOLDER_NAME", loginModelPref.userName
                    )!!
                    template = ticketNumber?.let {
                        template?.replace(
                            "PNR_NUMBER", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.ticketStatus?.let {
                        template?.replace(
                            "TICKET_STATUS", it
                        )
                    } ?: "-"
                    template = template?.replace("ORIGIN", ticketDetailsComposeViewModel.origin)!!
                    template = ticketDetailsComposeViewModel.destination?.let {
                        template?.replace(
                            "DESTINATION", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.boardingDetails.value.depTime?.let {
                        template?.replace(
                            "DEPARTURE_TIME", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.travelDate?.let {
                        template?.replace(
                            "TRAVEL_DATE", "$it"
                        )
                    }!!
                    template = template?.replace(
                        "TICKET_FARE", "${privilegeResponseModel.currency} ${
                            ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                                ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                        }"
                    )!!
                    template = ticketDetailsComposeViewModel.passengerDetails?.get(i)?.name?.let {
                        template?.replace(
                            "PASSENGER_NAME", it
                        )
                    } ?: ""
                    template = ticketDetailsComposeViewModel.passengerDetails?.get(i)?.let {
                        it.mobile?.let { it1 ->
                            template?.replace(
                                "MOBILE_NUMBER", it1
                            )
                        }
                    } ?: ""
                    ticketDetailsComposeViewModel.seatNumbers?.toString()
                        ?.let { Log.d("seatNumber", it) }
                    template = ticketDetailsComposeViewModel.seatNumbers?.let {
                        template?.replace(
                            "SEAT_NUMBERS", it
                        )
                    }!!


                    template = ticketDetailsComposeViewModel.boardingDetails.value.address?.let {
                        template?.replace(
                            "BOARDING_POINT", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.dropOffDetails.value.address?.let {
                        template?.replace(
                            "DROPPING_POINT", it
                        )
                    }!!
                    template =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
                            template?.replace(
                                "CONTACT_PERSON", it
                            )
                        }!!

                    template =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactNumbers?.let {
                            template?.replace(
                                "CONTACT_NUMBER_PERSON", it
                            )
                        }!!

                    /*template = ticketDetailsComposeViewModel.ticketLeadDetail?.ticketBookedBy?.let {
                        template?.replace(
                            "TICKET_BOOKED_BY",
                            it.substringBefore(",")
                        )
                    }!!*/

                    template = ticketDetailsComposeViewModel.busType?.let {
                        template?.replace(
                            "COACH_TYPE", it
                        )
                    }!!
                    template =
                        template?.replace("REMARKS", ticketDetailsComposeViewModel.remarks ?: "-")!!
                    template = template?.replace(
                        "TERMINAL_ID", ticketDetailsComposeViewModel.terminalRefNo ?: ""
                    )!!
                    if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
                        template = template?.replace("TERMINAL_PULOGABANG", "")!!
                    }
                    template = template?.replace("CURRENT_DATE", getTodayDate())!!
                    template = template?.replace("CURRENT_TIME", getTodayDateWithTime())!!

                    template = template?.replace("TAB_SPACE", " ")!!

                    template = template?.replace("BAR_CODE", " ")!!

                    template = template?.replace("BOLD_ON", " ")!!
                    template = template?.replace("BOLD_OFF", " ")!!

                    template = "\n$template\n"
                    template?.let { multiSeats.add(it) }

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
                            if (hexvalue!!.isNotEmpty()) {
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
                detailObj.put("PrintRefNo", ticketNumber!!)
                detailObj.put("SavePrintData", true)
                detailObj.put("Data", printArray)

                val json = JSONObject()
                json.put("Header", headerObj)
                json.put("Detail", detailObj)

                val data = Bundle()
                data.putString(BILLING_REQUEST_TAG, json.toString())
                message.setData(data);
                try {
                    message.replyTo = Messenger(IncomingHandler(this@TicketDetailsActivityCompose))
                    mServerMessenger!!.send(message)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }


    }

    /*var printerInterface: EPrinterInterface? = null
    private fun printEzetapTicket() {

        var template = bluetoothPrintTemplate!!

        try {
            if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel?.passengerDetails?.isNotEmpty()!!) {
                printArray = JSONArray()

                val multiSeats = mutableListOf<String>()
                Timber.e("Passenger size : ${ticketDetailsComposeViewModel.passengerDetails!!.size}")
                for (i in 0..ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)!!) {
                    if (ticketDetailsComposeViewModel.passengerDetails?.size!! != 1) {
                        if (i < ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)!!) {
                            template = template?.replace("[C]=", "")!!
                            template =
                                template?.replace("cut here", "")?.trimEnd()!!
                            template =
                                template?.replace("BOARDING_QR", "")?.trim()!!
                            template =
                                template?.replace("BAR_CODE", "")?.trim()!!

                        } else {
                            template = template?.replace("[C]=", "")!!
                            template =
                                template?.replace("=", "")?.trimEnd()!!

                            if (originalTemplate?.contains("BOARDING_QR")!!)
                                template = "${template}\nBOARDING_QR"
                            if (originalTemplate?.contains("BAR_CODE")!!)
                                template = "${template}\n\nBAR_CODE"
                            template =
                                "${template}"
                        }
                    }

                    template =
                        template?.replace("FOR_EACH_SEAT", "")!!
                    if (template?.contains("SEAT_EACH_NUMBERS")!!) {
                        template = template?.replace(
                            "SEAT_EACH_NUMBERS",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.seatNumber ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.seatNumber?.let {
                                    template?.replace(
                                        it,
                                        ticketDetailsComposeViewModel?.passengerDetails!![i]?.seatNumber
                                            ?: ""
                                    )
                                }!!
                        }
                    }
                    if (template?.contains("PASSENGER_EACH_NAME")!!) {
                        template = template?.replace(
                            "PASSENGER_EACH_NAME",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.name ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.name?.let {
                                    template?.replace(
                                        it,
                                        ticketDetailsComposeViewModel?.passengerDetails!![i]?.name
                                            ?: ""
                                    )
                                }!!
                        }
                    }


                    if (template?.contains("TICKET_EACH_FARE")!!) {
                        template = template?.replace(
                            "TICKET_EACH_FARE",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.netFare?.toDouble()
                                ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.netFare.toString()
                                    .let {
                                        template.replace(
                                            it,
                                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.netFare?.convert(
                                                privilegeResponseModel.currencyFormat
                                            ) ?: ""
                                        )
                                    }!!
                        }
                    }


                    if (!ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.isNullOrEmpty()) {
                        if (template?.contains("MEAL_COUPON_LOOP")!!) {

                            template = template?.replace(
                                "MEAL_COUPON_LOOP",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "")
                                    .replace("]", "").replace(",", "\n").replace(" ", "")
                            )!!
                        } else {
                            if (i > 0) {
                                template =
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.toString()
                                        ?.let {
                                            template?.replace(
                                                it,
                                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                            )
                                        }!!
                            }
                        }

                        if (template?.contains("MEAL_COUPON_NUMBER")!!) {
                            template = template?.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "")
                            )!!
                        } else {
                            if (i > 0) {
                                template = template?.replace(
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                        .replace("[", "").replace("]", ""),
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                        .replace("[", "").replace("]", "")
                                )!!
                            }
                        }

                        if (template?.contains("MEAL_COUNT")!!) {
                            template = template?.replace(
                                "MEAL_COUNT",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.size.toString()
                            )!!
                        } else {
                            if (i > 0) {
                                template = template?.replace(
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons?.size.toString(),
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.size.toString()
                                )!!
                            }
                        }
                    } else {
                        template = template?.replace(
                            "MEAL_COUPON_LOOP", ""
                        )!!
                    }




                    template =
                        ticketDetailsComposeViewModel.serviceNumber?.let {
                            template?.replace(
                                "SERVICE_NUMBER",
                                it
                            )
                        }!!

                    template = template?.replace(
                        "PAID_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails?.value?.paidAmount.toString()
                    ) ?: "-"
                    template = template?.replace(
                        "REMAINING_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails?.value?.remainingAmount.toString()
                    ) ?: "-"

                    if (template.contains("ORIGIN")) {
                        template = template.replace("ORIGIN", ticketDetailsComposeViewModel.origin)
                    }
                    if (template.contains("DESTINATION")) {
                        template = template.replace(
                            "DESTINATION",
                            ticketDetailsComposeViewModel.destination!!
                        )
                    }
                    if (template.contains("WEB_ADDRESS")) {
                        template = template?.replace(
                            "WEB_ADDRESS",
                            privilegeResponseModel.webAddressUrl
                        )!!
                    }


                    if (template.contains("TICKET_FARE")) {
                        template = template?.replace(
                            "TICKET_FARE",
                            "${privilegeResponseModel.currency} ${
                                ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                                    ?.convert(ticketDetailsComposeViewModel.currencyFormat) ?: ""
                            }"
                        )!!

                    }



                    template = ticketDetailsComposeViewModel.boardingDetails?.value?.landmark?.let {
                        template?.replace(
                            "LANDMARK",
                            it
                        )
                    } ?: "-"

                    template = template?.replace(
                        "OPERATOR_NAME",
                        privilegeResponseModel.operatorName
                    )!!

                    template = template?.replace(
                        "WEB_ADDRESS",
                        privilegeResponseModel.webAddressUrl
                    )!!

                    template = template?.replace(
                        "ACCOUNT_HOLDER_NAME",
                        loginModelPref.userName
                    )!!
                    template =
                        ticketNumber?.let {
                            template?.replace(
                                "PNR_NUMBER",
                                it
                            )
                        }!!
                    template =
                        ticketDetailsComposeViewModel.ticketStatus?.let {
                            template?.replace(
                                "TICKET_STATUS",
                                it
                            )
                        } ?: "-"
                    template =
                        template?.replace("ORIGIN", ticketDetailsComposeViewModel.origin)!!
                    template =
                        ticketDetailsComposeViewModel.destination?.let {
                            template?.replace(
                                "DESTINATION",
                                it
                            )
                        }!!
                    template = ticketDetailsComposeViewModel.boardingDetails?.value?.depTime?.let {
                        template?.replace(
                            "DEPARTURE_TIME",
                            it
                        )
                    }!!
                    template =
                        ticketDetailsComposeViewModel.travelDate?.let {
                            template?.replace(
                                "TRAVEL_DATE",
                                "$it"
                            )
                        }!!
//                    template = template?.replace(
//                        "TICKET_FARE",
//                        "${privilegeResponseModel.currency} ${
//                            ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
//                                ?.convert(privilegeResponseModel.currencyFormat) ?: ""
//                        }"
//                    )!!
                    template = ticketDetailsComposeViewModel.passengerDetails?.get(i)?.name?.let {
                        template?.replace(
                            "PASSENGER_NAME",
                            it
                        )
                    } ?: ""
                    template = ticketDetailsComposeViewModel.passengerDetails?.get(i)
                        ?.let {
                            it.mobile?.let { it1 ->
                                template?.replace(
                                    "MOBILE_NUMBER",
                                    it1
                                )
                            }
                        } ?: ""
                    template =
                        ticketDetailsComposeViewModel.seatNumbers?.let {
                            template?.replace(
                                "SEAT_NUMBERS",
                                it
                            )
                        }!!


                    template = ticketDetailsComposeViewModel.boardingDetails?.value?.address?.let {
                        template?.replace(
                            "BOARDING_POINT",
                            it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.dropOffDetails?.value?.address?.let {
                        template?.replace(
                            "DROPPING_POINT",
                            it
                        )
                    }!!
                    template =
                        ticketDetailsComposeViewModel.boardingDetails?.value?.contactPersons?.let {
                            template?.replace(
                                "CONTACT_PERSON",
                                it
                            )
                        }!!

                    template =
                        ticketDetailsComposeViewModel.boardingDetails?.value?.contactNumbers?.let {
                            template?.replace(
                                "CONTACT_NUMBER_PERSON",
                                it
                            )
                        }!!

                    template = ticketDetailsComposeViewModel.ticketBookedBy.let {
                        template?.replace(
                            "TICKET_BOOKED_BY",
                            it.substringBefore(",")
                        )
                    }!!

                    template =
                        ticketDetailsComposeViewModel.busType?.let {
                            template?.replace(
                                "COACH_TYPE",
                                it
                            )
                        }!!
                    template =
                        template?.replace("REMARKS", ticketDetailsComposeViewModel.remarks ?: "-")!!
                    template = template?.replace(
                        "TERMINAL_ID",
                        ticketDetailsComposeViewModel.terminalRefNo ?: ""
                    )!!
                    if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
                        template =
                            template?.replace("TERMINAL_PULOGABANG", "")!!
                    }
                    template =
                        template?.replace("CURRENT_DATE", getTodayDate())!!
                    template =
                        template?.replace("CURRENT_TIME", getTodayDateWithTime())!!

                    template = template?.replace("TAB_SPACE", " ")!!

                    template = template?.replace("BAR_CODE", " ")!!

                    template = template?.replace("BOLD_ON", "")!!
                    template = template?.replace("BOLD_OFF", "")!!
                    template = template?.replace("ALIGN_LEFT|", "")!!
                    template = template?.replace("ALIGN_CENTER|", "")!!
                    template = template?.replace("NEW_LINE", "\n")!!
                    template = template?.replace("ONE_SPACE", "")!!


                    template = "\n$template\n"
                    template?.let { multiSeats.add(it) }

                    Timber.e("Template : $template")




                    printerInterface = EPrinterImplementation.getInstance()
                    printerInterface!!.init(this)
                    if (printerInterface!!.isPrinterSupported()) {
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
            }
        } catch (e: Exception) {

        }
    }*/


    val callback = object : PrintStatusCallBack {
        override fun onFailure(id: String, status: PrintStatusEnum) {

        }

        override fun onSuccess(id: String) {

        }
    }


    private fun paytmPrint(singlePrint: Boolean) {
        var page = Page()
        if (singlePrint) {
            page = getDemoPage(this)
        } else {
            page = getPageForMultiplePassenger()
        }
        Printer.generateBitmapAsync(page, object : BitmapReceiverCallback {
            override fun onFailure(message: String) {

            }

            override fun onSuccess(image: Bitmap) {
                if (image != null)
                    Printer.print(
                        image!!,
                        "temp" + getRandomString(5),
                        this@TicketDetailsActivityCompose,
                        callback
                    )
                else
                    toast("Bitmap is Null")
            }
        })
    }

    private fun getPageForMultiplePassenger(): Page {

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y_H_M)
        val currentDate = dateFormat.format(calendar.time);

        val page = Page()

        try {


            var template = originalTemplate!!
            if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel?.passengerDetails?.isNotEmpty()!!) {
                printArray = JSONArray()

                val multiSeats = mutableListOf<String>()
                Timber.e("Passenger size : ${ticketDetailsComposeViewModel.passengerDetails!!.size}")
                for (i in 0..ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)!!) {
                    if (ticketDetailsComposeViewModel.passengerDetails?.size!! != 1) {
                        if (i < ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)!!) {
                            template = template?.replace("[C]=", "")!!
                            template = template?.replace("cut here", "")?.trimEnd()!!
                            template = template?.replace("BOARDING_QR", "")?.trim()!!
                            template = template?.replace("BAR_CODE", "")?.trim()!!

                        } else {
                            template = template?.replace("[C]=", "")!!
                            template = template?.replace("=", "")?.trimEnd()!!

                            if (originalTemplate?.contains("BOARDING_QR")!!) template =
                                "${template}\nBOARDING_QR"
                            if (originalTemplate?.contains("BAR_CODE")!!) template =
                                "${template}\n\nBAR_CODE"
                            template = "${template}\nALIGN_CENTER|=======cut here======="
                        }
                    }

                    template = template?.replace("FOR_EACH_SEAT", "")!!
                    if (template?.contains("SEAT_EACH_NUMBERS")!!) {
                        template = template?.replace(
                            "SEAT_EACH_NUMBERS",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.seatNumber ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.seatNumber?.let {
                                    template?.replace(
                                        it,
                                        ticketDetailsComposeViewModel?.passengerDetails!![i]?.seatNumber
                                            ?: ""
                                    )
                                }!!
                        }
                    }
                    if (template?.contains("PASSENGER_EACH_NAME") == true) {
                        template = template?.replace(
                            "PASSENGER_EACH_NAME",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.name ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.name?.let {
                                    template?.replace(
                                        it,
                                        ticketDetailsComposeViewModel?.passengerDetails!![i]?.name
                                            ?: ""
                                    )
                                }!!
                        }
                    }


                    if (template?.contains("TICKET_EACH_FARE")!!) {
                        template = template?.replace(
                            "TICKET_EACH_FARE",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.netFare?.toDouble()
                                ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.netFare.let {
                                    template.replace(
                                        it.toString(),
                                        ticketDetailsComposeViewModel.passengerDetails[i]?.netFare?.convert(
                                            privilegeResponseModel.currencyFormat
                                        ) ?: ""
                                    )
                                }
                        }
                    }


                    if (!ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.isNullOrEmpty()) {
                        if (template?.contains("MEAL_COUPON_LOOP")!!) {

                            template = template?.replace(
                                "MEAL_COUPON_LOOP",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "").replace(",", "\n")
                                    .replace(" ", "")
                            )!!
                        } else {
                            if (i > 0) {
                                template =
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons?.toString()
                                        ?.let {
                                            template?.replace(
                                                it,
                                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                            )
                                        }!!
                            }
                        }

                        if (template?.contains("MEAL_COUPON_NUMBER")!!) {
                            template = template?.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "")
                            )!!
                        } else {
                            if (i > 0) {
                                template = template?.replace(
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons.toString()
                                        .replace("[", "").replace("]", ""),
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                        .replace("[", "").replace("]", "")
                                )!!
                            }
                        }

                        if (template?.contains("MEAL_COUNT")!!) {
                            template = template?.replace(
                                "MEAL_COUNT",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.size.toString()
                            )!!
                        } else {
                            if (i > 0) {
                                template = template?.replace(
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons?.size.toString(),
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.size.toString()
                                )!!
                            }
                        }
                    } else {
                        template = template?.replace(
                            "MEAL_COUPON_LOOP", ""
                        )!!
                    }




                    template = ticketDetailsComposeViewModel.serviceNumber?.let {
                        template?.replace(
                            "SERVICE_NUMBER", it
                        )
                    }!!

                    template = template?.replace(
                        "PAID_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    ) ?: "-"
                    template = template.replace(
                        "REMAINING_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    ) ?: "-"

                    if (template.contains("ORIGIN")) {
                        template = template.replace("ORIGIN", ticketDetailsComposeViewModel.origin)
                    }
                    if (template.contains("DESTINATION")) {
                        template = template.replace(
                            "DESTINATION", ticketDetailsComposeViewModel.destination
                        )
                    }
                    if (template.contains("WEB_ADDRESS")) {
                        template = template.replace(
                            "WEB_ADDRESS", privilegeResponseModel.webAddressUrl
                        )
                    }


                    if (template.contains("TICKET_FARE")) {
                        template = template.replace(
                            "TICKET_FARE", "${privilegeResponseModel.currency} ${
                                ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                                    ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                            }"
                        )
                    }



                    template = ticketDetailsComposeViewModel.boardingDetails.value.landmark?.let {
                        template?.replace(
                            "LANDMARK", it
                        )
                    } ?: "-"

                    template = template?.replace(
                        "OPERATOR_NAME", privilegeResponseModel.operatorName
                    )!!

                    template = template?.replace(
                        "WEB_ADDRESS", privilegeResponseModel.webAddressUrl
                    )!!

                    template = template?.replace(
                        "ACCOUNT_HOLDER_NAME", loginModelPref.userName
                    )!!
                    template = ticketNumber?.let {
                        template?.replace(
                            "PNR_NUMBER", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.ticketStatus?.let {
                        template?.replace(
                            "TICKET_STATUS", it
                        )
                    } ?: "-"
                    template = template?.replace("ORIGIN", ticketDetailsComposeViewModel.origin)!!
                    template = ticketDetailsComposeViewModel.destination?.let {
                        template?.replace(
                            "DESTINATION", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.boardingDetails.value.depTime?.let {
                        template?.replace(
                            "DEPARTURE_TIME", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.travelDate?.let {
                        template?.replace(
                            "TRAVEL_DATE", "$it"
                        )
                    }!!
                    template = template?.replace(
                        "TICKET_FARE", "${privilegeResponseModel.currency} ${
                            ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                                ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                        }"
                    )!!
                    template = ticketDetailsComposeViewModel.passengerDetails?.get(i)?.name?.let {
                        template?.replace(
                            "PASSENGER_NAME", it
                        )
                    } ?: ""
                    template = ticketDetailsComposeViewModel.passengerDetails?.get(i)?.let {
                        it.mobile?.let { it1 ->
                            template?.replace(
                                "MOBILE_NUMBER", it1
                            )
                        }
                    } ?: ""
                    ticketDetailsComposeViewModel.seatNumbers?.toString()
                        ?.let { Log.d("seatNumber", it) }
                    template = ticketDetailsComposeViewModel.seatNumbers?.let {
                        template?.replace(
                            "SEAT_NUMBERS", it
                        )
                    }!!


                    template = ticketDetailsComposeViewModel.boardingDetails.value.address?.let {
                        template?.replace(
                            "BOARDING_POINT", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.dropOffDetails.value.address?.let {
                        template?.replace(
                            "DROPPING_POINT", it
                        )
                    }!!
                    template =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
                            template?.replace(
                                "CONTACT_PERSON", it
                            )
                        }!!

                    template =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactNumbers?.let {
                            template?.replace(
                                "CONTACT_NUMBER_PERSON", it
                            )
                        }!!

                    /*template = ticketDetailsComposeViewModel.ticketLeadDetail?.ticketBookedBy?.let {
                        template?.replace(
                            "TICKET_BOOKED_BY",
                            it.substringBefore(",")
                        )
                    }!!*/

                    template = ticketDetailsComposeViewModel.busType?.let {
                        template?.replace(
                            "COACH_TYPE", it
                        )
                    }!!
                    template =
                        template?.replace("REMARKS", ticketDetailsComposeViewModel.remarks ?: "-")!!
                    template = template?.replace(
                        "TERMINAL_ID", ticketDetailsComposeViewModel.terminalRefNo ?: ""
                    )!!
                    if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
                        template = template?.replace("TERMINAL_PULOGABANG", "")!!
                    }
                    template = template?.replace("CURRENT_DATE", getTodayDate())!!
                    template = template?.replace("CURRENT_TIME", getTodayDateWithTime())!!

                    template = template?.replace("TAB_SPACE", " ")!!

                    template = template?.replace("BAR_CODE", " ")!!

                    template = template?.replace("BOLD_ON", " ")!!
                    template = template?.replace("BOLD_OFF", " ")!!

                    template = "\n$template\n"
                    template?.let { multiSeats.add(it) }

                    Timber.e("Template : $template")

                    page.addLine().addElement(
                        TextElement(
                            false,
                            false,
                            FontSize.FONT_NORMAL,
                            template,
                            alignment = com.paytm.printgenerator.Alignment.LEFT
                        )
                    )


                }

            }


        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }

        return page
    }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }


    fun getDemoPage(context: Context): Page {

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y_H_M)
        val currentDate = dateFormat.format(calendar.time);

        val page = Page()


        var template = originalTemplate

        if (template!!.contains("PNR_NUMBER")) {
            template = template.replace("PNR_NUMBER", ticketNumber!!)
        }
        if (template.contains("TRAVEL_DATE")) {
            template =
                template.replace("TRAVEL_DATE", ticketDetailsComposeViewModel.travelDate!!)

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
            template = template.replace(
                "SEAT_EACH_NUMBERS", ticketDetailsComposeViewModel.seatNumbers!!
            )

        }
        if (template.contains("SEAT_NUMBERS")) {
            template =
                template.replace("SEAT_NUMBERS", ticketDetailsComposeViewModel.seatNumbers!!)

        }
        if (template.contains("BOARDING_POINT")) {
            template = template.replace(
                "BOARDING_POINT", ticketDetailsComposeViewModel.boardingDetails.value.address!!
            )

        }
        if (template.contains("DROPPING_POINT")) {
            template = template.replace(
                "DROPPING_POINT", ticketDetailsComposeViewModel.dropOffDetails.value.address!!
            )

        }
        if (template.contains("PASSENGER_NAME")) {
            template = template.replace(
                "PASSENGER_NAME", ticketDetailsComposeViewModel.passengerDetails?.get(0)?.name!!
            )
        }
        if (template.contains("DEPARTURE_TIME")) {
            template = template.replace(
                "DEPARTURE_TIME", ticketDetailsComposeViewModel.boardingDetails.value.depTime!!
            )
        }
        if (template.contains("MOBILE_NUMBER")) {
            template = template.replace(
                "MOBILE_NUMBER",
                ticketDetailsComposeViewModel.passengerDetails?.get(0)?.mobile!!
            )
        }
        if (template.contains("TICKET_EACH_FARE")) {
            template = template.replace(
                "TICKET_EACH_FARE", "${privilegeResponseModel.currency} ${
                    ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                        ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                }"
            )
        }
        if (template.contains("ACCOUNT_HOLDER_NAME")) {
            template = template.replace("ACCOUNT_HOLDER_NAME", loginModelPref.userName)

        }
        if (template.contains("TICKET_BOOKED_BY")) {
            template = template.replace(
                "TICKET_BOOKED_BY",
                ticketDetailsComposeViewModel.ticketBookedBy
            )
        }
        if (template.contains("SERVICE_NUMBER")) {
            template = template.replace(
                "SERVICE_NUMBER", ticketDetailsComposeViewModel.serviceNumber!!
            )

        }
        if (template.contains("CONTACT_NUMBER_PERSON")) {
            template = ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
                template?.replace(
                    "CONTACT_NUMBER_PERSON", it
                )
            }!!
        }
        if (template.contains("LANDMARK")) {
            template = ticketDetailsComposeViewModel.boardingDetails.value.landmark?.let {
                template?.replace(
                    "LANDMARK", it
                )
            } ?: "-"
        }
        if (template.contains("CURRENT_TIME")) {
            template = template?.replace("CURRENT_TIME", currentDate)!!
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
            template = template.replace("ORIGIN", ticketDetailsComposeViewModel.origin)
        }
        if (template.contains("DESTINATION")) {
            template =
                template.replace("DESTINATION", ticketDetailsComposeViewModel.destination!!)
        }
        if (template.contains("WEB_ADDRESS")) {
            template = template?.replace(
                "WEB_ADDRESS", privilegeResponseModel.webAddressUrl
            )!!
        }


        if (template.contains("ALIGN_LEFT")) {
            template = template?.replace(
                "ALIGN_LEFT", ""
            )!!
        }

        if (template.contains("ALIGN_CENTER")) {
            template = template?.replace(
                "ALIGN_CENTER", ""
            )!!
        }

        if (template.contains("|")) {
            template = template?.replace(
                "|", ""
            )!!
        }





        if (template.contains("TICKET_FARE")) {
            template = template?.replace(
                "TICKET_FARE", "${privilegeResponseModel.currency} ${
                    ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                        ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                }"
            )!!
        }


        page.addLine().addElement(
            TextElement(
                false,
                false,
                FontSize.FONT_NORMAL,
                template,
                alignment = com.paytm.printgenerator.Alignment.LEFT
            )
        )




        return page
    }


    private fun pineLabPrint() {

        if (isBound!!) {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y_H_M)
            val currentDate = dateFormat.format(calendar.time);


            printArray = JSONArray()
            var template = bluetoothPrintTemplate

            if (template!!.contains("PNR_NUMBER")) {
                template = template.replace("PNR_NUMBER", ticketNumber!!)
            }
            if (template.contains("TRAVEL_DATE")) {
                template =
                    template.replace("TRAVEL_DATE", ticketDetailsComposeViewModel.travelDate!!)

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
                template = template.replace(
                    "SEAT_EACH_NUMBERS", ticketDetailsComposeViewModel.seatNumbers!!
                )

            }
            if (template.contains("SEAT_NUMBERS")) {
                template =
                    template.replace("SEAT_NUMBERS", ticketDetailsComposeViewModel.seatNumbers!!)

            }
            if (template.contains("BOARDING_POINT")) {
                template = template.replace(
                    "BOARDING_POINT", ticketDetailsComposeViewModel.boardingDetails.value.address!!
                )

            }
            if (template.contains("DROPPING_POINT")) {
                template = template.replace(
                    "DROPPING_POINT", ticketDetailsComposeViewModel.dropOffDetails.value.address!!
                )

            }
            if (template.contains("PASSENGER_NAME")) {
                template = template.replace(
                    "PASSENGER_NAME", ticketDetailsComposeViewModel.passengerDetails?.get(0)?.name!!
                )
            }
            if (template.contains("DEPARTURE_TIME")) {
                template = template.replace(
                    "DEPARTURE_TIME", ticketDetailsComposeViewModel.boardingDetails.value.depTime!!
                )
            }
            if (template.contains("MOBILE_NUMBER")) {
                template = template.replace(
                    "MOBILE_NUMBER",
                    ticketDetailsComposeViewModel.passengerDetails?.get(0)?.mobile!!
                )
            }
            if (template.contains("TICKET_EACH_FARE")) {
                template = template.replace(
                    "TICKET_EACH_FARE", "${privilegeResponseModel.currency} ${
                        ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                            ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                    }"
                )
            }
            if (template.contains("ACCOUNT_HOLDER_NAME")) {
                template = template.replace("ACCOUNT_HOLDER_NAME", loginModelPref.userName)

            }/*if (template.contains("TICKET_BOOKED_BY")) {
                template = template.replace(
                    "TICKET_BOOKED_BY",
                    ticketDetailsComposeViewModel.ticketLeadDetail?.ticketBookedBy!!
                )
            }*/
            if (template.contains("SERVICE_NUMBER")) {
                template = template.replace(
                    "SERVICE_NUMBER", ticketDetailsComposeViewModel.serviceNumber!!
                )

            }
            if (template.contains("CONTACT_NUMBER_PERSON")) {
                template = ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
                    template?.replace(
                        "CONTACT_NUMBER_PERSON", it
                    )
                }!!
            }
            if (template.contains("LANDMARK")) {
                template = ticketDetailsComposeViewModel.boardingDetails.value.landmark?.let {
                    template?.replace(
                        "LANDMARK", it
                    )
                } ?: "-"
            }
            if (template.contains("CURRENT_TIME")) {
                template = template?.replace("CURRENT_TIME", currentDate)!!
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
                template = template.replace("ORIGIN", ticketDetailsComposeViewModel.origin)
            }
            if (template.contains("DESTINATION")) {
                template =
                    template.replace("DESTINATION", ticketDetailsComposeViewModel.destination!!)
            }
            if (template.contains("WEB_ADDRESS")) {
                template = template?.replace(
                    "WEB_ADDRESS", privilegeResponseModel.webAddressUrl
                )!!
            }


            if (template.contains("TICKET_FARE")) {
                template = template?.replace(
                    "TICKET_FARE", "${privilegeResponseModel.currency} ${
                        ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                            ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                    }"
                )!!
            }


            val arr = template!!.split("\n")

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
            detailObj.put("PrintRefNo", ticketNumber)
            detailObj.put("SavePrintData", true)
            detailObj.put("Data", printArray)


            val json = JSONObject()
            json.put("Header", headerObj)
            json.put("Detail", detailObj)


            val data = Bundle()
            data.putString(BILLING_REQUEST_TAG, json.toString())
            message.setData(data);
            try {
                message.replyTo = Messenger(
                    IncomingHandler(
                        this
                    )
                )
                mServerMessenger!!.send(message)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        } else {
            toast("Pinelab device not found")
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

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH), PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), PERMISSION_BLUETOOTH_CONNECT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_SCAN), PERMISSION_BLUETOOTH_SCAN
            )
        } else enableDeviceBluetooth()
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




        Timber.d("bluetoothPrintTemplate image edit $bluetoothPrintTemplate")

        if (bluetoothPrintTemplate != null && ::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
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
            template = template.replace("PNR_NUMBER", ticketNumber ?: "")
        }
        if (template.contains("TRAVEL_DATE")) {
            template = template.replace("TRAVEL_DATE", ticketDetailsComposeViewModel.travelDate!!)
        }
        if (template.contains("SERVICE_NUMBER")) {
            template =
                template.replace("SERVICE_NUMBER", ticketDetailsComposeViewModel.serviceNumber!!)
        }
        if (template.contains("TAB_SPACE")) {
            template = template.replace("TAB_SPACE", " ")
        }
        if (template.contains("SEAT_EACH_NUMBERS")) {
            template =
                template.replace("SEAT_EACH_NUMBERS", ticketDetailsComposeViewModel.seatNumbers!!)
        }
        if (template.contains("BOARDING_POINT")) {
            template = template.replace(
                "BOARDING_POINT", ticketDetailsComposeViewModel.boardingDetails.value.address!!
            )
        }
        if (template.contains("DROPPING_POINT")) {
            template = template.replace(
                "DROPPING_POINT", ticketDetailsComposeViewModel.dropOffDetails.value.address!!
            )
        }
        if (template.contains("DEPARTURE_TIME")) {
            template = template.replace(
                "DEPARTURE_TIME", ticketDetailsComposeViewModel.boardingDetails.value.depTime!!
            )
        }
        if (template.contains("PASSENGER_NAME")) {
            template = template.replace(
                "PASSENGER_NAME", ticketDetailsComposeViewModel.passengerDetails?.get(0)?.name!!
            )
        }
        if (template.contains("MOBILE_NUMBER")) {
            template = template.replace(
                "MOBILE_NUMBER", ticketDetailsComposeViewModel.passengerDetails?.get(0)?.mobile!!
            )
        }
        if (template.contains("TICKET_EACH_FARE")) {
            template = template.replace(
                "TICKET_EACH_FARE", "${privilegeResponseModel.currency} ${
                    ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                        ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                }"
            )
        }
        if (template.contains("ACCOUNT_HOLDER_NAME")) {
            template = template.replace("ACCOUNT_HOLDER_NAME", loginModelPref.userName)
        }
        if (template.contains("TICKET_BOOKED_BY")) {
            template = ticketDetailsComposeViewModel.ticketBookedBy.let {
                template?.replace(
                    "TICKET_BOOKED_BY",
                    it.substringBefore(",")
                )
            }!!
        }
        if (!ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.isNullOrEmpty()) {
            template = template?.replace(
                "PAID_AMOUNT",
                ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    .toDouble().convert(currencyFormat = privilegeResponseModel.currencyFormat)
            ) ?: "-"
        } else {
            template = template?.replace("PAID_AMOUNT", "-")!!
        }

        if (!ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount.isNullOrEmpty()) {
            template = template?.replace(
                "REMAINING_AMOUNT",
                ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount.toString()
                    .toDouble().convert(currencyFormat = privilegeResponseModel.currencyFormat)
            ) ?: "-"
        } else {
            template = template?.replace(
                "REMAINING_AMOUNT", "-"
            )!!
        }

        template = ticketDetailsComposeViewModel.ticketStatus?.let {
            template?.replace(
                "TICKET_STATUS", it
            )
        } ?: "-"

        template = template?.replace(
            "TICKET_FARE", "${privilegeResponseModel.currency} ${
                ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                    ?.convert(privilegeResponseModel.currencyFormat) ?: ""
            }"
        )!!

        template = ticketDetailsComposeViewModel.seatNumbers?.let {
            template?.replace(
                "SEAT_NUMBERS", it
            )
        }!!

        template = ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
            template?.replace(
                "CONTACT_PERSON", it
            )
        }!!

        template =
            ticketDetailsComposeViewModel.busType?.let { template?.replace("COACH_TYPE", it) }!!
        template = template?.replace("REMARKS", ticketDetailsComposeViewModel.remarks ?: "-")!!
        template =
            template?.replace("TERMINAL_ID", ticketDetailsComposeViewModel.terminalRefNo ?: "")!!
        if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
            template = template?.replace("TERMINAL_PULOGABANG", "")!!
        }
        template = template?.replace("CURRENT_DATE", getTodayDate())!!

        template = template?.replace("CURRENT_TIME", getTodayDateWithTime())!!

        var passengerCtgy: String? =
            ticketDetailsComposeViewModel.passengerDetails[0]?.passengerCategory
        template =
            template.replace(
                "SERVICE_BY",
                ticketDetailsComposeViewModel.serviceBy.substringBefore(",").trim() ?: ""
            )
        template =
            template.replace("COACH_NUMBER", ticketDetailsComposeViewModel.coachNumber ?: "")
        template =
            template.replace("PASSENGER_CATEGORY", passengerCtgy ?: "")

        if (template?.contains("PICKUP_ADDRESS") == true) {
            template =
                template.replace("PICKUP_ADDRESS", ticketDetailsComposeViewModel.pickupAddress)
        }
        if (template?.contains("PICKUP_CHARGE") == true) {
            template = template.replace(
                "PICKUP_CHARGE",
                ticketDetailsComposeViewModel.pickupCharge.toString()
            )
        }
        if (template?.contains("DROPOFF_ADDRESS") == true) {
            template =
                template.replace("DROPOFF_ADDRESS", ticketDetailsComposeViewModel.dropoffAddress)
        }
        if (template?.contains("DROPOFF_CHARGE") == true) {
            template = template.replace(
                "DROPOFF_CHARGE",
                ticketDetailsComposeViewModel.dropoffCharge.toString()
            )
        }

        if (template?.contains("MEAL_COUPON_NUMBER")!!) {
            template =
                if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel.passengerDetails!!.isNotEmpty()) {
                    var mealCoupons = ""
                    ticketDetailsComposeViewModel.passengerDetails?.forEach {
                        mealCoupons += it?.mealCoupons.toString().replace("[", "").replace("]", "")
                            .replace(",", "\n").replace(" ", "")
                        mealCoupons += "\n"
                    }
                    template?.replace(
                        "MEAL_COUPON_NUMBER", mealCoupons
                    )
                } else {
                    template?.replace(
                        "MEAL_COUPON_NUMBER", "-"
                    )!!
                }!!

        }

        if (template?.contains("MEAL_COUNT")!! && !ticketDetailsComposeViewModel.passengerDetails.isNullOrEmpty()) {
            var mealCouponCount = 0
            ticketDetailsComposeViewModel.passengerDetails?.forEach {
                mealCouponCount += it?.mealCoupons?.size!!
            }
            template = template?.replace(
                "MEAL_COUNT", mealCouponCount.toString()
            )!!
        }

        if (template?.contains("MEAL_COUPON_LOOP")!!) {
            template = template?.replace(
                "MEAL_COUPON_LOOP", ""
            )!!
        }

        template = ticketDetailsComposeViewModel.boardingDetails.value.contactNumbers?.let {
            template?.replace(
                "CONTACT_NUMBER_PERSON", it
            )
        }!!

        template = ticketDetailsComposeViewModel.boardingDetails.value.landmark?.let {
            template?.replace(
                "LANDMARK", it
            )
        } ?: "-"

        template = template?.replace(
            "WEB_ADDRESS", privilegeResponseModel.webAddressUrl
        )!!


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
                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                            SunmiPrintHelper.getInstance().printBitmap(busLogo, 1)
                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                        }
                    }


                    i.contains("TERMINAL_REF_QR_CODE") -> {
                        if (terminalQrBitmap != null) {
                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                            SunmiPrintHelper.getInstance().printBitmap(terminalQrBitmap, 1)
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
                            SunmiPrintHelper.getInstance().printText(i, 24f, true, false, "")
                        } else {
                            SunmiPrintHelper.getInstance().setAlign(0)
                            SunmiPrintHelper.getInstance().printText(i, 24f, false, false, "")
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
                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                            SunmiPrintHelper.getInstance().printBitmap(busLogo, 1)
                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                        }
                    }


                    i.contains("TERMINAL_REF_QR_CODE") -> {
                        if (terminalQrBitmap != null) {
                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
                            SunmiPrintHelper.getInstance().printBitmap(terminalQrBitmap, 1)
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
                        SunmiPrintHelper.getInstance().feedPaper()


                    }

                    else -> {
                        if (isBold) {
                            SunmiPrintHelper.getInstance().setAlign(0)
                            SunmiPrintHelper.getInstance().printText(i + "\n", 24f, true, false, "")
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
            if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel?.passengerDetails?.isNotEmpty()!!) {

                val multiSeats = mutableListOf<String>()
                for (i in 0..ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)!!) {
                    if (ticketDetailsComposeViewModel.passengerDetails?.size!! != 1) {
                        if (i < ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)!!) {
                            template = template?.replace("[C]=", "")!!
                            template = template?.replace("cut here", "")?.trimEnd()!!
                            template = template?.replace("BOARDING_QR", "")?.trim()!!
                            template = template?.replace("BAR_CODE", "")?.trim()!!

                        } else {
                            template = template?.replace("[C]=", "")!!
                            template = template?.replace("=", "")?.trimEnd()!!

                            if (originalTemplate?.contains("BOARDING_QR")!!) template =
                                "${template}\nBOARDING_QR"
                            if (originalTemplate?.contains("BAR_CODE")!!) template =
                                "${template}\n\nBAR_CODE"
                            template = "${template}\nALIGN_CENTER|=======cut here======="
                        }
                    }

                    template = template?.replace("FOR_EACH_SEAT", "")!!
                    if (template?.contains("SEAT_EACH_NUMBERS")!!) {
                        template = template?.replace(
                            "SEAT_EACH_NUMBERS",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.seatNumber ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.seatNumber?.let {
                                    template?.replace(
                                        it,
                                        ticketDetailsComposeViewModel?.passengerDetails!![i]?.seatNumber
                                            ?: ""
                                    )
                                }!!
                        }
                    }
                    if (template?.contains("PASSENGER_EACH_NAME")!!) {
                        template = template?.replace(
                            "PASSENGER_EACH_NAME",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.name ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.name?.let {
                                    template?.replace(
                                        it,
                                        ticketDetailsComposeViewModel?.passengerDetails!![i]?.name
                                            ?: ""
                                    )
                                }!!
                        }
                    }


                    if (template?.contains("TICKET_EACH_FARE")!!) {
                        template = template?.replace(
                            "TICKET_EACH_FARE",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.netFare?.toDouble()
                                ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                        )!!
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.netFare.toString()
                                    .let {
                                        template.replace(
                                            it,
                                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.netFare?.convert(
                                                privilegeResponseModel.currencyFormat
                                            ) ?: ""
                                        )
                                    }!!
                        }
                    }


                    if (!ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.isNullOrEmpty()) {
                        if (template?.contains("MEAL_COUPON_LOOP")!!) {

                            template = template?.replace(
                                "MEAL_COUPON_LOOP",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "").replace(",", "\n")
                                    .replace(" ", "")
                            )!!
                        } else {
                            if (i > 0) {
                                template =
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons?.toString()
                                        ?.let {
                                            template?.replace(
                                                it,
                                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                            )
                                        }!!
                            }
                        }

                        if (template?.contains("MEAL_COUPON_NUMBER")!!) {
                            template = template?.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "")
                            )!!
                        } else {
                            if (i > 0) {
                                template = template?.replace(
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons.toString()
                                        .replace("[", "").replace("]", ""),
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                        .replace("[", "").replace("]", "")
                                )!!
                            }
                        }

                        if (template?.contains("MEAL_COUNT")!!) {
                            template = template?.replace(
                                "MEAL_COUNT",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.size.toString()
                            )!!
                        } else {
                            if (i > 0) {
                                template = template?.replace(
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons?.size.toString(),
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.size.toString()
                                )!!
                            }
                        }
                    } else {
                        template = template?.replace(
                            "MEAL_COUPON_LOOP", ""
                        )!!
                    }




                    template = ticketDetailsComposeViewModel.serviceNumber?.let {
                        template?.replace(
                            "SERVICE_NUMBER", it
                        )
                    }!!

                    template = template?.replace(
                        "PAID_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    ) ?: "-"
                    template = template?.replace(
                        "REMAINING_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount.toString()
                    ) ?: "-"


                    template = ticketDetailsComposeViewModel.boardingDetails.value.landmark?.let {
                        template?.replace(
                            "LANDMARK", it
                        )
                    } ?: "-"

                    template = template?.replace(
                        "OPERATOR_NAME", privilegeResponseModel.operatorName
                    )!!

                    template = template?.replace(
                        "WEB_ADDRESS", privilegeResponseModel.webAddressUrl
                    )!!

                    template = template?.replace(
                        "ACCOUNT_HOLDER_NAME", loginModelPref.userName
                    )!!
                    template = ticketNumber?.let {
                        template?.replace(
                            "PNR_NUMBER", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.ticketStatus?.let {
                        template?.replace(
                            "TICKET_STATUS", it
                        )
                    } ?: "-"
                    template = template?.replace("ORIGIN", ticketDetailsComposeViewModel.origin)!!
                    template = ticketDetailsComposeViewModel.destination?.let {
                        template?.replace(
                            "DESTINATION", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.boardingDetails.value.depTime?.let {
                        template?.replace(
                            "DEPARTURE_TIME", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.travelDate?.let {
                        template?.replace(
                            "TRAVEL_DATE", "$it"
                        )
                    }!!
                    template = template?.replace(
                        "TICKET_FARE", "${privilegeResponseModel.currency} ${
                            ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                                ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                        }"
                    )!!
                    template = ticketDetailsComposeViewModel.passengerDetails?.get(i)?.name?.let {
                        template?.replace(
                            "PASSENGER_NAME", it
                        )
                    } ?: ""
                    template = ticketDetailsComposeViewModel.passengerDetails?.get(i)?.let {
                        it.mobile?.let { it1 ->
                            template?.replace(
                                "MOBILE_NUMBER", it1
                            )
                        }
                    } ?: ""
                    template = ticketDetailsComposeViewModel.seatNumbers?.let {
                        template?.replace(
                            "SEAT_NUMBERS", it
                        )
                    }!!


                    template = ticketDetailsComposeViewModel.boardingDetails.value.address?.let {
                        template?.replace(
                            "BOARDING_POINT", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.dropOffDetails.value.address?.let {
                        template?.replace(
                            "DROPPING_POINT", it
                        )
                    }!!
                    template =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
                            template?.replace(
                                "CONTACT_PERSON", it
                            )
                        }!!

                    template =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactNumbers?.let {
                            template?.replace(
                                "CONTACT_NUMBER_PERSON", it
                            )
                        }!!

                    template = ticketDetailsComposeViewModel.ticketBookedBy.let {
                        template?.replace(
                            "TICKET_BOOKED_BY",
                            it.substringBefore(",")
                        )
                    }!!

                    template = ticketDetailsComposeViewModel.busType?.let {
                        template?.replace(
                            "COACH_TYPE", it
                        )
                    }!!
                    template =
                        template?.replace("REMARKS", ticketDetailsComposeViewModel.remarks ?: "-")!!
                    template = template?.replace(
                        "TERMINAL_ID", ticketDetailsComposeViewModel.terminalRefNo ?: ""
                    )!!
                    if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
                        template = template?.replace("TERMINAL_PULOGABANG", "")!!
                    }
                    template = template?.replace("CURRENT_DATE", getTodayDate())!!
                    template = template?.replace("CURRENT_TIME", getTodayDateWithTime())!!

                    template = template?.replace("TAB_SPACE", " ")!!

                    template = template?.replace("BAR_CODE", " ")!!

                    template = "\n$template\n"
                    template?.let { multiSeats.add(it) }

                    var passengerCtgy: String? =
                        ticketDetailsComposeViewModel.passengerDetails[i]?.passengerCategory
                    template =
                        template.replace(
                            "SERVICE_BY",
                            ticketDetailsComposeViewModel.serviceBy.substringBefore(",").trim()
                                ?: ""
                        )
                    template =
                        template.replace(
                            "COACH_NUMBER",
                            ticketDetailsComposeViewModel.coachNumber ?: ""
                        )
                    template =
                        template.replace("PASSENGER_CATEGORY", passengerCtgy ?: "")

                    if (template?.contains("PICKUP_ADDRESS") == true) {
                        template = template.replace(
                            "PICKUP_ADDRESS",
                            ticketDetailsComposeViewModel.pickupAddress
                        )
                    }
                    if (template?.contains("PICKUP_CHARGE") == true) {
                        template = template.replace(
                            "PICKUP_CHARGE",
                            ticketDetailsComposeViewModel.pickupCharge.toString()
                        )
                    }
                    if (template?.contains("DROPOFF_ADDRESS") == true) {
                        template = template.replace(
                            "DROPOFF_ADDRESS",
                            ticketDetailsComposeViewModel.dropoffAddress
                        )
                    }
                    if (template?.contains("DROPOFF_CHARGE") == true) {
                        template = template.replace(
                            "DROPOFF_CHARGE",
                            ticketDetailsComposeViewModel.dropoffCharge.toString()
                        )
                    }


                    val temp = template.split("\n")
                    var isBold = false
                    if (withoutSpacePrint) {
                        for (i in temp) {
                            when {
                                i.contains("FOR_EACH_SEAT") -> {
                                    SunmiPrintHelper.getInstance().printText(
                                        i.replace("FOR_EACH_SEAT", ""), 24f, false, false, ""
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
                                        SunmiPrintHelper.getInstance()
                                            .printText("\n", 24f, true, false, "")
                                        SunmiPrintHelper.getInstance().printBitmap(busLogo, 1)
                                        SunmiPrintHelper.getInstance()
                                            .printText("\n", 24f, true, false, "")
                                    }
                                }


                                i.contains("TERMINAL_REF_QR_CODE") -> {
                                    if (terminalQrBitmap != null) {
                                        SunmiPrintHelper.getInstance()
                                            .printText("\n", 24f, true, false, "")
                                        SunmiPrintHelper.getInstance()
                                            .printBitmap(terminalQrBitmap, 1)
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
                                    SunmiPrintHelper.getInstance().printText(
                                        i.replace("FOR_EACH_SEAT", ""), 24f, false, false, ""
                                    )
                                }

                                i.contains("ALIGN_CENTER|") -> {
                                    SunmiPrintHelper.getInstance().setAlign(1)
                                    SunmiPrintHelper.getInstance().printText(
                                        i.substringAfter("|") + "\n", 24f, false, false, ""
                                    )

                                }

                                i.contains("ALIGN_CENTER") -> {
                                    SunmiPrintHelper.getInstance().setAlign(1)
                                    SunmiPrintHelper.getInstance().printText(
                                        i.substringAfter("R") + "\n", 24f, false, false, ""
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
                                        SunmiPrintHelper.getInstance()
                                            .printText("\n", 24f, true, false, "")
                                        SunmiPrintHelper.getInstance().printBitmap(busLogo, 1)
                                        SunmiPrintHelper.getInstance()
                                            .printText("\n", 24f, true, false, "")
                                    }
                                }


                                i.contains("TERMINAL_REF_QR_CODE") -> {
                                    if (terminalQrBitmap != null) {
                                        SunmiPrintHelper.getInstance()
                                            .printText("\n", 24f, true, false, "")
                                        SunmiPrintHelper.getInstance()
                                            .printBitmap(terminalQrBitmap, 1)
                                        SunmiPrintHelper.getInstance()
                                            .printText("\n", 24f, true, false, "")
                                    }
                                }

                                i.contains("ALIGN_LEFT|") -> {
                                    if (isBold) {
                                        SunmiPrintHelper.getInstance().setAlign(0)
                                        SunmiPrintHelper.getInstance().printText(
                                            i.substringAfter("|") + "\n", 24f, true, false, ""
                                        )
                                    } else {
                                        SunmiPrintHelper.getInstance().setAlign(0)
                                        SunmiPrintHelper.getInstance().printText(
                                            i.substringAfter("|") + "\n", 24f, false, false, ""
                                        )
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
                    if (i == ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)) {
                        SunmiPrintHelper.getInstance().feedPaper()
                    }


                }


            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }


    }

    private fun bytesToHex(bytes: ByteArray): String {
        val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = HEX_ARRAY[v ushr 4]
            hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun createJson(
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
            if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel?.passengerDetails?.isNotEmpty()!!) {

                val multiSeats = mutableListOf<String>()
                for (i in 0..ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)!!) {
                    val passengerDetailData: PassengerDetail? =
                        ticketDetailsComposeViewModel.passengerDetails?.get(i)
                    bluetoothPrintTemplate = originalTemplate ?: ""
                    commonReplacementPrint()

                    /*if (i > 0) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("IMAGE", "\nIMAGE")
                    }*/


                    if (ticketDetailsComposeViewModel.passengerDetails.isNotEmpty() && !ticketDetailsComposeViewModel.passengerDetails.get(
                            i
                        )?.meal_coupon_qr.isNullOrEmpty()
                    ) {
                        bluetoothPrintTemplate =
                            ticketDetailsComposeViewModel.passengerDetails.get(i)?.meal_coupon_qr?.let { str ->

                                if (str.isNotEmpty()) {
                                    bluetoothPrintTemplate?.replace(
                                        "meal_coupon_qr", "[C]<qrcode size='20'>$str</qrcode>"
                                    )
                                } else {
                                    bluetoothPrintTemplate?.replace(
                                        "meal_coupon_qr", "-"
                                    )
                                }

                            }

                    } else {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "meal_coupon_qr", ""
                        )
                    }
                    if (i < ticketDetailsComposeViewModel.passengerDetails?.size?.minus(1)!!) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("[C]=", "")
                        bluetoothPrintTemplate =
                            bluetoothPrintTemplate?.replace("cut here", "")?.trimEnd()/*    bluetoothPrintTemplate =
                                    bluetoothPrintTemplate?.replace("BOARDING_QR", "")?.trim()*/
                        bluetoothPrintTemplate =
                            bluetoothPrintTemplate?.replace("BAR_CODE", "")?.trim()

                    } else {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("[C]=", "")
                        bluetoothPrintTemplate =
                            bluetoothPrintTemplate?.replace("=", "")?.trimEnd()

                        /*       if (originalTemplate?.contains("BOARDING_QR")!!)
                                   bluetoothPrintTemplate = "${bluetoothPrintTemplate}\nBOARDING_QR"*/
                        if (originalTemplate?.contains("BAR_CODE")!!) bluetoothPrintTemplate =
                            "${bluetoothPrintTemplate}\n\nBAR_CODE"
                        bluetoothPrintTemplate =
                            "${bluetoothPrintTemplate}\n[C]=======cut here======="
                    }


                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("FOR_EACH_SEAT", "")


                    if (bluetoothPrintTemplate?.contains("SEAT_EACH_NUMBERS")!!) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "SEAT_EACH_NUMBERS",
                            " ${ticketDetailsComposeViewModel?.passengerDetails!![i]?.seatNumber} "
                        )
                    } else {
                        if (i > 0) {
                            bluetoothPrintTemplate =
                                ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.seatNumber?.let {
                                    bluetoothPrintTemplate?.replace(
                                        " $it ",
                                        " ${ticketDetailsComposeViewModel?.passengerDetails!![i]?.seatNumber} "
                                    )
                                }
                        }
                    }

                    if (bluetoothPrintTemplate?.contains("PASSENGER_EACH_NAME")!!) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "PASSENGER_EACH_NAME",
                            ticketDetailsComposeViewModel?.passengerDetails!![i]?.name ?: ""
                        )
                    } else {
                        if (i > 0) {
                            bluetoothPrintTemplate =
                                ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.name?.let {
                                    bluetoothPrintTemplate?.replace(
                                        it,
                                        ticketDetailsComposeViewModel?.passengerDetails!![i]?.name
                                            ?: ""
                                    )
                                }
                        }
                    }


                    if (bluetoothPrintTemplate?.contains("TICKET_EACH_FARE")!!) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "TICKET_EACH_FARE",
                            ticketDetailsComposeViewModel.passengerDetails!![i]?.netFare?.toDouble()
                                ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                        )
                    } else {
                        if (i > 0) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "${
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.netFare?.toDouble()
                                        ?.convert(privilegeResponseModel.currencyFormat)
                                }",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.netFare?.toDouble()
                                    ?.convert(privilegeResponseModel.currencyFormat) ?: ""
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
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    "$qrCodeInput\n[C]=======cut here=======",
                                    "[C]<qrcode size='25'>$qrCodeInput</qrcode>"
                                )
                            }
                        }
                    }

                    var passengerCtgy: String? =
                        ticketDetailsComposeViewModel.passengerDetails[i]?.passengerCategory
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace(
                            "SERVICE_BY",
                            ticketDetailsComposeViewModel.serviceBy.substringBefore(",").trim()
                                ?: ""
                        )
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace(
                            "COACH_NUMBER",
                            ticketDetailsComposeViewModel.coachNumber ?: ""
                        )
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace("PASSENGER_CATEGORY", passengerCtgy ?: "")

                    if (bluetoothPrintTemplate?.contains("PICKUP_ADDRESS") == true) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "PICKUP_ADDRESS",
                            ticketDetailsComposeViewModel.pickupAddress
                        )
                    }
                    if (bluetoothPrintTemplate?.contains("PICKUP_CHARGE") == true) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "PICKUP_CHARGE",
                            ticketDetailsComposeViewModel.pickupCharge.toString()
                        )
                    }
                    if (bluetoothPrintTemplate?.contains("DROPOFF_ADDRESS") == true) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "DROPOFF_ADDRESS",
                            ticketDetailsComposeViewModel.dropoffAddress
                        )
                    }
                    if (bluetoothPrintTemplate?.contains("DROPOFF_CHARGE") == true) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "DROPOFF_CHARGE",
                            ticketDetailsComposeViewModel.dropoffCharge.toString()
                        )
                    }

                    if (!ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.isNullOrEmpty()) {
                        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_LOOP")!!) {

                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_COUPON_LOOP",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "").replace(",", "\n")
                                    .replace(" ", "")
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate =
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons?.toString()
                                        ?.let {
                                            bluetoothPrintTemplate?.replace(
                                                it,
                                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                            )
                                        }
                            }
                        }

                        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_NUMBER")!! && !ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.isNullOrEmpty()) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "")
                            )
                        } else {
                            /*  if (i > 0 && !ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.isNullOrEmpty()) {
                                  if(!ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons.isNullOrEmpty()){
                                      bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                          ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons.toString()
                                              .replace("[", "").replace("]", ""),
                                          ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                              .replace("[", "").replace("]", "")
                                      )
                                  }else{
                                      bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                          ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                              .replace("[", "").replace("]", ""),
                                          ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.toString()
                                              .replace("[", "").replace("]", "")
                                      )
                                  }
                              }
                              Log.d("template666",bluetoothPrintTemplate.toString())*/

                        }


                        if (bluetoothPrintTemplate?.contains("MEAL_COUNT")!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_COUNT",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.size.toString()
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.mealCoupons?.size.toString(),
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons?.size.toString()
                                )
                            }
                        }
                    } else {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "MEAL_COUPON_LOOP", ""
                        )
                    }

                    if (ticketDetailsComposeViewModel.passengerDetails!![i]?.selectedMealType.toString()
                            .isNotEmpty() && ticketDetailsComposeViewModel.passengerDetails!![i]?.selectedMealType.toString() != "-"
                    ) {
                        if (bluetoothPrintTemplate?.contains("MEAL_TYPE")!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_TYPE",
                                ticketDetailsComposeViewModel?.passengerDetails!![i]?.selectedMealType.toString()
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    ticketDetailsComposeViewModel?.passengerDetails!![i.minus(1)]?.selectedMealType.toString(),
                                    ticketDetailsComposeViewModel?.passengerDetails!![i]?.selectedMealType.toString()
                                )
                            }
                        }
                    } else {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "MEAL_TYPE", "-"
                        )
                    }

                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace(
                            "PASSENGER_CATEGORY",
                            passengerDetailData?.passengerCategory.toString()
                        )



                    if (!ticketDetailsComposeViewModel.insuranceTransDetails.value.details.isNullOrEmpty()) {
                        if (bluetoothPrintTemplate?.contains("ALL_INSURANCE_NUMBERS")!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "ALL_INSURANCE_NUMBERS",
                                "${getString(R.string.seat)} (${getString(R.string.booking_code)}): ${
                                    ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.get(
                                        i
                                    )?.seat_no
                                } (${
                                    ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.get(
                                        i
                                    )?.info?.booking_code
                                }) ${
                                    getString(
                                        R.string.policy
                                    )
                                }: ${
                                    ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.get(
                                        i
                                    )?.info?.policy_number
                                }"
                            )
                        } else {
                            val newPolicyNumber =
                                ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.get(
                                    i
                                )?.info?.policy_number?.trim()
                            val newSeatNo = "${
                                ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.get(
                                    i
                                )?.seat_no
                            }"
                            if (i > 0) {
                                val oldPolicyNumber =
                                    ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.get(
                                        i.minus(1)
                                    )?.info?.policy_number?.trim()
                                val oldSeatNo = "${
                                    ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.get(
                                        i.minus(1)
                                    )?.seat_no
                                }"
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    oldValue = "(${
                                        ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.get(
                                            i.minus(
                                                1
                                            )
                                        )?.info?.booking_code
                                    })", newValue = "(${
                                        ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.get(
                                            i
                                        )?.info?.booking_code
                                    })"
                                )

                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    oldPolicyNumber ?: "", newPolicyNumber ?: ""
                                )

                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replaceFirst(
                                    oldSeatNo, newSeatNo
                                )
                            }
                        }
                    }

                    bluetoothPrintTemplate = ticketDetailsComposeViewModel.serviceNumber?.let {
                        bluetoothPrintTemplate?.replace(
                            "SERVICE_NUMBER", it
                        )
                    }

                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "PAID_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    ) ?: "-"
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "REMAINING_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount.toString()
                    ) ?: "-"

                    getLandmarkPrint()
                    getOperatorNamePrint()
                    getWebAddressPrint()

                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "ACCOUNT_HOLDER_NAME", loginModelPref.userName
                    )

                    bluetoothPrintTemplate = ticketDetailsComposeViewModel.ticketStatus?.let {
                        bluetoothPrintTemplate?.replace(
                            "TICKET_STATUS", it
                        )
                    } ?: "-"
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "ORIGIN", ticketDetailsComposeViewModel.origin
                    )
                    bluetoothPrintTemplate = ticketDetailsComposeViewModel.destination?.let {
                        bluetoothPrintTemplate?.replace(
                            "DESTINATION", it
                        )
                    }
                    bluetoothPrintTemplate =
                        ticketDetailsComposeViewModel.boardingDetails.value.depTime?.let {
                            bluetoothPrintTemplate?.replace(
                                "DEPARTURE_TIME", it
                            )
                        }
                    bluetoothPrintTemplate = ticketDetailsComposeViewModel.travelDate?.let {
                        bluetoothPrintTemplate?.replace(
                            "TRAVEL_DATE", "$it"
                        )
                    }
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "TICKET_FARE", "${privilegeResponseModel.currency} ${
                            ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                                ?.convert(privilegeResponseModel.currencyFormat) ?: ""
                        }"
                    )
                    bluetoothPrintTemplate =
                        ticketDetailsComposeViewModel.passengerDetails?.get(i)?.name?.let {
                            bluetoothPrintTemplate?.replace(
                                "PASSENGER_NAME", it
                            )
                        } ?: ""
                    bluetoothPrintTemplate =
                        ticketDetailsComposeViewModel.passengerDetails?.get(i)?.let {
                            it.mobile?.let { it1 ->
                                bluetoothPrintTemplate?.replace(
                                    "MOBILE_NUMBER", it1
                                )
                            }
                        } ?: ""
                    bluetoothPrintTemplate = ticketDetailsComposeViewModel.seatNumbers?.let {
                        bluetoothPrintTemplate?.replace(
                            "SEAT_NUMBERS", passengerDetailData?.seatNumber ?: ""
                        )
                    }


                    bluetoothPrintTemplate =
                        ticketDetailsComposeViewModel.boardingDetails.value.address?.let {
                            bluetoothPrintTemplate?.replace(
                                "BOARDING_POINT", it
                            )
                        }
                    if (ticketDetailsComposeViewModel?.dropOffDetails != null) {
                        bluetoothPrintTemplate =
                            ticketDetailsComposeViewModel.dropOffDetails.value.address?.let {
                                bluetoothPrintTemplate?.replace(
                                    "DROPPING_POINT", it
                                )
                            }
                    }
                    bluetoothPrintTemplate =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
                            bluetoothPrintTemplate?.replace(
                                "CONTACT_PERSON", it
                            )
                        }
                    getContactNumberPrint()
                    getTicketBookedByPrint()

                    bluetoothPrintTemplate = ticketDetailsComposeViewModel.busType?.let {
                        bluetoothPrintTemplate?.replace(
                            "COACH_TYPE", it
                        )
                    }
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "REMARKS", ticketDetailsComposeViewModel.remarks ?: "-"
                    )
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "TERMINAL_ID", ticketDetailsComposeViewModel.terminalRefNo ?: ""
                    )
                    if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
                        bluetoothPrintTemplate =
                            bluetoothPrintTemplate?.replace("TERMINAL_PULOGABANG", "")
                    }
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace("CURRENT_DATE", getTodayDate())
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace("CURRENT_TIME", getTodayDateWithTime())

                    bluetoothPrintTemplate = "\n$bluetoothPrintTemplate\n"
                    bluetoothPrintTemplate?.let { multiSeats.add(it) }

                }
                Timber.d("multiSeats $multiSeats")
                bluetoothPrintTemplate =
                    multiSeats.toString().removePrefix("[").removeSuffix("]").replace(",", "")

                bluetoothPrintTemplate = ticketNumber?.let {
                    bluetoothPrintTemplate?.replace(
                        "PNR_NUMBER", it
                    )
                }

                bluetoothPrintTemplate =
                    bluetoothPrintTemplate?.replace(
                        "SERVICE_BY",
                        ticketDetailsComposeViewModel.serviceBy.toString().substringBefore(",")
                            .trim() ?: "-"
                    )
                bluetoothPrintTemplate =
                    bluetoothPrintTemplate?.replace(
                        "COACH_NUMBER",
                        ticketDetailsComposeViewModel.coachNumber.toString() ?: "-"
                    )

            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    private fun singleSeatBluetoothPrint() {
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.serviceNumber?.let {
            bluetoothPrintTemplate?.replace(
                "SERVICE_NUMBER", it
            )
        }

        if (!ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.isNullOrEmpty()) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PAID_AMOUNT",
                ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    .toDouble().convert(currencyFormat = privilegeResponseModel.currencyFormat)
            ) ?: "-"
        } else {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PAID_AMOUNT", "-"
            )
        }

        if (!ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount.isNullOrEmpty()) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "REMAINING_AMOUNT",
                ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount.toString()
                    .toDouble().convert(currencyFormat = privilegeResponseModel.currencyFormat)
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
        bluetoothPrintTemplate = ticketNumber?.let {
            bluetoothPrintTemplate?.replace(
                "PNR_NUMBER", it
            )
        }
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.ticketStatus?.let {
            bluetoothPrintTemplate?.replace(
                "TICKET_STATUS", it
            )
        } ?: "-"
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("ORIGIN", ticketDetailsComposeViewModel.origin)
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.destination?.let {
            bluetoothPrintTemplate?.replace(
                "DESTINATION", it
            )
        }
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.boardingDetails.value.depTime?.let {
            bluetoothPrintTemplate?.replace(
                "DEPARTURE_TIME", it
            )
        }
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.travelDate?.let {
            bluetoothPrintTemplate?.replace(
                "TRAVEL_DATE", it
            )
        }


        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "TICKET_EACH_FARE", "${privilegeResponseModel.currency} ${
                ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                    ?.convert(privilegeResponseModel.currencyFormat) ?: ""
            }"
        )

        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "TICKET_FARE", "${privilegeResponseModel.currency} ${
                ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                    ?.convert(privilegeResponseModel.currencyFormat) ?: ""
            }"
        )
        println("QRCode->T$bluetoothPrintTemplate")

        bluetoothPrintTemplate = ticketDetailsComposeViewModel.passengerDetails?.get(0)?.name?.let {
            bluetoothPrintTemplate?.replace(
                "PASSENGER_NAME", it
            )
        } ?: ""
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.passengerDetails?.get(0)?.let {
            it.mobile?.let { it1 ->
                bluetoothPrintTemplate?.replace(
                    "MOBILE_NUMBER", it1
                )
            }
        } ?: ""

        if (bluetoothPrintTemplate?.contains("SEAT_EACH_NUMBERS") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "SEAT_EACH_NUMBERS", ticketDetailsComposeViewModel.seatNumbers ?: ""
            )
        }

        bluetoothPrintTemplate = ticketDetailsComposeViewModel.seatNumbers?.let {
            bluetoothPrintTemplate?.replace(
                "SEAT_NUMBERS", it
            )
        }

        bluetoothPrintTemplate = ticketDetailsComposeViewModel.boardingDetails.value.address?.let {
            bluetoothPrintTemplate?.replace(
                "BOARDING_POINT", it
            )
        }
        if (ticketDetailsComposeViewModel.passengerDetails.isNotEmpty() && !ticketDetailsComposeViewModel.passengerDetails.get(
                0
            )?.meal_coupon_qr.isNullOrEmpty()
        ) {
            bluetoothPrintTemplate =
                ticketDetailsComposeViewModel.passengerDetails.get(0)?.meal_coupon_qr?.let { it ->
                    println("QRCode->$it")

                    if (it.isNotEmpty()) {
                        bluetoothPrintTemplate?.replace(
                            "meal_coupon_qr", "[C]<qrcode size='20'>$it</qrcode>"
                        )
                    } else {
                        bluetoothPrintTemplate?.replace(
                            "meal_coupon_qr", "-"
                        )
                    }
                }

        } else {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "meal_coupon_qr", "-"
            )
        }

        if (ticketDetailsComposeViewModel?.dropOffDetails != null) {
            bluetoothPrintTemplate =
                ticketDetailsComposeViewModel.dropOffDetails.value.address?.let {
                    bluetoothPrintTemplate?.replace(
                        "DROPPING_POINT", it
                    )
                }
        }
        bluetoothPrintTemplate =
            ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
                bluetoothPrintTemplate?.replace(
                    "CONTACT_PERSON", it
                )
            }
        getContactNumberPrint()
        getTicketBookedByPrint()
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.busType?.let {
            bluetoothPrintTemplate?.replace(
                "COACH_TYPE", it
            )
        }
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("REMARKS", ticketDetailsComposeViewModel.remarks ?: "-")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "TERMINAL_ID", ticketDetailsComposeViewModel.terminalRefNo ?: ""
        )

        var passengerCtgy: String? =
            ticketDetailsComposeViewModel.passengerDetails[0]?.passengerCategory
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace(
                "SERVICE_BY",
                ticketDetailsComposeViewModel.serviceBy.substringBefore(",").trim() ?: ""
            )
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace(
                "COACH_NUMBER",
                ticketDetailsComposeViewModel.coachNumber ?: ""
            )
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("PASSENGER_CATEGORY", passengerCtgy ?: "")

        if (bluetoothPrintTemplate?.contains("PICKUP_ADDRESS") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PICKUP_ADDRESS",
                ticketDetailsComposeViewModel.pickupAddress
            )
        }
        if (bluetoothPrintTemplate?.contains("PICKUP_CHARGE") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PICKUP_CHARGE",
                ticketDetailsComposeViewModel.pickupCharge.toString()
            )
        }
        if (bluetoothPrintTemplate?.contains("DROPOFF_ADDRESS") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "DROPOFF_ADDRESS",
                ticketDetailsComposeViewModel.dropoffAddress
            )
        }
        if (bluetoothPrintTemplate?.contains("DROPOFF_CHARGE") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "DROPOFF_CHARGE",
                ticketDetailsComposeViewModel.dropoffCharge.toString()
            )
        }

        if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("TERMINAL_PULOGABANG", "")
        }
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("CURRENT_DATE", getTodayDate())

        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("CURRENT_TIME", getTodayDateWithTime())


        if (bluetoothPrintTemplate?.contains("PASSENGER_EACH_NAME") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PASSENGER_EACH_NAME",
                ticketDetailsComposeViewModel.passengerDetails!![0]?.name ?: ""
            )
        }

        if (bluetoothPrintTemplate?.contains("SEAT_EACH_NUMBER") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "SEAT_EACH_NUMBER",
                ticketDetailsComposeViewModel.passengerDetails!![0]?.seatNumber ?: ""
            )
        }

        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_NUMBER") == true) {
            bluetoothPrintTemplate =
                if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel.passengerDetails!!.isNotEmpty()) {
                    var mealCoupons = ""
                    ticketDetailsComposeViewModel.passengerDetails?.forEach {
                        mealCoupons += it?.mealCoupons.toString().replace("[", "").replace("]", "")
                            .replace(",", "\n").replace(" ", "")
                        mealCoupons += "\n"
                    }
                    bluetoothPrintTemplate?.replace(
                        "MEAL_COUPON_NUMBER", mealCoupons
                    )
                } else {
                    bluetoothPrintTemplate?.replace(
                        "MEAL_COUPON_NUMBER", "-"
                    )
                }

        }


        if (bluetoothPrintTemplate?.contains("MEAL_COUNT") == true && !ticketDetailsComposeViewModel.passengerDetails.isNullOrEmpty()) {
            var mealCouponCount = 0
            ticketDetailsComposeViewModel.passengerDetails?.forEach {
                mealCouponCount += it?.mealCoupons?.size!!
            }
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "MEAL_COUNT", mealCouponCount.toString()
            )
        }

        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_LOOP") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "MEAL_COUPON_LOOP", ""
            )
        }

        if (bluetoothPrintTemplate?.contains("MEAL_TYPE") == true) {
            bluetoothPrintTemplate =
                if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel.passengerDetails!!.isNotEmpty() && ticketDetailsComposeViewModel.passengerDetails!!.any { it?.selectedMealType != "-" }) {
                    var mealTypes = ""
                    ticketDetailsComposeViewModel.passengerDetails?.forEach {
                        mealTypes += it?.selectedMealType.toString()
                        mealTypes += ","
                    }
                    bluetoothPrintTemplate?.replace(
                        "MEAL_TYPE", mealTypes.removeSuffix(",")
                    )
                } else {
                    bluetoothPrintTemplate?.replace(
                        "MEAL_TYPE", "-"
                    )
                }

        }
        if (bluetoothPrintTemplate?.contains("ALL_INSURANCE_NUMBERS") == true && ticketDetailsComposeViewModel?.insuranceTransDetails?.value?.details != null) {
            var allInsuranceNumbers = ""
            ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.forEach {
                allInsuranceNumbers += "${getString(R.string.seat)} (${getString(R.string.booking_code)}): ${it?.seat_no} (${it?.info?.booking_code}) ${
                    getString(
                        R.string.policy
                    )
                }: ${it?.info?.policy_number}\n"
            }
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "ALL_INSURANCE_NUMBERS", allInsuranceNumbers
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


    /*private fun singleSeatEzetapPrint() {
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.serviceNumber?.let {
            bluetoothPrintTemplate?.replace(
                "SERVICE_NUMBER", it
            )
        }

        if (!ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.isNullOrEmpty()) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PAID_AMOUNT",
                ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    .toDouble().convert(currencyFormat = privilegeResponseModel.currencyFormat)
            ) ?: "-"
        } else {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PAID_AMOUNT", "-"
            )
        }

        if (!ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount.isNullOrEmpty()) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "REMAINING_AMOUNT",
                ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount.toString()
                    .toDouble().convert(currencyFormat = privilegeResponseModel.currencyFormat)
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
        bluetoothPrintTemplate = ticketNumber?.let {
            bluetoothPrintTemplate?.replace(
                "PNR_NUMBER", it
            )
        }
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.ticketStatus?.let {
            bluetoothPrintTemplate?.replace(
                "TICKET_STATUS", it
            )
        } ?: "-"
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("ORIGIN", ticketDetailsComposeViewModel.origin)
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.destination?.let {
            bluetoothPrintTemplate?.replace(
                "DESTINATION", it
            )
        }
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.boardingDetails.value.depTime?.let {
            bluetoothPrintTemplate?.replace(
                "DEPARTURE_TIME", it
            )
        }
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.travelDate?.let {
            bluetoothPrintTemplate?.replace(
                "TRAVEL_DATE", it
            )
        }

        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "TICKET_EACH_FARE", "${privilegeResponseModel.currency} ${
                ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                    ?.convert(privilegeResponseModel.currencyFormat) ?: ""
            }"
        )

        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "TICKET_FARE", "${privilegeResponseModel.currency} ${
                ticketDetailsComposeViewModel.totalFare?.toString()?.toDouble()
                    ?.convert(privilegeResponseModel.currencyFormat) ?: ""
            }"
        )

        bluetoothPrintTemplate = ticketDetailsComposeViewModel.passengerDetails?.get(0)?.name?.let {
            bluetoothPrintTemplate?.replace(
                "PASSENGER_NAME", it
            )
        } ?: ""
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.passengerDetails?.get(0)?.let {
            it.mobile?.let { it1 ->
                bluetoothPrintTemplate?.replace(
                    "MOBILE_NUMBER", it1
                )
            }
        } ?: ""

        if (bluetoothPrintTemplate?.contains("SEAT_EACH_NUMBERS") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "SEAT_EACH_NUMBERS", ticketDetailsComposeViewModel.seatNumbers ?: ""
            )
        }




        bluetoothPrintTemplate = ticketDetailsComposeViewModel.seatNumbers?.let {
            bluetoothPrintTemplate?.replace(
                "SEAT_NUMBERS", it
            )
        }

        bluetoothPrintTemplate = ticketDetailsComposeViewModel.boardingDetails.value.address?.let {
            bluetoothPrintTemplate?.replace(
                "BOARDING_POINT", it
            )
        }

        if (ticketDetailsComposeViewModel?.dropOffDetails != null) {
            bluetoothPrintTemplate =
                ticketDetailsComposeViewModel.dropOffDetails.value.address?.let {
                    bluetoothPrintTemplate?.replace(
                        "DROPPING_POINT", it
                    )
                }
        }
        bluetoothPrintTemplate =
            ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
                bluetoothPrintTemplate?.replace(
                    "CONTACT_PERSON", it
                )
            }
        getContactNumberPrint()
        getTicketBookedByPrint()
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.busType?.let {
            bluetoothPrintTemplate?.replace(
                "COACH_TYPE", it
            )
        }
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("REMARKS", ticketDetailsComposeViewModel.remarks ?: "-")
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "TERMINAL_ID", ticketDetailsComposeViewModel.terminalRefNo ?: ""
        )
        if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("TERMINAL_PULOGABANG", "")
        }
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("CURRENT_DATE", getTodayDate())

        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("CURRENT_TIME", getTodayDateWithTime())


        if (bluetoothPrintTemplate?.contains("PASSENGER_EACH_NAME") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PASSENGER_EACH_NAME",
                ticketDetailsComposeViewModel.passengerDetails!![0]?.name ?: ""
            )
        }

        if (bluetoothPrintTemplate?.contains("SEAT_EACH_NUMBER") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "SEAT_EACH_NUMBER",
                ticketDetailsComposeViewModel.passengerDetails!![0]?.seatNumber ?: ""
            )
        }

        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_NUMBER") == true) {
            bluetoothPrintTemplate =
                if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel.passengerDetails!!.isNotEmpty()) {
                    var mealCoupons = ""
                    ticketDetailsComposeViewModel.passengerDetails?.forEach {
                        mealCoupons += it?.mealCoupons.toString().replace("[", "").replace("]", "")
                            .replace(",", "\n").replace(" ", "")
                        mealCoupons += "\n"
                    }
                    bluetoothPrintTemplate?.replace(
                        "MEAL_COUPON_NUMBER", mealCoupons
                    )
                } else {
                    bluetoothPrintTemplate?.replace(
                        "MEAL_COUPON_NUMBER", "-"
                    )
                }

        }

        if (bluetoothPrintTemplate?.contains("MEAL_COUNT") == true && !ticketDetailsComposeViewModel.passengerDetails.isNullOrEmpty()) {
            var mealCouponCount = 0
            ticketDetailsComposeViewModel.passengerDetails?.forEach {
                mealCouponCount += it?.mealCoupons?.size!!
            }
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "MEAL_COUNT", mealCouponCount.toString()
            )
        }

        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_LOOP") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "MEAL_COUPON_LOOP", ""
            )
        }

        if (bluetoothPrintTemplate?.contains("MEAL_TYPE") == true) {
            bluetoothPrintTemplate =
                if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel.passengerDetails!!.isNotEmpty() && ticketDetailsComposeViewModel.passengerDetails!!.any { it?.selectedMealType != "-" }) {
                    var mealTypes = ""
                    ticketDetailsComposeViewModel.passengerDetails?.forEach {
                        mealTypes += it?.selectedMealType.toString()
                        mealTypes += ","
                    }
                    bluetoothPrintTemplate?.replace(
                        "MEAL_TYPE", mealTypes.removeSuffix(",")
                    )
                } else {
                    bluetoothPrintTemplate?.replace(
                        "MEAL_TYPE", "-"
                    )
                }

        }
        if (bluetoothPrintTemplate?.contains("ALL_INSURANCE_NUMBERS") == true && ticketDetailsComposeViewModel?.insuranceTransDetails?.value?.details != null) {
            var allInsuranceNumbers = ""
            ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.forEach {
                allInsuranceNumbers += "${getString(R.string.seat)} (${getString(R.string.booking_code)}): ${it?.seat_no} (${it?.info?.booking_code}) ${
                    getString(
                        R.string.policy
                    )
                }: ${it?.info?.policy_number}\n"
            }
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "ALL_INSURANCE_NUMBERS", allInsuranceNumbers
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


        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("CURRENT_DATE", getTodayDate())!!
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("CURRENT_TIME", getTodayDateWithTime())!!

        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("TAB_SPACE", " ")!!

        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("BAR_CODE", " ")!!

        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("BOLD_ON", "")!!
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("+", "")!!
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("ALIGN_LEFT|", "")!!
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("ALIGN_CENTER|", "")!!
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("NEW_LINE", "\n")!!
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("ONE_SPACE", "")!!


        printerInterface = EPrinterImplementation.getInstance()
        printerInterface!!.init(this)
        *//* if (printerInterface!!.isPrinterSupported()) {
             Log.v("tag", "printer is supported")
         } else {
             Log.v("tag", "printer NOT supported")
         }*//*

        if (printerInterface != null) {
            Thread {
                printerInterface!!.printText(
                    bluetoothPrintTemplate
                ) { event, data ->
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            data.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                        // handle the update print count api here
                        if (!ticketNumber.isNullOrEmpty()) {
                            ticketDetailsComposeViewModel.updatePrintCountApi(
                                ticketNumber ?: "",
                                true,
                                loginModelPref.api_key
                            )
                        }
                    }
                }
            }.start()
        }

    }*/


    private fun getLandmarkPrint() {
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.boardingDetails.value.landmark?.let {
            bluetoothPrintTemplate?.replace(
                "LANDMARK", it
            )
        } ?: "-"
    }

    private fun getOperatorNamePrint() {
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "OPERATOR_NAME", privilegeResponseModel.operatorName
        )
    }

    private fun getWebAddressPrint() {
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "WEB_ADDRESS", privilegeResponseModel.webAddressUrl
        )
    }

    @SuppressLint("MissingPermission")
    private fun enableDeviceBluetooth() {
        try {
            val bluetoothManager: BluetoothManager? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.getSystemService(BluetoothManager::class.java)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getSystemService(BluetoothManager::class.java)
                    } else {
                        null
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

                val savedBluetoothPrinter = PreferenceUtils.getPreference(PREF_BLUETOOTH_DEVICE, "")
                if (items.contains(savedBluetoothPrinter)) {
                    Handler().postDelayed(kotlinx.coroutines.Runnable {
                        val savedPrinterIndex =
                            bluetoothDevicesList.indexOfFirst { it.device.name == savedBluetoothPrinter }
                        if (savedPrinterIndex != -1) {
                            selectedDevice = bluetoothDevicesList[savedPrinterIndex]
                            //privilegeResponseModel?.availableAppModes?.allowReprint = true
                            if (isFirstPrint) printBluetooth()
                            else {
                                if (::privilegeResponseModel.isInitialized && privilegeResponseModel?.availableAppModes?.allowReprint == true) {
                                    printBluetooth()
                                } else toast(getString(R.string.not_allowed_to_reprint))
                            }
                        }
                    }, 1000)
                } else {
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
                                if (isFirstPrint) printBluetooth()
                                else {
                                    if (::privilegeResponseModel.isInitialized && privilegeResponseModel?.availableAppModes?.allowReprint == true) {
                                        printBluetooth()
                                    } else toast(getString(R.string.not_allowed_to_reprint))
                                }
                            }
                        }

                        val alert = alertDialog.create()
                        alert.setCanceledOnTouchOutside(true)
                        alert.show()
                    } else toast(getString(R.string.no_paired_devices))
                }
            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    private fun initializePinelab() {
        val intent = Intent()
        intent.setAction(PLUTUS_SMART_ACTION)
        intent.setPackage(PLUTUS_SMART_PACKAGE)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun getContactNumberPrint() {
        bluetoothPrintTemplate =
            ticketDetailsComposeViewModel.boardingDetails.value.contactNumbers?.let {
                bluetoothPrintTemplate?.replace(
                    "CONTACT_NUMBER_PERSON", it
                )
            }
    }

    private fun getTicketBookedByPrint() {
        bluetoothPrintTemplate = ticketDetailsComposeViewModel.ticketBookedBy.let {
            bluetoothPrintTemplate?.replace(
                "TICKET_BOOKED_BY",
                it.substringBefore(",")
            )
        }
    }

    private fun printBluetooth() {
        isFirstPrint = false
        AsyncBluetoothEscPosPrint(this, object : AsyncEscPosPrint.OnPrintFinished() {
            override fun onError(
                asyncEscPosPrinter: AsyncEscPosPrinter?, codeException: Int
            ) {
                Timber.d(
                    "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                )
            }

            override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                if (ActivityCompat.checkSelfPermission(
                        this@TicketDetailsActivityCompose, Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                PreferenceUtils.setPreference(
                    PREF_BLUETOOTH_DEVICE, selectedDevice?.device?.name
                )
                lifecycleScope.launch {
                    if (!ticketNumber.isNullOrEmpty()) {
                        if (isNetworkAvailable()) {
                            ticketDetailsComposeViewModel.updatePrintCountApi(
                                ticketNumber ?: "",
                                true,
                                loginModelPref.api_key
                            )
                            delay(1500L)
                        } else {
                            noNetworkToast()
                        }
                    }
                }
                Timber.d(
                    "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                )
            }
        }).execute(this.getAsyncEscPosPrinter(selectedDevice))
    }

    private fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        if (bmpLogo != null) {
            hexaDecimalString = PrinterTextParserImg.bitmapToHexadecimalString(
                printer, bmpLogo
            )

            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "IMAGE", "[C]<img>${
                    hexaDecimalString
                }</img>\n"
            )
        }

        if (bmpQrCode != null) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "BOARDING_QR", "[C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer, bmpQrCode
                    )
                }</img>"
            )
        }

        if (insuranceBitmap != null && ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.isNotEmpty() == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "INSURANCE_QRCODE", "[C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer, insuranceBitmap
                    )
                }</img>"
            )
        } else {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "INSURANCE_QRCODE", ""
            )
        }




        if (bmpQrCode != null) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "BOARDING_QR", "[C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer, bmpQrCode
                    )
                }</img>"
            )
        }



        if (terminalQrBitmap != null) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "TERMINAL_REF_QR_CODE", "[C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer, terminalQrBitmap
                    )
                }</img>"
            )
        }


        getBarCodePrint(printer)
        return printer.addTextToPrint(
            bluetoothPrintTemplate?.trim()
        )
    }

    private fun getBarCodePrint(printer: AsyncEscPosPrinter) {
        if (generateBarCode() != null && bluetoothPrintTemplate?.contains("BAR_CODE") == true && ticketDetailsComposeViewModel.isAllowToPrintBarcode && ticketDetailsComposeViewModel.barcodeValue != null) {
            hexaDecimalString = PrinterTextParserImg.bitmapToHexadecimalString(
                printer, generateBarCode()
            )

            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "BAR_CODE", "[C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer, generateBarCode()
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
            return barcodeEncoder.encodeBitmap(
                "$ticketDetailsComposeViewModel.barcodeValue", BarcodeFormat.CODE_128, 40, 80
            )
        } catch (e: java.lang.Exception) {
            Timber.d("exceptionMsg ${e.message} ")
        }
        return null
    }

    private class IncomingHandler(ticketDetailsActivityCompose: Context) : Handler() {
        private val BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE"
        val context = ticketDetailsActivityCompose
        override fun handleMessage(msg: Message) {
            val bundle = msg.data
            val value = bundle.getString(BILLING_RESPONSE_TAG)
            val json = JSONObject(value!!)
            val text = json.getJSONObject("Response").getString("ResponseMsg")
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
            Timber.e("Value : ${value.toString()}")
            // process the response Json as required.
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("LongLogTag", "MissingPermission")
    override fun onActivityResult(
        mRequestCode: Int, mResultCode: Int, mDataIntent: Intent?
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

    private fun callUpdatePrintCountApi() {
        lifecycleScope.launch {
            if (isNetworkAvailable()) {
                ticketDetailsComposeViewModel.updatePrintCountApi(
                    ticketNumber ?: "",
                    true,
                    loginModelPref.api_key
                )
                delay(1500L)
            } else {
                noNetworkToast()
            }
        }
    }

    private fun callSendSMSEmailApi(type: String) {
        val bccId = PreferenceUtils.getBccId()

        val reqBody = ReqBody(
            ticketNumber ?: "", type, locale = locale,
            api_key = if (privilegeResponseModel.country.equals("india", true)) {
                loginModelPref.api_key
            } else {
                null
            } ?: "",
        )

        if (isNetworkAvailable()) {

            ticketDetailsViewModel.sendSMSEmailApi(
                reqBody, send_sms_email_method_name
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
                            //openUnauthorisedDialog()

                            showUnauthorisedDialog()

                        }

                        else -> {
                            if (it.message != "null") it.message?.let { it1 -> toast(it1) }
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

    private fun setPaymentOptions(): MutableList<SearchModel> {
        val paymentOptionsList = mutableListOf<SearchModel>()
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel?.allowCashCreditOptionsInBooking != null && privilegeResponseModel.allowCashCreditOptionsInBooking) {
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

        if (ticketDetailsComposeViewModel.passengerDetails.isNotEmpty()) {
            val passList = ticketDetailsComposeViewModel.passengerDetails
            for (i in 0 until passList?.size!!) {
                selectedSeatNumber.append(passList[i]?.seatNumber)
                if (i < passList.size - 1) {
                    selectedSeatNumber.append(",")
                }
            }

            if (isNetworkAvailable()) {
                val reqBody = com.bitla.ts.domain.pojo.cancellation_details_model.request.ReqBody2(
                    apiKey = loginModelPref.api_key,
                    cancelType = selectedCancellationType,
                    isFromBusOptApp = true,
                    locale = locale,
                    operatorApiKey = operator_api_key,
                    passengerDetails = "",
                    pnrNumber = ticketNumber ?: "",
                    responseFormat = json_format,
                    seatNumbers = selectedSeatNumber.toString(),
                    zeroPercent = false,
                    isBimaTicket = ticketDetailsComposeViewModel.isBimaTicket,
                    json_format = json_format
                )

                cancelTicketViewModel.getZeroCancellationDetailsApi(
                    reqBody,
                    loginModelPref.api_key
                )
            }
        } else
            noNetworkToast()
    }

    private fun payPendingAmountDirectApi() {
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
                        ticketDetailsComposeViewModel.sideBarOptionsList.clear()

                        if (isNetworkAvailable()) {
                            ticketDetailsComposeViewModel.showTicketDetailsSideBarMenu = false
                            callTicketDetailsApi(false)
                        } else
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

    private fun setCancelTicketObserver() {
        cancelTicketViewModel.cancellationDetailsResponse.observe(this) {
            Timber.d("messageResult-${it}")
            progressDialog.dismiss()
            if (it != null) {
                if (it.code == 200) {
                    openCancelConfirmDialog(
                        it.result.cancelledFare.toString(),
                        it.result.refundAmount.toString(),
                        it.result.cancelPercent.toString()
                    )
                    Timber.d("messageResult-${it.result}")
                } else if (it.code == 401) {
                    //openUnauthorisedDialog()

                    showUnauthorisedDialog()

                } else {
                    if (it?.message != null) {
                        it?.message?.let { it -> toast(it) }
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
                cancellationChargesRupee = privilegeResponseModel.currency
                cancellationChargesPercentage = ""

            }

            "" -> {
                cancellationChargesPercentage = "%"
                cancellationChargesRupee = ""
            }
        }
        val informPassengersAboutCancellation =
            getString(R.string.selected_seat_s_will_be_cancelled)

        val source = ticketDetailsComposeViewModel.origin
        val destination = ticketDetailsComposeViewModel.destination
        DialogUtils.ticketCancelDialog(
            context = this,
            title = getString(R.string.cancel_tickets),
            message = informPassengersAboutCancellation,
            srcDest = "$source - $destination",
            journeyDate = ticketDetailsComposeViewModel.busType,
            ticketCancellationPercentage = "$cancellationChargesRupee$cancelPercent$cancellationChargesPercentage",
            seatNo = selectedSeatNumber.toString(),
            cancellationAmount = "${privilegeResponseModel.currency}${
                cancellationAmountX.toDouble().convert(privilegeResponseModel.currencyFormat)
            }",
            refundAmount = "${privilegeResponseModel.currency}${
                refundAmountX.toDouble().convert(privilegeResponseModel.currencyFormat)
            }",
            buttonLeftText = getString(R.string.goBack),
            buttonRightText = getString(R.string.confirm_cancellation),
            dialogButtonTagListener = object : DialogButtonTagListener {
                override fun onLeftButtonClick(tag: View?) {

                }

                override fun onRightButtonClick(tag: View?) {
                    cancelOtpLayoutDialogOpenCount = 0
                    DialogPinAuth()
                }

            }
        )
    }

    private fun DialogPinAuth() {
        if (shouldTicketCancellation && privilegeResponseModel.country.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = this@TicketDetailsActivityCompose,
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


    private fun setCancelPartialOtpTicketObserver() {
        cancelTicketViewModel.cancelPartialTicketViewModel.observe(this) {
            // sheetTicketCancellationBinding.includeProgress.progressBar.gone()

            if (it != null) {
                if (it.code == 200) {
                    DialogUtils.successfulMsgDialog(
                        this, getString(R.string.successfully_cancelled_ticket)
                    )

                    if (loginModelPref.role == getString(R.string.role_field_officer)) {
                        otpDialog?.dismiss()
                        bottomSheet?.dismiss()
                        Handler(Looper.getMainLooper()).postDelayed({
                            intent = Intent(this, DashboardNavigateActivity::class.java)
                            intent.putExtra("newBooking", true)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }, 1000)
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this, TicketDetailsActivityCompose::class.java)
                            intent.putExtra(
                                getString(R.string.put_extra_cancelTicket),
                                getString(R.string.put_extra_cancelTicket)
                            )
                            intent.putExtra(getString(R.string.TICKET_NUMBER), ticketNumber)
                            this.startActivity(intent)
                        }, 2000)
                    }
                    //bottomSheetDialog.dismiss()
                } else if (it.result?.key?.isNotEmpty() == true) {
                    if (cancelOtpLayoutDialogOpenCount == 0) {
                        DialogUtils.cancelOtpLayoutDialog(this,
                            object : DialogSingleButtonListener {
                                override fun onSingleButtonClick(str: String) {
                                    if (str == "resend") {
                                        if (ticketDetailsComposeViewModel.isBimaTicket) {
                                            callReleaseBimaTicketApi()
                                        } else {
                                            authPinPhoneReleaseDialog()
                                        }
                                    } else {
                                        cancelOtp = str
                                        if (::progressDialog.isInitialized)
                                            progressDialog.show()
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

                            }
                        )
                        cancelOptkey = it.result?.key.toString()
                        toast(it.result?.message.toString())
                    }
                    cancelOptkey = it.result?.key.toString()

                } else {
                    if (it?.message != null)
                        toast(it.message)
                    it.result?.message?.let { it1 -> toast(it1) }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun callCancelPartialTicketApi(authPin: String) {
        if (isNetworkAvailable()) {
            val reqBody = com.bitla.ts.domain.pojo.cancel_partial_ticket_model.request.ReqBody(
                apiKey = loginModelPref.api_key,
                cancelType = selectedCancellationType,
                isFromBusOptApp = true,
                locale = locale,
                operatorApiKey = operator_api_key,
                passengerDetails = "",
                responseFormat = json_format,
                seatNumbers = selectedSeatNumber.toString(),
                ticketCancellationPercentageP = "",
                ticketNumber = ticketNumber ?: "",
                travelDate = ticketDetailsComposeViewModel.travelDate,
                zeroPercent = false,
                isOnbehalfBookedUser = false,
                onbehalf_online_agent_flag = false,
                onBehalfUserId = null,
                json_format = json_format,
                is_sms_send = false,
                isBimaTicket = ticketDetailsComposeViewModel.isBimaTicket,
                authPin = authPin,
                remarkCancelTicket = ""
            )

            cancelTicketViewModel.getCancelPartialTicketApi(
                reqBody,
                loginModelPref.api_key
            )

        } else
            noNetworkToast()
    }

    private fun callConfirmOtpReleasePhoneBlockTicketApi() {
        val remarks = if(::_sheetReleaseTicketsBinding.isInitialized) {
            _sheetReleaseTicketsBinding.edtRemarks.text.toString()
        } else {
            ""
        }

        val reqBody =
            com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.ReqBody(
                apiKey = loginModelPref.api_key,
                isFromMiddleTier = true,
                key = cancelOptkey,
                otp = cancelOtp,
                pnrNumber = ticketNumber.toString(),
                remarks = remarks,
                ticket = com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.Ticket(
                    selectedSeatNumber.toString()
                )
            )

        cancelTicketViewModel.getConfirmOtpReleasePhoneBlockTicketApi(
            reqBody,
            cancellation_details_ticket_method_name
        )
    }

    private fun setConfirmOtpReleaseObserver() {

        cancelTicketViewModel.confirmOtpReleasePhoneBlockTicketResponse.observe(this) {
            progressDialog?.dismiss()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        otpDialog?.dismiss()
                        DialogUtils.successfulMsgDialog(this, it.message)
                        bottomSheet?.dismiss()
                        progressDialog?.dismiss()
                        Handler(Looper.getMainLooper()).postDelayed({
                            intent = Intent(this, DashboardNavigateActivity::class.java)
                            intent.putExtra("newBooking", true)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }, 2100)


                    }

                    422 -> {
                        if (it?.message != null) {
                            toast("${it.message}")
                        }
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        progressDialog?.dismiss()

                    }

                    401 -> {
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        progressDialog?.dismiss()
                        // openUnauthorisedDialog()

                        showUnauthorisedDialog()

                    }

                    else -> {
                        toast(it.message)
                        _sheetReleaseTicketsBinding.progressBarRelease.gone()
                        progressDialog?.dismiss()
                    }
                }

            } else {
                toast(getString(R.string.server_error))
                cancelOtp = ""
            }
        }
    }

    private fun dismissProgressBar() {
        _sheetReleaseTicketsBinding.progressBarRelease.gone()
    }

    private fun authPinPhoneReleaseDialog() {
        if (phoneBlockingRelease && privilegeResponseModel.country.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = this@TicketDetailsActivityCompose,
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
        }
    }

    private fun callReleaseTicketApi(authPin: String) {
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

    private fun setReleaseTicketObserver(bottomSheetDialoge: BottomSheetDialog) {

        cancelOtpLayoutDialogOpenCount = 0

        dashboardViewModel.releaseTicketResponseViewModel.observe(this) { it ->
            if (it != null && it.code == 200) {
                _sheetReleaseTicketsBinding.progressBarRelease.gone()

                if (it.key?.isNotEmpty() == true) {
                    if (cancelOtpLayoutDialogOpenCount == 0) {
                        DialogUtils.cancelOtpLayoutDialog(
                            this,
                            object : DialogSingleButtonListener {
                                override fun onSingleButtonClick(str: String) {
                                    if (str == "resend") {
                                        if (ticketDetailsComposeViewModel.isBimaTicket) {
                                            callReleaseBimaTicketApi()
                                        } else {
                                            authPinPhoneReleaseDialog()
                                        }
                                    } else {
                                        cancelOtp = str
                                        if (::progressDialog.isInitialized)
                                            progressDialog.show()
                                        callConfirmOtpReleasePhoneBlockTicketApi()
                                    }
                                }
                            },
                            object : DialogReturnDialogInstanceListener {
                                override fun onReturnInstance(dialog: Any) {
                                    otpDialog = dialog as AlertDialog
                                }
                            },
                            dimissAction = {})
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

    private fun releaseTicket() {
        bottomSheetDialoge = BottomSheetDialog(this, R.style.BottomSheetDialog)
        _sheetReleaseTicketsBinding = SheetReleaseTicketsBinding.inflate(LayoutInflater.from(this))
        bottomSheetDialoge.setContentView(_sheetReleaseTicketsBinding.root)

        bottomSheet = bottomSheetDialoge

        progressDialog = ProgressDialog(this)


        selectedSeatNumber.clear()
        currentCheckedItem.clear()

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
                if (ticketDetailsComposeViewModel.isBimaTicket) {
                    callReleaseBimaTicketApi()
                } else {
                    authPinPhoneReleaseDialog()
                }
                setReleaseTicketObserver(bottomSheetDialoge)
                releaseTicketBtnClickCount++
            }
        }

        setConfirmOtpReleaseObserver()
        setReleaseTicketPassengerAdapter()
        bottomSheetDialoge.show()
    }

    private fun setReleaseTicketPassengerAdapter() {
        _sheetReleaseTicketsBinding.rvPassengers.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        _sheetReleaseTicketsBinding.rvPassengers.adapter = ReleaseTicketPassengersListAdapterPhase3(
            this,
            ticketDetailsComposeViewModel.passengerDetails.toMutableList(),
            onItemCheck = { item ->
                currentCheckedItem.add(item)
                Timber.d("seat:added-$currentCheckedItem")
            },
            onItemUncheck = { item ->
                currentCheckedItem.remove(item)
                if (currentCheckedItem.size == 0) {
                    currentCheckedItem.clear()
                    selectedSeatNumber.clear()

                }
                selectedSeatNumber.clear()
                Timber.d("seat:removed-$currentCheckedItem")
            }
        )
    }

    // In your Compose UI
    fun showHideMenuFromOtherFragmentObserver() {

           ticketDetailsComposeViewModel.showHideMenuFromOtherFragment.observe(this) {
               ticketDetailsComposeViewModel.showTicketDetailsSideBarMenu = it
               callTicketDetailsApi(false)

           }



    }

    private fun payAtBus() {

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://$domain/")
            //.addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


        val apiInterface: ApiInterface = retrofit.create(ApiInterface::class.java)


        val payAtBusCall = apiInterface.confirmPayAtBUs(
            ticketNumber.toString(), loginModelPref.api_key, locale
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
                            if (!response.body()?.response?.message.isNullOrEmpty()) {
                                ticketDetailsComposeViewModel.showTicketDetailsSideBarMenu = false
                                callTicketDetailsApi(false)
                                toast(response.body()?.response?.message)
                            }
                        } else {
                            if (response.body()?.ticket_status.equals(getString(R.string.seat_booked))) {
                                ticketDetailsComposeViewModel.showTicketDetailsSideBarMenu = false
                                callTicketDetailsApi(false)
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


    private fun isPassengerMobileEmpty(mobileNumber: String): Boolean {
        return mobileNumber.length <= 4
    }
}