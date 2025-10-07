package com.bitla.ts.domain.pojo.all_coach.response


import com.google.gson.annotations.SerializedName

data class AllCoachResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("all_coach")
    val allCoaches: MutableList<AllCoach>,
    @SerializedName("result")
    val result: Result?
)