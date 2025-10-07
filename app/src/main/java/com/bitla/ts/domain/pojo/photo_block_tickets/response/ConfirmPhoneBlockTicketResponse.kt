package com.bitla.ts.domain.pojo.photo_block_tickets.response


import com.google.gson.annotations.SerializedName

data class ConfirmPhoneBlockTicketResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("result")
    val result: Result?
)