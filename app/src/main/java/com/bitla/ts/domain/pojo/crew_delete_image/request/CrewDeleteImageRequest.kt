package com.bitla.ts.domain.pojo.crew_delete_image.request

data class CrewDeleteImageRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)