package com.bitla.ts.domain.pojo.booking_history.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("description")
    val description: String,
    @SerializedName("pnr_number")
    val pnrNumber: String
)