package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.response


import com.google.gson.annotations.SerializedName

data class DayWiseServiceOccupancy(
    @SerializedName("date")
    val day: String,
    @SerializedName("occupancy")
    val occupancy: String
)