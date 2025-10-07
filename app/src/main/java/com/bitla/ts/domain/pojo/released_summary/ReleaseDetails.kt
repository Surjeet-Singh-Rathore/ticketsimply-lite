package com.bitla.ts.domain.pojo.released_summary

data class ReleaseDetails(
    val items: List<Item>,
    val total_cash: Double,
    val total_pending: Int,
    val total_released: Int
)