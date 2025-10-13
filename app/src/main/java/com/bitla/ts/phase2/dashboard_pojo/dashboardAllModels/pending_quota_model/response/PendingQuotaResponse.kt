package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response


import com.google.gson.annotations.SerializedName

data class PendingQuotaResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: MutableList<Result>
)