package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.db.UserViewModel
import com.bitla.ts.data.listener.DialogButtonListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.login_model.Users
import com.bitla.ts.domain.pojo.login_model.request.LoginRequest
import com.bitla.ts.domain.pojo.login_model.request.ReqBody
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.view.activity.resetpassword.MobileNumberEntry
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.viewModel.LoginViewModel
import com.bitla.ts.presentation.viewModel.PrivilegeDetailsViewModel
import com.bitla.ts.utils.LoadingState.Companion.LOADED
import com.bitla.ts.utils.LoadingState.Companion.LOADING
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.edgeToEdgeFromOnlyBottom
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getCountryCodes
import com.bitla.ts.utils.common.getDeviceUniqueId
import com.bitla.ts.utils.common.getUserRole
import com.bitla.ts.utils.common.updateBaseURL
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.security.EncrypDecryp
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getBccId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLogin
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getUpdatedApiUrlAddress
import com.bitla.ts.utils.showToast
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import toUserModel
import gone
import io.sentry.Sentry
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.viewModel
import setSafeOnClickListener
import timber.log.Timber
import toast
import visible
import java.util.*

@AndroidEntryPoint
class LoginActivity : BaseActivity(), DialogButtonListener, DialogSingleButtonListener {

    companion object {
        val TAG = LoginActivity::class.java.simpleName
    }

    private var isReLoginClick: Boolean = false
    private var deviceId: String = ""
    private var logo: String? = null
    private var domain: String? = null
    private val loginViewModel by viewModel<LoginViewModel>()
    private val userViewModel: UserViewModel by viewModels()

    private var onChangeUsername: String = ""
    private var onChangePassword: String = ""
    private var username: String = ""
    private var password: String = ""
    private var bccId: Int? = 0

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var loginModelPref: LoginModel = LoginModel()
    private var isAgentLogin: Boolean = false
    private var locale: String? = ""
    private var isBiometricLinked: Boolean = false
    private var nameCountry: String = ""
    private var countryList: MutableList<Int> = mutableStateListOf()
    private var selectedShiftId: Int? = null
    private var selectedCounterId: Int? = null


