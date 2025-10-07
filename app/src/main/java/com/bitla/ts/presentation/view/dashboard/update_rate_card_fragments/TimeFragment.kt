package com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.util.*
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.route_manager.*
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.request.ReqBody
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.*
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_time.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.dialog.DialogUtils.Companion.dismissProgressDialog
import com.bitla.ts.utils.sharedPref.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*


class TimeFragment(var isMultiHopService: Boolean) : Fragment(), DialogButtonListener, DialogSingleButtonListener,
    DialogButtonAnyDataListener {

    private var privileges: PrivilegeResponseModel? = null
    private var selectedCityId: String = ""
    private lateinit var binding: FragmentTimeBinding
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
    private var fromDateDDMMYYYY: String? = null
    private var farePercentage: Int = 0
    private var incOrDec: Int = 0
    private var hh: String? = null
    private var mm: String? = null
    private var time: String? = null
    private var multistationFareResponse: MultiStationWiseFareResponse? = null
    private var hubAdapter: SourceDestinatinAdapter? = null

    private var sourceId = ""
    private var source: String = ""
    private var destinationId: String? = null
    private var destination: String = ""
    private var busType: String? = null
    private var convertedDate: String? = null
    private var routeId: String? = null
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var locale: String? = ""
    private var isChkArrival: Boolean = false
    private var isChkDeparture: Boolean = false
    private var isChkBP: Boolean = false
    private var isChkDP: Boolean = false
    private lateinit var applyForList: ApplyFor
    private var pinSize = 0
    private var modifyReservation = false
    private var currentCountry: String = ""
    private var sourcePopupWindow: PopupWindow? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            init()
        }
    }

    private fun setFromCity() {

        val cityList = multistationFareResponse?.filterOption?.fromToCityTime

        val obj = FromToCityTime(getString(R.string.select_city_),"")
        cityList?.add(0,obj)

        val uniqueCityFrom = cityList?.distinctBy { it.city }




        cityFromAdapter(uniqueCityFrom)

        binding.imgCrossFrom.setOnClickListener {
            binding.fromCityET.setText("")
            binding.imgCrossFrom.gone()
        }
    }

    private fun cityFromAdapter(cityList: List<FromToCityTime>?) {
        var popupBinding: AdapterSearchBpdpBinding? = null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(requireContext()))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val list: ArrayList<CitiesListData> = arrayListOf()
        for (i in 0 until cityList!!.size) {
            val obj = CitiesListData()
            obj.id = cityList?.get(i)?.id ?: ""
            obj.name = cityList?.get(i)?.city ?: ""
            if (obj.name.isNotBlank()) {
                list.add(obj)
            }
        }

        hubAdapter = SourceDestinatinAdapter(requireContext(), list, this, HUB)
        popupBinding.searchRV.adapter = hubAdapter


        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                hubAdapter?.filter?.filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = (screenHeight * 0.3).toInt()

        if (cityList.size <= 10) {
            popupHeight = FrameLayout.LayoutParams.WRAP_CONTENT
        }


        sourcePopupWindow = PopupWindow(
            popupBinding.root, binding.fromCityET.width, popupHeight,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(binding.fromCityET)

        sourcePopupWindow?.elevation = 25f


        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun init() {
        privileges = (activity as BaseActivity).getPrivilegeBase()

        getPref()
        onClick()
        setMultiStationWiseFareObserver()
        callMultiStationWiseFairApi("")
        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)
        fromDateDDMMYYYY = getTodayDate()
        val parser = SimpleDateFormat("dd-MM-yyyy")
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        fromDate = formatter.format(parser.parse(fromDateDDMMYYYY))
        binding.etFromDateUrc.setText(fromDateDDMMYYYY)

        binding.fromCityET.setText(getString(R.string.select_city))


        binding.incDecFare.setOnClickListener {
            incOrDec = 0
            farePercentage = incOrDec
            if(privileges?.allowToDoFareCustomizationForSeatTypes == true){
                binding.fromCityTIL.visible()
            }else{
                binding.fromCityTIL.gone()
            }
        }

        binding.fromCityET.setOnClickListener {
            setFromCity()

        }

        binding.decFare.setOnClickListener {
            incOrDec = 1
            farePercentage = incOrDec
            binding.fromCityTIL.gone()
            selectedCityId = ""

        }
        binding.etHour.setAdapter(
            ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                requireContext().resources.getStringArray(R.array.hourArray)
            )
        )



        binding.etMinute.setAdapter(
            ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                requireContext().resources.getStringArray(R.array.minuteArray)
            )
        )

        binding.etHour.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {

//                    binding.etHour.filters = arrayOf<InputFilter>(InputFilterMinMax("1", "24"))

                    if (!binding.etToDateUrc.text.isNullOrEmpty() && !binding.etMinute.text.isNullOrEmpty() && (!binding.incDecFare.isSelected && selectedCityId != "")) {
                        binding.btnModifyTime.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                        }
                    }
                } else {
                    binding.btnModifyTime.apply {
                        setBackgroundResource(R.drawable.button_default_bg)
                        isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })


        binding.etMinute.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {

//                    binding.etMinute.filters = arrayOf<InputFilter>(InputFilterMinMax("1", "59"))

                    if (!binding.etToDateUrc.text.isNullOrEmpty() && !binding.etHour.text.isNullOrEmpty()) {
                        binding.btnModifyTime.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                        }
                    }
                } else {
                    binding.btnModifyTime.apply {
                        setBackgroundResource(R.drawable.button_default_bg)
                        isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        if (requireContext().isNetworkAvailable())
            callServiceApi()
        else requireContext().noNetworkToast()

        if (!privileges?.country.isNullOrEmpty()) {
            if (privileges?.country.equals("india", true)) {
                selectedSeatTypeImp()
                binding.tvSeatType.visible()
                binding.seatTypeContainer.visible()
                binding.seatTypeContainer2.visible()
            } else {
                binding.tvSeatType.gone()
                binding.seatTypeContainer.gone()
                binding.seatTypeContainer2.gone()
            }
        }

        if(incOrDec == 0 && privileges?.allowToDoFareCustomizationForSeatTypes == true){
            binding.fromCityTIL.visible()
        }else{
            binding.fromCityTIL.gone()
        }

        updateRateCardTimeObserver()

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


    private fun callMultiStationWiseFairApi(selectedChannelId: String) {

        if (requireContext().isNetworkAvailable()) {
            pickUpChartViewModel.fetchMultiStatioWiseFareApi(
                ReqBody(
                    apiKey = loginModelPref.api_key,
                    reservation_id = resID.toString(),
                    date = convertedDate.toString(),
                    channelId = selectedChannelId,
                    templateId = "",
                    locale = locale
                ),
                manage_fare_method_name
            )
        } else requireContext().noNetworkToast()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setMultiStationWiseFareObserver() {

        pickUpChartViewModel.fetchMultiStatioWiseFareResponse.observe(viewLifecycleOwner) {

            dismissProgressDialog()


            if (it != null) {
                when (it.code) {
                    200 -> {
                        multistationFareResponse = it
                    }
                    401 -> {
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

    private fun selectedSeatTypeImp() {
        binding.apply {
            chkArrival.isChecked = true
            chkBP.isChecked = true
            chkDP.isChecked = true
            chkDeparture.isChecked = true
        }

        applyForList = ApplyFor(
            arrival = true,
            bp = true,
            departure = true,
            dp = true
        )

        binding.chkAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.apply {
                    chkArrival.isChecked = true
                    chkBP.isChecked = true
                    chkDP.isChecked = true
                    chkDeparture.isChecked = true
                }
                isChkArrival = true
                isChkBP = true
                isChkDP = true
                isChkDeparture = true
            } else {
                binding.apply {
                    if (chkDP.isChecked
                        && chkBP.isChecked
                        && chkDeparture.isChecked
                        && chkArrival.isChecked
                    ) {
                        binding.apply {
                            chkArrival.isChecked = false
                            chkBP.isChecked = false
                            chkDP.isChecked = false
                            chkDeparture.isChecked = false
                        }
                    }
                }
                isChkArrival = false
                isChkBP = false
                isChkDP = false
                isChkDeparture = false
            }
        }

        binding.chkArrival.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (binding.chkDP.isChecked
                    && binding.chkBP.isChecked
                    && binding.chkDeparture.isChecked
                ) {
                    binding.apply {
                        chkAll.isChecked = true
                    }
                }
                isChkArrival = true
            } else {
                binding.chkAll.isChecked = false
                isChkArrival = false
            }
        }

        binding.chkDeparture.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (binding.chkDP.isChecked
                    && binding.chkBP.isChecked
                    && binding.chkArrival.isChecked
                ) {
                    binding.apply {
                        chkAll.isChecked = true
                    }
                }
                isChkDeparture = true
            } else {
                binding.chkAll.isChecked = false
                isChkDeparture = false
            }
        }

        binding.chkBP.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (binding.chkDP.isChecked
                    && binding.chkDeparture.isChecked
                    && binding.chkArrival.isChecked
                ) {
                    binding.apply {
                        chkAll.isChecked = true
                    }
                }
                isChkBP = true
            } else {
                binding.chkAll.isChecked = false
                isChkBP = false
            }
        }

        binding.chkDP.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                applyForList = ApplyFor(
                    arrival = false,
                    bp = false,
                    departure = false,
                    dp = true
                )

                if (binding.chkBP.isChecked
                    && binding.chkDeparture.isChecked
                    && binding.chkArrival.isChecked
                ) {
                    binding.apply {
                        chkAll.isChecked = true
                    }
                }
                isChkDP = true

            } else {
                applyForList = ApplyFor(
                    arrival = false,
                    bp = false,
                    departure = false,
                    dp = false
                )
                binding.chkAll.isChecked = false
                isChkDP = false
            }
        }
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()



        resID = PreferenceUtils.getString(getString(R.string.updateRateCard_resId))
        source = PreferenceUtils.getString(getString(R.string.updateRateCard_origin)).toString()
        destination =
            PreferenceUtils.getString(getString(R.string.updateRateCard_destination)).toString()
        sourceId = PreferenceUtils.getString(getString(R.string.updateRateCard_originId)).toString()
        destinationId =
            PreferenceUtils.getString(getString(R.string.updateRateCard_destinationId)).toString()
