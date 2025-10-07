package com.bitla.ts.domain.pojo.smart_miles_otp.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val phone_number: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)