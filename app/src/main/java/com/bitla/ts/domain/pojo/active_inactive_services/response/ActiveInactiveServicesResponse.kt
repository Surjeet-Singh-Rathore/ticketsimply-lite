package com.bitla.ts.domain.pojo.active_inactive_services.response


import com.google.gson.annotations.SerializedName

data class ActiveInactiveServicesResponse(
    @SerializedName("active_service")
    var activeService: MutableList<Service?>?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("inactive_service")
    var inactiveService: MutableList<Service?>?,
    @SerializedName("message")
    val message: String?
)