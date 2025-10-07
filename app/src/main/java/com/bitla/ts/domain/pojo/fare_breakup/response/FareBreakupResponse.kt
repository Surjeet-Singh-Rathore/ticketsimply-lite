package com.bitla.ts.domain.pojo.fare_breakup.response

data class FareBreakupResponse(
    val code: Int,
    val fare_break_up_hash: List<FareBreakUpHash>,
    val payble_amount: Any,
    val service_tax_amount: Any,
    val ticket_fare: Any,
    val total_fare: Any,
    val message: String?=null,
    val status: StatusFareBreakup
)