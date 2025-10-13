package com.bitla.ts.domain.pojo.starred_reports.request

data class StarredReportsRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)