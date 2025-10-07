package com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response

import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.response.Result
import com.google.gson.annotations.SerializedName

data class MultiStationWiseFareResponse(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("result")
    val result: Result,
    @SerializedName("fare_templates")
    val fareTemplate: MutableList<FareTemplate>,
    @SerializedName("filter_option")
    val filterOption: FilterOption,
    @SerializedName("multistation_fare_details")
    var multistation_fare_details: MutableList<MultistationFareDetails>,
    @SerializedName("configured_amount_type")
    var configuredAmountType: String? = null,
)