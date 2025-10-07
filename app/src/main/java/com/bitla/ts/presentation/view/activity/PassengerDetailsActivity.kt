package com.bitla.ts.presentation.view.activity

import SingleViewModel
import android.annotation.*
import android.app.*
import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.text.*
import android.view.*
import android.view.animation.*
import android.view.inputmethod.*
import android.widget.*
import androidx.core.content.*
import androidx.core.view.*
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
import com.bitla.ts.domain.pojo.book_ticket_full.request.*
import com.bitla.ts.domain.pojo.book_ticket_full.request.BookingDetails
import com.bitla.ts.domain.pojo.booking_custom_request.*
import com.bitla.ts.domain.pojo.custom_applied_coupons.*
import com.bitla.ts.domain.pojo.fare_breakup.request.*
import com.bitla.ts.domain.pojo.fare_breakup.request.ReqBody
import com.bitla.ts.domain.pojo.fare_breakup.response.*
import com.bitla.ts.domain.pojo.getCouponDiscount.*
import com.bitla.ts.domain.pojo.getCouponDiscount.Response.*
import com.bitla.ts.domain.pojo.getPrefillPassenger.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.passenger_details_result.*
import com.bitla.ts.domain.pojo.passenger_details_result.ContactDetail
import com.bitla.ts.domain.pojo.passenger_history.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.rutDiscountDetails.request.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.google.gson.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.*
import setMaxLength
import timber.log.*
import toast
import visible

