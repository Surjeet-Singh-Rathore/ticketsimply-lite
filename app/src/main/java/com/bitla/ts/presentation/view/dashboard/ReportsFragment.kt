package com.bitla.ts.presentation.view.dashboard

import android.annotation.*
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
import com.bitla.ts.utils.constants.Report.REPORT_ICON
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.isActivityIsLive
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.tabs.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.*
import toast
import java.time.*

class ReportsFragment : BaseFragment(), DialogSingleButtonListener {

    private lateinit var binding: FragmentReportsBinding
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val privilegeDetailsViewModel by sharedViewModel<PrivilegeDetailsViewModel>()
    private var locale: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun isInternetOnCallApisAndInitUI() {
        if(isAdded){
            requireActivity().isActivityIsLive {
                if (checkIfFragmentAttached(this, requireContext())) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        getPref()
                    }
                }
            }
        }

    }

    override fun isNetworkOff() {
        
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
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportsBinding.inflate(inflater, container, false)
        val view: View = binding.root

        if (checkIfFragmentAttached(this, requireContext())) {
            viewLifecycleOwner.lifecycleScope.launch {
                getPref()
                firebaseLogEvent(
                requireContext(),
                REPORT,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                REPORT,
                    REPORT_ICON
                )
                initTab()
                setPrivilegesObserver()
            }
        }

        return view
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
    }

    @SuppressLint("LogNotTimber")
    private fun initTab() {
        val tabsList: MutableList<Tabs> = mutableListOf()

        val tabFavorites = Tabs()
        tabFavorites.title = getString(R.string.favourites)
        tabsList.add(tabFavorites)

        val tabAllReports = Tabs()
        tabAllReports.title = getString(R.string.all_reports)
        tabsList.add(tabAllReports)

        val fragmentAdapter = ReportsPagerAdapter(
            context = requireContext(),
            tabList = tabsList,
            fm = requireActivity().supportFragmentManager
        )
        binding.viewpagerReport.adapter = fragmentAdapter
        binding.tabsBus.setupWithViewPager(binding.viewpagerReport)
        // custom tabs

        for (i in 0..binding.tabsBus.tabCount.minus(3)) {
            val tab = binding.tabsBus.getTabAt(i)!!
            tab.customView = null
            //tab!!.customView = fragmentAdapter.getTabView(i)

            val tabTextView = TextView(activity)

            tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            tabTextView.text = tab.text
            tabTextView.setTextColor(Color.BLACK)
            tab.customView = tabTextView
            
/*            if (i == 0) {
                // This set the font style of the first tab
                tabTextView.setTypeface(null, Typeface.BOLD)

            }
            if (i == 1) {
                // This set the font style of the second tab
                tabTextView.setTypeface(null, Typeface.NORMAL)
            }*/
        }
        binding.tabsBus.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewpagerReport.currentItem = tab!!.position
                if (tab.customView != null) {
                    tab.customView as TextView?

                    //text?.setTypeface(null, Typeface.BOLD)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab?.customView != null) {
                    val text: TextView = tab.customView as TextView
                    text.setTextColor(Color.BLACK)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    fun setData(it:PrivilegeResponseModel){
        PreferenceUtils.setPreference("otp_validation_time",it.configuredLoginValidityTime)

        PreferenceUtils.setPreference("send_qr_code_to_customers_to_authenticate_boarding_status",
            it.sendQrCodeToCustomersToAuthenticateBoardingStatus
        )
        PreferenceUtils.setPreference("send_otp_to_customers_to_authenticate_boarding_status",
            it.sendOtpToCustomersToAuthenticateBoardingStatus
        )
    }

    private fun setPrivilegesObserver() {
        privilegeDetailsViewModel.privilegeResponseModel.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                     //   PreferenceUtils.putObject(it, PREF_PRIVILEGE_DETAILS)
                        PreferenceUtils.putObject(LocalDateTime.now(), PREF_PRIVILEGE_DETAILS_CALLED)
                        setData(it)
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    else -> {
                        requireActivity().toast("${it.result.message}")
                    }
                }
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
}