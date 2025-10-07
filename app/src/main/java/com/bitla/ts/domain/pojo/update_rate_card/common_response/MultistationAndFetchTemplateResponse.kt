package com.bitla.ts.domain.pojo.update_rate_card.common_response

import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.FareTemplate
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultistationFareDetails
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.response.Result
import com.google.gson.annotations.SerializedName

data class MultiStationWiseFareResponse(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("result")
    val result: Result,
    @SerializedName("fare_templates")
    val fareTemplate: MutableList<FareTemplate>,
    @SerializedName("multistation_fare_details")
    var multistation_fare_details: MutableList<MultistationFareDetails>
)