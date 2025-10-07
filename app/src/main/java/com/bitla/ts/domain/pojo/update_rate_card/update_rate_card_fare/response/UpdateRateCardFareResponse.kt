package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.response


import com.google.gson.annotations.SerializedName

data class UpdateRateCardFareResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result?
)