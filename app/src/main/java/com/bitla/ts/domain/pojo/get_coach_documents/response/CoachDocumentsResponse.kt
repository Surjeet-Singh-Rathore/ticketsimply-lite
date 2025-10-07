package com.bitla.ts.domain.pojo.get_coach_documents.response


import com.google.gson.annotations.SerializedName

data class CoachDocumentsResponse(
    @SerializedName("result")
    val result: ArrayList<CoachDocumentsResponseItem> = arrayListOf(),
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?,
)