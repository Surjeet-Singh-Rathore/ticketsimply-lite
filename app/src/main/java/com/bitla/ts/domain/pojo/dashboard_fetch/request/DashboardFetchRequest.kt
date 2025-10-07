package com.bitla.ts.domain.pojo.dashboard_fetch.request


import com.google.gson.annotations.SerializedName

data class DashboardFetchRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)