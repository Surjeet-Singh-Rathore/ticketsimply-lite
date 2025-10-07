package com.bitla.ts.domain.pojo.confirm_pay_at_bus

data class TicketLeadDetail(
    val email: String,
    val is_boarded: Int,
    val is_day_visit_lead_privilege: Boolean,
    val is_drop_off_lead_privilege: Boolean,
    val is_hotel_lead_privilege: Boolean,
    val is_pick_up_lead_privilege: Boolean,
    val issued_by: String,
    val issued_by_contact_no: String,
    val lead_details: List<Any>,
    val phone_number: String,
    val pnr_number: String,
    val seat_number: String,
    val ticket_booked_by: String
)