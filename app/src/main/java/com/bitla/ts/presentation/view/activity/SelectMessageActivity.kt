package com.bitla.ts.presentation.view.activity

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.lifecycle.*
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.sms_types.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible

class SelectMessageActivity : BaseActivity(), OnItemClickListener, DialogSingleButtonListener {

    companion object {
        val tag: String = SelectMessageActivity::class.java.simpleName
    }

    private var smsTemplate: SmsTemplate? = null
    private var smsTemplatesList: List<SmsTemplate> = listOf()
    private lateinit var binding: ActivitySelectMessageBinding
    private lateinit var selectSmsAdapter: SelectSmsAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val smsTypesViewModel by viewModel<SmsTypesViewModel<Any?>>()
    private var apiKey: String = ""
    private var bccId: String = ""
    private var resId: Long = 0
    private var loginModelPref = LoginModel()
    private var locale: String? = ""

    override fun initUI() {
        binding = ActivitySelectMessageBinding.inflate(layoutInflater)
        val view = binding.root
        binding.toolbar.tvCurrentHeader.text = getString(R.string.select_message)
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        lifecycleScope.launch {
            smsTypesViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPref()
        callSmsTypesApi()
        setUpObserver()
        onClickListener()
    }

    override fun isInternetOnCallApisAndInitUI() {
        getPref()
        callSmsTypesApi()
        setUpObserver()
        onClickListener()
    }

    private fun onClickListener() {
        binding.toolbar.imgBack.setOnClickListener(this)
        binding.toolbar.tvDone.setOnClickListener(this)
        binding.btnCustomMessage.setOnClickListener(this)
        binding.layoutCustomMessage.setOnClickListener(this)
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        locale = PreferenceUtils.getlang()
        apiKey = loginModelPref.api_key
        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!
    }

    private fun callSmsTypesApi() {
        if (isNetworkAvailable()) {
            smsTypesViewModel.smsTypesApi(
                apiKey = loginModelPref.api_key,
                resId = resId.toString(),
                locale = locale ?: "en",
                responseFormat = response_format,
                apiType = sms_types_method_name
            )

        } else
            noNetworkToast()
    }

    private fun setUpObserver() {
        smsTypesViewModel.loadingState.observe(this, Observer {
            when (it) {
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> {
                    it.msg?.let { it1 -> toast(it1) }
                    binding.includeProgress.progressBar.gone()
                }
            }

        })
        smsTypesViewModel.smsTypes.observe(this, Observer {
            binding.includeProgress.progressBar.gone()
            binding.layoutCustomMessage.visible()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        smsTemplatesList =
                            it.result?.sms_templates?.subList(1, it.result.sms_templates.size)!!
                        val employeeTypes = EmployeeTypes(it.result.employee_type_options!!)
                        PreferenceUtils.putObject(
                            employeeTypes,
                            PREF_EMPLOYEE_TYPE_OPTIONS
                        )
                        it.result.sms_templates.let { it1 -> setSelectSmsAdapter(it1) }
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }
                    else -> it.result?.message?.let { it1 -> toast(it1) }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        })
    }

    private fun setSelectSmsAdapter(smsTemplates: List<SmsTemplate>) {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvMessages.layoutManager = layoutManager
        selectSmsAdapter =
            SelectSmsAdapter(this, this, smsTemplates.subList(1, smsTemplates.size), -1)
        binding.rvMessages.adapter = selectSmsAdapter
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (position in smsTemplatesList.indices) {
            binding.toolbar.tvDone.visible()
            if (smsTemplatesList.isNotEmpty()) {
                smsTemplate = smsTemplatesList[position]
                PreferenceUtils.putObject(smsTemplate, PREF_SMS_TEMPLATE)
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.img_back -> {
                onBackPressed()
            }
            R.id.tvDone -> {
                val intent = Intent(this, SmsNotificationActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.btnCustomMessage -> {
                val intent = Intent(this, CustomMessageActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.layoutCustomMessage -> {
                val intent = Intent(this, CustomMessageActivity::class.java)
                startActivity(intent)
                finish()
            }
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