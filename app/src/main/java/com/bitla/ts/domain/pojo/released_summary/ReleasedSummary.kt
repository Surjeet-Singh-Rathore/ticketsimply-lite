package com.bitla.ts.domain.pojo.released_summary

data class ReleasedSummary(
    val code: Int,
    val release_details: ReleaseDetails,
    val result: Result,
    val release_tickets: MutableList<ReleaseTicket>
)