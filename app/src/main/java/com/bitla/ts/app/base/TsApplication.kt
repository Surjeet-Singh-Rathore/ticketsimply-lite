package com.bitla.ts.app.base

import android.app.*
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.*
import com.bitla.ts.BuildConfig
import com.bitla.ts.koin.appModule.*
import com.bitla.ts.koin.networkModule.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.constants.DEFAULT_TEXT_SIZE
import com.bitla.ts.utils.constants.LARGE_TEXT_SIZE
import com.bitla.ts.utils.constants.SMALL_TEXT_SIZE
import com.bitla.ts.utils.constants.XLARGE_TEXT_SIZE
import com.bitla.ts.utils.sharedPref.*
import com.google.android.gms.tasks.*
import com.google.firebase.crashlytics.*
import com.google.firebase.messaging.*
import com.google.firebase.remoteconfig.*
import dagger.hilt.android.*
import io.sentry.android.core.*
import org.koin.android.ext.koin.*
import org.koin.core.context.*
import org.koin.core.logger.*
import timber.log.*
import toast

@HiltAndroidApp
class TsApplication : Application(), ExceptionListener, RemoteConfigUpdateHelper.SentryListener {

    companion object {
        private lateinit var instance: TsApplication

        fun getAppContext(): TsApplication {
            return instance
        }
    }

    override fun onCreate() {
        instance = this
        super.onCreate()

      // turnOnStrictMode()
//        setupExceptionHandler()
        PreferenceUtils.with(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(getAppContext())
            modules(listOf(RepositoryModule, ViewModelModule, NetworkModule, ApiModule))
        }

//        fetch remote config
        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
//        val defaultData: MutableMap<String, Any> = HashMap()
//        defaultData[RemoteConfigUpdateHelper.KEY_UPDATE_ENABLE] = false
//        defaultData[RemoteConfigUpdateHelper.KEY_UPDATE_VERSION] = "3.2"
//        defaultData[RemoteConfigUpdateHelper.KEY_UPDATE_URL] = "update_url"
//        remoteConfig.setDefaultsAsync(defaultData)

        remoteConfig.fetch(5)
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    remoteConfig.fetchAndActivate()
                    remoteConfigCheckSentryEnabled()
                }
            }



//        get FCM token
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Timber.d("fcmToken-$token")
                PreferenceUtils.putString(PREF_FCM_TOKEN,token.toString())
            }
        }

//        turnOnStrictMode()

    }

    private fun remoteConfigCheckSentryEnabled() {
        RemoteConfigUpdateHelper.with(this).onCheckIsSentryEnabled(this)?.check()
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Also indicate that something went wrong to the user like maybe a dialog or an activity.
        FirebaseCrashlytics.getInstance().recordException(throwable)
//        throwable.message?.let { Timber.d("ExceptionMsg", it) }
    }

    private fun setupExceptionHandler() {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    uncaughtException(Looper.getMainLooper().thread, e)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            uncaughtException(t, e)
        }
    }

    override fun onSentryListener(
        isSentryEnabled: Boolean?,
        country: String,
        currentCountry: String?,
        sentryPerformanceForCountry: String
    ) {
        val dsn = if (isSentryEnabled == true && BuildConfig.APPLICATION_ID == "com.bitla.ticketsimply" && currentCountry != null && country.contains(currentCountry,true)) "https://7a68a2d379f883e665d9984e5301007b@o4505674387423232.ingest.sentry.io/4505674393649152" else ""
        SentryAndroid.init(
            this
        ) { options: SentryAndroidOptions ->
            options.dsn = dsn
            options.isAnrEnabled = true
            options.isAttachScreenshot = true
            options.isEnableFramesTracking = true
            options.enableAllAutoBreadcrumbs(true)
            options.isAnrReportInDebug = true
            options.isCollectAdditionalContext = true
            if (dsn.isNotEmpty() && currentCountry != null && sentryPerformanceForCountry.contains(currentCountry,true))
            {
                options.isEnableAutoActivityLifecycleTracing = true
                options.profilesSampleRate = 1.0
                options.tracesSampleRate = 1.0
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { adjustFontScale(it) })
    }

    private fun adjustFontScale(context: Context): Context {
        val customScale = when (PreferenceUtils.getTextSize(context)) {
            SMALL_TEXT_SIZE -> 0.85f
            DEFAULT_TEXT_SIZE -> 1.0f
            LARGE_TEXT_SIZE -> 1.15f
            XLARGE_TEXT_SIZE -> 1.3f
            else -> 1.0f
        }


        val config = context.resources.configuration
        config.fontScale = 1f // ignore system font scale

        val metrics = context.resources.displayMetrics
        metrics.scaledDensity = config.fontScale * metrics.density * customScale

        return context.createConfigurationContext(config)
    }

}

interface ExceptionListener {
    fun uncaughtException(thread: Thread, throwable: Throwable)
}

fun turnOnStrictMode() {
    if (BuildConfig.DEBUG) {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyFlashScreen()
                .detectCustomSlowCalls()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyDeath()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects() // API level 11
//                .setClassInstanceLimit(Class.forName(“com.apress.proandroid.SomeClass”), 100)
                .penaltyDeath().build()
        )
        StrictMode.allowThreadDiskReads()
    }
}

