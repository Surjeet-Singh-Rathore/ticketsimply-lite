package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName

data class BookingDetails(
    @SerializedName("available")
    var available: Int,
    @SerializedName("available_seats")
    var availableSeats: String,
    @SerializedName("blocked")
    var blocked: Int,
    @SerializedName("pending")
    var pending: Int,
    @SerializedName("reserved")
    var reserved: Int,
    @SerializedName("total_seats")
    var totalSeats: Int,
    @SerializedName("booked_passenger_count")
    var bookedPassengerCount: Int,
     @SerializedName("total_booked")
    var total_booked: Int?=0,
     @SerializedName("borded")
    var boarded: Int?=0,
     @SerializedName("yet_to_board")
    var yet_to_board: Int?= 0,

)