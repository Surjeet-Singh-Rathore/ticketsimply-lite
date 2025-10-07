package com.bitla.ts.domain.pojo.view_reservation.request


import com.google.gson.annotations.SerializedName

data class ViewReservationRequest(
    @SerializedName("bcc_id")
    var bccId: String,
    @SerializedName("format")
    var format: String,
    @SerializedName("method_name")
    var methodName: String,
    @SerializedName("req_body")
    var reqBody: ReqBody
)