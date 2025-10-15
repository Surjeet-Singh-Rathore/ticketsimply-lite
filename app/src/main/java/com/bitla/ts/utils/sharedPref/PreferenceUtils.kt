package com.bitla.ts.utils.sharedPref

import android.app.*
import android.content.*
import android.content.res.Resources
import android.os.*
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import android.util.Log
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.fare_breakup.request.*
import com.bitla.ts.domain.pojo.getPrefillPassenger.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.recent_search.*
import com.bitla.ts.domain.pojo.view_reservation.*
import com.bitla.ts.domain.pojo.view_reservation.RespHash
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.google.gson.*
import com.google.gson.reflect.*
import timber.log.*
import toast
import java.io.FileNotFoundException
import java.security.KeyStore
import java.security.KeyStoreException
import com.bitla.ts.utils.application.LocaleManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object PreferenceUtils {
    lateinit var mLocalPreferences: SharedPreferences
    lateinit var applicationContext: Context

    //Name of Shared Preference file
    private const val PREFERENCES_FILE_NAME = "com.bitla.ts"

    fun with(application: Application) {
        val oldPolicy = StrictMode.allowThreadDiskReads()

        try {
            // Validate or reset master key
            val masterKeyAlias = getOrResetMasterKey()

            // Initialize EncryptedSharedPreferences
            mLocalPreferences = EncryptedSharedPreferences.create(
                // passing a file name to share a preferences
                PREFERENCES_FILE_NAME,
                masterKeyAlias,
                application,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            applicationContext = application

        } catch (e: Exception) {
            Timber.tag("PreferenceUtils").e(e, "Error initializing EncryptedSharedPreferences. Falling back.")

            // Fallback to regular SharedPreferences in case of failure
            mLocalPreferences = application.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
            applicationContext = application
        } finally {
            StrictMode.setThreadPolicy(oldPolicy)
        }
    }


    /**
     * Validates the master key. Resets it if unusable and regenerates a new one.
     */
    private fun getOrResetMasterKey(): String {
        return try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            // Ensure the key is valid and usable
            validateMasterKey(masterKeyAlias)
            masterKeyAlias
        } catch (e: Exception) {
            Timber.tag("PreferenceUtils").e(e, "Master key is corrupted. Resetting master key.")
            resetMasterKey()
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC) // Generate a new key
        }
    }

    /**
     * Validates the master key by checking its presence in the Keystore.
     */
    private fun validateMasterKey(masterKeyAlias: String) {
        // Run the heavy keystore work in background
        runBlocking(Dispatchers.IO) {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            if (!keyStore.containsAlias(masterKeyAlias)) {
                throw KeyStoreException("Master key alias not found in Keystore.")
            }
        }
    }
    /**
     * Deletes the master key from the Keystore if corrupted or unusable.
     */
    private fun resetMasterKey() {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            if (keyStore.containsAlias(masterKeyAlias)) {
                keyStore.deleteEntry(masterKeyAlias)
            }
        } catch (e: Exception) {
            Timber.tag("PreferenceUtils").e(e, "Error resetting master key.")
        }
    }

