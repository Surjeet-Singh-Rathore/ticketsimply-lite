package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.request


import com.google.gson.annotations.SerializedName

data class FareDetail(
    @SerializedName("fare")
    val fare: String,
    @SerializedName("seat_type_id")
    val seatType: String? = null
)