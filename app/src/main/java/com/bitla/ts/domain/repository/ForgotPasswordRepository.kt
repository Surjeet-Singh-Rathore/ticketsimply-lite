package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface


class ForgotPasswordRepository(private val apiInterface: ApiInterface) {
    // fun getLoginDetails(otp:String,key:String,id:String) = apiInterface.loginWithOTPApi(otp,key,id)
    suspend fun getLogoutDetails(login: String, password: String, deviceId: String) =
        apiInterface.logoutApi(login, password, deviceId)


}