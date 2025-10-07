package com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response


import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.FareDetail
import com.google.gson.annotations.SerializedName

data class FetchRouteWiseFareDetail(
    @SerializedName("destination_id")
    val destinationId: String,
    @SerializedName("destination_name")
    val destinationName: String,
    @SerializedName("fare_details")
    val fareDetails: MutableList<FareDetail>,
    @SerializedName("origin_id")
    val originId: String,
    @SerializedName("origin_name")
    val originName: String,
    var isExpandable: Boolean = false

)