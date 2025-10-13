package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.request


import com.google.gson.annotations.SerializedName

data class OccupancyDetailsRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)