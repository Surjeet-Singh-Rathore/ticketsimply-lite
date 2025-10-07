package com.bitla.ts.domain.pojo.login_with_otp.request

data class ReqBody(
    val id: String,
    val key: String,
    val new_otp: String,
    var locale: String?,
    var device_id: String
)