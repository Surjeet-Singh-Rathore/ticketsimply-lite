package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName

data class BookedSeats(
    @SerializedName("SAAS")
    var sAAS: Int
)