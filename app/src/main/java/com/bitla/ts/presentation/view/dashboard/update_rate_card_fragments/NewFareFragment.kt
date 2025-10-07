package com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments

import SingleViewModel
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.DialogButtonListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.DialogCalendarBinding
import com.bitla.ts.databinding.FragmentModifyFareNewBinding
import com.bitla.ts.domain.pojo.SpinnerItemsModifyFare
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.update_rate_card.fetch_fare_template.response.FetchFareTemplateResponse
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.request.ReqBody
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultiStationWiseFareResponse
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultistationFareDetails
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ChannelType
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.CityWiseFare
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.CopyFare
import com.bitla.ts.presentation.adapter.MultipleHorizontalItemSelectionAdapter
import com.bitla.ts.presentation.adapter.SelectUserTypeModifyFareAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.ModifyIndividualRouteFareActivity
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.dialog.DialogUtils.Companion.dismissProgressDialog
import com.bitla.ts.utils.dialog.DialogUtils.Companion.showProgressDialog
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import me.drakeet.support.toast.ToastCompat
import noNetworkToast
import okhttp3.internal.toImmutableList
import org.koin.androidx.viewmodel.ext.android.viewModel
import setMaxLength
import timber.log.Timber
import toast
import visible
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class NewFareFragment : Fragment(), DialogButtonListener, DialogSingleButtonListener,
    OnItemClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var channelArray: Array<String>
    private var currencySymbol: String = ""
    private lateinit var binding: FragmentModifyFareNewBinding

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
    private var locale: String? = ""
    private var routeId: String? = null
    private var selectedOriginId = ""
    private var selectedDestinationId = ""

    private var cityFromList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var cityToList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var cityPairList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var cityFromListTemp: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var cityToListTemp: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var cityPairTemp: MutableList<SpinnerItemsModifyFare> = mutableListOf()

    private var multistationFareDetails = mutableListOf<MultistationFareDetails>()
    private lateinit var spinnerItemsCityPairCityFrom: SpinnerItemsModifyFare
    private lateinit var spinnerItemsCityPairCityTo: SpinnerItemsModifyFare
//    private lateinit var SpinnerItemsCityPairRoute: SpinnerItemsCityPair2
    private lateinit var spinnerCityPair: SpinnerItemsModifyFare
    private lateinit var spinnerItemsModifyFareFareTemplate: SpinnerItemsModifyFare
    private var seatList = mutableListOf<Service>()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var searchServiceAdapter: MultipleHorizontalItemSelectionAdapter
    private var seatId = ""
    private var selectedViaRoutePosition = 0
    private var multistationFareResponse: MultiStationWiseFareResponse? = null
    private lateinit var copyFare: CopyFare
    private lateinit var channelType: ChannelType
    private val singleViewModel by viewModel<SingleViewModel<Any?>>()
    private var allowBookingTypeFare: String = ""
    private var selectedTemplateId: String = ""
    private var templateList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var branchCityWiseFareList: MutableList<CityWiseFare> = mutableListOf()
    private var onlineCityWiseFareList: MutableList<CityWiseFare> = mutableListOf()
    private var otaCityWiseFareList: MutableList<CityWiseFare> = mutableListOf()
    private var eBookingCityWiseFareList: MutableList<CityWiseFare> = mutableListOf()
    private var tempSeatListTemp: MutableList<Service> = mutableListOf()   // for duplicate
    private var cityWiseFareList: MutableList<com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.request.CityWiseFare> = mutableListOf()

    //    private var manageFareApiDelay = 4000L
    private var copied1 = ""
    private var copied2 = ""
    private var copied3 = ""
    private var selectedCityPairOriginId: String = ""
    private var selectedCityPairDestinationId: String = ""
    private var selectedSeatList = mutableListOf<Service>()
    private var branchId: Int = 1
    private var onlineAgentId: Int = 2
    private var otasId: Int = 3
    private var eBookingId: Int = 4
    private var copyFareBranch: Boolean = false
    private var copyFareOnline: Boolean = false
    private var copyFareOta: Boolean = false
    private var copyFareEBooking: Boolean = false
    private var selectedFromCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var selectedToCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var selectedCityPairList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var updatedFromCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var updatedToCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var fetchFareTemplateResponse:FetchFareTemplateResponse? = null
    private var fetchFareTemplateDetails = mutableListOf<MultistationFareDetails>()
    private var isAllowToDoFareCustomizationForSeatTypes: Boolean = false
    private var selectedTemplateType: Int = 0
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var daysTempList:ArrayList<EventDay>?= arrayListOf()
    private var dayList: ArrayList<EventDay>? = arrayListOf()
    private var selectedMultipleDate = ""
    private lateinit var copyFareTemplate: CopyFare
    private var configuredAmountType: String? = null
    private var pinSize = 0
    private var shouldModifyReservation = false
    private var currentCountry: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentModifyFareNewBinding.inflate(inflater, container, false)

        init()
        return binding.root
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun init() {
        showProgressDialog(requireContext())
        getPref()
        onClick()
        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)
        try {
            fromDateDDMMYYYY = convertDateYYYYMMDDtoDDMMYY(convertedDate.toString())
            val parser = SimpleDateFormat("dd-MM-yyyy")
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            fromDate = formatter.format(parser.parse(fromDateDDMMYYYY))
            toDate = formatter.format(parser.parse(fromDateDDMMYYYY))
            binding.etFromDateUrc.setText(fromDateDDMMYYYY)
            binding.etToDateUrc.setText(fromDateDDMMYYYY)
        }catch (e: Exception){
            requireActivity().toast(e.message)
        }

        binding.textView4.text = "${requireActivity().getString(R.string.increase)} (%)"
        binding.layoutAddpercentage.hint = "${requireActivity().getString(R.string.add_lowercase)} (%)"
        setManageFareObserver()
        setMultiStationWiseFareObserver()
        setFetchFareTemplateObserver()
        setCreateFareTemplateApiObserver()
        binding.customFareTemplateLayout.gone()

        binding.etFromDateUrc.setOnClickListener {
            openFromDateDialog()
        }
        binding.etToDateUrc.setOnClickListener {
            openToDateDialog()
        }
        binding.etMultipleDateUrc.setOnClickListener {
            openMultipleDateDialog()
        }

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

        resID = PreferenceUtils.getString(getString(R.string.updateRateCard_resId))

        source = PreferenceUtils.getString(getString(R.string.updateRateCard_origin)).toString()
        destination = PreferenceUtils.getString(getString(R.string.updateRateCard_destination)).toString()
        sourceId = PreferenceUtils.getString(getString(R.string.updateRateCard_originId)).toString()
        destinationId = PreferenceUtils.getString(getString(R.string.updateRateCard_destinationId)).toString()
        convertedDate = PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate)).toString()
//            Timber.d("gayrav: date : ${convertedDate}")
        busType = PreferenceUtils.getString(getString(R.string.updateRateCard_busType))
//
        PreferenceUtils.setPreference(PREF_SOURCE, source)
        PreferenceUtils.setPreference(PREF_DESTINATION, destination)
//            PreferenceUtils.setPreference(PREF_TRAVEL_DATE, getDateDMY(convertedDate!!)!!)
        PreferenceUtils.setPreference(PREF_LAST_SEARCHED_SOURCE, source)
        PreferenceUtils.setPreference(PREF_LAST_SEARCHED_DESTINATION, destination)

        if ((activity as BaseActivity).getPrivilegeBase() != null) {
            privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase()

            privilegeResponseModel?.let {
                pinSize = privilegeResponseModel?.pinCount ?: 6
                shouldModifyReservation = privilegeResponseModel?.pinBasedActionPrivileges?.modifyReservation ?: false
                currentCountry = privilegeResponseModel?.country ?: ""
                if (privilegeResponseModel?.allowToDoFareCustomizationForSeatTypes == true) {
                    binding.layoutCityFare.visible()
                    isAllowToDoFareCustomizationForSeatTypes = true
                } else {
                    binding.layoutCityFare.gone()
                    isAllowToDoFareCustomizationForSeatTypes = false
                }

                binding.apply {

                    privilegeResponseModel?.let {
                        if (privilegeResponseModel?.allowToUpdateFareTemplates == true) {
                            selectFareTemplateTVLayout.visible()
                            createFareTemplateLayout.visible()
                            setCustomFareTemplate()
                            setFareTemplate()
                        } else {
                            selectFareTemplateTVLayout.gone()
                            createFareTemplateLayout.gone()
                        }
                    }
                }
                
                configuredAmountType = it.configuredAmountType
            }
        }

        allowBookingTypeFare = PreferenceUtils.getString(getString(R.string.updateRateCard_allow_booking_type_fare)).toString()

        if (allowBookingTypeFare == "true") {
            binding.individualRouteCL.visible()
            binding.view1.visible()
            callMultiStationWiseFairApi("1")

            copyFare = CopyFare(
                branch = false,
                api = false,
                ebooking = false,
                online = false
            )
            singleViewModel.setChannelId(getString(R.string.branch))
            singleViewModel.setAmountTypeBranch(amountType)
            singleViewModel.setSelectedIncOrDecBranch(incOrDec)
            setChannels()
        } else {
            binding.individualRouteCL.gone()
            binding.view1.gone()
            callMultiStationWiseFairApi("")

            copyFare = CopyFare(
                branch = false,
                api = false,
                ebooking = false,
                online = false
            )
        }
    }
    
    private fun setPercentageRadio() {
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
        
        when (singleViewModel.selectedChannelIdLiveData.value) {
            getString(R.string.branch) -> {
                singleViewModel.setAmountTypeBranch(amountType)
            }
            getString(R.string.online_agent) -> {
                singleViewModel.setAmountTypeOnline(amountType)
            }
            getString(R.string.otas) -> {
                singleViewModel.setAmountTypeOta(amountType)
            }
            getString(R.string.e_booking) -> {
                singleViewModel.setAmountTypeEBooking(amountType)
            }
        }
        
        binding.percentageRadio.isChecked = true
        binding.fixedRadio.isChecked = false
    }
    
    private fun setFixedRadio() {
        binding.etAddPercentage.setText("")
        amountType = 2
        amountTypeText = "(${currencySymbol})"
        
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
        when (singleViewModel.selectedChannelIdLiveData.value) {
            getString(R.string.branch) -> {
                singleViewModel.setAmountTypeBranch(amountType)
            }
            getString(R.string.online_agent) -> {
                singleViewModel.setAmountTypeOnline(amountType)
            }
            getString(R.string.otas) -> {
                singleViewModel.setAmountTypeOta(amountType)
            }
            getString(R.string.e_booking) -> {
                singleViewModel.setAmountTypeEBooking(amountType)
            }
        }
        
        binding.percentageRadio.isChecked = false
        binding.fixedRadio.isChecked = true
    }
    @SuppressLint("SetTextI18n")
    private fun setAmountTypeNdIncDecOptions() {
        
        if (configuredAmountType.equals("fixed", true)) {
            setFixedRadio()
        } else {
            setPercentageRadio()
        }
        
        binding.percentageRadio.setOnClickListener {
            setPercentageRadio()
        }

        binding.fixedRadio.setOnClickListener {
            setFixedRadio()
        }

        binding.incDecFare.setOnClickListener {
            incOrDec = 0
            binding.textView4.text =
                "${requireActivity().getString(R.string.increase)} $amountTypeText"

            when (singleViewModel.selectedChannelIdLiveData.value) {
                getString(R.string.branch) -> {
                    singleViewModel.setSelectedIncOrDecBranch(incOrDec)
                }
                getString(R.string.online_agent) -> {
                    singleViewModel.setSelectedIncOrDecOnline(incOrDec)
                }
                getString(R.string.otas) -> {
                    singleViewModel.setSelectedIncOrDecOta(incOrDec)
                }
                getString(R.string.e_booking) -> {
                    singleViewModel.setSelectedIncOrDecEBooking(incOrDec)
                }
            }
        }

        binding.decFare.setOnClickListener {
            incOrDec = 1
            binding.textView4.text =
                "${requireActivity().getString(R.string.decrease)} $amountTypeText"

            when (singleViewModel.selectedChannelIdLiveData.value) {
                getString(R.string.branch) -> {
                    singleViewModel.setSelectedIncOrDecBranch(incOrDec)
                }
                getString(R.string.online_agent) -> {
                    singleViewModel.setSelectedIncOrDecOnline(incOrDec)
                }
                getString(R.string.otas) -> {
                    singleViewModel.setSelectedIncOrDecOta(incOrDec)

                }
                getString(R.string.e_booking) -> {
                    singleViewModel.setSelectedIncOrDecEBooking(incOrDec)
                }
            }
        }


        binding.etTemplateName.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (selectedTemplateType == 0){
                    if (s.isNotEmpty()) {
                        binding.btnSaveAsFareTemplate.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                            this.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }

                    } else {
                        binding.btnSaveAsFareTemplate.apply {
                            setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                            isEnabled = true
                            this.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimary
                                )
                            )
                        }
                    }
                } else {
                    if (s.isNotEmpty() && binding.etMultipleDateUrc.text.toString().isNotEmpty()) {
                        binding.btnSaveAsFareTemplate.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                            this.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                        }

                    } else {
                        binding.btnSaveAsFareTemplate.apply {
                            setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                            isEnabled = true
                            this.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimary
                                )
                            )
                        }
                    }
                }

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.etAddPercentage.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
//                    binding.modifyFareBT.apply {
//                        setBackgroundResource(R.drawable.button_selected_bg)
//                        isEnabled = true
//                        this.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//                    }
                    binding.btnApply.apply {
                        setBackgroundResource(R.drawable.button_selected_bg)
                        isEnabled = true
                        this.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }

                } else {
//                    binding.modifyFareBT.apply {
//                        setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
//                        isEnabled = false
//                    }
                    binding.btnApply.apply {
                        setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                        isEnabled = true
                        this.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorPrimary
                            )
                        )
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.includeSeatWiseChk.setOnCheckedChangeListener { buttonView, isChecked ->
            when (singleViewModel.selectedChannelIdLiveData.value) {
                getString(R.string.branch) -> {
                    singleViewModel.setIncludeSeatWiseCheckBranch(isChecked)
                }
                getString(R.string.online_agent) -> {
                    singleViewModel.setIncludeSeatWiseCheckOnline(isChecked)
                }
                getString(R.string.otas) -> {
                    singleViewModel.setIncludeSeatWiseCheckOta(isChecked)
                }
                getString(R.string.e_booking) -> {
                    singleViewModel.setIncludeSeatWiseCheckEBooking(isChecked)
                }
            }
        }

        if (requireContext().isNetworkAvailable())
            callServiceApi()
        else requireContext().noNetworkToast()
    }

    private fun setCustomFareTemplate() {
        binding.apply {

            if (selectedTemplateType == 0) {
                layoutMultipleDate.gone()
            }

            fareTemplateDateRangeRadio.setOnClickListener {
                layoutFromDateUrc.visible()
                layoutToDateUrc.visible()
                layoutMultipleDate.gone()
                selectedTemplateType = 0

                if (binding.etTemplateName.text.toString().isNotEmpty()) {
                    binding.btnSaveAsFareTemplate.apply {
                        setBackgroundResource(R.drawable.button_selected_bg)
                        isEnabled = true
                        this.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    }

                } else {
                    binding.btnSaveAsFareTemplate.apply {
                        setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                        isEnabled = true
                        this.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorPrimary
                            )
                        )
                    }
                }
            }

            fareTemplateMultipleDateRadio.setOnClickListener {
                layoutFromDateUrc.gone()
                layoutToDateUrc.gone()
                layoutMultipleDate.visible()
                selectedTemplateType = 1

                if (binding.etTemplateName.text.toString().isNotEmpty()
                    && binding.etMultipleDateUrc.text.toString().isNotEmpty()) {
                    binding.btnSaveAsFareTemplate.apply {
                        setBackgroundResource(R.drawable.button_selected_bg)
                        isEnabled = true
                        this.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    }

                } else {
                    binding.btnSaveAsFareTemplate.apply {
                        setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                        isEnabled = true
                        this.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorPrimary
                            )
                        )
                    }
                }
            }

            imgArrowCustomFareTemplate.setOnClickListener {
                if (customFareTemplateLayout.isVisible){
                    customFareTemplateLayout.gone()
                    imgArrowCustomFareTemplate.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    customFareTemplateLayout.visible()
                    imgArrowCustomFareTemplate.setImageResource(R.drawable.ic_arrow_up_24)
                }
            }

