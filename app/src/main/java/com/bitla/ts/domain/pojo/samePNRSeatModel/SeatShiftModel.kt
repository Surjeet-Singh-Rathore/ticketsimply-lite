package com.bitla.ts.domain.pojo.samePNRSeatModel

import com.example.buscoach.service_details_response.SeatDetail
data class SeatShiftModel(
    val oldSeat: SeatDetail,
    var newSeat: SeatDetail?,
)