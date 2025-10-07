package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_commission.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean
)