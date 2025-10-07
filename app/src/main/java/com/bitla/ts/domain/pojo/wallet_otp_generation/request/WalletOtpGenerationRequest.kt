package com.bitla.ts.domain.pojo.wallet_otp_generation.request

data class WalletOtpGenerationRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)