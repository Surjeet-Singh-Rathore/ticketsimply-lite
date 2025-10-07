package com.bitla.ts.domain.pojo.ticket_details_phase_3.response


import com.google.gson.annotations.SerializedName

data class Detail(
    @SerializedName("info")
    val info: Info?,
    @SerializedName("seat_no")
    val seatNo: String?
)