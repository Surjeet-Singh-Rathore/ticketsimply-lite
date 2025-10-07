package com.bitla.ts.domain.pojo.reset_password_with_otp.request

data class ReqBody(
    val mobile_number: String,
    var locale: String?
)