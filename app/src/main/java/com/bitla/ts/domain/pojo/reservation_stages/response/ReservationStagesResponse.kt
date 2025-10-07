package com.bitla.ts.domain.pojo.reservation_stages.response

import com.google.gson.annotations.SerializedName

data class ReservationStagesResponse (
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("result")
    val result: ReservationStagesData? = null,
    @SerializedName("message")
    val message: String? = null
)