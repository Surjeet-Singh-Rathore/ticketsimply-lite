package com.bitla.ts.domain.pojo.collection_details.request

data class ReqBody(
    val api_key: String,
    val reservation_id: String,
    val is_from_middle_tier: Boolean = true,
    var locale: String?
)