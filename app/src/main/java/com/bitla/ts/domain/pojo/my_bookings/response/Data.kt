package com.bitla.ts.domain.pojo.my_bookings.response


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("booked_by_name")
    val bookedByName: String,
    @SerializedName("booked_for")
    val bookedFor: Any,
    @SerializedName("booked_on")
    val bookedOn: String,
    @SerializedName("comm_disc")
    val commDisc: Double,
    @SerializedName("is_cancellable")
    val isCancellable: Boolean,
    @SerializedName("is_updatable")
    val isUpdatable: Boolean,
    @SerializedName("is_updated")
    val isUpdated: Boolean,
    @SerializedName("no_of_seats")
    val noOfSeats: Int,
    @SerializedName("payment_type")
    val paymentType: String,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("route")
    val route: String,
    @SerializedName("route_number")
    val routeNumber: String,
    @SerializedName("seat_number")
    val seatNumber: String,
    @SerializedName("service_name")
    val serviceName: String,
    @SerializedName("service_tax_amount")
    val serviceTaxAmount: Double,
    @SerializedName("sub_agent_booked_by")
    val subAgentBookedBy: String,
    @SerializedName("ticket_status")
    val ticketStatus: String,
    @SerializedName("total_fare")
    val totalFare: Double,
    @SerializedName("travel_date")
    val travelDate: String
)