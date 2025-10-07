package com.bitla.ts.domain.pojo.send_sms_email


import com.google.gson.annotations.SerializedName

data class SendSmsEmailResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?
)