package com.bitla.ts.domain.pojo.routewise_booking_memo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RouteWiseResponse(
    @SerializedName("pdf_url")
    @Expose
    val pdfUrl: String = "",

    @SerializedName("operator_name")
    @Expose
    val operatorName: String = "",

    @SerializedName("user_branch_address")
    @Expose
    val userBranchAddress: String = "",

    @SerializedName("report_name")
    @Expose
    val reportName: String = "",

    @SerializedName("bus_number")
    @Expose
    val busNumber: String = "",

    @SerializedName("origin")
    @Expose
    val origin: String = "",

    @SerializedName("destination")
    @Expose
    val destination: String = "",

    @SerializedName("departure_time")
    @Expose
    val departureTime: String = "",

    @SerializedName("printed_by")
    @Expose
    val printedBy: String = "",

    @SerializedName("print_date_time")
    @Expose
    val printDateTime: String = "",

    @SerializedName("total_amount")
    @Expose
    val totalAmount: String = "",

    @SerializedName("total_seat_count")
    @Expose
    val totalSeatCount: String = "",

    @SerializedName("report")
    @Expose
    val report: ArrayList<RoutewiseReportData> = arrayListOf(),

    @SerializedName("code")
    @Expose
    val code: Int,

    @SerializedName("message")
    @Expose
    val message: String = "",

    @SerializedName("result")
    @Expose
    val result: Result?
)

