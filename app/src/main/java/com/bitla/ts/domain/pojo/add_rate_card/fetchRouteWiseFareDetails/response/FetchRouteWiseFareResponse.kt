package com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response


import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.response.Result
import com.google.gson.annotations.SerializedName

data class FetchRouteWiseFareResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("result")
    val result: Result,
    @SerializedName("fetch_routewise_fare_details")
    var fetchRouteWiseFareDetails: MutableList<FetchRouteWiseFareDetail>,
    @SerializedName("filter_option")
    val filterOption: FilterOption,
    @SerializedName("configured_amount_type")
    var configuredAmountType: String? = null,
)