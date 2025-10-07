package com.bitla.ts.presentation.view.activity


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.ActivityManageAccountBinding
import com.bitla.ts.domain.pojo.SpinnerItems
import com.bitla.ts.domain.pojo.branch_list_model.request.BranchListRequest
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.user_list.request.UserListRequest
import com.bitla.ts.presentation.viewModel.BlockViewModel
import com.bitla.ts.presentation.viewModel.ManageAccountViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_AGENT_ID
import com.bitla.ts.utils.sharedPref.PREF_BRANCH_ID
import com.bitla.ts.utils.sharedPref.PREF_CATEGORY_TYPE
import com.bitla.ts.utils.sharedPref.PREF_FILTER_BRANCH_OR_AGENT
import com.bitla.ts.utils.sharedPref.PREF_FROM_DATE
import com.bitla.ts.utils.sharedPref.PREF_LIST_TYPE
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_BRANCH_OR_AGENT
import com.bitla.ts.utils.sharedPref.PREF_TO_DATE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.util.*

class ManageAccountActivity : BaseActivity(), DialogSingleButtonListener {

    private var privilegeResponse: PrivilegeResponseModel? = null
    private var agentBranchType: String = "Agent"
    private lateinit var binding : ActivityManageAccountBinding

    private var defaultSelection = 1
    private var isBeforeFromDateSelection: Boolean = true
    private var isAfterToDateSelection: Boolean = true
    private var isAfterFromDateSelection: Boolean = true
    private var hideYesterdayDateFilter: Boolean = false
    private var hideTodayDateFilter: Boolean = false
    private var hideTomorrowDateFilter: Boolean = false
    private var hideLast7DaysDateFilter: Boolean = false
    private var hideLast30DaysDateFilter: Boolean = false
    private var hideCustomDateFilter: Boolean = false
    private var hideCustomDateRangeFilter: Boolean = false
    private var isCustomDateFilterSelected: Boolean = false
    private var isCustomDateRangeFilterSelected: Boolean = false
    private var isLocalFilter = false
    private var currentSelection = ""
    private var fromDate = ""
    private var toDate = ""
    private var currentDate = ""
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private var selectedAgentId: String = ""
    private var userTypeId: Int = 1
    private var userListResponse: ArrayList<SpinnerItems> = arrayListOf()
    private lateinit var bccId: String
    private lateinit var apiKey: String
    private var locale: String? = ""
    private var loginModelPref: LoginModel = LoginModel()
    private var country: String? = null
    private var currency: String = ""
    private var currencyFormat: String = ""
    private var agentId: String = "-1"
    private var branchId: String = ""
    private var accountSelectedFor = "Agent"
    private val manageAccountViewModel by viewModel<ManageAccountViewModel<Any?>>()
    private var isShowManageAgentAccountLinkinAccount: Boolean? = false
    private var isShowManageBranchAccounting: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    override fun initUI() {
        binding = ActivityManageAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        binding.viewBT.setBackgroundResource(R.drawable.button_selected_bg)
        binding.viewBT.isEnabled = true

        binding.manageAccountToolBar.toolbarHeaderText.text = getString(R.string.manage_account)

        getPref()

        dashboardDateSetText(
            textView = binding.etToDate,
            fromDate = getDateYMD(getTodayDate()),
            toDate = null,
            inputDateFormat = DATE_FORMAT_Y_M_D
        )

        fromDate = getDateYMD(getTodayDate())
        toDate = getDateYMD(getTodayDate())

        binding.etToDate.setOnClickListener{
            DialogUtils.dialogDateFilter(
                context = this,
                defaultSelection = defaultSelection,
                todayDate = getDateYMD(getTodayDate()),
                fromDate = fromDate,
                toDate = toDate,
                isBeforeFromDateSelection = isBeforeFromDateSelection,
                isAfterToDateSelection = isAfterToDateSelection,
                isAfterFromDateSelection = isAfterFromDateSelection,
                hideYesterdayDateFilter = hideYesterdayDateFilter,
                hideTodayDateFilter = hideTodayDateFilter,
                hideTomorrowDateFilter = hideTomorrowDateFilter,
                hideLast7DaysDateFilter = hideLast7DaysDateFilter,
                hideLast30DaysDateFilter = hideLast30DaysDateFilter,
                hideCustomDateFilter = true,
                hideCustomDateRangeFilter = hideCustomDateRangeFilter,
                isCustomDateFilterSelected = isCustomDateFilterSelected,
                isCustomDateRangeFilterSelected = isCustomDateRangeFilterSelected,
                fragmentManager = this.supportFragmentManager,
                tag = "",
                onApply = { finalFromDate,
                            finalToDate,
                            lastSelectedItem,
                            isCustomDateFilter,
                            isCustomDateRangeFilter ->
                    if (finalFromDate != null) {
                        fromDate = finalFromDate
                        toDate = finalToDate ?: fromDate
                        defaultSelection = lastSelectedItem

                        isCustomDateFilterSelected = isCustomDateFilter
                        isCustomDateRangeFilterSelected = isCustomDateRangeFilter

                        isLocalFilter = true

                        if(defaultSelection == 3 || defaultSelection == 4 || defaultSelection == 6) {
                            dashboardDateSetText(
                                textView = binding.etToDate,
                                fromDate = fromDate,
                                toDate = toDate,
                                inputDateFormat = DATE_FORMAT_Y_M_D
                            )
                        } else  {
                            dashboardDateSetText(
                                textView = binding.etToDate,
                                fromDate = fromDate,
                                toDate = null,
                                inputDateFormat = DATE_FORMAT_Y_M_D
                            )
                        }
                    }

                    validation()
                }
            )
        }

        binding.filterAgentET.setText(getString(R.string.all))
        binding.etSelectAgentNdBranch.setText("")

        binding.etSelectCategories.setText(getString(R.string.show_all))
        callOnlineAgentApi()
        clickListener()
        setDefault()
        setOnlineAgentListObserver()
        setShowCategoriesAdapter()

        lifecycleScope.launch {
            blockViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }


        binding.filterAgentET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                callOnlineAgentApi()
                binding.etSelectAgentNdBranch.setText("")
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
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key

        if (getPrivilegeBase() != null) {
             privilegeResponse = getPrivilegeBase()

            if (!privilegeResponse?.country.isNullOrEmpty()) {
                country = privilegeResponse?.country
            }
            currency = if (!privilegeResponse?.currency.isNullOrEmpty())
                privilegeResponse?.currency.toString()
            else
                getString(R.string.rupeeSybbol)

            currencyFormat = privilegeResponse?.currencyFormat ?: getString(R.string.indian_currency_format)
            isShowManageAgentAccountLinkinAccount = privilegeResponse?.showManageAgentAccountLinkInAccount
            isShowManageBranchAccounting = privilegeResponse?.manageBranchAccounting

            if (isShowManageAgentAccountLinkinAccount == true) {
                binding.chkAgent.visible()
                binding.chkAgent.isChecked = true
            } else {
                binding.chkAgent.gone()
            }

            if (isShowManageBranchAccounting == true) {
                binding.chkBranch.visible()
                if (isShowManageAgentAccountLinkinAccount == false){
                    binding.chkBranch.isChecked = true
                }
            } else {
                binding.chkBranch.gone()
            }

            if (country != null && country.equals("India", true)) {
                binding.filterAgentTV.visible()
                binding.filterAgentTIL.visible()
            } else {
                binding.filterAgentTV.gone()
                binding.filterAgentTIL.gone()
            }
        } else {
            toast(getString(R.string.server_error))
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun clickListener() {
        binding.manageAccountToolBar.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.etSelectAgentNdBranch.setOnClickListener {
            getAgentsNdBranch()
        }

        binding.viewBT.setOnClickListener {
            val intent = Intent(this, ManageAccountListActivity::class.java)
            intent.apply {
               putExtra(PREF_BRANCH_ID, branchId)
               putExtra(PREF_AGENT_ID, agentId)
               putExtra(PREF_CATEGORY_TYPE, binding.etSelectCategories.text.toString())
               putExtra(PREF_LIST_TYPE, accountSelectedFor)
               putExtra(PREF_FROM_DATE, fromDate)
               putExtra(PREF_TO_DATE, toDate)
            }

            startActivity(intent)
        }

        binding.rechargeBT.setOnClickListener {
            val intent = Intent(this, RechargeActivity::class.java)
            intent.apply {
                putExtra(PREF_BRANCH_ID, branchId)
                putExtra(PREF_AGENT_ID, agentId)
                putExtra(PREF_CATEGORY_TYPE, binding.etSelectCategories.text.toString())
                putExtra(PREF_LIST_TYPE, accountSelectedFor)
                putExtra(PREF_FROM_DATE, fromDate)
                putExtra(PREF_TO_DATE, toDate)
                putExtra(PREF_FILTER_BRANCH_OR_AGENT, binding.filterAgentET.text.toString())
                putExtra(PREF_SELECTED_BRANCH_OR_AGENT, binding.etSelectAgentNdBranch.text.toString())
            }

            startActivity(intent)
        }

//        binding.buttonProceed.setBackgroundResource(R.drawable.button_default_bg)
//        binding.buttonProceed.isEnabled = false

        binding.radioGroup2.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chk_agent -> {
                    binding.apply {
                        balanceInfoCL.gone()
                        filterAgentET.setText(getString(R.string.all))
                        headerLabel.text = resources.getString(R.string.selectAgent)
                        subHeaderLabel.hint = resources.getString(R.string.selectAgent)
                        filterAgentTIL.hint = resources.getString(R.string.filter_agents)
                        binding.etSelectAgentNdBranch.setText("")

                    }
                    validation()
                    val selectedId = group.checkedRadioButtonId
                    val radio: RadioButton = group.findViewById(selectedId)
                    accountSelectedFor = radio.text.toString()
                    setAgentTypeAdapter()
                    agentId = "-1"
                    branchId = ""
                }

                R.id.chk_branch -> {
                    callBranchListApi()
                    binding.apply {
                        balanceInfoCL.gone()
                        filterAgentET.setText(getString(R.string.all))
                        headerLabel.text = resources.getString(R.string.selectBranch)
                        subHeaderLabel.hint = resources.getString(R.string.selectBranch)
                        filterAgentTIL.hint = resources.getString(R.string.filter_branch)
                        binding.etSelectAgentNdBranch.setText("")
                    }
                    validation()
                    val selectedId = group.checkedRadioButtonId
                    val radio: RadioButton = group.findViewById(selectedId)
                    accountSelectedFor = radio.text.toString()
                    setBranchTypeAdapter()
                    branchId = "-1"
                    agentId = ""
                }
            }
        }
    }
    private fun setDefault() {
        setAgentTypeAdapter()
    }

