package com.bitla.ts.domain.pojo.book_with_extra_seat.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ExtraSeats (
    @SerializedName("ex_no_of_seats")
    @Expose
    var exNoOfSeats: Int?= null,

    @SerializedName("seat_details")
    @Expose
    var seatDetails: MutableList<SeatDetailExtra>?= null
)