package com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response


import com.google.gson.annotations.SerializedName

data class FilterOption(
    @SerializedName("city_pair")
    val cityPair: MutableList<CityPair>,
    @SerializedName("destination_city")
    val destinationCity: MutableList<DestinationCity>,
    @SerializedName("origin_city")
    val originCity: MutableList<OriginCity>
)