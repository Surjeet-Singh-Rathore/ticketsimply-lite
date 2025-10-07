package com.bitla.ts.domain.pojo.activate_deactivate_service.response


import com.google.gson.annotations.SerializedName

data class ActivateDeactivateServiceResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?
)