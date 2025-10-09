package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.response


import com.google.gson.annotations.SerializedName

data class DayWiseCollection(
    @SerializedName("revenue")
    val revenue: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("gross_revenue")
    val grossRevenue: String?
)