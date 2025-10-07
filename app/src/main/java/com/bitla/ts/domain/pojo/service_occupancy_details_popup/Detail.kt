package com.bitla.ts.domain.pojo.service_occupancy_details_popup


import com.google.gson.annotations.SerializedName

data class Detail(
    @SerializedName("booking_mode")
    val bookingMode: String?,
    @SerializedName("occupancy")
    val occupancy: Double?,
    @SerializedName("seats")
    val seats: Int?
)