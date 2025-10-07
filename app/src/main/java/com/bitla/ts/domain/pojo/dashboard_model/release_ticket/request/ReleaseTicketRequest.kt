package com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request


import com.google.gson.annotations.SerializedName

data class ReleaseTicketRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)