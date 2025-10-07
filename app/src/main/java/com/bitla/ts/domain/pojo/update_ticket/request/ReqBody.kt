package com.bitla.ts.domain.pojo.update_ticket.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("ticket")
    val ticket: Ticket,
    val json_format: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)