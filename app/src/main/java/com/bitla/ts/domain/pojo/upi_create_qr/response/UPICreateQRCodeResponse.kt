package com.bitla.ts.domain.pojo.upi_create_qr.response


import com.google.gson.annotations.SerializedName

data class UPICreateQRCodeResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data
)