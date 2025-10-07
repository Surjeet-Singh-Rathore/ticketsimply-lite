package com.bitla.ts.domain.pojo.merge_service_details.response


import com.google.gson.annotations.SerializedName

data class MergeServiceDetailsResponse(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("message")
    val message: String?
)