package com.bitla.ts.domain.pojo.booking_summary

data class BookingSummary(
    val code: Int,
    val result: Result,
    val message: String? = null
)