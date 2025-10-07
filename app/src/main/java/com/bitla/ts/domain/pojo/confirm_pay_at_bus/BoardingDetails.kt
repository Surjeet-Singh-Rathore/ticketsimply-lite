package com.bitla.ts.domain.pojo.confirm_pay_at_bus

data class BoardingDetails(
    val address: String,
    val contact_numbers: String,
    val contact_persons: String,
    val dep_time: String,
    val is_pick_up: Boolean,
    val landmark: String,
    val latitude: String,
    val longitude: String,
    val pin_code: String,
    val stage_id: Int,
    val stage_name: String,
    val travel_date: String
)