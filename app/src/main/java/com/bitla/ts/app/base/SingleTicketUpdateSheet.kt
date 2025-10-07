package com.bitla.ts.app.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.bitla.ts.R
import com.bitla.ts.data.bulk_ticket_update_method_name
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.data.operator_api_key
import com.bitla.ts.data.service_details_method
import com.bitla.ts.data.ticket_details_method_name
import com.bitla.ts.databinding.SheetModifyDetailsBinding
import com.bitla.ts.domain.pojo.bulk_ticket_update.request.UpdateData
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details_response.StageDetail
import com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail
import com.bitla.ts.presentation.adapter.SelectBoardingDroppingArrayAdapter
import com.bitla.ts.presentation.view.ticket_details_compose.TicketDetailsActivityCompose
import com.bitla.ts.presentation.viewModel.CancelTicketViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.presentation.viewModel.TicketDetailsViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.getAllCountries
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_PRIVILEGE_DETAILS
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import gone
import org.koin.androidx.viewmodel.ext.android.viewModel
import setMaxLength
import timber.log.Timber
import toast
import visible
import java.util.ArrayList

/**
 * This class handles the functionality for updating a single ticket's details.
 * It contains all the necessary methods and properties required for the operation.
 */
class SingleTicketUpdateSheet(private val fragment: Fragment) :BaseFragment(), DialogAnyClickListener {

    // ViewModels
    private val ticketDetailsViewModel by fragment.viewModel<TicketDetailsViewModel<Any?>>()
    private val cancelTicketViewModel by fragment.viewModel<CancelTicketViewModel<Any?>>()
    private val sharedViewModel by fragment.viewModel<SharedViewModel<Any?>>()

    // Data properties
    private var singleSelectedSeatNumber: String = ""
    private var passengerList: MutableList<PassengerDetail?> = mutableListOf()
    private var updateBulkDataList: ArrayList<UpdateData> = ArrayList()
    private var boardingList: MutableList<StageDetail>? = null
    private var droppingList: MutableList<StageDetail>? = null
    private var stageDetails = mutableListOf<StageDetail>()
    private var seatMap = mutableMapOf<Int, String>()
    private var countryCode = ArrayList<Int>()
    private var locale = PreferenceUtils.getlang()

    // UI properties
    private lateinit var _sheetModifyDetailsBinding: SheetModifyDetailsBinding
    private val sheetModifyDetailsBinding get() = _sheetModifyDetailsBinding
    private lateinit var countryPickerDialog: BottomSheetDialog

    // Flags and configuration
    private var isSingleUpdate: Boolean = false
    private var showIsdCountryCode: Boolean = false
    private var isAllowProvisiontoSelectMultipleBoardingDropOff: Boolean? = null
    private var isApplyBpDpFare: Boolean? = null
    private var isETicket: Boolean = false
    private var isSingleUpdateCalled = false
    private var isHandlerRunning = false
    private var updateDataCount = 0
    private var isCheckSMSValue = 0

    // Ticket data
    private var ticketNumber: String = ""
    private var busType: String = ""
    private var depTime: String? = null
    private var isBimaTicket: Boolean = false
    private var isOnbehalfOnlineTicket: Boolean = false
    private var isApiTicket: Boolean = false

    // Passenger details
    private var passName: String? = ""
    private var passCountryCode: String? = ""
    private var passAge: String? = ""
    private var passPhone: String? = ""
    private var passEmail: String? = ""
    private var passGender: String? = ""
    private var passBoardingAt: String? = ""
    private var passDroppingAt: String? = ""
    private var seatNumber: String = ""
    private var passFare: String = ""

    // Form inputs
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

    // Navigation properties
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var reservationId: String? = ""

    // Other
    private var privilegePhoneValidationCount: Int? = null
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 1500
    private var apiKey: String = ""

    /**
     * Main function to show the single ticket update sheet
     */
    fun showSingleTicketUpdateSheet(pnrNumber: Any, seatNo: String) {
        Timber.d("updateTicketFlow:3: ")

        singleSelectedSeatNumber = seatNo
        passengerList.clear()
        getPref()
        setTicketDetailObserver()
        executeTicketDetailsApiCall(pnrNumber)
        setSingleUpdateTicketObserver()
        isSingleUpdateCalled = true
    }

