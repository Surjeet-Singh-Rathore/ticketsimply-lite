package com.bitla.ts.domain.pojo.multiple_services_manage_fare.response

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("message")
    val message: String,
)