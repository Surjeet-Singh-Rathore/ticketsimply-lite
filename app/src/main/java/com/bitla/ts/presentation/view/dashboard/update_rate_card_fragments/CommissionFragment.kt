package com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.*
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_commission.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import setMaxLength
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*

class CommissionFragment : Fragment(), DialogButtonListener, DialogSingleButtonListener,
    OnItemClickListener, SeatSelectionAdapter.Callback {

    private lateinit var binding: FragmentCommisionBinding
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String? = null
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var mcalendar: Calendar
    private var fromDate: String? = null
    private var toDate: String? = null
    private var amount: String? = null
    private var incOrDec: Int = 0
    private var amountType: Int = 1
    private var fromDateDDMMYYYY: String? = null
    private var sourceId = ""
    private var source: String = ""
    private var destinationId: String = ""
    private var destination: String = ""
    private var busType: String? = null
    private var convertedDate: String? = null

    private var multistationFareDetails = mutableListOf<MultistationFareDetails>()
    private var seatPosition = 0
    private lateinit var spinnerItemsRoute: SpinnerItems2
    private var routeList: MutableList<SpinnerItems2> = mutableListOf()
    private var getSelectedSeatType:String?=null
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var routeId: String? = null
    private var amountTypeText: String = ""
    private var currency = ""
    private var locale: String? = ""

    private val seatList = mutableListOf<Service>()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var multipleHorizontalItemSelectionAdapter: MultipleHorizontalItemSelectionAdapter
    private var seatTypeList: MutableList<SpinnerItems> = mutableListOf()
    private var seatId = ""
    private var pinSize = 0
    private var modifyReservation = false
    private var currentCountry: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCommisionBinding.inflate(inflater, container, false)

        init()
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    private fun init() {
        getPref()
        if (requireContext().isNetworkAvailable()) {
            callMultiStationWiseFairApi()
            DialogUtils.showProgressDialog(requireContext())
        }else requireContext().noNetworkToast()
//        callServiceApi()
        onClick()
        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)
        fromDateDDMMYYYY = getTodayDate()
        val parser = SimpleDateFormat("dd-MM-yyyy")
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        fromDate = formatter.format(parser.parse(fromDateDDMMYYYY)!!)
        binding.etFromDateUrc.setText(fromDateDDMMYYYY)

        binding.percentageRadio.setOnClickListener {
            binding.etAmount.setText("")
            amountType = 1
            amountTypeText = "(%)"
            binding.layoutAmount.hint="${requireActivity().getString(R.string.amount)} $amountTypeText"
            binding.etAmount.setMaxLength(2)
        }

        binding.fixedRadio.setOnClickListener {
            binding.etAmount.setText("")
            binding.etAmount.setText("")
            amountType = 2
            amountTypeText = "($currency)"
            binding.layoutAmount.hint="${requireActivity().getString(R.string.amount)} $amountTypeText"
            maxDigitPreventAfterDecimal(binding.etAmount)
        }

        binding.incDecFare.setOnClickListener {
            incOrDec = 0
        }

        binding.decFare.setOnClickListener {
            incOrDec = 1
        }

        binding.selectSeatType.setOnItemClickListener { parent, view, position, id ->

            getSelectedSeatType = parent.getItemAtPosition(position).toString()
        }

        binding.etAmount.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    binding.btnUpdate.apply {
                        setBackgroundResource(R.drawable.button_selected_bg)
                        isEnabled = true
                    }
                } else {
                    binding.btnUpdate.apply {
                        setBackgroundResource(R.drawable.button_default_bg)
                        isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.chkSelectAll.setOnClickListener {

            seatId = ""

            if (binding.chkSelectAll.isChecked) {
                seatList.forEach {
                    it.isSeatChecked = true
                }
                binding.chkSelectAll.text = "All"
            } else {
                seatList.forEach {
                    it.isSeatChecked = false
                }
                binding.chkSelectAll.text = "All"
            }

            multipleHorizontalItemSelectionAdapter.addData(seatList)
        }

        updateRateCardCommissionObserver()
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }

    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        resID = PreferenceUtils.getString(getString(R.string.updateRateCard_resId))
        convertedDate = PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate)).toString()
        routeId= PreferenceUtils.getString(getString(R.string.routeId)).toString()
        (activity as BaseActivity).getPrivilegeBase()?.let { privilegeResponseModel ->
            pinSize = privilegeResponseModel.pinCount ?: 6
            modifyReservation = privilegeResponseModel.pinBasedActionPrivileges?.modifyReservation ?: false
            currentCountry = privilegeResponseModel.country ?: ""
        }
    }

    private fun onClick() {

        binding.btnUpdate.setOnClickListener {
            source = PreferenceUtils.getString(getString(R.string.updateRateCard_origin)).toString()
            destination = PreferenceUtils.getString(getString(R.string.updateRateCard_destination)).toString()
            sourceId = PreferenceUtils.getString(getString(R.string.updateRateCard_originId)).toString()
            destinationId = PreferenceUtils.getString(getString(R.string.updateRateCard_destinationId)).toString()
            convertedDate = PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate)).toString()
            busType = PreferenceUtils.getString(getString(R.string.updateRateCard_busType))
            
            PreferenceUtils.apply {
                setPreference(PREF_SOURCE, source)
                setPreference(PREF_DESTINATION, destination)
                setPreference(PREF_TRAVEL_DATE, getDateDMY(convertedDate!!)!!)
                setPreference(PREF_LAST_SEARCHED_SOURCE, source)
                setPreference(PREF_LAST_SEARCHED_DESTINATION, destination)
                removeKey(PREF_BOARDING_STAGE_DETAILS)
                removeKey(PREF_DROPPING_STAGE_DETAILS)
            }
            
            val fromDateDialog = binding.etFromDateUrc.text.toString()
            val toDateDialog = binding.etToDateUrc.text.toString()
            amount = binding.etAmount.text.toString()
            
            var amountTypeFixed = ""
            var amountTypePercentage = ""
            var increaseOrDecreaseByLabel = ""

            if (amountType == 1) {
                amountTypeFixed = ""
                amountTypePercentage = "%"
            } else {
                amountTypeFixed = currency
                amountTypePercentage = ""
            }

            increaseOrDecreaseByLabel = if (incOrDec == 0) {
                requireContext().getString(R.string.increase_by)
            } else {
                requireContext().getString(R.string.decrease_by)
            }

            seatList.forEach{
                if (it.isSeatChecked) {
                    seatId += it.routeId.toString().replace(".0", "") + ","
                }
            }

            if (seatId.isNotEmpty()) {
                seatId = seatId.substring(0, seatId.lastIndexOf(","))
            }

            if (seatId.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please select at least one seat type",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (amount.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Amount should not be blank",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                DialogUtils.UpdateRcDialoge(
                    context = requireContext(),
                    title = requireContext().getString(R.string.update_rate_card_question),
                    message = "",
                    increaseby = "$amountTypeFixed$amount$amountTypePercentage",
                    increaseOrDecreaseByLabel = increaseOrDecreaseByLabel,
                    fromDate = convertedDate?.let { getDateDMY(it)}.toString(),
                    toDate = convertedDate?.let { getDateDMY(it)}.toString(),
                    srcDest = "$source-$destination",
                    journeyDate = busType.toString(),
                    buttonLeftText = requireActivity().getString(R.string.goBack),
                    buttonRightText = requireActivity().getString(R.string.confirm),
                    dialogButtonListener = this
                )
            }
            Timber.d("cmsSeatIdList - $seatId")

        }

        binding.etFromDateUrc.setOnClickListener {
            openFromDateDialog()
        }
        binding.etToDateUrc.setOnClickListener {
            openToDateDialog()
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun openFromDateDialog() {
        val listener =
            DatePickerDialog.OnDateSetListener {
                    _, year, monthOfYear, dayOfMonth,
                ->
                val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
                val date = dateFormat.parse("$dayOfMonth-${monthOfYear + 1}-$year")
                binding.etFromDateUrc.setText(dateFormat.format(date).toString())

                fromDate = binding.etFromDateUrc.text.toString()

                fromDateDDMMYYYY = fromDate
                val parser = SimpleDateFormat("dd-MM-yyyy")
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                fromDate = formatter.format(parser.parse(fromDate))
                toDate = null
                binding.etToDateUrc.setText("")
                binding.etToDateUrc.clearFocus()

            }
        setDateLocale(locale!!,requireContext())
        val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
        val dateInString: String = getTodayDate()
        val simpleDateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
        val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormat.parse(dateInString)
        calendar.add(Calendar.DATE, 28)
        dpDialog.datePicker.maxDate = calendar.timeInMillis
        val calendarMinDate = Calendar.getInstance()
        calendarMinDate.time = simpleDateFormat.parse(dateInString)
        dpDialog.datePicker.minDate = calendarMinDate.timeInMillis
        dpDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun openToDateDialog() {
        if (fromDate.isNullOrEmpty()) {
            Toast.makeText(context, requireActivity().getString(R.string.validate_from_date), Toast.LENGTH_SHORT).show()
        } else {
            val listener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth, ->
                    val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
                    val date = dateFormat.parse("$dayOfMonth-${monthOfYear + 1}-$year")
                    binding.etToDateUrc.setText(dateFormat.format(date).toString())
                    toDate = binding.etToDateUrc.text.toString()
                    val parser = SimpleDateFormat("dd-MM-yyyy")
                    val formatter = SimpleDateFormat("yyyy-MM-dd")
                    toDate = formatter.format(parser.parse(toDate))

                    if(!binding.etAmount.text.isNullOrEmpty()){
                        binding.btnUpdate.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                            binding.btnUpdate.isEnabled=true
                        }
                    }
                }
            setDateLocale(locale!!,requireContext())
            val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
            val dateInString: String = getTodayDate()
            val simpleDateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            val calendar = Calendar.getInstance()
            calendar.time = simpleDateFormat.parse(dateInString)
            calendar.add(Calendar.DATE, 28)
            dpDialog.datePicker.maxDate = calendar.timeInMillis
            val calenderMinDate = Calendar.getInstance()
            calenderMinDate.time = simpleDateFormat.parse(fromDateDDMMYYYY)
            dpDialog.datePicker.minDate = calenderMinDate.timeInMillis
            dpDialog.show()
        }
    }

    private fun updateRateCardCommissionObserver() {

        pickUpChartViewModel.updateRateCardCommissionResponse.observe(viewLifecycleOwner) {

            DialogUtils.dismissProgressDialog()

            Timber.d("reservationCommission ${it}")

            if (it != null) {
                when (it.code) {
                    200 -> {

                        if (isAttachedToActivity()){
                            it.result?.message?.let { it1 ->
                                DialogUtils.successfulMsgDialog(
                                    requireContext(), it1
                                )
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                activity?.finish()
                            }, 2000)
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
                    else -> {
                        it.result?.message?.let { it1 -> requireContext().toast(it1) }
                    }
                }
            }
            else{
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun callUpdateRateCardCommissionApi(authPin: String) {

        pickUpChartViewModel.updateRateCardCommissionApi(
            ReqBody(
                apiKey = loginModelPref.api_key,
                id = resID.toString(),
                routeId = routeId.toString(),
                type = amountType.toString(),
                incOrDec = incOrDec.toString(),
                category = "cmsn",
                cmsn = amount.toString(),
                seatType = seatId,
                fromDate = convertedDate.toString(),
                toDate = convertedDate.toString(),
                locale = locale,
                authPin = authPin
            ),
            apiType = manage_fare_method_name
        )
    }

    override fun onLeftButtonClick() {
        seatId = ""
    }

    private fun pinAuthDialog(){
        if (modifyReservation && currentCountry.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = requireActivity(),
                fragmentManager = childFragmentManager,
                pinSize = pinSize,
                getString(R.string.rate_card_commission).capitalize(),
                onPinSubmitted = { pin: String ->
                    callUpdateRateCardCommissionApi(pin)
                    DialogUtils.dismissProgressDialog()
                },
                onDismiss = {
                    DialogUtils.dismissProgressDialog()
                }
            )
        } else {
            callUpdateRateCardCommissionApi("")
            DialogUtils.dismissProgressDialog()
        }
    }

    override fun onRightButtonClick() {
        DialogUtils.showProgressDialog(requireContext())
        pinAuthDialog()
    }

//    private fun callServiceApi() {
//        val bccId = PreferenceUtils.getBccId().toString()
//        val loginModelPref = PreferenceUtils.getLogin()
//        sourceId = PreferenceUtils.getString(getString(R.string.updateRateCard_originId)).toString()
//        destinationId = PreferenceUtils.getString(getString(R.string.updateRateCard_destinationId)).toString()
//
//        val serviceDetailsRequest = ServiceDetailsRequest(
//            bccId, service_details_method, format_type,
//            com.bitla.ts.domain.pojo.service_details.request.ReqBody(
//                resID.toString(),
//                loginModelPref.api_key,
//                operator_api_key,
//                locale,
//                sourceId,
//                destinationId,
//                response_format
//            )
//        )
//        sharedViewModel.getServiceDetails(
//            loginModelPref.auth_token,
//            loginModelPref.api_key, serviceDetailsRequest, service_details_method
//        )
//        serviceDetailsApiObserver()
//    }

    private fun serviceDetailsApiObserver() {
        sharedViewModel.serviceDetails.observe(viewLifecycleOwner) {
            if (it.code == 200) {
                routeId = it.body.routeId.toString()
            } else
                it.message?.let { it1 ->
                    Toast.makeText(requireContext(), "commision${it.message}",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun callMultiStationWiseFairApi() {
        val loginModelPref = PreferenceUtils.getLogin()
        pickUpChartViewModel.fetchMultiStatioWiseFareApi(
            com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.request.ReqBody(
                apiKey = loginModelPref.api_key,
                reservation_id = resID.toString(),
                channelId = "",
                templateId = "",
                date = convertedDate.toString(),
                locale = locale
            ),
            apiType = manage_fare_method_name
        )
        multiStationWiseFareObserver()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun multiStationWiseFareObserver() {
        pickUpChartViewModel.fetchMultiStatioWiseFareResponse.observe(viewLifecycleOwner) {
            seatTypeList.clear()
            DialogUtils.dismissProgressDialog()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        multistationFareDetails = it.multistation_fare_details

                        multistationFareDetails.forEach { it ->
                            spinnerItemsRoute = SpinnerItems2(
                                it.origin_id.toInt(), it.origin_name, "-",
                                it.destination_id.toInt(), it.destination_name)

                            routeList.add(spinnerItemsRoute)
                            Timber.d("$spinnerItemsRoute")

                            seatPosition = it.fareDetails[0].id!!.toInt()

                        }

                        it.multistation_fare_details[0].fareDetails.forEach { it ->
//                            spinnerItemsSeatType = SpinnerItems(it.id!!.toInt(), it.seat_type.toString())
//                            seatTypeList.add(spinnerItemsSeatType)
//                            if (spinnerItemsSeatType.id == seatPosition) {
//                                binding.selectSeatType.setText("${it.seat_type}", false)
//                            }
                            val seatModel = Service()
                            seatModel.routeId = it.id?.toInt()
                            seatModel.number = it.seatType
                            seatList.add(seatModel)
                        }
//                        getSelectedSeatType= seatTypeList[0].toString()
                        layoutManager = GridLayoutManager(requireContext(), 3)
//                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        binding.rvSelectSeat.layoutManager = layoutManager
                        multipleHorizontalItemSelectionAdapter = MultipleHorizontalItemSelectionAdapter(requireContext(), this)
                        multipleHorizontalItemSelectionAdapter.addData(seatList)
                        binding.rvSelectSeat.adapter = multipleHorizontalItemSelectionAdapter
                        multipleHorizontalItemSelectionAdapter.notifyDataSetChanged()

//                        binding.apply {
//                            tvSeatType.visible()
//                            chkSelectAll.visible()
//                            view.visible()
//                            rvSelectSeat.visible()
//                        }

                        if ((activity as BaseActivity).getPrivilegeBase()!= null) {
                            val privilegeResponse = (activity as BaseActivity).getPrivilegeBase()
                            privilegeResponse?.let {
                                currency = privilegeResponse.currency
                            }

                            if (!privilegeResponse?.country.isNullOrEmpty()) {
                                if (privilegeResponse?.country.equals("india", true)) {
                                    binding.apply {
                                        tvSeatType.visible()
                                        chkSelectAll.visible()
//                                        view.visible()
                                        rvSelectSeat.visible()
                                    }

                                    binding.selectSeatType.setAdapter(
                                        ArrayAdapter(
                                            requireContext(),
                                            R.layout.spinner_dropdown_item,
                                            R.id.tvItem,
                                            seatTypeList
                                        )
                                    )
                                } else {
                                    binding.apply {
                                        tvSeatType.visible()
                                        chkSelectAll.visible()
//                                        view.visible()
                                        rvSelectSeat.visible()
                                    }
                                }
                            }

                        } else {
                            requireActivity().toast(getString(R.string.server_error))
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
                    else -> {
                        if (it.result?.message != null){
                            it.result.message.let { it1 -> requireContext().toast(it1) }
                        }
                    }
                }
            }
            else{
                requireContext().toast(getString(R.string.server_error))
            }
        }
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

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
        val index = seatList.indexOfFirst {
            it.routeId == position
        }
        seatList[index].isSeatChecked = data == "true"
        seatList.forEach {
            if (!it.isSeatChecked) {
                binding.chkSelectAll.isChecked = false
                binding.chkSelectAll.text = "All"
                return@forEach
            }
        }
        seatId = ""
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onCheckedChanged(item: String?, isChecked: Boolean) {
    }
}