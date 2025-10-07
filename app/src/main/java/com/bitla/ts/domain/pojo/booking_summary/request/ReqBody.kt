package com.bitla.ts.domain.pojo.booking_summary.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val reservation_id: String,
    val response_format: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)