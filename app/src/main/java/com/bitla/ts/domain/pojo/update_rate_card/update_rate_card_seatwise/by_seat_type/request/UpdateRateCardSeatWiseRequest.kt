package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.request


import com.google.gson.annotations.SerializedName

data class UpdateRateCardSeatWiseRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.request.ReqBody
)