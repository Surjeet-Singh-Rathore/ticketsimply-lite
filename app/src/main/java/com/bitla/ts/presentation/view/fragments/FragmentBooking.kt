package com.bitla.ts.presentation.view.fragments

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.view.*
import android.view.animation.*
import android.widget.*
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctRequest.*
import com.bitla.ts.domain.pojo.alloted_services.*
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.destination_pair.Destination
import com.bitla.ts.domain.pojo.destination_pair.Origin
import com.bitla.ts.domain.pojo.destination_pair.Result
import com.bitla.ts.domain.pojo.destination_pair.request.*
import com.bitla.ts.domain.pojo.filter_model.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.recent_bookings.*
import com.bitla.ts.domain.pojo.recent_search.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.view.dashboard.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.removeKey
import com.bitla.tscalender.*
import com.google.firebase.analytics.*
import com.google.firebase.analytics.ktx.*
import com.google.firebase.ktx.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.io.*
import java.text.*
import java.time.*
import java.util.*


class FragmentBooking : BaseUpdateCancelTicket(), View.OnClickListener,
    SlyCalendarDialog.Callback, VarArgListener, OnPnrListener,
    RemoteConfigUpdateHelper.onDestinationPairCacheCheckClickListener, DialogButtonAnyDataListener {

    private var privilegeDetails: PrivilegeResponseModel? = null
    private var currentCountry: String = ""
    private var sourcePopupWindow: PopupWindow? = null
    private var destinationPopupWindow: PopupWindow? = null
    private var servicePopupWindow: PopupWindow? = null
    private var selectedOriginId: String = "0"
    private var selectedDestinationId: String = "0"
    private val TAG: String = FragmentBooking::class.java.simpleName
    private var busStageData = mutableListOf<StageData>()
    private var isSourceSlected: Boolean = false
    private lateinit var cancelTicketSheet: CancelTicketSheet

    private var returnDate: String = ""
    private var selectedDateType: String = ""
    private var sourceId = ""
    private var finalSourceId = ""
    private var destinationId = ""
    private var finalDestinationId = ""
    private var source: String = ""
    private var services: String = ""
    private var destination: String = ""
    private var travelDate: String = ""


    var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resultList: MutableList<Result> = mutableListOf()
    private lateinit var originList: MutableList<Origin>
    private  var destinationList= arrayListOf<Destination>()
    private lateinit var interDestinationList: MutableList<Destination>
    private lateinit var tvSource: TextView
    private lateinit var tvDestination: TextView
    private lateinit var tvSelectDate: TextView
    private lateinit var rvBooking: RecyclerView
    private lateinit var editPassengerSheet: EditPassengerSheet


    private lateinit var binding: LayoutBookTicketsBinding
    private lateinit var lastBookedAdapter: LastBookedAdapter
    private var searchList = mutableListOf<SearchModel>()
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var convertedDate: String? = null
    private var convertedReturnDate: String? = null
    private lateinit var destinationPairModel: DestinationPairModel

    private val recentSearchViewModel by sharedViewModel<RecentSearchViewModel<Any?>>()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()

    private var isAllowBookingAfterTravelDate = false
    private var bookingAfterDoj: Int = 0
    private var service =
        listOf<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>()
    private var summary: ViewSummary? = null
    private var locale: String? = ""

    //BP DP fare for staging//
    private var isAllowBpDpFare = false
    private var cityStagingIdOnTrue: String? = null
    private var cityStagingNameOnTrue: String? = null
    private var cityStagingIdOnFalse: String? = null
    private var cityStagingNameOnFalse: String? = null

    //humsafar
    private lateinit var sourceDestinationArrayAdapter: SourceDestinationArrayAdapter
    private var sourceNewAdapter: SimpleListAdapter? = null
    private var destinationNewAdapter: SimpleListAdapter? = null
    private lateinit var destinationCustomArrayAdapter: SourceDestinationArrayAdapter
    private lateinit var serviceCustomArrayAdapter: ServiceCustomArrayAdapter
    private var serviceNewAdapter: SimpleListAdapter? = null
    private val availableRoutesViewModel by viewModel<AvailableRoutesViewModel<Any?>>()
    private var allowToShowNewFlowInTsApp = false


    //    bus details
    companion object {

        val TAG = FragmentBooking::class.java.simpleName
    }

    private var sevenDaysDate: String = getTodayDate()
    private var serviceApiType: String? = null
    private var busType: String? = null
    private var depTime: String? = null
    private lateinit var rapidBookingDialog: AlertDialog
    private var boardingPoint: String = ""
    private var droppingPoint: String = ""
    private lateinit var boardingStageDetail: StageDetail
    private var stageDetails = mutableListOf<StageDetail>()
    private var boardingList: MutableList<StageDetail>? = null
    private var droppingList: MutableList<StageDetail>? = null
    private var resId: Long? = null
    private var dateList = mutableListOf<StageData>()
    private var availableRoutesList =
        mutableListOf<com.bitla.ts.domain.pojo.available_routes.Result>()
    private var ymdDate: String = ""
    private var ymdCurrentDate: String = ""
    private lateinit var recentSearchModel: RecentSearchModel
    private var busList: ArrayList<BusFilterModel> = arrayListOf()
    private var filteredList = mutableListOf<com.bitla.ts.domain.pojo.available_routes.Result>()
    private var updatedList = mutableListOf<com.bitla.ts.domain.pojo.available_routes.Result>()
    private lateinit var searchListAdapter: MyReservationAdapterBook
    private var resID: Long? = 0L
    private var isAllowBookingForAllServices: Boolean = false
    private var isAllowBookingForAllotedServices: Boolean? = false
    private var role: String? = ""
    private var isAgentLogin: Boolean = false
    private var groupByHubs = false
    private var searchList1 =
        ArrayList<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>()

    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var allowBimaInTs: Boolean = false
    private var recentSearchList = mutableListOf<RecentSearch>()
    private var tempRecentSearchList = mutableListOf<RecentSearch>()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var selectedOrigin = ""
    private var currency = ""
    private var currencyFormat = ""

    //    on service selection
    private var serviceBusType: String? = ""
    private var serviceDepTime: String? = ""
    private var service_ServiceNUmber: String? = ""
    private var service_title: String? = ""
    private var serviceType: String? = ""
    var emptyBoarding: BoardingPointDetail = BoardingPointDetail("", "", "", "", "")
    var emptyDropping: DropOffDetail = DropOffDetail("", "", "", "", "")
    var oldCount = 0
    var isDestinationPairCache = false
    var destinationPairCount = 0

    override fun onResume() {
        super.onResume()
        // startShimmerEffect()
        getPref()
        PreferenceUtils.setPreference(PREF_TRAVEL_DATE, getTodayDate())
        travelDate = getTodayDate()
        if (travelDate.isNotEmpty()) {
            convertedDate = getDateYMD(travelDate)
            travelDate = thFormatDateMMMOutput(getDateYMD(getTodayDate()))
        }
        tvSource.text = source
//        Timber.i("AllPairsList: ${PreferenceUtils.getSource()} ${sourceId} ; ${source} ${finalSourceId} :${selectedOriginId}resume")
        tvDestination.text = destination
        tvSelectDate.text = travelDate





        binding.sourceDropdownTv.setText(source)
        binding.destinationDropdownTv.setText(destination)

//        checkSource()

        when {
            PreferenceUtils.getTravelDate() == getTodayDate() -> todayDateColor()
            PreferenceUtils.getTravelDate() == getTomorrowDate() -> tomorrowDateColor()
            else -> calenderDateColor()
        }

        if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
            boardingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)!!
            boardingPoint = boardingStageDetail.name!!
        }

        if (boardingPoint.isNotEmpty() && droppingPoint.isNotEmpty() && ::rapidBookingDialog.isInitialized && rapidBookingDialog.isShowing) {
            rapidBookingDialog.cancel()
            rapidBookingDialog = DialogUtils.rapidBookingDialog(
                boardingPoint = boardingPoint,
                droppingPoint = droppingPoint,
                context = requireContext(),
                varArgListener = this
            )!!
        }
        if (!binding.busServices.text.isNullOrEmpty()) {
            binding.busServiceCross.visible()
        } else
            binding.busServiceCross.gone()


        if (PreferenceUtils.getString(getString(R.string.BACK_PRESS)) != null) {
            val backPress = PreferenceUtils.getString(getString(R.string.BACK_PRESS))
            busStageData = getBusStageData()
            if (backPress == getString(R.string.new_booking) && busStageData.isNotEmpty()) {
                //newBookingSelection()
                tvSource.text = ""
                tvDestination.text = ""
                source = ""
                destination = ""
                services = ""
                removeKey(PREF_SOURCE)
                removeKey(PREF_SOURCE_ID)
                removeKey(PREF_DESTINATION)
                removeKey(PREF_DESTINATION_ID)
                if (recentSearchList.isNotEmpty()) {
//                    setRecentSearch(recentSearchList)
                    setMostFrequentlyBookedAdapter(recentSearchList)
                }
            } else
               // callRecentSearch()
            removeKey(getString(R.string.BACK_PRESS))
        }
        // setPrivilegesObserver()