//            customFareTemplateLayout.setOnClickListener {
//                rgFareTemplateType2.visible()
//                fareTemplateET.gone()
//                selectedTemplateType = 1
//            }

//            if (binding.etTemplateName.text.toString().isNotEmpty()) {
//                btnSaveModifyService.apply {
//                    setBackgroundResource(R.drawable.button_selected_bg)
//                    isEnabled = true
//                    this.setTextColor(
//                        ContextCompat.getColor(
//                            requireContext(),
//                            R.color.white
//                        )
//                    )
//                }
//            }

            btnSaveAsFareTemplate.setOnClickListener {

                if (binding.etTemplateName.text.toString().isEmpty() &&
                    privilegeResponseModel?.allowUsersToSkipFareTemplateNameWhileCreating == false) {
                    requireActivity().toast("Fare template name cannot be blank")
                } else {
                    if (selectedTemplateType == 0) {
                        if (fromDate?.isNotEmpty() == true && toDate?.isNotEmpty() == true) {
                            val infoSaveTemplate = "Dates between $fromDate and $toDate Fare Template has been Created to ${busType?.substringBefore("|")}"
                            DialogUtils.twoButtonDialog(
                                context = requireContext(),
                                title = getString(R.string.create_fare_template),
                                message = infoSaveTemplate,
                                buttonLeftText = getString(R.string.cancel),
                                buttonRightText = getString(R.string.okay),
                                dialogButtonListener = this@NewFareFragment
                            )
                        } else {
                            requireActivity().toast(getString(R.string.validate_date))
                        }

                    } else {
                        if (binding.etMultipleDateUrc.text.toString().isEmpty()) {
                            requireActivity().toast(getString(R.string.validate_date))
                        } else {
                            val infoMultipleDatsSaveTemplate = "Dates for $selectedMultipleDate Fare Template has been Created to ${busType?.substringBefore("|")}"
                            DialogUtils.twoButtonDialog(
                                context = requireContext(),
                                title = getString(R.string.create_fare_template),
                                message = infoMultipleDatsSaveTemplate,
                                buttonLeftText = getString(R.string.cancel),
                                buttonRightText = getString(R.string.okay),
                                dialogButtonListener = this@NewFareFragment
                            )
                        }
                    }
                }
            }
        }
    }
    private fun setFareTemplate() {

        binding.apply {

            fareTemplateET.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    templateList
                )
            )

            fareTemplateET.setOnItemClickListener { parent, view, position, id ->
                selectedTemplateId = templateList[position].id.toString()
                showProgressDialog(requireContext())
                imgCross.visible()

                if (allowBookingTypeFare == "true") {
                    when (singleViewModel.selectedChannelIdLiveData.value) {
                        getString(R.string.branch) -> {
                            callFetchFareTemplateApi(selectedTemplateId)
                            singleViewModel.setTemplateValueBranch(templateList[position].value)
                        }
                        getString(R.string.online_agent) -> {
                            callFetchFareTemplateApi(selectedTemplateId)
                            singleViewModel.setTemplateValueOnline(binding.fareTemplateET.text.toString())
                        }
                        getString(R.string.otas) -> {
                            callFetchFareTemplateApi(selectedTemplateId)
                            singleViewModel.setTemplateValueOta(binding.fareTemplateET.text.toString())
                        }
                        getString(R.string.e_booking) -> {
                            callFetchFareTemplateApi(selectedTemplateId)
                            singleViewModel.setTemplateValueEBooking(binding.fareTemplateET.text.toString())
                        }
                    }
                } else {
                    callFetchFareTemplateApi(selectedTemplateId)
                    singleViewModel.setTemplateValueBranch(templateList[position].value)
                    binding.createFareTemplateLayout.visible()
                    clearCityPair()
                    binding.etAddPercentage.setText("")

                    binding.btnApply.apply {
                        setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorPrimary
                            )
                        )
                        setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                        isEnabled = true
                    }
                }
            }

            if (selectedTemplateId.isNotEmpty()) {
                btnSaveModifyService.apply {
                    setBackgroundResource(R.drawable.button_selected_bg)
                    isEnabled = true
                    this.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                }
            }

            imgCross.setOnClickListener {
                selectedTemplateId = ""
                fareTemplateET.setText("")
                refreshPage()
                if (allowBookingTypeFare == "true") {
                    when (singleViewModel.selectedChannelIdLiveData.value) {
                        getString(R.string.branch) -> {
                            callMultiStationWiseFairApi("1")
                            showProgressDialog(requireContext())
                        }

                        getString(R.string.online_agent) -> {
                            callMultiStationWiseFairApi("2")
                            showProgressDialog(requireContext())
                        }

                        getString(R.string.otas) -> {
                            callMultiStationWiseFairApi("4")
                            showProgressDialog(requireContext())
                        }

                        getString(R.string.e_booking) -> {
                            callMultiStationWiseFairApi("5")
                            showProgressDialog(requireContext())
                        }
                    }
                } else {
                    callMultiStationWiseFairApi("")
                    showProgressDialog(requireContext())
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun invalidateFromCityCount() {
        if (selectedFromCityList.size > 1) {
            binding.tvMoreUserTypeToCity.apply {
                visibility = View.VISIBLE
                text = "+ ${selectedFromCityList.size - 1} more"
            }
        } else {
            binding.tvMoreUserTypeToCity.visibility = View.GONE
        }
    }

    private fun invalidateToCityCount() {
        if (selectedToCityList.size > 1) {
            binding.tvMoreUserTypeFromCity.apply {
                visibility = View.VISIBLE
                text = "+ ${selectedToCityList.size - 1} more"
            }
        } else {
            binding.tvMoreUserTypeFromCity.visibility = View.GONE
        }
    }

    private fun invalidateCityPairCount() {
        if (selectedToCityList.size > 1) {
            binding.tvMoreUserTypeFromCity.apply {
                visibility = View.VISIBLE
                text = "+ ${selectedToCityList.size - 1} more"
            }
        } else {
            binding.tvMoreUserTypeFromCity.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fromCityTag(
        position: Int
    ) {
        selectedCityPairOriginId = if (selectedFromCityList.isNotEmpty()){
            this.cityFromList[position].id.toString()
        } else {
            ""
        }
        Timber.d("selectedCityPair_OriginId- $selectedCityPairOriginId")
    }

    @SuppressLint("SetTextI18n")
    private fun toCityTypeTag(position: Int) {

        selectedCityPairDestinationId = if (selectedToCityList.isNotEmpty()){
            this.cityToList[position].id
        } else {
            ""
        }

        Timber.d("selectedCityPair_DestinationId- $selectedCityPairDestinationId")
    }

    @SuppressLint("SetTextI18n")
    private fun cityPairTypeTag(position: Int) {

        if (selectedCityPairList.isNotEmpty()){
            selectedCityPairOriginId = this.cityPairList[position].id.substringBefore("-")
            selectedCityPairDestinationId = this.cityPairList[position].id.substringAfter("-")
        } else {
            selectedCityPairOriginId = ""
            selectedCityPairDestinationId = ""
        }

//        Timber.d("selectedCityPair_OriginId- $selectedCityPairOriginId")
//        Timber.d("selectedCityPair_DestinationId- $selectedCityPairDestinationId")
    }

    private fun setFilterSingaleSelection() {
     /****   binding.fromCityET.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_dropdown_item_witch_checkbox,
                R.id.tvItem,
                cityFromList
            )
        )

        binding.toCityET.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                cityToList
            )
        )

        binding.cityPairET.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                cityPairList
            )
        )

        binding.fromCityET.setOnItemClickListener { parent, view, position, id ->

            selectedOriginId = cityFromList[position].id.toString()
//            selectedDestinationId = cityPairList[position].id2.toString()
            selectedViaRoutePosition = position
            selectedCityPairDestinationId= cityFromList[position].id.toString()


            if (selectedOriginId.isNotEmpty()) {
                binding.imgCrossFrom.visible()
            }
        }

        binding.toCityET.setOnItemClickListener { parent, view, position, id ->
            selectedDestinationId = cityToList[position].id.toString()
            selectedViaRoutePosition = position
            selectedCityPairDestinationId = cityToList[position].id.toString()
            if (selectedDestinationId.isNotEmpty()) {
                binding.imgCrossTo.visible()
            }
        }

        binding.cityPairET.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(requireActivity(), ModifyIndividualRouteFareActivity::class.java)
            selectedCityPairOriginId = cityPairList[position].id.toString()
            selectedCityPairDestinationId = cityPairList[position].id2.toString()

            if (selectedCityPairOriginId.isNotEmpty()) {
                binding.imgCrossCityPair.visible()
            }

//            Timber.d("selectedCityPair - $selectedCityPairOriginId - $selectedCityPairDestinationId")

            intent.putExtra(
                getString(R.string.multistation_fare_response_model), jsonToString(
                    multistationFareResponseModel!!
                )
            )
            intent.putExtra(
                getString(R.string.multistation_amount_type),
                amountType.toString()
            )
            intent.putExtra(
                getString(R.string.multistation_incOrDec_fare),
                incOrDec.toString()
            )

            intent.putExtra(
                getString(R.string.updateRateCard_originId),
                selectedCityPairOriginId.toString()
            )
            intent.putExtra(
                getString(R.string.updateRateCard_destinationId),
                selectedCityPairDestinationId.toString()
            )
            startActivityForResult(intent, 2)
        } */
    }

    private fun setFilterOptions() {
        setFromCity()
        setToCity()
        setCityPair()
    }

    private fun setFromCity() {
        binding.fromCityET.onItemSelectedListener = this
        
        cityFromAdapter(cityFromList)
        
        binding.imgCrossFrom.setOnClickListener {
            selectedOriginId = ""
            binding.fromCityET.setText("")
            binding.imgCrossFrom.gone()
        }
    }

    private fun setToCity() {

        cityToCityAdapter(cityToList)

        binding.imgCrossTo.setOnClickListener {
            selectedDestinationId = ""
            binding.toCityET.setText("")
            binding.imgCrossTo.gone()
        }
    }

    private fun setCityPair() {

        cityPairAdapter(cityPairList)

        binding.imgCrossCityPair.setOnClickListener {
            selectedCityPairOriginId = ""
            selectedCityPairDestinationId = ""
            binding.cityPairET.setText("")
            binding.imgCrossCityPair.gone()
        }
    }

    private fun cityFromAdapter(fromCityList: MutableList<SpinnerItemsModifyFare>){
        binding.fromCityET.onItemSelectedListener = this

        binding.fromCityET.setAdapter(
            SelectUserTypeModifyFareAdapter(requireContext(),
                R.layout.spinner_dropdown_item_witch_checkbox,
                R.id.tvItem,
                fromCityList,
                selectedFromCityList,
                true,

                object : SelectUserTypeModifyFareAdapter.ItemClickListener {
                    override fun onSelected(position: Int, item: SpinnerItemsModifyFare) {
                        if (selectedFromCityList.contains(item).not()){
                            selectedFromCityList.add(item)
                        }

                        binding.fromCityET.setText(item.value)
                        invalidateFromCityCount()
                        fromCityTag(position)
                        
                        if(selectedToCityList.isNotEmpty()){
                            val list = getSelectedCityPairs(selectedFromCityList,selectedToCityList)
                            cityPairAdapter(list)
                        }else{
                            cityPairList.forEachIndexed { index, spinnerItems2 ->
//                            Timber.d("selectedList_from - ${spinnerItems2.id.substringBefore("-")} -- ${item.value}")
                                
                                if (spinnerItems2.id.substringBefore("-") == item.id) {
                                    spinnerCityPair = SpinnerItemsModifyFare(
                                        id = spinnerItems2.id,
                                        value = spinnerItems2.value,
                                    )
                                    updatedFromCityList.add(spinnerCityPair)
                                }
                            }
                            cityPairAdapter(updatedFromCityList)
                        }
                        singleViewModel.setSelectedOriginIdList(selectedFromCityList)
                    }
                    
                    override fun onDeselect(position: Int, item: SpinnerItemsModifyFare) {
                        if (selectedFromCityList.contains(item)){
                            selectedFromCityList.remove(item)
                        }
//
                        binding.cityPairET
                        binding.fromCityET.setText(
                            selectedFromCityList.firstOrNull().toString().replace("null", "")
                        )
                        if (selectedFromCityList.size == 0) {
                            binding.fromCityET.isFocusable = false
                        }
                        fromCityTag(position)
                        
                        if (selectedFromCityList.size == 0) {
                            selectedFromCityList.clear()
                            binding.fromCityET.setText("")
                            updatedFromCityList.clear()
                            clearUpdatedCityPair()
                            cityPairAdapter(cityPairList)
                            invalidateToCityCount()
                        } else {
                            binding.cityPairET.setText("")

                            if(selectedToCityList.isNotEmpty()){
                                val list = getSelectedCityPairs(selectedFromCityList,selectedToCityList)
                                filterList(list)
                                cityPairAdapter(list)
                            } else {
                                cityPairList.forEachIndexed { index, spinnerItems2 ->
                                    if (spinnerItems2.id.substringBefore("-") == item.id) {
                                        spinnerCityPair = SpinnerItemsModifyFare(
                                            id = spinnerItems2.id,
                                            value = spinnerItems2.value,
                                        )
                                        updatedFromCityList.remove(spinnerCityPair)
                                    }
                                }
                                filterList(updatedFromCityList)
                                cityPairAdapter(updatedFromCityList)
                            }
                        }
                        singleViewModel.setSelectedOriginIdList(selectedFromCityList)
                        invalidateFromCityCount()
                    }
                }
            )
        )
    }


    private fun filterList(list: MutableList<SpinnerItemsModifyFare>){
        if (!selectedCityPairList.isNullOrEmpty()){
            val tempList= selectedCityPairList
            val tempList2: MutableList<SpinnerItemsModifyFare> = mutableListOf()
            tempList.forEach {
                if (!list.contains(it)){
                    tempList2.add(it)
                }
            }

            tempList2.forEach {
                selectedCityPairList.remove(it)
            }
            binding.cityPairET.setText(
                selectedCityPairList.firstOrNull().toString().replace("null", "")
            )
//            selectedCityPairList= tempList2
        }
    }
    private fun cityToCityAdapter(toCityList: MutableList<SpinnerItemsModifyFare>){
        binding.toCityET.onItemSelectedListener = this
        
        binding.toCityET.setAdapter(
            SelectUserTypeModifyFareAdapter(requireContext(),
                R.layout.spinner_dropdown_item_witch_checkbox,
                R.id.tvItem,
                toCityList,
                selectedToCityList,
                true,
                object : SelectUserTypeModifyFareAdapter.ItemClickListener {
                    override fun onSelected(position: Int, item: SpinnerItemsModifyFare) {
                        
                        
                        if (selectedToCityList.contains(item).not()){
                            selectedToCityList.add(item)
                        }
                        
                        binding.toCityET.setText(item.value)
                        invalidateToCityCount()
                        toCityTypeTag(position)
                        
                        //for generating pairs in case of from city
                        if(binding.fromCityET.text.toString().equals(binding.toCityET.text.toString())){
                            val list : ArrayList<SpinnerItemsModifyFare> = arrayListOf()
                            cityPairAdapter(list)
                        }
                        else if(selectedFromCityList.isNotEmpty()){
                            val list = getSelectedCityPairs(selectedFromCityList,selectedToCityList)
                            cityPairAdapter(list)
                        }else{
                            cityPairList.forEachIndexed { index, spinnerItems2 ->
                                
                                if (spinnerItems2.id.substringAfter("-") == item.id) {
                                    spinnerCityPair = SpinnerItemsModifyFare(
                                        id = spinnerItems2.id,
                                        value = spinnerItems2.value,
                                    )
                                    updatedToCityList.add(spinnerCityPair)
                                }
                            }
                            cityPairAdapter(updatedToCityList)
                        }
                        
                        singleViewModel.setSelectedDestinationIdList(selectedToCityList)
                    }
                    
                    override fun onDeselect(position: Int, item: SpinnerItemsModifyFare) {
                        
                        if (selectedToCityList.contains(item)){
                            selectedToCityList.remove(item)
                        }
                        
                        binding.toCityET.setText(
                            selectedToCityList.firstOrNull().toString().replace("null", "")
                        )
                        
                        if (selectedToCityList.size == 0) {
                            binding.toCityET.isFocusable = false
                        }
                        
                        toCityTypeTag(position)
                        invalidateToCityCount()

//                        if (selectedToCityList.size == 0) {
//                            clearUpdatedCityPair()
//                            invalidateFromCityCount()
//                            cityPairAdapter(cityPairList)
//                        }
                        
                        if (selectedToCityList.size == 0) {
                            clearUpdatedCityPair()
                            invalidateFromCityCount()
                            
                            if(selectedFromCityList.isNotEmpty()) {
                                
                                updatedFromCityList.clear()
                                cityPairList.forEachIndexed { index, spinnerItems2 ->
                                    selectedFromCityList.forEach { fromItem ->
                                        if (spinnerItems2.id.substringBefore("-") == fromItem.id) {
                                            spinnerCityPair = SpinnerItemsModifyFare(
                                                id = spinnerItems2.id,
                                                value = spinnerItems2.value,
                                            )
                                            updatedFromCityList.add(spinnerCityPair)
                                        }
                                    }
                                    
                                }
                                cityPairAdapter(updatedFromCityList)
                            }
                            //cityPairAdapter(cityPairList)
                        }
                        else {
                            binding.cityPairET.setText("")

                            if (selectedFromCityList.isNotEmpty()){
                                val list = getSelectedCityPairs(selectedFromCityList,selectedToCityList)
                                filterList(list)
                                cityPairAdapter(list)
                            } else{
                                cityPairList.forEachIndexed { index, spinnerItems2 ->
                                    
                                    if (spinnerItems2.id.substringAfter("-") == item.id) {
                                        spinnerCityPair = SpinnerItemsModifyFare(
                                            id = spinnerItems2.id,
                                            value = spinnerItems2.value,
                                        )
                                        
                                        updatedToCityList.remove(spinnerCityPair)
                                    }
                                }
                                filterList(updatedToCityList)
                                cityPairAdapter(updatedToCityList)
                            }
                            
                        }
                        singleViewModel.setSelectedDestinationIdList(selectedToCityList)
                    }
                }
            )
        )
    }
    
    
    fun getSelectedCityPairs(fromCityList : MutableList<SpinnerItemsModifyFare>,toCityList: MutableList<SpinnerItemsModifyFare>): ArrayList<SpinnerItemsModifyFare> {
        
        val tempFromCityList : ArrayList<SpinnerItemsModifyFare> = arrayListOf()
        val tempToCityList : ArrayList<SpinnerItemsModifyFare> = arrayListOf()
        val finalPairCityList : ArrayList<SpinnerItemsModifyFare> = arrayListOf()
        
        for (i in 0 until cityPairList.size){
            for (j in 0 until fromCityList.size){
                if(cityPairList[i].id.substringBefore("-") == fromCityList[j].id){
                    tempFromCityList.add(cityPairList[i])
                }
            }
        }
        
        for (i in 0 until cityPairList.size){
            for (j in 0 until toCityList.size){
                if(cityPairList[i].id.substringAfter("-") == toCityList[j].id){
                    tempToCityList.add(cityPairList[i])
                }
            }
        }
        
        val commonValues =  tempFromCityList.intersect(tempToCityList)
        
        finalPairCityList.addAll(commonValues.toMutableList())
        
        return finalPairCityList
        
    }
    
    
    private fun cityPairAdapter(cityPairList: MutableList<SpinnerItemsModifyFare>){
        binding.cityPairET.onItemSelectedListener = this
        
        
        binding.cityPairET.setAdapter(
            SelectUserTypeModifyFareAdapter(requireContext(),
                resource = R.layout.spinner_dropdown_item_witch_checkbox,
                textViewResourceId = R.id.tvItem,
                objects = cityPairList,
                selectedUserTypeList = selectedCityPairList,
                isAllowMultipleQuota = true,
                
                onClickListener = object : SelectUserTypeModifyFareAdapter.ItemClickListener {
                    
                    override fun onSelected(position: Int, item: SpinnerItemsModifyFare) {
                        if (selectedCityPairList.contains(item).not())
                            selectedCityPairList.add(item)

                        
                        binding.cityPairET.setText(item.value)
                        invalidateCityPairCount()
                        singleViewModel.setSelectedCityPairIdList(selectedCityPairList)
                    }
                    
                    override fun onDeselect(position: Int, item: SpinnerItemsModifyFare) {
                        if (selectedCityPairList.contains(item))
                            selectedCityPairList.remove(item)


                        if (singleViewModel.selectedOriginIdList.value?.size==1 && singleViewModel.selectedDestinationIdList.value?.size==1) {
                            selectedCityPairList.clear()
                        }

                        binding.cityPairET.setText(
                            selectedCityPairList.firstOrNull().toString().replace("null", "")
                        )
                        
                        if (selectedCityPairList.size == 0) {
                            binding.cityPairET.isFocusable = false
                        }
                        invalidateCityPairCount()

//                        Timber.d("selectedCityPairList = ${selectedCityPairList} = ${singleViewModel.selectedOriginIdList.value?.size}")
                        singleViewModel.setSelectedCityPairIdList(selectedCityPairList)
                    }
                }
            )
        )
    }
    
    private fun clearCityPair() {
        cityFromList.clear()
        cityToList.clear()
        cityPairList.clear()
        selectedOriginId = ""
        selectedCityPairOriginId = ""
        selectedCityPairDestinationId = ""
        binding.fromCityET.setText("")
        binding.toCityET.setText("")
        binding.cityPairET.setText("")
        binding.imgCrossFrom.gone()
        binding.imgCrossTo.gone()
        binding.imgCrossCityPair.gone()
    }
    
    private fun clearUpdatedCityPair() {
        selectedCityPairOriginId = ""
        selectedCityPairDestinationId = ""
//        updatedFromCityList.clear()
        updatedToCityList.clear()
        selectedToCityList.clear()
//        selectedFromCityList.clear()
//        binding.fromCityET.setText("")
        binding.toCityET.setText("")
    }
    private fun setChannels() {
        channelArray = resources.getStringArray(R.array.channel_name)
        binding.selectChannelET.setText(channelArray[0])

        binding.selectChannelET.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                channelArray
            )
        )


        binding.selectChannelET.setOnItemClickListener { parent, view, position, id ->
            val selectedChannelName = parent.getItemAtPosition(position).toString()
            setChannelName(selectedChannelName)
        }
    }

    private fun setChannelName(selectedChannelName:String){
        if (allowBookingTypeFare == "true") {
            when (selectedChannelName) {
                getString(R.string.branch) -> {
                    binding.createFareTemplateLayout.visible()
                    if (branchCityWiseFareList.isNullOrEmpty()) {
                        callMultiStationWiseFairApi("1")
                        showProgressDialog(requireContext())

                        selectedTemplateId = ""
                        binding.fareTemplateET.setText("")
                    } else {
                        binding.fareTemplateET.setText(singleViewModel.selectedBranchTemplateValueLiveData.value)
                        setFareTemplate()
                    }
                    singleViewModel.setChannelId(getString(R.string.branch))
                    singleViewModel.setSelectedIncOrDecBranch(incOrDec)
                    singleViewModel.setAmountTypeBranch(amountType)
                    setBranchChannelState()
                    clearCityPair()

                    if (binding.etAddPercentage.text.toString().isEmpty()) {
                        binding.btnApply.apply {
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimary
                                )
                            )
                            setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                            isEnabled = true
                        }
                    } else {
                        binding.btnApply.apply {
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            setBackgroundResource(R.drawable.layout_rounded_shape_left_selected)
                            isEnabled = true
                        }
                    }
                }

                getString(R.string.online_agent) -> {
                    binding.createFareTemplateLayout.gone()
                    if (onlineCityWiseFareList.isNullOrEmpty()) {
                        callMultiStationWiseFairApi("2")
                        showProgressDialog(requireContext())
                        selectedTemplateId = ""
                        binding.fareTemplateET.setText("")
                    }
                    else {
                        binding.fareTemplateET.setText(singleViewModel.selectedOnlineTemplateValueLiveData.value)
                        setFareTemplate()
                    }
                    singleViewModel.setChannelId(getString(R.string.online_agent))
                    singleViewModel.setSelectedIncOrDecOnline(incOrDec)
                    singleViewModel.setAmountTypeOnline(amountType)
                    setOnlineAgentChannelState()
                    clearCityPair()
                    if (binding.etAddPercentage.text.toString().isEmpty()) {
                        binding.btnApply.apply {
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimary
                                )
                            )
                            setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                            isEnabled = true
                        }
                    } else {
                        binding.btnApply.apply {
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            setBackgroundResource(R.drawable.layout_rounded_shape_left_selected)
                            isEnabled = true
                        }
                    }
//                    binding.modifyFareBT.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                }

                getString(R.string.otas) -> {
                    binding.createFareTemplateLayout.gone()
                    if (otaCityWiseFareList.isNullOrEmpty()) {
                        callMultiStationWiseFairApi("4")
                        showProgressDialog(requireContext())
                        selectedTemplateId = ""
                        binding.fareTemplateET.setText("")
                    }
                    else {
                        binding.fareTemplateET.setText(singleViewModel.selectedOtaTemplateValueLiveData.value)
                        setFareTemplate()
                    }
                    singleViewModel.setChannelId(getString(R.string.otas))
                    singleViewModel.setSelectedIncOrDecOta(incOrDec)
                    singleViewModel.setAmountTypeOta(amountType)
                    setOtaChannelState()
                    clearCityPair()
                    if (binding.etAddPercentage.text.toString().isEmpty()) {
                        binding.btnApply.apply {
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimary
                                )
                            )
                            setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                            isEnabled = true
                        }
                    } else {
                        binding.btnApply.apply {
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            setBackgroundResource(R.drawable.layout_rounded_shape_left_selected)
                            isEnabled = true
                        }
                    }
//                    binding.modifyFareBT.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                }

                getString(R.string.e_booking) -> {
                    binding.createFareTemplateLayout.gone()
                    if (eBookingCityWiseFareList.isNullOrEmpty()) {
                        callMultiStationWiseFairApi("5")
                        showProgressDialog(requireContext())
                        selectedTemplateId = ""
                        binding.fareTemplateET.setText("")
                    }
                    else {
                        binding.fareTemplateET.setText(singleViewModel.selectedEBookingTemplateValueLiveData.value)
                        setFareTemplate()
                    }
                    singleViewModel.setChannelId(getString(R.string.e_booking))
                    singleViewModel.setSelectedIncOrDecEBooking(incOrDec)
                    singleViewModel.setAmountTypeEBooking(amountType)
                    setEBookingChannelState()
                    clearCityPair()
                    if (binding.etAddPercentage.text.toString().isEmpty()) {
                        binding.btnApply.apply {
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimary
                                )
                            )
                            setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                            isEnabled = true
                        }
                    } else {
                        binding.btnApply.apply {
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            setBackgroundResource(R.drawable.layout_rounded_shape_left_selected)
                            isEnabled = true
                        }
                    }
