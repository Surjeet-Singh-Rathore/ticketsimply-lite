package com.bitla.ts.presentation.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.db.UserViewModel
import com.bitla.ts.data.listener.DialogButtonListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.NewLayoutOtpVerificationBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.login_with_otp.request.ReqBody
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.viewModel.LoginViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getDeviceUniqueId
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.security.EncrypDecryp
import com.bitla.ts.utils.sharedPref.IS_LOGOUT_VIA_AUTH_FAIL
import com.bitla.ts.utils.sharedPref.PREF_DOMAIN
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import dagger.hilt.android.AndroidEntryPoint
import gone
import isNetworkAvailable
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.viewModel
import toUserModel
import toast
import visible
import java.text.DecimalFormat

@AndroidEntryPoint
class OtpVerification : BaseActivity(), DialogButtonListener, DialogSingleButtonListener {
    companion object {
        val TAG = OtpVerification::class.java.simpleName
    }

    private var isReLoginClick: Boolean = false
    private val loginViewModel by viewModel<LoginViewModel>()
    private var domain: String? = ""
    private var username: String = ""
    private var password: String = ""
    private var key: String = ""
    private var mobileNumber: String = ""
    private var OTP: String = ""
    private lateinit var binding: NewLayoutOtpVerificationBinding
    private var locale: String? = ""
    private val userViewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.hasExtra(getString(R.string.username))
        username = intent.getStringExtra(getString(R.string.username))!!

        intent.hasExtra(getString(R.string.password))
        password = intent.getStringExtra(getString(R.string.password))!!

        intent.hasExtra("KEY")
        key = intent.getStringExtra("KEY")!!

        intent.hasExtra("MOBILE")
        mobileNumber = intent.getStringExtra("MOBILE")!!

        locale = PreferenceUtils.getlang()

        if (PreferenceUtils.getPreference(PREF_DOMAIN, "") != null)
            domain = PreferenceUtils.getPreference(PREF_DOMAIN, "")!!

//        edt_domain.setText(domain)
        otpCountDown()

        loginViewModel.dataLoginWithOtp.observe(this) {
            // Populate the UI
            when (it.code) {
                200 -> {
                    navigateToDashboard(it)
                }
                399 -> {
                    DialogUtils.twoButtonDialog(
                        this, getString(R.string.use_here),
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
                        false,
                        deviceId = getDeviceUniqueId(this),
                        this
                    )
                }
                else -> {
                    it.result.message?.let { it1 -> toast(it1) }
                }
            }
        }


        loginViewModel.data.observe(this) {
            // Populate the UI
            if (it.code == 200) {
                //toast(it.otp)
                val loginModel = it as LoginModel
                loginModel.userName = username
                loginModel.password = password
                key = it.key
                PreferenceUtils.removeKey(IS_LOGOUT_VIA_AUTH_FAIL)


            } else if (it.code == 399) {
                DialogUtils.twoButtonDialog(
                    this,
                    getString(R.string.use_here),
                    getString(R.string.already_logged_in),
                    getString(R.string.cancel),
                    getString(R.string.use_here2),
                    this
                )
            } else if (it.code == 411 && isReLoginClick) {
                DialogUtils.deviceRegistrationDialog(
                    this,
                    it.result.message ?: "",
                    false,
                    deviceId = getDeviceUniqueId(this),
                    this
                )
            } else {
                it.result.message?.let { it1 -> toast(it1) }
            }
            isReLoginClick = false
        }

        loginViewModel.dataLogout.observe(this) {
            if (it.code == 200) {
                it.otp = OTP
                it.key = key
                navigateToDashboard(it)
                PreferenceUtils.removeKey(IS_LOGOUT_VIA_AUTH_FAIL)

            } else {
                it.result.message?.let { it1 -> toast(message = it1) }
            }
        }

        loginViewModel.validationData.observe(this) {
            if (it.isNotEmpty())
                toast(it)
            else {
                if (isNetworkAvailable()) {
                    binding.includeProgress.progressBar.visible()
                    val reqBody = ReqBody(mobileNumber, key, OTP, locale = locale, getDeviceUniqueId(this))

                    loginViewModel.confirmOTP(
                        reqBody
                    )
                } else {
                    noNetworkToast()
                }
            }
        }

