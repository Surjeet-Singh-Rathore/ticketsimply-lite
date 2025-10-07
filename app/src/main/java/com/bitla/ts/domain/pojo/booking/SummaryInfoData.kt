package com.bitla.ts.domain.pojo.booking

data class SummaryInfoData(
    val name: String,
    var numberOfSeats: String,
    var seatNumber: String,
    var amount: String = "",
    var released_by: String? = ""
)