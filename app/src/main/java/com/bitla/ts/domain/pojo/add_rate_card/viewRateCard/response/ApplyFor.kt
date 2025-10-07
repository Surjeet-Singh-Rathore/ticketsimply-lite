package com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response


import com.google.gson.annotations.SerializedName

data class ApplyFor(
    @SerializedName("arrival")
    val arrival: Boolean,
    @SerializedName("bp")
    val bp: Boolean,
    @SerializedName("departure")
    val departure: Boolean,
    @SerializedName("dp")
    val dp: Boolean
)