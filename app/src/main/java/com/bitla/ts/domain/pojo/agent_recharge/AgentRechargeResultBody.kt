package com.bitla.ts.domain.pojo.agent_recharge


import com.google.gson.annotations.SerializedName

data class AgentRechargeResultBody(
    @SerializedName("agent_name")
    val agentName: String,
    @SerializedName("amount_updated")
    val amountUpdated: String,
    @SerializedName("current_balance")
    val currentBalance: String,
    @SerializedName("key")
    val key: String = "",
    @SerializedName("otp")
    val otp: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("previous_balance")
    val previousBalance: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("transaction_number")
    val transactionNumber: String,
    @SerializedName("otp_validtation_time")
    val otpValidationTime: Int,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true
)