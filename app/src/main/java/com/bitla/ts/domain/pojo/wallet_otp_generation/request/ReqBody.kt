package com.bitla.ts.domain.pojo.wallet_otp_generation.request

data class ReqBody(
    val amount: String,
    val api_key: String,
    val is_from_middle_tier: Boolean,
    val pnr_number: String,
    val wallet_mobile: String,
    val wallet_type: String,
    var locale: String?,
    var is_resend_otp: Boolean?
)