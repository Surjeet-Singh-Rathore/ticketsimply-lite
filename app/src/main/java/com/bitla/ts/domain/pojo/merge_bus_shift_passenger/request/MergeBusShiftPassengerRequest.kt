package com.bitla.ts.domain.pojo.merge_bus_shift_passenger.request


import com.google.gson.annotations.SerializedName

data class MergeBusShiftPassengerRequest(

    val apiKey: String,

    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean?,
    @SerializedName("locale")
    val locale: String?,
    @SerializedName("new_res_id")
    val newResId: String?,
    @SerializedName("old_res_id")
    val oldResId: String?,
    @SerializedName("remarks")
    val remarks: String?,
    @SerializedName("shift_to_extra_seats")
    val shiftToExtraSeats: String?,
    @SerializedName("to_send_sms")
    val toSendSms: String?
)