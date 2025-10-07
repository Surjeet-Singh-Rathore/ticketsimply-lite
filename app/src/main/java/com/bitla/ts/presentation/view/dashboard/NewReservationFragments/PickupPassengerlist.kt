package com.bitla.ts.presentation.view.dashboard.NewReservationFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.collection.arrayMapOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseUpdateCancelTicket
import com.bitla.ts.app.base.CancelTicketSheet
import com.bitla.ts.app.base.EditPassengerSheet
import com.bitla.ts.data.city_pickup_chart_by_stage
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogButtonListener
import com.bitla.ts.data.listener.DialogButtonMultipleView
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.FragmentDataListener
import com.bitla.ts.data.listener.OnItemCheckedMultipledataListner
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.data.listener.OnclickitemMultiView
import com.bitla.ts.data.resend_otp_and_qr_code_method_name
import com.bitla.ts.data.ticket_details_method_name
import com.bitla.ts.data.update_boarded_status_method_name
import com.bitla.ts.data.view_reservation_method_name
import com.bitla.ts.databinding.ChildPassengerCallBottomSheetBinding
import com.bitla.ts.databinding.FragmentPassengerListBinding
import com.bitla.ts.databinding.SheetBoardedCheckBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.custom_stage_summary.PrintStageSummary
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.luggage_details.request.ReqBody
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.sendOtpAndQrCode.request.SendOtpAndQrCodeRequest
import com.bitla.ts.domain.pojo.service_details_response.LatLongData
import com.bitla.ts.domain.pojo.service_stages.StageDetailsItem
import com.bitla.ts.domain.pojo.update_boarded_status.request.CargoDetails
import com.bitla.ts.domain.pojo.update_boarded_status.request.UpdateBoardedStartusCargo
import com.bitla.ts.domain.pojo.view_reservation.ChartType
import com.bitla.ts.domain.pojo.view_reservation.CitySeqOrder
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetail
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetailX
import com.bitla.ts.domain.pojo.view_reservation.PnrGroup
import com.bitla.ts.domain.pojo.view_reservation.RespHash
import com.bitla.ts.domain.pojo.view_reservation.ViewReservationResponseModel
import com.bitla.ts.presentation.adapter.ChartListAdapter
import com.bitla.ts.presentation.adapter.NewSortByAdaper.PassengerSortSublistAdapter
import com.bitla.ts.presentation.adapter.PassengerStageAdapter
import com.bitla.ts.presentation.adapter.SortByAdaper.StageAdapter
import com.bitla.ts.presentation.view.activity.IvrCallingActivity
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.reservationOption.ViewReservationActivity
import com.bitla.ts.presentation.view.ticket_details_compose.TicketDetailsActivityCompose
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.presentation.viewModel.ServiceStageViewModel
import com.bitla.ts.presentation.viewModel.TicketDetailsComposeViewModel
import com.bitla.ts.presentation.viewModel.TicketDetailsViewModel
import com.bitla.ts.utils.PosPrintUtils.SunmiPrintHelper
import com.bitla.ts.utils.bluetooth_print.AsyncBluetoothEscPosPrint
import com.bitla.ts.utils.bluetooth_print.AsyncEscPosPrint
import com.bitla.ts.utils.bluetooth_print.AsyncEscPosPrinter
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getCountryCodes
import com.bitla.ts.utils.common.getDateDMY
import com.bitla.ts.utils.common.getPhoneNumber
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.common.getTodayDateWithTime
import com.bitla.ts.utils.common.getUserRole
import com.bitla.ts.utils.constants.APPLY_FILTER
import com.bitla.ts.utils.constants.CLOSE_CHART
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y_H_M
import com.bitla.ts.utils.constants.IS_PINELAB_DEVICE
import com.bitla.ts.utils.constants.MODIFY_DETAILS
import com.bitla.ts.utils.constants.PASSENGER_LIST
import com.bitla.ts.utils.constants.PERMISSION_BLUETOOTH
import com.bitla.ts.utils.constants.PERMISSION_BLUETOOTH_ADMIN
import com.bitla.ts.utils.constants.PERMISSION_BLUETOOTH_CONNECT
import com.bitla.ts.utils.constants.PERMISSION_BLUETOOTH_SCAN
import com.bitla.ts.utils.constants.PRINT_OPTION
import com.bitla.ts.utils.constants.REQUEST_ENABLE_BT
import com.bitla.ts.utils.constants.SHOW_ALL
import com.bitla.ts.utils.constants.STATUS
import com.bitla.ts.utils.constants.TICKET_BOOKED_FAILED
import com.bitla.ts.utils.constants.TicketBookedFailed
import com.bitla.ts.utils.common.jsonToString
import com.bitla.ts.utils.constants.CALL_OPTION_CLICKS
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.IS_APPLY_BP_DP_FARE
import com.bitla.ts.utils.sharedPref.PREF_DESTINATION
import com.bitla.ts.utils.sharedPref.PREF_DESTINATION_ID
import com.bitla.ts.utils.sharedPref.PREF_DOMAIN
import com.bitla.ts.utils.sharedPref.PREF_IS_APPLY_BPDP_FARE
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_LOGO
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PREF_SOURCE
import com.bitla.ts.utils.sharedPref.PREF_SOURCE_ID
import com.bitla.ts.utils.sharedPref.PREF_TRAVEL_DATE
import com.bitla.ts.utils.sharedPref.PRINT_TYPE_BLUETOOTH
import com.bitla.ts.utils.sharedPref.PRINT_TYPE_HARVARD
import com.bitla.ts.utils.sharedPref.PRINT_TYPE_PINELAB
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import gone
import isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import noNetworkToast
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toBitmap
import toast
import visible
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class PickupPassengerList : BaseUpdateCancelTicket(), OnItemClickListener,
    DialogSingleButtonListener, DialogButtonListener, OnItemPassData, View.OnClickListener,
    OnclickitemMultiView, DialogButtonMultipleView,
    OnItemCheckedMultipledataListner {
    private var currency: String = ""
    private var currencyFormat: String = ""
    private var privilegeResponse: PrivilegeResponseModel? = null
    private var operatorName: String = ""
    private var serviceName: String? = null
    private var travelDate: String? = null
    private var deptTime: String? = null
    private var driverName: String? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var stageAdapter: StageAdapter
    private lateinit var newStageAdapter: PassengerStageAdapter
    private lateinit var chartAdapter: ChartListAdapter
    private lateinit var cancelTicketSheet: CancelTicketSheet

    private lateinit var editPassengerSheet: EditPassengerSheet

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private val ticketDetailsComposeViewModel by viewModel<TicketDetailsComposeViewModel<Any?>>()
    lateinit var binding: FragmentPassengerListBinding
    lateinit var bindingSheet: SheetBoardedCheckBinding
    lateinit var bottomSheetDialog: BottomSheetDialog
    private var chartTypeList: ArrayList<ChartType> = arrayListOf()
    private var chartType: String = "1"
    private var respHashList: ArrayList<RespHash> = arrayListOf()
    private var passengerlist: ArrayList<PassengerDetailX> = arrayListOf()
    private var passengerStatus: String? = null
    private var pnrNum: String? = null
    private var coachNumber: String? = ""
    private var seatNum: String? = null
    private var resID: Long? = null
    private val citySqeName: ArrayList<String> = arrayListOf()
    private var citySqeDetail: ArrayList<CitySeqOrder> = arrayListOf()
    private var citySqeId: ArrayList<Int> = arrayListOf()
    private var cityselected: Boolean = false
    private var selected: Int? = -1
    private var passengerName: String = ""
    private var newOtp: String = ""
    private var skipQrCcode: Boolean = false
    private var scanTag = ""
    private var listSeatno = arrayListOf<String>()
    private var listSassName = arrayListOf<String>()
    private var qrresponse = ""
    private var qrSelectedResId = ""
    private var qrSelectedPnrNumber = ""
    private var flagTemp = false
    private var changeChart = false
    private var resetcount = 0
    private var templist = listOf<String>()
    private var tempMapList = arrayMapOf<String, String>()
    private lateinit var boardedSwitchButton: SwitchCompat
    private lateinit var boardedStatusText: TextView
    private var selectedDevice: BluetoothConnection? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var locale: String? = ""
    private lateinit var rotate: RotateAnimation
    private lateinit var fragmentDataListener: FragmentDataListener
    private var openDialog = true
    private var bluetoothPrintTemplate: String? = null
    private var originalTemplate: String? = null
    private var ticketData: com.bitla.ts.domain.pojo.ticket_details_phase_3.response.Body? = null
    private var qrCodeInput: String = ""
    private var busLogo: Bitmap? = null
    private var operatorLogo: String? = ""
    private var hexaDecimalString: String? = null
    private var printArray = JSONArray()
    private var bmpLogo: Bitmap? = null
    private var bmpQrCode: Bitmap? = null
    private var insuranceBitmap: Bitmap? = null
    private var hexvalue: String? = ""
    private var domain: String = ""
    private var withoutSpacePrint = false
    private var TicketQRCode: Bitmap? = null
    private val BILLING_REQUEST_TAG = "MASTERAPPREQUEST"
    private val MESSAGE_CODE = 1001
    private var message: Message = Message.obtain(null, MESSAGE_CODE)
    private var mServerMessenger: Messenger? = null
    private var isBound: Boolean? = false
    private var isTicketDetailsPrintClicked: Boolean? = false
    private val serviceStageViewModel by viewModel<ServiceStageViewModel<Any?>>()
    private var updateLuggageDetailsPostConfirmation: Boolean? = false
    private var luggagePnrNum: String? = ""

    private var stageList: ArrayList<LatLongData>? = arrayListOf()
    private var neededCountry: String = ""
    private var reprintChargesAmount: Double? = null
    private var pnrTicketNumber: String? = ""
    private var allowToDisplayCustomerPhoneNumber: Boolean? = false



    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentDataListener = activity as FragmentDataListener
        } catch (e: ClassCastException) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentPassengerListBinding.inflate(layoutInflater)
        SunmiPrintHelper.getInstance().initSunmiPrinterService(requireContext())
        getPref()
        setServicePointObserver()

        firebaseLogEvent(
            requireContext(),
            PASSENGER_LIST,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            PASSENGER_LIST,
            "Passenger list"
        )
//        requireContext().toast("$resID")
        cancelTicketSheet = childFragmentManager.findFragmentById(R.id.layoutCancelTicketSheet) as CancelTicketSheet
        editPassengerSheet = childFragmentManager.findFragmentById(R.id.layoutEditPassengerSheet) as EditPassengerSheet

        startShimmerEffect()
        setMenuVisibility(true)
        binding.cityHub.setOnClickListener(this)
        PreferenceUtils.putString("BulkShiftBack", "")
        if (requireContext().isNetworkAvailable()) {
//            dummyAPI()
//            setPickupFilter()
            setUpdatePrintCountObserver()
            updateBoardedStatusObserver()
            viewReservationObserver()
            cityPickupByStageObserver()
            fetchLuggageDetailsIntlObserver()
            updateLuggageDetailsIntlObserver()
        } else
            requireContext().noNetworkToast()
        bindingSheet = SheetBoardedCheckBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        bottomSheetDialog.setContentView(bindingSheet.root)
        binding.refreshCard.gone()
        binding.refreshCard.setOnClickListener {
            rotate = RotateAnimation(
                0f,
                180f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )

            rotate.duration = 750
            rotate.fillAfter = true
            rotate.interpolator = LinearInterpolator()
            rotate.repeatCount = Animation.INFINITE
            binding.refreshIcon.startAnimation(rotate)

            initRefreshListner()
        }

        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = true
            initRefreshListner()
        }

        binding.pickupFilterLL.setOnClickListener {
            setPickupFilter()
        }


        binding.dropOffFilterLL.setOnClickListener {
            setDropOffFilter()
        }

        binding.btnMapNavigation.setOnClickListener {
            getServicePointsRoute()
        }

        if(neededCountry.equals("india", ignoreCase = true)){
            binding.btnMapNavigation.gone()
        }
        binding.btnSearchPnrMobileNum.visible()
        binding.btnSearchPnrMobileNum.setOnClickListener {
            if (binding.svSearchPnrMobileNum.isVisible) {
                binding.svSearchPnrMobileNum.setQuery("", false)
                binding.svSearchPnrMobileNum.clearFocus()
                binding.svSearchPnrMobileNum.gone()
            } else {
                binding.svSearchPnrMobileNum.visible()
            }
        }

        binding.svSearchPnrMobileNum.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredRespHashList = filterRespHashBySearch(respHashList, newText)
                dummy(filteredRespHashList, null, true, chartType)

                return true
            }
        })

//        pickUpChartApi(chartType)

        //activity.binding.updateRatecardToolbar.imageHeaderPrint

        setTicketDetailsV1Observer()

        val printView =
            (requireActivity() as ViewReservationActivity).findViewById<ImageView>(R.id.image_header_print)

        if (privilegeResponse != null && privilegeResponse?.pickupChartPrint != null && privilegeResponse?.pickupChartPrint!!)
            printView.visible()
        else
            printView.gone()

        printView.setOnClickListener {
            firebaseLogEvent(
                requireContext(),
                PRINT_OPTION,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                PRINT_OPTION,
                "Print Option Clicks - ViewReservation"
            )
            isTicketDetailsPrintClicked = false
            if (privilegeResponse?.tsPrivileges?.thermalPrintForTsApp == true) {
                operatorLogo?.takeIf { it.isNotEmpty() }?.let {
                    getBitmapFromURL(it, getString(R.string.logo))
                }
            }
            if (passengerlist.isNotEmpty()) {
                checkPermissions()
            } else
                requireContext().toast(getString(R.string.no_data_available))
        }
        bottomSheetDialog.dismiss()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            ticketDetailsViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            ticketDetailsComposeViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    requireContext().showToast(it)
                }
            }
        }
    }

    private fun filterRespHashBySearch(
        respHashList: ArrayList<RespHash>,
        query: String?
    ): ArrayList<RespHash> {
        val searchText = query?.lowercase(Locale.getDefault()) ?: ""
        if (searchText.isEmpty()) {
            return respHashList // Return the original list if no query
        }

        val filteredRespHashList = ArrayList<RespHash>()

        respHashList.forEach { respHash ->
            val filteredPnrGroups = ArrayList<PnrGroup?>()
            respHash.pnr_group?.forEach { pnrGroup ->
                val filteredPassengers = pnrGroup?.passenger_details
                    ?.filter { passenger ->
                        passenger?.pnr_number?.lowercase(Locale.getDefault())?.contains(searchText) == true ||
                                passenger?.phone_number?.lowercase(Locale.getDefault())?.contains(searchText) == true
                    }
                    ?.let { ArrayList(it) }

                if (!filteredPassengers.isNullOrEmpty()) {
                    // Create a new PnrGroup with filtered passengers
                    filteredPnrGroups.add(
                        pnrGroup.copy(passenger_details = filteredPassengers)
                    )
                }
            }
            if (filteredPnrGroups.isNotEmpty()) {
                // Create a new RespHash with filtered PnrGroups
                filteredRespHashList.add(
                    respHash.copy(pnr_group = filteredPnrGroups, passengerDetails = arrayListOf())
                )
            }
        }

        return filteredRespHashList
    }

    private fun getServicePointsRoute() {
        if (requireActivity().isNetworkAvailable()) {
            serviceStageViewModel.serviceStageDetailsApi(
                resId = resID.toString(),
                apiKey = loginModelPref.api_key,
            )

        } else {
            requireActivity().noNetworkToast()
        }

    }

    private fun setServicePointObserver() {

        serviceStageViewModel.stageDetails.observe(
            requireActivity()
        ) {
            binding.progressBar.gone()

            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {

                            getServiceStagesAndMapRedirection(it.result)

                        }

                        401 -> {
                            DialogUtils.unAuthorizedDialog(
                                requireContext(),
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )
                        }

                        //else -> it.message?.let { it1 -> requireContext().toast(it1) }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                requireContext().toast("An error occurred")
            }
        }
    }



    private fun getServiceStagesAndMapRedirection(stageDetails: List<StageDetailsItem?>?) {
        var isStageConfigRequired = false
        stageList?.clear()
        stageDetails?.forEach {
            val obj = LatLongData()

            if (it?.latitude != null && it?.longitude != null) {
                obj.lat = it?.latitude.toString()
                obj.long = it?.longitude.toString()
                stageList?.add(obj)
            } else {
                isStageConfigRequired = true
            }
        }

        if (!isStageConfigRequired) {
            gotoGoogleMap(stageList)
        } else {
            DialogUtils.mapErrorDialog(
                requireContext(),
                getString(R.string.please_configure_all_stages), this
            )
        }
    }

    private fun gotoGoogleMap(stageList: ArrayList<LatLongData>?) {
        if (!stageList.isNullOrEmpty()) {
            var endPoints = (stageList?.get(0)?.lat ?: "0.0") + "," + stageList?.get(0)?.long

            for (i in 1 until stageList?.size!!) {
                endPoints += "+to:" + (stageList?.get(i)?.lat
                    ?: "0.0") + "," + stageList?.get(i)?.long
            }
            val uri =
                "http://maps.google.com/maps?daddr=" + endPoints
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setClassName(
                "com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"
            )
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent)
            }
        }
    }

    private fun setPickupFilter() {
//        binding.imgPickupStage.setColorFilter(R.color.colorPrimary)
        binding.tvStageLable.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )

        binding.imgStage.imageTintList =
            ColorStateList.valueOf((requireContext().resources.getColor(R.color.colorButton)))

