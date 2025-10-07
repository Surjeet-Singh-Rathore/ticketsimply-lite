package com.bitla.ts.domain.pojo.collection_details


data class CollectionSummary(
    val booking_source: String,
    val passenger_details: ArrayList<PassengerDetail>,
    val total_amount: Double,
    val total_seats: Int
)