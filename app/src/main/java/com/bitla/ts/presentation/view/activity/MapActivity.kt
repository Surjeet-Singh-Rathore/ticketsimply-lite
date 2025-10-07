package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.animation.*
import android.annotation.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.graphics.*
import android.graphics.drawable.*
import android.graphics.drawable.shapes.*
import android.location.*
import android.location.Location
import android.net.*
import android.os.*
import android.provider.*
import android.speech.tts.*
import android.view.*
import android.view.animation.*
import android.widget.*
import androidx.core.app.*
import androidx.core.content.*
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.*
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.BpDpService.request.*
import com.bitla.ts.domain.pojo.BpDpService.request.ReqBody
import com.bitla.ts.domain.pojo.BpDpService.response.*
import com.bitla.ts.domain.pojo.BpDpService.response.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.update_boarded_status.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.*
import com.google.android.material.internal.*
import com.google.android.material.snackbar.*
import com.google.gson.*
import com.google.maps.android.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import okhttp3.*
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.util.*


class MapActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
    OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    DialogSingleButtonListener, OnItemPassData, TextToSpeech.OnInitListener {


    private lateinit var mMap: GoogleMap
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var oldLatitude: Double = 0.0
    private var oldLongitude: Double = 0.0

    private var stationList = mutableListOf<Result>()
    private var fixedStationList = mutableListOf<LatLng>()
    private var tempStationList = mutableListOf<Result>()

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = ""
    private val agentAccountInfoViewModel by viewModel<AgentAccountInfoViewModel<Any>>()
    private var resId: Long? = null
    private var stationClose = true
    private var stationNearby = true
    private var stationReached = false
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()

    private lateinit var switch: Switch
    private var statusChanged = false
    private var removedStation = arrayListOf<String>()
    private var firstTime = false
    var mapline: ArrayList<Polyline> = arrayListOf()
    var stationPath: ArrayList<ArrayList<LatLng>> = arrayListOf()
    var stationLine: ArrayList<Polyline> = arrayListOf()
    private var updatePassenger = false

    private var pathList = mutableListOf<LatLng>()
    private var tempPathList = mutableListOf<LatLng>()


    private var tts: TextToSpeech? = null
    private var message: String? = null


    private lateinit var binding: ActivityMapBinding

    private var foregroundOnlyLocationServiceBound = false

    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null

    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver
    private var dialogVisible = false

    private var updatePassengerList = false
    private var fromMapMarker = false
    private lateinit var markerDetails: Marker
    private var markerDetailsList: ArrayList<Marker> = arrayListOf()
    lateinit var lm: LocationManager
    var gps_enabled = false
    var network_enabled = false
    var currentLocationMArkerAdded = false
    val currentLocationRoute = false
    var fromContinue = false
    var mapCurrentPlot = false
    var count = 0
    var markerPosition = -1
    var newRoute = false
    var lastStationId = ""
    private var privilegeResponse: PrivilegeResponseModel? = null

    lateinit var currentMaker: Marker

    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }


    override fun initUI() {
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                      showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            agentAccountInfoViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                      showToast(it)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )

        getPref()
        bpDpObserver()
        updateBoardedStatusObserver()
        btnOnClicks()
        callApi(true, true)



        if (!PreferenceUtils.getString("mapRoute").isNullOrEmpty()) {
            alreadyInJourney()
        }

        BottomSheetBehavior.from(binding.sheet).apply {
            peekHeight = 230
            this.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }

    override fun isInternetOnCallApisAndInitUI() {
        callApi(true, true)
    }


    private fun callApi(isBpDp: Boolean, isFirst: Boolean) {
        if (isBpDp) {
            if (isNetworkAvailable()) {
                firstTime = isFirst
                bpDpServiceApi()
            } else
                noNetworkToast()
        } else {

        }

    }

    private fun btnOnClicks() {
        if (foregroundPermissionApproved()) {
            foregroundOnlyLocationService?.subscribeToLocationUpdates()
                ?: Timber.d("Service Not Bound")
            binding.btnStart.setOnClickListener(this)
        } else {
            requestForegroundPermissions()
        }
        binding.btnNavigate.setOnClickListener(this)

    }


    private fun alreadyInJourney() {
        Timber.d("mapFlowCheck::3")
        fromContinue = true
        firstTime = false
        currentLocationMArkerAdded = false
        PreferenceUtils.putString("mapRoute", "$resId")
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.btnStart -> {

                lm = this.getSystemService(LOCATION_SERVICE) as LocationManager
                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                } catch (ex: java.lang.Exception) {
                }
                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                } catch (ex: java.lang.Exception) {
                }

                if (!gps_enabled && !network_enabled) {
                    // notify user
                    AlertDialog.Builder(this)
                        .setMessage("Gps Not Enabled")
                        .setPositiveButton(
                            "",
                            DialogInterface.OnClickListener(fun(
                                paramDialogInterface: DialogInterface?,
                                paramInt: Int
                            ) {
                                this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            })
                        )
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()

                } else {
                    if (binding.btnStart.text == getString(R.string.start)) {
                        tts = TextToSpeech(this, this)
                        message = getString(R.string.start_Trip)
                        foregroundOnlyLocationService?.subscribeToLocationUpdates()
                        PreferenceUtils.putString("mapRoute", "$resId")
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        binding.btnStart.text = getString(R.string.stop)
                        tempStationList.clear()
                        binding.btnNavigate.visible()
                        binding.stationParentLayout.visible()
                        bpDpServiceApi()
                    } else {
                        //    deactivateForeGroundPermission()
                        binding.btnStart.text = getString(R.string.start)
                        val emptyArray = arrayListOf<String>()
                        PreferenceUtils.putReachedStationList(emptyArray)
                        Timber.d("prefrenceCheck:3${PreferenceUtils.getString("mapRoute")} ")
                        tts = TextToSpeech(this, this)
                        message = getString(R.string.stop_trip)
                        binding.currentServiceName.visible()
                        PreferenceUtils.removeKey("mapRoute")
                        binding.stationParentLayout.gone()
                        toast("Tracking service Stopped")
                        binding.btnNavigate.gone()

                    }
                }

            }
            R.id.btn_navigate -> {
                if (tempStationList.isNotEmpty() && tempStationList.size > 0) {
                    val listSize = tempStationList.size
                    val srcLat = currentLatitude
                    val srcLong = currentLongitude
                    val destLat = tempStationList[listSize.minus(1)].latitude
                    val destLong = tempStationList[listSize.minus(1)].longitude

                    var wayPoints = ""
                    if (listSize > 1) {
                        for (i in 0..tempStationList.size.minus(2)) {
                            wayPoints += "${tempStationList[i].latitude},${tempStationList[i].longitude}|"
                        }
                        wayPoints = wayPoints.removeSuffix("|")
                    }


                    if (listSize == 1) {
                        val gmmIntentUri =
                            Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$srcLat,$srcLong&destination=$destLat,$destLong&travelmode=driving")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                    } else {
                        val gmmIntentUri =
                            Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$srcLat,$srcLong&destination=$destLat,$destLong&waypoints=$wayPoints&travelmode=driving")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                    }

                    // val gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=18.519513,73.868315&destination=18.518496,73.879259&waypoints=18.520561,73.872435|18.519254,73.876614&travelmode=driving")


                }
            }
        }
    }


    private fun containLocation(polyLatLang: LatLng, list: List<LatLng>): Boolean {
        return PolyUtil.isLocationOnPath(polyLatLang, list, true, 100.0)
//        return PolyUtil.containsLocation(polyLatLang, list,geodisc)
    }
