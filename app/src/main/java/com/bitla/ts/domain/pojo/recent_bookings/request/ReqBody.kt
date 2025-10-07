package com.bitla.ts.domain.pojo.recent_bookings.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val limit: Int,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)