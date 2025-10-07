package com.bitla.ts.domain.pojo.confirm_reset_password


data class ConfirmResetPasswordModel(
    val code: Int,
    val error: String,
    val result: Result,
    val message: String
)