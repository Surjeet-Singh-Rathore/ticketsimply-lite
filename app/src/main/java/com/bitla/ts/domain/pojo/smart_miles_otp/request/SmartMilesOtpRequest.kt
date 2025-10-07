package com.bitla.ts.domain.pojo.smart_miles_otp.request

data class SmartMilesOtpRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)