//        if (requireActivity() is DashboardNavigateActivity) {
//            lifecycleScope.launch {
//                (requireActivity() as DashboardNavigateActivity).cityName.collect{
//                    if(!currentCountry.isNullOrEmpty() && currentCountry.equals("india",true)){
//                        setGpsLocation()
//                    }
//                }
//            }
//        }

        lifecycleScope.launch {
            val activity = activity
            if (activity is DashboardNavigateActivity) {
                activity.cityName.collect { cityName ->
                    if (!currentCountry.isNullOrEmpty() && currentCountry.equals("india", true)) {
                        setGpsLocation()
                    }
                }
            }
        }
    }


    private fun search(text: String) {
        val temp = mutableListOf<Origin>()
        originList.forEach {
            if (it.name?.contains(text, true) == true) {
                temp.add(it)
            }
        }


        // Set the adapter to the AutoCompleteTextView
        sourceDestinationArrayAdapter.updateList(temp)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = LayoutBookTicketsBinding.inflate(inflater, container, false)
        firebaseAnalytics = Firebase.analytics
        binding.busServices.text = ""
        tvSource = binding.tvSource
        tvDestination = binding.tvDestination
        tvSelectDate = binding.tvSelectDate
        rvBooking = binding.rvLastBooked
        setDateLocale(PreferenceUtils.getlang(), requireContext())

        (activity as? DashboardNavigateActivity)?.reduceMarginTop()

      //  edgeToEdgeFromOnlyBottom(binding.root)


        startShimmerEffect()
        getPref()

        cancelTicketSheet = childFragmentManager.findFragmentById(R.id.layoutCancelTicketSheet) as CancelTicketSheet
        editPassengerSheet = childFragmentManager.findFragmentById(R.id.layoutEditPassengerSheet) as EditPassengerSheet







        if (!PreferenceUtils.getString(PREF_SOURCE).isNullOrEmpty()) {
            tvSource.text = PreferenceUtils.getString(PREF_SOURCE)
            binding.sourceDropdownTv.setText(PreferenceUtils.getString(PREF_SOURCE))
        }

        if (!PreferenceUtils.getString(PREF_DESTINATION).isNullOrEmpty()) {
            binding.tvDestination.text = PreferenceUtils.getString(PREF_DESTINATION)
            binding.destinationDropdownTv.setText(PreferenceUtils.getString(PREF_DESTINATION))
        }


        binding.sourceDropdownTv.setOnClickListener {
            sourcePopupDialog()
        }







        if (source.isEmpty() && destination.isEmpty()) {
            val intent = requireActivity().intent
            source = intent.getStringExtra(getString(R.string.last_searched_source)).toString()
            destination =
                intent.getStringExtra(getString(R.string.last_searched_destination)).toString()
        } else {
            sourceId = PreferenceUtils.getSourceId()
            destinationId = PreferenceUtils.getDestinationId()
        }

        if (!binding.busServices.text.isNullOrEmpty()) {
            binding.busServiceCross.visible()
        } else
            binding.busServiceCross.gone()


        if (travelDate.isNotEmpty()) {
            travelDate = thFormatDateMMMOutput(getDateYMD(getTodayDate()))
        }
        when (travelDate) {
            thFormatDateMMMOutput(getDateYMD(getTodayDate())) -> todayDateColor()
            thFormatDateMMMOutput(getDateYMD(getTomorrowDate())) -> tomorrowDateColor()
            else -> calenderDateColor()
        }

        clickListener()
        setObserver()
        setDestinationPairObserver()


        setBusDetailsObserver()

        setUpServiceListObserver()

        remoteConfigDestinationPairCacheCheck()

        lifecycleScope.launch {
            recentSearchViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    requireContext().showToast(it)
                }
            }
        }

        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
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


        binding.tvService.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    binding.clearIV.visible()
                } else {
                    binding.clearIV.gone()

                }
            }
        })

        binding.clearIV.setOnClickListener {
            PreferenceUtils.removeKey(PREF_SELECTED_AVAILABLE_ROUTES)
            binding.tvService.setText("")
        }

        recentSearchViewModel.callDestinationPairApi.observe(viewLifecycleOwner) {
            if (it != null && it) {
                callDestinationPairApi()
            }
        }

        return binding.root

    }




    fun setGpsLocation(){

        if(::originList.isInitialized){
            var gpsCityName = (activity as DashboardNavigateActivity).getGpsCity().lowercase()

            if (gpsCityName.equals("bengaluru", true)) {
                gpsCityName = "bangalore"
            }

            if (tvSource.text.toString().isEmpty() || !gpsCityName.equals(
                    tvSource.text.toString(),
                    true
                )
            ) {
                if (!gpsCityName.isNullOrEmpty()) {
                    if (originList.isNotEmpty()) {
                        for (i in 0 until originList.size) {
                            if (originList[i].name.equals(gpsCityName, true)) {
                                PreferenceUtils.putString(PREF_SOURCE, originList[i].name)
                                PreferenceUtils.putString(
                                    PREF_SOURCE_ID,
                                    originList[i].id.toString()
                                )
                                tvSource.text = originList[i].name
                                binding.sourceDropdownTv.setText(originList[i].name)
                                binding.tvDestination.text = ""
                                binding.destinationDropdownTv.setText("")

                                sourcePopupWindow?.dismiss()
                                destinationId = ""
                                finalDestinationId = ""
                                sourceId = originList[i].id ?: ""
                                finalSourceId = sourceId
                                createDestinationList()
                                invalidateServiceList()


                            }
                        }
                    }
                }
            }
        }

    }

    private fun invalidateServiceList() {
        binding.tvService.setText("")
        searchList.clear()
        //    binding.tvService.setAdapter(null)

    }

    private fun checkSource() {
        Timber.i("CheckBPDP : $isAllowBpDpFare,$cityStagingNameOnTrue,$cityStagingNameOnFalse")

        if (isAgentLogin && cityStagingIdOnTrue != null) {
            var id = ""
            if (!isAllowBpDpFare) {
                id = cityStagingIdOnFalse.toString()
            } else {
                id = cityStagingIdOnTrue.toString()

            }


            val localCityId = PreferenceUtils.getLogin().city_id


            val selectedCityType: String = getString(R.string.selectSource)
            var selectedCityName: String = ""

            if (!isAllowBpDpFare) {
                selectedCityName = cityStagingNameOnFalse.toString()

            } else {
                selectedCityName = cityStagingNameOnTrue.toString()

            }

            if (selectedCityType == getString(R.string.selectSource)) {
                finalSourceId = id.toString()
                PreferenceUtils.putString(PREF_SOURCE_ID, finalSourceId)
                isSourceSlected = true
            }

            val selectedCityId: String = ""
            if (selectedCityType == getString(R.string.selectSource)) {
                selectedOrigin = selectedCityId

                PreferenceUtils.putString(PREF_SOURCE, selectedCityName)
                PreferenceUtils.putString(PREF_SOURCE_ID, finalSourceId)
            }

            getDataFromActivity(
                selectedCityId,
                selectedCityName,
                selectedCityType
            )
            binding.progress.isVisible = false
        }
    }

    private fun setBookTicketData(it: PrivilegeResponseModel) {

        if (it.boLicenses?.allowBookingForAllServices == true
            && it.boLicenses.allowBookingForAllotedServices
            && role == getString(R.string.role_field_officer)
        ) {
            binding.apply {
                noData.gone()
                tvNoService.gone()
                rvDateDetails.gone()
                busDetailsContainer.gone()
                myBookingBookTicketContainer.visible()
            }
            stopShimmerEffect()

        } else if (it.boLicenses?.allowBookingForAllServices == false
            && !it.boLicenses.allowBookingForAllotedServices
            && role == getString(R.string.role_field_officer)
        ) {

            binding.apply {
                rvDateDetails.gone()
                busDetailsContainer.gone()
                myBookingBookTicketContainer.gone()
            }

            stopShimmerEffect()

        } else if (it.boLicenses?.allowBookingForAllServices == false
            && it.boLicenses.allowBookingForAllotedServices
            && role == getString(R.string.role_field_officer)
        ) {
            allotedDirectService(1, ymdCurrentDate, "", "", null)
            binding.apply {
                noData.gone()
                tvNoService.gone()
                rvDateDetails.visible()
                myBookingBookTicketContainer.gone()
                busDetailsContainer.visible()
            }

        } else if (it.boLicenses?.allowBookingForAllServices == true
            && !it.boLicenses.allowBookingForAllotedServices
            && role == getString(R.string.role_field_officer)
        ) {
            binding.apply {
                noData.gone()
                tvNoService.gone()
                rvDateDetails.gone()
                busDetailsContainer.gone()
                myBookingBookTicketContainer.visible()
            }
            stopShimmerEffect()
        } else {
            binding.apply {
                noData.gone()
                tvNoService.gone()
                rvDateDetails.gone()
                busDetailsContainer.gone()
            }
        }
    }

    private fun callDestinationPairApi() {
        startShimmerEffect()

        allowBimaInTs = PreferenceUtils.getPreference("is_bima", false)!!
        Timber.i("BimaInTs_BookTicket: $allowBimaInTs")

        destinationPairModel = DestinationPairModel(this)
        DestinationPairRequest(
            bccId.toString(), format_type, destination_pair,
            com.bitla.ts.domain.pojo.destination_pair.request.ReqBody(
                api_key = loginModelPref.api_key,
                operator_api_key = operator_api_key,
                response_format = "true",
                app_bima_enabled = allowBimaInTs,
                locale = locale
            )
        )

        recentSearchViewModel.getDestinationPair(
            apiKey = loginModelPref.api_key,
            operatorKey = operator_api_key,
            responseFormat = "true",
            appBimaEnabled = allowBimaInTs,
            locale = locale!!,
            apiType = destination_pair
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObserver() {
        sharedViewModel.dataRecentSearch.observe(requireActivity()) {
            if (it != null) {
                binding.includeProgress.progressBar.gone()
                if (it.code == 200 && isAttachedToActivity() && !it.recent_search.isNullOrEmpty()) {
                    recentSearchList = it.recent_search
                    PreferenceUtils.putRecentSearch(it.recent_search)
                    if (it.recent_search != null)
                        setBusBookings(it.recent_search)
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }

        sharedViewModel.deleteSearch.observe(requireActivity()) { it ->
            if (it != null) {
                if (it.code == 200) {
                    if (busStageData.any { it.origin_id == selectedOriginId && it.destination_id == selectedDestinationId.toString() }) {
                        val index =
                            busStageData.indexOfFirst { it.origin_id == selectedOriginId && it.destination_id == selectedDestinationId.toString() }
                        busStageData.removeAt(index)
                        binding.rvBookingSrcDes.adapter?.notifyDataSetChanged()
                    }
                } else
                    if (isAttachedToActivity()) {
                        if (it.message != null) {
                            it.message.let { it1 -> requireContext().toast(it1) }
                        }
                    }

            } else {
                if (isAttachedToActivity()) {
                    requireContext().toast(getString(R.string.server_error))
                }
            }
        }
        binding.rvLastBooked.gone()


    }

    private fun setDestinationPairObserver() {
        recentSearchViewModel.dataDestinationPairList.observe(viewLifecycleOwner) {
            if (it != null) {

                if (it.code == 200) {
                    destinationPairModel = it
                    if (destinationPairModel != null && destinationPairModel.result != null) {
                        resultList = destinationPairModel.result!!
//                    adding data to source list
                        createSourceList()

                        if (!isSourceSlected) {
                            if (tvSource.text.isEmpty()) {
                                checkSource()
                            }
                        }
//                    adding data to destination list
//                    createDestinationList()
                        if (isDestinationPairCache) {
                            PreferenceUtils.setPreference(
                                requireContext().getString(R.string.OLD_COUNT_KEY),
                                oldCount
                            )
                            val oldDateTime = LocalDateTime.now()
                            PreferenceUtils.setPreference(
                                requireContext().getString(R.string.OLD_DATE_TIME_KEY),
                                oldDateTime.toString()
                            )
                            PreferenceUtils.putObject(
                                destinationPairModel,
                                requireContext().getString(R.string.DESTINATION_PAIR_MODEL_KEY)
                            )
                        }
                    }
                    recentSearchViewModel.setDestinationPairApiCall(false)
                } else
                    recentSearchViewModel.setDestinationPairApiCall(true)
            } else {
                recentSearchViewModel.setDestinationPairApiCall(true)
                if (isAttachedToActivity()) {
                    requireContext().toast(getString(R.string.something_went_wrong))
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        finalSourceId = PreferenceUtils.getSourceId()
        finalDestinationId = PreferenceUtils.getDestinationId()
        locale = PreferenceUtils.getlang()
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()

        PreferenceUtils.putString(LAST_SEARCHED_SOURCE_ID, sourceId)
        PreferenceUtils.putString(LAST_SEARCHED_DESTINATION_ID, destinationId)

        if (travelDate.isNotEmpty())
            convertedDate = getDateYMD(travelDate)
        else {
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            travelDate = sdf.format(Date())
            ymdDate = getDateYMD(travelDate)
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        travelDate = sdf.format(Date())
        ymdCurrentDate = getDateYMD(travelDate)

        loginModelPref = PreferenceUtils.getLogin()

    }

    private suspend fun setPrefWithCoroutines(privilegeResponseModel: PrivilegeResponseModel) =
        withContext(Dispatchers.IO) {
            PreferenceUtils.putObject(privilegeResponseModel, PREF_PRIVILEGE_DETAILS)
        }

    private fun setData(it: PrivilegeResponseModel) {
        if (it.isAgentLogin) {
            isAgentLogin = it.isAgentLogin
        }

        PreferenceUtils.setPreference("otp_validation_time", it.configuredLoginValidityTime)
        isAllowBookingAfterTravelDate = it.isAllowBookingAfterTravelDate
        isAllowBookingForAllServices = it.boLicenses?.allowBookingForAllServices ?: false

        isAllowBookingForAllotedServices = it.boLicenses?.allowBookingForAllotedServices
        isAllowBpDpFare = it.availableAppModes?.allowBpDpFare ?: false

        if (it.user_city != null && isAgentLogin) {
            cityStagingIdOnFalse = it.user_city.cityId.toString()
            cityStagingNameOnFalse = it.user_city.cityName
            cityStagingIdOnTrue = it.user_city.defaultStageId
            cityStagingNameOnTrue = it.user_city.defaultStageName
        }

        PreferenceUtils.setPreference(
            getString(R.string.mobile_number_length),
            it.phoneNumValidationCount
        )

        if (PreferenceUtils.getTravelDate().isNotEmpty()) {
            bookingAfterDoj = if (it.bookingAfterDoj == null) {
                0
            } else {
                if (it.bookingAfterDoj.trim().isEmpty()) {
                    0
                } else {
                    it.bookingAfterDoj.trim().toInt()
                }
            }
        }

        // if (true) {
        if (it.allowToShowNewFlowInTsApp == true) {
            allowToShowNewFlowInTsApp = true
            binding.apply {
                this.sourceDropdownTv.visible()
                this.tvSource.gone()
                this.destinationDropdownTv.visible()
                this.tvDestination.gone()
                this.btnRotateHumsafar.visible()
                this.btnRotate.gone()
                this.tvService.visible()
                this.busServices.gone()
            }
        } else {
            allowToShowNewFlowInTsApp = false
            binding.apply {
                this.sourceDropdown.gone()
                this.destinationDropdown.gone()
                this.timeInMins3.gone()
                this.busServices.visible()
                this.btnRotateHumsafar.gone()
                this.btnRotate.visible()
            }

        }

        stopShimmerEffect()
    }

    private fun setLastBookedAdapter(recentBooking: MutableList<RecentBooking>) {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvLastBooked.layoutManager = layoutManager
        lastBookedAdapter =
            LastBookedAdapter(requireActivity(), this, this, recentBooking)
        binding.rvLastBooked.adapter = lastBookedAdapter
    }

    private fun callRecentSearch() {
        sharedViewModel.recentSearchApi(
            apiKey = loginModelPref.api_key,
            limit = 10,
            isBima = allowBimaInTs,
            locale = locale!!,
            apiType = recent_search_method_name
        )
    }

    private fun setBusBookings(
        it: MutableList<RecentSearch>?,
    ) {
        rvBooking.visible()
        busStageData = mutableListOf()
        var isNewBooking = true

        if (it != null) {
            it.forEach {
                val isSelected =
                    "${it.origin_name}-${it.dest_name}" == "$source-$destination"
                if (it.origin_name != null && it.dest_name != null) {
                    busStageData.add(
                        StageData(
                            "${it.origin_name}-${it.dest_name}",
                            true,
                            isSelected,
                            "BOOKING",
                            it.origin_id,
                            it.dest_id
                        )
                    )
                }
            }
        }

// checking for "New Booking"
        if (busStageData.any { stageData -> stageData.isSelected })
            isNewBooking = false

// "New Booking" added at first position
        rvBooking.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

        if (busStageData.isNotEmpty()) {
            rvBooking.adapter =
                MostFrequentlyBookedAdapter(
                    requireContext(),
                    this,
                    busStageData,
                    true
                )
        }
    }

    private fun setRecentSearch(
        it: MutableList<RecentSearch>,
    ) {
        binding.rvBookingSrcDes.visible()
        busStageData = mutableListOf()

        it.forEach {
            if (it.origin_name != null && it.dest_name != null) {
                busStageData.add(
                    StageData(
                        "${it.origin_name}-${it.dest_name}",
                        true,
                        false,
                        "BOOKING",
                        it.origin_id,
                        it.dest_id
                    )
                )
            }
        }

// "New Booking" added at first position
        busStageData.add(
            0,
            StageData(
                getString(R.string.new_booking),
                isRemovable = false,
                isSelected = true,
                layoutType = "BOOKING"
            )
        )

        rvBooking.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvBooking.adapter =
            context?.let {
                BookingStageAdapter(
                    it,
                    this,
                    busStageData,
                    true
                )
            }
    }

    private fun setMostFrequentlyBookedAdapter(
        it: MutableList<RecentSearch>,
    ) {

        rvBooking.visible()
        busStageData = mutableListOf()

        it.forEach {
            if (it.origin_name != null && it.dest_name != null) {
                busStageData.add(
                    StageData(
                        title = "${it.origin_name}-${it.dest_name}",
                        isRemovable = true,
                        isSelected = false,
                        layoutType = "BOOKING",
                        origin_id = it.origin_id,
                        destination_id = it.dest_id
                    )
                )
            }
        }

// "New Booking" added at first position

        rvBooking.layoutManager =
            LinearLayoutManager(
                /* context = */ requireContext(),
                /* orientation = */ LinearLayoutManager.VERTICAL,
                /* reverseLayout = */ false
            )

        if (busStageData.isNotEmpty()) {
            rvBooking.adapter =
                MostFrequentlyBookedAdapter(
                    context = requireContext(),
                    onItemClickListener = this,
                    menuList = busStageData,
                    isCrossIconVisible = true
                )
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

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
                        PreferenceUtils.putString(PREF_SOURCE_ID, finalSourceId)
                        isSourceSlected = true
                    } else if (selectedCityType == getString(R.string.selectDestination)) {
                        finalDestinationId =
                            data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                        PreferenceUtils.putString(
                            PREF_DESTINATION_ID, finalDestinationId
                        )
//                        Timber.d("destination129911: ${finalDestinationId.toString()}")
                    }
                    var selectedCityId: String = ""

                    if (data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                            .contains(":")
                    ) {
                        if (data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                                .contains("-1")
                        ) {
                            val temp =
                                data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                                    .split(":")
                            selectedCityId = temp[1]
                        } else {
                            val temp =
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
                    } else if (selectedCityType == getString(R.string.selectDestination)) {
                        binding.busServices.text = ""

                        PreferenceUtils.putString(
                            PREF_DESTINATION,
                            selectedCityName
                        )
                        PreferenceUtils.putString(
                            PREF_DESTINATION_ID,
                            finalDestinationId
                        )
                    }
                    Timber.d("selectedServiceOnBookingPage123: $services")


                    getDataFromActivity(
                        selectedCityId,
                        selectedCityName,
                        selectedCityType
                    )
                } else {
                    services =
                        data.getStringExtra(getString(R.string.SELECTED_CITY_NAME)).toString()
                    binding.busServices.text = services
                    if (data.getStringExtra(getString(R.string.bus_type)) != null) {
                        serviceBusType = data.getStringExtra(getString(R.string.bus_type))
                    }
                    if (data.getStringExtra(getString(R.string.service_number)) != null) {
                        service_ServiceNUmber =
                            data.getStringExtra(getString(R.string.service_number))
                        PreferenceUtils.setPreference(PREF_RESERVATION_ID, service_ServiceNUmber?.toLong() ?: 0L)
                    }
                    if (data.getStringExtra(getString(R.string.dep_time)) != null)
                        serviceDepTime = data.getStringExtra(getString(R.string.dep_time))
                    if (data.getStringExtra(getString(R.string.toolbar_title)) != null)
                        service_title = data.getStringExtra(getString(R.string.toolbar_title))
                    if (data.getStringExtra(getString(R.string.service_type)) != null)
                        serviceType = data.getStringExtra(getString(R.string.service_type))


                }
            }

        }
    }
    private fun createSourceList() {
        lifecycleScope.launch(Dispatchers.IO) {
            originList = mutableListOf()
            val dummyList = mutableListOf<String>()

            for (i in resultList.indices) {
                val cityName = resultList[i].origin?.name?.trim()
                if (!cityName.isNullOrEmpty() && !dummyList.contains(cityName)) {
                    dummyList.add(cityName)
                    originList.add(resultList[i].origin ?: Origin())
                }
            }

            if(privilegeDetails?.tsPrivileges?.allowAllToAllSearchInTsMobileApp==true){
                val originData=Origin()
                originData.id="0"
                originData.name="All"
                originList.add(0,originData)
            }
            PreferenceUtils.putOriginCity(ArrayList(originList))
        }

        if (!currentCountry.isNullOrEmpty() && currentCountry.equals("india", true)) {
            setGpsLocation()
        }
    }

    private fun createDestinationList() {
        if (tvSource.text.isNotEmpty()) {
            source = tvSource.text.toString()
            if (sourceId == "0" || destinationId == "0") {
                binding.selectServiceCL.gone()
            } else {
                binding.selectServiceCL.visible()
            }
            lifecycleScope.launch(Dispatchers.IO) {


//                destinationList = arrayListOf()
                val tempDestinationList = arrayListOf<Destination>()

                for (i in 0..resultList.size.minus(1)) {
                    if (finalSourceId.contains(":")) {
                        if (resultList[i].origin?.id.toString().contains("-1")) {
                            val temp = resultList[i].origin?.id.toString().split(":")
                            if (finalSourceId.contains("-1")) {
                                val tempS = finalSourceId.split(":")
                                if (tempS[1] == temp[1]) {
                                    tempDestinationList.add(resultList[i].destination!!)
                                }
                            }
                        } else {
                            if (resultList[i].origin?.id.toString() == finalSourceId) {
                                tempDestinationList.add(resultList[i].destination!!)
                            }
                        }
                    } else {
                        if (resultList[i].origin?.id.toString().contains(":")) {
                            val temp = resultList[i].origin?.id.toString().split(":")
                            if (temp[1] == sourceId) {
                                tempDestinationList.add(resultList[i].destination!!)
                            }
                            val b = tempDestinationList.distinctBy { it.name } as MutableList
                            tempDestinationList.clear()
                            tempDestinationList.addAll(b)

                        } else {
                            if(privilegeDetails?.tsPrivileges?.allowAllToAllSearchInTsMobileApp == true && sourceId=="0"){
                                tempDestinationList.add(resultList[i].destination!!)
                            }
                            else  if (resultList[i].origin?.id.toString() == sourceId
                            ) {
                                val index =
                                    tempDestinationList.indexOfFirst { it.name?.trim() == resultList[i].destination?.name?.trim() }
                                if (index == -1 && resultList[i].destination?.name?.trim() != source.trim() && resultList[i].destination?.id.toString() != sourceId) {
                                    tempDestinationList.add(resultList[i].destination!!)
                                }
                            }
                        }
                    }
                }
                if(privilegeDetails?.tsPrivileges?.allowAllToAllSearchInTsMobileApp == true){
                    val destination=Destination()
                    destination.id="0"
                    destination.name="All"
                    tempDestinationList.add(0,destination)
                }



                val finalDestination =
                    tempDestinationList.distinctBy { it.name } as MutableList<Destination>
                destinationList = ArrayList(finalDestination)
                PreferenceUtils.putDestinationCity(
                    finalDestination
                )
            }
        }
    }

    private fun interCreateDestinationList() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (binding.tvDestination.text.isNullOrEmpty()) {
                PreferenceUtils.putString("recentDestination", "")
            }

            tempRecentSearchList.clear()
            if (tvSource.text.isNotEmpty()) {
                Timber.d("destinationClickTest:0 ${cityStagingIdOnTrue} == ${sourceId} == ${selectedOriginId}")
                if (sourceId.isEmpty()) {
                    selectedOrigin = cityStagingIdOnTrue ?: ""
                } else {
                    selectedOrigin = sourceId
                }


                interDestinationList = mutableListOf()
                for (i in 0..resultList.size.minus(1)) {

                    if (resultList[i].origin?.id.toString() == selectedOrigin) {
                        interDestinationList.add(resultList[i].destination!!)
                    }

                }
                val b = interDestinationList.distinctBy { it.id } as MutableList
                PreferenceUtils.putInterDestinationCity(
                    b
                )

                for (i in 0..recentSearchList.size.minus(1)) {
                    if (recentSearchList[i].dest_id.toString() != selectedOrigin) {
                        tempRecentSearchList.add(recentSearchList[i])
                    }
                }


            }

        }

    }

    private fun clickListener() {
        binding.btnRotate.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.tvSource.setOnClickListener(this)
        binding.tvDestination.setOnClickListener(this)
        binding.tvTodayDate.setOnClickListener(this)
        binding.tvTomorrowDate.setOnClickListener(this)
        binding.tvSelectDate.setOnClickListener(this)
        binding.tvSelectReturnDate.setOnClickListener(this)
        binding.imgClear.setOnClickListener(this)
        binding.btnPnrSearch.setOnClickListener(this)
        binding.busServices.setOnClickListener(this)
        binding.busServiceCross.setOnClickListener(this)
        binding.tvService.setOnClickListener(this)
        binding.btnRotateHumsafar.setOnClickListener(this)
        binding.destinationDropdownTv.setOnClickListener(this)
        binding.busDetailsIncludeLayout.imageCalender.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRotate -> {

                source = tvSource.text.toString()
                destination = tvDestination.text.toString()

                if (tvSource.text.toString()
                        .isNotEmpty() && tvDestination.text.toString()
                        .isNotEmpty()
                ) {

                    val temp = destination
                    destination = source
                    source = temp

                    val tempId = destinationId
                    destinationId = sourceId
                    sourceId = tempId

                    finalDestinationId = destinationId
                    finalSourceId = sourceId

                    tvSource.text = source
                    tvDestination.text = destination


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
                            activity,
                            R.anim.rotate_clockwise
                        )
                    binding.imgRotate.startAnimation(
                        aniRotate
                    )
                }

                firebaseLogEvent(
                    requireContext(),
                    ORIGIN_DESTINATION_SWAP_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    ORIGIN_DESTINATION_SWAP_CLICKS,
                    "Origin-Destination Swap"
                )
            }

            R.id.btnRotateHumsafar -> {

                source = binding.sourceDropdownTv.text.toString()
                destination = binding.destinationDropdownTv.text.toString()

                if (binding.sourceDropdownTv.text.toString()
                        .isNotEmpty() && binding.destinationDropdownTv.text.toString()
                        .isNotEmpty()
                ) {

                    val temp = destination
                    destination = source
                    source = temp

                    val tempId = destinationId
                    destinationId = sourceId
                    sourceId = tempId

                    finalDestinationId = destinationId
                    finalSourceId = sourceId

                    binding.sourceDropdownTv.setText(source)
                    binding.destinationDropdownTv.setText(destination)


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

                    PreferenceUtils.removeKey(PREF_SELECTED_AVAILABLE_ROUTES)
                    invalidateServiceList()


                    val aniRotate =
                        AnimationUtils.loadAnimation(
                            activity,
                            R.anim.rotate_clockwise
                        )
                    binding.imgRotateHumsafar.startAnimation(
                        aniRotate
                    )
                    createDestinationList()
                }

                firebaseLogEvent(
                    requireContext(),
                    ORIGIN_DESTINATION_SWAP_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    ORIGIN_DESTINATION_SWAP_CLICKS,
                    "Origin-Destination Swap"
                )
            }

            R.id.destination_dropdown_tv -> {
                if (binding.sourceDropdownTv.text.toString().isEmpty()) {
                    requireContext().toast(getString(R.string.please_select_source_city))
                } else {
                    if (destinationList.isNotEmpty()) {
                        destinationPopupDialog()
                    } else {
                        lifecycleScope.launch {
                            createDestinationList()
                            delay(100)
                            destinationPopupDialog()
                            // setDestinationListAdapter()
                        }
                    }
                }
            }

            R.id.tvSource -> {
                if ((activity as BaseActivity).getPrivilegeBase() != null) {
                    val privilegeResponse = (activity as BaseActivity).getPrivilegeBase()

                    if (privilegeResponse?.isCityWiseBpDpDisplay == null) {
                        requireContext().toast(getString(R.string.server_error))
                    } else {
                        if (::originList.isInitialized) {
                            if (privilegeResponse.isCityWiseBpDpDisplay && privilegeResponse.availableAppModes?.allowBpDpFare == true) {
                                val intent = Intent(
                                    activity,
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
                                    activity,
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

                    }

                } else {
                    requireContext().toast(requireContext().getString(R.string.server_error))
                }

                firebaseLogEvent(
                    requireContext(),
                    ORIGIN_POINT_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    ORIGIN_POINT_CLICKS,
                    "Origin Point - Booking"
                )
            }

            R.id.tvDestination -> {
                if (tvSource.text.isEmpty()) activity?.toast(
                    getString(R.string.please_select_source_city)
                )
                else if ((activity as BaseActivity).getPrivilegeBase() != null) {
                    val privilegeResponse = (activity as BaseActivity).getPrivilegeBase()
                    if (privilegeResponse?.isCityWiseBpDpDisplay == null) {
                        requireContext().toast(getString(R.string.server_error))
                    } else {

                        if (privilegeResponse.isCityWiseBpDpDisplay && privilegeResponse.availableAppModes?.allowBpDpFare == true) {
                            Timber.d("destinationClickTestflow:00123 ${cityStagingIdOnTrue} , ${sourceId} , ${selectedOrigin}")

                            interCreateDestinationList()

                            val intent = Intent(
                                activity,
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
                                activity,
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
                    requireContext().toast(requireContext().getString(R.string.server_error))
                }

                firebaseLogEvent(
                    requireContext(),
                    DESTINATION_POINT_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    DESTINATION_POINT_CLICKS,
                    "Destination Point- Booking"
                )
            }


            R.id.btnSearch -> {
                if (allowToShowNewFlowInTsApp) {
                    searchBtnNavigationHumsafar(false)
                } else {
                    searchBtnNavigation(false)
                }


                firebaseLogEvent(
                    requireContext(),
                    SEARCH_BUTTON,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    SEARCH_BUTTON,
                    "Search Button - Booking"
                )
            }

            R.id.tvTodayDate -> {
                binding.busServices.text = ""

                setTodayDate()
                searchBtnNavigation(false)

                firebaseLogEvent(
                    requireContext(),
                    TODAY_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    TODAY_CLICKS,
                    "Today Clicks - Booking"
                )
            }

            R.id.tvTomorrowDate -> {
                binding.busServices.text = ""
                if (allowToShowNewFlowInTsApp) {
                    setTomorrowDateHumsafar()
                    searchBtnNavigationHumsafar(false)
                } else {
                    setTomorrowDate()
                    searchBtnNavigation(false)
                }

                firebaseLogEvent(
                    requireContext(),
                    TOMORROW_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    TOMORROW_CLICKS,
                    "Tomorrow Clicks - Booking"
                )
            }

            R.id.tvSelectDate -> {
                selectedDateType = "Single"
                var minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                if (isAllowBookingAfterTravelDate) {
                    minDate = stringToDate("01-01-1900", DATE_FORMAT_D_M_Y)
                }
                if (isAgentLogin) {
                    if (isAllowBookingAfterTravelDate) {
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
                    .show(requireFragmentManager(), TAG)
            }

            R.id.tvSelectReturnDate -> {
                selectedDateType = "Round"

                if (convertedDate != null) {
                    val fromDate: String = getDateDMY(convertedDate!!)!!

                    SlyCalendarDialog()
                        .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                        .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                        .setSingle(true)
                        .setFirstMonday(false)
                        .setCallback(this)
                        .show(requireFragmentManager(), TAG)
                }
            }

            R.id.btn_pnr_Search -> {

                firebaseLogEvent(
                    requireContext(),
                    PNR_SEARCH,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    PNR_SEARCH,
                    "Pnr Search - Booking"
                )
            }

            R.id.image_calender -> {

                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show(requireFragmentManager(), BusDetailsActivity.TAG)
            }

            R.id.tvService -> {

                if (binding.sourceDropdownTv.text.toString().isEmpty()) activity?.toast(
                    requireActivity().getString(R.string.validate_source)
                )
                else if (binding.destinationDropdownTv.text.toString().isEmpty()) activity?.toast(
                    requireActivity().getString(R.string.validate_destination)
                )
                else if (tvSelectDate.text.isEmpty()) activity?.toast(
                    requireActivity().getString(R.string.validate_date)
                ) else {
                    if (convertedDate != null) {
                        availableRoutesApi(
                            finalDestinationId,
                            finalSourceId,
                            getDateYMD(getDateDMY(convertedDate!!)!!)
                        )
                    }
                }


            }

            R.id.bus_services -> {
                if (tvSource.text.isEmpty()) activity?.toast(
                    requireActivity().getString(R.string.validate_source)
                )
                else if (tvDestination.text.isEmpty()) activity?.toast(
                    requireActivity().getString(R.string.validate_destination)
                )
                else if (tvSelectDate.text.isEmpty()) activity?.toast(
                    requireActivity().getString(R.string.validate_date)
                )
                else {
                    PreferenceUtils.apply {
                        setPreference(PREF_SOURCE, tvSource.text.toString())
                        setPreference(PREF_DESTINATION, tvDestination.text.toString())
                        putString(PREF_SOURCE_ID, finalSourceId)
                        setPreference(PREF_DESTINATION_ID, finalDestinationId)
                        setPreference(PREF_SOURCE_ID, finalSourceId)
                        setPreference(PREF_DESTINATION_ID, finalDestinationId)
                    }

                    Timber.d("lastSearchedSource sourceId $sourceId destinationId $destinationId")
                    PreferenceUtils.setPreference(PREF_TRAVEL_DATE,
                        convertedDate?.let { getDateDMY(it) })
                    val intent = Intent(
                        activity,
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
                        requireContext(),
                        ORIGIN_POINT_CLICKS,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        ORIGIN_POINT_CLICKS,
                        "Origin Point - Booking"
                    )
                }
            }

            R.id.bus_service_cross -> {
                binding.busServices.text = ""
                binding.busServiceCross.gone()
            }
        }
    }

    private fun saveSelectedAvailableHumsafar(position: Int) {
        com.bitla.ts.utils.common.availableRoutesList.forEach {
            if (it.reservation_id == position.toLong()) {
                PreferenceUtils.putObject(it.is_apply_bp_dp_fare, SERVICE_IS_APPLY_BP_DP_FARE)

                PreferenceUtils.setPreference("is_bima", it.is_bima)
                PreferenceUtils.putObject(
                    it, PREF_SELECTED_AVAILABLE_ROUTES
                )
                PreferenceUtils.putString((PREF_COACH_NUMBER), it.number)
            }
        }
        PreferenceUtils.setPreference(
            PREF_RESERVATION_ID,
            position
        )
    }

    private fun searchBtnNavigation(isBusService: Boolean) {
        if (tvSource.text.isEmpty()) activity?.toast(
            requireActivity().getString(R.string.validate_source)
        )
        else if (tvDestination.text.isEmpty()) activity?.toast(
            requireActivity().getString(R.string.validate_destination)
        )
        else if (tvSelectDate.text.isEmpty()) activity?.toast(
            requireActivity().getString(R.string.validate_date)
        )
        else {
            PreferenceUtils.apply {
                setPreference(PREF_SOURCE, tvSource.text.toString())
                setPreference(PREF_DESTINATION, tvDestination.text.toString())
                putString(PREF_SOURCE_ID, finalSourceId)
                setPreference(PREF_DESTINATION_ID, finalDestinationId)
                setPreference(PREF_SOURCE_ID, finalSourceId)
                setPreference(PREF_DESTINATION_ID, finalDestinationId)
                setPreference(PREF_TRAVEL_DATE, convertedDate?.let { getDateDMY(it) })
                setPreference(PREF_LAST_SEARCHED_SOURCE, tvSource.text.toString())
                setPreference(PREF_LAST_SEARCHED_DESTINATION, tvDestination.text.toString())
                putString(PREF_NEW_BOOKING_NAVIGATION, FragmentBooking.TAG)
            }

            if (convertedReturnDate == null) {
                if (!binding.busServices.text.isNullOrEmpty()) {
                    PreferenceUtils.setPreference("seatwiseFare", "")
                    val serviceBpDpFare =
                        PreferenceUtils.getObject<String>(SERVICE_IS_APPLY_BP_DP_FARE)
                    Timber.d("serviceBpDPCheck0: $serviceBpDpFare")

                    if (serviceBpDpFare == "true") {
                        Timber.d("serviceBpDPCheck1: $serviceBpDpFare")
                        bpDpService()

                    } else {
                        Timber.d("serviceBpDPCheck2: $serviceBpDpFare")
                        val intent = Intent(requireContext(), NewCoachActivity::class.java)
                        PreferenceUtils.putString("SelectionCoach", "BOOK")
                        PreferenceUtils.putString("fromBusDetails", "bookBlock")
                        startActivity(intent)
                    }

                } else {
                    if(sourceId=="0" || destinationId=="0"){
                        PreferenceUtils.setPreference("isAllToAllSearch", true)
                            PreferenceUtils.setPreference("AllToAllSource", source)
                            PreferenceUtils.setPreference("AllToAllSourceId", sourceId)
                            PreferenceUtils.setPreference("AllToAllDestination",destination)
                            PreferenceUtils.setPreference("AllToAllDestinationId", destinationId)

                    }
                    val intent = Intent(activity, BusDetailsActivity::class.java)

                    PreferenceUtils.putObject(destinationPairModel, "destinationPairModel")
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
        }
    }

    private fun searchBtnNavigationHumsafar(isBusService: Boolean) {
        if (binding.sourceDropdownTv.text.toString().isEmpty()) activity?.toast(
            requireActivity().getString(R.string.validate_source)
        )
        else if (binding.destinationDropdownTv.text.toString().isEmpty()) activity?.toast(
            requireActivity().getString(R.string.validate_destination)
        )
        else if (binding.tvSelectDate.text.isEmpty()) activity?.toast(
            requireActivity().getString(R.string.validate_date)
        )
        else {
            PreferenceUtils.setPreference(PREF_SOURCE, binding.sourceDropdownTv.text.toString())
            PreferenceUtils.setPreference(
                PREF_DESTINATION,
                binding.destinationDropdownTv.text.toString()
            )
            PreferenceUtils.putString(PREF_SOURCE_ID, finalSourceId)
            PreferenceUtils.setPreference(PREF_DESTINATION_ID, finalDestinationId)
            PreferenceUtils.setPreference(PREF_SOURCE_ID, finalSourceId)
            PreferenceUtils.setPreference(PREF_DESTINATION_ID, finalDestinationId)

            PreferenceUtils.setPreference(PREF_TRAVEL_DATE,
                convertedDate?.let { getDateDMY(it) })
            PreferenceUtils.setPreference(
                PREF_LAST_SEARCHED_SOURCE,
                binding.sourceDropdownTv.text.toString()
            )
            PreferenceUtils.setPreference(
                PREF_LAST_SEARCHED_DESTINATION,
                binding.destinationDropdownTv.text.toString()
            )

            PreferenceUtils.putString(
                PREF_NEW_BOOKING_NAVIGATION,
                FragmentBooking.TAG
            )


            if (convertedReturnDate == null) {
                if (!binding.tvService.text.isNullOrEmpty()) {
                    PreferenceUtils.setPreference("seatwiseFare", "")
                    val serviceBpDpFare =
                        PreferenceUtils.getObject<String>(SERVICE_IS_APPLY_BP_DP_FARE)
                    Timber.d("serviceBpDPCheck0: $serviceBpDpFare")

                    if (serviceBpDpFare == "true") {
                        Timber.d("serviceBpDPCheck1: $serviceBpDpFare")
                        bpDpService()

                    } else {
                        Timber.d("serviceBpDPCheck2: $serviceBpDpFare")
                        val intent = Intent(requireContext(), NewCoachActivity::class.java)
                        PreferenceUtils.putString("SelectionCoach", "BOOK")
                        PreferenceUtils.putString("fromBusDetails", "bookBlock")
                        startActivity(intent)
                    }

                } else {
                    if(sourceId=="0" || destinationId=="0"){
                    PreferenceUtils.setPreference("isAllToAllSearch", true)
                    PreferenceUtils.setPreference("AllToAllSource", source)
                    PreferenceUtils.setPreference("AllToAllSourceId", sourceId)
                    PreferenceUtils.setPreference("AllToAllDestination",destination)
                    PreferenceUtils.setPreference("AllToAllDestinationId", destinationId)

                }
                    val intent = Intent(activity, BusDetailsActivity::class.java)
                    PreferenceUtils.putObject(destinationPairModel, "destinationPairModel")
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
        }
    }

    private fun bpDpService() {
        val bpDpBoarding = PreferenceUtils.getBoarding()
        val bpDpDropping = PreferenceUtils.getDropping()
        if (!sourceId.contains("-1") && !destinationId.contains("-1")) {
            if (!sourceId.contains(":") || !destinationId.contains(":")) {
                if (bpDpBoarding?.size == 1 && bpDpDropping?.size == 1) {
                    PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                    PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                    val intent = Intent(requireContext(), NewCoachActivity::class.java)
                    startActivity(intent)
                } else {
                    PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)
                    PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)

                    val intent = Intent(requireContext(), InterBDActivity::class.java)
                    PreferenceUtils.putBoarding(bpDpBoarding)
                    PreferenceUtils.putDropping(bpDpDropping)

                    intent.apply {
                        putExtra("PreSelectedDropping", "false")
                        putExtra("preSelectedBoarding", "false")
                        putExtra(getString(R.string.bus_type), serviceBusType)
                        putExtra(getString(R.string.dep_time), serviceDepTime)
                        putExtra(getString(R.string.service_number), service_ServiceNUmber)
                        putExtra(
                            getString(R.string.toolbar_title),
                            "${getString(R.string.booking)}"
                        )
                        putExtra(getString(R.string.service_type), getString(R.string.proceed))
                    }

                    startActivity(intent)
                }
            } else {
                var tempSource = ""
                var tempDestination = ""
                if (source.contains(",") == true) {
                    val temp = source.split(",")
                    tempSource = temp[0]
                }
                if (destination.contains(",") == true) {
                    val temp = destination.split(",")
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
                PreferenceUtils.putString("SelectionCoach", "BOOK")
                PreferenceUtils.putString("fromBusDetails", "bookBlock")

                val intent = Intent(requireContext(), NewCoachActivity::class.java)
                startActivity(intent)
            }

        } else if (sourceId.contains("-1") && !destinationId.contains("-1")) {
            if (bpDpBoarding?.size == 1 && bpDpDropping?.size == 1) {
                PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                val intent = Intent(requireContext(), NewCoachActivity::class.java)
                startActivity(intent)
            } else {
                if (bpDpBoarding!!.size > 1 && bpDpDropping!!.size == 1) {

                    val intent = Intent(context, InterBDActivity::class.java)
                    PreferenceUtils.putBoarding(bpDpBoarding)
                    PreferenceUtils.putDropping(bpDpDropping)
                    intent.apply {
                        putExtra("PreSelectedDropping", "false")
                        putExtra("preSelectedBoarding", "false")
                        putExtra(getString(R.string.bus_type), serviceBusType)
                        putExtra(getString(R.string.dep_time), serviceDepTime)
                        putExtra(getString(R.string.service_number), service_ServiceNUmber)
                        putExtra(
                            getString(R.string.toolbar_title),
                            "${getString(R.string.booking)}"
                        )
                        putExtra(getString(R.string.service_type), getString(R.string.proceed))
                    }

                    startActivity(intent)
                } else if (bpDpBoarding.size == 1 && bpDpDropping!!.size > 1) {

                    var tempSource = ""
                    if (destination.contains(",") == true) {
                        val temp = destination.split(",")
                        tempSource = temp[0]
                    }
                    bpDpDropping.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_DROPPING_DETAIL)
                        }
                    }
                    PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                    val intent = Intent(requireContext(), NewCoachActivity::class.java)
                    startActivity(intent)
                } else if (bpDpBoarding.size > 1 && bpDpDropping!!.size > 1) {
                    PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)

                    val intent = Intent(requireContext(), InterBDActivity::class.java)
                    intent.putExtra("PreSelectedDropping", "true")
                    intent.putExtra("preSelectedBoarding", "false")
                    PreferenceUtils.putBoarding(bpDpBoarding)
                    PreferenceUtils.putDropping(bpDpDropping)
                    var tempSource = ""
                    if (destination.contains(",") == true) {
                        val temp = destination.split(",")
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
                val intent = Intent(requireContext(), NewCoachActivity::class.java)
                startActivity(intent)
            } else {

                PreferenceUtils.putObject(emptyBoarding, SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)
                PreferenceUtils.putString("SelectionCoach", "BOOK")
                PreferenceUtils.putString("fromBusDetails", "bookBlock")

                val intent = Intent(requireContext(), InterBDActivity::class.java)
                PreferenceUtils.putBoarding(bpDpBoarding)
                PreferenceUtils.putDropping(bpDpDropping)
                intent.apply {
                    putExtra("PreSelectedDropping", "false")
                    putExtra("preSelectedBoarding", "false")
                    putExtra(getString(R.string.bus_type), serviceBusType)
                    putExtra(getString(R.string.dep_time), serviceDepTime)
                    putExtra(getString(R.string.service_number), service_ServiceNUmber)
                    putExtra(getString(R.string.toolbar_title), "${getString(R.string.booking)}")
                    putExtra(getString(R.string.service_type), getString(R.string.proceed))
                }

                startActivity(intent)
            }

        } else if (!sourceId.contains("-1") && destinationId.contains("-1")) {

            if (bpDpBoarding!!.size == 1 && bpDpDropping!!.size == 1) {
                PreferenceUtils.putObject(bpDpBoarding[0], SELECTED_BOARDING_DETAIL)
                PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                val intent = Intent(requireContext(), NewCoachActivity::class.java)
                startActivity(intent)
            } else {
                var tempSource = ""

                if (bpDpBoarding.size > 1 && bpDpDropping!!.size == 1) {
                    if (source.contains(",") == true) {
                        val temp = source.split(",")
                        tempSource = temp[0]
                    }
                    bpDpBoarding.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_BOARDING_DETAIL)
                        }
                    }
                    PreferenceUtils.putObject(bpDpDropping[0], SELECTED_DROPPING_DETAIL)
                    val intent = Intent(requireContext(), NewCoachActivity::class.java)
                    startActivity(intent)
                } else if (bpDpBoarding.size == 1 && bpDpDropping!!.size > 1) {
                    PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)

                    val intent = Intent(requireContext(), InterBDActivity::class.java)
                    PreferenceUtils.putBoarding(bpDpBoarding)
                    PreferenceUtils.putDropping(bpDpDropping)
                    intent.apply {
                       putExtra("PreSelectedDropping", "false")
                       putExtra("preSelectedBoarding", "false")
                       putExtra(getString(R.string.bus_type), serviceBusType)
                       putExtra(getString(R.string.dep_time), serviceDepTime)
                       putExtra(getString(R.string.service_number), service_ServiceNUmber)
                       putExtra(getString(R.string.toolbar_title), "${getString(R.string.booking)}")
                       putExtra(getString(R.string.service_type), getString(R.string.proceed))
                    }

                    startActivity(intent)

                } else if (bpDpBoarding.size > 1 && bpDpDropping!!.size > 1) {
                    PreferenceUtils.putObject(emptyDropping, SELECTED_DROPPING_DETAIL)

                    val intent = Intent(context, InterBDActivity::class.java)
                    PreferenceUtils.putBoarding(bpDpBoarding)
                    PreferenceUtils.putDropping(bpDpDropping)
                    intent.putExtra("PreSelectedDropping", "false")
                    intent.putExtra("preSelectedBoarding", "true")
                    if (source.contains(",") == true) {
                        val temp = source.split(",")
                        tempSource = temp[0]
                    }
                    bpDpBoarding.forEach {
                        if (it.name == tempSource) {
                            PreferenceUtils.putObject(it, SELECTED_BOARDING_DETAIL)
                        }
                    }
                    intent.apply {
                       putExtra(getString(R.string.bus_type), serviceBusType)
                       putExtra(getString(R.string.dep_time), serviceDepTime)
                       putExtra(getString(R.string.service_number), service_ServiceNUmber)
                       putExtra(getString(R.string.toolbar_title), "${getString(R.string.booking)}")
                       putExtra(getString(R.string.service_type), getString(R.string.proceed))
                    }

                    startActivity(intent)
                }
            }
        }
    }

    private fun tomorrowDateColor() {
        binding.tvTodayDate.setTextColor(requireContext().resources.getColor(R.color.button_default_color))
        binding.tvTomorrowDate.setTextColor(requireContext().resources.getColor(R.color.colorPrimary))
    }

    private fun todayDateColor() {
        if (isAttachedToActivity()) {
            binding.tvTodayDate.setTextColor(requireActivity().resources.getColor(R.color.colorPrimary))
            binding.tvTomorrowDate.setTextColor(requireActivity().resources.getColor(R.color.button_default_color))
        }
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    private fun navigateToAvailableRoute(journeyDate: String) {

    }

    private fun setTomorrowDate() {
        if (tvSource.text.isEmpty()) activity?.toast(requireActivity().getString(R.string.validate_source))
        else if (tvDestination.text.isEmpty()) activity?.toast(requireActivity().getString(R.string.validate_destination))
        else if (tvSelectDate.text.isEmpty()) activity?.toast(requireActivity().getString(R.string.validate_date))
        else {
            tomorrowDateColor()
            tvSelectDate.text = thFormatDateMMMOutput(getDateYMD(getTomorrowDate()))
            val journeyDate = getDateYMD(getTomorrowDate())
            convertedDate = getDateYMD(getTomorrowDate())
            binding.tvSelectReturnDate.text = getString(R.string.empty)
            returnDate = getString(R.string.empty)
            navigateToAvailableRoute(journeyDate)

        }
    }

    private fun setTomorrowDateHumsafar() {
        if (binding.sourceDropdownTv.text.toString().isEmpty()) activity?.toast(requireActivity().getString(R.string.validate_source))
        else if (binding.destinationDropdownTv.text.toString().isEmpty()) activity?.toast(
            requireActivity().getString(
                R.string.validate_destination
            )
        )
        else if (tvSelectDate.text.isEmpty()) activity?.toast(requireActivity().getString(R.string.validate_date))
        else {
            tomorrowDateColor()
            tvSelectDate.text = thFormatDateMMMOutput(getDateYMD(getTomorrowDate()))
            val journeyDate = getDateYMD(getTomorrowDate())
            convertedDate = getDateYMD(getTomorrowDate())
            binding.tvSelectReturnDate.text = getString(R.string.empty)
            returnDate = getString(R.string.empty)
            navigateToAvailableRoute(journeyDate)

        }
    }

    private fun setTodayDate() {
        when {
            tvSource.text.isEmpty() -> activity?.toast(getString(R.string.please_select_source_city))
            tvDestination.text.isEmpty() -> activity?.toast(getString(R.string.please_select_destination_city))
            tvSelectDate.text.isEmpty() -> activity?.toast(getString(R.string.please_select_date))
            else -> {
                tvSelectDate.text = thFormatDateMMMOutput(getDateYMD(getTodayDate()))
                todayDateColor()
                convertedDate = getDateYMD(getTodayDate())
                val journeyDate = getDateYMD(getTodayDate())
                returnDate = getString(R.string.empty)
                binding.tvSelectReturnDate.text = getString(R.string.empty)
                navigateToAvailableRoute(journeyDate)

            }
        }
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    @SuppressLint("LogNotTimber")
    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            when {
                view.tag == getString(R.string.most_frequently_booked) -> {
                    if (busStageData.size > 0) {
                        tvSource.text = busStageData[position].title.split("-")[0]
                        tvDestination.text = busStageData[position].title.split("-")[1]
                        sourceId = busStageData[position].origin_id.toString()
                        destinationId = busStageData[position].destination_id.toString()
                        binding.busServices.text = ""
                        finalDestinationId = destinationId
                        finalSourceId = sourceId

                        searchBtnNavigation(false)
                    }
                }

                view.tag == "BOOKING" && busStageData.size > 0 && busStageData[position].title.contains(
                    "-"
                ) -> {


                    tvSource.text = busStageData[position].title.split("-")[0]
                    tvDestination.text = busStageData[position].title.split("-")[1]
                    sourceId = busStageData[position].origin_id.toString()
                    destinationId = busStageData[position].destination_id.toString()
                    binding.busServices.text = ""
                    finalDestinationId = destinationId
                    finalSourceId = sourceId

                    PreferenceUtils.apply {
                       putString(PREF_SOURCE_ID, finalSourceId)
                       putString(PREF_DESTINATION_ID, finalDestinationId)
                       putString(PREF_LAST_SEARCHED_SOURCE, source)
                       putString(PREF_LAST_SEARCHED_DESTINATION, destination)
                    }
                    searchBtnNavigation(false)
                }

                else -> {
                    newBookingSelection()

                    firebaseLogEvent(
                        context = requireContext(),
                        logEventName = NEW_BOOKING,
                        loginId = loginModelPref.userName,
                        operatorName = loginModelPref.travels_name,
                        roleName = loginModelPref.role,
                        eventKey = NEW_BOOKING,
                        eventValue = "New Booking - Booking"
                    )
                }
            }

            when {
                view.tag == requireActivity().getString(R.string.tag_book_seat) -> {
                    saveSelectedAvailableRoute(position)
                    PreferenceUtils.setPreference("seatwiseFare", "fromBulkShiftPassenger")
                    val intent = Intent(requireContext(), NewCoachActivity::class.java)
                    PreferenceUtils.putString("SelectionCoach", "BOOK")
                    PreferenceUtils.putString("fromBusDetails", "bookBlock")
                    removeKey("seatwiseFare")
                    removeKey("isEditSeatWise")
                    removeKey("PERSEAT")
                    startActivity(intent)
                }

                view.tag == "viewReservation" -> {
                    resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
                    PreferenceUtils.apply {
                        putString("ViewReservation_date", "${ymdCurrentDate}")
                        setPreference("BUlK_shifting", false)
                        putString("BulkShiftBack", "")
                        setPreference("shiftPassenger_tab", 0)
                        setPreference("seatwiseFare", "fromBulkShiftPassenger")
                    }

                    val intent = Intent(context, ViewReservationActivity::class.java)
                    intent.putExtra("pickUpResid", resID)
                    startActivity(intent)
                }

                view.tag == getString(R.string.delete_recent_search) -> {
                    try {
                        callDeleteRecentSearchApi(position)
                    } catch (e: Exception) {
                        Timber.d("$e")
                    }
                }

                view.tag == getString(R.string.open_calender) -> {
                    var minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                    if (isAllowBookingAfterTravelDate) {
                        minDate = stringToDate("01-01-1900", DATE_FORMAT_D_M_Y)
                    }
                    //openDateDialog()
                    if (isAgentLogin) {
                        minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                    }
                    SlyCalendarDialog()
                        .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                        .setMinDate(minDate)
                        .setSingle(true)
                        .setFirstMonday(false)
                        .setCallback(this)
                        .show(childFragmentManager, BusDetailsActivity.TAG)

                    updatedList.clear()
                    filteredList.clear()
                }

                view.tag == getString(R.string.edit_chart) -> {
                    serviceApiType = getString(R.string.edit_chart)
                    resId = availableRoutesList[position].reservation_id
                    saveSelectedAvailableRoute(position)
                    callServiceApi()
                }

                view.id == R.id.layout_book_ticket || view.tag == getString(R.string.tag_book_seat) -> {
                    if (availableRoutesList.isNotEmpty()) {
                        saveSelectedAvailableRoute(position)
                        PreferenceUtils.setPreference("seatwiseFare", "fromBulkShiftPassenger")
                        val intent = Intent(requireContext(), NewCoachActivity::class.java)
                        PreferenceUtils.putString("SelectionCoach", "BOOK")
                        PreferenceUtils.putString("fromBusDetails", "bookBlock")
                        removeKey("seatwiseFare")
                        removeKey("isEditSeatWise")
                        removeKey("PERSEAT")

                        startActivity(intent)
                    }
                }

                view.tag == getString(R.string.tag_block_seat) -> {
                    if (availableRoutesList.isNotEmpty()) {
                        saveSelectedAvailableRoute(position)
                    }
                    PreferenceUtils.setPreference("seatwiseFare", "fromBulkShiftPassenger")
                    val intent = Intent(requireContext(), BlockActivity::class.java)
                    PreferenceUtils.putString("SelectionCoach", "BLOCK")
                    PreferenceUtils.putString("fromBusDetails", "bookBlock")
                    removeKey("seatwiseFare")
                    removeKey("isEditSeatWise")
                    removeKey("PERSEAT")
                    startActivity(intent)
                }

                view.tag == "BOOKING" && busStageData.size > 0 && busStageData[position].title.contains(
                    "-"
                ) -> {
                    source = busStageData[position].title.split("-")[0]
                    destination = busStageData[position].title.split("-")[1]
                    sourceId = busStageData[position].origin_id.toString()
                    destinationId = busStageData[position].destination_id.toString()

                    PreferenceUtils.putString(PREF_SOURCE, source)
                    PreferenceUtils.putString(PREF_SOURCE_ID, sourceId)
                    selectedOrigin = sourceId
                    PreferenceUtils.putString("recentOrigin", sourceId)
                    PreferenceUtils.putString("recentDestination", destinationId)
                    PreferenceUtils.putString(PREF_DESTINATION, destination)
                    updatedList.clear()
                    filteredList.clear()
                }

                view.tag == "BOOKING" && busStageData.size > 0 && busStageData[position].title == getString(
                    R.string.new_booking
                ) -> {
                    updatedList.clear()
                    filteredList.clear()
                    PreferenceUtils.putString("recentOrigin", "")
                    PreferenceUtils.putString("recentDestination", "")
                    selectedOrigin = ""
                }

                view.tag == getString(R.string.edit) -> {
                    updatePassengersDialog(position)
                }

                else -> {
                    if (position < dateList.size) {
                        ymdDate = inputFormatToOutput(
                            dateList[position].title,
                            DATE_FORMAT_MMM_DD_EEE_YYYY,
                            DATE_FORMAT_Y_M_D
                        ).replace("1970", getCurrentYear())
                        binding.busDetailsIncludeLayout.rvBusDetails.visibility = View.GONE
                        if (role == getString(R.string.role_field_officer) && !isAllowBookingForAllServices) {
                            allotedDirectService(1, ymdDate, "", "", null)
                        }
                        updatedList.clear()
                    }
                }
            }
        }
    }

    private fun newBookingSelection() {
        binding.busServices.text = ""
        binding.busServiceCross.gone()

        if (::tvSource.isInitialized) {
            tvSource.text = ""
            source = ""
            services = ""
            checkSource()

        }
        if (::tvDestination.isInitialized) {
            tvDestination.text = ""
            destination = ""
            services = ""
        }
    }

    private fun callDeleteRecentSearchApi(position: Int) {
        selectedOriginId = busStageData[position].origin_id.toString()
        selectedDestinationId = busStageData[position].destination_id.toString()

        val reqBody = com.bitla.ts.domain.pojo.delete_recent_search.request.ReqBody(
            api_key = loginModelPref.api_key,
            origin_id = selectedOriginId.toString(),
            destination_id = selectedDestinationId.toString(),
            locale = locale
        )
        sharedViewModel.deleteRecentSearchApi(
            reqBody,
            delete_recent_search_method_name
        )
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    private fun getDataFromActivity(vararg input: String) {
        for (i in 0..input.size.minus(1)) {
            if (input[2] == getString(R.string.selectSource)) {
                sourceId = input[0]
                tvSource.text = input[1]
                source = input[1]
                tvDestination.text = ""
                destination = ""
                destinationId = ""
                PreferenceUtils.putString(PREF_DESTINATION, destination)
                PreferenceUtils.putString(PREF_DESTINATION_ID, finalDestinationId)
// adding data to destination list
                createDestinationList()

                if (binding.progress.isVisible) {
                    binding.progress.isVisible = false
                }
            } else {

                destinationId = input[0]
                tvDestination.text = input[1]
// adding data to source list
                Timber.i("AllPairsApi: ${input[1]}+${destinationId}")
                if (binding.progress.isVisible) {
                    binding.progress.isVisible = false
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCalenderDate(date: String) {
        if (selectedDateType == "Single") {
            val roundJourney = binding.tvSelectReturnDate.text.toString()
            val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            val sdfSingle: Date = sdf.parse(date)
            if (roundJourney.isNotEmpty()) {
                val sdfRound: Date = sdf.parse(roundJourney)
                if (sdfSingle.after(sdfRound)) {
                    setDate(date)
                }
            } else {
                tvSelectDate.text = date
                PreferenceUtils.putString(PREF_TRAVEL_DATE, date)
                when (date) {
                    thFormatDateMMMOutput(getDateYMD(getTodayDate())) -> todayDateColor()
                    thFormatDateMMMOutput(getDateYMD(getTomorrowDate())) -> tomorrowDateColor()
                    else -> calenderDateColor()
                }
            }
        } else {
            val singleJourney = tvSelectDate.text.toString()
            val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            val sdfSingle: Date = sdf.parse(singleJourney)
            val sdfRound: Date = sdf.parse(date)

            if (sdfSingle.after(sdfRound)) {
                setDate(date)
            } else binding.tvSelectReturnDate.text = date
            binding.imgClear.visible()
            binding.tvOptional.gone()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDate(date: String) {
        val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_M_Y)
        val currentDate = sdfDMY.parse(date)
        currentDate.date = currentDate.date.plus(1)
        val afterDate = sdfDMY.format(currentDate)
        tvSelectDate.text = date
        PreferenceUtils.putString(PREF_TRAVEL_DATE, date)
        binding.tvSelectReturnDate.text = afterDate
        binding.imgClear.visible()
        binding.tvOptional.gone()
    }

    private fun calenderDateColor() {
        binding.tvTodayDate.setTextColor(requireContext().resources.getColor(R.color.button_default_color))
        binding.tvTomorrowDate.setTextColor(requireContext().resources.getColor(R.color.button_default_color))
    }


    /*
    * this method to used for start Shimmer Effect
    * */
    private fun startShimmerEffect() {
        binding.includeProgress.progressBar.visible()

        binding.shimmerBookTicket.visible()
        binding.shimmerBookTicket.startShimmer()

        binding.noData.gone()
        binding.tvNoService.gone()
        binding.myBookingBookTicketContainer.gone()
        binding.busDetailsContainer.gone()
    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.includeProgress.progressBar.gone()

        binding.shimmerBookTicket.gone()
        if (binding.shimmerBookTicket.isShimmerStarted) {
            binding.shimmerBookTicket.stopShimmer()
        }

        if ((activity as BaseActivity).getPrivilegeBase()?.boLicenses?.allowBookingForAllServices == false
            && (activity as BaseActivity).getPrivilegeBase()?.boLicenses?.allowBookingForAllotedServices == true
            && role == getString(R.string.role_field_officer)
        ) {
            binding.myBookingBookTicketContainer.gone()
        } else {
            binding.myBookingBookTicketContainer.visible()
        }
    }

    private fun callServiceApi() {
        binding.busDetailsIncludeLayout.includeProgress.progressBar.visible()

        if (requireContext().isNetworkAvailable()) {
            sharedViewModel.getServiceDetails(
                reservationId = resId.toString(),
                apiKey = loginModelPref.api_key,
                originId = sourceId,
                destinationId = destinationId,
                operatorApiKey = operator_api_key,
                locale = locale!!,
                apiType = service_details_method,
                excludePassengerDetails = false
            )

        } else {
            requireContext().noNetworkToast()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setBusDetailsObserver() {

        pickUpChartViewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.LOADING -> {
                    if (role != getString(R.string.role_field_officer)) {
                        startShimmerEffect()
                    }
                }

                LoadingState.LOADED -> binding.busDetailsIncludeLayout.includeProgress.progressBar.gone()
                else -> binding.busDetailsIncludeLayout.includeProgress.progressBar.gone()
            }
        }

//      get next seven days date with current date

        ymdDate = getTodayDate()
        sevenDaysDate = getTodayDate()
        pickUpChartViewModel.getNextCalenderDates(sevenDaysDate, ymdDate)

        pickUpChartViewModel.dataAllotedServiceDirect.observe(viewLifecycleOwner) {
            searchList1.clear()

            if (it != null) {

                when (it.code) {
                    200 -> {
                        service = it.services
                            ?: listOf<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>()
                        for (i in 0..service.size.minus(1)) {
                            if (service[i].status.equals(
                                    requireContext().getString(R.string.active),
                                    true
                                )
                            )
                                searchList1.add(service[i])
                        }
                        setBusDetailsAdapter(searchList1)

                        binding.noData.gone()
                        binding.tvNoService.gone()
                        binding.busDetailsContainer.visible()
                        layoutVisibility(searchList1)
                        stopShimmerEffect()
                    }

                    401 -> {

                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        binding.apply {
                            busDetailsContainer.gone()
                            noData.visible()
                            tvNoService.visible()
                            tvNoService.text = it.result?.message ?: ""
                        }
                        stopShimmerEffect()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))

            }
        }

        sharedViewModel.deleteSearch.observe(viewLifecycleOwner) {
            binding.busDetailsIncludeLayout.includeProgress.progressBar.gone()
            if (it != null) {
                if (it.code == 200) {
                    if (busStageData.any { it.origin_id == selectedOriginId && it.destination_id == selectedDestinationId }) {
                        val index =
                            busStageData.indexOfFirst { it.origin_id == selectedOriginId && it.destination_id == selectedDestinationId }
                        busStageData.removeAt(index)
                        binding.busDetailsIncludeLayout.rvBookingDetails.adapter?.notifyDataSetChanged()
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

        sharedViewModel.serviceDetails.observe(viewLifecycleOwner) {
            binding.busDetailsIncludeLayout.includeProgress.progressBar.gone()
            if (it.code == 200) {
                if (serviceApiType != null) {
                    boardingList = mutableListOf()
                    droppingList = mutableListOf()
                    stageDetails = it.body.stageDetails!!
                    for (i in 0..it.body.stageDetails?.size!!.minus(1)) {
                        if (it?.body?.stageDetails!![i].cityId.toString() == sourceId) {
                            generateBoardingList(i)
                        } else {
                            generateDroppingList(i)
                        }
                    }


                    val availableSeatList = mutableListOf<String>()
                    val passengerList = mutableListOf<PassengerDetails>()
                    if (serviceApiType == getString(R.string.edit_chart)) {
                        val seatDetails: List<SeatDetail>? = it?.body?.coachDetails?.seatDetails
                        seatDetails?.forEach { it ->
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
                        }
                        availableSeats(availableSeats = availableSeatList)
                        setPassengerDetails(passengerDetails = passengerList)
                        val intent = Intent(requireContext(), EditChartActivity::class.java)
                        startActivity(intent)
                    } else {
                        if (boardingList != null && boardingList!!.isNotEmpty()) {
                            boardingPoint = boardingList!![0].name!!
                            PreferenceUtils.putObject(
                                boardingList!![0],
                                PREF_BOARDING_STAGE_DETAILS
                            )
                        }
                        if (droppingList != null && droppingList!!.isNotEmpty()) {
                            droppingPoint = droppingList!![0].name!!
                            PreferenceUtils.putObject(
                                droppingList!![0],
                                PREF_DROPPING_STAGE_DETAILS
                            )
                        }

                        rapidBookingDialog = DialogUtils.rapidBookingDialog(
                            boardingPoint = boardingPoint,
                            droppingPoint = droppingPoint,
                            context = requireContext(),
                            varArgListener = this
                        )!!
                    }
                }
            } else
                it.message?.let { it1 -> requireContext().toast(it1) }
        }

        pickUpChartViewModel.listOfDates.observe(viewLifecycleOwner) {
            dateList = it
            setDatesAdapter()
        }
    }

    private fun setDatesAdapter() {
        binding.rvDateDetails.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDateDetails.adapter =
            MyBookingsDatesAdapter(
                context = requireContext().applicationContext,
                onItemClickListener = this,
                menuList = dateList,
                isShowCalendar = true
            )
    }

    private fun generateBoardingList(i: Int) {
        if (boardingList != null) {
            boardingList?.add(stageDetails[i])
            setBoardingList(boardings = boardingList!!)
            Timber.d("boardingList ${boardingList?.size}")
        }
    }

    private fun generateDroppingList(i: Int) {
        if (droppingList != null) {
            droppingList?.add(stageDetails[i])
            setDroppingList(droppings = droppingList!!)
            Timber.d("droppingList ${droppingList?.size}")
        }

    }

    private fun setBusDetailsAdapter(finallist: ArrayList<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>) {

            binding.busDetailsIncludeLayout.rvBusDetails.visible()
            binding.busDetailsIncludeLayout.rvBusDetails.layoutManager =
                LinearLayoutManager(
                    /* context = */ requireContext(),
                    /* orientation = */ LinearLayoutManager.VERTICAL,
                    /* reverseLayout = */ false)

            searchListAdapter = MyReservationAdapterBook(
                context = requireContext(),
                searchList = finallist,
                privilegeDetails = privilegeDetails,
                loginModelPref = loginModelPref,
                onItemClickListener = this
            ) { menuItemPosition, itemPosition ->


                when (menuItemPosition) {
                    0 -> {
                        removeKey(PREF_BOARDING_STAGE_DETAILS)
                        removeKey(PREF_DROPPING_STAGE_DETAILS)
                        saveSelectedAvailableRoute(itemPosition)
                        resId = service[itemPosition].reservationId?.toLong()
                        busType = service[itemPosition].busType
                        depTime = service[itemPosition].departureTime
                        serviceApiType = getString(R.string.rapid_booking)
                        callServiceApi()
                    }

                    1 -> {
                        firebaseLogEvent(
                            requireActivity(),
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
                        val intent = Intent(requireActivity(), SmsNotificationActivity::class.java)
                        startActivity(intent)
                    }

                    5 -> {
                        resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
                        PreferenceUtils.apply {
                            putString("ViewReservation_date", "${ymdCurrentDate}")
                            setPreference("BUlK_shifting", false)
                            putString("BulkShiftBack", "")
                            setPreference("shiftPassenger_tab", 0)
                            setPreference("seatwiseFare", "fromBulkShiftPassenger")
                        }

                        val intent = Intent(context, ViewReservationActivity::class.java)
                        intent.putExtra("pickUpResid", resID)
                        startActivity(intent)
                    }
                }

            }
            binding.busDetailsIncludeLayout.rvBusDetails.adapter = searchListAdapter
    }

    private fun setAdapters() {
        busStageData = mutableListOf()
        var isNewBooking = true

        // checking for selected recent searched
        if (::recentSearchModel.isInitialized) {
            if (recentSearchModel.recent_search != null) {
                recentSearchModel.recent_search.forEach {
                    val isSelected = "${it.origin_name}-${it.dest_name}" == "$source-$destination"
                    if (it.dest_name != null) {
                        busStageData.add(
                            StageData(
                                "${it.origin_name}-${it.dest_name}",
                                isRemovable = true,
                                isSelected = isSelected,
                                layoutType = "BOOKING",
                                origin_id = it.origin_id,
                                destination_id = it.dest_id
                            )
                        )
                    }
                }
            }
        }

        // checking for "New Booking"
        if (busStageData.any { stageData -> stageData.isSelected })
            isNewBooking = false

        // "New Booking" added at first position
        busStageData.add(
            0,
            StageData(
                getString(R.string.new_booking),
                isRemovable = false,
                isSelected = isNewBooking,
                layoutType = "BOOKING"
            )
        )

        binding.busDetailsIncludeLayout.rvBookingDetails.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.busDetailsIncludeLayout.rvBookingDetails.adapter =
            BookingStageAdapter(requireContext(), this, busStageData, true)
    }

    private fun saveSelectedAvailableRoute(position: Int) {
        PreferenceUtils.setPreference(
            PREF_RESERVATION_ID,
            service[position].reservationId
        )

        PreferenceUtils.putObject(
            service[position], PREF_SELECTED_AVAILABLE_ROUTES
        )
    }

    @SuppressLint("LogNotTimber")
    override fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        busData: com.bitla.ts.domain.pojo.available_routes.Result,
    ) {
        removeKey(PREF_BOARDING_STAGE_DETAILS)
        removeKey(PREF_DROPPING_STAGE_DETAILS)
        saveSelectedAvailableRoute(itemPosition)
        resId = availableRoutesList[itemPosition].reservation_id
        busType = availableRoutesList[itemPosition].bus_type
        depTime = availableRoutesList[itemPosition].dep_time
        when (menuPosition) {
            1 -> {
                serviceApiType = getString(R.string.rapid_booking)
                callServiceApi()
            }
        }
    }

    override fun onCancelled() {

    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?,
        hours: Int,
        minutes: Int,
    ) {
        if (firstDate != null) {
            binding.busServices.text = ""

            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours)
                firstDate.set(Calendar.MINUTE, minutes)

                travelDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)
                //convertedDate = getDateYMD(getTomorrowDate())

                convertedDate = SimpleDateFormat(
                    DATE_FORMAT_Y_M_D,
                    Locale.getDefault()
                ).format(firstDate.time)
                binding.busDetailsIncludeLayout.rvBusDetails.gone()
                sevenDaysDate = travelDate
                pickUpChartViewModel.getNextCalenderDates(sevenDaysDate, travelDate)

                /*ymdDate = inputFormatToOutput(
                    travelDate,
                    DATE_FORMAT_D_M_Y,
                    DATE_FORMAT_Y_M_D
                )*/
                tvSelectDate.text = thFormatDateMMMOutput(convertedDate!!)
                PreferenceUtils.putString(PREF_TRAVEL_DATE, travelDate)
                binding.busServiceCross.gone()

                if (!convertedDate.isNullOrEmpty()) {
                    when (thFormatDateMMMOutput(convertedDate!!)) {
                        thFormatDateMMMOutput(getDateYMD(getTodayDate())) -> todayDateColor()
                        thFormatDateMMMOutput(getDateYMD(getTomorrowDate())) -> tomorrowDateColor()
                        else -> calenderDateColor()
                    }
                }

                if (role == getString(R.string.role_field_officer) && !isAllowBookingForAllServices) {
                    if (!convertedDate.isNullOrEmpty()) {
                        allotedDirectService(
                            page_count = 1,
                            travelDate = convertedDate.toString(),
                            originID = "",
                            destinationId = "",
                            selectedHubId = null
                        )
                    }
                }
            }
        }
    }

    override fun onButtonClick(vararg args: Any) {
        if (args.isNotEmpty()) {
            if (args[0] == getString(R.string.confirm)) {
                val noOfTickets: String = args[1] as String
                val intent = Intent(requireContext(), QuickBookingActivity::class.java)
                intent.putExtra("SEATS", noOfTickets)
                intent.putExtra(getString(R.string.boarding_point), boardingPoint)
                intent.putExtra(getString(R.string.dropping_point), droppingPoint)
                startActivity(intent)
            } else if (args[0] == getString(R.string.boarding_at)) {
                val intent =
                    Intent(requireContext(), SelectBoardingDroppingPointActivity::class.java)
                intent.putExtra(getString(R.string.tag), getString(R.string.boarding))
                intent.putExtra(getString(R.string.boarding), boardingList as Serializable)
                intent.putExtra(getString(R.string.dropping), droppingList as Serializable)
                intent.putExtra(getString(R.string.bus_type), busType)
                intent.putExtra(getString(R.string.dep_time), depTime)
                intent.putExtra(
                    getString(R.string.toolbar_title),
                    getString(R.string.rapid_booking)
                )
                startActivity(intent)
            } else if (args[0] == getString(R.string.drop_off_at)) {
                val intent =
                    Intent(requireContext(), SelectBoardingDroppingPointActivity::class.java)
                intent.putExtra(getString(R.string.tag), getString(R.string.dropping))
                intent.putExtra(getString(R.string.boarding), boardingList as Serializable)
                intent.putExtra(getString(R.string.dropping), droppingList as Serializable)
                intent.putExtra(
                    getString(R.string.toolbar_title),
                    getString(R.string.rapid_booking)
                )
                startActivity(intent)
            }
        }
    }

    private fun layoutVisibility(list: ArrayList<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>) {

        if (list.isNotEmpty()) {
            binding.busDetailsIncludeLayout.rvBusDetails.visible()
            binding.busDetailsIncludeLayout.filters.gone()
        } else {
            binding.busDetailsIncludeLayout.rvBusDetails.gone()
            binding.busDetailsIncludeLayout.filters.gone()
        }
    }


    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            // clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }


    private fun allotedDirectService(
        page_count: Int,
        travelDate: String,
        originID: String,
        destinationId: String,
        selectedHubId: Int?
    ) {
        val orId: String?
        val destId: String?
        var hubId: Int?
        if (originID.isEmpty())
            orId = ""
        else
            orId = originID
        if (destinationId.isEmpty())
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
            is_group_by_hubs = groupByHubs,
            hub_id = hubId,
            api_key = loginModelPref.api_key,
            travel_date = travelDate,
            page = page_count,
            per_page = 7,
            view_mode = "",
            pagination = true,
            origin = orId,
            destination = destId,
            locale = locale, isCheckingInspector = null,
            serviceFilter = null
        )

        pickUpChartViewModel.allotedServiceApiDirect(
            allotedDirectRequest,
            lock_chart_method_name
        )
    }

    override fun onPnrSelection(tag: String, pnr: Any, doj: Any?) {
        if (tag == getString(R.string.booking_edit)) {

            editPassengerSheet.showEditPassengersSheet(pnr)

        } else if (tag == getString(R.string.booking_close)) {
            cancelTicketSheet.showTicketCancellationSheet(pnr)
        }
    }

    private fun remoteConfigDestinationPairCacheCheck() {
        RemoteConfigUpdateHelper.with(requireContext()).onDestinationPairCheck(this).check()
    }

    override fun onCheckRemoteConfigUpdateListener(
        destinationPairTime: Int?,
        isDestinationPairCacheEnable: Boolean?
    ) {

        if (isDestinationPairCacheEnable != null) {
            isDestinationPairCache = isDestinationPairCacheEnable
        }
        if (destinationPairTime != null) {
            destinationPairCount = destinationPairTime
        }
    }


// humsafar
//for service
    private fun availableRoutesApi(destinationId: String, sourceId: String, ymdDate: String) {
        bccId = PreferenceUtils.getBccId()
        var isBima: Boolean? = null
        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }
        var isCsShared: Boolean? = null
        if (isBima == true) {
            isCsShared = true
        }
        val ymdDat = getDateYMD(ymdDate)

        if (requireActivity().isNetworkAvailable()) {
            binding.progress.visible()
            binding.tvService.isEnabled = false
            availableRoutesViewModel.availableRoutesApi(
                apiKey = loginModelPref.api_key,
                originId = sourceId,
                destinationId = destinationId,
                showInJourneyServices = "true",
                isCsShared = isCsShared ?: false,
                operatorkey = operator_api_key,
                responseFormat = format_type,
                travelDate = ymdDate,
                showOnlyAvalServices = "fixed",
                locale = locale ?: "en",
                apiType = available_routes_method_name,
                appBimaEnabled = isBima ?: false
            )
        } else
            requireActivity().noNetworkToast()
    }

    private fun setUpServiceListObserver() {
        availableRoutesViewModel.dataAvailableRoutes.observe(viewLifecycleOwner) {

            binding.progress.gone()
            binding.tvService.isEnabled = true

            if (it != null) {
                if (it.code == 200) {

                    PreferenceUtils.putObject(it, PREF_AVAILABLE_ROUTES_RESPONSE)
                    if (it.result.isNullOrEmpty()) {
                        requireContext().toast(it.message)
                    } else {
                        val list: MutableList<com.bitla.ts.domain.pojo.available_routes.Result> =
                            it.result
                        com.bitla.ts.utils.common.availableRoutesList.clear()
                        com.bitla.ts.utils.common.availableRoutesList = list
                        searchList = mutableListOf()

                        if (com.bitla.ts.utils.common.availableRoutesList.isNotEmpty()) {
                            for (i in 0..com.bitla.ts.utils.common.availableRoutesList.size.minus(1)) {
                                var cityModel = SearchModel()
                                cityModel.id =
                                    com.bitla.ts.utils.common.availableRoutesList[i].id.toString()
                                cityModel.name =
                                    com.bitla.ts.utils.common.availableRoutesList[i].number
                                cityModel.isAllowMultistationBlockedService =
                                    com.bitla.ts.utils.common.availableRoutesList[i].is_allow_multistation_blocked_service
                                cityModel.multistationAllowedTime =
                                    com.bitla.ts.utils.common.availableRoutesList[i].multistation_allowed_time
                                searchList.add(cityModel)

                            }

                            searchList.sortBy { it.name?.lowercase() }
                            servicePopupDialog()
                        }
                    }

                } else if (it.code == 401) {
                    DialogUtils.unAuthorizedDialog(
                        requireContext(),
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )
                } else {
                    requireContext().toast(it.message)
                    invalidateServiceList()
                }
            } else {
                requireActivity().toast(getString(R.string.server_error))
                invalidateServiceList()
            }

        }
    }


    private fun sourcePopupDialog() {
        if (!::originList.isInitialized) {
            return
        }
        var popupBinding: AdapterSearchBpdpBinding? = null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(requireContext()))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val sortedList = originList.sortedBy { it.name }

        sourceNewAdapter = SimpleListAdapter(requireContext(), sortedList.toMutableList(), this, SOURCE)
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
            popupBinding.root,binding.sourceDropdownTv.width, FrameLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        val xOff = 0
        val yOff = binding.sourceDropdownTv.height

        sourcePopupWindow?.showAsDropDown(binding.tvSource,xOff,yOff)

        sourcePopupWindow?.elevation=25f


        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }


    private fun destinationPopupDialog(){
        var popupBinding : AdapterSearchBpdpBinding ?= null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(requireContext()))



        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)


        val destList : MutableList<Origin> = arrayListOf()


    for (i in 0 until destinationList.size) {
        val obj = Origin()
        obj.id = destinationList[i].id
        obj.name = destinationList[i].name
        destList.add(obj)
    }

        val sortedList = destList.sortedBy { it.name }


        destinationNewAdapter = SimpleListAdapter(requireContext(), sortedList.toMutableList(), this, DESTINATION)
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
            popupBinding.root,binding.destinationDropdownTv.width, FrameLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        val xOff = 0
        val yOff = binding.destinationDropdownTv.height

        destinationPopupWindow?.showAsDropDown(binding.tvDestination,xOff,yOff)

        destinationPopupWindow?.elevation=25f


        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            destinationPopupWindow?.dismiss()
            true
        }
    }

    private fun servicePopupDialog(){
        var popupBinding : AdapterSearchBpdpBinding ?= null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(requireContext()))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)

        binding.nestedScrollView5.smoothScrollTo(0, 500)

        val destList : MutableList<Origin> = arrayListOf()

        for (i in 0 until searchList.size){
            val obj  = Origin()
            obj.id = searchList[i].id.toString()
            obj.name = searchList[i].name
            destList.add(obj)
        }

        serviceNewAdapter = SimpleListAdapter(requireContext(),destList,this, SERVICE)
        popupBinding.searchRV.adapter = serviceNewAdapter

        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                serviceNewAdapter?.filter?.filter(s.toString())


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

        servicePopupWindow = PopupWindow(
            popupBinding.root,binding.tvService.width, FrameLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        if (isAdded && binding.tvService.isAttachedToWindow) {
            servicePopupWindow?.showAsDropDown(binding.tvService)
        }

        servicePopupWindow?.elevation=25f

        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            servicePopupWindow?.dismiss()
            true
        }
    }

    override fun onDataSend(type: Int, file: Any) {


    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {
        when(type){
            1 -> {
                val selectedData = file as Origin
                when((extra as Int)) {
                    SOURCE -> {
                        sourcePopupWindow?.dismiss()
                        binding.sourceDropdownTv.setText(selectedData.name)
                        binding.destinationDropdownTv.setText("")
                        destinationId = ""
                        finalDestinationId = ""
                        tvSource.setText(selectedData.name)
                        sourceId = selectedData.id ?: ""
                        finalSourceId = sourceId
                        createDestinationList()
                        invalidateServiceList()
                    }

                    DESTINATION -> {
                        destinationPopupWindow?.dismiss()
                        binding.destinationDropdownTv.setText(selectedData.name)
                        destinationId = selectedData.id ?: ""
                        finalDestinationId = destinationId
                        invalidateServiceList()
                        if(sourceId=="0"||destinationId=="0"){
                            binding.selectServiceCL.gone()
                        }else{
                            binding.selectServiceCL.visible()
                        }
                    }

                    SERVICE -> {
                        servicePopupWindow?.dismiss()
                        val selectedServiceItem = selectedData

                        var boardingList = mutableListOf<BoardingPointDetail>()
                        var droppingList = mutableListOf<DropOffDetail>()

                        com.bitla.ts.utils.common.availableRoutesList.forEach {
                            if (it.id.toString() == selectedServiceItem.id) {
                                binding.tvService.setText(it.number)
                                service_ServiceNUmber = it.reservation_id.toString()
                                boardingList = it.boarding_point_details as MutableList
                                droppingList = it.drop_off_details as MutableList
                                serviceBusType = it.bus_type
                                serviceDepTime = it.dep_time
                                PreferenceUtils.putObject(it.is_apply_bp_dp_fare, IS_APPLY_BP_DP_FARE)

                                PreferenceUtils.putObject(it.is_apply_bp_dp_fare, SERVICE_IS_APPLY_BP_DP_FARE)

                                PreferenceUtils.setPreference("is_bima", it.is_bima)
                                PreferenceUtils.putObject(
                                    it, PREF_SELECTED_AVAILABLE_ROUTES
                                )
                                PreferenceUtils.putString((PREF_COACH_NUMBER), it.number)
                                PreferenceUtils.setPreference(
                                    PREF_RESERVATION_ID,
                                    it.reservation_id
                                )
                            }
                        }
                        PreferenceUtils.putBoarding(boardingList)
                        PreferenceUtils.putDropping(droppingList)
                    }
                }


            }
        }
    }

}
