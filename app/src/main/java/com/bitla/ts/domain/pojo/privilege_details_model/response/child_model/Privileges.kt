package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model

data class Privileges(
    val allow_booking: Boolean,
    val allow_phone_blocking_ticket_onbehalf_online_agent: Boolean,
    val is_allow_branch_booking: Boolean,
    val is_allow_offline_agent_booking: Boolean,
    val is_allow_online_agent_booking: Boolean,
    val is_allow_phone_blocking_in_bima: Boolean,
    val allow_to_book_extra_seat: Boolean
)