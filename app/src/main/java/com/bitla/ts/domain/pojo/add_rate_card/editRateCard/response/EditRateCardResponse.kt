package com.bitla.ts.domain.pojo.add_rate_card.editRateCard.response


import com.google.gson.annotations.SerializedName

data class EditRateCardResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result
)