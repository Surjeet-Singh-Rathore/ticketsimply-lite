package com.bitla.ts.domain.pojo.service_summary.request

data class ServiceSummaryRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)