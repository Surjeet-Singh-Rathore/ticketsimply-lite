package com.bitla.ts.domain.pojo.samePNRSeatModel

data class SamePNRSeatModel(
    val pnr: String,
    val seatShiftList: MutableList<SeatShiftModel>,
    val destinationName: String,
    val bookedBy: String
)