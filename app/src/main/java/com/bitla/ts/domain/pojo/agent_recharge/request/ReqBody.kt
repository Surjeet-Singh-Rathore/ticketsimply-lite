package com.bitla.ts.domain.pojo.agent_recharge.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val amount: String,
    val api_key: String,
    val branch_id: String,
    val date: String,
    val dd_number: String,
    val description: String,
    val payment_type: String,
    val status: String,
    val transaction_type: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    @SerializedName("resend_otp")
    val resend_otp: Boolean = false,
    @SerializedName("transaction_number")
    val transaction_number: String = "",
    @SerializedName("device_id")
    val device_id: String = "",
    var locale: String?
)