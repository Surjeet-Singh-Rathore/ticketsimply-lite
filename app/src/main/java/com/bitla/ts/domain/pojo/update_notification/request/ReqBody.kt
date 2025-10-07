package com.bitla.ts.domain.pojo.update_notification.request

data class ReqBody(
    val api_key: String,
    val device_id: String,
    val is_read: String,
    val notify_ids: String
)