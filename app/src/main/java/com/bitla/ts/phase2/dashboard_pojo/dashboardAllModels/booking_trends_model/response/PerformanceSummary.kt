package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.response


import com.google.gson.annotations.SerializedName

data class PerformanceSummary(
    @SerializedName("performance_source")
    val performanceSource: List<PerformanceSource>,
    @SerializedName("total_gross_revenue")
    val totalGrossRevenue: Any,
    @SerializedName("total_net_revenue")
    val totalNetRevenue: Any,
    @SerializedName("total_seats")
    val totalSeats: Int,
    @SerializedName("total_sold_seats")
    val totalSoldSeats: Int
)