package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.booking_trends_model.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("best_performance_days")
    val bestPerformanceDays: List<BestPerformanceDay>?,
    @SerializedName("branch_profit_performance")
    val branchProfitPerformance: List<BranchProfitPerformance>?,
    @SerializedName("e_tickets_performance")
    val eTicketsPerformance: List<ETicketsPerformance>?,
    @SerializedName("performance_summary")
    val performanceSummary: List<PerformanceSummary>?
)