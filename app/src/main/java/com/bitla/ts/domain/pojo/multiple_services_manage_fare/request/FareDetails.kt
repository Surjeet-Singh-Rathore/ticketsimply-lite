package com.bitla.ts.domain.pojo.multiple_services_manage_fare.request

import com.google.gson.annotations.SerializedName

data class FareDetails(
    @SerializedName("id")
    val id: Int,

    @SerializedName("seat_type")
    val seatType: String,

    @SerializedName("amount_or_perc")
    val amountOrPerc: String
)