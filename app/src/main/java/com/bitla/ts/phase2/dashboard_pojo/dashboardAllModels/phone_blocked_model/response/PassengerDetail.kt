package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.phone_blocked_model.response


import com.google.gson.annotations.SerializedName

data class PassengerDetail(
    @SerializedName("blocked_by")
    val blockedBy: String,
    @SerializedName("collection")
    val collection: Double,
    @SerializedName("date_time")
    val dateTime: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("seat_no")
    val seatNo: String,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("released_by")
    val releasedBy: String,
    @SerializedName("is_pay_at_bus")
    val isPayAtBus: Boolean? = true
)