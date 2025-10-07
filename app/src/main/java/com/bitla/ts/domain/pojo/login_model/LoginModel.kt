package com.bitla.ts.domain.pojo.login_model

import android.os.Parcelable
import com.bitla.ts.domain.pojo.redelcom.RedelcomPreferenceData
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginModel(
    var api_key: String = "",
    val code: Int = 0,
    val message: String? = "",
    val email: String = "",
    val header: String? = "",
    val language: String? = "",
    var logo_url: String? = "",
    var name: String = "",
    var phone_number: String = "",
    var role: String = "",
    val trackingo_api_key: String? = "",
    val trackingo_url: String? = "",
    var travels_name: String? = "",
    val user_id: Int? = 0,
    val result: Result = Result(""),
    var otp: String = "",
    var key: String = "",
    var mobile_number: String = "",
    var userName: String = "",
    var city_name: String? = "",
    var password: String = "",
    var domainName: String = "",
    var auth_token: String = "",
    var linked: Boolean = false,
    var city_id: String? = "",
    var account_balance: String? = "",
    var bccId: Int = 0,
    var mba_url: String = "",
    var dialingCode: ArrayList<Int>?= null,
    var redelcomData: RedelcomPreferenceData ?= null,
    var firstTime: String?= "",
    var api_travel_id: String = "",
    var isEncryptionEnabled: Boolean = false,
    var is_sub_agent_and_user: String = "false",
    val shift_list: ArrayList<Shift>? = null,
    val counter_list: ArrayList<Counter>? = null,
    val is_counter_enabled_by_user: Boolean? = null,
    val counter_details: CounterDetails? = null

):Parcelable


@Parcelize
data class Result(
    val message: String?
):Parcelable

@Parcelize
data class Shift(
    val id: Int = 0,
    val name: String =""
) : Parcelable {
    override fun toString(): String {
        return name
    }
}

@Parcelize
data class Counter(
    val id: Int = 0,
    val name: String =""
) : Parcelable {
    override fun toString(): String {
        return name
    }
}

@Parcelize
data class CounterDetails(
    val shift_name: String? = null,
    val counter_name: String? = null
) : Parcelable