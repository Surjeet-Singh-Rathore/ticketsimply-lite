package com.bitla.ts.presentation.view.fragments

import android.content.*
import android.content.res.*
import android.graphics.*
import android.os.*
import android.text.*
import android.text.style.*
import android.util.*
import android.view.*
import android.widget.*
import androidx.core.view.*
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.move_to_extra_seat.request.*
import com.bitla.ts.domain.pojo.move_to_normal_seats.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments.SeatWiseFragment
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import kotlin.collections.ArrayList
import java.util.Locale
import androidx.lifecycle.lifecycleScope

class AllCoachFragment : BaseUpdateCancelTicket(), NewCoachActivity.CoachDataInterface,
    NewCoachActivity.coachDialogData,
    View.OnClickListener, DialogSingleButtonListener, OnItemCheckedListener {
    private var isOpenTooltip: Boolean = true
    lateinit var binding: FragmentAllCoachBinding
    private var shiftPassengerCount: Int = 0
    private var isFromShiftPassenger: Boolean = false
    private var currency: String = ""
    //    private var privilegeDetails: PrivilegeResponseModel? = null
    private var currentCountry: String = ""
    private var currencyFormat: String = ""
    private var loginModelPref: LoginModel = LoginModel()
    private val moveToExtraSeatViewModel by viewModel<MoveToExtraSeatViewModel<Any?>>()
    private val moveQuotaBlockSeatViewModel by viewModel<MoveQuotaBlockSeatViewModel<Any?>>()
    private val shiftPassengerViewModel by viewModel<ShiftPassengerViewModel<Any?>>()
    private var bccId: String = ""
    var seatprice: Any? = 0.0
    private var seatWiseSelectedList: ArrayList<String> = arrayListOf()
    private lateinit var onSeatSelectionListener: OnSeatSelectionListener
    private var selectedFilterPrice: String = ""
    private var lowerAvailableSeat = 0
    private var upperAvailableSeat = 0
    var STATUS_AVAILABLE = 0
    var LOWER_STATUS_AVAILABLE = 1
    var UPPER_STATUS_AVAILABLE = 3
    var selectedSeatDetails = ArrayList<SeatDetail>()
    var finalSeatNumbers = arrayListOf<String?>()
    val selectedlistlower = arrayListOf<SeatDetail>()
    var totalSeatPrice = arrayListOf<Any?>()
    var seatInfoL = ArrayList<SeatDetail>()
    var seatInfoU = ArrayList<SeatDetail>()
    var seatTextView = ArrayList<TextView>()
    var iscalledFirstTime = true
    var selectedSeats = ArrayList<TextView>()
    private var source = PreferenceUtils.getSource()
    private var destination = PreferenceUtils.getDestination()
    private val coachfunction = PreferenceUtils.getString("SelectionCoach")
    private var sourceId = ""
    private var destinationId = ""
    private var ticketNumber = ""
    private var oldServiceName = ""
    private var oldSeatNumbers = ""
    private var numberOfOldSeatList = 0
    private var oldSeatAllList = ""
    private var selectedResevationID = ""
    private var selectedPassengerName = ""
    private var selectedSeatNumber = ""
    private lateinit var extraSeatAdapter: ExtraSeatAdapter
    private var extraSeatList = mutableListOf<ExtraSeatDetail>()
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var selectedColor: String? = ""

    //    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var isOwnRoute: Boolean = false
    private var isMoveToExtraSeatAllow: Boolean = false
    private var oldTraveldate: String? = ""
    private var seatWiseDecide = ""
    private var is_hide_driver_seat = true
    private var isServiceBlocked: Boolean? = false
    private var canConfirmPhoneBlock: Boolean? = false
    private var canReleasePhoneBlock: Boolean? = false
    private var canCancelTicket: Boolean? = false
    private var callPassenger: Boolean? = false
    var contactNumber = ""
    var status = false
    var locale: String? = ""
    private var showFare = false
    private var extraSelected = false
    private var isAgentLogin = false
    private var updatePassengerTravelStatus = false
    private var isFromChile: Boolean = false

    private var lowerSeat = 0
    private var upperSeat = 0
    private var isAllSeatsBlocked = false
    private var blockedSeatsList = arrayListOf<SeatDetail>()
    private var serviceDetailsData: Body? = null
    private var coachChoice = ""

    private var allowToBookExtraSeats: Boolean = false
    private var allowToBookExtraSeatForOtherRoute: Boolean = false
    private var isIndonesiaLogin: Boolean = false
    private var isUpdateFromCoach: Boolean = false
    private var selectedBlockedSeatNo = arrayListOf<String?>()
    private var ticketMoveToSeatExtraSeat: Boolean = false
    private var pinSize = 0

    private var hideFareBookedSeats: Boolean = false
    private var blockedSeatsHideFare: Boolean = false
    private var blockingNumber: String = ""
    private var oldSeatNumber: String = ""
    private var newSeatNumber: String = ""
    private var isAllowUsersToViewTicket: Boolean = false
    private var isMarginApplied = false  // Track toggle state
    private var role: String = ""
    private var isBookedSeat: Boolean = false  // For drag and drop seat
    private var canShiftTicket: Boolean = false  // For drag and drop seat

    private fun getPref() {
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()

//        privilegeDetails = (activity as BaseActivity).getPrivilegeBase()

        lifecycleScope.launch {
            val privilege = (activity as BaseActivity).getPrivilegeBaseSafely()
            moveToExtraSeatViewModel.updatePrivileges(privilege)

            isAllowUsersToViewTicket = privilege?.allowBooking != true && privilege?.tsPrivileges?.allowUsersToViewTicket == true
        }

        moveToExtraSeatViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponseModel ->

            currentCountry = privilegeResponseModel?.country ?: ""

            seatWiseDecide = PreferenceUtils.getString("seatwiseFare").toString()
            isServiceBlocked = PreferenceUtils.getPreference(getString(R.string.is_service_blocked), false)

            when (seatWiseDecide) {
                "SEATWISE" -> {
                    val listener = parentFragment as? OnSeatSelectionListener
                    if (listener != null) {
                        onSeatSelectionListener = listener
                    } else{
                        Log.e("AllCoachFragment", "ParentFragment must implement OnSeatSelectionListener")
                    }
                }

                "fromBulkShiftPassenger" -> {
                    //Timber.d("")
                }

                else -> {
                    try {
                        onSeatSelectionListener = activity as OnSeatSelectionListener
                    } catch (e: Exception) {
                        //                throw ClassCastException("${activity.toString()} must implement onSeatSelectionListener")
                    }
                }
            }

//        if (privilegeDetails != null) {
//            privilegeResponseModel = privilegeDetails as PrivilegeResponseModel
//        }

            if (PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false) != null) {
                isOwnRoute = PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false)!!
            }

            if (privilegeResponseModel != null) {
                isAgentLogin = privilegeResponseModel.isAgentLogin
                coachChoice = PreferenceUtils.getPreference("COACH_VIEW_SELECTION", "SingleViewSelected") ?: "SingleViewSelected"
                isFromChile = privilegeResponseModel.isChileApp
                currency = privilegeResponseModel.currency?:""
                currencyFormat = getCurrencyFormat(requireContext(), privilegeResponseModel.currencyFormat)

                ticketMoveToSeatExtraSeat = privilegeResponseModel?.pinBasedActionPrivileges?.ticketMoveToSeatExtraSeat ?: false
                pinSize = privilegeResponseModel.pinCount ?: 6

                if (privilegeResponseModel.showSeatFareOnTheCoach != null) {
                    showFare = privilegeResponseModel.showSeatFareOnTheCoach
                }

                hideFareBookedSeats = privilegeResponseModel.hideFareBookedSeats ?: false
                blockedSeatsHideFare = privilegeResponseModel.blockedSeatsHideFare ?: false
                isIndonesiaLogin = privilegeResponseModel.country.equals("Indonesia", true)

                if (privilegeResponseModel.country.equals("India", true)) {
                    binding.manifestBtn.gone()
                }
            }
            role = getUserRole(loginModelPref, isAgentLogin, requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllCoachBinding.inflate(layoutInflater)
        getPref()
        onClickListener()
        binding.chkAllSeats.text = getString(R.string.book_extra_seats)
        lifecycleScope.launch {
            moveToExtraSeatViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        moveQuoteBlockSeatObserver()
        shiftPassengerObserver()
        return binding.root
    }

    private fun onClickListener() {
        binding.lowertab.setOnClickListener(this)
        binding.uppertab.setOnClickListener(this)
        binding.manifestBtn.setOnClickListener(this)
        binding.chkAllSeats.setOnClickListener(this)
    }

    override fun setCoachData(serviceDetails: Body) {
        serviceDetailsData = serviceDetails
        if (serviceDetails.isFromShiftPassenger) {
            isFromShiftPassenger = serviceDetails.isFromShiftPassenger
            shiftPassengerCount = serviceDetails.shiftPassengerCount
            onSeatSelectionListener = activity as OnSeatSelectionListener
        }
        if (serviceDetails != null)
            service(serviceDetails, 0)
    }

    private var coachNumber: Any? = null

    private fun privilegeForExtraSeat() {

        moveToExtraSeatViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponseModel ->
            if (privilegeResponseModel!=null) {
                if (serviceDetailsData?.isBima == null || serviceDetailsData?.isBima == false) {
                    if (!privilegeResponseModel?.allowToBookExtraSeats!!) {
                        if (isOwnRoute && isServiceBlocked == false) {
                            binding.chkAllSeats.visible()
                        } else {
                            if (privilegeResponseModel?.allowToBookExtraSeatForOtherRoute == true && isServiceBlocked == false) {
                                binding.chkAllSeats.visible()
                            } else {
                                binding.chkAllSeats.gone()
                            }
                        }

                    } else {
                        binding.chkAllSeats.visible()
                    }

                    if (isAgentLogin) {
                        if (privilegeResponseModel.allowToBookExtraSeats && isServiceBlocked == false) {
                            binding.chkAllSeats.visible()
                        } else {
                            binding.chkAllSeats.gone()
                        }
                    }
                }
                else {
                    if (privilegeResponseModel?.chartSharedPrivilege?.isNotEmpty() == true
                        && privilegeResponseModel.chartSharedPrivilege?.get(0)?.privileges?.allow_to_book_extra_seat == true
                    ) {
                        binding.chkAllSeats.visible()
                    } else {
                        binding.chkAllSeats.gone()
                    }
                }

                if (serviceDetailsData?.isBima == true) {
                    binding.layoutCrewI.busDetailsContainer.gone()
                } else {
                    binding.layoutCrewI.busDetailsContainer.visible()
                }

                if (activity is CoachLayoutReportingActivity) {
                    binding.chkAllSeats.gone()
                }
            }
        }
    }

    private fun setExtraSeatAdapter() {
        val extraSeats = mutableListOf<ExtraSeatDetail>()
        for (i in 0..extraSeatList.size.minus(1)) {
            if (extraSeatList[i].seatNo?.split("-")?.get(1)?.isNotEmpty()!!)
                extraSeats.add(extraSeatList[i])
        }
        layoutManager = GridLayoutManager(context, 6)

        binding.rvExtraSeats.layoutManager = layoutManager
        if (requireActivity() != null) {
            extraSeatAdapter =
                ExtraSeatAdapter(
                    requireActivity(),
                    extraSeats,
                    currency?:"",
                    currencyFormat,
                    currentCountry,
                ) { view, position ->
                    getExtraSeatsOptions(view, position)
                }
            binding.rvExtraSeats.adapter = extraSeatAdapter
        }
    }

    private fun getExtraSeatsOptions(view: View, position: Int) {

        moveToExtraSeatViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponseModel ->

            if (privilegeResponseModel?.country.equals("India", true)) {
                availableSeats(arrayListOf())
                val resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!

                try {
                    val seatDetails: List<SeatDetail>? = serviceDetailsData?.coachDetails?.seatDetails
                    val availableList: ArrayList<String>? = arrayListOf()
                    seatDetails?.forEach { it ->
                        if (it.available!! && it.isBlocked == false) {
                            availableList?.add(it.number.toString())
                            //Timber.d(it.number.toString())
                        }
                    }

                    if (availableList!!.size > 0) {
                        availableSeats(availableList)
                    }

                    val ticket = extraSeatList[position]
                    isOpenTooltip = ticket.isOpenTooltip

                    if (ticket.seatNo != null) {
                        if (isOpenTooltip) {
                            if (privilegeResponseModel?.allowBooking != true && privilegeResponseModel?.tsPrivileges?.allowUsersToViewTicket != true) {
                                return@observe
                            }

                            if(activity is CoachLayoutReportingActivity) {
                                (activity as CoachLayoutReportingActivity).hitMultistationSeatDetailApi(
                                    resId.toString(),
                                    ticket.seatNo!!
                                )
                                (activity as CoachLayoutReportingActivity).setIsExtraSeat(true)
                            } else {
                                (activity as NewCoachActivity).hitMultistationSeatDetailApi(
                                    resId.toString(),
                                    ticket.seatNo!!
                                )
                                (activity as NewCoachActivity).setIsExtraSeat(true)
                            }
                        } else {
                            requireActivity().toast(getString(R.string.seat_is_booked))

                        }

                    }
                } catch (e: Exception){
                    requireActivity().toast(e.message)
                }
            }
        }
    }

    private fun callMoveToNormalSeatApi(
        sms: Boolean,
        remarks: String,
        resID: Long,
        seatNo: String,
        extraSeatNo: String,
        ticketNo: String,
        authPin: String
    ) {


        if (requireContext().isNetworkAvailable()) {
            val reqBody =
                MoveToNormalSeatRequest(
                    loginModelPref.api_key,
                    sms,
                    remarks,
                    resID,
                    extraSeatNo,
                    seatNo,
                    ticketNo,
                    "1",
                    authPin
                )

            moveToExtraSeatViewModel.moveToNormalSeatApi(
                reqBody,
                move_to_extra_seat
            )
        } else
            requireContext().noNetworkToast()


    }








    private fun moveQuoteBlockSeatObserver() {
        moveQuotaBlockSeatViewModel.moveQuotaSeat.observe(viewLifecycleOwner) {
            //Timber.d("moveToExtraSeatResponse ${it.toString()}")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        //requireContext().toast(it.message)
                        it.message?.let { it1 ->
                            DialogUtils.successfulMsgDialog(
                                requireContext(),
                                it1
                            )
                        }
                        (activity as NewCoachActivity).callCoach=true
                        (activity as NewCoachActivity).callServiceApi()
                    }

                    413 -> {

                        if (it?.message != null) {
                            it?.message?.let { it1 -> requireContext().toast(it1) }
                        }
                    }

                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {

                        if (it.message == "Selected seat is successfully shifted!") {
                            it.message?.let { it1 ->
                                DialogUtils.successfulMsgDialog(
                                    requireContext(),
                                    it1
                                )
                            }
                        }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }


    }













    private fun moveToExtraSeatObserver() {
        moveToExtraSeatViewModel.moveToExtraSeat.observe(viewLifecycleOwner) {
            //Timber.d("moveToExtraSeatResponse ${it.toString()}")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        //requireContext().toast(it.message)
                        it.message?.let { it1 ->
                            DialogUtils.successfulMsgDialog(
                                requireContext(),
                                it1
                            )
                        }
                        onSeatSelectionListener.moveExtraSeat(true)
                    }

                    413 -> {

                        if (it?.message != null) {
                            it?.message?.let { it1 -> requireContext().toast(it1) }
                        }
                        DialogUtils.moveToExtraSeatDialog(
                            context,
                            requireContext().resources.getString(R.string.move_to_extra_seat),
                            "${requireContext().resources.getString(R.string.selectedSeatNo)} : $selectedSeatNumber",
                            requireContext().resources.getString(R.string.cancel),
                            requireContext().resources.getString(R.string.move),
                            selectedSeatNumber,
                            " ",
                            object : DialogButtonMoveSeatExtraListener {
                                override fun onLeftButtonClick(string: String?) {

                                }

                                override fun onRightButtonClick(
                                    remarks: String,
                                    seatNo: String,
                                    extraSeatNo: String,
                                    sms: Boolean
                                ) {
                                    if (ticketNumber.contains("("))
                                        ticketNumber = ticketNumber.substringBefore("(").trim()

                                    if(ticketMoveToSeatExtraSeat && currentCountry.equals("india", true)) {
                                        DialogUtils.showFullHeightPinInputBottomSheet(
                                            activity = requireActivity(),
                                            fragmentManager = childFragmentManager,
                                            pinSize,
                                            "Move To Extra",
                                            onPinSubmitted = { pin: String ->
                                                callMoveToExtraSeatApi(
                                                    true,
                                                    remarks = remarks,
                                                    resID = selectedResevationID,
                                                    seatNo = seatNo,
                                                    extraSeatNo = extraSeatNo,
                                                    ticketNo = ticketNumber,
                                                    authPin = pin
                                                )
                                            },
                                            onDismiss = null
                                        )
                                    }
                                    else {
                                        callMoveToExtraSeatApi(
                                            true,
                                            remarks = remarks,
                                            resID = selectedResevationID,
                                            seatNo = seatNo,
                                            extraSeatNo = extraSeatNo,
                                            ticketNo = ticketNumber,
                                            authPin = ""
                                        )
                                    }
                                }

                            },
                            true
                        )
                    }

                    401 -> {
                        /* DialogUtils.unAuthorizedDialog(
                             requireContext(),
                             "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                             this
                         )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {

                        if (it.message == "Selected seat is successfully shifted!") {
                            it.message?.let { it1 ->
                                DialogUtils.successfulMsgDialog(
                                    requireContext(),
                                    it1
                                )
                            }
                            onSeatSelectionListener.moveExtraSeat(true)
                            extraSeatList.clear()
                        }

//                        it.message?.let { it1 -> requireContext().toast(it1) }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }


    }

    private fun callMoveToExtraSeatApi(
        sms: Boolean,
        remarks: String,
        resID: String,
        seatNo: String,
        extraSeatNo: String,
        ticketNo: String,
        authPin: String
    ) {


        if (requireContext().isNetworkAvailable()) {
            val reqBody =
                ReqBody(
                    loginModelPref.api_key,
                    sms,
                    remarks,
                    resID,
                    seatNo,
                    extraSeatNo,
                    ticketNo,
                    locale = locale,
                    auth_pin = authPin
                )
            val moveToExtraSeatRequest =
                MoveToExtraSeatRequest(bccId, format_type, move_to_extra_seat, reqBody)

            /* moveToExtraSeatViewModel.moveToExtraSeatApi(
                 loginModelPref.auth_token,
                 loginModelPref.api_key,
                 moveToExtraSeatRequest,
                 move_to_extra_seat
             ) */

            moveToExtraSeatViewModel.moveToExtraSeatApi(
                reqBody,
                move_to_extra_seat
            )
        } else
            requireContext().noNetworkToast()


    }

    fun service(response: Body, selectedPosition: Int) {
        lowerSeat= 0
        upperSeat= 0
        binding.chkAllSeats.isChecked = false
//        if (response.isOwnRoute == null) {
//            privilegeForExtraSeat(false)
//        } else {
//            privilegeForExtraSeat(response.isOwnRoute!!)
//        }
        coachNumber = response.coachDetails?.coachNumber ?: ""

        try {
            if (response.coachDetails?.coachNumber != null && response.coachDetails?.coachNumber.toString()
                    .isNotEmpty()
            ) {
                binding.layoutCrewI.busNumberTV.text =
                    response.coachDetails?.coachNumber.toString()
            } else {
                binding.layoutCrewI.busNumberTV.text =
                    requireActivity().getString(R.string.not_assigned)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        //Timber.d("lowerUpperCoach : -${response.legendDetails} ")

        if (!response.legendDetails.isNullOrEmpty()) {
            for (i in 0..(response.legendDetails?.size?.minus(1) ?: 0)) {
                /*Timber.d(
                "lowerUpperCoach11: -${
                    response.legendDetails?.get(i)?.colorLegend.toString() == requireContext().getString(
                        R.string.selected
                    )
                } "
            )*/

                if (response.legendDetails?.get(i)?.colorLegend.toString()
                        .equals(getString(R.string.selected_seat_color), true)
                ) {
                    selectedColor = response.legendDetails?.get(i)?.color
                }
            }
        }

        binding.layoutCrewI.crewTV.setOnClickListener {

            firebaseLogEvent(
                requireContext(),
                CREW_SEAT_LAYOUT,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                CREW_SEAT_LAYOUT,
                "Crew_SeatLayout"
            )

            if (requireContext().isNetworkAvailable())
                (activity as NewCoachActivity).callPickupChartCrewDetailsApi()
            else
                requireContext().noNetworkToast()
        }
        if (response.coachDetails?.coachNumber != null) {
            coachNumber = response.coachDetails?.coachNumber
        }

        if (response.coachDetails?.coachNumber != null && response.coachDetails?.coachNumber != "") {
            binding.layoutCrewI.busNumberTV.text = response.coachDetails?.coachNumber.toString()
        } else {
            binding.layoutCrewI.busNumberTV.text =
                requireActivity().getString(R.string.not_assigned)
        }


        if (response.extraSeatDetails != null) {
            extraSeatList = response.extraSeatDetails ?: ArrayList<ExtraSeatDetail>()
        }

        privilegeForExtraSeat()

        if (response.extraSeatDetails?.isEmpty() == false) {
            binding.layoutExtraSeats.visible()
            setExtraSeatAdapter()
        } else {
            binding.layoutExtraSeats.gone()

        }


        // val columns: Int = response.coachDetails?.noOfCols?.plus(3)!!

        val rows: Int = response.coachDetails?.noOfRows?.plus(4) ?: 0
        try {
            sourceId = response.origin?.id.toString()
            destinationId = response.destination?.id.toString()
            oldServiceName = response.number ?: ""
            oldTraveldate = response.travelDate
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }


        val upperCoach = ArrayList<SeatDetail?>()
        val lowerCoach = ArrayList<SeatDetail?>()
        seatTextView.clear()

        var uCount: Int = 0
        var lCount: Int = 0
        var rowidchckU: Int = 1
        var rowidchckL: Int = 1
        var hasUpperBirth: Boolean = false
        var maxcolid = 0
        var lowerArrayCount = 0
        var upperArrayCount = 0


        /*var isGangwayOnly = true
           for(i in 0..response?.coachDetails?.seatDetails?.size?.minus(1)!!){
               if(response?.coachDetails?.seatDetails?.get(i)?.rowId==1 && !response?.coachDetails?.seatDetails?.get(i)?.isGangway!!){
                   isGangwayOnly = false
                   break
               }
           }*/
//        if (response.coachDetails?.driverPosition != null && response.coachDetails?.driverPosition?.toLowerCase()
//                ?.contains("right")!!
//        ) {
//            binding.rightDriver.gone()
//            binding.leftDriver.visible()
//
//        } else {
//            // binding.rightDriver.visible()
//            binding.rightDriver.gone()
//            binding.leftDriver.gone()
//        }

        var driverPosition: String = response.coachDetails?.driverPosition.toString()
        var isDriverExistSeat: Boolean = false

        for (i in 0..(response.coachDetails?.seatDetails?.size?.minus(1) ?: 0)) {
            val isSeatAvailable: Boolean? =
                response.coachDetails?.seatDetails?.get(i)?.available
            val type: String? = response.coachDetails?.seatDetails?.get(i)?.type
            val rowid: Int? = response.coachDetails?.seatDetails?.get(i)?.rowId
            val colid: Int = response.coachDetails?.seatDetails?.get(i)?.colId?:0
            val seatDetail: SeatDetail? = response.coachDetails?.seatDetails?.get(i)
            if (type?.lowercase(Locale.getDefault())
                    ?.contains("upper") == true || type?.lowercase(Locale.getDefault())
                    ?.equals("ub") == true || (!seatDetail?.floorType.isNullOrBlank() && seatDetail?.floorType?.contains(
                    "2"
                ) == true)
            ) {
                upperSeat += 1
            } else {
                /*if ((type?.toLowerCase()?.contains("lower") == true || type?.toLowerCase()
                        ?.equals("lb") == true || (seatDetail?.floorType.isNullOrBlank() && seatDetail?.floorType?.contains(
                        "1"
                    ) == true)
                            )
                ) {
                    lowerSeat += 1
                }*/
                lowerSeat += 1


            }


            var berthTextPos: Boolean = false

            if (maxcolid < colid) {
                maxcolid = colid
            }

            if (type?.lowercase(Locale.getDefault())?.contains("berth") == true || type?.lowercase(
                    Locale.getDefault()
                )
                    ?.contains("ub") == true || type?.lowercase(Locale.getDefault())
                    ?.contains("lb") == true || type?.lowercase(Locale.getDefault())
                    ?.contains("window single lower") == true || type?.lowercase(Locale.getDefault())
                    ?.contains("window single lower") == true || type?.lowercase(Locale.getDefault())
                    ?.contains("window single upper") == true
            ) {
                berthTextPos = true
                seatDetail?.isBerth = true
                seatDetail?.rowSpan = 2
                seatDetail?.isSeat = false
            } else {
                berthTextPos = false
                seatDetail?.isBerth = false
                seatDetail?.rowSpan = 1
            }
            seatDetail?.isReservable = seatDetail?.available
            seatDetail?.isUpper = false

            if (type?.lowercase(Locale.getDefault())?.contains("upper") == true
                || type?.lowercase(Locale.getDefault())?.equals("ub") == true
                || (!seatDetail?.floorType.isNullOrBlank() && seatDetail?.floorType?.contains("2") == true)
            ) {
                seatDetail?.isUpper = true
                var upperAvalibility = seatDetail?.isUpper
                hasUpperBirth = true
            }
            seatDetail?.isLower = !seatDetail?.isUpper!!
            seatDetail?.isGangway = seatDetail?.isGangway == true || type?.contains(
                "Gang",
                true
            ) == true || type?.contains(
                ".GY",
                true
            ) == true || type?.contains(
                "Break",
                true
            ) == true || type?.contains(
                "Un Reservable Seat",
                true
            ) == true
            seatDetail?.isBreak = type?.equals("Break", true) == true
            //coachLayoutJson[rowid?][colid?] = SeatDetail
            if (coachChoice == "WebViewSelected") {
                upperSeat = 0
                if (seatDetail?.isLower == true || seatDetail?.isUpper == true || seatDetail?.isGangway == true) {
                    if (isSeatAvailable == true) {
                        if (selectedPosition > 0) {
                            when {
                                response.coachDetails?.seatDetails?.get(i)?.fare!! == selectedFilterPrice.toDouble() -> {
                                    lowerAvailableSeat++
                                }
                            }
                        } else {
                            lowerAvailableSeat++
                        }
                        lCount++
                    }
                    if (lowerArrayCount == 0) {
                        rowidchckL = rowid!!
                    }
                    lowerCoach.add(seatDetail)
                    if (rowidchckL != rowid) {
                        rowidchckL = rowid!!
                        lowerArrayCount++
                    }
                }
            }
            else {
                if (seatDetail?.isLower == true || seatDetail?.isGangway == true) {
                    if (isSeatAvailable == true) {
                        if (selectedPosition > 0) {
                            if (response.coachDetails?.seatDetails?.get(i)?.fare!! == selectedFilterPrice.toDouble()) {
                                lowerAvailableSeat++
                            }
                        } else {
                            lowerAvailableSeat++
                        }
                        lCount++
                    }
                    if (lowerArrayCount == 0) {
                        rowidchckL = rowid!!
                    }
                    lowerCoach.add(seatDetail)
                    if (rowidchckL != rowid) {
                        rowidchckL = rowid!!
                        lowerArrayCount++
                    }
                }
                if (seatDetail?.isUpper == true || seatDetail?.isGangway == true) {
                    if (isSeatAvailable == true) {
                        if (selectedPosition > 0) {
                            if (response.coachDetails?.seatDetails?.get(i)?.fare!! == selectedFilterPrice.toDouble()) {
                                upperAvailableSeat++
                            }
                        } else {
                            upperAvailableSeat++
                        }
                        uCount++
                    }
                    if (upperArrayCount == 0) {
                        rowidchckU = rowid!!
                    }
                    upperCoach.add(seatDetail)
                    if (rowidchckU != rowid) {
                        rowidchckU = rowid!!
                        upperArrayCount++
                    }
                }
            }
        }

        val isAllSeatsBlockedLower = checkIfAllSeatsBlocked(lowerCoach, true)

        val areAtleastTwoSeatsAvailableToBlockLower = areAtleastTwoSeatsAvailableToBlock(lowerCoach)
        val areAtleastTwoSeatsAvailableToUnblockLower = areAtleastTwoSeatsAvailableToUnblock(lowerCoach)

        isAllSeatsBlocked = isAllSeatsBlockedLower

        /*        if(isAllSeatsBlocked){
                    //binding.selectallseats.text = requireActivity().getString(R.string.selectAllSeatsToUnblock)
                    binding.layoutBlockAllSeats.gone()
                } else {
                    //binding.selectallseats.text = requireContext().getString(R.string.selectAllSeat)
                    binding.layoutBlockAllSeats.gone()
                }*/

        if (areAtleastTwoSeatsAvailableToBlockLower) {
            binding.selectallseats.visible()
        } else {
            binding.selectallseats.gone()
        }

        moveToExtraSeatViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponseModel ->
            if (areAtleastTwoSeatsAvailableToUnblockLower && privilegeResponseModel?.allowBookingForAllotedServices == false) {
                binding.layoutSelectAllSeatsToUnblock.visible()
            } else {
                binding.layoutSelectAllSeatsToUnblock.gone()
            }

            if (hasUpperBirth) {
                //seatLayoutGenerate(layoutU, coachlistMake(coachLayoutJsonUTemp, false), false)
                val isAllSeatsBlockedUpper = checkIfAllSeatsBlocked(upperCoach, false)

                val areAtleastTwoSeatsAvailableToBlockUpper = areAtleastTwoSeatsAvailableToBlock(upperCoach)
                val areAtleastTwoSeatsAvailableToUnblockUpper = areAtleastTwoSeatsAvailableToUnblock(upperCoach)

                /*            if (isAllSeatsBlockedLower && isAllSeatsBlockedUpper) {
                                isAllSeatsBlocked = true
                                //binding.selectallseats.text = requireActivity().getString(R.string.selectAllSeatsToUnblock)
                                binding.layoutBlockAllSeats.gone()
                            } else {
                                //binding.selectallseats.text = requireContext().getString(R.string.selectAllSeat)
                                binding.layoutBlockAllSeats.visible()
                            }*/

                if (areAtleastTwoSeatsAvailableToBlockLower || areAtleastTwoSeatsAvailableToBlockUpper) {
                    binding.selectallseats.visible()
                } else {
                    binding.selectallseats.gone()
                }

                if (privilegeResponseModel?.allowBookingForAllotedServices == false
                    && (areAtleastTwoSeatsAvailableToUnblockLower || areAtleastTwoSeatsAvailableToUnblockUpper))
                {
                    binding.layoutSelectAllSeatsToUnblock.visible()
                } else {
                    binding.layoutSelectAllSeatsToUnblock.gone()
                }
            }

//        coachlistMakeNew(binding.layoutSeatUpper, upperCoach, false, maxcolid + 2, rows)
            //Timber.d("lowerUpperCoach : -${lowerSeat}, -${upperSeat} ")
//        callCoachList(lowerSeat, upperSeat,lowerCoach, upperCoach,)

            if (lowerSeat == 0 && upperSeat == 0) {
                binding.upperMainLayout.gone()
                binding.lowerMainLayout.visible()
                binding.loweruppertab.gone()
                coachlistMakeNew(
                    layout = binding.layoutSeatlower,
                    listArray = lowerCoach,
                    isLower = true,
                    noOfCol = maxcolid + 2,
                    noOfRow = rows+2
                )
            }
            else if (lowerSeat != 0 && upperSeat != 0) {
                coachlistMakeNew(
                    layout = binding.layoutSeatUpper,
                    listArray = upperCoach,
                    isLower = false,
                    noOfCol = maxcolid + 2,
                    noOfRow = rows+2
                )
                coachlistMakeNew(
                    layout = binding.layoutSeatlower,
                    listArray = lowerCoach,
                    isLower = true,
                    noOfCol = maxcolid + 2,
                    noOfRow = rows+2
                )
                callCoachList()
            }
            else if (lowerSeat != 0 && upperSeat == 0) {
                binding.apply {
                    lowerMainLayout.visible()
                    upperMainLayout.gone()
                    loweruppertab.gone()
                    lowerText.gone()
                    upperText.gone()
                }

                coachlistMakeNew(
                    layout = binding.layoutSeatlower,
                    listArray = lowerCoach,
                    isLower = true,
                    noOfCol = maxcolid + 2,
                    noOfRow = rows+2
                )
            }

        }
    }

    private fun areAtleastTwoSeatsAvailableToBlock(listArray: ArrayList<SeatDetail?>): Boolean {
        var counter = 0
        var flag = false
        listArray.forEach {
            if (it?.isGangway == false) {

                if ((it.available == true && it.isBlocked == false) && counter == 1) {
                    flag = true
                    return@forEach
                } else if ((it.available == true && it.isBlocked == false) && counter == 0) {
                    counter++
                }
            }
        }
        return flag
    }

    private fun checkIfAllSeatsBlocked(
        listArray: ArrayList<SeatDetail?>,
        clearBlockList: Boolean
    ): Boolean {

        if (clearBlockList) {
            blockedSeatsList.clear()
        }

        var totalSeats = 0
        var totalBlockedSeats = 0
        var totalAvailableSeats = 0
        var totalUnavailableSeats = 0

        listArray.forEach {

            if (it?.isGangway == false) {
                totalSeats++
                if (it.available == true) {
                    if (it.isBlocked == true) {

                        totalBlockedSeats += 1
                        blockedSeatsList.add(it)
                    } else {
                        totalAvailableSeats++
                    }
                }
                if (it.available == false) {
                    if (it.isBlocked == true) {
                        totalBlockedSeats += 1
                        blockedSeatsList.add(it)
                    } else {
                        totalUnavailableSeats += 1
                    }
                }

            }
        }

        if (isUpdateFromCoach) {
            (activity as NewCoachActivity).setBlockedList(blockedSeatsList)
        }

        return (totalAvailableSeats == 0 && totalBlockedSeats >= 1)
    }

    private fun areAtleastTwoSeatsAvailableToUnblock(listArray: ArrayList<SeatDetail?>): Boolean {
        if (coachfunction == "BOOK") {
            return false
        }
        var counter = 0
        var flag = false
        listArray.forEach {
            if (it?.isGangway == false) {

                if (it.isBlocked == true && counter == 1) {
                    flag = true
                    return@forEach
                } else if (it.isBlocked == true && counter == 0) {
                    counter++
                }
            }
        }
        return flag
    }

    private fun onUpperTabSelection() {
        binding.lowertab.apply {
            setBackgroundResource(R.drawable.layout_rounded_shape_left_unselected)
            setTextColor(context?.resources?.getColor(R.color.colorDimShadow6)!!)
        }
        binding.uppertab.apply {
            setBackgroundResource(R.drawable.layout_rounded_shape_right)
            setTextColor(context?.resources?.getColor(R.color.white)!!)
        }
        binding.upperMainLayout.visible()
        binding.lowerMainLayout.gone()
    }

    private fun onLowerTabSelection() {
        binding.lowertab.apply {
            setBackgroundResource(R.drawable.layout_rounded_shape_left_selected)
            setTextColor(context?.resources?.getColor(R.color.white)!!)
        }
        binding.uppertab.apply {
            setBackgroundResource(R.drawable.layout_rounded_shape_right_unselected)
            setTextColor(context?.resources?.getColor(R.color.colorDimShadow6)!!)
        }
        binding.lowerMainLayout.visible()
        binding.upperMainLayout.gone()
    }

    fun callCoachList() {

        val coachChoice =
            PreferenceUtils.getPreference(
                "COACH_VIEW_SELECTION",
                "SingleViewSelected"
            )
        //Timber.d("checkCoachView: $coachChoice")

        when (coachChoice) {
            "SingleViewSelected" -> {
                binding.loweruppertab.gone()
                binding.lowerText.visible()
                binding.upperText.visible()
                binding.upperMainLayout.visible()
                binding.lowerMainLayout.visible()
            }

            "SplitViewSelected" -> {
                binding.loweruppertab.visible()
                binding.lowerText.gone()
                binding.upperText.gone()
                binding.upperMainLayout.gone()
                binding.lowerMainLayout.visible()

                if (binding.lowertab.currentTextColor == context?.resources?.getColor(R.color.white)) {
                    onLowerTabSelection()
                } else {
                    onUpperTabSelection()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.lowertab) {
            onLowerTabSelection()
        }
        if (v?.id == R.id.uppertab) {
            onUpperTabSelection()
        }
        if (v?.id == R.id.chkAllSeats) {
            if (binding.chkAllSeats.text == getString(R.string.book_extra_seats)) {

                // Convert to pixels
                val paddingDp = 40f
                val paddingPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, paddingDp,
                    requireContext().resources.displayMetrics
                ).toInt()

                if (binding.chkAllSeats.isChecked) {
                    extraSelected = true
                    onSeatSelectionListener.bookExtraSeats(true)
                    binding.containerAllCoach.updatePadding(0, 0, 0, paddingPx)
                } else {
                    extraSelected = false
                    onSeatSelectionListener.bookExtraSeats(false)
                    binding.containerAllCoach.updatePadding(0, 0, 0, 0)
                }
            }

            firebaseLogEvent(
                requireContext(),
                BOOK_EXTRA_SEATS,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                BOOK_EXTRA_SEATS,
                "Book Extra Seats checkbox click"
            )
        }
        if (v?.id == R.id.manifest_btn) {

            if (!serviceDetailsData?.number.isNullOrEmpty()) {
                PreferenceUtils.putString(
                    "ViewReservation_data",
                    "${serviceDetailsData?.number} | ${
                        serviceDetailsData?.travelDate?.let {
                            getDateDMY(it)
                        }
                    } | ${serviceDetailsData?.origin?.name} - ${serviceDetailsData?.destination?.name} | ${serviceDetailsData?.busType}"
                )
            }

            PreferenceUtils.putString(
                "ViewReservation_date",
                getDateYMD(serviceDetailsData?.travelDate.toString().replace("/", "-"))
            )

            PreferenceUtils.putString(
                "ViewReservation_name",
                "${serviceDetailsData?.origin?.name} - ${serviceDetailsData?.destination?.name}"
            )

            PreferenceUtils.setPreference("BUlK_shifting", false)
            PreferenceUtils.putString("BulkShiftBack", "")
            PreferenceUtils.setPreference("shiftPassenger_tab", 0)
            // PreferenceUtils.setPreference("seatwiseFare", "fromBulkShiftPassenger")

            val result = Result()
            result.bus_type = serviceDetailsData?.busType ?: ""
            result.number = serviceDetailsData?.number ?: ""
            result.dep_time = serviceDetailsData?.depTime ?: ""
            result.reservation_id = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) ?: 0

            PreferenceUtils.putObject(
                result, PREF_SELECTED_AVAILABLE_ROUTES
            )

            val intent = Intent(context, ViewReservationActivity::class.java)
            intent.putExtra(
                "pickUpResid",
                PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) ?: 0
            )

            startActivity(intent)
        }

    }

    override fun onItemChecked(isChecked: Boolean, view: View, position: Int) {

    }

    override fun dialogSwitch(isChecked: Boolean) {

    }


    private fun colorOutline(
        isHisBooking: Boolean,
        isHorizontal: Boolean,
        titleText: TextView,
        isBerth: Boolean
    ) {

        if (isHorizontal) {
            if (isHisBooking) {
                titleText.setBackgroundResource(
                    R.drawable.agent_outline_horizontal
                )
            } else {
                titleText.setBackgroundResource(
                    R.drawable.ic_dev_available_horizontal
                )
            }
            titleText.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorDimShadow6))
        } else if (isBerth == true) {
            if (isHisBooking) {
                titleText.setBackgroundResource(R.drawable.agent_outline_horizontal)
            } else {
                titleText.setBackgroundResource(R.drawable.ic_dev_available_vertical)
            }
            titleText.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorDimShadow6))
        } else {
            if (isHisBooking) {
                titleText.setBackgroundResource(R.drawable.agent_outline_seat)
            } else {
                titleText.setBackgroundResource(R.drawable.ic_dev_available_seat)
            }
            titleText.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorDimShadow6))
        }
    }

    private fun colorSolid(
        isHisBooking: Boolean,
        seatColor: String,
        isHorizontal: Boolean,
        titleText: TextView,
        isBerth: Boolean
    ) {
        if (isHorizontal) {
            try {
                if (isHisBooking) {
                    titleText.setBackgroundResource(
                        R.drawable.agent_birth_horizontal
                    )
                } else {
                    titleText.setBackgroundResource(
                        R.drawable.ic_dev_selected_horizontal
                    )
                }

                titleText.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(seatColor))
            } catch (e: Exception) {
                Timber.d("e: $e")
            }
        } else if (isBerth) {
            try {
                if (isHisBooking) {
                    titleText.setBackgroundResource(R.drawable.agent_birth_vertical)
                } else {
                    titleText.setBackgroundResource(R.drawable.ic_dev_selected_vertical)
                }
                titleText.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(seatColor))
            } catch (e: Exception) {
                Timber.d("e: $e")
            }
        } else {
            try {
                if (isHisBooking) {
                    titleText.setBackgroundResource(R.drawable.agent_seat)
                } else {
                    titleText.setBackgroundResource(R.drawable.ic_dev_selected_seats)
                }
                titleText.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(seatColor))
            } catch (e: Exception) {
                Timber.d("e: $e")
            }

        }
    }

    private fun colorLockedOutline(
        isHisBooking: Boolean,
        isHorizontal: Boolean,
        titleText: TextView,
        isBerth: Boolean
    ) {
        if (isHorizontal) {

            if (isHisBooking) {
                titleText.setBackgroundResource(
                    R.drawable.agent_outline_lock_horizontal
                )
            } else {
                titleText.setBackgroundResource(R.drawable.ic_dev_lock_outline_horizontal)
            }
            titleText.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorDimShadow6))
        } else if (isBerth) {

            if (isHisBooking) {
                titleText.setBackgroundResource(
                    R.drawable.agent_outline_lock_vertical
                )
            } else {
                titleText.setBackgroundResource(R.drawable.ic_dev_lock_outline_vertical)
            }
            titleText.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorDimShadow6))
        } else {
            if (isHisBooking) {
                titleText.setBackgroundResource(
                    R.drawable.agent_outline_lock_seat
                )
            } else {
                titleText.setBackgroundResource(R.drawable.ic_dev_lock_outline_seat)
            }
            //titleText.setBackgroundResource(R.drawable.ic_dev_lock_outline_seat) ic_dev_lock_seat
            titleText.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorDimShadow6))
        }
    }

    private fun driverImage(seatnumber2: String, titleText: TextView) {

        if (seatnumber2.contains("DR_IMG")) {
            titleText.setBackgroundResource(R.drawable.ic_driver)
        } else if (seatnumber2.contains("TV_IMG")) {
            titleText.setBackgroundResource(R.drawable.television)
        } else if (seatnumber2.contains("PA_IMG")) {
            titleText.setBackgroundResource(R.drawable.restaurant)
        } else if (seatnumber2.contains("WR_IMG")) {
            titleText.setBackgroundResource(R.drawable.wash_room)
        } else if (seatnumber2.contains("SM_IMG")) {
            titleText.setBackgroundResource(R.drawable.smoking_area)
        } else if (seatnumber2.contains("ST_IMG")) {
            titleText.setBackgroundResource(R.drawable.stair)
        }
        titleText.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorDimShadow6))
    }

    private fun colorLocked(
        isHisBooking: Boolean,
        seatColor: String,
        isHorizontal: Boolean,
        titleText: TextView,
        isBerth: Boolean
    ) {
        if (isHorizontal) {

            if (isHisBooking) {
                titleText.setBackgroundResource(
                    R.drawable.agent_birth_horizontal_lock
                )
            } else {
                titleText.setBackgroundResource(R.drawable.ic_dev_lock_horizontal)
            }
            if (!seatColor.isNullOrEmpty())
                titleText.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(seatColor))
        } else if (isBerth) {

            if (isHisBooking) {
                titleText.setBackgroundResource(
                    R.drawable.agent_birth_verticl_lock
                )
            } else {
                titleText.setBackgroundResource(R.drawable.ic_dev_lock_vertical)
            }

            if (!seatColor.isNullOrEmpty())
                titleText.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(seatColor))
        } else {
            //titleText.setBackgroundResource(R.drawable.ic_dev_lock_seat)

            if (isHisBooking) {
                titleText.setBackgroundResource(
                    R.drawable.agent_lock_seat
                )
            } else {
                titleText.setBackgroundResource(R.drawable.ic_dev_selected_seats)
            }

            if (!seatColor.isNullOrEmpty()) {
                titleText.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(seatColor))
            }
        }
    }

    fun coachlistMakeNew(
        layout: ViewGroup,
        listArray: ArrayList<SeatDetail?>,
        isLower: Boolean,
        noOfCol: Int,
        noOfRow: Int,
    ) {
        finalSeatNumbers.clear()
        totalSeatPrice.clear()
        selectedlistlower.clear()
        layout.removeAllViews()
        layout.removeAllViewsInLayout()


        val seatInfo = ArrayList<SeatDetail>()
        val gridLayout = GridLayout(requireContext())
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS)
        gridLayout.setColumnCount(noOfCol)
        gridLayout.setRowCount(noOfRow.plus(1))


        if (isLower == true) {
            STATUS_AVAILABLE = LOWER_STATUS_AVAILABLE
        } else {
            STATUS_AVAILABLE = UPPER_STATUS_AVAILABLE
        }
        var count = 0


        val listsubitem = listArray
        for (j in 0..listsubitem.size.minus(1)) {

            val available = listsubitem[j]?.available
            val is_ladies_seat = listsubitem[j]?.isLadiesSeat ?: false
            val seatnumber2 = listsubitem[j]?.number
            val isgangway = listsubitem[j]?.isGangway
            val isHorizontal = listsubitem[j]?.isHorizontal ?: false
            val isBerth = listsubitem[j]?.isBerth
            val is_blocked = listsubitem[j]?.isBlocked
            val fare = listsubitem[j]?.fare
            var bgcolor = listsubitem[j]?.backgroundColor
            val isShifed = listsubitem[j]?.isShifted
            var isBoarded = false
            if (listsubitem[j]?.passengerDetails?.status == 2)
                isBoarded = true

            val isMulti = listsubitem[j]?.isMultiHop
            val isUpdated = listsubitem[j]?.isUpdated
            val isInJourney = listsubitem[j]?.isInJourney
            val remarks = listsubitem[j]?.remarks
            var isHisBookingStatus = false
            //Timber.d("coach_fare: ${listsubitem[j].passengerDetails}")
            if (listsubitem[j]?.passengerDetails != null) {
                isHisBookingStatus = listsubitem[j]?.passengerDetails?.isHisBooking!!
            } else {
                isHisBookingStatus = listsubitem[j]?.isHisBooking ?: false

            }
            val passStatus = listsubitem[j]?.passengerDetails?.status


            if (listsubitem[j]?.backgroundColor.isNullOrEmpty()) {
                bgcolor = "#FFFFFF"
            }
            //Timber.d("coach_fare: $isHisBookingStatus")

            val a: Int? = listsubitem[j]?.rowId
            val row: Int = a ?: -1

            val b: Int? = listsubitem[j]?.colId
            val col: Int = b ?: -1

            val titleText = TextView(context)
            titleText.gravity = Gravity.CENTER_HORIZONTAL
            titleText.textSize = resources.getDimension(R.dimen.seat_text_size).toInt().toFloat()
            titleText.setTypeface(null, Typeface.BOLD)
            if (!seatnumber2.isNullOrEmpty()) {
                val isEditSeatWise = PreferenceUtils.getPreference("isEditSeatWise", false) ?: false
                var roundOff = "${(fare?.toString()?.toDouble())?.convert(currencyFormat)}"
                if (currentCountry.equals("india", true)) {
                    roundOff = roundOff.substringBeforeLast(".")
                }
                val spannableEdit = SpannableString(getString(R.string.edit_caps))
                spannableEdit.setSpan(
                    ForegroundColorSpan(Color.RED),
                    0, // start
                    4, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )

                seatText(
                    seatNumber = seatnumber2,
                    isBoarded = isBoarded,
                    isShifted = isShifed ?: false,
                    isEditSeatWise = isEditSeatWise,
                    isLadies = is_ladies_seat,
                    isMulti = isMulti ?: false,
                    isUpdated = isUpdated ?: false,
                    inJourney = isInJourney ?: false,
                    roundOff = roundOff,
                    spannableEdit = spannableEdit,
                    titleText = titleText,
                    showFare = showFare,
                    status = passStatus.toString(),
                    listItem = listsubitem[j]!!
                )
            }

            titleText.setTextColor(Color.BLACK)
            //titleText.setPadding(5,5,5,5)
            if (isgangway == false) {
                count++
                titleText.id = count
                seatInfo.add(count.minus(1), listsubitem[j]!!)
                if (available == true) {
                    titleText.setTextColor(Color.BLACK)
                    try {
                        if (bgcolor == ("#FFFFFF")) {
                            colorOutline(isHisBookingStatus, isHorizontal, titleText, isBerth!!)
                        } else {
                            colorSolid(
                                isHisBooking = isHisBookingStatus,
                                seatColor = bgcolor ?: "",
                                isHorizontal = isHorizontal,
                                titleText = titleText,
                                isBerth = isBerth ?: false
                            )
                        }

                        val seatStatus = SeatStatusData()
                        seatStatus.status = STATUS_AVAILABLE
                        seatStatus.value = fare.toString()
                        titleText.tag = seatStatus
                    } catch (e: Exception) {
                        e.message
                    }

                } else {
                    try {
                        titleText.setTextColor(Color.BLACK)
                        if (is_blocked == true && !isAgentLogin) {
                            if (bgcolor == "#FFFFFF") {
                                colorLockedOutline(
                                    isHisBooking = isHisBookingStatus,
                                    isHorizontal = isHorizontal,
                                    titleText = titleText,
                                    isBerth = isBerth ?: false
                                )
                            } else {
                                colorLocked(
                                    isHisBooking = isHisBookingStatus,
                                    seatColor = bgcolor!!,
                                    isHorizontal = isHorizontal,
                                    titleText = titleText,
                                    isBerth = isBerth ?: false
                                )
                            }
                        } else {
                            if (bgcolor == "#FFFFFF") {
                                //Timber.d("lockseatcolor1:$bgcolor")
                                colorOutline(
                                    isHisBooking = isHisBookingStatus,
                                    isHorizontal = isHorizontal,
                                    titleText = titleText,
                                    isBerth = isBerth ?: false
                                )
                            } else {
                                //Timber.d("lockseatcolor12:$bgcolor")
                                colorSolid(
                                    isHisBooking = isHisBookingStatus,
                                    seatColor = bgcolor!!,
                                    isHorizontal = isHorizontal,
                                    titleText = titleText,
                                    isBerth = isBerth ?: false
                                )
                            }
                        }
                        val seatStatus = SeatStatusData()
//                            seatStatus.status = CommonCoachFragment.STATUS_BOOKED
                        seatStatus.value = fare.toString()
                        titleText.tag = seatStatus
                        //titleText.tag = STATUS_BOOKED

                    } catch (e: Exception) {
                        e.message
                    }
                }
                if (bgcolor != null && bgcolor.contains(",")) {
                    val getColor = bgcolor.split(",")
                    bgcolor = if (getColor[0].contains("#"))
                        getColor[0]
                    else
                        getColor[1]
                }
            }
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.toFloat())
            gridLayout.addView(titleText)
            val param = GridLayout.LayoutParams()

            if (isBerth == true) param.height =
                resources.getDimension(R.dimen.is_birth_true_height).toInt()
            else param.height = resources.getDimension(R.dimen.is_birth_false_height).toInt()

            if (isHorizontal == true) {
                param.width = resources.getDimension(R.dimen.is_horizontal_width).toInt()
                param.height = resources.getDimension(R.dimen.is_horizontal_height).toInt()
            } else if (isgangway != false) {
                param.width = resources.getDimension(R.dimen.gangway_true_width).toInt()
            } else {
                param.width = resources.getDimension(R.dimen.gangway_false_width).toInt()
            }

            param.rightMargin = 5
            param.topMargin = 2
            param.leftMargin = 5
            param.bottomMargin = 2
            param.setGravity(Gravity.CENTER)

            if (isHorizontal == true) param.columnSpec = GridLayout.spec(col, 2)
            else param.columnSpec = GridLayout.spec(col)