    /**
     * Get preferences and configurations
     */
    private fun getPref() {
        val loginModelPref: LoginModel = PreferenceUtils.getLogin()
        apiKey = loginModelPref.api_key

        if (PreferenceUtils.getObject<PrivilegeResponseModel>(PREF_PRIVILEGE_DETAILS) != null) {
            val privilegeResponseModel: PrivilegeResponseModel? =
                PreferenceUtils.getObject<PrivilegeResponseModel>(PREF_PRIVILEGE_DETAILS)

            privilegeResponseModel?.let {
                showIsdCountryCode = privilegeResponseModel.showCountriesIsdCodesSelection ?: false
                isAllowProvisiontoSelectMultipleBoardingDropOff =
                    privilegeResponseModel.allowProvisionToSelectMultipleBoardingAndDropoffPoint
                isApplyBpDpFare = privilegeResponseModel.availableAppModes?.allowBpDpFare
            }
        }

        countryCode = getCountryCodes()
    }

    /**
     * Get country codes
     */
    private fun getCountryCodes(): ArrayList<Int> {
        // This is a simplified version, actual implementation would be more complex
        return arrayListOf(91, 62, 60, 65)
    }

    /**
     * Set up the ticket details observer
     */
    private fun setTicketDetailObserver() {
        ticketDetailsViewModel.dataTicketDetails.observe(fragment as LifecycleOwner) { it ->
            if (it != null) {
                if (it.code == 200 && it.success) {
                    source = it.body.origin
                    destination = it.body.destination
                    isETicket = it.body.isEticket
                    sourceId = it.body.originId.toString()
                    destinationId = it.body.destinationId.toString()
                    reservationId = it.body.reservationId.toString()
                    boardingAt = it.body.boardingDetails?.address
                    droffAt = it.body.dropOffDetails?.address
                    busType = it.body.busType.toString()
                    depTime = it.body.depTime
                    ticketNumber = it.body.ticketNumber.toString()
                    isBimaTicket = it.body.isBimaTicket ?: false

                    executeServiceApiCall()
                }
                else if (it.code == 401) {
                    (fragment.activity as BaseActivity).showUnauthorisedDialog()
                }
                else {
                    if (it?.result?.message != null) {
                        it.result?.message?.let { it -> fragment.requireActivity().toast(it) }
                    }
                }
            } else {
                fragment.requireContext().toast(fragment.getString(R.string.server_error))
            }
        }
    }

    /**
     * Execute the service API call
     */
    private fun executeServiceApiCall() {
        val loginModelPref: LoginModel = PreferenceUtils.getLogin()

        val apiHelper = BaseCancelUpdateApis.ServiceApiHelper(
            activity = fragment.requireActivity(),
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

    /**
     * Set up the service details API observer
     */
    private fun setServiceDetailsApiObserver() {
        sharedViewModel.serviceDetails.observe(fragment as LifecycleOwner) {
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

                    if (isSingleUpdateCalled && it.body.coachDetails?.seatDetails?.size != 0) {
                        for (i in 0 until (it.body.coachDetails?.seatDetails?.size ?: 0)) {
                            val passengerDetailRemarks = it.body.coachDetails?.seatDetails?.get(i)?.passengerDetails

                            if (!passengerDetailRemarks?.remarks.isNullOrEmpty()
                                && passengerDetailRemarks?.name == sheetModifyDetailsBinding.etName.text.toString().trim()
                            ) {
                                sheetModifyDetailsBinding.etRemarkSingleUpdate.setText("${passengerDetailRemarks.remarks}")
                                    .toString()
                            }
                        }
                    }
                }
                401 -> {
                    (fragment.activity as BaseActivity).showUnauthorisedDialog()
                }
                else -> it.message?.let { it1 -> fragment.requireContext().toast(it1) }
            }
        }
    }

    /**
     * Generate boarding list
     */
    private fun generateBoardingList(i: Int) {
        if (boardingList != null) {
            boardingList?.add(stageDetails[i])
            Timber.d("boardingList ${boardingList?.size}")
        }
    }

    /**
     * Generate dropping list
     */
    private fun generateDroppingList(i: Int) {
        if (droppingList != null) {
            droppingList?.add(stageDetails[i])
            Timber.d("droppingList ${droppingList?.size}")
        }
    }

