package com.bitla.ts.domain.pojo.bulk_cancellation.request

data class BulkCancellationRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)