//    {START LOCAL PREFERENCE  SAVE}

    /**
     * Get data from mPreferenceUtil with key {key} & of type {obj}
     *
     * @param key          preference key
     * @param defautlValue default key for preference
     * @param <T>
     * @return
    </T> */
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
                else -> {
                    throw IllegalArgumentException("Unsupported preference type")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Save data to mPreferenceUtil with key {key} & of type {obj}
     *
     * @param key
     * @param value
     * @param <T>
     * @return
    </T> */
    fun <T> setPreference(key: String, value: T) {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        try {
            try {
                val editor = mLocalPreferences?.edit()
                if (value is String) {
                    editor?.putString(key, value as String)
                } else if (value is Int) {
                    editor?.putInt(key, value as Int)
                } else if (value is Boolean) {
                    editor?.putBoolean(key, value as Boolean)
                } else if (value is Float) {
                    editor?.putFloat(key, value as Float)
                } else if (value is Long) {
                    editor?.putLong(key, value as Long)
                }
                editor?.apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } finally {
            StrictMode.setThreadPolicy(oldPolicy)
        }

    }


    /**
     * Saves object into the Preferences.
     *
     * @param `object` Object of model class (of type [T]) to save
     * @param key Key with which Shared preferences to
     **/
    fun <T> putObject(`object`: T, key: String, context: Context? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val jsonString = GsonBuilder().create().toJson(`object`)

            if (key == PREF_PRIVILEGE_DETAILS) {
                mLocalPreferences.edit()?.putString(key, jsonString)?.apply()

                context?.openFileOutput("privilege_data", Context.MODE_PRIVATE)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
            } else {
                mLocalPreferences.edit()?.putString(key, jsonString)?.apply()
            }
        }
    }

    /**
     * Used to retrieve object from the Preferences.
     *
     * @param key Shared Preference key with which object was saved.
     **/
    inline fun <reified T> getObject(key: String): T? {
        //We read JSON String which was saved.
        val value = mLocalPreferences.getString(key, null)
        //JSON String was found which means object can be read.
        //We convert this JSON String to model object. Parameter "c" (of
        //type “T” is used to cast.
        return GsonBuilder().create().fromJson(value, T::class.java)
    }

    /**reified
     * clear key preference when required
     */
    fun removeKey(key: String) {
        mLocalPreferences.edit()?.remove(key)?.apply()
    }

    /**
     * clear preference when required
     */
    fun clearAllPreferences() {
        mLocalPreferences.edit()?.clear()?.apply()
    }

    fun putOriginCity(list: MutableList<Origin>?) {
        if (list == null) return
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(PREF_ORIGIN_LIST, json)
        editor.apply()
    }

    inline fun <reified T> Gson.fromJson(json: String) : T {
        return fromJson(json, object : TypeToken<T>() {}.type)
    }


    fun getOriginCity(): MutableList<Origin>? {
        val gson = Gson()
        val json = mLocalPreferences.getString(PREF_ORIGIN_LIST, null)
        return json?.let { gson.fromJson(it) }
    }

    fun putDestinationCity(list: MutableList<Destination>?) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(PREF_DESTINATION_LIST, json)
        editor.apply()
    }

    fun putRapidBookingType(type: Int) {
        val editor = mLocalPreferences.edit()
        editor.putInt(PREF_RAPID_BOOKING_TYPE, type)
        editor.apply()
    }

    fun getRapidBookingType(): Int {
        return mLocalPreferences.getInt(PREF_RAPID_BOOKING_TYPE, 0)

    }

    fun getDestinationCity(): MutableList<Destination>? {
        val gson = Gson()
        val json = mLocalPreferences.getString(PREF_DESTINATION_LIST, null)
        return json?.let { gson.fromJson(it) }
    }

    fun putInterDestinationCity(list: MutableList<Destination>?) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(PREF_INTER_DESTINATION_LIST, json)
        editor.apply()
    }

    fun putBoarding(list: MutableList<BoardingPointDetail>?) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(BP_DP_BOARDING_LIST, json)
        editor.apply()
    }

    fun putBpDpList(list: MutableList<SearchModel>) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(SET_STATION, json)
        editor.apply()
    }

    fun putOriginDestList(list: ArrayList<SearchModel>) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(ORIGIN_DEST_LIST, json)
        editor.apply()
    }

    fun getOriginDestList(): ArrayList<SearchModel> {
        val gson = Gson()
        val json = mLocalPreferences.getString(ORIGIN_DEST_LIST, null)
        return gson.fromJson(json ?: "")
    }

    fun getBpDpList(): MutableList<SearchModel> {
        val gson = Gson()
        val json = mLocalPreferences.getString(SET_STATION, null)
        return gson.fromJson(json ?: "")
    }

    fun putDropping(list: MutableList<DropOffDetail>?) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(BP_DP_Dropping_LIST, json)
        editor.apply()
    }

    fun getBoarding(): MutableList<BoardingPointDetail>? {
        val gson = Gson()
        val json = mLocalPreferences.getString(BP_DP_BOARDING_LIST, null)
        return gson.fromJson(json ?: "")
    }
    fun putRespHashBoardingList(list: ArrayList<RespHash>) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString("RESP_HASH_LIST", json)
        editor.apply()
    }

    fun getRespHashBoardingList(): ArrayList<RespHash>? {
        val gson = Gson()
        val json = mLocalPreferences.getString("RESP_HASH_LIST", null)
        return gson.fromJson(json ?: "")
    }

    fun getDropping(): MutableList<DropOffDetail>? {
        val gson = Gson()
        val json = mLocalPreferences.getString(BP_DP_Dropping_LIST, null)
        return gson.fromJson(json ?: "")
    }

    fun putCountryCodes(list: ArrayList<Int>) {
        if (list != null) {
            val editor = mLocalPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(list)
            editor.putString(PREF_COUNTRY_CODE, json)
            editor.apply()
        }
    }


    fun getCountryCodes(): ArrayList<Int> {
        val gson = Gson()
        val json =
            mLocalPreferences.getString(PREF_COUNTRY_CODE, "[]") // "[]" for the default value
        return gson.fromJson(json ?: "")
    }

    fun putLogFileNames(list: ArrayList<String>) {
        if (list != null) {
            val editor = mLocalPreferences.edit()
            val gson = Gson()
            val safeCopy = ArrayList(list)
            val json = gson.toJson(safeCopy)
            editor.putString(PREF_LOG_FILE_NAME, json)
            editor.apply()
        }
    }


    fun getIsWhatsAppSMS(): Boolean {
        val gson = Gson()
        val json = mLocalPreferences.getBoolean(IS_WHATSAPP_SMS_ENABLE, true)
        return json
    }

    fun putIsWhatsAppSMS(isWhatsAppSMSEnable: Boolean) {
        val editor = mLocalPreferences.edit()
        editor.putBoolean(IS_WHATSAPP_SMS_ENABLE, isWhatsAppSMSEnable)
        editor.apply()

    }


    fun getLogFileNames(): ArrayList<String>? {
        val gson = Gson()
        val json = mLocalPreferences.getString(PREF_LOG_FILE_NAME, "")
        return gson.fromJson(json ?: "")
    }


    fun getInterDestinationCity(): MutableList<Destination>? {
        val gson = Gson()
        val json = mLocalPreferences.getString(PREF_INTER_DESTINATION_LIST, null)
        return gson.fromJson(json ?: "")
    }

    fun putRecentSearch(list: MutableList<RecentSearch>?) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(PREF_RECENT_SEARCH_LIST, json)
        editor.apply()
    }

    fun getRecentSearch(): MutableList<RecentSearch>? {
        val gson = Gson()
        val json = mLocalPreferences.getString(PREF_RECENT_SEARCH_LIST, null)
        return gson.fromJson(json ?: "")
    }

    fun putCitySeqOrder(list: MutableList<CitySeqOrder>?) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(PREF_CITY_SEQ_ORDER, json)
        editor.apply()
    }

    fun getCitySeqOrder(): MutableList<CitySeqOrder>? {
        val gson = Gson()
        val json = mLocalPreferences.getString(PREF_CITY_SEQ_ORDER, null)
        return gson.fromJson(json ?: "")
    }


    fun putString(key: String, value: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val edit = mLocalPreferences.edit()
            edit.putString(key, value)
            edit.apply() // runs async, encryption already done off main thread
        }
    }

    fun putPrintingType(value: Int) {
        val edit = mLocalPreferences.edit()
        edit.putInt(PRINTING_TYPE, value)
        edit.apply()
    }

    fun getPrintingType(): Int {
        return mLocalPreferences.getInt(PRINTING_TYPE, PRINT_TYPE_BLUETOOTH)
    }


    fun getString(key: String): String? {
        return mLocalPreferences.getString(key, "")
    }


    fun getBccId(): Int {
        var bccId = 0
        if (getPreference(PREF_BCC_ID, 0) != null)
            bccId = getPreference(PREF_BCC_ID, 0)!!
        return bccId
    }

    fun getDashboardCurrentDate(): String {
        var dasbhaordCurrentDate = getDateYMD(getTodayDate())
        if (getPreference(PREF_DASHBOARD_CURRENT_DATE, getDateYMD(getTodayDate())) != null)
            dasbhaordCurrentDate =
                getPreference(PREF_DASHBOARD_CURRENT_DATE, getDateYMD(getTodayDate()))!!
        return dasbhaordCurrentDate
    }

    fun getDashboardFromPastDate(): String {
        var dasbhaordPastDate = ""
        if (getPreference(PREF_DASHBOARD_PAST_DATE, getDateYMD(getTodayDate())) != null) {
            dasbhaordPastDate =
                getPreference(PREF_DASHBOARD_PAST_DATE, getDateYMD(getTodayDate())).toString()
        }
        return dasbhaordPastDate
    }

    fun getDashboardToFutureDate(): String {
        var dasbhaordFutureDate = ""
        if (getPreference(PREF_DASHBOARD_FUTURE_DATE, getDateYMD(getTodayDate())) != null)
            dasbhaordFutureDate =
                getPreference(PREF_DASHBOARD_FUTURE_DATE, getDateYMD(getTodayDate()))!!
        return dasbhaordFutureDate
    }

    fun getAccountBalance(): Int {
        var accountBalance = 0
        if (getPreference(PREF_ACCOUNT_BALNACE, 0) != null)
            accountBalance = getPreference(PREF_ACCOUNT_BALNACE, 0)!!
        return accountBalance
    }

    fun getSourceId(): String {
        var sourceId = ""
        if (isAgentAndAllowBookingForAllotedServices()) {
            Timber.d("Agent Source ID is called")
            if (getString(AGENT_SELECTED_SOURCE_ID) != null)
                sourceId = getString(AGENT_SELECTED_SOURCE_ID)?.replace(".0", "") as String
        } else {
            if (getString(PREF_SOURCE_ID) != null)
                sourceId = getString(PREF_SOURCE_ID)?.replace(".0", "") as String
        }
        return sourceId
    }

    fun getDestinationId(): String {
        var destinationId = ""
        if (isAgentAndAllowBookingForAllotedServices()) {
            Timber.d("Agent Destination ID is called")
            if (getString(AGENT_SELECTED_DESTINATION_ID) != null)
                destinationId =
                    getString(AGENT_SELECTED_DESTINATION_ID)?.replace(".0", "") as String
        } else {
            if (getString(PREF_DESTINATION_ID) != null)
                destinationId = getString(PREF_DESTINATION_ID)?.replace(".0", "") as String
        }
        return destinationId
    }


    fun getSource(): String {
        var source = ""
        if (isAgentAndAllowBookingForAllotedServices()) {
            Timber.d("Agent Source is called")
            if (getString(AGENT_SELECTED_SOURCE) != null)
                source = getString(AGENT_SELECTED_SOURCE) as String
        } else {
            if (getString(PREF_SOURCE) != null)
                source = getString(PREF_SOURCE)?.replace(".0", "") as String
        }
        return source
    }

    fun getDestination(): String {
        var dest = ""
        if (isAgentAndAllowBookingForAllotedServices()) {
            Timber.d("Agent Destination is called")
            if (getString(AGENT_SELECTED_DESTINATION) != null)
                dest = getString(AGENT_SELECTED_DESTINATION) as String
        } else {
            if (getString(PREF_DESTINATION) != null)
                dest = getString(PREF_DESTINATION)?.replace(".0", "") as String
        }
        return dest
    }

    fun getLastSearchSource(): String {
        var source = ""
        if (getString(PREF_LAST_SEARCHED_SOURCE) != null)
            source = getString(PREF_LAST_SEARCHED_SOURCE)?.replace(".0", "") as String
        return source
    }

    fun getLastSearchDestination(): String {
        var dest = ""
        if (getString(PREF_LAST_SEARCHED_DESTINATION) != null)
            dest = getString(PREF_LAST_SEARCHED_DESTINATION)?.replace(".0", "") as String
        return dest
    }

    fun getTravelDate(): String {
        var travel = ""
        if (getString(PREF_TRAVEL_DATE) != null)
            travel = getString(PREF_TRAVEL_DATE)?.replace(".0", "") as String
        return travel
    }

    fun getLogin(): LoginModel {
        return getObject<LoginModel>(PREF_LOGGED_IN_USER)?:LoginModel()
    }

    fun getPrivilege(context: Context?=null): PrivilegeResponseModel? {

            if(context != null){
                try {
                    context.toast("yes")
                    // Read the JSON string from the file
                    val jsonString = context.openFileInput("privilege_data").bufferedReader().use { it.readText() }
                    // Convert the JSON string back to an object
                    return GsonBuilder().create().fromJson(jsonString, PrivilegeResponseModel::class.java)
                } catch (e: Exception) {
                    context.toast(e.message)
                    // Handle case where the file doesn't exist
                    return null
                }
            }else{
                return getObject<PrivilegeResponseModel>(
                    PREF_PRIVILEGE_DETAILS
                )
            }
    }

    fun getPrivilegejJava() : PrivilegeResponseModel? {
        return getObject<PrivilegeResponseModel>(
            PREF_PRIVILEGE_DETAILS
        )
    }

    fun hasKey(key: String): Boolean {
        return mLocalPreferences.contains(key)
    }

    fun getlang(): String {

        val deviceLang: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0].language
        } else {
            Resources.getSystem().configuration.locale.language
        }

        val language = if (deviceLang.equals(LocaleManager.INDONESIAN, ignoreCase = true)) {
            "id"
        } else if (deviceLang.equals(LocaleManager.SPANISH, ignoreCase = true)) {
            "es"
        } else if (deviceLang.equals(LocaleManager.CAMBODIAN, ignoreCase = true)) {
            "km"
        } else if (deviceLang.equals(LocaleManager.VIETNAMESE, ignoreCase = true)) {
            "vi"
        } else {
            "en"
        }

        return try {
            mLocalPreferences.getString(PREF_LANGUAGE, language).toString()
        } catch (e: Throwable) {
            language
        }
    }

    fun setlang(language: String) {
        val editor = mLocalPreferences.edit()
        editor.putString(PREF_LANGUAGE, language)
        editor.apply()
    }

    fun setUpdatedApiUrlAddress(updatedUrl: String) {
        val editor = mLocalPreferences.edit()
        editor.putString(PREF_SERVER, updatedUrl)
        editor.apply()
    }

    fun setUpdatedDirectApiUrlAddress(updatedUrl: String) {
        val editor = mLocalPreferences.edit()
        editor.putString(DIRECT_SERVER_URL, updatedUrl)
        editor.apply()
    }

    fun getUpdatedApiUrlAddress(): String {
        return mLocalPreferences.getString(PREF_SERVER, "").toString()
    }

    fun getDirectUpdatedApiUrlAddress(): String {
        return mLocalPreferences.getString(DIRECT_SERVER_URL, "").toString()
    }

    fun putLatLong(list: ArrayList<String>) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(PREF_LAT_LANG, json)
        editor.apply()
    }

    fun getLatLang(): ArrayList<String> {
        val gson = Gson()
        val json = mLocalPreferences.getString(PREF_LAT_LANG, null)
        return gson.fromJson(json ?: "")
    }

    fun putReachedStationList(list: ArrayList<String>) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(STATION_REACHED, json)
        editor.apply()
    }

    fun getReachedStationList(): ArrayList<String>? {
        val gson = Gson()
        val json = mLocalPreferences.getString(STATION_REACHED, null)
        return gson.fromJson(json ?: "")
    }


    fun putSelectedCoupon(list: ArrayList<SeatWiseFare>) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(COUPON_SELECTED_INDIVIDUAL, json)
        editor.apply()
    }

    fun getSelectedCoupon(): ArrayList<SeatWiseFare>? {
        val gson = Gson()
        val json = mLocalPreferences.getString(COUPON_SELECTED_INDIVIDUAL, null)
        return gson.fromJson(json ?: "")
    }

    fun putPrefillData(list: GetPrefillResponse) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(PREFILL_DATA, json)
        editor.apply()
    }

    fun getPrefillData(): GetPrefillResponse? {
        val gson = Gson()
        val json = mLocalPreferences.getString(PREFILL_DATA, null)
        return gson.fromJson(json ?: "")
    }

    fun setNotificationSoundType(mContext: Context, notificationSoundType: String) {

        val mPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
            mContext
        )

        val editor = mPreferences.edit()
        editor.putString(PREF_NOTIFICATION_SOUND_TYPE, notificationSoundType)
        editor.apply()

    }

    fun getNotificationSoundType(mContext: Context): String {
        val mPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
            mContext
        )

        return mPreferences.getString(PREF_NOTIFICATION_SOUND_TYPE, NOTIFICATION_DEFAULT_SOUND)
            ?: NOTIFICATION_DEFAULT_SOUND
    }

    fun getTextSize(mContext: Context): String {
        val mPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
            mContext
        )

        return mPreferences.getString(PREF_TEXT_SIZE, DEFAULT_TEXT_SIZE)
            ?: DEFAULT_TEXT_SIZE
    }

    fun setTextSize(mContext: Context, textsize: String) {

        val mPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
            mContext
        )

        val editor = mPreferences.edit()
        editor.putString(PREF_TEXT_SIZE, textsize)
        editor.apply()

    }

    fun isAgentAndAllowBookingForAllotedServices(): Boolean {
        return mLocalPreferences.getBoolean(
            PREF_IS_AGENT_AND_ALLOW_BOOKING_FOR_ALLOTED_SERVICES,
            false
        )
    }

    fun setIsAgentAndAllowBookingForAllotedServices(flag: Boolean) {
        if (flag) {
            setPreference(PREF_IS_AGENT_AND_ALLOW_BOOKING_FOR_ALLOTED_SERVICES, true)
        } else {
            setPreference(PREF_IS_AGENT_AND_ALLOW_BOOKING_FOR_ALLOTED_SERVICES, false)
        }
    }


    fun putRevenueFilterList(list: MutableList<Service>) {
        val editor = mLocalPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString("revenue_filter_list", json)
        editor.apply()
    }

    fun getRevenueFilterList(): MutableList<Service> {
        val gson = Gson()
        val json = mLocalPreferences.getString("revenue_filter_list", null)
        return gson.fromJson(json ?: "") ?: mutableListOf()
    }



    fun getLocationApiInterval(): String {
        return mLocalPreferences.getString(locationApiInterval, "2000").toString()
    }

    fun setLocationApiInterval(interval: String) {
        val editor = mLocalPreferences.edit()
        editor.putString(locationApiInterval, interval)
        editor.apply()
    }


    fun getIsHttpsSupport(): Boolean {
        return mLocalPreferences.getBoolean("isHttpsSupport", true)
    }

    fun setIsHttpsSupport(isHttpsSupport: Boolean) {
        val editor = mLocalPreferences.edit()
        editor.putBoolean("isHttpsSupport", isHttpsSupport)
        editor.apply()
    }


    fun setSubAgentRole(role: String) {
        val editor = mLocalPreferences.edit()
        editor.putString("isSubAgent", role.toString())
        editor.apply()
    }


    fun getSubAgentRole(): String {
        return mLocalPreferences.getString("isSubAgent", "false").toString()?:"false"
    }


}
