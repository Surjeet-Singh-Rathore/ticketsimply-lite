package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.ActivityManageAccountListBinding
import com.bitla.ts.databinding.DialogChangeToCompleteBinding
import com.bitla.ts.databinding.DialogTransactionInfoBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.manage_account_view.get_transaction_pdf_url.request.GetTransactionPdfUrlRequest
import com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.PagenationData
import com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.request.ReqBody
import com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.request.ShowTransactionListRequest
import com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.response.Result
import com.bitla.ts.domain.pojo.manage_account_view.transaction_info.request.TransactionInformationRequest
import com.bitla.ts.domain.pojo.manage_account_view.update_account_status.request.UpdateAccountStatusRequest
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.ManageAccountAdapter
import com.bitla.ts.presentation.adapter.PagenationNumberAdapter
import com.bitla.ts.presentation.viewModel.ManageAccountViewModel
import com.bitla.ts.utils.bluetooth_print.AsyncBluetoothEscPosPrint
import com.bitla.ts.utils.bluetooth_print.AsyncEscPosPrint
import com.bitla.ts.utils.bluetooth_print.AsyncEscPosPrinter
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.downloadPdf.DownloadPdf
import com.bitla.ts.utils.sharedPref.PREF_AGENT_ID
import com.bitla.ts.utils.sharedPref.PREF_BRANCH_ID
import com.bitla.ts.utils.sharedPref.PREF_CATEGORY_TYPE
import com.bitla.ts.utils.sharedPref.PREF_FROM_DATE
import com.bitla.ts.utils.sharedPref.PREF_LIST_TYPE
import com.bitla.ts.utils.sharedPref.PREF_LOGO
import com.bitla.ts.utils.sharedPref.PREF_TO_DATE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.bumptech.glide.Glide
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import gone
import isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*


