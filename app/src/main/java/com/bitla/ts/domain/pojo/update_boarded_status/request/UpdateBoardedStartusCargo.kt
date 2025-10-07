package com.bitla.ts.domain.pojo.update_boarded_status.request

data class UpdateBoardedStartusCargo(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)