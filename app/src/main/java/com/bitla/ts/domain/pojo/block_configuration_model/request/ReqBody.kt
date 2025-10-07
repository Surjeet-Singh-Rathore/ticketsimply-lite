package com.bitla.ts.domain.pojo.block_configuration_model.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)