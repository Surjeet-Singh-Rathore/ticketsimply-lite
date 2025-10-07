package com.bitla.ts.domain.pojo.collection_details.request

data class CollectionDetailsRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)