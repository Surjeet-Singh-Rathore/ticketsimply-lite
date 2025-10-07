package com.bitla.ts.app.base

import android.annotation.*
import android.app.*
import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.text.*
import android.util.Log
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
open class BaseUpdateCancelTicket : BaseFragment(), DialogButtonListener, OnItemClickListener,
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
                    if(isAdded){
                        ticketDetailsViewModel.messageSharedFlow.collect{
                            if (it.isNotEmpty()){
                                if(isAdded){
                                    requireActivity().showToast(it)

                                }
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
    private var seatNumber: String = ""
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
    private var isAllowToShowRemarksForCancelTicket = false

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
    private var remarkCancelTicket: String = ""

    override fun showTicketCancellationSheet(pnrNumber: Any) {
        Timber.d("updateTicketFlow:0: ")

        getPref()
        setTicketDetailObserver()
        executeUserListApi()
        executeTicketDetailsApiCall(pnrNumber)
        setCancellationTicketDetailObserver()
        isCancelDetailsCalled = true
    }

    override fun showEditPassengersSheet(pnrNumber: Any) {
        Timber.d("updateTicketFlow:1: ")

        tagSelection = "showEditPassengersSheet"
        getPref()

//        if (allowModify == true) {
//        } else {
//            requireActivity().toast(getString(R.string.can_not_update_ticket))
//        }
        setUpdatePassengerObserver()
        executeTicketDetailsApiCall(pnrNumber)
        isUpdateTicketCalled = true
    }

    override fun showViewPassengersSheet(pnrNumber: Any) {
        Timber.d("updateTicketFlow:2: ")

        tagSelection = "showViewPassengersSheet"
        getPref()
        executeTicketDetailsApiCall(pnrNumber)
        setTicketDetailObserver()
        setViewPassengerObserver()
        isViewPassengerCalled = true

    }

    override fun showSingleTicketUpdateSheet(pnrNumber: Any, seatNo: String) {
        Timber.d("updateTicketFlow:3: ")

        singleSelectedSeatNumber = seatNo
        tagSelection = "showSingleTicketUpdateSheet"
        passengerList.clear()
        getPref()
        setTicketDetailObserver()
        executeTicketDetailsApiCall(pnrNumber)
        setSingleUpdateTicketObserver()
        isSingleUpdateCalled = true
    }

    fun updateBulkTicketData(
        pnrNumber: Any,
        seatNo: String,
        anyDataListener: DialogButtonAnyDataListener
    ) {
        singleSelectedSeatNumber = seatNo
        tagSelection = "showSingleTicketUpdateSheet"
        passengerList.clear()
        getPref()
        setTicketDetailObserver()
        executeTicketDetailsApiCall(pnrNumber)
        setSingleUpdateTicketObserver()
        sendUpdateDataListener = anyDataListener
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
                isAllowToShowRemarksForCancelTicket = it.tsPrivileges?.allowToShowRemarksForCancelTicket ?: false

            }

        }

        countryCode = getCountryCodes()
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

    private fun setCancellationTicketDetailObserver() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
        _sheetTicketCancellationBinding = SheetTicketCancellationBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetTicketCancellationBinding.root)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        sheetTicketCancellationBinding.apply {

            layoutCancellationPercentage.hint = "Cancellation (%)"

            rbPercent.setOnClickListener {
                selectedCancellationType = "1"
                layoutCancellationPercentage.hint = "Cancellation (%)"
                layoutCancellationPercentage.visible()
                noticeText.visible()
            }

            rbFixed.setOnClickListener {
                selectedCancellationType = "2"
                layoutCancellationPercentage.hint = "Cancellation ($amountCurrency)"
                layoutCancellationPercentage.visible()
                noticeText.visible()

            }
        }

        isSeatClick = 0

        if (isSeatClick == 0) {
            isSeatClick++
            sheetTicketCancellationBinding.rvSelectMultipleSeats.visible()
            sheetTicketCancellationBinding.txtSelectAll.gone()
            sheetTicketCancellationBinding.selectAllHeader.gone()
            selectedSeatNumber.clear()
        }

        sheetTicketCancellationBinding.etSeats.setOnClickListener {

            if (isSeatClick == 0) {
                isSeatClick++
                sheetTicketCancellationBinding.rvSelectMultipleSeats.visible()
                sheetTicketCancellationBinding.txtSelectAll.gone()
                sheetTicketCancellationBinding.selectAllHeader.gone()
                selectedSeatNumber.clear()
            } else {
                sheetTicketCancellationBinding.rvSelectMultipleSeats.gone()
                sheetTicketCancellationBinding.txtSelectAll.gone()
                sheetTicketCancellationBinding.selectAllHeader.gone()
                isSeatClick = 0

                for (i in 0 until currentCheckedItem.size) {
                    selectedSeatNumber.append(currentCheckedItem[i]?.seatNumber)
                    if (i < currentCheckedItem.size - 1) {
                        selectedSeatNumber.append(",")
                    }
                }
                sheetTicketCancellationBinding.etSeats.setText(selectedSeatNumber)
            }
        }




        sheetTicketCancellationBinding.tvCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
            currentCheckedItem.clear()
            selectedSeatNumber.clear()
            isZeroPercentCancellationCheck = false
            selectedCancellationType = ""
        }

        sheetTicketCancellationBinding.btnCancelTicket.setOnClickListener {
            selectedSeatNumber.clear()

            var isButtonClickable = true

            if (isButtonClickable) {
                if (isAllowToShowRemarksForCancelTicket && country.equals("India", true)) {
                    remarkCancelTicket = sheetTicketCancellationBinding.etRemarks.text.toString().trim()
                    if (remarkCancelTicket == "") {
                        requireContext().toast("Validation: Please enter the Remarks")
                        sheetTicketCancellationBinding.btnCancelTicket.isEnabled = true
                        return@setOnClickListener
                    }
                }

                isButtonClickable = false
                sheetTicketCancellationBinding.btnCancelTicket.isEnabled = false

                if (passengerDetailList?.size == 1 && !isSingleSeatClick) {
                    selectedSeatNumber.append(passengerDetailList?.get(0)?.seatNumber)
                    sheetTicketCancellationBinding.btnCancelTicket.setBackgroundResource(R.drawable.button_selected_bg)
                }

                for (i in 0 until currentCheckedItem.size) {
                    selectedSeatNumber.append(currentCheckedItem[i]?.seatNumber)
                    if (i < currentCheckedItem.size - 1) {
                        selectedSeatNumber.append(",")
                    }
                }
                sheetTicketCancellationBinding.etSeats.setText(selectedSeatNumber)

                val getCancellationPercentage =
                    sheetTicketCancellationBinding.etCancellationPercentage.text.toString()
                ticketCancellationPercentage = getCancellationPercentage.ifEmpty { "" }

                val cancellationPercentage = ticketCancellationPercentage.toIntOrNull()

                if (selectedSeatNumber.isEmpty()) {
                    requireContext().toast(getString(R.string.selectSeat))
                    isSeatClick++
                    sheetTicketCancellationBinding.rvSelectMultipleSeats.visible()
                    currentCheckedItem.clear()
//                sheetTicketCancellationBinding.txtSelectAll.gone()
//                sheetTicketCancellationBinding.selectAllHeader.gone()
                } else if (cancellationPercentage != null
                    && cancellationPercentage > PHONE_VALIDATION_COUNT
                    && selectedCancellationType == "1"
                ) {
                    requireContext().toast("Cancellation Percentage should not be more than 100%.")
                } else {
                    sheetTicketCancellationBinding.apply {
                        rvSelectMultipleSeats.gone()
                        txtSelectAll.gone()
                        selectAllHeader.gone()
                    }
                    isSeatClick = 0

                    if (isZeroPercentCancellationCheck) {
                        executeZeroCancellationDetailsApiCall(ticketNumber, isBimaTicket)
//                    Timber.d("CancellationDetailsApi_check - 0")
                    } else if (isZeroPercentCancellation == true
                        && isAllowCancellationTypeAsFixedOrPercentage == false
                        && isAllowToAlterCancelPercent == false
                    ) {
                        executeZeroCancellationDetailsApiCall(ticketNumber, isBimaTicket)
//                    Timber.d("CancellationDetailsApi_check - 1")
                    } else if (isZeroPercentCancellation == true
                        && isAllowCancellationTypeAsFixedOrPercentage == true
                        && isAllowToAlterCancelPercent == false
                    ) {
                        executeZeroCancellationDetailsApiCall(ticketNumber,isBimaTicket)
//                    Timber.d("CancellationDetailsApi_check - 2")
                    } else if (isZeroPercentCancellation == true
                        && isAllowCancellationTypeAsFixedOrPercentage == false
                        && isAllowToAlterCancelPercent == true
                    ) {
                        executeZeroCancellationDetailsApiCall(ticketNumber,isBimaTicket)
//                    Timber.d("CancellationDetailsApi_check - 3")
                    } else if (isZeroPercentCancellation == false
                        && isAllowCancellationTypeAsFixedOrPercentage == true
                        && isAllowToAlterCancelPercent == true
                    ) {
                        executeCancellationDetailsApiCall(ticketNumber,isBimaTicket)
//                    Timber.d("CancellationDetailsApi_check - 4")
                    } else {
                        executeCancellationDetailsApiCall(ticketNumber,isBimaTicket)
//                    Timber.d("CancellationDetailsApi_check - 5")
                    }

                    if (dialogOpenCount == 0) {
                        setCancelTicketObserve(bottomSheetDialog)
                    }
                }

                Handler().postDelayed({
                    isButtonClickable = true
                    sheetTicketCancellationBinding.btnCancelTicket.isEnabled = true
                }, 2000)
            }

        }
