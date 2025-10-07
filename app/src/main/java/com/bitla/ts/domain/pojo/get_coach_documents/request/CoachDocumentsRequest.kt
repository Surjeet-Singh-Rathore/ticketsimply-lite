package com.bitla.ts.domain.pojo.get_coach_documents.request

import com.google.gson.annotations.SerializedName

data class CoachDocumentsRequest(

    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean?,
    @SerializedName("coach_number")
    val coachNumber: String?,
    @SerializedName("api_key")
    val apiKey: String?,
    @SerializedName("operator_api_key")
    val operatorApiKey: String?,
    @SerializedName("locale")
    val locale: String?,
    @SerializedName("coach_id")
    val coachId : String?
)