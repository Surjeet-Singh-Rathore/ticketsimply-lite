package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.response


import com.google.gson.annotations.SerializedName

data class ETicketsPerformance(
    @SerializedName("e_ticket")
    val eTicket: String,
    @SerializedName("performance")
    val performance: Double
)