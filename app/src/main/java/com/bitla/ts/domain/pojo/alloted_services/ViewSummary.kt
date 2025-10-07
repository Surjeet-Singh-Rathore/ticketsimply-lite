package com.bitla.ts.domain.pojo.alloted_services


import com.google.gson.annotations.SerializedName

data class ViewSummary(
    @SerializedName("api")
    var api: Int,
    @SerializedName("branch_conf")
    var branchConf: Int,
    @SerializedName("e_ticket")
    var eTicket: Int,
    @SerializedName("extra_seat_booked")
    var extraSeatBooked: Int,
    @SerializedName("gents_quota")
    var gentsQuota: Int,
    @SerializedName("in_journey")
    var inJourney: Int,
    @SerializedName("ladies_quota")
    var ladiesQuota: Int,
    @SerializedName("offline_agent")
    var offlineAgent: Int,
    @SerializedName("online")
    var online: Int,
    @SerializedName("online_agent")
    var onlineAgent: Int,
    @SerializedName("quota")
    var quota: Int,
    @SerializedName("time_blocked_seats")
    var timeBlockedSeats: Int,
    @SerializedName("total_booked_seats")
    var totalBookedSeats: Int,
    @SerializedName("total_quota_seats")
    var totalQuotaSeats: Int,
    @SerializedName("total_revenue")
    var totalRevenue: String,
    @SerializedName("user_conf")
    var userConf: Int,
    @SerializedName("occupancy")
    var occupancy: Double,
    @SerializedName("available_seats")
    var availableSeats: Int,

    )