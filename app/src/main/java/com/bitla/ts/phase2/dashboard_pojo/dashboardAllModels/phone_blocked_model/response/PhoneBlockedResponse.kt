package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.phone_blocked_model.response


import com.google.gson.annotations.SerializedName

data class PhoneBlockedResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: List<Result>,

    @SerializedName("details")
    val details: List<Detail>,
    @SerializedName("status")
    val status: String,
    @SerializedName("total_seat_count")
    val totalSeatCount: Int
)