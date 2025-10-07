package com.bitla.ts.domain.pojo.bulk_ticket_update.request


import com.google.gson.annotations.SerializedName

data class BulkTicketUpdateRequestModel(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)