package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.response


import com.google.gson.annotations.SerializedName

data class UpdateRateCardSeatWiseResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result
)