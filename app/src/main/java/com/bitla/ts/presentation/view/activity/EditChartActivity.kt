package com.bitla.ts.presentation.view.activity

import android.os.Build
import android.os.Bundle
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityEditChartBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.view.fragments.BookedSeatsFragment
import com.bitla.ts.presentation.view.fragments.EmptySeatsFragment
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getDateDMYY
import com.bitla.ts.utils.constants.EDIT_CHART
import com.bitla.ts.utils.constants.EditChart
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_AVAILABLE_ROUTES
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class EditChartActivity : BaseActivity() {
    companion object {
        val tag: String = EditChartActivity::class.java.simpleName
    }

    private var serviceNumber: String = ""
    private lateinit var binding: ActivityEditChartBinding
    private var loginModelPref: LoginModel = LoginModel()
    private var bccId: Int? = 0
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var busType: String? = null


    override fun initUI() {
        binding = ActivityEditChartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()

        firebaseLogEvent(
        this,
        EDIT_CHART,
        loginModelPref.userName,
        loginModelPref.travels_name,
        loginModelPref.role,
        EDIT_CHART,
        EditChart.EDIT_CHART
        )
        // setNetworkConnectionObserver

        setTabs(
            binding.tabs,
            binding.viewpager,
            EmptySeatsFragment(),
            BookedSeatsFragment(),
            getString(
                R.string.empty_seats
            ),
            getString(
                R.string.booked_seats
            )
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarTitle()

        binding.toolbar.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
        getPref()
        // setNetworkConnectionObserver

       /* setTabs(
            binding.tabs,
            binding.viewpager,
            EmptySeatsFragment(),
            BookedSeatsFragment(),
            getString(
                R.string.empty_seats
            ),
            getString(
                R.string.booked_seats
            )
        )*/
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()

        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            busType = result?.bus_type ?: getString(R.string.empty)
            serviceNumber = result?.number ?: getString(R.string.empty)
        }
    }

    private fun setToolbarTitle() {
        val srcDest = "$source-$destination"
        // val subtitle = "${getDateDMYY(travelDate)} - $srcDest  $busType"
        val subtitle = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${getDateDMYY(travelDate)} - $srcDest  $busType"
        else
            "${getDateDMYY(travelDate)} - $srcDest  $busType"
        binding.toolbar.tvCurrentHeader.text = getString(R.string.edit_chart)
        binding.toolbar.toolbarSubtitle.text = subtitle
    }


}