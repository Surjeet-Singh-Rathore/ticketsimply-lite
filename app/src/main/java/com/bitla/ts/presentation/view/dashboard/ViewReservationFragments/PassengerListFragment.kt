package com.bitla.ts.presentation.view.dashboard.ViewReservationFragments

import android.Manifest
import android.annotation.*
import android.app.*
import android.bluetooth.*
import android.content.*
import android.content.pm.*
import android.net.Uri
import android.os.*
import android.text.*
import android.view.*
import android.view.animation.*
import android.widget.*
import android.widget.SearchView
import androidx.annotation.*
import androidx.appcompat.widget.*
import androidx.collection.*
import androidx.constraintlayout.widget.*
import androidx.core.app.*
import androidx.core.content.*
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.custom_stage_summary.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.sendOtpAndQrCode.request.*
import com.bitla.ts.domain.pojo.update_boarded_status.request.*
import com.bitla.ts.domain.pojo.view_reservation.*
import com.bitla.ts.domain.pojo.view_reservation.request.*
import com.bitla.ts.domain.pojo.view_reservation.request.ReqBody
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.adapter.SortByAdaper.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.bluetooth_print.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.constants.PrintOptions.PRINT_OPTION_CLICKS_VIEW_RESERVATION
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.dantsu.escposprinter.connection.*
import com.dantsu.escposprinter.connection.bluetooth.*
import com.google.android.material.bottomsheet.*
import com.google.zxing.integration.android.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*

class PassengerListFragment : BaseUpdateCancelTicket(), OnItemClickListener,
    DialogSingleButtonListener, DialogButtonListener, OnItemPassData, View.OnClickListener,
    OnclickitemMultiView, DialogButtonMultipleView,
    OnItemCheckedMultipledataListner,OnPnrListener {

    private var isAgentRole: Boolean = false
    private var restrictSkipVerification = false
    private var allowBookingForAllotedServices = false
    private lateinit var childStatusOption: LinearLayout
    private lateinit var boardedLayout: LinearLayout
    private var lastPassengerPosition: Int? = null
    private var isModifyOptionClicked: Boolean = false
    private var isViewTicketClicked: Boolean = false
    private var viewTicketPNRNumber = ""
    private var currency: String = ""
    private var currencyFormat: String = ""
//    private var privilegeResponse: PrivilegeResponseModel? = null
    private var operatorName: String = ""
    private var serviceName: String? = null
    private var travelDate: String? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var stageAdapter: StageAdapter
    private lateinit var childSortSublistAdapter: ChildSortSublistAdapter
    private lateinit var chartAdapter: ChartListAdapter

    private lateinit var cancelTicketSheet: CancelTicketSheet
    private lateinit var editPassengerSheet: EditPassengerSheet


    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    lateinit var binding: FragmentPassengerListBinding
    lateinit var bindingSheet: SheetBoardedCheckBinding
    lateinit var bottomSheetDialog: BottomSheetDialog
    private var chartTypeList: ArrayList<ChartType> = arrayListOf()
    private var chartTypeListsize: Int = 0
    private var chartType: String = "1"
    private var passengerlist: ArrayList<PassengerDetail> = arrayListOf()
    private var passengerStatus: String? = null
    private var pnrNum: String? = null
    private var seatNum: String? = null
    private var resID: String = ""
    private var respHashCityId: ArrayList<Int> = arrayListOf()
    private val citySqeName: ArrayList<String> = arrayListOf()
    private var citySqeDetail: ArrayList<CitySeqOrder> = arrayListOf()
    private var citySqeId: ArrayList<Int> = arrayListOf()
    private var respHashList: ArrayList<RespHash> = arrayListOf()
    private var passengerlist2: ArrayList<PassengerDetail> = arrayListOf()
    private var respHashList2: ArrayList<RespHash> = arrayListOf()
    private var cityselected: Boolean = false
    private var selected: Int? = null
    private var passengerName: String = ""
    private var newOtp: String = ""
    private var skipQrCcode: Boolean = false
    private var passGender = ""
    private var passAge = ""
    private var passStatus = ""
    private var scanTag = ""
    private var listSeatno = arrayListOf<String>()
    private var listSassName = arrayListOf<String>()
    private var qrresponse = ""
    private var qrSelectedResId = ""
    private var qrSelectedPnrNumber = ""
    private var flagTemp = false
    private var resetcount = 0
    private var templist = listOf<String>()
    private var tempMultiplelist = arrayListOf<String>()
    private var tempMapList = arrayMapOf<String, String>()
    private lateinit var boardedSwitch: SwitchCompat
    private lateinit var boardedStatusText: TextView

    private var selectedDevice: BluetoothConnection? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var locale: String? = ""
    private lateinit var fragmentDataListener: FragmentDataListener
    private lateinit var rotate: RotateAnimation

    //boarding status iteration updation
    private var isAllowStatus: Boolean? = false
    private var isAllowToChangeStatusOnlyOnce: Boolean = false
    private var role = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentDataListener = activity as FragmentDataListener
        } catch (e: ClassCastException) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentPassengerListBinding.inflate(layoutInflater)
        getPref()

        firebaseLogEvent(
            context = requireContext(),
            logEventName = PASSENGER_LIST,
            loginId = loginModelPref.userName,
            operatorName = loginModelPref.travels_name,
            roleName = loginModelPref.role,
            eventKey = PASSENGER_LIST,
            eventValue = PassengerList.PASSENGER_LIST
        )

        cancelTicketSheet =  childFragmentManager.findFragmentById(R.id.layoutCancelTicketSheet) as CancelTicketSheet
        editPassengerSheet =  childFragmentManager.findFragmentById(R.id.layoutEditPassengerSheet) as EditPassengerSheet


//        requireContext().toast("$resID")
        startShimmerEffect()
//        setMenuVisibility(true)
        binding.cityHub.setOnClickListener(this)
        PreferenceUtils.putString("BulkShiftBack", "")
        if (requireContext().isNetworkAvailable()) {
            pickUpChartApi(chartType)
            updateBoardedStatusObserver()
            viewReservationObserver()
            cityPickupByStageObserver()
            setTicketDetailsObserver()

        } else
            requireContext().noNetworkToast()
        bindingSheet = SheetBoardedCheckBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        bottomSheetDialog.setContentView(bindingSheet.root)
        binding.refreshCard.setOnClickListener {
            rotate = RotateAnimation(
                /* fromDegrees = */ 0f,
                /* toDegrees = */ 180f,
                /* pivotXType = */ Animation.RELATIVE_TO_SELF,
                /* pivotXValue = */ 0.5f,
                /* pivotYType = */ Animation.RELATIVE_TO_SELF,
                /* pivotYValue = */ 0.5f
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

        binding.btnMapNavigation.gone()

        binding.btnSearchPnrMobileNum.visible()
        binding.btnSearchPnrMobileNum.setOnClickListener {
            if (binding.svSearchPnrMobileNum.isGone) {
                binding.svSearchPnrMobileNum.setQuery("", false)
                binding.svSearchPnrMobileNum.clearFocus()
                binding.svSearchPnrMobileNum.visible()
            } else {
                binding.svSearchPnrMobileNum.gone()
            }
        }

        binding.svSearchPnrMobileNum.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                val filteredRespHashList = if (respHashList.isNotEmpty()) {
                    filterRespHashBySearch(respHashList, newText)
                } else {
                    filterRespHashBySearch(respHashList2, newText)
                }

                val filteredPassengerList = if (passengerlist.isNotEmpty()) {
                    filterPassengerDetailsBySearch(passengerlist, newText)
                } else {
                    filterPassengerDetailsBySearch(passengerlist2, newText)
                }

                if (filteredRespHashList.isNotEmpty()) {
                    binding.NoResult.gone()
                    adapter(filteredRespHashList, filteredPassengerList, true, chartType)
                } else {
                    adapter(null, filteredPassengerList, false, chartType)
                    binding.NoResult.visible()
                }
                return true
            }
        })

