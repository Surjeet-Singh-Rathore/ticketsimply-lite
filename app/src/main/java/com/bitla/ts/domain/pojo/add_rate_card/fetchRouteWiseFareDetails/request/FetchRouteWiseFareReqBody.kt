package com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.request

import com.google.gson.annotations.SerializedName

data class FetchRouteWiseFareReqBody (
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("route_id")
    val routeId: String,
    @SerializedName("locale")
    var locale: String,
)