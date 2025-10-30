package com.bitla.ts.presentation.view.activity

import SingleViewModel
import android.annotation.*
import android.app.*
import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.view.*
import android.view.inputmethod.*
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.account_info.request.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.block_seats.request.*
import com.bitla.ts.domain.pojo.booking_custom_request.*
import com.bitla.ts.domain.pojo.custom_applied_coupons.*
import com.bitla.ts.domain.pojo.fare_breakup.request.*
import com.bitla.ts.domain.pojo.fare_breakup.response.*
import com.bitla.ts.domain.pojo.getCouponDiscount.Response.*
import com.bitla.ts.domain.pojo.getPrefillPassenger.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.passenger_details_result.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.rutDiscountDetails.request.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.user_list.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.security.EncrypDecryp
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.bitla.tscalender.*
import com.google.android.material.bottomsheet.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*


class NewPassengerDetailsActivity : BaseActivity(), View.OnClickListener, OnItemClickListener,
    DialogButtonListener, DialogSingleButtonListener, OnItemPassData, OnItemCheckedListener,
    VarArgListener, AdapterView.OnItemSelectedListener, SlyCalendarDialog.Callback {

    companion object {
        val TAG: String = NewPassengerDetailsActivity::class.java.simpleName
        lateinit var binding: NewPassengerDetailsActivityBinding
    }

    private var serviceNumber: String = ""
    private var isOwnRoute: Boolean = false
    private var deletePassengerPosition: Int? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var additionalFare: Boolean? = null
    private var discountAmount: Boolean? = null
    private var isArrowActive = false
    private var passengerList: ArrayList<PassengerDetailsResult> = ArrayList()

    //    private var passengerContactDetailList: ContactDetail = ContactDetail()
    private var passengerContactDetailList: ArrayList<ContactDetail> = ArrayList()
    private var getPassengerNameList: ArrayList<String>? = arrayListOf()
    private var getPassengerAgeList: ArrayList<String>? = arrayListOf()
    private var getPassengerSexList: ArrayList<String>? = arrayListOf()
    private var getPassengerIdNumberList: ArrayList<String>? = arrayListOf()
    private var getPassengerIdTypeList: ArrayList<String>? = arrayListOf()
    private var getPassengerAdditionalFareList: ArrayList<String>? = arrayListOf()
    private var getPassengerAdditionalDiscountList: ArrayList<String>? = arrayListOf()
    private var getSeatNoList: ArrayList<String>? = arrayListOf()
    private var getExtraSeatFareList: ArrayList<String>? = arrayListOf()
    private var getMealCheckedList: ArrayList<String> = arrayListOf()

    private lateinit var spinnerItems: SpinnerItems
    private var idTypeList: MutableList<SpinnerItems> = mutableListOf()

    private var isValidationFlag: Boolean = false
    private var isDiccountFlag: Boolean = false
    private var isAdditionalFlag: Boolean = false

    private var privilegePassengerName: String? = ""
    private var privilegePassengerAge: String? = ""
    private var privilegePassengerSex: String? = ""
    private var privilegePassengerIDType: String? = ""
    private var privilegePassengerIDNumber: String? = ""
    private var privilegePassengerPrimaryPassengerMandatory: String? = ""
    private var privilegePassengerFirstName: String? = ""
    private var privilegePassengerLastName: String? = ""
    private var privilegePassengerAddress: String? = ""
    private var privilegePassengerRemarks: String? = ""

    private var privilegePassengerMobileNo: String? = ""
    private var privilegePassengerAlternateNo: String? = ""
    private var privilegePassengerEmail: String? = ""
    private var privilegePhoneValidationCount: Int? = null

    private var countryCode: String? = ""

    private var totalFare: Double = 0.0
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var busType: String? = null
    private var deptTime: String? = null

    var check: Boolean? = null

//    var selectedSeatDetails = java.util.ArrayList<SeatDetail>()
    var selectedExtraSeatDetails = ArrayList<SeatDetail>()
    private var noOfSeats: String? = "0"

    //    private var totalBalance: Int? = 0
    private var toolbarTitle: String = ""
    private var srcDest: String = ""

    //    fareBreakup
    private val isMatchPrepostponeAmount: String = "false"
    private val privilegeCardNo: String = "" //fixed
    private val useSmartMiles: String = "false" //fixed
    private val offerCoupon: String = "" //fixed
    private val promoCoupon: String = "" //fixed
    private var isBima: Boolean = false
    private val isRoundTrip: Boolean = false //fixed
    private val vipTicket: String = "1" //fixed
    private val isFreeBookingAllowed: String = "1" //fixed

    private var paymentType: String = "1" //(by default for cash)
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var arrTime: String? = null
    private var deptDate: String? = null
    private var arrDate: String? = null
    private var boardingPoint: String? = null
    private var droppingPoint: String? = null
    private var returnBoardingPoint: String = ""
    private var returnDroppingPoint: String = ""
    private var droppingId: Int? = null
    private var boardingId: Int? = null
    private var resId: Long? = null
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var appliedCouponList = mutableListOf<AppliedCoupon>()
    private var fareBreakUpHashNoDataFound = false
    private var isAgentLogin: Boolean = false
    private var amountCurrency: String = ""
    private var currencyFormat: String = ""

    private var bookingCustomRequest = BookingCustomRequest()
    private var agentType: String? = null

    private var getAllSeatList: ArrayList<String>? = arrayListOf()
    private val bookingOptionViewModel by viewModel<BookingOptionViewModel<Any?>>()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private val singleViewModel by viewModel<SingleViewModel<Any?>>()
    private val privilegeDetailsViewModel by viewModel<PrivilegeDetailsViewModel>()
    private val agentAccountInfoViewModel by viewModel<AgentAccountInfoViewModel<Any>>()


    private var countryList = ArrayList<Int>()
    lateinit var selectedDiscountCode: PerBookingCoupon
    private var selectedDiscountCodeList = ArrayList<PerBookingCoupon>()
    private var locale: String? = ""
    private var isFromChile: Boolean = false
    private var isPerSeat: Boolean = false
    private var isCodeSelected: Boolean = false
    private var perSeatCouponList: ArrayList<PerSeatCoupon> = arrayListOf()
    private var couponCodeList: ArrayList<String> = arrayListOf()
    private var seatNumberList: ArrayList<String> = arrayListOf()

    private var idType: Int? = null
    private var hideCoup: String = ""
    private var isExtraSeats: Boolean = false
    private var extraSeatFirstPosition: Int = -1
    private var isCallIntentCall = false


    private var rutSeatNumber: String? = null
    private var oldRutSeatNumber: ArrayList<String> = arrayListOf()
    private var rutNum: String? = null
    private var appliedSeat: String = ""
    private var rutApplied: Boolean = false
    private var validRut: Boolean = true
    private var allowAutoDiscount: Boolean = false
    private var allowRutDiscount: Boolean = false


    private var amountPaidOffline: Boolean = false
    private var selectedBranchId: Int = 0
    private var selectedUserId: Int = 0
    private var selectedOnlineAgentId: Int = 0
    private var selectedOfflineAgentId: Int = 0
    private var selectedBookingTypeId: Int? = null
    private var selectedBookingType: String? = null
    private val walkinId: Int = 0 //fixed
    private val onlineAgentId: Int = 1 //fixed
    private val offlineAgentId: Int = 2 //fixed
    private var branchId: String? = null
    private var branchIdFixed= 12
    private var bookingTypes: MutableList<SpinnerItems> = mutableListOf()
    private val cityDetailViewModel by viewModel<CityDetailViewModel<Any?>>()

    private var selectedCityIdNo: Int = 0
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private var bookingAgentList: MutableList<SpinnerItems> = mutableListOf()
    private var currency: String = ""
    var payableAmount = 0.0
    val fareBreakup = mutableListOf<FareBreakUpHash>()
    private var boardingStageTime = ""
    private var droppingStageTime = ""
    private var individualDiscountAmount: Int = 0
    private var flowBooking= false
    private var routeId= ""
    private var selectedGender = ""
    private var seatNumbers= ""
    private var seatNumberArrayList= arrayListOf<String>()
    private var seatDetailList= arrayListOf<SeatDetail>()
    private var genderDefaultHoQuotaBlocked: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceUtils.removeKey("AutoDiscountCouponCode")
        couponCodeList.clear()
        seatNumberList.clear()
        val emptyArray2 = GetPrefillResponse()
        PreferenceUtils.putPrefillData(emptyArray2)
        val emptyArray = arrayListOf<SeatWiseFare>()
        PreferenceUtils.putSelectedCoupon(emptyArray)

        getPref()
        setObserver()
        getSeatDetails()
        setToolbarTitle()


        if (flowBooking){
            binding.blockView.root.gone()
            agentAccountInfo()
            callPrivilegeDetailsApi()
            customBookingTypes()
            setAdapter()
        }else{
            blockTypeValue = "none"
            hiddenPropertiesAll()
            initVariables()
            binding.blockView.root.visible()
            blockRadioGroup()
            callBlockConfigApi()
            clickListener()
            setWeekdays()
        }
        lifecycleScope.launch {
            supervisorScope {
                launch {
                    privilegeDetailsViewModel.messageSharedFlow.collect{
                        if (it.isNotEmpty()){
                            showToast(it)
                        }
                    }
                }
                launch {
                    blockViewModel.messageSharedFlow.collect{
                        if (it.isNotEmpty()){
                            showToast(it)
                        }
                    }
                }
                launch {
                    cityDetailViewModel.messageSharedFlow.collect{
                        if (it.isNotEmpty()){
                            showToast(it)
                        }
                    }
                }
                launch {
                    agentAccountInfoViewModel.messageSharedFlow.collect{
                        if (it.isNotEmpty()){
                            showToast(it)
                        }
                    }
                }
                launch {
                    bookingOptionViewModel.messageSharedFlow.collect{
                        if (it.isNotEmpty()){
                            showToast(it)
                        }
                    }
                }
            }
        }
    }
    @SuppressLint("ResourceType")
    private fun setAdapter() {
        removePrefs()
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel.allowToSwitchSinglePageBooking != null && privilegeResponseModel.allowToSwitchSinglePageBooking) {
            selectedBookingType = getString(R.string.confirmBooking)
        } else {
            selectedBookingType = getString(R.string.walkin)
        }
        selectedBookingTypeId = walkinId
    }


    private fun customBookingTypes() {
        bookingTypes.clear()
        var itemWalkin = SpinnerItems(walkinId, getString(R.string.walkin))
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel.allowToSwitchSinglePageBooking != null && privilegeResponseModel.allowToSwitchSinglePageBooking)
            itemWalkin = SpinnerItems(walkinId, getString(R.string.confirmBooking))
        val itemOnlAgent = SpinnerItems(onlineAgentId, getString(R.string.online_agent))
        val itemOfflineAgent = SpinnerItems(offlineAgentId, getString(R.string.offline_agent))
        val itemBranch = SpinnerItems(branchIdFixed, getString(R.string.branch))
        bookingTypes.add(itemWalkin)

        /* checking below privilege
        * "is_allow_online_agent_booking": true,
          "is_allow_offline_agent_booking": true,
          "is_allow_branch_booking": true,
        *   */
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if ((!isOwnRoute && privilegeResponseModel.isAllowOnlineAgentBookingForOtherRoutes == true) || (isOwnRoute && privilegeResponseModel.isAllowOnlineAgentBooking))
                bookingTypes.add(itemOnlAgent)
            if (privilegeResponseModel.isAllowOfflineAgentBooking)
                bookingTypes.add(itemOfflineAgent)
            if (privilegeResponseModel.isAllowBranchBooking)
                bookingTypes.add(itemBranch)
        }
    }

    private fun callCityDetailsApi() {
        cityDetailViewModel.cityDetailAPI(
            loginModelPref.api_key,
            response_format,
            locale!!,
            city_Details_method_name
        )
    }


    private fun removePrefs() {
        amountPaidOffline = false
        selectedBranchId = 0
        selectedUserId = 0
        selectedOnlineAgentId = 0
        selectedOfflineAgentId = 0
        selectedCityIdNo = 0
    }


    private fun callBranchListApi() {
        if (isNetworkAvailable()) {
            blockViewModel.branchListApi(
                loginModelPref.api_key,
                locale!!,
                branch_list_method_name
            )

        } else
            noNetworkToast()
    }
    private fun callUserListApi() {
        Timber.d("userListRequest $userTypeId  $selectedBookingTypeId")

        if (isNetworkAvailable()) {

            val reqBody = selectedBookingTypeId?.let {
                com.bitla.ts.domain.pojo.user_list.request.ReqBody(
                    loginModelPref.api_key,
                    it,
                    locale = locale
                )
            }

            if (selectedCityIdNo != 0) {
                reqBody?.city_id = selectedCityIdNo.toString()
            }
                blockViewModel.userListApi(
                    apiKey = loginModelPref.api_key,
                    cityId = "",
                    userType = userTypeId.toString(),
                    branchId = branchId.toString(),
                    locale = locale!!,
                    apiType = user_list_method_name
                )
        } else
            noNetworkToast()
    }


    override fun isInternetOnCallApisAndInitUI() {
    }


    private fun getPref() {
        locale = PreferenceUtils.getlang()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()

        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }
        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            deptTime = result?.dep_time ?: getString(R.string.dash)
            busType = result?.bus_type ?: getString(R.string.empty)
            serviceNumber = result?.number ?: getString(R.string.empty)
        }
        if (intent.hasExtra(NEW_BOOK_BLOCK_CHECK)) {
            flowBooking = intent.getBooleanExtra(NEW_BOOK_BLOCK_CHECK, false)
        }
        if (intent.hasExtra(ROUTE_ID)) {
            routeId = intent.getStringExtra(ROUTE_ID)?: ""
        }

        bccId = PreferenceUtils.getBccId()
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()
        loginModelPref = PreferenceUtils.getLogin()


        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!

        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            busType = result?.bus_type ?: getString(R.string.empty)
            deptTime = result?.dep_time ?: getString(R.string.empty)
            arrTime = result?.arr_time ?: getString(R.string.empty)
            deptDate = result?.dep_date ?: getString(R.string.empty)
            arrDate = result?.arr_date ?: getString(R.string.empty)
            serviceNumber = result?.number ?: getString(R.string.empty)
        }




        if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
            val droppingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)!!
            droppingPoint = droppingStageDetail.name!!
            droppingId = droppingStageDetail.id
            arrDate=droppingStageDetail.travelDate
            arrTime=droppingStageDetail.time
        }




        if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
            val boardingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)!!
            boardingPoint = boardingStageDetail.name!!
            boardingId = boardingStageDetail.id
            deptDate=boardingStageDetail.travelDate
            deptTime=boardingStageDetail.time
        }


        if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
            droppingStageTime =
                PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)?.time.toString()
        }
        if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
            boardingStageTime =
                PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)?.time.toString()
        }

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
            currencyFormat = getCurrencyFormat(this, privilegeResponseModel.currencyFormat)

            genderDefaultHoQuotaBlocked = privilegeResponseModel?.tsPrivileges?.allowGenderDefaultHoQuotaBlockedSeat?:false

            if (privilegeResponseModel.isChileApp) {
                isFromChile = true
                if (privilegeResponseModel.availableAppModes?.allowAutoDiscount != null) {
                    allowAutoDiscount = privilegeResponseModel.availableAppModes?.allowAutoDiscount!!
                }
                if (allowAutoDiscount) {
                    if (privilegeResponseModel.isAutoDiscountRutEnable != null) {
                        allowRutDiscount = privilegeResponseModel.isAutoDiscountRutEnable!!
                    }
                }
            }


        }


        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel

            isAllowMultipleQuota = privilegeResponseModel.allowMultipleQuota
        }



        if (PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false) != null)
            isOwnRoute = PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false)!!

        bookingCustomRequest = retrieveBookingCustomRequest()
        agentType = bookingCustomRequest.selected_booking_id.toString()
        try {
            if (getCountryCodes().isNotEmpty())
                countryList = getCountryCodes()
        } catch (e: Exception) {
            Timber.d("countryCode - $e")
        }
    }

    private fun setToolbarTitle() {
        srcDest = "$source-$destination"
        binding.toolbarPassengerDetails.tvCurrentHeader.gone()

        //val subtitle = "$busType"
        val subtitle = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${travelDate.let { getDateDMYY(it) }} $deptTime | $busType"
        else
            "${getDateDMYY(travelDate)} $deptTime | $busType"
        binding.toolbarPassengerDetails.toolbarHeaderText.text = srcDest
        binding.toolbarPassengerDetails.toolbarSubtitle.text = subtitle

    }

    private fun getSeatDetails() {

        retrieveSelectedSeats().forEach {
            if (seatNumbers==""){
                seatNumbers+=it.number
            }    else{
                seatNumbers+=",${it.number}"
            }
            seatNumberArrayList.add(it.number)
            seatDetailList.add(it)
        }
        com.bitla.ts.utils.common.seatDetailList = retrieveSelectedSeats()
        selectedExtraSeatDetails = retrieveSelectedExtraSeats()

        first@ for (i in 0 until com.bitla.ts.utils.common.seatDetailList.size) {
            if (com.bitla.ts.utils.common.seatDetailList[i].isExtraSeat) {
                extraSeatFirstPosition = i
                return
            }
        }

        isExtraSeats = (selectedExtraSeatDetails.size > 0)

        noOfSeats = com.bitla.ts.utils.common.seatDetailList.size.toString()
        if (com.bitla.ts.utils.common.seatDetailList.any { it.isExtraSeat }) {
            additionalFare = false
            discountAmount = false
        }

        com.bitla.ts.utils.common.seatDetailList.forEach {
            if (it.editFare != null && it.editFare.toString().isNotEmpty()) {
                it.editFare?.toString()?.let { fare ->
                    if (fare.all { fareCheck -> fareCheck.isDigit() || fareCheck == '.' }) {
                        totalFare += fare.toDouble()
                    }
                }
            } else {
                if (it.baseFareFilter != null) {
                    it.baseFareFilter?.toString()?.let { fare ->
                        if (fare.isNotEmpty() && fare.all { fareCheck -> fareCheck.isDigit() || fareCheck == '.' }) {
                            totalFare += fare.toDouble()
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }

            }
        }
        val totalNetAmount =
            "${getString(R.string.netAmount)} : $amountCurrency$totalFare"
    }

    private fun callPrivilegeDetailsApi() {
        privilegeDetailsViewModel.getPrivilegeDetailsApi(
            loginModelPref.api_key,
            privilege_details_method_name,
            format_type,
            locale!!
        )
    }
    private fun getObserverCoupon() {
        try {
            bookingOptionViewModel.getCouponDetails.observe(this) {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            var perBookinfNames = arrayListOf<String>()

                            if (it.per_booking_coupons.isNotEmpty()) {
                                it.per_booking_coupons.forEach {
                                    if (!it.is_rut_discount) {
                                        selectedDiscountCodeList.add(it)
                                        perBookinfNames.add(it.coupon_name)

                                    }
                                }
                            }


                            if (it.per_seat_coupons.isNotEmpty()) {
                                it.per_seat_coupons.forEach {
                                    if (!it.is_rut_discount) {
                                        perSeatCouponList.add(it)
                                    }
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
                            it.result?.message?.let { it1 -> toast(it1) }
                        }
                    }

                } else {
                    toast(getString(R.string.server_error))
                }
            }

        } catch (e: Exception) {
            toast(getString(R.string.server_error))
        }
    }

    private fun setPrivilegesObserver() {
        privilegeDetailsViewModel.privilegeResponseModel.observe(this) {
            if (it != null) {

                when (it.code) {
                    200 -> {
                        PreferenceUtils.setPreference(
                            "otp_validation_time",
                            it.configuredLoginValidityTime
                        )
                        stopShimmerEffect()

                        it.apply {
                            amountCurrency = currency.ifEmpty {
                                currency
                            }
                            additionalFare = isAdditionalFare
                            discountAmount = isDiscountOnTotalAmount
                            privilegeResponseModel = it
                        }
                        currency = privilegeResponseModel.currency
                        currencyFormat = getCurrencyFormat(this, privilegeResponseModel.currencyFormat)
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
                        it.result.message?.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    override fun initUI() {
        binding = NewPassengerDetailsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
    }

    private fun setObserver() {

        bookingOptionViewModel.fareBreakup.observe(this) { it
            Timber.d("fareBreakupByAli $it")
            if (it != null) {
                if (it.code == 200) {
                    fareBreakup.clear()
                    fareBreakUpHashNoDataFound = true
                    for (i in 0..it.fare_break_up_hash.size.minus(1)) {
                        if (it.fare_break_up_hash[i].value.toString().toDouble() > 0.0)
                            fareBreakup.add(it.fare_break_up_hash[i])
                    }
                    payableAmount = it.payble_amount.toString().toDouble()
                } else {
                    toast(it.message)
                }
            } else {
                toast(getString(R.string.server_error))
                fareBreakUpHashNoDataFound = false
            }
        }

        cityDetailViewModel.cityDetailResponse.observe(this) {
            if (it != null) {
                if (it.code == 200) {
                    var cityList =
                        mutableListOf<com.bitla.ts.domain.pojo.city_details.response.Result>()
                    cityList = it.result
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

        blockViewModel.blockDetails.observe(this) { it ->
            if (it != null) {
                if (!it.user_types.isNullOrEmpty()) {
                    userTypeList.clear()
                    it.user_types.forEach {

                        if (it.label == "USER") {
                            userTypeId = it.id
                        }
                        val spinnerItems = SpinnerItems(it.id, it.label)
                        userTypeList.add(spinnerItems)
                        userTypeSpinner()
                    }
                }

                if (!it.blocking_types.isNullOrEmpty()) {

                    if (blockTypeList != null && blockTypeList.isNotEmpty()) {
                        blockTypeList.clear()
                    }

                    it.blocking_types.forEach {
                        val spinnerBlockItems = SpinnerItems(0, it)
                        if (it.equals(getString(R.string.none),true))
                            blockTypeList.add(0,spinnerBlockItems)
                        else
                            blockTypeList.add(spinnerBlockItems)
                    }
                    blockTypeSpinner()
                }

            } else {
                toast(getString(R.string.server_error))
            }
        }

        blockViewModel.validationData.observe(this) {
            if (it == getString(R.string.empty)) {
                binding.blockView.btnBlockSeats.setBackgroundResource(R.drawable.button_selected_bg)
                DialogUtils.blockSeatsDialog(
                    true,
                    this,
                    getString(R.string.confirmSeats),
                    updateBlockContentMessage(),
                    "$source-$destination",
                    binding.toolbarPassengerDetails.toolbarSubtitle.text.toString(),
                    seatNumberArrayList.size.toString(),
                    seatNumbers,
                    getString(R.string.goBack),
                    getString(R.string.block),
                    this
                )
            } else {
                toast(it)
            }
        }

        blockViewModel.changeButtonBackground.observe(this) {
            if (it) {
                binding.blockView.btnBlockSeats.apply {
                    setBackgroundResource(R.drawable.button_selected_bg)
                    isEnabled = true
                }
            } else {
                if (!isAllowMultipleQuota) {
                    binding.blockView.btnBlockSeats.setBackgroundResource(R.drawable.button_selected_bg)
                    binding.blockView.btnBlockSeats.isEnabled = true
                } else {
                    binding.blockView.btnBlockSeats.apply {
                        setBackgroundResource(R.drawable.button_default_bg)
                        isEnabled = false
                    }
                }
            }
        }

        blockViewModel.blockSeats.observe(this) {
                if (it != null) {
                    PreferenceUtils.setPreference(PREF_UPDATE_COACH,true)
                    if (it.code == 200) {
                        DialogUtils.successfulBlockSeatDialog(
                            this,
                            getString(R.string.seat_block_successful)
                        )

                        lifecycleScope.launch {
                            delay(3000)
                            val intent = Intent(this@NewPassengerDetailsActivity, NewCoachActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                    } else {
                        toast(it.message)
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
        }

        blockViewModel.branchList.observe(this) { it ->
//            binding.coachProgressBar.gone()
            if (it != null) {

                if (flowBooking) {
                    if (it.branchlists.isNotEmpty()) {
                        branchList.clear()
                        it.branchlists.forEach {
                            val spinnerItems = SpinnerItems(it.id, it.label)
                            branchList.add(spinnerItems)
                        }
                    }
                    saveBranchList(branchList)

                    val intent = Intent(this, SearchActivity::class.java)
                    intent.putExtra(
                        getString(R.string.CITY_SELECTION_TYPE),
                        getString(R.string.selectBranch)
                    )
                    startActivityForResult(intent, RESULT_CODE_SEARCH_BRANCH)
                } else {
                    branchList.clear()

                    if (it.branchlists.isNotEmpty()) {
                        it.branchlists.forEach {
                            val spinnerItems = SpinnerItems(it.id, it.label)
                            branchList.add(spinnerItems)
                        }
                    }
                    branchListSpinner()
                    if (!isAllowMultipleQuota && branchList.isNotEmpty()) {
                        branchList.sortBy { it.value.lowercase() }
                        branchId = branchList[0].id.toString()
                        binding.blockView.tvBranchTypeSpinner.setText(branchList[0].value)
                        if (userTypeValue.equals("USER", true) || userTypeId == 12)
                            callUserListApi()
                        setButtonObservable()
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }

        }

        getObserverCoupon()

        setPrivilegesObserver()

        userListObserver()

    }
    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }
    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onLeftButtonClick() {

    }

    override fun onRightButtonClick() {
        if (flowBooking){
        }else{
            isblocked = 0
            callBlockSeatApi()
        }
//        deletePassengerPosition?.let { passengerDetailsAdapter.notifyItemRemoved(it) }
//        deletePassengerPosition?.let { passengerDetailsAdapter.notifyItemRangeChanged(it, passengerList.size) }
    }

    private fun callBlockSeatApi() {

        if (binding.blockView.sBlockType.text.toString().equals("none")) {

            timeMM = ""
            timeHH = ""
            toDate = ""
            fromDate = ""
        } else if (binding.blockView.sBlockType.text.equals("permanent")) {
            toDate = ""
            fromDate = ""
        } else if (binding.blockView.sBlockType.text.equals("custom") || binding.blockView.sBlockType.text.equals("temporary")) {
            timeHH = ""
            timeHH = ""

        }
        var isBima: Boolean? = null
        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }
        val blockSeatRequest = BlockSeatRequest()
        blockSeatRequest.bccId = bccId.toString()
        blockSeatRequest.methodName = block_seat_method_name
        blockSeatRequest.format = format_type

        val reqBody1 = ReqBody__1()
        reqBody1.agentType = userTypeId.toString()
        reqBody1.apiKey = loginModelPref.api_key
        reqBody1.isFromMiddleTier = true
        reqBody1.locale = locale
        reqBody1.mainOpId = routeId
        reqBody1.gender = selectedGender

        reqBody1.operatorApiKey = operator_api_key
        reqBody1.reservedSeatCount = seatNumberArrayList.size
        reqBody1.is_bima = isBima
        //reqBody1.remarks = binding.blockView.remrksField.text.toString()
        if (binding.blockView.sBlockType.text.toString().equals("none", true))
            reqBody1.selectionType = ""
        else if (binding.blockView.sBlockType.text.toString().equals("custom", true))
            reqBody1.selectionType = "custom"
        else if (binding.blockView.sBlockType.text.toString().equals("permanent", true))
            reqBody1.selectionType = "apply_all"
        else if (binding.blockView.sBlockType.text.toString().equals("temporary", true))
            reqBody1.selectionType = "temporary"

        reqBody1.resId =  resId?.toString()
        reqBody1.agent_types = null
        val ticket = Ticket()
        ticket.selectedSeats = seatNumbers.trim()
        ticket.selectedSeats = ticket.selectedSeats?.replace(" ", "")
        reqBody1.ticket = ticket
        //toast(ticket.selectedSeats.toString())


        val searchBusParams = SearchbusParams()

        searchBusParams.from = sourceId.substringAfter(":")
        searchBusParams.to = destinationId.substringAfter(":")


        reqBody1.searchbusParams = searchBusParams

        val recordModel = Record()
        recordModel.weekly_schedule = getSelectedWeekdays()
        recordModel.from_date = fromDate
        recordModel.to_date = toDate
        recordModel.quota_release_hours = timeHH
        recordModel.quota_release_mins = timeMM
        recordModel.remarks = binding.blockView.remrksField.text.toString()

        if (binding.blockView.sBlockType.text.toString().equals("none")) {
            recordModel.from_date = ""
            recordModel.to_date = ""
            recordModel.quota_release_hours = ""
            recordModel.quota_release_mins = ""
        } else if (binding.blockView.sBlockType.text.toString().equals("custom")) {
            recordModel.quota_release_hours = ""
            recordModel.quota_release_mins = ""
        } else if (binding.blockView.sBlockType.text.toString().equals("permanent")) {
            recordModel.from_date = ""
            recordModel.to_date = ""
        } else if (binding.blockView.sBlockType.text.toString().equals("temporary")) {
            recordModel.quota_release_hours = ""
            recordModel.quota_release_mins = ""
        }



        reqBody1.record = recordModel
        // based on user type
        if (userTypeId == 12) // USER
        {
            reqBody1.searchBusOnBehalfBranch = branchId ?: getString(R.string.empty)
            reqBody1.searchBusOnBehalfUser = userId ?: getString(R.string.empty)
        } else if (userTypeId == 1) // Onl-Agt
        {
            reqBody1.searchBusOnBehalfOnlineAgent = agentId ?: getString(R.string.empty)
        }

        if (isAllowMultipleQuota && selectedUserTypeList.size >= 2) {
            var a: String = ""
            reqBody1.agentType = null
            selectedUserTypeList.forEach {
                if (it.value.equals("Api-Agt", ignoreCase = true)) {
                    a += "9" + ","
                    return@forEach
                }
                if (it.value.equals("Default HO", ignoreCase = true)) {
                    a += "2" + ","
                    return@forEach
                }
                a += it.id.toString() + ","

            }
            reqBody1.agent_types = a.substring(0, a.length - 1)
        } else if (isAllowMultipleQuota && selectedUserTypeList.size == 1) {
            if (userTypeId == 1) {
                if (reqBody1.searchBusOnBehalfOnlineAgent?.isEmpty() == true) {
                    reqBody1.agent_types = reqBody1.agentType
                    reqBody1.agentType = null
                } else {
                    reqBody1.agentType = selectedUserTypeList[0].id.toString()
                }
            } else if (userTypeId == 12) {
                if (reqBody1.searchBusOnBehalfBranch?.isEmpty() == true) {
                    reqBody1.agent_types = selectedUserTypeList[0].id.toString()
                    reqBody1.agentType = null
                    reqBody1.searchBusOnBehalfBranch = null
                    if (reqBody1.searchBusOnBehalfUser?.isEmpty() == true) {
                        reqBody1.searchBusOnBehalfUser = null
                    } else {
                        reqBody1.searchBusOnBehalfBranch = branchId
                        reqBody1.searchBusOnBehalfUser = userId
                    }
                } else {
                    reqBody1.agentType = selectedUserTypeList[0].id.toString()
                }
            } else {
                if (selectedUserTypeList[0].value.equals("Default HO", ignoreCase = true)) {
                    reqBody1.agent_types = null
                    reqBody1.agentType = "2"
                } else {
                    reqBody1.agent_types = null
                    reqBody1.agentType = selectedUserTypeList[0].id.toString()
                }
            }
        } else {

            if (binding.blockView.sUserType.text.toString() == "Default HO") {
                reqBody1.agent_types = null
                reqBody1.agentType = "2"
            }
        }

        blockSeatRequest.reqBody = reqBody1

        blockViewModel.blockSeatsApi(
            reqBody1,
            block_seat_method_name
        )

    }

    /*
    * this method to used for start Shimmer Effect
    * */
    private fun startShimmerEffect() {
        binding.apply {
            shimmerDashboard.visible()
            toolbarPassengerDetails.root.gone()
            shimmerDashboard.startShimmer()
        }

    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.apply {
            shimmerDashboard.gone()
            toolbarPassengerDetails.root.visible()
            if (shimmerDashboard.isShimmerStarted) {
                shimmerDashboard.stopShimmer()
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun agentAccountInfo() {

        Timber.d("agentinfosome: ${loginModelPref.api_key}")
        val agentRequest = AgentAccountInfoRequest(
            bccId.toString(),
            format_type,
            agent_account_info,
            com.bitla.ts.domain.pojo.account_info.request.ReqBody(
                loginModelPref.api_key,
                locale = locale
            )
        )
        agentAccountInfoViewModel.agentAccountInfoAPI(
            agentRequest,"","",
            agent_account_info
        )
    }


    private fun showKeyboard(editText: EditText) {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onItemData(view: View, str1: String, str2: String) {

    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
        if (!oldRutSeatNumber.contains(str1)) {
            oldRutSeatNumber.add(str1)
        }

        idType = str3.toInt()
        rutNum = str2
        rutSeatNumber = str1
        if (appliedSeat == str1) {
            appliedSeat = ""
            rutApplied = false
        }
        getPrefillPassengerDiscountAPi(str2, str3.toInt(), str1)
//            getRutdetails(str1, str2)
    }





    private fun getPrefillPassengerDiscountAPi(rutNumber: String, idType: Int, seatNUm: String) {
//        Timber.d("hideCouponCheck:: $hideCoupon")
//        hideCoup= hideCoupon


        bookingOptionViewModel.getPrefillPassenger(
            GetPrefillPassengerRequest(
                rutNumber,
                idType,
                seatNUm,
                loginModelPref.api_key,
                locale,
                true
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (isCallIntentCall) {
            stopShimmerEffect()
        }
    }

    override fun onItemChecked(isChecked: Boolean, view: View, position: Int) {
//       startShimmerEffect()
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialog_custom_progressbar)
        val dialog: AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        lifecycleScope.launch(Dispatchers.IO) {
            delay(500)
            withContext(Dispatchers.Main) {
                if (view.isAttachedToWindow) {
                    dialog.dismiss()
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data?.getStringExtra(getString(R.string.SELECTED_SEARCHED_TYPE)) != null) {
                val selectedType: String = data.getStringExtra(getString(R.string.SELECTED_SEARCHED_TYPE)).toString()
                val selectedName: String = data.getStringExtra(getString(R.string.SELECTED_SEARCHED_NAME)).toString()
                val selectedId: String = data.getStringExtra(getString(R.string.SELECTED_SEARCHED_ID)).toString()

                when (selectedType) {
                    getString(R.string.selectAgent) -> {
                        agentId = selectedId
                        // binding.tvAgentHint.visible()
                        binding.blockView.tvAgentTypeSpinner.setText(selectedName)
                        //binding.tvAgentTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                        setButtonObservable()
                    }
                    getString(R.string.selectBranch) -> {
                        // binding.tvBranchHint.visible()
                        branchId = selectedId
                        binding.blockView.tvBranchTypeSpinner.setText(selectedName)
                        //binding.tvUserSpinner.setText(getString(R.string.selectUser))
                        // binding.tvBranchTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                        if (userTypeValue.equals("USER", true) || userTypeId == 12)
                            callUserListApi()
                        else
                            callUserListApi()
                        setButtonObservable()
                    }
                    getString(R.string.selectUser) -> {
                        userId = selectedId
                        //binding.tvUserHint.visible()
                        binding.blockView.tvUserSpinner.setText(selectedName)
                        //binding.tvUserSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                        setButtonObservable()
                    }
                }
            }
        }
    }


    private fun callAgentListApi() {
        if (isNetworkAvailable()) {
            val reqBody = selectedBookingTypeId?.let {
                com.bitla.ts.domain.pojo.user_list.request.ReqBody(
                    loginModelPref.api_key,
                    it,
                    locale = locale
                )
            }

            if (selectedCityIdNo != 0) {
                reqBody?.city_id = selectedCityIdNo.toString()
            }
            val userListRequest =
                reqBody?.let {
                    UserListRequest(
                        bccId.toString(),
                        format_type,
                        user_list_method_name,
                        it
                    )
                }

            userListRequest?.let {


                blockViewModel.userListApi(
                    apiKey = loginModelPref.api_key,
                    cityId = selectedCityIdNo.toString(),
                    userType = selectedBookingTypeId.toString(),
                    branchId = selectedBranchId.toString(),
                    locale = locale!!,
                    apiType = user_list_method_name
                )
            }

        } else
            noNetworkToast()
    }

    override fun onButtonClick(vararg args: Any) {

    }

//    BLOCK FLOW FUNCTIONS

    private var timeHH: String = ""
    private var timeMM: String = ""
    private var userId: String? = null
    private var agentId: String? = null
    private var userTypeId: Int = 0
    private var userTypeValue: String = ""
    private var blockTypeId: Int? = null
    private var blockTypeValue: String? = null
    private var fromDate: String? = null
    private var toDate: String? = null
    private var userTypeList: MutableList<SpinnerItems> = mutableListOf()
    private var blockTypeList: MutableList<SpinnerItems> = mutableListOf()
    private var userList: MutableList<SpinnerItems> = mutableListOf()
    private var branchList: MutableList<SpinnerItems> = mutableListOf()
    private var selectedUserTypeList: MutableList<SpinnerItems> = mutableListOf()

    private var weekdays: ArrayList<Weekdays> = arrayListOf()
    private var dateType: String? = null
    private var selectedSeats: String? = null
    private var isClick: Int = 0
    private var isAllowMultipleQuota: Boolean = true
    private var isblocked = 0
    lateinit var bindingSheet: SheetReleaseTicketsBinding
    lateinit var bottomSheetDialoge: BottomSheetDialog


    private fun initVariables() {

        // userTypeId = 0
        selectedUserTypeList.clear()

        branchId = null
        userId = null
        agentId = null
        blockTypeValue = "none"
        fromDate = null
        toDate = null

       // com.bitla.ts.utils.common.seatDetailList.clear()
        binding.blockView.sUserType.setSelection(0)

        //As block type none is selected by default therefore following views are no long required to be displayed
        binding.blockView.blockDurationTextView.visibility = View.GONE
        binding.blockView.blockDateDurationLinearLayout.visibility = View.GONE
        binding.blockView.tvFromDate.visibility = View.GONE
        binding.blockView.tvToDate.visibility = View.GONE
        binding.blockView.releaseTimeTextView.visibility = View.GONE
        binding.blockView.layoutHour.visibility = View.GONE
        binding.blockView.editTextHour.visibility = View.GONE
        binding.blockView.layoutMinute.visibility = View.GONE
        binding.blockView.editTextMinute.visibility = View.GONE


        binding.blockView.tvFromDate.text = getString(R.string.fromDate)
        binding.blockView.tvToDate.text = getString(R.string.toDate)


        invalidateCount()
        if (isAllowMultipleQuota)
            binding.blockView.sUserType.setText("")
        /*weekdays.forEach {
            it.isSelected = true
        }
        weekdaysAdapter.notifyDataSetChanged()*/
    }




    private fun userListObserver(){
        blockViewModel.userList.observe(this) { it ->
            userList.clear()
            if (it != null && it.code==200) {
                if (flowBooking){
                    if (it.active_users != null && it.active_users.isNotEmpty()) {
                        if (selectedBranchId == 0) {
                            bookingAgentList.clear()
                            it.active_users.forEach {
                                val spinnerItems = SpinnerItems(it.id, it.label)
                                bookingAgentList.add(spinnerItems)
                            }
                            saveAgentList(bookingAgentList)
                        } else {
                            userList.clear()
                            it.active_users.forEach {
                                val spinnerItems = SpinnerItems(it.id, it.label)
                                userList.add(spinnerItems)
                            }
                            saveUserList(userList)
                            val intent = Intent(this, SearchActivity::class.java)
                            intent.putExtra(
                                getString(R.string.CITY_SELECTION_TYPE),
                                getString(R.string.selectUser)
                            )
                            startActivityForResult(intent, RESULT_CODE_SEARCH_USER)
                        }
                    } else {
                        bookingAgentList.clear()
                        saveAgentList(bookingAgentList)
                        if (it.message != null)
                            toast(it.message)
                    }
                }
                else{
                    if (it.active_users != null && it.active_users.isNotEmpty()) {
                        it.active_users.forEach {
                            val spinnerItems = SpinnerItems(it.id, it.label)
                            userList.add(spinnerItems)
                        }
                    }
                }
                if (userTypeId == 1 || userTypeValue.equals("Onl-Agt", true)) {
                    stageUserSpinner()
                    if (!isAllowMultipleQuota && userList.isNotEmpty()) {
                        userList.sortBy { it.value.lowercase() }
                        agentId = userList[0].id.toString()
                        binding.blockView.tvAgentTypeSpinner.setText(userList[0].value)
                        setButtonObservable()
                    }
                } else {
                    userListSpinner()
                    if (!isAllowMultipleQuota && userList.isNotEmpty()) {
                        userList.sortBy { it.value.lowercase() }
                        userId = userList[0].id.toString()
                        binding.blockView.tvUserSpinner.setText(userList[0].value)
                        setButtonObservable()
                    }
                }
            }else {
                toast(getString(R.string.server_error))
            }



        }
    }
    private fun callBlockConfigApi() {
        if (isNetworkAvailable()) {
            blockViewModel.blockConfigurationApi(
                loginModelPref.api_key,
                locale!!,
                block_config_method_name
            )


        } else
            noNetworkToast()
    }
    private fun blockRadioGroup() {
        val radioId = binding.blockView.rgBlockAll.checkedRadioButtonId
        val selectedBlockRadio = findViewById<RadioButton>(radioId)
    }
    private fun userTypeSpinner() {
        binding.blockView.sUserType.onItemSelectedListener = this

        if (isAllowMultipleQuota) {

            binding.blockView.sUserType.setAdapter(
                SelectUserTypeArrayAdapter(this,
                    R.layout.spinner_dropdown_item_witch_checkbox,
                    R.id.tvItem,
                    userTypeList,
                    selectedUserTypeList,
                    isAllowMultipleQuota,
                    object : SelectUserTypeArrayAdapter.ItemClickListener {
                        override fun onSelected(position: Int, item: SpinnerItems) {
                            if (selectedUserTypeList.contains(item).not())
                                selectedUserTypeList.add(item)
                            binding.blockView.sUserType.setText(item.value)

                            userTypeTag(position)
                            invalidateCount()
                            setButtonObservable()
                        }

                        override fun onDeselect(position: Int, item: SpinnerItems) {
                            if (selectedUserTypeList.contains(item))
                                selectedUserTypeList.remove(item)
                            binding.blockView.sUserType.setText(
                                selectedUserTypeList.firstOrNull().toString().replace("null", "")
                            )
                            if (selectedUserTypeList.size == 0) {
                                binding.blockView.sUserType.isFocusable = false
                                userTypeTag(position)
                            }
                            if (selectedUserTypeList.size == 1) {
                                val selectedPosition = userTypeList.indexOfFirst {
                                    it.value == selectedUserTypeList[0].value
                                }
                                binding.blockView.sUserType.isFocusable = true
                                userTypeTag(selectedPosition)

                            }
                            invalidateCount()
                            setButtonObservable()
                        }
                    })
            )
        }
        else {
            if (userTypeList.isNotEmpty()) {
                binding.blockView.sUserType.setText(userTypeList[0].value)
                selectedUserTypeList.clear()
                selectedUserTypeList.add(userTypeList[0])
                userTypeTag(0)
                setButtonObservable()
            }
            binding.blockView.sUserType.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    userTypeList
                )
            )
            binding.blockView.sUserType.onItemClickListener =
                AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    selectedUserTypeList.clear()
                    selectedUserTypeList.add(userTypeList[position])
                    userTypeTag(position)
                    setButtonObservable()
                }
        }
        binding.blockView.sBlockType.setOnItemClickListener { adapterView, view, position, l ->
            blockTypeTag(position)
            setButtonObservable()
        }

    }
    private fun getSelectedWeekdays(): String {
        var days: String = ""
        weekdays.forEach {
            if (it.isSelected == true) {
                days = days + "1"
            } else
                days = days + "0"
        }
        return days
    }
    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id){
            R.id.img_back -> {
                onBackPressed()
            }

            R.id.tvFromDate -> {
                dateType = getString(R.string.fromDate)
                if (binding.blockView.tvToDate.text != getString(R.string.toDate)) {
                    binding.blockView.tvToHint.gone()
                    binding.blockView.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)
                    binding.blockView.tvToDate.text = getString(R.string.toDate)
                    val scale = resources.displayMetrics.density
                    val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                    binding.blockView.tvToDate.setPadding(paddingtLeftRightinDp, 0, paddingtLeftRightinDp, 0)
                }

                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show(supportFragmentManager, TAG)
                fromDate = binding.blockView.tvFromDate.text.toString()
                setButtonObservable()


            }

            R.id.tvToDate -> {
                dateType = getString(R.string.toDate)
                val fromDate: String = binding.blockView.tvFromDate.text.toString()
                val toDate1: String = binding.blockView.tvToDate.text.toString()

                if (fromDate == getString(R.string.fromDate)) {
                    toast("Please select from date")
                } else {

                    if (fromDate != getString(R.string.fromDate) && toDate1 != getString(R.string.toDate)) // both the dates already selected
                    {
                        SlyCalendarDialog()
                            .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setEndDate(stringToDate(toDate1, DATE_FORMAT_D_M_Y))
                            .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(supportFragmentManager, TAG)
                    } else if (fromDate != getString(R.string.fromDate)) // only from date selected
                    {
                        SlyCalendarDialog()
                            .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(supportFragmentManager, TAG)
                    } else {
                        SlyCalendarDialog()
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(supportFragmentManager, TAG)
                    }
                }
                toDate = binding.blockView.tvToDate.text.toString()

            }

            R.id.editText_hour -> {
                timeHH = binding.blockView.editTextHour.text.toString()
            }

            R.id.editText_minute -> {
                timeMM = binding.blockView.editTextMinute.text.toString()
            }

            R.id.tvAgentTypeSpinner -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(
                    getString(R.string.CITY_SELECTION_TYPE),
                    getString(R.string.selectAgent)
                )
                startActivityForResult(intent, RESULT_CODE_SEARCH_AGENT)
            }

            R.id.tvBranchTypeSpinner -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(
                    getString(R.string.CITY_SELECTION_TYPE),
                    getString(R.string.selectBranch)
                )
                startActivityForResult(intent, RESULT_CODE_SEARCH_BRANCH)
            }

            R.id.tvUserSpinner -> {
                if (branchId == null) {
                    toast("Please select branch")
                } else {
                    val intent = Intent(this, SearchActivity::class.java)
                    intent.putExtra(
                        getString(R.string.CITY_SELECTION_TYPE),
                        getString(R.string.selectUser)
                    )
                    startActivityForResult(intent, RESULT_CODE_SEARCH_USER)
                }
            }

            R.id.layout_sunday -> {
                var txtview = findViewById<TextView>(R.id.tvSunday)
                setDaysBackground(0, v, txtview)
            }

            R.id.layout_monday -> {
                var txtview = findViewById<TextView>(R.id.tvMonday)
                setDaysBackground(1, v, txtview)
            }

            R.id.layout_tuesday -> {
                var txtview = findViewById<TextView>(R.id.tvTuesday)
                setDaysBackground(2, v, txtview)
            }

            R.id.layout_wednesday -> {
                var txtview = findViewById<TextView>(R.id.tvWednesday)
                setDaysBackground(3, v, txtview)
            }

            R.id.layout_thursday -> {
                var txtview = findViewById<TextView>(R.id.tvThursday)
                setDaysBackground(4, v, txtview)
            }

            R.id.layout_friday -> {
                var txtview = findViewById<TextView>(R.id.tvFriday)
                setDaysBackground(5, v, txtview)
            }

            R.id.layout_saturday -> {
                var txtview = findViewById<TextView>(R.id.tvSaturday)
                setDaysBackground(6, v, txtview)
            }

            R.id.btn_block_seats -> {

                timeHH = binding.blockView.editTextHour.text.toString()
                timeMM = binding.blockView.editTextMinute.text.toString()
                blockViewModel.validation(
                    selectedSeats,
                    selectedUserTypeList,
                    userTypeId,
                    branchId.toString(),
                    userId,
                    agentId,
                    blockTypeValue,
                    fromDate,
                    toDate,
                    timeHH,
                    timeMM,
                    isAllowMultipleQuota
                )
            }

            R.id.sUserType -> {
                if (isClick == 0) {
                    binding.blockView.rvSelectMultipleUserType.visible()
                    isClick++
                } else {
                    binding.blockView.rvSelectMultipleUserType.gone()
                    isClick = 0
                }
            }

            R.id.layout_service_details -> {
                //val busDetails = "$travelDate $source - $destination $busType "

                val busDetails = "$serviceNumber $travelDate $source - $destination $busType "
                val intent = Intent(this, ServiceDetailsActivity::class.java)
                intent.putExtra(getString(R.string.origin), source)
                intent.putExtra(getString(R.string.destination), destination)
                intent.putExtra(getString(R.string.bus_type), busDetails)

                PreferenceUtils.removeKey(getString(R.string.scannedUserName))
                PreferenceUtils.removeKey(getString(R.string.scannedUserId))
                PreferenceUtils.removeKey("selectedScanType")
                PreferenceUtils.removeKey(getString(R.string.scan_coach))
                PreferenceUtils.removeKey(getString(R.string.scan_driver_1))
                PreferenceUtils.removeKey(getString(R.string.scan_driver_2))
                PreferenceUtils.removeKey(getString(R.string.scan_cleaner))
                PreferenceUtils.removeKey(getString(R.string.scan_contractor))
                startActivity(intent)
            }
        }
    }

    fun setWeekdays() {
        weekdays.add(Weekdays("Sun", true))
        weekdays.add(Weekdays("Mon", true))
        weekdays.add(Weekdays("Tue", true))
        weekdays.add(Weekdays("Wed", true))
        weekdays.add(Weekdays("Thu", true))
        weekdays.add(Weekdays("Fri", true))
        weekdays.add(Weekdays("Sat", true))
        findViewById<TextView>(R.id.tvSunday).text = weekdays[0].day
        findViewById<TextView>(R.id.tvMonday).text = weekdays[1].day
        findViewById<TextView>(R.id.tvTuesday).text = weekdays[2].day
        findViewById<TextView>(R.id.tvWednesday).text = weekdays[3].day
        findViewById<TextView>(R.id.tvThursday).text = weekdays[4].day
        findViewById<TextView>(R.id.tvFriday).text = weekdays[5].day
        findViewById<TextView>(R.id.tvSaturday).text = weekdays[6].day
    }
    private fun setDaysBackground(
        index: Int,
        view: View,
        textView: TextView
    ) {
        if (!weekdays[index].isSelected) {
            // Change this color for selection
            view.setBackgroundColor(Color.parseColor("#00adb5"))
            textView.setTextColor(Color.parseColor("#ffffff"))
            weekdays[index].isSelected = true
        } else {
            view.setBackgroundColor(Color.parseColor("#ffffff"))
            textView.setTextColor(Color.parseColor("#9b9b9b"))
            weekdays[index].isSelected = false

        }
    }
    private fun clickListener() {
        binding.toolbarPassengerDetails.imgBack.setOnClickListener(this)
        binding.blockView.tvFromDate.setOnClickListener(this)
        binding.blockView.tvToDate.setOnClickListener(this)
        binding.blockView.editTextHour.setOnClickListener(this)
        binding.blockView.editTextMinute.setOnClickListener(this)
        binding.blockView.tvAgentTypeSpinner.setOnClickListener(this)
        binding.blockView.tvBranchTypeSpinner.setOnClickListener(this)
        binding.blockView.tvUserSpinner.setOnClickListener(this)

        binding.blockView.btnBlockSeats.setOnClickListener(this)
        binding.blockView.layoutWeekdaysLinear.layoutSunday.setOnClickListener(this)
        binding.blockView.layoutWeekdaysLinear.layoutMonday.setOnClickListener(this)
        binding.blockView.layoutWeekdaysLinear.layoutTuesday.setOnClickListener(this)
        binding.blockView.layoutWeekdaysLinear.layoutWednesday.setOnClickListener(this)
        binding.blockView.layoutWeekdaysLinear.layoutThursday.setOnClickListener(this)
        binding.blockView.layoutWeekdaysLinear.layoutFriday.setOnClickListener(this)
        binding.blockView.layoutWeekdaysLinear.layoutSaturday.setOnClickListener(this)

//        binding.sUserType.setOnClickListener(this)
    }

    private fun invalidateCount() {
        if (selectedUserTypeList.size > 1) {
            binding.blockView.tvMoreUserType.apply {
                visibility = View.VISIBLE
                text = "+ ${selectedUserTypeList.size - 1} more"
            }
        } else {
            binding.blockView.tvMoreUserType.visibility = View.GONE
        }
    }

    private fun updateBlockContentMessage(): String {
        val blockContent = getString(R.string.blockContentUserTypes)
        if (!isAllowMultipleQuota) {
            return "$blockContent ${binding.blockView.sUserType.text}"
        } else {
            var temp = "and "
            var prefix = ""
            if (selectedUserTypeList.size > 1) {
                temp += selectedUserTypeList[selectedUserTypeList.size - 1].value
                for (i in 0..selectedUserTypeList.size - 2) {
                    prefix += selectedUserTypeList[i].value + ", "
                }
                prefix = prefix.substring(0, prefix.length - 2)
                temp = prefix + " " + temp
            } else if (selectedUserTypeList.size == 1) {
                temp = selectedUserTypeList[selectedUserTypeList.size - 1].value
            } else {
                temp = ""
            }
            return blockContent + " " + temp
        }
    }

    private fun blockTypeSpinner() {
        /*    binding.sBlockType.onItemSelectedListener = this
            blockSpinnerAdapter = SpinnersAdapter(
                applicationContext,
                blockTypeList,
                getString(R.string.blockType)
            )
            binding.sBlockType.adapter = blockSpinnerAdapter*/
        //blockTypeList = blockTypeList.asReversed()

        if(blockTypeList.isNotEmpty()) {
            blockTypeValue = blockTypeList.get(0).value
            binding.blockView.sBlockType.setText(blockTypeList.get(0).value)
            binding.blockView.sBlockType.setAdapter(
                ArrayAdapter<SpinnerItems>(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    blockTypeList
                )
            )
            blockTypeTag(0)

        }
    }

    private fun userListSpinner() {
        saveUserList(userList)
    }

    private fun stageUserSpinner() {
        saveAgentList(userList)
    }

    private fun branchListSpinner() {
        saveBranchList(branchList)
    }

    private fun setButtonObservable() {
        blockViewModel.changeButtonBackground1(
            seatNumbers,
            selectedUserTypeList,
            userTypeId,
            branchId.toString(),
            userId,
            agentId,
            blockTypeValue,
            fromDate,
            toDate,
            timeHH,
            timeMM,
            isAllowMultipleQuota
        )
    }

    private fun blockTypeTag(position: Int) {
        blockTypeId = blockTypeList[position].id
        blockTypeValue = blockTypeList[position].value
        binding.toolbarPassengerDetails.tvCurrentHeader.text = getString(R.string.blocking)
        if (blockTypeValue.equals("permanent", true)) {

            binding.blockView.blockDurationTextView.gone()
            //binding.tvFromDate.text=null
            binding.blockView.tvFromDate.visibility = View.GONE
            //binding.tvToDate.text=null
            binding.blockView.tvToDate.visibility = View.GONE
            binding.blockView.blockDateDurationLinearLayout.gone()
            binding.blockView.btnBlockSeats.setBackgroundResource(R.drawable.button_selected_bg)
            binding.blockView.btnBlockSeats.isEnabled = true


            blockViewModel.changeButtonBackground1(
                selectedSeats,
                selectedUserTypeList,
                userTypeId,
                branchId.toString(),
                userId,
                agentId,
                blockTypeValue,
                fromDate,
                toDate,
                timeHH,
                timeMM,
                isAllowMultipleQuota
            )

            binding.blockView.releaseTimeTextView.gone()
            binding.blockView.layoutHour.gone()
            binding.blockView.editTextHour.gone()
            binding.blockView.layoutMinute.gone()
            binding.blockView.editTextMinute.gone()
            binding.blockView.selectRecurringDays.gone()

            binding.blockView.mainWeekendLayout.gone()
        }
        else if (blockTypeValue.equals("none", true)) {
            binding.blockView.mainWeekendLayout.visible()
            binding.blockView.selectRecurringDays.visible()

            binding.blockView.blockDurationTextView.visibility = View.GONE
            binding.blockView.blockDateDurationLinearLayout.visibility = View.GONE
            //binding.tvFromDate.text=""
            binding.blockView.tvFromDate.visibility = View.GONE
            //binding.tvToDate.text=""
            binding.blockView.tvToDate.visibility = View.GONE
            binding.blockView.releaseTimeTextView.visibility = View.GONE
            binding.blockView.layoutHour.visibility = View.GONE
            //binding.editTextHour.text=null
            binding.blockView.editTextHour.visibility = View.GONE
            binding.blockView.layoutMinute.visibility = View.GONE
            //binding.editTextMinute.text=null
            binding.blockView.editTextMinute.visibility = View.GONE

        }
        else if (blockTypeValue.equals("custom", true) || blockTypeValue.equals(
                "temporary",
                true
            )
        ) {
            binding.blockView.selectRecurringDays.visible()
            binding.blockView.mainWeekendLayout.visible()
            binding.blockView.releaseTimeTextView.visibility = View.GONE
            binding.blockView.layoutHour.visibility = View.GONE
            binding.blockView.editTextHour.visibility = View.GONE
            binding.blockView.layoutMinute.visibility = View.GONE
            binding.blockView.editTextMinute.visibility = View.GONE
            binding.blockView.editTextHour.text = null
            binding.blockView.editTextMinute.text = null


            binding.blockView.blockDurationTextView.visibility = View.VISIBLE
            binding.blockView.blockDateDurationLinearLayout.visibility = View.VISIBLE
            binding.blockView.tvFromDate.visibility = View.VISIBLE
            binding.blockView.tvToDate.visibility = View.VISIBLE
            toDate = binding.blockView.tvToDate.text.toString()
            fromDate = binding.blockView.tvFromDate.text.toString()

        }
        /*if (position > 0) {
           *//* binding.tvBlockTypeHint.visible()
            blockSpinnerAdapter.selectViewColor()*//*
        } else {
            binding.includeToolbar.tvCurrentHeader.text = getString(R.string.blocking)
           *//* binding.tvBlockTypeHint.gone()
            blockSpinnerAdapter.unSelectViewColor()*//*
        }*/
    }

    @SuppressLint("SetTextI18n")
    private fun userTypeTag(
        position: Int
    ) {
        userTypeId = this.userTypeList[position].id
        userTypeValue = this.userTypeList[position].value

        // set toolbar header
        binding.toolbarPassengerDetails.tvCurrentHeader.text =
            "$userTypeValue - ${getString(R.string.blocking)}"
        // when user type "onl-Agt"
        userTypeDefaultHO()
        userTypeOnlineAgent()

        //when user type "USER"
        userTypeUSER()
    }

    private fun userTypeUSER() {
        if (selectedUserTypeList.any {
                it.value.equals(
                    "USER",
                    true
                )
            } && selectedUserTypeList.size == 1) {
            binding.blockView.tvBranchType.visible()
            binding.blockView.layoutBranch.visible()
            binding.blockView.tvSelectUserType.visible()
            binding.blockView.layoutUser.visible()

            //binding.tvBranchTypeSpinner.setText(getString(R.string.selectBranch))
            //binding.tvUserSpinner.setText(getString(R.string.selectUser))
            //binding.tvBranchHint.gone()
            //binding.tvUserHint.gone()
            //binding.tvBranchTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
            //binding.tvUserSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)

            // call "user_list" api on selection of "USER" from "user type" spinner
            callBranchListApi()
        } else {
            hiddenPropertiesForUser()
        }
    }

    private fun hiddenPropertiesForUser() {
        binding.blockView.tvBranchType.gone()
        binding.blockView.layoutBranch.gone()
        binding.blockView.tvSelectUserType.gone()
        binding.blockView.layoutUser.gone()
        //binding.tvBranchHint.gone()
        //binding.tvUserHint.gone()
        // binding.tvBranchTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
        //binding.tvUserSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
    }

    private fun hiddenPropertiesAll() {
        if (isAllowMultipleQuota && !userTypeValue.equals("USER", true))
            hiddenPropertiesForUser()
        // binding.tvFromDate.gone()
        binding.blockView.tvFromDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)

        binding.blockView.tvToHint.gone()
        binding.blockView.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)

        /*binding.tvBlockTypeHint.gone()
        if (::blockSpinnerAdapter.isInitialized)
            blockSpinnerAdapter.unSelectViewColor()*/

        // binding.tvUserTypeHint.gone()
        /*if (::spinnerAdapter.isInitialized)
            spinnerAdapter.unSelectViewColor()*/

        //binding.tvAgentHint.gone()
        //binding.tvAgentTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
    }

    private fun userTypeOnlineAgent() {
        if (selectedUserTypeList.any {
                it.value.equals(
                    "Onl-Agt",
                    true
                )
            } && selectedUserTypeList.size == 1) {
            binding.blockView.tvAgentType.visible()
            binding.blockView.layoutAgent.visible()

            /*if (binding.tvAgentTypeSpinner.text.toString() != getString(R.string.selectAgent)) {
                binding.tvAgentHint.visible()
                binding.tvAgentTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
            } else {
                binding.tvAgentHint.gone()
                binding.tvAgentTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
            }*/

            callUserListApi()
        } else {
            // binding.tvAgentHint.gone()
            binding.blockView.tvAgentType.gone()
            binding.blockView.layoutAgent.gone()
            //binding.tvAgentTypeSpinner.setBackgroundResource(R.drawable.header_gradient_bg_underline)
        }
    }
    private fun userTypeDefaultHO() {
        if (selectedUserTypeList.singleOrNull()?.value.equals("Default HO", ignoreCase = true)) {
            if (genderDefaultHoQuotaBlocked && privilegeResponseModel?.country.equals("India", true) ) {
                binding.blockView.allGenderGroup.visible()

                selectedGender = if (binding.blockView.groupMale.isChecked) "Male" else "Female"
                binding.blockView.allGenderGroup.setOnCheckedChangeListener { _, checkedId ->
                    selectedGender = when (checkedId) {
                        binding.blockView.groupFemale.id -> "Female"
                        else -> "Male"
                    }
                }
            } else {
                binding.blockView.allGenderGroup.gone()
                selectedGender = ""
            }
        } else {
            binding.blockView.allGenderGroup.gone()
            selectedGender = ""
        }
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onCancelled() {

    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?,
        hours: Int,
        minutes: Int
    ) {
        val scale = resources.displayMetrics.density
        if (firstDate != null) {
            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours)
                firstDate.set(Calendar.MINUTE, minutes)
                binding.blockView.tvFromDate.visible()
                binding.blockView.tvFromHint.visible()
                binding.blockView.tvFromDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)


                if (dateType != null && dateType == getString(R.string.fromDate)) {
                    fromDate = SimpleDateFormat(
                        DATE_FORMAT_D_M_Y,
                        Locale.getDefault()
                    ).format(firstDate.time)

                    binding.blockView.tvFromDate.text = fromDate
                    val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                    val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                    binding.blockView.tvFromDate.setPadding(
                        paddingtLeftRightinDp,
                        paddingtTopinDp,
                        paddingtLeftRightinDp,
                        0
                    )
                } else {
                    toDate = SimpleDateFormat(
                        DATE_FORMAT_D_M_Y,
                        Locale.getDefault()
                    ).format(firstDate.time)

                    binding.blockView.tvToDate.text = toDate
                    binding.blockView.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                    binding.blockView.tvToHint.visible()
                    val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                    val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                    binding.blockView.tvToDate.setPadding(
                        paddingtLeftRightinDp,
                        paddingtTopinDp,
                        paddingtLeftRightinDp,
                        0
                    )
                }

            } else {
                binding.blockView.tvToHint.visible()
                binding.blockView.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                fromDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)
                binding.blockView.tvFromDate.text = fromDate
                val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                binding.blockView.tvFromDate.setPadding(
                    paddingtLeftRightinDp,
                    paddingtTopinDp,
                    paddingtLeftRightinDp,
                    0
                )


                toDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(secondDate.time)
                binding.blockView.tvToDate.text = toDate
                binding.blockView.tvToDate.setPadding(
                    paddingtLeftRightinDp,
                    paddingtTopinDp,
                    paddingtLeftRightinDp,
                    0
                )

                setButtonObservable()
            }
        } else {
            binding.blockView.tvFromDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)
            binding.blockView.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
            binding.blockView.tvFromDate.gone()
            binding.blockView.tvToHint.gone()
        }
        setButtonObservable()

    }

}