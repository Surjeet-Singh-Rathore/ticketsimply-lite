package com.bitla.ts.domain.pojo.singleShiftPassenger.request

data class SingleShiftPassengerRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)