package com.bitla.ts.domain.pojo.activate_deactivate_service.request


import com.google.gson.annotations.SerializedName

data class ActivateDeactivateServiceRequest(
    @SerializedName("service_list")
    val serviceList: MutableList<Service?>?
)