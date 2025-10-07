package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_time.request


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