package com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("reservation_id")
    val reservation_id: String,
    @SerializedName("channel_id")
    val channelId: String,
    @SerializedName("template_id")
    val templateId: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)