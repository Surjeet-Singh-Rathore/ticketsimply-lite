package com.bitla.ts.domain.pojo.released_summary

import java.io.Serializable

data class ReleaseTicket(
    val seats: String,
    val status: String,
    val ticket_number: String,
    val total_bookings: Int,
    var released_by: String?
) : Serializable