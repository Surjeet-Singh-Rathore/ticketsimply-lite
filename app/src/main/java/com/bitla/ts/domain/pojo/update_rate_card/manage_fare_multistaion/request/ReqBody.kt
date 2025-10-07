package com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("id")
    val reservation_id: String,
    @SerializedName("route_id")
    val route_id: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("origin_id")
    val origin_id: String,
    @SerializedName("destination_id")
    val destination_id: String,
    @SerializedName("from_date")
    val from_date: String,
    @SerializedName("to_date")
    val to_date: String,
    @SerializedName("fare_details")
    val fare_detailRequests: ArrayList<FareDetailsRequest>,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?,
    @SerializedName("auth_pin")
    var authPin: String
)