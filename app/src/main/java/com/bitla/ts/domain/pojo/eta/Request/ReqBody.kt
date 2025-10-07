package com.bitla.ts.domain.pojo.eta.Request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val route_id: String,
    val travel_date: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)