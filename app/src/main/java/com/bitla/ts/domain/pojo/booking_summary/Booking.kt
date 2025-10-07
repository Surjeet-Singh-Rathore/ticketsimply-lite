package com.bitla.ts.domain.pojo.booking_summary

import java.io.Serializable

data class Booking(
    var isChecked: Boolean = false,
    val seats: String,
    val ticket_number: String,
    val total_bookings: Int,
    val passenger_name: String,
    val total_booked_amount: String?= null
) : Serializable {
    var boarding_point: String = ""
}