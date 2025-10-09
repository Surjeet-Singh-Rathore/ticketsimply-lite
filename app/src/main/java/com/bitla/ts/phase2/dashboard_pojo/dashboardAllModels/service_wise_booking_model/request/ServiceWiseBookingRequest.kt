package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.service_wise_booking_model.request


import com.google.gson.annotations.SerializedName

data class ServiceWiseBookingRequest(
    @SerializedName("bcc_id")
    val bccId: Int,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)