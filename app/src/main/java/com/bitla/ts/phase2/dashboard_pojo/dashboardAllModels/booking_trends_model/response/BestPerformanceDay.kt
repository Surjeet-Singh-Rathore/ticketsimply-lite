package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.response


import com.google.gson.annotations.SerializedName

data class BestPerformanceDay(
    @SerializedName("performance")
    val performance: String,
    @SerializedName("service")
    val service: String
)