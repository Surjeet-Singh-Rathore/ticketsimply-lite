package com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.request

import com.google.gson.annotations.SerializedName

data class CreateFareTemplateRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)