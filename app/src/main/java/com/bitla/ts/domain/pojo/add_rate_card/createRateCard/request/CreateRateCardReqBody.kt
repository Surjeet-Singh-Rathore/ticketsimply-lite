package com.bitla.ts.domain.pojo.add_rate_card.createRateCard.request

import com.google.gson.annotations.SerializedName

data class CreateRateCardReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("rate_card_name")
    val rateCardName: String,
    @SerializedName("route_id")
    val routeId: String,
    @SerializedName("fare")
    val fare: MutableList<Fare>,
    @SerializedName("time")
    val time: Time?,
    @SerializedName("commission")
    val commission: Commission?,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("auth_pin")
    var authPin: String
)