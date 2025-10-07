package com.bitla.ts.domain.pojo.add_rate_card.createRateCard.response


import com.google.gson.annotations.SerializedName

data class CreateRateCardResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result
)