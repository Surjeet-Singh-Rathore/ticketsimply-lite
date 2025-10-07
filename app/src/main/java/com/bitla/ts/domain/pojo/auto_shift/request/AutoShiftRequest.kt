package com.bitla.ts.domain.pojo.auto_shift.request

data class AutoShiftRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)