package com.bitla.ts.domain.pojo.state_details.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val response_format: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)