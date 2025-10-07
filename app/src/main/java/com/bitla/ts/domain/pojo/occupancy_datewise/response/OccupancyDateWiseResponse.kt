package com.bitla.ts.domain.pojo.occupancy_datewise.response


import com.google.gson.annotations.SerializedName

data class OccupancyDateWiseResponse(
    @SerializedName("active_services")
    val activeServices: Int?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("date_wise_summary")
    val dateWiseSummary: List<DateWiseSummary?>?,
    @SerializedName("service_list")
    val serviceList: List<Service?>?
)