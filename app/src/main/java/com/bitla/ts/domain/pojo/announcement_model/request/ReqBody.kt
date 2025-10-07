package com.bitla.ts.domain.pojo.announcement_model.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("reservation_id")
    val reservationId: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)