package com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response


import com.google.gson.annotations.SerializedName

data class CityPair(
    @SerializedName("city")
    val city: String,
    @SerializedName("id")
    val id: String
)