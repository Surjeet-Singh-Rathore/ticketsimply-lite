package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.response


import com.google.gson.annotations.SerializedName

data class ServiceTaxCollection(
    @SerializedName("revenue")
    val revenue: String,
    @SerializedName("service")
    val service: String
)