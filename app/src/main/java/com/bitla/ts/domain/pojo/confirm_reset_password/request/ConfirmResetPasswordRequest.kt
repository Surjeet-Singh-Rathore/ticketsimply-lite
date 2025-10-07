package com.bitla.ts.domain.pojo.confirm_reset_password.request

data class ConfirmResetPasswordRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)