    private fun notifyUser(message: String) {
        DialogUtils.oneTouchDialog(this, message, this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 201) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun enableNotification()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.areNotificationsEnabled() == false) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        AlertDialog.Builder(this)
                            .setMessage("Need notification permission.")
                            .setPositiveButton("OK") { dialogInterface, i ->
                                dialogInterface.dismiss()
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                    201
                                )
                            }
                            .setNegativeButton("Cancel") { dialogInterface, i ->
                                dialogInterface.dismiss()
                            }
                            .create()
                            .show()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            201
                        )
                    }
                }
                else if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2){
                    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    if (!notificationManager.areNotificationsEnabled()) {
                        AlertDialog.Builder(this)
                            .setMessage("Need notification permission.")
                            .setPositiveButton("OK") { dialogInterface, i ->
                                dialogInterface.dismiss()
                                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
                                startActivity(settingsIntent)
                            }
                            .setNegativeButton("Cancel") { dialogInterface, i ->
                                dialogInterface.dismiss()
                            }
                            .create()
                            .show()
                    } else {
                        val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
                        startActivity(settingsIntent)
                    }
                }

            }
        }
    }

    override fun initUI() {
        // toolbar_header_text.text = getString(R.string.login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            WindowCompat.setDecorFitsSystemWindows(window, false) // Enables edge-to-edge
            edgeToEdgeFromOnlyBottom(binding.root)

        }


        binding.includeHeader.layoutTravelLogoView.visible()
        binding.includeHeader.toolbarImageLeft.visible()
        clickListener()

        lifecycleScope.launch {
            loginViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }

        // setNetworkConnectionObserver(view)
    }

    private fun checkDmn(str: String)
    {
        if (str.contains("co.in")) {
            enableNotification()
        } else {
            Timber.d("no india Country==>")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = Firebase.analytics
        locale = PreferenceUtils.getlang()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.textOneTouchLogin.visible()
        } else {
            binding.textOneTouchLogin.gone()
        }
        binding.includeHeader.imageLogo.gone()

        //val loginModelPref = PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER)!!
        //Timber.d("loginModelPref==> ${Gson().toJson(loginModelPref)}")


        binding.edtName.setText(PreferenceUtils.getLogin().userName)

        bccId = getBccId()

        if (PreferenceUtils.getPreference(PREF_LOGO, getString(R.string.empty)) != null)
            logo = PreferenceUtils.getPreference(PREF_LOGO, getString(R.string.empty))

        if (logo != null) {
            Glide.with(this).load(logo)
                .override(600, 200)
                .fitCenter()
                .error(R.drawable.ic_ts_logo)
                .into(binding.includeHeader.imgLogo)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_ts_logo)
                .override(600, 200)
                .fitCenter()
                .placeholder(R.drawable.ic_ts_logo)
                .error(R.drawable.ic_ts_logo)
                .into(binding.includeHeader.imgLogo)

        }

        domain = PreferenceUtils.getPreference(
            PREF_DOMAIN,
            getString(R.string.empty)
        )

        loginViewModel.data.observe(this) {
            // Populate the UI

            binding.includeProgress.progressBar.gone()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        if(it.is_counter_enabled_by_user == true) {
                            showCounterLogin(it)
                            return@observe
                        }

                        val loginModel = it
                        loginModel.userName = binding.edtName.text.toString()
                        loginModel.password = binding.edtPassword.text.toString()
                        loginModel.domainName = domain ?: ""

                        loginModelPref.name = it.name
                        loginModelPref.travels_name = it.travels_name
                        loginModelPref.role = it.role
                        PreferenceUtils.setSubAgentRole(it.role)

                        //                PreferenceUtils.putObject(loginModel, PREF_LOGGED_IN_USER)
                        if (it.otp != null && it.otp.length > 0) {
                            if (it.mobile_number.length <= 10) {
                                //toast(it.otp)
                                val intent = Intent(this, OtpVerification::class.java)
                                intent.putExtra("MOBILE", it.mobile_number)
                                intent.putExtra(getString(R.string.username), username)
                                intent.putExtra(getString(R.string.password), password)
                                intent.putExtra("KEY", it.key)
                                startActivity(intent)
                            }
                        } else {
                            if (domain != null)
                                loginModel.domainName = domain!!

                            addUsers(loginModel)
                        }

                        PreferenceUtils.removeKey(IS_LOGOUT_VIA_AUTH_FAIL)

                        loginModelPref = PreferenceUtils.getLogin()
                        if (getPrivilegeBase() != null) {
                            val privilegeResponse = getPrivilegeBase()
                            privilegeResponse?.let {
                                if (privilegeResponse.isAgentLogin) {
                                    isAgentLogin = privilegeResponse.isAgentLogin
                                }
                            }
                        }

                        val role = getUserRole(loginModel, isAgentLogin, this)

                        try {
                            if (getCountryCodes().isNotEmpty())
                                countryList = getCountryCodes()
                        } catch (e: Exception) {
                            Timber.d("countryCode - $e")
                        }

                        val countyCode = if (countryList.isNotEmpty()) {
                            countryList[0]
                        } else {
                            ""
                        }

                        logFeatureSelectedEvent(
                            loginId = username,
                            operatorName = loginModel.travels_name,
                            roleName = role,
                            countyCode = countyCode.toString()
                        )

                        if(isBiometricLinked) {
                            firebaseLogEvent(
                                this,
                                ONE_TOUCH_LOGIN,
                                loginModelPref.userName,
                                loginModelPref.travels_name,
                                loginModelPref.role,
                                ONE_TOUCH_LOGIN,
                                "One-touch Login click"
                            )

                        } else {

                            firebaseLogEvent(
                                this,
                                APP_LOGIN,
                                loginModelPref.userName,
                                loginModelPref.travels_name,
                                loginModelPref.role,
                                APP_LOGIN,
                                "Login button"
                            )
                        }

                    }
                    399 -> {
                        DialogUtils.twoButtonDialog(
                            this,
                            getString(R.string.use_here),
                            getString(R.string.already_logged_in),
                            getString(R.string.cancel),
                            getString(R.string.use_here2),
                            this
                        )
                    }
                    411 -> {
                        DialogUtils.deviceRegistrationDialog(
                            this,
                            it.result.message ?: "",
                            true,
                            deviceId = deviceId,
                            this
                        )
                    }
                    else -> {
                        it.result.message?.let { it1 -> toast(it1) }
                        isBiometricLinked = false

                    }
                }
            } else {
                toast(getString(R.string.server_error))
                isBiometricLinked = false

            }
        }

        loginViewModel.dataLogout.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                if (it.code == 200) {

                    if(it.is_counter_enabled_by_user == true) {
                        showCounterLogin(it)
                        return@observe
                    }

                    PreferenceUtils.putObject(it, PREF_LOGGED_IN_USER)
                    PreferenceUtils.putString(PREF_IS_USER_LOGIN, "true")

                    PreferenceUtils.putString(PREF_NEW_BUS_LOCATION_ADDED_LOGO_DISPLAYED,"false")
                    PreferenceUtils.putString(PREF_NEW_BUS_LOCATION_ADDED_POPUP_DISPLAYED,"false")

                    if (domain != null)
                        it.domainName = domain!!
                    it.userName = binding.edtName.text.toString()
                    it.password = binding.edtPassword.text.toString()
                    loginModelPref.travels_name = it.travels_name
                    loginModelPref.name = it.name
                    loginModelPref.role = it.role

                    //it.bccId = getBccId()

                    PreferenceUtils.removeKey(IS_LOGOUT_VIA_AUTH_FAIL)
                    PreferenceUtils.setSubAgentRole(it.is_sub_agent_and_user)

                    addUsers(it)

                    /*var userList = mutableListOf<LoginModel>()
                    userList.add(it)
                    PreferenceUtils.putObject(Users(userList), PREF_USER_LIST_STRING)*/

                    intent = Intent(this, DashboardNavigateActivity::class.java)
                    startActivity(intent)

                    if(isBiometricLinked) {
                        firebaseLogEvent(
                            this,
                            ONE_TOUCH_LOGIN,
                            loginModelPref.name,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            ONE_TOUCH_LOGIN,
                            "One-touch Login click"
                        )

                    } else {

                        firebaseLogEvent(
                            this,
                            APP_LOGIN,
                            loginModelPref.name,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            APP_LOGIN,
                            "Login button"
                        )
                    }
                } else if (it.code == 411 && isReLoginClick) {
                    DialogUtils.deviceRegistrationDialog(
                        this,
                        it.result.message ?: "",
                        true,
                        deviceId = deviceId,
                        this
                    )
                } else {
                    toast(it.message)
                    isBiometricLinked = false

                }
            } else {
                toast(getString(R.string.server_error))
                isBiometricLinked = false

            }
            isReLoginClick = false
        }

        loginViewModel.loadingState.observe(this, Observer {
            // Observe the loading state
            when (it) {
                LOADING -> binding.includeProgress.progressBar.visible()
                LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }

        })

        loginViewModel.etOnChange.observe(this, Observer {
            if (!it) {
                binding.buttonLogin.setBackgroundResource(R.drawable.button_default_bg)
            } else {
                binding.buttonLogin.setBackgroundResource(R.drawable.button_selected_bg)
            }
        })

        binding.edtName.onChange {
            onChangeUsername = it
            listenTextWatcher()
        }

        binding.edtPassword.onChange {
            onChangeUsername = binding.edtName.text.toString()
            onChangePassword = it
            listenTextWatcher()
        }

        loginViewModel.validationData.observe(this) {
            if (it.isNotEmpty())
                toast(it)
            else {
                //  binding.includeProgress.progressBar.visible()
                deviceId = getDeviceUniqueId(this)

                if (isNetworkAvailable()) {
                    loginViewModel.loginApi(username,password,  locale, getDeviceUniqueId(this))
                } else
                    noNetworkToast()
            }
        }
       nameCountry = domain.toString()
     //  checkDmn(nameCountry)
        enableNotification()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    @SuppressLint("SetTextI18n")
    private fun addUsers(loginModel: LoginModel) {

        loginModel.dialingCode = getCountryCodes()
        loginModel.isEncryptionEnabled = EncrypDecryp.isEncrypted()

        /*val obj = loginModel
        obj.api_key = getEncryptedValue(loginModel.api_key)*/
        PreferenceUtils.putObject(loginModel, PREF_LOGGED_IN_USER)
        PreferenceUtils.putString(PREF_IS_USER_LOGIN, "true")


        if(loginModel.api_travel_id.isNotEmpty()){
        PreferenceUtils.putString("apiTravelId", loginModel.api_travel_id)
      }
        loginModel.mba_url = getUpdatedApiUrlAddress()


        loginModel.isEncryptionEnabled=EncrypDecryp.isEncrypted()
        userViewModel.insertUser(loginModel.toUserModel())

    }


    private fun listenTextWatcher() {
        loginViewModel.etTextWatcher(onChangeUsername, onChangePassword)
    }


    private fun clickListener() {
        //binding.buttonLogin.setOnClickListener(this)
        binding.buttonLogin.setSafeOnClickListener {
            checkValidation()
            /*firebaseLogEvent(
                this,
                APP_LOGIN,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                APP_LOGIN,
                "Login button"
            )*/

        }

        binding.includeCounter.buttonLoginCounter.setSafeOnClickListener {
            if(selectedShiftId == null) {
                toast(getString(R.string.pleaseSelectShift))
                return@setSafeOnClickListener
            }

            if(selectedCounterId == null) {
                toast(getString(R.string.pleaseSelectCounter))
                return@setSafeOnClickListener
            }




            if (isNetworkAvailable()) {
                loginViewModel.loginApi(
                    username,
                    password,
                    locale,
                    getDeviceUniqueId(this),
                    selectedShiftId,
                    selectedCounterId,
                    binding.includeCounter.etCounterBalance.text.toString()
                )
            } else {
                noNetworkToast()
            }
        }

        binding.includeCounter.tvCancel.setOnClickListener {
            hideCounterLogin()
        }

        binding.textResetPassword.setOnClickListener(this)
        binding.includeHeader.toolbarImageLeft.setOnClickListener(this)
        binding.tvChangeDomain.setOnClickListener(this)
        binding.textOneTouchLogin.setOnClickListener(this)
        binding.layoutOneTouchLogin.textCancel.setOnClickListener(this)
    }

    private fun checkValidation() {
        username = binding.edtName.text.toString()
        password = binding.edtPassword.text.toString()

        loginViewModel.validation(username, password)
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.text_reset_password -> {
                intent = Intent(this, MobileNumberEntry::class.java)
                startActivity(intent)
            }
            R.id.toolbar_image_left -> {
                //   onBackPressed()
                intent = Intent(this, DomainActivity::class.java)
                intent.putExtra(getString(R.string.CHANGE_DOMAIN), getString(R.string.YES))
                startActivity(intent)
                finish()
            }
            R.id.tv_change_domain -> {
                intent = Intent(this, DomainActivity::class.java)
                intent.putExtra(getString(R.string.CHANGE_DOMAIN), getString(R.string.YES))
                startActivity(intent)
                finish()
            }
            R.id.text_one_touch_login -> {
//                binding.layoutOneTouchLogin.root.visible()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showBiometricPrompt()
                } else {
                    notifyUser(getString(R.string.fingerprint_not_supported))
                }

                /*firebaseLogEvent(
                this,
                ONE_TOUCH_LOGIN,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                ONE_TOUCH_LOGIN,
                "One-touch Login click"
                )*/
            }
            R.id.text_cancel -> {
                binding.layoutOneTouchLogin.root.gone()
            }


        }
    }

    override fun onRightButtonClick() {
        isReLoginClick = true
        callResetApi()
    }

    override fun onLeftButtonClick() {
        isBiometricLinked = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.re_login)) {
            isReLoginClick = true
            callResetApi()
        }
    }

    private fun callResetApi() {
        val loginRequest = LoginRequest(
            bccId.toString(), format_type, reset_method_name,
            ReqBody(username, password, getString(R.string.empty), locale = locale)
        )
        if (isNetworkAvailable()) {
            // binding.includeProgress.progressBar.visible()
            if(binding.includeCounter.llCounter.isVisible) {

                if(selectedShiftId == null) {
                    toast(getString(R.string.pleaseSelectShift))
                    return
                }

                if(selectedCounterId == null) {
                    toast(getString(R.string.pleaseSelectCounter))
                    return
                }

                loginViewModel.resetApi(username,password,
                    getDeviceUniqueId(this), selectedShiftId, selectedCounterId, binding.includeCounter.etCounterBalance.text.toString().toDouble())
            } else {

                loginViewModel.resetApi(username,password,
                    getDeviceUniqueId(this))
            }
        } else
            noNetworkToast()
    }

    override fun onResume() {
        super.onResume()

        if (PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER) != null) {
            val loginModelPref = PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER)!!

            binding.edtName.setText(loginModelPref.userName)
            binding.edtPassword.setText("")
        }
    }

    private fun showBiometricPrompt() {

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
                    object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            toast("Authentication error: $errString")
                        }

                        override fun onAuthenticationSucceeded(
                            result: androidx.biometric.BiometricPrompt.AuthenticationResult
                        ) {
                            super.onAuthenticationSucceeded(result)
                            if (PreferenceUtils.getObject<LoginModel>(PREF_CURRENT_BIOMETRIC_USER) != null) {

                                val user =
                                    PreferenceUtils.getObject<LoginModel>(PREF_CURRENT_BIOMETRIC_USER)!!
                                /*val domain = PreferenceUtils.getPreference(
                                    PREF_DOMAIN,
                                    getString(R.string.empty)
                                )*/


                                //if (user.logo_url.contains(domain.toString())) {
                                if (user.linked) {
                                    binding.edtName.setText(user.userName)
                                    binding.edtPassword.setText(user.password)

                                    username = user.userName
                                    password = user.password
                                    domain = user.domainName

                                    if (isNetworkAvailable()) {
                                        updateBaseURL(user.domainName)
                                        loginViewModel.loginApi(username,password, locale,
                                            getDeviceUniqueId(this@LoginActivity))
                                        isBiometricLinked = true
                                    } else {
                                        noNetworkToast()
                                    }

                                } else {
                                    notifyUser(getString(R.string.fingerprint_no_users_found))
                                }
                                /*} else {
                                    notifyUser(getString(R.string.fingerprint_no_users_found))
                                }*/
                            } else {
                                notifyUser(getString(R.string.fingerprint_no_users_found))
                            }
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            toast("Authentication failed")
                        }
                    })

                var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo =
                    androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                        .setTitle("One-touch Login")
                        .setSubtitle("Please place your fingertip on the scanner to verify your identity")
                        .setNegativeButtonText("Cancel")
                        .build()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                toast("No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                toast("Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                /*val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                startActivityForResult(enrollIntent, 2)*/
                toast("Fingerprint is not configured")
            }
        }

    }

    private fun logFeatureSelectedEvent(
        loginId: String?,
        operatorName: String?,
        roleName: String?,
        countyCode:String?
    ) {

        firebaseAnalytics.logEvent("change_user_option") {
            param(LOGIN_ID, loginId.toString())
            param(OPERATOR_NAME, operatorName.toString())
            param(ROLE_NAME, roleName.toString())
        }

        Sentry.configureScope { scope ->
            scope.setTag(LOGIN_ID, loginId.toString())
            scope.setTag(OPERATOR_NAME, operatorName.toString())
            scope.setTag(ROLE_NAME, roleName.toString())
            scope.setTag(COUNTRY_CODE, countyCode.toString())
        }

//        firebaseAnalytics.setUserProperty("rer","")
    }

    private fun hideCounterLogin() {
        binding.includeCounter.llCounter.gone()
        binding.llLogin.visible()
        selectedShiftId = null
        selectedCounterId = null
        binding.includeCounter.acSelectShift.setText("")
        binding.includeCounter.acSelectCounter.setText("")
    }

    private fun showCounterLogin(it: LoginModel) {
        binding.includeCounter.llCounter.visible()
        binding.llLogin.gone()
        if (it.shift_list?.isNotEmpty() == true) {
            binding.includeCounter.acSelectShift.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    it.shift_list
                )
            )
            binding.includeCounter.acSelectShift.setOnItemClickListener { parent, view, position, id ->
                selectedShiftId = it.shift_list[position].id
            }
        }
        if (it.counter_list?.isNotEmpty() == true) {
            binding.includeCounter.acSelectCounter.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    it.counter_list
                )
            )
            binding.includeCounter.acSelectCounter.setOnItemClickListener { parent, view, position, id ->
                selectedCounterId = it.counter_list[position].id
            }
        }
    }

}