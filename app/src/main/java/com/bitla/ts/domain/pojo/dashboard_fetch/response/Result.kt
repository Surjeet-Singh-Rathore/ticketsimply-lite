package com.bitla.ts.domain.pojo.dashboard_fetch.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("agent")
    val agent: String?,
    @SerializedName("api")
    val api: String?,
    @SerializedName("e-ticket")
    val eTicket: String?,
    @SerializedName("booking_details")
    val bookingDetails: List<BookingDetail>?,
    @SerializedName("branch")
    val branch: String?,
    @SerializedName("is_pinned")
    var isPinned: Int,
    @SerializedName("label")
    val label: String,
    @SerializedName("last_updated")
    val lastUpdated: String?,
    @SerializedName("order")
    val order: Int?,
    @SerializedName("today")
    val today: String?,
    @SerializedName("tomorrow")
    val tomorrow: String?,
    @SerializedName("total_occupancy")
    val totalOccupancy: String?,
    @SerializedName("total_performance")
    val totalPerformance: String?,
    @SerializedName("total_revenue")
    val totalRevenue: String?,
    @SerializedName("total_seats")
    val totalSeats: String?,
    @SerializedName("total_services")
    val totalServices: String?,
    @SerializedName("yesterday")
    val yesterday: String?,
    @SerializedName("total_occupancy_by_seats")
    val totalOccupancyBySeats: String?,
    @SerializedName("yesterday_by_seats")
    val yesterdayBySeats: String?,
    @SerializedName("today_by_seats")
    val todayBySeats: String?,
    @SerializedName("tomorrow_by_seats")
    val tomorrowBySeats: String?
)