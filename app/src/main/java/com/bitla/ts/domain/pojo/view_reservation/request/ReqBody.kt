package com.bitla.ts.domain.pojo.view_reservation.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    var apiKey: String,
    @SerializedName("chart_type")
    var chartType: String,
    @SerializedName("is_from_middle_tier")
    var isFromMiddleTier: Boolean,
    @SerializedName("res_id")
    var resId: String,
    var locale: String?
)