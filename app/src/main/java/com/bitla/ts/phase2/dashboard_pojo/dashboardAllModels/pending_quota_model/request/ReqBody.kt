package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("service_id")
    val serviceId: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("sort_by")
    val sortBy: String,
    @SerializedName("reservation_id")
    val reservationId: String = ""
)