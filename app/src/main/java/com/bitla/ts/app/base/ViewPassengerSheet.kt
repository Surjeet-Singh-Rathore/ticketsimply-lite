package com.bitla.ts.app.base

import android.annotation.*
import android.app.*
import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.text.*
import android.view.*
import android.view.inputmethod.*
import android.widget.*
import androidx.activity.result.contract.*
import androidx.core.content.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.bulk_ticket_update.request.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.ticket_details.response.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.dashboard.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.bottomsheet.*
import com.google.gson.*
import gone
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.koin.androidx.viewmodel.ext.android.*
import setMaxLength
import timber.log.*
import toast
import visible
import java.io.*
import kotlin.Pair
import kotlin.math.roundToInt

import com.bitla.ts.app.base.BaseCancelUpdateApis.UserListApiHelper
import com.bitla.ts.app.base.BaseCancelUpdateApis.CancellationDetailsApiHelper
import com.bitla.ts.app.base.BaseCancelUpdateApis.TicketDetailsApiHelper
import com.bitla.ts.app.base.BaseCancelUpdateApis.ZeroCancellationDetailsApiHelper
import com.bitla.ts.app.base.BaseCancelUpdateApis.CancelPartialTicketApiHelper
import com.bitla.ts.app.base.BaseCancelUpdateApis.ConfirmOtpCancelPartialTicketApiHelper
import com.bitla.ts.app.base.BaseCancelUpdateApis.BulkTicketUpdateApiHelper
import com.bitla.ts.app.base.BaseCancelUpdateApis.Companion.showCountryPickerBottomsheet
import com.bitla.ts.app.base.BaseCancelUpdateApis.Companion.updateTwoButtonDialog
import com.bitla.ts.app.base.BaseCancelUpdateApis.ServiceApiHelper
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.sharedPref.PreferenceUtils

/**
 * Created by Taiyab Ali on 20-Aug-21.
 * */

