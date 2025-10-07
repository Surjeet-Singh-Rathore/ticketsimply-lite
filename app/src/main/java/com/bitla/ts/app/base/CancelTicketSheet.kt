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
import androidx.lifecycle.Observer
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
import com.bitla.ts.domain.pojo.cancel_partial_ticket_model.response.CancelPartialTicketResponse
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

/**
 * Created by Taiyab Ali on 20-Aug-21.
 * */

@Suppress("UNCHECKED_CAST")
open class CancelTicketSheet : BaseFragment(), DialogButtonListener, OnItemClickListener,
    OnItemCheckListener, DialogButtonTagListener,
    BaseActivity.UpdateBulkTicketInterface, BaseActivity.UpdateSingleTicketInterface,
    BaseActivity.CancelTicketTicketInterface,
    BaseActivity.ViewPassengersSheetInterface, DialogReturnDialogInstanceListener,
    DialogAnyClickListener {

    private var remarks: String = ""
    private lateinit var cancelDialogBinding: DialogCancelTicketBinding
    private var showIsdCountryCode: Boolean = false
    private lateinit var _sheetTicketCancellationBinding: SheetTicketCancellationBinding
    private val sheetTicketCancellationBinding get() = _sheetTicketCancellationBinding
    private var isAllowToShowRemarksForCancelTicket = false

    private lateinit var _sheetViewPassengerDetailsBinding: SheetPassengerDetailsBinding
    private val sheetViewPassengerDetailsBinding get() = _sheetViewPassengerDetailsBinding

    private lateinit var _sheetUpdatePassengersDetailsBinding: SheetUpdatePassengersDetailsBinding
    private val sheetUpdatePassengersDetailsBinding get() = _sheetUpdatePassengersDetailsBinding

    private var dialogUpdatePersonalDetailsBinding: DialogUpdatePersonalDetailsBinding? = null

    private lateinit var _sheetModifyDetailsBinding: SheetModifyDetailsBinding
    private val sheetModifyDetailsBinding get() = _sheetModifyDetailsBinding

    private var getPassengerDetailResponse: ArrayList<PassengerDetail> = ArrayList()
    private lateinit var spinnerItems: SpinnerItems
    private var userListResponse: ArrayList<SpinnerItems> = arrayListOf()
    private lateinit var selectedAcOnBehalfOf: String
    private var passengerDetailList: MutableList<PassengerDetail?>? = null

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
    private var passAge: String? = ""
    private var passEmail: String? = ""
    private var passGlobalEmail: String? = ""
    private var passPhone: String? = ""
    private var passGender: String? = ""
    private var passBoardingAt: String? = ""
    private var passDroppingAt: String? = ""
    private var isETicket: Boolean = false

    //    private var refundType = 2
    private var sendSms = false
    private var boardingAt: String? = ""
    private var droffAt: String? = ""

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
    private var isSingleSeatClick: Boolean = false
    private var countryCode = ArrayList<Int>()
    private var locale = PreferenceUtils.getlang()
    private var amountCurrency: String = ""
    private var currencyFormatt: String = ""

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

    private var isUpdateFareForPhoneBlockedOrConfirmedTickets: Boolean = false

    override fun showTicketCancellationSheet(pnrNumber: Any) {
        getPref()
        setTicketDetailObserver()
        executeUserListApi()
        executeTicketDetailsApiCall(pnrNumber)
        setCancellationTicketDetailObserver()
        isCancelDetailsCalled = true
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
            remarks = sheetTicketCancellationBinding.etRemarks.text.toString()

            if (remarks == "" && isAllowToShowRemarksForCancelTicket) {
                requireContext().toast("Please enter the remarks")
                return@setOnClickListener
            }

            if (isButtonClickable) {

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


                    if (country.equals("India",true)) {
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

    private fun cancelTypeGone() {
        sheetTicketCancellationBinding.apply {
            rgCancellationType.gone()
            cancellationTypeLabel.gone()
            layoutCancellationPercentage.gone()
            noticeText.gone()
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
            pinAuthDialogBox()
            binding.btnLight.isEnabled = false
        }

        setCancelPartialTicketObserver(bottomSheetDialog)
        setConfirmOtpCancelPartialTicketObserver()
       // setCancelPartialOtpTicketObserver(bottomSheetDialog)
        builder.setView(binding.root)
        builder.show()
    }

    private fun executeCancelPartialTicketApiCall(authPin: String) {
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
            authPin = authPin,
            remarks = remarks

        )

        apiHelper.callCancelPartialTicketApi()
    }

    private fun setCancelPartialTicketObserver(bottomSheetDialog: BottomSheetDialog) {
        dialogPartialTicketOpenCount++

        // Use observeOnce extension function or implement one-time observer pattern
        cancelTicketViewModel.cancelPartialTicketViewModel.observe(this, object :
            Observer<CancelPartialTicketResponse> {
            override fun onChanged(value: CancelPartialTicketResponse) {
                // Remove the observer after first execution
                cancelTicketViewModel.cancelPartialTicketViewModel.removeObserver(this)

                sheetTicketCancellationBinding.includeProgress.progressBar.gone()

                if (value != null) {
                    when (value.code) {
                        200 -> {
                            bottomSheetDialog.dismiss()

                            DialogUtils.successfulMsgDialog(
                                requireContext(),
                                getString(R.string.successfully_cancelled_ticket)
                            )

                            if (loginModelPref.role == getString(R.string.role_field_officer)) {
                                    isHandlerRunning = true
                                    runnable?.let { handler.postDelayed(it, DELAY_MILLIS_24) }
                                    val intent = Intent(requireContext(), DashboardNavigateActivity::class.java)
                                    intent.putExtra("newBooking", true)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    if (activity != null) {
                                        startActivity(intent)
                                        requireActivity().finish()
                                    }

                            } else {
                                    isHandlerRunning = true
                                Handler(Looper.getMainLooper()).postDelayed({

                                    val intent = Intent(
                                        requireContext(),
                                        TicketDetailsActivityCompose::class.java
                                    )
                                    intent.putExtra(
                                        getString(R.string.put_extra_cancelTicket),
                                        getString(R.string.put_extra_cancelTicket)
                                    )
                                    intent.putExtra(getString(R.string.TICKET_NUMBER), ticketNumber)
                                    if (activity != null) {
                                        requireContext().startActivity(intent)
                                    }
                                },2000)

                            }
                            currentCheckedItem.clear()
                            selectedSeatNumber.clear()
                            selectedCancellationType = ""
                        }


                        422 -> {
                            cancelDialogBinding.btnLight.isEnabled = true
                            value.result?.message?.let { requireContext().toast(it) }
                        }

                        401 -> {
                            cancelDialogBinding.btnLight.isEnabled = true
                            (activity as BaseActivity).showUnauthorisedDialog()
                        }

                        else -> {
                            if (value.result?.key?.isNotEmpty() == true) {
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
                                                    executeCancelPartialTicketApiCall("")
                                                } else {
                                                    executeConfirmOtpCancelPartialTicketApiCall(selectedSeatNumber.toString())
                                                }
                                            }

                                        },
                                        this@CancelTicketSheet,
                                        dimissAction = {})
                                    cancelOptkey = value.result?.key.toString()
                                    requireActivity().toast(value.result?.message.toString())
                                    cancelOtpLayoutDialogOpenCount++
                                }
                                cancelOptkey = value.result?.key.toString()

                            }
                            cancelDialogBinding.btnLight.isEnabled = true
                            when {
                                value.result?.message != null -> value.result.message.let { requireContext().toast(it) }
                                value.result == null -> value.message?.let { requireContext().toast(it) }
                                else -> requireContext().toast(getString(R.string.opps))
                            }
                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            }
        })
    }

    private fun pinAuthDialogBox() {
        if (shouldTicketCancellation && country.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = requireActivity(),
                fragmentManager = childFragmentManager,
                pinSize = pinSize,
                getString(R.string.bulk_cancel),
                onPinSubmitted = { pin: String ->
                    executeCancelPartialTicketApiCall(pin)
                },
                onDismiss = {
                }
            )
        } else {
            executeCancelPartialTicketApiCall("")
        }
    }

    /*private fun setCancelPartialTicketObserver(bottomSheetDialog: BottomSheetDialog) {
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
    }*/

    private fun setConfirmOtpCancelPartialTicketObserver() {

        cancelTicketViewModel.confirmOtpCancelPartialTicketResponse.observe(this) {
            sheetTicketCancellationBinding.includeProgress.progressBar.gone()
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

                            lifecycleScope.launch {
                                delay(1500)
                                val intent = Intent(
                                    requireContext(),
                                    TicketDetailsActivityCompose::class.java
                                )
                                intent.putExtra(
                                    getString(R.string.put_extra_cancelTicket),
                                    getString(R.string.put_extra_cancelTicket)
                                )
                                intent.putExtra(getString(R.string.TICKET_NUMBER), ticketNumber)
                                this@CancelTicketSheet.startActivity(intent)
                            }
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
                                    Timber.d("buttonClickCheck: 1")
                                    Timber.d("buttonCheck:11 Clicked $str ")

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
                                        executeCancelPartialTicketApiCall("")
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


    //unused override functions
    override fun onLeftButtonClick(tag: View?) {
    }
    override fun onRightButtonClick(tag: View?) {
    }
    override fun onButtonClick(view: Any, dialog: Dialog) {
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

    override fun showEditPassengersSheet(pnrNumber: Any) {
    }
    override fun showSingleTicketUpdateSheet(pnrNumber: Any, seatNo: String) {
    }
    override fun showViewPassengersSheet(pnrNumber: Any) {
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