package com.bitla.ts.domain.pojo.ivr_call


import com.google.gson.annotations.SerializedName

data class IvrCallResponse(
    @SerializedName("msg")
    val msg: String?,
    @SerializedName("status")
    val status: Int?
)