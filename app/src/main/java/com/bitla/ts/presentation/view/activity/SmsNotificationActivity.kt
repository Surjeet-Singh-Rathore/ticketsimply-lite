package com.bitla.ts.presentation.view.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivitySmsNotificationBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.presentation.view.fragments.AllPassengersFragment
import com.bitla.ts.presentation.view.fragments.SelectedPassengersFragment
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getDateDMYY
import com.bitla.ts.utils.sharedPref.PREF_BUS_TYPE
import com.bitla.ts.utils.sharedPref.PREF_CHECKED_PNR
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_AVAILABLE_ROUTES
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.material.tabs.TabLayout

class SmsNotificationActivity : BaseActivity() {
    companion object {
        val tag: String = SmsNotificationActivity::class.java.simpleName
    }

    private var serviceNumber: String = ""
    private var pnrNos: String? = null
    private lateinit var binding: ActivitySmsNotificationBinding
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var busType: String? = null
    private var deptTime: String? = null


    override fun initUI() {
        binding = ActivitySmsNotificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }


        getPrefs()
        // setNetworkConnectionObserver
        //val toolbarSubTitleInfo = "${getDateDMYY(travelDate)} - $source - $destination $busType"
        val toolbarSubTitleInfo = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${getDateDMYY(travelDate)} | $deptTime | $source - $destination | $busType"
        else
            "${getDateDMYY(travelDate)} | $deptTime | $source - $destination | $busType"
        binding.apply {
            toolbar.tvCurrentHeader.text = getString(R.string.send_sms)
            toolbar.toolbarSubtitle.text = toolbarSubTitleInfo
        }

        setTabs(
            binding.tabs,
            binding.viewpager,
            AllPassengersFragment(),
            SelectedPassengersFragment(),
            getString(
                R.string.all_passengers
            ),
            getString(
                R.string.selected_passengers
            )
        )

        openTab()
    }

    private fun openTab() {
        // ----start select tab ----//
        if (pnrNos == null || pnrNos?.isEmpty()!!)
            binding.viewpager.currentItem = 0
        else {
            binding.viewpager.currentItem = 1
            //disableTab(binding.tabs,0)
        }
        // ---- end select tab ----//

    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clickListener()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun getPrefs() {
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()

        if (intent.hasExtra(getString(R.string.bus_type)))
            busType = intent.getStringExtra(getString(R.string.bus_type))
        if (intent.hasExtra(getString(R.string.dep_time)))
            deptTime = intent.getStringExtra(getString(R.string.dep_time))
        if (intent.hasExtra(getString(R.string.service_number)))
            serviceNumber = intent.getStringExtra(getString(R.string.service_number))!!
        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            if (busType == null)
                busType = result?.bus_type ?: getString(R.string.empty)
            if (deptTime == null)
                deptTime = result?.dep_time ?: getString(R.string.empty)
            if (serviceNumber.isEmpty())
                serviceNumber = result?.number ?: getString(R.string.empty)
        } else {
            if (PreferenceUtils.getString(PREF_BUS_TYPE) != null) {
                busType = PreferenceUtils.getString(PREF_BUS_TYPE)
            }
        }


        if (PreferenceUtils.getString(PREF_CHECKED_PNR) != null) {
            pnrNos = PreferenceUtils.getString(PREF_CHECKED_PNR)!!
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.img_back -> onBackPressed()
        }
    }

    private fun disableTab(tabLayout: TabLayout, index: Int) {
        (tabLayout.getChildAt(0) as ViewGroup).getChildAt(index).isEnabled = false
        binding.viewpager.setOnTouchListener { v, event -> true }
    }

}