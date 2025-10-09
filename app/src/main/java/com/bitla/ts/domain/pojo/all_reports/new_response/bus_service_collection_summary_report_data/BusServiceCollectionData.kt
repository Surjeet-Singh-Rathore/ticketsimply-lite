package com.bitla.ts.domain.pojo.all_reports.new_response.bus_service_collection_summary_report_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BusServiceCollectionData(

    //Summary Report
    @SerializedName("from_to_trip")
    @Expose
    val fromToTrip: String,

    @SerializedName("from_to_seats")
    @Expose
    val fromToSeats: String,

    //Detailed Report
    @SerializedName("pnr_number")
    @Expose
    val pnrNumber: String = "",

    @SerializedName("origin")
    @Expose
    val origin: String = "",

    @SerializedName("destination")
    @Expose
    val destination: String = "",

    @SerializedName("coach_number")
    @Expose
    val coachNumber: String = "",

    @SerializedName("seat_numbers")
    @Expose
    val seatNumbers: String = "",

    @SerializedName("branch_name")
    @Expose
    val branchName: String = "",

    @SerializedName("issued_by")
    @Expose
    val issuedBy: String = "",

    @SerializedName("fare")
    @Expose
    val fare: String = "",

    @SerializedName("net_amount")
    @Expose
    val netAmount: String = "",

    @SerializedName("message")
    val message: String? = null

)
