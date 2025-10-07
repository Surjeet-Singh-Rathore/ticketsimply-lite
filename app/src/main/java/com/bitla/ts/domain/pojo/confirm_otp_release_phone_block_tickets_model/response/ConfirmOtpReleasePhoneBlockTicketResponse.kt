package com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.response


import com.google.gson.annotations.SerializedName

data class ConfirmOtpReleasePhoneBlockTicketResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("body")
    val result: Result
)