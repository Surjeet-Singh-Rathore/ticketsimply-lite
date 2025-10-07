package com.bitla.ts.domain.pojo.fare_breakup.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FareBreakupRequest {
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
    var reqBody: ReqBody? = null
}