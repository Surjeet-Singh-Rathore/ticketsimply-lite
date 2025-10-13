package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.service_wise_booking_model.response


import com.google.gson.annotations.SerializedName

data class ServiceWiseBookingResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result,
    @SerializedName("message")
    val message: String?
)