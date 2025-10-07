package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model

import com.google.gson.annotations.SerializedName

data class UserCityStageModel(
    @SerializedName("city_id")
    val cityId: Int,
    @SerializedName("city_name")
    val cityName: String,
    @SerializedName("default_stage_id")
    val defaultStageId: String,
    @SerializedName("default_stage_name")
    val defaultStageName: String

)
