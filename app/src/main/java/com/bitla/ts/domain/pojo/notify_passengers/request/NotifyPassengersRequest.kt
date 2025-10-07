package com.bitla.ts.domain.pojo.notify_passengers.request

data class NotifyPassengersRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)