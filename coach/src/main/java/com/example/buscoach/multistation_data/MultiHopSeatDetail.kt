package com.example.buscoach.multistation_data

import com.example.buscoach.service_details_response.SeatDetail

data class MultiHopSeatDetail(
    val pnr: String?,
    var total_count: Int?,
    var seat_details: MutableList<SeatDetail>?,
    var isPNRGroupSelected: Boolean?
)