package com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("day")
    val day: String,
    @SerializedName("occupancy")
    val occupancy: String
)