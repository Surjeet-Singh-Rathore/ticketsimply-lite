package com.bitla.ts.domain.pojo.boarding_stage_seats.request

import com.google.gson.annotations.SerializedName

data class BoardingStageSeatsRequest(
    @SerializedName("reservation_id")
    val reservationId: String,
    @SerializedName("origin_id")
    val originId: String,
    @SerializedName("destination_id")
    val destinationId: String,
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("operator_api_key")
    val operatorApiKey: String,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("app_bima_enabled")
    val appBimaEnabled: Boolean,
    @SerializedName("boarding_id")
    val boardingId: String
)
