package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("route_id")
    val routeId: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("origin_id")
    val originId: Int,
    @SerializedName("destination_id")
    val destinationId: Int,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("fare_details")
    val fareDetails: ArrayList<FareDetailPerSeat>,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)