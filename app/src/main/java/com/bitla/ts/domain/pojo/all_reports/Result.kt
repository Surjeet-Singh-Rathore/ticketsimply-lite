package com.bitla.ts.domain.pojo.all_reports

import com.bitla.ts.domain.pojo.all_reports.new_response.branch_collection_detailed_report_data.Booking
import com.bitla.ts.domain.pojo.all_reports.new_response.branch_collection_detailed_report_data.Cancellation
import com.google.gson.annotations.SerializedName

data class Result(

    @SerializedName("Bookings")
    val bookings: ArrayList<Booking>,
    @SerializedName("Cancellations")
    val cancellations: ArrayList<Cancellation>,
    @SerializedName("total_booking_amount")
    val totalBookingAmount: Double,
    @SerializedName("total_booking_count")
    val totalBookingCount: Int,
    @SerializedName("total_cancel_amount")
    val totalCancelAmount: Double,
    @SerializedName("total_cancellation_count")
    val totalCancellationCount: Int,
    @SerializedName("current_page")
    val current_page: String,
    @SerializedName("total_items_booked")
    val totalItemsBooked: Int,
    @SerializedName("no_of_pages_booked")
    val noOfPagesBooked: Int,
    @SerializedName("total_items_cancelled")
    val totalItemsCancelled: Int,
    @SerializedName("no_of_pages_cancelled")
    val noOfPagesCancelled: Int,
    val message: String? = null
)


