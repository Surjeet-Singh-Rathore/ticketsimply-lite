package com.bitla.ts.domain.pojo.notify_passengers.request

data class Delay(
    val time: String,
    val traffic_hours: String,
    val traffic_minutes: String
)