package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.add_driver.request.*
import com.bitla.ts.domain.pojo.branch_list_model.request.*
import com.bitla.ts.domain.pojo.city_details.request.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.state_details.request.*
import com.bitla.ts.domain.pojo.state_details.request.ReqBody
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import java.util.*

class AddDriverActivity : BaseActivity(), DialogSingleButtonListener {

    private lateinit var activityAddDriverBinding: ActivityAddDriverBinding
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val cityDetailViewModel by viewModel<CityDetailViewModel<Any?>>()
    private val stateDetailViewModel by viewModel<CityDetailViewModel<Any?>>()
    private val addADHOCDriverViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var cityList: MutableList<SpinnerItems> = mutableListOf()
    private var stateList: MutableList<SpinnerItems> = mutableListOf()
    private var driverDetailsList = Driver()

    private lateinit var spinnerItemsCity: SpinnerItems
    private lateinit var spinnerItemsState: SpinnerItems
    private var selectedCityId = 0
    private var selectedStateId = 0
    
    private var accountNumber: String? = null
    private var accountType: String? = null
    private var addressLine1: String? = null
    private var badgeNo: String? = null
    private var bankName: String? = null
    private var beneficiaryName: String? = null
    private var branchName: String? = null
    private var city: String? = null
    private var country: String? = null
    private var dateOfBirth: String? = null
    private var dlExpiryDate: String? = null
    private var driverLicenceIssuingAuthority: String? = null
    private var drivingLicence: String? = null
    private var email: String? = null
    private var esi: String? = null
    private var firstName: String? = null
    private var ifscCode: String? = null
    private var lastName: String? = null
    private var micrCode: String? = null
    private var mobileNumber: String? = null
    private var paymentType: String? = null
    private var pf: String? = null
    private var pinCode: String? = null
    private var sex: String? = null
    private var state: String? = null
    private var travelBranch: String? = null
    private var uanNo: String? = null

    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var mcalendar: Calendar
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private lateinit var apiKey: String
    private var selectedBranchId: String = ""
    private var locale: String? = ""
    private var countryArray = arrayListOf<String?>()
    override fun initUI() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddDriverBinding = ActivityAddDriverBinding.inflate(layoutInflater)
        setContentView(activityAddDriverBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(activityAddDriverBinding.root)
        }

        getPref()
        init()
        lifecycleScope.launch {
            blockViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            cityDetailViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            addADHOCDriverViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
        getPref()
        init()
    }


    private fun init() {

        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)

        activityAddDriverBinding.toolbarPassengerDetails.toolbarTvTitle.text =
            getString(R.string.add_driver)

        val busType = PreferenceUtils.getString("serviceSubToolbar")
        activityAddDriverBinding.toolbarPassengerDetails.toolbarSubtitle.text = busType.toString()

        callBranchListApi()
        setBranchListObserver()
        callCityDetailsApi()
        callStateDetailsApi()
        setCityStateDetailsObserver()
        setAddADHOCDriverObserver()

        with(activityAddDriverBinding) {
            etDob.setOnClickListener {
                openDOBDateDialog()
            }

            etDrivingLicenseExpiry.setOnClickListener {
                openDLExpiryDateDialog()
            }

            toolbarPassengerDetails.imgBack.setOnClickListener {
                onBackPressed()
                PreferenceUtils.removeKey("serviceSubToolbar")

            }

            btnCancelDriverDialog.setOnClickListener {
                cityList.clear()
                stateList.clear()
                finish()
            }
        }

