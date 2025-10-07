package com.bitla.ts.domain.pojo.booking_summary_details


import com.google.gson.annotations.SerializedName

data class OtaData(
    @SerializedName("details")
    val details: List<Detail>?,
    @SerializedName("seats")
    val seatCount: String?,
    @SerializedName("revenue")
    val revenue: String?
)