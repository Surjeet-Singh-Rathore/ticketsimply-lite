package com.bitla.ts.domain.pojo.crew_toolkit.request

data class ReqBody(
    val api_key: String,
    val destination_id: String,
    val is_from_middle_tier: Boolean,
    val origin_id: String,
    val res_id: String,
    val travel_date: String
)