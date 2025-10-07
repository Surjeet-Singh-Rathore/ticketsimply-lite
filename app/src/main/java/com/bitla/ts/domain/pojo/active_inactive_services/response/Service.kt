package com.bitla.ts.domain.pojo.active_inactive_services.response


import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("service_id", alternate = ["id"])
    val serviceId: Long?,
    @SerializedName("service_name", alternate = ["label"])
    val serviceName: String?,
    @SerializedName("route_id")
    val routeId: String?,
    var isChecked: Boolean? = true
)