package com.bitla.ts.domain.pojo.ticket_details.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("ticket_number")
    val ticketNumber: String,
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("json_format")
    val jsonFormat: Boolean,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("is_from_qr_scan")
    val isFromQrScan: Boolean = false,
    @SerializedName("locale")
    var locale: String?
)