//                    binding.modifyFareBT.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                }
            }
        }
        else {
            refreshPage()
            selectedTemplateId = ""
            binding.fareTemplateET.setText("")
        }

        if (binding.fareTemplateET.text.toString().isNullOrEmpty()){
            binding.imgCross.gone()
        } else {
            binding.imgCross.visible()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setBranchChannelState() {
        if (singleViewModel.amountTypeBranch.value == 1) {
            binding.percentageRadio.isChecked = true
            binding.fixedRadio.isChecked = false
            amountType = 1
        } else {
            binding.fixedRadio.isChecked = true
            binding.percentageRadio.isChecked = false
            amountType = 2
        }

        if (singleViewModel.incDecAmountBranch.value?.isNotEmpty() == true) {
            binding.etAddPercentage.setText("${singleViewModel.incDecAmountBranch.value}")
        } else {
            binding.etAddPercentage.setText("")
        }

        if (singleViewModel.selectedIncOrDecBranch.value == 0) {
            incOrDec = 0
            binding.textView4.text =
                "${requireActivity().getString(R.string.increase)} $amountTypeText"
            binding.incDecFare.isChecked = true
            binding.decFare.isChecked = false
        } else {
            incOrDec = 1
            binding.decFare.isChecked = true
            binding.incDecFare.isChecked = false
            binding.textView4.text =
                "${requireActivity().getString(R.string.decrease)} $amountTypeText"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setOnlineAgentChannelState() {
        if (singleViewModel.amountTypeOnline.value == null) {
            binding.percentageRadio.isChecked = true
            binding.fixedRadio.isChecked = false
            amountType = 1
        } else {
            if (singleViewModel.amountTypeOnline.value == 1) {
                binding.percentageRadio.isChecked = true
                binding.fixedRadio.isChecked = false
                amountType = 1
            } else {
                binding.fixedRadio.isChecked = true
                binding.percentageRadio.isChecked = false
                amountType = 2
            }
        }

        if (singleViewModel.incDecAmountOnline.value?.isNotEmpty() == true) {
            binding.etAddPercentage.setText("${singleViewModel.incDecAmountOnline.value}")
        } else {
            binding.etAddPercentage.setText("")
        }

        if (singleViewModel.selectedIncOrDecOnline.value == null) {
            incOrDec = 0
            binding.textView4.text =
                "${requireActivity().getString(R.string.increase)} $amountTypeText"
            binding.incDecFare.isChecked = true
            binding.decFare.isChecked = false
        } else {
            if (singleViewModel.selectedIncOrDecOnline.value == 0) {
                incOrDec = 0
                binding.textView4.text =
                    "${requireActivity().getString(R.string.increase)} $amountTypeText"
                binding.incDecFare.isChecked = true
                binding.decFare.isChecked = false
            } else {
                incOrDec = 1
                binding.decFare.isChecked = true
                binding.incDecFare.isChecked = false
                binding.textView4.text =
                    "${requireActivity().getString(R.string.decrease)} $amountTypeText"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setOtaChannelState() {
        if (singleViewModel.amountTypeOta.value == null) {
            binding.percentageRadio.isChecked = true
            binding.fixedRadio.isChecked = false
            amountType = 1
        } else {
            if (singleViewModel.amountTypeOta.value == 1) {
                binding.percentageRadio.isChecked = true
                binding.fixedRadio.isChecked = false
                amountType = 1
            } else {
                binding.fixedRadio.isChecked = true
                binding.percentageRadio.isChecked = false
                amountType = 2
            }
        }

        if (singleViewModel.incDecAmountOta.value?.isNotEmpty() == true) {
            binding.etAddPercentage.setText("${singleViewModel.incDecAmountOta.value}")
        } else {
            binding.etAddPercentage.setText("")
        }

        if (singleViewModel.selectedIncOrDecOta.value == null) {
            incOrDec = 0
            binding.textView4.text =
                "${requireActivity().getString(R.string.increase)} $amountTypeText"
            binding.incDecFare.isChecked = true
            binding.decFare.isChecked = false
        } else {
            if (singleViewModel.selectedIncOrDecOta.value == 0) {
                incOrDec = 0
                binding.textView4.text =
                    "${requireActivity().getString(R.string.increase)} $amountTypeText"
                binding.incDecFare.isChecked = true
                binding.decFare.isChecked = false
            } else {
                incOrDec = 1
                binding.decFare.isChecked = true
                binding.incDecFare.isChecked = false
                binding.textView4.text =
                    "${requireActivity().getString(R.string.decrease)} $amountTypeText"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setEBookingChannelState() {

        if (singleViewModel.amountTypeEBooking.value == null) {
            binding.percentageRadio.isChecked = true
            binding.fixedRadio.isChecked = false
            amountType = 1
        } else {
            if (singleViewModel.amountTypeEBooking.value == 1) {
                binding.percentageRadio.isChecked = true
                binding.fixedRadio.isChecked = false
                amountType = 1
            } else {
                binding.fixedRadio.isChecked = true
                binding.percentageRadio.isChecked = false
                amountType = 2
            }
        }

        if (singleViewModel.incDecAmountEBooking.value?.isNotEmpty() == true) {
            binding.etAddPercentage.setText("${singleViewModel.incDecAmountEBooking.value}")
        } else {
            binding.etAddPercentage.setText("")
        }

        if (singleViewModel.selectedIncOrDecEBooking.value == null) {
            incOrDec = 0
            binding.textView4.text =
                "${requireActivity().getString(R.string.increase)} $amountTypeText"
            binding.incDecFare.isChecked = true
            binding.decFare.isChecked = false
        } else {
            if (singleViewModel.selectedIncOrDecEBooking.value == 0) {
                incOrDec = 0
                binding.textView4.text =
                    "${requireActivity().getString(R.string.increase)} $amountTypeText"
                binding.incDecFare.isChecked = true
                binding.decFare.isChecked = false
            } else {
                incOrDec = 1
                binding.decFare.isChecked = true
                binding.incDecFare.isChecked = false
                binding.textView4.text =
                    "${requireActivity().getString(R.string.decrease)} $amountTypeText"
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

    private fun callCreateFareTemplateApi() {

        showProgressDialog(requireContext())

        copyFareTemplate = CopyFare(
            branch = false,
            api = true,
            ebooking = true,
            online = true
        )
        pickUpChartViewModel.createFareTemplateApi(
            com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.request.ReqBody (
                apiKey = loginModelPref.api_key,
                reservationId = resID.toString(),
                routeId = routeId.toString(),
                templateName = binding.etTemplateName.text.toString(),
                cityWiseFare = cityWiseFareList,
                weeklyScheduleCopy = "1111111",
                fromDate = if (selectedTemplateType == 0) { fromDate.toString() } else { "" },
                toDate = if (selectedTemplateType == 0) { toDate.toString() } else { "" },
                multipleDates = if (selectedTemplateType == 1) { selectedMultipleDate } else { "" },
                copyFare = copyFareTemplate,
                locale = locale.toString(),
                isFromMiddleTier = true
            )
        )
    }

    private fun setCreateFareTemplateApiObserver() {
        pickUpChartViewModel.createFareTemplateResponse.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        requireActivity().toast(it.message)
                        refreshPage()

                        if (allowBookingTypeFare == "true") {
                            when (singleViewModel.selectedChannelIdLiveData.value) {
                                getString(R.string.branch) -> {
                                    callMultiStationWiseFairApi("1")
                                    showProgressDialog(requireContext())
                                }

                                getString(R.string.online_agent) -> {
                                    callMultiStationWiseFairApi("2")
                                    showProgressDialog(requireContext())
                                }

                                getString(R.string.otas) -> {
                                    callMultiStationWiseFairApi("4")
                                    showProgressDialog(requireContext())
                                }

                                getString(R.string.e_booking) -> {
                                    callMultiStationWiseFairApi("5")
                                    showProgressDialog(requireContext())
                                }
                            }
                        } else {
                            callMultiStationWiseFairApi("")
                            showProgressDialog(requireContext())
                        }

                    }
                    else -> {
                        requireActivity().toast(it.message)
                    }
                }
            }
        }
    }

    private fun refreshPage() {
        clearCityPair()
        seatList.clear()
        selectedSeatList.clear()
        selectedMultipleDate = ""
        dayList?.clear()

        binding.apply {
            createFareTemplateLayout.visible()
            chkSelectAll.isChecked = false
            if (fareTemplateET.text.toString().isNullOrEmpty()){
                imgCross.gone()
            } else {
                imgCross.visible()
            }
            moreTV.gone()
            moreTV.text = ""
            etMultipleDateUrc.setText("")
            etTemplateName.setText("")
            customFareTemplateLayout.gone()
            imgArrowCustomFareTemplate.setImageResource(R.drawable.ic_arrow_down)

            etAddPercentage.setText("")
            binding.etAddPercentage.setMaxLength(2)
            percentageRadio.isChecked = true
            fixedRadio.isChecked = false
            amountType = 1
            incOrDec = 0
            textView4.text = "${requireActivity().getString(R.string.increase)} (%)"
            layoutAddpercentage.hint = "${requireActivity().getString(R.string.add_lowercase)} (%)"
            incDecFare.isChecked = true
            decFare.isChecked = false
        }

        binding.btnApply.apply {
            setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
            isEnabled = true
            this.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
        }
    }
    private fun callFetchFareTemplateApi(templateId:String) {

        if (requireContext().isNetworkAvailable()) {
            pickUpChartViewModel.fetchFareTemplateDetailsApi(
                com.bitla.ts.domain.pojo.update_rate_card.fetch_fare_template.request.ReqBody(
                    apiKey = loginModelPref.api_key,
                    templateId = templateId,
                    locale = locale
                ),
                fetch_fare_template_details
            )
        } else requireContext().noNetworkToast()
    }

    private fun setFetchFareTemplateObserver() {

        pickUpChartViewModel.fetchFareTemplateResponse.observe(viewLifecycleOwner) {

            dismissProgressDialog()
            seatList.clear()
            selectedSeatList.clear()
            tempSeatListTemp.clear()
            binding.chkSelectAll.isChecked = false

            if (it != null) {
                when (it.code) {
                    200 -> {
                        fetchFareTemplateResponse = it
                        fetchFareTemplateDetails = it.multistationFareDetails

                        when (singleViewModel.selectedChannelIdLiveData.value) {
                            getString(R.string.branch) -> {
                                singleViewModel.setFetchBranchFareTemplateResponse(it)

                                if (selectedTemplateId.isNotEmpty()){
                                    fetchFareTemplateDetails.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        branchCityWiseFareList.add(cityWiseFare)

                                        val cityWiseFareCreateTemplate = com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.request.CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        cityWiseFareList.add(cityWiseFareCreateTemplate)
                                    }
                                }
                            }
                            getString(R.string.online_agent) -> {
                                singleViewModel.setFetchOnlineFareTemplateResponse(it)

                                if (selectedTemplateId.isNotEmpty()){
                                    fetchFareTemplateDetails.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        onlineCityWiseFareList.add(cityWiseFare)
                                    }
                                }
                            }
                            getString(R.string.otas) -> {
                                singleViewModel.setFetchOtaFareTemplateResponse(it)

                                if (selectedTemplateId.isNotEmpty()){
                                    fetchFareTemplateDetails.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        otaCityWiseFareList.add(cityWiseFare)
                                    }
                                }
                            }
                            getString(R.string.e_booking) -> {
                                singleViewModel.setFetchEBookingFareTemplateResponse(it)

                                if (selectedTemplateId.isNotEmpty()){
                                    fetchFareTemplateDetails.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        eBookingCityWiseFareList.add(cityWiseFare)
                                    }
                                }
                            }
                            else -> {
                                singleViewModel.setFetchBranchFareTemplateResponse(it)

                                if (selectedTemplateId.isNotEmpty()){
                                    fetchFareTemplateDetails.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        branchCityWiseFareList.add(cityWiseFare)
                                    }
                                }

                            }
                        }

                        fetchFareTemplateDetails.forEach { it ->
                            it.fareDetails.forEach {
                                val seatModel = Service()
                                seatModel.routeId = it.id?.toInt()
                                seatModel.number = it.seatType

                                tempSeatListTemp.add(seatModel)
                            }
                        }

                        val uniqueSeatList = tempSeatListTemp.toSet()
                        uniqueSeatList.forEach {
                            seatList.add(it)
                        }

                        layoutManager = GridLayoutManager(requireContext(), 3)
                        binding.rvSelectSeat.layoutManager = layoutManager
                        searchServiceAdapter = MultipleHorizontalItemSelectionAdapter(requireContext(), this)
                        searchServiceAdapter.addData(seatList)
                        binding.rvSelectSeat.adapter = searchServiceAdapter
                        searchServiceAdapter.notifyDataSetChanged()

                        setFareTemplate()
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


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setMultiStationWiseFareObserver() {

        pickUpChartViewModel.fetchMultiStatioWiseFareResponse.observe(viewLifecycleOwner) {
            
            dismissProgressDialog()
            seatList.clear()
            selectedSeatList.clear()
            tempSeatListTemp.clear()
            templateList.clear()
            binding.chkSelectAll.isChecked = false

            if (it != null) {
                when (it.code) {
                    200 -> {
                        multistationFareResponse = it
                        multistationFareDetails = it.multistation_fare_details


                        setAmountTypeNdIncDecOptions()
                        
                        when (singleViewModel.selectedChannelIdLiveData.value) {
                            getString(R.string.branch) -> {
                                singleViewModel.setBranchMultiStationFareResponse(it)
                                if (singleViewModel.includeSeatWiseCheckBranch.value == true) {
                                    binding.includeSeatWiseChk.isChecked = true
                                }

                                if (selectedTemplateId.isNotEmpty()){
                                    multistationFareDetails.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        branchCityWiseFareList.add(cityWiseFare)
                                    }
                                }

                                multistationFareDetails.forEach {
                                    val cityWiseFareCreateTemplate = com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.request.CityWiseFare(
                                        originId = it.origin_id,
                                        destinationId = it.destination_id,
                                        fareDetails = it.fareDetails
                                    )
                                    cityWiseFareList.add(cityWiseFareCreateTemplate)
                                }
                            }
                            getString(R.string.online_agent) -> {
                                singleViewModel.setOnlineMultiStationFareResponse(it)
                                if (singleViewModel.includeSeatWiseCheckOnline.value == true) {
                                    binding.includeSeatWiseChk.isChecked = true
                                }

                                if (selectedTemplateId.isNotEmpty()){
                                    multistationFareDetails.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        onlineCityWiseFareList.add(cityWiseFare)
                                    }
                                }
                            }
                            getString(R.string.otas) -> {
                                singleViewModel.setOtaMultiStationFareResponse(it)
                                if (singleViewModel.includeSeatWiseCheckOta.value == true) {
                                    binding.includeSeatWiseChk.isChecked = true
                                }

                                if (selectedTemplateId.isNotEmpty()){
                                    multistationFareDetails?.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        otaCityWiseFareList.add(cityWiseFare)
                                    }
                                }
                            }
                            getString(R.string.e_booking) -> {
                                singleViewModel.setEBookingMultiStationFareResponse(it)
                                if (singleViewModel.includeSeatWiseCheckEBooking.value == true) {
                                    binding.includeSeatWiseChk.isChecked = true
                                }

                                if (selectedTemplateId.isNotEmpty()){
                                    multistationFareDetails.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        eBookingCityWiseFareList.add(cityWiseFare)
                                    }
                                }
                            }
                            else -> {
                                singleViewModel.setBranchMultiStationFareResponse(it)
                                if (singleViewModel.includeSeatWiseCheckBranch.value == true) {
                                    binding.includeSeatWiseChk.isChecked = true
                                }
                                
                                if (selectedTemplateId.isNotEmpty()){
                                    multistationFareDetails.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        branchCityWiseFareList.add(cityWiseFare)
                                    }
                                }
                                
                                multistationFareDetails.forEach {
                                    val cityWiseFareCreateTemplate = com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.request.CityWiseFare(
                                        originId = it.origin_id,
                                        destinationId = it.destination_id,
                                        fareDetails = it.fareDetails
                                    )
                                    cityWiseFareList.add(cityWiseFareCreateTemplate)
                                }
                            }
                        }

//                        Timber.d("fare_template - ${it.fareTemplate}")
                        if (it.fareTemplate.isNotEmpty()){
                            it.fareTemplate.forEach {
                                spinnerItemsModifyFareFareTemplate = SpinnerItemsModifyFare(
                                    id = it.id,
                                    value = it.name
                                )
                                if (::spinnerItemsModifyFareFareTemplate.isInitialized) {
                                    templateList.add(spinnerItemsModifyFareFareTemplate)
                                }
                            }
                        }

                        if (it.filterOption.originCity.isNotEmpty()){
                            it.filterOption.originCity.forEach {
                                spinnerItemsCityPairCityFrom = SpinnerItemsModifyFare(
                                    id = it.id,
                                    value = it.city
                                )
                                if (::spinnerItemsCityPairCityFrom.isInitialized) {
                                    cityFromListTemp.add(spinnerItemsCityPairCityFrom)
                                }
                            }
                        }

                        if (it.filterOption.destinationCity.isNotEmpty()){
                            it.filterOption.destinationCity.forEach {
                                spinnerItemsCityPairCityTo = SpinnerItemsModifyFare(
                                    id = it.id,
                                    value = it.city
                                )
                                if (::spinnerItemsCityPairCityTo.isInitialized) {
                                    cityToListTemp.add(spinnerItemsCityPairCityTo)
                                }
                            }
                        }

                        if (it.filterOption.cityPair.isNotEmpty()) {
                            it.filterOption.cityPair.forEach {
                                spinnerCityPair = SpinnerItemsModifyFare(
                                    id = it.id,
                                    value = it.city
                                )
                                if (::spinnerCityPair.isInitialized) {
                                    cityPairTemp.add(spinnerCityPair)
                                }
                            }
                        }

                        if (multistationFareDetails.isNotEmpty()) {
                            multistationFareDetails.forEach { it ->
                                it.fareDetails.forEach {
                                    val seatModel = Service()
                                    seatModel.routeId = it.id?.toInt()
                                    seatModel.number = it.seatType
                                    tempSeatListTemp.add(seatModel)
                                }
                            }
                        }

                        val uniqueSeatList = tempSeatListTemp.toSet()
                        uniqueSeatList.forEach {
                            seatList.add(it)
                        }

                        val uniqueCityFrom = cityFromListTemp.toSet()
                        uniqueCityFrom.forEach {
                            cityFromList.add(it)
                        }

                        val uniqueCityTo = cityToListTemp.toSet()
                        uniqueCityTo.forEachIndexed { index, SpinnerItemsCityPair ->
                            cityToList.add(SpinnerItemsCityPair)
                        }

                        val uniqueCityPare = cityPairTemp.toSet()
                        uniqueCityPare.forEachIndexed { index, SpinnerItemsCityPair ->
                            cityPairList.add(SpinnerItemsCityPair)
                        }

                        layoutManager = GridLayoutManager(requireContext(), 3)
//                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        binding.rvSelectSeat.layoutManager = layoutManager
                        searchServiceAdapter = MultipleHorizontalItemSelectionAdapter(requireContext(), this)
                        searchServiceAdapter.addData(seatList)
                        binding.rvSelectSeat.adapter = searchServiceAdapter
                        searchServiceAdapter.notifyDataSetChanged()

                        binding.apply {
                            privilegeResponseModel?.let {
                                if (privilegeResponseModel?.allowToDoFareCustomizationForSeatTypes == true) {
                                    tvSeatType.visible()
                                    chkSelectAll.visible()
                                    rvSelectSeat.visible()
                                } else {
                                    tvSeatType.gone()
                                    chkSelectAll.gone()
                                    rvSelectSeat.gone()
                                }
                            }
                        }
                        selectedOriginId = cityPairList[0].id.toString()
                        selectedDestinationId = cityPairList[0].id.toString()
                        setFilterOptions()
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == SELECT_SERVICE_INTENT_REQUEST_CODE) {
            when (singleViewModel.selectedChannelIdLiveData.value) {
                getString(R.string.branch) -> {
                    branchCityWiseFareList.clear()

                    if (singleViewModel.selectedBranchTemplateValueLiveData.value?.isNotEmpty() == true){

                        singleViewModel.fetchBranchFareTemplateResponse.value?.multistationFareDetails?.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.origin_id,
                                destinationId = it.destination_id,
                                fareDetails = it.fareDetails
                            )
                            branchCityWiseFareList.add(cityWiseFare)
                        }
                    } else {
                        singleViewModel.branchMultiStationFareLiveData.value?.multistation_fare_details?.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.origin_id,
                                destinationId = it.destination_id,
                                fareDetails = it.fareDetails
                            )
                            branchCityWiseFareList.add(cityWiseFare)
                        }
                    }

                }
                getString(R.string.online_agent) -> {
                    onlineCityWiseFareList.clear()

                    if (singleViewModel.selectedOnlineTemplateValueLiveData.value?.isNotEmpty() == true){
                        singleViewModel.fetchOnlineFareTemplateResponse.value?.multistationFareDetails?.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.origin_id,
                                destinationId = it.destination_id,
                                fareDetails = it.fareDetails
                            )
                            onlineCityWiseFareList.add(cityWiseFare)
                        }
                    } else {
                        singleViewModel.onlineMultiStationFareResponseLiveData.value?.multistation_fare_details?.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.origin_id,
                                destinationId = it.destination_id,
                                fareDetails = it.fareDetails
                            )
                            onlineCityWiseFareList.add(cityWiseFare)
                        }
                    }


                }
                getString(R.string.otas) -> {
                    otaCityWiseFareList.clear()
                    if (singleViewModel.selectedOtaTemplateValueLiveData.value?.isNotEmpty() == true){
                        singleViewModel.fetchOtaFareTemplateResponse.value?.multistationFareDetails?.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.origin_id,
                                destinationId = it.destination_id,
                                fareDetails = it.fareDetails
                            )
                            otaCityWiseFareList.add(cityWiseFare)
                        }
                    } else {
                        singleViewModel.oTAMultiStationFareResponseLiveData.value?.multistation_fare_details?.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.origin_id,
                                destinationId = it.destination_id,
                                fareDetails = it.fareDetails
                            )
                            otaCityWiseFareList.add(cityWiseFare)
                        }
                    }

                }
                getString(R.string.e_booking) -> {
                    eBookingCityWiseFareList.clear()
                    if (singleViewModel.selectedOtaTemplateValueLiveData.value?.isNotEmpty() == true){

                        singleViewModel.fetchEBookingFareTemplateResponse.value?.multistationFareDetails?.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.origin_id,
                                destinationId = it.destination_id,
                                fareDetails = it.fareDetails
                            )
                            eBookingCityWiseFareList.add(cityWiseFare)
                        }
                    } else {
                        singleViewModel.eBookingMultiStationFareResponseLiveData.value?.multistation_fare_details?.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.origin_id,
                                destinationId = it.destination_id,
                                fareDetails = it.fareDetails
                            )
                            eBookingCityWiseFareList.add(cityWiseFare)
                        }
                    }
                }

                else -> {
                    branchCityWiseFareList.clear()

                    if (singleViewModel.selectedBranchTemplateValueLiveData.value?.isNotEmpty() == true){

                        singleViewModel.fetchBranchFareTemplateResponse.value?.multistationFareDetails?.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.origin_id,
                                destinationId = it.destination_id,
                                fareDetails = it.fareDetails
                            )
                            branchCityWiseFareList.add(cityWiseFare)
                        }
                    } else {
                        singleViewModel.branchMultiStationFareLiveData.value?.multistation_fare_details?.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.origin_id,
                                destinationId = it.destination_id,
                                fareDetails = it.fareDetails
                            )
                            branchCityWiseFareList.add(cityWiseFare)
                        }
                    }
                }
            }