//        routeId= PreferenceUtils.getString(getString(R.string.routeId)).toString()
        privileges?.let { privilegeResponseModel ->
            pinSize = privilegeResponseModel.pinCount ?: 6
            modifyReservation = privilegeResponseModel.pinBasedActionPrivileges?.modifyReservation ?: false
            currentCountry = privilegeResponseModel.country ?: ""
        }

    }

    private fun onClick() {
        binding.btnModifyTime.setOnClickListener {

            if (!privileges?.country.isNullOrEmpty()) {
                if (privileges?.country.equals("india", true)) {
                    if (binding.chkArrival.isChecked) {
                        isChkArrival = true
                    }
                    if (binding.chkBP.isChecked) {
                        isChkBP = true
                    }
                    if (binding.chkDeparture.isChecked) {
                        isChkDeparture = true
                    }
                    if (binding.chkDP.isChecked) {
                        isChkDP = true
                    }

                    applyForList = ApplyFor(
                        arrival = isChkArrival,
                        bp = isChkBP,
                        departure = isChkDeparture,
                        dp = isChkDP
                    )

//                    requireContext().toast("${applyForList}")
                }
            }

            source = PreferenceUtils.getString(getString(R.string.updateRateCard_origin)).toString()
            destination =
                PreferenceUtils.getString(getString(R.string.updateRateCard_destination)).toString()
            sourceId =
                PreferenceUtils.getString(getString(R.string.updateRateCard_originId)).toString()
            destinationId =
                PreferenceUtils.getString(getString(R.string.updateRateCard_destinationId))
                    .toString()
            convertedDate =
                PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate)).toString()
            busType = PreferenceUtils.getString(getString(R.string.updateRateCard_busType))

            PreferenceUtils.setPreference(PREF_SOURCE, source)
            PreferenceUtils.setPreference(PREF_DESTINATION, destination)
            PreferenceUtils.setPreference(PREF_TRAVEL_DATE, getDateDMY(convertedDate!!)!!)
            PreferenceUtils.setPreference(PREF_LAST_SEARCHED_SOURCE, source)
            PreferenceUtils.setPreference(PREF_LAST_SEARCHED_DESTINATION, destination)

            val fromDateDialog = binding.etFromDateUrc.text.toString()
            val toDateDialog = binding.etToDateUrc.text.toString()
            hh = binding.etHour.text.toString()
            mm = binding.etMinute.text.toString()
            time = "$hh:$mm"

            var increaseOrDecreaseByLabel = ""

            increaseOrDecreaseByLabel = if (incOrDec == 0) {
                requireContext().getString(R.string.increase_by)
            } else {
                requireContext().getString(R.string.decrease_by)
            }
            if (hh == "00" && mm == "00") {
                requireContext().toast("00 time & hour not allowed")
            } else {
                if (hh.isNullOrEmpty() ||
                    mm.isNullOrEmpty() ||
                    fromDate.isNullOrEmpty() ||
                    toDate.isNullOrEmpty()
                ) {
                    requireContext().toast(requireContext().getString(R.string.please_enter_all_the_details))
                }

                else {
                    DialogUtils.UpdateRcDialoge(
                        context = requireContext(),
                        title = requireContext().getString(R.string.update_rate_card_question),
                        message = "",
                        increaseby = time.toString(),
                        increaseOrDecreaseByLabel = increaseOrDecreaseByLabel,
                        fromDate = fromDateDialog,
                        toDate = toDateDialog,
                        srcDest = "$source-$destination",
                        journeyDate = busType.toString(),
                        buttonLeftText = requireActivity().getString(R.string.goBack),
                        buttonRightText = requireActivity().getString(R.string.confirm),
                        dialogButtonListener = this
                    )
                }
            }
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
        setDateLocale(locale!!, requireContext())
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

                    if (!binding.etHour.text.isNullOrEmpty() && !binding.etMinute.text.isNullOrEmpty()) {
                        binding.btnModifyTime.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = false
                            binding.btnModifyTime.isEnabled = true
                        }
                    }
                }
            setDateLocale(locale!!, requireContext())
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


    private fun callServiceApi() {

        sharedViewModel.getServiceDetails(
            reservationId = resID.toString(),
            apiKey = loginModelPref.api_key,
            originId = sourceId,
            destinationId = destinationId.toString(),
            operatorApiKey = operator_api_key,
            locale = locale!!,
            apiType = service_details_method,
            excludePassengerDetails = false
        )

        serviceDetailsApiObserver()
    }

    private fun serviceDetailsApiObserver() {
        sharedViewModel.serviceDetails.observe(viewLifecycleOwner) {
            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            routeId = it.body.routeId.toString()
                        }

                        401 -> {
                            /* DialogUtils.unAuthorizedDialog(
                                 requireContext(),
                                 "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                 this
                             )*/
                            (activity as BaseActivity).showUnauthorisedDialog()

                        }

                        else -> it.message?.let { it1 ->
                            Toast.makeText(
                                requireContext(),
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            } catch (e: Exception) {
                requireContext().toast(e.message.toString())
            }
        }
    }

    private fun updateRateCardTimeObserver() {

        pickUpChartViewModel.updateRateCardTimeResponse.observe(viewLifecycleOwner) {
            Timber.d("reservationbTime${it}")
            DialogUtils.dismissProgressDialog()

            if (it != null) {
                when (it.code) {
                    200 -> {

                        if (isAttachedToActivity()) {
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
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun callUpdateRateCardTimeApi(authKey: String) {
        pickUpChartViewModel.updateRateCardTimeApi(
            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_time.request.ReqBody(
                apiKey = loginModelPref.api_key,
                id = resID.toString(),
                fromDate = fromDate.toString(),
                toDate = toDate.toString(),
                category = "time",
                incOrDec = incOrDec.toString(),
                routeId = routeId.toString(),
                time = time.toString(),
                locale = locale,
                applyFor = applyForList,
                authKey = authKey,
                cityId = selectedCityId
            ),
            manage_fare_method_name
        )
    }

    override fun onLeftButtonClick() {

    }

    override fun onRightButtonClick() {
        DialogUtils.showProgressDialog(requireContext())

        if (requireContext().isNetworkAvailable()) {
            if (modifyReservation && currentCountry.equals("india", true)) {
                DialogUtils.showFullHeightPinInputBottomSheet(
                    activity = requireActivity(),
                    fragmentManager = childFragmentManager,
                    pinSize = pinSize,
                    getString(R.string.rate_card_time).capitalize(),
                    onPinSubmitted = { pin: String ->
                        callUpdateRateCardTimeApi(pin)
                        DialogUtils.dismissProgressDialog()
                    },
                    onDismiss = {
                        DialogUtils.dismissProgressDialog()
                    }
                )
            } else {
                callUpdateRateCardTimeApi("")
                DialogUtils.dismissProgressDialog()
            }
        } else requireContext().noNetworkToast()

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

    override fun onDataSend(type: Int, file: Any) {
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {
        when (type) {
            1 -> {
                val selectedData = file as CitiesListData
                when (extra as Int) {


                    HUB -> {
                        binding.fromCityET.setText(selectedData.name)
                        selectedCityId = selectedData.id
                        sourcePopupWindow?.dismiss()
                    }


                }
            }
        }
    }
}