package com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.request

import com.google.gson.annotations.SerializedName

data class ViewRateCardReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("rate_card_id")
    val rateCardId: String,
    @SerializedName("locale")
    var locale: String,
)