package com.bitla.ts.domain.pojo.confirm_pay_at_bus

data class CancellationPolicy(
    val cancellation_policy_id: Int,
    val percent: Int,
    val time_limit_from: String,
    val time_limit_to: String
)