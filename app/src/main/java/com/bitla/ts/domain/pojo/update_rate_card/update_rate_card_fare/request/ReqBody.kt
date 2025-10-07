package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("route_id")
    val routeId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("inc_or_dec")
    val incOrDec: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("fare")
    val fare: String,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?,
    @SerializedName("auth_key")
    var authKey: String
)