package com.bitla.ts.domain.pojo.booking_summary

data class Result(
    val amount_details: AmountDetails,
    val booking: MutableList<Booking>,
    val total_seats: Int?,
    val total_amount: String?
)