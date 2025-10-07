package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request


import com.google.gson.annotations.SerializedName

data class UpdateRateCardPerSeatRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)