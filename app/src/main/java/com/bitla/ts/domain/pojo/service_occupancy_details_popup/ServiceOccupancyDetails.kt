package com.bitla.ts.domain.pojo.service_occupancy_details_popup


import com.google.gson.annotations.SerializedName

data class ServiceOccupancyDetails(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("message")
    val message: String?
)