//            Timber.d("onActivity_Branch - $branchCityWiseFareList")
//            Timber.d("onActivity_Online - $onlineCityWiseFareList")
//            Timber.d("onActivity_OTA - $otaCityWiseFareList")
//            Timber.d("onActivity_EBooking - $eBookingCityWiseFareList")

            if (branchCityWiseFareList.isNotEmpty()
                || onlineCityWiseFareList.isNotEmpty()
                || otaCityWiseFareList.isNotEmpty()
                || eBookingCityWiseFareList.isNotEmpty()
            ) {
                binding.btnSaveModifyService.apply {
                    setBackgroundResource(R.drawable.button_selected_bg)
                    isEnabled = true
                    binding.btnSaveModifyService.isEnabled = true
                }
            }
        }
    }

    private fun setIntent() {
        val intent = Intent(requireActivity(), ModifyIndividualRouteFareActivity::class.java)

        intent.apply {
            putExtra(
                getString(R.string.from_city),
                jsonToString(selectedFromCityList)
            )
            putExtra(
                getString(R.string.multistation_fare_response_model),
                jsonToString(multistationFareResponse ?: "")
            )
            putExtra(
                getString(R.string.multistation_fare_fixed_percent),
                binding.etAddPercentage.text.toString()
            )
            putExtra(
                getString(R.string.multistation_amount_type),
                amountType.toString()
            )
            putExtra(
                getString(R.string.multistation_incOrDec_fare),
                incOrDec.toString()
            )
            putExtra(
                getString(R.string.updateRateCard_originId),
                selectedCityPairOriginId.toString()
            )
            putExtra(
                getString(R.string.updateRateCard_destinationId),
                selectedCityPairDestinationId.toString()
            )
        }

        startActivityForResult(intent, 2)
    }
    @SuppressLint("SetTextI18n")
    private fun onClick() {

        if (privilegeResponseModel != null) {
            privilegeResponseModel?.let {
                if (it.currency.isNotEmpty()) {
                    currencySymbol = it.currency
                }
            }
        } else {
            requireActivity().toast(requireActivity().getString(R.string.server_error))
        }

        binding.apply {
            chkSelectAll.setOnClickListener {
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
                searchServiceAdapter.addData(seatList)
            }

            modifyFareBT.setOnClickListener {

                if (selectedFromCityList.size >= 1 && selectedToCityList.size >= 1) {
                    if (selectedFromCityList[0].value == selectedToCityList[0].value
                        && binding.cityPairET.text.toString().isEmpty()
                    ) {
                        requireActivity().toast(requireContext().getString(R.string.city_pair_not_found))
                    } else {
                        setIntent()
                    }
                } else {
                    setIntent()
                }

//                Timber.d("origin_des_id - $selectedCityPairOriginId  - $selectedCityPairDestinationId")
//                Timber.d("fareSeatId - $seatId")
            }

            btnApply.setOnClickListener {

                if (!isAllowToDoFareCustomizationForSeatTypes) {
                    if (binding.etAddPercentage.text.toString().isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Amount should not be blank",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        saveSelectedValue()
                        applyAndSaveChannels()
                    }
                } else {
                    selectedSeatList.clear()
                    seatList.forEachIndexed { i, it ->
                        if (it.isSeatChecked) {
                            seatId += it.routeId.toString().replace(".0", "") + ","

                            val seatModel = Service()
                            seatModel.routeId = it.routeId
                            selectedSeatList.add(seatModel)
                        }
                    }

                    if (seatId.isNotEmpty()) {
                        try {
                            seatId = seatId.substring(0, seatId.lastIndexOf(","))
                        } catch (_:Exception){ }
                    }

//                Timber.d("fareSeatIdList - $seatId")

                    if (seatId.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Please select at least one seat type",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (binding.etAddPercentage.text.toString().isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Amount should not be blank",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        saveSelectedValue()
                        applyAndSaveChannels()
                    }
                }
            }

            copyFareBT.setOnClickListener {
                DialogUtils.showCopyFareDialog(
                    context = requireContext(),
                    selectedChannel = singleViewModel.selectedChannelIdLiveData.value.toString(),
                    onApply = { copyFareTo ->

                        for (i in 0 until copyFareTo.size) {
                            when (singleViewModel.selectedChannelIdLiveData.value) {
                                getString(R.string.branch) -> {

                                    branchCityWiseFareList.clear()
                                    singleViewModel.branchMultiStationFareLiveData.value?.multistation_fare_details?.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        branchCityWiseFareList.add(cityWiseFare)
                                    }

                                    if (copyFareTo[i] == getString(R.string.online_agent)) {
                                        onlineCityWiseFareList = branchCityWiseFareList
                                        copied1 = " ${copyFareTo[i]},"
                                        copyFareOnline = true
                                    }
                                    if (copyFareTo[i] == getString(R.string.otas)) {
                                        otaCityWiseFareList = branchCityWiseFareList
                                        copied2 = " ${copyFareTo[i]},"
                                        copyFareOta = true

                                    }
                                    if (copyFareTo[i] == getString(R.string.e_booking)) {
                                        eBookingCityWiseFareList = branchCityWiseFareList
                                        copied3 = " ${copyFareTo[i]},"
                                        copyFareEBooking = true
                                    }

                                    copyFare = CopyFare(
                                        branch = copyFareBranch,
                                        api = copyFareOta,
                                        ebooking = copyFareEBooking,
                                        online = copyFareOnline
                                    )
                                }

                                getString(R.string.online_agent) -> {

                                    onlineCityWiseFareList.clear()
                                    singleViewModel.onlineMultiStationFareResponseLiveData.value?.multistation_fare_details?.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        onlineCityWiseFareList.add(cityWiseFare)
                                    }

                                    if (copyFareTo[i] == getString(R.string.branch)) {
                                        branchCityWiseFareList = onlineCityWiseFareList
                                        copied1 = " ${copyFareTo[i]},"
                                        copyFareBranch = true
                                    }
                                    if (copyFareTo[i] == getString(R.string.otas)) {
                                        otaCityWiseFareList = onlineCityWiseFareList
                                        copied2 = " ${copyFareTo[i]},"
                                        copyFareOta = true

                                    }
                                    if (copyFareTo[i] == getString(R.string.e_booking)) {
                                        eBookingCityWiseFareList = onlineCityWiseFareList
                                        copied3 = " ${copyFareTo[i]},"
                                        copyFareEBooking = true
                                    }

                                    copyFare = CopyFare(
                                        branch = copyFareBranch,
                                        api = copyFareOta,
                                        ebooking = copyFareEBooking,
                                        online = copyFareOnline
                                    )
                                }

                                getString(R.string.otas) -> {

                                    otaCityWiseFareList.clear()
                                    singleViewModel.oTAMultiStationFareResponseLiveData.value?.multistation_fare_details?.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        otaCityWiseFareList.add(cityWiseFare)
                                    }

                                    if (copyFareTo[i] == getString(R.string.branch)) {
                                        branchCityWiseFareList = otaCityWiseFareList
                                        copied1 = " ${copyFareTo[i]},"
                                        copyFareBranch = true

                                    }
                                    if (copyFareTo[i] == getString(R.string.online_agent)) {
                                        onlineCityWiseFareList = otaCityWiseFareList
                                        copied2 = " ${copyFareTo[i]},"
                                        copyFareOnline = true
                                    }
                                    if (copyFareTo[i] == getString(R.string.e_booking)) {
                                        eBookingCityWiseFareList = otaCityWiseFareList
                                        copied3 = " ${copyFareTo[i]},"
                                        copyFareEBooking = true
                                    }

                                    copyFare = CopyFare(
                                        branch = copyFareBranch,
                                        api = copyFareOta,
                                        ebooking = copyFareEBooking,
                                        online = copyFareOnline
                                    )
                                }

                                getString(R.string.e_booking) -> {
                                    eBookingCityWiseFareList.clear()
                                    singleViewModel.eBookingMultiStationFareResponseLiveData.value?.multistation_fare_details?.forEach {
                                        val cityWiseFare = CityWiseFare(
                                            originId = it.origin_id,
                                            destinationId = it.destination_id,
                                            fareDetails = it.fareDetails
                                        )
                                        eBookingCityWiseFareList.add(cityWiseFare)
                                    }

                                    if (copyFareTo[i] == getString(R.string.branch)) {
                                        branchCityWiseFareList = eBookingCityWiseFareList
                                        copied1 = " ${copyFareTo[i]},"
                                        copyFareBranch = true
                                    }
                                    if (copyFareTo[i] == getString(R.string.online_agent)) {
                                        onlineCityWiseFareList = eBookingCityWiseFareList
                                        copied2 = " ${copyFareTo[i]},"
                                        copyFareOnline = true
                                    }
                                    if (copyFareTo[i] == getString(R.string.otas)) {
                                        otaCityWiseFareList = eBookingCityWiseFareList
                                        copied3 = " ${copyFareTo[i]},"
                                        copyFareOta = true
                                    }

                                    copyFare = CopyFare(
                                        branch = copyFareBranch,
                                        api = copyFareOta,
                                        ebooking = copyFareEBooking,
                                        online = copyFareOnline
                                    )
                                }
                            }
                        }

                        DialogUtils.successfulMsgDialog(
                            requireContext(), "Fare Copied Successfully"
                        )

                        if (copied1.isNotEmpty() && copied2.isNotEmpty() && copied3.isNotEmpty()) {
                            binding.fareCopiedLabelTV.text =
                                "Fare copied from ${singleViewModel.selectedChannelIdLiveData.value} to All"
                        } else {
                            binding.fareCopiedLabelTV.text =
                                "Fare copied from ${singleViewModel.selectedChannelIdLiveData.value} to $copied1$copied2$copied3"
                            val removeLastComma =
                                binding.fareCopiedLabelTV.text.toString().dropLast(1)
                            binding.fareCopiedLabelTV.text = removeLastComma
                        }
                        binding.fareCopiedLabelTV.visible()
                        binding.btnSaveModifyService.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                            this.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                        }
                    }
                )
            }

            imgBulkModify.setOnClickListener {
                if (innerConstraintLayout.isVisible) {
                    innerConstraintLayout.gone()
                    firstV.gone()
                    imgBulkModify.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    innerConstraintLayout.visible()
                    firstV.visible()
                    imgBulkModify.setImageResource(R.drawable.ic_arrow_up_24)
                }
            }

            btnSaveModifyService.setOnClickListener {

                showProgressDialog(requireContext())

                if (requireContext().isNetworkAvailable()) {
                    if (allowBookingTypeFare == "true") {
                        if (copied1.isNotEmpty() || copied2.isNotEmpty() || copied3.isNotEmpty()) {
                            when (singleViewModel.selectedChannelIdLiveData.value) {
                                getString(R.string.branch) -> {
                                    if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                        DialogUtils.showFullHeightPinInputBottomSheet(
                                            activity = requireActivity(),
                                            fragmentManager = childFragmentManager,
                                            pinSize = pinSize,
                                            getString(R.string.branch),
                                            onPinSubmitted = { pin: String ->
                                                callManageFareBranchApi(pin)
                                                dismissProgressDialog()
                                            },
                                            onDismiss = {
                                                dismissProgressDialog()
                                            }
                                        )
                                    } else {
                                        callManageFareBranchApi("")
                                        dismissProgressDialog()
                                    }
                                }
                                getString(R.string.online_agent) -> {
                                    if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                        DialogUtils.showFullHeightPinInputBottomSheet(
                                            activity = requireActivity(),
                                            fragmentManager = childFragmentManager,
                                            pinSize = pinSize,
                                            getString(R.string.online_agent),
                                            onPinSubmitted = { pin: String ->
                                                callManageFareOnlineAgentApi(pin)
                                                dismissProgressDialog()
                                            },
                                            onDismiss = {
                                                dismissProgressDialog()
                                            }
                                        )
                                    } else {
                                        callManageFareOnlineAgentApi("")
                                        dismissProgressDialog()
                                    }
                                }
                                getString(R.string.otas) -> {
                                    if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                        DialogUtils.showFullHeightPinInputBottomSheet(
                                            activity = requireActivity(),
                                            fragmentManager = childFragmentManager,
                                            pinSize = pinSize,
                                            getString(R.string.otas),
                                            onPinSubmitted = { pin: String ->
                                                callManageFareOTAApi(pin)
                                                dismissProgressDialog()
                                            },
                                            onDismiss = {
                                                dismissProgressDialog()
                                            }
                                        )
                                    } else {
                                        callManageFareOTAApi("")
                                        dismissProgressDialog()
                                    }
                                }
                                getString(R.string.e_booking) -> {
                                    if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                        DialogUtils.showFullHeightPinInputBottomSheet(
                                            activity = requireActivity(),
                                            fragmentManager = childFragmentManager,
                                            pinSize = pinSize,
                                            getString(R.string.e_booking),
                                            onPinSubmitted = { pin: String ->
                                                callManageFareEBookingApi(pin)
                                                dismissProgressDialog()
                                            },
                                            onDismiss = {
                                                dismissProgressDialog()
                                            }
                                        )
                                    } else {
                                        callManageFareEBookingApi("")
                                        dismissProgressDialog()
                                    }
                                }
                            }
                        }
                        else {
                            Timber.d("branchCityWiseFareList - $branchCityWiseFareList")
                            if (branchCityWiseFareList.isNotEmpty()) {
                                if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                    DialogUtils.showFullHeightPinInputBottomSheet(
                                        activity = requireActivity(),
                                        fragmentManager = childFragmentManager,
                                        pinSize = pinSize,
                                        getString(R.string.branch),
                                        onPinSubmitted = { pin: String ->
                                            callManageFareBranchApi(pin)
                                            dismissProgressDialog()
                                        },
                                        onDismiss = {
                                            dismissProgressDialog()
                                        }
                                    )
                                } else {
                                    callManageFareBranchApi("")
                                    dismissProgressDialog()
                                }
                            } else if (onlineCityWiseFareList.isNotEmpty()) {
                                if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                    DialogUtils.showFullHeightPinInputBottomSheet(
                                        activity = requireActivity(),
                                        fragmentManager = childFragmentManager,
                                        pinSize = pinSize,
                                        getString(R.string.online_agent),
                                        onPinSubmitted = { pin: String ->
                                            callManageFareOnlineAgentApi(pin)
                                            dismissProgressDialog()
                                        },
                                        onDismiss = {
                                            dismissProgressDialog()
                                        }
                                    )
                                } else {
                                    callManageFareOnlineAgentApi("")
                                    dismissProgressDialog()
                                }
                            } else if (otaCityWiseFareList.isNotEmpty()) {
                                if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                    DialogUtils.showFullHeightPinInputBottomSheet(
                                        activity = requireActivity(),
                                        fragmentManager = childFragmentManager,
                                        pinSize = pinSize,
                                        getString(R.string.otas),
                                        onPinSubmitted = { pin: String ->
                                            callManageFareOTAApi(pin)
                                            dismissProgressDialog()
                                        },
                                        onDismiss = {
                                            dismissProgressDialog()
                                        }
                                    )
                                } else {
                                    callManageFareOTAApi("")
                                    dismissProgressDialog()
                                }
                            } else if (eBookingCityWiseFareList.isNotEmpty()) {
                                if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                    DialogUtils.showFullHeightPinInputBottomSheet(
                                        activity = requireActivity(),
                                        fragmentManager = childFragmentManager,
                                        pinSize = pinSize,
                                        getString(R.string.e_booking),
                                        onPinSubmitted = { pin: String ->
                                            callManageFareEBookingApi(pin)
                                            dismissProgressDialog()
                                        },
                                        onDismiss = {
                                            dismissProgressDialog()
                                        }
                                    )
                                } else {
                                    callManageFareEBookingApi("")
                                    dismissProgressDialog()
                                }
                            }
                        }
                    } else {
                        if (shouldModifyReservation && currentCountry.equals("india", true)) {
                            DialogUtils.showFullHeightPinInputBottomSheet(
                                activity = requireActivity(),
                                fragmentManager = childFragmentManager,
                                pinSize = pinSize,
                                getString(R.string.bulk_modify),
                                onPinSubmitted = { pin: String ->
                                    callBulkManageFareBranchApi(pin)
                                    dismissProgressDialog()
                                },
                                onDismiss = {
                                    dismissProgressDialog()
                                }
                            )
                        } else {
                            callBulkManageFareBranchApi("")
                            dismissProgressDialog()
                        }
                        branchId =0
                        onlineAgentId=0
                        otasId=0
                        eBookingId=0
                    }

                } else requireContext().noNetworkToast()
