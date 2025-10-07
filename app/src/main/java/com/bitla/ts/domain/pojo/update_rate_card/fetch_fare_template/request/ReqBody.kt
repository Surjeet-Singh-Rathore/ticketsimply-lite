package com.bitla.ts.domain.pojo.update_rate_card.fetch_fare_template.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("template_id")
    val templateId: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    @SerializedName("locale")
    var locale: String?
)