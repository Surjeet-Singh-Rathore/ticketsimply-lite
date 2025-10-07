package com.bitla.ts.domain.pojo.passenger_history.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val json_format: String,
    val locale: String?,
    val passenger_details: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true
)