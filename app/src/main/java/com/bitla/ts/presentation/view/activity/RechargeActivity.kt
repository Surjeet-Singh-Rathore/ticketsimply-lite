package com.bitla.ts.presentation.view.activity


import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.widget.*
import androidx.core.content.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.account_info.request.*
import com.bitla.ts.domain.pojo.account_info.request.ReqBody
import com.bitla.ts.domain.pojo.agent_recharge.*
import com.bitla.ts.domain.pojo.agent_recharge.request.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.user_list.*
import com.bitla.ts.domain.pojo.user_list.request.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.bottomsheet.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.*
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.util.*
import java.util.concurrent.*

class RechargeActivity : BaseActivity(), DialogSingleButtonListener {

    companion object{
         val tag : String = RechargeActivity::class.java.simpleName
    }

    private var currencyFormat: String = ""
    private var agentId: String = ""
    private var branchId: String = ""
    private var newUsersList: ArrayList<ActiveUser> = arrayListOf()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var branchData: BranchRechargeResponseModel? = null
    private var agentData: AgentRechargeResponseModel? = null
    private var isDialogShowing = false
    private var otpValidationTime = 120
    private var latestTransactionNumber: String = ""
    private var latestChequeDDNumber: String? = ""
    private var latestStatus: String? = ""
    private var currency: String = ""
    private var statusId: Int? = null
    private var isOtpExpired = false
    private var country: String? = null
    private var transactionTypes = mutableListOf<SpinnerItems>()
    private lateinit var binding: ActivityRechargeBinding
    private var bookingAgentList: MutableList<SpinnerItems> = mutableListOf()
    private lateinit var bccId: String
    private lateinit var apiKey: String
    private var apiCode: Int = 0
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private var selectedAgentId: String = ""
    private var enteredOTP: String = ""
    private lateinit var mcalendar: Calendar
    private var loginModelPref: LoginModel = LoginModel()
    private val agentRechargeViewModel by viewModel<AgentRechargeViewModel<Any?>>()
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private var userTypeId: Int = 1
    private var userListResponse: ArrayList<SpinnerItems> = arrayListOf()
    private var rechargePaymentStatusList = mutableListOf<RechargePaymentStatus>()
    private var showManageAgentAccountLinkInAccount: Boolean = false
    private var manageBranchAccounting: Boolean = false
    private val agentAccountInfoViewModel by viewModel<AgentAccountInfoViewModel<Any>>()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var paymentType = ""
    var transactionType = ""
    private var rechargeSelectedFor = ""
    private var rechargeStatus = ""
    private var locale: String? = ""
    private var selectedFilterBranchOrAgent: String? = ""
    private var selectedAgentNdBranch: String? = ""

    private lateinit var rechargePaymentTypesList: Array<String>
    private lateinit var rechargeStatusList: Array<String>
    private lateinit var rechargeTransactionTypesList: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUI()
        setAccountInfoObserver()
        callOnlineAgentApi()

        binding.acStatuses.setOnItemClickListener { adapterView, view, i, l ->
            if (country != null && country.equals(
                    "India",
                    true
                ) && rechargePaymentStatusList.isNotEmpty()
            )
                statusId = rechargePaymentStatusList[i].id

            firebaseLogEvent(
                this,
                PAYMENT_STATUS,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                PAYMENT_STATUS,
                PaymentStatus.STATUS
            )
        }

