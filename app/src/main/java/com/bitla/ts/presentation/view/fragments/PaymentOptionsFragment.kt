package com.bitla.ts.presentation.view.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity.PrivilegeManager.getPrivilegeBase
import com.bitla.ts.app.base.BaseUpdateCancelTicket
import com.bitla.ts.data.agent_account_info
import com.bitla.ts.data.confirm_phone_block_ticket_method_name
import com.bitla.ts.data.format_type
import com.bitla.ts.data.is_from_middle_tier
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.VarArgListener
import com.bitla.ts.data.upi_tranx_status
import com.bitla.ts.data.validate_otp_wallets_method_name
import com.bitla.ts.data.wallet_otp_generation_method_name
import com.bitla.ts.databinding.FragmentPaymentOptionsBinding
import com.bitla.ts.domain.pojo.account_info.request.AgentAccountInfoRequest
import com.bitla.ts.domain.pojo.booking.PayGayType
import com.bitla.ts.domain.pojo.booking_custom_request.BookingCustomRequest
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody
import com.bitla.ts.domain.pojo.photo_block_tickets.request.Ticket
import com.bitla.ts.domain.pojo.pinelabs.CardSaleResponse
import com.bitla.ts.domain.pojo.pinelabs.ReqBodyPinelab
import com.bitla.ts.domain.pojo.privilege_details_model.response.child_model.WalletPaymentOption
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.validate_otp_wallets.request.ValidateOtpWalletsRequest
import com.bitla.ts.domain.pojo.wallet_otp_generation.request.WalletOtpGenerationRequest
import com.bitla.ts.presentation.adapter.EasebuzzOptionAdapter
import com.bitla.ts.presentation.adapter.FilterAdapter
import com.bitla.ts.presentation.adapter.WalletOptionAdapter
import com.bitla.ts.presentation.adapter.WalletOptionAgentRechargeAdapter
import com.bitla.ts.presentation.view.activity.BookingPaymentOptionsActivity
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.NewConfirmPhoneBookingActivity
import com.bitla.ts.presentation.view.ticket_details_compose.TicketDetailsActivityCompose
import com.bitla.ts.presentation.viewModel.AgentAccountInfoViewModel
import com.bitla.ts.presentation.viewModel.BookingOptionViewModel
import com.bitla.ts.presentation.viewModel.DashboardViewModel
import com.bitla.ts.presentation.viewModel.PaymentMethodViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.ResourceProvider
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.common.getUserRole
import com.bitla.ts.utils.common.retrieveBookingCustomRequest
import com.bitla.ts.utils.constants.IS_PINELAB_DEVICE
import com.bitla.ts.utils.constants.PAYMENT_DEBIT_CREDIT
import com.bitla.ts.utils.constants.PAYMENT_QR
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.sharedPref.pinelab_transaction_status_api
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import gone
import isNetworkAvailable
import noNetworkToast
import onChange
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible


