package com.bitla.ts.domain.pojo.announcement_details_model.response


import com.google.gson.annotations.SerializedName

data class AnnouncementMessage(
    @SerializedName("language")
    val language: String,
    @SerializedName("message")
    val message: String?
)