package com.bitla.ts.domain.pojo.delete_recent_search.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val origin_id: String,
    val destination_id: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)