package com.bitla.ts.domain.pojo.recent_bookings

data class RecentBooking(
    val created_on: String,
    val destination_id: Int,
    val destination_name: String,
    val is_cancellable: Boolean,
    val is_updatable: Boolean,
    val no_of_seats: Int,
    val origin_id: Int,
    val origin_name: String,
    val pnr_number: String,
    val seat_number: String,
    val time: String,
    val total_fare: Any,
    val travel_date: String
)