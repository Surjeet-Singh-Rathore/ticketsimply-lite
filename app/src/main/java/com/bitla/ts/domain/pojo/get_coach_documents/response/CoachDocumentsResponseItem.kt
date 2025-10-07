package com.bitla.ts.domain.pojo.get_coach_documents.response


import com.google.gson.annotations.SerializedName

data class CoachDocumentsResponseItem(
    @SerializedName("alert_title")
    val alertTitle: String?,
    @SerializedName("expiry_date")
    val expiryDate: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("issue_date")
    val issueDate: String?
)