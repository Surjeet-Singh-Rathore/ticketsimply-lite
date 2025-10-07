package com.bitla.ts.domain.pojo.move_to_extra_seat.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val is_send_sms: Boolean,
    val remarks: String,
    val reservation_id: String,
    val seat_number: String,
    val extra_seat_no: String,
    val ticket_number: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?,
    var auth_pin: String
)