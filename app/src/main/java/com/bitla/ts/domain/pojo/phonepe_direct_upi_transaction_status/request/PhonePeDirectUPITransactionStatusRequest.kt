package com.bitla.ts.domain.pojo.phonepe_direct_upi_transaction_status.request


import com.google.gson.annotations.SerializedName

data class PhonePeDirectUPITransactionStatusRequest(
    @SerializedName("api_key")
    val apiKey: String?,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean?,
    @SerializedName("is_send_sms")
    val isSendSms: Boolean?,
    @SerializedName("pnr_number")
    val pnrNumber: String?
)