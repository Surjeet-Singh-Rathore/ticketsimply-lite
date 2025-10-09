package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.response


import com.google.gson.annotations.SerializedName

data class OccupancyByBookingSource(
    @SerializedName("booking_source")
    val bookingSource: String,
    @SerializedName("occupancy")
    val occupancy: String
)