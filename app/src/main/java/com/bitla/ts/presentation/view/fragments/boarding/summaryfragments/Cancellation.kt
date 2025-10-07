package com.bitla.ts.presentation.view.fragments.boarding.summaryfragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.cancellation_policies_service_summary_method_name
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.service_details_method
import com.bitla.ts.databinding.FragmentCancellationBinding
import com.bitla.ts.domain.pojo.cancellation_policies_service_summary.request.CancellationPoliciesServiceSummaryRequest
import com.bitla.ts.domain.pojo.cancellation_policies_service_summary.request.ReqBody
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.constants.CANCELLATION
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible


class Cancellation : Fragment(), DialogSingleButtonListener {
    private lateinit var binding: FragmentCancellationBinding
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var locale: String? = ""
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCancellationBinding.inflate(inflater, container, false)
//            initTab()
//        binding.headerText.text= "Select Boarding point"
        getPref()
        firebaseLogEvent(
        requireContext(),
        CANCELLATION,
        loginModelPref.userName,
        loginModelPref.travels_name,
        loginModelPref.role,
        CANCELLATION,
        "Cancellation"
        )
        callCancellationPoliciesServiceSummaryApi()
        cancellationPoliciesServiceSummaryApiObserver()
        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        return binding.root
    }

    private fun getPref() {
        locale = PreferenceUtils.getlang()
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
    }

    private fun callCancellationPoliciesServiceSummaryApi() {
//        val cancellationPoliciesServiceSummaryRequest = CancellationPoliciesServiceSummaryRequest(
//            bccId = bccId.toString(),
//            format = "json",
//            methodName = cancellation_policies_service_summary_method_name,
//            reqBody = ReqBody(
//                apiKey = loginModelPref.api_key.toString(),
//                isFromMiddleTier = true,
//                responseFormat = "true",
//                locale = locale
//
//            )
//        )
        if (requireContext().isNetworkAvailable()) {
           /* sharedViewModel.cancellationPoliciesServiceSummmary(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                cancellationPoliciesServiceSummaryRequest,
                service_details_method
            )*/
            sharedViewModel.cancellationPoliciesServiceSummmary(
                loginModelPref.api_key,
                locale!!,
                true,
                service_details_method
            )
        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun cancellationPoliciesServiceSummaryApiObserver() {
        sharedViewModel.cancellationPoliciesServiceSummary.observe(viewLifecycleOwner) {

            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            binding.svCancellationPolicies.visible()
                            binding.layoutNoData.root.gone()
                            var data = ""
                            it.result.forEach {
                                data += "Between, ${it.timeLimitFrom} to ${it.timeLimitTo} from the station departure time: ${it.percent}% \n \n"
                            }
                            binding.textview.text = data
                        }
                        401 -> {
                            /*DialogUtils.unAuthorizedDialog(
                                requireContext(),
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            (activity as BaseActivity).showUnauthorisedDialog()

                        }
                        501 -> {
                            binding.svCancellationPolicies.gone()
                            binding.layoutNoData.root.visible()
                            if (it.message != null)
                                requireActivity().toast(it.message)
                        }
                        else -> {
                            binding.svCancellationPolicies.gone()
                            binding.layoutNoData.root.visible()
                            if (it.message != null)
                                requireActivity().toast(it.message)
                        }
                    }

                } else {
                    binding.svCancellationPolicies.gone()
                    binding.layoutNoData.root.visible()
                    requireContext().toast("Enable Cancellation Policies Privilege")
                }
            } catch (e: Exception) {
                binding.svCancellationPolicies.gone()
                binding.layoutNoData.root.visible()
                e.message?.let { it1 -> requireActivity().toast(it1) }
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}