//        sheetTicketCancellationBinding.txtSelectAll.setOnClickListener {}

        blockViewModel.userList.observe(this) { it ->
            userList.clear()
            if (it != null) {
                if (it.active_users != null && it.active_users.isNotEmpty()) {
                    it.active_users.forEach {
                        spinnerItems = SpinnerItems(it.id, it.label)
                        userList.add(spinnerItems)
                        userListResponse.add(spinnerItems)
                    }
                }
            }

            selectedAcOnBehalfOf = sheetTicketCancellationBinding.acOnBehalfOf.text.toString()

            sheetTicketCancellationBinding.acOnBehalfOf.setOnItemClickListener { _, _, _, _ ->

                val manager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                manager?.hideSoftInputFromWindow(
                    sheetTicketCancellationBinding.acOnBehalfOf.windowToken, 0
                )
                selectedAcOnBehalfOf = sheetTicketCancellationBinding.acOnBehalfOf.text.toString()
                for (i in 0 until userListResponse.size) {
                    if (userListResponse[i].value == selectedAcOnBehalfOf) {
                        cancelOnBehalOf = userListResponse[i].id
                    }
                }
            }
        }
        bottomSheetDialog.show()
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

    private fun setCancelTicketObserve(bottomSheetDialog: BottomSheetDialog) {

        dialogOpenCount++
        cancelTicketViewModel.cancellationDetailsResponse.observe(requireActivity()) {

            Timber.d("messageResult-${it}")

            if (it != null) {
                if (it.code == 200) {
                    cancellationAmount = it.result.cancelledFare.toString()
                    refundAmount = it.result.refundAmount.toString()
                    cancelPercent = it.result.cancelPercent.toString()

                    openCancelConfirmDialog(
                        bottomSheetDialog = bottomSheetDialog,
                        cancellationAmountX = cancellationAmount,
                        refundAmountX = refundAmount,
                        cancelPercent = cancelPercent
                    )
                    Timber.d("messageResult-${it.result}")
                } else if (it.code == 401) {
                    (activity as BaseActivity).showUnauthorisedDialog()
                } else {
                    if (it?.message != null) {
                        it?.message?.let { it -> requireActivity().toast(it) }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun openCancelConfirmDialog(
        bottomSheetDialog: BottomSheetDialog,
        cancellationAmountX: String,
        refundAmountX: String,
        cancelPercent: String
    ) {

        var cancellationChargesPercentage = ""
        var cancellationChargesRupee = ""

        when (selectedCancellationType) {
            "1" -> {
                cancellationChargesPercentage = "%"
                cancellationChargesRupee = ""
            }

            "2" -> {
                cancellationChargesRupee = amountCurrency
                cancellationChargesPercentage = ""
            }

            "" -> {
                cancellationChargesPercentage = "%"
                cancellationChargesRupee = ""
            }
        }

        if (sheetTicketCancellationBinding.chkInformPassengerCancellation.isChecked) {
            informPassengersAboutCancellation = getString(R.string.selected_seat_s_will_be_cancelled)
            sendSms = true
        } else {
            informPassengersAboutCancellation = ""
            sendSms = false
        }

        ticketCancelDialog(
            context = requireActivity(),
            title = getString(R.string.cancel_tickets),
            message = informPassengersAboutCancellation,
            srcDest = sourceDestination,
            journeyDate = busType,
            ticketCancellationPercentage = "$cancellationChargesRupee$cancelPercent$cancellationChargesPercentage",
            seatNo = selectedSeatNumber.toString(),
            cancellationAmount = "$amountCurrency${
                cancellationAmountX.toDouble().convert(currencyFormatt)
            }",
            refundAmount = "$amountCurrency${refundAmountX.toDouble().convert(currencyFormatt)}",
            buttonLeftText = getString(R.string.goBack),
            buttonRightText = getString(R.string.confirm_cancellation),
            bottomSheetDialog = bottomSheetDialog,
            dialogButtonTagListener = this
        )
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

    private fun setUpdatePassengerObserver() {
        val bottomSheetDialogUpdatePassenger = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
        _sheetUpdatePassengersDetailsBinding = SheetUpdatePassengersDetailsBinding.inflate(layoutInflater)
        SheetUpdatePassengersDetailsBinding.inflate(layoutInflater)
        bottomSheetDialogUpdatePassenger.setContentView(sheetUpdatePassengersDetailsBinding.root)
        bottomSheetDialogUpdatePassenger.setCancelable(false)
        bottomSheetDialogUpdatePassenger.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        sheetUpdatePassengersDetailsBinding.apply {

            etBoardingAt.setOnClickListener {
                if (sheetUpdatePassengersDetailsBinding.etBoardingAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                }
                if (sheetUpdatePassengersDetailsBinding.etDropOffAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                }
                openActivityForResult()
                updateButtonEnable()
            }

            etDropOffAt.setOnClickListener {
                if (sheetUpdatePassengersDetailsBinding.etBoardingAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                }
                if (sheetUpdatePassengersDetailsBinding.etDropOffAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                }
                openActivityForResult()
                updateButtonEnable()
            }

            etGmail.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    emailId = s.toString()

//                if (!isEmailValid(emailId)) {
//                    requireContext().toast(getString(R.string.email_format))
//                }
                    if (emailId.isNotEmpty() && isEmailValid(emailId)) {
                        updateButtonEnable()
                    } else {
                        updateButtonDisable()
                    }
                }

                override fun afterTextChanged(s: Editable) {
                }
            })

            etRemark.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    remarks = s.toString()
                    updateButtonEnable()
                }

                override fun afterTextChanged(s: Editable) {
                }
            })
        }

        sheetUpdatePassengersDetailsBinding.apply {
            if (isAllowProvisiontoSelectMultipleBoardingDropOff == false && isApplyBpDpFare == false) {
                layoutBoardingAt.visible()
                layoutDropOffAt.visible()
                headerBoardingDropping.visible()
            }
            else {
                layoutBoardingAt.gone()
                layoutDropOffAt.gone()
                headerBoardingDropping.gone()
            }
        }

        sheetUpdatePassengersDetailsBinding.tvCancel.setOnClickListener {
            updateBulkDataList.clear()
            boardingList?.clear()
            droppingList?.clear()
            seatMap.clear()
            updateDataCount = 0
            bottomSheetDialogUpdatePassenger.dismiss()
        }

        setServiceDetailsApiObserver()
        setTicketDetailObserver()

        sheetUpdatePassengersDetailsBinding.btnUpdateBulkTicket.setOnClickListener {

            informPassengersAboutUpdate =
                if (sheetUpdatePassengersDetailsBinding.chkSms.isChecked) {
                    getString(R.string.sms_sent)
                } else {
                    ""
                }

            updateTwoButtonDialog(
                context = requireContext(),
                title = "${getString(R.string.update_ticket_details)}?",
                message = informPassengersAboutUpdate,
                messageTextColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                buttonLeftText = getString(R.string.goBack),
                buttonRightText = getString(R.string.update),
                bottomSheetDialog = bottomSheetDialogUpdatePassenger,
                dialogButtonTagListener = this
            )

//            if (emailId.isNullOrEmpty()) {
//                updateTwoButtonDialog(
//                    requireActivity(),
//                    "${getString(R.string.update_ticket_details)}?",
//                    informPassengersAboutUpdate,
//                    ContextCompat.getColor(requireContext(), R.color.colorPrimary),
//                    getString(R.string.goBack),
//                    getString(R.string.update),
//                    bottomSheetDialogUpdatePassenger
//                )
//            }
//            else {
//                if (!isEmailValid(emailId)) {
//                    requireContext().toast(getString(R.string.email_format))
//                } else {
//                    updateTwoButtonDialog(
//                        requireActivity(),
//                        "${getString(R.string.update_ticket_details)}?",
//                        informPassengersAboutUpdate,
//                        ContextCompat.getColor(requireContext(), R.color.colorPrimary),
//                        getString(R.string.goBack),
//                        getString(R.string.update),
//                        bottomSheetDialogUpdatePassenger
//                    )
//                }
//            }
        }
        bottomSheetDialogUpdatePassenger.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setSingleUpdateTicketObserver() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
        _sheetModifyDetailsBinding = SheetModifyDetailsBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetModifyDetailsBinding.root)
        sheetModifyDetailsBinding.includeProgress.progressBar.visible()

        val privileges = (requireContext() as BaseActivity).getPrivilegeBase()

        val list = getAllCountries(requireContext())

        sheetModifyDetailsBinding.etCountryCode.setOnClickListener {
            if (isETicket && showIsdCountryCode) {
                isSingleUpdate = true
                countryPickerDialog = showCountryPickerBottomsheet(
                    context = requireContext(),
                    countriesList = list,
                    onItemClickListener = this
                )
            }
        }

        sheetModifyDetailsBinding.apply {
            if (isAllowProvisiontoSelectMultipleBoardingDropOff == true && isApplyBpDpFare == false) {
                layoutBoardingAt.visible()
                layoutDropOffAt.visible()
                headerBoardingDropping.visible()
            } else {
                layoutBoardingAt.gone()
                layoutDropOffAt.gone()
                headerBoardingDropping.gone()
            }
        }

        sheetModifyDetailsBinding.chkSms.setOnClickListener {
            isCheckSMSValue = if (sheetModifyDetailsBinding.chkSms.isChecked) {
                1
            } else {
                0
            }
        }

        sheetModifyDetailsBinding.tvCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
            updateBulkDataList.clear()
            boardingList?.clear()
            droppingList?.clear()
            seatMap.clear()
            updateDataCount = 0
        }

        ticketDetailsViewModel.dataTicketDetails.observe(this) { it ->


            sheetModifyDetailsBinding.includeProgress.progressBar.gone()

            if (it != null) {
                ticketDetailsViewModel.loadingState.observe(this) {
                    Timber.d("LoadingState-ticketDetailsViewModel ${it.status}")
                    when (it) {
                        LoadingState.LOADING -> sheetModifyDetailsBinding.includeProgress.progressBar.visible()
                        LoadingState.LOADED -> sheetModifyDetailsBinding.includeProgress.progressBar.gone()
                        else -> {
                            it.msg?.let { it1 -> context?.toast(it1) }
                            sheetModifyDetailsBinding.includeProgress.progressBar.gone()
                        }
                    }
                }

                if (it.code == 200 && it.success) {

                    Timber.d("genderArray2 ${Gson().toJson(resources.getStringArray(R.array.genderArray2))}")

                    sheetModifyDetailsBinding.includeProgress.progressBar.gone()

                    busType = it.body.busType.toString()
                    depTime = it.body.depTime
                    ticketNumber = it.body.ticketNumber.toString()
                    isBimaTicket = it.body.isBimaTicket ?: false
                    boardingAt = it.body.boardingDetails?.address
                    droffAt = it.body.dropOffDetails?.address
                    isOnbehalfOnlineTicket = it.body.isOnbehalfOnlineTicket
                    isApiTicket = it.body.isApiTicket



                    if (it?.body?.passengerDetails != null
                        && it?.body?.passengerDetails?.isNotEmpty() == true
                    ) {
                        passengerList = it.body.passengerDetails
                        isETicket = it.body.isEticket

                        if (it.body.isEticket && showIsdCountryCode) {
                            sheetModifyDetailsBinding.layoutCountryCode.visible()
                        } else {
                            sheetModifyDetailsBinding.layoutCountryCode.gone()
                        }

                        it.body.passengerDetails.forEach {
                            if (it?.seatNumber == singleSelectedSeatNumber) {
                                passName = it?.name
                                passCountryCode = it?.countryCode
                                passAge = it?.age.toString()
                                passPhone = it?.cusMobile
                                passEmail = it?.cusEmail
                                passGender = it?.title
                                passBoardingAt = it?.boardingAt.toString()
                                passDroppingAt = it?.dropOff.toString()
                                seatNumber = it?.seatNumber.toString()
                                passFare = it?.netFare.toString()
                            }
                        }

                        Timber.d("passBoardingAt  = $passBoardingAt -- $passDroppingAt")
                    }

                    sheetModifyDetailsBinding.apply {

                        privilegePhoneValidationCount = PreferenceUtils.getPrivilege()?.phoneNumValidationCount ?: 13
                        etName.setText(passName).toString()
                        etAge.setText(passAge).toString()
                        //  etPhoneNumber.setText(getPhoneNumber(passPhone, country))
                        if(!showIsdCountryCode) {
                            if (privilegePhoneValidationCount == 0) {
                                etPhoneNumber.setMaxLength(14)
                            } else {
                                etPhoneNumber.setMaxLength(privilegePhoneValidationCount ?: 14)
                            }
                        }
                        /*  if (countryCode.isNotEmpty()) {
                             etPhoneNumber.setText(passPhone?.removePrefix("${countryCode[0]}-"))
                                 .toString()
                         } else {
                             etPhoneNumber.setText(passPhone?.removePrefix("-")).toString()
                         }*/
                        etPhoneNumber.setText(passPhone?.substringAfter("-"))
                        if(passCountryCode?.contains("+") == true){
                            etCountryCode.setText(passCountryCode)
                        }else{
                            etCountryCode.setText("+"+passCountryCode)
                        }
                        etEmail.setText(passEmail).toString()
                        autoCompleteGender.setText(passGender).toString()
                        sheetModifyDetailsBinding.autoCompleteGender.setAdapter(
                            ArrayAdapter(
                                requireActivity(),
                                R.layout.spinner_dropdown_item,
                                R.id.tvItem,
                                requireActivity().resources.getStringArray(R.array.genderArray2)
                            )
                        )
                        etBoardingAt.setText("$boardingAt ${it.body.boardingDetails?.depTime}")
                            .toString()
                        etDropOffAt.setText("$droffAt ${it.body.dropOffDetails?.arrTime}")
                            .toString()

                        etFare.setText(passFare)
                    }

                    executeServiceApiCall()

                    sharedViewModel.serviceDetails.observe(this) {
                        Timber.d("responseBodyServiceDetails ${it.body}")

                        if (it.code == 200) {
                            boardingList = mutableListOf()
                            droppingList = mutableListOf()

                            stageDetails = it.body.let {
                                stageDetails
                            }

                            it.body.let {
                                for (i in 0..stageDetails.size.minus(1)) {
                                    if (stageDetails[i].cityId.toString() == sourceId) {
                                        generateBoardingList(i)
                                    } else {
                                        generateDroppingList(i)
                                    }
                                }
                            }

                            sheetModifyDetailsBinding.etBoardingAt.setAdapter(
                                SelectBoardingDroppingArrayAdapter(
                                    requireContext(),
                                    R.layout.spinner_dropdown_item,
                                    R.id.tvItem,
                                    boardingList ?: emptyList<StageDetail>().toMutableList(),
                                    object : SelectBoardingDroppingArrayAdapter.ItemClickListener {
                                        @SuppressLint("SetTextI18n")
                                        override fun onItemSelected(
                                            position: Int,
                                            item: StageDetail,
                                        ) {
                                            sheetModifyDetailsBinding.etBoardingAt.setText(item.name.toString() + " " + item.time.toString())
                                            passBoardingAt = item.id.toString()
                                        }
                                    }
                                )
                            )
                            sheetModifyDetailsBinding.etDropOffAt.setAdapter(
                                SelectBoardingDroppingArrayAdapter(
                                    requireContext(),
                                    R.layout.spinner_dropdown_item,
                                    R.id.tvItem,
                                    droppingList ?: emptyList<StageDetail>().toMutableList(),
                                    object : SelectBoardingDroppingArrayAdapter.ItemClickListener {
                                        @SuppressLint("SetTextI18n")
                                        override fun onItemSelected(
                                            position: Int,
                                            item: StageDetail,
                                        ) {
                                            sheetModifyDetailsBinding.etDropOffAt.setText(item.name.toString() + " " + item.time.toString())
                                            passDroppingAt = item.id.toString()
                                        }
                                    }
                                )
                            )

                            if (it.body.coachDetails?.seatDetails?.size != 0) {
                                for (i in 0 until (it.body.coachDetails?.seatDetails?.size ?: 0)) {

                                    val passengerDetailRemarks =
                                        it.body.coachDetails?.seatDetails?.get(i)?.passengerDetails

                                    if (!passengerDetailRemarks?.remarks.isNullOrEmpty()
                                        && passengerDetailRemarks?.name == sheetModifyDetailsBinding.etName.text.toString()
                                            .trim()
                                    ) {
                                        sheetModifyDetailsBinding.etRemarkSingleUpdate.setText("${passengerDetailRemarks.remarks}")
                                            .toString()
                                    }

                                    if (it.body.coachDetails?.seatDetails?.get(i)?.number == singleSelectedSeatNumber) {
                                        isUpdateFareSeatType =
                                            it.body.coachDetails?.seatDetails?.get(i)?.fareUpdateSeatType
                                                ?: false
                                    }
                                }


                                isShowFareOption =
                                    (isUpdateFareForPhoneBlockedOrConfirmedTickets && isUpdateFareSeatType) && !(isOnbehalfOnlineTicket || isApiTicket || isETicket)

                                if (isShowFareOption) {
                                    sheetModifyDetailsBinding.layoutFare.visible()
                                } else {
                                    sheetModifyDetailsBinding.layoutFare.gone()
                                }
                            }

                        } else if (it.code == 401) {
                            /*DialogUtils.unAuthorizedDialog(
                                requireContext(),
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            (activity as BaseActivity).showUnauthorisedDialog()

                        } else {
                            if (it?.message != null) {
                                it?.message?.let { it -> requireActivity().toast(it) }
                            }
                        }
                    }

                    Timber.d("BoardingList Details: $boardingList ")


                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }

        /*sheetModifyDetailsBinding.etBoardingAt.setOnClickListener {
            openActivityForResult()
        }
        sheetModifyDetailsBinding.etDropOffAt.setOnClickListener {
            openActivityForResult()
        }*/

        sheetModifyDetailsBinding.btnModifyDetails.setOnClickListener {

            sheetModifyDetailsBinding.apply {

                name = etName.text.toString()
                age = etAge.text.toString()
                if (isETicket && showIsdCountryCode) {
                    finalCountryCode = etCountryCode.text.toString()
                } else {
                    finalCountryCode = ""

                }
                phone = if (!countryCode.isNullOrEmpty()) {
                    etPhoneNumber.text.toString().removePrefix("${countryCode[0]}-")
                } else {
                    etPhoneNumber.text.toString()
                }
                gender = autoCompleteGender.text.toString()
                boardingAt = etBoardingAt.text.toString()
                droffAt = etDropOffAt.text.toString()
                emailId = etEmail.text.toString()
                remarks = etRemarkSingleUpdate.text.toString()
                updatedFare = etFare.text.toString()
            }

            if (PreferenceUtils.getPrivilege()!!.phoneNumValidationCount == 0
                || PreferenceUtils.getPrivilege()!!.phoneNumValidationCount == null
            ) {
                if (phone.length < 10) {
                    Toast.makeText(
                        context,
                        getString(R.string.give_valid_mobile_number),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    hitBulkUpdateApi()
                }
            } else {
                hitBulkUpdateApi()
            }

            Timber.d("updateTicketPhoneCheck:1")
        }

        cancelTicketViewModel.loadingState.observe(this) {
            Timber.d("LoadingState ${it.status}")
            when (it) {
                LoadingState.LOADING -> sheetModifyDetailsBinding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> sheetModifyDetailsBinding.includeProgress.progressBar.gone()
                else -> {
                    it.msg?.let { it1 -> requireContext().toast(it1) }
                    sheetModifyDetailsBinding.includeProgress.progressBar.gone()
                }
            }
        }

        cancelTicketViewModel.bulkTicketUpdateResponse.observe(this) {
            sheetModifyDetailsBinding.includeProgress.progressBar.gone()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        DialogUtils.successfulMsgDialog(
                            requireContext(),
                            it.message
                        )

                        if (::sendUpdateDataListener.isInitialized) {
                            sendUpdateDataListener.onDataSend(1, "success")
                        }

                        handler.postDelayed(Runnable {
                            isHandlerRunning = true
                            handler.postDelayed(runnable!!, delay.toLong())

                            /*val intent = if(country.equals("Indonesia", true)) {
                                Intent(requireContext(), TicketDetailsActivityCompose::class.java)
                            } else {
                                Intent(requireContext(), TicketDetailsActivity::class.java)
                            }*/
                            val intent =
                                Intent(requireContext(), TicketDetailsActivityCompose::class.java)
                            intent.putExtra("returnToDashboard", false)
                            intent.putExtra(
                                getString(R.string.put_extra_bulkTicketUpdate),
                                getString(R.string.put_extra_bulkTicketUpdate)
                            )
                            intent.putExtra(getString(R.string.TICKET_NUMBER), ticketNumber)

                        }.also { runnable = it }, delay.toLong())


                        bottomSheetDialog.dismiss()
                        updateBulkDataList.clear()
                        boardingList?.clear()
                        droppingList?.clear()
                        seatMap.clear()
                        updateDataCount = 0

                    }

                    422 -> {
                        it.result.message?.let { it1 -> requireContext().toast(it1) }
                        updateBulkDataList.clear()
                    }

                    401 -> {

                        (activity as BaseActivity).showUnauthorisedDialog()
                    }

                    else -> {
                        it.result.message?.let { it1 -> requireContext().toast(it1) }
                        updateBulkDataList.clear()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }

        bottomSheetDialog.show()
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

                    if (country.equals("India",true) && ::_sheetTicketCancellationBinding.isInitialized) {
                        if (isAllowToShowRemarksForCancelTicket) {
                            sheetTicketCancellationBinding.layoutRemarks.visible()
                        } else {
                            sheetTicketCancellationBinding.layoutRemarks.gone()
                        }
                    }

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

    private fun setConfirmOtpCancelPartialTicketObserver() {

        cancelTicketViewModel.confirmOtpCancelPartialTicketResponse.observe(this) {
            sheetTicketCancellationBinding.includeProgress.progressBar.gone()
//            Timber.d("messageResponse - ${it} , ${it.code}")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        DialogUtils.successfulMsgDialog(
                            requireContext(), getString(R.string.successfully_cancelled_ticket)
                        )

                        if (loginModelPref.role == getString(R.string.role_field_officer)) {
                            handler.postDelayed(Runnable {
                                isHandlerRunning = true
                                runnable?.let { it1 -> handler.postDelayed(it1, DELAY_MILLIS_24) }
                                val intent = Intent(requireContext(), DashboardNavigateActivity::class.java)
                                intent.putExtra("newBooking", true)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                if (activity != null) {
                                    startActivity(intent)
                                    requireActivity().finish()
                                }

                            }.also { runnable = it }, DELAY_MILLIS_24)
                        } else {
                            handler.postDelayed(Runnable {
                                isHandlerRunning = true
                                handler.postDelayed(runnable!!, delay.toLong())
                                val intent= Intent(requireContext(), TicketDetailsActivityCompose::class.java)
                                intent.putExtra(
                                    getString(R.string.put_extra_cancelTicket),
                                    getString(R.string.put_extra_cancelTicket)
                                )
                                intent.putExtra(getString(R.string.TICKET_NUMBER), ticketNumber)
                                this.startActivity(intent)

                            }.also { runnable = it }, delay.toLong())
                        }
                    }

                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }

                    else -> {
                        it.message?.let { it1 -> requireActivity().toast(it1) }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.something_went_wrong))
            }
        }
    }

    private fun setCancelPartialOtpTicketObserver(bottomSheetDialog: BottomSheetDialog) {
        cancelTicketViewModel.cancelPartialTicketViewModel.observe(this) {
            sheetTicketCancellationBinding.includeProgress.progressBar.gone()

            if (it != null) {
                if (it.code == 200) {
                    bottomSheetDialog.dismiss()

                    DialogUtils.successfulMsgDialog(
                        requireActivity(), getString(R.string.successfully_cancelled_ticket)
                    )

                    handler.postDelayed(Runnable {
                        isHandlerRunning = true
                        handler.postDelayed(runnable!!, DELAY_MILLIS_24)
                        val intent = Intent(requireContext(), TicketDetailsActivityCompose::class.java)
                        intent.putExtra(
                            activity?.getString(R.string.put_extra_cancelTicket),
                            activity?.getString(R.string.put_extra_cancelTicket)
                        )
                        intent.putExtra(activity?.getString(R.string.TICKET_NUMBER), ticketNumber)
                        this.startActivity(intent)

                        intent.putExtra(
                            activity?.getString(R.string.TICKET_NUMBER),
                            ticketNumber
                        )
                        if (activity != null && activity?.isFinishing==false) {
                            activity?.startActivity(intent)
                        }
                    }.also { runnable = it }, DELAY_MILLIS_24)

                } else if (it.result?.key?.isNotEmpty() == true) {
                    if (cancelOtpLayoutDialogOpenCount == 0) {
                        DialogUtils.cancelOtpLayoutDialog(
                            requireContext(),
                            object : DialogSingleButtonListener {
                                override fun onSingleButtonClick(str: String) {

                                    if (str == getString(R.string.unauthorized)) {
                                        //clearAndSave(requireContext())
                                        PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
                                        val intent =
                                            Intent(requireActivity(), LoginActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    }
                                    cancelOtp = str

                                    if (str == "resend") {
                                        executeCancelPartialTicketApiCall()
                                    } else {
                                        executeConfirmOtpCancelPartialTicketApiCall(selectedSeatNumber.toString())
                                    }
                                }

                            },
                            this,
                            dimissAction = {})
                        cancelOptkey = it.result?.key.toString()
                        requireActivity().toast(it.result?.message.toString())
                        cancelOtpLayoutDialogOpenCount++
                    }
                    cancelOptkey = it.result?.key.toString()

                } else {
                    if (it?.message != null)
                        requireActivity().toast(it.message)
                    it.result?.message?.let { it1 -> requireActivity().toast(it1) }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun setCancelPartialTicketObserver(bottomSheetDialog: BottomSheetDialog) {
        dialogPartialTicketOpenCount++
        cancelTicketViewModel.cancelPartialTicketViewModel.observe(this) {
            sheetTicketCancellationBinding.includeProgress.progressBar.gone()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        bottomSheetDialog.dismiss()

                        DialogUtils.successfulMsgDialog(
                            requireContext(),
                            getString(R.string.successfully_cancelled_ticket)
                        )

                        if (loginModelPref.role == getString(R.string.role_field_officer)) {
                            handler.postDelayed(Runnable {
                                isHandlerRunning = true
                                runnable?.let { it1 -> handler.postDelayed(it1, DELAY_MILLIS_24) }
                                val intent = Intent(requireContext(), DashboardNavigateActivity::class.java)
                                intent.putExtra("newBooking", true)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                if (activity != null) {
                                    startActivity(intent)
                                    requireActivity().finish()
                                }

                            }.also { runnable = it }, DELAY_MILLIS_24)
                        } else {
                            handler.postDelayed(Runnable {
                                isHandlerRunning = true
                                handler.postDelayed(runnable!!, delay.toLong())
                                val intent = Intent(requireContext(), TicketDetailsActivityCompose::class.java)
                                intent.putExtra(
                                    getString(R.string.put_extra_cancelTicket),
                                    getString(R.string.put_extra_cancelTicket)
                                )
                                intent.putExtra(getString(R.string.TICKET_NUMBER), ticketNumber)
                                if (activity != null) {
                                    requireContext().startActivity(intent)
                                }

                            }.also { runnable = it }, delay.toLong())
                        }
//                        builder.cancel()
                        currentCheckedItem.clear()
                        selectedSeatNumber.clear()
                        selectedCancellationType = ""
                    }

                    422 -> {
                        cancelDialogBinding.btnLight.isEnabled = true

                        if (it?.result?.message != null) {
                            it.result?.message?.let { it1 -> requireContext().toast(it1) }
                        }
                    }

                    401 -> {
                        cancelDialogBinding.btnLight.isEnabled = true
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }

                    else -> {
                        cancelDialogBinding.btnLight.isEnabled = true

                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> requireContext().toast(it1) }
                        } else if (it.result == null) {
                            it.message?.let { it1 -> requireContext().toast(it1) }
                        } else {
                            requireContext().toast(getString(R.string.opps))
                        }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
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

    private fun setEditPassengerAdapter() {
        sheetUpdatePassengersDetailsBinding.rvPassengers.layoutManager =
            LinearLayoutManager(
                /* context = */ requireContext(),
                /* orientation = */ LinearLayoutManager.VERTICAL,
                /* reverseLayout = */ false
            )
        sheetUpdatePassengersDetailsBinding.rvPassengers.adapter = EditPassengersAdapter(requireContext(), passengerDetailList, this)
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

    // Call APIs
    private fun executeUserListApi() {
        val apiHelper = UserListApiHelper(
            activity = requireActivity(),
            blockViewModel = blockViewModel,
            loginModelPref = loginModelPref,
            userTypeId = userTypeId,
            locale = locale,
            userListMethodName = user_list_method_name
        )
        apiHelper.callUserListApi()
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

    private fun executeCancellationDetailsApiCall(pnrNumber: Any, isBima: Boolean) {
        val apiHelper = CancellationDetailsApiHelper(
            activity = requireActivity(),
            cancelTicketViewModel = cancelTicketViewModel,
            apiKey = apiKey,
            locale = locale,
            operatorApiKey = operator_api_key,
            selectedCancellationType = selectedCancellationType,
            ticketCancellationPercentage = ticketCancellationPercentage,
            jsonFormat = json_format,
            cancellationDetailsTicketMethodName = cancellation_details_ticket_method_name,
            selectedSeatNumber = selectedSeatNumber,
            isZeroPercentCancellationCheck = isZeroPercentCancellationCheck
        )

        apiHelper.callCancellationDetailsApi(pnrNumber, isBima)
    }

    private fun executeZeroCancellationDetailsApiCall(pnrNumber: String, isBima: Boolean) {
        val apiHelper = ZeroCancellationDetailsApiHelper(
            activity = requireActivity(),
            cancelTicketViewModel = cancelTicketViewModel,
            apiKey = apiKey,
            locale = locale,
            operatorApiKey = operator_api_key,
            selectedCancellationType = selectedCancellationType,
            jsonFormat = json_format,
            cancellationDetailsTicketMethodName = cancellation_details_ticket_method_name,
            selectedSeatNumber = selectedSeatNumber.toString(),
            isZeroPercentCancellationCheck = isZeroPercentCancellationCheck
        )

        apiHelper.callZeroCancellationDetailsApi(pnrNumber, isBima)
    }

    private fun executeCancelPartialTicketApiCall() {
        val apiHelper = CancelPartialTicketApiHelper(
            activity = requireActivity(),
            cancelTicketViewModel = cancelTicketViewModel,
            apiKey = apiKey,
            locale = locale,
            operatorApiKey = operator_api_key,
            selectedCancellationType = selectedCancellationType,
            jsonFormat = json_format,
            selectedSeatNumber = selectedSeatNumber.toString(),
            ticketCancellationPercentage = ticketCancellationPercentage,
            ticketNumber = ticketNumber,
            travelDate = travelDate,
            isZeroPercentCancellationCheck = isZeroPercentCancellationCheck,
            isCanCancelTicketForUser = isCanCancelTicketForUser,
            isOnbehalfOnlineAgentFlag = isOnbehalfOnlineAgentFlag,
            cancelOnBehalOf = cancelOnBehalOf,
            isBimaTicket = isBimaTicket,
            sendSms = sendSms,
            cancellationDetailsTicketMethodName = cancellation_details_ticket_method_name,
            authPin = "",
            remarks = ""
        )

        apiHelper.callCancelPartialTicketApi()
    }

    private fun executeConfirmOtpCancelPartialTicketApiCall(seatNoSelected: String) {

        val apiHelper = ConfirmOtpCancelPartialTicketApiHelper(
            activity = requireActivity(),
            cancelTicketViewModel = cancelTicketViewModel,
            apiKey = apiKey,
            cancelOptKey = cancelOptkey,
            cancelOtp = cancelOtp,
            selectedCancellationType = selectedCancellationType,
            locale = locale,
            operatorApiKey = operator_api_key,
            jsonFormat = json_format,
            ticketCancellationPercentage = ticketCancellationPercentage,
            ticketNumber = ticketNumber,
            travelDate = travelDate,
            isZeroPercentCancellationCheck = isZeroPercentCancellationCheck,
            isCanCancelTicketForUser = isCanCancelTicketForUser,
            isOnbehalfOnlineAgentFlag = isOnbehalfOnlineAgentFlag,
            cancelOnBehalOf = cancelOnBehalOf,
            isBimaTicket = isBimaTicket,
            confirmOtpCancelPartialTicketMethodName = confirm_otp_cancel_partial_ticket_method_name
        )

        apiHelper.callConfirmOtpCancelPartialTicketApi(seatNoSelected)
    }

    private fun executeBulkTicketUpdateApiCall() {

        val apiHelper = BulkTicketUpdateApiHelper(
            activity = requireActivity(),
            cancelTicketViewModel = cancelTicketViewModel,
            apiKey = apiKey,
            emailId = emailId,
            ticketNumber = ticketNumber,
            jsonFormat = json_format,
            updateBulkDataList = updateBulkDataList,
            passBoardingAt = passBoardingAt,
            passDroppingAt = passDroppingAt,
            locale = locale,
            remarks = remarks,
            isBimaTicket = isBimaTicket,
            bulkTicketUpdateMethodName = bulk_ticket_update_method_name
        )

        apiHelper.callBulkTicketUpdateApi()
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

    private fun hitBulkUpdateApi() {
        val countyCode = if (phone.isEmpty()) {
            ""
        } else {
            if (countryCode.isEmpty()) {
                ""
            } else
                "${countryCode[0]}"
        }

        val list = passengerList.distinctBy { it?.seatNumber }
        list.forEach {

            if (it?.seatNumber == seatNumber) {
                val passengerDetailData = UpdateData(
                    isSingleSeat = seatNumber,
                    phoneNumber = phone,
                    passName = name,
                    passAge = age,
                    email = emailId,
                    passGender = gender,
                    boardingAt = passBoardingAt,
                    dropOff = passDroppingAt,
                    countryCode = finalCountryCode,
                    updatedFare = sheetModifyDetailsBinding.etFare.text.toString()
                )
                updateBulkDataList.add(passengerDetailData)
            } else {
                val passengerDetailData = UpdateData(
                    isSingleSeat = it?.seatNumber,
                    phoneNumber = "${it?.cusMobile}",
                    passName = it?.name,
                    passAge = it?.age.toString(),
                    email = it?.cusEmail,
                    passGender = it?.title,
                    boardingAt = it?.boardingAt.toString(),
                    dropOff = it?.dropOff.toString(),
                    countryCode = finalCountryCode

                )
                updateBulkDataList.add(passengerDetailData)
            }
        }

        executeBulkTicketUpdateApiCall()
    }

    //--------------//
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

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val returnResultBoardingPointId = result.data?.getStringExtra(getString(R.string.boarding_point_id_key)).toString()
                val returnResultDroppingPointId = result.data?.getStringExtra(getString(R.string.dropping_point_id_key)).toString()

                val returnResultBoardingPoint = result.data?.getStringExtra(getString(R.string.boarding_point_key)).toString()
                val returnResultDroppingPoint = result.data?.getStringExtra(getString(R.string.dropping_point_key)).toString()

                dialogUpdatePersonalDetailsBinding?.apply {
                    etBoardingAt.setText(returnResultBoardingPoint)
                    etDropOffAt.setText(returnResultDroppingPoint)
                }

                if (this::_sheetModifyDetailsBinding.isInitialized) {
                    sheetModifyDetailsBinding.etBoardingAt.setText(returnResultBoardingPoint)
                    sheetModifyDetailsBinding.etDropOffAt.setText(returnResultDroppingPoint)
                }

                if (this::_sheetUpdatePassengersDetailsBinding.isInitialized) {
                    sheetUpdatePassengersDetailsBinding.etBoardingAt.setText(
                        returnResultBoardingPoint
                    )
                    sheetUpdatePassengersDetailsBinding.etDropOffAt.setText(
                        returnResultDroppingPoint
                    )
                }

                passBoardingAt = returnResultBoardingPointId
                passDroppingAt = returnResultDroppingPointId
//                Timber.d("resultData>> $returnResultBoardingPoint - $returnResultDroppingPoint >>> $passBoardingAt $passDroppingAt")
            }
        }

    private fun openActivityForResult() {
        val intent =
            Intent(requireContext(), SelectBoardingDroppingPointActivity::class.java).apply {
                putExtra(getString(R.string.tag), getString(R.string.boarding))
                putExtra(getString(R.string.boarding), boardingList as? Serializable)
                putExtra(getString(R.string.dropping), droppingList as? Serializable)
                putExtra(getString(R.string.bus_type), busType)
                putExtra(getString(R.string.dep_time), depTime)
                putExtra(getString(R.string.travel_date), travelDate)
                putExtra(getString(R.string.booking_source), source)
                putExtra(getString(R.string.destination), destination)
                putExtra(getString(R.string.toolbar_title), getString(R.string.update_ticket))
            }

        resultLauncher.launch(intent)
    }

    private fun ticketCancelDialog(
        context: Context,
        title: String,
        message: String,
        srcDest: String,
        journeyDate: String,
        ticketCancellationPercentage: String,
        seatNo: String,
        cancellationAmount: String,
        refundAmount: String,
        buttonLeftText: String,
        buttonRightText: String,
        bottomSheetDialog: BottomSheetDialog,
        dialogButtonTagListener: DialogButtonTagListener,
    ) {
        val builder = AlertDialog.Builder(context).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
        val binding: DialogCancelTicketBinding =
            DialogCancelTicketBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)

        cancelDialogBinding = binding

        binding.apply {
            tvRefundAmount.visible()
            tvCancellationAmount.visible()
            viewBottom2.visible()
            tvCanellationAmmountText.visible()
            tvRefundText.visible()
            tvHeader.text = title
            tvMessage.text = message
            tvHeaderText.text = srcDest
            tvSubtitle.text = journeyDate
            tvTicketCancellationPercentage.text = ticketCancellationPercentage
            tvSelectedSeatNo.text = seatNo
            tvCancellationAmount.text = cancellationAmount
            tvRefundAmount.text = refundAmount
            btnDark.text = buttonLeftText
            btnLight.text = buttonRightText
        }

        if (message.isEmpty()) {
            binding.tvMessage.gone()
            binding.viewBottom2.gone()
        } else {
            binding.tvMessage.visible()
            binding.viewBottom2.visible()
        }

        binding.btnDark.setOnClickListener {
            builder.cancel()
            dialogButtonTagListener.onLeftButtonClick(binding.btnDark)
        }

        binding.btnLight.setOnClickListener {
            cancelOtpLayoutDialogOpenCount = 0
            executeCancelPartialTicketApiCall()
            binding.btnLight.isEnabled = false
        }

        setCancelPartialTicketObserver(bottomSheetDialog)
        setConfirmOtpCancelPartialTicketObserver()
        setCancelPartialOtpTicketObserver(bottomSheetDialog)
        builder.setView(binding.root)
        builder.show()
    }

    private fun updateSinglePassengersDialog(position: Int, seatNo: String) {
        privilegePhoneValidationCount =
            PreferenceUtils.getPreference(getString(R.string.mobile_number_length), 0).toString()
                .toInt()
        Timber.d("privilege phone validation : $privilegePhoneValidationCount")

        val builder = AlertDialog.Builder(context).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background

        dialogUpdateSinglePassengerDetailsBinding = DialogUpdateSinglePassengerDetailsBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)

        executeServiceApiCall()

        sharedViewModel.serviceDetails.observe(this) {
            if (it.code != null) {
                when (it.code) {
                    200 -> {
                        boardingList = mutableListOf()
                        droppingList = mutableListOf()
                        stageDetails = it.body.stageDetails!!

                        it.body.let {
                            for (i in 0..stageDetails.size.minus(1)) {
                                if (stageDetails[i].cityId.toString() == sourceId) {
                                    generateBoardingList(i)
                                } else {
                                    generateDroppingList(i)
                                }
                            }
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

                    else -> it.message?.let { it1 -> requireContext().toast(it1) }
                }
            } else {
                requireContext().toast(getString(R.string.something_went_wrong))
            }
        }

        if (isAllowProvisiontoSelectMultipleBoardingDropOff == true && isApplyBpDpFare == false) {
            dialogUpdateSinglePassengerDetailsBinding?.layoutBoardingAt?.visible()
            dialogUpdateSinglePassengerDetailsBinding?.layoutDropOffAt?.visible()
        } else {
            dialogUpdateSinglePassengerDetailsBinding?.layoutBoardingAt?.gone()
            dialogUpdateSinglePassengerDetailsBinding?.layoutDropOffAt?.gone()
        }

        passName = getPassengerDetailResponse[position].name
        passAge = getPassengerDetailResponse[position].age.toString()
        passPhone = getPassengerDetailResponse[position].cusMobile
        passEmail = getPassengerDetailResponse[position].cusEmail
        passGender = getPassengerDetailResponse[position].title
        passIsSingleSeat = getPassengerDetailResponse[position].seatNumber
        passBoardingAt = getPassengerDetailResponse[position].boardingAt.toString()
        passDroppingAt = getPassengerDetailResponse[position].dropOff.toString()

        dialogUpdateSinglePassengerDetailsBinding?.apply {

            etBoardingAt.setOnClickListener {
                if (etBoardingAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                }
                if (etDropOffAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                }
                openActivityForResult()
            }

            etDropOffAt.setOnClickListener {
                if (etBoardingAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                }
                if (etDropOffAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                }
                openActivityForResult()
            }

            etName.setText(passName).toString()
            etAge.setText(passAge).toString()

            if (countryCode.isNotEmpty()) {
                etPhoneNumber.setText(passPhone?.removePrefix("${countryCode[0]}-")).toString()
            } else {
                etPhoneNumber.setText(passPhone?.removePrefix("-")).toString()
            }

            autoCompleteGender.setText(passGender).toString()
            etBoardingAt.setText(boardingAt)
            etDropOffAt.setText(droffAt)
            if(!showIsdCountryCode){
                if (privilegePhoneValidationCount == 0) {
                    etPhoneNumber.setMaxLength(14)
                } else {
                    etPhoneNumber.setMaxLength(privilegePhoneValidationCount ?: 14)
                }
            }


            autoCompleteGender.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    requireContext().resources.getStringArray(R.array.genderArray2)
                )
            )

            btnSaveDetails.setOnClickListener {

                updateBulkDataList.clear()
                name = etName.text.toString()
                age = etAge.text.toString()
                phone = etPhoneNumber.text.toString()
                gender = autoCompleteGender.text.toString()
                boardingAt = etBoardingAt.text.toString()
                droffAt = etDropOffAt.text.toString()
                getPassengerDetailResponse.forEach {
                    if (seatNo == it.seatNumber) {
                        var tempPhone = ""
                        if (!phone.isNullOrEmpty()) {
                            tempPhone = "$phone"
                            Timber.d("tempHone : ", tempPhone)
                        }
                        //${countryCode[0]}-$phone
                        val passengerDetailData = UpdateData(
                            isSingleSeat = passIsSingleSeat.toString(),
                            phoneNumber = tempPhone,
                            passName = name,
                            passAge = age,
                            email = "",
                            passGender = gender,
                            boardingAt = passBoardingAt,
                            dropOff = passDroppingAt
                        )
                        updateBulkDataList.add(passengerDetailData)
                    } else {
                        var tempPhone = ""
                        if (!it.cusMobile.isNullOrEmpty()) {
                            if (it.cusMobile.contains("-")) {
                                tempPhone = "${it.cusMobile.substringAfter("-")}"
                            }
                            /*else {
                                tempPhone = "${countryCode[0]}-${it.cusMobile}"
                            }*/

                        }
                        Timber.d("tempHone2 : ", tempPhone)

                        val passengerDetailData = UpdateData(
                            isSingleSeat = it.seatNumber,
                            phoneNumber = tempPhone,
                            passName = it.name,
                            passAge = it.age.toString(),
                            email = it.cusEmail,
                            passGender = it.title,
                            boardingAt = it.boardingAt.toString(),
                            dropOff = it.dropOff.toString()
                        )
                        updateBulkDataList.add(passengerDetailData)
                    }
                }

                executeBulkTicketUpdateApiCall()
                cancelTicketViewModel.loadingState.observe(requireActivity()) {
                    Timber.d("LoadingState ${it.status}")
                    when (it) {

                        LoadingState.LOADING -> dialogUpdateSinglePassengerDetailsBinding.let {
                            includeProgress.progressBar.visible()
                        }

                        LoadingState.LOADED -> dialogUpdateSinglePassengerDetailsBinding.let {
                            includeProgress.progressBar.gone()
                        }

                        else -> {
                            it.msg?.let { it1 -> context?.toast(it1) }

                            dialogUpdateSinglePassengerDetailsBinding.let {
                                includeProgress.progressBar.gone()
                            }
                        }
                    }
                }

                cancelTicketViewModel.bulkTicketUpdateResponse.observe(requireActivity()) {
                    dialogUpdateSinglePassengerDetailsBinding?.includeProgress?.progressBar?.gone()
                    if (it != null) {
                        when (it.code) {
                            200 -> {
                                DialogUtils.successfulMsgDialog(
                                    requireContext(), it.message
                                )

                                executeTicketDetailsApiCall(it.pnrNumber)
                                builder.cancel()
                            }

                            422 -> {
                                it.result.message?.let { it1 -> requireContext().toast(it1) }
                            }
                            else -> {
                                it.result.message?.let { it1 -> requireContext().toast(it1) }
                            }
                        }
                    } else {
                        requireContext().toast(getString(R.string.server_error))
                    }
                }
                builder.cancel()
            }
            tvCancel.setOnClickListener {
                builder.cancel()
            }
        }

        builder.setView(dialogUpdateSinglePassengerDetailsBinding?.root)
        builder.show()
    }

    fun updatePassengersDialog(position: Int) {
        val countries = getAllCountries(requireContext())

        val builder = AlertDialog.Builder(context).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
        dialogUpdatePersonalDetailsBinding = DialogUpdatePersonalDetailsBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)

        executeServiceApiCall()

        if (isAllowProvisiontoSelectMultipleBoardingDropOff == true && isApplyBpDpFare == false) {
            dialogUpdatePersonalDetailsBinding?.layoutBoardingAt?.visible()
            dialogUpdatePersonalDetailsBinding?.layoutDropOffAt?.visible()

        } else {
            dialogUpdatePersonalDetailsBinding?.layoutBoardingAt?.gone()
            dialogUpdatePersonalDetailsBinding?.layoutDropOffAt?.gone()
        }

        passName = getPassengerDetailResponse[position].name
        passAge = getPassengerDetailResponse[position].age.toString()
        passPhone = getPassengerDetailResponse[position].cusMobile
        passEmail = getPassengerDetailResponse[position].cusEmail
        passCountryCode = getPassengerDetailResponse[position].countryCode
        passGender = getPassengerDetailResponse[position].title
        passIsSingleSeat = getPassengerDetailResponse[position].seatNumber
        passBoardingAt = getPassengerDetailResponse[position].boardingAt.toString()
        passDroppingAt = getPassengerDetailResponse[position].dropOff.toString()

        privilegePhoneValidationCount = PreferenceUtils.getPreference(getString(R.string.mobile_number_length), 0).toString().toInt()

        dialogUpdatePersonalDetailsBinding?.apply {

            etBoardingAt.setOnClickListener {
                if (etBoardingAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                }
                if (etDropOffAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                }
                openActivityForResult()
            }

            etCountryCode.setOnClickListener {
                if (isETicket && showIsdCountryCode) {
                    isSingleUpdate = false
                    countryPickerDialog = showCountryPickerBottomsheet(
                        context = requireContext(),
                        countriesList = countries,
                        onItemClickListener = this@BaseUpdateCancelTicket
                    )
                }
            }

            etDropOffAt.setOnClickListener {
                if (etBoardingAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                }
                if (etDropOffAt.text?.isNullOrEmpty() == true) {
                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                }
                openActivityForResult()
            }

            if (updateBulkDataList.size > seatMap.size) {

//                Timber.d("UpdatedList-isSingleSeat=${updateBulkDataList[position.toInt()].isSingleSeat}")
//                Timber.d("UpdatedList-seatMapForSeat=${seatMap[position.toInt()]}")

                if (updateBulkDataList[position.toInt()].isSingleSeat == seatMap[position.toInt()]) {
                    etName.setText(updateBulkDataList[position.toInt()].passName).toString()
                    etAge.setText(updateBulkDataList[position.toInt()].passAge).toString()
                    autoCompleteGender.setText(updateBulkDataList[position.toInt()].passGender).toString()
                    if (countryCode.isNotEmpty()) {
                        etPhoneNumber.setText(
                            updateBulkDataList[position.toInt()].phoneNumber?.substringAfter("-")
                        ).toString()
                    }
                    if(passCountryCode?.contains("+") == true) {

                        etCountryCode.setText(updateBulkDataList[position.toInt()].countryCode)
                    }else{
                        etCountryCode.setText("+"+updateBulkDataList[position.toInt()].countryCode)

                    }

                    for (j in 0 until (boardingList?.size ?: 0)) {
                        val boardingName = boardingList?.get(j)?.name

                        if (boardingName == boardingAt) {
                            etBoardingAt.setText(boardingName).toString()
                        }
                    }

                    for (j in 0 until (droppingList?.size ?: 0)) {
                        val droppingName = droppingList?.get(j)?.name

                        if (droppingName == droffAt) {
                            etDropOffAt.setText(droppingName).toString()
                        }
                    }
                } else {
                    etName.setText(passName).toString()
                    etAge.setText(passAge).toString()
                    autoCompleteGender.setText(passGender).toString()
                    etBoardingAt.setText(boardingAt)
                    etDropOffAt.setText(droffAt)
                    if(!showIsdCountryCode) {
                        if (privilegePhoneValidationCount == 0) {
                            etPhoneNumber.setMaxLength(14)
                        } else {
                            etPhoneNumber.setMaxLength(privilegePhoneValidationCount ?: 14)
                        }
                    }
                    Timber.d("finalMobileNumber 1 $finalMobileNumber $passPhone")
                  /*  if (countryCode.isNotEmpty()) {
                        etPhoneNumber.setText(passPhone?.removePrefix("${countryCode[0]}-"))
                            .toString()
                    } else {
                        etPhoneNumber.setText(passPhone?.removePrefix("-")).toString()
                    }*/

                        etPhoneNumber.setText(
                            passPhone?.substringAfter("-")
                        ).toString()
                    if(passCountryCode?.contains("+") == true){
                        etCountryCode.setText(passCountryCode)
                    }else{
                        etCountryCode.setText("+" +passCountryCode)
                    }
                    //   etPhoneNumber.setText(getPhoneNumber(passPhone, country))

                }

            } else {
                etName.setText(passName).toString()
                etAge.setText(passAge).toString()
                autoCompleteGender.setText(passGender).toString()
                etBoardingAt.setText(boardingAt)
                etDropOffAt.setText(droffAt)
                if(!showIsdCountryCode) {
                    if (privilegePhoneValidationCount == 0) {
                        etPhoneNumber.setMaxLength(14)
                    } else {
                        etPhoneNumber.setMaxLength(privilegePhoneValidationCount ?: 14)
                    }
                }
//                Timber.d("finalMobileNumber 2 $finalMobileNumber $passPhone")
                /*if (countryCode.isNotEmpty()) {
                    etPhoneNumber.setText(passPhone?.removePrefix("${countryCode[0]}-")).toString()
                } else {
                    etPhoneNumber.setText(passPhone?.removePrefix("-")).toString()
                }*/

                etPhoneNumber.setText(
                    passPhone?.substringAfter("-")
                ).toString()

                if(passCountryCode?.contains("+") == true){
                    etCountryCode.setText(passCountryCode)
                }else{
                    etCountryCode.setText("+" +passCountryCode)
                }
            }

            autoCompleteGender.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    requireContext().resources.getStringArray(R.array.genderArray2)
                )
            )

//            Timber.d("UpdatedList=$updateBulkDataList")
//            Timber.d("UpdatedList=SeatMap$seatMap")

            dialogUpdatePersonalDetailsBinding.apply {

                etName.addTextChangedListener(object :
                    TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        name = s.toString()

                        if (name.isNotEmpty()) {
                            updateButtonEnable()
                            isEditable = true
                            layoutName.isErrorEnabled = false
                        } else {
                            if (!isEditable) {
                                updateButtonDisable()
                            }
                        }
                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })

                etAge.addTextChangedListener(object :
                    TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        age = s.toString()
                        if (age.isNotEmpty()) {
                            updateButtonEnable()
                            isEditable = true
                            layoutAge.isErrorEnabled = false
                        } else {
                            if (!isEditable) {
                                updateButtonDisable()
                            }
                        }
                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })

                autoCompleteGender.addTextChangedListener(object :
                    TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        gender = s.toString()

                        if (gender.isNotEmpty()) {
                            updateButtonEnable()
                            isEditable = true
                        } else {
                            if (!isEditable) {
                                updateButtonDisable()
                            }
                        }
                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })

                etCountryCode.addTextChangedListener(object :
                    TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                      //  gender = s.toString()
                            updateButtonEnable()
                            isEditable = true

                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })

                etPhoneNumber.addTextChangedListener(object :
                    TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {
                        phone = s.toString()

                        if (privilegePhoneValidationCount != 0 && privilegePhoneValidationCount != null
                        ) {
                            if(!showIsdCountryCode) {
                                etPhoneNumber.setMaxLength(privilegePhoneValidationCount!!)
                            }

                            if (phone.isNotEmpty() && phone.length == privilegePhoneValidationCount!!) {
                                updateButtonEnable()
                                isEditable = true
                                layoutPhoneNumber.isErrorEnabled = false

                            } else {
                                if(showIsdCountryCode) {
                                    updateButtonEnable()
                                } else {
                                    if (!isEditable) {
                                        updateButtonDisable()
                                    }
                                }
                            }
                        } else {
                            if(!showIsdCountryCode) {
                                etPhoneNumber.setMaxLength(14)
                            }
                            updateButtonEnable()
                            isEditable = true
                            layoutPhoneNumber.isErrorEnabled = false
                        }
                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })

                etBoardingAt.addTextChangedListener(object :
                    TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        boardingAt = s.toString()
                        if (boardingAt?.isNotEmpty() == true) {
                            updateButtonEnable()
                            isEditable = true
                        } else {
                            if (!isEditable) {
                                updateButtonDisable()
                            }
                        }
                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })

                etDropOffAt.addTextChangedListener(object :
                    TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        droffAt = s.toString()

                        if (droffAt?.isNotEmpty() == true) {
                            updateButtonEnable()
                            isEditable = true
                        } else {
                            if (!isEditable) {
                                updateButtonDisable()
                            }
                        }
                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })
            }

            tvCancel.setOnClickListener {
                builder.cancel()
            }

            btnSaveDetails.setOnClickListener {
                when {
                    etName.text.toString().isEmpty() -> {
                        layoutName.isErrorEnabled = true
                        layoutName.error = "enter name"
                    }

                    etAge.text.toString().isEmpty() -> {
                        layoutAge.isErrorEnabled = true
                        layoutAge.error = "enter age"
                    }

                    else -> {
                        var mobileNumberFinal = ""

                        if (name.isEmpty()) {
                            name = etName.text.toString()
                        }

                        if (age.isEmpty())
                            age = etAge.text.toString()

                        if (phone.isEmpty()) {
                            phone = etPhoneNumber.text.toString()
                            mobileNumberFinal = phone
                        } else {
                            phone = etPhoneNumber.text.toString()
                            mobileNumberFinal = phone
                            // mobileNumberFinal = "${countryCode[0]}$phone"
                        }

                        val countryCode = if(isETicket && showIsdCountryCode){
                             etCountryCode.text.toString()
                        }else{
                            ""
                        }

                        if (gender.isEmpty())
                            gender = autoCompleteGender.text.toString()

                        if (boardingAt?.isEmpty() == true)
                            boardingAt = etBoardingAt.text.toString()

                        if (droffAt?.isEmpty() == true)
                            droffAt = etDropOffAt.text.toString()

                        passengerDetailData = UpdateData(
                            isSingleSeat = passIsSingleSeat.toString(),
                            phoneNumber = mobileNumberFinal,
                            passName = name,
                            passAge = age,
                            email = "",
                            passGender = gender,
                            boardingAt = passBoardingAt,
                            dropOff = passDroppingAt,
                            countryCode = countryCode
                        )

                        if (updateDataCount == 0) {
                            for (i in 0 until getPassengerDetailResponse.size) {
                                if (passengerDetailData.isSingleSeat == getPassengerDetailResponse[i].seatNumber) {
                                    updateBulkDataList.add(passengerDetailData)
                                } else {
                                    val temp = UpdateData(
                                        isSingleSeat = getPassengerDetailResponse[i].seatNumber,
                                        phoneNumber = getPassengerDetailResponse[i].cusMobile,
                                        passName = getPassengerDetailResponse[i].name,
                                        passAge = getPassengerDetailResponse[i].age.toString(),
                                        email = getPassengerDetailResponse[i].cusEmail,
                                        passGender = getPassengerDetailResponse[i].title,
                                        boardingAt = getPassengerDetailResponse[i].boardingAt.toString(),
                                        dropOff = getPassengerDetailResponse[i].dropOff.toString(),
                                    )
                                    updateBulkDataList.add(i, temp)
                                }
                            }
                        }

                        if (seatMap[position] == updateBulkDataList[position].isSingleSeat) {

                            updateBulkDataList[position] = passengerDetailData
                            seatMap += Pair(
                                position,
                                getPassengerDetailResponse[position].seatNumber
                            )


                            Timber.d("updateBulkDataList-updated")
                        } else {
                            seatMap += Pair(

                                position,
                                getPassengerDetailResponse[position].seatNumber
                            )
                            updateBulkDataList.removeAt(position)
                            updateBulkDataList.add(position, passengerDetailData)
                            Timber.d("updateBulkDataList-added")
                        }

                        updateDataCount++
                        builder.cancel()
                    }
                }
            }

            if (isETicket && showIsdCountryCode) {
                layoutCountryCode.visible()
            } else {
                layoutCountryCode.gone()
            }
        }

        if (isETicket && showIsdCountryCode) {
            dialogUpdatePersonalDetailsBinding?.layoutCountryCode?.visible()
        } else {
            dialogUpdatePersonalDetailsBinding?.layoutCountryCode?.gone()

        }

        builder.setView(dialogUpdatePersonalDetailsBinding?.root)
        builder.show()
    }


    private fun updateButtonEnable() {
        dialogUpdatePersonalDetailsBinding?.btnSaveDetails?.setBackgroundResource(R.drawable.button_selected_bg)
        dialogUpdatePersonalDetailsBinding?.btnSaveDetails?.isEnabled = true
        sheetUpdatePassengersDetailsBinding.btnUpdateBulkTicket.isEnabled = true
        sheetUpdatePassengersDetailsBinding.btnUpdateBulkTicket.setBackgroundResource(R.drawable.button_selected_bg)
    }

    private fun updateButtonDisable() {
        dialogUpdatePersonalDetailsBinding?.btnSaveDetails?.setBackgroundResource(R.drawable.button_default_bg)
        dialogUpdatePersonalDetailsBinding?.btnSaveDetails?.isEnabled = false
        sheetUpdatePassengersDetailsBinding.btnUpdateBulkTicket.isEnabled = false
        sheetUpdatePassengersDetailsBinding.btnUpdateBulkTicket.setBackgroundResource(R.drawable.button_default_bg)
    }

    override fun onItemCheck(item: PassengerDetail?) {

        currentCheckedItem.add(item)

        if (passengerDetailList?.size == 1 && currentCheckedItem.size == 0) {
            selectedSeatNumber.append(passengerDetailList?.get(0)?.seatNumber)
            isSingleSeatClick = false
        } else {
            sheetTicketCancellationBinding.btnCancelTicket.setBackgroundResource(R.drawable.button_selected_bg)
        }
    }

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

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onLeftButtonClick(tag: View?) {
        currentCheckedItem.clear()
        selectedCancellationType = "1"
        isSeatClick = 0
    }

    override fun onRightButtonClick(tag: View?) {

        executeBulkTicketUpdateApiCall()

        cancelTicketViewModel.bulkTicketUpdateResponse.observe(requireActivity()) {
//            binding.includeProgress.progressBar.gone()
            if (it != null) {
                when (it.code) {
                    200 -> {

                        DialogUtils.successfulMsgDialog(
                            requireContext(), it.message
                        )

                        updateBulkDataList.clear()
                        boardingList?.clear()
                        droppingList?.clear()
                        seatMap.clear()
                        updateDataCount = 0
                    }

                    422 -> {
                        requireContext().toast(it.result.message!!)
                        updateBulkDataList.clear()
                        boardingList?.clear()
                        droppingList?.clear()
                        seatMap.clear()
                        updateDataCount = 0
                    }

                    else -> {
                        requireContext().toast(it.result.message!!)
                        updateBulkDataList.clear()
                        boardingList?.clear()
                        droppingList?.clear()
                        seatMap.clear()
                        updateDataCount = 0
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                updateBulkDataList.clear()
                boardingList?.clear()
                droppingList?.clear()
                seatMap.clear()
                updateDataCount = 0
            }
        }
    }

    override fun onClickOfNavMenu(position: Int) {}

    override fun onLeftButtonClick() {}

    override fun onRightButtonClick() {}

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            if (view.tag == getString(R.string.edit)) {
                updatePassengersDialog(position.toInt())          //called from new ticket details page
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
        Timber.d("onclickTEst: $data , $position")
        if (data != null) {
            val temp = data.split("&")
            if (temp[0] == getString(R.string.edit)) {
                updateSinglePassengersDialog(position, temp[1])
            }
        }
    }

    override fun onPause() {
        if (isHandlerRunning) {
            handler.removeCallbacks(runnable!!)
            isHandlerRunning = false
        }
        super.onPause()
    }

    override fun onReturnInstance(dialog: Any) {}

    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
        if (type == 0) {
            if (::countryPickerDialog.isInitialized && countryPickerDialog != null) {
                countryPickerDialog?.dismiss()
            }

            val selectedCountryCode = view as String
            if (isSingleUpdate) {
                sheetModifyDetailsBinding.etCountryCode.setText(selectedCountryCode)
            } else {
                dialogUpdatePersonalDetailsBinding?.etCountryCode?.setText(selectedCountryCode)
            }
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

}