package com.bitla.ts.domain.pojo.multiple_shift_passenger.request

data class ReqBody(
    val api_key: String,
    val `data`: List<Data>,
    val shift_to_extra_seats: Int,
    val new_res_id: String,
    val old_res_id: String,
    val remarks: String,
    val to_send_sms: Int,
    val is_from_middle_tier: Boolean = true,
    var locale: String?,
    var is_bima_service : Boolean,
)