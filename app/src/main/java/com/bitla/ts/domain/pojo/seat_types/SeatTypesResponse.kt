package com.bitla.ts.domain.pojo.seat_types

import com.google.gson.annotations.SerializedName

data class SeatTypesResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("seat_types")
    val seatTypes: MutableList<SeatTypes>,

    @SerializedName("message")
    val message: String? = null
)