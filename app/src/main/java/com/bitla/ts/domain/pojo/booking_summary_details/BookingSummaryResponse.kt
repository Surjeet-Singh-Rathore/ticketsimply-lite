package com.bitla.ts.domain.pojo.booking_summary_details


import com.google.gson.annotations.SerializedName

data class BookingSummaryResponse(
    @SerializedName("agent_data")
    val agentData: AgentData?,
    @SerializedName("branch_data")
    val branchData: BranchData?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("e_booking_data")
    val eBookingData: EBookingData?,
    @SerializedName("ota_data")
    val otaData: OtaData?,
    @SerializedName("total_revenue")
    val totalRevenue: String?,
    @SerializedName("total_seats")
    val totalSeats: String?
)