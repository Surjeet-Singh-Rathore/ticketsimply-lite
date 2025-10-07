package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.pickup_chart_crew_details
import com.bitla.ts.databinding.ActivityBusTrackingBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.view.fragments.BusTrackingFragment
import com.bitla.ts.presentation.viewModel.BusTrackingViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.routeId
import com.bitla.ts.utils.constants.CALL_PHONE_PERMISSION
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_AVAILABLE_ROUTES
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import isNetworkAvailable
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import java.net.URLEncoder

class BusTrackingActivity : BaseActivity(), DialogSingleButtonListener {

    private var shareTextWhatsapp: String? = null
    private var crewContactNumber: String? = null
    private lateinit var binding: ActivityBusTrackingBinding
    private val busTrackingViewModel by viewModel<BusTrackingViewModel<Any?>>()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var loginModelPref: LoginModel = LoginModel()
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var locale: String? = null
    private var resID: Long? = 0
    private var serviceData= Result()
    override fun initUI() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shareTextWhatsapp = getString(R.string.share_bus_location_text)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        getIntentData()

        setToolbarTitle()

        getPref()

        setTabs(
            binding.tabsBusTracking,
            binding.viewpager,
            BusTrackingFragment(),
            BusTrackingFragment(),
            getString(
                R.string.tracking
            ),
           ""
        )

        clickListener()
        crewDetailsObserver()
    }

    private fun getIntentData() {
        if (intent.hasExtra("toolbarSubHeader"))
            intent.getStringExtra("toolbarSubHeader")?.let { busTrackingViewModel.setSrcDest(it) }
        if (intent.hasExtra("coachNumber"))
            intent.getStringExtra("coachNumber")?.let { busTrackingViewModel.setCoachNumber(it) }
        if (intent.hasExtra("serviceNumber"))
            intent.getStringExtra("serviceNumber")?.let { busTrackingViewModel.setServiceNumber(it) }
        if (intent.hasExtra("deptDateTime"))
            intent.getStringExtra("deptDateTime")?.let { busTrackingViewModel.setDepartureDateAndTime(it) }
    }

    private fun setToolbarTitle() {
        if (!busTrackingViewModel.srcDest.value.isNullOrEmpty())
            binding.toolbar.toolbarHeaderText.text = busTrackingViewModel.srcDest.value
    }

    private fun getPref() {
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
        resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
        }

        if(intent.hasExtra("routeId")){
            busTrackingViewModel.routeId.value= (intent.extras?.get("routeId") ?:"").toString()
            busTrackingViewModel.apiTravelId.value=PreferenceUtils?.getString("apiTravelId")?:""
        }

        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            serviceData =
                PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)!!
            busTrackingViewModel.serviceTravelDate.value=serviceData.dep_date
            busTrackingViewModel.deptTime.value=serviceData.dep_time
            busTrackingViewModel.serviceBusType.value=serviceData.bus_type
            busTrackingViewModel.origin.value=serviceData.origin
            busTrackingViewModel.destination.value=serviceData.destination
        }



    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener(this)
        binding.toolbar.imgCrew.setOnClickListener(this)
        binding.toolbar.imgShare.setOnClickListener(this)
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.imgBack -> onBackPressedDispatcher.onBackPressed()
            R.id.imgCrew -> {
                if (isNetworkAvailable())
                    callCrewDetailsApi()
                else
                    noNetworkToast()
            }

            R.id.imgShare -> {
             shareCurrentLocation()
            }
        }
    }

    private fun shareCurrentLocation() {
        val uri = "https://www.google.com/maps/?q=${busTrackingViewModel.busLocation.value?.data?.lat?:0.0},${busTrackingViewModel.busLocation.value?.data?.long?:0.0}"
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share_bus_location_text)+" "+ uri)
        startActivity(Intent.createChooser(sharingIntent, "Share in..."))
    }

    private fun callCrewDetailsApi() {

        pickUpChartViewModel.pickupChartCrewDetailsApi(
            apiKey = loginModelPref.api_key,
            reservationId = resID.toString(),
            apiType = pickup_chart_crew_details,
            locale = locale.toString()
        )
    }

    private fun crewDetailsObserver() {

        pickUpChartViewModel.pickupChartCrewDetailsResponse.observe(this) { it ->
            if (it != null) {
                when (it.code) {
                    200 -> {
                        DialogUtils().dialogCrewDetails(
                            this,
                            it,
                            privilegeResponseModel = privilegeResponseModel,
                            callUserCallback = { mobileNumber, isCall ->
                                crewContactNumber = mobileNumber
                                if (isCall)
                                    callUser()
                                else
                                    shareOnWhatsApp()
                            })

                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    else -> {
                        it.result?.message?.let { it1 -> toast(it1) }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun callUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_PERMISSION)
        }
    }

    private fun shareOnWhatsApp() {
        try {
            val i = Intent(Intent.ACTION_VIEW)

            if (!crewContactNumber.isNullOrEmpty()) {
                val url =
                    "https://api.whatsapp.com/send?phone=+$crewContactNumber&text=" + URLEncoder.encode(
                        shareTextWhatsapp,
                        "UTF-8"
                    )
                i.setPackage(packageName)
                i.data = Uri.parse(url)
                startActivity(i)
            } else
                toast(getString(R.string.number_not_registered))
        } catch (ex: ActivityNotFoundException) {
            toast(getString(R.string.whatsapp_is_not_installed))
        } catch (e: java.lang.Exception) {
            Timber.d("ExceptionMsg ${e.printStackTrace()}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_PHONE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!crewContactNumber.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$crewContactNumber"))
                        startActivity(intent)
                    } else {
                        toast(this.getString(R.string.error_occured))
                    }
                } else
                    toast(getString(R.string.call_permission_denied))
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent =
                Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}