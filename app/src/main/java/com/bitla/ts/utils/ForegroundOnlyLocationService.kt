package com.bitla.ts.utils

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.location.Location
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.YourBus
import com.bitla.ts.domain.pojo.location_logs.LocationLogs
import com.bitla.ts.domain.pojo.location_logs.request.LocationLogRequest
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.view.activity.MapActivity
import com.bitla.ts.utils.constants.KEY_FOREGROUND_ENABLED
import com.bitla.ts.utils.sharedPref.PREF_MAP_COACH
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLatLang
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getUpdatedApiUrlAddress
import com.bitla.ts.utils.sharedPref.PreferenceUtils.putLatLong
import com.google.android.gms.location.*
import com.google.gson.GsonBuilder
import isNetworkAvailable
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ForegroundOnlyLocationService : Service() {
    private var configurationChange = false

    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()

    private lateinit var notificationManager: NotificationManager

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    private var currentLocation: Location? = null
    private var list: ArrayList<String> = arrayListOf()


    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = null

    override fun onCreate() {
        getPref()
        val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level: Int? = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)

        Timber.d("batteryLevel $level")

        var privilegeInterval: Long = 5000

        try {
            if ((applicationContext as BaseActivity).getPrivilegeBase()?.pingRate != null) {
                val pingRate = (applicationContext as BaseActivity).getPrivilegeBase()?.pingRate
                pingRate?.forEach {
                    val range = it.key.split("-")
                    if (level in range[0].toInt()..range[1].toInt()) {
                        privilegeInterval = it.value.toLong()
                    }
                }
            }
        } catch (e: Exception) {
            privilegeInterval = 5000
        }


        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(3)

            fastestInterval = TimeUnit.SECONDS.toMillis(7)

            maxWaitTime = TimeUnit.SECONDS.toMillis(15)

            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                currentLocation = locationResult.lastLocation

                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                if (serviceRunningInForeground) {
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        generateNotification(currentLocation)
                    )
                }
                if (currentLocation != null && currentLocation?.latitude != null && currentLocation?.longitude != null && currentLocation?.latitude != 0.0 && currentLocation?.longitude != 0.0) {
                    Timber.d("current_Location_123${getLatLang()}")
//                    putLatLong("")
                    val currentTimeStamp = System.currentTimeMillis() / 1000
                    if (getLatLang().isNullOrEmpty()) {
                        Timber.d("current Location 2")

                        list.add("${currentLocation?.latitude},${currentLocation?.longitude},$currentTimeStamp,30")
                        if (isNetworkAvailable()) {
                            list.forEach {
                                callYourBusApi(it)
                            }
                        }
                    } else {
                        Timber.d("current Location 3")

                        list.add("${currentLocation?.latitude},${currentLocation?.longitude},$currentTimeStamp,30")
                        if (isNetworkAvailable()) {
                            list.forEach {
                                callYourBusApi(it)
                            }
                        }
                    }
                    locationLogsApi(currentLocation)

//                    if (isNetworkAvailable()) {
//                        callYourBusApi(currentLocation)
//                        locationLogsApi(currentLocation)
//                    }
                }
            }
        }
    }

    private fun getPref() {
        putLatLong(list)
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.d(TAG, "onStartCommand()")

        val cancelLocationTrackingFromNotification =
            intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)

        if (cancelLocationTrackingFromNotification) {
            stopSelf()
            unsubscribeToLocationUpdates()
        }
        // Tells the system not to recreate the service after it's been killed.
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent): IBinder {
        Timber.d(TAG, "onBind()")

        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Timber.d(TAG, "onRebind()")

        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Timber.d(TAG, "onUnbind()")

        if (!configurationChange && PreferenceUtils.getPreference(
                KEY_FOREGROUND_ENABLED,
                false
            )!!
        ) {
            Timber.d(TAG, "Start foreground service")
            val notification = generateNotification(currentLocation)
            startForeground(NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }

        // Ensures onRebind() is called if MapActivity (client) rebinds.
        return true
    }

    override fun onDestroy() {
        Timber.d(TAG, "onDestroy()")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    fun subscribeToLocationUpdates() {
        Timber.d(TAG, "subscribeToLocationUpdates()")

        PreferenceUtils.setPreference(KEY_FOREGROUND_ENABLED, true)

        startService(Intent(applicationContext, ForegroundOnlyLocationService::class.java))

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            PreferenceUtils.setPreference(KEY_FOREGROUND_ENABLED, false)
            Timber.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun unsubscribeToLocationUpdates() {
        stopSelf()
        Timber.d(TAG, "unsubscribeToLocationUpdates()")

        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Location Callback removed.")
                    stopSelf()
                } else {
                    Timber.d("Failed to remove Location Callback.")
                }
            }
            PreferenceUtils.removeKey("mapRoute")
            PreferenceUtils.setPreference(KEY_FOREGROUND_ENABLED, false)

        } catch (unlikely: SecurityException) {
            PreferenceUtils.setPreference(KEY_FOREGROUND_ENABLED, true)
            Timber.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    /*
     * Generates a BIG_TEXT_STYLE Notification that represent latest location.
     */
    private fun generateNotification(location: Location?): Notification {
        Timber.d(TAG, "generateNotification()")
        // 0. Get data
        val latLongText: String? = "${location?.latitude},${location?.longitude}"
        val mainNotificationText = getString(R.string.tracking_on)
        val titleText = getString(R.string.app_name)

        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // 2. Build the BIG_TEXT_STYLE.
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        // 3. Set up main Intent/Pending Intents for notification.
        val launchActivityIntent = Intent(this, MapActivity::class.java)

        val cancelIntent = Intent(this, ForegroundOnlyLocationService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)
//        val servicePendingIntent:PendingIntent

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            servicePendingIntent =
//                PendingIntent.getActivity(this, 0, launchActivityIntent, PendingIntent.FLAG_MUTABLE)
//        }else{
        val servicePendingIntent = PendingIntent.getService(
            this, 0, cancelIntent, FLAG_IMMUTABLE
        )
//        }
//        val servicePendingIntent = PendingIntent.getService(
//            this, 0, cancelIntent, PendingIntent.FLAG_MUTABLE)

        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, launchActivityIntent, FLAG_IMMUTABLE
        )

        // 4. Build and issue the notification.
        // Notification Channel Id is ignored for Android pre O (26).
        val notificationCompatBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)


//        notificationCompatBuilder.setContentIntent(activityPendingIntent).setsoi
        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainNotificationText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setNotificationSilent()
            .setContentIntent(activityPendingIntent)
            .addAction(
                R.drawable.ic_play_small, getString(R.string.launch_activity),
                activityPendingIntent
            )
            .addAction(
                R.drawable.ic_stop,
                getString(R.string.stop_location_updates_button_text),
                servicePendingIntent,
            )
            .build()
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        internal val service: ForegroundOnlyLocationService
            get() = this@ForegroundOnlyLocationService
    }

    companion object {
        private const val TAG = "ForegroundOnlyLocationService"

        private const val PACKAGE_NAME = "com.bitla.ts"

        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"

        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"

        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"

        private const val NOTIFICATION_ID = 12345678

        private const val NOTIFICATION_CHANNEL_ID = "while_in_use_channel_01"
    }


    private fun callYourBusApi(requestString: String) {
        val currentItem = requestString
//        Timber.d("currentTimeStamp $currentLocation")
        val gson = GsonBuilder().setLenient().create()
        val retrofit: Retrofit = Retrofit.Builder().baseUrl("http://platform.yourbus.in/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson)).build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

        apiInterface.yourBus(
            "mObt3o03cm6y",
            PreferenceUtils.getString(PREF_MAP_COACH) ?: "",
            requestString
        ).enqueue(object : Callback<YourBus> {

            override fun onResponse(call: Call<YourBus>?, response: Response<YourBus>?) {
                Timber.d("current Location 0${currentItem}")
                list.remove(currentItem)

                Timber.d("Success ${response?.body()}")
            }

            override fun onFailure(call: Call<YourBus>?, t: Throwable?) {
                Timber.d("current Location 1${currentItem}")

                Timber.d("exceptionMsg ${t?.message}")
            }
        })
    }

    private fun locationLogsApi(currentLocation: Location?) {
        val currentTimeStamp = System.currentTimeMillis() / 1000
        val gson = GsonBuilder().setLenient().create()

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://${getUpdatedApiUrlAddress()}")
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

        val locationLogRequest = LocationLogRequest(
            api_key = loginModelPref.api_key,
            bus_no = PreferenceUtils.getPreference(PREF_MAP_COACH, "") ?: "",
            locale = locale ?: "",
            description = "latitude ${currentLocation?.latitude},longitude ${currentLocation?.longitude}",
            latitude = currentLocation?.latitude.toString(),
            longitude = currentLocation?.longitude.toString(),
            time_stamp = currentTimeStamp.toString()
        )

        apiInterface.locationLogs(
            locationLogRequest = locationLogRequest
        ).enqueue(object : Callback<LocationLogs> {
            override fun onResponse(call: Call<LocationLogs>?, response: Response<LocationLogs>?) {
                Timber.d("Success ${response?.body()}")
            }

            override fun onFailure(call: Call<LocationLogs>?, t: Throwable?) {
                Timber.d("exceptionMsg ${t?.message}")
            }
        })
    }
}