@Suppress("UNCHECKED_CAST")
open class ViewPassengerSheet : BaseFragment(), DialogButtonListener, OnItemClickListener,
    OnItemCheckListener, DialogButtonTagListener,
    BaseActivity.UpdateBulkTicketInterface, BaseActivity.UpdateSingleTicketInterface,
    BaseActivity.CancelTicketTicketInterface,
    BaseActivity.ViewPassengersSheetInterface, DialogReturnDialogInstanceListener,
    DialogAnyClickListener {

    private lateinit var cancelDialogBinding: DialogCancelTicketBinding
    private var showIsdCountryCode: Boolean = false
    private var isSingleUpdate: Boolean = false

    private lateinit var countryPickerDialog: BottomSheetDialog
    private lateinit var sendUpdateDataListener: DialogButtonAnyDataListener
    private lateinit var _sheetTicketCancellationBinding: SheetTicketCancellationBinding
    private val sheetTicketCancellationBinding get() = _sheetTicketCancellationBinding

    private lateinit var _sheetViewPassengerDetailsBinding: SheetPassengerDetailsBinding
    private val sheetViewPassengerDetailsBinding get() = _sheetViewPassengerDetailsBinding

    private lateinit var _sheetUpdatePassengersDetailsBinding: SheetUpdatePassengersDetailsBinding
    private val sheetUpdatePassengersDetailsBinding get() = _sheetUpdatePassengersDetailsBinding

    private var dialogUpdatePersonalDetailsBinding: DialogUpdatePersonalDetailsBinding? = null
    private var dialogUpdateSinglePassengerDetailsBinding: DialogUpdateSinglePassengerDetailsBinding? =
        null
//    private val sheetUpdatePassengersDetailsBinding get() = _sheetUpdatePassengersDetailsBinding

    private lateinit var _sheetModifyDetailsBinding: SheetModifyDetailsBinding
    private val sheetModifyDetailsBinding get() = _sheetModifyDetailsBinding

    //    private var ticketList = com.bitla.ts.domain.pojo.update_ticket.request.Ticket()
    private var getPassengerDetailResponse: ArrayList<PassengerDetail> = ArrayList()
    private lateinit var spinnerItems: SpinnerItems
    private var userListResponse: ArrayList<SpinnerItems> = arrayListOf()
    private lateinit var selectedAcOnBehalfOf: String
    private var passengerDetailList: MutableList<PassengerDetail?>? = null
    private var updateBulkDataList: ArrayList<UpdateData> = ArrayList()
    private var tempUpdateBulkDataList: ArrayList<UpdateData> = ArrayList()

    private val currentCheckedItem: MutableList<PassengerDetail?> = ArrayList()
    private var userList: MutableList<SpinnerItems> = mutableListOf()

    private var refundTypeList: ArrayList<SpinnerItems> = arrayListOf()
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private val cancelTicketViewModel by viewModel<CancelTicketViewModel<Any?>>()
    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()

    private lateinit var editPassengersAdapter: EditPassengersAdapter

    override fun isInternetOnCallApisAndInitUI() {

        lifecycleScope.launch {
            supervisorScope {
                launch {
                    ticketDetailsViewModel.messageSharedFlow.collect{
                        if (it.isNotEmpty()){
                            if(isAdded){
                                requireActivity().showToast(it)

                            }
                        }
                    }
                }
               launch {
                   blockViewModel.messageSharedFlow.collect{
                       if (it.isNotEmpty()){
                           if(isAdded){
                               requireActivity().showToast(it)

                           }
                       }
                   }
               }
                launch {
                   sharedViewModel.messageSharedFlow.collect{
                       if (it.isNotEmpty()){
                           if(isAdded){
                               requireActivity().showToast(it)

                           }
                       }
                   }
               }
                launch {
                   cancelTicketViewModel.messageSharedFlow.collect{
                       if (it.isNotEmpty()){
                           if(isAdded){
                               requireActivity().showToast(it)

                           }
                       }
                   }
               }
            }
        }
    }

    override fun isNetworkOff() {
    }

    var handler: Handler = Handler(Looper.getMainLooper())
    private var isHandlerRunning = false
    var runnable: Runnable? = null
    var delay = 1500

    private lateinit var selectMultipleSeatsAdapter: SelectMultipleSeatsAdapter
    private var loginModelPref: LoginModel = LoginModel()
    private var isAbleToCancelOnBehalfOtherUser: Boolean? = null
    private var allowModify: Boolean? = null
    private var isAllowCancellationTypeAsFixedOrPercentage: Boolean? = null
    private var isAllowProvisiontoSelectMultipleBoardingDropOff: Boolean? = null
    private var isApplyBpDpFare: Boolean? = null
    private var isAllowToAlterCancelPercent: Boolean? = null
    private var canCancelTicketForUser: Boolean? = null
    private var canCancelTicketForAgent: Boolean? = null
    private var isEticket: Boolean? = null
    private lateinit var sourceDestination: String
    private var busType: String = ""
    private var informPassengersAboutCancellation: String = ""
    private var informPassengersAboutUpdate: String = ""
    private var selectedSeatNumber = StringBuilder()
    private var cancellationAmount: String = ""
    private var refundAmount: String = ""
    private var cancelPercent: String = ""
    private var travelDate: String = ""
    private lateinit var bccId: String
    private lateinit var apiKey: String
    private var userTypeId: Int = 12
    private var ticketNumber: String =""
    private var isSeatClick = 0
    private var cancelOnBehalOf: Int? = null
    private var isCanCancelTicketForUser: Boolean = false
    private var isOnbehalfOnlineAgentFlag: Boolean = false
    private var ticketCancellationPercentage: String = ""
    private var selectedCancellationType: String = ""

    //    private var cancellationValue: Int = 1
    private var passName: String? = ""
    private var passCountryCode: String? = ""
    private var passAge: String? = ""
    private var passEmail: String? = ""
    private var passGlobalEmail: String? = ""
    private var passPhone: String? = ""
    private var finalMobileNumber: String? = ""
    private var passGender: String? = ""
    private var passIsSingleSeat: String? = ""
    private var passBoardingAt: String? = ""
    private var passDroppingAt: String? = ""
    private var isETicket: Boolean = false
    private var isApiTicket: Boolean = false
    private var isOnbehalfOnlineTicket: Boolean = false
    private var passFare: String = ""

    //    private var refundType = 2
    private var sendSms = false
    private var isCheckSMSValue = 0
    private lateinit var seatNumber: String
    private var name: String = ""
    private var age: String = ""
    private var phone: String = ""
    private var finalCountryCode: String = ""
    private var gender: String = ""
    private var emailId: String = ""
    private var boardingAt: String? = ""
    private var droffAt: String? = ""
    private var remarks: String = ""
    private var updatedFare: String = ""

    // boarding and dropping
    private var reservationId: String? = ""
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var stageDetails = mutableListOf<StageDetail>()
    private var boardingList: MutableList<StageDetail>? = null
    private var droppingList: MutableList<StageDetail>? = null
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var depTime: String? = null
    private var isZeroPercentCancellation: Boolean? = null
    private var isZeroPercentCancellationCheck = false

    private var dialogOpenCount = 0
    private var dialogPartialTicketOpenCount = 0
    private var updateDataCount = 0
    private var passengerDetailData = UpdateData()
    private var seatMap = mutableMapOf<Int, String>()
    private var isSingleSeatClick: Boolean = false
    private var privilegePhoneValidationCount: Int? = null
    private var isEditable: Boolean = false
    private var countryCode = ArrayList<Int>()
    private var locale = PreferenceUtils.getlang()
    private var amountCurrency: String = ""
    private var currencyFormatt: String = ""

    private var passengerList: MutableList<PassengerDetail?> = mutableListOf()
    private var singleSelectedSeatNumber: String = ""
    private var tagSelection: String = ""
    var country = ""
    private var isAgentLogin: Boolean = false
    private var cancelOtpLayoutDialogOpenCount = 0
    private var cancelOptkey = ""
    private var cancelOtp = ""
    private var isBimaTicket: Boolean = false
    private var isCancelDetailsCalled = false
    private var isUpdateTicketCalled = false
    private var isSingleUpdateCalled = false
    private var isViewPassengerCalled = false
    private var pinSize = 0
    private var shouldTicketCancellation = false
    private var privilegeResponseModel: PrivilegeResponseModel?= null

    private var isUpdateFareForPhoneBlockedOrConfirmedTickets: Boolean = false
    private var isUpdateFareSeatType: Boolean = false
    private var isShowFareOption: Boolean = false



    override fun showViewPassengersSheet(pnrNumber: Any) {
        Timber.d("updateTicketFlow:2: ")

        tagSelection = "showViewPassengersSheet"
        getPref()
        executeTicketDetailsApiCall(pnrNumber)
        setTicketDetailObserver()
        setViewPassengerObserver()
        isViewPassengerCalled = true

    }

    private fun getPref() {

        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key

        if (PreferenceUtils.getObject<PrivilegeResponseModel>(PREF_PRIVILEGE_DETAILS) != null) {
            val privilegeResponseModel: PrivilegeResponseModel? =
                PreferenceUtils.getObject<PrivilegeResponseModel>(PREF_PRIVILEGE_DETAILS)

            privilegeResponseModel?.let {
                showIsdCountryCode = privilegeResponseModel.showCountriesIsdCodesSelection ?: false
                isAbleToCancelOnBehalfOtherUser = privilegeResponseModel.ableToCancelOnbehalfOtherUser
                allowModify = privilegeResponseModel.availableAppModes?.allowModify
                isAllowProvisiontoSelectMultipleBoardingDropOff = privilegeResponseModel.allowProvisionToSelectMultipleBoardingAndDropoffPoint
                isApplyBpDpFare = privilegeResponseModel.availableAppModes?.allowBpDpFare
                amountCurrency = privilegeResponseModel.currency.ifEmpty {
                    privilegeResponseModel.currency
                }
                //Timber.d("ampuntCurrency: $amountCurrency")
                country = privilegeResponseModel.country

                currencyFormatt = getCurrencyFormat(requireContext(), privilegeResponseModel.currencyFormat)

                pinSize = it.pinCount ?: 6
                shouldTicketCancellation = it.pinBasedActionPrivileges?.ticketCancellation ?: false
                isUpdateFareForPhoneBlockedOrConfirmedTickets = it.tsPrivileges?.updateFareForPhoneBlockedOrConfirmedTickets ?: false
            }

        }

        countryCode = getCountryCodes()
    }

    private fun executeTicketDetailsApiCall(pnrNumber: Any) {
        val apiHelper = TicketDetailsApiHelper(
            activity = requireActivity(),
            ticketDetailsViewModel = ticketDetailsViewModel,
            loginModelPref = loginModelPref,
            locale = locale,
            ticketDetailsMethodName = ticket_details_method_name
        )

        apiHelper.callTicketDetailsApi(pnrNumber)
    }

    private fun setTicketDetailObserver() {
        ticketDetailsViewModel.dataTicketDetails.observe(this) { it ->
            if (it != null) {
                if (it.code == 200 && it.success) {
                    source = it.body.origin
                    destination = it.body.destination
                    isETicket = it.body.isEticket
                    sourceId = it.body.originId.toString()
                    destinationId = it.body.destinationId.toString()
                    travelDate = it.body.travelDate.toString()
                    reservationId = it.body.reservationId.toString()
                    boardingAt = it.body.boardingDetails?.address
                    droffAt = it.body.dropOffDetails?.address
                    busType = it.body.busType.toString()
                    depTime = it.body.depTime
                    ticketNumber = it.body.ticketNumber.toString()
                    isBimaTicket = it.body.isBimaTicket ?: false

                    if (PreferenceUtils.getObject<PrivilegeResponseModel>(PREF_PRIVILEGE_DETAILS) != null) {
                        val privilegeResponse = PreferenceUtils.getObject<PrivilegeResponseModel>(
                            PREF_PRIVILEGE_DETAILS
                        )
                        privilegeResponse?.let {
                            if (privilegeResponse.isAgentLogin != null)
                                isAgentLogin = privilegeResponse.isAgentLogin
                        }
                    }

                    if (!getUserRole(loginModelPref, isAgentLogin, requireContext()).contains(
                            getString(R.string.role_agent),
                            true
                        )
                    ) {
                        executeServiceApiCall()
                        setServiceDetailsApiObserver()
                    }

                    if (isCancelDetailsCalled) {
                        setTicketDetailsCancellationDetail(it)
                    }
                    if (isUpdateTicketCalled) {
                        setTicketDetailsUpdatePassenger(it)
                    }
                    if (isViewPassengerCalled) {
                        setTicketDetailsViewPassenger(it)
                    }

                }
                else if (it.code == 401) {
                    (activity as BaseActivity).showUnauthorisedDialog()
                }
                else {
                    if (it?.result?.message != null) {
                        it.result?.message?.let { it -> requireActivity().toast(it) }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun setViewPassengerObserver() {
        val bottomSheetDialogUpdatePassenger = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
        _sheetViewPassengerDetailsBinding = SheetPassengerDetailsBinding.inflate(layoutInflater)
        bottomSheetDialogUpdatePassenger.setContentView(sheetViewPassengerDetailsBinding.root)
        bottomSheetDialogUpdatePassenger.setCancelable(false)
        bottomSheetDialogUpdatePassenger.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        sheetViewPassengerDetailsBinding.btnGoBack.setOnClickListener {
            bottomSheetDialogUpdatePassenger.dismiss()
        }
        bottomSheetDialogUpdatePassenger.show()
    }

    private fun executeServiceApiCall() {
        val apiHelper = ServiceApiHelper(
            activity = requireActivity(),
            sharedViewModel = sharedViewModel,
            loginModelPref = loginModelPref,
            reservationId = reservationId,
            sourceId = sourceId,
            destinationId = destinationId,
            operatorApiKey = operator_api_key,
            locale = locale,
            serviceDetailsMethod = service_details_method
        )

        apiHelper.callServiceApi()

        setServiceDetailsApiObserver()
    }

    private fun setServiceDetailsApiObserver() {
        sharedViewModel.serviceDetails.observe(this) {

            when (it.code) {
                200 -> {

                    boardingList = mutableListOf()
                    droppingList = mutableListOf()
                    stageDetails = it.body.stageDetails!!

                    it.body.stageDetails.let {
                        for (i in 0..stageDetails.size.minus(1)) {
                            if (stageDetails[i].cityId.toString() == sourceId) {
                                generateBoardingList(i)
                            } else {
                                generateDroppingList(i)
                            }
                        }
                    }

                    if (isSingleUpdateCalled)
                        if (it.body.coachDetails?.seatDetails?.size != 0) {
                            for (i in 0 until (it.body.coachDetails?.seatDetails?.size?:0)) {

                                val passengerDetailRemarks = it.body.coachDetails?.seatDetails?.get(i)?.passengerDetails

                                if (!passengerDetailRemarks?.remarks.isNullOrEmpty()
                                    && passengerDetailRemarks?.name == sheetModifyDetailsBinding.etName.text.toString().trim()
                                ) {
                                    sheetModifyDetailsBinding.etRemarkSingleUpdate.setText("${passengerDetailRemarks.remarks}")
                                        .toString()
                                }
                            }
                        }

                    if (isUpdateTicketCalled) {
                        if (it.body.coachDetails?.seatDetails?.size != 0) {
                            for (i in 0 until it.body.coachDetails?.seatDetails?.size!!) {

                                val passengerRemarks =
                                    it.body.coachDetails?.seatDetails?.get(i)?.passengerDetails?.remarks

                                if (passengerRemarks?.isNotEmpty() == true) {
                                    sheetUpdatePassengersDetailsBinding.etRemark.setText(passengerRemarks).toString()
                                    updateButtonDisable()
                                }
                            }
                        }
                    }
                }

                401 -> {
                    (activity as BaseActivity).showUnauthorisedDialog()
                }

                else -> it.message?.let { it1 -> requireContext().toast(it1) }
            }
        }
    }

    private fun generateBoardingList(i: Int) {
        if (boardingList != null) {
            boardingList?.add(stageDetails[i])
            Timber.d("boardingList ${boardingList?.size}")
        }
    }

    private fun generateDroppingList(i: Int) {
        if (droppingList != null) {
            droppingList?.add(stageDetails[i])
            Timber.d("droppingList ${droppingList?.size}")
        }
    }

    private fun updateButtonDisable() {
        dialogUpdatePersonalDetailsBinding?.btnSaveDetails?.setBackgroundResource(R.drawable.button_default_bg)
        dialogUpdatePersonalDetailsBinding?.btnSaveDetails?.isEnabled = false
        sheetUpdatePassengersDetailsBinding.btnUpdateBulkTicket.isEnabled = false
        sheetUpdatePassengersDetailsBinding.btnUpdateBulkTicket.setBackgroundResource(R.drawable.button_default_bg)
    }

    private fun setTicketDetailsCancellationDetail(it: TicketDetailsModel) {
        if (it.code == 200 && it.success) {
            sheetTicketCancellationBinding.includeProgress.progressBar.gone()
            val source = it.body.origin
            val destination = it.body.destination
            isETicket = it.body.isEticket

            sourceDestination = "$source - $destination"
            busType = it.body.busType.toString()
            depTime = it.body.depTime
            ticketNumber = it.body.ticketNumber.toString()
            isBimaTicket = it.body.isBimaTicket ?: false
            travelDate = it.body.travelDate.toString()

            isAllowCancellationTypeAsFixedOrPercentage = it.body.isAllowCancellationTypeAsFixedOrPercentage
            isAllowToAlterCancelPercent = it.body.isAllowToAlterCancelPercent
            isZeroPercentCancellation = it.body.isZeroPercentCancellation
            isOnbehalfOnlineAgentFlag = it.body.isOnbehalfOnlineTicket == true

            it.body.passengerDetails?.forEach {
                canCancelTicketForUser = it?.canCancelTicketForUser
                canCancelTicketForAgent = it?.can_cancel_ticket_for_agent
            }

            passengerDetailList = it.body.passengerDetails?.filter { it?.ticketStatus?.equals(getString(R.string.cancelled),true) == false }.toArrayList()
            setSeatNoListAdapter()
            selectMultipleSeatsAdapter = SelectMultipleSeatsAdapter(requireContext(), passengerDetailList, this)

            if (passengerDetailList?.size == 1) {
                sheetTicketCancellationBinding.btnCancelTicket.setBackgroundResource(R.drawable.button_selected_bg)
            } else {
                sheetTicketCancellationBinding.btnCancelTicket.setBackgroundResource(R.drawable.button_default_bg)
            }

            //                    case 1

            if (isZeroPercentCancellation == true
                && isAllowCancellationTypeAsFixedOrPercentage == true
                && isAllowToAlterCancelPercent == true
            ) {
                sheetTicketCancellationBinding.chkZeroCancellation.gone()
                cancelTypeVisible()
                Timber.d("CancellationVisibility: case 1")
            }

            //                    case:2

            else if (isZeroPercentCancellation == false
                && isAllowCancellationTypeAsFixedOrPercentage == false
                && isAllowToAlterCancelPercent == false
            ) {
                sheetTicketCancellationBinding.chkZeroCancellation.gone()
                cancelTypeGone()
                Timber.d("CancellationVisibility: case 2")
            }

            //                    case:3

            else if (isZeroPercentCancellation == true
                && isAllowCancellationTypeAsFixedOrPercentage == false
                && isAllowToAlterCancelPercent == false
            ) {
                sheetTicketCancellationBinding.chkZeroCancellation.visible()
                cancelTypeGone()
                Timber.d("CancellationVisibility: case 3")
            }

            //                    case:4

            else if (isZeroPercentCancellation == true
                && isAllowCancellationTypeAsFixedOrPercentage == true
                && isAllowToAlterCancelPercent == false
            ) {
                sheetTicketCancellationBinding.chkZeroCancellation.visible()
                cancelTypeGone()
                Timber.d("CancellationVisibility: case 4")
            }

            //                    case:5

            else if (isZeroPercentCancellation == true
                && isAllowCancellationTypeAsFixedOrPercentage == false
                && isAllowToAlterCancelPercent == true
            ) {
                sheetTicketCancellationBinding.chkZeroCancellation.visible()
                cancelTypeGone()
                Timber.d("CancellationVisibility: case 5")
            }

            //                    case:6

            else if (isZeroPercentCancellation == false
                && isAllowCancellationTypeAsFixedOrPercentage == true
                && isAllowToAlterCancelPercent == true
            ) {
                sheetTicketCancellationBinding.chkZeroCancellation.gone()
                cancelTypeVisible()
                Timber.d("CancellationVisibility: case 6")
            }

            //  if zero Cancellation checked

            sheetTicketCancellationBinding.chkZeroCancellation.setOnClickListener {
                sheetTicketCancellationBinding.apply {
                    if (chkZeroCancellation.isChecked) {
                        rgCancellationType.gone()
                        cancellationTypeLabel.gone()
                        layoutCancellationPercentage.gone()
                        noticeText.gone()
                        isZeroPercentCancellationCheck = true
                        selectedCancellationType = "1"

                    } else {
                        isZeroPercentCancellationCheck = false
                        selectedCancellationType = ""
                    }
                }
            }

            if (canCancelTicketForUser == true) {
                sheetTicketCancellationBinding.chkCancellingOnBehalfUser.visible()
                sheetTicketCancellationBinding.chkCancellingOnBehalfUser.setOnClickListener {
                    if (sheetTicketCancellationBinding.chkCancellingOnBehalfUser.isChecked) {
                        isCanCancelTicketForUser = true
                        sheetTicketCancellationBinding.spinnerOnBehalfOf.gone()
                    } else {
                        sheetTicketCancellationBinding.spinnerOnBehalfOf.visible()
                        isCanCancelTicketForUser = false
                    }
                }
            } else {
                sheetTicketCancellationBinding.chkCancellingOnBehalfUser.gone()
            }

            if (isAbleToCancelOnBehalfOtherUser == true) {
                sheetTicketCancellationBinding.acOnBehalfOf.setAdapter(
                    ArrayAdapter(
                        requireActivity(),
                        R.layout.spinner_dropdown_item,
                        R.id.tvItem,
                        userList
                    )
                )
            } else {
                sheetTicketCancellationBinding.spinnerOnBehalfOf.gone()
            }

            // refund
            if (isEticket == true) {
                refundTypeList.clear()

                it.body.refundTypes?.forEach {
                    val spinnerItems = it?.id?.let { it1 -> SpinnerItems(it1, it.label) }
                    if (spinnerItems != null) {
                        refundTypeList.add(spinnerItems)
                    }
                }
                sheetTicketCancellationBinding.acRefundType.setAdapter(
                    ArrayAdapter(
                        requireActivity(),
                        R.layout.spinner_dropdown_item,
                        R.id.tvItem,
                        refundTypeList
                    )
                )

            } else sheetTicketCancellationBinding.spinnerRefundType.gone()

        }
    }
    private fun cancelTypeVisible() {
        sheetTicketCancellationBinding.apply {
            rgCancellationType.visible()
            cancellationTypeLabel.visible()
//            layoutCancellationPercentage.visible()
//            noticeText.visible()

        }
    }

    private fun cancelTypeGone() {
        sheetTicketCancellationBinding.apply {
            rgCancellationType.gone()
            cancellationTypeLabel.gone()
            layoutCancellationPercentage.gone()
            noticeText.gone()
        }
    }

    private fun setSeatNoListAdapter() {
        sheetTicketCancellationBinding.rvSelectMultipleSeats.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        selectMultipleSeatsAdapter =
            SelectMultipleSeatsAdapter(
                context = requireContext(),
                passengerDetailSeatList = passengerDetailList,
                onItemCheckListener = this
            )
        sheetTicketCancellationBinding.rvSelectMultipleSeats.adapter = selectMultipleSeatsAdapter
    }

    private fun setTicketDetailsUpdatePassenger(it:TicketDetailsModel) {
        Timber.d("Inside Observer")
        try {
            val source = it.body.origin
            val destination = it.body.destination
            isETicket = it.body.isEticket

            sourceDestination = "$source - $destination"
            busType = it.body.busType.toString()
            ticketNumber = it.body.ticketNumber.toString()
            isBimaTicket = it.body.isBimaTicket ?: false
            travelDate = it.body.travelDate.toString()

            boardingAt = it.body.boardingDetails?.address
            droffAt = it.body.dropOffDetails?.address
            droffAt = it.body.dropOffDetails?.address

            it.body.passengerDetails?.forEach {
                passName = it?.name
                passAge = it?.age.toString()
                passPhone = it?.cusMobile
                passEmail = it?.cusEmail
                passGender = it?.title
                passBoardingAt = it?.boardingAt.toString()
                passDroppingAt = it?.dropOff.toString()
            }

            getPassengerDetailResponse = it.body.passengerDetails as ArrayList<PassengerDetail>
            passGlobalEmail = it.body.ticketLeadDetail?.email
            sheetUpdatePassengersDetailsBinding.etGmail.setText(passGlobalEmail).toString()
            sheetUpdatePassengersDetailsBinding.etBoardingAt.setText(boardingAt)
            sheetUpdatePassengersDetailsBinding.etDropOffAt.setText(droffAt)
            // emailId = sheetUpdatePassengersDetailsBinding.etGmail.text.toString()
            isAllowCancellationTypeAsFixedOrPercentage = it.body.isAllowCancellationTypeAsFixedOrPercentage
            isAllowToAlterCancelPercent = it.body.isAllowToAlterCancelPercent
            canCancelTicketForUser = it.body.passengerDetails[0].canCancelTicketForUser

            passengerDetailList = it.body.passengerDetails
            setEditPassengerAdapter()
            editPassengersAdapter = EditPassengersAdapter(
                context = requireContext(),
                menuList = passengerDetailList,
                onItemClickListener = this
            )

        } catch (t: Throwable) {
            requireContext().toast(requireContext().getString(R.string.server_error))
//            bottomSheetDialogUpdatePassenger.cancel()
        }
    }

    private fun setEditPassengerAdapter() {
        sheetUpdatePassengersDetailsBinding.rvPassengers.layoutManager =
            LinearLayoutManager(
                /* context = */ requireContext(),
                /* orientation = */ LinearLayoutManager.VERTICAL,
                /* reverseLayout = */ false
            )
        sheetUpdatePassengersDetailsBinding.rvPassengers.adapter = EditPassengersAdapter(requireContext(), passengerDetailList, this)
    }

    private fun setTicketDetailsViewPassenger(it : TicketDetailsModel) {
        try {
            val source = it.body.origin
            val destination = it.body.destination
            isETicket = it.body.isEticket

            sourceDestination = "$source - $destination"
            busType = it.body.busType.toString()
            ticketNumber = it.body.ticketNumber.toString()
            isBimaTicket = it.body.isBimaTicket ?: false
            travelDate = it.body.travelDate.toString()

            boardingAt = it.body.boardingDetails?.address
            droffAt = it.body.dropOffDetails?.address

            it.body.passengerDetails?.forEach {
                passName = it?.name
                passAge = it?.age.toString()
                passPhone = it?.cusMobile
                passEmail = it?.cusEmail
                passGender = it?.title
                passBoardingAt = it?.boardingAt.toString()
                passDroppingAt = it?.dropOff.toString()
            }
        }
        catch (e: Exception) {
            Timber.d(e)
        }

        getPassengerDetailResponse = it.body.passengerDetails as ArrayList<PassengerDetail>

        passengerDetailList = it.body.passengerDetails
        setViewPassengerAdapter()
        editPassengersAdapter = EditPassengersAdapter(
            context = requireContext(),
            menuList = passengerDetailList,
            onItemClickListener = this
        )
    }

    private fun setViewPassengerAdapter() {
        sheetViewPassengerDetailsBinding.rvPassengers.layoutManager =
            LinearLayoutManager(
                /* context = */ requireContext(),
                /* orientation = */ LinearLayoutManager.VERTICAL,
                /* reverseLayout = */ false
            )
        sheetViewPassengerDetailsBinding.rvPassengers.adapter =
            EditSinglePassengersAdapter(requireContext(), passengerDetailList, this)
    }

    override fun onLeftButtonClick(tag: View?) {
        currentCheckedItem.clear()
        selectedCancellationType = "1"
        isSeatClick = 0
    }

    override fun onRightButtonClick(tag: View?) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onItemCheck(item: PassengerDetail?) {
        currentCheckedItem.add(item)

        if (passengerDetailList?.size == 1 && currentCheckedItem.size == 0) {
            selectedSeatNumber.append(passengerDetailList?.get(0)?.seatNumber)
            isSingleSeatClick = false
        } else {
            sheetTicketCancellationBinding.btnCancelTicket.setBackgroundResource(R.drawable.button_selected_bg)
        }    }

    override fun onItemUncheck(item: PassengerDetail?) {
        currentCheckedItem.remove(item)

        if (passengerDetailList?.size == 1) {
            isSingleSeatClick = true
            selectedSeatNumber.append("")
        } else {
            sheetTicketCancellationBinding.btnCancelTicket.setBackgroundResource(R.drawable.button_default_bg)
        }

        if (currentCheckedItem.size == 0) {
            sheetTicketCancellationBinding.btnCancelTicket.setBackgroundResource(R.drawable.button_default_bg)
        } else {
            sheetTicketCancellationBinding.btnCancelTicket.setBackgroundResource(R.drawable.button_selected_bg)
        }
    }

    override fun showEditPassengersSheet(pnrNumber: Any) {
    }

    override fun showSingleTicketUpdateSheet(pnrNumber: Any, seatNo: String) {
    }

    override fun showTicketCancellationSheet(pnrNumber: Any) {
    }

    override fun onReturnInstance(dialog: Any) {
    }

    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
    }

    override fun onAnyClickListenerWithExtraParam(
        type: Int,
        view: Any,
        list: Any,
        position: Int,
        outPos: Int
    ) {
    }


}