    private fun getAgentsNdBranch() {
        val intent = Intent(this, SearchActivity::class.java)
        if (binding.chkAgent.isChecked) {
            saveAgentList(userList)
            intent.putExtra("filter_type", binding.filterAgentET.text.toString())
            intent.putExtra(getString(R.string.CITY_SELECTION_TYPE), getString(R.string.selectAgent))
        } else {
            saveBranchList(branchList)
            intent.putExtra("filter_type", binding.filterAgentET.text.toString())
            intent.putExtra(getString(R.string.CITY_SELECTION_TYPE), getString(R.string.selectBranch))
        }

        startActivityForResult(intent, RESULT_CODE_SEARCH_AGENT)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            Timber.d(data.getStringExtra(getString(R.string.SELECTED_SEARCHED_NAME)))
            val value = data.getStringExtra(getString(R.string.SELECTED_SEARCHED_NAME))
            selectedAgentId = data.getStringExtra(getString(R.string.SELECTED_SEARCHED_ID)).toString()
            binding.etSelectAgentNdBranch.setText(value)
            if (binding.chkAgent.isChecked) {
                agentId = selectedAgentId
                branchId = ""
            } else {
                branchId = selectedAgentId
                agentId = ""
            }
        }
    }

    private fun callOnlineAgentApi() {
        if (isNetworkAvailable()) {
            val reqBody = com.bitla.ts.domain.pojo.user_list.request.ReqBody(
                api_key = apiKey,
                user_type = userTypeId,
                locale = locale
            )
            val userListRequest = UserListRequest(bccId, format_type, user_list_method_name, reqBody)

            blockViewModel.userListApi(
                apiKey = loginModelPref.api_key,
                cityId = "",
                userType = userTypeId.toString(),
                branchId = "",
                locale = locale!!,
                apiType = user_list_method_name
            )

            Timber.d("userListRequest $userListRequest")

        } else
            noNetworkToast()
    }

    private fun callBranchListApi() {
        if (isNetworkAvailable()) {
            val reqBody = com.bitla.ts.domain.pojo.branch_list_model.request.ReqBody(
                api_key = apiKey,
                locale = locale
            )

            BranchListRequest(
                bcc_id = bccId,
                format = format_type,
                method_name = branch_list_method_name,
                req_body = reqBody
            )

            blockViewModel.branchListApi(
                apiKey = loginModelPref.api_key,
                locale = locale.toString(),
                apiType = branch_list_method_name
            )
        } else
            noNetworkToast()
    }

    private fun setOnlineAgentListObserver() {
        blockViewModel.userList.observe(this) { it ->
            try {
                Timber.d("userListResponse ${userList.size}")
                Timber.d("userListResponse ${it.active_users}")
                userList.clear()

                if (it.active_users != null && it.active_users.isNotEmpty()) {
                    it.active_users.forEach {
                        val spinnerItems = SpinnerItems(it.id, it.label, it.type ?: "")
                        Timber.d("userListResponse ${it.id} : ${it.label}")
                        userList.add(spinnerItems)
                        userListResponse.add(spinnerItems)
                    }
                }
                saveUserList(userList)
            } catch (t: Throwable) {
                Timber.d("exceptionMsgUser ${t.message}")
                toast("An error occurred while fetching Agent List")
            }
        }

        blockViewModel.branchList.observe(this) { it ->
            Timber.d("branchListResponse $it")
            binding.includeProgress.progressBar.gone()
            branchList.clear()
            try {
                if (it != null) {
                    if (it.branchlists != null && it.branchlists.isNotEmpty()) {
                        it.branchlists.forEach {
                            val spinnerItems = SpinnerItems(it.id, it.label, it.type ?: "")
                            branchList.add(spinnerItems)
                        }
                    }
                    saveBranchList(branchList)
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                Timber.d("exceptionMsgBranch ${t.message}")
                toast("An error occurred while fetching Branch List")
            }
        }
    }

    private fun setAgentTypeAdapter() {
        binding.filterAgentTV.text = getString(R.string.filter_agents)
        binding.filterAgentET.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                resources.getStringArray(R.array.agentTypes)
            )
        )
    }

    private fun setBranchTypeAdapter() {
        binding.filterAgentTV.text = getString(R.string.filter_branch)
        binding.filterAgentET.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                resources.getStringArray(R.array.agentTypesWithoutNone)
            )
        )
    }

    private fun setShowCategoriesAdapter() {
        binding.etSelectCategories.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                resources.getStringArray(R.array.selectCategories)
            )
        )
    }


    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


    private fun validation() {

        manageAccountViewModel.validation(
            binding.etToDate.text.toString(),
        )

        manageAccountViewModel.validationData.observe(this) { it ->

            manageAccountViewModel.loadingState.observe(this) {
                Timber.d("LoadingState ${it.status}")
                when (it) {
                    LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                    LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                    else -> {

                        binding.includeProgress.progressBar.gone()
                    }
                }
            }

            if (it) {
                binding.includeProgress.progressBar.gone()
                binding.viewBT.setBackgroundResource(R.drawable.button_selected_bg)
                binding.viewBT.isEnabled = true
            } else {
                binding.includeProgress.progressBar.gone()
                binding.viewBT.setBackgroundResource(R.drawable.button_default_bg)
                binding.viewBT.isEnabled = false
            }
        }
    }
}