        activityAddDriverBinding.etSex.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                this.resources.getStringArray(R.array.genderArray)
            )
        )

        activityAddDriverBinding.etCountry.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                countryArray
            )
        )

        activityAddDriverBinding.etTravelBranch.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                branchList
            )
        )


        activityAddDriverBinding.etTravelBranch.setOnItemClickListener { _, _, _, _ ->

            for (i in 0 until branchList.size) {
                selectedBranchId = branchList[i].id.toString()
            }
        }

        activityAddDriverBinding.apply {

            btnAddDriver.setOnClickListener {
                firstName = etFirstName.text?.trim().toString()
                lastName = etLastname.text?.trim().toString()
                accountNumber = etAccountNumber.text?.trim().toString()
                accountType = etAccountType.text?.trim().toString()
                addressLine1 = etAddress.text?.trim().toString()
                badgeNo = etBadgeNo.text?.trim().toString()
                bankName = etBankName.text?.trim().toString()
                beneficiaryName = etBenificiaryName.text?.trim().toString()
                branchName = etBranchName.text?.trim().toString()
                dateOfBirth = etDob.text?.trim().toString()
                dlExpiryDate = etDrivingLicenseExpiry.text?.trim().toString()
                driverLicenceIssuingAuthority = etDlAuthority.text?.trim().toString()
                drivingLicence = etDrivingLicense.text?.trim().toString()
                email = etEmail.text?.trim().toString()
                esi = etEsi.text?.trim().toString()
                micrCode = etMICRCode.text?.trim().toString()
                mobileNumber = etPhoneNumber.text?.trim().toString()
                paymentType = etPaymentType.text?.trim().toString()
                pf = etPf.text?.trim().toString()
                pinCode = etPinCode.text?.trim().toString()
                sex = etSex.text?.trim().toString()
                country = etCountry.text?.trim().toString()
                travelBranch = etTravelBranch.text?.trim().toString()
                uanNo = etUanNo.text?.trim().toString()


                if (firstName.isNullOrEmpty()) {
                    activityAddDriverBinding.layoutFirstName.error = getString(R.string.not_filled)
                    activityAddDriverBinding.layoutFirstName.isErrorEnabled = true
                } else {
                    activityAddDriverBinding.layoutFirstName.isErrorEnabled = false

                }
                if (lastName.isNullOrEmpty()) {
                    activityAddDriverBinding.layoutLastName.error = getString(R.string.not_filled)
                    activityAddDriverBinding.layoutLastName.isErrorEnabled = true
                } else {
                    activityAddDriverBinding.layoutLastName.isErrorEnabled = false
                }

                if (sex.isNullOrEmpty()) {
                    activityAddDriverBinding.layoutSex.error = getString(R.string.not_filled)
                    activityAddDriverBinding.layoutSex.isErrorEnabled = true
                } else {
                    activityAddDriverBinding.layoutSex.isErrorEnabled = false
                }

                if (travelBranch.isNullOrEmpty()) {
                    activityAddDriverBinding.layoutTravelBranch.error =
                        getString(R.string.not_filled)
                    activityAddDriverBinding.layoutTravelBranch.isErrorEnabled = true
                } else {
                    activityAddDriverBinding.layoutTravelBranch.isErrorEnabled = false
                }

                if (addressLine1.isNullOrEmpty()) {
                    activityAddDriverBinding.layoutAddress.error = getString(R.string.not_filled)
                    activityAddDriverBinding.layoutAddress.isErrorEnabled = true
                } else {
                    activityAddDriverBinding.layoutAddress.isErrorEnabled = false
                }

                if (etCity.text.isNullOrEmpty()) {
                    activityAddDriverBinding.layoutCity.error = getString(R.string.not_filled)
                    activityAddDriverBinding.layoutCity.isErrorEnabled = true
                } else {
                    activityAddDriverBinding.layoutCity.isErrorEnabled = false
                }

                if (etState.text.isNullOrEmpty()) {
                    activityAddDriverBinding.layoutState.error = getString(R.string.not_filled)
                    activityAddDriverBinding.layoutState.isErrorEnabled = true
                } else {
                    activityAddDriverBinding.layoutState.isErrorEnabled = false
                }

                if (etCountry.text.isNullOrEmpty()) {
                    activityAddDriverBinding.layoutCountry.error = getString(R.string.not_filled)
                    activityAddDriverBinding.layoutCountry.isErrorEnabled = true
                } else {
                    activityAddDriverBinding.layoutCountry.isErrorEnabled = false
                }

                if (etEmail.text.isNullOrEmpty() || !isEmailValid(email)) {
                    activityAddDriverBinding.layoutEmail.error = getString(R.string.not_filled)
                    activityAddDriverBinding.layoutEmail.isErrorEnabled = true
                } else {
                    activityAddDriverBinding.layoutEmail.isErrorEnabled = false
                }

                if (pinCode.isNullOrEmpty() || pinCode.toString().length != 6) {
                    activityAddDriverBinding.layoutPinCode.error = getString(R.string.not_filled)
                    activityAddDriverBinding.layoutPinCode.isErrorEnabled = true
                } else {
                    activityAddDriverBinding.layoutPinCode.isErrorEnabled = false
                }

                if (firstName.isNullOrEmpty()
                    || lastName.isNullOrEmpty()
                    || sex.isNullOrEmpty()
                    || travelBranch.isNullOrEmpty()
                    || addressLine1.isNullOrEmpty()
                    || pinCode.isNullOrEmpty()
                    || etState.text.isNullOrEmpty()
                    || etCity.text.isNullOrEmpty()
                    || etCountry.text.isNullOrEmpty()
                    || email.isNullOrEmpty()
                ) {

                    toast(getString(R.string.mandatory_fields_cant_be_blank))
                } else if (pinCode.toString().length != 6) {
                    toast(getString(R.string.pin_code_length_msg))

                } else if (!isEmailValid(email)) {
                    toast(getString(R.string.email_format))

                } else {
                    driverDetailsList = Driver(
                        firstName = firstName,
                        lastName = lastName,
                        mobileNumber = mobileNumber,
                        email = email,
                        sex = sex,
                        dateOfBirth = dateOfBirth,
                        pinCode = pinCode,
                        esi = esi,
                        pf = pf,
                        uanNo = uanNo,
                        addressLine1 = addressLine1,
                        state = selectedStateId.toString(),
                        city = selectedCityId.toString(),
                        country = country,
                        micrCode = micrCode,
                        accountNumber = accountNumber,
                        accountType = accountType,
                        ifscCode = ifscCode,
                        bankName = bankName,
                        beneficiaryName = beneficiaryName,
                        branchName = branchName,
                        dlExpiryDate = dlExpiryDate,
                        driverLicenceIssuingAuthority = driverLicenceIssuingAuthority,
                        drivingLicence = drivingLicence,
                        badgeNo = badgeNo,
                        travelBranch = selectedBranchId,
                        paymentType = paymentType,
                        responseFormat = response_format
                    )
                    callAddADHOCDriversApi()
                }
            }

        }

        activityAddDriverBinding.etDob.setOnClickListener {
            openDOBDateDialog()
        }

        activityAddDriverBinding.etDrivingLicenseExpiry.setOnClickListener {
            openDLExpiryDateDialog()
        }

        validateMandatoryDriverDetailsWatcher()
    }

    private fun callBranchListApi() {
        if (isNetworkAvailable()) {

            blockViewModel.branchListApi(
                loginModelPref.api_key,
                locale ?: "en",
                branch_list_method_name
            )

        } else
            noNetworkToast()
    }

    private fun setBranchListObserver() {
        blockViewModel.branchList.observe(this) {
            branchList.clear()
            try {
                if (it != null) {
                    if (it.branchlists.isNotEmpty()) {
                        it.branchlists.forEach {
                            val spinnerItems = SpinnerItems(it.id, it.label)
                            branchList.add(spinnerItems)
                        }
                    }
//                    saveBranchList(branchList)
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                toast("An error occurred while fetching Branch List")
            }
        }

    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        apiKey = loginModelPref.api_key

        if (getPrivilegeBase() != null) {
            country = getPrivilegeBase()?.country ?: ""
            countryArray.add(country)
        }
    }

    private fun setCityStateDetailsObserver() {

        cityDetailViewModel.cityDetailResponse.observe(this) {
            if (it != null) {
                if (it.code == 200) {
                    if(!it.result.isNullOrEmpty()) {
                        it.result.forEach {
                            spinnerItemsCity = SpinnerItems(it.id!!, it.name!!)
                            cityList.add(spinnerItemsCity)
                        }
                    }

                    activityAddDriverBinding.apply {
                        etCity.setAdapter(
                            ArrayAdapter(
                                this@AddDriverActivity,
                                R.layout.spinner_dropdown_item,
                                R.id.tvItem,
                                cityList
                            )
                        )
                        etCity.setOnItemClickListener { parent, view, position, id ->
                            selectedCityId = cityList[position].id
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        stateDetailViewModel.stateDetailResponse.observe(this) {
            Timber.d("state> ${it.states}")
            if (it != null) {
                if (it.code == 200) {

                    if(!it.states.isNullOrEmpty()) {
                        it.states.forEach {
                            spinnerItemsState = SpinnerItems(it.id, it.name)
                            stateList.add(spinnerItemsState)
                        }
                    }

                    activityAddDriverBinding.apply {
                        etState.setAdapter(
                            ArrayAdapter(
                                this@AddDriverActivity,
                                R.layout.spinner_dropdown_item,
                                R.id.tvItem,
                                stateList
                            )
                        )

                        etState.setOnItemClickListener { parent, view, position, id ->
                            selectedStateId = stateList[position].id
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

    }

    private fun setAddADHOCDriverObserver() {

        addADHOCDriverViewModel.addADHOCDriverResponse.observe(this) {
            if (it != null) {
                if (it.code == 200) {
                    if (it.message != null) {
                        it.message.let { it -> toast(it) }
                    }
                    finish()
                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

                } else {
                    if (it.message != null) {
                        it.message.let { it -> toast(it) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun validateMandatoryDriverDetailsWatcher() {

        activityAddDriverBinding.apply {

            etFirstName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    firstName = s.toString()

                    if (!firstName.isNullOrEmpty()) {

                        layoutFirstName.isErrorEnabled = false
                    } else {
                        layoutFirstName.error = "Enter fName"
                        layoutFirstName.isErrorEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etLastname.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    lastName = s.toString()

                    if (!lastName.isNullOrEmpty()) {

                        layoutLastName.isErrorEnabled = false
                    } else {
                        layoutLastName.error = "Enter lName"
                        layoutLastName.isErrorEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    email = s.toString()

                    if (!email.isNullOrEmpty()
                        && isEmailValid(email)
                    ) {

                        layoutEmail.isErrorEnabled = false
                    } else {
                        layoutEmail.error = "Enter email"
                        layoutEmail.isErrorEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etSex.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    addressLine1 = s.toString()

                    if (!etSex.text.isNullOrEmpty()) {

                        layoutSex.isErrorEnabled = false
                    } else {
                        layoutSex.error = "Enter state"
                        layoutSex.isErrorEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etCity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                    email=s.toString()

                    if (!etCity.text.isNullOrEmpty()) {

                        layoutCity.isErrorEnabled = false
                    } else {
                        layoutCity.error = "Enter city"
                        layoutCity.isErrorEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etState.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                    email=s.toString()

                    if (!etState.text.isNullOrEmpty()) {

                        layoutState.isErrorEnabled = false
                    } else {
                        layoutState.error = "Enter state"
                        layoutState.isErrorEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etCountry.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                    email=s.toString()

                    if (!etCountry.text.isNullOrEmpty()) {

                        layoutCountry.isErrorEnabled = false
                    } else {
                        layoutCountry.error = "Enter country"
                        layoutCountry.isErrorEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etTravelBranch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                    email=s.toString()

                    if (!etTravelBranch.text.isNullOrEmpty()) {

                        layoutTravelBranch.isErrorEnabled = false
                    } else {
                        layoutTravelBranch.error = "Enter state"
                        layoutTravelBranch.isErrorEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    addressLine1 = s.toString()

                    if (!addressLine1.isNullOrEmpty()) {

                        layoutAddress.isErrorEnabled = false
                    } else {
                        layoutAddress.error = "Enter state"
                        layoutAddress.isErrorEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etPinCode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    pinCode = s.toString()

                    if (!pinCode.isNullOrEmpty() && pinCode.toString().length == 6) {

                        layoutPinCode.isErrorEnabled = false
                    } else {
                        layoutPinCode.error = "Enter pin"
                        layoutPinCode.isErrorEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }

    }

    private fun callCityDetailsApi() {

        cityDetailViewModel.cityDetailAPI(
            apiKey = loginModelPref.api_key,
            responseFormat = response_format,
            locale = locale ?: "en",
            apiType = city_Details_method_name
        )
    }

    private fun callStateDetailsApi() {
        stateDetailViewModel.stateDetailAPI(
            apiKey = loginModelPref.api_key,
            responseFormat = response_format,
            locale = locale ?: "en",
            apiType = state_Details_method_name
        )
    }

    private fun callAddADHOCDriversApi() {
        addADHOCDriverViewModel.addADHOCDriverAPI(
            com.bitla.ts.domain.pojo.add_driver.request.ReqBody(
                apiKey = loginModelPref.api_key,
                driver = driverDetailsList,
                locale = locale
            ),
            add_driver_method_name
        )
    }

    @SuppressLint("SetTextI18n")
    private fun openDOBDateDialog() {
        val listener =
            DatePickerDialog.OnDateSetListener {
                    _, year, monthOfYear, dayOfMonth,
                ->
                activityAddDriverBinding.etDob.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            }
        val dpDialog = DatePickerDialog(this, listener, year, month, day)
        dpDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun openDLExpiryDateDialog() {
        val listener =
            DatePickerDialog.OnDateSetListener {
                    _, year, monthOfYear, dayOfMonth,
                ->
                activityAddDriverBinding.etDrivingLicenseExpiry.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            }
        val dpDialog = DatePickerDialog(this, listener, year, month, day)
        dpDialog.show()
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}