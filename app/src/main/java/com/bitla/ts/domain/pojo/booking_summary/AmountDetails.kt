package com.bitla.ts.domain.pojo.booking_summary

data class AmountDetails(
    val booking_items: List<BookingItem>,
    val net_amount: String,
    val other_items: List<OtherItem>,
    val total_cash: String
)