class PassengerDetailsActivity : BaseActivity(), View.OnClickListener, OnItemClickListener,
    DialogButtonListener, DialogSingleButtonListener, OnItemPassData, OnItemCheckedListener {

    companion object {
        val TAG: String = PassengerDetailsActivity::class.java.simpleName
        lateinit var binding: PassengerDetailsActivityBinding
    }
    
    private lateinit var binding: PassengerDetailsActivityBinding
    private var displayPassengerDetailsByMobile: Boolean = false
    private var checkedPassengerList = mutableListOf<PassengerHistoryModel>()
    private var passengerHistory = mutableListOf<PassengerHistoryModel>()
    private var isFirstNameMandatory: Boolean = false
    private var isLastNameMandatory: Boolean = false
    private var isInsuranceChecked: Boolean = false
    private var allowQoalaInsurance: Boolean = false
    private var insuranceMandatoryForBookings: Boolean = false
    private var enableInsuranceCheckboxForBooking: Boolean = false
    private var freezeMealSelection: Boolean = false
    private var selectedMealTypes: Any? = null
    private var isMealNoType: Boolean = false
    private var isMealRequired: Boolean = false
    private var serviceNumber: String = ""
    private var isOwnRoute: Boolean = false
    private var deletePassengerPosition: Int? = null
    private lateinit var seatLegendsAdapter: SeatLegendsAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var passengerDetailsAdapter: PassengerDetailsAdapter
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

    private var passengerFieldMandatory: String? = ""
    private var passengerFieldOptional: String? = ""
    private var passengerFieldHide: String? = ""

    private var nameFiledMandatory: String = ""
    private var ageFiledMandatory: String = ""
    private var genderFiledMandatory: String = ""
    private var idTypeFiledMandatory: String = ""
    private var idNumberFiledMandatory: String = ""

    private var mobileNumberForMeals: String? = ""

    private var mobileNumberFiledMandatory: String? = ""
    private var alternativeMobileNumberFiledMandatory: String? = ""
    private var emailFiledMandatory: String? = ""

    private var isMobileMandatory: Boolean = false
    private var isAlterNateMobileMandatory: Boolean = false
    private var isEmailMandatory: Boolean = false
    private var isNameMandatory: Boolean = false
    private var isAgeMandatory: Boolean = false
    private var isGenderMandatory: Boolean = false
    private var isIdTypeMandatory: Boolean = false
    private var isIdNumberMandatory: Boolean = false
    private var isSeatNumberMandatory: Boolean = false
    private var isExtraSeatFareMandatory: Boolean = false

    private var isMandatoryFlag: Boolean = false
    private var isMandatory: String? = "Mandatory"

    private var countryCode: String? = ""
    private var mobileNumber: String? = ""
    private var alternativeMobileNumber: String? = ""
    private var email: String? = ""

    private var totalFare: Double = 0.0
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var busType: String? = null
    private var deptTime: String? = null

    var check: Boolean? = null

    var selectedSeatDetails = java.util.ArrayList<SeatDetail>()
    var selectedExtraSeatDetails = java.util.ArrayList<SeatDetail>()
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

    private lateinit var retrievePaxAdapter: RetrievePaxAdapter
    private val passengerHistoryViewModel by viewModel<PassengerHistoryViewModel<Any?>>()


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

        binding.apply {
            layoutPassengerDetailsSeatLegends.passengerDetailsTvOkay.setOnClickListener(this@PassengerDetailsActivity)
            passengerDetailsPaymentProceed.passengerDetailsImgArrowUp.setOnClickListener(this@PassengerDetailsActivity)
            passengerDetailsPaymentProceed.btnProcceed.setOnClickListener(this@PassengerDetailsActivity)
        }

        getPref()
        getObserverCoupon()
        getRutObserver()
        getPrefillPassengerDetailsObserver()

        callPrivilegeDetailsApi()
        setPrivilegesObserver()
        mealInfoObserver()
        paxHistoryObserver()
        getSeatDetails()
        initRecyclerView()
        setToolbarTitle()
        accountObserver()
        qoalaInsurance()

        if (isFromChile && allowAutoDiscount) {
            binding.autoDiscountLayout.visible()
            getCouponDetails()
        } else {
            binding.autoDiscountLayout.gone()
        }

        binding.perBooking.isChecked = true
        binding.perSeat.setOnClickListener {
            validRut = true

            rutApplied = false

            passengerList.forEach {
                it.idnumber = ""
                it.idType = ""
            }
            binding.discountText.gone()
            binding.discountCouponLayout.gone()
            isPerSeat = true
            PreferenceUtils.removeKey("AutoDiscountCouponCode")
            binding.dicountCodeDrop.text.clear()
            val emptyArray2 = GetPrefillResponse()
            PreferenceUtils.putPrefillData(emptyArray2)
            getAllSeatList?.let { setPassengerListAdapter(passengerList, it, "", false, false) }
        }
        binding.perBooking.setOnClickListener {
            validRut = true

            rutApplied = false

            passengerList.forEach {
                it.idnumber = ""
                it.idType = ""
            }
            binding.discountText.visible()
            binding.discountCouponLayout.visible()
            isPerSeat = false
            couponCodeList.clear()
            seatNumberList.clear()
            val emptyArray = arrayListOf<SeatWiseFare>()
            PreferenceUtils.putSelectedCoupon(emptyArray)
            val emptyArray2 = GetPrefillResponse()
            PreferenceUtils.putPrefillData(emptyArray2)
            getAllSeatList?.let { setPassengerListAdapter(passengerList, it, "", false, false) }
        }
        binding.dicountCodeDrop.onChange {
            if (!binding.dicountCodeDrop.text.isNullOrEmpty()) {
                getAllSeatList?.let { setPassengerListAdapter(passengerList, it, "", false, true) }
                binding.crossClick.visible()
            } else {
                binding.crossClick.gone()
            }
            isCodeSelected = true
            selectedDiscountCodeList.forEach {
                if (it.coupon_name == binding.dicountCodeDrop.text.toString()) {
                    selectedDiscountCode = it

                    PreferenceUtils.putString(
                        "AutoDiscountCouponCode",
                        binding.dicountCodeDrop.text.toString()
                    )
                }
            }
            val emptyArray = arrayListOf<SeatWiseFare>()
            PreferenceUtils.putSelectedCoupon(emptyArray)


        }
        binding.crossClick.setOnClickListener {
            PreferenceUtils.removeKey("AutoDiscountCouponCode")
            binding.dicountCodeDrop.text.clear()
            binding.discountCouponLayout.isFocusable = true
            binding.discountCouponLayout.clearFocus()
            binding.dicountCodeDrop.isEnabled = true
            getAllSeatList?.let { setPassengerListAdapter(passengerList, it, "", false, false) }

        }
        
        retrievePax()
    }

    private fun paxHistoryObserver() {
        passengerHistoryViewModel.dataPassengersHistory.observe(this) {
            if (it != null && it.code == 200) {
                passengerHistory =
                    it.body.distinctBy { it.name } as MutableList<PassengerHistoryModel>
                if (passengerHistory.isNotEmpty())
                    dialogRetrievePax()
            } else if (it.code == 401) {
                /*DialogUtils.unAuthorizedDialog(
                    this,
                    "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                    this
                )*/
                showUnauthorisedDialog()

            } else {
                it?.message?.let { it1 -> toast(it1) }
            }
        }
    }

    private fun retrievePax() {
        if (displayPassengerDetailsByMobile) {
            binding.tvRetrievePax.visible()
            binding.tvRetrievePax.setOnClickListener {

                firebaseLogEvent(
                    this,
                    RETRIEVE_PASSENGER,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    RETRIEVE_PASSENGER,
                    RETRIEVE_PASSENGER
                )

                checkedPassengerList.clear()
                if (binding.edtMobileNumber.text.toString().isNotEmpty())
                    getPassengerHistoryApi()
                else
                    toast(getString(R.string.validate_mobile_number))
            }
        } else
            binding.tvRetrievePax.gone()
    }

    private fun qoalaInsurance() {
        if (allowQoalaInsurance && !selectedSeatDetails.any { it.isExtraSeat }) {
            binding.layoutInsurance.visible()
            if (insuranceMandatoryForBookings) {
                binding.checkInsurance.isChecked = true
                isInsuranceChecked = true
            } else {
                binding.checkInsurance.isChecked = enableInsuranceCheckboxForBooking
                isInsuranceChecked = enableInsuranceCheckboxForBooking
            }
            binding.checkInsurance.isClickable = !insuranceMandatoryForBookings
            PreferenceUtils.setPreference(
                PREF_IS_INSURANCE_CHECKED,
                binding.checkInsurance.isChecked
            )
            binding.checkInsurance.setOnCheckedChangeListener { compoundButton, isChecked ->
                isInsuranceChecked = isChecked
                PreferenceUtils.setPreference(PREF_IS_INSURANCE_CHECKED, isInsuranceChecked)
                if (isChecked) {
                    privilegePassengerMobileNo = getString(R.string.mandatory)
                    privilegePassengerSex = getString(R.string.mandatory)
                    privilegePassengerAge = getString(R.string.mandatory)
                    nameFiledMandatory = getString(R.string.mandatory)
                    ageFiledMandatory = getString(R.string.mandatory)
                    binding.layoutMobileNo.visible()
                } else {
                    if (::privilegeResponseModel.isInitialized) {
                        privilegeResponseModel.appPassengerDetailConfig?.apply {
                            privilegePassengerMobileNo = phoneNumber?.option
                            privilegePassengerSex = title?.option
                            nameFiledMandatory = name?.option!!
                            privilegePassengerAge = age?.option
                            ageFiledMandatory = age?.option!!
                            genderFiledMandatory = title?.option!!
                        }
                    }
                    checkPassengerContactDetailsHide()
                }

                if (::passengerDetailsAdapter.isInitialized)
                    passengerDetailsAdapter.insuranceCheckbox(
                        isInsuranceChecked,
                        privilegePassengerAge,
                        privilegePassengerSex
                    )
            }

        } else {
            binding.layoutInsurance.gone()
        }
    }

    private fun mealInfoObserver() {
        singleViewModel.mealInfoLiveData.observe(this)
        {
            isMealRequired = it.isMealRequired ?: false
            isMealNoType = it.isMealNoType ?: false
            selectedMealTypes = it.selectedMealTypes

            Timber.d("isMealNoType $isMealNoType")
        }
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
        }

        if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
            val boardingStageDetail =
                PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)!!
            boardingPoint = boardingStageDetail.name!!
            boardingId = boardingStageDetail.id
        }
        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
            currencyFormat = getCurrencyFormat(this, privilegeResponseModel.currencyFormat)

            if (privilegeResponseModel.isChileApp) {
                isFromChile = true
                if (privilegeResponseModel.availableAppModes?.allowAutoDiscount != null) {
                    allowAutoDiscount =
                        privilegeResponseModel.availableAppModes?.allowAutoDiscount!!
                }
                if (allowAutoDiscount) {
                    if (privilegeResponseModel.isAutoDiscountRutEnable != null) {
                        allowRutDiscount = privilegeResponseModel.isAutoDiscountRutEnable!!
                    }
                }
            }

            allowQoalaInsurance = privilegeResponseModel.allowQoalaInsurance
            insuranceMandatoryForBookings = privilegeResponseModel.insuranceMandatoryForBookings
            enableInsuranceCheckboxForBooking =
                privilegeResponseModel.enableInsuranceCheckboxForBooking

            displayPassengerDetailsByMobile = privilegeResponseModel.displayPassengerDetailsByMobile
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

        if (countryList.isNotEmpty()) {
            binding.mobileNoCountryCode.setText(countryList[0].toString())
            binding.alternativeMobileNoCountryCode.setText(countryList[0].toString())
        }
    }

    private fun setToolbarTitle() {
        srcDest = "$source-$destination"
        // val subtitle = "${getDateDMYY(travelDate)} $deptTime - | $busType"
        val subtitle = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${travelDate.let { getDateDMYY(it) }} $deptTime | $busType"
        else
            "${getDateDMYY(travelDate)} $deptTime | $busType"
        binding.toolbarPassengerDetails.toolbarHeaderText.text = srcDest
        binding.toolbarPassengerDetails.toolbarSubtitle.text = subtitle

        if (intent.getStringExtra(getString(R.string.toolbar_title)) != null) {
            toolbarTitle = intent.getStringExtra(getString(R.string.toolbar_title))!!
            binding.toolbarPassengerDetails.tvCurrentHeader.text = toolbarTitle
        }

        binding.toolbarPassengerDetails.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getSeatDetails() {
        selectedSeatDetails = retrieveSelectedSeats()
        selectedExtraSeatDetails = retrieveSelectedExtraSeats()

        first@ for (i in 0 until selectedSeatDetails.size) {
            if (selectedSeatDetails[i].isExtraSeat) {
                extraSeatFirstPosition = i
                return
            }
        }

        isExtraSeats = (selectedExtraSeatDetails.size > 0)

        noOfSeats = selectedSeatDetails.size.toString()
        if (selectedSeatDetails.any { it.isExtraSeat }) {
            additionalFare = false
            discountAmount = false
        }

        selectedSeatDetails.forEach {
            if (it.editFare != null && it.editFare.toString().isNotEmpty()) {
                totalFare += it.editFare?.toString()!!.toDouble()
            } else {
                if (it.baseFareFilter != null) {
                    totalFare += it.baseFareFilter?.toString()!!.toDouble()
                } else {
                    toast(getString(R.string.server_error))
                }
            }
        }
        val totalNetAmount =
            "${getString(R.string.netAmount)} : $amountCurrency$totalFare"
        binding.passengerDetailsPaymentProceed.tvNetAmt.text = totalNetAmount
    }

    private fun callPrivilegeDetailsApi() {
        lifecycleScope.launch {
            privilegeDetailsViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            passengerHistoryViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            agentAccountInfoViewModel.messageSharedFlow.collect{
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
        privilegeDetailsViewModel.getPrivilegeDetailsApi(
            apiKey = loginModelPref.api_key,
            apiType = privilege_details_method_name,
            respFormat = format_type,
            locale = locale ?: "en"
        )
    }

    private fun getCouponDetails() {
        bookingOptionViewModel.getCouponDetails(
            GetCouponDiscountRequest(
                getDateYMD(dateDMY = travelDate),
                origin = sourceId,
                destination = destinationId,
                no_of_seats = noOfSeats!!,
                is_round_trip = isRoundTrip,
                api_key = loginModelPref.api_key,
                locale = locale
            )
        )
    }

    private fun getObserverCoupon() {
        try {
            bookingOptionViewModel.getCouponDetails.observe(this) {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            val perBookinfNames = arrayListOf<String>()

//                            Timber.d("per_booking_coupons-${it.per_booking_coupons}")

                            if (!it.per_booking_coupons.isNullOrEmpty()) {
                                it.per_booking_coupons.forEach {
                                    if (!it.is_rut_discount) {
                                        selectedDiscountCodeList.add(it)
                                        perBookinfNames.add(it.coupon_name)

                                    }
                                }
                            }

                            if (!it.per_seat_coupons.isNullOrEmpty()) {
                                it.per_seat_coupons.forEach {
                                    if (!it.is_rut_discount) {
                                        perSeatCouponList.add(it)
                                    }
                                }
                            }
                            binding.dicountCodeDrop.setOnClickListener {
                                if (perBookinfNames.isNullOrEmpty()) {
                                    toast(getString(R.string.no_coupon_available))
                                } else {
                                    binding.dicountCodeDrop.showDropDown()
                                }
                            }
                            Timber.d("perseatList: ${perSeatCouponList.size}")

//                        it.per_booking_coupons.forEach {
//                        }
                            binding.dicountCodeDrop.setAdapter(
                                ArrayAdapter(
                                    this,
                                    android.R.layout.simple_dropdown_item_1line,
                                    perBookinfNames
                                )
                            )
                            binding.dicountCodeDrop.setOnClickListener {
                                binding.dicountCodeDrop.showDropDown()
                            }


                            Timber.d("requestBodyDataCoupon: -: ${it}")
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
        startShimmerEffect()
        privilegeDetailsViewModel.privilegeResponseModel.observe(this) {
            if (it != null) {

                when (it.code) {
                    200 -> {
                        PreferenceUtils.setPreference(
                            "otp_validation_time",
                            it.configuredLoginValidityTime
                        )
                        stopShimmerEffect()

                        passengerFieldMandatory = getString(R.string.mandatory)
                        passengerFieldOptional = getString(R.string.optional)
                        passengerFieldHide = getString(R.string.hide)

                        it.apply {
                            if (it.currency != null) {
                                amountCurrency = currency.ifEmpty {
                                    currency
                                }
                            }
                            additionalFare = isAdditionalFare
                            discountAmount = isDiscountOnTotalAmount
                            privilegeResponseModel = it

                            it.appPassengerDetailConfig?.apply {
                                if (allowQoalaInsurance && isInsuranceChecked) {
                                    privilegePassengerMobileNo = getString(R.string.mandatory)
                                    privilegePassengerSex = getString(R.string.mandatory)
                                    privilegePassengerAge = getString(R.string.mandatory)
                                } else {
                                    privilegePassengerMobileNo = phoneNumber?.option
                                    privilegePassengerSex = title?.option
                                    privilegePassengerAge = age?.option
                                }

                                privilegePassengerName = name?.option
                                privilegePassengerAlternateNo = alternateNo?.option
                                privilegePhoneValidationCount = phoneNumValidationCount
                                privilegePassengerEmail = email?.option
                                privilegePassengerAddress = address?.option
                                privilegePassengerIDType = iDType?.option
                                privilegePassengerIDNumber = iDNumber?.option

                                privilegePassengerRemarks = remarks?.option
                                privilegePassengerPrimaryPassengerMandatory =
                                    primaryPassengerMandatory?.option
                                privilegePassengerFirstName = firstName?.option
                                privilegePassengerLastName = lastName?.option

//                                request keyboard focus
                                if (privilegePassengerMobileNo == passengerFieldMandatory
                                ) {
                                    binding.edtMobileNumber.requestFocus()
                                    showKeyboard(binding.edtMobileNumber)
                                } else if (privilegePassengerAlternateNo == passengerFieldMandatory
                                ) {
                                    binding.edtAlterNativeMobileNumber.requestFocus()
                                    showKeyboard(binding.edtAlterNativeMobileNumber)
                                }
                            }

                            if (privilegePassengerName == passengerFieldMandatory)
                                nameFiledMandatory = getString(R.string.mandatory)

                            if (privilegePassengerAge == passengerFieldMandatory)
                                ageFiledMandatory = getString(R.string.mandatory)

                            if (privilegePassengerSex == passengerFieldMandatory)
                                genderFiledMandatory = getString(R.string.mandatory)

                            if (privilegePassengerIDType == passengerFieldMandatory)
                                idTypeFiledMandatory = getString(R.string.mandatory)

                            if (privilegePassengerIDNumber == passengerFieldMandatory)
                                idNumberFiledMandatory = getString(R.string.mandatory)

                            if (privilegePassengerMobileNo.isNullOrEmpty() && !enableInsuranceCheckboxForBooking && !insuranceMandatoryForBookings)
                                binding.layoutMobileNo.gone()
                            else if (privilegePassengerMobileNo == passengerFieldMandatory)
                                mobileNumberFiledMandatory = getString(R.string.mandatory)

                            if (privilegePassengerAlternateNo.isNullOrEmpty()) {
                                binding.layoutEdtAlternativeMobileNumber.gone()
                            } else if (privilegePassengerAlternateNo == passengerFieldMandatory) {
                                alternativeMobileNumberFiledMandatory =
                                    getString(R.string.mandatory)
                            } else if (privilegePassengerAlternateNo == passengerFieldOptional) {
                                alternativeMobileNumberFiledMandatory = getString(R.string.optional)
                            }

                            if (privilegePassengerEmail.isNullOrEmpty())
                                binding.layoutEmailId.gone()
                            else if (privilegePassengerEmail == passengerFieldMandatory)
                                emailFiledMandatory = getString(R.string.mandatory)
                        }

                        freezeMealSelection = it.freezeMealSelection
                        checkPassengerContactDetailsHide()
                        setPassengerDetailsAdapter()

                        if (isAgentLogin == true) {
                            agentAccountInfo()
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
                        it.result.message?.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    override fun initUI() {
        binding = PassengerDetailsActivityBinding.inflate(layoutInflater)
        countryCode = binding.mobileNoCountryCode.text.toString()

        binding.edtMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mobileNumber = s.toString()

                if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null) {
                    privilegePhoneValidationCount?.let { binding.edtMobileNumber.setMaxLength(it) }

                    if (!mobileNumber.isNullOrEmpty() && mobileNumber.toString().length == privilegePhoneValidationCount ?: 100
                    ) {
                        binding.layoutEdtMobileNumber.isErrorEnabled = false
                    }

                } else if (mobileNumberFiledMandatory == passengerFieldMandatory) {
                    if (!mobileNumber.isNullOrEmpty()) {
                        isMobileMandatory = false
                        binding.layoutEdtMobileNumber.isErrorEnabled = false
                    } else {
                        isMobileMandatory = true
                        binding.layoutEdtMobileNumber.isErrorEnabled = true
                        binding.layoutEdtMobileNumber.error = "enter mobile no"
                    }

                }

            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.edtAlterNativeMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null) {
                    privilegePhoneValidationCount?.let {
                        binding.edtAlterNativeMobileNumber.setMaxLength(
                            it
                        )
                    }

                    if (!alternativeMobileNumber.isNullOrEmpty()
                        && alternativeMobileNumber.toString().length == privilegePhoneValidationCount ?: 100
                    ) {
                        binding.layoutEdtAlternativeMobileNumber.isErrorEnabled = false
                        alternativeMobileNumber = binding.edtAlterNativeMobileNumber.text.toString()

                    }

                } else if (alternativeMobileNumberFiledMandatory == passengerFieldMandatory) {
                    if (!s.isNullOrEmpty()) {
                        isAlterNateMobileMandatory = false
                        binding.layoutEdtAlternativeMobileNumber.isErrorEnabled = false
                        alternativeMobileNumber = s.toString()

                    } else {
                        isAlterNateMobileMandatory = true
                        binding.layoutEdtAlternativeMobileNumber.isErrorEnabled = true
                        binding.layoutEdtAlternativeMobileNumber.error = "enter mobile no"
                    }

                } else if (alternativeMobileNumberFiledMandatory == passengerFieldOptional) {
                    isAlterNateMobileMandatory = false
                    binding.layoutEdtAlternativeMobileNumber.isErrorEnabled = false
                    if (!s.isNullOrEmpty()) {
                        alternativeMobileNumber = s.toString()
                    }


                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.etEmailId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                email = s.toString()

                if (!email.isNullOrEmpty() && isEmailValid(email))
                    binding.layoutEmailId.isErrorEnabled = false
                else {
                    if (emailFiledMandatory == passengerFieldMandatory) {
                        binding.layoutEmailId.isErrorEnabled = true
                        binding.layoutEmailId.error = "enter email id"
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.mobileNoCountryCode.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                countryList
            )
        )

        binding.mobileNoCountryCode.setOnClickListener {
            binding.mobileNoCountryCode.showDropDown()
        }
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
    }

    private fun setObserver() {

        bookingOptionViewModel.fareBreakup.observe(this) {
            it
            Timber.d("fareBreakupByAli $it")
            if (it != null) {
                if (it.code == 200) {
                    fareBreakUpHashNoDataFound = true
                    setChargesAdapter(it.fare_break_up_hash as ArrayList<FareBreakUpHash>)
                } else {
                    toast(it.message)
                }
            } else {
                toast(getString(R.string.server_error))
                fareBreakUpHashNoDataFound = false
            }
        }
    }

    private fun fareBreakupApi() {
        val seatNumberArray = mutableListOf<String>()
        selectedSeatDetails.forEach {
            seatNumberArray.add(it.number.toString())
        }

        val returnSeatNumberArray = mutableListOf<String>()
        val couponDetails = mutableListOf<String>()

        val reqBody = ReqBody()
        val bookingDetails = BookingDetails()

        // Coupon Code
        val couponCodeIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.coupon_code) }
        if (couponCodeIndex != -1) {
            val couponCode = appliedCouponList[couponCodeIndex].coupon_code
            Timber.d("couponCode $couponCode")
            bookingDetails.couponCode = couponCode
        }

        // Free Ticket
        val isFreeTicket =
            appliedCouponList.any { it.coupon_type == getString(R.string.free_ticket) }
        if (isFreeTicket)
            bookingDetails.isFreeBookingAllowed = isFreeBookingAllowed

        // Vip Booking
        val isVipTicket = appliedCouponList.any { it.coupon_type == getString(R.string.vip_ticket) }
        if (isVipTicket)
            bookingDetails.isVipTicket = vipTicket

        // Discount On Total Amount
        val discountOnTotalIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.discount_amount) }
        if (discountOnTotalIndex != -1) {
            val amount = appliedCouponList[discountOnTotalIndex].coupon_code
            Timber.d("amount $amount")
            reqBody.totalDiscountAmount = amount
        }

        // Smart miles
        val smartMilesIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.apply_smart_miles) }
        if (smartMilesIndex != -1) {
            val smartMilesNumber = appliedCouponList[smartMilesIndex].coupon_code
            Timber.d("smartMilesNumber $smartMilesNumber")
            val smartMilesHash = SmartMilesHash()
            smartMilesHash.phoneNumber = smartMilesNumber
            bookingDetails.smartMilesHash = smartMilesHash
        }

        // Privilege Card
        val privilegeCardIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.privilege_card) }
        if (privilegeCardIndex != -1) {
            val cardOrMobileNumber = appliedCouponList[privilegeCardIndex].coupon_code
            Timber.d("cardOrMobileNumber $cardOrMobileNumber")
            var privilegeMobileNumber = ""
            var cardNumber = ""
            if (isNumeric(cardOrMobileNumber)) {
                privilegeMobileNumber = cardOrMobileNumber.trim()
                cardNumber = getString(R.string.empty)
            } else {
                cardNumber = cardOrMobileNumber.trim()
                privilegeMobileNumber = getString(R.string.empty)
            }

            val privilegeCardHash = PrivilegeCardHash()
            privilegeCardHash.cardNumber = cardNumber
            privilegeCardHash.mobileNumber = privilegeMobileNumber
            privilegeCardHash.reservationId = resId.toString()
            privilegeCardHash.selectedSeats = noOfSeats
            bookingDetails.privilegeCardHash = privilegeCardHash
        }

        // Pre-postpone
        val prePostponeIndex =
            appliedCouponList.indexOfFirst { it.coupon_type == getString(R.string.pre_postpone_ticket) }
        if (prePostponeIndex != -1) {
            val prePostponeNumber = appliedCouponList[prePostponeIndex].coupon_code
            Timber.d("prePostponeNumber $prePostponeNumber")
            bookingDetails.prePostPonePnr = prePostponeNumber
            bookingDetails.isMatchPrepostponeAmount = isMatchPrepostponeAmount
        }

        if (selectedSeatDetails.any { it.additionalFare != null && it.additionalFare!! > 0.0 }) {
            val additionalFares = mutableListOf<AdditionalFare>()
            selectedSeatDetails.forEach {
                val additionalFare = AdditionalFare()
                additionalFare.seatNo = it.number
                additionalFare.fare = it.additionalFare.toString()
                additionalFares.add(additionalFare)
            }
            reqBody.additionalFare = additionalFares
        }

        if (selectedSeatDetails.any { it.fare != null && it.fare.toString().toDouble() > 0.0 }) {
            val editFares = mutableListOf<EditFare>()
            selectedSeatDetails.forEach {
                val editFare = EditFare()
                editFare.seatNo = it.number
                if (it.editFare != null && it.editFare.toString().isNotEmpty())
                    editFare.fare = it.editFare.toString()
                else
                    editFare.fare = it.baseFareFilter.toString()
                editFares.add(editFare)
            }
            reqBody.editFare = editFares
        }

        // individual discount
        if (selectedSeatDetails.any { it.discountAmount != null && it.discountAmount!! > 0.0 }) {
            val seatWiseFares = mutableListOf<SeatWiseFare>()
            selectedSeatDetails.forEach {
                val seatWiseFare = SeatWiseFare()
                seatWiseFare.seatNo = it.number
                seatWiseFare.discount = it.discountAmount.toString()
                seatWiseFares.add(seatWiseFare)
            }
            reqBody.seatWiseFare = seatWiseFares
        }

        // extra seat fare
        if (selectedSeatDetails.any { it.isExtraSeat }) {
            reqBody.isExtraSeat = true
            val extraSeatFares = mutableListOf<ExtraSeatFare>()
            selectedSeatDetails.forEach {
                val extraSeatFare = ExtraSeatFare()
                extraSeatFare.seatNo = it.number
                if (it.editFare != null && it.editFare.toString().isNotEmpty())
                    extraSeatFare.fare = it.editFare.toString()
                else
                    extraSeatFare.fare = it.fare.toString()
                extraSeatFares.add(extraSeatFare)
            }
            reqBody.extraSeatFare = extraSeatFares
        }

        reqBody.bookingDetails = bookingDetails

        reqBody.apiKey = loginModelPref.api_key
        reqBody.resId = resId.toString()
        reqBody.origin = sourceId
        reqBody.destination = destination
        reqBody.boardingAt = boardingPoint
        reqBody.dropOff = droppingPoint
        reqBody.noOfSeats = noOfSeats?.toInt()
        reqBody.isMiddleTier = is_middle_tier
        reqBody.isRoundTrip = isRoundTrip
        reqBody.isBima = "$isBima"
        reqBody.seatNumbers = seatNumberArray
        reqBody.returnSeatNumbers = returnSeatNumberArray
        reqBody.passengerTitles = PassengerTitles()
        reqBody.returnBoardingAt = returnBoardingPoint
        reqBody.returnDropoff = returnDroppingPoint
        reqBody.offerCoupon = offerCoupon
        reqBody.promoCoupon = promoCoupon
        reqBody.useSmartMiles = useSmartMiles
        reqBody.privCardNumber = privilegeCardNo
        // reqBody.previousPnrDetails = PreviousPnrDetails()
        reqBody.couponDetails = couponDetails
        reqBody.paymentType = paymentType
        reqBody.locale = locale


        val fareBreakupRequest = FareBreakupRequest()
        fareBreakupRequest.bccId = bccId.toString()
        fareBreakupRequest.format = format_type
        fareBreakupRequest.methodName = fare_breakup_method_name
        fareBreakupRequest.reqBody = reqBody

        /* bookingOptionViewModel.fareBreakupApi(
             authorization = loginModelPref.auth_token,
             apiKey = loginModelPref.api_key,
             fareBreakupRequest = fareBreakupRequest,
             apiType = fare_breakup_method_name
         )*/

        bookingOptionViewModel.fareBreakupApi(
            reqBody,
            apiType = fare_breakup_method_name
        )

        Timber.d("fareBreakupRequest ${Gson().toJson(fareBreakupRequest)}")
    }

    private fun setChargesAdapter(fareBreakUpHashList: ArrayList<FareBreakUpHash>) {
        val fareBreakup = mutableListOf<FareBreakUpHash>()
        for (i in 0..fareBreakUpHashList.size.minus(1)) {
            if (fareBreakUpHashList[i].value.toString().toDouble() > 0.0)
                fareBreakup.add(fareBreakUpHashList[i])
        }

        layoutManager = GridLayoutManager(this, 2)
        binding.passengerDetailsPaymentProceed.rvCharges.layoutManager = layoutManager
        val bookingChargesAdapter = BookingChargesAdapter(
            this,
            fareBreakup,
            amountCurrency,
            currencyFormat
        )
        binding.passengerDetailsPaymentProceed.rvCharges.adapter = bookingChargesAdapter
    }

    private fun initRecyclerView() {
        binding.layoutPassengerDetailsSeatLegends.rvPassengersDetailsSeats.apply {

            layoutManager = LinearLayoutManager(
                this@PassengerDetailsActivity,
                LinearLayoutManager.HORIZONTAL, false
            )
            binding.layoutPassengerDetailsSeatLegends.rvPassengersDetailsSeats.adapter =
                SeatLegendsAdapter()
            seatLegendsAdapter = SeatLegendsAdapter()
            adapter = seatLegendsAdapter
        }
    }

    private fun setPassengerDetailsAdapter() {

        /*spinnerItems = SpinnerItems(1, getString(R.string.pan_card))
        idTypeList.add(spinnerItems)
        spinnerItems = SpinnerItems(2, getString(R.string.dl))
        idTypeList.add(spinnerItems)
        spinnerItems = SpinnerItems(3, getString(R.string.passport))
        idTypeList.add(spinnerItems)
        spinnerItems = SpinnerItems(4, getString(R.string.voter_id))
        idTypeList.add(spinnerItems)
        spinnerItems = SpinnerItems(5, getString(R.string.aadhar_card))
        idTypeList.add(spinnerItems)
        spinnerItems = SpinnerItems(6, getString(R.string.ration_card))
        idTypeList.add(spinnerItems)
        spinnerItems = SpinnerItems(7, getString(R.string.rut))
        idTypeList.add(spinnerItems)
        spinnerItems = SpinnerItems(8, getString(R.string.dni))
        idTypeList.add(spinnerItems)
        spinnerItems = SpinnerItems(9, getString(R.string.ci))
        idTypeList.add(spinnerItems)
        spinnerItems = SpinnerItems(10, getString(R.string.emp_id))
        idTypeList.add(spinnerItems)*/

        idTypeList = getIdTypesList()

        Timber.d("userTypeListResponse $idTypeList")

        selectedSeatDetails.forEach {
            val fare = if (it.editFare != null && it.editFare.toString().isNotEmpty())
                it.editFare
            else
                it.baseFareFilter

            passengerList.add(
                PassengerDetailsResult(
                    expand = true,
                    isPrimary = true,
                    seatNumber = it.number,
                    fare = fare.toString(),
                    contactDetail = passengerContactDetailList,
                    discountAmount = it.discountAmount.toString(),
                    additionalFare = it.additionalFare!!.toDouble().toInt().toString(),
                    isExtraSeat = it.isExtraSeat
                )
            )
        }

        for (i in 0 until passengerList.size) {
            getAllSeatList?.add(passengerList[i].seatNumber.toString())
        }

        getAllSeatList?.let { setPassengerListAdapter(passengerList, it, "", false, false) }
    }

    private fun isAdditionalFare(): Boolean {

        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {

            if (privilegeResponseModel.isAdditionalFare != null) {
                isAgentLogin = privilegeResponseModel.isAgentLogin

                val role = getUserRole(loginModelPref, isAgentLogin, this)
                if (role.contains(getString(R.string.role_agent), true)) {
//                    binding.passengerDetailsPaymentProceed.balanceAvailableHeader.visible()
//                if (privilegeResponseModel.allowDiscountForAgents
//                    && !privilegeResponseModel.isDiscountOnTotalAmount){
//                    isDiccountFlag=true
//                }
                    isAdditionalFlag = false

                } else if (isBima == true || (agentType != null && agentType == "1" || agentType == "2")
                    || (isOwnRoute && privilegeResponseModel.isAllowedToEditFare)
                    || (selectedSeatDetails.any { it.isExtraSeat })
                    || (!isOwnRoute && privilegeResponseModel.isAllowedToEditFareForOtherRoute)
                ) {
                    isAdditionalFlag = false
                } else {
                    isAdditionalFlag = privilegeResponseModel.isAdditionalFare!!
                }
            } else {
                isAdditionalFlag = false
            }
        }
        return isAdditionalFlag
    }


    private fun isDiscountAmount(): Boolean {

        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {

            isAgentLogin = privilegeResponseModel.isAgentLogin
            val role = getUserRole(loginModelPref, isAgentLogin, this)

            if (privilegeResponseModel.isDiscountOnTotalAmount != null) {
                if (role.contains(getString(R.string.role_agent), true)) {
                    isDiccountFlag = true
                } else if (isBima && (agentType != null && agentType == "1" || agentType == "2")
                    || (isOwnRoute && privilegeResponseModel.isAllowedToEditFare)
                    || (!isOwnRoute && privilegeResponseModel.isAllowedToEditFareForOtherRoute)
                ) {
                    isDiccountFlag = true
                } else if (isOwnRoute) {
                    if (!privilegeResponseModel.isAllowedToEditFare) {
                        if (privilegeResponseModel.isAllowDiscountWhileBooking) {
                            isDiccountFlag =
                                (privilegeResponseModel.isDiscountOnTotalAmount == true
                                        || privilegeResponseModel.isDiscountOnTotalAmount == null)
                        }
                    }
                } else {
                    if (!privilegeResponseModel.isAllowedToEditFareForOtherRoute) {
                        if (privilegeResponseModel.isAllowDiscountWhileBookingForOtherRoute) {
                            isDiccountFlag =
                                (privilegeResponseModel.isDiscountOnTotalAmount == true || privilegeResponseModel.isDiscountOnTotalAmount == null)
                        }
                    }
                }
            } else {
                isDiccountFlag = true
            }
        }

        return isDiccountFlag
    }

    private fun hasPassengerContent(): Boolean {
        return isValidationFlag
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setPassengerListAdapter(
        passengerList: MutableList<PassengerDetailsResult>,
        getAllSeatList: ArrayList<String>,
        hideCoupon: String,
        prefillData: Boolean,
        hideData: Boolean
    ) {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPassengerDetails.layoutManager = layoutManager

        passengerDetailsAdapter = PassengerDetailsAdapter(
            this,
            passengerList,
            this,
            this,
            isAdditionalFare(),
            isDiscountAmount()!!,
            idTypeList,
            hasPassengerContent(),
            hasExtraSeats = isExtraSeats,
            hasExtraSeatFirstPosition = extraSeatFirstPosition,
            isNameField = privilegePassengerName,
            isAgeField = privilegePassengerAge,
            isSexField = privilegePassengerSex,
            isIdTypeField = privilegePassengerIDType,
            isIdNumberField = privilegePassengerIDNumber,
            amountCurrency = amountCurrency,
            currencyFormat = currencyFormat,
            mobileNo = privilegePassengerMobileNo!!,
            alternateMobileNo = privilegePassengerAlternateNo!!,
            isPerSeat = isPerSeat,
            couponList = perSeatCouponList,
            onItemPassData = this,
            hideCoupon = hideCoupon,
            preFillData = prefillData,
            hideDiscount = hideData,
            isMealRequired = isMealRequired,
            isMealNoType = isMealNoType,
            selectedMealTypes = selectedMealTypes,
            freezeMealSelection = freezeMealSelection,
            privilegeResponseModel
        )

        binding.rvPassengerDetails.adapter = passengerDetailsAdapter
        passengerDetailsAdapter.notifyDataSetChanged()

        if (::passengerDetailsAdapter.isInitialized && binding.layoutInsurance.isVisible)
            passengerDetailsAdapter.insuranceCheckbox(
                binding.checkInsurance.isChecked,
                privilegePassengerAge,
                privilegePassengerSex
            )
    }

    override fun onClick(v: View) {
        when (v.id) {

//            R.id.passengerDetails_imgSeat -> {
//                binding.layoutPassengerDetailsSeatLegends.root.visible()
//            }
            R.id.passengerDetails_tvOkay -> {
                binding.layoutPassengerDetailsSeatLegends.root.gone()
            }

            R.id.passenger_details_imgArrowUp -> {
                if (!fareBreakUpHashNoDataFound) {
                    toast("No data found")
                } else {
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.mainLayout.windowToken, 0)
                    if (isArrowActive) {
                        isArrowActive = false
                        val shake: Animation =
                            AnimationUtils.loadAnimation(this, R.anim.dialog_slide_down)
                        binding.passengerDetailsPaymentProceed.root.animation = shake
                        binding.passengerDetailsPaymentProceed.constraintLayout4.gone()
                        binding.passengerDetailsPaymentProceed.passengerDetailsImgArrowUp.setBackgroundResource(
                            R.drawable.ic_arrow_down
                        )

                    } else {
                        isArrowActive = true
                        val shake: Animation =
                            AnimationUtils.loadAnimation(this, R.anim.dialog_slide_up)
                        binding.passengerDetailsPaymentProceed.root.animation = shake
                        binding.passengerDetailsPaymentProceed.constraintLayout4.visible()
                        binding.passengerDetailsPaymentProceed.passengerDetailsImgArrowUp.setBackgroundResource(
                            R.drawable.ic_arrow_up
                        )
                    }
                }

            }

            R.id.btn_procceed -> {

                if (validRut) {
                    getPassengerNameList?.clear()
                    getPassengerAgeList?.clear()
                    getPassengerSexList?.clear()
                    getPassengerIdNumberList?.clear()
                    getPassengerIdTypeList?.clear()
                    getPassengerAdditionalFareList?.clear()
                    getPassengerAdditionalDiscountList?.clear()
                    getSeatNoList?.clear()
                    getExtraSeatFareList?.clear()
                    isValidationFlag = true
                    setSelectedPassengers(passengers = passengerList)
                    //   getAllSeatList?.let { setPassengerListAdapter(passengerList, it,"",true, true) }
                    validatePassengerContactDetails()

                    //startShimmerEffect()
//                    goToNextActivity()
                    startShimmerEffect()
                } else {
                    toast(getString(R.string.give_valid_rut))
                }

            }
        }
    }

    private fun goToNextActivity() {
        getPassengerContact()
        getPassengerListData()


        mobileIsMandatory()
        alternativeMobileNumberIsMandatory()
        emailIsMandatory()
        if (!isInsuranceChecked)
            nameIsMandatory()
        else
            isNameMandatory = false
        ageIsMandatory()
        genderIsMandatory()
        idTypeIsMandatory()
        idNumberIsMandatory()
        idSeatNoMandatory()

        if (checkMandatoryFields()) {
            callIntent()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                delay100()
            }
        }

//        Timber.d("passengerContactList${passengerContactDetailList}")
//        Timber.d("getPassengerDetailResponse${getPassengerNameList?.size}")
//        Timber.d("getPassengerDetailResponse${getPassengerNameList}")
//        Timber.d(("passengerListData- $passengerList"))
//        Timber.d(("getPassengerIdTypeList- $getPassengerIdTypeList"))
    }


    private suspend fun delay100() {
        delay(100)
        withContext(Dispatchers.Main) {
            stopShimmerEffect()
        }
    }

    private suspend fun delay2000() {
        delay(2000)
        withContext(Dispatchers.Main) {
            stopShimmerEffect()
        }
    }

    private fun getPassengerContact() {
        countryCode = if (countryCode == getString(R.string.countryCodeNum)) {
            "91-"
        } else {
            binding.mobileNoCountryCode.text.toString()
        }
        passengerContactDetailList.clear()
        if (mobileNumber.isNullOrEmpty()) {
            passengerContactDetailList.add(
                ContactDetail(
                    "$mobileNumber",
                    "$countryCode-$alternativeMobileNumber",
                    email
                )
            )
        } else {
            passengerContactDetailList.add(
                ContactDetail(
                    "$countryCode-$mobileNumber",
                    "$countryCode-$alternativeMobileNumber",
                    email
                )
            )
        }
    }

    private fun getPassengerListData() {

        for (i in 0 until passengerList.size) {
            if (privilegePassengerName == passengerFieldMandatory) {
                if (passengerList[i].name!!.isNotBlank()) {
                    getPassengerNameList?.add(passengerList[i].name.toString())
                }
            }
            if (privilegePassengerAge == passengerFieldMandatory) {
                if (passengerList[i].age.toString().isNotBlank()) {
                    getPassengerAgeList?.add(passengerList[i].age.toString())
                }
            }
            if (privilegePassengerSex == passengerFieldMandatory) {
                if (passengerList[i].sex.toString().isNotBlank()) {
                    getPassengerSexList?.add(passengerList[i].sex.toString())
                }
            }
            if (privilegePassengerIDType == passengerFieldMandatory) {
                if (passengerList[i].idCardType.toString().isNotBlank()) {
                    getPassengerIdTypeList?.add(passengerList[i].idCardType.toString())
                }
            }
            if (privilegePassengerIDNumber == passengerFieldMandatory) {
                if (passengerList[i].idCardNumber.toString().isNotBlank()) {
                    getPassengerIdNumberList?.add(passengerList[i].idCardNumber.toString())
                }
            }
            if (passengerList[i].isExtraSeat) {
                if (passengerList[i].seatNumber.toString().isNotBlank()) {
                    getSeatNoList?.add(passengerList[i].seatNumber.toString())
                }
                if (passengerList[i].fare != null && passengerList[i].fare.toString()
                        .isNotBlank() && passengerList[i].fare.toString().toDouble() != 0.0
                ) {
                    getExtraSeatFareList?.add(passengerList[i].fare.toString())
                }
            }
            if (isAdditionalFare()) {
                if (passengerList[i].additionalFare.toString().isNotBlank()) {
                    getPassengerAdditionalFareList?.add(passengerList[i].additionalFare.toString())
                }
            }
            if (isDiscountAmount() == false) {
                if (passengerList[i].discountAmount.toString().isNotBlank()) {
                    getPassengerAdditionalDiscountList?.add(passengerList[i].discountAmount.toString())
                }
            }
        }

        if (allowQoalaInsurance && binding.layoutInsurance.isVisible && binding.checkInsurance.isChecked && passengerList.any { it.firstName?.isEmpty()!! || it.lastName?.isEmpty()!! }) {
            isFirstNameMandatory = true
            isLastNameMandatory = true
        } else {
            isFirstNameMandatory = false
            isLastNameMandatory = false
        }
    }

    //    check Mandatory Fields
    private fun checkMandatoryFields(): Boolean {
        if (isMobileMandatory || isAlterNateMobileMandatory || isEmailMandatory || isNameMandatory || isAgeMandatory || isGenderMandatory
            || isIdTypeMandatory || isIdNumberMandatory || isSeatNumberMandatory || isExtraSeatFareMandatory || isFirstNameMandatory || isLastNameMandatory
        ) {
            isMandatoryFlag = false
            Timber.d("Mandatory fields not filled")
            toast(getString(R.string.fill_mandatory_fields))
        } else {
            isMandatoryFlag = true
        }

//        for (i in getPassengerNameList!!.indices) {
//            Timber.d("listXyz-name- ${getPassengerNameList!!.size}")
//        }
//        for (i in passengerList.indices) {
//            Timber.d("listXyz-passengerList- ${passengerList.size}")
//        }
//        for (i in getSeatNoList!!.indices) {
//            Timber.d("listXyz-seatList- ${getSeatNoList!!.size}")
//        }
        return isMandatoryFlag
    }

    private fun mobileIsMandatory() {
        if (isInsuranceChecked)
            mobileNumberFiledMandatory = getString(R.string.mandatory)
        if (passengerFieldMandatory == mobileNumberFiledMandatory) {
            isMobileMandatory = true

            if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null) {
                if (!mobileNumber.isNullOrEmpty()
                    && mobileNumberFiledMandatory == isMandatory
                    && mobileNumber.toString().length == privilegePhoneValidationCount ?: 100
                ) {
                    isMandatoryFlag = true
                    isMobileMandatory = false
                } else {
                    isMandatoryFlag = false
                }
            } else {
                isMobileMandatory =
                    passengerFieldMandatory == mobileNumberFiledMandatory && binding.edtMobileNumber.text.isNullOrEmpty()
            }


        } else {
            if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null) {
                if (!mobileNumber.isNullOrEmpty() && mobileNumber.toString().length != privilegePhoneValidationCount
                ) {
                    isMobileMandatory = true
                    toast(getString(R.string.invalid_mobile_number))

                } else {
                    isMobileMandatory = false
                    isMandatoryFlag = false
                }
            } else {
                isMobileMandatory = false
                isMandatoryFlag = false
            }
        }
    }

    private fun emailIsMandatory() {
        if (passengerFieldMandatory == emailFiledMandatory) {
            isEmailMandatory = true
            if (!email.isNullOrEmpty()
                && isEmailValid(email)
                && emailFiledMandatory == isMandatory
            ) {
                isMandatoryFlag = true
                isEmailMandatory = false

            } else {
                isMandatoryFlag = false
            }
        } else {
            if (!email.isNullOrEmpty() && !isEmailValid(email)) {
                isEmailMandatory = true
                toast(getString(R.string.invalid_email_id))

            } else {
                isEmailMandatory = false
                isMandatoryFlag = false
            }
        }
    }

    private fun nameIsMandatory() {
        if (passengerFieldMandatory == nameFiledMandatory) {
            isNameMandatory = true
            if (getPassengerNameList?.size == passengerList.size) {
                isMandatoryFlag = true
                isNameMandatory = false
            } else {
                isMandatoryFlag = false
            }
        } else {
            isMandatoryFlag = false
        }
    }

    private fun ageIsMandatory(): Boolean {
        if (passengerFieldMandatory == ageFiledMandatory) {
            isAgeMandatory = true
            if (getPassengerAgeList?.size == passengerList.size) {
                isMandatoryFlag = true
                isAgeMandatory = false
            } else {
                isMandatoryFlag = false
            }
        } else {
            isMandatoryFlag = false
        }

        return isMandatoryFlag
    }

    private fun genderIsMandatory(): Boolean {
        if (passengerFieldMandatory == genderFiledMandatory) {
            isGenderMandatory = true
            if (getPassengerSexList?.size == passengerList.size) {
                isMandatoryFlag = true
                isGenderMandatory = false
            } else {
                isMandatoryFlag = false
            }
        } else {
            isMandatoryFlag = false
        }
        return isMandatoryFlag
    }

    private fun idTypeIsMandatory(): Boolean {
        if (passengerFieldMandatory == idTypeFiledMandatory) {
            isIdTypeMandatory = true
            if (getPassengerIdTypeList?.size == passengerList.size) {
                isMandatoryFlag = true
                isIdTypeMandatory = false
            } else {
                isMandatoryFlag = false
            }
        } else {
            isMandatoryFlag = false
        }
        return isMandatoryFlag
    }

    private fun idNumberIsMandatory(): Boolean {
        if (passengerFieldMandatory == idNumberFiledMandatory) {
            isIdNumberMandatory = true
            if (getPassengerIdNumberList?.size == passengerList.size) {
                isMandatoryFlag = true
                isIdNumberMandatory = false
            } else {
                isMandatoryFlag = false
            }
        } else {
            isMandatoryFlag = false
        }
        return isMandatoryFlag
    }

    private fun idSeatNoMandatory(): Boolean {

        if (selectedSeatDetails.isNotEmpty() && selectedExtraSeatDetails.isNotEmpty()) {
            for (i in 0 until selectedExtraSeatDetails.size) {
                if (selectedExtraSeatDetails[i].isExtraSeat) {
                    isSeatNumberMandatory = true
                    isExtraSeatFareMandatory = true

                    if (getSeatNoList?.size == selectedExtraSeatDetails.size) {
                        isMandatoryFlag = true
                        isSeatNumberMandatory = false
                    } else {
                        isMandatoryFlag = false
                    }

                    if (getExtraSeatFareList?.size == selectedExtraSeatDetails.size) {
                        isMandatoryFlag = true
                        isExtraSeatFareMandatory = false
                    } else {
                        isMandatoryFlag = false
                    }
                } else if (passengerList[i].isExtraSeat) {
                    isSeatNumberMandatory = true
                    isExtraSeatFareMandatory = true
                    if (getSeatNoList?.size == passengerList.size) {
                        isMandatoryFlag = true
                        isSeatNumberMandatory = false
                    } else {
                        isMandatoryFlag = false
                    }

                    if (getExtraSeatFareList?.size == passengerList.size) {
                        isMandatoryFlag = true
                        isExtraSeatFareMandatory = false
                    } else {
                        isMandatoryFlag = false
                    }
                } else {
                    isMandatoryFlag = false
                }
            }

        } else {
            for (i in 0 until passengerList.size) {
                if (passengerList[i].isExtraSeat) {
                    isSeatNumberMandatory = true
                    isExtraSeatFareMandatory = true
                    if (getSeatNoList?.size == passengerList.size) {
                        isMandatoryFlag = true
                        isSeatNumberMandatory = false
                    } else {
                        isMandatoryFlag = false
                    }

                    if (getExtraSeatFareList?.size == passengerList.size) {
                        isMandatoryFlag = true
                        isExtraSeatFareMandatory = false
                    } else {
                        isMandatoryFlag = false
                    }

                } else {
                    isMandatoryFlag = false
                }
            }
        }
        return isMandatoryFlag
    }

    private fun callIntent() {
        if (selectedSeatDetails.any { it.isExtraSeat } && selectedExtraSeatDetails.size == 0) {
            stopShimmerEffect()
            isCallIntentCall = true
            val intent = Intent(this, BookingPaymentOptionsActivity::class.java)
            intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
            startActivity(intent)
        } else {
            if (isBima == null || isBima == false) {
                stopShimmerEffect()
                isCallIntentCall = true
              //  val intent = Intent(this, AdditionalOfferTypesActivity::class.java)
                intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                startActivity(intent)
            } else {
                stopShimmerEffect()
                isCallIntentCall = true
                val intent = Intent(this, BookingPaymentOptionsActivity::class.java)
                intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                startActivity(intent)
            }
        }
    }

    private fun checkPassengerContactDetailsHide() {
        if ((!enableInsuranceCheckboxForBooking || !isInsuranceChecked || !insuranceMandatoryForBookings) && (privilegePassengerMobileNo == passengerFieldHide)) {
            binding.layoutMobileNo.gone()
        } else {
            binding.mobileNoCountryCode.setAdapter(
                ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    countryList
                )
            )
            binding.mobileNoCountryCode.setOnClickListener {
                binding.mobileNoCountryCode.showDropDown()
            }
        }

        if (privilegePassengerAlternateNo == passengerFieldHide) {
            binding.layoutAlternativeMobileNo.gone()
        } else {
            binding.alternativeMobileNoCountryCode.setAdapter(
                ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    countryList
                )
            )
            binding.alternativeMobileNoCountryCode.setOnClickListener {
                binding.alternativeMobileNoCountryCode.showDropDown()
            }
        }

        if (privilegePassengerEmail == passengerFieldHide) {
            binding.layoutEmailId.gone()
        }

    }

    /* private fun validatePassengerContactDetails() {

         if (privilegePassengerMobileNo == passengerFieldMandatory) {

             if (!binding.mobileNoCountryCode.text.isNullOrEmpty())
                 check = true
             else {
                 binding.layoutMobileNoCode.error = "Code"
                 check = false
             }

             if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null) {
                 if (!binding.edtMobileNumber.text.isNullOrEmpty()
                     && binding.edtMobileNumber.text.toString().length <= privilegePhoneValidationCount!!
                 ) {
                     mobileNumber = binding.edtMobileNumber.text.toString()
                     check = true

                 } else {
                     binding.layoutEdtMobileNumber.error = "Enter mobile number"
                     check = false
                 }
             }
             else{
                 mobileNumber = binding.edtMobileNumber.text.toString()
             }

         }

         if (privilegePassengerAlternateNo == passengerFieldMandatory) {
             if (!binding.alternativeMobileNoCountryCode.text.isNullOrEmpty()
             ) {
                 countryCode = binding.alternativeMobileNoCountryCode.text.toString()
                 check = true
             } else {
                 binding.layoutAlternativeMobileNoCode.error = "Code"
                 check = false
             }

             if (!binding.edtAlterNativeMobileNumber.text.isNullOrEmpty()
                 && binding.edtAlterNativeMobileNumber.text.toString().length == privilegePhoneValidationCount
             ) {
                 alternativeMobileNumber = binding.edtMobileNumber.text.toString()
                 check = true
             } else {
                 binding.layoutEdtAlternativeMobileNumber.error = "Enter Alternative Mobile Name"
                 check = false
             }
         }

         if (privilegePassengerEmail == passengerFieldMandatory) {
             if (!binding.etEmailId.text.isNullOrEmpty()
             ) {
                 email = binding.etEmailId.text.toString()
             } else {
                 binding.layoutEmailId.error = "Enter email Id"
                 binding.layoutEmailId.isErrorEnabled = true
             }
         }
     }*/

    private fun validatePassengerContactDetails() {
        var mainMobile = false
        var alternateMobile = false
        var emailCheck = false

        if (privilegePassengerMobileNo == passengerFieldMandatory) {

            if (!binding.mobileNoCountryCode.text.isNullOrEmpty()) {
                check = true
                mainMobile = true
            } else {
                binding.layoutMobileNoCode.error = "Code"
                check = false
                mainMobile = false
            }

            if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null) {
                if (!binding.edtMobileNumber.text.isNullOrEmpty()
                    && binding.edtMobileNumber.text.toString().length <= privilegePhoneValidationCount ?: 100
                ) {
                    mobileNumber = binding.edtMobileNumber.text.toString()
                    check = true
                    mainMobile = true
                } else {
                    check = false
                    mainMobile = false
                }
            } else {
                if (!binding.edtMobileNumber.text.isNullOrEmpty()
                ) {
                    mobileNumber = binding.edtMobileNumber.text.toString()
                    check = true
                    mainMobile = true
                } else {
                    check = false
                    mainMobile = false
                }
            }
        } else {
            mainMobile = true
        }

        if (privilegePassengerAlternateNo == passengerFieldMandatory) {
            if (!binding.alternativeMobileNoCountryCode.text.isNullOrEmpty()
            ) {
                countryCode = binding.alternativeMobileNoCountryCode.text.toString()
                check = true
                alternateMobile = true
            } else {
                binding.layoutAlternativeMobileNoCode.error = "Code"
                check = false
                alternateMobile = false
            }

            if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null) {
                if (!binding.edtAlterNativeMobileNumber.text.isNullOrEmpty()
                    && binding.edtAlterNativeMobileNumber.text.toString().length <= privilegePhoneValidationCount ?: 100
                ) {
                    alternativeMobileNumber = binding.edtAlterNativeMobileNumber.text.toString()
                    check = true
                    alternateMobile = true
                } else {
                    binding.layoutEdtAlternativeMobileNumber.error = "Enter Alternative Mobile Name"
                    check = false
                    alternateMobile = false
                }
            } else {
                if (!binding.edtAlterNativeMobileNumber.text.isNullOrEmpty()) {
                    alternativeMobileNumber = binding.edtAlterNativeMobileNumber.text.toString()
                    check = true
                    alternateMobile = true
                } else {
                    binding.layoutEdtAlternativeMobileNumber.error = "Enter Alternative Mobile Name"
                    check = false
                    alternateMobile = false
                }
            }

        } else {
            if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null) {
                if (!binding.edtAlterNativeMobileNumber.text.isNullOrEmpty()
                    && binding.edtAlterNativeMobileNumber.text.toString().length <= privilegePhoneValidationCount ?: 100
                ) {
                    alternativeMobileNumber = binding.edtAlterNativeMobileNumber.text.toString()
                    check = true
                    alternateMobile = true
                } else {
                    check = true
                    alternateMobile = true
                }
            } else {
                if (!binding.edtAlterNativeMobileNumber.text.isNullOrEmpty()) {
                    alternativeMobileNumber = binding.edtAlterNativeMobileNumber.text.toString()
                    check = true
                    alternateMobile = true
                } else {
                    binding.layoutEdtAlternativeMobileNumber.error = "Enter Alternative Mobile Name"
                    check = true
                    alternateMobile = true
                }
            }
        }

        if (privilegePassengerEmail == passengerFieldMandatory) {
            if (!binding.etEmailId.text.isNullOrEmpty()
            ) {
                emailCheck = true
                email = binding.etEmailId.text.toString()
            } else {
                emailCheck = false
            }
        } else {
            emailCheck = true
        }

        Timber.d("validationChach:: $emailCheck , $mainMobile, $alternateMobile $privilegePassengerMobileNo")

        if (emailCheck && mainMobile && alternateMobile) {
            goToNextActivity()
        } else if (!emailCheck) {
            binding.layoutEmailId.error = "Enter email Id"
            toast(getString(R.string.please_enter_email_id))
            binding.layoutEmailId.isErrorEnabled = true
            goToNextActivity()
        } else if (!mainMobile) {
            binding.layoutEdtMobileNumber.error = "Enter mobile number"
            toast(getString(R.string.please_enter_mobile_number))
            binding.layoutEdtMobileNumber.isErrorEnabled = true
            goToNextActivity()
        } else if (!alternateMobile) {
            binding.layoutEdtAlternativeMobileNumber.error = "Enter Alternative Mobile Name"
            toast(getString(R.string.please_enter_alternate_mobile_number))
            binding.layoutEdtAlternativeMobileNumber.isErrorEnabled = true
            goToNextActivity()
        }
    }


    private fun alternativeMobileNumberIsMandatory() {
        if (passengerFieldMandatory == alternativeMobileNumberFiledMandatory) {
            isAlterNateMobileMandatory = true

            if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null) {
                if (!alternativeMobileNumber.isNullOrEmpty()
                    && alternativeMobileNumberFiledMandatory == isMandatory
                    && alternativeMobileNumber.toString().length == privilegePhoneValidationCount ?: 100
                ) {
                    isMandatoryFlag = true
                    isAlterNateMobileMandatory = false
                } else {
                    isMandatoryFlag = false
                }
            } else {
                isAlterNateMobileMandatory =
                    passengerFieldMandatory == mobileNumberFiledMandatory && binding.edtMobileNumber.text.isNullOrEmpty()
            }

        } else {
            if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null) {
                if (!alternativeMobileNumber.isNullOrEmpty() && alternativeMobileNumber.toString().length != privilegePhoneValidationCount
                ) {
                    isAlterNateMobileMandatory = true
                    toast(getString(R.string.invalid_mobile_number))

                } else {
                    isAlterNateMobileMandatory = false
                    isMandatoryFlag = false
                }
            } else {
                isAlterNateMobileMandatory = false
                isMandatoryFlag = false
            }
        }
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            if (view.tag == getString(R.string.retrieve)) {
                val checkedPassenger = passengerHistory[position]
                if (passengerHistory[position].isChecked)
                    checkedPassengerList.add(checkedPassenger)
                else
                    checkedPassengerList.remove(checkedPassenger)
            } else if (view.tag == getString(R.string.extra_seat_fare)) {
                var totalFare = 0.0
                passengerList.forEach {
                    if (it.fare != null && it.fare!!.isNotEmpty())
                        totalFare += it.fare?.toDouble()!!
                }
                val totalNetAmount =
                    "${getString(R.string.netAmount)} : $amountCurrency$totalFare"
                binding.passengerDetailsPaymentProceed.tvNetAmt.text = totalNetAmount

            } else if (view.tag == getString(R.string.delete_passenger)) {
                //  updateSeatsInfo(position)
                deletePassengerPosition = position
                val srcDest = "$source-$destination"
                val subtitle = "${getDateDMYY(travelDate)} $deptTime | $busType"
                DialogUtils.deletePassengerDialog(
                    this,
                    getString(R.string.delete_passenger),
                    selectedSeatDetails[position].number,
                    srcDest,
                    subtitle,
                    getString(R.string.goBack),
                    getString(R.string.delete),
                    this
                )
            }
        }

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSeatsInfo(position: Int) {
        val selectedSeatDetailsUpdated =
            mutableListOf<com.bitla.mba.morningstartravels.mst.pojo.service_details.SeatDetail>()
        val seatNoUpdatedList = mutableListOf<String>()
        totalFare = 0.0
        passengerList.removeAt(position)
        selectedSeatDetails.removeAt(position)
        selectedSeatDetails.forEach {
            it.number.let { it1 -> seatNoUpdatedList.add(it1) }
            //totalFare += it.fare?.toString()!!.toDouble()
            totalFare += if (it.editFare != null && it.editFare.toString().isNotEmpty())
                it.editFare?.toString()!!.toDouble()
            else
                it.baseFareFilter?.toString()!!.toDouble()
            val seatDetails = com.bitla.mba.morningstartravels.mst.pojo.service_details.SeatDetail()
            seatDetails.age = it.age
            seatDetails.discountAmount = it.discountAmount
            seatDetails.fare = it.fare
            seatDetails.sex = it.sex
            seatDetails.name = it.name
            seatDetails.number = it.number
            selectedSeatDetailsUpdated.add(seatDetails)
        }

        setSelectedSeats(selectedSeatDetailsUpdated)
        val commaSeparatedSeatNoUpdated = TextUtils.join(",", seatNoUpdatedList)
        setSelectSeatNumber(commaSeparatedSeatNoUpdated)
        val totalNetAmount =
            "${getString(R.string.netAmount)} : $amountCurrency$totalFare"
        binding.passengerDetailsPaymentProceed.tvNetAmt.text = totalNetAmount
        if (::passengerDetailsAdapter.isInitialized)
            passengerDetailsAdapter.notifyDataSetChanged()
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onLeftButtonClick() {

    }

    override fun onRightButtonClick() {
        deletePassengerPosition?.let { updateSeatsInfo(it) }
//        deletePassengerPosition?.let { passengerDetailsAdapter.notifyItemRemoved(it) }
//        deletePassengerPosition?.let { passengerDetailsAdapter.notifyItemRangeChanged(it, passengerList.size) }
    }

    /*
    * this method to used for start Shimmer Effect
    * */
    private fun startShimmerEffect() {
        binding.apply {
            shimmerDashboard.visible()
            mainLayout.gone()
            passengerDetailsPaymentProceed.root.gone()
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
            mainLayout.visible()
            passengerDetailsPaymentProceed.root.visible()
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
            agentRequest, "", "",
            agent_account_info
        )
    }

    private fun accountObserver() {
        agentAccountInfoViewModel.agentInfo.observe(this) {
            Timber.d("fareBreakupByAli $it")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        val availableBalance = it.available_balance
                        if (availableBalance != null && availableBalance.isNotEmpty()) {

                            if (privilegeResponseModel.isAgentLogin && !privilegeResponseModel.country.equals(
                                    "india",
                                    true
                                )
                            ) {
                                binding.passengerDetailsPaymentProceed.balanceAvailableHeader.visible()
                                binding.passengerDetailsPaymentProceed.tvTotalBalance.text =
                                    availableBalance.toDouble().convert(currencyFormat)
                            }
                        }
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
                        it.result.message?.let { it1 -> toast(it1) }
                    }
                }
            } else {
                binding.passengerDetailsPaymentProceed.balanceAvailableHeader.gone()
//                toast(getString(R.string.server_error))
            }
        }
    }

    private fun showKeyboard(editText: EditText) {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onItemData(view: View, str1: String, str2: String) {
        if (view.tag != null) {
            if (view.tag == "clearCode") {
                rutApplied = false
                if (!isPerSeat) {
                    binding.discountText.visible()
                    binding.discountCouponLayout.visible()
                    val emptyArray = arrayListOf<SeatWiseFare>()
                    PreferenceUtils.putSelectedCoupon(emptyArray)
                }
                var seatList = PreferenceUtils.getSelectedCoupon()
                var finalList = arrayListOf<SeatWiseFare>()
                if (!seatList.isNullOrEmpty()) {
                    if (seatList.size > 1) {
                        seatList.forEach {
                            Timber.d("coupnClearCheck: ${it.seatNo}== $str1 ")
                            if (it.seatNo.toString() != str1) {
                                if (str2 != "") {
                                    var element = SeatWiseFare()
                                    element.seatNo = str1
                                    element.auto_discount_coupon = str2
                                    finalList.add(element)
                                }
                            }
                        }
                        PreferenceUtils.putSelectedCoupon(finalList)
                    } else {
                        val emptyArray = arrayListOf<SeatWiseFare>()
                        PreferenceUtils.putSelectedCoupon(emptyArray)
                    }
                }
//                getAllSeatList?.let {
//                    setPassengerListAdapter(
//                        passengerList,
//                        it,
//                        "",
//                        true
//                        )
//                }
            } else {
                var perSeatList: ArrayList<SeatWiseFare> = arrayListOf()

                if (seatNumberList.isNullOrEmpty()) {
                    seatNumberList.add(str2)
                    couponCodeList.add(str1)
                } else {
                    if (seatNumberList.contains(str2)) {
                        for (i in 0..seatNumberList.size.minus(1)) {
                            if (seatNumberList[i] == str2) {
                                couponCodeList[i] = str1
                            }
                        }
                    } else {
                        seatNumberList.add(str2)
                        couponCodeList.add(str1)
                    }

                }

                for (i in 0..seatNumberList.size.minus(1)) {
                    var element = SeatWiseFare()
                    element.auto_discount_coupon = couponCodeList[i]
                    element.seatNo = seatNumberList[i]
                    perSeatList.add(element)
                }

                PreferenceUtils.putSelectedCoupon(perSeatList)
            }
        }


    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
        if (!oldRutSeatNumber.contains(str1)) {
            oldRutSeatNumber.add(str1)
        }
        Timber.d("itemIdPosition: $str1, $str2, $str3, RUTseatList ${oldRutSeatNumber}")

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

    private fun getRutdetails(seatNumber: String, rutNumber: String) {
        bookingOptionViewModel.getRutDiscount(
            RutDiscountRequest(
                seatNumber,
                resId.toString(),
                sourceId,
                destinationId,
                rutNumber,
                1,
                getDateYMD(travelDate),
                loginModelPref.api_key,
                locale,
                true
            )
        )
    }

    private fun getRutObserver() {
        bookingOptionViewModel.getRutDiscount.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        validRut = true
                        var hideCoupon: String = ""
                        val rutSeatOrderList =
                            arrayListOf<com.bitla.ts.domain.pojo.rutDiscountDetails.response.PerSeatCoupon>()
                        if (!it.per_seat_coupons.isNullOrEmpty()) {
                            it.per_seat_coupons.forEach {
                                if (it.is_rut_discount && it.rut_order != null) {
                                    rutSeatOrderList.add(it)
                                    rutSeatOrderList.sortedByDescending { it.rut_order }
                                }
                            }
                            if (!rutSeatOrderList.isNullOrEmpty()) {
                                Timber.d("CheckAutodiscount: $rutApplied")
                                if (isPerSeat) {
                                    if (!rutApplied) {
                                        var element = SeatWiseFare()

//                                    if (rutSeatNumber== passengerList[0].seatNumber){
                                        hideCoupon = rutSeatOrderList[0].coupon_name
                                        var seatList = arrayListOf<SeatWiseFare>()
                                        seatList.add(element)
                                        PreferenceUtils.putSelectedCoupon(seatList)
                                        rutApplied = true
//                                    }
                                        element.seatNo = rutSeatNumber
                                        element.auto_discount_coupon =
                                            rutSeatOrderList[0].coupon_name
                                    } else {
                                        toast(getString(R.string.Rut_on_single_seat_only))
                                    }
                                } else {
                                    if (passengerList.size > 1) {
                                        toast(getString(R.string.Rut_on_single_seat_only))
                                    } else {
                                        if (!rutApplied) {
                                            binding.dicountCodeDrop.setText(rutSeatOrderList[0].coupon_name)
                                            PreferenceUtils.putString(
                                                "AutoDiscountCouponCode",
                                                binding.dicountCodeDrop.text.toString()
                                            )
                                            binding.dicountCodeDrop.isEnabled = false
                                            binding.crossClick.visible()
                                            binding.dicountCodeDrop.isFocusable = false
                                            binding.discountCouponLayout.isClickable = false
                                            hideCoupon = ""
                                            rutApplied = true
                                        }
                                    }
                                }

                            } else {
                                if (!isPerSeat) {
                                    rutApplied = false
                                    binding.dicountCodeDrop.text?.clear()
                                    PreferenceUtils.removeKey("AutoDiscountCouponCode")
                                }
                            }
                        }
                        Timber.d("CheckAutodiscount:1 $rutApplied")

                        if (isPerSeat) {
                            getAllSeatList?.let {
                                setPassengerListAdapter(
                                    passengerList,
                                    it,
                                    hideCoupon,
                                    true,
                                    true
                                )
                            }
                        } else {
                            if (rutApplied) {
                                getAllSeatList?.let {
                                    setPassengerListAdapter(
                                        passengerList,
                                        it,
                                        hideCoupon,
                                        true,
                                        true
                                    )
                                }
                            } else {
                                getAllSeatList?.let {
                                    setPassengerListAdapter(
                                        passengerList,
                                        it,
                                        hideCoupon,
                                        true,
                                        false
                                    )
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

    private fun getPrefillPassengerDetailsObserver() {
        bookingOptionViewModel.getPrefillPassenger.observe(this) {
            if (it != null) {
                val body = GetPrefillResponse()
                val jsonBody = it.asJsonObject

                val keyArray = jsonBody.keySet()

                keyArray.forEach {
                    if (it.contains("code", true)) {
                        body.code = jsonBody.get(it).toString()
                        body.rutNumber = rutNum
                    }
                    if (it.contains("passenger_age", true)) {
                        var seat = it.split("_")
                        body.passenger_seat = seat[seat.size.minus(1)]
                        body.passenger_age = jsonBody.get(it).toString()
                    }

                    if (it.contains("passenger_name", true)) {
                        val text = jsonBody.get(it).toString().replace("\"", "")
                        body.passenger_name = text
                    }
                    if (it.contains("result", true)) {
                        var result = jsonBody.get(it).asJsonObject
                        body.message = result.get("message").toString()
                    }
                }
                try {
                    when (body.code) {
                        "200" -> {

//                            if (oldRutSeatNumber.contains(body.passenger_seat)){
//                                rutApplied= false
//                            }
                            PreferenceUtils.putPrefillData(body)
                            Timber.d("refilldataCheck:: ${PreferenceUtils.getPrefillData()}")
                            if (appliedSeat == "") {
                                appliedSeat = body.passenger_seat!!
                            }
                            if (allowRutDiscount) {
                                getRutdetails(body.passenger_seat!!, body.rutNumber!!)
                            } else {
                                val hideCoupon = ""
                                getAllSeatList?.let {
                                    setPassengerListAdapter(
                                        passengerList,
                                        it,
                                        hideCoupon,
                                        true,
                                        false
                                    )
                                }
                            }


                        }

                        "401" -> {
                            /*DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            showUnauthorisedDialog()

                        }

                        "412" -> {
                            hideCoup = ""
                            val emptyArray2 = GetPrefillResponse()
                            PreferenceUtils.putPrefillData(emptyArray2)
                            validRut = true
                            // toast(getString(R.string.give_valid_rut))
                            //  toast("Please enter passenger name")
                            // val emptyArray = arrayListOf<SeatWiseFare>()
                            //PreferenceUtils.putSelectedCoupon(emptyArray)
                            binding.discountCouponLayout.isEnabled = true
                            /*binding.dicountCodeDrop.text.clear()
                            PreferenceUtils.removeKey("AutoDiscountCouponCode")*/
                            Timber.d("refilldataCheck:: ${Gson().toJson(PreferenceUtils.getPrefillData())}")
                            /*  getAllSeatList?.let {
                                  setPassengerListAdapter(
                                      passengerList,
                                      it,
                                      hideCoup,
                                      false,
                                      false
                                  )
                              }*/
                            getAllSeatList?.let {
                                setPassengerListAdapter(
                                    passengerList,
                                    it,
                                    hideCoup,
                                    true,
                                    false
                                )
                            }
                        }

                        else -> {
                            body.message?.let { it1 -> toast(it1) }
                            PreferenceUtils.removeKey("AutoDiscountCouponCode")
                            val emptyArray = arrayListOf<SeatWiseFare>()
                            PreferenceUtils.putSelectedCoupon(emptyArray)
                            val emptyArray2 = GetPrefillResponse()
                            PreferenceUtils.putPrefillData(emptyArray2)
                        }

                    }
                } catch (e: Exception) {
                    toast(e.message)
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
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
                dialog.dismiss()
            }

        }
    }

    private fun getPassengerHistoryApi() {
        if (isNetworkAvailable()) {
            passengerHistoryViewModel.passengerHistoryApi(
                loginModelPref.api_key,
                json_format,
                "${binding.mobileNoCountryCode.text}-${binding.edtMobileNumber.text.toString()}",
                operator_api_key,
                locale!!,
                ticket_details_method_name
            )
        } else {
            noNetworkToast()
        }
    }

    private fun dialogRetrievePax() {
        val builder = AlertDialog.Builder(this).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
        val binding: DialogRetrievePaxBinding = DialogRetrievePaxBinding.inflate(
            LayoutInflater.from(this)
        )

        setRetrievePaxAdapter(binding)
        builder.setCancelable(true)
        binding.btnGoBack.setOnClickListener {
            builder.cancel()
        }
        binding.btnUpdate.setOnClickListener {
            Timber.d("checkedPassengerList ${checkedPassengerList.size}")
            autoFillPassengerInfo()
            builder.cancel()
        }
        builder.setView(binding.root)
        builder.show()
    }

    private fun autoFillPassengerInfo() {
        try {
            for (i in 0..checkedPassengerList.size.minus(1)) {
                passengerList[i].sex = checkedPassengerList[i].passenger_title
                val rvPassengerView = binding.rvPassengerDetails.getChildAt(i)
                if (rvPassengerView != null) {
                    val viewName =
                        rvPassengerView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                            R.id.etName
                        )
                    val viewFirstName =
                        rvPassengerView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                            R.id.etFirstName
                        )

                    val viewAge =
                        rvPassengerView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                            R.id.etAge
                        )
                    val viewMale =
                        rvPassengerView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                            R.id.passengerDetails_btnMale
                        )
                    val viewFemale =
                        rvPassengerView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                            R.id.passengerDetails_btnFemale
                        )

                    viewName.setText(checkedPassengerList[i].name)
                    viewFirstName.setText(checkedPassengerList[i].name)
                    viewAge.setText(checkedPassengerList[i].passenger_age.toString())
                    if (checkedPassengerList[i].passenger_title.isNotEmpty()) {
                        if (checkedPassengerList[i].passenger_title.trim() == "Mr") {
                            viewMale.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    com.bitla.tscalender.R.color.slycalendar_defSelectedColor
                                )
                            )
                            viewFemale.setBackgroundResource(R.drawable.layout_rounded_shape_border_radius_2_dp_black)
                            viewFemale.setHintTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.gray
                                )
                            )
                            viewMale.setHintTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.white
                                )
                            )
                            viewMale.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.white
                                )
                            )
                            viewFemale.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.black
                                )
                            )

                        } else {
                            viewFemale.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    com.bitla.tscalender.R.color.slycalendar_defSelectedColor
                                )
                            )

                            viewMale.background = ContextCompat.getDrawable(
                                this,
                                R.drawable.layout_rounded_shape_border_radius_2_dp_black
                            )
                            viewFemale.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.white
                                )
                            )
                        }
                    }
                }
            }
            if (::passengerDetailsAdapter.isInitialized)
                passengerDetailsAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Timber.d("execeptionMsg ${e.message}")
        }
    }

    private fun setRetrievePaxAdapter(
        binding: DialogRetrievePaxBinding
    ) {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvRetrievePax.layoutManager = layoutManager
        retrievePaxAdapter =
            RetrievePaxAdapter(this, passengerHistory, passengerList, this)
        binding.rvRetrievePax.adapter = retrievePaxAdapter
    }
}