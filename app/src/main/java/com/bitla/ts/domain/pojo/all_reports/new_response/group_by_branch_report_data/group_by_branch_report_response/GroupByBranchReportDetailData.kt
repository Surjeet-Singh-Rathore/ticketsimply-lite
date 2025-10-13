package com.bitla.ts.domain.pojo.all_reports.new_response.group_by_branch_report_data.group_by_branch_report_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GroupByBranchReportDetailData(

    @SerializedName("customer_name")
    @Expose
    val customerName: String = "",

    @SerializedName("customer_phone_number")
    @Expose
    val customerPhoneNumber: String = "",

    @SerializedName("pnr_number")
    @Expose
    val pnrNumber: String = "",

    @SerializedName("seat_numbers")
    @Expose
    val seatNumbers: String = "",

    @SerializedName("fare")
    @Expose
    val fare: Double = 0.0,

    @SerializedName("user_booked")
    @Expose
    val userBooked: String = "",

    @SerializedName("boarding_stage")
    @Expose
    val boardingStage: String = "",

    @SerializedName("booking_status")
    @Expose
    val bookingStatus: String = ""

)
