package com.bitla.ts.domain.pojo.add_rate_card.editRateCard.request

import com.google.gson.annotations.SerializedName

data class EditRateCardReqBody (
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("commission")
    val commission: Commission?,
    @SerializedName("fare")
    val fare: MutableList<Fare>,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("rate_card_id")
    val rateCardId: String,
    @SerializedName("rate_card_name")
    val rateCardName: String,
    @SerializedName("route_id")
    val routeId: String,
    @SerializedName("time")
    val time: Time?,
    @SerializedName("to_date")
    val toDate: String

)