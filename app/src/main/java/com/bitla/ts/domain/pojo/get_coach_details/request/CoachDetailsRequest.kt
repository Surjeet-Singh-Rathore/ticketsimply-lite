package com.bitla.ts.domain.pojo.get_coach_details.request

import com.google.gson.annotations.SerializedName

data class CoachDetailsRequest(

    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean?,
    @SerializedName("api_key")
    val apiKey: String?,
    @SerializedName("operator_api_key")
    val operatorApiKey: String?,
    @SerializedName("locale")
    val locale: String?
)