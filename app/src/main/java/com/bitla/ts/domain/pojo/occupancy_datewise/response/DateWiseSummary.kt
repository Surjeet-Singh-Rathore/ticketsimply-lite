package com.bitla.ts.domain.pojo.occupancy_datewise.response


import com.google.gson.annotations.SerializedName

data class DateWiseSummary(
    @SerializedName("active_services")
    val activeServices: Int?,
    @SerializedName("available_seats")
    val availableSeats: Int?,
    @SerializedName("booked_seats")
    val bookedSeats: Int?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("inactive_services")
    val inactiveServices: Int?,
    @SerializedName("total_fare")
    val totalFare: Double?,
    @SerializedName("total_seats")
    val totalSeats: Int?
)