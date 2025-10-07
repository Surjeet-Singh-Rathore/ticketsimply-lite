package com.bitla.ts.domain.pojo.passenger_history.request

data class PassengerHistoryRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)