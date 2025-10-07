package com.bitla.ts.domain.pojo.reservation_stages.request

import com.google.gson.annotations.SerializedName

data class ReservationStagesRequest(
    @SerializedName("reservation_id")
    val reservationId: String,
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("operator_api_key")
    val operatorApiKey: String,
    @SerializedName("locale")
    val locale: String,
)
