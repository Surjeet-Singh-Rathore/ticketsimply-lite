package com.bitla.ts.domain.pojo.edit_chart.request

data class EditChartRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)