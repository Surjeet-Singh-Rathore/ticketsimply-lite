package com.bitla.ts.domain.pojo.send_sms_email.request


import com.google.gson.annotations.*

data class ReqBody(
    @SerializedName("ticket_number")
    val ticketNumber: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?,
    @SerializedName("api_key")
    var api_key: String
)