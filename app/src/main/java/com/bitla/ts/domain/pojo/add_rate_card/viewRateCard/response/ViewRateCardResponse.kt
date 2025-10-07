package com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response


import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.response.Result
import com.google.gson.annotations.SerializedName

data class ViewRateCardResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("result")
    val result: Result,
    @SerializedName("filter_option")
    val filterOption: FilterOption,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("rate_card_name")
    val rateCardName: String,
    @SerializedName("routewise_cmsn_details")
    val routeWiseCmsnDetails: RouteWiseCmsnDetails,
    @SerializedName("routewise_fare_details")
    var routeWiseFareDetails: MutableList<RouteWiseFareDetail>,
    @SerializedName("routewise_time_details")
    val routeWiseTimeDetails: RouteWiseTimeDetails,
    @SerializedName("to_date")
    val toDate: String
)