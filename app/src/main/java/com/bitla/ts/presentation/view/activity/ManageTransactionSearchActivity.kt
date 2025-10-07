package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.data.search_transaction_data_method_name
import com.bitla.ts.data.transaction_info_method_name
import com.bitla.ts.data.update_account_status_method_name
import com.bitla.ts.databinding.ActivityManageTransactionSearchBinding
import com.bitla.ts.databinding.DialogChangeToCompleteBinding
import com.bitla.ts.databinding.DialogTransactionInfoBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.manage_account_view.manage_transaction_search.request.ManageTransactionSearchRequest
import com.bitla.ts.domain.pojo.manage_account_view.manage_transaction_search.request.ReqBody
import com.bitla.ts.domain.pojo.manage_account_view.manage_transaction_search.response.Result
import com.bitla.ts.domain.pojo.manage_account_view.transaction_info.request.TransactionInformationRequest
import com.bitla.ts.domain.pojo.manage_account_view.update_account_status.request.UpdateAccountStatusRequest
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.ManageTransactionSearchAdapter
import com.bitla.ts.presentation.viewModel.ManageAccountViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.STATUS
import com.bitla.ts.utils.sharedPref.PREF_AGENT_ID
import com.bitla.ts.utils.sharedPref.PREF_BRANCH_ID
import com.bitla.ts.utils.sharedPref.PREF_CATEGORY_TYPE
import com.bitla.ts.utils.sharedPref.PREF_FROM_DATE
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_LIST_TYPE
import com.bitla.ts.utils.sharedPref.PREF_LOGO
import com.bitla.ts.utils.sharedPref.PREF_TO_DATE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.bumptech.glide.Glide
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible
import java.util.Locale

