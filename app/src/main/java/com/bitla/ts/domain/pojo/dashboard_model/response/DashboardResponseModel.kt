package com.bitla.ts.domain.pojo.dashboard_model.response


import com.bitla.ts.domain.pojo.bulk_ticket_update.response.Result
import com.google.gson.annotations.SerializedName

data class DashboardResponseModel(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("result")
    val result: Result?
)