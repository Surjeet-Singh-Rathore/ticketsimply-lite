package com.bitla.ts.domain.pojo.upi_check_status.response


import com.google.gson.annotations.SerializedName

data class UpiTranxStatusResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: Data?=null,
    @SerializedName("message")
    val message: String,
    @SerializedName("pnr_number")
    val pnrNumber: String?="",
    @SerializedName("status")
    val status: String,
    @SerializedName("error")
    val error: String?=""
)