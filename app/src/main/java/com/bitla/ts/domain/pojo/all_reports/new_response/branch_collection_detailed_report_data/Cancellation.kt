package com.bitla.ts.domain.pojo.all_reports.new_response.branch_collection_detailed_report_data


import com.google.gson.annotations.SerializedName

data class Cancellation(
    @SerializedName("booked_by")
    val bookedBy: String,
    @SerializedName("booked_on")
    val bookedOn: String,
    @SerializedName("no_of_seats")
    val noOfSeats: String,
    @SerializedName("refund_amount")
    val refundAmount: Double,
    @SerializedName("ticket_number")
    val ticketNumber: String,
    @SerializedName("travel_date")
    val travelDate: String
)