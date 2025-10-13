package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.service_wise_booking_model.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("occupancy_end")
    val occupancyEnd: String,
    @SerializedName("occupancy_start")
    val occupancyStart: String,
    @SerializedName("sort_by")
    val sortBy: String,
    @SerializedName("date")
    val date: String?,
    @SerializedName("from")
    val from: String,
    @SerializedName("to")
    val to: String,
    @SerializedName("service_id")
    val routeId: String,
    @SerializedName("reservation_id")
    val reservationId: String = "",
    @SerializedName("service_id")
    val serviceId: String
)