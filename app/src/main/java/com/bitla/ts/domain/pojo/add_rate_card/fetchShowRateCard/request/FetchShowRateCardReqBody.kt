package com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.request


import com.google.gson.annotations.SerializedName

data class FetchShowRateCardReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("route_id")
    val routeId: String,
    @SerializedName("locale")
    var locale: String,
)