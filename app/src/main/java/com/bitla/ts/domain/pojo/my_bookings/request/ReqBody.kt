package com.bitla.ts.domain.pojo.my_bookings.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("response_format")
    val responseFormat: String,
    @SerializedName("date_type")
    val dateType: Int,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)