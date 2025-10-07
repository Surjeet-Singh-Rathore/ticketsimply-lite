package com.bitla.ts.presentation.view.activity.reservationOption.extendedFare

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityUpdateRateCardBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.presentation.adapter.UpdateRateCardAdapter
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.sharedPref.PREF_BUS_TYPE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.material.tabs.TabLayout
import gone
import toast

class UpdateRateCardActivity : BaseActivity() {
    private var isMultiHopService: Boolean = false
    private lateinit var binding: ActivityUpdateRateCardBinding
    private var tabsList: MutableList<Tabs> = mutableListOf()
    private var originName: String? = null
    private var destinationName: String? = null
    private var busType: String? = null
    private var currentCountry: String? = ""
    private var isHideCommissionTab: Boolean? = false
    
    
    override fun isInternetOnCallApisAndInitUI() {
    }

    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityUpdateRateCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        val privileges = getPrivilegeBase()
        if (!privileges?.country.isNullOrEmpty()) {
            currentCountry = privileges?.country
        }

        if (privileges?.hideCommissionAndTieupCommissionInRouteLevel!=null) {
            isHideCommissionTab = privileges.hideCommissionAndTieupCommissionInRouteLevel
        }
        isMultiHopService = intent.getBooleanExtra(getString(R.string.is_multi_hop_service),false)

        initTab()
        binding.updateRatecardToolbar.imageOptionLayout.gone()

        PreferenceUtils.removeKey("fromBusDetails")

        if (intent.getStringExtra(getString(R.string.origin)) != null) {
            originName = intent.getStringExtra(getString(R.string.origin))
        }


        if (intent.getStringExtra(getString(R.string.destination)) != null) {
            destinationName = intent.getStringExtra(getString(R.string.destination))
        }

        if (intent.getStringExtra(getString(R.string.bus_type)) != null) {
            busType = intent.getStringExtra(getString(R.string.bus_type))
        }

        PreferenceUtils.setPreference(PREF_BUS_TYPE, busType)

//        binding.updateRatecardToolbar.textHeaderTitle.text = "$originName - $destinationName"
        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.modify_reservation)
        binding.updateRatecardToolbar.headerTitleDesc.text = busType

        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            viewModelStore.clear()
            onBackPressed()
        }
    }

    private fun initTab() {
        val tabFare = Tabs()
        tabFare.title = getString(R.string.fare)
        tabsList.add(tabFare)

        val tabTime = Tabs()
        tabTime.title = getString(R.string.time)
        tabsList.add(tabTime)

        if (isHideCommissionTab == false) {
            val tabCommission = Tabs()
            tabCommission.title = getString(R.string.commission)
            tabsList.add(tabCommission)
        }

        // Optional additional tabs (currently commented)
        /*
        val tabMultistation = Tabs()
        tabMultistation.title = getString(R.string.multi_station)
        tabsList.add(tabMultistation)

        val tabSeatwise = Tabs()
        tabSeatwise.title = getString(R.string.seat_wise)
        tabsList.add(tabSeatwise)
        */

        // Set tab mode to FIXED or SCROLLABLE depending on your design
        binding.tabsPickup.tabMode = TabLayout.MODE_FIXED
        // binding.tabsPickup.tabMode = TabLayout.MODE_SCROLLABLE // uncomment if many tabs

        val fragmentAdapter = UpdateRateCardAdapter(
            this,
            tabsList,
            this.supportFragmentManager,
            currentCountry,
            isHideCommissionTab,
            isMultiHopService
        )

        binding.viewpagerPickup.adapter = fragmentAdapter
        binding.tabsPickup.setupWithViewPager(binding.viewpagerPickup)

        // Custom tabs with single-line text
        for (i in 0 until binding.tabsPickup.tabCount) {
            val tab = binding.tabsPickup.getTabAt(i)
            val tabTextView = TextView(this)

            tabTextView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            tabTextView.text = tab?.text
            tabTextView.maxLines = 1
            tabTextView.setSingleLine(true)
            tabTextView.ellipsize = TextUtils.TruncateAt.END
            tabTextView.gravity = Gravity.CENTER
            tabTextView.setPadding(16, 8, 16, 8)
            tabTextView.textSize = 14f

            // Optional: different font style for first/second tab
            if (i == 0) {
                tabTextView.setTypeface(null, Typeface.BOLD)
            } else {
                tabTextView.setTypeface(null, Typeface.NORMAL)
            }

            tab?.customView = tabTextView
        }

        binding.tabsPickup.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewpagerPickup.currentItem = tab!!.position
                val text: TextView = tab.customView as TextView
                text.setTypeface(null, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text: TextView = tab?.customView as TextView
                text.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Optional: handle reselection if needed
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
//        PreferenceUtils.removeKey("seatwiseFare")
        PreferenceUtils.removeKey("isEditSeatWise")
        PreferenceUtils.removeKey("PERSEAT")
        PreferenceUtils.removeKey("fromBusDetails")
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_resId))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_origin))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_destination))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_originId))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_destinationId))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_travelDate))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_busType))
    }
}