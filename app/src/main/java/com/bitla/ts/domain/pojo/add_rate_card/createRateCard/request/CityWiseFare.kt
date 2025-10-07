package com.bitla.ts.domain.pojo.add_rate_card.createRateCard.request


import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.FareDetail
import com.google.gson.annotations.SerializedName

data class CityWiseFare(
    @SerializedName("origin_name")
    val originName: String,
    @SerializedName("destination_name")
    val destinationName: String,
    @SerializedName("destination_id")
    val destinationId: String,
    @SerializedName("fare_details")
    val fareDetails: MutableList<FareDetail>,
    @SerializedName("origin_id")
    val originId: String
)