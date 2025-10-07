package com.bitla.ts.domain.pojo.smart_miles_otp

data class SmartMilesOtp(
    val code: Int,
    val customer_id: Int,
    val otp: String,
    val otp_key: String,
    val message: String?
)