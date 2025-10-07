package com.bitla.ts.domain.pojo.unblock_seat.request


import com.google.gson.annotations.SerializedName

data class Ticket(
    @SerializedName("selected_seats")
    var selectedSeats: String?
)