package com.bitla.ts.presentation.view.dashboard.ViewReservationFragments

import EndlessRecyclerOnScrollListener
import android.Manifest
import android.annotation.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.content.res.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.activity.result.contract.*
import androidx.annotation.*
import androidx.cardview.widget.*
import androidx.core.app.*
import androidx.core.content.res.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctRequest.*
import com.bitla.ts.domain.pojo.alloted_services.*
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.alloted_services.ViewSummary
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.block_unblock_reservation.ReasonList
import com.bitla.ts.domain.pojo.block_unblock_reservation.request.*
import com.bitla.ts.domain.pojo.block_unblock_reservation.request.ReqBody
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.city_details.request.*
import com.bitla.ts.domain.pojo.lock_chart.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.service_details.request.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.viewSummary.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.security.EncrypDecryp
import com.bitla.ts.utils.sharedPref.*
import com.bitla.tscalender.*
import com.google.android.material.bottomsheet.*
import com.google.android.material.snackbar.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*


class ReservationChartFragment : BaseFragment(), View.OnClickListener, OnItemClickListener,
    SlyCalendarDialog.Callback, DialogButtonTagListener, DialogButtonListener,
    OnclickitemMultiView, OnItemPassData, DialogSingleButtonListener,
    RemoteConfigUpdateHelper.LocationPopup, DialogButtonAnyDataListener {

    companion object{
        val tag = ReservationChartFragment::class.java.simpleName
    }

//    private var privilegeResponse: PrivilegeResponseModel? = null
    private var filteredServiceName: String? = ""
    private var filteredResvId: String? = ""
    private var filteredServiceId: String? = ""
    private var serviceFilterPosition: Int = 0
    private var selectedServiceFilter = "1"
    private var popUpDialog: PopupMenu? = null
    private var sevenDaysDate: String = getTodayDate()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var nextDateAdapter: NextDateAdapterReservation
    private lateinit var myReservationAdapter: MyReservationAdapter
    private lateinit var myReservationHubAdapter: MyReservationHubsAdapter
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var dateList = mutableListOf<StageData>()
    private var convertedDate: String? = null

    private var groupByHubs = false
    private var isActiveService = true
    private var searchList1 =
        ArrayList<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>()
    private var hub = ArrayList<RespHash>()
    private var hubservice = ArrayList<ArrayList<Service>>()
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var finaloriginID: String = ""
    private var finaldestinationId: String = ""
    private var originData = ""
    private var destinationData = ""
    private var ymdDate: String = ""
    private var origindataID: String = "0"
    private var destinationdataID: String = "0"
    private var travelDate: String = ""
    private var travelSelection: String? = ""
    private var summary: com.bitla.ts.domain.pojo.viewSummary.ViewSummary? = null
    private var hubSummary = ArrayList<ViewSummary>()
    private var serviceDirect =
        arrayListOf<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>()
    private var tempServiceDirect =
        arrayListOf<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>()
    private var resID: Long? = null
    private var temOrigin = ""
    private var temDestination = ""
    private var temIsHubs = false
    private var isactivetemp = true
    private var btnValidate = false
    private var lockres = 0
    private var directionUrl: String = ""
    private var maxPage: Int = 0

    private var originChangeCheck = false
    private var destinationChangeCheck = false
    private var activeCheck = false
    private var hubChangeCheck = false
    private var popUpText = ""


    lateinit var binding: FragmentReservationChartBinding
    lateinit var binding1: ReservationbottomSheetBinding
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()

    private var boardingList: MutableList<StageDetail>? = null
    private var droppingList: MutableList<StageDetail>? = null
    private var boardingListTemp: MutableList<BoardingPointDetail>? = null
    private var droppingListTemp: MutableList<DropOffDetail>? = null
    private var stageDetails = mutableListOf<StageDetail>()

    private val cityDetailViewModel by viewModel<CityDetailViewModel<Any?>>()

    private var tempOriginList = arrayListOf<String>()
    private var tempOriginId = arrayListOf<Int?>()
    private var count = 0
    private var canclePressed = 0
    private var cityId: String? = ""
//    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    var isAgentLogin: Boolean = false
    var country = ""
    var currency = ""
    var currencyFormat = ""
    private var locale: String? = ""
    private var foregroundOnlyLocationServiceBound = false

    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null
    private var bottomSheetDialoge: BottomSheetDialog? = null

    private var privilegeResponseModel: PrivilegeResponseModel? = null

    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }
    private var hubsList: ArrayList<HubDetails>? = arrayListOf()
    private var spinnerHUbs = mutableListOf<SpinnerItems>()
    private var selectedHubId: Int? = null
    private var finalSelectedHubId: Int? = 0
    private var sectedHubName = ""
    private var resumeCall = false
    private var recallAdapter = false
    private var recallAdapterHub = false
    lateinit var domain: String


    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener
    private val QUERY_PER_PAGE = 6
    private var tempnum = 0

    private var PAGE_NUMBER = 1

    private var pinSize = 0
    private var shouldBlockReservation = false

    private var blockReasonsList = mutableListOf<ReasonList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            cityDetailViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (resumeCall) {
            binding.constraintLayout.gone()
            startShimmerEffect()
            tempServiceDirect.clear()
            searchList1.clear()
            serviceDirect.clear()
            PAGE_NUMBER = 1
            maxPage = 0

            allotedDirectService(
                PAGE_NUMBER,
                ymdDate,
                finaloriginID,
                finaldestinationId,
                finalSelectedHubId
            )

        } else {
            resumeCall = true
        }

