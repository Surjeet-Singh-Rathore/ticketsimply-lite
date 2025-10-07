package com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("key")
    val key: String,
    @SerializedName("otp")
    val otp: String,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("remarks")
    val remarks: String,
    @SerializedName("ticket")
    val ticket: Ticket
)