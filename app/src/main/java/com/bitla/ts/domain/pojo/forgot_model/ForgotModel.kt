package com.bitla.ts.domain.pojo.forgot_model


data class ForgotModel(
    val otp: String,
    val key: String,
    val mobile_number: String,
    val code: Int

)

data class Result(
    val message: String
)