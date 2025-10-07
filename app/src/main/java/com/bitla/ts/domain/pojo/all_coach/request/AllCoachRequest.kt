package com.bitla.ts.domain.pojo.all_coach.request


import com.google.gson.annotations.SerializedName

data class AllCoachRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody
)