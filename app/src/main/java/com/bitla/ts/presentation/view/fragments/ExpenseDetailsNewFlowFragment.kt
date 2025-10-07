package com.bitla.ts.presentation.view.fragments

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.expenses_details.response.CrewExpense
import com.bitla.ts.domain.pojo.expenses_details.response.GeneralDetails
import com.bitla.ts.domain.pojo.expenses_details.response.VehicleExpense
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.update_expenses_details.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.roundOffDecimal
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.google.android.material.textfield.*
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible

class ExpenseDetailsNewFlowFragment : BaseFragment(), DialogSingleButtonListener,
    View.OnClickListener {

    private lateinit var binding: FragmentExpenseDetailsNewFlowBinding

    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()

    private val LAST_CLOSING_KMS = "last_closing_kms"
    private val OPENING_KMS = "opening_kms"
    private val CLOSING_KMS = "closing_kms"
    private val MISSING_KMS = "missing_kms"
    private val TOTAL_KMS = "total_kms"
    private val NO_OF_LITRES = "no_of_litres"
    private val PRICE_PER_LITRE = "amount_per_liter"
    private val FUEL_VALUE = "total_amount"
    private val MILEAGE = "mileage"

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var resID: Long? = 0L
    private var locale: String = ""

    private var crewDetailsHashMap = HashMap<Int, CrewExpense>()
    private var vehicleDetailsHashMap = HashMap<Int, VehicleExpense>()
    private var generalDetailsHashMap = HashMap<String, GeneralDetails>()

    private var lastClosingKms: GeneralDetails? = null
    private var openingKms: GeneralDetails? = null
    private var closingKms: GeneralDetails? = null
    private var missingKms: GeneralDetails? = null
    private var totalKms: GeneralDetails? = null
    private var noOfLitres: GeneralDetails? = null
    private var pricePerLitre: GeneralDetails? = null
    private var fuelValue: GeneralDetails? = null
    private var mileage: GeneralDetails? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentExpenseDetailsNewFlowBinding.inflate(inflater, container, false)
        initUi()
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        return binding.root
    }

    override fun isInternetOnCallApisAndInitUI() {
        initUi()
    }

    override fun isNetworkOff() {}

    private fun initUi() {
        resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
        getPref()
        setClickListener()
        callExpensesDetailsApi()
        setExpensesDetailsObserver()
        updateExpensesDetailsObserver()
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()

        lifecycleScope.launch {
            val privilege = (activity as BaseActivity).getPrivilegeBaseSafely()
            pickUpChartViewModel.updatePrivileges(privilege)
        }

        pickUpChartViewModel.privilegesLiveData.observe(requireActivity()) {

            if (loginModelPref.role.equals(
                    requireContext().getString(R.string.role_field_officer),
                    true
                )
            ) {
                if (it?.boLicenses?.allowToUpdateVehicleExpenses == true) {
                    binding.generalDetailsCV.visible()
                } else {
                    binding.generalDetailsCV.gone()
                }
            }
        }
    }

    private fun setClickListener() {
        binding.generalDetailsIV.setOnClickListener(this)
        binding.crewExpensesIV.setOnClickListener(this)
        binding.vehicleExpensesIV.setOnClickListener(this)
        binding.btnUpdate.setOnClickListener(this)

        binding.openingKmsET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                openingKms?.value = binding.openingKmsET.text.toString()
                updateGeneralDetailsData(OPENING_KMS)
            }
        }

        binding.closingKmsET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                closingKms?.value = binding.closingKmsET.text.toString()
                updateGeneralDetailsData(CLOSING_KMS)
            }
        }

        binding.noOfLitresET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                noOfLitres?.value = binding.noOfLitresET.text.toString()
                updateGeneralDetailsData(NO_OF_LITRES)
            }
        }

        binding.pricePerLitreET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                pricePerLitre?.value = binding.pricePerLitreET.text.toString()
                updateGeneralDetailsData(PRICE_PER_LITRE)
            }
        }

        binding.mainLayout.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                closeKeyBoard()
                v.clearFocus()
            }
            false
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.generalDetailsIV -> {
                showHideView(binding.generalDetailsLayout, binding.generalDetailsIV)
            }

            R.id.crewExpensesIV -> {
                showHideView(binding.rvCrewExpenses, binding.crewExpensesIV)
            }

            R.id.vehicleExpensesIV -> {
                showHideView(binding.rvVehicleExpenses, binding.vehicleExpensesIV)
            }

            R.id.btnUpdate -> {
                callUpdateExpensesDetailsApi()
            }
        }
    }

    private fun showHideView(view: View, imageView: ImageView) {
        if (view.isVisible) {
            view.gone()
            imageView.setImageResource(R.drawable.ic_arrow_down)
        } else {
            view.visible()
            imageView.setImageResource(R.drawable.ic_arrow_up)
        }
    }

    private fun callExpensesDetailsApi() {
        try {
            pickUpChartViewModel.getExpensesDetails(
                loginModelPref.api_key,
                resID.toString(),
                locale,
                response_format,
                expenses_details_method_name
            )
        } catch (t: Throwable) {
            requireContext().toast(requireContext().getString(R.string.opps))
        }
    }

    private fun setExpensesDetailsObserver() {
        pickUpChartViewModel.expensesDetailsResponse.observe(requireActivity()) { response ->
            try {
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            setCrewExpenseAdapter(response.result.crewExpenses)
                            setVehicleExpenseAdapter(response.result.vehicleExpenses)

                            response.result.crewExpenses.forEachIndexed { index, crewExpense ->
                                crewDetailsHashMap[index] = crewExpense
                            }
                            response.result.vehicleExpenses.forEachIndexed { index, vehicleExpense ->
                                vehicleDetailsHashMap[index] = vehicleExpense
                            }

                            val generalDetailsList = response.result.generalDetails
                            generalDetailsHashMap =
                                generalDetailsList.associateBy { it.key } as HashMap<String, GeneralDetails>

                            setGeneralDetailsData()
                        }

                        401 -> {
                            showUnauthorisedDialog()
                        }

                        else -> {
                            requireContext().toast(response.message)
                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                requireContext().toast(requireContext().getString(R.string.opps))
            }
        }
    }

    private fun setCrewExpenseAdapter(crewExpenses: List<CrewExpense>) {
        try {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.rvCrewExpenses.layoutManager = layoutManager
            val crewExpenseAdapter = CrewExpenseNewFlowAdapter(
                requireActivity(),
                crewExpenses,
                object : CrewExpenseNewFlowAdapter.OnTextChangedListener {
                    override fun onTextChanged(position: Int, expenses: CrewExpense) {
                        crewDetailsHashMap[position] = expenses
                    }
                }
            )
            binding.rvCrewExpenses.adapter = crewExpenseAdapter
        } catch (t: Throwable) {
            requireContext().toast(requireContext().getString(R.string.opps))
        }
    }

    private fun setVehicleExpenseAdapter(vehicleExpenses: List<VehicleExpense>) {
        try {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.rvVehicleExpenses.layoutManager = layoutManager
            val vehicleExpenseAdapter = VehicleExpenseNewFlowAdapter(
                requireActivity(),
                vehicleExpenses,
                object : VehicleExpenseNewFlowAdapter.OnTextChangedListener {
                    override fun onTextChanged(position: Int, expenses: VehicleExpense) {
                        vehicleDetailsHashMap[position] = expenses
                    }
                }
            )
            binding.rvVehicleExpenses.adapter = vehicleExpenseAdapter
        } catch (t: Throwable) {
            requireContext().toast(requireContext().getString(R.string.opps))
        }
    }

    private fun setGeneralDetailsData() {
        lastClosingKms = generalDetailsHashMap[LAST_CLOSING_KMS]
        openingKms = generalDetailsHashMap[OPENING_KMS]
        closingKms = generalDetailsHashMap[CLOSING_KMS]
        missingKms = generalDetailsHashMap[MISSING_KMS]
        totalKms = generalDetailsHashMap[TOTAL_KMS]
        noOfLitres = generalDetailsHashMap[NO_OF_LITRES]
        pricePerLitre = generalDetailsHashMap[PRICE_PER_LITRE]
        fuelValue = generalDetailsHashMap[FUEL_VALUE]
        mileage = generalDetailsHashMap[MILEAGE]

        setHintAndValue(
            binding.lastClosingKmsTIL,
            binding.lastClosingKmsET,
            lastClosingKms?.label,
            lastClosingKms?.value
        )
        setHintAndValue(
            binding.openingKmsTIL,
            binding.openingKmsET,
            openingKms?.label,
            openingKms?.value
        )
        setHintAndValue(
            binding.closingKmsTIL,
            binding.closingKmsET,
            closingKms?.label,
            closingKms?.value
        )
        setHintAndValue(
            binding.missingKmsTIL,
            binding.missingKmsET,
            missingKms?.label,
            missingKms?.value
        )
        setHintAndValue(
            binding.totalKmsTIL,
            binding.totalKmsET,
            totalKms?.label,
            totalKms?.value
        )
        setHintAndValue(
            binding.noOfLitresTIL,
            binding.noOfLitresET,
            noOfLitres?.label,
            noOfLitres?.value
        )
        setHintAndValue(
            binding.pricePerLitreTIL,
            binding.pricePerLitreET,
            pricePerLitre?.label,
            pricePerLitre?.value
        )
        setHintAndValue(
            binding.fuelValueTIL,
            binding.fuelValueET,
            fuelValue?.label,
            fuelValue?.value
        )
        setHintAndValue(
            binding.mileageTIL,
            binding.mileageET,
            mileage?.label,
            mileage?.value
        )
    }

    private fun setHintAndValue(
        inputLayout: TextInputLayout,
        editText: TextInputEditText,
        label: String?,
        value: String?
    ) {
        inputLayout.isVisible = label.isNullOrEmpty().not()
        if (!label.isNullOrEmpty()) {
            inputLayout.hint = label
            editText.setText(value)
        }
    }

    private fun updateGeneralDetailsData(key: String) {
        val lastClosing = lastClosingKms?.value?.toDoubleOrNull() ?: 0.0
        val opening = openingKms?.value?.toDoubleOrNull() ?: 0.0
        val closing = closingKms?.value?.toDoubleOrNull() ?: 0.0
        val litres = noOfLitres?.value?.toDoubleOrNull() ?: 0.0
        val pricePerLitreValue = pricePerLitre?.value?.toDoubleOrNull() ?: 0.0

        val missingKm = (opening - lastClosing).takeIf { it > 0 }?.toString() ?: ""
        val totalKm = (closing - opening).takeIf { it > 0 }?.toString() ?: ""
        val fuelValueTemp = (litres * pricePerLitreValue).takeIf { it > 0 }?.toString() ?: ""
        val mileageTemp: String = if (litres > 0) {
            roundOffDecimal((totalKm.toDoubleOrNull() ?: 0.0) / litres).toString()
        } else ""

        updateValue(binding.missingKmsET, missingKm, missingKms)
        updateValue(binding.totalKmsET, totalKm, totalKms)
        updateValue(binding.fuelValueET, fuelValueTemp, fuelValue)
        updateValue(binding.mileageET, mileageTemp, mileage)

        when (key) {
            OPENING_KMS -> {
                if (opening < lastClosing) {
                    requireContext().toast(requireContext().getString(R.string.opening_kms_validation))
                } else if (closing < opening && closing != 0.0) {
                    requireContext().toast(requireContext().getString(R.string.closing_kms_validation))
                }
            }

            CLOSING_KMS -> {
                if (opening == 0.0) {
                    requireContext().toast(requireContext().getString(R.string.enter_valid_opening_kms))
                } else if (closing < opening) {
                    requireContext().toast(requireContext().getString(R.string.closing_kms_validation))
                }
            }

            NO_OF_LITRES -> {}
            PRICE_PER_LITRE -> {}
        }
    }

    private fun updateValue(editText: TextInputEditText, value: String, item: GeneralDetails?) {
        editText.setText(value)
        item?.value = value
    }

    private fun callUpdateExpensesDetailsApi() {
        try {
            val crewExpensesList =
                ArrayList<com.bitla.ts.domain.pojo.update_expenses_details.request.CrewExpense>()
            crewDetailsHashMap.forEach {
                val crewExpense = CrewExpense(
                    it.value.key,
                    it.value.value,
                    it.value.remarks
                )
                crewExpensesList.add(crewExpense)
            }

            val vehicleExpensesList =
                ArrayList<com.bitla.ts.domain.pojo.update_expenses_details.request.VehicleExpense>()
            vehicleDetailsHashMap.forEach {
                val vehicleExpense = VehicleExpense(
                    it.value.key,
                    it.value.value,
                    it.value.remarks
                )
                vehicleExpensesList.add(vehicleExpense)
            }

            val generalDataList = ArrayList<GeneralDetailsReqBody>()
            generalDetailsHashMap.forEach {
                val item = GeneralDetailsReqBody(
                    it.value.key,
                    it.value.value
                )
                generalDataList.add(item)
            }

            var isCrewExpensesListDataEmpty = true
            var isVehicleExpensesListDataEmpty = true
            var canUpdateGeneralDetails = true

            crewExpensesList.forEach {
                if (it.value.trim().isEmpty().not() || it.remarks?.trim().isNullOrEmpty().not()) {
                    isCrewExpensesListDataEmpty = false
                }
            }
            vehicleExpensesList.forEach {
                if (it.value.trim().isEmpty().not() || it.remarks?.trim().isNullOrEmpty().not()) {
                    isVehicleExpensesListDataEmpty = false
                }
            }

            if (binding.generalDetailsCV.isVisible) {
                if ((openingKms?.value?.toDoubleOrNull()
                        ?: 0.0) < (lastClosingKms?.value?.toDoubleOrNull() ?: 0.0)
                ) {
                    requireContext().toast(requireContext().getString(R.string.opening_kms_validation))
                    canUpdateGeneralDetails = false
                } else if ((closingKms?.value?.toDoubleOrNull()
                        ?: 0.0) < (openingKms?.value?.toDoubleOrNull() ?: 0.0)
                ) {
                    requireContext().toast(requireContext().getString(R.string.closing_kms_validation))
                    canUpdateGeneralDetails = false
                }
            }

            if ((isCrewExpensesListDataEmpty.not() || isVehicleExpensesListDataEmpty.not()) && canUpdateGeneralDetails) {
                pickUpChartViewModel.updateExpensesDetails(
                    ReqBody(
                        apiKey = loginModelPref.api_key,
                        crewExpenses = crewExpensesList,
                        reservationId = resID.toString(),
                        vehicleExpenses = vehicleExpensesList,
                        locale = locale,
                        generalDetails = generalDataList
                    ),
                    update_expenses_details_method_name
                )
            } else if (canUpdateGeneralDetails) {
                requireContext().toast(requireContext().getString(R.string.please_enter_value_in_at_least_one_field))
            }
        } catch (t: Throwable) {
            requireContext().toast(requireContext().getString(R.string.opps))
        }
    }

    private fun updateExpensesDetailsObserver() {
        pickUpChartViewModel.updateExpensesDetailsResponse.observe(requireActivity()) {
            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            it.message?.let { it1 -> requireContext().toast(it1) }
                            requireActivity().finish()
                        }

                        401 -> {
                            showUnauthorisedDialog()
                        }

                        else -> {
                            it.message?.let { it1 -> requireContext().toast(it1) }
                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                requireContext().toast(requireContext().getString(R.string.opps))
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

    override fun onButtonClick(view: Any, dialog: Dialog) {}
}