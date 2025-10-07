package com.bitla.ts.domain.pojo.collection_summary

data class AmountDetails(
    val booking_items: List<BookingItem>,
    val gst_items: List<GstItem>,
    val net_amount: String,
    val other_items: List<OtherItem>
)