//fun index(polyLatLang: LatLng, list:List<LatLng>):Int{
//        return  PolyUtil.locationIndexOnPath(polyLatLang, list,true)

//        return PolyUtil.containsLocation(polyLatLang, list,geodisc)
//    }

    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()
        if (provideRationale) {
            Snackbar.make(
                binding.root,
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(com.bitla.tscalender.R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            Timber.d("Request foreground only permission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    private fun getKmFromLatLong(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        try {
            val loc1 = Location("")
            loc1.latitude = lat1
            loc1.longitude = lng1
            val loc2 = Location("")
            loc2.latitude = lat2
            loc2.longitude = lng2
            val distanceInMeters = loc1.distanceTo(loc2)
            return distanceInMeters.toDouble()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return 0.0
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.d("onRequestPermissionResult")

        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    Timber.d("User interaction was cancelled.")

                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()

                else -> {
                    // Permission denied.
                    updateButtonState(false)

                    Snackbar.make(
                        binding.root,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onStart() {
        super.onStart()

        PreferenceUtils.getPreference(KEY_FOREGROUND_ENABLED, false)?.let {
            updateButtonState(
                it
            )
        }

        val serviceIntent = Intent(this, ForegroundOnlyLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        Timber.d("mapFlowCheck::2")
        if (!PreferenceUtils.getString("mapRoute").isNullOrEmpty()) {
            Timber.d("mapFlowCheck::2.1")

            foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

            LocalBroadcastManager.getInstance(this).registerReceiver(
                foregroundOnlyBroadcastReceiver,
                IntentFilter(
                    ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
                )
            )
            foregroundOnlyLocationService?.subscribeToLocationUpdates()
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            binding.btnStart.text = getString(R.string.stop)
            binding.btnNavigate.visible()
        } else {
            binding.btnStart.text = getString(R.string.start)
            binding.btnNavigate.gone()
        }
    }

    private fun activateForeGroundPermission() {
        foregroundOnlyLocationService?.subscribeToLocationUpdates()
    }

    private fun deactivateForeGroundPermission() {
        foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
    }


    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }

        super.onStop()
    }

    private fun updateButtonState(trackingLocation: Boolean) {
        if (trackingLocation) {
            val a = getString(R.string.stop_location_updates_button_text)
        } else {
            val b = getString(R.string.start_location_updates_button_text)
        }
    }

    private fun logResultsToScreen(output: Location?) {
        output?.let { getCurrentLocation(it) }
    }

    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                ForegroundOnlyLocationService.EXTRA_LOCATION
            )

            if (location != null) {
                logResultsToScreen(location)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Updates button states if new while in use location is added to SharedPreferences.
        if (key == KEY_FOREGROUND_ENABLED) {
            updateButtonState(
                sharedPreferences?.getBoolean(
                    KEY_FOREGROUND_ENABLED, false
                ) == true
            )
        }
    }

    private fun bpDpServiceApi() {
        val agentRequest = BpDpServiceRequest(
            bccId.toString(),
            format_type,
            bp_dp_service,
            ReqBody(
                loginModelPref.api_key,
                resId.toString(),
                true,
                "json",
                locale!!
            )
        )
        agentAccountInfoViewModel.newBpDpService(
            resId.toString(),
            loginModelPref.api_key,
            bp_dp_service
        )
    }

    @SuppressLint("MissingPermission")
    private fun bpDpObserver() {
        agentAccountInfoViewModel.bpDpService.observe(this) {
            Timber.d("mapApiResponse $it")
            if (it != null) {
                if (it.code == 200) {

                    if (updatePassengerList) {
                        val list = PreferenceUtils.getReachedStationList()
                        markerClick(it)
                    } else if (updatePassenger) {
                        for (i in 0..it.result.size.minus(1)) {
                            if (tempStationList[0].id == it.result[i].id) {
                                count = it.result[i].passenger_details.size
                            }
                        }
                        updatePassenger = false
                    } else {
                        statusChanged = false
                        mMap.isMyLocationEnabled = false
                        mMap.uiSettings.apply {
                            isMyLocationButtonEnabled = false
                            isMapToolbarEnabled = true
                        }
                        mMap.setOnMyLocationButtonClickListener(this)
                        stationList.clear()
                        tempStationList.clear()
                        it.result.forEach {
                            if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty() && it.latitude.toDouble() != 0.0 && it.longitude.toDouble() != 0.0) {
                                stationList.add(it)
                                lastStationId = it.id.toString()

                                fixedStationList.add(
                                    LatLng(
                                        it.latitude.toDouble(),
                                        it.longitude.toDouble()
                                    )
                                )
                            }
                        }
                        if (binding.currentCityName.text == getString(R.string.notAvailable)) {
                            if (!stationList[0].city.isNullOrEmpty()) {
                                binding.currentCityName.text = stationList[0].city
                            }
                        }
                        count = it.result[0].passenger_details.size
                        binding.currentCityName.text = it.result[0].city
                        binding.currentCityName.text = it.result[0].city
                        tempStationList.addAll(stationList)

                        fixedStationList.distinctBy { it }

                        if (stationList.size < 1) {
                            binding.sheet.gone()
                            toast("no Stations to show")
                        }

                        if (fromContinue) {
                            var tempStation = arrayListOf<Result>()

                            tempStation.addAll(tempStationList)

                            fromContinue = false
                            if (!PreferenceUtils.getReachedStationList().isNullOrEmpty()) {
                                val list = PreferenceUtils.getReachedStationList()
                                tempStationList.forEach {
                                    for (i in 0..list!!.size.minus(1)) {
                                        Timber.d("continueList: ${tempStationList.size}")

                                        if (it.id.toString() == list[i]) {
                                            tempStation.remove(it)
                                        }
                                    }
                                }
                                tempStationList.clear()
                                tempStationList = tempStation
                                Timber.d("continueList: ${tempStationList.size}")
                                addOnlyMarker()

                            } else {
                                addOnlyMarker()
                            }


                            activateForeGroundPermission()
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    fixedStationList[0],
                                    16F
                                )
                            )


                        }
                        if (firstTime) {
                            Timber.d("mapFlowCheck::0.1.2")
                            firstTime = false
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    fixedStationList[0],
                                    16F
                                )
                            )
                            addOnlyMarker()
                        }


                        mMap.setOnMarkerClickListener { marker ->
                            updatePassengerList = true
                            fromMapMarker = true
                            markerDetails = marker
                            bpDpServiceApi()
                            true
                        }
                    }
                }
            }
        }
    }

    private fun visibleDialogue(tempList: ArrayList<PassengerDetail>) {
        DialogUtils.mapPassengerList(this, tempList, this, this,privilegeResponse)
        dialogVisible = true
    }

    private fun markerClick(it: BpDpServiceResponse) {
        updatePassengerList = false
        var tempList = arrayListOf<PassengerDetail>()

        if (fromMapMarker) {
            fromMapMarker = false
            it.result.forEach {
                Timber.d("passengerListSize::${markerDetails.position} == ${it.latitude},${it.longitude}")
                if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty() && it.latitude.toDouble() != 0.0 && it.longitude.toDouble() != 0.0) {
                    if (markerDetails.position.latitude == it.latitude.toDouble()
                        && markerDetails.position.longitude == it.longitude.toDouble()
                    ) {
                        tempList.addAll(it.passenger_details)
                    }
                }
            }
        } else {

            it.result.forEach {
                if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty() && it.latitude.toDouble() != 0.0 && it.longitude.toDouble() != 0.0) {
                    if (markerDetailsList[markerPosition].position.latitude == it.latitude.toDouble()
                        && markerDetailsList[markerPosition].position.longitude == it.longitude.toDouble()
                    ) {
                        tempList.addAll(it.passenger_details)
                    }
                }
            }
            tts = TextToSpeech(this, this)
            message =
                "${getString(R.string.there_are)} ${tempList.size} ${getString(R.string.passenger_to_board_at)} ${tempStationList[markerPosition].name}"
        }
        binding.currentStationPassCount.text = tempList.size.toString()

        visibleDialogue(tempList)
        true
    }


    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0)!!
        if (PreferenceUtils.getPreference(PREF_CURRENT_LATITUDE, 0.0) != null)
            currentLatitude = PreferenceUtils.getPreference(PREF_CURRENT_LATITUDE, 0.0)!!
        if (PreferenceUtils.getPreference(PREF_CURRENT_LONGITUDE, 0.0) != null)
            currentLongitude = PreferenceUtils.getPreference(PREF_CURRENT_LONGITUDE, 0.0)!!
        if (PreferenceUtils.getPreference(PREF_MAP_COACH, "") != null)
            binding.currentServiceName.text = PreferenceUtils.getPreference(PREF_MAP_COACH, "")!!
        if (getPrivilegeBase() != null) {
            privilegeResponse = getPrivilegeBase()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (currentLatitude != 0.0 && currentLongitude != 0.0) {
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        currentLatitude,
                        currentLongitude
                    ), 14F
                )
            )
        }

    }

    override fun onMyLocationButtonClick(): Boolean {
        //val isLocationEnable = getCurrentLocation()

//        if (isLocationEnable) {
//            binding.hintTextView.animate()?.alpha(0f)?.duration = 1500
//            lifecycleScope.launch {
//                delay(2500)
//                binding.hintTextView.gone()
//                binding.startButton.visible()
//            }
//        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getCurrentLocation(location: Location) {
        if (PreferenceUtils.getString("notificationReceived") == "true") {
            PreferenceUtils.putString("notificationReceived", "false")
            updatePassenger = true
            Handler(Looper.getMainLooper()).postDelayed({
                bpDpServiceApi()

            }, 2000)

        }

        if (fromContinue) {
            fromContinue = false
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude,
                        location.longitude
                    ), 16F
                )
            )
        }

        if (isLocationEnabled()) {
            if (location != null) {

                currentLatitude = location.latitude
                currentLongitude = location.longitude
                val latLng = LatLng(currentLatitude, currentLongitude)
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                if (tempStationList.isNotEmpty()) {
                    var latLong = LatLng(
                        tempStationList[0].latitude.toDouble(),
                        tempStationList[0].longitude.toDouble()
                    )
//                    getEta(location, latLong)
                }
                addCurrentLocationMarker()

            }
        } else {
            toast("Turn on location")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    fun getTextDrawable(): Drawable {
        val shape: Shape = object : Shape() {
            @SuppressLint("RestrictedApi")
            override fun draw(canvas: Canvas, paint: Paint) {
                paint.setColor(Color.BLUE)
                paint.textSize = 100F
                val radii =
                    ViewUtils.dpToPx(this@MapActivity, 3).toInt()
                canvas.drawText(
                    "Hello Canvas",
                    (canvas.width - 150).toFloat(),
                    (canvas.height / 2).toFloat(),
                    paint
                )
                canvas.drawCircle(
                    (canvas.width - radii * 2).toFloat(),
                    (canvas.height / 2 - radii).toFloat(),
                    radii.toFloat(),
                    paint
                )
            }
        }
        shape.height
        val drawable: Drawable = ShapeDrawable(shape)
        
        
        return drawable
    }

    fun animateTheCarBetweenTheLatLng(
        startPosition: LatLng,
        endPosition: LatLng,
        mMap: GoogleMap?,
        marker: Marker
    ) {
        if (startPosition == endPosition) return
//        Timber.d(TAG, "animateTheCarBetweenTheLatLng: $startPosition, $endPosition")
        val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 3000
        valueAnimator.interpolator = LinearInterpolator()
//        marker?.rotation = getBearing(startPosition, endPosition)
//        Timber.d(
//            TAG,
//            "animateTheCarBetweenTheLatLng: $startPosition,   $endPosition,   ${marker?.rotation}"
//        )
        valueAnimator.addUpdateListener {
//            Timber.d(TAG, "animateTheCarBetweenTheLatLng: $startPosition, $endPosition")
            val v = valueAnimator.animatedFraction
            val lng = v * endPosition.longitude + (1 - v) * startPosition.longitude
            val lat = v * endPosition.latitude + (1 - v) * startPosition.latitude
            val newPos = LatLng(lat, lng)
            marker.position = newPos
            marker.setAnchor(0.5f, 0.5f)

//            val animateCameraPosition = CameraUpdateFactory.newCameraPosition(
//                CameraPosition.Builder().target(newPos)
//                    .zoom(16f).build()
//            )
//            mMap?.animateCamera(
//                animateCameraPosition
//            )
        }
        valueAnimator.start()
    }

    private fun addCurrentLocationMarker() {
        try {
            var distance = 0.0
            val currentLocation = LatLng(currentLatitude, currentLongitude)
            mMap.animateCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        currentLatitude,
                        currentLongitude
                    )
                ),
            )
            if (currentLocation.latitude != 0.0 && currentLocation.longitude != 0.0) {

                var tempList = arrayListOf<PassengerDetail>()
                if (!tempStationList.isNullOrEmpty() && tempStationList[0].latitude != null && tempStationList[0].longitude != null) {
                    Timber.d("check flow:: 0")
                    if (!tempStationList[0].city.isNullOrEmpty()) {
                        binding.currentCityName.text = tempStationList[0].city
                    }
                    val latLang = LatLng(
                        tempStationList[0].latitude.toDouble(),
                        tempStationList[0].longitude.toDouble()
                    )
                    if (!currentLocationMArkerAdded) {
                        currentLocationMArkerAdded = true
                        currentMaker = mMap.addMarker(
                            MarkerOptions()
                                .position(currentLocation)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_img))
                        )!!
                    }

                    if (oldLatitude == 0.0) {
                        oldLatitude = currentLatitude
                        oldLongitude = currentLongitude
                    }

