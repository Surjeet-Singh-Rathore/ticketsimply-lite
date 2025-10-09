package com.bitla.ts.domain.pojo.all_reports.new_response.group_by_branch_report_data.group_by_branch_report_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GroupByBranchReportResponse(

    @SerializedName("pdf_url")
    @Expose
    val pdfUrl: String = "",

    @SerializedName("branch")
    @Expose
    val branch: ArrayList<GroupByBranchReportBranchData>,

    @SerializedName("e_ticket")
    @Expose
    val eTicket: ArrayList<GroupByBranchReportBranchData>,

    @SerializedName("api_booking")
    @Expose
    val apiBooking: ArrayList<GroupByBranchReportBranchData>,

    @SerializedName("total_branch_booking_count")
    @Expose
    val totalBranchBookingCount: Int = 0,

    @SerializedName("total_branch_fare")
    @Expose
    val totalBranchFare: String = "",

    @SerializedName("total_e_ticket_booking_count")
    @Expose
    val totalETicketBookingCount: Int = 0,

    @SerializedName("total_e_ticket_fare")
    @Expose
    val totalETicketFare: String = "",

    @SerializedName("total_api_booking_booking_count")
    @Expose
    val totalApiBookingCount: Int = 0,

    @SerializedName("total_api_booking_fare")
    @Expose
    val totalApiBookingFare: String = "",

    @SerializedName("code")
    @Expose
    val code: Int? = 0,

    @SerializedName("message")
    @Expose
    val message: String? = "",


    @SerializedName("result")
    @Expose
    val result: Result? = null

)

data class Result(val message: String? = "")