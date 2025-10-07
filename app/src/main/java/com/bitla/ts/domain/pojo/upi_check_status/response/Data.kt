package com.bitla.ts.domain.pojo.upi_check_status.response


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("boarding_details")
    val boardingDetails: BoardingDetails,
    @SerializedName("bus_type")
    val busType: String,
    @SerializedName("dep_time")
    val depTime: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("no_of_seats")
    val noOfSeats: Int,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("passenger_details")
    val passengerDetails: List<PassengerDetail>,
    @SerializedName("seat_numbers")
    val seatNumbers: String,
    @SerializedName("service_number")
    val serviceNumber: String,
    @SerializedName("ticket_number")
    val ticketNumber: String?="",
    @SerializedName("travel_date")
    val travelDate: String
)