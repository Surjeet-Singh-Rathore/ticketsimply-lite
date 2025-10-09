package com.bitla.restaurant_app.presentation.pojo

import android.os.Parcelable
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
    val role: String = "",
    val trackingo_api_key: String? = "",
    val trackingo_url: String? = "",
    var travels_name: String? = "",
    val user_id: Int? = 0,
    val result: Result = Result(""),
    var otp: String = "",
    var key: String = "",
    var mobile_number: String = "",
    var userName: String = "",
    var password: String = "",
    var domainName: String = "",
    var auth_token: String = "",
    var linked: Boolean = false,
    var city_id: String? = "",
    var account_balance: String? = "",
    var bccId: Int = 0,
    var mba_url: String = "",
    var dialingCode: ArrayList<Int>?= null,
    var redelcomData: RedelcomPreferenceData?= null,
    var firstTime: String?= "",

    ):Parcelable


@Parcelize
data class Result(
    val message: String?
):Parcelable

@Parcelize
data class RedelcomPreferenceData(
    var terminalId: String = "",
    var api_key: String = "",
    var client_id: String = "",
    var redelcom_uri: String = "",
    var is_redelcom_enabled: Boolean = false,
):Parcelable
