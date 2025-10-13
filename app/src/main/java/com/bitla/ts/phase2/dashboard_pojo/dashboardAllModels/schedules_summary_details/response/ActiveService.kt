package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.response


import com.google.gson.annotations.SerializedName

data class ActiveService(
    @SerializedName("service")
    val service: String
)