//                    Timber.d("distance_check::","${}")


                    if (!mapCurrentPlot) {
                        mapCurrentPlot = true
                        val urll = getDirectionURL(
                            currentLocation,
                            latLang,
                            getGoogleMapKey(this)
                        )
                        if (isNetworkAvailable())
                            getcurrentdirection(urll).execute()
                    } else {
                        if (oldLatitude != 0.0 && oldLongitude != 0.0) {
                            val startLat = LatLng(oldLatitude, oldLongitude)
                            val endLat = LatLng(currentLatitude, currentLongitude)
//                            if (getKmFromLatLong(startLat.latitude,startLat.longitude, oldLongitude,oldLongitude)>50){
//                                Timber.d("distance_check::","${getKmFromLatLong(startLat.latitude,startLat.longitude, oldLongitude,oldLongitude)}")
                            animateTheCarBetweenTheLatLng(startLat, endLat, mMap, currentMaker)

//                            }
//                            Timber.d("distance_check::","${containLocation(LatLng(currentLatitude,currentLongitude),pathList)}")
                            if (!containLocation(
                                    LatLng(currentLatitude, currentLongitude),
                                    pathList
                                )
                            ) {
                                val urll = getDirectionURL(
                                    currentLocation,
                                    latLang,
                                    getGoogleMapKey(this)
                                )
                                if (isNetworkAvailable())
                                    getcurrentdirection(urll).execute()


                            }

                            oldLatitude = currentLatitude
                            oldLongitude = currentLongitude
                        }

                    }
                    for (i in tempStationList.indices) {
                        if (getKmFromLatLong(
                                currentLocation.latitude,
                                currentLocation.longitude,
                                tempStationList[i].latitude.toDouble(),
                                tempStationList[i].longitude.toDouble()
                            ) < 200
                        ) {
                            markerPosition = i
                            distance = getKmFromLatLong(
                                currentLocation.latitude,
                                currentLocation.longitude,
                                tempStationList[i].latitude.toDouble(),
                                tempStationList[i].longitude.toDouble()
                            )
                        }
                    }


