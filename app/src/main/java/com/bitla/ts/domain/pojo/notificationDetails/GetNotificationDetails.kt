package com.bitla.ts.domain.pojo.notificationDetails

data class GetNotificationDetails(
    val code: Int,
    val description: String,
    val is_mark_as_read: Boolean,
    val notification_msg: String,
    val notification_type: String,
    val title: String,
    val booked_by: String,
    val fares: String,
    val seats: String,
    val doj: String,
    val service_number: String,
    val route: String,
    val message: String,
    val updated_response: UpdatedResponse? = null
)