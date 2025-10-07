package com.bitla.ts.domain.pojo.sms_types.request

data class SmsTypesRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)