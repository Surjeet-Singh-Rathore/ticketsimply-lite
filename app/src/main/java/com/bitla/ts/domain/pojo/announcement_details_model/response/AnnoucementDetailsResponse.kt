package com.bitla.ts.domain.pojo.announcement_details_model.response


import com.google.gson.annotations.SerializedName

data class AnnoucementDetailsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: ResultX?
)