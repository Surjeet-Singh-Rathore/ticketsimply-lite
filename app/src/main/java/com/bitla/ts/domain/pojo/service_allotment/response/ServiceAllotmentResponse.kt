package com.bitla.ts.domain.pojo.service_allotment.response


import com.google.gson.annotations.SerializedName

data class ServiceAllotmentResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("result")
    val result: Result?
)

data class Result(
    @SerializedName("message")
    val message: String?
)