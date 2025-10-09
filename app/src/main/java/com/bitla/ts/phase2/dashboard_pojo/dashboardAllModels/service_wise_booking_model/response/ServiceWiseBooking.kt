package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.service_wise_booking_model.response


import com.google.gson.annotations.SerializedName

data class ServiceWiseBooking(
    @SerializedName("occupancy")
    val occupancy: String?,
    @SerializedName("service")
    val service: String,
    @SerializedName("revenue")
    val revenue: String,
    @SerializedName("seats_sold")
    val seatsSold: String?,
    @SerializedName("gross_revenue")
    val grossRevenue: String?
)