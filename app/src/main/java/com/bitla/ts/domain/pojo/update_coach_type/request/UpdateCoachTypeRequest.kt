package com.bitla.ts.domain.pojo.update_coach_type.request

data class UpdateCoachTypeRequest(
    val api_key: String,
    val is_from_middle_tier: Boolean,
    val coach_type: String
)