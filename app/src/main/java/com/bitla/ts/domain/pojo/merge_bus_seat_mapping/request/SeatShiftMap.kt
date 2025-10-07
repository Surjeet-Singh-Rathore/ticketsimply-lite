package com.bitla.ts.domain.pojo.merge_bus_seat_mapping.request


import com.google.gson.annotations.SerializedName

data class SeatShiftMap(
    @SerializedName("pnr_number")
    val pnrNumber: String?,
    @SerializedName("seats")
    val seats: List<Seat?>?
)