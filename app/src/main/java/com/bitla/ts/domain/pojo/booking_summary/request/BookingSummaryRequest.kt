package com.bitla.ts.domain.pojo.booking_summary.request

data class BookingSummaryRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)