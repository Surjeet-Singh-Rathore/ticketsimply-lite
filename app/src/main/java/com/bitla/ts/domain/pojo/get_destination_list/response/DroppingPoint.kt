package com.bitla.ts.domain.pojo.get_destination_list.response


import com.google.gson.annotations.SerializedName

data class DroppingPoint(
    @SerializedName("default_stage_id")
    val defaultStageId: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("time")
    val time: String?
)