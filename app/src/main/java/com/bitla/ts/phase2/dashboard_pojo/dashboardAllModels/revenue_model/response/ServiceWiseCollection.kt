package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.response


import com.google.gson.annotations.SerializedName

data class ServiceWiseCollection(
    @SerializedName("revenue")
    val revenue: String,
    @SerializedName("service")
    val service: String,
    @SerializedName("gross_revenue")
    val grossRevenue: String?
)