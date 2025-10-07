package com.bitla.ts.domain.pojo.service_occupancy_details_popup


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("details")
    val details: List<Detail>?,
    @SerializedName("occupied_seats")
    val occupiedSeats: Int?,
    @SerializedName("total_seats")
    val totalSeats: Int?
)