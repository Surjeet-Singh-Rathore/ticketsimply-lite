package com.bitla.ts.domain.pojo.block_configuration_model.request

data class BlockConfigRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)