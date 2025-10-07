package com.bitla.ts.domain.pojo.book_ticket_full

data class BoardingDetails(
    val address: String,
    val contact_numbers: String,
    val contact_persons: String,
    val dep_time: String,
    val landmark: String,
    val stage_name: String? = "",
)