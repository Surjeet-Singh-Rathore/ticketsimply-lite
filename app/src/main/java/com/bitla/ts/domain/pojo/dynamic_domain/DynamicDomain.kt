package com.bitla.ts.domain.pojo.dynamic_domain

import com.google.gson.annotations.SerializedName

data class DynamicDomain(
    @SerializedName("result")
    val result: Result?,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = ""
)