package com.bitla.ts.domain.pojo.dashboard_branchwise_revenue_popup


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("details")
    val details: List<Detail?>?
)