package com.bitla.ts.presentation.view.dashboard

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.app.base.TsApplication
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.FragmentBookingsBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.BusPagerAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.PrivilegeDetailsViewModel
import com.bitla.ts.utils.common.checkIfFragmentAttached
import com.bitla.ts.utils.common.setDateLocale
import com.bitla.ts.utils.isActivityIsLive
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_PRIVILEGE_DETAILS
import com.bitla.ts.utils.sharedPref.PREF_PRIVILEGE_DETAILS_CALLED
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.material.tabs.TabLayout
import gone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import toast
import visible
import java.time.LocalDateTime


class BookingsFragment : BaseFragment(), DialogSingleButtonListener {
    companion object {
        val tag: String = BookingsFragment::class.java.simpleName
    }

    private var fragmentAdapter: BusPagerAdapter? = null
    private var availableRoutesCount: Int? = null
    private var allowBookingForAllotedServices: Boolean = false

    lateinit var binding: FragmentBookingsBinding
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()

    private val privilegeDetailsViewModel by sharedViewModel<PrivilegeDetailsViewModel>()


    private var locale: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setDateLocale(PreferenceUtils.getlang(), requireContext())
    }

    override fun isInternetOnCallApisAndInitUI() {
        if (checkIfFragmentAttached(this, TsApplication.getAppContext())) {

            if (checkIfFragmentAttached(this, TsApplication.getAppContext())) {
                viewLifecycleOwner.lifecycleScope.launch {
                    getPref()
                }
            }
        }
    }

    override fun isNetworkOff() {

    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPref()
        setPrivilegesObserver()
    }

    private fun startShimmer() {
        // binding.shimmerBookTicket.visible()
        binding.includeProgress.progressBar.visible()
    }

    private fun stopShimmer() {
        //  binding.shimmerBookTicket.gone()
        binding.includeProgress.progressBar.gone()
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
        stopShimmer()
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }


    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()

    }

    private fun initTab() {
        val tabsList: MutableList<Tabs> = mutableListOf()
        val tabBooking = Tabs()
        tabBooking.title = "Book Tickets"
        tabsList.add(tabBooking)

        val tabMyBooking = Tabs()
        tabMyBooking.title = "My Bookings"
        tabsList.add(tabMyBooking)

        fragmentAdapter =
            BusPagerAdapter(
                TsApplication.getAppContext(),
                tabsList,
                requireActivity().supportFragmentManager,
                allowBookingForAllotedServices,
                availableRoutesCount
            )
        binding.viewpagerBus.adapter = fragmentAdapter
        binding.tabsBus.setupWithViewPager(binding.viewpagerBus)
        // custom tabs
        for (i in 0..binding.tabsBus.tabCount.minus(1)) {
            val tab = binding.tabsBus.getTabAt(i)!!
            tab.customView = null
            //tab!!.customView = fragmentAdapter.getTabView(i)

            val tabTextView = TextView(requireContext())
            tab.customView = tabTextView

            tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            tabTextView.text = tab.text
            tabTextView.setTextColor(Color.BLACK)
        }



        binding.tabsBus.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewpagerBus.currentItem = tab!!.position
                if (tab.customView != null) {
                    val text: TextView = tab.customView as TextView
                    //text.setTypeface(null, Typeface.BOLD)
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // val text: TextView = tab?.customView as TextView
                //text.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun setPrivilegesObserver() {
        requireActivity().isActivityIsLive {
            privilegeDetailsViewModel.availableRouteCount.observe(viewLifecycleOwner) {
                if (it != null && it != 0) {
                    availableRoutesCount = it
                    setTabTitle("${getString(R.string.services)} ($it)")
                } else {
                    setTabTitle(getString(R.string.services))
                }
            }


            privilegeDetailsViewModel.privilegeResponseModel.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            allowBookingForAllotedServices = it.allowBookingForAllotedServices
                            if (checkIfFragmentAttached(this, requireContext())) {
                                viewLifecycleOwner.lifecycleScope.launch {
                                    withContext(Dispatchers.Main) {
                                        if (isFragmentSafeForAction()) {
                                            initTab()
                                        }
                                    }
                                }
                            }
                            PreferenceUtils.setPreference(
                                "otp_validation_time",
                                it.configuredLoginValidityTime
                            )
                           // PreferenceUtils.putObject(it, PREF_PRIVILEGE_DETAILS)
                            PreferenceUtils.putObject(
                                LocalDateTime.now(),
                                PREF_PRIVILEGE_DETAILS_CALLED
                            )
                            (activity as BaseActivity).putObjectBase(it,PREF_PRIVILEGE_DETAILS)



                            PreferenceUtils.setPreference(
                                "send_qr_code_to_customers_to_authenticate_boarding_status",
                                it.sendQrCodeToCustomersToAuthenticateBoardingStatus
                            )
                            PreferenceUtils.setPreference(
                                "send_otp_to_customers_to_authenticate_boarding_status",
                                it.sendOtpToCustomersToAuthenticateBoardingStatus
                            )
                            // initTab()
                            // binding.tabsBus.visible()
                        }

                        401 -> {
                            lifecycleScope.launch(Dispatchers.Main) {
                                if (isFragmentSafeForAction()) {
                                   /* DialogUtils.unAuthorizedDialog(
                                        requireContext(),
                                        "${getString(R.string.authentication_failed)}\n\n ${
                                            getString(
                                                R.string.please_try_again
                                            )
                                        }",
                                        this@BookingsFragment
                                    )*/
                                    (activity as BaseActivity).showUnauthorisedDialog()

                                }
                            }
                        }

                        else -> {
                            if (it.result?.message != null) {
                                if (!(requireActivity() as Activity).isFinishing)
                                    requireActivity().toast("${it.result?.message}")
                            } else {
                                if (!(requireActivity() as Activity).isFinishing)
                                    requireActivity().toast(getString(R.string.server_error))
                            }
                        }
                    }
                } else {
                    if (!(requireActivity() as Activity).isFinishing)
                        requireActivity().toast(getString(R.string.server_error))
                }
            }
        }
    }

    private fun setTabTitle(title: String) {
        if (fragmentAdapter != null) {
            binding.tabsBus.getTabAt(0)?.apply {
                (customView as TextView).text = title
            }
        }
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

    private fun isFragmentSafeForAction(): Boolean {
        return isAdded && activity != null && view != null
    }
}

