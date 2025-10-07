package com.bitla.ts.domain.pojo.upi_check_status.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("is_send_sms")
    val isSendSms: Boolean,
    @SerializedName("pnr_number")
    val pnrNumber: String? = null,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("amount")
    val amount: String? = null,
    @SerializedName("is_from_agent_recharge")
    val isFromAgentRecharge: String? = null,
    
    )