        loginViewModel.loadingState.observe(this) {
            // Observe the loading state
            binding.includeProgress.progressBar.gone()
        }

    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun ontextChangeListener() {
        binding.editOtpOne.onChange {
            if (!it.isNullOrEmpty() && it.length == 1) {
                binding.editOtpTwo.requestFocus()
            }
            validateOtp()
        }
        binding.editOtpTwo.onChange {
            if (!it.isNullOrEmpty() && it.length == 1) {
                binding.editOtpThree.requestFocus()
            } else {
                binding.editOtpOne.requestFocus()
            }
            validateOtp()
        }
        binding.editOtpThree.onChange {
            if (!it.isNullOrEmpty() && it.length == 1) {
                binding.editOtpFour.requestFocus()
            } else {
                binding.editOtpTwo.requestFocus()
            }
            validateOtp()
        }
        binding.editOtpFour.onChange {
            if (!it.isNullOrEmpty() && it.length == 1) {
                binding.editOtpFive.requestFocus()
            } else {
                binding.editOtpThree.requestFocus()
            }
            validateOtp()
        }
        binding.editOtpFive.onChange {
            if (!it.isNullOrEmpty() && it.length == 1) {
                binding.editOtpSix.requestFocus()
            } else {
                binding.editOtpFour.requestFocus()
            }
            validateOtp()
        }
        binding.editOtpSix.onChange {
            if (!it.isNullOrEmpty() && it.length == 1) {
            } else {
                binding.editOtpFive.requestFocus()
            }
            validateOtp()
        }
    }


    private fun navigateToDashboard(it: LoginModel) {
        val loginModel = it
        it.domainName = PreferenceUtils.getPreference(PREF_DOMAIN, getString(R.string.empty))!!
        loginModel.userName = username
        loginModel.password = password
        loginModel.phone_number = mobileNumber
        loginModel.isEncryptionEnabled = EncrypDecryp.isEncrypted()
        PreferenceUtils.setSubAgentRole(it.is_sub_agent_and_user)

        PreferenceUtils.setPreference(PREF_DOMAIN, domain)

        PreferenceUtils.putString(PREF_IS_USER_LOGIN, "true")

        userViewModel.insertUser(loginModel.toUserModel())

        intent = Intent(this, DashboardNavigateActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun initUI() {
        // toolbar_header_text.text = getString(R.string.enter_otp_login)
        // text_otp_mobile_number.text = "OTP send to ${intent.getStringExtra("MOBILE")}"
        binding = NewLayoutOtpVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        binding.includeHeader.imageLogo.visible()
        binding.textPhone.text = mobileNumber

        binding.includeHeader.imgLogo.visible()
        binding.includeHeader.imgLogo.setBackgroundResource(R.drawable.icon_verification_in_process)
        clickListener()
        ontextChangeListener()
    }

    private fun clickListener() {
        binding.includeHeader.toolbarImageLeft.setOnClickListener(this)
        binding.buttonVerifyOtp.setOnClickListener(this)
        binding.textResendOtp.setOnClickListener(this)
    }

    private fun validateOtp() {
        OTP =
            "${binding.editOtpOne.text.toString()}${binding.editOtpTwo.text.toString()}${binding.editOtpThree.text.toString()}${binding.editOtpFour.text.toString()}${binding.editOtpFive.text.toString()}${binding.editOtpSix.text.toString()}"
        if (OTP.length == 6) {
            binding.buttonVerifyOtp.setBackgroundResource(R.drawable.button_selected_bg)
            binding.buttonVerifyOtp.isClickable = true
            binding.buttonVerifyOtp.isEnabled = true
        } else {
            binding.buttonVerifyOtp.setBackgroundResource(R.drawable.button_default_bg)
            binding.buttonVerifyOtp.isClickable = false
            binding.buttonVerifyOtp.isEnabled = false
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.toolbar_image_left -> {
                onBackPressed()
            }

            R.id.button_verify_otp -> {
                if (OTP.length == 6) {
                    loginViewModel.validationOTP(OTP)
                } else {
                    toast("Please enter valid OTP")
                }
            }

            R.id.text_resend_otp -> {

                if (isNetworkAvailable()) {
                    loginViewModel.loginApi(username,password, locale, getDeviceUniqueId(this))
                } else
                    noNetworkToast()

                otpCountDown()
            }
        }
    }

    private fun otpCountDown() {
        if (binding.textResendOtp.text == getString(R.string.resend_otp)) {

            binding.includeProgress.progressBar.visible()

            object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Used for formatting digit to be in 2 digits only
                    val f = DecimalFormat("00")
                    val sec = millisUntilFinished / 1000 % 60
                    binding.textResendOtp.text = "Resend OTP in ${f.format(sec)} sec"
                }

                override fun onFinish() {
                    binding.textResendOtp.text = getString(R.string.resend_otp)
                }
            }.start()
        }
    }

    override fun onRightButtonClick() {
        //popup reset button
        binding.includeProgress.progressBar.visible()
        isReLoginClick = true
        callResetApi()
    }

    private fun callResetApi() {
        if (isNetworkAvailable())
            loginViewModel.resetApi(username,password, getDeviceUniqueId(this))
        else
            noNetworkToast()
    }

    override fun onLeftButtonClick() {
        //popup cancel button
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.re_login)) {
            isReLoginClick = true
            callResetApi()
        }
    }

}