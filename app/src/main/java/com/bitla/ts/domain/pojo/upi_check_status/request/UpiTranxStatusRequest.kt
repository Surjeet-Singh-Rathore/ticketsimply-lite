package com.bitla.ts.domain.pojo.upi_check_status.request


import com.google.gson.annotations.SerializedName

data class UpiTranxStatusRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)