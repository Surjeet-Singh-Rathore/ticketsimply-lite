package com.bitla.ts.domain.pojo.city_pickup_by_chart_stage.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val city_id: String,
    val res_id: String,
    val stage_id: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)