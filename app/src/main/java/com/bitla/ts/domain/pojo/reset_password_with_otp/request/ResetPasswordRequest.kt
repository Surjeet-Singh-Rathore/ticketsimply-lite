package com.bitla.ts.domain.pojo.reset_password_with_otp.request

data class ResetPasswordRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)