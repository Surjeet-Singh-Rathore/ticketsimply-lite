package com.bitla.ts.utils.sharedPref

import android.content.Context


private val PREFERENCES_FILE_NAME = "com.bitla.ts"

class MyPref(context: Context) {

//    private val PREFERENCES_FILE_NAME = "com.bitla.ts"

    val prefrence = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)


    fun getlang(): String {
        return PreferenceUtils.mLocalPreferences.getString(PREF_LANGUAGE, "en").toString()
    }

    fun setlang(language: String) {
        val editor = PreferenceUtils.mLocalPreferences.edit()
        editor.putString(PREF_LANGUAGE, language)
        editor.apply()
    }
}

