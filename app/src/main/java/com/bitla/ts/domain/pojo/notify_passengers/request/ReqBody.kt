package com.bitla.ts.domain.pojo.notify_passengers.request

data class ReqBody(
    val api_key: String,
    val bus: Bus,
    val charts: Charts,
    val delay: Delay,
    val driver: Driver,
    val employee: Employee,
    val id: String,
    val mobile: Mobile,
    val sms_type: String,
    val custom: Custom,
    val pnr_nos: String,
    val is_from_middle_tier: String,
    var locale: String?
)