package com.bitla.ts.domain.pojo.passenger_history

data class PassengerHistoryModel(
    val boarding_at: String,
    val boarding_on: String,
    val coach_number: String,
    val destination: String,
    val email: String,
    val issued_by: String,
    val issued_on: String,
    val journey_date: String,
    val name: String,
    val origin: String,
    val passenger_count: String,
    val phone_number: String,
    val seat_numbers: String,
    val service: String,
    val service_number: String,
    val ticket_fare: Double,
    val ticket_number: String,
    val passenger_age: Int,
    val passenger_title: String,
    var isChecked : Boolean = false,
    var trip_counts : String?
)