package com.bitla.ts.domain.pojo.booking_history.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("response_format")
    val responseFormat: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)