package com.bitla.ts.presentation.view.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.branch_list_method_name
import com.bitla.ts.data.city_Details_method_name
import com.bitla.ts.data.confirm_phone_block_ticket_method_name
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.data.operator_api_key
import com.bitla.ts.data.response_format
import com.bitla.ts.data.ticket_details_method_name
import com.bitla.ts.data.user_list_method_name
import com.bitla.ts.databinding.ActivityNewConfirmPhoneBlockBinding
import com.bitla.ts.databinding.SearchBpCoachLayoutReportingBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody
import com.bitla.ts.domain.pojo.photo_block_tickets.request.Ticket
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.reservation_stages.request.ReservationStagesRequest
import com.bitla.ts.domain.pojo.reservation_stages.response.StageItem
import com.bitla.ts.presentation.adapter.PassengerConfirmPhoneBookingAdapter
import com.bitla.ts.presentation.adapter.SearchBpCoachLayoutReportingAdapter
import com.bitla.ts.presentation.adapter.SearchDropDownListAdapter
import com.bitla.ts.presentation.view.fragments.PaymentOptionsFragment
import com.bitla.ts.presentation.viewModel.BlockViewModel
import com.bitla.ts.presentation.viewModel.BookingOptionViewModel
import com.bitla.ts.presentation.viewModel.CityDetailViewModel
import com.bitla.ts.presentation.viewModel.CoachLayoutReportingViewModel
import com.bitla.ts.presentation.viewModel.PaymentMethodViewModel
import com.bitla.ts.presentation.viewModel.TicketDetailsComposeViewModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.common.passengerList
import com.bitla.ts.utils.common.retrieveSelectedPassengers
import com.bitla.ts.utils.common.retrieveSelectedSeatNumber
import com.bitla.ts.utils.common.retrieveSelectedSeats
import com.bitla.ts.utils.constants.TICKET_BOOKED_FAILED
import com.bitla.ts.utils.constants.TicketBookedFailed
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gone
import isNetworkAvailable
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.lang.Double.parseDouble

class NewConfirmPhoneBookingActivity : BaseActivity(), DialogAnyClickListener {

    private var totalNetAmount: String = ""
    private var transactionFare: String = ""
    private var pnrNumber: String = ""
    private var paymentType: Int = 1
    private var lastSelectedPaymentType : Int = 1
    private var currentUserId: String = ""
    private var currentCityId: String = ""
    private var currentBranchId: String = ""
    private var isAgentLogin: Boolean = false

    private val citiesList: ArrayList<StageItem> = arrayListOf()
    private val branchList: ArrayList<StageItem> = arrayListOf()
    private val usersList: ArrayList<StageItem> = arrayListOf()
    private val genderList: ArrayList<StageItem> = arrayListOf()
    private var currentSelectedBoardingStageId: String = ""
    private var reservationId: String = ""
    private var creditDebitCardNo: String? = null
    private var selectedSubPaymentOptionName: String? = null
    private var agentPayViaVPA: String = ""
    private var branchUserPayViaVPA: String = ""
    private var branchUserPayViaPhoneNumberSMS: String = ""

    private lateinit var binding: ActivityNewConfirmPhoneBlockBinding
    private var adapter: PassengerConfirmPhoneBookingAdapter? = null
    private var privileges: PrivilegeResponseModel? = null
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var currencyFormatt: String = ""
    private val coachLayoutReportingViewModel by viewModel<CoachLayoutReportingViewModel>()
    private var searchBpCoachLayoutReportingAdapter: SearchBpCoachLayoutReportingAdapter? = null
    private val paymentMethodViewModel by viewModel<PaymentMethodViewModel>()
    private val ticketDetailsComposeViewModel by viewModel<TicketDetailsComposeViewModel<Any?>>()

    private var citiesListAdapter: SearchDropDownListAdapter? = null
    private var branchListAdapter: SearchDropDownListAdapter? = null
    private var usersListAdapter: SearchDropDownListAdapter? = null
    private var genderListAdapter: SearchDropDownListAdapter? = null
    private var sourcePopupWindow: PopupWindow? = null
    private var boardingPointList: MutableList<String> = mutableListOf()
    private val cityDetailViewModel by viewModel<CityDetailViewModel<Any?>>()
    private val bookingOptionViewModel by viewModel<BookingOptionViewModel<Any?>>()
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private var userTypeId: Int = 1
    private var agentPayViaPhoneNumberSMS: String = ""
    private var selectedSeatDetails =
        ArrayList<com.bitla.ts.domain.pojo.service_details_response.SeatDetail>()
    private var noOfSeats: String? = "0"
    private var selectedSeatNo: String? = null

