package com.bitla.ts.domain.pojo.book_ticket

data class Result(
    val boarding_details: BoardingDetails,
    val booked_by: String,
    val bus_type: String,
    val dep_time: String,
    val dest_id: String,
    val destination: String,
    val duration: String,
    val issued_on: String,
    val mobile_terms_and_conditions: String,
    val no_of_seats: Int,
    val origin: String,
    val origin_id: String,
    val passenger_details: List<PassengerDetail>,
    val res_id: Long,
    val seat_numbers: String,
    val service_number: String,
    val ticket_number: String,
    val ticket_status: String,
    val total_fare: Any,
    val travel_date: String,
    val payment_initiatives: String = ""

)