package com.bitla.ts.domain.pojo.recommended_seats.request

import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.PassengerDetail

data class RecommendedSeatsRequest(
    val apiKey: String?,
    val resId: String?,
    val pnr: String?,
    val originId: String?,
    val destinationId: String?,
    val excludePassengerDetails: Boolean?,
    val locale: String?
)