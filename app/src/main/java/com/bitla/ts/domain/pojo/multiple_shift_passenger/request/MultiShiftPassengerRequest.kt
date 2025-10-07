package com.bitla.ts.domain.pojo.multiple_shift_passenger.request

data class MultiShiftPassengerRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)