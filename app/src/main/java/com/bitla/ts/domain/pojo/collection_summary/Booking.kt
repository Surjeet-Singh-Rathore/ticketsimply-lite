package com.bitla.ts.domain.pojo.collection_summary

import java.io.Serializable

data class Booking(
    val seats: String,
    val ticket_number: String,
    val total_bookings: Int,
    val amount: String
) : Serializable