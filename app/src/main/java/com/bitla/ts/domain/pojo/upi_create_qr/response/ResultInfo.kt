package com.bitla.ts.domain.pojo.upi_create_qr.response


import com.google.gson.annotations.SerializedName

data class ResultInfo(
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String,
    @SerializedName("resultStatus")
    val resultStatus: String
)