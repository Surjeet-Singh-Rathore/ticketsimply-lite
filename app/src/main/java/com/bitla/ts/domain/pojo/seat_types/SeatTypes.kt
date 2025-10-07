package com.bitla.ts.domain.pojo.seat_types

import com.google.gson.annotations.SerializedName

data class SeatTypes(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("seat_type")
    val seatType: String? = null
) {
    var amount: String = ""
}