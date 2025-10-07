package com.bitla.ts.domain.pojo.notification_details_phase_3.request


import com.google.gson.annotations.SerializedName

data class NotificationDetailsPhase3Request(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)