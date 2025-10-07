package com.bitla.ts.domain.pojo.rapid_booking

data class Result(
    val rapid_seats_data: List<RapidSeatsData>,
    val service_tax: Any,
    val ticket_fare: Any,
    val total_fare: Any,
    val mot_discount: Any?= null,
    val discount: Any,
)