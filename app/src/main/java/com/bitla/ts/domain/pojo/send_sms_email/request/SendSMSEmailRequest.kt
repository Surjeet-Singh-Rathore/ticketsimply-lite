package com.bitla.ts.domain.pojo.send_sms_email.request


import com.google.gson.annotations.SerializedName

data class SendSMSEmailRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)