package com.bitla.ts.presentation.view.activity.resetpassword

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.format_type
import com.bitla.ts.data.reset_password_with_otp_method_name
import com.bitla.ts.databinding.NewLayoutOtpVerificationBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.reset_password_with_otp.request.ReqBody
import com.bitla.ts.domain.pojo.reset_password_with_otp.request.ResetPasswordRequest
import com.bitla.ts.presentation.viewModel.ResetPasswordViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.sharedPref.PREF_LOCALE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible
import java.text.DecimalFormat

class OtpEntry : BaseActivity() {
    private var responseOtp: String? = null
    private var countryCode: String? = null
    private var mobileNumber: String? = null
    private var passKey: String? = null
    private var OTP: String = ""

    companion object {
        val TAG = OtpEntry::class.java.simpleName
    }

    private lateinit var binding: NewLayoutOtpVerificationBinding
    private val resetPasswordViewModel by viewModel<ResetPasswordViewModel<Any?>>()

    private var bccId: String? = null
    private var apiKey: String? = null
    private var loginModelPref = LoginModel()
    private var locale: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.hasExtra("MOBILENUMBER")
        if (intent.hasExtra(getString(R.string.otp)))
            responseOtp = intent.getStringExtra(getString(R.string.otp))
        if (intent.hasExtra(getString(R.string.countryCode)))
            countryCode = intent.getStringExtra(getString(R.string.countryCode))
        if (intent.hasExtra(getString(R.string.passKey)))
            passKey = intent.getStringExtra(getString(R.string.passKey))
        if (intent.hasExtra("MOBILENUMBER"))
            mobileNumber = intent.getStringExtra("MOBILENUMBER")

        locale = PreferenceUtils.getlang()
        binding.textPhone.text = "$countryCode${mobileNumber}"

        binding.buttonVerifyOtp.setOnClickListener {
            if (OTP.length < 6) {
                toast(getString(R.string.validOtp))
            } else if (responseOtp != null && responseOtp != OTP) {
                toast(getString(R.string.validOtp))
            } else {
                // toast(getString(R.string.validOtp))

                intent = Intent(this, ResetPassword::class.java)
                intent.putExtra(getString(R.string.otp), responseOtp)
                intent.putExtra(getString(R.string.passKey), passKey)
                startActivity(intent)
            }
        }

        binding.includeHeader.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        binding.textEditPhone.setOnClickListener {
            onBackPressed()
        }

        binding.textResendOtp.setOnClickListener {

            if (isNetworkAvailable()) {
                val reqBody = mobileNumber?.let { it1 -> ReqBody(it1, locale = locale) }
                val resetPasswordRequest =
                    reqBody?.let { it1 ->
                        ResetPasswordRequest(
                            bccId!!, format_type, reset_password_with_otp_method_name,
                            it1
                        )
                    }

                /*resetPasswordViewModel.resetPasswordWithOtpApi(
                    loginModelPref.auth_token,
                    loginModelPref.api_key,
                    resetPasswordRequest!!,
                    reset_password_with_otp_method_name
                )*/

                resetPasswordViewModel.resetPasswordWithOtpApi(
                    reqBody!!,
                    reset_password_with_otp_method_name
                )

            } else
                noNetworkToast()

            otpCountDown()
        }

        lifecycleScope.launch {
            resetPasswordViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }

        ontextChangeListener()
        otpCountDown()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun setUpObserver() {
        resetPasswordViewModel.loadingState.observe(this, Observer {
            when (it) {
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> {
                    it.msg?.let { it1 -> toast(it1) }
                    binding.includeProgress.progressBar.gone()
                }
            }

        })

        resetPasswordViewModel.resetPasswordWithOtp.observe(this, Observer {
            binding.includeProgress.progressBar.gone()
            if (it.code == 200) {
            } else
                it.result?.message?.let { it1 -> toast(it1) }
        })
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

    fun validateOtp() {
        OTP =
            "${binding.editOtpOne.text.toString()}${binding.editOtpTwo.text.toString()}${binding.editOtpThree.text.toString()}${binding.editOtpFour.text.toString()}${binding.editOtpFive.text.toString()}${binding.editOtpSix.text.toString()}"
        if (OTP.length == 6) {
            binding.buttonVerifyOtp.setBackgroundResource(R.drawable.button_selected_bg)
            binding.buttonVerifyOtp.isClickable = true
            binding.buttonVerifyOtp.isEnabled = true
            binding.layoutEditPhone.gone()
            binding.textResendOtp.gone()
        } else {
            binding.buttonVerifyOtp.setBackgroundResource(R.drawable.button_default_bg)
            binding.buttonVerifyOtp.isClickable = false
            binding.buttonVerifyOtp.isEnabled = false
            binding.layoutEditPhone.visible()
            binding.textResendOtp.visible()
        }
    }

    /* override fun getLayout(): Int {
         return R.layout.new_layout_otp_verification
     }*/

    override fun initUI() {
        binding = NewLayoutOtpVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        getPref()
        setUpObserver()

        binding.textEditPhone.visible()
        binding.includeHeader.imgLogo.setBackgroundResource(R.drawable.icon_verification_in_process)
        binding.includeHeader.imgLogo.gone()
        binding.includeHeader.poweredby.gone()
        binding.includeHeader.imageLogo.visible()
        binding.includeHeader.imageLogo.setBackgroundResource(R.drawable.icon_verification_in_process)

    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
    }

    private fun otpCountDown() {
        if (binding.textResendOtp.text == getString(R.string.resend_otp)) {
            object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Used for formatting digit to be in 2 digits only
                    val f = DecimalFormat("00")
                    val min = millisUntilFinished / 60000 % 60
                    val sec = millisUntilFinished / 1000 % 60
                    binding.textResendOtp.text = "Resend OTP in ${f.format(sec)} sec"
                }

                override fun onFinish() {
                    binding.textResendOtp.text = getString(R.string.resend_otp)
                }
            }.start()
        }
    }


}
