package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request

import com.google.gson.annotations.SerializedName

data class ReqBodyNew(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("city_wise_fare")
    val cityWiseFare: MutableList<CityWiseFare>,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("copy_fare")
    val copyFare: CopyFare,
    @SerializedName("channel_type")
    val channelType: ChannelType,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("inc_or_dec")
    val incOrDec: String,
    @SerializedName("override_seat_wise_fare")
    val overrideSeatWiseFare: Boolean,
    @SerializedName("route_id")
    val routeId: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("multiple_dates")
    val multipleDates: String,
    @SerializedName("type")
    val type: String,
//    @SerializedName("fare")
//    val fare: String,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("auth_pin")
    var authPin: String,
)