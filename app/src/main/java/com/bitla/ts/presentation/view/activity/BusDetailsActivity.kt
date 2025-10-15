package com.bitla.ts.presentation.view.activity

//import com.skydoves.balloon.*
import android.annotation.*
import android.app.*
import android.content.*
import android.content.res.*
import android.os.*
import android.text.*
import android.util.Log
import android.view.*
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.compose.Visibility
import androidx.core.content.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.block_unblock_reservation.ReasonList
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.delete_recent_search.request.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.destination_pair.Destination
import com.bitla.ts.domain.pojo.destination_pair.Origin
import com.bitla.ts.domain.pojo.filter_model.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.single_block_unblock.single_block_unblock_request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.view.dashboard.*
import com.bitla.ts.presentation.view.fragments.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.constants.NewBookings.New_BOOKING_SRP
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getBccId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getDestination
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getDestinationId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLogin
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getSource
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getSourceId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getTravelDate
import com.bitla.ts.utils.sharedPref.PreferenceUtils.putBoarding
import com.bitla.ts.utils.sharedPref.PreferenceUtils.putDropping
import com.bitla.ts.utils.sharedPref.PreferenceUtils.removeKey
import com.bitla.tscalender.*
import com.google.firebase.analytics.*
import gone
import invisible
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*


class BusDetailsActivity : BaseActivity(), OnItemClickListener, SlyCalendarDialog.Callback,
    VarArgListener, View.OnTouchListener, RadioGroup.OnCheckedChangeListener,
    DialogSingleButtonListener, DialogButtonAnyDataListener {

    companion object {
        val TAG = BusDetailsActivity::class.java.simpleName
    }


    private var sourcePopupWindow: PopupWindow? = null
    private var destinationPopupWindow: PopupWindow? = null
    private var excludePassengerDetails: Boolean = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var sevenDaysDate: String = getTodayDate()
    private var serviceApiType: String? = null
    private val showOnlyAvailableServices: String = "false" //fixed
    private val showInJourneyServices: String = "true" // fixed
    private var serviceNumber: String = ""
    private var busType: String? = null
    private var depTime: String? = null
    private lateinit var rapidBookingDialog: AlertDialog
    private var boardingPoint: String = ""
    private var droppingPoint: String = ""
    private lateinit var droppingStageDetail: StageDetail
    private lateinit var boardingStageDetail: StageDetail

    private var stageDetails = mutableListOf<StageDetail>()
    private var boardingList: MutableList<StageDetail>? = null
    private var droppingList: MutableList<StageDetail>? = null
    private var resId: Long? = null
    private var selectedOriginId: String = "0"
    private var selectedDestinationId: String = "0"
    private lateinit var busStageData: MutableList<StageData>
    private var dateList = mutableListOf<StageData>()
    private var availableRoutesList = mutableListOf<Result>()
    lateinit var context: Context

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var ymdDate: String = ""
    private var selectedDateType: String = ""
    private val availableRoutesViewModel by viewModel<AvailableRoutesViewModel<Any?>>()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private lateinit var binding: LayoutBusDetailsBinding
    private var busList: ArrayList<BusFilterModel> = arrayListOf()
    private var selectedBusType = ArrayList<String>()
    private var filteredList = mutableListOf<Result>()
    private var listForFilter = mutableListOf<Result>()
    private var updatedList = mutableListOf<Result>()
    private var individualList: MutableList<Result>? = null

    private lateinit var searchListAdapter: BusDetailsAdapter

    private var sourceNewAdapter: SimpleListAdapter? = null
    private var destinationNewAdapter: SimpleListAdapter? = null


    private var resID: Long? = 0L

    //private var isAllowBookingAfterTravelDate = false
    private var bookingAfterDoj: Int = 0
    private var isAgentLogin: Boolean = false
    private var currency = ""
    private var currencyFormat = ""
    private var locale: String? = ""

    private var bpDpBoarding: MutableList<BoardingPointDetail> = mutableListOf()
    private var bpDpDropping: MutableList<DropOffDetail> = mutableListOf()
    var isApplybdDpRapid = false
    var isApplybpDpEditChart = 0
    var noOfTickets = ""
    var emptyBoarding: BoardingPointDetail = BoardingPointDetail("", "", "", "", "", "")
    var emptyDropping: DropOffDetail = DropOffDetail("", "", "", "", "", "")
    lateinit var oldBoarding: BoardingPointDetail
    lateinit var oldDroping: DropOffDetail
    private var isMotAllowed = false
    private lateinit var interDestinationList: MutableList<Destination>
    private var selectedOrigin = ""

    private var finalSourceId = ""
    private var finalDestinationId = ""
    private var services: String = ""


    //    on service selection
    private var serviceBusType: String? = ""
    private var serviceDepTime: String? = ""
    private var service_ServiceNUmber: String? = ""
    private var service_title: String? = ""
    private var serviceType: String? = ""

    private var resultList: MutableList<com.bitla.ts.domain.pojo.destination_pair.Result> =
        mutableListOf()
    private lateinit var originList: MutableList<Origin>

    private var convertedDate: String? = null
    private var returnDate: String = ""
    private var convertedReturnDate: String? = null
    private var isFromModifyDatePopup: Boolean? = true
    private var isFirstDateSelected: Boolean? = false
//    private var privilegeResponse: PrivilegeResponseModel? = null
    private var destinationPairModel: DestinationPairModel? = null
    private lateinit var destinationList: MutableList<Destination>
    private var rapidBookingItemPosition = 0
    private var pinSize = 0
    private var shouldSingleBlockUnblock = false
    private var isPickupDropoffChargesEnabled: Boolean? = false

    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var blockReasonsList = mutableListOf<ReasonList>()

    private var pagination: Boolean = true
    private var perPage: Int = 20
    private var pageNumber: Int = 1
    private var totalPage: Int = 1

    override fun onResume() {
        super.onResume()
        setDateLocale(PreferenceUtils.getlang(), this@BusDetailsActivity)
        if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
            droppingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)!!
            droppingPoint = droppingStageDetail.name!!
        }
        if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
            boardingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)!!
            boardingPoint = boardingStageDetail.name!!
        }

        if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
            rapidBookingItemPosition =
                PreferenceUtils.getPreference(getString(R.string.rapid_booking_item_position), 0)!!
        }


        val toolbarTextList = binding.toolbarHeaderText.text.toString().split("-")
        val isAllSelected = toolbarTextList.any { it.trim().equals("All", true) }
        if (isAllSelected) {
            PreferenceUtils.setPreference(PREF_SOURCE, PreferenceUtils.getPreference("AllToAllSource",""))
            PreferenceUtils.setPreference(PREF_SOURCE_ID,  PreferenceUtils.getPreference("AllToAllSourceId",""))
            PreferenceUtils.setPreference(PREF_DESTINATION,PreferenceUtils.getPreference("AllToAllDestination",""))
            PreferenceUtils.setPreference(PREF_DESTINATION_ID, PreferenceUtils.getPreference("AllToAllDestinationId",""))
            sourceId=PreferenceUtils.getPreference("AllToAllSourceId","").toString()
            destinationId=PreferenceUtils.getPreference("AllToAllDestinationId","").toString()
            binding.toolbarHeaderText.setCompoundDrawables(null,null,null,null)

        }

        if (boardingPoint.isNotEmpty() && droppingPoint.isNotEmpty() && ::rapidBookingDialog.isInitialized && rapidBookingDialog.isShowing) {
            rapidBookingDialog.cancel()

            rapidBookingDialog = if (isApplybpDpEditChart == 0) {
                DialogUtils.editChartBpDp(
                    boardingPoint = boardingPoint,
                    droppingPoint = droppingPoint,
                    this,
                    this
                )!!
            } else {
                DialogUtils.rapidBookingDialog(
                    boardingPoint = boardingPoint,
                    droppingPoint = droppingPoint,
                    position = rapidBookingItemPosition,
                    context = this,
                    varArgListener = this
                )!!
            }
        }





        if (isNetworkAvailable()) {
            if(sourceId=="0" || destinationId=="0"){
                pageNumber=1
                allToAllavailableRoutesApi()
            }else{
            availableRoutesApi()}
        } else
            noNetworkToast()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@BusDetailsActivity

        getPrefs()
        binding.rvBusDetails.gone()
        binding.rvDateDetails.gone()



        PreferenceUtils.removeKey(PREF_PICKUP_DROPOFF_CHARGES_ENABLED)

        if (source.isNullOrEmpty() && destination.isNullOrEmpty()) {
            val intent = intent
            source = intent.getStringExtra(getString(R.string.last_searched_source))
            destination = intent.getStringExtra(getString(R.string.last_searched_destination))
        } else {
            sourceId = getSourceId()
            destinationId = getDestinationId()
        }

        // setAdapters()

        setToolbarTitle()

        if (travelDate.isNotEmpty()) {
            ymdDate = getDateYMD(travelDate)

        }

        swipeRefreshLayout()
        setObserver()

        sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

            if (privilegeResponse?.isChileApp == true || privilegeResponse?.country.equals(
                    "indonesia",
                    true
                )
            ) {
                if (privilegeResponse?.country.equals("indonesia", true))
                    binding.rgAcNonAc.gone() else binding.rgAcNonAc.visible()
                binding.rgSleeperSeater.gone()
            } else {
                binding.rgSleeperSeater.visible()
            }
        }

        setFilterVisibility()

    }

    private fun setFilterVisibility() {
        sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->
            if (!privilegeResponse?.country.equals("India", true)
            ) {
                binding.rgAcNonAc.gone()
                binding.rgSleeperSeater.gone()

            } else {
                binding.rgAcNonAc.visible()
                binding.rgSleeperSeater.visible()



            }
        }


    }

    override fun isInternetOnCallApisAndInitUI() {
        if(sourceId=="0"||destinationId=="0"){
            pageNumber=1
            allToAllavailableRoutesApi()
        }else{
            availableRoutesApi()
        }
    }

    private fun callBpDpServiceApi(boarding: String, dropping: String) {
        binding.includeProgress.progressBar.visible()

        if (isNetworkAvailable()) {
            sharedViewModel.getBpDpServiceDetails(
                reservationId = resId.toString(),
                apiKey = loginModelPref.api_key,
                origin = sourceId,
                destinationId = destinationId,
                operator_api_key = operator_api_key,
                locale = "$locale",
                apiType = service_details_method,
                boardingAt = boarding,
                dropOff = dropping
            )
        } else {
            noNetworkToast()
        }
    }

    private fun callServiceApi(tempSourceId:String="",tempDestination: String="") {
        var mainOriginId=sourceId
        var mainDestinationId=destinationId

        if(tempSourceId.isNotEmpty()){
            mainOriginId=tempSourceId
        }
        if(tempDestination.isNotEmpty()){
            mainDestinationId=tempDestination
        }
        binding.includeProgress.progressBar.visible()

        if (isNetworkAvailable()) {
            sharedViewModel.getServiceDetails(
                reservationId = resId.toString(),
                apiKey = loginModelPref.api_key,
                originId = mainOriginId,
                destinationId = mainDestinationId,
                operatorApiKey = operator_api_key,
                locale = "$locale",
                appBimaEnabled = false,
                apiType = service_details_method,
                excludePassengerDetails = excludePassengerDetails
            )
        } else {
            noNetworkToast()
        }
    }

    private fun callSingleBlockUnblock(
        resId: String,
        isBlock: Boolean,
        remarks: String = "",
        authPin: String
    ) {
        val reqBody =
            com.bitla.ts.domain.pojo.single_block_unblock.single_block_unblock_request.ReqBody(
                api_key = loginModelPref.api_key,
                res_id = resId,
                response_format = response_format.toBoolean(),
                locale = locale
            )

        if (isBlock) {
            val remarksInList = remarks.split(",")
            val blockingReasonId = remarksInList[0]
            val remark = remarksInList.drop(1).joinToString(",")

            reqBody.remarks = remark
            reqBody.blockingReason = blockingReasonId
        }

        val singleBlockUnblockRequest = SingleBlockUnblockRequest(
            bccId.toString(),
            format_type,
            single_block_unblock_method_name,
            req_body = reqBody
        )
        availableRoutesViewModel.singleBlockUnblockApi(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            singleBlockUnblockRequest,
            single_block_unblock_method_name,
            authPin = authPin
        )
    }

    private fun setObserver() {

        if (::rapidBookingDialog.isInitialized) {
            if (rapidBookingDialog.isShowing) {
                rapidBookingDialog.dismiss()
            }
        }

        availableRoutesViewModel.loadingState.observe(this) { it ->

            when (it) {
                LoadingState.LOADING -> startShimmerEffect()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }
        }

        sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

            privilegeResponse?.let {
                if (privilegeResponse?.isAllowBookingAfterTravelDate == true) {
                    val date = travelDate
                    if (date.isNotEmpty())
                        sevenDaysDate = getPreviousssDate(date)
                    availableRoutesViewModel.getNextCalenderDates(sevenDaysDate, travelDate)
                } else {
                    // get next seven days date with current date
                    sevenDaysDate = travelDate.ifEmpty { getTodayDate() }
                    availableRoutesViewModel.getNextCalenderDates(sevenDaysDate, travelDate)
                }
            }
        }


        availableRoutesViewModel.dataAvailableRoutes.observe(this) {
            binding.includeProgress.progressBar.gone()
            stopShimmerEffect()

            removeKey(PREF_AVAILABLE_ROUTES_ITEM_IS_SERVICE_BLOCKED)

            if (it != null) {
                if (it.code == 200) {
                    if (sourceId!="0" && destinationId!="0"&& availableRoutesList != null) {
                        availableRoutesList.clear()
                    }

                    if(sourceId=="0" || destinationId=="0") {
                        totalPage = it.number_of_pages ?: 1
                        pageNumber = it.current_page ?: 1


                        binding.paginationProgress.progressBar.gone()
//                        if (pageNumber == 1){
//                            availableRoutesList.clear()}
                    }

                    val list: MutableList<Result> = it.result

                    if((sourceId=="0" || destinationId=="0")&& pageNumber>1) {

                    }else{
                        availableRoutesList = list
                    }


                    PreferenceUtils.putObject(it, PREF_AVAILABLE_ROUTES_RESPONSE)

                    if (it.result.isNullOrEmpty()) {
                        binding.noData.visible()
                        binding.tvNoService.visible()
                        binding.tvNoService.text = "${it.message}"
                        layoutVisibility(availableRoutesList)
                    } else {
                        layoutVisibility(availableRoutesList)

                        setAvailableRoutes(availableRoutesList)
                        binding.noData.gone()
                        binding.tvNoService.gone()
                        setFilterVisibility()
                        if(pageNumber !=1 && (sourceId=="0" || destinationId=="0")){
                            searchListAdapter.updateData(it.result)
                        }else{
                        setBusDetailsAdapter()
                        }
                        setMinFare(availableRoutesList)


                        if (!isFirstDateSelected!!) {
                            isFirstDateSelected = true
                            sevenDaysDate = travelDate
                            availableRoutesViewModel.getNextCalenderDates(sevenDaysDate, travelDate)
                        }

                        if((sourceId=="0"||destinationId=="0") ){

                            if( pageNumber==totalPage){
                                binding.filterCardCV.visibility = View.VISIBLE
                                binding.sortTV.visibility = View.VISIBLE
                            }else{
                            binding.filterCardCV.visibility = View.GONE
                            binding.sortTV.visibility = View.GONE
                        }}


                    }

                } else if (it.code == 401) {
                    /* DialogUtils.unAuthorizedDialog(
                         this,
                         "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                         this
                     )*/
                    showUnauthorisedDialog()

                } else {
                    availableRoutesList.clear()
                    binding.noData.visible()
                    binding.tvNoService.visible()
                    binding.tvNoService.text = "${it.message}"
                    binding.filters.gone()
                    binding.rvBusDetails.gone()
                    setBusDetailsAdapter()
                }
            } else {
                Timber.d("serverErrorCheck::2")

                toast(getString(R.string.server_error))
            }

        }

        availableRoutesViewModel.listOfDates.observe(this) {
            dateList = it
            binding.rvDateDetails.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rvDateDetails.adapter =
                MyBookingsDatesAdapter(
                    context = this,
                    onItemClickListener = this,
                    menuList = it,
                    isShowCalendar = true
                )
        }

        sharedViewModel.serviceDetails.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                if (it.code == 200) {
                    if (serviceApiType != null) {
                        boardingList = mutableListOf()
                        droppingList = mutableListOf()
                        stageDetails = it.body.stageDetails!!

                        for (i in 0..it.body.stageDetails?.size!!.minus(1)) {
                            if (it.body?.stageDetails!![i].cityId.toString() == sourceId) {
                                generateBoardingList(i)
                            } else {
                                generateDroppingList(i)
                            }
                        }

                        val availableSeatList = mutableListOf<String>()
                        val passengerList = mutableListOf<PassengerDetails>()

                        if (serviceApiType == getString(R.string.edit_chart)) {
                            val seatDetails: List<SeatDetail>? = it.body?.coachDetails?.seatDetails
                            seatDetails?.forEach { it ->
                                if (it != null) {
                                    if (it.available!! && it.isBlocked == false)
                                        availableSeatList.add(
                                            "${it.number} ($currency${
                                                it.fare.toString().toDouble()
                                                    .convert(currencyFormat)
                                            })"
                                        )
                                    if (it.passengerDetails != null) {
                                        passengerList.add(it.passengerDetails!!)
                                    }
                                } else {
                                    Timber.d("serverErrorCheck::3")

                                    toast(getString(R.string.server_error))
                                }
                            }
                            availableSeats(availableSeats = availableSeatList)
                            setPassengerDetails(passengerDetails = passengerList)
                            val intent = Intent(this, EditChartActivity::class.java)
                            startActivity(intent)
                        } else {
                            val dropOff =
                                PreferenceUtils.getObject<DropOffDetail>(PREF_DROPPING_STAGE_DETAILS)
                            val boardingAt = PreferenceUtils.getObject<BoardingPointDetail>(
                                PREF_BOARDING_STAGE_DETAILS
                            )


                            PreferenceUtils.putString(PREF_SOURCE, it.body.origin?.name)
                            PreferenceUtils.putString(PREF_SOURCE_ID, it.body.origin?.id)
                            PreferenceUtils.putString(PREF_DESTINATION, it.body.destination?.name)
                            PreferenceUtils.putString(PREF_DESTINATION_ID, it.body.destination?.id)


                            stageDetails.forEach {
                                if (it.id.toString() == dropOff?.id) {
                                    PreferenceUtils.putObject(it, PREF_DROPPING_STAGE_DETAILS)
                                } else if (it.id.toString() == boardingAt?.id) {
                                    PreferenceUtils.putObject(it, PREF_BOARDING_STAGE_DETAILS)
                                }
                            }

                            val intent = Intent(this, QuickBookingActivity::class.java)
                            intent.putExtra("SEATS", noOfTickets)
                            Timber.d("confertmbpdp:001 $noOfTickets , $boardingPoint, $droppingPoint")
                            intent.putExtra(getString(R.string.boarding_point), boardingPoint)
                            intent.putExtra(getString(R.string.dropping_point), droppingPoint)
                            startActivity(intent)

                        }
                    }
                } else if (it.code == 401) {
                    /* DialogUtils.unAuthorizedDialog(
                         this,
                         "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                         this
                     )*/
                    showUnauthorisedDialog()

                } else
                    it.message?.let { it1 -> toast(it1) }
            } else
                toast(getString(R.string.server_error))
        }

        availableRoutesViewModel.dataSingleBLockUnblock.observe(this) {
            it
            stopShimmerEffect()
            if (it != null) {
                if (it.code == 200) {
                    if (it.message != null)
                        toast(it.message)

                    if(sourceId=="0"||destinationId=="0"){
                        pageNumber=1
                        allToAllavailableRoutesApi()
                    }else{
                        availableRoutesApi()
                    }
                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

                } else {
                    if (it.message != null)
                        toast(it.message)
                }

            } else {
                Timber.d("serverErrorCheck::4")

                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setMinFare(availableRoutesList: MutableList<Result>) {
        availableRoutesList.forEach { it ->
            try {
                if (it.fare_str.contains(",")) {
                    val list: List<Int> = it.fare_str.split(",").map(String::toInt)
                    val minValue = list.minOf { it }
                    it.fare_min = minValue
                } else
                    it.fare_min = it.fare_str.toInt()
            } catch (e: Exception) {
                Timber.d("exceptionMsg ${e.message}")
            }
        }
    }

    private fun setRadioGroupState(isShimmerActive: Boolean) {
        disableRadioGroup(binding.rgSleeperSeater, isShimmerActive)
        disableRadioGroup(binding.rgAcNonAc, isShimmerActive)
        disableRadioGroup(binding.rgPriceDeparture, isShimmerActive)
    }

    private fun disableRadioGroup(radioGroup: RadioGroup, isDisabled: Boolean) {
        for (i in 0 until radioGroup.childCount) {
            val child = radioGroup.getChildAt(i)
            if (child is RadioButton) {
                child.isEnabled = !isDisabled
            }
        }
        radioGroup.isEnabled = !isDisabled
    }

    private fun startShimmerEffect() {
        if(pageNumber<=1){
        binding.noData.gone()
        binding.tvNoService.gone()
        binding.filters.gone()
        binding.rvBusDetails.gone()
        //binding.toolbarHeaderText.gone()
        //binding.toolbarHeaderDate.gone()
        //binding.toolbarImageLeft.gone()
        binding.shimmerLayoutBusDetails.shimmerToolbar.gone()
        binding.shimmerAvailableRoutes.visible()
        setRadioGroupState(true)
    }}

    private fun stopShimmerEffect() {
        binding.swipeRefreshLayout.isRefreshing = false
        binding.shimmerLayoutBusDetails.shimmerLayoutDates.gone()
        binding.shimmerLayoutBusDetails.shimmerLayoutRecentSearch.gone()

        binding.shimmerAvailableRoutes.gone()
        binding.rvBusDetails.visible()
        binding.swipeRefreshLayout.visible()
        binding.rvDateDetails.visible()
        binding.filters.visible()
        defaultIconRgSleeperSeater()
        defaultIconRgAcNonAc()
        defaultIconRgPriceDeparture()
        binding.toolbarHeaderText.visible()
        //New Booking Flow Date is Not Visible on toolbar
        binding.toolbarHeaderDate.gone()
        binding.toolbarImageLeft.visible()
        setRadioGroupState(false)
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

    private fun getPrefs() {
        bccId = getBccId()
        locale = PreferenceUtils.getlang()
        sourceId = PreferenceUtils.getString(PREF_SOURCE_ID)!!
        destinationId = PreferenceUtils.getString(PREF_DESTINATION_ID)!!
        source = getSource()
        destination = getDestination()
        travelDate = getTravelDate()
        loginModelPref = getLogin()
        lifecycleScope.launch {
            destinationPairModel = getDestinationPairModel()

        }

//        privilegeResponse = getPrivilegeBase()

        lifecycleScope.launch {
            val privilege = getPrivilegeBaseSafely()
            sharedViewModel.updatePrivileges(privilege)
        }

        sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

            if (privilegeResponse != null) {
                bookingAfterDoj = if (privilegeResponse?.bookingAfterDoj == null) {
                    0
                } else {
                    if (privilegeResponse?.bookingAfterDoj?.trim()?.isEmpty() == true) {
                        0
                    } else {
                        privilegeResponse?.bookingAfterDoj?.trim()?.toInt() ?: 0
                    }
                }
                privilegeResponse?.let {
                    if (privilegeResponse?.isAgentLogin == true) {
                        isAgentLogin = privilegeResponse?.isAgentLogin ?: false
                    }
                }

                privilegeResponse?.let {
                    currency = privilegeResponse?.currency ?: ""
                    currencyFormat = getCurrencyFormat(
                        this@BusDetailsActivity,
                        privilegeResponse?.currencyFormat
                    )
                }
                if (privilegeResponse?.rapidBookingWithMotCouponInTsApp == true) {
                    isMotAllowed = true
                }
                pinSize = privilegeResponse?.pinCount ?: 6
                shouldSingleBlockUnblock =
                    privilegeResponse?.pinBasedActionPrivileges?.singlePageBlockUnblock ?: false

                if (privilegeResponse.tsPrivileges?.allowServiceBlockingReasonsList == true) {
                    if (isNetworkAvailable())
                        pickUpChartViewModel.getServiceBlockReasonsListApi(loginModelPref.api_key)
                    else
                        noNetworkToast()

                    serviceBlockReasonsListObserver()
                }
            }

        }


        PreferenceUtils.setPreference(PREF_UPDATE_COACH, true)
    }

    private suspend fun getLoginInfo(): LoginModel = withContext(Dispatchers.IO) {
        getLogin()
    }



    private suspend fun getDestinationPairModel(): DestinationPairModel? =
        withContext(Dispatchers.IO) {
            PreferenceUtils.getObject<DestinationPairModel>("destinationPairModel")
        }

    private fun setToolbarTitle() {
        val srcDest = "$source-$destination"
        binding.toolbarHeaderText.text = srcDest

        //New Booking Flow Date is Not Visible on toolbar
        binding.toolbarHeaderDate.gone()
//        if (travelDate.isNotEmpty())
//            binding.toolbarHeaderDate.text = getDateDMYY(travelDate)
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    private fun setBusDetailsAdapter() {

        busList.clear()

        val busType = BusFilterModel()
        busType.busType = "Seater"
        busType.icon = R.drawable.ic_filter_seat
        busList.add(busType)

        val busType1 = BusFilterModel()
        busType1.busType = "Sleeper"
        busType1.icon = R.drawable.ic_sleeper_seat
        busList.add(busType1)

        val busType2 = BusFilterModel()
        busType2.busType = "AC"
        busType2.icon = R.drawable.ic_ac
        busList.add(busType2)

        val busType3 = BusFilterModel()
        busType3.busType = "Non-AC"
        busType3.icon = R.drawable.ic_non_ac
        busList.add(busType3)


        binding.rvBusDetails.visibility = View.VISIBLE

        sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->


            binding.rvBusDetails.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            searchListAdapter =
                BusDetailsAdapter(
                    context = this,
                    onItemClickListener = this,
                    availableRoutesList = availableRoutesList,
                    lifecycle = this,
                    privilegeResponseModel = privilegeResponse,
                    loginModelPref = loginModelPref
                )
            binding.rvBusDetails.adapter = searchListAdapter
        }

    }

    @SuppressLint("LogNotTimber")
    private fun availableRoutesApi() {

        val isBima: Boolean? = PreferenceUtils.getPreference("is_bima", false)!!
        Timber.d("checkBima-BusDetailsActivity = $isBima")

        var isCsShared: Boolean? = null
        if (isBima == true) {
            isCsShared = true
        }

        if (isNetworkAvailable()) {
            availableRoutesViewModel.availableRoutesApi(
                apiKey = loginModelPref.api_key,
                originId = sourceId,
                destinationId = destinationId,
                showInJourneyServices = showInJourneyServices,
                isCsShared = isCsShared ?: false,
                operatorkey = operator_api_key,
                responseFormat = format_type,
                travelDate = ymdDate,
                showOnlyAvalServices = showOnlyAvailableServices,
                locale = locale ?: "",
                appBimaEnabled = isBima ?: false,
                apiType = available_routes_method_name
            )
        } else
            noNetworkToast()
    }





    @SuppressLint("LogNotTimber")
    private fun allToAllavailableRoutesApi() {
        if (pageNumber == 1) {
            binding.includeProgress.progressBar.visible()
            binding.rvBusDetails.gone()
        } else {
            if(pageNumber>1){
            binding.paginationProgress.progressBar.visible()
            binding.rvBusDetails.visible()}
        }
        binding.noData.gone()

        val isBima: Boolean? = PreferenceUtils.getPreference("is_bima", false)!!
        Timber.d("checkBima-BusDetailsActivity = $isBima")

        var isCsShared: Boolean? = null
        if (isBima == true) {
            isCsShared = true
        }

        if (isNetworkAvailable()) {
            availableRoutesViewModel.availableRoutesForAgent(
                apiKey = loginModelPref.api_key,
                originId = sourceId,
                destinationId = destinationId,
                showInJourneyServices = showInJourneyServices,
                isCsShared = isCsShared ?: false,
                operatorkey = operator_api_key,
                responseFormat = format_type,
                travelDate = ymdDate,
                showOnlyAvalServices = showOnlyAvailableServices,
                locale = locale ?: "",
               pagination =  pagination.toString(),
               per_page =  perPage.toString(),
               page = pageNumber.toString()
            )
        } else
            noNetworkToast()
    }






    override fun initUI() {
        setDateLocale(PreferenceUtils.getlang(), this@BusDetailsActivity)
        binding = LayoutBusDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        clickListener()
        lifecycleScope.launch {
            availableRoutesViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
//        showCaseDialog()
    }

    private fun clickListener() {
        binding.toolbarImageLeft.setOnClickListener(this)
        binding.toolbarHeaderText.setOnClickListener(this)
        binding.imageFilter.setOnClickListener(this)
        binding.layoutFilterBooking.buttonSave.setOnClickListener(this)
        binding.layoutFilterBooking.textAddUserCancel.setOnClickListener(this)
        binding.imageCalender.setOnClickListener(this)

        binding.modifySearchLayout.btnCancel.setOnClickListener(this)

//        binding.toolbarImageLeft.setOnClickListener(this)

        binding.rgSleeperSeater.setOnCheckedChangeListener(this)
        binding.rgAcNonAc.setOnCheckedChangeListener(this)
        binding.rgPriceDeparture.setOnCheckedChangeListener(this)

        binding.layoutSleeper.setOnTouchListener(this)
        binding.layoutSeater.setOnTouchListener(this)
        binding.layoutAc.setOnTouchListener(this)
        binding.layoutNonAc.setOnTouchListener(this)
        binding.layoutPriceLow.setOnTouchListener(this)
        binding.layoutPriceHigh.setOnTouchListener(this)
        binding.layoutDepartingEarly.setOnTouchListener(this)
        binding.layoutDepartingLate.setOnTouchListener(this)


        //New Booking Flow Clicks

        binding.modifySearchLayout.tvSource.setOnClickListener(this)
        binding.modifySearchLayout.tvDestination.setOnClickListener(this)
        binding.modifySearchLayout.tvTodayDate.setOnClickListener(this)
        binding.modifySearchLayout.tvTomorrowDate.setOnClickListener(this)
        binding.modifySearchLayout.tvSelectDate.setOnClickListener(this)
        binding.modifySearchLayout.tvSelectReturnDate.setOnClickListener(this)
        binding.modifySearchLayout.btnRotate.setOnClickListener(this)
        binding.modifySearchLayout.btnSearch.setOnClickListener(this)
        binding.modifySearchLayout.busServices.setOnClickListener(this)


    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.toolbar_image_left -> {
//                storePrefs()
                onBackPressed()
            }

            R.id.toolbar_header_text -> {
//                storePrefs()
//                onBackPressed()

                if(sourceId!="0" && destinationId!="0") {
                    binding.modifySearchLayout.root.visible()
                    binding.modifySearchLayout.tansparentbackbroundServiceSummary.visible()


                    setExistingSearchData()
                }

//                showCalendar()


            }

            R.id.btnCancel -> {
                isFromModifyDatePopup = false
                binding.modifySearchLayout.root.gone()
                binding.modifySearchLayout.tansparentbackbroundServiceSummary.gone()

            }

            R.id.image_filter -> {
                binding.layoutFilterBooking.root.visible()
                binding.imageFilter.visibility = View.GONE
            }

            R.id.button_save -> {
                binding.layoutFilterBooking.root.gone()
                binding.imageFilter.visibility = View.VISIBLE

            }

            R.id.text_add_user_cancel -> {

            }

            R.id.image_calender -> {
                /* selectedDateType = "Single"
                var intent = Intent(this, CalendarActivity::class.java)
                intent.putExtra(getString(R.string.TRIP_TYPE), selectedDateType)
                startActivityForResult(intent, RESULT_CODE_DATE_BUS)*/

                sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

                    var minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)

                    if (privilegeResponse?.isAllowBookingAfterTravelDate == true) {
                        minDate = stringToDate("01-01-1900", DATE_FORMAT_D_M_Y)
                    }

                    //openDateDialog()

                    if (isAgentLogin) {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.add(Calendar.DATE, -1 * bookingAfterDoj)
                        minDate = stringToDate(
                            inputFormatToOutput(
                                inputDate = calendar.time.toString(),
                                inputFormat = "EEE MMM dd HH:mm:ss zzzz yyyy",
                                outputFormat = DATE_FORMAT_D_M_Y
                            ), DATE_FORMAT_D_M_Y
                        )
                    }

                    SlyCalendarDialog()
                        .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                        .setMinDate(minDate)
                        .setSingle(true)
                        .setFirstMonday(false)
                        .setCallback(this)
                        .show(supportFragmentManager, TAG)
                }



            }


            R.id.tvSource -> {

                sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

                    if (privilegeResponse?.allowToShowNewFlowInTsApp == true) {
                        sourcePopupDialog()

                    }
                    else {
                        if (privilegeResponse != null) {
                            val privilegeResponse = privilegeResponse

                            if (privilegeResponse?.isCityWiseBpDpDisplay == null) {
                                context.toast(getString(R.string.server_error))
                            } else {
                                if (privilegeResponse.isCityWiseBpDpDisplay && privilegeResponse.availableAppModes?.allowBpDpFare == true) {
                                    val intent = Intent(
                                        this,
                                        InterCityActivity::class.java
                                    )
                                    intent.putExtra(
                                        getString(R.string.CITY_SELECTION_TYPE),
                                        getString(R.string.SOURCE_SELECTION)
                                    )
                                    startActivityForResult(
                                        intent,
                                        RESULT_CODE_SOURCE
                                    )
                                } else {
                                    val intent = Intent(
                                        this,
                                        SearchActivity::class.java
                                    )
                                    intent.putExtra(
                                        getString(R.string.CITY_SELECTION_TYPE),
                                        getString(R.string.SOURCE_SELECTION)
                                    )
                                    startActivityForResult(
                                        intent,
                                        RESULT_CODE_SOURCE
                                    )

                                }

                            }

                        } else {
                            context.toast(context.getString(R.string.server_error))
                        }
                    }
                }




                firebaseLogEvent(
                    context,
                    ORIGIN_POINT_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    ORIGIN_POINT_CLICKS,
                    "Origin Point - Booking"
                )
            }

            R.id.tvDestination -> {

                sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

                    if (privilegeResponse?.allowToShowNewFlowInTsApp == true) {

                        if (binding.modifySearchLayout.tvSource.text.isEmpty()) {
                            toast("Please select Source first")
                        } else {
                            createDestinationList()
                            destinationPopupDialog()
                        }


                    }
                    else {

                        if (binding.modifySearchLayout.tvSource.text.isEmpty())
                            context.toast("Please select Source first")
                        else if (privilegeResponse != null) {
                            if (privilegeResponse?.isCityWiseBpDpDisplay == null) {
                                context.toast(getString(R.string.server_error))
                            } else {

                                if (privilegeResponse?.isCityWiseBpDpDisplay!! && privilegeResponse?.availableAppModes?.allowBpDpFare == true) {
                                    interCreateDestinationList()

                                    val intent = Intent(
                                        this,
                                        InterCityActivity::class.java
                                    )
                                    intent.putExtra(
                                        getString(R.string.CITY_SELECTION_TYPE),
                                        getString(R.string.DESTINATION_SELECTION)
                                    )
                                    startActivityForResult(
                                        intent,
                                        RESULT_CODE_SOURCE
                                    )
                                } else {
                                    createDestinationList()
                                    val intent = Intent(
                                        this,
                                        SearchActivity::class.java
                                    )
                                    intent.putExtra(
                                        getString(R.string.CITY_SELECTION_TYPE),
                                        getString(R.string.DESTINATION_SELECTION)
                                    )
                                    startActivityForResult(
                                        intent,
                                        RESULT_CODE_SOURCE
                                    )
                                }

                            }

                        } else {
                            context.toast(context.getString(R.string.server_error))
                        }
                    }
                }

                firebaseLogEvent(
                    this,
                    DESTINATION_POINT_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    DESTINATION_POINT_CLICKS,
                    "Destination Point- Booking"
                )
            }

            R.id.tvTodayDate -> {
                binding.modifySearchLayout.busServices.text = ""

                setTodayDate()
//                searchBtnNavigation(false)

                firebaseLogEvent(
                    this,
                    TODAY_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    TODAY_CLICKS,
                    "Today Clicks - Booking"
                )
            }


            R.id.tvTomorrowDate -> {
                binding.modifySearchLayout.busServices.text = ""

                setTomorrowDate()
//                searchBtnNavigation(false)

                firebaseLogEvent(
                    context,
                    TOMORROW_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    TOMORROW_CLICKS,
                    "Tomorrow Clicks - Booking"
                )
            }


            R.id.btnRotate -> {

                source = binding.modifySearchLayout.tvSource.text.toString()
                destination = binding.modifySearchLayout.tvDestination.text.toString()

                if (binding.modifySearchLayout.tvSource.text.toString()
                        .isNotEmpty() && binding.modifySearchLayout.tvDestination.text.toString()
                        .isNotEmpty()
                ) {
                    /*  val src = tvDestination.text.toString()
                      tvDestination.text = tvSource.text.toString()
                      tvSource.text = src*/

                    val temp = destination
                    destination = source
                    source = temp

                    val tempId = destinationId
                    destinationId = sourceId
                    sourceId = tempId

                    finalDestinationId = destinationId
                    finalSourceId = sourceId

                    binding.modifySearchLayout.tvSource.text = source
                    binding.modifySearchLayout.tvDestination.text = destination


                    PreferenceUtils.setPreference(
                        PREF_SOURCE,
                        source
                    )
                    PreferenceUtils.putString(
                        PREF_SOURCE_ID,
                        finalSourceId
                    )

                    PreferenceUtils.setPreference(
                        PREF_DESTINATION,
                        destination
                    )
                    PreferenceUtils.setPreference(
                        PREF_DESTINATION_ID,
                        finalDestinationId
                    )


                    val aniRotate =
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.rotate_clockwise
                        )
                    binding.modifySearchLayout.imgRotate.startAnimation(
                        aniRotate
                    )
                }

                firebaseLogEvent(
                    this,
                    ORIGIN_DESTINATION_SWAP_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    ORIGIN_DESTINATION_SWAP_CLICKS,
                    "Origin-Destination Swap"
                )
            }

            R.id.btnSearch -> {
                finalDestinationId = destinationId
                finalSourceId = sourceId


                searchBtnNavigation(false)

                firebaseLogEvent(
                    this,
                    SEARCH_BUTTON,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    SEARCH_BUTTON,
                    "Search Button - Booking"
                )
            }

            R.id.tvSelectDate -> {
                selectedDateType = "Single"
                /*var intent = Intent(activity, CalendarActivity::class.java)
                intent.putExtra(getString(R.string.TRIP_TYPE), selectedDateType)
                startActivityForResult(intent, RESULT_CODE_DATE)*/

                sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

                    var minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                    if (privilegeResponse?.isAllowBookingAfterTravelDate == true) {
                        minDate = stringToDate("01-01-1900", DATE_FORMAT_D_M_Y)
                    }
                    //openDateDialog()


                    if (isAgentLogin) {
                        if (privilegeResponse?.isAllowBookingAfterTravelDate == true) {
                            val calendar: Calendar = Calendar.getInstance()
                            calendar.add(Calendar.DATE, -1 * bookingAfterDoj)
                            minDate = stringToDate(
                                inputFormatToOutput(
                                    calendar.time.toString(),
                                    DATE_FORMAT_EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY,
                                    DATE_FORMAT_D_M_Y
                                ), DATE_FORMAT_D_M_Y
                            )
                        } else
                            minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                    }
                    isFromModifyDatePopup = true
                    SlyCalendarDialog()
                        .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                        .setMinDate(minDate)
                        .setSingle(true)
                        .setFirstMonday(false)
                        .setCallback(object : SlyCalendarDialog.Callback {
                            override fun onCancelled() {

                            }

                            override fun onDataSelected(
                                firstDate: Calendar?,
                                secondDate: Calendar?,
                                hours: Int,
                                minutes: Int
                            ) {
                                firstDate!!.set(Calendar.HOUR_OF_DAY, hours)
                                firstDate.set(Calendar.MINUTE, minutes)
                                travelDate = SimpleDateFormat(
                                    DATE_FORMAT_D_M_Y,
                                    Locale.getDefault()
                                ).format(firstDate.time)
                                PreferenceUtils.setPreference(PREF_TRAVEL_DATE, travelDate)

                                ymdDate = inputFormatToOutput(
                                    travelDate,
                                    DATE_FORMAT_D_M_Y,
                                    DATE_FORMAT_Y_M_D
                                )


                                convertedDate = getDateYMD(travelDate)
                                binding.modifySearchLayout.tvSelectDate.text = thFormatDateMMMOutput(convertedDate!!)
                                privilegeResponse?.let {
                                    if (privilegeResponse?.isAllowBookingAfterTravelDate == true) {
                                        val date = travelDate
                                        if (date.isNotEmpty())
                                            sevenDaysDate = getPreviousssDate(date)

                                    } else {
                                        // get next seven days date with current date
                                        sevenDaysDate = travelDate.ifEmpty { getTodayDate() }

                                    }
                                }
                            }

                        }).show(supportFragmentManager, TAG)
                }

            }

            R.id.bus_services -> {
                finalDestinationId = destinationId
                finalSourceId = sourceId


                if (binding.modifySearchLayout.tvSource.text.isEmpty()) context.toast(
                    context.getString(R.string.validate_source)
                )
                else if (binding.modifySearchLayout.tvDestination.text.isEmpty()) context.toast(
                    context.getString(R.string.validate_destination)
                )
                else if (binding.modifySearchLayout.tvSelectDate.text.isEmpty()) context.toast(
                    context.getString(R.string.validate_date)
                )
                else {
                    PreferenceUtils.setPreference(
                        PREF_SOURCE,
                        binding.modifySearchLayout.tvSource.text.toString()
                    )
                    PreferenceUtils.setPreference(
                        PREF_DESTINATION,
                        binding.modifySearchLayout.tvDestination.text.toString()
                    )
                    PreferenceUtils.putString(PREF_SOURCE_ID, finalSourceId)
                    PreferenceUtils.setPreference(PREF_DESTINATION_ID, finalDestinationId)
                    PreferenceUtils.setPreference(PREF_SOURCE_ID, finalSourceId)
                    PreferenceUtils.setPreference(PREF_DESTINATION_ID, finalDestinationId)

                    Timber.d("lastSearchedSource sourceId $sourceId destinationId $destinationId")
                    PreferenceUtils.setPreference(PREF_TRAVEL_DATE,
                        convertedDate?.let { getDateDMY(it) })
                    val intent = Intent(
                        this,
                        SearchActivity::class.java
                    )
                    intent.putExtra(
                        getString(R.string.CITY_SELECTION_TYPE),
                        getString(R.string.select_service)
                    )
                    startActivityForResult(
                        intent,
                        RESULT_CODE_SOURCE
                    )

                    firebaseLogEvent(
                        context,
                        ORIGIN_POINT_CLICKS,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        ORIGIN_POINT_CLICKS,
                        "Origin Point - Booking"
                    )
                }

            }


        }
    }

    private fun sourcePopupDialog() {
        var popupBinding: AdapterSearchBpdpBinding? = null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(this))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        popupBinding.searchET.requestFocus()

        forceShowKeyboard(popupBinding.searchET)
        originList = PreferenceUtils.getOriginCity() ?: arrayListOf()

        sourceNewAdapter = SimpleListAdapter(this, originList, this, SOURCE)
        popupBinding.searchRV.adapter = sourceNewAdapter


        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                sourceNewAdapter?.filter?.filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })


        sourcePopupWindow = PopupWindow(
            popupBinding.root,
            binding.modifySearchLayout.tvSource.width,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        val xOff = 0
        val yOff = binding.modifySearchLayout.tvSource.height

        sourcePopupWindow?.showAsDropDown(binding.modifySearchLayout.tvSource, xOff, yOff)

        sourcePopupWindow?.elevation = 25f


        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }


    private fun searchBtnNavigation(isBusService: Boolean) {
        if (binding.modifySearchLayout.tvSource.text.isEmpty()) this.toast(
            context.getString(R.string.validate_source)
        )
        else if (binding.modifySearchLayout.tvDestination.text.isEmpty()) this.toast(
            context.getString(R.string.validate_destination)
        )
        else if (binding.modifySearchLayout.tvSelectDate.text.isEmpty()) this.toast(
            context.getString(R.string.validate_date)
        )
        else {
            PreferenceUtils.setPreference(
                PREF_SOURCE,
                binding.modifySearchLayout.tvSource.text.toString()
            )
            PreferenceUtils.setPreference(
                PREF_DESTINATION,
                binding.modifySearchLayout.tvDestination.text.toString()
            )
            PreferenceUtils.putString(PREF_SOURCE_ID, finalSourceId)
            PreferenceUtils.setPreference(PREF_DESTINATION_ID, finalDestinationId)
            PreferenceUtils.setPreference(PREF_SOURCE_ID, finalSourceId)
            PreferenceUtils.setPreference(PREF_DESTINATION_ID, finalDestinationId)

            Timber.d("lastSearchedSource sourceId $sourceId destinationId $destinationId")
            PreferenceUtils.setPreference(PREF_TRAVEL_DATE,
                convertedDate?.let { getDateDMY(it) })
            PreferenceUtils.setPreference(
                PREF_LAST_SEARCHED_SOURCE,
                binding.modifySearchLayout.tvSource.text.toString()
            )
            PreferenceUtils.setPreference(
                PREF_LAST_SEARCHED_DESTINATION,
                binding.modifySearchLayout.tvDestination.text.toString()
            )

            PreferenceUtils.putString(
                PREF_NEW_BOOKING_NAVIGATION,
                FragmentBooking.TAG
            )

            if(sourceId=="0"||destinationId=="0"){
                PreferenceUtils.setPreference("isAllToAllSearch", true)
                PreferenceUtils.setPreference("AllToAllSource", source)
                PreferenceUtils.setPreference("AllToAllSourceId", sourceId)
                PreferenceUtils.setPreference("AllToAllDestination",destination)
                PreferenceUtils.setPreference("AllToAllDestinationId", destinationId)
                pageNumber=1
                allToAllavailableRoutesApi()
            }else{
                availableRoutesApi()
            }

//                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
//                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)

            if (convertedReturnDate == null) {
                if (!binding.modifySearchLayout.busServices.text.isNullOrEmpty()) {
                    PreferenceUtils.setPreference("seatwiseFare", "")
                    val serviceBpDpFare =
                        PreferenceUtils.getObject<String>(SERVICE_IS_APPLY_BP_DP_FARE)
                    Timber.d("serviceBpDPCheck0: $serviceBpDpFare")

                    if (serviceBpDpFare == "true") {
                        Timber.d("serviceBpDPCheck1: $serviceBpDpFare")
                        bpDpService()

                    } else {
                        Timber.d("serviceBpDPCheck2: $serviceBpDpFare")
                        val intent = Intent(context, NewCoachActivity::class.java)
                        PreferenceUtils.putString("SelectionCoach", "BOOK")
                        PreferenceUtils.putString("fromBusDetails", "bookBlock")
                        startActivity(intent)
                    }

                } else {
                    val intent = Intent(this, BusDetailsActivity::class.java)

                    intent.putExtra(
                        getString(R.string.JOURNEY_DATE),
                        convertedDate?.let { getDateDMY(it) }
                    )
                    PreferenceUtils.putString(
                        "convertedDate",
                        convertedDate?.let { getDateDMY(it) })
                    startActivity(intent)
                }

            }
            finish()
        }
    }


    private fun bpDpService() {
        val bpDpBoarding = PreferenceUtils.getBoarding()
        val bpDpDropping = PreferenceUtils.getDropping()
        if (!sourceId.contains("-1") && !destinationId.contains("-1")) {
            Timber.d("clickListnerTESTLISTENER: ${sourceId}, $destinationId")

            if (!sourceId.contains(":") || !destinationId.contains(":")) {
                if (bpDpBoarding?.size == 1 && bpDpDropping?.size == 1) {
                    PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                    PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                    val intent = Intent(this, NewCoachActivity::class.java)
                    startActivity(intent)
                } else {
                    PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)
                    PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)

                    val intent = Intent(this, InterBDActivity::class.java)
                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    intent.putExtra("PreSelectedDropping", "false")
                    intent.putExtra("preSelectedBoarding", "false")
                    intent.putExtra(getString(R.string.bus_type), serviceBusType)
                    intent.putExtra(getString(R.string.dep_time), serviceDepTime)
                    intent.putExtra(getString(R.string.service_number), service_ServiceNUmber)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        "${getString(R.string.booking)}"
                    )
                    intent.putExtra(
                        getString(R.string.service_type),
                        getString(R.string.proceed)
                    )
                    startActivity(intent)
                }
            } else {
                var tempSource = ""
                var tempDestination = ""
                if (source?.contains(",") == true) {
                    val temp = source!!.split(",")
                    tempSource = temp[0]
                }
                if (destination?.contains(",") == true) {
                    val temp = destination!!.split(",")
                    tempDestination = temp[0]
                }
                bpDpBoarding!!.forEach {
                    if (it.name == tempSource) {
                        PreferenceUtils.putObject(it, SELECTED_BOARDING_DETAIL)
                    }
                }
                bpDpDropping!!.forEach {
                    if (it.name == tempDestination) {
                        PreferenceUtils.putObject(it, SELECTED_DROPPING_DETAIL)
                    }
                }
                val intent = Intent(this, NewCoachActivity::class.java)
                startActivity(intent)
            }

        } else if (sourceId.contains("-1") && !destinationId.contains("-1")) {
            if (bpDpBoarding?.size == 1 && bpDpDropping?.size == 1) {
                PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                val intent = Intent(this, NewCoachActivity::class.java)
                startActivity(intent)
            } else {
                if (bpDpBoarding!!.size > 1 && bpDpDropping!!.size == 1) {

                    val intent = Intent(context, InterBDActivity::class.java)
                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    intent.putExtra("PreSelectedDropping", "false")
                    intent.putExtra("preSelectedBoarding", "false")
                    intent.putExtra(getString(R.string.bus_type), serviceBusType)
                    intent.putExtra(getString(R.string.dep_time), serviceDepTime)
                    intent.putExtra(getString(R.string.service_number), service_ServiceNUmber)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        "${getString(R.string.booking)}"
                    )
                    intent.putExtra(
                        getString(R.string.service_type),
                        getString(R.string.proceed)
                    )
                    startActivity(intent)
                } else if (bpDpBoarding.size == 1 && bpDpDropping!!.size > 1) {

                    var tempSource = ""
                    if (destination?.contains(",") == true) {
                        val temp = destination!!.split(",")
                        tempSource = temp[0]
                    }
                    bpDpDropping.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_DROPPING_DETAIL)
                        }
                    }
                    PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                    val intent = Intent(this, NewCoachActivity::class.java)
                    startActivity(intent)
                } else if (bpDpBoarding.size > 1 && bpDpDropping!!.size > 1) {
                    PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)

                    val intent = Intent(this, InterBDActivity::class.java)
                    intent.putExtra("PreSelectedDropping", "true")
                    intent.putExtra("preSelectedBoarding", "false")
                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    var tempSource = ""
                    if (destination?.contains(",") == true) {
                        val temp = destination!!.split(",")
                        tempSource = temp[0]
                    }
                    bpDpDropping.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_DROPPING_DETAIL)

                        }
                    }

                    intent.putExtra(getString(R.string.bus_type), serviceBusType)
                    intent.putExtra(getString(R.string.dep_time), serviceDepTime)
                    intent.putExtra(getString(R.string.service_number), service_ServiceNUmber)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        "${getString(R.string.booking)}"
                    )
                    intent.putExtra(
                        getString(R.string.service_type),
                        getString(R.string.proceed)
                    )
                    startActivity(intent)
                }

            }
        } else if (sourceId.contains("-1") && destinationId.contains("-1")) {

            if (bpDpBoarding!!.size == 1 && bpDpDropping!!.size == 1) {
                PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                val intent = Intent(this, NewCoachActivity::class.java)
                startActivity(intent)
            } else {

                PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)

                val intent = Intent(this, InterBDActivity::class.java)
                putBoarding(bpDpBoarding)
                putDropping(bpDpDropping)
                intent.putExtra("PreSelectedDropping", "false")
                intent.putExtra("preSelectedBoarding", "false")
                intent.putExtra(getString(R.string.bus_type), serviceBusType)
                intent.putExtra(getString(R.string.dep_time), serviceDepTime)
                intent.putExtra(getString(R.string.service_number), service_ServiceNUmber)
                intent.putExtra(
                    getString(R.string.toolbar_title),
                    "${getString(R.string.booking)}"
                )
                intent.putExtra(
                    getString(R.string.service_type),
                    getString(R.string.proceed)
                )
                startActivity(intent)
            }

        } else if (!sourceId.contains("-1") && destinationId.contains("-1")) {

            if (bpDpBoarding!!.size == 1 && bpDpDropping!!.size == 1) {
                PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                val intent = Intent(this, NewCoachActivity::class.java)
                startActivity(intent)
            } else {
                var tempSource = ""

                if (bpDpBoarding.size > 1 && bpDpDropping!!.size == 1) {
                    if (source?.contains(",") == true) {
                        val temp = source!!.split(",")
                        tempSource = temp[0]
                    }
                    bpDpBoarding.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_BOARDING_DETAIL)
                        }
                    }
                    PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                    val intent = Intent(this, NewCoachActivity::class.java)
                    startActivity(intent)
                } else if (bpDpBoarding.size == 1 && bpDpDropping!!.size > 1) {
                    PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)

                    val intent = Intent(this, InterBDActivity::class.java)
                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    intent.putExtra("PreSelectedDropping", "false")
                    intent.putExtra("preSelectedBoarding", "false")
                    intent.putExtra(getString(R.string.bus_type), serviceBusType)
                    intent.putExtra(getString(R.string.dep_time), serviceDepTime)
                    intent.putExtra(getString(R.string.service_number), service_ServiceNUmber)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        "${getString(R.string.booking)}"
                    )
                    intent.putExtra(
                        getString(R.string.service_type),
                        getString(R.string.proceed)
                    )
                    startActivity(intent)

                } else if (bpDpBoarding.size > 1 && bpDpDropping!!.size > 1) {
                    PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)

                    val intent = Intent(context, InterBDActivity::class.java)
                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    intent.putExtra("PreSelectedDropping", "false")
                    intent.putExtra("preSelectedBoarding", "true")
                    if (source?.contains(",") == true) {
                        val temp = source!!.split(",")
                        tempSource = temp[0]
                    }
                    bpDpBoarding.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_BOARDING_DETAIL)
                        }
                    }
                    intent.putExtra(getString(R.string.bus_type), serviceBusType)
                    intent.putExtra(getString(R.string.dep_time), serviceDepTime)
                    intent.putExtra(getString(R.string.service_number), service_ServiceNUmber)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        "${getString(R.string.booking)}"
                    )
                    intent.putExtra(
                        getString(R.string.service_type),
                        getString(R.string.proceed)
                    )
                    startActivity(intent)
                }

            }
        }
    }

    private fun setExistingSearchData() {

        if (travelDate.isNotEmpty()) {
            convertedDate = getDateYMD(travelDate)
        }

        binding.modifySearchLayout.tvSelectDate.text = thFormatDateMMMOutput(getDateYMD(travelDate))
        binding.modifySearchLayout.tvSource.text = source
        binding.modifySearchLayout.tvDestination.text = destination
    }


    private fun setTodayDate() {
        when {
            binding.modifySearchLayout.tvSource.text.isEmpty() -> context.toast("Please select source city")
            binding.modifySearchLayout.tvDestination.text.isEmpty() -> context.toast("Please select destination city")
            binding.modifySearchLayout.tvSelectDate.text.isEmpty() -> context.toast("Please select date")
            else -> {
                binding.modifySearchLayout.tvSelectDate.text =
                    thFormatDateMMMOutput(getDateYMD(getTodayDate()))
                todayDateColor()
                convertedDate = getDateYMD(getTodayDate())
                val journeyDate = getDateYMD(getTodayDate())
                /*  if(tvSelectReturnDate.text.isNotEmpty())
                      returnDate = getDateYMD(tvSelectReturnDate.text.toString())*/
                returnDate = getString(R.string.empty)
                binding.modifySearchLayout.tvSelectReturnDate.text = getString(R.string.empty)
//                navigateToAvailableRoute(journeyDate)

            }
        }
    }


    private fun setTomorrowDate() {
        if (binding.modifySearchLayout.tvSource.text.isEmpty()) context.toast(context.getString(R.string.validate_source))
        else if (binding.modifySearchLayout.tvDestination.text.isEmpty()) context.toast(
            context.getString(
                R.string.validate_destination
            )
        )
        else if (binding.modifySearchLayout.tvSelectDate.text.isEmpty()) context.toast(
            context.getString(
                R.string.validate_date
            )
        )
        else {
            tomorrowDateColor()
            binding.modifySearchLayout.tvSelectDate.text =
                thFormatDateMMMOutput(getDateYMD(getTomorrowDate()))
            val journeyDate = getDateYMD(getTomorrowDate())
            convertedDate = getDateYMD(getTomorrowDate())
            /*  if(tvSelectReturnDate.text.isNotEmpty()) returnDate =
                  getDateYMD(tvSelectReturnDate.text.toString())*/
            binding.modifySearchLayout.tvSelectReturnDate.text = getString(R.string.empty)
            returnDate = getString(R.string.empty)
//            navigateToAvailableRoute(journeyDate)

        }
    }

    private fun tomorrowDateColor() {
        binding.modifySearchLayout.tvTodayDate.setTextColor(context.resources.getColor(R.color.button_default_color))
        binding.modifySearchLayout.tvTomorrowDate.setTextColor(context.resources.getColor(R.color.colorPrimary))
    }

    private fun todayDateColor() {
        binding.modifySearchLayout.tvTodayDate.setTextColor(context.resources.getColor(R.color.colorPrimary))
        binding.modifySearchLayout.tvTomorrowDate.setTextColor(context.resources.getColor(R.color.button_default_color))
    }


    override fun onClick(view: View, position: Int) {
        if (!availableRoutesList.isNullOrEmpty() && availableRoutesList.size > position)
            PreferenceUtils.setPreference(
                getString(R.string.is_service_blocked),
                availableRoutesList[position].is_service_blocked
            )
        if (view.tag == "viewReservation") {
            resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
            PreferenceUtils.putString("ViewReservation_date", ymdDate)

            if (position < availableRoutesList.size) {
                PreferenceUtils.putString(
                    "ViewReservation_data",
                    "${availableRoutesList[position].number} | ${getDateDMY(ymdDate)} | ${availableRoutesList[position].origin} - ${availableRoutesList[position].destination} | ${availableRoutesList[position].bus_type}"
                )
            }

            PreferenceUtils.setPreference("BUlK_shifting", false)
            PreferenceUtils.putString("BulkShiftBack", "")
            PreferenceUtils.setPreference("shiftPassenger_tab", 0)
            PreferenceUtils.setPreference("seatwiseFare", "fromBulkShiftPassenger")

            val intent = Intent(context, ViewReservationActivity::class.java)
            intent.putExtra("pickUpResid", resID)

            startActivity(intent)
        }

        if (view.tag == getString(R.string.delete_recent_search)) {
            if (isNetworkAvailable())
                callDeleteRecentSearchApi(position)
            else
                noNetworkToast()
        } else if (view.tag == this.getString(R.string.open_calender)) {

            sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

                var minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                if (privilegeResponse?.isAllowBookingAfterTravelDate == true) {
                    minDate = stringToDate("01-01-1900", DATE_FORMAT_D_M_Y)
                }


                if (isAgentLogin) {
                    if (privilegeResponse?.isAllowBookingAfterTravelDate == true) {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.add(Calendar.DATE, -1 * bookingAfterDoj)
                        minDate = stringToDate(
                            inputFormatToOutput(
                                calendar.time.toString(),
                                DATE_FORMAT_EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY,
                                DATE_FORMAT_D_M_Y
                            ), DATE_FORMAT_D_M_Y
                        )
                    } else
                        minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                }


                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(minDate)
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show(supportFragmentManager, TAG)
            }


//            updatedList.clear()
//            filteredList.clear()
//            binding.rgSleeperSeater.clearCheck()
//            binding.rgAcNonAc.clearCheck()
//            binding.rgPriceDeparture.clearCheck()
//            defaultIconRgSleeperSeater()
//            defaultIconRgAcNonAc()
//            defaultIconRgPriceDeparture()

        } else if (view.tag == getString(R.string.edit_chart)) {
            serviceApiType = getString(R.string.edit_chart)
            resId = availableRoutesList[position].reservation_id
            isApplybdDpRapid = availableRoutesList[position].is_apply_bp_dp_fare
            bpDpBoarding.clear()
            bpDpDropping.clear()
            Timber.d("isbpdpApplyFare:1: $sourceId")
            bpDpBoarding =
                availableRoutesList[position].boarding_point_details as MutableList<BoardingPointDetail>
            bpDpDropping =
                availableRoutesList[position].drop_off_details as MutableList<DropOffDetail>
            putBoarding(bpDpBoarding)
            putDropping(bpDpDropping)
            if (isApplybdDpRapid) {
                if (PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL) != null) {
                    oldBoarding =
                        PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL)!!
                }
                if (PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL) != null) {
                    oldDroping =
                        PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL)!!
                }

                serviceApiType = getString(R.string.rapid_booking)

                if (sourceId.contains(":")) {
                    Timber.d("flow check:1: $sourceId")

                    if (sourceId.contains("-1")) {
                        Timber.d("flow check:4: $sourceId")
                        if (bpDpBoarding.size == 1) {
                            boardingPoint = bpDpBoarding[0].name
                            PreferenceUtils.putObject(
                                bpDpBoarding[0],
                                PREF_BOARDING_STAGE_DETAILS
                            )
                        } else {
                            PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)
                            boardingPoint = ""
                        }
                    } else {
                        Timber.d("flow check:3: $sourceId")
                        var tempSource = ""
                        bpDpBoarding.forEach {
                            Timber.d("flow check:3.1: ${it.name} == $source")
                            if (source?.contains(",") == true) {
                                val temp = source?.split(",")
                                tempSource = temp?.get(0)!!
                            }

                            if (it.name == tempSource) {
                                boardingPoint = it.name
                                PreferenceUtils.putObject(
                                    bpDpBoarding[0],
                                    PREF_BOARDING_STAGE_DETAILS
                                )
                                PreferenceUtils.putObject(
                                    bpDpBoarding[0],
                                    SELECTED_BOARDING_DETAIL
                                )
                            }
                        }
                    }
                } else {
                    Timber.d("flow check:2: $sourceId")
                    if (bpDpBoarding.size == 1) {
                        boardingPoint = bpDpBoarding[0].name
                        PreferenceUtils.putObject(
                            bpDpBoarding[0],
                            PREF_BOARDING_STAGE_DETAILS
                        )
                        PreferenceUtils.putObject(
                            bpDpBoarding[0],
                            SELECTED_BOARDING_DETAIL
                        )
                    } else {
                        PreferenceUtils.putObject(
                            emptyBoarding,
                            SELECTED_BOARDING_DETAIL
                        )
                        boardingPoint = ""
                    }
                }


                if (destinationId.contains(":")) {
                    if (destinationId.contains("-1")) {
                        if (bpDpBoarding.size == 1) {
                            droppingPoint = bpDpDropping[0].name
                            PreferenceUtils.putObject(
                                bpDpDropping[0],
                                PREF_DROPPING_STAGE_DETAILS
                            )
                        } else {
                            PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)
                            droppingPoint = ""
                        }
                    } else {
                        Timber.d("flow check:destination1: $destinationId")
                        var tempDestination = ""
                        bpDpDropping.forEach {
                            Timber.d("flow check:destination2: ${it.name} == $destination")
                            if (destination?.contains(",") == true) {
                                val temp = destination?.split(",")
                                tempDestination = temp?.get(0)!!
                            }

                            if (it.name == tempDestination) {
                                droppingPoint = it.name
                                PreferenceUtils.putObject(
                                    bpDpDropping[0],
                                    PREF_DROPPING_STAGE_DETAILS
                                )
                                PreferenceUtils.putObject(
                                    bpDpDropping[0],
                                    SELECTED_DROPPING_DETAIL
                                )
                            }
                        }
                    }
                } else {
                    if (bpDpBoarding.size == 1) {
                        droppingPoint = bpDpDropping[0].name
                        PreferenceUtils.putObject(
                            bpDpDropping[0],
                            PREF_DROPPING_STAGE_DETAILS
                        )
                        PreferenceUtils.putObject(
                            bpDpDropping[0],
                            SELECTED_DROPPING_DETAIL
                        )
                    } else {
                        PreferenceUtils.putObject(
                            emptyDropping,
                            SELECTED_DROPPING_DETAIL
                        )
                        droppingPoint = ""
                    }

                }




                rapidBookingDialog = DialogUtils.editChartBpDp(
                    boardingPoint = boardingPoint,
                    droppingPoint = droppingPoint,
                    this,
                    this
                )!!

            } else {


                PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)
                callServiceApi()
            }

            saveSelectedAvailableRoute(position)
        } else if (view.id == R.id.layout_book_ticket || view.tag == getString(R.string.tag_book_seat)) {
            if (availableRoutesList.isNotEmpty()) {
                PreferenceUtils.setPreference(
                    "OriginDestinationCity",
                    "${availableRoutesList[position].origin}-${availableRoutesList[position].destination}"
                )

                val isApplyBPDPFare = availableRoutesList[position].is_apply_bp_dp_fare
                PreferenceUtils.putObject(isApplyBPDPFare, IS_APPLY_BP_DP_FARE)
                PreferenceUtils.setPreference("seatwiseFare", "fromBulkShiftPassenger")
                PreferenceUtils.putString("SelectionCoach", "BOOK")
                PreferenceUtils.putString("fromBusDetails", "bookBlock")
                PreferenceUtils.putString(PREF_TRAVEL_DATE, travelDate)
                if((sourceId=="0" || destinationId=="0")){
                    PreferenceUtils.setPreference(PREF_SOURCE_ID, availableRoutesList[position].origin_id)
                    PreferenceUtils.setPreference(PREF_DESTINATION_ID, availableRoutesList[position].destination_id)
                    PreferenceUtils.setPreference(
                        PREF_SOURCE,
                        availableRoutesList[position].origin
                    )
                    PreferenceUtils.setPreference(
                        PREF_DESTINATION,
                        availableRoutesList[position].destination
                    )
                    PreferenceUtils.setPreference(
                        "isAllToAllFlow",
                        true
                    )
                    PreferenceUtils.putString("serviceRouteOriginId",sourceId)
                    PreferenceUtils.putString("serviceRouteDestinationId",destinationId)
                }else{
                    PreferenceUtils.setPreference(
                        "isAllToAllFlow",
                        false
                    )
                    PreferenceUtils.putString("serviceRouteOriginId",availableRoutesList[position].origin_id)
                    PreferenceUtils.putString("serviceRouteDestinationId", availableRoutesList[position].destination_id)
                }

                removeKey("seatwiseFare")
                removeKey("isEditSeatWise")
                removeKey("PERSEAT")
                if (isApplyBPDPFare) {
                    bpDpDropping.clear()
                    bpDpBoarding.clear()
                    val boardingDetails = availableRoutesList[position].boarding_point_details
                    val dropOffDetails = availableRoutesList[position].drop_off_details
                    boardingDetails.forEach {
                        bpDpBoarding.add(it)
                    }
                    dropOffDetails.forEach {
                        bpDpDropping.add(it)
                    }
                    resId = availableRoutesList[position].reservation_id.toLong()
                    serviceApiType = getString(R.string.booking)
                    isPickupDropoffChargesEnabled = availableRoutesList[position].pickup_dropoff_charges_enabled ?: false
                    bpDpFlow(position, false)

                } else {

//                    val intent = Intent(this, NewCoachActivity::class.java)
                    val intent = Intent(this, NewCoachActivity::class.java)
                    intent.putExtra(REDIRECT_FROM, TAG)
                    intent.putExtra(getString(R.string.srp_service_selection_pos), position)
                    intent.putExtra("is_bima", availableRoutesList[position].is_bima)
                    startActivity(intent)
                }
//
                retrieveSelectedSeats().clear()
                seatDetailList.clear()
                retrieveSelectedExtraSeats().clear()
                selectedExtraSeatDetails.clear()
                getPassengerDetails().clear()
                passengerList.clear()
                retrieveSelectedPassengers().clear()
                saveSelectedAvailableRoute(position)
            }
            firebaseLogEvent(
                this,
                BOOK_TICKETS,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                BOOK_TICKETS,
                BookTickets.BOOK_TICKETS
            )
        } else if (view.tag == getString(R.string.tag_block_seat)) {

            if (availableRoutesList[position].is_apply_bp_dp_fare) {
                PreferenceUtils.putObject(
                    availableRoutesList[position].is_apply_bp_dp_fare,
                    IS_APPLY_BP_DP_FARE
                )

                bpDpDropping.clear()
                bpDpBoarding.clear()
                val boardingDetails = availableRoutesList[position].boarding_point_details
                val dropOffDetails = availableRoutesList[position].drop_off_details
                boardingDetails.forEach {
                    bpDpBoarding.add(it)
                }
                PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                dropOffDetails.forEach {
                    bpDpDropping.add(it)
                }
                PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                putBoarding(bpDpBoarding)
                putDropping(bpDpDropping)
                if (!availableRoutesList[position].is_service_blocked) {
                    val intent = Intent(this, BlockActivity::class.java)
                    intent.putExtra(getString(R.string.blocked), "")
                    PreferenceUtils.setPreference(getString(R.string.is_service_blocked), false)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, BlockActivity::class.java)
                    intent.putExtra(getString(R.string.blocked), getString(R.string.blocked))
                    startActivity(intent)
                    PreferenceUtils.setPreference(getString(R.string.is_service_blocked), true)
                }
            } else {

                if (!availableRoutesList[position].is_service_blocked) {
                    val intent = Intent(this, BlockActivity::class.java)
                    intent.putExtra(getString(R.string.blocked), "")
                    PreferenceUtils.setPreference(getString(R.string.is_service_blocked), false)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, BlockActivity::class.java)
                    intent.putExtra(getString(R.string.blocked), getString(R.string.blocked))
                    startActivity(intent)
                    PreferenceUtils.setPreference(getString(R.string.is_service_blocked), true)
                }
            }

            saveSelectedAvailableRoute(position)

            PreferenceUtils.putObject(
                availableRoutesList[position].is_apply_bp_dp_fare,
                IS_APPLY_BP_DP_FARE
            )
            PreferenceUtils.setPreference(PREF_SOURCE_ID, availableRoutesList[position].origin_id)
            PreferenceUtils.setPreference(
                PREF_DESTINATION_ID,
                availableRoutesList[position].destination_id
            )

            PreferenceUtils.setPreference("seatwiseFare", "fromBulkShiftPassenger")
            PreferenceUtils.setPreference("is_bima", availableRoutesList[position].is_bima)
            PreferenceUtils.putString("SelectionCoach", "BLOCK")
            PreferenceUtils.putString("fromBusDetails", "bookBlock")
            removeKey("seatwiseFare")
            removeKey("isEditSeatWise")
            removeKey("PERSEAT")
            saveSelectedAvailableRoute(position)

        } else if (view.tag == "BOOKING" && busStageData[position].title.contains("-")) {
            source = busStageData[position].title.split("-")[0]
            destination = busStageData[position].title.split("-")[1]
            sourceId = busStageData[position].origin_id ?: "0"
            destinationId = busStageData[position].destination_id.toString()

            PreferenceUtils.putString(PREF_SOURCE, source)
            PreferenceUtils.putString(PREF_SOURCE_ID, sourceId)
            PreferenceUtils.putString(PREF_DESTINATION, destination)
            PreferenceUtils.putString(PREF_DESTINATION_ID, destinationId)


            PreferenceUtils.putString(PREF_LAST_SEARCHED_SOURCE, source)
            PreferenceUtils.putString(PREF_LAST_SEARCHED_DESTINATION, destination)

            setToolbarTitle()
            if(sourceId=="0"||destinationId=="0"){
                pageNumber=1
                allToAllavailableRoutesApi()
            }else{
                availableRoutesApi()
            }
            updatedList.clear()
            filteredList.clear()
            binding.rgSleeperSeater.clearCheck()
            binding.rgAcNonAc.clearCheck()
            binding.rgPriceDeparture.clearCheck()
            defaultIconRgSleeperSeater()
            defaultIconRgAcNonAc()
            defaultIconRgPriceDeparture()
        } else if (view.tag == "BOOKING" && busStageData[position].title == getString(R.string.new_booking)) {
            updatedList.clear()
            filteredList.clear()
            binding.rgSleeperSeater.clearCheck()
            binding.rgAcNonAc.clearCheck()
            binding.rgPriceDeparture.clearCheck()
            defaultIconRgSleeperSeater()
            defaultIconRgAcNonAc()
            defaultIconRgPriceDeparture()
            PreferenceUtils.putString(
                getString(R.string.BACK_PRESS),
                getString(R.string.new_booking)
            )
            if (PreferenceUtils.getString(PREF_NEW_BOOKING_NAVIGATION) != null) {
                val navigationPage = PreferenceUtils.getString(PREF_NEW_BOOKING_NAVIGATION)
                if (navigationPage == FragmentBooking.TAG)
                    onBackPressed()
                else {
                    intent = Intent(this, DashboardNavigateActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("newBooking", true)
                    startActivity(intent)
                    finish()
                }
            }
            // onBackPressed()
            /*intent = Intent(this, DashboardNavigateActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("newBooking", true)
            startActivity(intent)
            finish()*/
            firebaseLogEvent(
                this,
                NEW_BOOKING,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                NEW_BOOKING,
                New_BOOKING_SRP
            )
        } else if (view.tag == "DATES") {
            ymdDate = inputFormatToOutput(
                dateList[position].title,
                DATE_FORMAT_MMM_DD_EEE_YYYY,
                DATE_FORMAT_Y_M_D
            ).replace("1970", getCurrentYear())
            travelDate = getDateDMY(ymdDate)!!
            PreferenceUtils.putString(PREF_TRAVEL_DATE, travelDate)

            binding.rvBusDetails.visibility = View.GONE
            setToolbarTitle()
            if(sourceId=="0"||destinationId=="0"){
                pageNumber=1
                allToAllavailableRoutesApi()
            }else{
                availableRoutesApi()
            }
            updatedList.clear()
            filteredList.clear()
            binding.rgSleeperSeater.clearCheck()
            binding.rgAcNonAc.clearCheck()
            binding.rgPriceDeparture.clearCheck()
            defaultIconRgSleeperSeater()
            defaultIconRgAcNonAc()
            defaultIconRgPriceDeparture()

        }else if(view.tag=="loadNextPage"){

            pageNumber++
            if (pageNumber < totalPage.plus(1)) {
                binding.rvBusDetails.gone()
                allToAllavailableRoutesApi()
            }



        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    private fun saveSelectedAvailableRoute(position: Int) {
        try {
            if (position < availableRoutesList.size) {

                PreferenceUtils.setPreference(
                    PREF_AVAILABLE_ROUTES_ITEM_IS_SERVICE_BLOCKED,
                    availableRoutesList[position].is_service_blocked
                )

                PreferenceUtils.setPreference(
                    PREF_RESERVATION_ID,
                    availableRoutesList[position].reservation_id
                )
                PreferenceUtils.putObject(
                    availableRoutesList[position], PREF_SELECTED_AVAILABLE_ROUTES
                )

                PreferenceUtils.putString((PREF_COACH_NUMBER), availableRoutesList[position].number)

                PreferenceUtils.setPreference(PREF_PICKUP_DROPOFF_CHARGES_ENABLED, availableRoutesList[position].pickup_dropoff_charges_enabled)
            }
        } catch (_: ArrayIndexOutOfBoundsException) {
        }
    }

    override fun onClickOfItem(data: String, position: Int) {

    }


    private fun bpDpFlow(position: Int, fromEditChart: Boolean) {

        if (!sourceId.contains("-1") && !destinationId.contains("-1")) {


            if (!sourceId.contains(":") || !destinationId.contains(":")) {
                if (bpDpBoarding.size == 1 && bpDpDropping.size == 1 && isPickupDropoffChargesEnabled == false) {
                    PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                    PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                    val intent = Intent(this, NewCoachActivity::class.java)
                    intent.putExtra(REDIRECT_FROM, TAG)
                    startActivity(intent)
                } else {

                    PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)
                    PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)
                    val intent = Intent(this, InterBDActivity::class.java)
                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    intent.putExtra("bpdpEditChart", fromEditChart.toString())
                    intent.putExtra("PreSelectedDropping", "false")
                    intent.putExtra("preSelectedBoarding", "false")
                    intent.putExtra(
                        getString(R.string.bus_type),
                        availableRoutesList[position].bus_type
                    )
                    intent.putExtra(
                        getString(R.string.dep_time),
                        availableRoutesList[position].dep_time
                    )
                    intent.putExtra(getString(R.string.service_number), serviceNumber)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        "${getString(R.string.booking)}"
                    )
                    intent.putExtra(
                        getString(R.string.service_type),
                        getString(R.string.proceed)
                    )
                    startActivity(intent)
                }

            } else {


                var tempSource = ""
                var tempDestination = ""
                if (source?.contains(",") == true) {
                    val temp = source!!.split(",")
                    tempSource = temp[0]
                }
                if (destination?.contains(",") == true) {
                    val temp = destination!!.split(",")
                    tempDestination = temp[0]
                }
                bpDpBoarding.forEach {
                    if (it.name == tempSource) {
                        PreferenceUtils.putObject(it, SELECTED_BOARDING_DETAIL)
                    }
                }
                bpDpDropping.forEach {
                    if (it.name == tempDestination) {
                        PreferenceUtils.putObject(it, SELECTED_DROPPING_DETAIL)
                    }
                }
                val intent = Intent(this, NewCoachActivity::class.java)
                intent.putExtra(REDIRECT_FROM, TAG)
                startActivity(intent)
            }

        } else if (sourceId.contains("-1") && !destinationId.contains("-1")) {


            if (bpDpBoarding.size == 1 && bpDpDropping.size == 1 && isPickupDropoffChargesEnabled == false) {
                PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                val intent = Intent(this, NewCoachActivity::class.java)
                intent.putExtra(REDIRECT_FROM, TAG)
                startActivity(intent)
            } else {
                if (bpDpBoarding.size > 1 && bpDpDropping.size == 1 && isPickupDropoffChargesEnabled == false) {

                    val intent = Intent(this, InterBDActivity::class.java)
                    PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)

                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    intent.putExtra("bpdpEditChart", fromEditChart.toString())

                    intent.putExtra("PreSelectedDropping", "false")
                    intent.putExtra("preSelectedBoarding", "false")
                    intent.putExtra(
                        getString(R.string.bus_type),
                        availableRoutesList[position].bus_type
                    )
                    intent.putExtra(
                        getString(R.string.dep_time),
                        availableRoutesList[position].dep_time
                    )
                    intent.putExtra(getString(R.string.service_number), serviceNumber)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        "${getString(R.string.booking)}"
                    )
                    intent.putExtra(
                        getString(R.string.service_type),
                        getString(R.string.proceed)
                    )
                    startActivity(intent)
                } else if (bpDpBoarding.size == 1 && bpDpDropping.size > 1 && isPickupDropoffChargesEnabled == false) {

                    var tempSource = ""
                    if (destination?.contains(",") == true) {
                        val temp = destination!!.split(",")
                        tempSource = temp[0]
                    }
                    bpDpDropping.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_DROPPING_DETAIL)
                        }
                    }
                    PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                    val intent = Intent(this, NewCoachActivity::class.java)
                    intent.putExtra(REDIRECT_FROM, TAG)
                    startActivity(intent)
                } else if ((bpDpBoarding.size > 1 && bpDpDropping.size > 1) ||
                    (bpDpBoarding.size >= 1 && bpDpDropping.size >= 1 && isPickupDropoffChargesEnabled == true))
                {
                    PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)

                    val intent = Intent(this, InterBDActivity::class.java)
                    intent.putExtra("bpdpEditChart", fromEditChart.toString())

                    intent.putExtra("PreSelectedDropping", "true")
                    intent.putExtra("preSelectedBoarding", "false")
                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    var tempSource = ""
                    if (destination?.contains(",") == true) {
                        val temp = destination!!.split(",")
                        tempSource = temp[0]
                    }
                    bpDpDropping.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_DROPPING_DETAIL)

                        }
                    }

                    intent.putExtra(
                        getString(R.string.bus_type),
                        availableRoutesList[position].bus_type
                    )
                    intent.putExtra(
                        getString(R.string.dep_time),
                        availableRoutesList[position].dep_time
                    )
                    intent.putExtra(getString(R.string.service_number), serviceNumber)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        "${getString(R.string.booking)}"
                    )
                    intent.putExtra(
                        getString(R.string.service_type),
                        getString(R.string.proceed)
                    )
                    startActivity(intent)
                }

            }
        } else if (sourceId.contains("-1") && destinationId.contains("-1")) {

            if (bpDpBoarding.size == 1 && bpDpDropping.size == 1 && isPickupDropoffChargesEnabled == false) {
                PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                val intent = Intent(this, NewCoachActivity::class.java)
                intent.putExtra(REDIRECT_FROM, TAG)
                startActivity(intent)
            } else {
                val intent = Intent(this, InterBDActivity::class.java)
                PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)
                putBoarding(bpDpBoarding)
                putDropping(bpDpDropping)
                intent.putExtra("bpdpEditChart", fromEditChart.toString())

                intent.putExtra("PreSelectedDropping", "false")
                intent.putExtra("preSelectedBoarding", "false")
                intent.putExtra(
                    getString(R.string.bus_type),
                    availableRoutesList[position].bus_type
                )
                intent.putExtra(
                    getString(R.string.dep_time),
                    availableRoutesList[position].dep_time
                )
                intent.putExtra(getString(R.string.service_number), serviceNumber)
                intent.putExtra(
                    getString(R.string.toolbar_title),
                    "${getString(R.string.booking)}"
                )
                intent.putExtra(
                    getString(R.string.service_type),
                    getString(R.string.proceed)
                )
                startActivity(intent)
            }

        } else if (!sourceId.contains("-1") && destinationId.contains("-1")) {

            if (bpDpBoarding.size == 1 && bpDpDropping.size == 1 && isPickupDropoffChargesEnabled == false) {
                PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                val intent = Intent(this, NewCoachActivity::class.java)
                intent.putExtra(REDIRECT_FROM, TAG)
                startActivity(intent)
            } else {
                var tempSource = ""
                if (bpDpBoarding.size > 1 && bpDpDropping.size == 1 && isPickupDropoffChargesEnabled == false) {
                    if (source?.contains(",") == true) {
                        val temp = source!!.split(",")
                        tempSource = temp[0]
                    }
                    bpDpBoarding.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_BOARDING_DETAIL)
                            intent.putExtra(getString(R.string.select_boarding_point), true)
                        }
                    }
                    PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                    val intent = Intent(this, NewCoachActivity::class.java)
                    intent.putExtra(REDIRECT_FROM, TAG)
                    startActivity(intent)
                } else if (bpDpBoarding.size == 1 && bpDpDropping.size > 1 && isPickupDropoffChargesEnabled == false) {
                    PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)

                    val intent = Intent(this, InterBDActivity::class.java)
                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    intent.putExtra("bpdpEditChart", fromEditChart.toString())

                    intent.putExtra("PreSelectedDropping", "false")
                    intent.putExtra("preSelectedBoarding", "false")
                    intent.putExtra(
                        getString(R.string.bus_type),
                        availableRoutesList[position].bus_type
                    )
                    intent.putExtra(
                        getString(R.string.dep_time),
                        availableRoutesList[position].dep_time
                    )
                    intent.putExtra(getString(R.string.service_number), serviceNumber)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        "${getString(R.string.booking)}"
                    )
                    intent.putExtra(
                        getString(R.string.service_type),
                        getString(R.string.proceed)
                    )
                    startActivity(intent)

                } else if ((bpDpBoarding.size > 1 && bpDpDropping.size > 1) ||
                    (bpDpBoarding.size >= 1 && bpDpDropping.size >= 1 && isPickupDropoffChargesEnabled == true) )
                 {
                    PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)

                    val intent = Intent(this, InterBDActivity::class.java)
                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    intent.putExtra("bpdpEditChart", fromEditChart.toString())

                    intent.putExtra("PreSelectedDropping", "false")
                    intent.putExtra("preSelectedBoarding", "true")
                    if (source?.contains(",") == true) {
                        val temp = source!!.split(",")
                        tempSource = temp[0]
                    }
                    bpDpBoarding.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_BOARDING_DETAIL)
                        }
                    }
                    intent.putExtra(
                        getString(R.string.bus_type),
                        availableRoutesList[position].bus_type
                    )
                    intent.putExtra(
                        getString(R.string.dep_time),
                        availableRoutesList[position].dep_time
                    )
                    intent.putExtra(getString(R.string.service_number), serviceNumber)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        "${getString(R.string.booking)}"
                    )
                    intent.putExtra(
                        getString(R.string.service_type),
                        getString(R.string.proceed)
                    )
                    startActivity(intent)
                }

            }
        }
    }


    @SuppressLint("LogNotTimber")
    override fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        busData: Result,
    ) {
        removeKey(PREF_BOARDING_STAGE_DETAILS)
        removeKey(PREF_DROPPING_STAGE_DETAILS)
        saveSelectedAvailableRoute(itemPosition)
        resId = availableRoutesList[itemPosition].reservation_id
        busType = availableRoutesList[itemPosition].bus_type
        depTime = availableRoutesList[itemPosition].dep_time
        serviceNumber = availableRoutesList[itemPosition].number
        when (menuPosition) {
            1 -> {
                firebaseLogEvent(
                    this,
                    RAPID_BOOK,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    RAPID_BOOK,
                    RapidBook.RAPID_BOOK
                )
                isApplybpDpEditChart = 1
                if (PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL) != null) {
                    oldBoarding =
                        PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL)!!
                }
                if (PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL) != null) {
                    oldDroping =
                        PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL)!!
                }

                isApplybdDpRapid = availableRoutesList[itemPosition].is_apply_bp_dp_fare
                serviceApiType = getString(R.string.rapid_booking)
                bpDpBoarding =
                    availableRoutesList[itemPosition].boarding_point_details as MutableList<BoardingPointDetail>
                bpDpDropping =
                    availableRoutesList[itemPosition].drop_off_details as MutableList<DropOffDetail>

                if (sourceId.contains(":")) {
                    Timber.d("flow check:1: $sourceId")

                    if (sourceId.contains("-1")) {
                        Timber.d("flow check:4: $sourceId")
                        if (bpDpBoarding.size == 1) {
                            boardingPoint = bpDpBoarding[0].name
                            PreferenceUtils.putObject(
                                bpDpBoarding[0],
                                PREF_BOARDING_STAGE_DETAILS
                            )
                        } else {
                            PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)
                            boardingPoint = ""
                        }
                    } else {
                        Timber.d("flow check:3: $sourceId")
                        var tempSource = ""
                        bpDpBoarding.forEach {
                            Timber.d("flow check:3.1: ${it.name} == $source")
                            if (source?.contains(",") == true) {
                                val temp = source?.split(",")
                                tempSource = temp?.get(0)!!
                            }

                            if (it.name == tempSource) {
                                boardingPoint = it.name
                                val boardingPointIndex =
                                    bpDpBoarding.indexOfFirst { it.name == tempSource }
                                if (boardingPointIndex != -1) {
                                    PreferenceUtils.putObject(
                                        bpDpBoarding[boardingPointIndex],
                                        PREF_BOARDING_STAGE_DETAILS
                                    )
                                    PreferenceUtils.putObject(
                                        bpDpBoarding[boardingPointIndex],
                                        SELECTED_BOARDING_DETAIL
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Timber.d("flow check:2: $sourceId")
                    if (bpDpBoarding.size == 1) {
                        boardingPoint = bpDpBoarding[0].name
                        PreferenceUtils.putObject(
                            bpDpBoarding[0],
                            PREF_BOARDING_STAGE_DETAILS
                        )
                        PreferenceUtils.putObject(
                            bpDpBoarding[0],
                            SELECTED_BOARDING_DETAIL
                        )
                    } else {
                        PreferenceUtils.putObject(
                            emptyBoarding,
                            SELECTED_BOARDING_DETAIL
                        )
                        boardingPoint = ""
                    }
                }


                if (destinationId.contains(":")) {

                    if (destinationId.contains("-1")) {
                        if (bpDpDropping.size == 1) {
                            droppingPoint = bpDpDropping[0].name
                            PreferenceUtils.putObject(
                                bpDpDropping[0],
                                PREF_DROPPING_STAGE_DETAILS
                            )
                        } else {
                            PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)
                            droppingPoint = ""
                        }
                    } else {
                        var tempDestination = ""
                        bpDpDropping.forEach {
                            if (destination?.contains(",") == true) {
                                val temp = destination?.split(",")
                                tempDestination = temp?.get(0)!!
                            }

                            if (it.name == tempDestination) {
                                droppingPoint = it.name
                                PreferenceUtils.putObject(
                                    bpDpDropping[0],
                                    PREF_DROPPING_STAGE_DETAILS
                                )
                                PreferenceUtils.putObject(
                                    bpDpDropping[0],
                                    SELECTED_DROPPING_DETAIL
                                )
                            }
                        }
                    }
                } else {
                    if (bpDpDropping.size == 1) {
                        droppingPoint = bpDpDropping[0].name
                        PreferenceUtils.putObject(
                            bpDpDropping[0],
                            PREF_DROPPING_STAGE_DETAILS
                        )
                        PreferenceUtils.putObject(
                            bpDpDropping[0],
                            SELECTED_DROPPING_DETAIL
                        )
                    } else {
                        PreferenceUtils.putObject(
                            emptyDropping,
                            SELECTED_DROPPING_DETAIL
                        )
                        droppingPoint = ""
                    }

                }


//                if (bpDpBoarding != null && bpDpBoarding!!.isNotEmpty()) {
//                    boardingPoint = bpDpBoarding!![0].name!!
//                    PreferenceUtils.putObject(bpDpBoarding!![0],
//                        PREF_BOARDING_STAGE_DETAILS)
//                }
//                if (bpDpDropping != null && bpDpDropping!!.isNotEmpty()) {
//                    droppingPoint = bpDpDropping!![0].name!!
//                    PreferenceUtils.putObject(bpDpDropping!![0],
//                        PREF_DROPPING_STAGE_DETAILS)
//                }


                PreferenceUtils.setPreference(
                    getString(R.string.rapid_booking_item_position),
                    itemPosition
                )

                if (isMotAllowed) {
                    val intent =
                        Intent(this, MotCouponActivity::class.java)
                    putBoarding(bpDpBoarding)
                    putDropping(bpDpDropping)
                    intent.putExtra("busType", busType)
                    intent.putExtra("serviceNumber", serviceNumber)
                    intent.putExtra("depTime", depTime)
                    intent.putExtra("travelDate", travelDate)
                    intent.putExtra("resId12", resId.toString())
                    startActivity(intent)
                } else {
                    rapidBookingDialog = DialogUtils.rapidBookingDialog(
                        boardingPoint = boardingPoint,
                        droppingPoint = droppingPoint,
                        position = itemPosition,
                        this,
                        object : VarArgListener {
                            override fun onButtonClick(vararg args: Any) {
                                if (args[0] == getString(R.string.confirm)) {

                                    val availableSeatsCount =
                                        availableRoutesList[itemPosition].available_seats
                                    noOfTickets = ""
                                    noOfTickets = args[1] as String

                                    serviceApiType = getString(R.string.rapid_booking)

//                val availableSeatLength = availableSeatsCount.toString().length

                                    try {
                                        if (noOfTickets.toInt() > availableSeatsCount) {
                                            toast(getString(R.string.rapid_booking_seat_count))
                                        } else if (noOfTickets.toInt() == 0) {
                                            toast("Please enter valid number of tickets")
                                        } else {
                                            sourceId=availableRoutesList[itemPosition].origin_id
                                            destinationId=availableRoutesList[itemPosition].destination_id
                                            if (isApplybdDpRapid) {
                                                val dropOff =
                                                    PreferenceUtils.getObject<DropOffDetail>(
                                                        PREF_DROPPING_STAGE_DETAILS
                                                    )
                                                val boardingAt =
                                                    PreferenceUtils.getObject<BoardingPointDetail>(
                                                        PREF_BOARDING_STAGE_DETAILS
                                                    )
                                                callBpDpServiceApi(
                                                    boardingAt?.id.toString(),
                                                    dropOff?.id.toString()
                                                )
                                            } else {
                                                callServiceApi(availableRoutesList[itemPosition].origin_id,availableRoutesList[itemPosition].destination_id)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        toast(getString(R.string.rapid_booking_seat_count))
                                    }


                                } else if (args[0] == getString(R.string.boarding_at)) {
                                    //Timber.d("isbpdpApplyFare:2: $isApplybdDpRapid")
                                    val intent =
                                        Intent(this@BusDetailsActivity, InterBDActivity::class.java)
                                    intent.putExtra(
                                        getString(R.string.tag),
                                        getString(R.string.boarding)
                                    )
                                    putBoarding(bpDpBoarding)
                                    putDropping(bpDpDropping)
                                    intent.putExtra(getString(R.string.bus_type), busType)
                                    intent.putExtra(getString(R.string.dep_time), depTime)
                                    intent.putExtra(
                                        getString(R.string.toolbar_title),
                                        getString(R.string.rapid_booking)
                                    )
                                    intent.putExtra("sourceKey", "fromBusDetails")
                                    startActivity(intent)
                                } else if (args[0] == getString(R.string.drop_off_at)) {
                                    //Timber.d("isbpdpApplyFare:2: $isApplybdDpRapid")
                                    val intent =
                                        Intent(this@BusDetailsActivity, InterBDActivity::class.java)
                                    intent.putExtra(
                                        getString(R.string.tag),
                                        getString(R.string.dropping)
                                    )
                                    putBoarding(bpDpBoarding)
                                    putDropping(bpDpDropping)
                                    intent.putExtra(getString(R.string.bus_type), busType)
                                    intent.putExtra(getString(R.string.dep_time), depTime)
                                    intent.putExtra(
                                        getString(R.string.toolbar_title),
                                        getString(R.string.rapid_booking)
                                    )
                                    intent.putExtra("sourceKey", "fromBusDetails")
                                    startActivity(intent)
                                } else if (args[0] == getString(R.string.cancel)) {
                                    if (::oldBoarding.isInitialized)
                                        PreferenceUtils.putObject(
                                            oldBoarding,
                                            SELECTED_BOARDING_DETAIL
                                        )
                                    if (::oldDroping.isInitialized)
                                        PreferenceUtils.putObject(
                                            oldDroping,
                                            SELECTED_DROPPING_DETAIL
                                        )
                                }
                            }

                        }
                    )!!
                }


            }

            3 -> {
                PreferenceUtils.putString(PREF_SOURCE, availableRoutesList[itemPosition].origin)
                            PreferenceUtils.putString(PREF_SOURCE_ID, availableRoutesList[itemPosition].origin_id)
                            PreferenceUtils.putString(PREF_DESTINATION, availableRoutesList[itemPosition].destination)
                            PreferenceUtils.putString(PREF_DESTINATION_ID, availableRoutesList[itemPosition].destination_id)

                firebaseLogEvent(
                    this,
                    BOOKINGPG_SEND_SMS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    BOOKINGPG_SEND_SMS,
                    BookingPGSendSms.SEND_SMS
                )
                removeKey(PREF_EMPLOYEE_TYPE_OPTIONS)
                removeKey(PREF_SMS_TEMPLATE)
                removeKey(PREF_CHECKED_PNR)
                removeKey(PREF_SMS_PASSENGER_TYPE)
                val intent = Intent(this, SmsNotificationActivity::class.java)
                startActivity(intent)
            }

            4 -> {
                // val busDetails = "$travelDate $source - $destination $busType"
                firebaseLogEvent(
                    this,
                    BOOKINGPG_UPDATE_DETAILS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    BOOKINGPG_UPDATE_DETAILS,
                    BookingPGUpdateDetails.UPDATE_DETAILS
                )
                val busDetails = "$serviceNumber | ${getDateDMYY(travelDate)} $depTime | $busType"
                val intent = Intent(context, ServiceDetailsActivity::class.java)
                intent.putExtra(context.getString(R.string.origin), source)
                intent.putExtra(context.getString(R.string.destination), destination)
                intent.putExtra(context.getString(R.string.bus_type), busDetails)

                removeKey(context.getString(R.string.scannedUserName))
                removeKey(context.getString(R.string.scannedUserId))
                removeKey("selectedScanType")
                removeKey(context.getString(R.string.scan_coach))
                removeKey(context.getString(R.string.scan_driver_1))
                removeKey(context.getString(R.string.scan_driver_2))
                removeKey(context.getString(R.string.scan_cleaner))
                removeKey(context.getString(R.string.scan_contractor))

                context.startActivity(intent)
            }

            5 -> {
                Timber.d("isSingleBlockUnblock ${busData.isSingleBlockUnblock}")

                if (busData.isSingleBlockUnblock.isNotEmpty()) {
                    if (busData.isSingleBlockUnblock == getString(R.string.block)) {
                        DialogUtils.releaseTicketDialog(
                            this,
                            "",
                            "",
                            this,
                            isSingleBlockUnblock = true,
                            blockReasonsList
                        )
                        firebaseLogEvent(
                            context,
                            BLOCK_UNBLOCK,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            BLOCK_UNBLOCK,
                            BlockUnblock.BLOCK
                        )

                    } else {
                        sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

                            if (isNetworkAvailable()) {
                                if (shouldSingleBlockUnblock && privilegeResponse?.country.equals(
                                        "india",
                                        true
                                    )
                                ) {
                                    DialogUtils.showFullHeightPinInputBottomSheet(
                                        activity = this@BusDetailsActivity,
                                        fragmentManager = supportFragmentManager,
                                        pinSize = pinSize,
                                        getString(R.string.single_block_unblock),
                                        onPinSubmitted = { pin: String ->
                                            callSingleBlockUnblock(
                                                resId = resId.toString(),
                                                isBlock = false,
                                                "",
                                                pin
                                            )
                                            binding.includeProgress.progressBar.gone()
                                        },
                                        onDismiss = {
                                            binding.includeProgress.progressBar.gone()
                                        }
                                    )
                                } else {
                                    callSingleBlockUnblock(
                                        resId = resId.toString(),
                                        isBlock = false,
                                        "",
                                        ""
                                    )
                                    binding.includeProgress.progressBar.gone()
                                }
                            } else
                                noNetworkToast()
                        }

                    }
                }

                firebaseLogEvent(
                    this,
                    BLOCK_RESERVATION,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    BLOCK_RESERVATION,
                    BlockReservation.BLOCK
                )
            }

            6 -> {
                serviceApiType = getString(R.string.edit_chart)

                bpDpBoarding =
                    availableRoutesList[itemPosition].boarding_point_details as MutableList<BoardingPointDetail>
                bpDpDropping =
                    availableRoutesList[itemPosition].drop_off_details as MutableList<DropOffDetail>

                putBoarding(bpDpBoarding)
                putDropping(bpDpDropping)

                Timber.d("bpDpBoarding ${bpDpBoarding.size} bpDpDropping ${bpDpDropping.size}")

                if (bpDpBoarding.size == 1 && bpDpDropping.size == 1) {
                    PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                    PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                }

                if (isApplybdDpRapid) {
                    serviceApiType
                    val dropOff =
                        PreferenceUtils.getObject<DropOffDetail>(PREF_DROPPING_STAGE_DETAILS)
                    val boardingAt =
                        PreferenceUtils.getObject<BoardingPointDetail>(PREF_BOARDING_STAGE_DETAILS)

                    callBpDpServiceApi(boardingAt?.id.toString(), dropOff?.id.toString())
                } else {
                    callServiceApi()
                }
            }

            10 -> {
                val intent = Intent(this, FrequentTravellerDataActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun callDeleteRecentSearchApi(position: Int) {
        try {
            selectedOriginId = busStageData[position].origin_id ?: "0"
            selectedDestinationId = busStageData[position].destination_id?:"0"
            val reqBody = com.bitla.ts.domain.pojo.delete_recent_search.request.ReqBody(
                loginModelPref.api_key,
                selectedOriginId.toString(),
                selectedDestinationId.toString(),
                locale = locale
            )
        } catch (ex: Exception) {

        }
    }


    @Deprecated("Deprecated in Java")
    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        if (resultCode == Activity.RESULT_OK) {
//            if (data?.getStringExtra(getString(R.string.SCREEN_TAG)) != null) {
//                var tag: String = data.getStringExtra(getString(R.string.SCREEN_TAG))!!
//
//                /*  if (CalendarActivity.TAG == tag) {
//                      var selectedDate: String =
//                          data?.getStringExtra(getString(R.string.CALENDER_DATE)).toString()
//
//                      travelDate = selectedDate
//                      binding.rvBusDetails.visibility = View.GONE
//                      setToolbarTitle()
//                      availableRoutesViewModel.getNextCalenderDates(getTodayDate(), travelDate)
//
//                      ymdDate = inputFormatToOutput(
//                          travelDate,
//                          DATE_FORMAT_D_M_Y,
//                          DATE_FORMAT_Y_M_D
//                      )!!
//                      availableRoutesApi()
//                  }*/
//            }
//        }


        //New Booking Flow

        if (resultCode == Activity.RESULT_OK) {

            if (data?.getStringExtra(getString(R.string.SELECTED_CITY_TYPE)) != null) {
                if (data.getStringExtra(getString(R.string.SELECTED_CITY_TYPE)) != getString(R.string.select_service)) {
                    data.getStringExtra(getString(R.string.SELECTED_CITY_TYPE))!!
                    services = ""

                    val selectedCityType: String =
                        data.getStringExtra(getString(R.string.SELECTED_CITY_TYPE)).toString()
                    val selectedCityName: String =
                        data.getStringExtra(getString(R.string.SELECTED_CITY_NAME)).toString()
                    if (selectedCityType == getString(R.string.selectSource)) {
                        finalSourceId =
                            data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                        sourceId = finalSourceId
                        PreferenceUtils.putString(PREF_SOURCE_ID, finalSourceId)
                    } else if (selectedCityType == getString(R.string.selectDestination)) {
                        finalDestinationId =
                            data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                        destinationId = finalDestinationId
                        PreferenceUtils.putString(
                            PREF_DESTINATION_ID,
                            finalDestinationId
                        )
                    }
//                    if (selectedCityType == getString(R.string.selectSource)) {
//                        finalSourceId= data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
//                    }
//                    else if (selectedCityType == getString(R.string.selectDestination)) {
//                        finalDestinationId= data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
//                    }
                    var selectedCityId: String = ""

                    if (data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                            .contains(":")
                    ) {
                        if (data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                                .contains("-1")
                        ) {
                            var temp =
                                data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                                    .split(":")
                            selectedCityId = temp[1]
                        } else {
                            var temp =
                                data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                                    .split(":")
                            selectedCityId = temp[0]
                        }
                    } else {
                        selectedCityId =
                            data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                    }
                    if (selectedCityType == getString(R.string.selectSource)) {
                        selectedOrigin = selectedCityId

                        PreferenceUtils.putString(PREF_SOURCE, selectedCityName)
                        PreferenceUtils.putString(PREF_SOURCE_ID, finalSourceId)

                        binding.modifySearchLayout.tvSource.text = selectedCityName
                        binding.modifySearchLayout.tvDestination.text = ""
                    } else if (selectedCityType == getString(R.string.selectDestination)) {
                        binding.modifySearchLayout.busServices.text = ""

                        PreferenceUtils.putString(
                            PREF_DESTINATION,
                            selectedCityName
                        )
                        PreferenceUtils.putString(
                            PREF_DESTINATION_ID,
                            finalDestinationId
                        )

                        binding.modifySearchLayout.tvDestination.text = selectedCityName
                    }


                } else {

                    services =
                        data.getStringExtra(getString(R.string.SELECTED_CITY_NAME)).toString()
                    binding.modifySearchLayout.busServices.text = services
                    if (data.getStringExtra(getString(R.string.bus_type)) != null) {
                        serviceBusType = data.getStringExtra(getString(R.string.bus_type))
                    }
                    if (data.getStringExtra(getString(R.string.service_number)) != null)
                        service_ServiceNUmber =
                            data.getStringExtra(getString(R.string.service_number))
                    if (data.getStringExtra(getString(R.string.dep_time)) != null)
                        serviceDepTime = data.getStringExtra(getString(R.string.dep_time))
                    if (data.getStringExtra(getString(R.string.toolbar_title)) != null)
                        service_title = data.getStringExtra(getString(R.string.toolbar_title))
                    if (data.getStringExtra(getString(R.string.service_type)) != null)
                        serviceType = data.getStringExtra(getString(R.string.service_type))

//                    binding.busServices.setText("${data?.getStringExtra(getString(R.string.SELECTED_CITY_NAME))}")

                }
            }
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (intent.hasExtra("fromTicketDetails")) {
            PreferenceUtils.putString(
                getString(R.string.BACK_PRESS),
                getString(R.string.new_booking)
            )
            intent = Intent(this, DashboardNavigateActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("newBooking", true)
            startActivity(intent)
            finish()
        }
        storePrefs()
        //   removeKey(PREF_SOURCE)
        // removeKey(PREF_DESTINATION)
        //removeKey(travelDate)
        //removeKey(sourceId)
        //removeKey(destinationId)
    }

    private fun storePrefs() {
        PreferenceUtils.setPreference(PREF_SOURCE, source)
        PreferenceUtils.setPreference(PREF_DESTINATION, destination)
        PreferenceUtils.setPreference(PREF_TRAVEL_DATE, travelDate)
        PreferenceUtils.setPreference(PREF_SOURCE_ID, sourceId)
        PreferenceUtils.setPreference(PREF_DESTINATION_ID, destinationId)
    }


    override fun onCancelled() {
    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?,
        hours: Int,
        minutes: Int,
    ) {
        if (!isFromModifyDatePopup!!) {
            if (firstDate != null) {
                if (secondDate == null) {
                    firstDate.set(Calendar.HOUR_OF_DAY, hours)
                    firstDate.set(Calendar.MINUTE, minutes)

                    refreshPageOnDateSelection(firstDate)

                }
            }
        } else {

            firstDate!!.set(Calendar.HOUR_OF_DAY, hours)
            firstDate.set(Calendar.MINUTE, minutes)
            refreshPageOnDateSelection(firstDate)


        }
    }

    private fun refreshPageOnDateSelection(firstDate: Calendar) {
        travelDate = SimpleDateFormat(
            DATE_FORMAT_D_M_Y,
            Locale.getDefault()
        ).format(firstDate.time)
        PreferenceUtils.setPreference(PREF_TRAVEL_DATE, travelDate)
        binding.rvBusDetails.gone()
        setToolbarTitle()


        ymdDate = inputFormatToOutput(
            travelDate,
            DATE_FORMAT_D_M_Y,
            DATE_FORMAT_Y_M_D
        )
        sevenDaysDate = travelDate
        availableRoutesViewModel.getNextCalenderDates(sevenDaysDate, travelDate)
        updatedList.clear()
        filteredList.clear()
        binding.rgSleeperSeater.clearCheck()
        binding.rgAcNonAc.clearCheck()
        binding.rgPriceDeparture.clearCheck()
        defaultIconRgSleeperSeater()
        defaultIconRgAcNonAc()
        defaultIconRgPriceDeparture()
        if(sourceId=="0"||destinationId=="0"){
            pageNumber=1
            allToAllavailableRoutesApi()
        }else{
            availableRoutesApi()
        }
    }


    override fun onButtonClick(vararg args: Any) {

        if (args.isNotEmpty()) {

            if (args[0] == getString(R.string.confirm)) {

                var availableSeatsCount = 0
                var position = 0
                noOfTickets = ""
                noOfTickets = args[1] as String
                position = args[2] as Int

                availableSeatsCount = availableRoutesList[position].available_seats

                serviceApiType = getString(R.string.rapid_booking)

//                val availableSeatLength = availableSeatsCount.toString().length

                try {
                    if (noOfTickets.toInt() > availableSeatsCount) {
                        toast(getString(R.string.rapid_booking_seat_count))
                    } else if (noOfTickets.toInt() == 0) {
                        toast("Please enter valid number of tickets")
                    } else {
                        if (isApplybdDpRapid) {
                            val dropOff =
                                PreferenceUtils.getObject<DropOffDetail>(PREF_DROPPING_STAGE_DETAILS)
                            val boardingAt = PreferenceUtils.getObject<BoardingPointDetail>(
                                PREF_BOARDING_STAGE_DETAILS
                            )
                            callBpDpServiceApi(boardingAt?.id.toString(), dropOff?.id.toString())
                        } else {
                            callServiceApi(availableRoutesList[position].origin_id,availableRoutesList[position].destination_id)
                        }
                    }
                } catch (e: Exception) {
                    toast(getString(R.string.rapid_booking_seat_count))
                }


            } else if (args[0] == getString(R.string.edit_chart)) {
                serviceApiType = getString(R.string.edit_chart)

                if (isApplybdDpRapid) {
                    serviceApiType
                    val dropOff =
                        PreferenceUtils.getObject<DropOffDetail>(PREF_DROPPING_STAGE_DETAILS)
                    val boardingAt =
                        PreferenceUtils.getObject<BoardingPointDetail>(PREF_BOARDING_STAGE_DETAILS)

                    callBpDpServiceApi(boardingAt?.id.toString(), dropOff?.id.toString())
                } else {
                    callServiceApi()
                }

            } else if (args[0] == getString(R.string.boarding_at)) {
                Timber.d("isbpdpApplyFare:2: $isApplybdDpRapid")
                val intent = Intent(this, InterBDActivity::class.java)
                intent.putExtra(getString(R.string.tag), getString(R.string.boarding))
                putBoarding(bpDpBoarding)
                putDropping(bpDpDropping)
                intent.putExtra(getString(R.string.bus_type), busType)
                intent.putExtra(getString(R.string.dep_time), depTime)
                intent.putExtra(
                    getString(R.string.toolbar_title),
                    getString(R.string.rapid_booking)
                )
                intent.putExtra("sourceKey", "fromBusDetails")
                startActivity(intent)
            } else if (args[0] == getString(R.string.drop_off_at)) {
                Timber.d("isbpdpApplyFare:2: $isApplybdDpRapid")
                val intent = Intent(this, InterBDActivity::class.java)
                intent.putExtra(getString(R.string.tag), getString(R.string.dropping))
                putBoarding(bpDpBoarding)
                putDropping(bpDpDropping)
                intent.putExtra(getString(R.string.bus_type), busType)
                intent.putExtra(getString(R.string.dep_time), depTime)
                intent.putExtra(
                    getString(R.string.toolbar_title),
                    getString(R.string.rapid_booking)
                )
                intent.putExtra("sourceKey", "fromBusDetails")
                startActivity(intent)
            } else if (args[0] == getString(R.string.cancel)) {
                if (::oldBoarding.isInitialized)
                    PreferenceUtils.putObject(oldBoarding, SELECTED_BOARDING_DETAIL)
                if (::oldDroping.isInitialized)
                    PreferenceUtils.putObject(oldDroping, SELECTED_DROPPING_DETAIL)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val radioButton: RadioButton = v as RadioButton
        if (radioButton.isChecked) {
            when (v.id) {
                R.id.layout_sleeper, R.id.layout_seater -> {
                    binding.rgSleeperSeater.clearCheck()
                    defaultIconRgSleeperSeater()
                    return true

                }

                R.id.layout_ac, R.id.layout_non_ac -> {
                    binding.rgAcNonAc.clearCheck()
                    defaultIconRgAcNonAc()

                    return true
                }

                R.id.layout_price_low, R.id.layout_price_high, R.id.layout_departing_early, R.id.layout_departing_late -> {
                    binding.rgPriceDeparture.clearCheck()
                    defaultIconRgPriceDeparture()
                    return true
                }
            }

        }
        return false
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        setFilterSelection(getSelectedFilter(checkedId))
    }

    @SuppressLint("LogNotTimber")
    private fun setFilterSelection(selectedFilters: ArrayList<String>) {
        selectedBusType = selectedFilters
        filteredList = mutableListOf<Result>()

        firebaseLogEvent(
            context,
            FILTERS_OPTIONS,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            FILTERS_OPTIONS,
            "${selectedBusType.joinToString()} - SRP"
        )

        if (selectedFilters.isNotEmpty() && selectedFilters.size > 1) {
            var busListString = mutableListOf<String>()

            for (i in 0..updatedList.size.minus(1)) {
                val busType = updatedList[i].bus_type.split(",")
                var isFirstFilterAvailable = false
                var isSecondFilterAvailable = false

                busType?.forEach {
                    if (it.trim().equals(selectedFilters[0], true)) {
                        isFirstFilterAvailable = true
                    } else if (it.trim().contains(selectedFilters[0], true)) {
                        isFirstFilterAvailable =
                            !(selectedFilters[0] == "Sleeper" && it.trim().contains("Semi", true))
                    } else if (it.trim().contains("Semi", true) && selectedFilters[0] == "Seater") {
                        isFirstFilterAvailable =
                            (selectedFilters[0] == "Seater" && it.trim().contains("Semi", true))
                    } else if (it.trim().equals(selectedFilters[1], true)) {
                        isSecondFilterAvailable = true
                    }
                }

                if (isFirstFilterAvailable && isSecondFilterAvailable && !filteredList.contains(
                        updatedList[i]
                    )
                ) {
                    filteredList.add(updatedList[i])
                } else if ((isFirstFilterAvailable || isSecondFilterAvailable) && !filteredList.contains(
                        updatedList[i]
                    ) && selectedFilters.any {
                        it == getString(R.string.price_low) || it == getString(
                            R.string.price_high
                        ) || it == getString(R.string.departing_early) || it == getString(R.string.departing_late)
                    } && selectedFilters.size == 2
                ) {
                    filteredList.add(updatedList[i])
                }

                Timber.d("updateListSize ${updatedList.size}")
                Timber.d("filterListSize ${filteredList.size}")
            }
        } else if (selectedFilters.isEmpty()) {
            filteredList = getAvailableRoutes()
        } else {
            if (individualList == null) {
                if (listForFilter.isEmpty()) {
                    listForFilter = availableRoutesList
                }
            }


            for (i in 0..listForFilter.size.minus(1)) {
                val busType = listForFilter[i].bus_type.split(",")
                var actualBusType = ""

                busType.forEach {
                    if (it.trim().contains(selectedFilters[0], true)) {
                        actualBusType = it.trim()
                    }
                }

                for (j in 0..busList.size.minus(1)) {
                    /* if(busList[j].busType.equals(actualBusType,true) && !filteredList.contains(
                             availableRoutesList[i])
                     ) {
                         filteredList.add(availableRoutesList[i])
                     }*/

                    if (selectedFilters[0] == "Sleeper") {
                        if (actualBusType.contains("Semi", true) == true) {
                            continue
                        } else {
                            if (actualBusType.contains(busList[j].busType!!) && !filteredList.contains(
                                    listForFilter[i]
                                )
                            ) {
                                filteredList.add(listForFilter[i])
                            }
                        }
                    } else if (selectedFilters[0] == "Seater") {
                        if ((actualBusType.contains(busList[j].busType!!) || listForFilter[i].bus_type.contains(
                                "Semi"
                            )) && !filteredList.contains(listForFilter[i])
                        ) {
                            filteredList.add(listForFilter[i])
                        }
                    } else {
                        var actualBusType = ""

                        busType.forEach {
                            if (it.trim().equals(selectedFilters[0], true)) {
                                actualBusType = it.trim()
                            }
                        }

                        if (busList[j].busType.equals(
                                actualBusType,
                                true
                            ) && !filteredList.contains(
                                listForFilter[i]
                            )
                        ) {
                            filteredList.add(listForFilter[i])
                        }
                    }
                }
            }
        }
        setBusDetailsAdapter()
        if (filteredList.isNotEmpty()) updatedList = filteredList
        checkPriceDepartureIndividual(filteredList, selectedFilters)
        layoutVisibility(filteredList)
        setBusDetailsAdapter()

        Timber.d("filteredListSize ${filteredList.size} ${availableRoutesList.size}")
        if (::searchListAdapter.isInitialized) {
            searchListAdapter.filterList(filteredList)
        }

        // updatedList = getAvailableRoutes()

//        if(departureTime.isNotEmpty() || arrivalTime.isNotEmpty()) {
//            updatedList = filteredList
//        }
//        else {
//            updatedList = availableRoutesList
//        }
        updatedList = availableRoutesList
    }

    private fun checkPriceDepartureIndividual(
        filteredList: MutableList<Result>,
        selectedFilters: ArrayList<String>,
    ) {
        if (this.filteredList.isEmpty() && selectedFilters.size == 1 && selectedFilters.any {
                it == getString(R.string.price_low) || it == getString(
                    R.string.price_high
                ) || it == getString(R.string.departing_early) || it == getString(R.string.departing_late)
            }) {
            this.filteredList = availableRoutesList
        }

        when {
            selectedFilters.contains(getString(R.string.price_low)) -> {
                this.filteredList.sortWith(compareBy<Result> {
                    it.fare_str.replace(",", "").toString().toDoubleOrNull() ?: 0.0
                })

            }

            selectedFilters.contains(getString(R.string.price_high)) -> {
                this.filteredList.sortWith(compareByDescending<Result> {
                    it.fare_str.replace(
                        ",",
                        ""
                    ).toString().toDoubleOrNull() ?: 0.0
                })
                val list = arrayListOf<String>()
            }

            selectedFilters.contains(getString(R.string.departing_early)) -> {
                this.filteredList.sortWith(compareBy<Result> {
                    if (it.dep_time.contains(
                            "am",
                            true
                        ) || it.dep_time.contains("pm", true)
                    ) amPmToTwentyFour(it.dep_time) else it.dep_time
                })
            }

            selectedFilters.contains(getString(R.string.departing_late)) -> {
                this.filteredList.sortWith(compareByDescending<Result> {
                    if (it.dep_time.contains(
                            "am",
                            true
                        ) || it.dep_time.contains("pm", true)
                    ) amPmToTwentyFour(it.dep_time) else it.dep_time
                })
            }
        }
    }

    private fun layoutVisibility(list: MutableList<Result>) {

        if (!list.isNullOrEmpty()) {
            binding.rvBusDetails.visible()
            binding.filters.visible()
            binding.tvNoService.gone()
        } else {
            binding.tvNoService.visible()
            binding.rvBusDetails.gone()
        }
    }

    private fun getSelectedFilter(checkedId: Int): ArrayList<String> {
        val filters = arrayListOf<String>()
        if (binding.rgSleeperSeater.checkedRadioButtonId != -1) {
            if (binding.layoutSleeper.isChecked) {
                filters.add("Sleeper")
                binding.layoutSleeper.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_sleeper_icon_blue_without_label,
                    0,
                    0
                )
                binding.layoutSleeper.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                )

                binding.layoutSeater.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_filter_seat,
                    0,
                    0
                )
                binding.layoutSeater.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                )
            } else if (binding.layoutSeater.isChecked) {
                filters.add("Seater")
                binding.layoutSeater.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_filter_seat_blue,
                    0,
                    0
                )
                binding.layoutSeater.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                )
                binding.layoutSleeper.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_sleeper_icon_without_label,
                    0,
                    0
                )
                binding.layoutSleeper.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                )
            }
        }
        if (binding.rgAcNonAc.checkedRadioButtonId != -1) {
            if (binding.layoutAc.isChecked) {
                filters.add("AC")
                binding.layoutAc.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_ac_blue_without_label,
                    0,
                    0
                )
                binding.layoutAc.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                )
                binding.layoutNonAc.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_noac_without_label,
                    0,
                    0
                )
                binding.layoutNonAc.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                )

            } else if (binding.layoutNonAc.isChecked) {
                filters.add("Non-AC")
                binding.layoutNonAc.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_noac_blue_without_label,
                    0,
                    0
                )
                binding.layoutNonAc.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                )
                binding.layoutAc.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_ac_without_label,
                    0,
                    0
                )
                binding.layoutAc.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                )
            }
        }


        if (binding.rgPriceDeparture.checkedRadioButtonId != -1) {
            when {
                binding.layoutPriceLow.isChecked -> {
                    filters.add(getString(R.string.price_low))
                    binding.layoutPriceLow.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_price_low_blue_without_label_new,
                        0,
                        0
                    )
                    binding.layoutPriceLow.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    )
                    binding.layoutPriceHigh.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_price_high_without_label_new,
                        0,
                        0
                    )
                    binding.layoutPriceHigh.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )
                    binding.layoutDepartingEarly.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_departing_early_without_label,
                        0,
                        0
                    )
                    binding.layoutDepartingEarly.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )
                    binding.layoutDepartingLate.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_departing_late_without_label,
                        0,
                        0
                    )
                    binding.layoutDepartingLate.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )

                }

                binding.layoutPriceHigh.isChecked -> {
                    filters.add(getString(R.string.price_high))
                    binding.layoutPriceHigh.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_price_high_blue_without_label_new,
                        0,
                        0
                    )
                    binding.layoutPriceHigh.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    )
                    binding.layoutPriceLow.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_price_low_without_label_new,
                        0,
                        0
                    )
                    binding.layoutPriceLow.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )
                    binding.layoutDepartingEarly.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_departing_early_without_label,
                        0,
                        0
                    )
                    binding.layoutDepartingEarly.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )
                    binding.layoutDepartingLate.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_departing_late_without_label,
                        0,
                        0
                    )
                    binding.layoutDepartingLate.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )
                }

                binding.layoutDepartingEarly.isChecked -> {
                    filters.add(getString(R.string.departing_early))
                    binding.layoutDepartingEarly.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_departing_early_blue_without_label,
                        0,
                        0
                    )
                    binding.layoutDepartingEarly.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    )
                    binding.layoutDepartingLate.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_departing_late_without_label,
                        0,
                        0
                    )
                    binding.layoutDepartingLate.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )
                    binding.layoutPriceHigh.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_price_high_without_label_new,
                        0,
                        0
                    )
                    binding.layoutPriceHigh.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )
                    binding.layoutPriceLow.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_price_low_without_label_new,
                        0,
                        0
                    )
                    binding.layoutPriceLow.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )

                }

                binding.layoutDepartingLate.isChecked -> {
                    filters.add(getString(R.string.departing_late))
                    binding.layoutDepartingLate.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_departing_late_blue_without_label,
                        0,
                        0
                    )
                    binding.layoutDepartingLate.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    )
                    binding.layoutDepartingEarly.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_departing_early_without_label,
                        0,
                        0
                    )
                    binding.layoutDepartingEarly.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )
                    binding.layoutPriceHigh.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_price_high_without_label_new,
                        0,
                        0
                    )
                    binding.layoutPriceHigh.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )
                    binding.layoutPriceLow.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.ic_price_low_without_label_new,
                        0,
                        0
                    )
                    binding.layoutPriceLow.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    )
                }
            }
        }
        return filters
    }

    override fun onSingleButtonClick(str: String) {
        if (str.contains(getString(R.string.block))) {

            sharedViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

                val remarks = str.substringBefore("|")
                if (isNetworkAvailable()) {
                    if (shouldSingleBlockUnblock && privilegeResponse?.country.equals("india", true)) {
                        DialogUtils.showFullHeightPinInputBottomSheet(
                            activity = this@BusDetailsActivity,
                            fragmentManager = supportFragmentManager,
                            pinSize = pinSize,
                            getString(R.string.single_block_unblock),
                            onPinSubmitted = { pin: String ->
                                callSingleBlockUnblock(
                                    resId = resId.toString(),
                                    isBlock = true,
                                    remarks = remarks,
                                    authPin = pin
                                )
                                binding.includeProgress.progressBar.gone()
                            },
                            onDismiss = {
                                binding.includeProgress.progressBar.gone()
                            }
                        )
                    } else {
                        callSingleBlockUnblock(
                            resId = resId.toString(),
                            isBlock = true,
                            remarks = remarks,
                            authPin = ""
                        )
                        binding.includeProgress.progressBar.gone()
                    }
                } else
                    noNetworkToast()
            }

        } else if (str == getString(R.string.unauthorized)) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun defaultIconRgSleeperSeater() {
        binding.layoutSleeper.setCompoundDrawablesWithIntrinsicBounds(
            0,
            R.drawable.ic_sleeper_icon_without_label,
            0,
            0
        )
        binding.layoutSleeper.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )
        binding.layoutSeater.setCompoundDrawablesWithIntrinsicBounds(
            0,
            R.drawable.ic_filter_seat,
            0,
            0
        )
        binding.layoutSeater.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )
    }

    private fun defaultIconRgAcNonAc() {
        binding.layoutAc.setCompoundDrawablesWithIntrinsicBounds(
            0,
            R.drawable.ic_ac_without_label,
            0,
            0
        )
        binding.layoutAc.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )
        binding.layoutNonAc.setCompoundDrawablesWithIntrinsicBounds(
            0,
            R.drawable.ic_noac_without_label,
            0,
            0
        )
        binding.layoutNonAc.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )
    }

    private fun defaultIconRgPriceDeparture() {
        binding.layoutPriceLow.setCompoundDrawablesWithIntrinsicBounds(
            0,
            R.drawable.ic_price_low_without_label_new,
            0,
            0
        )
        binding.layoutPriceLow.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )
        binding.layoutPriceHigh.setCompoundDrawablesWithIntrinsicBounds(
            0,
            R.drawable.ic_price_high_without_label_new,
            0,
            0
        )
        binding.layoutPriceHigh.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )
        binding.layoutDepartingEarly.setCompoundDrawablesWithIntrinsicBounds(
            0,
            R.drawable.ic_departing_early_without_label,
            0,
            0
        )
        binding.layoutDepartingEarly.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )
        binding.layoutDepartingLate.setCompoundDrawablesWithIntrinsicBounds(
            0,
            R.drawable.ic_departing_late_without_label,
            0,
            0
        )
        binding.layoutDepartingLate.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )

    }

    private fun swipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            startShimmerEffect()
            if(sourceId=="0" || destinationId=="0"){
                pageNumber=1
                allToAllavailableRoutesApi()
            }else{
            availableRoutesApi()
            }
            binding.rgSleeperSeater.clearCheck()
            binding.rgAcNonAc.clearCheck()
            binding.rgPriceDeparture.clearCheck()
        }
    }

    private fun interCreateDestinationList() {

        interDestinationList = mutableListOf()
        if (destinationPairModel != null) {
            for (i in 0..(destinationPairModel?.result?.size?.minus(1) ?: 0)) {

                if (destinationPairModel?.result?.get(i)?.origin?.id.toString() == sourceId) {
                    interDestinationList.add(destinationPairModel?.result?.get(i)?.destination!!)
                }

            }
            val b = interDestinationList.distinctBy { it.id } as MutableList

            PreferenceUtils.putInterDestinationCity(
                b
            )
        }

    }

    private fun createDestinationList() {
        lifecycleScope.launch {
            val finalDestination = createDestinationWithCoroutines()
            PreferenceUtils.putDestinationCity(
                finalDestination
            )
        }


    }

    private suspend fun createDestinationWithCoroutines(): MutableList<Destination> =
        withContext(Dispatchers.IO) {
            destinationList = mutableListOf()
            if (destinationPairModel != null) {
                for (i in 0..(destinationPairModel?.result?.size?.minus(1) ?: 0)) {
                    if (sourceId.contains(":")) {
                        if (destinationPairModel?.result?.get(i)?.origin?.id.toString()
                                .contains("-1")
                        ) {
                            val temp =
                                destinationPairModel?.result?.get(i)?.origin?.id.toString()
                                    .split(":")
                            if (sourceId.contains("-1")) {
                                val tempS = sourceId.split(":")
                                if (tempS[1] == temp[1]) {
                                    destinationList.add(destinationPairModel?.result?.get(i)?.destination!!)
                                }
                            }
                        } else {
                            if (destinationPairModel?.result?.get(i)?.origin?.id.toString() == sourceId) {
                                destinationList.add(destinationPairModel?.result?.get(i)?.destination!!)
                            }
                        }
                    } else {
                        if (destinationPairModel?.result?.get(i)?.origin?.id.toString()
                                .contains(":")
                        ) {
                            val temp =
                                destinationPairModel?.result?.get(i)?.origin?.id.toString()
                                    .split(":")
                            if (temp[1] == sourceId) {
                                destinationList.add(destinationPairModel?.result?.get(i)?.destination!!)
                            }
                            val b = destinationList.distinctBy { it.name } as MutableList
                            destinationList.clear()
                            destinationList.addAll(b)

                        } else {
                            if(sharedViewModel.privilegesLiveData?.value?.tsPrivileges?.allowAllToAllSearchInTsMobileApp==true && sourceId=="0"){
                                destinationList.add(destinationPairModel?.result?.get(i)?.destination!!)
                            }
                           else if (destinationPairModel?.result?.get(i)?.origin?.id.toString() == sourceId
                            ) {
                                val index =
                                    destinationList.indexOfFirst {
                                        it.name?.trim() == destinationPairModel?.result?.get(
                                            i
                                        )?.destination?.name?.trim()
                                    }
                                if (index == -1 && destinationPairModel?.result?.get(i)?.destination?.id.toString() != sourceId) {
                                    destinationList.add(destinationPairModel?.result?.get(i)?.destination!!)
                                }

                            }
                        }


                    }
                }
            }

            if(sharedViewModel.privilegesLiveData?.value?.tsPrivileges?.allowAllToAllSearchInTsMobileApp == true){
                val destination=Destination()
                destination.id="0"
                destination.name="All"
                destinationList.add(0,destination)
            }

            return@withContext destinationList.distinctBy<Destination, String?> { it.name } as MutableList<Destination>
        }

    override fun onDataSend(type: Int, file: Any) {

    }


    private fun destinationPopupDialog() {

        var popupBinding: AdapterSearchBpdpBinding? = null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(this))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        popupBinding.searchET.requestFocus()

        forceShowKeyboard(popupBinding.searchET)


        destinationList = PreferenceUtils.getDestinationCity() ?: arrayListOf()

        val destList: MutableList<Origin> = arrayListOf()

        for (i in 0 until destinationList.size) {
            val obj = Origin()
            obj.id = destinationList[i].id
            obj.name = destinationList[i].name
            destList.add(obj)

        }


        destinationNewAdapter = SimpleListAdapter(this, destList, this, DESTINATION)
        popupBinding.searchRV.adapter = destinationNewAdapter


        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                destinationNewAdapter?.filter?.filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })


        destinationPopupWindow = PopupWindow(
            popupBinding.root,
            binding.modifySearchLayout.tvDestination.width,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        val xOff = 0
        val yOff = binding.modifySearchLayout.tvDestination.height

        destinationPopupWindow?.showAsDropDown(binding.modifySearchLayout.tvDestination, xOff, yOff)

        destinationPopupWindow?.elevation = 25f


        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            destinationPopupWindow?.dismiss()
            true
        }
    }


    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {

        when (type) {
            1 -> {
                val selectedData = file as Origin
                when ((extra as Int)) {
                    SOURCE -> {
                        sourcePopupWindow?.dismiss()
                        binding.modifySearchLayout.tvSource.text = selectedData.name
                        sourceId = selectedData.id ?: ""
                        binding.modifySearchLayout.tvDestination.setText("")
                        destinationId = ""
                        finalDestinationId = ""
                        finalSourceId = sourceId
                        createDestinationList()
                    }

                    DESTINATION -> {
                        destinationPopupWindow?.dismiss()
                        binding.modifySearchLayout.tvDestination.setText(selectedData.name)
                        destinationId = selectedData.id ?: ""
                        finalDestinationId = destinationId
                    }

                }


            }
        }
    }

    private fun serviceBlockReasonsListObserver() {
        pickUpChartViewModel.serviceBlockReasonsListResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        blockReasonsList = it.reasons
                    }

                    401 -> {
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
}