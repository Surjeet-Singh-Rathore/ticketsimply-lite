package com.bitla.ts.domain.pojo.available_routes

data class CancellationPolicy(
    val cancellation_percentage: Int,
    val cancellation_type: Int,
    val id: Int,
    val time_limit_from: String,
    val time_limit_to: String
)