package com.bitla.ts.presentation.view.activity.resetpassword

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.confirm_reset_password_with_otp_method_name
import com.bitla.ts.data.format_type
import com.bitla.ts.databinding.NewLayoutSetNewPasswordBinding
import com.bitla.ts.domain.pojo.confirm_reset_password.request.ConfirmResetPasswordRequest
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.ResetPasswordViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.constants.RESET_PASSWORD
import com.bitla.ts.utils.constants.ResetPassword.REST_PASSWORD_CLICK
import com.bitla.ts.utils.sharedPref.PREF_LOGO
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.bumptech.glide.Glide
import com.google.gson.Gson
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible

class ResetPassword : BaseActivity() {
    companion object {
        val TAG = ResetPassword::class.java.simpleName
    }

    private var onChangeConfirmPassword: String = ""
    private var onChangePassword: String = ""
    private lateinit var binding: NewLayoutSetNewPasswordBinding
    private val resetPasswordViewModel by viewModel<ResetPasswordViewModel<Any?>>()

    private var bccId: String? = null
    private var apiKey: String? = null
    private var authToken: String? = null

    private var responseOtp: String? = null
    private var passKey: String? = null
    private var password: String? = null
    private var confirmPassword: String? = null
    private var logo: String? = null
    private var locale: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra(getString(R.string.otp)))
            responseOtp = intent.getStringExtra(getString(R.string.otp))
        if (intent.hasExtra(getString(R.string.passKey)))
            passKey = intent.getStringExtra(getString(R.string.passKey))
        locale = PreferenceUtils.getlang()
        binding.includeHeader.imageLogo.setImageResource(R.drawable.reset_password_img)
        binding.includeHeader.poweredby.gone()
        binding.includeHeader.bitlaImg.gone()
        binding.includeHeader.textTravelQuote.gone()
        binding.includeHeader.boldText.visible()
        binding.includeHeader.boldText.text = resources.getString(R.string.verify)

        binding.buttonSetPassword.setOnClickListener {

            password = binding.edtNewPassword.text.toString()
            confirmPassword = binding.edtNewCnfmPassword.text.toString()

            resetPasswordViewModel.validation(password!!, confirmPassword!!)

            firebaseLogEvent(
                this,
                RESET_PASSWORD,
                PreferenceUtils.getLogin().userName,
                PreferenceUtils.getLogin().travels_name,
                PreferenceUtils.getLogin().role,
                RESET_PASSWORD,
                REST_PASSWORD_CLICK
            )
        }

        binding.edtNewPassword.onChange {
            onChangeConfirmPassword = it
            listenTextWatcher()
        }

        binding.edtNewCnfmPassword.onChange {
            onChangePassword = it
            listenTextWatcher()
        }

        binding.includeHeader.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        if (PreferenceUtils.getPreference(PREF_LOGO, getString(R.string.empty)) != null)
            logo = PreferenceUtils.getPreference(PREF_LOGO, getString(R.string.empty))

        if (logo != null) {
            Glide.with(this)
                .load(logo)
                .override(600, 200)
                .fitCenter()
                .into(binding.includeHeader.imgLogo)
        }

        lifecycleScope.launch {
            resetPasswordViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }


    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun listenTextWatcher() {
        if (onChangeConfirmPassword.isNotEmpty() && onChangePassword.isNotEmpty() && onChangePassword == onChangeConfirmPassword) {
            binding.buttonSetPassword.setBackgroundResource(R.drawable.button_selected_bg)
            binding.buttonSetPassword.isClickable = true
            binding.buttonSetPassword.isEnabled = true
        } else {
            binding.buttonSetPassword.setBackgroundResource(R.drawable.button_default_bg)
            binding.buttonSetPassword.isClickable = true
            binding.buttonSetPassword.isEnabled = true
        }

    }

    override fun initUI() {
        binding = NewLayoutSetNewPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        getPref()
        setUpObserver()

        binding.includeHeader.layoutOtpVerification.visible()
        binding.includeHeader.imgOtpLogo.setBackgroundResource(R.drawable.icon_verification_completed)
    }

    private fun setUpObserver() {
        try {
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

            resetPasswordViewModel.confirmResetPassword.observe(this, Observer {
                binding.includeProgress.progressBar.gone()
                Timber.d("responseBody confirmResetPassword $it")

                if (it != null) {
                    if (it.code == 200) {
                        toast(it.message)
                        intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else
                        if (it.result?.message != null) {
                            it.result.message.let { it1 ->
                                toast(it1)
                            }
                        }
                } else {
                    toast(getString(R.string.server_error))
                }
            }
            )


            resetPasswordViewModel.validationData.observe(this, Observer {
                if (it.isNotEmpty())
                    toast(it)
                else {
                    if (isNetworkAvailable()) {
                        val reqBody =
                            com.bitla.ts.domain.pojo.confirm_reset_password.request.ReqBody(
                                api_key = apiKey!!,
                                password!!,
                                passKey!!,
                                confirmPassword!!,
                                otp = responseOtp!!,
                                locale = locale
                            )
                        val confirmResetPasswordRequest =
                            ConfirmResetPasswordRequest(
                                bccId!!, format_type,
                                confirm_reset_password_with_otp_method_name,
                                reqBody
                            )

                        /* resetPasswordViewModel.confirmResetPasswordApi(
                             authToken!!,
                             apiKey!!,
                             confirmResetPasswordRequest,
                             confirm_reset_password_with_otp_method_name
                         ) */

                        resetPasswordViewModel.confirmResetPasswordApi(
                            reqBody,
                            confirm_reset_password_with_otp_method_name
                        )

                        Timber.d(
                            " confirmResetPasswordRequest ${
                                Gson().toJson(
                                    confirmResetPasswordRequest
                                )
                            }"
                        )
                    } else
                        noNetworkToast()
                }
            })
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId().toString()
        if (PreferenceUtils.getString(getString(R.string.API_KEY)) != null) {
            apiKey = PreferenceUtils.getString(getString(R.string.API_KEY))
        }
        if (PreferenceUtils.getString(getString(R.string.AUTH_TOKEN)) != null) {
            authToken = PreferenceUtils.getString(getString(R.string.AUTH_TOKEN))
        }
    }

}
