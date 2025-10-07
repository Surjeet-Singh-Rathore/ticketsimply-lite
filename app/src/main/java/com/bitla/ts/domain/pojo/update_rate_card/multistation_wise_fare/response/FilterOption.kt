package com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response


import com.google.gson.annotations.SerializedName

data class FilterOption(
    @SerializedName("city_pair")
    val cityPair: MutableList<CityPair>,
    @SerializedName("destination_city")
    val destinationCity: List<DestinationCity>,
    @SerializedName("origin_city")
    val originCity: MutableList<OriginCity>,
    @SerializedName("from_to_city_time")
    val fromToCityTime: MutableList<FromToCityTime>
)