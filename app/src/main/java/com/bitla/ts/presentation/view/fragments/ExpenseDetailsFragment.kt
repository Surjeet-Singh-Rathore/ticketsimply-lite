package com.bitla.ts.presentation.view.fragments

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.expenses_details.response.CrewExpense
import com.bitla.ts.domain.pojo.expenses_details.response.VehicleExpense
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.update_expenses_details.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast

class ExpenseDetailsFragment : BaseFragment(), DialogSingleButtonListener {

    private lateinit var fragmentExpenseDetailsBinding: FragmentExpenseDetailsBinding

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var resID: Long? = 0L
    private var crewDetailsHashMap = HashMap<Int, CrewExpense>()
    private var vehicleDetailsHashMap = HashMap<Int, VehicleExpense>()
    private var locale: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        fragmentExpenseDetailsBinding = FragmentExpenseDetailsBinding.inflate(inflater, container, false)
        initUi()
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        return fragmentExpenseDetailsBinding.root
    }

    override fun isInternetOnCallApisAndInitUI() {
        initUi()
    }

    override fun isNetworkOff() {
    }

    private fun initUi() {
        resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
        getPref()
        setClickListener()
        callExpensesDetailsApi()
        setExpensesDetailsObserver()
        updateExpensesDetailsObserver()
    }

    private fun setClickListener() {
        fragmentExpenseDetailsBinding.btnUpdate.setOnClickListener {
            callUpdateExpensesDetailsApi()
        }
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
    }

    private fun callExpensesDetailsApi() {
        try {
            pickUpChartViewModel.getExpensesDetails(
                loginModelPref.api_key,
                resID.toString(),
                locale!!,
                response_format,
                expenses_details_method_name
            )
        } catch (t: Throwable) {
            Timber.d("An error occurred at callExpensesDetailsApi(): ${t.message}")
            requireContext().toast(requireContext().getString(R.string.opps))
        }
    }

    private fun setExpensesDetailsObserver() {

        pickUpChartViewModel.expensesDetailsResponse.observe(requireActivity()) {
            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            setCrewExpenseAdapter(it.result.crewExpenses)
                            setVehicleExpenseAdapter(it.result.vehicleExpenses)
                        }
                        401 -> {
                            /*DialogUtils.unAuthorizedDialog(
                                requireContext(),
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            (activity as BaseActivity).showUnauthorisedDialog()

                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                Timber.d("An error occurred at setExpensesDetailsObserver(): ${t.message}")
                requireContext().toast(requireContext().getString(R.string.opps))
            }
        }
    }

    private fun setCrewExpenseAdapter(crewExpenses: List<CrewExpense>) {
        try {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            fragmentExpenseDetailsBinding.rvCrewExpenses.layoutManager = layoutManager
            val crewExpenseAdapter = CrewExpenseAdapter(
                requireActivity(),
                crewExpenses,
                object : CrewExpenseAdapter.OnTextChangedListener {
                    override fun onTextChanged(position: Int, expenses: CrewExpense) {
                        crewDetailsHashMap[position] = expenses
                    }
                })
            fragmentExpenseDetailsBinding.rvCrewExpenses.adapter = crewExpenseAdapter
        } catch (t: Throwable) {
            Timber.d("An error occurred at setCrewExpenseAdapter(): ${t.message}")
            requireContext().toast(requireContext().getString(R.string.opps))
        }
    }

    private fun setVehicleExpenseAdapter(vehicleExpenses: List<VehicleExpense>) {
        try {

            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            fragmentExpenseDetailsBinding.rvVehicleExpenses.layoutManager = layoutManager
            val vehicleExpenseAdapter = VehicleExpenseAdapter(
                requireActivity(),
                vehicleExpenses,
                object : VehicleExpenseAdapter.OnTextChangedListener {
                    override fun onTextChanged(position: Int, expenses: VehicleExpense) {
                        vehicleDetailsHashMap[position] = expenses
                    }
                })
            fragmentExpenseDetailsBinding.rvVehicleExpenses.adapter = vehicleExpenseAdapter
        } catch (t: Throwable) {
            Timber.d("An error occurred at setVehicleExpenseAdapter(): ${t.message}")
            requireContext().toast(requireContext().getString(R.string.opps))
        }
    }


    private fun callUpdateExpensesDetailsApi() {
        try {
            val crewExpensesList =
                ArrayList<com.bitla.ts.domain.pojo.update_expenses_details.request.CrewExpense>()
            crewDetailsHashMap.forEach {
                val crewExpense =
                    com.bitla.ts.domain.pojo.update_expenses_details.request.CrewExpense(
                        it.value.key,
                        it.value.value
                    )
                crewExpensesList.add(crewExpense)
            }

            val vehicleExpensesList =
                ArrayList<com.bitla.ts.domain.pojo.update_expenses_details.request.VehicleExpense>()
            vehicleDetailsHashMap.forEach {
                val vehicleExpense =
                    com.bitla.ts.domain.pojo.update_expenses_details.request.VehicleExpense(
                        it.value.key,
                        it.value.value
                    )
                vehicleExpensesList.add(vehicleExpense)
            }

            var isCrewExpensesListDataEmpty = true
            var isVehicleExpensesListDataEmpty = true

            crewExpensesList.forEach {
                if (it.value.trim().isEmpty().not()) {
                    isCrewExpensesListDataEmpty = false
                }
            }
            vehicleExpensesList.forEach {
                if (it.value.trim().isEmpty().not()) {
                    isVehicleExpensesListDataEmpty = false
                }
            }
            if (isCrewExpensesListDataEmpty.not() || isVehicleExpensesListDataEmpty.not()) {

                pickUpChartViewModel.updateExpensesDetails(
                    ReqBody(
                        apiKey = loginModelPref.api_key,
                        crewExpenses = crewExpensesList,
                        reservationId = resID.toString(),
                        vehicleExpenses = vehicleExpensesList,
                        locale = locale
                    ),
                    update_expenses_details_method_name
                )
            } else {
                requireContext().toast(requireContext().getString(R.string.please_enter_value_in_at_least_one_field))
            }
        } catch (t: Throwable) {
            Timber.d("An error occurred at callUpdateExpensesDetailsApi(): ${t.message}")
            requireContext().toast(requireContext().getString(R.string.opps))
        }
    }

    private fun updateExpensesDetailsObserver() {

        pickUpChartViewModel.updateExpensesDetailsResponse.observe(requireActivity()) {
            try {
                Timber.d("updateExpensesMsg $it")
                if (it != null) {
                    if (it.code == 200) {
                        it.message?.let { it1 -> requireContext().toast(it1) }
                        requireActivity().finish()
                        //callExpensesDetailsApi()
                    } else {
                        it.message?.let { it1 -> requireContext().toast(it1) }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                Timber.d("An error occurred at updateExpensesDetailsObserver(): ${t.message}")
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

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

}