package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.view.*
import android.view.inputmethod.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.passenger_history.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.google.zxing.integration.android.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible

class PnrSearchActivity : BaseActivity(), OnItemClickListener, DialogSingleButtonListener {

    lateinit var binding: ActivityPnrSearchBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val passengerHistoryViewModel by viewModel<PassengerHistoryViewModel<Any?>>()
    private var loginModelPref: LoginModel = LoginModel()
    private lateinit var baseUpdateCancelTicket: BaseUpdateCancelTicket
    private lateinit var editPassengerSheet: EditPassengerSheet
    private lateinit var cancelTicketSheet: CancelTicketSheet

    private var isAllowPNRSearch: Boolean? = false
    private var isShowPassengerSearchInHomePageForUsers: Boolean? = false
    private val ticketDetailsViewModel by viewModel<TicketDetailsComposeViewModel<Any?>>()
    private var locale:String? = ""
    private var pnrNumber:String?=""
    private var country:String?=""
//    private var privilegeResponseModel: PrivilegeResponseModel?= null

    override fun initUI() {
        binding = ActivityPnrSearchBinding.inflate(layoutInflater)
        val view = binding.root
        baseUpdateCancelTicket =
            supportFragmentManager.findFragmentById(R.id.layoutUpdateTicketContainer) as BaseUpdateCancelTicket
        cancelTicketSheet =
            supportFragmentManager.findFragmentById(R.id.layoutCancelTicketSheet) as CancelTicketSheet
        editPassengerSheet =
            supportFragmentManager.findFragmentById(R.id.layoutEditPassengerSheet) as EditPassengerSheet



        getPref()

        // setNetworkConnectionObserver
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        lifecycleScope.launch {
            ticketDetailsViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            passengerHistoryViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnScan.gone()

        showSoftKeyboard(binding.etPnrSearch)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_pnr -> {
//                    binding.etPnrSearch.inputType = InputType.TYPE_CLASS_TEXT
                }
                R.id.radio_passenger -> {
//                    binding.etPnrSearch.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
        }

        binding.etPnrSearch.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_NEXT) {
                val radioId = binding.radioGroup.checkedRadioButtonId
                when (radioId) {
                    R.id.radio_passenger -> {
                        getPassengerHistoryApi()
                        binding.rvPnrDetails.visible()
                    }
                    R.id.radio_pnr -> {
                        next(v)
                    }
                }
            }
            false
        }

        binding.btnScan.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(true)
            scanner.setBarcodeImageEnabled(true)
            scanner.initiateScan()
        }

//        binding.btnScan.setOnClickListener {
//            val scanner = IntentIntegrator(this)
//            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
//            scanner.setBeepEnabled(true)
//            scanner.setBarcodeImageEnabled(true)
//            scanner.initiateScan()
//        }

