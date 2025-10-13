package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.response


import com.google.gson.annotations.SerializedName

data class ServiceOccupancy(
    @SerializedName("occupancy")
    val occupancy: String,
    @SerializedName("service")
    val service: String,
    @SerializedName("route_id")
    val routeId: String,
)