package com.bitla.ts.presentation.view.dashboard

import android.app.*
import android.content.*
import android.graphics.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.tabs.*
import gone
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible
import java.time.*


class PickupChartFragment : BaseFragment(), DialogSingleButtonListener {

    private lateinit var fragmentPickupChartBinding: FragmentPickupChartBinding
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val privilegeDetailsViewModel by sharedViewModel<PrivilegeDetailsViewModel>()
    private var locale: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        with(FragmentPickupChartBinding.inflate(inflater, container, false)) {
            fragmentPickupChartBinding = this
            return root
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
        if (isAdded && checkIfFragmentAttached(this, requireContext())) {
            viewLifecycleOwner.lifecycleScope.launch {
                getPref()
                firebaseLogEvent(
                    context = requireContext(),
                    logEventName = PICK_UP_ICON,
                    loginId = loginModelPref.userName,
                    operatorName = loginModelPref.travels_name,
                    roleName = loginModelPref.role,
                    eventKey = PICK_UP_ICON,
                    eventValue = "Pick-up Charts icon"
                )
            }
        }
    }

    override fun isNetworkOff() {
    }
    

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    private fun getPref() {
        startShimmerEffect()
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceUtils.putString(PREF_TRAVEL_DATE, getTodayDate())
        if (checkIfFragmentAttached(this, requireContext())) {
            viewLifecycleOwner.lifecycleScope.launch {
                getPref()
                setPrivilegesObserver()
            }
        }
    }


    private fun initTab(isShowPickupVanChartTabInReservationChart: Boolean) {
        val tabsList: MutableList<Tabs> = mutableListOf()


        (activity as? DashboardNavigateActivity)?.reduceMarginTop()

        val tabBooking = Tabs()
        tabBooking.title = "Reservation Charts"
        tabsList.add(tabBooking)

        if (isShowPickupVanChartTabInReservationChart) {
            fragmentPickupChartBinding.tabsPickup.visible()
            val tabMyBooking = Tabs()
            tabMyBooking.title = "Pickup Van Chart"
            tabsList.add(tabMyBooking)
        } else {
            fragmentPickupChartBinding.tabsPickup.gone()
        }
        if (isAdded) {
            val fragmentAdapter = PickupPagerAdapter(
                requireContext(),
                tabsList,
                requireActivity().supportFragmentManager
            )
            fragmentPickupChartBinding.viewpagerPickup.adapter = fragmentAdapter
            fragmentPickupChartBinding.tabsPickup.setupWithViewPager(fragmentPickupChartBinding.viewpagerPickup)
            // custom tabs
            for (i in 0..fragmentPickupChartBinding.tabsPickup.tabCount.minus(1)) {
                val tab = fragmentPickupChartBinding.tabsPickup.getTabAt(i)!!
                tab.customView = null
                //tab!!.customView = fragmentAdapter.getTabView(i)

                val tabTextView: TextView = TextView(activity)
                tab.customView = tabTextView

                tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

                tabTextView.text = tab.text

                if (i == 0) {
                    // This set the font style of the first tab
                    tabTextView.setTypeface(null, Typeface.BOLD)

                }
                if (i == 1) {
                    // This set the font style of the second tab
                    tabTextView.setTypeface(null, Typeface.NORMAL)
                }
            }
            fragmentPickupChartBinding.tabsPickup.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    fragmentPickupChartBinding.viewpagerPickup.currentItem = tab!!.position
                    val text: TextView? = tab.customView as TextView?
                    text?.setTypeface(null, Typeface.BOLD)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val text: TextView = tab?.customView as TextView
                    text.setTypeface(null, Typeface.NORMAL)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
        }
    }

    private fun setPrivilegesObserver() {
        privilegeDetailsViewModel.privilegeResponseModel.observe(requireActivity()) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        PreferenceUtils.setPreference(
                            "otp_validation_time",
                            it.configuredLoginValidityTime
                        )
                       // PreferenceUtils.putObject(it, PREF_PRIVILEGE_DETAILS)
                        PreferenceUtils.putObject(
                            LocalDateTime.now(),
                            PREF_PRIVILEGE_DETAILS_CALLED
                        )
                        setData(it)

                        lifecycleScope.launch(Dispatchers.Main) { initTab( it.showPickupVanChartTabInReservationChart || it.tsPrivileges?.allowToViewPickupVanChartsAgent == true) }
                        stopShimmerEffect()

                    }

                    401 -> {
                        if (isAdded)
                            /*DialogUtils.unAuthorizedDialog(
                                requireContext(),
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        try {
                            if(it.result != null && !it.result.message.isNullOrEmpty()){
                                requireActivity().toast(it.result.message?:"Something went wrong!")
                            }
                        }catch (e: Exception){
                            requireActivity().toast(e.message)
                        }
                    }
                }
            } else
                requireActivity().toast(getString(R.string.server_error))
        }
    }

    fun setData(it: PrivilegeResponseModel) {
        PreferenceUtils.setPreference("otp_validation_time", it.configuredLoginValidityTime)
        PreferenceUtils.setPreference(
            "send_qr_code_to_customers_to_authenticate_boarding_status",
            it.sendQrCodeToCustomersToAuthenticateBoardingStatus
        )
        PreferenceUtils.setPreference(
            "send_otp_to_customers_to_authenticate_boarding_status",
            it.sendOtpToCustomersToAuthenticateBoardingStatus
        )
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun startShimmerEffect() {
        fragmentPickupChartBinding.shimmerLayout.visible()
        fragmentPickupChartBinding.shimmerLayout.startShimmer()

    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        fragmentPickupChartBinding.shimmerLayout.gone()

        if (fragmentPickupChartBinding.shimmerLayout.isShimmerStarted) {
            fragmentPickupChartBinding.shimmerLayout.stopShimmer()

        }
    }
}