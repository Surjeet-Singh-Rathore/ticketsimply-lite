package com.bitla.ts.domain.pojo.pickup_chart_pdf_url.request

data class PickupChartPdfRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)