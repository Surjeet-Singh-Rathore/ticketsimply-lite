package com.bitla.ts.domain.pojo.add_bp_dp_to_service.request

data class AddBpDpToServiceRequest(
    val apiKey: String?,
    val resId: String?,
    val pnr_number: String?,
    val boardingTime: String?,
    val dept_time: String?,
)
