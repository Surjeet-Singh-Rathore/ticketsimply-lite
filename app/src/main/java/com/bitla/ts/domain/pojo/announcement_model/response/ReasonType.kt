package com.bitla.ts.domain.pojo.announcement_model.response

import com.google.gson.annotations.SerializedName

data class ReasonType(
    @SerializedName("arraiving")
    val arraiving: List<String>,
    @SerializedName("current_status")
    val currentStatus: List<String>,
    @SerializedName("delayed")
    val delayed: List<String>,
    @SerializedName("ready_to_depart")
    val readyToDepart: List<String>
)