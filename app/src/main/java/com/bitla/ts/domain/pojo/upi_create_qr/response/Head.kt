package com.bitla.ts.domain.pojo.upi_create_qr.response


import com.google.gson.annotations.SerializedName

data class Head(
    @SerializedName("clientId")
    val clientId: String,
    @SerializedName("responseTimestamp")
    val responseTimestamp: String,
    @SerializedName("signature")
    val signature: String,
    @SerializedName("version")
    val version: String
)