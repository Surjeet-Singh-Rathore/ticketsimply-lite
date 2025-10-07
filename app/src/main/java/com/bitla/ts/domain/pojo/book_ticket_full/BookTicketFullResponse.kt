package com.bitla.ts.domain.pojo.book_ticket_full

data class BookTicketFullResponse(
    val code: Int,
    val result: Result,
    val message: Any?,
    val ticket_number: String
)