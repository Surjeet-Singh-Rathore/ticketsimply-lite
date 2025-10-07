package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request


import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.FareDetail
import com.google.gson.annotations.SerializedName

data class CityWiseFare(

    @SerializedName("origin_id")
    val originId: String,
    @SerializedName("fare_details")
    val fareDetails: MutableList<FareDetail>,
    @SerializedName("destination_id")
    val destinationId: String,
)