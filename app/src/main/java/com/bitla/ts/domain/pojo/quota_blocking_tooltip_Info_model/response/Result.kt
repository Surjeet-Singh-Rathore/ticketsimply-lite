package com.bitla.ts.domain.pojo.quota_blocking_tooltip_Info_model.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("blocked_by")
    val blockedBy: String,
    @SerializedName("blocked_on")
    val blockedOn: String,
    @SerializedName("blocking_no")
    val blockingNo: String,
    @SerializedName("quota_for")
    val quotaFor: String,
    @SerializedName("quota_type")
    val quotaType: String,
    @SerializedName("remarks")
    val remarks: String,
    @SerializedName("blocked_seats")
    val blockedSeats: String,
    @SerializedName("gender")
    val gender: String
)