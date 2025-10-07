package com.bitla.ts.domain.pojo.book_ticket

data class PassengerDetail(
    val adult_fare: String,
    val age: Int,
    val cat_id: Int,
    val email: String,
    val gender: String,
    val mobile: String,
    val name: String,
    val net_fare: String,
    val seat_number: String,
    val title: String
)