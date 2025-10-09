package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.revenue_model.response


import com.google.gson.annotations.SerializedName

data class UnsoldSeatsLos(
    @SerializedName("revenue")
    val revenue: String? = "",
    @SerializedName("unsold_seats")
    val unsoldSeats: String? = "",
    @SerializedName("empty_seats_count")
    val emptySeatsCount: Int? = 0
)