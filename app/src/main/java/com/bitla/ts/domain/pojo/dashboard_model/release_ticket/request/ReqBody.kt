package com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("remarks")
    val remarks: String,
    @SerializedName("is_from_dashboard")
    val isFromDashboard: Boolean,
    @SerializedName("ticket")
    val ticket: Ticket,
    val json_format: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?,
    @SerializedName("auth_pin")
    val authPin: String
)