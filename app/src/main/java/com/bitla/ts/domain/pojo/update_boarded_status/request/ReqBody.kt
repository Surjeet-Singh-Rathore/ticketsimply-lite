package com.bitla.ts.domain.pojo.update_boarded_status.request

data class ReqBody(
    val api_key: String,
    val pnr_number: String,
    val seat_number: String,
    val status: String,
    val cargo_details: CargoDetails,
    val is_from_middle_tier: Boolean = true,
    var locale: String?

)