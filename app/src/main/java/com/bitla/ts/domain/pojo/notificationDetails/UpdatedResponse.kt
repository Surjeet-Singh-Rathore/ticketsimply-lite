package com.bitla.ts.domain.pojo.notificationDetails

data class UpdatedResponse(
    val is_msg_having_feedback: Boolean,
    val message: String,
    val notification_date: String,
    val notification_tag: String,
    val result: List<Result>
)