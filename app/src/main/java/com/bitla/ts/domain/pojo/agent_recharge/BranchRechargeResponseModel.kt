package com.bitla.ts.domain.pojo.agent_recharge

import com.google.gson.annotations.SerializedName

data class BranchRechargeResponseModel(
    val code: Int,
    val result: Result?,
    val message: String,
    @SerializedName("transaction_number")
    val transactionNumber: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("otp_validtation_time")
    val otpValidationTime: Int,
)