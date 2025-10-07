package com.bitla.ts.domain.pojo.fetch_notification.request

import com.bitla.ts.domain.pojo.fetch_notification.Result

data class FetchNotificationModel(
    val code: Int,
    val result: Result
)