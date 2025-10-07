package com.bitla.ts.domain.pojo.dashboard_branchwise_revenue_popup


import com.google.gson.annotations.SerializedName

data class Detail(
    @SerializedName("revenue")
    val revenue: String?,
    @SerializedName("seats")
    val seats: String?,
    @SerializedName("user")
    val user: String?
)