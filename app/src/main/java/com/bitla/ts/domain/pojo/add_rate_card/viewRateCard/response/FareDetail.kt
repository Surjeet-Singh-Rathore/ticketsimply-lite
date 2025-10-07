package com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FareDetail(
    @SerializedName("fare")
    var fare: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("seat_type")
    val seatType: String,

    @SerializedName("edited_fare")
    @Expose
    var editedFare: String? = null
)