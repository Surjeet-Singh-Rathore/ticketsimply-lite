package com.bitla.ts.domain.pojo.reservation_stages.response

import com.google.gson.annotations.SerializedName

data class StageItem (
    @SerializedName("stage_id")
    var stageId: Int? = null,
    @SerializedName("stage_name")
    var stageName: String? = null,
    @SerializedName("city_name")
    val cityName: String? = null,
    @SerializedName("stage_time")
    val stageTime: String? = null
)