//                    distance = getKmFromLatLong(
//                        currentLocation.latitude,
//                        currentLocation.longitude,
//                        latLang.latitude,
//                        latLang.longitude
//                    )

                    stationReached = false
                    Timber.d("markerCheck::0-- $distance")

                    if (distance <= 200 && distance != 0.0) {

                        Timber.d("markerCheck:: --$markerPosition")


                        if (markerPosition != 0) {
                            if (distance < 100.0) {
                                if (stationNearby) {
                                    stationNearby = false
                                    if (!markerDetailsList.isNullOrEmpty()) {
                                        if (!dialogVisible) {
                                            fromMapMarker = false
                                            updatePassengerList = true
                                            bpDpServiceApi()
                                        }
                                    }
                                }

                            }
                            if (distance > 100.00) {
                                stationNearby = true
                            }

                        } else {
                            if (stationClose) {
                                stationClose = false
                                if (!tempStationList.isNullOrEmpty()) {
                                    stationList.forEach {
                                        if (latLang.latitude.toString() == it.latitude
                                            && latLang.longitude.toString() == it.longitude
                                        ) {
                                            tempList.addAll(it.passenger_details)
                                        }
                                    }
                                    var size = 0
                                    tempList.forEach {
                                        if (it.status != 2) {
                                            size += 1
                                        }
                                    }

                                    if (!markerDetailsList.isNullOrEmpty()) {
                                        if (!dialogVisible) {
                                            fromMapMarker = false
                                            updatePassengerList = true
                                            bpDpServiceApi()
//                                        DialogUtils.mapPassengerList(this, tempList, this, this)
                                        }
                                    }
                                }
                            }

                            if (tempStationList.size > 1) {
                                binding.currentCityName.text = tempStationList[0].city
                                binding.currentStationName.text = tempStationList[0].name
                                binding.nextStationName.text = tempStationList[1].name
                                binding.nextPassengerCount.text =
                                    tempStationList[1].passenger_details.size.toString()
                                count = tempStationList[1].passenger_details.size
                                binding.bpDpCurrentLayout.visible()
                                binding.bpDpNextLayout.visible()

                            } else {
                                binding.currentCityName.text = tempStationList[0].city
                                binding.currentStationName.text = tempStationList[0].name
                                binding.bpDpNextLayout.gone()
                            }
                            binding.divisionView.visible()

                            if (!stationReached) {
                                if (distance < 75.00) {
                                    tts = TextToSpeech(this, this)
                                    message =
                                        "${getString(R.string.you_have_arrived_at)}${tempStationList[0].name} ${
                                            getString(R.string.station)
                                        }"
                                    if (!dialogVisible) {
                                        if (tempStationList[0].id.toString() == lastStationId) {
                                            deactivateForeGroundPermission()
                                            DialogUtils.tripEnded(this, this)

                                        }
                                    }


                                    if (!stationReached) {
                                        newRoute = true
                                        if (tempStationList.size > 0) {
                                            val urll = getDirectionURL(
                                                currentLocation,
                                                LatLng(
                                                    tempStationList[1].latitude.toDouble(),
                                                    tempStationList[1].longitude.toDouble()
                                                ),
                                                getGoogleMapKey(this)
                                            )
                                            getcurrentdirection(urll).execute()
                                        }
                                    }
                                    stationReached = true
                                    stationClose = true
                                    removedStation.add(tempStationList[0].id.toString())
                                    if (PreferenceUtils.getReachedStationList().isNullOrEmpty()) {
                                        val temList = arrayListOf(tempStationList[0].id.toString())
                                        PreferenceUtils.putReachedStationList(temList)
                                    } else {
                                        val stationReachedtemp =
                                            PreferenceUtils.getReachedStationList()
                                        stationReachedtemp?.add(tempStationList[0].id.toString())
                                        PreferenceUtils.putReachedStationList(stationReachedtemp!!)
                                    }



                                    tempStationList.remove(tempStationList[0])
                                    markerDetailsList.removeAt(0)
                                    stationLine[0].remove()
                                    stationLine.removeAt(0)
                                }
                            }
                            for (i in 0 until removedStation.size) {
                                tempStationList.forEach {
                                    if (removedStation[i] == it.id.toString()) {
                                        tempStationList.remove(it)
                                    }
                                }
                            }

                        }
                    } else {

                        binding.bpDpCurrentLayout.gone()
                        binding.divisionView.gone()

                        if (tempStationList.size > 0) {
                            binding.nextStationName.text = tempStationList[0].name
                            binding.nextPassengerCount.text = count.toString()
                        }
                        binding.bpDpNextLayout.visible()

                    }
                }

            }

        } catch (e: Exception) {
            Timber.d("error msg: ${e.message}")
        }
    }

    private fun addOnlyMarker() {
        try {
//            Timber.d("mapFlowCheck::1")
            for (i in 0..fixedStationList.size.minus(1)) {
                markerDetailsList.add(mMap.addMarker(MarkerOptions().position(fixedStationList[i]))!!)

            }
            for (i in 0..tempStationList.size.minus(1)) {
                if (i != tempStationList.size - 1) {
                    val originLocation = LatLng(
                        tempStationList[i].latitude.toDouble(),
                        tempStationList[i].longitude.toDouble()
                    )
                    val destinationLocation = LatLng(
                        tempStationList[i + 1].latitude.toDouble(),
                        tempStationList[i + 1].longitude.toDouble()
                    )
                    val urll = getDirectionURL(
                        originLocation,
                        destinationLocation,
                        getGoogleMapKey(this)
                    )
                    if (isNetworkAvailable())
                        GetDirection(urll).execute()
                }
            }
        } catch (e: Exception) {
            Timber.d("error msg: ${e.message}")
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        @Deprecated("Deprecated in Java")


        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val result = ArrayList<List<LatLng>>()

            if (isNetworkAvailable()) {
                try {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    val data = response.body!!.string()

                    try {

                        val respObj = Gson().fromJson(data, MapData::class.java)
                        val path = ArrayList<LatLng>()
                        for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                            path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                        }
                        stationPath.add(path)
                        result.add(path)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    Timber.d("locationclicktest:0")
                }
            }

            return result

        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: List<List<LatLng>>) {
//            Timber.d("locationclicktest:0")

            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(resources.getColor(R.color.colorPrimary))
                lineoption.geodesic(true)
            }

            stationLine.add(mMap.addPolyline(lineoption))
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class getcurrentdirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            pathList.clear()
            val result = ArrayList<List<LatLng>>()

            if (isNetworkAvailable()) {
                try {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    val data = response.body!!.string()

                    try {

                        val respObj = Gson().fromJson(data, MapData::class.java)
                        val path = ArrayList<LatLng>()
                        for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                            path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                        }
                        result.add(path)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    Timber.d("locationclicktest:0")
                }
            }

            return result

        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: List<List<LatLng>>) {
//            Timber.d("locationclicktest:0")
            val lineoption = PolylineOptions()
            if (!result.isNullOrEmpty()) {
                result[0].forEach {
                    pathList.add(LatLng(it.latitude, it.longitude))
                }
            }

            for (i in result.indices) {
                result[i]
                Timber.d("pathLocation:0, : ${i}")
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(resources.getColor(R.color.colorPrimary))
                lineoption.geodesic(true)
            }
            mapline.add(mMap.addPolyline(lineoption))
//            Timber.d("locationclicktest:0.0: ${mapline.size}")
            if (mapline.size > 1) {

                mapline[0].remove()
                mapline.removeAt(0)
            }
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    private fun updateBoardedStatusApi(
        passengerName: String,
        pnrNumber: String,
        seatNumber: String,
        status: String,
        templist: List<String>,
    ) {
        if (isNetworkAvailable()) {
//        if (status == "2") {
            val updateBoardedStatusRequest = UpdateBoardedStatusRequest(
                bccId.toString(),
                format_type,
                update_boarded_status_method_name,
                com.bitla.ts.domain.pojo.update_boarded_status.ReqBody(
                    loginModelPref.api_key,
                    pnrNumber,
                    seatNumber,
                    status,
                    "",//Qr Code
                    true,
                    "",//New OTP
                    passengerName,
                    resId!!.toString(),
                    templist,
                    "idn-server",
                    locale = locale
                )
            )
           /* pickUpChartViewModel.updateBoardedStatusAPI(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                updateBoardedStatusRequest,
                update_boarded_status_method_name
            )*/

            pickUpChartViewModel.updateBoardedStatusAPI(
                com.bitla.ts.domain.pojo.update_boarded_status.ReqBody(
                    loginModelPref.api_key,
                    pnrNumber,
                    seatNumber,
                    status,
                    "",//Qr Code
                    true,
                    "",//New OTP
                    passengerName,
                    resId!!.toString(),
                    templist,
                    "idn-server",
                    locale = locale
                ),
                update_boarded_status_method_name
            )
        } else noNetworkToast()
    }

    private fun updateBoardedStatusObserver() {
        pickUpChartViewModel.updateBoardedStatusResponse.observe(this) {
//            Timber.d("reservationblock ${it}")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        toast(it.message)

                        switch.isChecked = it.status == "2"
                        statusChanged = true


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
                        toast(it.result.message)
                    }
                }
            } else {
                toast(getString(R.string.opps))
            }
        }
    }


    private fun getDirectionURL(origin: LatLng, dest: LatLng, secret: String): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving&key=$secret"
    }

    override fun onSingleButtonClick(str: String) {
        if (str == "Cancel") {
//            if (statusChanged) {
//                bpDpServiceApi()
//            }
            dialogVisible = false
            if (stationReached) {
                if (tempStationList[0].id.toString() == lastStationId) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        deactivateForeGroundPermission()
                        binding.btnStart.performClick()
                        DialogUtils.tripEnded(this, this)
                    }, 100)
                }
            }


        } else if (str == "trip_complete") {
            onBackPressed()
        }

    }

    override fun onItemData(view: View, str1: String, str2: String) {

    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
        var status = view.tag
        switch = view as Switch
        updateBoardedStatusApi(str1, str2, str3, status.toString(), arrayListOf("$str3:89.0"))
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Timber.d("TTS", "The Language not supported!")
            } else {
                speakOut(message.toString())

            }
        }
    }

    private fun speakOut(message: String) {
        tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null, "")
    }


}