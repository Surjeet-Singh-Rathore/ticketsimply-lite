package com.bitla.ts.presentation.view.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.booking_summary_method_name
import com.bitla.ts.data.collection_summary_method_name
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.released_summary_method_name
import com.bitla.ts.data.response_format
import com.bitla.ts.data.service_summary_method_name
import com.bitla.ts.databinding.LayoutServiceSummaryViewBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking.SeatLegends
import com.bitla.ts.domain.pojo.booking.SummaryInfoData
import com.bitla.ts.domain.pojo.booking_summary.Booking
import com.bitla.ts.domain.pojo.booking_summary.request.BookingSummaryRequest
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.released_summary.ReleaseTicket
import com.bitla.ts.domain.pojo.service_summary.Agent
import com.bitla.ts.domain.pojo.service_summary.BoardingFrom
import com.bitla.ts.domain.pojo.service_summary.DropOff
import com.bitla.ts.domain.pojo.service_summary.Multistation
import com.bitla.ts.domain.pojo.service_summary.request.ServiceSummaryRequest
import com.bitla.ts.presentation.adapter.SeatLegendsAdapter
import com.bitla.ts.presentation.adapter.ServiceSummaryAdapter1
import com.bitla.ts.presentation.view.activity.reservationOption.CurrentLocationActivity
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.common.getDateDMYY
import com.bitla.ts.utils.constants.INDIA
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_AVAILABLE_ROUTES
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getBccId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLogin
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible

class ServiceSummaryActivity : BaseActivity(), DialogSingleButtonListener {

    private var currency: String = ""
    private var currencyFormat: String = ""
    private lateinit var seatLegendsList: MutableList<SeatLegends>
    private var serviceInfoDataList = mutableListOf<SummaryInfoData>()
    private lateinit var seatLegendsAdapter: SeatLegendsAdapter

    lateinit var context: Context
    private var loginModelPref: LoginModel = LoginModel()
    private lateinit var serviceSummaryAdapter: ServiceSummaryAdapter1
    var reservationId: Long = 0

    private var bccId: Int? = 0
    private var serviceType: String? = null
    private lateinit var binding: LayoutServiceSummaryViewBinding
    private var multiStationList = mutableListOf<Multistation>()
    private var boardingList = mutableListOf<BoardingFrom>()
    private var dropOffList = mutableListOf<DropOff>()
    private var agentList = mutableListOf<Agent>()
    private var bookingList = mutableListOf<Booking>()
    private var collectionBookingList =
        mutableListOf<com.bitla.ts.domain.pojo.collection_summary.Booking>()
    private var releasedTicketsList = mutableListOf<ReleaseTicket>()
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var isAmount = false
    private var destination: String? = ""
    private var travelDate: String = ""
    private var busType: String? = null
    private var deptTime: String? = null
    private var IsIndia= false
    private var serviceNumber: String = ""
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var locale: String? = ""
    private var isOnlineInit = false

    private lateinit var networkErrorWithDisableAllViews: ViewGroup
    private lateinit var networkBackOnline: ViewGroup
    private var country: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@ServiceSummaryActivity

        getPref()
        setToolbarTitle()
        observerRelease()

        // setNetworkConnectionObserver


        binding.includeHeader.tvScan.gone()
        binding.includeHeader.layoutLegendLocation.visible()
        binding.includeHeader.imgLegend.gone()
        //binding.includeHeader.tvCurrentHeader.text = getString(R.string.branch_booking)
        binding.includeHeader.currentLocation.setOnClickListener {
            val intent = Intent(this, CurrentLocationActivity::class.java)
            startActivity(intent)
        }
        if (intent.hasExtra("TYPE")) {
            serviceType = intent.getStringExtra("TYPE")
            api(serviceType!!)
        }
        binding.includeHeader.imgBack.setOnClickListener { onBackPressed() }

