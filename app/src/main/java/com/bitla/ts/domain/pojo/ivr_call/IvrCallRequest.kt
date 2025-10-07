package com.bitla.ts.domain.pojo.ivr_call


import com.google.gson.annotations.SerializedName

data class IvrCallRequest(
    @SerializedName("api_key")
    val apiKey: String?,
    @SerializedName("boarding_id")
    val boardingId: String?,
    @SerializedName("option")
    val option: String?,
    @SerializedName("res_id")
    val resId: String?
)