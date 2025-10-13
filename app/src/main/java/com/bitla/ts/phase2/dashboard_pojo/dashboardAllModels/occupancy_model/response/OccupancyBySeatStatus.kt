package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.response


import com.google.gson.annotations.SerializedName

data class OccupancyBySeatStatus(
    @SerializedName("available_seats")
    val availableSeats: String,
    @SerializedName("booked_seats")
    val bookedSeats: String,
    @SerializedName("cancelled_seats")
    val cancelledSeats: String,
    @SerializedName("pending_seats")
    val pendingSeats: String,
    @SerializedName("total_seats")
    val totalSeats: String
)