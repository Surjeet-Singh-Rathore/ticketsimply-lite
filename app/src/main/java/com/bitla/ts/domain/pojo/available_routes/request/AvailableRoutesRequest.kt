package com.bitla.ts.domain.pojo.available_routes.request

data class AvailableRoutesRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)