        lifecycleScope.launch {
            blockViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            agentRechargeViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun initUI() {
        binding = ActivityRechargeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        rechargePaymentTypesList = resources.getStringArray(R.array.rechargePaymentTypes)
        rechargeStatusList = resources.getStringArray(R.array.rechargeStatus)
        rechargeTransactionTypesList = resources.getStringArray(R.array.rechargeTransactionTypes)

        clickListener()
        getPref()
        setDefault()
        onEditFields()
        setOnlineAgentListObserver()
        getIntentData()

        if (!manageBranchAccounting) {
            binding.chkBranch.gone()
        }
        if (!showManageAgentAccountLinkInAccount) {
            binding.chkAgent.gone()
            binding.chkBranch.isChecked = true
        }

        if (!manageBranchAccounting && !showManageAgentAccountLinkInAccount) {
            notifyUserError("Recharge option is not available for you.")
        }

        firebaseAnalytics = Firebase.analytics


        binding.filterAgentET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                callOnlineAgentApi()
                binding.etSelectAgent.setText("")
//                binding.etSelectAgent.requestFocus()

                binding.balanceInfoCL.gone()

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

        binding.etEnterAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    val words = if (country != null && country.equals("India", true)) {
                        amountToWordsIndianFormat(s.toString().toLong())
                    } else {
                        convertAmountToWords(s.toString().toDouble())
                    }
                    binding.amountInWordsTV.text = words
                } else {
                    binding.amountInWordsTV.text = ""

                }
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

    override fun onResume() {
        super.onResume()
        setAgentTypeAdapter()

    }

    private fun accountInfoApi() {

        Timber.d("agentinfosome: ${loginModelPref.api_key}")
        val agentRequest = AgentAccountInfoRequest(
            bccId,
            format_type,
            agent_account_info,
            ReqBody(
                loginModelPref.api_key,
                locale = locale
            )
        )
        agentAccountInfoViewModel.agentAccountInfoAPI(
            agentRequest,
            agentId,
            branchId,
            agent_account_info,

            )
    }

    private fun setAccountInfoObserver() {
        agentAccountInfoViewModel.agentInfo.observe(this) {
            try {
                it.apply {
                    if (country != null && country.equals("India", true))
                        binding.balanceInfoCL.visible()
                    if (this.credit_limit.isEmpty()) {
                        binding.creditLimitValueTV.text = "-"
                    } else {
                        binding.creditLimitValueTV.text =
                            currency + this.credit_limit.toDouble().convert(currencyFormat)
                    }

                    if (this.last_recharge_amount.isEmpty()) {
                        binding.lastRechargeValueTV.text = "-"
                    } else {
                        binding.lastRechargeValueTV.text =
                            currency + this.last_recharge_amount.toDouble().convert(currencyFormat)
                    }

                    binding.lastTransactionDateTimeTV.text =
                        getString(R.string.last_transaction_time) + " " + this.last_recharged_on
                    binding.balanceValueTV.text =
                        currency + this.available_balance.toDouble().convert(currencyFormat)

                    if (this.commission_balance.equals("")) {
                        binding.comissionValueTV.text = "-"
                    } else if (this.commission_balance.contains("Default Cmsn") || this.commission_balance.contains(
                            "%"
                        )
                    ) {
                        binding.comissionValueTV.text = this.commission_balance
                    } else {
                        binding.comissionValueTV.text =
                            currency + this.commission_balance.toDouble().convert(currencyFormat)
                    }
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getIntentData() {
//        branchId = intent.getStringExtra("branchId").toString()
//        agentId = intent.getStringExtra("agentId").toString()
//        rechargeSelectedFor = intent.getStringExtra("listType").toString()
//        selectedFilterBranchOrAgent = intent.getStringExtra("selectedFilterBranchOrAgent").toString()
//        selectedAgentNdBranch = intent.getStringExtra("selectedAgentNdBranch").toString()

        branchId = intent.getStringExtra(PREF_BRANCH_ID).toString()
        agentId = intent.getStringExtra(PREF_AGENT_ID).toString()
        selectedFilterBranchOrAgent = intent.getStringExtra(PREF_FILTER_BRANCH_OR_AGENT).toString()
        selectedAgentNdBranch = intent.getStringExtra(PREF_SELECTED_BRANCH_OR_AGENT).toString()
        rechargeSelectedFor = intent.getStringExtra(PREF_LIST_TYPE) ?: ""

        Timber.d("rechargeSelectedFor - $rechargeSelectedFor")
        if (rechargeSelectedFor.isNotEmpty()) {
            accountInfoApi()
            binding.chkAgent.isChecked
            if (rechargeSelectedFor.equals("Agent", true)) {
                binding.apply {
                    chkAgent.isChecked = true
                    filterAgentET.setText("$selectedFilterBranchOrAgent")
                    etSelectAgent.setText("$selectedAgentNdBranch")
                    setAgentTypeAdapter()
                }
                selectedAgentId = agentId
            } else {
                binding.apply {
                    chkBranch.isChecked = true
                    filterAgentET.setText("$selectedFilterBranchOrAgent")
                    etSelectAgent.setText("$selectedAgentNdBranch")
                    filterAgentTV.text = resources.getString(R.string.filter_branch)
                    headerLabel.text = resources.getString(R.string.selectBranch)
                    subHeaderLabel.hint = resources.getString(R.string.selectBranch)
                    filterAgentTIL.hint = resources.getString(R.string.filter_branch)
                }
                selectedAgentId = branchId
                setBranchTypeAdapter()
            }
        } else {
            binding.chkAgent.isChecked = true
//            binding.filterAgentET.setText(getString(R.string.all))
            PreferenceUtils.removeKey(PREF_BRANCH_ID)
            PreferenceUtils.removeKey(PREF_AGENT_ID)
            PreferenceUtils.removeKey(PREF_FILTER_BRANCH_OR_AGENT)
            PreferenceUtils.removeKey(PREF_SELECTED_BRANCH_OR_AGENT)
            PreferenceUtils.removeKey(PREF_LIST_TYPE)
            selectedAgentId = ""
            setAgentTypeAdapter()
        }
    }

    private fun showRechargeOTPDialog() {

        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val dialogBinding = DialogConfirmRechargePaymentBinding.inflate(layoutInflater)
        bottomSheetDialog!!.setContentView(dialogBinding.root)

        if (binding.chkAgent.isChecked) {
            dialogBinding.titleTV.text = getString(R.string.agent_recharge)
        } else {
            dialogBinding.titleTV.text = getString(R.string.branch_recharge)
        }
        startCoundown(dialogBinding)

        bottomSheetDialog!!.setCancelable(false)

        dialogBinding.resendOtpBT.setOnClickListener {
            dialogBinding.otpET.setText("")
            startCoundown(dialogBinding)
            isDialogShowing = true

            if (binding.chkAgent.isChecked) {
                val reqBody =
                    com.bitla.ts.domain.pojo.agent_recharge.request.AgentReqBody(
                        agentId = selectedAgentId,
                        amount = binding.etEnterAmount.text.toString(),
                        apiKey = apiKey,
                        paymentType = paymentType,
                        remarks = binding.etDescription.text.toString(),
                        responseFormat = "true",
                        status = latestStatus!!,
                        transactionType = transactionType,
                        travelAccountDate = getDateYMD(binding.etToDate.text.toString()),
                        cheque_description = latestChequeDDNumber,
                        locale = locale,
                        resend_otp = true,
                        transactionNumber = latestTransactionNumber
                    )
                AgentRechargeRequest(bccId, format_type, credit_amount_method_name, reqBody)

                /* agentRechargeViewModel.agentRechargeApi(
                     loginModelPref.auth_token,
                     loginModelPref.api_key,
                     agentListRequest,
                     manual_recharge_method_name
                 )*/

                agentRechargeViewModel.agentRechargeApi(
                    reqBody,
                    manual_recharge_method_name
                )
            } else {
                val reqBody =
                    com.bitla.ts.domain.pojo.agent_recharge.request.ReqBody(
                        amount = binding.etEnterAmount.text.toString(),
                        api_key = apiKey,
                        branch_id = selectedAgentId,
                        date = getDateYMD(binding.etToDate.text.toString()),
                        dd_number = latestChequeDDNumber!!,
                        description = binding.etDescription.text.toString(),
                        payment_type = paymentType,
                        status = latestStatus!!,
                        transaction_type = binding.acTransactionType.text.toString(),
                        locale = locale,
                        resend_otp = true,
                        transaction_number = latestTransactionNumber
                    )
                BranchRechargeRequest(bccId, format_type, manual_recharge_method_name, reqBody)

                /*agentRechargeViewModel.branchRechargeApi(
                    loginModelPref.auth_token,
                    loginModelPref.api_key,
                    userListRequest,
                    manual_recharge_method_name
                )*/
                agentRechargeViewModel.branchRechargeApi(
                    reqBody,
                    manual_recharge_method_name
                )
            }
        }


        dialogBinding.goBackBT.setOnClickListener {
            isDialogShowing = false
            bottomSheetDialog!!.dismiss()
        }

        dialogBinding.confirmRechargeBT.setOnClickListener {
            enteredOTP = dialogBinding.otpET.text.toString()
            if (enteredOTP != "") {

                if (binding.chkAgent.isChecked) {
                    Timber.d("Is agent", "true")
                    val reqBody =
                        com.bitla.ts.domain.pojo.agent_recharge.request.ConfirmAgentRequestBody(
                            apiKey = apiKey,
                            key = agentData!!.agentRechargeResult[0].key,
                            transaction_number = agentData!!.agentRechargeResult[0].transactionNumber,
                            otp = enteredOTP,
                            locale = locale!!,
                            is_from_middle_tier = "true",
                            response_format = true,
                            isOtpExpired = isOtpExpired
                        )
                    val confirmAgentRequestList =
                        ConfirmAgentRechargeRequest(
                            bccId,
                            format_type,
                            confirm_credit_recharge_branch,
                            reqBody
                        )

                    /* agentRechargeViewModel.confirmAgentRechargeApi(
                         loginModelPref.auth_token,
                         loginModelPref.api_key,
                         confirmAgentRequestList,
                         confirm_credit_recharge_branch
                     )*/

                    agentRechargeViewModel.confirmAgentRechargeApi(
                        reqBody,
                        confirm_credit_recharge_branch
                    )
                } else {
                    Timber.d("Is agent", "false")

                    val reqBody =
                        com.bitla.ts.domain.pojo.agent_recharge.request.ConfirmAgentRequestBody(
                            apiKey = apiKey,
                            key = branchData!!.key,
                            transaction_number = branchData!!.transactionNumber,
                            otp = enteredOTP,
                            locale = locale!!,
                            is_from_middle_tier = "true",
                            response_format = true,
                            isOtpExpired = isOtpExpired
                        )
                    val confirmAgentRequestList =
                        ConfirmAgentRechargeRequest(
                            bccId,
                            format_type,
                            confirm_manual_recharge_branch,
                            reqBody,
                        )

                    /*agentRechargeViewModel.confirmBranchRechargeApi(
                        loginModelPref.auth_token,
                        loginModelPref.api_key,
                        confirmAgentRequestList,
                        confirm_manual_recharge_branch
                    )*/

                    agentRechargeViewModel.confirmBranchRechargeApi(
                        reqBody,
                        confirm_manual_recharge_branch
                    )
                }
            } else {
                toast(getString(R.string.please_enter_otp_to_proceed))
            }
        }

        if (!bottomSheetDialog!!.isShowing) {
            bottomSheetDialog!!.show()
        }
    }

    private fun startCoundown(dialogBinding: DialogConfirmRechargePaymentBinding) {
        var counter: Int = otpValidationTime
        val timeInMillis = counter * 1000

        val minutes = TimeUnit.SECONDS.toMillis(timeInMillis.toLong())

        object : CountDownTimer(timeInMillis.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                dialogBinding.resendOtpBT.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.button_little_round_light_grey
                )
                isOtpExpired = false
                dialogBinding.resendOtpBT.isClickable = false
                if (counter <= 60) {
                    dialogBinding.resendOtpTV.text =
                        "Your OTP will expire in $counter seconds"
                    counter--
                } else {
                    dialogBinding.resendOtpTV.text =
                        "Your OTP will expire in " + String.format(
                            "%d min %d sec",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(
                                            millisUntilFinished
                                        )
                                    )
                        )
                    counter--
                }
            }

            override fun onFinish() {
                isOtpExpired = true
                dialogBinding.resendOtpTV.text = ""
                dialogBinding.resendOtpBT.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.button_little_round_light_blue
                )

                dialogBinding.resendOtpBT.isClickable = true
            }
        }.start()
    }

    private fun setDefault() {
        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)
        binding.etToDate.setText(getTodayDate())
        val monthname: String = android.text.format.DateFormat.format("MMM", Date()) as String
        binding.rechargeAppBar.headerTitleDesc.text =
            "${resources.getString(R.string.today)}, $monthname ${day}, $year"
        setTransactionTypeObserver()
        setStatusesObserver()
        setPaymentTypeObserver()
        setTicketDetailsObserver()
        setAgentTypeAdapter()

    }

