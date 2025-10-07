package com.bitla.ts.domain.pojo.dashboard_model.response


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("booked")
    val booked: Double?,
    @SerializedName("cancelled")
    val cancelled: Double?,
    @SerializedName("collections")
    val collections: Collections,
    @SerializedName("most_searched")
    val mostSearched: MutableList<MostSearched>,
    @SerializedName("pending_api_tickets")
    val pendingApiTickets: PendingApiTickets,
    @SerializedName("pending_e_tickets")
    val pendingETickets: PendingETickets,
    @SerializedName("phone_blocked")
    val phoneBlocked: Double?,
    @SerializedName("quota_blocked")
    val quotaBlocked: Double?,
    @SerializedName("server_date_time")
    val serverDateTime: String? = "",
    @SerializedName("available_balance")
    val availableBalance: Double?,
    @SerializedName("branch_balance")
    val branchBalance: Double?,
    @SerializedName("e_booking")
    val eBooking: Double?,
    @SerializedName("api")
    val apiBooking: Double?,
    @SerializedName("branch")
    val branchBooking: Double?,
    @SerializedName("online_agent")
    val onlineAgent: Double?,
    @SerializedName("offline_agent")
    val offlineAgent: Double?,
    @SerializedName("e_booking_percentage")
    val eBookingPercentage: Double?,
    @SerializedName("api_percentage")
    val apiBookingPercentage: Double?,
    @SerializedName("branch_percentage")
    val branchBookingPercentage: Double?,
    @SerializedName("online_agent_percentage")
    val onlineAgentPercentage: Double?,
    @SerializedName("offline_agent_percentage")
    val offlineAgentPercentage: Double?
)