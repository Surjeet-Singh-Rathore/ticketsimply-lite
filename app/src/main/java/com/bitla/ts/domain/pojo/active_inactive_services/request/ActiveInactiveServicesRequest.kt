package com.bitla.ts.domain.pojo.active_inactive_services.request

import com.google.gson.annotations.SerializedName


data class ActiveInactiveServicesRequest (
    @SerializedName("api_key")
    val apiKey: String?,
    @SerializedName("from")
    val from: String?,
    @SerializedName("to")
    val to: String?,
)