package com.bitla.ts.domain.pojo.my_bookings.response


import com.google.gson.annotations.SerializedName

data class MyBookings(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("result")
    val result: Result
)