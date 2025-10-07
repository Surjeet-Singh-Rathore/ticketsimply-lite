package com.bitla.ts.domain.pojo.booking_summary_details


import com.google.gson.annotations.SerializedName

data class Detail(
    @SerializedName("name")
    val name: String?,
    @SerializedName("revenue")
    val revenue: String?,
    @SerializedName("seat_count")
    val seatCount: String?
)