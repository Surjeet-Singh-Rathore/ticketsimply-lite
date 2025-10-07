package com.bitla.ts.domain.pojo.merge_bus_seat_mapping.response


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("new_res_id")
    val newResId: String?,
    @SerializedName("no_difference")
    val noDifference: MutableList<SeatMappingDetail?>?,
    @SerializedName("old_res_id")
    val oldResId: String?,
    @SerializedName("to_pay")
    val toPay: MutableList<SeatMappingDetail?>?,
    @SerializedName("to_receive")
    val toReceive: MutableList<SeatMappingDetail?>?
)