        if (getPrivilegeBase()!= null) {
            val privilegeResponseModel: PrivilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel

            if (!privilegeResponseModel.country.isNullOrEmpty()
                && privilegeResponseModel.country.equals(INDIA, true)) {
                binding.textNumberOfSeats.visible()
            } else {
                binding.textNumberOfSeats.gone()
            }
        }

        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }

    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    fun api(serviceType: String) {
        if (serviceType != null) {
            when (serviceType) {
                "Multistation Booking" -> {
                    binding.includeHeader.toolbarHeaderText.text =
                        getString(R.string.multistation_booking)
                    binding.textName.text = getString(R.string.multistation)
                    binding.textNumberOfSeats.text = getString(R.string.amount)
                    binding.textNumber.text = getString(R.string.seats)
                    inCaseOfIndia()
                    callServiceSummaryApi()

                }
                "Boarding Points" -> {
                    binding.includeHeader.toolbarHeaderText.text =
                        getString(R.string.boarding_points)
                    binding.textName.text = getString(R.string.boarding_from)
                    binding.textNumberOfSeats.text = getString(R.string.amount)
                    binding.textNumber.text = getString(R.string.seats)
                    inCaseOfIndia()
                    callServiceSummaryApi()


                }
                "Dropping Points" -> {
                    binding.includeHeader.toolbarHeaderText.text =
                        getString(R.string.dropping_points)
                    binding.textName.text = getString(R.string.dropping_points)
                    binding.textNumberOfSeats.text = getString(R.string.amount)
                    binding.textNumber.text = getString(R.string.seats)
                    inCaseOfIndia()
                    callServiceSummaryApi()

                }
                "Booked By" -> {
                    binding.includeHeader.toolbarHeaderText.text = getString(R.string.booked_by)
                    binding.textName.text = getString(R.string.agent_name)
                    binding.textNumberOfSeats.text = getString(R.string.amount)
                    binding.textNumber.text = getString(R.string.seats)
                    inCaseOfIndia()
                    callServiceSummaryApi()
                }

                "Bookings" -> {
                    binding.includeHeader.toolbarHeaderText.text = getString(R.string.bookings)
                    binding.textName.text = getString(R.string.ticket_no)
                    binding.textNumberOfSeats.text = getString(R.string.amount)
                    binding.textNumber.text = getString(R.string.seats)
                    inCaseOfIndia()

                    callBookingSummaryApi()
                }
                "Collection" -> {
                    binding.includeHeader.toolbarHeaderText.text = getString(R.string.collection)
                    binding.textName.text = getString(R.string.ticket_no)
                    binding.textNumberOfSeats.visible()
                    binding.textNumberOfSeats.text = getString(R.string.amount)
                    binding.textNumber.text = getString(R.string.seats)
                    isAmount = true
                    callCollectionSummaryApi()
                }
                "Released Tickets" -> {
                    binding.includeHeader.toolbarHeaderText.text =
                        getString(R.string.released_tickets)
                    binding.textName.text = getString(R.string.ticket_no)
                    binding.textNumberOfSeats.text = getString(R.string.status)
                    binding.textNumber.text = getString(R.string.seats)
                    binding.textNumberOfSeats.gone()
                    callReleasedSummaryApi()
                }

            }
        }
    }

    fun amountVisible(visible: Boolean){
        if (visible){
            isAmount= true
            binding.textNumberOfSeats.visible()
            binding.textNumberOfSeats.text = getString(R.string.amount)

        }else{
            isAmount= false
            binding.textNumberOfSeats.text=""

        }
    }
    private fun inCaseOfIndia(){
        Timber.d("CountryCheck: $IsIndia")
        if (privilegeResponseModel.country.equals("India", true)){
            isAmount= true
            binding.textNumberOfSeats.visible()
        }else{
            isAmount= false
        }
    }

    private fun noResultVisibility(List: MutableList<SummaryInfoData>) {
        if (List.size == 0) {
            binding.shimmerLayout.gone()
            binding.NoResult.visible()
            binding.layoutTitle.gone()
            binding.shimmerLayout.gone()
            binding.layoutTotal.gone()
        } else {
            binding.layoutTitle.visible()
            binding.NoResult.gone()
            binding.shimmerLayout.gone()
            if(privilegeResponseModel.country.equals("India",true)) {
                binding.layoutTotal.visible()
            }
            setAdapter()
        }
    }

    private fun getPref() {
        bccId = getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = getLogin()
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()
        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            reservationId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!

        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            deptTime = result?.dep_time ?: getString(R.string.dash)
            busType = result?.bus_type ?: getString(R.string.empty)
            serviceNumber = result?.number ?: getString(R.string.empty)
        }

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
            currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(this, privilegeResponseModel.currencyFormat)
            IsIndia= privilegeResponseModel.country.equals("India", true)
        }
    }

    override fun initUI() {
        binding = LayoutServiceSummaryViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
    }

    private fun setToolbarTitle() {
        val subtitle = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${getDateDMYY(travelDate)} $deptTime | $busType"
        else
            "${getDateDMYY(travelDate)} $deptTime | $busType"
        binding.includeHeader.toolbarSubtitle.text = subtitle
    }

    fun setAdapter() {
        binding.recyclerServiceView.visible()
        binding.recyclerServiceView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        serviceSummaryAdapter = ServiceSummaryAdapter1(
            this,
            seatOnClick = {heading , position->
                if (serviceInfoDataList.isNotEmpty()) {
                    /*val ticketNo =
                        "${getString(R.string.ticket_no)}"*/
                    val ticketNo = getString(R.string.seats)
                    val seatNo = serviceInfoDataList[position].seatNumber
                    val seatNoList = serviceInfoDataList[position].seatNumber.split(",")
                    val totalSeats = seatNoList.size
                    val releasedBy = serviceInfoDataList[position].released_by
                    val bookedSeat = serviceInfoDataList[position].numberOfSeats
                    val srcDest = heading
                    val dateBusType = "${getDateDMYY(travelDate)} | $deptTime | $busType"
                    DialogUtils.showServiceSummaryDialog(
                        ticketNo,
                        heading,
                        dateBusType,
                        totalSeats.toString(),
                        seatNo,
                        releasedBy.toString(),
                        this
                    )
                }
//                toast(heading)
            },
            serviceInfoDataList,
            isAmount,
            currency?:"",
            currencyFormat
        )
        binding.recyclerServiceView.adapter = serviceSummaryAdapter

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.toolbar_image_left -> {
                onBackPressed()
            }
        }
    }





    private fun callBookingSummaryApi() {
        if (this.isNetworkAvailable()) {
            val reqBody = com.bitla.ts.domain.pojo.booking_summary.request.ReqBody(
                loginModelPref.api_key,
                reservationId.toString(),
                response_format,
                locale = locale
            )
            val bookingSummaryRequest = BookingSummaryRequest(
                bccId.toString(), format_type,
                booking_summary_method_name, reqBody
            )


            sharedViewModel.bookingSummaryApi(
                loginModelPref.api_key,
                reservationId.toString(),
                response_format,
                locale!!,
                booking_summary_method_name
            )

            Timber.d("bookingSummaryRequest $bookingSummaryRequest")
        } else this.noNetworkToast()
    }

    private fun callCollectionSummaryApi() {
        if (this.isNetworkAvailable()) {

            val reqBody = com.bitla.ts.domain.pojo.booking_summary.request.ReqBody(
                loginModelPref.api_key,
                reservationId.toString(),
                response_format,
                locale = locale
            )
            val bookingSummaryRequest = BookingSummaryRequest(
                bccId.toString(), format_type,
                collection_summary_method_name, reqBody
            )

           /* sharedViewModel.collectionSummaryApi(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                bookingSummaryRequest,
                collection_summary_method_name
            )
            */
            sharedViewModel.collectionSummaryApi(
                loginModelPref.api_key,
                reservationId.toString(),
                response_format,
                locale!!,
                collection_summary_method_name
            )



            Timber.d("collectionSummaryRequest $bookingSummaryRequest")
        } else this.noNetworkToast()
    }

    private fun callReleasedSummaryApi() {
        if (this.isNetworkAvailable()) {

            Timber.d("loginprefcheck ${loginModelPref.api_key}, ${loginModelPref.auth_token}")

            val reqBody = com.bitla.ts.domain.pojo.booking_summary.request.ReqBody(
                loginModelPref.api_key,
                reservationId.toString(),
                response_format,
                locale = locale
            )
            val bookingSummaryRequest = BookingSummaryRequest(
                bccId.toString(), format_type,
                released_summary_method_name, reqBody
            )

            sharedViewModel.releasedSummaryApi(
                apiKey = loginModelPref.api_key,
                reservationId = reservationId.toString(),
                responseFormat = response_format,
                locale = locale!!,
                apiType = released_summary_method_name
            )
            Timber.d("releasedSummaryRequest $bookingSummaryRequest")
        } else this.noNetworkToast()
    }

    private fun callServiceSummaryApi() {
        val reqBody = com.bitla.ts.domain.pojo.service_summary.request.ReqBody(
            loginModelPref.api_key,
            reservationId.toString(),
            response_format,
            locale = locale
        )

        val serviceSummaryRequest = ServiceSummaryRequest(
            bccId.toString(), format_type,
            service_summary_method_name, reqBody
        )

        if (isNetworkAvailable()) {
            sharedViewModel.serviceSummaryApi(
                apiKey = loginModelPref.api_key,
                locale = locale ?: "en",
                reservationId = reservationId.toString(),
                reservationFormat = true,
                apiType = service_summary_method_name
            )

            Timber.d("serviceSummaryRequest $serviceSummaryRequest")
        } else {
            noNetworkToast()
        }
    }


    private fun observerRelease() {

        sharedViewModel.bookingSummary.observe(this) {
            if (it != null) {
                Timber.d("responseBodyBookingSummary $it")

                when (it.code) {
                    200 -> {
                        bookingList = it.result.booking
                        val totalAmount = if (it.result.total_amount?.isNotEmpty() == true) (it.result.total_amount.toDouble()).convert(
                            currencyFormat
                        ) else it.result.total_amount

                        binding.totalAmount.text = "${privilegeResponseModel.currency}$totalAmount"
                        //binding.totalAmount.text = it.result.total_amount ?: ""
                        binding.totalSeats.text = it.result.total_seats.toString()

                        bookingList.forEach {
                            val summaryInfoData = SummaryInfoData(
                                it.ticket_number,
                                getString(R.string.dash),
                                it.seats,
                                amount = it.total_booked_amount?:""
                            )
                            serviceInfoDataList.add(summaryInfoData)
                        }
                        noResultVisibility(serviceInfoDataList)

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
                        if (it.message!=null){
                            toast(it.message)
                            noResultVisibility(mutableListOf())

                        }
                    }
                }

            } else {
                toast(getString(R.string.server_error))
            }
        }

        sharedViewModel.releasedSummary.observe(this) {
            try {
                if (it != null) {
                    Timber.d("responseBodyReleasedSummary $it")
                    if (it.code == 200) {
                        releasedTicketsList = it.release_tickets
                        releasedTicketsList.forEach {
                            if (!it.ticket_number.isNullOrEmpty()) {
                                val summaryInfoData =
                                    SummaryInfoData(
                                        it.ticket_number,
                                        getString(R.string.dash),
                                        it.seats,
                                        "",
                                        it.released_by.toString()
                                    )

                                serviceInfoDataList.add(summaryInfoData)
                            }
                        }

                        binding.totalAmount.gone()
                        binding.totalSeats.text = it.release_details.total_released.toString()

                        noResultVisibility(serviceInfoDataList)
                    } else
                        it.result.message?.let { it1 -> Timber.d(it1) }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (e: Exception) {
                toast(getString(R.string.server_error))
            }
        }

        sharedViewModel.collectionSummary.observe(this, Observer {
//            binding.coachProgressBar.gone()
//            callReleasedSummaryApi()
            try {
                if (it != null) {
                    Timber.d("responseBodyCollectionSummary $it")
                    if (it.code == 200) {
                        collectionBookingList = it.booking
                        var amount = "0"
                        val totalAmount = if (it.total_amount?.isNotEmpty() == true) (it.total_amount.toDouble()).convert(
                            currencyFormat
                        ) else it.total_amount

                        binding.totalAmount.text = "${privilegeResponseModel.currency}$totalAmount"
                        //binding.totalAmount.text = it.total_amount ?: ""
                        binding.totalSeats.text = it.total_seats.toString()

                        collectionBookingList.forEach {
                            if (!it.amount.isNullOrEmpty()) {
                                amount = it.amount
                            }
                            val summaryInfoData = SummaryInfoData(
                                it.ticket_number,
                                getString(R.string.dash),
                                it.seats,
                                amount,

                                )
                            serviceInfoDataList.add(summaryInfoData)
                        }
                        noResultVisibility(serviceInfoDataList)

                    } else if (it.code == 500) {
                        serviceInfoDataList.clear()
                        noResultVisibility(serviceInfoDataList)
                        try {
                            toast(it.resut.message)
                        } catch (e: Exception) {
                            toast(getString(R.string.server_error))
                        }
                    } else
                        serviceInfoDataList.clear()
                    noResultVisibility(serviceInfoDataList)
                    if (it.message != null)
                        toast(it.message)
                } else {
                    serviceInfoDataList.clear()
                    noResultVisibility(serviceInfoDataList)
                    toast(getString(R.string.server_error))
                }
            } catch (e: Exception) {
                toast(getString(R.string.server_error))
            }
        })

        sharedViewModel.serviceSummary.observe(this, Observer {
            Timber.d("responseBodyServiceSummary $it")
            if (it != null) {
                if (it.code == 200) {
                    when (serviceType) {
                        "Multistation Booking" -> {
                            multiStationList = it.result?.multistation!!
                            multiStationList.forEach {
                                val summaryInfoData = SummaryInfoData(
                                    it.multistation,
                                    it.total_bookings.toString(),
                                    it.seats,
                                    amount = it.total_booked_amount?:""
                                )
                                if(it.total_booked_amount.isNullOrEmpty()){
                                    amountVisible(false)
                                }else{
                                    amountVisible(true)
                                }
                                serviceInfoDataList.add(summaryInfoData)
                            }

                            var totalAmount = 0.0
                            var totalSeats = 0
                            it.result.multistation.forEach {
                                totalAmount += it.total_booked_amount?.toDouble() ?: 0.0

                                if(it.seats.contains(",")) {
                                    totalSeats+= it.seats.split(",").size
                                } else {
                                    totalSeats++
                                }
                            }

                            binding.totalAmount.text = "${privilegeResponseModel.currency}${totalAmount.convert(
                                currencyFormat
                            )}"
                            //binding.totalAmount.text = totalAmount.toString()
                            binding.totalSeats.text = totalSeats.toString()

                        }
                        "Boarding Points" -> {
                            boardingList = it.result?.boarding_from!!
                            boardingList.forEach {
                                val summaryInfoData = SummaryInfoData(
                                    it.boarding_at,
                                    it.total_bookings.toString(),
                                    it.seats,
                                    amount = it.total_booked_amount?:""
                                )
                                if(it.total_booked_amount.isNullOrEmpty()){
                                    amountVisible(false)
                                }else{
                                    amountVisible(true)
                                }

                                serviceInfoDataList.add(summaryInfoData)
                            }

                            var totalAmount = 0.0
                            var totalSeats = 0

                            it.result.boarding_from.forEach {
                                totalAmount += it.total_booked_amount?.toDouble() ?: 0.0
                                if(it.seats.contains(",")) {
                                    totalSeats+= it.seats.split(",").size
                                } else {
                                    totalSeats++
                                }
                            }

                            binding.totalAmount.text = "${privilegeResponseModel.currency}${totalAmount.convert(
                                currencyFormat
                            )}"
                            binding.totalSeats.text = totalSeats.toString()

                        }
                        "Dropping Points" -> {
                            dropOffList = it.result!!.drop_off
                            dropOffList.forEach {
                                val summaryInfoData =
                                    SummaryInfoData(
                                        it.drop_off,
                                        it.total_bookings.toString(),
                                        it.seats,
                                        amount = it.total_booked_amount?:""
                                    )
                                if(it.total_booked_amount.isNullOrEmpty()){
                                    amountVisible(false)
                                }else{
                                    amountVisible(true)
                                }
                                serviceInfoDataList.add(summaryInfoData)
                            }

                            var totalAmount = 0.0
                            var totalSeats = 0

                            it.result.drop_off.forEach {
                                totalAmount += it.total_booked_amount?.toDouble() ?: 0.0
                                if(it.seats.contains(",")) {
                                    totalSeats+= it.seats.split(",").size
                                } else {
                                    totalSeats++
                                }
                            }

                            binding.totalAmount.text = "${privilegeResponseModel.currency}${totalAmount.convert(
                                currencyFormat
                            )}"
                            binding.totalSeats.text = totalSeats.toString()

                        }
                        "Booked By" -> {
                            agentList = it.result?.agent!!
                            agentList.forEach {
                                val summaryInfoData =
                                    SummaryInfoData(
                                        it.agent,
                                        it.total_bookings.toString(),
                                        it.seats,
                                        amount = it.total_booked_amount?:""
                                    )
                                if(it.total_booked_amount.isNullOrEmpty()){
                                    amountVisible(false)
                                }else{
                                    amountVisible(true)
                                }
                                serviceInfoDataList.add(summaryInfoData)
                            }

                            var totalAmount = 0.0
                            var totalSeats = 0
                            
                            it.result.agent.forEach {
                                totalAmount += it.total_booked_amount?.toDouble() ?: 0.0
                                if(it.seats.contains(",")) {
                                    totalSeats+= it.seats.split(",").size
                                } else {
                                    totalSeats++
                                }
                            }

                            binding.totalAmount.text = "${privilegeResponseModel.currency}${totalAmount.convert(
                                currencyFormat
                            )}"
                            binding.totalSeats.text = totalSeats.toString()

                        }
                    }
                    noResultVisibility(serviceInfoDataList)

                } else
                    serviceInfoDataList.clear()
                noResultVisibility(serviceInfoDataList)
                it.message?.let { it1 -> Timber.d( it1) }
            } else {
                toast(getString(R.string.server_error))
            }
        })


    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}


