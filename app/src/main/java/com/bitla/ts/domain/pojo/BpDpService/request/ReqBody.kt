package com.bitla.ts.domain.pojo.BpDpService.request

data class ReqBody(
    val api_key: String,
    val id: String,
    val is_from_middle_tier: Boolean,
    val json_format: String,
    val locale: String
)