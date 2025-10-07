package com.bitla.ts.domain.pojo.update_rate_card.fetch_fare_template.response


import com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.response.Result
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultistationFareDetails
import com.google.gson.annotations.SerializedName

data class FetchFareTemplateResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("multistation_fare_details")
    var multistationFareDetails: MutableList<MultistationFareDetails>
)