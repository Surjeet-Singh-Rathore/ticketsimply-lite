package com.bitla.restaurant_app.presentation.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import android.util.Log
import com.bitla.restaurant_app.presentation.pojo.LoginModel
import com.bitla.restaurant_app.presentation.utils.Constants.PREF_LOGGED_IN_USER
import com.google.gson.GsonBuilder

object PreferenceUtils {

    lateinit var mLocalPreferences: SharedPreferences
    lateinit var applicationContext: Context

    //Name of Shared Preference file
    private val PREFERENCES_FILE_NAME = "com.bitla.ts.restaurant"

    fun with(application: Application) {

        val oldPolicy = StrictMode.allowThreadDiskReads()
        try {
            mLocalPreferences =
                application.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
            applicationContext = application
        } finally {
            StrictMode.setThreadPolicy(oldPolicy)
        }


    }

    fun <T> putObject(`object`: T, key: String) {
        val jsonString = GsonBuilder().create().toJson(`object`)
        mLocalPreferences.edit().putString(key, jsonString).apply()
    }


    inline fun <reified T> getObject(key: String): T? {
        val value = mLocalPreferences.getString(key, null)
        return GsonBuilder().create().fromJson(value, T::class.java)
    }


    fun getLogin(): LoginModel {
        var loginModelPref = LoginModel()
        if (getObject<LoginModel>(PREF_LOGGED_IN_USER) != null)
            loginModelPref =
                getObject<LoginModel>(PREF_LOGGED_IN_USER)!!
        Log.d("fromPrefGet",loginModelPref.toString())
        return loginModelPref
    }

    fun clear(){
        mLocalPreferences.edit().clear().apply()
    }

    fun removeKey(key: String) {
        mLocalPreferences.edit().remove(key).apply()
    }

    fun putString(key: String, value: String?) {
        val edit = mLocalPreferences.edit()
        edit.putString(key, value)
        edit.apply()
    }


    fun <T> getPreference(key: String, defautlValue: T): T? {
        try {
            when (defautlValue) {
                is String -> {
                    return mLocalPreferences?.getString(key, defautlValue as String) as T
                }

                is Int -> {
                    return mLocalPreferences?.getInt(key, defautlValue as Int) as T
                }

                is Boolean -> {
                    return mLocalPreferences?.getBoolean(key, defautlValue as Boolean) as T
                }

                is Float -> {
                    return mLocalPreferences?.getFloat(key, defautlValue as Float) as T
                }

                is Long -> {
                    return mLocalPreferences?.getLong(key, defautlValue as Long) as T
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


}