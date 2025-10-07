package com.bitla.ts.domain.pojo.upi_create_qr.response


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("image")
    val image: String,
    @SerializedName("qrCodeId")
    val qrCodeId: String,
    @SerializedName("qrData")
    val qrData: String,
    @SerializedName("resultInfo")
    val resultInfo: ResultInfo
)