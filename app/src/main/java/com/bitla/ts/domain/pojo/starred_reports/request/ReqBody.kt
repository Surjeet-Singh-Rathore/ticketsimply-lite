package com.bitla.ts.domain.pojo.starred_reports.request

data class ReqBody(
    val api_key: String,
    val recent_data: Boolean,
    var locale: String?
)