package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.response


import com.google.gson.annotations.SerializedName

data class OccupancyDetailsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result,
    @SerializedName("message")
    val message: String,
)