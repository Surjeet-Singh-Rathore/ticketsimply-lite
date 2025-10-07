package com.bitla.ts.domain.pojo.trackingo_response

import com.google.gson.annotations.SerializedName

data class TrackingoResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: TrankingoResponseData
)

