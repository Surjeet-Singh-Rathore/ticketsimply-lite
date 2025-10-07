package com.bitla.ts.domain.pojo.cancellation_policies_service_summary.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("response_format")
    val responseFormat: String,
    var locale: String?
)