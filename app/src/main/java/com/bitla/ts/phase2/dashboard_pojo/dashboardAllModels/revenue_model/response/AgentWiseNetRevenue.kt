package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.response

import com.google.gson.annotations.SerializedName

data class AgentWiseNetRevenue(
    @SerializedName("branch")
    val branch: String?,
    @SerializedName("revenue")
    var revenue: String?
)