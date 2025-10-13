package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("day_wise_service_occupancy")
    val dayWiseServiceOccupancy: List<DayWiseServiceOccupancy>?,
    @SerializedName("occupancy_by_booking_source")
    val occupancyByBookingSource: List<OccupancyByBookingSource>?,
    @SerializedName("occupancy_by_seat_status")
    val occupancyBySeatStatus: List<OccupancyBySeatStatus>?,
    @SerializedName("service_occupancy")
    val serviceOccupancy: List<ServiceOccupancy>?
)