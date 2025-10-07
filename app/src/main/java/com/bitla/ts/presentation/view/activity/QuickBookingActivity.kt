package com.bitla.ts.presentation.view.activity

import android.app.*
import android.content.*
import android.content.res.*
import android.os.*
import android.view.*
import androidx.core.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import androidx.transition.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.book_ticket.request.*
import com.bitla.ts.domain.pojo.book_ticket.request.ReqBody
import com.bitla.ts.domain.pojo.book_ticket_full.request.*
import com.bitla.ts.domain.pojo.booking.PayGayType
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.rapid_booking.request.*
import com.bitla.ts.domain.pojo.redelcom.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getBccId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getDestination
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getDestinationId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getSource
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getSourceId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getTravelDate
import com.bitla.ts.utils.showToast
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import okhttp3.*
import org.koin.androidx.viewmodel.ext.android.*
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.*
import toast
import visible
import java.util.*
import javax.crypto.*
import javax.crypto.spec.*


class QuickBookingActivity : BaseActivity(), OnItemClickListener, DialogSingleButtonListener,
    DialogButtonAnyDataListener {

    companion object {
        val tag: String = QuickBookingActivity::class.java.simpleName
    }

    private var isHandlerRunning = false
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 1500
    private var allowBpDpFare: Boolean? = false
    private var boardingAtId: String = ""
    private var dropOffId: String = ""
    private var srcDest: String? = null
    private lateinit var paymentOptionsList: MutableList<PayGayType>
    var noOfSeats = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val bookTicketViewModel by viewModel<BookTicketViewModel<Any?>>()
    private var terminalId: String = ""
    private val redelcomViewModel by viewModel<RedelcomViewModel<Any?>>()
    private var pnrNumber = ""
    private var toolbarTitle: String = ""
    private var redelcomPaymentDialog: AlertDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private var bookingDetails = BookingDetails()
    private var bccId: Int? = 0
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String? = ""
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var resId: Long = 0L
    private var isFromBusOptApp = "true"
    private var isRapidBooking = "true"
    private lateinit var binding: LayoutQuickBookingDetailsBinding
    private var boardingPoint: String = ""
    private var boardingDate: String = ""
    private var boardingTime: String = ""
    private var droppingPoint: String = ""
    private var droppingDate: String = ""
    private var droppingTime: String = ""
    private var serviceNumber: String = ""
    private var busType: String? = null
    private var deptTime: String? = null
    private var arrTime: String? = null
    private lateinit var droppingStageDetail: StageDetail
    private lateinit var boardingStageDetail: StageDetail

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var currency: String = ""
    private var currencyFormat: String = ""
    private var locale: String? = ""
    private var motCoupon: String? = ""
    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("SEATS")) {
            try {
                noOfSeats = intent.getStringExtra("SEATS")!!.toInt()
            } catch (e: Exception) {
            }
        }
        if (intent.hasExtra("motCoupon")) {
            motCoupon = intent.getStringExtra("motCoupon")
        }

        bccId = getBccId()
        source = getSource()
        sourceId = getSourceId()
        destination = getDestination()
        destinationId = getDestinationId()
        travelDate = getTravelDate()
        locale = PreferenceUtils.getlang()

        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!
        if (PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER) != null)
            loginModelPref = PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER)!!
        if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
            droppingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)!!
            droppingPoint = droppingStageDetail.name!!
            droppingTime = droppingStageDetail.time!!
            droppingDate = droppingStageDetail.travelDate ?: ""
            dropOffId = droppingStageDetail.id.toString()
        }
        if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
            boardingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)!!
            boardingPoint = boardingStageDetail.name!!
            boardingDate = boardingStageDetail.travelDate ?: ""
            boardingTime = boardingStageDetail.time!!
            boardingAtId = boardingStageDetail.id.toString()
        }

        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            deptTime = result?.dep_time ?: getString(R.string.empty)
            arrTime = result?.arr_time ?: getString(R.string.empty)
            busType = result?.bus_type ?: getString(R.string.empty)
            serviceNumber = result?.number ?: getString(R.string.empty)
        }

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
            currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(this, privilegeResponseModel.currencyFormat)
            allowBpDpFare = privilegeResponseModel.availableAppModes?.allowBpDpFare

            if (::binding.isInitialized && privilegeResponseModel.allowToShowWhatsappCheckboxInBookingPage)
                binding.layoutQuickBookingData.cardWhatsapp.visible() else
                binding.layoutQuickBookingData.cardWhatsapp.gone()
        }


        srcDest = "$source-$destination"
        // val subtitle = "${getDateDMYY(travelDate!!)} | $deptTime - $arrTime | $busType"
        val subtitle = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${travelDate?.let { getDateDMYY(it) }} $deptTime | $busType"
        else
            "${travelDate?.let { getDateDMYY(it) }} $$deptTime | $busType"
        binding.toolbar.tvCurrentHeader.text = srcDest
        binding.toolbar.toolbarSubtitle.text = subtitle
        binding.layoutQuickBookingData.tvMobileNumber.text = loginModelPref.phone_number
        binding.layoutQuickBookingData.layoutBoardingDropping.tvBoarding.text = boardingPoint


        if (boardingDate.isNotEmpty())
            binding.layoutQuickBookingData.layoutBoardingDropping.tvBoardingDate.text =
                inputFormatToOutput(
                    boardingDate,
                    DATE_FORMAT_D_M_Y_SLASH,
                    DATE_FORMAT_D_M_YY
                )
        binding.layoutQuickBookingData.layoutBoardingDropping.tvBoardingTime.text = boardingTime
        binding.layoutQuickBookingData.layoutBoardingDropping.tvDropping.text = droppingPoint
        if (droppingDate.isNotEmpty())
            binding.layoutQuickBookingData.layoutBoardingDropping.tvDroppingDate.text =
                inputFormatToOutput(
                    droppingDate,
                    DATE_FORMAT_D_M_Y_SLASH,
                    DATE_FORMAT_D_M_YY
                )
        binding.layoutQuickBookingData.layoutBoardingDropping.tvDroppingTime.text = droppingTime

        if (motCoupon.isNullOrEmpty() || motCoupon == "") {
            callRapidBookingApi()
        }else{
            if (!motCoupon.isNullOrEmpty() && motCoupon != "") {
                var motCodeVal= ""
                var motDif= ""
                var motAmt= ""
                var motDst= ""

                if (intent.hasExtra("motCodeValue")) {
                    motCodeVal = intent.getStringExtra("motCodeValue")!!
                }
                if (intent.hasExtra("motDifference")) {
                    motDif = intent.getStringExtra("motDifference")!!
                }
                if (intent.hasExtra("motAmount")) {
                    motAmt = intent.getStringExtra("motAmount")!!
                }
                if (intent.hasExtra("motDiscount")) {
                    motDst = intent.getStringExtra("motDiscount")!!
                }

                binding.layoutQuickBookingData.apply {
                    motMainLayout.visible()
                    motCodeValue.text = motCodeVal
                    motCode.text = motCoupon
                    motDifference.text = motDif
                    motAmount.text = motAmt
                    motDiscount.text = motDst
                }
            }
        }
        //  setPaymentAdapter()
        setObserver()


        binding.layoutQuickBookingData.textTotalSeatsValue.text =
            noOfSeats.toString()
        binding.layoutQuickBookingData.textOriginValue.text =
            source ?: getString(R.string.notAvailable)
        binding.layoutQuickBookingData.textDestinationValue.text =
            destination ?: getString(R.string.notAvailable)
        binding.layoutQuickBookingData.textTravelDateValue.text =
            getDateDMYY(travelDate!!) ?: getString(R.string.notAvailable)
    }

    override fun onPause() {
        if (isHandlerRunning) {
            handler.removeCallbacks(runnable!!)
            isHandlerRunning = false
        }
        super.onPause()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun setObserver() {
        bookTicketViewModel.dataBookTicket.observe(this, Observer {
            binding.includeProgress.progressBar.gone()

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
                        } else {

                            if(privilegeResponseModel.rapidBookingWithMotCouponInTsApp == true) {

                                callTicketDetailsApi(it.result.ticket_number)
                            } else {

//                                val intent = if(privilegeResponseModel.country.equals("India", true) || privilegeResponseModel.country.equals("Indonesia", true)) {
//                                    Intent(this, TicketDetailsActivityCompose::class.java)
//                                } else {
//                                    Intent(this, TicketDetailsActivity::class.java)
//                                }


                                val intent=   Intent(this, TicketDetailsActivityCompose::class.java)

                                intent.putExtra(
                                    getString(R.string.TICKET_NUMBER),
                                    it.result.ticket_number
                                )
                                startActivity(intent)
                                finish()
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
                        toast(it.message.toString())
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        })

        redelcomViewModel.redelComData.observe(this) {
            if (it.code == 200) {
                handler.removeCallbacks(runnable!!)
                isHandlerRunning = false
//                val intent = if(privilegeResponseModel.country.equals("India", true) || privilegeResponseModel.country.equals("Indonesia", true)) {
//                    Intent(this, TicketDetailsActivityCompose::class.java)
//                } else {
//                    Intent(this, TicketDetailsActivity::class.java)
//                }

                val intent=Intent(this, TicketDetailsActivityCompose::class.java)

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
                DialogUtils.transactionFailedDialog(this, it.message)


            }

        }


        bookTicketViewModel.rapidBooking.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        var totalFare = it.result.total_fare
                        totalFare = "${
                            if (totalFare.toString().isNotEmpty()) (totalFare.toString()
                                .toDouble()).convert(currencyFormat) else totalFare
                        }"
                        val collectTotal =
                            "${getString(R.string.collect)} $currency $totalFare"
                        binding.buttonCollect.text = collectTotal
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
//                        toast(it.message)
                    }
                }
            } else {
//                toast(getString(R.string.server_error))
            }
        }

        ticketDetailsViewModel.dataTicketDetails.observe(this) {
            binding.includeProgress.progressBar.gone()

            if (it != null) {

                when {
                    it.code == 200 && it.success -> {
                        if (privilegeResponseModel.rapidBookingWithMotCouponInTsApp == true) {
                            redelcomPrintdataSet(it.body)
                            DialogUtils.quickBookBookingConfirmedDialogMOT(
                                "1 ${getString(R.string.tickets)}",
                                this,
                                onLeftButtonClick = {

                                    val intent =
                                        Intent(
                                            this@QuickBookingActivity,
                                            BusDetailsActivity::class.java
                                        )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    startActivity(intent)
                                    finish()
                                },

                                onRightButtonClick = {
                                    val intent =
                                        Intent(
                                            this@QuickBookingActivity,
                                            MotCouponActivity::class.java
                                        )
                                    startActivity(intent)
                                    finish()
                                }
                            )
                        }
                    }

                    it.code == 401 -> {
                       /* DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }
                    else -> {
                        if (it.message?.isNotEmpty() == true) {
                            it.message.let { it1 -> toast(it1) }

                        } else {
                            toast(getString(R.string.server_error))

                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun callRapidBookingApi() {
        var tempMot: String? = null
        if (!motCoupon.isNullOrEmpty() && motCoupon != "") {
            tempMot = motCoupon
        }
        if (isNetworkAvailable()) {
            binding.includeProgress.progressBar.visible()

            val seatCount = SeatCount(noOfSeats)
            if (allowBpDpFare != null && allowBpDpFare == false) {
                val reqBody = com.bitla.ts.domain.pojo.rapid_booking.request.ReqBody(
                    api_key = loginModelPref.api_key,
                    destination = destinationId,
                    locale = locale,
                    no_of_seats = noOfSeats,
                    mot_coupon = tempMot,
                    origin = sourceId,
                    res_id = resId.toString(),
                    seat_count = seatCount,
                    is_offline_booked_tic = true

                )



                bookTicketViewModel.rapidBookingApi(
                    reqBody,
                    rapid_booking_method_name
                )

            } else {
                val reqBody = ReqBody1(
                    loginModelPref.api_key,
                    destinationId,
                    locale,
                    noOfSeats,
                    sourceId,
                    resId.toString(),
                    seatCount,
                    boarding_at = boardingAtId,
                    drop_off = dropOffId,
                    is_offline_booked_tic = true
                )

                bookTicketViewModel.rapidBookingApi(
                    reqBody,
                    rapid_booking_method_name
                )
            }
        } else
            noNetworkToast()
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun initUI() {
        binding = LayoutQuickBookingDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        lifecycleScope.launch {
            bookTicketViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }

        lifecycleScope.launch {
            ticketDetailsViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            redelcomViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
        setPaymentOptionsAdapter()
        clickListener()



    }

    private fun setPaymentOptionsAdapter() {
        val searchList = mutableListOf<SearchModel>()
        val searchModel = SearchModel()
        searchList.add(searchModel)
        searchList.add(searchModel)
        searchList.add(searchModel)
        searchList.add(searchModel)

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.layoutQuickBookingData.rvPaymentOptions.layoutManager = layoutManager
        val filterAdapter =
            FilterAdapter(this, this, searchList, 0, true)
        binding.layoutQuickBookingData.rvPaymentOptions.adapter = filterAdapter
    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener(this)
        binding.layoutQuickBookingData.layoutBookingDetailsFixed.setOnClickListener(this)
        binding.buttonCollect.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.img_back -> {
                onBackPressed()
            }
            R.id.button_collect -> {

                if (isNetworkAvailable()) {
                    binding.includeProgress.progressBar.visible()

                    var tempMot: String? = null
                    if (!motCoupon.isNullOrEmpty() && motCoupon != "") {
                        tempMot = motCoupon
                    }

                    val reqBody = ReqBody(
                        loginModelPref.api_key,
                        destinationId,
                        mot_coupon = tempMot,
                        isFromBusOptApp,
                        isRapidBooking,
                        locale,
                        noOfSeats.toString(),
                        operator_api_key,
                        sourceId,
                        resId.toString(),
                        boarding_at = boardingAtId,
                        drop_off = dropOffId,
                        true,
                        true,
                        "hash",
                        bookingDetails
                    )
                    val bookTicketRequest = BookTicketRequest(
                        bccId.toString(),
                        format_type,
                        book_ticket_method_name,
                        reqBody
                    )

                    /* bookTicketViewModel.bookTicketApi(
                         loginModelPref.auth_token,
                         loginModelPref.api_key,
                         bookTicketRequest,
                         book_ticket_method_name
                     )*/
                    bookTicketViewModel.bookTicketApi(
                        reqBody,
                        book_ticket_method_name
                    )
                } else
                    noNetworkToast()
            }
            R.id.layoutBookingDetailsFixed -> {
                if (binding.layoutQuickBookingData.layoutBookingDetailsHidden.isVisible) {
                    TransitionManager.beginDelayedTransition(
                        binding.layoutQuickBookingData.cardBookingDetails,
                        AutoTransition()
                    )
                    binding.layoutQuickBookingData.layoutBookingDetailsHidden.gone()
                    binding.layoutQuickBookingData.imgBookingDetails.setImageResource(R.drawable.ic_arrow_down)
                    binding.layoutQuickBookingData.imgBookingDetails.imageTintList =
                        ColorStateList.valueOf((resources.getColor(R.color.white)))
                } else {
                    TransitionManager.beginDelayedTransition(
                        binding.layoutQuickBookingData.cardBookingDetails,
                        AutoTransition()
                    )
                    binding.layoutQuickBookingData.layoutBookingDetailsHidden.visible()
                    binding.layoutQuickBookingData.imgBookingDetails.setImageResource(R.drawable.ic_arrow_up)
                }
            }
        }
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

    override fun onDataSend(type: Int, file: Any) {
        when (type) {
            1 -> {
                countDownTimer = file as CountDownTimer
            }
            2 -> {
                redelcomPaymentDialog = file as AlertDialog
            }
        }
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {


    }


    private fun redelcomPrintdataSet(ticketData: com.bitla.ts.domain.pojo.ticket_details.response.Body) {
        /*val cDate = Date()
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy").format(cDate)
        val currentTime: String = SimpleDateFormat("HH:mm").format(cDate)
        var ticketPrint = " ";

        val bAddress = ticketData.origin
        val dAddress = ticketData.destination
        var usersList: MutableList<LoginModel>? = arrayListOf()
        if (PreferenceUtils.getObject<Users>(PREF_USER_LIST_STRING) != null) {
            val users = PreferenceUtils.getObject<Users>(PREF_USER_LIST_STRING)
            usersList = users!!.users
        }

        if (usersList?.get(0)?.redelcomData != null) {
            if (usersList[0].redelcomData?.is_redelcom_enabled == true) {

        val passengerNameList: ArrayList<String> = arrayListOf()
        val seatNoList: ArrayList<String> = arrayListOf()

        ticketData.passengerDetails?.forEach {
            passengerNameList.add(it?.name ?: "")
            seatNoList.add(it?.seatNumber ?: "")
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
                "{left}" + getString(R.string.TOTAL_UNDERSCORE_FARE) + ": " + "{right}" + ticketData.totalFare + "{br}" +
                "{left}" + getString(R.string.BOOKED_BY) + ": " + "{right}" + ticketData.booking_source + "{br}{br}" +

                "{left}" + getString(R.string.PASSENGER_DETAILS) + "{br}" +
                "{left}" + getString(R.string.NAME) + ": " + "{right}" + passengerNames + "{br}" +
                //"{left}" + getString(R.string.CONTACT_NUMBER) + ": " + "{right}" + ticketData.passenger_details[0].mobile + "{br}" +
                "{left}" + getString(R.string.SEATS) + ": " + "{right}" + seatsNumbers + "{br}{br}" +

                "{left}" + getString(R.string.PRINT_BY) + ": " + "{right}" + ownerName + "{br}" +
                //"{left}" + getString(R.string.CONTACT_NUMBER) + ": " + "{right}" + ownerContact + "{br}" +
                "{left}" + getString(R.string.DATE_AND_TIME) + ": " + "{right}" + currentDate + "," + "{right}" + currentTime +

                getString(R.string.NOTE_PRINT) + "{br}{br}{br}";


        val json = JSONObject()
        json.put("printText", ticketPrint)
        json.put("terminalId", usersList[0].redelcomData!!.terminalId)
        val body = ReqBodyPrint(ticketPrint, usersList[0].redelcomData!!.terminalId)
        val authCode =
            generateAuthCode(usersList[0].redelcomData!!.api_key, API_PRINT, json.toString())
        hitRedelcomPrintApi(authCode!!, body)
            }
        }
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

    private fun initRetrofit(): Retrofit? {
        val retrofit = Retrofit.Builder()
            .baseUrl(PreferenceUtils.getObject<RedelcomPreferenceData>(PREF_REDELCOM_DETAILS)!!.redelcom_uri)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    private fun callTicketDetailsApi(ticketNumber: String) {
        binding.includeProgress.progressBar.visible()

        ticketDetailsViewModel.ticketDetailsApi(
            loginModelPref.api_key,
            ticketNumber,
            jsonFormat = true,
            isQrScan = false,
            locale = locale ?: "en",
            apiType = ticket_details_method_name
        )
    }
}

