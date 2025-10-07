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
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.service_details.request.*
import com.bitla.ts.domain.pojo.service_details.request.ReqBody
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.*
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
import java.text.*
import java.util.*

class FareFragment : Fragment(), DialogButtonListener, DialogSingleButtonListener {

    private var currencySymbol: String = ""
    private lateinit var binding: FragmentFareBinding

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
    private var farePercentage: String? = null
    private var incOrDec: Int = 0
    private var amountType: Int = 1
    private var amountTypeText: String = ""

    private var sourceId: String? = null
    private var source: String = ""
    private var destinationId: String? = null
    private var destination: String = ""
    private var busType: String? = null
    private var convertedDate: String? = null
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var routeId: String? = null
    private var locale: String? = ""
    private var shouldModifyReservation = false
    private var pinSize = 0
    private var currentCountry: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFareBinding.inflate(inflater, container, false)

        init()
        return binding.root
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun init() {
        getPref()
        onClick()
        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)
        fromDateDDMMYYYY = getTodayDate()
        val parser = SimpleDateFormat("dd-MM-yyyy")
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        fromDate = formatter.format(parser.parse(fromDateDDMMYYYY))
        binding.etFromDateUrc.setText(fromDateDDMMYYYY)


        binding.textView4.text = "${requireActivity().getString(R.string.increase)} (%)"
        binding.layoutAddpercentage.hint =
            "${requireActivity().getString(R.string.add_lowercase)} (%)"

        binding.percentageRadio.setOnClickListener {
            binding.etAddPercentage.setText("")
            binding.etAddPercentage.setMaxLength(2)
            amountType = 1
            amountTypeText = "(%)"

            if (incOrDec == 0) {
                binding.textView4.text =
                    "${requireActivity().getString(R.string.increase)} $amountTypeText"
                binding.layoutAddpercentage.hint =
                    "${requireActivity().getString(R.string.add_lowercase)} $amountTypeText"
            } else {
                binding.textView4.text =
                    "${requireActivity().getString(R.string.decrease)} $amountTypeText"
                binding.layoutAddpercentage.hint =
                    "${requireActivity().getString(R.string.add_lowercase)} $amountTypeText"
            }

            binding.etAddPercentage.setMaxLength(2)
        }

        binding.fixedRadio.setOnClickListener {
            binding.etAddPercentage.setText("")
            amountType = 2
            amountTypeText = currencySymbol

            if (incOrDec == 0) {
                binding.textView4.text =
                    "${requireActivity().getString(R.string.increase)} $amountTypeText"
                binding.layoutAddpercentage.hint =
                    "${requireActivity().getString(R.string.add_lowercase)} $amountTypeText"
            } else {
                binding.textView4.text =
                    "${requireActivity().getString(R.string.decrease)} $amountTypeText"
                binding.layoutAddpercentage.hint =
                    "${requireActivity().getString(R.string.add_lowercase)} $amountTypeText"
            }

            maxDigitPreventAfterDecimal(binding.etAddPercentage)
        }

        binding.incDecFare.setOnClickListener {
            incOrDec = 0
            binding.textView4.text =
                "${requireActivity().getString(R.string.increase)} $amountTypeText"
        }

        binding.decFare.setOnClickListener {
            incOrDec = 1
            binding.textView4.text =
                "${requireActivity().getString(R.string.decrease)} $amountTypeText"
        }

