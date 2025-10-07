package com.bitla.ts.domain.pojo.service_summary

import java.io.Serializable

data class Result(
    val agent: MutableList<Agent>,
    val boarding_from: MutableList<BoardingFrom>,
    val drop_off: MutableList<DropOff>,
    val multistation: MutableList<Multistation>,
    val view_summary: ViewSummary,
    val booking_details:ArrayList<SummaryData>? = null,
    val total_amount:Double
) : Serializable