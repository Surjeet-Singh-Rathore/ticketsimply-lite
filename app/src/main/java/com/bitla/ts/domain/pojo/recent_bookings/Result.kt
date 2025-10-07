package com.bitla.ts.domain.pojo.recent_bookings

data class Result(
    val api_booking: List<Any>,
    val recent_booking: MutableList<RecentBooking>
)