//                DialogUtils.UpdateRcDialoge(
//                        context = requireContext(),
//                        title = requireContext().getString(R.string.update_rate_card_question),
//                        message = "",
//                        increaseby = "$amountTypeFixed$farePercentage$amountTypePercentage",
//                        increaseOrDecreaseByLabel = increaseOrDecreaseByLabel,
//                        fromDate = fromDateDialog,
//                        toDate = fromDateDialog,
//                        srcDest = "$source-$destination",
//                        journeyDate = busType.toString(),
//                        buttonLeftText = requireActivity().getString(R.string.goBack),
//                        buttonRightText = requireActivity().getString(R.string.confirm),
//                        dialogButtonListener = this@NewFareFragment
//                    )
            }
        }
    }

    private fun saveSelectedValue() {
        when (singleViewModel.selectedChannelIdLiveData.value) {
            getString(R.string.branch) -> {
                singleViewModel.setIncDecAmountBranch(binding.etAddPercentage.text.toString())
//                Timber.d("selectedChannel - ${singleViewModel.selectedChannelIdLiveData.value}}")
            }
            getString(R.string.online_agent) -> {
                singleViewModel.setIncDecAmountOnline(binding.etAddPercentage.text.toString())
//                Timber.d("selectedChannel - ${singleViewModel.selectedChannelIdLiveData.value}}")
            }
            getString(R.string.otas) -> {
                singleViewModel.setIncDecAmountOta(binding.etAddPercentage.text.toString())
//                Timber.d("selectedChannel - ${singleViewModel.selectedChannelIdLiveData.value}}")
            }
            getString(R.string.e_booking) -> {
                singleViewModel.setIncDecAmountEBooking(binding.etAddPercentage.text.toString())
//                Timber.d("selectedChannel - ${singleViewModel.selectedChannelIdLiveData.value}}")
            }
        }
    }

    private fun applyAndSaveChannels() {
        if (selectedTemplateId.isNotEmpty()){
            applyAndSaveFetchFareTemplate()
        } else {
            applyAndSaveMultistationFare()
        }
    }

    private fun applyAndSaveMultistationFare() {

        if (allowBookingTypeFare == "true") {
            when (singleViewModel.selectedChannelIdLiveData.value) {
                getString(R.string.branch) -> {
                    incrementAndDecrementFare(branchCityWiseFareList)
                    binding.tvModifiedChannelLabel1.visible()
                    binding.tvModifiedChannelLabel1.text = getString(R.string.branch_channel_modified)
                }

                getString(R.string.online_agent) -> {
                    incrementAndDecrementFare(onlineCityWiseFareList)
                    binding.tvModifiedChannelLabel2.visible()
                    binding.tvModifiedChannelLabel2.text = getString(R.string.online_a_channel_modified)
                }

                getString(R.string.otas) -> {
                    incrementAndDecrementFare(otaCityWiseFareList)
                    binding.tvModifiedChannelLabel3.visible()
                    binding.tvModifiedChannelLabel3.text = getString(R.string.otas_channel_modified)
                }

                getString(R.string.e_booking) -> {
                    incrementAndDecrementFare(eBookingCityWiseFareList)
                    binding.tvModifiedChannelLabel4.visible()
                    binding.tvModifiedChannelLabel4.text = getString(R.string.e_booking_channel_modified)
                }
            }
        } else {
            incrementAndDecrementFare(branchCityWiseFareList)
            binding.apply {
                tvModifiedChannelLabel2.gone()
                tvModifiedChannelLabel3.gone()
                tvModifiedChannelLabel4.gone()
                tvModifiedChannelLabel1.visible()
                tvModifiedChannelLabel1.text = getString(R.string.all_channel_modified)
            }
        }

        if (branchCityWiseFareList.isNotEmpty()
            || onlineCityWiseFareList.isNotEmpty()
            || otaCityWiseFareList.isNotEmpty()
            || eBookingCityWiseFareList.isNotEmpty()
        ) {
            binding.btnSaveModifyService.apply {
                setBackgroundResource(R.drawable.button_selected_bg)
                isEnabled = true
                binding.btnSaveModifyService.isEnabled = true
            }
        }
    }

    private fun applyAndSaveFetchFareTemplate(){

        if (allowBookingTypeFare == "true") {
            when (singleViewModel.selectedChannelIdLiveData.value) {
                getString(R.string.branch) -> {
                    incrementAndDecrementTemplateFare(branchCityWiseFareList)
                    binding.tvModifiedChannelLabel1.visible()
                    binding.tvModifiedChannelLabel1.text = getString(R.string.branch_channel_modified)
                }
                getString(R.string.online_agent) -> {
                    incrementAndDecrementTemplateFare(onlineCityWiseFareList)
                    binding.tvModifiedChannelLabel2.visible()
                    binding.tvModifiedChannelLabel2.text = getString(R.string.online_a_channel_modified)
                }
                getString(R.string.otas) -> {
                    incrementAndDecrementTemplateFare(otaCityWiseFareList)
                    binding.tvModifiedChannelLabel3.visible()
                    binding.tvModifiedChannelLabel3.text = getString(R.string.otas_channel_modified)
                }
                getString(R.string.e_booking) -> {
                    incrementAndDecrementTemplateFare(eBookingCityWiseFareList)
                    binding.tvModifiedChannelLabel4.visible()
                    binding.tvModifiedChannelLabel4.text = getString(R.string.e_booking_channel_modified)
                }
            }
        } else {
            incrementAndDecrementTemplateFare(branchCityWiseFareList)
            binding.apply {
                tvModifiedChannelLabel2.gone()
                tvModifiedChannelLabel3.gone()
                tvModifiedChannelLabel4.gone()
                tvModifiedChannelLabel1.visible()
                tvModifiedChannelLabel1.text = getString(R.string.all_channel_modified)
            }
        }

        if (branchCityWiseFareList.isNotEmpty()
            || onlineCityWiseFareList.isNotEmpty()
            || otaCityWiseFareList.isNotEmpty()
            || eBookingCityWiseFareList.isNotEmpty()
        ) {
            binding.btnSaveModifyService.apply {
                setBackgroundResource(R.drawable.button_selected_bg)
                isEnabled = true
                binding.btnSaveModifyService.isEnabled = true
            }
        }
    }

    
    private fun incrementAndDecrementFare(cityWiseFareList: MutableList<CityWiseFare>) {
        
        cityWiseFareList.clear()
        try {
            var fareDetailsList = mutableListOf<MultistationFareDetails>()
            var selectedCityPairIdList = mutableListOf<SpinnerItemsModifyFare>()

            var selectedFromCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
            var selectedToCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()

            if (singleViewModel.selectedOriginIdList.value?.isNotEmpty() == true) {
                selectedFromCityList = singleViewModel.selectedOriginIdList.value!!
            }
            if (singleViewModel.selectedDestinationIdList.value?.isNotEmpty() == true) {
                selectedToCityList = singleViewModel.selectedDestinationIdList.value!!
            }

            if(singleViewModel.branchMultiStationFareLiveData.value!!.multistation_fare_details.isNotEmpty()){
                fareDetailsList = singleViewModel.branchMultiStationFareLiveData.value!!.multistation_fare_details
            }
            if (singleViewModel.selectedCityPairIdList.value?.isNotEmpty() == true){
                selectedCityPairIdList = singleViewModel.selectedCityPairIdList.value!!
            }

            fareDetailsList.forEach { it1->

                if (!isAllowToDoFareCustomizationForSeatTypes) {
                    it1.fareDetails.forEachIndexed { indexInner, it ->

                        val newFareRounded = it.fare?.toDouble()?.roundToInt()

                        if (amountType == 1) {
                            if (incOrDec == 0) {
                                if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                    if (newFareRounded != null) {
                                        it.fare = ((newFareRounded + newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                    }
                                }

                            } else {
                                if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                    if (newFareRounded != null) {
                                        it.fare = ((newFareRounded - newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                    }
                                }
                            }
                        } else {
                            if (incOrDec == 0) {
                                if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                    if (newFareRounded != null) {
                                        it.fare = ((newFareRounded + binding.etAddPercentage.text.toString().toInt()).toString())
                                    }
                                }

                            } else {
                                if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                    if (newFareRounded != null) {
                                        it.fare = ((newFareRounded - binding.etAddPercentage.text.toString().toInt()).toString())
                                    }
                                }
                            }
                        }
                    }

                } else {
                    if (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty() && selectedCityPairIdList.isEmpty()){

                        it1.fareDetails.forEachIndexed { indexInner, it ->

                            val newFareRounded = it.fare?.toDouble()?.roundToInt()

                            selectedSeatList.forEachIndexed { i, e ->
                                if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                    if (binding.etAddPercentage.text.toString().isNotEmpty()
                                    ) {
                                        if (newFareRounded != null) {
                                            if (amountType == 1) {
                                                if (incOrDec == 0){
                                                    it.fare = ((newFareRounded + newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                                } else {
                                                    it.fare = ((newFareRounded - newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                                }
                                            } else {
                                                if (incOrDec == 0) {
                                                    it.fare = ((newFareRounded + binding.etAddPercentage.text.toString().toInt()).toString())
                                                }  else {
                                                    it.fare = ((newFareRounded - binding.etAddPercentage.text.toString().toInt()).toString())
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    it.fare = it.fare
                                }
                            }
                        }

                    } else {
                        if (selectedCityPairIdList.isNotEmpty()){
                            selectedCityPairIdList.forEachIndexed { index, spinnerItems2 ->
                                if (spinnerItems2.id.substringBefore("-") == it1.origin_id
                                    && spinnerItems2.id.substringAfter("-") == it1.destination_id
                                ) {

                                    it1.fareDetails.forEachIndexed { indexInner, it ->
                                        val newFareRounded = it.fare?.toDouble()?.roundToInt()

                                        selectedSeatList.forEachIndexed { i, e ->
                                            if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                                if (binding.etAddPercentage.text.toString().isNotEmpty()
                                                ) {
                                                    try {
                                                        val percentage = binding.etAddPercentage.text.toString().toIntOrNull()
                                                        if (newFareRounded != null) {
                                                            it.fare = when (amountType) {
                                                                1 -> {
                                                                    if (incOrDec == 0) {
                                                                        (newFareRounded + (newFareRounded * percentage!! / 100)).toString()
                                                                    } else {
                                                                        (newFareRounded - (newFareRounded * percentage!! / 100)).toString()
                                                                    }
                                                                }
                                                                else -> {
                                                                    if (incOrDec == 0) {
                                                                        (newFareRounded + percentage!!).toString()
                                                                    } else {
                                                                        (newFareRounded - percentage!!).toString()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        Timber.tag("Error").e("Exception occurred: %s", e.message)
                                                    }
                                                }
                                            } else {
                                                it.fare = it.fare
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if (selectedFromCityList.isNotEmpty()){
                                selectedFromCityList.forEachIndexed { index, spinnerItems2 ->
                                    if (spinnerItems2.id.substringBefore("-") == it1.origin_id
                                    ) {

                                        it1.fareDetails.forEachIndexed { indexInner, it ->
                                            val newFareRounded = it.fare?.toDouble()?.roundToInt()

                                            selectedSeatList.forEachIndexed { i, e ->
                                                if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                                    if (binding.etAddPercentage.text.toString().isNotEmpty()
                                                    ) {
                                                        if (newFareRounded != null) {
                                                            if (amountType == 1) {
                                                                if (incOrDec == 0){
                                                                    it.fare = ((newFareRounded + newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                                                } else {
                                                                    it.fare = ((newFareRounded - newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                                                }
                                                            } else {
                                                                if (incOrDec == 0) {
                                                                    it.fare = ((newFareRounded + binding.etAddPercentage.text.toString().toInt()).toString())
                                                                }  else {
                                                                    it.fare = ((newFareRounded - binding.etAddPercentage.text.toString().toInt()).toString())
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    it.fare = it.fare
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (selectedToCityList.isNotEmpty()){
                                selectedToCityList.forEachIndexed { index, spinnerItems2 ->
                                    if (spinnerItems2.id.substringAfter("-") == it1.destination_id) {
                                        it1.fareDetails.forEachIndexed { indexInner, it ->
                                            val newFareRounded = it.fare?.toDouble()?.roundToInt()

                                            selectedSeatList.forEachIndexed { i, e ->
                                                if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                                    if (binding.etAddPercentage.text.toString().isNotEmpty()
                                                    ) {
                                                        if (newFareRounded != null) {
                                                            if (amountType == 1) {
                                                                if (incOrDec == 0){
                                                                    it.fare = ((newFareRounded + newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                                                } else {
                                                                    it.fare = ((newFareRounded - newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                                                }
                                                            } else {
                                                                if (incOrDec == 0) {
                                                                    it.fare = ((newFareRounded + binding.etAddPercentage.text.toString().toInt()).toString())
                                                                }  else {
                                                                    it.fare = ((newFareRounded - binding.etAddPercentage.text.toString().toInt()).toString())
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    it.fare = it.fare
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                val cityWiseFare = CityWiseFare(
                    it1.origin_id,
                    it1.fareDetails,
                    it1.destination_id
                )

                cityWiseFareList.add(cityWiseFare)
            }
        }catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }
        

    }

    private fun incrementAndDecrementTemplateFare(cityWiseFareList: MutableList<CityWiseFare>) {

        cityWiseFareList.clear()

        fetchFareTemplateResponse?.multistationFareDetails?.forEach { outerLoop ->
            outerLoop.fareDetails.forEach {

                val newFareRounded = it.fare.toString().toDouble().roundToInt()

                if (amountType == 1) {
                    if (incOrDec == 0) {
                        if (isAllowToDoFareCustomizationForSeatTypes) {
                            if (selectedFromCityList.size != 0) {

                                selectedCityPairList.forEachIndexed { index, spinnerItemsCityPair2 ->
                                    if (outerLoop.origin_id == spinnerItemsCityPair2.id.substringBefore("-")
                                        && outerLoop.destination_id == spinnerItemsCityPair2.id.substringAfter("")
                                    ) {
                                        selectedSeatList.forEachIndexed { i, e ->
                                            if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                                if (binding.etAddPercentage.text.toString().isNotEmpty()
                                                ) {
                                                    it.fare = ((newFareRounded + newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                                }
                                            } else {
                                                it.fare = it.fare
                                            }
                                        }
                                    }
                                }
                            } else {
                                selectedSeatList.forEachIndexed { i, e ->
                                    if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                        if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                            it.fare = ((newFareRounded + newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                        }
                                    } else {
                                        it.fare = it.fare
                                    }
                                }
                            }
                        }
                        else {
                            if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                it.fare = ((newFareRounded + newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                            }
                        }
                    }
                    else {
                        if (isAllowToDoFareCustomizationForSeatTypes) {
                            if (selectedFromCityList.size != 0) {

                                selectedCityPairList.forEachIndexed { index, spinnerItemsCityPair2 ->
                                    if (outerLoop.origin_id == spinnerItemsCityPair2.id.substringBefore("-")
                                        && outerLoop.destination_id == spinnerItemsCityPair2.id.substringAfter("")
                                    ) {
                                        selectedSeatList.forEachIndexed { i, e ->
                                            if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                                if (binding.etAddPercentage.text.toString().isNotEmpty()
                                                ) {
                                                    it.fare = ((newFareRounded - newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                                }
                                            } else {
                                                it.fare = it.fare
                                            }
                                        }
                                    }
                                }
                            } else {
                                selectedSeatList.forEachIndexed { i, e ->
                                    if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                        if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                            it.fare = ((newFareRounded - newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                                        }
                                    } else {
                                        it.fare = it.fare
                                    }
                                }
                            }
                        }
                        else {
                            if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                it.fare = ((newFareRounded - newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
                            }
                        }
                    }
                } else {
                    if (incOrDec == 0) {
                        if (isAllowToDoFareCustomizationForSeatTypes) {
                            if (selectedCityPairList.size != 0) {

                                selectedCityPairList.forEachIndexed { index, spinnerItemsCityPair2 ->
                                    if (outerLoop.origin_id == spinnerItemsCityPair2.id.substringBefore("-")
                                        && outerLoop.destination_id == spinnerItemsCityPair2.id.substringAfter("")
                                    ) {
                                        selectedSeatList.forEachIndexed { i, e ->
                                            if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                                if (binding.etAddPercentage.text.toString().isNotEmpty()
                                                ) {
                                                    it.fare = ((newFareRounded + binding.etAddPercentage.text.toString().toInt()).toString())
                                                }
                                            } else {
                                                it.fare = it.fare
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                selectedSeatList.forEachIndexed { i, e ->
                                    if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                        if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                            it.fare = ((newFareRounded + binding.etAddPercentage.text.toString().toInt()).toString())
                                        }
                                    } else {
                                        it.fare = it.fare
                                    }
                                }
                            }
                        }
                        else {
                            if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                it.fare = ((newFareRounded + binding.etAddPercentage.text.toString().toInt()).toString())
                            }
                        }
                    }
                    else {
                        if (isAllowToDoFareCustomizationForSeatTypes) {
                            if (selectedCityPairList.size != 0) {

                                selectedCityPairList.forEachIndexed { index, spinnerItemsCityPair2 ->
                                    if (outerLoop.origin_id == spinnerItemsCityPair2.id.substringBefore("-")
                                        && outerLoop.destination_id == spinnerItemsCityPair2.id.substringAfter("")
                                    ) {
                                        selectedSeatList.forEachIndexed { i, e ->
                                            if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                                if (binding.etAddPercentage.text.toString().isNotEmpty()
                                                ) {
                                                    it.fare = ((newFareRounded - binding.etAddPercentage.text.toString().toInt()).toString())
                                                }
                                            } else {
                                                it.fare = it.fare
                                            }
                                        }
                                    }
                                }
                            } else {
                                selectedSeatList.forEachIndexed { i, e ->
                                    if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                        if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                            it.fare = ((newFareRounded - binding.etAddPercentage.text.toString().toInt()).toString())
                                        }
                                    } else {
                                        it.fare = it.fare
                                    }
                                }
                            }
                        }
                        else {
                            if (binding.etAddPercentage.text.toString().isNotEmpty()) {
                                it.fare = ((newFareRounded - binding.etAddPercentage.text.toString().toInt()).toString())
                            }
                        }
                    }
                }
            }

            val cityWiseFare = CityWiseFare(
                outerLoop.origin_id,
                outerLoop.fareDetails,
                outerLoop.destination_id
            )

            cityWiseFareList.add(cityWiseFare)
        }
    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun openFromDateDialog() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
                val date = dateFormat.parse("$dayOfMonth-${monthOfYear + 1}-$year")
                binding.etFromDateUrc.setText(dateFormat.format(date).toString())

                fromDate = binding.etFromDateUrc.text.toString()

                fromDateDDMMYYYY = fromDate
                val parser = SimpleDateFormat("dd-MM-yyyy")
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                fromDate = formatter.format(parser.parse(fromDate))
                toDate = ""
                binding.etToDateUrc.setText("")
                binding.etToDateUrc.clearFocus()
            }

        setDateLocale(locale!!, requireContext())
        val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
        val dateInString: String = getTodayDate()
        val simpleDateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
        val calendar = Calendar.getInstance()
        calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale("es", "chile"))
        calendar.time = simpleDateFormat.parse(dateInString)!!
        calendar.add(Calendar.DATE, 28)
        dpDialog.datePicker.maxDate = calendar.timeInMillis
        val calendarMinDate = Calendar.getInstance()
        calendarMinDate.time = simpleDateFormat.parse(dateInString)!!
        dpDialog.datePicker.minDate = calendarMinDate.timeInMillis
        dpDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun openToDateDialog() {
        if (fromDate.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
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
                        binding.btnSaveModifyService.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                            binding.btnSaveModifyService.isEnabled = true
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

    @SuppressLint("SetTextI18n")
    private fun openMultipleDateDialog() {

        val dialog = Dialog(requireContext())
        dialog.setCancelable(true)
        val dialogBinding = DialogCalendarBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        val calendar= Calendar.getInstance()
        calendar.add(Calendar.DATE,-1)
        dialogBinding.calendarView.setMinimumDate(calendar)

        val calendars: MutableList<Calendar> = ArrayList()
        daysTempList!!.clear()
        for (i in 0 until dayList!!.size) {
            calendars.add(i, dayList!![i].calendar)
            daysTempList!!.add(dayList!![i])
        }


        dialogBinding.calendarView.selectedDates = calendars.toImmutableList()
        dialogBinding.calendarView.selectedDates = calendars

        dialogBinding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                if (daysTempList!!.contains(eventDay)) {
                    daysTempList!!.remove(eventDay)
                } else {
                    if (daysTempList!!.size < 30) {
                        daysTempList!!.add(eventDay)
                    } else {
                        requireActivity().toast(requireContext().getString(R.string.can_not_select_more_then_30_seats))
                        daysTempList!!.remove(eventDay)
                    }
                }
            }
        })

        dialogBinding.cancelTV.setOnClickListener {
            dialog.cancel()
        }

        dialogBinding.doneTV.setOnClickListener {
            val dates: ArrayList<String> = arrayListOf()
            val datesYY: ArrayList<String> = arrayListOf()
            binding.etMultipleDateUrc.setText("")
            selectedMultipleDate = ""
            dialog.cancel()
            dayList?.clear()
            if (daysTempList?.size!! > 0) {
                for (i in 0 until daysTempList!!.size) {

                    val format1 = SimpleDateFormat("dd-MM-yyyy")
                    val format2 = SimpleDateFormat("yyyy-MM-dd")
                    var inActiveDate: String? = null
                    var inActiveDateYYYY: String? = null

                    try {
                        inActiveDate = format1.format(daysTempList!![i].calendar.time)
                        inActiveDateYYYY = format2.format(daysTempList!![i].calendar.time)
                        println(inActiveDate)
                    } catch (e1: ParseException) {
                        e1.printStackTrace()
                    }
                    dates.add(inActiveDate!!)
                    datesYY.add(inActiveDateYYYY!!)
                    dayList!!.add(daysTempList!![i])

                }
            }

            if (dates.size > 2) {
                val fontSize = PreferenceUtils.getTextSize(requireContext())

                if (fontSize == LARGE_TEXT_SIZE || fontSize == XLARGE_TEXT_SIZE) {
                    binding.etMultipleDateUrc.setText(dates[0])
                    binding.moreTV.text = "+" + (dates.size - 1).toString() + " more"
                } else {
                    binding.etMultipleDateUrc.setText(dates[0] + ", " + dates[1])
                    binding.moreTV.text = "+" + (dates.size - 2).toString() + " more"
                }

                binding.moreTV.visible()
//                binding.layoutMultipleDateUrc.apply {
//                    visibility = View.VISIBLE
//                    text = dates[0] + ", " + dates[1]
//                }
                binding.layoutMultipleDateUrc.visibility = View.VISIBLE
            } else {
                binding.moreTV.gone()
                binding.moreTV.text = ""
                for (i in 0 until dates.size) {
                    if (binding.etMultipleDateUrc.text.toString().isEmpty()) {
                        binding.etMultipleDateUrc.setText(dates[i])
//                        selectedMultipleDate = datesYY[i]
                    } else {
                        binding.etMultipleDateUrc.setText(binding.etMultipleDateUrc.text.toString() + ", " + dates[i])
//                        selectedMultipleDate = binding.etMultipleDateUrc.text.toString() + ", " + datesYY[i]
                    }
                }
            }

            for (i in 0 until datesYY.size) {
                selectedMultipleDate += if (i == 0) {
                    datesYY[i]
                } else {
                    ",${datesYY[i]}"
                }
            }

//            Timber.d("dateXYZ---- $selectedMultipleDate---- ${datesYY.size}")

            if (dates.size == 0) {
                binding.etMultipleDateUrc.setText("")
                selectedMultipleDate = ""
            }

            if (selectedMultipleDate.isNotEmpty() && binding.etTemplateName.text.toString().isNotEmpty()) {
                binding.btnSaveAsFareTemplate.apply {
                    setBackgroundResource(R.drawable.button_selected_bg)
                    isEnabled = true
                    this.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                }

            } else {
                binding.btnSaveAsFareTemplate.apply {
                    setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                    isEnabled = true
                    this.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                }
            }
        }
    }

    private fun setManageFareObserver() {
        pickUpChartViewModel.updateRateCardFareResponse.observe(viewLifecycleOwner) {
//            Timber.d("reservationbFare ${it}")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        convertedDate = PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate)).toString()
                        PreferenceUtils.setPreference(PREF_TRAVEL_DATE, getDateDMY(convertedDate!!)!!)
//                        Timber.d("list-branch - $branchCityWiseFareList")
//                        Timber.d("list-ota- $otaCityWiseFareList")
//                        Timber.d("list-eBooking - $eBookingCityWiseFareList")
                        if (allowBookingTypeFare=="true"){
                            if (copied1.isNotEmpty() || copied2.isNotEmpty() || copied3.isNotEmpty()) {
                                branchCityWiseFareList.clear()
                                onlineCityWiseFareList.clear()
                                otaCityWiseFareList.clear()
                                eBookingCityWiseFareList.clear()
                                branchId=0
                                onlineAgentId=0
                                otasId=0
                                eBookingId=0
                            } else {
                                if (branchId == 1) {
                                    branchCityWiseFareList.clear()

                                    branchId = 0
                                    if (onlineCityWiseFareList.isNotEmpty()) {
                                        if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                            DialogUtils.showFullHeightPinInputBottomSheet(
                                                activity = requireActivity(),
                                                fragmentManager = childFragmentManager,
                                                pinSize = pinSize,
                                                getString(R.string.online_agent),
                                                onPinSubmitted = { pin: String ->
                                                    callManageFareOnlineAgentApi(pin)
                                                    dismissProgressDialog()
                                                },
                                                onDismiss = {
                                                    dismissProgressDialog()
                                                }
                                            )
                                        } else {
                                            callManageFareOnlineAgentApi("")
                                            dismissProgressDialog()
                                        }
                                    } else if (otaCityWiseFareList.isNotEmpty()) {
                                        if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                            DialogUtils.showFullHeightPinInputBottomSheet(
                                                activity = requireActivity(),
                                                fragmentManager = childFragmentManager,
                                                pinSize = pinSize,
                                                getString(R.string.otas),
                                                onPinSubmitted = { pin: String ->
                                                    callManageFareOTAApi(pin)
                                                    dismissProgressDialog()
                                                },
                                                onDismiss = {
                                                    dismissProgressDialog()
                                                }
                                            )
                                        } else {
                                            callManageFareOTAApi("")
                                            dismissProgressDialog()
                                        }
                                    } else if (eBookingCityWiseFareList.isNotEmpty()) {
                                        if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                            DialogUtils.showFullHeightPinInputBottomSheet(
                                                activity = requireActivity(),
                                                fragmentManager = childFragmentManager,
                                                pinSize = pinSize,
                                                getString(R.string.e_booking),
                                                onPinSubmitted = { pin: String ->
                                                    callManageFareEBookingApi(pin)
                                                    dismissProgressDialog()
                                                },
                                                onDismiss = {
                                                    dismissProgressDialog()
                                                }
                                            )
                                        } else {
                                            callManageFareEBookingApi("")
                                            dismissProgressDialog()
                                        }
                                    }

                                } else if (onlineAgentId == 2) {
                                    onlineCityWiseFareList.clear()
                                    onlineAgentId = 0
                                    if (otaCityWiseFareList.isNotEmpty()) {
                                        if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                            DialogUtils.showFullHeightPinInputBottomSheet(
                                                activity = requireActivity(),
                                                fragmentManager = childFragmentManager,
                                                pinSize = pinSize,
                                                getString(R.string.otas),
                                                onPinSubmitted = { pin: String ->
                                                    callManageFareOTAApi(pin)
                                                    dismissProgressDialog()
                                                },
                                                onDismiss = {
                                                    dismissProgressDialog()
                                                }
                                            )
                                        } else {
                                            callManageFareOTAApi("")
                                            dismissProgressDialog()
                                        }
                                    } else if (eBookingCityWiseFareList.isNotEmpty()) {
                                        if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                            DialogUtils.showFullHeightPinInputBottomSheet(
                                                activity = requireActivity(),
                                                fragmentManager = childFragmentManager,
                                                pinSize = pinSize,
                                                getString(R.string.e_booking),
                                                onPinSubmitted = { pin: String ->
                                                    callManageFareEBookingApi(pin)
                                                    dismissProgressDialog()
                                                },
                                                onDismiss = {
                                                    dismissProgressDialog()
                                                }
                                            )
                                        } else {
                                            callManageFareEBookingApi("")
                                            dismissProgressDialog()
                                        }
                                    }

                                } else if (otasId == 3) {
                                    otasId = 0
                                    otaCityWiseFareList.clear()
                                    if (eBookingCityWiseFareList.isNotEmpty()) {
                                        if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                            DialogUtils.showFullHeightPinInputBottomSheet(
                                                activity = requireActivity(),
                                                fragmentManager = childFragmentManager,
                                                pinSize = pinSize,
                                                getString(R.string.e_booking),
                                                onPinSubmitted = { pin: String ->
                                                    callManageFareEBookingApi(pin)
                                                    dismissProgressDialog()
                                                },
                                                onDismiss = {
                                                    dismissProgressDialog()
                                                }
                                            )
                                        } else {
                                            callManageFareEBookingApi("")
                                            dismissProgressDialog()
                                        }
                                    }

                                } else if (eBookingId == 4) {
                                    eBookingId = 0
                                    eBookingCityWiseFareList.clear()
                                    if (branchCityWiseFareList.isNotEmpty()) {
                                        if (shouldModifyReservation && currentCountry.equals("india", true)) {
                                            DialogUtils.showFullHeightPinInputBottomSheet(
                                                activity = requireActivity(),
                                                fragmentManager = childFragmentManager,
                                                pinSize = pinSize,
                                                getString(R.string.e_booking),
                                                onPinSubmitted = { pin: String ->
                                                    callManageFareBranchApi(pin)
                                                    dismissProgressDialog()
                                                },
                                                onDismiss = {
                                                    dismissProgressDialog()
                                                }
                                            )
                                        } else {
                                            callManageFareBranchApi("")
                                            dismissProgressDialog()
                                        }
                                    }
                                }
                            }
                        } else {
                            branchCityWiseFareList.clear()
                            onlineCityWiseFareList.clear()
                            otaCityWiseFareList.clear()
                            eBookingCityWiseFareList.clear()
                            branchId=0
                            onlineAgentId=0
                            otasId=0
                            eBookingId=0
                        }

                        if (branchCityWiseFareList.isEmpty() && onlineCityWiseFareList.isEmpty()
                            && otaCityWiseFareList.isEmpty() && eBookingCityWiseFareList.isEmpty()
                        ) {

                            dismissProgressDialog()

                            if (isAttachedToActivity()) {
                                it.result?.message?.let { it1 ->
                                    DialogUtils.successfulMsgDialog(
                                        requireContext(), it1
                                    )
                                }
                            }
                            Handler(Looper.getMainLooper()).postDelayed(2400) {
                                activity?.viewModelStore?.clear()
                                singleViewModel.apply {
                                    selectedOriginIdList.value?.clear()
                                    selectedCityPairIdList.value?.clear()
                                    selectedDestinationIdList.value?.clear()
                                }
                                activity?.finish()
                            }
                        }
                    }
                    401 -> {
                       /* DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    else -> {
                        dismissProgressDialog()

                        it.result?.message?.let { it1 -> requireContext().toast(it1) }

                        Handler(Looper.getMainLooper()).postDelayed(1500) {
                            activity?.finish()
                        }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun callBulkManageFareBranchApi(authPin: String) {

        copyFare = CopyFare(
            branch = false,
            api = true,
            ebooking = true,
            online = true
        )

        channelType = ChannelType(
            branch = true,
            api = false,
            ebooking = false,
            online = false
        )

        pickUpChartViewModel.updateRateCardFareApiNew(
            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBodyNew(
                apiKey = loginModelPref.api_key,
                id = resID.toString(),
                routeId = routeId.toString(),
                type = amountType.toString(),
                incOrDec = incOrDec.toString(),
                category = "multistation",
                comment = "n/a",
                fromDate = fromDate ?: "",
                toDate = toDate ?: "",
                multipleDates = binding.etMultipleDateUrc.text.toString(),
                locale = locale.toString(),
                channelType = channelType,
                overrideSeatWiseFare = false,
                copyFare = copyFare,
                cityWiseFare = branchCityWiseFareList,
                isFromMiddleTier = true,
                authPin = authPin
            ),
            manage_fare_method_name
        )
    }

    private fun callManageFareBranchApi(authPin: String) {
        channelType = ChannelType(
            branch = true,
            api = false,
            ebooking = false,
            online = false
        )
        pickUpChartViewModel.updateRateCardFareApiNew(
            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBodyNew(
                apiKey = loginModelPref.api_key,
                id = resID.toString(),
                routeId = routeId.toString(),
                type = singleViewModel.amountTypeBranch.value.toString().ifEmpty {
                    amountType.toString()
                },
                incOrDec = singleViewModel.selectedIncOrDecBranch.value.toString().ifEmpty {
                    incOrDec.toString()
                },
                category = "multistation",
                comment = "n/a",
                fromDate = fromDate ?: "",
                toDate = toDate ?: "",
                multipleDates = binding.etMultipleDateUrc.text.toString(),
                locale = locale.toString(),
                channelType = channelType,
                overrideSeatWiseFare = singleViewModel.includeSeatWiseCheckBranch.value ?: false,
                copyFare = copyFare,
                cityWiseFare = branchCityWiseFareList,
                isFromMiddleTier = true,
                authPin = authPin
            ),
            manage_fare_method_name
        )
    }

    private fun callManageFareOnlineAgentApi(authPin: String) {
        channelType = ChannelType(
            branch = false,
            api = false,
            ebooking = false,
            online = true
        )
        pickUpChartViewModel.updateRateCardFareApiNew(
            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBodyNew(
                apiKey = loginModelPref.api_key,
                id = resID.toString(),
                routeId = routeId.toString(),
                type = singleViewModel.amountTypeOnline.value.toString().ifEmpty {
                    amountType.toString()
                },
                incOrDec = singleViewModel.selectedIncOrDecOnline.value.toString().ifEmpty {
                    incOrDec.toString()
                },
                category = "multistation",
                comment = "n/a",
                fromDate = fromDate ?: "",
                toDate = toDate ?: "",
                multipleDates = binding.etMultipleDateUrc.text.toString(),
                locale = locale.toString(),
                channelType = channelType,
                overrideSeatWiseFare = singleViewModel.includeSeatWiseCheckOnline.value ?: false,
                copyFare = copyFare,
                cityWiseFare = onlineCityWiseFareList,
                isFromMiddleTier = true,
                authPin = authPin
            ),
            manage_fare_method_name
        )
    }

    private fun callManageFareOTAApi(authPin: String) {
        channelType = ChannelType(
            branch = false,
            api = true,
            ebooking = false,
            online = false
        )
        pickUpChartViewModel.updateRateCardFareApiNew(
            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBodyNew(
                apiKey = loginModelPref.api_key,
                id = resID.toString(),
                routeId = routeId.toString(),
                type = singleViewModel.amountTypeOta.value.toString().ifEmpty {
                    amountType.toString()
                },
                incOrDec = singleViewModel.selectedIncOrDecOta.value.toString().ifEmpty {
                    incOrDec.toString()
                },
                category = "multistation",
                comment = "n/a",
                fromDate = fromDate ?: "",
                toDate = toDate ?: "",
                multipleDates = binding.etMultipleDateUrc.text.toString(),
                locale = locale.toString(),
                channelType = channelType,
                overrideSeatWiseFare = singleViewModel.includeSeatWiseCheckOta.value ?: false,
                copyFare = copyFare,
                cityWiseFare = otaCityWiseFareList,
                isFromMiddleTier = true,
                authPin = authPin
            ),
            manage_fare_method_name
        )
    }

    private fun callManageFareEBookingApi(authPin: String) {
        channelType = ChannelType(
            branch = false,
            api = false,
            ebooking = true,
            online = false
        )
        pickUpChartViewModel.updateRateCardFareApiNew(
            com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBodyNew(
                apiKey = loginModelPref.api_key,
                id = resID.toString(),
                routeId = routeId.toString(),
                type = singleViewModel.amountTypeEBooking.value.toString().ifEmpty {
                    amountType.toString()
                },
                incOrDec = singleViewModel.selectedIncOrDecEBooking.value.toString().ifEmpty {
                    incOrDec.toString()
                },
                category = "multistation",
                comment = "n/a",
                fromDate = fromDate ?: "",
                toDate = toDate ?: "",
                multipleDates = binding.etMultipleDateUrc.text.toString(),
                locale = locale.toString(),
                channelType = channelType,
                overrideSeatWiseFare = singleViewModel.includeSeatWiseCheckEBooking.value ?: false,
                copyFare = copyFare,
                cityWiseFare = eBookingCityWiseFareList,
                isFromMiddleTier = true,
                authPin = authPin
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
            apiType = service_details_method, excludePassengerDetails = false
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
        Timber.d("seatId-selected - $seatId")

    }
    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onLeftButtonClick() {
    }

    override fun onRightButtonClick() {
        callCreateFareTemplateApi()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        singleViewModel.apply {
            selectedOriginIdList.value?.clear()
            selectedCityPairIdList.value?.clear()
            selectedDestinationIdList.value?.clear()
        }
    }
}