package com.bitla.ts.domain.pojo.single_block_unblock.single_block_unblock_request

data class ReqBody(
    val api_key: String,
    val res_id: String,
    val response_format: Boolean,
    var locale: String?
) {
    var remarks: String? = null
    var blockingReason: String? = null
}