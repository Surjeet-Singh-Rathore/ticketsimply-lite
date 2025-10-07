package com.bitla.ts.domain.pojo.passenger_details_result


import com.google.gson.annotations.SerializedName

data class BookingDetails(
    @SerializedName("agent_type")
    val agentType: String,
    @SerializedName("discount_amount")
    val discountAmount: String,
    @SerializedName("discount_on_total_amount")
    val discountOnTotalAmount: String,
    @SerializedName("is_free_booking_allowed")
    val isFreeBookingAllowed: String,
    @SerializedName("is_vip_ticket")
    val isVipTicket: String,
    @SerializedName("remarks")
    val remarks: String
)