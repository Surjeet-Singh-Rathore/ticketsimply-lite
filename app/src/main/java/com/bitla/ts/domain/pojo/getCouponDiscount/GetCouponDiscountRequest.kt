package com.bitla.ts.domain.pojo.getCouponDiscount

import com.google.gson.annotations.SerializedName
import retrofit2.http.Query

data class GetCouponDiscountRequest(
    @SerializedName("date")
    val date: String? = "",
    @SerializedName("origin")
    val origin: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("no_of_seats")
    val no_of_seats: String,
    @SerializedName("is_round_trip")
    val is_round_trip: Boolean,
    @SerializedName("api_key")
    val api_key: String? = "",
    @SerializedName("locale")
    val locale: String? = "",
    val is_from_middle_tier: Boolean=true
)