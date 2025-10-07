package com.bitla.ts.domain.pojo.get_destination_list.response


import com.google.gson.annotations.SerializedName

data class BoardingDetails(
    @SerializedName("boarding_stage_id")
    val boardingStageId: String?,
    @SerializedName("boarding_stage_name")
    val boardingStageName: String?,
    @SerializedName("origin")
    val origin: String?,
    @SerializedName("time")
    val time: String?
)