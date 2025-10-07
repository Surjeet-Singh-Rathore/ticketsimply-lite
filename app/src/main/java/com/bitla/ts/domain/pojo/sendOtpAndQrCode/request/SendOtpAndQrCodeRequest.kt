package com.bitla.ts.domain.pojo.sendOtpAndQrCode.request

data class SendOtpAndQrCodeRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)