package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.response


import com.google.gson.annotations.SerializedName

data class BranchAccountsSummary(
    @SerializedName("branch_id")
    val branchId: String?,
    @SerializedName("branch")
    val branch: String,
    @SerializedName("revenue")
    val revenue: String,
    @SerializedName("gross_revenue")
    val grossRevenue: String?
)