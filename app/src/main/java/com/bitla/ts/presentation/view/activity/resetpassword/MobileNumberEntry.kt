package com.bitla.ts.presentation.view.activity.resetpassword

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.format_type
import com.bitla.ts.data.reset_password_with_otp_method_name
import com.bitla.ts.databinding.NewLayoutMobileEnterBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.reset_password_with_otp.request.ReqBody
import com.bitla.ts.domain.pojo.reset_password_with_otp.request.ResetPasswordRequest
import com.bitla.ts.presentation.viewModel.ResetPasswordViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getCountryCodes
import com.bitla.ts.utils.sharedPref.PREF_LOCALE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible

class MobileNumberEntry : BaseActivity() {
    companion object {
        val TAG = MobileNumberEntry::class.java.simpleName
    }

    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var bccId: String? = null
    private var apiKey: String? = null
    private var loginModelPref = LoginModel()
    private lateinit var binding: NewLayoutMobileEnterBinding

    private val resetPasswordViewModel by viewModel<ResetPasswordViewModel<Any?>>()
    private var countryList = ArrayList<Int>()
    private var locale: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locale = PreferenceUtils.getlang()
        val privilegePhoneValidationCount = PreferenceUtils.getPreference(getString(R.string.mobile_number_length), 0).toString().toInt()

        if (countryList.isNotEmpty())
            binding.aeCountryCode.setText(countryList[0].toString())

        binding.includeHeader.poweredby.gone()
        binding.includeHeader.layoutTravelLogoView.gone()
        binding.buttonSendOtp.setOnClickListener {
            if (binding.aeCountryCode.text.isNullOrEmpty()) {
                toast(getString(R.string.validate_country_code))
            } else if (binding.edtMobileNumber.text.isNullOrEmpty()
                || binding.edtMobileNumber.text.toString().length < 9
            ) {
                toast(getString(R.string.validate_mobile_number))
            } else {
                val mobileNumber = binding.edtMobileNumber.text.toString()

                if (isNetworkAvailable()) {
                    val reqBody = ReqBody(mobileNumber, locale = locale)
                    val resetPasswordRequest =
                        ResetPasswordRequest(
                            bccId!!,
                            format_type,
                            reset_password_with_otp_method_name,
                            reqBody
                        )

                    /*resetPasswordViewModel.resetPasswordWithOtpApi(
                        loginModelPref.auth_token,
                        loginModelPref.api_key,
                        resetPasswordRequest,
                        reset_password_with_otp_method_name
                    ) */

                    resetPasswordViewModel.resetPasswordWithOtpApi(
                        reqBody,
                        reset_password_with_otp_method_name
                    )

                } else
                    noNetworkToast()

            }
        }

        binding.includeHeader.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        binding.edtMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {


                if (binding.edtMobileNumber.text.toString().length >= 9) {
                    binding.buttonSendOtp.setBackgroundResource(R.drawable.button_selected_bg)
                    binding.buttonSendOtp.isClickable = true
                    binding.buttonSendOtp.isEnabled = true
                } else {
                    binding.buttonSendOtp.setBackgroundResource(R.drawable.button_default_bg)
                    binding.buttonSendOtp.isClickable = false
                    binding.buttonSendOtp.isEnabled = false
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })


    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        if (getPrivilegeBase() != null)
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
    }


    override fun initUI() {
        binding = NewLayoutMobileEnterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        getPref()
        setUpObserver()

        binding.includeHeader.imageLogo.visible()
        binding.includeHeader.imageLogo.setImageDrawable(getDrawable(R.drawable.header_logo_))


        if (getCountryCodes().isNotEmpty())
            countryList = getCountryCodes()
        else
            countryList.add(91)


        binding.aeCountryCode.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                countryList
            )
        )
        binding.aeCountryCode.setOnClickListener {
            binding.aeCountryCode.showDropDown()
        }
        binding.includeHeader.layoutTravelLogoView.gone()
//        if (PreferenceUtils.getString(PREF_LOGO) != null)
//            Glide.with(this).load(PreferenceUtils.getString(PREF_LOGO)).into(binding.includeHeader.imgLogo)

        lifecycleScope.launch {
            resetPasswordViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
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
            if (it != null) {
                if (it.code == 200) {
                    PreferenceUtils.putString(getString(R.string.API_KEY), it.api_key)
                    PreferenceUtils.putString(getString(R.string.AUTH_TOKEN), it.auth_token)
                    intent = Intent(this, OtpEntry::class.java)
                    intent.putExtra("MOBILENUMBER", binding.edtMobileNumber.text.toString())
                    intent.putExtra(getString(R.string.otp), it.otp)
                    intent.putExtra(
                        getString(R.string.countryCode),
                        "${binding.aeCountryCode.text}"
                    )
                    intent.putExtra(getString(R.string.passKey), it.key)
                    startActivity(intent)
                } else {
                    if (it.result?.message != null) {
                        toast(it.result.message)
                    }
                }
            }
        })
    }

}
