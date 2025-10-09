package com.bitla.ts.domain.pojo.all_reports.new_response.branch_collection_summary_report_data

import com.bitla.ts.domain.pojo.all_reports.new_response.branch_collection_detailed_report_data.Booking
import com.bitla.ts.domain.pojo.all_reports.new_response.branch_collection_detailed_report_data.Cancellation
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TicketData {
    @SerializedName("branch_name")
    @Expose
    var branchName: String? = null

    @SerializedName("booking_count")
    @Expose
    var bookingCount: Int? = null

    @SerializedName("cancel_count")
    @Expose
    var cancelCount: Int? = null

    @SerializedName("booking_amount")
    @Expose
    var bookingAmount: Float? = null

    @SerializedName("cancel_amount")
    @Expose
    var cancelAmount: Float? = null

    @SerializedName("total_amount")
    @Expose
    var totalAmount: Float? = null


    @SerializedName("Bookings")
    val bookings: ArrayList<Booking> = arrayListOf()
    @SerializedName("Cancellations")
    val cancellations: ArrayList<Cancellation> = arrayListOf()
    @SerializedName("total_booking_amount")
    val totalBookingAmount: Double? = null
    @SerializedName("total_booking_count")
    val totalBookingCount: Int? = null
    @SerializedName("total_cancel_amount")
    val totalCancelAmount: Double? = null
    @SerializedName("total_cancellation_count")
    val totalCancellationCount: Int? = null
}