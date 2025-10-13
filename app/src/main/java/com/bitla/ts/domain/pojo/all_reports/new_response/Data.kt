package com.bitla.ts.domain.pojo.all_reports.new_response


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("booked_on")
    val bookedOn: String,
    @SerializedName("no_of_seats")
    val noOfSeats: Int,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("route")
    val route: String,
    @SerializedName("route_number")
    val routeNumber: String,
    @SerializedName("seat_numbers")
    val seatNumbers: String,
    @SerializedName("seat_number")
    val seatNumber: String,
    @SerializedName("service_name")
    val serviceName: String,
    @SerializedName("ticket_status")
    val ticketStatus: String,
    @SerializedName("total_fare")
    val totalFare: Double,
    @SerializedName("travel_date")
    val travelDate: String,
    @SerializedName("discount")
    val discount: String? = null,
    @SerializedName("paid_amount")
    val paidAmount:Double,
@SerializedName("pending_amount")
val pendingAmount:Double
)