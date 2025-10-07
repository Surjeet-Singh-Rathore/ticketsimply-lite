package com.bitla.ts.domain.pojo.state_details.request


class StateDetailRequest(
    val bcc_id: String,
    val method_name: String,
    val format: String,
    val req_body: ReqBody
)
