package com.bitla.ts.presentation.view.dashboard

import android.annotation.*
import android.app.*
import android.content.*
import android.graphics.*
import android.os.*
import android.view.*
import androidx.core.content.*
import androidx.core.view.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.*
import androidx.transition.*
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.*
import com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.*
import com.bitla.ts.domain.pojo.dashboard_model.response.*
import com.bitla.ts.domain.pojo.dashboard_model.response.MostSearched
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.github.mikephil.charting.animation.*
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.*
import com.google.firebase.analytics.*
import gone
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.time.*

class DashboardFragment : BaseFragment(), OnItemClickListener, OnPnrListener, View.OnClickListener,
    DialogSingleButtonListener, DialogReturnDialogInstanceListener {

//    private var privilegeResponse: PrivilegeResponseModel? = null
    private var currency: String? = null
    private var currencyFormat: String = ""
    private var phoneBookedValue: String = ""
    private var quotaBlocked: String = ""
    private var cancelled: String = ""
    private var booked: String = ""
    private var payableAmount = ""
    lateinit var binding: FragmentDashboardBinding
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any?>>()
    private var dashboardPendingApiTicketsAdapter: DashboardPendingApiTicketsAdapter? = null
    private var dashboardPendingETicketsAdapter: DashboardPendingETicketsAdapter? = null
    private var mostSearchDataAdapter: MostSearchDataAdapter? = null
    private var searchList = ArrayList<DashboardResponseModel>()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var pnrNumber: String
    private var getMostSearchedResponse: ArrayList<MostSearched> = ArrayList()
    private var source: String = ""
    private var destination: String = ""
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var lastSearchedSource: String = ""
    private var lastSearchedDestination: String = ""
    private var isShowPendingApiBookingsLinkInHomePage: Boolean? = false
    private var isShowPendingConfirmationLinkInHomePage: Boolean? = false
    private var isAllowToReleaseApiTentativeBlockedTickets: Boolean? = false
    private val privilegeDetailsViewModel by sharedViewModel<PrivilegeDetailsViewModel>()
    private var checkLoginUserType: String? = ""
    private var isAllowBookingForAllServices: Boolean? = false
    private var isAllowBookingForAllotedServices: Boolean? = false
    private var isFromDashboard: Boolean = false
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var locale: String? = ""
    private var pendingApiTicketsCount = 0
    private var pendingETicketsCount = 0
    private var cancelOptkey = ""
    private var cancelOtp = ""
    private val cancelTicketViewModel by viewModel<CancelTicketViewModel<Any?>>()
    private var cancelOtpLayoutDialogOpenCount = 0
    lateinit var created :PrivilegeResponseModel
    private var eBooking: String = ""
    private var apiTicketBooking: String = ""
    private var branchTicketBooking: String = ""
    private var offlineAgent: String = ""
    private var onlineAgent: String = ""
    private var country = ""
    private var eBookingPercentage: String = ""
    private var apiTicketBookingPercentage: String = ""
    private var branchTicketBookingPercentage: String = ""
    private var offlineAgentPercentage: String = ""
    private var onlineAgentPercentage: String = ""

    companion object {
        val tag = DashboardFragment::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun isInternetOnCallApisAndInitUI() {
        if (view != null && checkIfFragmentAttached(this, requireContext())) {

            if (checkIfFragmentAttached(this, requireContext())) {
                viewLifecycleOwner.lifecycleScope.launch {
                        getPref()
                        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
                        swipeRefreshLayout()
                }
            }
        }
    }

    override fun isNetworkOff() {
        
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view: View = binding.root

        edgeToEdgeFromOnlyBottom(binding.root)

        setObserver()
        releaseTicketObserver()
        setPrivilegesObserver()
        onClickListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkIfFragmentAttached(this, requireContext())) {
            Timber.d(":fragmentVisibility: 24")

            if (checkIfFragmentAttached(this, requireContext())) {
                getPref()
                privilegeDetailsViewModel.isDashboardDefaultTab.observe(viewLifecycleOwner) {
                    if (it) {
                        startShimmerEffect()
                        callDashboardsApi()
                    }
                }

                firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
                swipeRefreshLayout()
            }
        }

        lifecycleScope.launch {
            dashboardViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            cancelTicketViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
    }


    fun setData(it:PrivilegeResponseModel){

        if (!checkIfFragmentAttached(this, requireContext())) return

        PreferenceUtils.setPreference("otp_validation_time",it.configuredLoginValidityTime)

        PreferenceUtils.setPreference(
            "send_qr_code_to_customers_to_authenticate_boarding_status",
            it.sendQrCodeToCustomersToAuthenticateBoardingStatus
        )
        PreferenceUtils.setPreference(
            "send_otp_to_customers_to_authenticate_boarding_status",
            it.sendOtpToCustomersToAuthenticateBoardingStatus
        )
        it.apply {
            if (showPendingApiBookingsLinkInHomePage != null) {
                isShowPendingApiBookingsLinkInHomePage =
                    showPendingApiBookingsLinkInHomePage
            }
            if (showPendingConfirmationLinkInHomePage != null) {
                isShowPendingConfirmationLinkInHomePage =
                    showPendingConfirmationLinkInHomePage
            }
            if (allowToReleaseApiTentativeBlockedTickets != null) {
                isAllowToReleaseApiTentativeBlockedTickets =
                    allowToReleaseApiTentativeBlockedTickets
            }

            boLicenses?.let {
                isAllowBookingForAllServices =
                    boLicenses.allowBookingForAllServices
                isAllowBookingForAllotedServices =
                    boLicenses.allowBookingForAllotedServices
            }

            if (isShowPendingApiBookingsLinkInHomePage == true) {
                binding.cardPendingTickets.visible()
            } else {
                binding.cardPendingTickets.gone()
            }

            if (isShowPendingConfirmationLinkInHomePage == true) {
                binding.cardPendingETickets.visible()
            } else {
                binding.cardPendingETickets.gone()
            }
            try {
                requireActivity().isActivityIsLive{
                    PreferenceUtils.setPreference(
                        requireContext().getString(R.string.mobile_number_length),
                        phoneNumValidationCount
                    )
                }
            }catch (e: Exception){
                if(BuildConfig.DEBUG){
                    e.printStackTrace()
                }
            }



        }
        requireActivity().isActivityIsLive {
            if (isAllowBookingForAllServices == true
                && isAllowBookingForAllotedServices == true
                && checkLoginUserType == requireContext().getString(R.string.role_field_officer)
            ) {
                binding.dashboardContainerMostSearches.visible()

            } else if (isAllowBookingForAllServices == false
                && isAllowBookingForAllotedServices == true
                && checkLoginUserType != requireContext().getString(R.string.role_field_officer)
            ) {
                binding.dashboardContainerMostSearches.gone()

            } else if (isAllowBookingForAllServices == false
                && isAllowBookingForAllotedServices == true
                && checkLoginUserType == requireContext().getString(R.string.role_field_officer)
            ) {
                binding.dashboardContainerMostSearches.gone()

            } else if (isAllowBookingForAllServices == true
                && isAllowBookingForAllotedServices == false
                && checkLoginUserType == requireContext().getString(R.string.role_field_officer)
            ) {
                binding.dashboardContainerMostSearches.visible()
            } else {
                binding.dashboardContainerMostSearches.visible()
            }
        }
        currency = it.currency
        currencyFormat = getCurrencyFormat(requireContext(), it.currencyFormat)
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
        lastSearchedSource = PreferenceUtils.getLastSearchSource()
        lastSearchedDestination = PreferenceUtils.getLastSearchDestination()
        checkLoginUserType = loginModelPref.role

        lifecycleScope.launch {
            val privilege = (activity as BaseActivity).getPrivilegeBaseSafely()
            dashboardViewModel.updatePrivileges(privilege)
        }

        dashboardViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
            safeUIOperation {
                if (privilegeResponse?.allowToViewTsAppNewDashboard == true) {
                    val params = binding.swipeRefreshLayoutDashboard.layoutParams as ViewGroup.MarginLayoutParams
                    params.topMargin = 0
                    binding.swipeRefreshLayoutDashboard.layoutParams = params
                }
                if(privilegeResponse != null) {
                    country = privilegeResponse?.country ?: ""
                }
            }
        }

        if (checkLoginUserType == getString(R.string.travel_agent)
        ) {
            binding.layoutPendingTicketsFixed.gone()
            binding.layoutPendingETicketsFixed.gone()
        } else {
            binding.layoutPendingTicketsFixed.visible()
            binding.layoutPendingETicketsFixed.visible()
        }
    }

    private fun callDashboardsApi() {
        dashboardViewModel.dashboardSummaryAPI(
            com.bitla.ts.domain.pojo.dashboard_model.request.ReqBody(
                api_key = loginModelPref.api_key,
                json_format = json_format,
                locale = locale
            ),
            dashboard_summary_method_name
        )
    }

    private fun callReleaseTicketApi(remark: String) {
        dashboardViewModel.releaseTicketAPIWithoutTicket(
            ReqBodyWithoutTicket(
                apiKey = loginModelPref.api_key,
                pnrNumber = pnrNumber,
                remarks = remark,
                isFromDashboard = isFromDashboard,
                json_format = json_format,
                locale = locale
            ),
            release_phone_block_ticket_method_name
        )
    }

    private fun setPrivilegesObserver() {
        privilegeDetailsViewModel.privilegeResponseModel.observe(viewLifecycleOwner) {
            if (it != null && checkIfFragmentAttached(this, requireContext())) {
                when (it.code) {
                    200 -> {
                        PreferenceUtils.putObject(LocalDateTime.now(), PREF_PRIVILEGE_DETAILS_CALLED)
                        // PreferenceUtils.putObject(it, PREF_PRIVILEGE_DETAILS)
                        (activity as BaseActivity).putObjectBase(it,PREF_PRIVILEGE_DETAILS)
                        it.isEzetapEnabledInTsApp=false

                        // For the field officer role, the fragment is not verifying setData() and  is showing all the unnecessary components too.
                        // so commented above part
//                        if (isAdded && view != null) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                setData(it)
                            }
                        }


//                        it.apply {
//                            if (showPendingApiBookingsLinkInHomePage != null) {
//                                isShowPendingApiBookingsLinkInHomePage =
//                                    showPendingApiBookingsLinkInHomePage
//                            }
//                            if (showPendingConfirmationLinkInHomePage != null) {
//                                isShowPendingConfirmationLinkInHomePage =
//                                    showPendingConfirmationLinkInHomePage
//                            }
//                            if (allowToReleaseApiTentativeBlockedTickets != null) {
//                                isAllowToReleaseApiTentativeBlockedTickets =
//                                    allowToReleaseApiTentativeBlockedTickets
//                            }
//
//                            boLicenses.let {
//                                isAllowBookingForAllServices =
//                                    boLicenses?.allowBookingForAllServices
//                                isAllowBookingForAllotedServices =
//                                    boLicenses?.allowBookingForAllotedServices
//                            }
//
//                            if (isShowPendingApiBookingsLinkInHomePage == true) {
//                                binding.cardPendingTickets.visible()
//                            } else {
//                                binding.cardPendingTickets.gone()
//                            }
//
//                            if (isShowPendingConfirmationLinkInHomePage == true) {
//                                binding.cardPendingETickets.visible()
//                            } else {
//                                binding.cardPendingETickets.gone()
//                            }
//
//                            PreferenceUtils.setPreference(
//                                getString(R.string.mobile_number_length),
//                                phoneNumValidationCount
//                            )
//
//                        }
//
//
//                        if (isAllowBookingForAllServices == true
//                            && isAllowBookingForAllotedServices == true
//                            && checkLoginUserType == getString(R.string.role_field_officer)
//                        ) {
//                            binding.dashboardContainerMostSearches.visible()
//
//                        } else if (isAllowBookingForAllServices == false
//                            && isAllowBookingForAllotedServices == true
//                            && checkLoginUserType != getString(R.string.role_field_officer)
//                        ) {
//                            binding.dashboardContainerMostSearches.gone()
//
//                        } else if (isAllowBookingForAllServices == false
//                            && isAllowBookingForAllotedServices == true
//                            && checkLoginUserType == getString(R.string.role_field_officer)
//                        ) {
//                            binding.dashboardContainerMostSearches.gone()
//
//                        } else if (isAllowBookingForAllServices == true
//                            && isAllowBookingForAllotedServices == false
//                            && checkLoginUserType == getString(R.string.role_field_officer)
//                        ) {
//                            binding.dashboardContainerMostSearches.visible()
//                        } else {
//                            binding.dashboardContainerMostSearches.visible()
//                        }
//                        currency = it.currency
//                        currencyFormat = getCurrencyFormat(requireContext(), it.currencyFormat)
                    }
                    401 -> {
                        if (isAttachedToActivity()) {
                            /*DialogUtils.unAuthorizedDialog(
                                requireContext(),
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            (activity as BaseActivity).showUnauthorisedDialog()

                        }
                    }
                }
            } else {
                requireActivity().toast(getString(R.string.something_went_wrong))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        try {
            dashboardViewModel.loadingState.observe(viewLifecycleOwner) {
                Timber.d("LoadingState ${it.status}")
                when (it) {
                    LoadingState.LOADING -> startShimmerEffect()
                }
            }

            dashboardViewModel.dashboardSummaryViewModelModel.observe(viewLifecycleOwner) { it ->
                if (it != null && isAdded && !isDetached) {
                    stopShimmerEffect()
                    if (it.code == 200 && it.success) {
                        pendingApiTicketsCount = it.body?.pendingApiTickets?.count ?: 0
                        pendingETicketsCount = it.body?.pendingETickets?.count ?: 0
                        getMostSearchedResponse = it.body?.mostSearched as? ArrayList<MostSearched> ?: ArrayList()
                        phoneBookedValue = it.body?.phoneBlocked?.toString() ?: ""
                        quotaBlocked = it.body?.quotaBlocked?.toString() ?: ""
                        cancelled = it.body?.cancelled?.toString() ?: ""
                        booked = it.body?.booked?.toString() ?: ""

                        eBooking = (it.body?.eBooking ?: 0.0f).toString()
                        eBookingPercentage = (it.body?.eBookingPercentage ?: 0.0f).toString()

                        apiTicketBooking = (it.body?.apiBooking ?: 0.0f).toString()
                        apiTicketBookingPercentage = (it.body?.apiBookingPercentage ?: 0.0f).toString()

                        branchTicketBooking = (it.body?.branchBooking ?: 0.0f).toString()
                        branchTicketBookingPercentage = (it.body?.branchBookingPercentage ?: 0.0f).toString()

                        onlineAgent = (it.body?.onlineAgent ?: 0.0f).toString()
                        onlineAgentPercentage = (it.body?.onlineAgentPercentage ?: 0.0f).toString()

                        offlineAgent = (it.body?.offlineAgent ?: 0.0f).toString()
                        offlineAgentPercentage = (it.body?.offlineAgentPercentage ?: 0.0f).toString()

                        safeUIOperation {
                            binding.apply {
                                swipeRefreshLayoutDashboard.isRefreshing = false
                                tvPendingTickets.text = "$pendingApiTicketsCount"
                                tvPendingETickets.text = "$pendingETicketsCount"
                            }
                        }


                        if(country.equals("India", true)) {
                            binding.apply {
                                dashboardChartCard.visible()
                                relativeLayout7.gone()
                                relativeLayout6.gone()
                                relativeLayout8.gone()
                                relativeLayout9.gone()
                                textSummary.text = getString(R.string.business_summary)
                            }
                        } else {
                            binding.apply {
                                relativeLayout1.gone()
                                relativeLayout3.gone()
                                relativeLayout4.gone()
                                relativeLayout5.gone()
                                relativeLayout10.gone()
                                textSummary.text = getString(R.string.today_s_summary)
                                if (quotaBlocked == getString(R.string.summary_value)) {
                                    relativeLayout7.gone()
                                } else {
                                    relativeLayout7.visible()
                                }

                                if (phoneBookedValue == getString(R.string.summary_value)
                                    && quotaBlocked == getString(R.string.summary_value)
                                    && cancelled == getString(R.string.summary_value)
                                    && booked == getString(R.string.summary_value)
                                ) {
                                    dashboardChartCard.visible()
                                } else {
                                    dashboardChartCard.visible()
                                }
                            }
                        }

                        safeUIOperation {
                            binding.tvEBookingValue.text =
                                if(eBooking.isNotEmpty() && eBookingPercentage.isNotEmpty()
                                    && eBooking != getString(R.string.summary_value) && eBookingPercentage != getString(R.string.summary_value)) {
                                    "${currency ?: getString(R.string.rupess_symble)} ${
                                        (eBooking.toDouble()).convert(
                                            currencyFormat
                                        )
                                    } (${eBookingPercentage}%)"
                                } else {
                                    "${currency ?: getString(R.string.rupess_symble)} ${
                                        (eBooking.toDouble()).convert(
                                            currencyFormat
                                        )
                                    }"
                                }
                            binding.tvApiValue.text =
                                if(apiTicketBooking.isNotEmpty() && apiTicketBookingPercentage.isNotEmpty()
                                    && apiTicketBooking != getString(R.string.summary_value) && apiTicketBookingPercentage != getString(R.string.summary_value)) {
                                    "${currency ?: getString(R.string.rupess_symble)} ${
                                        (apiTicketBooking.toDouble()).convert(
                                            currencyFormat
                                        )
                                    } (${apiTicketBookingPercentage}%)"
                                } else {
                                    "${currency ?: getString(R.string.rupess_symble)} ${
                                        (apiTicketBooking.toDouble()).convert(
                                            currencyFormat
                                        )
                                    }"
                                }
                            binding.tvBranchValue.text =
                                if(branchTicketBooking.isNotEmpty() && branchTicketBookingPercentage.isNotEmpty()
                                    && branchTicketBooking != getString(R.string.summary_value) && branchTicketBookingPercentage != getString(R.string.summary_value)) {
                                    "${currency ?: getString(R.string.rupess_symble)} ${
                                        (branchTicketBooking.toDouble()).convert(
                                            currencyFormat
                                        )
                                    } (${branchTicketBookingPercentage}%)"
                                } else {
                                    "${currency ?: getString(R.string.rupess_symble)} ${
                                        (branchTicketBooking.toDouble()).convert(
                                            currencyFormat
                                        )
                                    }"
                                }
                            binding.tvOnlineAgentValue.text =
                                if(onlineAgent.isNotEmpty() && onlineAgentPercentage.isNotEmpty()
                                    && onlineAgent != getString(R.string.summary_value) && onlineAgentPercentage != getString(R.string.summary_value)) {
                                    "${currency ?: getString(R.string.rupess_symble)} ${
                                        (onlineAgent.toDouble()).convert(
                                            currencyFormat
                                        )
                                    } (${onlineAgentPercentage}%)"
                                } else {
                                    "${currency ?: getString(R.string.rupess_symble)} ${
                                        (onlineAgent.toDouble()).convert(
                                            currencyFormat
                                        )
                                    }"
                                }
                            binding.tvOfflineAgentValue.text =
                                if(offlineAgent.isNotEmpty() && offlineAgentPercentage.isNotEmpty()
                                    && offlineAgent != getString(R.string.summary_value) && offlineAgentPercentage != getString(R.string.summary_value)) {
                                    "${currency ?: getString(R.string.rupess_symble)} ${
                                        (offlineAgent.toDouble()).convert(
                                            currencyFormat
                                        )
                                    } (${offlineAgentPercentage}%)"
                                } else {
                                    "${currency ?: getString(R.string.rupess_symble)} ${
                                        (offlineAgent.toDouble()).convert(
                                            currencyFormat
                                        )
                                    }"
                                }
                            binding.tvBookedValue.text =
                                "${currency ?: getString(R.string.rupess_symble)} ${
                                    (booked.toDouble()).convert(
                                        currencyFormat
                                    )
                                }"
                            binding.tvBlockedValue.text =
                                "${currency ?: getString(R.string.rupess_symble)} ${
                                    (quotaBlocked.toDouble()).convert(
                                        currencyFormat
                                    )
                                }"
                            binding.tvCancelledValue.text =
                                "${currency ?: getString(R.string.rupess_symble)} ${
                                    (cancelled.toDouble()).convert(
                                        currencyFormat
                                    )
                                }"
                            binding.tvPhoneBlockedValue.text =
                                "${currency ?: getString(R.string.rupess_symble)} ${
                                    (phoneBookedValue.toDouble()).convert(
                                        currencyFormat
                                    )
                                }"
                        }
                        setGraphData()

                        val mostSearched = mutableListOf<MostSearched>()
                        it.body?.mostSearched?.forEach { mostSearchItem ->
                            if (mostSearchItem.originName != null || mostSearchItem.destName != null)
                                mostSearched.add(mostSearchItem)
                        }
                        setMostSearchDataListAdapter(mostSearched)
                        it.body?.collections?.let { collections ->
                            setCollectionData(collections)
                        }

                        if (it.body?.pendingETickets?.count == 0) {
                                binding.tvMsgPendingETickets.visible()
                                TransitionManager.beginDelayedTransition(
                                    binding.cardPendingTickets,
                                    AutoTransition()
                                )

                        } else {
                                binding.tvMsgPendingETickets.gone()

                            it.body?.pendingETickets?.data?.let { data ->
                                setPendingETicketsAdapter(data)
                            }
                        }
                        if (it.body?.pendingApiTickets?.count == 0) {
                                binding.tvMsgPendingTickets.visible()
                                binding.rvPendingTickets.gone()
                                TransitionManager.beginDelayedTransition(
                                    binding.cardPendingTickets,
                                    AutoTransition()
                                )

                        } else {
                                binding.tvMsgPendingTickets.gone()
                                binding.rvPendingTickets.visible()

                            it.body?.pendingApiTickets?.data?.let { data ->
                                setPendingTicketsAdapter(data)
                            }
                        }

                        it.body?.pendingETickets?.data?.forEach { ticket ->
                            pnrNumber = ticket.pnrNumber.toString()
                        }

                        it.body?.pendingApiTickets?.data?.forEach { ticket ->
                            pnrNumber = ticket.pnrNumber.toString()
                        }

                        if (it.body?.serverDateTime != null && it.body.serverDateTime.isNotEmpty() == true) {
                            (activity as? DashboardNavigateActivity)?.lastUpdatedOn(
                                "${it.body.serverDateTime}"
                            )
                        } else {
                            //binding.tvServerDateTime.gone()
                        }

                        if(it.body?.availableBalance != null || it.body?.branchBalance != null) {

                            binding.linearLayoutBalance.visible()

                            var isAgentBalanceNull = false
                            if(it.body.availableBalance != null) {
                                binding.linearLayoutAvailableBalance.visible()
                                val availableBalance = it.body.availableBalance.convert(currencyFormat)
                                binding.tvAvailableBalance.text = "${currency ?: getString(R.string.rupess_symble)} $availableBalance"
                            } else {
                                isAgentBalanceNull = true

                                binding.linearLayoutAvailableBalance.gone()
                            }

                            if(it.body.branchBalance != null) {

                                if(isAgentBalanceNull) {
                                    binding.linearLayoutBranchBalanceSingleItem.visible()
                                    val branchBalance = it.body.branchBalance.convert(currencyFormat)
                                    binding.tvBranchBalanceSingleItem.text = "${currency ?: getString(R.string.rupess_symble)} $branchBalance"

                                } else {
                                    binding.linearLayoutBranchBalanceSingleItem.gone()

                                    binding.constraintLayoutBranchBalance.visible()
                                    val branchBalance = it.body.branchBalance.convert(currencyFormat)
                                    binding.tvBranchBalance.text = "${currency ?: getString(R.string.rupess_symble)} $branchBalance"
                                }

                            } else {
                                binding.constraintLayoutBranchBalance.gone()
                            }
                        } else {
                            binding.linearLayoutBalance.gone()
                        }

                    } else if (it.code == 401) {
                        if (isAttachedToActivity()) {
                            /*DialogUtils.unAuthorizedDialog(
                                requireContext(),
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            (activity as BaseActivity).showUnauthorisedDialog()

                        }
                    } else {
                        requireActivity().toast(getString(R.string.something_went_wrong))
                    }
                }

            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    private fun releaseTicketObserver() {

        dashboardViewModel.releaseTicketResponseViewModel.observe(requireActivity()) {

            if (it != null && checkIfFragmentAttached(this, requireContext())) {
                if (it.code == 200) {

                    cancelOtpLayoutDialogOpenCount = 0

                    if (it.otpValidation) {
                        if (cancelOtp.isEmpty()) {
                            if (it.key.isNotEmpty()) {
                                if (cancelOtpLayoutDialogOpenCount == 0) {
                                    DialogUtils.cancelOtpLayoutDialog(requireContext(), this,this, dimissAction = {})
                                    cancelOptkey = it.key
                                    requireContext().toast(it.message)
                                    cancelOtpLayoutDialogOpenCount++
                                }

                            } else {
                                requireActivity().toast(it.message)
                            }
                        } else {
                            callConfirmOtpReleasePhoneBlockTicketApi()
                            setConfirmOtpReleaseObserver()
                        }
                    } else {
                        stopShimmerEffect()
                        if (it.message.isNotBlank()) {
                            requireActivity().toast(it.message)
                        } else {
                            context?.toast(getString(R.string.something_went_wrong))
                        }
                    }
                    binding.swipeRefreshLayoutDashboard.isRefreshing = false
                } else {
                    if (it.message.isNotBlank()) {
                        requireActivity().toast(it.message)
                    } else {
                        context?.toast(getString(R.string.something_went_wrong))
                    }
                    stopShimmerEffect()
                    binding.swipeRefreshLayoutDashboard.isRefreshing = false
                }
            } else {
                if (isAttachedToActivity()) {
                    requireContext().toast(getString(R.string.server_error))
                }
                stopShimmerEffect()
                binding.swipeRefreshLayoutDashboard.isRefreshing = false
            }
        }
    }

    private fun callConfirmOtpReleasePhoneBlockTicketApi() {
        val reqBody = com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.ReqBody(
                apiKey = loginModelPref.api_key,
                isFromMiddleTier = true,
                key = cancelOptkey,
                otp = cancelOtp,
                pnrNumber = pnrNumber,
                remarks = "",
                ticket = com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.Ticket("")
            )
        cancelTicketViewModel.getConfirmOtpReleasePhoneBlockTicketApi(
            confirmOtpReleasePhoneBlockTicketRequest = reqBody,
            apiType = cancellation_details_ticket_method_name
        )
    }

    private fun setConfirmOtpReleaseObserver() {

        cancelTicketViewModel.confirmOtpReleasePhoneBlockTicketResponse.observe(requireActivity()) {
            if (it != null && checkIfFragmentAttached(this, requireContext())) {
                when (it.code) {
                    200 -> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            DialogUtils.successfulMsgDialog(requireContext(), it.message)
                        }, 2000)
                    }
                    422 -> {
                        if (it.message != null) {
                            requireContext().toast("${it.message}")
                        }

                        cancelOtp = ""
                    }
                    401 -> {
                       /* DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    else -> {
                        requireContext().toast(it.message)
                        cancelOtp = ""
                    }
                }

            } else {
                if (!checkIfFragmentAttached(this, requireContext())){
                    requireContext().toast(getString(R.string.server_error))
                }
                cancelOtp = ""
            }
        }
    }

    private fun setGraphData() {
        
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            dragDecelerationFrictionCoef = 0.0f
            isDrawHoleEnabled = true
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(10)
            isRotationEnabled = false
            isHighlightPerTapEnabled = false
            animateY(1000, Easing.EaseInOutQuad)
            legend.isEnabled = false
            holeRadius = 80f
        }

        val entries: ArrayList<PieEntry> = ArrayList()
        try {
            if(country.equals("India", true)) {
                entries.add(PieEntry(eBooking.toFloatOrNull() ?: 0f))
                entries.add(PieEntry(apiTicketBooking.toFloatOrNull() ?: 0f))
                entries.add(PieEntry(branchTicketBooking.toFloatOrNull() ?: 0f))
                entries.add(PieEntry(onlineAgent.toFloatOrNull() ?: 0f))
                entries.add(PieEntry(offlineAgent.toFloatOrNull() ?: 0f))
            } else {
                entries.add(PieEntry(booked.toFloatOrNull() ?: 0f))
                entries.add(PieEntry(quotaBlocked.toFloatOrNull() ?: 0f))
                entries.add(PieEntry(cancelled.toFloatOrNull() ?: 0f))
                entries.add(PieEntry(phoneBookedValue.toFloatOrNull() ?: 0f))
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return
        }


        val dataSet = PieDataSet(entries, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 3f
        val colors: ArrayList<Int> = ArrayList()
        if (isAttachedToActivity()) {
            if(country.equals("India", true)) {
                colors.add(ContextCompat.getColor(requireContext(), R.color.booked_tickets))
                colors.add(ContextCompat.getColor(requireContext(), R.color.cancelled_tickets))
                colors.add(ContextCompat.getColor(requireContext(), R.color.blocked_tickets))
                colors.add(ContextCompat.getColor(requireContext(), R.color.blue_dark))
                colors.add(ContextCompat.getColor(requireContext(), R.color.offline_agent_tickets))
            } else {
                colors.add(ContextCompat.getColor(requireContext(), R.color.booked_tickets))
                colors.add(ContextCompat.getColor(requireContext(), R.color.cancelled_tickets))
                colors.add(ContextCompat.getColor(requireContext(), R.color.blocked_tickets))
                colors.add(ContextCompat.getColor(requireContext(), R.color.color_blue))
            }
            dataSet.colors = colors
        }

        dataSet.valueLinePart1OffsetPercentage = 100f
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pieChart))
        data.setValueTextSize(0f)
        data.setValueTextColor(Color.BLACK)
        binding.pieChart.data = data
        binding.pieChart.highlightValues(null)
        binding.pieChart.invalidate()
    }


    @SuppressLint("SetTextI18n")
    private fun setCollectionData(collections: Collections) {
        if (!checkIfFragmentAttached(this, requireContext()) || view == null || collections == null) return

        binding.textCashAmountValue.text =
            "${currency ?: getString(R.string.rupess_symble)} ${
                (collections.cash)?.convert(
                    currencyFormat
                )
            }"
        binding.textWalletAmountValue.text =
            "${currency ?: getString(R.string.rupess_symble)} ${
                (collections.wallet)?.convert(
                    currencyFormat
                )
            }"
        binding.textCardAmountValue.text =
            "${currency ?: getString(R.string.rupess_symble)} ${
                (collections.card)?.convert(
                    currencyFormat
                )
            }"
        binding.textOtherAmountValue.text =
            "${currency ?: getString(R.string.rupess_symble)} ${
                (collections.others)?.convert(
                    currencyFormat
                )
            }"
        binding.textABhimaAmountValue.text =
            "${currency ?: getString(R.string.rupess_symble)} ${
                (collections.bima)?.convert(
                    currencyFormat
                )
            }"
        binding.textAgentCollectionAmountValue.text =
            "${currency ?: getString(R.string.rupess_symble)} ${
                (collections.agentCollection)?.convert(
                    currencyFormat
                )
            }"

        payableAmount =
            ((collections.cash ?: 0.0)
                    + (collections.wallet ?: 0.0)
                    + (collections.card ?: 0.0)
                    + (collections.others ?: 0.0)
                    + (collections.bima ?: 0.0)).toString()
        binding.tvPayableAmount.text =
            "${currency ?: getString(R.string.rupess_symble)} ${
                (payableAmount.toDouble()).convert(
                    currencyFormat
                )
            }"

    }

    private fun setPendingTicketsAdapter(pendingApiTicketsList: MutableList<Data>) {
        if (!checkIfFragmentAttached(this, requireContext())) return

        safeUIOperation {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.rvPendingTickets.layoutManager = layoutManager
            dashboardPendingApiTicketsAdapter =
                DashboardPendingApiTicketsAdapter(
                    requireActivity(), this@DashboardFragment,
                    this@DashboardFragment, pendingApiTicketsList,
                    isAllowToReleaseApiTentativeBlockedTickets ?: false
                )
            binding.rvPendingTickets.adapter = dashboardPendingApiTicketsAdapter
        }
    }

    private fun setPendingETicketsAdapter(pendingETicketsList: MutableList<Data>) {
        if (!checkIfFragmentAttached(this, requireContext())) return

        safeUIOperation {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.rvPendingETickets.layoutManager = layoutManager
            dashboardPendingETicketsAdapter =
                DashboardPendingETicketsAdapter(requireActivity(), this@DashboardFragment, this@DashboardFragment, pendingETicketsList)
            binding.rvPendingETickets.adapter = dashboardPendingETicketsAdapter
        }
    }

    private fun setMostSearchDataListAdapter(mostSearched: MutableList<MostSearched>) {
        if (!checkIfFragmentAttached(this, requireContext())) return

        safeUIOperation {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.mostSearchRecyclerMainView.layoutManager = layoutManager
            mostSearchDataAdapter =
                MostSearchDataAdapter(requireActivity(), mostSearched, this@DashboardFragment)
            binding.mostSearchRecyclerMainView.adapter = mostSearchDataAdapter
        }
    }


    private fun onClickListener() {
        binding.layoutSearch.setOnClickListener(this)
        binding.layoutCollectionFixed.setOnClickListener(this)
        binding.layoutPendingTicketsFixed.setOnClickListener(this)
        binding.layoutPendingETicketsFixed.setOnClickListener(this)
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {

        if (view.tag != null) {
            if (view.tag == getString(R.string.most_searched)) {
                source = getMostSearchedResponse[position].originName ?: ""
                destination = getMostSearchedResponse[position].destName ?: ""
                sourceId = getMostSearchedResponse[position].originId.toString()
                destinationId = getMostSearchedResponse[position].destId.toString()
                lastSearchedSource = getMostSearchedResponse[position].originName ?: ""
                lastSearchedDestination = getMostSearchedResponse[position].destName ?: ""

                PreferenceUtils.putString(PREF_SOURCE, source)
                PreferenceUtils.putString(PREF_DESTINATION, destination)
                PreferenceUtils.putString(PREF_SOURCE_ID, sourceId)
                PreferenceUtils.putString(PREF_DESTINATION_ID, destinationId)
                PreferenceUtils.putString(PREF_LAST_SEARCHED_SOURCE, lastSearchedSource)
                PreferenceUtils.putString(
                    PREF_LAST_SEARCHED_DESTINATION,
                    lastSearchedDestination
                )

                PreferenceUtils.putString(PREF_TRAVEL_DATE, getTodayDate())

                PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)

                PreferenceUtils.putString(
                    PREF_NEW_BOOKING_NAVIGATION,
                    DashboardFragment.tag
                )

                val intent = Intent(activity, BusDetailsActivity::class.java)
                startActivity(intent)

                if (isAttachedToActivity()) {
                    firebaseLogEvent(
                        requireContext(),
                        MOST_SEARCHED,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        MOST_SEARCHED,
                       com.bitla.ts.utils.constants.MostSearched.MOST_SEARCHED
                    )
                }
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        busData: com.bitla.ts.domain.pojo.available_routes.Result,
    ) {
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.layout_search -> {
//                v.findNavController().navigate(R.id.bookings_fragment)
                PreferenceUtils.putString(PREF_TRAVEL_DATE, getTodayDate())

                Timber.d("lastSearchedSource $lastSearchedSource -- $sourceId lastSearchedDestination $lastSearchedDestination -- $destinationId")

                if (sourceId.isEmpty() && PreferenceUtils.getString(LAST_SEARCHED_SOURCE_ID) != null) {
                    sourceId = PreferenceUtils.getString(LAST_SEARCHED_SOURCE_ID)!!
                    PreferenceUtils.putString(PREF_SOURCE_ID, sourceId)
                }

                if (destinationId.isEmpty() && PreferenceUtils.getString(
                        LAST_SEARCHED_DESTINATION_ID
                    ) != null
                ) {
                    destinationId = PreferenceUtils.getString(LAST_SEARCHED_DESTINATION_ID)!!
                    PreferenceUtils.putString(PREF_DESTINATION_ID, destinationId)
                }

                PreferenceUtils.putString(
                    PREF_NEW_BOOKING_NAVIGATION,
                    DashboardFragment.tag
                )

                val intent = Intent(activity, BusDetailsActivity::class.java)
                intent.putExtra(getString(R.string.last_searched_source), lastSearchedSource)
                intent.putExtra(
                    getString(R.string.last_searched_destination),
                    lastSearchedDestination
                )
                startActivity(intent)

                if (isAttachedToActivity()) {
                    firebaseLogEvent(
                        requireContext(),
                        CONTINUE_LAST_SEARCH,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        CONTINUE_LAST_SEARCH,
                        ContinueSearch.CONTINUE_LAST_SEARCH
                    )
                }
            }
            R.id.layoutCollectionFixed -> {
                if (binding.layoutCollectionHidden.isVisible) {
//                    TransitionManager.beginDelayedTransition(
//                        binding.cardCollection,
//                        AutoTransition()
//                    )
                    binding.layoutCollectionHidden.gone()
                    binding.imgCollection.setImageResource(R.drawable.ic_arrow_up_24)
                } else {
//                    TransitionManager.beginDelayedTransition(
//                        binding.cardCollection,
////                        AutoTransition()
//                    )
                    binding.layoutCollectionHidden.visible()
                    binding.imgCollection.setImageResource(R.drawable.ic_arrow_down)
                }
            }
            R.id.layoutPendingTicketsFixed -> {

                if (binding.layoutPendingTicketsHidden.isVisible) {
                    binding.layoutPendingTicketsHidden.gone()
                    binding.imgPendingTickets.setImageResource(R.drawable.ic_arrow_up_24)

                } else {
                    binding.layoutPendingTicketsHidden.visible()
                    binding.imgPendingTickets.setImageResource(R.drawable.ic_arrow_down)

                    if (isAttachedToActivity()) {
                        firebaseLogEvent(
                            requireContext(),
                            PENDING_API_TICKETS,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            PENDING_API_TICKETS,
                            PendingSearch.PENDING_SEARCH
                        )
                    }
                }
            }
            R.id.layoutPendingETicketsFixed -> {
                if (binding.layoutPendingETicketsHidden.isVisible) {
                    binding.layoutPendingETicketsHidden.gone()
                    binding.imgPendingETickets.setImageResource(R.drawable.ic_arrow_up_24)

                } else {
                    binding.layoutPendingETicketsHidden.visible()
                    binding.imgPendingETickets.setImageResource(R.drawable.ic_arrow_down)

                    if (isAttachedToActivity()) {
                        firebaseLogEvent(
                            requireContext(),
                            PENDING_E_TICKETS,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            PENDING_E_TICKETS,
                            PendingETicket.PENDING_E_TICKET
                        )
                    }
                }
            }
        }
    }

    override fun onSingleButtonClick(str: String) {

        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        } else {
            callReleaseTicketApi(str)
            if (str == "resend") {
                callReleaseTicketApi(str)
            } else {
                cancelOtp = str
            }

            if (isAttachedToActivity()) {
                firebaseLogEvent(
                    requireContext(),
                    RELEASE_TICKET,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    RELEASE_TICKET,
                    ReleaseTicket.RELEASE_TICKET_DASHBOARD
                )
            }
        }
    }

    /*
     * this method to used for start Shimmer Effect
     * */
    private fun startShimmerEffect() {
        binding.shimmerDashboard.visible()
        binding.dashboardContainer.gone()
        binding.shimmerDashboard.startShimmer()
        //binding.tvServerDateTime.gone()
    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.shimmerDashboard.gone()
        binding.dashboardContainer.visible()
        //binding.tvServerDateTime.visible()
        if (binding.shimmerDashboard.isShimmerStarted) {
            binding.shimmerDashboard.stopShimmer()
        }
    }

    private fun swipeRefreshLayout() {
        binding.swipeRefreshLayoutDashboard.setOnRefreshListener {
            startShimmerEffect()
            callDashboardsApi()
        }
    }

    override fun onPause() {
        super.onPause()
        searchList.clear()
    }
    
    override fun onResume() {
        super.onResume()
        PreferenceUtils.apply {
            setPreference("selectedCityOrigin", "All")
            setPreference("selectedCityIdOrigin", "0")
            setPreference("selectedCityDestination", "All")
            setPreference("selectedCityIdDestination", "0")
            setPreference("TravelSelection", "none")
        }

        getPref()
    }

    override fun onPnrSelection(tag: String, pnr: Any, doj: Any?) {

        if (isAttachedToActivity()) {
            if (tag == getString(R.string.api_release_ticket)) {
                pnrNumber = pnr.toString()
                isFromDashboard = false
                DialogUtils.releaseTicketDialog(
                    requireActivity(),
                    "${requireActivity().getString(R.string.pnr)} $pnr",
                    "${requireActivity().getString(R.string.doj)} ${getDateDMY(doj.toString())}",
                    this
                )
            } else {
                pnrNumber = pnr.toString()
                isFromDashboard = true
                DialogUtils.releaseTicketDialog(
                    requireActivity(),
                    "${requireActivity().getString(R.string.pnr)} $pnr",
                    "${requireActivity().getString(R.string.doj)} ${getDateDMY(doj.toString())}",
                    this
                )
            }
        }

    }

    private fun isAttachedToActivity(): Boolean {
        return isAdded && !isDetached && !isRemoving && activity != null
    }

    override fun onReturnInstance(dialog: Any) {

    }

    // Helper function for safe UI operations
    private fun safeUIOperation(operation: () -> Unit): Boolean {
        return if (isAdded && !isDetached && !isRemoving && activity != null) {
            try {
                operation()
                true
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Timber.e(e, "UI operation failed")
                }
                false
            }
        } else {
            false
        }
    }
}