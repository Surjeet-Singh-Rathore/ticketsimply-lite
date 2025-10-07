package com.bitla.ts.domain.pojo.recommended_seats.response


import com.google.gson.annotations.SerializedName

data class Seat(
    @SerializedName("recommended_seats")
    val recommendedSeats: List<String?>?,
    @SerializedName("seat_number")
    val seatNumber: String?
)