package com.bitla.ts.domain.pojo.update_boarded_status

data class ReqBody(
    val api_key: String,
    val pnr_number: String,
    val seat_number: String,
    val status: String,
    val new_qr_code: String,
    val skip_qr_code: Boolean,
    val new_otp: String,
    val passenger_name: String,
    val reservation_id: String,
    val temp: List<String>,
    val remarks: String? = null,
    val is_from_middle_tier: Boolean= true,
    var locale:String?
    )