        binding.etAddPercentage.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    if (!binding.etToDateUrc.text.isNullOrEmpty()) {
                        binding.btnModifyService.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                        }
                    }
                } else {
                    binding.btnModifyService.apply {
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

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()

        (activity as BaseActivity).getPrivilegeBase()?.let { privilegeResponseModel ->
            pinSize = privilegeResponseModel.pinCount ?: 6
            shouldModifyReservation = privilegeResponseModel.pinBasedActionPrivileges?.modifyReservation ?: false
            currentCountry = privilegeResponseModel.country ?: ""
        }

        resID = PreferenceUtils.getString(getString(R.string.updateRateCard_resId))

        source = PreferenceUtils.getString(getString(R.string.updateRateCard_origin)).toString()
        destination =
            PreferenceUtils.getString(getString(R.string.updateRateCard_destination)).toString()
        sourceId = PreferenceUtils.getString(getString(R.string.updateRateCard_originId)).toString()
        destinationId =
            PreferenceUtils.getString(getString(R.string.updateRateCard_destinationId)).toString()
//            convertedDate = PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate)).toString()
//            Timber.d("gayrav: date : ${convertedDate}")
        busType = PreferenceUtils.getString(getString(R.string.updateRateCard_busType))
//
        PreferenceUtils.setPreference(PREF_SOURCE, source)
        PreferenceUtils.setPreference(PREF_DESTINATION, destination)
//            PreferenceUtils.setPreference(PREF_TRAVEL_DATE, getDateDMY(convertedDate!!)!!)
        PreferenceUtils.setPreference(PREF_LAST_SEARCHED_SOURCE, source)
        PreferenceUtils.setPreference(PREF_LAST_SEARCHED_DESTINATION, destination)

    }

    private fun onClick() {
        binding.btnModifyService.setOnClickListener {
            val fromDateDialog = binding.etFromDateUrc.text.toString()
            val toDateDialog = binding.etToDateUrc.text.toString()
            farePercentage = binding.etAddPercentage.text.toString()

            var amountTypeFixed = ""
            var amountTypePercentage = ""
            var increaseOrDecreaseByLabel = ""

            if (amountType == 1) {
                amountTypeFixed = ""
                amountTypePercentage = "%"
                binding.etAddPercentage.setLines(1)
            } else {
                amountTypeFixed = currencySymbol
                amountTypePercentage = ""
            }

            increaseOrDecreaseByLabel = if (incOrDec == 0) {
                requireContext().getString(R.string.increase_by)
            } else {
                requireContext().getString(R.string.decrease_by)
            }


            if (farePercentage.isNullOrEmpty() ||
                fromDate.isNullOrEmpty() ||
                toDate.isNullOrEmpty()
            ) {
                requireContext().toast(requireContext().getString(R.string.please_enter_all_the_details))
            } else {
                DialogUtils.UpdateRcDialoge(
                    context = requireContext(),
                    title = requireContext().getString(R.string.update_rate_card_question),
                    message = "",
                    increaseby = "$amountTypeFixed$farePercentage$amountTypePercentage",
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

        if ((activity as BaseActivity).getPrivilegeBase() != null) {
            val privilegeResponse = (activity as BaseActivity).getPrivilegeBase()

            privilegeResponse?.let {
                if (privilegeResponse.currency.isNotEmpty()) {
                    currencySymbol = privilegeResponse.currency
                }
            }
        } else {
            requireActivity().toast(requireActivity().getString(R.string.server_error))
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
        calendar.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale("es","chile"))
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
            Toast.makeText(
                context,
                requireActivity().getString(R.string.validate_from_date),
                Toast.LENGTH_SHORT
            ).show()
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

                    if (!binding.etAddPercentage.text.isNullOrEmpty()) {
                        binding.btnModifyService.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                            binding.btnModifyService.isEnabled = true
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

    private fun updateRateCardFareObserver() {
        pickUpChartViewModel.updateRateCardFareResponse.observe(viewLifecycleOwner) {

            Timber.d("reservationbFare ${it}")

            if (it != null) {
                when (it.code) {
                    200 -> {

                        if (isAttachedToActivity()) {
                            it.result?.message?.let { it1 ->
                                DialogUtils.successfulMsgDialog(
                                    requireContext(), it1
                                )
                            }
                        }

                        convertedDate =
                            PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate))
                                .toString()
                        PreferenceUtils.setPreference(
                            PREF_TRAVEL_DATE,
                            getDateDMY(convertedDate!!)!!
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            activity?.finish()
                        }, 2000)

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

    private fun callUpdateRateCardFareApi(authKey: String) {
        pickUpChartViewModel.updateRateCardFareApi(
            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBody(
                apiKey = loginModelPref.api_key,
                id = resID.toString(),
                routeId = routeId.toString(),
                type = amountType.toString(),
                incOrDec = incOrDec.toString(),
                category = "fare",
                comment = "n/a",
                fare = farePercentage.toString(),
                fromDate = fromDate.toString(),
                toDate = toDate.toString(),
                locale = locale,
                authKey = authKey
            ),
            manage_fare_method_name
        )
    }

    private fun callServiceApi() {

        sharedViewModel.getServiceDetails(
            reservationId = resID.toString(),
            apiKey = loginModelPref.api_key,
            originId = sourceId.toString(),
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
            if (it != null) {
                when (it.code) {
                    200 -> {
                        routeId = it.body.routeId.toString()
                        PreferenceUtils.putString(getString(R.string.routeId), routeId)
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
                            requireContext(),
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else
                requireActivity().toast(getString(R.string.server_error))
        }
    }

    override fun onLeftButtonClick() {

    }

    override fun onRightButtonClick() {
        if (requireContext().isNetworkAvailable()) {
            if (shouldModifyReservation && currentCountry.equals("india", true)) {
                DialogUtils.showFullHeightPinInputBottomSheet(
                    activity = requireActivity(),
                    fragmentManager = childFragmentManager,
                    pinSize = pinSize,
                    getString(R.string.rate_card_fare).capitalize(),
                    onPinSubmitted = { pin: String ->
                        callUpdateRateCardFareApi(pin)
                    },
                    onDismiss = null
                )
            } else {
                callUpdateRateCardFareApi("")
            }
            updateRateCardFareObserver()
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
}
