package com.bitla.ts.domain.pojo.frequent_traveller_model.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("pnr_number")
    val pnrNo: String,
    @SerializedName("mobile_number")
    val mobileNo: String,
    @SerializedName("passenger_name")
    val passengerName: String,
    @SerializedName("seat_number")
    val seatNo: String,
    @SerializedName("service_name")
    val serviceName: String,
    @SerializedName("trip_count")
    val tripCounts: String,
    @SerializedName("date_of_journey")
    val doj: String
)