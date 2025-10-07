package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName

data class MultiStationBookingDetail(
    @SerializedName("available_seats")
    var availableSeats: String,
    @SerializedName("available_seats_count")
    var availableSeatsCount: Int,
    @SerializedName("city_pair")
    var cityPair: String
)