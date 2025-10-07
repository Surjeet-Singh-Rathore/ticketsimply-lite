package com.bitla.ts.domain.pojo.reservation_stages.response

import com.google.gson.annotations.SerializedName

data class ReservationStagesData (
    @SerializedName("stage_details")
    val stageDetails: ArrayList<StageItem>
)
