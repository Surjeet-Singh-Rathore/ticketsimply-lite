package com.bitla.ts.domain.pojo.routewise_booking_memo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RoutewiseReportData(
    @SerializedName("from")
    @Expose
    val from: String = "",

    @SerializedName("to")
    @Expose
    val to: String = "",

    @SerializedName("rate")
    @Expose
    val rate: String = "",

    @SerializedName("seat_number")
    @Expose
    val seatNumber: String = "",

    @SerializedName("no_of_seats")
    @Expose
    val noOfSeats: String = "",

    @SerializedName("amount")
    @Expose
    val amount: String = ""
)
