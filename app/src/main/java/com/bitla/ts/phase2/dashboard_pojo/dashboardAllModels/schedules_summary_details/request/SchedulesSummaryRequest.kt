package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.schedules_summary_details.request


import com.google.gson.annotations.SerializedName

data class SchedulesSummaryRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)