    private var seatNumbers: String? = null
    private var totalFare = 0.0
    private var totalFareString = ""

    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var busType: String? = null
    private var deptTime: String? = null
    private var arrTime: String? = null
    private var deptDate: String? = null
    private var arrDate: String? = null
    private var boardingPoint: String? = null
    private var droppingPoint: String? = null
    private var droppingId: String? = ""
    private var boardingId: String? = ""
    private var isOnBehalfOfAgent = false


    private var loginModelPref = LoginModel()
    private var locale = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewConfirmPhoneBlockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        setObservers()
        setTicketDetailsV1Observer()
        getSeatDetails()
        init()


    }

    private fun getSeatDetails() {
        selectedSeatDetails.clear()
        selectedSeatDetails = retrieveSelectedSeats()
        noOfSeats = selectedSeatDetails.size.toString()
        paymentMethodViewModel.noOfSeats = noOfSeats

        if (!passengerList.any { it.isExtraSeat }) {
            selectedSeatNo = retrieveSelectedSeatNumber()
        }

    }

    private fun callTicketDetailsV1Api() {

        val locale = PreferenceUtils.getlang()
        val numeric = if (intent.hasExtra("qrscan")) {
            intent.getBooleanExtra("qrscan", false)
        } else {
            false
        }

        try {
            parseDouble(pnrNumber)
        } catch (e: NumberFormatException) {
            Timber.d(" numeric: $numeric")

//            numeric = true
        }


        ticketDetailsComposeViewModel.ticketDetailsApi(
            apiKey = loginModelPref.api_key,
            ticketNumber = pnrNumber,
            jsonFormat = true,
            isQrScan = false,
            locale = locale,
            apiType = ticket_details_method_name,
            loadPrivs = true,
            menuPrivilege = false
        )
    }

    private fun setTicketDetailsV1Observer() {
        ticketDetailsComposeViewModel.dataTicketDetails.observe(this) {
            ticketDetailsComposeViewModel.showRootProgressBar = false

            if (it != null) {
                when (it.code) {
                    200 -> {

                        if (it.body != null && it.body?.code == 419) {
                            ticketDetailsComposeViewModel.setIsTicketDetailsApiSuccess(false)
                            // failed case


                        } else {
                            try {
                                for (i in 0 until it.body?.passengerDetails!!.size) {
                                    for (j in 0 until passengerList.size){
                                        if(it.body?.passengerDetails[i]?.seatNumber.equals(passengerList[j].seatNumber)){
                                            passengerList[j].contactDetail[0].cusMobileNumber = it.body?.passengerDetails[i]?.cusMobile
                                        }
                                    }
                                }

                                if (passengerList.size > 0) {
                                    setPassengerAdapter()

                                }
                            }catch (e: Exception){
                                if(BuildConfig.DEBUG){
                                    e.printStackTrace()
                                    toast("error")
                                }
                            }


                        }
                    }

                    401 -> {
                        // openUnauthorisedDialog()
                        showUnauthorisedDialog()


                        firebaseLogEvent(
                            this,
                            TICKET_BOOKED_FAILED,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            TICKET_BOOKED_FAILED,
                            TicketBookedFailed.TICKET_BOOKED_FAILED
                        )
                        ticketDetailsComposeViewModel.setIsTicketDetailsApiSuccess(false)
                    }

                    else -> {
                        if (it.message?.isNotEmpty() == true) {
                            toast(it.message)
                            onBackPressed()
                        } else {
                            toast(getString(R.string.server_error))
                            onBackPressed()
                        }
                        firebaseLogEvent(
                            this,
                            TICKET_BOOKED_FAILED,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            TICKET_BOOKED_FAILED,
                            TICKET_BOOKED_FAILED
                        )
                        ticketDetailsComposeViewModel.setIsTicketDetailsApiSuccess(false)
                    }
                }
            } else {
                toast(getString(R.string.server_error))
                onBackPressed()
            }
        }
    }



    private fun callGetReservationStagesApi(reservationId: String) {
        val reservationStagesRequest = ReservationStagesRequest(
            reservationId,
            loginModelPref.api_key,
            operator_api_key,
            locale
        )

        coachLayoutReportingViewModel.getReservationStagesApi(reservationStagesRequest)

    }

    fun setObservers() {

        coachLayoutReportingViewModel.reservationStagesResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        binding.includeProgress.progressBar.gone()

                        //For start for the activity
                        val stageList = it.result?.stageDetails
                        if (stageList?.isNotEmpty() == true) {
                            if (!boardingId.isNullOrEmpty()) {
                                val matchingStage =
                                    stageList.find { it.stageId.toString() == boardingId }
                                if (matchingStage != null) {
                                    // If a match is found, set the values
                                    binding.boardingPointET.setText(
                                        this.getString(
                                            R.string.stage_details_text,
                                            matchingStage.stageTime,
                                            matchingStage.cityName,
                                            matchingStage.stageName
                                        )
                                    )
                                    currentSelectedBoardingStageId =
                                        matchingStage.stageId.toString()
                                    paymentMethodViewModel.selectedBoardingPoint =
                                        currentSelectedBoardingStageId

                                }else{
                                    setTheDefaultStageId(stageList)
                                }
                            } else {
                                setTheDefaultStageId(stageList)
                            }




                            binding.boardingPointET.setOnClickListener {
                                sourcePopupDialog()
                            }
                            binding.layoutBoardingPoint.setEndIconOnClickListener {
                                sourcePopupDialog()
                            }
                        }
                    }

                    401 -> showUnauthorisedDialog()
                    else -> toast(it.message)
                }
            }
        }

        cityDetailViewModel.cityDetailResponse.observe(this) {
            Timber.d("LoadingState ${it}")
            if (it != null) {
                binding.includeProgress.progressBar.gone()

                if (!it.result.isNullOrEmpty()) {
                    citiesList.clear()
                    for (i in 0..it.result.size.minus(1)) {
                        val obj = StageItem()
                        obj.stageName = it.result[i].name
                        obj.stageId = it.result[i].id
                        citiesList.add(obj)


                    }

                }
            }

        }

        blockViewModel.userList.observe(this) { it ->
            binding.includeProgress.progressBar.gone()

            try {
                Timber.d("userListResponse ${com.bitla.ts.utils.common.userList.size}")
                Timber.d("userListResponse ${it.active_users}")

                usersList.clear()
                if (it.active_users != null && it.active_users.isNotEmpty()) {
                    it.active_users.forEach {
                        val obj = StageItem()
                        obj.stageName = it.label
                        obj.stageId = it.id
                        usersList.add(obj)

                    }
                }
            } catch (t: Throwable) {
                toast(getString(R.string.an_error_occurred_while_fetching_agent_list))
            }
        }


        blockViewModel.branchList.observe(this) { it ->
            Timber.d("branchListResponse $it")
            binding.includeProgress.progressBar.gone()
            branchList.clear()
            try {
                if (it != null) {
                    if (it.branchlists != null && it.branchlists.isNotEmpty()) {
                        it.branchlists.forEach {
                            val obj = StageItem()
                            obj.stageName = it.label
                            obj.stageId = it.id
                            branchList.add(obj)
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                toast("An error occurred while fetching Branch List")
            }
        }

    }

    private fun setTheDefaultStageId(stageList: ArrayList<StageItem>) {
        binding.boardingPointET.setText(
            this.getString(
                R.string.stage_details_text,
                stageList[0].stageTime,
                stageList[0].cityName,
                stageList[0].stageName
            )
        )
        currentSelectedBoardingStageId =
            stageList[0].stageId.toString()
        paymentMethodViewModel.selectedBoardingPoint =
            currentSelectedBoardingStageId
    }

    private fun citiesPopupDialog() {
        var popupBinding = SearchBpCoachLayoutReportingBinding.inflate(LayoutInflater.from(this))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        citiesListAdapter =
            SearchDropDownListAdapter(
                this,
                citiesList
                    ?: arrayListOf(),
            ) { item ->       // handle the click listener of city inside this function
                sourcePopupWindow?.dismiss()
                binding.etTravelBranch.setText(
                    item.stageName
                )
                currentCityId = item.stageId.toString()
                paymentMethodViewModel.selectedUserCity = currentCityId
                callOnlineAgentApi()

            }
        popupBinding.searchRV.adapter = citiesListAdapter

        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                citiesListAdapter?.filter?.filter(s.toString())
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

        val displayMetrics = DisplayMetrics()
        val windowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = (screenHeight * 0.4).toInt()

        if (citiesList.size <= 5) {
            popupHeight = FrameLayout.LayoutParams.WRAP_CONTENT
        }

        sourcePopupWindow = PopupWindow(
            popupBinding.root, binding.etTravelBranch.width, popupHeight,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(binding.etTravelBranch, 0, 0, Gravity.END)
        sourcePopupWindow?.elevation = 25f

        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }

    private fun branchPopupDialog() {
        var popupBinding = SearchBpCoachLayoutReportingBinding.inflate(LayoutInflater.from(this))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        branchListAdapter =
            SearchDropDownListAdapter(
                this,
                branchList
                    ?: arrayListOf(),
            ) { item ->       // handle the click listener of city inside this function
                sourcePopupWindow?.dismiss()
                binding.etTravelBranch.setText(
                    item.stageName
                )
                currentBranchId = item.stageId.toString()
                paymentMethodViewModel.selectedTravelBranch = currentBranchId
                userTypeId = 12
                callOnlineAgentApi()

            }
        popupBinding.searchRV.adapter = branchListAdapter

        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                branchListAdapter?.filter?.filter(s.toString())
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

        val displayMetrics = DisplayMetrics()
        val windowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = (screenHeight * 0.4).toInt()

        if (branchList.size <= 5) {
            popupHeight = FrameLayout.LayoutParams.WRAP_CONTENT
        }

        sourcePopupWindow = PopupWindow(
            popupBinding.root, binding.etTravelBranch.width, popupHeight,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(binding.etTravelBranch, 0, 0, Gravity.END)
        sourcePopupWindow?.elevation = 25f

        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }

    private fun usersListPopupDialog() {
        var popupBinding = SearchBpCoachLayoutReportingBinding.inflate(LayoutInflater.from(this))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        usersListAdapter =
            SearchDropDownListAdapter(
                this,
                usersList
                    ?: arrayListOf(),
            ) { item ->       // handle the click listener of city inside this function
                sourcePopupWindow?.dismiss()
                binding.userET.setText(
                    item.stageName
                )

                currentUserId = item.stageId.toString()

                if(binding.radioUser.isChecked){
                    paymentMethodViewModel.selectedUser = currentUserId
                }else if(binding.radioOnlineAgent.isChecked){
                    paymentMethodViewModel.selectedOnlineAgentId = currentUserId
                }else{
                    paymentMethodViewModel.selectedOfflineAgentId = currentUserId
                }

            }
        popupBinding.searchRV.adapter = usersListAdapter

        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                usersListAdapter?.filter?.filter(s.toString())
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

        val displayMetrics = DisplayMetrics()
        val windowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = (screenHeight * 0.4).toInt()

        if (usersList.size <= 5) {
            popupHeight = FrameLayout.LayoutParams.WRAP_CONTENT
        }

        sourcePopupWindow = PopupWindow(
            popupBinding.root, binding.userET.width, popupHeight,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(binding.userET, 0, 0, Gravity.END)
        sourcePopupWindow?.elevation = 25f

        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }


    private fun genderPopup(editText: AutoCompleteTextView, position: Int) {
        var popupBinding = SearchBpCoachLayoutReportingBinding.inflate(LayoutInflater.from(this))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        popupBinding.searchET.gone()

        genderListAdapter =
            SearchDropDownListAdapter(
                this,
                genderList
                    ?: arrayListOf(),
            ) { item ->       // handle the click listener of city inside this function
                sourcePopupWindow?.dismiss()
                editText.setText(
                    item.stageName
                )
                if (item.stageName.equals("male", true)) {
                    passengerList[position].sex = "M"
                } else {
                    passengerList[position].sex = "F"

                }
                adapter?.notifyItemChanged(position)

            }
        popupBinding.searchRV.adapter = genderListAdapter


        val displayMetrics = DisplayMetrics()
        val windowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = 350


        sourcePopupWindow = PopupWindow(
            popupBinding.root, editText.width, popupHeight,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(editText, 0, 0, Gravity.END)
        sourcePopupWindow?.elevation = 25f

        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }

    private fun sourcePopupDialog() {
        var popupBinding = SearchBpCoachLayoutReportingBinding.inflate(LayoutInflater.from(this))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        searchBpCoachLayoutReportingAdapter =
            SearchBpCoachLayoutReportingAdapter(
                this,
                coachLayoutReportingViewModel.reservationStagesResponse.value?.result?.stageDetails
                    ?: arrayListOf(),
            ) { item ->
                sourcePopupWindow?.dismiss()
                boardingPointList.add(
                    this.getString(
                        R.string.stage_details_text, item.stageTime, item.cityName, item.stageName
                    )
                )
                binding.boardingPointET.setText(
                    this.getString(
                        R.string.stage_details_text, item.stageTime, item.cityName, item.stageName
                    )
                )
                currentSelectedBoardingStageId = item.stageId.toString()
                paymentMethodViewModel.selectedBoardingPoint = currentSelectedBoardingStageId

            }
        popupBinding.searchRV.adapter = searchBpCoachLayoutReportingAdapter

        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                searchBpCoachLayoutReportingAdapter?.filter?.filter(s.toString())
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

        val displayMetrics = DisplayMetrics()
        val windowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = (screenHeight * 0.4).toInt()

        if (boardingPointList.size <= 5) {
            popupHeight = FrameLayout.LayoutParams.WRAP_CONTENT
        }

        sourcePopupWindow = PopupWindow(
            popupBinding.root, binding.boardingPointET.width, popupHeight,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(binding.boardingPointET, 0, 0, Gravity.END)
        sourcePopupWindow?.elevation = 25f

        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }

    private fun callOnlineAgentApi() {
        if (isNetworkAvailable()) {
            blockViewModel.userListApi(
                apiKey = loginModelPref.api_key,
                cityId = currentCityId,
                userType = userTypeId.toString(),
                branchId = currentBranchId,
                locale = locale!!,
                apiType = user_list_method_name
            )
        } else
            noNetworkToast()
    }

    private fun callCityDetailsApi() {
        cityDetailViewModel.cityDetailAPI(
            loginModelPref.api_key,
            response_format,
            locale!!,
            city_Details_method_name
        )
    }

    fun createUserJson() {

        val passengerDetailsList = JsonArray()

        for (i in passengerList) {
            val passengerDetails = JsonObject().apply {
                addProperty("title", if (i.sex == "M") "Mr" else "Ms")
                addProperty("gender", i.sex ?: "")
                addProperty("seat_number", i.seatNumber ?: "")
                addProperty("name", i.name ?: "")
                addProperty("age", i.age?.toIntOrNull() ?: 0)
                addProperty("mobile", i.contactDetail[0].cusMobileNumber ?: "")
                addProperty("email", i.contactDetail[0].email ?: "")
                addProperty("fare", i.fare.toString())
            }
            passengerDetailsList.add(passengerDetails)
        }


        paymentMethodViewModel.passengerDetailsList = passengerDetailsList

        callPaymentOptionsFragmentFunction()

    }


    fun init() {

        privileges = getPrivilegeBase()

        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        paymentMethodViewModel.agentTypeId = "0"



        val arr = arrayListOf("Male", "Female")
        for (i in 0 until arr.size) {
            val obj = StageItem()
            obj.stageName = arr[i]
            genderList.add(obj)
        }


        if (intent.hasExtra(getString(R.string.pnr_number))) {
            pnrNumber = intent.getStringExtra(getString(R.string.pnr_number)).toString()
            paymentMethodViewModel.pnrNumber = pnrNumber


        }
        if (intent.hasExtra("reservationId")) {
            reservationId = intent.getLongExtra("reservationId", 0L).toString()
            paymentMethodViewModel.reservationId = reservationId
        }
        if (intent.hasExtra(getString(R.string.transaction_fare))) {
            transactionFare = intent.getStringExtra(getString(R.string.transaction_fare)).toString()
        //    paymentMethodViewModel.reservationId = reservationId
        }
        if (intent.hasExtra(getString(R.string.total_net_amount))) {
            totalNetAmount = intent.getStringExtra(getString(R.string.total_net_amount)).toString()
        //    paymentMethodViewModel.reservationId = reservationId
        }


        if (intent.hasExtra(getString(R.string.select_boarding_stage))) {
            boardingId = intent.getStringExtra(getString(R.string.select_boarding_stage)).toString()
            paymentMethodViewModel.boardingId = boardingId
        }
        if (intent.hasExtra(getString(R.string.select_dropping_stage))) {
            droppingId = intent.getStringExtra(getString(R.string.select_dropping_stage)).toString()
            paymentMethodViewModel.droppingId = droppingId

        }
        if (intent.hasExtra(getString(R.string.travel_date))) {
            travelDate = intent.getStringExtra(getString(R.string.travel_date)).toString()
            paymentMethodViewModel.travelDate = travelDate

        }
        if (intent.hasExtra(getString(R.string.source_id))) {
            sourceId = intent.getStringExtra(getString(R.string.source_id)).toString()
            paymentMethodViewModel.sourceId = sourceId
        }
        if (intent.hasExtra(getString(R.string.destination_id))) {
            destinationId = intent.getStringExtra(getString(R.string.destination_id)).toString()
            paymentMethodViewModel.destinationId = destinationId
        }
        if (intent.hasExtra(getString(R.string.origin))) {
            source = intent.getStringExtra(getString(R.string.origin)).toString()
            paymentMethodViewModel.source = source
        }
        if (intent.hasExtra(getString(R.string.destination))) {
            destination = intent.getStringExtra(getString(R.string.destination)).toString()
            paymentMethodViewModel.destination = destination
        }
        if (intent.hasExtra(getString(R.string.bus_type))) {
            busType = intent.getStringExtra(getString(R.string.bus_type)).toString()
            paymentMethodViewModel.busType = busType
        }
        if (intent.hasExtra("seatNumbers")) {
            seatNumbers = intent.getStringExtra("seatNumbers").toString()
            paymentMethodViewModel.seatNumbers = seatNumbers
        }
        if (intent.hasExtra(getString(R.string.totalAmount))) {
            val fare = intent.getStringExtra(getString(R.string.totalAmount)).toString()
            totalFareString = fare.toDouble()?.convert(currencyFormatt) ?: ""
            totalFare = fare.toDouble() ?: 0.0

            paymentMethodViewModel.totalFareString = totalFareString
            paymentMethodViewModel.totalFare = totalFare
        }
        if (intent.hasExtra("isOnBehalgOfAgent")) {
            isOnBehalfOfAgent = intent.getBooleanExtra("isOnBehalgOfAgent", false)
            paymentMethodViewModel.isOnBehalfOfAgent = isOnBehalfOfAgent
        }
        Timber.d("confirmBook: $totalFareString")


        binding.ticketNumberValueTV.setText(pnrNumber)


        binding.tvTotalFareValue.setText(totalFareString)
        binding.tvTransactionChargeValue.setText(transactionFare)
        getPassengersList()











        if (privileges != null) {
            privilegeResponseModel = privileges as PrivilegeResponseModel
            privilegeResponseModel.apply {
                currencyFormatt = getCurrencyFormat(
                    this@NewConfirmPhoneBookingActivity,
                    privilegeResponseModel.currencyFormat
                )

                updateUIVisibility(this)
            }
        }


        callGetReservationStagesApi(reservationId)


        binding.onBehalfGroup.gone()
        binding.layoutTravelBranch.gone()
        binding.layoutUser.gone()
        binding.onBehalfLabel.gone()


        binding.radioSelf.setOnClickListener {
            binding.onBehalfGroup.gone()
            binding.onBehalfLabel.gone()
            binding.layoutTravelBranch.gone()
            binding.layoutUser.gone()
            paymentMethodViewModel.agentTypeId = "0"
            currentBranchId = ""
            currentUserId = ""
            currentCityId = ""
        }

        binding.radioOnBehalf.setOnClickListener {
            binding.onBehalfLabel.visible()
            binding.layoutUser.visible()
            binding.layoutTravelBranch.visible()
            binding.onBehalfGroup.visible()

            if (privilegeResponseModel?.tsPrivileges?.isAllowPhoneBlockedTicketConfirmOnBehalfOfOnlineOfflineAgent == true) {
                binding.radioOnlineAgent.visible()
                binding.radioOfflineAgent.visible()
            } else {
                binding.radioOnlineAgent.gone()
                binding.radioOfflineAgent.gone()
            }
        }

        binding.radioOnlineAgent.setOnClickListener {
            binding.layoutTravelBranch.hint = "City"
            binding.layoutUser.hint = "Online Agent"
            binding.etTravelBranch.setText("")
            binding.userET.setText("")
            paymentMethodViewModel.agentTypeId = "2"
            currentCityId = ""
            currentUserId = ""
            currentBranchId = ""
            userTypeId = 1
            callOnlineAgentApi()
            binding.paymentGatewayLL.gone()
            lastSelectedPaymentType = paymentType
            paymentType = 1

        }

        binding.radioOfflineAgent.setOnClickListener {
            binding.layoutTravelBranch.hint = "City"
            binding.layoutUser.hint = "Offline Agent"
            paymentMethodViewModel.agentTypeId = "3"
            binding.etTravelBranch.setText("")
            binding.userET.setText("")

            currentCityId = ""
            currentUserId = ""
            currentBranchId = ""
            binding.paymentGatewayLL.gone()
            lastSelectedPaymentType = paymentType
            paymentType = 1

            userTypeId = 2
            callOnlineAgentApi()


        }

        binding.radioUser.setOnClickListener {
            binding.layoutTravelBranch.hint = "Travel branch"
            binding.layoutUser.hint = "User"
            binding.etTravelBranch.setText("")
            binding.userET.setText("")
            paymentMethodViewModel.agentTypeId = "1"

            currentCityId = ""
            currentUserId = ""
            currentBranchId = ""

            binding.paymentGatewayLL.visible()
            paymentType = lastSelectedPaymentType


            userTypeId = 12
            callOnlineAgentApi()

        }

        binding.backIV.setOnClickListener {
            onBackPressed()
        }

        binding.etTravelBranch.setOnClickListener {
            if (binding.radioUser.isChecked) {
                branchPopupDialog()
            } else {
                citiesPopupDialog()

            }
        }
        binding.layoutTravelBranch.setEndIconOnClickListener {
            if (binding.radioUser.isChecked) {
                branchPopupDialog()
            } else {
                citiesPopupDialog()

            }
        }

        binding.userET.setOnClickListener {
            usersListPopupDialog()
        }
        binding.layoutUser.setEndIconOnClickListener {
            usersListPopupDialog()

        }

        binding.btnSave.setOnClickListener {
            if(checkValidation()){
                createUserJson()

            }

        }

        callCityDetailsApi()
        callOnlineAgentApi()
        callBranchListApi()


        if (selectedSeatDetails.isNotEmpty()) {
            for (i in 0..selectedSeatDetails.size.minus(1)) {
                val seatDetails = selectedSeatDetails[i]

                if (passengerList.isNotEmpty() && passengerList.size == selectedSeatDetails.size) {
                    seatDetails.additionalFare = passengerList[i].additionalFare?.toDouble()
                    if (!passengerList[i].discountAmount.isNullOrEmpty())
                        seatDetails.discountAmount = passengerList[i].discountAmount?.toDouble()
                    seatDetails.passportIssuedDate = passengerList[i].passportIssuedDate
                    seatDetails.passportExpiryDate = passengerList[i].passportExpiryDate
                    seatDetails.placeOfIssue = passengerList[i].placeOfIssue
                    seatDetails.nationality = passengerList[i].nationality
                    seatDetails.idCardNumber = passengerList[i].idCardNumber
                    if (!passengerList[i].idCardType.isNullOrEmpty()) {
                        seatDetails.idCardType = passengerList[i].idCardType?.toInt() ?: 0
                    }
                    seatDetails.isPrimary = passengerList[i].isPrimary
                    seatDetails.age = passengerList[i].age
                    seatDetails.name = passengerList[i].name
                    seatDetails.sex = passengerList[i].sex
                }
            }
        }


    }

    private fun checkValidation(): Boolean {
        if (binding.radioOnBehalf.isChecked) {
            when {
                binding.radioUser.isChecked -> {
                    if (currentUserId.isEmpty()) {
                        toast(getString(R.string.please_select_branch_and_user))
                        return false
                    }
                }
                binding.radioOnlineAgent.isChecked -> {
                    if (currentUserId.isEmpty()) {
                        toast(getString(R.string.please_select_city_and_online_agent))
                        return false
                    }
                }
                binding.radioOfflineAgent.isChecked -> {
                    if (currentUserId.isEmpty()) {
                        toast(getString(R.string.please_select_city_and_offline_agent))
                        return false
                    }
                }
            }
        }
        return true
    }




    private fun getPassengersList() {
        passengerList = retrieveSelectedPassengers()
        val bookExtraSeatNoList = mutableListOf<String>()
        val seatNoUpdatedList = mutableListOf<String>()



        passengerList.forEach {
//            Timber.d("selectedSeatNoPassengerList111 - ${it.fare}")
//            totalFare += it.fare.toString()
            Timber.d("selectedSeatNoPassengerList ${it.seatNumber}")

            selectedSeatNo = it.seatNumber
            seatNoUpdatedList.add(selectedSeatNo.toString())
            val commaSeparatedSeatNoUpdated = android.text.TextUtils.join(",", seatNoUpdatedList)
            selectedSeatNo = commaSeparatedSeatNoUpdated
        }

        if (passengerList.any { it.isExtraSeat }) {
            val commaSeparatedExtraSeats = android.text.TextUtils.join(",", bookExtraSeatNoList)
            selectedSeatNo = commaSeparatedExtraSeats
            for (i in 0..passengerList.size.minus(1)) {
                selectedSeatDetails[i].fare = passengerList[i].fare
                selectedSeatDetails[i].number = passengerList[i].seatNumber ?: ""
            }
        }
//        selectedSeatDetails.forEach {
//            it.additionalFare = passengerList[0].additionalFare?.toDouble()
//        }

        if (passengerList.isNotEmpty()
        ) {
            val mealCouponList = mutableListOf<String>()
            var mealCoupons = ""

            val mealTypeList = mutableListOf<String>()
            var mealTypes = ""

            passengerList.forEach {
                if (it.meal_coupons != null && it.meal_coupons.isNotEmpty()) {
                    mealCoupons += it.meal_coupons.toString().replace("[", "").replace("]", "")
                        .replace(",", "\n").replace(" ", "")
                    mealCouponList.add(mealCoupons)
                    mealCoupons = ""
                }
                if (!it.selectedMealType.isNullOrEmpty()) {
                    mealTypes += it.selectedMealType
                    mealTypeList.add(mealTypes)
                    mealTypes = ""
                }
            }
            if (mealCouponList.isNotEmpty()) {
                //  binding.cardMealCoupons.visible()
                //   setMealsAdapter(mealCouponList)
            }
            if (mealTypeList.isNotEmpty()) {
                //    binding.cardMealTypes.visible()
                //   setMealTypeAdapter(mealTypeList)
            }
        } else {
            //   binding.cardMealCoupons.gone()
            //   binding.cardMealTypes.gone()
        }

        callTicketDetailsV1Api()


//        Timber.d("passengerList- $passengerList")
    }


    private fun confirmPhoneBlockTicketApi() {
        binding.includeProgress.progressBar.visible()

        if (isNetworkAvailable()) {
            var reqBody: ReqBody? = null
            reqBody = ReqBody(
                apiKey = loginModelPref.api_key,
                paymentType = if (privilegeResponseModel.allowUpiForDirectPgBookingForAgents && paymentType == 1) 1
                else if (!privilegeResponseModel.allowUpiForDirectPgBookingForAgents) paymentType
                else 0,
                pnrNumber = pnrNumber,
                ticket = Ticket(creditDebitCardNo.toString()),
                travelBranch = "",
                userId = "",
                locale = locale,
                agentPaymentType = if (paymentType == 1) "" else if (isAgentLogin) "$paymentType" else "",
                agentSubPaymentType = if (paymentType == 1) "" else if (isAgentLogin) "$selectedSubPaymentOptionName" else "",
                agentPhone = agentPayViaPhoneNumberSMS,
                agentVpa = agentPayViaVPA,
                subPaymentType = if (!isAgentLogin) selectedSubPaymentOptionName else "",
                branchVpa = branchUserPayViaVPA,
                branchPhone = branchUserPayViaPhoneNumberSMS

            )

            bookingOptionViewModel.confirmPhoneBlockTicketApi(
                confirmPhoneBlockTicketReq = reqBody,
                apiType = confirm_phone_block_ticket_method_name
            )
        } else
            noNetworkToast()
    }

    private fun callBranchListApi() {
        if (isNetworkAvailable()) {

            blockViewModel.branchListApi(
                apiKey = loginModelPref.api_key,
                locale = locale.toString(),
                apiType = branch_list_method_name
            )
        } else
            noNetworkToast()
    }


    private fun updateUIVisibility(privilegeResponseModel: PrivilegeResponseModel) {
        val isAllowPassengerUpdate =
            privilegeResponseModel.tsPrivileges?.isAllowPassengerDetailsUpdateWhileConfirmingPhoneBlockSeats == true
        val isAllowOnlineOfflineAgent =
            privilegeResponseModel.tsPrivileges?.isAllowPhoneBlockedTicketConfirmOnBehalfOfOnlineOfflineAgent == true
        val isConfirmOnbehalfOfPendingTicket =
            privilegeResponseModel?.isConfirmOnbehalfOfPendingTicket == true

        // Common elements that are always visible
        binding.apply {
            confirmByLabel.setVisibility(isConfirmOnbehalfOfPendingTicket)
            confirmByGroup.setVisibility(isConfirmOnbehalfOfPendingTicket)
            onBehalfGroup.setVisibility(isConfirmOnbehalfOfPendingTicket)
            onBehalfLabel.setVisibility(isConfirmOnbehalfOfPendingTicket)
            radioOnBehalf.setVisibility(isConfirmOnbehalfOfPendingTicket)

            // Update passenger visibility
            passengerRV.setVisibility(isAllowPassengerUpdate)

            if(isAllowPassengerUpdate){
                binding.tvTotalFareLabel.gone()
                binding.tvTotalFareValue.gone()
                binding.tvTransactionChargeValue.gone()
                binding.tvTransactionChargeLabel.gone()
            }else{
                binding.tvTotalFareLabel.visible()
                binding.tvTotalFareValue.visible()
                binding.tvTransactionChargeValue.visible()
                binding.tvTransactionChargeLabel.visible()
            }

            // Update boarding point visibility
            val showBoardingPoint = isAllowPassengerUpdate &&
                    (!isConfirmOnbehalfOfPendingTicket || isConfirmOnbehalfOfPendingTicket)
            selectBoardingPointTV.setVisibility(showBoardingPoint)
            layoutBoardingPoint.setVisibility(showBoardingPoint)

            // Update agent radio buttons
            val showAgentOptions = isConfirmOnbehalfOfPendingTicket && isAllowOnlineOfflineAgent
            radioOnlineAgent.setVisibility(showAgentOptions)
            radioOfflineAgent.setVisibility(showAgentOptions)

            // Update layout visibility
            layoutUser.setVisibility(isConfirmOnbehalfOfPendingTicket)
            layoutTravelBranch.setVisibility(isConfirmOnbehalfOfPendingTicket)
        }
    }

    // Extension function to make visibility changes more readable
    private fun View.setVisibility(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }


    fun showProgressBar() {
        binding.includeProgress.progressBar.visible()
    }

    fun hideProgressBar() {
        binding.includeProgress.progressBar.gone()

    }

    override fun initUI() {


    }

    private fun setPassengerAdapter() {

        adapter = PassengerConfirmPhoneBookingAdapter(this, passengerList, this)
        binding.passengerRV.adapter = adapter

    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
        if (type == 1) {
            genderPopup(view as AutoCompleteTextView, position)
        }
    }

    override fun onAnyClickListenerWithExtraParam(
        type: Int,
        view: Any,
        list: Any,
        position: Int,
        outPos: Int
    ) {

    }

    private fun callPaymentOptionsFragmentFunction() {
        // Get the fragment by its tag or ID
        val fragment =
            supportFragmentManager.findFragmentById(R.id.paymentMethodFragment) as? PaymentOptionsFragment
        // Ensure the fragment is not null and is added
        if (fragment != null && fragment.isAdded) {
            fragment.callBookingRequest() // Call the function
        } else {
            toast(getString(R.string.error_occured))
        }
    }


}