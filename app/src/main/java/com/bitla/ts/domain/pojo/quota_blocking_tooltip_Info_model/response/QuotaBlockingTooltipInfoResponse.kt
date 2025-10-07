package com.bitla.ts.domain.pojo.quota_blocking_tooltip_Info_model.response


import com.google.gson.annotations.SerializedName

data class QuotaBlockingTooltipInfoResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result?
)