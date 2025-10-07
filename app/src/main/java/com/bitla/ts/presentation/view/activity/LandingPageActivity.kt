package com.bitla.ts.presentation.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityLandingPageBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.constants.LANDING_PAGE
import com.bitla.ts.utils.constants.LANDING_PAGE_SELECTED
import com.bitla.ts.utils.constants.LandingPage
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import toast
import visible

class LandingPageActivity : BaseActivity() {

    private var privileges: PrivilegeResponseModel? = null
    private lateinit var activityLandingPageBinding: ActivityLandingPageBinding
    private lateinit var radioGroup: RadioGroup
    override fun initUI() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        activityLandingPageBinding = ActivityLandingPageBinding.inflate(layoutInflater)
        activityLandingPageBinding.simpleToolbar.toolbarHeaderText.setText(R.string.landing_page)
        val view = activityLandingPageBinding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(activityLandingPageBinding.root)
        }

         privileges = PreferenceUtils.getPrivilege()
         val currentUser = PreferenceUtils.getLogin()

        if(currentUser.role == getString(R.string.role_field_officer) && privileges?.boLicenses?.showBookingAndCollectionTabInTsApp != null && privileges?.boLicenses?.showBookingAndCollectionTabInTsApp == false && privileges?.country.equals(
                "India", true
            )){
            activityLandingPageBinding.radioBtnBooking.gone()
        }else{
            activityLandingPageBinding.radioBtnBooking.visible()
        }

        if(privileges?.showBusMobilityAppDashboard != null && privileges?.showBusMobilityAppDashboard == false){
            activityLandingPageBinding.radioBtnDashboard.gone()
        }else{
            activityLandingPageBinding.radioBtnDashboard.visible()
        }










            radioGroup = activityLandingPageBinding.radiogroup

        if (getPrivilegeBase()?.availableAppModes?.checkingInspectorMode == true) {
            activityLandingPageBinding.checkingInspectorBT.visible()
        }else{
            activityLandingPageBinding.checkingInspectorBT.gone()

        }



        when (PreferenceUtils.getString(getString(R.string.landing_page)).toString()) {
            getString(R.string.dashboard) -> {
                activityLandingPageBinding.radioBtnDashboard.isChecked = true
            }
            getString(R.string.booking) -> {
                activityLandingPageBinding.radioBtnBooking.isChecked = true
            }
            getString(R.string.pickup_chart) -> {
                activityLandingPageBinding.radioBtnPickupChart.isChecked = true
            }
            getString(R.string.reports) -> {
                activityLandingPageBinding.radioBtnReports.isChecked = true
            }
            getString(R.string.checking_inspector) -> {
                if (getPrivilegeBase()?.availableAppModes?.checkingInspectorMode == true) {
                    PreferenceUtils.putString(
                        getString(R.string.landing_page),
                        getString(R.string.booking)
                    )
                    activityLandingPageBinding.checkingInspectorBT.isChecked = true
                }else{
                    activityLandingPageBinding.radioBtnBooking.isChecked = true
                }
            }
            else -> {
                activityLandingPageBinding.radioBtnBooking.isChecked = true
            }
        }

        if(privileges?.boLicenses?.showBookingAndCollectionTabInTsApp != null && privileges?.boLicenses?.showBookingAndCollectionTabInTsApp == false
            && privileges?.showBusMobilityAppDashboard != null && privileges?.showBusMobilityAppDashboard == false && privileges?.country.equals(
                "India", true
            )) {
            activityLandingPageBinding.radioBtnPickupChart.isChecked = true
            PreferenceUtils.putString(getString(R.string.landing_page),getString(R.string.pickup_chart))
        }


        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {

                R.id.radioBtnDashboard -> {
                    PreferenceUtils.putString(
                        getString(R.string.landing_page),
                        getString(R.string.dashboard)
                    )
                }
                R.id.radioBtnBooking -> {
                    PreferenceUtils.putString(
                        getString(R.string.landing_page),
                        getString(R.string.booking)
                    )
                }
                R.id.radioBtnPickupChart -> {
                    PreferenceUtils.putString(
                        getString(R.string.landing_page),
                        getString(R.string.pickup_chart)
                    )
                }
                R.id.radioBtnReports -> {
                    PreferenceUtils.putString(
                        getString(R.string.landing_page),
                        getString(R.string.reports)
                    )
                }
                R.id.checkingInspectorBT -> {
                    PreferenceUtils.putString(
                        getString(R.string.landing_page),
                        getString(R.string.checking_inspector)
                    )
                }
            }
        }

        firebaseLogEvent(
            this,
            LANDING_PAGE,
            PreferenceUtils.getLogin().userName,
            PreferenceUtils.getLogin().travels_name,
            PreferenceUtils.getLogin().role,
            LANDING_PAGE,
            LandingPage.LANDING_PAGE_SELECTION
        )
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    fun onclickBack(v: View) {
        sendLandingPageName()
    }

    override fun onBackPressed() {
        sendLandingPageName()
    }

    private fun sendLandingPageName(){

        var landingPageKey = PreferenceUtils.getString(getString(R.string.landing_page)).toString()
        if (landingPageKey.isEmpty()) {
            landingPageKey = getString(R.string.bookings)
        }

        firebaseLogEvent(
            this,
            LANDING_PAGE_SELECTED,
            PreferenceUtils.getLogin().userName,
            PreferenceUtils.getLogin().travels_name,
            PreferenceUtils.getLogin().role,
            LANDING_PAGE_SELECTED,
            landingPageKey
        )

        val intent = Intent()
        intent.putExtra(getString(R.string.landing_page_key), landingPageKey)
        setResult(RESULT_OK, intent)
        finish()
    }
}