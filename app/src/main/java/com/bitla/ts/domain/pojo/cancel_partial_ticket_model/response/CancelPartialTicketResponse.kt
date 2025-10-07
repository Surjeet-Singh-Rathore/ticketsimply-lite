package com.bitla.ts.domain.pojo.cancel_partial_ticket_model.response


import com.google.gson.annotations.SerializedName

data class CancelPartialTicketResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("message")
    val message: String?
)