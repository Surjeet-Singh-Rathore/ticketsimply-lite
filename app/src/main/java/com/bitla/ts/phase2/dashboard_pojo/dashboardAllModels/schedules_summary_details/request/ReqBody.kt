package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("origin_id")
    val originId: Int,
    @SerializedName("destination")
    val destination: Int,
    @SerializedName("service_id")
    val serviceId: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("sort_by")
    val sortBy: String,
)