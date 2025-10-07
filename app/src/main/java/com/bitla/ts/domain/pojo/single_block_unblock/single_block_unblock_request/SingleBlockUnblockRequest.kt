package com.bitla.ts.domain.pojo.single_block_unblock.single_block_unblock_request

data class SingleBlockUnblockRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)