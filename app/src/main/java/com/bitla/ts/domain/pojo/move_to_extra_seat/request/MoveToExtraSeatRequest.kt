package com.bitla.ts.domain.pojo.move_to_extra_seat.request

data class MoveToExtraSeatRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)