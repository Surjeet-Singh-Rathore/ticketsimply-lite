package com.bitla.ts.domain.pojo.sendOtpAndQrCode.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val pnr_number: String,
    val seat_number: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)