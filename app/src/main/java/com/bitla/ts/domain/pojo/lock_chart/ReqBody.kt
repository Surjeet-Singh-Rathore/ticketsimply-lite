package com.bitla.ts.domain.pojo.lock_chart

data class ReqBody(
    val api_key: String,
    val reservation_id: String,
    var locale: String?
)