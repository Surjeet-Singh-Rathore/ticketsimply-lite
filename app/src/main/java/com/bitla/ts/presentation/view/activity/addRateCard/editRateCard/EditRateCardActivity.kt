package com.bitla.ts.presentation.view.activity.addRateCard.editRateCard

import AddRateCardSingleViewModel
import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.view.*
import android.widget.*
import androidx.core.content.*
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.applandeo.materialcalendarview.utils.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.add_rate_card.editRateCard.request.*
import com.bitla.ts.domain.pojo.add_rate_card.editRateCard.request.ApplyFor
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response.*
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.request.*
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.*
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.dialog.DialogUtils.Companion.dismissProgressDialog
import com.bitla.ts.utils.dialog.DialogUtils.Companion.showProgressDialog
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
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
import kotlin.math.*

class EditRateCardActivity : BaseActivity(), DialogButtonListener,
    DialogSingleButtonListener,
    OnItemClickListener, AdapterView.OnItemSelectedListener {

    private var currencySymbol: String = ""
    private lateinit var binding: ActivityCreateRateCardBinding
    private val addRateCardViewModel by viewModel<AddRateCardViewModel<Any?>>()

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String? = null
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
    private var currency = ""
    // calendar
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var mcalendar: Calendar

    private var viewRateCardFareResponse: ViewRateCardResponse? = null
    private val addRateCardSingleViewModel by viewModel<AddRateCardSingleViewModel<Any?>>()
    private var routeWiseFareDetails = mutableListOf<RouteWiseFareDetail>()
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    // fare
    private var selectedOriginId = ""
    private var selectedDestinationId = ""
    private var seatListFare = mutableListOf<Service>()
    private var seatIdFare = ""
    private var cityFromList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var cityToList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var cityPairList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var cityFromListTemp: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var cityToListTemp: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var cityPairTemp: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private lateinit var spinnerItemsCityPairCityFrom: SpinnerItemsModifyFare
    private lateinit var spinnerItemsCityPairCityTo: SpinnerItemsModifyFare
    private lateinit var spinnerCityPair: SpinnerItemsModifyFare
    private var branchCityWiseFareList: MutableList<CityWiseFare> = mutableListOf()
    private var tempSeatListFare: MutableList<Service> = mutableListOf()   // for duplicate seat
    private var selectedCityPairOriginId: String = ""
    private var selectedCityPairDestinationId: String = ""
    private var selectedSeatList = mutableListOf<Service>()
    private var selectedFromCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var selectedToCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var selectedCityPairList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var updatedFromCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var updatedToCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var multipleHorizontalItemSelectionAdapterFare: MultipleHorizontalItemSelectionAdapter
    private var allowBookingTypeFare: String = ""
    private var isAllowToDoFareCustomizationForSeatTypes: Boolean = false
    // time
    private var farePercentageTime: Int = 0
    private var incOrDecTime: Int = 0
    private var isChkArrival: Boolean = false
    private var isChkDeparture: Boolean = false
    private var isChkBP: Boolean = false
    private var isChkDP: Boolean = false
    private lateinit var applyForList: ApplyFor
    private var hh: String? = null
    private var mm: String? = null
    private var time: String? = null
    // Cmsn
    private lateinit var multipleHorizontalItemSelectionAdapter: MultipleHorizontalItemSelectionAdapter
    private var getSelectedSeatType:String?=null
    private var incOrDecCmsn: Int = 0
    private var amountTypeCmsn: Int = 1
    private var amountTypeTextCmsn: String = ""
    private var seatListCmsn = mutableListOf<Service>()
    private var seatIdCmsn = ""
    private var tempSeatListCmsn: MutableList<Service> = mutableListOf()   // for duplicate

    private var fareData: MutableList<Fare> = mutableListOf()
    private var timeData: Time? = null
    private var commissionData: Commission? = null

    private var isCreateRateCard = false
    private var amountCms = ""
    private var rateCardId = ""
    private var isHideCommissionTab: Boolean? = false
    private var configuredAmountType: String? = null
    
    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun initUI() {
        binding = ActivityCreateRateCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        init()
        setFareAmountTypeNdIncDecOptions()
        binding.tvIncPercentage.text = "${getString(R.string.increase)} (%)"
        binding.layoutAddpercentage.hint = "${getString(R.string.add_lowercase)} (%)"

        binding.etFromDateUrc.setOnClickListener {
            openFromDateDialog()
        }
        binding.etToDateUrc.setOnClickListener {
            openToDateDialog()
        }

        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()

        PreferenceUtils.apply {
            routeId = getString(getString(R.string.routeId))
            resID = getString(getString(R.string.updateRateCard_resId))
            source = getString(getString(R.string.updateRateCard_origin)).toString()
            destination = getString(getString(R.string.updateRateCard_destination)).toString()
            sourceId = getString(getString(R.string.updateRateCard_originId)).toString()
            destinationId = getString(getString(R.string.updateRateCard_destinationId)).toString()
            busType = getString(getString(R.string.updateRateCard_busType))
            convertedDate = getString(getString(R.string.updateRateCard_travelDate)) ?: ""
        }
        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.edit_rate_cardX)
        binding.updateRatecardToolbar.headerTitleDesc.text = busType

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase()

            currency = privilegeResponseModel?.currency ?:""

            privilegeResponseModel?.let {
                if (privilegeResponseModel?.allowToDoFareCustomizationForSeatTypes == true) {
                    binding.layoutCityFare.visible()
                    isAllowToDoFareCustomizationForSeatTypes = true
                } else {
                    binding.layoutCityFare.gone()
                    isAllowToDoFareCustomizationForSeatTypes = false
                }

                if (it.country.equals("india", true)) {
                    binding.tvSeatTypeTime.visible()
                    binding.seatTypeContainerTime.visible()
                    binding.seatTypeContainer2Time.visible()
                } else {
                    binding.tvSeatTypeTime.gone()
                    binding.seatTypeContainerTime.gone()
                    binding.seatTypeContainer2Time.gone()
                }
                
                if (privilegeResponseModel?.hideCommissionAndTieupCommissionInRouteLevel!=null) {
                    isHideCommissionTab = privilegeResponseModel?.hideCommissionAndTieupCommissionInRouteLevel
                }
                
                configuredAmountType = it.configuredAmountType
                
            }
        }
        
        if (isHideCommissionTab == false) {
            binding.parentLayoutCms.visible()
        } else {
            binding.parentLayoutCms.gone()
        }
        
        
        allowBookingTypeFare = PreferenceUtils.getString(getString(R.string.updateRateCard_allow_booking_type_fare)).toString()
        rateCardId = intent.getStringExtra(getString(R.string.rate_card_id)) ?:""
