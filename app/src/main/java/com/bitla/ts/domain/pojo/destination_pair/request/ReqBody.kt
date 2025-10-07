package com.bitla.ts.domain.pojo.destination_pair.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val operator_api_key: String,
    val response_format: String,
    val app_bima_enabled: Boolean?,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)