package com.bitla.ts.domain.pojo.mealCoupon

import com.google.gson.annotations.SerializedName

data class MealCouponStatusResponse(
    @SerializedName("status")
    var status: Int,
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String
)
