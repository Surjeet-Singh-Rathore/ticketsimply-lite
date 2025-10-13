package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response


import com.google.gson.annotations.SerializedName

data class PassengerDetail(
    @SerializedName("blocked_by")
    val blockedBy: String,
    @SerializedName("collection")
    val collection: String,
    @SerializedName("date_time")
    val dateTime: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("quota_type")
    val quotaType: String,
    @SerializedName("seat_no")
    val seatNo: String
)