package com.bitla.ts.domain.pojo.confirm_pay_at_bus

data class DropOffDetails(
    val address: String,
    val arr_time: String,
    val contact_numbers: String,
    val contact_persons: String,
    val landmark: String,
    val latitude: String,
    val longitude: String,
    val pin_code: String,
    val stage_id: Int,
    val stage_name: String,
    val travel_date: String
)