package com.bitla.ts.domain.pojo.book_with_extra_seat.response


import com.google.gson.annotations.SerializedName

data class PassengerDetail(
    @SerializedName("adult_fare")
    val adultFare: String,
    @SerializedName("age")
    val age: Int,
    @SerializedName("cat_id")
    val catId: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("insurance_amount")
    val insuranceAmount: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("net_fare")
    val netFare: String,
    @SerializedName("seat_number")
    val seatNumber: String,
    @SerializedName("title")
    val title: String
)