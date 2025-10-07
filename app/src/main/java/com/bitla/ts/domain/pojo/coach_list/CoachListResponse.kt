package com.bitla.ts.domain.pojo.coach_list

import com.google.gson.annotations.SerializedName

data class CoachListResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("list")
    val coaches: List<Coach>
)
