package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_commission.response


import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.response.Result
import com.google.gson.annotations.SerializedName

data class UpdateRateCardCommissionResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result?
)