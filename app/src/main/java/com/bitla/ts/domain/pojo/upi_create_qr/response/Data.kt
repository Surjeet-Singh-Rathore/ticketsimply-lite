package com.bitla.ts.domain.pojo.upi_create_qr.response


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("body")
    val body: Body,
    @SerializedName("head")
    val head: Head
)