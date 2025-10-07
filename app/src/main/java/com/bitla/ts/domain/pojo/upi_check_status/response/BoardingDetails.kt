package com.bitla.ts.domain.pojo.upi_check_status.response


import com.google.gson.annotations.SerializedName

data class BoardingDetails(
    @SerializedName("address")
    val address: String,
    @SerializedName("dep_time")
    val depTime: String
)