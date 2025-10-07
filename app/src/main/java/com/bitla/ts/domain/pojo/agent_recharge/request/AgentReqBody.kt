package com.bitla.ts.domain.pojo.agent_recharge.request


import com.google.gson.annotations.SerializedName

data class AgentReqBody(
    @SerializedName("agent_id")
    val agentId: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("payment_type")
    val paymentType: String,
    @SerializedName("remarks")
    val remarks: String,
    @SerializedName("response_format")
    val responseFormat: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("transaction_type")
    val transactionType: String,
    @SerializedName("travel_account_date")
    val travelAccountDate: String,
    @SerializedName("cheque_description")
    val cheque_description: String?,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?,
    @SerializedName("resend_otp")
    val resend_otp: Boolean = false,
    @SerializedName("transaction_number")
    val transactionNumber: String = "",
    @SerializedName("device_id")
    val device_id: String = "",
)