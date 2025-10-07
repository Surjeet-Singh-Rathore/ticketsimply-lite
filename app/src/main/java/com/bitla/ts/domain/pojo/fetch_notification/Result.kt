package com.bitla.ts.domain.pojo.fetch_notification

import com.bitla.ts.domain.pojo.fetch_notification.request.NotificationFilter

data class Result(
    val notification_details: MutableList<NotificationDetail>,
    val notification_filter: MutableList<NotificationFilter>,
    val totalItems: Int,
    val numberOfPages: Int,
    val currentPage: Int
)