package com.bitla.ts.domain.pojo.phonepe_direct_upi_transaction_status.response


import com.google.gson.annotations.SerializedName

data class PhonePeDirectUPITransactionStatusResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?
)