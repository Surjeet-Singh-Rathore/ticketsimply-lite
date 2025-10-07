package com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FareDetail(
    @SerializedName("fare")
    var fare: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("seat_type")
    val seatType: String,
    @SerializedName("edited_fare")
    @Expose
    var editedFare: String? = null
)