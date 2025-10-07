package com.bitla.ts.domain.pojo.dashboard_model.release_ticket.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("key")
    val key: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("otp_validation")
    val otpValidation: Boolean
)