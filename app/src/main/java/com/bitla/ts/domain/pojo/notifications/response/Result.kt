package com.bitla.ts.domain.pojo.notifications.response

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("notification_count")
    val notificationCount: String,
    @SerializedName("notification_filter")
    val notificationFilter: MutableList<NotificationFilter>,

    @SerializedName("notification_details")
    val notificationDetails: MutableList<NotificationDetails>
)