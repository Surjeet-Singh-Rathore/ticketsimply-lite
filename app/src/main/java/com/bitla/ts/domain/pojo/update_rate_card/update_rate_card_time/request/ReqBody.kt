package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_time.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("inc_or_dec")
    val incOrDec: String,
    @SerializedName("route_id")
    val routeId: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("apply_for")
    val applyFor: ApplyFor,
    @SerializedName("city_id")
    val cityId: String = "",
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?,
    @SerializedName("auth_key")
    var authKey: String
)