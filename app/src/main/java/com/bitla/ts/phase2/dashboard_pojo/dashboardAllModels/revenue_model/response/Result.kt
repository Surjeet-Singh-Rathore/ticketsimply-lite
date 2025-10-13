package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("agent_wise_net_revenue")
    val agentWiseNetRevenue: List<AgentWiseNetRevenue?>?,
    @SerializedName("branch_accounts_summary")
    val branchAccountsSummary: List<BranchAccountsSummary>?,
    @SerializedName("day_wise_collection")
    val dayWiseCollection: List<DayWiseCollection>?,
    @SerializedName("service_tax_collection")
    val serviceTaxCollection: List<ServiceTaxCollection>?,
    @SerializedName("service_wise_collection")
    val serviceWiseCollection: List<ServiceWiseCollection>?,
    @SerializedName("unsold_seats_loss")
    val unsoldSeatsLoss: List<UnsoldSeatsLos>?
)