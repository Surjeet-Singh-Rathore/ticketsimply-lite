package com.bitla.ts.domain.pojo.update_notification.request

data class UpdateNotificationRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)