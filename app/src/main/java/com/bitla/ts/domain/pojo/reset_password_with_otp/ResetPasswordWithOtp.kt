package com.bitla.ts.domain.pojo.reset_password_with_otp

data class ResetPasswordWithOtp(
    val code: Int,
    val result: Result?,
    val otp: String,
    val key: String,
    val mobile_number: String,
    val api_key: String,
    val auth_token: String,
)