class ManageAccountListActivity : BaseActivity(), DialogSingleButtonListener, OnItemPassData,
    DialogButtonAnyDataListener {

    private var privileges: PrivilegeResponseModel? = null
    private var currentUser: LoginModel? = null
    private var isTransTicketPrint = false
    private var multiTemplate: String = ""
    private var busLogo: Bitmap? = null
    private lateinit var binding: ActivityManageAccountListBinding
    private val manageAccountViewModel by viewModel<ManageAccountViewModel<Any?>>()
    var adapter: ManageAccountAdapter? = null
    private var template: String = ""
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var selectedDevice: BluetoothConnection? = null
    private lateinit var bccId: String
    private lateinit var apiKey: String
    private var locale: String? = ""
    private var loginModelPref: LoginModel = LoginModel()
    private var country: String? = null
    private var currency: String = ""
    private var currencyFormat: String = ""
    private var agentId: String = ""
    private var branchId: String = ""
    private var fromDate = ""
    private var toDate = ""
    private var categoryType = ""
    private var listType = ""
    private var manageAccountList: MutableList<Result> = arrayListOf()
    private var pagenationList: ArrayList<PagenationData> = arrayListOf()

    //    private var showTransactionData: ShowTransactionListResponse? = null
    private var pageAdapter: PagenationNumberAdapter? = null
    private var isPageAdapterSet: Boolean = true
    private var arrowLastPos = 1
    private var logo: String? = null
    private var totalRecords : Int = 0
    private var isBranch = false
    private var branchOrAgentName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUI()
        setShowManageListObserver()
        setGetTransactionPdfUrlObserver()
        setTransactionInformationObserver()
    }

    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityManageAccountListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        binding.apply {
            toolbarI.toolbarHeaderText.text = getString(R.string.agents_account)
            toolbarI.imgToolbarSearch.setImageDrawable(
                ContextCompat.getDrawable(
                    this@ManageAccountListActivity,
                    R.drawable.ic_search
                )
            )
            toolbarI.imgBack.setOnClickListener {
                onBackPressed()
            }
            toolbarI.imgToolbarSearch.setOnClickListener {
            }
        }

        getPref()
        getIntentData()
        callShowManageListApi(1)
        setShowManageListObserver()
        setGetTransactionPdfUrlObserver()

        binding.icLeftArrow.setOnClickListener {

            binding.icRightArrow.setBackgroundResource(R.drawable.ic_right_arrow_pagination_active)
            arrowLastPos--

            if (arrowLastPos == 1) {
                binding.icLeftArrow.setBackgroundResource(R.drawable.ic_left_arrow_pagination_disable)
                binding.icLeftArrow.isEnabled = false
                binding.icLeftArrow.isClickable = false
            }

            if (arrowLastPos > 0) {

                binding.includeProgress.progressBar.visible()

                for (i in 0 until pagenationList.size) {
                    pagenationList[i].isSelected = false
                }
                pagenationList[arrowLastPos - 1].isSelected = true
                pageAdapter?.notifyDataSetChanged()

                callShowManageListApi(arrowLastPos)
                isPageAdapterSet = false
            }
        }

        binding.apply {
            if (branchId.isNullOrEmpty()){
                toolbarI.toolbarHeaderText.text = getString(R.string.agents_account)
            } else {
                toolbarI.toolbarHeaderText.text = getString(R.string.branch_account)
            }
            toolbarI.icDotsGrey.visible()
            toolbarI.imageOptionLayout.visible()
            toolbarI.imgToolbarSearch.visible()

            toolbarI.imgBack.setOnClickListener {
                onBackPressed()
                PreferenceUtils.removeKey(PREF_LOGO)
            }

            toolbarI.icDotsGrey.setOnClickListener {
                showPopupMenu()
            }

            toolbarI.imgToolbarSearch.setOnClickListener {

                val intent = Intent(this@ManageAccountListActivity, ManageTransactionSearchActivity::class.java)
                intent.apply {
                    putExtra(PREF_BRANCH_ID, branchId)
                    putExtra(PREF_AGENT_ID, agentId)
                    putExtra(PREF_CATEGORY_TYPE, categoryType)
                    putExtra(PREF_LIST_TYPE, listType)
                    putExtra(PREF_FROM_DATE, fromDate)
                    putExtra(PREF_TO_DATE, toDate)
                }
                startActivity(intent)
            }

            icRightArrow.setOnClickListener {

                icLeftArrow.setBackgroundResource(R.drawable.ic_left_arrow_pagination_active)

                if (arrowLastPos >= pagenationList.size - 1) {
                    icRightArrow.setBackgroundResource(R.drawable.ic_right_arrow_pagination_disable)
                }

                if (arrowLastPos >= pagenationList.size) {
                    icRightArrow.setBackgroundResource(R.drawable.ic_right_arrow_pagination_disable)
                } else {
                    includeProgress.progressBar.visible()

                    icLeftArrow.isEnabled = true
                    icLeftArrow.isClickable = true

                    for (i in 0 until pagenationList.size) {
                        pagenationList[i].isSelected = false
                    }
                    pagenationList[arrowLastPos].isSelected = true
                    pageAdapter?.notifyDataSetChanged()

                    callShowManageListApi(arrowLastPos + 1)
                    arrowLastPos++
                    isPageAdapterSet = false
                }
            }
        }
    }

    override fun onDataSend(type: Int, file: Any) {
        when (type) {
            1 -> {
                val lastPos = file as Int
                arrowLastPos = lastPos + 1
                binding.includeProgress.progressBar.visible()

                for (i in 0 until pagenationList.size) {
                    pagenationList[i].isSelected = false
                }
                pagenationList[lastPos].isSelected = true
                pageAdapter?.notifyDataSetChanged()

                callShowManageListApi(lastPos + 1)
                isPageAdapterSet = false

                binding.apply {

                    if (lastPos == pagenationList.size - 1) {
                        icLeftArrow.setBackgroundResource(R.drawable.ic_left_arrow_pagination_active)
                        icRightArrow.setBackgroundResource(R.drawable.ic_right_arrow_pagination_disable)
                    } else {
                        icLeftArrow.setBackgroundResource(R.drawable.ic_left_arrow_pagination_active)
                    }

                    if (lastPos == 0) {
                        icLeftArrow.setBackgroundResource(R.drawable.ic_left_arrow_pagination_disable)
                        icLeftArrow.isEnabled = false
                        icLeftArrow.isClickable = false
                        icRightArrow.setBackgroundResource(R.drawable.ic_right_arrow_pagination_active)
                    } else {
                        icLeftArrow.isEnabled = true
                        icLeftArrow.isClickable = true
                    }
                }
            }
        }
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {


    }

    private fun setPageAdapter() {
        pageAdapter = PagenationNumberAdapter(this, pagenationList, this)
        binding.pagenationRV.adapter = pageAdapter
    }

    private fun getIntentData() {
        branchId = intent.getStringExtra(PREF_BRANCH_ID).toString()
        agentId = intent.getStringExtra(PREF_AGENT_ID).toString()
        categoryType = intent.getStringExtra(PREF_CATEGORY_TYPE).toString()
        listType = intent.getStringExtra(PREF_LIST_TYPE).toString()
        fromDate = intent.getStringExtra(PREF_FROM_DATE).toString()
        toDate = intent.getStringExtra(PREF_TO_DATE).toString()
    }

    private fun getPref() {
        privileges = getPrivilegeBase()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key

        if (privileges != null) {


            if (!privileges?.country.isNullOrEmpty()) {
                country = privileges?.country
            }

            currentUser = PreferenceUtils.getLogin()


            busLogo = getBitmapDirectFromUrl(currentUser?.logo_url ?: "")

            currency = if (!privileges?.currency.isNullOrEmpty())
                privileges?.currency.toString()
            else
                getString(R.string.rupeeSybbol)

            currencyFormat =
                privileges?.currencyFormat ?: getString(R.string.indian_currency_format)

        } else {
            toast(getString(R.string.server_error))
        }
    }


    private fun getBitmapDirectFromUrl(image: String): Bitmap? {
        var image1: Bitmap? = null
        try {
            CoroutineScope(Dispatchers.IO).run {
                val url = URL(image)
                image1 = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
        return image1
    }

    private fun showChangeToCompleteDialog(transactionNo: String) {
        val builder = AlertDialog.Builder(this).create()
        val inflater = LayoutInflater.from(this)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
        // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
        val binding: DialogChangeToCompleteBinding =
            DialogChangeToCompleteBinding.inflate(LayoutInflater.from(this))
        builder.setCancelable(false)

        binding.viewBT.setOnClickListener {
            builder.cancel()
            callUpdateAccountStatusApi(
                transactionNo = transactionNo,
                cashCheckDDNo = binding.etEnterAmount.text.toString()
            )
            setUpdateAccountStatusObserver()
        }

        binding.cancelTV.setOnClickListener {
            builder.cancel()
        }

        builder.setView(binding.root)
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

        logo = PreferenceUtils.getPreference(PREF_LOGO, "")

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
                isTransTicketPrint = true
                printTicket(transactionNo, agentName, transAmount, transBalance, date)
            }

            cancelTV.setOnClickListener {
                builder.cancel()
            }
        }

        builder.setView(binding.root)
        builder.show()
    }

    private fun setAdapter() {
        adapter = ManageAccountAdapter(this, manageAccountList, this,privileges)
        binding.recycleV.adapter = adapter
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun callGetTransactionPdfUrlApi() {

        if (isNetworkAvailable()) {

            binding.includeProgress.progressBar.visible()

            val reqBody =
                com.bitla.ts.domain.pojo.manage_account_view.get_transaction_pdf_url.request.ReqBody(
                    apikey = apiKey,
                    listType = listType.lowercase(Locale.getDefault()),
                    agentId = agentId,
                    branchId = branchId,
                    fromDate = fromDate,
                    toDate = toDate,
                    category = categoryType,
                    perticulerBrachId = 1,
                    isFromMiddleTier = true,
                    locale = locale
                )
            GetTransactionPdfUrlRequest(
                bcc_id = bccId,
                format = format_type,
                method_name = get_transaction_pdf_url_method_name,
                req_body = reqBody
            )

            manageAccountViewModel.getTransactionPdfUrlApi(
                getTransactionPdfUrlRequest = reqBody,
                methodName = get_transaction_pdf_url_method_name
            )

        } else {
            noNetworkToast()
        }
    }

    private fun setGetTransactionPdfUrlObserver() {
        manageAccountViewModel.getTransactionPdfUrlResponse.observe(this) {

            binding.includeProgress.progressBar.gone()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (!it.pdfUrl.isNullOrEmpty()) {
                            checkDownloadPermission(
                                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                pdfUri = it.pdfUrl
                            )

                        } else
                            toast(getString(R.string.no_pdf_link))
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

    private fun callShowManageListApi(pageCount: Int) {

        if (isNetworkAvailable()) {

            binding.includeProgress.progressBar.visible()
            binding.layoutNoData.root.gone()
            binding.recycleV.gone()

            when {
                categoryType.equals("Show all", true) -> {
                    categoryType = "-1"
                }

                categoryType.equals("Show only credits", true) -> {
                    categoryType = "1"
                }

                categoryType.equals("Show only debits", true) -> {
                    categoryType = "0"
                }

                categoryType.equals("Show not cleared", true) -> {
                    categoryType = "2"
                }

                categoryType.equals("Show manual credits/debits", true) -> {
                    categoryType = "3"
                }
            }

            val reqBody = ReqBody(
                apikey = apiKey,
                listType = listType.lowercase(Locale.getDefault()),
                agentId = agentId,
                branchId = branchId,
                fromDate = fromDate,
                toDate = toDate,
                category = categoryType,
                pageNo = pageCount,
                perPage = 20,
                pagination = true,
                isFromMiddleTier = true,
                locale = locale
            )
            ShowTransactionListRequest(
                bcc_id = bccId,
                format = format_type,
                method_name = show_transaction_method_name,
                req_body = reqBody
            )

            manageAccountViewModel.showTransactionListApi(
                showTransactionListRequest = reqBody,
                methodName = show_transaction_method_name
            )

        } else {
            noNetworkToast()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setShowManageListObserver() {
        lifecycleScope.launch {
            manageAccountViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        manageAccountViewModel.showTransactionListResponse.observe(this) {

            binding.includeProgress.progressBar.gone()

            if (it != null) {

                when (it.code) {
                    200 -> {

                        manageAccountList = it.result
                        totalRecords = it.totalRecords
                        if (it.result[0].branchName?.isNotEmpty() == true){
                            binding.toolbarI.toolbarHeaderText.text = "${getString(R.string.branch_account)} ($totalRecords)"
                        } else {
                            binding.toolbarI.toolbarHeaderText.text = "${getString(R.string.agents_account)} ($totalRecords)"
                        }

                        if (isPageAdapterSet) {
                            pagenationList.clear()
                            for (i in 0 until it.totalPages) {
                                val obj = PagenationData()
                                obj.position = i
                                obj.isSelected = false
                                pagenationList.add(obj)
//                                Timber.d("paginationList - ${pagenationList.size} ")
                            }

                            if (pagenationList.size > 0) {
                                pagenationList[0].isSelected = true
                                setPageAdapter()
                                binding.pagenationRVContainer.visible()
                            } else {
                                binding.pagenationRVContainer.gone()
                            }

                            if (it.totalPages == 1) {
                                binding.pagenationRVContainer.gone()
                            } else {
                                binding.pagenationRVContainer.visible()
                            }
                        }

                        setAdapter()
                        binding.layoutNoData.root.gone()
                        binding.recycleV.visible()
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
                        binding.recycleV.gone()
                        binding.pagenationRVContainer.gone()
                    }

                    else -> {
                        if (!it.message.isNullOrEmpty()) {
                            binding.layoutNoData.tvNoData.text = it.message
                        } else {
                            binding.layoutNoData.tvNoData.text =
                                getString(R.string.something_went_wrong)
                        }
                        binding.layoutNoData.root.visible()
                        binding.recycleV.gone()
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun callUpdateAccountStatusApi(transactionNo: String, cashCheckDDNo: String) {

        if (isNetworkAvailable()) {

            binding.includeProgress.progressBar.visible()

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
            binding.includeProgress.progressBar.gone()
            if (it != null) {

                when (it.code) {
                    200 -> {

                        if (it.message.isNotEmpty()) {
                            toast(it.message)
                            callShowManageListApi(1)
//                            Timber.d("testUpdateStatus - call")
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

        if (isNetworkAvailable()) {

            binding.includeProgress.progressBar.visible()

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


    private fun printList() {
        multiTemplate += "$branchOrAgentName Account - Transaction info between $fromDate to $toDate\n\n"

        for (i in manageAccountList) {

            template = "$branchOrAgentName Name : AGENT_NAME\n" +
                    "Trans No : TRANSACTION_NUMBER\n" +
                    "Type : TYPE\n" +
                    "Payment Type : PAYMENT_TYPE\n" +
                    "DD/Cheque No : DD_NO\n" +
                    "Amount(Rs) : AMOUNT\n" +
                    "Balance(Rs) : BALANCE\n" +
                    "Updated On : UPDATED_ON\n" +
                    "Updated By : UPDATED_BY\n" +
                    "Created On : CREATED_ON\n" +
                    "Status : STATUS\n" +
                    "END_LINE"

            if(branchOrAgentName == "Agent"){
                if (template.contains("AGENT_NAME")) {
                    template = template.replace("AGENT_NAME", i.agentName.toString())
                } else {
                    template = template.replace("AGENT_NAME", "")
                }
            }else{
                if (template.contains("_NAME")) {
                    template = template.replace("AGENT_NAME", i.branchName.toString())
                } else {
                    template = template.replace("AGENT_NAME", "")
                }
            }


            if (template.contains("TRANSACTION_NUMBER")) {
                template = template.replace("TRANSACTION_NUMBER", i.transactionNo)
            } else {
                template = template.replace("TRANSACTION_NUMBER", "")
            }

            if (template.contains("PAYMENT_TYPE")) {
                template = template.replace("PAYMENT_TYPE", i.paymentType)
            } else {
                template = template.replace("PAYMENT_TYPE", "")
            }

            if (template.contains("TYPE")) {
                template = template.replace("TYPE", i.transactionType)
            } else {
                template = template.replace("TYPE", "")
            }

            if (!i.ddChequeNo.isNullOrEmpty()) {
                if (template.contains("DD_NO")) {
                    template = template.replace("DD_NO", i.ddChequeNo)
                }
            } else {
                template = template.replace("DD_NO", "")
            }

            if (template.contains("AMOUNT")) {
                template = template.replace("AMOUNT", i.amount.toString())
            } else {
                template = template.replace("AMOUNT", "")
            }

            if (template.contains("BALANCE")) {
                template = template.replace("BALANCE", i.balance.toString())
            } else {
                template = template.replace("BALANCE", "")
            }
            if (template.contains("UPDATED_ON")) {
                template = template.replace("UPDATED_ON", i.updatedOn)
            } else {
                template = template.replace("UPDATED_ON", "")
            }
            if (template.contains("UPDATED_BY")) {
                template = template.replace("UPDATED_BY", i.updatedBy)
            } else {
                template = template.replace("UPDATED_BY", "")
            }
            if (template.contains("CREATED_ON")) {
                template = template.replace("CREATED_ON", i.createdOn)
            } else {
                template = template.replace("CREATED_ON", "")
            }

            if (template.contains("STATUS")) {
                template = template.replace("STATUS", i.status)
            } else {
                template = template.replace("STATUS", "")
            }

            if (template.contains("END_LINE")) {
                template = template.replace("END_LINE", "\n\n\n")
            }

            multiTemplate += template


        }

        multiTemplate += "Printed By : ${currentUser!!.name}\n"
        multiTemplate += "Printed On : ${getTodayDateWithTime()} "

        //Timber.e(multiTemplate)


        checkPermissions()

    }


    fun decodeBitmap(image: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream) // Convert the Bitmap to a PNG format
        return stream.toByteArray()
    }

    private fun printTicket(
        transactionNo: String,
        agentName: String,
        transAmount: String,
        transBalance: String,
        date: String
    ) {


        template =
            "OPERATOR_LOGO\n" +
                    "Transaction Number : TRANSACTION_NUMBER\n" +
                    "$branchOrAgentName Name : AGENT_NAME\n" +
                    "Amount(Rs) : AMOUNT\n" +
                    "Balance(Rs) : BALANCE\n" +
                    "Date : DATE_TIME\n\n" +
                    "[C]Receiver's Signature[C]\n" + "[C](Receiver's Name)[C]"




        if (!transactionNo.isNullOrEmpty()) {
            template = template.replace("TRANSACTION_NUMBER", agentName)
        } else {
            template = template.replace("TRANSACTION_NUMBER", "")

        }

        if (!agentName.isNullOrEmpty()) {
            template = template.replace("AGENT_NAME", transactionNo)
        } else {
            template = template.replace("AGENT_NAME", "")

        }
        if (!transactionNo.isNullOrEmpty()) {
            template = template.replace("AMOUNT", transAmount)
        } else {
            template = template.replace("AMOUNT", "")

        }
        if (!transBalance.isNullOrEmpty()) {
            template = template.replace("BALANCE", transBalance)
        } else {
            template = template.replace("BALANCE", "")

        }
        if (!date.isNullOrEmpty()) {
            template = template.replace("DATE_TIME", date)
        } else {
            template = template.replace("DATE_TIME", "")

        }

        //Timber.e(template.toString())


        checkPermissions()
    }

    private fun checkDownloadPermission(permission: String, pdfUri: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, this)
        if (Build.VERSION.SDK_INT >= 33) {
            DownloadPdf.downloadReportPdf(this, pdfUri)

        } else {
            if (permissionResult) {
                DownloadPdf.downloadReportPdf(this, pdfUri)

            } else {
                DownloadPdf.onRequestPermissionsResult(STORAGE_PERMISSION_CODE, permission, this)
            }
        }

    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PERMISSION_BLUETOOTH_ADMIN
            )
        } else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    PERMISSION_BLUETOOTH_CONNECT
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                    PERMISSION_BLUETOOTH_SCAN
                )
            } else
                enableDeviceBluetooth()
    }

    @SuppressLint("MissingPermission")
    private fun enableDeviceBluetooth() {
        try {
            val bluetoothManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.getSystemService(BluetoothManager::class.java)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getSystemService(BluetoothManager::class.java)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            }
            mBluetoothAdapter = bluetoothManager.adapter
            if (mBluetoothAdapter != null) {
                if (!mBluetoothAdapter?.isEnabled!!) {
                    val enableBtIntent = Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE
                    )
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                } else {
                    browseBluetoothDevice()
                }
            }
        } catch (e: Exception) {
//            handle
        }
    }

    @SuppressLint("MissingPermission")
    private fun browseBluetoothDevice() {
        try {
            val bluetoothDevicesList = BluetoothPrintersConnections().list
            if (bluetoothDevicesList != null) {
                val items = arrayOfNulls<String>(bluetoothDevicesList.size)
                var i = 0
                for (device in bluetoothDevicesList) {
                    items[i++] = device.device.name
                }

                if (items.isNotEmpty()) {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle(getString(R.string.bluetooth_printer_selection))
                    alertDialog.setItems(
                        items
                    ) { dialogInterface, i ->
                        val index = i
                        if (index == -1) {
                            selectedDevice = null
                        } else {
                            selectedDevice = bluetoothDevicesList[index]
                            //privilegeResponseModel?.availableAppModes?.allowReprint = true
                            printBluetooth()

                        }
                    }

                    val alert = alertDialog.create()
                    alert.setCanceledOnTouchOutside(true)
                    alert.show()
                } else
                    toast(getString(R.string.no_paired_devices))
            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    private fun printBluetooth() {
        AsyncBluetoothEscPosPrint(
            this,
            object : AsyncEscPosPrint.OnPrintFinished() {
                override fun onError(
                    asyncEscPosPrinter: AsyncEscPosPrinter?,
                    codeException: Int
                ) {
                    Timber.d(
                        "Async.OnPrintFinished",
                        "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                    )
                }

                override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                    Timber.d(
                        "Async.OnPrintFinished",
                        "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                    )
                }
            }
        )
            .execute(this.getAsyncEscPosPrinter(selectedDevice))
    }

    private fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)

        return printer.addTextToPrint(
            if(isTransTicketPrint){
                if (busLogo != null) {
                    val hexaDecimalString = PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        busLogo
                    )
                    template = template.replace("OPERATOR_LOGO", "[C]<img>${
                        hexaDecimalString
                    }</img>")
                }else{
                    template = template.replace("OPERATOR_LOGO","")
                }
                template.trim()
            }else{
                multiTemplate.trim()
            }
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

    override fun onItemData(view: View, str1: String, str2: String) {

        if (view.tag == STATUS) {
            showChangeToCompleteDialog(str1)
        } else {
            callTransactionInformationApi(str1)
        }
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPopupMenu() {
        val popup = PopupMenu(this@ManageAccountListActivity, binding.toolbarI.icDotsGrey)
        popup.inflate(R.menu.custom_menu)
        popup.gravity = Gravity.END

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }

        popup.setOnMenuItemClickListener {
                item -> onMenuItemClick(item)
        }

        popup.menu.getItem(0).title = "Download"


        popup.menu.getItem(1).isVisible = false
        popup.menu.getItem(2).isVisible = false
        popup.menu.getItem(3).isVisible = false
        popup.menu.getItem(4).isVisible = false
        popup.menu.getItem(5).isVisible = country != "India"


        popup.show()
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.downloadMI -> {
                callGetTransactionPdfUrlApi()
            }

           /* R.id.wifiPrint -> {
                if(manageAccountList != null && manageAccountList.size > 0){
                    isTransTicketPrint = false
                    printList()
                }else{
                    toast(getString(R.string.please_wait_while_data_is_being_loaded))
                }
            }*/
        }
        return false
    }
}