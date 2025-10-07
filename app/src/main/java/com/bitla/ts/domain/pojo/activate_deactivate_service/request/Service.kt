package com.bitla.ts.domain.pojo.activate_deactivate_service.request


import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("activate")
    val activate: Boolean?,
    @SerializedName("service_id")
    val serviceId: Long?
)