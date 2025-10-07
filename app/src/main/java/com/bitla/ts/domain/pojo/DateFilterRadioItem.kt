package com.bitla.ts.domain.pojo

data class DateFilterRadioItem(
    val id: Int,
    var headerValue: String,
    var subHeaderValue: String,
    var todayDate: String,
    var fromDate: String?,
    var toDate: String?
)