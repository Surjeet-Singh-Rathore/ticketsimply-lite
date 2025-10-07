package com.bitla.ts.domain.pojo.bulk_ticket_update.request


import com.google.gson.annotations.*

data class ReqBody(
    @SerializedName("alternate_number")
    val alternateNumber: String,
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("is_notify_passenger")
    val isNotifyPassenger: Boolean,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("primary")
    val primary: String,
    @SerializedName("json_format")
    val json_format: String,
    @SerializedName("update_data")
    val updateData: List<UpdateData>,
    @SerializedName("boarding_at")
    val boardingAt: String?,
    @SerializedName("drop_off")
    val dropOff: String?,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    @SerializedName("locale")
    var locale: String?,
    @SerializedName("remarks")
    val remarks: String? = "",
    @SerializedName("is_bima_service")
    val isBimaTicket: Boolean?
)