//        Timber.d("isCreateRateCard- $isCreateRateCard")

        callViewRateCardApi(rateCardId)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun init() {
        showProgressDialog(this)
        getPref()
        onClick()
        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)

//        if (!convertedDate.isNullOrEmpty()) {
//            fromDateDDMMYYYY = convertDateYYYYMMDDtoDDMMYY(convertedDate.toString())
//            val parser = SimpleDateFormat("dd-MM-yyyy")
//            val formatter = SimpleDateFormat("yyyy-MM-dd")
//            fromDate = formatter.format(parser.parse(fromDateDDMMYYYY)!!)
//            toDate = formatter.format(parser.parse(fromDateDDMMYYYY))
//            binding.etFromDateUrc.setText(fromDateDDMMYYYY)
//            binding.etToDateUrc.setText(fromDateDDMMYYYY)
//        }

        setCommissionData()
        setViewRateCardObserver()
        setEditRateCardApiObserver()

        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            addRateCardViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    private fun setTimeData() {
        selectedApplyForImpTime()

        binding.incFareTime.setOnClickListener {
            incOrDecTime = 0
        }

        binding.decFareTime.setOnClickListener {
            incOrDecTime = 1
        }

        binding.etHour.setAdapter(
            ArrayAdapter<String>(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                this.resources.getStringArray(R.array.hourArray)
            )
        )

        binding.etMinute.setAdapter(
            ArrayAdapter<String>(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                this.resources.getStringArray(R.array.minuteArray)
            )
        )

        binding.etHour.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {

//                    binding.etHour.filters = arrayOf<InputFilter>(InputFilterMinMax("1", "24"))

                    binding.btnSaveModifyService.apply {
                        setBackgroundResource(R.drawable.button_selected_bg)
                        isEnabled = true
                    }
                } else {
                    if (binding.etAmountCmsn.text.toString().isEmpty()
                        && branchCityWiseFareList.isEmpty()
                    ) {
                        binding.btnSaveModifyService.apply {
                            setBackgroundResource(R.drawable.button_default_bg)
                            isEnabled = true
                        }
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

                    binding.btnSaveModifyService.apply {
                        setBackgroundResource(R.drawable.button_selected_bg)
                        isEnabled = true
                    }
                } else {
                    if (binding.etAmountCmsn.text.toString().isEmpty()
                        && branchCityWiseFareList.isEmpty()
                    ) {
                        binding.btnSaveModifyService.apply {
                            setBackgroundResource(R.drawable.button_default_bg)
                            isEnabled = true
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun setCommissionData () {
        binding.percentageRadioCms.setOnClickListener {
            binding.etAmountCmsn.setText("")
            amountTypeCmsn = 1
            amountTypeTextCmsn = "(%)"
            binding.layoutAmountCmsn.hint="${getString(R.string.amount)} $amountTypeTextCmsn"
            binding.etAmountCmsn.setMaxLength(2)
        }

        binding.fixedRadioCms.setOnClickListener {
            binding.etAmountCmsn.setText("")
            binding.etAmountCmsn.setText("")
            amountTypeCmsn = 2
            amountTypeTextCmsn = "($currency)"
            binding.layoutAmountCmsn.hint="${getString(R.string.amount)} $amountTypeTextCmsn"
            maxDigitPreventAfterDecimal(binding.etAmountCmsn)
        }

        binding.incFareCms.setOnClickListener {
            incOrDecCmsn = 0
        }

        binding.decFareCms.setOnClickListener {
            incOrDecCmsn = 1
        }

        binding.selectSeatTypeCms.setOnItemClickListener { parent, view, position, id ->

            getSelectedSeatType = parent.getItemAtPosition(position).toString()
        }

        binding.etAmountCmsn.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    binding.btnSaveModifyService.apply {
                        setBackgroundResource(R.drawable.button_selected_bg)
                        isEnabled = true
                    }
                } else {
                    if (binding.etMinute.text.toString().isEmpty()
                        && binding.etHour.text.toString().isEmpty()
                        && branchCityWiseFareList.isEmpty()
                    ) {
                        binding.btnSaveModifyService.apply {
                            setBackgroundResource(R.drawable.button_default_bg)
                            isEnabled = true
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.chkSelectAllCms.setOnClickListener {

            seatIdCmsn = ""

            if (binding.chkSelectAllCms.isChecked) {
                seatListCmsn.forEach {
                    it.isSeatChecked = true
                }
                binding.chkSelectAllCms.text = "All"
            } else {
                seatListCmsn.forEach {
                    it.isSeatChecked = false
                }
                binding.chkSelectAllCms.text = "All"
            }

            multipleHorizontalItemSelectionAdapter.addData(seatListCmsn)
        }
    }
    private fun selectedApplyForImpTime() {
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
    
    
    private fun setPercentageRadio() {
        binding.apply {
            etAddPercentage.setText("")
            etAddPercentage.setMaxLength(2)
            amountType = 1
            amountTypeText = "(%)"
            
            if (incOrDec == 0) {
                tvIncPercentage.text = "${getString(R.string.increase)} $amountTypeText"
                layoutAddpercentage.hint = "${getString(R.string.add_lowercase)} $amountTypeText"
            } else {
                tvIncPercentage.text = "${getString(R.string.decrease)} $amountTypeText"
                layoutAddpercentage.hint = "${getString(R.string.add_lowercase)} $amountTypeText"
            }
            
            etAddPercentage.setMaxLength(2)
            percentageRadio.isChecked = true
            fixedRadio.isChecked = false
        }
    }
    
    private fun setFixedRadio() {
        binding.apply {
            etAddPercentage.setText("")
            amountType = 2
            amountTypeText = "(${currencySymbol})"
            
            if (incOrDec == 0) {
                tvIncPercentage.text = "${getString(R.string.increase)} $amountTypeText"
                layoutAddpercentage.hint = "${getString(R.string.add_lowercase)} $amountTypeText"
            } else {
                tvIncPercentage.text = "${getString(R.string.decrease)} $amountTypeText"
                layoutAddpercentage.hint = "${getString(R.string.add_lowercase)} $amountTypeText"
            }
            
            maxDigitPreventAfterDecimal(binding.etAddPercentage)
            percentageRadio.isChecked = false
            fixedRadio.isChecked = true
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun setFareAmountTypeNdIncDecOptions() {

        
        binding.apply {
            
            if (configuredAmountType.equals("fixed", true)) {
                setFixedRadio()
            } else {
                setPercentageRadio()
            }
            
            // ------------------------------------------
            
            percentageRadio.setOnClickListener {
                setPercentageRadio()
            }
            
            fixedRadio.setOnClickListener {
                setFixedRadio()
            }
            
            

            incDecFare.setOnClickListener {
                incOrDec = 0
                tvIncPercentage.text = "${getString(R.string.increase)} $amountTypeText"
            }

            decFare.setOnClickListener {
                incOrDec = 1
                tvIncPercentage.text = "${getString(R.string.decrease)} $amountTypeText"

            }

            etRateCardName.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etAddPercentage.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotEmpty()) {
                        btnApply.apply {
                            setBackgroundResource(R.drawable.button_selected_bg)
                            isEnabled = true
                            this.setTextColor(
                                ContextCompat.getColor(
                                    this@EditRateCardActivity, R.color.white
                                )
                            )
                        }

                    } else {
                        btnApply.apply {
                            setBackgroundResource(R.drawable.bg_blue_stroke_white_little_round)
                            isEnabled = true
                            this.setTextColor(
                                ContextCompat.getColor(
                                    this@EditRateCardActivity,
                                    R.color.colorPrimary
                                )
                            )
                        }
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }


        if (isNetworkAvailable())
            callServiceApi()
        else noNetworkToast()
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
        this@EditRateCardActivity,
        R.layout.spinner_dropdown_item_witch_checkbox,
        R.id.tvItem,
        cityFromList
        )
        )

        binding.toCityET.setAdapter(
        ArrayAdapter(
        this@EditRateCardActivity,
        R.layout.spinner_dropdown_item,
        R.id.tvItem,
        cityToList
        )
        )

        binding.cityPairET.setAdapter(
        ArrayAdapter(
        this@EditRateCardActivity,
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
        viewRateCardFareResponse!!
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
            SelectUserTypeModifyFareAdapter(this@EditRateCardActivity,
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

                       /* cityPairList.forEachIndexed { index, spinnerItems2 ->
//                            Timber.d("selectedList_from - ${spinnerItems2.id.substringBefore("-")} -- ${item.value}")

                            if (spinnerItems2.id.substringBefore("-") == item.id) {
                                spinnerCityPair = SpinnerItemsModifyFare(
                                    id = spinnerItems2.id,
                                    value = spinnerItems2.value,
                                )
                                updatedFromCityList.add(spinnerCityPair)
                            }
                        }

                        cityPairAdapter(updatedFromCityList)*/

                        if(selectedToCityList.isNotEmpty()){
                            val list = getSelectedCityPairs(selectedFromCityList,selectedToCityList)
                            cityPairAdapter(list)
                        }else{
                            cityPairList.forEachIndexed { index, spinnerItems2 ->
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

                        addRateCardSingleViewModel.setSelectedOriginIdList(selectedFromCityList)
                    }

                    override fun onDeselect(position: Int, item: SpinnerItemsModifyFare) {

                        Timber.d("From Deselect")

                        if (selectedFromCityList.contains(item)){
                            selectedFromCityList.remove(item)
                        }

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
                                cityPairAdapter(list)
                            }else{
                                cityPairList.forEachIndexed { index, spinnerItems2 ->
                                    if (spinnerItems2.id.substringBefore("-") == item.id) {
                                        spinnerCityPair = SpinnerItemsModifyFare(
                                            id = spinnerItems2.id,
                                            value = spinnerItems2.value,
                                        )
                                        updatedFromCityList.remove(spinnerCityPair)
                                    }
                                }
                                cityPairAdapter(updatedFromCityList)
                            }
                        }
                        addRateCardSingleViewModel.setSelectedOriginIdList(selectedFromCityList)
                        invalidateFromCityCount()
                    }
                }
            )
        )
    }

    private fun cityToCityAdapter(toCityList: MutableList<SpinnerItemsModifyFare>){
        binding.toCityET.onItemSelectedListener = this

        binding.toCityET.setAdapter(
            SelectUserTypeModifyFareAdapter(this@EditRateCardActivity,
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

                      /*  cityPairList.forEachIndexed { index, spinnerItems2 ->
//                            Timber.d("selectedList_from - ${spinnerItems2.id.substringBefore("-")} -- ${item.value}")

                            if (spinnerItems2.id.substringAfter("-") == item.id) {
                                spinnerCityPair = SpinnerItemsModifyFare(
                                    id = spinnerItems2.id,
                                    value = spinnerItems2.value,
                                )
                                updatedToCityList.add(spinnerCityPair)
                            }
                        }
                        cityPairAdapter(updatedToCityList)*/

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
                        addRateCardSingleViewModel.setSelectedDestinationIdList(selectedToCityList)
                    }

                    override fun onDeselect(position: Int, item: SpinnerItemsModifyFare) {
                        Timber.d("To Deselect")

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
                        } else {
                            binding.cityPairET.setText("")
                            if(selectedFromCityList.isNotEmpty()){
                                val list = getSelectedCityPairs(selectedFromCityList,selectedToCityList)
                                cityPairAdapter(list)
                            }else{
                                cityPairList.forEachIndexed { index, spinnerItems2 ->

                                    if (spinnerItems2.id.substringAfter("-") == item.id) {
                                        spinnerCityPair = SpinnerItemsModifyFare(
                                            id = spinnerItems2.id,
                                            value = spinnerItems2.value,
                                        )

                                        updatedToCityList.remove(spinnerCityPair)
                                    }
                                }

                                cityPairAdapter(updatedToCityList)
                            }
                        }
                        addRateCardSingleViewModel.setSelectedDestinationIdList(selectedToCityList)
                    }
                }
            )
        )
    }

    fun getSelectedCityPairs(fromCityList : MutableList<SpinnerItemsModifyFare>,toCityList: MutableList<SpinnerItemsModifyFare>): ArrayList<SpinnerItemsModifyFare> {

        var tempFromCityList : ArrayList<SpinnerItemsModifyFare> = arrayListOf()
        var tempToCityList : ArrayList<SpinnerItemsModifyFare> = arrayListOf()
        var finalPairCityList : ArrayList<SpinnerItemsModifyFare> = arrayListOf()

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
            SelectUserTypeModifyFareAdapter(this@EditRateCardActivity,
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
                        addRateCardSingleViewModel.setSelectedCityPairIdList(selectedCityPairList)
                    }

                    override fun onDeselect(position: Int, item: SpinnerItemsModifyFare) {
                        if (selectedCityPairList.contains(item))
                            selectedCityPairList.remove(item)

                        if (addRateCardSingleViewModel.selectedOriginIdList.value?.size==1 && addRateCardSingleViewModel.selectedDestinationIdList.value?.size==1) {
                            selectedCityPairList.clear()
                        }

                        binding.cityPairET.setText(
                            selectedCityPairList.firstOrNull().toString().replace("null", "")
                        )

                        if (selectedCityPairList.size == 0) {
                            binding.cityPairET.isFocusable = false
                        }
                        invalidateCityPairCount()
                        addRateCardSingleViewModel.setSelectedCityPairIdList(selectedCityPairList)
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
        //updatedFromCityList.clear()
        updatedToCityList.clear()
        selectedToCityList.clear()
        //selectedFromCityList.clear()
        //binding.fromCityET.setText("")
        binding.toCityET.setText("")
    }

    @SuppressLint("SetTextI18n")
    private fun setBranchChannelState() {
        if (addRateCardSingleViewModel.amountTypeBranch.value == 1) {
            binding.percentageRadio.isChecked = true
            binding.fixedRadio.isChecked = false
            amountType = 1
        } else {
            binding.fixedRadio.isChecked = true
            binding.percentageRadio.isChecked = false
            amountType = 2
        }

        if (addRateCardSingleViewModel.incDecAmountBranch.value?.isNotEmpty() == true) {
            binding.etAddPercentage.setText("${addRateCardSingleViewModel.incDecAmountBranch.value}")
        } else {
            binding.etAddPercentage.setText("")
        }

        if (addRateCardSingleViewModel.selectedIncOrDecBranch.value == 0) {
            incOrDec = 0
            binding.tvIncPercentage.text =
                "${getString(R.string.increase)} $amountTypeText"
            binding.incDecFare.isChecked = true
            binding.decFare.isChecked = false
        } else {
            incOrDec = 1
            binding.decFare.isChecked = true
            binding.incDecFare.isChecked = false
            binding.tvIncPercentage.text =
                "${getString(R.string.decrease)} $amountTypeText"
        }
    }

    private fun callViewRateCardApi(rateCardIdX: String) {

        if (isNetworkAvailable()) {
            addRateCardViewModel.viewRateCardApi(
                ViewRateCardReqBody(
                    apiKey = loginModelPref.api_key,
                    rateCardId = rateCardIdX,
                    locale = locale ?: ""
                ),
            )
        } else noNetworkToast()
    }


    private fun callEditRateCardApi() {

        // fare
        if (binding.tvModifiedChannelLabel1.isVisible){
            val fareModel = Fare(
                category = "fare",
                cityWiseFare = branchCityWiseFareList,
                type = amountType.toString()
            )

            fareData.add(fareModel)
        }

        // time
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

        applyForList =
            ApplyFor(
                arrival = isChkArrival,
                bp = isChkBP,
                departure = isChkDeparture,
                dp = isChkDP
            )

        hh = binding.etHour.text.toString()
        mm = binding.etMinute.text.toString()
        time = "$hh:$mm"

        if (hh.toString().isNotEmpty() || mm.toString().isNotEmpty()) {
            timeData = Time(
                applyFor = applyForList,
                category = "time",
                incOrDec = incOrDecTime.toString(),
                time = time.toString()
            )
        }

        // commission
        amountCms = binding.etAmountCmsn.text.toString()

        if (amountCms.isNotEmpty()) {
            commissionData =
                Commission(
                    category = "cmsn",
                    cmsn = amountCms,
                    incOrDec = incOrDecCmsn.toString(),
                    seatTypes = seatIdCmsn,
                    type = amountTypeCmsn.toString()
                )
        }


        addRateCardViewModel.editRateCardApi(
            EditRateCardReqBody(
                apiKey = loginModelPref.api_key,
                rateCardName = binding.etRateCardName.text.toString(),
                routeId = routeId.toString(),
                rateCardId = rateCardId,
                fare = fareData ,
                time = timeData,
                commission = commissionData,
                fromDate = fromDate ?: "",
                toDate = toDate ?: "",
                locale = locale.toString(),
                isFromMiddleTier = true
            )
        )
    }

    private fun setEditRateCardApiObserver() {
        addRateCardViewModel.editRateCardResponse.observe(this) {
            dismissProgressDialog()

            if (it != null) {
                when (it.code) {
                    200 -> {

                        if (it.result.message?.isNotEmpty() == true) {
                            it.result.message.let { it1 ->
                                DialogUtils.successfulMsgDialog(
                                    this, it1
                                )
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                addRateCardSingleViewModel.apply {
                                    selectedOriginIdList.value?.clear()
                                    selectedCityPairIdList.value?.clear()
                                    selectedDestinationIdList.value?.clear()
                                }
                                finish()
                            }, 2000)
                        } else if (it.message.isNotEmpty() == true) {
                            toast(it.message)

                        } else {
                            toast(getString(R.string.something_went_wrong))
                        }

                    }
                    404 -> {
                        if (it.result.message?.isNotEmpty() == true) {
                            toast(it.result.message)
                        } else {
                            toast(it.message)
                        }
                    }

                    else -> {
                        if (it.result.message.isNotEmpty()) {
                            toast(it.result.message)
                        } else {
                            toast(it.message)
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setViewRateCardObserver() {

        addRateCardViewModel.viewRateCardResponse.observe(this) {

            dismissProgressDialog()
            seatListFare.clear()
            seatListCmsn.clear()
            selectedSeatList.clear()
            tempSeatListFare.clear()
            tempSeatListCmsn.clear()
            binding.chkSelectAll.isChecked = false
            binding.chkSelectAllCms.isChecked = false

            if (it != null) {
                when (it.code) {
                    200 -> {
                        viewRateCardFareResponse = it
                        routeWiseFareDetails = it.routeWiseFareDetails
                        binding.etRateCardName.setText(it.rateCardName)
                        addRateCardSingleViewModel.setBranchViewRateResponse(it)

                        if (it.fromDate.isNotEmpty()) {
                            binding.etFromDateUrc.setText(getDateDMY(it.fromDate))
                            fromDate = it.fromDate
                            fromDateDDMMYYYY = fromDate
                        }
                        if (it.toDate.isNotEmpty()) {
                            binding.etToDateUrc.setText(getDateDMY(it.toDate))
                            toDate = it.toDate
                        }

                        // time
                        val time = it.routeWiseTimeDetails.time.substringBefore(":")
                        val hour = it.routeWiseTimeDetails.time.substringAfter(":")
                        binding.etHour.setText(time)
                        binding.etMinute.setText(hour)

                        setTimeData()
                        setFareAmountTypeNdIncDecOptions()

                        routeWiseFareDetails.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.originId,
                                destinationId = it.destinationId,
                                originName = it.originName,
                                destinationName = it.destinationName,
                                fareDetails = it.fareDetails
                            )
                            branchCityWiseFareList.add(cityWiseFare)
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

                        val uniqueCityFrom = cityFromListTemp.toSet()
                        uniqueCityFrom.forEach {
                            cityFromList.add(it)
                        }

                        val uniqueCityTo = cityToListTemp.toSet()
                        uniqueCityTo.forEachIndexed { index, SpinnerItemsCityPair ->
                            cityToList.add(SpinnerItemsCityPair)
                        }

                        val uniqueCityPair = cityPairTemp.toSet()
                        uniqueCityPair.forEachIndexed { index, SpinnerItemsCityPair ->
                            cityPairList.add(SpinnerItemsCityPair)
                        }

                        selectedOriginId = cityPairList[0].id.toString()
                        selectedDestinationId = cityPairList[0].id.toString()
                        setFilterOptions()

                        // fare seats
                        if (routeWiseFareDetails.isNotEmpty()) {
                            routeWiseFareDetails.forEach { it ->
                                it.fareDetails.forEach {
                                    val seatModel = Service()
                                    seatModel.routeId = it.id.toInt()
                                    seatModel.number = it.seatType
                                    tempSeatListFare.add(seatModel)
                                }
                            }
                        }
                        val uniqueSeatList = tempSeatListFare.toSet()
                        uniqueSeatList.forEach {
                            seatListFare.add(it)
                        }

                        layoutManager = GridLayoutManager(this@EditRateCardActivity, 3)
                        binding.rvSelectSeat.layoutManager = layoutManager
                        multipleHorizontalItemSelectionAdapterFare = MultipleHorizontalItemSelectionAdapter(this@EditRateCardActivity, this)
                        multipleHorizontalItemSelectionAdapterFare.addData(seatListFare)
                        binding.rvSelectSeat.adapter = multipleHorizontalItemSelectionAdapterFare
                        multipleHorizontalItemSelectionAdapterFare.notifyDataSetChanged()

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


                        // cmsn seats
                        if (routeWiseFareDetails.isNotEmpty()) {
                            routeWiseFareDetails.forEach { it ->
                                it.fareDetails.forEach {
                                    val seatModel = Service()
                                    seatModel.routeId = it.id.toInt()
                                    seatModel.number = it.seatType
                                    tempSeatListCmsn.add(seatModel)
                                }
                            }
                        }
                        val uniqueSeatListCmsn = tempSeatListCmsn.toSet()
                        uniqueSeatListCmsn.forEach {
                            seatListCmsn.add(it)
                        }

                        layoutManager = GridLayoutManager(this@EditRateCardActivity, 3)
                        binding.rvSelectSeatCms.layoutManager = layoutManager
                        multipleHorizontalItemSelectionAdapter = MultipleHorizontalItemSelectionAdapter(this@EditRateCardActivity, this)
                        multipleHorizontalItemSelectionAdapter.addData(seatListCmsn)
                        binding.rvSelectSeatCms.adapter = multipleHorizontalItemSelectionAdapter
                        multipleHorizontalItemSelectionAdapter.notifyDataSetChanged()

                        binding.apply {
                            if (privilegeResponseModel?.allowToDoFareCustomizationForSeatTypes == true) {
                                tvSeatTypeCms.visible()
                                chkSelectAllCms.visible()
                                rvSelectSeatCms.visible()
                            } else {
                                tvSeatTypeCms.gone()
                                chkSelectAllCms.gone()
                                rvSelectSeatCms.gone()
                            }
                        }

                    }
                    411 -> {
                        if (it.message != null) {
                            it.message.let { it1 -> this@EditRateCardActivity.toast(it1) }
                        }
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this@EditRateCardActivity,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    else -> {
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> this@EditRateCardActivity.toast(it1) }
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == SELECT_SERVICE_INTENT_REQUEST_CODE) {
            
            branchCityWiseFareList.clear()
            
            addRateCardSingleViewModel.branchViewRateCardLiveData.value?.routeWiseFareDetails?.forEach {
                val cityWiseFare =
                    CityWiseFare(
                        originId = it.originId,
                        destinationId = it.destinationId,
                        originName = it.originName,
                        destinationName = it.destinationName,
                        fareDetails = it.fareDetails
                    )
                branchCityWiseFareList.add(cityWiseFare)
            }
            
            if (branchCityWiseFareList.isNotEmpty()
            ) {
                binding.tvModifiedChannelLabel1.visible()
                binding.tvModifiedChannelLabel1.text = getString(R.string.add_rate_card_fare_modified)
                
                binding.btnSaveModifyService.apply {
                    setBackgroundResource(R.drawable.button_selected_bg)
                    isEnabled = true
                    binding.btnSaveModifyService.isEnabled = true
                }
            }
        }
    }

    private fun setIntent() {
        val intent = Intent(this, ModifyEditRateCardActivity::class.java)

        intent.apply {
            PreferenceUtils.putString(getString(R.string.multistation_fare_response_model), jsonToString(viewRateCardFareResponse ?: ""))
            /*putExtra(
                getString(R.string.multistation_fare_response_model),
                jsonToString(viewRateCardFareResponse ?: "")
            )*/
            
            putExtra(
                getString(R.string.updateRateCard_originId),
                selectedCityPairOriginId.toString()
            )
            putExtra(
                getString(R.string.updateRateCard_destinationId),
                selectedCityPairDestinationId.toString()
            )

            putExtra(
                getString(R.string.from_view_rate_card),
                false
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
            toast(getString(R.string.server_error))
        }

        binding.apply {
            chkSelectAll.setOnClickListener {
                seatIdFare = ""
                if (binding.chkSelectAll.isChecked) {
                    seatListFare.forEach {
                        it.isSeatChecked = true
                    }
                    binding.chkSelectAll.text = "All"
                } else {
                    seatListFare.forEach {
                        it.isSeatChecked = false
                    }
                    binding.chkSelectAll.text = "All"
                }
                multipleHorizontalItemSelectionAdapterFare.addData(seatListFare)
            }

            modifyFareBT.setOnClickListener {

                if (selectedFromCityList.size >= 1 && selectedToCityList.size >= 1) {
                    if (selectedFromCityList[0].value == selectedToCityList[0].value
                        && binding.cityPairET.text.toString().isEmpty()
                    ) {
                        toast(this@EditRateCardActivity.getString(R.string.city_pair_not_found))
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
                            this@EditRateCardActivity,
                            "Amount should not be blank",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        applyAndSaveRouteWiseFare()
                    }
                } else {
                    selectedSeatList.clear()
                    seatListFare.forEachIndexed { i, it ->
                        if (it.isSeatChecked) {
                            seatIdFare += it.routeId.toString().replace(".0", "") + ","

                            val seatModel = Service()
                            seatModel.routeId = it.routeId
                            selectedSeatList.add(seatModel)
                        }
                    }

                    if (seatIdFare.isNotEmpty()) {
                        try {
                            seatIdFare = seatIdFare.substring(0, seatIdFare.lastIndexOf(","))
                        } catch (_:Exception){ }
                    }

//                Timber.d("fareSeatIdList - $seatId")

                    if (seatIdFare.isEmpty()) {
                        Toast.makeText(
                            this@EditRateCardActivity,
                            "Please select at least one seat type",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (binding.etAddPercentage.text.toString().isEmpty()) {
                        Toast.makeText(
                            this@EditRateCardActivity,
                            "Amount should not be blank",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        applyAndSaveRouteWiseFare()
                    }
                }
            }

            imgArrowRateCard.setOnClickListener {
                if (childRateCardDetails.isVisible) {
                    childRateCardDetails.gone()
                    firstV.gone()
                    imgArrowRateCard.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    childRateCardDetails.visible()
                    firstV.visible()
                    imgArrowRateCard.setImageResource(R.drawable.ic_arrow_up_24)
                }
            }

            imgArrowFare.setOnClickListener {
                if (childLayoutFare.isVisible) {
                    childLayoutFare.gone()
                    lineViewFare.gone()
                    imgArrowFare.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    childLayoutFare.visible()
                    lineViewFare.visible()
                    imgArrowFare.setImageResource(R.drawable.ic_arrow_up_24)
                }
            }

            imgArrowTime.setOnClickListener {
                if (childLayoutTime.isVisible) {
                    childLayoutTime.gone()
                    lineViewTime.gone()
                    imgArrowTime.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    childLayoutTime.visible()
                    lineViewTime.visible()
                    imgArrowTime.setImageResource(R.drawable.ic_arrow_up_24)
                }
            }

            imgArrowCms.setOnClickListener {
                if (childLayoutCms.isVisible) {
                    childLayoutCms.gone()
                    lineViewCmsn.gone()
                    imgArrowCms.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    childLayoutCms.visible()
                    lineViewCmsn.visible()
                    imgArrowCms.setImageResource(R.drawable.ic_arrow_up_24)
                }
            }

            btnSaveModifyService.setOnClickListener {

                seatListCmsn.forEach{
                    if (it.isSeatChecked) {
                        seatIdCmsn += it.routeId.toString().replace(".0", "") + ","
                    }
                }

                if (seatIdCmsn.isNotEmpty()) {
                    seatIdCmsn = seatIdCmsn.substring(0, seatIdCmsn.lastIndexOf(","))
                }

                if (isNetworkAvailable()) {
                    callEditRateCardApi()
                    showProgressDialog(this@EditRateCardActivity)

                } else noNetworkToast()

            }
        }
    }


    private fun applyAndSaveRouteWiseFare() {

        incrementAndDecrementFare(branchCityWiseFareList)
        binding.tvModifiedChannelLabel1.visible()
        binding.tvModifiedChannelLabel1.text = getString(R.string.add_rate_card_fare_modified)

        if (branchCityWiseFareList.isNotEmpty()
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
        
        var fareDetailsList = mutableListOf<RouteWiseFareDetail>()
        var selectedCityPairIdList = mutableListOf<SpinnerItemsModifyFare>()
        
        var selectedFromCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
        var selectedToCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
        
        if (addRateCardSingleViewModel.selectedOriginIdList.value?.isNotEmpty() == true) {
            selectedFromCityList = addRateCardSingleViewModel.selectedOriginIdList.value!!
        }
        if (addRateCardSingleViewModel.selectedDestinationIdList.value?.isNotEmpty() == true) {
            selectedToCityList = addRateCardSingleViewModel.selectedDestinationIdList.value!!
        }
        
        if(addRateCardSingleViewModel.branchViewRateCardLiveData.value!!.routeWiseFareDetails.isNotEmpty()){
            fareDetailsList = addRateCardSingleViewModel.branchViewRateCardLiveData.value!!.routeWiseFareDetails
        }
        if (addRateCardSingleViewModel.selectedCityPairIdList.value?.isNotEmpty() == true){
            selectedCityPairIdList = addRateCardSingleViewModel.selectedCityPairIdList.value!!
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
                            if (spinnerItems2.id.substringBefore("-") == it1.originId
                                && spinnerItems2.id.substringAfter("-") == it1.destinationId
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
                    else {
                        if (selectedFromCityList.isNotEmpty()){
                            selectedFromCityList.forEachIndexed { index, spinnerItems2 ->
                                if (spinnerItems2.id.substringBefore("-") == it1.originId
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
                                if (spinnerItems2.id.substringAfter("-") == it1.destinationId) {
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
            
            val cityWiseFare =
                CityWiseFare(
                    originId = it1.originId,
                    destinationId = it1.destinationId,
                    originName = it1.originName,
                    destinationName = it1.destinationName,
                    fareDetails = it1.fareDetails
                )
            
            cityWiseFareList.add(cityWiseFare)
        }
    }

//    private fun incrementAndDecrementFare(cityWiseFareList: MutableList<CityWiseFare>) {
//
//        cityWiseFareList.clear()
//
//        viewRateCardFareResponse?.routeWiseFareDetails?.forEach { outerLoop ->
//            outerLoop.fareDetails.forEach {
//
//                val newFareRounded = it.fare.toString().toDouble().roundToInt()
//
//                if (amountType == 1) {
//                    if (incOrDec == 0) {
//                        if (isAllowToDoFareCustomizationForSeatTypes) {
//                            if (selectedFromCityList.size != 0) {
//
//                                selectedCityPairList.forEachIndexed { index, spinnerItemsCityPair2 ->
//                                    if (outerLoop.originId == spinnerItemsCityPair2.id.substringBefore("-")
//                                        && outerLoop.destinationId == spinnerItemsCityPair2.id.substringAfter("")
//                                    ) {
//                                        selectedSeatList.forEachIndexed { i, e ->
//                                            if (selectedSeatList[i].routeId == it.id.toInt()) {
//                                                if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                                    it.fare = ((newFareRounded + newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
//                                                }
//                                            } else {
//                                                it.fare = it.fare
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                selectedSeatList.forEachIndexed { i, e ->
//                                    if (selectedSeatList[i].routeId == it.id.toInt()) {
//                                        if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                            it.fare = ((newFareRounded + newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
//                                        }
//                                    } else {
//                                        it.fare = it.fare
//                                    }
//                                }
//                            }
//                        } else {
//                            if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                it.fare = ((newFareRounded + newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
//                            }
//                        }
//                    }
//                    // if incOrDec = 1 and amount type = 0
//                    else {
//                        if (isAllowToDoFareCustomizationForSeatTypes) {
//                            if (selectedFromCityList.size != 0) {
//
//                                selectedCityPairList.forEachIndexed { index, spinnerItemsCityPair2 ->
//                                    if (outerLoop.originId == spinnerItemsCityPair2.id.substringBefore("-")
//                                        && outerLoop.destinationId == spinnerItemsCityPair2.id.substringAfter("")
//                                    ) {
//                                        selectedSeatList.forEachIndexed { i, e ->
//                                            if (selectedSeatList[i].routeId == it.id.toInt()) {
//                                                if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                                    it.fare = ((newFareRounded - newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
//                                                }
//                                            } else {
//                                                it.fare = it.fare
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                selectedSeatList.forEachIndexed { i, e ->
//                                    if (selectedSeatList[i].routeId == it.id.toInt()) {
//                                        if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                            it.fare = ((newFareRounded - newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
//                                        }
//                                    } else {
//                                        it.fare = it.fare
//                                    }
//                                }
//                            }
//                        } else {
//                            if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                it.fare = ((newFareRounded - newFareRounded * binding.etAddPercentage.text.toString().toInt() / 100).toString())
//                            }
//                        }
//                    }
//                }
//                // if amount type = 1
//                else {
//                    if (incOrDec == 0) {
//                        if (isAllowToDoFareCustomizationForSeatTypes) {
//                            if (selectedCityPairList.size != 0) {
//
//                                selectedCityPairList.forEachIndexed { index, spinnerItemsCityPair2 ->
//                                    if (outerLoop.originId == spinnerItemsCityPair2.id.substringBefore("-")
//                                        && outerLoop.destinationId == spinnerItemsCityPair2.id.substringAfter("")
//                                    ) {
//                                        selectedSeatList.forEachIndexed { i, e ->
//                                            if (selectedSeatList[i].routeId == it.id.toInt()) {
//                                                if (binding.etAddPercentage.text.toString().isNotEmpty()
//                                                ) {
//                                                    it.fare = ((newFareRounded + binding.etAddPercentage.text.toString().toInt()).toString())
//                                                }
//                                            } else {
//                                                it.fare = it.fare
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            else {
//                                selectedSeatList.forEachIndexed { i, e ->
//                                    if (selectedSeatList[i].routeId == it.id.toInt()) {
//                                        if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                            it.fare = ((newFareRounded + binding.etAddPercentage.text.toString().toInt()).toString())
//                                        }
//                                    } else {
//                                        it.fare = it.fare
//                                    }
//                                }
//                            }
//                        }
//                        else {
//                            if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                it.fare = ((newFareRounded + binding.etAddPercentage.text.toString().toInt()).toString())
//                            }
//                        }
//                    }
//                    else {
//                        if (isAllowToDoFareCustomizationForSeatTypes) {
//                            if (selectedCityPairList.size != 0) {
//
//                                selectedCityPairList.forEachIndexed { index, spinnerItemsCityPair2 ->
//                                    if (outerLoop.originId == spinnerItemsCityPair2.id.substringBefore("-")
//                                        && outerLoop.destinationId == spinnerItemsCityPair2.id.substringAfter("")
//                                    ) {
//                                        selectedSeatList.forEachIndexed { i, e ->
//                                            if (selectedSeatList[i].routeId == it.id.toInt()) {
//                                                if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                                    it.fare = ((newFareRounded - binding.etAddPercentage.text.toString().toInt()).toString())
//                                                }
//                                            } else {
//                                                it.fare = it.fare
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                selectedSeatList.forEachIndexed { i, e ->
//                                    if (selectedSeatList[i].routeId == it.id.toInt()) {
//                                        if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                            it.fare = ((newFareRounded - binding.etAddPercentage.text.toString().toInt()).toString())
//                                        }
//                                    } else {
//                                        it.fare = it.fare
//                                    }
//                                }
//                            }
//                        } else {
//                            if (binding.etAddPercentage.text.toString().isNotEmpty()) {
//                                it.fare = ((newFareRounded - binding.etAddPercentage.text.toString().toInt()).toString())
//                            }
//                        }
//                    }
//                }
//            }
//
//            val cityWiseFare = CityWiseFare(
//                originId = outerLoop.originId,
//                destinationId = outerLoop.destinationId,
//                originName = outerLoop.originName,
//                destinationName = outerLoop.destinationName,
//                fareDetails = outerLoop.fareDetails
//            )
//
//            cityWiseFareList.add(cityWiseFare)
//        }
//    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun openFromDateDialog() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth, ->
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

        setDateLocale(locale!!, this@EditRateCardActivity)
        val dpDialog = DatePickerDialog(this@EditRateCardActivity, listener, year, month, day)
        val dateInString: String = getTodayDate()
        val simpleDateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
        val calendar = Calendar.getInstance()
//        calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale("es", "chile"))
        calendar.time = simpleDateFormat.parse(dateInString)!!
//        calendar.add(Calendar.DATE,28)
//        dpDialog.datePicker.maxDate = calendar.timeInMillis
        val calendarMinDate = Calendar.getInstance()
        calendarMinDate.time = simpleDateFormat.parse(dateInString)!!
        dpDialog.datePicker.minDate = calendarMinDate.timeInMillis
        dpDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun openToDateDialog() {
        if (fromDate.isNullOrEmpty()) {
            Toast.makeText(
                this@EditRateCardActivity,
                getString(R.string.validate_from_date),
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
            setDateLocale(locale!!, this@EditRateCardActivity)
            val dpDialog = DatePickerDialog(this@EditRateCardActivity, listener, year, month, day)
            val dateInString: String = getTodayDate()
            val simpleDateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            val calendar = Calendar.getInstance()
            calendar.time = simpleDateFormat.parse(dateInString)
//            calendar.add(Calendar.DATE, 28)
//            dpDialog.datePicker.maxDate = calendar.timeInMillis
            val calenderMinDate = Calendar.getInstance()
            calenderMinDate.time = simpleDateFormat.parse(dateInString)
            dpDialog.datePicker.minDate = calenderMinDate.timeInMillis
            dpDialog.show()
        }
    }

    private fun callServiceApi() {
        sharedViewModel.getServiceDetails(
            reservationId = resID.toString(),
            apiKey = loginModelPref.api_key,
            originId = sourceId.toString(),
            destinationId = destinationId.toString(),
            operatorApiKey = operator_api_key,
            locale = locale ?: "",
            apiType = service_details_method, excludePassengerDetails = false
        )
        serviceDetailsApiObserver()
    }

    private fun serviceDetailsApiObserver() {

        dismissProgressDialog()

        sharedViewModel.serviceDetails.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        routeId = it.body.routeId.toString()
                        PreferenceUtils.putString(getString(R.string.routeId), routeId)
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this@EditRateCardActivity,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    else -> it.message?.let { it1 ->
                        Toast.makeText(
                            this@EditRateCardActivity,
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else
                toast(getString(R.string.server_error))
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(this@EditRateCardActivity)
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

        // fare seat
        val index = seatListFare.indexOfFirst {
            it.routeId == position
        }
        seatListFare[index].isSeatChecked = data == "true"
        seatListFare.forEach {
            if (!it.isSeatChecked) {
                binding.chkSelectAll.isChecked = false
                binding.chkSelectAll.text = "All"
                return@forEach
            }
        }
        seatIdFare = ""
        Timber.d("seatId-fare-selected - $seatIdFare")


        // cmsn seat
        val indexCmsn = seatListCmsn.indexOfFirst {
            it.routeId == position
        }
        seatListCmsn[indexCmsn].isSeatChecked = data == "true"
        seatListCmsn.forEach {
            if (!it.isSeatChecked) {
                binding.chkSelectAllCms.isChecked = false
                binding.chkSelectAllCms.text = "All"
                return@forEach
            }
        }
        seatIdCmsn = ""
        Timber.d("seatId-Cmsn-selected - $seatIdCmsn")


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
    }
    
    override fun onDestroy() {
        super.onDestroy()
        addRateCardSingleViewModel.apply {
            selectedOriginIdList.value?.clear()
            selectedCityPairIdList.value?.clear()
            selectedDestinationIdList.value?.clear()
        }
    }
}