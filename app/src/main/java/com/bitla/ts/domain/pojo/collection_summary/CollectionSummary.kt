package com.bitla.ts.domain.pojo.collection_summary

data class CollectionSummary(
    val amount_details: AmountDetails,
    val booking: MutableList<Booking>,
    val code: Int,
    val message: String,
    val resut: Result,
    val total_seats: Int?,
    val total_amount: String?

)

class Result(
    val message: String

)