package com.bitla.ts.domain.pojo.ticket_details_phase_3.request


import com.google.gson.annotations.SerializedName

data class TicketDetailsRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)