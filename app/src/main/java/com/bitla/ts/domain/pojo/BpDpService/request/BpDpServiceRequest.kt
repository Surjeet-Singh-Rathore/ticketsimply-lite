package com.bitla.ts.domain.pojo.BpDpService.request

data class BpDpServiceRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)