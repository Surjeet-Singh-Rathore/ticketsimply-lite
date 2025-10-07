package com.bitla.ts.domain.pojo.service_summary

import java.io.Serializable

data class Agent(
    val agent: String,
    val seats: String,
    val total_bookings: Int,
    val total_booked_amount: String?= null

) : Serializable