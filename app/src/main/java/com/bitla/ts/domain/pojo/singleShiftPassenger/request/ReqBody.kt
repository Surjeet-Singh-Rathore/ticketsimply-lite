package com.bitla.ts.domain.pojo.singleShiftPassenger.request

data class ReqBody(
    val api_key: String,
    val extra_seat_nos: String,
    val old_seat_numbers: String,
    val remarks: String,
    val reservation_id: String,
    val seat_count: String,
    val seat_number: String,
    val ticket_number: String,
    val to_send_sms: String,
    val is_from_middle_tier: Boolean = true,
    val partial_shift: Boolean = true,
    var locale: String?,
    var is_bima_service : Boolean,
    var auth_pin : String?,
)