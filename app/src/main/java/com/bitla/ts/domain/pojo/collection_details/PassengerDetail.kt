package com.bitla.ts.domain.pojo.collection_details

data class PassengerDetail(
    val amount: Double,
    val booked_by: String,
    val seat_numbers: String,
    val from_to: String
)