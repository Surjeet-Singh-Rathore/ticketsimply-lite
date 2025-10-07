package com.bitla.ts.domain.pojo.lock_chart

data class LockChartRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)