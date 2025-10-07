package com.bitla.ts.domain.pojo.fetch_notification

data class NotificationDetail(
    val `data`: List<Data>,
    val header_title: String,
    val unread_notification_count: String
)