class ManageTransactionSearchActivity : BaseActivity(),
    DialogSingleButtonListener, OnItemPassData {

    private var privileges: PrivilegeResponseModel? = null
    lateinit var binding: ActivityManageTransactionSearchBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var loginModelPref: LoginModel = LoginModel()
    private lateinit var bccId: String
    private lateinit var apiKey: String
    private var locale: String? = ""
    private var manageTransactionSearchList: MutableList<Result> = arrayListOf()
    private val manageAccountViewModel by viewModel<ManageAccountViewModel<Any?>>()
    private var agentId: String = ""
    private var branchId: String = ""
    private var categoryType = ""
    private var listType = ""
    private var fromDate = ""
    private var toDate = ""
    private var logo: String? = null
    private var currency: String = ""
    private var currencyFormat: String = ""
    private var country: String? = null
    private var isBranch = false
    private var branchOrAgentName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUI()
        setTransactionInformationObserver()
    }

    override fun initUI() {
        binding = ActivityManageTransactionSearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        showSoftKeyboard(binding.etTransactionNoSearch)
        getPref()
        getIntentData()

        lifecycleScope.launch {
            manageAccountViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                   showToast(it)
                }
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
            PreferenceUtils.removeKey(PREF_LOGO)
        }

        binding.etTransactionNoSearch.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_NEXT) {

                manageTransactionSearchList.clear()
                callManageTransactionSearchApi()

            }
            false
        }

        binding.etTransactionNoSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int,
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int,
            ) {
            }
        })
    }

    private fun getIntentData() {
        branchId = intent.getStringExtra(PREF_BRANCH_ID).toString()
        agentId = intent.getStringExtra(PREF_AGENT_ID).toString()
        categoryType = intent.getStringExtra(PREF_CATEGORY_TYPE).toString()
        listType = intent.getStringExtra(PREF_LIST_TYPE).toString()
        fromDate = intent.getStringExtra(PREF_FROM_DATE).toString()
        toDate = intent.getStringExtra(PREF_TO_DATE).toString()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        privileges = getPrivilegeBase()

        if (privileges!= null) {


            privileges?.apply {

                if (!privileges?.country.isNullOrEmpty()) {
                    country = privileges?.country.toString()
                }
                currency = if (currency.isNullOrEmpty())
                    currency
                else
                    getString(R.string.rupeeSybbol)

                currencyFormat = getCurrencyFormat(this@ManageTransactionSearchActivity, privileges?.currencyFormat)
                
            }
        } else {
            toast(getString(R.string.server_error))
        }
    }

    private fun callManageTransactionSearchApi() {
        binding.rvTransactionDetails.gone()
        binding.includeProgress.progressBar.visible()

        if (isNetworkAvailable()) {
            val reqBody = ReqBody(
                apikey = apiKey,
                search = binding.etTransactionNoSearch.text.toString(),
                listType = listType.lowercase(Locale.getDefault()),
                agentId = agentId,
                branchId = branchId,
                category = categoryType,
                fromDate = fromDate,
                toDate = toDate,
                isFromMiddleTier = true,
                locale = locale
            )

            ManageTransactionSearchRequest(
                bcc_id = bccId,
                format = format_type,
                method_name = search_transaction_data_method_name,
                req_body = reqBody
            )

            manageAccountViewModel.manageTransactionSearchApi(
                manageTransactionSearchReq = reqBody,
                methodName = search_transaction_data_method_name
            )

            manageAccountViewModel.manageTransactionSearchResponse.observe(this) {

                binding.includeProgress.progressBar.gone()

                if (it != null) {

                    when (it.code) {
                        200 -> {
                            manageTransactionSearchList = it.result
                            binding.layoutNoData.root.gone()
                            binding.rvTransactionDetails.visible()
                            setManageTransactionSearchAdapter()
                        }

                        401 -> {
                            /*DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            showUnauthorisedDialog()

                        }

                        404 -> {
                            if (!it.message.isNullOrEmpty()) {
                                binding.layoutNoData.tvNoData.text = it.message
                            } else {
                                binding.layoutNoData.tvNoData.text =
                                    getString(R.string.something_went_wrong)
                            }

                            binding.layoutNoData.root.visible()
                            binding.rvTransactionDetails.gone()
                        }

                        else -> {
//                        if (!it.message.isNullOrEmpty()) {
//                            binding.layoutNoData.tvNoData.text = it.message
//                        } else {
//                            binding.layoutNoData.tvNoData.text = getString(R.string.something_went_wrong)
//                        }
//                        binding.layoutNoData.root.visible()
//                        binding.recycleV.gone()
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            }

        } else {
            noNetworkToast()
        }
    }

    private fun setManageTransactionSearchAdapter() {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvTransactionDetails.layoutManager = layoutManager
        val adapter = ManageTransactionSearchAdapter(this, manageTransactionSearchList, this,privileges)
        binding.rvTransactionDetails.adapter = adapter
    }

    private fun callUpdateAccountStatusApi(transactionNo: String, cashCheckDDNo: String) {

        binding.includeProgress.progressBar.visible()

        if (isNetworkAvailable()) {
            val reqBody = UpdateAccountStatusRequest(
                apikey = apiKey,
                transactionNo = transactionNo,
                details = cashCheckDDNo,
                isFromMiddleTier = true,
                locale = locale
            )

            manageAccountViewModel.updateAccountStatusApi(
                reqBody = reqBody,
                apiType = update_account_status_method_name
            )

        } else {
            noNetworkToast()
        }
    }

    private fun setUpdateAccountStatusObserver() {
        manageAccountViewModel.updateAccountStatusResponse.observe(this) {

            if (it != null) {

                when (it.code) {
                    200 -> {

                        if (it.message.isNotEmpty()) {
                            toast(it.message)
                            callManageTransactionSearchApi()
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

                    404 -> {
                        toast(getString(R.string.opps))
                    }

                    else -> {
                        if (!it.message.isNullOrEmpty()) {
                            toast(it.message)
                        } else {
                            toast(getString(R.string.something_went_wrong))
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun callTransactionInformationApi(transactionInformationNo: String) {

        binding.includeProgress.progressBar.visible()
        binding.rvTransactionDetails.gone()

        if (isNetworkAvailable()) {
            val reqBody =
                com.bitla.ts.domain.pojo.manage_account_view.transaction_info.request.ReqBody(
                    apikey = apiKey,
                    transactionNo = transactionInformationNo,
                    fromDate = fromDate,
                    toDate = toDate,
                    isFromMiddleTier = true,
                    locale = locale.toString()
                )
            TransactionInformationRequest(
                bcc_id = bccId,
                format = format_type,
                method_name = transaction_info_method_name,
                req_body = reqBody
            )

            manageAccountViewModel.transactionInfoApiApi(
                transactionInfoRequest = reqBody,
                methodName = transaction_info_method_name
            )

        } else {
            noNetworkToast()
        }
    }

    private fun setTransactionInformationObserver() {
        manageAccountViewModel.transactionInformationResponse.observe(this) {

            binding.includeProgress.progressBar.gone()
            binding.rvTransactionDetails.visible()

            if (it != null) {

                when (it.code) {
                    200 -> {

                        if (it.result[0].branchName?.isNotEmpty() == true){
                            branchOrAgentName = it.result[0].branchName.toString()
                            isBranch = true
                        } else {
                            branchOrAgentName =  it.result[0].agentName.toString()
                            isBranch  = false
                        }

                        showTransactionInfoDialog(
                            isBranch,
                            agentName = branchOrAgentName,
                            transactionNo = it.result[0].transactionNo,
                            amount = it.result[0].amount,
                            balance = it.result[0].balance,
                            date = it.result[0].date,
                        )
                    }

                    401 -> {
                       /* DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    404 -> {
                        toast(getString(R.string.opps))
                    }

                    else -> {
                        if (it.message.isNullOrEmpty()) {
                            toast(it.message)
                        } else {
                            toast(getString(R.string.something_went_wrong))
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }
    private fun showChangeToCompleteDialog(transactionNo: String) {
        val builder = AlertDialog.Builder(this).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
        val bindingDialogChangeToComplete: DialogChangeToCompleteBinding =
            DialogChangeToCompleteBinding.inflate(LayoutInflater.from(this))
        builder.setCancelable(false)

        bindingDialogChangeToComplete.viewBT.setOnClickListener {
            builder.cancel()
            binding.rvTransactionDetails.gone()
            callUpdateAccountStatusApi(
                transactionNo = transactionNo,
                cashCheckDDNo = bindingDialogChangeToComplete.etEnterAmount.text.toString()
            )
            setUpdateAccountStatusObserver()
        }

        bindingDialogChangeToComplete.cancelTV.setOnClickListener {
            builder.cancel()
        }

        builder.setView(bindingDialogChangeToComplete.root)
        builder.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showTransactionInfoDialog(
        isBranch : Boolean,
        agentName: String,
        transactionNo: String,
        amount: String,
        balance: String,
        date: String
    ) {
        val builder = AlertDialog.Builder(this).create()
        val inflater = LayoutInflater.from(this)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
        // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
        val binding: DialogTransactionInfoBinding =
            DialogTransactionInfoBinding.inflate(LayoutInflater.from(this))
        builder.setCancelable(false)

        if (PreferenceUtils.getPreference(PREF_LOGO, getString(R.string.empty)) != null)
            logo = PreferenceUtils.getPreference(PREF_LOGO, getString(R.string.empty))

        if (logo != null) {
            binding.operatorLogoIV.visible()
            Glide.with(this).load(logo)
                .override(600, 200)
                .fitCenter()
                .error(R.drawable.ic_ts_logo)
                .into(binding.operatorLogoIV)
        } else {
            binding.operatorLogoIV.visible()
            Glide.with(this)
                .load(R.drawable.ic_ts_logo)
                .override(600, 200)
                .fitCenter()
                .placeholder(R.drawable.ic_ts_logo)
                .error(R.drawable.ic_ts_logo)
                .into(binding.operatorLogoIV)
        }

        val transAmount = if (amount.isNotEmpty()) (amount
            .toDouble()).convert(currencyFormat) else amount

        val transBalance = if (balance.isNotEmpty()) (balance
            .toDouble()).convert(currencyFormat) else balance

        binding.apply {

            if (isBranch) {
                agentNameTV.text = getString(R.string.branch_name)
            } else {
                agentNameTV.text = getString(R.string.agent_name)
            }

            transNoValueTV.text = transactionNo
            agentNameValueTV.text = agentName
            typeValueTV.text = "$currency $transAmount"
            paymentTypeValueTV.text = "$currency $transBalance"
            chequeNoValueTV.text = date

            printBT.setOnClickListener {
//                printTicket()
            }

            cancelTV.setOnClickListener {
                builder.cancel()
            }
        }

        builder.setView(binding.root)
        builder.show()
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val inputMethodManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onItemData(view: View, str1: String, str2: String) {

        if (view.tag == STATUS) {
            showChangeToCompleteDialog(str1)
        } else {
            callTransactionInformationApi(str1)
        }
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
    }
}