//        binding.imgDropOffStage.setColorFilter(R.color.white)
        binding.imgDropOff.imageTintList =
            ColorStateList.valueOf((requireContext().resources.getColor(R.color.white)))
        binding.tvDropLable.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        changeChart = true
        chartType = "1"
        cityselected = false
        startShimmerEffect()
        binding.NoResult.gone()
        pickUpChartApi(chartType)
        binding.cityHub.text = getString(R.string.show_all)
    }


    private fun setDropOffFilter() {
        binding.imgStage.imageTintList =
            ColorStateList.valueOf((requireContext().resources.getColor(R.color.white)))
        binding.tvStageLable.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        binding.imgDropOff.imageTintList =
            ColorStateList.valueOf((requireContext().resources.getColor(R.color.colorPrimary)))
        binding.tvDropLable.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )

        changeChart = true
        chartType = "5"
        cityselected = false
        startShimmerEffect()
        binding.NoResult.gone()
        pickUpChartApi(chartType)
        binding.cityHub.text = getString(R.string.show_all)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PERMISSION_BLUETOOTH_ADMIN
            )
        } else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    PERMISSION_BLUETOOTH_CONNECT
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                    PERMISSION_BLUETOOTH_SCAN
                )
            } else
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    enableDeviceBluetooth()
                }
    }

    private fun pickUpChartApi(chartTypeSelected: String) {
        if (requireContext().isNetworkAvailable()) {
            pickUpChartViewModel.viewReservationAPI(
                apiKey = loginModelPref.api_key,
                resId = resID.toString(),
                chartType = chartType,
                locale = locale!!,
                apiType = view_reservation_method_name,
                newPickUpChart = true
            )

        } else requireContext().noNetworkToast()
    }

    override fun setMenuVisibility(visible: Boolean) {
        super.setUserVisibleHint(visible)
        if (visible) {
            resetcount += 1
            if (resetcount > 1) {
                startShimmerEffect()
                bccId = PreferenceUtils.getBccId()
                loginModelPref = PreferenceUtils.getLogin()
                // resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
                resID = if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
                    PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
                else
                    PreferenceUtils.getString("reservationid")?.toLong()
                pickUpChartApi(chartType)
            }
        } else {
            // fragment is no longer visible
        }
    }

    var originId: Int = 0
    var destId: Int = 0
    private fun viewReservationObserver() {

        pickUpChartViewModel.viewReservationResponse.observe(viewLifecycleOwner) {
            binding.refreshLayout.isRefreshing = false

            if (it != null) {
                if (it.code == 200) {


                    fragmentDataListener.onUpdateFragment(true)

                    originId = it.originId
                    destId = it.destinationId
                    coachNumber = it.coachNumber ?: ""
                    deptTime = it.depTime
                    savePref(it)
                    passengerlist.clear()
                    chartTypeList.clear()
                    respHashList = it.respHash
                    it.respHash?.forEach { respHash ->
                        respHash.pnr_group?.forEach { pnrGroup ->
                            pnrGroup?.passenger_details?.forEach { passengerDetailX ->
                                if (passengerDetailX != null) {
                                    passengerlist.add(passengerDetailX)
                                }
                            }

                        }

                    }

                    if(neededCountry.equals("india", ignoreCase = true)){
                        chartTypeList.addAll(it.chartType)
                    } else {
                        setChartBottomFilter()
                    }

//                    setChartBottomFilter()

//                    chartTypeList.addAll(it.chartType)
                    binding.cardTotalBookingStatus.visible()
                    binding.tvBoarded.text = it.boarded ?: ""
                    binding.tvYetToBoarded.text = it.yetToBoard ?: ""
                    binding.tvBooked.text = it.booked ?: ""
                    if (::rotate.isInitialized) {
                        //binding.refreshIcon.startAnimation(null)
                        if (rotate.hasStarted()) {
                            binding.refreshIcon.clearAnimation()

                            val date = Date()
                            val simpleDate = SimpleDateFormat("HH:mm")
                            binding.refreshTime.text = " (${simpleDate.format(date)})"

                        }
                    }
                    if (it.bookingDetails.boarded != null || it.bookingDetails.total_booked != null || it.bookingDetails.yet_to_board != null) {
                        binding.countlayout.visible()
                    }
                    if (it.bookingDetails.boarded != null) {
                        binding.boardedCount.text =
                            "${requireContext().getString(R.string.boarded_status)}: ${it.bookingDetails.boarded}"
                        binding.boardedCount.visible()
                    }
                    if (it.bookingDetails.total_booked != null) {
                        binding.bookedCount.text =
                            "${requireContext().getString(R.string.booked)}: ${it.bookingDetails.total_booked}"
                        binding.bookedCount.visible()
                    }
                    if (it.bookingDetails.yet_to_board != null) {
                        binding.yetToBoardCount.text =
                            "${requireContext().getString(R.string.yet_to_board)}: ${it.bookingDetails.yet_to_board}"
                        binding.yetToBoardCount.visible()
                    }


                    when (chartType) {
                        "1" -> {
                            binding.tvSortBy.text =
                                "${getString(R.string.sorted_by)} ${it.chartType[0].label}"
                            binding.cityHub.visible()

                        }

                        "2" -> {
                            binding.tvSortBy.text =
                                "${getString(R.string.sorted_by)} ${it.chartType[1].label}"
                            binding.cityHub.gone()
                        }

                        "3" -> {
                            binding.tvSortBy.text =
                                "${getString(R.string.sorted_by)} ${it.chartType[2].label}"
                            binding.cityHub.gone()

                        }

                        "4" -> {
                            binding.tvSortBy.text =
                                "${getString(R.string.sorted_by)} ${it.chartType[3].label}"
                            binding.cityHub.gone()
                        }

                        "5" -> {
                            binding.tvSortBy.text =
                                "${getString(R.string.sorted_by)} ${it.chartType[4].label}"
                            binding.cityHub.gone()
                        }

                        "6" -> {
                            binding.tvSortBy.text =
                                "${getString(R.string.sorted_by)} ${it.chartType[5].label}"
                            binding.cityHub.gone()
                        }
                    }
                    fromRespHash(it)

                    binding.rvChartList.gone()
                    if(neededCountry.equals("india", ignoreCase = true)){
                        chartListAdapter(chartTypeList)
                        binding.bottomMenu.visible()
                        binding.rvChartList.visible()
                    } else {
                        binding.idnBottomFilterView.visible()
                    }
//                    binding.idnBottomFilterView.visible()
//                    chartListAdapter(chartTypeList)
                } else {
                    fragmentDataListener.onUpdateFragment(false)
                    binding.bottomMenu.gone()
                    binding.idnBottomMenuAgentLogin.gone()
                    errorPage(true)
                }


                binding.btnFilterScan.setOnClickListener {
                    listSeatno.clear()
                    listSassName.clear()
                    tempMapList.clear()
                    scanTag = "QuickScan"
                    scanScaeen()
                }

            } else {
                fragmentDataListener.onUpdateFragment(false)
                requireContext().toast(getString(R.string.server_error))
            }
        }

    }

    private fun setChartBottomFilter() {
        chartTypeList.add(ChartType("", 1, true, "Pickup"))
        chartTypeList.add(ChartType("", 5, true, "Drop Off"))

    }

    fun fromRespHash(it: ViewReservationResponseModel) {
        binding.NoResult.gone()
        binding.tvSortBy.visible()
        savePref(it)
        citySqeDetail = it.citySeqOrder
        citySqeDetail.add(
            0,
            CitySeqOrder(
                0,
                getString(R.string.show_all),
                true,
                "",
                false,
                false
            )
        )
        citySqeName.clear()
        for (a in 0..citySqeDetail.size.minus(1)) {
            if (a == 0) {
                citySqeName.add(citySqeDetail[0].name)
                citySqeId.add(citySqeDetail[a].id)
            } else {
                citySqeId.add(citySqeDetail[a].id)
                val stageTime = citySqeDetail[a].stageTime
                val strs =
                    citySqeDetail[a].name.split("-").toTypedArray()
                citySqeName.add("${stageTime} - ${strs[0]}")
            }
        }
        PreferenceUtils.setPreference("dataAvailable", true)
        binding.rvPnrGroupPassengerList.gone()
        binding.passengerListSortby.gone()
        if (!it.respHash.isNullOrEmpty()) {

            binding.rvPnrGroupPassengerList.gone()
            binding.passengerListSortby.visible()
            if (!chartType.isNullOrEmpty()) {
                chartTypeOne(it)
            }/* else if (chartType == "5") {
                chartTypeFive(it)
            }*/
        } else if (!it.pnr_group.isNullOrEmpty()) {
            chartTypeRest(it)
        } else {
            errorPage(true)
        }
    }

    fun chartTypeOne(it: ViewReservationResponseModel) {
        if (!neededCountry.equals("India", true)) {
            binding.cityHub.visible()
        }
        if (cityselected) {
            var dataList: ArrayList<RespHash> = arrayListOf()
            for (i in 0..it.respHash.size.minus(1)) {
                if (it.respHash[i].cityId == selected) {
                    dataList.add(it.respHash[i])
                }
            }
            if (!dataList.isNullOrEmpty()) {
                searchErrorPage(false)
                callAdapter(dataList, null, true, chartType)
            } else {
                searchErrorPage(true)

            }

        } else {
            if (it.bookingDetails.bookedPassengerCount == 0) {
                errorPage(true)
            } else {
                errorPage(false)
                callAdapter(it.respHash, null, true, chartType)
            }
        }
    }

    fun chartTypeFive(it: ViewReservationResponseModel) {
        var destinationPassengerList = arrayListOf<PassengerDetail>()
        binding.cityHub.visible()
        it.respHash.forEach {
            destinationPassengerList.addAll(it.passengerDetails)
        }
        if (destinationPassengerList.isNullOrEmpty()) {
            errorPage(true)
        } else {
            errorPage(false)
            callAdapter(null, destinationPassengerList, false, chartType)

//            oldAdapter(destinationPassengerList)
        }
    }

    fun chartTypeRest(it: ViewReservationResponseModel) {
        binding.rvPnrGroupPassengerList.visible()
        binding.passengerListSortby.gone()
        callPnrGroupAdapter(it.pnr_group ?: arrayListOf())
        stopShimmerEffect()
        binding.mainLayout.visible()
        binding.NoResult.gone()
    }

    fun searchErrorPage(isNoData: Boolean) {
        stopShimmerEffect()
        if (isNoData) {
            PreferenceUtils.setPreference("dataAvailable", false)
            binding.passengerListSortby.gone()
            binding.NoResult.visible()

        } else {
            binding.passengerListSortby.visible()
            binding.NoResult.gone()
        }
    }


    fun errorPage(isNoData: Boolean) {
        stopShimmerEffect()
        if (isNoData) {
            PreferenceUtils.setPreference("dataAvailable", false)
            binding.mainLayout.gone()
            binding.passengerListSortby.gone()
            binding.NoResult.visible()
            binding.btnSearchPnrMobileNum.gone()
            binding.svSearchPnrMobileNum.gone()
        } else {
            binding.mainLayout.visible()
            binding.passengerListSortby.visible()
            binding.NoResult.gone()
            binding.btnSearchPnrMobileNum.visible()
            binding.svSearchPnrMobileNum.gone()
        }
    }

    fun callAdapter(
        respHash: ArrayList<RespHash>?,
        passengerList: kotlin.collections.ArrayList<PassengerDetail>?,
        parentVisible: Boolean,
        chartType: String
    ) {
        if (::stageAdapter.isInitialized.not()) {
            dummy(respHash, passengerList, parentVisible, chartType)
        } else {
            if (changeChart) {
                changeChart = false
                dummy(respHash, passengerList, parentVisible, chartType)
            } else {
                if (parentVisible) {
                    newStageAdapter.notifyAdapter(respHash!!)
                } else {
                    newStageAdapter.oldNotifyAdapter(passengerList!!)

                }
            }

        }

    }

    private fun callPnrGroupAdapter(pnrGroupList: ArrayList<PnrGroup?>?) {
        binding.rvPnrGroupPassengerList.adapter = PassengerSortSublistAdapter(
            requireContext(),
            pnrGroupList ?: arrayListOf(), privilegeResponse,
            neededCountry,
            editPassengerSheet,
            boardedSwitchActionClicked = { dialogue: Boolean, boardedSwitch: SwitchCompat, statusText: TextView, seatNumber: String, passengerName: String, pnrNumber: String, remarks: String ->
                boardedSwitchButton = boardedSwitch
                boardedStatusText = statusText
                if (dialogue) {
                    DialogUtils.statusDialog(
                        requireContext(),
                        pnrNumber,
                        seatNumber,
                        boardedSwitchButton,
                        statusText,
                        passengerName,
                        getString(R.string.goBack),
                        getString(R.string.confirm),
                        btnConfirm = { pnr, pName, btnswitch, statusText, statusSelected, sNumber ->
                            passengerStatus = statusSelected

                            if (statusSelected == null) {
                                requireContext().toast(getString(R.string.please_selecte_an_option))
                            } else if (statusSelected == "2") {
                                boarded(pName, sNumber, pnr, remarks)
                            } else {
                                if (privilegeResponse != null
                                ) {

                                    privilegeResponse?.let {
                                        Timber.d("privilegeoutput12: ${privilegeResponse?.validateRemarksForBoardingStageInMobilityApp}")
                                        if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {
                                            bindingSheet.lpassengerTemp.gone()
                                            bindingSheet.remarksLayout.visible()
                                            bindingSheet.bottomSheetHeader.text =
                                                getString(R.string.remarks)
                                            bindingSheet.scanLayout.gone()
                                            bindingSheet.otpLayout.gone()
                                            bindingSheet.resendOtp.gone()
                                            bindingSheet.skipVerification.gone()
                                            bindingSheet.etRemarksText.text?.clear()
                                            bindingSheet.etRemarksText.requestFocus()
                                            bottomSheetDialog.show()
                                            qrresponse = ""
                                            newOtp = ""
                                            seatNum = seatNumber
                                            remarksObserver(pnr)
                                        } else {
                                            updateBoardedStatusApi(
                                                "",
                                                "",
                                                pnr,//pnrNumber
                                                sNumber,//seatNumber
                                                statusSelected,
                                                templist,
                                                ""
                                            )

                                        }
                                    }
                                } else {
                                    requireContext().toast(requireContext().getString(R.string.server_error))
                                }
                            }


                        }
                    )
                    firebaseLogEvent(
                        requireContext(),
                        STATUS,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        STATUS,
                        "Status"
                    )
                } else {
                    passengerStatus = "2"
                    boarded(passengerName, seatNumber, pnrNumber, remarks)
                }
            },
            boardedSwitchMultiSeatActionClicked = { dialogue: Boolean, seatNumber: List<String>, pnrNumber: String, remarks: String ->
                if (dialogue) {
                    DialogUtils.statusMultiSeatDialog(
                        requireContext(),
                        pnrNumber,
                        seatNumber,
                        getString(R.string.goBack),
                        getString(R.string.confirm),
                        btnConfirm = { pnr: String, seatNumbers: String, statusSelected: String ->
                            if (statusSelected == null) {
                                requireContext().toast(getString(R.string.please_selecte_an_option))
                            } else if (statusSelected == "2") {
                                boarded("", seatNumbers, pnr, remarks)
                            } else {
                                if(privilegeResponse != null) {
                                    if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {
                                        bindingSheet.lpassengerTemp.gone()
                                        bindingSheet.remarksLayout.visible()
                                        bindingSheet.bottomSheetHeader.text =
                                            getString(R.string.remarks)
                                        bindingSheet.scanLayout.gone()
                                        bindingSheet.otpLayout.gone()
                                        bindingSheet.resendOtp.gone()
                                        bindingSheet.skipVerification.gone()
                                        bindingSheet.etRemarksText.text?.clear()
                                        bindingSheet.etRemarksText.requestFocus()
                                        bottomSheetDialog.show()
                                        qrresponse = ""
                                        newOtp = ""
                                        seatNum = seatNumbers
                                        remarksObserver(pnr)
                                    } else {
                                        updateBoardedStatusApi(
                                            "",
                                            "",
                                            pnr,//pnrNumber
                                            seatNumbers,//seatNumber
                                            statusSelected,
                                            templist,
                                            remarks
                                        )
                                    }
                                } else {
                                    requireContext().toast(requireContext().getString(R.string.server_error))
                                }
                            }
                        }
                    )
                    firebaseLogEvent(
                        requireContext(),
                        STATUS,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        STATUS,
                        "Status"
                    )
                }
            },
            actionModify = { seatNumber, pnrNumber ->
                firebaseLogEvent(
                    requireContext(),
                    MODIFY_DETAILS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    MODIFY_DETAILS,
                    "Modify Details"
                )

                showSingleTicketUpdateSheet(pnrNumber, seatNumber)
            },
            actionLuggageClick = { seatNumber: String, passengerName: String, pnrNumber: String, passengerAge: String, passengerStatus: String, passengerSex: String ->
                skipQrCcode = false
                DialogUtils.luggageDialogBox(
                    requireContext(),
                    pnrNumber,
                    passengerName,
                    seatNumber,
                    passengerStatus,
                    passengerAge,
                    passengerSex,
                    dialogueLuggage = { amount, quantity, item ->
                        updateCargodetails(
                            pnrNumber,
                            seatNumber,
                            passengerStatus,
                            amount,
                            quantity,
                            item
                        )
                    }
                )
            },
            actionLuggageMultiSeat = { passengerName: String, passengerAge: String, passengerSex: String, seatNumbers: List<String>, pnrNumber: String, pnrStatus: String ->
                skipQrCcode = false
                DialogUtils.luggageDialogBoxMultiSeat(
                    requireContext(),
                    passengerName,
                    passengerAge,
                    passengerSex,
                    pnrStatus,
                    seatNumbers,
                    dialogueLuggage = { seatNumbers, amount, quantity, item ->
                        updateCargodetails(
                            pnrNumber,
                            seatNumbers,
                            pnrStatus,
                            amount,
                            quantity,
                            item
                        )

                    }
                )
            },
            onCallClickListener = { phoneNumber: String ->
                if (allowToDisplayCustomerPhoneNumber == true) {
                    showCallConfirmationBottomSheet(phoneNumber)
                } else {
                    handleCallRequest(phoneNumber)
                }
            },
            actionLuggageOptionClick = { pnrNumber: String ->
                luggagePnrNum = pnrNumber

                if (requireContext().isNetworkAvailable()) fetchLuggageDetailsIntlApi(pnrNumber)
                else requireContext().noNetworkToast()
            }
        ) {
            openDialog = true
            callTicketDetailsV1Api(it)
        }
    }

    private fun savePref(it: ViewReservationResponseModel) {
        if (!it.citySeqOrder.isNullOrEmpty())
            PreferenceUtils.putCitySeqOrder(it.citySeqOrder)
        PreferenceUtils.setPreference(PREF_IS_APPLY_BPDP_FARE, it.isApplyBpDpFare)
        if (it.resId != null)
            PreferenceUtils.setPreference(
                PREF_RESERVATION_ID, it.resId.toLong()
            )
        PreferenceUtils.putString(
            "reservationid", it.resId
        )
        PreferenceUtils.putObject(it.isApplyBpDpFare, IS_APPLY_BP_DP_FARE)
        PreferenceUtils.putString(PREF_SOURCE, it.originName)
        PreferenceUtils.putString(PREF_SOURCE_ID, it.originId.toString())
        PreferenceUtils.putString(PREF_DESTINATION, it.destinationName)
        PreferenceUtils.putString(PREF_DESTINATION_ID, it.destinationId.toString())
        PreferenceUtils.putString(PREF_TRAVEL_DATE, getDateDMY(it.travelDate))

        PreferenceUtils.setPreference("seatwiseFare", "fromBulkShiftPassenger")
        PreferenceUtils.putString("SelectionCoach", "BOOK")
        PreferenceUtils.putString("fromBusDetails", "bookBlock")
        PreferenceUtils.removeKey("seatwiseFare")
        PreferenceUtils.removeKey("isEditSeatWise")
        PreferenceUtils.removeKey("PERSEAT")
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
        if (requireContext().isNetworkAvailable()) {
            if (status == "2") {
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
                        reservation_id = resID.toString(),
                        temp = templist,
                        remarks = remarks,
                        locale = locale
                    ),
                    update_boarded_status_method_name
                )


            } else {
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
                        reservation_id = resID.toString(),
                        temp = templist,
                        remarks = remarks,
                        locale = locale
                    ),
                    update_boarded_status_method_name
                )
            }
        } else requireContext().noNetworkToast()


    }

    private fun updateBoardedStatusObserver() {
        pickUpChartViewModel.updateBoardedStatusResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        bottomSheetDialog.dismiss()
                        PreferenceUtils.setPreference(
                            "pickUpChartStatus",
                            "${passengerStatus}"
                        )
                        requireContext().toast(it.message)
                        if (qrresponse != "") {
                            val intent =
                                Intent(requireContext(), ViewReservationActivity::class.java)
                            intent.putExtra("pickUpResid", resID)
                            startActivity(intent)
                        } else {
                            when (passengerStatus) {
                                "0" -> {
                                    boardedStatusText.setText(R.string.yet_to_board)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.colorRed2))
                                    boardedSwitchButton.isChecked = false

                                }

                                "1" -> {
                                    boardedStatusText.setText(R.string.unboarded_status)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.colorRed2))
                                    boardedSwitchButton.isChecked = false

                                }

                                "2" -> {
                                    boardedStatusText.setText(R.string.boarded_status)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.colorPrimary))
                                    boardedSwitchButton.isChecked = true
                                }

                                "3" -> {
                                    boardedStatusText.setText(R.string.no_show)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.black))
                                    boardedSwitchButton.isChecked = false

                                }

                                "4" -> {
                                    boardedStatusText.setText(R.string.missing_status)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.color_03_review_02_moderate))
                                    boardedSwitchButton.isChecked = false

                                }

                                "5" -> {
                                    boardedStatusText.setText(R.string.dropped_off)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.colorPrimary))
                                    boardedSwitchButton.isChecked = false
                                }
                            }

                            bottomSheetDialog.dismiss()
                        }


                    }

                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        flagTemp = false
                        requireContext().toast(it.result.message)
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun showCallConfirmationBottomSheet(
        phoneNumber: String,
    ) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bottomSheetBinding = ChildPassengerCallBottomSheetBinding.inflate(LayoutInflater.from(requireContext()))

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()

        bottomSheetBinding.callPassengerBtn.text = "${requireContext().getString(R.string.call)} $phoneNumber"

        bottomSheetBinding.callPassengerBtn.setOnClickListener {
            handleCallRequest(phoneNumber)

            firebaseLogEvent(
                context = requireContext(),
                logEventName = CALL_OPTION_CLICKS,
                loginId = loginModelPref.userName,
                operatorName = loginModelPref.travels_name,
                roleName = loginModelPref.role,
                eventKey = CALL_OPTION_CLICKS,
                eventValue = "Call Option Clicks - ViewReservation"
            )

            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.cancelCallBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    private fun handleCallRequest(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(Manifest.permission.CALL_PHONE),
                200
            )
        } else {
            if (privilegeResponse != null && privilegeResponse?.country != null) {
                val countryName = privilegeResponse?.country
                var countryList = ArrayList<Int>()

                if (getCountryCodes() != null && getCountryCodes().isNotEmpty())
                    countryList = getCountryCodes()

                val telNo =
                    getPhoneNumber(
                        passPhone = phoneNumber,
                        countryName
                    )
                if (countryList.isNotEmpty()) {
                    val finalTelNo = "+${countryList[0]}$telNo"
                    val intent =
                        Intent(Intent.ACTION_CALL, Uri.parse("tel:${finalTelNo}"))
                    requireContext().startActivity(intent)
                }
            }
        }
    }


    private fun updateCargodetails(
        pnrNumber: String,
        seatNumber: String,
        status: String,
        amount: String,
        quantity: String,
        item: String,

        ) {
        val updateCargo = UpdateBoardedStartusCargo(
            bccId.toString(),
            format_type,
            update_boarded_status_method_name,
            com.bitla.ts.domain.pojo.update_boarded_status.request.ReqBody(
                loginModelPref.api_key,
                pnrNumber,
                seatNumber,
                status,
                CargoDetails(amount, item, quantity),
                locale = locale
            )
        )
        /*pickUpChartViewModel.updateBoardedStatusCargoAPI(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            updateCargo,
            update_boarded_status_method_name
        )*/

        pickUpChartViewModel.updateBoardedStatusCargoAPI(
            com.bitla.ts.domain.pojo.update_boarded_status.request.ReqBody(
                loginModelPref.api_key,
                pnrNumber,
                seatNumber,
                status,
                CargoDetails(amount, item, quantity),
                locale = locale
            ),
            update_boarded_status_method_name
        )
        updateCargoObserver()
    }

    private fun updateCargoObserver() {

        pickUpChartViewModel.updateBoardedStartusCargo.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.code == 200) {
                    requireContext().toast(it.message)
                    bottomSheetDialog.dismiss()

                } else {
                    requireContext().toast(it.result.message)
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
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
        /*pickUpChartViewModel.resendOtpAndQrCodeAPI(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            sendOtpAndQrCodeRequest,
            resend_otp_and_qr_code_method_name
        )*/

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

    private fun resendOtpAndQrCodeObserver() {

        pickUpChartViewModel.sendOtpAndQrCodeResponse.observe(viewLifecycleOwner) {

            if (it != null) {
                if (it.code == 200) {
                    if (it.message != null)
                        requireContext().toast(it.message)
                } else {

                    if (it.result?.message != null) {
                        it.result.message.let { it1 ->
                            requireContext().toast(it1)
                        }
                    }
                }


            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun chartListAdapter(chartList: ArrayList<ChartType>) {

        binding.rvChartList.layoutManager =
            if (neededCountry.equals("india", ignoreCase = true)) {
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            } else {
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(20, 0, 0, 0)
        binding.rvChartList.layoutParams = layoutParams

//            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        chartAdapter =
            ChartListAdapter(requireActivity(), chartList,
                chartClick = { chartTypeId ->
                    firebaseLogEvent(
                        requireContext(),
                        APPLY_FILTER,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        APPLY_FILTER,
                        "Apply filter"
                    )
                    if (chartType != chartTypeId.toString()) {
                        changeChart = true
                        chartType = chartTypeId.toString()
                        cityselected = false
                        startShimmerEffect()
                        binding.NoResult.gone()
                        pickUpChartApi(chartType)
                        binding.cityHub.text = getString(R.string.show_all)

                    }
                }
            )
        binding.rvChartList.adapter = chartAdapter
//        binding.rvChartList.adapter.smoothScrollToPosition(chartType.toInt())
        chartAdapter.notifyDataSetChanged()

        if (neededCountry.equals("india", ignoreCase = true)) {
            binding.rvChartList.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->
                            rv.parent?.requestDisallowInterceptTouchEvent(true)
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                            rv.parent?.requestDisallowInterceptTouchEvent(false)
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, event: MotionEvent) {}

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }

//      For both the sorting in rvChartList
        if (neededCountry.equals("india", ignoreCase = true)) {
            // Smoothly scroll to the specific position if neededCountry is "india"
            if (chartType == "5") {
                (binding.rvChartList.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                    2,
                    0
                )
            }
            (binding.rvChartList.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(chartType.toInt(), 0)
        } else {
            // Default behavior (no smooth scrolling)
            binding.rvChartList.layoutParams = layoutParams
            if (chartType == "5") {
                (binding.rvChartList.layoutManager as? GridLayoutManager)?.scrollToPositionWithOffset(
                    2, 0
                )
            }
        }
//        (binding.rvChartList.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(chartType.toInt(), 0)

    }


    override fun onClickOfNavMenu(position: Int) {
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(view: View, position: Int) {

        try {
            if (view.tag != null) {
                if (view.tag == "CHARTTYPE") {
                    firebaseLogEvent(
                        requireContext(),
                        APPLY_FILTER,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        APPLY_FILTER,
                        "Apply filter"
                    )
                    if (chartType != position.toString()) {
                        chartType = position.toString()
                        cityselected = false
                        // startShimmerEffect()
                        binding.NoResult.gone()
//                        pickUpChartApi(chartType)
                        binding.cityHub.text = getString(R.string.show_all)

                    }
                } else {

                    val list = view.tag.toString().split("&")

                    val num = list[0]
                    val seat = list[1]
                    firebaseLogEvent(
                        requireContext(),
                        MODIFY_DETAILS,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        MODIFY_DETAILS,
                        "Modify Details"
                    )

                    showSingleTicketUpdateSheet(num, seat)

                }

            }
        } catch (e: Exception) {
            Timber.d("ExceptionMsg ${e.message}")
        }

    }

    override fun onClickOfItem(data: String, position: Int) {
        if (view?.tag != null) {
            when (data) {
                getString(R.string.edit_passenger_details) -> {
                    editPassengerSheet.showEditPassengersSheet(position)
                }

                getString(R.string.view_ticket) -> {
                    val intent = Intent(requireContext(), TicketDetailsActivityCompose::class.java)
                    intent.putExtra("returnToDashboard", false)
                    intent.putExtra(getString(R.string.TICKET_NUMBER), position.toString())
                    startActivity(intent)
                }

                else -> {
                    cancelTicketSheet.showTicketCancellationSheet(position)
                }
            }

        }

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }


    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        resID = if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
        else
            PreferenceUtils.getString("reservationid")?.toLong()

        locale = PreferenceUtils.getlang()
        operatorLogo = PreferenceUtils.getPreference(PREF_LOGO, "")
        if (PreferenceUtils.getPrintingType() == PRINT_TYPE_PINELAB) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val imagePath = getBitmapDirectFromUrl(operatorLogo!!)
                        val output = ByteArrayOutputStream(imagePath!!.getByteCount())
                        imagePath.compress(Bitmap.CompressFormat.JPEG, 100, output)
                        val imageBytes: ByteArray = output.toByteArray()

                        hexvalue = bytesToHex(imageBytes)
                    }catch (e: Exception){
                        if(BuildConfig.DEBUG){
                            e.printStackTrace()
                        }
                    }

                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        }

        domain = PreferenceUtils.getPreference(
            PREF_DOMAIN,
            getString(R.string.empty)
        ) ?: ""


        if ((activity as BaseActivity).getPrivilegeBase()!= null) {
            privilegeResponse = (activity as BaseActivity).getPrivilegeBase()
            currency = privilegeResponse?.currency ?: getString(R.string.rupeeSybbol)
            currencyFormat =
                privilegeResponse?.currencyFormat ?: getString(R.string.indian_currency_format)
            neededCountry = privilegeResponse?.country?.takeIf { it.equals("India", true) } ?: neededCountry
            allowToDisplayCustomerPhoneNumber = privilegeResponse?.tsPrivileges?.allowToDisplayCustomerPhoneNumber ?: false
            var isAgentLogin: Boolean = false
            var loginModelPref: LoginModel = LoginModel()
            loginModelPref = PreferenceUtils.getLogin()
            val role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, requireContext())
            serviceName = PreferenceUtils.getString("ViewReservation_name")
            travelDate = PreferenceUtils.getString("ViewReservation_date")
            driverName = PreferenceUtils.getString("ViewReservation_driverName")
            privilegeResponse?.let {
                if (role == getString(R.string.role_field_officer)) {
                    if ((privilegeResponse?.boLicenses?.updatePassengerTravelStatus == true)
                        && (privilegeResponse?.sendQrCodeToCustomersToAuthenticateBoardingStatus == true)
                    ) {
                        binding.btnFilterScan.visible()
                    } else {
                        binding.btnFilterScan.gone()

                    }
                } else {
                    if ((privilegeResponse?.updatePassengerTravelStatus ?: true)
                        && (privilegeResponse?.sendQrCodeToCustomersToAuthenticateBoardingStatus == true)
                    ) {
                        binding.btnFilterScan.visible()
                    } else {
                        if (neededCountry.equals("india", ignoreCase = true)) {
                            binding.btnFilterScan.visible()
                        } else {
                            binding.btnFilterScan.gone()
                        }

                    }
                }
            }
            operatorName = privilegeResponse?.operatorName ?: ""
            updateLuggageDetailsPostConfirmation = privilegeResponse?.tsPrivileges?.updateLuggageDetailsPostConfirmation ?: false
//            if(privilegeResponse?.isAgentLogin == true) {
//                sortByForIDNAgent()
//                binding.idnBottomMenuAgentLogin.visible()
            binding.bottomMenu.visible()
//            } else {
//                binding.idnBottomMenuAgentLogin.gone()
//                binding.bottomMenu.visible()
//            }
        } else {
            requireContext().toast(requireContext().getString(R.string.server_error))
        }

    }

    override fun onSingleButtonClick(str: String) {
        //super.onSingleButtonClick(str)

        passengerStatus = str
        if (str.contains("scan")) {
            var tempRemarks = ""
            if (str.contains("&")) {
                val scan = str.split("&")
                tempRemarks = scan[1]
            }

            binding.NoResult.gone()
            // startShimmerEffect()
//            pickUpChartApi(chartType)
            val temlist = arrayListOf<String>()
            val seat = listSeatno.toString().replace("[", "").replace("]", "").replace(" ", "")

            val name =
                listSassName.toString().replace("[", "").replace("]", "").replace(" ", "")

            for (i in 0..tempMapList.size.minus(1)) {
                temlist.add("${tempMapList.keyAt(i)}:${tempMapList.valueAt(i)}")
            }
            passengerName = name
            Timber.d("updateBoardedStatusApi:1 ${passengerStatus}")

            updateBoardedStatusApi(
                "",
                qrresponse,
                qrSelectedPnrNumber,
                seat,
                "2",
                temlist,
                tempRemarks
            )

        } else if (str == getString(R.string.luggage_option)) {
            val original = PreferenceUtils.getString("luggageOptionData")?.trim().orEmpty()
            val luggageOptionDetail = original.substringBeforeLast(" ")

            if (!luggagePnrNum.isNullOrEmpty()) {
                if (requireContext().isNetworkAvailable()) {
                    updateLuggageDetailsIntlApi(luggagePnrNum, luggageOptionDetail)
                } else {
                    requireContext().noNetworkToast()
                }
            } else {
                requireContext().toast(getString(R.string.pnr_number_not_found))
            }

        } else if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        } else {
            cityselected = false
        }
    }

    private fun updateLuggageDetailsIntlApi(pnrNumber: String?, luggageOptionDetail: String?) {
        pickUpChartViewModel.updateLuggageOptionIntlApi(
            reqBody = ReqBody(
                apiKey = loginModelPref.api_key,
                locale = locale,
                pnrNumber = pnrNumber,
                luggageDetail = luggageOptionDetail,
            )
        )
    }

    private fun fetchLuggageDetailsIntlApi(pnr: String) {
        pickUpChartViewModel.fetchLuggageDetailsIntlApi(
            apiKey = loginModelPref.api_key,
            pnrNumber = pnr
        )
    }

    private fun fetchLuggageDetailsIntlObserver() {
        pickUpChartViewModel.fetchLuggageDetailsResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        DialogUtils.dialogUpdateLuggageIntl(
                            requireContext(),
                            luggagePnrNum ?: "",
                            it.luggageDesc,
                            singleButtonListener = this
                        )
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        requireContext().toast(getString(R.string.luggageNotFound, luggagePnrNum ?: ""))
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun updateLuggageDetailsIntlObserver() {
        pickUpChartViewModel.updateLuggageOption.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.code == 200) {
                    requireContext().toast(it.message)
                } else {
                    requireContext().toast(it.message)
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    override fun onItemData(view: View, str1: String, str2: String) {
        if (view != null) {
            if (view.tag == getString(R.string.close_chart)) {
                firebaseLogEvent(
                    requireContext(),
                    CLOSE_CHART,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    CLOSE_CHART,
                    "Close chart"
                )
                try {
                    closeChartByCity(str1, str2)
                } catch (e: Exception) {
                    Timber.d("error:  ${e}")
                }
            }
        }
    }


    override fun onItemDataMore(
        view: View,
        str1: String,
        str2: String,
        str3: String,

        ) {

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.city_hub -> {
                firebaseLogEvent(
                    requireContext(),
                    SHOW_ALL,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    SHOW_ALL,
                    "Show all"
                )
                if (binding.cityHub.text == getString(R.string.show_all)) {
                    cityDialog(true)
                } else cityDialog(false)

            }

            R.id.resend_otp -> {
                if (requireContext().isNetworkAvailable()) {
                    resendOtpAndQrCodeAPI(pnrNum!!, seatNum!!)
                    resendOtpAndQrCodeObserver()
                } else requireContext().noNetworkToast()
            }

            R.id.scan_qr_code -> {
                scanTag = "verificationScan"
                qrresponse = ""
                scanScaeen()

            }

            R.id.pickup1 -> {
                binding.tvPickupStage.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimary
                    )
                )
                binding.imgPickupStage.imageTintList =
                    ColorStateList.valueOf((requireContext().resources.getColor(R.color.colorButton)))

                binding.tvDropOffStage.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.imgDropOffStage.imageTintList =
                    ColorStateList.valueOf((requireContext().resources.getColor(R.color.white)))


                changeChart = true
                chartType = "1"
                cityselected = false
                startShimmerEffect()
                binding.NoResult.gone()
                pickUpChartApi(chartType)
                binding.cityHub.text = getString(R.string.show_all)
            }

        }
    }

    fun boarded(passengerNam: String, seatNumber: String, pnrNumber: String, remarks: String) {
        bindingSheet.btnVerifyBoarding.text = getString(R.string.update)
        pnrNum = pnrNumber
        seatNum = seatNumber
        passengerName = passengerNam
        var passengerTempCheck = false
        var isBoardedSms = false
        var isBoardedQr = false
        verifybtnobserver(
            false,
            "",
            qrresponse,
            pnrNumber,
            seatNumber,
            remarks,
            "2"
        )
        bindingSheet.remarksLayout.gone()
        if (privilegeResponse != null) {

            privilegeResponse?.let {
                isBoardedSms = privilegeResponse?.sendOtpToCustomersToAuthenticateBoardingStatus?:false
                isBoardedQr = privilegeResponse?.sendQrCodeToCustomersToAuthenticateBoardingStatus?:false

                if (isBoardedSms || isBoardedQr) {
                    bindingSheet.skipVerification.visible()
                } else {
                    bindingSheet.skipVerification.gone()
                }
                passengerTempCheck = privilegeResponse?.allowToCapturePassAndCrewTemp?:false

                if (passengerTempCheck) {
                    bindingSheet.lpassengerTemp.visible()
                } else {
                    bindingSheet.lpassengerTemp.gone()
                }
            }
        } else {
            requireContext().toast(requireContext().getString(R.string.server_error))
        }
        Timber.d("test123ClickEt::0  $isBoardedSms, $isBoardedQr, $passengerTempCheck")
        if (!isBoardedQr && !isBoardedSms) {
            bindingSheet.bottomSheetHeader.text = getString(R.string.verify_boarding)
            bottomSheetViewVisibility(
                remarks = false,
                temprature = passengerTempCheck,
                otp = isBoardedSms,
                qrCode = isBoardedQr
            )

            if (passengerTempCheck) {
                Timber.d("verificationObserverCheck::0")

                verifybtnobserver(true, "", "", pnrNumber, seatNumber, "", "2")
            } else {
                updateBoardedStatusApi(
                    newOtp,
                    "",
                    pnrNum!!,
                    seatNum!!,
                    "2",
                    templist,
                    remarks
                )
            }

        } else if (isBoardedQr == true && isBoardedSms == true) {
            bottomSheetViewVisibility(
                remarks = false,
                temprature = passengerTempCheck,
                otp = isBoardedSms,
                qrCode = isBoardedQr
            )
            qrresponse = ""
            bindingSheet.scanQrCode.setOnClickListener(this)
            bindingSheet.etenterOtp.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNullOrEmpty()) {
                        if (qrresponse == "") {
                            Timber.d("verificationObserverCheck::1")

                            verifybtnobserver(
                                false,
                                "",
                                qrresponse,
                                pnrNumber,
                                seatNumber,
                                "",
                                "2"
                            )
                        } else {
                            if (privilegeResponse != null
                            ) {

                                privilegeResponse?.let {
                                    if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {
                                        remarksObserver(pnrNum!!)
                                    } else {
                                        Timber.d("verificationObserverCheck::2")

                                        verifybtnobserver(
                                            true,
                                            "",
                                            qrresponse,
                                            pnrNum!!,
                                            seatNum!!,
                                            "",
                                            "2"
                                        )
                                    }
                                }
                            } else {
                                requireContext().toast(requireContext().getString(R.string.server_error))
                            }
                        }
                    } else {
                        if (privilegeResponse != null
                        ) {

                            privilegeResponse?.let {
                                if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {
                                    newOtp = s.toString()
                                    remarksObserver(pnrNum!!)
                                } else {
                                    Timber.d("verificationObserverCheck::3")

                                    verifybtnobserver(
                                        true,
                                        s.toString(),
                                        "",
                                        pnrNumber,
                                        seatNumber,
                                        "",
                                        "2"
                                    )
                                }
                            }
                        } else {
                            requireContext().toast(requireContext().getString(R.string.server_error))
                        }

                    }
                }
            })
        } else if (isBoardedQr == true && isBoardedSms == false) {
            bottomSheetViewVisibility(
                remarks = false,
                temprature = passengerTempCheck,
                otp = isBoardedSms,
                qrCode = isBoardedQr
            )
            bindingSheet.scanQrCode.setOnClickListener(this)
        } else if (isBoardedQr == false && isBoardedSms == true) {
            bottomSheetViewVisibility(
                remarks = false,
                temprature = passengerTempCheck,
                otp = isBoardedSms,
                qrCode = isBoardedQr
            )

            bindingSheet.etenterOtp.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s == "") {
                        Timber.d("verificationObserverCheck::4")

                        verifybtnobserver(false, "", "", "", "", "", "")
                    } else {
                        Timber.d("verificationObserverCheck::5")

                        verifybtnobserver(
                            true,
                            s.toString(),
                            "",
                            pnrNumber,
                            seatNumber,
                            "",
                            "2"
                        )
                    }
                }
            })
        }
        bindingSheet.resendOtp.setOnClickListener(this)
    }

    private fun scanScaeen() {
        val scanner = IntentIntegrator.forSupportFragment(this)
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        scanner.setBeepEnabled(true)
        scanner.setBarcodeImageEnabled(true)
        scanner.initiateScan()

    }

    fun dummy(
        respHash: ArrayList<RespHash>?,
        passengerList: ArrayList<PassengerDetail>?,
        parentVisible: Boolean,
        chartType: String
    ) {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.passengerListSortby.layoutManager = layoutManager
        newStageAdapter =
            PassengerStageAdapter(
                requireActivity(),
                respHash,
                passengerList,
                parentVisible,
                chartType,
                currency,
                currencyFormat,
                neededCountry,
                editPassengerSheet,
                privilegeResponse,

                actionStageDetails = { stageName, landmark ->
                    DialogUtils.dialogStageDetails(
                        requireContext(),
                        stageName,
                        landmark
                    )
                },

                closeChartAction = { cityId, reservationId ->
                    firebaseLogEvent(
                        requireContext(),
                        CLOSE_CHART,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        CLOSE_CHART,
                        "Close chart"
                    )
                    try {
                        closeChartByCity(cityId, reservationId)
                    } catch (e: Exception) {
                        Timber.d("error:  ${e}")
                    }
                },
                boardedSwitchActionClicked = { dialogue: Boolean, boardedSwitch: SwitchCompat, statusText: TextView, seatNumber: String, passengerName: String, pnrNumber: String, remarks: String ->
                    boardedSwitchButton = boardedSwitch
                    boardedStatusText = statusText
                    if (dialogue) {
                        DialogUtils.statusDialog(
                            requireContext(),
                            pnrNumber,
                            seatNumber,
                            boardedSwitchButton,
                            statusText,
                            passengerName,
                            getString(R.string.goBack),
                            getString(R.string.confirm),
                            btnConfirm = { pnr, pName, btnswitch, statusText, statusSelected, sNumber ->
                                passengerStatus = statusSelected

                                if (statusSelected == null) {
                                    requireContext().toast(getString(R.string.please_selecte_an_option))
                                } else if (statusSelected == "2") {
                                    boarded(pName, sNumber, pnr, remarks)
                                } else {
                                    if (privilegeResponse != null
                                    ) {

                                        privilegeResponse?.let {
                                            Timber.d("privilegeoutput12: ${privilegeResponse?.validateRemarksForBoardingStageInMobilityApp}")
                                            if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {
                                                bindingSheet.lpassengerTemp.gone()
                                                bindingSheet.remarksLayout.visible()
                                                bindingSheet.bottomSheetHeader.text =
                                                    getString(R.string.remarks)
                                                bindingSheet.scanLayout.gone()
                                                bindingSheet.otpLayout.gone()
                                                bindingSheet.resendOtp.gone()
                                                bindingSheet.skipVerification.gone()
                                                bindingSheet.etRemarksText.text?.clear()
                                                bindingSheet.etRemarksText.requestFocus()
                                                bottomSheetDialog.show()
                                                qrresponse = ""
                                                newOtp = ""
                                                seatNum = seatNumber
                                                remarksObserver(pnr)
                                            } else {
                                                updateBoardedStatusApi(
                                                    "",
                                                    "",
                                                    pnr,//pnrNumber
                                                    sNumber,//seatNumber
                                                    statusSelected,
                                                    templist,
                                                    remarks
                                                )

                                            }
                                        }
                                    } else {
                                        requireContext().toast(requireContext().getString(R.string.server_error))
                                    }
                                }


                            }
                        )
                        firebaseLogEvent(
                            requireContext(),
                            STATUS,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            STATUS,
                            "Status"
                        )
                    } else {
                        passengerStatus = "2"
                        boarded(passengerName, seatNumber, pnrNumber, remarks)
                    }
                },
                boardedSwitchMultiSeatActionClicked = { dialogue: Boolean, seatNumber: List<String>, pnrNumber: String, remarks: String ->
                    if (dialogue) {
                        DialogUtils.statusMultiSeatDialog(
                            requireContext(),
                            pnrNumber,
                            seatNumber,
                            getString(R.string.goBack),
                            getString(R.string.confirm),
                            btnConfirm = { pnr: String, seatNumbers: String, statusSelected: String ->
                                if (statusSelected == null) {
                                    requireContext().toast(getString(R.string.please_selecte_an_option))
                                } else if (statusSelected == "2") {
                                    boarded("", seatNumbers, pnr, remarks)
                                } else {
                                    if(privilegeResponse != null) {
                                        if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {
                                            bindingSheet.lpassengerTemp.gone()
                                            bindingSheet.remarksLayout.visible()
                                            bindingSheet.bottomSheetHeader.text =
                                                getString(R.string.remarks)
                                            bindingSheet.scanLayout.gone()
                                            bindingSheet.otpLayout.gone()
                                            bindingSheet.resendOtp.gone()
                                            bindingSheet.skipVerification.gone()
                                            bindingSheet.etRemarksText.text?.clear()
                                            bindingSheet.etRemarksText.requestFocus()
                                            bottomSheetDialog.show()
                                            qrresponse = ""
                                            newOtp = ""
                                            seatNum = seatNumbers
                                            remarksObserver(pnr)
                                        } else {
                                            updateBoardedStatusApi(
                                                "",
                                                "",
                                                pnr,//pnrNumber
                                                seatNumbers,//seatNumber
                                                statusSelected,
                                                templist,
                                                remarks
                                            )
                                        }
                                    } else {
                                        requireContext().toast(requireContext().getString(R.string.server_error))
                                    }
                                }
                            }
                        )
                        firebaseLogEvent(
                            requireContext(),
                            STATUS,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            STATUS,
                            "Status"
                        )
                    }
                },
                actionModifyPass = { seatNumber, pnrNumber ->
                    firebaseLogEvent(
                        requireContext(),
                        MODIFY_DETAILS,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        MODIFY_DETAILS,
                        "Modify Details"
                    )

                    showSingleTicketUpdateSheet(pnrNumber, seatNumber)
                },

                actionLuggagePass = { seatNumber: String, passengerName: String, pnrNumber: String, passengerAge: String, passengerStatus: String, passengerSex: String ->
                    skipQrCcode = false
                    DialogUtils.luggageDialogBox(
                        requireContext(),
                        pnrNumber,
                        passengerName,
                        seatNumber,
                        passengerStatus,
                        passengerAge,
                        passengerSex,
                        dialogueLuggage = { amount, quantity, item ->
                            updateCargodetails(
                                pnrNumber,
                                seatNumber,
                                passengerStatus,
                                amount,
                                quantity,
                                item
                            )
                        }
                    )
                },
                actionLuggageMultiSeatPass = { passengerName: String, passengerAge: String, passengerSex: String, seatNumbers: List<String>, pnrNumber: String, pnrStatus: String ->
                    skipQrCcode = false
                    DialogUtils.luggageDialogBoxMultiSeat(
                        requireContext(),
                        passengerName,
                        passengerAge,
                        passengerSex,
                        pnrStatus,
                        seatNumbers,
                        dialogueLuggage = { seatNumbers, amount, quantity, item ->
                            updateCargodetails(
                                pnrNumber,
                                seatNumbers,
                                pnrStatus,
                                amount,
                                quantity,
                                item
                            )

                        }
                    )
                },
                onCallClickListener = { phoneNumber: String ->
                    if (allowToDisplayCustomerPhoneNumber == true) {
                        showCallConfirmationBottomSheet(phoneNumber)
                    } else {
                        handleCallRequest(phoneNumber)
                    }
                },
                actionLuggageOptionPass = { pnrNumber: String ->
                    luggagePnrNum = pnrNumber
                    fetchLuggageDetailsIntlApi(pnrNumber)
                },
                onIvrCallClick = {
                    val respItem = respHash?.get(it)
                    if (respItem != null) {
                        openIvrCallActivity(respHash,respItem)
                    }
                }
            ) {
                openDialog = true
                callTicketDetailsV1Api(it)
            }
        binding.passengerListSortby.adapter = newStageAdapter
    }

    private fun openIvrCallActivity(respHashList: ArrayList<RespHash>,currentRespHash: RespHash){
        val intent = Intent(requireActivity(),IvrCallingActivity::class.java)
        intent.putExtra("resp_hash", jsonToString(currentRespHash))
        PreferenceUtils.putRespHashBoardingList(respHashList)
        requireActivity().startActivity(intent)
    }

    fun bottomSheetViewVisibility(
        remarks: Boolean,
        temprature: Boolean,
        otp: Boolean,
        qrCode: Boolean
    ) {
        Timber.d("checkBottomSheetVisibility: 0")
        if (remarks) {
            bindingSheet.bottomSheetHeader.text = requireContext().getString(R.string.remarks)
            bindingSheet.remarksLayout.visible()
            bindingSheet.etRemarksText.clearFocus()
            bindingSheet.etRemarksText.text?.clear()
            bindingSheet.skipVerification.gone()
            bindingSheet.otpLayout.gone()
            bindingSheet.resendOtp.gone()
            bindingSheet.scanLayout.gone()
            bindingSheet.lpassengerTemp.gone()
        } else {
            bindingSheet.bottomSheetHeader.text = getString(R.string.verify_boarding)

            /*Temprature View Visibility*/
            if (temprature) {
                bindingSheet.lpassengerTemp.visible()
                bindingSheet.etPassengerTemp.text?.clear()
                bindingSheet.etPassengerTemp.clearFocus()
            } else {
                bindingSheet.lpassengerTemp.gone()
            }

            if (otp && qrCode) {
                Timber.d("checkBottomSheetVisibility: 1")

                bindingSheet.scanLayout.visible()
                bindingSheet.otpLayout.visible()
                bindingSheet.resendOtp.visible()
                bindingSheet.resendText.text = requireContext().getString(R.string.resend_sms)
                bindingSheet.resendSmsImg.visible()
                bindingSheet.resendQrImg.gone()
                bindingSheet.etenterOtp.text?.clear()
                bindingSheet.skipVerification.visible()
            } else if (otp && !qrCode) {
                Timber.d("checkBottomSheetVisibility: 2")

                bindingSheet.scanLayout.gone()
                bindingSheet.otpLayout.visible()
                bindingSheet.resendOtp.visible()
                bindingSheet.resendText.text = requireContext().getString(R.string.resend_otp)
                bindingSheet.resendSmsImg.visible()
                bindingSheet.resendQrImg.gone()
                bindingSheet.etenterOtp.text?.clear()
                bindingSheet.skipVerification.visible()

            } else if (!otp && qrCode) {
                Timber.d("checkBottomSheetVisibility: 3")

                bindingSheet.scanLayout.visible()
                bindingSheet.otpLayout.gone()
                bindingSheet.resendOtp.visible()
                bindingSheet.resendText.text = requireContext().getString(R.string.resend_qr)
                bindingSheet.resendSmsImg.gone()
                bindingSheet.resendQrImg.visible()
                bindingSheet.skipVerification.visible()

            } else {
                Timber.d("checkBottomSheetVisibility: 4")

                bindingSheet.scanLayout.gone()
                bindingSheet.otpLayout.gone()
                bindingSheet.resendOtp.gone()
                bindingSheet.skipVerification.gone()

            }
        }

        bindingSheet.skipVerification.setOnClickListener {
            Timber.d("skipQrTEst: 0")
            skipQrCcode = true
            var remarksText = ""
            var temp = arrayListOf<String>()
            val temp2 = bindingSheet.etPassengerTemp.text.toString()
            if (temprature) {
                if (temp2.isNullOrEmpty()) {

                    requireContext().toast(requireContext().getString(R.string.please_enter_temperature))
                } else {
                    val floatTemp = temp2.toFloat()
                    if (floatTemp in 89.00..108.00) {
                        temp =
                            arrayListOf("${seatNum!!}:$floatTemp")
                    }
                    updateBoardedStatusApi(
                        "",
                        "",
                        pnrNum!!,
                        seatNum!!,
                        "2",
                        temp,
                        remarksText
                    )
                }

            } else {
                updateBoardedStatusApi(
                    "",
                    "",
                    pnrNum!!,
                    seatNum!!,
                    "2",
                    temp,
                    remarksText
                )
            }


        }

        if (remarks || temprature || otp || qrCode) {
            bottomSheetDialog.show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Timber.d("qrScanResult :  ${result.contents}")
                    qrresponse = result.contents
                    if (scanTag == "QuickScan") {

                        val pnrNumber = result.contents
                        if (pnrNumber.isNotEmpty()) {
                            if (requireContext().isNetworkAvailable()) {
                                val fromQr= !pnrNumber.contains("PNR!", true)
                                getTicketDetailsApi(pnrNumber, fromQr)
                                setTicketDetailsObserver()
                            } else requireContext().noNetworkToast()
                        }
                    } else {
                        if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {
                            bindingSheet.remarksLayout.visible()
                        } else {
                            bindingSheet.remarksLayout.gone()
                        }
                        if (privilegeResponse?.allowToCapturePassAndCrewTemp == true) {
                            bindingSheet.lpassengerTemp.visible()
                        } else {
                            bindingSheet.lpassengerTemp.gone()
                        }
                        bindingSheet.scanqrText.text = "QR Scanned"
                        if (privilegeResponse != null) {

                            privilegeResponse?.let {
                                if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {
                                    remarksObserver(pnrNum!!)
                                } else {
                                    Timber.d("verificationObserverCheck::6")

                                    verifybtnobserver(
                                        true,
                                        "",
                                        qrresponse,
                                        pnrNum!!,
                                        seatNum!!,
                                        "",
                                        "2"
                                    )
                                }
                            }
                        } else {
                            requireContext().toast(requireContext().getString(R.string.server_error))
                        }
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    fun cityDialog(reset: Boolean) {
        DialogUtils.cityFilterDialog(
            requireActivity(),
            citySqeName,
            getString(R.string.filter_by),
            getString(R.string.apply_filter),
            reset,
            true,
            applyCityFilter = { cityName, CitySelected ->

                if (CitySelected) {
                    binding.cityHub.text = cityName
                    if (citySqeId.size > citySqeName.indexOf(cityName)) {
                        val position = citySqeName.indexOf(cityName)
                        if (position != -1)
                            selected = citySqeId[position]
                    }
                    cityselected = selected != 0
                    startShimmerEffect()
                    pickUpChartApi(chartType)
                } else {
                    cityselected = CitySelected
                }

            }
        )
    }

    fun remarksObserver(pnrNumber: String) {
        bindingSheet.etRemarksText.addTextChangedListener(object : TextWatcher {

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
                    Timber.d("verificationObserverCheck::7")

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
                    Timber.d("verificationObserverCheck::8")

                    verifybtnobserver(
                        true,
                        newOtp,
                        qrresponse,
                        pnrNumber,//pnrNumber
                        seatNum!!,
                        s.toString(),
                        passengerStatus.toString()
                    )
                }
            }
        })
    }

    private fun setUpdatePrintCountObserver() {
        ticketDetailsComposeViewModel.updatePrintCountData.observe(viewLifecycleOwner) {
            if (it != null && it.code == 200) {
                if (requireContext().isNetworkAvailable()) {
                    callTicketDetailsV1Api(pnrTicketNumber.toString())
                    ticketDetailsComposeViewModel.resetMenuAction()
                } else {
                    requireContext().noNetworkToast()

                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }


    private fun getTicketDetailsApi(pnrNumber: String, fromQrScan: Boolean) {
        ticketDetailsViewModel.ticketDetailsApi(
            loginModelPref.api_key,
            pnrNumber,
            true,
            fromQrScan, locale!!,
            ticket_details_method_name
        )


    }

    private fun setTicketDetailsObserver() {
        ticketDetailsViewModel.dataTicketDetails.observe(this) {
            if (it != null) {
                if (it.code == 200) {
                    val passdetail =
                        arrayListOf<com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail?>()
                    qrSelectedPnrNumber = it.body.ticketNumber ?: ""
                    qrSelectedResId = it.body.reservationId.toString()

                    it.body.passengerDetails?.forEach {
                        if (it!!.boardingStatus!!.lowercase(Locale.getDefault()) == "yet to board") {
                            passdetail.add(it)
                        }
                    }
                    if (passdetail.isEmpty()) {
                        requireContext().toast(getString(R.string.all_passengers_are_boarded_fro_this_pnr))
                    } else {
                        DialogUtils.dialogScanStatus(
                            requireContext(),
                            passdetail,
                            it.body.ticketNumber ?: "",
                            getString(R.string.verify),
                            this,
                            this
                        )
                    }


                } else
                    if (it.message != null) {
                        it.message.let { it1 ->
                            requireContext().toast(it1)
                        }
                    }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
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


    private fun initRefreshListner() {
        binding.NoResult.gone()
        startShimmerEffect()
        pickUpChartApi(chartType)

    }


    private fun startShimmerEffect() {
        binding.mainLayout.gone()
        binding.NoResult.gone()
        binding.passengerListSortby.gone()
        binding.rvPnrGroupPassengerList.gone()
        binding.cardTotalBookingStatus.gone()
        binding.refreshCard.gone()
        binding.shimmerLayout.visible()
        binding.shimmerLayout.startShimmer()

    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.shimmerLayout.gone()
        binding.mainLayout.visible()
        binding.cardTotalBookingStatus.visible()
        if (neededCountry.equals("india", ignoreCase = true)) {
            binding.refreshCard.visible()
        }
        //binding.refreshCard.visible()
        if (binding.shimmerLayout.isShimmerStarted) {
            binding.shimmerLayout.stopShimmer()

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
            Timber.d("privilegeOutput1: $remarks")

            bindingSheet.btnVerifyBoarding.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            bindingSheet.btnVerifyBoarding.setOnClickListener {
                if (!remarks.isNullOrEmpty()) {
                    Timber.d("privilegeOutput2: True")

                    val temp2 = bindingSheet.etPassengerTemp.text.toString()
                    if (temp2.isNullOrEmpty()) {
                        val templistSingle = listOf("")
                        Timber.d("updateBoardedStatusApi:5 ${passengerStatus}")

                        updateBoardedStatusApi(
                            otp,
                            qr,
                            pnrNumber,
                            seatNumber,
                            status,
                            templistSingle,
                            remarks
                        )
                    } else {
                        try {
                            val floatTemp = temp2.toFloat()
                            if (floatTemp in 89.00..108.00) {
                                val templistSingle = listOf("$seatNumber:$temp2")
                                Timber.d("updateBoardedStatusApi:6${passengerStatus}")

                                updateBoardedStatusApi(
                                    otp,
                                    qr,
                                    pnrNumber,
                                    seatNumber,
                                    status,
                                    templistSingle,
                                    remarks
                                )
                            } else {
                                requireContext().toast(requireContext().getString(R.string.temp_range_validation))
                            }
                        } catch (e: Exception) {
                            requireContext().toast(e.message.toString())

                        }


                    }

                } else {
                    Timber.d("privilegeOutput3: True")
                    val temp2 = bindingSheet.etPassengerTemp.text.toString()
                    if (temp2.isNullOrEmpty()) {
                        val templistSingle = listOf("")
                        Timber.d("updateBoardedStatusApi:8${passengerStatus}")
                        updateBoardedStatusApi(
                            otp,
                            qr,
                            pnrNumber,
                            seatNumber,
                            status,
                            templistSingle,
                            ""
                        )
                    } else {
                        try {
                            val floatTemp = temp2.toFloat()
                            if (floatTemp in 89.00..108.00) {
                                val templistSingle = listOf("$seatNumber:$temp2")
                                Timber.d("updateBoardedStatusApi:9${passengerStatus}")

                                updateBoardedStatusApi(
                                    otp,
                                    qr,
                                    pnrNumber,
                                    seatNumber,
                                    status,
                                    templistSingle,
                                    ""
                                )
                            } else {
                                requireContext().toast(requireContext().getString(R.string.temp_range_validation))
                            }
                        } catch (e: Exception) {
                            requireContext().toast(e.message.toString())

                        }
                    }
                }
            }
        } else {
            bindingSheet.btnVerifyBoarding.setBackgroundColor(resources.getColor(R.color.button_default_color))
            bindingSheet.btnVerifyBoarding.setOnClickListener {
                requireContext().toast("please fill details")
            }
        }
    }

    private fun closeChartByCity(cityId: String, stageId: String) {
        pickUpChartViewModel.cityPickupChartByStage(
            com.bitla.ts.domain.pojo.city_pickup_by_chart_stage.request.ReqBody(
                loginModelPref.api_key,
                cityId,
                resID.toString(),
                stageId,
                locale = locale
            ),
            city_pickup_chart_by_stage
        )
    }

    private fun cityPickupByStageObserver() {

        pickUpChartViewModel.cityPickupChartByStageResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.code == 200) {
                    requireContext().toast(it.header)
                    startShimmerEffect()
                    pickUpChartApi(chartType)
                } else {
                    requireContext().toast(it.result.message)
                }

            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    override fun onClickMuliView(
        view: View,
        view2: View,
        view3: View,
        view4: View,
        passengerName: String,
        seatNum: String
    ) {
    }

    override fun onClickAdditionalData(view0: View, view1: View) {

    }

    override fun onLeftButtonClick(
        view: View?,
        view1: View?,
        view2: View?,
        view3: View?,
        resId: String
    ) {
    }

    override fun onRightButtonClick(
        view0: View?,
        view1: View?,
        view2: View?,
        view3: View?,
        resId: String,
        seatNumber: String
    ) {
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
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle(getString(R.string.bluetooth_printer_selection))
                    alertDialog.setItems(
                        items
                    ) { dialogInterface, i ->
                        val index = i
                        if (index == -1) {
                            selectedDevice = null
                        } else {
                            selectedDevice = bluetoothDevicesList[index]
                            if (isTicketDetailsPrintClicked == true) {
                                isTicketDetailsPrintClicked = false
                                printBluetoothTicketDetails()
                            } else {
                                if (privilegeResponse?.tsPrivileges?.thermalPrintForTsApp == true)
                                    printThermalPrint()
                                else
                                    printBluetooth()
                            }
                        }

                    }
                    val alert = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()
                } else
                    requireContext().toast(getString(R.string.no_paired_devices))
            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    private fun printThermalPrint() {
        AsyncBluetoothEscPosPrint(
            requireContext(),
            object : AsyncEscPosPrint.OnPrintFinished() {
                @SuppressLint("TimberArgCount")
                override fun onError(
                    asyncEscPosPrinter: AsyncEscPosPrinter?,
                    codeException: Int
                ) {
                    Timber.d(
                        "Async.OnPrintFinished",
                        "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                    )
                }

                @SuppressLint("TimberArgCount")
                override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                    Timber.d(
                        "Async.OnPrintFinished",
                        "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                    )
                }
            }
        )
            .execute(this.getAsyncEscPosPrinterThermal(selectedDevice))
    }

    private fun printBluetooth() {
        AsyncBluetoothEscPosPrint(
            requireContext(),
            object : AsyncEscPosPrint.OnPrintFinished() {
                @SuppressLint("TimberArgCount")
                override fun onError(
                    asyncEscPosPrinter: AsyncEscPosPrinter?,
                    codeException: Int
                ) {
                    Timber.d(
                        "Async.OnPrintFinished",
                        "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                    )
                }
                @SuppressLint("TimberArgCount")
                override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                    Timber.d(
                        "Async.OnPrintFinished",
                        "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                    )

                    lifecycleScope.launch {
                        if (requireContext().isNetworkAvailable()) {
                            ticketDetailsComposeViewModel.updatePrintCountApi(
                                pnrTicketNumber ?: "",
                                true,
                                loginModelPref.api_key
                            )
                            delay(1500L)
                        } else {
                            requireContext().noNetworkToast()
                        }
                    }

                }
            }
        )
            .execute(this.getAsyncEscPosPrinter(selectedDevice))
    }

    private fun printBluetoothTicketDetails() {
        AsyncBluetoothEscPosPrint(
            requireContext(),
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
            .execute(this.getAsyncEscPosPrinterTicketDetails(selectedDevice))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun enableDeviceBluetooth() {
        val bluetoothManager = requireContext().getSystemService(BluetoothManager::class.java)
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

    private fun getAsyncEscPosPrinterThermal(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)

        var bluetoothTemplate = ""
        val bluetoothTemplateList = mutableListOf<String>()

        if (passengerlist.isNotEmpty()) {
            if (bmpLogo != null) {
                hexaDecimalString = PrinterTextParserImg.bitmapToHexadecimalString(
                    printer, bmpLogo
                )
                bluetoothTemplateList.add("[C]<img>${hexaDecimalString}</img>")
            } else {
                bluetoothTemplateList.add("[C]<b>$operatorName</b>")
            }
            bluetoothTemplateList.add("\n[C]SERVICE MANIFEST\n[C]${serviceName}")

            val currentDateTime = getTodayDateWithTime()

            bluetoothTemplateList.add("\n[L]<b>------------------------------</b>")
            bluetoothTemplateList.add("\n[L]<b>DOJ:</b> $travelDate $deptTime")
            bluetoothTemplateList.add("\n[L]<b>Name of Supervisor:</b> $driverName")
            bluetoothTemplateList.add("\n[L]<b>Bus No:</b> ${coachNumber}")
            bluetoothTemplateList.add("\n[L]<b>SPJ No:</b> ")
            bluetoothTemplateList.add("\n[L]<b>Manifest Print Date & Time:</b>\n[L]$currentDateTime")
            bluetoothTemplateList.add("\n[L]<b>------------------------------</b>")

            val boardingPointList = passengerlist.map{ it.stage_name }.distinct()
            val droppingPointList = passengerlist.map{ it.dropping_point }.distinct()
            val totalAmount = passengerlist.sumOf{ it.ticket_fare ?: 0.0}
            var filteredPassengerList : List<PassengerDetailX>

            for (i in 0..boardingPointList.size.minus(1)) {
                for (j in 0..droppingPointList.size.minus(1)) {
                    filteredPassengerList = passengerlist.filter { it.stage_name == boardingPointList[i] && it.dropping_point == droppingPointList[j]}

                    if (filteredPassengerList.isNotEmpty()) {
                        bluetoothTemplateList.add("\n[L]<b>Boarding:</b> ${boardingPointList[i]}")
                        bluetoothTemplateList.add("\n[L]<b>Dropping:</b> ${droppingPointList[j]}")
                        bluetoothTemplateList.add("\n[L]<b>==============================</b>")

                        for (passenger in filteredPassengerList) {
                            bluetoothTemplateList.add("\n[L]<b>Seat No:</b> ${passenger.seat_number}[R]<b>PNR:</b> ${passenger.pnr_number}")
                            bluetoothTemplateList.add("\n[L]<b>Passenger Name:</b> ${passenger.passenger_name}")
                            bluetoothTemplateList.add("\n[L]<b>------------------------------</b>")
                        }
                        bluetoothTemplateList.add("\n")
                    }
                }
            }

            bluetoothTemplateList.add("\n[L]Filled By:\n")
            bluetoothTemplateList.add("\n[L]<b>Total Passenger:</b> ${passengerlist.size}")
            bluetoothTemplateList.add("\n[L]<b>Total Amount:</b> ${privilegeResponse?.currency} ${totalAmount?.convert(privilegeResponse?.currencyFormat)}")
        }

        bluetoothTemplate =
            bluetoothTemplateList.toString().removePrefix("[").removeSuffix("]").replace(",", "")

        return printer.addTextToPrint(
            bluetoothTemplate
        )
    }


    private fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)

        var bluetoothTemplate = ""
        val bluetoothTemplateList = mutableListOf<String>()
        val stageSummaryList = mutableListOf<PrintStageSummary>()
        if (passengerlist.isNotEmpty()) {
            bluetoothTemplateList.add("[C]<b>$operatorName</b>\n[C]SERVICE MANIFEST\n[C]${serviceName}\n[C]${travelDate}\n")
            var boardingCount = 1
            var droppingCount = 1

            for (i in 0..passengerlist.size.minus(1)) {
                val seatNo = "Seat: ${passengerlist[i].seat_number}\n"
                val name = "Name: ${passengerlist[i].passenger_name}\n"
                val pnrNo = "PNR number: ${passengerlist[i].pnr_number}\n"
                val boardingStage = "Boarding stage: ${passengerlist[i].stage_name}\n"
                val bookedBy = "Booked By: ${passengerlist[i].booked_by}\n"
                bluetoothTemplateList.add(seatNo)
                bluetoothTemplateList.add(name)
                bluetoothTemplateList.add(pnrNo)
                bluetoothTemplateList.add(boardingStage)
                bluetoothTemplateList.add(bookedBy)
                bluetoothTemplateList.add("\n")

                if (stageSummaryList.any { it.boarding_dropping == passengerlist[i].stage_name }) {
                    boardingCount++
                    val index =
                        stageSummaryList.indexOfFirst { it.boarding_dropping == passengerlist[i].stage_name }
                    if (index != -1) {
                        stageSummaryList.removeAt(index)
                    }
                } else
                    boardingCount = 1
                val stageSummary =
                    PrintStageSummary(passengerlist[i].stage_name ?: "", boardingCount, true)
                stageSummaryList.add(stageSummary)
                if (stageSummaryList.any { it.boarding_dropping == passengerlist[i].dropping_point }) {
                    droppingCount++

                    val index =
                        stageSummaryList.indexOfFirst { it.boarding_dropping == passengerlist[i].dropping_point }
                    if (index != -1) {
                        stageSummaryList.removeAt(index)
                    }
                } else
                    droppingCount = 1
                val stageSummary1 =
                    PrintStageSummary(passengerlist[i].dropping_point ?: "", droppingCount, false)
                stageSummaryList.add(stageSummary1)

            }
        }


        bluetoothTemplateList.add("\n[C]<b>STAGE SUMMARY</b>\n[C]Boarding At\n")
        for (i in 0..stageSummaryList.size.minus(1)) {
            if (stageSummaryList[i].isBoarding) {
                bluetoothTemplateList.add("${stageSummaryList[i].boarding_dropping}: ${stageSummaryList[i].count}\n")
            }
        }

        bluetoothTemplateList.add("\n[C]Drop Off\n")
        for (i in 0..stageSummaryList.size.minus(1)) {
            if (!stageSummaryList[i].isBoarding) {
                bluetoothTemplateList.add("${stageSummaryList[i].boarding_dropping}: ${stageSummaryList[i].count}\n")
            }
        }


        bluetoothTemplate =
            bluetoothTemplateList.toString().removePrefix("[").removeSuffix("]").replace(",", "")

        Timber.d("bluetoothPrintTemplate $bluetoothTemplate")

        return printer.addTextToPrint(
            bluetoothTemplate
        )
    }


    private fun getAsyncEscPosPrinterTicketDetails(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        if (bmpLogo != null) {
            hexaDecimalString = PrinterTextParserImg.bitmapToHexadecimalString(
                printer, bmpLogo
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

        getBarCodePrint(printer)
        return printer.addTextToPrint(
            bluetoothPrintTemplate?.trim()
        )
    }

    private fun callTicketDetailsV1Api(pnrNumber: String) {

        pnrTicketNumber = pnrNumber
        val locale = PreferenceUtils.getlang()
        binding.progressBar.visible()
        ticketDetailsComposeViewModel.ticketDetailsApi(
            loginModelPref.api_key,
            pnrNumber,
            jsonFormat = true,
            isQrScan = false,
            locale = locale,
            apiType = ticket_details_method_name,
            false,
            false
        )
    }

    private fun setTicketDetailsV1Observer() {
        ticketDetailsComposeViewModel.dataTicketDetails.observe(viewLifecycleOwner) {
            binding.progressBar.gone()
            if (it != null) {

                when (it.code) {
                    200 -> {
                        if (it.body != null && it.body?.code == 419) {
                            requireContext().toast(it.body?.message ?: getString(R.string.invalid_pnr))
                        } else {
                            ticketData = it.body
                            qrCodeInput = it.body?.qrCodeData ?: ""
                            originalTemplate = it.body?.tsAppPrintTemplate
                            bluetoothPrintTemplate = it.body?.tsAppPrintTemplate
                            reprintChargesAmount = it.body?.reprintCharges

                        ticketDetailsComposeViewModel.setTicketDetailsData(it, getString(
                            R.string.notAvailable
                        ))

                        var primaryPassengerName = ""
                        var primaryPassengerMobileNumber = ""

                            if (it.body?.passengerDetails?.isNotEmpty() == true) {
                                primaryPassengerName = it.body.passengerDetails[0]?.name ?: ""
                                primaryPassengerMobileNumber = it.body.passengerDetails[0]?.mobile ?: ""
                            }

                            if (openDialog) {
                                DialogUtils().dialogTicketDetails(
                                    context = requireContext(),
                                    primaryPassengerName = primaryPassengerName,
                                    seatNumbers = it.body?.seatNumbers ?: "",
                                    pnrNumber = it.body?.ticketNumber ?: "",
                                    mobileNumber = primaryPassengerMobileNumber,
                                    fromAndTo = "${it.body?.origin ?: ""} - ${it.body?.destination ?: ""}",
                                    fare = "${privilegeResponse?.currency} ${
                                        it.body?.totalFare?.toDouble() ?: 0.0
                                    }",
                                    printTicketCallback = { pnrNumber ->
                                        if (reprintChargesAmount == null || reprintChargesAmount == 0.0) {
                                            isTicketDetailsPrintClicked = true
                                            printTicketDetailsDialog()
                                        } else {
                                            DialogUtils.twoButtonDialog(
                                                requireContext(),
                                                "${getString(R.string.reprint_charges)}",
                                                getString(R.string.reprint_charges_confirmation, String.format("$currency %.2f", reprintChargesAmount)),
                                                getString(R.string.cancel),
                                                getString(R.string.confirm),
                                                object : DialogButtonListener {
                                                    override fun onLeftButtonClick() {

                                                    }
                                                    override fun onRightButtonClick() {
                                                        isTicketDetailsPrintClicked = true
                                                        printTicketDetailsDialog()
                                                    }

                                                }
                                            )
                                        }
                                    },
                                    viewTicketCallback = { pnrNumber ->
                                        val intent = Intent(
                                            requireContext(),
                                            TicketDetailsActivityCompose::class.java
                                        )
                                        intent.putExtra("returnToDashboard", false)
                                        intent.putExtra(getString(R.string.TICKET_NUMBER), pnrNumber)
                                        startActivity(intent)
                                    }
                                )
                                openDialog = false
                            }
                        }
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            object : DialogSingleButtonListener {
                                override fun onSingleButtonClick(str: String) {
                                    if (str == getString(R.string.unauthorized)) {

                                        PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
                                        val intent = Intent(
                                            requireContext(),
                                            LoginActivity::class.java
                                        )
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        requireContext().startActivity(intent)
                                        requireActivity().finish()
                                    }
                                }

                            })*/

                        (activity as BaseActivity).showUnauthorisedDialog()

                        firebaseLogEvent(
                            requireContext(),
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
                            requireContext().toast(it.message)
                        } else {
                            requireContext().toast(getString(R.string.server_error))
                        }
                        firebaseLogEvent(
                            requireContext(),
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
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun printTicket() {
        checkPermissions()
        bluetoothPrint()
    }

    private fun bluetoothPrint() {

        Timber.d("bluetoothPrintTemplate before $bluetoothPrintTemplate ")

        if (bluetoothPrintTemplate?.contains("</img>") == true) {
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

        if (bluetoothPrintTemplate != null && ticketData != null && privilegeResponse != null) {
            commonReplacementPrint()
            if (bluetoothPrintTemplate?.contains("FOR_EACH_SEAT") == false) {
                singleSeatBluetoothPrint()
            } else {
                multiSeatBluetoothPrint()
            }
        }

        Timber.d("bluetoothPrintTemplate after $bluetoothPrintTemplate ")
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
            if (ticketData?.passengerDetails != null && ticketData?.passengerDetails?.isNotEmpty()!!) {

                val multiSeats = mutableListOf<String>()
                for (i in 0..ticketData?.passengerDetails?.size?.minus(1)!!) {
                    /*if (i > 0) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("IMAGE", "\nIMAGE")
                    }*/

                    bluetoothPrintTemplate= originalTemplate
                    commonReplacementPrint()

                    if (ticketData?.passengerDetails?.size!! != 1) {
                        if (i < ticketData?.passengerDetails?.size?.minus(1)!!) {
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
                            " ${ticketData?.passengerDetails!![i]?.seatNumber} "
                        )
                    } else {
                        if (i > 0) {
                            bluetoothPrintTemplate =
                                ticketData?.passengerDetails!![i.minus(1)]?.seatNumber?.let {
                                    bluetoothPrintTemplate?.replace(
                                        " $it ",
                                        " ${ticketData?.passengerDetails!![i]?.seatNumber} "
                                    )
                                }
                        }
                    }
                    if (bluetoothPrintTemplate?.contains("PASSENGER_EACH_NAME")!!) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "PASSENGER_EACH_NAME",
                            ticketData?.passengerDetails!![i]?.name ?: ""
                        )
                    } else {
                        if (i > 0) {
                            bluetoothPrintTemplate =
                                ticketData?.passengerDetails!![i.minus(1)]?.name?.let {
                                    bluetoothPrintTemplate?.replace(
                                        it,
                                        ticketData?.passengerDetails!![i]?.name ?: ""
                                    )
                                }
                        }
                    }


                    if (bluetoothPrintTemplate?.contains("TICKET_EACH_FARE")!!) {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "TICKET_EACH_FARE",
                            ticketData?.passengerDetails!![i]?.netFare?.toDouble()
                                ?.convert(privilegeResponse?.currencyFormat)
                                ?: ""
                        )
                    } else {
                        if (i > 0) {
                            bluetoothPrintTemplate =
                                bluetoothPrintTemplate?.replace(
                                    "${
                                        ticketData?.passengerDetails!![i]?.netFare?.toDouble()
                                            ?.convert(privilegeResponse?.currencyFormat)
                                    }",
                                    ticketData?.passengerDetails!![i]?.netFare?.toDouble()
                                        ?.convert(privilegeResponse?.currencyFormat) ?: ""
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

                    if (!ticketData?.passengerDetails!![i]?.mealCoupons.isNullOrEmpty()) {
                        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_LOOP")!!) {

                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_COUPON_LOOP",
                                ticketData?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "")
                                    .replace("]", "").replace(",", "\n").replace(" ", "")
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate =
                                    ticketData?.passengerDetails!![i.minus(1)]?.mealCoupons?.toString()
                                        ?.let {
                                            bluetoothPrintTemplate?.replace(
                                                it,
                                                ticketData?.passengerDetails!![i]?.mealCoupons.toString()
                                            )
                                        }
                            }
                        }

                        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_NUMBER")!! && !ticketData?.passengerDetails!![i]?.mealCoupons.isNullOrEmpty()) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketData?.passengerDetails!![i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "")
                            )
                        } else {
//                            if (i > 0) {
//                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
//                                    ticketData?.passengerDetails!![i.minus(1)]?.mealCoupons.toString()
//                                        .replace("[", "").replace("]", ""),
//                                    ticketData?.passengerDetails!![i]?.mealCoupons.toString()
//                                        .replace("[", "").replace("]", "")
//                                )
//                            }
                        }

                        if (bluetoothPrintTemplate?.contains("MEAL_COUNT")!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_COUNT",
                                ticketData?.passengerDetails!![i]?.mealCoupons?.size.toString()
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    ticketData?.passengerDetails!![i.minus(1)]?.mealCoupons?.size.toString(),
                                    ticketData?.passengerDetails!![i]?.mealCoupons?.size.toString()
                                )
                            }
                        }
                    } else {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "MEAL_COUPON_LOOP", ""
                        )
                    }

                    if (ticketData?.passengerDetails!![i]?.selectedMealType.toString()
                            .isNotEmpty() && ticketData?.passengerDetails!![i]?.selectedMealType.toString() != "-"
                    ) {
                        if (bluetoothPrintTemplate?.contains("MEAL_TYPE")!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "MEAL_TYPE",
                                ticketData?.passengerDetails!![i]?.selectedMealType.toString()
                            )
                        } else {
                            if (i > 0) {
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    ticketData?.passengerDetails!![i.minus(1)]?.selectedMealType.toString(),
                                    ticketData?.passengerDetails!![i]?.selectedMealType.toString()
                                )
                            }
                        }
                    } else {
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "MEAL_TYPE",
                            "-"
                        )
                    }


                    if (!ticketData?.insuranceTransDetails?.details.isNullOrEmpty()) {
                        if (bluetoothPrintTemplate?.contains("ALL_INSURANCE_NUMBERS")!!) {
                            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                "ALL_INSURANCE_NUMBERS",
                                "${getString(R.string.seat)} (${getString(R.string.booking_code)}): ${
                                    ticketData?.insuranceTransDetails?.details?.get(
                                        i
                                    )?.seat_no
                                } (${ticketData?.insuranceTransDetails?.details?.get(i)?.info?.booking_code}) ${
                                    getString(
                                        R.string.policy
                                    )
                                }: ${ticketData?.insuranceTransDetails?.details?.get(i)?.info?.policy_number}"
                            )
                        } else {
                            val newPolicyNumber =
                                ticketData?.insuranceTransDetails?.details?.get(i)?.info?.policy_number?.trim()
                            val newSeatNo =
                                "${ticketData?.insuranceTransDetails?.details?.get(i)?.seat_no}"
                            if (i > 0) {
                                val oldPolicyNumber =
                                    ticketData?.insuranceTransDetails?.details?.get(i.minus(1))?.info?.policy_number?.trim()
                                val oldSeatNo =
                                    "${ticketData?.insuranceTransDetails?.details?.get(i.minus(1))?.seat_no}"
                                bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                                    oldValue = "(${
                                        ticketData?.insuranceTransDetails?.details?.get(
                                            i.minus(
                                                1
                                            )
                                        )?.info?.booking_code
                                    })",
                                    newValue = "(${ticketData?.insuranceTransDetails?.details?.get(i)?.info?.booking_code})"
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
                        ticketData?.serviceNumber?.let {
                            bluetoothPrintTemplate?.replace(
                                "SERVICE_NUMBER",
                                it
                            )
                        }

                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "PAID_AMOUNT",
                        ticketData?.partialPaymentDetails?.paidAmount.toString()
                    ) ?: "-"
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "REMAINING_AMOUNT",
                        ticketData?.partialPaymentDetails?.remainingAmount.toString()
                    ) ?: "-"

                    getLandmarkPrint()
                    getOperatorNamePrint()
                    getWebAddressPrint()

                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "ACCOUNT_HOLDER_NAME",
                        loginModelPref.userName
                    )

                    bluetoothPrintTemplate =
                        ticketData?.ticketStatus?.let {
                            bluetoothPrintTemplate?.replace(
                                "TICKET_STATUS",
                                it
                            )
                        } ?: "-"
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace("ORIGIN", ticketData?.origin ?: "")
                    bluetoothPrintTemplate =
                        ticketData?.destination?.let {
                            bluetoothPrintTemplate?.replace(
                                "DESTINATION",
                                it
                            )
                        }
                    bluetoothPrintTemplate = ticketData?.boardingDetails?.depTime?.let {
                        bluetoothPrintTemplate?.replace(
                            "DEPARTURE_TIME",
                            it
                        )
                    }
                    bluetoothPrintTemplate =
                        ticketData?.travelDate?.let {
                            bluetoothPrintTemplate?.replace(
                                "TRAVEL_DATE",
                                "$it"
                            )
                        }
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "TICKET_FARE",
                        "${privilegeResponse?.currency ?: ""} ${
                            ticketData?.totalFare?.toString()?.toDouble()
                                ?.convert(privilegeResponse?.currencyFormat) ?: ""
                        }"
                    )
                    bluetoothPrintTemplate = ticketData?.passengerDetails?.get(i)?.name?.let {
                        bluetoothPrintTemplate?.replace(
                            "PASSENGER_NAME",
                            it
                        )
                    } ?: ""
                    bluetoothPrintTemplate = ticketData?.passengerDetails?.get(i)
                        ?.let {
                            it.mobile?.let { it1 ->
                                bluetoothPrintTemplate?.replace(
                                    "MOBILE_NUMBER",
                                    it1
                                )
                            }
                        } ?: ""
                    bluetoothPrintTemplate =
                        ticketData?.seatNumbers?.let {
                            bluetoothPrintTemplate?.replace(
                                "SEAT_NUMBERS",
                                it
                            )
                        }


                    bluetoothPrintTemplate = ticketData?.boardingDetails?.address?.let {
                        bluetoothPrintTemplate?.replace(
                            "BOARDING_POINT",
                            it
                        )
                    }


                    if (ticketData?.passengerDetails?.isNotEmpty()==true && !ticketData?.passengerDetails?.get(i)?.meal_coupon_qr.isNullOrEmpty()) {
                        bluetoothPrintTemplate =
                            ticketData?.passengerDetails?.get(i)?.meal_coupon_qr?.let { it ->
                                if (it.isNotEmpty()) {
                                    bluetoothPrintTemplate?.replace(
                                        "meal_coupon_qr", "[C]<qrcode size='20'>$it</qrcode>"
                                    )
                                }else{
                                    bluetoothPrintTemplate?.replace(
                                        "meal_coupon_qr", ""
                                    )
                                }


                            }
                    }else{
                        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                            "meal_coupon_qr", ""
                        )
                    }

                    if (ticketData?.dropOffDetails != null) {
                        bluetoothPrintTemplate = ticketData?.dropOffDetails?.address?.let {
                            bluetoothPrintTemplate?.replace(
                                "DROPPING_POINT",
                                it
                            )
                        }
                    }
                    bluetoothPrintTemplate = ticketData?.boardingDetails?.contactPersons?.let {
                        bluetoothPrintTemplate?.replace(
                            "CONTACT_PERSON",
                            it
                        )
                    }
                    getContactNumberPrint()
                    getTicketBookedByPrint()

                    bluetoothPrintTemplate =
                        ticketData?.busType?.let {
                            bluetoothPrintTemplate?.replace(
                                "COACH_TYPE",
                                it
                            )
                        }
                    bluetoothPrintTemplate =
                        bluetoothPrintTemplate?.replace("REMARKS", ticketData?.remarks ?: "-")
                    bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                        "TERMINAL_ID",
                        ticketData?.terminalRefNo ?: ""
                    )
                    if (ticketData?.terminalRefNo.isNullOrEmpty()) {
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

                bluetoothPrintTemplate =
                    ticketData?.ticketNumber?.let {
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

    private fun singleSeatBluetoothPrint() {
        bluetoothPrintTemplate =
            ticketData?.serviceNumber?.let {
                bluetoothPrintTemplate?.replace(
                    "SERVICE_NUMBER",
                    it
                )
            }

        if (ticketData?.partialPaymentDetails?.paidAmount != null) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PAID_AMOUNT",
                ticketData?.partialPaymentDetails?.paidAmount.toString().toDouble()
                    .convert(currencyFormat = privilegeResponse?.currencyFormat)
            ) ?: "-"
        } else {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PAID_AMOUNT", "-"
            )
        }

        if (ticketData?.partialPaymentDetails?.remainingAmount != null) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "REMAINING_AMOUNT",
                ticketData?.partialPaymentDetails?.remainingAmount.toString().toDouble()
                    .convert(currencyFormat = privilegeResponse?.currencyFormat)
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
            ticketData?.ticketNumber?.let {
                bluetoothPrintTemplate?.replace(
                    "PNR_NUMBER",
                    it
                )
            }
        bluetoothPrintTemplate =
            ticketData?.ticketStatus?.let {
                bluetoothPrintTemplate?.replace(
                    "TICKET_STATUS",
                    it
                )
            } ?: "-"
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("ORIGIN", ticketData?.origin ?: "")
        bluetoothPrintTemplate =
            ticketData?.destination?.let {
                bluetoothPrintTemplate?.replace(
                    "DESTINATION",
                    it
                )
            }
        bluetoothPrintTemplate = ticketData?.boardingDetails?.depTime?.let {
            bluetoothPrintTemplate?.replace(
                "DEPARTURE_TIME",
                it
            )
        }
        bluetoothPrintTemplate =
            ticketData?.travelDate?.let {
                bluetoothPrintTemplate?.replace(
                    "TRAVEL_DATE",
                    it
                )
            }

        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "TICKET_EACH_FARE",
            "${privilegeResponse?.currency ?: ""} ${
                ticketData?.totalFare?.toString()?.toDouble()
                    ?.convert(privilegeResponse?.currencyFormat) ?: ""
            }"
        )

        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "TICKET_FARE",
            "${privilegeResponse?.currency ?: ""} ${
                ticketData?.totalFare?.toString()?.toDouble()
                    ?.convert(privilegeResponse?.currencyFormat) ?: ""
            }"
        )

        bluetoothPrintTemplate = ticketData?.passengerDetails?.get(0)?.name?.let {
            bluetoothPrintTemplate?.replace(
                "PASSENGER_NAME",
                it
            )
        } ?: ""
        bluetoothPrintTemplate = ticketData?.passengerDetails?.get(0)
            ?.let {
                it.mobile?.let { it1 ->
                    bluetoothPrintTemplate?.replace(
                        "MOBILE_NUMBER",
                        it1
                    )
                }
            } ?: ""

        if (bluetoothPrintTemplate?.contains("SEAT_EACH_NUMBERS") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "SEAT_EACH_NUMBERS",
                ticketData?.seatNumbers ?: ""
            )
        }

        bluetoothPrintTemplate =
            ticketData?.seatNumbers?.let {
                bluetoothPrintTemplate?.replace(
                    "SEAT_NUMBERS",
                    it
                )
            }

        bluetoothPrintTemplate = ticketData?.boardingDetails?.address?.let {
            bluetoothPrintTemplate?.replace(
                "BOARDING_POINT",
                it
            )
        }
          if (ticketData?.passengerDetails?.isNotEmpty()==true && !ticketData?.passengerDetails?.get(0)?.meal_coupon_qr.isNullOrEmpty()) {
            bluetoothPrintTemplate =
                ticketData?.passengerDetails?.get(0)?.meal_coupon_qr?.let { it ->
                    if (it.isNotEmpty()) {
                        bluetoothPrintTemplate?.replace(
                            "meal_coupon_qr", "[C]<qrcode size='20'>$it</qrcode>"
                        )
                    }else{
                        bluetoothPrintTemplate?.replace(
                            "meal_coupon_qr", ""
                        )
                    }
                }

        }else{
              bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                  "meal_coupon_qr", ""
              )
        }

        if (ticketData?.dropOffDetails != null) {
            bluetoothPrintTemplate = ticketData?.dropOffDetails?.address?.let {
                bluetoothPrintTemplate?.replace(
                    "DROPPING_POINT",
                    it
                )
            }
        }
        bluetoothPrintTemplate = ticketData?.boardingDetails?.contactPersons?.let {
            bluetoothPrintTemplate?.replace(
                "CONTACT_PERSON",
                it
            )
        }
        getContactNumberPrint()
        getTicketBookedByPrint()
        bluetoothPrintTemplate =
            ticketData?.busType?.let { bluetoothPrintTemplate?.replace("COACH_TYPE", it) }
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("REMARKS", ticketData?.remarks ?: "-")
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("TERMINAL_ID", ticketData?.terminalRefNo ?: "")
        if (ticketData?.terminalRefNo.isNullOrEmpty()) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace("TERMINAL_PULOGABANG", "")
        }
        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("CURRENT_DATE", getTodayDate())

        bluetoothPrintTemplate =
            bluetoothPrintTemplate?.replace("CURRENT_TIME", getTodayDateWithTime())


        if (bluetoothPrintTemplate?.contains("PASSENGER_EACH_NAME") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "PASSENGER_EACH_NAME",
                ticketData?.passengerDetails!![0]?.name ?: ""
            )
        }

        if (bluetoothPrintTemplate?.contains("SEAT_EACH_NUMBER") == true) {
            bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
                "SEAT_EACH_NUMBER",
                ticketData?.passengerDetails!![0]?.seatNumber ?: ""
            )
        }

        if (bluetoothPrintTemplate?.contains("MEAL_COUPON_NUMBER") == true) {
            bluetoothPrintTemplate =
                if (ticketData?.passengerDetails != null && ticketData?.passengerDetails!!.isNotEmpty()) {
                    var mealCoupons = ""
                    ticketData?.passengerDetails?.forEach {
                        mealCoupons += it?.mealCoupons.toString()
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

        if (bluetoothPrintTemplate?.contains("MEAL_COUNT") == true && !ticketData?.passengerDetails.isNullOrEmpty()) {
            var mealCouponCount = 0
            ticketData?.passengerDetails?.forEach {
                mealCouponCount += it?.mealCoupons?.size!!
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
                if (ticketData?.passengerDetails != null && ticketData?.passengerDetails!!.isNotEmpty() && ticketData?.passengerDetails!!.any { it?.selectedMealType != "-" }) {
                    var mealTypes = ""
                    ticketData?.passengerDetails?.forEach {
                        mealTypes += it?.selectedMealType.toString()
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
        if (bluetoothPrintTemplate?.contains("ALL_INSURANCE_NUMBERS") == true && ticketData?.insuranceTransDetails?.details != null) {
            var allInsuranceNumbers = ""
            ticketData?.insuranceTransDetails?.details?.forEach {
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

    private fun getLandmarkPrint() {
        bluetoothPrintTemplate = ticketData?.boardingDetails?.landmark?.let {
            bluetoothPrintTemplate?.replace(
                "LANDMARK",
                it
            )
        } ?: "-"
    }

    private fun getOperatorNamePrint() {
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "OPERATOR_NAME",
            privilegeResponse?.operatorName ?: ""
        )
    }

    private fun getWebAddressPrint() {
        bluetoothPrintTemplate = bluetoothPrintTemplate?.replace(
            "WEB_ADDRESS",
            privilegeResponse?.webAddressUrl ?: ""
        )
    }

    private fun getContactNumberPrint() {
        bluetoothPrintTemplate = ticketData?.boardingDetails?.contactNumbers?.let {
            bluetoothPrintTemplate?.replace(
                "CONTACT_NUMBER_PERSON",
                it
            )
        }
    }

    private fun getTicketBookedByPrint() {
        bluetoothPrintTemplate = ticketData?.ticketBookedBy?.let {
            bluetoothPrintTemplate?.replace(
                "TICKET_BOOKED_BY",
                it.substringBefore(",")
            )
        }
    }

    private fun sortByForIDNAgent() {

        binding.tvPickupStage.text = "Pickup bb"
        binding.imgPickupStage.setImageResource(R.drawable.current_location)
        binding.tvPickupStage.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
        binding.imgPickupStage.imageTintList =
            ColorStateList.valueOf((requireContext().resources.getColor(R.color.colorButton)))


        requireContext().toast("asd")
        binding.pickup1.setOnClickListener(this)
        binding.imgPickupStage.setOnClickListener {

            requireContext().toast("asd1")
            binding.tvPickupStage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
            binding.imgPickupStage.imageTintList =
                ColorStateList.valueOf((requireContext().resources.getColor(R.color.colorButton)))

            binding.tvDropOffStage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.imgDropOffStage.imageTintList =
                ColorStateList.valueOf((requireContext().resources.getColor(R.color.white)))


            changeChart = true
            chartType = "1"
            cityselected = false
            startShimmerEffect()
            binding.NoResult.gone()
            pickUpChartApi(chartType)
            binding.cityHub.text = getString(R.string.show_all)

        }

        binding.tvDropOffStage.text = "Drop Off"
        binding.imgDropOffStage.setImageResource(R.drawable.ic_location)


        binding.dropOff.setOnClickListener {

            requireContext().toast("asd2")
            binding.tvDropOffStage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
            binding.imgDropOffStage.imageTintList =
                ColorStateList.valueOf((requireContext().resources.getColor(R.color.colorButton)))

            binding.tvPickupStage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.imgPickupStage.imageTintList =
                ColorStateList.valueOf((requireContext().resources.getColor(R.color.white)))

            changeChart = true
            chartType = "5"
            cityselected = false
            startShimmerEffect()
            binding.NoResult.gone()
            pickUpChartApi(chartType)
            binding.cityHub.text = getString(R.string.show_all)
        }
    }

    private fun printTicketDetailsDialog() {

        if(operatorLogo != null){
            CoroutineScope(Dispatchers.Main).launch {
                busLogo = getBitmapDirectFromUrl(operatorLogo!!)
            }

        }
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

            if (!privilegeResponse?.tsPrivileges?.qoalaImageV1.isNullOrEmpty()) {
                getBitmapFromURL(
                    privilegeResponse?.tsPrivileges?.qoalaImageV1 ?: "",
                    getString(R.string.insurance_bitmap)
                )
            }

            if (IS_PINELAB_DEVICE) {
                if (bluetoothPrintTemplate!!.contains("FOR_EACH_SEAT")) {
                    multiplePrintPinelab()
                } else {
                    pineLabPrint()
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


        } else requireContext().toast(getString(R.string.template_not_configured))
    }

    private fun callUpdatePrintCountApi() {
        lifecycleScope.launch {
            if (requireContext().isNetworkAvailable()) {
                ticketDetailsComposeViewModel.updatePrintCountApi(
                    pnrTicketNumber ?: "",
                    true,
                    loginModelPref.api_key
                )
                delay(1500L)
            } else {
                requireContext().noNetworkToast()
            }
        }
    }

    private fun getBitmapFromURL(image: String, imageType: String) {
        try {
            val urlImage = URL(image)
            val result: Deferred<Bitmap?> = GlobalScope.async {
                urlImage.toBitmap()
            }

            GlobalScope.launch(Dispatchers.Main) {
                if (imageType == getString(R.string.logo)) bmpLogo = result.await()
                if (imageType == getString(R.string.qr_code)) bmpQrCode = result.await()
                if (imageType == getString(R.string.insurance_bitmap)) insuranceBitmap = result.await()
            }
        }catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }


    }


   /* private fun getBitmapDirectFromUrl(image: String): Bitmap? {
        var image1: Bitmap? = null
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val url = URL(image)
                image1 = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
        return image1
    }*/

    suspend fun getBitmapDirectFromUrl(image: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(image)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    BitmapFactory.decodeStream(connection.inputStream)
                } else {
                    // Log or handle HTTP error codes
                    null
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                null
            }
        }
    }

    private fun multiplePrintPinelab() {
        var template = bluetoothPrintTemplate!!

        try {
            if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel.passengerDetails?.isNotEmpty()!!) {
                printArray = JSONArray()

                val multiSeats = mutableListOf<String>()
                Timber.e("Passenger size : ${ticketDetailsComposeViewModel.passengerDetails.size}")
                for (i in 0..ticketDetailsComposeViewModel.passengerDetails.size?.minus(1)!!) {
                    if (ticketDetailsComposeViewModel.passengerDetails.size != 1) {
                        if (i < ticketDetailsComposeViewModel.passengerDetails.size?.minus(1)!!) {
                            template = template.replace("[C]=", "")
                            template = template.replace("cut here", "")?.trimEnd()!!
                            template = template.replace("BOARDING_QR", "")?.trim()!!
                            template = template.replace("BAR_CODE", "")?.trim()!!

                        } else {
                            template = template.replace("[C]=", "")
                            template = template.replace("=", "")?.trimEnd()!!

                            if (originalTemplate?.contains("BOARDING_QR")!!) template =
                                "${template}\nBOARDING_QR"
                            if (originalTemplate?.contains("BAR_CODE")!!) template =
                                "${template}\n\nBAR_CODE"
                            template = "${template}\nALIGN_CENTER|=======cut here======="
                        }
                    }

                    template = template.replace("FOR_EACH_SEAT", "")
                    if (template.contains("SEAT_EACH_NUMBERS")) {
                        template = template.replace(
                            "SEAT_EACH_NUMBERS",
                            ticketDetailsComposeViewModel.passengerDetails[i]?.seatNumber ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.seatNumber?.let {
                                    template.replace(
                                        it,
                                        ticketDetailsComposeViewModel.passengerDetails[i]?.seatNumber
                                            ?: ""
                                    )
                                }!!
                        }
                    }
                    if (template.contains("PASSENGER_EACH_NAME")) {
                        template = template.replace(
                            "PASSENGER_EACH_NAME",
                            ticketDetailsComposeViewModel.passengerDetails[i]?.name ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.name?.let {
                                    template.replace(
                                        it,
                                        ticketDetailsComposeViewModel.passengerDetails[i]?.name
                                            ?: ""
                                    )
                                }!!
                        }
                    }


                    if (template.contains("TICKET_EACH_FARE")) {
                        template = template.replace(
                            "TICKET_EACH_FARE",
                            ticketDetailsComposeViewModel.passengerDetails[i]?.netFare?.toDouble()
                                ?.convert(privilegeResponse?.currencyFormat) ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.netFare.let {
                                    template.replace(
                                        it.toString(),
                                        ticketDetailsComposeViewModel.passengerDetails[i]?.netFare?.convert(
                                            privilegeResponse?.currencyFormat
                                        ) ?: ""
                                    )
                                }
                        }
                    }


                    if (!ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons.isNullOrEmpty()) {
                        if (template.contains("MEAL_COUPON_LOOP")) {

                            template = template.replace(
                                "MEAL_COUPON_LOOP",
                                ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "").replace(",", "\n")
                                    .replace(" ", "")
                            )
                        } else {
                            if (i > 0) {
                                template =
                                    ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.mealCoupons?.toString()
                                        ?.let {
                                            template.replace(
                                                it,
                                                ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons.toString()
                                            )
                                        }!!
                            }
                        }

                        if (template.contains("MEAL_COUPON_NUMBER") && !ticketDetailsComposeViewModel?.passengerDetails!![i]?.mealCoupons.isNullOrEmpty()) {
                            template = template.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "")
                            )
                        } else {
//                            if (i > 0) {
//                                template = template.replace(
//                                    ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.mealCoupons.toString()
//                                        .replace("[", "").replace("]", ""),
//                                    ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons.toString()
//                                        .replace("[", "").replace("]", "")
//                                )
//                            }
                        }

                        if (template.contains("MEAL_COUNT")) {
                            template = template.replace(
                                "MEAL_COUNT",
                                ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons?.size.toString()
                            )
                        } else {
                            if (i > 0) {
                                template = template.replace(
                                    ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.mealCoupons?.size.toString(),
                                    ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons?.size.toString()
                                )
                            }
                        }
                    } else {
                        template = template.replace(
                            "MEAL_COUPON_LOOP", ""
                        )
                    }




                    template = ticketDetailsComposeViewModel.serviceNumber.let {
                        template.replace(
                            "SERVICE_NUMBER", it
                        )
                    }!!

                    template = template.replace(
                        "PAID_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    )
                    template = template.replace(
                        "REMAINING_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    )

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
                            "WEB_ADDRESS", privilegeResponse?.webAddressUrl ?: ""
                        )
                    }


                    if (template.contains("TICKET_FARE")) {
                        template = template.replace(
                            "TICKET_FARE", "${privilegeResponse?.currency} ${
                                ticketDetailsComposeViewModel.totalFare.toString()?.toDouble()
                                    ?.convert(privilegeResponse?.currencyFormat) ?: ""
                            }"
                        )
                    }



                    template = ticketDetailsComposeViewModel.boardingDetails.value.landmark?.let {
                        template.replace(
                            "LANDMARK", it
                        )
                    } ?: "-"

                    template = template.replace(
                        "OPERATOR_NAME", privilegeResponse?.operatorName ?: ""
                    )

                    template = template.replace(
                        "WEB_ADDRESS", privilegeResponse?.webAddressUrl ?: ""
                    )

                    template = template.replace(
                        "ACCOUNT_HOLDER_NAME", loginModelPref.userName
                    )
                    template = ticketData?.ticketNumber?.let {
                        template.replace(
                            "PNR_NUMBER", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.ticketStatus.let {
                        template.replace(
                            "TICKET_STATUS", it
                        )
                    } ?: "-"
                    template = template.replace("ORIGIN", ticketDetailsComposeViewModel.origin)
                    template = ticketDetailsComposeViewModel.destination.let {
                        template.replace(
                            "DESTINATION", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.boardingDetails.value.depTime?.let {
                        template.replace(
                            "DEPARTURE_TIME", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.travelDate.let {
                        template.replace(
                            "TRAVEL_DATE", "$it"
                        )
                    }!!
                    template = template.replace(
                        "TICKET_FARE", "${privilegeResponse?.currency} ${
                            ticketDetailsComposeViewModel.totalFare.toString()?.toDouble()
                                ?.convert(privilegeResponse?.currencyFormat) ?: ""
                        }"
                    )
                    template = ticketDetailsComposeViewModel.passengerDetails.get(i)?.name?.let {
                        template.replace(
                            "PASSENGER_NAME", it
                        )
                    } ?: ""
                    template = ticketDetailsComposeViewModel.passengerDetails.get(i)?.let {
                        it.mobile?.let { it1 ->
                            template.replace(
                                "MOBILE_NUMBER", it1
                            )
                        }
                    } ?: ""
                    template = ticketDetailsComposeViewModel.seatNumbers.let {
                        template.replace(
                            "SEAT_NUMBERS", it
                        )
                    }!!


                    template = ticketDetailsComposeViewModel.boardingDetails.value.address?.let {
                        template.replace(
                            "BOARDING_POINT", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.dropOffDetails.value.address?.let {
                        template.replace(
                            "DROPPING_POINT", it
                        )
                    }!!
                    template =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
                            template.replace(
                                "CONTACT_PERSON", it
                            )
                        }!!

                    template =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactNumbers?.let {
                            template.replace(
                                "CONTACT_NUMBER_PERSON", it
                            )
                        }!!

                    /*template = ticketDetailsComposeViewModel.ticketLeadDetail?.ticketBookedBy?.let {
                        template?.replace(
                            "TICKET_BOOKED_BY",
                            it.substringBefore(",")
                        )
                    }!!*/

                    template = ticketDetailsComposeViewModel.busType.let {
                        template.replace(
                            "COACH_TYPE", it
                        )
                    }!!
                    template =
                        template.replace("REMARKS", ticketDetailsComposeViewModel.remarks)
                    template = template.replace(
                        "TERMINAL_ID", ticketDetailsComposeViewModel.terminalRefNo
                    )
                    if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
                        template = template.replace("TERMINAL_PULOGABANG", "")
                    }
                    template = template.replace("CURRENT_DATE", getTodayDate())
                    template = template.replace("CURRENT_TIME", getTodayDateWithTime())

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
                detailObj.put("PrintRefNo", ticketData?.ticketNumber ?: "")
                detailObj.put("SavePrintData", true)
                detailObj.put("Data", printArray)

                val json = JSONObject()
                json.put("Header", headerObj)
                json.put("Detail", detailObj)

                val data = Bundle()
                data.putString(BILLING_REQUEST_TAG, json.toString())
                message.data = data
                try {
                    message.replyTo = Messenger(IncomingHandler(requireContext()))
                    mServerMessenger!!.send(message)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }


    }

    private fun pineLabPrint() {

        if (isBound!!) {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y_H_M)
            val currentDate = dateFormat.format(calendar.time)


            printArray = JSONArray()
            var template = bluetoothPrintTemplate

            if (template!!.contains("PNR_NUMBER")) {
                template = template.replace("PNR_NUMBER", ticketData?.ticketNumber ?: "")
            }
            if (template.contains("TRAVEL_DATE")) {
                template =
                    template.replace("TRAVEL_DATE", ticketDetailsComposeViewModel.travelDate)

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
                    "SEAT_EACH_NUMBERS", ticketDetailsComposeViewModel.seatNumbers
                )

            }
            if (template.contains("SEAT_NUMBERS")) {
                template =
                    template.replace("SEAT_NUMBERS", ticketDetailsComposeViewModel.seatNumbers)

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
                    "PASSENGER_NAME", ticketDetailsComposeViewModel.passengerDetails.get(0)?.name!!
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
                    ticketDetailsComposeViewModel.passengerDetails.get(0)?.mobile!!
                )
            }
            if (template.contains("TICKET_EACH_FARE")) {
                template = template.replace(
                    "TICKET_EACH_FARE", "${privilegeResponse?.currency} ${
                        ticketDetailsComposeViewModel.totalFare.toString()?.toDouble()
                            ?.convert(privilegeResponse?.currencyFormat) ?: ""
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
                    "SERVICE_NUMBER", ticketDetailsComposeViewModel.serviceNumber
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
                template = template.replace("ORIGIN", ticketDetailsComposeViewModel.origin)
            }
            if (template.contains("DESTINATION")) {
                template =
                    template.replace("DESTINATION", ticketDetailsComposeViewModel.destination)
            }
            if (template.contains("WEB_ADDRESS")) {
                template = template.replace(
                    "WEB_ADDRESS", privilegeResponse?.webAddressUrl ?: ""
                )!!
            }


            if (template.contains("TICKET_FARE")) {
                template = template.replace(
                    "TICKET_FARE", "${privilegeResponse?.currency} ${
                        ticketDetailsComposeViewModel.totalFare.toString()?.toDouble()
                            ?.convert(privilegeResponse?.currencyFormat) ?: ""
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
            detailObj.put("PrintRefNo", ticketData?.ticketNumber)
            detailObj.put("SavePrintData", true)
            detailObj.put("Data", printArray)


            val json = JSONObject()
            json.put("Header", headerObj)
            json.put("Detail", detailObj)


            val data = Bundle()
            data.putString(BILLING_REQUEST_TAG, json.toString())
            message.data = data
            try {
                message.replyTo = Messenger(
                    IncomingHandler(
                        requireContext()
                    )
                )
                mServerMessenger!!.send(message)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        } else {
            requireContext().toast("Pinelab device not found")
        }


    }

    private fun generateQrcode() {
        val manager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager

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

    private fun singleSeatPOSPrint() {
        generateQrcode()
        var isBold = false
        var template = ""

        template = bluetoothPrintTemplate!!


        if (template.contains("PNR_NUMBER")) {
            template = template.replace("PNR_NUMBER", ticketData?.ticketNumber ?: "")
        }
        if (template.contains("TRAVEL_DATE")) {
            template = template.replace("TRAVEL_DATE", ticketDetailsComposeViewModel.travelDate)
        }
        if (template.contains("SERVICE_NUMBER")) {
            template =
                template.replace("SERVICE_NUMBER", ticketDetailsComposeViewModel.serviceNumber)
        }
        if (template.contains("TAB_SPACE")) {
            template = template.replace("TAB_SPACE", " ")
        }
        if (template.contains("SEAT_EACH_NUMBERS")) {
            template =
                template.replace("SEAT_EACH_NUMBERS", ticketDetailsComposeViewModel.seatNumbers)
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
                "PASSENGER_NAME", ticketDetailsComposeViewModel.passengerDetails.get(0)?.name!!
            )
        }
        if (template.contains("MOBILE_NUMBER")) {
            template = template.replace(
                "MOBILE_NUMBER", ticketDetailsComposeViewModel.passengerDetails.get(0)?.mobile!!
            )
        }
        if (template.contains("TICKET_EACH_FARE")) {
            template = template.replace(
                "TICKET_EACH_FARE", "${privilegeResponse?.currency} ${
                    ticketDetailsComposeViewModel.totalFare.toString()?.toDouble()
                        ?.convert(privilegeResponse?.currencyFormat) ?: ""
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

        if (ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount != null) {
            template = template.replace(
                "PAID_AMOUNT",
                ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    .toDouble().convert(currencyFormat = privilegeResponse?.currencyFormat)
            )
        } else {
            template = template.replace("PAID_AMOUNT", "-")
        }

        if (ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount != null) {
            template = template.replace(
                "REMAINING_AMOUNT",
                ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount.toString()
                    .toDouble().convert(currencyFormat = privilegeResponse?.currencyFormat)
            )
        } else {
            template = template.replace(
                "REMAINING_AMOUNT", "-"
            )
        }

        template = ticketDetailsComposeViewModel.ticketStatus.let {
            template.replace(
                "TICKET_STATUS", it
            )
        } ?: "-"

        template = template.replace(
            "TICKET_FARE", "${privilegeResponse?.currency} ${
                ticketDetailsComposeViewModel.totalFare.toString()?.toDouble()
                    ?.convert(privilegeResponse?.currencyFormat) ?: ""
            }"
        )

        template = ticketDetailsComposeViewModel.seatNumbers.let {
            template.replace(
                "SEAT_NUMBERS", it
            )
        }!!

        template = ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
            template.replace(
                "CONTACT_PERSON", it
            )
        }!!

        template =
            ticketDetailsComposeViewModel.busType.let { template.replace("COACH_TYPE", it) }!!
        template = template.replace("REMARKS", ticketDetailsComposeViewModel.remarks)
        template =
            template.replace("TERMINAL_ID", ticketDetailsComposeViewModel.terminalRefNo)
        if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
            template = template.replace("TERMINAL_PULOGABANG", "")
        }
        template = template.replace("CURRENT_DATE", getTodayDate())

        template = template.replace("CURRENT_TIME", getTodayDateWithTime())



        if (template.contains("MEAL_COUPON_NUMBER")) {
            template =
                if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel.passengerDetails.isNotEmpty()) {
                    var mealCoupons = ""
                    ticketDetailsComposeViewModel.passengerDetails.forEach {
                        mealCoupons += it?.mealCoupons.toString().replace("[", "").replace("]", "")
                            .replace(",", "\n").replace(" ", "")
                        mealCoupons += "\n"
                    }
                    template.replace(
                        "MEAL_COUPON_NUMBER", mealCoupons
                    )
                } else {
                    template.replace(
                        "MEAL_COUPON_NUMBER", "-"
                    )
                }!!

        }

        if (template.contains("MEAL_COUNT") && !ticketDetailsComposeViewModel.passengerDetails.isNullOrEmpty()) {
            var mealCouponCount = 0
            ticketDetailsComposeViewModel.passengerDetails.forEach {
                mealCouponCount += it?.mealCoupons?.size!!
            }
            template = template.replace(
                "MEAL_COUNT", mealCouponCount.toString()
            )
        }

        if (template.contains("MEAL_COUPON_LOOP")) {
            template = template.replace(
                "MEAL_COUPON_LOOP", ""
            )
        }

        template = ticketDetailsComposeViewModel.boardingDetails.value.contactNumbers?.let {
            template.replace(
                "CONTACT_NUMBER_PERSON", it
            )
        }!!

        template = ticketDetailsComposeViewModel.boardingDetails.value.landmark?.let {
            template.replace(
                "LANDMARK", it
            )
        } ?: "-"

        template = template.replace(
            "WEB_ADDRESS", privilegeResponse?.webAddressUrl ?: ""
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
                            SunmiPrintHelper.getInstance().printText("\n", 24f, true, false, "")
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
            if (ticketDetailsComposeViewModel.passengerDetails != null && ticketDetailsComposeViewModel.passengerDetails?.isNotEmpty()!!) {

                val multiSeats = mutableListOf<String>()
                for (i in 0..ticketDetailsComposeViewModel.passengerDetails.size?.minus(1)!!) {
                    if (ticketDetailsComposeViewModel.passengerDetails.size != 1) {
                        if (i < ticketDetailsComposeViewModel.passengerDetails.size?.minus(1)!!) {
                            template = template.replace("[C]=", "")
                            template = template.replace("cut here", "")?.trimEnd()!!
                            template = template.replace("BOARDING_QR", "")?.trim()!!
                            template = template.replace("BAR_CODE", "")?.trim()!!

                        } else {
                            template = template.replace("[C]=", "")
                            template = template.replace("=", "")?.trimEnd()!!

                            if (originalTemplate?.contains("BOARDING_QR")!!) template =
                                "${template}\nBOARDING_QR"
                            if (originalTemplate?.contains("BAR_CODE")!!) template =
                                "${template}\n\nBAR_CODE"
                            template = "${template}\nALIGN_CENTER|=======cut here======="
                        }
                    }

                    template = template.replace("FOR_EACH_SEAT", "")
                    if (template.contains("SEAT_EACH_NUMBERS")) {
                        template = template.replace(
                            "SEAT_EACH_NUMBERS",
                            ticketDetailsComposeViewModel.passengerDetails[i]?.seatNumber ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.seatNumber?.let {
                                    template.replace(
                                        it,
                                        ticketDetailsComposeViewModel.passengerDetails[i]?.seatNumber
                                            ?: ""
                                    )
                                }!!
                        }
                    }
                    if (template.contains("PASSENGER_EACH_NAME")) {
                        template = template.replace(
                            "PASSENGER_EACH_NAME",
                            ticketDetailsComposeViewModel.passengerDetails[i]?.name ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.name?.let {
                                    template.replace(
                                        it,
                                        ticketDetailsComposeViewModel.passengerDetails[i]?.name
                                            ?: ""
                                    )
                                }!!
                        }
                    }


                    if (template.contains("TICKET_EACH_FARE")) {
                        template = template.replace(
                            "TICKET_EACH_FARE",
                            ticketDetailsComposeViewModel.passengerDetails[i]?.netFare?.toDouble()
                                ?.convert(privilegeResponse?.currencyFormat) ?: ""
                        )
                    } else {
                        if (i > 0) {
                            template =
                                ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.netFare.toString()
                                    .let {
                                        template.replace(
                                            it,
                                            ticketDetailsComposeViewModel.passengerDetails[i]?.netFare?.convert(
                                                privilegeResponse?.currencyFormat ?: ""
                                            ) ?: ""
                                        )
                                    }
                        }
                    }


                    if (!ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons.isNullOrEmpty()) {
                        if (template.contains("MEAL_COUPON_LOOP")) {

                            template = template.replace(
                                "MEAL_COUPON_LOOP",
                                ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "").replace(",", "\n")
                                    .replace(" ", "")
                            )
                        } else {
                            if (i > 0) {
                                template =
                                    ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.mealCoupons?.toString()
                                        ?.let {
                                            template.replace(
                                                it,
                                                ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons.toString()
                                            )
                                        }!!
                            }
                        }

                        if (template.contains("MEAL_COUPON_NUMBER")) {
                            template = template.replace(
                                "MEAL_COUPON_NUMBER",
                                ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons.toString()
                                    .replace("[", "").replace("]", "")
                            )
                        } else {
                            if (i > 0) {
                                template = template.replace(
                                    ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.mealCoupons.toString()
                                        .replace("[", "").replace("]", ""),
                                    ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons.toString()
                                        .replace("[", "").replace("]", "")
                                )
                            }
                        }

                        if (template.contains("MEAL_COUNT")) {
                            template = template.replace(
                                "MEAL_COUNT",
                                ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons?.size.toString()
                            )
                        } else {
                            if (i > 0) {
                                template = template.replace(
                                    ticketDetailsComposeViewModel.passengerDetails[i.minus(1)]?.mealCoupons?.size.toString(),
                                    ticketDetailsComposeViewModel.passengerDetails[i]?.mealCoupons?.size.toString()
                                )
                            }
                        }
                    } else {
                        template = template.replace(
                            "MEAL_COUPON_LOOP", ""
                        )
                    }




                    template = ticketDetailsComposeViewModel.serviceNumber.let {
                        template.replace(
                            "SERVICE_NUMBER", it
                        )
                    }!!

                    template = template.replace(
                        "PAID_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.paidAmount.toString()
                    )
                    template = template.replace(
                        "REMAINING_AMOUNT",
                        ticketDetailsComposeViewModel.partialPaymentDetails.value.remainingAmount.toString()
                    )


                    template = ticketDetailsComposeViewModel.boardingDetails.value.landmark?.let {
                        template.replace(
                            "LANDMARK", it
                        )
                    } ?: "-"

                    template = template.replace(
                        "OPERATOR_NAME", privilegeResponse?.operatorName ?: ""
                    )

                    template = template.replace(
                        "WEB_ADDRESS", privilegeResponse?.webAddressUrl ?: ""
                    )

                    template = template.replace(
                        "ACCOUNT_HOLDER_NAME", loginModelPref.userName
                    )
                    template = ticketData?.ticketNumber?.let {
                        template.replace(
                            "PNR_NUMBER", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.ticketStatus.let {
                        template.replace(
                            "TICKET_STATUS", it
                        )
                    } ?: "-"
                    template = template.replace("ORIGIN", ticketDetailsComposeViewModel.origin)
                    template = ticketDetailsComposeViewModel.destination.let {
                        template.replace(
                            "DESTINATION", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.boardingDetails.value.depTime?.let {
                        template.replace(
                            "DEPARTURE_TIME", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.travelDate.let {
                        template.replace(
                            "TRAVEL_DATE", "$it"
                        )
                    }!!
                    template = template.replace(
                        "TICKET_FARE", "${privilegeResponse?.currency} ${
                            ticketDetailsComposeViewModel.totalFare.toString()?.toDouble()
                                ?.convert(privilegeResponse?.currencyFormat) ?: ""
                        }"
                    )
                    template = ticketDetailsComposeViewModel.passengerDetails.get(i)?.name?.let {
                        template.replace(
                            "PASSENGER_NAME", it
                        )
                    } ?: ""
                    template = ticketDetailsComposeViewModel.passengerDetails.get(i)?.let {
                        it.mobile?.let { it1 ->
                            template.replace(
                                "MOBILE_NUMBER", it1
                            )
                        }
                    } ?: ""
                    template = ticketDetailsComposeViewModel.seatNumbers.let {
                        template.replace(
                            "SEAT_NUMBERS", it
                        )
                    }!!


                    template = ticketDetailsComposeViewModel.boardingDetails.value.address?.let {
                        template.replace(
                            "BOARDING_POINT", it
                        )
                    }!!
                    template = ticketDetailsComposeViewModel.dropOffDetails.value.address?.let {
                        template.replace(
                            "DROPPING_POINT", it
                        )
                    }!!
                    template =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactPersons?.let {
                            template.replace(
                                "CONTACT_PERSON", it
                            )
                        }!!

                    template =
                        ticketDetailsComposeViewModel.boardingDetails.value.contactNumbers?.let {
                            template.replace(
                                "CONTACT_NUMBER_PERSON", it
                            )
                        }!!

                    template = ticketDetailsComposeViewModel.ticketBookedBy.let {
                        template?.replace(
                            "TICKET_BOOKED_BY",
                            it.substringBefore(",")
                        )
                    }!!

                    template = ticketDetailsComposeViewModel.busType.let {
                        template.replace(
                            "COACH_TYPE", it
                        )
                    }!!
                    template =
                        template.replace("REMARKS", ticketDetailsComposeViewModel.remarks)
                    template = template.replace(
                        "TERMINAL_ID", ticketDetailsComposeViewModel.terminalRefNo
                    )
                    if (ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
                        template = template.replace("TERMINAL_PULOGABANG", "")
                    }
                    template = template.replace("CURRENT_DATE", getTodayDate())
                    template = template.replace("CURRENT_TIME", getTodayDateWithTime())

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
                    if (i == ticketDetailsComposeViewModel.passengerDetails.size?.minus(1)) {
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

    private class IncomingHandler(pickupPassengerList: Context) : Handler() {
        private val BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE"
        val context = pickupPassengerList
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

}