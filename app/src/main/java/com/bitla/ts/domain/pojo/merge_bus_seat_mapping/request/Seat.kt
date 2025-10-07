package com.bitla.ts.domain.pojo.merge_bus_seat_mapping.request


import com.google.gson.annotations.SerializedName

data class Seat(
    @SerializedName("new_seat")
    val newSeat: String?,
    @SerializedName("old_seat")
    val oldSeat: String?
)