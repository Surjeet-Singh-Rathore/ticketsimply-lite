package com.bitla.ts.domain.pojo.ticket_details_phase_3.response


import com.google.gson.annotations.SerializedName

data class DropOffDetails(
    @SerializedName("address")
    val address: String?,
    @SerializedName("arr_time")
    val arrTime: String?,
    @SerializedName("stage_id")
    val stageId: Int?,
    @SerializedName("stage_name")
    val stageName: String?,
    @SerializedName("travel_date")
    val travelDate: String?
)