package com.bitla.ts.domain.pojo.book_ticket_full.request

data class UpiDirectPaymentHash(
    val allow_upi_booking: Boolean,
    val upi_payment_type: String? = null
)