package com.bitla.ts.presentation.view.fragments.boarding.summaryfragments

import android.annotation.*
import android.content.*
import android.os.*
import android.view.*
import androidx.fragment.app.*
import androidx.lifecycle.*
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.service_summary.request.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible


class Summary : Fragment(), DialogSingleButtonListener {

    private lateinit var binding: FragmentSummaryBinding
    private var loginModelPref: LoginModel = LoginModel()
    var reservationId: Long = 0L
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var bccId: Int? = 0


    private var totalAvailableSeats = 0
    private var totalSeatsSummary = 0
    private var totalBookedSeats = 0
    private var totalPhoneBlockedSeat = 0
    private var totalQuotaBlockedSeat = 0
    private var totalApi = 0
    private var totalUserConf = 0
    private var totalBranchConf = 0
    private var totalOnlineAgent = 0
    private var totalOfflineAgent = 0
    private var totalOnline = 0
    private var totalQuota = 0
    private var totalETticket = 0
    private var totalLadiesQuota = 0
    private var totalGentsQuota = 0
    private var totalInTourney = 0
    private var totalExtraSeats = 0
    private var locale: String? = ""
    
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
//            initTab()
//        binding.headerText.text= "Select Boarding point"
        getPref()
        observer()
        callCityDetailsApi()
        binding.progressCircularSummary.visible()

        firebaseLogEvent(
        requireContext(),
        SUMMARY,
        loginModelPref.userName,
        loginModelPref.travels_name,
        loginModelPref.role,
        SUMMARY,
        "summary"
        )

        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }

        return binding.root

    }

    private fun callCityDetailsApi() {
        if (requireContext().isNetworkAvailable()) {
            val serviceSummary = ServiceSummaryRequest(
                bccId.toString(),
                format_type,
                service_summary_method_name,

                ReqBody(
                    loginModelPref.api_key,
                    reservationId.toString(),
                    response_format,
                    locale = locale
                )
            )
          /*  sharedViewModel.serviceSummaryApi(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                serviceSummary,
                service_summary_method_name
            )*/

            sharedViewModel.serviceSummaryApi(
                loginModelPref.api_key,
                locale!!,
                reservationId.toString(),
                true,
                service_summary_method_name
            )
        } else requireContext().noNetworkToast()
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        sharedViewModel.serviceSummary.observe(requireActivity(), Observer {
            Timber.d("responseBodyServiceSummary $it")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        binding.progressCircularSummary.gone()
                        binding.mainLayoutSummary.visible()
                        Timber.d("servicesummary: ${it.result?.view_summary}")
                        if (it.result?.view_summary?.total_available_seats != null) {
                            totalAvailableSeats = it.result.view_summary.total_available_seats
                        }
                        if (it.result?.view_summary?.total_seats != null) {
                            totalSeatsSummary = it.result.view_summary.total_seats
                        }
                        if (it.result?.view_summary?.total_booked_seats != null) {
                            totalBookedSeats = it.result.view_summary.total_booked_seats
                        }
                        if (it.result?.view_summary?.total_phone_blocked_seat != null) {
                            totalPhoneBlockedSeat =
                                it.result.view_summary.total_phone_blocked_seat
                        }
                        if (it.result?.view_summary?.total_quota_blocked_seat != null) {
                            totalQuotaBlockedSeat =
                                it.result.view_summary.total_quota_blocked_seat
                        }
                        if (it.result?.view_summary?.total_api != null) {
                            totalApi = it.result.view_summary.total_api
                        }
                        if (it.result?.view_summary?.total_user_conf != null) {
                            totalUserConf = it.result.view_summary.total_user_conf
                        }

                        if (it.result?.view_summary?.total_branch_conf != null) {
                            totalBranchConf = it.result.view_summary.total_branch_conf
                        }
                        if (it.result?.view_summary?.total_online_agent != null) {
                            totalOnlineAgent = it.result.view_summary.total_online_agent
                        }
                        if (it.result?.view_summary?.total_offline_agent != null) {
                            totalOfflineAgent = it.result.view_summary.total_offline_agent
                        }
                        if (it.result?.view_summary?.total_online != null) {
                            totalOnline = it.result.view_summary.total_online
                        }
                        if (it.result?.view_summary?.total_quota != null) {
                            totalQuota = it.result.view_summary.total_quota
                        }
                        if (it.result?.view_summary?.total_e_ticket != null) {
                            totalETticket = it.result.view_summary.total_e_ticket
                        }
                        if (it.result?.view_summary?.total_ladies_quota != null) {
                            totalLadiesQuota = it.result.view_summary.total_ladies_quota
                        }
                        if (it.result?.view_summary?.total_gents_quota != null) {
                            totalGentsQuota = it.result.view_summary.total_gents_quota
                        }
                        if (it.result?.view_summary?.total_in_journey != null) {
                            totalInTourney = it.result.view_summary.total_e_ticket!!
                        }
                        if (it.result?.view_summary?.total_extra_seats != null) {
                            totalExtraSeats = it.result.view_summary.total_extra_seats
                        }

                        binding.api.text = ("${getString(R.string.summary_api)} $totalApi")
                        binding.userConfig.text = "${getString(R.string.summary_user_cnf)}$totalUserConf"
                        binding.branchConfig.text = "${getString(R.string.summary_branch_cnf)}$totalBranchConf"
                        binding.onlineAgent.text = "${getString(R.string.summary_online_agent)} $totalOnlineAgent"
                        binding.offlineAgent.text = "${getString(R.string.summary_offline_agent)} $totalOfflineAgent"
                        binding.online.text = "${getString(R.string.summary_online)} $totalOnline"
                        binding.quota.text = "${getString(R.string.summary_quota)} $totalQuota"
                        binding.eTicket.text = "${getString(R.string.summary_e_ticket)} $totalETticket"
                        binding.ladiesQuota.text = "${getString(R.string.summary_ladies_quota)}$totalLadiesQuota"
                        binding.gentsQuota.text = "${getString(R.string.summary_gents_quota)} $totalGentsQuota"
                        binding.injourny.text = "${getString(R.string.summary_in_journey)} $totalInTourney"
                        binding.extraSeatsBooked.text = "${totalExtraSeats} ${getString(R.string.summary_extra_seat_booked)}"
                        binding.totalPhone.text =
                            "${getString(R.string.phone)}- $totalPhoneBlockedSeat"
                        
                        binding.totalAvailable.text = "${totalAvailableSeats} ${getString(R.string.summary_seat_available)}"
                        binding.totalSeats.text = "$totalSeatsSummary"
                        binding.totalBookedSeats.text = "$totalBookedSeats"
                        Timber.d("serciveSummaryfinal: ${it.result?.view_summary}")
                    }
                    401 -> {
                       /* DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    else -> it.message?.let { it1 -> Timber.d( it1) }
                }
            } else {
                binding.progressCircularSummary.gone()
                requireContext().toast(getString(R.string.server_error))
            }
        })
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
        reservationId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!

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