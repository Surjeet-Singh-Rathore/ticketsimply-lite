package com.bitla.ts.domain.pojo.all_reports.new_response


import com.bitla.ts.domain.pojo.all_reports.new_response.branch_collection_summary_report_data.TicketData
import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("commission")
    val commission: String,
    @SerializedName("pdf_url")
    val pdfUrl: String = "",
    @SerializedName("data")
    val data: ArrayList<Data> = arrayListOf(),
    @SerializedName("net_amount")
    val netAmount: String,
    @SerializedName("total_amount")
    val totalAmount: String = "",
    @SerializedName("total_discount")
    val totalDiscount: String = "",
    @SerializedName("total_seats")
    val totalSeats: String = "",
    @SerializedName("total_seat")
    val totalSeat: String = "",
    @SerializedName("message")
    val message: String = "",
    @SerializedName("header")
    val header: String = "",
    @SerializedName("grand_total_amount")
    val grandTotalAmount: String = "",

    // branch collection details report
    @SerializedName("tickets")
    val tickets: ArrayList<TicketData> = arrayListOf(),

    // occupancy report
    @SerializedName("route")
    val route: String,
    @SerializedName("route_number")
    val routeNumber: String,
    @SerializedName("service_name")
    val serviceName: String,
    @SerializedName("total_bookings")
    val totalBookings: Int,
    @SerializedName("total_prime_bookings")
    val totalPrimeBookings: Int,
//    @SerializedName("total_seats")
//    val totalSeats: Int,
    @SerializedName("total_via_bookings")
    val totalViaBookings: Int,


    //payment status report
    @SerializedName("total_transaction")
    val totalTransaction: Int,
    @SerializedName("total_paid_amount")
    val totalPaidAmount: Double,
    @SerializedName("total_unpaid_amount")
    val totalUnpaidAmount: Double



)