package com.bitla.ts.domain.pojo.add_rate_card.createRateCard.request


import com.google.gson.annotations.SerializedName

data class Fare(
    @SerializedName("category")
    val category: String,
    @SerializedName("city_wise_fare")
    val cityWiseFare: List<CityWiseFare>,
    @SerializedName("type")
    val type: String
)