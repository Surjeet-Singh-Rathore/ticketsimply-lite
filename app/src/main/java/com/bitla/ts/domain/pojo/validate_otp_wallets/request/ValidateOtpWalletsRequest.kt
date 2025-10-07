package com.bitla.ts.domain.pojo.validate_otp_wallets.request

data class ValidateOtpWalletsRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)