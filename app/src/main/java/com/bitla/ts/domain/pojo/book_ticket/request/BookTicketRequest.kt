package com.bitla.ts.domain.pojo.book_ticket.request

data class BookTicketRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)