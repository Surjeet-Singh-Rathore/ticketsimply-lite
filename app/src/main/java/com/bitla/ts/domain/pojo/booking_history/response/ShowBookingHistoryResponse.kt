package com.bitla.ts.domain.pojo.booking_history.response


import com.google.gson.annotations.SerializedName

data class ShowBookingHistoryResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: MutableList<Result>,
    @SerializedName("message")
    val message: String
)