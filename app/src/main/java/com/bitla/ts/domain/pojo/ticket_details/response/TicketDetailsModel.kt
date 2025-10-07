package com.bitla.ts.domain.pojo.ticket_details.response


import com.google.gson.annotations.SerializedName

data class TicketDetailsModel(
    @SerializedName("body")
    val body: Body,
    @SerializedName("code")
    val code: Int?=null,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: Result,
    @SerializedName("message")
    val message: String?
)