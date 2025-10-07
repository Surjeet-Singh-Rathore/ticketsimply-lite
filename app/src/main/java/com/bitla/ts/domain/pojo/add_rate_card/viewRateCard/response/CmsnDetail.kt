package com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response


import com.google.gson.annotations.SerializedName

data class CmsnDetail(
    @SerializedName("cmsn")
    var cmsn: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("seat_type")
    val seatType: String
)