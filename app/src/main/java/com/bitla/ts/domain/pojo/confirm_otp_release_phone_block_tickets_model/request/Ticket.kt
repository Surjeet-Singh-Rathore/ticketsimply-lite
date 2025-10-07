package com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request


import com.google.gson.annotations.SerializedName

data class Ticket(
    @SerializedName("seat_numbers")
    val seatNumbers: String? = ""
)