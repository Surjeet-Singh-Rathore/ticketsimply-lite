package com.bitla.ts.domain.pojo.quota_blocking_tooltip_Info_model.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("res_id")
    val resId: String,
    @SerializedName("seat_no")
    val seatNumber: String,
    var locale: String?
)