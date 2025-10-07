package com.bitla.ts.domain.pojo.service_stages


import com.google.gson.annotations.SerializedName

data class ServiceStageResponseModel(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("result")
    val result: List<StageDetailsItem?>?
)