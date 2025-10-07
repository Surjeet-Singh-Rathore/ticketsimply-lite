package com.bitla.ts.domain.pojo.boarding_stage_seats.response


import com.google.gson.annotations.SerializedName

data class BoardingStageSeatsResponse(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("message")
    val message: String? = null
)