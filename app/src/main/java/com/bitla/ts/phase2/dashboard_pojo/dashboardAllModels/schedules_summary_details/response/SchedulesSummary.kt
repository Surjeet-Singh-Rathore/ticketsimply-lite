package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.response


import com.google.gson.annotations.SerializedName

data class SchedulesSummary(
    @SerializedName("active_services")
    val activeServices: List<ActiveService>,
    @SerializedName("cancelled_services")
    val cancelledServices: List<CancelledService>
)