package com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String,
)