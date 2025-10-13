package com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response


import com.google.gson.annotations.SerializedName

data class OccupancyCalendarResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: List<Result>
)