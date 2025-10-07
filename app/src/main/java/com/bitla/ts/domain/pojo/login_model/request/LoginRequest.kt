package com.bitla.ts.domain.pojo.login_model.request

data class LoginRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)