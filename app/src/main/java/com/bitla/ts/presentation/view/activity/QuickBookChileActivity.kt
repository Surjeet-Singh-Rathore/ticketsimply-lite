package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnQuickBookListener
import com.bitla.ts.databinding.ActivityQuickBookChileBinding
import com.bitla.ts.domain.pojo.book_ticket_full.request.BookTicketFullRequest
import com.bitla.ts.domain.pojo.book_ticket_full.request.BookingDetails
import com.bitla.ts.domain.pojo.book_ticket_full.request.ReqBody
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.login_model.Users
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.quick_book_chile.request.quickbook_book_ticket_req.SelectedSeatType
import com.bitla.ts.domain.pojo.quick_book_chile.request.quickbook_book_ticket_req.Type
import com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.CitySeqOrder
import com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.QuickBookServiceDetailsResponse
import com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.Result
import com.bitla.ts.domain.pojo.redelcom.RedelcomPreferenceData
import com.bitla.ts.domain.pojo.redelcom.ReqBodyPrint
import com.bitla.ts.presentation.adapter.QBSeatTypePassengerCategoryAdapter
import com.bitla.ts.presentation.viewModel.BookingOptionViewModel
import com.bitla.ts.presentation.viewModel.RedelcomViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.bluetooth_print.AsyncBluetoothEscPosPrint
import com.bitla.ts.utils.bluetooth_print.AsyncEscPosPrint
import com.bitla.ts.utils.bluetooth_print.AsyncEscPosPrinter
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.dialog.DialogUtils.Companion.transactionFailedDialog
import com.bitla.ts.utils.dialog.DialogUtils.Companion.transactionFailedInterfaceDialog
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64
import com.google.gson.Gson
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import okhttp3.ResponseBody
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
import java.lang.Runnable
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class QuickBookChileActivity : BaseActivity(), OnQuickBookListener, DialogSingleButtonListener,
    DialogButtonAnyDataListener {

    private lateinit var binding: ActivityQuickBookChileBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var quickBookSeatTypePassengerCategoryAdapter: QBSeatTypePassengerCategoryAdapter
    private var redelcomPaymentDialog: AlertDialog? = null
    private var countDownTimer: CountDownTimer? = null

    private var loginModelPref = LoginModel()
    private var apiKey: String = ""
    private var bccId: String = ""
    private var resId: String = ""
    private var totalSelectedSeats: Int = 0
    private var locale: String? = ""
    private var sourceId: String? = ""
    private var originalSourceId: String? = ""
    private var originalDestinationId: String? = ""
    private var destinationId: String? = ""
    private var boardingId: String = ""
    private var droppingId: String = ""
    private val isRapidBooking: String = "false" //fixed
    private val isFromBusOptApp: String = "true" //fixed
    private val isQuickBooking: String = "true" //fixed
    private var terminalId: String = ""

    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private val bookingOptionViewModel by viewModel<BookingOptionViewModel<Any?>>()
    private val redelcomViewModel by viewModel<RedelcomViewModel<Any?>>()
    private var pnrNumber = ""
    private var isRedelcomPrintEnable = false

    private var isHandlerRunning = false
    private var handler: Handler = Handler()
    private var runnable: Runnable? = null
    private var delay = 1500

    //  add/remove item list
    private var addRemoveBookSeatTypeList = mutableListOf<Type>()
    private var addRemoveBookQBSeatTypePassengerCategoryList = mutableListOf<Type>()

    //  type 1 & type 2 data list
    private var selectedSeatTypeData: SelectedSeatType? = null
    private var selectedPassengerCategoryData: SelectedSeatType? = null

    //  QB Service Details Type Model list
    private val quickBookSeatResultList = mutableListOf<Result>()
    private val quickBookPassengerCategoryResultList = mutableListOf<Result>()

    //  final QB Service Details Type Model list
    private val quickBookSeatTypeList = mutableListOf<com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.Type>()
    private val quickBookPassengerCategoryTypeList = mutableListOf<com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.Type>()

    //  final list
    private var selectedSeatTypeList = mutableListOf<SelectedSeatType>()

    //  quick book type response
    private lateinit var quickBookSeatModel: com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.Type

    //  quick book type and label response
    private lateinit var quickBookResultModel: Result

    //  seat type map and passenger category map
    private var seatMapSeatType = mutableMapOf<Int, String>()
    private var seatMapPassengerCategory = mutableMapOf<Int, String>()

    // add passenger category and seat type
    private val typesBookTicketSeatType = mutableListOf<Type>()
    private val typesBookTicketPassengerCategory = mutableListOf<Type>()

    private var totalSeatTypeAndPassengerAmount = 0.0
    private var totalPassengerAndSeatTypeCount = 0

    //  Quick Book Print
    private var privilegeResponseModel: PrivilegeResponseModel?= null
    private lateinit var ticketData: com.bitla.ts.domain.pojo.book_ticket_full.Result
    private var operatorLogo: String? = null
    private var bmpLogo: Bitmap? = null
    private var bmpQrCode: Bitmap? = null
    private var hexaDecimalString: String? = null
    private var bluetoothPrintTemplate: String? = null
    private var originalTemplate: String? = null
    private var qrCodeInput: String = ""
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var isFirstPrint: Boolean = true
    private var selectedDevice: BluetoothConnection? = null
    private var redelcomPaymentCreatedDialog: AlertDialog? = null
    private val sourceList = mutableListOf<CitySeqOrder>()
    private val destinationList = mutableListOf<CitySeqOrder>()
    private var selectedSrcPosition = 0
    private var selectedDestPosition = 0
    private val citySourceArray= arrayListOf<String>()
    private val cityDestinationArray= arrayListOf<String>()
    //    private lateinit var ticketDataTicketDetails: Body
//    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()
    private var isSourceSelected = false

    override fun initUI() {
        binding = ActivityQuickBookChileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.tvCurrentHeader.text = getString(R.string.quick_book)
        binding.toolbar.toolbarSubtitle.text = ""

        binding.toolbar.imgBack.setOnClickListener {
            onBackPressed()
        }

        getPref()
        getIntentData()
        callQuickBookServiceApi(sourceId.toString(),destinationId.toString())
        setUpQBServiceDetailsObserver()
        setUpQuickBookTicketObserver()
        setRedelcomViewModelObserver()

        binding.btnLeft.setOnClickListener {
            checkSeatTypeAndPassengerCategoryList(false)
        }

        binding.btnRight.setOnClickListener {
            checkSeatTypeAndPassengerCategoryList(true)
        }

        binding.toolbar.imgReload.setOnClickListener {
            clearList()
            callQuickBookServiceApi(originalSourceId.toString(),originalDestinationId.toString())
        }

        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            bookingOptionViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {}

    private fun clearList() {
        totalSelectedSeats = 0

        quickBookSeatTypeList.clear()
        quickBookSeatResultList.clear()
        quickBookPassengerCategoryTypeList.clear()
        quickBookPassengerCategoryResultList.clear()
        selectedSeatTypeData?.types?.clear()
        selectedPassengerCategoryData?.types?.clear()
        selectedSeatTypeList.clear()
        addRemoveBookSeatTypeList.clear()
        addRemoveBookQBSeatTypePassengerCategoryList.clear()

        totalSeatTypeAndPassengerAmount = 0.0
        totalPassengerAndSeatTypeCount = 0
        binding.tvTotalAmount.text = " : $$totalSeatTypeAndPassengerAmount"
        binding.tvTotalPassenger.text = " : $totalPassengerAndSeatTypeCount"

        typesBookTicketPassengerCategory.clear()
        typesBookTicketSeatType.clear()
        seatMapPassengerCategory.clear()
        seatMapSeatType.clear()

        sourceList.clear()
        destinationList.clear()
        citySourceArray.clear()
        cityDestinationArray.clear()
    }

    private fun checkSeatTypeAndPassengerCategoryList(isRedelcomPay: Boolean) {

        startShimmerEffect()
        totalSelectedSeats = 0

        if (selectedSeatTypeData?.types.isNullOrEmpty() && selectedPassengerCategoryData?.types.isNullOrEmpty()) {
            toast("Selected seat count is 0, please select seat")
            stopShimmerEffect()
        } else if (!selectedSeatTypeData?.types.isNullOrEmpty() && !selectedPassengerCategoryData?.types.isNullOrEmpty()) {
            selectedSeatTypeList.add(selectedSeatTypeData!!)
            selectedSeatTypeList.add(selectedPassengerCategoryData!!)

            for (i in 0 until selectedSeatTypeList.size) {
                for (j in 0 until selectedSeatTypeList[i].types!!.size) {
                    totalSelectedSeats += selectedSeatTypeList[i].types?.get(j)!!.selectedSeatCount
                }
            }
            callQuickBookTicketApi(totalSelectedSeats, isRedelcomPay)
        } else if (selectedSeatTypeData?.types.isNullOrEmpty()) {
            val selectedSeatTypeDataNew = SelectedSeatType(1, typesBookTicketSeatType)
            selectedSeatTypeList.add(selectedSeatTypeDataNew)
            selectedSeatTypeList.add(selectedPassengerCategoryData!!)

            for (i in 0 until selectedSeatTypeList.size) {
                for (j in 0 until selectedSeatTypeList[i].types!!.size) {
                    totalSelectedSeats += selectedSeatTypeList[i].types?.get(j)!!.selectedSeatCount
                }
            }

            callQuickBookTicketApi(totalSelectedSeats, isRedelcomPay)
        } else if (selectedPassengerCategoryData?.types.isNullOrEmpty()) {
            selectedSeatTypeList.add(selectedSeatTypeData!!)
            val typesBookTicketPassengerCategoryNew =
                SelectedSeatType(2, typesBookTicketPassengerCategory)
            selectedSeatTypeList.add(typesBookTicketPassengerCategoryNew)

            for (i in 0 until selectedSeatTypeList.size) {
                for (j in 0 until selectedSeatTypeList[i].types!!.size) {
                    totalSelectedSeats += selectedSeatTypeList[i].types?.get(j)!!.selectedSeatCount
                }
            }

            callQuickBookTicketApi(totalSelectedSeats, isRedelcomPay)
        }
    }

    private fun getIntentData() {
        originalSourceId = intent.getIntExtra("originID", 0).toString()
        sourceId = intent.getIntExtra("originID", 0).toString()
        destinationId = intent.getIntExtra("destinationID", 0).toString()
        originalDestinationId = intent.getIntExtra("destinationID", 0).toString()
        resId = intent.getIntExtra("reservationID", 0).toString()
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()

        binding.btnRight.gone()

        if (PreferenceUtils.getPreference(PREF_LOGO, getString(R.string.empty)) != null) {
            operatorLogo = PreferenceUtils.getPreference(PREF_LOGO, getString(R.string.empty))
            Timber.d("operatorLogo $operatorLogo")
        }

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase()
        }
    }

    private fun callQuickBookServiceApi(sourceId:String,destinationId:String) {
        startShimmerEffect()

        if (isNetworkAvailable()) {
            sharedViewModel.getQuickBookServiceDetail(
                resId,
                loginModelPref.api_key,
                sourceId,
                destinationId,
                operator_api_key,
                locale!!,
                service_details_method
            )
        } else {
            noNetworkToast()
        }
    }

    private fun setUpQBServiceDetailsObserver() {

        sharedViewModel.quickBookServiceDetails.observe(this) {
            if (it?.code == 200) {
                binding.containerQuickBookBottom.visible()
                binding.toolbar.root.visible()
                binding.toolbar.toolbarSubtitle.text =
                    "${it.origin} - ${it.destination}\n" + "${getString(R.string.total_seats)}: ${it.totalSeats} | ${
                        getString(R.string.booked)
                    }: ${it.booked} | ${getString(R.string.quota_blocked)}: ${it.quotaBlocked}"

                for (i in 0 until it.result.size) {
                    if (it.result[i].id == 1) {

                        for (j in 0 until it.result[i].types.size) {
                            quickBookSeatModel =
                                com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.Type(
                                    idType = it.result[i].types[j].idType,
                                    label = it.result[i].types[j].label,
                                    fare = it.result[i].types[j].fare
                                )

                            quickBookSeatTypeList.add(quickBookSeatModel)

                            quickBookResultModel = Result(
                                it.result[i].id,
                                it.result[i].label,
                                quickBookSeatTypeList
                            )
                            quickBookSeatResultList.add(quickBookResultModel)
                        }
                    } else {
                        for (j in 0 until it.result[i].types.size) {
                            quickBookSeatModel =
                                com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.Type(
                                    idType = it.result[i].types[j].idType,
                                    label = it.result[i].types[j].label,
                                    fare = it.result[i].types[j].fare
                                )

                            quickBookPassengerCategoryTypeList.add(quickBookSeatModel)

                            quickBookResultModel = Result(
                                it.result[i].id,
                                it.result[i].label,
                                quickBookPassengerCategoryTypeList
                            )
                            quickBookPassengerCategoryResultList.add(quickBookResultModel)
                        }
                    }
                }

                setQuickBookSeatTypeAdapter()
                setQuickBookPassengerCategoryAdapter()

//                totalSeats = it.totalSeats
                sourceId = it.originId.toString()
                destinationId = it.destinationId.toString()
                boardingId = it.boardingStages[0].id.toString()
                droppingId = it.dropoffStages[0].id.toString()


                sourceList.clear()
                destinationList.clear()
                citySourceArray.clear()
                cityDestinationArray.clear()

                setCityPairData(it)
                stopShimmerEffect()

            } else {
                stopShimmerEffect()

                binding.apply {
                    noData.visible()
                    tvNoService.visible()
                    toolbar.root.visible()
                    containerMain.gone()
                    containerQuickBookBottom.gone()

                }

                try {
                    if (it.message != null) {
                        binding.tvNoService.text = it.message.toString()
                    } else {
                        binding.tvNoService.text = getString(R.string.opps)
                    }
                } catch (e: Exception) {
//                    handle
                }
//                it.message?.let { it1 -> toast(it1)
            }
        }
    }

    private fun setCityPairData(qBResponse: QuickBookServiceDetailsResponse){

        if (qBResponse.citySeqOrder != null) {

            binding.quickBookCityPairContainer.visible()

            binding.etSource.setText(
                qBResponse.origin.ifEmpty {
                    ""
                })

            if (isSourceSelected) {
                binding.etDestination.setText("")
                destinationId = ""

            } else {
                binding.etDestination.setText(qBResponse.destination)
            }

            qBResponse.citySeqOrder.forEach {
                if (it.isSource == true) {
                    sourceList.add(it)
                }
                if (it.isDestination == true && it.id != sourceId?.toInt()) {
                    destinationList.add(it)
                }
            }

            sourceList.forEach {
                citySourceArray.add(it.name)
            }
            destinationList.forEach {
                cityDestinationArray.add(it.name)
            }

            binding.etSource.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    citySourceArray
                )
            )

            binding.etDestination.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    cityDestinationArray
                )
            )

            binding.etSource.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    selectedSrcPosition = position

                    sourceId = sourceList[selectedSrcPosition].id.toString()

                    clearList()
                    callQuickBookServiceApi(sourceId.toString(), destinationId.toString())
                    isSourceSelected = true
                }

            binding.etDestination.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    selectedDestPosition = position

                    destinationId = destinationList[selectedDestPosition].id.toString()

                    clearList()
                    callQuickBookServiceApi(sourceId.toString(), destinationId.toString())
                    isSourceSelected = false

                }
        } else {
            binding.quickBookCityPairContainer.gone()
        }

    }

    private fun setUpQuickBookTicketObserver() {

        bookingOptionViewModel.bookTicketFull.observe(this) {
            stopShimmerEffect()
            Timber.d("bookTicketFullResponse $it")

            if (it != null) {

                when (it.code) {
                    200 -> {
                        ticketData = it.result

                        if (it.result.payment_initiatives == "RedelcomPay") {

                            redelcomPaymentCreatedDialog =
                                DialogUtils.createRedelcomPaymentDialog(
                                    this,
                                    this,this
                                )

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

                            isRedelcomPrintEnable = false

                            DialogUtils.quickBookBookingConfirmedDialog(
                                isRedelcomPrintEnable,
                                " $totalPassengerAndSeatTypeCount ${getString(R.string.tickets)}",
                                this,
                                this
                            )

                            clearList()
                            callQuickBookServiceApi(ticketData.origin_id.toString(),ticketData.dest_id.toString())
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
                    400 -> {
//                        lastSelectedPaymentPosition = 0
                        toast(it.message?.toString())

                    }
                    else -> {
                        try {
                            if (it.message != null) {
                                toast(it.message.toString())
                            } else {
                                toast(getString(R.string.opps))
                            }
                        } catch (e: Exception) {
                            toast(e.toString())
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setRedelcomViewModelObserver() {
        redelcomViewModel.redelComData.observe(this) {
            if (it.code == 200) {

                handler.removeCallbacks(runnable!!)
                isHandlerRunning = false
                redelcomPaymentCreatedDialog?.dismiss()
                redelcomPaymentCreatedDialog?.cancel()

                isRedelcomPrintEnable = false

                DialogUtils.quickBookBookingConfirmedDialog(
                    isRedelcomPrintEnable,
                    " $totalPassengerAndSeatTypeCount ${getString(R.string.tickets)}",
                    this,
                    this
                )
            }
            else if(it.code == 402){
                handler.removeCallbacks(runnable!!)
                isHandlerRunning = false
                if(countDownTimer != null){
                    countDownTimer!!.cancel()
                }
                redelcomPaymentDialog!!.dismiss()
                transactionFailedInterfaceDialog(this,it.message,this)
            }
        }
    }

    private fun setQuickBookSeatTypeAdapter() {

        Timber.d("quickBookSeatResultList-SeatType - $quickBookSeatResultList")

        if (quickBookSeatTypeList.size != 0) {
            binding.containerSeatType.visible()
            layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvQuickBookSeatType.layoutManager = layoutManager
            quickBookSeatTypePassengerCategoryAdapter = QBSeatTypePassengerCategoryAdapter(
                this,
                quickBookSeatResultList,
                this
            )
            binding.rvQuickBookSeatType.adapter = quickBookSeatTypePassengerCategoryAdapter
        } else {
            binding.containerSeatType.gone()
        }
    }

    private fun setQuickBookPassengerCategoryAdapter() {

        Timber.d("quickBookSeatResultList-PassengerCategory - $quickBookPassengerCategoryResultList")

        if (quickBookPassengerCategoryTypeList.size != 0) {
            binding.containerPassengerCategory.visible()
            layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvQuickBookPassengerCategory.layoutManager = layoutManager
            quickBookSeatTypePassengerCategoryAdapter = QBSeatTypePassengerCategoryAdapter(
                this,
                quickBookPassengerCategoryResultList,
                this
            )
            binding.rvQuickBookPassengerCategory.adapter = quickBookSeatTypePassengerCategoryAdapter
        } else {
            binding.containerPassengerCategory.gone()
        }
    }

    private fun callQuickBookTicketApi(totalSelectedSeats: Int, isRedelcomPay: Boolean) {

        val bookTicketFullRequest = BookTicketFullRequest()
        bookTicketFullRequest.bccId = bccId
        bookTicketFullRequest.format = format_type
        bookTicketFullRequest.methodName = book_ticket_method_name

        val bookingDetails = BookingDetails()

        //Redelcom payment integration


        val reqBody = ReqBody()

        reqBody.apiKey = loginModelPref.api_key
        reqBody.bookingDetails = bookingDetails

        reqBody.boardingAt = boardingId
        reqBody.destinationId = destinationId.toString()
        reqBody.dropOff = droppingId
        reqBody.locale = locale
        reqBody.noOfSeats = totalSelectedSeats.toString()
        reqBody.operatorApiKey = operator_api_key
        reqBody.originId = sourceId.toString()
        reqBody.reservationId = resId.toString()

        reqBody.isFromBusOptApp = isFromBusOptApp
        reqBody.isFromMiddleTier = is_from_middle_tier
        reqBody.isQuickBooking = isQuickBooking
        reqBody.isRapidBooking = isRapidBooking
        reqBody.selectedSeatTypes = selectedSeatTypeList
        reqBody.responseFormat = "hash"

        bookTicketFullRequest.reqBody = reqBody
        //Timber.d("bookTicketFullRequest ${Gson().toJson(bookTicketFullRequest)}")

        bookingOptionViewModel.bookTicketFullApi(
            reqBody,
            apiType = book_ticket_method_name
        )
    }

    override fun quickBook(
        view: View,
        isAdd: Boolean,
        position: Int,
        passengerCount: Int,
        totalPassengerCount: Int,
        label: String,
        id: Int,
        labelType: String,
        labelTypeId: Int,
        fare: Double,
    ) {

        if (id == 1) {
            if (isAdd) {
                totalSeatTypeAndPassengerAmount += fare
                totalPassengerAndSeatTypeCount += totalPassengerCount
                binding.tvTotalAmount.text = " : $$totalSeatTypeAndPassengerAmount"
                binding.tvTotalPassenger.text = " : $totalPassengerAndSeatTypeCount"

                addRemoveBookSeatTypeList = addRemoveSeatTypeList(
                    position = position,
                    passengerCount = passengerCount,
                    labelTypeId = labelTypeId
                )
                selectedSeatTypeData = SelectedSeatType(1, addRemoveBookSeatTypeList)
//                Timber.d("typesBookTicketSeatType - ${selectedSeatTypeData}")
            } else {
                totalSeatTypeAndPassengerAmount -= fare
                totalPassengerAndSeatTypeCount -= totalPassengerCount
                binding.tvTotalAmount.text = " : $$totalSeatTypeAndPassengerAmount"
                binding.tvTotalPassenger.text = " : $totalPassengerAndSeatTypeCount"

                addRemoveBookSeatTypeList = addRemoveSeatTypeList(
                    position = position,
                    passengerCount = passengerCount,
                    labelTypeId = labelTypeId
                )
                selectedSeatTypeData = SelectedSeatType(1, addRemoveBookSeatTypeList)
//                Timber.d("typesBookTicketSeatType-removeBookTicketList(Type1) - ${selectedSeatTypeData}")
            }
        } else {
            if (isAdd) {
                totalSeatTypeAndPassengerAmount += fare
                totalPassengerAndSeatTypeCount += totalPassengerCount
                binding.tvTotalAmount.text = " : $$totalSeatTypeAndPassengerAmount"
                binding.tvTotalPassenger.text = " : $totalPassengerAndSeatTypeCount"

                addRemoveBookQBSeatTypePassengerCategoryList = addRemovePassengerCategoryList(
                    position = position,
                    passengerCount = passengerCount,
                    labelTypeId = labelTypeId
                )
                selectedPassengerCategoryData =
                    SelectedSeatType(2, addRemoveBookQBSeatTypePassengerCategoryList)
//                Timber.d("typesBookTicketSeatType - ${selectedPassengerCategoryData}")
            } else {
                totalSeatTypeAndPassengerAmount -= fare
                totalPassengerAndSeatTypeCount -= totalPassengerCount
                binding.tvTotalAmount.text = " : $$totalSeatTypeAndPassengerAmount"
                binding.tvTotalPassenger.text = " : $totalPassengerAndSeatTypeCount"

                addRemoveBookQBSeatTypePassengerCategoryList = addRemovePassengerCategoryList(
                    position = position,
                    passengerCount = passengerCount,
                    labelTypeId = labelTypeId
                )
                selectedPassengerCategoryData =
                    SelectedSeatType(2, addRemoveBookQBSeatTypePassengerCategoryList)
//                Timber.d("typesBookTicketSeatType - ${selectedPassengerCategoryData}")
            }
        }
    }

    private fun addRemoveSeatTypeList(
        position: Int,
        passengerCount: Int,
        labelTypeId: Int
    ): MutableList<Type> {
        seatMapSeatType += Pair(position, labelTypeId.toString())
        Timber.d("SeatType<MAP> - $seatMapSeatType")

        if (typesBookTicketSeatType.isNotEmpty() && typesBookTicketSeatType.size == seatMapSeatType.size) {
//            if (typesBookTicketSeatType.size==seatMapSeatType.size){
//            } else{
//                val newType = Type(
//                    labelTypeId,
//                    totalPassengerAndSeatTypeCount
//                )
//                typesBookTicketSeatType.add(newType)
//            }

            val updateSelectedSeatCount = typesBookTicketSeatType.find {
                it.id.toString() == seatMapSeatType[position]
            }

            if (updateSelectedSeatCount != null) {
                updateSelectedSeatCount.selectedSeatCount = passengerCount
            }

        } else {
            val initType = Type(
                labelTypeId,
                passengerCount
            )
            typesBookTicketSeatType.add(initType)
        }
        return typesBookTicketSeatType
    }

    private fun addRemovePassengerCategoryList(
        position: Int,
        passengerCount: Int,
        labelTypeId: Int
    ): MutableList<Type> {
        seatMapPassengerCategory += Pair(position, labelTypeId.toString())
        Timber.d("SeatType<MAP> - $seatMapPassengerCategory")

        if (typesBookTicketPassengerCategory.isNotEmpty() && typesBookTicketPassengerCategory.size == seatMapPassengerCategory.size) {

            val updateSelectedSeatCount = typesBookTicketPassengerCategory.find {
                it.id.toString() == seatMapPassengerCategory[position]
            }
            if (updateSelectedSeatCount != null) {
                updateSelectedSeatCount.selectedSeatCount = passengerCount
//                Timber.d("seatMapPassengerCategory - $passengerCount = $updateSelectedSeatCount}" )
            }

        } else {
            val initType = Type(labelTypeId, passengerCount)
            typesBookTicketPassengerCategory.add(initType)
        }

        return typesBookTicketPassengerCategory
    }

    override fun onSingleButtonClick(str: String) {

        if (str == getString(R.string.timeout) || str == "go_back") {
            handler.removeCallbacks(runnable!!)
            isHandlerRunning = false
            clearList()
            callQuickBookServiceApi(sourceId.toString(),destinationId.toString())
        } else {
            printTicket()
        }
    }

    private fun printTicket() {
            if (!originalTemplate.isNullOrEmpty())
                bluetoothPrintTemplate = originalTemplate

            Timber.d("originalTemplate $originalTemplate")

            if (bluetoothPrintTemplate != null && bluetoothPrintTemplate!!.isNotEmpty()) {
                if (bluetoothPrintTemplate?.contains("00000")!! && hexaDecimalString != null) {
                    return
                    //bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("<img>$hexaDecimalString<img>","IMAGE")
                }

                operatorLogo?.let { getBitmapFromURL(it, getString(R.string.logo)) }
                checkPermissions()
                bluetoothPrint()
                // printWebView()
            } else
                toast(getString(R.string.template_not_configured))

    }

    private fun getBitmapFromURL(image: String, imageType: String) {
        val urlImage = URL(image)
        val result: Deferred<Bitmap?> = lifecycleScope.async {
            urlImage.toBitmap()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            if (imageType == getString(R.string.logo))
                bmpLogo = result.await()
            if (imageType == getString(R.string.qr_code))
                bmpQrCode = result.await()
        }
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

        bluetoothPrintTemplate = if (bluetoothPrintTemplate?.contains("BOARDING_QR")!! && qrCodeInput.isNotEmpty()) {
            bluetoothPrintTemplate?.replace(
                "BOARDING_QR", "[C]<qrcode size='25'>$qrCodeInput</qrcode>"
            )
        } else {
            bluetoothPrintTemplate?.replace(
                "BOARDING_QR", ""
            )
        }

        // bluetoothPrintTemplate = ""
        //val qrcode = "[C]<barcode type='ean13' height='10'>831254784551</barcode>"
        /* bluetoothPrintTemplate = "$bluetoothPrintTemplate \"[C]<img>${
             PrinterTextParserImg.bitmapToHexadecimalString(
                 printer,
                 qrDemo
             )
         }</img>\""*/

        return printer.addTextToPrint(
            bluetoothPrintTemplate?.trim()
        )
    }

    private fun bluetoothPrint() {

        Timber.d("bluetoothPrintTemplate before $bluetoothPrintTemplate ")

        if (bluetoothPrintTemplate?.contains("</img>")!!) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.substringAfter("</img>")
            bluetoothPrintTemplate = "IMAGE$bluetoothPrintTemplate"
        }
        if (bluetoothPrintTemplate?.contains("<qrcode")!!) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "[C]<qrcode size='25'>$qrCodeInput</qrcode>\n[C]=======cut here=======",
                "BOARDING_QR\n[C]=======cut here======="
            )
            // bluetoothPrintTemplate = "${bluetoothPrintTemplate?.replace("[C]=======cut here=======","BOARDING_QR\n[C]=======cut here=======")}"
        }
        Timber.d("bluetoothPrintTemplate image edit $bluetoothPrintTemplate ")

        if ((bluetoothPrintTemplate != null
                    && ::ticketData.isInitialized) && privilegeResponseModel != null
        ) {
            if (!bluetoothPrintTemplate?.contains("FOR_EACH_SEAT")!!) {
//                singleSeatBluetoothPrint()
            } else {
//                multiSeatBluetoothPrint()
            }
        }

        Timber.d("bluetoothPrintTemplate after $bluetoothPrintTemplate ")
    }

    private fun redelcomPrintdataSet(ticketData: com.bitla.ts.domain.pojo.book_ticket_full.Result) {
        /*val cDate = Date()
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy").format(cDate)
        val currentTime: String = SimpleDateFormat("HH:mm").format(cDate)
        var ticketPrint = " ";

        //Timber.d("Ticket Data : ",Gson().toJson(ticketData))


        val bAddress = ticketData.boarding_details.stage_name ?: getString(R.string.notAvailable)
        val dAddress = ticketData.dropOffDetails?.stageName ?: getString(R.string.notAvailable)
        var usersList: MutableList<LoginModel>? = arrayListOf()
        if (PreferenceUtils.getObject<Users>(PREF_USER_LIST_STRING) != null) {
            val users = PreferenceUtils.getObject<Users>(PREF_USER_LIST_STRING)
            usersList = users!!.users
        }

        val passengerNameList: ArrayList<String> = arrayListOf()
        val seatNoList: ArrayList<String> = arrayListOf()
        for (i in 0 until ticketData.passenger_details.size) {
            passengerNameList.add(ticketData.passenger_details[i].name)
            seatNoList.add(ticketData.passenger_details[i].seat_number)
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
        val ownerName = usersList!![0].name
        ownerContact = if (usersList[0].phone_number == "") {
            "-"
        } else {
            usersList[0].phone_number
        }

        val operatorName = PreferenceUtils.getLogin().travels_name
        val domainName = PreferenceUtils.getLogin().domainName
        ticketPrint += "{reset}";
        ticketPrint += "{center}";
        ticketPrint += "{b}" + operatorName + "{br}";
        ticketPrint += domainName + "{br}";
        ticketPrint += "BUS_TICKET" + "{br}{br}";


        ticketPrint += "TRAVEL_DETAILS" + "{br}" +
                "{left}" + getString(R.string.TICKET_NUMBER_PRINT) + ": " + "{right}" + ticketData.ticket_number + "{br}" +
                "{left}" + getString(R.string.BOARDING_STAGE) + ": " + "{right}" + bAddress + "{br}" +
                "{left}" + getString(R.string.ORIGIN_LABEL) + ": " + "{right}" + ticketData.origin + "{br}" +
                "{left}" + getString(R.string.DESTINATION) + ": " + "{right}" + dAddress + "{br}" +
                "{left}" + getString(R.string.DEP_TIME) + ": " + "{right}" + ticketData.dep_time + "{br}" +


                "{left}" + getString(R.string.TRAVEL_DATE) + ": " + "{right}" + ticketData.travel_date + "{br}" +
                "{left}" + getString(R.string.BASE_FARE) + ": " + "{right}" + ticketData.total_fare + "{br}" +
                "{left}" + getString(R.string.BOOKED_BY) + ": " + "{right}" + ticketData.booked_by + "{br}{br}" +

                "{left}" + getString(R.string.PASSENGER_DETAILS) + "{br}" +
                "{left}" + getString(R.string.NAME) + ": " + "{right}" + passengerNames + "{br}" +
                "{left}" + getString(R.string.CONTACT_NUMBER) + ": " + "{right}" + ticketData.passenger_details[0].mobile + "{br}" +
                "{left}" + getString(R.string.SEATS) + ": " + "{right}" + seatsNumbers + "{br}{br}" +

                "{left}" + getString(R.string.PRINT_BY) + ": " + "{right}" + ownerName + "{br}" +
                "{left}" + getString(R.string.CONTACT_NUMBER) + ": " + "{right}" + ownerContact + "{br}" +
                "{left}" + getString(R.string.DATE_AND_TIME) + ": " + "{right}" + currentDate + "," + "{right}" + currentTime +

                getString(R.string.NOTE_PRINT) + "{br}{br}{br}"


        val json = JSONObject()
        json.put("printText", ticketPrint)
        json.put("terminalId", usersList[0].redelcomData!!.terminalId)
        val body = ReqBodyPrint(ticketPrint, usersList[0].redelcomData!!.terminalId)
        val authCode = generateAuthCode(usersList[0].redelcomData!!.api_key, API_PRINT, json.toString())
        hitRedelcomPrintApi(authCode!!, body)

         */
    }

    private fun hitRedelcomPrintApi(authCode: String, body: ReqBodyPrint) {

        startShimmerEffect()
        val retrofit = initRetrofit()
        val api = retrofit!!.create(ApiInterface::class.java)

        val call = api.apiRedelcomPrint(authCode, body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == 200) {
                    stopShimmerEffect()

                    Timber.d("redelcomResponseData ${Gson().toJson(response.body())}")

//                    Timber.d("redelcomResponseData - ${response.}")
                    toast(getString(R.string.printing))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })
    }

    private fun initRetrofit(): Retrofit? {
        return Retrofit.Builder()
            .baseUrl(PreferenceUtils.getObject<RedelcomPreferenceData>(PREF_REDELCOM_DETAILS)!!.redelcom_uri)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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

    @SuppressLint("MissingPermission")
    private fun enableDeviceBluetooth() {
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        mBluetoothAdapter = bluetoothManager.adapter
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
                                if (privilegeResponseModel != null && privilegeResponseModel?.availableAppModes?.allowReprint == true) {
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
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
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
                        "Async.OnPrintFinished",
                        "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                    )
                }

                override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                    Timber.d(
                        "Async.OnPrintFinished",
                        "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                    )
                }
            }
        )
            .execute(this.getAsyncEscPosPrinter(selectedDevice))
    }

    @Throws(java.lang.Exception::class)
    fun generateAuthCode(key: String, path: String, body: String): String {
        val msg = "/$path,$body"
        Timber.d("MESSAGE", msg)
        val sha256HMAC = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key.toByteArray(charset("UTF-8")), "HmacSHA256")
        sha256HMAC.init(secretKey)
        val base64 =
            Base64.encodeBase64String(sha256HMAC.doFinal(msg.toByteArray(charset("UTF-8"))))
        return PreferenceUtils.getObject<RedelcomPreferenceData>(PREF_REDELCOM_DETAILS)!!.client_id + ";" + base64
    }

    private fun startShimmerEffect() {
        binding.apply {
            containerMain.gone()
            containerScrollBar.gone()
            containerQuickBookBottom.gone()
            noData.gone()
            tvNoService.gone()
//            toolbar.root.gone()
            shimmerLayoutQuickBook.startShimmer()
            shimmerLayoutQuickBook.visible()
            binding.toolbar.imgReload.isEnabled=false

        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            shimmerLayoutQuickBook.gone()
            containerMain.visible()
            containerScrollBar.visible()
            containerQuickBookBottom.visible()
            toolbar.root.visible()
            binding.toolbar.imgReload.isEnabled=true
            if (shimmerLayoutQuickBook.isShimmerStarted) {
                shimmerLayoutQuickBook.stopShimmer()
            }
        }
    }

    override fun onPause() {
        if (isHandlerRunning) {
            clearList()
            handler.removeCallbacks(runnable!!)
            isHandlerRunning = false
        }
        super.onPause()
    }

    override fun onDataSend(type: Int, file: Any) {
        when(type) {
            1 -> {
                countDownTimer = file as CountDownTimer
            }
            2 -> {
                redelcomPaymentDialog = file as AlertDialog
            }
            3 -> {
                clearList()
                callQuickBookServiceApi(originalSourceId.toString(),originalDestinationId.toString())
            }
        }
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {


    }
}
