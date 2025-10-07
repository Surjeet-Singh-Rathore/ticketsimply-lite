package com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.OnSeatSelectionListener
import com.bitla.ts.databinding.FragmentSeatwisefragmentBinding
import com.bitla.ts.domain.pojo.SpinnerItems
import com.bitla.ts.domain.pojo.SpinnerItems2
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details.request.ServiceDetailsRequest
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.request.ReqBody
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultistationFareDetails
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.request.FareDetail
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.request.UpdateRateCardSeatWiseRequest
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request.FareDetailPerSeat
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request.UpdateRateCardPerSeatRequest
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.fragments.AllCoachFragment
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.text.SimpleDateFormat
import java.util.*


class SeatWiseFragment : BaseFragment(), OnSeatSelectionListener {

    private var privilegeDetails: PrivilegeResponseModel? = null
    private var country: String = ""
    private var travelDate: String = ""
    private var currencyFormat: String = ""
    private lateinit var binding: FragmentSeatwisefragmentBinding
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    var fromDate: String? = null
    var fromDateDDMMYYYY: String? = null
    var toDate: String? = null
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var mcalendar: Calendar
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    var seatPosition = 0
    var selectedViaRoutePosition = 0
    private var selectedSeatTypePosition = 0
    var isRvItemEmpty: Boolean = true
    private var multistationFareDetails = mutableListOf<MultistationFareDetails>()
    private lateinit var spinnerItemsRoute: SpinnerItems2
    private var routeList: MutableList<SpinnerItems2> = mutableListOf()
    private lateinit var spinnerItemsSeatType: SpinnerItems
    private var seatTypeList: MutableList<SpinnerItems> = mutableListOf()
    private lateinit var spinnerItemsFare: SpinnerItems
    private var fareAmtList: MutableList<SpinnerItems> = mutableListOf()
    private var fareAmtListNew: MutableList<SpinnerItems> = mutableListOf()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var routeId: String? = null
    private var selectedOriginId = 0
    private var selectedDestinationId = 0
    private var selectedFare: String? = null
    private var getSeatTypeFare: String? = null
    private var selectedSeatType: String? = null
    private var getSelectedSeatTypeId: String? = null
    private var selectedRadioBtn: String = ""
    private var updateFareDetailsList: ArrayList<FareDetail> = ArrayList()
    private var fareDetailPerSeatList: ArrayList<FareDetailPerSeat> = ArrayList()
    private lateinit var commonCoachsingle: AllCoachFragment
    private var resID: String? = null
    private var sourceId = ""
    private var source: String = ""
    private var destinationId: String? = null
    private var destination: String = ""
    private var busType: String? = null
    private var convertedDate: String? = null
    private var isAllowToConfigureSeatWiseFare: Boolean? = false
    private var locale: String? = ""
    private var pinSize = 0
    private var modifyReservation = false

    private val ORIGIN_ID_FOR_ALL = -1
    private val DESTINATION_ID_FOR_ALL = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSeatwisefragmentBinding.inflate(inflater, container, false)
        selectedRadioBtn = requireActivity().getString(R.string.by_seat_type)
        getPref()
        callMultiStationWiseFairApi()
        init()


        binding.confirmSeatTypeFareChangeBtn.setOnClickListener {
            selectedSeatType = binding.selectSeatType.text.toString()
            if (requireContext().isNetworkAvailable())
                authPinDialog()
            else requireContext().noNetworkToast()
        }

        binding.confirmPerSeatFareChangeBtn.setOnClickListener {
            if(country.equals("india",true)){
                selectedSeatType = binding.selectSeatType.text.toString()
                if(binding.newFareET.text.toString().startsWith("0")){
                    requireContext().toast(getString(R.string.please_enter_valid_amount))
                }else{
                    if(fareDetailPerSeatList.size > 0){
                        for (i in 0 until fareDetailPerSeatList.size){
                            fareDetailPerSeatList[i].fare = binding.newFareET.text.toString()
                        }
                    }

                    if (requireContext().isNetworkAvailable())
                        callSeatWisePerSeatFareApi()
                    else requireContext().noNetworkToast()
                }

            } else{
                selectedSeatType = binding.selectSeatType.text.toString()
                if (requireContext().isNetworkAvailable())
                    callSeatWisePerSeatFareApi()
                else requireContext().noNetworkToast()
            }
        }

