package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.phone_blocked_model.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("destination")
    val destination: String,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("passenger_details")
    val passengerDetails: List<PassengerDetail>,
    @SerializedName("seat_count")
    val seatCount: Int,
    @SerializedName("service_no")
    val serviceNo: String,

    @SerializedName("details")
    val details: MutableList<Detail>,
    @SerializedName("status")
    val status: String,
    @SerializedName("total_seat_count")
    val totalSeatCount: Int
)