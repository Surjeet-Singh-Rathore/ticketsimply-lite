package com.bitla.ts.domain.pojo.phonepe_direct_upi_for_app.response


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("instrumentResponse")
    val instrumentResponse: InstrumentResponse?,
    @SerializedName("merchantId")
    val merchantId: String?,
    @SerializedName("merchantTransactionId")
    val merchantTransactionId: String?,
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("amount")
    val amount: String?,
    @SerializedName("payLink")
    val payLink: String?,
    @SerializedName("mobileNumber")
    val mobileNumber: String?
)