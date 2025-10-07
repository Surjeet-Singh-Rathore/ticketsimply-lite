package com.bitla.ts.domain.pojo.service_allotment.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String? = "",
    @SerializedName("id")
    val reservationId: String? = "",
    @SerializedName("coach_id")
    val coachId: String? = "",
    @SerializedName("driver1")
    val driver1: String? = "",
    @SerializedName("driver2")
    val driver2: String? = "",
    @SerializedName("driver3")
    val driver3: String? = "",
    @SerializedName("cleaner")
    val cleaner: String? = "",
    @SerializedName("checking_inspector")
    val checkingInspector: String? = "",
    @SerializedName("chart_operated_by")
    val chartOperatedBy: String? = "",
    @SerializedName("contractor")
    val contractor: String? = "",
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    @SerializedName("send_bus_info")
    val sendBusInfo: Boolean = false,
    var locale: String?
)