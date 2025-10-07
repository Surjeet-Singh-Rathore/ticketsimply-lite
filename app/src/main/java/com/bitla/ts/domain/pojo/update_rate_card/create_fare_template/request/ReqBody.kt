package com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.request

import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.CopyFare
import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("reservation_id")
    val reservationId: String,
    @SerializedName("route_id")
    val routeId: String,
    @SerializedName("template_name")
    val templateName: String,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("multiple_dates")
    val multipleDates: String,
    @SerializedName("city_wise_fare")
    val cityWiseFare: MutableList<CityWiseFare>,
    @SerializedName("weekly_schedule_copy")
    val weeklyScheduleCopy: String,
    @SerializedName("copy_fare")
    val copyFare: CopyFare,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("locale")
    val locale: String
)