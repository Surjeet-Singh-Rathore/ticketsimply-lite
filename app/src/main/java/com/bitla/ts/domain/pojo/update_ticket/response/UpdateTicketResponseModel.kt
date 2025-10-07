package com.bitla.ts.domain.pojo.update_ticket.response


import com.bitla.ts.domain.pojo.bulk_ticket_update.response.Result
import com.google.gson.annotations.SerializedName

data class UpdateTicketResponseModel(
    @SerializedName("code")
    val code: Int,
    @SerializedName("header")
    val header: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("result")
    val result: Result?
)