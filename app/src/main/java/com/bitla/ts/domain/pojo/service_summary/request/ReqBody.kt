package com.bitla.ts.domain.pojo.service_summary.request

data class ReqBody(
    val api_key: String,
    val reservation_id: String,
    val response_format: String,
    val is_from_middle_tier: Boolean = true,
    var locale: String?

)