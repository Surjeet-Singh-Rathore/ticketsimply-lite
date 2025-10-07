package com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request


import com.google.gson.annotations.SerializedName

data class ConfirmOtpReleasePhoneBlockTicketRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)