package com.bitla.ts.domain.pojo.cancel_partial_ticket_model.request


import com.google.gson.annotations.SerializedName

data class CancelPartialTicketRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody

)