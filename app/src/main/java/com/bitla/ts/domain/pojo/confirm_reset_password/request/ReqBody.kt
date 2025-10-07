package com.bitla.ts.domain.pojo.confirm_reset_password.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val confirm_password: String,
    val key: String,
    val new_password: String,
    val otp: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)