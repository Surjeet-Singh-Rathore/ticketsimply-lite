package com.bitla.ts.domain.pojo.dashboard_branchwise_revenue_popup


import com.google.gson.annotations.SerializedName

data class BranchWiseRevenuePopUpResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("result")
    val result: Result?
)