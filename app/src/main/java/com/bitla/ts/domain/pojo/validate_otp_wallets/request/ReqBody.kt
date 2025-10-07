package com.bitla.ts.domain.pojo.validate_otp_wallets.request

data class ReqBody(
    val amount: String,
    val api_key: String,
    val is_from_middle_tier: Boolean,
    val otp_number: String,
    val phone_blocked: String,
    val pnr_number: String,
    val wallet_mobile: String,
    val wallet_type: String,
    var locale: String?
)