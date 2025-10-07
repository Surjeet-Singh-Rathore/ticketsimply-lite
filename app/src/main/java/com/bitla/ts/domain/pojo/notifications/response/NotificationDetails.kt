package com.bitla.ts.domain.pojo.notifications.response

import com.google.gson.annotations.SerializedName

data class NotificationDetails(
    @SerializedName("notification_title")
    val notificationTitle: String,
    @SerializedName("notification_message")
    val notificationMessage: String,
    @SerializedName("notification_type")
    val notificationType: String,
    @SerializedName("is_mark_read_unread")
    val isMarkReadUnread: Boolean
)