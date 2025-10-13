package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.response


import com.google.gson.annotations.SerializedName

data class PerformanceSource(
    @SerializedName("gross_revenue")
    val grossRevenue: Double,
    @SerializedName("net_revenue")
    val netRevenue: Double,
    @SerializedName("seats_sold")
    val seatsSold: Int,
    @SerializedName("source")
    val source: String
)