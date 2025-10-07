package com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.response


import com.google.gson.annotations.SerializedName

data class FetchShowRateCardResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("routewise_ratecard_details")
    val routeWiseRateCardDetails: MutableList<RouteWiseRateCardDetail>
)