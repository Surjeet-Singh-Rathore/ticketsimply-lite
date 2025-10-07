package com.bitla.ts.domain.pojo.confirm_otp_cancel_partial_ticket_model.response

import com.google.gson.annotations.SerializedName

data class ConfirmOtpCancelPartialTicketResponse(
    @SerializedName("body")
    val body: Body,
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?
//    val result: com.bitla.ts.domain.pojo.confirm_otp_cancel_partial_ticket_model.response.Result
)