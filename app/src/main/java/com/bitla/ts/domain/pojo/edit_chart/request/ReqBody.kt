package com.bitla.ts.domain.pojo.edit_chart.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val adult_fare: String,
    val api_key: String,
    val boarding_at: String,
    val drop_off: String,
    val from: String,
    val name: String,
    val phone_number: String,
    val res_id: Long,
    val seat_number: String,
    val title: String,
    val to: String,
    val travel_date: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)