package com.bitla.ts.domain.pojo.ticket_details.response


import com.google.gson.annotations.SerializedName

data class TicketLeadDetail(
    @SerializedName("email")
    val email: String,
    @SerializedName("is_boarded")
    val isBoarded: Int,
    @SerializedName("is_day_visit_lead_privilege")
    val isDayVisitLeadPrivilege: Boolean,
    @SerializedName("is_drop_off_lead_privilege")
    val isDropOffLeadPrivilege: Boolean,
    @SerializedName("is_hotel_lead_privilege")
    val isHotelLeadPrivilege: Boolean,
    @SerializedName("is_pick_up_lead_privilege")
    val isPickUpLeadPrivilege: Boolean,
    @SerializedName("lead_details")
    val leadDetails: List<Any>,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("seat_number")
    val seatNumber: String,
    @SerializedName("ticket_booked_by")
    val ticketBookedBy: String?
)