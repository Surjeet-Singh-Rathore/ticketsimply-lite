package com.bitla.ts.domain.pojo.announcement_model.response

import com.google.gson.annotations.SerializedName

data class BoardingPoint(
    @SerializedName("stage_id")
    val stageId: Int,
    @SerializedName("stage_name")
    val stageName: String
)