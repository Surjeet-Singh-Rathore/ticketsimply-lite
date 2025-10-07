package com.bitla.ts.domain.pojo.upi_check_status.response


import com.google.gson.annotations.SerializedName

data class PassengerDetail(
    @SerializedName("age")
    val age: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("seat_number")
    val seatNumber: String,
    @SerializedName("title")
    val title: String
)