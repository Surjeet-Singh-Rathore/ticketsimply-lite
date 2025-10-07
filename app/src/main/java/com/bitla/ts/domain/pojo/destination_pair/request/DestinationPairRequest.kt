package com.bitla.ts.domain.pojo.destination_pair.request

data class DestinationPairRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)