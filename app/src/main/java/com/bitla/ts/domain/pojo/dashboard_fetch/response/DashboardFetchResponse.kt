package com.bitla.ts.domain.pojo.dashboard_fetch.response


import com.google.gson.annotations.SerializedName

data class DashboardFetchResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("last_updated")
    val lastUpdated: String? = null,
    @SerializedName("order_by")
    val orderBy: List<String>,
    @SerializedName("pinned")
    val pinned: List<String>,
    @SerializedName("result")
    val result: List<Result>
)