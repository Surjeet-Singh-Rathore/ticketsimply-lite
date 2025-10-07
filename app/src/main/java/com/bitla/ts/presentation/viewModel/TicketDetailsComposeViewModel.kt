package com.bitla.ts.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.phonepe.PhonePeStatusResponse
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.ticket_details.response.InsuranceDetails
import com.bitla.ts.domain.pojo.ticket_details_menu.Body
import com.bitla.ts.domain.pojo.ticket_details_menu.TicketDetailsMenu
import com.bitla.ts.domain.pojo.ticket_details_phase_3.TicketDetailsSideBarOptionsModel
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.BoardingDetails
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.DropOffDetails
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.PartialPaymentDetails
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.PassengerDetail
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.RefundType
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.TicketDetailsResponse
import com.bitla.ts.domain.repository.TicketDetailsRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.ResourceProvider
import com.bitla.ts.utils.common.getPhoneNumber
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketDetailsComposeViewModel<T : Any?>(private val ticketDetailsRepository: TicketDetailsRepository) :
    ViewModel() {
     var bookingSource: String? = null
    private var ticketStatusCancelled: String? = null
    private var ticketStatusPending: String? = null
    private var ticketStatusBook: String? = null
    private var ticketStatusSeatBooked: String? = null
    private var ticketStatusPhoneBlocked: String? = null
    var swipeToRefresh by mutableStateOf(false)
    var showRootProgressBar by mutableStateOf(false)
    var showWhiteBackgroundInProgressBar by mutableStateOf(true)
    var showTicketDetailsSideBarMenu by mutableStateOf(false)


    var showTicketDetails by mutableStateOf(false)
    var showTicketDetailsButtonText by mutableStateOf("Hide Details")
    var showRebookButton by mutableStateOf(false)
    var showNewBookingButton by mutableStateOf(true)
    var pnrNumber by mutableStateOf("")
    var travelDate by mutableStateOf("")
    var bookingType by mutableStateOf("")
    var ticketBookedBy by mutableStateOf("")
    var boardingTime by mutableStateOf("")
    var boardingDate by mutableStateOf("")
    var boardingLocation by mutableStateOf("")
    var bAddress by mutableStateOf("")
    var dAddress by mutableStateOf("")
    var duration by mutableStateOf("")
    var droppingTime by mutableStateOf("")
    var droppingDate by mutableStateOf("")
    var droppingLocation by mutableStateOf("")
    var passengerName by mutableStateOf("")
    var passengerAge by mutableStateOf("")
    var passengerGender by mutableStateOf("")
    var passengerMobileNumber by mutableStateOf("")
    var bookingAmount by mutableStateOf("")
    var totalNetAmount by mutableStateOf("")
    var transactionFare by mutableStateOf("")
    var bookedSeats by mutableStateOf("")
    var noOfSeats by mutableStateOf(0)
    var boardingPointContactNumber by mutableStateOf("")
    var vehicleDetails by mutableStateOf("")
    var serviceNumber by mutableStateOf("")
    var smsTicketHash by mutableStateOf("")
    var sharingPDFLink by mutableStateOf("")
    var printCount by mutableIntStateOf(0)

    var country by mutableStateOf("")
    var countryCode by mutableStateOf("")
    var ticketDetailsPassMobNumber by mutableStateOf("")
    var currency by mutableStateOf("")
    var currencyFormat by mutableStateOf("")
    var originId by mutableStateOf("")
    var origin by mutableStateOf("")
    var destinationId by mutableStateOf("")
    var destination by mutableStateOf("")
    var boardingStageID by mutableStateOf("")
    var droppingStageID by mutableStateOf("")
    var busType by mutableStateOf("")
    var ticketStatus by mutableStateOf("")
    var totalFare by mutableStateOf("")
    var remarks by mutableStateOf("")
    var terminalRefNo by mutableStateOf("")
    var qrCode by mutableStateOf("")
    var totalInsuranceAmt by mutableDoubleStateOf(0.0)

    var passengerDetails = mutableStateListOf<PassengerDetail?>()
    var sideBarOptionsList = mutableStateListOf<TicketDetailsSideBarOptionsModel>()

    var isUpdateTicket by mutableStateOf(false)
    var canShiftTicket by mutableStateOf(false)
    var canCancel by mutableStateOf(false)
    var canCancelTicketForUser by mutableStateOf(false)
    var canCancelTicketForAgent by mutableStateOf(false)
    var canConfirmPhoneBlock by mutableStateOf(false)
    var isAllowCancellationTypeAsFixedOrPercentage by mutableStateOf(false)
    var isAllowToAlterCancelPercent by mutableStateOf(false)
    var isETicket by mutableStateOf(false)
    var isOnBehalfOnlineTicket by mutableStateOf(false)
    var isZeroPercentCancellation by mutableStateOf(false)
    var refundTypes = mutableStateListOf<RefundType>()
    var isCancelledTicket by mutableStateOf(false)
    var pickupAddress by mutableStateOf("")
    var dropoffAddress by mutableStateOf("")
    var pickupCharge by mutableStateOf(0.0)
    var dropoffCharge by mutableStateOf(0.0)
    var isPickupDropoffChargesEnabled by mutableStateOf(false)

    var barcodeValue by mutableStateOf("")
    var isAllowToPrintBarcode by mutableStateOf(false)
    var reservationId by mutableLongStateOf(0L)
    var tripCount by mutableStateOf("")
    var isBimaTicket by mutableStateOf(false)

    private val _updatePrintCountData = MutableLiveData<PhonePeStatusResponse>()
    val updatePrintCountData: LiveData<PhonePeStatusResponse>
        get() = _updatePrintCountData

    private val _showTicketDetailsSideBarMenu = MutableLiveData<Boolean>()
    val showHideMenuFromOtherFragment : LiveData<Boolean>
        get() = _showTicketDetailsSideBarMenu

    var partialPaymentDetails = mutableStateOf(
        PartialPaymentDetails(
            "", "", ""
        )
    )




    var boardingDetails = mutableStateOf(
        BoardingDetails(
            "",
            "",
            "",
            "",
            "",
            0,
            "",
            ""
        )
    )

    var insuranceTransDetails = mutableStateOf(
        InsuranceDetails(
            mutableStateListOf(),
            ""
        )
    )

    var dropOffDetails = mutableStateOf(
        DropOffDetails(
            "",
            "",
            0,
            "",
            ""
        )
    )
    var seatNumbers by mutableStateOf("")
    var notAvailable by mutableStateOf(ResourceProvider.TextResource.fromStringId(R.string.notAvailable))

    var serviceBy by mutableStateOf("")
    var coachNumber by mutableStateOf("")
    var passengerCategory by mutableStateOf("")

    var ticketDetailsMenuOptions by mutableStateOf<Body?>(null)
    var headerBgColor by mutableStateOf<Color?>(null)
    var ticketStatusIcon by mutableStateOf<Int?>(null)
    var ticketStatusTitle by mutableStateOf<String?>(null)
    var isPayAtBusTicket by mutableStateOf(false)
    var allowBluetoothPrint by mutableStateOf(false)
    var showInsuranceDetails by mutableStateOf(false)
    val mealCouponList = mutableStateListOf<String>()
    val mealTypeList = mutableStateListOf<String>()
    var shareTicketOnWhatsapp by mutableStateOf(false)
    var isPartialTicket by mutableStateOf(false)

    val messageSharedFlow = MutableSharedFlow<String>()
    var isTicketDetailsApiSuccess by mutableStateOf<Boolean?>(null)


    private var apiType: String? = null
    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _dataTicketDetails = MutableLiveData<TicketDetailsResponse>()
    val dataTicketDetails: LiveData<TicketDetailsResponse>
        get() = _dataTicketDetails

    private val _dataTicketDetailsMenus = MutableLiveData<TicketDetailsMenu>()
    val dataTicketDetailsMenus: LiveData<TicketDetailsMenu>
        get() = _dataTicketDetailsMenus


    fun ticketDetailsApi(
        apiKey: String,
        ticketNumber: String,
        jsonFormat: Boolean,
        isQrScan: Boolean,
        locale: String,
        apiType: String,
        loadPrivs: Boolean,
        menuPrivilege: Boolean
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            ticketDetailsRepository.ticketDetailsPhase3(
                apiKey,
                ticketNumber, jsonFormat, isQrScan, locale, loadPrivs, menuPrivilege
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _dataTicketDetails.postValue(
                            it.data
                        )
                    }

                    is NetworkProcess.Failure -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        messageSharedFlow.emit(it.message)
                    }
                }
            }


        }
    }


    fun showHideSideMenuBar(b : Boolean) {
        _showTicketDetailsSideBarMenu.postValue(b)

    }

    fun ticketDetailsMenus(
        apiKey: String,
        ticketNumber: String,
        jsonFormat: Boolean,
        locale: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            ticketDetailsRepository.ticketDetailsMenus(
                apiKey,
                ticketNumber, jsonFormat, locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _dataTicketDetailsMenus.postValue(
                            it.data
                        )
                    }

                    is NetworkProcess.Failure -> {
                        messageSharedFlow.emit(it.message)
                    }
                }
            }

        }
    }

    fun setTicketDetailsData(
        ticketDetailsResponse: TicketDetailsResponse,
        notAvailableString: String
    ) {

        swipeToRefresh = false
        isPayAtBusTicket = ticketDetailsResponse.body?.isPayAtBusTicket ?: false
        boardingStageID = (ticketDetailsResponse.body?.boardingDetails?.stageId ?: 0).toString()
        droppingStageID = (ticketDetailsResponse.body?.dropOffDetails?.stageId ?: 0).toString()
        pnrNumber = ticketDetailsResponse.body?.ticketNumber ?: notAvailableString
        reservationId = ticketDetailsResponse.body?.reservationId ?: 0L
        bookingSource = ticketDetailsResponse.body?.bookingSource ?: ""
        travelDate = ticketDetailsResponse.body?.travelDate ?: notAvailableString
        bookingType = ticketDetailsResponse.body?.bookingSource ?: notAvailableString
        ticketBookedBy =
            ticketDetailsResponse.body?.ticketBookedBy ?: ticketDetailsResponse.body?.issuedBy ?: ""
        boardingTime = ticketDetailsResponse.body?.boardingDetails?.depTime ?: notAvailableString
        boardingDate = ticketDetailsResponse.body?.boardingDetails?.travelDate ?: notAvailableString
        isETicket = ticketDetailsResponse.body?.isEticket ?: false
        isBimaTicket = ticketDetailsResponse.body?.isBimaTicket ?: false
        printCount = ticketDetailsResponse.body?.printCount ?: 0


        boardingLocation = "${
            ticketDetailsResponse.body?.origin ?: notAvailableString
        }, ${
            ticketDetailsResponse.body?.boardingDetails?.stageName ?: ticketDetailsResponse.body?.boardingDetails?.address ?: notAvailableString
        }"
        bAddress =
            ticketDetailsResponse.body?.boardingDetails?.stageName
                ?: ticketDetailsResponse.body?.boardingDetails?.address ?: notAvailableString
        dAddress =
            ticketDetailsResponse.body?.dropOffDetails?.stageName
                ?: ticketDetailsResponse.body?.dropOffDetails?.address ?: notAvailableString
        duration =
            ticketDetailsResponse.body?.duration?.replace(":", "h ")?.plus("m") ?: ""
        droppingTime =
            ticketDetailsResponse.body?.dropOffDetails?.arrTime ?: notAvailableString
        droppingDate = ticketDetailsResponse.body?.dropOffDetails?.travelDate ?: notAvailableString
        droppingLocation = "${ticketDetailsResponse.body?.destination ?: notAvailableString}, ${
            ticketDetailsResponse.body?.dropOffDetails?.stageName ?: notAvailableString
        }"
        passengerName = ticketDetailsResponse.body?.passengerDetails?.get(0)?.name
            ?: notAvailableString
        passengerAge =
            "${ticketDetailsResponse.body?.passengerDetails?.get(0)?.age ?: notAvailableString}"

        passengerMobileNumber = ticketDetailsResponse.body?.passengerDetails?.get(0)?.mobile
            ?: ""
        bookingAmount = ticketDetailsResponse.body?.totalFare ?: ""
        transactionFare = ticketDetailsResponse.body?.transactionFare ?: ""
        totalNetAmount = ticketDetailsResponse.body?.totalNetAmount ?: ""
        bookedSeats = ticketDetailsResponse.body?.seatNumbers ?: notAvailableString
        noOfSeats = ticketDetailsResponse.body?.noOfSeats ?: 0
        boardingPointContactNumber = getPhoneNumber(
            ticketDetailsResponse.body?.boardingDetails?.contactNumbers ?: "",
            country
        )
        vehicleDetails =
            "${ticketDetailsResponse.body?.coachNumber ?: notAvailableString} | ${
                ticketDetailsResponse.body?.busType ?: notAvailableString
            }"
        serviceNumber = ticketDetailsResponse.body?.serviceNumber ?: notAvailableString
        smsTicketHash = ticketDetailsResponse.body?.smsTicketHash ?: notAvailableString
        sharingPDFLink = ticketDetailsResponse.body?.sharingPdfLink ?: notAvailableString
        seatNumbers = ticketDetailsResponse.body?.seatNumbers ?: notAvailableString
        originId = "${ticketDetailsResponse.body?.originId ?: 0}"
        origin = ticketDetailsResponse.body?.origin ?: notAvailableString
        destinationId = "${ticketDetailsResponse.body?.destinationId ?: 0}"
        destination = ticketDetailsResponse.body?.destination ?: notAvailableString
        busType = ticketDetailsResponse.body?.busType ?: notAvailableString
        ticketStatus = ticketDetailsResponse.body?.ticketStatus ?: notAvailableString
        totalFare = ticketDetailsResponse.body?.totalFare ?: notAvailableString


        remarks = ticketDetailsResponse.body?.remarks ?: notAvailableString
        terminalRefNo = ticketDetailsResponse.body?.terminalRefNo ?: notAvailableString
        qrCode = ticketDetailsResponse.body?.terminalRefQrCode ?: notAvailableString

        serviceBy = ticketDetailsResponse.body?.serviceBy ?: notAvailableString
        coachNumber = ticketDetailsResponse.body?.coachNumber ?: notAvailableString
        passengerCategory = ticketDetailsResponse.body?.passengerDetails?.get(0)?.passengerCategory ?: notAvailableString

        barcodeValue = ticketDetailsResponse.body?.barcodeValue ?: notAvailableString
        isAllowToPrintBarcode = ticketDetailsResponse.body?.isAllowToPrintBarcode ?: false
        tripCount = ticketDetailsResponse.body?.tripCounts ?: ""

        passengerDetails = ticketDetailsResponse.body?.passengerDetails?.toMutableStateList()
            ?: mutableStateListOf()

        partialPaymentDetails.value =
            ticketDetailsResponse.body?.partialPaymentDetails ?: PartialPaymentDetails(
                "", "", ""
            )
        boardingDetails.value = ticketDetailsResponse.body?.boardingDetails ?: BoardingDetails(
            "", "", "", "", "", 0, "", ""
        )

        dropOffDetails.value = ticketDetailsResponse.body?.dropOffDetails ?: DropOffDetails(
            "", "", 0, "", ""
        )

        insuranceTransDetails.value =
            ticketDetailsResponse.body?.insuranceTransDetails ?: InsuranceDetails(
                mutableListOf(), ""
            )

        setMealData()
        setMobileWithCountryCode()        //for setting country code with mobile number


    }

    fun setMobileWithCountryCode() {
        ticketDetailsPassMobNumber = if (isETicket) {
            if (passengerMobileNumber.substringBefore("-").contains("+")) {
                "${passengerMobileNumber.substringBefore("-")}-${
                    passengerMobileNumber.substringAfter(
                        "-"
                    )
                }"
            } else {
                "+${passengerMobileNumber.substringBefore("-")}-${
                    passengerMobileNumber.substringAfter(
                        "-"
                    )
                }"
            }
        } else {

            // if(country.equals("india",true)){
            val phNumber = getPhoneNumber(passPhone = passengerMobileNumber, country)
            "${countryCode}-${phNumber}"

            //}

        }
    }


    fun nullSafeString(input: String?): ResourceProvider.TextResource {

        return ResourceProvider.TextResource.fromText(input ?: "")
    }


    fun setMenusAction(body: Body) {
        ticketDetailsMenuOptions = body
        smsTicketHash = ticketDetailsMenuOptions?.sms_ticket_hash ?: ""
        sharingPDFLink = ticketDetailsMenuOptions?.sharing_pdf_link ?: ""
    }

    fun resetMenuAction() {
        ticketDetailsMenuOptions = null
        smsTicketHash = ""
        sharingPDFLink = ""
        shareTicketOnWhatsapp = false
        sideBarOptionsList.clear()
    }

    fun setTicketStatus(
        cancelled: String,
        pending: String,
        phoneBlocked: String,
        booked: String,
        seatBooked: String
    ) {
        ticketStatusCancelled = cancelled
        ticketStatusPending = pending
        ticketStatusSeatBooked = seatBooked
        ticketStatusBook = booked
        ticketStatusPhoneBlocked = phoneBlocked
    }

    fun setIsTicketDetailsApiSuccess(isSuccess: Boolean?) {
        isTicketDetailsApiSuccess = isSuccess
    }

    fun updatePrintCountApi(
        pnrNumber: String,
        isUpdatePrintCount: Boolean,
        apiKey: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            ticketDetailsRepository.updatePrintCountApi(
                pnrNumber,isUpdatePrintCount,apiKey
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _updatePrintCountData .postValue(
                            it.data
                        )
                }

                is NetworkProcess.Failure -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    messageSharedFlow.emit(it.message)
                }
            }
        }

        }
    }

    fun updateTicketBtnVisibility(): Boolean {
        return ticketDetailsMenuOptions?.is_update_ticket == true && _dataTicketDetails.value?.body?.ticketStatus?.equals(
            ticketStatusCancelled,
            true
        ) == false
    }

    fun cancelTicketBtnVisibility(): Boolean {
        return ticketDetailsMenuOptions?.can_cancel == true && _dataTicketDetails.value?.body?.partialPaymentDetails == null && _dataTicketDetails.value?.body?.ticketStatus?.equals(
            ticketStatusPending,
            true
        ) == false && PreferenceUtils.getSubAgentRole() != "true"
    }

    /*fun shiftTicketBtnVisibility(privilegeResponseModel: PrivilegeResponseModel): Boolean {
        return privilegeResponseModel.country.equals(
            "Indonesia",
            true
        ) && _dataTicketDetails.value?.body?.insuranceTransDetails?.details?.isEmpty() == true && ticketDetailsMenuOptions?.can_shift_ticket == true && _dataTicketDetails.value?.body?.ticketStatus?.equals(
            ticketStatusCancelled,
            true
        ) == false
    }*/

    fun shiftTicketBtnVisibility(privilegeResponseModel: PrivilegeResponseModel): Boolean {
        return (_dataTicketDetails.value?.body?.insuranceTransDetails == null || _dataTicketDetails.value?.body?.insuranceTransDetails?.details?.isEmpty() == true) && ticketDetailsMenuOptions?.can_shift_ticket == true && _dataTicketDetails.value?.body?.ticketStatus?.equals(
            ticketStatusCancelled,
            true
        ) == false
    }

    fun confirmTicketBtnVisibility(privilegeResponseModel: PrivilegeResponseModel): Boolean {
        return (_dataTicketDetails.value?.body?.ticketStatus?.equals(
            ticketStatusBook,
            true
        ) == false && _dataTicketDetails.value?.body?.ticketStatus?.equals(
            ticketStatusPhoneBlocked,
            true
        ) == false && (privilegeResponseModel?.country.equals(
            "Indonesia",
            true
        ) || privilegeResponseModel?.country.equals(
            "Vietnam",
            true
        ) ) && _dataTicketDetails.value?.body?.isPayAtBusTicket == true && _dataTicketDetails.value?.body?.isConfirmOtaBooking == true) || (_dataTicketDetails.value?.body?.ticketStatus?.equals(
            ticketStatusPending,
            true
        ) == true && ticketDetailsMenuOptions?.can_confirm_phone_block == true)
    }

    fun releaseTicketBtnVisibility(privilegeResponseModel: PrivilegeResponseModel): Boolean {
        return privilegeResponseModel.country.equals(
            "Indonesia",
            true
        )
                && ticketDetailsMenuOptions?.can_shift_ticket == true && _dataTicketDetails.value?.body?.isPayAtBusTicket == true && _dataTicketDetails.value?.body?.partialPaymentDetails == null && _dataTicketDetails.value?.body?.ticketStatus?.equals(
            ticketStatusBook,
            true
        ) == false && _dataTicketDetails.value?.body?.ticketStatus?.equals(
            ticketStatusPhoneBlocked,
            true
        ) == false && _dataTicketDetails.value?.body?.ticketStatus?.equals(
            ticketStatusPending,
            true
        ) == true && privilegeResponseModel.allowToReleaseApiTentativeBlockedTickets == true
    }

    fun printTicketBtnVisibility(): Boolean {

        return !(ticketStatus.equals(
            ticketStatusPending,
            true
        ) || ticketStatus.equals(
            ticketStatusCancelled,
            true
        ) || isPartialTicket)
    }

    fun whatsappShareTicketBtnVisibility(): Boolean {
        return !ticketStatus.equals(ticketStatusCancelled, true)
    }

    fun smsBtnVisibility(): Boolean {
        return !ticketStatus.equals(ticketStatusCancelled, true)
    }

    fun emailBtnVisibility(): Boolean {
        return !ticketStatus.equals(ticketStatusCancelled, true) && !isBimaTicket
    }

    fun shareTicketVisibility(): Boolean {
        return !ticketStatus.equals(ticketStatusCancelled, true)
    }

    fun setMealData() {

        mealCouponList.clear()
        mealTypeList.clear()

        if (passengerDetails.isNotEmpty()) {
            var mealCoupons = ""
            var mealTypes = ""
            passengerDetails.forEach {
                if (it?.mealCoupons != null && it.mealCoupons.isNotEmpty()) {
                    mealCoupons += it.mealCoupons.toString().replace("[", "").replace("]", "")
                        .replace(",", "\n").replace(" ", "")
                    mealCouponList.add(mealCoupons)
                    mealCoupons = ""
                }

                if (!it?.selectedMealType.isNullOrEmpty() && it?.selectedMealType != "-") {
                    mealTypes += it?.selectedMealType
                    mealTypeList.add(mealTypes)
                    mealTypes = ""
                }
            }
        }
    }
}