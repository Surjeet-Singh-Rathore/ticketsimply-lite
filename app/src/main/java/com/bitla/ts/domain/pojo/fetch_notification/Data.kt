package com.bitla.ts.domain.pojo.fetch_notification

data class Data(
    val title: String,
    val description: String,
    val booked_by: String,
    val doj: String,
    val fares: String,
    val id: String,
    val is_mark_as_read: Boolean,
    val notification_msg: String,
    val notification_type: String,
    val route: String,
    val seats: String,
    val service_number: String
)