class PaymentOptionsFragment : BaseUpdateCancelTicket(), OnItemClickListener,
    DialogSingleButtonListener, VarArgListener {

    private val paymentOptionsList = mutableListOf<SearchModel>()
    private val otherPaymentOptions = mutableListOf<PayGayType>()
    private var paymentType: Int = 1//(by default for cash)
    private var lastSelectedPaymentPosition: Int = 0
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var privileges: PrivilegeResponseModel? = null
    private var currencyFormatt: String = ""
    private var mobileNumber: String? = null
    private lateinit var walletUpiAlertDialog: AlertDialog
    private var pinelabResponseString: String? = ""
    private var pinelabResponseData: CardSaleResponse? = null
   // private val paymentMethodViewModel by viewModel<PaymentMethodViewModel>()
    private val paymentMethodViewModel: PaymentMethodViewModel by activityViewModels()

    private var ezetapDeviceId: String = ""
    private var selectedWalletUpiOptionId: Int? = null
    private val PLUTUS_SMART_PACKAGE = "com.pinelabs.masterapp"
    private val PLUTUS_SMART_ACTION = "com.pinelabs.masterapp.SERVER"
    private val MESSAGE_CODE = 1001
    private val BILLING_REQUEST_TAG = "MASTERAPPREQUEST"
    private val BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE"
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var bccId: Int? = 0
    private val agentAccountInfoViewModel by viewModel<AgentAccountInfoViewModel<Any>>()
    private var noOfSeats: String? = "0"
    private var selectedSeatNo: String? = null
    private var srcDest: String? = null
    private var toolbarSubTitleInfo: String? = null
    private var locale: String? = ""
    private var creditDebitCardNo: String? = null
    private var isAgentLogin: Boolean = false
    private var selectedSubPaymentOptionName: String? = null
    private var agentPayViaPhoneNumberSMS: String = ""
    private var branchUserPayViaVPA: String = ""
    private var branchUserPayViaPhoneNumberSMS: String = ""
    private var walletPaymentOptions = mutableListOf<WalletPaymentOption>()
    private var totalFare = 0.0
    private var totalFareString = ""
    private var getAvailableBalance = ""
    private var easebuzzPaymentOptions = mutableListOf<WalletPaymentOption>()
    private var isBima: Boolean? = null
    private var pnrNumber = ""
    private var reservationId = ""
    private var loginModelPref: LoginModel = LoginModel()
    private var agentPayViaVPA: String = ""
    private var mServerMessenger: Messenger? = null
    var message: Message = Message.obtain(null, MESSAGE_CODE)
    private var pinelabBillingRefNo: String = ""
    private var toolbarTitle: String = ""
    private var bookTicketPnr: String = ""
    private var isCancelledClicked: Boolean = false
    private var isOnBehalfOfAgent = false
    private var currency: String = ""
    private var selectedEasebuzzOptionId: Int? = null
    private var selectedWalletOrUpi: String? = null
    private var selectedWalletUpiOptionName: String? = null

    private var selectedEasebuzz: String? = null
    private var selectedEasebuzzOptionName: String? = null
    private var selectedOtherPaymentOption: String? = null
    private var walletMobileNo: String = ""
    private var isPhoneBlocking: Boolean = false

    private lateinit var upiCreateQRAlertDialog: AlertDialog
    private lateinit var upiAuthSmsAndVPADialog: AlertDialog
    private val isPhoneBlockedWallet: String = "true" // fixed

    private var bookingCustomRequest = BookingCustomRequest()
    private var onBeHalfUser: Int = 0
    private var onBehalfBranch: String? = null
    private var refBookingNo: String? = null
    private var onBehalfOnlineAgent: String? = null

    private var onBehalf: String? = null

    private var agentType: String? = null

    private val bookingOptionViewModel by viewModel<BookingOptionViewModel<Any?>>()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var isBound: Boolean? = false
    private var easebuzzPayViaQr: Int? = null
    private var easebuzzPayViaSms: Int? = null
    private var easebuzzPayViaUpi: Int? = null

    lateinit var binding: FragmentPaymentOptionsBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentOptionsBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun initUi() {



        privileges = getPrivilegeBase(requireActivity())
        if (IS_PINELAB_DEVICE) {
            if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                paymentType = 6 // QR code by default
            }
        }

        if (privileges != null) {
            privilegeResponseModel = privileges as PrivilegeResponseModel

            privilegeResponseModel.apply {
                currencyFormatt = getCurrencyFormat(
                    requireActivity(),
                    privilegeResponseModel.currencyFormat
                )
            }
        }


      /*  if(paymentOptionsList.size > 0){
          //  setAdapter()
        }*/
        getData()
        setObserver()
        getPref()
        setPaymentOptionsAdapter()
        agentAccountInfo()



    }

    private fun getData() {
        paymentMethodViewModel.let {
                pnrNumber = paymentMethodViewModel.pnrNumber
                reservationId= paymentMethodViewModel.reservationId
                totalFareString = paymentMethodViewModel.totalFareString
                totalFare = paymentMethodViewModel.totalFare
                isOnBehalfOfAgent = paymentMethodViewModel.isOnBehalfOfAgent

            }




    }

    private var isPhoneBlockTicket = false


     fun callBookingRequest(){
        if (isPhoneBlockTicket) {
            if (requireActivity().isNetworkAvailable()) {
                DialogUtils.blockSeatsDialog(
                    showMsg = false,
                    requireContext(),
                    getString(R.string.confirmBooking),
                    getString(R.string.selected_seat_s_will_be_assigned),
                    srcDest = srcDest ?: getString(R.string.dash),
                    journeyDate = toolbarSubTitleInfo ?: getString(R.string.dash),
                    noOfSeats = noOfSeats!!,
                    seatNo = selectedSeatNo.toString(),
                    getString(R.string.goBack),
                    getString(R.string.confirmBooking),
                    this
                )
            } else {
               requireActivity().noNetworkToast()
            }
        } else {
            /*if (binding.btnBook.text.toString() != getString(R.string.do_phone_booking)) {
                DialogUtils.blockSeatsDialog(
                    showMsg = false,
                    this,
                    getString(R.string.confirmBooking),
                    getString(R.string.selected_seat_s_will_be_assigned),
                    srcDest = srcDest ?: getString(R.string.dash),
                    journeyDate = toolbarSubTitleInfo ?: getString(R.string.dash),
                    noOfSeats = noOfSeats!!,
                    seatNo = selectedSeatNo.toString(),
                    getString(R.string.goBack),
                    getString(R.string.confirmBooking),
                    this
                )
            } else {*/
                isPhoneBlocking = true
                if (requireActivity().isNetworkAvailable()) {
                    if (paymentType == 6) {
                        pinelabPaymentType(PAYMENT_QR)

                    } else if (paymentType == 7) {
                        pinelabPaymentType(PAYMENT_DEBIT_CREDIT)

                    }else if(paymentType == 14 && privilegeResponseModel.isPaytmPosEnabled){
                        generatePaytmPayment()
                    }else if (paymentType == 14) {
                        EzePayAPI()
                    }  else {
                        confirmPhoneBlockTicketApi()

                    }
                } else
                    requireActivity().noNetworkToast()
           // }
        }
    }
    private var isPermanentPhoneBooking: Boolean = false

    private fun getPref(){
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()

        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel.currency != null)
                currency = privilegeResponseModel.currency
            if (privilegeResponseModel.isAgentLogin != null)
                isAgentLogin = privilegeResponseModel.isAgentLogin
            if (privilegeResponseModel.isPermanentPhoneBooking != null)
                isPermanentPhoneBooking = privilegeResponseModel.isPermanentPhoneBooking
            if (privilegeResponseModel.walletPaymentOptions != null) {
                walletPaymentOptions = privilegeResponseModel.walletPaymentOptions

                val upiIndex = walletPaymentOptions.indexOfFirst {
                    it.type.equals(getString(R.string.upi), true)
                }
                // removing UPI options
                if (upiIndex != -1)
                    walletPaymentOptions.removeAt(upiIndex)
            }
            val upiIndex = walletPaymentOptions.indexOfFirst {
                it.type.equals(getString(R.string.upi), true)
            }
            // removing UPI options
            if (upiIndex != -1)
                walletPaymentOptions.removeAt(upiIndex)
            if (privilegeResponseModel.allowToShowWhatsappCheckboxInBookingPage){
               // binding.cardWhatsapp.gone()

            }
              //  binding.cardWhatsapp.visible() else

            val role = getUserRole(loginModelPref, isAgentLogin, requireContext())
            if (privilegeResponseModel.isPhoneBooking && !role.contains(
                    getString(R.string.role_agent),
                    true
                ) && !role.contains(getString(R.string.role_field_officer), true)
            ) {
//                privilegesPhoneBookingForOnlineAgent()
//                privilegesPhoneBookingForOfflineAgent()
//                privilegesPhoneBookingForBranchAndWalkin()
            } else{
                //   binding.cardPhoneBooking.gone()

            }
        }

        if (privilegeResponseModel.allowUpiForDirectPgBookingForAgents && paymentOptionsList.isNotEmpty()) {
           // binding.layoutPaymentOptions.visible()

        } else {
            if (getUserRole(loginModelPref, isAgentLogin, requireContext()).contains(getString(R.string.role_agent), true)
                || paymentOptionsList.isEmpty()
            ) {
                paymentType = 1
              //  binding.layoutPaymentOptions.gone()
            } else {
                paymentType = 1
              //  binding.layoutPaymentOptions.visible()
            }
        }

        if(!privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser){
            if(privilegeResponseModel.easebuzzSubPaymentOptions?.payViaQr != null)
                easebuzzPayViaQr = privilegeResponseModel.easebuzzSubPaymentOptions?.payViaQr!!
            if(privilegeResponseModel.easebuzzSubPaymentOptions?.payViaSms != null)
                easebuzzPayViaSms = privilegeResponseModel.easebuzzSubPaymentOptions?.payViaSms!!
            if(privilegeResponseModel.easebuzzSubPaymentOptions?.payViaUpi != null)
                easebuzzPayViaUpi = privilegeResponseModel.easebuzzSubPaymentOptions?.payViaUpi!!
        }
    }

    private fun setAdapter() {
        val filterAdapter = FilterAdapter(requireActivity(), this, paymentOptionsList, lastSelectedPaymentPosition,true)
        binding.rvPaymentOptions.adapter = filterAdapter
    }

    override fun onClick(view: View, position: Int) {
        super.onClick(view, position)

        if (paymentOptionsList.isNotEmpty()) {
            paymentType = paymentOptionsList[position].id.toString().toInt()

            if (paymentOptionsList[position].name == getString(R.string.credit_debit)) {
                DialogUtils.creditDebitDialog(requireActivity(), this)
            } else if (paymentOptionsList[position].name == getString(R.string.others)) {
                otherPaymentOptions.clear()
                /*val payGayType = PayGayType()
                payGayType.payGayTypeName = getString(R.string.notAvailable)
                otherPaymentOptions.add(payGayType)*/

                if (::privilegeResponseModel.isInitialized
                    && !privilegeResponseModel.othersPaymentOption.isNullOrEmpty()
                ) {
                    privilegeResponseModel.othersPaymentOption.forEach {
                        val payGayType = PayGayType()
                        payGayType.payGayTypeName = it.label
                        payGayType.payGayTypeId = it.id.toString()
                        otherPaymentOptions.add(payGayType)
                    }
                }
                if (otherPaymentOptions.isNotEmpty())
                    DialogUtils.otherPaymentsDialog(requireActivity(), otherPaymentOptions, this)
            } else if (paymentOptionsList[position].name == getString(R.string.wallet_upi)) {
                var passengerMobile = getString(R.string.empty)
                if (mobileNumber != null)
                    passengerMobile = mobileNumber?.substringAfter("-").toString()
//                if (!walletPaymentOptions.contains(WalletPaymentOption("UPI", 2, ""))) {
//                    walletPaymentOptions.add(0, WalletPaymentOption("UPI", 2, ""))
//                }
                walletUpiAlertDialog = DialogUtils.walletUpiDialog(
                    requireActivity(),
                    walletPaymentOptions,
                    this,
                    mobile = passengerMobile
                )
            } else if (paymentOptionsList[position].name == this.getString(R.string.pinelab_QR)) {
                paymentType = paymentOptionsList[position].id.toString().toInt()

            } else if (paymentOptionsList[position].name == this.getString(R.string.pinelab_debitcard)) {
                paymentType = paymentOptionsList[position].id.toString().toInt()

            } else if (paymentOptionsList[position].name == this.getString(R.string.paytm)) {
                paymentType = paymentOptionsList[position].id.toString().toInt()
            }else if (paymentOptionsList[position].name == this.getString(R.string.ezetap)) {
                paymentType = paymentOptionsList[position].id.toString().toInt()

            }

            else if (paymentOptionsList[position].name?.split(" ")?.get(0) == getString(R.string.wallet)) {

                if (totalFare > (getAvailableBalance.toDoubleOrNull() ?: 0.0)) {
                    walletPaymentOptions.apply {
                        clear()
                        add(0, WalletPaymentOption(getString(R.string.pay_via_qr), 15, ""))
                        add(1, WalletPaymentOption(getString(R.string.pay_via_sms), 16, ""))
                        add(2, WalletPaymentOption(getString(R.string.pay_via_upi), 17, ""))
                    }

                    walletUpiAlertDialog = DialogUtils.walletUpiDialog(
                        context = requireActivity(),
                        walletPaymentOption = walletPaymentOptions,
                        dialogSingleButtonListener = this,
                        mobile = ""
                    )

                    walletUpiAlertDialog.apply {
                        findViewById<TextView>(R.id.tvTitle).text = getString(R.string.wallet)
                    }
                }
            }

            else if (paymentOptionsList[position].name?.contains("PAY NET AMOUNT",true) == true) {

                walletPaymentOptions.apply {
                    clear()
                    add(0, WalletPaymentOption(getString(R.string.pay_via_qr), 15, ""))
                    add(1, WalletPaymentOption(getString(R.string.pay_via_sms), 16, ""))
                    add(2, WalletPaymentOption(getString(R.string.pay_via_upi), 17, ""))
                }

                walletUpiAlertDialog = DialogUtils.walletUpiDialog(
                    context = requireActivity(),
                    walletPaymentOption = walletPaymentOptions,
                    dialogSingleButtonListener = this,
                    mobile = ""
                )

                walletUpiAlertDialog.apply {
                    findViewById<TextView>(R.id.tvTitle).text = getString(R.string.net_amt_less_off_comm)
                }
            }

            else if (paymentOptionsList[position].name?.contains("PAY FULL AMOUNT",true) == true) {

                walletPaymentOptions.apply {
                    clear()
                    add(0, WalletPaymentOption(getString(R.string.pay_via_qr), 15, ""))
                    add(1, WalletPaymentOption(getString(R.string.pay_via_sms), 16, ""))
                    add(2, WalletPaymentOption(getString(R.string.pay_via_upi), 17, ""))
                }

                walletUpiAlertDialog = DialogUtils.walletUpiDialog(
                    context = requireActivity(),
                    walletPaymentOption = walletPaymentOptions,
                    dialogSingleButtonListener = this,
                    mobile = ""
                )

                walletUpiAlertDialog.apply {
                    findViewById<TextView>(R.id.tvTitle).text = getString(R.string.full_amount)
                }
            }

            else if (paymentOptionsList[position].name == getString(R.string.upi_caps)) {

                easebuzzPaymentOptions.apply {
                    clear()
                    add(0, WalletPaymentOption(getString(R.string.pay_via_qr_user), 20, ""))
                    add(1, WalletPaymentOption(getString(R.string.pay_via_sms_user), 21, ""))
                    add(2, WalletPaymentOption(getString(R.string.pay_via_upi_user), 22, ""))
                }

                walletUpiAlertDialog = DialogUtils.easebuzzDialog(
                    context = requireActivity(),
                    easebuzzPaymentOption = easebuzzPaymentOptions,
                    dialogSingleButtonListener = this,
                    mobile = ""
                )

                walletUpiAlertDialog.apply {
                    findViewById<TextView>(R.id.tvTitle).text = getString(R.string.upi_caps)
                }
            }

        }

    }


    override fun onRightButtonClick() {
        if (paymentType == 6) {
            DialogUtils.showProgressDialog(requireActivity())
            pinelabPaymentType(PAYMENT_QR)
        } else if (paymentType == 7) {
            DialogUtils.showProgressDialog(requireActivity())
            pinelabPaymentType(PAYMENT_DEBIT_CREDIT)
        }else if(paymentType == 14 && privilegeResponseModel.isPaytmPosEnabled){
            generatePaytmPayment()
        }
        else if (paymentType == 14) {
            EzePayAPI()
        } else {

            if (privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                confirmPhoneBlockTicketApi()

            } else {
                if (isBima != null && isBima == true) {
                    confirmBimaPhoneBlockTicketApi()
                } else {
                    confirmPhoneBlockTicketApi()
                }
            }
        }
    }

    private fun pinelabPaymentType(type: Int) {
        var totalFaree: String = ""
        var passengerPhoneNo: String? = ""
        var ticketNumber: String? = ""

        val finalfare = requireActivity().intent.getStringExtra(getString(R.string.totalAmount)).toString()
        // val ticketData = (bookTicketFullResponse as BookTicketFullResponse)
        totalFaree = (finalfare.toDouble() * 100).toInt().toString()
        passengerPhoneNo = mobileNumber
        ticketNumber = pnrNumber
        /*  } else {
              val ticketData = (bookTicketFullResponse as BookExtraSeatResponse)
              if(ticketData.total_fare.contains("₹")){
                  totalFaree = (ticketData.total_fare.substringAfter("₹").toDouble() * 100).toInt().toString()
              }else{
                  totalFaree = (ticketData.total_fare.toDouble() * 100).toInt().toString()
              }
              passengerPhoneNo = ticketData.passenger_details[0].mobile
              ticketNumber = ticketData.ticketNumber
          }*/

        var paymentType = ""
        if (type == PAYMENT_QR) {
            paymentType = "5120"
        } else {
            paymentType = "4001"

        }

        val headerObj = JSONObject()
        headerObj.put("ApplicationId", "be1dc81f1cd941f39afd7ccbb7d7f023")
        headerObj.put("UserId", "user1234")
        headerObj.put("MethodId", "1001")
        headerObj.put("VersionNo", "1.0")

        val detailObj = JSONObject()
        detailObj.put("TransactionType", paymentType)
        detailObj.put("BillingRefNo", ticketNumber)
        detailObj.put("PaymentAmount", totalFaree)
        detailObj.put("MobileNumberForEChargeSlip", passengerPhoneNo)




        val json = JSONObject()
        json.put("Header", headerObj)
        json.put("Detail", detailObj)

        pinelabPayment(json)
    }

    private fun generatePaytmPayment() {

        var ticketNumber: String? = ""
        var finalfare = requireActivity().intent.getStringExtra(getString(R.string.totalAmount)).toString()
        ticketNumber = pnrNumber

        val edcPackage="com.paytm.pos.debug"
//       val edcPackage="com.paytm.pos"

        val packageName=requireActivity().packageName
        val payDeepLink="paytmedc://paymentV2"
        val callBackAction="com.paytm.pos.payment.CALL_BACK_RESULT_PHONE_BLOCK"
        val orderId= ticketNumber
        val payMode="all"
        val amount=finalfare.toDouble().toInt()*100
        val deepLink= "paytmedc://paymentV2?" + "callbackAction=" + callBackAction + "&stackClear=true" +
                "&callbackPkg=" + packageName + "&callbackDl=" +  payDeepLink + "&requestPayMode=" + payMode +
                "&orderId=" + orderId + "&amount=" + amount


        val launchIntent=requireActivity().packageManager.getLaunchIntentForPackage(edcPackage)
        if (launchIntent != null) {
            launchIntent.putExtra("deeplink", deepLink)
            startActivity(launchIntent)
        }

    }


    private fun EzePayAPI() {


        var ticketNumber: String? = ""
        var finalfare = requireActivity().intent.getStringExtra(getString(R.string.totalAmount)).toString()
        ticketNumber = pnrNumber


        var obj = JSONObject()
        obj.put("appKey", privilegeResponseModel.ezetapApiKey ?: "")
        obj.put("externalRefNumber", ticketNumber)
        obj.put("username", privilegeResponseModel.ezetapUserName ?: "")
        if (finalfare.contains("₹")) {
            finalfare = finalfare.replace("₹", "")
        }
        obj.put("amount", finalfare)
        obj.put("mode", "ALL")
        val pushObj = JsonObject()
        pushObj.addProperty("deviceId", "$ezetapDeviceId|ezetap_android")
        obj.put("pushTo", pushObj)




//        EzeAPI.pay(this, REQUEST_CODE_PAY_EZETAP, obj)
    }

    private fun confirmPhoneBlockTicketApi() {
        (activity as NewConfirmPhoneBookingActivity).showProgressBar()

        if (requireActivity().isNetworkAvailable()) {
            var reqBody: ReqBody? = null
            reqBody = ReqBody(
                apiKey = loginModelPref.api_key,
                paymentType = if (privilegeResponseModel.allowUpiForDirectPgBookingForAgents && paymentType == 1) 1
                else if (!privilegeResponseModel.allowUpiForDirectPgBookingForAgents) paymentType
                else 0,
                pnrNumber = paymentMethodViewModel.pnrNumber,
                ticket = Ticket(creditDebitCardNo.toString()),
                travelBranch = "",
                userId = "",
                locale = locale,
                agentPaymentType = if (paymentType == 1) "" else if (isAgentLogin) "$paymentType" else "",
                agentSubPaymentType = if (paymentType == 1) "" else if(isAgentLogin) "$selectedSubPaymentOptionName" else "",
                agentPhone = agentPayViaPhoneNumberSMS,
                agentVpa = agentPayViaVPA,
                subPaymentType = if(!isAgentLogin) selectedSubPaymentOptionName else "",
                branchVpa = branchUserPayViaVPA,
                branchPhone = branchUserPayViaPhoneNumberSMS,
                boardingPointId = paymentMethodViewModel.selectedBoardingPoint,
                passengerDetails = paymentMethodViewModel.passengerDetailsList,
                onbehalfUser = paymentMethodViewModel.selectedUser,
                onbehalfBranch = paymentMethodViewModel.selectedTravelBranch,
                agentType = paymentMethodViewModel.agentTypeId,
                onBehalfOnlineAgentValue = paymentMethodViewModel.selectedOnlineAgentId,
                onBehalf = paymentMethodViewModel.selectedOfflineAgentId,
                paymentTypeConfig  = selectedOtherPaymentOption

            )

            bookingOptionViewModel.confirmPhoneBlockTicketApi(
                confirmPhoneBlockTicketReq = reqBody,
                apiType = confirm_phone_block_ticket_method_name
            )
        } else
            requireActivity().noNetworkToast()
    }

    private fun confirmBimaPhoneBlockTicketApi() {

        if (requireActivity().isNetworkAvailable()) {
            var reqBody: ReqBody? = null
            reqBody = ReqBody(
                apiKey = loginModelPref.api_key,
                paymentType = if (!privilegeResponseModel.allowUpiForDirectPgBookingForAgents) paymentType else 0,
                pnrNumber = pnrNumber,
                ticket = Ticket(creditDebitCardNo.toString()),
                travelBranch = "",
                userId = "",
                locale = locale,
                agentPaymentType = "$selectedWalletUpiOptionId",
                agentSubPaymentType = selectedSubPaymentOptionName,
                subPaymentType = "",
                branchVpa = "",
                branchPhone = ""
            )
            bookingOptionViewModel.confirmBimaPhoneBlockTicketApi(
                reqBody
            )
        } else
            requireActivity().noNetworkToast()
    }

    private fun pinelabPayment(json: JSONObject) {
        if (isBound!!) {
            val data = Bundle()
            val value = json.toString()
            data.putString(BILLING_REQUEST_TAG, value)
            message.data = data
            try {
                message.replyTo = Messenger(IncomingHandler(this))
                mServerMessenger!!.send(message)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        } else {
            requireActivity().toast("Pinelab device connection error!")
        }

    }

    private class IncomingHandler(bookingPaymentOptionsActivity: PaymentOptionsFragment) :
        Handler() {
        private val BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE"
        var activity = bookingPaymentOptionsActivity

        override fun handleMessage(msg: Message) {
            val bundle = msg.data
            val value = bundle.getString(BILLING_RESPONSE_TAG)

            val data =
                Gson().fromJson<CardSaleResponse>(value.toString(), CardSaleResponse::class.java)

            var respData: CardSaleResponse? = null
            respData = data
            activity.handlePinelabSuccessResp(respData, value.toString())
        }
    }

    fun handlePinelabSuccessResp(respData: CardSaleResponse, pinelabResponse: String) {
        pinelabResponseData = respData
        pinelabResponseString = pinelabResponse
        if (DialogUtils.progressDialog != null && DialogUtils.progressDialog!!.isShowing) {
            DialogUtils.progressDialog?.dismiss()
        }
        // if (respData.detail ) {
        val jsonObj = JSONObject(pinelabResponseString!!)
        val reqBody =
            ReqBodyPinelab(
                loginModelPref.api_key,
                pnrNumber,
                true,
                pinelab_response = jsonObj,
                pinelab_payment_type = paymentType

            )
        DialogUtils.showProgressDialog(requireContext())
        bookingOptionViewModel.pinelabStatusApi(reqBody, pinelab_transaction_status_api)
        pinelabBillingRefNo = pnrNumber
        /* } else {
             toast("Booking failed, Please try again")
             *//*  dashboardViewModel.releaseTicketAPI(
                  com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBody(
                      loginModelPref.api_key,
                      lastBookedTicketNumber!!,
                      releaseTicketRemarks,
                      false,
                      com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.Ticket(
                          lastBookedSeatsNumber.toString()
                      ),
                      json_format,
                      locale = locale
                  ),
                  release_phone_block_ticket_method_name
              )*//*


        }*/


    }


    private fun setObserver() {


        bookingOptionViewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.LOADING -> (activity as NewConfirmPhoneBookingActivity).showProgressBar()
                LoadingState.LOADED -> (activity as NewConfirmPhoneBookingActivity).hideProgressBar()
                else -> (activity as NewConfirmPhoneBookingActivity).hideProgressBar()
            }
        }


        bookingOptionViewModel.confirmPhoneBlockTicket.observe(viewLifecycleOwner) {
            if (it != null) {
                (activity as NewConfirmPhoneBookingActivity).showProgressBar()

                if (it.code == 200) {

                    if (selectedSubPaymentOptionName == "QR"
                        && it.result?.status.equals(getString(R.string.pending), ignoreCase = true)
                    ) {

                        if (it.result?.agentRechargeQrResp?.isNotEmpty() == true) {
                            try {
                                upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                                    context = requireContext(),
                                    isFromAgentRechargePG = true,
                                    dialogSingleButtonListener = this
                                )

                                val base64String = it.result.agentRechargeQrResp.substring(22)
                                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                                val decodedImage =
                                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                                if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {

                                    upiCreateQRAlertDialog.findViewById<ImageView>(
                                        R.id.qr_code_image
                                    ).setImageBitmap(decodedImage)
                                }

                                callPayStatOfAgentInsRechargStatusApi()
                            } catch (e: Exception) {
                                requireActivity().toast(getString(R.string.something_went_wrong))
                            }
                        } else if (it.result?.branchUpiQrResp?.isNotEmpty() == true) {
                            try {
                                upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                                    context = requireContext(),
                                    isFromAgentRechargePG = false,
                                    dialogSingleButtonListener = this,
                                    isFromBranchUser = true
                                )

                                val base64String = it.result.branchUpiQrResp?.substring(22)
                                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                                val decodedImage =
                                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                                if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {

                                    upiCreateQRAlertDialog.findViewById<ImageView>(
                                        R.id.qr_code_image
                                    ).setImageBitmap(decodedImage)
                                }

                                callBranchUpiTranxStatusApi()
                            } catch (e: Exception) {
                                requireActivity().toast(getString(R.string.something_went_wrong))
                            }
                        } else {
                            requireActivity().toast(getString(R.string.something_went_wrong))
                        }

                    }
                    else if (selectedSubPaymentOptionName.toString() == "SMS"
                        && it.result?.status.equals(getString(R.string.pending), ignoreCase = true)
                    ) {

                        upiAuthSmsAndVPADialog =  DialogUtils.upiAuthSmsAndVPADialog(
                            context = requireContext(),
                            isSmsAuth = true,
                            dialogSingleButtonListener = this,
                            isFromBranchUser = privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser && !privilegeResponseModel.isAgentLogin
                        )
                        if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                            callPayStatOfAgentInsRechargStatusApi()
                        } else if (privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser) {
                            callBranchUpiTranxStatusApi()
                        }

                    }
                    else if (selectedSubPaymentOptionName == "VPA"
                        && it.result?.status.equals(getString(R.string.pending), ignoreCase = true)
                    ) {

                        upiAuthSmsAndVPADialog = DialogUtils.upiAuthSmsAndVPADialog(
                            context = requireContext(),
                            isSmsAuth = false,
                            dialogSingleButtonListener = this,
                            isFromBranchUser = privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser && !privilegeResponseModel.isAgentLogin
                        )

                        if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                            callPayStatOfAgentInsRechargStatusApi()
                        } else if (privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser) {
                            callBranchUpiTranxStatusApi()
                        }
                    }

                    else {
                        val intent= Intent(requireActivity(), TicketDetailsActivityCompose::class.java)
                        intent.apply {
                            putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                            putExtra("activityName2", "booking")
                            putExtra(getString(R.string.TICKET_NUMBER), it.result?.ticketNumber)
                            putExtra(getString(R.string.toolbar_title), toolbarTitle)
                        }
                        startActivity(intent)
                        requireActivity().finish()
                    }
                } else {
                    it.message?.let { it1 -> requireActivity().toast(it1) }
                }
            } else {
//                toast(getString(R.string.server_error))
            }
        }

        agentAccountInfoViewModel.agentInfo.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        getAvailableBalance = it.available_balance
                        setPaymentOptionsAdapter()
                    }
                    401 -> {
                        showUnauthorisedDialog()
                    }
                    else -> {
                        it.result.message?.let { it1 -> requireActivity().toast(it1) }
                    }
                }
            } else {
                requireActivity().toast(getString(R.string.server_error))
            }
        }


        bookingOptionViewModel.pinelabTransactionData.observe(viewLifecycleOwner) {
            (activity as NewConfirmPhoneBookingActivity).hideProgressBar()


            if (it != null && it.code != null) {
                when (it.code) {
                    200 -> {
                        if (DialogUtils.progressDialog!!.isShowing) {
                            DialogUtils.progressDialog?.dismiss()
                        }
                        if (it.responseCode == 0 && it.data!!.code == null) {
//                            val intent = if(privilegeResponseModel.country.equals("India", true) || privilegeResponseModel.country.equals("Indonesia", true)) {
//                                Intent(this, TicketDetailsActivityCompose::class.java)
//                            } else {
//                                Intent(this, TicketDetailsActivity::class.java)
//                            }
                            val intent= Intent(requireActivity(), TicketDetailsActivityCompose::class.java)

                            intent.putExtra(
                                "activityName",
                                BookingPaymentOptionsActivity::class.java
                            )
                            intent.putExtra("activityName2", "booking")

                            intent.putExtra(getString(R.string.TICKET_NUMBER), pinelabBillingRefNo)
                            intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                            startActivity(intent)
                            requireActivity().finish()
                        } else if (it.message == "This ticket is released and now its ready for re-booking.") {
                            // do nothing
                            if (DialogUtils.progressDialog!!.isShowing) {
                                DialogUtils.progressDialog?.dismiss()
                            }
                            requireActivity().toast("Payment failed! Please try again")
                        } else {
                            requireActivity().toast(it.message)
                            val jsonObj = JSONObject(pinelabResponseString!!)
                            val reqBody =
                                ReqBodyPinelab(
                                    loginModelPref.api_key,
                                    pnrNumber,
                                    true,
                                    pinelab_response = jsonObj
                                )
                            DialogUtils.showProgressDialog(requireContext())
                            bookingOptionViewModel.pinelabStatusApi(
                                reqBody,
                                pinelab_transaction_status_api
                            )
                            pinelabBillingRefNo = pnrNumber


                        }
                    }

                    400 -> {
                        if (DialogUtils.progressDialog!!.isShowing) {
                            DialogUtils.progressDialog?.dismiss()
                        }
                    }

                    211 -> {
                        if (DialogUtils.progressDialog!!.isShowing) {
                            DialogUtils.progressDialog?.dismiss()
                        }
                        requireActivity().toast("Payment failed! Please try again")
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    402 -> {
                        it.message.let { it1 -> requireActivity().toast(it1) }
                    }

                    else -> {
                        if (it.message != null) {
                            requireActivity().toast(it.message)
                        }
                    }
                }
            } else {
                if (DialogUtils.progressDialog!!.isShowing) {
                    DialogUtils.progressDialog?.dismiss()
                }
                requireActivity().toast("Error occured! Please try again")
            }
        }


        bookingOptionViewModel.walletOtpGeneration.observe(viewLifecycleOwner) {

            (activity as NewConfirmPhoneBookingActivity).hideProgressBar()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (::walletUpiAlertDialog.isInitialized) {
                            walletUpiAlertDialog.findViewById<TextInputLayout>(
                                R.id.layout_otp
                            ).visible()

                            walletUpiAlertDialog.findViewById<Button>(
                                R.id.btnConfirm
                            ).text = getString(R.string.confirm_validate)

                            walletUpiAlertDialog.findViewById<TextView>(
                                R.id.tvSubTitle
                            ).gone()

                            walletUpiAlertDialog.findViewById<RecyclerView>(
                                R.id.rvWalletUpi
                            ).gone()
                        }
                        requireActivity().toast(it.message)
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
                        requireActivity().toast(it.message)
                    }
                }
            } else {
                requireActivity().toast(getString(R.string.server_error))
            }
        }

        bookingOptionViewModel.validateWalletOtp.observe(viewLifecycleOwner) {
            (activity as NewConfirmPhoneBookingActivity).hideProgressBar()
            if (it != null) {
                if (it.code == 200) {
                    if (::walletUpiAlertDialog.isInitialized)
                        walletUpiAlertDialog.cancel()

//                    val intent = if(privilegeResponseModel.country.equals("India", true) || privilegeResponseModel.country.equals("Indonesia", true)) {
//                        Intent(this, TicketDetailsActivityCompose::class.java)
//                    } else {
//                        Intent(this, TicketDetailsActivity::class.java)
//                    }

                    val intent= Intent(requireActivity(), TicketDetailsActivityCompose::class.java)

                    intent.putExtra(getString(R.string.TICKET_NUMBER), bookTicketPnr)
                    intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                    intent.putExtra("activityName2", "booking")
                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    if (it.message != null) {
                        it.message.let { it1 ->
                            requireActivity().toast(it1)
                        }
                    }
                }
            } else {
                requireActivity().toast(getString(R.string.server_error))
            }
        }

        dashboardViewModel.releaseTicketResponseViewModel.observe(viewLifecycleOwner) {

            if (it != null) {
                if (it.code == 200) {
                    if(!it.result.message.isNullOrEmpty()){
                        requireActivity().toast(it.result.message)
                    } else if (!it.message.isNullOrEmpty()) {
                        it.message.let { it1 -> requireActivity().toast(it1) }
                    }
                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

                }
            } else {
                requireActivity().toast(getString(R.string.server_error))
            }
        }

        bookingOptionViewModel.upiCreateQRCodeObserver.observe(viewLifecycleOwner) {

            walletUpiAlertDialog.findViewById<ProgressBar>(
                R.id.progress_bar
            ).gone()


            if (it != null) {
                if (it.code == 200) {

                    if (it.data.body.resultInfo.resultStatus == "FAILURE") {
                        requireActivity().toast(it.data.body.resultInfo.resultMsg)

                    } else {
                        val base64String = it.data.body.image
                        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                        val decodedImage =
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        upiCreateQRAlertDialog = DialogUtils.upiCreateQrCodeDialog(
                            context = requireActivity(),
                            isFromAgentRechargePG = false,
                            dialogSingleButtonListener = this
                        )
                        if (::upiCreateQRAlertDialog.isInitialized && upiCreateQRAlertDialog != null) {

                            upiCreateQRAlertDialog.findViewById<ImageView>(
                                R.id.qr_code_image
                            ).setImageBitmap(decodedImage)
                        }

                        callUPICheckStatusApi()
                    }
                }
            } else {
                requireActivity().toast(getString(R.string.server_error))
            }
        }

        bookingOptionViewModel.upiTranxStatusObserver.observe(viewLifecycleOwner) {

            (activity as NewConfirmPhoneBookingActivity).hideProgressBar()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        requireActivity().toast(it.message)

                        val intent= Intent(requireActivity(), TicketDetailsActivityCompose::class.java)

                        intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                        intent.putExtra("activityName2", "booking")

                        if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                            requireActivity().toast(it.status)
                            intent.putExtra(getString(R.string.TICKET_NUMBER), it.pnrNumber)
                        } else {
                            requireActivity().toast(it.message)
                            intent.putExtra(getString(R.string.TICKET_NUMBER), it.data?.ticketNumber)
                        }
                        intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    400 -> {
                        if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                            if (!isCancelledClicked) {
                                callPayStatOfAgentInsRechargStatusApi()
                            }
                        } else if (!privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser) {
                            if (!isCancelledClicked) {
                                callBranchUpiTranxStatusApi()
                            }
                        }
                        else {
                                callUPICheckStatusApi()

                        }
                    }
                }
            } else {
                requireActivity().toast(getString(R.string.server_error))
            }
        }


        bookingOptionViewModel.ezetapTransactionData.observe(viewLifecycleOwner) {

            (activity as NewConfirmPhoneBookingActivity).hideProgressBar()

            if (it != null) {
                if (it.code == 200) {
                    requireActivity().toast(it.message)

//                    val intent = if(privilegeResponseModel.country.equals("India", true) || privilegeResponseModel.country.equals("Indonesia", true)) {
//                        Intent(this, TicketDetailsActivityCompose::class.java)
//                    } else {
//                        Intent(this, TicketDetailsActivity::class.java)
//                    }


                    val intent= Intent(requireActivity(), TicketDetailsActivityCompose::class.java)
                    intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                    intent.putExtra("activityName2", "booking")

                    intent.putExtra(getString(R.string.TICKET_NUMBER), pnrNumber)
                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                    requireActivity().finish()
                } else if (it.code == 211) {
                    requireActivity().toast(getString(R.string.payment_error_please_try_again_later))
                } else if (it.code == 400) {
                    requireActivity().toast(getString(R.string.error_occured_please_try_again_later))
                }

            } else {
                requireActivity().toast(getString(R.string.server_error))
            }
        }



        bookingOptionViewModel.paytmPosTxnStatusResponse.observe(viewLifecycleOwner) {

            (activity as NewConfirmPhoneBookingActivity).hideProgressBar()

            if (it != null) {
                if (it.code == 200) {
                    requireActivity().toast(it.message.toString())

//                    val intent = if(privilegeResponseModel.country.equals("India", true) || privilegeResponseModel.country.equals("Indonesia", true)) {
//                        Intent(this, TicketDetailsActivityCompose::class.java)
//                    } else {
//                        Intent(this, TicketDetailsActivity::class.java)
//                    }


                    val intent= Intent(requireActivity(), TicketDetailsActivityCompose::class.java)
                    intent.putExtra("activityName", BookingPaymentOptionsActivity::class.java)
                    intent.putExtra("activityName2", "booking")

                    intent.putExtra(getString(R.string.TICKET_NUMBER), pnrNumber)
                    intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                    startActivity(intent)
                    requireActivity().finish()
                } else if (it.code == 211) {
                    requireActivity().toast(getString(R.string.payment_error_please_try_again_later))
                } else if (it.code == 400) {
                    requireActivity().toast(getString(R.string.error_occured_please_try_again_later))
                }

            } else {
                requireActivity().toast(getString(R.string.server_error))
            }
        }
    }

    private fun callUPICheckStatusApi() {
        val reqBody = com.bitla.ts.domain.pojo.upi_check_status.request.ReqBody(
            apiKey = loginModelPref.api_key,
            isFromMiddleTier = is_from_middle_tier.toBoolean(),
            pnrNumber = pnrNumber,
            isSendSms = true
        )

        bookingOptionViewModel.upiTranxStatusApi(
            reqBody = reqBody,
            apiType = upi_tranx_status
        )
    }

    private fun callPayStatOfAgentInsRechargStatusApi() {
        if (requireActivity().isNetworkAvailable()) {
            bookingOptionViewModel.getPayStatOfAgentInsRechargStatusApi(
                apiKey = loginModelPref.api_key,
                pnrNumber = pnrNumber,
                phone = agentPayViaPhoneNumberSMS,
//                amount = totalFareString,
                isFromAgentRecharge = "${privilegeResponseModel.allowUpiForDirectPgBookingForAgents}"
            )
        } else {
            requireActivity().noNetworkToast()
        }
    }

    private fun callBranchUpiTranxStatusApi() {
        if (requireActivity().isNetworkAvailable()) {
            bookingOptionViewModel.getBranchUpiTranxStatusApi(
                apiKey = loginModelPref?.api_key ?: "",
                pnrNumber = bookTicketPnr,
                branchPhone = branchUserPayViaPhoneNumberSMS
            )
        } else {
            requireActivity().noNetworkToast()
        }
    }


    private fun setPaymentOptionsAdapter() {
        paymentOptionsList.clear()

        if (::privilegeResponseModel.isInitialized) {

            if (IS_PINELAB_DEVICE) {
                if (privilegeResponseModel.allowUserToUsePinelabDevicesForUpiPayment) {
                    val pinelabQR = SearchModel()
                    pinelabQR.id = "6"
                    pinelabQR.name = getString(R.string.pinelab_QR)

                    paymentOptionsList.add(pinelabQR)


                    val pinelabCreditDebit = SearchModel()
                    pinelabCreditDebit.id = "7"
                    pinelabCreditDebit.name = getString(R.string.pinelab_debitcard)


                    paymentOptionsList.add(pinelabCreditDebit)
                }
            }
            val availablePaymentOptionsList =privilegeResponseModel.tsPrivileges?.bookingPaymentOptions

            if (privilegeResponseModel.allowCashCreditOptionsInBooking) {
                if(!availablePaymentOptionsList.isNullOrEmpty() && availablePaymentOptionsList.contains("CASH")){
                    val cash = SearchModel()
                    cash.id = "1"
                    cash.name = getString(R.string.cash)
                    paymentOptionsList.add(cash)

                }

                if(!availablePaymentOptionsList.isNullOrEmpty() && availablePaymentOptionsList.contains("CREDIT_DEBIT")){

                    val creditDebitCard = SearchModel()
                    creditDebitCard.id = "2"
                    creditDebitCard.name = getString(R.string.credit_debit)

                    paymentOptionsList.add(creditDebitCard)
                }





            }

            if (!privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowWalletAndUpiOptionsInBookingPage != null
                && privilegeResponseModel.allowWalletAndUpiOptionsInBookingPage
            ) {
                if(!availablePaymentOptionsList.isNullOrEmpty() && availablePaymentOptionsList.contains("WALLET_UPI")){
                    val walletUpi = SearchModel()
                    walletUpi.id = "4"
                    walletUpi.name = getString(R.string.wallet_upi)
                    paymentOptionsList.add(walletUpi)
                }


            }
            if (privilegeResponseModel.allowToConfigurePaymentOptionsInBookingPage) {
                if(!availablePaymentOptionsList.isNullOrEmpty() && availablePaymentOptionsList.contains("OTHERS")){
                    val others = SearchModel()
                    others.id = "3"
                    others.name = getString(R.string.others)
                    paymentOptionsList.add(others)
               }


            }

            if (privilegeResponseModel.isEzetapEnabledInTsApp && !privilegeResponseModel.isAgentLogin && !isOnBehalfOfAgent) {
                paymentOptionsList.clear()


                //Ezetap Radio Option
                val ezetap = SearchModel()
                ezetap.id = "14"
                ezetap.name = getString(R.string.ezetap)
                paymentOptionsList.add(ezetap)


                if(!availablePaymentOptionsList.isNullOrEmpty() && availablePaymentOptionsList.contains("CASH")){
                    val cash = SearchModel()
                    cash.id = "1"
                    cash.name = getString(R.string.cash)
                    paymentOptionsList.add(cash)
                }




            }

            setUpiForDirectPgBookingForAgents()


            // As discussed with Naresh & Faraz, easebuzz pg is renamed as UPI
            if (!privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser &&
                privilegeResponseModel.tsPrivileges?.allowEasebuzzInTs == true
            ){
                if(!availablePaymentOptionsList.isNullOrEmpty() && availablePaymentOptionsList.contains("UPI")){
                    val easebuzz = SearchModel()
                    easebuzz.id = "11"
                    easebuzz.name = getString(R.string.upi_caps)
                    paymentOptionsList.add(easebuzz)
                }


            }

            if (privilegeResponseModel.isPaytmPosEnabled && !privilegeResponseModel.isAgentLogin && !isOnBehalfOfAgent) {
                paymentOptionsList.clear()

                //Paytm Radio Option
                val paytm = SearchModel()
                paytm.id = "14"
                paytm.name = getString(R.string.paytm)
                paymentOptionsList.add(paytm)


                val cash = SearchModel()
                cash.id = "1"
                cash.name = getString(R.string.cash)
                paymentOptionsList.add(cash)

            }
        }

        /*if (!privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
            if(privilegeResponseModel.isAgentLogin || isOnBehalfOfAgent){
                paymentOptionsList.clear()

                paymentType = 1
                val obj = SearchModel()
                obj.id = 1
                obj.name = "Cash"
                paymentOptionsList.add(obj)
            }
        }*/




        if (paymentOptionsList.size > 0) {
            layoutManager = GridLayoutManager(requireActivity(),2, LinearLayoutManager.VERTICAL, false)
            binding.rvPaymentOptions.layoutManager = layoutManager
            val filterAdapter = FilterAdapter(
                requireActivity(),
                this,
                paymentOptionsList,
                lastSelectedPaymentPosition,
                true
            )
            binding.rvPaymentOptions.adapter = filterAdapter
        }

    }

    private fun agentAccountInfo() {
        val agentRequest = AgentAccountInfoRequest(
            bccId.toString(),
            format_type,
            agent_account_info,
            com.bitla.ts.domain.pojo.account_info.request.ReqBody(
                loginModelPref.api_key,
                locale = locale
            )
        )

        agentAccountInfoViewModel.agentAccountInfoAPI(
            agentAccountInfoRequest = agentRequest,
            agentId = "",
            branchId = "",
            apiType = agent_account_info
        )
    }


    private fun setUpiForDirectPgBookingForAgents() {
        if (privilegeResponseModel.isAgentLogin) {
//            val cash = SearchModel()
//            cash.id = "1"
//            cash.name = getString(R.string.cash)
//            paymentOptionsList.add(cash)
//            if(PreferenceUtils.getSubAgentRole()!="true"){
//
//                val walletAgent = SearchModel()
//                walletAgent.id = "15"
//                walletAgent.name = "${getString(R.string.wallet)} (Bal: $currency $getAvailableBalance)"
//                paymentOptionsList.add(walletAgent)
//
//                val netAmtAgent = SearchModel()
//                netAmtAgent.id = "16"
//                netAmtAgent.name = getString(R.string.net_amt_less_off_comm)
//                paymentOptionsList.add(netAmtAgent)
//
//                val fullAmountAgent = SearchModel()
//                fullAmountAgent.id = "17"
//                fullAmountAgent.name = getString(R.string.full_amount)
//                paymentOptionsList.add(fullAmountAgent)
//            }

            if (privileges != null) {
                val availablePaymentOptionsListForAgent=privileges?.agentPaymentOptions

                if(availablePaymentOptionsListForAgent != null){
                    paymentOptionsList.clear()



                    if ("CASH" in availablePaymentOptionsListForAgent) {
                        val cash = SearchModel().apply {
                            id = "1"
                            name = "CASH"
                        }
                        paymentOptionsList.add(cash)
                    }


                    // Only add wallet if user is not a sub-agent
                   if ("PAY_FROM_WALLET" in availablePaymentOptionsListForAgent && PreferenceUtils.getSubAgentRole() != "true") {
                        val walletAgent = SearchModel().apply {
                            id = "15"
                            name = "PAY FROM WALLET (Bal: $currency $getAvailableBalance)"
                        }
                        paymentOptionsList.add(walletAgent)
                   }

                    if ("PAY_NET_AMOUNT" in availablePaymentOptionsListForAgent) {
                        val netAmtAgent = SearchModel().apply {
                            id = "16"
                            name = "PAY NET AMOUNT"
                        }
                        paymentOptionsList.add(netAmtAgent)
                    }

                    if ("PAY_FULL_AMOUNT" in availablePaymentOptionsListForAgent) {
                        val fullAmountAgent = SearchModel().apply {
                            id = "17"
                            name = "PAY FULL AMOUNT"
                        }
                        paymentOptionsList.add(fullAmountAgent)
                    }

                }



            }
        }
        /*else if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser) {
            val cash = SearchModel()
            cash.id = "1"
            cash.name = getString(R.string.cash)
            paymentOptionsList.add(cash)
        }*/

    }

    override fun onButtonClick(vararg args: Any) {
        if (args.isNotEmpty()) {
            val tag = args[0]
            when (tag) {
                getString(R.string.goBack) -> {
                    lastSelectedPaymentPosition = 0
                    setPaymentOptionsAdapter()

                }

                getString(R.string.credit_debit) -> {
                    creditDebitCardNo = args[1].toString()
                    DialogUtils.blockSeatsDialog(
                        showMsg = false,
                        requireActivity(),
                        getString(R.string.confirmBooking),
                        getString(R.string.selected_seat_s_will_be_assigned),
                        srcDest = "${paymentMethodViewModel.source} - ${paymentMethodViewModel.destination} "?: getString(R.string.dash),
                        journeyDate = paymentMethodViewModel.travelDate ?: getString(R.string.dash),
                        noOfSeats = paymentMethodViewModel.noOfSeats!!,
                        seatNo = paymentMethodViewModel.seatNumbers.toString(),
                        getString(R.string.goBack),
                        getString(R.string.confirmBooking),
                        this
                    )
                }


               /* getString(R.string.phone_blocking_cancel_btn) -> {
                    binding.layoutPaymentOptions.visible()

                    val bookingAmount =
                        "${getString(R.string.collet_cash)} $currency $totalFareString ${getString(R.string.and)} ${
                            getString(
                                R.string.book
                            )
                        }"
                    binding.btnBook.text = bookingAmount

                    binding.cardPhoneBooking.setCardBackgroundColor(resources.getColor(R.color.button_secondary_bg))
                }*/
            }
        }
    }

    override fun onSingleButtonClick(str: String) {

        if (str.isNotEmpty()) {
            if (str == getString(R.string.goBack)) {
                lastSelectedPaymentPosition = 0
                paymentType = 1
                if (IS_PINELAB_DEVICE) {
                    if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                        paymentType = 6
                    }
                }
                setPaymentOptionsAdapter()

            }

            else if(str == "Confirm Release"){
                bookingOptionViewModel.upiTranxStatusObserver.removeObservers(viewLifecycleOwner)

            }



            else if (str == getString(R.string.wallet_go_back)) {
                lastSelectedPaymentPosition = 0
                paymentType = 1
                setPaymentOptionsAdapter()

                if (IS_PINELAB_DEVICE) {
                    if (privileges?.allowUserToUsePinelabDevicesForUpiPayment == true) {
                        paymentType = 6
                    }
                }

                selectedWalletUpiOptionId = null
                selectedWalletOrUpi = null
                selectedWalletUpiOptionName = null

                if (bookTicketPnr.isNotEmpty()) {
                    if (requireActivity().isNetworkAvailable())
//                        callReleaseTicketApi()
                    else
                        requireActivity().noNetworkToast()
                }
                paymentMethodViewModel.stopRunningApi = true


            }

            else if (str == getString(R.string.easebuzz_go_back)) {
                lastSelectedPaymentPosition = 0
                paymentType = 1
                setPaymentOptionsAdapter()

                if (IS_PINELAB_DEVICE) {
                    if (PreferenceUtils.getPrivilege()!!.allowUserToUsePinelabDevicesForUpiPayment) {
                        paymentType = 6
                    }
                }

                selectedEasebuzzOptionId = null
                selectedEasebuzz = null
                selectedEasebuzzOptionName = null

                if (bookTicketPnr.isNotEmpty()) {
                    if (requireActivity().isNetworkAvailable())
//                        callReleaseTicketApi()
                    else
                        requireActivity().noNetworkToast()
                }
                paymentMethodViewModel.stopRunningApi = true


            }

            else if (str.contains(getString(R.string.other_payments_confirm))) {
                val otherPaymentOptionPosition = str.substringAfter("-")

                selectedOtherPaymentOption =
                    otherPaymentOptions[otherPaymentOptionPosition.toDouble()
                        .toInt()].payGayTypeName

            }

            else if (str.contains(WalletOptionAdapter.TAG) || str.contains(
                    WalletOptionAgentRechargeAdapter.TAG)) {

                val walletUpiPosition = str.substringAfter("-")

                selectedWalletUpiOptionName = walletPaymentOptions[walletUpiPosition.toDouble().toInt()].name
                selectedWalletUpiOptionId = walletPaymentOptions[walletUpiPosition.toDouble().toInt()].paygayType
                selectedWalletOrUpi = walletPaymentOptions[walletUpiPosition.toDouble().toInt()].type


                when (selectedWalletUpiOptionId) {

                    15-> {
                        PreferenceUtils.putString("upiSelected", "QR")

                        walletUpiAlertDialog.apply {
                            findViewById<TextInputLayout>(R.id.layout_mobile_number).gone()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).gone()
                            findViewById<RadioGroup>(R.id.layout_upi_id).gone()
                            findViewById<RadioGroup>(R.id.et_upi_id).gone()
                        }
                    }

                    16-> {
                        PreferenceUtils.putString("upiSelected", "SMS")

                        walletUpiAlertDialog.apply {
                            findViewById<TextInputLayout>(R.id.layout_mobile_number).visible()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).visible()

                            findViewById<RadioGroup>(R.id.layout_upi_id).gone()
                            findViewById<RadioGroup>(R.id.et_upi_id).gone()
                        }

                    }
                    17-> {
                        PreferenceUtils.putString("upiSelected", "VPA")

                        walletUpiAlertDialog.apply {
                            findViewById<RadioGroup>(R.id.layout_upi_id).visible()
                            findViewById<RadioGroup>(R.id.et_upi_id).visible()

                            findViewById<TextInputLayout>(R.id.layout_mobile_number).gone()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).gone()
                        }
                    }
                    5 -> {
                        PreferenceUtils.putString("upiSelected", "wallet")
                        val groupRadio = walletUpiAlertDialog.findViewById<RadioGroup>(
                            R.id.upi_radio_group
                        )
                        groupRadio.gone()
                        walletUpiAlertDialog.findViewById<TextInputLayout>(
                            R.id.layout_mobile_number
                        ).visible()

                        walletUpiAlertDialog.findViewById<TextInputEditText>(
                            R.id.et_mobile_number
                        ).visible()
                    }

                    6 -> {
                        PreferenceUtils.putString("upiSelected", "wallet")
                        val groupRadio = walletUpiAlertDialog.findViewById<RadioGroup>(
                            R.id.upi_radio_group
                        )
                        groupRadio.gone()
                        walletUpiAlertDialog.findViewById<TextInputLayout>(
                            R.id.layout_mobile_number
                        ).visible()

                        walletUpiAlertDialog.findViewById<TextInputEditText>(
                            R.id.et_mobile_number
                        ).visible()
                    }

                    else -> {
                        PreferenceUtils.putString("upiSelected", "wallet")
                    }
                }

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val walletConfirmButton =
                        walletUpiAlertDialog.findViewById<Button>(
                            R.id.btnConfirm
                        )
                    walletConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))


                    walletUpiAlertDialog.findViewById<TextInputEditText>(
                        R.id.et_mobile_number
                    ).onChange {
                        if (it.isNotEmpty())
                            walletConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                        else
                            walletConfirmButton.setBackgroundColor(resources.getColor(R.color.button_default_color))

                    }
                }

            }

            else if (str.contains(EasebuzzOptionAdapter.TAG)) {
                val easebuzzPosition = str.substringAfter("-")

                selectedEasebuzzOptionName = easebuzzPaymentOptions[easebuzzPosition.toDouble().toInt()].name
                selectedEasebuzzOptionId = easebuzzPaymentOptions[easebuzzPosition.toDouble().toInt()].paygayType
                selectedEasebuzz = easebuzzPaymentOptions[easebuzzPosition.toDouble().toInt()].type

                val btnConfirm =  walletUpiAlertDialog.findViewById<Button>(R.id.btnConfirm)

                when (selectedEasebuzzOptionId) {

                    20-> {
                        PreferenceUtils.putString("easebuzzSelected", "QR")

                        walletUpiAlertDialog.apply {
                            findViewById<TextInputLayout>(R.id.layout_mobile_number).gone()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).gone()
                            findViewById<RadioGroup>(R.id.layout_upi_id).gone()
                            findViewById<RadioGroup>(R.id.et_upi_id).gone()
                        }

                        enableDisableConfirmBtn(btnConfirm, true)
                    }

                    21-> {
                        PreferenceUtils.putString("easebuzzSelected", "SMS")

                        walletUpiAlertDialog.apply {
                            findViewById<TextInputLayout>(R.id.layout_mobile_number).visible()
                            val etMobileNumber = findViewById<TextInputEditText>(R.id.et_mobile_number)
                            etMobileNumber.visible()

                            findViewById<RadioGroup>(R.id.layout_upi_id).gone()
                            findViewById<RadioGroup>(R.id.et_upi_id).gone()

                            enableDisableConfirmBtn(btnConfirm, etMobileNumber.text.toString().isNotEmpty() && etMobileNumber.text.toString().length == 10)
                        }

                    }
                    22-> {
                        PreferenceUtils.putString("easebuzzSelected", "VPA")

                        walletUpiAlertDialog.apply {
                            findViewById<RadioGroup>(R.id.layout_upi_id).visible()
                            val etUpiId = findViewById<TextInputEditText>(R.id.et_upi_id)
                            etUpiId.visible()

                            findViewById<TextInputLayout>(R.id.layout_mobile_number).gone()
                            findViewById<TextInputEditText>(R.id.et_mobile_number).gone()

                            enableDisableConfirmBtn(btnConfirm, etUpiId.text.toString().isNotEmpty())
                        }
                    }
                    else -> {
                        PreferenceUtils.putString("easebuzzSelected", "UPI_Selected")
                    }
                }

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    walletUpiAlertDialog.findViewById<TextInputEditText>(R.id.et_mobile_number).onChange {
                        if (it.length == 10) {
                            enableDisableConfirmBtn(btnConfirm, true)
                        } else {
                            enableDisableConfirmBtn(btnConfirm, false)
                        }
                    }

                    walletUpiAlertDialog.findViewById<TextInputEditText>(R.id.et_upi_id).onChange {
                        if (it.isNotEmpty()) {
                            enableDisableConfirmBtn(btnConfirm, true)
                        } else {
                            enableDisableConfirmBtn(btnConfirm, false)
                        }
                    }
                }
            }

            else if (str.contains("UPI_Selected")) {

                val groupRadio = walletUpiAlertDialog.findViewById<RadioGroup>(
                    R.id.upi_radio_group
                )

//                groupRadio.visible()

                walletUpiAlertDialog.findViewById<RadioButton>(
                    R.id.upi_create_qr
                ).setOnClickListener {
                    walletUpiAlertDialog.findViewById<TextInputLayout>(
                        R.id.layout_mobile_number
                    ).gone()

                    walletUpiAlertDialog.findViewById<TextInputEditText>(
                        R.id.et_mobile_number
                    ).gone()

                }

                when (groupRadio.checkedRadioButtonId) {
//                    R.id.upi_send_sms -> {
//
//                    }

//                    R.id.upi_create_qr -> {
//                        walletUpiAlertDialog.findViewById<ProgressBar>(
//                            R.id.progress_bar
//                        ).visible()
//
//                        callUPICreateQrCodeApi()
//                    }
                }
            }

            else if (str.contains(getString(R.string.wallet_upi_confirm))) {

                val strList = str.split("-")
                if (strList.isNotEmpty()) {
                    walletMobileNo = strList[1]
                }

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val btnText = walletUpiAlertDialog.findViewById<Button>(
                        R.id.btnConfirm
                    ).text.toString()
                    if (btnText == getString(R.string.confirm)) {
                        when {
                            selectedWalletUpiOptionId == null -> requireActivity().toast(getString(R.string.validate_wallet_upi))
                            walletMobileNo.isEmpty() -> requireActivity().toast(getString(R.string.validate_mobile_number))
                            else -> {
                                if (privilegeResponseModel.phoneNumValidationCount!! <= walletMobileNo.toCharArray().size) {
                                    if (requireActivity().isNetworkAvailable()) {
                                        walletOtpGenerationApi()
                                    } else
                                        requireActivity().noNetworkToast()
                                } else {
                                   requireActivity().toast(getString(R.string.invalid_mobile_number))
                                }

                            }
                        }
                    } else {
                        val walletOtp =
                            walletUpiAlertDialog.findViewById<TextInputEditText>(
                                R.id.et_otp
                            ).text.toString()

                        if (walletOtp.isEmpty())
                            requireActivity().toast(getString(R.string.validate_otp))
                        else {
                            if (requireActivity().isNetworkAvailable()) {
                                validateWalletOtpApi(otp = walletOtp)
                            } else
                                requireActivity().noNetworkToast()
                        }
                    }
                }
            }

            else if (str.contains("QR")) {
//                toast("testing QR $str")
                selectedSubPaymentOptionName = str

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val btnText = walletUpiAlertDialog.findViewById<Button>(R.id.btnConfirm
                    ).text.toString()
                    if (btnText == getString(R.string.select)) {

                        when {
                            selectedSubPaymentOptionName?.isEmpty() == true -> requireActivity().toast(getString(R.string.please_selecte_an_option))
                            else -> {
                                walletUpiAlertDialog.dismiss()
                            }
                        }
                    }
                }
            }

            else if (str.contains("SMS")) {
                val strList = str.split("-")
                if (strList.isNotEmpty()) {
                    if(isAgentLogin)
                        agentPayViaPhoneNumberSMS = strList[1]
                    else
                        branchUserPayViaPhoneNumberSMS = strList[1]
                }

                selectedSubPaymentOptionName = strList[0]

//                toast("testing SMS - $agentPayViaPhoneNumberSMS")

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val btnText = walletUpiAlertDialog.findViewById<Button>(
                        R.id.btnConfirm
                    ).text.toString()
                    if (btnText == getString(R.string.select)) {
                        val walletConfirmButton =
                            walletUpiAlertDialog.findViewById<Button>(
                                R.id.btnConfirm
                            )
                        walletConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))

                        if (isAgentLogin) {
                            when {
                                agentPayViaPhoneNumberSMS.isEmpty() -> requireActivity().toast(getString(R.string.validate_mobile_number))
                                selectedSubPaymentOptionName?.isEmpty() == true -> requireActivity().toast(getString(R.string.please_selecte_an_option))
                                else -> if (privilegeResponseModel.phoneNumValidationCount!! <= agentPayViaPhoneNumberSMS.toCharArray().size) {
                                    if (requireActivity().isNetworkAvailable()) {
                                        walletUpiAlertDialog.dismiss()
                                    } else
                                        requireActivity().noNetworkToast()
                                } else {
                                    requireActivity().toast(getString(R.string.invalid_mobile_number))
                                }
                            }
                        } else {
                            when {
                                branchUserPayViaPhoneNumberSMS.isEmpty() -> requireActivity().toast(getString(R.string.validate_mobile_number))
                                selectedSubPaymentOptionName?.isEmpty() == true -> requireActivity().toast(getString(R.string.please_selecte_an_option))
                                else -> if (privilegeResponseModel.phoneNumValidationCount!! <= branchUserPayViaPhoneNumberSMS.toCharArray().size) {
                                    if (requireActivity().isNetworkAvailable()) {
                                        walletUpiAlertDialog.dismiss()
                                    } else
                                        requireActivity().noNetworkToast()
                                } else {
                                    requireActivity().toast(getString(R.string.invalid_mobile_number))
                                }
                            }
                        }
                    }
                }
            }

            else if (str.contains("VPA")) {
                val strList = str.split("-")
                if (strList.isNotEmpty()) {
                    if(isAgentLogin)
                        agentPayViaVPA = strList[1]
                    else
                        branchUserPayViaVPA = strList[1]
                }

//                toast("testing VPA - $agentPayViaVPA")

                selectedSubPaymentOptionName = strList[0]

                if (::walletUpiAlertDialog.isInitialized && walletUpiAlertDialog != null) {
                    val btnText = walletUpiAlertDialog.findViewById<Button>(
                        R.id.btnConfirm
                    ).text.toString()
                    if (btnText == getString(R.string.select)) {
                        val walletConfirmButton =
                            walletUpiAlertDialog.findViewById<Button>(
                                R.id.btnConfirm
                            )
                        walletConfirmButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))

                        if (isAgentLogin) {
                            when {
                                selectedSubPaymentOptionName?.isEmpty() == true -> requireActivity().toast(getString(R.string.please_selecte_an_option))
                                agentPayViaVPA.isEmpty() -> requireActivity().toast(getString(R.string.enter_upi_id))
                                else -> {
                                    walletUpiAlertDialog.dismiss()
                                }
                            }
                        } else {
                            when {
                                branchUserPayViaVPA.isEmpty() -> requireActivity().toast(getString(R.string.enter_upi_id))
                                selectedSubPaymentOptionName?.isEmpty() == true -> requireActivity().toast(getString(R.string.please_selecte_an_option))
                                else -> {
                                    walletUpiAlertDialog.dismiss()
                                }
                            }
                        }
                    }
                }
            }

            else if (str == "qr_confirm") {
                if (::walletUpiAlertDialog.isInitialized) {
                    walletUpiAlertDialog.dismiss()
                }
                if (::upiCreateQRAlertDialog.isInitialized) {
                    upiCreateQRAlertDialog.dismiss()
                }

                if (::upiAuthSmsAndVPADialog.isInitialized) {
                    upiAuthSmsAndVPADialog.dismiss()
                }
            }
            else if (str == getString(R.string.cancel)) {

                lastSelectedPaymentPosition = 0
                paymentType = 1

                if (::walletUpiAlertDialog.isInitialized) {
                    walletUpiAlertDialog.dismiss()
                }
                if (::upiCreateQRAlertDialog.isInitialized) {
                    upiCreateQRAlertDialog.dismiss()
                }

                if (::upiAuthSmsAndVPADialog.isInitialized) {
                    upiAuthSmsAndVPADialog.dismiss()
                }

                setPaymentOptionsAdapter()
                isCancelledClicked = true
            }
            else if (str == getString(R.string.unauthorized)) {
                //clearAndSave(requireContext())
                PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun walletOtpGenerationApi() {
        val reqBody = com.bitla.ts.domain.pojo.wallet_otp_generation.request.ReqBody(
            amount = totalFareString,
            api_key = loginModelPref.api_key,
            is_from_middle_tier = is_from_middle_tier.toBoolean(),
            pnr_number = pnrNumber,
            wallet_mobile = walletMobileNo,
            wallet_type = selectedWalletUpiOptionId.toString(),
            locale = locale,
            is_resend_otp = true
        )

        val walletOtpGenerationRequest = WalletOtpGenerationRequest(
            bcc_id = bccId.toString(), format_type, wallet_otp_generation_method_name, reqBody
        )
        bookingOptionViewModel.walletOtpGenerationApi(
            reqBody,
            apiType = wallet_otp_generation_method_name
        )
    }

    private fun enableDisableConfirmBtn(button: Button, isEnable: Boolean) {
        if (isEnable) {
            button.backgroundTintList = AppCompatResources.getColorStateList(requireContext(), R.color.colorPrimary)
            button.isEnabled = true
        } else {
            button.backgroundTintList = AppCompatResources.getColorStateList(requireContext(), R.color.colorShadow)
            button.isEnabled = false
        }
    }

    private fun validateWalletOtpApi(otp: String) {
        val reqBody = com.bitla.ts.domain.pojo.validate_otp_wallets.request.ReqBody(
            amount = totalFareString,
            api_key = loginModelPref.api_key,
            is_from_middle_tier = is_from_middle_tier.toBoolean(),
            otp_number = otp,
            phone_blocked = isPhoneBlockedWallet,
            pnr_number = pnrNumber,
            wallet_mobile = walletMobileNo,
            wallet_type = selectedWalletUpiOptionId.toString(),
            locale = locale
        )

        val validateOtpWalletsRequest = ValidateOtpWalletsRequest(
            bcc_id = bccId.toString(), format_type, validate_otp_wallets_method_name, reqBody
        )
        bookingOptionViewModel.validateWalletOtpApi(
            reqBody,
            apiType = validate_otp_wallets_method_name
        )
    }


    private fun getBookingRequest() {
        bookingCustomRequest = retrieveBookingCustomRequest()
        agentType = if (bookingCustomRequest.selected_booking_id.toString() == "12")
            "3"
        else
            bookingCustomRequest.selected_booking_id.toString()
        onBeHalfUser = bookingCustomRequest.branch_user
        onBehalfBranch = bookingCustomRequest.branch_id.toString()
        refBookingNo = bookingCustomRequest.reference_no
        onBehalf = bookingCustomRequest.offline_agent_on_behalf.toString()
        onBehalfOnlineAgent = bookingCustomRequest.online_agent_on_behalf.toString()
    }




}
