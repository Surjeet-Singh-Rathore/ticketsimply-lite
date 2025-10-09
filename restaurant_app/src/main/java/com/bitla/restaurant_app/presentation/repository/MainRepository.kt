package com.bitla.restaurant_app.presentation.repository

import com.bitla.restaurant_app.presentation.network.RetrofitInstance
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.ReqBody

class MainRepository {
    private val retrofitInstance = RetrofitInstance.apiService


    suspend fun getMealCouponDetailsApi(
        apiKey: String,
        qrValue: String,
        couponCode: String,

        ) = retrofitInstance.getMealCouponDetailsApi(apiKey, qrValue, couponCode)

    suspend fun getMealCouponStatus(updateMealCouponStatusRequest: ReqBody) =
        retrofitInstance.updateMealCouponStatus(updateMealCouponStatusRequest)

    suspend fun logoutApi(
        apiKey: String,
        deviceId: String
    ) = retrofitInstance.logoutApi(apiKey, true, deviceId)
}