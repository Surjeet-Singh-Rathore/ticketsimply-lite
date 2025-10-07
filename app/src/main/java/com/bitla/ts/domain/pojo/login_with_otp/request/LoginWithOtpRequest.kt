package com.bitla.ts.domain.pojo.login_with_otp.request

data class LoginWithOtpRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)