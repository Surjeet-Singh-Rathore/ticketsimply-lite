package com.bitla.ts.utils.security

import com.bitla.ts.utils.sharedPref.PREF_IS_ENCRYPTED
import com.bitla.ts.utils.sharedPref.PreferenceUtils

object EncrypDecryp {
    private const val NATIVE_LIBRARY_KEY = "keys"

    init {
        System.loadLibrary(NATIVE_LIBRARY_KEY)
    }
    private external fun getApiKey(): String
    private external fun encodeToBase64(s: String): String?
    private external fun decodeFromBase64(s: String): String?

    fun isEncrypted():Boolean{
        return (PreferenceUtils.getPreference(PREF_IS_ENCRYPTED,false) == true)
    }

    fun getEncryptedValue(str:String):String{
        return if (PreferenceUtils.getPreference(PREF_IS_ENCRYPTED,false) == true) encodeToBase64(str)?:"" else str

    }

    fun getDecryptedValue(str: String):String{
        return if (PreferenceUtils.getPreference(PREF_IS_ENCRYPTED,false) == true) decodeFromBase64(str)?:"" else str
    }

    fun getMapApiKey():String{
        return getApiKey()
    }



}