    /**
     * Execute ticket details API call
     */
    private fun executeTicketDetailsApiCall(pnrNumber: Any) {
        val loginModelPref: LoginModel = PreferenceUtils.getLogin()

        val apiHelper = BaseCancelUpdateApis.TicketDetailsApiHelper(
            activity = fragment.requireActivity(),
            ticketDetailsViewModel = ticketDetailsViewModel,
            loginModelPref = loginModelPref,
            locale = locale,
            ticketDetailsMethodName = ticket_details_method_name
        )

        apiHelper.callTicketDetailsApi(pnrNumber)
    }

    /**
     * Set up the single update ticket observer
     */
    @SuppressLint("SetTextI18n")
    private fun setSingleUpdateTicketObserver() {
        val bottomSheetDialog = BottomSheetDialog(fragment.requireActivity(), R.style.BottomSheetDialog)
        _sheetModifyDetailsBinding = SheetModifyDetailsBinding.inflate(fragment.layoutInflater)
        bottomSheetDialog.setContentView(sheetModifyDetailsBinding.root)
        sheetModifyDetailsBinding.includeProgress.progressBar.visible()

        val list = getAllCountries(fragment.requireContext())

        sheetModifyDetailsBinding.etCountryCode.setOnClickListener {
            if (isETicket && showIsdCountryCode) {
                isSingleUpdate = true
                countryPickerDialog = BaseCancelUpdateApis.Companion.showCountryPickerBottomsheet(
                    context = fragment.requireContext(),
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

        ticketDetailsViewModel.dataTicketDetails.observe(fragment as LifecycleOwner) { it ->
            sheetModifyDetailsBinding.includeProgress.progressBar.gone()

            if (it != null) {
                ticketDetailsViewModel.loadingState.observe(fragment) {
                    Timber.d("LoadingState-ticketDetailsViewModel ${it.status}")
                    when (it) {
                        LoadingState.LOADING -> sheetModifyDetailsBinding.includeProgress.progressBar.visible()
                        LoadingState.LOADED -> sheetModifyDetailsBinding.includeProgress.progressBar.gone()
                        else -> {
                            it.msg?.let { it1 -> fragment.context?.toast(it1) }
                            sheetModifyDetailsBinding.includeProgress.progressBar.gone()
                        }
                    }
                }

                if (it.code == 200 && it.success) {
                    Timber.d("genderArray2 ${Gson().toJson(fragment.resources.getStringArray(R.array.genderArray2))}")

                    sheetModifyDetailsBinding.includeProgress.progressBar.gone()

                    busType = it.body.busType.toString()
                    depTime = it.body.depTime
                    ticketNumber = it.body.ticketNumber.toString()
                    isBimaTicket = it.body.isBimaTicket ?: false
                    boardingAt = it.body.boardingDetails?.address
                    droffAt = it.body.dropOffDetails?.address
                    isOnbehalfOnlineTicket = it.body.isOnbehalfOnlineTicket
                    isApiTicket = it.body.isApiTicket

                    if (it?.body?.passengerDetails != null && it?.body?.passengerDetails?.isNotEmpty() == true) {
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

                        Timber.d("passBoardingAt = $passBoardingAt -- $passDroppingAt")
                    }

                    sheetModifyDetailsBinding.apply {
                        privilegePhoneValidationCount = PreferenceUtils.getPrivilege()?.phoneNumValidationCount ?: 13
                        etName.setText(passName).toString()
                        etAge.setText(passAge).toString()

                        if(!showIsdCountryCode) {
                            if (privilegePhoneValidationCount == 0) {
                                etPhoneNumber.setMaxLength(14)
                            } else {
                                etPhoneNumber.setMaxLength(privilegePhoneValidationCount ?: 14)
                            }
                        }

                        etPhoneNumber.setText(passPhone?.substringAfter("-"))
                        if(passCountryCode?.contains("+") == true){
                            etCountryCode.setText(passCountryCode)
                        }else{
                            etCountryCode.setText("+"+passCountryCode)
                        }
                        etEmail.setText(passEmail).toString()
                        autoCompleteGender.setText(passGender).toString()

                        autoCompleteGender.setAdapter(
                            ArrayAdapter(
                                fragment.requireActivity(),
                                R.layout.spinner_dropdown_item,
                                R.id.tvItem,
                                fragment.requireActivity().resources.getStringArray(R.array.genderArray2)
                            )
                        )

                        etBoardingAt.setText("$boardingAt ${it.body.boardingDetails?.depTime}")
                            .toString()
                        etDropOffAt.setText("$droffAt ${it.body.dropOffDetails?.arrTime}")
                            .toString()

                        etFare.setText(passFare)
                    }

                    executeServiceApiCall()

                    sharedViewModel.serviceDetails.observe(fragment) {
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
                                    fragment.requireContext(),
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
                                    fragment.requireContext(),
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
                        } else if (it.code == 401) {
                            (fragment.activity as BaseActivity).showUnauthorisedDialog()
                        } else {
                            if (it?.message != null) {
                                it?.message?.let { it -> fragment.requireActivity().toast(it) }
                            }
                        }
                    }
                }
            } else {
                fragment.requireContext().toast(fragment.getString(R.string.server_error))
            }
        }

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
                        fragment.context,
                        fragment.getString(R.string.give_valid_mobile_number),
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

        cancelTicketViewModel.loadingState.observe(fragment) {
            Timber.d("LoadingState ${it.status}")
            when (it) {
                LoadingState.LOADING -> sheetModifyDetailsBinding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> sheetModifyDetailsBinding.includeProgress.progressBar.gone()
                else -> {
                    it.msg?.let { it1 -> fragment.requireContext().toast(it1) }
                    sheetModifyDetailsBinding.includeProgress.progressBar.gone()
                }
            }
        }

        cancelTicketViewModel.bulkTicketUpdateResponse.observe(fragment) {
            sheetModifyDetailsBinding.includeProgress.progressBar.gone()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        DialogUtils.successfulMsgDialog(
                            fragment.requireContext(),
                            it.message
                        )

                        handler.postDelayed(Runnable {
                            isHandlerRunning = true
                            handler.postDelayed(runnable!!, delay.toLong())

                            if (fragment.isAdded && fragment.requireActivity() != null) {

                                val intent = Intent(fragment.requireContext(), TicketDetailsActivityCompose::class.java)
                            intent.putExtra("returnToDashboard", false)
                            intent.putExtra(
                                fragment.getString(R.string.put_extra_bulkTicketUpdate),
                                fragment.getString(R.string.put_extra_bulkTicketUpdate)
                            )
                            intent.putExtra(fragment.getString(R.string.TICKET_NUMBER), ticketNumber)
                            }

                        }.also { runnable = it }, delay.toLong())

                        bottomSheetDialog.dismiss()
                        updateBulkDataList.clear()
                        boardingList?.clear()
                        droppingList?.clear()
                        seatMap.clear()
                        updateDataCount = 0
                    }

                    422 -> {
                        it.result.message?.let { it1 -> fragment.requireContext().toast(it1) }
                        updateBulkDataList.clear()
                    }

                    401 -> {
                        (fragment.activity as BaseActivity).showUnauthorisedDialog()
                    }

                    else -> {
                        it.result.message?.let { it1 -> fragment.requireContext().toast(it1) }
                        updateBulkDataList.clear()
                    }
                }
            } else {
                fragment.requireContext().toast(fragment.getString(R.string.server_error))
            }
        }

        bottomSheetDialog.show()
    }

    /**
     * Process and submit the bulk update API
     */
    private fun hitBulkUpdateApi() {
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
                    countryCode = finalCountryCode
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

    /**
     * Execute the bulk ticket update API call
     */
    private fun executeBulkTicketUpdateApiCall() {
        val apiHelper = BaseCancelUpdateApis.BulkTicketUpdateApiHelper(
            activity = fragment.requireActivity(),
            cancelTicketViewModel = cancelTicketViewModel,
            apiKey = apiKey,
            emailId = emailId,
            ticketNumber = ticketNumber,
            jsonFormat = "json",
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

    /**
     * Handle the click event from country picker
     */
    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
        if (type == 0) {
            if (::countryPickerDialog.isInitialized && countryPickerDialog != null) {
                countryPickerDialog.dismiss()
            }

            val selectedCountryCode = view as String
            if (isSingleUpdate) {
                sheetModifyDetailsBinding.etCountryCode.setText(selectedCountryCode)
            }
        }
    }

    /**
     * Required for DialogAnyClickListener interface
     */
    override fun onAnyClickListenerWithExtraParam(
        type: Int,
        view: Any,
        list: Any,
        position: Int,
        outPos: Int
    ) {
        // Not used for this functionality
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }
}