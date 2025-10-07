package com.bitla.ts.domain.pojo.merge_bus_seat_mapping.request


import com.google.gson.annotations.SerializedName

data class MergeBusSeatMappingRequest(

    val apiKey: String?,

    @SerializedName("new_res_id")
    val newResId: Int?,
    @SerializedName("old_res_id")
    val oldResId: Int?,
    @SerializedName("seat_shift_map")
    val seatShiftMap: List<SeatShiftMap?>?
)