package com.bitla.ts.domain.pojo.extend_fare

import com.bitla.ts.domain.pojo.extend_fare.request.RequestBody
import com.google.gson.annotations.SerializedName

class ExtendFareRequestModel {
    @SerializedName("bcc_id")
    var bccId: String? = null

    @SerializedName("method_name")
    var methodName: String? = null

    @SerializedName("format")
    var format: String? = null

    @SerializedName("req_body")
    var req_body: RequestBody? = null
}