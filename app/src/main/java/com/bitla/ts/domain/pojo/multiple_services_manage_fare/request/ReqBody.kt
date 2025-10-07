package com.bitla.ts.domain.pojo.multiple_services_manage_fare.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,

    @SerializedName("locale")
    val locale: String,

    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,

    @SerializedName("channel_type")
    val channelType: MultipleServicesChannelType,

    @SerializedName("from_date")
    val fromDate: String,

    @SerializedName("to_date")
    val toDate: String,

    @SerializedName("inc_or_dec")
    val incOrDec: Int,

    @SerializedName("type")
    val type: Int?,

    @SerializedName("route_ids")
    val routeIds: String,

    @SerializedName("fare_details")
    val fareDetails: MutableList<FareDetails>,

    @SerializedName("origin_id")
    val originId: String?,

    @SerializedName("destination_id")
    val destinationId: String?,
)