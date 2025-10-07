package com.bitla.ts.domain.pojo.coach_list

import com.google.gson.annotations.SerializedName

data class Coach(
    @SerializedName("coach_id")
    val coachId: Int,
    @SerializedName("coach_name")
    val coachName: String
)
