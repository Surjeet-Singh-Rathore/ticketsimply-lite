package com.bitla.ts.domain.pojo.notification_details_phase_3.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String
)