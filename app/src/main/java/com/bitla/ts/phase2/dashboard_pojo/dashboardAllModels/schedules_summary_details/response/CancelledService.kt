package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.response


import com.google.gson.annotations.SerializedName

data class CancelledService(
    @SerializedName("cancelled_by")
    val cancelledBy: String,
    @SerializedName("service")
    val service: String,
    @SerializedName("cancelled_on")
    val time: String
)