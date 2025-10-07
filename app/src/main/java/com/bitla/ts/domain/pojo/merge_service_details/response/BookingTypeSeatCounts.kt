package com.bitla.ts.domain.pojo.merge_service_details.response


import com.google.gson.annotations.SerializedName

data class BookingTypeSeatCounts(
    @SerializedName("available_seats_count")
    val availableSeatsCount: Int?,
    @SerializedName("booked_seats_count")
    val bookedSeatsCount: Int?,
    @SerializedName("total_seats_count")
    val totalSeatsCount: Int?
)