package com.bitla.ts.domain.pojo.phonepe_direct_upi_for_app.response


import com.google.gson.annotations.SerializedName

data class PhonePeDirectUPIForAppResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?
)