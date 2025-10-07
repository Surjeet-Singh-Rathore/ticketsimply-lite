package com.bitla.ts.domain.pojo.book_ticket_full.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BookTicketFullRequest {
    @SerializedName("bcc_id")
    @Expose
    var bccId: String? = null

    @SerializedName("method_name")
    @Expose
    var methodName: String? = null

    @SerializedName("format")
    @Expose
    var format: String? = null

    @SerializedName("req_body")
    @Expose
    var reqBody: Any? = null
}