package com.bitla.ts.domain.pojo.BpDpService.response

data class Result(
    val act_as: String,
    val city: String,
    val city_id: Int,
    val contact_numbers: String,
    val contact_persons: String,
    val id: Int,
    val is_next_day: String,
    val is_pick_up: Boolean,
    val latitude: String,
    val longitude: String,
    val name: String,
    val passenger_details: List<PassengerDetail>,
    val pin_code: String,
    val seq_number: Int,
    val state: Int,
    val time: String,
    val type: Int
)