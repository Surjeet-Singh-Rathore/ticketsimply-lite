package com.bitla.ts.domain.pojo.merge_bus_shift_passenger.response


import com.google.gson.annotations.SerializedName

data class MergeBusShiftPassengerResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("not_shifted")
    val notShifted: String?,
    @SerializedName("remaining_seat_counts")
    val remainingSeatCounts: Int?,
    @SerializedName("shifted_seats")
    val shiftedSeats: MutableList<ShiftedSeat?>?
)