package com.bitla.ts.domain.pojo.book_ticket.release_ticket.request


import com.google.gson.annotations.SerializedName

data class ReleaseAgentRechargBlockedSeatsRequest(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    val json_format: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)