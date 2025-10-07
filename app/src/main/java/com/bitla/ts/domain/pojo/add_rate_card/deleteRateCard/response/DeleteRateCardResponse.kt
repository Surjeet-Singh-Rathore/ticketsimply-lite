package com.bitla.ts.domain.pojo.add_rate_card.deleteRateCard.response


import com.google.gson.annotations.SerializedName

data class DeleteRateCardResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result
)