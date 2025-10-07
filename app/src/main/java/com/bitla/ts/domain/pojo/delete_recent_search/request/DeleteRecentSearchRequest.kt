package com.bitla.ts.domain.pojo.delete_recent_search.request

data class DeleteRecentSearchRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)