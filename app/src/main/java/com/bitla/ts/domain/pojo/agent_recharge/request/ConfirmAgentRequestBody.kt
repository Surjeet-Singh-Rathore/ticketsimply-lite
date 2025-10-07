package com.bitla.ts.domain.pojo.agent_recharge.request

import com.google.gson.annotations.SerializedName

data class ConfirmAgentRequestBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("otp")
    val otp: String,
    @SerializedName("transaction_number")
    val transaction_number: String,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("is_from_middle_tier")
    val is_from_middle_tier: String,
    @SerializedName("response_format")
    val response_format: Boolean,
    @SerializedName("is_otp_expired")
    val isOtpExpired: Boolean = false,
    @SerializedName("device_id")
    val device_id: String = "",
)
