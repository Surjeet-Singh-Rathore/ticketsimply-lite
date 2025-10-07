package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.collection.arrayMapOf
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemCheckedMultipledataListner
import com.bitla.ts.data.rapid_booking_method_name
import com.bitla.ts.data.ticket_details_method_name
import com.bitla.ts.data.update_boarded_status_method_name
import com.bitla.ts.databinding.ActivityRapidMotBookingBinding
import com.bitla.ts.domain.pojo.available_routes.BoardingPointDetail
import com.bitla.ts.domain.pojo.available_routes.DropOffDetail
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.rapid_booking.request.SeatCount
import com.bitla.ts.domain.pojo.service_details_response.StageDetail
import com.bitla.ts.presentation.viewModel.BookTicketViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.presentation.viewModel.TicketDetailsViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.google.zxing.integration.android.IntentIntegrator
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.util.*

class MotCouponActivity : BaseActivity(), DialogSingleButtonListener,
    OnItemCheckedMultipledataListner {


    private lateinit var binding: ActivityRapidMotBookingBinding
    private var bpDpBoarding: MutableList<BoardingPointDetail> = mutableListOf()
    private var bpDpDropping: MutableList<DropOffDetail> = mutableListOf()
    private var busType = ""
    private var depTime = ""
    private var finalBoardingName = ""
    private var finalDroppingName = ""
    private var travelDte = ""
    private var resId = ""
    private var serviceNumber = ""
    lateinit var oldBoarding: BoardingPointDetail
    lateinit var oldDroping: DropOffDetail
    private var couponCodeScan = -1
    private lateinit var stageDetailItem: StageDetail
    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()
    private var source: String? = ""
    private var destination: String? = ""
    private var sourceId: String = ""
    private var destinationId: String = ""

    private var allowBpDpFare: Boolean? = false
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private val bookTicketViewModel by viewModel<BookTicketViewModel<Any?>>()
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = ""
    private var currency: String = ""
    private var currencyFormat: String = ""
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var bccId: Int? = 0
    private var qrSelectedPnrNumber = ""
    private var listSeatno = arrayListOf<String>()
    private var listSassName = arrayListOf<String>()
    private var tempMapList = arrayMapOf<String, String>()
    private var qrResponse = ""
    private var boardingScanningAllowed = false
    override fun initUI() {

        binding = ActivityRapidMotBookingBinding.inflate(layoutInflater)

        getPref()

        source = PreferenceUtils.getSource()
        sourceId = PreferenceUtils.getSourceId()
        destination = PreferenceUtils.getDestination()
        destinationId = PreferenceUtils.getDestinationId()
        val subtitle = if(resId.isNotEmpty())
            "${serviceNumber} | ${travelDte.let { getDateDMYY(it) }} $depTime | $busType"
        else
            "${travelDte.let { getDateDMYY(it) }} $$depTime | $busType"

        binding.includeHeader.apply {
            tvScan.gone()
            toolbarHeaderText.text = getString(R.string.rapid_mot_booking)
            toolbarSubtitle.text= "$source-$destination \n$subtitle"
            imgBack.setOnClickListener {
                onBackPressed()
            }
        }

        observer()
        updateBoardedStatusObserver()
        setTicketDetailsObserver()


        val view = binding.root
        setContentView(view)

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
            bookTicketViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
    }

    private fun getPref(){

        PreferenceUtils.getBoarding()!!.forEach {
            bpDpBoarding.add(it)
        }
        PreferenceUtils.getDropping()!!.forEach {
            bpDpDropping.add(it)
        }
        if (intent.hasExtra("busType")) {
            busType = intent.getStringExtra("busType")!!
        }
        if (intent.hasExtra("depTime")) {
            depTime = intent.getStringExtra("depTime")!!
        }
        if (intent.hasExtra("travelDate")) {
            travelDte = intent.getStringExtra("travelDate")!!
        }
        if (intent.hasExtra("resId12")) {
            resId = intent.getStringExtra("resId12")!!
        }
        if (intent.hasExtra("serviceNumber")) {
            serviceNumber = intent.getStringExtra("serviceNumber")!!
        }
        locale = PreferenceUtils.getlang()



        if (PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER) != null)
            loginModelPref = PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER)!!

        bccId = PreferenceUtils.getBccId()

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
            if(privilegeResponseModel.boLicenses?.AllowToScanAndUpdateTheBoardingStatusInRapidMOTBookingPageForTSApp== true){
                boardingScanningAllowed= true
            }
            currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(this,privilegeResponseModel.currencyFormat)
            allowBpDpFare = privilegeResponseModel.availableAppModes?.allowBpDpFare
        }
        if (boardingScanningAllowed){
            binding.boardingStatusTV.visible()
            binding.scanForBoardedStatus.visible()
        }else{
            binding.boardingStatusTV.gone()
            binding.scanForBoardedStatus.gone()
        }
    }

    private fun defaultBpDp() {
        if (bpDpBoarding.size == 1) {
            binding.etBoardingAt.setText(bpDpBoarding[0].name)
        }
        if (bpDpDropping.size == 1) {
            binding.etDropOffAt.setText(bpDpDropping[0].name)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        defaultBpDp()
        onClickListener()

    }
    private fun updateBoardedStatusObserver() {
        pickUpChartViewModel.updateBoardedStatusResponse.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                when (it.code) {
                    200 -> {
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
                        toast(it.result.message)
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }


    private fun onClickListener() {
        binding.etBoardingAt.setOnClickListener {
            val intent = Intent(this, InterBDActivity::class.java)
            intent.putExtra(getString(R.string.tag), getString(R.string.boarding))
            PreferenceUtils.putBoarding(bpDpBoarding)
            PreferenceUtils.putDropping(bpDpDropping)
            intent.putExtra(getString(R.string.bus_type), busType)
            intent.putExtra(getString(R.string.dep_time), depTime)
            intent.putExtra(
                getString(R.string.toolbar_title),
                getString(R.string.rapid_booking)
            )
            startActivity(intent)
        }
        binding.etDropOffAt.setOnClickListener {
            val intent = Intent(this, InterBDActivity::class.java)
            intent.putExtra(getString(R.string.tag), getString(R.string.dropping))
            PreferenceUtils.putBoarding(bpDpBoarding)
            PreferenceUtils.putDropping(bpDpDropping)
            intent.putExtra(getString(R.string.bus_type), busType)
            intent.putExtra(getString(R.string.dep_time), depTime)
            intent.putExtra(
                getString(R.string.toolbar_title),
                getString(R.string.rapid_booking)
            )
            startActivity(intent)

        }

        binding.confirmTV.setOnClickListener {
            btnConfirmClick()
        }
        binding.qrScanIV.setOnClickListener {
            couponCodeScan = 0
            scanScaeen()
        }
        binding.scanForBoardedStatus.setOnClickListener {
            scanToBoard()
        }
        binding.resetTV.setOnClickListener {
            binding.motET.setText("")
            binding.motET.clearFocus()

        }

    }

    private fun scanScaeen() {
        val scanner = IntentIntegrator(this)
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        scanner.setBeepEnabled(true)
        scanner.setBarcodeImageEnabled(true)
        scanner.initiateScan()

    }

    override fun onResume() {
        super.onResume()

        if (PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL) != null) {
            oldBoarding =
                PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL)!!
            binding.etBoardingAt.setText(oldBoarding.name)
        }
        if (PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL) != null) {
            oldDroping =
                PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL)!!
            binding.etDropOffAt.setText(oldDroping.name)

        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("MotActivityTest: ${requestCode}, $resultCode")
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Timber.d("qrScanResult :  ${result.contents}")
                if (couponCodeScan == 1) {
                    qrResponse= result.contents
                    listSeatno.clear()
                    tempMapList.clear()
                    var pnrNumber =result.contents
                    var fromQr = !pnrNumber.contains("PNR!", true)
                    if (this.isNetworkAvailable()) {
                        getTicketDetailsApi(pnrNumber, fromQr)
                    } else this.noNetworkToast()

                } else if (couponCodeScan == 0) {
                    if (result.contents.contains("MOT!", true)){
                        val temp = result.contents.replace("MOT!", "")
                        binding.motET.setText(temp)
                    }else{
                        binding.motET.setText(result.contents)
                    }
                }


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    private fun getTicketDetailsApi(pnrNumber: String, fromQrScan: Boolean) {
        binding.includeProgress.progressBar.visible()

        ticketDetailsViewModel.ticketDetailsApi(
            loginModelPref.api_key,
            pnrNumber,
            true,
            fromQrScan,
            locale!!,
            ticket_details_method_name
        )
    }
    private fun setTicketDetailsObserver() {
        ticketDetailsViewModel.dataTicketDetails.observe(this) {
            binding.includeProgress.progressBar.gone()

            if (it != null) {
                if (it.code == 200) {
                    binding.includeProgress.progressBar.gone()
                    val passdetail =
                        arrayListOf<com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail?>()
                    qrSelectedPnrNumber = it.body.ticketNumber!!

                    val qrSelectedResId = it.body.reservationId.toString()

                    it.body.passengerDetails?.forEach {
                        if (it!!.boardingStatus!!.lowercase(Locale.getDefault()) == "yet to board") {
                            passdetail.add(it)
                        }
                    }
                    if(resId.equals(qrSelectedResId)){
                        if (passdetail.isEmpty()) {
                            toast(getString(R.string.all_passengers_are_boarded_fro_this_pnr))
                        } else {
                            DialogUtils.dialogScanStatus(
                                this,
                                passdetail,
                                it.body.ticketNumber,
                                getString(R.string.verify),
                                this,
                                this
                            )
                        }
                    }
                    else{
                        toast(getString(R.string.customer_does_not_belong_to_this_service))
                    }

                } else
                    if (it.message != null) {
                        it.message.let { it1 ->
                            toast(it1)
                        }
                    }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun updateBoardedStatusApi(
        pnrNumber: String,
        seatNumber: String,
        temp:ArrayList<String>,
        passengerName: String
    ) {

        if (isNetworkAvailable()) {
            binding.includeProgress.progressBar.visible()
            pickUpChartViewModel.updateBoardedStatusAPI(
                com.bitla.ts.domain.pojo.update_boarded_status.ReqBody(
                    api_key = loginModelPref.api_key,
                    pnr_number = pnrNumber,
                    seat_number = seatNumber,
                    status = "2",
                    new_qr_code = "",//Qr Code
                    skip_qr_code = true,
                    new_otp = "",//New OTP
                    passenger_name = passengerName,
                    reservation_id = resId,
                    temp = temp,
                    remarks = "",
                    locale = locale
                ),
                update_boarded_status_method_name
            )

        } else {
            noNetworkToast()
        }
    }



    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun btnConfirmClick() {
        if (binding.etBoardingAt.text.isNullOrEmpty() || binding.etDropOffAt.text.isNullOrEmpty() || binding.motET.text.isNullOrEmpty()) {
            toast(getString(R.string.please_fill_all_the_required_details))
        } else {
            boardingStage()
        }
    }
    private fun boardingStage() {

        if (bpDpBoarding.size > 1) {
            stageDetailItem = StageDetail()
            stageDetailItem.travelDate = travelDte
            stageDetailItem.id = oldBoarding.id.toInt()
            stageDetailItem.name = oldBoarding.name
            stageDetailItem.time = oldBoarding.time
        } else {
            stageDetailItem = StageDetail()
            stageDetailItem.travelDate = travelDte
            stageDetailItem.id = bpDpBoarding[0].id.toInt()
            stageDetailItem.name = bpDpBoarding[0].name
            stageDetailItem.time = bpDpBoarding[0].time
        }
        PreferenceUtils.putObject(
            stageDetailItem,
            PREF_BOARDING_STAGE_DETAILS
        )
        droppingStage()

    }

    private fun droppingStage() {
        if (bpDpDropping.size > 1) {
            stageDetailItem.travelDate = travelDte
            stageDetailItem.id = oldDroping.id.toInt()
            stageDetailItem.name = oldDroping.name
            stageDetailItem.time = oldDroping.time
        } else {
            stageDetailItem.travelDate = travelDte
            stageDetailItem.id = bpDpDropping[0].id.toInt()
            stageDetailItem.name = bpDpDropping[0].name
            stageDetailItem.time = bpDpDropping[0].time
        }

        PreferenceUtils.putObject(
            stageDetailItem,
            PREF_DROPPING_STAGE_DETAILS
        )
        callRapidBookingApi()

    }

    private fun startQuickBooking(motCodeValue:String , motDifference:String, motAmount:String, motDiscount:String) {

        val intent = Intent(this, QuickBookingActivity::class.java)
        intent.putExtra("SEATS", "1")
        intent.putExtra(getString(R.string.boarding_point), finalBoardingName)
        intent.putExtra(getString(R.string.dropping_point), finalDroppingName)
        intent.putExtra("motCoupon", binding.motET.text.toString())

        intent.putExtra("motCodeValue", motCodeValue)
        intent.putExtra("motDifference", motDifference)
        intent.putExtra("motAmount", motAmount)
        intent.putExtra("motDiscount", motDiscount)
        startActivity(intent)
    }

    private fun scanToBoard() {
        couponCodeScan = 1
        scanScaeen()
    }

    override fun onSingleButtonClick(str: String) {
        val temlist = arrayListOf<String>()
        val seat = listSeatno.toString().replace("[", "").replace("]", "").replace(" ", "")

        val name =
            listSassName.toString().replace("[", "").replace("]", "").replace(" ", "")

        for (i in 0..tempMapList.size.minus(1)) {
            temlist.add("${tempMapList.keyAt(i)}:${tempMapList.valueAt(i)}")
        }

        updateBoardedStatusApi(
            qrSelectedPnrNumber,
            seat,
            temlist,
            name
        )

    }

    override fun onItemChecked(
        isChecked: Boolean,
        view: View,
        data1: String,
        data2: String,
        data3: String,
        position: Int
    ) {
        if (isChecked) {

            listSassName.add(data2)
            listSeatno.add(data1)
            tempMapList += Pair(data1, data3)

        } else {
            if (listSeatno.contains(data1)) {
                listSassName.remove(data2)
                listSeatno.remove(data1)
                tempMapList.remove(data1)
            }
        }
    }



    private fun callRapidBookingApi() {
        var tempMot: String? = null
        if (!binding.motET.text.isNullOrEmpty() ) {
            tempMot = binding.motET.text.toString()
        }
        if (isNetworkAvailable()) {
            binding.includeProgress.progressBar.visible()

            val seatCount = SeatCount(1)
            if (allowBpDpFare != null && allowBpDpFare == false) {
                val reqBody = com.bitla.ts.domain.pojo.rapid_booking.request.ReqBody(
                    api_key = loginModelPref.api_key,
                    destination = destinationId,
                    locale = locale,
                    no_of_seats = 1,
                    mot_coupon = tempMot,
                    origin = sourceId,
                    res_id = resId.toString(),
                    seat_count = seatCount,
                )

//                 val obj = JSONObject()
//                 obj.put("1",noOfSeats)
//                 Timber.d("SEATS",obj.toString())


                bookTicketViewModel.rapidBookingApi(
                    reqBody,
                    rapid_booking_method_name
                )

            } else {
                val reqBody = com.bitla.ts.domain.pojo.rapid_booking.request.ReqBody1(
                    api_key = loginModelPref.api_key,
                    destination = destinationId,
                    locale = locale,
                    no_of_seats =1,
                    mot_coupon = tempMot,
                    origin = sourceId,
                    res_id = resId.toString(),
                    seat_count = seatCount,
                    boarding_at = oldBoarding.id,
                    drop_off = oldDroping.id,
                )


//                val obj = JSONObject()
//                obj.put("1",noOfSeats)
//                Timber.d("SEATS",obj.toString())


                bookTicketViewModel.rapidBookingApi(
                    reqBody,
                    rapid_booking_method_name
                )
            }
        } else
            noNetworkToast()
    }

    private fun observer(){
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
                        startQuickBooking(motCodeValue = it.result.mot_discount.toString(),
                            motDifference = totalFare,
                            motAmount = it.result.ticket_fare.toString(),
                            motDiscount = it.result.discount.toString())
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
                        toast(it.message)
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

    }

    override fun onNewIntent(intent: Intent) {
        val extras = intent.extras

        val msgIntent = Intent(this, MotCouponActivity::class.java)
        msgIntent.putExtra("busType",busType)
        msgIntent.putExtra("depTime",depTime)
        msgIntent.putExtra("travelDate",travelDte)
        msgIntent.putExtra("resId12",resId)
        msgIntent.putExtra("serviceNumber",serviceNumber)

        startActivity(msgIntent)
        finish()
    }
}