package com.bitla.ts.domain.pojo.all_reports.new_response.checking_inspector_report_data

import com.google.gson.annotations.SerializedName

data class InspectionResult(
    @SerializedName("origin")
    val origin: String,

    @SerializedName("destination")
    val destination: String,

    @SerializedName("route_name")
    val routeName: String,

    @SerializedName("total_seats")
    val totalSeats: Int,

    @SerializedName("reserved_seats")
    val reservedSeats: Int,

    @SerializedName("male_seats")
    val maleSeats: Int,

    @SerializedName("female_seats")
    val femaleSeats: Int,

    @SerializedName("extra_cabin_seats")
    val extraCabinSeats: Int,

    @SerializedName("message")
    val message: String? = null
)
