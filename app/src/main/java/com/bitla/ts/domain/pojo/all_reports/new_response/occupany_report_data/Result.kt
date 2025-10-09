package com.bitla.ts.domain.pojo.all_reports.new_response.occupany_report_data


import com.google.gson.annotations.SerializedName

data class Result(
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
    @SerializedName("total_seats")
    val totalSeats: Int,
    @SerializedName("total_via_bookings")
    val totalViaBookings: Int,
    @SerializedName("message")
    val message: String = ""
)