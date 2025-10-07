package com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request


import com.google.gson.annotations.SerializedName

data class Ticket(
    @SerializedName("seat_numbers")
    val seatNumbers: String
)