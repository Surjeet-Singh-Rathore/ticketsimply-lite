package com.bitla.ts.domain.pojo.occupancy_datewise.request


import com.google.gson.annotations.SerializedName

data class OccupancyDateWiseRequest (
    @SerializedName("api_key")
    val apiKey: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("route_id")
    val routeId: String?
)