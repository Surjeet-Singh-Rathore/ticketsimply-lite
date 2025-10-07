package com.bitla.ts.utils.application

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.annotation.StringDef
import java.util.*

object LocaleManager {
    const val ENGLISH = "en"
    const val HINDI = "hi"
    const val INDONESIAN = "in"
    const val SPANISH = "es"
    const val CAMBODIAN = "km"
    const val VIETNAMESE = "vi"

    /**
     * SharedPreferences Key
     */
    private const val LANGUAGE_KEY = "language_key"

    /**
     * set current pref locale
     */
    fun setLocale(mContext: Context): Context {
        return updateResources(mContext, getLanguagePref(mContext))
    }

    /**
     * Set new Locale with context
     */
    fun setNewLocale(mContext: Context, @LocalDef language: String): Context {
        setLanguagePref(mContext, language)
        return updateResources(mContext, language)
    }

    /**
     * Get saved Locale from SharedPreferences
     *
     * @param mContext current context
     * @return current locale key by default return english locale
     */
    private fun getLanguagePref(mContext: Context?): String? {
        val mPreferences = mContext?.let {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(it)
        }
        return mPreferences?.getString(LANGUAGE_KEY, ENGLISH)
    }

    /**
     * set pref key
     */
    private fun setLanguagePref(mContext: Context?, localeKey: String) {
        val mPreferences = mContext?.let {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(
                it
            )
        }
        mPreferences?.edit()?.putString(LANGUAGE_KEY, localeKey)?.apply()
    }

    /**
     * update resource
     */
    private fun updateResources(context: Context, language: String?): Context {
        val locale = language?.let { Locale(it) }
        if (locale != null) {
            Locale.setDefault(locale)
        }
        val res = context.resources
        val config = Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    /**
     * get current locale
     */
    fun getLocale(res: Resources): Locale {
        val config = res.configuration
        return if (Build.VERSION.SDK_INT >= 24)
            config.locales[0] else config.locales[0]
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(ENGLISH, HINDI)
    annotation class LocalDef {
        companion object {
            var SUPPORTED_LOCALES = arrayOf(ENGLISH, HINDI)
        }
    }
}
