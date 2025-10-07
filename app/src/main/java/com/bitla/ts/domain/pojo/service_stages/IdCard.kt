package com.bitla.ts.domain.pojo.service_stages


import com.google.gson.annotations.SerializedName

data class IdCard(
    @SerializedName("number")
    val number: String?,
    @SerializedName("type")
    val type: String?
)