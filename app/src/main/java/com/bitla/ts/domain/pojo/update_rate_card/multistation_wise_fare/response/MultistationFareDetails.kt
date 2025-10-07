package com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response

import com.google.gson.annotations.SerializedName

data class MultistationFareDetails(
    @SerializedName("origin_id")
    val origin_id: String,
    @SerializedName("origin_name")
    val origin_name: String,
    @SerializedName("destination_id")
    val destination_id: String,
    @SerializedName("destination_name")
    val destination_name: String,
    @SerializedName("fare_details")
    val fareDetails: ArrayList<FareDetail>,
    var isExpandable: Boolean = false
)