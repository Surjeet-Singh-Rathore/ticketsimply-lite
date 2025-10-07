package com.bitla.ts.domain.pojo.fetch_notification.request

data class FetchNotificationRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)