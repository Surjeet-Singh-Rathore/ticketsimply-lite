package com.bitla.ts.domain.pojo.unblock_seat.request


import com.google.gson.annotations.SerializedName

data class UnblockSeatRequest(
    @SerializedName("bcc_id")
    var bccId: String?,
    @SerializedName("format")
    var format: String?,
    @SerializedName("method_name")
    var methodName: String?,
    @SerializedName("req_body")
    var reqBody: ReqBody?
)