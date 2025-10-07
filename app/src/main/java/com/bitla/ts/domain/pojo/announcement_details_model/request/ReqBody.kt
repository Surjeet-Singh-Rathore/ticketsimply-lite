package com.bitla.ts.domain.pojo.announcement_details_model.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("reservation_id")
    val reservationId: String,
    @SerializedName("languages")
    val languages: List<String>,
    @SerializedName("city_id")
    val cityId: Int,
    @SerializedName("boarding_point")
    val boardingPoint: Int,
    @SerializedName("reason_type")
    val reasonType: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("response_format")
    val responseFormat: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)