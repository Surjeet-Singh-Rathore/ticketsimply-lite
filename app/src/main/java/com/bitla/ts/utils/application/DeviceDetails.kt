package com.bitla.ts.utils.application

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.bitla.ts.BuildConfig
import java.util.*

object DeviceDetails {
    fun getId(): String {
        return Build.ID
    }

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getModel(): String {
        return Build.MODEL
    }

    fun getManufacturer(): String {
        return Build.MANUFACTURER
    }

    fun getBrand(): String {
        return Build.BRAND
    }

    fun getHost(): String {
        return Build.HOST
    }

    fun getSDK(): String {
        return Build.VERSION.SDK_INT.toString()
    }

    fun getAndroidVersion(): String {
        return android.os.Build.VERSION.RELEASE
    }

    fun getAppVersion(): String {
        return BuildConfig.VERSION_CODE.toString()
    }

    fun getIncremental(): String {
        return Build.VERSION.INCREMENTAL
    }

    fun getDeviceLanguage(): String {
        return Locale.getDefault().displayLanguage
    }


}