package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.response


import com.google.gson.annotations.SerializedName

data class UpdateRateCardPerSeatResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result?
)