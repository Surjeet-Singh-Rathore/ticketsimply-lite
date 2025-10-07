package com.bitla.ts.domain.pojo.all_coach.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("res_id")
    val resId: String,
    var locale: String?
)