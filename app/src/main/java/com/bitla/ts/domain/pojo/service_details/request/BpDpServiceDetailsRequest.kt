package com.bitla.ts.domain.pojo.service_details.request

import com.google.gson.annotations.SerializedName

data class BpDpServiceDetailsRequest(
    @SerializedName("bcc_id")
    val bcc_id: String,
    @SerializedName("method_name")
    val method_name: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("req_body")
    val req_body: BPDPReqBody
)