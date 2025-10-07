package com.bitla.ts.domain.pojo.dashboard_fetch.response


import com.google.gson.annotations.SerializedName

data class BookingDetail(
    @SerializedName("occupancy")
    val occupancy: String,
    @SerializedName("revenue")
    val revenue: String,
    @SerializedName("seats_sold")
    val seatsSold: String,
    @SerializedName("service")
    val service: String,
    val isCancelled: Boolean,


    )