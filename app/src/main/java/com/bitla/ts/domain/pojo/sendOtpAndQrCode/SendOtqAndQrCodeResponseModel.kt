package com.bitla.ts.domain.pojo.sendOtpAndQrCode

data class SendOtqAndQrCodeResponseModel(
    val code: Int,
    val message: String,
    val result: Message?
)

class Message(
    val message: String?
)