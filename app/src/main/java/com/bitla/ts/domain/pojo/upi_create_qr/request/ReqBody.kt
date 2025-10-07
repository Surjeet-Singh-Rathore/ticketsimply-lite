package com.bitla.ts.domain.pojo.upi_create_qr.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("upi_type")
    val upiType: Int,
    @SerializedName("user_number")
    val userNumber: String
)