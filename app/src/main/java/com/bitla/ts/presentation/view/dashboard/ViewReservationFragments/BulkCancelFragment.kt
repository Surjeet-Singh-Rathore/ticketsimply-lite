package com.bitla.ts.presentation.view.dashboard.ViewReservationFragments

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.bulkCancelOtpConfirmtion.request.*
import com.bitla.ts.domain.pojo.bulk_cancellation.request.*
import com.bitla.ts.domain.pojo.cancellation_details_model.request.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.view_reservation.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.constants.CancelFinalCtaClicks.CANCEL_FINAL_CTA_CLICKS_BULK_CANCEL
import com.bitla.ts.utils.constants.SelectAllClicks.SELECT_ALL_CLICK_BULK_CANCEL
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.google.android.material.bottomsheet.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible

class BulkCancelFragment : Fragment(), DialogButtonListener,
    OnItemClickListener, DialogSingleButtonListener, DialogReturnDialogInstanceListener {
    private var wrongPercentage = false
    private var canAlterCancellationPercentage = true
    private lateinit var binding: FragmentBulkCancelBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var bulkCancelAdapter: BulkCancelAdapter

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var serviceData: String? = null
    private var serviceName: String? = null
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var resID: String = ""
    private var chartTypeList: ArrayList<ChartType> = arrayListOf()
    private var chartType: String = "2"
    private var passengerlist: ArrayList<PassengerDetail> = arrayListOf()
    private var passengerlistSelected: ArrayList<PassengerDetail> = arrayListOf()
    private var allSelected = 0
    private var percentagetext: String = "0"
    private var remarksText: String? = null
    private var sendSms: Boolean = false
    private var zeroPercentage: Boolean = false
    private val cancelTicketViewModel by viewModel<CancelTicketViewModel<Any?>>()
    private var seatNumberList = arrayListOf<String>()
//    private var cancellationMessage = ""

    private var currency: String = ""
    private val bulkParams: ArrayList<BulkCancelParam> = arrayListOf()
    val seatList = arrayListOf<String>()
    private val pnr = arrayListOf<String>()

    private var currencyFormat: String = ""
//    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var locale: String? = ""
    private var zeroCancelCheck: Boolean =false
    private var coutomPercentage: Boolean =false
    private lateinit var otpVerifiction: AlertDialog

    private var pinSize = 0
    private var shouldTicketCancel = false
    private var currentCountry: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBulkCancelBinding.inflate(inflater, container, false)
        getPref()
        pickUpChartApi(chartType)

        startShimmerEffect()
        initRefreshListner()
        viewReservationObserver()
        cancellationDetailResponse()
        bulkCancelOtpVerificationObserver()
        bulkCancellationObserver()

        binding.selectAll.setOnClickListener {

            when (allSelected) {
                //toClearAll
                0 -> {
                    allSelected = 1
                    binding.selectAll.text = getString(R.string.clear_all)
                    passengerlistSelected.clear()
                    passengerlistSelected.addAll(passengerlist)

                    binding.btnProceedCancel.visible()
                    binding.btnProceedCancel.text =
                        "${getString(R.string.proceed_to_cancel)} ${passengerlistSelected.size} ${
                            getString(
                                R.string.tickets
                            )
                        }"
                }

                //selectAll
                1 -> {
                    allSelected = 0
                    binding.selectAll.text = getString(R.string.select_all)
                    passengerlistSelected.clear()
                    binding.btnProceedCancel.gone()
//                        Timber.d("seatselected al;S", "${passengerlistSelected.size}")
                }
            }
            setBulkCancelAdapter(passengerlist, allSelected)

            firebaseLogEvent(
                context = requireContext(),
                logEventName = SELECT_ALL_CLICKS,
                loginId = loginModelPref.userName,
                operatorName = loginModelPref.travels_name,
                roleName = loginModelPref.role,
                eventKey = SELECT_ALL_CLICKS,
                eventValue = SELECT_ALL_CLICK_BULK_CANCEL
            )
        }

        binding.btnProceedCancel.setOnClickListener {
            sendSms = false
            zeroPercentage = false
            val bottomSheetDialoge = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
            val binding = SheetCancelTicketsBinding.inflate(layoutInflater)
            bottomSheetDialoge.setContentView(binding.root)
            bottomSheetDialoge.show()
            pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponseModel ->

                if(canAlterCancellationPercentage) {

                    binding.layoutCancellationPercentage.visible()
                    binding.textView7.visible()

                    if (privilegeResponseModel?.bulkCancellationConfig == "zero_percent") {
                        binding.etCancellationPercentage.setText("0")
                        binding.checkboxZeroCancellation.gone()
                    }

                    if (privilegeResponseModel?.bulkCancellationConfig == "applicable_percent") {
                        binding.checkboxZeroCancellation.gone()
                    }

                }
                else {
                    binding.layoutCancellationPercentage.gone()
                    binding.textView7.gone()

                    if (privilegeResponseModel?.bulkCancellationConfig == "applicable_percent") {
                        binding.checkboxZeroCancellation.gone()
                    }

                    if (privilegeResponseModel?.bulkCancellationConfig == "zero_percent") {
                        binding.checkboxZeroCancellation.visible()
                        binding.checkboxZeroCancellation.isChecked = true
                        binding.etCancellationPercentage.setText("0")
                    }
                }

            }

            binding.etCancellationPercentage.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    if(s.isNotEmpty()){
                        if (s.toString().toInt() > 100) {
                            wrongPercentage = true
                            binding.etCancellationPercentage.error = "Percentage should not be greater than 100"
                        }else{
                            wrongPercentage = false
                        }
                    }else{
                        wrongPercentage = false
                    }

                }
            })

            binding.checkBoxSms.setOnCheckedChangeListener { buttonView, isChecked ->
                sendSms = isChecked
            }

            binding.checkboxZeroCancellation.setOnCheckedChangeListener { buttonView, isChecked ->
                zeroPercentage = isChecked
            }

            binding.tvtotalSelectedSeats.text = passengerlistSelected.size.toString()

            binding.btnCancelTickets.setOnClickListener {
//                requireActivity().toast("test")
                if(!wrongPercentage){
                    pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponseModel ->

                        var pnr = ""
                        var seatnumber = ""
                        seatNumberList.clear()

                        if (binding.etCancellationPercentage.text.isNullOrEmpty()){
                            coutomPercentage= false

                            if (privilegeResponseModel?.bulkCancellationConfig == "zero_percent") {
                                percentagetext = "0"
                            }
                        } else{
                            coutomPercentage= true
                            percentagetext = binding.etCancellationPercentage.text.toString()

                        }

                        remarksText = binding.etRemarks.text.toString()
                        for (i in 0..passengerlistSelected.size.minus(1)) {
                            seatNumberList.add(passengerlistSelected[i].seatNumber)
                            pnr = passengerlistSelected[i].pnrNumber
                            seatnumber = passengerlistSelected[i].seatNumber
                        }

                        if (remarksText.isNullOrEmpty()) {
                            requireContext().toast(getString(R.string.please_fill_all_the_required_details))

                        }else{
                            if (binding.checkboxZeroCancellation.isChecked){
                                zeroCancelCheck= true
                            }
                            callZeroCancellationDetailsApi(pnr, seatnumber)
                            bottomSheetDialoge.dismiss()
                        }
                    }
                } else{
                    requireActivity().toast(getString(R.string.please_enter_valid_cancellation_percentage))
                }

            }
            binding.imgCrossPopup.setOnClickListener {
                bottomSheetDialoge.dismiss()
            }

            firebaseLogEvent(
                context = requireContext(),
                logEventName = CANCEL_FINAL_CTA_CLICKS,
                loginId = loginModelPref.userName,
                operatorName = loginModelPref.travels_name,
                roleName = loginModelPref.role,
                eventKey = CANCEL_FINAL_CTA_CLICKS,
                eventValue = CANCEL_FINAL_CTA_CLICKS_BULK_CANCEL
            )
        }

        lifecycleScope.launch {
            cancelTicketViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }

        return binding.root
    }

    private fun callZeroCancellationDetailsApi(pnrNumber: Any, seatnumber: String) {

        if (requireActivity().isNetworkAvailable()) {

            val reqBody = ReqBody2(
                apiKey = loginModelPref.api_key,
                cancelType = "",
                isFromBusOptApp = true,
                locale = locale,
                operatorApiKey = operator_api_key,
                passengerDetails = "",
                pnrNumber = pnrNumber.toString(),
                responseFormat = json_format,
                seatNumbers = seatnumber,
                zeroPercent = zeroPercentage,
                json_format = json_format
            )

            cancelTicketViewModel.getZeroCancellationDetailsApi(
                reqBody,
                cancellation_details_ticket_method_name
            )

        } else
            requireActivity().noNetworkToast()
    }

    private fun cancellationDetailResponse() {
        cancelTicketViewModel.cancellationDetailsResponse.observe(requireActivity()) {
            if (it != null) {
                when (it.code) {
                    200 -> {

                        if (zeroCancelCheck){
                            percentagetext= "0"
                            zeroCancelCheck= false
                        }else{
                            if (coutomPercentage == false) {
                                percentagetext = it.result.cancelPercent.toString()
                                coutomPercentage = false
                            }
                        }

                        var message = ""

                        if (sendSms) {
                            message =
                                getString(R.string.selected_seat_s_will_be_cancelled_and_customer_will_be_notified_via_sms)
                        } else {
                            message = ""
                        }
                        val seat = seatNumberList.toString().replace("[", "").replace("]", "")
                        DialogUtils.cancelTicketsDialog(
                            context = requireActivity(),
                            title = "${getString(R.string.canceling)} ${passengerlistSelected.size} ${
                                getString(
                                    R.string.tickets
                                )
                            } ",
                            message = message,
                            srcDest = serviceName.toString(),
                            journeyDate = serviceData.toString(),
                            ticketCancellationPercentage = "${percentagetext}%",
                            seatNo = seat,
                            buttonLeftText = getString(R.string.goBack),
                            buttonRightText = getString(R.string.proceed),
                            dialogButtonListener = this,
                        )
                        //                    openCancelConfirmDialog(bottomSheetDialog,cancellationAmount,refundAmount)

                        Timber.d("messageResult-${it.result}")
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    else -> {
                        requireActivity().toast(it.message)
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun pickUpChartApi(chartTypeSelected: String) {
        if (requireContext().isNetworkAvailable()) {
            pickUpChartViewModel.viewReservationAPI(
                apiKey = loginModelPref.api_key,
                resId = resID.toString(),
                chartType = chartType,
                locale = locale ?: "en",
                apiType = view_reservation_method_name,
                newPickUpChart = null
            )
        } else requireContext().noNetworkToast()
    }

    private fun viewReservationObserver() {

        pickUpChartViewModel.viewReservationResponse.observe(viewLifecycleOwner) {
            chartTypeList.clear()
            if (it != null) {
                binding.refreshLayout.isRefreshing = false
                try {
                    if (it.passengerDetails != null) {
                        binding.rvreservationBulkCancel.visible()
                        binding.selectAll.visible()
                        passengerlist = it.passengerDetails
                        setBulkCancelAdapter(passengerlist, allSelected)
                    } else {
                        stopShimmerEffect()
                        binding.rvreservationBulkCancel.gone()
                        binding.selectAll.gone()
                        binding.NoResult.visible()
                        PreferenceUtils.setPreference("dataAvailable", false)
                        binding.allservice.gone()
                    }
                    //if(it.canAlterCancellationPercentage)
                        canAlterCancellationPercentage = it.canAlterCancellationPercentage ?: true
                } catch (t: Throwable) {
                    Timber.d("An error occurred: ${t.message}")
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun setBulkCancelAdapter(passengerDetail: List<PassengerDetail>, allSelected: Int) {
        stopShimmerEffect()

        pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponseModel ->

            try {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                binding.rvreservationBulkCancel.layoutManager = layoutManager
                bulkCancelAdapter =
                    BulkCancelAdapter(
                        context = requireActivity(),
                        searchList = passengerDetail,
                        onItemClickListener = this,
                        allselected = allSelected,
                        currency = currency,
                        currencyFormat = currencyFormat,
                        privilegeResponseModel = privilegeResponseModel
                    )
                binding.rvreservationBulkCancel.adapter = bulkCancelAdapter
            } catch (t: Throwable) {
                binding.NoResult.visible()
                Timber.d("An error occurred: ${t.message}")
            }
        }

    }

    private fun getPref() {

        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
//        resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
        serviceData = PreferenceUtils.getString("ViewReservation_data")
        serviceName = PreferenceUtils.getString("ViewReservation_name")
        resID = PreferenceUtils.getString("reservationid") ?: ""

        lifecycleScope.launch {
            val privilege = (activity as BaseActivity).getPrivilegeBaseSafely()
            pickUpChartViewModel.updatePrivileges(privilege)
        }

        pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
            currency = privilegeResponse?.currency ?: getString(R.string.rupeeSybbol)
            currencyFormat = privilegeResponse?.currencyFormat ?: getString(R.string.indian_currency_format)
            pinSize = privilegeResponse?.pinCount ?: 6
            shouldTicketCancel = privilegeResponse?.pinBasedActionPrivileges?.ticketCancellation ?: false
            currentCountry = privilegeResponse?.country ?: ""

        }

//        if ((activity as BaseActivity).getPrivilegeBase() != null) {
//            privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase() as PrivilegeResponseModel
//            currency = privilegeResponseModel?.currency ?: getString(R.string.rupeeSybbol)
//            currencyFormat = privilegeResponseModel?.currencyFormat ?: getString(R.string.indian_currency_format)
//            pinSize = privilegeResponseModel?.pinCount ?: 6
//            shouldTicketCancel = privilegeResponseModel?.pinBasedActionPrivileges?.ticketCancellation ?: false
//            currentCountry = privilegeResponseModel?.country ?: ""
//        }
    }

    private fun bulkCancelApi(bulkParams: List<BulkCancelParam>, authPin: String) {
        pickUpChartViewModel.bulkCancellationAPI(
            com.bitla.ts.domain.pojo.bulk_cancellation.request.ReqBody(
                api_key = loginModelPref.api_key,
                bulk_cancel_params = bulkParams,
                cancel_percent = percentagetext.toFloat(),
                is_sms_send = sendSms,
                remarks = remarksText!!,
                res_id = resID.toString(),
                locale = locale,
                auth_pin = authPin
            ),
            bulk_cancellation_method_name
        )
    }

    private fun bulkCancelOtpVerificationApi(bulkParams: List<BulkCancelParam>, key:String, otp:String) {
        pickUpChartViewModel.bulkCancelOtpVerificationApi(
            BulkCancelVerificationRequest(
                api_key = loginModelPref.api_key,
                bulk_cancel_params = bulkParams,
                cancel_percent = percentagetext.toFloat(),
                is_sms_send = sendSms,
                remarks = remarksText!!,
                otp = otp,
                res_id = resID.toString(),
                locale = locale!!,
                is_from_middle_tier = true,
                key = key
            ),
            bulk_ticket_cancellation_confirmation_method
        )
    }
    private fun bulkCancelOtpVerificationObserver() {

        pickUpChartViewModel.bulkCancelOtpVerificationResponse.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                stopShimmerEffect()
                if(it.code=="200"){
                    DialogUtils.successfulMsgDialog(
                        requireContext(), it.message
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (:: otpVerifiction.isInitialized){
                            otpVerifiction.dismiss()
                            pickUpChartApi(chartType)
                        }
                    }, 1000)
                }else{
                    it.message.let { it1 -> requireContext().toast(it1) }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }
    private fun bulkCancellationObserver() {

        pickUpChartViewModel.bulkCancellationResponse.observe(viewLifecycleOwner) { it ->
            stopShimmerEffect()
            chartTypeList.clear()
//            Timber.d("viewReservation", "${it}")
            if (it != null) {
                when (it.code) {
                    "200" -> {
                        it.message?.let { it1 ->
                            DialogUtils.successfulMsgDialog(
                                requireContext(),
                                it1
                            )
                        }
//                        startShimmerEffect()
//                        pickUpChartApi(chartType)
                       /* val intent = Intent(requireContext(), ViewReservationActivity::class.java)
                        startActivity(intent)*/
                        passengerlist.clear()
                        setBulkCancelAdapter(passengerlist,allSelected)
                        callOnSwipe()

                    }
                    "401" -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    else -> {
                        if (it.result != null){
                            if (it.result.key.isNotEmpty()){
                                setBulkCancelAdapter(passengerlist, allSelected)
                                DialogUtils.cancelOtpLayoutDialog(requireContext(),  object : DialogSingleButtonListener {
                                    override fun onSingleButtonClick(str: String) {
                                        if (str == getString(R.string.unauthorized)) {
                                            //clearAndSave(requireContext())
                                            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
                                            val intent = Intent(requireActivity(), LoginActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                            requireActivity().finish()
                                        }
                                        Timber.d("seats--- 123$str, $")

                                        if (str == "resend") {
                                            pinAuthDialogBox(bulkParams)
                                        } else {
                                            stopShimmerEffect()
                                            bulkCancelOtpVerificationApi(
                                                bulkParams = bulkParams, otp =  str, key = it.result.key)
                                        }
                                    }

                                },this, dimissAction = {
                                    otpVerifiction= it
                                })
                            }

                        }else{
                            pickUpChartApi(chartType)
                            it.message?.let { it1 -> requireContext().toast(it1) }
                        }

                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            if (view.tag == "true") {

                if (passengerlistSelected.contains(passengerlist[position])) {
                    passengerlistSelected.remove(passengerlist[position])
                    if (passengerlistSelected.isNotEmpty()) {
                        binding.btnProceedCancel.visible()
                        binding.btnProceedCancel.text =
                            "${getString(R.string.proceed_to_cancel)} ${passengerlistSelected.size} ${
                                getString(
                                    R.string.tickets
                                )
                            }"

                    } else {
                        binding.btnProceedCancel.gone()
                    }
                } else {
                    passengerlistSelected.add(passengerlist[position])
                    binding.btnProceedCancel.visible()
                    binding.btnProceedCancel.text =
                        "${getString(R.string.proceed_to_cancel)} ${passengerlistSelected.size} ${
                            getString(
                                R.string.tickets
                            )
                        }"
                }
            }
            if (view.tag == "false") {
                if (passengerlistSelected.size == passengerlist.size) {
                    passengerlistSelected.removeAt(position)

                } else {
                    for (i in 0..passengerlistSelected.size.minus(1)) {
                        if (passengerlistSelected[i].seatNumber.contains(passengerlist[position].seatNumber)) {
                            passengerlistSelected.remove(passengerlistSelected[i])
                            break
                        }
                    }
                }
                if (passengerlistSelected.size == 0) {
                    binding.btnProceedCancel.gone()
                } else if (passengerlistSelected.size > 0) {
                    binding.btnProceedCancel.visible()
                    binding.btnProceedCancel.text =
                        "${getString(R.string.proceed_to_cancel)} ${passengerlistSelected.size} ${
                            getString(
                                R.string.tickets
                            )
                        }"
                }
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onClickOfItem(data: String, position: Int) {
        firebaseLogEvent(
            requireContext(),
            VIEW_OPTION,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            VIEW_OPTION,
            ViewOptions.VIEW
        )
//        val intent = if (privilegeResponseModel?.country.equals("India", true) || privilegeResponseModel?.country.equals("Indonesia", true)) {
//            Intent(requireContext(), TicketDetailsActivityCompose::class.java)
//        } else {
//            Intent(requireContext(), TicketDetailsActivity::class.java)
//        }

        val intent= Intent(requireContext(), TicketDetailsActivityCompose::class.java)

        intent.putExtra("returnToDashboard", false)
        intent.putExtra(getString(R.string.TICKET_NUMBER), data)
        startActivity(intent)
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onLeftButtonClick() {
    }

    override fun onRightButtonClick() {
        binding.NoResult.gone()
        startShimmerEffect()
        binding.allservice.visible()
        binding.rvreservationBulkCancel.gone()
        pnr.clear()
        val finalSeatNumber = arrayListOf<String>()
        bulkParams.clear()

        val map: MutableMap<String, String> = mutableMapOf()
        for (i in 0..passengerlistSelected.size.minus(1)) {
            pnr.add(passengerlistSelected[i].pnrNumber)
        }
        val distinct = pnr.distinct().toList()
        seatList.clear()

        for (i in 0..passengerlistSelected.size.minus(1)) {

            seatList.add(passengerlistSelected[i].seatNumber)
        }
        for (j in 0..distinct.size.minus(1)) {
            var seatNumList = ""

            for (i in 0..seatList.size.minus(1)) {
                if (passengerlistSelected[i].pnrNumber == distinct[j]) {
                    seatNumList += ",${passengerlistSelected[i].seatNumber}"
                }
            }
            finalSeatNumber.add(seatNumList)

        }

        for (i in 0..distinct.size.minus(1)) {
            map[distinct[i]] = finalSeatNumber[i]
        }
        for (i in 0..distinct.size.minus(1)) {
            bulkParams.add(
                BulkCancelParam(
                    distinct[i],
                    map.getValue(distinct[i]).toString().drop(1)
                )
            )
        }
        finalSeatNumber.clear()
        passengerlistSelected.clear()
        allSelected = 0
        binding.btnProceedCancel.gone()
        binding.rvreservationBulkCancel.gone()

        if (requireContext().isNetworkAvailable()) {
            pinAuthDialogBox(bulkParams)
        } else requireContext().noNetworkToast()
    }

    private fun pinAuthDialogBox(bulkParams: List<BulkCancelParam>) {
        if (shouldTicketCancel && currentCountry.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = requireActivity(),
                fragmentManager = childFragmentManager,
                pinSize = pinSize,
                getString(R.string.bulk_cancel),
                onPinSubmitted = { pin: String ->
                    bulkCancelApi(bulkParams, pin)
                    stopShimmerEffect()
                },
                onDismiss = {
                    stopShimmerEffect()
                }
            )
        } else {
            bulkCancelApi(bulkParams, "")
            stopShimmerEffect()
        }
    }

    private fun startShimmerEffect() {
        binding.shimmerBookTicket.visible()
        binding.rvreservationBulkCancel.gone()

//        binding.myBookingBookTicketContainer.gone()
        binding.shimmerBookTicket.startShimmer()

    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.shimmerBookTicket.gone()
//        binding.myBookingBookTicketContainer.visible()
        if (binding.shimmerBookTicket.isShimmerStarted) {
            binding.shimmerBookTicket.stopShimmer()
            binding.rvreservationBulkCancel.visible()

        }
    }

    private fun initRefreshListner() {
        binding.refreshLayout.setOnRefreshListener {
           callOnSwipe()
        }
    }

    private fun callOnSwipe() {
        startShimmerEffect()
        binding.NoResult.gone()
        allSelected = 0
        binding.selectAll.text = getString(R.string.select_all)
        passengerlistSelected.clear()
        binding.btnProceedCancel.gone()
//            Timber.d("seatselected al;S", "${passengerlistSelected.size}")
        pickUpChartApi(chartType)
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onReturnInstance(dialog: Any) {

    }
}