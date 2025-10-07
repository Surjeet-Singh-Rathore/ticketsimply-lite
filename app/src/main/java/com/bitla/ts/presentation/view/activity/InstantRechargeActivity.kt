package com.bitla.ts.presentation.view.activity


import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.*
import androidx.core.os.postDelayed
import com.bitla.ts.BuildConfig
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.instant_recharge.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.phonepe.HtmlResponse
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.dashboard.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.presentation.viewModel.AgentRechargeViewModel.Companion.TAG
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.easebuzz.payment.kit.*
import com.razorpay.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import noNetworkToast
import org.json.*
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.util.*

class InstantRechargeActivity : BaseActivity(), DialogButtonAnyDataListener,
    PaymentResultWithDataListener, DialogSingleButtonListener {


    private var privilegeData: PrivilegeResponseModel? = null
    private var finalAmount: String = ""
    private var pnrNumber: String = ""
    private var emailId: String = ""
    private var phoneNumber: String = ""

    private var pgList: ArrayList<PgData> = arrayListOf()
    private var pgListPayBitla : MutableList<String> = listOf<String>().toMutableList()
    private var pgNamePayBitla : String= ""

    private lateinit var binding: ActivityInstantRechargeBinding
    private var selectedPgType: String = ""
    private var is_easebuzz_payment: Boolean = true
    private var orderIdEaseBuzz: String = ""

    private val agentRechargeViewModel by viewModel<AgentRechargeViewModel<Any?>>()
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String = ""
    private lateinit var apiKey: String
    private var adapter: PaymentGatewayAdapter? = null

    private var orderIdPhonePeV2: String = ""
    private var isPhonePeV2CancelClicked: Boolean = false
    private var showPhonePeV2PendingDialog: Boolean = false
    private lateinit var phonePeV2PendingDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUI()
        getAgentPGDetailObserver()
        getPGtransactionStatusObserver()
        payBitlaStatusObserver()
        getAgentTransactionObserver()
        phonePeV2StatusObserver()

        //razorPayStatusObserver()

        if (intent.hasExtra("pnr_number")) {
            pnrNumber = intent.getStringExtra("pnr_number") ?: ""
        }
        if (!pnrNumber.isNullOrEmpty()) {
            if(intent.getBooleanExtra("is_pay_bitla",false) == true){
                hitPayBitlaStatusApi(pnrNumber)
            }else{
                hitPhonePeTransactionStatusApi(pnrNumber)

            }
        }

        lifecycleScope.launch {
            agentRechargeViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
//                    showToast(it)
                }
            }
        }
    }

    private fun getPGtransactionStatusObserver() {
        agentRechargeViewModel.getPhonePeTransStatus.observe(this) {
            var amount = ""
            if (intent.hasExtra("amount")) {
                amount = intent.getStringExtra("amount") ?: ""
            } else {
                amount = finalAmount
            }
            if (it.message != null) {
                showDialog(it.message, amount)
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun initUI() {
        getPref()

        binding = ActivityInstantRechargeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }


        binding.rechargeAppBar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        val mcalendar = Calendar.getInstance()
        val day = mcalendar.get(Calendar.DAY_OF_MONTH)
        val year = mcalendar.get(Calendar.YEAR)
        val monthname: String = android.text.format.DateFormat.format("MMM", Date()) as String
        binding.rechargeAppBar.headerTitleDesc.text =
            "${resources.getString(R.string.today)}, $monthname ${day}, $year"

        binding.rechargeAppBar.textHeaderTitle.text = getString(R.string.recharge)


        binding.etEnterAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(1000)
                        getAgentTransactionDetailsApi(s.toString())
                        binding.buttonProceed.isEnabled = true
                        binding.buttonProceed.background = ContextCompat.getDrawable(
                            this@InstantRechargeActivity,
                            R.color.colorPrimary
                        )
                    }

                } else {
                    binding.etTransactionCharge.setText("")
                    binding.netAmountET.setText("")
                    binding.buttonProceed.isEnabled = false
                    binding.buttonProceed.background =
                        ContextCompat.getDrawable(this@InstantRechargeActivity, R.color.littleGrey)

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

        if (pgList != null && pgList.size > 0) {
            setPGAdapter()
        }

        binding.buttonProceed.setOnClickListener {
            if(binding.netAmountET.text.toString().isNotEmpty()){
                val amount= binding.netAmountET.text.toString().toFloat()
                if (amount <= 0f) {
                    toast(getString(R.string.instant_recharge_amount_validation))
                } else {
                    if(!selectedPgType.isNullOrEmpty()){
                        val amountt = binding.netAmountET.text.toString()
                        getAgentPGDetailApi(amountt, selectedPgType)
                    }else{
                        toast(getString(R.string.please_select_the_payment_method))
                    }

                }
            }else{
                toast(getString(R.string.please_enter_valid_amount))

            }

        }
    }

    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        isPhonePeV2CancelClicked = false
        showPhonePeV2PendingDialog = true
        callPhonePeV2StatusApi()
    }

    private fun callPhonePeV2StatusApi() {
        if (isNetworkAvailable()) {
            agentRechargeViewModel.getPhonePeV2Status(apiKey, orderIdPhonePeV2)
        } else
            noNetworkToast()
    }

    private fun phonePeV2StatusObserver() {
        agentRechargeViewModel.phonePeV2StatusResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        when (it.status) {
                            "COMPLETED" -> {
                                hitPhonePeV2SuccessConPay()
                                closePhonePeV2PendingDialog()
                                showDialogRazorpay(
                                    getString(R.string.payment_successful),
                                    finalAmount,
                                    R.drawable.phonepe_success
                                )
                            }
                            "PENDING" -> {
                                if (!isPhonePeV2CancelClicked) {
                                    Handler(Looper.getMainLooper()).postDelayed(1000) {
                                        callPhonePeV2StatusApi()
                                    }
                                }
                                if (showPhonePeV2PendingDialog) {
                                    showPhonePeV2PendingDialog = false
                                    phonePeV2PendingDialog = DialogUtils.phonePeV2PendingDialog(
                                        this,
                                        this,
                                        getString(R.string.payment_pending),
                                        getString(R.string.payment_pending_msg),
                                        getString(R.string.payment_pending_desc),
                                        getString(R.string.cancel_payment),
                                    )
                                }
                            }
                            "FAILED" -> {
                                closePhonePeV2PendingDialog()
                                showDialogRazorpay(
                                    getString(R.string.payment_failed),
                                    finalAmount,
                                    R.drawable.phonepe_fail
                                )
                            }
                            else -> {
                                toast(getString(R.string.server_error))
                            }
                        }
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        toast(getString(R.string.something_went_wrong))
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun closePhonePeV2PendingDialog() {
        if (::phonePeV2PendingDialog.isInitialized) {
            phonePeV2PendingDialog.dismiss()
        }
    }

    private fun hitPhonePeV2SuccessConPay() {
        if (isNetworkAvailable()) {
            agentRechargeViewModel.phonePeV2RechargeSuccessConPay(
                pnrNumber = orderIdPhonePeV2
            )
        } else {
            noNetworkToast()
        }
    }

    @SuppressLint("SetTextI18n")
    fun showDialog(response: String, amount: String) {
        val dialogBuilder = AlertDialog.Builder(this, R.style.BottomSheetDialogInstantRecharge)
        val inflater = LayoutInflater.from(this)
        val dialogBinding = DialogPhonepeSuccessFailBinding.inflate(inflater)

        dialogBuilder.setCancelable(true)


        dialogBinding.amountTV.text = "Amount : Rs ${amount}"
        //dialogBinding.amountTV.text = "Amount : Rs ${binding.etEnterAmount.toString()}"

        if (response != "success") {
            dialogBinding.imageIV.setImageResource(R.drawable.phonepe_fail)
            if(response.isNullOrEmpty()){
                dialogBinding.paymentTitleTV.text = getString(R.string.payment_failed)
            }else{
                dialogBinding.paymentTitleTV.text = "Payment ${response}"

            }
        } else {
            dialogBinding.imageIV.setImageResource(R.drawable.phonepe_success)
            dialogBinding.paymentTitleTV.text = getString(R.string.payment_successful)

        }


        val alertDialog = dialogBuilder.setView(dialogBinding.root).create()

        if (!alertDialog.isShowing) {
            alertDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun showDialogRazorpay(message: String, amount: String, image: Int) {
        val dialogBuilder = AlertDialog.Builder(this, R.style.BottomSheetDialog)
        val inflater = LayoutInflater.from(this)
        val dialogBinding = DialogPhonepeSuccessFailBinding.inflate(inflater)

        dialogBuilder.setCancelable(true)


        dialogBinding.amountTV.text = "Amount : Rs ${amount}"


        dialogBinding.imageIV.setImageResource(image)
        dialogBinding.paymentTitleTV.text = message


        val alertDialog = dialogBuilder.setView(dialogBinding.root).create()

        if (!alertDialog.isShowing) {
            alertDialog.show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardNavigateActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun razorPayPayment(
        merchantKey: String,
        name: String,
        description: String,
        image: String,
        themeColor: String,
        currency: String,
        order_id: String,
        amount: Any,
        cust_email: String,
        cust_contact: String
    ) {/*
       *  You need to pass current activity in order to let Razorpay create CheckoutActivity
       * */
        val activity: Activity = this
        val co = Checkout()
        co.setKeyID(merchantKey) // It will come separate API


        try {
            val options = JSONObject()
            options.put("name", name) // From String
            options.put("description", description) // From book ticket
            //You can omit the image option to fetch the image from dashboard
            options.put("image", image)
            options.put("theme.color", themeColor); // Button color from metaapp
            options.put("currency", currency);
            options.put("order_id", order_id); // From bookticket
            options.put("amount", amount)//From book ticket


            val prefill = JSONObject()
            prefill.put("email", cust_email) //From book ticket
            prefill.put("contact", cust_contact)//From book ticket


            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            Timber.d(TAG, "Error in payment: " + e.message)
            e.printStackTrace()
        }
    }


    private fun getAgentPGDetailObserver() {
        agentRechargeViewModel.getAgentPGData.observe(this) {
            binding.progressBar.progressBar.gone()



            if (it != null && it.code == 200) {
                pnrNumber = it.pnr_number
                finalAmount = binding.etEnterAmount.text.toString()

                if (it.is_razorpay_payment) {
                    pnrNumber = it.customerTransactionId
                    val amount = it.amount
                    val orderId = it.order_id
                     emailId = it.email_id
                     phoneNumber = it.phone_no
                    val merchantId = decryptMerchantId(
                        it.merchant_id,
                        getDateYMD(getTodayDate()),
                        loginModelPref.domainName.substringBefore(".")
                    )
                    razorPayPayment(
                        merchantId,
                        loginModelPref.travels_name ?: "",
                        "Instant Recharge",
                        "https://s3.amazonaws.com/rzp-mobile/images/rzp.png",
                        "#3399cc",
                        "INR",
                        orderId,
                        amount,
                        emailId,
                        phoneNumber
                    )


                } else if (is_easebuzz_payment&&it.access_key!= null) {
                    orderIdEaseBuzz = it.order_id
                    val intentProceed = Intent(baseContext, PWECouponsActivity::class.java)
                    intentProceed.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
                    intentProceed.putExtra("access_key", it.access_key)
                    intentProceed.putExtra("pay_mode", "production")
                    startActivityForResult(intentProceed, EASEBUZZ_REQUEST_CODE)

                } else if (it.is_phonepe_v2_payment) {
                    orderIdPhonePeV2 = it.order_id
                    openPhonePeV2(
                        context = this,
                        activityResultLauncher = activityResultLauncher,
                        isLiveEnvironment = it.is_live_environment ?: false,
                        merchantId = it.merchant_id,
                        flowId = orderIdPhonePeV2,
                        token = it.token,
                        orderId = it.orderIdPhonePe
                    )
                } else {

                    var hashMap = HashMap<String, String>()
                    var formHtml = ""
                    var bodyHtml = ""
                    val paymentUrl = it.payment_link

                    hashMap.put("payment_url", paymentUrl)
                    hashMap.put("amount", finalAmount)
                    hashMap.put("order_id", it.order_id)
                    hashMap.put("redirect_url", it.redirect_url)
                    hashMap.put("currency", it.currency)
                    hashMap.put("billing_cust_name", it.billing_cust_name)
                    hashMap.put("billing_cust_country", it.billing_cust_country)
                    hashMap.put("address", it.address.toString())
                    hashMap.put("billing_cust_address", it.billing_cust_address.toString())
                    hashMap.put("billing_cust_tel", it.billing_cust_tel)
                    hashMap.put("billing_cust_email", it.billing_cust_email)
                    hashMap.put("billing_cust_notes", it.billing_cust_notes)
                    hashMap.put("merchant_Param", it.merchant_Param)
                    hashMap.put("pnr_number", it.pnr_number)
                    hashMap.put("ts_booking_type", "Web")
                    hashMap.put("preferred_seats", it.preferred_seats)
                    hashMap.put("referral_url_ts", it.referral_url_ts)
                    hashMap.put("pay_mode", it.pay_mode.toString())
                    hashMap.put("checksum", it.checksum)
                    hashMap.put("wallet_sel", it.wallet_sel)
                    hashMap.put("user_key", it.user_key)
                    hashMap.put("password_key", it.password_key)
                    hashMap.put("from", it.from)
                    hashMap.put("to", it.to)

                    hashMap.keys.forEach {
                        formHtml += "<input type=\"hidden\" name=\"$it\" value=\"${hashMap[it]}\"/>"
                    }

                    bodyHtml =
                        "<html lang=\"en\">\n" + "    <head>\n" + "      <title>Perform Payment</title>\n" + "      <meta charset=\"utf-8\">\n" + "      <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" + "      <title> Processing Payment ... .. . TicketSimply </title>\n" + "    \n" + "       <script type=\"text/javascript\">\n" + "          function submitForm(){\n" + "           var form = document.forms[0];\n" + "           form.submit();\n" + "          }\n" + "    </script>\n" + "    </head>\n" + "    <body  onload=\"javascript:submitForm()\"  style=\"background:none;\">\n" + "        <center>Do not \"close the window\" or press \"refresh\" or \"browser back button\".</center> <form id=\"pg_form\" name = \"pg_form\" method=\"post\" action=$paymentUrl>\n" + "$formHtml\n" + "        </form>\n" + "      <script type=\"text/javascript\">\n" + "           \n" + "      </script>\n" + "    </body>\n" + "    </head></html>"


                    Log.e("html = ",bodyHtml)

                    val intent = Intent(this, PaymentGatewayActivity::class.java)
                    var htmlResponse = HtmlResponse()
                    htmlResponse.htmlBody = bodyHtml
                    intent.putExtra(getString(R.string.HTML_BODY), htmlResponse)
                    intent.putExtra("pnr_number", pnrNumber)
                    intent.putExtra("amount", finalAmount)
                    intent.putExtra("is_pay_bitla",true)
                    startActivity(intent)
                    finish()

                }

            } else {

                try {
                    if (it != null && it.message != null){
//                        toast(it.message)
                    }else{
                        toast(getString(R.string.error_occured))
                    }
                }catch (e : Exception){
                    if(BuildConfig.DEBUG){
                        e.printStackTrace()
                    }
                }


            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == EASEBUZZ_REQUEST_CODE) {
            val result = data?.getStringExtra("result")
            val payment_response = data?.getStringExtra("payment_response")
            val amount = binding.etEnterAmount.text.toString()




            when (result) {
                EasebuzzStatus.EASEBUZZ_PAYMENT_SUCCESS_STATUS -> {
                    agentRechargeViewModel.easeBuzzRechargeApiSuccess(
                        true,
                        orderIdEaseBuzz,
                        amount,
                        phoneNumber,
                        emailId

                    )
                    getEaseBuzzPopup(getString(R.string.payment_successful))
                }

                EasebuzzStatus.EASEBUZZ_PAYMENT_ERROR -> {
                    getEaseBuzzPopup(getString(R.string.payment_failed_please_try_again))
                    toast(baseContext!!.getString(R.string.payment_failed_please_try_again))

                }

                EasebuzzStatus.EASEBUZZ_PAYMENT_CANCELLED -> {
                    getEaseBuzzPopup(getString(R.string.payment_declined))
//                    toast(getString(R.string.payment_declined))

                }
            }
        }
    }

    private fun getEaseBuzzPopup(message: String) {

            if (message == "Payment Successful") {
                showDialogRazorpay(
                    message,
                    binding.etEnterAmount.text.toString(),
                    R.drawable.phonepe_success
                )
            } else {
                showDialogRazorpay(
                    message,
                    binding.etEnterAmount.text.toString(),
                    R.drawable.phonepe_fail
                )
            }
    }


    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        try {
            agentRechargeViewModel.razorpayRechargeApiSuccess(
                pnrNumber = pnrNumber,
                paymentId = p1?.paymentId ?: ""

            )
        }
        catch(e:Exception) {
//            toast(e.toString())
        }
        
        //p1.data.
        val message = getString(R.string.payment_successful)
        showDialogRazorpay(
            message, binding.etEnterAmount.text.toString(),
            R.drawable.phonepe_success
        )

    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            agentRechargeViewModel.razorpayRechargeApiFailure(
                pnrNumber,
                p2?.orderId.toString()
            )
        } catch (e: Exception) {
//            toast(e.message)
            Timber.d(e.message)
        }
        val message = getString(R.string.razorpay_failure)
        showDialogRazorpay(
            message, binding.etEnterAmount.text.toString(),
            R.drawable.phonepe_fail
        )


    }


    private fun getAgentTransactionObserver() {
        agentRechargeViewModel.getAgentRechargeData.observe(this) {
            if (it != null && it.code == 200) {
                binding.etTransactionCharge.setText(it.result?.transactionCharge.toString())
                binding.netAmountET.setText(it.result?.netAmount.toString())


            }
        }
    }


    private fun getAgentTransactionDetailsApi(amount: String) {
        if (isNetworkAvailable()) {
            agentRechargeViewModel.getAgentTransactionDetailApi(
                apiKey, amount, locale
            )
        } else
            noNetworkToast()
    }

    private fun getAgentPGDetailApi(amount: String, pgType: String) {
        if (isNetworkAvailable()) {
            binding.progressBar.progressBar.visible()
            agentRechargeViewModel.getAgentPGDetailApi(
                apiKey = apiKey,
                amount = amount,
                pgType = pgType,
                nativeAppType = ANDROID_NATIVE_TYPE
            )
        } else
            noNetworkToast()
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        apiKey = loginModelPref.api_key
        val namePaybitlaList : java.util.ArrayList<String> = arrayListOf()
        if (getPrivilegeBase() != null) {
            privilegeData = getPrivilegeBase()

            if(!privilegeData?.agentRechargePgType.isNullOrEmpty()){
                if(privilegeData?.agentRechargePgType!!.get(0).id == "32") {
                    privilegeData?.agentRechargePgType!!.get(0).pgNamePayBitla.toString()
                    val responseList = privilegeData?.agentRechargePgType!!.get(0).pgNamePayBitla.split(",")
                    pgList.clear()
                    for (i in 0 until responseList.size){
                        val obj = PgData(responseList[i],responseList[i].substringAfterLast("-"))
                        pgList.add(obj)
                    }
                }
                else {
                    pgList = privilegeData?.agentRechargePgType!!
                }
            }else{
                toast(getString(R.string.payment_gateway_not_found))
            }
        }
    }

    private fun setPGAdapter() {
        adapter = PaymentGatewayAdapter(this, pgList, this)
        binding.pgRV.adapter = adapter


    }


    override fun onResume() {
        super.onResume()
        binding.etEnterAmount.setText("")
        binding.etTransactionCharge.setText("")
        binding.netAmountET.setText("")

    }


    private fun hitPhonePeTransactionStatusApi(pnrNumber: String) {
        agentRechargeViewModel.getPhonePeStatusApi(apiKey, pnrNumber)
    }


    private fun hitPayBitlaStatusApi(pnrNumber: String) {
        agentRechargeViewModel.payBitlaRechargeApiSuccess(pnrNumber)
    }

    private fun payBitlaStatusObserver(){
        agentRechargeViewModel.getPayBitlaSuccess.observe(this){
            if(it != null){
                val data = it.txn_status
                var amount = ""

                if(intent.hasExtra("amount")) {
                    amount = intent.getStringExtra("amount") ?: ""
                } else {
                    amount = finalAmount
                }

                if (data?.message != null) {
                    showDialog(data.message, amount)
                }

            }

        }
    }


    override fun onDataSend(type: Int, file: Any) {
        if (type == 1) {
            for (i in 0 until pgList.size) {
                pgList[i].isSelected = false
            }
            val pos = file as Int
            pgList[pos].isSelected = true
            selectedPgType = pgList[pos].id
            adapter?.updateChecks(pgList)
        }
    }



    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {


    }

    override fun onSingleButtonClick(str: String) {
        if (str.isNotEmpty()) {
            if (str == getString(R.string.cancel)) {
                isPhonePeV2CancelClicked = true
                showPhonePeV2PendingDialog = false
                closePhonePeV2PendingDialog()
            }
        }
    }
}