    private fun clickListener() {
        binding.rechargeAppBar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
        binding.etSelectAgent.setOnClickListener {
            getAgents()
        }
        binding.buttonProceed.setOnClickListener {
            DialogUtils.rechargeSummaryDialog(
                this,
                getString(R.string.confirm_recharge),
                binding.etSelectAgent.text.toString(),
                binding.etEnterAmount.text.toString(),
                binding.amountInWordsTV.text.toString(),
                binding.acStatuses.text.toString(),
                this
            )
        }
        binding.buttonProceed.setBackgroundResource(R.drawable.button_default_bg)
        binding.buttonProceed.isEnabled = false
        binding.layoutDdNumber.gone()
        binding.layoutChequeNumber.gone()
        binding.etDdNumber.setText("")
        binding.etChequeNumber.setText("")

        binding.radioGroup2.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chk_agent -> {
                    binding.apply {
                        balanceInfoCL.gone()
                        filterAgentET.setText(getString(R.string.all))
                        headerLabel.text = resources.getString(R.string.selectAgent)
                        subHeaderLabel.hint = resources.getString(R.string.selectAgent)
                        filterAgentTIL.hint = resources.getString(R.string.filter_agents)
                        etSelectAgent.setText("")
                        filterAgentTV.text = resources.getString(R.string.filter_agents)
                    }

                    selectedAgentId = ""
                    validation()
                    val selectedId = group.checkedRadioButtonId
                    val radio: RadioButton = group.findViewById(selectedId)
                    rechargeSelectedFor = radio.text.toString()
                    setAgentTypeAdapter()
                    binding.balanceInfoCL.gone()

                }

                R.id.chk_branch -> {
                    callBranchListApi()
                    binding.apply {
                        balanceInfoCL.gone()
                        filterAgentET.setText(getString(R.string.all))
                        headerLabel.text = resources.getString(R.string.selectBranch)
                        subHeaderLabel.hint = resources.getString(R.string.selectBranch)
                        filterAgentTIL.hint = resources.getString(R.string.filter_branch)
                        etSelectAgent.setText("")
                        filterAgentTV.text = resources.getString(R.string.filter_branch)
                    }

                    selectedAgentId = ""
                    validation()
                    val selectedId = group.checkedRadioButtonId
                    val radio: RadioButton = group.findViewById(selectedId)
                    rechargeSelectedFor = radio.text.toString()
                    setBranchTypeAdapter()
                    binding.balanceInfoCL.gone()
                }
            }
        }
        calendar()
    }

    private fun setTransactionTypeObserver() {
        Timber.d("setTransactionTypeObserver()")
        binding.acTransactionType.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                resources.getStringArray(R.array.rechargeTransactionTypes)
            )
        )
    }

    private fun setAgentTypeAdapter() {
//        binding.filterAgentTV.text = getString(R.string.filter_agents)
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
//        binding.filterAgentTV.text = getString(R.string.filter_branch)
        binding.filterAgentET.setText(getString(R.string.all))
        binding.filterAgentET.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                resources.getStringArray(R.array.agentTypesWithoutNone)
            )
        )
    }

    private fun setStatusesObserver() {
        if (country != null && country.equals(
                "indonesia",
                true
            ) || country != null && country.equals("Malaysia", true)
            || country != null && country.equals("Vietnam", true)
        ) {
            binding.acStatuses.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    resources.getStringArray(R.array.rechargeStatus)
                )
            )
        } else {
            binding.acStatuses.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    rechargePaymentStatusList
                )
            )
        }
    }

    private fun setPaymentTypeObserver() {
        binding.acPaymentType.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                resources.getStringArray(R.array.rechargePaymentTypes)
            )
        )
    }


    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        if (getPrivilegeBase() != null) {
            val privilegeResponse = getPrivilegeBase()
            if (privilegeResponse?.manageBranchAccounting != null) {
                manageBranchAccounting = privilegeResponse.manageBranchAccounting
            }
            if (privilegeResponse?.showManageAgentAccountLinkInAccount != null) {
                showManageAgentAccountLinkInAccount =
                    privilegeResponse.showManageAgentAccountLinkInAccount
            }
            if (!privilegeResponse?.rechargePaymentStatusType.isNullOrEmpty()) {
                rechargePaymentStatusList = privilegeResponse?.rechargePaymentStatusType!!
            }
            if (!privilegeResponse?.country.isNullOrEmpty()) {
                country = privilegeResponse?.country
            }
            currency = if (!privilegeResponse?.currency.isNullOrEmpty())
                privilegeResponse?.currency.toString()
            else
                getString(R.string.rupeeSybbol)

            currencyFormat = privilegeResponse?.currencyFormat
                ?: getString(R.string.indian_currency_format)


            if (country != null && country.equals(
                    "India",
                    true
                )
            ) {
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

    fun calendar() {
        binding.etToDate.setOnClickListener {
            val listener =
                DatePickerDialog.OnDateSetListener {
                        _, year, monthOfYear, dayOfMonth,
                    ->
                    binding.etToDate.setText(getDateDMY("$year-${monthOfYear + 1}-$dayOfMonth"))
                }
            val dpDialog = DatePickerDialog(this, listener, year, month, day)
            dpDialog.show()
        }
    }


    private fun getAgents() {

        //Timber.d(" branches ${privilegeResponseModel.branches.toString()}")

        /*
                privilegeResponseModel.branches.forEach {
                    val spinnerItems = SpinnerItems(it[0].toString().dropLast(2).toInt(), it[1] as String)
                    bookingAgentList.add(spinnerItems)
                }
        */


        val intent = Intent(this, SearchActivity::class.java)
        if (binding.chkAgent.isChecked) {
            saveAgentList(userList)
            intent.putExtra(
                getString(R.string.CITY_SELECTION_TYPE),
                getString(R.string.selectAgent)
            )
        } else {
           // saveBranchList(bookingAgentList)
            intent.putExtra(
                getString(R.string.CITY_SELECTION_TYPE),
                getString(R.string.selectBranch)
            )
        }
        intent.putExtra("filter_type", binding.filterAgentET.text.toString())
        intent.putExtra(PREVIOUS_SCREEN, tag)
        startActivityForResult(intent, RESULT_CODE_SEARCH_AGENT)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            Timber.d(data.getStringExtra(getString(R.string.SELECTED_SEARCHED_NAME)))
            val value = data.getStringExtra(getString(R.string.SELECTED_SEARCHED_NAME))
            selectedAgentId =
                data.getStringExtra(getString(R.string.SELECTED_SEARCHED_ID)).toString()
            binding.etSelectAgent.setText(value)
            if (binding.chkAgent.isChecked) {
                agentId = selectedAgentId
                branchId = ""
            } else {
                branchId = selectedAgentId
                agentId = ""
            }
            if (country != null && country.equals(
                    "India",
                    true
                )
            ) {
                accountInfoApi()
            }
        }
    }

    private fun callBranchRechargeApi() {
        if (this.isNetworkAvailable()) {
            paymentType = binding.acPaymentType.text.toString()
            if (paymentType == rechargePaymentTypesList.getOrNull(6)) { // Net Pay
                paymentType = "net_pay"
            }
            if (paymentType == rechargePaymentTypesList.getOrNull(7)) { // Pay Order
                paymentType = "pay_order"
            }
            if (paymentType == rechargePaymentTypesList.getOrNull(1)) { // Cash
                paymentType = "Cash"
            }
            var chequeDDNumber: String = ""
            if (binding.acPaymentType.text.toString() == rechargePaymentTypesList.getOrNull(2)) { // DD
                if (binding.etDdNumber.text.toString().isNullOrEmpty()) {
                    chequeDDNumber = ""
                } else {
                    chequeDDNumber = binding.etDdNumber.text.toString()
                }
            } else if (binding.acPaymentType.text.toString() == rechargePaymentTypesList.getOrNull(0)) { // Cheque
                if (binding.etChequeNumber.text.toString().isNullOrEmpty()) {
                    chequeDDNumber = ""
                } else {
                    chequeDDNumber = binding.etChequeNumber.text.toString()
                }
            } else {
                chequeDDNumber = ""
            }

            val status = if (country != null && country.equals(
                    "India",
                    true
                ) && statusId != null
            ) {
                statusId.toString()
            } else {
                binding.acStatuses.text.toString()
            }

            latestStatus = status
            latestChequeDDNumber = chequeDDNumber
            val reqBody =
                com.bitla.ts.domain.pojo.agent_recharge.request.ReqBody(
                    binding.etEnterAmount.text.toString(),
                    apiKey,
                    selectedAgentId,
                    getDateYMD(binding.etToDate.text.toString()),
                    chequeDDNumber,
                    binding.etDescription.text.toString(),
                    paymentType,
                    status,
                    binding.acTransactionType.text.toString(),
                    locale = locale,
                    device_id = getDeviceUniqueId(this)
                )
            val userListRequest =
                BranchRechargeRequest(bccId, format_type, manual_recharge_method_name, reqBody)

            /*agentRechargeViewModel.branchRechargeApi(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                userListRequest,
                manual_recharge_method_name
            )
            */
            agentRechargeViewModel.branchRechargeApi(
                reqBody,
                manual_recharge_method_name
            )
            setTransactionTypeObserver()
        } else
            this.noNetworkToast()
    }

    private fun callAgentRechargeApi() {
        if (isNetworkAvailable()) {
            paymentType = binding.acPaymentType.text.toString()
            when (paymentType) {
                rechargePaymentTypesList.getOrNull(0) -> { // Cheque
                    paymentType = "0"
                }
                rechargePaymentTypesList.getOrNull(1) -> { // Cash
                    paymentType = "3"
                }
                rechargePaymentTypesList.getOrNull(2) -> { // DD
                    paymentType = "1"
                    toast("DD selected")
                }
                rechargePaymentTypesList.getOrNull(3) -> { // NEFT
                    paymentType = "9"
                }
                rechargePaymentTypesList.getOrNull(4) -> { // IMPS
                    paymentType = "11"
                }
                rechargePaymentTypesList.getOrNull(5) -> { // RTGs
                    paymentType = "10"
                }
                rechargePaymentTypesList.getOrNull(6) -> { // Net Pay
                    paymentType = "4"
                }
                rechargePaymentTypesList.getOrNull(7) -> { // Pay Order
                    paymentType = "2"
                }
                rechargePaymentTypesList.getOrNull(8) -> { // Other
                    paymentType = "5"
                }
            }

            if (binding.acTransactionType.text.toString() == rechargeTransactionTypesList.getOrNull(0)) { // Credit
                transactionType = "1"
            } else if (binding.acTransactionType.text.toString() == rechargeTransactionTypesList.getOrNull(1)) { // Debit
                transactionType = "0"
            }

            var status = ""
            if (country != null && country.equals(
                    "India",
                    true
                ) && statusId != null
            ) {
                status = statusId.toString()
            } else {
                when(binding.acStatuses.text.toString()) {
                    rechargeStatusList.getOrNull(0) -> {
                        status = "1"
                    }
                    rechargeStatusList.getOrNull(1) -> {
                        status = "0"
                    }
                    rechargeStatusList.getOrNull(2) -> {
                        status = "5"
                    }
                }
            }
            var chequeDDNumber: String? = ""
            if (binding.acPaymentType.text.toString() == rechargePaymentTypesList.getOrNull(2)) { // DD
                if (binding.etDdNumber.text.toString().isNullOrEmpty()) {
                    chequeDDNumber = null
                } else {
                    chequeDDNumber = binding.etDdNumber.text.toString()
                }
            } else if (binding.acPaymentType.text.toString() == rechargePaymentTypesList.getOrNull(0)) { // Cheque
                if (binding.etChequeNumber.text.toString().isNullOrEmpty()) {
                    chequeDDNumber = null
                } else {
                    chequeDDNumber = binding.etChequeNumber.text.toString()
                }
            } else {
                chequeDDNumber = null
            }
            latestStatus = status
            latestChequeDDNumber = chequeDDNumber
            val reqBody =
                com.bitla.ts.domain.pojo.agent_recharge.request.AgentReqBody(
                    agentId = selectedAgentId,
                    amount = binding.etEnterAmount.text.toString(),
                    apiKey = apiKey,
                    paymentType = paymentType,
                    remarks = binding.etDescription.text.toString(),
                    responseFormat = "true",
                    status = status,
                    transactionType = transactionType,
                    travelAccountDate = getDateYMD(binding.etToDate.text.toString()),
                    cheque_description = chequeDDNumber,
                    locale = locale,
                    device_id = getDeviceUniqueId(this)
                )
            val agentListRequest =
                AgentRechargeRequest(bccId, format_type, credit_amount_method_name, reqBody)

            agentRechargeViewModel.agentRechargeApi(
                reqBody,
                manual_recharge_method_name
            )

        } else {
            noNetworkToast()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTicketDetailsObserver() {
        agentRechargeViewModel.branchRecharge.observe(this) {
            try {

                when (it.code) {
                    200 -> {
                        if (it.key.isNullOrEmpty()) {
                            apiCode = it.code
                            rechargeStatus = if (apiCode == 200) {
                                "Recharge Successful"
                            } else {
                                "Recharge Failed"
                            }

                            logFeatureSelectedEvent(
                                loginId = loginModelPref.userName,
                                operatorName = loginModelPref.travels_name,
                                roleName = loginModelPref.role,
                                rechargeFor = rechargeSelectedFor,
                                transactionType = transactionType,
                                paymentType = paymentType,
                                rechargeButton = rechargeStatus
                            )
                            Timber.d("Test Run agent recharge $it")
                            notifyUser(it.message)

                        } else {
                            branchData = it
                            otpValidationTime = it.otpValidationTime
                            latestTransactionNumber = if (!it.transactionNumber.isNullOrEmpty()) {
                                it.transactionNumber
                            } else {
                                it.result!!.transactionNumber
                            }
                            if (!isDialogShowing) {
                                showRechargeOTPDialog()
                            }
                        }
                    }

                    401 -> {
                       /* DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    else -> {
                        if (it?.result!!.message != null) {
                            it.result.message?.let { it1 -> notifyUserError(it1) }
                        }

                    }
                }
            }
            catch (t: Throwable) {
                toast("An error occurred during Branch Recharge")
            }
            binding.includeProgress.progressBar.gone()
            binding.buttonProceed.setBackgroundResource(R.drawable.button_selected_bg)
            binding.buttonProceed.isEnabled = true
        }

        agentRechargeViewModel.confirmBranchRecharge.observe(this) {

            try {
                apiCode = it.code
                rechargeStatus = if (apiCode == 200) {
                    "Recharge Successful"
                } else {
                    "Recharge Failed"
                }

                logFeatureSelectedEvent(
                    loginId = loginModelPref.userName,
                    operatorName = loginModelPref.travels_name,
                    roleName = loginModelPref.role,
                    rechargeFor = rechargeSelectedFor,
                    transactionType = transactionType,
                    paymentType = paymentType,
                    rechargeButton = rechargeStatus
                )
                Timber.d("Test Run agent recharge $it")

                when (it.code) {
                    200 -> {
                        bottomSheetDialog?.dismiss()
                        notifyUser(it.message)
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }
                    else -> {
                        if (it?.message != null) {
                            it.message.let { it1 -> notifyUserError(it1) }
                        }

                    }
                }
            } catch (t: Throwable) {
                toast("An error occurred during Branch Recharge")
            }
            binding.includeProgress.progressBar.gone()
            binding.buttonProceed.setBackgroundResource(R.drawable.button_selected_bg)
            binding.buttonProceed.isEnabled = true
        }

        agentRechargeViewModel.confirmAgentRecharge.observe(this) {
            try {
                apiCode = it.code
                rechargeStatus = if (apiCode == 200) {
                    "Recharge Successful"
                } else {
                    "Recharge Failed"
                }

                logFeatureSelectedEvent(
                    loginId = loginModelPref.userName,
                    operatorName = loginModelPref.travels_name,
                    roleName = loginModelPref.role,
                    rechargeFor = rechargeSelectedFor,
                    transactionType = transactionType,
                    paymentType = paymentType,
                    rechargeButton = rechargeStatus
                )

                Timber.d("Test Run agent recharge $it")

                when (it.code) {
                    200 -> {
                        bottomSheetDialog?.dismiss()
                        notifyUser("Recharge is successfully done. The Updated balance is: $currency ${it.agentRechargeResult[0].currentBalance}")
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    else -> {
                        notifyUserError(it.message)
                    }
                }

            } catch (t: Throwable) {
                toast("An error occurred during Agent Recharge")
            }
        }

        agentRechargeViewModel.agentRecharge.observe(this) {

            try {
                when (it.code) {
                    200 -> {
                        agentData = it
                        if (it.agentRechargeResult[0].key.isNullOrEmpty()) {
                            Timber.d("otp_time", otpValidationTime.toString())
                            apiCode = it.code
                            rechargeStatus = if (apiCode == 200) {
                                "Recharge Successful"
                            } else {
                                "Recharge Failed"
                            }

                            logFeatureSelectedEvent(
                                loginId = loginModelPref.userName,
                                operatorName = loginModelPref.travels_name,
                                roleName = loginModelPref.role,
                                rechargeFor = rechargeSelectedFor,
                                transactionType = transactionType,
                                paymentType = paymentType,
                                rechargeButton = rechargeStatus
                            )
                            Timber.d("Test Run agent recharge $it")
                            bottomSheetDialog?.dismiss()
                            notifyUser("Recharge is successfully done. The Updated balance is: $currency ${it.agentRechargeResult[0].currentBalance}")
                        } else {
                            otpValidationTime = it.agentRechargeResult[0].otpValidationTime
                            latestTransactionNumber = it.agentRechargeResult[0].transactionNumber
                            if (!isDialogShowing) {
                                showRechargeOTPDialog()
                            }
                        }
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    else -> {
                        notifyUserError(it.message)
                    }
                }
            } catch (t: Throwable) {
                toast("An error occurred during Agent Recharge")
            }
            binding.includeProgress.progressBar.gone()
            binding.buttonProceed.setBackgroundResource(R.drawable.button_selected_bg)
            binding.buttonProceed.isEnabled = true
        }
    }

    private fun onEditFields() {

        binding.etSelectAgent.onChange {
            validation()
        }
        binding.acTransactionType.onChange {
            validation()
        }
        binding.etToDate.onChange {
            validation()
        }
        binding.etEnterAmount.onChange {
            validation()
        }
        binding.acPaymentType.onChange {
            validation()
        }
        binding.etDescription.onChange {
            validation()
        }
        binding.acStatuses.onChange {
            validation()
        }
    }

    private fun validation() {
        if (binding.acPaymentType.text.toString() == rechargePaymentTypesList.getOrNull(2)) { // DD
            binding.layoutDdNumber.visible()
        } else {
            binding.layoutDdNumber.gone()
            binding.etDdNumber.setText("")
        }
        if (binding.acPaymentType.text.toString() == rechargePaymentTypesList.getOrNull(0)) { // Cheque
            binding.layoutChequeNumber.visible()
        } else {
            binding.layoutChequeNumber.gone()
            binding.etChequeNumber.setText("")
        }

        agentRechargeViewModel.validation(
            selectedAgentId,
            binding.acTransactionType.text.toString(),
            binding.etToDate.text.toString(),
            binding.etEnterAmount.text.toString(),
            binding.acPaymentType.text.toString(),
            binding.etDescription.text.toString(),
            binding.acStatuses.text.toString()
        )

        agentRechargeViewModel.validationData.observe(this, Observer {

            agentRechargeViewModel.loadingState.observe(this) {
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
                binding.buttonProceed.setBackgroundResource(R.drawable.button_selected_bg)
                binding.buttonProceed.isEnabled = true
            } else {
                binding.includeProgress.progressBar.gone()
                binding.buttonProceed.setBackgroundResource(R.drawable.button_default_bg)
                binding.buttonProceed.isEnabled = false
            }
        })
    }

    private fun notifyUser(message: String) {

        DialogUtils.oneTouchDialogSuccess(
            context = this,
            message = message,
            dialogSingleButtonListener = this
        )
    }

    private fun notifyUserError(message: String) {
        DialogUtils.oneTouchDialog(this, message, this)
    }

    override fun onSingleButtonClick(str: String) {

        if (str == "confirm") {
            if (binding.chkAgent.isChecked) {
                callAgentRechargeApi()
            } else {
                callBranchRechargeApi()
            }
            binding.includeProgress.progressBar.gone()
            binding.buttonProceed.setBackgroundResource(R.drawable.button_default_bg)
            binding.buttonProceed.isEnabled = false

        } else {
            if (apiCode == 200) {
//            onBackPressed()
                val intent = Intent(this, RechargeActivity::class.java)
                startActivity(intent)
                finish()
            }
            if (!manageBranchAccounting && !showManageAgentAccountLinkInAccount) {
//            onBackPressed()
                val intent = Intent(this, RechargeActivity::class.java)
                startActivity(intent)
                finish()
            }
            if (str == getString(R.string.unauthorized)) {
                //clearAndSave(requireContext())
                PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
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
            val userListRequest =
                UserListRequest(bccId, format_type, user_list_method_name, reqBody)

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
//            val reqBody = com.bitla.ts.domain.pojo.branch_list_model.request.ReqBody(apiKey, locale = locale)
//            val branchListRequest = BranchListRequest(bccId, format_type, branch_list_method_name, reqBody)

            /* blockViewModel.branchListApi(
                 loginModelPref.auth_token,
                 loginModelPref.api_key,
                 branchListRequest,
                 branch_list_method_name
             )*/

            blockViewModel.branchListApi(
                apiKey = loginModelPref.api_key,
                locale = locale ?: "",
                apiType = branch_list_method_name
            )

        } else
            noNetworkToast()
    }

    private fun setOnlineAgentListObserver() {

        blockViewModel.userList.observe(this) { it ->
            try {
                userList.clear()

                if (it.active_users != null && it.active_users.isNotEmpty()) {
                    it.active_users.forEach {
                        val spinnerItems = SpinnerItems(it.id, it.label, it.type ?: "")
                        userList.add(spinnerItems)
                        userListResponse.add(spinnerItems)
                    }
                }
            } catch (t: Throwable) {
                Timber.d("exceptionMsgUser ${t.message}")
                toast("An error occurred while fetching Agent List")
            }
        }

        blockViewModel.branchList.observe(this, Observer { it ->
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
        })
    }

    private fun logFeatureSelectedEvent(
        loginId: String?,
        operatorName: String?,
        roleName: String?,
        rechargeFor: String,
        transactionType: String,
        paymentType: String,
        rechargeButton: String
    ) {

        firebaseAnalytics.logEvent("recharge_option") {
            param(LOGIN_ID, loginId.toString())
            param(OPERATOR_NAME, operatorName.toString())
            param(ROLE_NAME, roleName.toString())
            param(RECHARGE_FOR, rechargeFor)
            param(PAYMENT_TYPE, paymentType)
            param(TRANSACTION_TYPE, transactionType)
            param(RECHARGE_BUTTON, rechargeButton)
        }
    }
}

