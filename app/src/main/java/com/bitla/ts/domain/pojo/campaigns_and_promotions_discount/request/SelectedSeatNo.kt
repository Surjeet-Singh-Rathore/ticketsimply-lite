package com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.request


import com.google.gson.annotations.SerializedName

data class SelectedSeatNo(
    @SerializedName("age")
    val age: String?,
    @SerializedName("passenger_category")
    val passengerCategory: String?,
    @SerializedName("seat_no")
    val seatNo: String?
)