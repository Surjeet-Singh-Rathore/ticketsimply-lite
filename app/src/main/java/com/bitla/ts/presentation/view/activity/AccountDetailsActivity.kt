package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.os.Build
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.account_info.request.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast

class AccountDetailsActivity : BaseActivity() {

    companion object {
        val TAG: String = AccountDetailsActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityAccountDetailsBinding
    private val agentAccountInfoViewModel by viewModel<AgentAccountInfoViewModel<Any>>()
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var loginModelPref: LoginModel = LoginModel()
    private var bccId: Int? = 0
    private var locale: String? = ""


    override fun initUI() {

        binding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()
        binding.toolbar.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.toolbar.tvCurrentHeader.text = getString(R.string.account_details)
        callAgentAccountInfo()
        setUpObserver()
        lifecycleScope.launch {
            agentAccountInfoViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                  showToast(it)
                }
            }
        }
    }

    private fun getPref() {
        privilegeResponseModel = getPrivilegeBase()
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObserver() {
        agentAccountInfoViewModel.agentInfo.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        try {
                            if (privilegeResponseModel != null) {
                                val availableBalance = it.available_balance
                                val creditLimit = it.credit_limit
                                val lastRechargeAmount = it.last_recharge_amount
                                val lastRechargeOn = it.last_recharged_on
                                if (availableBalance.isNotEmpty()) {

                                    if (privilegeResponseModel?.isAgentLogin == true && !privilegeResponseModel?.country.equals(
                                            "india",
                                            true
                                        )
                                    ) {
                                        binding.balanceAmount.text =
                                            "${privilegeResponseModel?.currency} ${
                                                availableBalance.toDouble()
                                                    .convert(privilegeResponseModel?.currencyFormat ?: "")
                                            }"

                                        if(creditLimit == "Nil") {
                                            binding.creditAmount.text = creditLimit
                                        } else {
                                            binding.creditAmount.text =
                                                "${privilegeResponseModel?.currency} ${
                                                    creditLimit.toDouble()
                                                        .convert(privilegeResponseModel?.currencyFormat ?: "")
                                                }"
                                        }


                                        binding.lastRechargeAmount.text =
                                            "${privilegeResponseModel?.currency} ${
                                                lastRechargeAmount.toDouble()
                                                    .convert(privilegeResponseModel?.currencyFormat ?: "")
                                            }"

                                        binding.lastTransactionTime.text = lastRechargeOn

                                    }
                                }
                            } else {
                                toast(getString(R.string.server_error))
                            }

                        } catch (e: Exception) {
                            toast(getString(R.string.server_error))
                            Timber.d("Error in AccountDetailsActivity agentInfoObserver ${e.message}")
                        }
                    }

                    else -> {
                        it.result.message?.let { it1 -> toast(it1) }
                    }
                }
            } else {
                binding.balanceDetailsCard.gone()
                toast(getString(R.string.server_error))
            }
        }

    }

    override fun isInternetOnCallApisAndInitUI() {
        callAgentAccountInfo()
    }

    private fun callAgentAccountInfo() {

        val agentRequest = AgentAccountInfoRequest(
            bccId.toString(),
            format_type,
            agent_account_info,
            ReqBody(
                loginModelPref.api_key,
                locale = locale
            )
        )
        agentAccountInfoViewModel.agentAccountInfoAPI(
            agentRequest,"","",
            agent_account_info
        )
    }

}