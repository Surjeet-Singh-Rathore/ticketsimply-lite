package com.bitla.ts.domain.pojo.crew_toolkit.request

data class CrewToolKitRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)