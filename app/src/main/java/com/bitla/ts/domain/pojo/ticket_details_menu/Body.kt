package com.bitla.ts.domain.pojo.ticket_details_menu

data class Body(
    val can_cancel: Boolean,
    val can_cancel_ticket_for_agent: Boolean,
    val can_cancel_ticket_for_user: Boolean,
    val can_shift_ticket: Boolean,
    val is_allow_cancellation_type_as_fixed_or_percentage: Boolean,
    val is_allow_to_alter_cancel_percent: Boolean,
    val is_eticket: Boolean,
    val is_onbehalf_online_ticket: Boolean,
    val is_update_ticket: Boolean,
    val is_zero_percent_cancellation: Boolean,
    val sharing_pdf_link: String,
    val sms_ticket_hash: String,
    val can_release_phone_block: Boolean,
    val can_confirm_phone_block: Boolean,
    val partial_payment_details: PartialPaymentDetails?,
    val is_update_luggage_post_confirmation: Boolean
)