//            if (isBerth == true) {
//                if (isHorizontal == true) {
//                    //param.rowSpec = GridLayout.spec(row.plus(1))
//                    param.rowSpec = GridLayout.spec(row)
//                } else {
//                    param.rowSpec = GridLayout.spec(row, 2)
//                }
//            } else param.rowSpec = GridLayout.spec(row)

            if (row >= 0) { // Ensure row is non-negative
                if (isBerth == true) {
                    if (isHorizontal == true) {
                        param.rowSpec = GridLayout.spec(row)
                    } else {
                        param.rowSpec = GridLayout.spec(row, 2)
                    }
                } else {
                    param.rowSpec = GridLayout.spec(row)
                }
            } else {
                Timber.e("Invalid row value: $row")
                // Handle the error gracefully, e.g., skip setting the layout params
            }

            val originalWidth = param.width
            val originalHeight = param.height

            when (context?.let { PreferenceUtils.getTextSize(it) }) {
                SMALL_TEXT_SIZE -> {
                    param.width = (originalWidth * 0.85).toInt()
                    param.height = (originalHeight * 0.85).toInt()
                }
                DEFAULT_TEXT_SIZE -> {
                    param.width = (originalWidth * 1).toInt()
                    param.height = (originalHeight * 1).toInt()
                }
                LARGE_TEXT_SIZE -> {
                    param.width = (originalWidth * 1.15).toInt()
                    param.height = (originalHeight * 1.15).toInt()
                }
                XLARGE_TEXT_SIZE -> {
                    param.width = (originalWidth * 1.3).toInt()
                    param.height = (originalHeight * 1.3).toInt()
                }
                else -> {
                    param.width = (originalWidth * 1).toInt()
                    param.height = (originalHeight * 1).toInt()
                }
            }



            titleText.layoutParams = param
            param.setMargins(4)

            val seatWiseDecide = PreferenceUtils.getString("seatwiseFare")

            if (seatWiseDecide == "SEATWISE") {
                val perSeat = PreferenceUtils.getPreference("PERSEAT", false)

                if (perSeat == true) {

                    titleText.setOnClickListener {
                        isOpenTooltip = listsubitem[j]?.isOpenTooltip ?: true

                        if (currentCountry.equals("india", true)) {
                            //for india
                            seatSelectUnselectFunction(
                                item = listsubitem[j]!!,
                                titleText = titleText,
                                bgColor = bgcolor ?: "FFFFFF",
                                isHisBookingStatus = isHisBookingStatus
                            )
                            if (seatWiseSelectedList.size > 0) {
                                var isremoved = false
                                var removeVarName = ""
                                for (i in 0 until seatWiseSelectedList.size) {
                                    if (seatWiseSelectedList[i].equals(listsubitem[j]?.number)) {
                                        isremoved = true
                                        removeVarName = listsubitem[j]?.number!!
                                    }
                                }
                                if (!isremoved) {
                                    seatWiseSelectedList.add(listsubitem[j]?.number.toString())
                                } else {
                                    seatWiseSelectedList.remove(listsubitem[j]?.number)

                                }
                            } else {
                                seatWiseSelectedList.add(listsubitem[j]?.number.toString())
                            }

                            onSeatSelectionListener.editSeatFare(
                                seatNumber = seatWiseSelectedList.joinToString(),
                                newFare = ""
                            )
                        } else {
                            val roundOffFare = fare.toString().toDouble().toInt()
                            val roundOffFareString = roundOffFare.toDouble().convert(currencyFormat)

                            DialogUtils.editSeatFareDialog(
                                context = requireContext(),
                                title = getString(R.string.edit_seat_fare),
                                message = "${getString(R.string.current_seat_fare)} $roundOffFareString",
                                buttonLeftText = getString(R.string.goBack),
                                buttonRightText = getString(R.string.confirm),
                                dialogButtonStringListener = object : DialogButtonStringListener {

                                    override fun onLeftButtonClick(string: String?) {
                                    }

                                    override fun onRightButtonClick(string: String?) {
                                        onSeatSelectionListener.editSeatFare(
                                            seatnumber2.toString(),
                                            string.toString()
                                        )

                                    }
                                }
                            )
                        }
                    }
                } else {
                    // Timber.d("seatWiseCommonCoachSingle - click failed")
                }
            } else if (seatWiseDecide == "fromBulkShiftPassenger") {
                titleText.setOnClickListener {

                    requireContext().toast(getString(R.string.this_seat_cannot_be_selected))
                }

            } else {

                moveToExtraSeatViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponseModel ->

                    if (privilegeResponseModel?.country.equals("India", true) && (activity is NewCoachActivity)) {
                        if (isAgentLogin) {
                            dragAndDropSeats(privilegeResponseModel, titleText, seatnumber2,listsubitem[j]!!)
                        } else {
                            dragAndDropSeats(
                                privilegeResponseModel,
                                titleText,
                                seatnumber2,
                                listsubitem[j]!!
                            )
                        }
                    }



//                    titleText.setOnClickListener {
//
//                        isOpenTooltip = listsubitem[j]?.isOpenTooltip ?: true
//
//                        if ((isFromShiftPassenger && selectedlistlower.size >= shiftPassengerCount)) {
//                            if (listsubitem[j]?.isimage == true) {
//                                requireContext().toast(getString(R.string.this_seat_cannot_be_selected))
//                            } else if (selectedlistlower.contains(listsubitem[j])) {
//                                selectedlistlower.remove(listsubitem[j])
//                                finalSeatNumbers.remove(listsubitem[j]?.number)
//                                selectedSeats.remove(titleText)
//
//                                totalSeatPrice.remove(listsubitem[j]?.baseFareFilter)
//
//                                titleText.setTextColor(resources.getColor(R.color.black))
//                                if (bgcolor!!.contains("#FFFFFF")) {
//                                    colorOutline(
//                                        isHisBookingStatus,
//                                        isHorizontal,
//                                        titleText,
//                                        isBerth!!
//                                    )
//                                } else {
//                                    colorSolid(
//                                        isHisBooking = isHisBookingStatus,
//                                        seatColor = bgcolor,
//                                        isHorizontal = isHorizontal,
//                                        titleText = titleText,
//                                        isBerth = isBerth!!
//                                    )
//                                }
//                            } else
//                                requireContext().toast(getString(R.string.no_more_seats_can_be_selected))
//                        } else {
//                            /*if (listsubitem[j].passengerDetails != null) {
//                                canReleasePhoneBlock =
//                                    listsubitem[j].passengerDetails?.canReleasePhoneBlock
//                                canConfirmPhoneBlock =
//                                    listsubitem[j].passengerDetails?.canConfirmPhoneBlock
//                                canCancelTicket = listsubitem[j].passengerDetails?.canCancel
//                            }*/
//
//                            isServiceBlocked =
//                                PreferenceUtils.getPreference(
//                                    getString(R.string.is_service_blocked),
//                                    false
//                                )
//                            if (titleText.tag != null) {
//                                if (isServiceBlocked == true && available == true) {
//                                    requireActivity().toast(getString(R.string.service_is_blocked))
//                                } else {
//                                    if (listsubitem.isNotEmpty()) {
//
//                                        if (isAgentLogin && is_blocked == true && available == false
//                                            && serviceDetailsData?.canBlockSeat == false
//                                            && serviceDetailsData?.canUnblockSeat == false
//                                        ) {
//                                            requireContext().toast(getString(R.string.this_seat_is_blocked))
//                                        } else if (available == false
//                                            && privilegeResponseModel?.isCanBlockSeats == false
//                                            && privilegeResponseModel?.isCanUnblockSeats == false
//                                            && is_blocked == true
//                                        ) {
//                                            requireContext().toast(getString(R.string.this_seat_is_blocked))
//                                        } else {
//                                            if (selectedlistlower.size == 0) {
//
//                                                if (serviceDetailsData?.isBima != null && serviceDetailsData?.isBima == true) {
//
//                                                    if (privilegeResponseModel?.chartSharedPrivilege?.get(
//                                                            0
//                                                        )?.privileges?.allow_booking == true
//                                                    ) {
//                                                        seatSelectUnselectFunction(
//                                                            item = listsubitem[j]!!,
//                                                            titleText = titleText,
//                                                            bgColor = bgcolor ?: "FFFFFF",
//                                                            isHisBookingStatus = isHisBookingStatus
//                                                        )
//                                                    } else {
//                                                        requireContext().toast(getString(R.string.booking_not_allowed))
//                                                    }
//                                                } else if (privilegeResponseModel?.allowBimaInTs == true && privilegeResponseModel.chartSharedPrivilege?.isNotEmpty() == true) {
//
//                                                    if (privilegeResponseModel.chartSharedPrivilege?.get(
//                                                            0
//                                                        )?.privileges?.allow_booking == true
//                                                    ) {
//                                                        seatSelectUnselectFunction(
//                                                            item = listsubitem[j]!!,
//                                                            titleText = titleText,
//                                                            bgColor = bgcolor ?: "FFFFFF",
//                                                            isHisBookingStatus = isHisBookingStatus
//                                                        )
//                                                    } else {
//                                                        requireContext().toast(getString(R.string.booking_not_allowed))
//                                                    }
//                                                } else {
//                                                    if (privilegeResponseModel?.allowBooking == true) {
//                                                        seatSelectUnselectFunction(
//                                                            item = listsubitem[j]!!,
//                                                            titleText = titleText,
//                                                            bgColor = bgcolor ?: "FFFFFF",
//                                                            isHisBookingStatus = isHisBookingStatus
//                                                        )
//                                                    } else {
//                                                        requireContext().toast(getString(R.string.booking_not_allowed))
//                                                    }
//                                                }
//
//                                            } else {
//                                                if (selectedlistlower[0].available == false) {
//                                                    if (selectedlistlower[0].available == false && listsubitem[j]?.available == false) {
//                                                        seatSelectUnselectFunction(
//                                                            item = listsubitem[j]!!,
//                                                            titleText = titleText,
//                                                            bgColor = bgcolor ?: "FFFFFF",
//                                                            isHisBookingStatus = isHisBookingStatus
//                                                        )
//                                                    } else {
//                                                        requireContext().toast(getString(R.string.this_seat_cannot_be_selected))
//                                                    }
//                                                } else {
//                                                    if (listsubitem[j]?.available == false) {
//                                                        requireContext().toast(getString(R.string.this_seat_cannot_be_selected))
//                                                    } else {
//                                                        seatSelectUnselectFunction(
//                                                            item = listsubitem[j]!!,
//                                                            titleText = titleText,
//                                                            bgColor = bgcolor ?: "FFFFFF",
//                                                            isHisBookingStatus = isHisBookingStatus
//                                                        )
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        if (::onSeatSelectionListener.isInitialized) {
//                            onSeatSelectionListener.selectedSeatCount(selectedlistlower)
//                        }
//                    }



                    titleText.setOnSingleDoubleClickListener(
                        onDoubleClick = {
                            seatLongPressSelected(
                                item = listsubitem[j]!!,
                            )
                        },
                        onSingleClick = {
                            isOpenTooltip = listsubitem[j]?.isOpenTooltip ?: true

                            if ((isFromShiftPassenger && selectedlistlower.size >= shiftPassengerCount)) {
                                if (listsubitem[j]?.isimage == true) {
                                    requireContext().toast(getString(R.string.this_seat_cannot_be_selected))
                                } else if (selectedlistlower.contains(listsubitem[j])) {
                                    selectedlistlower.remove(listsubitem[j])
                                    finalSeatNumbers.remove(listsubitem[j]?.number)
                                    selectedSeats.remove(titleText)

                                    totalSeatPrice.remove(listsubitem[j]?.baseFareFilter)

                                    titleText.setTextColor(resources.getColor(R.color.black))
                                    if (bgcolor!!.contains("#FFFFFF")) {
                                        colorOutline(
                                            isHisBookingStatus,
                                            isHorizontal,
                                            titleText,
                                            isBerth!!
                                        )
                                    } else {
                                        colorSolid(
                                            isHisBooking = isHisBookingStatus,
                                            seatColor = bgcolor,
                                            isHorizontal = isHorizontal,
                                            titleText = titleText,
                                            isBerth = isBerth!!
                                        )
                                    }
                                } else
                                    requireContext().toast(getString(R.string.no_more_seats_can_be_selected))
                            } else {
                                /*if (listsubitem[j].passengerDetails != null) {
                                    canReleasePhoneBlock =
                                        listsubitem[j].passengerDetails?.canReleasePhoneBlock
                                    canConfirmPhoneBlock =
                                        listsubitem[j].passengerDetails?.canConfirmPhoneBlock
                                    canCancelTicket = listsubitem[j].passengerDetails?.canCancel
                                }*/

                                isServiceBlocked =
                                    PreferenceUtils.getPreference(
                                        getString(R.string.is_service_blocked),
                                        false
                                    )
                                if (titleText.tag != null) {
                                    if (isServiceBlocked == true && available == true) {
                                        requireActivity().toast(getString(R.string.service_is_blocked))
                                    } else {
                                        if (listsubitem.isNotEmpty()) {

                                            if (isAgentLogin && is_blocked == true && available == false
                                                && serviceDetailsData?.canBlockSeat == false
                                                && serviceDetailsData?.canUnblockSeat == false
                                            ) {
                                                requireContext().toast(getString(R.string.this_seat_is_blocked))
                                            } else if (available == false
                                                && privilegeResponseModel?.isCanBlockSeats == false
                                                && privilegeResponseModel?.isCanUnblockSeats == false
                                                && is_blocked == true
                                            ) {
                                                requireContext().toast(getString(R.string.this_seat_is_blocked))
                                            } else {
                                                if (selectedlistlower.size == 0) {

                                                    if (serviceDetailsData?.isBima != null && serviceDetailsData?.isBima == true) {

                                                        if (privilegeResponseModel?.chartSharedPrivilege?.get(
                                                                0
                                                            )?.privileges?.allow_booking == true
                                                        ) {
                                                            seatSelectUnselectFunction(
                                                                item = listsubitem[j]!!,
                                                                titleText = titleText,
                                                                bgColor = bgcolor ?: "FFFFFF",
                                                                isHisBookingStatus = isHisBookingStatus
                                                            )
                                                        } else {
                                                            requireContext().toast(getString(R.string.booking_not_allowed))
                                                        }
                                                    } else if (privilegeResponseModel?.allowBimaInTs == true && privilegeResponseModel.chartSharedPrivilege?.isNotEmpty() == true) {

                                                    if (privilegeResponseModel.chartSharedPrivilege?.get(
                                                            0
                                                        )?.privileges?.allow_booking == true
                                                    ) {
                                                        seatSelectUnselectFunction(
                                                            item = listsubitem[j]!!,
                                                            titleText = titleText,
                                                            bgColor = bgcolor ?: "FFFFFF",
                                                            isHisBookingStatus = isHisBookingStatus
                                                        )
                                                    } else {
                                                        if (listsubitem[j]?.available == true || listsubitem[j]?.isBlocked == true) {
                                                            requireContext().toast(getString(R.string.booking_not_allowed))
                                                        } else if (isAllowUsersToViewTicket) {
                                                            seatSelectUnselectFunction(
                                                                item = listsubitem[j]!!,
                                                                titleText = titleText,
                                                                bgColor = bgcolor ?: "FFFFFF",
                                                                isHisBookingStatus = isHisBookingStatus
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    if (privilegeResponseModel?.allowBooking == true) {
                                                        seatSelectUnselectFunction(
                                                            item = listsubitem[j]!!,
                                                            titleText = titleText,
                                                            bgColor = bgcolor ?: "FFFFFF",
                                                            isHisBookingStatus = isHisBookingStatus
                                                        )
                                                    } else {
                                                        if (listsubitem[j]?.available == true || listsubitem[j]?.isBlocked == true) {
                                                            requireContext().toast(getString(R.string.booking_not_allowed))
                                                        } else if (isAllowUsersToViewTicket) {
                                                            seatSelectUnselectFunction(
                                                                item = listsubitem[j]!!,
                                                                titleText = titleText,
                                                                bgColor = bgcolor ?: "FFFFFF",
                                                                isHisBookingStatus = isHisBookingStatus
                                                            )
                                                        }
                                                    }
                                                }

                                                } else {
                                                    if (selectedlistlower[0].available == false) {
                                                        if (selectedlistlower[0].available == false && listsubitem[j]?.available == false) {
                                                            seatSelectUnselectFunction(
                                                                item = listsubitem[j]!!,
                                                                titleText = titleText,
                                                                bgColor = bgcolor ?: "FFFFFF",
                                                                isHisBookingStatus = isHisBookingStatus
                                                            )
                                                        } else {
                                                            requireContext().toast(getString(R.string.this_seat_cannot_be_selected))
                                                        }
                                                    } else {
                                                        if (listsubitem[j]?.available == false) {
                                                            requireContext().toast(getString(R.string.this_seat_cannot_be_selected))
                                                        } else {
                                                            seatSelectUnselectFunction(
                                                                item = listsubitem[j]!!,
                                                                titleText = titleText,
                                                                bgColor = bgcolor ?: "FFFFFF",
                                                                isHisBookingStatus = isHisBookingStatus
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (::onSeatSelectionListener.isInitialized) {
                                onSeatSelectionListener.selectedSeatCount(selectedlistlower)
                            }
                        }
                    )

                }

            }
            if (isLower) {
                iscalledFirstTime = true

                if (iscalledFirstTime) {
                    if (listsubitem[j]?.isBlocked == false && !seatTextView.contains(titleText)) {
                        if (listsubitem[j]?.available != false && !seatTextView.contains(titleText)) {
                            seatTextView.add(titleText)
                        }
                    }
                }
            } else {
                iscalledFirstTime = true

                if (iscalledFirstTime) {
                    if (listsubitem[j]?.isBlocked == false && !seatTextView.contains(titleText)) {
                        if (listsubitem[j]?.available != false && !seatTextView.contains(titleText)) {
                            seatTextView.add(titleText)
                        }
                    }
                }
            }
        }
        iscalledFirstTime = false

        if (isLower == true) {
            seatInfoL = seatInfo
        } else {
            seatInfoU = seatInfo
        }


        layout.addView(gridLayout)


    }




    fun View.setOnSingleDoubleClickListener(
        doubleClickTime: Long = 300L,
        onDoubleClick: () -> Unit,
        onSingleClick: () -> Unit
    ) {
        var lastClickTime = 0L
        var isDoubleClick = false

        this.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastClickTime < doubleClickTime) {
                isDoubleClick = true
                onDoubleClick()
            } else {
                isDoubleClick = false
                this.postDelayed({
                    if (!isDoubleClick) {
                        onSingleClick()
                    }
                }, doubleClickTime)
            }

            lastClickTime = currentTime
        }
    }

    private fun proceedForShiftPassengerApi(
        sourceSeat: String,
        destinationSeat: String,
        ticketNumber: String,
        remarks: String,
        isSendSms: Boolean,
        privilegeResponseModel: PrivilegeResponseModel?
    ) {
        val authPinNeeded = privilegeResponseModel?.pinBasedActionPrivileges?.ticketShifting ?: false
        val pinSize = privilegeResponseModel?.pinCount ?: 6

        if (authPinNeeded && privilegeResponseModel?.country.equals("India", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = requireActivity(),
                fragmentManager = childFragmentManager,
                pinSize = pinSize,
                getString(R.string.shift_passengers),
                onPinSubmitted = { pin: String ->
                    hitShiftPassengerAPI(sourceSeat, destinationSeat, ticketNumber, remarks, isSendSms, pin)
                },
                onDismiss = null
            )
        } else {
            hitShiftPassengerAPI(sourceSeat, destinationSeat, ticketNumber, remarks, isSendSms, "")
        }
    }

    private fun hitShiftPassengerAPI(
        sourceSeat: String,
        destinationSeat: String,
        ticketNumber: String,
        remarks: String,
        isSendSms: Boolean,
        authPin: String
    ) {
        if (requireContext().isNetworkAvailable()) {
            val resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) ?: 0

            shiftPassengerViewModel.singleShiftPassengerAPI(
                com.bitla.ts.domain.pojo.singleShiftPassenger.request.ReqBody(
                    api_key = loginModelPref.api_key,
                    extra_seat_nos = "",
                    old_seat_numbers = sourceSeat,
                    remarks = remarks,
                    reservation_id = resId.toString(),
                    seat_count = "1",
                    seat_number = destinationSeat,
                    ticket_number = ticketNumber,
                    to_send_sms = if (isSendSms) "1" else "0",
                    partial_shift = true,
                    locale = locale,
                    is_bima_service = serviceDetailsData?.isBima ?: false,
                    auth_pin = authPin
                ),
                resend_otp_and_qr_code_method_name
            )
        } else requireContext().noNetworkToast()
    }

    private fun shiftPassengerObserver() {
        shiftPassengerViewModel.singleShiftPassengerResponse.observe(requireActivity()) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        DialogUtils.successfulMsgDialog(
                            requireContext(),
                            it.message
                        )
                        (activity as NewCoachActivity).callCoach = true
                        (activity as NewCoachActivity).callServiceApi()
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        requireContext().toast(it.message)
                    }
                }

            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun checkBookedSeat(
        seatList: List<SeatDetail>?,
        seatNumber: String?
    ) : Boolean {
        val seat = seatList?.find { it.number == seatNumber }
        seat?.let {
            if (it.available == false && it.isBlocked == false && it.isGangway == false && it.passengerDetails != null)
                return true
        }
        return false
    }

    private fun getTicketNumber(
        seatList: List<SeatDetail>?,
        seatNumber: String?
    ) : String {
        val seat = seatList?.find { it.number == seatNumber }
        seat?.passengerDetails?.let {
            return it.ticketNo?.substringBefore("(")?.trim() ?: ""
        }
        return ""
    }

    private fun checkShiftTicketPermission(
        seatList: List<SeatDetail>?,
        seatNumber: String?
    ) : Boolean {
        val seat = seatList?.find { it.number == seatNumber }
        seat?.passengerDetails?.let {
            return it.canShiftTicket == true
        }
        return false
    }

    private fun dragAndDropSeats(
        privilegeResponseModel: PrivilegeResponseModel?,
        titleText: TextView,
        seatnumber2: String?,
        seatDetail: SeatDetail
    ) {

        titleText.setOnLongClickListener { v ->
            val item = ClipData.Item(seatnumber2)

            getSourceSeatBlockingNumber(
                serviceDetailsData?.coachDetails?.seatDetails,
                seatnumber2!!
            )
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val dragData = ClipData("Text", mimeTypes, item)

            isBookedSeat = checkBookedSeat(serviceDetailsData?.coachDetails?.seatDetails, seatnumber2)
            canShiftTicket = checkShiftTicketPermission(serviceDetailsData?.coachDetails?.seatDetails, seatnumber2)

            if (isBookedSeat && canShiftTicket && role == getString(R.string.user)) {
                val myShadow = View.DragShadowBuilder(titleText)
                v.startDragAndDrop(dragData, myShadow, v, 0)

            } else if (!isBookedSeat && privilegeResponseModel?.tsPrivileges?.allowQuotaBlockingByDragDropInTheCoachLayout == true && loginModelPref.role != getString(R.string.role_field_officer)) {
                if (blockingNumber.isNotEmpty()) {
                    val myShadow = View.DragShadowBuilder(titleText)
                    v.startDragAndDrop(dragData, myShadow, v, 0)
                }
            } else {
                seatLongPressSelected(
                    item = seatDetail,
                )
                return@setOnLongClickListener true
            }
            false
        }

        titleText.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    true
                }

                DragEvent.ACTION_DROP -> {
                    val sourceView = event.localState as? TextView
                    val sourceText = sourceView?.text?.toString() ?: "Unknown"

                    val targetView = v as TextView
                    val targetText = targetView.text.toString()

                    val sourceSeat = getFirstPartFromText(sourceText)
                    val destinationSeat = getFirstPartFromText(targetText)

                    val isSeatAvailable = getSeatAvailability(
                        seatNumber = destinationSeat,
                        seatList = serviceDetailsData?.coachDetails?.seatDetails
                    )

                    if (sourceSeat != destinationSeat && isSeatAvailable) {
                        if (isBookedSeat && canShiftTicket && role == getString(R.string.user)) {
                            DialogUtils.shiftBookedSeatConfirmationDialog(
                                requireContext(),
                                getString(R.string.confirmShiftingPassenger),
                                sourceSeat,
                                destinationSeat,
                            ) { remarks, isSendSms ->
                                val ticketNumber = getTicketNumber(serviceDetailsData?.coachDetails?.seatDetails, sourceSeat)
                                proceedForShiftPassengerApi(
                                    sourceSeat = sourceSeat,
                                    destinationSeat = destinationSeat,
                                    ticketNumber = ticketNumber,
                                    remarks = remarks,
                                    isSendSms = isSendSms,
                                    privilegeResponseModel = privilegeResponseModel,
                                )
                            }
                        } else if (!isBookedSeat && privilegeResponseModel?.tsPrivileges?.allowQuotaBlockingByDragDropInTheCoachLayout == true && loginModelPref.role != getString(R.string.role_field_officer)) {
                            hitQuotaBlockSeatShiftApi(
                                blockingNumber = blockingNumber,
                                oldSeatNumber = sourceSeat,
                                newSeatNumber = destinationSeat,
                                apiKey = loginModelPref.api_key
                            )
                        }
                    }
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    true
                }

                else -> false
            }
        }
    }

    private fun getSeatAvailability(seatNumber: String, seatList: List<SeatDetail>?) : Boolean{

        val seat = seatList?.find { it?.number == seatNumber }
        seat.let {
            if (it != null) {
              if(it.available==false || it.isBlocked==true || it.isGangway==true){
                  return false
            }
        }

        return true

    }}


    private fun hitQuotaBlockSeatShiftApi(
        blockingNumber: String,
        oldSeatNumber: String,
        newSeatNumber: String,
        apiKey: String
    ) {
        moveQuotaBlockSeatViewModel.moveQuotaSeatApi(
            blockingNumber = blockingNumber,
            oldSeatNumber = oldSeatNumber,
            newSeatNumber = newSeatNumber,
            apiKey = apiKey
        )

    }

    fun getFirstPartFromText(text: String): String {
        val cleanedText = if (text.startsWith("\n")) {
            text.removePrefix("\n")
        } else {
            text
        }

        val parts = when {
            cleanedText.contains("\n") -> cleanedText.split("\n")
            cleanedText.contains("  ") -> cleanedText.split("\\s{2,}".toRegex()) // 2+ spaces
            cleanedText.contains(" ") -> cleanedText.split(" ")
            else -> listOf(cleanedText)
        }

        return if(parts[0].contains("(")){
            parts[0].substringBefore("(")
        }else{
            parts[0] ?: ""
        }
    }


    fun getSourceSeatBlockingNumber(
        seatList: List<SeatDetail>?,
        seatNumber: String
    ) {
        val seat = seatList?.find { it?.number == seatNumber }
        seat.let {
            if (it != null) {
                blockingNumber = it.blockingNumber
            }
        }
}

    private fun seatLongPressSelected(item: SeatDetail) {
        val blocked = item.isBlocked
        selectedBlockedSeatNo.clear()
        selectedBlockedSeatNo.add(item.number)

        if (blocked == true) {
            if (::onSeatSelectionListener.isInitialized) {
                onSeatSelectionListener.onSeatSelection(
                    selectedSeatDetails = selectedlistlower,
                    finalSeatNumber = selectedBlockedSeatNo,
                    totalSum = 0.0,
                    isAllSeatSelected = false,
                    isSeatLongPress = true
                )
            }
        }
    }

    private fun seatSelectUnselectFunction(
        item: SeatDetail,
        titleText: TextView,
        bgColor: String,
        isHisBookingStatus: Boolean = false
    ) {
        val available = item.available
        val blocked = item.isBlocked

        if (available != null && blocked != null) {
            if (!isAllowUsersToViewTicket && ((available == true && blocked == false) || (available == true && blocked == true)) && (activity is NewCoachActivity) || ((activity is ShiftPassengerActivity)) || (activity is SeatWiseFareActivity)){
                selectionFilter(titleText, item, bgColor)
            } else if (!isAllowUsersToViewTicket && (blocked == true && available == false) && (activity is NewCoachActivity) || (activity is ShiftPassengerActivity)) {
                selectionFilter(titleText, item, bgColor)
            } else {
                if (!isFromShiftPassenger && item.number != null) {
                    //New Booking Changes
                    val resId = if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
                        PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
                    else
                        PreferenceUtils.getString("reservationid")

                    moveToExtraSeatViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponseModel ->
                        if (isAgentLogin) {
                            if (privilegeResponseModel?.country.equals(
                                    "India",
                                    true
                                )
                            ) {
                                if (isOpenTooltip) {
                                    (activity as NewCoachActivity).hitMultistationSeatDetailApi(
                                        resId.toString(),
                                        item.number
                                    )
                                } else {
                                    requireActivity().toast(getString(R.string.seat_is_booked))
                                }

                                (activity as NewCoachActivity).setIsExtraSeat(false)
                            } else if (privilegeResponseModel?.country.equals("Indonesia", true)) {
                                if (privilegeResponseModel?.is_confirm_ota_booking == true) {
                                    if (isOpenTooltip) {
                                        (activity as NewCoachActivity).hitMultistationSeatDetailApi(
                                            resId.toString(),
                                            item.number
                                        )
                                        (activity as NewCoachActivity).setIsExtraSeat(false)
                                    } else {
                                        requireActivity().toast(getString(R.string.seat_is_booked))

                                    }

                                } else {
                                    if (isHisBookingStatus) {
                                        (activity as NewCoachActivity).hitMultistationSeatDetailApi(
                                            resId.toString(),
                                            item.number
                                        )
                                        (activity as NewCoachActivity).setIsExtraSeat(false)
                                    } else {
                                        requireContext().toast(getString(R.string.seat_is_booked))
                                    }
                                }
                            } else {
                                /*if (isHisBookingStatus){
                                    (activity as NewCoachActivity).hitMultistationSeatDetailApi(
                                        resId.toString(),
                                        item.number
                                    )
                                    (activity as NewCoachActivity).setIsExtraSeat(false)
                                }else{
                                    requireContext().toast(getString(R.string.seat_is_booked))
                                }*/
                                if (privilegeResponseModel?.is_confirm_ota_booking == true) {
                                    if (isOpenTooltip) {
                                        (activity as NewCoachActivity).hitMultistationSeatDetailApi(
                                            resId.toString(),
                                            item.number
                                        )
                                        (activity as NewCoachActivity).setIsExtraSeat(false)
                                    } else {
                                        if (isAdded) {
                                            requireActivity().toast(getString(R.string.seat_is_booked))
                                        }
                                    }

                                } else {
                                    if (isHisBookingStatus) {
                                        (activity as NewCoachActivity).hitMultistationSeatDetailApi(
                                            resId.toString(),
                                            item.number
                                        )
                                        (activity as NewCoachActivity).setIsExtraSeat(false)
                                    } else {
                                        requireContext().toast(getString(R.string.seat_is_booked))
                                    }
                                }
                            }
                        }
                        else {
                            if (isOpenTooltip) {
                                if (activity is NewCoachActivity) {
                                    (activity as NewCoachActivity).hitMultistationSeatDetailApi(
                                        resId.toString(),
                                        item.number
                                    )
                                    (activity as NewCoachActivity).setIsExtraSeat(false)
                                } else if (activity is CoachLayoutReportingActivity && item.passengerDetails != null) {
                                    (activity as CoachLayoutReportingActivity).hitMultistationSeatDetailApi(
                                        resId.toString(),
                                        item.number
                                    )
                                }
                            } else {
                                requireActivity().toast(getString(R.string.seat_is_booked))

                            }

                        }
                    }

                    /*if((privilegeResponseModel.is_confirm_ota_booking == true && country.equals("Indonesia",true))  || (isAgentLogin && isHisBookingStatus)) {
                        (activity as NewCoachActivity).hitMultistationSeatDetailApi(
                            resId,
                            item.number!!
                        )
                        (activity as NewCoachActivity).setIsExtraSeat(false)
                    }
                    else
                        requireContext().toast(getString(R.string.seat_is_booked))*/

                }
            }
        }
    }

    private fun seatSelectAction(
        titleText: TextView,
        item: SeatDetail,
        isHisBooking: Boolean,
        bgColor: String
    ) {
        if (!finalSeatNumbers.contains(item.number)) {
            selectedSeats.add(titleText)
            selectedlistlower.add(item)
            finalSeatNumbers.add(item.number)
            totalSeatPrice.add(item.baseFareFilter)
            titleText.setTextColor(resources.getColor(R.color.black))
            if (item.isBlocked == true && item.available == false) {

                if (item.isBlocked == true && !isAgentLogin) {
                    if (selectedColor == "#FFFFFF") {
                        colorLockedOutline(
                            isHisBooking = isHisBooking,
                            isHorizontal = item.isHorizontal ?: false,
                            titleText = titleText,
                            isBerth = item.isBerth ?: false
                        )
                    } else {
                        colorLocked(
                            isHisBooking = isHisBooking,
                            seatColor = selectedColor ?: "",
                            isHorizontal = item.isHorizontal ?: false,
                            titleText = titleText,
                            isBerth = item.isBerth ?: false
                        )
                    }
                } else {
                    if (selectedColor?.contains("#FFFFFF") == true) {
                        colorOutline(
                            isHisBooking = isHisBooking,
                            isHorizontal = item.isHorizontal ?: false,
                            titleText = titleText,
                            isBerth = item.isBerth ?: false
                        )
                    } else {
                        colorSolid(
                            isHisBooking = isHisBooking,
                            seatColor = selectedColor ?: "",
                            isHorizontal = item.isHorizontal ?: false,
                            titleText = titleText,
                            isBerth = item.isBerth ?: false
                        )
                    }
                }
            } else {

                if (selectedColor == "#FFFFFF") {
                    colorOutline(
                        isHisBooking = isHisBooking,
                        isHorizontal = item.isHorizontal ?: false,
                        titleText = titleText,
                        isBerth = item.isBerth ?: false
                    )
                } else {
                    colorSolid(
                        isHisBooking = isHisBooking,
                        seatColor = selectedColor ?: "",
                        isHorizontal = item.isHorizontal ?: false,
                        titleText = titleText,
                        isBerth = item.isBerth ?: false
                    )
                }
            }
        } else {
            //Timber.d("isBlocked ${item.isBlocked}")
            seatUnSelectAction(titleText, item, isHisBooking, bgColor)
        }


        val layoutParams = binding.seatlayout.layoutParams as ViewGroup.MarginLayoutParams

        val marginInDp = 32
        val scale = resources.displayMetrics.density
        val marginInPx = (marginInDp * scale + 0.5f).toInt()

        if (isMarginApplied) {
            layoutParams.bottomMargin = 0
        } else {
            layoutParams.bottomMargin = marginInPx
        }

        binding.seatlayout.layoutParams = layoutParams
        isMarginApplied = !isMarginApplied

    }

    private fun seatUnSelectAction(
        titleText: TextView,
        item: SeatDetail,
        isHisBooking: Boolean,
        bgcolor: String
    ) {
        selectedlistlower.remove(item)
        finalSeatNumbers.remove(item.number)
        selectedSeats.remove(titleText)
        totalSeatPrice.remove(item.baseFareFilter)
        titleText.setTextColor(resources.getColor(R.color.black))
        if (item.isBlocked == true && item.available == false) {

            if (item.isBlocked == true && !isAgentLogin) {
                if (bgcolor == "#FFFFFF") {
                    colorLockedOutline(
                        isHisBooking = isHisBooking,
                        isHorizontal = item.isHorizontal ?: false,
                        titleText = titleText,
                        isBerth = item.isBerth ?: false
                    )
                } else {
                    colorLocked(
                        isHisBooking = isHisBooking,
                        seatColor = bgcolor,
                        isHorizontal = item.isHorizontal ?: false,
                        titleText = titleText,
                        isBerth = item.isBerth ?: false
                    )
                }
            } else {
                if (bgcolor.contains("#FFFFFF")) {
                    colorOutline(
                        isHisBooking = isHisBooking,
                        isHorizontal = item.isHorizontal ?: false,
                        titleText = titleText,
                        isBerth = item.isBerth ?: false
                    )
                } else {
                    colorSolid(
                        isHisBooking = isHisBooking,
                        seatColor = bgcolor,
                        isHorizontal = item.isHorizontal ?: false,
                        titleText = titleText,
                        isBerth = item.isBerth ?: false
                    )
                }
            }
        } else {
            if (bgcolor.contains("#FFFFFF")) {
                colorOutline(
                    isHisBooking = isHisBooking,
                    isHorizontal = item.isHorizontal ?: false,
                    titleText = titleText,
                    isBerth = item.isBerth ?: false
                )
            } else {
                colorSolid(
                    isHisBooking = isHisBooking,
                    seatColor = bgcolor,
                    isHorizontal = item.isHorizontal ?: false,
                    titleText = titleText,
                    isBerth = item.isBerth ?: false
                )
            }
        }
    }

    private fun selectionFilter(titleText: TextView, item: SeatDetail, bgColor: String) {
        var isHisBookingStatus = false
        if (item.passengerDetails != null) {
            isHisBookingStatus = item.passengerDetails?.isHisBooking == true
        } else {
            isHisBookingStatus = item.isHisBooking ?: false
        }

        if (item.isimage == true) {
            requireContext().toast(getString(R.string.this_seat_cannot_be_selected))
        } else if (selectedlistlower.contains(item)) {
            seatUnSelectAction(titleText, item, isHisBookingStatus, bgColor)
        } else {
            seatSelectAction(titleText, item, isHisBookingStatus, bgColor)
        }
        var total = 0.0
        if (!isFromChile) {
            if (finalSeatNumbers.size >= 1) {
                binding.chkAllSeats.isChecked = false
                binding.chkAllSeats.gone()
                extraSelected = false
                onSeatSelectionListener.bookExtraSeats(false)
            } else {
                privilegeForExtraSeat()
            }
        }
        for (q in 0..finalSeatNumbers.size.minus(1)) {
            var sum: Double = totalSeatPrice[q].toString().toDouble()
            sum.toString().toDouble()
            total += sum
        }
        item.editFare = null
        if (::onSeatSelectionListener.isInitialized) {
            onSeatSelectionListener.onSeatSelection(
                selectedlistlower,
                finalSeatNumbers,
                total,
                false
            )
        }
    }

    private fun isImageSeat(seatNumber: String): Boolean = ((seatNumber.contains("DR_IMG", true)
            || seatNumber.contains("TV_IMG", true)
            || seatNumber.contains("PA_IMG", true)
            || seatNumber.contains("WR_IMG", true)
            || seatNumber.contains("SM_IMG", true)
            || seatNumber.contains("ST_IMG", true))
            )

    private fun seatText(
        seatNumber: String,
        isEditSeatWise: Boolean,
        isLadies: Boolean,
        isShifted: Boolean,
        isUpdated: Boolean,
        isMulti: Boolean,
        isBoarded: Boolean,
        inJourney: Boolean,
        showFare: Boolean,
        titleText: TextView,
        roundOff: String,
        spannableEdit: SpannableString,
        status: String,
        listItem: SeatDetail
    ) {
        if (isImageSeat(seatNumber)) {
            listItem.isimage = true
            driverImage(seatNumber ?: "", titleText)
        } else {
            val seatTextArray = arrayListOf<String>()
            val seatSymbol = arrayListOf<String>()

            if (isLadies) {
                seatTextArray.add("(F)")
            }
            //this.showFare = false

            moveToExtraSeatViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponseModel ->

                if (showFare) {
                    if ((loginModelPref?.role == getString(R.string.role_field_officer) || loginModelPref?.role == getString(R.string.role_chairman)) &&
                        privilegeResponseModel?.country.equals("India", true)) {

                        hideFareCondition(listItem.available, listItem.passengerDetails, listItem.isBlocked, seatTextArray, roundOff)

                    } else {
                        seatTextArray.add("\n$currency$roundOff")
                    }
                }
                if (isEditSeatWise) {
                    seatTextArray.add("\n  $spannableEdit")
                }
                if (isBoarded) {
                    seatSymbol.add("✔")
                }
                if (isShifted) {
                    seatSymbol.add("S")
                }
                if (isMulti) {
                    seatSymbol.add("+")
                }
                if (isUpdated) {
                    seatSymbol.add("*")
                }
                if (inJourney) {
                    seatSymbol.add("#")
                }

                if (status == "9") {
                    seatSymbol.add("↓")
                }

                var text = seatNumber

                seatTextArray.forEach {
                    text = text + it
                }

                if (seatSymbol.isNotEmpty()) {
                    text = text + "\n"
                    seatSymbol.forEach {
                        text = text + it
                    }
                }
                if (listItem.isBerth == true) {
                    text = "\n" + text
                } else {
                    if (seatSymbol.isNotEmpty() || showFare) {
                    } else {
                        text = "\n" + text
                    }
                }

                titleText.text = text
                if (listItem.multiHopSeat) {
                    text += "\n" + "*"
                    blinkingSpanString(text, titleText)
                }
            }

        }
    }

    private fun hideFareCondition(available: Boolean?, passengerDetails: PassengerDetails?, isBlocked: Boolean?,
                                  seatTextArray: ArrayList<String>, roundOff: String) {
        when {
            // Condition for setting fare for non-booked and non-blocked seats
            (hideFareBookedSeats && available == true && passengerDetails == null) && (blockedSeatsHideFare && isBlocked == false) -> {
                seatTextArray.add("\n$currency$roundOff")
            }

            // Condition for setting fare for non-booked seats
            hideFareBookedSeats && (available == true || isBlocked == true) && passengerDetails == null && !blockedSeatsHideFare -> {
                seatTextArray.add("\n$currency$roundOff")
            }

            // Condition for setting fare for non-blocked seats
            !hideFareBookedSeats && blockedSeatsHideFare && isBlocked == false -> {
                seatTextArray.add("\n$currency$roundOff")
            }

            // Condition for setting fare for all seats
            !hideFareBookedSeats && !blockedSeatsHideFare -> {
                seatTextArray.add("\n$currency$roundOff")
            }

        }
    }

    fun updateFromCoach() {
        isUpdateFromCoach = true
    }

    fun toggleExtraSeatsButtonVisibility(isVisible: Boolean) {
        if(isVisible) {
            binding.chkAllSeats.visible()
        } else {
            binding.chkAllSeats.gone()
        }
    }
}