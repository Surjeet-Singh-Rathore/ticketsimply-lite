package com.bitla.ts.domain.pojo.crew_update.request

data class UpdateCrewRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)