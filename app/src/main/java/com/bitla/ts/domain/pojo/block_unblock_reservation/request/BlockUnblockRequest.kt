package com.bitla.ts.domain.pojo.block_unblock_reservation.request

class BlockUnblockRequest(
    val bcc_id: String,
    val method_name: String,
    val format: String,
    val req_body: com.bitla.ts.domain.pojo.block_unblock_reservation.request.ReqBody
)