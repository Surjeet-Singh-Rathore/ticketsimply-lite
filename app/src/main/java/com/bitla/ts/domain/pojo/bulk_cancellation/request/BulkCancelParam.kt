package com.bitla.ts.domain.pojo.bulk_cancellation.request

data class BulkCancelParam(
    val pnr_number: String,
    val seat_numbers: String
)