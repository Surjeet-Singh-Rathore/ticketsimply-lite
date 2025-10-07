package com.bitla.ts.domain.pojo.booking_custom_request

import com.google.gson.annotations.SerializedName

data class BookingCustomRequest(
    val amt_paid_offline: Boolean = false,
    val branch_id: Int = 0,
    val branch_user: Int = 0,
    val offline_agent_on_behalf: Int = 0,
    val online_agent_on_behalf: Int = 0,
    val reference_no: String = "",
    val selected_booking_id: Int = 0,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true
)