        setUpSeatWiseSeatTypeObserver()
        setUpSeatWisePerSeatObserver()
        setUpServiceDetailsObserver()

        if (!binding.etAmount.text.isNullOrEmpty()) {
            binding.confirmPerSeatFareChangeBtn.isEnabled = true
            binding.confirmSeatTypeFareChangeBtn.isEnabled = true
        }

        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                maxDigitPreventAfterDecimal(binding.etAmount)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    if (!binding.etToDateUrc.text.isNullOrEmpty()) {

                        binding.confirmSeatTypeFareChangeBtn.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                        }
                    }
                } else {
                    binding.confirmSeatTypeFareChangeBtn.apply {
                        setBackgroundResource(R.drawable.button_default_bg)
                        isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
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
    }

    private fun authPinDialog() {
        if (modifyReservation && country.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = requireActivity(),
                fragmentManager = childFragmentManager,
                pinSize = pinSize,
                getString(R.string.seat_wise_fare),
                onPinSubmitted = { pin: String ->
                    callSeatWiseFareApi(pin)
                },
                onDismiss = null
            )
        } else {
            callSeatWiseFareApi("")
        }

    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {
        
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        parentFragment as OnSeatSelectionListener?
    }*/

    override fun onButtonClick(view: Any, dialog: Dialog) {
        
    }

    @SuppressLint("SimpleDateFormat")
    private fun init() {


        val parser = SimpleDateFormat("dd-MM-yyyy")
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        travelDate =
            PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate)).toString()
        if(!travelDate.isNullOrEmpty()){
            mcalendar = Calendar.getInstance()
            day = travelDate.substringAfterLast("-").replace("0","").toInt()
            year = mcalendar.get(Calendar.YEAR)
            month = travelDate.substringAfter("-").substringBefore("-").replace("0","").toInt() - 1
            fromDate = travelDate
            val inputDate: Date = formatter.parse(travelDate)
            fromDateDDMMYYYY = parser.format(inputDate)

        }else{
            mcalendar = Calendar.getInstance()
            day = mcalendar.get(Calendar.DAY_OF_MONTH)
            year = mcalendar.get(Calendar.YEAR)
            month = mcalendar.get(Calendar.MONTH)
            fromDateDDMMYYYY = getTodayDate()
            fromDate = formatter.format(parser.parse(fromDateDDMMYYYY))
        }

        binding.etFromDateUrc.setText(fromDateDDMMYYYY)
        changeButtonBackground(fromDate, toDate, isRvItemEmpty)

        PreferenceUtils.putString("seatwiseFare", "SEATWISE")
        binding.bySeatTypeRadioBtn.setOnClickListener {
            binding.layoutSelectSeatType.visible()
            binding.layoutSelectViaRoute.visible()
            binding.layoutEdtAmount.visible()
            binding.coachProgressBar.visible()
            binding.newFareG.gone()

            selectedRadioBtn = binding.bySeatTypeRadioBtn.text.toString()
            PreferenceUtils.setPreference("PERSEAT", false)
            PreferenceUtils.setPreference("isEditSeatWise", false)

            Handler(Looper.getMainLooper()).postDelayed({
                setUpServiceDetailsObserver()
                binding.coachProgressBar.gone()
            }, 500)

            binding.confirmSeatTypeFareChangeBtn.visible()
            binding.confirmPerSeatFareChangeBtn.gone()
        }


        binding.perSeatRadioBtn.setOnClickListener {
            binding.layoutSelectSeatType.gone()
            binding.layoutEdtAmount.gone()
            binding.layoutSelectViaRoute.visible()
            binding.coachProgressBar.visible()

            selectedRadioBtn = binding.perSeatRadioBtn.text.toString()
            PreferenceUtils.setPreference("PERSEAT", true)
            PreferenceUtils.setPreference("isEditSeatWise", true)

            Handler(Looper.getMainLooper()).postDelayed({
                setUpServiceDetailsObserver()
                binding.coachProgressBar.gone()
            }, 500)

            binding.confirmSeatTypeFareChangeBtn.gone()
            binding.confirmPerSeatFareChangeBtn.visible()
        }

        binding.selectSeatType.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                seatTypeList
            )
        )


        binding.selectSeatType.setOnItemClickListener { parent, view, position, id ->

            val getSelectedItem2 = parent.getItemAtPosition(position)
            getSelectedSeatTypeId = seatTypeList[position].id.toString()

            selectedSeatTypePosition = position

            for (n in seatTypeList) {
                if (n == getSelectedItem2 && selectedViaRoutePosition < routeList.size) {
                    //binding.etAmount.setText("${multistationFareDetails[selectedViaRoutePosition].fare_detailResponses[selectedViaRoutePosition].fare}")
                    val fare = if (selectedOriginId == ORIGIN_ID_FOR_ALL) {
                        multistationFareDetails[selectedViaRoutePosition].fareDetails[selectedSeatTypePosition].fare
                    } else {
                        multistationFareDetails[selectedViaRoutePosition -1].fareDetails[selectedSeatTypePosition].fare
                    }
                    binding.etAmount.setText(
                        "${
                            if (fare != null && fare.isNotEmpty()) (fare.toDouble()).convert(
                                currencyFormat
                            ) else fare
                        }"
                    )
                    break
                }
            }
        }

        binding.selectViaRoute.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                routeList
            )
        )

        binding.selectViaRoute.setOnItemClickListener { parent, view, position, id ->

            selectedOriginId = routeList[position].id
            selectedDestinationId = routeList[position].id2

            val getSelectedItem = parent.getItemAtPosition(position)
            selectedViaRoutePosition = position

            for (n in routeList) {
                if (n == getSelectedItem && selectedViaRoutePosition < routeList.size) {
                    if (selectedOriginId == ORIGIN_ID_FOR_ALL) {
                        binding.etAmount.setText("${multistationFareDetails[selectedViaRoutePosition].fareDetails[selectedSeatTypePosition].fare}")
                    } else {
                        binding.etAmount.setText("${multistationFareDetails[selectedViaRoutePosition -1].fareDetails[selectedSeatTypePosition].fare}")
                    }
                    break
                }
            }
            callServiceApi()
        }

        binding.etFromDateUrc.setOnClickListener {
            openFromDateDialog()
            changeButtonBackground(fromDate, toDate, isRvItemEmpty)
        }
        binding.etToDateUrc.setOnClickListener {
            openToDateDialog()
            changeButtonBackground(fromDate, toDate, isRvItemEmpty)
        }


        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getSeatTypeFare = s.toString()
            }
        })

        binding.newFareET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.toString().isNotEmpty() && binding.etToDateUrc.text.toString().isNotEmpty()){
                    binding.confirmPerSeatFareChangeBtn.apply {
                        setBackgroundResource(R.drawable.button_selected_bg)
                        isEnabled = true
                        binding.confirmPerSeatFareChangeBtn.isEnabled = true
                    }
                }else{
                    binding.confirmPerSeatFareChangeBtn.apply {
                        setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.un_select_color))
                        isEnabled = false
                        binding.confirmPerSeatFareChangeBtn.isEnabled = false
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.etToDateUrc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.toString().isNotEmpty() && binding.newFareET.text.toString().isNotEmpty()){
                    binding.confirmPerSeatFareChangeBtn.apply {
                        setBackgroundResource(R.drawable.button_selected_bg)
                        isEnabled = true
                        binding.confirmPerSeatFareChangeBtn.isEnabled = true
                    }
                }else{
                    binding.confirmPerSeatFareChangeBtn.apply {
                        setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.un_select_color))
                        isEnabled = false
                        binding.confirmPerSeatFareChangeBtn.isEnabled = false
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })



    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
        privilegeDetails = (activity as BaseActivity).getPrivilegeBase()

        pinSize = privilegeDetails?.pinCount ?: 6
        modifyReservation = privilegeDetails?.pinBasedActionPrivileges?.modifyReservation ?: false

        country = privilegeDetails?.country?:""

        resID = PreferenceUtils.getString(getString(R.string.updateRateCard_resId))

        convertedDate =
            PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate)).toString()


        if (privilegeDetails!= null) {
            val privilegeResponseModel = privilegeDetails as PrivilegeResponseModel

            privilegeResponseModel.apply {
                if (allowToConfigureSeatWiseFare != null) {
                    isAllowToConfigureSeatWiseFare = allowToConfigureSeatWiseFare
                }
            }

            currencyFormat =
                getCurrencyFormat(requireContext(), privilegeResponseModel.currencyFormat)
        }

        if (isAllowToConfigureSeatWiseFare == true) {
            binding.perSeatRadioBtn.visible()
        } else {
            binding.perSeatRadioBtn.gone()
        }

        if(country.equals("india",true)){
            binding.perSeatRadioBtn.text = getString(R.string.multiple_seats)
        }else{
            binding.perSeatRadioBtn.text = getString(R.string.per_seat)
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
            Toast.makeText(context, "Please select from date", Toast.LENGTH_SHORT).show()
        } else {
            val listener =
                DatePickerDialog.OnDateSetListener {
                        _, year, monthOfYear, dayOfMonth,
                    ->
                    val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
                    val date = dateFormat.parse("$dayOfMonth-${monthOfYear + 1}-$year")
                    binding.etToDateUrc.setText(dateFormat.format(date).toString())
                    toDate = binding.etToDateUrc.text.toString()
                    val parser = SimpleDateFormat("dd-MM-yyyy")
                    val formatter = SimpleDateFormat("yyyy-MM-dd")
                    toDate = formatter.format(parser.parse(toDate))

                    if (!binding.etAmount.text.isNullOrEmpty()) {
                        binding.confirmSeatTypeFareChangeBtn.apply {

                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                            if(binding.newFareET.text.toString().isNotEmpty()){
                                binding.confirmPerSeatFareChangeBtn.isEnabled = true
                            }

                        }
                    }
                    if(country.equals("indonesia",true)){
                        if(fareDetailPerSeatList.size > 0){
                            binding.confirmPerSeatFareChangeBtn.apply {                                 //commented for new flow multi select per seat
                                setBackgroundResource(R.drawable.button_selected_bg)
                                isEnabled = true
                                binding.confirmPerSeatFareChangeBtn.isEnabled = true
                            }
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

    private fun callMultiStationWiseFairApi() {
        if (requireContext().isNetworkAvailable()) {
            val loginModelPref = PreferenceUtils.getLogin()

            pickUpChartViewModel.fetchMultiStatioWiseFareApi(
                ReqBody(
                    apiKey = loginModelPref.api_key,
                    reservation_id =  resID.toString(),
                    date = convertedDate.toString(),
                    channelId = "",
                    templateId = "",
                    locale = locale
                ),
                manage_fare_method_name
            )
            multiStationWiseFareObserver()
        } else requireContext().noNetworkToast()
    }

    @SuppressLint("SetTextI18n")
    private fun multiStationWiseFareObserver() {
        pickUpChartViewModel.fetchMultiStatioWiseFareResponse.observe(viewLifecycleOwner) {

            seatTypeList.clear()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        multistationFareDetails = it.multistation_fare_details

                        val routeItemForAll = SpinnerItems2(ORIGIN_ID_FOR_ALL,"-",getString(R.string.all),DESTINATION_ID_FOR_ALL,"-")
                        routeList.add(routeItemForAll)

                        multistationFareDetails.forEach { it ->
                            spinnerItemsRoute = SpinnerItems2(
                                it.origin_id.toInt(), it.origin_name, "-",
                                it.destination_id.toInt(), it.destination_name
                            )

                            routeList.add(spinnerItemsRoute)
                            Timber.d("$spinnerItemsRoute")

                            seatPosition = it.fareDetails[0].id!!.toInt()

                            it.fareDetails.forEach {
                                spinnerItemsFare = SpinnerItems(it.id!!.toInt(), it.fare.toString())
                                fareAmtListNew.add(spinnerItemsFare)
                            }

                        }

                        it.multistation_fare_details[0].fareDetails.forEach { it ->
                            spinnerItemsSeatType =
                                SpinnerItems(it.id!!.toInt(), it.seatType.toString())
                            seatTypeList.add(spinnerItemsSeatType)

                            spinnerItemsFare = SpinnerItems(it.id!!.toInt(), it.fare.toString())
                            fareAmtList.add(spinnerItemsFare)

                            if (spinnerItemsSeatType.id == seatPosition) {
                                binding.selectSeatType.setText("${it.seatType}", false)
                                if (it.fare != null && it.fare!!.isNotEmpty()) {
                                    binding.etAmount.setText(
                                        it.fare
                                    )
                                }
                                if (routeList.size > 0) {
                                    binding.selectViaRoute.setText(
                                        "${routeList[0].value} ${routeList[0].dash} ${routeList[0].value2}",
                                        false
                                    )
                                }
                            }
                        }

                        getSelectedSeatTypeId = seatTypeList[0].id.toString()
                        selectedOriginId = routeList[0].id
                        selectedDestinationId = routeList[0].id2

                        callServiceApi()

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
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> requireContext().toast(it1) }
                        }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun callServiceApi() {
        if (requireContext().isNetworkAvailable()) {
            val bccId = PreferenceUtils.getBccId().toString()

            var originId = 0
            var destinationId = 0
            if (routeList.size > 1) {
                if (selectedOriginId == ORIGIN_ID_FOR_ALL) {
                    originId = routeList[1].id
                    destinationId = routeList[1].id2
                } else {
                    originId = selectedOriginId
                    destinationId = selectedDestinationId
                }
            }

            val loginModelPref = PreferenceUtils.getLogin()
            sharedViewModel.getServiceDetails(
                resID.toString(),
                loginModelPref.api_key,
                originId.toString(),destinationId.toString(), operator_api_key,locale!!, service_details_method,excludePassengerDetails = false
            )
            serviceDetailsApiObserver()
        } else requireContext().noNetworkToast()
    }

    private fun serviceDetailsApiObserver() {
        sharedViewModel.serviceDetails.observe(viewLifecycleOwner) {
            when (it.code) {
                200 -> {
                    routeId = it.body.routeId.toString()
                }
                401 -> {
                    /*DialogUtils.unAuthorizedDialog(
                        requireContext(),
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    (activity as BaseActivity).showUnauthorisedDialog()

                }
                else -> it.message?.let { it1 ->
                    Toast.makeText(
                        requireContext(), it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun callSeatWiseFareApi(authPin: String) {
        val fareDetailsArrayList = FareDetail(
            getSeatTypeFare.toString(), getSelectedSeatTypeId
        )

        updateFareDetailsList.add(fareDetailsArrayList)
        
        pickUpChartViewModel.updateRateCardSeatWiseApi(
            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.request.ReqBody(
                apiKey = loginModelPref.api_key,
                id = resID.toString(),
                routeId = routeId.toString(),
                category = "seatwise",
                originId = selectedOriginId,
                destinationId = selectedDestinationId,
                fromDate = fromDate.toString(),
                toDate = toDate.toString(),
                fareDetails = updateFareDetailsList,
                locale = locale,
                authPin = authPin
            ),
            manage_fare_method_name
        )
    }

    private fun setUpSeatWiseSeatTypeObserver() {
        pickUpChartViewModel.updateRateCardSeatWiseResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.code == 200) {

                    source = PreferenceUtils.getString(getString(R.string.updateRateCard_origin))
                        .toString()
                    destination =
                        PreferenceUtils.getString(getString(R.string.updateRateCard_destination))
                            .toString()
                    sourceId =
                        PreferenceUtils.getString(getString(R.string.updateRateCard_originId))
                            .toString()
                    destinationId =
                        PreferenceUtils.getString(getString(R.string.updateRateCard_destinationId))
                            .toString()
                    convertedDate =
                        PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate))
                            .toString()
                    busType = PreferenceUtils.getString(getString(R.string.updateRateCard_busType))

                    PreferenceUtils.setPreference(PREF_SOURCE, source)
                    PreferenceUtils.setPreference(PREF_DESTINATION, destination)
                    PreferenceUtils.setPreference(PREF_TRAVEL_DATE, getDateDMY(convertedDate!!)!!)
                    PreferenceUtils.setPreference(PREF_LAST_SEARCHED_SOURCE, source)
                    PreferenceUtils.setPreference(PREF_LAST_SEARCHED_DESTINATION, destination)

                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)

                    if (isAttachedToActivity()) {
                        DialogUtils.successfulMsgDialog(
                            requireContext(), it.result.message
                        )

                        Handler(Looper.getMainLooper()).postDelayed({
                            activity?.apply {
                                val resultIntent = Intent().apply {
                                    putExtra("call_coach", true)
                                }
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
                            }
                        }, 2000)
                    }

                } else {
                    requireContext().toast(it.result.message)
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun callSeatWisePerSeatFareApi() {
        binding.mainProgressBar.visible()
        binding.confirmPerSeatFareChangeBtn.isEnabled = false
        val fareDetailsArrayList = FareDetail(selectedFare.toString())
        updateFareDetailsList.add(fareDetailsArrayList)
        
        pickUpChartViewModel.updateRateCardSeatWisePerSeatApi(
            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request.ReqBody(
                apiKey = loginModelPref.api_key,
                id = resID.toString(),
                routeId = routeId.toString(),
                category = "perseat",
                originId = selectedOriginId,
                destinationId = selectedDestinationId,
                fromDate = fromDate.toString(),
                toDate = toDate.toString(),
                fareDetails = fareDetailPerSeatList,
                locale = locale
            ),
            manage_fare_method_name
        )
    }

    private fun setUpSeatWisePerSeatObserver() {

        pickUpChartViewModel.updateRateCardPerSeatResponse.observe(viewLifecycleOwner) {
            binding.mainProgressBar.gone()
            binding.confirmPerSeatFareChangeBtn.isEnabled = true

            if (it != null) {
                if (it.code == 200) {

                    source = PreferenceUtils.getString(getString(R.string.updateRateCard_origin))
                        .toString()
                    destination =
                        PreferenceUtils.getString(getString(R.string.updateRateCard_destination))
                            .toString()
                    sourceId =
                        PreferenceUtils.getString(getString(R.string.updateRateCard_originId))
                            .toString()
                    destinationId =
                        PreferenceUtils.getString(getString(R.string.updateRateCard_destinationId))
                            .toString()
                    convertedDate =
                        PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate))
                            .toString()
                    busType = PreferenceUtils.getString(getString(R.string.updateRateCard_busType))

                    PreferenceUtils.setPreference(PREF_SOURCE, source)
                    PreferenceUtils.setPreference(PREF_DESTINATION, destination)
                    PreferenceUtils.setPreference(PREF_TRAVEL_DATE, getDateDMY(convertedDate!!)!!)
                    PreferenceUtils.setPreference(PREF_LAST_SEARCHED_SOURCE, source)
                    PreferenceUtils.setPreference(PREF_LAST_SEARCHED_DESTINATION, destination)

                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)

                    it.result?.message?.let { it1 ->
                        DialogUtils.successfulMsgDialog(
                            requireContext(), it1
                        )
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        activity?.apply {
                            val resultIntent = Intent().apply {
                                putExtra("call_coach", true)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    }, 2000)

                } else {
                    it.result?.message?.let { it1 -> requireContext().toast(it1) }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
            fareDetailPerSeatList.clear()
        }
    }

    private fun changeButtonBackground(fromDate: String?, toDate: String?, isEmpty: Boolean) {
        pickUpChartViewModel.changeButtonBackground(fromDate, toDate, isEmpty)
    }

    private fun setUpServiceDetailsObserver() {

        sharedViewModel.serviceDetails.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.code == 200) {
                    it.body.routeId?.let { it1 -> setRouteId(it1, it.body.isGstApplicable) }

                        binding.coachProgressBar.gone()
                        binding.layoutCoachSingle.visible()
                        commonCoachsingle =
                            childFragmentManager.findFragmentById(R.id.layout_coach_single) as AllCoachFragment
                        if (::commonCoachsingle.isInitialized) {
                            commonCoachsingle.setCoachData(it.body)

                            commonCoachsingle.binding.apply {
                                selectallseats.gone()
                                selectAllSeatsToUnblock.gone()
                                layoutBlockAllSeats.gone()
                                manifestBtn.gone()
                            }
                        }
                        commonCoachsingle.binding.layoutBlockAllSeats.gone()
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    override fun onSeatSelection(
        selectedSeatDetails: ArrayList<SeatDetail>,
        finalSeatNumber: ArrayList<String?>,
        totalSum: Double,
        isAllSeatSelected: Boolean,
        isSeatLongPress: Boolean?
    ) {
    }


    override fun unSelectAllSeats() {
    }

    override fun unblockSeat(
        seatNumber: String,
        selectionType: String,
        fromDate: String?,
        toDate: String?,
        remarks: String?
    ) {
    }

    override fun editSeatFare(seatNumber: String, newFare: String) {

        if(country.equals("india",true)){
            val arr = seatNumber.split(",")
            fareDetailPerSeatList.clear()
            if(seatNumber.isNotEmpty()){
                binding.newFareG.visible()
                for (i in 0 until arr.size){
                    val fareDetailPerSeatData = FareDetailPerSeat("","")
                    fareDetailPerSeatData.fare = ""
                    fareDetailPerSeatData.seatNumber = arr[i].trim()
                    fareDetailPerSeatList.add(fareDetailPerSeatData)
                }
            }else{
                binding.newFareG.gone()

            }
        }else{
            fareDetailPerSeatList.clear()
            val fareDetailPerSeatData = FareDetailPerSeat(
                newFare,
                seatNumber,
            )
            fareDetailPerSeatList.add(fareDetailPerSeatData)

            if(fareDetailPerSeatList.size > 0 && binding.etToDateUrc.text.toString().isNotEmpty()){
                binding.confirmPerSeatFareChangeBtn.isEnabled = true
                binding.confirmPerSeatFareChangeBtn.setBackgroundResource(R.drawable.button_selected_bg)
            }
        }








    }

    override fun bookExtraSeats(isChecked: Boolean?, isSeatSelected: Boolean?) {
    }

    override fun moveExtraSeat(isChecked: Boolean) {

    }

    override fun releaseTicket(ticketNumber: String, ReleaseTicket: String) {

    }

    override fun callPassenger(ticketNumber: String, contactNumber: String) {
    }

    override fun checkBoardedStatus(
        status: Boolean,
        passengerName: String,
        pnrNum: String,
        seatNumber: String,
        view: View
    ) {

    }

    override fun selectedSeatCount(selectedSeats: ArrayList<SeatDetail>) {

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
}