//        pickUpChartApi(chartType)

        //activity.binding.updateRatecardToolbar.imageHeaderPrint

        pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
            privilegeResponse?.apply {
                val printView = (requireActivity() as ViewReservationActivity).findViewById<ImageView>(R.id.image_header_print)

                if (privilegeResponse?.pickupChartPrint != null && privilegeResponse?.pickupChartPrint!!)
                    printView.visible()
                else
                    printView.gone()

                printView.setOnClickListener {
                    firebaseLogEvent(
                        context = requireContext(),
                        logEventName = PRINT_OPTION,
                        loginId = loginModelPref.userName,
                        operatorName = loginModelPref.travels_name,
                        roleName = loginModelPref.role,
                        eventKey = PRINT_OPTION,
                        eventValue = PRINT_OPTION_CLICKS_VIEW_RESERVATION
                    )

                    if (passengerlist.isNotEmpty())
                        checkPermissions()
                    else
                        requireContext().toast(getString(R.string.no_data_available))
                }
                bottomSheetDialog.dismiss()
            }
        }
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
            pickUpChartViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    requireContext().showToast(it)
                }
            }
        }
    }

    @SuppressLint("NewApi")
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
                enableDeviceBluetooth()
    }

    private fun pickUpChartApi(chartTypeSelected: String) {
        if (requireContext().isNetworkAvailable()) {
            ViewReservationRequest(
                bccId = bccId.toString(),
                format = format_type,
                methodName = view_reservation_method_name,
                reqBody = ReqBody(
                    apiKey = loginModelPref.api_key,
                    chartType = chartTypeSelected,
                    isFromMiddleTier = true,
                    resId = resID.toString(),
                    locale = locale
                )
            )
            pickUpChartViewModel.viewReservationAPI(
                apiKey = loginModelPref.api_key,
                resId = resID.toString(),
                chartType = chartTypeSelected,
                locale = locale ?: "",
                apiType = view_reservation_method_name,
                newPickUpChart = null
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
                resID = PreferenceUtils.getString("reservationid") ?: ""
                
                pickUpChartApi(chartType)
            }
        } else {
            // fragment is no longer visible
        }
    }


    private fun viewReservationObserver() {

        pickUpChartViewModel.viewReservationResponse.observe(viewLifecycleOwner) {
            binding.refreshLayout.isRefreshing = false
            binding.passengerListSortby.visible()
            if (it != null) {
                    if (it.code == 200) {
                        fragmentDataListener.onUpdateFragment(true)
                        savePref(it)
                        passengerlist.clear()
                        chartTypeList.clear()


                        PreferenceUtils.putString("is_trip_complete", it.isTripComplete)




                        if (::rotate.isInitialized) {
                            //binding.refreshIcon.startAnimation(null)
                            if (rotate.hasStarted()) {
                                binding.refreshIcon.clearAnimation()

                                val date = Date()
                                val simpleDate = SimpleDateFormat("HH:mm")
                                binding.refreshTime.text = " (${simpleDate.format(date)})"
                            }
                        }

                        if (cityselected) {
                            if (chartType == "1") {
                                if (it.code == 200) {
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
                                    if (it.respHash != null) {
                                        passengerlist2.clear()
                                        respHashList2.clear()
                                        respHashList = it.respHash
                                        if (!respHashList.isNullOrEmpty()) {


                                            for (i in 0..respHashList.size.minus(1)) {
                                                if (respHashList[i].cityId == selected) {
                                                    respHashList2.add(respHashList[i])
                                                    for (j in 0..respHashList[i].passengerDetails.size.minus(
                                                        1
                                                    ))
                                                        passengerlist2.add(respHashList[i].passengerDetails[j])
                                                }
                                            }
                                            if (!respHashList2.isNullOrEmpty()) {

                                                binding.NoResult.gone()
                                                binding.cityHub.visible()
                                                stopShimmerEffect()
                                                binding.btnSearchPnrMobileNum.visible()
                                                binding.tvSortBy.visible()
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    binding.mainLayout.visible()

                                                    binding.passengerListSortby.visible()

                                                }, 200)
                                                var chartType = ""
                                                for (i in 0..it.chartType.size.minus(1)) {
                                                    if (it.chartType[i].isSelected) {
                                                        chartType = it.chartType[i].id.toString()
                                                    }
                                                }

                                                adapter(
                                                    respHashList2,
                                                    passengerlist2,
                                                    true,
                                                    chartType
                                                )

                                            } else {
                                                stopShimmerEffect()
                                                binding.svSearchPnrMobileNum.gone()
                                                binding.passengerListSortby.gone()
                                                binding.NoResult.visible()
                                            }

                                        } else {
                                            stopShimmerEffect()
                                            binding.svSearchPnrMobileNum.gone()
                                            binding.passengerListSortby.gone()
                                            binding.NoResult.visible()
                                        }

                                    } else {
                                        stopShimmerEffect()
                                        binding.cityHub.gone()
                                        binding.svSearchPnrMobileNum.gone()
                                        binding.mainLayout.gone()
                                    }
                                } else {
                                    stopShimmerEffect()
                                    binding.cityHub.gone()
                                    binding.svSearchPnrMobileNum.gone()
                                    binding.mainLayout.gone()
                                    binding.passengerListSortby.gone()
                                    binding.bottomMenu.gone()
                                    binding.NoResult.visible()
                                }
                            } else {
                                binding.cityHub.gone()
                            }

                            binding.btnFilterScan.setOnClickListener {
                                isModifyOptionClicked = false
                                listSeatno.clear()
                                listSassName.clear()
                                scanTag = "QuickScan"
                                scanScaeen()
                            }

                        } else {
                            if (it.code == 200) {

                                // After discussion with Dhaval Sir, the branch sort in Pickup chart is commented for now. Will be done in next iteration.

                                /* it.chartType.forEach { item ->
                                if (
                                    item.label.equals("stage", true)
                                    || item.label.equals("status", true)
                                    || item.label.equals("pnr number", true)
                                    || item.label.equals("seat number", true)
                                    || item.label.equals("destination", true)
                                ) {
                                    chartTypeList.add(item)
                                }

                            }

                            chartTypeListsize = chartTypeList.size*/

                                if (it.chartType != null) {
                                    chartTypeList.addAll(it.chartType)
                                    chartTypeListsize = it.chartType.size
                                }

                                try {
                                    if (it.bookingDetails != null &&
                                        (it.bookingDetails.boarded != null ||
                                                it.bookingDetails.total_booked != null ||
                                                it.bookingDetails.yet_to_board != null)
                                    ) {
                                        binding.countlayout.visible()
                                    }
                                } catch (e: Exception) {
                                    if (BuildConfig.DEBUG) {
                                        e.printStackTrace()
                                    }
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
                                if (it.bookingDetails.bookedPassengerCount == 0) {
                                    PreferenceUtils.setPreference("dataAvailable", false)
                                    binding.passengerListSortby.gone()
                                    binding.NoResult.visible()
                                    binding.tvSortBy.gone()
                                    binding.btnSearchPnrMobileNum.gone()
                                    binding.svSearchPnrMobileNum.gone()
                                    binding.cityHub.gone()
                                    chartListAdapter(chartTypeList)
                                } else {
                                    if (it.passengerDetails != null) {
                                        var respHashList = it.respHash
                                        passengerlist = it.passengerDetails
                                        adapter(respHashList, passengerlist, true, chartType)
                                        chartListAdapter(chartTypeList)
                                        binding.NoResult.gone()
                                        binding.tvSortBy.visible()
                                        binding.btnSearchPnrMobileNum.visible()
                                        binding.svSearchPnrMobileNum.gone()
                                    } else {
                                        if (!it.respHash.isNullOrEmpty()) {
                                            binding.NoResult.gone()
                                            binding.tvSortBy.visible()
                                            binding.btnSearchPnrMobileNum.visible()
                                            binding.svSearchPnrMobileNum.gone()
                                            PreferenceUtils.setPreference("dataAvailable", true)
                                            if (chartType == "1") {
                                                binding.cityHub.visible()
                                            } else {
                                                binding.cityHub.gone()
                                            }

                                            respHashList.clear()
                                            citySqeDetail.clear()
                                            respHashList = it.respHash
                                            for (i in 0..respHashList.size.minus(1)) {
                                                respHashCityId.add(respHashList[i].cityId)
                                                for (j in 0..respHashList[i].passengerDetails.size.minus(
                                                    1
                                                )) {
                                                    passengerlist.add(respHashList[i].passengerDetails[j])


                                                }
                                            }
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
                                            var chartType = ""
                                            for (i in 0..it.chartType.size.minus(1)) {
                                                if (it.chartType[i].isSelected) {
                                                    chartType = it.chartType[i].id.toString()
                                                }
                                            }

                                            for (a in 0..citySqeDetail.size.minus(1)) {
                                                if (a == 0) {
                                                    citySqeName.add(citySqeDetail[0].name)
                                                    citySqeId.add(citySqeDetail[a].id)

                                                } else {
                                                    citySqeId.add(citySqeDetail[a].id)
                                                    val stageTime = citySqeDetail[a].stageTime
                                                    val strs =
                                                        citySqeDetail[a].name.split("-")
                                                            .toTypedArray()
                                                    citySqeName.add("${stageTime} - ${strs[0]}")
                                                }
                                            }
                                            adapter(respHashList, passengerlist, true, chartType)
                                            chartListAdapter(chartTypeList)
                                        }
                                    }

                                }
                                stopShimmerEffect()

                            } else {
                                stopShimmerEffect()
                                binding.cityHub.gone()
                                binding.svSearchPnrMobileNum.gone()
                                binding.mainLayout.gone()
                                binding.passengerListSortby.gone()
                                binding.bottomMenu.gone()
                                binding.NoResult.visible()
                            }
                        }
                    } else {
                        fragmentDataListener.onUpdateFragment(false)
                        stopShimmerEffect()
                        binding.cityHub.gone()
                        binding.svSearchPnrMobileNum.gone()
                        binding.mainLayout.gone()
                        binding.passengerListSortby.gone()
                        binding.bottomMenu.gone()
                        binding.NoResult.visible()
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

    private fun savePref(it: ViewReservationResponseModel) {

            PreferenceUtils.apply {
                if (!it.citySeqOrder.isNullOrEmpty()) {
                    putCitySeqOrder(it.citySeqOrder)
                }

                setPreference(PREF_IS_APPLY_BPDP_FARE, it.isApplyBpDpFare)

                if (it.resId != null){
                    setPreference(PREF_RESERVATION_ID, it.resId.toDouble().toInt())
                }

                putObject(it.isApplyBpDpFare, IS_APPLY_BP_DP_FARE)

                if (!isAgentRole && allowBookingForAllotedServices == false) {
                    putString(PREF_SOURCE, it.originName)
                    putString(PREF_SOURCE_ID, it.originId.toString())
                    putString(PREF_DESTINATION, it.destinationName)
                    putString(PREF_DESTINATION_ID, it.destinationId.toString())
                }

                if (!it.travelDate.isNullOrEmpty()){
                    putString(PREF_TRAVEL_DATE, getDateDMY(it.travelDate))
                }

                setPreference("seatwiseFare", "fromBulkShiftPassenger")
                putString("SelectionCoach", "BOOK")
                putString("fromBusDetails", "bookBlock")
                removeKey("seatwiseFare")
                removeKey("isEditSeatWise")
                removeKey("PERSEAT")
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
                        reservation_id = "$resID",
                        temp = templist,
                        remarks = "",
                        locale = locale
                    ),
                    update_boarded_status_method_name
                )
            } else {
                pickUpChartViewModel.updateBoardedStatusAPI(
                    com.bitla.ts.domain.pojo.update_boarded_status.ReqBody(
                        loginModelPref.api_key,
                        pnrNumber,
                        seatNumber,
                        status,
                        qrCode,//Qr Code
                        skipQrCcode,
                        otp,//New OTP
                        passengerName,
                        "$resID",
                        templist,
                        remarks = remarks,
                        locale = locale
                    ),
                    update_boarded_status_method_name
                )
            }
        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun updateBoardedStatusObserver() {
        pickUpChartViewModel.updateBoardedStatusResponse.observe(viewLifecycleOwner) {
            Timber.d("reservationblock ${it}")
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
                            Timber.d("boardedStatusNumber:0 ${passengerStatus}")
                            val intent =
                                Intent(requireContext(), ViewReservationActivity::class.java)
                            intent.putExtra("pickUpResid", resID)
                            startActivity(intent)

//                            val intent =
//                                Intent(requireContext(), ViewReservationActivity::class.java)
//                            requireContext().startActivity(intent)
//                            requireActivity().finish()

                        } else {
                            Timber.d("boardedStatusNumber:1 ${passengerStatus}")

                            when (passengerStatus) {
                                "0" -> {
                                    boardedStatusText.setText(R.string.yet_to_board)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.colorRed2))
                                    boardedSwitch.isChecked = false

                                }
                                "1" -> {
                                    boardedStatusText.setText(R.string.unboarded_status)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.colorRed2))
                                    boardedSwitch.isChecked = false

                                }

                                "2" -> {
                                    boardedStatusText.setText(R.string.boarded_status)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.colorPrimary))
                                    boardedSwitch.isChecked = true
                                }

                                "3" -> {
                                    boardedStatusText.setText(R.string.no_show)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.black))
                                    boardedSwitch.isChecked = false

                                }

                                "4" -> {
                                    boardedStatusText.setText(R.string.missing_status)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.color_03_review_02_moderate))
                                    boardedSwitch.isChecked = false

                                }

                                "5" -> {
                                    boardedStatusText.setText(R.string.dropped_off)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.colorPrimary))
                                    boardedSwitch.isChecked = false
                                }
                                "9" -> {
                                    boardedStatusText.setText(R.string.check_in)
                                    boardedStatusText.setTextColor(resources.getColor(R.color.colorRed2))
                                    boardedSwitch.isChecked = false
                                }
                            }

                            bottomSheetDialog.dismiss()
                        }

                        /* if(lastPassengerPosition != null){
                             stageAdapter.notifyChildAdapter(lastPassengerPosition!!)

                         }*/
                        var isAgentLogin: Boolean = false
                        var isAllowOnlyOnce = false
                        var checkInToBoard  =false

                        pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
                            privilegeResponse?.apply {
                                isAgentLogin = privilegeResponse?.isAgentLogin ?: false

                                role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, requireContext())
                                if (role == requireContext().getString(R.string.role_field_officer)) {
                                    isAllowOnlyOnce = privilegeResponse?.boLicenses?.allowUserToBoardingStatusOnlyOnce?:false
                                    checkInToBoard  = privilegeResponse?.boLicenses?.allowUserToChangeCheckInStatusToBoardedOnly?: false

                                } else {
                                    isAllowOnlyOnce = privilegeResponse?.availableAppModes?.allow_user_to_change_the_the_boarding_status_only_once?: false
                                    checkInToBoard  = privilegeResponse?.availableAppModes?.allow_user_to_change_the_check_in_status_to_boarded_status_only?: false
                                }
                                // checkInToBoard= true
                                if (isAllowOnlyOnce) {
                                    if (::childStatusOption.isInitialized) {
                                        if (passengerStatus == "0"|| passengerStatus == "9"){
                                            if (passengerStatus== "9" && checkInToBoard){
                                                childStatusOption.gone()
                                                boardedSwitch.isEnabled = true
                                            } else {
                                                childStatusOption.visible()
                                                boardedSwitch.isEnabled = true
                                            }
                                        } else {
                                            childStatusOption.gone()
                                            boardedSwitch.isEnabled = false
                                        }
                                    }
                                } else {
                                    if (passengerStatus== "9" && checkInToBoard) {
                                        childStatusOption.gone()
                                        boardedSwitch.isEnabled = true
                                    }
                                }
                            }
                        }
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
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

            Timber.d("reservationblock ${it}")

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


    private fun adapter(
        respHash: ArrayList<RespHash>?,
        passlist: ArrayList<PassengerDetail>,
        parentVisible: Boolean,
        chartType: String
    ) {
            pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
                privilegeResponse?.apply {
                    if (parentVisible && respHash != null) {
                        layoutManager =
                            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                        binding.passengerListSortby.layoutManager = layoutManager
                        stageAdapter =
                            StageAdapter(
                                context = requireActivity(),
                                onItemClickListener = this@PassengerListFragment,
                                searchList = respHash,
                                isParentVisible = parentVisible,
                                onItemPassData = this@PassengerListFragment,
                                chartType = chartType,
                                onclickitemMultiView = this@PassengerListFragment,
                                currency = currency,
                                currencyFormat = currencyFormat
                                    ?: requireContext().getString(R.string.indian_currency_format),
                                privilegeResponseModel = privilegeResponse,
                                loginModelPref = loginModelPref,
                                onPnrListener = this@PassengerListFragment,
                                onCallClickListener = { phoneNumber ->
                                    if (privilegeResponse?.tsPrivileges?.allowToDisplayCustomerPhoneNumber == true) {
                                        showCallConfirmationBottomSheet(phoneNumber)
                                    } else {
                                        handleCallRequest(phoneNumber)
                                    }
                                }
                            )
                        binding.passengerListSortby.adapter = stageAdapter
                    } else {
                        layoutManager =
                            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                        binding.passengerListSortby.layoutManager = layoutManager
                        childSortSublistAdapter =
                            ChildSortSublistAdapter(
                                context = requireActivity(),
                                directRoute = true,
                                searchList = passlist,
                                branchName = "",
                                onItemClickListener = this@PassengerListFragment,
                                onItemPassData = this@PassengerListFragment,
                                chartClosed = false,
                                onclickitemMultiView = this@PassengerListFragment,
                                currency = currency,
                                currencyFormat = currencyFormat
                                    ?: requireContext().getString(R.string.indian_currency_format),
                                privilegeResponseModel = privilegeResponse,
                                loginModelPref = loginModelPref,
                                onPnrListener = this@PassengerListFragment,
                                chartType = chartType,
                                onCallClickListener = { phoneNumber ->
                                    if (privilegeResponse?.tsPrivileges?.allowToDisplayCustomerPhoneNumber == true) {
                                        showCallConfirmationBottomSheet(phoneNumber)
                                    } else {
                                        handleCallRequest(phoneNumber)
                                    }
                                }
                            )
                        binding.passengerListSortby.adapter = childSortSublistAdapter
                    }
                }
            }
    }


    private fun chartListAdapter(chartList: ArrayList<ChartType>) {
        binding.rvChartList.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        chartAdapter =
            ChartListAdapter(requireActivity(), chartList) { chartTypeId ->
                if (chartType != chartTypeId.toString()) {
                    chartType = chartTypeId.toString()
                    cityselected = false
                    startShimmerEffect()
                    binding.NoResult.gone()
                    pickUpChartApi(chartType)
                    binding.cityHub.text = getString(R.string.show_all)
                }

                firebaseLogEvent(
                    context = requireContext(),
                    logEventName = APPLY_FILTER,
                    loginId = loginModelPref.userName,
                    operatorName = loginModelPref.travels_name,
                    roleName = loginModelPref.role,
                    eventKey = APPLY_FILTER,
                    eventValue = "Apply filter"
                )
            }
        binding.rvChartList.adapter = chartAdapter
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

//        binding.rvChartList.adapter.smoothScrollToPosition(chartType.toInt())
        chartAdapter.notifyDataSetChanged()
        if (chartType == "5") {
            (binding.rvChartList.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                2,
                0
            )

        }
        (binding.rvChartList.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(chartType.toInt(), 0)

    }


    override fun onClickOfNavMenu(position: Int) {
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(view: View, position: Int) {

        try {
            if (view.tag != null) {
                if (view.tag == "CHARTTYPE") {
                    val itemPosition = chartTypeList.indexOfFirst {
                        it.id == position
                    }
                    val itemLabel = chartTypeList.get(itemPosition).label
                    firebaseLogEvent(
                        requireContext(),
                        APPLY_FILTER,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        APPLY_FILTER,
                        itemLabel
                    )
                    if (chartType != position.toString()) {
                        chartType = position.toString()
                        cityselected = false
                        startShimmerEffect()
                        binding.NoResult.gone()
                        pickUpChartApi(chartType)
                        binding.cityHub.text = getString(R.string.show_all)

                    }
                } else {
                    isModifyOptionClicked  = true
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
                        ModifyDetails.MODIFY_DETAILS
                    )

                   // showSingleTicketUpdateSheet(num, seat)
                    val singleTicketUpdateSheet = SingleTicketUpdateSheet(this)
                    singleTicketUpdateSheet.showSingleTicketUpdateSheet(num,seat)

                }

            }
        } catch (e: Exception) {
            Timber.d("ExceptionMsg ${e.message}")
        }

    }

    override fun onClickOfItem(data: String, position: Int) {

        when (data) {
            getString(R.string.edit_passenger_details) -> {
                editPassengerSheet.showEditPassengersSheet(position)


            }
            else -> {
                cancelTicketSheet.showTicketCancellationSheet(position)
            }
        }

    }

    override fun onPnrSelection(tag: String, pnr: Any, doj: Any?) {
        when (tag) {
            getString(R.string.view_ticket) -> {
                viewTicketPNRNumber = pnr.toString()
                val intent= Intent(requireContext(), TicketDetailsActivityCompose::class.java)
                intent.putExtra("returnToDashboard", false)
                intent.putExtra(getString(R.string.TICKET_NUMBER), viewTicketPNRNumber)
                startActivity(intent)


//                getTicketDetailsApi(pnr.toString(),false)
//                    val intent = if (country.equals("India", true) || country.equals("Indonesia", true)) {
//                        Intent(requireContext(), TicketDetailsActivityCompose::class.java)
//                    } else {
//                        Intent(requireContext(), TicketDetailsActivity::class.java)
//                    }
            }
            else -> {
                cancelTicketSheet.showTicketCancellationSheet(pnr)
            }
        }
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }


    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        resID = PreferenceUtils.getString("reservationid") ?: ""
        locale = PreferenceUtils.getString(PREF_LOCALE)

        lifecycleScope.launch {
            val privilege = (activity as BaseActivity).getPrivilegeBaseSafely()
            pickUpChartViewModel.updatePrivileges(privilege)
        }

        pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
            if (privilegeResponse != null) {
                privilegeResponse.apply {
                    restrictSkipVerification = restrictOrHideSkipVerificationOptionInTsApp?:false
                    currency = privilegeResponse.currency ?: getString(R.string.rupeeSybbol)
                    currencyFormat = privilegeResponse.currencyFormat ?: getString(R.string.indian_currency_format)
                    loginModelPref = PreferenceUtils.getLogin()
                    role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, requireContext())
                    serviceName = PreferenceUtils.getString("ViewReservation_name")
                    travelDate = PreferenceUtils.getString("ViewReservation_date")
                    isAllowStatus = availableAppModes?.allowStatus
                    isAllowToChangeStatusOnlyOnce = availableAppModes?.allow_user_to_change_the_the_boarding_status_only_once == true
                    allowBookingForAllotedServices = privilegeResponse?.allowBookingForAllotedServices == true

                    if (role == getString(R.string.role_field_officer)) {
                        if (boLicenses?.updatePassengerTravelStatus == true && sendQrCodeToCustomersToAuthenticateBoardingStatus
                        ) {
                            binding.btnFilterScan.visible()
                        } else {
                            binding.btnFilterScan.gone()
                        }
                    } else {
                        if (updatePassengerTravelStatus && sendQrCodeToCustomersToAuthenticateBoardingStatus
                        ) {
                            binding.btnFilterScan.visible()
                        } else {
                            binding.btnFilterScan.visible()

                        }
                    }

                    this@PassengerListFragment.operatorName = operatorName?:""
                    isAgentRole = role == getString(R.string.agent)
                }
            } else {
                requireContext().toast(requireContext().getString(R.string.server_error))
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        //super.onSingleButtonClick(str)

        passengerStatus = str
        Timber.d("dataString : ${str}")

        if (citySqeName.contains(str)) {

            binding.cityHub.text = str
            selected = citySqeId[citySqeName.indexOf(str)]
            cityselected = selected != 0
            startShimmerEffect()
            pickUpChartApi(chartType)
        } else if (str == "0"
            || str == "1"
            || str == "2"
            || str == "3"
            || str == "4"
            || str == "5"
            || str == "9"
        ) {
            Timber.d("dataString : ${str}")

        } else if (str == "Cancel") {
            Timber.d("cancelled the selection")
        } else if (str == "luggage") {
            val cargo = PreferenceUtils.getString("cargoDetails")!!.split(",")
            val amount = cargo[0]
            val quantity = cargo[1]
            val item = cargo[2]

            updateCargodetails(pnrNum!!, seatNum!!, passStatus, amount, quantity, item)
        } else if (str.contains("scan")) {
            var tempRemarks = ""
            if (str.contains("&")) {
                val scan = str.split("&")
                tempRemarks = scan[1]
            }

            binding.NoResult.gone()
            startShimmerEffect()
            pickUpChartApi(chartType)
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
                otp = "",
                qrCode = qrresponse,
                pnrNumber = qrSelectedPnrNumber,
                seatNumber = seat,
                status = "2",
                templist = temlist,
                remarks = tempRemarks
            )

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
                    CloseChart.CLOSE_CHART
                )
                try {
                    closeChartByCity(str1, str2)
                } catch (e: Exception) {
                    Timber.d("error:  ${e}")
                }
            }

        }
        Timber.d("qwe123 ${pnrNum}, ${seatNum}, ${passengerStatus}, ${passengerName}")

    }


    override fun onItemDataMore(
        view: View,
        str1: String,
        str2: String,
        str3: String
    ) {
        if (view.tag != null) {
            passengerName = str1
            seatNum = str2
            pnrNum = str3
            skipQrCcode = false

            if (view.tag == "luggage") {
                val genderAger = PreferenceUtils.getString("genderAge")!!.split(",")
                passStatus = genderAger[0]
                passGender = genderAger[1]
                passAge = genderAger[2]

                DialogUtils.luggageIndiaDialogBox(
                    context = requireContext(),
                    pnr = pnrNum.toString(),
                    name = passengerName,
                    seatNumber = seatNum.toString(),
                    boardedStatus = passStatus,
                    age = passAge,
                    gender = passGender,
                    singleButtonListener = this
                )
            }
        }
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
                    ShowAll.SHOW_ALL
                )
                if (binding.cityHub.text == getString(R.string.show_all)) {
                    binding.svSearchPnrMobileNum.gone()
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

        }
    }

    fun boarded(passengerNam: String, seatNumber: String, pnrNumber: String) {
        bindingSheet.btnVerifyBoarding.text = getString(R.string.update)
        pnrNum = pnrNumber
        seatNum = seatNumber
        passengerName = passengerNam
        var isBoardedSms = false
        var isBoardedQr = false
        
        bindingSheet.remarksLayout.gone()
        bindingSheet.etRemarksText.isEnabled = false
        bindingSheet.bottomSheetHeader.text = getString(R.string.verify_boarding)

        pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
            privilegeResponse?.apply {
                privilegeResponse?.let {
                    isBoardedSms = privilegeResponse?.sendOtpToCustomersToAuthenticateBoardingStatus?:false
                    isBoardedQr = privilegeResponse?.sendQrCodeToCustomersToAuthenticateBoardingStatus?:false
                    if (privilegeResponse?.allowToCapturePassAndCrewTemp == true) {
                        bindingSheet.lpassengerTemp.visible()
                    } else {
                        bindingSheet.lpassengerTemp.gone()
                    }
                }

//                Timber.d("test123ClickEt::0  $isBoardedSms, $isBoardedQr")
//                Timber.d("test123ClickEt::0  ${privilegeResponse?.validateRemarksForBoardingStageInMobilityApp}, ${privilegeResponse?.allowToCapturePassAndCrewTemp}")

                binding.apply {
                    if (!isBoardedQr && !isBoardedSms) {
                        bindingSheet.skipVerification.gone()

                        if (bindingSheet.lpassengerTemp.isVisible) {
                            bindingSheet.apply {
                                scanLayout.gone()
                                otpText.gone()
                                lenterotp.gone()
                                resendOtp.gone()
                                etenterOtp.clearFocus()
                                etenterOtp.text?.clear()
                                etPassengerTemp.clearFocus()
                                etPassengerTemp.text?.clear()
                            }

                            Timber.d("verificationobserverCheck::: 3")

                            verifyBtnObserver(
                                verifybutton = true,
                                otp = "",
                                qr = "", pnrNumber = pnrNumber,
                                seatNumber = seatNumber,
                                remarks = "remark",
                                status = "2"
                            )

                            bottomSheetDialog.show()
                        } else {
                            bindingSheet.lpassengerTemp.gone()
                            Timber.d("updateBoardedStatusApi:2 ${passengerStatus}")

                            updateBoardedStatusApi(
                                otp = newOtp,
                                qrCode = "",
                                pnrNumber = pnrNum!!,
                                seatNumber = seatNum!!,
                                status = "2",
                                templist = templist,
                                remarks = ""
                            )
                        }

                    } else {
                        bindingSheet.apply {
                            etenterOtp.clearFocus()
                            etenterOtp.text?.clear()
                            etPassengerTemp.clearFocus()
                            etPassengerTemp.text?.clear()
                        }
                        qrresponse = ""

                        if(!restrictSkipVerification){
                            bindingSheet.skipVerification.visible()
                        }else{
                            bindingSheet.skipVerification.gone()
                        }

                        if (isBoardedQr && isBoardedSms) {
                            if(!restrictSkipVerification){
                                bindingSheet.skipVerification.visible()
                            }else{
                                bindingSheet.skipVerification.gone()
                            }

                            bindingSheet.apply {
                                btnVerifyBoarding.text = getString(R.string.update)
                                scanLayout.visible()
                                otpText.visible()
                                lenterotp.visible()
                                resendOtp.visible()
                                etenterOtp.clearFocus()
                                etenterOtp.text?.clear()
                                etPassengerTemp.clearFocus()
                                etPassengerTemp.text?.clear()
                                resendQrImg.gone()
                                resendSmsImg.visible()
                                scanQrCode.setOnClickListener(this@PassengerListFragment)
                            }

                            bottomSheetDialog.show()

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
                                            Timber.d("verificationobserverCheck::: 4")

                                            verifyBtnObserver(
                                                verifybutton = false,
                                                otp = "",
                                                qr = qrresponse,
                                                pnrNumber = pnrNumber,
                                                seatNumber = seatNumber,
                                                remarks = "",
                                                status = "2"
                                            )
                                        } else {
                                            Timber.d("verificationobserverCheck::: 5")

                                            verifyBtnObserver(
                                                verifybutton = true,
                                                otp = "",
                                                qr = qrresponse,
                                                pnrNumber = pnrNum!!,
                                                seatNumber = seatNum!!,
                                                remarks = "",
                                                status = "2"
                                            )
                                        }
                                    } else {

                                        Timber.d("verificationobserverCheck::: 6")
                                        verifyBtnObserver(
                                            verifybutton = true,
                                            otp = s.toString(),
                                            qr = "",
                                            pnrNumber = pnrNumber,
                                            seatNumber = seatNumber,
                                            remarks = "remark2",
                                            status = "2"
                                        )
                                    }
                                }
                            })
                        }

                        if (isBoardedQr && !isBoardedSms) {
                            //bindingSheet.skipVerification.visible()
                            if(!restrictSkipVerification){
                                bindingSheet.skipVerification.visible()
                            }else{
                                bindingSheet.skipVerification.gone()
                            }
                            bindingSheet.apply {
                                btnVerifyBoarding.text = getString(R.string.update)
                                scanLayout.visible()
                                resendQrImg.visible()
                                resendOtp.visible()
                                etenterOtp.clearFocus()
                                etenterOtp.text?.clear()
                                etPassengerTemp.clearFocus()
                                etPassengerTemp.text?.clear()
                                lenterotp.gone()
                                otpText.gone()
                                resendText.text = requireContext().getString(R.string.resend_qr)
                                scanQrCode.setOnClickListener(this@PassengerListFragment)
                                resendQrImg.visible()
                                resendSmsImg.gone()
                            }
                            bottomSheetDialog.show()

                        }

                        if (!isBoardedQr && isBoardedSms) {
                            //bindingSheet.skipVerification.visible()
                            if(!restrictSkipVerification){
                                bindingSheet.skipVerification.visible()
                            }else{
                                bindingSheet.skipVerification.gone()
                            }

                            bindingSheet.apply {
                                btnVerifyBoarding.text = getString(R.string.update)
                                scanLayout.gone()
                                otpText.visible()
                                lenterotp.visible()
                                resendOtp.visible()
                                etenterOtp.clearFocus()
                                etenterOtp.text?.clear()
                                etPassengerTemp.clearFocus()
                                etPassengerTemp.text?.clear()
                                etRemarksText.text?.clear()
                                scanLayout.gone()
                                resendQrImg.gone()
                                resendSmsImg.visible()
                            }

                            bottomSheetDialog.show()

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
                                        Timber.d("verificationobserverCheck::: 7")

                                        verifyBtnObserver(false, "", "", "", "", "", "")
                                    } else {

                                        Timber.d("verificationobserverCheck::: 8")

                                        verifyBtnObserver(
                                            verifybutton = true,
                                            otp = s.toString(),
                                            qr = "",
                                            pnrNumber = pnrNumber,
                                            seatNumber = seatNumber,
                                            remarks = "",
                                            status = "2"
                                        )
                                    }
                                }
                            })
                        }

                        bindingSheet.resendOtp.setOnClickListener(this@PassengerListFragment)
                        bindingSheet.skipVerification.setOnClickListener {
                            Timber.d("skipQrTEst: 0")

                            skipQrCcode = true
                            var remarks = ""
                            var temp = arrayListOf<String>()
                            Timber.d("skipQrTEst: 1")


                            if (bindingSheet.lpassengerTemp.isVisible) {
                                Timber.d("skipQrTEst: 1")

                                val temp2 = bindingSheet.etPassengerTemp.text.toString()
                                if (temp2.isNullOrEmpty()) {

                                    requireContext().toast(requireContext().getString(R.string.please_enter_temperature))
                                } else {
                                    val floatTemp = temp2.toFloat()
                                    if (floatTemp in 89.00..108.00) {
                                        temp =
                                            arrayListOf("${seatNum!!}:$floatTemp")
                                    }
                                }
                                Timber.d("updateBoardedStatusApi:3 ${passengerStatus}")

                                updateBoardedStatusApi(
                                    this@PassengerListFragment.newOtp,
                                    qrCode = "",
                                    pnrNumber = pnrNum!!,
                                    seatNumber = seatNum!!,
                                    status = "2",
                                    templist = temp,
                                    remarks = ""
                                )
                            } else {
                                Timber.d("skipQrTEst: 2")

                                Timber.d("updateBoardedStatusApi:4 ${passengerStatus}")

                                updateBoardedStatusApi(
                                    otp = newOtp,
                                    qrCode = "",
                                    pnrNumber = pnrNum!!,
                                    seatNumber = seatNum!!,
                                    status = "2",
                                    templist = temp,
                                    remarks = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun reset(flag: Boolean) {
        if (flag)
            startShimmerEffect()
    }

    private fun scanScaeen() {
        val scanner = IntentIntegrator.forSupportFragment(this)
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        scanner.setBeepEnabled(true)
        scanner.setBarcodeImageEnabled(true)
        scanner.initiateScan()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                try {
                    if (result.contents == null) {
                        Toast.makeText(requireActivity(), getString(R.string.cancelled), Toast.LENGTH_LONG).show()
                    } else {
                      //  Timber.d("qrScanResult :  ${result.getContents()}")
                        qrresponse = result.contents
                        if (scanTag == "QuickScan") {

                            val pnrNumber = result.contents
                            if (pnrNumber.isNotEmpty()) {
                                if (requireContext().isNetworkAvailable()) {
                                    val fromQr= !pnrNumber.contains("PNR!", true)
                                    getTicketDetailsApi(pnrNumber, fromQr)
                                } else requireContext().noNetworkToast()
                            }
                        } else {
                           // Timber.d("observer:Check: 2")

                            pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
                                privilegeResponse?.apply {
                                    if (privilegeResponse?.allowToCapturePassAndCrewTemp == true) {
                                        bindingSheet.lpassengerTemp.visible()
                                    } else {
                                        bindingSheet.lpassengerTemp.gone()
                                    }
                                    bindingSheet.scanqrText.text = "QR Scanned"

                                    if (bindingSheet.remarksLayout.isVisible) {
                                        remarksObserver(pnrNum!!)
                                    } else {
                                        verifyBtnObserver(
                                            verifybutton = true,
                                            otp = "",
                                            qr = qrresponse,
                                            pnrNumber = pnrNum!!,
                                            seatNumber = seatNum!!,
                                            remarks = "",
                                            status = "2"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }catch (e : Exception){
                    if(BuildConfig.DEBUG){
                        e.printStackTrace()
                    }
                }

            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun cityDialog(reset: Boolean) {
        DialogUtils.cityFilterDialog(
            context = requireActivity(),
            searchList = citySqeName,
            title = getString(R.string.filter_by),
            btnText = getString(R.string.apply_filter),
            reset = reset,
            isCancelBtnVisible = true,
            applyCityFilter = { cityName, CitySelected ->

            }
        )
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
            // Filter the passengers within the RespHash based on the query
            val filteredPassengers = respHash.passengerDetails.filter { passenger ->
                passenger.pnrNumber.lowercase(Locale.getDefault()).contains(searchText) ||
                        passenger.phoneNumber.lowercase(Locale.getDefault()).contains(searchText)
            }

            if (filteredPassengers.isNotEmpty()) {
                filteredRespHashList.add(
                    respHash.copy(
                        passengerDetails = ArrayList(filteredPassengers)
                    )
                )
            }
        }

        return filteredRespHashList
    }

    private fun filterPassengerDetailsBySearch(
        passengerList: ArrayList<PassengerDetail>,
        query: String?
    ): ArrayList<PassengerDetail> {
        val searchText = query?.lowercase(Locale.getDefault()) ?: ""
        if (searchText.isEmpty()) {
            return passengerList
        }

        val result = mutableSetOf<PassengerDetail>()

        passengerList.forEach { passenger ->
            val isPNRMatch = passenger.pnrNumber.lowercase(Locale.getDefault()).contains(searchText)
            val isMobileMatch = passenger.phoneNumber?.lowercase(Locale.getDefault())?.contains(searchText) == true

            // Add passenger if PNR matches, or if mobile number matches but PNR isn't already added
            if (isPNRMatch || (isMobileMatch && result.none { it.pnrNumber == passenger.pnrNumber })) {
                result.add(passenger)
            }
        }

        return ArrayList(result)
    }


    fun remarksObserver(pnrNumber: String) {
        bindingSheet.etRemarksText.isEnabled = true
        if (bindingSheet.etRemarksText.isVisible) {
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
                    Timber.d("testclick123: $s")
                    if (s.isNullOrEmpty()) {
                        Timber.d("verificationobserverCheck::: 1")

                        verifyBtnObserver(
                            false,
                            otp = newOtp,
                            qr = qrresponse,
                            pnrNumber = "",//pnrNumber
                            seatNumber = "",
                            remarks = "",
                            status = ""
                        )
                    } else {
                        Timber.d("verificationobserverCheck::: 2")
                        verifyBtnObserver(
                            verifybutton = true,
                            otp = newOtp,
                            qr = qrresponse,
                            pnrNumber = pnrNumber,//pnrNumber
                            seatNumber = seatNum!!,
                            remarks = s.toString(),
                            status = passengerStatus.toString()
                        )
                    }
                }
            })
        }
    }

    private fun getTicketDetailsApi(pnrNumber: String, fronQrScan:Boolean) {

        ticketDetailsViewModel.ticketDetailsApi(
            apiKey = loginModelPref.api_key,
            ticketNumber = pnrNumber,
            jsonFormat = true,
            isQrScan = fronQrScan, locale = locale!!,
            apiType = ticket_details_method_name
        )
    }

    private fun setTicketDetailsObserver() {
        ticketDetailsViewModel.dataTicketDetails.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.code == 200) {

                    if (isViewTicketClicked) {
                        val intent= Intent(requireContext(), TicketDetailsActivityCompose::class.java)
                        intent.putExtra("returnToDashboard", false)
                        intent.putExtra(getString(R.string.TICKET_NUMBER), viewTicketPNRNumber)
                        startActivity(intent)
                        isViewTicketClicked = false
                    } else {
                        if(!isModifyOptionClicked){
                            val passdetail =
                                arrayListOf<com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail?>()
                            qrSelectedPnrNumber = it.body.ticketNumber!!
                            qrSelectedResId = it.body.reservationId.toString()

                            it.body.passengerDetails?.forEach {
                                if (it!!.boardingStatus!!.lowercase(Locale.getDefault()) == "yet to board") {
                                    passdetail.add(it)
                                }
                            }
                            if(resID.toString().equals(qrSelectedResId)){
                                if (passdetail.isEmpty()) {
                                    requireContext().toast(getString(R.string.all_passengers_are_boarded_fro_this_pnr))
                                } else {
                                    DialogUtils.dialogScanStatus(
                                        context = requireContext(),
                                        searchList = passdetail,
                                        pnr = it.body.ticketNumber,
                                        btnText = getString(R.string.verify),
                                        dialogSingleButtonListener = this,
                                        onItemCheckedMultipledataListner = this
                                    )
                                }
                            }
                            else{
                                requireActivity().toast(getString(R.string.customer_does_not_belong_to_this_service))
                            }
                        }
                    }
                } else
                    if (isViewTicketClicked) {
                        if (it.message != null) {
                            it.message.let { it1 ->
                                requireContext().toast(it1)
                            }
                            isViewTicketClicked = false
                        }
                    }

            } else {
                requireContext().toast(getString(R.string.server_error))
                isViewTicketClicked = false
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
        //startShimmerEffect()
        pickUpChartApi(chartType)
    }


    private fun startShimmerEffect() {
        binding.apply {
           mainLayout.gone()
           NoResult.gone()
           svSearchPnrMobileNum.gone()
           passengerListSortby.gone()
           refreshCard.gone()
           shimmerLayout.visible()
           shimmerLayout.startShimmer()
        }
      

    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.apply {
            shimmerLayout.gone()
            mainLayout.visible()
            refreshCard.visible()
            if (shimmerLayout.isShimmerStarted) {
                shimmerLayout.stopShimmer()
                
            }
        }
    }

    fun verifyBtnObserver(
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
                            otp = otp,
                            qrCode = qr,
                            pnrNumber = pnrNumber,
                            seatNumber = seatNumber,
                            status = status,
                            templist = templistSingle,
                            remarks = remarks
                        )
                    } else {
                        try {
                            val floatTemp = temp2.toFloat()
                            if (floatTemp in 89.00..108.00) {
                                val templistSingle = listOf("$seatNumber:$temp2")
                                Timber.d("updateBoardedStatusApi:6${passengerStatus}")

                                updateBoardedStatusApi(
                                    otp = otp,
                                    qrCode = qr,
                                    pnrNumber = pnrNumber,
                                    seatNumber = seatNumber,
                                    status = status,
                                    templist = templistSingle,
                                    remarks = remarks
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
                            otp = otp,
                            qrCode = qr,
                            pnrNumber = pnrNumber,
                            seatNumber = seatNumber,
                            status = status,
                            templist = templistSingle,
                            remarks = ""
                        )
                    } else {
                        try {
                            val floatTemp = temp2.toFloat()
                            if (floatTemp in 89.00..108.00) {
                                val templistSingle = listOf("$seatNumber:$temp2")
                                Timber.d("updateBoardedStatusApi:9${passengerStatus}")

                                updateBoardedStatusApi(
                                    otp = otp,
                                    qrCode = qr,
                                    pnrNumber = pnrNumber,
                                    seatNumber = seatNumber,
                                    status = passengerStatus!!,
                                    templist = templistSingle,
                                    remarks = ""
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
                api_key = loginModelPref.api_key,
                city_id = cityId,
                res_id = resID.toString(),
                stage_id = stageId,
                locale = locale

            ),
            city_pickup_chart_by_stage
        )
    }

    private fun cityPickupByStageObserver() {

        pickUpChartViewModel.cityPickupChartByStageResponse.observe(viewLifecycleOwner) {

            Timber.d("reservationblock ${it}")

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
        lastPassengerPosition = view4.tag.toString().toInt()
        boardedSwitch = view3 as SwitchCompat
        boardedStatusText = view2 as TextView
        if (view3.tag == "boarded") {
            passengerStatus = "2"
            try {
                boarded(passengerName, seatNum, view.tag.toString())
            } catch (e: Exception) {
                requireContext().toast(getString(R.string.opps))
            }
        } else {
            pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
                privilegeResponse?.apply {
                    val appModes = privilegeResponse?.availableAppModes

                    if (privilegeResponse?.country.equals("india", true)) {
                        if (appModes?.yet_to_board_status == true || appModes?.dropped_off_status == true || appModes?.boarded_status == true || appModes?.missing_status == true || appModes?.no_show_status == true || appModes?.unboarded_status == true) {
                            openStatusDialog(view, view2, view3, view4, passengerName, seatNum)
                        } else {
                            requireActivity().toast(getString(R.string.no_status_found))
                        }
                    } else {
                        openStatusDialog(view, view2, view3, view4, passengerName, seatNum)
                    }
                }
            }


        }
    }

    private fun openStatusDialog(
        view: View,
        view2: View,
        view3: View,
        view4: View,
        passengerName: String,
        seatNum: String
    ) {
        DialogUtils.statusIndiaDialog(
            context = requireContext(),
            buttonLeftText = getString(R.string.goBack),
            buttonRightText = getString(R.string.confirm),
            dialogSingleButtonListener = this,
            dialogButtonMultipleView = this,
            view1 = view,
            view2 = view2,
            view3 = view3,
            view4 = view4,
            resId = passengerName,
            remarks = seatNum
        )

        firebaseLogEvent(
            context = requireContext(),
            logEventName = STATUS,
            loginId = loginModelPref.userName,
            operatorName = loginModelPref.travels_name,
            roleName = loginModelPref.role,
            eventKey = STATUS,
            eventValue = Status.STATUS
        )
    }

    override fun onClickAdditionalData(view0: View, view1: View) {
        childStatusOption = view1 as LinearLayout
        boardedLayout = view0 as LinearLayout

    }

    override fun onLeftButtonClick(
        view: View?,
        view1: View?,
        view2: View?,
        view3: View?,
        resId: String
    ) {
        Timber.d("nothing selected")
    }

    override fun onRightButtonClick(
        view0: View?,
        view1: View?,
        view2: View?,
        view3: View?,
        resId: String,
        seatNumber: String
    ) {
        Timber.d("observer:Check: 1")

        val statusSelected = PreferenceUtils.getPreference("pickUpChartStatus", "")
        Timber.d("passstatusoldOne: $passengerStatus")
        if (passengerStatus == null) {
            requireContext().toast(getString(R.string.please_selecte_an_option))
        } else if (passengerStatus == "2") {
            Timber.d("observer:Check: 00")
            try {
                boarded(resId/*passenger Name*/, seatNumber/*SeatNumber*/, view0!!.tag.toString())
            } catch (e: Exception) {
                requireContext().toast(getString(R.string.opps))
            }
        } else {
            Timber.d("observer:Check: 001")

            pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
                privilegeResponse?.apply {
                    if (privilegeResponse != null) {
                        privilegeResponse?.let {
                            Timber.d("privilegeoutput12: ${privilegeResponse?.validateRemarksForBoardingStageInMobilityApp}")
                            if (privilegeResponse?.validateRemarksForBoardingStageInMobilityApp == true) {

                                bindingSheet.apply {
                                    lpassengerTemp.gone()
                                    remarksLayout.visible()
                                    bottomSheetHeader.text = getString(R.string.remarks)
                                    skipVerification.gone()
                                    scanLayout.gone()
                                    otpText.gone()
                                    lenterotp.gone()
                                    resendOtp.gone()
                                    etRemarksText.text?.clear()
                                    etRemarksText.requestFocus()
                                }

                                bottomSheetDialog.show()
                                qrresponse = ""
                                newOtp = ""
                                seatNum = seatNumber
                                try {
                                    remarksObserver(view0!!.tag.toString())
                                } catch (e: Exception) {
                                    requireContext().toast(getString(R.string.opps))
                                    bottomSheetDialog.dismiss()
                                }
                            } else {
                                Timber.d("privilegeoutput: false")
                                Timber.d("updateBoardedStatusApi:10${passengerStatus}")

                                updateBoardedStatusApi(
                                    "",
                                    "",
                                    view0!!.tag.toString(),//pnrNumber
                                    seatNumber,//seatNumber
                                    passengerStatus.toString(),
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
                            printBluetooth()
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

    private fun printBluetooth() {
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
            .execute(this.getAsyncEscPosPrinter(selectedDevice))
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
                val seatNo = "Seat: ${passengerlist[i].seatNumber}\n"
                val name = "Name: ${passengerlist[i].passengerName}\n"
                val pnrNo = "PNR number: ${passengerlist[i].pnrNumber}\n"
                val boardingStage = "Boarding stage: ${passengerlist[i].stageName}\n"
                val bookedBy = "Booked By: ${passengerlist[i].bookedBy}\n"
                bluetoothTemplateList.add(seatNo)
                bluetoothTemplateList.add(name)
                bluetoothTemplateList.add(pnrNo)
                bluetoothTemplateList.add(boardingStage)
                bluetoothTemplateList.add(bookedBy)
                bluetoothTemplateList.add("\n")

                if (stageSummaryList.any { it.boarding_dropping == passengerlist[i].stageName }) {
                    boardingCount++
                    val index =
                        stageSummaryList.indexOfFirst { it.boarding_dropping == passengerlist[i].stageName }
                    if (index != -1) {
                        stageSummaryList.removeAt(index)
                    }
                } else
                    boardingCount = 1
                val stageSummary =
                    PrintStageSummary(passengerlist[i].stageName, boardingCount, true)
                stageSummaryList.add(stageSummary)



                if (stageSummaryList.any { it.boarding_dropping == passengerlist[i].droppingPoint }) {
                    droppingCount++

                    val index =
                        stageSummaryList.indexOfFirst { it.boarding_dropping == passengerlist[i].droppingPoint }
                    if (index != -1) {
                        stageSummaryList.removeAt(index)
                    }
                } else
                    droppingCount = 1
                val stageSummary1 =
                    PrintStageSummary(passengerlist[i].droppingPoint, droppingCount, false)
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
        Timber.d("bluetoothPrintTemplate $bluetoothTemplate")
        bluetoothTemplate = bluetoothTemplateList.toString().removePrefix("[").removeSuffix("]").replace(",", "")
        return printer.addTextToPrint(
            bluetoothTemplate
        )
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
            pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
                if (privilegeResponse != null) {
                    if (privilegeResponse?.country != null) {
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
        }
    }
}