//        binding.etPnrSearch.setOnFocusChangeListener { view, b ->
//
//            if (binding.radioPassenger.isChecked) {
////                binding.etPnrSearch.inputType = InputType.TYPE_CLASS_TEXT
//            } else if (binding.radioPnr.isChecked) {
////                binding.etPnrSearch.inputType = InputType.TYPE_CLASS_TEXT
//            }
//        }

        binding.etPnrSearch.addTextChangedListener(object : TextWatcher {
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
//                if (count > 0) {
//                    binding.etPnrSearch.setCompoundDrawablesWithIntrinsicBounds(
//                        R.drawable.ic_search,
//                        0,
//                        0,
//                        0
//                    )
//                } else {
//                    binding.etPnrSearch.setCompoundDrawablesWithIntrinsicBounds(
//                        R.drawable.ic_search,
//                        0,
//                        R.drawable.ic_scan,
//                        0
//                    )
//                }
            }
        })
        setTicketDetailsObserver()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        passengerHistoryViewModel.privilegesLiveData.observe(this) { privilegeResponse ->
            if (privilegeResponse != null) {
                privilegeResponse.apply {
                    if (availableAppModes?.allowPNRSearch != null) {
                        isAllowPNRSearch = availableAppModes.allowPNRSearch
                    }
                    if (showPassengerSearchInHomePageForUsers != null) {
                        isShowPassengerSearchInHomePageForUsers = showPassengerSearchInHomePageForUsers
                    }
                }

                country = privilegeResponse.country ?: ""

                if (isAllowPNRSearch == true) {
                    binding.radioPnr.visible()
                } else {
                    binding.radioPnr.gone()
                }
                if (isShowPassengerSearchInHomePageForUsers == true) {
                    binding.radioPassenger.visible()
                } else {
                    binding.radioPassenger.gone()
                }

                toast("hello")
            }
        }
    }

    private fun getPassengerHistoryApi() {
        val passengerDetails = binding.etPnrSearch
        loginModelPref = PreferenceUtils.getLogin()
        
        if (isNetworkAvailable()) {
            if (passengerDetails.text.isNotEmpty()) {
                passengerHistoryViewModel.passengerHistoryApi(
                    apiKey = loginModelPref.api_key,
                    response_format = json_format,
                    passenger_details = passengerDetails.text.toString(),
                    operator_api_key = operator_api_key,
                    locale = locale!!,
                    apiType = ticket_details_method_name
                )
            } else {
                toast("Please enter passenger details to search with")
            }
        } else {
            noNetworkToast()
        }

        passengerHistoryViewModel.dataPassengersHistory.observe(this) {
            if (it != null && it.code == 200) {
                val passengerHistory = it.body as ArrayList<PassengerHistoryModel>
                setPnrDetailsAdapter(passengerHistory)
            } else if (it.code == 401) {
                /*DialogUtils.unAuthorizedDialog(
                    this,
                    "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                    this
                )*/
                showUnauthorisedDialog()

            } else {
                it?.message?.let { it1 -> toast(it1) }
            }
        }
    }

    private fun setPnrDetailsAdapter(passengerHistory: ArrayList<PassengerHistoryModel>) {
        val searchList = passengerHistory
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPnrDetails.layoutManager = layoutManager
        val bookPassengersAdapter = PnrDetailsAdapter(this, this, searchList, country)
        binding.rvPnrDetails.adapter = bookPassengersAdapter
    }

    fun next(v: View) {
        Timber.d("pnrNumber ${binding.etPnrSearch.text}")
        pnrNumber = binding.etPnrSearch.text.toString().trim()

        val intent = Intent(this, TicketDetailsActivityCompose::class.java)
        intent.apply {
            putExtra(getString(R.string.TICKET_NUMBER), pnrNumber)
            putExtra(getString(R.string.pnr_search), true)
            putExtra("returnToDashboard", false)
            putExtra("fromPnrActivity", true)
        }
        startActivity(intent)

//        getTicketDetailsApi(pnrNumber.toString())
    }

    @SuppressLint("SetTextI18n")
    private fun getTicketDetailsApi(pnrNumber: String) {
        if (this.isNetworkAvailable()) {
            binding.includeProgress.progressBar.visible()
            ticketDetailsViewModel.ticketDetailsApi(
                apiKey = loginModelPref.api_key,
                ticketNumber = pnrNumber.trim(),
                jsonFormat = true,
                loadPrivs = false,
                menuPrivilege = false,
                isQrScan = false,
                locale = locale!!,
                apiType = ticket_details_method_name
            )
        } else this.noNetworkToast()
    }

    @SuppressLint("SetTextI18n")
    private fun setTicketDetailsObserver() {
        ticketDetailsViewModel.dataTicketDetails.observe(this) {
            binding.includeProgress.progressBar.gone()

            if (it != null) {
                if (it.code == 200) {
                    pnrNumber = binding.etPnrSearch.text.toString().trim()
                    
                    if (it.body?.code == 419) {
                        if (it.body.message != null) {
                            it.body.message.let { it1 -> toast(it1) }
                        }
                    } else {
                        val intent = Intent(this, TicketDetailsActivityCompose::class.java)
                        intent.apply {
                            putExtra(getString(R.string.TICKET_NUMBER), pnrNumber)
                            putExtra(getString(R.string.pnr_search), true)
                            putExtra("returnToDashboard", false)
                            putExtra("fromPnrActivity", true)
                        }
                        startActivity(intent)
                    }
                    
                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

                } else {
                    if (it.message != null) {
                        it.message.let { it1 -> toast(it1) }
                    } else if (it.message != null) {
                        it.message.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.opps))
            }
        }
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        val intent=Intent(this, TicketDetailsActivityCompose::class.java)
        intent.putExtra("returnToDashboard", false)
        intent.putExtra("fromPnrActivity", true)
        startActivity(intent)
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

        when (data) {
            getString(R.string.edit_passenger_details) -> {
               // baseUpdateCancelTicket.showEditPassengersSheet(position)
                editPassengerSheet.showEditPassengersSheet(position)
            }
            getString(R.string.view_ticket) -> {
//                val intent = if (country.equals("India", true) || country.equals("Indonesia", true)) {
//                    Intent(this, TicketDetailsActivityCompose::class.java)
//                } else {
//                    Intent(this, TicketDetailsActivity::class.java)
//                }

                val intent=Intent(this, TicketDetailsActivityCompose::class.java)
                intent.putExtra(getString(R.string.TICKET_NUMBER), position.toString())
                intent.putExtra("returnToDashboard", false)
                startActivity(intent)
            }
            else -> {
              //  baseUpdateCancelTicket.showTicketCancellationSheet(position)
                cancelTicketSheet.showTicketCancellationSheet(position)

            }
        }
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, result: Result) {

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    toast(getString(R.string.cancelled))
                } else {
                    toast(result.contents)
                    val pnrNumber = result.contents
                    if (pnrNumber.isNotEmpty()) {
                        val intent=Intent(this, TicketDetailsActivityCompose::class.java)
                        intent.putExtra(getString(R.string.TICKET_NUMBER), pnrNumber)
                        intent.putExtra("returnToDashboard", false)
                        intent.putExtra("fromPnrActivity", true)
                        intent.putExtra("qrscan", true)
                        startActivity(intent)
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
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
}