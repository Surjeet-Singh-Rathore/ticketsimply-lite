package com.bitla.ts.domain.pojo.merge_bus_shift_passenger.response


import com.google.gson.annotations.SerializedName

data class ShiftedSeat(
    @SerializedName("from")
    val from: String?,
    @SerializedName("to")
    val to: String?
)