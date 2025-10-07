package com.bitla.ts.domain.pojo.move_to_normal_seats

data class MoveToNormalSeatRequest (
    val api_key:String,
    val send_sms: Boolean,
    val remarks: String,
    val reservation_id: Long,
    val booked_seat_nos: String,
    val extra_seat_number: String,
    val pnr_number: String,
    var seat_count: String,
    var auth_pin: String
)