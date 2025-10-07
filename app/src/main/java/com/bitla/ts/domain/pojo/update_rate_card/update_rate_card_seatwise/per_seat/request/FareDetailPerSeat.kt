package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request


import com.google.gson.annotations.SerializedName

data class FareDetailPerSeat(
    @SerializedName("fare")
    var fare: String,
    @SerializedName("seat_number")
    var seatNumber: String
)