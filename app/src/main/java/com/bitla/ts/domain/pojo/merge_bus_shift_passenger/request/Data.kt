package com.bitla.ts.domain.pojo.merge_bus_shift_passenger.request


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("new_seat_number")
    val newSeatNumber: String?,
    @SerializedName("old_seat_number")
    val oldSeatNumber: String?,
    @SerializedName("pay_status")
    val payStatus: String?,
    @SerializedName("ticket_number")
    val ticketNumber: String?
)