//        callAllotedSearviceApi(ymdDate, finaloriginID, finaldestinationId)

    }

    override fun isInternetOnCallApisAndInitUI() {

        if (isAttachedToActivity()) {
//            init()
        }
        if (bottomSheetDialoge?.isShowing == true) {
            bottomSheetDialoge?.dismiss()
        }

    }

    override fun isNetworkOff() {

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReservationChartBinding.inflate(inflater, container, false)
        setDateLocale(PreferenceUtils.getlang(), requireContext())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false) // Enables edge-to-edge
            ViewCompat.setOnApplyWindowInsetsListener(binding.btnFilter) { view, insets ->
                val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = bottomInset + 16.toDp // add extra margin if needed
                view.layoutParams = layoutParams
                insets
            }

        }

        init()

        bottomSheetDialoge?.setOnDismissListener {
            it?.dismiss()
        }
        return binding.root
    }

    private fun init() {
        onClickListener()
        travelDate = getTodayDate()

        ymdDate = SimpleDateFormat("yyyy-mm-dd").format(SimpleDateFormat("dd-mm-yyyy").parse(travelDate))

//        if ((activity as BaseActivity).getPrivilegeBase() != null) {
//            privilegeResponse = (activity as BaseActivity).getPrivilegeBase()
//            privilegeResponse?.let {
//                sevenDaysDate =
//                    if (privilegeResponse?.availableAppModes?.allowToShowPickupChartForPastDates == true)
//                        getYesterdayDate()
//                    else
//                        getTodayDate()
//            }
//        } else {
//            requireContext().toast(requireContext().getString(R.string.server_error))
//        }

        binding.constraintLayout.gone()
        getPref()
        setObserver()
        startShimmerEffect()
        initRefreshListner()
        setPrivilegeObserver()
        allotedObserver()
        setCityDetailsObserver()

        callCityDetailsApi()
        locationPopUpCheck()
        viewSummaryObserver()

//        allotedDirectService(
//            PAGE_NUMBER,
//            ymdDate,
//            finaloriginID,
//            finaldestinationId,
//            finalSelectedHubId
//        )
//        allotedServiceDirectApi(PAGE_NUMBER,ymdDate, finaloriginID,finaldestinationId, finalSelectedHubId)
//        viewSummaryApi(ymdDate, finaloriginID, finaldestinationId, finalSelectedHubId)
        onScrollListener = object : EndlessRecyclerOnScrollListener(tempnum) {
            override fun onLoadMore() {
                Timber.d("checkCallApiNumber:1")

                Timber.d("checkCallApiNumber:")
                if (PAGE_NUMBER < maxPage) {
                    binding.reservationProgressBar.visible()
                    //Handler(Looper.getMainLooper()).postDelayed({

                    PAGE_NUMBER += 1

                    allotedDirectService(
                        PAGE_NUMBER,
                        ymdDate,
                        finaloriginID,
                        finaldestinationId,
                        finalSelectedHubId
                    )
                    //}, 1500)

                }
            }
        }
        binding.rvreservationPickup.addOnScrollListener(onScrollListener)

        PreferenceUtils.setPreference("selectedCityDestination", destinationData)
        PreferenceUtils.setPreference("selectedCityIdDestination", destinationdataID)
        PreferenceUtils.setPreference("TravelSelection", "none")

    }

    private fun setPrivilegeObserver() {
        pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) {

            if (it != null) {
                privilegeResponseModel = it
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun setObserver() {
        sharedViewModel.serviceDetails.observe(viewLifecycleOwner, Observer {
            when (it.code) {
                200 -> {
                    boardingList = mutableListOf()
                    droppingList = mutableListOf()
                    stageDetails = it.body.stageDetails!!
                    for (i in 0..it.body.stageDetails?.size!!.minus(1)) {

                        if (it?.body?.stageDetails!![i].cityId.toString() == origindataID) {
                            generateBoardingList(i)
                        } else {
                            generateDroppingList(i)
                        }
                    }
                    val availableSeatList = mutableListOf<String>()
                    val passengerList = mutableListOf<PassengerDetails>()

                    val seatDetails: List<SeatDetail>? = it?.body?.coachDetails?.seatDetails
                    seatDetails?.forEach { it ->
                        if (it != null) {
                            if (it.available != null && it.available!!)
                                availableSeatList.add(
                                    "${it.number} ($currency${
                                        it.fare.toString().toDouble().convert(currencyFormat)
                                    })"
                                )
                            if (it.passengerDetails != null) {
                                passengerList.add(it.passengerDetails!!)
                            }
                        } else {
                            requireContext().toast(getString(R.string.server_error))
                        }
                    }
                    availableSeats(availableSeats = availableSeatList)
                    setPassengerDetails(passengerDetails = passengerList)

                    val intent = Intent(requireContext(), EditChartActivity::class.java)
                    requireContext().startActivity(intent)
                }

                401 -> {
                   /* DialogUtils.unAuthorizedDialog(
                        requireContext(),
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    (activity as BaseActivity).showUnauthorisedDialog()

                }

                else -> it.message?.let { it1 -> requireContext().toast(it1) }
            }
        })
    }

    private fun generateBoardingList(i: Int) {
        if (boardingList != null) {
            boardingList?.add(stageDetails[i])
            setBoardingList(boardings = boardingList!!)
        }
    }

    private fun generateDroppingList(i: Int) {
        if (droppingList != null) {
            droppingList?.add(stageDetails[i])
            setDroppingList(droppings = droppingList!!)
        }

    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val returnOriginCityName =
                    result.data?.getStringExtra("CityOriginCityName").toString()
                val returnDestinationCityName =
                    result.data?.getStringExtra("selectedCityDestination").toString()
                val returnOriginCityID =
                    result.data?.getStringExtra("selectedCityIdOrigin")
                val returnDestinationCityID =
                    result.data?.getStringExtra("selectedCityIdDestination")
                val travel =
                    result.data?.getStringExtra("TravelSelection")

                if (travel.equals("ServiceId")) {
                    filteredServiceName = result.data?.getStringExtra("selected_service_name")
                    filteredServiceId = result.data?.getStringExtra("selected_service_id")
                    filteredResvId = result.data?.getStringExtra("selected_reservation_id")

                    if (::binding1.isInitialized) {
                        binding1.selectServiceTV.text = filteredServiceName
                    }


                } else {
                    filteredServiceName = ""
                    filteredServiceId = ""
                    filteredResvId = ""
                    if (::binding1.isInitialized) {
                        binding1.selectServiceTV.text = getString(R.string.all)
                    }
                    originData = returnOriginCityName
                    origindataID = returnOriginCityID!!
                    destinationData = returnDestinationCityName
                    destinationdataID = returnDestinationCityID!!
                    travelSelection = travel

                    Timber.d("originData- originData")


                    PreferenceUtils.setPreference("selectedCityOrigin", originData)
                    PreferenceUtils.setPreference("selectedCityDestination", destinationData)
                    if (origindataID == "0") {
                        origindataID = ""
                        finaldestinationId = destinationdataID
                        finaloriginID = origindataID

                        PreferenceUtils.setPreference(
                            "selectedCityIdDestination",
                            destinationdataID
                        )
                        PreferenceUtils.setPreference("selectedCityIdOrigin", origindataID)

                    } else if (destinationdataID == "0") {
                        destinationdataID = ""
                        finaldestinationId = destinationdataID
                        finaloriginID = origindataID

                        PreferenceUtils.setPreference(
                            "selectedCityIdDestination",
                            destinationdataID
                        )
                        PreferenceUtils.setPreference("selectedCityIdOrigin", origindataID)
                    } else if (destinationdataID == "0" && origindataID == "0") {
                        origindataID = ""
                        destinationdataID = ""
                        finaloriginID = origindataID
                        finaldestinationId = destinationdataID


                        PreferenceUtils.setPreference(
                            "selectedCityIdDestination",
                            destinationdataID
                        )
                        PreferenceUtils.setPreference("selectedCityIdOrigin", origindataID)
                    } else if (origindataID.isNotEmpty()) {
                        finaldestinationId = destinationdataID
                        finaloriginID = origindataID

                        PreferenceUtils.setPreference(
                            "selectedCityIdDestination",
                            destinationdataID
                        )
                        PreferenceUtils.setPreference("selectedCityIdOrigin", origindataID)
                    }
                    if (travelSelection == "OriginCity") {
                        //binding.childReservationBottomSheet.selectionFromCity.text = originData

                        if (::binding1.isInitialized) {
                            binding1.selectionFromCity.text = originData
                        }
                        if (temOrigin != finaloriginID) {
                            originChangeCheck = true
                        } else {
                            originChangeCheck = false
                        }
                    } else if (travelSelection == "DestinationCity") {
                        destinationChangeCheck = temDestination != finaldestinationId
                        //binding.childReservationBottomSheet.selectionToCity.text = destinationData

                        if (::binding1.isInitialized) {
                            binding1.selectionToCity.text = destinationData
                        }
                    }
                    validateButton(
                        originChangeCheck,
                        destinationChangeCheck,
                        activeCheck,
                        hubChangeCheck
                    )
                }
            }


        }

    private fun getDates() {
        // get next seven days date with current date

        pickUpChartViewModel.getNextCalenderDates(sevenDaysDate, travelDate)
        pickUpChartViewModel.listOfDates.observe(requireActivity(), Observer {
            dateList = it
            if (isAttachedToActivity()) {
                setDatesAdapter()
            }
        })

    }

    private fun callServiceApi() {
        val serviceDetailsRequest = ServiceDetailsRequest(
            bccId.toString(), service_details_method, format_type,
            com.bitla.ts.domain.pojo.service_details.request.ReqBody(
                resID.toString(),
                loginModelPref.api_key,
                operator_api_key,
                locale,
                origindataID,
                destinationdataID,
                json_format
            )
        )

        if (requireContext().isNetworkAvailable()) {
            sharedViewModel.getServiceDetails(
                resID.toString(),
                loginModelPref.api_key,
                origindataID,
                destinationdataID,
                operator_api_key,
                locale!!,
                service_details_method,
                excludePassengerDetails = false
            )

        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    @SuppressLint("SetTextI18n")
    private fun onClickListener() {

        binding.viewSummary.setOnClickListener {
            binding.progressViewSummary.visible()

            viewSummaryApi(ymdDate, finaloriginID, finaldestinationId, finalSelectedHubId)
        }

        binding.btnFilter.setOnClickListener {

            firebaseLogEvent(
                requireContext(),
                FILTERS_OPTIONS,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                FILTERS_OPTIONS,
                "Filter floating icon clicks - PickupCharts"
            )

            btnValidate = false
            bottomSheetDialoge = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
            binding1 = ReservationbottomSheetBinding.inflate(layoutInflater)
            bottomSheetDialoge?.setContentView(binding1.root)


            validateButton(
                originChangeCheck,
                destinationChangeCheck,
                true,
                hubChangeCheck
            )

            if (!filteredServiceName.isNullOrBlank()) {
                binding1.selectServiceTV.text = filteredServiceName
            }

            if (groupByHubs) {
                binding1.layoutSelectHubs.visible()
                binding1.etHubs.setText(sectedHubName)
            }

            pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
                if (privilegeResponse != null) {
                    privilegeResponse.let {
                        if (privilegeResponse.showPickupChartBasedOnHubsConfiguration == true) {
                            binding1.chkByHubService.visible()
                            binding1.selectServiceLL.gone()
                        } else {
                            binding1.chkByHubService.gone()
                            if (country.equals("india", true)) {
                                binding1.selectServiceLL.visible()
                            } else {
                                binding1.selectServiceLL.gone()
                            }
                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            }


            binding1.chkByHubService.isChecked = groupByHubs
            binding1.chkActiveService.isChecked = isActiveService

            val bottomSheetBehavior = BottomSheetBehavior.from((binding1.root.parent) as View)
            bottomSheetBehavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            binding1.chkByHubService.setOnCheckedChangeListener { compoundButton, b ->
                temIsHubs = b
                if (b) {
                    binding1.layoutSelectHubs.visible()
                    if (binding1.etHubs.text.isNullOrEmpty()) {
                        validateButton(false, false, false, false)
                    }
                } else {
                    binding1.layoutSelectHubs.gone()
                    validateButton(originChangeCheck, destinationChangeCheck, activeCheck, true)
                }

            }
            binding1.etHubs.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    spinnerHUbs
                )
            )


            binding1.etHubs.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    selectedHubId = spinnerHUbs[position].id

                    if (finalSelectedHubId == selectedHubId) {
                        hubChangeCheck = false
                    } else {
                        finalSelectedHubId = selectedHubId
                        sectedHubName = spinnerHUbs[position].value
                        hubChangeCheck = true
                    }
                    validateButton(
                        originChangeCheck,
                        destinationChangeCheck,
                        activeCheck,
                        hubChangeCheck
                    )
                }
            binding1.chkActiveService.setOnCheckedChangeListener { compoundButton, b ->

                activeCheck = isActiveService != b
                validateButton(
                    originChangeCheck,
                    destinationChangeCheck,
                    activeCheck,
                    hubChangeCheck
                )
            }
//            {
//
//                isactivetemp = !isactivetemp
//
//                if (isactivetemp == isActiveService) {
//                    if (finaloriginID != temOrigin || finaldestinationId != temDestination || temIsHubs != groupByHubs) {
//                        btnValidate = true
//                        validateButton(btnValidate)
//                    } else {
//                        btnValidate = false
//                        validateButton(btnValidate)
//                    }
//                } else {
//
//                    btnValidate = true
//                    validateButton(btnValidate)
//
//
//                }
//
//            }

            if (count == 0) {

                if (cityId == "") {
                    binding1.selectionFromCity.text = getString(R.string.all)
                    originData = ""
                    finaloriginID = cityId!!
                    PreferenceUtils.setPreference("selectedCityOrigin", originData)
                    PreferenceUtils.setPreference("selectedCityIdOrigin", cityId)
                    PreferenceUtils.setPreference("selectedCityDestination", destinationData)
                    PreferenceUtils.setPreference("selectedCityIdDestination", "")
                } else {
                    for (i in 0 until tempOriginId.size) {

                        if (tempOriginId[i] == cityId?.toInt()) {

                            binding1.selectionFromCity.text = tempOriginList[i]
                            originData = tempOriginList[i]
                            finaloriginID = cityId!!
                            PreferenceUtils.setPreference("selectedCityOrigin", originData)
                            PreferenceUtils.setPreference("selectedCityIdOrigin", cityId)
                            PreferenceUtils.setPreference(
                                "selectedCityDestination",
                                destinationData
                            )
                            PreferenceUtils.setPreference("selectedCityIdDestination", "")
                        }
                    }
                }

            } else {
                if (originData.isNullOrEmpty()) {
                    binding1.selectionFromCity.text = getString(R.string.all)
                } else {
                    binding1.selectionFromCity.text = originData
                }
                if (destinationData.isNullOrEmpty()) {
                    binding1.selectionToCity.text = getString(R.string.all)
                } else {
                    binding1.selectionToCity.text = destinationData
                }
            }



            binding1.selectionFromCity.setOnClickListener {
                PreferenceUtils.setPreference("TravelSelection", "OriginCity")
                resumeCall = false
                val intent = Intent(requireContext(), CityDetailsActivity::class.java)
                resultLauncher.launch(intent)
                count = 1
                firebaseLogEvent(
                    requireContext(),
                    FROM_CITY,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    FROM_CITY,
                    "from city click"
                )
//                PreferenceUtils.setPreference("selectedCityOrigin", originData)
//              PreferenceUtils.setPreference("selectedCityIdOrigin", cityId)
            }
            binding1.selectionToCity.setOnClickListener {
                PreferenceUtils.setPreference("TravelSelection", "DestinationCity")
                resumeCall = false
                val intent = Intent(requireContext(), CityDetailsActivity::class.java)
                resultLauncher.launch(intent)

                firebaseLogEvent(
                    requireContext(),
                    TO_CITY,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    TO_CITY,
                    "To city click"
                )
            }

            binding1.selectServiceTV.setOnClickListener {
                resumeCall = false
                PreferenceUtils.setPreference(PREF_TRAVEL_DATE,
                    convertedDate?.let { getDateDMY(it) })
                val intent = Intent(
                    activity,
                    SelectAllotedServiceActivity::class.java
                )
                var originID = ""
                var destID = ""
                if(!cityId.isNullOrBlank()){
                    originID = cityId?:""
                }

                if(destinationdataID != "0"){
                    destID = destinationdataID
                }
                if(origindataID != "0"){
                    originID = origindataID
                }


                intent.putExtra("selected_source_name", originData)
                intent.putExtra("selected_destination_name", destinationData)
                intent.putExtra("selected_origin_id", originID)
                intent.putExtra("selected_dest_id", destID)
                if(binding1.chkByHubService.isChecked){
                    intent.putExtra("is_group_by_hub",true)
                    intent.putExtra("selected_hub_id",finalSelectedHubId)
                }
                intent.putExtra("is_from_reservation_chart", true)
                resultLauncher.launch(intent)

            }

            binding1.tvCancel.setOnClickListener {

                if (cityId == "") {
                    binding1.selectionFromCity.text = getString(R.string.all)
                    originData = ""
                    finaloriginID = cityId!!
                    origindataID = cityId!!


                } else {
                    for (i in 0 until tempOriginId.size) {

                        if (tempOriginId[i] == cityId?.toInt()) {

                            binding1.selectionFromCity.text = tempOriginList[i]
                            originData = tempOriginList[i]
                            finaloriginID = cityId!!
                            origindataID = cityId!!
                            binding1.selectionFromCity.text = originData

                        }
                    }
                }

                filteredResvId = ""
                filteredServiceName = ""
                filteredServiceId = ""
                PreferenceUtils.setPreference(
                    PREF_RESERVATION_ID, 0L
                )
                binding1.selectServiceTV.text = context?.getString(R.string.all)
                destinationData = getString(R.string.all)
                destinationdataID = ""
                finaldestinationId = ""
                PreferenceUtils.setPreference("selectedCityOrigin", originData)
                PreferenceUtils.setPreference("selectedCityDestination", destinationData)
                PreferenceUtils.setPreference("selectedCityIdOrigin", origindataID)
                PreferenceUtils.setPreference("selectedCityIdDestination", destinationdataID)
                binding1.chkActiveService.isChecked = false
                binding1.chkByHubService.isChecked = false
                binding1.selectionToCity.text = getString(R.string.all)

                validateButton(
                    originChangeCheck,
                    destinationChangeCheck,
                    activeCheck,
                    hubChangeCheck
                )
            }
            binding1.btnApply.setOnClickListener {
                if (btnValidate) {
                    PAGE_NUMBER = 1


                    if (count == 0) {
                        groupByHubs = binding1.chkByHubService.isChecked
                        isActiveService = binding1.chkActiveService.isChecked

                        if (cityId == "") {
                            binding1.selectionFromCity.text = getString(R.string.all)
                            originData = ""
                            finaloriginID = cityId!!
                            PreferenceUtils.setPreference("selectedCityOrigin", originData)
                            PreferenceUtils.setPreference("selectedCityIdOrigin", cityId)
                            PreferenceUtils.setPreference(
                                "selectedCityDestination",
                                destinationData
                            )
                            PreferenceUtils.setPreference("selectedCityIdDestination", "")
                        } else {
                            for (i in 0 until tempOriginId.size) {
                                if (tempOriginId[i] == cityId?.toInt()) {

                                    binding1.selectionFromCity.text = tempOriginList[i]
                                    originData = tempOriginList[i]
                                    finaloriginID = cityId!!
                                    PreferenceUtils.setPreference("selectedCityOrigin", originData)
                                    PreferenceUtils.setPreference("selectedCityIdOrigin", cityId)
                                    PreferenceUtils.setPreference(
                                        "selectedCityDestination",
                                        destinationData
                                    )
                                    PreferenceUtils.setPreference("selectedCityIdDestination", "")
                                }
                            }
                        }

                        count = 1
                    } else {
                        groupByHubs = binding1.chkByHubService.isChecked
                        if (binding1.chkActiveService.isChecked) {
                            isActiveService = true
//                            isActiveService = binding1.chkActiveService.isChecked


                        } else {
                            isActiveService = false
                        }
                    }
                    binding.constraintLayout.gone()
                    recallAdapterHub = true
                    recallAdapter = true
                    tempServiceDirect.clear()
                    searchList1.clear()
                    serviceDirect.clear()
//                    viewSummary(ymdDate, finaloriginID, finaldestinationId,finalSelectedHubId )

                    allotedDirectService(
                        PAGE_NUMBER,
                        ymdDate,
                        finaloriginID,
                        finaldestinationId,
                        finalSelectedHubId
                    )
                    startShimmerEffect()
                    binding.btnFilter.visible()
                    binding.NoResult.gone()
                    binding.btnFilter.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_filter_selected,
                            null
                        )
                    )
                    binding.btnFilter.imageTintList =
                        ColorStateList.valueOf(requireContext().resources.getColor(R.color.color_03_review_02_moderate))
                    bottomSheetDialoge?.dismiss()

                } else {
                    Timber.d("invalidate button")
                }

                if (binding1.selectionFromCity.text == getString(R.string.all)) {
                    binding.allservice.text = getString(R.string.service_from)
                } else {
                    binding.allservice.text =
                        "${getString(R.string.service_from_city)} ${binding1.selectionFromCity.text}"
                }

                firebaseLogEvent(
                    requireContext(),
                    APPLY,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    APPLY,
                    "Apply btn"
                )
            }

            temOrigin = finaloriginID
            temDestination = finaldestinationId
            temIsHubs = groupByHubs

            if (country == "Indonesia") {
                binding1.layoutServices.visible()
                val serviceList = getServiceList()
                selectedServiceFilter = serviceList[serviceFilterPosition].id.toString()
                binding1.etServices.setText(serviceList[serviceFilterPosition].value)
                binding1.etServices.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_dropdown_item,
                        R.id.tvItem,
                        serviceList
                    )
                )
                binding1.etServices.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        selectedServiceFilter = serviceList[position].id.toString()
                        serviceFilterPosition = position
                    }
            } else {
                binding1.layoutServices.gone()
            }

            bottomSheetDialoge?.show()
        }


        binding.childReservationBottomSheet.selectionFromCityLayout.setOnClickListener {
            PreferenceUtils.setPreference("TravelSelection", "OriginCity")
            val intent = Intent(requireContext(), CityDetailsActivity::class.java)
            resultLauncher.launch(intent)
        }


        binding.childReservationBottomSheet.selectionToCityLayout.setOnClickListener {
            PreferenceUtils.setPreference("TravelSelection", "DestinationCity")
            val intent = Intent(requireContext(), CityDetailsActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun getServiceList(): MutableList<SpinnerItems> {
        val serviceList: MutableList<SpinnerItems> = mutableListOf()
        val allServices = SpinnerItems(1, getString(R.string.item_all_services))
        val onlyViaRoutes = SpinnerItems(2, getString(R.string.only_via_routes))
        val onlyMainSourceDestinationRoutes =
            SpinnerItems(3, getString(R.string.only_main_src_dest))
        serviceList.add(allServices)
        serviceList.add(onlyViaRoutes)
        serviceList.add(onlyMainSourceDestinationRoutes)
        return serviceList
    }

    private fun setDatesAdapter() {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvChatDates.layoutManager = layoutManager
        nextDateAdapter = NextDateAdapterReservation(requireActivity(), this, dateList)
        binding.rvChatDates.adapter = nextDateAdapter
    }

    private fun setMyBookingsAdapter(finallist: ArrayList<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>) {
        if (finallist.isEmpty()) {
            stopShimmerEffect()
            binding.NoResult.visible()
        } else {
                stopShimmerEffect()
                binding.constraintLayout.visible()
                binding.NoResult.gone()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

                binding.rvreservationPickup.layoutManager = WrapContentLinearLayoutManager(
                    linearLayoutManager = layoutManager,
                    context = requireContext()
                )

                myReservationAdapter = MyReservationAdapter(
                    context = requireContext(),
                    privilegeResponseModel = privilegeResponseModel,
                    onItemClickListener = this,
                    onclickitemMultiView = this,
                    searchList = finallist,
                    onItemPassData = this,
                    dialogButtonAnyDataListener = this,
                    enableCoachLevelReporting = privilegeResponseModel?.enableCoachLevelReporting ?: false,
                    serviceBlockReasonsList = blockReasonsList
                )

                binding.rvreservationPickup.adapter = myReservationAdapter
        }
    }

    private fun setMyBookingsByHubsAdapter(
        finallist: ArrayList<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>,
    ) {


        binding.NoResult.gone()

        if (finallist.isEmpty()) {
            stopShimmerEffect()
            binding.NoResult.visible()
        } else {
                stopShimmerEffect()
                binding.constraintLayout.visible()
                binding.NoResult.gone()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                binding.rvreservationPickup.layoutManager = layoutManager
                myReservationHubAdapter =
                    MyReservationHubsAdapter(
                        requireActivity(),privilegeResponseModel,
                        onItemClickListener = this,
                        onclickitemMultiView = this,
                        searchList = finallist,
                        onItemPassData = this,
                        enableCoachLevelReporting = privilegeResponseModel?.enableCoachLevelReporting ?: false,
                        serviceBlockReasonsList = blockReasonsList
                    )
                binding.rvreservationPickup.adapter = myReservationHubAdapter
                if (::myReservationAdapter.isInitialized) {
                    myReservationAdapter.notifyItemRangeRemoved(0, finallist.size)
            }
        }
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        cityId = loginModelPref.city_id
        Timber.d("cityId: $cityId")
        locale = PreferenceUtils.getlang()

//        if((activity as BaseActivity).getPrivilegeBase() != null) {
//            privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase() as PrivilegeResponseModel
//        }

        lifecycleScope.launch {
            val privilege = (activity as BaseActivity).getPrivilegeBaseSafely()
            pickUpChartViewModel.updatePrivileges(privilege)
        }

        pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
            privilegeResponse?.let {

                if (privilegeResponse.tsPrivileges?.allowServiceBlockingReasonsList == true) {
                    if (requireContext().isNetworkAvailable()) {
                        pickUpChartViewModel.getServiceBlockReasonsListApi(loginModelPref.api_key)
                    } else {
                        requireContext().noNetworkToast()
                    }
                    serviceBlockReasonsListObserver()
                }

                getDates()

                sevenDaysDate =
                    if (privilegeResponse.availableAppModes?.allowToShowPickupChartForPastDates == true) {
                        getYesterdayDate()
                    } else {
                        getTodayDate()
                    }

                if (privilegeResponse.isAgentLogin != null)
                    isAgentLogin = privilegeResponse.isAgentLogin == true

                val role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, requireContext())
                domain = PreferenceUtils.getPreference(
                    key = PREF_DOMAIN,
                    defautlValue = getString(R.string.empty)
                )!!

                cityId = if (role == getString(R.string.role_field_officer)) {
                    ""
                } else {
                    loginModelPref.city_id

                }
                finaloriginID = cityId ?: ""
                temOrigin = finaloriginID
                if(privilegeResponse.country!=null){
                country = privilegeResponse.country
                }

                if(!privilegeResponse.currency.isNullOrEmpty()){
                    currency = privilegeResponse.currency
                    currencyFormat =
                        getCurrencyFormat(requireContext(), privilegeResponse.currencyFormat)
                }

                if (!privilegeResponse.hubDetails.isNullOrEmpty()) {
                    hubsList?.addAll(privilegeResponse.hubDetails!!)
                }
                pinSize = privilegeResponse.pinCount ?: 6
                shouldBlockReservation = privilegeResponse.pinBasedActionPrivileges?.allowBlockServices ?: false

        //        hubsList?.add(HubDetails(1,"new"))
        //        hubsList?.add(HubDetails(5,"delhi"))
        //        hubsList?.add(HubDetails(3,"punjab"))
        //        hubsList?.add(HubDetails(4,"haryana"))

                if (!hubsList.isNullOrEmpty()) {
                    for (i in 0..hubsList?.size?.minus(1)!!) {
                        var lable = "null"
                        if (!hubsList!![i].label.isNullOrEmpty())
                            lable = hubsList!![i].label!!
                        val spinnerItems =
                            SpinnerItems(
                                hubsList!![i].id,
                                lable
                            )
                        spinnerHUbs.add(spinnerItems)
                    }

                }
            }
        }
    }

    private fun blockUnblockReservationApi(
        reason: String,
        reservationId: String,
        status: String,
        authPin: String,
        ) {
        if (requireContext().isNetworkAvailable()) {
            val blockUnblockRequest = BlockUnblockRequest(
                bccId.toString(),
                block_unblock_reservation_method_name,
                format_type,
                ReqBody(
                    loginModelPref.api_key,
                    reason,
                    reservationId,
                    status,
                    locale = locale,
                    authPin = authPin
                )
            )
            /*pickUpChartViewModel.blockUnblockAPI(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                blockUnblockRequest,
                block_unblock_reservation_method_name
            ) */

            val remarksInList = reason.split(",")
            val blockingReasonId = remarksInList[0]
            val remark = remarksInList.drop(1).joinToString(",")

            pickUpChartViewModel.blockUnblockAPI(
                ReqBody(
                    loginModelPref.api_key,
                    remark,
                    EncrypDecryp.getEncryptedValue(reservationId),
                    status,
                    locale = locale,
                    is_encrypted = EncrypDecryp.isEncrypted(),
                    authPin = authPin,
                    blockingReason = blockingReasonId
                ),
                block_unblock_reservation_method_name
            )
        } else requireContext().noNetworkToast()
    }

    private fun blockUnblockObserver(view: View, view2: View, view3: View, view4: View) {

        pickUpChartViewModel.blockUnblockReservationResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        requireContext().toast(it.message)
                        view as ImageView
                        view2 as TextView
                        view3 as TextView
                        view4 as CardView

                        if (view3.tag.toString()
                                .equals(requireContext().getString(R.string.inactive), true)
                        ) {
                            view3.text = requireContext().getString(R.string.inactive)
                            view3.setTextColor(requireContext().resources.getColor(R.color.blocked_tickets))

                            view4.setCardBackgroundColor(requireContext().resources.getColor(R.color.light_grey))

                        } else {
                            view3.text = requireContext().getString(R.string.active)

                            if (view2.text == requireContext().getString(R.string.locked)) {
                                view2.backgroundTintList = ColorStateList.valueOf(
                                    requireContext().resources.getColor(
                                        R.color.colorRed
                                    )
                                )
                                view2.isClickable = false
                                view2.setTextColor(requireContext().resources.getColor(R.color.white))


                            } else {
                                view2.backgroundTintList = ColorStateList.valueOf(
                                    requireContext().resources.getColor(
                                        R.color.primaryLight
                                    )
                                )
                                view2.isClickable = true
                                view2.setTextColor(requireContext().resources.getColor(R.color.colorPrimary))

                            }
                            view3.setTextColor(requireContext().resources.getColor(R.color.booked_tickets))
                            view4.setCardBackgroundColor(requireContext().resources.getColor(R.color.white))
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
                        it.message?.let { it1 -> requireContext().toast(it1) }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }


            Handler(Looper.getMainLooper()).postDelayed({
                binding.rvreservationPickup.visible()
                //this.startActivity(intent)
            }, 1000)

        }
    }


    private fun lockChartApi(
        reservationId: String,

        ) {
        val lockChartRequest = LockChartRequest(
            bccId.toString(),
            format_type,
            lock_chart_method_name,
            com.bitla.ts.domain.pojo.lock_chart.ReqBody(
                loginModelPref.api_key,
                reservationId,
                locale = locale
            )
        )
        /*pickUpChartViewModel.lockChartAPI(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            lockChartRequest,
            lock_chart_method_name
        )*/

        pickUpChartViewModel.lockChartAPI(
            com.bitla.ts.domain.pojo.lock_chart.ReqBody(
                loginModelPref.api_key,
                reservationId,
                locale = locale
            ),
            lock_chart_method_name
        )
    }

    private fun lockChartObserver(view: View) {

        pickUpChartViewModel.lockChartResponse.observe(viewLifecycleOwner) {

            if (it != null) {
                if (it.code == 200) {
                    if (it.message != null) {
                        it.message.let { it1 ->
                            requireContext().toast(it1)
                        }
                    }

                    view as TextView
                    view.backgroundTintList = ColorStateList.valueOf(
                        requireContext().resources.getColor(
                            R.color.colorRed
                        )
                    )
                    view.text = requireContext().getString(R.string.locked)
                    view.setTextColor(requireContext().resources.getColor(R.color.white))
                    view.isClickable = false
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }


    override fun onClickOfNavMenu(position: Int) {

    }

    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()
        if (provideRationale) {
            Snackbar.make(
                binding.root,
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(com.bitla.tscalender.R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            Timber.d("Request foreground only permission")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    private fun allotedDirectService(
        page_count: Int,
        travelDate: String,
        originID: String,
        destinationId: String,
        selectedHubId: Int?
    ) {
        var orId: String?
        var destId: String?
        var hubId: Int?
        if (originID.isNullOrEmpty())
            orId = ""
        else
            orId = originID
        if (destinationId.isNullOrEmpty())
            destId = ""
        else
            destId = destinationId

        if (selectedHubId != null)
            hubId = selectedHubId
        else
            hubId = null

        if (!groupByHubs)
            hubId = null

        val allotedDirectRequest = AllotedDirectRequest(
            groupByHubs,
            hubId,
            loginModelPref.api_key,
            travelDate,
            page_count,
            QUERY_PER_PAGE,
            "",
            true,
            orId,
            destId,
            locale,
            serviceFilter = selectedServiceFilter,
            res_id = filteredResvId
        )

        pickUpChartViewModel.allotedServiceApiDirect(
            allotedDirectRequest,
            lock_chart_method_name
        )
    }

    private fun allotedObserver() {
        pickUpChartViewModel.dataAllotedServiceDirect.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.refreshLayout.isRefreshing = false
                stopShimmerEffect()

                when (it.code) {
                    200 -> {
                        binding.constraintLayout.visible()
                        binding.relativeLayout5.visible()
                        binding.reservationProgressBar.gone()
                        maxPage = it.number_of_pages

                        if (!it.resp_hash.isNullOrEmpty()) {
                            recallAdapter = true
                            if (!it.resp_hash[0].hub_name.isNullOrEmpty()) {
                                binding.allservice.text = "(${it.resp_hash[0].hub_name})"
                            }
                            tempServiceDirect.addAll(it.resp_hash[0].services)
                            if (isActiveService) {
                                searchList1.clear()
                                for (i in 0..tempServiceDirect.size.minus(1)) {
                                    if (tempServiceDirect[i].status.toString()
                                            .equals(
                                                requireContext().getString(R.string.active),
                                                true
                                            )
                                    )
                                        if (!serviceDirect.contains(tempServiceDirect[i])) {
                                            serviceDirect.add(tempServiceDirect[i])
                                            tempnum += 1
                                        }
                                }
                            } else {
                                for (i in 0..tempServiceDirect.size.minus(1)) {
                                    if (!serviceDirect.contains(tempServiceDirect[i])) {
                                        serviceDirect.add(tempServiceDirect[i])
                                        tempnum += 1
                                    }
                                }
//                                    tempnum=serviceDirect.size

                            }
                            searchList1.addAll(serviceDirect)
                            if (::myReservationHubAdapter.isInitialized) {
                                if (recallAdapterHub) {
                                    setMyBookingsByHubsAdapter(serviceDirect)
                                    recallAdapterHub = false
                                } else {
                                    myReservationHubAdapter.notifyDataSetChanged()
                                }
                            } else {
                                setMyBookingsByHubsAdapter(serviceDirect)

                            }

                        } else {

                            recallAdapterHub = true
//                            binding.allservice.text = ""
                            if (it.services != null)
                                tempServiceDirect.addAll(it.services)
                            if (isActiveService) {
                                searchList1.clear()
                                for (i in 0..tempServiceDirect.size.minus(1)) {
                                    if (tempServiceDirect[i].status.toString()
                                            .equals(
                                                requireContext().getString(R.string.active),
                                                true
                                            )
                                    )
                                        if (!serviceDirect.contains(tempServiceDirect[i])) {
                                            serviceDirect.add(tempServiceDirect[i])
                                            tempnum += 1
                                        }
                                }
                            } else {
                                for (i in 0..tempServiceDirect.size.minus(1)) {
                                    if (!serviceDirect.contains(tempServiceDirect[i])) {
                                        serviceDirect.add(tempServiceDirect[i])
                                        tempnum += 1

                                    }
                                }

                            }
                            searchList1.addAll(serviceDirect)
                            if (::myReservationAdapter.isInitialized) {
                                if (recallAdapter) {
                                    setMyBookingsAdapter(serviceDirect)
                                    recallAdapter = false
                                } else {
                                    myReservationAdapter.notifyDataSetChanged()
                                }
                            } else {
                                setMyBookingsAdapter(searchList1)
                            }

                        }
                        stopShimmerEffect()
                        binding.rvreservationPickup.visible()

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
                        binding.reservationProgressBar.gone()


                        binding.NoResult.visible()
                        binding.rvreservationPickup.gone()
                        binding.noResultText.text = it.result?.message ?: ""
                        stopShimmerEffect()
                        binding.relativeLayout5.gone()
//                        it.result.message?.let { it1 -> requireContext().toast(it1) }
                    }
                }
            } else {
//                requireContext().toast(getString(R.string.server_error))
            }

        })
    }

    private fun viewSummaryApi(
        travelDate: String,
        originID: String,
        destinationId: String,
        selectedHubId: Int?
    ) {
        var orId: Int?
        var destId: Int?
        var hubId: Int?
        if (originID.isNullOrEmpty())
            orId = null
        else
            orId = originID.toInt()
        if (destinationId.isNullOrEmpty())
            destId = null
        else
            destId = destinationId.toInt()

        if (selectedHubId != null)
            hubId = selectedHubId
        else
            hubId = null

        if (!groupByHubs)
            hubId = null
        val viewSummaryRequest = ViewSummaryRequest(
            groupByHubs, hubId, loginModelPref.api_key, travelDate, true, true, orId, destId, locale
        )
        pickUpChartViewModel.viewSummaryApi(
            viewSummaryRequest,
            view_summary
        )
    }

    private fun viewSummaryObserver() {

        pickUpChartViewModel.viewSummaryDirect.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.progressViewSummary.gone()

                binding.refreshLayout.isRefreshing = false

                when (it.code) {
                    200 -> {
                        summary = it.view_summary
                        val bottomSheetDialoge =
                            BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
                        val binding = SheetSummaryBinding.inflate(layoutInflater)
                        bottomSheetDialoge.setContentView(binding.root)
                        val dayMonth = getNextDate2(ymdDate)
                        if (groupByHubs) {
                            binding.textHeading.text =
                                "${requireActivity().getString(R.string.summary)} ($sectedHubName $dayMonth)"
                        } else {
                            binding.textHeading.text =
                                "${requireActivity().getString(R.string.summary)} (${
                                    requireActivity().getString(R.string.all_services_on)
                                } $dayMonth)"
                        }
                        binding.summaryTotalBookedSeats.text = "${summary?.totalBookedSeats}"
                        binding.totalQuotaSeats.text = "${summary?.totalQuotaSeats}"
                        binding.totalRevenue.text = "${summary?.totalRevenue}"
                        binding.totalBlockedSeats.text = "${summary?.timeBlockedSeats}"
                        binding.extraSeatsBooked.text = "${summary?.extraSeatBooked}"
                        binding.summaryApi.text = "${getString(R.string.summary_api)} ${summary?.api}"
                        binding.summaryUserCnf.text = "${getString(R.string.summary_user_cnf)} ${summary?.userConf}"
                        binding.summaryBranchCnf.text = "${getString(R.string.summary_branch_cnf)} ${summary?.branchConf}"
                        binding.summaryOnlineagent.text = "${getString(R.string.summary_online_agent)} ${summary?.onlineAgent}"
                        binding.summaryOfflineAgent.text = "${getString(R.string.summary_offline_agent)} ${summary?.offlineAgent}"
                        binding.summaryOnline.text = "${getString(R.string.summary_online)} ${summary?.online}"
                        binding.summaryQuota.text = "${getString(R.string.summary_agent_quota)} ${summary?.quota}"
                        binding.summaryETicket.text = "${getString(R.string.summary_e_ticket)} ${summary?.eTicket}"
                        binding.summaryLadiesQuota.text = "${getString(R.string.summary_ladies_quota)} ${summary?.ladiesQuota}"
                        binding.summaryGentsQuota.text = "${getString(R.string.summary_gents_quota)}${summary?.gentsQuota}"
                        binding.summaryInJourney.text = "${getString(R.string.summary_in_journey)} ${summary?.inJourney}"
                        binding.extraSeatsBooked.text = "${summary?.extraSeatBooked} - ${
                            getString(
                                R.string.summary_extra_seat_booked
                            )
                        }"
                        binding.totalCount.text = "${summary?.availableSeats} ${getString(R.string.summary_seat_available)} ${
                            getString(
                                R.string.summary_occupancy
                            )
                        } ${summary?.occupancy}%"

                        binding.textHeading.setOnClickListener {
                            bottomSheetDialoge.dismiss()
                        }
                        bottomSheetDialoge.show()

                        firebaseLogEvent(
                            requireContext(),
                            VIEW_SUMMARY_CLICKS,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            VIEW_SUMMARY_CLICKS,
                            "View Summary Clicks"
                        )
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
                        requireContext().toast(getString(R.string.server_error))
                    }
                }
            } else {
                binding.progressViewSummary.gone()
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            if (view.tag == getString(R.string.edit)) {
                DialogUtils.updatePassengersDialog(requireActivity())
            }
            if (view.tag == "viewReservation") {
                resumeCall=false
                resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
                val result = Result()

                if (position in 0 until searchList1.size) {
                    result.bus_type = searchList1[position].busType ?: ""
                    result.number = searchList1[position].number
                    result.dep_time = searchList1[position].departureTime ?:""
                    //result.reservation_id = searchList1[position].reservationId!!.toLong()
                    result.reservation_id = resID ?: 0L
                    result.dep_date= searchList1[position].travelDate.toString()
                    result.bus_type=searchList1[position].busType.toString()
                    result.origin=searchList1[position].origin.toString()
                    result.destination=searchList1[position].destination.toString()

                    val intent = Intent(context, ViewReservationActivity::class.java)
                    intent.putExtra("pickUpResid", resID)
                    intent.putExtra("routeId",searchList1[position].routeId.toString())
                    intent.putExtra("deptTime",searchList1[position].departureTime.toString()+"T"+travelDate)

                    PreferenceUtils.apply {
                        putString("ViewReservation_date", "${ymdDate}")
                        setPreference("BUlK_shifting", false)
                        putString("BulkShiftBack", "")
                        setPreference("shiftPassenger_tab", 0)
                        setPreference("seatwiseFare", "fromBulkShiftPassenger")
                        putString("reservationid", resID.toString())
                        putObject(result, PREF_SELECTED_AVAILABLE_ROUTES)
                        setPreference(PREF_RESERVATION_ID, searchList1[position].reservationId.toString().toLong())
                    }

                    startActivity(intent)
                }

                else {
                    requireContext().toast(requireContext().getString(R.string.something_went_wrong))
                    Timber.e("Index $position out of bounds for searchList1 size: ${searchList1.size}")
                }
            }

            if(view.tag == "viewCoachLayout") {
                resumeCall=false
                val intent = Intent(context, CoachLayoutReportingActivity::class.java)
                PreferenceUtils.removeKey("seatwiseFare")
                intent.putExtra("reservationId", searchList1[position].reservationId.toString())
                intent.putExtra("originId", searchList1[position].originId.toString())
                intent.putExtra("destinationId", searchList1[position].destinationId.toString())
                intent.putExtra("origin", searchList1[position].origin.toString())
                intent.putExtra("destination", searchList1[position].destination.toString())
                intent.putExtra("busType", searchList1[position].busType.toString())
                intent.putExtra("travelDate", searchList1[position].travelDate.toString())
                startActivity(intent)
            }

            if (view.tag == "Mapp") {
                if (!searchList1[position].coachNumber.isNullOrEmpty()) {
                    var title = ""
                    var text = ""

                    if (foregroundPermissionApproved()) {
                        foregroundOnlyLocationService?.subscribeToLocationUpdates()
                            ?: Timber.d("Service Not Bound")
                        PreferenceUtils.putString(PREF_MAP_COACH, searchList1[position].coachNumber)
                        val intent = Intent(context, MapActivity::class.java)
                        startActivity(intent)

                    } else {
                        if (popUpText.equals("")) {
                            requestForegroundPermissions()
                        } else {
//                            requireContext().toast(popUpText)
                            if (popUpText.contains(":")) {
                                if (locale.equals("id", true)) {
                                    if (popUpText.contains(",")) {
                                        title = popUpText.split(",")[1].split(":")[1]
                                        text = popUpText.split(",")[1].split(":")[2]
                                        val text2 = text.replace("\"}", "")
                                        val title2 = title.replace("\"", "")
                                        DialogUtils.twoButtonDialog(
                                            requireContext(),
                                            title2,
                                            text2,
                                            getString(R.string.cancel),
                                            getString(R.string.okay),
                                            this
                                        )
                                    }
                                } else {
                                    title = popUpText.split(":")[1]
                                    text = popUpText.split(":")[2]
                                    val text2 = text.split(",")[0]
                                    val text3 = text2.replace("\"", "")
                                    val title2 = title.replace("\"", "")
                                    DialogUtils.twoButtonDialog(
                                        requireContext(),
                                        title2,
                                        text3,
                                        getString(R.string.cancel),
                                        getString(R.string.okay),
                                        this
                                    )
                                }
                            }
                        }
                    }

                } else
                    requireContext().toast(getString(R.string.coach_warning))
            }
//            else{
//                    if (PreferenceUtils.getString("mapRoute")== searchList1[position].coachNumber) {
//                        if (!searchList1[position].coachNumber.isNullOrEmpty()) {
//
//                            if (foregroundPermissionApproved()) {
//                                foregroundOnlyLocationService?.subscribeToLocationUpdates()
//                                    ?: Timber.d("Service Not Bound")
//                                PreferenceUtils.putString(PREF_MAP_COACH, searchList1[position].coachNumber)
//                                val intent = Intent(context, MapActivity::class.java)
//                                startActivity(intent)
//
//                            } else {
//                                requestForegroundPermissions()
//                            }
//
//                        }else
//                            requireContext().toast(getString(R.string.coach_warning))
//                    }else{
//                        requireContext().toast("Service is already running, please stop it first")
//                    }
//                }
//            }
            if (view.tag == resources.getString(R.string.open_calender)) {

                pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
                    if (privilegeResponse != null) {
                        if (privilegeResponse.availableAppModes?.allowToShowPickupChartForPastDates == true) {
                            SlyCalendarDialog()
                                .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                                .setMinDate(stringToDate("1970-01-01", DATE_FORMAT_D_M_Y))
                                .setSingle(true)
                                .setFirstMonday(false)
                                .setCallback(this)
                                .show(requireFragmentManager(), view.tag.toString())
                        } else {
                            SlyCalendarDialog()
                                .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                                .setMinDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                                .setSingle(true)
                                .setFirstMonday(false)
                                .setCallback(this)
                                .show(requireFragmentManager(), view.tag.toString())
                        }
                    } else {
                        requireContext().toast(requireContext().getString(R.string.server_error))
                    }
                }
            }
            if (view.tag == "DATES") {
                ymdDate = inputFormatToOutput(
                    dateList[position].title,
                    DATE_FORMAT_MMM_DD_EEE_YYYY,
                    DATE_FORMAT_Y_M_D
                ).replace("1970", getCurrentYear())
                hub.clear()
                hubservice.clear()
                travelDate = getDateDMY(ymdDate)!!
                tempServiceDirect.clear()
                searchList1.clear()
                recallAdapter = true
                recallAdapterHub = true
                serviceDirect.clear()
                PAGE_NUMBER = 1
                startShimmerEffect()
                allotedDirectService(
                    PAGE_NUMBER,
                    ymdDate,
                    finaloriginID,
                    finaldestinationId,
                    finalSelectedHubId
                )

            }
            if (view.tag == "yes") {
                val bottomSheetDialoge =
                    BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
                val binding = SheetSummaryBinding.inflate(layoutInflater)
                bottomSheetDialoge.setContentView(binding.root)

                binding.apply {
                    textHeading.text = "${getString(R.string.summary)}(${hub[position].hubName})"
                    summaryTotalBookedSeats.text = "${hubSummary[position].totalBookedSeats}"
                    totalQuotaSeats.text = "${hubSummary[position].totalQuotaSeats}"
                    totalRevenue.text = "${hubSummary[position].totalRevenue}"
                    totalBlockedSeats.text = "${hubSummary[position].timeBlockedSeats}"
                    extraSeatsBooked.text = "${hubSummary[position].extraSeatBooked}"

                    summaryApi.text = "${getString(R.string.summary_api)} ${hubSummary[position].api}"
                    summaryUserCnf.text = "${getString(R.string.summary_user_cnf)} ${hubSummary[position].userConf}"
                    summaryBranchCnf.text = "${getString(R.string.summary_branch_cnf)} ${hubSummary[position].branchConf}"
                    summaryOnlineagent.text = "${getString(R.string.summary_online_agent)} ${hubSummary[position].onlineAgent}"
                    summaryOfflineAgent.text = "${getString(R.string.summary_offline_agent)} ${hubSummary[position].offlineAgent}"
                    summaryOnline.text = "${getString(R.string.summary_online)} ${hubSummary[position].online}"
                    summaryQuota.text = "${getString(R.string.summary_quota)} ${hubSummary[position].quota}"
                    summaryETicket.text = "${getString(R.string.summary_e_ticket)} ${hubSummary[position].eTicket}"
                    summaryLadiesQuota.text = "${getString(R.string.summary_ladies_quota)} ${hubSummary[position].ladiesQuota}"
                    summaryGentsQuota.text = "${getString(R.string.summary_gents_quota)}${hubSummary[position].gentsQuota}"
                    summaryInJourney.text = "${getString(R.string.summary_in_journey)} ${hubSummary[position].inJourney}"
                    extraSeatsBooked.text = "${hubSummary[position].extraSeatBooked} - ${
                        getString(
                            R.string.summary_extra_seat_booked
                        )
                    }"
                    totalCount.text = "${hubSummary[position].availableSeats} ${
                        getString(
                            R.string.summary_seat_available
                        )
                    } ${getString(R.string.summary_occupancy)} ${hubSummary[position].occupancy}%"

                    textHeading.setOnClickListener {
                        bottomSheetDialoge.dismiss()
                    }
                }

                bottomSheetDialoge.show()
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }


    override fun onClickOfItem(data: String, position: Int) {
        if (data != null) {
            if (data == getString(R.string.edit_chart)) {
                originData = searchList1[position].origin!!
                destinationData = searchList1[position].destination.toString()
                origindataID = searchList1[position].originId.toString()
                destinationdataID = searchList1[position].destinationId.toString()
                resID = searchList1[position].reservationId
                travelDate = searchList1[position].travelDate.toString()

                PreferenceUtils.apply {
                    setPreference("selectedCityOrigin", originData)
                    setPreference("selectedCityDestination", destinationData)
                    setPreference("selectedCityIdOrigin", origindataID)
                    setPreference("selectedCityIdDestination", destinationdataID)

                    putString(PREF_SOURCE, originData)
                    putString(PREF_DESTINATION, destinationData)
                    putString(PREF_SOURCE_ID, origindataID)
                    putString(PREF_DESTINATION_ID, destinationdataID)
                    putString(PREF_RESERVATION_ID, resID.toString())
                    putString(PREF_TRAVEL_DATE, getDateDMY(travelDate))
                    putObject(searchList1[position], PREF_SELECTED_AVAILABLE_ROUTES)
                }
                callServiceApi()
            }

        }
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }


    override fun onClick(v: View?) {

    }

    override fun onCancelled() {

    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?,
        hours: Int,
        minutes: Int,
    ) {
        if (firstDate != null) {
            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours)
                firstDate.set(Calendar.MINUTE, minutes)

                travelDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)
                sevenDaysDate = travelDate
                pickUpChartViewModel.getNextCalenderDates(sevenDaysDate, travelDate)

                ymdDate = inputFormatToOutput(
                    travelDate,
                    DATE_FORMAT_D_M_Y,
                    DATE_FORMAT_Y_M_D
                )
                tempServiceDirect.clear()
                searchList1.clear()
                recallAdapter = true
                recallAdapterHub = true
                serviceDirect.clear()
                PAGE_NUMBER = 1
                startShimmerEffect()
                allotedDirectService(
                    PAGE_NUMBER,
                    ymdDate,
                    finaloriginID,
                    finaldestinationId,
                    finalSelectedHubId
                )
            }
        }
    }

    override fun onLeftButtonClick(tag: View?) {
    }

    override fun onRightButtonClick(tag: View?) {
        tag as TextView
        if (requireContext().isNetworkAvailable()) {
            lockChartApi(lockres.toString())
            lockChartObserver(tag)
        } else requireContext().noNetworkToast()

    }


    override fun onLeftButtonClick() {
        Timber.d("cancel")
    }

    override fun onRightButtonClick() {
        requestForegroundPermissions()
    }


    private fun callCityDetailsApi() {
        if (requireContext().isNetworkAvailable()) {
            val cityDetailRequest = CityDetailRequest(
                bccId.toString(),
                city_Details_method_name,
                format_type,
                com.bitla.ts.domain.pojo.city_details.request.ReqBody(
                    loginModelPref.api_key,
                    response_format,
                    locale = locale
                )
            )
            /* cityDetailViewModel.cityDetailAPI(
                 loginModelPref.auth_token,
                 loginModelPref.api_key,
                 cityDetailRequest,
                 city_Details_method_name
             )*/

            cityDetailViewModel.cityDetailAPI(
                loginModelPref.api_key,
                response_format,
                locale!!,
                city_Details_method_name
            )
        } else requireContext().noNetworkToast()
    }

    private fun setCityDetailsObserver() {
        cityDetailViewModel.cityDetailResponse.observe(requireActivity()) {
            if (it != null) {
                if (it.code == 200) {
                    if (it.result != null && it.result.isNotEmpty()) {
                        for (i in 0..it.result.size.minus(1)) {
                            tempOriginId.add(it.result[i].id)
                            it.result[i].name?.let { it1 -> tempOriginList.add(it1) }
                        }
                        if (cityId == "") {
                            binding.allservice.text = getString(R.string.service_from)
                        } else if (tempOriginId.contains(cityId!!.toInt())) {
                            for (i in 0 until tempOriginId.size) {
                                if (tempOriginId[i] == cityId?.toInt()) {
                                    originData = tempOriginList[i]
                                    binding.allservice.text =
                                        "${getString(R.string.service_from_city)} ${originData}"
                                }
                            }
                        } else {
                            cityId = ""
                            finaloriginID = cityId!!
                        }


                    }
                    allotedDirectService(
                        PAGE_NUMBER,
                        ymdDate,
                        finaloriginID,
                        finaldestinationId,
                        finalSelectedHubId
                    )
                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        requireContext(),
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/

                    (activity as BaseActivity).showUnauthorisedDialog()

                } else {
                    cityId = ""
                    finaloriginID = cityId!!
                    allotedDirectService(
                        PAGE_NUMBER,
                        ymdDate,
                        finaloriginID,
                        finaldestinationId,
                        finalSelectedHubId
                    )
                }
            } else {
                cityId = ""
                finaloriginID = cityId!!
                allotedDirectService(
                    PAGE_NUMBER,
                    ymdDate,
                    finaloriginID,
                    finaldestinationId,
                    finalSelectedHubId
                )
                requireActivity().isActivityIsLive {

                    requireContext().toast(getString(R.string.server_error))
                }
            }

        }
    }

    private fun initRefreshListner() {
        binding.refreshLayout.setOnRefreshListener {
            tempServiceDirect.clear()
            searchList1.clear()
            serviceDirect.clear()
            PAGE_NUMBER = 1
            startShimmerEffect()

            if (::myReservationAdapter.isInitialized) {
                val itemCount = myReservationAdapter.itemCount
                myReservationAdapter.notifyItemRangeRemoved(0, itemCount)
            }

            allotedDirectService(
                page_count = PAGE_NUMBER,
                travelDate = ymdDate,
                originID = finaloriginID,
                destinationId = finaldestinationId,
                selectedHubId = finalSelectedHubId
            )
            binding.NoResult.gone()

        }

    }


    private fun startShimmerEffect() {
        binding.relativeLayout5.gone()
        binding.rvreservationPickup.gone()
        binding.shimmerLayout.visible()
        binding.shimmerLayout.startShimmer()


    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.shimmerLayout.gone()

        if (binding.shimmerLayout.isShimmerStarted) {
            binding.shimmerLayout.stopShimmer()

        }
    }

    private fun validateButton(
        originChange: Boolean,
        destinationChange: Boolean,
        activeCheck: Boolean,
        hubChangeCheck: Boolean
    ) {
        Timber.d("valiidate: $originChange, $destinationChange, $activeCheck, $hubChangeCheck")
        if (originChange || destinationChange || activeCheck || hubChangeCheck) {

            if (::binding1.isInitialized) {
                binding1.btnApply.isEnabled = true
                btnValidate = true
                binding1.btnApply.backgroundTintList = ColorStateList.valueOf(
                    requireContext().resources.getColor(
                        R.color.colorPrimary
                    )
                )
            }

        } else {
            if (::binding1.isInitialized) {
                binding1.btnApply.isEnabled = false
                binding1.btnApply.backgroundTintList = ColorStateList.valueOf(
                    requireContext().resources.getColor(
                        R.color.button_default_color
                    )
                )
                btnValidate = false
            }
        }
    }


    override fun onClickMuliView(
        view: View,
        view2: View,
        view3: View,
        view4: View,
        resID: String,
        remarks: String
    ) {


        if (view.tag == "Right") {
            if (view3.tag.toString().equals(requireContext().getString(R.string.inactive), true)) {
                if (shouldBlockReservation && country.equals("india", true)) {
                    DialogUtils.showFullHeightPinInputBottomSheet(
                        activity = requireActivity(),
                        fragmentManager = childFragmentManager,
                        pinSize = pinSize,
                        getString(R.string.block_unblock_reservation),
                        onPinSubmitted = { pin: String ->
                            blockUnblockReservationApi(remarks, resID, "Block", pin)
                        },
                        onDismiss = null
                    )
                } else {
                    blockUnblockReservationApi(remarks, resID, "Block", "")
                }
                blockUnblockObserver(view, view2, view3, view4)
            } else {
                if (shouldBlockReservation && country.equals("india", true)) {
                    DialogUtils.showFullHeightPinInputBottomSheet(
                        activity = requireActivity(),
                        fragmentManager = childFragmentManager,
                        pinSize = pinSize,
                        getString(R.string.block_unblock_reservation),
                        onPinSubmitted = { pin: String ->
                            blockUnblockReservationApi(remarks, resID, "Allow", pin)
                        },
                        onDismiss = null
                    )
                } else {
                    blockUnblockReservationApi(remarks, resID, "Allow", "")
                }
                blockUnblockObserver(view, view2, view3, view4)
            }
        }


    }

    override fun onClickAdditionalData(view0: View, view1: View) {

    }

    override fun onItemData(view: View, str1: String, str2: String) {

        lockres = str2.toInt()
        val str1 = str1.toInt()
        DialogUtils.lockChartDialog(
            requireContext(),
            getString(R.string.lock_chart) + "?",
            getString(R.string.lock_chart_message),
            "${serviceDirect[str1].number} ${serviceDirect[str1].origin}- ${serviceDirect[str1].destination}",
            "${serviceDirect[str1].travelDate}| ${serviceDirect[str1].departureTime}- ${serviceDirect[str1].arrivalTime}| ${serviceDirect[str1].busType}",
            getString(R.string.goBack),
            getString(R.string.proceed_to_lock),
            this,
            view
        )


    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {

    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }


    /* private fun viewSummary(
         travelDate: String,
         originID: String,
         destinationId: String,
         selectedHubId: Int?
     ) {
         var orId:Int?
         var destId:Int?
         var hubId:Int?


         val gson = GsonBuilder()
             .setLenient()
             .create()

         val retrofit: Retrofit = Retrofit.Builder()
             .baseUrl("http://$domain/")
             //.addConverterFactory(ScalarsConverterFactory.create())
             .addConverterFactory(GsonConverterFactory.create(gson))
             .build()


         val apiInterface: ApiInterface = retrofit.create(ApiInterface::class.java)
         if (originID.isNullOrEmpty())
             orId= null
         else
             orId= originID.toInt()
         if (destinationId.isNullOrEmpty())
             destId= null
         else
             destId= destinationId.toInt()

         if (selectedHubId != null)
             hubId= selectedHubId
         else
             hubId= null

         if (!groupByHubs)
             hubId= null

         val viewSuaryCall = apiInterface.viewSummaryApi(
             groupByHubs,hubId,loginModelPref.api_key,travelDate,true,true,orId, destId,locale
         )
         val call:Call<ViewSummaryResonse> =viewSuaryCall

         Timber.d("okhttp ViewSummaryRequest ${call.request()}")

         call.enqueue(object :Callback<ViewSummaryResonse>{
             override fun onResponse(
                 call: Call<ViewSummaryResonse>,
                 response: Response<ViewSummaryResonse>
             ) {
                 if (response !=null){
                     if (response.body() != null){
                         binding.refreshLayout.isRefreshing = false
                         if (response.body()!!.code== 200){
                             summary= response.body()!!.view_summary
                         }else{
                                if(response.body()!!.result?.message != null)
                                {
                                    requireContext().toast(response.body()!!.result?.message)
                                }
                         }

                     }


                 }


             }

             override fun onFailure(call: Call<ViewSummaryResonse>, t: Throwable) {
                 Timber.d("directApiCall:1: ${t.message}")
             }

         })

 //        ApiRepo.callRetrofit(directionCall, this, directionUrl, this, progress_bar, this)
     }*/


    private fun locationPopUpCheck() {
        RemoteConfigUpdateHelper.with(requireContext()).LocationPopUpListner(this).check()
    }

    override fun locationPop(loationPopString: String?) {
        if (!loationPopString.isNullOrEmpty()) {
            popUpText = loationPopString
        } else {
            popUpText = ""
        }
    }

    override fun onDataSend(type: Int, file: Any) {

    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {


    }

    private fun serviceBlockReasonsListObserver() {
        pickUpChartViewModel.serviceBlockReasonsListResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (!it.reasons.isNullOrEmpty()) {
                            blockReasonsList = it.reasons
                        }
                    }

                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }

                    else -> {
                        it.message.let { it1 -> requireContext().toast(it1) }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }
}



