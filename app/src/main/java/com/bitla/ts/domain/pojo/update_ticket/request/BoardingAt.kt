package com.bitla.ts.domain.pojo.update_ticket.request


import com.google.gson.annotations.SerializedName

data class BoardingAt(
    @SerializedName("boarding_at")
    val seatNumber: String,
    val boardingAt: String
)