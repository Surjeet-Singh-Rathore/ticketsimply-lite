package com.bitla.ts.domain.pojo.dashboard_fetch.request


import com.google.gson.annotations.SerializedName

data class OrderBy(
    @SerializedName("occupancy")
    var occupancy: String,
    @SerializedName("performance")
    var performance: String,
    @SerializedName("revenue")
    var revenue: String,
    @SerializedName("schedules_summary_active_cancelled")
    var schedulesSummaryActiveCancelled: String,
    @SerializedName("time_blocked_seats_booked_released")
    var timeBlockedSeatsBookedReleased: String,
    @SerializedName("total_pending_quota_seats")
    var totalPendingQuotaSeats: String
)