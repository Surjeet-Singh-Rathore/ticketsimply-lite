package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.service_wise_booking_model.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("service_wise_booking")
    val serviceWiseBooking: List<ServiceWiseBooking>?,
    @SerializedName("total_revenue")
    val totalRevenue: String,
    @SerializedName("total_seats")
    val totalSeats: String,
    @SerializedName("total_services")
    val totalServices: String
)